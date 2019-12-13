package com.waps.service.jd.es.service;

import com.waps.elastic.search.service.ESClient;
import org.springframework.stereotype.Component;

@Component
public class JDSkuInfoESService extends ESClient {

    public JDSkuInfoESService(){
        System.out.println("==JDSkuInfoESService==");
        INDEX_NAME = "jd_sku_lib";
        TYPE_NAME = "jd_sku_lib";
        INDEX_MAPPING = "es_mapping/jd_sku_lib.json";
        this.autoCheck();
    }
}
