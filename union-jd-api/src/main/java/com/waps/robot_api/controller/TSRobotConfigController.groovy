package com.waps.robot_api.controller

import com.waps.robot_api.bean.response.TSResponseRobotInfoBean
import com.waps.robot_api.service.TSRobotConfigService
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
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
            @RequestParam(value = "robot_id", required = true) String vcRobotSerialNo,
            @RequestParam(value = "isAuto", required = true) boolean isAuto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retStr = tsRobotConfigService.setAutoAddFriendSetup(vcRobotSerialNo, isAuto)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retStr))
    }

    @RequestMapping(value = "/autoJoinChatRoom")
    public void autoJoinChatRoom(
            @RequestParam(value = "robot_id", required = true) String vcRobotSerialNo,
            @RequestParam(value = "isAuto", required = true) boolean isAuto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retStr = tsRobotConfigService.setAutoJoinChatRoomSetup(vcRobotSerialNo, isAuto)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retStr))
    }

    @RequestMapping(value = "/set_profile_info")
    public void profile_info(
            @RequestParam(value = "robot_id", required = true) String vcRobotSerialNo,
            @RequestParam(value = "whatsup", required = false) String whats_up,
            @RequestParam(value = "nick_name", required = false) String nick_name,
            @RequestParam(value = "head_img", required = false) String head_img,
            @RequestParam(value = "sex", required = false) Integer sex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retStr = ""
        if (!StringUtils.isNull(whats_up)) {
            retStr = tsRobotConfigService.setProfileWhatsUp(vcRobotSerialNo, whats_up)
        }
        if (!StringUtils.isNull(nick_name)) {
            retStr = tsRobotConfigService.setProfileName(vcRobotSerialNo, nick_name)
        }
        if (!StringUtils.isNull(head_img)) {
            retStr = tsRobotConfigService.setProfileHeadImg(vcRobotSerialNo, head_img)
        }
        if (sex != null) {
            retStr = tsRobotConfigService.setProfileGender(vcRobotSerialNo, sex)
        }
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retStr))
    }

}
