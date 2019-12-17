package com.waps.union_jd_api.utils

import com.alibaba.fastjson.JSONObject

class ObjectUtils {
    def copyProperties(source, target) {
        source.properties.each { key, value ->
            if (target.hasProperty(key) && !(key in ['class', 'metaClass']))
                target[key] = value
        }
        return target
    }
}
