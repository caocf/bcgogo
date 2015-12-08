<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
  Created by IntelliJ IDEA.
  User: wjl
  Date: 11-9-30
  Time: 上午9:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page import="com.bcgogo.search.dto.InventorySearchIndexDTO" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>库存明细</title>
  <%
    boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn(WebUtil.getShopId(request));//选配仓位功能 默认开启这个功能false
    boolean tradePriceTag = ServiceManager.getService(IShopConfigService.class).isTradePriceSwitchOn(WebUtil.getShopId(request),WebUtil.getShopVersionId(request));//选配批发价功能

    String webapp = request.getContextPath();
    Integer pagenum = (Integer) request.getSession(true).getAttribute("pageNo");
    Integer pageNo = pagenum == null || pagenum <= 0 ? 1 : pagenum;
    InventorySearchIndexDTO isiDTO = (InventorySearchIndexDTO) request.getAttribute("inventorySearchIndexDTO");
    List<InventorySearchIndexDTO> isiDTOList = (List<InventorySearchIndexDTO>) request.getAttribute("inventorySearchIndexDTOList");
    Boolean isVehicleNull = isiDTO.getVehicleNull();
    isVehicleNull = isVehicleNull == null ? false : isVehicleNull;
    int isnewproduct = 0;
    if (isiDTOList == null || isiDTOList.size() <= 0) {
      isnewproduct = 1;
    }
    Integer indexnum = isiDTO.getIndexNum();
  %>
</head>
<link rel="stylesheet" type="text/css" href="<%=webapp%>/styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="<%=webapp%>/styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css"
      href="<%=webapp%>/styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="<%=webapp%>/styles/cuDetail<%=ConfigController.getBuildVersion()%>.css"/>
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
#cuDetail{
    border-color: #BBBBBB;
    border-style: solid;
    border-width: 1px;
}
</style>
<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/cudetail<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
$().ready(function () {
    window.parent.document.getElementById("iframe_PopupBox").style.display = "block";
    window.parent.document.getElementById("mask").style.height = "1150px";
    document.getElementById("div_close").onclick = closeWindow;
//    window.parent.addHandle(document.getElementById('div_drag'), window);
    if ('<%=isnewproduct%>' == '1') {
        if ('<%=isVehicleNull%>' == 'true') {
            $("#check_product_id").html("系统未找到本商品的信息");
        } else {
            $("#check_product_id").html("系统未找到本商品的信息");
        }
        $(".table_title").css({'display':'none'});
        $(".i_leftBtn").css({'display':'none'});
    }


    $(".qian_blue").each(function () {
        var jv = $(this).html();
        if (jv != "多款") {
            $(this).removeAttr("class");
            $(this).removeAttr("onclick");
        }
    });
    tableUtil.tableStyle('#cuDetail','.table_title');
});
function closeWindow() {
    window.parent.document.getElementById("mask").style.display = "none";
    window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
    try {
        $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
    } catch(e) {
        ;
    }
}

function getInventorySearchInfo(indexNo) {
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.id")) {
		 window.parent.document.getElementById("itemDTOs<%=indexnum%>.id").value = "";
	}
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.isNewAdd") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.isNewAdd").value = "0";
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.productType") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.productType").value =
            document.getElementById(indexNo + "productVehicleStatus").innerHTML;
    }
    window.parent.document.getElementById("itemDTOs<%=indexnum%>.productName").value =
        document.getElementById(indexNo + "productName").innerHTML;
		window.parent.document.getElementById("itemDTOs<%=indexnum%>.commodityCode").value =
        document.getElementById(indexNo + "commodityCode").innerHTML;
    window.parent.document.getElementById("itemDTOs<%=indexnum%>.brand").value =
        document.getElementById(indexNo + "productBrand").innerHTML;
    window.parent.document.getElementById("itemDTOs<%=indexnum%>.spec").value =
        document.getElementById(indexNo + "productSpec").innerHTML;
    window.parent.document.getElementById("itemDTOs<%=indexnum%>.model").value =
        document.getElementById(indexNo + "productModel").innerHTML;
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.isOldProduct") != null) {
        if (document.getElementById(indexNo + "productId").innerHTML != "") {
            window.parent.document.getElementById("itemDTOs<%=indexnum%>.isOldProduct").value = "true";
        } else {
            window.parent.document.getElementById("itemDTOs<%=indexnum%>.isOldProduct").value = "false";
        }
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleBrand") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleBrand").value =
            document.getElementById(indexNo + "brand").innerHTML;
        if (document.getElementById(indexNo + "brand").innerHTML == "全部" ||
            document.getElementById(indexNo + "brand").innerHTML == "多款") {
            if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleModel") != null) {
                window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleModel").disabled = true;
            }
            <%--if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleYear") != null) {--%>
                <%--window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleYear").disabled = true;--%>
            <%--}--%>
            <%--if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleEngine") != null) {--%>
                <%--window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleEngine").disabled = true;--%>
            <%--}--%>
        }
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleModel") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleModel").value =
            document.getElementById(indexNo + "model").innerHTML;
    }
