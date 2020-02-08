package com.waps.robot_api.bean.request

class TSPostChatRoomInfoBean extends TSPostBaseBean {
    String vcRobotSerialNo
    String vcChatRoomSerialNo
    int isOpenMessage = 0  //是否已开通（10 是 11 否）, 0 查询全部
}
