<%--
  User: terry
  Date: 13-8-19
  Time: 上午9:17
  我要卖配件
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>汽配在线——我要卖配件</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.AUTOACCESSORYONLINE.SALES_ACCESSORY.BASE");
    </script>

    <script type="text/javascript">
        $(document).ready(function () {
            $("#productInSalesCount").click(function (e) {
                e.stopPropagation();
                window.open("goodsInOffSales.do?method=toInSalingGoodsList");
            });
            $("#preBuyOrdersCount").click(function (e) {
                e.stopPropagation();
                window.open("preBuyOrder.do?method=quotedPreBuyOrderManage");
            });
            $("#customerCount").click(function(){
                window.open("customer.do?method=customerdata&filter=relatedNum");
            });

        });
    </script>

</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
<jsp:include page="../txn/unit.jsp"/>
<div class="clear i_height"></div>
<div class="titBody">
<jsp:include page="supplyCenterLeftNavi.jsp">
    <jsp:param name="currPage" value="preSellMain"/>
</jsp:include>

<div class="content-main">

<!--我的销售订单-->
<div class="buy-sale-parts module-sales-order">
    <dl>
        <dt class="content-title">
        <div class="bg-top-hr"></div>
        <div class="bar-tab">
            <span class="title-name">在线销售订单</span>
            <span class="button-more" ><a href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS" target="_blank" class="blue_color">查看详细</a></span>
        </div>
        </dt>
        <dd class="content-details">
            <dl>
                <dt>新订单共有<span class="char-highlight node-linked" onclick="window.open('orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&currTab=NEW&orderStatus=SELLER_PENDING', 'salesOrderWindow')">${orderCenterDTO.saleNew}</span>条</dt>
                <dd>
                    今日新增：<span class="char-highlight node-linked" onclick="window.open('orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&currTab=NEW&orderStatus=SELLER_PENDING&startTimeStr=${todayDate}&endTimeStr=${todayDate}', 'salesOrderWindow')">${orderCenterDTO.saleTodayNew}</span>条
                    &nbsp;&nbsp;
                    往日新增：<span class="char-highlight node-linked" onclick="window.open('orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&currTab=NEW&orderStatus=SELLER_PENDING&endTimeStr=${yesterdayDate}', 'salesOrderWindow')">${orderCenterDTO.saleEarlyNew}</span>条
                </dd>
                <dt>处理中订单 共有<span class="char-highlight node-linked" onclick="window.open('orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=STOCKING,DISPATCH,SALE_DEBT_DONE', 'salesOrderWindow')">${orderCenterDTO.saleInProgress}</span>条</dt>
                <dd>
                    <div>
                        <div class="in-progress-span">
                        备货中待发货：<span class="char-highlight node-linked" onclick="window.open('orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=STOCKING', 'salesOrderWindow')">${orderCenterDTO.saleStocking}</span>条
                        </div>
                        <div class="in-progress-span">
                        已发货待结算：<span class="char-highlight node-linked" onclick="window.open('orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=DISPATCH', 'salesOrderWindow')">${orderCenterDTO.saleDispatch}</span>条
                        </div>
                        <div class="in-progress-span" style="margin-left:24px;">
                        欠款结算：<span class="char-highlight node-linked" onclick="window.open('orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=SALE_DEBT_DONE', 'salesOrderWindow')">${orderCenterDTO.saleSaleDebtDone}</span>条
                        </div>
                    </div>
            </dl>
        </dd>
    </dl>
</div>

<!--我的销售退货单-->
<div class="buy-sale-parts module-sales-return-order">
    <dl>
        <dt class="content-title">
        <div class="bg-top-hr"></div>
        <div class="bar-tab">
            <span class="title-name">在线销售退货单</span>
            <span class="button-more"><a target="_blank" href="onlineSalesReturnOrder.do?method=toOnlineSalesReturnOrder" class="blue_color">查看详细</a></span>
        </div>
        </dt>
        <dd class="content-details">
            <dl>
                <dt>新退货单共有<span class="char-highlight node-linked" onclick="window.open('onlineSalesReturnOrder.do?method=toOnlineSalesReturnOrder&orderStatus=PENDING&startTimeStr=&endTimeStr=', 'salesReturnWindow')">${orderCenterDTO.saleReturnNew}</span>条</dt>
                <dd>
                    今日新增：<span class="char-highlight node-linked" onclick="window.open('onlineSalesReturnOrder.do?method=toOnlineSalesReturnOrder&orderStatus=PENDING&startTimeStr=${todayDate}&endTimeStr=${todayDate}', 'salesReturnWindow')">${orderCenterDTO.saleReturnTodayNew}</span>条
                    &nbsp;&nbsp;
                    往日新增：<span class="char-highlight node-linked" onclick="window.open('onlineSalesReturnOrder.do?method=toOnlineSalesReturnOrder&orderStatus=PENDING&startTimeStr=&endTimeStr=${yesterdayDate}', 'salesReturnWindow')">${orderCenterDTO.saleReturnEarlyNew}</span>条
                </dd>
                <dt>处理中退货单 共有<span class="char-highlight node-linked" onclick="window.open('onlineSalesReturnOrder.do?method=toOnlineSalesReturnOrder&orderStatus=WAITING_STORAGE', 'salesReturnWindow')">${orderCenterDTO.saleReturnInProgress}</span>条
                </dt>
                <dd>
                    待入库结算：<span class="char-highlight node-linked" onclick="window.open('onlineSalesReturnOrder.do?method=toOnlineSalesReturnOrder&orderStatus=WAITING_STORAGE', 'salesReturnWindow')">${orderCenterDTO.saleReturnInProgress}</span>条
                </dd>
            </dl>
        </dd>
    </dl>
