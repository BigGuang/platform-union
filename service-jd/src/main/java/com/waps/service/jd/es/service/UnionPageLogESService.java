package com.waps.service.jd.es.service;

import com.waps.elastic.search.service.ESClient;
import org.springframework.stereotype.Component;

@Component
public class UnionPageLogESService extends ESClient {

    public UnionPageLogESService(){
        System.out.println("==UnionShortUrlESService==");
        INDEX_NAME = "union_page_log";
        TYPE_NAME = "union_page_log";
        INDEX_MAPPING = "es_mapping/union_page_log.json";
        this.autoCheck();
    }
}
