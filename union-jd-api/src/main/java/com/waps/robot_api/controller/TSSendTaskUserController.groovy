package com.waps.robot_api.controller

import com.waps.elastic.search.ESReturnList
import com.waps.elastic.search.utils.SearchHitsUtils
import com.waps.robot_api.service.TSSendTaskUserService
import com.waps.service.jd.es.domain.TSSendTaskESMap
import com.waps.service.jd.es.service.TSSendTaskUserESService
import com.waps.tools.security.MD5
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.utils.DateUtils
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.search.SearchHits
import org.jsoup.helper.StringUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.SimpleDateFormat

@Controller
@RequestMapping("/ts/robot/task_user")
class TSSendTaskUserController {

    @Autowired
    private TSSendTaskUserESService tsSendTaskUserESService
    @Autowired
    private TSSendTaskUserService tsSendTaskUserService

    @RequestMapping(value = "/list")
    public void list(
            @RequestParam(value = "task_status", required = true) Integer task_status,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        SearchHits hits = tsSendTaskUserService.getSendTaskListByStatus(task_status, page, size)
        println hits.getTotalHits().value
        println hits.getHits().length
        ESReturnList esReturnList = SearchHitsUtils.getHits2ReturnMap(hits)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", esReturnList))
    }



    @RequestMapping(value = "/info")
    public void info(
            @RequestParam(value = "id", required = true) String id,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        TSSendTaskESMap tsSendTaskESMap = TSSendTaskUserESService.load(id, TSSendTaskESMap.class) as TSSendTaskESMap
        ResponseUtils.write(response, new ReturnMessageBean(200, "", tsSendTaskESMap))
    }

    @RequestMapping(value = "/delete")
    public void delete(
            @RequestParam(value = "id", required = true) String id,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        DeleteResponse deleteResponse = TSSendTaskUserESService.delete(id)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", deleteResponse))
    }

    @RequestMapping(value = "/save")
    public void save(
            @RequestBody TSSendTaskESMap tsSendTaskESMap,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (tsSendTaskESMap && !StringUtils.isNull(tsSendTaskESMap.getTarget_channel_name())) {
            if (StringUtils.isNull(tsSendTaskESMap.getId())) {
                tsSendTaskESMap.setId(new MD5().getMD5(UUID.randomUUID().toString()))
            }
            if(StringUtils.isNull(tsSendTaskESMap.getSend_time()) || StringUtils.isNull(tsSendTaskESMap.getSend_day())) {
                SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")
                Date nextDate = tsSendTaskUserService.getSendTaskUserNextTime()
                String send_day = dayFormat.format(nextDate)
                String send_time = timeFormat.format(nextDate)
                tsSendTaskESMap.setSend_day(send_day)
                tsSendTaskESMap.setSend_time(send_time)
            }
            tsSendTaskESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
            tsSendTaskUserESService.save(tsSendTaskESMap.getId(), tsSendTaskESMap)
            ResponseUtils.write(response, new ReturnMessageBean(200, ""))
        }else{
            ResponseUtils.write(response, new ReturnMessageBean(500, "必须有定向信息"))
        }

    }
}
