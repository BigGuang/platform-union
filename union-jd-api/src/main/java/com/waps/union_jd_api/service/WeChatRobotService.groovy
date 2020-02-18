package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.es.domain.JDMediaInfoESMap
import com.waps.service.jd.es.domain.JDRobotMsgESMap
import com.waps.service.jd.es.domain.UnionUserESMap
import com.waps.service.jd.es.service.JDRobotMsgESService
import com.waps.union_jd_api.bean.MessagePayload
import com.waps.union_jd_api.bean.SendMessageBean
import com.waps.union_jd_api.bean.SendWxMessageBean
import com.waps.union_jd_api.bean.WeChatMessageBean
import com.waps.union_jd_api.utils.DateUtils
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import com.waps.utils.TemplateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.regex.Matcher
import java.util.regex.Pattern

@Component
class WeChatRobotService {

    public final static String TOKEN = "5d55fffb1fb2c366fc8b8bda"

    final static String DEFAULT_CHANNEL_NAME = "A002"  //A002

    final static String POST_MESSAGE_URL = "https://ex-api.botorange.com/message/send"
    final static String POST_WECHAT_PUBLIC_ACCOUNT_URL = "http://wx.wapg.cn/send"

    final static String CMD_HELP = "帮助,help"
    final static String CMD_CREATE_CHANNEL = "建群"
    final static String CMD_RED_PACKET = "红包"

    @Autowired
    JDSkuRobotService jdSkuRobotService

    @Autowired
    JDMediaService jdMediaService

    @Autowired
    JDRobotMsgESService jdRobotMsgESService

    @Autowired
    UnionUserService unionUserService

    @Autowired
    JDConvertLinkService jdConvertLinkService