</div>

<!--我的上架商品-->
<div class="buy-sale-parts module-added-products">
    <dl>
        <dt class="content-title">
        <div class="bg-top-hr"></div>
        <div class="bar-tab">
            <span class="title-name">我的上架商品</span>
            <span class="button-promotions-publish button-blue-gradient" onclick="javascript:window.open('promotions.do?method=toPromotionsList')">我要促销</span>
            <span class="button-more"><%--<a href="goodsInOffSales.do?method=toInSalingGoodsList" class="blue_color">查看详细</a>--%></span>
        </div>
        </dt>
        <dd class="content-details content-details-product">
            <c:choose>
                <c:when test="${empty products}">
                    您还未上架任何商品哦，<a target="_blank" class="blue_color" href="goodsInOffSales.do?method=toUnInSalingGoodsList" >赶紧去上架</a>吧！
                </c:when>
                <c:otherwise>
                    <ul>
                        <c:forEach items="${products}" var="item" varStatus="status">
                            <c:choose>
                               <c:when test="${not empty item.promotionsDTOs && item.inSalesPriceAfterCal-item.inSalesPrice lt 0}">
                                    <li>
                                        <dl>
                                            <c:if test="${not empty item.imageCenterDTO && not empty item.imageCenterDTO.productListSmallImageDetailDTO}">
                                                <dt class="item-title">
                                                    <img class="title-img" src="${item.imageCenterDTO.productListSmallImageDetailDTO.imageURL}"/>
                                                </dt>
                                            </c:if>
                                            <dd class="item-info">
                                                <div class="info-main"><a target="_blank" href="shopProductDetail.do?method=toShopProductDetail&paramShopId=${item.shopIdStr}&productLocalId=${item.productLocalInfoIdStr}" class="blue_color">${item.productInfoStr}</a></div>
                                                <div class="info-comment">
                                                    <div class="comment-original-price"><span class="arialFont">&yen;</span>${item.inSalesPrice}</div>
                                                    <div class="comment-current-price"><span class="arialFont">&yen;</span>${item.inSalesPriceAfterCal}</div>
                                                    <%--<div class="comment-img-green">省<span class="arialFont">&yen;</span>${item.inSalesPrice-item.inSalesPriceAfterCal}</div>--%>
                                                    <%--<div class="green-bg">
                                                        <div class="left"></div>
                                                        <div class="right">省<span class="arialFont">&yen;</span>${item.inSalesPrice-item.inSalesPriceAfterCal}</div>
                                                    </div>--%>
                                                    <c:if test="${not empty item.promotionTypesShortStr}">
                                                        <div class="comment-img-yellow">${item.promotionTypesShortStr}</div>
                                                    </c:if>
                                                    <div class="cl"></div>
                                                </div>
                                            </dd>
                                            <div class="cl"></div>
                                        </dl>
                                    </li>
                                </c:when>
                                <c:otherwise>
                                    <li>
                                        <dl>
                                            <c:if test="${not empty item.imageCenterDTO && not empty item.imageCenterDTO.productListSmallImageDetailDTO}">
                                                <dt class="item-title">
                                                    <img class="title-img" src="${item.imageCenterDTO.productListSmallImageDetailDTO.imageURL}"/>
                                                </dt>
                                            </c:if>
                                            <dd class="item-info">
                                                <div class="info-main"><a target="_blank" href="shopProductDetail.do?method=toShopProductDetail&paramShopId=${item.shopIdStr}&productLocalId=${item.productLocalInfoIdStr}" class="blue_color">${item.productInfoStr}</a></div>
                                                <div class="info-comment">
                                                    <div class="comment-current-price"><span class="arialFont">&yen;</span>${item.inSalesPrice}</div>
                                                    <c:if test="${not empty item.promotionTypesShortStr}">
                                                        <div class="comment-img-yellow">${item.promotionTypesShortStr}</div>
                                                    </c:if>
                                                    <div class="cl"></div>
                                                </div>
                                            </dd>
                                            <div class="cl"></div>
                                        </dl>
                                    </li>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </ul>
                    <div class="info-note" id="productInSalesCount">
                        共上架<em>${productInSalesCount}</em>种商品
                    </div>
                </c:otherwise>
            </c:choose>
        </dd>
    </dl>
