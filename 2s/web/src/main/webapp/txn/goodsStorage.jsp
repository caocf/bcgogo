<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>入库单</title>
<%
    boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn(WebUtil.getShopId(request));//选配仓位功能 默认开启这个功能false
    boolean tradePriceTag = ServiceManager.getService(IShopConfigService.class).isTradePriceSwitchOn(WebUtil.getShopId(request),WebUtil.getShopVersionId(request));//选配批发价功能
%>
<link rel="stylesheet" type="text/css" href="styles/goodstorageNew<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css"/>
<c:choose>
    <c:when test="<%=storageBinTag%>">
        <link rel="stylesheet" type="text/css" href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" type="text/css" href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="<%=tradePriceTag%>">
        <link rel="stylesheet" type="text/css" href="styles/tradePriceOn<%=ConfigController.getBuildVersion()%>.css"/>
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" type="text/css" href="styles/tradePriceOff<%=ConfigController.getBuildVersion()%>.css"/>
    </c:otherwise>
</c:choose>
<link rel="stylesheet" type="text/css" href="styles/activeRecommendSupplierTip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>
<style type="text/css">
    .item input {
        text-overflow: clip;
    }

</style>
<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/goodsStorageSolr<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/goodsStorage<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/supplierSearch<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/barCode<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/setLimit<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/storeHouseDialog<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
APP_BCGOGO.PersonalizedConfiguration.TradePriceTag =<%=tradePriceTag%>;
APP_BCGOGO.PersonalizedConfiguration.StorageBinTag =<%=storageBinTag%>;
<bcgogo:permissionParam permissions="WEB.VERSION.ACTIVE_RECOMMEND_SUPPLIER">
    APP_BCGOGO.Permission.Version.ActiveRecommendSupplier=${WEB_VERSION_ACTIVE_RECOMMEND_SUPPLIER}
</bcgogo:permissionParam>
var repairOrderId = '${param.repairOrderId}';
var productAmount = '${param.productAmount}';
var productIds = '${param.productIds}';
var returnType = '${purchaseInventoryDTO.returnType}';
var returnIndex = '${purchaseInventoryDTO.returnIndex}';
//print为详细结算弹出框的打印状态,id为单据ID
var print = "${print}";
var id = "${purchaseInventoryDTO.id}";
defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.PURCHASE_MANAGE.STORAGE");
defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");

