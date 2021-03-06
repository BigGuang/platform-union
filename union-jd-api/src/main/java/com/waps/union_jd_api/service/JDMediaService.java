package com.waps.union_jd_api.service;

import com.alibaba.fastjson.JSONObject;
import com.waps.service.jd.db.dao.WapsJdUserDao;
import com.waps.service.jd.db.model.WapsJdUser;
import com.waps.service.jd.db.model.WapsJdUserExample;
import com.waps.service.jd.es.domain.JDMediaInfoESMap;
import com.waps.service.jd.es.service.JDMediaESService;
import com.robot.netty.server.OnLineService;
import com.waps.tools.test.TestUtils;
import com.waps.union_jd_api.utils.DateUtils;
import com.waps.utils.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class JDMediaService {

    @Autowired
    JDMediaESService jdMediaESService;
    @Autowired
    WapsJdUserDao wapsJdUserDao;

    JDMediaService() {
//        loadAllChannelInfo2Map(1);
    }

    public IndexResponse insertMedia(JDMediaInfoESMap mediaInfoESMap) {
        return jdMediaESService.save(mediaInfoESMap.getId(), mediaInfoESMap);
    }

    public JDMediaInfoESMap getMediaInfoByChannelName(String channel_name) {
        HashMap paramsMap = new HashMap();
        paramsMap.put("from", 0);
        paramsMap.put("size", 1);
        paramsMap.put("channel_name", channel_name);
        List<JDMediaInfoESMap> mediaList = findMediaChannel(paramsMap);
        if (mediaList.size() > 0) {
            JDMediaInfoESMap jdMediaInfoESMaps = mediaList.get(0);
            if (StringUtils.isNull(jdMediaInfoESMaps.getDesc())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getDomain(jdMediaInfoESMaps);
                    }
                }).start();
            }
            return jdMediaInfoESMaps;
        } else {
            return null;
        }
    }

    public void getDomain(JDMediaInfoESMap jdMediaInfoESMap) {
        WapsJdUserExample example = new WapsJdUserExample();
        example.createCriteria().andUNameEqualTo(jdMediaInfoESMap.getChannel_name());
        List<WapsJdUser> list = wapsJdUserDao.selectByExample(example);
        if (list != null && !list.isEmpty()) {
            WapsJdUser wapsJdUser = list.get(0);
            if (wapsJdUser != null && !StringUtils.isNull(wapsJdUser.getuDomain())) {
                jdMediaInfoESMap.setDesc(wapsJdUser.getuDomain());
                jdMediaESService.save(jdMediaInfoESMap.getId(), jdMediaInfoESMap);
            }
        }
    }

    public void insertMedia(List<JDMediaInfoESMap> mediaInfoESMapList) {
        for (JDMediaInfoESMap mediaInfoESMap : mediaInfoESMapList) {
            if (mediaInfoESMap != null && !StringUtils.isNull(mediaInfoESMap.getId())) {
                insertMedia(mediaInfoESMap);
            }
        }
    }

    public List<HashMap> groupMediaList(HashMap paramsMap) {

        List<HashMap> arrayList = new ArrayList<>();
        LinkedHashMap<String, Long> maps = new LinkedHashMap();
        Aggregations aggregations = jdMediaESService.groupByFreeMarkerFromResource("es_script/jd_media_group.ftl", paramsMap);
        Terms genders = aggregations.get("group_by");

        for (Terms.Bucket entry : genders.getBuckets()) {
            HashMap map = new HashMap();
            map.put("media_name", (String) entry.getKey());
            map.put("count", entry.getDocCount());
            Terms g2 = entry.getAggregations().get("media_info");
            for (Terms.Bucket e2 : g2.getBuckets()) {
                map.put("media_id", (String) e2.getKey());
            }
            arrayList.add(map);
        }
        return arrayList;
    }


    public List<JDMediaInfoESMap> findMediaChannel(HashMap paramsMap) {
        List<JDMediaInfoESMap> arrayList = new ArrayList<>();
        SearchHits hits = jdMediaESService.findByFreeMarkerFromResource("es_script/jd_media_channel_search.ftl", paramsMap);
        if (hits != null && hits.getTotalHits().value > 0) {
            SearchHit[] hitList = hits.getHits();
            for (SearchHit hit : hitList) {
                String json = hit.getSourceAsString();
                JDMediaInfoESMap jdMediaInfoESMap = JSONObject.parseObject(json, JDMediaInfoESMap.class);
                arrayList.add(jdMediaInfoESMap);
            }
        }
        if (arrayList.size() > 0) {
            return arrayList;
        } else if (paramsMap.get("channel_name") != null) {
            String channel_name = (String) paramsMap.get("channel_name");
            WapsJdUserExample example = new WapsJdUserExample();
            example.createCriteria().andUNameEqualTo(channel_name);
            List<WapsJdUser> list = wapsJdUserDao.selectByExample(example);
            if (list != null && !list.isEmpty()) {
                WapsJdUser wapsJdUser = list.get(0);
                if (wapsJdUser != null) {
                    JDMediaInfoESMap jdMediaInfoESMap = new JDMediaInfoESMap();
                    jdMediaInfoESMap.setChannel_name(wapsJdUser.getuName());
                    jdMediaInfoESMap.setChannel_id(wapsJdUser.getuPid());
                    jdMediaInfoESMap.setDesc(wapsJdUser.getuDomain());
                    jdMediaInfoESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""));
                    jdMediaInfoESMap.setId("1000440357_63665_" + wapsJdUser.getuPid());
                    jdMediaESService.save(jdMediaInfoESMap.getId(), jdMediaInfoESMap);

//                    TestUtils.outPrint(jdMediaInfoESMap);
                    arrayList.add(jdMediaInfoESMap);
                }
            }
            return arrayList;
        } else {
            return arrayList;
        }
    }

    public SearchHits findMediaChannelToHits(HashMap paramsMap) {
        SearchHits hits = jdMediaESService.findByFreeMarkerFromResource("es_script/jd_media_channel_search.ftl", paramsMap);
        return hits;
    }

    public void loadAllChannelInfo2Map(int page) {
        int now_count = OnLineService.getChannelInfoMap().size();
        if (page < 2 && now_count > 1) {
        } else {
            if (page < 0) {
                page = 1;
            }
            int size = 100;
            SearchHits hits = jdMediaESService.findAll(page, size);
            long total = hits.getTotalHits().value;
            SearchHit[] hitList = hits.getHits();
            for (SearchHit hit : hitList) {
                String json = hit.getSourceAsString();
                JDMediaInfoESMap jdMediaInfoESMap = JSONObject.parseObject(json, JDMediaInfoESMap.class);
                OnLineService.addChannelInfo(jdMediaInfoESMap.getChannel_id(), jdMediaInfoESMap);
            }
            int _count = OnLineService.getChannelInfoMap().size();
            long count = Integer.parseInt(_count + "");
            System.out.println("loadAllChannelInfo2Map:" + count + "  total:" + total);
            if (total > count) {
                int nextPage = page + 1;
                System.out.println("nextPage:" + nextPage);
                loadAllChannelInfo2Map(page + 1);
            }
        }
    }


}