</div>

<!--我的报价-->
<div class="buy-sale-parts module-quoted-price">
    <dl>
        <dt class="content-title">
        <div class="bg-top-hr"></div>
        <div class="bar-tab">
            <span class="title-name">我的报价</span>
            <span class="button-more"><%--<a href="preBuyOrder.do?method=quotedPreBuyOrderManage" class="blue_color">查看详细</a>--%></span>
        </div>
        </dt>
        <dd class="content-details">
            <c:choose>
                <c:when test="${empty quotedPreBuyOrders}">
                    您暂无报价信息哦！<a class="blue_color" href="preBuyOrder.do?method=preBuyInformation" target="_blank">去看看最近的求购</a>吧！
                </c:when>
                <c:otherwise>
                    <ul>
                        <c:forEach items="${quotedPreBuyOrders}" var="item" varStatus="status">
                            <li>
                                <dl>
                                    <c:if test="${not empty item.itemImageCenterDTO && not empty item.itemImageCenterDTO.productListSmallImageDetailDTO}">
                                        <dt class="item-title">
                                            <a href="preBuyOrder.do?method=showBuyInformationDetailByQuotedPreBuyOrderItemId&quotedPreBuyOrderItemId=${quotedPreBuyItems[item.id].id}" target="_blank"><img class="title-img" src="${item.itemImageCenterDTO.productListSmallImageDetailDTO.imageURL}"/></a>
                                        </dt>
                                    </c:if>
                                    <dd class="item-info">
                                        <div class="info-main"><a class="blue_color" href="preBuyOrder.do?method=showBuyInformationDetailByQuotedPreBuyOrderItemId&quotedPreBuyOrderItemId=${quotedPreBuyItems[item.id].id}" target="_blank">${item.title}</a></div>
                                        <div class="info-comment">
                                            <div class="comment-status">
                                                <c:choose>
                                                    <c:when test="${item.purchase}">
                                                        <span class="status-done">已下单</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-todo">暂未下单</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="cl"></div>
                                        </div>
                                    </dd>
                                    <div class="cl"></div>
                                </dl>
                            </li>
                        </c:forEach>
                    </ul>
                    <div class="info-note" id="preBuyOrdersCount">
                        共发布<em>${allPreBuyOrdersCount}</em>条报价信息，通过报价下单<em>${ordersFromQuotedPreBuyOrderCount}</em>次
                    </div>
                </c:otherwise>
            </c:choose>
        </dd>
    </dl>
</div>

<!--我的在线客户-->
<div class="buy-sale-parts module-online-customer">
    <dl>
        <dt class="content-title">
        <div class="bg-top-hr"></div>
        <div class="bar-tab">
            <span class="title-name">关联客户</span>
            <span class="button-more"><%--<a href="customer.do?method=customerdata&filter=relatedNum" class="blue_color" >查看详细</a>--%></span>
        </div>
        </dt>
        <dd class="content-details">
            <c:choose>
                <c:when test="${empty customerDTOs}">
                    您暂无关联客户哦！<a class="blue_color" href="apply.do?method=getApplyCustomersIndexPage" target="_blank">赶紧去找客户</a>吧！
                </c:when>
                <c:otherwise>
                    <ul>
                        <c:forEach items="${customerDTOs}" varStatus="status" var="item">
                            <li>
                                <div class="item-info">
                                    <div class="item-main ellipsis">
                                        <a target="_blank" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${item.customerShopId}"
                                           class="blue_color" title="${item.name}">${item.name}</a>
                                    </div>
                                    <div class="item-comment">
                                        <c:if test="${not empty item.customerShopId}">
                                            <div class="comment-img-red">商家</div>
                                        </c:if>
                                    </div>
                                    <div class="cl"></div>
                                </div>
                            </li>
                        </c:forEach>
                    </ul>
                    <div class="info-note" id="customerCount">
                        共有<em>${customerCount}</em>家关联客户
                    </div>
                </c:otherwise>
            </c:choose>
        </dd>
    </dl>
</div>

</div>


<div class="height"></div>
<!----------------------------页脚----------------------------------->

</div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id"></div>
</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1000px" frameborder="0" src="" scrolling="no"></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>