package com.waps.union_jd_api.controller

import com.waps.service.jd.es.domain.UnionPageLogESMap
import com.waps.service.jd.es.service.UnionPageLogESService
import com.waps.tools.ip.IPSeeker
import com.waps.tools.security.MD5
import com.waps.union_jd_api.utils.DateUtils
import com.waps.utils.CookieUtils
import com.waps.utils.RequestUtils
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import com.waps.utils.UserAgentUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.regex.Matcher
import java.util.regex.Pattern

@Controller
@RequestMapping("/h5")
class UnionPageLogController {

    @Autowired
    private UnionPageLogESService unionPageLogESService

    @RequestMapping(value = "/{page}")
    public void count(
            @PathVariable("page") String page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String id = System.currentTimeMillis()
        String ip = RequestUtils.getIp(request)
        String referer = request.getHeader("referer")
        String user_agent = request.getHeader("user-agent")

//        朋友圈 from=timeline&isappinstalled=0
//        微信群 from=groupmessage&isappinstalled=0
//        好友分享 from=singlemessage&isappinstalled=0


        UnionPageLogESMap unionPageLogESMap = new UnionPageLogESMap()
        unionPageLogESMap.setId(id)
        unionPageLogESMap.setIp(ip)
        unionPageLogESMap.setUser_agent(user_agent)
        unionPageLogESMap.setReferer(referer)
        unionPageLogESMap.setPage(page)
        if (!StringUtils.isNull(referer)) {
            String host = ""
            String regex = "(?<=/|)((\\w)+\\.)+\\w+"  //获取host
            Pattern pt = Pattern.compile(regex)
            Matcher mt = pt.matcher(referer)
            while (mt.find()) {
                host = mt.group()
            }

            if (!StringUtils.isNull(host)) {
                unionPageLogESMap.setHost(host)
                String channel_name = host.replaceAll("\\.wapg\\.cn", "")
                unionPageLogESMap.setChannel_name(channel_name)
            }

            String from = getParamsFrom(referer)
            if (!StringUtils.isNull(from)) {
                unionPageLogESMap.setFrom(from)
            }

            String search = getParamsSearch(referer)
            if (!StringUtils.isNull(search)) {
                unionPageLogESMap.setSearch(search)
            }
        }
        if (!StringUtils.isNull(ip)) {
            IPSeeker ipSeeker = IPSeeker.getInstance()
            String area = ipSeeker.getArea(ip)
            String isp = ipSeeker.getISP(ip)
            unionPageLogESMap.setArea(area)
            unionPageLogESMap.setIsp(isp)
        }

        String userId = getUserIDFromCookie(request, response)

        if (!StringUtils.isNull(user_agent)) {

            UserAgentUtils userAgentUtils = new UserAgentUtils(user_agent)
            String getOSVersion = userAgentUtils.getOSVersion()
            String getDeviceVandor = userAgentUtils.getDeviceVandor()
            String getOSName = userAgentUtils.getOSName()
            String getBrowserName = userAgentUtils.getBrowserName()

            if (!StringUtils.isNull(getDeviceVandor) || "Unknown".equalsIgnoreCase(getDeviceVandor)) {
                getDeviceVandor = userAgentUtils.getDevice()
            }

            unionPageLogESMap.setOsVersion(getOSVersion)
            unionPageLogESMap.setOsName(getOSName)
            unionPageLogESMap.setDeviceVandor(getDeviceVandor)
            unionPageLogESMap.setBrowserName(getBrowserName)

            if (StringUtils.isNull(userId)) {
                userId = new MD5().getMD5(user_agent + "_" + ip)
            }

        }

        unionPageLogESMap.setUser_id(userId)

        println "userId:" + userId


        unionPageLogESMap.setCreatetime(DateUtils.timeTmp2DateStr(System.currentTimeMillis() + ''))
        unionPageLogESService.save(unionPageLogESMap)

        ResponseUtils.write(response, "ok");
    }

    public static String getUserIDFromCookie(HttpServletRequest request, HttpServletResponse response) {
        String cookie_name = "uid"
        String user_id = null
        Cookie cookie = CookieUtils.getCookieByName(request, cookie_name)
        if (cookie != null) {
            user_id = cookie.getValue()
        }
        if (StringUtils.isNull(user_id)) {
            user_id = System.currentTimeMillis() + ""
            CookieUtils.addCookie(response, cookie_name, user_id, CookieUtils.COOKIE_AGE_MAX)
        }
        return user_id
    }

    public static String getParamsFrom(String referer) {
//        朋友圈 from=timeline&isappinstalled=0
//        微信群 from=groupmessage&isappinstalled=0
//        好友分享 from=singlemessage&isappinstalled=0
        println "referer:" + referer
        String from = ""
        if (!StringUtils.isNull(referer) && referer.indexOf("from") > 0) {
            String regex = "[?&]from=([^&#]+)"
            Pattern pt = Pattern.compile(regex)
            Matcher mt = pt.matcher(referer)
            while (mt.find()) {
                String param = mt.group()
                from = param.replaceAll("\\?from=", "")
            }
        }
        return from
    }

    public static String getParamsSearch(String referer) {
//        http://v557.wapg.cn/?r=search?kw=%E6%97%A0%E7%BA%BF%E8%80%B3%E6%9C%BA
        println "referer:" + referer
        String search = ""
        if (!StringUtils.isNull(referer) && referer.indexOf("kw") > 0) {
            String regex = "[?&]kw=([^&#]+)"
            Pattern pt = Pattern.compile(regex)
            Matcher mt = pt.matcher(referer)
            while (mt.find()) {
                String param = mt.group()
                search = param.replaceAll("\\?kw=", "")
            }
        }
        if (!StringUtils.isNull(search)) {
            search = URLDecoder.decode(search, "UTF-8")
        }
        return search
    }


}
