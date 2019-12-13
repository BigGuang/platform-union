package com.waps.union_jd_api.controller;

import com.alibaba.fastjson.JSONObject;
import com.waps.union_jd_api.service.SeleniumService;
import com.waps.service.jd.es.service.JDOrderESService;
import com.waps.utils.ResponseUtils;
import com.waps.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/channel")
public class JdChannelController {
    @Autowired
    private JDOrderESService jdOrderESService;

    @RequestMapping(value = "/get_sku")
    public void get_sku(
            @RequestParam(value = "url", required = true) String url,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String currentUrl = SeleniumService.localGetCurrentUrl(url);
        String json = getSkuIDInfo(currentUrl);
        ResponseUtils.write(response, json);
    }

    @RequestMapping(value = "/get_sku_server")
    public void get_sku_server(
            @RequestParam(value = "url", required = true) String url,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String currentUrl = SeleniumService.serverGetCurrentUrl(url);
        String json = getSkuIDInfo(currentUrl);
        ResponseUtils.write(response, json);
    }

    @RequestMapping(value = "/data_income")
    public void getGoodsPromotionInfo(
            @RequestParam(value = "chennel", required = true) String channel,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {


        ResponseUtils.write(response, "");
    }

    public String getSkuIDInfo(String currentUrl) {
        String host = "https://item.jd.com/";
        String skuID = "";
        if (!StringUtils.isNull(currentUrl)) {
            if (currentUrl.indexOf("sku=") > 0) {
                String _temp = currentUrl.substring(currentUrl.indexOf("sku=") + 4, currentUrl.length());
                if (_temp.indexOf("&") > 0) {
                    skuID = _temp.substring(0, _temp.indexOf("&"));
                } else {
                    skuID = _temp;
                }
            }
        }
        if (StringUtils.isNull(skuID) && currentUrl.startsWith(host)) {
            skuID = currentUrl.substring(currentUrl.indexOf(host) + host.length(), currentUrl.indexOf(".html"));
        }

        Map<String, String> map = new HashMap<>();
        map.put("sku", skuID);
        map.put("currentUrl", currentUrl);
        String json = JSONObject.toJSONString(map);
        return json;
    }
}