<%--if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleYear") != null) {--%>
<%--window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleYear").value =--%>
<%--document.getElementById(indexNo + "year").innerHTML;--%>
<%--}--%>
<%--if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleEngine") != null) {--%>
<%--window.parent.document.getElementById("itemDTOs<%=indexnum%>.vehicleEngine").value =--%>
<%--document.getElementById(indexNo + "engine").innerHTML;--%>
<%--}--%>
    window.parent.document.getElementById("itemDTOs<%=indexnum%>.productId").value =
        document.getElementById(indexNo + "productId").innerHTML;
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.productVehicleStatus") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.productVehicleStatus").value =
            document.getElementById(indexNo + "productVehicleStatus").innerHTML;
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_vehicleBrand") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_vehicleBrand").value =
            document.getElementById(indexNo + "brand").innerHTML;
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_vehicleModel") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_vehicleModel").value =
            document.getElementById(indexNo + "model").innerHTML;
    }
<%--if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_vehicleYear") != null) {--%>
<%--window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_vehicleYear").value =--%>
<%--document.getElementById(indexNo + "year").innerHTML;--%>
<%--}--%>
<%--if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_vehicleEngine") != null) {--%>
<%--window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_vehicleEngine").value =--%>
<%--document.getElementById(indexNo + "engine").innerHTML;--%>
<%--}--%>
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_productVehicleStatus") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_productVehicleStatus").value =
            document.getElementById(indexNo + "productVehicleStatus").innerHTML;
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_productId") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.hidden_productId").value =
            document.getElementById(indexNo + "productId").innerHTML;
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.inventoryAmount") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.inventoryAmount").value =
            document.getElementById(indexNo + "amount").innerHTML;
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.inventoryAmountSpan").style.display = "block";
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.inventoryAmountSpan").innerHTML =
                document.getElementById(indexNo + "amount").innerHTML;
        var parentNode = window.parent.document.getElementById("itemDTOs<%=indexnum%>.inventoryAmount").parentNode;
        var childrenCnt = parentNode.childNodes.length;
        for (var i = 0; i < childrenCnt; i++) {
            var childNode = parentNode.childNodes[i];
            if (childNode.tagName != null && childNode.tagName.toLowerCase() == "span") {
                if (childNode.id != "itemDTOs<%=indexnum%>.inventoryAmountSpan") {
                 childNode.style.display = "none";
                }
            }
        }
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.lastPrice") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.lastPrice").value =
            document.getElementById(indexNo + "price").innerHTML;
    }

    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.lastPurchasePrice") != null) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.lastPurchasePrice").value =
            document.getElementById(indexNo + "purchasePrice").innerHTML;
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.amount")) {      //清空单据上商品数量
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.amount").value = "1";
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.purchasePrice") != null
        && document.getElementById(indexNo + "purchasePrice").innerHTML != "") {

        window.parent.document.getElementById("itemDTOs<%=indexnum%>.purchasePrice").value =
                $("#" + indexNo + "purchasePrice").html() * 1;
        try {
            window.parent.setTotal();
        } catch (e) {
        }
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.recommendedPrice") != null
        && document.getElementById(indexNo + "recommendedPrice").innerHTML != "") {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.recommendedPrice").value =
                $.trim(document.getElementById(indexNo + "recommendedPrice").innerHTML);
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.price") != null
        && document.getElementById(indexNo + "recommendedPrice").innerHTML != "") {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.price").value =
                $.trim(document.getElementById(indexNo + "recommendedPrice").innerHTML);
        try {
            window.parent.setTotal();
        } catch (e) {
        }
    }

    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.unitSpan")) {     //删除unitSpan
        var unitSpan = window.parent.document.getElementById("itemDTOs<%=indexnum%>.unitSpan");
        unitSpan.parentNode.removeChild(unitSpan);
    }

    window.parent.document.getElementById("itemDTOs<%=indexnum%>.unit").value =
            document.getElementById(indexNo + "unit").innerHTML;
    window.parent.document.getElementById("itemDTOs<%=indexnum%>.unit").style.display = "block";

    window.parent.document.getElementById("itemDTOs<%=indexnum%>.storageUnit").value =
            document.getElementById(indexNo + "storageUnit").innerHTML;

    window.parent.document.getElementById("itemDTOs<%=indexnum%>.sellUnit").value =
            document.getElementById(indexNo + "sellUnit").innerHTML;

    window.parent.document.getElementById("itemDTOs<%=indexnum%>.rate").value =
            document.getElementById(indexNo + "rate").innerHTML;


    var parentDom = $("#itemDTOs<%=indexnum%>" + "\\.unit", parent.document);
    window.parent.initUnitTd(parentDom);

    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.lowerLimit")) {
       window.parent.document.getElementById("itemDTOs<%=indexnum%>.lowerLimit").value =
               document.getElementById(indexNo + "lowerLimit").value;
    }
    if (window.parent.document.getElementById("itemDTOs<%=indexnum%>.upperLimit")) {
        window.parent.document.getElementById("itemDTOs<%=indexnum%>.upperLimit").value =
                document.getElementById(indexNo + "upperLimit").value;
    }
    if ($("#itemDTOs<%=indexnum%>\\.storageBin", parent.document)) {
        $("#itemDTOs<%=indexnum%>\\.storageBin", parent.document).val($.trim($("#" + indexNo + "storageBin").html()));
		}
    if ($("#itemDTOs<%=indexnum%>\\.storageBinSpan", parent.document)) {
        $("#itemDTOs<%=indexnum%>\\.storageBinSpan", parent.document).html($.trim($("#" + indexNo + "storageBin").html()));
		}
		if ($("#itemDTOs<%=indexnum%>\\.tradePrice", parent.document)) {
			$("#itemDTOs<%=indexnum%>\\.tradePrice", parent.document).val($.trim($("#" + indexNo + "tradePriceSpan").html()));
		}

    if ($("#orderType", parent.document).val() == "repairOrder" && typeof(window.parent.isLack) == "function") {
        window.parent.isLack();
    }
    closeWindow();
    callBack();
}
function callBack() {
    if (window.parent.operation == "search") {
        window.parent.confirmModification("itemDTOs<%=indexnum%>");
    }
    else if (window.parent.operation == "repaireOrderAdd") {
        window.parent.repaireOrderAdd();
    }
    else if (window.parent.operation == "purchaseOrderAdd") {
        window.parent.purchaseOrderAdd();
    }
    else if (window.parent.operation == "inventoryOrderAdd") {
        window.parent.inventoryOrderAdd();
    }
    else if (window.parent.operation == "repairOrderSubmit") {
        window.parent.repairOrderSubmit();
    }
    else if (window.parent.operation == "repairOrderFinish") {
        window.parent.repairOrderFinish();
    }
    else if (window.parent.operation == "repairOrderAccount") {
        window.parent.repairOrderAccount();
    }
    else if (window.parent.operation == "purchaseOrderSubmit") {
        window.parent.purchaseOrderSubmit();
    }
    else if (window.parent.operation == "inventoryOrderSubmit") {
        window.parent.inventoryOrderSubmit();
    }
}
</script>
<body>
<bcgogo:permissionParam permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
<input type="hidden" value="${permissionParam1}" name="goodsBuyPermission" id="goodsBuyPermission"/>
</bcgogo:permissionParam>
<input type="hidden" id="storageBinTag" value="<%=storageBinTag%>"/>
<input type="hidden" id="tradePriceTag" value="<%=tradePriceTag%>"/>

