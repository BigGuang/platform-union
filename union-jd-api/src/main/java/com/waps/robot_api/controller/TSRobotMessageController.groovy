package com.waps.robot_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.service.TSRobotMessageService
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/robot/message")
class TSRobotMessageController {
    @Autowired
    TSRobotMessageService tsRobotMessageService

    @RequestMapping(value = "/list")
    public void friendList(
            @RequestParam(value = "type", required = true) Integer type,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            @RequestBody HashMap<String, Object> params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        SearchHits hits = null;
        if (type == 1 || type == 5001) {
            hits = tsRobotMessageService.loadFriendMessage(params, page, size)
        } else {
            hits = tsRobotMessageService.loadChatRoomMessage(params, page, size)
        }
        Map retMap = new HashMap()
        long total = 0
        List<Map> _list = new ArrayList<>()
        if (hits != null) {
            total = hits.getTotalHits().value
            for (SearchHit hit : hits.getHits()) {
                _list.add(hit.getSourceAsMap())
            }
            retMap.put("list", _list)
        }
        retMap.put("total", total)
        retMap.put("list", _list)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retMap))
    }
}
