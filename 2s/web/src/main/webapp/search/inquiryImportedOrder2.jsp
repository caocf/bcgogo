<%--
  Created by IntelliJ IDEA.
  User: jinyuan
  Date: 13-7-8
  Time: 上午9:36
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>导入单据</title>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"IMPORT_ORDER");
    </script>
</head>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/search/inquiryImportedOrder2<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
<body>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
<input name="pageType" id="inquiryCenterPageType" type="hidden" value="${inquiryCenterInitialDTO.pageType}"/>
<div class="mainTitles">
    <div class="titleWords">单据查询</div>
    <div class="titleList">
        <a id="sysOrderBtn" href="inquiryCenter.do?method=inquiryCenterIndex&pageType=${inquiryCenterInitialDTO.pageType}">系统单据</a>
        <a id="importedOrderBtn" href="inquiryCenter.do?method=toInquiryImportedOrder&pageType=${inquiryCenterInitialDTO.pageType}" class="click">导入单据</a>
    </div>
</div>
<div class="titBody">
<form id="inquiryCenterSearchForm" commandName="inquiryCenterInitialDTO" name="inquiryCenterSearchForm" action="inquiryCenter.do?method=inquiryImportedOrder" method="post">
<input type="hidden" name="maxRows" id="pageRows" value="20">
<input type="hidden" name="totalRows" id="totalRows" value="0">
<input type="hidden"  id="searchType"  value="importedOrder" class="searchType" name="searchType"/>
<input type="hidden" id="sortStatus" name="sort" value="created_time desc"/>
<div class="lineTitle">导入单据搜索</div>
<div class="lineTop"></div>
<div class="lineBody lineAll">
<div class="i_height"></div>
<%--日期--%>
            <div class="divTit">
                    <span class="spanName">日期</span>&nbsp;
                <a class="btnList" id="my_date_yesterday" name="my_date_select">昨天</a>&nbsp;
                <a class="btnList" id="my_date_today" name="my_date_select">今天</a>&nbsp;
                <a class="btnList" id="my_date_lastmonth" name="my_date_select">上月</a>&nbsp;
                <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
                <a class="btnList" id="my_date_thisyear" name="my_date_select">今年</a>&nbsp;
                <input id="startDate" type="text" value="今天" readonly="readonly" name="startTimeStr" class="txt my_startdate"/>&nbsp;至&nbsp;
                <input id="endDate" type="text" value="今天" readonly="readonly" name="endTimeStr" class='txt my_enddate'/>&nbsp;&nbsp;
            </div>
<%--单据类型--%>
        <div class="divTit divWarehouse member">
            <span class="spanName">单据类型</span>
            <div class="warehouseList" id="orderTypes">
                <label class="rad" id="orderTypeAllLabel"><input type="checkbox" id="orderTypeAll"/>所有</label>&nbsp;
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
                    <label class="rad" id="inventoryLabel"><input type="checkbox" name="orderType" value="INVENTORY" />入库单</label>&nbsp;
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
                        <label class="rad" id="saleLabel"><input type="checkbox"  name="orderType"  value="SALE"/>销售单</label>&nbsp;
                </bcgogo:hasPermission>
                <bcgogo:permission>
                    <bcgogo:if permissions="WEB.VEHICLE_CONSTRUCTION.BASE&&WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
                        <label class="rad" id="repairLabel"><input type="checkbox" name="orderType" value="REPAIR"/>施工单</label>&nbsp;
                    </bcgogo:if>
                </bcgogo:permission>
                <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE">
                    <label class="rad" id="washLabel"><input type="checkbox" name="orderType" value="WASH_BEAUTY"/>洗车美容</label>&nbsp;
                </bcgogo:hasPermission>

            </div>
        </div>

