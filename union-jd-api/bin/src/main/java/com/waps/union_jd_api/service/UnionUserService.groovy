package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.es.domain.JDMediaInfoESMap
import com.waps.service.jd.es.domain.UnionUserESMap
import com.waps.service.jd.es.service.UnionUserESService
import com.waps.tools.security.MD5
import com.waps.union_jd_api.bean.WeChatUserInfoBean
import com.waps.union_jd_api.utils.Config
import com.waps.union_jd_api.utils.DateUtils
import com.waps.utils.StringUtils
import org.elasticsearch.action.DocWriteResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnionUserService {

    @Autowired
    private UnionUserESService unionUserESService
    @Autowired
    private JDMediaService jdMediaService;

    public void syncWxUserInfo(String openID, String wxUserJson, String channel) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(wxUserJson)
            println "===userObject==="
            println jsonObject
            UnionUserESMap userESMap = null
            if (!StringUtils.isNull(openID)) {
                String id = new MD5().getMD5(openID)
                userESMap = loadUserByID(id)
            }
            if (userESMap == null) {
                userESMap = new UnionUserESMap()
            }
            if (jsonObject != null) {
                WeChatUserInfoBean weChatUserInfoBean = jsonObject.toJavaObject(WeChatUserInfoBean.class) as WeChatUserInfoBean
                userESMap.setWx_id(openID)
                userESMap.setOpen_id(openID)
                userESMap.setUser_id(openID)
                userESMap.setUser_name(weChatUserInfoBean.getNickname())
                userESMap.setUser_icon(weChatUserInfoBean.getHeadimgurl())
                userESMap.setUser_sex(weChatUserInfoBean.getSex() + "")
                userESMap.setProvince(weChatUserInfoBean.getProvince())
                userESMap.setCountry(weChatUserInfoBean.getCountry())
                userESMap.setCity(weChatUserInfoBean.getCity())
                userESMap.setUser_type("wx")
                userESMap.setFrom_type("wx")
            } else {
                println "wx jsonObject is null"
            }
            if (!StringUtils.isNull(channel)) {
                userESMap.setChannel_name(channel)
            }
            saveUser(userESMap)
        } catch (Exception e) {
            println "syncWxUserInfo ERROR:" + e.getLocalizedMessage()
        }
    }


    public String getUCode(String openID) {
        String md5_id = new MD5().getMD5(openID)
        String ucode = md5_id.substring(md5_id.length() - 6, md5_id.length())
        return ucode.toLowerCase()
    }

    /**
     * 保存用户信息
     * @param userESMap
     * @return
     */
    public DocWriteResponse saveUser(UnionUserESMap userESMap) {
        try {
            if (!StringUtils.isNull(userESMap.getWx_id())) {
                String md5_id = new MD5().getMD5(userESMap.getWx_id())
                userESMap.setId(md5_id)
            } else if (userESMap.getOpen_id()) {
                String md5_id = new MD5().getMD5(userESMap.getOpen_id())
                userESMap.setId(md5_id)
                userESMap.setU_code(getUCode(userESMap.getOpen_id()))
            } else if (userESMap.getUser_id()) {
                String md5_id = new MD5().getMD5(userESMap.getUser_id())
                userESMap.setId(md5_id)
            }

            //直接以群号做邀请码的情况
            if (!StringUtils.isNull(userESMap.getInvite_code())) {
                userESMap.setF_name(userESMap.getInvite_code())
            }

            //有邀请码的情况，找到上级信息
            if (!StringUtils.isNull(userESMap.getF_code())) {
                if ("ROOT" == userESMap.getF_code()) {
                    userESMap.setRole_id(Config.USER_ROLE_LEVEL_2)
                }
                UnionUserESMap f_userMap = loadUserByCODE(userESMap.getF_code())
                if (f_userMap != null) {
                    //保存上级用户openID
                    if (!StringUtils.isNull(f_userMap.getOpen_id())) {
                        userESMap.setF_open_id(f_userMap.getOpen_id())
                    }
                    //保存上级关系
                    if (!StringUtils.isNull(f_userMap.getChannel_name()) && !StringUtils.isNull(f_userMap.getChannel_id())) {
                        userESMap.setF_name(f_userMap.getChannel_name())
                        userESMap.setF_id(f_userMap.getChannel_id())
                    }
                    //继承上级的导师
                    if (!StringUtils.isNull(f_userMap.getT_open_id())) {
                        userESMap.setT_open_id(f_userMap.getT_open_id())
                    }
                    //如果上级是导师，设置导师ID
                    if (Config.USER_ROLE_LEVEL_2 == f_userMap.getRole_id()) {
                        userESMap.setT_open_id(f_userMap.getOpen_id())
                    }
                }
            }
            //补完群主信息,channel_name不为空就去补全
            if (!StringUtils.isNull(userESMap.getChannel_name())) {
                userESMap.setChannel_name(userESMap.getChannel_name().replaceAll(" ",""))
                if (StringUtils.isNull(userESMap.getChannel_id())) {
                    JDMediaInfoESMap jdMediaInfoESMap = jdMediaService.getMediaInfoByChannelName(userESMap.getChannel_name())
                    if (jdMediaInfoESMap != null) {
                        userESMap.setChannel_id(jdMediaInfoESMap.getChannel_id())
                    }
                }
                //有channel_name是超级用户, 同步上级关系到自己信息中
                UnionUserTreeBean unionUserTreeBean = getUnionUserTree(userESMap.getChannel_name())
                if (unionUserTreeBean != null) {
                    userESMap.setF_name(unionUserTreeBean.getFather_id())
                    userESMap.setF_id("")
                    userESMap.setG_name(unionUserTreeBean.getRoot_id())
                    userESMap.setG_id("")
                }
            }

            //补完上级信息
            if (!StringUtils.isNull(userESMap.getF_name()) && StringUtils.isNull(userESMap.getF_id())) {
                JDMediaInfoESMap jdMediaInfoESMap = jdMediaService.getMediaInfoByChannelName(userESMap.getF_name())
                if (jdMediaInfoESMap != null)
                    userESMap.setF_id(jdMediaInfoESMap.getChannel_id())
            }

            //补完上上级信息
            if (!StringUtils.isNull(userESMap.getG_name()) && StringUtils.isNull(userESMap.getG_id())) {
                JDMediaInfoESMap jdMediaInfoESMap = jdMediaService.getMediaInfoByChannelName(userESMap.getG_name())
                if (jdMediaInfoESMap != null)
                    userESMap.setG_id(jdMediaInfoESMap.getChannel_id())
            }

            //设置会员级别
            if (!StringUtils.isNull(userESMap.getChannel_id()) && !StringUtils.isNull(userESMap.getChannel_name())) {

            } else {
                userESMap.setRole_id(Config.USER_ROLE_LEVEL_0)  //普通会员
            }


            if (!StringUtils.isNull(userESMap.getId())) {
                GetResponse response = unionUserESService.load(userESMap.getId())
                if (response != null && response.isExists()) {
                    userESMap.setModifytime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ''))
                    JSONObject newJsonObj = (JSONObject) JSONObject.toJSON(userESMap)
                    Map<String, Object> newMap = (Map<String, Object>) newJsonObj
                    UpdateResponse updateResponse = unionUserESService.update(userESMap.getId(), newMap)
                    return updateResponse
                } else {
                    userESMap.setModifytime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ''))
                    userESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ''))
                    IndexResponse indexResponse = unionUserESService.save(userESMap.getId(), userESMap)
                    return indexResponse
                }
            }
        } catch (Exception e) {
            println "UnionUserService saveUser ERROR:" + e.getLocalizedMessage()
            e.printStackTrace()
        }
    }

    /**
     * 从接口读取用户层级关系
     * @param uid
     */
    public UnionUserTreeBean getUnionUserTree(String uid) {
        if (!StringUtils.isNull(uid)) {
            String r_num = System.currentTimeMillis()
            String md5 = new MD5().getMD5(uid + r_num + "Waps_jd_MD5key").toLowerCase()
            String url = Config.UNION_USER_TREE_URL.replace("[RNUM]", r_num).replace("[MD5]", md5).replace("[UID]", uid)
            println url
            String json = StringUtils.getUrlTxt(url)
            JSONObject retObj = JSONObject.parseObject(json)
            if (retObj.getInteger("code") == 200) {
                JSONObject jsonObject = retObj.get("data") as JSONObject
                UnionUserTreeBean unionUserTreeBean = jsonObject.toJavaObject(UnionUserTreeBean.class)
                if ("0".equalsIgnoreCase(unionUserTreeBean.getFather_id())) unionUserTreeBean.setFather_id("")
                if ("0".equalsIgnoreCase(unionUserTreeBean.getRoot_id())) unionUserTreeBean.setRoot_id("")
                return unionUserTreeBean
            }
        }

    }

    /**
     * 通过wx_id读取用户信息
     * @param wx_id
     * @return
     */
    public UnionUserESMap loadUserByWXID(String wx_id) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("wx_id", wx_id)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过user_id读取用户信息
     * @param user_id
     * @return
     */
    public UnionUserESMap loadUserByUserID(String user_id) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("user_id", user_id)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过ID读取用户信息，id是MD5
     * @param id
     * @return
     */
    public UnionUserESMap loadUserByID(String id) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("id", id)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过openID读取用户信息
     * @param openID
     * @return
     */
    public UnionUserESMap loadUserByOpenID(String openID) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("open_id", openID)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过openID读取用户信息
     * @param openID
     * @return
     */
    public UnionUserESMap loadUserByCODE(String code) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("u_code", code)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过用户名读取用户信息
     * @param userName
     * @return
     */
    public UnionUserESMap loadUserByUserName(String userName) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("user_name", userName)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过手机号读取用户信息
     * @param userName
     * @return
     */
    public UnionUserESMap loadUserByPhone(String phone) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("phone", phone)
        return loadUserByRequest(kvMap)
    }


    public UnionUserESMap loadUserByRequest(Map<String, String> kvMap) {
        try {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
            Iterator it = kvMap.entrySet().iterator()
            while (it.hasNext()) {
                Map.Entry entity = (Map.Entry) it.next()
                String field = (String) entity.getKey()
                String value = (String) entity.getValue()
                System.out.println(entity.getKey() + "=" + entity.getValue())
                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(field, value))
            }

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(boolQueryBuilder)
            sourceBuilder.explain(true)// 设置是否按查询匹配度排序
            sourceBuilder.from(0)
            sourceBuilder.size(1)
            SearchRequest searchRequest = new SearchRequest()
            searchRequest.source(sourceBuilder)
            SearchHits hits = unionUserESService.find(searchRequest)
            SearchHit[] hit = hits.getHits()
            if (hit.size() > 0) {
                String json = hit[0].getSourceAsString()
                if (!StringUtils.isNull(json)) {
                    UnionUserESMap userESMap = unionUserESService.getObjectFromJson(json, UnionUserESMap.class) as UnionUserESMap
                    return userESMap
                }
            }
        } catch (Exception e) {
            println "UnionUserService loadUserByRequest ERROR:" + e.getLocalizedMessage()
        }
    }

    /**
     * 翻页形式更新下级用户的上级渠道信息
     * @param f_open_id
     * @param newChannelName
     * @param newChannelID
     * @param page
     * @param size
     */
    public void updateUserList(String f_open_id, String newChannelName, String newChannelID, int page, int size) {
        try {
            Map<String, String> params = new HashMap<>()
            params.put("f_open_id", f_open_id)
            SearchHits hits = unionUserESService.findByKVMap(params, page, size)
            SearchHit[] hit = hits.getHits()
            if (hit.size() > 0) {
                String json = hit[0].getSourceAsString()
                if (!StringUtils.isNull(json)) {
                    UnionUserESMap userESMap = unionUserESService.getObjectFromJson(json, UnionUserESMap.class) as UnionUserESMap
                    if (StringUtils.isNull(userESMap.getF_id())) {
                        UnionUserESMap updateUser = new UnionUserESMap()
                        updateUser.setOpen_id(userESMap.getOpen_id())
                        updateUser.setF_name(newChannelName)
                        updateUser.setF_id(newChannelID)
                        saveUser(updateUser)
                    }
                }
            }
            //下一页继续更新
            if ((page * size) < hits.getTotalHits().value) {
                updateUserList(f_open_id, newChannelName, newChannelID, page + 1, size)
            }
        } catch (Exception e) {
            println "updateUserList ERROR:" + e.getLocalizedMessage()
        }
    }


    /**
     * 批量更新下级用户的上级信息
     * @param f_userESMap
     */
    public void bulkUpdateUserFChannel(UnionUserESMap f_userESMap) {
        int page = 1
        int size = 100
        updateUserList(f_userESMap.getOpen_id(), f_userESMap.getChannel_name(), f_userESMap.getChannel_id(), page, size)
    }
}

class UnionUserTreeBean {
    String root_id
    String father_id
    String three_id
    String two_id
}

class UnionUserSetChannel {
    String open_id
    String channel_name
    String channel_id
    String password
    String md5
}
