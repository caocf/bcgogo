<%@ page import="com.bcgogo.txn.dto.PurchaseReturnDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>入库退货单</title>
    <%
        boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn((Long) request.getSession().getAttribute("shopId"));//选配仓位功能 默认开启这个功能false
    %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
    <c:choose>
        <c:when test="<%=storageBinTag%>">
            <link rel="stylesheet" type="text/css" href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
        </c:when>
        <c:otherwise>
            <link rel="stylesheet" type="text/css" href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
        </c:otherwise>
    </c:choose>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/returnStorage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/supplierSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/storeHouseDialog<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.PURCHASE_MANAGE.RETURN");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");

        $(document).ready(function () {
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
//                            getContactListByIdAndType($("#supplierId").val(), "supplier", $(this), eventKeyCode); //@see js/contact.js
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
                $("#yingshou").css("color","#FF0000");
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


        });
    </script>
</head>
<body pagetype="order" ordertype="">
<script type="text/javascript">
    $(document.body).attr("ordertype", App.OrderTypes.RETURN);
</script>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div style="display:none" id="errorMsg">${errorMsg}</div>
<div class="i_main">
<jsp:include page="txnNavi.jsp">
    <jsp:param name="currPage" value="purchase"/>
</jsp:include>
<jsp:include page="purchaseNavi.jsp">
    <jsp:param name="currPage" value="purchaseReturn"/>
</jsp:include>
<jsp:include page="unit.jsp"/>
<div class="i_mainRight" id="i_mainRight">
<div class="cartTop"></div>
<div class="cartBody">
<form:form commandName="purchaseReturnDTO" id="purchaseReturnForm" action="goodsReturn.do?method=saveReturnStorage" method="post" class="J_leave_page_prompt">
<input id="orderType" name="orderType" value="purchaseReturnOrder" type="hidden"/>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<form:hidden path="draftOrderIdStr" value="${purchaseReturnDTO.draftOrderIdStr==null?'':purchaseReturnDTO.draftOrderIdStr}"/>
<form:hidden path="id" value="${purchaseReturnDTO.id == null?'': purchaseReturnDTO.id}"/>
<form:hidden path="status" value="${purchaseReturnDTO.status == null?'': purchaseReturnDTO.status}"/>
<form:hidden path="readOnly" value="${purchaseReturnDTO.readOnly}"/>
<form:hidden path="originReceiptNo" value="${purchaseReturnDTO.originReceiptNo}"/>
<form:hidden path="originOrderId" value="${purchaseReturnDTO.originOrderIdStr}"/>
<form:hidden path="selectSupplier" value="${WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER}"/>
<form:hidden path="contactId" value="${purchaseReturnDTO.contactId}"/>
<form:hidden path="huankuanTime" value="${purchaseReturnDTO.huankuanTime}"/>

<div class="tuihuo_first">
<table id="supplier_table_return" class="elivate">
<col width="100">
<col width="155">
<col width="80">
<col width="120">
<col width="100">
<col width="70">
<col width="100">
<col width="110">
<col width="60">
<c:if test="${purchaseReturnDTO.originOrderId != null}">
    <tr>
        <td colspan="15">
            <div class="divTit" style="float:left;width:200px;">相关单据号：<span>${purchaseReturnDTO.originReceiptNo}</span>
            </div>
        </td>
    </tr>
