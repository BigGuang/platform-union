package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class UnionActionESService extends ESClient {

    public UnionActionESService() {
        System.out.println("==UnionActionESService==");
        INDEX_NAME = "union_action";
        TYPE_NAME = "union_action";
        INDEX_MAPPING = "es_mapping/union_action.json";
        this.autoCheck();
    }
}

