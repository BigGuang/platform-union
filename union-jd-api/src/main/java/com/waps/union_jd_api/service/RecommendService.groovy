package com.waps.union_jd_api.service

import com.waps.elastic.search.utils.PageUtils
import com.waps.service.jd.api.bean.SearchParams
import com.waps.service.jd.api.service.JdUnionService
import com.waps.service.jd.es.domain.JDSkuInfoESMap
import com.waps.service.jd.es.domain.SkuBeanESMap
import com.waps.service.jd.es.domain.UnionEditorLogESMap
import com.waps.service.jd.es.service.UnionEditorLogESService
import com.waps.union_jd_api.utils.JDConfig
import com.waps.utils.StringUtils
import org.apache.commons.beanutils.ConvertUtils
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RecommendService {
    @Autowired
    JdUnionService jdUnionService

    @Autowired
    private UnionEditorLogESService unionEditorLogESService

    @Autowired
    private JDSkuInfoService jdSkuInfoService

    public List<JDSkuInfoESMap> editorRecommend(String startTime, String endTime, Integer page, Integer size) {
        PageUtils pageUtils = new PageUtils(page, size)
        Map<String, Object> params = new HashMap<>()
        if (startTime && startTime.length() <= 11) {
            startTime = startTime + " 00:00:00"
        }
        if (endTime && endTime.length() <= 11) {
            endTime = endTime + " 23:59:59"
        }
        params.put("start_time", startTime)
        params.put("end_time", endTime)
        params.put("from", pageUtils.getFrom())
        params.put("size", pageUtils.getSize())
        List<String> skuIdList = new ArrayList<>()
        SearchHits hits = unionEditorLogESService.findByFreeMarkerFromResource("es_script/jtb_send_done.json", params)
        if (hits != null) {
            long total = hits.getTotalHits().value
            SearchHit[] searchHits = hits.getHits()

            List<UnionEditorLogESMap> list = new ArrayList<>()
            for (SearchHit hit : searchHits) {
                UnionEditorLogESMap unionEditorLogESMap = unionEditorLogESService.getObjectFromJson(hit.getSourceAsString(), UnionEditorLogESMap.class) as UnionEditorLogESMap
                if (unionEditorLogESMap.getSkuList()) {
                    for (SkuBeanESMap skuBeanESMap in unionEditorLogESMap.getSkuList()) {
                        if (!StringUtils.isNull(skuBeanESMap.getSkuId())) {
                            skuIdList.add(skuBeanESMap.getSkuId())
                        }
                    }
                }
            }
        }
        String[] skuList = skuIdList.toArray(String[])
        println skuList

        List<JDSkuInfoESMap> recommendList = jdSkuInfoService.getSkuListBySkuIDs(skuList)
        return recommendList
    }


    public String priceRecommend(Double priceTo, Integer page, Integer size) {
        SearchParams searchParams = new SearchParams()
        searchParams.setApp_key(JDConfig.APP_KEY)
        searchParams.setApp_secret(JDConfig.SECRET_KEY)
        searchParams.setPriceto(priceTo)
        searchParams.setPageIndex(page)
        searchParams.setPageSize(size)
        String json = jdUnionService.getGoodsListBySearch(searchParams)
        return json
    }


    public String skuListRecommend(String[] skuIDs) {
        Long[] longSkuIDs = (Long[]) ConvertUtils.convert(skuIDs, long.class);
        SearchParams searchParams = new SearchParams()
        searchParams.setApp_key(JDConfig.APP_KEY)
        searchParams.setApp_secret(JDConfig.SECRET_KEY)
        searchParams.setSkuIds(longSkuIDs)
        String json = jdUnionService.getGoodsListBySearch(searchParams)
        return json
    }


}
