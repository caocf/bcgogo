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
    <title>系统单据</title>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-droplist.css"/>

<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/search/inquirySystemOrder<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/search/inquirySuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/exportExcel<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-droplist.js"></script>
<script type="text/javascript">
    <bcgogo:hasPermission permissions="WEB.VERSION.VEHICLE_CONSTRUCTION" resourceType="logic">
        APP_BCGOGO.Permission.Version.VehicleConstruction = true;
    </bcgogo:hasPermission>
    defaultStorage.setItem(storageKey.MenuUid,"SYSTEM_ORDER");
</script>
</head>
<body>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
<input name="pageType" id="inquiryCenterPageType" type="hidden" value="${inquiryCenterInitialDTO.pageType}"/>
<input id="startDateStr" type="hidden" value="${inquiryCenterInitialDTO.startDateStr}"/>
<input id="endDateStr" type="hidden" value="${inquiryCenterInitialDTO.endDateStr}"/>
<input id="orderTypesHidden" type="hidden" value="${inquiryCenterInitialDTO.orderTypes}"/>
<div class="mainTitles">
    <div class="titleWords">单据查询</div>
    <div class="titleList">
        <a id="sysOrderBtn" href="inquiryCenter.do?method=inquiryCenterIndex&pageType=${inquiryCenterInitialDTO.pageType}" class="click">系统单据</a>
        <a id="importedOrderBtn" href="inquiryCenter.do?method=toInquiryImportedOrder&pageType=${inquiryCenterInitialDTO.pageType}">导入单据</a>
    </div>
</div>
<div class="titBody">
<form id="inquiryCenterSearchForm" commandName="inquiryCenterInitialDTO" name="inquiryCenterSearchForm" action="inquiryCenter.do?method=inquiryCenterSearchOrderAction" method="post">
<input type="hidden" name="maxRows" id="pageRows" value="20">
<input type="hidden" name="totalRows" id="totalRows" value="0">
<input type="hidden" id="sortStatus" name="sort" value="created_time desc"/>
<div class="lineTitle">系统单据搜索</div>
<div class="lineTop"></div>
<div class="lineBody lineAll">
<div class="i_height"></div>
<%--日期--%>
<bcgogo:orderPageConfigurationParam orderGroupName="order_other_condition" orderNameAndResource="order_time" >
    <bcgogo:orderPageConfigurationParam orderGroupName="order_type_condition" orderNameAndResource="order_number">
        <c:if test="${order_other_condition_order_time || order_type_condition_order_number}">
            <div class="divTit">
                <c:if test="${order_other_condition_order_time}">
                    <span class="spanName">日期</span>&nbsp;
                    <a class="btnList" id="my_date_yesterday" name="my_date_select">昨天</a>&nbsp;
                    <a class="btnList" id="my_date_today" name="my_date_select">今天</a>&nbsp;
                    <a class="btnList" id="my_date_lastmonth" name="my_date_select">上月</a>&nbsp;
                    <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
                    <a class="btnList" id="my_date_thisyear" name="my_date_select">今年</a>&nbsp;
                    <input id="startDate" type="text" value="${startDateStr}" readonly="readonly" name="startTimeStr" class="my_startdate txt"/>&nbsp;至&nbsp;
                    <input id="endDate" type="text" value="${endDateStr}" readonly="readonly" name="endTimeStr" class='my_enddate txt'/>&nbsp;&nbsp;
                </c:if>
                <c:if test="${order_type_condition_order_number && !order_other_condition_order_time}">
                    <span class="spanName">单据号</span>&nbsp;<input type="text" value="${inquiryCenterInitialDTO.receiptNo!=null?inquiryCenterInitialDTO.receiptNo:''}" autocomplete="off"  id="receiptNo" class="txt"  name="receiptNo">
                </c:if>
                <c:if test="${order_type_condition_order_number && order_other_condition_order_time}">
                    单据号&nbsp;<input type="text" value="${inquiryCenterInitialDTO.receiptNo!=null?inquiryCenterInitialDTO.receiptNo:''}" autocomplete="off"  id="receiptNo" class="txt"  name="receiptNo">
                </c:if>
            </div>
        </c:if>
    </bcgogo:orderPageConfigurationParam>
