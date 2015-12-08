<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>销售单</title>
<%
    boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn(WebUtil.getShopId(request));//选配仓位功能 默认开启这个功能false
    boolean tradePriceTag = ServiceManager.getService(IShopConfigService.class).isTradePriceSwitchOn(WebUtil.getShopId(request),WebUtil.getShopVersionId(request));//选配批发价功能
%>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
<c:choose>
    <c:when test="<%=storageBinTag%>">
        <link rel="stylesheet" type="text/css" href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" type="text/css" href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
    </c:otherwise>
</c:choose>
<link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/goodsSale<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>
<style type="text/css">
    .item input {
        text-overflow: clip;
    }

    #table_productNo {
        position: relative;
        z-index: 1;
        color: #272727;
        border-bottom: 1px solid #bbbbbb;
    }

    #table_sale_otherIncome {
        position: relative;
        z-index: 1;
        color: #272727;
        border-bottom: 1px solid #bbbbbb;
    }
    #table_sale_otherIncome .table_title, #table_sale_otherIncome .item2 {
        border-color: #BBBBBB;
        border-style: solid;
        border-width: 0 1px;
    }
    .blue_col {color:#0094FF;}
    .blue_col:hover {color:#FD5300; text-decoration:underline; cursor:pointer;}
</style>
<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/goodsSale<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/customerSearch<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/goodsSaleSolr<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/otherIncomeKind<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/storeHouseDialog<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
    APP_BCGOGO.PersonalizedConfiguration.TradePriceTag =<%=tradePriceTag%>;
    APP_BCGOGO.PersonalizedConfiguration.StorageBinTag =<%=storageBinTag%>;
    defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.SALE_MANAGE.SALE");
    defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
    APP_BCGOGO.UserGuide.currentPageIncludeGuideStep = "PRODUCT_ONLINE_GUIDE_GOODS_ONLINE";
    APP_BCGOGO.UserGuide.currentPage = "goodsSale";
    var returnType = '${salesOrderDTO.returnType}';
    var returnIndex = '${salesOrderDTO.returnIndex}';

    var salesOrderId = '${salesOrderDTO.id}';
    var debt = '${salesOrderDTO.debt}';
    var customer = '${salesOrderDTO.customer}';
    var licenceNo = '${salesOrderDTO.licenceNo}';

    function detailsArrears() {
        var customerId = $("#customerId").val();
        toReceivableSettle(customerId);
    }

    //author:zhangjuntao
    var time = new Array(), timeFlag1 = true, timeFlag2 = true;
    time[0] = new Date().getTime();
    time[1] = new Date().getTime();
    time[2] = new Date().getTime();
    time[3] = new Date().getTime();
    var reg = /^\d+(\.{0,1}\d*)$/;


    $(document).ready(function () {
        $(".itemAmount,.itemTotal").bind("blur", function () {
            dataTransition.roundingSpanNumber("totalSpan");
            var format = $(this).val();
            format = dataTransition.rounding(format, 2);
            $(this).val() && $(this).val(format);
        });
        $("#table_productNo input").bind("mouseover", function () {
            this.title = this.value;
        });

        $(document).click(function (e) {
            var e = e || event;
            var target = e.srcElement || e.target;
            if (target && typeof(target.id) == "string" && target.id.split(".")[1] != "productName" && target.id.split(".")[1] != "brand"
                    && target.id.split(".")[1] != "spec" && target.id.split(".")[1] != "model"
                    && target.id.split(".")[1] != "vehicleBrand" && target.id.split(".")[1] !=
                    "vehicleModel"
                    && target.id.split(".")[1] != "vehicleYear" && target.id.split(".")[1] !=
                    "vehicleEngine"
                    && target.id != "div_brand") {
                $("#div_brand")[0].style.display = "none";
            }
        });

        $("#goodsSaler").live("click focus keyup", function (event) {
            event = event || event.which;
            var keyCode = event.keyCode;

            if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
                return;
            }

            var obj = this;
            droplistLite.show({
                event: event,
                hiddenId: "salesManIds",
                id: "id",
                name: "name",
                data: "member.do?method=getSaleMans"
            });
        });

        $(document).click(function (e) {
            var e = e || event;
            var target = e.srcElement || e.target;
            if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName") {
                $("#div_serviceName").hide();
            }
        });

        $("#deleteGoodsSaler").hide();

        $("#deleteGoodsSaler").live("click", function () {
            $("#goodsSaler").val("");
            $("#goodsSalerId").val("");
            $("#deleteGoodsSaler").hide();
        });

        $("#goodsSalerDiv").mouseenter(function () {
            if ($("#goodsSaler").val() && !isDisable()) {
                $("#deleteGoodsSaler").show();
            }
        });

        $("#goodsSalerDiv").mouseleave(function () {
            $("#deleteGoodsSaler").hide();
        });

        $(document).bind("click", function (event) {
            if ($(event.target).attr("id") !== "historySearchButton_id"
                    && $("#customer")[0]) {
                $("#customer").tipsy("hide");
            }
        });

        App.Module.searchcompleteMultiselect.moveFollow({
            node: $("#customer")[0]
        });

        $("#historySearchButton_id")
                .tipsy({delay: 0, gravity: "s", html: true})
                .bind("click", function (event) {
                    var foo = App.Module.searchcompleteMultiselect;
                    $("#customer").tipsy("hide");
                    if (foo.detailsList.isVisible()) {
                        foo.hide();
                        return;
                    }

                    if (!foo._relInst || G.isEmpty(foo._relInst.value)) {
                        $("#customer").tipsy("show");
                        return;
                    }
                    foo.hide();
                    searchOrderSuggestion(foo, foo._relInst, "");
                    try{
                        App.Module.searchcomplete.hide();
                    }catch(e) {
                        G.debug("error searchcomplete instance is undefined!");
                    }
                    event.stopPropagation();
                })
                .toggle(!G.isEmpty(G.normalize($("#customer").val())));

        window.timerCheckHistoryButton = 0;
        function toggleHistoryButton(){
            $("#historySearchButton_id").toggle( !G.isEmpty(G.normalize($("#customer").val())) );
            timerCheckHistoryButton = setTimeout(toggleHistoryButton, 500);
        }
        timerCheckHistoryButton = setTimeout(toggleHistoryButton, 500);
    });



