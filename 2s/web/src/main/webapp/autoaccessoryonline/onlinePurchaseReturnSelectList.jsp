<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>汽配在线——在线退货</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <script type="text/javascript"
            src="js/page/autoaccessoryonline/onlinePurchaseReturnSelectList<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/search/customerSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE_RETURN_ONLINE");
    </script>
</head>

<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <jsp:include page="autoAccessoryOnlineNavi.jsp">
        <jsp:param name="currPage" value="onlineReturn"/>
    </jsp:include>
    <div class="titBody">
        <div class="lineTitle">在线采购单查询</div>
        <div class="lineBody online bodys">
            <form:form commandName="orderSearchConditionDTO" id="purchaseReturnForm"
                       action="onlineReturn.do?method=getOnlinePurchaseList" method="post" name="thisform">

                <div class="divTit">采购商品：<form:input cssClass="txt J-productSuggestion J-initialCss" path="searchWord"  searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆" cssStyle="width:190px;"/></div>
                <div class="divTit">
                    <form:input cssClass="txt J-productSuggestion J-initialCss" path="productName" searchField="product_name" initialValue="品名"  cssStyle="width:90px;"/>
                </div>
                <div class="divTit">
                    <form:input cssClass="txt J-productSuggestion J-initialCss" path="productBrand" searchField="product_brand" initialValue="品牌/产地"  cssStyle="width:80px;"/>
                </div>
                <div class="divTit">
                    <form:input cssClass="txt J-productSuggestion J-initialCss" path="productSpec"  searchField="product_spec" initialValue="规格" cssStyle="width:80px;"/>
                </div>
                <div class="divTit">
                    <form:input cssClass="txt J-productSuggestion J-initialCss" path="productModel" searchField="product_model" initialValue="型号" cssStyle="width:80px;"/>
                </div>
                <div class="divTit">
                    <form:input cssClass="txt J-productSuggestion J-initialCss" path="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌" cssStyle="width:80px;"/>
                </div>
                <div class="divTit">
                    <form:input cssClass="txt J-productSuggestion J-initialCss" path="productVehicleModel" searchField="product_vehicle_model" initialValue="车型" cssStyle="width:80px;"/>
                </div>
                <div class="divTit">
                    <form:input cssClass="txt J-productSuggestion J-initialCss" path="commodityCode" searchField="commodity_code" initialValue="商品编号" cssStyle="text-transform: uppercase;width:80px;"/>
                </div>
                <div class="divTit">采购时间：
                    <label class="lbl" style="padding-left:0px;"><input id="date_this_day" type="radio" name="date_select"/>本日</label>&nbsp;
                    <label class="lbl"><input id="date_this_week" type="radio" name="date_select"/>本周</label>&nbsp;
                    <label class="lbl"><input id="date_this_month" type="radio" name="date_select"/>本月</label>&nbsp;
                    <label class="lbl"><input id="date_this_year" type="radio" name="date_select"/>本年</label>&nbsp;
                    <label class="lbl"><input id="date_default" type="radio" name="date_select"/>自定义时间段</label>
                    <form:input cssClass="txt" path="startTimeStr" cssStyle="color:#000"/>&nbsp;至&nbsp;<form:input
                            cssClass="txt" path="endTimeStr" cssStyle="color:#000"/></div>
                <div class="divTit"  style="margin-left:20px;">采购单号：<form:input cssClass="txt" path="receiptNo" style="width:100px;"/></div>
                <div class="divTit">供应商：<form:input cssClass="txt  J-initialCss" cssStyle="width: 210px" path="customerOrSupplierName"
                                                     initialValue="供应商名/手机号" id="supplierInfoText" pagetype="onlinePurchaseOrder"/></div>
                <%--J-wholesalerSuggestion--%>
                <div class="divTit"><form:input cssClass="txt  J-initialCss" path="mobile"
                                                                     initialValue="手机号"/></div>
                <div class="divTit">
                    <input class="btn" type="button" id="searchOnlinePurchaseOrderBtn"
                           style="margin-top:0px;" value="查&nbsp;询"/></div>
                <div class="divTit" >
                    <input class="btn" type="button" style="margin-top:0px;" value="重&nbsp;置" id ="reset_btn"/>
                </div>

            </form:form>
        </div>
        <div class="lineBottom"></div>
        <div class="clear height"></div>
    </div>

    <div class="titBody">
        <div class="divSlip">
            <div style="width:710px; padding-left:10px;">采购商品</div>
            <div style="width:90px;">单价</div>
            <div style="width:90px;">采购数量</div>
            <div style="width:100px;">金额</div>
        </div>
        <div class="i_height"></div>
        <div id="purchaseOrderShow">
        </div>
        <div class="purchaseOrderPager" style="margin-right:15px;">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="onlineReturn.do?method=getOnlinePurchaseList"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,maxRows:15}"></jsp:param>
                <jsp:param name="jsHandleJson" value="drawPurchaseOrders"></jsp:param>
                <jsp:param name="dynamical" value="onlinePurchaseOrder"></jsp:param>
                <jsp:param name="display" value="none"></jsp:param>
            </jsp:include>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>

</html>