</bcgogo:orderPageConfigurationParam>
<%--单据类型--%>
<bcgogo:orderPageConfigurationParam orderGroupName="order_type_condition" orderNameAndResource="[purchase_order,WEB.TXN.PURCHASE_MANAGE.PURCHASE];
        [storage_order,WEB.TXN.PURCHASE_MANAGE.STORAGE];[sale_order,WEB.TXN.SALE_MANAGE.SALE];[vehicle_construction_order,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE];
        [wash_beauty_order,WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE];[purchase_return_order,WEB.TXN.PURCHASE_MANAGE.RETURN];
        [sale_return_order,WEB.TXN.SALE_MANAGE.RETURN];[buy_card_order,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER];
        [return_card,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER]" >
    <c:if test="${!order_type_condition_has_none_of_the_order_group}" >
        <div class="divTit divWarehouse member">
            <span class="spanName">单据类型</span>
            <div class="warehouseList" id="orderTypes">
                <label class="rad" id="orderTypeAllLabel"><input type="checkbox" id="orderTypeAll" data-name="all"/>所有</label>&nbsp;
                <c:choose>
                    <c:when test="${order_type_condition_purchase_order}">
                        <label class="rad" id="purchaseLabel"><input type="checkbox" name="orderType" value="PURCHASE" data-name="purchase" />采购单</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
                            <input type="hidden" name="orderType" origValue="PURCHASE" />
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_storage_order}">
                        <label class="rad" id="inventoryLabel"><input type="checkbox" name="orderType" value="INVENTORY" data-name="storage"/>入库单</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">
                            <input type="hidden" name="orderType" origValue="INVENTORY" />
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_sale_order}">
                        <label class="rad" id="saleLabel"><input type="checkbox"  name="orderType"  value="SALE" data-name="sale"/>销售单</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE">
                            <input type="hidden" name="orderType" origValue="SALE"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_vehicle_construction_order}">
                        <label class="rad" id="repairLabel"><input type="checkbox" name="orderType" value="REPAIR" data-name="construction"/>施工单</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                            <input type="hidden" name="orderType" origValue="REPAIR"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_wash_beauty_order}">
                        <label class="rad" id="washLabel"><input type="checkbox" name="orderType" value="WASH_BEAUTY" data-name="beauty"/>洗车美容</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                            <input type="hidden" name="orderType" origValue="WASH_BEAUTY"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_purchase_return_order}">
                        <label class="rad" id="returnLabel"><input type="checkbox" name="orderType" value="RETURN" data-name="return"/>入库退货单</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN" >
                            <input type="hidden" name="orderType" origValue="RETURN"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_sale_return_order}">
                        <label class="rad" id="saleReturnLabel"><input type="checkbox" name="orderType"  value="SALE_RETURN" data-name="saleReturn"/>销售退货单</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
                            <input type="hidden" name="orderType" origValue="SALE_RETURN"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_buy_card_order}">
                        <label class="rad" id="memberLabel"><input type="checkbox" name="orderType" value="MEMBER_BUY_CARD" data-name="buyCard"/>会员购卡续卡</label>&nbsp;
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                            <input type="hidden" name="orderType" origValue="MEMBER_RETURN_CARD"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${order_type_condition_return_card}">
                        <label class="rad" id="memberReturnLabel"><input type="checkbox" name="orderType"  value="MEMBER_RETURN_CARD" data-name="returnCard"/>会员退卡</label>
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                            <input type="hidden" name="orderType" origValue="MEMBER_RETURN_CARD"/>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>

            </div>
        </div>
    </c:if>