</script>

<bcgogo:hasPermission permissions="WEB.AD_SHOW">
    <script type="text/javascript" src="js/adShow<%=ConfigController.getBuildVersion()%>.js"></script>
</bcgogo:hasPermission>
</head>

<body class="bodyMain" pagetype="order" ordertype="">
<script type="text/javascript">
    $(document.body).attr("ordertype", App.OrderTypes.SALE);
    <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.ORDER_MOBILE_REMIND">
    APP_BCGOGO.Permission.Version.OrderMobileRemind=${WEB_VERSION_ORDER_MOBILE_REMIND};
    </bcgogo:permissionParam>

</script>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<div class="i_main clear">
<jsp:include page="txnNavi.jsp">
    <jsp:param name="currPage" value="goodsSale"/>
</jsp:include>
<jsp:include page="saleNavi.jsp">
    <jsp:param name="currPage" value="goodsSale"/>
</jsp:include>
<div class="clear"></div>
<div id="i_mainRight" class="i_mainRight shoppingCart" >
<div class="cartTop"></div>
<div class="cartBody">
    <form:form commandName="salesOrderDTO" id="salesOrderForm" action="sale.do?method=saveSale" method="post" name="thisform" class="J_leave_page_prompt">
    <jsp:include page="unit.jsp"/>
    <form:hidden path="id" value="${salesOrderDTO.id}"/>
    <form:hidden path="status" value="${salesOrderDTO.status}"/>
    <form:hidden path="receivableId" value="${salesOrderDTO.receivableId}"/>
    <form:hidden path="shopId" value="${sessionScope.shopId}"/>
    <form:hidden path="draftOrderIdStr" value="${salesOrderDTO.draftOrderIdStr}"/>
    <form:hidden path="selectSupplier" value="${WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER}"/>
    <form:hidden path="contactId" value="${salesOrderDTO.contactId}"  />
    <form:hidden path="qq" value="${salesOrderDTO.qq}"  />
    <form:hidden path="email" value="${salesOrderDTO.email}"  />
    <input id="confirm_account_date" name="accountDateStr" type="hidden" />

    <input type="hidden" id="wholesalerVersionNode"value="${wholesalerVersion}">
    <input type="hidden" id="isAdd" name="isAdd" value="${isAdd}"/> <!-- 默认为空 标示用户没有进行操作 -->
    <input id="type" name="type" type="hidden" value="${param.type}"> <!-- 库存带过来的参数-->
    <input id="orderType" name="orderType" value="goodsSaleOrder" type="hidden"/>
    <table cellpadding="0" style="margin: 10px;" cellspacing="0" class="table2 tabCart tabSales" id="table_productNo">
        <col width="75"/>
        <col width="150"/>
        <col width="80"/>
        <col width="80"/>
        <col width="80"/>
        <col width="55"/>
        <col width="60"/>
        <col width="45"/>
        <col width="45"/>
        <col width="35"/>
        <col width="45"/>
        <col width="40" class="storage_bin_col">
        <col width="45"/>
        <col width="70"/>
        <col width="70"/>
        <tr class="s_tabelBorder" id="trCustomer">
            <td colspan="15"  id="tdCustomer" style="padding-bottom: 11px;">
                <form:hidden path="customerId" value="${salesOrderDTO.customerId}" autocomplete="off"/>

                <div class="saleDiv">

                    <div class="cell">
                        <label style="width:60px;display: inline-block;">单据号</label>
                        <span class="receiptNoSpan" id="receiptNoSpan">系统自动生成</span>
                        <input type="hidden" value="${salesOrderDTO.receiptNo}" id="receiptNo" name="receiptNo">
                    </div>

                    <div class="cell cell2" style="position:relative;">
                        <label style="width:60px;display: inline-block;">客户名</label>
                        <input type="text" id="customer" name="customer" value="${salesOrderDTO.customer}" class="customerSuggestion checkStringEmpty textbox" style="width:120px;" kissfocus="on" autocomplete="off"/>
                        <input type="button" id="historySearchButton_id" alt="" title="历史查询" class="historySearchButton_c" style="display: block; top: 1px; right: -12px;position: absolute"/>
                    </div>

                    <div class="cell cell2">
                        <label style="width:60px;display: inline-block;">联系人</label>
                        <input type="text" id="contact" name="contact" value="${salesOrderDTO.contact}" maxlength="30" style="width:120px;" class="checkStringEmpty textbox" kissfocus="on" autocomplete="off" />
                    </div>

                    <div class="cell cell3">
                        <label style="width:60px;display: inline-block;">联系电话</label>
                        <form:input path="mobile" maxlength="11" value="${salesOrderDTO.mobile}" cssStyle="width: 120px;" class="checkStringEmpty textbox" autocomplete="off"/>
                        <form:hidden path="landline" value="${salesOrderDTO.landline}"></form:hidden>
                        <input type="hidden" id="hiddenMobile"/>
                    </div>

                    <div class="cell more">
                        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
                            <div id="customerInfo" class="blue_color i_clickClient client">更多客户信息>></div>
                        </bcgogo:hasPermission>
                    </div>

                </div>

                <div style="overflow: hidden;width:100%;">
                    <div style="width: 675px;float: left;">

                        <c:if test="${!wholesalerVersion}">
                            <div class="saleDiv" >
                                <input id="vehicleId" name="vehicleId" type="hidden" value="${salesOrderDTO.vehicleId}" autocomplete="off">
                                <div class="cell">
                                    <label style="width:60px;display: inline-block;">车牌号</label>
                                    <input type="text" id="licenceNo" name="licenceNo" value="${salesOrderDTO.licenceNo}" style="width:120px;text-transform:uppercase;" class="checkStringEmpty textbox" maxlength="10" kissfocus="on" autocomplete="off"/>
                                </div>
                                <div class="cell cell2">
                                    <label style="width:60px;display: inline-block;">车主</label>
                                    <input type="text" id="vehicleContact" name="vehicleContact" value="${salesOrderDTO.vehicleContact}" style="width:120px;" class="checkStringEmpty textbox" maxlength="10" kissfocus="on" autocomplete="off"/>
                                </div>
                                <div class="cell cell3">
                                    <label style="width:60px;display: inline-block;">车主号码</label>
                                    <input type="text" id="vehicleMobile" name="vehicleMobile" value="${salesOrderDTO.vehicleMobile}" style="width:120px;" class="checkStringEmpty textbox" maxlength="11" kissfocus="on" autocomplete="off"/>
                                </div>
                            </div>
                        </c:if>

                        <div class="saleDiv" style="width: 665px;">
                            <div class="cell" id="goodsSalerDiv">
                                <label style="width:60px;display: inline-block;">销售人</label>
                            <span id="goodsSalerSpan">
                                <form:input path="goodsSaler" value="${salesOrderDTO.goodsSaler}" readOnly="true" initgoodssalervalue="${salesOrderDTO.goodsSaler}" cssStyle="width: 120px;" cssClass="checkStringChanged textbox" autocomplete="off"/>
                                <img src="images/list_close.png" id="deleteGoodsSaler" style="width:12px;cursor:pointer">
                                <input type="hidden" id="goodsSalerId" name="goodsSalerId" initgoodssalervalue=""/>
                            </span>
                                <form:hidden path="editorId" value="${salesOrderDTO.editorId}" initgoodssalervalue="${salesOrderDTO.editorId}"/>
                            </div>

                            <div class="cell cell2">
                                <label style="width:60px;display: inline-block;">销售日期</label>
                                <form:hidden path="editDateStr" value="${salesOrderDTO.editDateStr}"/>
                                <span><form:input path="vestDateStr" ordertype="sale" id="orderVestDate" size="15" readonly="true" value="${salesOrderDTO.vestDateStr}" lastvalue="${salesOrderDTO.vestDateStr}" initordervestdatevalue="${salesOrderDTO.vestDateStr}" cssClass="checkStringChanged textbox" cssStyle="width:120px;" autocomplete="off"/></span>
                            </div>
                            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                                <div class="cell cell3" class="divTit">销售仓库
                                    <form:select path="storehouseId" cssClass="j_checkStoreHouse checkSelectChanged" cssStyle="width:130px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                                        <option value="">—请选择仓库—</option>
                                        <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
                                    </form:select>
                                </div>
                            </bcgogo:hasPermission>
                        </div>
                    </div>

                    <div class="saleDiv" style="float: left;margin-left: 10px;">
                        <div class="reconciliation" class="cash">
                            应收：<span class="arialFont">&yen;</span><span id="receivable" data-filter-zero="true">${totalReceivable == null ? 0 : totalReceivable}</span>
                            应付：<span class="arialFont">&yen;</span><span id="payable" data-filter-zero="true">${totalPayable == null ? 0 : totalPayable}</span>
                            <a class="blue_color" id="duizhan">对 账</a>
                        </div>
                    </div>
                </div>
            </td>
        </tr>

        <tr class="table_title titleBg">
            <td style="border-left:none;padding-left: 10px;">商品编号</td>
            <td>品名</td>
            <td>品牌/产地</td>
            <td>规格</td>
            <td>型号</td>
            <td>车型</td>
            <td>车辆品牌</td>
            <td>单价</td>
            <td>数量</td>
            <td>单位</td>
            <td>小计</td>
            <td class="storage_bin_td">货位</td>
            <td>库存量</td>
            <td>营业分类</td>
            <td style="border-right:none;">操作<input class="opera2" type="button" style="display:none;"></td>
        </tr>
        <tr class="space">
            <td colspan="15"></td>
        </tr>
        <c:forEach items="${salesOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">
            <c:if test="${itemDTO!=null}">
                <tr class="bg item table-row-original">
                    <td style="border-left:none;padding-left: 10px;">
                        <form:input path="itemDTOs[${status.index}].commodityCode"
                                    value='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}' class="table_input checkStringEmpty"
                                    title='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}' style="width:85%" maxlength="20"/>
                    </td>
                    <td>
                        <form:hidden path="itemDTOs[${status.index}].id" value="${itemDTO.id}"/>
                        <form:hidden path="itemDTOs[${status.index}].productType" value="${itemDTO.productType}"/>
                        <form:hidden path="itemDTOs[${status.index}].purchasePrice" value="${itemDTO.purchasePrice}"/>
                        <form:hidden path="itemDTOs[${status.index}].inventoryAveragePrice" value="${itemDTO.inventoryAveragePrice}"/>
                        <form:hidden path="itemDTOs[${status.index}].productId" value="${itemDTO.productId}"/>
                        <form:hidden path="itemDTOs[${status.index}].draftOrderItemIdStr" value="${itemDTO.draftOrderItemIdStr}"/>
                        <form:hidden path="itemDTOs[${status.index}].productVehicleStatus"
                                     value="${itemDTO.productVehicleStatus}" class="itemProductVehicleStatus"/>
                            <%--<span style="display:none" id="itemDTOs[${status.index}].purchasePrice">  ${itemDTO.price}</span>--%>
                            <%--<form:hidden path="itemDTOs[${status.index}].purchasePrice" value="${itemDTO.purchasePrice}"/>--%>
                        <form:hidden path="itemDTOs[${status.index}].vehicleBrandId" value="${itemDTO.vehicleBrandId}"/>
                        <form:hidden path="itemDTOs[${status.index}].vehicleModelId" value="${itemDTO.vehicleModelId}"/>
                        <form:hidden disabled="disabled" path="itemDTOs[${status.index}].vehicleYearId"/>
                        <form:hidden disabled="disabled" path="itemDTOs[${status.index}].vehicleEngineId"/>
                        <form:input path="itemDTOs[${status.index}].productName" value="${itemDTO.productName}"
                                    class="table_input checkStringEmpty" style="width:80%"/>
                        <input id="itemDTOs${status.index}.editbutton" class="edit1" type="button"
                               onclick="searchInventoryIndex(this)" name="itemDTOs[${status.index}].editbutton"
                               onfocus="this.blur();" value="" autocomplete="off" title="" style="margin-left: 6px">
                    </td>
                    <td>
                        <form:input path="itemDTOs[${status.index}].brand" value="${itemDTO.brand}" class="table_input checkStringEmpty" maxlength="100"/>
                    </td>
                    <td>
                        <form:input path="itemDTOs[${status.index}].spec" value="${itemDTO.spec}" class="table_input checkStringEmpty"/>
                    </td>
                    <td>
                        <form:input path="itemDTOs[${status.index}].model" value="${itemDTO.model}" class="table_input checkStringEmpty"/>
                    </td>
                    <td>
                        <form:input path="itemDTOs[${status.index}].vehicleModel" value="${itemDTO.vehicleModel}" maxlength="100"
                                    class="table_input checkStringEmpty" title="${itemDTO.vehicleModel}"/>
                    </td>
                    <td>
                        <form:input path="itemDTOs[${status.index}].vehicleBrand" value="${itemDTO.vehicleBrand}"
                                    class="table_input checkStringEmpty" title="${itemDTO.vehicleBrand}" maxlength="100"/>
                    </td>
                    <td style="color:#FF6700;">
                        <form:input path="itemDTOs[${status.index}].price" value="${itemDTO.price}"
                                    class="itemPrice table_input checkNumberEmpty" data-filter-zero="true"
                                    onchange="checkPrice(this)" kissfocus="on" style="text-overflow:clip"/>

                    </td>
                    <bcgogo:permission>
                        <bcgogo:if resourceType="logic" permissions="WEB.VERSION.PRODUCT.THROUGH_SELECT_SUPPLIER">
                            <td style="color:#FF0000;">
                                <form:input path="itemDTOs[${status.index}].amount" value="0"
                                            class="itemAmount table_input checkNumberEmpty" data-filter-zero="true"/>
                                <form:hidden path="itemDTOs[${status.index}].amountHid" class="itemAmountHid"
                                             value="${itemDTO.amountHid}"/>
                            </td>
                        </bcgogo:if>
                        <bcgogo:else>
                            <td style="color:#FF0000;">
                                <form:input path="itemDTOs[${status.index}].amount" value="${itemDTO.amount}"
                                            class="itemAmount table_input checkNumberEmpty" kissfocus="on" data-filter-zero="true"/>
                                <form:hidden path="itemDTOs[${status.index}].amountHid" class="itemAmountHid"
                                             value="${itemDTO.amountHid}"/>
                            </td>
                        </bcgogo:else>
                    </bcgogo:permission>

                    <td>
                        <form:input path="itemDTOs[${status.index}].unit" value="${itemDTO.unit}"
                                    class="itemUnit table_input checkStringEmpty"/>
                        <form:hidden path="itemDTOs[${status.index}].storageUnit" value="${itemDTO.storageUnit}"
                                     class="itemStorageUnit table_input"/>
                        <form:hidden path="itemDTOs[${status.index}].sellUnit" value="${itemDTO.sellUnit}"
                                     class="itemSellUnit table_input"/>
                        <form:hidden path="itemDTOs[${status.index}].rate" value="${itemDTO.rate}"
                                     class="itemRate table_input"/>
                    </td>
                    <td >
                        <span id="itemDTOs${status.index}.total_span" class="itemTotalSpan" title="${itemDTO.total}"
                              name="itemDTOs[${status.index}].total" data-filter-zero="true">${itemDTO.total}</span>
                        <form:hidden path="itemDTOs[${status.index}].total" value="${itemDTO.total}"
                                    class="itemTotal"/>
                    </td>
                    <td class="storage_bin_td">
                       <span id="itemDTOs${status.index}.storageBinSpan"
                             name="itemDTOs[${status.index}].storageBinSpan">${itemDTO.storageBin}</span>
                        <form:hidden path="itemDTOs[${status.index}].storageBin" value='${itemDTO.storageBin}'
                                     class="table_input" title='${itemDTO.storageBin}'/>
                    </td>
                    <td>
                            <span id="itemDTOs${status.index}.inventoryAmountSpan" data-filter-zero="true"
                                  name="itemDTOs[${status.index}].inventoryAmountSpan">${itemDTO.inventoryAmountApprox}</span>
                        <form:hidden path="itemDTOs[${status.index}].inventoryAmount"
                                     class="itemInventoryAmount table_input" value="${itemDTO.inventoryAmount}"
                                     readonly="true"/>
                    </td>

                    <td>
                        <form:input path="itemDTOs[${status.index}].businessCategoryName" value="${itemDTO.businessCategoryName}"
                                    class="table_input businessCategoryName" hiddenValue="${itemDTO.businessCategoryName}"/>
                        <form:hidden path="itemDTOs[${status.index}].businessCategoryId" value="${itemDTO.businessCategoryId}"/>

                    </td>
                        <%--<td><form:input path="itemDTOs[${status.index}].vehicleModel" value="${itemDTO.vehicleModel}" class="table_input" title="${itemDTO.vehicleModel}"/></td>--%>
                        <%--<td><form:input path="itemDTOs[${status.index}].vehicleBrand" value="${itemDTO.vehicleBrand}" class="table_input" title="${itemDTO.vehicleBrand}"/></td>--%>
                    <td style="display: none"><form:hidden disabled="disabled" path="itemDTOs[${status.index}].vehicleYear"
                                                           class="table_input" cssStyle="display: none;"/></td>
                    <td style="display: none"><form:hidden disabled="disabled"
                                                           path="itemDTOs[${status.index}].vehicleEngine"
                                                           class="table_input" cssStyle="display: none;"/></td>
                        <%--<td>--%>
                        <%--<form:input path="itemDTOs[${status.index}].memo" value="${itemDTO.memo}" class="table_input"/>--%>
                        <%--</td>--%>
                    <td style="border-right:none;">
                        <a id="itemDTOs${status.index}.deletebutton" name="itemDTOs[${status.index}].deletebutton"
                           class="opera1">删除</a>
                    </td>
                </tr>
                <c:if test="${itemDTO.outStorageRelationDTOs!=null}">
                    <tr id="itemDTOs${status.index}_supplierInfo" class="supplierInfo">
                        <td colspan="15">
                            <div class="trList">
                                <c:forEach items="${itemDTO.outStorageRelationDTOs}" var="outStorageRelationDTO" varStatus="status_out">
                                    <div class="divList">
                                        <input type="hidden" value="${outStorageRelationDTO.relatedSupplierIdStr!=null?outStorageRelationDTO.relatedSupplierIdStr:""}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].relatedSupplierId" />
                                        <input type="hidden" value="${outStorageRelationDTO.relatedSupplierName}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].relatedSupplierName" />
                                        <input type="hidden" value="${outStorageRelationDTO.relatedSupplierInventory}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].relatedSupplierInventory" />
                                        <input type="hidden" value="${outStorageRelationDTO.supplierType}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].supplierType" />
                                        <div style="width: 140px" class="supplierName" title="${outStorageRelationDTO.relatedSupplierName}">${outStorageRelationDTO.relatedSupplierName}</div>
                                        <div style="width: 100px">
                                            剩余库存：<label class="remainAmount">${outStorageRelationDTO.relatedSupplierInventory}</label>
                                        </div>
                                        <div style="width: 120px">
                                            销售数量<input type="text" remainAmount="${outStorageRelationDTO.relatedSupplierInventory}" value="${outStorageRelationDTO.useRelatedAmount}" vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].useRelatedAmount"/>
                                        </div>
                                        <div class="rightIcon" style="display: none;width:19px;"></div>
                                        <div class="wrongIcon" style="display: none;width:100px;">库存量不足！</div>
                                    </div>
                                </c:forEach>
                            </div>
                        </td>
                    </tr>
                </c:if>
            </c:if>
        </c:forEach>
    </table>
