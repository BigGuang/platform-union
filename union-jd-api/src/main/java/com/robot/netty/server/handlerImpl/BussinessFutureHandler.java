package com.robot.netty.server.handlerImpl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waps.service.jd.es.domain.JDMediaInfoESMap;
import com.robot.netty.server.CmdBean;
import com.robot.netty.server.GroupBean;
import com.robot.netty.server.Handler;
import com.robot.netty.server.OnLineService;
import com.waps.union_jd_api.service.JDMediaService;
import com.waps.union_jd_api.service.JDSkuRobotService;
import com.waps.utils.SpringBeanUtils;
import com.waps.utils.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BussinessFutureHandler implements Handler {

    final String CMD_TEST = "test";
    final String CMD_MSG_PERSON = "msg_person";
    final String CMD_MSG_GROUP = "msg_group";
    final String CMD_GET_GROUP = "get_group";
    final String CMD_GET_FRIENDS = "get_friends";
    final String CMD_ADD_FRIEND = "add_friend";
    final String CMD_CLIENT_INFO = "client_info";

    private Handler nextHandler;

    public Handler getNextHandler() {
        return nextHandler;
    }

    public void setNextHandler(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public String hander(String msg) {
        System.out.println("接收到信息：" + msg);
        if (nextHandler != null) {
            //回复信息
            nextHandler.hander(msg);
        }

        //todo 这里添加对收到消息的处理
        if (isJson(msg)) {
            CmdBean cmdBean = JSONObject.parseObject(msg, CmdBean.class);
            if (cmdBean != null) {
                msg = handleCMD(null, cmdBean);
            }
        }
        return getHandle(null, msg);
    }

    @Override
    public String hander(String uuid, String msg) {
        if (nextHandler != null) {
            //回复信息
            nextHandler.hander(msg);
        }


        return getHandle(uuid, msg);
    }

    public String getHandle(String uuid, String msg) {
        String retMsg = "";
        if (isJson(msg)) {
            CmdBean cmdBean = JSONObject.parseObject(msg, CmdBean.class);
            if (cmdBean != null) {
                retMsg = handleCMD(uuid, cmdBean);
            }
        }
        return retMsg;
    }

    public String handleCMD(String uuid, CmdBean cmdBean) {
        String cmd = cmdBean.getCmd();
        if (CMD_TEST.equalsIgnoreCase(cmd)) {
            return null;
        }

        if (CMD_CLIENT_INFO.equalsIgnoreCase(cmd)) {
            String client_info = cmdBean.getData();
            if (!StringUtils.isNull(client_info)) {
                OnLineService.addUserInfo(uuid, client_info);
            }
        }

        if (CMD_GET_GROUP.equalsIgnoreCase(cmd)) {
            String groupInfo = cmdBean.getData();
            if (!StringUtils.isNull(uuid)) {
                GroupBean groupBean = JSONObject.parseObject(groupInfo, GroupBean.class);
                OnLineService.addOnlineGroup(uuid, groupBean);
            }
        }
        if (CMD_GET_FRIENDS.equalsIgnoreCase(cmd)) {
            String frirendsList = cmdBean.getData();
            System.out.println("好友列表");
            System.out.println(frirendsList);
            if (!StringUtils.isNull(uuid)) {
                OnLineService.addFrinds(uuid, frirendsList);
            }
        }

        if (CMD_MSG_PERSON.equalsIgnoreCase(cmd)) {
            String msg = cmdBean.getData();
            System.out.println("个人消息");
//            System.out.println(msg);
        }

        if (CMD_MSG_GROUP.equalsIgnoreCase(cmd)) {
            String msg = cmdBean.getData();
            System.out.println(msg);
            if (!StringUtils.isNull(msg)) {
                msg = msg.replaceAll("True", "'true'").replaceAll("False", "'false'");
                JSONObject jsonObject = JSONObject.parseObject(msg);
                String fromUser = (String) jsonObject.get("FromUserName");
                String toUser = (String) jsonObject.get("ToUserName");
                String isAt = (String) jsonObject.get("isAt");
                String actualNickName = (String) jsonObject.get("ActualNickName");
                String actualUserName = (String) jsonObject.get("ActualUserName");

                String content = (String) jsonObject.get("Content");
                System.out.println("isAt:" + isAt);
                System.out.println("iactualNickNamesAt:" + actualNickName);
                System.out.println("actualUserName:" + actualUserName);
                System.out.println("content:" + content);
                //判断是否@给机器人的信息
                if ("true".equalsIgnoreCase(isAt+"") && !StringUtils.isNull(content)) {
                    content = content.replace("\u2005", " ");

                    String search = content.substring(content.indexOf(" ") + 1, content.length());
                    System.out.println("fromUser:" + fromUser);
                    System.out.println("toUser:" + toUser);
                    System.out.println("search:" + search);

                    GroupBean groupBean = OnLineService.getOnlineGroup(uuid, fromUser);

                    String roomName = "";
                    String channelName = null;
                    String pid = null;

                    if (groupBean != null) {
                        roomName = groupBean.getNickName();
                        System.out.println("匹配群名称:" + groupBean.getNickName());
                        String pattern = "([A-z][0-9][0-9][0-9])";
                        Pattern r = Pattern.compile(pattern);
                        Matcher m = r.matcher(roomName);
                        if (m.find()) {
                            channelName = m.group(0);
                            JDMediaService jdMediaService = (JDMediaService) SpringBeanUtils.getBeanByType(JDMediaService.class);
                            HashMap paramsMap = new HashMap();
                            paramsMap.put("from", 0);
                            paramsMap.put("size", 1);
                            paramsMap.put("channel_name", channelName);
                            List<JDMediaInfoESMap> mediaList = jdMediaService.findMediaChannel(paramsMap);
                            if (mediaList.size() > 0) {
                                JDMediaInfoESMap jdMediaInfoESMaps = mediaList.get(0);
                                pid = jdMediaInfoESMaps.getChannel_id();
                            }
                        }
                    }

                    System.out.println("匹配推广位名称:" + channelName);
                    System.out.println("匹配推广位ID:" + pid);
                    System.out.println("群ID:" + fromUser);
                    if (!StringUtils.isNull(roomName)
                            && !StringUtils.isNull(fromUser)
                            && !StringUtils.isNull(pid)
                    ) {

                        System.out.println("搜索:" + search);

                        JDSkuRobotService jdSkuRobotService = (JDSkuRobotService) SpringBeanUtils.getBeanByType(JDSkuRobotService.class);


                        String text = jdSkuRobotService.getSkuList(pid, search, 6);
                        if (!StringUtils.isNull(actualNickName)) {
                            text = "@" + actualNickName + "  您要的'" + search + "':\n\n" + text;
                        }
                        System.out.println("===查询结果===");
                        System.out.println(text);
                        CmdBean retCmdBean = new CmdBean();
                        retCmdBean.setCmd(cmd);
                        retCmdBean.setParams(fromUser);
                        retCmdBean.setData(text);
                        String retJson = JSONObject.toJSONString(retCmdBean);
                        return retJson;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
            return null;

        }

        if (CMD_ADD_FRIEND.equalsIgnoreCase(cmd)) {
            String msg = cmdBean.getData();
            System.out.println("添加好友");
            System.out.println(msg);
        }

        return "success";
    }


    public final static boolean isJson(String jsonInString) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
