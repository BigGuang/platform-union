package com.waps.robot_api.service

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.elastic.search.utils.PageUtils
import com.waps.robot_api.bean.request.TSPostModifyInfoBean
import com.waps.robot_api.bean.request.TSPostRobotInfoBean
import com.waps.robot_api.bean.request.TSPostRobotSwitchBean
import com.waps.robot_api.bean.response.TSResponseRobotInfoBean
import com.waps.robot_api.utils.TSApiConfig
import com.waps.service.jd.es.domain.TSRobotESMap
import com.waps.service.jd.es.domain.TSRoomConfigESMap
import com.waps.service.jd.es.service.TSRobotESService
import com.waps.service.jd.es.service.TSRoomConfigESService
import com.waps.union_jd_api.service.WeChatRobotService
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TSRobotConfigService {
    @Autowired
    TSAuthService tsAuthService
    @Autowired
    TSRobotESService tsRobotESService
    @Autowired
    TSRobotChatRoomService tsRobotChatRoomService
    @Autowired
    TSRoomConfigESService tsRoomConfigESService
    @Autowired
    WeChatRobotService weChatRobotService

    /**
     * 设置是否自动通过添加好友
     * @param vcRobotSerialNo
     * @param flg
     * @return
     */
    public String setAutoAddFriendSetup(String vcRobotSerialNo, boolean flg) {
        String url = TSApiConfig.ROBOT_SETTING_AutoAddFriendSetup.replace("{TOKEN}", tsAuthService.getToken())
        TSPostRobotSwitchBean tsPostAddFriendSetup = new TSPostRobotSwitchBean()
        tsPostAddFriendSetup.setVcRobotSerialNo(vcRobotSerialNo)
        if (flg) {
            tsPostAddFriendSetup.setnSwitch(1)
        } else {
            tsPostAddFriendSetup.setnSwitch(0)
        }
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(tsPostAddFriendSetup))
        return retJson
    }

    /**
     * 设置是否自动加入群聊
     * @param vcRobotSerialNo
     * @param flg
     * @return
     */
    public String setAutoJoinChatRoomSetup(String vcRobotSerialNo, boolean flg) {
        String url = TSApiConfig.ROBOT_SETTING_AutoJoinChatRoomSetup.replace("{TOKEN}", tsAuthService.getToken())
        TSPostRobotSwitchBean postRobotSwitchBean = new TSPostRobotSwitchBean()
        postRobotSwitchBean.setVcRobotSerialNo(vcRobotSerialNo)
        if (flg) {
            postRobotSwitchBean.setnSwitch(1)
        } else {
            postRobotSwitchBean.setnSwitch(0)
        }
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postRobotSwitchBean))
        return retJson
    }


    /**
     * 实时获取机器人信息列表，返回json字符串
     * @return
     */
    public String getRobotInfoListStr(String[] robotIdList, int page) {
        String url = TSApiConfig.ROBOT_INFO_LIST.replace("{TOKEN}", tsAuthService.getToken())
        TSPostRobotInfoBean tsPostRobotInfoBean = new TSPostRobotInfoBean()
        tsPostRobotInfoBean.setVcRobotSerialNos(robotIdList)
        tsPostRobotInfoBean.setnPageIndex(page)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(tsPostRobotInfoBean))
        return retJson
    }

    /**
     * 实时获取机器人信息列表，返回bean
     * @return
     */
    public TSResponseRobotInfoBean getRobotInfoListBean(String[] robotIdList, int page) {
        String retJson = getRobotInfoListStr(robotIdList, page)
        if (!StringUtils.isNull(retJson)) {
            TSResponseRobotInfoBean tsResponseRobotInfoBean = JSONObject.parseObject(retJson, TSResponseRobotInfoBean.class) as TSResponseRobotInfoBean
            println tsResponseRobotInfoBean
            return tsResponseRobotInfoBean
        } else {
            return null
        }
    }

    /**
     * 从ES中读取定时同步的信息
     * @param page
     * @param size
     * @return
     */
    public SearchHits getRobotInfoListFromES(int page, int size) {
        PageUtils pageUtils = new PageUtils(page, size)
        HashMap params = new HashMap()
        params.put("from", pageUtils.getFrom())
        params.put("size", pageUtils.getSize())
        SearchHits hits = tsRobotESService.findByFreeMarkerFromResource("es_script/ts_robot_list.json", params)
        return hits
    }


    /**
     * 同步所有机器人信息，和机器人下的房间信息
     */
    public void syncAllRobotAndRoom() {
        try {
            println "===syncAllRobotAndRoom==="
            int page = 1
            while (page == 1) {
                TSResponseRobotInfoBean tsResponseRobotInfoBean = getRobotInfoListBean(null, page)
                page = tsResponseRobotInfoBean.getData().getnPageIndex()

                List<TSRobotESMap> list = tsResponseRobotInfoBean.getData().getRobotList()
                for (TSRobotESMap tsRobotESMap : list) {
                    println "==sync:" + tsRobotESMap.getVcNickName() + " " + tsRobotESMap.getVcRobotSerialNo()
                    String robot_id = tsRobotESMap.getVcRobotSerialNo()
                    tsRobotESMap.setId(tsRobotESMap.getVcRobotSerialNo())
                    tsRobotESService.save(tsRobotESMap.getId(), tsRobotESMap)
                    String retJson = tsRobotChatRoomService.getChatRoomInfoList(tsRobotESMap.getVcRobotSerialNo(), null, 0)
                    JSONObject jsonObject = JSONObject.parseObject(retJson)
                    List<TSRoomConfigESMap> _roomList = new ArrayList<>()
                    if (jsonObject && jsonObject.getJSONArray("Data")) {
                        JSONArray array = jsonObject.getJSONArray("Data")
                        for (int i = 0; i < array.size(); i++) {
                            JSONObject obj = array.get(i) as JSONObject
                            TSRoomConfigESMap room = tsRobotChatRoomService.convertRoomJson2Obj(tsRobotESMap.getVcRobotSerialNo(), obj)
                            _roomList.add(room)
                        }
                        tsRoomConfigESService.saveBulk(_roomList)
                    }
                    tsRobotChatRoomService.autoSetAllowAddRoom(robot_id)
                }
            }

        } catch (Exception e) {
            e.printStackTrace()
        }
    }


    /**
     * 修改机器人个性签名
     * @return
     */
    public String setProfileWhatsUp(String vcRobotSerialNo, String vcWhatsUp) {
        String url = TSApiConfig.ROBOT_SETTING_ModifyProfileWhatsUp.replace("{TOKEN}", tsAuthService.getToken())
        TSPostModifyInfoBean postModifyInfoBean = new TSPostModifyInfoBean()
        postModifyInfoBean.setVcRobotSerialNo(vcRobotSerialNo)
        postModifyInfoBean.setVcWhatsUp(vcWhatsUp)
        return HttpUtils.postJsonString(url, JSONObject.toJSONString(postModifyInfoBean))
    }

    /**
     * 修改机器人昵称
     * @param vcRobotSerialNo
     * @param vcNickName
     * @return
     */
    public String setProfileName(String vcRobotSerialNo, String vcNickName) {
        String url = TSApiConfig.ROBOT_SETTING_ModifyProfileName.replace("{TOKEN}", tsAuthService.getToken())
        TSPostModifyInfoBean postModifyInfoBean = new TSPostModifyInfoBean()
        postModifyInfoBean.setVcRobotSerialNo(vcRobotSerialNo)
        postModifyInfoBean.setVcNickName(vcNickName)
        return HttpUtils.postJsonString(url, JSONObject.toJSONString(postModifyInfoBean))
    }

    /**
     * 修改机器人头像
     * @param vcRobotSerialNo
     * @param vcHeadImgUrl
     * @return
     */
    public String setProfileHeadImg(String vcRobotSerialNo, String vcHeadImgUrl) {
        String url = TSApiConfig.ROBOT_SETTING_ModifyProfileHeadImg.replace("{TOKEN}", tsAuthService.getToken())
        TSPostModifyInfoBean postModifyInfoBean = new TSPostModifyInfoBean()
        postModifyInfoBean.setVcRobotSerialNo(vcRobotSerialNo)
        postModifyInfoBean.setVcHeadImgUrl(vcHeadImgUrl)
        return HttpUtils.postJsonString(url, JSONObject.toJSONString(postModifyInfoBean))

    }

    /**
     * 修改机器人性别
     * @param vcRobotSerialNo
     * @param nSex
     * @return
     */
    public String setProfileGender(String vcRobotSerialNo, int nSex) {
        String url = TSApiConfig.ROBOT_SETTING_ModifyProfileGender.replace("{TOKEN}", tsAuthService.getToken())
        TSPostModifyInfoBean postModifyInfoBean = new TSPostModifyInfoBean()
        postModifyInfoBean.setVcRobotSerialNo(vcRobotSerialNo)
        postModifyInfoBean.setnSex(nSex)
        return HttpUtils.postJsonString(url, JSONObject.toJSONString(postModifyInfoBean))
    }


    /**
     * 修改机器人信息回调 1006
     * http://docs.op.opsdns.cc:8081/Personal-number-msg/Personal-msg-callback/
     * @param strContext
     */
    public callBackModifyInfo(String strContext) {

    }


}