</bcgogo:orderPageConfigurationParam>
<%--商品--%>
<bcgogo:orderPageConfigurationParam orderGroupName="order_product_condition" orderNameAndResource="product_info;commodity_code">
    <c:if test="${!order_product_condition_has_none_of_the_order_group}">
        <div class="divTit divWarehouse member" id="productProperty">
            <span class="spanName">商品</span>
            <div class="warehouseList">
                <c:if test="${order_product_condition_product_info}">
                    <input type="text" class="txt J-productSuggestion" id="searchWord" name="searchWord" searchField="product_info" value="品名/品牌/规格/型号/适用车辆" initValue="品名/品牌/规格/型号/适用车辆" style="width:180px;" />&nbsp;
                    <input type="text" class="txt J-productSuggestion" id="productName" name="productName" searchField="product_name" value="${inquiryCenterInitialDTO.productName!=null?inquiryCenterInitialDTO.productName:'品名'}" initValue="品名" style="width:85px;"/>
                    <input type="text" class="txt J-productSuggestion" id="productBrand" name="productBrand" searchField="product_brand" value="${inquiryCenterInitialDTO.productBrand!=null?inquiryCenterInitialDTO.productBrand:'品牌/产地'}" initValue="品牌/产地" style="width:85px;"/>
                    <input type="text" class="txt J-productSuggestion" id="productSpec" name="productSpec" searchField="product_spec" value="${inquiryCenterInitialDTO.productSpec!=null?inquiryCenterInitialDTO.productSpec:'规格'}" initValue="规格" style="width:85px;"/>
                    <input type="text" class="txt J-productSuggestion" id="productModel" name="productModel" searchField="product_model" value="${inquiryCenterInitialDTO.productModel!=null?inquiryCenterInitialDTO.productModel:'型号'}" initValue="型号" style="width:85px;"/>
                    <input type="text" class="txt J-productSuggestion" id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand" value="${inquiryCenterInitialDTO.productVehicleBrand!=null?inquiryCenterInitialDTO.productVehicleBrand:'车辆品牌'}" initValue="车辆品牌" style="width:85px;"/>
                    <input type="text" class="txt J-productSuggestion" id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model" value="${inquiryCenterInitialDTO.productVehicleModel!=null?inquiryCenterInitialDTO.productVehicleModel:'车型'}" initValue="车型" style="width:85px;"/>
                </c:if>
                <c:if test="${order_product_condition_commodity_code}">
                    <input type="text" class="txt J-productSuggestion" id="commodityCode" name="commodityCode" searchField="commodity_code" value="${inquiryCenterInitialDTO.commodityCode!=null?inquiryCenterInitialDTO.commodityCode:'商品编号'}" initValue="商品编号" style="text-transform: uppercase;width:85px;"/>
                </c:if>
            </div>
        </div>
    </c:if>
</bcgogo:orderPageConfigurationParam>
<%--客户--%>
<bcgogo:orderPageConfigurationParam orderGroupName="order_customer_supplier_condition" orderNameAndResource="name;contact;mobile;
         area;[member_type,WEB.VERSION.VEHICLE_CONSTRUCTION];[member_card_no,WEB.VERSION.VEHICLE_CONSTRUCTION];
         [payPerProject,WEB.VERSION.VEHICLE_CONSTRUCTION]">
    <c:if test="${order_customer_supplier_condition_name || order_customer_supplier_condition_contact || order_customer_supplier_condition_mobile || order_customer_supplier_condition_license_number || order_customer_supplier_condition_area}">
        <div class="divTit divWarehouse member" id="customerInfo">
            <span class="spanName">供应商/客户</span>
            <div class="warehouseList">
                <c:if test="${order_customer_supplier_condition_name}"><input type="text"  class="customerOrSupplierName txt" autocomplete="off" initValue="供应商/客户" pagetype="inquiryCenter" value="${inquiryCenterInitialDTO.customerOrSupplier!=null?inquiryCenterInitialDTO.customerOrSupplier:'供应商/客户'}" name="customerOrSupplierName" id="customer_supplierInfoText"></c:if>
                <c:if test="${order_customer_supplier_condition_contact}"><input type="text"  class="contact txt" initValue="联系人" value="${inquiryCenterInitialDTO.contact!=null?inquiryCenterInitialDTO.contact:'联系人'}" name="contact" id="contact"></c:if>
                <c:if test="${order_customer_supplier_condition_mobile}"><input type="text" class="mobile txt" initValue="手机" value="${inquiryCenterInitialDTO.mobile!=null?inquiryCenterInitialDTO.mobile:'手机'}" id="mobile" name="contactNum"> </c:if>

                    <c:if test="${order_customer_supplier_condition_area}">
                所属区域&nbsp; <select class="txt J_province" name="province"><option value="">-所有省-</option></select>&nbsp;<select class="txt J_city" name="city"><option value="">-所有市-</option></select>&nbsp;<select class="txt J_region" name="region"><option value="">-所有区-</option></select>
                    </c:if>
            </div>
        </div>
    </c:if>
    <div class="divTit member">
        <c:if test="${order_customer_supplier_condition_member_type || order_customer_supplier_condition_member_card_no || order_customer_supplier_condition_payPerProject}">
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
            <span class="spanName">会员</span>
                    <span style="display: inline-block; margin-left: 5px;">
                    <c:if test="${order_customer_supplier_condition_member_type}"><input type="text"  class="searchDetail memberRelated txt" value="会员类型" pagetype="inquiryCenter"  name="memberType" id="memberCardType" initValue="会员类型" style="padding-bottom: 0px;">  </c:if>
                    <c:if test="${order_customer_supplier_condition_member_card_no}"><input type="text" class="searchDetail memberRelated memberNo txt"  value="${not empty inquiryCenterInitialDTO.memberNo?inquiryCenterInitialDTO.memberNo:'会员卡号'}" autocomplete="off"  pagetype="inquiryCenter" name="accountMemberNo" id="customerMemberNo" initValue="会员卡号" style="padding-bottom: 0px;">   </c:if>
                    <c:if test="${order_customer_supplier_condition_payPerProject}"><input type="text"  class="searchDetail memberRelated txt" value="计次收费项目" id="payPerProject" name="payPerProject" initValue="计次收费项目" style="padding-bottom: 0px;"></c:if>
                    </span>
                </bcgogo:hasPermission>
            </c:if>
    </div>
