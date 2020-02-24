package com.waps.union_jd_api.controller

import com.waps.tools.security.MD5
import com.waps.tools.test.TestUtils
import com.waps.union_jd_api.bean.ReturnMessageBean
import com.waps.union_jd_api.utils.FFMpegUtils
import com.waps.union_jd_api.utils.FileMD5
import com.waps.union_jd_api.utils.ImageUtils
import com.waps.union_jd_api.utils.VideoInfo
import com.waps.utils.ConfigUtils
import com.waps.utils.ResponseUtils
import com.waps.utils.StringUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.SimpleDateFormat

@Controller
@RequestMapping("/image")
class ImageController {

    @RequestMapping(value = "/upload")
    public void upLoad(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        println "==upload=="
        try {
            MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = req.getFile("file");
            println "===multipartFile.getContentType():"+multipartFile.getContentType()
            String type = ".jpg"
            if (multipartFile.getContentType() == "image/jpeg") {
                type = ".jpg"
            }
            if (multipartFile.getContentType() == "image/png") {
                type = ".png"
            }
            if (multipartFile.getContentType() == "image/gif") {
                type = ".gif"
            }
            if (multipartFile.getContentType() == "video/mp4") {
                type = ".mp4"
            }
            SimpleDateFormat format0 = new SimpleDateFormat("yyyyMMdd")
            String dateDir = format0.format(new Date())
            String uriDir = "/images/" + dateDir + "/"

            String fileName = FileMD5.getInputStreamMD5(multipartFile.getInputStream()).toLowerCase() + type

            String uri = uriDir + fileName

            String saveRealDir = StringUtils.getRealPath(uriDir)
            new File(saveRealDir).mkdirs()
            String saveRealPath = saveRealDir + fileName
            String url = "http://api.wapg.cn/jd_union" + uri
            println saveRealPath
            println url
            multipartFile.transferTo(new File(saveRealPath))
            if(saveRealPath.endsWith(".mp4")){
                String ret=FFMpegUtils.getAutoScreenShot(saveRealPath,saveRealDir)
                println ret
                VideoInfo videoInfo=FFMpegUtils.getVideoINFO(saveRealPath)
                TestUtils.outPrint(videoInfo)
            }

            ResponseUtils.write(response, new ReturnMessageBean(200, "", url))
        } catch (Exception e) {
            ResponseUtils.write(response, new ReturnMessageBean(500, "文件上传出现错误"))
        }
    }

    @RequestMapping(value = "/puzzle", method = RequestMethod.POST)
    public void puzzle(
            @RequestBody ImagesBean imagesBean,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String IMG_URI = "/jd_union/send_images/"
        if (imagesBean.getImg_list().length > 0) {
            String path = ImageUtils.mergeImage(imagesBean.getImg_list(), 1, new ConfigUtils().getHtmlDirPath() + IMG_URI + System.currentTimeMillis() + ".jpg")
            if (!StringUtils.isNull(path)) {
                println "path:" + path
                path = path.replaceAll(new ConfigUtils().getHtmlDirPath(), new ConfigUtils().getApiHost())
                ResponseUtils.write(response, new ReturnMessageBean(200, "", path))
            }
        } else {
            ResponseUtils.write(response, new ReturnMessageBean(500, "至少要有一张图片"))
        }

    }


}

class ImagesBean {
    String[] img_list
}
