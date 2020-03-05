package com.waps.robot_api.service


import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSMessageBean
import com.waps.service.jd.es.domain.RobotSendLogESMap
import com.waps.service.jd.es.domain.TSRoomConfigESMap
import com.waps.service.jd.es.domain.TSSendMessageESMap
import com.waps.service.jd.es.domain.TSSendTaskESMap
import com.waps.service.jd.es.service.RobotSendLogESService
import com.waps.service.jd.es.service.TSSendTaskUserESService
import com.waps.union_jd_api.service.JDConvertLinkService
import com.waps.union_jd_api.service.ResultBean
import com.waps.union_jd_api.utils.DateUtils
import com.waps.utils.StringUtils
import com.waps.utils.TxtUtils
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
    @Autowired
    private RobotSendLogESService robotSendLogESService
    @Autowired
    private TSSendTaskUserESService tsSendTaskUserESService


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
                                //todo:对文字内容做10%差异化处理

                                //发送消息
                                String robot_id = messageTaskBean.getRoomConfigESMap().getVcRobotSerialNo()
                                String room_id = messageTaskBean.getRoomConfigESMap().getVcChatRoomSerialNo()
                                String channel_id = messageTaskBean.getRoomConfigESMap().getChannel_id()
                                String channel_name = messageTaskBean.getRoomConfigESMap().getChannel_name()
                                String action_id = UUID.randomUUID().toString()
                                List<TSMessageBean> messageList = convertTask2Message(channel_name, messageTaskBean.getSendTaskESMap())
                                if (messageList != null && messageList.size() > 0) {
                                    String retJson = tsRobotMessageService.sendChatRoomMessageList(robot_id, room_id, action_id, "", messageList)
                                    JSONObject retObj = JSONObject.parseObject(retJson)
                                    if (retObj != null && retObj.getIntValue("nResult") == 1) {
                                        saveSendLog(messageTaskBean, retJson)
                                    }
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
     * 记录发送日志
     * @param messageTaskBean
     * @param runResultJson
     */
    public void saveSendLog(MessageTaskBean messageTaskBean, String runResultJson) {
        try {
            RobotSendLogESMap robotSendLogESMap = new RobotSendLogESMap()
            TSSendTaskESMap sendTaskESMap = messageTaskBean.getSendTaskESMap()
            TSRoomConfigESMap roomConfigESMap = messageTaskBean.getRoomConfigESMap()
            Long id = System.currentTimeMillis()
            robotSendLogESMap.setId(id + "")
            robotSendLogESMap.setRobot_id(roomConfigESMap.getVcRobotSerialNo())
            robotSendLogESMap.setRoom_id(roomConfigESMap.getVcChatRoomSerialNo())
            robotSendLogESMap.setChannel_name(roomConfigESMap.getChannel_name())
            robotSendLogESMap.setSku_id(sendTaskESMap.getSku_id())
            robotSendLogESMap.setTask_id(sendTaskESMap.getId())
            robotSendLogESMap.setRun_result(runResultJson)
            robotSendLogESMap.setMessage_list(sendTaskESMap.getMessage_list())
            robotSendLogESMap.setCreate_time(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
            robotSendLogESService.save(robotSendLogESMap.getId(), robotSendLogESMap)
            //群主自发消息发送状态更新
            if (!StringUtils.isNull(messageTaskBean.getSendTaskESMap().getTarget_channel_name())) {
                Map updateParams = new HashMap()
                updateParams.put("run_time", DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
                updateParams.put("run_result", runResultJson)
                tsSendTaskUserESService.update(sendTaskESMap.getId(), updateParams)
            }
        } catch (Exception e) {
            println "==saveSendLog ERROR:" + e.getLocalizedMessage()
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
            List<TSSendMessageESMap> message_list = taskESMap.getMessage_list()
            int i = 0
            for (TSSendMessageESMap messageESMap : message_list) {
                if (messageESMap) {
                    i = i + 1
                    TSMessageBean tsMessageBean = new TSMessageBean()
                    tsMessageBean.setnMsgNum(i)
                    tsMessageBean.setnMsgType(messageESMap.getnMsgType())
                    if (tsMessageBean.getnMsgType() == 2001) {
                        ResultBean resultBean = jdConvertLinkService.convertLink(messageESMap.getMsgContent(), channel_name, "true")
                        if (resultBean) {
                            if (!StringUtils.isNull(taskESMap.getTarget_channel_name())) {
                                tsMessageBean.setMsgContent(resultBean.getContent())
                            } else {
                                //如果没有指定群收消息，需要对内容做90%不同处理
                                tsMessageBean.setMsgContent(makeContent2New(resultBean.getContent()))
                            }
                        }
                    } else {
                        if (tsMessageBean.getnMsgType() == 2002) {
                            String sendImg = makeImage2NewUUID(messageESMap.getMsgContent())
                            println "==sendImg:" + sendImg
                            tsMessageBean.setMsgContent(sendImg)
                        } else {
                            tsMessageBean.setMsgContent(messageESMap.getMsgContent())
                        }
                    }
                    tsMessageBean.setnVoiceTime(messageESMap.getnVoiceTime())
                    tsMessageBean.setVcTitle(messageESMap.getVcTitle())
                    tsMessageBean.setVcDesc(messageESMap.getVcDesc())
                    tsMessageBean.setVcHref(messageESMap.getVcHref())
                    messageBeanList.add(tsMessageBean)
                }
            }
        }
        return messageBeanList
    }

    public String makeImage2NewUUID(String oldImageUrl) {
        String ret = makeImage2New(oldImageUrl)
        if (!StringUtils.isNull(ret) && ret.endsWith("/")) {
            String uuid = UUID.randomUUID().toString()
            ret = ret + uuid + ".jpg"
        }
        return ret
    }

    public String makeImage2New(String oldImageUrl) {
        String newImageUrl = ""
        if (!StringUtils.isNull(oldImageUrl) && oldImageUrl.startsWith("http")) {
            try {
                String ret = StringUtils.getUrlTxt("https://api.wapg.cn/union_robot/pic/picConVert?fileurl=" + oldImageUrl)
                if (ret) {
                    newImageUrl = "https://api.wapg.cn/union_robot/" + ret
                }
            } catch (Exception e) {
                newImageUrl = oldImageUrl
                println "makeImage2New ERROR:" + e.getLocalizedMessage()
            }
        } else {
            newImageUrl = oldImageUrl
        }
        return newImageUrl
    }

    /**
     *
     * @return
     */
    public String makeContent2New(String oldContent) {
        TxtUtils textUtils = new TxtUtils()
        String newContent = textUtils.RandomReplaceTxt(oldContent, 80)
        return newContent
    }
}

class MessageTaskBean {
    TSSendTaskESMap sendTaskESMap
    TSRoomConfigESMap roomConfigESMap
    int taskType
}

class MessageSendBean {
    String robot_id
    String room_id
    String toWx_id
    List<TSMessageBean> messageList = new ArrayList<>()
}
