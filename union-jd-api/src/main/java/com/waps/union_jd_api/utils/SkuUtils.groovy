package com.waps.union_jd_api.utils

import com.waps.utils.StringUtils

class SkuUtils {
    /**
     * 匹配出url中的skuID
     * @param url
     * @param paramsName
     * @return
     */
    public static getSkuIDFromUrl(String url, paramsName) {
        String sukId = ""
        String param = "sku"
        if (!StringUtils.isNull(paramsName)) {
            param = paramsName
        }
        param = param + "="
        if (!StringUtils.isNull(url)) {
            if (url.indexOf(param) > 0) {
                String _temp = url.substring(url.indexOf(param) + param.length(), url.length());
                if (_temp.indexOf("&") > 0) {
                    sukId = _temp.substring(0, _temp.indexOf("&"));
                } else {
                    sukId = _temp;
                }
            }
        }
        return sukId
    }
}
