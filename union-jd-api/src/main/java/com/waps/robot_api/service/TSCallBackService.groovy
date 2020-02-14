package com.waps.robot_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.es.domain.TSCallBackLogESMap
import com.waps.service.jd.es.service.TSCallBackLogESService
import com.waps.tools.test.TestUtils
import com.waps.union_jd_api.utils.DateUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.SimpleDateFormat

@Component
class TSCallBackService {

    @Autowired
    TSCallBackLogESService tsCallBackLogESService
    @Autowired
    TSRobotFriendService tsRobotFriendService
    @Autowired
    TSRobotChatRoomService tsChatRoomService
    @Autowired
    TSRobotConfigService tsRobotConfigService
    @Autowired
    TSRobotMessageService tsRobotMessageService

    public Boolean callBack(int nType, String strContext) {

        println "==strContext=="

        if (!StringUtils.isNull(strContext)) {
            strContext = URLDecoder.decode(strContext, "UTF-8")
        }
        saveCallBackLog2ES(strContext)
        saveCallBackLog(nType, strContext)

        //1006  修改机器人信息回调  http://docs.op.opsdns.cc:8081/Personal-number-msg/Personal-msg-callback/
        //3001  机器人登录后，回调给商家一次全量好友列表, 每24小时推送一次
        //3002  【直接回调】 主动添加好友结果回调接口（还未成为好友）
        //3003  【直接回调】 新好友请求回调接口  http://docs.op.opsdns.cc:8081/Personal-number-function/NewFriendRq-callback/
        //3004   删除联系人好友结果回调
        //3005  【直接回调】 机器人被加/主动加好友（成为好友）回调接口
        //3006  机器邀请好友入群回调接口
        //3008   设置好友备注接口回调
        //3009   设置好友黑名单接口回调
        //3011  【直接回调】 通过好友请求回调接口
        //3015   好友信息变动回调
        //4001  群信息变动回调, 商家扫码号第一次登录至平台时，登录初始化成功后，会将所有群的基本信息通过4001回调给商家；
        //4007   机器人主动退群回调接口
        //4009  设置群内昵称回调接口
        //4010   设置保存群至通讯录回调接口
        //4501  群成员信息列表回调接口
        //4502  新成员入群回调接口
        //4503  群成员退群回调接口
        //4504  扫码入群回调接口
        //4505   机器人入群回调接口, 机器人入群方式为好友邀请时，接收本接口回调
        //4506  【直接回调】 机器人收到入群邀请回调,收到此回调的条件：被邀请的群群成员人数超过40人，且收到邀请的机器人关闭了自动进入群聊设置。
        //4507  机器人被踢出群回调接口
        //4510  被设置为群主的机器人管理员取消
        //4511  撤销机器人邀请好友入群回调接口
        //5001  【直接回调】 机器人私聊信息回调（包含机器人发给好友的和好友发给机器人的）接口
        //5002  群聊消息发送结果回调接口
        //5003  群内实时消息回调
        //5004  私聊消息发送结果回调接口

        println "=处理nType:"+nType
        switch (nType) {
            case 1006:
                //1006  修改机器人信息回调  http://docs.op.opsdns.cc:8081/Personal-number-msg/Personal-msg-callback/
                tsRobotConfigService.callBackModifyInfo(strContext)
                break;
            case 3001:
                //3001  机器人登录后，回调给商家一次全量好友列表, 每24小时推送一次
                tsRobotFriendService.callBackAllFriendList(strContext)
                break;
            case 3002:
                //3002  【直接回调】 主动添加好友结果回调接口（还未成为好友）
                tsRobotFriendService.callBackPushAddFriendRequest(strContext)
                break;
            case 3003:
                //3003  【直接回调】 新好友请求回调接口  http://docs.op.opsdns.cc:8081/Personal-number-function/NewFriendRq-callback/
                tsRobotFriendService.callBackAddFriendRequest(strContext)
                break;
            case 3004:
                //3004   删除联系人好友结果回调
                tsRobotFriendService.callBackDeleteFriend(strContext)
                break;
            case 3005:
                //3005  【直接回调】 机器人被加/主动加好友（成为好友）回调接口
                tsRobotFriendService.callBackAddFriendSuccess(strContext)
                break;
            case 3006:
                //3006  机器邀请好友入群回调接口

                break;
            case 3008:
                //3008   设置好友备注接口回调

                break;
            case 3009:
                //3009   设置好友黑名单接口回调

                break;
            case 3011:
                //3011  【直接回调】 通过好友请求回调接口
                tsRobotFriendService.callBackAddFriendThrough(strContext)
                break;
            case 3015:
                //3015   好友信息变动回调

                break;
            case 4001:
                //4001  群信息变动回调, 商家扫码号第一次登录至平台时，登录初始化成功后，会将所有群的基本信息通过4001回调给商家；
                println "==开始处理=="
                tsChatRoomService.callBackChatRoomInfo(strContext)
                break;

            case 4002:
                //4002 修改群名称回调
                tsChatRoomService.callBackChatRoomNameChange(strContext)
                break;

            case 4007:
                //4007   机器人主动退群回调接口
                tsChatRoomService.callBackQuitChatRoom(strContext)

                break;
            case 4009:
                //4009  设置群内昵称回调接口

                break;
            case 4010:
                //4010   设置保存群至通讯录回调接口

                break;
            case 4501:
                //4501  群成员信息列表回调接口
                tsChatRoomService.callBackChatRoomMemberInfo(strContext)
                break;
            case 4502:
                //4502  新成员入群回调接口
                tsChatRoomService.callBackChatRoomAddMember(strContext)
                break;
            case 4503:
                //4503  群成员退群回调接口
                tsChatRoomService.callBackChatRoomDeleteMember(strContext)
                break;
            case 4504:
                //4504  扫码入群回调接口

                break;
            case 4505:
                //4505   机器人入群回调接口, 机器人入群方式为好友邀请时，接收本接口回调
                tsChatRoomService.callBackJoinChatRoom(strContext)
                break;
            case 4506:
                //4506  【直接回调】 机器人收到入群邀请回调,收到此回调的条件：被邀请的群群成员人数超过40人，且收到邀请的机器人关闭了自动进入群聊设置。
                tsChatRoomService.callBackJoinCharRoomRequest(strContext)
                break;
            case 4507:
                //4507  机器人被踢出群回调接口
                tsChatRoomService.callBackQuitChatRoom(strContext)
                break;
            case 4510:
                //4510  被设置为群主的机器人管理员取消

                break;
            case 4511:
                //4511  撤销机器人邀请好友入群回调接口

                break;
            case 5001:
                //5001  【直接回调】 机器人私聊信息回调（包含机器人发给好友的和好友发给机器人的）接口
                tsRobotMessageService.callBackReceivePrivateMessage(strContext)
                break;
            case 5002:
                //5002  群聊消息发送结果回调接口
                tsRobotMessageService.callBackChatRoomSendMessageResult(strContext)
                break;
            case 5003:
                //5003  群内实时消息回调
                tsRobotMessageService.callBackChatRoomReceiveMessage(strContext)
                break;
            case 5004:
                //5004  私聊消息发送结果回调接口
                tsRobotMessageService.callBackSendPrivateMessageResult(strContext)
                break;
        }
        return true
    }

    public void saveCallBackLog(int nType, String strContext) {
        try {
            Date nowTime = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
            String time = simpleDateFormat.format(nowTime)
            String path = "/home/robot_logs/" + time + "_" + nType + ".log"
            println "path:" + path
            if (!StringUtils.isNull(strContext)) {
                new File(path).write(strContext)
            }
            println "nType:" + nType
        } catch (Exception e) {
            println "saveCallBackLog ERROR:" + e.getLocalizedMessage()
        }
    }

    public void saveCallBackLog2ES(String strContext) {
        try {
            TSCallBackLogESMap tsCallBackLogESMap = JSONObject.parseObject(strContext, TSCallBackLogESMap.class) as TSCallBackLogESMap
            if (tsCallBackLogESMap != null) {
                tsCallBackLogESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
                String id = UUID.randomUUID().toString()
                tsCallBackLogESService.save(id, tsCallBackLogESMap)
            }
        } catch (Exception e) {
            println "saveCallBackLog2ES ERROR:" + e.getLocalizedMessage()
        }
    }
}
