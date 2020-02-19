package com.waps.robot_api.controller

import com.waps.elastic.search.ESReturnList
import com.waps.elastic.search.utils.SearchHitsUtils
import com.waps.robot_api.service.TSSendTaskService
import com.waps.service.jd.es.domain.TSSendTaskESMap
import com.waps.service.jd.es.service.TSSendTaskESService
import com.waps.tools.security.MD5
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.utils.DateUtils
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/robot/task")
class TSSendTaskController {
    @Autowired
    TSSendTaskService tsSendTaskService
    @Autowired
    TSSendTaskESService tsSendTaskESService

    @RequestMapping(value = "/send")
    public void getToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        tsSendTaskService.sendTask2Room()
        ResponseUtils.write(response, new ReturnMessageBean(200, ""))
    }

    @RequestMapping(value = "/list")
    public void list(
            @RequestParam(value = "task_status", required = true) Integer task_status,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        SearchHits hits=tsSendTaskService.getSendTaskListByStatus(task_status,page,size)
        println hits.getTotalHits().value
        println hits.getHits().length
        ESReturnList esReturnList= SearchHitsUtils.getHits2ReturnMap(hits)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", esReturnList))
    }


    @RequestMapping(value = "/info")
    public void info(
            @RequestParam(value = "id", required = true) String id,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        TSSendTaskESMap tsSendTaskESMap = tsSendTaskESService.load(id, TSSendTaskESMap.class) as TSSendTaskESMap
        ResponseUtils.write(response, new ReturnMessageBean(200, "", tsSendTaskESMap))
    }

    @RequestMapping(value = "/delete")
    public void delete(
            @RequestParam(value = "id", required = true) String id,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        DeleteResponse deleteResponse = tsSendTaskESService.delete(id)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", deleteResponse))
    }

    @RequestMapping(value = "/save")
    public void save(
            @RequestBody TSSendTaskESMap tsSendTaskESMap,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (tsSendTaskESMap) {
            if(StringUtils.isNull(tsSendTaskESMap.getId())){
                tsSendTaskESMap.setId(new MD5().getMD5(UUID.randomUUID().toString()))
            }
            tsSendTaskESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
            tsSendTaskESService.save(tsSendTaskESMap.getId(), tsSendTaskESMap)
        }
        ResponseUtils.write(response, new ReturnMessageBean(200, ""))
    }
}
