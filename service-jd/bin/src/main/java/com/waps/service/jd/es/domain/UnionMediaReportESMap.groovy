package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class UnionMediaReportESMap extends ESBaseBean {

    String media_id
    String media_name
    String channel_id
    String channel_name
    int click_num
    int in_order_num
    double in_order_price
    int complete_order_num
    double complete_order_price
    double estimate_price
    String log_time
    String createtime

}
