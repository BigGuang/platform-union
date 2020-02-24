package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class TSRoomConfigESService extends ESClient {

    public TSRoomConfigESService() {
        System.out.println("==TSRoomInfoESService==")
        INDEX_NAME = "ts_room_config"
        TYPE_NAME = "ts_room_config"
        INDEX_MAPPING = "es_mapping/ts_room_config.json"
        this.autoCheck()
    }
}
