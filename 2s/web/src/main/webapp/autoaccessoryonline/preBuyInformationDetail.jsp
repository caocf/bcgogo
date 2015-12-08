<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>求购资讯</title>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css">


    <script type="text/javascript" src="js/page/autoaccessoryonline/quotedPreBuyOrder<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>

    <bcgogo:permissionParam permissions="WEB.AUTOACCESSORYONLINE.ADD_QUOTEDPREBUYORDER">
        <c:set var="addQuotedPreBuyOrderPerm" value="${WEB_AUTOACCESSORYONLINE_ADD_QUOTEDPREBUYORDER}"/>
    </bcgogo:permissionParam>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB_AUTOACCESSORYONLINE_BUYING_INFORMATION");
    </script>
</head>
<body>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <jsp:include page="autoAccessoryOnlineNavi.jsp">
        <jsp:param name="currPage" value="preBuyDetail"/>
    </jsp:include>
    <div class="titBody">
        <div class="bodyLeft">
            <div class="cuSearch">
                <div class="lineTitle">
                    商品求购详情
                </div>
                <div class="cartBody lineBody">
                    <div class="divTit_left">
                        <div class="divTit" style="padding:0;width: 600px">
                            <span class="spanName">标题：</span>
                            <div class="divTit" style="padding:0;">
                                <div>${preBuyOrderDTO.title}</div>
                                <div style="color: #999999">
                                    （本次共求购&nbsp;<b class="red_color">${fn:length(preBuyOrderDTO.itemDTOs)}</b>&nbsp;种商品）</span>&nbsp;
                                </div>
                            </div>
                        </div>
                        <div class="divTit" style="padding:0;">
                            <span class="spanName">所在地：</span>
                            ${customerDTO.areaInfo}
                        </div>
                    </div>

                    <div class="divTit_right">
                        <div class="divTit countDown" style="padding:0;"><a class="clock"></a>距信息失效仅剩&nbsp;<b class="red_color">${preBuyOrderDTO.endDateCount}天</b></div>
                        <div class="divTit gray_color" style="padding:0;">
                            <span class="spanName">截止：</span>
                            ${preBuyOrderDTO.endDateStr}
                        </div>
                        <div class="divTit gray_color" style="padding:0;">
                            <span class="spanName">发布日期：</span>
                            ${preBuyOrderDTO.vestDateStr}
                        </div>
                    </div>
                    <div class="height"></div>
                    <h3>求购商品信息：</h3>
                    <c:forEach var="itemDTO" items="${preBuyOrderDTO.itemDTOs}" varStatus="status">
                        <div class="div_titBody">
                            <div class="titBody_Bg">
                                <div class="shop_line">
                                    <label class="shop_name">求购商品：</label>
                                <span class="yellow_color">
                                    ${itemDTO.commodityCode}
                                    ${itemDTO.productName}
                                    ${itemDTO.brand}
                                    ${itemDTO.spec}
                                    ${itemDTO.model}
                                    ${itemDTO.vehicleBrand}
                                    ${itemDTO.vehicleModel}
                                </span>
                                </div>
                                <div class="shop_line">
                                    <label class="shop_name">求购量：</label>
                                    <span>${itemDTO.amount}&nbsp;${itemDTO.unit}</span>
                                </div>
                                <div class="shop_line">
                                    <label class="shop_name">描述：</label>
                                    <span>${empty itemDTO.memo?"无":itemDTO.memo}</span>
                                </div>
                                <div class="shop_right">
                                    <label style="color: #999999">（已有&nbsp;<b class="red_color">${empty itemDTO.quotedCount?"0":itemDTO.quotedCount}</b>&nbsp;家卖家参与报价）</label>
                                    <c:choose>
                                        <c:when test="${ !empty itemDTO.myQuotedPreBuyOrderItemDTO}">
                                            <a class="button_already">我已报价</a>
                                        </c:when>
                                        <c:otherwise>
                                            <c:if test="${addQuotedPreBuyOrderPerm}">
                                            <span>
                                                <a class="button_unquoted J_QuotedStepFirst" data-prebuyorderitemid="${itemDTO.id}">我要报价</a>
                                                <input type="hidden" id="itemDTOs[${status.index}].productName" name="itemDTOs[${status.index}].productName" value="${itemDTO.productName}"/>
                                                <input type="hidden" id="itemDTOs[${status.index}].productBrand" name="itemDTOs[${status.index}].productName" value="${itemDTO.brand}"/>
                                                <input type="hidden" id="itemDTOs[${status.index}].productSpec" name="itemDTOs[${status.index}].productName" value="${itemDTO.spec}"/>
                                                <input type="hidden" id="itemDTOs[${status.index}].productModel" name="itemDTOs[${status.index}].productName" value="${itemDTO.model}"/>
                                                <input type="hidden" id="itemDTOs[${status.index}].productVehicleBrand" name="itemDTOs[${status.index}].productName" value="${itemDTO.vehicleBrand}"/>
                                                <input type="hidden" id="itemDTOs[${status.index}].productVehicleModel" name="itemDTOs[${status.index}].productName" value="${itemDTO.vehicleModel}"/>
                                                <input type="hidden" id="itemDTOs[${status.index}].commodityCode" name="itemDTOs[${status.index}].productName" value="${itemDTO.commodityCode}"/>
                                            </span>

                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <c:if test="${!empty itemDTO.myQuotedPreBuyOrderItemDTO}">
                                    <c:set var="quotedItemDTO" value="${itemDTO.myQuotedPreBuyOrderItemDTO}"/>
                                    <div class="offerInfo">
                                        <h4 class="clear offerTitle">我的报价信息</h4>
                                        <div class="shop_line">
                                            <label class="shop_name" style="width:95px;">对应商品：</label>
                                        <span>
                                            ${quotedItemDTO.commodityCode}
                                            ${quotedItemDTO.productName}
                                            ${quotedItemDTO.brand}
                                            ${quotedItemDTO.spec}
                                            ${quotedItemDTO.model}
                                            ${quotedItemDTO.vehicleBrand}
                                            ${quotedItemDTO.vehicleModel}
                                        </span>
                                        </div>
                                        <div class="shop_line">
                                            <label class="shop_name" style="width:95px;">我的报价：</label>
                                            <span><b class="yellow_color">${quotedItemDTO.price}</b>&nbsp;元/${quotedItemDTO.unit}（${quotedItemDTO.includingTax=="TRUE"?"含税、":""}<c:if test="${not empty quotedItemDTO.shippingMethod}">${quotedItemDTO.shippingMethod.name}、</c:if>下单后${quotedItemDTO.arrivalTime}天到货）</span>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                            <div class="titBottom_Bg"></div>
                        </div>
                        <div class="height"></div>
                    </c:forEach>

                </div>
                <div class="lineBottom"></div>

                <div class="clear i_height"></div>
                <div class="divTit button_conditon button_search" style="width:95px;">
                    <a class="button"href="preBuyOrder.do?method=preBuyInformation">返回列表</a>
                </div>

            </div>
        </div>
        <div class="bodyRight">
            <div class="titles buyingInfo_titles">
                <div class="leftTit">买家信息</div>
                <div class="list"><b class="yellow_color">${customerDTO.name}</b></div>
                <div class="list">
                    <span class="contactName" style="float: left">联系人：</span>
                    <div class="nameList" id="contactInfoDiv">
                        <div><span>${empty customerDTO.contact?"暂无信息":customerDTO.contact}</span>&nbsp;<span>${empty customerDTO.mobile?"暂无信息":customerDTO.mobile}</span></div>
                    </div>
                </div>
                <div class="list"><span class="contactName">座机：</span><span id="landLineSpan">${empty customerDTO.landLine?"暂无信息":customerDTO.landLine}</span></div>
                <div class="list"><span class="contactName">传真：</span><span id="faxSpan">${empty customerDTO.fax?"暂无信息":customerDTO.fax}</span></div>
                <div class="list"><span class="contactName">Email：</span><span id="emailSpan">${empty customerDTO.email?"暂无信息":customerDTO.email}</span></div>
                <div class="list"><span class="contactName">经营产品：</span><span id="businessScopeSpan">${empty customerDTO.businessScopeStr?"暂无信息":customerDTO.businessScopeStr}</span></div>
                <div id="relationOperationDiv">
                    <c:choose>
                        <c:when test="${'UN_APPLY_RELATED' eq relationMidStatus}">
                            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_APPLY_ACTION">
                                <div class="list"><a class="button_associate" id="applyCustomerRelationBtn" data-customershopid="${customerDTO.customerShopId}">我要关联</a></div>
                                <div class="list"><span style="color: #999999">关联后可查看买家信息</span></div>
                            </bcgogo:hasPermission>
                        </c:when>
                        <c:when test="${'APPLY_RELATED' eq relationMidStatus}">
                            <input type="hidden" id="relationMidStatus" value="${relationMidStatus}"/>
                            <div class="list"><span style="color: #999999">已申请关联</span></div>
                            <div class="list"><span style="color: #999999">关联后可查看买家信息</span></div>
                        </c:when>
                        <c:when test="${'BE_APPLY_RELATED' eq relationMidStatus}">
                            <div class="list"><a class="button_associate" id="acceptCustomerApplyBtn" data-inviteId="${inviteId}" >同意关联</a></div>
                            <div class="list"><span style="color: #999999">关联后可查看买家信息</span></div>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" id="relationMidStatus" value="${relationMidStatus}"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <c:if test="${allValidPreBuyOrderItemsCount>1}">
                    <div class="list">该买家共有&nbsp;<b class="red_color">${allValidPreBuyOrderItemsCount}</b>&nbsp;条求购信息</div>
                    <div class="list"><a class="blue_color" href="preBuyOrder.do?method=preBuyInformation&shopId=${customerDTO.customerShopId}">点击查看</a></div>
                </c:if>
            </div>
            <div class="titles">
                <div class="leftTit">我是卖家</div>
                <div class="list"><span class="questionMark" style="cursor:default">如何参与报价</span></div>
                <div class="list"><a class="titNum">1</a><span>查看您感兴趣的求购信息</span></div>
                <div class="list"><a class="titNum">2</a><span>提交您的报价信息</span></div>
                <div class="list"><a class="titNum">3</a><span>获得买家联系方式并与其交易</span></div>
            </div>
            <div class="titles">
                <div class="leftTit">我是买家</div>
                <div class="list"><span class="questionMark" style="cursor:default">如何快速采购</span></div>
                <div class="list"><a class="titNum">1</a><span>发布求购信息</span></div>
                <div class="list"><a class="titNum">2</a><span>等待卖家报价</span></div>
                <div class="list"><a class="titNum">3</a><span>选择卖家报价直接采购下单</span></div>
            </div>
        </div>
        <div class="height"></div>
        <!----------------------------页脚----------------------------------->
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>

