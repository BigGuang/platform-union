package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class TSCallBackLogESService extends ESClient {

    public TSCallBackLogESService() {
        System.out.println("==TSCallBackLogESService==");
        INDEX_NAME = "ts_callback_log";
        TYPE_NAME = "ts_callback_log";
        INDEX_MAPPING = "es_mapping/ts_callback_log.json";
        this.autoCheck();
    }
}
