package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class TSChatRoomESService extends ESClient {

    public TSChatRoomESService() {
        System.out.println("==TSChatRoomESService==");
        INDEX_NAME = "ts_chat_room";
        TYPE_NAME = "ts_chat_room";
        INDEX_MAPPING = "es_mapping/ts_chat_room.json";
        this.autoCheck();
    }
}
