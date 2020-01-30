package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class JDMediaInfoESMap extends ESBaseBean {
    String media_id
    String media_name
    String channel_id
    String channel_name
    String pid
    String desc
    String createtime
}
