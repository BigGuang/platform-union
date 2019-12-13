package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.api.bean.PromotionCodeParams
import com.waps.service.jd.api.bean.SearchParams
import com.waps.service.jd.api.service.JdUnionService
import com.waps.service.jd.es.domain.JDMediaInfoESMap
import com.waps.tools.security.MD5
import com.waps.tools.test.TestUtils
import com.waps.union_jd_api.utils.JDConfig
import com.waps.utils.Convert
import com.waps.utils.StringUtils
import jd.union.open.goods.query.response.Coupon
import jd.union.open.goods.query.response.GoodsResp
import jd.union.open.goods.query.response.UrlInfo
import org.jsoup.helper.StringUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.DecimalFormat
import java.util.regex.Matcher
import java.util.regex.Pattern


@Component
class JDConvertLinkService {

    @Autowired
    JdUnionService jdUnionService

    @Autowired
    JDMediaService jdMediaService

    @Autowired
    JDSkuRobotService jdSkuRobotService

    @Autowired
    ConvertLinkService convertLinkService

    /**
     * 转链入口
     * @param content
     * @return
     */
    public ResultBean convertLink(String content, String channelName) {
        long pid = getPositionId(channelName)
        int code = 0
        ArrayList<String> imgList = new ArrayList<>()
        String message = ""
        String jd_host = ".jd."
        double commission = 0L
        Map<String, LinkBean> linkMap = new HashMap<>()
        if (pid > 0) {
            List<String> urlList = getUrlList(content, jd_host)  //已有链接列表
            Map<String, String> infoMap = getInfoList(content)   //券地址+skuId 列表

            if (urlList != null && urlList.size() > 0) {
                Map<String, LinkBean> _map = convertLinkList(urlList, pid)
                if (_map.size() > 0) {
                    linkMap.putAll(_map)
                }
            }

            if (infoMap != null && infoMap.size() > 0) {
                if (infoMap.size() > 0) {
                    //批量反向找到商品信息
                    Map<String, LinkBean> _map = getInfoUnionLink(infoMap)
                    println "getInfoUnionLink:" + _map.size()
                    if (_map.size() > 0) {
                        linkMap.putAll(_map)
                    }
                }
            }
            if (linkMap.size() < 1) {
                code = 503
                message = "没有发现需要转换的链接"
            }

            if (linkMap.size() > 30) {
                code = 504
                message = "需要转链的数量请控制在30个以内"
            }

            if (code <= 200) {

                List<Long> _skuID_List = new ArrayList<>()
                List<String> _id_List = new ArrayList<>()

                Iterator _link = linkMap.keySet().iterator()
//                while (_link.hasNext()) {
//                    String id = _link.next()
//                    LinkBean linkBean = linkMap.get(id)
//                    //有skuID的
//                    if (linkBean.getSkuId() != null && linkBean.getSkuId() > 0) {
//                        println "添加:" + linkBean.getSkuId()
//                        _skuID_List.add(linkBean.getSkuId())
//                    } else {//没有skuid的
//                        _id_List.add(id)
//                    }
//                }


                //有skuID的批量获取商品信息和券信息
//                if (_skuID_List.size() > 0) {
//                    Long[] skuIDs = _skuID_List.toArray(Long[]) as Long[]
//                    println "skuIDs:" + skuIDs
//                    JSONArray jsonArray = getGoodsSearchBySkuIDs(skuIDs)
//                    GoodsResp[] goodsList = jsonArray.toArray(GoodsResp[]) as GoodsResp[]
//                    if (goodsList.size() > 0) {
//                        for (GoodsResp goodsResp in goodsList) {
//                            String materiaUrl = goodsResp.getMaterialUrl()
//                            long _skuId = goodsResp.getSkuId()
//                            Coupon[] couponList = goodsResp.getCouponInfo().getCouponList()
//                            String couponUrl = ""
//                            for (Coupon coupon in couponList) {
//                                if (coupon.getIsBest() == 1) {
//                                    couponUrl = coupon.getLink()
//                                }
//                            }
//
//                            LinkBean linkBean = linkMap.get(_skuId + "")
//                            String oldUrl = linkBean.getOldUrl()
//                            //关键：如果没有券，拿老链接直接转
//                            if (!StringUtils.isNull(oldUrl) && StringUtils.isNull(couponUrl)) {
//                                materiaUrl = oldUrl
//                            }
//                            linkBean.setCouponUrl(couponUrl)
//                            String newUrl = getNewLink(materiaUrl, couponUrl, pid)
//                            linkBean.setNewUrl(newUrl)
//                            UrlInfo[] urlInfos = goodsResp.getImageInfo().getImageList()
//                            if (urlInfos != null && urlInfos.size() > 0) {
//                                String _imgUrl = urlInfos[0].url
//                                linkBean.setImgUrl(_imgUrl)
//                            }
//
//                            linkMap.put(_skuId + "", linkBean)
//                        }
//                    }
//                }

                //对活动url做转链
                if (_id_List.size() > 0) {
                    println "活动url:" + _id_List
                    for (String id : _id_List) {
                        LinkBean linkBean = linkMap.get(id)
                        if (linkBean) {
                            String materiaUrl = linkBean.getMateriaUrl()
                            materiaUrl = removeParams(materiaUrl)
                            String newUrl = getNewLink(materiaUrl, null, pid)
                            println "materiaUrl:" + materiaUrl
                            println "newUrl:" + newUrl
                            if (!StringUtils.isNull(newUrl)) {
                                linkBean.setNewUrl(newUrl)
                                linkMap.put(id, linkBean)
                            }
                        }
                    }
                }


                //将老链接和内容替换成新的
                Iterator it = linkMap.keySet().iterator()
                while (it.hasNext()) {
                    String id = it.next()
                    LinkBean linkBean = linkMap.get(id)
                    if (linkBean.oldUrl) {
                        String newStr = linkBean.newUrl

                        boolean isSuccess = true
                        if (StringUtils.isNull(newStr)) {
                            isSuccess = false
                        }
                        if (linkBean.oldUrl == newStr) {
                            isSuccess = false
                        }
                        if (!StringUtils.isNull(newStr) && !newStr.toLowerCase().startsWith("http")) {
                            isSuccess = false
                        }
                        if (!isSuccess) {
                            newStr = "转链失败"
                        } else {
                            if (!StringUtils.isNull(linkBean.getImgUrl())) {
                                imgList.add(linkBean.getImgUrl())
                            }
                        }
                        if (!StringUtils.isNull(linkBean.getNewUrlPrefix())) {
                            newStr = linkBean.getNewUrlPrefix() + newStr
                        }
                        println "老链接:" + linkBean.oldUrl
                        println "新链接:" + newStr
                        linkBean.setContent(buildContent(linkBean))
                        //有文案的情况下不替换整体内容
                        if (linkMap.size() == 1 && !StringUtils.isNull(linkBean.getContent()) && content.indexOf("http") < 10) {
                            content = linkBean.getContent()
                        } else {
                            String _content = URLEncoder.encode(content, "UTF-8")
                            _content = _content.replaceAll(URLEncoder.encode(linkBean.oldUrl, "UTF-8"), URLEncoder.encode(newStr, "UTF-8"))
                            content = URLDecoder.decode(_content, "UTF-8")
                            println content
                        }
                        if (linkBean.getCommission() > 0) {
                            commission = linkBean.getCommission()
                        }
                    }

                }
                code = 200
            }

        } else {
            code = 500
            message = "pid信息不存在"
        }
        ResultBean resultBean = new ResultBean()
        resultBean.setCode(code)
        resultBean.setMessage(message)
        resultBean.setContent(content)
        resultBean.setCommission(commission)
        resultBean.setPositionId(pid.toString())
        if (imgList != null && imgList.size() > 0) {
            resultBean.setImgList(imgList)
        }
        return resultBean
    }