<input type="hidden" id="currentPreBuyOrderId" value="${preBuyOrderDTO.id}">
<%--<div class="alertMain newCustomers offerMain" id="selectQuotedProductDiv" title="我要报价——选择我的商品" style="display:none">--%>
    <%--<div class="step">--%>
        <%--<label class="title">报价流程：</label>--%>
        <%--<div class="stepLeft"></div>--%>
        <%--<div class="stepBody">--%>
            <%--<span class="hover">1、选择我的商品</span>--%>
            <%--<a class="stepImg"></a>--%>
            <%--<span>2、填写我的价格并提交报价</span>--%>
            <%--<a class="stepImg"></a>--%>
            <%--<span>3、完成</span>--%>
        <%--</div>--%>
        <%--<div class="stepRight"></div>--%>
    <%--</div>--%>
    <%--<div class="height"></div>--%>
    <%--<div class="line yellow_color">请在您的上架商品中选择与求购商品对应的商品！</div>--%>
    <%--<div class="offer_cuSearch">--%>
        <%--<div class="cartTop"></div>--%>
        <%--<div class="cartBody">--%>
            <%--<form id="searchConditionForm" action="preBuyOrder.do?method=quotedSelectProduct" method="post">--%>
            <%--<input type="hidden" name="maxRows" id="maxRows" value="15">--%>
                <%--<div class="divTit">--%>
                    <%--<span class="spanName">上架商品查询</span>--%>
                    <%--<div class="conditionList">--%>
                        <%--<input class="txt J-productSuggestion J-initialCss J_clear_input" id="searchWord" name="searchWord" searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆" style="width:330px;"/>--%>
                        <%--<br>--%>
                        <%--<input class="txt J-productSuggestion J-initialCss J_clear_input" id="productName" name="productName" searchField="product_name" initialValue="品名" style="width:70px;"/>&nbsp;--%>
                        <%--<input class="txt J-productSuggestion J-initialCss J_clear_input" id="productBrand" name="productBrand" searchField="product_brand" initialValue="品牌/产地" style="width:70px;"/>&nbsp;--%>
                        <%--<input class="txt J-productSuggestion J-initialCss J_clear_input" id="productSpec" name="productSpec" searchField="product_spec" initialValue="规格" style="width:70px;"/>&nbsp;--%>
                        <%--<input class="txt J-productSuggestion J-initialCss J_clear_input" id="productModel" name="productModel" searchField="product_model" initialValue="型号" style="width:70px;"/>&nbsp;--%>
                        <%--<input class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌" style="width:70px;"/>&nbsp;--%>
                        <%--<input class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model" initialValue="车型" style="width:70px;"/>&nbsp;--%>
                        <%--<input class="txt J-productSuggestion J-initialCss J_clear_input" id="commodityCode" name="commodityCode" searchField="commodity_code" initialValue="商品编号" style="text-transform: uppercase;width:70px;"/>&nbsp;--%>
                    <%--</div>--%>

                <%--</div>--%>
                <%--<div class="button_search">--%>
                    <%--<a class="blue_color clean" id="clearConditionBtn">清空条件</a>--%>
                    <%--<a class="button" id="searchConditionBtn">查&nbsp;询</a>--%>
                <%--</div>--%>
            <%--</form>--%>

            <%--<div class="height"></div>--%>
            <%--<table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="inSalesProductTable">--%>
                <%--<col width="30">--%>
                <%--<col>--%>
                <%--<col width="90">--%>
                <%--<col width="90">--%>
                <%--<col width="90">--%>
                <%--<tr class="titleBg J_title">--%>
                    <%--<td></td>--%>
                    <%--<td>上架商品信息</td>--%>
                    <%--<td>库存量</td>--%>
                    <%--<td>平均成本</td>--%>
                    <%--<td>批发价</td>--%>
                <%--</tr>--%>
                <%--<tr class="space J_title"><td colspan="5"></td></tr>--%>
            <%--</table>--%>
            <%--<div class="clear i_height"></div>--%>
            <%--<!----------------------------分页----------------------------------->--%>
            <%--<jsp:include page="/common/pageAJAX.jsp">--%>
                <%--<jsp:param name="url" value="preBuyOrder.do?method=quotedSelectProduct"></jsp:param>--%>
                <%--<jsp:param name="jsHandleJson" value="drawProductTable"></jsp:param>--%>
                <%--<jsp:param name="dynamical" value="InSalesProduct"></jsp:param>--%>
                <%--<jsp:param name="display" value="none"></jsp:param>--%>
            <%--</jsp:include>--%>
        <%--</div>--%>
        <%--<div class="cartBottom"></div>--%>
    <%--</div>--%>
    <%--<div class="height"></div>--%>
    <%--<div class="button button_step">--%>
        <%--<a class="btnSure" id="quotedFirstNextBtn">下一步</a>--%>
    <%--</div>--%>
