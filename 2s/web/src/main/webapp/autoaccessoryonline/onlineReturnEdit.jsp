<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>在线退货</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/onlineReturnEdit<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.TXN.PURCHASE_MANAGE.RETURN");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"编辑");
    </script>
</head>

<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="pendingImg" style="display:none;margin-top: -80px;"></div>
<div class="i_main clear">
<div class="mainTitles">
    <div class="titleWords">
        在线退货
    </div>
</div>

<div class="shoppingCart copy_shoppingCart">
    <jsp:include page="../txn/unit.jsp"/>
    <input id="orderType" name="orderType" value="purchaseReturnOrder" type="hidden"/>
    <input id="orderStatus" name="orderStatus" value="SELLER_DISPATCH" type="hidden"/>
    <form:form commandName="purchaseReturnDTO" id="purchaseReturnForm"
               action="onlineReturn.do?method=saveOnlineReturnStorage" method="post" name="thisform">
        <form:hidden path="supplierId"/>
        <form:hidden path="supplierShopId"/>
        <form:hidden path="purchaseOrderId"/>
        <form:hidden path="supplier"/>
        <form:hidden path="id" value="${purchaseReturnDTO.id == null?'': purchaseReturnDTO.id}"/>
        <form:hidden path="status" value="${purchaseReturnDTO.status == null?'': purchaseReturnDTO.status}"/>
        <form:hidden path="receiptNo" value="${purchaseReturnDTO.receiptNo == null?'': purchaseReturnDTO.receiptNo}"/>
        <c:if test="${!empty result}">
            <input type="hidden" id="onlinePurchaseReturnMessage" value="${result.msg}" resultDate="${result.data}"
                   resultOperation="${result.operation}">
        </c:if>
        <c:if test="${!empty purchaseReturnDTO.id}">
            <div class="divTits">退货单号：<span class="blue_color">${purchaseReturnDTO.receiptNo}</span><a class="connect">在线</a></div>
            <div class="divTits" style="width:150px;">制单人：<span>${purchaseReturnDTO.editor}</span></div>
            <div class="divTits">退货日期：<span>${purchaseReturnDTO.vestDateStr}</span></div>
            <div class="divTits">单据状态：<b class="yellow_color" style="font-size:16px;">${empty purchaseReturnDTO.status ? "":purchaseReturnDTO.status.name}</b></div>
            <div class="divTits">卖家退货单号：<span>${purchaseReturnDTO.saleReturnReceiptNo}</span></div>
        </c:if>

        <div class="cartTop"></div>
        <div class="cartBody">
            <div class="divTit" style="width:200px;">
                供应商：<a class="blue_color J-toSupplier" title="${purchaseReturnDTO.supplier}">${purchaseReturnDTO.supplier}</a>
                <a class="icon_online_shop" target="_blank" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${supplierDTO.supplierShopId}"></a>
                <a class="J_QQ" data_qq="${supplierShop.qqArray}"></a>
            </div>
            <div data-info="properties-info" style="padding:0;margin:0;display:block;float:left;width:760px;">
                <div class="divTit">
                    联系人：<form:input path="contact" cssClass="txt" maxlength="20" style="width:60px;"/>
                </div>
                <div class="divTit">
                    联系手机：<form:input path="mobile" cssClass="txt" maxlength="11" style="width:70px;"/>
                </div>
                <div class="divTit">
                    所属区域：
                    <select id="province" name="province"
                            initValue="${purchaseReturnDTO.province}" class="txt select J_province" style="width: 70px">
                    </select>
                    <select id="city" name="city"
                            initValue="${purchaseReturnDTO.city}" class="txt select J_city" style="width: 70px">
                    </select>
                    <select id="region" name="region"
                            initValue="${purchaseReturnDTO.region}" class="txt select J_region" style="width: 70px">
                    </select>
                </div>
                <div class="divTit">
                    &nbsp;
                    <form:input path="address" cssClass="txt J_address_input" cssStyle="width:120px;margin-top: 2px;" maxlength="50"/>
                </div>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                    <div class="divTit" style="clear:both;">
                        <span style="margin-top: 2px;">仓库：</span>
                        <form:select path="storehouseId" cssClass="j_checkStoreHouse checkSelectChanged txt" cssStyle="margin-top: 2px;">
                            <option value="">请选择仓库</option>
                            <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
                        </form:select>
                        <input type="hidden" id="oldStorehouseId" value="${purchaseReturnDTO.storehouseId}" />
                    </div>
                </bcgogo:hasPermission>
                <div style="clear:both;"></div>
            </div> <%-- end propertie-info --%>
            <div style="clear:both;"></div>
            <table cellpadding="0" cellspacing="0" class="table2 tabCart tabPick" id="table_purchaseReturn">
                <col>
                <col width="70">
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="80">
                <col width="60">
                <col width="80">
                <col width="45">
                <tr class="titleBg">
                    <td style="padding-left:10px;">商品</td>
                    <td>当前库存</td>
                    <td>采购单价</td>
                    <td>采购数量</td>
                    <td>退货单价</td>
                    <td>退货量</td>
                    <td>退货单位</td>
                    <td>退货小计</td>
                    <td>操作</td>
                </tr>
                <tr class="space">
                    <td colspan="9"></td>
                </tr>
                <c:forEach items="${purchaseReturnDTO.itemDTOs}" var="itemDTO" varStatus="status">
                    <tr class="item bg">
                        <td style="padding-left:10px;" title="${itemDTO.productInfo}">
                                ${itemDTO.productInfo}
                            <form:hidden path="itemDTOs[${status.index}].productId" value="${itemDTO.productId}"/>
                            <form:hidden path="itemDTOs[${status.index}].id" value="${itemDTO.idStr}"/>
                            <form:hidden path="itemDTOs[${status.index}].commodityCode"
                                         value="${itemDTO.commodityCode}"/>
                            <form:hidden path="itemDTOs[${status.index}].productName" value="${itemDTO.productName}"/>
                            <form:hidden path="itemDTOs[${status.index}].brand" value="${itemDTO.brand}"/>
                            <form:hidden path="itemDTOs[${status.index}].spec" value="${itemDTO.spec}"/>
                            <form:hidden path="itemDTOs[${status.index}].model" value="${itemDTO.model}"/>
                            <form:hidden path="itemDTOs[${status.index}].vehicleBrand" value="${itemDTO.vehicleBrand}"/>
                            <form:hidden path="itemDTOs[${status.index}].vehicleModel" value="${itemDTO.vehicleModel}"/>
                        </td>
                        <td>
                            <span id="itemDTOs${status.index}.inventoryAmountSpan">
                                <fmt:formatNumber value="${(itemDTO.inventoryAmount==null?0:itemDTO.inventoryAmount)+(itemDTO.reserved==null?0:itemDTO.reserved)}" pattern="#.#"/>
                            </span>
                            <span id="itemDTOs${status.index}.inventoryUnitSpan">${itemDTO.unit}</span>
                            <fmt:formatNumber value='${(itemDTO.inventoryAmount==null?0:itemDTO.inventoryAmount)+(itemDTO.reserved==null?0:itemDTO.reserved)}' pattern='#.#' var="formattedInventoryAmount"/>
                            <form:hidden path="itemDTOs[${status.index}].inventoryAmount"
                                         value="${formattedInventoryAmount}"/>
                            <form:hidden path="itemDTOs[${status.index}].reserved"
                                         value="${itemDTO.reserved == null?'0': itemDTO.reserved}"/>
                        </td>
                        <td>
                            <form:input path="itemDTOs[${status.index}].purchasePrice"
                                        value='${itemDTO.purchasePrice}'
                                        cssStyle="width:60px;border: 0 none;background-color:transparent;"
                                        readonly="true"/>
                        </td>
                        <td>
                                ${itemDTO.purchaseAmount}${itemDTO.purchaseUnit}
                            <form:hidden path="itemDTOs[${status.index}].purchaseAmount"
                                         value="${itemDTO.purchaseAmount}"/>
                            <form:hidden path="itemDTOs[${status.index}].purchaseUnit" value="${itemDTO.purchaseUnit}"/>
                        </td>
                        <td>
                            <form:input path="itemDTOs[${status.index}].price"
                                        value='${itemDTO.price!=null?itemDTO.price:"0"}'
                                        class="itemPrice checkNumberEmpty txt"/>
                        </td>
                        <td>
                            <form:input path="itemDTOs[${status.index}].amount" value='${itemDTO.amount==null?"0":itemDTO.amount}'
                                        class="itemAmount checkNumberEmpty txt"/>
                        </td>

                        <td><form:input path="itemDTOs[${status.index}].unit"
                                        value="${itemDTO.unit}" class="itemUnit checkStringEmpty txt"/>
                            <form:hidden path="itemDTOs[${status.index}].storageUnit"
                                         value="${itemDTO.storageUnit}"
                                         class="itemStorageUnit"/>
                            <form:hidden path="itemDTOs[${status.index}].sellUnit"
                                         value="${itemDTO.sellUnit}"
                                         class="itemSellUnit"/>
                            <form:hidden path="itemDTOs[${status.index}].rate"
                                         value="${itemDTO.rate}" class="itemRate"/>
                        </td>
                        <td>
                            <form:input path="itemDTOs[${status.index}].total"
                                        value='${itemDTO.total!=null?itemDTO.total:"0"}'
                                        class="itemTotal"
                                        cssStyle="width:60px;border: 0 none;background-color:transparent;"
                                        readonly="true"/>
                        </td>
                        <td>
                            <input class="btnMinus" type="button" onfocus="this.blur();">
                        </td>
                    </tr>
                </c:forEach>
                <tr class="space">
                    <td colspan="9"></td>
                </tr>
                <tr class="space">
                    <td colspan="6">
                        <span class="buyRemark" style="vertical-align:top;">退货备注：</span>
                        <form:textarea path="memo" cssClass="checkStringEmpty txt"
                                       value="${purchaseReturnDTO.memo == null ? '':purchaseReturnDTO.memo }"
                                       maxlength="500"/>
                    </td>
                    <td colspan="3">金额合计：<span class="yellow_color" id="total_span">${purchaseReturnDTO.total}</span>&nbsp;元
                        <form:hidden path="total" value="${purchaseReturnDTO.total}"/>
                    </td>
                </tr>
                <tr class="space">
                    <td colspan="9"></td>
                </tr>
            </table>
        </div>
    </form:form>
    <div class="cartBottom"></div>

