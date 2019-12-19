package com.waps.union_jd_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import com.waps.utils.TemplateUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 在线配置
 */
@Controller
@RequestMapping("/config")
class OnlineConfigController {

    /**
     * 读取在线配置
     * @param configName
     * @param uid
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/{configName}", method = RequestMethod.GET)
    public void getAppConfig(
            @PathVariable("configName") String configName,
            @RequestParam(value = "uid", required = false) String uid,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Map<String, String> params = new HashMap<>()
        String app_config = "config/" + configName + ".json"
        try {
            String str = new TemplateUtils().getFreeMarkerFromResource(app_config, params, "UTF-8")

            ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parse(str)))
        } catch (Exception e) {
            ResponseUtils.write(response, new ReturnMessageBean(500, "ERROR:" + e.getLocalizedMessage()).toString())
        }

    }

}
