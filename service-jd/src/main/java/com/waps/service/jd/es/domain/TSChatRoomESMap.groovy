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
