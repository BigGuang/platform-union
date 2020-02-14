package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class TSAuthTokenESService extends ESClient {

    public TSAuthTokenESService() {
        System.out.println("==TSAuthTokenESService==");
        INDEX_NAME = "ts_auth_token";
        TYPE_NAME = "ts_auth_token";
        INDEX_MAPPING = "es_mapping/ts_auth_token.json";
        this.autoCheck();
    }
}
