<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ taglib prefix="numberUtil" uri="http://www.bcgogo.com/taglibs/tags" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>我的求购</title>
<%@include file="/WEB-INF/views/header_script.jsp" %>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css">
<script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-scrollFlow<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
defaultStorage.setItem(storageKey.MenuUid, "WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER");
defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
$(document).ready(function() {
    $(".J_toggle_down").toggle(
            function () {
                $(".J_toggle_up").click();
                $(this).removeClass("J_toggle_down").addClass("J_toggle_up");
                $("#quoted_" + $(this).attr("data-index")).show();
            },
            function () {
                $("#quoted_" + $(this).attr("data-index")).hide();
                $(this).removeClass("J_toggle_up").addClass("J_toggle_down");
            }
    );
    var preBuyOrderItemId = $("#preBuyOrderItemId").val();
    var data = {
        preBuyOrderItemId:preBuyOrderItemId,
        startPageNo:1,
        pageSize:5
    };
    APP_BCGOGO.Net.syncPost({
        url: "preBuyOrder.do?method=getQuotedPreBuyOrders",
        dataType: "json",
        data: data,
        success: function (result) {
            initQuotedPreBuyOrderInfo(result);
            initPage(result, "quotedPreBuyOrderInfo", "preBuyOrder.do?method=getQuotedPreBuyOrders", '', "initQuotedPreBuyOrderInfo", '', '', data, '');
        },
        error: function () {
            nsDialog.jAlert("数据异常，请刷新页面！");
        }
    });
    function _initProductsFromNormal(products) {
        var size =4;
        if (G.isEmpty(products)||products.length == 0) {
            return;
        }
        var length = products.length;
        var liSize = length % size == 0 ? parseInt((length / size))  : parseInt((length / size)) + 1;
        var str = '<div class="JScrollGroup" style="padding-top: 10px;">';
        for (var liIndex = 0; liIndex < liSize; liIndex ++) {
            str += '<div id="li_' + liIndex + '" class="JScrollItem goodScroll"><div class="prev JScrollPrevButton"></div><div class="next JScrollNextButton"></div></div>';
        }
        str += '</div>';
        $("#productFromNormalDiv").html(str);
        for (var liIndex = 0; liIndex < liSize; liIndex ++) {
            var liHtml='<div class="goodList"><ul>';
            for (var index = 0; index < size; index ++) {
                var s = liIndex * size + index;
                if (s >= length) {
                    continue;
                }
                var product = products[s];
                var productInfo = G.normalize(product.productInfoStr);
                var productInfoShort=productInfo.length>32?productInfo.substr(0,32)+"...":productInfo;
                var hasPromotion=!G.isEmpty(product.promotionsDTOs);
                var pTitle=generateSimplePromotionsAlertTitle(product,"search");
                var inSalesPrice=G.rounding(product.inSalesPrice);
                var imageURL=product.imageCenterDTO.recommendProductListImageDetailDTO.imageURL;
                var shopIdStr=product.shopIdStr;
                var productLocalInfoIdStr=product.productLocalInfoIdStr;
                liHtml +='<li>'+
                        '<div class="goodList_pic">'+
                        '<div class="goodList_pic_main" onclick="toShopProductDetail(\''+shopIdStr+'\',\''+productLocalInfoIdStr+'\')"><img src="'+imageURL+'"></div>';
                if(hasPromotion){
                    if(product.hasBargain){
                        liHtml+='<div class="image-promotion-bargain-80X80">'+pTitle+'</div>';
                    }else{
                        liHtml+='<div class="image-promotion-common-80X80">'+pTitle+'</div>';
                    }
                }
                liHtml+='</div>'+
                        '<div class="clear"></div>'+
                        '<div class="goodList_info">'+
                        '<div class="p-info-detail" title="'+productInfo+'" onclick="toShopProductDetail(\''+shopIdStr+'\',\''+productLocalInfoIdStr+'\')">'+productInfoShort+'</div>'+
                        '<em>¥'+inSalesPrice+'</em>'+
                        '</div>'+
                        '<div class="clear"></div>'+
                        '<div class="goodsButton">'+
                        '<input type="button" value="加入购物车" supplierShopId="'+shopIdStr+'" productId="'+productLocalInfoIdStr+'" class="goodsButton_sub J-addToShoppingCartSingleBtn"/>'+
                        '<input type="button" value="立即采购" productId="'+productLocalInfoIdStr+'" class="goodsButton_sub J-purchaseSingleBtn"/>'+
                        '</div>'+
                        '</li>';
            }
            liHtml += '</ul></div>';
            $("#li_" + liIndex).append(liHtml);
        }
        $("#goodsRecommendFromNormal").show();
        var scrollFlowHorizontal = new App.Module.ScrollFlowHorizontal();
        scrollFlowHorizontal
                .init({
            "selector": "#productFromNormalDiv",
            "width":792,
            "height":220,
            "background": "#FFFFFF",
            "scrollInterval":5000
        }).startAutoScroll();
        window.scrollFlowHorizontal = scrollFlowHorizontal;
    }
    APP_BCGOGO.Net.asyncAjax({
        url: "supplyDemand.do?method=getRecommendProductsFromNormal",
        type: "POST",
        cache: false,
        dataType: "json",
        success: function (products) {
            _initProductsFromNormal(products);
        },
        error:function(){
            nsDialog.jAlert("网络异常。");
        }
    });
    $(".J-purchaseSingleBtn").live("click",function(e){
        e.preventDefault();
        var productId = $(this).attr("productId");
        var amount = 1;
        if(!G.Lang.isEmpty(productId)){
            purchaseProduct(productId,amount);
        }
    });

    $(".J-addToShoppingCartSingleBtn").live("click",function(e){
        e.preventDefault();
        var productId = $(this).attr("productId");
        var amount ="1";
        var supplierShopId=$(this).attr("supplierShopId");
        if(G.isEmpty(productId)||G.isEmpty(supplierShopId)){
            return;
        }
        var paramString = supplierShopId+"_"+productId+"_"+amount;
        addProductToShoppingCart(paramString);
    });
    $(".J_PurchaseOnline").live("click",function(){
        var productLocalInfoId = $(this).attr("data-productLocalInfoId");
        if(productLocalInfoId) {
            purchaseProduct(productLocalInfoId,1.0);
        }
    });

    $(".J_QQ").each(function(){
        $(this).multiQQInvoker({
            QQ:$(this).attr("data_qq")
        })
    });
     $("#closeBtn").bind("click",function(e){
        e.preventDefault();
        $("#returnDialog").dialog("close");
    });
      $("#goShoppingCartBtn").bind("click",function(e){
        e.preventDefault();
        window.location.href="shoppingCart.do?method=shoppingCartManage";
    });

});

