package com.waps.robot_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSMessageBean
import com.waps.robot_api.bean.response.TSResponseRobotInfoBean
import com.waps.robot_api.service.TSRobotConfigService
import com.waps.robot_api.service.TSRobotFriendService
import com.waps.robot_api.service.TSRobotMessageService
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/robot/friend")
class TSRobotFriendController {

    @Autowired
    TSRobotFriendService tsRobotFriendService

    @Autowired
    TSRobotMessageService tsRobotMessageService

    @RequestMapping(value = "/list")
    public void friendList(
            @RequestParam(value = "robot_id", required = true) String vcRobotSerialNo,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotFriendService.getRobotFriendListStr(vcRobotSerialNo)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    @RequestMapping(value = "/accept_new")
    public void acceptNewFriend(
            @RequestParam(value = "robot_id", required = true) String vcRobotSerialNo,
            @RequestParam(value = "serial_no", required = true) String vcSerialNo,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotFriendService.acceptNewFriendRequest(vcRobotSerialNo, vcSerialNo)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    @RequestMapping(value = "/send_message")
    public void sendMessage(
            @RequestParam(value = "robot_id", required = true) String vcRobotSerialNo,
            @RequestParam(value = "serial_no", required = true) String vcRelaSerialNo,
            @RequestParam(value = "serial_no", required = true) String vcToWxSerialNo,
            @RequestBody TSMessageBean tsMessageBean,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotMessageService.sendPrivateMessage(vcRobotSerialNo, vcRelaSerialNo, vcToWxSerialNo, tsMessageBean)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    @RequestMapping(value = "/delete")
    public void deleteFriend(
            @RequestParam(value = "robot_id", required = true) String vcRobotSerialNo,
            @RequestParam(value = "contact_id", required = true) String vcContactSerialNo,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotFriendService.deleteFriend(vcRobotSerialNo, vcContactSerialNo)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }
}