</div>
<div class="height"></div>
<c:choose>
    <c:when test="${empty purchaseReturnDTO.id}">
        <div class="btn_Click" style="width:auto; float:right;">
            <a id="confirmReturn">退 货</a>
            <a id="cancelReturn">取 消</a>
        </div>
    </c:when>
    <c:otherwise>
        <bcgogo:permission>
            <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.RETURN.CANCEL">
                <c:if test="${purchaseReturnDTO.status=='SELLER_PENDING'}">
                    <div class="invalidImg2" id="invalid_div" style="display: block;">
                        <input id="nullifyBtn" type="button" onfocus="this.blur();"/>
                        <div class="invalidWords" id="invalidWords">作废</div>
                    </div>
                </c:if>
            </bcgogo:if>
        </bcgogo:permission>
        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN.COPY">
            <div class="copyInput_div" id="copyInput_div">
                <input id="copyInput" type="button" onfocus="this.blur();"/>
                <div class="copyInput_text_div" id="copyInput_text">复制</div>
            </div>
        </bcgogo:hasPermission>
        <div class="btn_div_Img" id="cancel_div">
            <input id="cancelBtn" type="button" class="cancel j_btn_i_operate" onfocus="this.blur();"/>

            <div class="optWords">重置</div>
        </div>
        <div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">

            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN.SAVE">
                <div class="btn_div_Img" id="saleSave_div">
                    <input id="confirmReturnGoodsBtn" type="button" class="saleAccount j_btn_i_operate"
                           onfocus="this.blur();"/>

                    <div class="optWords">${purchaseReturnDTO.status =='SELLER_PENDING'?'改单':'退货'}</div>
                </div>
            </bcgogo:hasPermission>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.RETURN.PRINT">
                    <div class="btn_div_Img" id="print_div">
                        <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                        <input type="hidden" name="print" id="print" value="${purchaseReturnDTO.print}">

                        <div class="optWords">打印</div>
                    </div>
                </bcgogo:if>
            </bcgogo:permission>

        </div>
    </c:otherwise>
