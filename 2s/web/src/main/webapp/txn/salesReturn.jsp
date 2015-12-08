<%@ page import="com.bcgogo.txn.dto.SalesReturnDTO" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>销售退货单</title>
    <%
        boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn((Long) request.getSession().getAttribute("shopId"));//选配仓位功能 默认开启这个功能false
    %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
    <c:choose>
        <c:when test="<%=storageBinTag%>">
            <link rel="stylesheet" type="text/css"
                  href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
        </c:when>
        <c:otherwise>
            <link rel="stylesheet" type="text/css"
                  href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
        </c:otherwise>
    </c:choose>
    <link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
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

        #table_productNo .table_title, #table_productNo .item {
            border-color: #BBBBBB;
            border-style: solid;
            border-width: 0 1px;
        }

    </style>
    <script>
        var returnType = '${salesReturnDTO.returnType}';
        var salesOrderId = '${salesReturnDTO.id}';
        var debt = 0;
        var customer = '${salesReturnDTO.customer}';
        function detailsArrears() {
            var customerId = $("#customerId").val();
            toReceivableSettle(customerId);
        }
    </script>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customerSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/goodsSaleSolr<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/salesReturn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/storeHouseDialog<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.SALE_MANAGE.RETURN");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");

        //author:zhangjuntao
        var time = new Array(), timeFlag1 = true, timeFlag2 = true;
        time[0] = new Date().getTime();
        time[1] = new Date().getTime();
        time[2] = new Date().getTime();
        time[3] = new Date().getTime();
        var reg = /^\d+(\.{0,1}\d*)$/;

        $(document).ready(function () {

            // 客户 快速输入功能强化
             //blur 事件延时处理，select 下拉之后不触发blur事件
            $("#customer")
                    .attr("warning", "请先输入")
                    .tipsy({title: "warning", delay: 0, gravity: "s", html: true, trigger: 'hover'})
                    .bind("focus", function () {
                        $(this).tipsy("hide");
                        $(this).attr("lastValue",$(this).val());
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

            // add by zhuj 联系人下拉菜单
            // 绑定搜索下拉事件
            $("#contact")
                    .bind('click focus', function (e) {
                        e.stopImmediatePropagation();//可以阻止掉同一事件的其他优先级较低的侦听器的处理
                        if (!GLOBAL.Lang.isEmpty($("#customerId").val())) {
                            getContactListByIdAndType($("#customerId").val(), "customer", $(this)); //@see js/contact.js
                        }
                    })
                    .bind('keyup', function (event) {
                        var eventKeyCode = event.which || event.keyCode;
                        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                            //clear by qxy  why do we need this?
//                            getContactListByIdAndType($("#customerId").val(), "customer", $(this), eventKeyCode); //@see js/contact.js
                        }
                    });

            // add by zhuj　绑定customerName blur事件
            $("#customer").blur(function () {
                var $customer = $(this);
                setTimeout(function () {
                    if ($customer.attr("blurLock")) {
                        $customer.removeAttr("blurLock");
                        return;
                    }
                    if ($customer.val() == '') {
                        clearCustomerInputs();
                    }
                    if ($customer.val() == $customer.attr("lastValue")) {
                        return;
                    }
                    clearCustomerInputs();

                    var name = $customer.val();
                    var jsonCustomers = getCustomerByName(name);
                    if (!G.isEmpty(jsonCustomers) && !G.isEmpty(jsonCustomers.results)) {
                        // 渲染tip页面
                        var contactList = new Array();
                        for (var customerIndex in jsonCustomers.results) {
                            var customerId = jsonCustomers.results[customerIndex].idStr;
                            var contacts = jsonCustomers.results[customerIndex].contacts;
                            if (!G.isEmpty(contacts)) {
                                contacts = filterNullObjInArray(contacts);
                            }
                            if (G.isEmpty(contacts)) {
                                var customer = {
                                    customerId: customerId,
                                    contactId: "",
                                    contact: "",
                                    mobile: ""
                                };
                                contactList.push(customer);
                            } else {
                                for (var contactIndex in contacts) {
                                    if (contacts[contactIndex] && !G.Lang.isEmpty(contacts[contactIndex].idStr)) {
                                        var customer = {
                                            customerId: customerId,
                                            contactId: contacts[contactIndex].idStr,
                                            contact: contacts[contactIndex].name,
                                            mobile: contacts[contactIndex].mobile
                                        };
                                        contactList.push(customer);
                                    }
                                }
                            }

                        }
                        if (!G.isEmpty(contactList)) {
                            for (var contactIndex in contactList) {
                                var $sin_contact = $('<div class="sin_contact"><label class="rad"><input type="radio" name="sin_contact"/>'
                                        + name +
                                        '</label>&nbsp;&nbsp;<span>'
                                        + G.normalize(contactList[contactIndex].contact, "")
                                        + '</span>&nbsp;&nbsp;<span>'
                                        + G.normalize(contactList[contactIndex].mobile, "")
                                        + '</span>'
                                        + '<input type="hidden" class="contactName" value="' + G.normalize(contactList[contactIndex].contact, "") + '"/>'
                                        + '<input type="hidden" class="contactMobile" value="' + G.normalize(contactList[contactIndex].mobile, "") + '"/>'
                                        + '<input type="hidden" class="contactId" value="' + G.normalize(contactList[contactIndex].contactId, "") + '"/>'
                                        + '<input type="hidden" class="customerId" value="' + G.normalize(contactList[contactIndex].customerId, "") + '"/>'
                                        + '</div>');
                                $sin_contact.insertAfter($("#oldCustomers > div:last"));
                            }
                        }

                        $("#nameDupTip").dialog({
                            resizable: false,
                            height:200,
                             width:400,
                            title: "友情提示",
                            modal: true,
                            closeOnEscape: false,
                            close: function () {
                                $("input[type=radio]").attr("checked", "");
                                $(".sin_contact").remove();
                                $("#oldCustomers").hide();
                                $("#cusDupTip").show();
                            }, open: function (event, ui) {
                                $(event.target).parent().find(".ui-dialog-titlebar-close").hide();
                            }
                        });
                    }
                }, 200);

            });

            $("#newCustomer").click(function(){
                //$("#nameDupTip").dialog("close");
                $("#isAdd").val("true");
                $("#customerId").val(""); // 后台默认以这个值为判断是否为新增的依据
            });

            $("#oldCustomer").click(function(){
                $("#oldCustomers").show();
                $("#cusDupTip").hide();
                $("#isAdd").val("false");
                $("#nameDupTip .J_return").show();
            });

            $(".sin_contact").live("click", function () {
                $("#contact").val($(this).find("input[class=contactName]").val());
                $("#mobile").val($(this).find("input[class=contactMobile]").val());
                $("#contactId").val($(this).find("input[class=contactId]").val());
                $("#customerId").val($(this).find("input[class=customerId]").val());
                contactDeal(true); // @see suggestion.js
            });

            $("#nameDupTip .J_btnSure").click(function(){
                if ($("#cusDupTip").css("display") == "block") {
                    if (!$("#newCustomer").attr("checked") && !$("#oldCustomer").attr("checked")) {
                        nsDialog.jAlert("请选择新客户或者老客户！");
                        return;
                    } else {
                        $("#mobile").attr("mustInputBySameCustomer", true);
                    }
                }
                if ($("#oldCustomers").css("display") == "block") {
                    var oldChecked = false;
                    $("#oldCustomers input[name=sin_contact]").each(function () {
                        if ($(this).attr("checked")) {
                            oldChecked = true;
                        }
                    });
                    if (!oldChecked) {
                        nsDialog.jAlert("请在客户列表中选择！");
                        return;
                    }
                }
                if ($("#newCustomer").attr("checked")) {
                    $("#isAdd").val("true");
                    $("#customerId").val(""); // 后台默认以这个值为判断是否为新增的依据
                }
                $(".sin_contact").remove();
                $("#nameDupTip").dialog("close");
                $("input[type=radio]").attr("checked", "");
                $("#oldCustomers").hide();
                $("#cusDupTip").show();
            });

            $("#nameDupTip .J_return").bind("click",function(){
                $("#oldCustomers").find("[name='sin_contact']").each(function(){
                    $(this).attr("checked","");
                });
                $("#mobile").removeAttr("mustInputBySameCustomer");
                $("#oldCustomers").hide();
                $("#cusDupTip").find("[type='radio']").each(function () {
                    $(this).attr("checked","");
                });
                $("#cusDupTip").show();
                $(this).hide();
                $("#contact").val("");
                $("#mobile").val("");
                $("#contactId").val("");
                $("#customerId").val("");
                contactDeal(false);
            });

            //复制逻辑之后要校验多联系人
            if ($("#customerId").val() && $("#contactId").val()) {
                contactDeal(true);
            } else {
                contactDeal(false);
            }
            // add end

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
                        try {
                            App.Module.searchcomplete.hide();
                        } catch (e) {
                            G.debug("error searchcomplete instance is undefined!");
                        }
                        event.stopPropagation();
                    })
                    .toggle(!G.isEmpty(G.normalize($("#customer").val())));

            window.timerCheckHistoryButton = 0;
            function toggleHistoryButton() {
                $("#historySearchButton_id").toggle(!G.isEmpty(G.normalize($("#customer").val())));
                timerCheckHistoryButton = setTimeout(toggleHistoryButton, 500);
            }

            timerCheckHistoryButton = setTimeout(toggleHistoryButton, 500);
        });
        function clearCustomerInputs() {
            $("#customerId, #contactId, #contact, #mobile, #hiddenMobile, #landline").val("");
            $("#receivable, #payable").text("0");
            $("#mobile").removeAttr("mustInputBySameCustomer");
        }

    </script>
