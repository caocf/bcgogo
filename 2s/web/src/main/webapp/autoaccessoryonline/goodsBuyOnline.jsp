<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>商品采购</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cuxiao<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/minPageAJAX<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/goodsBuyOnline<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.PURCHASE_MANAGE.PURCHASE");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");

        var userGuideParam = '${param.currentStepName}';
        userGuide.currentPage = 'goodsBuyOnline';
        $(document).ready(function(){
            if(userGuideParam == "PRODUCT_PRICE_GUIDE_PURCHASE"){
                userGuide.funLib["PRODUCT_PRICE_GUIDE"]["_PRODUCT_PRICE_GUIDE_PURCHASE_STEP3_1"]();
            }
        });

    </script>
</head>

<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
<jsp:include page="autoAccessoryOnlineNavi.jsp">
    <jsp:param name="currPage" value="goodsBuyOnline"/>
</jsp:include>
<div class="clear"></div>
<div class="shoppingCart">
<form:form commandName="orderFormBean" id="purchaseOrdersForm" action="RFbuy.do?method=savePurchaseOrderOnline" method="post">
    <input type="hidden" name="returnLinkType" value="${returnLinkType}"/>
    <c:forEach var="purchaseOrderDTO" items="${orderFormBean.purchaseOrderDTOs}" varStatus="orderStatus">
        <div class="J-OrderBlock J-OrderBlock-${purchaseOrderDTO.supplierShopId}">
            <input type="hidden" class="J-Index" value="${orderStatus.index}">
            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].supplier"/>
            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].supplierId"/>
            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].supplierShopId"/>
            <div class="cartTop"></div>
            <div class="cartBody">
                <div class="divTit" style="width:280px;" >
                    &nbsp;&nbsp;供应商：
                    <a style="float: none" class="J-supplierDisabledWarningLink-${purchaseOrderDTO.supplierId} ${'DISABLED' eq purchaseOrderDTO.supplierStatus?'J-showAlert gray_color':'J-goSupplier blue_color'} ellipsis" data-supplierid="${purchaseOrderDTO.supplierId}"
                       title="${purchaseOrderDTO.supplier}" style="margin:0;width:140px;">${purchaseOrderDTO.supplier}</a>
                    <%--<div class="alert" style="margin-left:40px;margin-top:-5px;display: none;">--%>
                        <%--<a class="arrowTop" style="margin-left:50px;"></a>--%>
                        <%--<div class="alertAll">--%>
                            <%--<div class="alertLeft"></div>--%>
                            <%--<div class="alertBody">--%>
                                <%--供应商信息已失效，无法查看！--%>
                            <%--</div>--%>
                            <%--<div class="alertRight"></div>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <a href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${purchaseOrderDTO.supplierShopId}" >
                        <img src="images/icon_online_shop.png" alt="在线商铺" style="vertical-align:middle;margin-bottom:3px;">
                    </a>
                    <a class="J_QQ" data_qq="${purchaseOrderDTO.qqArray}" style="vertical-align:middle;"></a>

                    <%--<a class="Non-associated J-supplierRelationWarningLink-${purchaseOrderDTO.supplierId}" style="display: ${empty purchaseOrderDTO.supplierShopId?'':'none'}">非关联</a>--%>
                </div>
                <div class="divTit" style="width:100px">联系人：<span>${empty purchaseOrderDTO.contact ? "--": purchaseOrderDTO.contact}</span> <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].contact"  cssClass="txt" maxlength="20" cssStyle="width:60px;"/></div>
                <div class="divTit" style="width:160px">联系方式：<span>${empty purchaseOrderDTO.mobile ? "--": purchaseOrderDTO.mobile}</span> <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].mobile" cssClass="txt" maxlength="11" cssStyle="width:70px;"/></div>
                <div class="divTit" style="width:200px">所属区域：<span>${empty purchaseOrderDTO.areaInfo ? "--":purchaseOrderDTO.areaInfo}</span>
                    <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].province"/>
                    <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].city"/>
                    <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].region"/>
                    <%--<select id="purchaseOrderDTOs${orderStatus.index}.province" name="purchaseOrderDTOs[${orderStatus.index}].province"--%>
                            <%--initValue="${purchaseOrderDTO.province}" class="txt select J_province" style="width: 65px">--%>
                    <%--</select> <select id="purchaseOrderDTOs${orderStatus.index}.city" name="purchaseOrderDTOs[${orderStatus.index}].city"--%>
                            <%--initValue="${purchaseOrderDTO.city}" class="txt select J_city" style="width: 65px">--%>
                    <%--</select>--%>
                    <%--<select id="purchaseOrderDTOs${orderStatus.index}.region" name="purchaseOrderDTOs[${orderStatus.index}].region"--%>
                            <%--initValue="${purchaseOrderDTO.region}" class="txt select J_region" style="width: 65px">--%>
                    <%--</select>--%>

                    <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].address"  cssClass="txt J_address_input" maxlength="50" cssStyle="width: 120px"/>
                </div>
                <c:if test="${!purchaseOrderDTO.fromQuotedPreBuyOrder}">
                <div class="divTit" style="float: right; margin-right: 35px; margin-top: 5px;" >
                    <a class="addProduct blue_color J-addProduct">添加商品</a>
                </div>
                </c:if>
                <table cellpadding="0" cellspacing="0" class="tabCart J-ProductTable J-purchaseOnlineTable">
                    <col>
                    <col width="120">
                    <col width="80">
                    <col width="100">
                    <col width="80">
                    <col width="40">
                    <tr class="titleBg">
                        <td style="padding-left:10px;">商品信息</td>
                        <td>卖家报价（元）</td>
                        <td>采购量</td>
                        <td>促销优惠</td>
                        <td>小计（元）</td>
                        <td>操作</td>
                    </tr>
                    <tr class="space"><td colspan="5"></td></tr>
                    <c:forEach var="itemDTO" items="${purchaseOrderDTO.itemDTOs}" varStatus="itemStatus">
                        <tr class="item bg J-ItemBody">
                            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].productName"/>
                            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].brand"/>
                            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].spec"/>
                            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].model"/>
                            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].vehicleBrand"/>
                            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].vehicleModel"/>
                            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].supplierProductId"/>
                            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].unit"/>
                            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].shoppingCartItemId"/>
                            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].quotedPreBuyOrderItemId"/>
                            <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].promotionsId" cssClass="j_promotions_id"/>
                            <input type="hidden" name="inSalesAmount" value="${itemDTO.inSalesAmount}"/>
                            <td style="padding-left:10px;">
                                <div style="float: left">
                                    <div class="J-productNotInSalesWarningDiv-${itemDTO.supplierProductId}" style="margin: 10px 0px;float:left;display: ${'NotInSales' eq itemDTO.supplierProductSalesStatus?'':'none'}">
                                        <a class="ban J-showAlert" style="margin: 2px 5px 0 0;"></a>
                                        <div class="alert" style="display: none">
                                            <a class="arrowTop"></a>
                                            <div class="alertAll">
                                                <div class="alertLeft"></div>
                                                <div class="alertBody">
                                                    商品已下架
                                                </div>
                                                <div class="alertRight"></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div style="margin: 10px 0;float: left">
                                        <a style="width: 60px;height: 60px;cursor: pointer">
                                            <img src="${itemDTO.imageCenterDTO.productListSmallImageDetailDTO.imageURL}" style="width: 60px;height: 60px">
                                        </a>
                                    </div>
                                    <div style="width: 430px;margin: 10px 5px;float: left">
                                        ${itemDTO.commodityCode}
                                        <span style='font-weight: bold;'>
                                        ${itemDTO.productName}
                                        ${itemDTO.brand}
                                       </span>
                                        ${itemDTO.spec}
                                        ${itemDTO.model}
                                        ${itemDTO.vehicleBrand}
                                        ${itemDTO.vehicleModel}
                                    </div>
                                </div>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${!purchaseOrderDTO.fromQuotedPreBuyOrder}">
                                        <div style="float: left;">
                                            <div style="float: left">
                                                <a class="J-oldPrice" style="color: #848484">${itemDTO.quotedPrice}</a>&nbsp;
                                                <a class="J-newPrice yellow_color"></a>
                                                <input type="hidden" class="purchasePrice" value="${itemDTO.quotedPrice}" />
                                                <input type="hidden" class="bargainSaveValue" value="0"/>
                                                <span class="save J-SaveInfo" style="display: none">
                                                    <div class="saveLeft"></div>
                                                    <div class="saveBody J-SaveValue"> </div>
                                                    <div class="saveRight"></div>
                                                </span>
                                            </div>
                                        </div>
                                        <div style="float: right;margin-left: 2px;">
                                            <span class="J-customPriceInput" style="display: none">
                                                <form:input path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].price" cssClass="J-customPriceValue txt" cssStyle="width: 50px;height: 16px;" maxlength="7"/>
                                                <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].customPriceFlag" value="false"/>
                                            </span>
                                            <a class="yiPrice J-showAlert J-customPrice"></a>
                                            <div class="alert" style="margin: -5px 0px 0px -5px; display: none;">
                                                <a class="arrowTop" style="margin-left:10px;"></a>
                                                <div class="alertAll">
                                                    <div class="alertLeft"></div>
                                                    <div class="alertBody"> 期望价</div>
                                                    <div class="alertRight"></div>
                                                </div>
                                            </div>
                                            <a class="yiPriceBtn J-cancelCustomPrice" style="display:none">取消</a>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div style="float: left;">
                                            <div style="float: right">
                                                <a class="yellow_color" style="float: left;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></a>
                                                <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].price" cssClass="txt" cssStyle="width: 50px;height: 16px;" maxlength="7"/>
                                                <form:hidden path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].customPriceFlag" value="true"/>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                            </td>
                            <td><form:input path="purchaseOrderDTOs[${orderStatus.index}].itemDTOs[${itemStatus.index}].amount" maxlength="6" cssClass="txt J-ModifyAmount" cssStyle="width:50px; height:16px;"/>&nbsp;${itemDTO.unit}</td>
                            <td class="promotions_info_td"></td>
                            <td><b class="yellow_color J-ItemTotal"><fmt:formatNumber value="${itemDTO.total}" pattern="#.##"/></b></td>
                            <td><a class="blue_col J-deleteItemRow">删除</a></td>
                        </tr>
                    </c:forEach>
                    <tr class="J-ItemBottom">
                        <td colspan="3">
                            <span class="buyRemark" style="vertical-align:top;">采购备注：</span>
                            <form:textarea path="purchaseOrderDTOs[${orderStatus.index}].memo" cssClass="txt" maxlength="500"/>
                        </td>
                        <td colspan="3">
                            <c:if test="${!purchaseOrderDTO.fromQuotedPreBuyOrder}">
                            <div class="shopInfo">
                                <div class="infoName" style="font-size: 13">优惠明细：</div>
                                <div class="bargainDiv" style="display: none">
                                    <span>特价商品：</span>
                                    优惠<span class="arialFont">&yen;</span> <span class="promotions_total" id="bargainSpan">0</span>
                                </div>
                                <div class="mljDiv" style="display: none">
                                    <span>满立减：</span>
                                    优惠<span class="arialFont">&yen;</span> <span class="promotions_total" id="mljSpan">0</span>
                                </div>
                                <div class="mjsDiv" style="display: none">
                                    <span>满就送：</span>
                                    <span id="mjsSpan"></span>
                                </div>
                                <div class="freeShippingDiv" style="display: none">
                                    <span id="freeShipping">送货上门</span>
                                </div>


                                <b class="almost">
                                    优惠总计:<span class="arialFont">&yen;</span>
                                    <span class="green_color" id="promotionsTotalSpan">0</span> 元
                                </b>
                            </div>
                            </c:if>
                            <div style="float: right;">
                                合计：¥<span class="yellow_color J-OrderTotal"><fmt:formatNumber value="${purchaseOrderDTO.total}" pattern="#.##"/></span>
                                <c:set var="allTotal" value="${(allTotal==null?0:allTotal)+purchaseOrderDTO.total}"/>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <span class="buyRemark">期望交货日期：<span class="red_color">*</span></span>
                            <form:input path="purchaseOrderDTOs[${orderStatus.index}].deliveryDateStr" readonly="true" cssClass="J-DeliveryDate txt"/>
                        </td>
                        <td colspan="4"><a class="blue_col J-cancelOrder" style="float:right;padding-right:5px;">取消订单</a></td>
                    </tr>
                </table>
            </div>
            <div class="cartBottom"></div>
            <div class="clear i_height"></div>
        </div>
    </c:forEach>
