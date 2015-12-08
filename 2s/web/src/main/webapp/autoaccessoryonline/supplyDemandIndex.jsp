<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-10-16
  Time: 下午5:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="jUtil" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>供求中心首页</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-sidebarListPanel<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <style type="text/css">
        .JScrollDiv {
            overflow: hidden;
            height:240px;
        }
    </style>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/supplyDemandIndex<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scrollFlow<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-sidebarListPanel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE");
        function toShopQuotedPreBuy(paramShopId,quotedPreBuyOrderItemId,productLocalId){
            window.location.href='shopProductDetail.do?method=toShopProductDetail&'+
                    'paramShopId='+paramShopId+'&productLocalId='+productLocalId+'&quotedPreBuyOrderItemId='+quotedPreBuyOrderItemId;
        }

        function toShowPreBuyOrder(preBuyOrderId){
            window.location.href='preBuyOrder.do?method=showPreBuyOrderById&preBuyOrderId='+preBuyOrderId;
        }

        $(function(){


            function _initProductsFromNormal(products) {
                var size =4;
                if (G.isEmpty(products)||products.length == 0) {
                    $("#productFromNormalDiv").append('<span style="margin-left: 10px;margin-top: 10px;color: #8D8D8D;">真遗憾，系统根据您的求购没有匹配到对应的配件信息！<a  href="preBuyOrder.do?method=createPreBuyOrder" class="blue_color">赶紧发布更多求购吧！</a></span>');
                    $("#productFromNormalDiv").height(40);
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
                        var inSalesPrice=Number(product.inSalesPrice).toFixed(1);
                        var inSalesPriceAfterCal=Number(product.inSalesPriceAfterCal).toFixed(1);
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
                                '<div class="goodList_info" style="height: 50px">'+
                                '<div style="text-align:left" class="p-info-detail h40" title="'+productInfo+'" onclick="toShopProductDetail(\''+shopIdStr+'\',\''+productLocalInfoIdStr+'\')">'+productInfoShort+'</div>';
                        if(product.hasBargain){
                            liHtml+='<span class="promotion-original-price" style="margin-right: 2px">¥'+inSalesPrice+'</span>';
                            liHtml+='<em>¥'+inSalesPriceAfterCal+'</em>';
                        }else{
                            liHtml+='<em>¥'+inSalesPrice+'</em>';
                        }
                        liHtml+= '</div>'+
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
            // 商品分类 sidebar
            var sidebarListPanel = new App.Module.SidebarListPanel();
            sidebarListPanel.init({
                selector:".sidebar-list-panel-group",
                data:JSON.parse('${thirdProductCategoryDTOsJson}'),
                onSelect:function(data, event) {//data中的数据肯定 同一个parent
                    var fieldStr="";
                    if(data && data.length>0){
                        for(var i=0;i<data.length;i++){
                            var productCategory=data[i];
                            fieldStr+='<input type="hidden" value="'+productCategory.idStr+'" name="productCategoryDTOs['+i+'].idStr"/>';
                            fieldStr+='<input type="hidden" value="'+productCategory.name+'" name="productCategoryDTOs['+i+'].name"/>';
                            fieldStr+='<input type="hidden" value="'+productCategory.parentIdStr+'" name="productCategoryDTOs['+i+'].parentIdStr"/>';
                            fieldStr+='<input type="hidden" value="'+productCategory.categoryType+'" name="productCategoryDTOs['+i+'].categoryType"/>';
                            fieldStr+='<input type="hidden" value="'+productCategory.secondCategoryName+'" name="productCategoryDTOs['+i+'].secondCategoryName"/>';
                            fieldStr+='<input type="hidden" value="'+productCategory.secondCategoryIdStr+'" name="productCategoryDTOs['+i+'].secondCategoryIdStr"/>';
                        }
                    }
                    $("#productCategory").children().remove();
                    $("#productCategory").append(fieldStr);
                    $("#searchQuotationBtn").trigger("click");

                }
            });
            <%--var productsFromNormalJson='${productsFromNormalJson}';--%>
            <%--if(!G.isEmpty(productsFromNormalJson)){--%>
            <%--var productsFromNormal=JSON.parse(productsFromNormalJson);--%>
            <%--_initProductsFromNormal(productsFromNormal);--%>
            <%--}--%>
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

            $(".J-initialCss").placeHolder();
            var loop,loop2;
            var itemCount = $('.my-pre .scrollItemDiv .my-pre-item').length;
            if(itemCount > 4) {
                loop = setInterval(autoScroll,2000);
            }
            $('.my-pre .scrollItemDiv').hover(function(){
                clearInterval(loop);
            },function(){
               if(itemCount > 4) {
                   loop = setInterval(autoScroll,2000);
               }
            });
            var itemIndex=G.rounding($("#latestQuotedPreBuyOrderItems tr:last").attr("itemIndex"));
            if(itemIndex > 9) {
                loop2 = setInterval(autoScroll2,2000);
            } else {
                $('.JScrollDiv').height(22*(itemIndex+1));
            }

            $(".JScrollDiv").hover(function(){
                clearInterval(loop2);
            },function(){
                if(itemIndex>9){
                    loop2 = setInterval(autoScroll2,2000);
                }
            });
        });

        function autoScroll(){
            var $scrollItem=$('.my-pre .scrollItemDiv');
            var intervalHeight=-60;
            $scrollItem.animate({
                marginTop:intervalHeight+"px"
            },1000,function(){
                var $myTwoItems = $(this).find(".my-pre-item:lt(2)");
                $myTwoItems.remove();
                $(this).append($myTwoItems);
                $(this).css({marginTop:"0px"});
            });
        }

        function autoScroll2(){
            var $scrollItem=$('.JScrollDiv .scrollItem');
            var intervalHeight=-20;
            $scrollItem.animate({
                marginTop:intervalHeight+"px"
            },1000,function(){
//                $(this).css({marginTop:"0px"});
                var $tr = $(this).find("tr:first");
                $tr.remove();
                $(this).find("table").append($tr);
                $(this).css({marginTop:"0px"});
//                .find("a:first").appendTo(this);
            });
        }
    </script>

    <bcgogo:hasPermission permissions="WEB.AD_SHOW">
        <script type="text/javascript" src="js/adShow<%=ConfigController.getBuildVersion()%>.js"></script>
    </bcgogo:hasPermission>

</head>

<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" id="pageType" value="supplyDemandIndex">

<div class="i_main clear">
<input type="hidden" id="productCategoryIds">
<input type="hidden" id="policy" value="${upYunFileDTO.policy}">
<input type="hidden" id="signature" value="${upYunFileDTO.signature}">
<div class="mainTitles">
    <div class="titleWords">供求中心</div>
    <div class="titleList">
        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE">
            <a class="click" href="supplyDemand.do?method=toSupplyDemand">供求首页</a>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE">
            <a href="autoAccessoryOnline.do?method=toCommodityQuotations">配件报价</a>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.BUYING_INFORMATION">
            <a href="preBuyOrder.do?method=preBuyInformation">求购资讯</a>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SEARCH.APPLY.SUPPLIER">
            <a href="apply.do?method=getApplySuppliersIndexPage">找供应商</a>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.RELEASE_PREBUYORDER">
            <a href="preBuyMain.do?method=toPreBuyMain">我要买配件</a>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.TXN.ORDER_CENTER.BASE">
            <a href="orderCenter.do?method=showOrderCenter">订单中心</a>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.SHOPPINGCART">
            <a href="shoppingCart.do?method=shoppingCartManage">购物车</a>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.BCGOGO_RECEIVABLE">
            <a href="bcgogoReceivable.do?method=bcgogoReceivableOrderList">一发供求专区</a>
        </bcgogo:hasPermission>

    </div>
</div>

<div class="titBody">
<div class="accessories-container supply-demand">
    <div class="content-sidebar parts-quoted-price fl ">
        <div class="sidebar-title sidebar-yellow-gradient"> 全部商品分类 </div>
        <div class="sidebar-list-panel-group">
            <div class="search-goods-div">
                <h1>按城市查找供应商 </h1>
                <form:form id="searchApplySupplierForm" action="apply.do?method=getApplySuppliersPage" method="post">
                    <div class="supply_border">
                        <input  type="text" class="txt search_word" placeholder="请选择城市"/>
                        <input name="area_code" type="hidden" />
                        <div id="searchApplySupplierBtn" class="i_search"></div>
                    </div>
                    <div id="productCategory"></div>
                </form:form>
            </div>
            <div class="search-goods-div">
                <h1>按分类找配件 </h1>
                <form:form commandName="searchConditionDTO" id="searchQuotationForm" action="autoAccessoryOnline.do?method=toCommodityQuotations" method="post">
                    <div class="supply_border">
                        <input id="searchWord" name="searchWord" type="text"  class="txt " placeholder="请输入关键字"/>
                        <div id="searchQuotationBtn" class="i_search"></div>
                    </div>
                    <div id="productCategory"></div>
                </form:form>
            </div>
            <!--使用js脚本初始化组件-->
        </div>
    </div>
    <div class="accessories-center" style="width: 605px;">
        <img src="../web/images/c_r2_c2.jpg"  />
        <div class="accessories-news">
            <strong class="yellow_color">最新数据：</strong>您正在求购的商品：<strong class="yellow_color">${allPreBuyOrderCount}</strong> 种 |
            您的求购共被浏览：<strong class="yellow_color">${totalViewedBusinessChance}</strong> 次 | 给您报价的：<strong class="yellow_color">${allQuotedCount}</strong> 条
        </div>
        <div class="buyGoods">
            <div class="btitle">您求购的商品</div>
            <%--<ul>--%>
            <div class="my-pre">
                <div class="scrollItemDiv">
                <c:choose>
                    <c:when test="${not empty preBuyOrderItems}">
                        <c:forEach items="${preBuyOrderItems}" var="itemDTO" varStatus="status">
                            <%--<li>--%>
                            <div class="my-pre-item">
                                ·
                                <span class="p-info-detail" onclick="toShowPreBuyOrder('${itemDTO.preBuyOrderId}')">
                                [<span class="p-type">求购</span>]<span class="text-overflow" title="${itemDTO.productInfo}" style="width:20px "> ${jUtil:getShortStr(itemDTO.productNameBrand,10)}</span>
                                    求购量：<span style="color: #FF6600;">${itemDTO.amount}</span> ${itemDTO.unit}
                            </span>
                                <span class="item-other-info">被浏览：${jUtil:roundInt(itemDTO.viewedCount)}次
                                    共有${jUtil:roundInt(itemDTO.quotedCount)}个商家报价</span>
                                    <%--</li>--%>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        暂无数据
                    </c:otherwise>
                </c:choose>
                </div>
                <div class="clear"></div>
            </div>
            <%--</ul>--%>
        </div>
    </div>
    <div class="clear"></div>
    <div id="goodsRecommendFromNormal" class="goodsRecommend">
        <div class="btitle"> 根据求购为我推荐</div>
        <div id="productFromNormalDiv"></div>
        <div class="clear"></div>
    </div>
    <div class="boutiqueAccessories">
        <div class="btitle"><a href="autoAccessoryOnline.do?method=toCommodityQuotations">更多>></a>精品配件</div>
        <ul id="productRecommendContainer" class="boutiqueList">
            <c:if test="${empty productsFromOther}">
                <li>
                    <span style="color: #5C5C5C;margin-left: -91px">您关联的供应商暂无上架商品</span>
                </li>
            </c:if>
            <c:if test="${not empty productsFromOther}">
                <c:forEach items="${productsFromOther}" var="product" varStatus="status">
                    <c:if test="${product!=null}">
                        <li>
                            <div class="goodList_pic">
                                <div class="goodList_pic_main" onclick="toShopProductDetail('${product.shopIdStr}','${product.productLocalInfoIdStr}')"><img src="${product.imageCenterDTO.recommendProductListImageDetailDTO.imageURL}"></div>
                                <c:if test="${product.promotionsDTOs!=null}">
                                    <c:choose>
                                        <c:when test="${product.hasBargain}">
                                            <div class="image-promotion-bargain-80X80">${product.promotionTypesShortStr}</div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="image-promotion-common-80X80">${product.promotionTypesShortStr}</div>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </div>
                            <div class="clear"></div>
                            <div class="goodList_info">
                                <div style="text-align:left" class="p-info-detail h30" onclick="toShopProductDetail('${product.shopIdStr}','${product.productLocalInfoIdStr}')">
                                        ${jUtil:getShortStr(product.productInfoStr,28)}
                                </div>
                                <c:choose>
                                    <c:when test="${product.hasBargain}">
                                        <span class="promotion-original-price">¥${product.inSalesPrice}</span>
                                        <em>¥${product.inSalesPriceAfterCal}</em>
                                    </c:when>
                                    <c:otherwise>
                                        <em>¥${product.inSalesPrice}</em>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="clear"></div>
                            <div class="goodsButton">
                                <input type="button" value="加入购物车" productId="${product.productLocalInfoIdStr}" supplierShopId="${product.shopIdStr}" class="J-addToShoppingCartSingleBtn goodsButton_sub"/>
                                <input type="button" value="立即采购" productId="${product.productLocalInfoIdStr}" class="J-purchaseSingleBtn goodsButton_sub"/>
                            </div>
                        </li>
                    </c:if>
                </c:forEach>
            </c:if>
        </ul>
        <div class="clear"></div>
    </div>

    <div id="recommendShopDiv" class="accessoriesLeft" style="display: none">
        <div class="title"><a href="apply.do?method=getApplySuppliersIndexPage">更多>></a> 推荐供应商</div>
        <div class="content recommend-shop-div" style="height: 400px"></div>
        <div class="clear"></div>
    </div>
</div>
<div class="accessories-right">
    <div class="accessoriesRight">
        <div class="title">我要买配件</div>
        <div class="clear"></div>
        <div class="content">
            <a class="button-label" href="preBuyOrder.do?method=createPreBuyOrder">发布求购</a>
            <a class="button-label" href="preBuyOrder.do?method=preBuyOrderManage">我的求购</a>
            <a class="button-label" href="shoppingCart.do?method=shoppingCartManage">购物车</a>
            <a class="button-label" href="orderCenter.do?method=showOrderCenter">订单中心</a>
            <div class="clear"></div>
            <div class="lineHeight30"></div>
        </div>
    </div>
    <c:if test="${not empty promotingProducts}">
        <div class="accessoriesRight">
            <div class="title"><a href="autoAccessoryOnline.do?method=toCommodityQuotations&fromSource=allPromotion">更多>></a>正在促销</div>
            <div class="clear"></div>
            <div class="content">
                <c:forEach items="${promotingProducts}" var="product" varStatus="status">
                    <c:if test="${product!=null}">
                        <div class="clients quality-goods">
                            <div class="pic" onclick="toShopProductDetail('${product.shopIdStr}','${product.productLocalInfoIdStr}')">
                                <img src="${product.imageCenterDTO.productListSmallImageDetailDTO.imageURL}">
                            </div>
                            <c:if test="${product.promotionsDTOs!=null}">
                                <c:choose>
                                    <c:when test="${product.hasBargain}">
                                        <div class="image-promotion-bargain-60X60">${product.promotionTypesShortStr}</div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="image-promotion-common-60X60">${product.promotionTypesShortStr}</div>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            <div class="right">
                                <div class="p-info-detail h47" title="${product.productInfoStr}" onclick="toShopProductDetail('${product.shopIdStr}','${product.productLocalInfoIdStr}')">
                                        ${jUtil:getShortStr(product.productInfoStr,28)}
                                </div>
                                <c:choose>
                                    <c:when test="${product.hasBargain}">
                                        <span class="promotion-original-price">¥${product.inSalesPrice}</span>
                                        <em>¥${product.inSalesPriceAfterCal}</em>
                                    </c:when>
                                    <c:otherwise>
                                        <em>¥${product.inSalesPrice}</em>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="clear"></div>
                        </div>
                    </c:if>
                </c:forEach>
                <div class="clear"></div>
            </div>
        </div>
    </c:if>
    <c:if test="${not empty latestQuotedPreBuyOrderItems}">
        <div class="accessoriesRight">
            <div class="title">最新配件报价</div>
            <div class="clear"></div>
            <div class="content">
                <table style='width: 190px' width="190" border="0" cellspacing="0" class="ul-04">
                    <tr style="font-weight: bold;">
                        <td>配件信息</td>
                        <td>价格</td>
                    </tr>
                </table>
                <div class="JScrollDiv">
                <div class="scrollItem">
                <table style='width: 190px' width="190" border="0" cellspacing="0" class="ul-04" id="latestQuotedPreBuyOrderItems">
                    <c:forEach items="${latestQuotedPreBuyOrderItems}" var="item" varStatus="status">
                        <tr onclick="toShopQuotedPreBuy('${item.shopIdStr}','${item.idStr}','${item.productIdStr}')"
                            title="${item.productName} ${item.brand} ${item.vehicleBrand} ${item.vehicleModel}" itemIndex="${status.index}">
                            <td >
                                <span class="p-info-detail" title="${item.productInfo}">${jUtil:getShortStr(item.productInfo,10)}</span>
                            </td>
                            <td width="62px"><a class="yellow_color goods-price"><em style="font-size: 11px">¥${item.price}</em></a></td>
                        </tr>
                    </c:forEach>
                </table>
                </div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </c:if>
    <c:if test="${not empty lastInSalesProductDTOs}">
        <div class="accessoriesRight">
            <div class="title"><a href="autoAccessoryOnline.do?method=toCommodityQuotations">更多>></a>最新上架配件</div>
            <div class="clear"></div>
            <div class="content">
                <ul class="ul-03 J-insaling-product">
                    <c:forEach items="${lastInSalesProductDTOs}" var="product" varStatus="status">
                        <c:if test="${product!=null}">
                            <li>
                                <div class="goodList_pic">
                                    <div class="goodList_pic_main" onclick="toShopProductDetail('${product.shopIdStr}','${product.productLocalInfoIdStr}')">
                                        <img src="${product.imageCenterDTO.recommendProductListImageDetailDTO.imageURL}">
                                    </div>
                                    <c:if test="${product.promotionsDTOs!=null}">
                                        <c:choose>
                                            <c:when test="${product.hasBargain}">
                                                <div class="image-promotion-bargain-80X80">${product.promotionTypesShortStr}</div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="image-promotion-common-80X80">${product.promotionTypesShortStr}</div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </div>
                                <div class="goodList_info accessoriesList_info">
                                    <div class="p-info-detail h40" title="${product.productInfoStr}" onclick="toShopProductDetail('${product.shopIdStr}','${product.productLocalInfoIdStr}')">${jUtil:getShortStr(product.productInfoStr,32)}</div>
                                    <c:choose>
                                        <c:when test="${product.hasBargain}">
                                            <span class="promotion-original-price">¥${jUtil:round(product.inSalesPrice)}</span>
                                            <em>¥${jUtil:round(product.inSalesPriceAfterCal)}</em>
                                        </c:when>
                                        <c:otherwise>
                                            <em>¥${jUtil:round(product.inSalesPrice)}</em>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="clear"></div>
                                <div class="goodsButton">
                                    <input productId="${product.productLocalInfoIdStr}" supplierShopId="${product.shopIdStr}" class="goodsButton_sub J-addToShoppingCartSingleBtn" type="button" value="加入购物车">
                                    <input productId="${product.productLocalInfoIdStr}" class="goodsButton_sub J-purchaseSingleBtn" type="button" value="立即采购">
                                </div>
                            </li>
                        </c:if>
                    </c:forEach>
                </ul>
                <div class="clear"></div>
            </div>
        </div>
    </c:if>

    <div class="accessoriesRight">
        <div class="title">您最近浏览的商品</div>
        <div class="clear"></div>
        <div class="content">
            <c:if test="${empty recentlyViewedProductDTOList}">
                <span style="margin-left: 5px">您暂无浏览过的配件商品！</span>
            </c:if>
            <c:if test="${not empty recentlyViewedProductDTOList}">
                <c:forEach items="${recentlyViewedProductDTOList}" var="product" varStatus="status">
                    <c:if test="${product!=null}">
                        <div class="clients quality-goods">
                            <div class="pic" onclick="toShopProductDetail('${product.shopIdStr}','${product.productLocalInfoIdStr}')">
                                <img src="${product.imageCenterDTO.productListSmallImageDetailDTO.imageURL}">
                            </div>
                            <c:if test="${product.promotionsDTOs!=null}">
                                <c:choose>
                                    <c:when test="${product.hasBargain}">
                                        <div class="image-promotion-bargain-60X60">${product.promotionTypesShortStr}</div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="image-promotion-common-60X60">${product.promotionTypesShortStr}</div>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            <div class="right">
                                <div class="p-info-detail h47" title="${product.productInfoStr}" onclick="toShopProductDetail('${product.shopIdStr}','${product.productLocalInfoIdStr}')">${jUtil:getShortStr(product.productInfoStr,25)}</div>
                                <c:choose>
                                    <c:when test="${product.hasBargain}">
                                        <span class="promotion-original-price">¥${product.inSalesPrice}</span>
                                        <em>¥${product.inSalesPriceAfterCal}</em>
                                    </c:when>
                                    <c:otherwise>
                                        <em>¥${product.inSalesPrice}</em>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="clear"></div>
                        </div>
                    </c:if>
                </c:forEach>
            </c:if>

            <div class="clear"></div>
        </div>
    </div>
</div>
</div>
</div>

<jsp:include page="alert/areaSelector.jsp" />

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
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
