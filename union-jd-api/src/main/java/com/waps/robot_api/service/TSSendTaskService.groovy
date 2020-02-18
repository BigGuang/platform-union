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
import org.jsoup.helper.StringUtil
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
    private JDConvertLinkService jdConvertLinkService;

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
     * 发送任务中的信息转换成机器人需要的对象
     * @param taskESMap
     * @return
     */
    public List<TSMessageBean> convertTask2Message(String channel_name, TSSendTaskESMap taskESMap) {
//        消息类型
//        2001 文字
//        2002 图片
//        2003 语音(只支持amr格式)
//        2004 视频
//        2005 链接
//        2006 好友名片
//        2010 文件
//        2013 小程序
//        2016 音乐
        List<TSMessageBean> messageBeanList = new ArrayList<>()
        if (taskESMap != null) {
            if (!StringUtils.isNull(taskESMap.getImg_url())) {
                String imgUrl = taskESMap.getImg_url()
                if (imgUrl.indexOf(",") < 0) {
                    imgUrl = imgUrl + ","
                }
                String[] imgList = imgUrl.split(",")
                for (int i = 0; i < imgList.length; i++) {
                    String img = imgList[i]
                    if (!StringUtils.isNull(img)) {
                        TSMessageBean tsMessageESMap = new TSMessageBean()
                        tsMessageESMap.setnMsgType(2002)
                        tsMessageESMap.setMsgContent(img)
                        messageBeanList.add(tsMessageESMap)
                    }
                }
            }
            if (!StringUtils.isNull(taskESMap.getContent()) && StringUtils.isNull(taskESMap.getTitle()) && StringUtils.isNull(taskESMap.getDesc())) {
                TSMessageBean tsMessageESMap = new TSMessageBean()
                tsMessageESMap.setnMsgType(2001)

                println "==channel_name:"+channel_name
                if(!StringUtils.isNull(channel_name)){
                    //对taskESMap.getContent()内容做强制转链
                    ResultBean resultBean = jdConvertLinkService.convertLink(taskESMap.getContent(), channel_name, "true")
                    println "==转链结果=="
                    TestUtils.outPrint(resultBean)
                    taskESMap.setContent(resultBean.getContent())
                }
                tsMessageESMap.setMsgContent(taskESMap.getContent())
                messageBeanList.add(tsMessageESMap)
            }
            if (!StringUtils.isNull(taskESMap.getHref()) && !StringUtils.isNull(taskESMap.getTitle())) {
                TSMessageBean tsMessageESMap = new TSMessageBean()
                tsMessageESMap.setnMsgType(2005)
                tsMessageESMap.setMsgContent(taskESMap.getHref())
                tsMessageESMap.setVcHref(taskESMap.getHref())
                tsMessageESMap.setVcTitle(taskESMap.getTitle())
                tsMessageESMap.setVcDesc(taskESMap.getDesc())
                messageBeanList.add(tsMessageESMap)
            }
        }
        return messageBeanList
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
    public sendTask2Room() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")
        String send_day = dateFormat.format(new Date())
        String send_time = timeFormat.format(new Date())

        List<TSSendTaskESMap> taskList = loadSendTaskWaitingList(send_day, send_time)
        if (taskList.size() > 0) {
            println "===" + taskList.size() + " 条发送任务"
            for (TSSendTaskESMap tsSendTaskESMap : taskList) {
                int page = 1
                int size = 40
                RobotRoomListBean robotRoom = tsRobotRoomService.listRobotRoom(page, size)
                long total = robotRoom.getTotal()
                List<TSRoomInfoESMap> roomList = robotRoom.getList()
                println "===" + roomList.size() + " 个群聊发送"
                for (TSRoomInfoESMap roomInfoESMap : roomList) {

                    println "===判断给 " + roomInfoESMap.getVcName() + " 发送的内容"
                    boolean flg = checkSendStatus(tsSendTaskESMap, roomInfoESMap)
                    println "===判断结果:"+flg
                    if (flg) {
                        println "===给" + roomInfoESMap.getVcName() + " 发送内容"
                        println " room info:" + roomInfoESMap.getChannel_name() + " " + roomInfoESMap.getChannel_id() + " " + roomInfoESMap.getnUserCount() + "人"
                        //发送消息
                        String robot_id = roomInfoESMap.getVcRobotSerialNo()
                        String room_id = roomInfoESMap.getVcChatRoomSerialNo()
                        String channel_id = roomInfoESMap.getChannel_id()
                        String channel_name = roomInfoESMap.getChannel_name()
                        String action_id = UUID.randomUUID().toString()
                        List<TSMessageBean> messageList = convertTask2Message(channel_name, tsSendTaskESMap)
                        if (messageList != null && messageList.size() > 0) {
                            tsRobotMessageService.sendChatRoomMessageList(robot_id, room_id, action_id, "", messageList)
                        }
                    }
                }
            }
        } else {
            println "===无发送任务"
        }
    }


    public boolean checkSendStatus(TSSendTaskESMap tsSendTaskESMap, TSRoomInfoESMap roomInfoESMap) {
        boolean flg = true
        if (!StringUtils.isNull(tsSendTaskESMap.getBlack_channel_name())
                && !StringUtils.isNull(roomInfoESMap.getChannel_name())
                && tsSendTaskESMap.getBlack_channel_name().toLowerCase().indexOf(roomInfoESMap.getChannel_name().toLowerCase()) > -1) {
            flg = false
        }
        if (!StringUtils.isNull(tsSendTaskESMap.getTarget_channel_name())) {
            flg = false
        }
        if (!StringUtils.isNull(tsSendTaskESMap.getTarget_channel_name())
                && !StringUtils.isNull(roomInfoESMap.getChannel_name())
                && tsSendTaskESMap.getTarget_channel_name().toLowerCase().indexOf(roomInfoESMap.getChannel_name().toLowerCase()) > -1) {
            flg = true
        }
        return flg
    }
}
