package com.waps.union_jd_api.jobs

import com.waps.union_jd_api.service.JtbApiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SyncJTBSendLogJob {
    @Autowired
    JtbApiService jtbApiService

    public void doSomething() {

        if (jtbApiService != null) {
            println "开始执行:SyncJTBSendLogJob.findSendDone()"
            jtbApiService.syncSendDoneJob(1, 100)
        } else {
            println "jdSkuInfoService is null"
        }
    }

    String jobName = "jtbApiService_findSendDone"
}