<div id="div_show" class="i_searchBrand">
<div class="i_arrow"></div>
<div class="i_upLeft"></div>
<div class="i_upCenter">
        <div class="i_note" id="div_drag">库存明细</div>
        <div class="i_close" id="div_close"></div>
</div>
<div class="i_upRight"></div>
<div class="i_upBody">
        <form:form name="thisform" action="searchInventoryIndex.do?method=searchproduct" method="post"
                   commandName="inventorySearchIndexDTO">
            <div class="stock_txtName"><form:input path="productName"
                                                   value="${inventorySearchIndexDTO.productName==''?'品名':inventorySearchIndexDTO.productName}"
                                                   onfocus="if(this.value=='品名')this.value=''"
                                                   onblur="if(this.value=='')this.value='品名'" autocomplete="off"
                                                   style="width:82px;height:18px;border:1px solid #BEBEBE;"/></div>
            <div class="stock_txtName">
            <div class="stock_txtLeft"></div>
                <div class="stock_txtBody" id="div_txtBody"><form:input path="productBrand"
                                                                        value="${inventorySearchIndexDTO.productBrand==''?'品牌':inventorySearchIndexDTO.productBrand}"
                                                                        cssClass="stock_text"
                                                                        onfocus="if(this.value=='品牌')this.value=''"
                                                                        onblur="if(this.value=='')this.value='品牌'"
                                                                        autocomplete="off"/></div>
                <div class="stock_txtClick"><input type="button" onfocus="this.blur();"
                                                   onclick="stockSearch(document.getElementById('productBrand'),'click');"/>
            </div>
            </div>
            <div class="stock_txtName">
            <div class="stock_txtLeft"></div>
                <div class="stock_txtBody"><form:input path="productSpec"
                                                       value="${inventorySearchIndexDTO.productSpec==''?'规格':inventorySearchIndexDTO.productSpec}"
                                                       cssClass="stock_text" onfocus="if(this.value=='规格')this.value=''"
                                                       onblur="if(this.value=='')this.value='规格'"
                                                       autocomplete="off"/></div>
                <div class="stock_txtClick"><input type="button" onfocus="this.blur();"
                                                   onclick="stockSearch(document.getElementById('productSpec'),'click');">
            </div>
            </div>
            <div class="stock_txtName">
            <div class="stock_txtLeft"></div>
                <div class="stock_txtBody"><form:input path="productModel"
                                                       value="${inventorySearchIndexDTO.productModel==''?'型号':inventorySearchIndexDTO.productModel}"
                                                       cssClass="stock_text" onfocus="if(this.value=='型号')this.value=''"
                                                       onblur="if(this.value=='')this.value='型号'"
                                                       autocomplete="off"/></div>
                <div class="stock_txtClick"><input type="button" onfocus="this.blur();"
                                                   onclick="stockSearch(document.getElementById('productModel'),'click');">
            </div>
            </div>
            <div class="stock_txtName">
            <div class="stock_txtLeft"></div>
                <div class="stock_txtBody" id="div_txtBody_1"><form:input path="brand"
                                                                          value="${inventorySearchIndexDTO.brand==''?'车辆品牌':inventorySearchIndexDTO.brand}"
                                                                          cssClass="stock_text"
                                                                          onfocus="if(this.value=='车辆品牌')this.value=''"
                                                                          onblur="if(this.value=='')this.value='车辆品牌'"
                                                                          autocomplete="off"/></div>
                <div class="stock_txtClick"><input type="button" onfocus="this.blur();"
                                                   onclick="stockSearch(document.getElementById('brand'),'click');">
            </div>
            </div>
            <div class="stock_txtName">
            <div class="stock_txtLeft"></div>
                <div class="stock_txtBody"><form:input path="model"
                                                       value="${inventorySearchIndexDTO.model==''?'车型':inventorySearchIndexDTO.model}"
                                                       cssClass="stock_text" onfocus="if(this.value=='车型')this.value=''"
                                                       onblur="if(this.value=='')this.value='车型'"
                                                       autocomplete="off"/></div>
                <div class="stock_txtClick"><input type="button" onfocus="this.blur();"
                                                   onclick="stockSearch(document.getElementById('model'),'click');">
            </div>
            </div>
            <div class="stock_txtName">
                <input type="button" class="stock_search" onfocus="this.blur();" onclick="searchSubmit(0)"/>
            </div>
            <form:hidden path="year"/>
            <form:hidden path="engine"/>
            <form:hidden path="pageStatus"/>
            <form:hidden path="maxResult"/>
            <form:hidden path="indexNum"/>
            <form:hidden path="vehicleNull"/>
            <form:hidden path="orderType"/>
            <form:hidden path="sortStatus"/>
            <input id="stockQuery" name="stockQuery" type="hidden"/>
            <table cellpadding="0" cellspacing="0" class="table2" style="table-layout:fixed;" id="cuDetail">
                <caption style="text-align: left;" id="check_product_id"></caption>
                <col width="8%"/>
                <col width="12%"/>
                <col width="12.00%"/>
                <col width="10.00%"/>
                <col width="10.00%"/>
                <col width="10.00%"/>
                <col width="10.00%"/>
                <col width="5.50%"/>
                <tr class="table_title">
                    <td style="border-left:none;">商品编码</td>
                    <td>品名<input type="button" class="ascending" id="nid" onfocus="this.blur();"
                                 onclick="switchStyle(this,'name')"/></td>
                    <td>品牌/产地<input type="button" class="ascending" id="bid" onfocus="this.blur();"
                                    onclick="switchStyle(this,'brand')"/></td>
                    <td>规格<input type="button" class="ascending" id="sid" onfocus="this.blur();"
                                 onclick="switchStyle(this,'spec')"/></td>
                    <td>型号<input type="button" class="ascending" id="mid" onfocus="this.blur();"
                                 onclick="switchStyle(this,'model')"/></td>
                    <td>车辆品牌</td>
                    <td>车型<input type="button" class="ascending" id="vid" onfocus="this.blur();"
                                 onclick="switchStyle(this,'vehicleModel')"/></td>
                  <td>单位</td>
                </tr>
                <c:if test="${inventorySearchIndexDTOList != null}">
                    <c:forEach items="${inventorySearchIndexDTOList}" var="inventorySearchIndexDTO" varStatus="status">
                        <tr class="table-row-original">
                            <td style="border-left:none;" id="${status.index}commodityCode"
                                style="text-overflow:ellipsis" title="${inventorySearchIndexDTO.commodityCode}">${inventorySearchIndexDTO.commodityCode}</td>
                            <td id="${status.index}productName" style="color:#6699cc;cursor: pointer;"
                                onclick="getInventorySearchInfo(${status.index})"
                                title="${inventorySearchIndexDTO.productName}">${inventorySearchIndexDTO.productName}</td>
                            <td id="${status.index}productBrand"
                                class="font"
                                title="${inventorySearchIndexDTO.productBrand}">${inventorySearchIndexDTO.productBrand}</td>
                            <td id="${status.index}productSpec"
                                title="${inventorySearchIndexDTO.productSpec}">${inventorySearchIndexDTO.productSpec}</td>
                            <td id="${status.index}productModel"
                                title="${inventorySearchIndexDTO.productModel}">${inventorySearchIndexDTO.productModel}</td>
                        <td id="${status.index}brand" style="text-overflow:ellipsis"
                            title="${inventorySearchIndexDTO.brand}">${inventorySearchIndexDTO.brand=="多款"||inventorySearchIndexDTO.brand=="全部"?"":inventorySearchIndexDTO.brand}</td>
                        <td id="${status.index}model" style="text-overflow:ellipsis"
                            title="${inventorySearchIndexDTO.model}">${inventorySearchIndexDTO.model}</td>
                            <td id="${status.index}year" class="text_justified"
                                style="display:none">${inventorySearchIndexDTO.year}</td>
                            <td id="${status.index}engine" class="text_justified"
                                style="display:none">${inventorySearchIndexDTO.engine}</td>
                            <td id="${status.index}unit">${inventorySearchIndexDTO.unit}</td>
                            <td id="${status.index}storageUnit"
                                style="display: none">${inventorySearchIndexDTO.storageUnit}</td>
                            <td id="${status.index}sellUnit"
                                style="display: none">${inventorySearchIndexDTO.sellUnit}</td>
                            <td id="${status.index}rate" style="display: none">${inventorySearchIndexDTO.rate}</td>
                        <td id="${status.index}productVehicleStatus"
                            style="display: none">${inventorySearchIndexDTO.productVehicleStatus}</td>
                        <td id="${status.index}productId"
                            style="display: none">${inventorySearchIndexDTO.productId}</td>
                          <%--<c:if test="<%=goodsBuy%>">--%>
                            <td id="${status.index}price" style="display: none">${inventorySearchIndexDTO.price}</td>

                          <%--</c:if>--%>
                          <td style="display: none">
                                <input type="hidden" id="${status.index}lowerLimit"
                                       value="${inventorySearchIndexDTO.lowerLimit}"/>
                                <input type="hidden" id="${status.index}upperLimit"
                                       value="${inventorySearchIndexDTO.upperLimit}"/>
                              <span id="${status.index}amount">${inventorySearchIndexDTO.amount}</span>
                              <span id="${status.index}purchasePrice">${inventorySearchIndexDTO.purchasePrice}</span>
                              <span id="${status.index}recommendedPrice">${inventorySearchIndexDTO.recommendedPrice}</span>
                          </td>
                        </tr>
                    </c:forEach>
                </c:if>
            </table>
        </form:form>
        <div class="i_leftBtn">
            <%
            if (pageNo != 1) {
            %>
            <div class="lastPage" id="PageUp" onclick="if('<%=pageNo%>'!='1'){thisformsubmit(this)}">上一页</div>
            <%}%>
            <div><%=pageNo%>
            </div>
            <%
            if (null != isiDTOList && 10 == isiDTOList.size()) {
            %>
            <div class="nextPage" id="PageDown" onclick="thisformsubmit(this)">下一页</div>
            <%}%>
        </div>
</div>
<div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
</div>
</div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Container" style="height:230px;">
        <div id="Scroller-1" style="height:225px;width:140px;">
            <div class="Scroller-Container" id="Scroller-Container_id">
            </div>
        </div>
    </div>
</div>
</body>
</html>