package com.waps.union_jd_api.service;
import org.springframework.stereotype.Component;

@Component
public class SocketMessageService {

    SocketMessageService(){
        System.out.println("==SocketMessageService==");
    }

    /**
     * 接收消息处理
     */
    public String receiveMessageHandle(String message) {
        return "success";
    }
}