</form:form>
<c:if test="${fn:length(orderFormBean.purchaseOrderDTOs)>1}">
    <div class="J-floatHeightDiv" style="clear: both;height: 45px"></div>
</c:if>
<div class="order J-floatDiv" style="${fn:length(orderFormBean.purchaseOrderDTOs)>1?'position:fixed;bottom:0px':'position:static;bottom:0px'}">
    <c:choose>
        <c:when test="${returnLinkType=='CommodityQuotations'}">
            <a class="blue_col" href="autoAccessoryOnline.do?method=toCommodityQuotations" style=" padding-top:7px; position:absolute;">返回配件报价</a>
        </c:when>
        <c:otherwise>
            <a class="blue_color" href="shoppingCart.do?method=shoppingCartManage" style=" padding-top:7px; position:absolute;">返回购物车</a>
        </c:otherwise>
    </c:choose>
    <div style="float:right;">
        <a id="submitOrderBtn" class="orderSubmitBtn"></a>
    </div>
    <div class="priceTotal" style="padding: 6px;">
        所有采购单合计：<span class="yellow_color">¥<b class="J-AllTotal"><fmt:formatNumber value="${allTotal}" pattern="#.##"/></b></span>
    </div>
</div>
</div>
</div>

<div class="alertMain J-PurchaseOrderOnlineCreate" id="selectProductDialog" title="添加配件查询" style="display:none">
    <div class="titBody" style="height:100px;">
        <div class="titLine" style="width:705px;">配件查询</div>
        <div class="titBodys" style="height:60px;">
            <form id="wholeSalerStockSearchForm" action="autoAccessoryOnline.do?method=getCommodityQuotationsList" method="post" name="thisform">
                <input type="hidden" id="shopId" name="shopId" />
                <input type="hidden" id="fromSource" name="fromSource" value="goodsBuy"/>
                <input type="hidden" name="maxRows" id="maxRows" value="10">
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="searchWord" name="searchWord" searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆" style="width:175px;"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productName" name="productName" searchField="product_name" initialValue="品名"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productBrand" name="productBrand" searchField="product_brand" initialValue="品牌/产地"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productSpec" name="productSpec" searchField="product_spec" initialValue="规格"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productModel" name="productModel" searchField="product_model" initialValue="型号"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model" initialValue="车型"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="commodityCode" name="commodityCode" searchField="commodity_code" initialValue="商品编号" style="text-transform: uppercase;"/></div>
                <div class="alertDivTit">
                    <a class="btnCheck" id="clearConditionBtn">清空条件</a>
                    <a class="btnCheck" style="margin-right: 10px;" id="searchQuotationBtn">查&nbsp;询</a>
                </div>
            </form>
        </div>
    </div>
    <div class="record">共有配件&nbsp;<span class="J-totalNumber">0</span>&nbsp;条记录</div>
    <table cellpadding="0" cellspacing="0" class="tabRecord" id="commodityTable">
        <col width="70">
        <col />
        <col width="90" />
        <col width="90" />
        <tr class="tabTitle">
            <td><input type="checkbox" id="checkAll" class="chk" />全选</td>
            <td>配件名</td>
            <td>库存量</td>
            <td>上架售价</td>
        </tr>
    </table>
    <!--分页-->
    <div class="stockPager">
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="autoAccessoryOnline.do?method=getCommodityQuotationsList"></jsp:param>
            <jsp:param name="data" value="{startPageNo:1,maxRows:15}"></jsp:param>
            <jsp:param name="jsHandleJson" value="drawCommodityQuotationsTable"></jsp:param>
            <jsp:param name="dynamical" value="commodityQuotations"></jsp:param>
            <jsp:param name="pageType" value="minPage"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
    </div>
    <!--分页结束-->
    <div class="height"></div>
    <div class="button"><a class="btnSure" id="selectProductConfirmBtn">确&nbsp;定</a><a class="btnSure" id="selectProductCloseBtn">取&nbsp;消</a></div>
</div>
<div id="deliveryDateDialog" style="display:none;" class="alertMain">
    <div style="margin-top: 10px;">
        <span style="font-size: 14px;">您没有输入期望交货日期，请输入：</span>
        <input id="deliveryDateInput" type="text" class="text"/>
        <input id="btnType" type="hidden" />
    </div>
    <div class="button" style="width:100%;margin-top: 10px;">
        <a id="confirmBtn1" class="btnSure" href="javascript:;">确 定</a>
        <a id="cancleBtn" class="btnSure" href="javascript:;">取消</a>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>