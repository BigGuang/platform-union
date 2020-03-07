package com.waps.robot_api.controller

import com.waps.elastic.search.ESReturnList
import com.waps.elastic.search.utils.AggregationsUtils
import com.waps.elastic.search.utils.PageUtils
import com.waps.elastic.search.utils.SearchHitsUtils
import com.waps.service.jd.es.service.RobotSendLogESService
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.aggregations.Aggregations
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/robot/send_log")
class RobotSendLogController {
    @Autowired
    private RobotSendLogESService robotSendLogESService

    @RequestMapping(value = "/list")
    public void list(
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        PageUtils pageUtils = new PageUtils(page, size)
        HashMap params = pageUtils.getParamsMap()
        SearchHits hits = robotSendLogESService.findByFreeMarkerFromResource("es_script/robot_send_log_all.txt", params)
        ESReturnList esReturnList = SearchHitsUtils.getHits2ReturnMap(hits)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", esReturnList))
    }

    @RequestMapping(value = "/count_robot")
    public void groupCount(
            @RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "date_day", required = true) String date_day,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        HashMap params = new HashMap()
        params.put("type", type)
        params.put("day_start", date_day)
        params.put("day_end", date_day)
        Aggregations aggregations = robotSendLogESService.groupByFreeMarkerFromResource("es_script/robot_send_log_groupby_robot.txt", params)
        LinkedHashMap linkedHashMap = AggregationsUtils.getAggReturnMap(aggregations,"group_by")
        ResponseUtils.write(response, new ReturnMessageBean(200, "", linkedHashMap))
    }

    @RequestMapping(value = "/count_room")
    public void groupCountRoom(
            @RequestParam(value = "robot_id", required = false) String robot_id,
            @RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "date_day", required = true) String date_day,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        HashMap params = new HashMap()
        params.put("robot_id", robot_id)
        params.put("type", type)
        params.put("day_start", date_day)
        params.put("day_end", date_day)
        Aggregations aggregations = robotSendLogESService.groupByFreeMarkerFromResource("es_script/robot_send_log_groupby_room.txt", params)
        LinkedHashMap linkedHashMap = AggregationsUtils.getAggReturnMap(aggregations,"group_by")
        ResponseUtils.write(response, new ReturnMessageBean(200, "", linkedHashMap))
    }
}
