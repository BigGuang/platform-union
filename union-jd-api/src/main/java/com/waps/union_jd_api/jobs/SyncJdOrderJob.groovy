package com.waps.union_jd_api.jobs

import com.waps.union_jd_api.service.JDOrderLogService
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.text.SimpleDateFormat

@Component
class SyncJdOrderJob {

    @Resource
    private JDOrderLogService jdOrderLogService;

    public void doSomething() {

        if(jdOrderLogService!=null){
            println "开始执行:jdOrderLogService.getOrderLogByDay()"
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd")
            String day=formatter.format(new Date())
            jdOrderLogService.getOrderLogByDay(day)

        }else{
            println "jdOrderLogService is null"
        }
    }

    String jobName="jdOrderLogService_getOrderLogByDay_1min"
}
