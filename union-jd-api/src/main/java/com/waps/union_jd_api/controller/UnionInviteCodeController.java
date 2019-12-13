package com.waps.union_jd_api.controller;


import com.alibaba.fastjson.JSONObject;
import com.waps.service.jd.es.domain.JDMediaInfoESMap;
import com.waps.service.jd.es.domain.UnionInviteCodeESMap;
import com.waps.service.jd.es.domain.UnionShortUrlESMap;
import com.waps.union_jd_api.service.JDMediaService;
import com.waps.union_jd_api.service.UnionInviteCodeService;
import com.waps.utils.DateUtils;
import com.waps.utils.ResponseUtils;
import com.waps.utils.ShortUrlUtils;
import com.waps.utils.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/invite")
public class UnionInviteCodeController {

    @Autowired
    private UnionInviteCodeService unionInviteCodeService;
    @Autowired
    private JDMediaService jdMediaService;

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    public void saveInviteCode(
            @RequestParam(value = "channel_name", required = true) String channel_name,
            @RequestParam(value = "code", required = false) String code,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String channel_id = null;
        JDMediaInfoESMap jdMediaInfoESMaps = jdMediaService.getMediaInfoByChannelName(channel_name);
        if (jdMediaInfoESMaps != null) {
            channel_id = jdMediaInfoESMaps.getChannel_id();
        }

        Map<String, Object> map = new HashMap<>();
        if (!StringUtils.isNull(channel_id)) {
            if (StringUtils.isNull(code)) {
                code = ShortUrlUtils.getShortUrl(channel_name + "://" + System.currentTimeMillis());
            }
            UnionInviteCodeESMap unionInviteCodeESMap = new UnionInviteCodeESMap();
            unionInviteCodeESMap.setId(code);
            unionInviteCodeESMap.setInvite_code(code);
            unionInviteCodeESMap.setChannel_name(channel_name);
            unionInviteCodeESMap.setChannel_id(channel_id);
            unionInviteCodeESMap.setCreatetime(DateUtils.getNow());
            IndexResponse indexResponse = unionInviteCodeService.saveInviteCode(unionInviteCodeESMap);
            if (indexResponse != null) {
                map.put("data", unionInviteCodeESMap);
            } else {
                map.put("error:", "保存失败");
            }
        } else {
            map.put("error:", "未匹配到推广位信息");
        }

        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);
    }


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public void getUrl(@RequestParam(value = "code", required = true) String code,
                       HttpServletRequest request,
                       HttpServletResponse response) throws Exception {
        UnionInviteCodeESMap inviteCodeESMap = unionInviteCodeService.loadInviteCode(code);
        Map<String, Object> map = new HashMap<>();
        map.put("data", inviteCodeESMap);
        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);
    }
}
