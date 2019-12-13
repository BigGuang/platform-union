package com.waps.union_jd_api.service;

import org.springframework.stereotype.Component;

@Component
public class JDMessageService {

    JDMessageService(){
        System.out.println("==JDMessageService==");
    }

    /**
     * 接收消息处理
     */
    public String receiveMessageHandle(String message) {
        System.out.println(message);
        return "success";
    }
}
