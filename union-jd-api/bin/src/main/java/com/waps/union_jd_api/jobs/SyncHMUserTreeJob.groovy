package com.waps.union_jd_api.jobs

import com.waps.union_jd_api.service.HemaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SyncHMUserTreeJob {

    @Autowired
    private HemaService hemaService

    public void doSomething() {

        if(hemaService!=null){
            println "开始执行:hemaService.startSyncHemaUserTree()"
            hemaService.startSyncHemaUserTree()
        }else{
            println "hemaService is null"
        }
    }

    String jobName="hemaService.startSyncHemaUserTree_1hour"
}
