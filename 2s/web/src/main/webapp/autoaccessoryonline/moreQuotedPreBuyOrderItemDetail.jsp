<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>报价详细</title>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cuxiao<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/supplierQuotedPreBuyOrder<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER");
    </script>
</head>
<body>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody tib_show ">
        <jsp:include page="supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="preBuyOrderManage"/>
        </jsp:include>

        <div class="bodyLeft bodyCenter">
            <c:set var="quotedPreBuyOrderItemIds" value=""/>
            <c:forEach items="${preBuyOrderDTO.itemDTOs}" var="preBuyOrderItemDTO" varStatus="status">
                <c:set var="quotedPreBuyOrderItemDTO" value="${preBuyOrderItemDTO.myQuotedPreBuyOrderItemDTO}"/>
                <c:set var="productDTO" value="${quotedPreBuyOrderItemDTO.productDTO}"/>
                <c:set var="separator" value=","/>
                <c:set var="quotedPreBuyOrderItemIds" value="${quotedPreBuyOrderItemIds}${empty quotedPreBuyOrderItemIds?'':separator}${quotedPreBuyOrderItemDTO.id}"/>
                <div class="cuSearch">
                    <div class="line_description">
                        <div class="divTit" style="width: 450px">
                            <span class="spanName tb_name">我的求购商品：</span>
                                ${preBuyOrderItemDTO.commodityCode}
                                ${preBuyOrderItemDTO.productName}
                                ${preBuyOrderItemDTO.brand}
                                ${preBuyOrderItemDTO.spec}
                                ${preBuyOrderItemDTO.model}
                                ${preBuyOrderItemDTO.vehicleBrand}
                                ${preBuyOrderItemDTO.vehicleModel}
                        </div>
                        <div class="divTit_right" style="width: 150px">
                            <div class="divTit"><span class="spanName tb_name">求购量：</span>${preBuyOrderItemDTO.amount}${preBuyOrderItemDTO.unit}</div>
                        </div>
                        <div class="buying_description" style="width: 100%">
                            <span class="spanName">求购商品描述：</span>
                            <span class="descriptionList" style="width:84%;">${empty preBuyOrderItemDTO.memo?"无":preBuyOrderItemDTO.memo}</span>
                        </div>
                    </div>

                    <div class="cartBody lineBody">
                        <div class="divTit">
                            <span class="spanName tb_name">给我的报价：</span>
                            <b class="yellow_color">${quotedPreBuyOrderItemDTO.price}</b>&nbsp;元/${quotedPreBuyOrderItemDTO.unit}
                                ${quotedPreBuyOrderItemDTO.includingTax=="TRUE"?"（含税）":""}
                        </div>
                        <div class="divTit_right">
                            <div class="divTit"><span class="spanName tb_name">其他信息：</span>&nbsp;
                                <c:if test="${not empty quotedPreBuyOrderItemDTO.shippingMethod}">${quotedPreBuyOrderItemDTO.shippingMethod.name}</c:if>
                                下单后${quotedPreBuyOrderItemDTO.arrivalTime}天到货
                            </div>
                        </div>
                        <div class="tb_bao">
                            <div class="divTit" style="position:relative;">
                                <label>对应商品:
                                        ${productDTO.commodityCode}
                                        ${productDTO.name}
                                        ${productDTO.brand}
                                        ${productDTO.spec}
                                        ${productDTO.model}
                                        ${productDTO.productVehicleBrand}
                                        ${productDTO.productVehicleModel}
                                </label>
                                <c:if test="${not empty productDTO.promotionsDTO}">
                                    <div style="float: left">
                                        <span style="display:block; float:left;" class="J-showAlert cuxiaoSpan">促销</span>
                                        <div class="alert" style="margin: 24px 0 0 -22px;display: none">
                                            <div class="ti_top"></div>
                                            <div class="ti_body alertBody">
                                                <div style="font-weight:bold;">活动截止：<span style="color: #FF5113;">${productDTO.promotionsDTO.endTimeCNStr==null?"不限期":productDTO.promotionsDTO.endTimeCNStr}</span>
                                                </div>
                                                <c:forEach items="${productDTO.promotionsDTO.promotionsRuleDTOList}" var="promotionsRuleDTO">
                                                    <div>
                                                        满
                                                        <span class="green_color">${promotionsRuleDTO.minAmountStr}</span>件，单价打
                                                        <span style="color: #CB0000;">${promotionsRuleDTO.discountAmountStr}</span>折
                                                    </div>
                                                </c:forEach>
                                            </div>
                                            <div class="ti_bottom"></div>
                                        </div>
                                    </div>
                                </c:if>

                            </div>
                            <div class="divTit_right" style="width: 140px">上架单价: ${productDTO.inSalesPrice}元/${productDTO.sellUnit}</div>
                        </div>

                        <div class="clear i_height"></div>
                    </div>
                    <div class="lineBottom"></div>
                </div>
                <div class="clear i_height"></div>
            </c:forEach>

            <div class="divTit button_conditon" style="width:100%;">
                <a class="button" style="float:right;" href="preBuyOrder.do?method=showPreBuyOrderById&preBuyOrderId=${preBuyOrderDTO.id}">取&nbsp;消</a>
                <a class="button" style="float:right;" id="createPurchaseOrderBtn" data-itemids="${quotedPreBuyOrderItemIds}">直接下单</a>
            </div>
        </div>
        <div class="bodyRight">
            <div class="titles buyingInfo_titles">
                <div class="leftTit">卖家信息</div>
                <div class="list"><b class="yellow_color">${supplierDTO.name}</b></div>
                <div class="list">
                    <span class="contactName" style="float: left">联系人：</span>
                    <div class="nameList" id="contactInfoDiv">
                        <div><span>${empty supplierDTO.contact?"暂无信息":supplierDTO.contact}</span>&nbsp;<span>${empty supplierDTO.mobile?"暂无信息":supplierDTO.mobile}</span></div>
                    </div>
                </div>
                <div class="list"><span class="contactName">座机：</span><span id="landLineSpan">${empty supplierDTO.landLine?"暂无信息":supplierDTO.landLine}</span></div>
                <div class="list"><span class="contactName">传真：</span><span id="faxSpan">${empty supplierDTO.fax?"暂无信息":supplierDTO.fax}</span></div>
                <div class="list"><span class="contactName">Email：</span><span id="emailSpan">${empty supplierDTO.email?"暂无信息":supplierDTO.email}</span></div>
                <div class="list"><span class="contactName">经营产品：</span><span id="businessScopeSpan">${empty supplierDTO.businessScope?"暂无信息":supplierDTO.businessScope}</span></div>
                <div id="relationOperationDiv">
                    <c:choose>
                        <c:when test="${'UN_APPLY_RELATED' eq relationMidStatus}">
                            <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.APPLY_ACTION">
                                <div class="list"><a class="button_associate" id="applySupplierRelationBtn" data-suppliershopid="${supplierDTO.supplierShopId}">我要关联</a></div>
                                <div class="list"><span style="color: #999999">关联后可查看卖家信息</span></div>
                            </bcgogo:hasPermission>
                        </c:when>
                        <c:when test="${'APPLY_RELATED' eq relationMidStatus}">
                            <div class="list"><span style="color: #999999">已申请关联</span></div>
                            <div class="list"><span style="color: #999999">关联后可查看卖家信息</span></div>
                        </c:when>
                        <c:when test="${'BE_APPLY_RELATED' eq relationMidStatus}">
                            <div class="list"><a class="button_associate" id="acceptSupplierApplyBtn" data-inviteId="${inviteId}" >同意关联</a></div>
                            <div class="list"><span style="color: #999999">关联后可查看卖家信息</span></div>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" id="relationMidStatus" value="${relationMidStatus}"/>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <div class="height"></div>
        <!----------------------------页脚----------------------------------->
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>