<div class="divTit allMoney">
    商品数量合计：<span id="amountTotal" class="yellow_color" data-filter-zero="true">0</span>
    <span style="margin-left:10px;">商品费用合计：</span><span id="productTotal" class="yellow_color" data-filter-zero="true">0</span>元
</div>
    <%--<div class="height"></div>--%>
<table cellpadding="0" cellspacing="0" style="margin: 10px;" name="otherIncomeTb" class="table2 tabCart tabSales" id="table_sale_otherIncome">
    <col width="180"/>
    <col width="300"/>
    <col/>
    <col width="250"/>
    <col width="70">
    <tr class="table_title titleBg">
        <td style="border-left:none;padding-left: 9px;">其他费用</td>
        <td>金额</td>
        <td>是否计入成本</td>
        <td>备注</td>
        <td style="border-right:none;">操作<input class="operaAdd" type="button"
                                                style="display:none;"></td>
    </tr>
    <tr class="space">
        <td colspan="4"></td>
    </tr>
    <c:forEach items="${salesOrderDTO.otherIncomeItemDTOList}" var="itemDTO" varStatus="status">
        <c:if test="${itemDTO!=null}">
            <tr class="item2 table-row-original bg">
                <td style="border-left:none;color:#6D8FB9;padding-left: 9px;">
                    <form:input path="otherIncomeItemDTOList[${status.index}].name"
                                value='${itemDTO.name}' class="table_input otherIncomeKindName checkStringEmpty" maxlength="50"/>
                </td>
                <td>

                    <c:if test="${itemDTO.name=='材料管理费'}">

                      <form:hidden path="otherIncomeItemDTOList[${status.index}].id" value="${itemDTO.id}"/>


                      <c:if test="${itemDTO.otherIncomeCalculateWay=='RATIO'}">

                        <input id="otherIncomeItemDTOList${status.index}.otherIncomePriceByRate"
                               maxlength="100" type="radio" checked="checked"
                               class="otherIncomePriceByRate" name="otherIncomeItemDTOList[${status.index}].priceCheckBox"
                               style="float:left; margin-right:4px;margin-top: 4px;"/>
                        <label></label>按材料费比率计算
                        <input id="otherIncomeItemDTOList${status.index}.otherIncomePriceRate"
                               value="${itemDTO.otherIncomeRate}"
                               class="txt otherIncomePriceRate txt_color"
                               style="width:70px;" data-filter-zero="true"/>&nbsp;%&nbsp;&nbsp;<span id="otherIncomeItemDTOList${status.index}.otherIncomePriceSpan">${itemDTO.price}</span>元
                        <div class="clear i_height"></div>
                        <input id="otherIncomeItemDTOList${status.index}.otherIncomePriceByAmount"
                               maxlength="100" type="radio" name="otherIncomeItemDTOList[${status.index}].priceCheckBox"
                               class="otherIncomePriceByAmount"
                               style="float:left; margin-right:4px;margin-top: 4px;"/>
                        <label></label>按固定金额计算&nbsp;&nbsp;

                        <input id="otherIncomeItemDTOList${status.index}.otherIncomePriceText" value="请输入金额"
                               style="width:100px;color:#9a9a9a;" class="txt otherIncomePriceText txt_color" data-filter-zero="true"/>元


                      </c:if>
                      <c:if test="${itemDTO.otherIncomeCalculateWay!='RATIO'}">
                        <input id="otherIncomeItemDTOList${status.index}.otherIncomePriceByRate"
                               maxlength="100" type="radio" name="otherIncomeItemDTOList[${status.index}].priceCheckBox"
                               class="otherIncomePriceByRate"
                               style="float:left; margin-right:4px;margin-top: 4px;"/>
                        <label></label>按材料费比率计算
                        <input id="otherIncomeItemDTOList${status.index}.otherIncomePriceRate"
                               value="请输入比率"
                               class="txt otherIncomePriceRate txt_color"
                               style="width:70px;color:#9a9a9a;" data-filter-zero="true"/>&nbsp;%&nbsp;&nbsp;<span id="otherIncomeItemDTOList${status.index}.otherIncomePriceSpan">0</span>元
                        <div class="clear i_height"></div>
                        <input id="otherIncomeItemDTOList${status.index}.otherIncomePriceByAmount"
                               maxlength="100" type="radio" checked="checked"
                               name="otherIncomeItemDTOList[${status.index}].priceCheckBox"
                               class="otherIncomePriceByAmount" value="${itemDTO.price}"
                               style="float:left; margin-right:4px;margin-top: 4px;"/>
                        <label></label>按固定金额计算&nbsp;&nbsp;

                        <input id="otherIncomeItemDTOList${status.index}.otherIncomePriceText" value="${itemDTO.price}"
                               style="width:100px;" class="txt otherIncomePriceText txt_color" data-filter-zero="true"/>元

                      </c:if>

                      <form:input path="otherIncomeItemDTOList[${status.index}].price" value="${itemDTO.price}" type="hidden"
                                                   class="table_input itemTotal otherIncomePrice checkStringEmpty" style="width:80%"/>
                                       <form:input path="otherIncomeItemDTOList[${status.index}].otherIncomeRate" value="${itemDTO.otherIncomeRate}" type="hidden" style="width:90%"/>
                                       <form:input path="otherIncomeItemDTOList[${status.index}].otherIncomeCalculateWay" value="${itemDTO.otherIncomeCalculateWay}" type="hidden"/>
                    </c:if>

                  <c:if test="${itemDTO.name!='材料管理费'}">

                     <form:hidden path="otherIncomeItemDTOList[${status.index}].id" value="${itemDTO.id}"/>
                     <form:input path="otherIncomeItemDTOList[${status.index}].price" value="${itemDTO.price}"
                                 class="table_input itemTotal otherIncomePrice checkStringEmpty" style="width:80%" data-filter-zero="true"/>
                   </c:if>
                </td>

                <td>

                  <c:if test="${itemDTO.calculateCostPrice=='TRUE'}">

                    <input id="otherIncomeItemDTOList${status.index}.otherIncomeCostPriceCheckbox"
                           maxlength="100" type="checkbox" checked="checked"
                           name="otherIncomeItemDTOList[${status.index}].checkbox"
                           class="otherIncomeCostPriceCheckbox"
                           style="float:left; margin-right:4px; margin-top:3px;"/>
                    <label style="float:left; margin-right:4px;"/></label>计入成本
                             <span id="otherIncomeItemDTOList${status.index}.otherIncomeSpan">
                               <form:input path="otherIncomeItemDTOList[${status.index}].otherIncomeCostPrice"
                                           value="${itemDTO.otherIncomeCostPrice}"
                                           class="table_input otherIncomeCostPrice checkStringEmpty"
                                           style="width:70px;" data-filter-zero="true"/>

                             </span>
                  </c:if>

                  <c:if test="${itemDTO.calculateCostPrice !='TRUE'}">

                    <input id="otherIncomeItemDTOList${status.index}.otherIncomeCostPriceCheckbox"
                           maxlength="100" type="checkbox"
                           name="otherIncomeItemDTOList[${status.index}].checkbox"
                           class="otherIncomeCostPriceCheckbox"
                           style="float:left; margin-right:4px; margin-top:3px;"/>
                    <label style="float:left; margin-right:4px;"/></label>计入成本
                                       <span style="display:none;"
                                             id="otherIncomeItemDTOList${status.index}.otherIncomeSpan">
                                         <form:input path="otherIncomeItemDTOList[${status.index}].otherIncomeCostPrice"
                                                     value="${itemDTO.otherIncomeCostPrice}"
                                                     class="table_input otherIncomeCostPrice checkStringEmpty"
                                                     style="width:70px;" data-filter-zero="true"/>

                                       </span>
                  </c:if>

                </td>

                <td>
                    <form:input path="otherIncomeItemDTOList[${status.index}].memo" value="${itemDTO.memo}" maxlength="100"
                                class="table_input  checkStringEmpty"/>
                </td>
                <td style="border-right:none;">
                    <a id="otherIncomeItemDTOList${status.index}.deletebutton" name="otherIncomeItemDTOList[${status.index}].deletebutton"
                           class="operaMinus">删除</a>
                </td>
            </tr>
        </c:if>
    </c:forEach>

