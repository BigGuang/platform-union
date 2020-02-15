package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class TSSendTaskESService extends ESClient {

    public TSSendTaskESService() {
        System.out.println("==TSSendTaskESService==");
        INDEX_NAME = "ts_send_task";
        TYPE_NAME = "ts_send_task";
        INDEX_MAPPING = "es_mapping/ts_send_task.json";
        this.autoCheck();
    }
}