</c:if>
<tr>

    <td class="t_title">单据号：
        <input type="hidden" id="receiptNo" name="receiptNo" value="${purchaseReturnDTO.receiptNo}"/>
    </td>
    <td style="text-align: left;">
        <span id="receiptNoSpan" class="receiptNoSpan">系统自动生成</span>
    </td>

    <td class="t_title">验货人：</td>
    <td style="text-align: left;">
        <form:input path="editor" value="${purchaseReturnDTO.editor}" cssStyle="width: 90%;display:none;" maxlength="20" cssClass="checkStringChanged textbox"
                    initeditorvalue="${purchaseReturnDTO.editor}" readonly="true"/>
        <form:hidden path="editorId" value="${purchaseReturnDTO.editorId}"/>
            ${purchaseReturnDTO.editor}
    </td>
    <td class="t_title">退货日期：</td>
    <td style="text-align: left;">
        <c:choose>
            <c:when test="${!empty purchaseReturnDTO.id}">
                ${purchaseReturnDTO.vestDateStr}
                <form:hidden path="vestDateStr" value="${purchaseReturnDTO.vestDateStr}" ordertype="return" id="orderVestDate"/>
            </c:when>
            <c:otherwise>
                <form:input path="vestDateStr" ordertype="return" cssStyle="width:110px;" readonly="true" id="orderVestDate" size="10" cssClass="checkStringChanged textbox"
                            initordervestdatevalue="${purchaseReturnDTO.vestDateStr}" value="${purchaseReturnDTO.vestDateStr}" lastvalue="${purchaseReturnDTO.vestDateStr}"/>
            </c:otherwise>
        </c:choose>
        <form:hidden path="editDateStr" value="${purchaseReturnDTO.editDateStr}"/>
    </td>
    <td class="t_title">
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">仓库：</bcgogo:hasPermission>
    </td>
    <td>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
            <form:select path="storehouseId" cssClass="j_checkStoreHouse checkSelectChanged" cssStyle="width:120px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                <option value="">—请选择仓库—</option>
                <form:options items="${storeHouseDTOList}" itemValue="id" itemLabel="name"/>
            </form:select>
            <input type="hidden" id="oldStorehouseId" value="${purchaseReturnDTO.storehouseId}" />
        </bcgogo:hasPermission>
    </td>

    <td></td>
</tr>
<tr>
    <td class="t_title">供应商<a style="color:#F00000;">*</a>：</td>
    <form:hidden path="supplierId" value="${purchaseReturnDTO.supplierId == null?'': purchaseReturnDTO.supplierId}"/>
    <form:hidden path="status" value="${purchaseReturnDTO.status == null?'': purchaseReturnDTO.status}"/>
    <td>
        <input type="text" id="supplier" name="supplier"
            ${purchaseReturnDTO.status =='SELLER_PENDING'? 'disabled="disabled"' : ''}
               class="supplierSuggestion checkStringEmpty textbox" maxlength="20" style="width: 105px"
               value="${purchaseReturnDTO.supplier==null?'':purchaseReturnDTO.supplier}" kissfocus="on"/>
            <%--<img src="images/star.jpg" style=" display:block; float:left; margin:10px 5px;"/>--%>
        <input type="button" id="historySearchButton_id" alt="" title="历史查询" class="historySearchButton_c"
               style="display:block; width:24px;height: 25px; float:left; margin-top:-5px;"/>
            <%--<form:hidden path="supplierShopId" autocomplete="off" value="${purchaseReturnDTO.supplierShopId == null?'': purchaseReturnDTO.supplierShopId}"/>--%>
    </td>
    <td class="t_title">联系人：</td>
    <td>
        <input type="text" id="contact" name="contact" maxlength="20" class="checkStringEmpty textbox"
               value="${purchaseReturnDTO.contact}"  kissfocus="on" style="width:110px;"/>
    </td>
    <td class="t_title">联系电话：</td>
    <td>
        <form:input path="mobile" maxlength="11" cssClass="checkStringEmpty textbox" value="${purchaseReturnDTO.mobile}" cssStyle="width: 110px;"/>
    </td>
        <%--<td colspan="2" class="xiangxi_td">--%>
    <td class="t_title">
        应收应付：
    </td>
    <td>
        <div class="pay" style="width:184px;" id="duizhan">
            <a class="payMoney" style="color:#272727;">
                应收<span class="arialFont">&yen;</span><span id="receivable" data-filter-zero="true">${totalReceivable == null ? '0' : totalReceivable}</span>
            </a>
            <a class="fuMoney" style="color:#272727;">
                应付<span class="arialFont">&yen;</span><span id="payable" data-filter-zero="true">${totalPayable == null ? '0' : totalPayable}</span>
            </a>
        </div>
    </td>
    <td>
        <%--<a id="clickMore" class="blue_color down">详细</a>--%>
        <a id="orderSupplierInfo" class="down blue_color">详细</a>
    </td>
