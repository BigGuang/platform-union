package com.waps.service.jd.api.bean

import jd.union.open.order.query.request.OrderReq

class OrderQueryParams extends OrderReq implements Serializable{
    String app_key
    String app_secret
}
