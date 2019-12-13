package com.waps.service.jd.es.service;

import com.waps.elastic.search.service.ESClient;
import org.springframework.stereotype.Component;

@Component
public class UnionInviteCodeESService extends ESClient {

    public UnionInviteCodeESService() {
        System.out.println("==UnionInviteCodeESService==");
        INDEX_NAME = "union_invite_code";
        TYPE_NAME = "union_invite_code";
        INDEX_MAPPING = "es_mapping/union_invite_code.json";
        this.autoCheck();
    }
}
