package com.waps.union_jd_api.controller

import com.waps.union_jd_api.service.HemaService
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/hema")
class HemaUserController {

    @Autowired
    private HemaService hemaService

    @RequestMapping(value = "/user_tree")
    public void getGoodsPromotionInfo(
            @RequestParam(value = "sync", required = false) String sync,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        hemaService.startSyncHemaUserTree()
        ResponseUtils.write(response, "ok")
    }
}