</tr>
<%--<tr class="supplierDetailInfo" style="display:none">
    <td class="t_title">简称：</td>
    <td>
        <input type="text" id="abbr" name="abbr" maxlength="20" class="checkStringEmpty textbox"
               value="${purchaseReturnDTO.abbr}" style="width:110px;" kissfocus="on"/>
    </td>
    <td class="t_title">传真：</td>
    <td>
        <form:input path="fax" maxlength="20" cssClass="checkStringEmpty textbox" value="${purchaseReturnDTO.fax}" cssStyle="width:110px;"/>
    </td>

    <td class="t_title">所属区域：</td>
    <td colspan="4">
        <select class="txt area" style="width:85px; float:left; margin-right:5px;" id="select_province" name="province"><option value="">-所有省-</option></select>
        <select class="txt area" style="width:85px; float:left; margin-right:5px;" id="select_city" name="city"><option value="">-所有市-</option></select>
        <select class="txt area" style="width:85px; float:left; margin-right:5px;" id="select_township" name="region"><option value="">-所有区-</option></select>
        <input id="input_address" name="address" type="text" class="txt J_address_input" style="width:104px;" value="${purchaseReturnDTO.address}"/>
        <input type="hidden" id="select_province_input" value="${purchaseReturnDTO.province}" />
        <input type="hidden" id="select_city_input" value="${purchaseReturnDTO.city}" />
        <input type="hidden" id="select_township_input" value="${purchaseReturnDTO.region}" />
    </td>

        &lt;%&ndash;<c:if test="${!isWholesalerVersion}" >&ndash;%&gt;
        &lt;%&ndash;<td class="t_title">地址</td>&ndash;%&gt;
        &lt;%&ndash;<td>&ndash;%&gt;
        &lt;%&ndash;<input type="text" id="address" name="address" maxlength="50" class="checkStringEmpty textbox"&ndash;%&gt;
        &lt;%&ndash;value="${purchaseReturnDTO.address}" style="width:110px; margin-right:12px;" kissfocus="on"/>&ndash;%&gt;
        &lt;%&ndash;</td>&ndash;%&gt;
        &lt;%&ndash;</c:if>&ndash;%&gt;
</tr>
<tr class="supplierDetailInfo" style="display:none">
    <td class="t_title">座机：</td>
    <td>
        <form:input path="landline" maxlength="20" cssClass="checkStringEmpty textbox" value="${purchaseReturnDTO.landline}" cssStyle="width:110px;; display:block; float:left;"/>
    </td>
    <td class="t_title">Email：</td>
    <td>
        <input type="text" id="email" name="email" maxlength="50" class="checkStringEmpty textbox"
               value="${purchaseReturnDTO.email}" style="width:110px" kissfocus="on"/>
    </td>
    <td class="t_title">QQ：</td>
    <td>
        <form:input path="qq" maxlength="20" cssClass="checkStringEmpty textbox" value="${purchaseReturnDTO.qq}" cssStyle="width:110px;"/>
    </td>
    <td class="t_title">开户行：</td>
    <td>
        <input type="text" id="bank" name="bank" maxlength="20" class="checkStringEmpty textbox"
               value="${purchaseReturnDTO.bank}"  kissfocus="on" style="width:147px;"/>
    </td>

    <td></td>
