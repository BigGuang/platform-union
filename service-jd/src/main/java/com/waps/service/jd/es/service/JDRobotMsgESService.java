package com.waps.service.jd.es.service;

import com.waps.elastic.search.service.ESClient;
import org.springframework.stereotype.Component;

@Component
public class JDRobotMsgESService extends ESClient {

    public JDRobotMsgESService(){
        System.out.println("==JDRobotMsgESService==");
        INDEX_NAME = "jd_robot_msg_log";
        TYPE_NAME = "jd_robot_msg_log";
        INDEX_MAPPING = "es_mapping/jd_robot_msg_log.json";
        this.autoCheck();
    }
}
