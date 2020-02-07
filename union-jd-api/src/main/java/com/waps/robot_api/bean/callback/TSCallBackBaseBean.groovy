package com.waps.robot_api.bean.callback

class TSCallBackBaseBean {
    int nType
    String vcMerchantNo
    String vcRobotWxId
    String vcRobotSerialNo
    String vcSerialNo
    String nResult
    String vcResult
    Map<String, Object> Data = new HashMap<>()
}
