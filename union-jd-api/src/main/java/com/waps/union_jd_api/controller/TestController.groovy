package com.waps.union_jd_api.controller

import com.waps.service.jd.es.domain.UnionAppUserESMap
import com.waps.service.jd.es.service.UnionAppUserESService
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.service.UnionAppUserService
import com.waps.utils.ResponseUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/test")
class TestController {
    @Autowired
    private UnionAppUserService unionAppUserService
    @Autowired
    private UnionAppUserESService unionAppUserESService

    /**
     * 测试用户不断查找上级，直到找到channel_id
     */
    @RequestMapping(value = "/user_pid", method = RequestMethod.GET)
    public void loopFindUserPID(@RequestParam(value = "uid", required = false) String uid,
                                HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        UnionAppUserESMap unionAppUserESMap = unionAppUserService.loadUserByID(uid)
        if (unionAppUserESMap) {
            long commissionPositionID=unionAppUserService.findCommissionPositionID(unionAppUserESMap)
            ResponseUtils.write(response, new ReturnMessageBean(200, "",""+commissionPositionID).toString());
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(404, "用户不存在").toString());
        }
    }
}
