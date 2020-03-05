package com.waps.elastic.search.utils;

import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;

public class AggregationsUtils {
    public static LinkedHashMap getAggReturnMap(Aggregations aggregations, String key_params) {
        LinkedHashMap<String, Long> linkedHashMap = new LinkedHashMap<>();
        if (aggregations != null) {
            Terms genders = aggregations.get(key_params);
            for (Bucket entry : genders.getBuckets()) {
                String key = (String) entry.getKey();
                Long count = entry.getDocCount();
                linkedHashMap.put(key, count);
            }
        }
        return linkedHashMap;
    }
}