<%--商品--%>
        <div class="divTit divWarehouse member" id="productProperty">
            <span class="spanName">商品</span>
            <div class="warehouseList">
                    <input type="text" class="txt J-productSuggestion" id="productName" name="productName" searchField="product_name" value="${inquiryCenterInitialDTO.productName!=null?inquiryCenterInitialDTO.productName:'品名'}" initValue="品名" style="width:85px;"/>
                    <input type="text" class="txt J-productSuggestion" id="productBrand" name="productBrand" searchField="product_brand" value="${inquiryCenterInitialDTO.productBrand!=null?inquiryCenterInitialDTO.productBrand:'品牌'}" initValue="品牌" style="width:85px;"/>
                    <input type="text" class="txt J-productSuggestion" id="productSpec" name="productSpec" searchField="product_spec" value="${inquiryCenterInitialDTO.productSpec!=null?inquiryCenterInitialDTO.productSpec:'规格'}" initValue="规格" style="width:85px;"/>
                    <input type="text" class="txt J-productSuggestion" id="productModel" name="productModel" searchField="product_model" value="${inquiryCenterInitialDTO.productModel!=null?inquiryCenterInitialDTO.productModel:'型号'}" initValue="型号" style="width:85px;"/>
                    <input type="text" class="txt J-productSuggestion" id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand" value="${inquiryCenterInitialDTO.productVehicleBrand!=null?inquiryCenterInitialDTO.productVehicleBrand:'车辆品牌'}" initValue="车辆品牌" style="width:85px;"/>
                    <input type="text" class="txt J-productSuggestion" id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model" value="${inquiryCenterInitialDTO.productVehicleModel!=null?inquiryCenterInitialDTO.productVehicleModel:'车型'}" initValue="车型" style="width:85px;"/>
                    <input type="text" class="txt J-productSuggestion" id="commodityCode" name="commodityCode" searchField="commodity_code" value="${inquiryCenterInitialDTO.commodityCode!=null?inquiryCenterInitialDTO.commodityCode:'商品编号'}" initValue="商品编号" style="text-transform: uppercase;width:85px;"/>
            </div>
        </div>
<%--客户--%>
        <div class="divTit divWarehouse member" id="customerInfo">
            <span class="spanName">供应商/客户</span>
            <div class="warehouseList">
                <input type="text"  class="customerOrSupplierName txt" autocomplete="off" initValue="供应商/客户" pagetype="inquiryCenter" value="${inquiryCenterInitialDTO.customerOrSupplier!=null?inquiryCenterInitialDTO.customerOrSupplier:'供应商/客户'}" name="customerOrSupplierName" id="customer_supplierInfo">
                <input type="text"  class="contact txt" initValue="联系人" value="${inquiryCenterInitialDTO.contact!=null?inquiryCenterInitialDTO.contact:'联系人'}" name="contact" id="contact">
                <input type="text" class="mobile txt" initValue="手机" value="${inquiryCenterInitialDTO.mobile!=null?inquiryCenterInitialDTO.mobile:'手机'}" id="mobile" name="contactNum">
                <input type="hidden" name="vehicleId" id="vehicleId" value="${inquiryCenterInitialDTO.vehicleId!=null?inquiryCenterInitialDTO.vehicleId:''}">
                <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                <input type="text" class="vehicle txt" autocomplete="off"  initValue="车牌号" value="${inquiryCenterInitialDTO.vehicleNumber!=null?inquiryCenterInitialDTO.vehicleNumber:'车牌号'}" name="vehicle" id="vehicleNumber">
                    </bcgogo:hasPermission>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                        <input type="text"  class="searchDetail member txt" value="会员类型" pagetype="inquiryCenter"  name="memberType" id="memberCardType" initValue="会员类型" style="padding-bottom: 0px;">
                        <input type="text" class="searchDetail member memberNo txt"  value="会员卡号" autocomplete="off"  pagetype="inquiryCenter" name="accountMemberNo" id="memberNo" initValue="会员卡号" style="padding-bottom: 0px;">
                        <input type="text"  class="searchDetail member txt" value="计次收费项目" id="payPerProject" name="payPerProject" initValue="计次收费项目" style="padding-bottom: 0px;">
                    </bcgogo:hasPermission>
                </bcgogo:hasPermission>
                <br />
            </div>
        </div>