</bcgogo:orderPageConfigurationParam>

<bcgogo:orderPageConfigurationParam orderGroupName="order_vehicle_condition" orderNameAndResource="[license_number,WEB.VERSION.VEHICLE_CONSTRUCTION];brand;model;color">
    <div class="divTit">
        <c:if test="${order_vehicle_condition_license_number || order_vehicle_condition_brand || order_vehicle_condition_model || order_vehicle_condition_color}">
            <span class="spanName">车辆信息</span>
            <span style="display: inline-block; margin-left: 5px;">
                    <c:if test="${order_vehicle_condition_license_number}">
                        <input type="hidden" name="vehicleId" id="vehicleId"
                               value="${inquiryCenterInitialDTO.vehicleId!=null?inquiryCenterInitialDTO.vehicleId:''}">
                        <input type="text" class="vehicle txt" autocomplete="off" initValue="车牌号"
                               value="${inquiryCenterInitialDTO.vehicleNumber!=null?inquiryCenterInitialDTO.vehicleNumber:'车牌号'}"
                               name="vehicle" id="vehicleNumber">
        </c:if>
                    <c:if test="${order_vehicle_condition_brand}">
                        <input type="text" class="vehicle txt" autocomplete="off" initValue="车辆品牌"
                               value="${inquiryCenterInitialDTO.vehicleBrand!=null?inquiryCenterInitialDTO.vehicleBrand:'车辆品牌'}"
                               name="vBrand" id="vehicleBrand">
                    </c:if>
                    <c:if test="${order_vehicle_condition_model}">
                        <input type="text" class="vehicle txt" autocomplete="off" initValue="车型"
                               value="${inquiryCenterInitialDTO.vehicleModel!=null?inquiryCenterInitialDTO.vehicleModel:'车型'}"
                               name="vModel" id="vehicleModel">
                    </c:if>
                    <c:if test="${order_vehicle_condition_color}">
                        <input type="text" class="vehicle txt" autocomplete="off" initValue="车身颜色"
                               value="${inquiryCenterInitialDTO.vehicleColor!=null?inquiryCenterInitialDTO.vehicleColor:'车身颜色'}"
                               name="vColor" id="vehicleColor">
                    </c:if>
                    </span>
        </c:if>
    </div>
