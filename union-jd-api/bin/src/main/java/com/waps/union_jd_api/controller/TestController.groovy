package com.waps.union_jd_api.controller

import com.waps.service.jd.db.dao.WapsJdUserDao
import com.waps.service.jd.db.model.WapsJdUser
import com.waps.service.jd.db.model.WapsJdUserExample
import com.waps.service.jd.es.domain.JDMediaInfoESMap
import com.waps.service.jd.es.domain.UnionAppUserESMap
import com.waps.service.jd.es.service.UnionAppUserESService
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.service.JDMediaService
import com.waps.union_jd_api.service.UnionAppUserService
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/test")
class TestController {
    @Autowired
    private UnionAppUserService unionAppUserService
    @Autowired
    private UnionAppUserESService unionAppUserESService
    @Autowired
    private JDMediaService jdMediaService

    @Resource
    private WapsJdUserDao wapsJdUserDao

    /**
     * 测试用户不断查找上级，直到找到channel_id
     */
    @RequestMapping(value = "/user_pid", method = RequestMethod.GET)
    public void loopFindUserPID(@RequestParam(value = "uid", required = false) String uid,
                                HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        UnionAppUserESMap unionAppUserESMap = unionAppUserService.loadUserByID(uid)
        if (unionAppUserESMap) {
            long commissionPositionID = unionAppUserService.findCommissionPositionID(unionAppUserESMap, true)
            ResponseUtils.write(response, new ReturnMessageBean(200, "", "" + commissionPositionID).toString());
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(404, "用户不存在").toString());
        }
    }


    @RequestMapping(value = "/pid_info", method = RequestMethod.GET)
    public void loadPidInfo(@RequestParam(value = "id", required = false) Integer id,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        WapsJdUser wapsJdUser = wapsJdUserDao.selectByPrimaryKey(id)

        ResponseUtils.write(response, new ReturnMessageBean(200, "", wapsJdUser).toString())
    }

    @RequestMapping(value = "/get_pid", method = RequestMethod.GET)
    public void getPid(@RequestParam(value = "channel_name", required = false) String channel_name,
                       HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

        JDMediaInfoESMap jdMediaInfoESMap = jdMediaService.getMediaInfoByChannelName(channel_name)

        ResponseUtils.write(response, new ReturnMessageBean(200, "", jdMediaInfoESMap).toString())
    }

}
