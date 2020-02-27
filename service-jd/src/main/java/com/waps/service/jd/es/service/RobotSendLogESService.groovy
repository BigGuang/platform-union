package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class RobotSendLogESService extends ESClient {

    public RobotSendLogESService() {
        System.out.println("==RobotSendLogESService==")
        INDEX_NAME = "robot_send_log"
        TYPE_NAME = "robot_send_log"
        INDEX_MAPPING = "es_mapping/robot_send_log.json"
        this.autoCheck()
    }
}
