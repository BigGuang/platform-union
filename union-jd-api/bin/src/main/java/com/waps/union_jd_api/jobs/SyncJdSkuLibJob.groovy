package com.waps.union_jd_api.jobs

import com.waps.union_jd_api.service.JDSkuInfoService
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Component
class SyncJdSkuLibJob {

    @Resource
    private JDSkuInfoService jdSkuInfoService

    public void doSomething() {

        if (jdSkuInfoService != null) {
            println "开始执行:jdSkuInfoService.syncSkuInfo()"
            jdSkuInfoService.synxSkuInfoAll()

        } else {
            println "jdSkuInfoService is null"
        }
    }

    String jobName = "jdSkuInfoService_syncSkuInfo"
}
