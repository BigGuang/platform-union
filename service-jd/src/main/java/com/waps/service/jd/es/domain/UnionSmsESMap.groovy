package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class UnionSmsESMap extends ESBaseBean{
    String phone
    String code
    Long expiretime
    String createtime
}