</tr>
<tr class="supplierDetailInfo" style="display:none">

    <td class="t_title">开户名：</td>
    <td>
        <input type="text" id="accountName" name="accountName" maxlength="20" class="checkStringEmpty textbox"
               value="${purchaseReturnDTO.accountName}" style="width:110px;" kissfocus="on"/>
    </td>
    <td class="t_title">账号：</td>
    <td>
        <form:input path="account" maxlength="20" cssClass="checkStringEmpty textbox" value="${purchaseReturnDTO.account}" cssStyle="width:110px;"/>
    </td>
    <td class="t_title">结算方式：</td>
    <td>
        <form:select path="settlementType" cssStyle="width:76%;float:left;width:128px;" cssClass="checkSelectChanged">
            <form:option value="" label="-请选择-"/>
            <form:options items="${settlementTypeList}"/>
        </form:select>
    </td>
    <td class="t_title">发票类型：</td>
    <td>
        <form:select path="invoiceCategory" cssStyle="width:76%;float:left;width:154px;" cssClass="checkSelectChanged">
            <form:option value="" label="-请选择-"/>
            <form:options items="${invoiceCategoryList}"/>
        </form:select>
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
        <input id="businessScope" type="hidden" name="businessScope" class="txt" maxlength="500" value="${purchaseReturnDTO.businessScope}"/>

    </td>
    <td></td>
</tr>--%>
<!-- 供应商相关的隐藏信息 -->
<input type="hidden" name="abbr" id="abbr" value="${purchaseReturnDTO.abbr}"/>
<input type="hidden" name="fax" id="fax" value="${purchaseReturnDTO.fax}"/>
<input id="input_address" name="address" type="hidden" class="txt J_address_input" style="width:128px;" value="${purchaseReturnDTO.address}"/>
<input type="hidden" id="select_province_input" name="province" value="${purchaseReturnDTO.province}" />
<input type="hidden" id="select_city_input" name="city" value="${purchaseReturnDTO.city}" />
<input type="hidden" id="select_township_input" name="region" value="${purchaseReturnDTO.region}" />
<input type="hidden" id="landline" name="landline" value="${purchaseReturnDTO.landline}" />
<input type="hidden" id="email" name="email" value="${purchaseReturnDTO.email}" />
<input type="hidden" id="qq" name="qq" value="${purchaseReturnDTO.qq}"/>
<input type="hidden" id="bank" name="bank" value="${purchaseReturnDTO.bank}"/>
<input type="hidden" id="accountName" name="accountName" value="${purchaseReturnDTO.accountName}"/>
<input type="hidden" id="account" name="account" value="${purchaseReturnDTO.account}"/>
<input type="hidden" id="settlementType" name="settlementType" value="${purchaseReturnDTO.settlementType}"/>
<input type="hidden" id="invoiceCategory" name="invoiceCategory" value="${purchaseReturnDTO.invoiceCategory}"/>
<input id="businessScope" type="hidden" name="businessScope" class="txt"  value="${purchaseReturnDTO.businessScope}"/>
</table>
<span class="right_tuihuo"></span>
</div>
<div class="clear"></div>
<table class="table2 tuihuo_show clear" id="table_productNo">
<col width="80"/>
<col width="100"/>
<col width="90"/>
<col width="90"/>
<col width="90"/>
<col width="90"/>
<col width="80"/>
<c:if test="${purchaseReturnDTO.originOrderId != null}">
    <col width="50"/>
</c:if>
<col width="80"/>
<col width="50"/>
<col width="130"/>
<col width="50"/>
<col width="50"/>
<col  width="70"/>
<tr class="titleBg">
    <td style="padding-left:10px;">商品编号</td>
    <td>品名</td>
    <td>品牌/产地</td>
    <td>规 格</td>
    <td>型 号</td>
    <td>车辆品牌</td>
    <td>车型</td>
    <c:if test="${purchaseReturnDTO.originOrderId != null}">
        <td>入库价</td>
        <td>入库量</td>
    </c:if>
    <c:if test="${purchaseReturnDTO.originOrderId == null}">
        <td>库存均价</td>
    </c:if>
    <td>退货价</td>
    <td>退货量/库存量</td>
    <td>单位</td>
    <td style="padding-right:15px;">小 计</td>
    <td>操作<a class="opera2" style="display: none"></a></td>
</tr>
<tr class="space">
    <td colspan="15"></td>
