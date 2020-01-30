{
    "size":0,
    "aggs": {
        "group_by": {
            "terms": {
                "field": "media_name.raw",
                "size": 10,
                "order": {
                    "_count": "desc"
                }
            },
        "aggs": {
            "media_info": {
                "terms": {
                    "field": "media_id.raw",
                    "size": 10,
                        "order": {
                        "_count": "desc"
                        }
                    }
                }
            }
        }
    }
}