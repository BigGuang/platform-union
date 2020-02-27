package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean

class RobotSendLogESMap extends ESBaseBean{
    String robot_id
    String room_id
    String channel_name
    String task_id
    String sku_id
    String create_time
}
