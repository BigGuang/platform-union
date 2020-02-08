package com.waps.robot_api.bean.request

class TSPostGroupMessageBean extends TSPostBaseBean{
    String vcRobotSerialNo
    String vcRelaSerialNo
    String vcChatRoomSerialNo
    int nIsHit=1  //是否艾特 (0 艾特群内所有人 1 艾特或者不艾特用户)
    String vcToWxSerialNo  //指定艾特用户编号(多个用,号隔开,如果不用艾特则传空)
    List<TSMessageBean> Data = new ArrayList<>()
}
