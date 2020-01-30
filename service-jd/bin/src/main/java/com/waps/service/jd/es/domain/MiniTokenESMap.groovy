package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class MiniTokenESMap extends ESBaseBean {
    String name
    String app_id
    String app_secret
    String access_token
    long exp_time
    long end_time
    String createtime
}
