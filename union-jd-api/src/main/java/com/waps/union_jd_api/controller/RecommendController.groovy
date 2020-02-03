package com.waps.union_jd_api.controller

import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.es.domain.JDSkuInfoESMap
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.service.RecommendService
import com.waps.utils.DateUtils
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.SimpleDateFormat

@Controller
@RequestMapping("/recommend")
class RecommendController {
    @Autowired
    private RecommendService recommendService

    @RequestMapping(value = "/editor")
    public void count(
            @RequestParam(value = "sTime", required = true) String sTime,
            @RequestParam(value = "eTime", required = true) String eTime,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<JDSkuInfoESMap> list = recommendService.editorRecommend(sTime, eTime, page, size)
        if (list) {
            ResponseUtils.write(response, new ReturnMessageBean(200, "", list));
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "ERROR"));
        }
    }

    /**
     * 编辑推荐，自动往前推7天
     * @param page
     * @param size
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/editor_recommend")
    public void editRecommend(
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
        String eTime = simpleDateFormat.format(new Date())
        Date sDate = DateUtils.addByDate(new Date(), -7)
        String sTime = simpleDateFormat.format(sDate)
        println sTime + "  " + eTime
        List<JDSkuInfoESMap> list = recommendService.editorRecommend(sTime, eTime, page, size)
        if (list) {
            ResponseUtils.write(response, new ReturnMessageBean(200, "", list));
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "ERROR"));
        }
    }


    /**
     * 按指定最高价格搜索
     * @param priceTo
     * @param page
     * @param size
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/price_recommend")
    public void priceRecommend(
            @RequestParam(value = "priceTo", required = false) Double priceTo,
            @RequestParam(value = "page", required = true) Integer page,
            @RequestParam(value = "size", required = true) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (!priceTo || priceTo < 1) {
            priceTo = 9.9
        }
        String json = recommendService.priceRecommend(priceTo, page, size)
        ResponseUtils.write(response, json);
    }

    /**
     *
     * @param priceTo
     * @param page
     * @param size
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/sku_recommend")
    public void skuListRecommend(
            @RequestParam(value = "sku", required = true) String[] skuList,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        println skuList

        if (page != null && size != null) {
            if (page < 1) page = 1
            List<String> list = new ArrayList<>()
            int start = (page - 1) * size;
            int end = start + size;
            if (start < skuList.length && start < end) {
                if (end > skuList.length) {
                    end = skuList.length
                }
                for (int i = start; i < end; i++) {
                    String _sku=skuList[i]
                    list.add(_sku)
                }
            }
            String[] _list = new String[list.size()]
            String json = recommendService.skuListRecommend(list.toArray(_list))
            ResponseUtils.write(response, json);
        } else {
            String json = recommendService.skuListRecommend(skuList)
            ResponseUtils.write(response, json);
        }
    }

}
