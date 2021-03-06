package com.waps.union_jd_api.utils

class Config {

    final static String USER_ROLE_LEVEL_0 = "-1"
    final static String USER_ROLE_LEVEL_1 = "0"
    final static String USER_ROLE_LEVEL_2 = "1"
    final static String USER_ROLE_LEVEL_3 = "2"

    final static String HOST = "https://api.wapg.cn/jd_union/"

    final static String UNION_USER_TREE_URL = "https://api.wapg.cn/jd/UserLevel?rnum=[RNUM]&md5=[MD5]&uid=[UID]";

    final static String HEMA_SYNC_USER_TREE_URL = "https://hmxx.hemabuluo.com/hmxxApi/admin/jdCode/queryJdCode";

    final static String SYNC_USER_TREE_URL = "https://api.wapg.cn/jd/hemaPsync?md5=[MD5]&gid=[GID]&fid=[FID]&rnum=[RNUM]&uid=[UID]";

    final static String MINI_GET_OPEN_ID = "https://api.weixin.qq.com/sns/jscode2session";
    public static final MINI_GET_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=[APPID]&secret=[APPSECRET]"
    public static final MINI_POST_QR_URL = "http://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=[ACCESS_TOKEN]"


    //小程序模版消息下发接口，场景受限
    final static String SEND_MINI_TEMPLATE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=ACCESS_TOKEN"
    //下发小程序和公众号统一的服务消息
    final static String SEND_UNIFORM_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send?access_token=ACCESS_TOKEN"
    //订阅消息下发接口
    final static String SEND_SUBSCRIBE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=ACCESS_TOKEN"



    //京推宝API
    final static String JTB_LOGIN_ACCOUNT_NAME="13801205108"
    final static String JTB_LOGIN_ACCOUNT_PWD="744ba1ad5020cb4a60b68ea28cf63cf9"
    final static String JTB_LOGIN_ACCOUNT_URL="https://jingtuibao.ixiaocong.net/api/login"
    final static String JTB_LOGIN_SESSION_ID_URL="https://jingtuibao.ixiaocong.net/api/loginBySessionId"
    final static String JTB_UPLOAD_IMG_URL="https://jingtuibao.ixiaocong.net/api/upload/robot/image"
    final static String JTB_ADD_SEND_URL="https://jingtuibao.ixiaocong.net/api/robotMessage/add"
    final static String JTB_SEND_WAITING_URL="https://jingtuibao.ixiaocong.net/api/robotMessage/findSendWating"
    final static String JTB_SEND_ED_URL="https://jingtuibao.ixiaocong.net/api/robotMessage/findSended"
    final static String JTB_MESSAGE_DEL_URL="https://jingtuibao.ixiaocong.net/api/robotMessage/delete"
    final static String JTB_MESSAGE_UPDATE_URL="https://jingtuibao.ixiaocong.net/api/robotMessage/update"


}
