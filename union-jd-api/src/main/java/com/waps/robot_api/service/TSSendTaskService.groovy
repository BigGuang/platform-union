package com.waps.robot_api.service

import com.waps.elastic.search.utils.PageUtils
import com.waps.robot_api.bean.request.TSMessageBean
import com.waps.service.jd.es.domain.TSMessageESMap
import com.waps.service.jd.es.domain.TSRoomInfoESMap
import com.waps.service.jd.es.domain.TSSendTaskESMap
import com.waps.service.jd.es.service.TSRobotESService
import com.waps.service.jd.es.service.TSRobotRoomInfoESService
import com.waps.service.jd.es.service.TSSendTaskESService
import com.waps.tools.test.TestUtils
import com.waps.union_jd_api.service.JDConvertLinkService
import com.waps.union_jd_api.service.ResultBean
import com.waps.utils.StringUtils
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.SimpleDateFormat

@Component
class TSSendTaskService {
    @Autowired
    private TSSendTaskESService tsSendTaskESService
    @Autowired
    private TSRobotMessageService tsRobotMessageService
    @Autowired
    private TSRobotESService tsRobotESService
    @Autowired
    private TSRobotRoomService tsRobotRoomService
    @Autowired
    private JDConvertLinkService jdConvertLinkService
    @Autowired
    private SendService sendService

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
        return tsSendTaskESService.findByFreeMarkerFromResource("es_script/ts_send_task_time.json", params)
    }

    public List<TSSendTaskESMap> loadSendTaskWaitingList(String send_day, String send_time) {
        SearchHits hits = getSendTaskWaitingList(send_day, send_time, 1, 40)
        List<TSSendTaskESMap> _list = new ArrayList<>()
        for (SearchHit hit : hits) {
            TSSendTaskESMap tsSendTaskESMap = tsSendTaskESService.getObjectFromJson(hit.getSourceAsString(), TSSendTaskESMap.class) as TSSendTaskESMap
            _list.add(tsSendTaskESMap)
        }
        return _list
    }

    public SearchHits getSendTaskListByStatus(int task_status, int page, int size) {
        HashMap params = new HashMap()
        PageUtils pageUtils = new PageUtils(page, size)
        params.put("from", pageUtils.getFrom())
        params.put("size", pageUtils.getSize())
        params.put("task_status", task_status)
        return tsSendTaskESService.findByFreeMarkerFromResource("es_script/ts_send_task_time.json", params)
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
    public sendTask2Room(String params) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")
        String send_day = dateFormat.format(new Date())
        String send_time = timeFormat.format(new Date())

        List<MessageTaskBean> sendList = new ArrayList<>()

        List<TSSendTaskESMap> taskList = loadSendTaskWaitingList(send_day, send_time)
        if (taskList.size() > 0) {
            println "===" + taskList.size() + " 条发送任务"
            for (TSSendTaskESMap sendTaskESMap : taskList) {
                int page = 1
                int size = 40
                RobotRoomListBean robotRoom = tsRobotRoomService.listRobotRoom(page, size)
                long total = robotRoom.getTotal()
                List<TSRoomInfoESMap> roomList = robotRoom.getList()
                println "===" + roomList.size() + " 个群聊发送"
                for (TSRoomInfoESMap roomInfoESMap : roomList) {

                    println "===判断给 " + roomInfoESMap.getVcName() + " 发送的内容"
                    boolean flg = checkSendStatus(sendTaskESMap, roomInfoESMap)
                    println "===判断结果:" + flg
                    if (flg) {
                        MessageTaskBean messageTaskBean = new MessageTaskBean()
                        messageTaskBean.setSendTaskESMap(sendTaskESMap)
                        messageTaskBean.setRoomInfoESMap(roomInfoESMap)
                        sendList.add(messageTaskBean)
                    }
                }
            }
            if (sendList.size() > 0) {
                sendService.setParams(params)
                sendService.sendTask(sendList)

            }
        } else {
            println "===无发送任务"
        }
    }


    public boolean checkSendStatus(TSSendTaskESMap tsSendTaskESMap, TSRoomInfoESMap roomInfoESMap) {
        boolean flg = true
        //机器人在群里
        if (roomInfoESMap.nRobotInStatus != 0) {
            flg = false
        }
        //对应群号得存在
        if (flg && StringUtils.isNull(roomInfoESMap.getChannel_name())) {
            flg = false
        }
        //判断是否在黑名单中
        if (flg && !StringUtils.isNull(tsSendTaskESMap.getBlack_channel_name())
                && tsSendTaskESMap.getBlack_channel_name().toLowerCase().indexOf(roomInfoESMap.getChannel_name().toLowerCase()) > -1) {
            flg = false
        }

        if (flg) {
            //有指定群时再做判断，无指定群将直接true
            if (!StringUtils.isNull(tsSendTaskESMap.getTarget_channel_name())) {
                if (tsSendTaskESMap.getTarget_channel_name().toLowerCase().indexOf(roomInfoESMap.getChannel_name().toLowerCase()) > -1) {
                    flg = true
                } else {
                    flg = false
                }
            } else {
                flg = true
            }
        }
        return flg
    }
}
