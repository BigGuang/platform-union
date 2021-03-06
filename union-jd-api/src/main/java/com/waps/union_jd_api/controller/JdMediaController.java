package com.waps.union_jd_api.controller;

import com.alibaba.fastjson.JSONObject;
import com.waps.union_jd_api.bean.ReturnMessageBean;
import com.waps.union_jd_api.service.JDMediaService;
import com.waps.service.jd.es.domain.JDMediaInfoESMap;
import com.waps.utils.ResponseUtils;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/media")
public class JdMediaController {

    @Autowired
    JDMediaService jdMediaService;

    @RequestMapping(value = "/get_pid", method = RequestMethod.GET)
    public void getPid(@RequestParam(value = "channel_name", required = false) String channel_name,
                       HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

        JDMediaInfoESMap jdMediaInfoESMap = jdMediaService.getMediaInfoByChannelName(channel_name);

        ResponseUtils.write(response, new ReturnMessageBean(200, "", jdMediaInfoESMap).toString());
    }

    @RequestMapping(value = "/info")
    public void getMediaInfo(
            @RequestParam(value = "channel_name", required = true) String channelName,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        HashMap paramsMap = new HashMap();
        paramsMap.put("from", 0);
        paramsMap.put("size", 1);
        paramsMap.put("channel_name", channelName);
        Map map = new HashMap();

        List<JDMediaInfoESMap> mediaList = jdMediaService.findMediaChannel(paramsMap);
        if (mediaList.size() > 0) {
            JDMediaInfoESMap jdMediaInfoESMaps = mediaList.get(0);
            map.put("data", jdMediaInfoESMaps);
        }
        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);
    }

    @RequestMapping(value = "/get_media_group")
    public void get_media_group(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        List<HashMap> groupList = jdMediaService.groupMediaList(null);
        String json = JSONObject.toJSONString(groupList);

        System.out.println(json);

        ResponseUtils.write(response, json);
    }

    @RequestMapping(value = "/get_media_channel")
    public void get_media_channel(
            @RequestParam(value = "media_id", required = false) String media_id,
            @RequestParam(value = "channel_name", required = false) String channel_name,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        if (size < 4) {
            size = 4;
        }
        if (page < 1) {
            page = 1;
        }
        int from = 0;
        from = (page - 1) * size;
        HashMap paramsMap = new HashMap();
        paramsMap.put("media_id", media_id);
        paramsMap.put("channel_name", channel_name);
        paramsMap.put("from", from);
        paramsMap.put("size", size);
        Map map = new HashMap();
        List<JDMediaInfoESMap> arrayList = new ArrayList<>();
        SearchHits hits = jdMediaService.findMediaChannelToHits(paramsMap);
        if (hits != null) {
            SearchHit[] hitList = hits.getHits();
            for (SearchHit hit : hitList) {
                String json = hit.getSourceAsString();
                JDMediaInfoESMap jdMediaInfoESMap = JSONObject.parseObject(json, JDMediaInfoESMap.class);
                arrayList.add(jdMediaInfoESMap);
            }


            map.put("total", hits.getTotalHits().value);
        } else {
            map.put("total", 0);
        }
        map.put("data", arrayList);
        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);

    }
}
