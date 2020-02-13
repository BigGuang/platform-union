package com.waps.robot_api.utils

class TSApiConfig {
    final static String merchant_ID="202002050200001"
    final static String merchant_KEY="KnST6x3YucETLjkzokJQNlnWfjfR5Vm1"
    final static String[] robotList=['0AE8CA82E253061F10A63B91AE9577DE','64000270CCC99BCCDD4F33F95DD37F7F']

    final static String AUTH_HOST="http://auth.opsdns.cc:8081"
    final static String SERVICE_HOST="http://merchant.opsdns.cc:8081"

    final static String AUTH_TOKEN_URL=AUTH_HOST+"/api/oauth/get_token"   //merchant  //secret  //token值2个小时后，将会自动失效, 每天获取Token的次数上限为100次

    //【同步调用】获取机器人信息接口，要传机器人编号
    final static String ROBOT_INFO_LIST=SERVICE_HOST+"/api/Robot/MerchantRobotList?vcToken={TOKEN}"


    //【同步调用】获取机器人好友列表接口
    final static String ROBOT_FRIEND_LIST_URL=SERVICE_HOST+"/api/Friend/GetFriendList?vcToken={TOKEN}"
    //机器人删除联系人好友接口
    final static String ROBOT_FRIEND_DeleteContact=SERVICE_HOST+"/api/Friend/DeleteContact?vcToken={TOKEN}"
    //【同步调用】设置是否自动通过好友请求接口
    final static String ROBOT_SETTING_AutoAddFriendSetup=SERVICE_HOST+"/api/Friend/AutoAddFriendSetup?vcToken={TOKEN}"
    //【同步调用】设置是否自动进入群聊接口
    final static String ROBOT_SETTING_AutoJoinChatRoomSetup=SERVICE_HOST+"/api/Friend/AutoJoinChatRoomSetup?vcToken={TOKEN}"
    // 通过好友请求
    final static String ROBOT_SETTING_AcceptNewFriendRequest=SERVICE_HOST+"/api/Friend/AcceptNewFriendRequest?vcToken={TOKEN}"




    //修改机器人个性签名
    final static String ROBOT_SETTING_ModifyProfileWhatsUp=SERVICE_HOST+"/api/Robot/ModifyProfileWhatsUp?vcToken={TOKEN}"
    //修改机器人昵称
    final static String ROBOT_SETTING_ModifyProfileName=SERVICE_HOST+"/api/Robot/ModifyProfileName?  ={TOKEN}"
    //修改机器人头像
    final static String ROBOT_SETTING_ModifyProfileHeadImg=SERVICE_HOST+"/api/Robot/ModifyProfileHeadImg?vcToken={TOKEN}"
    //修改机器人性别
    final static String ROBOT_SETTING_ModifyProfileGender=SERVICE_HOST+"/api/Robot/ModifyProfileGender?vcToken={TOKEN}"



    //获取群列表接口

    final static String ROBOT_INFO_GetChatRoomList=SERVICE_HOST+"/api/ChatRoom/GetChatRoomList?vcToken={TOKEN}"
    //机器人通过好友群邀请接口
    final static String ROBOT_CHATROOM_RobotPullGroupAdopt=SERVICE_HOST+"/api/ChatRoom/RobotPullGroupAdopt?vcToken={TOKEN}"
    //【异步调用】获取群成员信息列表接口
    final static String ROBOT_CHATROOM_GetChatRoomUserInfo=SERVICE_HOST+"/api/ChatRoom/GetChatRoomUserInfo?vcToken={TOKEN}"
    //群消息开通接收，也就是设置isOpenMessage
    final static String ROBOT_CHATROOM_RobotChatRoomOpen=SERVICE_HOST+"/api/ChatRoom/RobotChatRoomOpen?vcToken={TOKEN}"
    //群消息注销接收，也就是设置isOpenMessage
    final static String ROBOT_CHATROOM_RobotChatRoomCancel=SERVICE_HOST+"/api/ChatRoom/RobotChatRoomCancel?vcToken={TOKEN}"




    //【异步调用】机器人私聊发消息接口
    final static String ROBOT_MESSAGE_SendPrivateChatMessages=SERVICE_HOST+"/api/ChatMessages/SendPrivateChatMessages?vcToken={TOKEN}"
    //【异步调用】群消息发送接口
    final static String ROBOT_MESSAGE_SendGroupChatMessages=SERVICE_HOST+"/api/ChatMessages/SendGroupChatMessages?vcToken={TOKEN}"
}
