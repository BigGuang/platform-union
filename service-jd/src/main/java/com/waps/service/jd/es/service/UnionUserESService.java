package com.waps.service.jd.es.service;

import com.waps.elastic.search.service.ESClient;
import org.springframework.stereotype.Component;

@Component
public class UnionUserESService extends ESClient {

    public UnionUserESService(){
        System.out.println("==UnionUserESService==");
        INDEX_NAME = "union_user";
        TYPE_NAME = "union_user";
        INDEX_MAPPING = "es_mapping/union_user.json";
        this.autoCheck();
    }

}