    public Map<String, LinkBean> convertLinkList(List<String> oldUrlList, long pid) {
        if (oldUrlList.size() > 0) {
            List<LinkBean> toConvertList = new ArrayList<>()
            if (oldUrlList.size() == 1) {
                String oldUrl = oldUrlList[0]
                String skuId = getSukIdFromUrl(oldUrl)
                println "skuId:" + skuId
                LinkBean linkBean = new LinkBean();
                if (!StringUtils.isNull(skuId)) {
                    //todo:
                    LinkBean _linkBean = getGoodsSearchBySkuID(skuId)
                    if (_linkBean != null) {
                        linkBean = _linkBean
                    }
                }
                linkBean.setOldUrl(oldUrl)
                toConvertList.add(linkBean)
            } else {
                for (int i; i < oldUrlList.size(); i++) {
                    String oldUrl = oldUrlList.get(i)
                    LinkBean linkBean = new LinkBean()
                    linkBean.setOldUrl(oldUrl)
                    toConvertList.add(linkBean)
                }
            }
            Map<String, LinkBean> linkMap = convertLinkService.threadConvertLink(toConvertList, pid)
            return linkMap
        }
    }

    public Map<String, LinkBean> getInfoUnionLink(Map<String, String> infoMap) {
        Map<String, LinkBean> linkMap = new HashMap<>()
        Iterator _infoIt = infoMap.keySet().iterator()
        while (_infoIt.hasNext()) {
            String _info = _infoIt.next()
            String _str = infoMap.get(_info)
            if (!StringUtils.isNull(_str)) {
                String _str_skuId = _str.split(";")[0]
                String _couponUrl = _str.split(";")[1]
                _couponUrl = getCouponUrl(_couponUrl)
                if (!StringUtils.isNull(_couponUrl) && !StringUtils.isNull(_str_skuId)) {
                    try {
                        LinkBean linkBean = new LinkBean()
                        linkBean.setId(_str_skuId)
                        linkBean.setOldUrl(_info)
                        long _skuId = Long.parseLong(_str_skuId)
                        linkBean.setNewUrlPrefix("抢购：")
                        linkBean.setSkuId(_skuId)
                        linkMap.put(_str_skuId, linkBean)
                    } catch (Exception e) {
                        println "getInfoUnionLink ERROR:" + e.getLocalizedMessage()
                    }
                }
            }
        }
        return linkMap
    }
    /**
     * 解析出内容中的推广链接
     * @param content
     * @return
     */
    public List<String> getUrlList(String content, String mustHost) {
        List<String> urlList = new ArrayList<>()

        String regex = "((ht|f)tps?):\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:\\/~\\+#]*[\\w\\-\\@?^=%&\\/~\\+#])?"
        Pattern pt = Pattern.compile(regex)
        Matcher mt = pt.matcher(content)
        while (mt.find()) {
            String url = mt.group()
            if (!StringUtils.isNull(mustHost)) {
                if (url.indexOf(mustHost) > -1) {
                    url = url.replaceAll("\n", "")
                    urlList.add(url)
                }
            } else {
                urlList.add(url)
            }
        }
        return urlList
    }

