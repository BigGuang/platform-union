package com.waps.union_jd_api.bean

class WeChatMessageBean {
    String messageId
    String chatId
    String roomTopic
    String roomID
    String contactName
    String contactId
    MessagePayload payload
    int type
    double timestamp
    String token
    String msgFrom
}

class SendMessageBean{
//    “chatId”: “bcdw2j234ko1”,
//    “token”: “abcd”,
//    “messageType”: 1, // MessageType, check below
//    “payload”: {
//        “text”: “Hello World”
//    }

    String chatId
    String token
    int messageType
    MessagePayload payload
}


class MessagePayload {
    String text
    String sourceUrl
    String summary
    String voiceUrl
    String imageUrl
    String videoUrl
    String title
    String description
    String thumbnailUrl
    String url
    String name
    String fileUrl
    String content
    String[] mention
}

class SendWxMessageBean{
    String toUserName
    String text
    String site='wpjx'
    String image
}