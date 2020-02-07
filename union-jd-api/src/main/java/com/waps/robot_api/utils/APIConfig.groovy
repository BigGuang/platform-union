package com.waps.robot_api.utils

class APIConfig {
    final static String merchant_ID="202002050200001"
    final static String merchant_KEY="KnST6x3YucETLjkzokJQNlnWfjfR5Vm1"
    final static String[] robotList=['0AE8CA82E253061F10A63B91AE9577DE','64000270CCC99BCCDD4F33F95DD37F7F']

    final static String AUTH_HOST="http://auth.opsdns.cc:8081"
    final static String SERVICE_HOST="http://merchant.opsdns.cc:8081"

    final static String AUTH_TOKEN_URL=AUTH_HOST+"/api/oauth/get_token"   //merchant  //secret  //token值2个小时后，将会自动失效, 每天获取Token的次数上限为100次

    //【同步调用】获取机器人信息接口，要传机器人编号
    final static String ROBOT_INFO_LIST=SERVICE_HOST+"/api/Robot/MerchantRobotList?vcToken={TOKEN}"
    final static String ROBOT_FRIEND_LIST_URL=SERVICE_HOST+"/api/Friend/GetFriendList?vcToken={TOKEN}"
}
