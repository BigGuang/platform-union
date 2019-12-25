package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONObject
import com.waps.tools.security.MD5
import com.waps.union_jd_api.utils.Config
import com.waps.union_jd_api.utils.HttpUtils
import org.springframework.stereotype.Component

@Component
class JtbApiService {

    /**
     * 京推宝 登录第一步接口
     * @return
     */
    public String loginByAccount() {
        Map<String, String> params = new HashMap<>()
        params.put("phone", Config.JTB_LOGIN_ACCOUNT_NAME)
        params.put("password", Config.JTB_LOGIN_ACCOUNT_PWD)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_LOGIN_ACCOUNT_URL, params, null)
        println jsonStr
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        String sessionId = jsonObject.getJSONObject("data").get("sessionId")
        println sessionId
        return sessionId
    }

    /**
     * 京推宝 登录第二步接口
     * @param sessionId
     * @return
     */
    public String loginBySessionID(String sessionId) {
        Map<String, String> params = new HashMap<>()
        params.put("sessionId", sessionId)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_LOGIN_SESSION_ID_URL, params, null)
        println jsonStr
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        String new_sessionId = jsonObject.getJSONObject("data").get("sessionId")
        println new_sessionId
        return new_sessionId
    }

    /**
     * 图片上传接口
     * @param sessionId
     * @param imgUrl
     * @return
     */
    public JSONObject uploadImage(String sessionId, String imgUrl) {
        Map<String, String> header = new HashMap<>()
        header.put("Content-Type", "multipart/form-data")
        header.put("Host", "jingtuibao.ixiaocong.net")
        header.put("Origin", "https://jingtuibao.ixiaocong.net")
        header.put("Cookie", "Admin-Token=" + sessionId)
        Map<String, Object> params = new HashMap<>()
        params.put("sessionId", sessionId)
        if (imgUrl && imgUrl.startsWith('http')) {
            try {
                String fileName = new MD5().getMD5(imgUrl) + ".jpg"
                String path = "/Users/xguang/temp06/" + fileName
                byte[] imgByte= HttpUtils.getImageFromNetByUrl(imgUrl)
                params.put("file", imgByte)

//                URL url = new URL(imgUrl)
//                DataInputStream dataInputStream = new DataInputStream(url.openStream())
//                FileOutputStream fileOutputStream = new FileOutputStream(new File(path))
//                ByteArrayOutputStream output = new ByteArrayOutputStream()
//                byte[] buffer = new byte[1024];
//                int length;
//                while ((length = dataInputStream.read(buffer)) > 0) {
//                    output.write(buffer, 0, length);
//                }
//                fileOutputStream.write(output.toByteArray());
//                dataInputStream.close();
//                fileOutputStream.close();
//                params.put("file", new File(path))

                String jsonStr = HttpUtils_bak.postFormParams(Config.JTB_UPLOAD_IMG_URL, params, header)
                JSONObject jsonObject = JSONObject.parseObject(jsonStr)
                return jsonObject
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 添加发送任务接口
     * @param sessionId
     * @param list
     * @return
     */
    public JSONObject addSendList(String sessionId, String text, String images, String videos, String planTime) {
        Map<String, String> header = new HashMap<>()
        header.put("Content-Type", "application/x-www-form-urlencoded")
        header.put("Host", "jingtuibao.ixiaocong.net")
        header.put("Origin", "https://jingtuibao.ixiaocong.net")
        header.put("Cookie", "Admin-Token=" + sessionId)
        Map<String, String> params = new HashMap<>()
        params.put("sessionId", sessionId)
        params.put("text", text)
        params.put("images", images)
        params.put("videos", videos)
        params.put("planTime", planTime)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_ADD_SEND_URL, params, header)
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        return jsonObject
    }

    /**
     * 待发送队列
     * @param sessionId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public JSONObject findSendWaiting(String sessionId, Integer pageIndex, Integer pageSize) {
        Map<String, String> header = new HashMap<>()
        header.put("Content-Type", "application/x-www-form-urlencoded")
        header.put("Host", "jingtuibao.ixiaocong.net")
        header.put("Origin", "https://jingtuibao.ixiaocong.net")
        header.put("Cookie", "Admin-Token=" + sessionId)
        Map<String, Object> params = new HashMap<>()
        params.put("sessionId", sessionId)
        params.put("pageIndex", pageIndex)
        params.put("pageSize", pageSize)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_SEND_WAITING_URL, params, header)
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        return jsonObject
    }


    /**
     * 已发送队列
     * @param sessionId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public JSONObject findSended(String sessionId, Integer pageIndex, Integer pageSize) {
        Map<String, String> header = new HashMap<>()
        header.put("Content-Type", "application/x-www-form-urlencoded")
        header.put("Host", "jingtuibao.ixiaocong.net")
        header.put("Origin", "https://jingtuibao.ixiaocong.net")
        header.put("Cookie", "Admin-Token=" + sessionId)
        Map<String, Object> params = new HashMap<>()
        params.put("sessionId", sessionId)
        params.put("pageIndex", pageIndex)
        params.put("pageSize", pageSize)
        String jsonStr = HttpUtils.postFormParams(Config.JTB_SEND_ED_URL, params, header)
        JSONObject jsonObject = JSONObject.parseObject(jsonStr)
        return jsonObject
    }


    public void syncSendDoneJob(){

    }
}
