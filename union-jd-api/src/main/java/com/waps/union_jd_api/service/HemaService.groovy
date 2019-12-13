package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.es.domain.UnionUserESMap
import com.waps.tools.security.MD5
import com.waps.union_jd_api.utils.Config
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HemaService {

    @Autowired
    UnionUserService unionUserService

    public void startSyncHemaUserTree() {
        String url = Config.HEMA_SYNC_USER_TREE_URL
        String jsonStr = StringUtils.getUrlTxt(url)
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)

        Integer total = jsonObject.getInteger("total")
        Integer code = jsonObject.getInteger("code")
        String errMsg = jsonObject.getString("errMsg")
        if (code == 0) {

            JSONArray jsonArray = jsonObject.getJSONArray("data")
            HmUserInfo[] userInfoList = jsonArray.toArray() as HmUserInfo[]
            println "===================="
            println "同步:" + userInfoList.length + "   total:" + total
            for (int i = 0; i < userInfoList.length; i++) {
                HmUserInfo hmUserInfo = userInfoList[i]

                if (!StringUtils.isNull(hmUserInfo.getOpenId())) {
                    UnionUserESMap userESMap = new UnionUserESMap()
                    userESMap.setOpen_id(hmUserInfo.getOpenId())
                    userESMap.setChannel_name(hmUserInfo.getUid())
                    userESMap.setChannel_id('')
                    userESMap.setF_name(hmUserInfo.getFid())
                    userESMap.setF_id('')
                    userESMap.setG_name(hmUserInfo.getGid())
                    userESMap.setG_id('')
                    userESMap.setFrom_type("mini_hm")
                    //todo:通知到老王的接口
                    println "同步" + hmUserInfo.getOpenId() + "  " + hmUserInfo
                    boolean flg=request2data(hmUserInfo)
                    println "保存" + hmUserInfo.getOpenId()
                    unionUserService.saveUser(userESMap)
                }
            }
        } else {
            println "ERROR:" + errMsg
        }
    }

    /**
     * 通知到群主用户关系同步接口
     */
    public boolean request2data(HmUserInfo hmUserInfo) {
        if (!StringUtils.isNull(hmUserInfo.getUid())) {
            String r_num = System.currentTimeMillis()
//            uid+gid+fid+rnum+"Waps_jd_MD5key"

            String gid=hmUserInfo.getGid()
            String fid=hmUserInfo.getFid()
            if(StringUtils.isNull(gid))gid=''
            if(StringUtils.isNull(fid))fid=''

            String str=hmUserInfo.getUid() + gid + fid + r_num + "Waps_jd_MD5key";
            String md5 = new MD5().getMD5(hmUserInfo.getUid() + gid + fid + r_num + "Waps_jd_MD5key").toLowerCase()
            String url = Config.SYNC_USER_TREE_URL
                    .replace("[RNUM]", r_num)
                    .replace("[MD5]", md5)
                    .replace("[UID]", hmUserInfo.getUid())
                    .replace("[FID]", fid)
                    .replace("[GID]", gid)
            String json = StringUtils.getUrlTxt(url)
            println "返回:"+json
            return true
//            if (!StringUtils.isNull(json)) {
//                JSONObject retObj = JSONObject.parseObject(json)
//                if (retObj.getInteger("code") == 200) {
//                    println retObj.getString("msg")
//                    return true
//                } else {
//                    println retObj.getString("msg")
//                    return false
//                }
//            }
        }
    }
}

class HmUserInfo {
    String openId
    String uid
    String fid
    String gid
}
