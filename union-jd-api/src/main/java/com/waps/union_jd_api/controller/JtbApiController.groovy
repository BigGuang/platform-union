package com.waps.union_jd_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.service.JtbApiService
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/jtb/api")
class JtbApiController {

    @Autowired
    private JtbApiService jtbApiService

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void getLink(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String _sessionID = jtbApiService.loginByAccount()
        String sessionID = jtbApiService.loginBySessionID(_sessionID)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", sessionID));
    }


    @RequestMapping(value = "/uploadImage")
    public void uploadImage(
            @RequestBody UpLoadImageParams params,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        JSONObject jsonObject = jtbApiService.uploadImage(params.getSessionId(), params.getUrl())
        if (jsonObject != null) {
            ResponseUtils.writeJsonObject(response, jsonObject)
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "出现错误"))
        }
    }

    @RequestMapping(value = "/add_send")
    public void addSendList(
            @RequestBody AddSendParams params,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        JSONObject jsonObject = jtbApiService.addSendList(params.getSessionId(), params.getText(), params.getImages(), params.getVideos(), params.getPlanTime())
        if (jsonObject != null) {
            ResponseUtils.writeJsonObject(response, jsonObject)
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "出现错误"))
        }
    }

    @RequestMapping(value = "/send_waiting")
    public void findSendWaiting(
            @RequestParam(value = "sessionId", required = true) String sessionId,
            @RequestParam(value = "pageIndex", required = true) Integer pageIndex,
            @RequestParam(value = "pageSize", required = true) Integer pageSize,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String retJson = jtbApiService.findSendWaiting(sessionId, pageIndex, pageSize)
        JSONObject jsonObject = JSONObject.parseObject(retJson)
        if (jsonObject != null) {
            ResponseUtils.writeJsonObject(response, jsonObject)
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "出现错误"))
        }
    }

    @RequestMapping(value = "/send_done")
    public void findSendDone(
            @RequestParam(value = "sessionId", required = true) String sessionId,
            @RequestParam(value = "pageIndex", required = true) Integer pageIndex,
            @RequestParam(value = "pageSize", required = true) Integer pageSize,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String retJson = jtbApiService.findSendDone(sessionId, pageIndex, pageSize)
        JSONObject jsonObject = JSONObject.parseObject(retJson)
        if (jsonObject != null) {
            ResponseUtils.writeJsonObject(response, jsonObject)
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "出现错误"))
        }
    }

    @RequestMapping(value = "/send_done_sku")
    public void findSendDoneSku(
            @RequestParam(value = "start_time", required = true) String start_time,
            @RequestParam(value = "end_time", required = true) String end_time,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        JSONObject jsonObject = jtbApiService.findSendDoneSku(start_time, end_time, page, size)
        if (jsonObject != null) {
            ResponseUtils.writeJsonObject(response, jsonObject)
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "出现错误"))
        }
    }


    @RequestMapping(value = "/sync_send_done")
    public void syncSendDone(
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String retJson = jtbApiService.syncSendDoneJob(page, size)
        if (retJson != null) {
            ResponseUtils.write(response, new ReturnMessageBean(200, "", retJson))
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "出现错误"))
        }
    }

}

class UpLoadImageParams {
    String sessionId
    String url
}

class AddSendParams {
    String sessionId
    String text
    String images
    String videos
    String planTime
}