</bcgogo:orderPageConfigurationParam>
<%--施工类型--%>
<bcgogo:orderPageConfigurationParam orderGroupName="order_operator_condition" orderNameAndResource="[service_worker,WEB.VERSION.VEHICLE_CONSTRUCTION];salesman;operator">
    <c:if test="${!order_operator_condition_has_none_of_the_order_group}">
        <div class="divTit more_condition">
            <span class="spanName">操作人</span>
            <span style="display: inline-block; margin-left: 5px;">
            <c:if test="${order_operator_condition_service_worker}"><input type="text"  autocomplete="off" class="repair searchDetail txt" id="serviceWorker" value="施工人" initValue="施工人" name="serviceWorker"></c:if>
            <c:if test="${order_operator_condition_salesman}"><input type="text" value="${inquiryCenterInitialDTO.salesman!=null?inquiryCenterInitialDTO.salesman:'销售人'}" autocomplete="off" initValue="销售人" id="saler" class="sale searchDetail txt"  name="salesman"></c:if>
            <c:if test="${order_operator_condition_operator}"> <input type="text" value="操作人" initValue="操作人" name="operator" id="operator" class="txt" lastValue=""><input type="hidden" id="operatorId" name="operatorId" /></c:if>
            </span>
        </div>
    </c:if>
</bcgogo:orderPageConfigurationParam>
<bcgogo:orderPageConfigurationParam orderGroupName="order_other_condition" orderNameAndResource="order_status_repeal" >
    <c:if test="${order_other_condition_order_status_repeal}">
        <div class="divTit more_condition">
            <span class="spanName">&nbsp;&nbsp;是否包含作废&nbsp;&nbsp;</span>&nbsp;<label class="rad"><input type="radio" name="orderStatusRepeal" value="YES"/>是</label>&nbsp;&nbsp;<label class="rad"><input type="radio" name="orderStatusRepeal" checked="checked" value="NO"/>否</label>
        </div>
    </c:if>
</bcgogo:orderPageConfigurationParam>
<%--结算方式--%>
<bcgogo:orderPageConfigurationParam orderGroupName="order_pay_method_condition" orderNameAndResource="cash;bankCard;cheque;deposit;[customer_deposit,WEB.VERSION.CUSTOMER.DEPOSIT.USE];
        [member_balance_pay,WEB.VERSION.VEHICLE_CONSTRUCTION];not_paid;statement_account;expense_amount;[coupon,WEB.VERSION.VEHICLE_CONSTRUCTION]">
    <c:if test="${!order_pay_method_condition_has_none_of_the_order_group}">
        <div class="divTit divWarehouse member more_condition" id="settlementMethod">
            <span class="spanName">结算方式</span>
            <div class="warehouseList">
                <c:if test="${order_pay_method_condition_cash}">
                    <label class="rad"><input type="checkbox" value="CASH"  id="cash" name="payMethod"/>现金</label>&nbsp;
                </c:if>
                <c:if test="${order_pay_method_condition_bankCard}">
                    <label class="rad"><input type="checkbox" value="BANK_CARD"  id="bankCard" name="payMethod"/>银联</label>
                </c:if>
                <c:if test="${order_pay_method_condition_cheque}">
                    <label class="rad"><input type="checkbox" value="CHEQUE" id="cheque" name="payMethod"/>支票</label>
                </c:if>
                <c:if test="${order_pay_method_condition_customer_deposit}">
                    <label class="rad"><input type="checkbox" value="CUSTOMER_DEPOSIT" id="customerDeposit" name="payMethod"/>预收款</label>
                </c:if>
                <c:if test="${order_pay_method_condition_deposit}">
                    <label class="rad"><input type="checkbox" value="DEPOSIT" id="deposit" name="payMethod"/>预付款</label>
                </c:if>
                <c:if test="${order_pay_method_condition_member_balance_pay}">
                    <label class="rad"><input type="checkbox" value="MEMBER_BALANCE_PAY" id="memberBalancePay" name="payMethod"/>会员储值</label>
                </c:if>
                <c:if test="${order_pay_method_condition_not_paid}">
                    <label class="rad"><input type="checkbox" value="true" id="notPaid" name="notPaid"/>挂账</label>
                </c:if>
                <c:if test="${order_pay_method_condition_statement_account}">
                    <label class="rad"><input type="checkbox" value="STATEMENT_ACCOUNT" id="statement_account" name="payMethod"/>对账</label>
                </c:if>
                <c:if test="${order_pay_method_condition_coupon}">
                    <label class="rad"><input type="checkbox" value="COUPON" id="coupon" name="payMethod"/>消费券</label>
                    <input type="text" initValue='消费券类型' id="couponType" name="couponType" class="txt" />
                </c:if>
                <c:if test="${order_pay_method_condition_expense_amount}">
                    消费金额
                    <input type="text" class="mon_search txt" name="amountLower" id="amountLower" style="width:90px;"/>~<input type="text" class="mon_search txt" name="amountUpper" id="amountUpper" style="width:90px;"/> 元
                </c:if>
            </div>
        </div>
    </c:if>
