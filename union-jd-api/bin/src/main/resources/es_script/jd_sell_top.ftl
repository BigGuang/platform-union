{
  "aggs": {
    "group_by": {
      "terms": {
        "field": "skuList.skuId",
        "order": {
          "1": "desc"
        },
        "size": ${top_num!"50"}
      },
      "aggs": {
        "1": {
          "cardinality": {
            "field": "orderId"
          }
        },
        "3": {
          "sum": {
            "field": "skuList.estimateFee"
          }
        }
      }
    }
  },
  "size": 0,
  "_source": {
    "excludes": []
  },
  "stored_fields": [
    "*"
  ],
  "script_fields": {},
  "docvalue_fields": [
    {
      "field": "createtime",
      "format": "date_time"
    },
    {
      "field": "finishTime_Date",
      "format": "date_time"
    },
    {
      "field": "orderTime_Date",
      "format": "date_time"
    }
  ],
  "query": {
    "bool": {
      "must": [
        {
          "bool": {
            "minimum_should_match": 1,
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
              },
              {
                "match_phrase": {
                  "validCode": "18"
                }
              }
            ]
          }
        },
        {
          "range": {
            "orderTime_Date": {
              "format": "strict_date_optional_time",
              "gte": "${start_day}",
              "lte": "${end_day}"
            }
          }
        }
      ],
      "filter": [
        {
          "match_all": {}
        },
        {
          "match_all": {}
        }
      ],
      "should": [],
      "must_not": []
    }
  }
}