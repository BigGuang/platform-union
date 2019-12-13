package com.waps.union_jd_api.service

import com.cloopen.rest.sdk.CCPRestSDK
import com.waps.service.jd.es.domain.UnionSmsESMap
import com.waps.service.jd.es.service.UnionSmsESService
import com.waps.tools.security.MD5
import com.waps.union_jd_api.utils.DateUtils
import com.waps.utils.StringUtils
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UnionSmsService {
    final String AccountSid = "8a48b55150b86ee80150c6dcf5892876"
    final String AccountToken = "7438b67338394ddfbe9ea81e6fc9313e"
    final String AppId = "8aaf07086e0115bb016e3fac7b7c21fe"
    final String TemplateID = "483796"   //

    @Autowired
    UnionSmsESService unionSmsESService

    public MessageBean sendCheckSMS(String phone) {
        //发送短信，根据发送结果保存验证码
        MessageBean messageBean = new MessageBean()
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000)
        if (!StringUtils.isNull(phone) && phone.length() == 11) {
            String id = new MD5().getMD5(phone)
            boolean flg = sendSMS(phone, verifyCode)
//            boolean flg = true
            if (flg) {
                //发送成功
                messageBean.setCode(200)
                messageBean.setMessage("验证码下发成功")
                UnionSmsESMap unionSmsESMap = new UnionSmsESMap()
                unionSmsESMap.setId(id)
                unionSmsESMap.setPhone(phone)
                unionSmsESMap.setCode(verifyCode)
                long expTime = System.currentTimeMillis() + 300000
                println System.currentTimeMillis() + "  " + expTime
                unionSmsESMap.setExpiretime(expTime)
                unionSmsESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ""))
                unionSmsESService.save(id,unionSmsESMap)
            } else {
                //发送失败
                messageBean.setCode(500)
                messageBean.setMessage("此号码已到今日上限,请24小时后再验证")
            }
        } else {
            messageBean.setCode(501)
            messageBean.setMessage("手机号格式不正确")
        }
        return messageBean

    }

    public MessageBean checkSMS(String phone, String code) {
        MessageBean messageBean = new MessageBean()
        String id = new MD5().getMD5(phone)
        long now = System.currentTimeMillis()
        UnionSmsESMap unionSmsESMap = loadUnionSMS(id)
        if (unionSmsESMap != null) {
            println unionSmsESMap.getExpiretime() + "  " + now
            if (unionSmsESMap.getExpiretime() > now) {
                if (unionSmsESMap.getCode().equalsIgnoreCase(code)) {
                    messageBean.setCode(200)
                    messageBean.setMessage("验证码正确,通过验证")
                } else {
                    messageBean.setCode(502)
                    messageBean.setMessage("验证码错误,请重新输入")
                }
            } else {
                messageBean.setCode(600)
                messageBean.setMessage("验证码已超时,请重新发送")
            }
        } else {
            messageBean.setCode(400)
            messageBean.setMessage("未找到验证码,请先输入手机号验证")
        }
        return messageBean
    }

    public UnionSmsESMap loadUnionSMS(String id) {
        Map<String, String> kvMap = new HashMap<>()
        kvMap.put("id", id)
        return loadUnionSMSByRequest(kvMap)
    }

    public UnionSmsESMap loadUnionSMSByRequest(Map<String, String> kvMap) {
        try {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
            Iterator it = kvMap.entrySet().iterator()
            while (it.hasNext()) {
                Map.Entry entity = (Map.Entry) it.next()
                String field = (String) entity.getKey()
                String value = (String) entity.getValue()
                System.out.println(entity.getKey() + "=" + entity.getValue())
                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(field, value))
            }

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(boolQueryBuilder)
            sourceBuilder.explain(true)// 设置是否按查询匹配度排序
            sourceBuilder.from(0)
            sourceBuilder.size(1)
            SearchRequest searchRequest = new SearchRequest()
            searchRequest.source(sourceBuilder)
            SearchHits hits = unionSmsESService.find(searchRequest)
            SearchHit[] hit = hits.getHits()
            if (hit.size() > 0) {
                String json = hit[0].getSourceAsString()
                if (!StringUtils.isNull(json)) {
                    UnionSmsESMap smsESMap = unionSmsESService.getObjectFromJson(json, UnionSmsESMap.class) as UnionSmsESMap
                    return smsESMap
                }
            }
        } catch (Exception e) {
            println "UnionUserService loadUserByRequest ERROR:" + e.getLocalizedMessage()
        }
    }

    public boolean sendSMS(String to, String code) {
        HashMap<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount(AccountSid, AccountToken);// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId(AppId);// 初始化应用ID
        def str = [code, '5分钟'] as String[]
        result = restAPI.sendTemplateSMS(to, TemplateID, str);//模板Id，不带此参数查询全部可用模板

        System.out.println("sendTemplateSMS result=" + result);

        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
            return true
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
            return false
        }
    }

}

class MessageBean {
    int code
    String message
}