$(document).ready(function () {
    if (print != "" && id != "") {
        window.open("storage.do?method=getPurchaseInventoryToPrint&purchaseInventoryId=" + id, '',
                "dialogWidth=1024px;dialogHeight=768px");
    }
    $(".itemAmount,.itemPurchasePrice,.itemTotal").live("blur", function () {
        dataTransition.roundingSpanNumber("totalSpan");
        var format = $(this).val();
        format = dataTransition.rounding(format, 2);
        $(this).val() != '' && $(this).val(format);
        setTotal();
        activeRecommendSupplierTip($(this).parent().parent(), getOrderType());
    });
    $("#table_productNo").bind("mouseenter", function (event) {
        $("input[type='text']", this).each(function () {
            this.title = this.value;
            $(this).tooltip({delay: 0});
        });
    });
    $(document).click(function (e) {
        var target = e.target;
        if (target.id != "div_brand") {
            $("#div_brand").css("display", "none");
        }
    });

    $(document).click(function (e) {
        var target = e.target;
        if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName") {
            $("#div_serviceName").hide();
        }
    });
    $(".item").each(function () {
        var productVehicleStatus = $(this).find("td:eq(1)>input").eq(2).val();
        var productId2 = $(this).find("td:eq(1)>input").eq(9).val();
        if (productId2 != null && productId2 != "") {
            if (productVehicleStatus == "0") {
                $(this).find("td:eq(6)>input:first").attr("disabled", true);
                $(this).find("td:eq(7)>input:first").attr("disabled", true);
                $(this).find("td:eq(8)>input:first").attr("disabled", true);
            } else if (productVehicleStatus == "1") {
                $(this).find("td:eq(5)>input:first").val("全部");
                $(this).find("td:eq(5)>input:last").val("全部");
                $(this).find("td:eq(6)>input:first").val("");
                $(this).find("td:eq(6)>input:last").val("");
                $(this).find("td:eq(6)>input:first").attr("disabled", true);
                $(this).find("td:eq(7)>input:first").val("");
                $(this).find("td:eq(7)>input:last").val("");
                $(this).find("td:eq(7)>input:first").attr("disabled", true);
                $(this).find("td:eq(8)>input:first").val("");
                $(this).find("td:eq(8)>input:last").val("");
                $(this).find("td:eq(8)>input:first").attr("disabled", true);
            }
        }
    });

    $("#acceptor").live("click", function () {
        var obj = this;
        $.ajax({
            type: "POST",
            url: "member.do?method=getSaleMans",
            async: true,
            data: {
                now: new Date()
            },
            cache: false,
            dataType: "json",
            error: function (XMLHttpRequest, error, errorThrown) {
                $("#div_serviceName").css({'display': 'none'});
            },
            success: function (jsonObject) {
                initSaleMan(obj, jsonObject);
            }
        });
    });

    $("#acceptor").live("blur", function () {
        $("#div_serviceName").fadeOut();
    });

    $("#deleteAcceptor").hide();

    $("#deleteAcceptor").live("click", function () {
        $("#acceptor").val("");
        $("#acceptorId").val("");
        $("#deleteAcceptor").hide();
    });

    $("#acceptorDiv").mouseenter(function () {
        if ($("#acceptor").val() && !isDisable()) {
            $("#deleteAcceptor").show();
        }
    });

    $("#acceptorDiv").mouseleave(function () {
        $("#deleteAcceptor").hide();
    });

    if ($("#goodsStorageMessage").val()) {
        if ($("#goodsStorageMessage").attr("resultOperation") == "ALERT") {
            nsDialog.jAlert($("#goodsStorageMessage").val(), null, function () {
            });
        }
    }

    // 供应商 快速输入功能强化
    $("#supplier")
            .attr("warning", "请先输入")
            .tipsy({title: "warning", delay: 0, gravity: "s", html: true, trigger: 'manual'})
            .bind("focus", function () {
                $(this).tipsy("hide");
            });

    $(document).bind("click", function (event) {
        if ($(event.target).attr("id") !== "historySearchButton_id"
                && $("#supplier")[0]) {
            $("#supplier").tipsy("hide");
        }
    });

    App.Module.searchcompleteMultiselect.moveFollow({
        node: $("#supplier")[0]
    });

    // add by zhuj 联系人下拉菜单
    // 绑定搜索下拉事件
    $("#contact")
            .bind('click focus', function (e) {
                e.stopImmediatePropagation();//可以阻止掉同一事件的其他优先级较低的侦听器的处理
                if (!GLOBAL.Lang.isEmpty($("#supplierId").val())) {
                    getContactListByIdAndType($("#supplierId").val(), "supplier", $(this)); //@see js/contact.js
                }
            })
            .bind('keyup', function (event) {
                var eventKeyCode = event.which || event.keyCode;
                if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
//                    getContactListByIdAndType($("#supplierId").val(), "supplier", $(this), eventKeyCode); //@see js/contact.js
                }
            });

    $("#historySearchButton_id")
            .tipsy({delay: 0, gravity: "s", html: true})
            .bind("click", function (event) {
                var foo = App.Module.searchcompleteMultiselect;
                $("#supplier").tipsy("hide");
                if (foo.detailsList.isVisible()) {
                    foo.hide();
                    return;
                }

                if (!foo._relInst || G.isEmpty(foo._relInst.value)) {
                    $("#supplier").tipsy("show");
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
            .toggle(!G.isEmpty(G.normalize($("#supplier").val())));

    $("#payDiv").mouseenter(function () {
        $("#yingshou").css("color","#FFFFFF");
        $("#yingfu").css("color","#FFFFFF");
    });

    $("#payDiv").mouseleave(function () {
        $("#yingshou").css("color","#FF0000");
        $("#yingfu").css("color","#1F541E");
    });

    window.timerCheckHistoryButton = 0;
    function toggleHistoryButton(){
        $("#historySearchButton_id").toggle( !G.isEmpty(G.normalize($("#supplier").val())) );
        timerCheckHistoryButton = setTimeout(toggleHistoryButton, 500);
    }
    timerCheckHistoryButton = setTimeout(toggleHistoryButton, 500);

    /*provinceBind();
    $("#select_province").bind("change",function(){
        cityBind(this);
    });
    $("#select_city").bind("change",function(){
        townshipBind(this);
    });
    $("#select_province,#select_city,#select_township,#settlementType,#invoiceCategory").click(function(){
        $(this).css("color","#000000");
    });
    setValues($("#select_province_input").val(),$("#select_city_input").val(),$("#select_township_input").val());
    $("#otherInput").keyup(function(){
        if($.trim($(this).val()) != '') {
            $("#otherCheckbox").attr("checked",true);
            $("#otherCheckbox").val($.trim($(this).val()));
        } else {
            $("#otherCheckbox").val('');
            $("#otherCheckbox").attr("checked",false);
        }
    });
    setBusinessScope();*/

    // add by zhuj  绑定浮出框
    $("#orderSupplierInfo").bind("click", function () {
        bcgogo.checksession({"parentWindow": window.parent, 'iframe_PopupBox': $("#iframe_PopupBox")[0], 'src': "txn.do?method=orderSupplierInfo&supplier="
                + encodeURIComponent($("#supplier").val()) + "&mobile=" + $("#mobile").val() + "&landLine=" + $("#landline").val() + "&hiddenMobile=" + $("#hiddenMobile").val()
                + "&supplierId=" + $("#supplierId").val() + "&contact=" + encodeURIComponent($("#contact").val())});
    });


});

function showIt() {
    bcgogo.checksession({"parentWindow": window.parent, 'iframe_PopupBox': $("#iframe_PopupBox_Limit")[0], 'src': "txn.do?method=setSale"});
}

function setRecommendedPrice(percent, value) {     //在入库单处设定销售价
    if (isNaN(percent) || isNaN(value)) {
        return false;
    }
    $("input[id$=recommendedPrice]").each(function (i) {
        var $recommendedPrice = $(this);
        var prefix = $recommendedPrice.attr("id");
        prefix = prefix.substr(0, prefix.indexOf("."));
//        var purchasePriceDomVal = $("#" + prefix + "\\.purchasePrice").val();
        var inventoryAveragePrice = $("#" + prefix + "\\.purchasePrice").val();
        if (!inventoryAveragePrice) {
            inventoryAveragePrice = 0;
        }
        $recommendedPrice.val((inventoryAveragePrice * (1 + percent / 100) + value * 1).toFixed(2));
        if (Number($recommendedPrice.val()) <= 0.0) {
            $recommendedPrice.val('0.0');
        }
        $recommendedPrice.change();
    });
}

function initSaleMan(domObject, jsonObject) {
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();
    var domTitle = domObject.name;
    var position = domObject.getBoundingClientRect();
    var x = position.left;
    var y = position.top;
    var selectmore = jsonObject.length;
    if (selectmore <= 0) {
        $("#div_serviceName").css({'display': 'none'});
    }
    else {
        $("#div_serviceName").css({
            'display': 'block', 'position': 'absolute',
            'left': x + 'px',
            'top': y + offsetHeight + 6 + 'px',
            'width': '105px',
            overflowY: "scroll",
            overflowX: "hidden"
        });

        $("#Scroller-Container_ServiceName").html("");

        for (var i = 0; i < jsonObject.length; i++) {
            var id = jsonObject[i].idStr;
            var a = $("<a id=" + id + "></a>");
            $(a).css("color", "#000");
            a.html(jsonObject[i].name + "<br>");
            $(a).bind("mouseover", function () {
                $("#Scroller-Container_ServiceName > a").removeAttr("class");
                $(this).attr("class", "hover");
                selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name;// $(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            $(a).click(function () {
                var sty = this.id;

                $("#acceptorId").val(sty);

                $(domObject).val(selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name); //取的第一字符串

                selectItemNum = -1;
            });

            $("#Scroller-Container_ServiceName").append(a);
        }
    }
}

function showSetTradePrice() {
    bcgogo.checksession({
        "parentWindow": window.parent,
        'iframe_PopupBox': $("#iframe_PopupBox_Limit")[0],
        'src': "txn.do?method=setTradePrice"
    });
}

function setMultipleTradePrice(percent, value) {
    if (isNaN(percent) || isNaN(value)) {
        return false;
    }
    $("input[id$='.tradePrice']").each(function () {
        var idPrefix = $(this).attr("id").split(".")[0];
        if(!G.Lang.isEmpty($("#" + idPrefix + "\\.productName").val())){
            var inventoryAveragePrice = $("#" + idPrefix + "\\.purchasePrice").val();
            var newTradePrice = dataTransition.simpleRounding(inventoryAveragePrice * (1 + percent / 100) + value * 1,2);
            if(newTradePrice>0){
                $("#" + idPrefix + "\\.tradePrice").val(newTradePrice);
            }
        }
    });
}
</script>
<bcgogo:hasPermission permissions="WEB.AD_SHOW">
    <script type="text/javascript" src="js/adShow<%=ConfigController.getBuildVersion()%>.js"></script>
</bcgogo:hasPermission>
</head>

<body class="bodyMain" pagetype="order" ordertype="">
<script type="text/javascript">
    $(document.body).attr("ordertype", App.OrderTypes.INVENTORY);
</script>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<bcgogo:permissionParam permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
    <input type="hidden" id="permissionGoodsSale" value="${WEB_TXN_SALE_MANAGE_SALE}"/>
</bcgogo:permissionParam>
<bcgogo:permissionParam permissions="WEB.TXN.SALE_MANAGE.SALE">
    <input type="hidden" id="permissionGoodsSale" value="${permissionParam1}"/>
</bcgogo:permissionParam>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>

<div class="i_main clear">
<jsp:include page="txnNavi.jsp">
    <jsp:param name="currPage" value="purchase"/>
</jsp:include>
<jsp:include page="purchaseNavi.jsp">
    <jsp:param name="currPage" value="goodsStorage"/>
</jsp:include>
<div class="clear"></div>
<form:form commandName="purchaseInventoryDTO" id="purchaseInventoryForm" action="storage.do?method=saveStorage" method="post" name="thisform" class="J_leave_page_prompt">
<div class="i_mainRight" id="i_mainRight">
<div class="cartTop"></div>
<div class="cartBody">
<jsp:include page="unit.jsp"/>
<form:hidden path="shopId" value="${sessionScope.shopId}"/>
<form:hidden path="id" value="${purchaseInventoryDTO.id}"/>
<input id="orderType" name="orderType" value="purchaseInventoryOrder" type="hidden"/>
<input id="type" name="type" type="hidden" value="${param.type}">
<input id="supplierId" name="supplierId" type="hidden" value="${purchaseInventoryDTO.supplierId}"/>
<input id="contactId" name="contactId" type="hidden" value="${purchaseInventoryDTO.contactId}"/>
<input id="saleOrderId" name="saleOrderId" type="hidden" value="${saleOrderId}"/>
<input id="purchaseOrderId" name="purchaseOrderId" type="hidden" value="${param.purchaseOrderId}">
<input id="repairOrderId" name="repairOrderId" type="hidden" value="${param.repairOrderId}">
<input id="productIds" name="productIds" type="hidden" value="${param.productIds}">
<input id="productAmount" name="productAmount" type="hidden" value="${param.productAmount}">
<form:hidden path="selectSupplier" value="${WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER}"/>

<input id="confirm_account_date" name="accountDateStr" type="hidden" />

<c:if test="${!empty result}">
    <input type="hidden" id="goodsStorageMessage" value="${result.msg}" resultDate="${result.data}" resultOperation="${result.operation}">
</c:if>
<bcgogo:permissionParam permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS,WEB.TXN.INVENTORY_MANAGE.SALE_PRICE_SETTING,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
    <input type="hidden" id="permissionInventoryAlarmSettings" value="${permissionParam1}"/>
    <input type="hidden" id="permissionSalePriceSettings" value="${permissionParam2}"/>
    <input type="hidden" id="repairOrderPermission" value="${permissionParam3}"/>
</bcgogo:permissionParam>
<form:hidden path="status" value="${purchaseInventoryDTO.status}"/>
<form:hidden path="draftOrderIdStr" value="${purchaseInventoryDTO.draftOrderIdStr}"/>
<div class="tuihuo_first">
<span class="left_tuihuo"></span>
<table id="table" class="elivate">
    <col width="100">
    <col width="170">
    <col width="100">
    <col width="120">
    <col width="100">
    <col width="70">
    <col width="100">
    <col width="110">
    <col width="60">
    <tr>
        <td class="t_title">订单号：</td>
        <td colspan="8">
            <a id="receiptNoSpan" class="receiptNoSpan">系统自动生成</a>
            <input type="hidden" name="receiptNo" id="receiptNo" value="${purchaseInventoryDTO.receiptNo}"/>
        </td>
    </tr>
    <tr>
            <%--<c:if test="${purchaseInventoryDTO.receiptNo != null}">--%>
            <%--<td width="7%">入库单号：</td>--%>
            <%--<td style="text-align:left;width:9%;" class="hover">--%>
            <%--${purchaseInventoryDTO.receiptNo}--%>
            <%--</td>--%>
            <%--</c:if>--%>



        <td class="t_title">验收人：</td>
        <td>
            <div id="acceptorDiv">
                <form:input path="acceptor" readOnly="true" maxlength="20" cssClass="checkStringChanged textbox" value="${purchaseInventoryDTO.acceptor}"
                            initacceptorvalue="${purchaseInventoryDTO.acceptor}" cssStyle="width: 50px;"/>
                <img src="images/list_close.png" id="deleteAcceptor" style="width:12px;cursor:pointer;margin-top:3px;margin-left:2px;">
                <input type="hidden" id="acceptorId" name="acceptorId"/>
                <form:hidden path="editor" value="${purchaseInventoryDTO.editor}"/>
                <form:hidden path="editorId" value="${purchaseInventoryDTO.editorId}"/>
            </div>
        </td>
        <td class="t_title">入库日期：</td>
        <td  style="text-align:left;">
            <form:hidden path="editDateStr" value="${purchaseInventoryDTO.editDateStr}"/>
            <c:choose>
                <c:when test="${!empty purchaseInventoryDTO.id}">
                    ${purchaseInventoryDTO.vestDateStr}
                    <form:hidden path="vestDateStr" value="${purchaseInventoryDTO.vestDateStr}" ordertype="purchase" id="orderVestDate"/>
                </c:when>
                <c:otherwise>
                    <form:input path="vestDateStr" ordertype="purchase" id="orderVestDate" readonly="true" size="10" value="${purchaseInventoryDTO.vestDateStr}"
                              cssClass="checkStringChanged textbox" lastvalue="${purchaseInventoryDTO.vestDateStr}" initordervestdatevalue="${purchaseInventoryDTO.vestDateStr}"/>
                </c:otherwise>
            </c:choose>
        </td>
        <td class="t_title">
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">仓库：</bcgogo:hasPermission>
        </td>
        <td style="text-align:left;">
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                <form:select path="storehouseId" cssClass="j_checkStoreHouse checkSelectChanged" cssStyle="height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                    <option value="">—请选择仓库—</option>
                    <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
                </form:select>
            </bcgogo:hasPermission>
        </td>
                <td class="t_title">
                    应收应付：
                </td>
                <td>
                    <div class="pay" style="width:154px;" id="duizhan">
                        <a class="payMoney" style="color:#272727;">应收<span class="arialFont">&yen;</span><span id="receivable" data-filter-zero="true">${totalReceivable == null ? '0':totalReceivable}</span></a>
                        <a class="fuMoney" style="color:#272727;">应付<span class="arialFont" style="margin-right:0;">&yen;</span><span id="payable" data-filter-zero="true">${totalPayable == null? '0':totalPayable}</span></a>
                    </div>
                </td>
        <td></td>
            <%--<td>--%>
            <%--<c:if test="${purchaseInventoryDTO.status.name!=null}">--%>
            <%--单据状态：--%>
            <%--</c:if>--%>
            <%--</td>--%>
            <%--<td style="text-align:left;">--%>
            <%--<c:if test="${purchaseInventoryDTO.status.name!=null}">--%>
            <%--<label style="color: #FF5E04;">${purchaseInventoryDTO.status.name}</label><br/>--%>
            <%--</c:if>--%>
            <%--<input type="hidden" name="receiptNo" id="receiptNo" value="${purchaseInventoryDTO.receiptNo}"/>--%>
            <%--</td>--%>
    </tr>
    <tr>
        <td class="t_title">供应商<a style="color:#F00000;">*</a>：</td>
        <td>
            <input type="text" id="supplier" name="supplier" maxlength="20" class="supplierSuggestion checkStringEmpty textbox"
                   value="${purchaseInventoryDTO.supplier}" style="width:116px" kissfocus="on"/>

                <%--<img src="images/star.jpg" style="float:left;">--%>
            <input type="button" id="historySearchButton_id" alt="" title="历史查询" class="historySearchButton_c"
                   style="display:block; width: 25px; float:left; margin-top:-5px;border:none;height: 25px"/>
        </td>
        <td class="t_title">座机：</td>
        <td>
            <form:input path="landline" id="landline" maxlength="15" value="${purchaseInventoryDTO.landline}" cssStyle="width: 100px;" cssClass="checkStringEmpty textbox"/>
        </td>
        <td class="t_title">联系人：</td>
        <td>
            <input type="text" id="contact" name="contact" maxlength="20" value="${purchaseInventoryDTO.contact}"
                   class="checkStringEmpty textbox" kissfocus="on"/>
        </td>
        <td class="t_title">联系电话：</td>
        <td>
            <form:input path="mobile" id="mobile" maxlength="11" value="${purchaseInventoryDTO.mobile}" cssStyle="" cssClass="checkStringEmpty textbox"/>
            <input type="hidden" id="hiddenMobile" value="${purchaseInventoryDTO.mobile}"/>
        </td>

        <td>
            <%--<a id="clickMore" class="down blue_color">详细</a>--%>
            <a id="orderSupplierInfo" class="down blue_color">详细</a>
        </td>
    </tr>
    <%--<tr class="supplierDetailInfo" style="display:none">

        <td class="t_title">简称：</td>
        <td>
            <input type="text" id="abbr" name="abbr" maxlength="20" class="textbox"
                   value="${purchaseInventoryDTO.abbr}" kissfocus="on"/>
        </td>
        <td class="t_title">传真：</td>
        <td><form:input path="fax" class="textbox" maxlength="20" value="${purchaseInventoryDTO.fax}"/></td>

            <td class="t_title">所属区域：</td>
            <td colspan="4">
                <select class="txt area" style="width:84px; float:left; margin-right:5px;" id="select_province" name="province"><option value="">-所有省-</option></select>
                <select class="txt area" style="width:84px; float:left; margin-right:5px;" id="select_city" name="city"><option value="">-所有市-</option></select>
                <select class="txt area" style="width:84px; float:left; margin-right:5px;" id="select_township" name="region"><option value="">-所有区-</option></select>
                <input id="input_address" name="address" type="text" class="txt J_address_input" style="width:104px;" value="${purchaseInventoryDTO.address}"/>
                <input type="hidden" id="select_province_input" value="${purchaseInventoryDTO.province}" />
                <input type="hidden" id="select_city_input" value="${purchaseInventoryDTO.city}" />
                <input type="hidden" id="select_township_input" value="${purchaseInventoryDTO.region}" />
            </td>

        &lt;%&ndash;<c:if test="${!isWholesalerVersion}" >&ndash;%&gt;
            &lt;%&ndash;<td class="t_title">地址</td>&ndash;%&gt;
            &lt;%&ndash;<td>&ndash;%&gt;
                &lt;%&ndash;<input type="text" id="address" name="address" maxlength="50" class="textbox"&ndash;%&gt;
                       &lt;%&ndash;value="${purchaseInventoryDTO.address}" style="width:116px;"&ndash;%&gt;
                       &lt;%&ndash;class="checkStringEmpty textbox" kissfocus="on"/>&ndash;%&gt;
            &lt;%&ndash;</td>&ndash;%&gt;
        &lt;%&ndash;</c:if>&ndash;%&gt;
    </tr>
    <tr class="supplierDetailInfo" style="display:none">
        <td class="t_title">座机：</td>
        <td><form:input path="landline" maxlength="20" class="textbox" value="${purchaseInventoryDTO.landline}"/></td>
        <td class="t_title">Email：</td>
        <td>
            <input type="text" id="email" name="email" maxlength="50" class="textbox"
                   value="${purchaseInventoryDTO.email}" kissfocus="on"/>
        </td>
        <td class="t_title">QQ：</td>
        <td><form:input path="qq" class="textbox" maxlength="20" value="${purchaseInventoryDTO.qq}"/></td>
        <td class="t_title">开户行：</td>
        <td>
            <input type="text" id="bank" name="bank" class="textbox" maxlength="20"
                   value="${purchaseInventoryDTO.bank}" kissfocus="on" style="width:147px;"/>
        </td>

        <td></td>
    </tr>
    <tr class="supplierDetailInfo" style="display:none">

        <td class="t_title">开户名：</td>
        <td>
            <input type="text" id="accountName" name="accountName" class="textbox" maxlength="20"
                   value="${purchaseInventoryDTO.accountName}" kissfocus="on"/>
        </td>
        <td class="t_title">账号：</td>
        <td><form:input path="account" class="textbox" maxlength="20"  value="${purchaseInventoryDTO.account}"/></td>
        <td class="t_title">结算方式：</td>
        <td>
            <form:select path="settlementType" id="settlementType" cssStyle="float:left;width:128px;" cssClass="checkSelectChanged">
                <form:option value="" label="-请选择-"/>
                <form:options items="${settlementTypeList}"/>
            </form:select>
            <form:hidden path="settlementType" value="${purchaseInventoryDTO.settlementType}"/>
            <form:hidden path="returnMoneyType" value="${purchaseInventoryDTO.returnMoneyType}"/>
        </td>
        <td class="t_title">发票类型：</td>
        <td>
            <form:select path="invoiceCategory" cssStyle="float:left;width:154px;" cssClass="checkSelectChanged">
                <form:option value="" label="-请选择-"/>
                <form:options items="${invoiceCategoryList}"/>
            </form:select>
            <form:hidden path="invoiceCategory" value="${purchaseInventoryDTO.invoiceCategory}"/>
        </td>
        <td></td>
    </tr>
    <tr class="supplierDetailInfo" style="display:none">
        <td class="t_title" style="width: 190px">经营产品：</td>
        <td colspan="8">
            <div class="warehouseList">
                <label class="rad"><input type="checkbox" name="businessScope1" value="发动机"/>发动机</label>
                <label class="rad"><input type="checkbox" name="businessScope1" value="车盘及车身"/>车盘及车身</label>
                <label class="rad"><input type="checkbox" name="businessScope1" value="电器"/>电器</label>
                <label class="rad"><input type="checkbox" name="businessScope1" value="材料及通用件"/>材料及通用件</label>
                <label class="rad"><input type="checkbox" name="businessScope1" value="汽保设备及工具"/>汽保设备及工具</label>
                <label class="rad"><input type="checkbox" name="businessScope1" value="油品（油品、油脂、添加剂）"/>油品（油品、油脂、添加剂）</label>
                <label class="rad"><input type="checkbox" name="businessScope1" value="汽车用品（美容护理、坐垫脚垫、汽车电子、汽车精品）"/>汽车用品（美容护理、坐垫脚垫、汽车电子、汽车精品）</label>
                <label class="rad"><input type="checkbox" name="businessScope1" value="" id="otherCheckbox"/>其他</label>
                <input type="text" class="txt" id="otherInput" />
            </div>
            <input id="businessScope" type="hidden" name="businessScope" class="txt" maxlength="500" value="${purchaseInventoryDTO.businessScope}"/>

        </td>
    </tr>--%>
    <!-- 供应商相关的隐藏信息 -->
    <input type="hidden" name="abbr" id="abbr" value="${purchaseInventoryDTO.abbr}"/>
    <input type="hidden" name="fax" id="fax" value="${purchaseInventoryDTO.fax}"/>
    <input id="input_address" name="address" type="hidden" class="txt J_address_input" style="width:128px;" value="${purchaseInventoryDTO.address}"/>
    <input type="hidden" id="select_province_input" name="province" value="${purchaseInventoryDTO.province}" />
    <input type="hidden" id="select_city_input" name="city" value="${purchaseInventoryDTO.city}" />
    <input type="hidden" id="select_township_input" name="region" value="${purchaseInventoryDTO.region}" />
    <input type="hidden" id="email" name="email" value="${purchaseInventoryDTO.email}" />
    <input type="hidden" id="qq" name="qq" value="${purchaseInventoryDTO.qq}"/>
    <input type="hidden" id="bank" name="bank" value="${purchaseInventoryDTO.bank}"/>
    <input type="hidden" id="accountName" name="accountName" value="${purchaseInventoryDTO.accountName}"/>
    <input type="hidden" id="account" name="account" value="${purchaseInventoryDTO.account}"/>
    <input type="hidden" id="settlementType" name="settlementType" value="${purchaseInventoryDTO.settlementType}"/>
    <input type="hidden" id="invoiceCategory" name="invoiceCategory" value="${purchaseInventoryDTO.invoiceCategory}"/>
    <input id="businessScope" type="hidden" name="businessScope" class="txt"  value="${purchaseInventoryDTO.businessScope}"/>
</table>
<span class="right_tuihuo"></span>
</div>

<div class="slider-main-area">
<table cellpadding="0" cellspacing="0" class="table2 tabCart tabSales slider-main-table" id="table_productNo">
    <col width="100">
    <col width="200">
    <col width="110">
    <col width="110">
    <col width="110">
    <col width="70">
    <col width="100">
    <col width="60">
    <col width="43">
    <col width="45">
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">
        <col width="65">
    </bcgogo:hasPermission>
    <col width="60">
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">
        <col width="75">
    </bcgogo:hasPermission>
    <tr class="table_title titleBg">
        <td style=" padding-left: 6px;border-left:none;">商品编号</td>
        <td>品名</td>
        <td>品牌/产地</td>
        <td>规格</td>
        <td>型号</td>
        <td>车型</td>
        <td>车辆品牌</td>
        <td>单价</td>
        <td>入库量</td>
        <td>单位</td>
        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">
            <td>小计</td>
        </bcgogo:hasPermission>
            <%--<td class="storage_bin_td">货位</td>--%>
        <td>库存量</td>
        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">
            <td style="border-right:none;">操作<input class="opera2" type="button" style="display:none;"></td>
        </bcgogo:hasPermission>
    </tr>
    <tr class="space">
        <td colspan="15"></td>
    </tr>
    <c:forEach items="${purchaseInventoryDTO.itemDTOs}" var="itemDTO" varStatus="status">
        <tr class="bg item table-row-original">
            <td style="border-left:none;">
                <form:input path="itemDTOs[${status.index}].commodityCode"
                            value='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}'
                            class="table_input checkStringEmpty"
                            title='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}' style="width:85%"
                            maxlength="20"/>
                <input id="itemDTOs0.barcode" name="itemDTOs[${status.index}].barCode" type="hidden" value=""/>
            </td>
            <td>
                <form:hidden path="itemDTOs[${status.index}].id" value='${itemDTO.id!=null?itemDTO.id:""}'/>
                <input type="hidden" value="true" name="itemDTOs[${status.index}].isOldProduct"
                       id="itemDTOs${status.index}.isOldProduct"/>
                <form:hidden path="itemDTOs[${status.index}].productVehicleStatus"
                             value='${itemDTO.productVehicleStatus!=null?itemDTO.productVehicleStatus:""}'
                             class="itemProductVehicleStatus"/>
                <input type="hidden" value="${itemDTO.productVehicleStatus!=null?itemDTO.productVehicleStatus:''}"
                       name="itemDTOs[${status.index}].hidden_productVehicleStatus"
                       id="itemDTOs${status.index}.hidden_productVehicleStatus"/>
                <form:hidden path="itemDTOs[${status.index}].productId"
                             value='${itemDTO.productId!=null?itemDTO.productId:""}'/>
                <form:hidden path="itemDTOs[${status.index}].supplierProductId"
                             value='${itemDTO.supplierProductId!=null?itemDTO.supplierProductId:""}'/>
                <input type="hidden" value="${itemDTO.productId!=null?itemDTO.productId:''}"
                       name="itemDTOs[${status.index}].hidden_productId" id="itemDTOs${status.index}.hidden_productId"/>
                <form:input path="itemDTOs[${status.index}].productName"
                            value='${itemDTO.productName!=null?itemDTO.productName:""}' class="table_input checkStringEmpty"
                            title='${itemDTO.productName!=null?itemDTO.productName:""}' style="width:80%"/>
                <input type="hidden" value="${itemDTO.productName!=null?itemDTO.productName:''}"
                       name="itemDTOs[${status.index}].hidden_productName" id="itemDTOs${status.index}.hidden_productName"/>
                <input type="hidden" id="itemDTOs${status.index}.lastPrice"/>
                <input type="hidden" id="itemDTOs${status.index}.lastPurchasePrice"/>
                <input type="button" class="edit" onfocus="this.blur();" id="itemDTOs${status.index}.editbutton"
                       name="itemDTOs[${status.index}].editbutton" onclick="searchInventoryIndex(this)"/>
            </td>
            <td>
                <form:input path="itemDTOs[${status.index}].brand" value='${itemDTO.brand!=null?itemDTO.brand:""}'
                            class="table_input checkStringEmpty" title='${itemDTO.brand!=null?itemDTO.brand:""}' maxLength="100"/>

                <input type="hidden" value="${itemDTO.brand!=null?itemDTO.brand:''}"
                       name="itemDTOs[${status.index}].hidden_brand" id="itemDTOs${status.index}.hidden_brand"/>
            </td>
            <td>
                <form:input path="itemDTOs[${status.index}].spec" value='${itemDTO.spec!=null?itemDTO.spec:""}'
                            class="table_input checkStringEmpty" title='${itemDTO.spec!=null?itemDTO.spec:""}'/>

                <input type="hidden" value="${itemDTO.spec!=null?itemDTO.spec:''}"
                       name="itemDTOs[${status.index}].hidden_spec" id="itemDTOs${status.index}.hidden_spec"/>
            </td>
            <td>
                <form:input path="itemDTOs[${status.index}].model" value='${itemDTO.model!=null?itemDTO.model:""}'
                            class="table_input checkStringEmpty" title='${itemDTO.model!=null?"":itemDTO.model}'/>
                <input type="hidden" value="${itemDTO.model!=null?itemDTO.model:''}"
                       name="itemDTOs[${status.index}].hidden_model" id="itemDTOs${status.index}.hidden_model"/>
            </td>
            <td>
                <form:input path="itemDTOs[${status.index}].vehicleModel"
                            value='${itemDTO.vehicleModel!=null?itemDTO.vehicleModel:""}'
                            class="table_input checkStringEmpty"
                            title="${itemDTO.vehicleModel}" maxLength="200"/>

                <input type="hidden" value="${itemDTO.vehicleModel!=null?itemDTO.vehicleModel:''}"
                       name="itemDTOs[${status.index}].hidden_vehicleModel"
                       id="itemDTOs${status.index}.hidden_vehicleModel"/>
            </td>
            <td><form:input path="itemDTOs[${status.index}].vehicleBrand"
                            value='${itemDTO.vehicleBrand!=null?itemDTO.vehicleBrand:""}'
                            class="table_input checkStringEmpty"
                            title="${itemDTO.vehicleBrand}" maxLength="200"/>

                <input type="hidden" value="${itemDTO.vehicleBrand!=null?itemDTO.vehicleBrand:''}"
                       name="itemDTOs[${status.index}].hidden_vehicleBrand"
                       id="itemDTOs${status.index}.hidden_vehicleBrand"/>
            </td>
            <td style="display:none"><form:hidden disabled="disabled" path="itemDTOs[${status.index}].vehicleYear"
                                                  class="table_input"
                                                  title="${itemDTO.vehicleYear!=null?itemDTO.vehicleYear:''}"/>

                <input type="hidden" disabled="disabled" name="itemDTOs[${status.index}].hidden_vehicleYear"
                       id="itemDTOs${status.index}.hidden_vehicleYear"/>
            </td>
            <td style="display:none"><form:hidden disabled="disabled" path="itemDTOs[${status.index}].vehicleEngine"
                                                  class="table_input"
                                                  title='${itemDTO.vehicleEngine!=null?itemDTO.vehicleEngine:""}'/>

                <input type="hidden" disabled="disabled" name="itemDTOs[${status.index}].hidden_vehicleEngine"
                       id="itemDTOs${status.index}.hidden_vehicleEngine"/>
            </td>

            <td style="color:#FF6700;">
                <form:input path="itemDTOs[${status.index}].purchasePrice"
                            value='${itemDTO.purchasePrice!=null?itemDTO.purchasePrice:""}'
                            class="itemPurchasePrice table_input checkNumberEmpty"
                            title='${itemDTO.purchasePrice!=null?itemDTO.purchasePrice:""}' data-filter-zero="true"/>
            </td>
            <td style="color:#FF0000;">
                <form:input path="itemDTOs[${status.index}].amount" value='${itemDTO.amount!=null?itemDTO.amount:""}'
                            class="itemAmount table_input checkNumberEmpty"
                            title='${itemDTO.amount!=null?itemDTO.amount:""}' data-filter-zero="true"/>
            </td>
            <td>
                <form:input path="itemDTOs[${status.index}].unit" value='${itemDTO.unit!=null?itemDTO.unit:""}'
                            class="itemUnit table_input checkStringEmpty" cssStyle="width: 85%"
                            title='${itemDTO.unit!=null?itemDTO.unit:""}'/>
                <form:hidden path="itemDTOs[${status.index}].storageUnit" value="${itemDTO.storageUnit}"
                             class="itemStorageUnit table_input"/>
                <form:hidden path="itemDTOs[${status.index}].sellUnit" value="${itemDTO.sellUnit}"
                             class="itemSellUnit table_input"/>
                <form:hidden path="itemDTOs[${status.index}].rate" value='${itemDTO.rate != null ? itemDTO.rate : ""}' class="itemRate table_input"/>
            </td>
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">
                <td>
				<span id="itemDTOs${status.index}.total_span" name="itemDTOs[${status.index}].total"
                      class="itemTotalSpan" data-filter-zero="true"
                      title='${itemDTO.total!=null?itemDTO.total:""}'>${itemDTO.total!=null?itemDTO.total:""}</span>
                    <input type="hidden" id="itemDTOs${status.index}.total" name="itemDTOs[${status.index}].total" class="itemTotal" value="${itemDTO.total!=null?itemDTO.total:''}"/>
                </td>
            </bcgogo:hasPermission>
                <%--<td class="storage_bin_td">--%>
                <%--<form:input path="itemDTOs[${status.index}].storageBin" maxlength="10" value='${itemDTO.storageBin}'--%>
                <%--class="table_input" title='${itemDTO.storageBin}' cssStyle="width:90%"/>--%>
                <%--</td>--%>
            <td>
            <span id="itemDTOs${status.index}.inventoryAmountSpan"
                  name="itemDTOs[${status.index}].inventoryAmountSpan" data-filter-zero="true">${itemDTO.inventoryAmountApprox}</span>
                <form:hidden path="itemDTOs[${status.index}].inventoryAmount" value='${itemDTO.inventoryAmount}'
                             class="itemInventoryAmount table_input"/>
                <span style="display: none;">新</span>
            </td>

            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">
                <td style="border-right:none;">
                    <a class="opera1" type="button" id="itemDTOs${status.index}.deletebutton"
                           name="itemDTOs[${status.index}].deletebutton">删除</a>
                </td>
            </bcgogo:hasPermission>
        </tr>
    </c:forEach>
</table>

<div class="slider-sub-area table2">
    <table id="table_productDetail" class="slider-sub-table table2">
        <bcgogo:permission>
            <bcgogo:if permissions="WEB.TXN.SALE_MANAGE.SALE">
                <col width="20%" class="storage_bin_col">
                <col width="20%">
                <col width="20%">
                <col width="20%">
                <col width="20%" class="trade_price_col">
            </bcgogo:if>
            <bcgogo:else>
                <col width="25%" class="storage_bin_col">
                <col width="25%">
                <col width="25%">
                <col width="25%">
            </bcgogo:else>
        </bcgogo:permission>
        <tr class="table_title titleBg">
            <td class="storage_bin_td" style="border-top:2px solid #6D8FB9">货位</td>
            <td class="td_input" style="border-top:2px solid #6D8FB9">
                <input type="button" value="商品分类" class="overRuKu" onclick="showSetproductKind()"/>
            </td>
            <input type="hidden" id="oldKindName"/>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS">
                    <td class="td_input" style="border-top:2px solid #6D8FB9">
                        <input class="overRuKu" type="button" onclick="showSetLimit(false);" value="下限"
                               onfocus="this.blur();"
                               title="下限">
                    </td>
                    <td class="td_input" style="border-top:2px solid #6D8FB9">
                        <input class="overRuKu" type="button" onclick="showSetLimit(false);" value="上限"
                               onfocus="this.blur();"
                               title="上限">
                    </td>
                </bcgogo:if>
                <bcgogo:else>
                    <td class="td_input" style="border-top:2px solid #6D8FB9">
                        下限
                    </td>
                    <td class="td_input" style="border-top:2px solid #6D8FB9">
                        上限
                    </td>
                </bcgogo:else>
            </bcgogo:permission>
            <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE">
                <td class="trade_price_td" style="border-top:2px solid #6D8FB9">
                    <input class="overRuKu" type="button" value="批发价" onclick="showSetTradePrice();"
                           onfocus="this.blur();" style="width:70px;">
                </td>
                <td class="td_input" style="border-top:2px solid #6D8FB9">
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.SALE_PRICE_SETTING">
                            <input type="button" value="销售价" class="overRuKu" onclick="showIt()"/>
                        </bcgogo:if>
                        <bcgogo:else>
                            <input type="button" value="销售价" class="overRuKu" style="cursor: default;"/>
                        </bcgogo:else>
                    </bcgogo:permission>
                </td>
            </bcgogo:hasPermission>
        </tr>

        <tr class="space">
            <td colspan="5"></td>
        </tr>

        <c:forEach items="${purchaseInventoryDTO.itemDTOs}" var="itemDTO" varStatus="status">
        <tr class="item2 table-row-original">
            <td class="storage_bin_td">
                <form:input path="itemDTOs[${status.index}].storageBin" maxlength="10" value='${itemDTO.storageBin}'
                            class="table_input" title='${itemDTO.storageBin}' cssStyle="width:90%"/>
            </td>
            <td class="txt_right">
                <form:input path="itemDTOs[${status.index}].productKind" maxlength="50" value='${itemDTO.productKind}'
                            class="table_input" title='${itemDTO.productKind}' cssStyle="width:90%"/>
            </td>
            <td class="txt_right">
                <form:input path="itemDTOs[${status.index}].lowerLimit" cssClass="order_input_lowerLimit checkNumberEmpty"
                            value="${itemDTO.lowerLimit}" cssStyle="width:100%"/>
            </td>
            <td class="txt_right">
                <form:input path="itemDTOs[${status.index}].upperLimit" cssClass="order_input_upperLimit checkNumberEmpty"
                            value="${itemDTO.upperLimit}" cssStyle="width:100%"/>
            </td>
            <td class="trade_price_td">
                <fmt:formatNumber value='${itemDTO.tradePrice}' pattern='###.##' var="formattedPrice"/>
                <form:input path="itemDTOs[${status.index}].tradePrice" maxlength="13" class="table_input tradePriceCheck"
                            value="${formattedPrice}" cssStyle="width:90%" title="${formattedPrice}"/>
                <input type="hidden" id="itemDTOs${status.index}.inventoryAveragePrice" value="${itemDTO.inventoryAveragePrice}">
            </td>
            <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE">
                <td>
                    <form:input path="itemDTOs[${status.index}].recommendedPrice"
                                value='${itemDTO.recommendedPrice!=null?itemDTO.recommendedPrice:"0"}'
                                class="itemRecommendedPrice table_input checkNumberEmpty priceCheck"
                                title='${itemDTO.recommendedPrice!=null?itemDTO.recommendedPrice:""}'/>
                </td>
            </bcgogo:hasPermission>
            </c:forEach>
        </tr>
    </table>
</div>

<i class="slider-btnExpand"><i></i></i>
</div>
<div class="height"></div>
<div class="tableInfo">

    <div class="s_total" style="float:right; width:150px;">
        <div>
            <div class="t_total">合计：<span id="totalSpan" class="yellow_color" data-filter-zero="true"> ${purchaseInventoryDTO.total!=0?purchaseInventoryDTO.total:"0"}</span>元
                <form:hidden path="total" value='${purchaseInventoryDTO.total!=null?purchaseInventoryDTO.total:0.00}'/>
            </div>
            <div style="display:none">
                <form:hidden path="stroageActuallyPaid" cssClass="checkNumberEmpty" type="text" style="width:40px;"/>
                <form:hidden path="stroageCreditAmount" cssClass="checkNumberEmpty" type="text" style="width:40px;"
                             value='${purchaseInventoryDTO.stroageCreditAmount!=null?purchaseInventoryDTO.stroageCreditAmount:""}'/>

                <form:hidden path="stroageSupplierDeduction" style="width:40px;"/>
            </div>
        </div>
    </div>
</div>

</div>
<!--结算详细弹出框-->
    <%--<div id="payDetail" style="position: fixed; left:33%; top: 37%; z-index: 10; display: none;">--%>
    <%--<jsp:include page="payDetail.jsp"></jsp:include>--%>
    <%--</div>--%>
    <%--<input type="hidden" id="settledAmount" style="width:100px;"/>--%>
<form:hidden path="deduction" value="${purchaseInventoryDTO.deduction}"/>
<form:hidden path="paidtype" value=''/>
<form:hidden path="creditAmount" value="${purchaseInventoryDTO.creditAmount}"/>
<form:hidden path="cash" value="${purchaseInventoryDTO.cash}"/>
<form:hidden path="bankCardAmount" value="${purchaseInventoryDTO.bankCardAmount}"/>
<form:hidden path="checkAmount" value="${purchaseInventoryDTO.checkAmount}"/>
<form:hidden path="checkNo" value="${purchaseInventoryDTO.checkNo}"/>
<form:hidden path="actuallyPaid" value="${purchaseInventoryDTO.actuallyPaid}"/>
<form:hidden path="depositAmount" value="${purchaseInventoryDTO.depositAmount}"/>

<input type="hidden" id="hiddenSubmitClick"/>
</div>
<div class="danju_beizhu clear" style="float:left ">
    <span>备注:</span>
    <input type="text" id="memo" name="memo" value="${purchaseInventoryDTO.memo}"
           class="memo checkStringEmpty textbox" maxlength="500" kissfocus="on"/>
</div>


<div class="btn_div_Img" id="saveDraftOrder_div">
    <input type="button" id="saveDraftBtn" class="i_savedraft" value="" onfocus="this.blur();"/>
    <div style="width:100%; ">保存草稿</div>
</div>
<bcgogo:permission>
    <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE.CANCEL">
        <div class="invalidImg" id="invalid_div">
            <input id="nullifyBtn" type="button" onfocus="this.blur();"/>
            <div class="invalidWords" id="invalidWords">作废</div>
        </div>
    </bcgogo:if>
</bcgogo:permission>
<bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE.COPY">
    <div class="copyInput_div" id="copyInput_div">
        <input id="copyInput" type="button" onfocus="this.blur();"/>
        <div class="copyInput_text_div" id="copyInput_text">复制</div>
    </div>
</bcgogo:hasPermission>
<div class="btn_div_Img" id="cancel_div">
    <input id="cancelBtn" type="button" class="cancel j_btn_i_operate" onfocus="this.blur();"/>
    <div class="optWords">清空</div>
</div>
<div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE.SAVE">
        <div class="btn_div_Img" id="inventorySave_div">
            <input id="inventorySaveBtn" type="button" class="sureInventory j_btn_i_operate"
                   onfocus="this.blur();"/>

            <div class="optWords">确认入库</div>
        </div>
    </bcgogo:hasPermission>
    <bcgogo:permission>
        <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE.PRINT">
            <input id="print" type="hidden" name="print" value="${purchaseInventoryDTO.print}"/>
            <c:if test="${purchaseInventoryDTO.receiptNo != null}">
                <div class="btn_div_Img" id="print_div">
                    <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                    <div class="optWords">打印</div>
                </div>
            </c:if>
        </bcgogo:if>
    </bcgogo:permission>
    <div class="btn_div_Img" id="return_to_repair_div" style="display: none;">
        <input id="return_to_repair" class="return_list" type="button" onfocus="this.blur();">
        <div class="sureWords">返回列表</div>
    </div>
</div>
</div>
</form:form>

<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id">
    </div>
</div>
<div class="zuofei" id="zuofei"></div>
<div id="mask" style="display:block;position: absolute;">
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" height="1000px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:6; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_Limit" style="position:absolute;z-index:5; left:40%; top:35%; display:none;"
        allowtransparency="true" width="286px" height="200px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_Kind" style="position:absolute;z-index:5; left:40%; top:35%; display:none;"
        allowtransparency="true" width="286px" height="400px" frameborder="0" src="" scrolling="no"></iframe>
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
                <td>供应商</td>
                <td>采购商品</td>
                <td class="tab_last"></td>
            </tr>
        </table>
        <!--分页-->
        <div class="hidePageAJAX">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="draft.do?method=getDraftOrders"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,orderTypes:'INVENTORY'}"></jsp:param>
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


