<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>购物车</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/emptyshopViewed<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" href="js/components/themes/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" href="js/components/themes/bcgogo-sidebarListPanel<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/emptyshopViewed.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scrollFlow<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-sidebarListPanel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE_SHOPPINGCART");
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <jsp:include page="autoAccessoryOnlineNavi.jsp">
        <jsp:param name="currPage" value="shoppingCart"/>
    </jsp:include>
    <div class="clear"></div>
    <div class="emptyBody">
        <div class="emptyInfo">
            <h3>您的购物车还是空的，赶紧行动吧！您可以：</h3>
            <p>1. 去 <a href="autoAccessoryOnline.do?method=toCommodityQuotations">配件报价</a> 赶紧看看您要的商品吧！</p>
            <p>2. 去查看你已买到的配件，进入 <a href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS">在线采购订单</a> ！</p>
        </div>
        <p class="empty_tit">您可能感兴趣的商品...</p>

        <div class="tablePage">
            <a class="normal_btn hover_btn" href="javascript:void(0)" data-page="recommend">推荐配件</a>
            <a class="normal_btn" href="javascript:void(0)" data-page="scan">最近浏览</a>
        </div>

        <div class="content" data-page="recommend">
            <c:if test="${empty productDTOList}">
                <p>暂无推荐配件信息！</p>
            </c:if>
            <c:if test="${!empty productDTOList}">
                <c:forEach var="productDTO" items="${productDTOList}">
                    <div class="item" data-id="${productDTO.productLocalInfoIdStr}" data-shopId="${productDTO.shopId}">
                        <div class="imgContainer">
                            <img src="${productDTO.imageCenterDTO.recommendProductListImageDetailDTO.imageURL}"/>
                            <c:if test="${!empty productDTO.promotionsDTOs}">
                                <div class="promotionsing <c:if test="${productDTO.hasBargain}">hasBargain</c:if>">
                                        ${productDTO.promotionTypesShortStr}
                                </div>
                            </c:if>
                        </div>
                        <div class="infoContainer">
                            <div class="info"><span>${productDTO.productInfo}</span></div>
                            <div class="price">
                                <c:if test="${!productDTO.hasBargain}">
                                    &yen;${empty productDTO.inSalesPrice ? 0 : productDTO.inSalesPrice}
                                </c:if>
                                <c:if test="${productDTO.hasBargain}">
                                    <span style="color: #848484;text-decoration: line-through;font-weight: normal;">&yen;${empty productDTO.inSalesPrice ? 0 : productDTO.inSalesPrice}</span>
                                    <span>&yen;${empty productDTO.inSalesPriceAfterCal ? 0 : productDTO.inSalesPriceAfterCal}</span>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
        </div>

        <div class="content hide" data-page="scan">
            <c:if test="${empty recentlyViewedProductDTOList}">
                <p>您暂无浏览过的配件！</p>
            </c:if>
            <c:if test="${!empty recentlyViewedProductDTOList}">
                <c:forEach var="recentlyViewedProductDTO" items="${recentlyViewedProductDTOList}">
                    <div class="item" data-id="${recentlyViewedProductDTO.productLocalInfoIdStr}" data-shopId="${recentlyViewedProductDTO.shopId}">
                        <div class="imgContainer">
                            <img src="${recentlyViewedProductDTO.imageCenterDTO.productListSmallImageDetailDTO.imageURL}"/>
                            <c:if test="${!empty recentlyViewedProductDTO.promotionsDTOs}">
                                <div class="promotionsing <c:if test="${recentlyViewedProductDTO.hasBargain}">hasBargain</c:if>">
                                        ${recentlyViewedProductDTO.promotionTypesShortStr}
                                </div>
                            </c:if>
                        </div>
                        <div class="infoContainer">
                            <div class="info"><span>${recentlyViewedProductDTO.productInfo}</span></div>
                            <div class="price">
                                <c:if test="${!recentlyViewedProductDTO.hasBargain}">
                                    &yen;${empty recentlyViewedProductDTO.inSalesPrice ? 0 : recentlyViewedProductDTO.inSalesPrice}
                                </c:if>
                                <c:if test="${recentlyViewedProductDTO.hasBargain}">
                                    <span style="color: #848484;text-decoration: line-through;font-weight: normal;">&yen;${empty recentlyViewedProductDTO.inSalesPrice ? 0 : recentlyViewedProductDTO.inSalesPrice}</span>
                                    <span>&yen;${empty recentlyViewedProductDTO.inSalesPriceAfterCal ? 0 : recentlyViewedProductDTO.inSalesPriceAfterCal}</span>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
        </div>
    </div>
    <div class="clear i_height"></div>
</div>


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
            <a class="blue_color" id="closeBtn">继续采购</a>
        </div>
    </div>
    <div class="alert_bottom"></div>
</div>


<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>