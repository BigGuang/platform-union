package com.waps.robot_api.service

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.elastic.search.utils.PageUtils
import com.waps.robot_api.bean.request.TSPostChatRoomInfoBean
import com.waps.robot_api.utils.TSApiConfig
import com.waps.service.jd.es.domain.JDMediaInfoESMap
import com.waps.service.jd.es.domain.TSChatRoomESMap
import com.waps.service.jd.es.domain.TSChatRoomMemberESMap
import com.waps.service.jd.es.domain.TSRoomInfoESMap
import com.waps.service.jd.es.service.TSChatRoomESService
import com.waps.service.jd.es.service.TSRobotRoomInfoESService
import com.waps.tools.security.MD5
import com.waps.tools.test.TestUtils
import com.waps.union_jd_api.service.WeChatRobotService
import com.waps.union_jd_api.utils.DateUtils
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import com.waps.utils.XmlUtils
import groovy.json.JsonSlurper
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TSRobotChatRoomService {

    @Autowired
    TSAuthService tsAuthService
    @Autowired
    TSChatRoomESService tsChatRoomESService
    @Autowired
    TSRobotRoomInfoESService tsRobotRoomInfoESService
    @Autowired
    WeChatRobotService weChatRobotService


    /**
     * 转能保存对象，并添加必要信息
     * @param robot_id
     * @param obj
     * @return
     */
    public TSRoomInfoESMap convertRoomJson2Obj(String robot_id, JSONObject obj) {
        TSRoomInfoESMap room = obj.toJavaObject(TSRoomInfoESMap.class)
        room.setVcRobotSerialNo(robot_id)
        String id = new MD5().getMD5(robot_id + room.getVcChatRoomSerialNo())
        room.setRoom_status("0")
        if (room.getVcName() != null) {
            String channel_name = weChatRobotService.getChannelName(room.getVcName())
            JDMediaInfoESMap jdMediaInfoESMap = weChatRobotService.getPidFromChannelName(channel_name)
            room.setChannel_name(channel_name)
            if (jdMediaInfoESMap != null) {
                room.setChannel_id(jdMediaInfoESMap.getChannel_id())
            }
        }
        room.setId(id)
        room.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
        println "======"
        TestUtils.outPrint(room)
        return room
    }

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
     * 从ES中读取定时同步的群信息
     * @param vcRobotSerialNo
     * @param page
     * @param size
     * @return
     */
    public SearchHits getChatRoomInfoListFromES(String vcRobotSerialNo, int page, int size) {
        PageUtils pageUtils = new PageUtils(page, size)
        HashMap params = new HashMap()
        params.put("from", pageUtils.getFrom())
        params.put("size", pageUtils.getSize())
        params.put("robot_id", vcRobotSerialNo)
        SearchHits hits = tsRobotRoomInfoESService.findByFreeMarkerFromResource("es_script/ts_robot_room_list.json", params)
        return hits
    }

    /**
     *  拉取
     * 【异步调用】获取群成员信息列表接口
     * 可通过接口主动获取机器人所在群成员信息列表（用户编号，昵称，头像）。
     * 调用限制：
     * 机器人维度，每分钟每个机器人最多获取3次群成员信息；每小时每个机器人最多获取20次群成员，每小时（例如08:00-09:00）；每日00：00-06:00禁止调用该接口；
     * 超出限制提示语：“每分钟每个机器人最多调用3次该接口” ； “每小时每个机器人最多调用该接口20次” “每日00：00-06:00禁止调用该接口”
     * 群维度，每分钟每个群最多被查询1次群成员信息；每小时每个群最多被查询5次群成员；
     * 超出限制提示语： “每分钟每个群最多被调用1次该接口” “每小时每个群最多调用5次该接口”
     * 次数按照调用即算作一次，不论是否失败；
     * @param vcRobotSerialNo
     * @param vcChatRoomSerialNo
     * @return
     */
    public String pullChatRoomMemberList(String vcRobotSerialNo, String vcChatRoomSerialNo) {
        String url = TSApiConfig.ROBOT_CHATROOM_GetChatRoomUserInfo.replace("{TOKEN}", tsAuthService.getToken())
        TSPostChatRoomInfoBean postChatRoomInfoBean = new TSPostChatRoomInfoBean()
        postChatRoomInfoBean.setVcRobotSerialNo(vcRobotSerialNo)
        postChatRoomInfoBean.setVcChatRoomSerialNo(vcChatRoomSerialNo)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postChatRoomInfoBean))
        return retJson
    }

    public TSChatRoomESMap loadChatRoomMemberList(String vcRobotSerialNo, String vcChatRoomSerialNo) {
        TSChatRoomESMap tsChatRoomESMap = tsChatRoomESService.load(vcChatRoomSerialNo, TSChatRoomESMap.class) as TSChatRoomESMap
        return tsChatRoomESMap
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
    public callBackChatRoomInfo(String strContext) {

        println strContext
        JSONObject jsonObject = JSONObject.parseObject(strContext)
        String vcRobotSerialNo = jsonObject.getString("vcRobotSerialNo")
        println "vcRobotSerialNo:" + vcRobotSerialNo
        println "jsonObject.get(\"Data\")=" + jsonObject.get("Data")
        if (jsonObject.getJSONArray("Data")) {
            JSONArray array = jsonObject.getJSONArray("Data")
            List<TSRoomInfoESMap> _list = new ArrayList<>()
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.get(i) as JSONObject
                TSRoomInfoESMap room = convertRoomJson2Obj(vcRobotSerialNo, obj)
                if (room != null) {
                    _list.add(room)
                } else {
                    println "==ERROR room is null=="
                }
            }
            tsRobotRoomInfoESService.saveBulk(_list)
        }
    }

    /**
     * 修改群名
     * @param strContext
     * @return
     */
    public callBackChatRoomNameChange(String strContext) {
        JSONObject jsonObject = JSONObject.parseObject(strContext)
        String vcRobotSerialNo = jsonObject.getString("vcRobotSerialNo")
        Integer nType = jsonObject.getInteger("nType")
        if (jsonObject.getJSONObject("Data")) {
            JSONObject obj = jsonObject.getJSONObject("Data")
            TSRoomInfoESMap room = convertRoomJson2Obj(vcRobotSerialNo, obj)
            if (room != null)
                tsRobotRoomInfoESService.save(room.getId(), room)
        }
    }

    /**
     * 4007和4507 主动退群回调 4007 被踢出群回调 4507
     * @param strContext
     */
    public callBackQuitChatRoom(String strContext) {
        JSONObject jsonObject = JSONObject.parseObject(strContext)
        String vcRobotSerialNo = jsonObject.getString("vcRobotSerialNo")
        Integer nType = jsonObject.getInteger("nType")
        if (jsonObject.getJSONObject("Data")) {
            String room_Id = jsonObject.getJSONObject("Data").getString("vcChatRoomSerialNo")
            String id = new MD5().getMD5(vcRobotSerialNo + room_Id)
            String status = "1"
            if (nType == 4007) {
                status = "1"
            } else if (nType == 4507) {
                status = "2"
            }
            tsRobotRoomInfoESService.update(id, "room_status", status)
        }
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
    public callBackChatRoomMemberInfo(String strContext) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(strContext)
            println jsonObject
            if (jsonObject && jsonObject.get("Data")) {
                JSONObject dataObj = jsonObject.get("Data") as JSONObject
                TSChatRoomESMap tsChatRoomESMap = JSONObject.parseObject(dataObj.toString(), TSChatRoomESMap.class) as TSChatRoomESMap
                if (tsChatRoomESService != null) {
                    tsChatRoomESMap.setId(tsChatRoomESMap.getVcChatRoomSerialNo())
                    tsChatRoomESMap.setUpdatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
                    tsChatRoomESService.save(tsChatRoomESMap.getId(), tsChatRoomESMap)
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    /**
     * 新成员入群回调接口 4502
     * 群内有新人加入，通过该接口将新人入群信息回调给商家
     * http://docs.op.opsdns.cc:8081/groupmember/chatroom-members-join/
     */
    public callBackChatRoomAddMember(String strContext) {
        println strContext
        def jsonSlurper = new JsonSlurper()
        def json = jsonSlurper.parseText(strContext)
        String robot_id = json['vcRobotSerialNo']
        if (json['Data'] != null) {
            String room_id = json['Data']['vcChatRoomSerialNo']
            JSONArray array = json['Data']['Members'] as JSONArray
            TSChatRoomESMap tsChatRoomESMap = tsChatRoomESService.load(room_id, TSChatRoomESMap.class) as TSChatRoomESMap
            List<TSChatRoomMemberESMap> memberESMapList = tsChatRoomESMap.getMembers()
            for (int i = 0; i < array.size(); i++) {
                JSONObject memberObj = array.get(i) as JSONObject
                TSChatRoomMemberESMap tsChatRoomMemberESMap = memberObj.toJavaObject(TSChatRoomMemberESMap.class) as TSChatRoomMemberESMap
                if (tsChatRoomMemberESMap) {
                    memberESMapList.add(tsChatRoomMemberESMap)
                }
            }
            tsChatRoomESMap.setMembers(memberESMapList)
            tsChatRoomESMap.setnMemberCount(memberESMapList.size())
            tsChatRoomESService.save(room_id, tsChatRoomESMap)
            println "===save callBackChatRoomAddMember==="
        }
    }

    /**
     * 群成员退群回调接口 4503
     * 群内有成员退群，通过该接口将成员退群信息回调给商家
     * http://docs.op.opsdns.cc:8081/groupmember/chatroom-members-quit/
     */
    public callBackChatRoomDeleteMember(String strContext) {
        println strContext
        def jsonSlurper = new JsonSlurper()
        def json = jsonSlurper.parseText(strContext)
        String robot_id = json['vcRobotSerialNo']

        if (json['Data'] != null) {
            json['Data'].each {it->
                String room_id=it['vcChatRoomSerialNo']
                String vcMemberUserSerialNo=it['vcMemberUserSerialNo']
                TSChatRoomESMap tsChatRoomESMap = tsChatRoomESService.load(room_id, TSChatRoomESMap.class) as TSChatRoomESMap
                List<TSChatRoomMemberESMap> memberESMapList = tsChatRoomESMap.getMembers()
                for(TSChatRoomMemberESMap tsChatRoomMemberESMap:memberESMapList){
                    if(tsChatRoomMemberESMap.vcMemberUserSerialNo==vcMemberUserSerialNo){
                        memberESMapList.remove(tsChatRoomMemberESMap)
                    }
                }
                tsChatRoomESMap.setMembers(memberESMapList)
                tsChatRoomESMap.setnMemberCount(memberESMapList.size())

                tsChatRoomESService.save(room_id, tsChatRoomESMap)
                println "===save callBackChatRoomDeleteMember==="
            }
        }
    }

}
