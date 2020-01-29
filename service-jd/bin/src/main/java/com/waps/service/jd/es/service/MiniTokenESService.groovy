package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class MiniTokenESService extends ESClient {

    public MiniTokenESService() {
        System.out.println("==MiniTokenESService==");
        INDEX_NAME = "mini_token";
        TYPE_NAME = "mini_token";
        INDEX_MAPPING = "es_mapping/mini_token.json";
        this.autoCheck();
    }
}
