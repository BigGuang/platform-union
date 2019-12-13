package com.waps.union_jd_api.controller

import com.waps.union_jd_api.bean.WeAppUserInfoBean
import com.waps.union_jd_api.service.MiniAppService
import com.waps.union_jd_api.service.UniformMessageParams
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/we_app")
class MiniAppMessageController {

    @Autowired
    MiniAppService miniAppService

    @RequestMapping("/send/uniform_msg")
    public String getAppUserInfo(@RequestBody UniformMessageParams uniformMessageParams,
                                 HttpServletResponse response) throws Exception {
        String json = miniAppService.sendUniformMessage(uniformMessageParams)
        ResponseUtils.write(response, json)
    }
}
