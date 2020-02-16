package com.waps.robot_api.service

import com.waps.elastic.search.utils.PageUtils
import com.waps.service.jd.es.domain.TSRoomInfoESMap
import com.waps.service.jd.es.service.TSRobotRoomInfoESService
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TSRobotRoomService {

    @Autowired
    TSRobotRoomInfoESService tsRobotRoomInfoESService

    /**
     * 读取出目前有机器人的群信息
     * @param page
     * @param size
     * @return
     */
    public RobotRoomListBean listRobotRoom(int page, int size) {
        HashMap params = new PageUtils(page, size).getParamsMap()
        params.put("room_status", "0")
        println params
        SearchHits hits = tsRobotRoomInfoESService.findByFreeMarkerFromResource("es_script/ts_robot_room_list_status.json", params)
        RobotRoomListBean listReturn = new RobotRoomListBean()
        listReturn.setTotal(hits.getTotalHits().value)
        List<TSRoomInfoESMap> _list = new ArrayList<>()
        for (SearchHit hit : hits) {
            TSRoomInfoESMap tsRoomInfoESMap = tsRobotRoomInfoESService.getObjectFromJson(hit.getSourceAsString(), TSRoomInfoESMap.class) as TSRoomInfoESMap
            _list.add(tsRoomInfoESMap)
        }
        listReturn.setList(_list)
        return listReturn
    }
}

class RobotRoomListBean {
    long total
    List<TSRoomInfoESMap> list = new ArrayList<>()
}
