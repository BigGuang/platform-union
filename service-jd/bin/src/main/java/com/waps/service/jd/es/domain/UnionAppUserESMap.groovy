package com.waps.service.jd.es.domain

import com.waps.elastic.search.ESBaseBean
import org.elasticsearch.common.geo.GeoPoint

class UnionAppUserESMap extends ESBaseBean {
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
    String invite_user_id    //邀请人ID，单独记录，和f_user_id区分
    String g_name
    String g_id
    String from_type
    String status
    String u_code
    String f_code
    String f_user_id     //上级群主ID
    String t_user_id     //导师ID
    String a_user_id     //合伙人ID
    String s_user_id     //超级合伙人ID
    String role_id    //0:普通会员，1:超级会员，2:导师，3：团长
    String createtime
    String modifytime
}