    /**
     * 获取链接+skuId方式的info
     * @param content
     * @return
     */
    public Map<String, String> getInfoList(String content) {
        Map<String, String> infoMap = new HashMap<>()
//        String regex = ".*：(.*)\n.*：(.*)"
        String regex = ".*((ht|f)tps?):\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:\\/~\\+#]*[\\w\\-\\@?^=%&\\/~\\+#])?\n.*([0-9]{6,}).*"
        Pattern pt = Pattern.compile(regex)
        Matcher mt = pt.matcher(content)
        while (mt.find()) {
            String info = mt.group()
            if (info) {
                List<String> _urlList = getUrlList(info, null)
                String _skuId = getSukIdFromInfo(info)
                if (_urlList.size() > 0 && !StringUtils.isNull(_skuId)) {
                    String _str = _skuId + ";" + _urlList.get(0)
                    infoMap.put(info, _str)
                }
            }
        }
        return infoMap
    }

    /**
     * 获取getInfoList中的skuId
     * @param info
     * @return
     */
    public String getSukIdFromInfo(String info) {
        String regex = "([0-9]{6,})"  //至少6位的纯数字
        Pattern pt = Pattern.compile(regex)
        Matcher mt = pt.matcher(info)
        while (mt.find()) {
            String sukId = mt.group()
            return sukId
        }
    }

    /**
     * 通过URL获取skuId, u.jd.com 开头会反向获取url，其他则从url中直接获取
     * @param url
     * @return
     */
    public String getSukIdFromUrl(String url) {
        String host = "/u.jd.com"
        println "url:" + url
        if (url) {
            if (url.indexOf(host) > -1) {
                String skuUrl = SeleniumService.serverGetCurrentUrl(url)
                if (!StringUtils.isNull(skuUrl)) {
                    url = skuUrl
                }
            }
            String sukId = ""
            String regex = "/([0-9]*).html"  //至少6位的纯数字
            Pattern pt = Pattern.compile(regex)
            Matcher mt = pt.matcher(url)
            while (mt.find()) {
                sukId = mt.group()
                sukId = sukId.replaceAll("/", "").replaceAll(".html", "")

            }
            if (StringUtils.isNull(sukId)) {
                if (!StringUtils.isNull(url)) {
                    if (url.indexOf("sku=") > 0) {
                        String _temp = url.substring(url.indexOf("sku=") + 4, url.length());
                        if (_temp.indexOf("&") > 0) {
                            sukId = _temp.substring(0, _temp.indexOf("&"));
                        } else {
                            sukId = _temp;
                        }
                    }
                }
            }
            return sukId
        }
    }


