package com.waps.elastic.search.utils;

public class PageUtils {
    public int getFrom() {
        return from;
    }

    public int getSize() {
        return size;
    }

    int from=0;
    int size=20;

    public PageUtils(int page, int size){
        this.size=size;
        if (size < 0) {
            size = 0;
        }
        if (page < 1) {
            page = 1;
        }
        from = 0;
        from = (page - 1) * size;
    }
}