</tr>
<%
    PurchaseReturnDTO purchaseReturnDTO = (PurchaseReturnDTO) request.getAttribute("purchaseReturnDTO");
    if (purchaseReturnDTO!=null&&purchaseReturnDTO.getReadOnly() != null && purchaseReturnDTO.getReadOnly()) {
%>
<c:forEach items="${purchaseReturnDTO.itemDTOs}" var="itemDTO" varStatus="status">
    <tr class="bg item table-row-original">
        <td style="padding-left:10px;">
             ${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}
            <form:input type="hidden" path="itemDTOs[${status.index}].commodityCode" value="${itemDTO.commodityCode!=null?itemDTO.commodityCode:''}"/>
        </td>
        <td>
            <form:hidden path="itemDTOs[${status.index}].id" value='${itemDTO.id!=null?itemDTO.id:""}'/>
            <form:hidden path="itemDTOs[${status.index}].productId"
                         value="${itemDTO.productIdStr == null?'': itemDTO.productIdStr}"/>
            <span id="itemDTOs${status.index}.productNameSpan"> ${itemDTO.productName!=null?itemDTO.productName:""}</span>
            <input type="hidden" id="itemDTOs${status.index}.productName" value="${itemDTO.productName!=null?itemDTO.productName:""}" />
            <form:input type="hidden" path="itemDTOs[${status.index}].productName" value="${itemDTO.productName!=null?itemDTO.productName:''}"/>
        </td>
        <td>
            <span id="itemDTOs${status.index}.brandSpan"> ${itemDTO.brand!=null?itemDTO.brand:""} </span>
            <form:input type="hidden" path="itemDTOs[${status.index}].brand" value="${itemDTO.brand!=null?itemDTO.brand:''}"/>
        </td>
        <td>
            <span id="itemDTOs${status.index}.spec">${itemDTO.spec!=null?itemDTO.spec:""} </span>
            <form:input type="hidden" path="itemDTOs[${status.index}].spec" value="${itemDTO.spec!=null?itemDTO.spec:''}"/>
        </td>
        <td>
            <span id="itemDTOs${status.index}.model">${itemDTO.model!=null?itemDTO.model:""} </span>
            <form:input type="hidden" path="itemDTOs[${status.index}].model" value="${itemDTO.model!=null?itemDTO.model:''}"/>
        </td>
        <td>
            <span id="itemDTOs${status.index}.vehicleBrand"> ${itemDTO.vehicleBrand!=null?itemDTO.vehicleBrand:""}</span>
            <form:input type="hidden" path="itemDTOs[${status.index}].vehicleBrand" value="${itemDTO.vehicleBrand!=null?itemDTO.vehicleBrand:''}"/>
        </td>
        <td>
            <span id="itemDTOs${status.index}.vehicleModel"> ${itemDTO.vehicleModel!=null?itemDTO.vehicleModel:""}</span>
            <form:input type="hidden" path="itemDTOs[${status.index}].vehicleModel" value="${itemDTO.vehicleModel!=null?itemDTO.vehicleModel:''}"/>
        </td>
        <td>
            <span id="itemDTOs${status.index}.iprice" data-filter-zero="true">${itemDTO.iprice!=null?itemDTO.iprice:""}</span>
            <form:input type="hidden" path="itemDTOs[${status.index}].iprice" value="${itemDTO.iprice!=null?itemDTO.iprice:''}"/>
        </td>
        <td>
            <span id="itemDTOs${status.index}.iamount" data-filter-zero="true">${itemDTO.iamount!=null?itemDTO.iamount:""}</span>
            <form:input type="hidden" path="itemDTOs[${status.index}].iamount" value="${itemDTO.iamount!=null?itemDTO.iamount:''}"/>
        </td>
        <td>
            <form:input path="itemDTOs[${status.index}].price" value='${(itemDTO.price != null && itemDTO.price > 0) ? itemDTO.price : ""}'
                        class="itemPrice checkNumberEmpty textbox" cssStyle="width:80%;" data-filter-zero="true"/>
        </td>

        <td>
            <bcgogo:permission>
                <bcgogo:if resourceType="logic" permissions="WEB.VERSION.PRODUCT.THROUGH_SELECT_SUPPLIER">
                    <form:input path="itemDTOs[${status.index}].amount"
                                value='0' class="itemAmount checkNumberEmpty textbox" cssStyle="width:30px;" data-filter-zero="true"/>
                </bcgogo:if>
                <bcgogo:else>
                    <form:input path="itemDTOs[${status.index}].amount" value='${itemDTO.amount==null?"":itemDTO.amount}'
                                class="itemAmount checkNumberEmpty textbox" cssStyle="width:30px;" data-filter-zero="true"/>
                </bcgogo:else>
            </bcgogo:permission>
            <form:input path="itemDTOs[${status.index}].inventoryAmount" data-filter-zero="true"
                        value='${(itemDTO.inventoryAmount==null?0:itemDTO.inventoryAmount)+(itemDTO.reserved==null?0:itemDTO.reserved)}'
                        class="inventoryAmount textbox" cssStyle="width:40px;border: 0 none;background-color:transparent;" readonly="true"/>
            <form:hidden path="itemDTOs[${status.index}].returnAbleAmount" value="${itemDTO.returnAbleAmount == null?'': itemDTO.returnAbleAmount}"/>
            <form:hidden path="itemDTOs[${status.index}].reserved" value="${itemDTO.reserved == null?'0': itemDTO.reserved}"/>
        </td>
        <td>
            <span id="itemDTOs${status.index}.unitSpan">  ${itemDTO.unit!=null?itemDTO.unit:""}</span>
            <form:input type="hidden" path="itemDTOs[${status.index}].unit" value="${itemDTO.unit!=null?itemDTO.unit:''}"/>
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
            <form:input path="itemDTOs[${status.index}].total" value='${itemDTO.total!=null?itemDTO.total:""}' data-filter-zero="true"
                        class="itemTotal" cssStyle="width:60px;border: 0 none;background-color:transparent;" readonly="true"/>
        </td>
        <td>
            <a class="dele_a" id="itemDTOs0.deletebutton" name="itemDTOs[0].deletebutton">删除</a>
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
</c:forEach>
<%
}else {
%>
<c:forEach items="${purchaseReturnDTO.itemDTOs}" var="itemDTO" varStatus="status">
    <tr class="bg item table-row-original">
        <td style="padding-left:10px;">
            <form:input path="itemDTOs[${status.index}].commodityCode"
                        value='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}' class="checkStringEmpty textbox"
                        title='${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}' style="width:80%" maxlength="20"/>
        </td>
        <td>
            <form:hidden path="itemDTOs[${status.index}].id" value='${itemDTO.id!=null?itemDTO.id:""}'/>
            <form:hidden path="itemDTOs[${status.index}].productId"
                         value="${itemDTO.productIdStr == null?'': itemDTO.productIdStr}"/>
            <form:input path="itemDTOs[${status.index}].productName" cssClass="checkStringEmpty textbox"
                        value='${itemDTO.productName!=null?itemDTO.productName:""}'
                        style="width:80%"/>
        </td>
        <td>
            <form:input path="itemDTOs[${status.index}].brand" cssClass="checkStringEmpty textbox"
                        value='${itemDTO.brand!=null?itemDTO.brand:""}'
                        style="width:80%"/>
        </td>
        <td>
            <form:input path="itemDTOs[${status.index}].spec" cssClass="checkStringEmpty textbox"
                        value='${itemDTO.spec!=null?itemDTO.spec:""}'
                        cssStyle="width:80%;"/>
        </td>
        <td>
            <form:input path="itemDTOs[${status.index}].model" cssClass="checkStringEmpty textbox"
                        value='${itemDTO.model!=null?itemDTO.model:""}'
                        cssStyle="width:80%;"/>
        </td>
        <td>
            <form:input path="itemDTOs[${status.index}].vehicleBrand" cssClass="checkStringEmpty textbox"
                        value='${itemDTO.vehicleBrand!=null?itemDTO.vehicleBrand:""}'
                        cssStyle="width:80%;" maxlength="200"/>
        </td>
        <td>
            <form:input path="itemDTOs[${status.index}].vehicleModel" cssClass="checkStringEmpty textbox"
                        value='${itemDTO.vehicleModel!=null?itemDTO.vehicleModel:""}'
                        cssStyle="width:80%;" maxlength="200"/>
        </td>
        <td>
            <span id="itemDTOs${status.index}.inventoryAveragePrice" data-filter-zero="true">${itemDTO.inventoryAveragePrice}</span>
        </td>
        <td>
            <form:input path="itemDTOs[${status.index}].price" value='${(itemDTO.price != null && itemDTO.price > 0 ) ? itemDTO.price : ""}'
                        class="itemPrice checkNumberEmpty textbox" cssStyle="width:80%;" autocomplete="off" data-filter-zero="true"/>
        </td>

        <td>
            <bcgogo:permission>
                <bcgogo:if resourceType="logic" permissions="WEB.VERSION.PRODUCT.THROUGH_SELECT_SUPPLIER">
                    <form:input path="itemDTOs[${status.index}].amount"
                                value='0'
                                class="itemAmount checkNumberEmpty textbox"
                                cssStyle="width:30px;" autocomplete="off" data-filter-zero="true"/>
                </bcgogo:if>
                <bcgogo:else>
                    <form:input path="itemDTOs[${status.index}].amount"
                                value='${itemDTO.amount==null?"":itemDTO.amount}'
                                class="itemAmount checkNumberEmpty textbox"
                                cssStyle="width:30px;" autocomplete="off" data-filter-zero="true"
                            />
                </bcgogo:else>
            </bcgogo:permission>
            <form:input path="itemDTOs[${status.index}].inventoryAmount" data-filter-zero="true"
                        value='${(itemDTO.inventoryAmount==null?"":itemDTO.inventoryAmount)+(itemDTO.reserved==null?0:itemDTO.reserved)}'
                        class="inventoryAmount textbox"
                        cssStyle="width:40px;border: 0 none;background-color:transparent;"
                        readonly="true" autocomplete="off"/>
            <form:hidden path="itemDTOs[${status.index}].returnAbleAmount"
                         value="${itemDTO.returnAbleAmount == null?'': itemDTO.returnAbleAmount}"/>
            <form:hidden path="itemDTOs[${status.index}].reserved"
                         value="${itemDTO.reserved == null?'0': itemDTO.reserved}"/>
        </td>
        <td>
            <form:input path="itemDTOs[${status.index}].unit"
                        value="${itemDTO.unit}" class="itemUnit checkStringEmpty textbox"
                        cssStyle="width:80%;"/>
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
                        value='${itemDTO.total!=null?itemDTO.total:""}'
                        class="itemTotal"
                        cssStyle="width:60px;border: 0 none;background-color:transparent;"
                        readonly="true" autocomplete="off"
                    />
        </td>
        <td>
            <a class="dele_a" id="itemDTOs0.deletebutton" name="itemDTOs[0].deletebutton">删除</a>
                <%--<a id="itemDTOs0.plusbutton" class="opera2" style="color: rgb(0, 148, 255); text-decoration: none;">增加</a>--%>
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
</c:forEach>
<%
    }
