package com.waps.service.jd.api.bean

import com.alibaba.fastjson.JSONObject

class ErrorBean {
    Integer code
    String message

    public static String returnErrorJson(Integer code, String message) {
        ErrorBean errorBean = new ErrorBean()
        errorBean.setCode(code)
        errorBean.setMessage(message)
        return JSONObject.toJSONString(errorBean)
    }
}
