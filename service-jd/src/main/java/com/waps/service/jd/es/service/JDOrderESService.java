package com.waps.service.jd.es.service;

import com.waps.elastic.search.service.ESClient;
import org.springframework.stereotype.Component;

@Component
public class JDOrderESService extends ESClient {

    public JDOrderESService(){
        System.out.println("==JDOrderESService==");
        INDEX_NAME = "jd_order_log";
        TYPE_NAME = "jd_order_log";
        INDEX_MAPPING = "es_mapping/jd_order.json";
        this.autoCheck();
    }
}
