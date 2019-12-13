package com.waps.service.jd.api.bean

import jd.union.open.promotion.bysubunionid.get.request.PromotionCodeReq

class PromotionCodeParams extends PromotionCodeReq implements Serializable{
//    String materialId    //推广物料链接，建议链接使用微Q前缀，能较好适配微信手Q页面 例：https://wqitem.jd.com/item/view?sku=23484023378
//    String subUnionId    //子联盟ID（需要联系运营开通权限才能拿到数据）  例:618_18_c35***e6a
//    Long positionId    //推广位ID  例：6
//    String pid           //子帐号身份标识，格式为子站长ID_子站长网站ID_子站长推广位ID， 例:618_618_6018
//    String couponUrl
//    //优惠券领取链接，在使用优惠券、商品二合一功能时入参，且materialId须为商品详情页链接 例：http://coupon.jd.com/ilink/get/get_coupon.action?XXXXXXX
//    Integer chainType = 2    //转链类型，1：长链， 2 ：短链 ，3： 长链+短链，默认短链
    String app_key
    String app_secret
}
