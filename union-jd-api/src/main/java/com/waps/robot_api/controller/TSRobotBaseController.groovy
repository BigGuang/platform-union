package com.waps.robot_api.controller

import com.waps.robot_api.bean.response.TSResponseRobotInfoBean
import com.waps.robot_api.service.SendCount
import com.waps.robot_api.service.TSAuthService
import com.waps.robot_api.service.TSCallBackService
import com.waps.robot_api.service.TSRobotConfigService
import com.waps.robot_api.service.TSSendTaskService
import com.waps.robot_api.service.TSSendTaskUserService
import com.waps.robot_api.service.TestTaskService
import com.waps.robot_api.service.TimeLineService
import com.waps.robot_api.utils.TSApiConfig
import com.waps.robot_api.utils.TestRequest
import com.waps.robot_api.utils.UrlUtil
import com.waps.service.jd.es.service.TSSendTaskUserESService
import com.waps.tools.test.TestUtils
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.utils.ResponseUtils
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.SimpleDateFormat

@Controller
@RequestMapping("/ts/robot/base")
class TSRobotBaseController {
    @Autowired
    private TSAuthService tuSeAuthService
    @Autowired
    private TSRobotConfigService tsRobotConfigService
    @Autowired
    private TestTaskService testTaskService
    @Autowired
    private TSSendTaskService tsSendTaskService
    @Autowired
    private TSSendTaskUserService tsSendTaskUserService
    @Autowired
    private TimeLineService timeLineService

    @RequestMapping(value = "/test_time_count")
    public void test_time_count(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        SendCount count=tsSendTaskUserService.getSendCount("s002")
        TestUtils.outPrint(count)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", count))
    }

    @RequestMapping(value = "/test_time")
    public void test_time(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")
        List<Date> list = timeLineService.getTimeLine()
        println list
        for (Date d : list) {
            println timeFormat.format(d)
        }
        Date nextDate = timeLineService.getSendTaskNextDateTime()
        if (nextDate != null) {
            String send_day = dayFormat.format(nextDate)
            String send_time = timeFormat.format(nextDate)
            Map params = new HashMap()
            params.put("send_day", send_day)
            params.put("send_time", send_time)
            ResponseUtils.write(response, new ReturnMessageBean(200, "", params))
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(404, ""))
        }
    }

    @RequestMapping(value = "/test_task")
    public void test(
            @RequestParam(value = "num", required = false) Integer num,
            @RequestParam(value = "wait", required = false) Integer wait,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        List<String> test_list = new ArrayList<>()
        for (int i = 0; i < 1000; i++) {
            String content = "test_" + i
            test_list.add(content)
        }
        String params = "{\"pool_num\":" + num + ",\"wait_time\":" + wait + "}"
        testTaskService.setParams(params)
        testTaskService.testTask(test_list)
        ResponseUtils.write(response, new ReturnMessageBean(200, ""))
    }

    @RequestMapping(value = "/token")
    public void getToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String token = tuSeAuthService.getToken()
        ResponseUtils.write(response, new ReturnMessageBean(200, "", token))
    }

    /**
     * 主动调用同步
     * @param request
     * @param response
     */
    @RequestMapping(value = "/sync")
    public void sync2ES(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        tsRobotConfigService.syncAllRobotAndRoom()
        ResponseUtils.write(response, new ReturnMessageBean(200, ""))
    }


    @RequestMapping(value = "/list_live", method = RequestMethod.POST)
    public void getRobotList(
            @RequestBody RobotListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        println "/ts/robot/base/list"
        println params.getRobot_ids()
        if (params.getPage() < 1) {
            params.setPage(1)
        }
        TSResponseRobotInfoBean responseRobotInfoBean = tsRobotConfigService.getRobotInfoListBean(params.getRobot_ids(), params.getPage())
        ResponseUtils.write(response, new ReturnMessageBean(200, "", responseRobotInfoBean))
    }

    /**
     * 从ES中读取定时同步下来的机器人列表
     * @param params
     * @param request
     * @param response
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public void getESRobotList(
            @RequestBody RobotListParams params,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        SearchHits hits = tsRobotConfigService.getRobotInfoListFromES(params.getPage(), params.getSize())
        Map retMap = new HashMap()
        long total = 0
        List<Map> _list = new ArrayList<>()
        if (hits != null) {
            total = hits.getTotalHits().value
            for (SearchHit hit : hits) {
                _list.add(hit.getSourceAsMap())
            }
        }
        retMap.put("total", total)
        retMap.put("list", _list)
        ResponseUtils.write(response, new ReturnMessageBean(200, "", retMap))
    }

}

class PostParams {
    String strContext
    String strSign
}

class RobotListParams {
    String[] robot_ids
    int page = 1
    int size = 50
}
