package com.waps.union_jd_api.bean

import com.alibaba.fastjson.JSONObject

class ReturnMessageBean {
    ReturnMessageBean(int code, String message, Object data) {
        this.code = code
        this.message = message
        if (data != null)
            this.data = data
    }

    ReturnMessageBean(int code, String message) {
        this.code = code
        this.message = message
    }

    int code = 200
    String message
    Object data

    public String toString() {
        return JSONObject.toJSONString(this)
    }
}
