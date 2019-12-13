package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.es.domain.MiniTokenESMap
import com.waps.service.jd.es.service.MiniTokenESService
import com.waps.union_jd_api.utils.Config
import com.waps.union_jd_api.utils.DateUtils
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MiniAppApiService {

    final static long TIME = (60 * 10 * 1000)  //提前10分钟重新获取

    static HashMap<String, MiniTokenBean> tokenMap = new HashMap<>()

    @Autowired
    private MiniTokenESService miniTokenESService

    /**
     * 通过appID获取token,走es缓存
     * @param appId
     */
    public getToken(String appId) {
        long nowTime = System.currentTimeMillis()
        MiniTokenESMap miniTokenESMap = miniTokenESService.load(appId, MiniTokenESMap.class) as MiniTokenESMap
        if (miniTokenESMap != null
                && !StringUtils.isNull(miniTokenESMap.getAccess_token())
                && miniTokenESMap.getEnd_time() > 0
                && miniTokenESMap.getEnd_time() - TIME > nowTime) {
            println appId + " " + miniTokenESMap.getAccess_token()
            println "过期时间:" + DateUtils.timeTmp2DateStr(miniTokenESMap.getEnd_time() + "")
            return miniTokenESMap.getAccess_token()
        } else {
            println "重新获取token:" + appId
            initMiniAppListToken(appId)
        }
    }

    /**
     * 加载app的信息并获取token,获取后保存到es
     * @param appId
     * @return
     */
    public String initMiniAppListToken(String appId) {
        String configPath = StringUtils.getRealPath("/WEB-INF/classes/config/mini_app_info.json")
        File configFile = new File(configPath)
        if (configFile.exists()) {
            StringBuffer buffer = new StringBuffer()
            configFile.eachLine { line ->
                buffer.append(line)
            }
            JSONArray array = JSONArray.parseArray(buffer.toString())
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.get(i)
                String name = obj.get("name")
                String app_id = obj.get("app_id")
                String app_secret = obj.get("app_secret")
                if (appId.equalsIgnoreCase(app_id)) {
                    MiniTokenBean miniTokenBean = getMiniToken(app_id, app_secret)
                    if (miniTokenBean != null) {
                        MiniTokenESMap miniTokenESMap = new MiniTokenESMap()
                        miniTokenESMap.setId(appId)
                        miniTokenESMap.setName(name)
                        miniTokenESMap.setApp_id(appId)
                        miniTokenESMap.setApp_secret(app_secret)
                        miniTokenESMap.setAccess_token(miniTokenBean.getAccess_token())
                        miniTokenESMap.setExp_time(miniTokenBean.getExp_time())
                        miniTokenESMap.setEnd_time(miniTokenBean.getEnd_time())
                        miniTokenESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
                        miniTokenESService.save(miniTokenESMap.getId(), miniTokenESMap)
                    }
                    return miniTokenBean.getAccess_token()
                }
            }
        }
    }
    /**
     * 实时获取小程序token
     * @param app_id
     * @param app_secret
     * @return
     */
    public MiniTokenBean getMiniToken(String app_id, String app_secret) {
        println "app_id:" + app_id
        println "app_secret:" + app_secret
        MiniTokenBean tokenBean = tokenMap.get(app_id)

        if (tokenBean != null && tokenBean.getEnd_time() - TIME > System.currentTimeMillis()) {
            return tokenBean
        }

        String url = Config.MINI_GET_TOKEN_URL.replace("[APPID]", app_id).replace("[APPSECRET]", app_secret)
        String ret = StringUtils.getUrlTxt(url)
        if (!StringUtils.isNull(ret)) {
            JSONObject tokenJsonObj = JSONObject.parseObject(ret)
            String token = tokenJsonObj.get("access_token")
            long exp_time = tokenJsonObj.getLong("expires_in")
            MiniTokenBean miniTokenBean = new MiniTokenBean()
            miniTokenBean.setAccess_token(token)
            miniTokenBean.setExp_time(exp_time)
            miniTokenBean.setEnd_time(System.currentTimeMillis() + (exp_time * 1000))
            tokenMap.put(app_id, miniTokenBean)
            return miniTokenBean
        }
    }


    /**
     * 获取小程序带参二维码
     * @param qrParam
     * @param token
     * @param qrImgPath
     * @return
     */
    public String getMiniQRFromToken(QRParam qrParam, String token, String qrImgPath) {
        String url = Config.MINI_POST_QR_URL.replace("[ACCESS_TOKEN]", token)
        Map<String, Object> param = new HashMap<>()
        param.put("scene", qrParam.getScene())
        param.put("width", qrParam.getWidth())
        param.put("path", qrParam.getPage())
        param.put("auto_color", qrParam.getAuto_color())
        String json = JSONObject.toJSONString(param)

        println "QR路径:" + qrImgPath
        String ret = HttpUtils.postJsonInputStream(url, json, qrImgPath)

//        String path=StringUtils.getRealPath("/images/qr/")+""+scene+".jpg"
        return ret
    }


    /**
     * 下发小程序和公众号统一的服务消息
     * @return
     */
    public String sendUniformMessage(String appID, UniformMessageBean uniformMessageBean) {
        if (uniformMessageBean != null && !StringUtils.isNull(uniformMessageBean.getTouser())) {
            String token = getToken(appID)
            String sendUrl = Config.SEND_UNIFORM_MESSAGE_URL.replace("ACCESS_TOKEN", token)
            String json = JSONObject.toJSONString(uniformMessageBean)
            println json
            String ret = HttpUtils.postJsonString(sendUrl, json)
            println ret
            return ret
        }
    }
}

class MiniTokenBean {
    String access_token
    long exp_time
    long end_time
}

class UniformMessageBean {
    String touser   //用户openid，可以是小程序的openid，也可以是mp_template_msg.appid对应的公众号的openid
    WeAppTemplateMsg weapp_template_msg
    MPTemplateMsg mp_template_msg
}

//小程序模板消息相关的信息，可以参考小程序模板消息接口; 有此节点则优先发送小程序模板消息
class WeAppTemplateMsg {
    String template_id
    String page
    String form_id
    Map<String, MessageParamsBean> data = new HashMap<>()
    String emphasis_keyword
}

//公众号模板消息相关的信息，可以参考公众号模板消息接口；有此节点并且没有weapp_template_msg节点时，发送公众号模板消息
class MPTemplateMsg {
    String appid
    String template_id
    String url
    MiniProgram miniprogram
    Map<String, MessageParamsBean> data = new HashMap<>()
}

class MiniProgram {
    MiniProgram(String appid, String page) {
        this.appid = appid
        this.pagepath = page
    }
    String appid
    String pagepath
}

class MessageParamsBean {
    MessageParamsBean(String value, String color) {
        this.value = value
        this.color = color
    }
    String value
    String color
}



