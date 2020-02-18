package com.waps.robot_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSMessageBean
import com.waps.utils.StringUtils
import org.springframework.stereotype.Component

import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Component
class TestTaskService {

    int threadPoolNum = 10
    int threadWaitTime = 2000

    public setParams(String params) {
        try {
            if (!StringUtils.isNull(params)) {
                JSONObject jsonObject = JSONObject.parseObject(params)
                int pool_num = jsonObject.getInteger("pool_num")
                if (pool_num && pool_num >= 5) {
                    threadPoolNum = pool_num
                }
                int wait_time = jsonObject.getInteger("wait_time")
                if (wait_time && wait_time >= 500) {
                    threadWaitTime = wait_time
                }
            }
        } catch (Exception e) {
            println "params to json error"
        }
    }

    public void testTask(List<String> test_list) {

        try {
            // 开始的倒数锁
            final CountDownLatch begin = new CountDownLatch(1);
            // 结束的倒数锁
            final CountDownLatch end = new CountDownLatch(test_list.size())
            // 十名选手
            final ExecutorService exec = Executors.newFixedThreadPool(threadPoolNum)
            for (int index = 0; index < test_list.size(); index++) {
                final int NO = index;
                int num = index;

                Runnable run = new Runnable() {
                    public void run() {
                        try {
                            // 如果当前计数为零，则此方法立即返回。
                            // 等待
                            begin.await()
                            String content = test_list.get(NO)
                            if (content != null) {
                                //处理区域
                                println content
                                Thread.sleep(threadWaitTime);
                            }

                        } catch (Exception e) {
                            System.out.println("Runnable sendTask ERROR:" + e.getLocalizedMessage());
                        } finally {
                            // 每个选手到达终点时，end就减一
                            end.countDown();
                        }
                    }
                };
                exec.submit(run);
            }

            System.out.println("===TestTask Start===")
            // begin减一，开始游戏
            begin.countDown();
            // 等待end变为0，即所有选手到达终点
            end.await();
            System.out.println("===TestTask Over===")
            exec.shutdown();

        } catch (Exception e) {
            println "### TestTask ERROR ###"
            println e.getLocalizedMessage()
        }

    }
}
