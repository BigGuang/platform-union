package com.waps.robot_api.service

import com.waps.elastic.search.ESReturnList
import com.waps.elastic.search.utils.PageUtils
import com.waps.elastic.search.utils.SearchHitsUtils
import com.waps.service.jd.es.domain.TSSendMessageESMap
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
    @Autowired
    private TimeLineService timeLineService


    public boolean checkTaskMessage(TSSendTaskESMap sendTaskESMap){
        boolean flg=true
        if(sendTaskESMap!=null){
            if(sendTaskESMap.getMessage_list().size()>0){
                for(TSSendMessageESMap messageESMap:sendTaskESMap.getMessage_list()){
                    if(messageESMap.getnMsgType()==2001 && StringUtils.isNull(messageESMap.getMsgContent())){
                        return false
                    }
                    if(messageESMap.getnMsgType()==2002 && StringUtils.isNull(messageESMap.getMsgContent())){
                        return false
                    }
                }
            }else{
                return false
            }
        }else{
            return false
        }
        return flg
    }

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
        params.put("send_day", send_day)
        println params
        return tsSendTaskUserESService.findByFreeMarkerFromResource("es_script/ts_send_task_user_status.txt", params)
    }


    /**
     * 自动重新排序
     * @param channel_name
     */
    public void reOderByAuto(channel_name) {
        //读取出当天还未发送的
        SearchHits hits = getSendTaskUserListByStatus(channel_name, null, "0", 1, 50, "send_time", "asc")
        List<String> idList = new ArrayList<>()
        if (hits != null) {
            println "===重新排序==="
            for (SearchHit hit : hits) {
                String id=hit.getId()
                println id
                idList.add(hit.getId())
            }
        }
        if (idList.size() > 0) {
            reOrderByIdList(idList)
        }
    }

    /**
     *
     * @param idList
     */
    public void reOrderByIdList(List<String> idList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")
        Date nextDate = new Date()
        println "==新排序=="
        for (String id : idList) {
            String nodeDateStr = dateFormat.format(nextDate)
            nextDate = timeLineService.getSendTaskNextDateTime(nodeDateStr)
            String send_day = dayFormat.format(nextDate)
            String send_time = timeFormat.format(nextDate)
            Map upParams = new HashMap()
            upParams.put("send_day", send_day)
            upParams.put("send_time", send_time)
            println id+"   "+send_day+" "+send_time
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

    /**
     * 计算当日发送数据
     * @param channel_name
     * @return
     */
    public SendCount getSendCount(String channel_name) {
        SendCount sendCount = new SendCount()
        int limitNum = sendCount.getDay_limit()
        SearchHits hits = getSendTaskUserListByStatus(channel_name, null, "3", 1, 100, "send_time", "desc")
        ESReturnList esReturnList = SearchHitsUtils.getHits2ReturnMap(hits)
        println "==ESTotal:"+esReturnList.getTotal()
        if (esReturnList.total > 0) {
            Map map = esReturnList.getList().get(0)
            String send_day = map.get("send_day")
            String send_time = map.get("send_time")
            String sendDate = send_day + " " + send_time
            println sendDate
            limitNum = timeLineService.getUsableTimeNumber(sendDate)
        } else {
            limitNum = timeLineService.getUsableTimeNumber()
        }
        sendCount.setToday_limit(limitNum)
        sendCount.setToday_total(Integer.parseInt(esReturnList.getTotal() + ""))
        return sendCount
    }
}

class SendCount {
    int today_total
    int today_limit
    int day_limit = 50
}
