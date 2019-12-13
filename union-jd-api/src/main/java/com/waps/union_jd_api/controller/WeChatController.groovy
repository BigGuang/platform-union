package com.waps.union_jd_api.controller

import com.waps.union_jd_api.service.WeChatService
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/wechat")
class WeChatController {

    @Autowired
    WeChatService weChatService

    @RequestMapping(value = "/user_info")
    public void add_black_channel(
            @RequestParam(value = "code", required = true) String code,
            @RequestParam(value = "channel", required = false) String channel,
            @RequestParam(value = "app_id", required = false) String app_id,
            @RequestParam(value = "app_secret", required = false) String app_secret,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        println code
        String result = ""
        if (!StringUtils.isNull(app_id) && !StringUtils.isNull(app_secret)) {
            result = weChatService.getUserInfo(app_id, app_secret, code, channel)
        } else {
            result = weChatService.getUserInfo(code, channel)
        }
        println "/wechat/user_info"
        ResponseUtils.write(response, result)
    }

    @RequestMapping(value = "/get_qr")
    public void getQR(
            @RequestParam(value = "fid", required = true) String fid,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String url = "http://wx.wapg.cn/qr?site=wpjx&channel=" + fid
        String result = StringUtils.getUrlTxt(url)
        ResponseUtils.write(response, result)
    }
}
