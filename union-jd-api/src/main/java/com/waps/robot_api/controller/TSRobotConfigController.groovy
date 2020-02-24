package com.waps.robot_api.controller

import com.waps.robot_api.bean.response.TSResponseRobotInfoBean
import com.waps.robot_api.service.TSRobotConfigService
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/robot/config")
class TSRobotConfigController {
    @Autowired
    TSRobotConfigService tsRobotConfigService

    @RequestMapping(value = "/autoAddFriend")
    public void autoAddFriend(
            @RequestBody ConfigAutoParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retStr = tsRobotConfigService.setAutoAddFriendSetup(params.getRobot_id(), params.getIsAuto())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retStr))
    }

    @RequestMapping(value = "/autoJoinChatRoom")
    public void autoJoinChatRoom(
            @RequestBody ConfigAutoParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retStr = tsRobotConfigService.setAutoJoinChatRoomSetup(params.getRobot_id(), params.getIsAuto())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retStr))
    }

    @RequestMapping(value = "/set_profile_info")
    public void profile_info(
            @RequestBody ConfigProfileParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retStr = ""
        if (!StringUtils.isNull(params.getWhats_up())) {
            retStr = tsRobotConfigService.setProfileWhatsUp(params.getRobot_id(), params.getWhats_up())
        }
        if (!StringUtils.isNull(params.getNick_name())) {
            retStr = tsRobotConfigService.setProfileName(params.getRobot_id(), params.getNick_name())
        }
        if (!StringUtils.isNull(params.getHead_img())) {
            retStr = tsRobotConfigService.setProfileHeadImg(params.getRobot_id(), params.getHead_img())
        }
        if (params.getSex() != null && params.getSex() > 0) {
            retStr = tsRobotConfigService.setProfileGender(params.getRobot_id(), params.getSex())
        }
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retStr))
    }

    @RequestMapping(value = "/room_send_status")
    public void room_send_status(
            @RequestBody ConfigRoomSendStatusParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retStr = tsRobotConfigService.setAutoJoinChatRoomSetup(params.getRobot_id(), params.getIsAuto())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retStr))
    }

    @RequestMapping(value = "/room_nick_name")
    public void room_nick_name(
            @RequestBody ConfigAutoParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retStr = tsRobotConfigService.setAutoJoinChatRoomSetup(params.getRobot_id(), params.getIsAuto())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retStr))
    }


}

class ConfigAutoParams {
    String robot_id
    boolean isAuto
}

class ConfigProfileParams {
    String robot_id
    String whats_up
    String nick_name
    String head_img
    Integer sex = -1
}
