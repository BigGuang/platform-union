package com.waps.robot_api.bean.request

class TSPostPrivateMessageBean extends TSPostBaseBean {
    String vcRobotSerialNo     //机器人编号
    String vcRelaSerialNo      //商家业务流水号
    String vcToWxSerialNo      //私聊好友编号
    List<TSMessageBean> Data = new ArrayList<>()
}