%>

</table>

<div class="divTit allMoney">
    合计：
   <span id="totalSpan" class="yellow_color" data-filter-zero="true">${purchaseReturnDTO.total == 0 ? '0':purchaseReturnDTO.total }</span> 元
        <form:hidden path="totalStr" value=""/>
        <form:hidden path="total" value="${purchaseReturnDTO.total == 0 ? '0':purchaseReturnDTO.total }"/>
</div>
<div class="clear"></div>


<input type="hidden" name="cash" id="cash" value="${purchaseReturnDTO.cash}">
<input type="hidden" name="bankAmount" id="bankAmount" value="${purchaseReturnDTO.bankAmount}">
<input type="hidden" name="bankCheckAmount" id="bankCheckAmount" value="${purchaseReturnDTO.bankCheckAmount}">
<input type="hidden" name="bankCheckNo" id="bankCheckNo" value="${purchaseReturnDTO.bankCheckNo}">
<input type="hidden" name="strikeAmount" id="strikeAmount" value="${purchaseReturnDTO.strikeAmount}">
<input type="hidden" name="depositAmount" id="depositAmount" value="${purchaseReturnDTO.depositAmount}">
<input type="hidden" name="accountDiscount" id="accountDiscount" value="${purchaseReturnDTO.accountDiscount}">
<input type="hidden" name="accountDebtAmount" id="accountDebtAmount" value="${purchaseReturnDTO.accountDebtAmount}">
<input type="hidden" name="settledAmount" id="settledAmount" value="${purchaseReturnDTO.depositAmount}">
</div>

