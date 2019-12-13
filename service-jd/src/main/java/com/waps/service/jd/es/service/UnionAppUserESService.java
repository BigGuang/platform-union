package com.waps.service.jd.es.service;

import com.waps.elastic.search.service.ESClient;
import org.springframework.stereotype.Component;

@Component
public class UnionAppUserESService extends ESClient {

    public UnionAppUserESService() {
        System.out.println("==UnionUserESService==");
        INDEX_NAME = "union_app_user";
        TYPE_NAME = "union_app_user";
        INDEX_MAPPING = "es_mapping/union_app_user.json";
        this.autoCheck();
    }

}