</c:choose>




</div>
<script type="text/javascript">
    //重写掉 unit.jsp 中原生的方法
    function changeToSellUnit(dom, sellUnitVal, rateVal) {      //点击变化成销售单位
        $(dom).text(sellUnitVal);
        var unitIdPrefix = $(dom).attr("id");
        unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
        $("#" + unitIdPrefix + "\\.unit").val(sellUnitVal);
        var inventoryAmount = dataTransition.rounding($("#" + unitIdPrefix + "\\.inventoryAmount").val() * rateVal, 2);
        $("#" + unitIdPrefix + "\\.inventoryAmount").val(inventoryAmount);
        $("#" + unitIdPrefix + "\\.inventoryAmountSpan").text(inventoryAmount);
        $("#" + unitIdPrefix + "\\.inventoryUnitSpan").text(sellUnitVal);
        var reservedAmount = dataTransition.rounding($("#" + unitIdPrefix + "\\.reserved").val() * rateVal, 2);
        $("#" + unitIdPrefix + "\\.reserved").val(reservedAmount);
    }
    //重写掉 unit.jsp 中原生的方法
    function changeToStorageUnit(dom, storageUnitVal, rateVal) {  //点击变化成库存单位
        $(dom).text(storageUnitVal);
        var unitIdPrefix = $(dom).attr("id");
        unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
        $("#" + unitIdPrefix + "\\.unit").val(storageUnitVal);
        var inventoryAmount = dataTransition.rounding($("#" + unitIdPrefix + "\\.inventoryAmount").val() * 1 / rateVal, 2);
        $("#" + unitIdPrefix + "\\.inventoryAmount").val(inventoryAmount);
        $("#" + unitIdPrefix + "\\.inventoryAmountSpan").text(inventoryAmount);
        $("#" + unitIdPrefix + "\\.inventoryUnitSpan").text(storageUnitVal);
        var reservedAmount = dataTransition.rounding($("#" + unitIdPrefix + "\\.reserved").val() / rateVal, 2);
        $("#" + unitIdPrefix + "\\.reserved").val(reservedAmount);
    }
</script>
<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <jsp:include page="../txn/common/selectStoreHouse.jsp"/>
</bcgogo:hasPermission>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>

</html>