</div>
<div class="danju_beizhu" style="float:left ">
    <span>备注:</span>
    <input type="text" id="memo" name="memo" class="checkStringEmpty textbox memo" maxlength="500"
           value="${purchaseReturnDTO.memo == null ? '':purchaseReturnDTO.memo}" kissfocus="on"/>
</div>


<div class="btn_div_Img" id="saveDraftOrder_div">
    <input type="button" id="saveDraftBtn" class="i_savedraft" value="" onfocus="this.blur();"/>

    <div style="width:100%; ">保存草稿</div>
</div>
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
    <div class="optWords">清空</div>
</div>
<div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN.SAVE">
        <div class="btn_div_Img" id="saleSave_div">
            <input id="confirmReturnGoodsBtn" type="button" class="saleAccount j_btn_i_operate" onfocus="this.blur();"/>
            <div class="optWords">${purchaseReturnDTO.status =='SELLER_PENDING'?'改单':'退货'}</div>
        </div>
    </bcgogo:hasPermission>
    <bcgogo:permission>
        <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.RETURN.PRINT">
            <div class="btn_div_Img" id="print_div" style="display:none">
                <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                <input type="hidden" name="print" id="print" value="${purchaseReturnDTO.print}">
                <div class="optWords">打印</div>
            </div>
        </bcgogo:if>
    </bcgogo:permission>