function initQuotedPreBuyOrderInfo(data) {
    var html = '';
    if(data == null || data.results == null || data.results.length == 0) {
        html = '<div class="detailed-dashed"><div class="left">暂无信息！</div><div class="clear"></div></div>';
        $(".accessoriesLeft .content").append($(html));
        return;
    }

    for(var i = 0; i < data.results.length; i++) {
        var quotedPreBuyOrderItemDTO = data.results[i];
        html += '<div class="detailed-dashed"><input type="hidden" id="quotedPreBuyOrderItem' + i + '" value="' + quotedPreBuyOrderItemDTO.idStr + '" /><div class="left">' + quotedPreBuyOrderItemDTO.quotedDateStr + '&nbsp;<a href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=' + quotedPreBuyOrderItemDTO.shopIdStr + '" class="blue_col">' + quotedPreBuyOrderItemDTO.shopName +  '</a><a class="J_QQ" data_qq="' + quotedPreBuyOrderItemDTO.qqArray + '" style="vertical-align:middle;"></a> 参与报价<br>报价商品：';
        html += '<a class="blue_color" href="shopProductDetail.do?method=toShopProductDetail&paramShopId=' + quotedPreBuyOrderItemDTO.shopIdStr + '&productLocalId=' + quotedPreBuyOrderItemDTO.productIdStr + '&quotedPreBuyOrderItemId=' + quotedPreBuyOrderItemDTO.idStr + '">' + (quotedPreBuyOrderItemDTO.commodityCode == null ? "" : quotedPreBuyOrderItemDTO.commodityCode) + '&nbsp;' + quotedPreBuyOrderItemDTO.productName + '&nbsp;' + quotedPreBuyOrderItemDTO.brand + '&nbsp;' + quotedPreBuyOrderItemDTO.spec + '&nbsp;' + quotedPreBuyOrderItemDTO.model + '&nbsp;' + quotedPreBuyOrderItemDTO.vehicleBrand + '&nbsp;' + quotedPreBuyOrderItemDTO.vehicleModel + '</a>';
        html += '<br>报价价格：' + (quotedPreBuyOrderItemDTO.price == null ? '0' : quotedPreBuyOrderItemDTO.price) + '元/' + quotedPreBuyOrderItemDTO.unit + '（';
        if(quotedPreBuyOrderItemDTO.includingTax == 'TRUE') {
            html += '含税、';
        }
        if(!G.isEmpty(quotedPreBuyOrderItemDTO.shippingMethodStr)) {
            html += '送货上门、';
        }
        html += '下单后' + quotedPreBuyOrderItemDTO.arrivalTime + '天到货） ';
        html += '<a class="blue_color J_PurchaseOnline" data-productLocalInfoId="' + quotedPreBuyOrderItemDTO.productIdStr + '">直接下单</a></div>';
        html += '<div class="right"> <a href="preBuyOrder.do?method=showSupplierOtherQuotedItems&quotedPreBuyOrderItemId=' + quotedPreBuyOrderItemDTO.idStr + '"><span class="gray_color hover_yellow">（此卖家还给其他 <strong class="yellow_color">' + quotedPreBuyOrderItemDTO.countSupplierOtherQuoted + '</strong> 种商品报价了）</span></a></div>';
        html += '<div class="clear"></div></div>';
    }
    $(".accessoriesLeft .content").html($(html));
}

