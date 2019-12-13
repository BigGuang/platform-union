package com.waps.union_jd_api.controller;

import com.alibaba.fastjson.JSONObject;
import com.waps.union_jd_api.service.JDSelectSkuService;
import com.waps.union_jd_api.service.JDSkuRobotService;
import com.waps.utils.ResponseUtils;
import com.waps.utils.StringUtils;
import jd.union.open.goods.query.response.GoodsResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
@RequestMapping("/api")
public class JdSelectSkuController {

    @Autowired
    private JDSelectSkuService jdSelectSkuService;

    @Autowired
    private JDSkuRobotService jdSkuRobotService;

    @RequestMapping(value = "/top_list", method = RequestMethod.GET)
    public void top_list(
            @RequestParam(value = "start_day", required = true) String start_day,
            @RequestParam(value = "end_day", required = true) String end_day,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Long[] list = jdSelectSkuService.getSellTop(start_day, end_day);
        HashMap map = new HashMap();
        if (list != null) {

            ArrayList<GoodsResp> goodsRespArrayList = jdSelectSkuService.getSelectSkuInfo(list);
            map.put("data", goodsRespArrayList);
        }
        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);
    }


    @RequestMapping(value = "/sku_info_list")
    public void sku_info_list(
            @RequestParam(value = "sku_list", required = true) Long[] skuIdList,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        HashMap map = new HashMap();
        Long[] list = skuIdList;
        list = skuIdList;
        if (list != null) {

            ArrayList<GoodsResp> goodsRespArrayList = jdSelectSkuService.getSelectSkuInfo(list);
            map.put("data", goodsRespArrayList);
        }

        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);
    }
}
