package com.waps.union_jd_api.controller

import com.waps.elastic.search.utils.PageUtils
import com.waps.service.jd.es.domain.UnionActionESMap
import com.waps.service.jd.es.domain.UnionAppUserESMap
import com.waps.service.jd.es.service.UnionActionESService
import com.waps.tools.security.MD5
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.service.UniformMessageParams
import com.waps.utils.DateUtils
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.SimpleDateFormat

@Controller
@RequestMapping("/union_action")
class UnionActionController {

    @Autowired
    private UnionActionESService unionActionESService


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public void actionList(
            @RequestParam(value = "end_time", required = false) String end_time,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        try {
            PageUtils pageUtils = new PageUtils(page, size)
            HashMap<String, Object> params = new HashMap<>()
            params.put("from", pageUtils.getFrom())
            params.put("size", pageUtils.getSize())
            if (StringUtils.isNull(end_time)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                end_time = simpleDateFormat.format(new Date())
            }

            params.put("end_time", end_time)

            SearchHits hits = unionActionESService.findByFreeMarkerFromResource("es_script/union_action_list.json", params)
            long total = hits.getTotalHits().value
            SearchHit[] list = hits.getHits()
            List<Map<String, Object>> _list = new ArrayList<>()
            for (SearchHit hit : list) {
                Map<String, Object> obj = hit.getSourceAsMap()
                _list.add(obj)
            }
            HashMap retMap = new HashMap()
            retMap.put("total", total)
            retMap.put("list", _list)
            ResponseUtils.write(response, new ReturnMessageBean(200, "", retMap).toString());
        } catch (Exception e) {
            ResponseUtils.write(response, new ReturnMessageBean(500, "错误:" + e.getLocalizedMessage()).toString());
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void actionSave(
            @RequestBody UnionActionESMap unionActionESMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            if (StringUtils.isNull(unionActionESMap.getId())) {
                unionActionESMap.setId(new MD5().getMD5(UUID.toString()))
            }
            unionActionESMap.setCreatetime(com.waps.union_jd_api.utils.DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
            unionActionESService.save(unionActionESMap.getId(), unionActionESMap)
            ResponseUtils.write(response, new ReturnMessageBean(200, "",).toString())

        } catch (Exception e) {
            ResponseUtils.write(response, new ReturnMessageBean(500, "保存失败:" + e.getLocalizedMessage()).toString());
        }
    }


    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void actionDelete(
            @RequestParam(value = "id", required = true) String id,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            unionActionESService.delete(id)
            ResponseUtils.write(response, new ReturnMessageBean(200, "",).toString())

        } catch (Exception e) {
            ResponseUtils.write(response, new ReturnMessageBean(500, "删除失败:" + e.getLocalizedMessage()).toString());
        }
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public void actionInfo(
            @RequestParam(value = "id", required = true) String id,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            UnionActionESMap unionActionESMap = unionActionESService.load(id, UnionActionESMap.class) as UnionActionESMap
            ResponseUtils.write(response, new ReturnMessageBean(200, "", unionActionESMap).toString())
        } catch (Exception e) {
            ResponseUtils.write(response, new ReturnMessageBean(500, "读取失败:" + e.getLocalizedMessage()).toString());
        }
    }
}
