package com.waps.robot_api.service

import com.waps.elastic.search.utils.PageUtils
import com.waps.service.jd.es.domain.TSRoomConfigESMap
import com.waps.service.jd.es.domain.TSSendMessageESMap
import com.waps.service.jd.es.domain.TSSendTaskESMap
import com.waps.service.jd.es.service.TSRobotESService
import com.waps.service.jd.es.service.TSSendTaskESService
import com.waps.service.jd.es.service.TSSendTaskUserESService
import com.waps.union_jd_api.service.JDConvertLinkService
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
    private TSSendTaskUserService tsSendTaskUserService
    @Autowired
    private TSSendTaskUserESService tsSendTaskUserESService
    @Autowired
    private TSRobotMessageService tsRobotMessageService
    @Autowired
    private TSRobotESService tsRobotESService
    @Autowired
    private TSRoomConfigService tsRobotRoomService
    @Autowired
    private JDConvertLinkService jdConvertLinkService
    @Autowired
    private SendService sendService
    @Autowired
    private TimeLineService timeLineService

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
        return tsSendTaskESService.findByFreeMarkerFromResource("es_script/ts_send_task_status.json", params)
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
        return tsSendTaskESService.findByFreeMarkerFromResource("es_script/ts_send_task_day.json", params)
    }

    /**
     * 以当前时间计算出下一次发送时间
     * @return
     */
    public Date getSendTaskNextTime() {
        Date currentTime = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
        String send_day = dayFormat.format(currentTime)
        SearchHits searchHits = getSendTaskListBySendDay(send_day, 1, 100, "send_time", "desc")
        println "===搜索 " + send_day + " 下一轮时间==="
        return getSendTaskNextTime(send_day, searchHits)
    }


    /**
     * 通过定时任务记录，计算出队列中下一步的时间
     * @return
     */
    public Date getSendTaskNextTime(String send_day, SearchHits searchHits) {

        Date currentTime = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")

        String send_time = "08:00"
        String new_send_date = dateFormat.format(currentTime)
        //没有记录的情况下,从当前时间获取最新时间
        if (searchHits == null) {
            new_send_date = null
        } else {    //以最新一条的时间来获取时间节点
            SearchHit[] hit_list = searchHits.getHits()
            if (hit_list.length > 0) {
                TSSendTaskESMap tsSendTaskESMap = tsSendTaskESService.getObjectFromJson(hit_list[0].getSourceAsString(), TSSendTaskESMap.class) as TSSendTaskESMap
                String _last_send_time = tsSendTaskESMap.getSend_time()
                if (!StringUtils.isNull(_last_send_time)) {
                    new_send_date = send_day + " " + _last_send_time
                    Date lastTime=dateFormat.parse(new_send_date)
                    if(lastTime.before(currentTime)){
                        new_send_date=dateFormat.format(currentTime)
                    }
                    println "=最近一轮时间:" + new_send_date
                }
            }
        }

        Date nextDate = timeLineService.getSendTaskNextDateTime(new_send_date)
        return nextDate
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

        Map<String, MessageTaskBean> sendMap = new HashMap<>()

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")
        String send_day = dateFormat.format(new Date())
        String send_time = timeFormat.format(new Date())


        List<TSSendTaskESMap> allTaskList = loadSendTaskWaitingList(send_day, send_time)
        List<TSSendTaskESMap> userTaskList = tsSendTaskUserService.loadSendTaskWaitingList(send_day, send_time)

        if (allTaskList.size() == 0) {
            allTaskList = new ArrayList<>()
        }
        //将两个发送定时合并
        allTaskList.addAll(userTaskList)
        if (allTaskList.size() > 0) {
            println "===" + allTaskList.size() + " 条发送任务"
            for (TSSendTaskESMap sendTaskESMap : allTaskList) {
                int page = 1
                int size = 40
                RoomConfigListBean robotRoom = tsRobotRoomService.listRobotRoom(page, size)
                List<TSRoomConfigESMap> roomList = robotRoom.getList()
                println "===" + roomList.size() + " 个群聊发送"
                for (TSRoomConfigESMap roomInfoESMap : roomList) {
                    if (!StringUtils.isNull(roomInfoESMap.getSend_status()) && "open" == roomInfoESMap.getSend_status()) {
                        boolean flg = checkSendStatus(sendTaskESMap, roomInfoESMap)
                        //如果有群主提交的文案，优先发群主提交的文案
                        if (flg) {
                            MessageTaskBean messageTaskBean = new MessageTaskBean()
                            messageTaskBean.setSendTaskESMap(sendTaskESMap)
                            messageTaskBean.setRoomConfigESMap(roomInfoESMap)
//                            println "@@@@@@@@@@  发送 " + roomInfoESMap.vcName + "@@@@@@@@@@"
//                            for (TSSendMessageESMap messageESMap : messageTaskBean.getSendTaskESMap().getMessage_list()) {
//                                println messageESMap.getMsgContent()
//                            }
                            sendMap.put(roomInfoESMap.getVcChatRoomSerialNo(), messageTaskBean)
                        }
                    } else {
//                        println roomInfoESMap.getVcName() + " send_status:" + roomInfoESMap.getSend_status() + " 已关闭发送"
                    }
                }
            }
            println "sendMap.size="+sendMap.size()
            if (sendMap.size() > 0) {
                List<MessageTaskBean> _sendList = new ArrayList<>()
                Iterator it = sendMap.keySet().iterator()
                while (it.hasNext()) {
                    String room_id = it.next()
                    MessageTaskBean messageTaskBean = sendMap.get(room_id)
                    _sendList.add(messageTaskBean)
                }
                sendService.setParams(params)
                testSendList(_sendList)
                sendService.sendTask(_sendList)
            }
        } else {
            println "===无发送任务"
        }
    }


    public void testSendList(List<MessageTaskBean> _sendList){
        println "_sendList:"+_sendList.size()
        for(MessageTaskBean messageTaskBean:_sendList){
            println "===发送群:"+messageTaskBean.getRoomConfigESMap().getVcName()+"  "+messageTaskBean.getRoomConfigESMap().getChannel_name()
            println "===发送内容==="
            TSSendTaskESMap sendTaskESMap=messageTaskBean.getSendTaskESMap()
            if(sendTaskESMap!=null) {
                tsSendTaskUserESService.update(sendTaskESMap.getId(),"task_status",1)
                List<TSSendMessageESMap> _list = sendTaskESMap.getMessage_list()
                for (TSSendMessageESMap messageESMap : _list) {
                    println messageESMap.getnMsgType()
                    println messageESMap.getMsgContent()
                    println "----------"
                }
            }
        }
    }


    public sendTaskUser2Room(String params) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")
        String send_day = dateFormat.format(new Date())
        String send_time = timeFormat.format(new Date())

        List<MessageTaskBean> sendList = new ArrayList<>()
        List<TSSendTaskESMap> userTaskList = tsSendTaskUserService.loadSendTaskWaitingList(send_day, send_time)
        if (userTaskList.size() > 0) {
            for (TSSendTaskESMap sendTaskESMap : userTaskList) {
                int page = 1
                int size = 40
                RoomConfigListBean robotRoom = tsRobotRoomService.listRobotRoom(page, size)
                List<TSRoomConfigESMap> roomList = robotRoom.getList()
                for (TSRoomConfigESMap roomInfoESMap : roomList) {

                }
            }
        }

        if (sendList.size() > 0) {
            sendService.setParams(params)
            sendService.sendTask(sendList)
        }
    }


    public boolean checkSendStatus(TSSendTaskESMap tsSendTaskESMap, TSRoomConfigESMap roomInfoESMap) {
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