<%--</div>--%>

<%--<div class="alertMain newCustomers offerMain" id="quotedPreBuyOrderDiv" title="我要报价——填写我的价格并提交报价" style="display:none">--%>
    <%--<div class="step">--%>
        <%--<label class="title">报价流程：</label>--%>
        <%--<div class="stepLeft"></div>--%>
        <%--<div class="stepBody">--%>
            <%--<span>1、选择我的商品</span>--%>
            <%--<a class="stepImg"></a>--%>
            <%--<span class="hover">2、填写我的价格并提交报价</span>--%>
            <%--<a class="stepImg"></a>--%>
            <%--<span>3、完成</span>--%>
        <%--</div>--%>
        <%--<div class="stepRight"></div>--%>
    <%--</div>--%>
    <%--<div class="height"></div>--%>
    <%--<div class="line yellow_color">请输入您的报价信息！</div>--%>
    <%--<form action="preBuyOrder.do?method=saveQuotedPreBuyOrder" id="quotedPreBuyOrderForm" method="post">--%>
        <%--<input type="hidden" id="preBuyOrderId" name="preBuyOrderId">--%>
        <%--<input type="hidden" id="preBuyOrderItemId" name="preBuyOrderItemId">--%>
        <%--<input type="hidden" id="productId" name="productId">--%>
        <%--<input type="hidden" id="unit" name="unit">--%>
        <%--<div class="offer_cuSearch">--%>
            <%--<div class="cartTop"></div>--%>
            <%--<div class="cartBody">--%>
                <%--<div class="divTit">--%>
                    <%--<span class="spanName">商品信息：</span>--%>
                    <%--<span class="spanContent" id="productInfoSpan"></span>--%>
                    <%--<span class="spanName">库存量：</span>--%>
                    <%--<span class="spanContent" id="inventoryInfoSpan"></span>--%>
                <%--</div>--%>
                <%--<div class="divTit">--%>
                    <%--<span class="spanName">平均成本：</span>--%>
                    <%--<span class="spanContent"><span class="arialFont">&yen;</span><span id="inventoryAveragePriceSpan"></span><a class="gray_color" style="color: #999999;">（不会给买家展示）</a></span>--%>
                    <%--<span class="spanName">批发价：</span>--%>
                    <%--<span class="spanContent"><span class="arialFont">&yen;</span><span id="tradePriceSpan"></span></span>--%>
                <%--</div>--%>
                <%--<div class="height"></div>--%>
                <%--<div class="divTit offer_divTit">--%>
                    <%--<span class="spanName">我的报价：</span>--%>
                    <%--<span class="spanContent"><input type="text" style="color: #000000;" class="txt J_QuotedPrice" id="price" name="price" />&nbsp;元&nbsp;--%>
                        <%--（说明：此价格 <label class="rad"><input type="checkbox" name="includingTax" value="TRUE" />含税</label>&nbsp;--%>
                        <%--<label class="rad"><input type="checkbox" name="shippingMethod" value="DELIVERY_HOME"/>送货上门</label>）--%>
                    <%--</span>--%>
                <%--</div>--%>
                <%--<div class="divTit offer_divTit">--%>
                    <%--<span class="spanName">到货时间：</span>--%>
                    <%--<span class="spanContent">--%>
                        <%--<label class="rad"><input type="radio" name="arrivalTimeRadio" value="1"/>一天内到货</label>&nbsp;&nbsp;--%>
                        <%--<label class="rad"><input type="radio" name="arrivalTimeRadio" value="2"/>两天内到货</label>&nbsp;&nbsp;--%>
                        <%--<label class="rad"><input type="radio" name="arrivalTimeRadio" value="3"/>三天内到货</label>&nbsp;&nbsp;--%>
                        <%--<label class="rad"><input type="radio" name="arrivalTimeRadio" value=""/></label><input type="text" class="txt J_ArrivalTime" id="arrivalTime" name="arrivalTime" style="width:40px;color: #000000;" />天内到货</span>--%>
                <%--</div>--%>
                <%--<div class="height"></div>--%>
            <%--</div>--%>
            <%--<div class="cartBottom"></div>--%>
        <%--</div>--%>
        <%--<div class="height"></div>--%>
        <%--<div class="button button_step">--%>
            <%--<a class="btnSure" id="saveQuotedBtn">提交报价</a>--%>
            <%--<a class="btnSure" id="quotedBackSelectProductBtn">返回上一步</a>--%>
        <%--</div>--%>
    <%--</form>--%>
