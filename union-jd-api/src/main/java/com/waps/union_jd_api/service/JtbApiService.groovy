package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.elastic.search.utils.PageUtils
import com.waps.service.jd.es.domain.SkuBeanESMap
import com.waps.service.jd.es.domain.UnionEditorLogESMap
import com.waps.service.jd.es.service.UnionEditorLogESService
import com.waps.tools.security.MD5
import com.waps.union_jd_api.utils.Config
import com.waps.union_jd_api.utils.DateUtils
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JtbApiService {

    @Autowired
    private JDConvertLinkService jdConvertLinkService

    @Autowired
    private UnionEditorLogESService unionEditorLogESService

    /**
     * 自动login
     * @return
     */
    public String autoLogin() {
        String _sessionID = loginByAccount()
        String sessionID = loginBySessionID(_sessionID)
        return sessionID
    }
    /**
     * 京推宝 登录第一步接口
     * @return
     */
    public String loginByAccount() {
        Map<String, String> params = new HashMap<>()
        params.put("phone", Config.JTB_LOGIN_ACCOUNT_NAME)
        params.put("password", Config.JTB_LOGIN_ACCOUNT_PWD)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_LOGIN_ACCOUNT_URL, params, null)
        println jsonStr
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        String sessionId = jsonObject.getJSONObject("data").get("sessionId")
        println sessionId
        return sessionId
    }

    /**
     * 京推宝 登录第二步接口
     * @param sessionId
     * @return
     */
    public String loginBySessionID(String sessionId) {
        Map<String, String> params = new HashMap<>()
        params.put("sessionId", sessionId)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_LOGIN_SESSION_ID_URL, params, null)
        println jsonStr
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        String new_sessionId = jsonObject.getJSONObject("data").get("sessionId")
        println new_sessionId
        return new_sessionId
    }

    /**
     * 图片上传接口
     * @param sessionId
     * @param imgUrl
     * @return
     */
    public JSONObject uploadImage(String sessionId, String imgUrl) {
        Map<String, String> header = new HashMap<>()
        header.put("Content-Type", "multipart/form-data")
        header.put("Host", "jingtuibao.ixiaocong.net")
        header.put("Origin", "https://jingtuibao.ixiaocong.net")
        header.put("Cookie", "Admin-Token=" + sessionId)
        Map<String, Object> params = new HashMap<>()
        params.put("sessionId", sessionId)
        if (imgUrl && imgUrl.startsWith('http')) {
            try {
                String fileName = new MD5().getMD5(imgUrl) + ".jpg"
                String path = "/Users/xguang/temp06/" + fileName
                byte[] imgByte = HttpUtils.getImageFromNetByUrl(imgUrl)
                params.put("file", imgByte)

//                URL url = new URL(imgUrl)
//                DataInputStream dataInputStream = new DataInputStream(url.openStream())
//                FileOutputStream fileOutputStream = new FileOutputStream(new File(path))
//                ByteArrayOutputStream output = new ByteArrayOutputStream()
//                byte[] buffer = new byte[1024];
//                int length;
//                while ((length = dataInputStream.read(buffer)) > 0) {
//                    output.write(buffer, 0, length);
//                }
//                fileOutputStream.write(output.toByteArray());
//                dataInputStream.close();
//                fileOutputStream.close();
//                params.put("file", new File(path))

                String jsonStr = HttpUtils.postFormParams(Config.JTB_UPLOAD_IMG_URL, params, header)
                JSONObject jsonObject = JSONObject.parseObject(jsonStr)
                return jsonObject
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 添加发送任务接口
     * @param sessionId
     * @param list
     * @return
     */
    public JSONObject addSendList(String sessionId, String text, String images, String videos, String planTime) {
        Map<String, String> header = new HashMap<>()
        header.put("Content-Type", "application/x-www-form-urlencoded")
        header.put("Host", "jingtuibao.ixiaocong.net")
        header.put("Origin", "https://jingtuibao.ixiaocong.net")
        header.put("Cookie", "Admin-Token=" + sessionId)
        Map<String, String> params = new HashMap<>()
        params.put("sessionId", sessionId)
        params.put("text", text)
        params.put("images", images)
        params.put("videos", videos)
        params.put("planTime", planTime)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_ADD_SEND_URL, params, header)
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        return jsonObject
    }

    /**
     * 删除消息
     * @param sessionId
     * @param robotMessageId
     * @return
     */
    public JSONObject deleteMessage(String sessionId, Integer robotMessageId) {
        Map<String, String> header = new HashMap<>()
        header.put("Content-Type", "application/x-www-form-urlencoded")
        header.put("Host", "jingtuibao.ixiaocong.net")
        header.put("Origin", "https://jingtuibao.ixiaocong.net")
        header.put("Cookie", "Admin-Token=" + sessionId)
        Map<String, String> params = new HashMap<>()
        params.put("sessionId", sessionId)
        params.put("robotMessageId", robotMessageId)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_MESSAGE_DEL_URL, params, header)
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        return jsonObject
    }

    /**
     * 更新消息
     * @param sessionId
     * @param robotMessageId
     * @param weight
     * @return
     */
    public JSONObject updateMessage(String sessionId, JtbMessageBean messageBean) {
        Map<String, String> header = new HashMap<>()
        header.put("Content-Type", "application/x-www-form-urlencoded")
        header.put("Host", "jingtuibao.ixiaocong.net")
        header.put("Origin", "https://jingtuibao.ixiaocong.net")
        header.put("Cookie", "Admin-Token=" + sessionId)
        Map<String, String> params = new HashMap<>()
        params.put("sessionId", sessionId)
        params.put("robotMessageId", messageBean.getRobotMessageId())
        if (messageBean.getWeight() > 0) {
            params.put("weight", messageBean.getWeight())
        }
        if (messageBean.getText()) {
            params.put("text", messageBean.getText())
        }
        if (messageBean.getImages()) {
            params.put("images", messageBean.getImages())
        }
        if (messageBean.getVideos()) {
            params.put("videos", messageBean.getVideos())
        }

        String jsonStr = HttpUtils.postFormParams(Config.JTB_MESSAGE_UPDATE_URL, params, header)
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        return jsonObject
    }

    /**
     * 待发送队列
     * @param sessionId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public JSONObject findSendWaiting(String sessionId, Integer pageIndex, Integer pageSize) {
        Map<String, String> header = new HashMap<>()
        header.put("Content-Type", "application/x-www-form-urlencoded")
        header.put("Host", "jingtuibao.ixiaocong.net")
        header.put("Origin", "https://jingtuibao.ixiaocong.net")
        header.put("Cookie", "Admin-Token=" + sessionId)
        Map<String, Object> params = new HashMap<>()
        params.put("sessionId", sessionId)
        params.put("pageIndex", pageIndex)
        params.put("pageSize", pageSize)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_SEND_WAITING_URL, params, header)
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        return jsonObject
    }


    /**
     * 已发送队列
     * @param sessionId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public JSONObject findSendDone(String sessionId, Integer pageIndex, Integer pageSize) {
        Map<String, String> header = new HashMap<>()
        header.put("Content-Type", "application/x-www-form-urlencoded")
        header.put("Host", "jingtuibao.ixiaocong.net")
        header.put("Origin", "https://jingtuibao.ixiaocong.net")
        header.put("Cookie", "Admin-Token=" + sessionId)
        Map<String, Object> params = new HashMap<>()
        params.put("sessionId", sessionId)
        params.put("pageIndex", pageIndex)
        params.put("pageSize", pageSize)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_SEND_ED_URL, params, header)
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        return jsonObject
    }

    public JSONObject findSendDoneSku(String startTime, String endTime, Integer page, Integer size) {
        PageUtils pageUtils = new PageUtils(page, size)
        Map<String, Object> params = new HashMap<>()
        if (startTime && startTime.length() <= 11) {
            startTime = startTime + " 00:00:00"
        }
        if (endTime && endTime.length() <= 11) {
            endTime = endTime + " 23:59:59"
        }
        params.put("start_time", startTime)
        params.put("end_time", endTime)
        params.put("from", pageUtils.getFrom())
        params.put("size", pageUtils.getSize())
        SearchHits hits = unionEditorLogESService.findByFreeMarkerFromResource("es_script/jtb_send_done.json", params)
        long total = hits.getTotalHits().value
        SearchHit[] searchHits = hits.getHits()
        List<UnionEditorLogESMap> list = new ArrayList<>()
        for (SearchHit hit : searchHits) {
            UnionEditorLogESMap unionEditorLogESMap = unionEditorLogESService.getObjectFromJson(hit.getSourceAsString(), UnionEditorLogESMap.class) as UnionEditorLogESMap
            list.add(unionEditorLogESMap)
        }
        Map<String, Object> map = new HashMap<>()
        map.put("total", total)
        map.put("list", list)
        return map as JSONObject
    }


    /**
     * 批量同步
     */
    public String syncSendDoneJob(int page, int size) {
        String jd_host = "u.jd.com"
        if (page < 1) {
            page = 1
        }
        if (size < 1) {
            size = 1
        }
        //先login 拿sessionID
        String sessionId = null
        if (StringUtils.isNull(sessionId)) {
            sessionId = autoLogin()
        }
        JSONObject jsonObject = findSendDone(sessionId, page, size)
        JSONObject dataObj = jsonObject.getJSONObject('data')

        if (dataObj) {
            JSONArray array = dataObj.getJSONArray('list')
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i)
                UnionEditorLogESMap unionEditorLogESMap = JSONObject.parseObject(obj.toString(), UnionEditorLogESMap.class) as UnionEditorLogESMap
                //todo 找到内容中的链接，反向找skuID
                List<SkuBeanESMap> skuList = new ArrayList<>()
                List<String> urlList = jdConvertLinkService.getUrlList(unionEditorLogESMap.getText(), jd_host)
                for (String url : urlList) {
                    if (url) {
                        println "获取已发信息:" + unionEditorLogESMap.getRobotMessageId() + " " + unionEditorLogESMap.getSendtime() + " " + unionEditorLogESMap.getMessageType()
                        String skuId = jdConvertLinkService.getSkuIdFromUrl(url)
                        println "skuId:" + skuId
                        if (!StringUtils.isNull(skuId)) {
                            SkuBeanESMap skuBeanESMap = new SkuBeanESMap()
                            skuBeanESMap.setSkuId(skuId)
                            skuList.add(skuBeanESMap)
                        }
                    }
                }
                unionEditorLogESMap.setSkuList(skuList)
                unionEditorLogESMap.setSendtime(unionEditorLogESMap.getSendtime() + ":00")
                unionEditorLogESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
                String id = new MD5().getMD5(unionEditorLogESMap.getRobotMessageId() + "")
                unionEditorLogESMap.setId(id)
                unionEditorLogESService.save(unionEditorLogESMap.getId(), unionEditorLogESMap)
                "已发保存:" + i + "/" + array.size()
            }
            return array.size() + ""
        } else {
            return 0 + ""
        }
    }
}

class JtbMessageBean {
    String sessionId
    String images
    int weight
    int robotMessageId
    String videos
    String text
}