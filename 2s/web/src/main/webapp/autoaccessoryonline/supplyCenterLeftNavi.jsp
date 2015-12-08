<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-8-7
  Time: 上午9:52
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<style>
    .supplyCenterLeftNavi ul li
    {
        float:none;
    }
</style>
<script type="text/javascript">
    $().ready(function(){
        $(".supplyCenterLeftNavi li").click(function(){
            var toURL=$(this).attr("toURL");
            if(!G.isEmpty(toURL)){
                window.location.href=toURL;
            }
        });
        //获取店铺资料
        if(!G.isEmpty($("#shopData").val())){
            getShopComment(function(shopInfo){
                if(G.isEmpty(shopInfo)){
                    return;
                }
                $("#shopName").text(G.normalize(shopInfo.name));
                if(shopInfo.licensed){
                    $("#licenseImg").attr("src","images/license.png");
                }else{
                   $("#licenseImg").attr("src","images/unlicense.png");
                }

                var supplierCommentStat=shopInfo.commentStatDTO;
                if(G.isEmpty(supplierCommentStat)){
                    $("#shopTotalScoreInNavi").text("暂无分数");
                    $("#shopTotalScoreStarInNavi").addClass("normal-light-star-level-0");
                }else{
                    var totalScore= Number(supplierCommentStat.totalScore).toFixed(1);
                    $("#shopTotalScoreInNavi").text(totalScore==0?"暂无分数":(supplierCommentStat.totalScore+"分"));
                    var totalScoreWidth = supplierCommentStat.totalScoreWidth;

                    $("#shopTotalScoreStarInNavi").addClass("normal-light-star-level-"+ totalScoreWidth);
                }
            });
        }
        linkBinds();
    });

    function linkBinds() {
        $("#preBuyMainHref").click(function (e) {
            e.stopPropagation();
            e.preventDefault();
            window.location.href = "preBuyMain.do?method=toPreBuyMain";
        });
        $("#preSellMainHref").click(function (e) {
            e.stopPropagation();
            e.preventDefault();
            window.location.href = "preSellMain.do?method=toPreSellMain";
        });
        $("#orderCenterHref").click(function (e) {
            e.stopPropagation();
            e.preventDefault();
            window.location.href = "navigator.do?method=orderCenter";
        });
        $("#bcgogoOrderCenterHref").click(function (e) {
            e.stopPropagation();
            e.preventDefault();
            window.location.href = "bcgogoReceivable.do?method=bcgogoReceivableOrderList";
        });
    }
</script>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<c:set var="biMenu" value="<%=request.getParameter(\"biMenu\")%>"/>
<div class="mainTitles">
  <c:if test='${currPage==\"promotions\"}'>
    <div class="titleWords">本店资料</div>
  </c:if>
  <c:if test='${currPage!=\"promotions\"}'>
    <div class="titleWords">我的供求</div>
  </c:if>
