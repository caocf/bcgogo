<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>汽配在线——我要买配件</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.AUTOACCESSORYONLINE.BUY_ACCESSORY.BASE");
    </script>

    <script type="text/javascript">
        $(document).ready(function () {
            $("#supplierCount").click(function(){
                window.location.href = "customer.do?method=searchSuppiler&filter=relatedNum";
            });
            $("#shoppingCartContentDetail").bind("mouseenter",function(event){
                event.stopImmediatePropagation();
                var _currentTarget=event.target;
                $("#shoppingCartMask").show(80);

                $("#shoppingCartMask").mouseleave(function (event) {
                    if (event.relatedTarget != _currentTarget) {
                        $("#shoppingCartMask").hide(80);
                    }
                });
            }).bind("mouseleave",function(event){
                event.stopImmediatePropagation();
                var _currentTarget=event.target;
                if(event.relatedTarget!=_currentTarget && event.relatedTarget !=$("#shoppingCartMask")[0]) {
                    _currentTarget.hide(80);
                }

            });

        });
        var todayDate = dateUtil.getToday();
        var yesterdayDate = dateUtil.getYesterday();
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
    <jsp:param name="currPage" value="preBuyMain"/>
</jsp:include>

<div class="content-main">

<!--在线采购订单-->
<div class="buy-sale-parts module-procurement-order">
    <dl>
        <dt class="content-title">
        <div class="bg-top-hr"></div>
        <div class="bar-tab">
            <span class="title-name">在线采购订单</span>
            <span class="button-more"><a target="_blank" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS" class="blue_color">查看详细</a></span>
        </div>
        </dt>
        <dd class="content-details">
            <dl>
                <dt>新订单共有<a class="char-highlight" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&filterParam=purchase_new" target="purchaseOrderWindow">${orderCenterDTO.purchaseNew}</a>条</dt>
                <dd>
                    今日新增：<a class="char-highlight" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&filterParam=purchase_today_new" target="purchaseOrderWindow">${orderCenterDTO.purchaseTodayNew}</a>条
                    &nbsp;&nbsp;
                    往日新增：<a class="char-highlight" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&filterParam=purchase_early_new" target="purchaseOrderWindow">${orderCenterDTO.purchaseEarlyNew}</a>条
                </dd>
                <dt>处理中订单 共有<a class="char-highlight" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&filterParam=purchase_in_progress" target="purchaseOrderWindow">${orderCenterDTO.purchaseInProgress}</a>条</dt>
                <dd>
                    <div>
                        卖家备货中：<a class="char-highlight" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&filterParam=purchase_seller_stock" target="purchaseOrderWindow">${orderCenterDTO.purchaseSellerStock}</a>条
                        &nbsp;&nbsp;
                        卖家拒绝销售：<a class="char-highlight" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&filterParam=purchase_seller_refused" target="purchaseOrderWindow">${orderCenterDTO.purchaseSellerRefused}</a>条
                    </div>
                    <div>
                        卖家已发货：<a class="char-highlight" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&filterParam=purchase_seller_dispatch" target="purchaseOrderWindow">${orderCenterDTO.purchaseSellerDispatch}</a>条
                        &nbsp;&nbsp;
                        卖家终止销售：<a class="char-highlight" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&filterParam=purchase_seller_stop" target="purchaseOrderWindow">${orderCenterDTO.purchaseSellerStop}</a>条
                    </div>
                </dd>
                <dt>已入库共有<a class="char-highlight" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&filterParam=purchase_done" target="purchaseOrderWindow">${orderCenterDTO.purchaseDone}</a>条</dt>
                <dd>
                    今日入库：<a class="char-highlight" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&filterParam=purchase_today_done" target="purchaseOrderWindow">${orderCenterDTO.purchaseTodayDone}</a>条
                    &nbsp;&nbsp;
                    往日入库：<a class="char-highlight" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&filterParam=purchase_early_done" target="purchaseOrderWindow">${orderCenterDTO.purchaseEarlyDone}</a>条
                </dd>
            </dl>
        </dd>
    </dl>
</div>

