package com.waps.robot_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.elastic.search.utils.PageUtils
import com.waps.robot_api.bean.request.TSMessageBean
import com.waps.robot_api.bean.request.TSPostChatRoomInfoBean
import com.waps.robot_api.bean.request.TSPostGroupMessageBean
import com.waps.robot_api.bean.request.TSPostPrivateMessageBean
import com.waps.robot_api.utils.TSApiConfig
import com.waps.security.Base64
import com.waps.service.jd.es.domain.TSMessageESMap
import com.waps.service.jd.es.service.TSMessageESService
import com.waps.union_jd_api.utils.DateUtils
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.sort.SortOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.Base64Utils

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
    public sendChatRoomMessage(String vcRobotSerialNo, String vcChatRoomSerialNo, String vcRelaSerialNo, String vcToWxSerialNo, TSMessageBean messageBean) {
        String url = TSApiConfig.ROBOT_MESSAGE_SendGroupChatMessages.replace("{TOKEN}", tsAuthService.getToken())
        TSPostGroupMessageBean postMessageBean = new TSPostGroupMessageBean()
        postMessageBean.setVcRobotSerialNo(vcRobotSerialNo)
        postMessageBean.setVcRelaSerialNo(vcRelaSerialNo)
        postMessageBean.setVcChatRoomSerialNo(vcChatRoomSerialNo)
        postMessageBean.setnIsHit(1)
        if (!StringUtils.isNull(vcToWxSerialNo)) {
            postMessageBean.setVcToWxSerialNo(vcToWxSerialNo)
            if (vcToWxSerialNo == "@所有人") {
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
    public String sendChatRoomMessageList(String vcRobotSerialNo, String vcChatRoomSerialNo, String vcRelaSerialNo, String vcToWxSerialNo, TSMessageBean... messageBeans) {
        String url = TSApiConfig.ROBOT_MESSAGE_SendGroupChatMessages.replace("{TOKEN}", tsAuthService.getToken())
        TSPostGroupMessageBean postMessageBean = new TSPostGroupMessageBean()
        postMessageBean.setVcRobotSerialNo(vcRobotSerialNo)
        postMessageBean.setVcRelaSerialNo(vcRelaSerialNo)
        postMessageBean.setVcToWxSerialNo(vcToWxSerialNo)
        postMessageBean.setVcChatRoomSerialNo(vcChatRoomSerialNo)
        postMessageBean.setnIsHit(1)
        if (!StringUtils.isNull(vcToWxSerialNo)) {
            postMessageBean.setVcToWxSerialNo(vcToWxSerialNo)
            if (vcToWxSerialNo == "@所有人") {
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
     * 设置群聊是否openMessage
     * @param vcRobotSerialNo
     * @param vcChatRoomSerialNo
     * @param open
     * @return
     */
    public String setChatRoomOpenMessage(String vcRobotSerialNo, String vcChatRoomSerialNo, boolean open) {
        String url = ""
        if (open) {
            url = TSApiConfig.ROBOT_CHATROOM_RobotChatRoomOpen.replace("{TOKEN}", tsAuthService.getToken())
        } else {
            url = TSApiConfig.ROBOT_CHATROOM_RobotChatRoomCancel.replace("{TOKEN}", tsAuthService.getToken())
        }
        TSPostChatRoomInfoBean postChatRoomInfoBean = new TSPostChatRoomInfoBean()
        postChatRoomInfoBean.setVcRobotSerialNo(vcRobotSerialNo)
        postChatRoomInfoBean.setVcChatRoomSerialNo(vcChatRoomSerialNo)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postChatRoomInfoBean))
        return retJson
    }

    /**
     * 将收到的消息context转成对象并保存,私聊和群聊通用
     * @param strContext
     */
    public void saveContext2MessageBean(String strContext) {
        try {
            if (strContext != null) {
                JSONObject jsonObject = JSONObject.parseObject(strContext)
                if (jsonObject != null && jsonObject.get("Data")) {
                    TSMessageESMap tsMessageESMap = JSONObject.parseObject(jsonObject.get("Data").toString(), TSMessageESMap.class) as TSMessageESMap
                    if (tsMessageESMap != null) {
                        int nType = jsonObject.getInteger("nType")
                        tsMessageESMap.setId(tsMessageESMap.getVcMsgId())
                        tsMessageESMap.setnType(jsonObject.getInteger("nType"))
                        tsMessageESMap.setVcMerchantNo(jsonObject.getString("vcMerchantNo"))
                        tsMessageESMap.setVcRobotSerialNo(jsonObject.getString("vcRobotSerialNo"))
                        tsMessageESMap.setVcRobotWxId(jsonObject.getString("vcRobotWxId"))
                        tsMessageESMap.setVcSerialNo(jsonObject.getString("vcSerialNo"))
                        tsMessageESMap.setMsg_from(jsonObject.getInteger("nType") + "")
                        tsMessageESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
//                        消息类型:
//                        2001 文字
//                        2002 图片
//                        2003 语音(只支持amr格式)
//                        2004 视频
//                        2005 链接
//                        2006 好友名片
//                        2010 文件
//                        2013 小程序
//                        2016 音乐
                        if (tsMessageESMap.getnMsgType() == 2001) {
                            if (!StringUtils.isNull(tsMessageESMap.getVcContent())) {
                                String content = tsMessageESMap.getVcContent()
                                println "==Test=="
                                println Base64.decode(content)
                                println Base64Utils.decode(content.getBytes())
                                try {
                                    tsMessageESMap.setContent(new String(Base64.decode(content), "UTF-8"))
                                } catch (Exception e) {
                                    println "Base64 ERROR:" + e.getLocalizedMessage()
                                    tsMessageESMap.setContent(tsMessageESMap.getVcContent())
                                }
                            }
                        }
                        if (!StringUtils.isNull(tsMessageESMap.getVcShareTitle())) {
                            String content = tsMessageESMap.getVcShareTitle()
                            tsMessageESMap.setVcShareTitle(new String(Base64.decode(content), "UTF-8"))
                        }
                        if (!StringUtils.isNull(tsMessageESMap.getVcShareDesc())) {
                            String content = tsMessageESMap.getVcShareDesc()
                            tsMessageESMap.setShareDesc(new String(Base64.decode(content), "UTF-8"))
                        }
                        IndexResponse indexResponse = tsMessageESService.save(tsMessageESMap.getId(), tsMessageESMap)
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }


    /**
     * 读取私聊消息列表
     * @param kvMap
     * @param page
     * @param size
     * @return
     */
    public SearchHits loadFriendMessage(HashMap<String, Object> kvMap, int page, int size) {
        PageUtils pageUtils = new PageUtils(page, size)
        kvMap.put("from", pageUtils.getFrom())
        kvMap.put("size", pageUtils.getSize())
        String script = "es_script/ts_message_friend.json"
        SearchHits hits = tsMessageESService.findByFreeMarkerFromResource(script, kvMap)
        return hits
    }

    /**
     * 读取聊天室消息列表
     * @param kvMap
     * @param page
     * @param size
     * @return
     */
    public SearchHits loadChatRoomMessage(HashMap<String, Object> kvMap, int page, int size) {
        PageUtils pageUtils = new PageUtils(page, size)
        kvMap.put("from", pageUtils.getFrom())
        kvMap.put("size", pageUtils.getSize())
        String script = "es_script/ts_message_room.json"
        SearchHits hits = tsMessageESService.findByFreeMarkerFromResource(script, kvMap)
        return hits
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
        saveContext2MessageBean(strContext)
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
        saveContext2MessageBean(strContext)
    }
}
