package com.waps.union_jd_api.controller

import com.waps.service.jd.api.bean.PromotionCodeParams
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.service.JtbApiService
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/jtb/api")
class JtbApiController {

    @Autowired
    private JtbApiService jtbApiService

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void getLink(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String _sessionID = jtbApiService.loginByAccount()
        String sessionID = jtbApiService.loginBySessionID(_sessionID)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", sessionID));
    }
}
