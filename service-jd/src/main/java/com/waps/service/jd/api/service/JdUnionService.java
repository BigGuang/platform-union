package com.waps.service.jd.api.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.waps.service.jd.api.bean.*;
import com.waps.tools.test.TestUtils;
import jd.union.open.category.goods.get.request.CategoryReq;
import jd.union.open.category.goods.get.request.UnionOpenCategoryGoodsGetRequest;
import jd.union.open.category.goods.get.response.UnionOpenCategoryGoodsGetResponse;
import jd.union.open.coupon.query.request.UnionOpenCouponQueryRequest;
import jd.union.open.coupon.query.response.UnionOpenCouponQueryResponse;
import jd.union.open.goods.jingfen.query.request.JFGoodsReq;
import jd.union.open.goods.jingfen.query.request.UnionOpenGoodsJingfenQueryRequest;
import jd.union.open.goods.jingfen.query.response.UnionOpenGoodsJingfenQueryResponse;
import jd.union.open.goods.promotiongoodsinfo.query.request.UnionOpenGoodsPromotiongoodsinfoQueryRequest;
import jd.union.open.goods.promotiongoodsinfo.query.response.UnionOpenGoodsPromotiongoodsinfoQueryResponse;
import jd.union.open.goods.query.request.GoodsReq;
import jd.union.open.goods.query.request.UnionOpenGoodsQueryRequest;
import jd.union.open.goods.query.response.UnionOpenGoodsQueryResponse;
import jd.union.open.goods.seckill.query.request.SecKillGoodsReq;
import jd.union.open.goods.seckill.query.request.UnionOpenGoodsSeckillQueryRequest;
import jd.union.open.goods.seckill.query.response.UnionOpenGoodsSeckillQueryResponse;
import jd.union.open.order.query.request.OrderReq;
import jd.union.open.order.query.request.UnionOpenOrderQueryRequest;
import jd.union.open.order.query.response.UnionOpenOrderQueryResponse;
import jd.union.open.promotion.bysubunionid.get.request.UnionOpenPromotionBysubunionidGetRequest;
import jd.union.open.promotion.bysubunionid.get.response.UnionOpenPromotionBysubunionidGetResponse;
import jd.union.open.promotion.common.get.request.UnionOpenPromotionCommonGetRequest;
import jd.union.open.promotion.common.get.response.UnionOpenPromotionCommonGetResponse;
import jd.union.open.user.pid.get.request.PidReq;
import jd.union.open.user.pid.get.request.UnionOpenUserPidGetRequest;
import jd.union.open.user.pid.get.response.UnionOpenUserPidGetResponse;
import org.springframework.stereotype.Component;

@Component
public class JdUnionService {

    public final static String SERVER_URL = "https://router.jd.com/api";
    public final static String appKey = "733c0c9c35f344fc5071172c4bc148fe";
    public final static String appSecret = "96bd02a7de4b425bb724fb56e7c66a84";
    public final static String accessToken = "";  //第三方授权用
    public final static String UNION_ID = "1000440357";


    //======以下为授权开通的接口======

    /**
     * 通过subUnionId获取推广链接【申请】jd.union.open.promotion.bysubunionid.get
     *
     * @param promotionCodeParams
     * @return
     */
    public UnionOpenPromotionBysubunionidGetResponse getGoodsUnionLinkResponse(PromotionCodeParams promotionCodeParams) {

        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, promotionCodeParams.getApp_key(), promotionCodeParams.getApp_secret());

        UnionOpenPromotionBysubunionidGetRequest request = new UnionOpenPromotionBysubunionidGetRequest();
        jd.union.open.promotion.bysubunionid.get.request.PromotionCodeReq promotionCodeReq = new jd.union.open.promotion.bysubunionid.get.request.PromotionCodeReq();
        if (promotionCodeParams != null) {
            promotionCodeReq = promotionCodeParams;
        }

        request.setPromotionCodeReq(promotionCodeReq);
        try {
            UnionOpenPromotionBysubunionidGetResponse response = client.execute(request);
            response.setSysOriginalMsg("");
            response.setSysRequestUrl("");
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过subUnionId获取推广链接【申请】jd.union.open.promotion.bysubunionid.get
     *
     * @param promotionCodeParams
     * @return
     */
    public String getGoodsUnionLink(PromotionCodeParams promotionCodeParams) {

        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, promotionCodeParams.getApp_key(), promotionCodeParams.getApp_secret());

        UnionOpenPromotionBysubunionidGetRequest request = new UnionOpenPromotionBysubunionidGetRequest();
        jd.union.open.promotion.bysubunionid.get.request.PromotionCodeReq promotionCodeReq = new jd.union.open.promotion.bysubunionid.get.request.PromotionCodeReq();
        if (promotionCodeParams != null) {
            promotionCodeReq = promotionCodeParams;
        }

        request.setPromotionCodeReq(promotionCodeReq);
        try {
            UnionOpenPromotionBysubunionidGetResponse response = client.execute(request);
            response.setSysOriginalMsg("");
            response.setSysRequestUrl("");
            return JSONObject.toJSONString(response);
        } catch (JdException e) {
            System.out.println(e.getLocalizedMessage());
            return ErrorBean.returnErrorJson(500, e.getLocalizedMessage());
        }
    }


