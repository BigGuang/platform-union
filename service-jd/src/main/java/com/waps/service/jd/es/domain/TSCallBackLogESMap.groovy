package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class TSCallBackLogESMap extends ESBaseBean{
    int nType
    String vcMerchantNo
    String vcRobotWxId
    String vcRobotSerialNo
    String vcSerialNo
    int nResult
    String vcResult
    String data
    String createtime
}
