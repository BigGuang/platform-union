package com.waps.union_jd_api.controller;

import com.alibaba.fastjson.JSONObject;
import com.waps.security.Base64;
import com.waps.service.jd.api.bean.PromotionCodeCommonParams;
import com.waps.service.jd.api.bean.PromotionCodeParams;
import com.waps.service.jd.api.service.JdUnionService;
import com.waps.service.jd.es.domain.UnionAppUserESMap;
import com.waps.tools.security.MD5;
import com.waps.union_jd_api.bean.ReturnMessageBean;
import com.waps.union_jd_api.service.*;
import com.waps.union_jd_api.utils.JDConfig;
import com.waps.utils.ResponseUtils;
import com.waps.utils.StringUtils;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;

@Controller
@RequestMapping("/app_user")
public class UnionAppUserController {

    @Autowired
    private UnionAppUserService unionAppUserService;

    @Autowired
    private JdUnionService jdUnionService;


    /**
     * 保存小程序过来的用户信息
     *
     * @param unionAppUserESMap
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void saveAppUser(
            @RequestBody UnionAppUserESMap unionAppUserESMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        unionAppUserESMap.setFrom_type("app");
        unionAppUserESMap.setUser_type("app");
        try {
            if (StringUtils.isNull(unionAppUserESMap.getId())) {
                String md5_id = new MD5().getMD5(unionAppUserESMap.getPhone());
                unionAppUserESMap.setId(md5_id);
            }
            DocWriteResponse ret = unionAppUserService.saveAppUser(unionAppUserESMap);
            if (ret != null) {
                ResponseUtils.write(response, new ReturnMessageBean(200, "", ret).toString());
            } else {
                ResponseUtils.write(response, new ReturnMessageBean(500, "保存失败").toString());
            }

        } catch (Exception e) {
            System.out.println("saveAppUser ERROR:" + e.getLocalizedMessage());
            ResponseUtils.write(response, new ReturnMessageBean(500, "ERROR:" + e.getLocalizedMessage()).toString());
        }
    }

    /**
     * 读取小程序用户信息
     *
     * @param open_id
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/info")
    public void getAppUserInfo(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "open_id", required = false) String open_id,
            @RequestParam(value = "phone", required = false) String phone,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            UnionAppUserESMap unionAppUserESMap = new UnionAppUserESMap();
            if (!StringUtils.isNull(id)) {
                unionAppUserESMap = unionAppUserService.loadUserByID(id);
            } else if (!StringUtils.isNull(phone)) {
                unionAppUserESMap = unionAppUserService.loadUserByPhone(phone);
            } else if (!StringUtils.isNull(open_id)) {
                unionAppUserESMap = unionAppUserService.loadUserByOpenID(open_id);
            }

            if (unionAppUserESMap != null && StringUtils.isNull(unionAppUserESMap.getU_code())) {
                String u_code = unionAppUserService.getUCode(unionAppUserESMap);
                unionAppUserESMap.setU_code(u_code);
            }


            if (unionAppUserESMap != null && !StringUtils.isNull(unionAppUserESMap.getId())) {
                ResponseUtils.write(response, new ReturnMessageBean(200, "", unionAppUserESMap).toString());
            } else {
                ResponseUtils.write(response, new ReturnMessageBean(404, "用户不存在").toString());
            }
        } catch (Exception e) {
            System.out.println("getAppUserInfo ERROR:" + e.getLocalizedMessage());
            ResponseUtils.write(response, new ReturnMessageBean(500, "ERROR:" + e.getLocalizedMessage()).toString());
        }
    }

    @RequestMapping(value = "/children")
    public void getChildren(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (!StringUtils.isNull(phone)) {
            id = new MD5().getMD5(phone);
        }

        UnionAppUserESMap unionAppUserESMap = unionAppUserService.loadUserByID(id);
        if (!StringUtils.isNull(unionAppUserESMap.getU_code())) {
            params.put("f_code", unionAppUserESMap.getU_code());
        }

        SearchHits hits = unionAppUserService.findFans(params, page, size);
        long total = hits.getTotalHits().value;
        SearchHit[] hitList = hits.getHits();
        List<Map<String, Object>> userList = new ArrayList<>();
        for (SearchHit hit : hitList) {
            Map<String, Object> userMap = hit.getSourceAsMap();
            userList.add(userMap);
        }
        Map ret = new HashMap();
        ret.put("total", total);
        ret.put("list", userList);
        ResponseUtils.write(response, new ReturnMessageBean(200, "", ret));
    }


    @RequestMapping(value = "/check_invite_user")
    public void checkInviteUser(@RequestParam(value = "code", required = true) String code,
                                HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        try {
            code = URLDecoder.decode(code, "UTF-8");
            code = code.replaceAll(" ", "");
            if (code.length() == 6) {
                UnionAppUserESMap unionUserESMap = unionAppUserService.loadUserByCODE(code);
                if (unionUserESMap != null) {
                    ResponseUtils.write(response, new ReturnMessageBean(200, "", unionUserESMap).toString());
                } else {
                    ResponseUtils.write(response, new ReturnMessageBean(404, "未找到邀请码对应用户").toString());
                }
            } else {
                ResponseUtils.write(response, new ReturnMessageBean(405, "邀请码长度不正确").toString());
            }
        } catch (Exception e) {
            System.out.println("checkInviteUser ERROR:" + e.getLocalizedMessage());
            ResponseUtils.write(response, new ReturnMessageBean(500, "ERROR:" + e.getLocalizedMessage()).toString());
        }
    }

    @RequestMapping(value = "/fans_list")
    public void fansList(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "role", required = false) String role_type,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("f_user_id", id);
        if (!StringUtils.isNull(role_type)) {
            params.put("role_id", role_type);
        }
        SearchHits hits = unionAppUserService.findFans(params, page, size);
        if (hits != null) {
            long total = hits.getTotalHits().value;
            List<Map<String, Object>> list = new ArrayList<>();
            SearchHit[] _hitList = hits.getHits();
            for (int i = 0; i < _hitList.length; i++) {
                SearchHit _hit = _hitList[i];
                Map<String, Object> userMap = _hit.getSourceAsMap();
                list.add(userMap);
            }
            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("code", 200);
            returnMap.put("list", list);
            returnMap.put("total", total);
            ResponseUtils.write(response, JSONObject.toJSONString(returnMap));
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "服务端出现错误").toString());
        }
    }

    /**
     * 解析手机号
     *
     * @param param
     * @param response
     * @throws Exception
     */
    @RequestMapping("/getPhoneNumber")
    public void getPhoneNumber(
            @RequestBody PhoneParam param,
            HttpServletResponse response) throws Exception {

        byte[] encrypData = Base64.decode(param.getEncryp_data());
        byte[] ivData = Base64.decode(param.getIv_data());
        byte[] sessionKey = Base64.decode(param.getSession_key());

        String resultString = null;
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivData);
        SecretKeySpec keySpec = new SecretKeySpec(sessionKey, "AES");
        try {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//                Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                resultString = new String(cipher.doFinal(encrypData), "UTF-8");
            } catch (Exception e) {
                System.out.println("getPhoneNumber ERROR:" + e.getLocalizedMessage());
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                resultString = new String(cipher.doFinal(encrypData), "UTF-8");
            }
        } catch (Exception e) {
            System.out.println("getPhoneNumber ERROR:" + e.getLocalizedMessage());
        }

