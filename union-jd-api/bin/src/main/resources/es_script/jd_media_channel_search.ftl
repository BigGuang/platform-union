{
    "from": "${from}",
    "size": "${size}",
    "track_total_hits": true,
    "query": {
<#if (media_id?? || channel_name??)>
    "bool": {
        "should": [
<#if media_id??>
                {
                    "match": {
                        "media_id.raw": {
                            "query": "${media_id}",
                            "boost": 2
                        }
                    }
                }
</#if>
<#if (media_id?? && channel_name??)>,</#if>
<#if channel_name??>
                {
                    "prefix": {
                        "channel_name":"${channel_name?lower_case}"
                    }
                }
</#if>
            ]
        }
<#else>
    "match_all": {}
</#if>
    },
    "sort":{
        "channel_name.raw":{
            "order": "asc"
        }
    }
}