package com.waps.service.jd.api.bean

import jd.union.open.user.pid.get.request.PidReq

class PidParams extends PidReq implements Serializable {
    String app_key
    String app_secret
}
