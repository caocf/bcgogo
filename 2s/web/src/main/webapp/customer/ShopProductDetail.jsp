<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>店铺资料</title>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/shopMsgDetail<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/zTreeStyle<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
<link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/spinner/ui.spinner.css"/>

<%@include file="/WEB-INF/views/header_script.jsp" %>
<%@include file="/WEB-INF/views/image_script.jsp" %>
<script type="text/javascript" src="js/shopProductDetail<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/shopMsgDetail<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.ztree.core-3.5.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/spinner/ui.spinner.min.js"></script>

<script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.config<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.all<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" charset="utf-8" src="js/extension/ueditor/ueditor.parse<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid, "APPLY_GET_APPLY_SUPPLIERS");
    defaultStorage.setItem(storageKey.MenuCurrentItem,"商品详情");

    var selfShopId = '${sessionScope.shopId}';
    uParse('#productDescription');
    $(document).ready(function(){
        function saveRecentlyViewedProduct(viewedProductLocalInfoId, productShopId){
            if(G.isEmpty(viewedProductLocalInfoId)){
                return;
            }
            APP_BCGOGO.Net.asyncPost({
                url: "autoAccessoryOnline.do?method=saveRecentlyViewedProduct",
                dataType: "json",
                data: {
                    viewedProductLocalInfoId: viewedProductLocalInfoId,
                    productShopId: productShopId
                }
            });
        }

        saveRecentlyViewedProduct($("#productLocalInfoId").val(), $('#productShopId').val());

        initProductCategoryTree();
    });

</script>

</head>

<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" id="promotionContent" value="${product.promotionContent}"/>
<input type="hidden" id="promotionTitleShort" value="${product.promotionTypesShortStr}"/>
<input type="hidden" id="promotionTitle" value="${product.promotionTypesStr}"/>
<input type="hidden" id="fromSource" value="${fromSource}"/>
<input type="hidden" id="pageType" value="shopProductDetail"/>
<div class="i_main clear">
<div class="clear i_height"></div>
<div class="titBody">
<%--<title --%>
<div class="store_top">
    <div class="storeName">
        <span>${shopDTO.name}</span>
        <span class="storeAddress">${shopDTO.areaName}</span>
    </div>
    <div class="storeManu">
        <a href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${paramShopId}" target="_self">店铺介绍</a>
        <a href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${paramShopId}&shopMsgTabFlag=comment" target="_self">店铺评价</a>
        <a href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${paramShopId}&shopMsgTabFlag=productList" target="_self">商品列表</a>
    </div>
