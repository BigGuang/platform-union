package com.waps.robot_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.service.TSRobotChatRoomService
import com.waps.robot_api.service.TSRobotConfigService
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/robot/chat_room")
class TSRobotChatRoomController {
    @Autowired
    TSRobotConfigService tsRobotConfigService
    @Autowired
    TSRobotChatRoomService tsRobotChatRoomService

    @RequestMapping(value = "/list")
    public void chat_room_ist(
            @RequestParam(value = "robot_id", required = true) String vcRobotSerialNo,
            @RequestParam(value = "room_id", required = false) String vcChatRoomSerialNo,
            @RequestParam(value = "is_open", required = false) Integer isOpenMessage,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (isOpenMessage == null) {
            isOpenMessage = 0
        }
        String retJson = tsRobotChatRoomService.getChatRoomInfoList(vcRobotSerialNo, vcChatRoomSerialNo, isOpenMessage)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }
}
