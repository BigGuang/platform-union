package com.waps.elastic.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ESReturnList {
    long total = 0;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    List<Map<String, Object>> list = new ArrayList<>();
}
