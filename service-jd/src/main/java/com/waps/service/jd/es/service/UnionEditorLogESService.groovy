package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class UnionEditorLogESService extends ESClient {
    public UnionEditorLogESService() {
        System.out.println("==UnionEditorLogESService==");
        INDEX_NAME = "union_editor_log";
        TYPE_NAME = "union_editor_log";
        INDEX_MAPPING = "es_mapping/union_editor_log.json";
        this.autoCheck();
    }
}
