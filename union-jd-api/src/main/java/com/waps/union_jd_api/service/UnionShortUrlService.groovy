package com.waps.union_jd_api.service

import com.waps.service.jd.es.domain.UnionShortUrlESMap
import com.waps.service.jd.es.service.UnionShortUrlESService
import com.waps.union_jd_api.utils.DateUtils
import com.waps.utils.StringUtils
import org.elasticsearch.action.index.IndexResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnionShortUrlService {

    @Autowired
    UnionShortUrlESService unionShortUrlESService

    public IndexResponse saveShortUrl(UnionShortUrlESMap unionShortUrlESMap) {
        if (!StringUtils.isNull(unionShortUrlESMap.getId())) {
            unionShortUrlESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
            return unionShortUrlESService.save(unionShortUrlESMap.getId(),unionShortUrlESMap)
        }
    }

    public UnionShortUrlESMap loadShortUrl(String id) {
        return unionShortUrlESService.load(id, UnionShortUrlESMap.class) as UnionShortUrlESMap
    }
}
