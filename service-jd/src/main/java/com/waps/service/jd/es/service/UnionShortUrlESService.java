package com.waps.service.jd.es.service;

import com.waps.elastic.search.service.ESClient;
import org.springframework.stereotype.Component;

@Component
public class UnionShortUrlESService extends ESClient {

    public UnionShortUrlESService(){
        System.out.println("==UnionShortUrlESService==");
        INDEX_NAME = "union_short_url";
        TYPE_NAME = "union_short_url";
        INDEX_MAPPING = "es_mapping/union_short_url.json";
        this.autoCheck();
    }
}
