package com.waps.union_jd_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.es.domain.JDSkuInfoESMap
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.service.RecommendService
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/recommend")
class RecommendController {
    @Autowired
    private RecommendService recommendService

    @RequestMapping(value = "/editor")
    public void count(
            @RequestParam(value = "sTime", required = true) String sTime,
            @RequestParam(value = "eTime", required = true) String eTime,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<JDSkuInfoESMap> list = recommendService.editorRecommend(sTime, eTime, page, size)
        if (list) {
            ResponseUtils.write(response, new ReturnMessageBean(200, "", list));
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "ERROR"));
        }
    }
}
