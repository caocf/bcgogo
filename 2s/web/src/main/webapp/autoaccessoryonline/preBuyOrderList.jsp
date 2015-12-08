<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>我的求购</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/preBuyOrderManage<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER");
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
            <jsp:param name="currPage" value="preBuyOrderManage"/>
        </jsp:include>

        <div class="bodyLeft">
            <div class="cuSearch content-main">
                <div class="group-notice">
                    <div class="statistics">
                        本店共发布了<span id="allPreBuyOrderCount" class="yellow_color">0</span>条求购信息，
                        共有<span id="allQuotedCount" class="blue_color">0</span>次报价记录
                        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.RELEASE_PREBUYORDER">
                            <a class="button" href="preBuyOrder.do?method=createPreBuyOrder">发布求购</a>
                        </bcgogo:hasPermission>
                    </div>
                </div>
                <div class="clear i_height"></div>
                <div class="lineTitle">
                    求购查询
                </div>
                <div class="cartBody lineBody">
                    <div class="lineAll">
                        <form:form commandName="orderSearchConditionDTO" id="searchConditionForm" action="preBuyOrder.do?method=getPreBuyOrderList" method="post">
                            <input type="hidden" name="maxRows" id="maxRows" value="15">
                            <div class="divTit" style="padding-right:0px;">
                                <span class="spanName">求购商品&nbsp;</span>
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
                                <span class="spanName">发布时间&nbsp;</span>
                                <a class="btnList" id="my_date_5days" name="vestDateSelect">5天内</a>&nbsp;
                                <a class="btnList" id="my_date_10days" name="vestDateSelect">10天内</a>&nbsp;
                                <a class="btnList" id="my_date_30days" name="vestDateSelect">30天内</a>&nbsp;
                                <a class="btnList" id="my_date_defining" name="vestDateSelect">自定义</a>&nbsp;
                                <input type="text" id="startDate" name="startTimeStr" class="my_startdate txt J_clear_input" />&nbsp;至&nbsp;
                                <input type="text" id="endDate" name="endTimeStr" class="my_enddate txt J_clear_input" />
                            </div>
                            <div class="divTit">
                                <span class="spanName">有效状态&nbsp;</span>
                                <label class="rad"><input type="radio" id="radioAll" name="preBuyOrderStatus" checked="checked" value=""/>全部</label>
                                <label class="rad"><input type="radio" name="preBuyOrderStatus" value="VALID"/>有效</label>
                                <label class="rad"><input type="radio" name="preBuyOrderStatus" value="EXPIRED"/>过期</label>
                            </div>
                            <div class="divTit button_conditon button_search">
                                <a class="blue_color clean" style="float:right;" id="clearConditionBtn">清空条件</a>
                                <a class="button" style="float:right;" id="searchConditionBtn">搜 索</a>
                            </div>
                        </form:form>
                    </div>
                    <table class="tab_cuSearch tabSales" cellpadding="0" cellspacing="0" id="myPreBuyOrderTable">
                        <col>
                        <col width="100">
                        <col width="120">
                        <col width="100">
                        <col width="100">
                        <col width="120">
                        <tr class="titleBg J_title">
                            <td style="padding-left:10px;">求购商品</td>
                            <td>求购数量</td>
                            <td>发布时间</td>
                            <td>发布人</td>
                            <td>报价次数</td>
                            <td>状态</td>
                        </tr>
                        <tr class="space J_title"><td colspan="6"></td></tr>
                    </table>
                    <div class="clear i_height"></div>
                    <!----------------------------分页----------------------------------->
                    <jsp:include page="/common/pageAJAX.jsp">
                        <jsp:param name="url" value="preBuyOrder.do?method=getPreBuyOrderItem"></jsp:param>
                        <jsp:param name="jsHandleJson" value="drawPreBuyOrderItemsTable"></jsp:param>
                        <jsp:param name="dynamical" value="PreBuyOrderItems"></jsp:param>
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