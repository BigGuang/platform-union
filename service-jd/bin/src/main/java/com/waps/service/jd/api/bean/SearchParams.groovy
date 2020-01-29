package com.waps.service.jd.api.bean

import jd.union.open.goods.query.request.GoodsReq

class SearchParams extends GoodsReq implements Serializable{
//    Long cid1        //一级类目id
//    Long cid2        //二级类目id
//    Long cid3        //三级类目id
//    Integer pageIndex = 1    //页码
//    Integer pageSize = 20    //每页数量，单页数最大30，默认20
//    Long[] skuIds      //skuid集合(一次最多支持查询100个sku)，数组类型开发时记得加[]
//    String keyword     //关键词，字数同京东商品名称一致，目前未限制
//    Double pricefrom   //商品价格下限
//    Double priceto     //商品价格上限
//    Integer commissionShareStart     //佣金比例区间开始
//    Integer commissionShareEnd       //佣金比例区间结束
//    String owner        //商品类型：自营[g]，POP[p]
//    String sortName
//    //排序字段(price：单价, commissionShare：佣金比例, commission：佣金， inOrderCount30Days：30天引单量， inOrderComm30Days：30天支出佣金)
//    String sort        //asc,desc升降序,默认降序
//    Integer isCoupon = 1       //是否是优惠券商品，1：有优惠券，0：无优惠券
//    Integer isPG = 0             //是否是拼购商品，1：拼购商品，0：非拼购商品
//    Double pingouPriceStart     //拼购价格区间开始
//    Double pingouPriceEnd       //拼购价格区间结束
//    Integer isHot = 1            //是否是爆款，1：爆款商品，0：非爆款商品
//    String brandCode       //品牌code
//    Integer shopId            //店铺Id
    String app_key
    String app_secret
}
