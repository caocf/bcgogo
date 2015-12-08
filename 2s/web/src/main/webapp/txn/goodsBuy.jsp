<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>采购单</title>
<%
    boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn(WebUtil.getShopId(request));//选配仓位功能 默认开启这个功能false
    boolean tradePriceTag = ServiceManager.getService(IShopConfigService.class).isTradePriceSwitchOn(WebUtil.getShopId(request),WebUtil.getShopVersionId(request));//选配批发价功能
%>
<link rel="stylesheet" type="text/css" href="styles/goodsBuyNew<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/cuxiao<%=ConfigController.getBuildVersion()%>.css"/>

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
<style type="text/css">
    .item input {
        text-overflow: ellipsis;
    }
</style>

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
<link rel="stylesheet" type="text/css" href="styles/cuSearch<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/activeRecommendSupplierTip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<style type="text/css">
    .item input {
        text-overflow: ellipsis;
    }
</style>


<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/goodsBuy<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/supplierSearch<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/setLimit<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
<bcgogo:permissionParam permissions="WEB.VERSION.ACTIVE_RECOMMEND_SUPPLIER">
    APP_BCGOGO.Permission.Version.ActiveRecommendSupplier=${WEB_VERSION_ACTIVE_RECOMMEND_SUPPLIER}
