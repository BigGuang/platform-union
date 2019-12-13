package com.waps.union_jd_api.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.HanLP;
import com.waps.union_jd_api.bean.ReturnMessageBean;
import com.waps.union_jd_api.service.JDSkuRobotService;
import com.waps.union_jd_api.service.RobotConfigService;
import com.waps.union_jd_api.service.WeChatRobotService;
import com.waps.union_jd_api.utils.ImageUtils;
import com.waps.utils.DateUtils;
import com.waps.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/wechat_robot")
public class WeChatRobotController {

    @Autowired
    WeChatRobotService weChatRobotService;

    @Autowired
    JDSkuRobotService jdSkuRobotService;

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public void message(
            @RequestBody Map<String, Object> paramsMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        if (paramsMap != null) {
            Object jsonObj = paramsMap.get("data");
            String jsonStr = JSONObject.toJSONString(jsonObj);

            //异步处理内容，并回复
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    weChatRobotService.getMessage(jsonStr);
                }
            }).start();
        }


        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("message", "");
        ResponseUtils.write(response, JSONObject.toJSONString(map));
    }

    /**
     * 通过句子接口发消息到指定群或人
     *
     * @param paramsMap
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/send_helper", method = RequestMethod.POST)
    public void sendMessageToHelper(
            @RequestBody Map<String, Object> paramsMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        System.out.println(paramsMap);
        String chatID = "5d5a4754bd6faa1c4edfe3cc";
        if (paramsMap.get("chat_id") != null) {
            chatID = (String) paramsMap.get("chat_id");
        }
        if (paramsMap.get("content") != null) {
            String content = (String) paramsMap.get("content");
            String ret = weChatRobotService.sendJZTextMessage(chatID, weChatRobotService.TOKEN, content);
            ResponseUtils.write(response, new ReturnMessageBean(200, "发送成功", ret));
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(400, "参数错误"));
        }
    }


    @RequestMapping(value = "/add_black_channel")
    public void add_black_channel(
            @RequestParam(value = "channelName", required = true) String channelName,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        RobotConfigService.addBlackChannel(channelName);
        ResponseUtils.write(response, "ok");
    }

    @RequestMapping(value = "/remove_black_channel")
    public void remove_black_channel(
            @RequestParam(value = "channelName", required = true) String channelName,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        RobotConfigService.removeBlackChannel(channelName);
        ResponseUtils.write(response, "ok");
    }

    @RequestMapping(value = "/list_black_channel")
    public void list_black_channel(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String list = RobotConfigService.getBlackChannelList();
        HashMap map = new HashMap();
        map.put("list", list);
        ResponseUtils.write(response, JSONObject.toJSONString(map));
    }


    @RequestMapping(value = "/add_black_word")
    public void add_black_word(
            @RequestParam(value = "word", required = true) String word,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        RobotConfigService.addBlackWord(word);
        ResponseUtils.write(response, "ok");
    }

    @RequestMapping(value = "/remove_black_word")
    public void remove_black_word(
            @RequestParam(value = "word", required = true) String word,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        RobotConfigService.removeBlackWord(word);
        ResponseUtils.write(response, "ok");
    }

    @RequestMapping(value = "/list_black_word")
    public void list_black_word(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String list = RobotConfigService.getBlackWordList();
        HashMap map = new HashMap();
        map.put("list", list);
        ResponseUtils.write(response, JSONObject.toJSONString(map));
    }


    /**
     * ===Test===
     */


    @RequestMapping(value = "/test_img", method = RequestMethod.GET)
    public void test_img(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        System.out.println("==收到juzi消息==");


        String[] imglist = {
                "http://img14.360buyimg.com/ads/jfs/t1/54725/27/7837/191381/5d565b80Ee01c2fcc/3cda382cb4aa916e.jpg",
                "http://img14.360buyimg.com/ads/jfs/t5233/305/2303283587/427149/dd73b158/59198c88N02713452.jpg",
                "http://img14.360buyimg.com/ads/jfs/t21514/219/1882923281/705681/dca4c93f/5b3c98f0Ne36de965.png",
                "http://img14.360buyimg.com/ads/jfs/t1/40309/3/2561/48251/5cc17ba0Eccade25a/4c4490da0a05cbf3.jpg1",
                "http://img14.360buyimg.com/ads/jfs/t1/80235/28/6022/77528/5d410abaEb3604415/eda8bab59ca4b509.jpg",
                "http://img14.360buyimg.com/ads/jfs/t23923/306/1709809426/330426/aef1419e/5b67b3c8N7a04f888.jpg"
        };


        System.out.println(imglist.length);
        String ppp = "/Users/xguang/temp03/" + System.currentTimeMillis() + ".jpg";
        ImageUtils.mergeImage(imglist, 1, ppp);

        ResponseUtils.write(response, "ok");
    }


    @RequestMapping(value = "/test_img2", method = RequestMethod.GET)
    public void test_message(
            @RequestParam(value = "s", required = true) String search,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        System.out.println("==收到juzi消息==");


        String[] imglist = {
                "http://img14.360buyimg.com/ads/jfs/t1/54725/27/7837/191381/5d565b80Ee01c2fcc/3cda382cb4aa916e.jpg",
                "http://img14.360buyimg.com/ads/jfs/t5233/305/2303283587/427149/dd73b158/59198c88N02713452.jpg",
                "http://img14.360buyimg.com/ads/jfs/t21514/219/1882923281/705681/dca4c93f/5b3c98f0Ne36de965.png",
                "http://img14.360buyimg.com/ads/jfs/t1/40309/3/2561/48251/5cc17ba0Eccade25a/4c4490da0a05cbf3.jpg",
                "http://img14.360buyimg.com/ads/jfs/t1/80235/28/6022/77528/5d410abaEb3604415/eda8bab59ca4b509_00.jpg",
                "http://img14.360buyimg.com/ads/jfs/t23923/306/1709809426/330426/aef1419e/5b67b3c8N7a04f888.jpg"
        };

        String text = jdSkuRobotService.getSkuList("1813900977", search, 6);

//        System.out.println(imglist.length);
//        ImageUtils.mergeImage(imglist,1,"/Users/xguang/temp03/test.jpg");


        String imgUrl = "";
        String pattern = "\\[IMG\\](http|https|ftp):\\/\\/(.[^\\[]*)\\[\\/IMG\\]";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(text);
        if (m.find()) {
            imgUrl = m.group(0);
            text = text.replace(imgUrl, "");
            imgUrl = imgUrl.replaceAll("\\[IMG\\]", "").replaceAll("\\[/IMG\\]", "");

        }
        System.out.println(imgUrl);
        ResponseUtils.write(response, text);
    }


    @RequestMapping(value = "/test_skulist", method = RequestMethod.GET)
    public void test_message2(
            @RequestParam(value = "s", required = true) String search,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String text = jdSkuRobotService.getSkuList("1813900977", search, 6);
        ResponseUtils.write(response, text);

    }

    @RequestMapping(value = "/test_hanlp", method = RequestMethod.GET)
    public void test_message3(
            @RequestParam(value = "s", required = true) String search,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String content = search;
        List<String> keywordList = HanLP.extractKeyword(content, 5);
        List<String> phraseList = HanLP.extractPhrase(content, 10);
        List<String> sentenceList = HanLP.extractSummary(content, 3);

        String array = JSONArray.toJSONString(keywordList);
        String array2 = JSONArray.toJSONString(phraseList);
        String array3 = JSONArray.toJSONString(sentenceList);


        System.out.println(array);
        System.out.println(array2);
        System.out.println(array3);

        ResponseUtils.write(response, array);

    }

    @RequestMapping(value = "/test_search", method = RequestMethod.GET)
    public void test_message4(@RequestParam(value = "s", required = true) String search,
                              HttpServletRequest request,
                              HttpServletResponse response) throws Exception {

        String ss = weChatRobotService.getSearchWrods(search);
        String channelName = weChatRobotService.getChannelName(search);
        String _a_channelName = weChatRobotService.getAssignChannelName(search);
        Boolean flg = RobotConfigService.hasBlackWords(search);
        String ret = ss + " " + flg + "  channelName:" + channelName + "  _a_channelName:" + _a_channelName;


        ResponseUtils.write(response, ret);
    }

}
