package com.waps.union_jd_api.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.robot.netty.server.CmdBean;
import com.robot.netty.server.GroupBean;
import com.robot.netty.server.OnLineService;
import com.waps.union_jd_api.service.JDSkuRobotService;
import com.waps.utils.ResponseUtils;
import com.waps.utils.StringUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/robot")
public class JdSkuRobotController {
    @Resource
    private JDSkuRobotService jdSkuRobotService;


    @RequestMapping(value = "/sku_search", method = RequestMethod.GET)
    public void sku_search(
            @RequestParam(value = "s", required = true) String searchWord,
            @RequestParam(value = "pid", required = true) String pid,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        //先暂不分词
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 5;

        String text = jdSkuRobotService.getSkuList(pid, searchWord, 6);
        ResponseUtils.write(response, text);
    }

    @RequestMapping(value = "/get_client_list", method = RequestMethod.GET)
    public void sku_search(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ArrayList<Map> clientList = new ArrayList<>();
        Map<String, String> clientMap = OnLineService.getClientInfoMap();
        Iterator it = clientMap.keySet().iterator();
        while (it.hasNext()) {
            Map client = new HashMap();
            String uuid = (String) it.next();
            client.put("uuid", uuid);
            String clientInfoJson = clientMap.get(uuid);
            if (!StringUtils.isNull(clientInfoJson)) {
                try {
                    JSONObject infoObj = JSONObject.parseObject(clientInfoJson);
                    client.put("info", infoObj);
                } catch (Exception e) {
                    System.out.println("解析client info 错误:" + e.getLocalizedMessage());
                }
            }

            //群列表
            Map<String, GroupBean> groupMap = OnLineService.getOnlineGroupList(uuid);
            if (groupMap != null) {
                ArrayList<GroupBean> roomList = new ArrayList<>();
                try {
                    Iterator _it = groupMap.keySet().iterator();
                    while (_it.hasNext()) {
                        String key = (String) _it.next();
                        GroupBean groupBean = groupMap.get(key);
                        roomList.add(groupBean);
                    }
//                    groupJson = groupJson.replace("None", "false");
                    client.put("group", roomList);
                } catch (Exception e) {
                    System.out.println("群列表获取错误:" + e.getLocalizedMessage());
                }
            }

            //好友列表
            String friends_Json = OnLineService.getFriends(uuid);
            if (!StringUtils.isNull(friends_Json)) {
                System.out.println(friends_Json);
                try {
//                    friends_Json = friends_Json.replace("None", "false");
                    JSONArray friendArrayObj = JSONArray.parseArray(friends_Json);
                    client.put("friends", friendArrayObj);
                } catch (Exception e) {
                    System.out.println("解析group 错误:" + e.getLocalizedMessage());
                    System.out.println(friends_Json);
                }
            }
            clientList.add(client);
        }
        String json = JSONObject.toJSONString(clientList);
        ResponseUtils.write(response, json);
    }

    @RequestMapping(value = "/send_msg")
    public void robotSendMessage(
            @RequestParam(value = "uuid", required = false) String uuid,
            @RequestBody CmdBean cmdBean,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String jsonStr = JSONObject.toJSONString(cmdBean);
        if (!StringUtils.isNull(uuid)) {
            SocketChannel channel = OnLineService.getSocketChannel(uuid);
            channel.writeAndFlush(jsonStr + '\n').addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println(uuid + " 发送完成");
                }
            });
        } else {
            Map<String, SocketChannel> map = OnLineService.getSocketChannelMap();
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                SocketChannel channel = map.get(key);
                channel.writeAndFlush(jsonStr + '\n').addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        System.out.println(key + " 发送完成");
                    }
                });
            }
        }
        ResponseUtils.write(response, "ok");
    }
}
