

//跳转到促销管理
function toPromotionsManager(openFlag){
    if(!openFlag){
        window.location.href="promotions.do?method=toPromotionsManager"
    }else{
        window.open("promotions.do?method=toPromotionsManager");
    }
}

//单个商品上架
function toGoodsInSalesEditor(productId){
    productId=G.isEmpty(productId)?"":productId;
    window.location.href="goodsInOffSales.do?method=toGoodsInSalesEditor&productId="+String(productId);
}
//上架商品列表
function toInSalingGoodsList(){
    window.location.href="goodsInOffSales.do?method=toInSalingGoodsList";
}
//商品报价
function toCommodityQuotations(){
    window.location.href="autoAccessoryOnline.do?method=toCommodityQuotations";
}
//店铺中商品详细
function toShopProductDetail(paramShopId,productLocalId,fromSource){
    if(G.isEmpty(paramShopId)||G.isEmpty(productLocalId)){
        return;
    }
    window.open("shopProductDetail.do?method=toShopProductDetail" +
        "&paramShopId="+paramShopId+"&productLocalId="+productLocalId+"&fromSource="+fromSource);
}
//店铺资料
function renderShopMsgDetail(paramShopId,openFlag){
    if(G.isEmpty(paramShopId)){
        return;
    }
     if(!openFlag){
         window.location.href='shopMsgDetail.do?method=renderShopMsgDetail&paramShopId='+paramShopId;
     }else{
        window.open('shopMsgDetail.do?method=renderShopMsgDetail&paramShopId='+paramShopId);
     }
}
//求购商品详细
function showBuyInformationDetail(preBuyOrderItemId){
    if(G.isEmpty(preBuyOrderItemId)){
        return;
    }
    window.location.href='preBuyOrder.do?method=showBuyInformationDetailByPreBuyOrderItemId&preBuyOrderItemId='+preBuyOrderItemId;
}
//求购列表（汽配）
function preBuyInformation(){
    window.location.href='preBuyOrder.do?method=preBuyInformation';
}
