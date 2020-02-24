package com.waps.robot_api.service

import com.waps.elastic.search.utils.PageUtils
import com.waps.service.jd.es.domain.TSRoomConfigESMap
import com.waps.service.jd.es.service.TSRoomConfigESService
import com.waps.tools.security.MD5
import com.waps.utils.StringUtils
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TSRoomConfigService {

    @Autowired
    TSRoomConfigESService tsRoomConfigESService
    @Autowired
    TSRobotChatRoomService tsRobotChatRoomService

    /**
     * 读取出目前有机器人的群信息
     * @param page
     * @param size
     * @return
     */
    public RoomConfigListBean listRobotRoom(int page, int size) {
        HashMap params = new PageUtils(page, size).getParamsMap()
        params.put("room_status", "0")
        println params
        SearchHits hits = tsRoomConfigESService.findByFreeMarkerFromResource("es_script/ts_robot_room_list_status.json", params)
        RoomConfigListBean listReturn = new RoomConfigListBean()
        listReturn.setTotal(hits.getTotalHits().value)
        List<TSRoomConfigESMap> _list = new ArrayList<>()
        for (SearchHit hit : hits) {
            TSRoomConfigESMap tsRoomInfoESMap = tsRoomConfigESService.getObjectFromJson(hit.getSourceAsString(), TSRoomConfigESMap.class) as TSRoomConfigESMap
            _list.add(tsRoomInfoESMap)
        }
        listReturn.setList(_list)
        return listReturn
    }

    public void setRoomSendStatus(String robot_id,String room_id,String send_status){
        if(!StringUtils.isNull(robot_id) && !StringUtils.isNull(room_id)) {
            String id = new MD5().getMD5(robot_id + room_id)
            tsRoomConfigESService.update(id,"send_status",send_status)
        }
    }

    public void setRobotNickNameInRoom(String robot_id,String room_id,String inRoomNickName ){
        if(!StringUtils.isNull(robot_id) && !StringUtils.isNull(room_id)) {
            tsRobotChatRoomService.setRobotNickNameInRoom(robot_id,room_id,inRoomNickName)
            String id = new MD5().getMD5(robot_id + room_id)
            tsRoomConfigESService.update(id,"room_nick_name",inRoomNickName)
        }
    }
}

class RoomConfigListBean {
    long total
    List<TSRoomConfigESMap> list = new ArrayList<>()
}
