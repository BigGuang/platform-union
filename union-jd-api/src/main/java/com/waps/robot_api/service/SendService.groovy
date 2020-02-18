package com.waps.robot_api.service

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSMessageBean
import com.waps.service.jd.es.domain.TSRoomInfoESMap
import com.waps.service.jd.es.domain.TSSendTaskESMap
import com.waps.tools.test.TestUtils
import com.waps.union_jd_api.service.JDConvertLinkService
import com.waps.union_jd_api.service.ResultBean
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 并发发送任务
 */
@Component
class SendService {
    @Autowired
    TSRobotMessageService tsRobotMessageService
    @Autowired
    private JDConvertLinkService jdConvertLinkService


    int threadPoolNum = 10
    int threadWaitTime = 1000

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

    public void sendTask(List<MessageTaskBean> send_task_list) {

        try {
            // 开始的倒数锁
            final CountDownLatch begin = new CountDownLatch(1);
            // 结束的倒数锁
            final CountDownLatch end = new CountDownLatch(send_task_list.size())
            // 十名选手
            final ExecutorService exec = Executors.newFixedThreadPool(threadPoolNum)
            for (int index = 0; index < send_task_list.size(); index++) {
                final int NO = index;
                int num = index;

                Runnable run = new Runnable() {
                    public void run() {
                        try {
                            // 如果当前计数为零，则此方法立即返回。
                            // 等待
                            begin.await()
                            MessageTaskBean messageTaskBean = send_task_list.get(NO)
                            if (messageTaskBean != null) {
                                //发送消息
                                String robot_id = messageTaskBean.getRoomInfoESMap().getVcRobotSerialNo()
                                String room_id = messageTaskBean.getRoomInfoESMap().getVcChatRoomSerialNo()
                                String channel_id = messageTaskBean.getRoomInfoESMap().getChannel_id()
                                String channel_name = messageTaskBean.getRoomInfoESMap().getChannel_name()
                                String action_id = UUID.randomUUID().toString()
                                List<TSMessageBean> messageList = convertTask2Message(channel_name, messageTaskBean.getSendTaskESMap())
                                if (messageList != null && messageList.size() > 0) {
                                    tsRobotMessageService.sendChatRoomMessageList(robot_id, room_id, action_id, "", messageList)
                                }
                                Thread.sleep(threadWaitTime)
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

            System.out.println("===SendTask Start===")
            // begin减一，开始游戏
            begin.countDown();
            // 等待end变为0，即所有选手到达终点
            end.await();
            System.out.println("===SendTask Over===")
            exec.shutdown();

        } catch (Exception e) {
            println "### SendTask ERROR ###"
            println e.getLocalizedMessage()
        }

    }

    /**
     * 发送任务中的信息转换成机器人需要的对象
     * @param taskESMap
     * @return
     */
    public List<TSMessageBean> convertTask2Message(String channel_name, TSSendTaskESMap taskESMap) {
//        消息类型
//        2001 文字
//        2002 图片
//        2003 语音(只支持amr格式)
//        2004 视频
//        2005 链接
//        2006 好友名片
//        2010 文件
//        2013 小程序
//        2016 音乐
        List<TSMessageBean> messageBeanList = new ArrayList<>()
        if (taskESMap != null) {
            if (!StringUtils.isNull(taskESMap.getImg_url())) {
                String imgUrl = taskESMap.getImg_url()
                if (imgUrl.indexOf(",") < 0) {
                    imgUrl = imgUrl + ","
                }
                String[] imgList = imgUrl.split(",")
                for (int i = 0; i < imgList.length; i++) {
                    String img = imgList[i]
                    if (!StringUtils.isNull(img)) {
                        if(img.toLowerCase().endsWith(".mp4")){
                            TSMessageBean tsMessageESMap = new TSMessageBean()
                            tsMessageESMap.setnMsgType(2004)
                            tsMessageESMap.setMsgContent("http://jd.wapg.cn/images/icon_3.png")
                            tsMessageESMap.setnVoiceTime(14)
                            tsMessageESMap.setVcHref(img)
                            messageBeanList.add(tsMessageESMap)
                        }else{
                            TSMessageBean tsMessageESMap = new TSMessageBean()
                            tsMessageESMap.setnMsgType(2002)
                            tsMessageESMap.setMsgContent(img)
                            messageBeanList.add(tsMessageESMap)
                        }

                    }
                }
            }
            if (!StringUtils.isNull(taskESMap.getContent()) && StringUtils.isNull(taskESMap.getTitle()) && StringUtils.isNull(taskESMap.getDesc())) {
                TSMessageBean tsMessageESMap = new TSMessageBean()
                tsMessageESMap.setnMsgType(2001)

                println "==channel_name:" + channel_name
                if (!StringUtils.isNull(channel_name)) {
                    //对taskESMap.getContent()内容做强制转链
                    ResultBean resultBean = jdConvertLinkService.convertLink(taskESMap.getContent(), channel_name, "true")
                    println "==转链结果=="
                    TestUtils.outPrint(resultBean)
                    taskESMap.setContent(resultBean.getContent())
                }
                tsMessageESMap.setMsgContent(taskESMap.getContent())
                messageBeanList.add(tsMessageESMap)
            }
            if (!StringUtils.isNull(taskESMap.getHref()) && !StringUtils.isNull(taskESMap.getTitle())) {
                TSMessageBean tsMessageESMap = new TSMessageBean()
                tsMessageESMap.setnMsgType(2005)
                tsMessageESMap.setMsgContent(taskESMap.getHref())
                tsMessageESMap.setVcHref(taskESMap.getHref())
                tsMessageESMap.setVcTitle(taskESMap.getTitle())
                tsMessageESMap.setVcDesc(taskESMap.getDesc())
                messageBeanList.add(tsMessageESMap)
            }
        }
        return messageBeanList
    }
}

class MessageTaskBean {
    TSSendTaskESMap sendTaskESMap
    TSRoomInfoESMap roomInfoESMap
}

class MessageSendBean {
    String robot_id
    String room_id
    String toWx_id
    List<TSMessageBean> messageList = new ArrayList<>()
}
