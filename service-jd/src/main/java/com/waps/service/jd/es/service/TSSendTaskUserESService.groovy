package com.waps.service.jd.es.service

import com.waps.elastic.search.service.ESClient
import org.springframework.stereotype.Component

@Component
class TSSendTaskUserESService extends ESClient {

    public TSSendTaskUserESService() {
        System.out.println("==TSUserSendTaskESService==");
        INDEX_NAME = "ts_send_task_user";
        TYPE_NAME = "ts_send_task_user";
        INDEX_MAPPING = "es_mapping/ts_send_task.json";
        this.autoCheck()
    }
}
