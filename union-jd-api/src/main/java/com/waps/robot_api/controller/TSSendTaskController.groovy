package com.waps.robot_api.controller

import com.waps.robot_api.service.TSSendTaskService
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/robot/task")
class TSSendTaskController {
    @Autowired
    TSSendTaskService tsSendTaskService

    @RequestMapping(value = "/send")
    public void getToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        tsSendTaskService.sendTask2Room()
        ResponseUtils.write(response, new ReturnMessageBean(200, ""))
    }

}