</bcgogo:orderPageConfigurationParam>
<div class="divTit button_conditon button_search"><a class="blue_color clean J_clean_style" id="resetSearchCondition" href="javascript:">清空条件</a><a class="button" id="btnSearch">搜&nbsp;索</a></div>
</div>
<div class="lineBottom"></div>
<div class="clear i_height"></div>
</form>
</div>
<div class="clear"></div>
<div class="titBody">
    <div class="lineTop"></div>
    <div class="lineBody" id="statisticsInfo">
        <div class="divTit">共&nbsp;<b id="totalNum">0</b>&nbsp;条记录&nbsp;&nbsp;</div>
        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
            <div class="divTit" data-type="purchase">采购（<b id="counts_order_total_amount_order_type_purchase">0</b>笔）</div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
            <div class="divTit" data-type="storage">入库（<b id="counts_order_total_amount_order_type_inventory">0</b>笔&nbsp;
                应付<b class="green_color" id="amounts_order_total_amount_order_type_inventory">0</b>元&nbsp;
                实付<b class="green_color" id="amounts_order_settled_amount_order_type_inventory">0</b>元&nbsp;
                欠款<b class="green_color" id="amounts_order_debt_amount_order_type_inventory">0</b>元）</div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
            <div class="divTit" data-type="sale">销售（<b id="counts_order_total_amount_order_type_sale">0</b>笔&nbsp;
                应收<b class="red_color" id="amounts_order_total_amount_order_type_sale">0</b>元&nbsp;
                实收<b class="red_color" id="amounts_order_settled_amount_order_type_sale">0</b>元&nbsp;
                欠款<b class="red_color" id="amounts_order_debt_amount_order_type_sale">0</b>元）</div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
            <div class="divTit" data-type="construction">施工（<b id="counts_order_total_amount_order_type_repair">0</b>笔&nbsp;
                应收<b class="red_color" id="amounts_order_total_amount_order_type_repair">0</b>元&nbsp;
                实收<b class="red_color" id="amounts_order_settled_amount_order_type_repair">0</b>元&nbsp;
                欠款<b class="red_color" id="amounts_order_debt_amount_order_type_repair">0</b>元）</div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
            <div class="divTit" data-type="beauty">洗车美容（<b id="counts_order_total_amount_order_type_wash_beauty">0</b>笔&nbsp;
                应收<b class="red_color" id="amounts_order_total_amount_order_type_wash_beauty">0</b>元&nbsp;
                实收<b class="red_color" id="amounts_order_settled_amount_order_type_wash_beauty">0</b>元&nbsp;
                欠款<b class="red_color" id="amounts_order_debt_amount_order_type_wash_beauty">0</b>元）</div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN||WEB.TXN.PURCHASE_MANAGE.RETURN" resourceType="menu">
            <div class="divTit" data-type="return">入库退货（<b id="counts_order_total_amount_order_type_return">0</b>笔&nbsp;
                应收<b class="red_color" id="amounts_order_total_amount_order_type_return">0</b>元&nbsp;
                实收<b class="red_color" id="amounts_order_settled_amount_order_type_return">0</b>元&nbsp;
                欠款<b class="red_color" id="amounts_order_debt_amount_order_type_return">0</b>元）</div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN||WEB.TXN.PURCHASE_MANAGE.RETURN" resourceType="menu">
            <div class="divTit" data-type="saleReturn">销售退货（<b id="counts_order_total_amount_order_type_sale_return">0</b>笔&nbsp;
                应付<b class="green_color" id="amounts_order_total_amount_order_type_sale_return">0</b>元&nbsp;
                实付<b class="green_color" id="amounts_order_settled_amount_order_type_sale_return">0</b>元&nbsp;
                欠付<b class="green_color" id="amounts_order_debt_amount_order_type_sale_return">0</b>元）</div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
            <div class="divTit" data-type="buyCard">购卡（<b id="counts_order_total_amount_order_type_member_buy_card">0</b>笔&nbsp;
                应收<b class="red_color" id="amounts_order_total_amount_order_type_member_buy_card">0</b>元&nbsp;
                实收<b class="red_color" id="amounts_order_settled_amount_order_type_member_buy_card">0</b>元&nbsp;
                欠款<b class="red_color" id="amounts_order_debt_amount_order_type_member_buy_card">0</b>元）</div>
            <div class="divTit" data-type="returnCard">退卡（<b id="counts_order_total_amount_order_type_member_return_card">0</b>笔<b class="green_color" id="amounts_debt_and_settled_amount_order_type_member_return_card">0</b>元）</div>
        </bcgogo:hasPermission>
    </div>
    <div class="lineBottom"></div>
