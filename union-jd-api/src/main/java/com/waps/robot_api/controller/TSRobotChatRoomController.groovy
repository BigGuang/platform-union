package com.waps.robot_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSMessageBean
import com.waps.robot_api.service.TSRobotChatRoomService
import com.waps.robot_api.service.TSRobotConfigService
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
@RequestMapping("/ts/robot/chat_room")
class TSRobotChatRoomController {
    @Autowired
    TSRobotChatRoomService tsRobotChatRoomService
    @Autowired
    TSRobotMessageService tsRobotMessageService

    @RequestMapping(value = "/list")
    public void chat_room_ist(
            @RequestBody ListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (params.getIs_open() == null) {
            params.setIs_open(0)
        }
        String retJson = tsRobotChatRoomService.getChatRoomInfoList(params.getRobot_id(), params.getRoom_id(), params.getIs_open())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    @RequestMapping(value = "/pull_member_list")
    public void pull_member_list(
            @RequestBody ListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (params.getIs_open() == null) {
            params.setIs_open(0)
        }
        String retJson = tsRobotChatRoomService.getChatRoomInfoList(params.getRobot_id(), params.getRoom_id(), params.getIs_open())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }


    @RequestMapping(value = "/send_message")
    public void sendMessage(
            @RequestBody SendMessageParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotMessageService.sendChatRoomMessage(params.getRobot_id(), params.getSerial_no(), params.getWx_id(), params.getTsMessageBean())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }


}
class ListParams {
    String robot_id
    String room_id
    Integer is_open
}

class SendMessageParams {
    String robot_id
    String serial_no
    String wx_id
    TSMessageBean tsMessageBean
}

class PullMemberParams{
    String robot_id
    String room_id
}