<%--</div>--%>


<%--<div class="alertMain newCustomers offerMain" id="quotedPreBuyOrderFinishDiv" title="我要报价——完成" style="display:none">--%>
    <%--<div class="step">--%>
        <%--<label class="title">报价流程：</label>--%>
        <%--<div class="stepLeft"></div>--%>
        <%--<div class="stepBody">--%>
            <%--<span>1、选择我的商品</span>--%>
            <%--<a class="stepImg"></a>--%>
            <%--<span>2、填写我的价格并提交报价</span>--%>
            <%--<a class="stepImg"></a>--%>
            <%--<span class="hover">3、完成</span>--%>
        <%--</div>--%>
        <%--<div class="stepRight"></div>--%>
    <%--</div>--%>
    <%--<div class="height"></div>--%>
    <%--<div class="offer_cuSearch">--%>
        <%--<div class="cartTop"></div>--%>
        <%--<div class="cartBody">--%>
            <%--<div class="divTit">--%>
                <%--<span class="spanName">商品名：</span>--%>
                <%--<span class="spanContent" id="confirmProductInfoSpan"></span>--%>
                <%--<span class="spanName">库存量：</span>--%>
                <%--<span class="spanContent" id="confirmInventoryInfoSpan"></span>--%>
            <%--</div>--%>
            <%--<div class="divTit">--%>
                <%--<span class="spanName">平均成本：</span>--%>
                <%--<span class="spanContent"><span class="arialFont">&yen;</span><span id="confirmInventoryAveragePriceSpan"></span><a class="gray_color" style="color: #999999;">（不会给买家展示）</a></span>--%>
                <%--<span class="spanName">批发价：</span>--%>
                <%--<span class="spanContent"><span class="arialFont">&yen;</span><span id="confirmTradePriceSpan"></span></span>--%>
            <%--</div>--%>
            <%--<div class="divTit offer_divTit">--%>
                <%--<span class="spanName">我的报价：</span>--%>
                <%--<span class="spanContent"><span id="confirmQuotedPriceSpan"></span>&nbsp;元&nbsp;（说明：此价格 <label class="rad"><input type="checkbox" disabled="disabled" name="includingTaxCheckBox" value="TRUE"/>含税</label>&nbsp;<label class="rad"><input type="checkbox" disabled="disabled" value="DELIVERY_HOME" name="shippingMethodCheckBox"/>送货上门</label>）</span>--%>
            <%--</div>--%>
            <%--<div class="divTit offer_divTit">--%>
                <%--<span class="spanName">到货时间：</span>--%>
                <%--<span class="spanContent"><span id="confirmArrivalTimeSpan"></span>天内到货</span>--%>
            <%--</div>--%>
            <%--<div class="height"></div>--%>
        <%--</div>--%>
        <%--<div class="cartBottom"></div>--%>
        <%--<div class="height"></div>--%>
        <%--<c:if test="${allValidPreBuyOrderItemsCount>1}">--%>
        <%--<div class="line">该买家共有&nbsp;<span class="red_color">${allValidPreBuyOrderItemsCount}</span>&nbsp;条求购信息&nbsp;<a class="blue_color" href="preBuyOrder.do?method=preBuyInformation&shopId=${customerDTO.customerShopId}">点击查看</a></div>--%>
        <%--</c:if>--%>
    <%--</div>--%>
    <%--<div class="height"></div>--%>
    <%--<div class="button button_step">--%>
        <%--<a class="btnSure" id="confirmFinishBtn">确&nbsp;定</a>--%>
    <%--</div>--%>