<!--在线入库退货单-->
<div class="buy-sale-parts module-warehousing-procurement-order">
    <dl>
        <dt class="content-title">
        <div class="bg-top-hr"></div>
        <div class="bar-tab">
            <span class="title-name">在线入库退货单</span>
            <span class="button-more"><a target="_blank" href="onlinePurchaseReturnOrder.do?method=toOnlinePurchaseReturnOrder" class="blue_color">查看详细</a></span>
        </div>
        </dt>
        <dd class="content-details">
            <dl>
                <dt>新退货单共有<a class="char-highlight" href="onlinePurchaseReturnOrder.do?method=toOnlinePurchaseReturnOrder&orderStatus=SELLER_PENDING&startTimeStr=&endTimeStr=" target="purchaseReturnWindow">${orderCenterDTO.purchaseReturnNew}</a>条</dt>
                <dd>
                    今日新增：<a class="char-highlight" href="onlinePurchaseReturnOrder.do?method=toOnlinePurchaseReturnOrder&orderStatus=SELLER_PENDING&startTimeStr=${todayDate}&endTimeStr=${todayDate}" target="purchaseReturnWindow">${orderCenterDTO.purchaseReturnTodayNew}</a>条
                    &nbsp;&nbsp;
                    往日新增：<a class="char-highlight" href="onlinePurchaseReturnOrder.do?method=toOnlinePurchaseReturnOrder&orderStatus=SELLER_PENDING&startTimeStr=&endTimeStr=${yesterdayDate}" target="purchaseReturnWindow">${orderCenterDTO.purchaseReturnEarlyNew}</a>条
                </dd>
                <dt>处理中退货单 共有<a class="char-highlight" href="onlinePurchaseReturnOrder.do?method=toOnlinePurchaseReturnOrder&orderStatus=SELLER_ACCEPTED,SELLER_REFUSED&startTimeStr=&endTimeStr=" target="purchaseReturnWindow">${orderCenterDTO.purchaseReturnInProgress}</a>条</dt>
                <dd>
                    卖家备货中：<a class="char-highlight" href="onlinePurchaseReturnOrder.do?method=toOnlinePurchaseReturnOrder&orderStatus=SELLER_ACCEPTED&startTimeStr=&endTimeStr=" target="purchaseReturnWindow">${orderCenterDTO.purchaseReturnSellerAccept}</a>条
                    &nbsp;&nbsp;
                    卖家拒绝销售：<a class="char-highlight" href="onlinePurchaseReturnOrder.do?method=toOnlinePurchaseReturnOrder&orderStatus=SELLER_REFUSED&startTimeStr=&endTimeStr=" target="purchaseReturnWindow">${orderCenterDTO.purchaseReturnSellerRefused}</a>条
                </dd>
            </dl>
        </dd>
    </dl>
</div>

