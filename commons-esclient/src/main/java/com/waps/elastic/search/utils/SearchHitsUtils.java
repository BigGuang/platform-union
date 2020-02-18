package com.waps.elastic.search.utils;

import com.waps.elastic.search.ESReturnList;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchHitsUtils {
    public static ESReturnList getHits2ReturnMap(SearchHits hits) {
        ESReturnList esReturnList = new ESReturnList();
        List<Map<String, Object>> _list = new ArrayList<>();
        if (hits != null) {
            esReturnList.setTotal(hits.getTotalHits().value);
            for (SearchHit hit : hits) {
                _list.add(hit.getSourceAsMap());
            }
        }
        esReturnList.setList(_list);
        return esReturnList;
    }
}