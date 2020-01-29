package com.waps.union_jd_api.service

import com.waps.service.jd.api.bean.PromotionCodeParams
import com.waps.service.jd.api.service.JdUnionService
import com.waps.tools.test.TestUtils
import com.waps.union_jd_api.utils.JDConfig
import com.waps.utils.StringUtils
import jd.union.open.promotion.bysubunionid.get.response.UnionOpenPromotionBysubunionidGetResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Component
class ConvertLinkService {
    @Autowired
    private JdUnionService jdUnionService

    public Map<String, LinkBean> threadConvertLink(List<LinkBean> linkBeanList, long pid) {
        println "开始异步转链:" + linkBeanList.size()
        Map<String, LinkBean> convertedMap = new HashMap<>()
        try {
            // 开始的倒数锁
            final CountDownLatch begin = new CountDownLatch(1)
            // 结束的倒数锁
            final CountDownLatch end = new CountDownLatch(linkBeanList.size())
            // 十名选手
            final ExecutorService exec = Executors.newFixedThreadPool(linkBeanList.size())

            for (int i = 0; i < linkBeanList.size(); i++) {
                final int NO = i
                Runnable run = new Runnable() {
                    public void run() {
                        try {
                            begin.await()
                            LinkBean linkBean = linkBeanList.get(NO)
                            String mUrl = linkBean.getMateriaUrl()
                            if (StringUtils.isNull(mUrl)) {
                                mUrl = linkBean.getOldUrl()
                            }
                            String couponUrl = linkBean.getCouponUrl()

                            PromotionCodeParams params = new PromotionCodeParams()

                            params.setApp_key(JDConfig.APP_KEY)
                            params.setApp_secret(JDConfig.SECRET_KEY)
                            params.setMaterialId(mUrl)
                            params.setCouponUrl(couponUrl)
                            params.setPositionId(pid)
                            params.setChainType(2)
                            println "===链接参数==="
                            TestUtils.outPrint(linkBean)
                            println "===转链参数==="
                            TestUtils.outPrint(params)
                            UnionOpenPromotionBysubunionidGetResponse response = jdUnionService.getGoodsUnionLinkResponse(params)
                            println "===转链结果==="
                            TestUtils.outPrint(response)
                            String newUrl = ""
                            if (response.getCode() == 200) {
                                newUrl = response.getData().getShortURL()
                            } else {
                                newUrl = linkBean.getOldUrl()
                            }
                            linkBean.setNewUrl(newUrl)
                            println "oldUrl:" + linkBean.getOldUrl() + "  newUrl:" + newUrl
                            convertedMap.put(linkBean.getOldUrl(), linkBean);
                        } catch (Exception e) {
                            System.out.println("threadConvertLink:" + e.getLocalizedMessage());
                        } finally {
                            end.countDown();
                        }
                    }
                }
                exec.submit(run);
            }
            begin.countDown()
            end.await()
            exec.shutdown()

        } catch (Exception e) {
            System.out.println("threadConvertLink Error:" + e.getLocalizedMessage())
        }
        return convertedMap
    }
}
