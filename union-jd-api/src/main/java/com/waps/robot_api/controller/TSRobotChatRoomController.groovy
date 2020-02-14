package com.waps.robot_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSMessageBean
import com.waps.robot_api.service.TSRobotChatRoomService
import com.waps.robot_api.service.TSRobotMessageService
import com.waps.service.jd.es.domain.TSChatRoomESMap
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/robot/chat_room")
class TSRobotChatRoomController {
    @Autowired
    TSRobotChatRoomService tsRobotChatRoomService
    @Autowired
    TSRobotMessageService tsRobotMessageService

    /**
     * [同步] 群列表
     * @param params
     * @param request
     * @param response
     */
    @RequestMapping(value = "/list_live")
    public void chat_room_list_live(
            @RequestBody ListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (params.getIs_open() == null) {
            params.setIs_open(0)
        }
        String retJson = tsRobotChatRoomService.getChatRoomInfoList(params.getRobot_id(), params.getRoom_id(), params.getIs_open())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    /**
     * 群列表,从ES同步的信息中获取
     * @param params
     * @param request
     * @param response
     */
    @RequestMapping(value = "/list")
    public void chat_room_list_es(
            @RequestBody RoomListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        SearchHits hits = tsRobotChatRoomService.getChatRoomInfoListFromES(params.getRobot_id(), params.getPage(), params.getSize())
        Map retMap = new HashMap()
        long total = 0
        List<Map> _list = new ArrayList<>()
        if (hits != null) {
            total = hits.getTotalHits().value
            for (SearchHit hit : hits) {
                _list.add(hit.getSourceAsMap())
            }
        }
        retMap.put("total", total)
        retMap.put("list", _list)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retMap))
    }

    /**
     * [异步回调] 拉取群成员信息
     * @param params
     * @param request
     * @param response
     */
    @RequestMapping(value = "/pull_member_list")
    public void pull_member_list(
            @RequestBody ListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotChatRoomService.pullChatRoomMemberList(params.getRobot_id(), params.getRoom_id())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    /**
     * 从本地数据库中读取群成员信息
     * @param params
     * @param request
     * @param response
     */
    @RequestMapping(value = "/member_list")
    public void member_list(
            @RequestBody ListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        TSChatRoomESMap tsChatRoomESMap = tsRobotChatRoomService.loadChatRoomMemberList(params.getRobot_id(), params.getRoom_id())
        if(tsChatRoomESMap!=null && !StringUtils.isNull(tsChatRoomESMap.getId())) {
            ResponseUtils.write(response, new ReturnMessageBean(200, "", tsChatRoomESMap))
        }else{
            ResponseUtils.write(response, new ReturnMessageBean(404, "未找到群成员信息"))
        }
    }


    /**
     * [同步] 发送群消息
     * @param params
     * @param request
     * @param response
     */
    @RequestMapping(value = "/send_message")
    public void sendMessage(
            @RequestBody SendChatRoomMessageParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotMessageService.sendChatRoomMessage(params.getRobot_id(), params.getRoom_id(), params.getSerial_no(), params.getWx_id(), params.getMessage())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    @RequestMapping(value = "/send_message_list")
    public void sendMessageList(
            @RequestBody SendChatRoomMessageListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotMessageService.sendChatRoomMessageList(params.getRobot_id(), params.getRoom_id(), params.getSerial_no(), params.getWx_id(), params.getMessage_list())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }

    /**
     * [同步] 设置机器人在此群是否接收消息
     * @param params
     * @param request
     * @param response
     */
    @RequestMapping(value = "/set_open_message")
    public void setOpenMessage(
            @RequestBody OpenMessageParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String retJson = tsRobotMessageService.setChatRoomOpenMessage(params.getRobot_id(), params.getRoom_id(), params.getOpen())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", JSONObject.parseObject(retJson)))
    }
}

class OpenMessageParams {
    String robot_id
    String room_id
    boolean open
}

class ListParams {
    String robot_id
    String room_id
    Integer is_open
}

class RoomListParams{
    String robot_id
    int page
    int size
}

class SendChatRoomMessageParams {
    String robot_id
    String room_id
    String serial_no
    String wx_id
    int nIsHit
    TSMessageBean message
}

class SendChatRoomMessageListParams {
    String robot_id
    String room_id
    String serial_no
    String wx_id
    int nIsHit
    List<TSMessageBean> message_list=new ArrayList<>()
}

class PullMemberParams {
    String robot_id
    String room_id
}