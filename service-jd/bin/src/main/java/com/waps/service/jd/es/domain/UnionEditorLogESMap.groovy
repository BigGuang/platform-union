package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class UnionEditorLogESMap extends ESBaseBean {
    List<SkuBeanESMap> skuList = new ArrayList<>()
    int imageCount
    int videoCount
    int accountId
    String images
    int messageType
    int weight
    int robotMessageId
    String videos
    String text
    int status
    String sendtime
    String createtime
}

class SkuBeanESMap {
    String skuId
}