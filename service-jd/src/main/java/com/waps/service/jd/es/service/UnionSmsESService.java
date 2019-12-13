package com.waps.service.jd.es.service;

import com.waps.elastic.search.service.ESClient;
import org.springframework.stereotype.Component;

@Component
public class UnionSmsESService extends ESClient {

    public UnionSmsESService(){
        System.out.println("==UnionSmsESService==");
        INDEX_NAME = "union_sms";
        TYPE_NAME = "union_sms";
        INDEX_MAPPING = "es_mapping/union_sms.json";
        this.autoCheck();
    }
}
