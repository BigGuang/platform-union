package com.waps.union_jd_api.controller;

import com.alibaba.fastjson.JSONObject;
import com.waps.service.jd.es.domain.UnionShortUrlESMap;
import com.waps.union_jd_api.service.UnionShortUrlService;
import com.waps.utils.ResponseUtils;
import com.waps.utils.ShortUrlUtils;
import com.waps.utils.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/url")
public class UnionShortUrlController {

    @Autowired
    private UnionShortUrlService unionShortUrlService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void saveUser(
            @RequestBody UnionShortUrlESMap urlESMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if(!StringUtils.isNull(urlESMap.getUrl())){
            urlESMap.setId(ShortUrlUtils.getShortUrl(urlESMap.getUrl()));
        }
        IndexResponse indexResponse = unionShortUrlService.saveShortUrl(urlESMap);
        Map<String, Object> map = new HashMap<>();
        if (indexResponse != null) {
            map.put("data", urlESMap);
        }
        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void getUrl(@PathVariable("id") String id,
                       HttpServletRequest request,
                       HttpServletResponse response) throws Exception {
        UnionShortUrlESMap urlESMap = unionShortUrlService.loadShortUrl(id);
        Map<String, Object> map = new HashMap<>();
        map.put("data", urlESMap);
        String json = JSONObject.toJSONString(map);
        ResponseUtils.write(response, json);
    }


}
