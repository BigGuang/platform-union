package com.waps.robot_api.service

import com.waps.elastic.search.utils.PageUtils
import com.waps.elastic.search.utils.SearchHitsUtils
import com.waps.service.jd.es.domain.TSSendTaskESMap
import com.waps.service.jd.es.service.TSSendTaskESService
import com.waps.service.jd.es.service.TSSendTaskUserESService
import com.waps.utils.StringUtils
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.SimpleDateFormat

@Component
class TSSendTaskUserService {
    @Autowired
    private TSSendTaskUserESService tsSendTaskUserESService
    @Autowired
    private TSSendTaskService tsSendTaskService

    /**
     * 发送任务列表
     * @param task_status
     * @param page
     * @param size
     * @return
     */
    public SearchHits getSendTaskWaitingList(String send_day, String send_time, int page, int size) {
        HashMap params = new HashMap()
        PageUtils pageUtils = new PageUtils(page, size)
        params.put("from", pageUtils.getFrom())
        params.put("size", pageUtils.getSize())
        params.put("send_day", send_day)
        params.put("send_time", send_time)
        println params
        return tsSendTaskUserESService.findByFreeMarkerFromResource("es_script/ts_send_task_user_time.json", params)
    }

    public List<TSSendTaskESMap> loadSendTaskWaitingList(String send_day, String send_time) {
        SearchHits hits = getSendTaskWaitingList(send_day, send_time, 1, 40)
        List<TSSendTaskESMap> _list = new ArrayList<>()
        for (SearchHit hit : hits) {
            TSSendTaskESMap tsSendTaskESMap = tsSendTaskUserESService.getObjectFromJson(hit.getSourceAsString(), TSSendTaskESMap.class) as TSSendTaskESMap
            _list.add(tsSendTaskESMap)
        }
        return _list
    }

    /**
     * 通过状态获取发送列表
     * @param task_status
     * @param page
     * @param size
     * @return
     */
    public SearchHits getSendTaskUserListByStatus(String channel_name, String send_day, String task_status, int page, int size, String order_by, String order) {
        HashMap params = new HashMap()
        PageUtils pageUtils = new PageUtils(page, size)
        if (!StringUtils.isNull(channel_name)) {
            channel_name = channel_name.toLowerCase()
        }
        if (StringUtils.isNull(order)) {
            order = "asc"
        }
        params.put("from", pageUtils.getFrom())
        params.put("size", pageUtils.getSize())
        params.put("channel_name", channel_name)
        params.put("send_day", send_day)
        if (!StringUtils.isNull(task_status)) {
            if ("0" == task_status) {
                params.put("task_status", 0)
            } else if ("1" == task_status) {
                params.put("task_status", 1)
            }
        }
        params.put("order_by", order_by)
        params.put("order", order)
        if (StringUtils.isNull(send_day)) {
            Date currentTime = new Date()
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
            send_day = dayFormat.format(currentTime)
        }
        println params
        return tsSendTaskUserESService.findByFreeMarkerFromResource("es_script/ts_send_task_user_status.txt", params)
    }


    public void reOderByAuto(channel_name) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
        SearchHits hits = getSendTaskUserListByStatus(channel_name, null, 0, 1, 50, "send_time", "asc")
        List<String> idList = new ArrayList<>()
        if (hits != null) {
            for (SearchHit hit : hits) {
                idList.add(hit.getId())
            }
        }
        if (idList.size() > 0) {
            reOrderByIdList(idList)
        }
    }

    public void reOrderByIdList(List<String> idList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")
        Date nextDate = new Date()
        for (String id : idList) {
            String nodeDateStr = dateFormat.format(nextDate)
            nextDate = tsSendTaskService.getSendTaskNextDateTime(nodeDateStr)
            String send_day = dateFormat.format(nextDate)
            String send_time = timeFormat.format(nextDate)
            Map upParams = new HashMap()
            upParams.put("send_day", send_day)
            upParams.put("send_time", send_time)
            tsSendTaskUserESService.update(id, upParams)
        }

    }

    /**
     * 获取某一天的
     * @param send_day
     * @param page
     * @param size
     * @return
     */
    public SearchHits getSendTaskListBySendDay(String channel_name, String send_day, int page, int size, String order_by, String order) {
        HashMap params = new HashMap()
        PageUtils pageUtils = new PageUtils(page, size)
        params.put("from", pageUtils.getFrom())
        params.put("size", pageUtils.getSize())
        params.put("channel_name", channel_name)
        params.put("send_day", send_day)
        params.put("order_by", order_by)
        params.put("order", order)
        println params
        return tsSendTaskUserESService.findByFreeMarkerFromResource("es_script/ts_send_task_user_day.json", params)
    }


    public Date getSendTaskUserNextTime(String channel_name) {
        Date currentTime = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
        String send_day = dayFormat.format(currentTime)
        SearchHits searchHits = getSendTaskListBySendDay(channel_name, send_day, 1, 100, "send_time", "desc")
        println "===搜索 " + send_day + " 下一轮时间==="
        return tsSendTaskService.getSendTaskNextTime(send_day, searchHits)
    }
}
