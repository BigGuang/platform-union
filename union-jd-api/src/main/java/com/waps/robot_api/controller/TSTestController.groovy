package com.waps.robot_api.controller

import com.waps.robot_api.service.SendService
import com.waps.robot_api.service.TSSendTaskService
import com.waps.service.jd.es.domain.TSRoomConfigESMap
import com.waps.service.jd.es.domain.TSSendTaskESMap
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/test")
class TSTestController {

    @Autowired
    SendService sendService
    @Autowired
    TSSendTaskService tsSendTaskService

    @RequestMapping(value = "/test_content")
    public void test_content(
            @RequestBody TestParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String newContent = sendService.makeContent2New(params.getContent())
        Map map = new HashMap()
        map.put("content", newContent)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", map))
    }

    @RequestMapping(value = "/send_check")
    public void send_check(
            @RequestBody TestSendParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        boolean flg = tsSendTaskService.checkSendStatus(params.getSendTaskESMap(), params.getRoomConfigESMap())
        println "flg=" + flg
        ResponseUtils.write(response, new ReturnMessageBean(200, "", flg))
    }
}

class TestParams {
    String content
}

class TestSendParams {
    TSSendTaskESMap sendTaskESMap
    TSRoomConfigESMap roomConfigESMap
}