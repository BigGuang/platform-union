package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean


class TSSendTaskESMap extends ESBaseBean{
    int task_status            //发送任务状态，0未发送，1已发送
    int send_day_order  //一天内发送排序
    String send_day //指定发送日期
    String send_time  //当天发送的时间点,可预估
    String target_channel_name    //指定的群号，多个用','分割
    String black_channel_name     //排除的群号，多个用','分割
    String createtime
    List<TSSendMessageESMap> message_list=new ArrayList<>()
}

class TSSendMessageESMap{
//    "nMsgNum": 1,
//    "nMsgType": 2001,
//    "msgContent": "hello",
//    "nVoiceTime": 0,
//    "vcTitle": "",
//    "vcDesc": "",
//    "vcHref": ""
    int nMsgNum     //消息编号(整型,用于区分同一组的消息)
    int nMsgType   //消息类型 2001 文字 2002 图片 2003 语音(只支持amr格式) 2004 视频 2005 链接 2006 好友名片 2010 文件 2013小程序 2016 音乐
    String msgContent   //文字内容（如果是图片或者链接则传图片地址[链接的图片不宜过大，建议160x160px，小于10k];如果是好友名片，则传好友的微信编号；如果是语音,则传语音的地址（语音的后缀必须为amr示例：http://downsc.chinaz.net/Files/DownLoadsound1/201910/12087.amr）;如果是小程序则传小程序的XML；如果是视频消息，则传视频的封面图【视频第一帧的图片链接地址，此处必传，否则视频消息类型会失败】）
    int nVoiceTime    //语音时长/视频时长；当消息类型为以上两种类型时，必须传时长且时长要正确，否则会发送失败，当时长不正确时可能会有很大的禁封风险
    String vcTitle    //链接标题
    String vcDesc    //链接描述
    String vcHref    //链接URL，当消息为视频时，此处传视频的链接地址
}