</head>

<body class="bodyMain" pagetype="order" ordertype="">
<script type="text/javascript">
    $(document.body).attr("ordertype", App.OrderTypes.SALE_RETURN);
    <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.ORDER_MOBILE_REMIND">
    APP_BCGOGO.Permission.Version.OrderMobileRemind=${WEB_VERSION_ORDER_MOBILE_REMIND};
    </bcgogo:permissionParam>

</script>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<div class="i_main clear">
<jsp:include page="unit.jsp"/>
<jsp:include page="txnNavi.jsp">
    <jsp:param name="currPage" value="goodsSale"/>
</jsp:include>
<jsp:include page="saleNavi.jsp">
    <jsp:param name="currPage" value="saleReturn"/>
</jsp:include>
<div class="clear"></div>
<div class="i_mainRight shoppingCart" id="i_mainRight">
<div class="cartTop"></div>
<div class="cartBody">
<form:form commandName="salesReturnDTO" id="salesReturnForm" action="salesReturn.do?method=settleForNormal"  class="J_leave_page_prompt"
           method="post" name="thisform">
<input type="hidden" id="id" name="id" value="${salesReturnDTO.id == null?'': salesReturnDTO.id}"/>
<form:hidden path="status" value="${salesReturnDTO.status}"/>
<form:hidden path="originOrderId" value="${salesReturnDTO.originOrderId}"/>
<form:hidden path="customerId" value="${salesReturnDTO.customerId}"/>
<form:hidden path="contactId" value="${salesReturnDTO.contactId}"/>
<form:hidden path="originOrderType" value="${salesReturnDTO.originOrderType}"/>
<form:hidden path="originReceiptNo" value="${salesReturnDTO.originReceiptNo}"/>
<form:hidden path="shopId" value="${sessionScope.shopId}"/>
<form:hidden path="readOnly" value="${salesReturnDTO.readOnly}"/>
<form:hidden path="draftOrderIdStr" value="${salesReturnDTO.draftOrderIdStr}"/>
<form:hidden path="qq" value="${salesReturnDTO.qq}"  />
<form:hidden path="email" value="${salesReturnDTO.email}"  />
<input id="type" name="type" type="hidden" value="${param.type}"> <!-- 库存带过来的参数-->
<input id="orderType" name="orderType" value="salesReturnOrder" type="hidden"/>
<form:hidden path="selectSupplier" value="${WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER}"/>
<table cellpadding="0" cellspacing="0" class="table2 tabCart tabSales" id="table_productNo">
<col width="75"/>
<col width="200"/>
<col width="80"/>
<col width="80"/>
<col width="80"/>
<col width="55"/>
<col width="60"/>
<col width="45"/>
<c:if test="${salesReturnDTO.originOrderId != null}">
    <col width="45"/>
