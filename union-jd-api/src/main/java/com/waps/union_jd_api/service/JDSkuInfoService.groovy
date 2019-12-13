package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.api.bean.SearchParams
import com.waps.service.jd.api.service.JdUnionService
import com.waps.service.jd.es.domain.JDSkuInfoESMap
import com.waps.service.jd.es.service.JDSkuInfoESService
import com.waps.union_jd_api.utils.DateUtils
import com.waps.union_jd_api.utils.JDConfig
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
}