<!-- 供应商下拉菜单 zhangchuanlong-->
<div id="div_brandvehiclelicenceNo" class="i_scroll" style="display:none;width:300px;">
    <div class="Container" style="width:300px;">
        <div id="Scroller-1licenceNo" style="width:300px;">
            <div class="Scroller-ContainerSupplier" id="Scroller-Container_idlicenceNo">
            </div>
        </div>
    </div>
</div>
<div id="div_serviceName" class="i_scroll" style="display:none;width:150px;">
    <div class="Scroller-Container" id="Scroller-Container_ServiceName">
    </div>
</div>
<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:150px; display:none;"
        allowtransparency="true" width="900" height="470px" frameborder="0" src="" scrolling="no"></iframe>
<!-- 搜索下拉, TODO 以后移到组件里 -->
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>

<!-- 选择现金或定金 确认弹出框-->
<div id="sureCashOrDeposit" style="position: fixed; left:30%; top: 37%; z-index: 14; display: none;">
    <jsp:include page="sureCashOrDeposit.jsp"></jsp:include>
</div>
<!-- 挂账，扣款免付确认弹出框-->
<div id="creditDeductionBtn" style="position: fixed; left:30%; top: 37%; z-index:11; display: none;">
    <jsp:include page="conform.jsp"></jsp:include>
</div>
<div id="dialog-confirm" title="提醒" style="display:none">
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

<div id="inputMobile" style="display: none">
    <div style="margin-left: 45px;margin-top: 15px">
        <label>手机号：</label>
        <input type="text" id="divMobile" style="width:125px;height: 20px" kissfocus="on">
    </div>
</div>
<style>
    .Scroller-Container a {
        /* 引入up.css导致下拉列表样式被覆盖为白色，将应付款确认框替换为JQuery UI后可删除  */
        color: #000000;
    }
</style>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

<div class="alertMain productDetails" id="mobileDupTip" style ="display:none;" >
    <div class="height"></div>
    <div id="mobileDupCustomers">
        <div>该手机存在重名供应商，请选择</div>
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
</body>
</html>
