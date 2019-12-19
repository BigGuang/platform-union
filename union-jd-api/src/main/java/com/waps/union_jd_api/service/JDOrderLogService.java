package com.waps.union_jd_api.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.waps.service.jd.api.bean.OrderQueryParams;
import com.waps.service.jd.api.service.JdUnionService;
import com.waps.service.jd.es.domain.*;
import com.waps.service.jd.es.service.JDOrderESService;
import com.robot.netty.server.OnLineService;
import com.waps.service.jd.es.service.JDSkuInfoESService;
import com.waps.union_jd_api.utils.JDConfig;
import com.waps.utils.StringUtils;
import jd.union.open.goods.promotiongoodsinfo.query.response.PromotionGoodsResp;
import jd.union.open.goods.promotiongoodsinfo.query.response.UnionOpenGoodsPromotiongoodsinfoQueryResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.SearchHits;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class JDOrderLogService {

    @Autowired
    private JDOrderESService jdOrderESService;

    @Autowired
    private JDMediaService jdMediaService;

    @Autowired
    private JDCategoryService jdCategoryService;

    @Autowired
    private JdUnionService jdUnionService;

    @Autowired
    private JDSkuInfoESService jdSkuInfoESService;

    public void getOrderLogByDay(String dateStr) {

        System.out.println("===加载字典信息===");
        jdMediaService.loadAllChannelInfo2Map(1);
        jdCategoryService.loadAllCategory2Map(1);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(dateStr);

            String strDateFormat = "yyyyMMdd";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            String dayStr = sdf.format(date);
            System.out.println(dayStr);
            for (int i = 0; i < 24; i++) {
                String h = "" + i;
                if (h.length() == 1) {
                    h = "0" + i;
                }
                String dStr = dayStr + "" + h;
                System.out.println(dStr);
                OrderQueryParams orderQueryParams = new OrderQueryParams();
                orderQueryParams.setApp_key(JDConfig.APP_KEY);
                orderQueryParams.setApp_secret(JDConfig.SECRET_KEY);
                orderQueryParams.setPageNo(1);
                orderQueryParams.setPageSize(50);
                orderQueryParams.setTime(dStr);
                orderQueryParams.setType(3);
                getOrderLogByParams(orderQueryParams);
                Thread.sleep(500);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getOrderLogByParams(OrderQueryParams orderQueryParams) {
        String retJson = jdUnionService.getGoodsOrderQuery(orderQueryParams);
        JSONObject jsonObject = JSONObject.parseObject(retJson);
        Boolean hasMore = (Boolean) jsonObject.get("hasMore");
        Integer code = (Integer) jsonObject.get("code");
        String message = (String) jsonObject.get("message");
//        System.out.println("code=" + code);
//        System.out.println("message=" + message);
//        System.out.println("hasMore=" + hasMore);
        JSONArray jsonArray = (JSONArray) jsonObject.get("data");
        if (jsonArray != null) {
            System.out.println(jsonArray.size());
            System.out.println("=============");
            ArrayList<Object> bulkList = new ArrayList<>();
            for (Object object : jsonArray) {
                String objJson = JSONObject.toJSONString(object);
                JDOrderInfoESMap jdOrderInfoESMap = JSONObject.parseObject(objJson, JDOrderInfoESMap.class);
                jdOrderInfoESMap.setId(jdOrderInfoESMap.getOrderId());
                jdOrderInfoESMap.setCreatetime(timeTmp2DateStr(System.currentTimeMillis() + ""));
                if (!jdOrderInfoESMap.getFinishTime().equals("0")) {
                    jdOrderInfoESMap.setFinishTime_Date(timeTmp2DateStr(jdOrderInfoESMap.getFinishTime()));
                }
                if (!jdOrderInfoESMap.getOrderTime().equals("0")) {
                    jdOrderInfoESMap.setOrderTime_Date(timeTmp2DateStr(jdOrderInfoESMap.getOrderTime()));
                }
                if (!StringUtils.isNull(jdOrderInfoESMap.getExt1()) && jdOrderInfoESMap.getExt1().contains("_")) {
                    String[] group = jdOrderInfoESMap.getExt1().split("_");
                    jdOrderInfoESMap.setExt1_positionId(group[0]);
                    jdOrderInfoESMap.setExt1_user(group[1]);
                }

                List<SkuInfoESMap> newSkuList = new ArrayList<>();
                List<SkuInfoESMap> skuInfoESMapList = jdOrderInfoESMap.getSkuList();
                for (SkuInfoESMap skuInfoESMap : skuInfoESMapList) {
                    String cid1 = skuInfoESMap.getCid1();
                    String cid2 = skuInfoESMap.getCid2();
                    String cid3 = skuInfoESMap.getCid3();
                    String positionId = skuInfoESMap.getPositionId();
                    skuInfoESMap.setCid1_name(cid1);
                    skuInfoESMap.setCid2_name(cid2);
                    skuInfoESMap.setCid3_name(cid3);
                    skuInfoESMap.setPositionName(positionId);
                    if (!StringUtils.isNull(cid1)) {
                        JDCategoryESMap jdCategoryESMap1 = OnLineService.getJdCategory(cid1);
                        if (jdCategoryESMap1 != null)
                            skuInfoESMap.setCid1_name(jdCategoryESMap1.getName());
                    }
                    if (!StringUtils.isNull(cid2)) {
                        JDCategoryESMap jdCategoryESMap2 = OnLineService.getJdCategory(cid2);
                        if (jdCategoryESMap2 != null)
                            skuInfoESMap.setCid2_name(jdCategoryESMap2.getName());
                    }
                    if (!StringUtils.isNull(cid3)) {
                        JDCategoryESMap jdCategoryESMap3 = OnLineService.getJdCategory(cid3);
                        if (jdCategoryESMap3 != null)
                            skuInfoESMap.setCid3_name(jdCategoryESMap3.getName());
                    }

                    if (!StringUtils.isNull(positionId)) {
                        JDMediaInfoESMap jdMediaInfoESMap = OnLineService.getChennelInfo(positionId);
                        if (jdMediaInfoESMap != null)
                            skuInfoESMap.setPositionName(jdMediaInfoESMap.getChannel_name());
                    }
                    if (!StringUtils.isNull(skuInfoESMap.getExt1()) && skuInfoESMap.getExt1().contains("_")) {
                        String[] group = skuInfoESMap.getExt1().split("_");
                        skuInfoESMap.setExt1_positionId(group[0]);
                        skuInfoESMap.setExt1_user(group[1]);
                    }
                    newSkuList.add(skuInfoESMap);
                }
                jdOrderInfoESMap.setSkuList(newSkuList);

                //获取商品图
                String skuImg = getSkuImage(jdOrderInfoESMap);
                jdOrderInfoESMap.setSkuImg(skuImg);
                bulkList.add(jdOrderInfoESMap);
            }
            if (bulkList.size() > 0) {
                jdOrderESService.saveBulk(bulkList);
            }
        }
        if (hasMore) {
            orderQueryParams.setPageNo(orderQueryParams.getPageNo() + 1);
            getOrderLogByParams(orderQueryParams);
        }
    }


    public String getSkuImage(JDOrderInfoESMap jdOrderInfoESMap) {
        String skuImage = "";
        try {
            JDOrderInfoESMap _oldJdOrderInfoMap = (JDOrderInfoESMap) jdOrderESService.load(jdOrderInfoESMap.getId(), JDOrderInfoESMap.class);
            //从已有数据中获取图片
            if (_oldJdOrderInfoMap != null && !StringUtils.isNull(_oldJdOrderInfoMap.getSkuImg())) {
                skuImage = _oldJdOrderInfoMap.getSkuImg();
            }
        } catch (Exception e) {
            System.out.println("es中无法读取图片:" + e.getLocalizedMessage());
        }


        //从京东获取图片
        String sku_ID = "";
        try {
            if (StringUtils.isNull(skuImage)) {
                if (jdOrderInfoESMap.getSkuList().size() > 0) {
                    SkuInfoESMap skuInfoESMap = jdOrderInfoESMap.getSkuList().get(0);
                    String skuID = skuInfoESMap.getSkuId();
                    sku_ID = skuID;

                    UnionOpenGoodsPromotiongoodsinfoQueryResponse goodsResponse = jdUnionService.getGoodsQueryPromotionInfoResponse(skuID);
                    if (goodsResponse.getCode() == 200) {
                        PromotionGoodsResp goodsResp = goodsResponse.getData()[0];
                        skuImage = goodsResp.getImgUrl();
                    } else {
                        System.out.println("获取图片错误:" + skuID + " 图片:" + goodsResponse.getCode() + " " + goodsResponse.getMessage());
                        JDSkuInfoESMap jdSkuInfoESMap = (JDSkuInfoESMap) jdSkuInfoESService.load(skuID, JDSkuInfoESMap.class);
                        if (jdSkuInfoESMap != null) {
                            skuImage = jdSkuInfoESMap.getImageInfo().getImageList()[0].getUrl();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("无法从JD获取商品图片:" + sku_ID + " ERROR:" + e.getLocalizedMessage());
        }
        return skuImage;
    }

    public String timeTmp2DateStr(String tmp) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(tmp);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    public SearchHits findChannelOrderListToHits(HashMap paramsMap) {
        SearchHits hits = jdOrderESService.findByFreeMarkerFromResource("es_script/jd_order_list_byUser.json", paramsMap);
        return hits;
    }

    public SearchHits findAllOrderListToHits(HashMap paramsMap) {
        SearchHits hits = jdOrderESService.findByFreeMarkerFromResource("es_script/jd_order_list_byAll.ftl", paramsMap);
        return hits;
    }

}