<%--施工类型--%>
        <div class="divTit more_condition">
            <span class="spanName">操作人</span>&nbsp;
            <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
            <input type="text"  autocomplete="off" class="repair searchDetail txt" id="serviceWorker" value="施工人" initValue="施工人" name="serviceWorker">
                </bcgogo:hasPermission>
            </bcgogo:hasPermission>
            <input type="text" value="${inquiryCenterInitialDTO.salesman!=null?inquiryCenterInitialDTO.salesman:'销售人'}" autocomplete="off" initValue="销售人" id="saler" class="sale searchDetail txt"  name="salesman">

        </div>
<div class="divTit button_conditon button_search"><a class="blue_color clean" id="resetSearchCondition">清空条件</a><a class="button" id="btnSearch">搜&nbsp;索</a></div>
</div>
<div class="lineBottom"></div>
<div class="clear i_height"></div>
</form>
</div>
<div class="clear"></div>
<div class="titBody">
    <div class="lineTop"></div>
    <div class="lineBody statisticalData">
        <div class="divTit statistics">共&nbsp;<b id="totalNum">0</b>&nbsp;条记录</div>
        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
            <div class="divTit statistics">入库（<b id="counts_inventory">0</b>笔）</div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
            <div class="divTit statistics">销售（<b id="counts_sale">0</b>笔）</div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
            <div class="divTit statistics">施工（<b id="counts_repair">0</b>笔）</div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
            <div class="divTit statistics">洗车美容（<b id="counts_wash_beauty">0</b>笔）</div>
        </bcgogo:hasPermission>

    </div>
    <div class="lineBottom"></div>
</div>

<div class="clear i_height"></div>
<div class="cuSearch">
<div class="cartTop"></div>
<div class="cartBody">
<%--<div class="line_develop list_develop">--%>
    <%--<a class="J_order_sort" sortField="receipt_no" currentSortStatus="desc">单据号<span class="arrowDown J_sort_span"></span></a>--%>
    <%--<a class="hover J_order_sort" sortField="created_time" currentSortStatus="desc">日期<span class="arrowDown J_sort_span"></span></a>--%>
    <%--<a class="J_order_sort" sortField="customer_or_supplier_name" currentSortStatus="desc">客户/供应商<span class="arrowDown J_sort_span"></span></a>--%>
    <%--<a class="J_order_sort" sortField="vehicle" currentSortStatus="desc">车牌号<span class="arrowDown J_sort_span"></span></a>--%>
    <%--<a class="J_order_sort" sortField="order_type" currentSortStatus="desc">单据类型<span class="arrowDown J_sort_span"></span></a>--%>
    <%--<a class="J_order_sort" sortField="order_content" currentSortStatus="desc">施工内容<span class="arrowDown J_sort_span"></span></a>--%>
    <%--<a class="J_order_sort" sortField="order_total_amount" currentSortStatus="desc">金额<span class="arrowDown J_sort_span"></span></a>--%>
<%--</div>--%>
<table class="tab_cuSearch" cellpadding="0" cellspacing="0">
<col width="110">
<col width="80">
<col width="130">
<col width="90">
<col width="90">
<col width="280">
<col width="80">
<col width="80">
<tr class="titleBg">
    <td style="padding-left:10px;">单据号</td>
    <td>日期</td>
    <td>客户/供应商</td>
    <td>车牌号</td>
    <td>单据类型</td>
    <td>施工内容/材料</td>
    <td>金额</td>
    <td>状态</td>
</tr>
<tr class="space"><td colspan="9"></td></tr>
</table>
<div class="height"></div>
<!----------------------------分页----------------------------------->
<bcgogo:ajaxPaging url="inquiryCenter.do?method=inquiryImportedOrder" postFn="showResponse" dynamical="inquiryCenter" display="none"/>
</div>
<div class="cartBottom"></div>
</div>
</div>
</div>
<div id="mask"  style="display:block;position: absolute;">
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>