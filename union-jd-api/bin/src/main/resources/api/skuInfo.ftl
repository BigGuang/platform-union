<#if owner?default("")?trim?length gt 0 && owner=='g'>【京东自营】</#if>${skuName}
<#if good?default("")?trim?length gt 0 >好评率: ${good}%</#if>
<#setting number_format="0.##">
京东价: ${jdPrice}元
<#if couponPrice gt 0>
优惠券: ${couponPrice}元
内购价: ${newPrice}元🉐
</#if>
入口:${shortUrl}
--------------------