<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>我的报价</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/quotedPreBuyOrderManage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB_AUTOACCESSORYONLINE_MY_QUOTEDPREBUYORDER");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"列表");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="quotedPreBuyOrderManage"/>
        </jsp:include>

        <div class="bodyLeft">
            <div class="cuSearch content-main">
                <div class="group-notice">
                    <div class="statistics">
                        本店共发布了&nbsp;<span class="yellow_color" id="allQuotedPreBuyOrderCount">0</span>&nbsp;条报价信息，通过报价下单的共 &nbsp;<span class="blue_color" id="allOrdersCount">0</span>&nbsp;次
                    </div>
                </div>
                <div class="clear i_height"></div>
                <div class="lineTitle">
                    报价查询
                </div>
                <div class="cartBody lineBody">
                    <div class="lineAll">
                        <form:form commandName="orderSearchConditionDTO" id="searchConditionForm" action="preBuyOrder.do?method=getQuotedPreBuyOrderList" method="post">
                            <input type="hidden" name="maxRows" id="maxRows" value="15">
                            <div class="divTit" style="padding-right:0px;">
                                <span class="spanName">报价商品&nbsp;</span>
                                <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="searchWord" searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆" cssStyle="width:170px;"/>
                                <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productName" searchField="product_name" initialValue="品名" cssStyle="width:65px;"/>
                                <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productBrand" searchField="product_brand" initialValue="品牌/产地" cssStyle="width:65px;"/>
                                <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productSpec" searchField="product_spec" initialValue="规格" cssStyle="width:65px;"/>
                                <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productModel" searchField="product_model" initialValue="型号" cssStyle="width:65px;"/>
                                <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌" cssStyle="width:65px;"/>
                                <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productVehicleModel" searchField="product_vehicle_model" initialValue="车型" cssStyle="width:65px;"/>
                                <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="commodityCode" searchField="commodity_code" initialValue="商品编号" cssStyle="text-transform: uppercase;width:60px;"/>
                            </div>
                            <div class="divTit">
                                <span class="spanName">报价时间&nbsp;</span>
                                <label class="rad"><input type="radio" name="vestDateRadio" value="5"/>5天内</label>
                                <label class="rad"><input type="radio" name="vestDateRadio" value="10" />10天内</label>
                                <label class="rad"><input type="radio" name="vestDateRadio" value="30" />30天内</label>
                                <label class="rad"><input type="radio" name="vestDateRadio" value="60" />60天内</label>
                                <label class="rad"><input type="radio" name="vestDateRadio" value="5" />自定义</label>
                                <input type="text" id="startDate" name="startTimeStr" class="txt J_clear_input" />&nbsp;至&nbsp;<input type="text" id="endDate" name="endTimeStr" class="txt J_clear_input" />
                            </div>
                            <div class="divTit button_conditon button_search">
                                <a class="blue_color clean" style="float:right;" id="clearConditionBtn">清空条件</a>
                                <a class="button" style="float:right;" id="searchConditionBtn">搜 索</a>
                            </div>
                        </form:form>
                    </div>
                    <table class="tab_cuSearch tabSales" cellpadding="0" cellspacing="0" id="myQuotedPreBuyOrderTable">
                        <col>
                        <col width="100">
                        <col width="120">
                        <col width="60">
                        <col width="140">
                        <col width="60">
                        <tr class="titleBg J_title">
                            <td style="padding-left:10px;">商机信息</td>
                            <td>报价信息</td>
                            <td>报价时间</td>
                            <td>报价人</td>
                            <td>求购买家</td>
                            <td>状态</td>
                        </tr>
                        <tr class="space J_title"><td colspan="6"></td></tr>
                    </table>
                    <div class="clear i_height"></div>
                    <!----------------------------分页----------------------------------->
                    <jsp:include page="/common/pageAJAX.jsp">
                        <jsp:param name="url" value="preBuyOrder.do?method=getQuotedPreBuyOrderList"></jsp:param>
                        <jsp:param name="jsHandleJson" value="drawQuotedPreBuyOrdersTable"></jsp:param>
                        <jsp:param name="dynamical" value="QuotedPreBuyOrders"></jsp:param>
                        <jsp:param name="display" value="none"></jsp:param>
                    </jsp:include>
                </div>
                <div class="lineBottom"></div>
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