</table>
<div class="divTit allMoney">
    其他费用合计：<span id="otherIncomeTotal" class="yellow_color" data-filter-zero="true">0</span>元
</div>
<input id="isMakeTime" type="hidden" value="0">
<input id="huankuanTime" type="hidden" value="" name="huankuanTime">
<input id="isAllMakeTime" type="hidden" value="0">
    <%--------------------欠款结算 zhouxiaochen 2011-12-14-------------------------------%>
<iframe id="iframe_qiankuan" style="position:absolute; left:0px; top:200px; display:none;"
        allowtransparency="true" width="1000px" height="600px" frameborder="0" src="">
</iframe>


</div>

<div class="tableInfo remarkInfo">
    <div class="danju_beizhu">
        <span style="margin-right:10px">备注:</span>
        <input type="text" id="memo" name="memo" value="${salesOrderDTO.memo}" style=" margin-left:-10px;" maxlength="500"
               class="memo checkStringEmpty textbox txt" kissfocus="on"/>
    </div>

    <div class="divTit allMoney">
        总计：<span id="totalSpan" class="yellow_color" data-filter-zero="true">0</span>元
    </div>
    <div class="total" class="divTit">
        <form:hidden path="total" value="${salesOrderDTO.total}"/>
        <form:hidden path="totalHid" value="${salesOrderDTO.totalHid}"/>
        <form:hidden path="cashAmount" value="${repairOrderDTO.cashAmount}"/>
        <form:hidden path="bankAmount" value="${repairOrderDTO.bankAmount}"/>
        <form:hidden path="customerDeposit" value="${repairOrderDTO.customerDeposit}"/> <!-- add by zhuj-->
        <form:hidden path="bankCheckAmount" value="${repairOrderDTO.bankCheckAmount}"/>
        <form:hidden path="bankCheckNo" value="${repairOrderDTO.bankCheckNo}"/>
        <form:hidden path="orderDiscount" value="${repairOrderDTO.orderDiscount}"/>
        <form:hidden path="accountMemberNo" value="${repairOrderDTO.accountMemberNo}"/>
        <form:hidden path="accountMemberPassword" value="${repairOrderDTO.accountMemberPassword}"/>
        <form:hidden path="memberAmount" value="${repairOrderDTO.memberAmount}"/>
        <form:hidden path="memberDiscountRatio"/>
        <form:hidden path="afterMemberDiscountTotal"/>
        <div style="display:none">实收：
            <span>
                <form:input path="settledAmount" value="${salesOrderDTO.settledAmount}" cssStyle="width: 40px;" cssClass="checkNumberEmpty"/>
                <form:hidden path="settledAmountHid" value="${salesOrderDTO.settledAmountHid}"/>
            </span>
        </div>
        <div style="display:none">欠款：<span><form:input path="debt" value="${salesOrderDTO.debt}" cssStyle="width:40px;" cssClass="checkNumberEmpty"/></span>
        </div>
        <div style="display:none"><input id="input_makeTime_sale" type="button" value="设置还款时间" style="display:none"/></div>

        <c:if test="${salesOrderDTO.debt>0}">
            <div>预计还款日期：${customerRecordDTO.repayDateStr}</div>
            <input type="hidden" id="repayDateStr" value="${customerRecordDTO.repayDateStr}"/>
        </c:if>
    </div>
    <div class="clear"></div>
    <div class="btn_div_Img divImg" id="saveDraftOrder_div" style="margin-left:8px;">
        <input type="button" id="saveDraftBtn" class="i_savedraft" value="" onfocus="this.blur();"/>

        <div  style="width:58px;" class="sureWords">保存草稿</div>
    </div>
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.CANCEL">
        <div class="invalidImg" id="invalid_div">
            <input id="nullifyBtn" type="button" onfocus="this.blur();"/>

            <div class="invalidWords" id="invalidWords">作废</div>
        </div>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.COPY">
        <div class="copyInput_div" id="copyInput_div">
            <input id="copyInput" type="button" onfocus="this.blur();"/>

            <div class="copyInput_text_div" id="copyInput_text">复制</div>
        </div>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE">
        <div class="btn_div_Img divImg" id="cancel_div">
            <input id="cancelBtn" type="button" class="cancel j_btn_i_operate" onfocus="this.blur();"/>

            <div class="optWords sureWords">清空</div>
        </div>
    </bcgogo:hasPermission>
    <div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">

        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE">
            <div class="btn_div_Img divImg" id="saleSave_div">
                <input id="saleAccountBtn" type="button" class="saleAccount j_btn_i_operate" onfocus="this.blur();"/>

                <div class="optWords sureWords">结算</div>
            </div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.TXN.SALES_MANAGE.PRINT">
            <div class="btn_div_Img divImg" id="print_div">
                <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                <input type="hidden" name="print" id="print" value="${salesOrderDTO.print}">

                <div class="optWords sureWords">打印</div>
            </div>
        </bcgogo:hasPermission>

    </div>
