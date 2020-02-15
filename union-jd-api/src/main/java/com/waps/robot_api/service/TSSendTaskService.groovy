package com.waps.robot_api.service

import com.waps.elastic.search.utils.PageUtils
import com.waps.service.jd.es.domain.TSMessageESMap
import com.waps.service.jd.es.domain.TSRoomInfoESMap
import com.waps.service.jd.es.domain.TSSendTaskESMap
import com.waps.service.jd.es.service.TSRobotESService
import com.waps.service.jd.es.service.TSRobotRoomInfoESService
import com.waps.service.jd.es.service.TSSendTaskESService
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TSSendTaskService {
    @Autowired
    TSSendTaskESService tsSendTaskESService
    @Autowired
    TSRobotMessageService tsRobotMessageService
    @Autowired
    TSRobotESService tsRobotESService
    @Autowired
    TSRobotRoomService tsRobotRoomService

    /**
     * 发送任务列表
     * @param task_status
     * @param page
     * @param size
     * @return
     */
    public SearchHits getSendTaskWaitingList(String send_day,String send_time, int page, int size) {
        HashMap params = new HashMap()
        PageUtils pageUtils = new PageUtils(page, size)
        params.put("from",pageUtils.getFrom())
        params.put("size",pageUtils.getSize())
        params.put("task_status",0)
        params.put("send_day",send_day)
        params.put("send_day",send_time)
        return tsSendTaskESService.findByFreeMarkerFromResource("es_script/ts_send_task_time.json",params)
    }

    public List<TSSendTaskESMap> loadSendTaskWaitingList(String send_day,String send_time){
        SearchHits hits=getSendTaskWaitingList(send_day,send_time,1,40)
        List<TSSendTaskESMap> _list=new ArrayList<>()
        for(SearchHit hit:hits){
            TSSendTaskESMap tsSendTaskESMap=tsSendTaskESService.getObjectFromJson(hit.getSourceAsString(),TSSendTaskESMap.class) as TSSendTaskESMap
            _list.add(tsSendTaskESMap)
        }
        return _list
    }

    public SearchHits getSendTaskListByStatus(int task_status, int page, int size) {
        HashMap params = new HashMap()
        PageUtils pageUtils = new PageUtils(page, size)
        params.put("from",pageUtils.getFrom())
        params.put("size",pageUtils.getSize())
        params.put("task_status",task_status)
        return tsSendTaskESService.findByFreeMarkerFromResource("es_script/ts_send_task_time.json",params)
    }

    /**
     * 发送任务中的信息转换成机器人需要的对象
     * @param taskESMap
     * @return
     */
    public TSMessageESMap convertTask2Message(TSSendTaskESMap taskESMap) {
        TSMessageESMap tsMessageESMap = new TSMessageESMap()
        return tsMessageESMap
    }


    /**
     * 执行发送任务
     * 步骤:
     * 1. 读取出可发送消息任务列表
     * 2. 任务列表不为空时，读取有机器人的群列表(机器人未被踢出)。
     * 3. 循环房间->循环任务列表，执行发送
     * 4. 发送前任务内容需要转链
     * 5. 发送要用多任务并发
     */
    public sendTask2Room(){
        String send_day=""
        String send_time=""

        List<TSSendTaskESMap> taskList=loadSendTaskWaitingList(send_day,send_time)
        if(taskList.size()>0) {
            int page=1
            int size=40
            RobotRoomListBean robotRoom= tsRobotRoomService.listRobotRoom(page,size)
            long total=robotRoom.getTotal()
            List<TSRoomInfoESMap> roomList=robotRoom.getList()
        }
    }
}
