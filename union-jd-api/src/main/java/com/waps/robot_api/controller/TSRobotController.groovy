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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/robot")
class TSRobotController {
    @Autowired
    private TSAuthService tuSeAuthService

    @Autowired
    private TSCallBackService tsCallBackService

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

    @RequestMapping(value = "/list")
    public void getRobotList(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        TSResponseRobotInfoBean responseRobotInfoBean=tsRobotConfigService.getRobotInfoListBean()
        ResponseUtils.write(response, new ReturnMessageBean(200, "", responseRobotInfoBean))
    }


    @RequestMapping(value = "/callback",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void callBack(
            @RequestParam(value = "nType", required = false) Integer nType,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String retString = "SUCCESS"
        if (nType != null) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(request.getInputStream()));
            String line = null;
            StringBuilder buffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            System.out.println(buffer.toString());
            String body = buffer.toString()

            TestRequest.outPrintRequest(request);

            Map<String, String> params = UrlUtil.parseBody(body)
            String strContext = params.get("strContext")
            String strSign = params.get("strSign")

            println "==save=="

            boolean flg = tsCallBackService.callBack(nType, strContext)
            if (!flg) {
                retString = "FALSE"
            }
        }
        ResponseUtils.write(response, retString)
    }
}

class PostParams {
    String strContext
    String strSign
}
