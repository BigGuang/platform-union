package com.waps.robot_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSPostChatRoomInfoBean
import com.waps.robot_api.utils.TSApiConfig
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TSRobotChatRoomService {

    @Autowired
    TSAuthService tsAuthService

    /**
     * 获取群列表
     * @param vcRobotSerialNo
     * @param vcChatRoomSerialNo
     * @param isOpenMessage
     * @return
     */
    public String getChatRoomInfoList(String vcRobotSerialNo, String vcChatRoomSerialNo, int isOpenMessage) {
        String url = TSApiConfig.ROBOT_INFO_GetChatRoomList.replace("{TOKEN}", tsAuthService.getToken())
        TSPostChatRoomInfoBean postChatRoomInfoBean = new TSPostChatRoomInfoBean()
        postChatRoomInfoBean.setVcRobotSerialNo(vcRobotSerialNo)
        if (!StringUtils.isNull(vcChatRoomSerialNo)) {
            postChatRoomInfoBean.setVcChatRoomSerialNo(vcChatRoomSerialNo)
        }
        if (isOpenMessage == 10 || isOpenMessage == 11) {
            postChatRoomInfoBean.setIsOpenMessage(isOpenMessage)
        } else {
            postChatRoomInfoBean.setIsOpenMessage(0)
        }
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postChatRoomInfoBean))
        return retJson
    }

    /**
     * 群信息回调  4001
     * 群名称、群主转移（机器人被转移为群主）、群头像变化，当多个机器人在群内时，所有机器人群信息变动回调参照以下规则：
     * 1、非机器人群主把群主转移给为管理员的机器人时，每个机器人不论是否开群都会收到群信息变动回调（4001），每个机器人收到群管理员变动回调（4510，被设置为群主的机器人管理员取消）
     * 2、非机器人群主把群主转移给非管理员机器人时，每个机器人收到4001回调
     * 3、非机器人群主把群主转移给普通成员的管理员时，每个机器人都会收到群信息变动回调（4001） ，同时每个机器人会收到群管理员变动回调（4510，普通群成员的管理员被取消变成了群主）
     * 4、非机器人群主把群主转移给普通成员时，每个机器人会收到4001回调
     * 机器人群主转移群主时，无群信息回调（4001）
     * 5、商家扫码号第一次登录至平台时，登录初始化成功后，会将所有群的基本信息通过4001回调给商家；
     */
    public callBackChatRoomInfo(String strContext){

    }

    /**
     * 收到入群邀请回调 4506
     * 机器人收到入群邀请回调码：4506。收到此回调的条件：被邀请的群群成员人数超过40人，且收到邀请的机器人关闭了自动进入群聊设置。
     * http://docs.op.opsdns.cc:8081/Personal-number-function/RobotNewChatRoomAccept-callback/
     */
    public callBackJoinCharRoomRequest(String strContext) {

    }

    /**
     * 手动通过入群邀请 机器人通过好友群邀请接口
     * 商家可以通过该接口，选择通过好友邀请入群的请求。
     * 机器人当天被踢出群次数达到3次，同时当天限制调用本接口，且调用此接口返回提示：“今日被踢出群次数已达3次，为了您的账号安全，今日内将禁止调用本接口”
     */
    public joinChatRoom() {

    }

    /**
     * 被好友邀请入群回调 4505
     * 机器人入群方式为好友邀请时，接收本接口回调。
     * 当日被踢出群超过3次时，且设置了自动进入群聊，当机器人在以上条件下被邀请入群，商家会收到4505机器人入群回调失败，提示：“当日被踢出群超过3次，禁止进入群聊”
     * http://docs.op.opsdns.cc:8081/Personal-number-function/RobotIntoChatRoom-callback/
     */
    public callBackJoinChatRoom(String strContext) {

    }

    /**
     * 群成员信息列表回调接口 4501
     * 商家通过调用群成员信息列表接口之后，可以通过"群成员信息列表回调接口"直接返回给商家群成员信息。 群成员排序为随机排序。
     * http://docs.op.opsdns.cc:8081/groupmember/get-chatroom-members-callback/
     */
    public callBackChatRoomMemberInfo(String strContext){

    }

    /**
     * 新成员入群回调接口 4502
     * 群内有新人加入，通过该接口将新人入群信息回调给商家
     * http://docs.op.opsdns.cc:8081/groupmember/chatroom-members-join/
     */
    public callBackChatRoomAddMember(String strContext){

    }

    /**
     * 群成员退群回调接口 4503
     * 群内有成员退群，通过该接口将成员退群信息回调给商家
     * http://docs.op.opsdns.cc:8081/groupmember/chatroom-members-quit/
     */
    public callBackChatRoomDeleteMember(String strContext){

    }

}
