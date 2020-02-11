package com.waps.robot_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSMessageBean
import com.waps.robot_api.bean.request.TSPostGroupMessageBean
import com.waps.robot_api.bean.request.TSPostPrivateMessageBean
import com.waps.robot_api.utils.TSApiConfig
import com.waps.service.jd.es.domain.TSMessageESMap
import com.waps.service.jd.es.service.TSMessageESService
import com.waps.union_jd_api.utils.DateUtils
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TSRobotMessageService {

    @Autowired
    TSAuthService tsAuthService
    @Autowired
    TSMessageESService tsMessageESService

    /**
     * 发送私聊消息,一次单条
     * @param vcRobotSerialNo
     * @param vcSerialNo
     * @return
     */
    public String sendPrivateMessage(String vcRobotSerialNo, String vcRelaSerialNo, String vcToWxSerialNo, TSMessageBean messageBean) {
        String url = TSApiConfig.ROBOT_MESSAGE_SendPrivateChatMessages.replace("{TOKEN}", tsAuthService.getToken())
        TSPostPrivateMessageBean postMessageBean = new TSPostPrivateMessageBean()
        postMessageBean.setVcRobotSerialNo(vcRobotSerialNo)
        postMessageBean.setVcRelaSerialNo(vcRelaSerialNo)
        postMessageBean.setVcToWxSerialNo(vcToWxSerialNo)
        List<TSMessageBean> list = new ArrayList<>()
        messageBean.setnMsgNum(1)
        list.add(messageBean)
        postMessageBean.setData(list)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postMessageBean))
        return retJson
    }

    /**
     * 一次发送多条消息
     * @param vcRobotSerialNo
     * @param vcRelaSerialNo
     * @param vcToWxSerialNo
     * @param messageBeans
     * @return
     */
    public String sendPrivateMessageList(String vcRobotSerialNo, String vcRelaSerialNo, String vcToWxSerialNo, TSMessageBean... messageBeans) {
        String url = TSApiConfig.ROBOT_MESSAGE_SendPrivateChatMessages.replace("{TOKEN}", tsAuthService.getToken())
        TSPostPrivateMessageBean postMessageBean = new TSPostPrivateMessageBean()
        postMessageBean.setVcRobotSerialNo(vcRobotSerialNo)
        postMessageBean.setVcRelaSerialNo(vcRelaSerialNo)
        postMessageBean.setVcToWxSerialNo(vcToWxSerialNo)
        List<TSMessageBean> list = new ArrayList<>()
        int i = 1
        for (TSMessageBean messageBean : messageBeans) {
            messageBean.setnMsgNum(i)
            list.add(messageBean)
            i = i + 1
        }
        postMessageBean.setData(list)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postMessageBean))
        return retJson
    }

    /**
     * 发送群消息
     * @param vcRobotSerialNo
     * @param vcRelaSerialNo
     * @param vcToWxSerialNo
     * @param messageBean
     * @return
     */
    public sendChatRoomMessage(String vcRobotSerialNo, String vcRelaSerialNo, String vcToWxSerialNo, TSMessageBean messageBean) {
        String url = TSApiConfig.ROBOT_MESSAGE_SendGroupChatMessages.replace("{TOKEN}", tsAuthService.getToken())
        TSPostGroupMessageBean postMessageBean = new TSPostGroupMessageBean()
        postMessageBean.setVcRobotSerialNo(vcRobotSerialNo)
        postMessageBean.setVcRelaSerialNo(vcRelaSerialNo)
        postMessageBean.setnIsHit(1)
        if (!StringUtils.isNull(vcToWxSerialNo)) {
            postMessageBean.setVcToWxSerialNo(vcToWxSerialNo)
            if (vcToWxSerialNo.equals("@所有人")) {
                postMessageBean.setnIsHit(0)
            }
        }
        List<TSMessageBean> list = new ArrayList<>()
        messageBean.setnMsgNum(1)
        list.add(messageBean)
        postMessageBean.setData(list)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postMessageBean))
        return retJson
    }

    /**
     * 一次发送多条消息
     * @param vcRobotSerialNo
     * @param vcRelaSerialNo
     * @param vcToWxSerialNo
     * @param messageBeans
     * @return
     */
    public String sendChatRoomMessageList(String vcRobotSerialNo, String vcRelaSerialNo, String vcToWxSerialNo, TSMessageBean... messageBeans) {
        String url = TSApiConfig.ROBOT_MESSAGE_SendGroupChatMessages.replace("{TOKEN}", tsAuthService.getToken())
        TSPostGroupMessageBean postMessageBean = new TSPostGroupMessageBean()
        postMessageBean.setVcRobotSerialNo(vcRobotSerialNo)
        postMessageBean.setVcRelaSerialNo(vcRelaSerialNo)
        postMessageBean.setVcToWxSerialNo(vcToWxSerialNo)
        postMessageBean.setnIsHit(1)
        if (!StringUtils.isNull(vcToWxSerialNo)) {
            postMessageBean.setVcToWxSerialNo(vcToWxSerialNo)
            if (vcToWxSerialNo.equals("@所有人")) {
                postMessageBean.setnIsHit(0)
            }
        }
        List<TSMessageBean> list = new ArrayList<>()
        int i = 1
        for (TSMessageBean messageBean : messageBeans) {
            messageBean.setnMsgNum(i)
            list.add(messageBean)
            i = i + 1
        }
        postMessageBean.setData(list)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postMessageBean))
        return retJson
    }


    /**
     * 发送信息结果回调  5004
     * http://docs.op.opsdns.cc:8081/Personal-number-function/sendprivatemsg-callback/
     */
    public void callBackSendPrivateMessageResult(String strContext) {

    }

    /**
     * 接收私聊信息回调  5001
     * http://docs.op.opsdns.cc:8081/Personal-number-function/friend-privateinformation-callback/
     */
    public void callBackReceivePrivateMessage(String strContext) {
        if (strContext != null) {
            JSONObject jsonObject = JSONObject.parseObject(strContext)
            if (jsonObject != null && jsonObject.get("Data")) {
                TSMessageESMap tsMessageESMap = JSONObject.parseObject(jsonObject.get("Data").toString(), TSMessageESMap.class) as TSMessageESMap
                if (tsMessageESMap != null) {
                    tsMessageESMap.setId(tsMessageESMap.getVcMsgId())
                    tsMessageESMap.setnType(jsonObject.getInteger("nType"))
                    tsMessageESMap.setVcMerchantNo(jsonObject.getString("vcMerchantNo"))
                    tsMessageESMap.setVcRobotSerialNo(jsonObject.getString("vcRobotSerialNo"))
                    tsMessageESMap.setVcRobotWxId(jsonObject.getString("vcRobotWxId"))
                    tsMessageESMap.setVcSerialNo(jsonObject.getString("vcSerialNo"))
                    tsMessageESMap.setMsg_from(jsonObject.getInteger("nType") + "")
                    tsMessageESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
                    tsMessageESService.save(tsMessageESMap.getId(), tsMessageESMap)
                }
            }
        }
    }


    /**
     * 群聊消息发送结果回调接口  5002
     * http://docs.op.opsdns.cc:8081/groupmessage/sendgroupmsg-callback/
     */
    public callBackChatRoomSendMessageResult(String strContext) {

    }

    /**
     * 群内实时消息回调  5003
     * http://docs.op.opsdns.cc:8081/groupmessage/realtime-notify/
     */
    public void callBackChatRoomReceiveMessage(String strContext) {
        if (strContext != null) {
            JSONObject jsonObject = JSONObject.parseObject(strContext)
            if (jsonObject != null && jsonObject.get("Data")) {
                TSMessageESMap tsMessageESMap = JSONObject.parseObject(jsonObject.get("Data").toString(), TSMessageESMap.class) as TSMessageESMap
                if (tsMessageESMap != null) {
                    tsMessageESMap.setId(tsMessageESMap.getVcMsgId())
                    tsMessageESMap.setnType(jsonObject.getInteger("nType"))
                    tsMessageESMap.setVcMerchantNo(jsonObject.getString("vcMerchantNo"))
                    tsMessageESMap.setVcRobotSerialNo(jsonObject.getString("vcRobotSerialNo"))
                    tsMessageESMap.setVcRobotWxId(jsonObject.getString("vcRobotWxId"))
                    tsMessageESMap.setVcSerialNo(jsonObject.getString("vcSerialNo"))
                    tsMessageESMap.setMsg_from(jsonObject.getInteger("nType") + "")
                    tsMessageESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
                    tsMessageESService.save(tsMessageESMap.getId(), tsMessageESMap)
                }
            }
        }
    }
}
