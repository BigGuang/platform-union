package com.waps.union_jd_api.controller;

import com.alibaba.fastjson.JSONObject;
import com.waps.union_jd_api.service.JDConvertLinkService;
import com.waps.union_jd_api.service.ResultBean;
import com.waps.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/link")
public class JdConvertController {

    @Autowired
    private JDConvertLinkService jdConvertLinkService;

    @RequestMapping(value = "/convert", method = RequestMethod.POST)
    public void getLink(
            @RequestBody Map<String, String> paramsMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String content = paramsMap.get("content");
        String pid = paramsMap.get("pid");

        System.out.println(content);
        System.out.println(pid);

        ResultBean resultBean = jdConvertLinkService.convertLink(content, pid);
        String json = JSONObject.toJSONString(resultBean);
        ResponseUtils.write(response, json);
    }


    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public void test(
            @RequestBody Map<String, String> paramsMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String content = paramsMap.get("content");
        String pid = paramsMap.get("pid");
        String url = paramsMap.get("url");

        System.out.println(content);
        System.out.println(pid);
        System.out.println(url);
        ResultBean resultBean = jdConvertLinkService.convertLink(content, pid);

        String _u=jdConvertLinkService.getCouponUrl(url);
        

        System.out.println(_u);

        String json = JSONObject.toJSONString(resultBean);
        ResponseUtils.write(response, json);
    }

}
