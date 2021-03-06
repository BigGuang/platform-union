package com.waps.robot_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSMessageBean
import com.waps.robot_api.service.TSRobotFriendService
import com.waps.robot_api.service.TSRobotMessageService
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

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
            @RequestBody FriendListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotFriendService.getRobotFriendListStr(params.getRobot_id())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    @RequestMapping(value = "/accept_new")
    public void acceptNewFriend(
            @RequestBody AcceptNewParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotFriendService.acceptNewFriendRequest(params.getRobot_id(), params.getSerial_no())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    @RequestMapping(value = "/send_message")
    public void sendMessage(
            @RequestBody SendFriendMessageParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotMessageService.sendPrivateMessage(params.getRobot_id(), params.getSerial_no(), params.getWx_id(), params.getMessage())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    @RequestMapping(value = "/send_message_list")
    public void sendMessageList(
            @RequestBody SendFriendMessageListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotMessageService.sendPrivateMessageList(params.getRobot_id(), params.getSerial_no(), params.getWx_id(), params.getMessage_list())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    @RequestMapping(value = "/delete")
    public void deleteFriend(
            @RequestBody DeleteContactParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotFriendService.deleteFriend(params.getRobot_id(), params.getContact_id())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }
}

class FriendListParams {
    String robot_id
}

class AcceptNewParams {
    String robot_id
    String serial_no
}

class DeleteContactParams {
    String robot_id
    String contact_id
}

class SendFriendMessageParams {
    String robot_id
    String serial_no
    String wx_id
    TSMessageBean message
}

class SendFriendMessageListParams {
    String robot_id
    String serial_no
    String wx_id
    List<TSMessageBean> message_list=new ArrayList<>()
}

