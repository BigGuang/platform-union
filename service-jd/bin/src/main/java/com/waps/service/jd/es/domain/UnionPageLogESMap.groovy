package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class UnionPageLogESMap extends ESBaseBean {
    String user_id
    String host
    String channel_name
    String user_agent
    String referer
    String area
    String isp
    String ip
    String page
    String from
    String osVersion
    String deviceVandor
    String osName
    String browserName
    String search
    String createtime
}
