package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.api.bean.PromotionCodeParams
import com.waps.service.jd.es.domain.JDMediaInfoESMap
import com.waps.union_jd_api.utils.JDConfig
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JDEventService {

    @Autowired
    private JDSkuRobotService jdSkuRobotService

    @Autowired
    JDMediaService jdMediaService

    public List<JDEventBean> getEventList() {
        String config_json = "api/event_list.json";
        File typeJson = new File(this.getClass().getClassLoader().getResource(config_json).getFile());
        String json = StringUtils.getFileTxt(typeJson.getPath())
        JSONObject jsonObject = JSONObject.parseObject(json)
        JSONArray jsonArray = jsonObject["data"] as JSONArray

        List<JDEventBean> list = new ArrayList<>()
        if (jsonArray != null) {

            for (Object object : jsonArray) {
                String objJson = JSONObject.toJSONString(object);
                JDEventBean jdEventBean = JSONObject.parseObject(objJson, JDEventBean.class)
                list.add(jdEventBean)
            }
        }

        return list
    }

    public JDEventBean getEventInfo(String id) {
        List<JDEventBean> list = getEventList()
        for (JDEventBean eventBean : list) {
            if (id.equalsIgnoreCase(eventBean.getId())) {
                return eventBean
            }
        }
    }

    public JDEventBean getEvent2Url(String id, String channel_name) {
        if (!StringUtils.isNull(channel_name)) {
            JDMediaInfoESMap jdMediaInfoESMap = jdMediaService.getMediaInfoByChannelName(channel_name)
            String channel_id = jdMediaInfoESMap.getChannel_id()
            JDEventBean eventBean = getEventInfo(id)

            if (eventBean && !StringUtils.isNull(eventBean.getMaterialUrl())) {

                try {
                    long pid = Long.parseLong(channel_id)
                    PromotionCodeParams params = new PromotionCodeParams()
                    params.setApp_key(JDConfig.APP_KEY)
                    params.setApp_secret(JDConfig.SECRET_KEY)
                    params.setMaterialId(eventBean.getMaterialUrl())
                    params.setPositionId(pid)
                    params.setChainType(2)

                    String shortUrl = jdSkuRobotService.getUnionLink(params)

                    eventBean.setShortUrl(shortUrl)

                    return eventBean
                } catch (Exception e) {
                    println "getEvent2Url ERROR:" + e.getLocalizedMessage()
                }
            }
        }
    }


}

class JDEventBean {
    String id
    String name
    String desc
    String materialUrl
    String posterImage
    String shortUrl
    String codeSize
    String codeLeft
    String codeTop
    String endTime
    String startTime
}
