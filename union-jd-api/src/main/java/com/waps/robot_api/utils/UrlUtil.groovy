package com.waps.robot_api.utils

import com.waps.utils.StringUtils

class UrlUtil {
    /**
     * 解析url
     *
     * @param url
     * @return
     */
    public static Map<String, String> parseBody(String body) {
        Map<String, String> paramMap = new HashMap<>()
        if (!StringUtils.isNull(body)) {
            //有参数
            body = body + "&"
            String[] params = body.split("&")
            for (String param : params) {
                if(!StringUtils.isNull(param)) {
                    String[] keyValue = param.split("=")
                    paramMap.put(keyValue[0], keyValue[1])
                }
            }
        }
        return paramMap
    }
}
