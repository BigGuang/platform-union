package com.waps.robot_api.controller

import com.waps.elastic.search.ESReturnList
import com.waps.elastic.search.utils.SearchHitsUtils
import com.waps.robot_api.service.SendCount
import com.waps.robot_api.service.TSSendTaskUserService
import com.waps.robot_api.service.TimeLineService
import com.waps.service.jd.es.domain.TSSendTaskESMap
import com.waps.service.jd.es.service.TSSendTaskUserESService
import com.waps.tools.security.MD5
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.utils.DateUtils
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.search.SearchHits
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
    @Autowired
    private TimeLineService timeLineService

    @RequestMapping(value = "/list")
    public void list(
            @RequestParam(value = "channel_name", required = false) String channel_name,
            @RequestParam(value = "task_status", required = false) String task_status,
            @RequestParam(value = "send_day", required = false) String send_day,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            @RequestParam(value = "order", required = false) String order,

            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (StringUtils.isNull(order)) {
            order = "asc"
        }
        String order_by = "send_time"
        SearchHits hits = tsSendTaskUserService.getSendTaskUserListByStatus(channel_name, send_day, task_status, page, size, order_by, order)
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
        try {
            TSSendTaskESMap sendTaskESMap = tsSendTaskUserESService.load(id, TSSendTaskESMap.class) as TSSendTaskESMap
            String channel_name = null
            if (sendTaskESMap && sendTaskESMap.getTarget_channel_name()) {
                channel_name = sendTaskESMap.getTarget_channel_name().toLowerCase()
            }
            if (sendTaskESMap.getTask_status() == 0) {
                DeleteResponse deleteResponse = tsSendTaskUserESService.delete(id)
                long start = System.currentTimeMillis()
                if (sendTaskESMap && sendTaskESMap.getTask_status() == 0) {
                    Timer timer = new Timer();//实例化Timer类
                    timer.schedule(new TimerTask() {
                        public void run() {
                            System.out.println("==reOderByAuto:" + channel_name + "==")
                            println "end=" + System.currentTimeMillis() - start
                            tsSendTaskUserService.reOderByAuto(channel_name)

                            this.cancel()
                        }
                    }, 1000);//五百毫秒
                }
                ResponseUtils.write(response, new ReturnMessageBean(200, "", deleteResponse))
            } else {
                ResponseUtils.write(response, new ReturnMessageBean(400, "已发送内容不可删除"))
            }
        } catch (Exception e) {
            ResponseUtils.write(response, new ReturnMessageBean(500, e.getLocalizedMessage()))
        }
    }

    @RequestMapping(value = "/save")
    public void save(
            @RequestBody TSSendTaskESMap tsSendTaskESMap,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (tsSendTaskESMap && !StringUtils.isNull(tsSendTaskESMap.getTarget_channel_name())) {
            SendCount sendCount = tsSendTaskUserService.getSendCount(tsSendTaskESMap.getTarget_channel_name())
            if (sendCount.getToday_total() < sendCount.getDay_limit() || sendCount.getToday_limit() < 1) {
                boolean canSave = true
                if (StringUtils.isNull(tsSendTaskESMap.getId())) {
                    tsSendTaskESMap.setId(new MD5().getMD5(UUID.randomUUID().toString()))
                }
                if (StringUtils.isNull(tsSendTaskESMap.getSend_time()) || StringUtils.isNull(tsSendTaskESMap.getSend_day())) {
                    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")
                    String channel_name = tsSendTaskESMap.getTarget_channel_name()
                    if (!StringUtils.isNull(channel_name)) {
                        channel_name = channel_name.toLowerCase()
                        tsSendTaskESMap.setTarget_channel_name(tsSendTaskESMap.getTarget_channel_name().toLowerCase())
                    }
                    TSSendTaskESMap _oldSendTaskMap = tsSendTaskUserESService.load(tsSendTaskESMap.getId(), TSSendTaskESMap.class) as TSSendTaskESMap
                    if (_oldSendTaskMap != null && !StringUtils.isNull(_oldSendTaskMap.getId())) {
                        tsSendTaskESMap.setSend_day(_oldSendTaskMap.getSend_day())
                        tsSendTaskESMap.setSend_time(_oldSendTaskMap.getSend_time())
                    } else {
                        Date nextDate = tsSendTaskUserService.getSendTaskUserNextTime(channel_name)
                        if (nextDate != null) {
                            String send_day = dayFormat.format(nextDate)
                            String send_time = timeFormat.format(nextDate)
                            tsSendTaskESMap.setSend_day(send_day)
                            tsSendTaskESMap.setSend_time(send_time)
                        } else {
                            canSave = false
                        }
                    }
                }
                tsSendTaskESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
                canSave = tsSendTaskUserService.checkTaskMessage(tsSendTaskESMap)
                if (canSave) {
                    IndexResponse rep = tsSendTaskUserESService.save(tsSendTaskESMap.getId(), tsSendTaskESMap)
                    if (rep.getResult().toString() == "CREATED") {
                        sendCount.setToday_total(sendCount.getToday_total() + 1)
                        if (sendCount.getToday_limit() > 0) {
                            sendCount.setToday_limit(sendCount.getToday_limit() - 1)
                        }
                    }
                    HashMap retMap = new HashMap()
                    retMap.put("id", tsSendTaskESMap.getId())
                    retMap.put("today_total", sendCount.getToday_total())
                    retMap.put("today_limit", sendCount.getToday_limit())
                    retMap.put("day_limit", sendCount.getDay_limit())
                    ResponseUtils.write(response, new ReturnMessageBean(200, "", retMap))
                } else {
                    ResponseUtils.write(response, new ReturnMessageBean(400, "今日发送时间已过,或出现参数错误", sendCount))
                }
            } else {
                ResponseUtils.write(response, new ReturnMessageBean(400, "今日已到上限,请明日再提交", sendCount))
            }
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "必须有定向信息"))
        }

    }

    /**
     * 查询某一群的发送计数
     * @param channel_name
     * @param day
     * @param request
     * @param response
     */
    @RequestMapping(value = "/send_count")
    public void send_count(
            @RequestParam(value = "channel_name", required = true) String channel_name,
            @RequestParam(value = "day", required = false) String day,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        SendCount sendCount = tsSendTaskUserService.getSendCount(channel_name)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", sendCount))
    }

    @RequestMapping(value = "/task_next_time")
    public void task_next_time(
            @RequestParam(value = "channel_name", required = true) String channel_name,
            @RequestParam(value = "day", required = false) String day,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")
        Date nextDate = tsSendTaskUserService.getSendTaskUserNextTime(channel_name)
        String send_day = dayFormat.format(nextDate)
        String send_time = timeFormat.format(nextDate)
        Map params = new HashMap()
        params.put("send_day", send_day)
        params.put("send_time", send_time)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", params))
    }

    @RequestMapping(value = "/order")
    public void order(
            @RequestBody OrderParams orderParams,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (orderParams && orderParams.getId_list().size() > 0) {
            tsSendTaskUserService.reOrderByIdList(orderParams.getId_list())
        }
        ResponseUtils.write(response, new ReturnMessageBean(200, ""))
    }

}

class OrderParams {
    List<String> id_list = new ArrayList<>()
}