</c:if>

<col width="45"/>
<col width="45"/>
<col width="35"/>
<col width="50"/>
<c:if test="${salesReturnDTO.originOrderId != null}">
    <col width="70"/>
</c:if>
<col width="75">
<c:if test="${salesReturnDTO.originOrderId != null}">
    <tr>
        <td colspan="15">
            <div class="divTit" style="float:left;width:200px;">相关单据号：<span>${salesReturnDTO.originReceiptNo}</span>
            </div>
        </td>
    </tr>
</c:if>
<tr class="s_tabelBorder" id="trCustomer">

    <td colspan="15" id="tdCustomer" style="padding-bottom: 11px">
        <div class="divTit" style="width:265px;">
            单据号<span id="receiptNoSpan" class="receiptNoSpan">系统自动生成</span>
        </div>
        <div style="float:left;width:145px;" id="salesReturnerDiv" class="divTit">
            退货人
                <span id="salesReturnerSpan">
                      <form:input path="salesReturner" value="${salesReturnDTO.salesReturner}" readOnly="true"
                                  initsalesReturnervalue="${salesReturnDTO.salesReturner}"
                                  cssStyle="width: 60px;" cssClass="checkStringChanged textbox"/>
                      <img src="images/list_close.png" id="deleteSalesReturner" style="width:12px;cursor:pointer">
                      <input type="hidden" id="salesReturnerId" name="salesReturnerId"
                             value="${salesReturnDTO.salesReturnerId}"
                             initsalesReturnerIdvalue="${salesReturnDTO.salesReturnerId}"/>
                </span>
            <form:hidden path="editorId" value="${salesReturnDTO.editorId}"
                         initeditorIdvalue="${salesReturnDTO.editorId}"/>
        </div>
        <div style="float:left;width:190px;" class="divTit">
            退货日期
            <form:hidden path="editDateStr" value="${salesReturnDTO.editDateStr}"/>
			<span>
				<form:input path="vestDateStr" ordertype="sale_return" id="orderVestDate" size="15" readonly="true"
                            value="${salesReturnDTO.vestDateStr}" lastvalue="${salesReturnDTO.vestDateStr}"
                            initordervestdatevalue="${salesReturnDTO.vestDateStr}"
                            cssClass="checkStringChanged textbox"/>
			</span>
        </div>

        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
            <div style="float:left;width:250px;padding-left: 12px" class="divTit">仓库
                <form:select path="storehouseId" cssClass="checkSelectChanged j_checkStoreHouse"
                             cssStyle="width:120px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                    <option value="">—请选择仓库—</option>
                    <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
                </form:select>
            </div>
        </bcgogo:hasPermission>
        <br />
        <div style="float:left;width:269px;position:relative;" class="divTit clear">
            单位/客户
            <input type="text" id="customer" name="customer" value="${salesReturnDTO.customer}" class="customerSuggestion checkStringEmpty textbox"
                   style="width: 130px;" kissfocus="on"/>
                <%--<img src="images/star.jpg"/>--%>
            <input type="button" id="historySearchButton_id" alt="" title="历史查询"
                   class="historySearchButton_c" style="position: absolute; display: block; top: 2px; right: 25px;"/>
            <form:hidden path="customerStr" value="${salesReturnDTO.customerStr}"/>
        </div>
        <div style="float:left;width:145px;margin-left: 0px;" class="divTit">
            联系人
            <input type="text" id="contact" value="${salesReturnDTO.contact}" style="width:60px;" name="contact"
                   class="checkStringEmpty textbox" kissfocus="on"/>

        </div>
        <div style="float:left;" class="divTit">
            手机号码
            <form:input path="mobile" maxlength="11" value="${salesReturnDTO.mobile}" cssStyle="width: 100px;"
                        class="checkStringEmpty textbox"/>
            <form:hidden path="landline" value="${salesReturnDTO.landline}"></form:hidden>
            <input type="hidden" id="hiddenMobile"/>
        </div>
        <div class="divTit" style="padding-left: 12px">
            <div class="reconciliation">
                应收：<span class="arialFont">&yen;</span>
                <span id="receivable" data-filter-zero="true">${totalReceivable == null ? '0' : totalReceivable}</span>
                应付：<span class="arialFont">&yen;</span>
                <span id="payable" data-filter-zero="true">${totalPayable == null ? 0 : totalPayable}</span>
                <a id="duizhan" class="receiptNoSpan">对 账</a>
            </div>
        </div>


        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
            <div id="customerInfo" class="i_clickClient client blue_col" style="float:right;padding-right: 12px;">更多客户信息>></div>
        </bcgogo:hasPermission>
    </td>