</div>

</form:form>

</div>
<%--缓存更多客户信息--%>
<input type="hidden" id="hidName"/>
<input type="hidden" id="hidShortName"/>
<input type="hidden" id="hidAddress"/>
<input type="hidden" id="hidContact"/>
<input type="hidden" id="hidMobile"/>
<input type="hidden" id="hidPhone"/>
<input type="hidden" id="hidFax"/>
<input type="hidden" id="hidMemberNumber"/>
<input type="hidden" id="hidBirthdayString"/>
<input type="hidden" id="hidQQ"/>
<input type="hidden" id="hidEmail"/>
<input type="hidden" id="hidBank"/>
<input type="hidden" id="hidBankAccountName"/>
<input type="hidden" id="hidAccount"/>
<%----%>
</div>
<div class="zuofei" id="zuofei"></div>
<div id="mask" style="display:block;position: absolute;">
</div>

<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:200px; display:none;overflow:hidden;"
        allowtransparency="true" width="900" height="450px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:50px; display:none;"
        allowtransparency="true" width="1000px" height="900px" scrolling="no" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_2"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBoxMakeTime" style="position:absolute;z-index:8;top:210px;left:87px;display:none; "
        allowtransparency="true" width="350px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<div id="isInvo"></div>

<div id="draftOrder_dialog" style="display:none">
    <div class="i_draft_table">
        <table cellpadding="0" cellspacing="0" class="i_draft_table_box" id="draft_table">
            <col>
            <col width="50">
            <col width="100">
            <col width="220">
            <col width="220">
            <col width="400">
            <col>
            <tr class="tab_title">
                <td class="tab_first"></td>
                <td>No</td>
                <td>单据号</td>
                <td>保存时间</td>
                <td>客户</td>
                <td>销售商品</td>
                <td class="tab_last"></td>
            </tr>
        </table>
        <!--分页-->
        <div class="hidePageAJAX">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="draft.do?method=getDraftOrders"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,orderTypes:'SALE'}"></jsp:param>
                <jsp:param name="jsHandleJson" value="initOrderDraftTable"></jsp:param>
                <jsp:param name="hide" value="hideComp"></jsp:param>
                <jsp:param name="dynamical" value="dynamical1"></jsp:param>
            </jsp:include>
        </div>
    </div>
