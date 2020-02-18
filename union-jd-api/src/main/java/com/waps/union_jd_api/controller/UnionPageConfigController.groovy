package com.waps.union_jd_api.controller

import com.waps.service.jd.es.domain.UnionPageConfigESMap
import com.waps.service.jd.es.service.UnionPageConfigESService
import com.waps.tools.security.MD5
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.utils.DateUtils
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.sort.SortOrder
import org.jsoup.helper.StringUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/page_config")
class UnionPageConfigController {
    @Autowired
    private UnionPageConfigESService unionPageConfigESService

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public void configList(
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            @RequestParam(value = "order_by", required = false) String order_by,
            @RequestParam(value = "sort", required = false) String sort,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String _order = "type"
        if (!StringUtils.isNull(order_by)) {
            _order = order_by
        }
        SortOrder _sort = SortOrder.DESC
        if (!StringUtils.isNull(sort) && "asc" == sort) {
            _sort = SortOrder.ASC
        }
        SearchHits hits = unionPageConfigESService.findAll(_order, _sort, page, size)
        Map<String, Object> map = unionPageConfigESService.searchHits2Map(hits)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", map))
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public void configInfo(
            @RequestParam(value = "id", required = true) String id,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            UnionPageConfigESMap unionPageConfigESMap = unionPageConfigESService.load(id, UnionPageConfigESMap.class) as UnionPageConfigESMap
            if (unionPageConfigESMap) {
                ResponseUtils.write(response, new ReturnMessageBean(200, "", unionPageConfigESMap))
            } else {
                ResponseUtils.write(response, new ReturnMessageBean(404, "配置信息不存在"))
            }
        } catch (Exception e) {
            ResponseUtils.write(response, new ReturnMessageBean(500, "读取信息出现错误"))
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void configSave(
            @RequestBody UnionPageConfigESMap unionPageConfigESMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (!StringUtils.isNull(unionPageConfigESMap.getPage_config())) {
            unionPageConfigESMap.setId(new MD5().getMD5(unionPageConfigESMap.getPage_config()))
            unionPageConfigESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
            unionPageConfigESService.save(unionPageConfigESMap.getId(), unionPageConfigESMap)
            ResponseUtils.write(response, new ReturnMessageBean(200, ""))
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "页面配置URI必须存在"))
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void configDelete(
            @RequestParam(value = "id", required = true) String id,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        DeleteResponse deleteResponse = unionPageConfigESService.delete(id)
        ResponseUtils.write(response, new ReturnMessageBean(200, ""))
    }
}