</tr>
<tr class="table_title titleBg">
    <td style="border-left:none;padding-left: 10px">商品编号</td>
    <td>品名</td>
    <td>品牌/产地</td>
    <td>规格</td>
    <td>型号</td>
    <td>车型</td>
    <td>车辆品牌</td>
    <td>销售价</td>
    <c:if test="${salesReturnDTO.originOrderId != null}">

        <td>销售量</td>
    </c:if>


    <td>退货价</td>
    <td>退货量</td>
    <td>单位</td>
    <td>小计</td>

    <c:if test="${salesReturnDTO.originOrderId != null}">
        <td>营业分类</td>
    </c:if>

    <td style="border-right:1px solid #BBBBBB;">操作<input class="opera2" type="button"
                                                         style="display:none;"></td>

</tr>
<tr class="space">
    <td colspan="15"></td>
</tr>
<%
    SalesReturnDTO salesReturnDTO = (SalesReturnDTO) request.getAttribute("salesReturnDTO");
    if (salesReturnDTO.getReadOnly() != null && salesReturnDTO.getReadOnly()) {
%>
<input type="hidden" id="editStatus" value="unEditable"/>
<c:forEach items="${salesReturnDTO.itemDTOs}" var="itemDTO" varStatus="status">
    <c:if test="${itemDTO!=null && itemDTO.productId != null}">
        <tr class="bg item table-row-original">
            <td style="border-left:none;padding-left:10px;"> ${itemDTO.commodityCode}
                <form:input type="hidden" path="itemDTOs[${status.index}].commodityCode"
                            value='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}'
                            class="table_input checkStringEmpty"
                            title='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}' style="width:85%"
                            maxlength="20"/>
            </td>
            <td> ${itemDTO.productName}
                <form:hidden path="itemDTOs[${status.index}].id" value="${itemDTO.id}"/>
                <form:hidden path="itemDTOs[${status.index}].productId" value="${itemDTO.productId}"/>
                <form:hidden path="itemDTOs[${status.index}].purchasePrice" value="${itemDTO.purchasePrice}"/>
                <form:hidden path="itemDTOs[${status.index}].costPrice" value="${itemDTO.costPrice}"/>
                <form:hidden path="itemDTOs[${status.index}].totalCostPrice" value="${itemDTO.totalCostPrice}"/>
                <form:hidden path="itemDTOs[${status.index}].originSaleTotal" value="${itemDTO.originSaleTotal}"/>
                <form:input type="hidden" path="itemDTOs[${status.index}].productName" value="${itemDTO.productName}"
                            class="table_input checkStringEmpty" style="width:80%"/>
            </td>
            <td>${itemDTO.brand}
                <form:input type="hidden" path="itemDTOs[${status.index}].brand" value="${itemDTO.brand}"
                            class="table_input checkStringEmpty"/>
            </td>
            <td>${itemDTO.spec}
                <form:input type="hidden" path="itemDTOs[${status.index}].spec" value="${itemDTO.spec}"
                            class="table_input checkStringEmpty"/>
            </td>
            <td>${itemDTO.model}
                <form:input type="hidden" path="itemDTOs[${status.index}].model" value="${itemDTO.model}"
                            class="table_input checkStringEmpty"/>
            </td>
            <td>${itemDTO.vehicleModel}
                <form:input type="hidden" path="itemDTOs[${status.index}].vehicleModel" value="${itemDTO.vehicleModel}"
                            class="table_input checkStringEmpty" title="${itemDTO.vehicleModel}"/>
            </td>
            <td>${itemDTO.vehicleBrand}
                <form:input type="hidden" path="itemDTOs[${status.index}].vehicleBrand" value="${itemDTO.vehicleBrand}"
                            class="table_input checkStringEmpty" title="${itemDTO.vehicleBrand}"/>
            </td>
            <td>
                <form:hidden path="itemDTOs[${status.index}].originSalesPrice"
                             id="itemDTOs${status.index}.originSalesPrice" value="${itemDTO.originSalesPrice}"/>
                <span id="itemDTOs${status.index}.originSalesPriceSpan"
                      name="itemDTOs${status.index}.originSalesPriceSpan" data-filter-zero="true">${itemDTO.originSalesPrice}</span>
            </td>

            <c:if test="${salesReturnDTO.originOrderId != null}">
                <td>
                    <form:hidden path="itemDTOs[${status.index}].originSaleAmount" value="${itemDTO.originSaleAmount}"/>
                    <span data-filter-zero-advanced="true">${itemDTO.originSaleAmountStr}</span>
                </td>
            </c:if>

            <td style="color:#FF6700;">
                <form:input path="itemDTOs[${status.index}].price" value="${itemDTO.price}"
                            class="itemPrice table_input checkNumberEmpty"
                            onblur="checkPrice(this)" style="text-overflow:clip" data-filter-zero="true"/>
            </td>
            <bcgogo:permission>
                <bcgogo:if resourceType="logic" permissions="WEB.VERSION.PRODUCT.THROUGH_SELECT_SUPPLIER">
                    <td style="color:#FF0000;">
                        <form:input path="itemDTOs[${status.index}].amount" value="0"
                                    class="itemAmount table_input checkNumberEmpty" data-filter-zero="true"/>
                    </td>
                </bcgogo:if>
                <bcgogo:else>
                    <td style="color:#FF0000;">
                        <form:input path="itemDTOs[${status.index}].amount" value="${itemDTO.amount}"
                                    class="itemAmount table_input checkNumberEmpty" data-filter-zero="true"/>
                    </td>
                </bcgogo:else>
            </bcgogo:permission>
            <td>
                    <%--<span id="itemDTOs${status.index}.saleReturnUnitSpan"--%>
                    <%--name="itemDTOs[${status.index}].saleReturnUnitSpan" class="itemUnitSpan">${itemDTO.unit}</span>--%>
                <form:input path="itemDTOs[${status.index}].unit" value="${itemDTO.unit}"
                            class="itemUnit table_input checkStringEmpty"/>
                <form:hidden path="itemDTOs[${status.index}].storageUnit" value="${itemDTO.storageUnit}"
                             class="itemStorageUn$('#repairOrderForm').submit(it table_input"/>
                <form:hidden path="itemDTOs[${status.index}].sellUnit" value="${itemDTO.sellUnit}"
                             class="itemSellUnit table_input"/>
                <form:hidden path="itemDTOs[${status.index}].rate" value="${itemDTO.rate}"
                             class="itemRate table_input"/>
            </td>

            <td>
				<span id="itemDTOs${status.index}.totalSpan"
                      name="itemDTOs[${status.index}].totalSpan" data-filter-zero="true">${itemDTO.total}
                </span>
                <form:input type="hidden" path="itemDTOs[${status.index}].total" value="${itemDTO.total}"
                            class="itemTotal" readonly="true"/>
            </td>

            <td style="display: none">
                <form:hidden disabled="disabled" path="itemDTOs[${status.index}].vehicleYear"
                             class="table_input" cssStyle="display: none;"/>
            </td>
            <td style="display: none">
                <form:hidden disabled="disabled"
                             path="itemDTOs[${status.index}].vehicleEngine"
                             class="table_input" cssStyle="display: none;"/>
            </td>

            <c:if test="${salesReturnDTO.originOrderId != null}">
                <td>${itemDTO.businessCategoryName}
                    <form:input type="hidden" path="itemDTOs[${status.index}].businessCategoryName"
                                value="${itemDTO.businessCategoryName}"/>
                    <form:input type="hidden" path="itemDTOs[${status.index}].businessCategoryId"
                                value="${itemDTO.businessCategoryId}"/>
                </td>
            </c:if>

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
                                <input type="hidden" value="${outStorageRelationDTO.relatedSupplierIdStr}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].relatedSupplierId" />
                                <input type="hidden" value="${outStorageRelationDTO.relatedSupplierName}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].relatedSupplierName" />
                                <input type="hidden" value="${outStorageRelationDTO.relatedSupplierInventory}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].relatedSupplierInventory" />
                                <input type="hidden" value="${outStorageRelationDTO.supplierType}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].supplierType" />
                                <div style="width: 140px" class="supplierName" title="${outStorageRelationDTO.relatedSupplierName}">${outStorageRelationDTO.relatedSupplierName}</div>
                                <div style="width: 100px">
                                    剩余库存：<label class="remainAmount">${outStorageRelationDTO.relatedSupplierInventory}</label>
                                </div>
                                <div style="width: 120px">
                                    退货数量<input type="text" remainAmount="${outStorageRelationDTO.relatedSupplierInventory}" value="${outStorageRelationDTO.useRelatedAmount}" vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].useRelatedAmount"/>
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
<%
} else {
%>
<input type="hidden" id="editStatus" value="editable"/>
<c:forEach items="${salesReturnDTO.itemDTOs}" var="itemDTO" varStatus="status">
    <c:if test="${itemDTO!=null && itemDTO.productId != null}">
        <tr class="item table-row-original">
            <td style="border-left:none;">
                <form:input path="itemDTOs[${status.index}].commodityCode"
                            value='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}'
                            class="table_input checkStringEmpty"
                            title='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}' style="width:85%"
                            maxlength="20"/>
            </td>
            <td>
                <form:hidden path="itemDTOs[${status.index}].id" value="${itemDTO.id}"/>
                <form:hidden path="itemDTOs[${status.index}].productId" value="${itemDTO.productId}"/>
                <form:hidden path="itemDTOs[${status.index}].purchasePrice" value="${itemDTO.purchasePrice}"/>
                <form:hidden path="itemDTOs[${status.index}].costPrice" value="${itemDTO.costPrice}"/>
                <form:hidden path="itemDTOs[${status.index}].totalCostPrice" value="${itemDTO.totalCostPrice}"/>
                <form:hidden path="itemDTOs[${status.index}].originSaleTotal" value="${itemDTO.originSaleTotal}"/>
                <form:input path="itemDTOs[${status.index}].productName" value="${itemDTO.productName}"
                            class="table_input checkStringEmpty" style="width:80%"/>
            </td>
            <td>
                <form:input path="itemDTOs[${status.index}].brand" value="${itemDTO.brand}"
                            class="table_input checkStringEmpty"/>
            </td>
            <td>
                <form:input path="itemDTOs[${status.index}].spec" value="${itemDTO.spec}"
                            class="table_input checkStringEmpty"/>
            </td>
            <td>
                <form:input path="itemDTOs[${status.index}].model" value="${itemDTO.model}"
                            class="table_input checkStringEmpty"/>
            </td>
            <td>
                <form:input path="itemDTOs[${status.index}].vehicleModel" value="${itemDTO.vehicleModel}"
                            class="table_input checkStringEmpty" title="${itemDTO.vehicleModel}"/>
            </td>
            <td>
                <form:input path="itemDTOs[${status.index}].vehicleBrand" value="${itemDTO.vehicleBrand}"
                            class="table_input checkStringEmpty" title="${itemDTO.vehicleBrand}"/>
            </td>
            <td>
                <form:hidden path="itemDTOs[${status.index}].originSalesPrice"
                             id="itemDTOs${status.index}.originSalesPrice" value="${itemDTO.originSalesPrice}"/>
                <span id="itemDTOs${status.index}.originSalesPriceSpan" data-filter-zero="true"
                      name="itemDTOs${status.index}.originSalesPriceSpan">${itemDTO.originSalesPrice}</span>
            </td>

                <%--<c:if test="${salesReturnDTO.originOrderId != null}">--%>
                <%--<td>--%>
                <%--${itemDTO.originSaleAmountStr}--%>
                <%--</td>--%>
                <%--</c:if>--%>

            <td style="color:#FF6700;">
                <form:input path="itemDTOs[${status.index}].price" value="${itemDTO.price}"
                            class="itemPrice table_input checkNumberEmpty"
                            onblur="checkPrice(this)" style="text-overflow:clip" data-filter-zero="true"/>
            </td>

            <bcgogo:permission>
                <bcgogo:if resourceType="logic" permissions="WEB.VERSION.PRODUCT.THROUGH_SELECT_SUPPLIER">
                    <td style="color:#FF0000;">
                        <form:input path="itemDTOs[${status.index}].amount" value="0"
                                    class="itemAmount table_input checkNumberEmpty"/>
                    </td>
                </bcgogo:if>
                <bcgogo:else>
                    <td style="color:#FF0000;">
                        <form:input path="itemDTOs[${status.index}].amount" value="${itemDTO.amount}"
                                    class="itemAmount table_input checkNumberEmpty"/>
                    </td>
                </bcgogo:else>
            </bcgogo:permission>
            <td>
                    <%--<span id="itemDTOs${status.index}.saleReturnUnitSpan"--%>
                    <%--name="itemDTOs[${status.index}].saleReturnUnitSpan" class="itemUnitSpan">${itemDTO.unit}</span>--%>
                <form:input path="itemDTOs[${status.index}].unit" value="${itemDTO.unit}"
                            class="itemUnit table_input checkStringEmpty"/>
                <form:hidden path="itemDTOs[${status.index}].storageUnit" value="${itemDTO.storageUnit}"
                             class="itemStorageUnit table_input"/>
                <form:hidden path="itemDTOs[${status.index}].sellUnit" value="${itemDTO.sellUnit}"
                             class="itemSellUnit table_input"/>
                <form:hidden path="itemDTOs[${status.index}].rate" value="${itemDTO.rate}"
                             class="itemRate table_input"/>
            </td>

            <td>
        <span id="itemDTOs${status.index}.totalSpan"
              name="itemDTOs[${status.index}].totalSpan" data-filter-zero="true">${itemDTO.total}</span>
                <form:input type="hidden" path="itemDTOs[${status.index}].total" value="${itemDTO.total}"
                            class="itemTotal" readonly="true"/>
            </td>

            <td style="display: none"><form:hidden disabled="disabled" path="itemDTOs[${status.index}].vehicleYear"
                                                   class="table_input" cssStyle="display: none;"/></td>
            <td style="display: none"><form:hidden disabled="disabled"
                                                   path="itemDTOs[${status.index}].vehicleEngine"
                                                   class="table_input" cssStyle="display: none;"/></td>

            <c:if test="${salesReturnDTO.originOrderId != null}">
                <td>${itemDTO.businessCategoryName}
                    <form:input type="hidden" path="itemDTOs[${status.index}].businessCategoryName"
                                value="${itemDTO.businessCategoryName}"/>
                    <form:input type="hidden" path="itemDTOs[${status.index}].businessCategoryId"
                                value="${itemDTO.businessCategoryId}"/>
                </td>
            </c:if>

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
                                <input type="hidden" value="${outStorageRelationDTO.relatedSupplierIdStr}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].relatedSupplierId" />
                                <input type="hidden" value="${outStorageRelationDTO.relatedSupplierName}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].relatedSupplierName" />
                                <input type="hidden" value="${outStorageRelationDTO.relatedSupplierInventory}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].relatedSupplierInventory" />
                                <input type="hidden" value="${outStorageRelationDTO.supplierType}" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].supplierType" />
                                <div style="width: 140px" class="supplierName" title="${outStorageRelationDTO.relatedSupplierName}">${outStorageRelationDTO.relatedSupplierName}</div>
                                <div style="width: 100px">
                                    剩余库存：<label class="remainAmount">${outStorageRelationDTO.relatedSupplierInventory}</label>
                                </div>
                                <div style="width: 120px">
                                    退货数量<input type="text" remainAmount="${outStorageRelationDTO.relatedSupplierInventory}" value="${outStorageRelationDTO.useRelatedAmount}" vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="itemDTOs[${status.index}].outStorageRelationDTOs[${status_out.index}].useRelatedAmount"/>
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

