<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-10-15
  Time: 下午6:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.bcgogo.enums.OrderStatus" %>
<%@ page import="com.bcgogo.utils.DateUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="jUtil" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>供求中心首页</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-sidebarListPanel<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css">

    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/page/autoaccessoryonline/supplyDemandIndex<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/quotedPreBuyOrderAlert<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/addNewProduct<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scrollFlow<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-sidebarListPanel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE_ORDER_CENTER");
        $(function(){

            APP_BCGOGO.Net.asyncAjax({
                url: "supplyDemand.do?method=getInSalingProductForSupplyDemand",
                type: "POST",
                cache: false,
                dataType: "json",
                success: function (inSalingProducts) {
                    if (G.isEmpty(inSalingProducts)) {
                         $('.J-insaling-product').append('<div style="margin-left: 2px">您暂无上架商品！</div>');
                        return;
                    }
                    var trStr='';
                    for(var i=0;i<inSalingProducts.length;i++){
                        var product=inSalingProducts[i];
                        var shopIdStr=product.shopIdStr;
                        var productLocalInfoIdStr=product.productLocalInfoIdStr;
                        var productInfo =G.normalize(product.productInfo);
                        var productInfoShort=productInfo.length>28?productInfo.substr(0,28):productInfo;
                        var isPromoting=!G.isEmpty(product.promotionsDTOs);
                        var hasBargain= product.hasBargain;
                        var promotionTypesShortStr=product.promotionTypesShortStr;
                        var inSalesPrice=G.rounding(product.inSalesPrice);
                        var viewedCount=G.rounding(product.viewedCount,0);
                        var imageURL=product.imageCenterDTO.recommendProductListImageDetailDTO.imageURL
                        trStr+='<li>'+
                                '<div class="goodList_pic">'+
                                '<div class="goodList_pic_main" onclick="toShopProductDetail(\''+shopIdStr+'\',\''+productLocalInfoIdStr+'\')"><img src="'+imageURL+'"></div>';
                        if(isPromoting){
                            if(hasBargain){
                                trStr+='<div class="image-promotion-bargain-80X80">'+promotionTypesShortStr+'</div>';
                            }else{
                                trStr+='<div class="image-promotion-common-80X80">'+promotionTypesShortStr+'</div>';
                            }
                        }
//                       trStr+= '<div class="goodList_pic_delivery">送货上门</div>'+
                        trStr+= '</div>'+
                                '<div class="goodList_info accessoriesList_info">'+
                                '<p class="p-info-detail h40" onclick="toShopProductDetail(\''+shopIdStr+'\',\''+productLocalInfoIdStr+'\')">'+productInfoShort+'</p>'+
                                '<em>¥'+inSalesPrice+'</em>'+
                                '<p>浏览量<span class="yellow_color">'+viewedCount+'</span>次</p>'+
                                '</div>'+
                                '<div class="clear"></div>'+
                                '</li>';
                    }
                    $('.J-insaling-product').append(trStr);
                },
                error:function(){
                    nsDialog.jAlert("网络异常。");
                }
            });
            APP_BCGOGO.Net.asyncAjax({
                url: "autoAccessoryOnline.do?method=getViewedBusinessChance",
                type: "POST",
                cache: false,
                dataType: "json",
                success: function (result) {
                    if(G.isEmpty(result)||!result.success){
                        return;
                    }
                    var preBuyOrderItems=result.data;
                    if(G.isEmpty(preBuyOrderItems)){
                        $('#viewedBusinessChance').append("<li>您暂无最近浏览的商机</li>");
                        return;
                    }
                    var trStr='';
                    for(var i=0;i<preBuyOrderItems.length;i++){
                        var preBuyOrderItem=preBuyOrderItems[i];
                        var commodityCode=preBuyOrderItem.commodityCode;
                        var productName=preBuyOrderItem.productName;
                        var brand=preBuyOrderItem.brand;
                        var model=preBuyOrderItem.model;
                        var spec=preBuyOrderItem.spec;
                        var vehicleBrand=preBuyOrderItem.vehicleBrand;
                        var vehicleModel=preBuyOrderItem.vehicleModel;
                        var productInfoStr = "";
//                        if(!G.isEmpty(commodityCode)){
//                            productInfoStr += commodityCode + " ";
//                        }
                        if(!G.isEmpty(productName)){
                            productInfoStr += productName + " ";
                        }
                        if(!G.isEmpty(brand)){
                            productInfoStr += brand + " ";
                        }
                        if (!G.isEmpty(model) &&!G.isEmpty(spec)) {
                            productInfoStr += model + "/" + spec;
                        } else if (!G.isEmpty(model)) {
                            productInfoStr += model;
                        } else if (!G.isEmpty(spec)) {
                            productInfoStr += spec;
                        }
                        if(!G.isEmpty(vehicleBrand)){
                            productInfoStr += vehicleBrand + " ";
                        }
                        if(!G.isEmpty(vehicleModel)){
                            productInfoStr += vehicleModel;
                        }
                        var businessChanceTypeStr = preBuyOrderItem.businessChanceTypeStr;
                        var businessChanceType = preBuyOrderItem.businessChanceType;
                        var changeClass='';
                        switch(businessChanceType){
                            case 'Normal':
                                changeClass='yellow_color';
                                break;
                            case 'SellWell':
                                changeClass='green_color';
                                break;
                            case 'Lack':
                                changeClass='red_color';
                                break;
                            default:
                                changeClass='yellow_color'
                        }
                        var itemId=preBuyOrderItem.idStr;
                        trStr+='<li class="p-info-detail text-overflow" onclick="showBuyInformationDetail(\''+itemId+'\')" title="'+productInfoStr+'">[<a class="'+changeClass+'">'+businessChanceTypeStr+'</a>]'+productInfoStr+'</li>';
                    }
                    $('#viewedBusinessChance').append(trStr);
                },
                error:function(){
                    nsDialog.jAlert("网络异常。");
                }
            });
             $(".J-initialCss").placeHolder();
        });


        $().ready(function() {
            // sidebar
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

        });
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" id="pageType" value="supplyDemandWholesalerIndex">
<input type="hidden" id="policy" value="${upYunFileDTO.policy}">
<input type="hidden" id="signature" value="${upYunFileDTO.signature}">
<input type="hidden" id="shopId" value="${sessionScope.shopId}"/>
<div class="i_main clear">
<div class="mainTitles">
    <div class="titleWords">供求中心</div>
    <div class="titleList">
        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE">
            <a class="click" href="supplyDemand.do?method=toSupplyDemand">首页</a>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE">
            <a href="autoAccessoryOnline.do?method=toCommodityQuotations">配件报价</a>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.BUYING_INFORMATION">
            <a href="preBuyOrder.do?method=preBuyInformation">求购资讯</a>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SEARCH.APPLY.CUSTOMER">
            <a href="apply.do?method=getApplyCustomersIndexPage">找客户</a>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.GOODS_IN_OFF_SALES_MANAGE">
            <a href="preSellMain.do?method=toPreSellMain">我要卖配件</a>
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
    <div class="accessories-container">
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
            <img src="../web/images/banner2.jpg"  />
            <div class="accessories-news"> <strong class="yellow_color">最新数据：</strong>
                您上架的商品共：<strong class="yellow_color">${inSalesProductNum}</strong> 种 |
                您的商品共被浏览：<strong class="yellow_color">${viewedProductNum}</strong> 次 |
                给您下单的：<strong class="yellow_color">${ordersFromQuotedPreBuyOrderCount}</strong> 条 </div>
            <div id="lastWeekStatDiv" class="JScrollFlowHorizontal accessories-round">
            </div>
        </div>
        <div class="clear"></div>
        <c:if test="${not empty latestPreBuyOrderItems}">
            <div class="accessoriesLeft">
                <div class="title"><a onclick="preBuyInformation()">更多>></a> 最新求购</div>
                <div class="content">
                    <table width="100%" border="0" class="accessories-table lastest-preBuy-table">
                        <c:forEach items="${latestPreBuyOrderItems}" var="preBuyOrderItem" varStatus="status">
                            <c:if test="${preBuyOrderItem!=null}">
                                <tr>
                                    <td>
                                        <input type="hidden" class="commodityCode" value="${preBuyOrderItem.commodityCode}"/>
                                        <input type="hidden" class="productName" value="${preBuyOrderItem.productName}"/>
                                        <input type="hidden" class="brand" value="${preBuyOrderItem.brand}"/>
                                        <input type="hidden" class="spec" value="${preBuyOrderItem.spec}"/>
                                        <input type="hidden" class="model" value="${preBuyOrderItem.model}"/>
                                        <input type="hidden" class="vehicleBrand" value="${preBuyOrderItem.vehicleBrand}"/>
                                        <input type="hidden" class="vehicleModel" value="${preBuyOrderItem.vehicleModel}"/>
                                        <div style="width: 220px" onclick="showBuyInformationDetail('${preBuyOrderItem.idStr}')" class="p-info-detail text-overflow" title="${preBuyOrderItem.productInfo}">[<a class="yellow_color">${preBuyOrderItem.businessChanceTypeStr}</a>] ${preBuyOrderItem.productInfo}</div>
                                    </td>
                                    <td class="p-info-detail" onclick="preBuyInformationByShop('${preBuyOrderItem.shopName}')">${preBuyOrderItem.shopName}</td>
                                    <td>${preBuyOrderItem.shopAreaInfo}</td>
                                    <td>${preBuyOrderItem.endDateStr}截止</td>
                                    <c:if test="${preBuyOrderItem.myQuoted}">
                                        <td><div  class="accessories-btn" style="background: #ddd">我已报价</div></td>
                                    </c:if>
                                    <c:if test="${!preBuyOrderItem.myQuoted}">
                                        <td><div data-itemId="${preBuyOrderItem.idStr}" data-shopId="${preBuyOrderItem.shopIdStr}" class="accessories-btn J_doQuoted">我要报价</div></td>
                                    </c:if>
                                </tr>
                            </c:if>
                        </c:forEach>
                    </table>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </c:if>
        <div class="accessoriesLeft preBuyItemRecommendContainer" style="display: none;">
            <div class="title"><a onclick="preBuyInformation()">更多>></a> 求购资讯</div>
            <div id="preBuyItemRecommendDiv" class="preBuy-item-recommend content JScrollFlowHorizontal" style="float: left;">
                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </div>
        <div id="recommendShopDiv" class="accessoriesLeft" style="display: none">
            <div class="title"><a href="apply.do?method=getApplyCustomersIndexPage">更多>></a> 推荐客户</div>
            <div class="content recommend-shop-div" style="height: 370px">
                <%--<div class="clients">--%>
                <%--<div class="pic"></div>--%>
                <%--<div class="right">--%>
                <%--<div class="right_h1"><a class="blue_color">苏州新宇商贸</a></div>--%>
                <%--<div>江苏省苏州市工业园区</div>--%>
                <%--<div class="red-bg">已认证</div>--%>
                <%--</div>--%>
                <%--<div class="clear"></div>--%>
                <%--经营范围：<span class="gray_color">欧曼重卡系列配件，平衡轴系列奔驰桥系列 轮边总成系列 制动器系列</span>--%>
                <%--</div>--%>
                <%--<div class="clients">--%>
                <%--<div class="pic"></div>--%>
                <%--<div class="right">--%>
                <%--<div class="right_h1"><a class="blue_color">苏州新宇商贸</a></div>--%>
                <%--<div>江苏省苏州市工业园区</div>--%>
                <%--<div class="red-bg2">--%>
                <%--未认证--%>
                <%--</div>--%>
                <%--</div>--%>
                <%--<div class="clear"></div>--%>
                <%--经营范围：<span class="gray_color">欧曼重卡系列配件，平衡轴系列奔驰桥系列 轮边总成系列 制动器系列</span>--%>

                <%--</div>--%>

                <%--<div class="clear"></div>--%>
            </div>
            <div class="clear"></div>
        </div>
    </div>
    <div class="accessories-right">
        <div class="accessoriesRight">
            <div class="title">我要卖配件</div>
            <div class="clear"></div>
            <div class="content">
                <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.GOODS_IN_OFF_SALES_MANAGE">
                    <a class="button-label" href="goodsInOffSales.do?method=toUnInSalingGoodsList">我要上架</a>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.PROMOTIONS_MANAGER_RENDER">
                    <a class="button-label" href="promotions.do?method=toPromotionsList">我要促销</a>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.MY_QUOTEDPREBUYORDER">
                    <a class="button-label" href="preBuyOrder.do?method=quotedPreBuyOrderManage">我的报价</a>
                </bcgogo:hasPermission>
                <a class="button-label" href="orderCenter.do?method=showOrderCenter">订单中心</a>
                <div class="clear"></div>
                <div class="lineHeight30">在线销售订单：共<a class="blue_color number" <c:if test="${orderCenterDTO.saleNew<=0}"> onclick="javascript:return false;"</c:if> href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=inProgress&currTab=NEW">${orderCenterDTO.saleNew}</a> 条新订单<br />
                    在线销售退货：共<a class="blue_color number" <c:if test="${orderCenterDTO.saleReturnNew<=0}"> onclick="javascript:return false;"</c:if> href="onlineSalesReturnOrder.do?method=toOnlineSalesReturnOrder&orderStatus=PENDING">${orderCenterDTO.saleReturnNew}</a> 条待处理</div>
            </div>
        </div>
        <div class="accessoriesRight">
            <div class="title"><a class="blue_color" onclick="toInSalingGoodsList()">更多>></a>我的上架商品</div>
            <div class="clear"></div>
            <div class="content">
                <ul class="ul-03 J-insaling-product">
                    <div class="clear"></div>
                </ul>
            </div>
        </div>
        <c:if test="${not empty wholesalerProductDTOs}">
            <div class="accessoriesRight">
                <div class="title"><a class="blue_color" onclick="toCommodityQuotations()">更多>></a>精品配件</div>
                <div class="clear"></div>
                <div class="content">
                    <c:forEach items="${wholesalerProductDTOs}" var="product" varStatus="status">
                        <c:if test="${product.productInfoStr!=null}">
                            <div class="clients quality-goods">
                                <div class="pic" onclick="toShopProductDetail('${product.shopIdStr}','${product.productLocalInfoIdStr}')">
                                    <img src="${product.imageCenterDTO.productListSmallImageDetailDTO.imageURL}">
                                </div>
                                <div class="right">
                                    <div class="p-info-detail h47" title="${product.productInfoStr}" onclick="toShopProductDetail('${product.shopIdStr}','${product.productLocalInfoIdStr}')">${jUtil:getShortStr(product.productInfoStr,25)}</div>
                                    <em>¥${product.inSalesPrice}</em>
                                </div>
                                <div class="clear"></div>
                            </div>
                        </c:if>
                    </c:forEach>
                    <div class="clear"></div>
                </div>
            </div>
        </c:if>
        <div class="accessoriesRight">
            <div class="title">您最近浏览过的商机</div>
            <div class="clear"></div>
            <div class="content">
                <ul class="ul-04" id="viewedBusinessChance">
                </ul>
                <div class="clear"></div>
            </div>
        </div>
    </div>
</div>
<div class="height"></div>
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
<%@ include file="./alert/quotedPreBuyOrderAlert.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