</bcgogo:permissionParam>
defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.PURCHASE_MANAGE.PURCHASE");
defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
$(document).ready(function () {
    $("#purchaseOrderForm input").bind("mouseover", function () {
        this.title = this.value;
    });

    $("input[id$='.price']").live("blur", function () {
        activeRecommendSupplierTip($(this).parent().parent(), getOrderType());
    });

    $(document).click(function (event) {
        if ($(event.target).attr("id") !== "div_brand") {
            $("#div_brand").css("display", "none");
        }
    });

    // 初始化 datepicker
    $("#deliveryDateStr,#deliveryDateInput")
            .datepicker({
                "numberOfMonths": 1,
                "showButtonPanel": true,
                "changeYear": true,
                "changeMonth": true,
                "yearRange": "c-100:c+100",
                "yearSuffix": "",
                "showHour": false,
                "showMinute": false
            })
            .bind("click", function (event) {
                $(this).blur();
            });


    $("#billProducer").live("click", function (event) {
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

    $(document).click(function (e) {
        var target = e.target;
        if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName") {
            $("#div_serviceName").hide();
        }
    });

    $("#deleteBillProducer")
            .hide()
            .live("click", function () {
                $("#billProducer").val("");
                $("#billProducerId").val("");
                $("#deleteBillProducer").hide();
            });

    $("#billProducerDiv")
            .mouseenter(function () {
                if ($("#billProducer").val() && !isDisable()) {
                    $("#deleteBillProducer").show();
                }
            })
            .mouseleave(function () {
                $("#deleteBillProducer").hide();
            });
    if ($("#purchaseOrderMessage").val()) {
        if ($("#purchaseOrderMessage").attr("resultOperation") == "MODIFY_COMMODITY_CODE") {
            nsDialog.jConfirm($("#purchaseOrderMessage").val(), null, function (returnVal) {
                if (returnVal) {
                    window.open($("#purchaseOrderMessage").attr("resultDate"));
                }
            });
        } else if ($("#purchaseOrderMessage").attr("resultOperation") == "ALERT") {
            nsDialog.jAlert($("#purchaseOrderMessage").val(), null, function () {
            });
        } else if ($("#purchaseOrderMessage").attr("resultOperation") == "REDIRECT_SHOW") {
            nsDialog.jAlert($("#purchaseOrderMessage").val(), null, function () {
                window.location = "RFbuy.do?method=show&id=" + $("#id").val();
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

    window.timerCheckHistoryButton = 0;
    function toggleHistoryButton(){
        $("#historySearchButton_id").toggle( !G.isEmpty(G.normalize($("#supplier").val())) );
        timerCheckHistoryButton = setTimeout(toggleHistoryButton, 500);
    }
    timerCheckHistoryButton = setTimeout(toggleHistoryButton, 500);

    $("#payDiv").mouseenter(function () {
        $("#yingshou").css("color","#FFFFFF");
        $("#yingfu").css("color","#FFFFFF");
    });

    $("#payDiv").mouseleave(function () {
        $("#yingshou").css("color","#AAAAAA");
        $("#yingfu").css("color","#1F541E");
    });

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

    $("#deliveryDateDialog").dialog({
        autoOpen: false,
        resizable: false,
        title: "选择预计交货日期",
        height: 130,
        width: 390,
        modal: true,
        closeOnEscape: false,
        showButtonPanel: true
    });
    $("#confirmBtn1").click(function(){
        $("#deliveryDateStr").val($("#deliveryDateInput").val());
        $("#deliveryDateInput").val('');
        $("#deliveryDateDialog").dialog("close");
        if(!G.isEmpty($("#btnType").val())) {
            $("#" + $("#btnType").val()).click();
        }

    });
    $("#cancleBtn").click(function(){
        $("#deliveryDateInput").val('');
        $("#deliveryDateDialog").dialog("close");
    });
});


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
        $("#div_serviceName").css({"display": "none"});
    }
    else {
        $("#div_serviceName").css({
            "display": "block", "position": "absolute",
            "left": x + "px",
            "top": y + offsetHeight + 6 + "px",
            "width": "90px;",
            "overflowY": "scroll",
            "overflowX": "hidden"
        });
        $("#Scroller-Container_ServiceName").html("");

        for (var i = 0; i < jsonObject.length; i++) {
            var id = jsonObject[i].idStr;
            var a = $("<a id=" + id + "></a>");
            a.css("color", "#000")
                    .html(jsonObject[i].name + "<br>")
                    .bind("mouseenter", function () {
                        $("#Scroller-Container_ServiceName > a").removeAttr("class");
                        $(this).attr("class", "hover");
                        // jQuery(this).html();
                        selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name;
                        selectItemNum = parseInt(this.id.substring(10));
                    })
                    .click(function () {
                        var sty = this.id;
                        $("#billProducerId").val(sty);
                        //取的第一字符串
                        $(domObject).val(selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name);
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
            var inventoryAveragePrice = $("#" + idPrefix + "\\.price").val();
            var newTradePrice = dataTransition.simpleRounding(inventoryAveragePrice * (1 + percent / 100) + value * 1,2);
            if(newTradePrice>0){
                $("#" + idPrefix + "\\.tradePrice").val(newTradePrice);
            }
        }
    });
}
</script>
</head>

<body class="bodyMain" pagetype="order" ordertype="">
<script type="text/javascript">
    $(document.body).attr("ordertype", App.OrderTypes.PURCHASE);
</script>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<bcgogo:permissionParam permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS">
	<input type="hidden" id="permissionInventoryAlarmSettings" value="${WEB_TXN_INVENTORY_MANAGE_ALARM_SETTINGS}"/>
    <input type="hidden" id="permissionGoodsSale" value="${permissionParam2}"/>
</bcgogo:permissionParam>
<bcgogo:permissionParam resourceType="menu" permissions="WEB.TXN.SALE_MANAGE.SALE">
	<input type="hidden" id="permissionGoodsSale" value="${WEB_TXN_SALE_MANAGE_SALE}"/>
</bcgogo:permissionParam>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>

<div class="i_main clear">
<jsp:include page="txnNavi.jsp">
	<jsp:param name="currPage" value="purchase"/>
</jsp:include>
<jsp:include page="purchaseNavi.jsp">
	<jsp:param name="currPage" value="goodsBuy"/>
</jsp:include>

<div class="clear"></div>
<div class="i_mainRight" id="i_mainRight">
<div class="cartTop"></div>
<div class="cartBody">
<form:form commandName="purchaseOrderDTO" id="purchaseOrderForm" action="RFbuy.do?method=save" method="post" name="thisform" class="J_leave_page_prompt">
<jsp:include page="unit.jsp"/>
<form:hidden path="shopId" value="${sessionScope.shopId}"/>
<form:hidden path="id" value="${purchaseOrderDTO.id}"/>
<form:hidden path="status" value="${purchaseOrderDTO.status}"/>
<form:hidden path="draftOrderIdStr" value="${purchaseOrderDTO.draftOrderIdStr}"/>
<form:hidden path="supplierId" value="${purchaseOrderDTO.supplierId}"/>
<form:hidden path="contactId" value="${purchaseOrderDTO.contactId}"/>
 <form:hidden path="selectSupplier" value="${WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER}"/>
<input id="type" name="type" type="hidden" value="${param.type}">
<input id="orderType" name="orderType" value="purchaseOrder" type="hidden"/>
<input type="hidden" id="isAdd" />
<c:if test="${!empty result}">
    <input type="hidden" id="purchaseOrderMessage" value="${result.msg}" resultDate="${result.data}" resultOperation="${result.operation}">
</c:if>

<div class="tuihuo_first">
    <span class="left_tuihuo"></span>
    <table id="table" class="elivate">
        <col width="110">
        <col width="160">
        <col width="100">
        <col width="120">
        <col width="100">
        <col width="70">
        <col width="100">
        <col width="110">
        <col width="60">
        <tr>
            <td class="t_title">订单号：</td>
            <td colspan="8" style="text-align:left;" class="hover">
                    <%--${purchaseOrderDTO.receiptNo}--%>
                <a id="receiptNoSpan" class="receiptNoSpan">系统自动生成</a>
                <input type="hidden" name="receiptNo" id="receiptNo" value="${purchaseOrderDTO.receiptNo}"/>
            </td>
        </tr>
        <tr>
            <c:if test="${!empty purchaseOrderDTO.saleOrderReceiptNo}">
                <td class="t_title">供货单号：</td>
            </c:if>
            <c:if test="${!empty purchaseOrderDTO.saleOrderReceiptNo}">
                <td style="text-align:left;" class="hover">
                    <label class="hover">${purchaseOrderDTO.saleOrderReceiptNo}</label>
                </td>
            </c:if>
            <td class="t_title">制单人：</td>
            <td style="text-align:left;">
                <div id="billProducerDiv">
                    <form:input path="billProducer" value="${purchaseOrderDTO.billProducer}" cssStyle="width: 50px;" initbillproducervalue="${purchaseOrderDTO.billProducer}"
                                readOnly="true" class="checkStringChanged textbox" initacceptorvalue="${purchaseOrderDTO.billProducer}"/>
                    <img src="images/list_close.png" id="deleteBillProducer" style="width:12px;cursor:pointer;margin-top:3px;margin-left:2px;"/>
                    <input type="hidden" name="billProducerId" id="billProducerId"/>
                </div>
                <form:hidden path="editor" value="${purchaseOrderDTO.editor}"/>
                <form:hidden path="editorId" value="${purchaseOrderDTO.editorId}"/>
            </td>
            <td class="t_title">采购日期：</td>
            <td style="text-align:left;">
                <form:hidden path="editDateStr" value="${purchaseOrderDTO.editDateStr}"/>
                <c:choose>
                    <c:when test="${!empty purchaseOrderDTO.id}">
                        ${purchaseOrderDTO.vestDateStr}
                        <form:hidden path="vestDateStr" value="${purchaseOrderDTO.vestDateStr}" ordertype="purchase" id="orderVestDate"/>
                    </c:when>
                    <c:otherwise>
                        <form:input path="vestDateStr" ordertype="purchase" id="orderVestDate" cssStyle="width:100px;" readonly="true" size="10" value="${purchaseOrderDTO.vestDateStr}"
                                    cssClass="checkStringChanged textbox" lastvalue="${purchaseOrderDTO.vestDateStr}" initordervestdatevalue="${purchaseOrderDTO.vestDateStr}"/>
                    </c:otherwise>
                </c:choose>
            </td>
            <td class="t_title">
                应收应付：
            </td>
            <td>
                <div class="pay" id="duizhan" style="width:160px;">
                    <a class="payMoney" style="color:#272727;">
                        应收<span class="arialFont">&yen;</span><span id="receivable" data-filter-zero="true">${totalReceivable == null?'0':totalReceivable}</span>
                    </a>
                    <a class="fuMoney" style="color:#272727;">
                        应付<span class="arialFont">&yen;</span><span id="payable" data-filter-zero="true">${totalPayable == null?'0':totalPayable}</span>
                    </a>
                </div>
            </td>
            <td></td>
        </tr>
        <tr>
            <td class="t_title">供应商<a style="color:#F00000;">*</a>：</td>
            <td>
                <input type="text" id="supplier" name="supplier" maxlength="20" class="supplierSuggestion checkStringEmpty textbox"
                       value="${purchaseOrderDTO.supplier}" style="width: 116px;" kissfocus="on"/>
                    <%--<img src="images/star.jpg" style="float:left;"/>--%>
                <input type="button" id="historySearchButton_id" alt="" title="历史查询" class="historySearchButton_c"
                       style="display:block; width:25px;height: 25px; float:left; margin-top:-5px;"/>
                <form:hidden path="supplierShopId" autocomplete="off" value="${purchaseOrderDTO.supplierShopId}"/>
            </td>
            <td class="t_title">座机：</td>
            <td>
                <form:input path="landline" id="landline" maxlength="15" value="${purchaseOrderDTO.landline}" cssStyle="width: 100px;" cssClass="checkStringEmpty textbox"/>
            </td>
            <td class="t_title">联系人：</td>
            <td>
                <input type="text" id="contact" name="contact" maxlength="20" value="${purchaseOrderDTO.contact}"
                       class="checkStringEmpty textbox" kissfocus="on"/>
            </td>
            <td class="t_title">联系电话：</td>
            <td>
                <form:input path="mobile" id="mobile" maxlength="11" value="${purchaseOrderDTO.mobile}" cssStyle="width: 100px;" cssClass="checkStringEmpty textbox"/>
                <input type="hidden" id="hiddenMobile" value="${purchaseOrderDTO.mobile}"/>
            </td>

            <td>
                <%--<a id="clickMore" class="down blue_color">详细</a>--%>
                <a id="orderSupplierInfo" class="down blue_color">详细</a>
            </td>
        </tr>

        <!-- 供应商相关的隐藏信息 -->
        <input type="hidden" name="abbr" id="abbr" value="${purchaseOrderDTO.abbr}"/>
        <input type="hidden" name="fax" id="fax" value="${purchaseOrderDTO.fax}"/>
        <input id="input_address" name="address" type="hidden" class="txt J_address_input" style="width:128px;" value="${purchaseOrderDTO.address}"/>
        <input type="hidden" id="select_province_input" name="province" value="${purchaseOrderDTO.province}" />
        <input type="hidden" id="select_city_input" name="city" value="${purchaseOrderDTO.city}" />
        <input type="hidden" id="select_township_input" name="region" value="${purchaseOrderDTO.region}" />

        <input type="hidden" id="email" name="email" value="${purchaseOrderDTO.email}" />
        <input type="hidden" id="qq" name="qq" value="${purchaseOrderDTO.qq}"/>
        <input type="hidden" id="bank" name="bank" value="${purchaseOrderDTO.bank}"/>
        <input type="hidden" id="accountName" name="accountName" value="${purchaseOrderDTO.accountName}"/>
        <input type="hidden" id="account" name="account" value="${purchaseOrderDTO.account}"/>
        <input type="hidden" id="settlementType" name="settlementType" value="${purchaseOrderDTO.settlementType}"/>
        <input type="hidden" id="invoiceCategory" name="invoiceCategory" value="${purchaseOrderDTO.invoiceCategory}"/>
        <input id="businessScope" type="hidden" name="businessScope" class="txt"  value="${purchaseOrderDTO.businessScope}"/>
    </table>
    <span class="right_tuihuo"></span>
</div>

<div class="slider-main-area">
<table cellpadding="0" cellspacing="0" class="table2 slider-main-table" id="table_productNo">
    <col width="90">
    <col width="100">
    <col width="90">
    <col width="90">
    <col width="90">
    <col width="75">
    <col width="75">
        <%--<col width="150">--%>
    <col width="65">
    <col width="45">
    <col width="45">
    <col width="65">
    <col width="75">
    <col width="75">
    <tr class="titleBg table_title">
        <td style="border-left:none;padding-left: 5px">商品编号</td>
        <td>品名</td>
        <td>品牌/产地</td>
        <td>规格 </td>
        <td>型号</td>
        <td>车型 </td>
        <td>车辆品牌</td>
        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
            <td>单价</td>
        </bcgogo:hasPermission>
        <td>采购量</td>
        <td>单位</td>
        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
            <td>小计</td>
        </bcgogo:hasPermission>
        <td>
            <span id="inventoryAmountTitle">库存量</span>
        </td>
        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
            <td style="border-right:none;">操作<input class="opera2" type="button" style="display:none;"></td>
        </bcgogo:hasPermission>
    </tr>
    <tr class="space">
        <td colspan="15"></td>
    </tr>
    <c:forEach items="${purchaseOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">
        <c:if test="${itemDTO!=null}">
            <tr class="bg item table-row-original">
                <td style="border-left:none;">
                    <form:input path="itemDTOs[${status.index}].commodityCode"
                                value='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}'
                                class="table_input checkStringEmpty"
                                title='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}' style="width:90%"
                                maxlength="20"/>
                </td>

                <td><form:hidden path="itemDTOs[${status.index}].id" value="${itemDTO.id}"/>
                    <input type="hidden" value="true" name="itemDTOs[${status.index}].isOldProduct"
                           id="itemDTOs${status.index}.isOldProduct"/>
                    <form:hidden path="itemDTOs[${status.index}].productVehicleStatus"
                                 value="${itemDTO.productVehicleStatus}" class="itemProductVehicleStatus"/>
                    <input type="hidden" value="${itemDTO.promotionsInfoJson}" name="itemDTOs[${status.index}].promotionsInfoJson" id="itemDTOs${status.index}.promotionsInfoJson"/>

                    <input type="hidden"
                           value="${itemDTO.productVehicleStatus!=null?itemDTO.productVehicleStatus:''}"
                           name="itemDTOs[${status.index}].hidden_productVehicleStatus"
                           id="itemDTOs${status.index}.hidden_productVehicleStatus"/>
                    <form:hidden path="itemDTOs[${status.index}].vehicleBrandId" value="${itemDTO.vehicleBrandId}"/>
                    <form:hidden path="itemDTOs[${status.index}].vehicleModelId" value="${itemDTO.vehicleModelId}"/>
                    <form:hidden disabled="disabled" path="itemDTOs[${status.index}].vehicleYearId"/>
                    <form:hidden disabled="disabled" path="itemDTOs[${status.index}].vehicleEngineId"/>
                    <form:hidden path="itemDTOs[${status.index}].productId" value="${itemDTO.productId}"/>
                    <input type="hidden" value="${itemDTO.productId!=null?itemDTO.productId:''}"
                           name="itemDTOs[${status.index}].hidden_productId"
                           id="itemDTOs${status.index}.hidden_productId"/>

                    <form:input path="itemDTOs[${status.index}].productName" value="${itemDTO.productName}"
                                cssStyle="width:80%" title="${itemDTO.productName}" class="table_input checkStringEmpty"/>

                    <input type="hidden" value="${itemDTO.productName!=null?itemDTO.productName:''}"
                           name="itemDTOs[${status.index}].hidden_productName"
                           id="itemDTOs${status.index}.hidden_productName"/>
                    <input type="hidden" id="itemDTOs${status.index}.lastPrice"/>
                    <input type="hidden" id="itemDTOs${status.index}.lastPurchasePrice"/>
                    <input type="hidden" class="edit" onfocus="this.blur();" id="itemDTOs${status.index}.editbutton"
                           name="itemDTOs[${status.index}].editbutton" onclick="searchInventoryIndex(this)"/>
                </td>
                <td><form:input path="itemDTOs[${status.index}].brand" value="${itemDTO.brand}"
                                title="${itemDTO.brand}" cssClass="table_input checkStringEmpty"/>
                    <input type="hidden" value="${itemDTO.brand!=null?itemDTO.brand:''}"
                           name="itemDTOs[${status.index}].hidden_brand" id="itemDTOs${status.index}.hidden_brand"/>
                </td>
                <td><form:input path="itemDTOs[${status.index}].spec" value="${itemDTO.spec}"
                                title="${itemDTO.spec}" cssClass="table_input checkStringEmpty"/>
                    <input type="hidden" value="${itemDTO.spec!=null?itemDTO.spec:''}"
                           name="itemDTOs[${status.index}].hidden_spec" id="itemDTOs${status.index}.hidden_spec"/>
                </td>
                <td><form:input path="itemDTOs[${status.index}].model" value="${itemDTO.model}"
                                title="${itemDTO.model}" cssClass="table_input checkStringEmpty"/>
                    <input type="hidden" value="${itemDTO.model!=null?itemDTO.model:''}"
                           name="itemDTOs[${status.index}].hidden_model" id="itemDTOs${status.index}.hidden_model"/>
                </td>

                <td><form:input path="itemDTOs[${status.index}].vehicleModel" value="${itemDTO.vehicleModel}"
                                title="${itemDTO.vehicleModel}" cssClass="table_input checkStringEmpty" maxlength="200"/>
                    <input type="hidden" value="${itemDTO.vehicleModel!=null?itemDTO.vehicleModel:''}"
                           name="itemDTOs[${status.index}].hidden_vehicleModel"
                           id="itemDTOs${status.index}.hidden_vehicleModel"/>
                </td>
                <td>
                    <form:input path="itemDTOs[${status.index}].vehicleBrand" value="${itemDTO.vehicleBrand}"
                                title="${itemDTO.vehicleBrand}" cssClass="table_input checkStringEmpty" maxlength="200"/>

                    <input type="hidden" value="${itemDTO.vehicleBrand!=null?itemDTO.vehicleBrand:''}"
                           name="itemDTOs[${status.index}].hidden_vehicleBrand"
                           id="itemDTOs${status.index}.hidden_vehicleBrand"/>
                </td>
                <td style="display:none"><form:hidden disabled="disabled"
                                                      path="itemDTOs[${status.index}].vehicleYear"
                                                      cssStyle="width: 40px;" title="${itemDTO.vehicleYear}"/>
                    <input type="hidden" disabled="disabled" name="itemDTOs[${status.index}].hidden_vehicleYear"
                           id="itemDTOs${status.index}.hidden_vehicleYear"/>
                </td>
                <td style="display:none"><form:hidden disabled="disabled"
                                                      path="itemDTOs[${status.index}].vehicleEngine"
                                                      cssStyle="width: 40px;" title="${itemDTO.vehicleEngine}"/>
                    <input type="hidden" disabled="disabled" name="itemDTOs[${status.index}].hidden_vehicleEngine"
                           id="itemDTOs${status.index}.hidden_vehicleEngine"/>
                </td>
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
                    <td>
                        <form:input path="itemDTOs[${status.index}].price" value="${itemDTO.price}"
                                    title="${itemDTO.price}" lastValue="${itemDTO.price}"
                                    cssClass="itemPrice checkNumberEmpty table_input" data-filter-zero="true"/>
                    </td>
                </bcgogo:hasPermission>
                <td><form:input path="itemDTOs[${status.index}].amount" value="${itemDTO.amount}"
                                class="itemAmount checkNumberEmpty table_input"
                                title="${itemDTO.amount}" data-filter-zero="true"/></td>
                <td>
                    <form:input path="itemDTOs[${status.index}].unit" value="${itemDTO.unit}"
                                class="itemUnit table_input checkStringEmpty"
                                cssStyle="width:80%;" title="${itemDTO.unit}"/>
                    <form:hidden path="itemDTOs[${status.index}].storageUnit" value="${itemDTO.storageUnit}"
                                 class="itemStorageUnit table_input"/>
                    <form:hidden path="itemDTOs[${status.index}].sellUnit" value="${itemDTO.sellUnit}"
                                 class="itemSellUnit table_input"/>
                    <form:hidden path="itemDTOs[${status.index}].rate" value="${itemDTO.rate}"
                                 class="itemRate table_input"/>
                </td>
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
                    <td>
                        <span id="itemDTOs${status.index}.total_span" style="display: inline-block;width: 30px;white-space: nowrap;text-overflow:ellipsis" data-filter-zero="true">${itemDTO.total}</span>
                        <form:hidden path="itemDTOs[${status.index}].total" value="${itemDTO.total}"
                                     class="itemTotal checkNumberEmpty table_input"
                                     readonly="true" title="${itemDTO.total}"/>
                    </td>
                </bcgogo:hasPermission>
                <td>
                    <span id="itemDTOs${status.index}.inventoryAmountSpan" data-filter-zero="true"
                          name="itemDTOs[${status.index}].inventoryAmountSpan">${itemDTO.inventoryAmountApprox}</span>
                    <form:hidden path="itemDTOs[${status.index}].inventoryAmount"
                                 class="itemInventoryAmount table_input" value="${itemDTO.inventoryAmount}"
                                 readonly="true" title="${itemDTO.inventoryAmount}"/>
                    <span style="display: none;">新</span>
                </td>
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
                    <td style="border-right:none;">
                        <a class="opera1"  id="itemDTOs${status.index}.deletebutton"
                               name="itemDTOs[${status.index}].deletebutton">删除</a>
                    </td>
                </bcgogo:hasPermission>
            </tr>
        </c:if>
    </c:forEach>
</table>

<div class="slider-sub-area table2">
	<table id="table_productDetail" class="slider-sub-table table2">
		<col width="80">
		<col width="80">
		<col width="80">
		<col class="trade_price_col" style="display: none">
		<tr class="table_title titleBg">
			<td class="td_input" style="border-top:2px solid #6D8FB9">
				<input type="button" value="商品分类" class="se_xiaoshouRuKu" onclick="showSetproductKind()"/>
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
						<input class="overRuKu" type="button" onclick="showSetLimit(true);" value="上限"
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
			<bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
				<td class="trade_price_td" style="border-top:2px solid #6D8FB9;display: none">
					<input class="se_xiaoshouRuKu" type="button" value="批发价" onclick="showSetTradePrice();"
						   onfocus="this.blur();">
				</td>
			</bcgogo:hasPermission>
		</tr>
        <tr class="space">
            <td colspan="4"></td>
        </tr>
		<c:forEach items="${purchaseOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">
		<tr class="item2 table-row-original">
			<td class="txt_right">
				<form:input path="itemDTOs[${status.index}].productKind" maxlength="50" value='${itemDTO.productKind}'
							class="table_input" title='${itemDTO.productKind}' cssStyle="width:90%"/>
			</td>
			<bcgogo:permission>
				<bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS">
					<td class="txt_right">
						<form:input path="itemDTOs[${status.index}].lowerLimit"
									class="order_input_lowerLimit checkNumberEmpty table_input"
									value="${itemDTO.lowerLimit}" cssStyle="width:100%"/>
					</td>
					<td class="txt_right">
						<form:input path="itemDTOs[${status.index}].upperLimit"
									class="order_input_upperLimit checkNumberEmpty table_input"
									value="${itemDTO.upperLimit}" cssStyle="width:100%"/>
					</td>
				</bcgogo:if>
				<bcgogo:else>
					<td class="txt_right">
						<form:input path="itemDTOs[${status.index}].lowerLimit"
									class="order_input_lowerLimit table_input"
									disabled="true" value="${itemDTO.lowerLimit}" cssStyle="width:100%"/>
					</td>
					<td class="txt_right">
						<form:input path="itemDTOs[${status.index}].upperLimit"
									class="order_input_upperLimit table_input"
									disabled="true" value="${itemDTO.upperLimit}" cssStyle="width:100%"/>
					</td>
				</bcgogo:else>
			</bcgogo:permission>
			<td class="trade_price_td" style="display: none">
                <fmt:formatNumber value='${itemDTO.tradePrice}' pattern='###.##' var="formattedPrice"/>
                <form:input path="itemDTOs[${status.index}].tradePrice" maxlength="13" class="table_input tradePriceCheck"
                            value="${formattedPrice}" cssStyle="width:90%" title="${formattedPrice}"/>
                <input type="hidden" id="itemDTOs${status.index}.inventoryAveragePrice" value="${itemDTO.inventoryAveragePrice}">
            </td>

            </c:forEach>
        </tr>
    </table>
</div>
<i class="slider-btnExpand"><i></i></i>
</div>

<div class="height"></div>
<div class="tableInfo">
    <div class="t_total">合计
        <span id="totalSpan" class="yellow_color" data-filter-zero="true"> ${purchaseOrderDTO.total!=0?purchaseOrderDTO.total:"0"}</span>元
        <form:hidden path="total" value='${purchaseOrderDTO.total!=null?purchaseOrderDTO.total:0}'/>
    </div>
    <div class="t_total" style="float:left;padding-left:10px;">
        预计交货日期:<form:input
            path="deliveryDateStr" value="${purchaseOrderDTO.deliveryDateStr}" cssStyle="width:150px;"
            readonly="true" cssClass="checkStringEmpty textbox"/>
    </div>

</div>
</div>
<div class="danju_beizhu clear">
    <span class="t_title">备注:</span>
    <input type="text" id="memo" name="memo" value="${purchaseOrderDTO.memo}"
           class="memo checkStringEmpty" maxlength="500" kissfocus="on"/>
</div>
<bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
    <c:if test="${empty purchaseOrderDTO.status}">
        <div id="saveDraftOrder_div" class="btn_div_Img">
            <input type="button" id="saveDraftOrder" class="i_savedraft" onfocus="this.blur();"/>
            <div style="width:100%; ">保存草稿</div>
        </div>
    </c:if>
</bcgogo:hasPermission>
<bcgogo:permission>
    <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE.CANCEL&&WEB.TXN.PURCHASE_MANAGE.PURCHASE">
        <c:if test="${purchaseOrderDTO.status eq 'PURCHASE_ORDER_WAITING'
			or purchaseOrderDTO.status eq 'SELLER_PENDING'
			or purchaseOrderDTO.status eq 'PURCHASE_SELLER_STOP'
			or purchaseOrderDTO.status eq 'REFUSED'}">
            <div class="invalidImg2" style="display: block">
                <input id="nullifyBtn" type="button" onfocus="this.blur();">

                <div class="invalidWords" id="invalidWords">作废</div>
            </div>
        </c:if>
    </bcgogo:if>
</bcgogo:permission>
<c:if test="${purchaseOrderDTO.status eq 'PURCHASE_ORDER_WAITING'
		or purchaseOrderDTO.status eq 'PURCHASE_ORDER_DONE'
		or purchaseOrderDTO.status eq 'PURCHASE_ORDER_REPEAL'
		or purchaseOrderDTO.status eq 'PURCHASE_SELLER_STOP'
		or purchaseOrderDTO.status eq 'REFUSED'}">
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
        <div class="copyInput_div" id="copyInput_div">
            <input id="copyInput" type="button" onfocus="this.blur();"/>
            <div class="copyInput_text_div" id="copyInput_text">复制</div>
        </div>
    </bcgogo:hasPermission>
</c:if>
<bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
    <div class="btn_div_Img" id="cancel_div">
        <input id="cancelBtn" type="button" class="cancel j_btn_i_operate" onfocus="this.blur();"/>
        <div class="optWords">清空</div>
    </div>
</bcgogo:hasPermission>
<div class="shopping_btn">
    <c:choose>
        <c:when test="${purchaseOrderDTO.status eq 'SELLER_PENDING'}">
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE.SAVE">
                <div class="btn_div_Img" id="purchaseModify_div">
                    <input id="purchaseModifyBtn" type="button" class="sureBuy j_btn_i_operate" onfocus="this.blur();"/>

                    <div class="optWords">改单</div>
                </div>
            </bcgogo:hasPermission>
        </c:when>
        <c:otherwise>
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE.SAVE">
                <div class="btn_div_Img" id="purchaseSave_div">
                    <input id="purchaseSaveBtn" type="button" class="sureBuy j_btn_i_operate" onfocus="this.blur();"/>
                    <div class="optWords">确认采购</div>
                </div>
            </bcgogo:hasPermission>
        </c:otherwise>
    </c:choose>
    <bcgogo:permission>
        <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE.PRINT">
            <div class="btn_div_Img" id="print_div" style="display:none">
                <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                <div class="optWords">打印</div>
            </div>
        </bcgogo:if>
    </bcgogo:permission>
</div>
</div>
</form:form>
</div>

<div class="height"></div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id">
    </div>
</div>
<div id="mask" style="display:block;position: absolute;">
</div>
<div class="zuofei" id="zuofei"></div>
<iframe name="iframe_PopupBox" id="iframe_PopupBox"
        style="position:absolute;z-index:5; left:200px; top:400px; display:none;" scrolling="no"
        allowtransparency="true" width="1000px" height="1000px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:6; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="800px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_Limit" style="position:absolute;z-index:5; left:40%; top:35%; display:none;"
        allowtransparency="true" width="286px" height="220px" frameborder="0" src="" scrolling="no"></iframe>
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
                <jsp:param name="data" value="{startPageNo:1,orderTypes:'PURCHASE'}"></jsp:param>
                <jsp:param name="jsHandleJson" value="initOrderDraftTable"></jsp:param>
                <jsp:param name="hide" value="hideComp"></jsp:param>
                <jsp:param name="dynamical" value="dynamical1"></jsp:param>
            </jsp:include>
        </div>
    </div>
    <div class="clear"></div>
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

<div id="div_serviceName" class="i_scroll" style="display:none;width:95px;">
    <div class="Scroller-Container" id="Scroller-Container_ServiceName">
    </div>
</div>
<div id="dialog-confirm" title="提醒" style="display:none;">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>

    <div id="dialog-confirm-text"></div>
    <p/>
</div>

<!-- 增加下拉建议框 -->
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>

<div id="div_suggestionList" class="i_scroll suggestionMain" style="display:none;">
    <div class="Container">
        <div class="Scroller-Container" id="suggestionContainer"></div>
    </div>
</div>

<div id="inputMobile" style="display: none">
    <div style="margin-left: 45px;margin-top: 15px">
        <label>手机号：</label>
        <input type="text" kissfocus="on" id="divMobile" style="width:125px;height: 20px">
    </div>
</div>
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
<div id="deliveryDateDialog" style="display:none;" class="alertMain">
    <div style="margin-top: 10px;">
        <span style="font-size: 14px;">您没有输入预计交货日期，请输入：</span>
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