    /**
     * 关键词商品查询接口【申请】jd.union.open.goods.query
     *
     * @param searchParams
     * @return
     */
    public String getGoodsListBySearch(SearchParams searchParams) {
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, searchParams.getApp_key(), searchParams.getApp_secret());

        UnionOpenGoodsQueryRequest request = new UnionOpenGoodsQueryRequest();
        GoodsReq goodsReq = new GoodsReq();

        if (searchParams != null) {
            goodsReq = searchParams;
        }

        request.setGoodsReqDTO(goodsReq);
        try {
            UnionOpenGoodsQueryResponse response = client.execute(request);
            response.setSysOriginalMsg("");
            response.setSysRequestUrl("");
            return JSONObject.toJSONString(response);
        } catch (JdException e) {
            System.out.println(e.getLocalizedMessage());
            return ErrorBean.returnErrorJson(500, e.getLocalizedMessage());
        }
    }


    /**
     * 优惠券领取情况查询接口【申请】jd.union.open.coupon.query
     *
     * @param urlList
     * @return
     */
    public String getGoodsCouponList(String[] urlList) {
//        new String[]{"http://coupon.jd.com/ilink/couponActiveFront/front_index.action?key=61c19ff15ceb450c8d801c50f729069d&roleId=7290729&to=jyhbj.jd.hk"}
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, appKey, appSecret);

        UnionOpenCouponQueryRequest request = new UnionOpenCouponQueryRequest();
        request.setCouponUrls(urlList);
        try {
            UnionOpenCouponQueryResponse response = client.execute(request);
            response.setSysOriginalMsg("");
            response.setSysRequestUrl("");
            return JSONObject.toJSONString(response);
        } catch (JdException e) {
            System.out.println(e.getLocalizedMessage());
            return ErrorBean.returnErrorJson(500, e.getLocalizedMessage());
        }
    }


    /**
     * 秒杀商品查询接口【申请】jd.union.open.goods.seckill.query
     *
     * @param secKillParams
     * @return
     */
    public String getGoodsSecKillList(SecKillParams secKillParams) {
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, secKillParams.getApp_key(), secKillParams.getApp_secret());
        UnionOpenGoodsSeckillQueryRequest request = new UnionOpenGoodsSeckillQueryRequest();
        SecKillGoodsReq secKillGoodsReq = new SecKillGoodsReq();
        if (secKillParams != null) {
            secKillGoodsReq = secKillParams;
        }
        request.setGoodsReq(secKillGoodsReq);
        try {
            UnionOpenGoodsSeckillQueryResponse response = client.execute(request);
            response.setSysOriginalMsg("");
            response.setSysRequestUrl("");
            return JSONObject.toJSONString(response);
        } catch (JdException e) {
            System.out.println(e.getLocalizedMessage());
            return ErrorBean.returnErrorJson(500, e.getLocalizedMessage());
        }
    }


    //======以下为不需授权的接口======

    /**
     * jd.union.open.category.goods.get 商品类目查询
     *
     * @param categoryParams
     * @return
     */
    public String getGoodsCategory(CategoryParams categoryParams) {
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, categoryParams.getApp_key(), categoryParams.getApp_secret());

        UnionOpenCategoryGoodsGetRequest request = new UnionOpenCategoryGoodsGetRequest();
        CategoryReq categoryReq = new CategoryReq();
        if (categoryParams != null) {
            categoryReq = categoryParams;
        }
        request.setReq(categoryReq);
        try {
            UnionOpenCategoryGoodsGetResponse response = client.execute(request);
            response.setSysOriginalMsg("");
            response.setSysRequestUrl("");
            return JSONObject.toJSONString(response);
        } catch (JdException e) {
            System.out.println(e.getLocalizedMessage());
            return ErrorBean.returnErrorJson(500, e.getLocalizedMessage());
        }
    }

    /**
     * jd.union.open.goods.jingfen.query 京粉精选商品查询接口
     *
     * @param jfGoodsParams
     * @return
     */
    public String getGoodsQueryJingFen(JFGoodsParams jfGoodsParams) {
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, jfGoodsParams.getApp_key(), jfGoodsParams.getApp_secret());

        UnionOpenGoodsJingfenQueryRequest request = new UnionOpenGoodsJingfenQueryRequest();
        JFGoodsReq goodsReq = new JFGoodsReq();
        if (jfGoodsParams != null) {
            goodsReq = jfGoodsParams;
        }
        request.setGoodsReq(goodsReq);
        try {
            UnionOpenGoodsJingfenQueryResponse response = client.execute(request);
            response.setSysOriginalMsg("");
            response.setSysRequestUrl("");
            return JSONObject.toJSONString(response);
        } catch (JdException e) {
            System.out.println(e.getLocalizedMessage());
            return ErrorBean.returnErrorJson(500, e.getLocalizedMessage());
        }
    }

    /**
     * jd.union.open.goods.promotiongoodsinfo.query 获取推广商品信息接口
     *
     * @param skuIds "5225346,7275691"  多个逗号
     * @return
     */
    public String getGoodsQueryPromotionInfo(String skuIds) {
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, appKey, appSecret);

        UnionOpenGoodsPromotiongoodsinfoQueryRequest request = new UnionOpenGoodsPromotiongoodsinfoQueryRequest();
        request.setSkuIds(skuIds);
        try {
            UnionOpenGoodsPromotiongoodsinfoQueryResponse response = client.execute(request);
            response.setSysOriginalMsg("");
            response.setSysRequestUrl("");
            return JSONObject.toJSONString(response);
        } catch (JdException e) {
            System.out.println(e.getLocalizedMessage());
            return ErrorBean.returnErrorJson(500, e.getLocalizedMessage());
        }
    }


    public UnionOpenGoodsPromotiongoodsinfoQueryResponse getGoodsQueryPromotionInfoResponse(String skuIds) {
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, appKey, appSecret);
        UnionOpenGoodsPromotiongoodsinfoQueryRequest request = new UnionOpenGoodsPromotiongoodsinfoQueryRequest();
        request.setSkuIds(skuIds);
        try {
            return client.execute(request);
        } catch (JdException e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }


    /**
     * jd.union.open.promotion.common.get 获取通用推广链接
     *
     * @param promotionCodeCommonParams
     * @return
     */
    public String getGoodsUnionLinkCommon(PromotionCodeCommonParams promotionCodeCommonParams) {
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, promotionCodeCommonParams.getApp_key(), promotionCodeCommonParams.getApp_secret());

        UnionOpenPromotionCommonGetRequest request = new UnionOpenPromotionCommonGetRequest();
        jd.union.open.promotion.common.get.request.PromotionCodeReq promotionCodeReq = new jd.union.open.promotion.common.get.request.PromotionCodeReq();
        if (promotionCodeCommonParams != null) {
            promotionCodeReq = promotionCodeCommonParams;
        }
        try {
            request.setPromotionCodeReq(promotionCodeReq);
            UnionOpenPromotionCommonGetResponse response = client.execute(request);
            response.setSysOriginalMsg("");
            response.setSysRequestUrl("");
            return JSONObject.toJSONString(response);
        } catch (JdException e) {
            System.out.println(e.getLocalizedMessage());
            return ErrorBean.returnErrorJson(500, e.getLocalizedMessage());
        }
    }


    /**
     * jd.union.open.user.pid.get 获取PID
     *
     * @param pidParams
     * @return
     */
    public String getPid(PidParams pidParams) {
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, pidParams.getApp_key(), pidParams.getApp_secret());

        UnionOpenUserPidGetRequest unionOpenUserPidGetRequest = new UnionOpenUserPidGetRequest();
        PidReq pidReq = new PidReq();
        if (pidParams != null) {
            pidReq = pidParams;
        }

        try {
            unionOpenUserPidGetRequest.setPidReq(pidReq);
            UnionOpenUserPidGetResponse response = client.execute(unionOpenUserPidGetRequest);
            response.setSysOriginalMsg("");
            response.setSysRequestUrl("");
            return JSONObject.toJSONString(response);
        } catch (JdException e) {
            System.out.println(e.getLocalizedMessage());
            return ErrorBean.returnErrorJson(500, e.getLocalizedMessage());
        }

    }


    /**
     * jd.union.open.order.query 订单查询接口
     *
     * @param orderQueryParams
     * @return
     */
    public String getGoodsOrderQuery(OrderQueryParams orderQueryParams) {
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, orderQueryParams.getApp_key(), orderQueryParams.getApp_secret());

        UnionOpenOrderQueryRequest unionOpenOrderQueryRequest = new UnionOpenOrderQueryRequest();
        OrderReq orderReq = new OrderReq();
        if (orderQueryParams != null) {
            orderReq = orderQueryParams;
        }
//        orderReq.setPageNo(1);
//        orderReq.setPageSize(3);
//        orderReq.setType(1);
//        orderReq.setTime("2018092018");
        try {
            unionOpenOrderQueryRequest.setOrderReq(orderReq);
            UnionOpenOrderQueryResponse response = client.execute(unionOpenOrderQueryRequest);
            response.setSysOriginalMsg("");
            response.setSysRequestUrl("");
            return JSONObject.toJSONString(response);
        } catch (JdException e) {
            System.out.println(e.getLocalizedMessage());
            return ErrorBean.returnErrorJson(500, e.getLocalizedMessage());
        }
    }
}
