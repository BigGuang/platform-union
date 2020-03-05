package com.waps.robot_api.service

import com.waps.utils.StringUtils
import org.springframework.stereotype.Component

import java.text.SimpleDateFormat

@Component
class TimeLineService {

    /**
     * 计算出时间线，也可以读取配置文件固定
     * @return
     */
    public List<Date> getTimeLine() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")

        List<Date> timeLine = new ArrayList<>()
        String startTime = "8:03"
        long loopTime_5min = 6 * 1000 * 60 * 3  //循环时间，18分钟
        Date currentTime = new Date()
        String nowDay = dayFormat.format(currentTime)
        Date fullDate = dateFormat.parse(nowDay + " " + startTime)
        int loopNum = 50
        for (int i = 0; i < loopNum; i++) {
            Date nextTime = new Date(fullDate.getTime() + (loopTime_5min) * i)
            timeLine.add(nextTime)
        }
        return timeLine
    }


    /**
     * 获取下次发送时间
     * @return
     */
    public Date getSendTaskNextDateTime() {
        return getSendTaskNextDateTime(null)
    }

    public Date getSendTaskNextDateTime(String anyTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
        List<Date> timeLine = getTimeLine()
        Date currentTime = new Date()
        if (!StringUtils.isNull(anyTime)) {
            currentTime = dateFormat.parse(anyTime)
        }

        for (Date dateTime : timeLine) {
            if ((dateTime.getTime() - currentTime.getTime()) > (1000 * 60)) {
                return dateTime
            }
        }
        return null
    }

    /**
     * 获取可用时间点
     * @return
     */
    public int getUsableTimeNumber() {
        getUsableTimeNumber(null)
    }

    /**
     * 获取可用时间点
     * @param anyTime
     * @return
     */
    public int getUsableTimeNumber(String anyTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm")
        Date currentTime = new Date()
        if (!StringUtils.isNull(anyTime)) {
            currentTime = dateFormat.parse(anyTime)
        }

        List<Date> timeLine = getTimeLine()
        int retNum = timeLine.size()
        int count = 0
        for (int i = 0; i < timeLine.size(); i++) {
            Date dateTime = timeLine.get(i)
            if ((dateTime.getTime() - currentTime.getTime()) > (1000 * 60)) {
                count = i
                break
            }
        }
        println "===getUsableTimeNumber:" + dateFormat.format(currentTime)
        println retNum + "  " + count
        retNum = retNum - count
        return retNum
    }

}
