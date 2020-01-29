package com.waps.service.jd.api.bean
import jd.union.open.promotion.common.get.request.PromotionCodeReq

class PromotionCodeCommonParams extends PromotionCodeReq implements Serializable{
    String app_key
    String app_secret
}
