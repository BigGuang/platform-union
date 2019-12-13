package com.robot.netty.server;

import com.waps.service.jd.es.domain.JDCategoryESMap;
import com.waps.service.jd.es.domain.JDMediaInfoESMap;
import io.netty.channel.socket.SocketChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OnLineService {
    private static Map<String, SocketChannel> channelMap = new ConcurrentHashMap<>();
    private static Map<String, String> clientInfoMap = new ConcurrentHashMap<>();
    private static Map<String, String> groupInfoMap = new ConcurrentHashMap<>();
    private static Map<String, String> frindsMap = new ConcurrentHashMap<>();
    private static Map<String, Map<String, GroupBean>> onLineGroupListMap = new ConcurrentHashMap<>();
    private static Map<String, JDMediaInfoESMap> channelInfoMap = new ConcurrentHashMap<>();
    private static Map<String, JDCategoryESMap> jdCategoryMap = new ConcurrentHashMap<>();

    public static void addSocketChannel(String uuid, SocketChannel gateway_channel) {
        channelMap.put(uuid, gateway_channel);
        clientInfoMap.put(uuid, "");
    }

    public static void addUserInfo(String uuid, String clientInfo) {
        clientInfoMap.put(uuid, clientInfo);
    }

    public static void addChannelInfo(String channelId, JDMediaInfoESMap jdMediaInfoESMap) {
        channelInfoMap.put(channelId, jdMediaInfoESMap);
    }

    public static void addJdCategory(String channelId, JDCategoryESMap jdCategoryESMap) {
        jdCategoryMap.put(channelId, jdCategoryESMap);
    }

    public static void addGroupInfo(String uuid, String groupInfo) {
        groupInfoMap.put(uuid, groupInfo);
    }

    public static void addOnlineGroup(String uuid, GroupBean groupBean) {
        Map<String, GroupBean> groupBeanMap = onLineGroupListMap.get(uuid);
        if (groupBeanMap == null) {
            groupBeanMap = new HashMap<>();
        }
        groupBeanMap.put(groupBean.getUserName(), groupBean);
        onLineGroupListMap.put(uuid, groupBeanMap);
    }

    public static void addFrinds(String uuid, String groupInfo) {
        frindsMap.put(uuid, groupInfo);
    }

    public static Map<String, SocketChannel> getSocketChannelMap() {
        return channelMap;
    }

    public static Map<String, String> getClientInfoMap() {
        return clientInfoMap;
    }

    public static Map<String, String> getGroupInfoMap() {
        return groupInfoMap;
    }

    public static Map<String, String> getFrindsMap() {
        return frindsMap;
    }

    public static Map<String, JDMediaInfoESMap> getChannelInfoMap() {
        return channelInfoMap;
    }

    public static Map<String, JDCategoryESMap> getJdCategoryMap(){
        return jdCategoryMap;
    }

    public static Map<String, Map<String, GroupBean>> getOnLineGroupListMap() {
        return onLineGroupListMap;
    }

    public static SocketChannel getSocketChannel(String uuid) {
        return channelMap.get(uuid);
    }

    public static String getClientInfo(String uuid) {
        return clientInfoMap.get(uuid);
    }

    public static String getGroupInfo(String uuid) {
        return groupInfoMap.get(uuid);
    }

    public static String getFriends(String uuid) {
        return frindsMap.get(uuid);
    }

    public static Map<String, GroupBean> getOnlineGroupList(String uuid) {
        return onLineGroupListMap.get(uuid);
    }

    public static GroupBean getOnlineGroup(String uuid, String roomId) {
        Map<String, GroupBean> map = onLineGroupListMap.get(uuid);
        if (map != null) {
            return map.get(roomId);
        } else {
            return null;
        }
    }

    public static JDMediaInfoESMap getChennelInfo(String channelId) {
        return channelInfoMap.get(channelId);
    }

    public static JDCategoryESMap getJdCategory(String cid){
        return jdCategoryMap.get(cid);
    }

    public static void removeSocketChannel(String uuid) {
        channelMap.remove(uuid);
        clientInfoMap.remove(uuid);
        groupInfoMap.remove(uuid);
        frindsMap.remove(uuid);
        onLineGroupListMap.remove(uuid);
    }
}
