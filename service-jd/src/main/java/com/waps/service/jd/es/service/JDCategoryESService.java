package com.waps.service.jd.es.service;

import com.waps.elastic.search.service.ESClient;
import org.springframework.stereotype.Component;

@Component
public class JDCategoryESService extends ESClient {

    public JDCategoryESService() {
        System.out.println("==JDCategoryESService==");
        INDEX_NAME = "jd_category";
        TYPE_NAME = "jd_category";
        INDEX_MAPPING = "es_mapping/jd_category.json";
        this.autoCheck();
    }
}