</div>
<%--left content--%>
<div class="store_left">
    <div class="store_kind">
        <span class="store_title">店铺信息
            <c:if test="${sessionScope.shopId == paramShopId}">
                <a href="shopData.do?method=toManageShopData" class="blue_color shop-data-editor" style="font-size: 12;float: right;">修改本店资料</a>
            </c:if>
        </span>

        <div class="shop store_list">
            <div class="title"><b>${shopDTO.name}</b></div>
            <div class="height"></div>
            <div class="divStar">
                综合评分：<span class="picStar" onclick="window.location='shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${paramShopId}&shopMsgTabFlag=comment'"
                           style=" background-position: 0px -${supplierCommentStatDTO.totalScoreSpan}px;"></span>
                <span class="yellow_color">
                    <c:choose>
                        <c:when test="${supplierCommentStatDTO.totalScore == 0}">
                            暂无分数
                        </c:when>
                        <c:otherwise>
                            ${supplierCommentStatDTO.totalScore} 分
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
            <c:if test="${isQQExist eq true}">
                <div class="store_connecter"><b>在线咨询</b></div>
                <c:choose>
                    <c:when test="${empty shopDTO.contacts}">
                        暂无联系方式
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${shopDTO.contacts}" var="contact">
                            <c:if test="${not empty contact && not empty contact.qq}">
                                <div class="store_connecter"><span class="name">${contact.name}</span>
                                    <a href="http://wpa.qq.com/msgrd?v=3&uin=${contact.qq}&site=qq&menu=yes" target="_blank">
                                        <img src="http://wpa.qq.com/pa?p=2:${contact.qq}:41"/>
                                    </a>
                                </div>
                            </c:if>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <div class="store_connecter">入驻日期：<span>${shopDTO.registrationDateStr}</span></div>
            <div class="store_connecter">商品数量：<a class="blue_color" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${paramShopId}&shopMsgTabFlag=productList"><b>${shopDTO.totalProductCount}</b></a>种</div>
            <div class="store_connecter">
                <div>认证情况
                ：</div>
                <c:choose>
                    <c:when test="${shopDTO.licensed}">
                        <img src="images/license.png"/>
                    </c:when>
                    <c:otherwise>
                        <img src="images/unlicense.png"/>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="store_connecter">收藏人气：已有<b>${shopDTO.beStored}</b>家</div>
            <div class="height"></div>
            <c:if test="${shopDTO.id != sessionScope.shopId}">
                <div id="relationStatusDiv" style="float:left;width:100%;">
                    <c:choose>
                        <c:when test="${relationStatus=='UN_APPLY_RELATED'}">
                            <c:choose>
                                <c:when test="${relateFlag eq 'relatedAsCustomer'}">
                                    <a class="search_button J_applyRelation" url="apply.do?method=applySupplierRelation&supplerShopId=${paramShopId}">申请关联</a>
                                </c:when>
                                <c:otherwise>
                                    <a class="search_button J_applyRelation" url="apply.do?method=applyCustomerRelation&customerShopId=${paramShopId}">申请关联</a>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:when test="${relationStatus=='APPLY_RELATED'}">
                            <a class="search_button" href="javascript:void(0);">已提交关联申请</a>
                        </c:when>
                        <c:when test="${relationStatus=='RELATED'}">
                            <a class="search_button" href="javascript:void(0);">已经关联</a>
                        </c:when>
                        <c:when test="${relationStatus=='BE_APPLY_RELATED'}">
                            <div class="txt-box-grey-center">对方已经向本店提交关联申请</div>
                            <c:if test="${inviteDTO.inviteType=='CUSTOMER_INVITE'}">
                                <a class="search_button J_supplier_accept" applyinvitestatus="pendingCustomer" originshopname="${shopDTO.name}" inviteid="${inviteDTO.id}" href="#">同意关联</a>
                            </c:if>
                            <c:if test="${inviteDTO.inviteType=='SUPPLIER_INVITE'}">
                                <a class="search_button J_customer_accept" applyinvitestatus="pendingSupplier" originshopname="${shopDTO.name}" inviteid="${inviteDTO.id}" href="#">同意关联</a>
                            </c:if>
                        </c:when>
                    </c:choose>
                </div>
            </c:if>
        </div>
    </div>
    <div class="store_kind">
        <span class="store_title">产品搜索</span>
        <form method="post" id="productSimpleSearchForm" action="shopMsgDetail.do?method=simpleSearch">
        <input type="hidden" id="paramShopId" name="paramShopId" value="${paramShopId}"/>
        <div class="shop store_list store_search">
            <div class="store_connecter">
                <span class="name">关键词</span>
                <input type="text" class="txt" id="productSimpleSearchField" name="searchWord"   placeHolder="品名/品牌/规格/型号/车型" style="width:150px;"/>
            </div>
            <div class="store_connecter">
                <span class="name">价格</span>
                <input type="text" class="txt" name="tradePriceStart" style="width:60px;"/>&nbsp;至&nbsp;<input type="text" class="txt"
                                                                                      name="tradePriceEnd" style="width:60px;"/>
            </div>
            <a class="search_button" id="productQuickSearchBtn">搜&nbsp;索</a>
        </div>
        </form>
    </div>
    <div class="store_kind">
        <span class="store_title">上架商品分类</span>

        <div class="shop store_list store_kindList">
            <ul id="productCategoryTree" class="ztree"></ul>
        </div>
    </div>
</div>

