package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class TSChatRoomESMap extends ESBaseBean {
    String vcChatRoomId
    String vcChatRoomSerialNo
    String vcChatRoomName
    String vcBase64ChatRoomName
    String vcChatAdminWxUserSerialNo
    String vcChatAdminWxId
    int nMemberCount
    List<TSChatRoomManagerESMap> Managers = new ArrayList<>()
    List<TSChatRoomMemberESMap> Members = new ArrayList<>()
    String updatetime
}

class TSChatRoomManagerESMap {
    String vcManagerWxId
    String vcManagerSerialNo
}

class TSChatRoomMemberESMap {
    String vcMemberUserSerialNo
    String vcMemberUserWxId
    String vcNickName
    String vcBase64NickName
    String vcHeadImgUrl
    String vcFatherWxId
    String vcFatherWxUserSerialNo
    String vcGroupNickName
    String vcBase64GroupNickName

}

class TSRoomInfoESMap extends ESBaseBean {
//    "vcChatRoomId": "18094678040@chatroom",
//    "nIsInContacts": 0,
//    "nUserCount": 13,
//    "vcHeadImg": "http://wx.qlogo.cn/mmcrhead/PiajxSqBRaEIrrBusqZIJT7crpDZHzHJ8e0WJAxm3kZbBIibOkK8jzLurHM0PhmnQz8YTPruqc3ianD2PDTaEG7GlI9VPYJqOwC/0",
//    "vcName": "测试群A004",
//    "vcChatRoomQRCode": "",
//    "vcAdminWxId": "wxid_1f0pttr8reyd11",
//    "vcAdminWxUserSerialNo": "7B269B91A63CDD8C3D15EA075FA7365A",
//    "vcBase64Name": "5rWL6K+V576kQTAwNA==",
//    "dtCreateDate": "2020-02-11T03:13:55.181Z",
//    "isOpenMessage": 10,
//    "nRobotInStatus": 0,
//    "vcChatRoomSerialNo": "C840CEBA985DEB039C3D3063C4D7DB6F"
    String vcRobotSerialNo  //机器人编号
    String vcChatRoomId
    int nIsInContacts
    int nUserCount
    String vcHeadImg
    String vcName
    String channel_name
    String channel_id
    String vcChatRoomQRCode
    String vcGroupNotice
    String vcBase64Notice
    String vcAdminWxId
    String vcAdminWxUserSerialNo
    String dtCreateDate
    String vcBase64Name
    int isOpenMessage
    int nRobotInStatus
    String vcChatRoomSerialNo
    String room_status
    String createtime
}

