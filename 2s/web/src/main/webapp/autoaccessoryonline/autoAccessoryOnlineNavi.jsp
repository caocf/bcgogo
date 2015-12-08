<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<style>
    .notification-num {
        background: url("images/tabs_bg.png") no-repeat -174px -153px;
        height: 16px;
        min-width: 12px;
        padding: 0 3px;
        color: #FFFFFF;
        cursor: pointer;
        /*float: left;*/
        position: absolute;
        line-height: 1.5;
        border: 1px solid #810C0E;
        margin-top: -8px;
        margin-left: 11px;
        /*text-align: center;*/
        font-size: 10px;
    }
</style>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<div class="mainTitles parts-quoted-price">
    <div class="titleWords">
        <c:choose>
            <c:when test="${currPage==\"relatedCustomerStock\"}">客户库存</c:when>
            <c:when test='${currPage==\"goodsInOffSalesManage\"}'>商品上架维护</c:when>
            <c:when test='${currPage==\"commodityQuotations\"}'>配件报价</c:when>
            <c:when test='${currPage==\"shoppingCart\"}'>购物车</c:when>
            <c:when test='${currPage==\"onlineReturn\"}'>在线退货</c:when>
            <c:when test='${currPage==\"orderCenter\"}'>订单中心</c:when>
            <c:when test='${currPage==\"goodsBuyOnline\"}'>在线采购</c:when>
            <c:when test='${currPage==\"preBuyList\"}'>求购资讯</c:when>
            <c:when test='${currPage==\"preBuyDetail\"}'>求购详情</c:when>
            <c:when test="${currPage==\"bcgogoReceivableRecord\"}">一发供求专区</c:when>
            <c:when test="${currPage==\"bcgogoOnlineOrder\"}">在线订单</c:when>
            <c:when test="${currPage==\"bcgogoOnlineOrderPay\"}">在线支付</c:when>
            <c:otherwise>汽配在线</c:otherwise>
        </c:choose>
    </div>

    <c:if test='${currPage==\"commodityQuotations\"}'>
        <div id="shoppingCartButton" class="shoppingCart">
            <div class="content-scb"><img src="images/cartIcon.png">购物车</div>
            <div class="bubble-scb">0</div>
            <div class="detailed-scb"></div>
        </div>

        <c:choose>
            <c:when test="${isWholesaler}">
                <bcgogo:permissionParam  permissions="WEB.AUTOACCESSORYONLINE.GOODS_IN_OFF_SALES_MANAGE,WEB.AUTOACCESSORYONLINE.MY_QUOTEDPREBUYORDER,WEB.AUTOACCESSORYONLINE.RELATEDCUSTOMERSTOCK.BASE,
                    WEB_TXN_ORDER_CENTER_BASE,WEB.AUTOACCESSORYONLINE.BUYING_INFORMATION,WEB.AUTOACCESSORYONLINE.SHOP_DATA">
                    <c:if test="${WEB_AUTOACCESSORYONLINE_GOODS_IN_OFF_SALES_MANAGE || WEB_AUTOACCESSORYONLINE_MY_QUOTEDPREBUYORDER || WEB_AUTOACCESSORYONLINE_RELATEDCUSTOMERSTOCK_BASE
                        || WEB_TXN_ORDER_CENTER_BASE || WEB_AUTOACCESSORYONLINE_BUYING_INFORMATION || WEB_AUTOACCESSORYONLINE_SHOP_DATA}">
                         <span class="group-button-seller fr">
                            <div class="button-label"><img style="float: left; margin: 2px 0px 0px 0px" src="images/opt-btn.png"/>我的供求</div>
                            <ul class="button-content-details">
                                <c:if test="${WEB_AUTOACCESSORYONLINE_GOODS_IN_OFF_SALES_MANAGE}">
                                    <li><a href="goodsInOffSales.do?method=toInSalingGoodsList">上架管理</a></li>
                                </c:if>
                                <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.PROMOTIONS_MANAGER_RENDER">
                                    <li><a href="promotions.do?method=toPromotionsList">促销管理</a></li>
                                </bcgogo:hasPermission>
                                <c:if test="${WEB_AUTOACCESSORYONLINE_MY_QUOTEDPREBUYORDER}">
                                    <li><a href="preBuyOrder.do?method=quotedPreBuyOrderManage">我的报价</a></li>
                                </c:if>
                                <c:if test="${WEB_AUTOACCESSORYONLINE_RELATEDCUSTOMERSTOCK_BASE}">
                                    <li><a href="autoAccessoryOnline.do?method=toRelatedCustomerStock">客户库存</a></li>
                                </c:if>
                                <c:if test="${WEB_TXN_ORDER_CENTER_BASE}">
                                    <li><a href="orderCenter.do?method=showOrderCenter">订单中心</a></li>
                                </c:if>
                                <li><a href="apply.do?method=getApplyCustomersIndexPage">找客户</a></li>
                                <c:if test="${WEB_AUTOACCESSORYONLINE_BUYING_INFORMATION}">
                                    <li><a href="preBuyOrder.do?method=preBuyInformation">求购资讯</a></li>
                                </c:if>
                                <c:if test="${WEB_AUTOACCESSORYONLINE_SHOP_DATA}">
                                    <li><a href="shopData.do?method=toManageShopData">本店资料</a></li>
                                </c:if>
                            </ul>
                        </span>
                        <div class="cl"></div>
                    </c:if>
                </bcgogo:permissionParam>
            </c:when>
            <c:otherwise>
                <bcgogo:permissionParam  permissions="WEB.AUTOACCESSORYONLINE.RELEASE_PREBUYORDER,WEB.AUTOACCESSORYONLINE.MY_PREBUYORDER,WEB.TXN.ORDER_CENTER.BASE,WEB.AUTOACCESSORYONLINE.SHOP_DATA">
                    <c:if test="${WEB_AUTOACCESSORYONLINE_RELEASE_PREBUYORDER || WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER || WEB_TXN_ORDER_CENTER_BASE || WEB_AUTOACCESSORYONLINE_SHOP_DATA}">
                        <span class="group-button-seller fr">
                            <div class="button-label"><img style="float: left; margin: 2px 0px 0px 0px" src="images/opt-btn.png"/>我的供求</div>
                            <ul class="button-content-details">
                                <c:if test="${WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER}">
                                    <li><a href="preBuyOrder.do?method=preBuyOrderManage">我的求购</a></li>
                                </c:if>
                                <c:if test="${WEB_AUTOACCESSORYONLINE_RELEASE_PREBUYORDER}">
                                    <li><a href="preBuyOrder.do?method=createPreBuyOrder">发布求购</a></li>
                                </c:if>
                                <li><a href="apply.do?method=getApplySuppliersIndexPage">找供应商</a></li>
                                <c:if test="${WEB_TXN_ORDER_CENTER_BASE}"><li><a href="orderCenter.do?method=showOrderCenter">订单中心</a></li></c:if>
                                <c:if test="${WEB_AUTOACCESSORYONLINE_SHOP_DATA}"><li><a href="shopData.do?method=toManageShopData">本店资料</a></li></c:if>
                            </ul>
                        </span>
                        <div class="cl"></div>
                    </c:if>
                </bcgogo:permissionParam>
            </c:otherwise>
        </c:choose>

    </c:if>
</div>