<%
    }
%>
</table>
<input id="isMakeTime" type="hidden" value="0">
<input id="huankuanTime" type="hidden" value="" name="huankuanTime">
<input id="isAllMakeTime" type="hidden" value="0">
    <%--------------------欠款结算 zhouxiaochen 2011-12-14-------------------------------%>
<iframe id="iframe_qiankuan" style="position:absolute; left:0px; top:200px; display:none;"
        allowtransparency="true" width="1000px" height="600px" frameborder="0" src="">
</iframe>
    <%--------------------End 欠款结算 zhouxiaochen 2011-12-14----------------------------%>
<div class="height"></div>
<div class="tableInfo">
    <div class="total">
        <div class="t_total">商品费用总计：<span id="totalSpan" class="yellow_color">${salesReturnDTO.total == 0?"0":salesReturnDTO.total}</span>元
            <form:hidden path="total" value="${salesReturnDTO.total}"/>
        </div>
        <form:hidden path="cashAmount" value="${salesReturnDTO.cashAmount}"/>
        <form:hidden path="bankAmount" value="${salesReturnDTO.bankAmount}"/>
        <form:hidden path="bankCheckAmount" value="${salesReturnDTO.bankCheckAmount}"/>
        <form:hidden path="bankCheckNo" value="${salesReturnDTO.bankCheckNo}"/>
        <form:hidden path="customerDeposit" value="${salesReturnDTO.customerDeposit}" />
        <form:hidden path="discountAmount" value="${salesReturnDTO.discountAmount}"/>
        <form:hidden path="strikeAmount" value="${salesReturnDTO.strikeAmount}"/>
        <form:hidden path="accountDebtAmount" value="${salesReturnDTO.accountDebtAmount}"/>

        <div style="display:none">实收：
            <span>
                <form:input path="settledAmount" value="${salesReturnDTO.settledAmount}" cssStyle="width: 40px;"
                            cssClass="checkNumberEmpty"/>
            </span>
        </div>
    </div>
