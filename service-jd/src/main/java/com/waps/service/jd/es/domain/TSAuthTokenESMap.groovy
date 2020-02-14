package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class TSAuthTokenESMap extends ESBaseBean{
    int code
    String result
    String merchant
    String token
    String refresh_token
    Long createTime = 0l
}