<!--我的购物车-->
<div class="buy-sale-parts module-shopping-cart">
    <dl>
        <dt class="content-title">
        <div class="bg-top-hr"></div>
        <div class="bar-tab">
            <span class="title-name">我的购物车</span>
            <span class="button-more"><a target="_blank" href="shoppingCart.do?method=shoppingCartManage" class="blue_color">查看详细</a></span>
        </div>
        </dt>
        <c:if test="${empty shoppingCarItemDTOs}">
            <div class="mask" id="shoppingCartMask" style="display: none"><strong>把想买的商品放进来吧，有优惠活动时我会提醒你哦！</strong></div>
        </c:if>
        <dd class="content-details" id="shoppingCartContentDetail">
            <c:choose>
                <c:when test="${empty shoppingCarItemDTOs}">
                    <div style="width: 100%;height: 100%;text-align: center;">
                        <img src="images/shopcart.png" style="margin-top: 30px">
                    </div>
                </c:when>
                <c:otherwise>
                    <ul>
                        <c:forEach items="${shoppingCarItemDTOs}" var="item" varStatus="status">
                            <c:choose>
                                <c:when test="${not empty item.promotionsDTOList && item.inSalesPriceAfterCal-item.inSalesPrice lt 0}">
                                    <li>
                                        <dl>
                                            <c:if test="${not empty item.imageCenterDTO && not empty item.imageCenterDTO.productListSmallImageDetailDTO}">
                                                <dt class="item-title">
                                                    <img class="title-img" src="${item.imageCenterDTO.productListSmallImageDetailDTO.imageURL}"/>
                                                </dt>
                                            </c:if>
                                            <dd class="item-info">
                                                <div class="info-main"><a target="_blank" href="shopProductDetail.do?method=toShopProductDetail&paramShopId=${item.supplierShopId}&productLocalId=${item.productLocalInfoIdStr}" class="blue_color">${item.productInfoStr}</a></div>
                                                <div class="info-comment">
                                                    <div class="comment-original-price">${item.inSalesPrice}</div>
                                                    <div class="comment-current-price">${item.inSalesPriceAfterCal}</div>
                                                    <%--<div class="comment-img-green">省${item.quotedPrice-item.price}</div>--%>
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
                                                <div class="info-main"><a target="_blank" href="shopProductDetail.do?method=toShopProductDetail&paramShopId=${item.supplierShopId}&productLocalId=${item.productLocalInfoIdStr}" class="blue_color">${item.productInfoStr}</a></div>
                                                <div class="info-comment">
                                                        <%--<div class="comment-original-price">${item.quotedPrice}</div>--%>
                                                    <div class="comment-current-price">${item.quotedPrice}</div>
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
                        <c:if test="${not empty shoppingCarItemDTOs && fn:length(shoppingCarItemDTOs)==1}">
                            <li>
                                <dl>
                                    <div class="cl"></div>
                                    <dd class="none-news">

                                        主人，不要太小气嘛，多放点商品来陪我吧。
                                    </dd>
                                    <div class="cl"></div>
                                </dl>
                            </li>
                        </c:if>
                    </ul>
                </c:otherwise>
            </c:choose>
        </dd>
    </dl>
</div>

<!--关联供应商-->
<div class="buy-sale-parts module-online-supplier">
    <dl>
        <dt class="content-title">
        <div class="bg-top-hr"></div>
        <div class="bar-tab">
            <span class="title-name">关联供应商</span>
            <%--<span class="button-more">查看详细</span>--%>
        </div>
        </dt>
        <dd class="content-details">
            <c:choose>
                <c:when test="${empty supplierDTOs}">
                    <table width="100%" border="0" height="100%">
                        <tr>
                            <td><img src="images/cry.png"/></td>
                            <td style="color: #999999"> 您暂无关联供应商哦！ </td>
                        </tr>
                    </table>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${supplierDTOs}" var="item" varStatus="status">
                        <ul>
                            <li>
                                <div class="item-info">
                                    <div class="item-main ellipsis">
                                        <a target="_blank" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${item.supplierShopId}"
                                           class="blue_color" title="${item.name}">${item.name}</a>
                                    </div>
                                    <div class="item-comment">
                                        <div class="comment-img-red">商家</div>
                                    </div>
                                    <div class="cl"></div>
                                </div>
                            </li>
                        </ul>
                        <div class="info-note" id="supplierCount" onclick="window.open('customer.do?method=searchSuppiler&filter=relatedSupplier', '_blank')">
                            共有<em>${relatedSupplierCount}</em>家关联供应商
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </dd>
    </dl>
</div>

