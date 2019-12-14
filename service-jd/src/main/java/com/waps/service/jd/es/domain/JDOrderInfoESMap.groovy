package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class JDOrderInfoESMap extends ESBaseBean{
    String finishTime
    String finishTime_Date
    String popId
    String orderTime
    String orderTime_Date
    String payMonth
    String unionId
    String orderId
    String validCode
    String parentId
    String plus
    String ext1
    String ext1_positionId
    String ext1_user
    String orderEmt
    List<SkuInfoESMap> skuList = new ArrayList<>()
    String createtime
}

class SkuInfoESMap {
    double commissionRate
    double estimateCosPrice
    String unionTrafficGroup
    String validCode
    double estimateFee
    String pid
    String skuName
    String popId
    String payMonth
    double price
    String unionTag
    String cid1
    String cid1_name
    String cid2
    String cid2_name
    String cid3
    String cid3_name
    double actualFee
    double finalRate
    String skuId
    String ext1
    String ext1_positionId
    String ext1_user
    double subsidyRate
    String unionAlias
    String subUnionId
    String skuReturnNum
    String positionId
    String positionName
    String traceType
    double actualCosPrice
    String siteId
    String frozenSkuNum
    String skuNum
    double subSideRate
}
