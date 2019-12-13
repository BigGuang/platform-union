package com.waps.union_jd_api.jobs

import com.waps.union_jd_api.service.JDCategoryService
import org.springframework.stereotype.Component

import javax.annotation.Resource
@Component
class SyncJdCategoryJob {

    @Resource
    private JDCategoryService jdCategoryService

    public void doSomething() {

        if(jdCategoryService!=null){
            println "开始执行:jdCategoryService.startSyncAllCategory()"
            jdCategoryService.startSyncAllCategory()

        }else{
            println "jdCategoryService is null"
        }
    }

    String jobName="jdCategoryService_startSyncAllCategory_1min"
}
