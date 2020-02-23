package com.waps.union_jd_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.security.Base64
import com.waps.service.jd.es.domain.UnionUserESMap
import com.waps.union_jd_api.bean.WeAppUserInfoBean
import com.waps.union_jd_api.service.QRParam
import com.waps.union_jd_api.service.UnionUserService
import com.waps.union_jd_api.service.MiniAppService
import com.waps.union_jd_api.utils.Config
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletResponse
import java.security.spec.AlgorithmParameterSpec

@Controller
@RequestMapping("/we_app")
class MiniAppController {

    @Autowired
    UnionUserService unionUserService
    @Autowired
    MiniAppService miniAppService

    /**
     * 拿用户open_id
     * @param app_id
     * @param secret
     * @param js_code
     * @param grant_type
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/user")
    public String getAppUserInfo(
            @RequestParam(value = "appid", required = true) String app_id,
            @RequestParam(value = "secret", required = true) String secret,
            @RequestParam(value = "js_code", required = true) String js_code,
            @RequestParam(value = "grant_type", required = false) String grant_type,
            HttpServletResponse response) throws Exception {
        String url = Config.MINI_GET_OPEN_ID + "?appid=" + app_id + "&secret=" + secret + "&js_code=" + js_code + "&grant_type=authorization_code";
        System.out.println("url=" + url);
        String json = StringUtils.getUrlTxt(url);
        System.out.println(json);
        ResponseUtils.write(response, json)
    }

    /**
     * 保存用户信息到数据库
     * @param userInfoMap
     * @return
     */
    @RequestMapping(value = "/save_user", method = RequestMethod.POST)
    public String saveUserInfo(@RequestBody WeAppUserInfoBean weAppUserInfoBean,
                               HttpServletResponse response) throws Exception {
        Map map = new HashMap()

        if (!StringUtils.isNull(weAppUserInfoBean.getOpen_id())) {
            UnionUserESMap userESMap = new UnionUserESMap()
            userESMap.setOpen_id(weAppUserInfoBean.getOpen_id())
            userESMap.setUser_name(weAppUserInfoBean.getNickName())
            userESMap.setUser_icon(weAppUserInfoBean.getAvatarUrl())
            userESMap.setProvince(weAppUserInfoBean.getProvince())
            userESMap.setCountry(weAppUserInfoBean.getCountry())
            userESMap.setCity(weAppUserInfoBean.getCity())
            userESMap.setUser_sex(weAppUserInfoBean.getGender() + "")
            if (!StringUtils.isNull(weAppUserInfoBean.getInvite_code())) {
                userESMap.setInvite_code(weAppUserInfoBean.getInvite_code())
            }
            unionUserService.saveUser(userESMap)
            map.put("code", 200)
        } else {
            map.put("code", 500)
        }
        String json = JSONObject.toJSONString(map)
        ResponseUtils.write(response, json)
    }

    /**
     * 解析手机号
     * @param open_id
     * @param encryp_data
     * @param iv_data
     * @param session_key
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/getPhoneNumber")
    public String getPhoneNumber(
            @RequestBody PhoneParam param,
            HttpServletResponse response) throws Exception {

        byte[] encrypData = Base64.decode(param.getEncryp_data());
        byte[] ivData = Base64.decode(param.getIv_data());
        byte[] sessionKey = Base64.decode(param.getSession_key());
        String str = "";
        try {
            str = decrypt(sessionKey, ivData, encrypData);
        } catch (Exception e) {
            println "getPhoneNumber ERROR:" + e.getLocalizedMessage()
            println PhoneParam.toString()
        }
        JSONObject retJsonObj = JSONObject.parseObject(str)
        System.out.println(str);
        if (retJsonObj != null && !StringUtils.isNull(param.getOpen_id())) {
            UnionUserESMap userESMap = new UnionUserESMap()
            userESMap.setOpen_id(param.getOpen_id())
            userESMap.setPhone(retJsonObj.getString("phoneNumber"))
            unionUserService.saveUser(userESMap)
        }
        Map map = new HashMap()
        map.put("data", retJsonObj)
        String json = JSONObject.toJSONString(map)
        ResponseUtils.write(response, json)
    }


    @RequestMapping("/qr")
    public String getMiniQR(
            @RequestBody QRParam qrParam,
            HttpServletResponse response) throws Exception {
        println "/we_app/qr"
        println qrParam
        String path = miniAppService.getMiniQR(qrParam)
        Map map = new HashMap()
        map.put("data", path)
        ResponseUtils.write(response, JSONObject.toJSONString(map))
    }


    /**
     * 解密
     * @param key
     * @param iv
     * @param encData
     * @return
     * @throws Exception
     */
    public static String decrypt(byte[] key, byte[] iv, byte[] encData) throws Exception {

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        //解析解密后的字符串
        return new String(cipher.doFinal(encData), "UTF-8");
    }

}

class PhoneParam {
    String open_id
    String encryp_data
    String iv_data
    String session_key
}

