package com.waps.robot_api.service

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSPostDeleteFriend
import com.waps.robot_api.bean.request.TSPostRobotFriendBean
import com.waps.robot_api.bean.request.TSPostRobotSerialNoBean
import com.waps.robot_api.bean.response.TSResponseRobotInfoBean
import com.waps.robot_api.utils.TSApiConfig
import com.waps.service.jd.es.domain.TSRobotESMap
import com.waps.service.jd.es.domain.TSRobotFriendESMap
import com.waps.service.jd.es.service.TSRobotESService
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import groovy.json.JsonSlurper
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TSRobotFriendService {
    @Autowired
    TSAuthService tsAuthService
    @Autowired
    TSRobotConfigService tsRobotConfigService
    @Autowired
    TSRobotESService tsRobotESService

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
     * 删除好友
     * @return
     */
    public String deleteFriend(String vcRobotSerialNo, String vcContactSerialNo) {
        String url = TSApiConfig.ROBOT_FRIEND_DeleteContact.replace("{TOKEN}", tsAuthService.getToken())
        TSPostDeleteFriend postDeleteFriend = new TSPostDeleteFriend()
        postDeleteFriend.setVcRobotSerialNo(vcRobotSerialNo)
        postDeleteFriend.setVcContactSerialNo(vcContactSerialNo)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(postDeleteFriend))
        return retJson
    }

    /**
     * 获取某一个机器人下的好友信息
     * @param robot_id
     * @param friend_id
     * @return
     */
    public TSRobotFriendESMap getRobotFriend(String robot_id, String friend_id) {
        if (!StringUtils.isNull(robot_id)) {
            TSRobotESMap tsRobotESMap = tsRobotESService.load(robot_id, TSRobotESMap.class) as TSRobotESMap
            for (TSRobotFriendESMap friendESMap : tsRobotESMap.getFriend_list()) {
                if (friendESMap.getVcFriendSerialNo() == friend_id) {
                    return friendESMap
                }
            }
        }
    }

    /**
     * 获取个机器人下的好友列表
     * @param robot_id
     * @return
     */
    public List<TSRobotFriendESMap> getRobotFriendList(String robot_id) {
        if (!StringUtils.isNull(robot_id)) {
            TSRobotESMap tsRobotESMap = tsRobotESService.load(robot_id, TSRobotESMap.class) as TSRobotESMap
            if (tsRobotESMap != null) {
                return tsRobotESMap.getFriend_list()
            }
        }
    }


    /**
     * 同步好友信息到es缓存
     */
    public void syncAllRobotFriend() {
        println "===syncAllRobotFriend==="
        try {
            int page = 1
            while (page == 1) {
                TSResponseRobotInfoBean tsResponseRobotInfoBean = tsRobotConfigService.getRobotInfoListBean(null, page)
                page = tsResponseRobotInfoBean.getData().getnPageIndex()
                List<TSRobotESMap> list = tsResponseRobotInfoBean.getData().getRobotList()
                for (TSRobotESMap tsRobotESMap : list) {
                    String robot_id = tsRobotESMap.getVcRobotSerialNo()
                    String friendJson = getRobotFriendListStr(robot_id)
                    def jsonSlurper = new JsonSlurper()
                    def json = jsonSlurper.parseText(friendJson)
                    def data = json['Data']
                    if (data) {
                        JSONArray _data = data as JSONArray
                        for (int i = 0; i < _data.size(); i++) {
                            def obj = _data.get(i)
                            JSONArray friend_list = obj['FriendList'] as JSONArray
                            List<TSRobotFriendESMap> _friendList = new ArrayList<>()
                            for (int j = 0; j < friend_list.size(); j++) {
                                JSONObject _friendObj = friend_list.get(j)
                                if (_friendObj) {
                                    TSRobotFriendESMap friendESMap = _friendObj.toJavaObject(TSRobotFriendESMap.class) as TSRobotFriendESMap
                                    _friendList.add(friendESMap)
                                }
                            }
                            Map<String, Object> params = new HashMap<>()
                            params.put("friend_list", _friendList)
                            TSRobotESMap _robotESMap = tsRobotESService.load(robot_id, TSRobotESMap.class) as TSRobotESMap
                            if (_robotESMap != null) {
                                _robotESMap.setFriend_list(_friendList)
                            }
                            tsRobotESService.save(robot_id, _robotESMap)

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }

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
    public callBackAddFriendRequest(String strContext) {

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
        println "===callBackAddFriendSuccess==="
        def jsonSlurper = new JsonSlurper()
        def json = jsonSlurper.parseText(strContext)
        String robot_id = json['vcRobotSerialNo']
        if (json['Data'] != null) {
            TSRobotESMap robotESMap = tsRobotESService.load(robot_id, TSRobotESMap.class) as TSRobotESMap
            if (robotESMap != null) {
                List<TSRobotFriendESMap> friend_list = robotESMap.getFriend_list()
                if (friend_list == null && friend_list.size() == 0) {
                    friend_list = new ArrayList<>()
                }
                JSONArray array = json['Data'] as JSONArray
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.get(i)
                    if (obj != null) {
                        TSRobotFriendESMap robotFriendESMap = obj.toJavaObject(TSRobotFriendESMap.class) as TSRobotFriendESMap
                        friend_list.add(robotFriendESMap)
                    }
                }
                robotESMap.setFriend_list(friend_list)
                tsRobotESService.save(robotESMap.getId(), robotESMap)
            }
        }
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
    public callBackDeleteFriend(String strContext) {
        println "===callBackDeleteFriend==="
        def jsonSlurper = new JsonSlurper()
        def json = jsonSlurper.parseText(strContext)
        String robot_id = json['vcRobotSerialNo']
        if (json['Data'] != null) {
            String vcContactSerialNo = json['Data']['vcContactSerialNo']
            if (!StringUtils.isNull(vcContactSerialNo)) {
                TSRobotESMap robotESMap = tsRobotESService.load(robot_id, TSRobotESMap.class) as TSRobotESMap
                if (robotESMap != null) {
                    List<TSRobotFriendESMap> _friend_list = robotESMap.getFriend_list()
                    for (TSRobotFriendESMap robotFriendESMap : _friend_list) {
                        if (robotFriendESMap.getVcFriendSerialNo() == vcContactSerialNo) {
                            _friend_list.remove(robotFriendESMap)
                        }
                    }
                    robotESMap.setFriend_list(_friend_list)
                    tsRobotESService.save(robotESMap.getId(), robotESMap)
                }
            }
        }
    }
}
