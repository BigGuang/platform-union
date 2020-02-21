package com.waps.robot_api.service

import com.waps.elastic.search.utils.PageUtils
import com.waps.service.jd.es.domain.TSSendTaskESMap
import com.waps.service.jd.es.service.TSSendTaskESService
import com.waps.service.jd.es.service.TSSendTaskUserESService
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
    public SearchHits getSendTaskListByStatus(int task_status, int page, int size) {
        HashMap params = new HashMap()
        PageUtils pageUtils = new PageUtils(page, size)
        params.put("from", pageUtils.getFrom())
        params.put("size", pageUtils.getSize())
        params.put("task_status", task_status)
        println params
        return tsSendTaskUserESService.findByFreeMarkerFromResource("es_script/ts_send_task_status.json", params)
    }

    /**
     * 获取某一天的
     * @param send_day
     * @param page
     * @param size
     * @return
     */
    public SearchHits getSendTaskListBySendDay(String send_day, int page, int size, String order_by, String order) {
        HashMap params = new HashMap()
        PageUtils pageUtils = new PageUtils(page, size)
        params.put("from", pageUtils.getFrom())
        params.put("size", pageUtils.getSize())
        params.put("send_day", send_day)
        params.put("order_by", order_by)
        params.put("order", order)
        println params
        return tsSendTaskUserESService.findByFreeMarkerFromResource("es_script/ts_send_task_day.json", params)
    }



    public Date getSendTaskUserNextTime() {
        Date currentTime = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
        String send_day = dayFormat.format(currentTime)
        SearchHits searchHits = getSendTaskListBySendDay(send_day, 1, 100, "send_time", "desc")
        println "===搜索 " + send_day + " 下一轮时间==="
        return tsSendTaskService.getSendTaskNextTime(send_day,searchHits)
    }
}
