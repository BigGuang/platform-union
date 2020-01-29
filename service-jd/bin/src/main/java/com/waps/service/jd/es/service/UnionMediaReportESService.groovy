package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class UnionMediaReportESService extends ESClient {
    public UnionMediaReportESService() {
        System.out.println("==UnionMediaReportESService==")
        INDEX_NAME = "union_media_report"
        TYPE_NAME = "union_media_report"
        INDEX_MAPPING = "es_mapping/union_media_report.json"
        this.autoCheck()
    }
}
