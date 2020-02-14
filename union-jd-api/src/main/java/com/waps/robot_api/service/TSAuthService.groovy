package com.waps.robot_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.utils.TSApiConfig
import com.waps.service.jd.es.domain.TSAuthTokenESMap
import com.waps.service.jd.es.service.TSAuthTokenESService
import com.waps.tools.security.MD5
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TSAuthService {

    @Autowired
    TSAuthTokenESService tsAuthTokenESService

    /**
     * token2小时刷新一次
     * 返回结果
     * @return
     */
    public String getToken() {
        return getToken(TSApiConfig.merchant_ID)
    }


    /**
     * token2小时刷新一次
     * 返回结果
     *{*     "code": 1,
     *     "result": "SUCCESS",
     *     "merchant": "201906011000001",
     *     "token": "509f62a966f9079ac25fc94f524e3b3f"
     *}* @return
     */
    public String getToken(String merchant_ID) {
//        String id=TSApiConfig.merchant_ID
        long nowTime = System.currentTimeSeconds()
        long _2h = 60 * 60 * 2
        TSAuthTokenESMap tokenESMap = tsAuthTokenESService.load(merchant_ID, TSAuthTokenESMap.class) as TSAuthTokenESMap
        if (tokenESMap != null
                && tokenESMap.getToken() != null
                && nowTime < (tokenESMap.getCreateTime() + _2h)) {
        } else {
            tokenESMap = new TSAuthTokenESMap()
            String url = TSApiConfig.AUTH_TOKEN_URL
            Map params = new HashMap()
            params.put("merchant", TSApiConfig.merchant_ID)
            params.put("secret", TSApiConfig.merchant_KEY)
            println url
            String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(params))
            println retJson
            if (!StringUtils.isNull(retJson)) {
                tokenESMap = JSONObject.parseObject(retJson, TSAuthTokenESMap.class) as TSAuthTokenESMap
                tokenESMap.setCreateTime(System.currentTimeSeconds())
                tokenESMap.setId(merchant_ID)
                tsAuthTokenESService.save(merchant_ID, tokenESMap)
            }
        }
        return tokenESMap.getToken()
    }
}