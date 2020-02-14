package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class TSRobotESService extends ESClient {

    public TSRobotESService() {
        System.out.println("==TSRobotESService==");
        INDEX_NAME = "ts_robot";
        TYPE_NAME = "ts_robot";
        INDEX_MAPPING = "es_mapping/ts_robot.json";
        this.autoCheck();
    }
}
