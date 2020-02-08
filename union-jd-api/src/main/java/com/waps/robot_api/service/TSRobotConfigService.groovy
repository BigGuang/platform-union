package com.waps.robot_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSPostDeleteFriend
import com.waps.robot_api.bean.request.TSPostModifyInfoBean
import com.waps.robot_api.bean.request.TSPostRobotFriendBean
import com.waps.robot_api.bean.request.TSPostRobotInfoBean
import com.waps.robot_api.bean.request.TSPostRobotSerialNoBean
import com.waps.robot_api.bean.request.TSPostRobotSwitchBean
import com.waps.robot_api.bean.response.TSResponseRobotInfoBean
import com.waps.robot_api.utils.TSApiConfig
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TSRobotConfigService {
    @Autowired
    TSAuthService tsAuthService

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
     * 手动通过加好友请求
     * @param vcRobotSerialNo
     * @param vcSerialNo
     * @return
     */
    public String acceptNewFriendRequest(String vcRobotSerialNo, String vcSerialNo) {
        String url = TSApiConfig.ROBOT_SETTING_AcceptNewFriendRequest.replace("{TOKEN}", tsAuthService.getToken())
        TSPostRobotSerialNoBean postRobotSerialNoBean = new TSPostRobotSerialNoBean()
        postRobotSerialNoBean.setVcRobotSerialNo(vcRobotSerialNo)
        postRobotSerialNoBean.setVcSerialNo(vcSerialNo)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postRobotSerialNoBean))
        return retJson
    }


    /**
     * 实时获取机器人信息列表，返回json字符串
     * @return
     */
    public String getRobotInfoListStr() {
        String url = TSApiConfig.ROBOT_INFO_LIST.replace("{TOKEN}", tsAuthService.getToken())
        TSPostRobotInfoBean tsPostRobotInfoBean = new TSPostRobotInfoBean()
        tsPostRobotInfoBean.setVcRobotSerialNos(TSApiConfig.robotList)
        tsPostRobotInfoBean.setnPageIndex(1)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(tsPostRobotInfoBean))
        return retJson
    }

    /**
     * 实时获取机器人信息列表，返回bean
     * @return
     */
    public TSResponseRobotInfoBean getRobotInfoListBean() {
        String retJson = getRobotInfoListStr()
        if (!StringUtils.isNull(retJson)) {
            TSResponseRobotInfoBean tsResponseRobotInfoBean = JSONObject.parseObject(retJson, TSResponseRobotInfoBean.class) as TSResponseRobotInfoBean
            println tsResponseRobotInfoBean
            return tsResponseRobotInfoBean
        } else {
            return null
        }
    }

    /**
     * 获取微信机器人的好友列表
     * @return
     */
    public String getRobotFriendListStr(String vcRobotSerialNo) {
        String url = TSApiConfig.ROBOT_FRIEND_LIST_URL.replace("{TOKEN}", tsAuthService.getToken())
        TSPostRobotFriendBean postRobotFriendBean = new TSPostRobotFriendBean()
        postRobotFriendBean.setVcRobotSerialNo(vcRobotSerialNo)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postRobotFriendBean))
        return retJson
    }


    /**
     * 获取微信机器人的好友列表
     * @return
     */
    public String deleteFriend(String vcRobotSerialNo,String vcContactSerialNo) {
        String url = TSApiConfig.ROBOT_FRIEND_DeleteContact.replace("{TOKEN}", tsAuthService.getToken())
        TSPostDeleteFriend postDeleteFriend = new TSPostDeleteFriend()
        postDeleteFriend.setVcRobotSerialNo(vcRobotSerialNo)
        postDeleteFriend.setVcContactSerialNo(vcContactSerialNo)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postDeleteFriend))
        return retJson
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
        String url = TSApiConfig.ROBOT_SETTING_ModifyProfileWhatsUp.replace("{TOKEN}", tsAuthService.getToken())
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
        String url = TSApiConfig.ROBOT_SETTING_ModifyProfileWhatsUp.replace("{TOKEN}", tsAuthService.getToken())
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
        String url = TSApiConfig.ROBOT_SETTING_ModifyProfileWhatsUp.replace("{TOKEN}", tsAuthService.getToken())
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

    /**
     * 好友信息列表回调 3001
     * 1、机器人登录后，回调给商家一次全量好友列表（3001）； 3、机器人在不掉线的情况下每24小时，平台主动推送一次全量好友列表（3001）用于商家校验；
     * http://docs.op.opsdns.cc:8081/Personal-number-msg/friends-callback/
     * @param strContext
     */
    public callBackAllFriendList(String strContext) {

    }

    /**
     * 【直接回调】 新好友请求回调接口 3003
     * 当机器人被用户加好友以后，我们会将这个申请添加好友的用户信息和请求回调给商家。便于之后选择是否添加该好友。
     * 如果对方是通过机器人的好友分享的名片来添加的机器人，
     * 则会在本回调中的vcFriendWxId、vcFriendSerialNo、vcFriendNickName三个参数中带对应机器人好友的参数值；
     * http://docs.op.opsdns.cc:8081/Personal-number-function/NewFriendRq-callback/
     * @param strContext
     */
    public callBackAddFriendRequest(String strContext){

    }

    /**
     * 【直接回调】 主动添加好友结果回调接口（还未成为好友）3002
     * 主动添加好友操作成功回调（只是添加操作成功，不代表已成为好友）
     * http://docs.op.opsdns.cc:8081/Personal-number-function/add-to-friends-operate-callback/
     * @param strContext
     */
    public callBackPushAddFriendRequest(String strContext) {

    }

    /**
     * 【直接回调】 机器人被加/主动加好友（成为好友）回调接口  3005
     * 条件：
     * 1、当微信用户添加机器人时，机器人开启了自动通过好友请求开关，且微信用户与机器人双边都没有好友关系；
     * 2、当微信用户添加机器人时，机器人未开启自动通过好友请求开关，且双边都不是好友关系；
     * 3、当机器人主动添加好友，好友通过验证后，回将本回调给商家；
     * 以上三种条件任意一种都会触发此回调；
     * http://docs.op.opsdns.cc:8081/Personal-number-function/add-to-friends-callback/
     */
    public callBackAddFriendSuccess(String strContext) {

    }

    /**
     * 【直接回调】 通过好友请求回调接口 3011
     * 回调条件：
     * 1、微信用户添加机器人好友
     * 2、机器人关闭了自动通过好友请求（不会自动通过好友验证）
     * 3、机器人与微信用户双边都不是好友关系。
     * 流程图：参见菜单栏流程图：微信用户添加机器人好友的回调机制
     * http://docs.op.opsdns.cc:8081/Personal-number-function/ThroughFriendRq-callback/
     */
    public callBackAddFriendThrough(String strContext) {

    }

    /**
     * 删除联系人好友结果回调 3004
     * http://docs.op.opsdns.cc:8081/Personal-number-function/DeleteContact-callback/
     * @param strContext
     */
    public callBackDeleteFriend(String strContext){

    }
}
