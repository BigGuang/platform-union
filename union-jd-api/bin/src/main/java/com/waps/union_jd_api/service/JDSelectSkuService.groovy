package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.api.bean.SearchParams
import com.waps.service.jd.api.service.JdUnionService
import com.waps.service.jd.es.service.JDOrderESService
import com.waps.union_jd_api.utils.JDConfig
import jd.union.open.goods.query.response.GoodsResp
import org.elasticsearch.search.aggregations.Aggregations
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JDSelectSkuService {

    @Autowired
    private JdUnionService jdUnionService
    @Autowired
    private JDOrderESService jdOrderESService

    public Long[] getSellTop(String start_day, String end_day) {
        ArrayList<Long> keyList = new ArrayList()
        HashMap params = new HashMap()
        params.put("start_day", start_day)
        params.put("end_day", end_day)
        Aggregations aggregations = jdOrderESService.groupByFreeMarkerFromResource("es_script/jd_sell_top.ftl", params)

        Terms genders = aggregations.get("group_by");

        for (Terms.Bucket entry : genders.getBuckets()) {
            String key = (String) entry.getKey()
            try {
                long skuId = Long.parseLong(key)
                keyList.add(skuId);
            } catch (Exception e) {

            }
        }

        return (Long[]) keyList.toArray(new Long[keyList.size()])
    }

    /**
     * 通过skuid查询，一次最多100个
     * @param arrayList
     * @return
     */
    public ArrayList<GoodsResp> getSelectSkuInfo(Long[] arrayList) {
        SearchParams searchParams = new SearchParams()
        searchParams.setApp_key(JDConfig.APP_KEY)
        searchParams.setApp_secret(JDConfig.SECRET_KEY)
        searchParams.setSkuIds(arrayList)
        JSONArray jsonArray = getGoodsListBySearch(searchParams)
        GoodsResp[] goodsList = jsonArray.toArray(GoodsResp[]) as GoodsResp[]
        ArrayList<GoodsResp> goodsRespArrayList = new ArrayList<>()

        for (Long skuId in arrayList) {
            for (GoodsResp goodsResp in goodsList) {
                if (skuId == goodsResp.getSkuId()) {
                    goodsRespArrayList.add(goodsResp)
                }
            }
        }


        return goodsRespArrayList
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
}
