package com.waps.union_jd_api.controller;

import com.alibaba.fastjson.JSONObject;
import com.waps.service.jd.es.domain.JDCategoryESMap;
import com.waps.union_jd_api.service.JDCategoryService;
import com.waps.union_jd_api.service.JDEventBean;
import com.waps.union_jd_api.service.JDEventService;
import com.waps.utils.ResponseUtils;
import com.waps.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class JdCategoryController {

    @Autowired
    JDCategoryService jdCategoryService;

    @Autowired
    JDEventService jdEventService;

    @RequestMapping(value = "/category_list")
    public void getGoodsPromotionInfo(
            @RequestParam(value = "type", required = false) String type,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

//
//        List<JDCategoryESMap> list = jdCategoryService.loadCategoryList(type);
//        Map<String, List> jsonMap = new HashMap<>();
//        jsonMap.put("data", list);
//
//        String json = JSONObject.toJSONString(jsonMap);
        String config_json = "api/category_type_home.json";
        File typeJson = new File(this.getClass().getClassLoader().getResource(config_json).getFile());
        String json = StringUtils.getFileTxt(typeJson.getPath());
        ResponseUtils.write(response, json);
    }


    /**
     * 京东活动
     * @param id
     * @param channel_name
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/events")
    public void eventList(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "channel_name", required = false) String channel_name,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        HashMap jsonMap = new HashMap();
        if (!StringUtils.isNull(id) && !StringUtils.isNull(channel_name)) {
            JDEventBean jdEventBean = jdEventService.getEvent2Url(id, channel_name);
            if (jdEventBean != null) {
                jsonMap.put("data", jdEventBean);
            }
        } else if (!StringUtils.isNull(id)) {
            JDEventBean jdEventBean = jdEventService.getEventInfo(id);
            if (jdEventBean != null) {
                jsonMap.put("data", jdEventBean);
            }
        } else {
            List<JDEventBean> jdEventBeanList = jdEventService.getEventList();
            if (jdEventBeanList != null) {
                jsonMap.put("data", jdEventBeanList);
            }
        }
        String json = JSONObject.toJSONString(jsonMap);
        ResponseUtils.write(response, json);
    }
}
