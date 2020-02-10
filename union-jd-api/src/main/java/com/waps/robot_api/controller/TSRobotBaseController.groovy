package com.waps.robot_api.controller

import com.waps.robot_api.bean.response.TSResponseRobotInfoBean
import com.waps.robot_api.service.TSAuthService
import com.waps.robot_api.service.TSCallBackService
import com.waps.robot_api.service.TSRobotConfigService
import com.waps.robot_api.utils.TestRequest
import com.waps.robot_api.utils.UrlUtil
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/robot/base")
class TSRobotBaseController {
    @Autowired
    private TSAuthService tuSeAuthService
    @Autowired
    private TSRobotConfigService tsRobotConfigService

    @RequestMapping(value = "/token")
    public void getToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String token = tuSeAuthService.getToken()
        ResponseUtils.write(response, new ReturnMessageBean(200, "", token))
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public void getRobotList(
            @RequestBody RobotListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        println "/ts/robot/base/list"
        println params.getRobot_ids()
        if (params.getPage() < 1) {
            params.setPage(1)
        }
        TSResponseRobotInfoBean responseRobotInfoBean = tsRobotConfigService.getRobotInfoListBean(params.getRobot_ids(), params.getPage())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", responseRobotInfoBean))
    }

}

class PostParams {
    String strContext
    String strSign
}

class RobotListParams {
    String[] robot_ids
    int page = 1
}