</div>

<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>
<div id="div_brand" class="i_scroll" style="display:none;">
    <div class="Scroller-Container" id="Scroller-Container_id">
    </div>
</div>
<!-- 客户商下拉菜单 zhangchuanlong-->
<div id="div_brandCustomer" class="i_scroll" style="display:none;width:300px;">
    <div class="Container" style="width:300px;">
        <div id="Scroller-1licenceNo1" style="width:300px;">
            <div class="Scroller-ContainerSupplier" id="Scroller-Container_idCustomer">
            </div>
        </div>
    </div>
</div>
<div id="div_serviceName" class="i_scroll" style="display:none;width:150px;">
    <div class="Scroller-Container" id="Scroller-Container_ServiceName">
    </div>
</div>
<input type="hidden" id="goodSalePage" value="1"/>

<!-- 搜索下拉, TODO 以后移到组件里 -->
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="dialog-confirm" title="提醒" style="display: none">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>

    <div id="dialog-confirm-text"></div>
    </p>
</div>

<div id="dialog-confirm-account-date" class="prompt_box" style="display: none">
    <div class="">
        <p><strong>友情提示：</strong>单据日期与当前系统日期不一致！请正确选择单据结算日期，财务营业流水按结算日期统计！</p>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td width="35%">请选择单据结算日期：</td>
                <td width="65%"><div class="fl"><input id="confirm_account_date_radio" name="account-date-radio" type="radio" checked="true" />
                    <span id="confirm_account_date_span"></span><span style="color:#767C7C">(单据日期)</span></div>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td><div class="fl"><input name="account-date-radio" type="radio" value="" />
                    <span id="confirm_current_date_span"></span><span style="color:#767C7C">(当前日期)</span></div>
                </td>
            </tr>
        </table>
        <div class="clear"></div>
        <div class="wid275">
            <div class="addressList"> <a class="ok_btn" href="#">确 定</a> <a class="cancel_btn" href="#">取 消</a></div>
        </div>
        <div class="clear"></div>
    </div>
