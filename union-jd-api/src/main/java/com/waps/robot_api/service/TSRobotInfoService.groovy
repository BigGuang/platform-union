package com.waps.robot_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.robot_api.bean.request.TSPostRobotInfoBean
import com.waps.robot_api.bean.response.TSResponseRobotInfoBean
import com.waps.robot_api.utils.APIConfig
import com.waps.union_jd_api.utils.HttpUtils
import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TSRobotInfoService {
    @Autowired
    TSAuthService tsAuthService

    /**
     * 实时获取机器人信息列表，返回json字符串
     * @return
     */
    public String getRobotInfoListStr() {
        String url = APIConfig.ROBOT_INFO_LIST.replace("{TOKEN}", tsAuthService.getToken())
        TSPostRobotInfoBean tsPostRobotInfoBean = new TSPostRobotInfoBean()
        tsPostRobotInfoBean.setVcRobotSerialNos(APIConfig.robotList)
        tsPostRobotInfoBean.setnPageIndex(1)
        String retJson = HttpUtils.postJsonString(url, JSONObject.toJSONString(tsPostRobotInfoBean))
        return retJson
    }

    /**
     * 实时获取机器人信息列表，返回bean
     * @return
     */
    public TSResponseRobotInfoBean getRobotInfoListBean() {
        String retJson = getRobotInfoListStr()
        if (!StringUtils.isNull(retJson)) {
            TSResponseRobotInfoBean tsResponseRobotInfoBean = JSONObject.parseObject(retJson, TSResponseRobotInfoBean.class) as TSResponseRobotInfoBean
            println tsResponseRobotInfoBean
            return tsResponseRobotInfoBean
        } else {
            return null
        }
    }
}
