package com.robot.netty.server

class GroupBean {
    String NickName
    String UserName
    String EncryChatRoomId
    Integer MemberCount=0
    String HeadImgUrl
    MemberBean[] MemberList=[]
}

class MemberBean{
    String EncryChatRoomId
    String UserName
    String NickName
    String Province
    String City
    String Signature
    Integer Sex
    String HeadImgUrl
}
