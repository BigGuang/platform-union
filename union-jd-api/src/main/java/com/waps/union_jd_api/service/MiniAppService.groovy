package com.waps.union_jd_api.service

import com.waps.tools.security.MD5
import com.waps.union_jd_api.utils.Config

import com.waps.utils.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MiniAppService {

    @Autowired
    private MiniAppApiService miniAppApiService


    public static final APP_ID = "wx51546016a2d42464"
    public static final APP_SECRET = "362c795d83649bffe9981788330a53c1"
    public static final DEFAULT_SCENE = "scene=S006"

    /**
     * 获取小程序带参二维码
     * @param qrParam
     * @return
     */
    public String getMiniQR(QRParam qrParam) {
        if (StringUtils.isNull(qrParam.getScene())) {
            qrParam.setScene(DEFAULT_SCENE)
        }
        if (StringUtils.isNull(qrParam.getApp_id())) {
            qrParam.setApp_id(APP_ID)
        }
        if (qrParam.getWidth() < 10) {
            qrParam.setWidth(300)
        }
        String qr_param=qrParam.getApp_id() + qrParam.getPage() + qrParam.getScene() + qrParam.getWidth()
        println "qr_param:"+qr_param
        String qr_name = new MD5().getMD5(qr_param)
        println "qr_name:"+qr_name
        String uri = "/images/qr/" + qr_name + ".jpg"
//        String path = "/Users/xguang/temp04/" + uri
        String path = StringUtils.getRealPath(uri)

        if (new File(path).exists()) {
            String imgUrl = Config.HOST + uri
            imgUrl = imgUrl.replace("//images", "/images")
            println "QR 存在:"+imgUrl
            return imgUrl
        } else {
            String token = miniAppApiService.getToken(qrParam.getApp_id())
            if (!StringUtils.isNull(token)) {
                miniAppApiService.getMiniQRFromToken(qrParam, token, path)
                String imgUrl = Config.HOST + uri
                imgUrl = imgUrl.replace("//images", "/images")
                println "QR 生成:"+imgUrl
                return imgUrl
            }
        }
    }

    public String sendUniformMessage(UniformMessageParams params) {
        final String TEMPLATE_ID = "-YQBojykAQ5fKrpkTSFIO7QqPdWYP0RH6RR8niS6kkY"  //对应小程序中的消息模版ID

        if (!StringUtils.isNull(params.getAccount_app_id())) {
            UniformMessageBean uniformMessageBean = new UniformMessageBean()
            uniformMessageBean.setTouser(params.getTouser())

            //判断公众号，加公众号消息模版
            if(!StringUtils.isNull(params.getOffice_id())) {
                MPTemplateMsg mpTemplateMsg = new MPTemplateMsg()
                mpTemplateMsg.setTemplate_id(TEMPLATE_ID)
                if (!StringUtils.isNull(params.getUrl())) {
                    mpTemplateMsg.setUrl(params.getUrl())
                }
                mpTemplateMsg.setAppid(params.getOffice_id())
                if (!StringUtils.isNull(params.getMini_page())) {
                    mpTemplateMsg.setMiniprogram(new MiniProgram(params.getApp_id(), params.getMini_page()))
                    uniformMessageBean.setMp_template_msg(mpTemplateMsg)
                }

                Map<String, MessageParamsBean> data = new HashMap<>()
                data.put("first", new MessageParamsBean(params.getTitle(), null))
                data.put("keyword1", new MessageParamsBean(params.getBill_account(), null))
                data.put("keyword2", new MessageParamsBean(params.getBill(), null))
                data.put("keyword3", new MessageParamsBean(params.getBill_date(), null))
                data.put("keyword4", new MessageParamsBean(params.getEnd_date(), null))
                data.put("remark", new MessageParamsBean(params.getDesc(), null))
                mpTemplateMsg.setData(data)
                uniformMessageBean.setMp_template_msg(mpTemplateMsg)
            }
            return miniAppApiService.sendUniformMessage(params.getAccount_app_id(), uniformMessageBean)
        }
    }

}


class QRParam {
    String app_id
    String scene
    int width
    String page
    boolean auto_color
}

class UniformMessageParams {
    String touser
    String account_app_id
    String app_id     //小程序ID
    String office_id  //公众号ID
    String url
    String mini_page
    String title
    String bill_account
    String bill
    String bill_date
    String end_date
    String desc
}
