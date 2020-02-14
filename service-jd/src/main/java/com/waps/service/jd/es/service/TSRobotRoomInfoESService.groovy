package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class TSRobotRoomInfoESService extends ESClient {

    public TSRobotRoomInfoESService() {
        System.out.println("==TSRobotRoomInfoESService==");
        INDEX_NAME = "ts_room_info";
        TYPE_NAME = "ts_room_info";
        INDEX_MAPPING = "es_mapping/ts_room_info.json";
        this.autoCheck();
    }
}
