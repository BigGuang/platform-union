package com.waps.union_jd_api.controller;


import com.alibaba.fastjson.JSONObject;
import com.waps.elastic.search.utils.PageUtils;
import com.waps.service.jd.es.service.JDOrderESService;
import com.waps.union_jd_api.service.JDOrderLogService;
import com.waps.utils.DateUtils;
import com.waps.utils.ResponseUtils;
import com.waps.utils.StringUtils;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/jd_order")
public class JdOrderLogController {

    @Autowired
    private JDOrderLogService jdOrderLogService;
    @Autowired
    private JDOrderESService jdOrderESService;

    @RequestMapping(value = "/sync_log_day", method = RequestMethod.GET)
    public void sync_log_day(
            @RequestParam(value = "day", required = true) String day,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        jdOrderLogService.getOrderLogByDay(day);
        ResponseUtils.write(response, "ok");
    }

    @RequestMapping(value = "/sync_log_from", method = RequestMethod.GET)
    public void sync_log_from(
            @RequestParam(value = "start_day", required = true) String start_day,
            @RequestParam(value = "end_day", required = true) String end_day,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse(start_day);
        Date date2 = formatter.parse(end_day);
        int flg = DateUtils.compareToByDay(date1, date2);
        //开始时间要比结束时间小,相同就抓取当天
        if (flg < 1) {

            long _num = DateUtils.getDifferDate(start_day, end_day);
            System.out.println(_num);

            for (int i = 0; i <= _num; i++) {
                Date _currentDate = DateUtils.addByDate(date1, i);
                String dayStr = formatter.format(_currentDate);
                System.out.println(dayStr);
                jdOrderLogService.getOrderLogByDay(dayStr);
            }
            ResponseUtils.write(response, "ok");

        } else {
            ResponseUtils.write(response, "error:开始时间要小于结束时间");
        }
    }


    @RequestMapping(value = "/log_list", method = RequestMethod.GET)
    public void log_list(
            @RequestParam(value = "pid", required = false) String pid,
            @RequestParam(value = "uid", required = false) String uid,
            @RequestParam(value = "isValid", required = false) String isValid,
            @RequestParam(value = "stime", required = false) String start,
            @RequestParam(value = "etime", required = false) String end,
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {


        HashMap<String, Object> params = new HashMap<>();
        params.put("pid", pid);
        params.put("uid", uid);

//        （-1：未知,2.无效-拆单,3.无效-取消,4.无效-京东帮帮主订单,5.无效-账号异常,6.无效-赠品类目不返佣,7.无效-校园订单,8.无效-企业订单,9.无效-团购订单,10.无效-开增值税专用发票订单,11.无效-乡村推广员下单,12.无效-自己推广自己下单,13.无效-违规订单,14.无效-来源与备案网址不符,15.待付款,16.已付款,17.已完成,18.已结算（5.9号不再支持结算状态回写展示））
        if (!StringUtils.isNull(isValid) && isValid.equals("0")) {
            params.put("validCode","\",-1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,\"");
        } else {
            params.put("isValid", true);
            params.put("validCode","16\",\"17");
        }

        if (StringUtils.isNull(sort)) {
            sort = "desc";
        }
        params.put("sort", sort);
        PageUtils pageUtils = new PageUtils(page, size);
        params.put("from", pageUtils.getFrom());
        params.put("size", pageUtils.getSize());

        if (!StringUtils.isNull(orderId)) {
            params.put("orderId", orderId);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat _formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = new Date();
            Date date2 = new Date();
            if (!StringUtils.isNull(start)) {
                date1 = formatter.parse(start);
            } else {
                date1 = formatter.parse(_formatter.format(new Date()) + " 00:00:00");
            }
            if (!StringUtils.isNull(end)) {
                date2 = formatter.parse(end);
            } else {
                date2 = formatter.parse(_formatter.format(new Date()) + " 23:59:59");
            }
            String start_time = formatter.format(date1);
            String end_time = formatter.format(date2);
            params.put("stime", start_time);
            params.put("etime", end_time);

        }

        SearchHits hits = jdOrderLogService.findChannelOrderListToHits(params);
        ArrayList<Map<String,Object>> list=new ArrayList<>();
        SearchHit[] searchHits=hits.getHits();
        for(int i=0;i<searchHits.length;i++){
            SearchHit hit=searchHits[i];
            Map<String,Object> map=hit.getSourceAsMap();
            list.add(map);
        }
        Map map = new HashMap();
        map.put("data", list);
        map.put("total", hits.getTotalHits().value);


        String json = JSONObject.toJSONString(map);

        ResponseUtils.write(response, json);
    }


    @RequestMapping(value = "/log_list_all", method = RequestMethod.GET)
    public void log_list_all(
            @RequestParam(value = "isValid", required = false) String isValid,
            @RequestParam(value = "stime", required = false) String start,
            @RequestParam(value = "etime", required = false) String end,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {


        HashMap<String, Object> params = new HashMap<>();

        if (!StringUtils.isNull(isValid) && isValid.equals("0")) {

        } else {
            params.put("isValid", true);
        }

        if (StringUtils.isNull(sort)) {
            sort = "desc";
        }
        params.put("sort", sort);

        PageUtils pageUtils = new PageUtils(page, size);
        params.put("from", pageUtils.getFrom());
        params.put("size", pageUtils.getSize());

        if (!StringUtils.isNull(search)) {
            params.put("search", search);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat _formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = new Date();
            Date date2 = new Date();
            if (!StringUtils.isNull(start)) {
                date1 = formatter.parse(start);
            } else {
                date1 = formatter.parse(_formatter.format(new Date()) + " 00:00:00");
            }
            if (!StringUtils.isNull(end)) {
                date2 = formatter.parse(end);
            } else {
                date2 = formatter.parse(_formatter.format(new Date()) + " 23:59:59");
            }
            String start_time = formatter.format(date1);
            String end_time = formatter.format(date2);

            params.put("stime", start_time);
            params.put("etime", end_time);

        }

        SearchHits hits = jdOrderLogService.findAllOrderListToHits(params);
        ArrayList<Map<String,Object>> list=new ArrayList<>();
        SearchHit[] searchHits=hits.getHits();
        for(int i=0;i<searchHits.length;i++){
            SearchHit hit=searchHits[i];
            Map<String,Object> map=hit.getSourceAsMap();
            list.add(map);
        }
        Map map = new HashMap();
        map.put("data", list);
        map.put("total", hits.getTotalHits().value);

        String json = JSONObject.toJSONString(map);

        ResponseUtils.write(response, json);
    }
}