<!-- 配件信息 -->
<div class="buy-sale-parts parts-info">
    <dl>
        <dt class="content-title">
        <div class="bg-top-hr"></div>
        <div class="bar-tab">
            <span class="title-name">配件信息</span>
        </div>
        </dt>
        <dd class="content-details">
            <ul>
                <c:choose>
                    <c:when test="${empty productList}">
                        暂无与您匹配的配件信息，前往<a class="blue_color" href="autoAccessoryOnline.do?method=toCommodityQuotations" target="_blank">配件报价</a>！
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${productList}" var="item" varStatus="status">
                            <c:choose>
                                <c:when test="${not empty item.promotionsDTOs && item.inSalesPriceAfterCal-item.inSalesPrice lt 0}">
                                    <li>
                                        <dl >
                                            <c:if test="${not empty item.imageCenterDTO && not empty item.imageCenterDTO.productListSmallImageDetailDTO}">
                                                <dt class="item-title">
                                                    <img class="title-img" src="${item.imageCenterDTO.productListSmallImageDetailDTO.imageURL}"/>
                                                </dt>
                                            </c:if>
                                            <dd class="item-info">
                                                <div class="info-main"><a target="_blank" href="shopProductDetail.do?method=toShopProductDetail&paramShopId=${item.shopIdStr}&productLocalId=${item.productLocalInfoIdStr}" class="blue_color">${item.productInfoStr}</a></div>
                                                <div class="info-comment">
                                                    <div class="comment-original-price">¥${item.inSalesPrice}</div>
                                                    <div class="comment-current-price">¥${item.inSalesPriceAfterCal}</div>
                                                    <div class="green-bg">
                                                        <div class="left"></div>
                                                        <div class="right">省<span class="arialFont">&yen;</span>${item.inSalesPrice-item.inSalesPriceAfterCal}</div>
                                                    </div>
                                                    <div class="cl"></div>
                                                </div>
                                                <div class="cl"></div>
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
                                                    <div class="comment-current-price">
                                                        ¥${item.inSalesPrice}
                                                    </div>
                                                    <c:if test="${not empty item.promotionTypesShortStr}">
                                                        <div class="comment-img-yellow">${item.promotionTypesShortStr}</div>
                                                    </c:if>
                                                    <div class="cl"></div>
                                                </div>
                                                <div class="cl"></div>
                                            </dd>
                                            <div class="cl"></div>
                                        </dl>
                                    </li>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>


            </ul>
            <div class="info-note" onclick="javascript:window.open('autoAccessoryOnline.do?method=toCommodityQuotations', '_blank')">
                查看更多配件信息
            </div>
        </dd>
    </dl>
</div>


<!--我的求购-->
<bcgogo:permission>
    <bcgogo:if permissions="WEB.SUPPLIER_DEMAND.MY_PRE_ORDER_BLOCK">
        <div class="buy-sale-parts module-buy" >
            <dl>
                <dt class="content-title">
                <div class="bg-top-hr"></div>
                <div class="bar-tab">
                    <span class="title-name">我的求购</span>
                    <span class="button-buy-publish button-blue-gradient" onclick="javascript:window.open('preBuyOrder.do?method=createPreBuyOrder')">发布求购</span>
                        <%--<span class="button-more">查看详细</span>--%>

                    <div class="cl"></div>
                </div>
                </dt>
                <dd class="content-details">
                    <ul>
                        <c:choose>
                            <c:when test="${empty preBuyOrderDTOs}">
                                您暂无求购信息哦！赶紧<a class="blue_color" href="preBuyOrder.do?method=createPreBuyOrder" target="_blank">发布求购</a>吧！
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${preBuyOrderDTOs}" var="item" varStatus="status">
                                    <li>
                                        <dl>
                                            <c:if test="${not empty item.itemImageCenterDTO && not empty item.itemImageCenterDTO.productListSmallImageDetailDTO}">
                                                <dt class="item-title">
                                                    <img class="title-img" src="${item.itemImageCenterDTO.productListSmallImageDetailDTO.imageURL}"/>
                                                </dt>
                                            </c:if>
                                            <dd class="item-info">
                                                <div class="info-main"><a class="blue_color" href="preBuyOrder.do?method=showPreBuyOrderById&preBuyOrderId=${item.id}" target="_blank">${item.title}</a></div>
                                                <div class="info-comment">
                                                    <span class="comment-count">共${fn:length(item.itemDTOs)}件商品</span>
                                                    <span class="comment-note">有${item.quotedCount}次报价</span>
                                                </div>
                                            </dd>
                                            <div class="cl"></div>
                                        </dl>
                                    </li>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                    <div class="info-note" id="preBuyOderServiceCount" onclick="window.open('preBuyOrder.do?method=preBuyOrderManage', '_blank')">
                        共发布<em>${preBuyOderServiceCount}</em>条求购信息，获得<em>${quotedPreBuyOderServiceCount}</em>次报价
                    </div>
                </dd>
            </dl>
        </div>
    </bcgogo:if>
    <bcgogo:else>
        <div class="buy-sale-parts module-buy" style="border-style: dashed; height: 259px;vertical-align:middle;">
            <div style="text-align: center;height:259px;color:#999999;margin-top:100px;">更多内容，敬请期待！</div>

        </div>
    </bcgogo:else>
</bcgogo:permission>



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