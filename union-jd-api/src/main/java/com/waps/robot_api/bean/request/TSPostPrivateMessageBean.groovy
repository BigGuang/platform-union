package com.waps.robot_api.bean.request

class TSPostPrivateMessageBean extends TSPostBaseBean {
    String vcRobotSerialNo
    String vcRelaSerialNo
    String vcToWxSerialNo
    List<TSMessageBean> Data = new ArrayList<>()
}