    /**
     * 抓取方式获取券链接
     * @param url
     * @return
     */
    public String getCouponUrl(String url) {
        String host = "/coupon.m.jd.com/"
        if (!StringUtils.isNull(url)) {
            if (url.indexOf(host) > -1) {
                return url;
            } else {
                try {
                    URL serverUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) serverUrl
                            .openConnection();
                    conn.setRequestMethod("GET");
                    // 必须设置false，否则会自动redirect到Location的地址
                    conn.setInstanceFollowRedirects(false);

                    conn.addRequestProperty("Accept-Charset", "UTF-8;");
                    conn.addRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
                    conn.addRequestProperty("Referer", "http://zuidaima.com/");
                    conn.connect();
                    String location = conn.getHeaderField("Location");

//            serverUrl = new URL(location);
//            conn = (HttpURLConnection) serverUrl.openConnection();
//            conn.setRequestMethod("GET");
//
//            conn.addRequestProperty("Accept-Charset", "UTF-8;");
//            conn.addRequestProperty("User-Agent",
//                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
//            conn.addRequestProperty("Referer", "http://zuidaima.com/");
//            conn.connect();
                    return location

                } catch (Exception e) {
                    println "getCouponUrl Error:" + e.getLocalizedMessage()
                }
            }
        }
    }


    /**
     * 获取对应的pid
     * @param channelName
     * @return
     */
    public Long getPositionId(String channelName) {
        long pid = 0L
        HashMap paramsMap = new HashMap()
        paramsMap.put("from", 0)
        paramsMap.put("size", 1)
        paramsMap.put("channel_name", channelName)
        List<JDMediaInfoESMap> mediaList = jdMediaService.findMediaChannel(paramsMap)
        if (mediaList.size() > 0) {
            JDMediaInfoESMap jdMediaInfoESMaps = mediaList.get(0)
            String pidString = jdMediaInfoESMaps.getChannel_id()
            if (!StringUtils.isNull(pidString)) {
                try {
                    pid = Long.parseLong(pidString)
                } catch (Exception e) {

                }
            }
        }
        return pid
    }


    /**
     * 生成新的推广链接
     * @param skuId
     * @param pid
     * @return
     */
    public String getNewLink(String materialUrl, String couponUrl, Long positionId) {
        PromotionCodeParams params = new PromotionCodeParams()
        params.setApp_key(JDConfig.APP_KEY)
        params.setApp_secret(JDConfig.SECRET_KEY)
        params.setMaterialId(materialUrl)
        params.setPositionId(positionId)
        params.setChainType(2)
        if (!StringUtils.isNull(couponUrl)) {
            params.setCouponUrl(couponUrl)
        }
        String shortUrl = jdSkuRobotService.getUnionLink(params)
        return shortUrl
    }


    /**
     * 查找优惠券
     * @param skuId
     * @return
     */
    public LinkBean getGoodsSearchBySkuID(String skuID) {
        LinkBean linkBean = new LinkBean()
        try {
            long _skuID = Long.parseLong(skuID)
            linkBean.setSkuId(_skuID)
            SearchParams searchParams = new SearchParams()
            searchParams.setApp_key(JDConfig.APP_KEY)
            searchParams.setApp_secret(JDConfig.SECRET_KEY)
            searchParams.setPageIndex(1)
            searchParams.setPageSize(1)
            searchParams.setSkuIds(_skuID)
            JSONArray array = jdSkuRobotService.getGoodsListBySearch(searchParams)
            JSONObject jsonObject = array.get(0)
            if (jsonObject != null) {
                GoodsResp goods = JSONObject.toJavaObject(jsonObject, GoodsResp.class) as GoodsResp
                if (goods != null) {
                    linkBean.setSkuName(goods.skuName)
                    linkBean.setMateriaUrl(goods.materialUrl)
                    linkBean.setComments(goods.getGoodCommentsShare())
                    Coupon coupon = findSkuCoupinLink(goods)
                    String _imgUrl = findSkuImage(goods)
                    double _commission = findCommission(goods)
                    double _price = findPrice(goods)
                    double _couponPrice = 0L
                    if (coupon) {
                        _couponPrice = coupon.getDiscount()
                        linkBean.setCouponUrl(coupon.link)
                    }
                    double _buyPrice = _price - _couponPrice

                    linkBean.setImgUrl(_imgUrl)
                    linkBean.setCommission(_commission)
                    linkBean.setPrice(_price)
                    linkBean.setCouponPrice(_couponPrice)
                    linkBean.setBuyPrice(_buyPrice)
                }

            }
            return linkBean
        } catch (Exception e) {
            e.printStackTrace()
        }
    }


    public String buildContent(LinkBean linkBean) {
        StringBuffer buffer = new StringBuffer()
        DecimalFormat df = new DecimalFormat("0.00");
        if (!StringUtils.isNull(linkBean.getSkuName())) {
//            return df.format(commission)
            buffer.append("【京东】" + linkBean.getSkuName() + "\n")
            buffer.append("----------\n")
            if (linkBean.getComments() > 0) {
                DecimalFormat df2 = new DecimalFormat("0");
                buffer.append("好评率: " + df2.format(linkBean.getComments()) + "%\n")
            }
            if (linkBean.getPrice() > 0) {
                buffer.append("京东价: ¥" + df.format(linkBean.getPrice()) + "\n")
            }
            if (linkBean.getCouponPrice() > 0) {
                buffer.append("优惠券: ¥" + df.format(linkBean.getCouponPrice()) + "\n")
                if (linkBean.getBuyPrice() > 0) {
                    buffer.append("内购价: ¥" + df.format(linkBean.getBuyPrice()) + "\n")
                }
            }

            buffer.append("抢购链接:" + linkBean.getNewUrl() + "\n")
            return buffer.toString()
        }

    }

    /**
     * 找到最佳券
     * @param goodsResp
     * @return
     */
    public Coupon findSkuCoupinLink(GoodsResp goodsResp) {
        if (goodsResp != null) {
            Coupon[] couponList = goodsResp.getCouponInfo().getCouponList()
            for (Coupon coupon in couponList) {
                if (coupon.getIsBest() == 1) {
                    return coupon
                }
            }
        }
    }

    public String findSkuImage(GoodsResp goodsResp) {
        if (goodsResp != null) {
            return goodsResp.getImageInfo().getImageList()[0].url
        }
    }

    public double findCommission(GoodsResp goodsResp) {
        if (goodsResp != null) {
            Coupon coupon = findSkuCoupinLink(goodsResp)
            double _couponPrice = 0L
            if (coupon != null) {
                _couponPrice = coupon.getDiscount()
            }

            double _price = goodsResp.getPriceInfo().getLowestPrice()      //最低价格
            double _commissionShare = goodsResp.getCommissionInfo().getCommissionShare()  //佣金比例
            double commission = (_price - _couponPrice) * (_commissionShare / 100) * 0.9 * 0.525
            return commission
        }
    }

    public double findPrice(GoodsResp goodsResp) {
        if (goodsResp != null) {
            double price = goodsResp.getPriceInfo().getLowestPrice()
//            DecimalFormat df = new DecimalFormat("0.00");
            return price
        }
    }

    /**
     * 查找优惠券
     * @param skuId
     * @return
     */
    public JSONArray getGoodsSearchBySkuIDs(Long[] skuIDs) {
        SearchParams searchParams = new SearchParams()
        searchParams.setApp_key(JDConfig.APP_KEY)
        searchParams.setApp_secret(JDConfig.SECRET_KEY)
        searchParams.setPageIndex(1)
        searchParams.setPageSize(30)
        searchParams.setSkuIds(skuIDs)
        searchParams.setIsCoupon(1)
        JSONArray array = jdSkuRobotService.getGoodsListBySearch(searchParams)
        return array
    }


    public String removeParams(String orgUrl) {
        if (!StringUtils.isNull(orgUrl)) {
            if (orgUrl.indexOf("?") > 0) {
                orgUrl = orgUrl.substring(0, orgUrl.indexOf("?"))
            }
        }
        return orgUrl
    }

}

class LinkBean {
    String id
    String oldUrl
    String materiaUrl
    Long skuId
    String skuName
    String couponUrl
    String newUrl
    String imgUrl
    String newUrlPrefix
    double comments
    double commission
    String content
    double price
    double couponPrice
    double buyPrice
}

class ResultBean {
    int code
    String content
    String message
    String positionId
    double commission
    ArrayList<String> imgList = new ArrayList<>()
}