</div>


<div id="inputMobile"  style="display:none">
    <div style="margin-left: 45px;margin-top: 15px">
        <label>手机号：</label>
        <input type="text" id="divMobile" style="width:125px;height: 20px">
    </div>
</div>

<!-- add by zhuj  name duplicate tip -->
<div class="alertMain productDetails" id="nameDupTip" style ="display:none;" >
    <div class="height"></div>
        <div id="cusDupTip">
            <div>该客户存在重名客户，请选择</div>
            <div class="height"></div>
            <label class="rad"><input type="radio" id="newCustomer"/>该客户为新客户,需填写手机或修改客户名加以区分</label><br/>
            <label class="rad" id="oldCustomer"><input type="radio"/>该客户为老客户,则请选择所需客户</label>
        </div>

    <div id="oldCustomers" style = "display:none">
        <div>请选择老客户</div>
        <div class="height"></div>
    </div>
    <div class="height"></div>
    <div class="button button_tip">
        <a class="btnSure J_btnSure">确 定</a>
        <a class="btnSure J_return" style="display: none">返回上一步</a>
    </div>
</div>

<div class="alertMain productDetails" id="mobileDupTip" style ="display:none;" >
    <div class="height"></div>
    <div id="mobileDupCustomers">
        <div>该客户存在重名客户，请选择</div>
        <div class="height"></div>
    </div>
    <div class="height"></div>
    <div class="button button_tip">
        <a class="btnSure J_selectSure">确 定</a>
    </div>
</div>
<div id="storeHouseDialog" style="display:none;" class="alertMain">
    <div style="margin-top: 10px;">
        <span style="font-size: 14px;">您没有选择仓库信息！请选择仓库：</span>
        <select id="storehouseDiv"
                style="width:120px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
            <option value="">—请选择仓库—</option>
            <c:forEach items="${storeHouseDTOList}" var="storeHouseDTO">
                <option value="${storeHouseDTO.id}">${storeHouseDTO.name}</option>
            </c:forEach>
        </select>
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