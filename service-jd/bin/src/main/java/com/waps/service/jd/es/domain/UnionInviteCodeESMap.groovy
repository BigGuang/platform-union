package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class UnionInviteCodeESMap extends ESBaseBean {
    String invite_code
    String channel_id
    String channel_name
    String f_open_id
    String t_open_id
    String createtime
}
