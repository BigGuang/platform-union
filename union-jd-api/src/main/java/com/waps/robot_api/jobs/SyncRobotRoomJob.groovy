package com.waps.robot_api.jobs

import com.waps.robot_api.service.TSRobotConfigService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SyncRobotRoomJob {
    @Autowired
    private TSRobotConfigService tsRobotConfigService

    public void doSomething() {

        if(tsRobotConfigService!=null){
            println "开始执行:tsRobotConfigService.syncAllRobotAndRoom()"
            tsRobotConfigService.syncAllRobotAndRoom()
        }else{
            println "tsRobotConfigService is null"
        }
    }

    String jobName="tsRobotConfigService.syncAllRobotAndRoom"
}
