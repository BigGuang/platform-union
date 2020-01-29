<#if getRoomMsg=='true'>
    <#if text?default("")?trim?length gt 1>
@${contactName} 您要的 '${search}' :

${text}<#if assignChannelName?default("")?trim?length gt 1>(${assignChannelName})</#if>
    <#else>
@${contactName} 非常抱歉,无法搜索到关于 '${search}' 的商品,可尝试换个搜索词
    </#if>
<#else>
    <#if text?default("")?trim?length gt 1>
您要的'${search}':

${text}<#if assignChannelName?default("")?trim?length gt 1>(${assignChannelName})</#if>
    <#else>
非常抱歉,无法搜索到关于 '${search}' 的商品,可尝试换个搜索词
    </#if>
</#if><#if domain?default("")?trim?length gt 1>--------------------
更多优惠：http://${domain}
</#if>