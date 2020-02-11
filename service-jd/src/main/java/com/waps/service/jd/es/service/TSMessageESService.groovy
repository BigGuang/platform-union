package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class TSMessageESService extends ESClient {

    public TSMessageESService() {
        System.out.println("==TSMessageESService==");
        INDEX_NAME = "ts_message";
        TYPE_NAME = "ts_message";
        INDEX_MAPPING = "es_mapping/ts_message.json";
        this.autoCheck();
    }
}
