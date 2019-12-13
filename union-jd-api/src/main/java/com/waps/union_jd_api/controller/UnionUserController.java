package com.waps.union_jd_api.controller;

import com.alibaba.fastjson.JSONObject;
import com.waps.service.jd.es.domain.UnionUserESMap;
import com.waps.tools.security.MD5;
import com.waps.union_jd_api.bean.ReturnMessageBean;
import com.waps.union_jd_api.service.MessageBean;
import com.waps.union_jd_api.service.UnionSmsService;
import com.waps.union_jd_api.service.UnionUserService;
import com.waps.union_jd_api.service.UnionUserSetChannel;
import com.waps.utils.ResponseUtils;
import com.waps.utils.StringUtils;
import org.elasticsearch.action.DocWriteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UnionUserController {

    @Autowired
    private UnionUserService unionUserService;

    @Autowired
    private UnionSmsService unionSmsService;


    /**
     * 保存小程序过来的用户信息
     *
     * @param userESMap
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/app/save", method = RequestMethod.POST)
    public void saveAppUser(
            @RequestBody UnionUserESMap userESMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        userESMap.setFrom_type("app");
        userESMap.setUser_type("app");
        try {
            DocWriteResponse ret = unionUserService.saveUser(userESMap);
            ResponseUtils.write(response, new ReturnMessageBean(200, "", ret).toString());
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
    @RequestMapping(value = "/app/info")
    public void getAppUserInfo(
            @RequestParam(value = "open_id", required = false) String open_id,
            @RequestParam(value = "phone", required = false) String phone,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            UnionUserESMap userESMap = new UnionUserESMap();
            if (!StringUtils.isNull(open_id)) {
                userESMap = unionUserService.loadUserByOpenID(open_id);
            } else if (!StringUtils.isNull(open_id)) {
                userESMap = unionUserService.loadUserByPhone(phone);
            }

            if (userESMap != null && StringUtils.isNull(userESMap.getU_code())) {
                String u_code = unionUserService.getUCode(userESMap.getOpen_id());
                userESMap.setU_code(u_code);
            }
            if (userESMap != null && !StringUtils.isNull(userESMap.getOpen_id())) {
                ResponseUtils.write(response, new ReturnMessageBean(200, "", userESMap).toString());
            } else {
                ResponseUtils.write(response, new ReturnMessageBean(404, "用户不存在").toString());
            }
        } catch (Exception e) {
            System.out.println("getAppUserInfo ERROR:" + e.getLocalizedMessage());
            ResponseUtils.write(response, new ReturnMessageBean(500, "ERROR:" + e.getLocalizedMessage()).toString());
        }
    }

    @RequestMapping(value = "/app/check_invite_user")
    public void checkInviteUser(@RequestParam(value = "code", required = true) String code,
                                HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        try {
            if (code.length() == 6) {
                UnionUserESMap unionUserESMap = unionUserService.loadUserByCODE(code);
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

    /**
     * 读取微信公众号用户信息
     *
     * @param id
     * @param wx_id
     * @param open_id
     * @param user_id
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/wx/info", method = RequestMethod.GET)
    public void getWxUserInfo(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "wx_id", required = false) String wx_id,
            @RequestParam(value = "open_id", required = false) String open_id,
            @RequestParam(value = "user_id", required = false) String user_id,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        UnionUserESMap userESMap = new UnionUserESMap();
        if (!StringUtils.isNull(id)) {
            userESMap = unionUserService.loadUserByID(id);
        } else if (!StringUtils.isNull(wx_id)) {
            userESMap = unionUserService.loadUserByWXID(wx_id);
        } else if (!StringUtils.isNull(open_id)) {
            userESMap = unionUserService.loadUserByOpenID(open_id);
        } else if (!StringUtils.isNull(user_id)) {
            userESMap = unionUserService.loadUserByUserID(user_id);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("data", userESMap);
        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);
    }

    /**
     * 保存微信公众号和网页授权过来的用户信息
     *
     * @param userESMap
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/wx/save", method = RequestMethod.POST)
    public void saveWxUser(
            @RequestBody UnionUserESMap userESMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        userESMap.setFrom_type("wx");
        userESMap.setUser_type("wx");
        DocWriteResponse ret = unionUserService.saveUser(userESMap);
        Map<String, Object> map = new HashMap<>();
        if (ret != null) {
            map.put("data", ret);
        }
        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void saveUser(
            @RequestBody UnionUserESMap userESMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        DocWriteResponse ret = unionUserService.saveUser(userESMap);
        Map<String, Object> map = new HashMap<>();
        if (ret != null) {
            map.put("data", ret);
        }
        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);
    }


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public void getUser(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "wx_id", required = false) String wx_id,
            @RequestParam(value = "open_id", required = false) String open_id,
            @RequestParam(value = "user_id", required = false) String user_id,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        UnionUserESMap userESMap = new UnionUserESMap();
        if (!StringUtils.isNull(id)) {
            userESMap = unionUserService.loadUserByID(id);
        } else if (!StringUtils.isNull(wx_id)) {
            userESMap = unionUserService.loadUserByWXID(wx_id);
        } else if (!StringUtils.isNull(open_id)) {
            userESMap = unionUserService.loadUserByOpenID(open_id);
        } else if (!StringUtils.isNull(user_id)) {
            userESMap = unionUserService.loadUserByUserID(user_id);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("data", userESMap);
        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);
    }

    @RequestMapping(value = "/verify/send", method = RequestMethod.GET)
    public void sendSmsCode(
            @RequestParam(value = "phone", required = false) String phone,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        MessageBean messageBean = unionSmsService.sendCheckSMS(phone);
        String json = JSONObject.toJSONString(messageBean);
        ResponseUtils.write(response, json);
    }

    @RequestMapping(value = "/verify/check", method = RequestMethod.GET)
    public void checkCode(
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "code", required = false) String code,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        MessageBean messageBean = unionSmsService.checkSMS(phone, code);
        String json = JSONObject.toJSONString(messageBean);
        ResponseUtils.write(response, json);
    }


    @RequestMapping(value = "/app/update_channel", method = RequestMethod.POST)
    public void setUserChannel(
            @RequestBody UnionUserSetChannel unionUserSetChannel,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String key = "Waps_jd_MD5key";
        //md5=new MD5(open_id+channel_name+channel_id+key)   key="Waps_jd_MD5key"
        if (unionUserSetChannel != null && !StringUtils.isNull(unionUserSetChannel.getOpen_id())) {
            UnionUserESMap unionUserESMap = new UnionUserESMap();
            unionUserESMap.setOpen_id(unionUserSetChannel.getOpen_id());
            unionUserESMap.setChannel_name(unionUserSetChannel.getChannel_name());
            unionUserESMap.setChannel_id(unionUserSetChannel.getChannel_id());
            if (!StringUtils.isNull(unionUserSetChannel.getPassword())) {
                unionUserESMap.setUser_pwd_md5(unionUserSetChannel.getPassword());
            }
            String md5 = new MD5().getMD5(unionUserESMap.getOpen_id() + unionUserESMap.getChannel_name() + unionUserESMap.getChannel_id() + key);
            System.out.println(md5);
            if (md5.equalsIgnoreCase(unionUserSetChannel.getMd5())) {
                UnionUserESMap oldUser = unionUserService.loadUserByOpenID(unionUserSetChannel.getOpen_id());
                if (oldUser != null && !StringUtils.isNull(oldUser.getId())) {
                    //异步处理
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            unionUserService.saveUser(unionUserESMap);
                            unionUserService.bulkUpdateUserFChannel(unionUserESMap);
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


}