</div>
</div>
<div class="clear"></div>
<div class="remarkInfo">
    <div class="danju_beizhu">
        <span style="margin-right:10px">备注:</span>
        <input type="text" id="memo" name="memo" value="${salesReturnDTO.memo}" style="width:930px; margin-left:-10px;" maxlength="500"
               class="memo checkStringEmpty textbox" kissfocus="on"/>
    </div>
</div>
</form:form>
<div class="btn_div_Img" id="saveDraftOrder_div">
    <input type="button" id="saveDraftBtn" class="i_savedraft" value="" onfocus="this.blur();"/>

    <div style="width:100%; ">保存草稿</div>
</div>
<bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
    <div class="btn_div_Img" id="print_div">
        <input id="cancelBtn" type="button" class="cancel j_btn_i_operate" onfocus="this.blur();"/>

      <c:if test="${salesReturnDTO.originOrderId != null}">
        <div class="optWords">重置</div>
      </c:if>
      <c:if test="${salesReturnDTO.originOrderId == null}">
        <div class="optWords">清空</div>
      </c:if>
    </div>
</bcgogo:hasPermission>
<div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
        <div class="btn_div_Img" id="saleSave_div">
            <input id="saleReturnBtn" type="button" class="saleAccount j_btn_i_operate" onfocus="this.blur();"/>

            <div class="optWords">确认退货</div>
        </div>
    </bcgogo:hasPermission>
