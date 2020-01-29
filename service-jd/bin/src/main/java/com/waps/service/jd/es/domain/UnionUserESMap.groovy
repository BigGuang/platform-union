package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean
import org.elasticsearch.common.geo.GeoPoint

class UnionUserESMap extends ESBaseBean {
    String user_id
    String wx_id
    String open_id
    String user_name
    String user_icon
    String user_sex
    String user_type
    String user_pwd_md5
    Long points
    String channel_id
    String channel_name
    String country
    String province
    String city
    String phone
    String ip
    GeoPoint location
    String invite_code
    String f_name
    String f_id
    String g_name
    String g_id
    String from_type
    String status
    String u_code
    String f_code
    String f_open_id   //上级用户openID
    String t_open_id
    String role_id    //0:普通会员，1:超级会员，2:导师，3：团长
    String createtime
    String modifytime
}
