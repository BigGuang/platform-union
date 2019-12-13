package com.waps.service.jd.es.service;

import com.waps.elastic.search.service.ESClient;
import org.springframework.stereotype.Component;

@Component
public class JDMediaESService extends ESClient {

    public JDMediaESService() {
        System.out.println("==JDMediaESService==");
        INDEX_NAME = "jd_media";
        TYPE_NAME = "jd_media";
        INDEX_MAPPING = "es_mapping/jd_media.json";
        this.autoCheck();
    }
}
