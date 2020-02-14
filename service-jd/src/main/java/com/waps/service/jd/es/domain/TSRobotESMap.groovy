package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class TSRobotESMap extends ESBaseBean{
    String vcRobotSerialNo  //机器人编号
    String vcRobotWxId    //机器人wxid
    String vcWxAlias      //用户微信号
    String vcNickName         //用户昵称
    String vcBase64NickName   //BASE64编码过后的用户昵称
    String vcHeadImgUrl       //头像地址
    String vcPersonQRCode      //用户二维码
    int nStatus = 11          //机器人状态 10 在线 11 离线
    int nIsLimit = 0          //机器人是否被封号 0 未封号 1 已封号
    int nSex = 0              //性别（1 男 2 女 0 未知）
    String vcWhatsUp           //个性签名
    String vcBase64WhatsUp     //base64编码个性签名
    int nTrueName = 1                //是否认证（ 0 否 1 是）
    int nSourceType = 0              //来源类型（0 平台号 1 托管号）
    int nIsAutoAddFriend = 1         //是否设置自动通过好友请求（ 0 否 1 是）
    int nIsAutoJoinChatRoom = 0      //是否设置自动进入群聊 （0 否 1 是）
    int nChatRoomCount = 25          //机器人的群数量
    int nOpenChatRoomCount           //机器人开群数量
    String vcPhone                   //扫码号绑定的手机号
    String updatetime
    String createtime
}