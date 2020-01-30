{
    "from": ${from},
    "size": ${size},
    "track_total_hits": true,
    "query": {
        "bool": {
            "must": [
                {
                "match_all": {}
                }
            <#if search??>
                ,{
                    "multi_match": {
                        "query":  "${search}",
                        "fields": [ "skuList.skuName", "orderId","skuList.skuId","skuList.positionId"]
                    }
                }
            </#if>
            <#if isValid??>
                ,{
                    "bool": {
                        "should": [
                            {
                                "match_phrase": {
                                    "validCode": "16"
                                }
                            },
                            {
                                "match_phrase": {
                                    "validCode": "17"
                                }
                            }
                        ],
                        "minimum_should_match": 1
                    }
                }
            </#if>
            <#if stime?? && etime??>
                ,{
                    "range": {
                        "orderTime_Date": {
                            "gte": "${stime}",
                            "lte": "${etime}"
                        }
                    }
                }
            </#if>
            ]
        }
    },
        "sort": {
            "orderTime_Date": {
                "order": "${sort}"
        }
    }
}