<div class="store_right">

    <div class="tab-content product-details">
        <input type="hidden" id="productLocalInfoId" value="${product.productLocalInfoId}"/>
        <input type="hidden" id="productShopId" value="${product.shopId}"/>
        <div class="group-content product-control">
            <div class="content-details">
                <div class="details-img fl">
                    <c:choose>
                        <c:when test="${empty product.imageCenterDTO.productInfoBigImageDetailDTOs}">
                            <div class="img-original">
                                <img src="${notFindImageURL_200X200}" alt=""/>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="img-original">
                                <c:forEach var="productInfoBigImageDetailDTO" items="${product.imageCenterDTO.productInfoBigImageDetailDTOs}" varStatus="status">
                                    <img src="${productInfoBigImageDetailDTO.imageURL}" style="display: ${status.index==0?'block':'none'}" alt="" class="J_bigImageSwitch" id="bigImageSwitch_${status.index}"/>
                                </c:forEach>
                            </div>
                            <div class="group-img-thumbnails">
                                <c:forEach var="productInfoSmallImageDetailDTO" items="${product.imageCenterDTO.productInfoSmallImageDetailDTOs}" varStatus="status">
                                    <div class="img-thumbnails-item fl ${status.index==0?'actived':''}">
                                        <div class="item-arrow"></div>
                                        <img src="${productInfoSmallImageDetailDTO.imageURL}" style="cursor: pointer" alt="" class="J_smallImageSwitch" data-index="${status.index}"/>
                                    </div>
                                </c:forEach>
                                <div class="cl"></div>
                            </div>
                        </c:otherwise>
                    </c:choose>


                </div>
                <div class="details-info fl">
                    <div class="info-title">${product.productInfoStr}</div>
                    <div class="info-item info-price">
                        <span class="info-label">价格：</span>
                        <c:choose>
                            <c:when test="${product.hasBargain}">
                                <span class="promotion-original-price">¥${product.inSalesPrice}</span>
                                <span class="info-content">¥${product.inSalesPriceAfterCal}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="info-content">¥${product.inSalesPrice}</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <c:if test="${not empty quotedPreBuyOrderItemDTO}">
                        <div class="info-off-the-shelf" style="font-size: 12px;">
                            推荐价格：
                        <span class="red_color">
                        <strong>¥${quotedPreBuyOrderItemDTO.price == null ? "0" : quotedPreBuyOrderItemDTO.price}</strong>
                        </span>
                            <span class="gray_color">（<c:if test="${quotedPreBuyOrderItemDTO.includingTax == 'TRUE'}">含税、</c:if><c:if test="${not empty quotedPreBuyOrderItemDTO.shippingMethodStr}">${quotedPreBuyOrderItemDTO.shippingMethodStr}、</c:if>下单后<c:if test="${not empty quotedPreBuyOrderItemDTO.arrivalTime}">${quotedPreBuyOrderItemDTO.arrivalTime}</c:if>天到货）</span>
                            <p>
                                您现在查看的是卖家给您的
                                <a class="red_color">推荐价格</a>
                            </p>
                            <br>
                            <a class="blue_color" href="shopProductDetail.do?method=toShopProductDetail&paramShopId=${paramShopId}&productLocalId=${product.productLocalInfoId}"> 点击查看最新商品详情</a>
                        </div>
                    </c:if>
                    <c:if test="${not empty product.promotionContent}">
                    <div class="info-item info-promotions">
                        <span class="info-label">促销：</span>
                        <span class="info-content">
                            <span class="content-note" class="promotionTitles" >${product.promotionTypesShortStr}</span>
                            <span class="content-button-hover">店铺活动 <span style="font-size: 10px">∨</span></span>
                        </span>
                    </div>
                    </c:if>
                    <div class="info-item info-quantity <c:if test="${isSnapShot eq true}">hidden</c:if>">
                        <span class="info-label">数量：</span>
                        <span class="info-content">
                            <input class="content-count" type="text" id="purchaseNum" maxlength="12" style="width:110px;"/>
                            <span class="content-additional">库存量：<c:choose>
                                <c:when test="${product.inSalesAmount == -1}">
                                    有货
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber pattern="#.##" value="${product.inSalesAmount}"/> ${product.inSalesUnit}
                                </c:otherwise>
                            </c:choose></span>
                        </span>
                    </div>
                    <div class="info-control <c:if test="${isSnapShot eq true}">hidden</c:if> ">
                        <div class="button-buy J-purchaseSingleBtn">立即购买</div>
                        <div class="button-buy-cart-added J-addToShoppingCartSingleBtn">加入购物车</div>
                    </div>

                    <c:if test="${isSnapShot eq true && isInSales eq true}">
                        <div class="info-trading-snapshot">
                            <div class="notice-icon warning-blue"></div>
                            <div class="notice-content">
                                <div class="content-line">您现在查看的是 <span class="word-blue">交易快照</span></div>
                                <div class="content-line">该宝贝在${product.lastUpdateTime}已被编辑。</div>
                                <div class="button-more" onclick="javascript:window.open('shopProductDetail.do?method=toShopProductDetail&paramShopId=${product.shopId}&productLocalId=${product.productLocalInfoId}', '_blank')">点击查看最新宝贝详情</div>
                                <div class="image-decorator"></div>
                            </div>
                            <div class="cl"></div>
                        </div>
                    </c:if>
                    <c:if test="${!isInSales}">
                        <div class="info-off-the-shelf">
                            此商品已下架
                        </div>
                    </c:if>
                </div><!--details-info-->
                <div class="cl"></div>

                <!--position:absolute-->
                <div class="button-edit">
                    <c:if test="${sessionScope.shopId == paramShopId}">
                    <a href="goodsInOffSales.do?method=toGoodsInSalesEditor&productId=${product.productLocalInfoId}" class="blue_color">[编辑]</a>
                    </c:if>
                </div>
            </div><!--content-details-->
        </div><!--end group-content-->

        <c:if test="${not empty product.promotionContent}">
        <div class="group-content product-sales-promotion-static">
            <div class="content-details j_promotions_content_static" >
                <div class="details-note-img fl">
                </div>
                <div class="cl"></div>
            </div>
        </div><!--end group-content product-sales-promotion--static-->
        </c:if>

        <div class="group-content product-sales-promotion-float" style="display:none;">
            <div class="content-details j_promotions_content" >
                <div class="details-note-img fl">
                </div>
                <div class="cl"></div>
            </div>
        </div><!--end group-content product-sales-promotion-float-->

        <div class="group-content product-basic">
            <div class="content-title">
                <div class="title-bg-hr"></div>
                <div class="title-label">商品基本信息</div>
            </div>
            <div class="content-details">
                <div class="details-line">
                    <span class="line-item col-1" style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                        <label class="item-label product-name">品名：</label>
                        <span class="item-content" title="${product.name}">${product.name}</span>
                    </span>
                    <span class="line-item col-2">
                        <label class="item-label product-specifications">规格：</label>
                        <span class="item-content">${product.spec}</span>
                    </span>
                    <span class="line-item col-3">
                        <label class="item-label product-vehicle-type">适用车牌：</label>
                        <span class="item-content">${product.productVehicleBrand}</span>
                    </span>
                </div>
                <div class="details-line">
                    <span class="line-item col-1">
                        <label class="item-label product-brand">品牌：</label>
                        <span class="item-content">${product.brand}</span>
                    </span>
                    <span class="line-item col-2">
                        <label class="item-label product-type">型号：</label>
                        <span class="item-content">${product.model}</span>
                    </span>
                    <span class="line-item col-3">
                        <label class="item-label product-vehicle-type">适用车型：</label>
                        <span class="item-content">${product.productVehicleModel}</span>
                    </span>
                </div>
                <div class="details-line">
                     <span class="line-item col-1">
                        <label class="item-label product-categories">质保时间：</label>
                        <span class="item-content">
                            <c:choose>
                                <c:when test="${empty product.guaranteePeriod}">
                                    --
                                </c:when>
                                <c:otherwise>
                                    ${product.guaranteePeriod}个月
                                </c:otherwise>
                            </c:choose></span>
                    </span>
                     <span class="line-item col-2" style="width: 520px;">
                        <label class="item-label product-categories">标准商品分类：</label>
                        <span class="item-content">${product.productCategoryInfo}</span>
                    </span>
                </div>
            </div><!--content-details-->
        </div><!--end group-content product-basic-->

        <div class="group-content product-details-info">
            <div class="content-title">
                <div class="title-bg-hr"></div>
                <div class="title-label">详细说明</div>
            </div>
            <div class="content-details">
                <div class="info-description" id="productDescription">
                    <c:if test="${empty product.description}">
                        暂无商品详细说明
                    </c:if>
                    ${product.description}
                </div>
            </div>
        </div><!--end group-content product-details-info-->

        <div class="group-content product-others">
            <div class="content-title">
                <div class="title-bg-hr"></div>
                <div class="title-label">
                    本公司其他商品信息
                    <div class="group-button">
                        <div class="button-more" onclick="javascript:window.location='shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${paramShopId}&shopMsgTabFlag=productList'">查看更多>></div>
                    </div>
                    <div class="cl"></div>
                </div>
            </div>
            <div class="content-details">
                <ul>
                    <c:forEach items="${otherProducts}" var="product">
                    <li class="details-product-item">
                        <div class="item-img">
                            <img src="${product.imageCenterDTO.otherProductListImageDetailDTO.imageURL}" alt=""/>
                        </div>
                        <div class="item-name" title="${product.name} ${product.brand} ${product.spec} ${product.model} ${product.productVehicleBrand} ${product.productVehicleModel}" onclick="window.open('shopProductDetail.do?method=toShopProductDetail&paramShopId=${paramShopId}&productLocalId=${product.productLocalInfoId}')">${product.name} ${product.brand} ${product.spec} ${product.model} ${product.productVehicleBrand} ${product.productVehicleModel}</div>
                    </li>
                    </c:forEach>
                    <div class="cl"></div>
                </ul>
            </div>
        </div><!--end group-content product-others-->


    </div><!--end tab-content product-details-->


</div>
<div class="height"></div>

<!-- shopping Car -->
<div class="alertCheck" id="returnDialog" style="display:none">
    <div class="alert_top"></div>
    <div class="alert_body">
        <div class="alertIcon">
            <a class="right"></a>

            <div class="line"><h3 id="resultMsg"></h3></div>
        </div>
        <div class="clear height"></div>
        <div class="line lines" id="warnMsg"></div>
        <div class="line lines">共有<b id="shoppingCartItemCount">0</b>种商品，合计：¥<b class="yellow_color"
                                                                                id="shoppingCartTotal">0</b></div>
        <div class="clear height"></div>
        <div class="button">
            <a class="btnHover" id="goShoppingCartBtn">去购物车结算</a>
            <a class="blue_color" id="closeBtn">继续采购</a>
        </div>
    </div>
    <div class="alert_bottom"></div>
</div>



<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>