package com.waps.union_jd_api.service

import com.waps.elastic.search.utils.PageUtils
import com.waps.service.jd.es.domain.SkuBeanESMap
import com.waps.service.jd.es.domain.UnionEditorLogESMap
import com.waps.service.jd.es.service.UnionEditorLogESService
import com.waps.utils.StringUtils
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RecommendService {

    @Autowired
    private UnionEditorLogESService unionEditorLogESService

    @Autowired
    private JDSkuInfoService jdSkuInfoService

    public String editorRecommend(String startTime, String endTime, Integer page, Integer size) {
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
        SearchHits hits = unionEditorLogESService.findByFreeMarkerFromResource("es_script/jtb_send_done.json", params)
        long total = hits.getTotalHits().value
        SearchHit[] searchHits = hits.getHits()
        List<String> skuIdList=new ArrayList<>()
        List<UnionEditorLogESMap> list = new ArrayList<>()
        for (SearchHit hit : searchHits) {
            UnionEditorLogESMap unionEditorLogESMap = unionEditorLogESService.getObjectFromJson(hit.getSourceAsString(), UnionEditorLogESMap.class) as UnionEditorLogESMap
            if(unionEditorLogESMap.getSkuList()){
                for(SkuBeanESMap skuBeanESMap in unionEditorLogESMap.getSkuList()){
                    if(!StringUtils.isNull(skuBeanESMap.getSkuId())){
                        skuIdList.add(skuBeanESMap.getSkuId())
                    }
                }
            }
            list.add(unionEditorLogESMap)
        }
    }
}
