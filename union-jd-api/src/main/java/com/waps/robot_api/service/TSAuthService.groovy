package com.waps.robot_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.utils.TSApiConfig
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import org.springframework.stereotype.Component

@Component
class TSAuthService {
    static TSTokenBean tokenReturnBean = new TSTokenBean()

    /**
     * token2小时刷新一次
     * 返回结果
     *{*     "code": 1,
     *     "result": "SUCCESS",
     *     "merchant": "201906011000001",
     *     "token": "509f62a966f9079ac25fc94f524e3b3f"
     *}* @return
     */
    public String getToken() {
        long nowTime = System.currentTimeSeconds()
        long _2h = 60 * 60 * 2
        if (tokenReturnBean != null
                && tokenReturnBean.getToken() != null
                && nowTime < (tokenReturnBean.getCreateTime() + _2h)) {
        } else {
            tokenReturnBean = new TSTokenBean()
            String url = TSApiConfig.AUTH_TOKEN_URL
            Map params = new HashMap()
            params.put("merchant", TSApiConfig.merchant_ID)
            params.put("secret", TSApiConfig.merchant_KEY)
            println url
            String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(params))
            println retJson
            if(!StringUtils.isNull(retJson)) {
                tokenReturnBean = JSONObject.parseObject(retJson, TSTokenBean.class) as TSTokenBean
                tokenReturnBean.setCreateTime(System.currentTimeSeconds())
            }
        }
        return tokenReturnBean.getToken()
    }
}

class TSTokenBean {
    int code
    String result
    String merchant
    String token
    String refresh_token
    Long createTime = 0l
}