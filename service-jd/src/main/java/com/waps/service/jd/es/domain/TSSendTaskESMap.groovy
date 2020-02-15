package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean


class TSSendTaskESMap extends ESBaseBean{
    String content
    String img_url             //图片地址，多个用','分割
    String title               //标题，用在一些消息类型
    String desc                //说明内容，用在链接等消息类型
    int send_type             //以什么类型发送，区分文本，链接，图片以及小程序
    int task_status            //发送任务状态，0未发送，1已发送
    int send_day_order  //一天内发送排序
    String send_day //指定发送日期
    String send_time  //当天发送的时间点,可预估
    String target_channel_name    //指定的群号，多个用','分割
    String black_channel_name     //排除的群号，多个用','分割
    String createtime
}