//        try {
//            str = decrypt(sessionKey, ivData, encrypData);
//        } catch (Exception e) {
//            System.out.println("getPhoneNumber ERROR:" + e.getLocalizedMessage());
//            TestUtils.outPrint(param);
//        }

        if (!StringUtils.isNull(resultString)) {
            JSONObject retJsonObj = JSONObject.parseObject(resultString);
            System.out.println(resultString);
            if (retJsonObj != null && !StringUtils.isNull(param.getOpen_id())) {
                UnionAppUserESMap userESMap = new UnionAppUserESMap();
                userESMap.setOpen_id(param.getOpen_id());
                userESMap.setPhone(retJsonObj.getString("phoneNumber"));
                unionAppUserService.saveAppUser(userESMap);
            }
            ResponseUtils.write(response, new ReturnMessageBean(200, "", retJsonObj));
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "手机号解码错误"));
        }

    }


    /**
     * 用户通过超级会员后的回调接口，更新下级用户所属群ID
     *
     * @param unionAppUserSetChannel
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/update_channel", method = RequestMethod.POST)
    public void setUserChannel(
            @RequestBody UnionAppUserSetChannel unionAppUserSetChannel,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String key = "Waps_jd_MD5key";
        //md5=new MD5(phone+channel_name+channel_id+key)   key="Waps_jd_MD5key"
        if (unionAppUserSetChannel != null && !StringUtils.isNull(unionAppUserSetChannel.getPhone())) {
            UnionAppUserESMap unionAppUserESMap = new UnionAppUserESMap();
            unionAppUserESMap.setPhone(unionAppUserSetChannel.getPhone());
            unionAppUserESMap.setChannel_name(unionAppUserSetChannel.getChannel_name());
            unionAppUserESMap.setChannel_id(unionAppUserSetChannel.getChannel_id());
            if (!StringUtils.isNull(unionAppUserSetChannel.getPassword())) {
                unionAppUserESMap.setUser_pwd_md5(unionAppUserSetChannel.getPassword());
            }
            String md5 = new MD5().getMD5(unionAppUserESMap.getPhone() + unionAppUserESMap.getChannel_name() + unionAppUserESMap.getChannel_id() + key);
            System.out.println(md5);
            if (md5.equalsIgnoreCase(unionAppUserSetChannel.getMd5())) {
                UnionAppUserESMap oldUser = unionAppUserService.loadUserByPhone(unionAppUserSetChannel.getPhone());
                if (oldUser != null && !StringUtils.isNull(oldUser.getId())) {
                    //异步处理
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            unionAppUserService.saveAppUser(unionAppUserESMap);
                            unionAppUserService.bulkUpdateUserFChannel(unionAppUserESMap);
                        }
                    }).start();
                    ResponseUtils.write(response, new ReturnMessageBean(200, "已保存"));
                } else {
                    ResponseUtils.write(response, new ReturnMessageBean(404, "用户不存在"));
                }
            } else {
                ResponseUtils.write(response, new ReturnMessageBean(401, "MD5值不正确"));
            }
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(400, "缺少参数"));
        }
    }

    /**
     * 最大channel_name
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/max_channel", method = RequestMethod.GET)
    public void getMaxChannel(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String max_channel_name = unionAppUserService.findMaxChannelName();
        ResponseUtils.write(response, new ReturnMessageBean(200, "", max_channel_name));
    }


    @RequestMapping(value = "/build_link", method = RequestMethod.POST)
    public void build_link(
            @RequestBody BuildLinkBean buildLinkBean,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (buildLinkBean != null) {
            if (!StringUtils.isNull(buildLinkBean.getUid())) {
                UnionAppUserESMap unionAppUserESMap = unionAppUserService.loadUserByID(buildLinkBean.getUid());
                if (unionAppUserESMap != null) {


                    //超级用户直接算佣金订单，非超级用户走通用
                    if (StringUtils.isNull(buildLinkBean.getPlace())) {
                        long pid = unionAppUserService.findCommissionPositionID(unionAppUserESMap, true);
                        PromotionCodeParams promotionCodeParams = new PromotionCodeParams();
                        promotionCodeParams.setApp_key(JDConfig.APP_KEY);
                        promotionCodeParams.setApp_secret(JDConfig.SECRET_KEY);
                        promotionCodeParams.setMaterialId(buildLinkBean.getMaterialId());
                        promotionCodeParams.setCouponUrl(buildLinkBean.getCouponUrl());
                        promotionCodeParams.setChainType(2);
                        promotionCodeParams.setPositionId(pid);
                        String retJson = jdUnionService.getGoodsUnionLink(promotionCodeParams);
                        JSONObject jsonObject = JSONObject.parseObject(retJson);
                        int code = jsonObject.getInteger("code");
                        if (jsonObject.getInteger("code") == 200) {
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            String shortURL = (String) dataObj.get("shortURL");
                            ResponseUtils.write(response, new ReturnMessageBean(code, "", shortURL));
                        } else {
                            String message = (String) jsonObject.get("message");
                            ResponseUtils.write(response, new ReturnMessageBean(code, message));
                        }
                    } else {
                        long pid = unionAppUserService.findCommissionPositionID(unionAppUserESMap, true);
                        PromotionCodeCommonParams promotionCodeCommonParams = new PromotionCodeCommonParams();
                        promotionCodeCommonParams.setApp_key(JDConfig.APP_KEY);
                        promotionCodeCommonParams.setApp_secret(JDConfig.SECRET_KEY);
                        promotionCodeCommonParams.setMaterialId(buildLinkBean.getMaterialId());
                        promotionCodeCommonParams.setCouponUrl(buildLinkBean.getCouponUrl());
                        promotionCodeCommonParams.setSiteId(JDConfig.SITE_ID);
                        promotionCodeCommonParams.setPositionId(JDConfig.SITE_PID_LONG);
                        //ext1=享受佣金pid+'_'+phone
                        String ext1 = pid + "_" + unionAppUserESMap.getPhone();
                        promotionCodeCommonParams.setExt1(ext1);
                        String retJson = jdUnionService.getGoodsUnionLinkCommon(promotionCodeCommonParams);
                        JSONObject jsonObject = JSONObject.parseObject(retJson);
                        int code = jsonObject.getInteger("code");
                        if (jsonObject.getInteger("code") == 200) {
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            String clickURL = (String) dataObj.get("clickURL");
                            ResponseUtils.write(response, new ReturnMessageBean(code, "", clickURL));
                        } else {
                            String message = (String) jsonObject.get("message");
                            ResponseUtils.write(response, new ReturnMessageBean(code, message));
                        }
                    }
                }
            } else {
                ResponseUtils.write(response, new ReturnMessageBean(404, "缺少用户参数"));
            }
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(404, "缺少参数"));
        }
    }
}