</div>
</form:form>
<div class="zuofei" id="zuofei"></div>

<div id="mask" style="display:block;position: absolute;">
</div>
</div>
<div class="tuihuo"></div>
<%--<div class="pendingImg" id="pendingImg"></div>--%>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;" allowtransparency="true" width="1000px" height="1000px" frameborder="0" src=""
        scrolling="no"></iframe>

<div id="id-searchcomplete" name="createGoodsReturnBill"></div>
<div id="id-searchcompleteMultiselect"></div>
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
                <td>退货商品</td>
                <td class="tab_last"></td>
            </tr>
        </table>
        <!--分页-->
        <div class="hidePageAJAX">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="draft.do?method=getDraftOrders"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,orderTypes:'RETURN'}"></jsp:param>
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
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id">
    </div>
</div>
<div id="dialog-confirm" title="提醒" style="display:none">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>

    <div id="dialog-confirm-text"></div>
    </p>
</div>
<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <jsp:include page="../txn/common/selectStoreHouse.jsp"/>
</bcgogo:hasPermission>
<div id="div_serviceName" class="i_scroll" style="display:none;width:228px;">
    <div class="Scroller-Container" id="Scroller-Container_ServiceName">
    </div>
</div>
<div id="inputMobile" style="display:none">
    <div style="margin-left: 45px;margin-top: 15px">
        <label>手机号：</label>
        <input type="text" id="divMobile" kissfocus="on" style="width:125px;height: 20px">
    </div>
</div>
<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:150px; display:none;"
        allowtransparency="true" width="900" height="470px" frameborder="0" src="" scrolling="no"></iframe>
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
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>