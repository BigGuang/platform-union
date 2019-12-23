package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.elastic.search.utils.PageUtils
import com.waps.service.jd.es.domain.JDMediaInfoESMap
import com.waps.service.jd.es.domain.UnionAppUserESMap
import com.waps.service.jd.es.service.UnionAppUserESService
import com.waps.tools.security.MD5
import com.waps.union_jd_api.utils.Config
import com.waps.union_jd_api.utils.DateUtils
import com.waps.utils.StringUtils
import org.elasticsearch.action.DocWriteResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.regex.Matcher
import java.util.regex.Pattern

@Component
class UnionAppUserService {

    @Autowired
    private UnionAppUserESService unionAppUserESService
    @Autowired
    private JDMediaService jdMediaService;


    /**
     * 生成用户的邀请码
     * @param unionAppUserESMap
     * @return
     */
    public String getUCode(UnionAppUserESMap unionAppUserESMap) {
        String _code = unionAppUserESMap.getId()
        String ucode = _code.substring(_code.length() - 6, _code.length())
        return ucode.toLowerCase()
    }

    public String makeInviteCode(String id) {
        if (!StringUtils.isNull(id)) {
            String ucode = id.substring(id.length() - 6, id.length())
            return ucode.toLowerCase()
        }
    }

    /**
     * 保存用户信息, 小程序以手机号作为用户标识
     * @param unionAppUserESMap
     * @return
     */
    public DocWriteResponse saveAppUser(UnionAppUserESMap unionAppUserESMap) {
        println "===收到 saveAppUser==="
        try {
            //id如果为空,将根据如下规则来生成ID
            if (StringUtils.isNull(unionAppUserESMap.getId())) {
                if (unionAppUserESMap.getPhone()) {
                    String md5_id = new MD5().getMD5(unionAppUserESMap.getPhone())
                    unionAppUserESMap.setId(md5_id)
                } else if (unionAppUserESMap.getOpen_id()) {
                    String md5_id = new MD5().getMD5(unionAppUserESMap.getOpen_id())
                    unionAppUserESMap.setId(md5_id)
                } else if (!StringUtils.isNull(unionAppUserESMap.getWx_id())) {
                    String md5_id = new MD5().getMD5(unionAppUserESMap.getWx_id())
                    unionAppUserESMap.setId(md5_id)
                }
            }

            //已有用户，f_code从已有信息读取，或从f_user_id生成
            UnionAppUserESMap _loadAppUserMap = loadUserByID(unionAppUserESMap.getId())
            if (_loadAppUserMap) {
                println "用户已存在:" + _loadAppUserMap.getUser_name() + " id:" + _loadAppUserMap.getId()
                if (!StringUtils.isNull(_loadAppUserMap.getF_code())) {
                    unionAppUserESMap.setF_code(_loadAppUserMap.getF_code())
                } else if (!StringUtils.isNull(_loadAppUserMap.getF_user_id())) {
                    unionAppUserESMap.setF_code(makeInviteCode(_loadAppUserMap.getF_user_id()))
                }

                if (StringUtils.isNull(unionAppUserESMap.getChannel_name())) {
                    unionAppUserESMap.setChannel_name(_loadAppUserMap.getChannel_name())
                }

                //解决小程序上丢失phone的bug
                if (!StringUtils.isNull(_loadAppUserMap.getPhone())) {
                    unionAppUserESMap.setPhone(_loadAppUserMap.getPhone())
                }
                println "f_code:" + unionAppUserESMap.getF_code()
            }

            unionAppUserESMap.setU_code(getUCode(unionAppUserESMap))

            //有邀请码的情况
            if (!StringUtils.isNull(unionAppUserESMap.getF_code())) {
                //找到上级用户信息
                UnionAppUserESMap f_userMap = loadUserByUCode(unionAppUserESMap.getF_code())
                if (f_userMap != null) {
                    //保存上级用户ID到f_user_id
                    if (!StringUtils.isNull(f_userMap.getId())) {
                        unionAppUserESMap.setInvite_user_id(f_userMap.getId())   //重要，记录邀请人，永不改变
                        unionAppUserESMap.setF_user_id(f_userMap.getId())      //先记录上级ID
                    }
                    //保存上级群关系，如果有的情况
                    if (!StringUtils.isNull(f_userMap.getChannel_name()) && !StringUtils.isNull(f_userMap.getChannel_id())) {
                        unionAppUserESMap.setF_name(f_userMap.getChannel_name())
                        unionAppUserESMap.setF_id(f_userMap.getChannel_id())
                    }
                    //继承上级的导师
                    if (!StringUtils.isNull(f_userMap.getT_user_id())) {
                        unionAppUserESMap.setT_user_id(f_userMap.getT_user_id())
                    }
                    //如果上级是导师，设置导师ID
                    if (Config.USER_ROLE_LEVEL_2 == f_userMap.getRole_id()) {
                        unionAppUserESMap.setT_user_id(f_userMap.getId())
                    }
                }
            }
            //补完群主信息,channel_name不为空就去补全
            if (!StringUtils.isNull(unionAppUserESMap.getChannel_name())) {
                unionAppUserESMap.setChannel_name(unionAppUserESMap.getChannel_name().replaceAll(" ", ""))
                JDMediaInfoESMap jdMediaInfoESMap = jdMediaService.getMediaInfoByChannelName(unionAppUserESMap.getChannel_name())
                if (jdMediaInfoESMap != null) {
                    unionAppUserESMap.setChannel_id(jdMediaInfoESMap.getChannel_id())
                }


                //有channel_name是超级用户, 同步上级关系到自己信息中
                UnionAppUserTreeBean unionAppUserTreeBean = getUnionAppUserTree(unionAppUserESMap.getChannel_name())
                if (unionAppUserTreeBean != null) {
                    if ("0" == unionAppUserTreeBean.getFather_id() && "0" == unionAppUserTreeBean.getRoot_id()) {
                        unionAppUserESMap.setF_code("ROOT")
                        unionAppUserESMap.setInvite_user_id("ROOT")
                        unionAppUserESMap.setA_user_id("")
                        unionAppUserESMap.setS_user_id("")
                        unionAppUserESMap.setT_user_id("")
                    }
                    if (!StringUtils.isNull(unionAppUserTreeBean.getFather_id()) && "0" != unionAppUserTreeBean.getFather_id()) {
                        unionAppUserESMap.setF_name(unionAppUserTreeBean.getFather_id())
                        unionAppUserESMap.setF_id("")
                    }
                    if (!StringUtils.isNull(unionAppUserTreeBean.getRoot_id()) && "0" != unionAppUserTreeBean.getRoot_id()) {
                        unionAppUserESMap.setG_name(unionAppUserTreeBean.getRoot_id())
                        unionAppUserESMap.setG_id("")
                    }


                    //同步上级信息
                    if (!StringUtils.isNull(unionAppUserTreeBean.getF_phone()) && unionAppUserTreeBean.getF_phone().length() == 11) {
                        String f_user_id = new MD5().getMD5(unionAppUserTreeBean.getF_phone())
                        unionAppUserESMap.setF_user_id(f_user_id)
                        unionAppUserESMap.setF_code(makeInviteCode(f_user_id))
                        unionAppUserESMap.setInvite_user_id(f_user_id)   //重要，记录邀请人，永不改变
                    }

                    //导师信息也同步过来
                    if (!StringUtils.isNull(unionAppUserTreeBean.getT_phone()) && unionAppUserTreeBean.getT_phone().length() == 11) {
                        String t_user_id = new MD5().getMD5(unionAppUserTreeBean.getT_phone())
                        unionAppUserESMap.setT_user_id(t_user_id)
                    }

                    //合作人ID同步
                    if (!StringUtils.isNull(unionAppUserTreeBean.getAgent_fid_phone()) && unionAppUserTreeBean.getAgent_fid_phone().length() == 11) {
                        String agent_fid_phone = new MD5().getMD5(unionAppUserTreeBean.getAgent_fid_phone())
                        unionAppUserESMap.setA_user_id(agent_fid_phone)
                    }

                    //超级合作人ID同步
                    if (!StringUtils.isNull(unionAppUserTreeBean.getAgent_sid_phone()) && unionAppUserTreeBean.getAgent_sid_phone().length() == 11) {
                        String agent_sid_phone = new MD5().getMD5(unionAppUserTreeBean.getAgent_sid_phone())
                        unionAppUserESMap.setS_user_id(agent_sid_phone)
                    }
                } else {
                    unionAppUserESMap.setF_id("")
                    unionAppUserESMap.setF_name("")
                    unionAppUserESMap.setG_id("")
                    unionAppUserESMap.setG_name("")
                    unionAppUserESMap.setA_user_id("")
                    unionAppUserESMap.setS_user_id("")
                    unionAppUserESMap.setT_user_id("")
                }
            }


            //补完上级信息
            if (!StringUtils.isNull(unionAppUserESMap.getF_name()) && StringUtils.isNull(unionAppUserESMap.getF_id())) {
                JDMediaInfoESMap jdMediaInfoESMap = jdMediaService.getMediaInfoByChannelName(unionAppUserESMap.getF_name())
                if (jdMediaInfoESMap != null)
                    unionAppUserESMap.setF_id(jdMediaInfoESMap.getChannel_id())
            }

            //补完上上级信息
            if (!StringUtils.isNull(unionAppUserESMap.getG_name()) && StringUtils.isNull(unionAppUserESMap.getG_id())) {
                JDMediaInfoESMap jdMediaInfoESMap = jdMediaService.getMediaInfoByChannelName(unionAppUserESMap.getG_name())
                if (jdMediaInfoESMap != null)
                    unionAppUserESMap.setG_id(jdMediaInfoESMap.getChannel_id())
            }

            //设置会员级别, rols_id不存在的时候，自动判断级别
            if (StringUtils.isNull(unionAppUserESMap.getRole_id())) {
                if (!StringUtils.isNull(unionAppUserESMap.getChannel_id()) && !StringUtils.isNull(unionAppUserESMap.getChannel_name())) {
                    unionAppUserESMap.setRole_id(Config.USER_ROLE_LEVEL_1)
                } else {
                    unionAppUserESMap.setRole_id(Config.USER_ROLE_LEVEL_0)  //普通会员
                }
            }

            if (!StringUtils.isNull(unionAppUserESMap.getId())) {

                println "===保存  saveAppUser==="
                if (_loadAppUserMap != null && !StringUtils.isNull(_loadAppUserMap.getId())) {
                    UpdateResponse updateResponse = updateUserInfo(unionAppUserESMap)
                    return updateResponse
                } else {
                    unionAppUserESMap.setModifytime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ''))
                    unionAppUserESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ''))
                    IndexResponse indexResponse = unionAppUserESService.save(unionAppUserESMap.getId(), unionAppUserESMap)
                    return indexResponse
                }
            }
        } catch (Exception e) {
            println "UnionAppUserService saveUser ERROR:" + e.getLocalizedMessage()
            e.printStackTrace()
        }
    }


    /**
     * 多参数读取用户信息
     * @param id
     * @param phone
     * @param open_id
     * @return
     */
    public UnionAppUserESMap loadUser(String id, String phone, String open_id) {
        UnionAppUserESMap unionAppUserESMap = new UnionAppUserESMap()
        if (!StringUtils.isNull(id)) {
            unionAppUserESMap = loadUserByID(id);
        } else if (!StringUtils.isNull(phone)) {
            unionAppUserESMap = loadUserByPhone(phone);
        } else if (!StringUtils.isNull(open_id)) {
            unionAppUserESMap = loadUserByOpenID(open_id);
        }

        if (unionAppUserESMap != null && StringUtils.isNull(unionAppUserESMap.getU_code())) {
            String u_code = getUCode(unionAppUserESMap);
            unionAppUserESMap.setU_code(u_code);
        }
        boolean isUpdate = false
        if (unionAppUserESMap && !StringUtils.isNull(unionAppUserESMap.getChannel_name())) {
            UnionAppUserTreeBean unionAppUserTreeBean = getUnionAppUserTree(unionAppUserESMap.getChannel_name())
            println "unionAppUserTreeBean.getU_type():" + unionAppUserTreeBean.getU_type()
            if (!unionAppUserESMap.getRole_id().equals(unionAppUserTreeBean.getU_type())) {
                unionAppUserESMap.setRole_id(unionAppUserTreeBean.getU_type())
                isUpdate = true
                updateUserInfo(unionAppUserESMap)
            }
        }


        if (unionAppUserESMap && StringUtils.isNull(unionAppUserESMap.getF_id()) && StringUtils.isNull(unionAppUserESMap.getChannel_id())) {
            ChannelInfo channelInfo = findCommissionPositionID(unionAppUserESMap, true)
            if (channelInfo.getChannel_id() > 0) {
                unionAppUserESMap.setF_id(channelInfo.getChannel_id() + "")
                unionAppUserESMap.setF_name(channelInfo.getChannel_name())
                isUpdate = true
            }
        }

        //暂不对f_id和f_name更新，实时查找
        if (false) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateUserInfo(unionAppUserESMap)
                }
            }).start();
        }


        return unionAppUserESMap
    }

    /**
     * 从接口读取用户层级关系
     * @param uid
     */
    public UnionAppUserTreeBean getUnionAppUserTree(String uid) {
        if (!StringUtils.isNull(uid)) {
            String r_num = System.currentTimeMillis()
            String md5 = new MD5().getMD5(uid + r_num + "Waps_jd_MD5key").toLowerCase()
            String url = Config.UNION_USER_TREE_URL.replace("[RNUM]", r_num).replace("[MD5]", md5).replace("[UID]", uid)
            println url
            String json = StringUtils.getUrlTxt(url)
            println json
            JSONObject retObj = JSONObject.parseObject(json)
            if (retObj.getInteger("code") == 200) {
                JSONObject jsonObject = retObj.get("data") as JSONObject
                UnionAppUserTreeBean unionUserTreeBean = jsonObject.toJavaObject(UnionAppUserTreeBean.class)
                return unionUserTreeBean
            }
        }
    }

    /**
     * 通过wx_id读取用户信息
     * @param wx_id
     * @return
     */
    public UnionAppUserESMap loadUserByWXID(String wx_id) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("wx_id", wx_id)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过user_id读取用户信息
     * @param user_id
     * @return
     */
    public UnionAppUserESMap loadUserByUserID(String user_id) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("user_id", user_id)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过ID读取用户信息，id是MD5
     * @param id
     * @return
     */
    public UnionAppUserESMap loadUserByID(String id) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("id", id)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过openID读取用户信息
     * @param openID
     * @return
     */
    public UnionAppUserESMap loadUserByOpenID(String openID) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("open_id", openID)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过openID读取用户信息
     * @param openID
     * @return
     */
    public UnionAppUserESMap loadUserByUCode(String code) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("u_code", code)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过用户名读取用户信息
     * @param userName
     * @return
     */
    public UnionAppUserESMap loadUserByUserName(String userName) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("user_name", userName)
        return loadUserByRequest(kvMap)
    }

    /**
     * 通过手机号读取用户信息
     * @param userName
     * @return
     */
    public UnionAppUserESMap loadUserByPhone(String phone) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("phone", phone)
        return loadUserByRequest(kvMap)
    }

    public UpdateResponse updateUserInfo(UnionAppUserESMap unionAppUserESMap) {
        if (unionAppUserESMap && !StringUtils.isNull(unionAppUserESMap.getId())) {
            JSONObject newJsonObj = (JSONObject) JSONObject.toJSON(unionAppUserESMap)
            Map<String, Object> newMap = (Map<String, Object>) newJsonObj
            UpdateResponse response = unionAppUserESService.update(unionAppUserESMap.getId(), newMap)
            return response
        }
    }

    public long countFans(Map<String, String> kvMap) {
        SearchHits hits = findFans(kvMap, 1, 1)
        return hits.getTotalHits().value
    }

    public SearchHits findFans(Map<String, String> kvMap, int page, int size) {
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
            PageUtils pageUtils = new PageUtils(page, size)
            sourceBuilder.from(pageUtils.getFrom())
            sourceBuilder.size(pageUtils.getSize())
            FieldSortBuilder fsb = SortBuilders.fieldSort("createtime");
            fsb.order(SortOrder.DESC);
            sourceBuilder.sort(fsb);
            SearchRequest searchRequest = new SearchRequest()
            searchRequest.source(sourceBuilder)
            SearchHits hits = unionAppUserESService.find(searchRequest)
            return hits
        } catch (Exception e) {
            println "UnionUserService loadUserByRequest ERROR:" + e.getLocalizedMessage()
        }
    }


    public UnionAppUserESMap loadUserByRequest(Map<String, String> kvMap) {
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
            SearchHits hits = unionAppUserESService.find(searchRequest)
            SearchHit[] hit = hits.getHits()
            if (hit.size() > 0) {
                String json = hit[0].getSourceAsString()
                if (!StringUtils.isNull(json)) {
                    UnionAppUserESMap unionAppUserESMap = unionAppUserESService.getObjectFromJson(json, UnionAppUserESMap.class) as UnionAppUserESMap
                    return unionAppUserESMap
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
    public void updateUserList(String f_user_id, String f_channelName, String f_channelID, int page, int size) {
        try {
            Map<String, String> params = new HashMap<>()
            params.put("f_user_id", f_user_id)
            SearchHits hits = unionAppUserESService.findByKVMap(params, page, size)
            SearchHit[] hit = hits.getHits()
            if (hit.size() > 0) {
                String json = hit[0].getSourceAsString()
                if (!StringUtils.isNull(json)) {
                    UnionAppUserESMap unionAppUserESMap = unionAppUserESService.getObjectFromJson(json, UnionAppUserESMap.class) as UnionAppUserESMap
                    if (StringUtils.isNull(unionAppUserESMap.getF_id())) {
                        UnionAppUserESMap updateUser = new UnionAppUserESMap()
                        updateUser.setOpen_id(unionAppUserESMap.getOpen_id())
                        updateUser.setF_name(f_channelName)
                        updateUser.setF_id(f_channelID)
                        saveAppUser(updateUser)
                    }
                }
            }
            //下一页继续更新
            if ((page * size) < hits.getTotalHits().value) {
                updateUserList(f_user_id, f_channelName, f_channelID, page + 1, size)
            }
        } catch (Exception e) {
            println "updateUserList ERROR:" + e.getLocalizedMessage()
        }
    }


    /**
     * 批量更新下级用户的上级信息
     * @param f_unionAppUserESMap
     */
    public void bulkUpdateUserFChannel(UnionAppUserESMap f_unionAppUserESMap) {
        int page = 1
        int size = 100
        updateUserList(f_unionAppUserESMap.getId(), f_unionAppUserESMap.getChannel_name(), f_unionAppUserESMap.getChannel_id(), page, size)
    }


    /**
     * 计算出当前'W'开头的最大渠道号，输出时+1
     * @return
     */
    public String findMaxChannelName() {
        String max_channel_name = "";
        int base_num = 100000;
        SearchHits hits = unionAppUserESService.findByFreeMarkerFromResource("es_script/union_app_user_max_channel.ftl", null)
        if (hits && hits.getTotalHits().value > 0) {
            SearchHit[] hitList = hits.getHits()
            Map<String, Object> map = hitList[0].sourceAsMap
            String _channel_name = map.get("channel_name")
            if (!StringUtils.isNull(_channel_name)) {
                String regex = "([0-9]{6,})"  //至少6位的纯数字
                Pattern pt = Pattern.compile(regex)
                Matcher mt = pt.matcher(_channel_name)
                while (mt.find()) {
                    String _num = mt.group()
                    try {
                        base_num = Integer.parseInt(_num)
                    } catch (Exception e) {
                    }
                }
            }
        }
        max_channel_name = "W" + (base_num + 1)
        return max_channel_name
    }

    /**
     * 找出直接购买的佣金受益pid
     * @return
     */
    public ChannelInfo findCommissionPositionID(UnionAppUserESMap unionAppUserESMap, boolean isLoopFindPid) {
        long pid = 0
        ChannelInfo channelInfo = new ChannelInfo()
        if (unionAppUserESMap != null) {
            if (!StringUtils.isNull(unionAppUserESMap.getChannel_id())) {
                pid = Long.parseLong(unionAppUserESMap.getChannel_id());
                channelInfo.setChannel_name(unionAppUserESMap.getChannel_name())
                channelInfo.setChannel_id(pid)
            } else if (!StringUtils.isNull(unionAppUserESMap.getF_id())) {
                pid = Long.parseLong(unionAppUserESMap.getF_id())
                channelInfo.setChannel_name(unionAppUserESMap.getF_name())
                channelInfo.setChannel_id(pid)
            }
        }
        if (isLoopFindPid) {
            //如果本人没有channel_id,找上级
            if (pid == 0) {
                channelInfo = findPositionIDLoop(unionAppUserESMap, 0)
            }
        }
        return channelInfo
    }


    /**
     * 一直往上找channel_id,直到找到一个
     * @param unionAppUserESMap
     * @return
     */
    public ChannelInfo findPositionIDLoop(UnionAppUserESMap unionAppUserESMap, int level) {
        level = level + 1
//        println "findPositionIDLoop level:" + level
        //防止死循环
        if (level > 100) return 0
        String f_code = unionAppUserESMap.getF_code()
        if (StringUtils.isNull(f_code)) {
            f_code = makeInviteCode(unionAppUserESMap.getF_user_id())
        }
//        println "上级邀请码:" + f_code
        if (true) {
            UnionAppUserESMap f_userMap = loadUserByUCode(f_code)

            if (f_userMap) {
                if (f_userMap.getChannel_id()) {
                    println "找到佣金收益 channel_id:" + f_userMap.getChannel_id() + "  channel_name:" + f_userMap.getChannel_name() + "  " +
                            "user_name:" + f_userMap.getUser_name() + "  " + f_userMap.getId()
                    String f_channel_name = f_userMap.getChannel_name()
                    Long pid = Long.parseLong(f_userMap.getChannel_id())
                    ChannelInfo channelInfo = new ChannelInfo()
                    channelInfo.setChannel_name(f_channel_name)
                    channelInfo.setChannel_id(pid)
                    return channelInfo
                } else {
                    return findPositionIDLoop(f_userMap, level)
                }
            } else {
                println "==邀请码为:" + f_code + " 的用户不存在!!!== "
            }
        }
    }

    public void logError(String str) {
        if (str) {
            str = com.waps.utils.DateUtils.getNow() + " " + str + "\n" + "==============="
            new File("/home/logs/error.log").append(str + "\n")
        }
    }
}

class UnionAppUserTreeBean {
    String u_phone
    String root_id
    String father_id
    String f_phone
    String t_phone    //导师手机号
    String u_type
    String three_id
    String two_id
    String agent_fid //合伙人
    String agent_fid_phone //合伙人手机号
    String agent_sid //超级合伙人
    String agent_sid_phone //超级合伙人手机号

}

class UnionAppUserSetChannel {
    String phone
    String channel_name
    String channel_id
    String password
    String md5
}

class BuildLinkBean {
    String skuId
    String uid
    String materialId
    String couponUrl
    String channel_id
    String f_id
    String place
}

class ChannelInfo {
    String channel_name
    Long channel_id = 0L;
}