    /**
     * 收到消息
     * @param messageJsonStr
     * @return
     */
    public String getMessage(String messageJsonStr) {
        int sendMessageFlg = 0
        String getRoomMsg = "false"
        boolean isRoomMsg = false
        String msg_type = "room"
        WeChatMessageBean weChatMessageBean = JSONObject.parseObject(messageJsonStr, WeChatMessageBean.class)
        println JSONObject.toJSONString(weChatMessageBean)
        String messageId = weChatMessageBean.getMessageId()
        String chatId = weChatMessageBean.getChatId()
        String contactId = weChatMessageBean.getContactId()
        String contactName = weChatMessageBean.getContactName()
        String roomName = weChatMessageBean.getRoomTopic()
        String roomID = weChatMessageBean.getRoomID()
        String msgFrom = weChatMessageBean.getMsgFrom()
        if (!StringUtils.isNull(msgFrom) && msgFrom.equalsIgnoreCase("wx")) {
            System.out.println("==" + com.waps.utils.DateUtils.getNow() + " 收到微信消息 ==");
        } else {
            System.out.println("==" + com.waps.utils.DateUtils.getNow() + " 收到juzi消息 ==");
        }
        //接收到的内容
        String content = weChatMessageBean.getPayload().getText()
        int type = weChatMessageBean.getType()
        String token = weChatMessageBean.getToken()

        if (!StringUtils.isNull(roomID)) {
            getRoomMsg = "true"
            isRoomMsg = true
        } else {
            getRoomMsg = "false"
            isRoomMsg = false
        }


        //文本
        if (type == 7) {

            //判断文字中是否有敏感词
            if (RobotConfigService.hasBlackWords(content) && !isRoomMsg) {
                return
            }

            //查找群对应广告位信息
            String channelName = getChannelName(roomName)
            boolean isBlack = RobotConfigService.isBlackChannel(channelName)

            if (isBlack) {
                println channelName + " isBlack:" + isBlack
                return
            }


            //指定生成渠道链接
            String assignChannelName = getAssignChannelName(content)
            if (!StringUtils.isNull(assignChannelName)) {
                println "==指定渠道:" + assignChannelName + "=="
                channelName = assignChannelName
                content = content.replaceAll(assignChannelName, "")
            }

            String pid = null
            String domain = null


            if (isRoomMsg) {
                msg_type = "room"
            } else {
                msg_type = "person"
                //通过用户信息找渠道信息， 暂不使用
//                if (StringUtils.isNull(channelName)) {
//                    UnionUserESMap userESMap = unionUserService.loadUserByWXID(contactId)
//                    channelName = userESMap.getChannel_name()
//                }

            }

            JDMediaInfoESMap jdMediaInfoESMaps = getPidFromChannelName(channelName)

            if (jdMediaInfoESMaps != null) {
                pid = jdMediaInfoESMaps.getChannel_id()
                domain = jdMediaInfoESMaps.getDesc()
            }


            //匹配出搜索词
            String search = getSearchWrods(content)
            println "===isBlack===" + isBlack + "  search:" + search + "  CMD_RED_PACKET:" + CMD_RED_PACKET
            if (!isBlack) {
                if (!StringUtils.isNull(search)) {
                    sendMessageFlg = 1  //正常搜索回复内容
                    if (search.indexOf(CMD_RED_PACKET.trim()) > -1) {
                        sendMessageFlg = 11
                    }else if (CMD_HELP.indexOf(search) > -1) {
                        sendMessageFlg = 3
                    } else if (CMD_CREATE_CHANNEL.indexOf(search) > -1) {
                        sendMessageFlg = 4
                    }


                } else {
                    sendMessageFlg = 2  //搜索内容不正确，回复帮助内容
                }

                if (StringUtils.isNull(pid)) {
                    sendMessageFlg = -2
                }

                if (!isRoomMsg && StringUtils.isNull(channelName)) {
                    sendMessageFlg = 5
                }
            } else {
                sendMessageFlg == -1
            }


            if (sendMessageFlg == -1) {
                println channelName + " 在黑名单内"
            }


            //pid或参数不对的情况
            if (sendMessageFlg == -2) {
                sendMessageOnError(chatId, token, contactName, "500", msgFrom)
            }

            //帮助
            if (sendMessageFlg == 2) {
                sendMessageOnHelp(chatId, token, contactName, msgFrom)
            }

            //帮助文档
            if (sendMessageFlg == 3) {
//                    sendUrl(chatId, token, "http://jd.wapg.cn", "帮助文档", "京东助手使用帮助", "http://api.wapg.cn/jd_union/images/help.jpg")
            }

            //自助建群
            if (sendMessageFlg == 4) {
//                    sendUrl(chatId, token, "http://jd.wapg.cn", "建群", "自助建群", "http://api.wapg.cn/jd_union/images/create.jpg")
            }

            if (sendMessageFlg == 11) {

                println "==红包=="
                String redUrl = "https://u.jd.com/mvTWWA"
                String url = jdConvertLinkService.convertUrl(redUrl, channelName)
                if (!StringUtils.isNull(url)) {
                    sendUrl(chatId, token, url, "年货节.京享红包", "最高888元,全品类,无门槛,可叠加", "https://jd.wapg.cn/images/red.jpg")
                }
            }

            if (sendMessageFlg == 5) {
                sendMessageOnHelpForPerson(chatId, token, contactName, msgFrom)
            }

            println "===sendMessageFlg " + sendMessageFlg + "=="

            if (sendMessageFlg == 1) {

                println '搜索:' + search + "   渠道:" + channelName + "  " + pid

                JDRobotMsgESMap jdRobotMsgESMap = new JDRobotMsgESMap()
                jdRobotMsgESMap.setId(messageId)
                jdRobotMsgESMap.setSearch(search)
                jdRobotMsgESMap.setContact_name(contactName)
                jdRobotMsgESMap.setRoom_name(roomName)
                jdRobotMsgESMap.setChannel_id(pid)
                jdRobotMsgESMap.setChannel_name(channelName)
                jdRobotMsgESMap.setType(msg_type)
                jdRobotMsgESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ''))

                //记录
                saveLog(jdRobotMsgESMap)
                String text = jdSkuRobotService.getSkuList(pid, search, 6, isRoomMsg)
                if (true) {

                    HashMap paramMap = new HashMap()
                    paramMap.put("getRoomMsg", getRoomMsg)
                    paramMap.put("contactName", contactName)
                    paramMap.put("search", search)
                    paramMap.put("text", text)
                    paramMap.put("assignChannelName", assignChannelName)
                    paramMap.put("domain", domain)

                    String msgText = new TemplateUtils().getFreeMarkerFromResource("api/robot_msg.ftl", paramMap, "UTF-8")

                    String imgUrl = ""
                    String pattern = "\\[IMG\\](http|https|ftp):\\/\\/(.[^\\[]*)\\[\\/IMG\\]"
                    Pattern r = Pattern.compile(pattern)
                    Matcher m = r.matcher(msgText)
                    if (m.find()) {
                        imgUrl = m.group(0)
                        msgText = msgText.replace(imgUrl, "")
                        imgUrl = imgUrl.replaceAll("\\[IMG\\]", "").replaceAll("\\[/IMG\\]", "")
                        println "生成图:" + imgUrl
                        sendImageMessage(chatId, token, imgUrl, msgFrom)
                        sleep(3000)
                    }

                    sendTextMessage(chatId, token, msgText, msgFrom)
                }
            }

