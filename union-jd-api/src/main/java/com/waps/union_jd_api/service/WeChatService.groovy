package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class WeChatService {
    final static String APP_ID = "wx441c534516758ef0"
    final static String APP_SECRET = "db330f7a282f73595bccdd448612f8bd"
    final static String GET_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=[app_id]&secret=[app_secret]&code=[code]&grant_type=authorization_code"
    final static String GET_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=[access_token]&openid=[open_id]&lang=zh_CN"

    @Autowired
    UnionUserService unionUserService

    public String getUserInfo(String code, String channel) {
        return getUserInfo(APP_ID, APP_SECRET, code, channel)
    }
    /**
     * 获取微信用户信息
     * @param code
     * @param channel
     * @return
     */
    public String getUserInfo(String app_id, String app_secret, String code, String channel) {
        String retJson = getTokenInfo(app_id, app_secret, code)
        if (!StringUtils.isNull(retJson)) {
            JSONObject jsonObject = JSONObject.parse(retJson)
            if (jsonObject && jsonObject.get("errcode") == null) {
                TokenBean tokenBean = JSONObject.parseObject(retJson, TokenBean.class)
                String ret = getUserInfoByToken(tokenBean.getAccess_token(), tokenBean.getOpenid())
                unionUserService.syncWxUserInfo(tokenBean.getOpenid(), ret, channel)
                return ret
            } else {
                return retJson
            }
        } else {
            return null
        }
    }

    public String getTokenInfo(String app_id, String app_secret, String code) {
        String t_url = GET_TOKEN_URL.replaceAll("\\[app_id\\]", app_id).replaceAll("\\[app_secret\\]", app_secret).replaceAll("\\[code\\]", code)
        println "getTokenInfo:" + t_url
        String retJson = HttpUtils.getUrl(t_url)
//        String retJson = StringUtils.getUrlTxt(t_url)
        println "getTokenInfo return:" + retJson
        if (!StringUtils.isNull(retJson)) {
            return retJson
        } else {
            return null
        }

    }

    public String getUserInfoByToken(String accessToken, String openID) {
        String t_url = GET_USER_INFO_URL.replaceAll("\\[access_token\\]", accessToken).replaceAll("\\[open_id\\]", openID)
        println "getUserInfoByToken:" + t_url
        String retJson = HttpUtils.getUrl(t_url)
//        String retJson = StringUtils.getUrlTxt(t_url)
        println "getUserInfoByToken return:" + retJson
        return retJson
    }

}

class TokenBean {
    String access_token;
    Long expires_in;
    String refresh_token;
    String openid;
    String scope;
}

class ErrorBean {
    int errcode
    String errmsg
}
