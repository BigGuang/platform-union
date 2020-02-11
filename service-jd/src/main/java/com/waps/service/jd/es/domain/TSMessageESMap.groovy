package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class TSMessageESMap extends ESBaseBean{
    int nType
    String vcMerchantNo
    String vcRobotWxId
    String vcRobotSerialNo
    String vcSerialNo
    String vcMsgSerialNo
    String vcMsgId
    String vcChatRoomSerialNo
    String vcChatRoomId
    String vcFromWxUserSerialNo
    String vcFromWxUserWxId
    String vcToWxUserSerialNo
    String vcToWxUserWxId
    int nMsgType
    String vcContent
    int nVoiceTime
    String vcShareTitle
    String vcShareDesc
    String vcShareUrl
    String dtMsgTime
    int nIsHit
    int nPlatformMsgType
    String vcRelaSerialNo
    int nMsgNum
    String vcDownFileSerialNo
    String msg_status
    String msg_from
    String createtime
}