function purchaseProduct(productLocalInfoIds,amounts){
    APP_BCGOGO.Net.syncPost({
        url: "autoAccessoryOnline.do?method=validatorPurchaseProduct",
        dataType: "json",
        data: {
            productLocalInfoIds: productLocalInfoIds,
            amounts:amounts
        },
        success: function (json) {
            if (json.success) {
                window.location = "RFbuy.do?method=createPurchaseOrderOnlineByProduct&paramString=" + json.data.join(",");
            } else {
                nsDialog.jAlert(json.msg);
            }
        },
        error: function () {
            nsDialog.jAlert("数据异常!");
        }
    });
}

function addProductToShoppingCart(paramString){
    if(!G.Lang.isEmpty(paramString)){
        APP_BCGOGO.Net.syncPost({
            url: "shoppingCart.do?method=addProductToShoppingCart",
            dataType: "json",
            data: {
                paramString:paramString
            },
            success: function (json) {
                updateShoppingCartNumber();
                $("#resultMsg").text(json.data.resultMsg);
                $("#warnMsg").text(json.data.warnMsg);
                $("#shoppingCartItemCount").text(json.data.shoppingCartItemCount);
                $("#shoppingCartTotal").text(json.data.shoppingCartTotal);
                $("#goShoppingCartBtn").text(json.data.goShoppingCartBtnName);
                $("#returnDialog").dialog({
                    width: 333,
                    minHeight:140,
                    modal: true,
                    resizable:false,
                    position:'center',
                    open: function() {
                        $(".ui-dialog-titlebar", $(this).parent()).hide();
                        $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
                    }
                });
//                if(userGuide.currentStepName == "PRODUCT_PRICE_GUIDE_PURCHASE"){
//                    userGuide.funLib["PRODUCT_PRICE_GUIDE"]["_PRODUCT_PRICE_GUIDE_PURCHASE_STEP3_2"]();
//                }
            },
            error: function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    }
}
</script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="titBody">
        <!-- TODO上架信息请关注此处 -->
        <jsp:include page="supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="preBuyOrderManage"/>
        </jsp:include>
        <div class="added-management">
            <div class="product-details">
                <input id="preBuyOrderItemId" value="${preBuyOrderDTO.itemDTO.idStr}" type="hidden" />
                <div class="date-relative">
                    <div class="date-absolute" style="top:60px;">
                        <c:if test="${preBuyOrderDTO.statusStr =='过期'}" var="invalid">
                            <h2>商机已过期</h2>
                        </c:if>
                        <c:if test="${!invalid}">
                            <h1>距信息失效仅剩 ${preBuyOrderDTO.endDateCount}天 </h1>
                        </c:if>
                        截止：${preBuyOrderDTO.endDateStr} <br />
                        发布日期：${preBuyOrderDTO.vestDateStr} </div>
                </div>
                <div class="details-pic">
                    <img src="${preBuyOrderDTO.itemDTO.imageCenterDTO.productInfoBigImageDetailDTOs[0].imageURL}"/>
                </div>
                <div class="details-right">
                    <div class="font14"><strong>[<a class="yellow_color">求购</a>] ${preBuyOrderDTO.itemDTO.productName} ${preBuyOrderDTO.itemDTO.brand}</strong></div>
                    <div class="product-line">
                        <div class="line-01">商品编号：</div>
                        <div class="line-02">${preBuyOrderDTO.itemDTO.commodityCode}&nbsp;</div>
                    </div>
                    <div class="product-line">
                        <div class="line-01">品名：</div>
                        <div class="line-02">${preBuyOrderDTO.itemDTO.productName}&nbsp;</div>
                    </div>
                    <div class="product-line">
                        <div class="line-01">品牌：</div>
                        <div class="line-02">${preBuyOrderDTO.itemDTO.brand}&nbsp;</div>
                    </div>
                    <div class="product-line">
                        <div class="line-01">规格/型号：</div>
                        <div class="line-02">${preBuyOrderDTO.itemDTO.spec}&nbsp;${preBuyOrderDTO.itemDTO.model}</div>
                    </div>
                    <div class="product-line">
                        <div class="line-01">适合车型：</div>
                        <div class="line-02">${preBuyOrderDTO.itemDTO.vehicleBrand}&nbsp;${preBuyOrderDTO.itemDTO.vehicleModel}</div>
                    </div>
                    <div class="product-line">
                        <div class="line-01">求购数量：</div>
                        <div class="line-02">${preBuyOrderDTO.itemDTO.amount}&nbsp;${preBuyOrderDTO.itemDTO.unit}</div>
                    </div>
                    <div class="product-line">
                        <div class="line-01">描述：</div>
                        <div class="line-02">${empty preBuyOrderDTO.itemDTO.memo?"无":preBuyOrderDTO.itemDTO.memo}</div>
                        <div class="i_height clear"></div>
                    </div>
                    <div class="clear"></div>
                    <div class="black-color">浏览量 <a class="yellow_color"><strong>${numberUtil:roundInt(preBuyOrderDTO.itemDTO.viewedCount)}</strong></a> 次 | 已有 <a class="yellow_color"> <strong>${empty preBuyOrderDTO.itemDTO.quotedCount?"0":preBuyOrderDTO.itemDTO.quotedCount}</strong></a> 家卖家参与报价</div>
                </div>
                <div class="clear"></div>
            </div>
            <div class="accessoriesLeft" style="width:816px;">
                <div class="title" style="width:794px;"> 参与报价卖家 <span class="font12-normal">(</span> ${empty preBuyOrderDTO.itemDTO.quotedCount?"0":preBuyOrderDTO.itemDTO.quotedCount} <span class="font12-normal">家)</span></div>
                <div class="clear"></div>
                <div class="content">
                </div>
                <bcgogo:ajaxPaging url="preBuyOrder.do?method=getQuotedPreBuyOrders" dynamical="quotedPreBuyOrderInfo"
                                   data='{startPageNo:1,pageSize:5,preBuyOrderItemId:${preBuyOrderDTO.itemDTO.idStr}}' postFn="initQuotedPreBuyOrderInfo" display="none"/>
                <div class="clear"></div>
            </div>
            <div id="goodsRecommendFromNormal" class="goodsRecommend" style="width:814px; font-size:12px;display: none;">
                <div class="btitle" style="width:794px;"> 推荐配件</div>
                <div id="productFromNormalDiv"></div>
                <div class="clear"></div>
            </div>
            <div class="height"></div>

        </div>
    </div>
    <div id="mask" style="display:block;position: absolute;"> </div>
    <iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
            allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
    <!----------------------------页脚----------------------------------->
    <%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
<div class="alertCheck" id="returnDialog" style="display:none">
    <div class="alert_top"></div>
    <div class="alert_body">
        <div class="alertIcon">
            <a class="right"></a>
            <div class="line"><h3 id="resultMsg"></h3></div>
        </div>
        <div class="clear height"></div>
        <div class="line lines" id="warnMsg"></div>
        <div class="line lines">共有<b id="shoppingCartItemCount">0</b>种商品，合计：¥<b class="yellow_color" id="shoppingCartTotal">0</b></div>
        <div class="clear height"></div>
        <div class="button">
            <a class="btnHover" style="border:0px" id="goShoppingCartBtn">去购物车结算</a>
            <a class="blue_color" id="closeBtn">关闭</a>
        </div>
    </div>
    <div class="alert_bottom"></div>
</div>
</html>