</div>
<div class="bodyRight releaseLeft">
    <div class="title_blue title_yellow">我的店铺</div>
    <div class="order_titles shop supplyCenterLeftNavi">
        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.SHOP_DATA">
            <input type="hidden" id="shopData" value="shopData" />
            <div class="order_list">
                <span class="list_top"></span>

                <div class="list_body">
                    <div class="title"><span id="shopName" class="shop_name"></span></div>
                    <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.SHOP_COMMENT">
                        <div class="divStar">
                            <a id="shopTotalScoreStarInNavi" class="picStar"></a><b id="shopTotalScoreInNavi" style="font-size: 13px" class="yellow_color"></b>
                        </div>
                    </bcgogo:hasPermission>
                    <img id="licenseImg"  style="width: 150px;"/>
                    <a class="blue_color shopData" href="shopData.do?method=toManageShopData">本店资料</a>
                </div>
                <span class="list_bottom"></span>
            </div>
        </bcgogo:hasPermission>
      <c:if test='${currPage!=\"promotions\"}'>

        <bcgogo:permissionParam  permissions="WEB.AUTOACCESSORYONLINE.RELEASE_PREBUYORDER,WEB.AUTOACCESSORYONLINE.MY_PREBUYORDER,WEB.AUTOACCESSORYONLINE.SHOPPINGCART,WEB.AUTOACCESSORYONLINE.PURCHASE.RETURN">
            <c:if test="${WEB_AUTOACCESSORYONLINE_RELEASE_PREBUYORDER || WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER || WEB_AUTOACCESSORYONLINE_SHOPPINGCART ||WEB_AUTOACCESSORYONLINE_PURCHASE_RETURN}">
                <div class="order_list">
                    <span class="list_top"></span>
                    <div class="list_body">
                        <div id="preBuyMainHref" class="parent-menu ${currPage=='preBuyMain'?'click':''}">我要买配件</div>
                        <ul class="shop_list">
                            <c:if test="${WEB_AUTOACCESSORYONLINE_RELEASE_PREBUYORDER}">
                                <li toURL="preBuyOrder.do?method=createPreBuyOrder" class="${currPage=='preBuyOrder'?'click':''}">发布求购</li>
                            </c:if>
                            <c:if test="${WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER}">
                                <li toURL="preBuyOrder.do?method=preBuyOrderManage" class="${currPage=='preBuyOrderManage'?'click':''}">我的求购</li>
                            </c:if>
                            <c:if test="${WEB_AUTOACCESSORYONLINE_SHOPPINGCART}">
                                <li toURL="shoppingCart.do?method=shoppingCartManage" class="${currPage=='shoppingCart'?'click':''}">购物车</li>
                            </c:if>
                        </ul>
                    </div>
                    <span class="list_bottom"></span>
                </div>
            </c:if>
        </bcgogo:permissionParam>

        <bcgogo:permissionParam  permissions="WEB.AUTOACCESSORYONLINE.GOODS_IN_OFF_SALES_MANAGE,WEB.AUTOACCESSORYONLINE.MY_QUOTEDPREBUYORDER,WEB.AUTOACCESSORYONLINE.RELATEDCUSTOMERSTOCK.BASE">
            <c:if test="${WEB_AUTOACCESSORYONLINE_GOODS_IN_OFF_SALES_MANAGE || WEB_AUTOACCESSORYONLINE_MY_QUOTEDPREBUYORDER || WEB_AUTOACCESSORYONLINE_RELATEDCUSTOMERSTOCK_BASE }">
                <div class="order_list">
                    <span class="list_top"></span>
                    <div class="list_body">
                        <div id="preSellMainHref" class="parent-menu ${currPage=='preSellMain'?'click':''}">我要卖配件</div>
                        <ul class="shop_list">
                            <c:if test="${WEB_AUTOACCESSORYONLINE_GOODS_IN_OFF_SALES_MANAGE}">
                                <li toURL="goodsInOffSales.do?method=toInSalingGoodsList" class="${currPage=='inSalingGoodsList'?'click':''}">上架管理</li>
                            </c:if>
                            <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.PROMOTIONS_MANAGER_RENDER">
                                <li toURL="promotions.do?method=toPromotionsList" class="${biMenu=='null'?'click':''}">促销管理</li>
                                <li class="shop_listNext">
                                    <ul>
                                        <li toURL="promotions.do?method=toPromotionsManager" class="${biMenu=='promotionManager'?'click':''}">创建促销</li>
                                    </ul>
                                </li>
                            </bcgogo:hasPermission>
                            <c:if test="${WEB_AUTOACCESSORYONLINE_MY_QUOTEDPREBUYORDER}">
                                <li toURL="preBuyOrder.do?method=quotedPreBuyOrderManage" class="${currPage=='quotedPreBuyOrderManage'?'click':''}">我的报价</li>
                            </c:if>
                            <c:if test="${WEB_AUTOACCESSORYONLINE_RELATEDCUSTOMERSTOCK_BASE}">
                                <li toURL="autoAccessoryOnline.do?method=toRelatedCustomerStock">客户库存</li>
                            </c:if>
                        </ul>
                    </div>
                    <span class="list_bottom"></span>
                </div>
            </c:if>
        </bcgogo:permissionParam>

        <bcgogo:permissionParam permissions="WEB.SCHEDULE.REMIND_ORDERS.SALE,WEB.SCHEDULE.REMIND_ORDERS.SALE_RETURN,
            WEB.SCHEDULE.REMIND_ORDERS.PURCHASE,WEB.SCHEDULE.REMIND_ORDERS.PURCHASE_RETURN">
            <div class="order_list">
                <span class="list_top"></span>

                <div class="list_body">
                    <div id="orderCenterHref" class="parent-menu ${currPage=='orderCenterIndex'?'click':''}">订单中心</div>
                    <ul class="shop_list">
                        <c:if test="${WEB_SCHEDULE_REMIND_ORDERS_PURCHASE}">
                            <li toURL="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS" class="${currPage=='purchase'?'click':''}">在线采购订单</li>
                        </c:if>
                        <c:if test="${WEB_SCHEDULE_REMIND_ORDERS_SALE}">
                            <li toURL="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS" class="${currPage=='sale'?'click':''}" id="mySalesOrderLink">在线销售订单</li>
                            <li class="shop_listNext">
                                <ul>
                                    <li toURL="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&currTab=NEW" id="myNewSalesOrderLink">我的新订单</li>
                                </ul>
                            </li>
                        </c:if>
                        <c:if test="${WEB_SCHEDULE_REMIND_ORDERS_PURCHASE_RETURN}">
                            <li toURL="onlinePurchaseReturnOrder.do?method=toOnlinePurchaseReturnOrder" class="${currPage=='purchaseReturn'?'click':''}">在线入库退货单</li>
                        </c:if>
                        <c:if test="${WEB_SCHEDULE_REMIND_ORDERS_SALE_RETURN}">
                            <li toURL="onlineSalesReturnOrder.do?method=toOnlineSalesReturnOrder" class="${currPage=='saleReturn'?'click':''}">在线销售退货单</li>
                        </c:if>
                    </ul>
                </div>
                <span class="list_bottom"></span>
            </div>
        </bcgogo:permissionParam>
        <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.BCGOGO_RECEIVABLE">
            <div class="order_list">
                <span class="list_top"></span>

                <div class="list_body">
                    <div id="bcgogoOrderCenterHref" class="parent-menu ${currPage=='bcgogoOrderCenterIndex'?'click':''}">一发供求专区</div>
                </div>
                <span class="list_bottom"></span>
            </div>
        </bcgogo:hasPermission>
      </c:if>

    </div>
</div>