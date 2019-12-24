package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.waps.union_jd_api.utils.Config
import com.waps.union_jd_api.utils.HttpUtils
import org.springframework.stereotype.Component

@Component
class JtbApiService {

    /**
     * 京推宝 登录第一步接口
     * @return
     */
    public String loginByAccount() {
        Map<String, String> params = new HashMap<>()
        params.put("phone", Config.JTB_LOGIN_ACCOUNT_NAME)
        params.put("password", Config.JTB_LOGIN_ACCOUNT_PWD)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_LOGIN_ACCOUNT_URL, params)
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
        String jsonStr = HttpUtils.postFormParams(Config.JTB_LOGIN_ACCOUNT_URL, params)
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
    public String uploadImage(String sessionId,String imgUrl){
        Map<String, String> params = new HashMap<>()
        params.put("sessionId", sessionId)
        return null
    }

    /**
     * 添加发送任务接口
     * @param sessionId
     * @param list
     * @return
     */
    public String addSendList(String sessionId,List<Object> list){
        Map<String, String> params = new HashMap<>()
        params.put("sessionId", sessionId)
        params.put("text", sessionId)
        params.put("images", sessionId)
        params.put("videos", sessionId)
        params.put("planTime", sessionId)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_ADD_SEND_URL, params)
        println jsonStr
        return null
    }
}