<%--</div>--%>

<%--<div class="alertMain newCustomers offerMain" id="productFastInSalesDiv" title="我要报价——上架报价商品" style="display:none">--%>
    <%--<div class="step">--%>
        <%--<label class="title">报价流程：</label>--%>
        <%--<div class="stepLeft"></div>--%>
        <%--<div class="stepBody">--%>
            <%--<span>1、选择我的商品</span>--%>
            <%--<a class="stepImg"></a>--%>
            <%--<span class="hover">2、填写我的价格并提交报价</span>--%>
            <%--<a class="stepImg"></a>--%>
            <%--<span>3、完成</span>--%>
        <%--</div>--%>
        <%--<div class="stepRight"></div>--%>
    <%--</div>--%>
    <%--<div class="height"></div>--%>
    <%--<div class="line yellow_color">请输入您的上架商品信息！</div>--%>
    <%--<form action="goodsInOffSales.do?method=saveProductFastInSales" id="productFastInSalesForm" method="post">--%>
        <%--<input type="hidden" id="preBuyOrderId">--%>
        <%--<input type="hidden" id="preBuyOrderItemId">--%>
        <%--<input type="hidden" id="productId" name="productId">--%>
        <%--<div class="offer_cuSearch">--%>
            <%--<div class="cartTop"></div>--%>
            <%--<div class="cartBody">--%>
                <%--<div class="divTit">--%>
                    <%--<span class="spanName">商品信息：</span>--%>
                    <%--<span class="spanContent" id="productInfoSpan"></span>--%>
                    <%--<span class="spanName">库存量：</span>--%>
                    <%--<span class="spanContent" id="inventoryInfoSpan"></span>--%>
                <%--</div>--%>
                <%--<div class="divTit">--%>
                    <%--<span class="spanName">平均成本：</span>--%>
                    <%--<span class="spanContent"><span class="arialFont">&yen;</span><span id="inventoryAveragePriceSpan"></span><a class="gray_color" style="color: #999999;">（不会给买家展示）</a></span>--%>
                    <%--<span class="spanName">批发价：</span>--%>
                    <%--<span class="spanContent"><span class="arialFont">&yen;</span><span id="tradePriceSpan"></span></span>--%>
                <%--</div>--%>
                <%--<div class="height"></div>--%>

                <%--<div class="height"></div>--%>
            <%--</div>--%>
            <%--<div class="cartBottom"></div>--%>
        <%--</div>--%>
        <%--<div class="height"></div>--%>
        <%--<div class="button button_step">--%>
            <%--<a class="btnSure" id="saveProductFastInSalesBtn">确认上架</a>--%>
            <%--<a class="btnSure" id="productFastInSalesBackSelectProductBtn">重新选择商品</a>--%>
        <%--</div>--%>
    <%--</form>--%>
<%--</div>--%>

<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>