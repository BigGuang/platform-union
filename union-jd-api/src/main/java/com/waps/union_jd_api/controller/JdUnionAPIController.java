package com.waps.union_jd_api.controller;

import com.waps.service.jd.api.bean.*;
import com.waps.union_jd_api.service.JDOrderLogService;
import com.waps.service.jd.api.service.JdUnionService;
import com.waps.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api")
public class JdUnionAPIController {

    @Autowired
    private JdUnionService jdUnionService;

    @Autowired
    private JDOrderLogService jdOrderLogService;

    /**
     * 通过subUnionId获取推广链接【申请】jd.union.open.promotion.bysubunionid.get
     *
     * @param promotionCodeParams
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/union_link", method = RequestMethod.POST)
    public void getLink(
            @RequestBody PromotionCodeParams promotionCodeParams,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String json = jdUnionService.getGoodsUnionLink(promotionCodeParams);
        ResponseUtils.write(response, json);
    }

    /**
     * 关键词商品查询接口【申请】jd.union.open.goods.query
     *
     * @param searchParams
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/goods_query",  method = RequestMethod.POST, produces = "application/json;charset=utf8")
    public void getSearch(
            @RequestBody SearchParams searchParams,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

//        System.out.println(mapJson);
//        SearchParams searchParams=JSONObject.parseObject(mapJson,SearchParams.class);
        String json = jdUnionService.getGoodsListBySearch(searchParams);
        ResponseUtils.write(response, json);
    }


    /**
     * 优惠券领取情况查询接口【申请】jd.union.open.coupon.query
     *
     * @param urlList
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/coupon_query", method = RequestMethod.POST)
    public void getCouponList(
            @RequestParam(value = "urlList", required = true) String[] urlList,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String json = jdUnionService.getGoodsCouponList(urlList);
        ResponseUtils.write(response, json);
    }

    /**
     * 秒杀商品查询接口【申请】jd.union.open.goods.seckill.query
     *
     * @param secKillParams
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/seckill_query", method = RequestMethod.POST)
    public void getCouponList(
            @RequestBody SecKillParams secKillParams,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String json = jdUnionService.getGoodsSecKillList(secKillParams);
        ResponseUtils.write(response, json);
    }

    //===无需开权限的接口===

    /**
     * jd.union.open.category.goods.get 商品类目查询
     *
     * @param categoryParams
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/get_category", method = RequestMethod.POST)
    public void getCategoryList(
            @RequestBody CategoryParams categoryParams,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String json = jdUnionService.getGoodsCategory(categoryParams);
        ResponseUtils.write(response, json);
    }

    /**
     * jd.union.open.goods.jingfen.query 京粉精选商品查询接口
     *
     * @param jfGoodsParams
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/jingfen_query", method = RequestMethod.POST)
    public void getGoodsJingFenQuery(
            @RequestBody JFGoodsParams jfGoodsParams,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String json = jdUnionService.getGoodsQueryJingFen(jfGoodsParams);
        ResponseUtils.write(response, json);
    }

    /**
     * jd.union.open.goods.promotiongoodsinfo.query 获取推广商品信息接口
     *
     * @param skuIds
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/promotion_info_query")
    public void getGoodsPromotionInfo(
            @RequestParam(value = "skuIds", required = true) String skuIds,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String json = jdUnionService.getGoodsQueryPromotionInfo(skuIds);
        ResponseUtils.write(response, json);
    }

    /**
     * jd.union.open.promotion.common.get 获取通用推广链接
     *
     * @param promotionCodeCommonParams
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/union_link_common", method = RequestMethod.POST)
    public void getGoodsUnionLinkCommon(
            @RequestBody PromotionCodeCommonParams promotionCodeCommonParams,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String json = jdUnionService.getGoodsUnionLinkCommon(promotionCodeCommonParams);
        ResponseUtils.write(response, json);
    }

    /**
     * jd.union.open.user.pid.get 获取PID
     *
     * @param pidParams
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/get_pid", method = RequestMethod.POST)
    public void getPid(
            @RequestBody PidParams pidParams,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String json = jdUnionService.getPid(pidParams);
        ResponseUtils.write(response, json);
    }

    /**
     * jd.union.open.order.query 订单查询接口
     *
     * @param orderQueryParams
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/order_query", method = RequestMethod.POST)
    public void getGoodsOrderQuery(
            @RequestBody OrderQueryParams orderQueryParams,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String json = jdUnionService.getGoodsOrderQuery(orderQueryParams);
        ResponseUtils.write(response, json);
    }
}