</div>
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

<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:200px; display:none;"
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
                <jsp:param name="data" value="{startPageNo:1,orderTypes:'SALE_RETURN'}"></jsp:param>
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
<div id="inputMobile" style="display: none">
    <div style="margin-left: 45px;margin-top: 15px">
        <label>手机号：</label>
        <input type="text" id="divMobile" style="width:125px;height: 20px">
    </div>
</div>

<!-- add by zhuj -->

<div class="alertMain productDetails" id="nameDupTip" style="display:none;">
    <div class="height"></div>
    <div id="cusDupTip">
        <div>该客户存在重名客户，请选择</div>
        <div class="height"></div>
        <label class="rad"><input type="radio" id="newCustomer"/>该客户为新客户,需填写手机或修改客户名加以区分</label><br/>
        <label class="rad" id="oldCustomer"><input type="radio"/>该客户为老客户,则请选择所需客户</label>
    </div>

    <div id="oldCustomers" style="display:none">
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
<%--<div class="alertMain productDetails" id="nameDupTip" style ="display:none;width:100%" >--%>
    <%--&lt;%&ndash;<div class="alert_title">--%>
        <%--<div class="left"></div>--%>
        <%--<div class="body">友情提示<a class="icon_Close"></a></div>--%>
        <%--<div class="right"></div>--%>
    <%--</div>&ndash;%&gt;--%>
    <%--<div class="height"></div>--%>

    <%--<div id="cusDupTip">--%>
        <%--<div>该s客户存在重名客户，请选择</div>--%>
        <%--<div class="height"></div>--%>
        <%--<label class="rad"><input type="radio" id="newCustomer" />该客户为新客户</label>&nbsp;<label--%>
            <%--class="rad" id="oldCustomer"><input type="radio"/>该客户为老客户</label>--%>
    <%--</div>--%>

    <%--<div id="oldCustomers" style = "display:none">--%>
        <%--<div>请选择老客户</div>--%>
        <%--<div class="height"></div>--%>
    <%--</div>--%>
    <%--<div class="height"></div>--%>
    <%--<div class="button">--%>
        <%--<a class="btnSure">确 定</a>--%>
    <%--</div>--%>
<%--</div>--%>
<!-- add end-->
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>