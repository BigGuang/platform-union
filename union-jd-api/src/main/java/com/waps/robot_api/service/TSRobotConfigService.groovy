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
     * 实时获取机器人信息列表，返回json字符串
     * @return
     */
    public String getRobotInfoListStr(String[] robotIdList,int page) {
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
    public TSResponseRobotInfoBean getRobotInfoListBean(String[] robotIdList,int page) {
        String retJson = getRobotInfoListStr(robotIdList,page)
        if (!StringUtils.isNull(retJson)) {
            TSResponseRobotInfoBean tsResponseRobotInfoBean = JSONObject.parseObject(retJson, TSResponseRobotInfoBean.class) as TSResponseRobotInfoBean
            println tsResponseRobotInfoBean
            return tsResponseRobotInfoBean
        } else {
            return null
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


}
