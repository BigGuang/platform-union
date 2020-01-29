package com.waps.union_jd_api.service

import com.waps.service.jd.es.domain.UnionInviteCodeESMap
import com.waps.service.jd.es.domain.UnionUserESMap
import com.waps.service.jd.es.service.UnionInviteCodeESService
import com.waps.utils.StringUtils
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnionInviteCodeService {

    @Autowired
    private UnionInviteCodeESService unionInviteCodeESService

    public IndexResponse saveInviteCode(UnionInviteCodeESMap inviteCodeESMap) {
        if (!StringUtils.isNull(inviteCodeESMap.getInvite_code())) {
            inviteCodeESMap.setId(inviteCodeESMap.getInvite_code())
            return unionInviteCodeESService.save(inviteCodeESMap.getId(), inviteCodeESMap)
        }
    }

    public UnionInviteCodeESMap loadInviteCode(String id) {
        return unionInviteCodeESService.load(id, UnionInviteCodeESMap.class) as UnionInviteCodeESMap
    }

    /**
     * 通过邀请码找信息
     * @param code
     * @return
     */
    public UnionUserESMap loadUserByInviteCode(String code) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("invite_code", code)
        return loadInviteCodeByRequest(kvMap)
    }

    public UnionInviteCodeESMap loadInviteCodeByRequest(Map<String, String> kvMap) {
        try {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
            Iterator it = kvMap.entrySet().iterator()
            while (it.hasNext()) {
                Map.Entry entity = (Map.Entry) it.next()
                String field = (String) entity.getKey()
                String value = (String) entity.getValue()
                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(field, value))
            }
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
            sourceBuilder.query(boolQueryBuilder)
            sourceBuilder.explain(true)// 设置是否按查询匹配度排序
            sourceBuilder.from(0)
            sourceBuilder.size(1)
            SearchRequest searchRequest = new SearchRequest()
            searchRequest.source(sourceBuilder)
            SearchHits hits = unionInviteCodeESService.find(searchRequest)
            SearchHit[] hit = hits.getHits()
            if (hit.size() > 0) {
                String json = hit[0].getSourceAsString()
                if (!StringUtils.isNull(json)) {
                    UnionUserESMap userESMap = unionInviteCodeESService.getObjectFromJson(json, UnionUserESMap.class) as UnionUserESMap
                    return userESMap
                }
            }
        } catch (Exception e) {
            println "unionInviteCodeESService loadInviteCodeByRequest ERROR:" + e.getLocalizedMessage()
        }
    }
}