</div>

<div class="clear i_height"></div>
<div class="cuSearch">
<div class="cartTop"></div>
<div class="cartBody">
<div class="line_develop list_develop sort_title_width">
    <div class="sort_label">排序方式：</div>
    <a class="J_order_sort" sortField="receipt_no" currentSortStatus="desc">单据号<span class="arrowDown J_sort_span"></span></a>
    <a class="hover J_order_sort" sortField="created_time" currentSortStatus="desc">日期<span class="arrowDown J_sort_span"></span></a>
    <a class="J_order_sort" sortField="order_total_amount" currentSortStatus="desc">金额<span class="arrowDown J_sort_span"></span></a>
</div>
<table class="tab_cuSearch J_tab_cuSearch" cellpadding="0" cellspacing="0">
<col width="110">
<col width="80">
<col width="130">
    <bcgogo:hasPermission permissions="WEB.VERSION.VEHICLE_CONSTRUCTION" resourceType="logic">
        <col width="70">
    </bcgogo:hasPermission>
<col width="80">
<col>
<col width="60">
<col width="70">
<col width="60">
<col width="60">
<col width="70">
<tr class="titleBg">
    <td style="padding-left:10px;">单据号</td>
    <td>日期</td>
    <td>客户/供应商</td>
    <bcgogo:hasPermission permissions="WEB.VERSION.VEHICLE_CONSTRUCTION" resourceType="logic">
    <td>车牌号</td>
    </bcgogo:hasPermission>
    <td>单据类型</td>
    <td>内容</td>
    <td style="padding-left: 10px;">总计</td>
    <td style="padding-left: 10px;">实收/实付</td>
    <td style="padding-left: 10px;">欠款</td>
    <td style="padding-left: 10px;">优惠</td>
    <td style="padding-left: 10px;">状态</td>
</tr>
<tr class="space"><td colspan="9"></td></tr>
</table>
<div class="height"></div>
<!----------------------------分页----------------------------------->
<bcgogo:ajaxPaging url="inquiryCenter.do?method=inquiryCenterSearchOrderAction" postFn="showResponse" dynamical="inquiryCenter" display="none"/>
</div>
<div class="cartBottom"></div>
<div class="height"></div>
<div class="divTit"><a  id="export" class="button">导出 </a></div>
    <img id="exporting" style="margin-left: 30px; display: none;" title="正在导出" alt="正在导出" src="images/loadinglit.gif">
</div>
</div>
</div>
<div id="mask"  style="display:block;position: absolute;">
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>


<!--下拉菜单-->
<div id="div_brand_head" class="i_scroll" style="display:none;height:230px;width:154px;">
    <div class="Scroller-Container" id="Scroller-Container_id_head" style="width:150px;">
    </div>
</div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px;width:154px;">
    <div class="Scroller-Container" id="Scroller-Container_id" style="width:150px;">
    </div>
</div>

<div id="div_brandvehicleheader" class="i_scroll" style="display:none;width:132px;">      <%--todo 需要整合--%>
    <div class="Container" style="width:132px;">
        <div id="Scroller-1header" style="width:132px;">
            <div class="Scroller-Containerheader" id="Scroller-Container_idheader">
            </div>
        </div>
    </div>
</div>
<div id="memberCardTypesPanel" style="display:none;color: #000000;background: none repeat scroll 0 0 #FFFFFF;line-height: 22px;">
    <c:forEach var="cardType" items='${memberCardTypes}'>
        <div style="overflow:hidden;padding:0 5px 0 5px;" title="${cardType}">${cardType}</div>
    </c:forEach>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>