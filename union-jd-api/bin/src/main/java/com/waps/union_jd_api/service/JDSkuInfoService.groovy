package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.api.bean.SearchParams
import com.waps.service.jd.api.service.JdUnionService
import com.waps.service.jd.es.domain.JDSkuInfoESMap
import com.waps.service.jd.es.service.JDSkuInfoESService
import com.waps.union_jd_api.utils.DateUtils
import com.waps.union_jd_api.utils.JDConfig
import jd.union.open.goods.query.response.Coupon
import org.apache.commons.collections.map.LinkedMap
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.MatchAllQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JDSkuInfoService {

    @Autowired
    private JdUnionService jdUnionService
    @Autowired
    private JDSkuInfoESService jdSkuInfoESService

    int pageSize = 30

    public void synxSkuInfoAll() {
        int allNum = 6000
        int pageNum = allNum / pageSize
        for (int i = 1; i <= pageNum + 1; i++) {
            println "==syncSkuInfo " + i + "=="
            syncSkuInfo(i)
        }

    }

    public void syncSkuInfo(int page) {
        if (page < 1) {
            page = 1
        }

        SearchParams searchParams = new SearchParams()
        searchParams.setApp_key(JDConfig.APP_KEY)
        searchParams.setApp_secret(JDConfig.SECRET_KEY)
        searchParams.setIsCoupon(1)
        searchParams.setPageIndex(page)
        searchParams.setPageSize(pageSize)
        searchParams.setSortName("inOrderCount30Days")
        searchParams.setSort("desc")
        JSONArray jsonArray = getGoodsListBySearch(searchParams)
        println jsonArray.size()

        ArrayList<Object> bulkList = new ArrayList<>()
        if (jsonArray != null) {
            for (Object object : jsonArray) {
                String objJson = JSONObject.toJSONString(object)
                JDSkuInfoESMap jdSkuInfoESMap = JSONObject.parseObject(objJson, JDSkuInfoESMap.class)
                jdSkuInfoESMap.setId(Long.toString(jdSkuInfoESMap.getSkuId()))
                jdSkuInfoESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
                bulkList.add(jdSkuInfoESMap)
            }
        }
        if (bulkList.size() > 0) {
            jdSkuInfoESService.saveBulk(bulkList)
        }
    }


    /**
     * 调用商品搜索接口
     * @param searchParams
     * @return
     */
    public JSONArray getGoodsListBySearch(SearchParams searchParams) {
        String jsonStr = jdUnionService.getGoodsListBySearch(searchParams)
        JSONObject jsonObjec = (JSONObject) JSONObject.parse(jsonStr)
        String code = jsonObjec.get("code")
        String message = jsonObjec.get("message")
        printf "code:" + code + "  message:" + message
        if (code.equals('200')) {
            if (jsonObjec.get('data') != null) {
                JSONArray jsonArray = jsonObjec.get('data') as JSONArray
                String totalCount = jsonObjec.get('totalCount')
                println "totalCount:" + totalCount
                return jsonArray
            } else {
                return null
            }

        } else {
            return null
        }
    }

    public List<JDSkuInfoESMap> getSkuListBySkuIDs(String[] ids) {
        QueryBuilder query
        if (ids != null) {
            query = QueryBuilders.termsQuery("skuId", ids) as QueryBuilder
        } else {
            query = new MatchAllQueryBuilder();
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(query as org.elasticsearch.index.query.QueryBuilder)
        sourceBuilder.explain(true)// 设置是否按查询匹配度排序
        sourceBuilder.from(0)
        sourceBuilder.size(ids.length)
        SearchRequest searchRequest = new SearchRequest()
        searchRequest.source(sourceBuilder)

        List<JDSkuInfoESMap> list = new ArrayList()
        SearchHits hits = jdSkuInfoESService.find(searchRequest)
        SearchHit[] hitList = hits.getHits()
        for (int i = 0; i < ids.length; i++) {
            String skuId = ids[i]
            for (int k = 0; k < hitList.length; k++) {
                SearchHit hit = hitList[k]
                Map<String, Object> map = hit.getSourceAsMap()
                String _skuId = (String) map.get("skuId")
                if (skuId == _skuId) {
                    String json = hit.getSourceAsString()
                    JDSkuInfoESMap jdSkuInfoESMap = jdSkuInfoESService.getObjectFromJson(json, JDSkuInfoESMap.class) as JDSkuInfoESMap
                    jdSkuInfoESMap = checkCoupon(jdSkuInfoESMap)
                    list.add(jdSkuInfoESMap)
                }
            }
        }
        return list
    }

    public JDSkuInfoESMap checkCoupon(JDSkuInfoESMap jdSkuInfoESMap) {
        Coupon[] couponList = jdSkuInfoESMap.getCouponInfo().getCouponList()
        List<Coupon> _cList = new ArrayList<>()
        if (couponList.size() > 0) {
            for (Coupon coupon : couponList) {
                if (coupon.isBest == 1) {
                    long _endTime = coupon.getEndTime
                    long _nowTime = System.currentTimeMillis()
                    if (_endTime > _nowTime) {
                        _cList.add(coupon)
                    }
                }
            }
            jdSkuInfoESMap.getCouponInfo().setCouponList(_cList.toArray() as Coupon[])
        }
        return jdSkuInfoESMap
    }
}