            if (!StringUtils.isNull(channelName)) {
                UnionUserESMap userESMap = new UnionUserESMap()
                userESMap.setWx_id(contactId)
                userESMap.setUser_name(contactName)
                userESMap.setUser_type(msg_type)
                userESMap.setChannel_id(pid)
                userESMap.setChannel_name(channelName)
                unionUserService.saveUser(userESMap)
            }
        }
    }

    /**
     * 拆分出搜索词
     * @param content
     * @return
     */
    public static String getSearchWrods(String content) {
        String search = null
        String res = content
        String b = "@"
        //判断含有几个@
        int count = (res.length() - res.replace(b, "").length()) / b.length()
        if (count < 2) {
            if (content.startsWith(" ")) {
                content = content.replaceFirst(" ", "")
            }
            //@开头的情况
            if (content.startsWith("@")) {
                content = content.replace("\u2005", " ")
                if (content.indexOf(" ") > -1) {
                    search = content.substring(content.indexOf(" ") + 1, content.length())
                }
            }
            //否则@非开头的情况
            else if (content.indexOf("@") > -1) {
                search = content.substring(0, content.indexOf("@"))
            }
            //在没有@的情况下
            else {
                search = content
            }

            if (!StringUtils.isNull(search) && search.length() > 20) {
                search = search.substring(0, 19)
            }

            if (!StringUtils.isNull(search) && search.replaceAll(" ", "").length() == 0) {
                search = null
            }
        }
        return search
    }


    /**
     * 获取内容中指定的渠道号
     * @param content
     * @return
     */
    public String getAssignChannelName(String content) {
        String _assignChannelName = null
        if (!StringUtils.isNull(content) && content.length() > 4) {
            try {
                _assignChannelName = getChannelName(content)
            } catch (Exception e) {
                _assignChannelName = null
            }
        }
        return _assignChannelName
    }

    /**
     * 从群名称中匹配出渠道名
     * @param roomName
     * @return
     */
    public String getChannelName(String roomName) {

        //todo 支持更多数字位数
        String channelName = null
        String regex = "([A-z][0-9]{2,})"
//        String pattern = "([A-z][0-9][0-9][0-9])"
        Pattern r = Pattern.compile(regex)
        Matcher m = r.matcher(roomName)
        if (m.find()) {
            channelName = m.group(0)
        }
        return channelName
    }

    /**
     * 通过渠道名称找到对应pid
     * @param channelName
     * @return
     */
    public JDMediaInfoESMap getPidFromChannelName(String channelName) {
        String pid = null
        String domain = null
        if (!StringUtils.isNull(channelName)) {
            HashMap paramsMap = new HashMap()
            paramsMap.put("from", 0)
            paramsMap.put("size", 1)
            paramsMap.put("channel_name", channelName)
            List<JDMediaInfoESMap> mediaList = jdMediaService.findMediaChannel(paramsMap)
            if (mediaList.size() > 0) {
                JDMediaInfoESMap jdMediaInfoESMaps = mediaList.get(0)
                return jdMediaInfoESMaps
            }
        }
        return null
    }

    public static String sendMessageOnHelp(String chatId, String token, String contactName, String msgFrom) {
        HashMap paramMap = new HashMap()
        paramMap.put("contactName", contactName)
        String msgText = new TemplateUtils().getFreeMarkerFromResource("api/robot_help.ftl", paramMap, "UTF-8")
        sendTextMessage(chatId, token, msgText, msgFrom)
    }

    public static String sendMessageOnHelpForPerson(String chatId, String token, String contactName, String msgFrom) {
        HashMap paramMap = new HashMap()
        paramMap.put("contactName", contactName)
        String msgText = new TemplateUtils().getFreeMarkerFromResource("api/robot_help_person.ftl", paramMap, "UTF-8")
        sendTextMessage(chatId, token, msgText, msgFrom)
    }

    public static String sendMessageOnError(String chatId, String token, String contactName, String codeCode, String msgFrom) {
        HashMap paramMap = new HashMap()
        paramMap.put("contactName", contactName)
        paramMap.put("codeCode", codeCode)
        String msgText = new TemplateUtils().getFreeMarkerFromResource("api/robot_error.ftl", paramMap, "UTF-8")
        sendTextMessage(chatId, token, msgText, msgFrom)
    }


    /**
     * 判断是句子还是微信接口
     * @param chatId
     * @param token
     * @param imgUrl
     * @param msgFrom
     * @return
     */
    public static String sendImageMessage(String chatId, String token, String imgUrl, String msgFrom) {
        if (!StringUtils.isNull(msgFrom) && msgFrom.equalsIgnoreCase("wx")) {
            return sendWxImageMessage(chatId, imgUrl)
        } else {
            return sendJZImageMessage(chatId, token, imgUrl)
        }
    }

    /**
     * 判断是句子还是微信接口
     * @param chatId
     * @param token
     * @param text
     * @param msgFrom
     * @return
     */
    public static String sendTextMessage(String chatId, String token, String text, String msgFrom) {
        if (!StringUtils.isNull(msgFrom) && msgFrom.equalsIgnoreCase("wx")) {
            return sendWxTextMessage(chatId, text)
        } else {
            return sendJZTextMessage(chatId, token, text)
        }
    }


    /**
     * 发送文本消息提交接口
     * @param chatId
     * @param token
     * @param text
     * @return
     */
    public static String sendJZTextMessage(String chatId, String token, String text) {

        if (!StringUtils.isNull(text)) {
            SendMessageBean sendMessageBean = new SendMessageBean()
            sendMessageBean.setChatId(chatId)
            sendMessageBean.setMessageType(0)//文本
            sendMessageBean.setToken(token)
            MessagePayload payload = new MessagePayload()
            payload.setText(text)
            sendMessageBean.setPayload(payload)
            String sendJson = JSONObject.toJSONString(sendMessageBean)
            String retStr = HttpUtils.postJsonString(POST_MESSAGE_URL, sendJson)
            //如果发送失败，2秒后再尝试发送一次
            if (StringUtils.isNull(retStr)) {
                println "发送失败,2秒后尝试重新发送"
                sleep(5000)
                retStr = HttpUtils.postJsonString(POST_MESSAGE_URL, sendJson)
            }
            println "文本发送结果:" + retStr
            return retStr
        }
    }


    /**
     * 发送图片提交接口
     * @param chatId
     * @param token
     * @param imgUrl
     * @return
     */
    public static String sendJZImageMessage(String chatId, String token, String imgUrl) {
        if (!StringUtils.isNull(imgUrl)) {
            SendMessageBean sendMessageBean = new SendMessageBean()
            sendMessageBean.setChatId(chatId)
            sendMessageBean.setMessageType(1)//图片
            sendMessageBean.setToken(token)
            MessagePayload payload = new MessagePayload()
            payload.setUrl(imgUrl)
            sendMessageBean.setPayload(payload)
            String sendJson = JSONObject.toJSONString(sendMessageBean)
            String retStr = HttpUtils.postJsonString(POST_MESSAGE_URL, sendJson)

            //如果发送失败，2秒后再尝试发送一次
            if (StringUtils.isNull(retStr)) {
                println "发送失败,2秒后尝试重新发送"
                sleep(5000)
                retStr = HttpUtils.postJsonString(POST_MESSAGE_URL, sendJson)
            }
            println "图片发送结果:" + retStr
            return retStr
        }
    }

    /**
     * 给微信公众号回复文本消息
     * @param chatId
     * @param text
     * @return
     */
    public static String sendWxTextMessage(String chatId, String text) {
        SendWxMessageBean sendWxMessageBean = new SendWxMessageBean()
        sendWxMessageBean.setToUserName(chatId)
        sendWxMessageBean.setText(text)
        String sendJson = JSONObject.toJSONString(sendWxMessageBean)
        String retStr = HttpUtils.postJsonString(POST_WECHAT_PUBLIC_ACCOUNT_URL, sendJson)
        println "众号发文字送结果:" + retStr
        return retStr
    }

    /**
     * 给微信公众号回复图片消息
     * @param chatId
     * @param text
     * @return
     */
    public static String sendWxImageMessage(String chatId, String imgUrl) {
        println "===发送微信图片==="
        SendWxMessageBean sendWxMessageBean = new SendWxMessageBean()
        sendWxMessageBean.setToUserName(chatId)
        sendWxMessageBean.setImage(imgUrl)
        String sendJson = JSONObject.toJSONString(sendWxMessageBean)
        String retStr = HttpUtils.postJsonString(POST_WECHAT_PUBLIC_ACCOUNT_URL, sendJson)
        println "公众号图片发送结果:" + retStr
        return retStr
    }


    /**
     * 发送链接提交接口
     * @param chatId
     * @param token
     * @param sourceUrl
     * @param title
     * @param summary
     * @param imageUrl
     * @return
     */
    public static String sendUrl(String chatId, String token, String sourceUrl, String title, String summary, String imageUrl) {
        if (!StringUtils.isNull(chatId)) {
            SendMessageBean sendMessageBean = new SendMessageBean()
            sendMessageBean.setChatId(chatId)
            sendMessageBean.setMessageType(2)//URL
            sendMessageBean.setToken(token)
            MessagePayload payload = new MessagePayload()

            payload.setSourceUrl(sourceUrl)
            payload.setTitle(title)
            payload.setSummary(summary)
            payload.setImageUrl(imageUrl)

            sendMessageBean.setPayload(payload)
            String sendJson = JSONObject.toJSONString(sendMessageBean)
            String retStr = HttpUtils.postJsonString(POST_MESSAGE_URL, sendJson)
            println "链接发送结果:" + retStr
            return retStr
        }
    }

    /**
     * 发送文件提交接口
     * @param chatId
     * @param token
     * @param name
     * @param url
     * @return
     */
    public static String sendFile(String chatId, String token, String name, String url) {
        if (!StringUtils.isNull(chatId)) {
            SendMessageBean sendMessageBean = new SendMessageBean()
            sendMessageBean.setChatId(chatId)
            sendMessageBean.setMessageType(3)//File
            sendMessageBean.setToken(token)
            MessagePayload payload = new MessagePayload()

            payload.setName(name)
            payload.setUrl(url)

            sendMessageBean.setPayload(payload)
            String sendJson = JSONObject.toJSONString(sendMessageBean)
            String retStr = HttpUtils.postJsonString(POST_MESSAGE_URL, sendJson)
            println "文件发送结果:" + retStr
            return retStr
        }
    }

    /**
     * 判断是否记录查询词
     * @param contactName
     * @return
     */
    public String saveLog(JDRobotMsgESMap jdRobotMsgESMap) {
        try {
            String black_contact = "凤梨罐头,老陈醋,京东小妹,京东小妹2,寒星"
            if (jdRobotMsgESMap != null && !StringUtils.isNull(jdRobotMsgESMap.getContact_name()) && black_contact.indexOf(jdRobotMsgESMap.getContact_name()) < 0) {
                jdRobotMsgESService.save(jdRobotMsgESMap.getId(), jdRobotMsgESMap)
            }

        } catch (Exception e) {
            println "saveLog:" + e.getLocalizedMessage()
        }
    }
}
