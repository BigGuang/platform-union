package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class UnionPageConfigESService extends ESClient {
    public UnionPageConfigESService() {
        System.out.println("==UnionPageConfigESService==")
        INDEX_NAME = "union_page_config"
        TYPE_NAME = "union_page_config"
        INDEX_MAPPING = "es_mapping/union_page_config.json"
        this.autoCheck()
    }
}
