package com.waps.robot_api.jobs

import com.waps.robot_api.service.TSRobotConfigService
import com.waps.robot_api.service.TSSendTaskService
import com.waps.robot_api.service.TestTaskService
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component

@Component
class JobHandle {

    @Autowired
    private TSSendTaskService tsSendTaskService
    @Autowired
    private TSRobotConfigService tsRobotConfigService

    @Autowired
    private TestTaskService testTaskService
    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("sendTaskJob")
    public ReturnT<String> sendTaskJob(String param) throws Exception {
        String catalinaHome = System.getProperty("catalina.home")
        println "==" + catalinaHome + "  收到sendTaskJob任务调度:" + param
        println "==调度:sendTask2Room()"
        tsSendTaskService.sendTask2Room()
        return ReturnT.SUCCESS;
    }

    @XxlJob("testTaskJob")
    public ReturnT<String> testTaskJob(String param) throws Exception {
        String catalinaHome = System.getProperty("catalina.home")
        println "==" + catalinaHome + "  收到testTaskJob任务调度:" + param
        println "==调度:testTask()"
        List<String> test_list = new ArrayList<>()
        for (int i = 0; i < 1000; i++) {
            String content = "test_" + i
            test_list.add(content)
        }
        testTaskService.setParams(param)
        testTaskService.testTask(test_list)
        return ReturnT.SUCCESS;
    }

    @XxlJob("syncRobotRoomJob")
    public ReturnT<String> syncRobotRoomJob(String param) throws Exception {
        println "==收到syncRobotRoomJob任务调度:" + param
        tsRobotConfigService.syncAllRobotAndRoom()
        return ReturnT.SUCCESS;
    }


    /**
     * 2、分片广播任务
     */
    @XxlJob("shardingJobHandler")
    public ReturnT<String> shardingTaskJob(String param) throws Exception {

        println "==分片广播任务 被调度:" + param
        return ReturnT.SUCCESS;
    }

    /**
     * 3、命令行任务
     */
    @XxlJob("commandJobHandler")
    public ReturnT<String> commandJobHandler(String param) throws Exception {
        println "==命令行任务 被调度:" + param
        return IJobHandler.SUCCESS;
    }

    /**
     * 4、跨平台Http任务
     */
    @XxlJob("httpJobHandler")
    public ReturnT<String> httpJobHandler(String param) throws Exception {

        println "==跨平台Http任务 被调度:" + param
        return ReturnT.SUCCESS;
    }
}
