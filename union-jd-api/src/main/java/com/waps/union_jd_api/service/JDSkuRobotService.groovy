package com.waps.union_jd_api.service

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.waps.service.jd.api.bean.PromotionCodeParams
import com.waps.service.jd.api.bean.SearchParams
import com.waps.service.jd.api.service.JdUnionService
import com.waps.union_jd_api.utils.BadWordUtils
import com.waps.union_jd_api.utils.ImageUtils
import com.waps.union_jd_api.utils.JDConfig
import com.waps.utils.ConfigUtils
import com.waps.utils.StringUtils
import com.waps.utils.TemplateUtils
import jd.union.open.goods.query.response.Coupon
import jd.union.open.goods.query.response.GoodsResp
import jd.union.open.goods.query.response.UrlInfo
import jd.union.open.promotion.bysubunionid.get.response.PromotionCodeResp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JDSkuRobotService {
    String IMG_URI = "/jd_union/channel_images/"
    int startPage = 1
    int pageSize = 30
    double title_v = 0.5

    @Autowired
    private JdUnionService jdUnionService

    JDSkuRobotService() {
        System.out.println("==JDSkuRobotService==")
    }

    public String getSkuList(String pid, String searchWord, Integer findSize) {
        return getSkuList(pid, searchWord, findSize, false)
    }

    /**
     * 搜索京东商品，30个一次，然后在30个中找 自营+有券，不足的情况再依次加有券、自营
     * @param pid
     * @param searchWrod
     * @param page
     * @param size
     * @param findSize
     * @return
     */
    public String getSkuList(String pid, String searchWord, Integer findSize, boolean isFilter) {
        ArrayList imgList = new ArrayList()
        StringBuffer buffer = new StringBuffer()
        if (pid) {
            long positionId = Long.parseLong(pid)
            SearchParams searchParams = new SearchParams()
            searchParams.setApp_key(JDConfig.APP_KEY)
            searchParams.setApp_secret(JDConfig.SECRET_KEY)
            searchParams.setPageIndex(startPage)
            searchParams.setPageSize(pageSize)
            searchParams.setSortName("inOrderCount30Days")
            searchParams.setSort("desc")
            searchParams.setIsCoupon(1)
            if (!StringUtils.isNull(searchWord))
                searchParams.setKeyword(searchWord)
            println "searchWord:" + searchWord

            try {

                JSONArray jsonArray = new JSONArray()
                println "==查找有优惠券=="
                JSONArray jsonArray_1 = getGoodsListBySearch(searchParams)
                if (jsonArray_1 != null && jsonArray_1.size() > 0) {
                    jsonArray.addAll(jsonArray_1)
                }
                if (jsonArray == null || jsonArray.size() < 1) {
                    //todo:未找到的情况，待处理
                }
                if (jsonArray == null || (jsonArray != null && jsonArray.size() < 6)) {
                    println "==查找无优惠券=="
                    searchParams.setIsCoupon(0)
                    JSONArray jsonArray_add = getGoodsListBySearch(searchParams)
                    if (jsonArray_add != null && jsonArray_add.size() > 0) {
                        jsonArray.addAll(jsonArray_add)
                    }
                }
                ArrayList<GoodsResp> _skuList = new ArrayList<>()
                ArrayList<GoodsResp> _jd_coupon_list = new ArrayList<>()  //自营并有券
                ArrayList<GoodsResp> _coupon_list = new ArrayList<>()     //仅有券
                ArrayList<GoodsResp> _jd_no_coupon_list = new ArrayList<>()         //自营无券的
                ArrayList<GoodsResp> _no_coupon_list = new ArrayList<>()         //非自营无券的

                HashMap<String, String> _temp_title = new HashMap<>()

                if (jsonArray != null && jsonArray.size() > 0) {
                    println "搜索结果:" + jsonArray.size()
                    GoodsResp[] goodsList = jsonArray.toArray(GoodsResp[]) as GoodsResp[]
                    for (GoodsResp goodsResp in goodsList) {
                        String skuName = goodsResp.getSkuName()
                        String _shortName = getShortTitle(skuName)

                        boolean hasBad = false
                        if (isFilter)
                            hasBad = BadWordUtils.isContaintBadWord(skuName, BadWordUtils.minMatchTYpe)

                        if (!hasBad) {
                            if (StringUtils.isNull(_temp_title.get(_shortName))) {
                                String owner = goodsResp.getOwner()
                                Coupon coupon = autoFindCouponByBest(goodsResp.getCouponInfo().couponList)
                                //自营并有券的
                                if (coupon != null && owner != null && "g".equalsIgnoreCase(owner)) {
                                    _jd_coupon_list.add(goodsResp)
                                } else if (coupon != null) { //仅有券的
                                    _coupon_list.add(goodsResp)
                                } else if ("g".equalsIgnoreCase(owner)) {
                                    _jd_no_coupon_list.add(goodsResp)
                                } else { //无券的
                                    _no_coupon_list.add(goodsResp)
                                }
                                _temp_title.put(_shortName, _shortName)
                            }
                        }
                    }
                }
                _temp_title.clear()

                println "_jd_coupon_list.size():" + _jd_coupon_list.size()
                println "_coupon_list.size():" + _coupon_list.size()
                println "_jd_no_coupon_list.size():" + _jd_no_coupon_list.size()
                println "_no_coupon_list.size():" + _no_coupon_list.size()

                _skuList.addAll(_jd_coupon_list)
                _skuList.addAll(_coupon_list)
                _skuList.addAll(_jd_no_coupon_list)
                _skuList.addAll(_no_coupon_list)

                println "_skuList.size():" + _skuList.size()
                int _size = findSize
                if (_skuList.size() < _size) {
                    _size = _skuList.size()
                }
                for (int i = 0; i < _size; i++) {
                    GoodsResp goodsResp = _skuList.get(i)
                    String skuText = getSkuContent(positionId, goodsResp)
                    if (!StringUtils.isNull(skuText)) {
                        UrlInfo[] urlInfos = goodsResp.getImageInfo().getImageList()
                        String img = urlInfos[0].getUrl()
                        println img
                        buffer.append(skuText + "\n")
                        if (!StringUtils.isNull(img)) {
                            imgList.add(img)
                        }
                    }
                }

            } catch (Exception e) {
                println "getSkuList ERROR:" + e.getLocalizedMessage()
            }
            String[] img = imgList.toArray()

            if (img.length > 0) {
                String path = ImageUtils.mergeImage(img, 1, new ConfigUtils().getHtmlDirPath() + IMG_URI + System.currentTimeMillis() + ".jpg")
                if (!StringUtils.isNull(path)) {
                    println "path:" + path
                    path = path.replaceAll(new ConfigUtils().getHtmlDirPath(), new ConfigUtils().getApiHost())
                    buffer.append("[IMG]" + path + "[/IMG]")
                }
            }
        }
        return findLine(buffer).toString()
    }


    /**
     * 调用商品搜索接口
     * @param searchParams
     * @return
     */
    public JSONArray getGoodsListBySearch(SearchParams searchParams) {
        String jsonStr = jdUnionService.getGoodsListBySearch(searchParams)
        JSONObject jsonObjec = (JSONObject) JSONObject.parse(jsonStr)
        String code = jsonObjec.get("code")
        String message = jsonObjec.get("message")
        if (code.equals('200')) {
            JSONArray jsonArray = jsonObjec.get('data') as JSONArray
            return jsonArray
        } else {
            return null
        }
    }


    /**
     * 生成内容
     * @param positionId
     * @param goodsResp
     * @return
     */
    public String getSkuContent(long positionId, GoodsResp goodsResp) {
//        Coupon coupon = autoFindCoupon(goodsResp.getCouponInfo().couponList)
        Coupon coupon = autoFindCouponByBest(goodsResp.getCouponInfo().couponList)

        double couponPrice = 0.0
        String link = ''
        if (coupon) {
            couponPrice = coupon.getDiscount()
            link = coupon.getLink()
        }
        String skuName = goodsResp.getSkuName()
        double jdPrice = goodsResp.getPriceInfo().getLowestPrice()
        double newPrice = jdPrice - couponPrice
        String materialUrl = goodsResp.getMaterialUrl()
        String owner = goodsResp.getOwner()
        String orderCount = goodsResp.getInOrderCount30Days()
        Double goodCommentsShare = goodsResp.getGoodCommentsShare()
        int goodCS = 100
        try {
            goodCS = goodCommentsShare.intValue()
        } catch (Exception e) {
        }
        PromotionCodeParams params = new PromotionCodeParams()
        params.setApp_key(JDConfig.APP_KEY)
        params.setApp_secret(JDConfig.SECRET_KEY)
        params.setMaterialId(materialUrl)
        params.setPositionId(positionId)
        params.setChainType(2)
        params.setCouponUrl(link)
        String shortUrl = getUnionLink(params)
        if (shortUrl) {
            HashMap map = new HashMap()
            map.put("skuName", skuName)
            map.put("jdPrice", jdPrice)
            map.put("owner", owner)
            map.put("good", goodCS)
            map.put("orderCount", orderCount)
            map.put("newPrice", newPrice)
            map.put("couponPrice", couponPrice)
            map.put("shortUrl", shortUrl)
            String skuText = new TemplateUtils().getFreeMarkerFromResource("api/skuInfo.ftl", map, "UTF-8")
            return skuText
        }

    }

    /**
     * 生成推广链接
     * @param promotionCodeParams
     * @return
     */
    public String getUnionLink(PromotionCodeParams promotionCodeParams) {
        String jsonStr = jdUnionService.getGoodsUnionLink(promotionCodeParams)
        JSONObject jsonObjec = (JSONObject) JSONObject.parse(jsonStr)
        String code = jsonObjec.get("code")
        String message = jsonObjec.get("message")
        println code + ' ' + message
        if (code.equals('200')) {
            JSONObject jsonObject = jsonObjec.get('data')
            PromotionCodeResp promotionCodeResp = jsonObject.toJavaObject(PromotionCodeResp.class)
            return promotionCodeResp.getShortURL()
        }
    }

    /**
     * 过滤成短名称，控制在10个字内
     * @param oldTitle
     * @return
     */
    public String getShortTitle(String oldTitle) {
        if (!StringUtils.isNull(oldTitle)) {
            oldTitle = oldTitle.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "").toLowerCase()
//            int _size=oldTitle.length()*title_v
            int _size = 10
            if (oldTitle.length() < _size) {
                _size = oldTitle.length()
            }
            if (_size > 0) {
                String shortTitle = oldTitle.substring(0, _size)
                return shortTitle
            }
        }
    }

    public StringBuffer findLine(StringBuffer content) {
        String ll = "--------------------"
        int start = content.lastIndexOf(ll)
        if (start > 0) {
            int end = start + ll.length()
            content = content.delete(start, end)
        }
        return content
    }


    /**
     * 直接找best=1的券
     * @param couponList
     * @return
     */
    public Coupon autoFindCouponByBest(Coupon[] couponList) {

        if (couponList.length > 0) {
            for (int i = 0; i < couponList.length; i++) {
                Coupon _coupon = couponList[i]
                int isBest = _coupon.isBest
                if (isBest == 1) {
                    return _coupon
                }
            }
        }
    }

    /**
     * 自动找券
     * @param couponList
     * @return
     */
    public Coupon autoFindCoupon(Coupon[] couponList) {
        int _bestIndex = -1
        int _maxDiscountIndex = -1
        if (couponList.length == 1) {
            return couponList[0]
        }
        if (couponList.length > 0) {
            double _maxDiscount = 0
            long nowTamp = System.currentTimeMillis()

            for (int i = 0; i < couponList.length; i++) {
                Coupon _coupon = couponList[i]
                int isBest = _coupon.isBest
                double discount = _coupon.discount //券面额
                long endTime = _coupon.getEndTime
                if (endTime > nowTamp) {
                    if (discount > _maxDiscount) {
                        _maxDiscount = discount
                        _maxDiscountIndex = i
                    }
                    if (isBest == 1) {
                        _bestIndex = i
                    }
                }
            }
        }
        int selectIndex = -1
        //有bast的情况下优先选best,没有的时候选券面值最大的
        if (_bestIndex > -1) {
            selectIndex = _bestIndex
        } else if (_maxDiscountIndex > -1) {
            selectIndex = _maxDiscountIndex
        }
        if (selectIndex > -1) {
            Coupon _coupon = couponList[selectIndex]
            return _coupon
        } else {
            return null
        }
    }


}

