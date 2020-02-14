package com.waps.robot_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.elastic.search.utils.PageUtils
import com.waps.robot_api.service.TSCallBackService
import com.waps.robot_api.utils.TestRequest
import com.waps.robot_api.utils.UrlUtil
import com.waps.service.jd.es.service.TSCallBackLogESService
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/ts/robot")
class TSRobotCallBackController {

    @Autowired
    private TSCallBackService tsCallBackService
    @Autowired
    private TSCallBackLogESService tsCallBackLogESService

    @RequestMapping(value = "/callback",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void callBack(
            @RequestParam(value = "nType", required = false) Integer nType,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String retString = "SUCCESS"
        if (nType != null) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(request.getInputStream()));
            String line = null;
            StringBuilder buffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            System.out.println(buffer.toString());
            String body = buffer.toString()

            Map<String, String> params = UrlUtil.parseBody(body)
            String strContext = params.get("strContext")
            String strSign = params.get("strSign")

            boolean flg = tsCallBackService.callBack(nType, strContext)
            if (!flg) {
                retString = "FALSE"
            }
        }
        ResponseUtils.write(response, retString)
    }

    @RequestMapping(value = "/callback_log")
    public void callBackLog(
            @RequestBody FindLogParams findLogParams,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map params = new HashMap()
        PageUtils pageUtils = new PageUtils(findLogParams.getPage(), findLogParams.getSize())
        params.put("from", pageUtils.getFrom())
        params.put("size", pageUtils.getSize())
        params.put("nType", findLogParams.getnType())
        SearchHits hits = tsCallBackLogESService.findByFreeMarkerFromResource("es_script/ts_callback_log.json", params);
        long total = hits.getTotalHits().value
        SearchHit[] list = hits.getHits()
        List<Map> _list = new ArrayList<>()
        for (SearchHit hit : list) {
            _list.add(hit.getSourceAsMap())
        }
        Map retMap = new HashMap()
        retMap.put("total", total)
        retMap.put("list", _list)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retMap))
    }
}

class FindLogParams{
    Integer nType
    Integer page
    Integer size
}
