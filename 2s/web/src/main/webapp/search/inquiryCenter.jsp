<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%--@elvariable id="inquiryCenterInitialDTO" type="com.bcgogo.search.dto.OrderSearchConditionDTO"--%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>查询中心</title>

    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/searchCenter<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up_storage<%=ConfigController.getBuildVersion()%>.css"/>
	<%@include file="/WEB-INF/views/style_thirdparty_extension_core.jsp"%>
    <%@include file="/WEB-INF/views/style_ui_components.jsp"%>
	<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css" />

	<%@include file="/WEB-INF/views/script_thirdparty_extension_core.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
	<%@include file="/WEB-INF/views/script_ui_components.jsp"%>

	<script type="text/javascript" src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-droplist-lite<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/page/search/suggestionBase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/search/productSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/search/customerSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/search/inquirySuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/search/inquiryCenter<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/vehiclenosolrheader<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
        APP_BCGOGO.Permission.Version.StoreHouse = true;
        </bcgogo:hasPermission>
    </script>
</head>
<body class="bodyMain" style="overflow:hidden;font-family:'宋体';">
<div class="i_history" >
<div class="i_arrow"></div>
<div class="i_upLeft"></div>
<div class="i_upCenter">
    <div class="i_note">查询中心</div>
    <div class="i_close" id="div_close" ></div>
</div>
<input name="pageType" id="inquiryCenterPageType" type="hidden" value="${inquiryCenterInitialDTO.pageType}"/>
<div class="i_upRight"></div>
<div class="i_upBody i_mainRight">
<div class="s_main clear"  id="inquiryCenterSearch" >
<form id="inquiryCenterSearchForm" commandName="inquiryCenterInitialDTO" name="inquiryCenterSearchForm" action="inquiryCenter.do?method=inquiryCenterSearchOrderAction" method="post">
    <input type="hidden" name="pageRows" id="pageRows" value="15">
    <input type="hidden" name="totalRows" id="totalRows" value="0">
    <input type="hidden" id="sortStatus" name="sort" value=""/>
  <%--日期--%>
  <div class="order_search_tab">
    <a id="sysOrderBtn" href="inquiryCenter.do?method=inquiryCenterIndex&pageType=${inquiryCenterInitialDTO.pageType}"  class="hover_title" >系统单据</a>
    <a id="importedBtn"  href="inquiryCenter.do?method=toInquiryImportedOrder&pageType=${inquiryCenterInitialDTO.pageType}" class="disStatus" style="color: #787777;">导入单据</a>
  </div>
  <div class="clear"></div>
    <bcgogo:orderPageConfigurationParam orderGroupName="order_other_condition" orderNameAndResource="order_time" >
        <c:if test="${order_other_condition_order_time}">
            <div class="good_his">
                <strong>日期：</strong>

                <div class="i_searchTime"><input id="startDate" type="text" value="今天" readonly="readonly" name="startTimeStr" /></div>
                <label>至</label>

                <div class="i_searchTime"><input id="endDate" type="text" value="今天" readonly="readonly" name="endTimeStr" /></div>
                <div class="today_list his_first">
                    <a class="his_now" id="date_yesterday">昨天</a>
                </div>
                <div class="today_list hoverList">
                    <a class="his_now" id="date_today">今天</a>
                </div>
                <div class="today_list">
                    <a class="his_now" id="date_last_month">上月</a>
                </div>
                <div class="today_list">
                    <a class="his_now" id="date_this_month">本月</a>
                </div>
                <div class="today_list">
                    <a class="his_now" id="date_this_year">今年</a>
                </div>

                <a class="moreCondition blue_col" id="moreConditionBtn">更多条件</a>
                <a class="moreCondition condition blue_col" id="resetSearchCondition">重置条件</a>
            </div>
        </c:if>
    </bcgogo:orderPageConfigurationParam>
    <%--单据类型--%>
    <bcgogo:orderPageConfigurationParam orderGroupName="order_type_condition" orderNameAndResource="[purchase_order,WEB.TXN.PURCHASE_MANAGE.PURCHASE];
        [storage_order,WEB.TXN.PURCHASE_MANAGE.STORAGE];[sale_order,WEB.TXN.SALE_MANAGE.SALE];[vehicle_construction_order,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE];
        [wash_beauty_order,WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE];[purchase_return_order,WEB.TXN.PURCHASE_MANAGE.RETURN];
        [sale_return_order,WEB.TXN.SALE_MANAGE.RETURN];[buy_card_order,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER];
        [return_card,WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER]" >
        <bcgogo:orderPageConfigurationParam orderGroupName="order_other_condition" orderNameAndResource="order_status_repeal" >
            <c:if test="${!order_type_condition_has_none_of_the_order_group||order_other_condition_order_status_repeal}">
                <div class="goods_chk clear">
                    <c:if test="${!order_type_condition_has_none_of_the_order_group}">
                        <strong>单据类型：</strong>
                        <label class="label_check chk_off" id="orderTypeAllLabel"><input type="checkbox" id="orderTypeAll"/>所有</label>
                        <c:choose>
                            <c:when test="${order_type_condition_purchase_order}">
                                <label class="label_check chk_off" id="purchaseLabel"><input type="checkbox" name="orderType" checked="checked" value="PURCHASE"/>采购单</label>
                            </c:when>
                            <c:otherwise>
                                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
                                    <input type="hidden" name="orderType" origValue="PURCHASE"/>
                                </bcgogo:hasPermission>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${order_type_condition_storage_order}">
                                <label class="label_check chk_off" id="inventoryLabel"><input type="checkbox" name="orderType" checked="checked" value="INVENTORY"/>入库单</label>
                            </c:when>
                            <c:otherwise>
                                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">
                                    <input type="hidden" name="orderType" origValue="INVENTORY"/>
                                </bcgogo:hasPermission>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${order_type_condition_sale_order}">
                                <label class="label_check chk_off" id="saleLabel"><input type="checkbox" name="orderType" checked="checked" value="SALE"/>销售单</label>
                            </c:when>
                            <c:otherwise>
                                <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE">
                                    <input type="hidden" name="orderType" origValue="SALE"/>
                                </bcgogo:hasPermission>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${order_type_condition_vehicle_construction_order}">
                                <label class="label_check chk_off" id="repairLabel"><input type="checkbox" name="orderType" checked="checked" value="REPAIR"/>施工单</label>
                            </c:when>
                            <c:otherwise>
                                <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                                    <input type="hidden" name="orderType" origValue="REPAIR"/>
                                </bcgogo:hasPermission>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${order_type_condition_wash_beauty_order}">
                                <label class="label_check chk_off" id="washLabel"><input type="checkbox" name="orderType" checked="checked" value="WASH_BEAUTY"/>洗车美容</label>
                            </c:when>
                            <c:otherwise>
                                <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                                    <input type="hidden" name="orderType" origValue="WASH_BEAUTY"/>
                                </bcgogo:hasPermission>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${order_type_condition_purchase_return_order}">
                                <label class="label_check chk_off" id="returnLabel"><input type="checkbox" name="orderType" checked="checked" value="RETURN"/>入库退货单</label>
                            </c:when>
                            <c:otherwise>
                                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN" >
                                    <input type="hidden" name="orderType" origValue="RETURN"/>
                                </bcgogo:hasPermission>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${order_type_condition_sale_return_order}">
                                <label class="label_check chk_off" id="saleReturnLabel"><input type="checkbox" name="orderType" checked="checked" value="SALE_RETURN"/>销售退货单</label>
                            </c:when>
                            <c:otherwise>
                                <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
                                    <input type="hidden" name="orderType" origValue="SALE_RETURN"/>
                                </bcgogo:hasPermission>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${order_type_condition_buy_card_order}">
                                <label class="label_check chk_off" id="memberLabel"><input type="checkbox" name="orderType" checked="checked" value="MEMBER_BUY_CARD"/>会员购卡续卡</label>
                            </c:when>
                            <c:otherwise>
                                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                                    <input type="hidden" name="orderType" origValue="MEMBER_RETURN_CARD"/>
                                </bcgogo:hasPermission>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${order_type_condition_return_card}">
                                <label class="label_check chk_off" id="memberReturnLabel"><input type="checkbox" name="orderType" checked="checked" value="MEMBER_RETURN_CARD"/>会员退卡</label>
                            </c:when>
                            <c:otherwise>
                                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                                    <input type="hidden" name="orderType" origValue="MEMBER_RETURN_CARD"/>
                                </bcgogo:hasPermission>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                    <c:if test="${order_other_condition_order_status_repeal}">
                        <div style="margin-right: 10px;float: right; margin-top: -2px; height:25px;"> 是否包含作废：
                            <select name="orderStatusRepeal">
                                <option value="NO" selected="selected">否</option>
                                <option value="YES">是</option>
                            </select>
                        </div>
                    </c:if>
                </div>
                <div class="clear"></div>
            </c:if>
        </bcgogo:orderPageConfigurationParam>
    </bcgogo:orderPageConfigurationParam>
    <%--客户--%>
     <bcgogo:orderPageConfigurationParam orderGroupName="order_customer_supplier_condition" orderNameAndResource="name;contact;mobile;
         [license_number,WEB.VERSION.VEHICLE_CONSTRUCTION];[member_type,WEB.VERSION.VEHICLE_CONSTRUCTION];[member_card_no,WEB.VERSION.VEHICLE_CONSTRUCTION];
         [payPerProject,WEB.VERSION.VEHICLE_CONSTRUCTION]">
         <c:if test="${!order_customer_supplier_condition_has_none_of_the_order_group}">
            <div class="first_tab" id="customerInfo">
                <c:if test="${order_customer_supplier_condition_name}"><input type="text" style="width:120px;" class="customerOrSupplierName textbox" autocomplete="off" initialValue="供应商/客户" pagetype="inquiryCenter" value="${inquiryCenterInitialDTO.customerOrSupplier!=null?inquiryCenterInitialDTO.customerOrSupplier:'供应商/客户'}" name="customerOrSupplierName" id="customer_supplierInfoText"></c:if>
                <c:if test="${order_customer_supplier_condition_contact}"><input type="text" style="width:55px;" class="contact textbox" initialValue="联系人" value="${inquiryCenterInitialDTO.contact!=null?inquiryCenterInitialDTO.contact:'联系人'}" name="contact" id="contact"></c:if>
                <c:if test="${order_customer_supplier_condition_mobile}"><input type="text" style="width:85px;" class="mobile textbox" initialValue="手机" value="${inquiryCenterInitialDTO.mobile!=null?inquiryCenterInitialDTO.mobile:'手机'}" id="mobile" name="contactNum"> </c:if>
                <c:if test="${order_customer_supplier_condition_license_number}">
                    <input type="hidden" name="vehicleId" id="vehicleId" value="${inquiryCenterInitialDTO.vehicleId!=null?inquiryCenterInitialDTO.vehicleId:''}">
                    <input type="text" class="vehicle textbox" autocomplete="off" style="width:80px;" initialValue="车牌号" value="${inquiryCenterInitialDTO.vehicleNumber!=null?inquiryCenterInitialDTO.vehicleNumber:'车牌号'}" name="vehicle" id="vehicleNumber">
                </c:if>
                <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
                    <c:if test="${order_customer_supplier_condition_member_type}"><input type="text"  style="width:90px;" class="searchDetail member textbox" value="会员类型" pagetype="inquiryCenter"  name="memberType" id="memberCardType" initialValue="会员类型" >  </c:if>
                    <c:if test="${order_customer_supplier_condition_member_card_no}"><input type="text"  style="width:90px;" class="searchDetail member memberNo textbox"  value="会员卡号" autocomplete="off"  pagetype="inquiryCenter" name="accountMemberNo" id="customerMemberNo" initialValue="会员卡号" >   </c:if>
                    <c:if test="${order_customer_supplier_condition_payPerProject}"><input type="text" style="width:100px;" class="searchDetail member textbox" value="计次收费项目" id="payPerProject" name="payPerProject" initialValue="计次收费项目" ></c:if>
                </bcgogo:hasPermission>
            </div>
         </c:if>
     </bcgogo:orderPageConfigurationParam><%--施工类型--%>

    <bcgogo:orderPageConfigurationParam orderGroupName="order_type_condition" orderNameAndResource="order_number">
        <bcgogo:orderPageConfigurationParam orderGroupName="order_operator_condition" orderNameAndResource="[service_worker,WEB.VERSION.VEHICLE_CONSTRUCTION];salesman">
        <c:if test="${!order_operator_condition_has_none_of_the_order_group|| order_type_condition_order_number}">
            <div class="shigong_tab" id="shigong">
                <c:if test="${order_operator_condition_service_worker}"><input type="text"  style="width:55px;" autocomplete="off" class="repair searchDetail textbox" id="serviceWorker" value="施工人" initialValue="施工人" name="serviceWorker"></c:if>
                <c:if test="${order_operator_condition_salesman}"><input type="text" value="${inquiryCenterInitialDTO.salesman!=null?inquiryCenterInitialDTO.salesman:'销售人'}" autocomplete="off" initialValue="销售人" id="saler" class="sale searchDetail textbox" style="width:55px;" name="salesman"></c:if>
                <c:if test="${order_type_condition_order_number}"><input type="text" value="${inquiryCenterInitialDTO.receiptNo!=null?inquiryCenterInitialDTO.receiptNo:'单据编号'}" autocomplete="off" initialValue="单据编号" id="receiptNo" class="textbox" style="width:75px;" name="receiptNo"></c:if>
            </div>
        </c:if>
    </bcgogo:orderPageConfigurationParam>
    </bcgogo:orderPageConfigurationParam>
    <%--商品--%>

    <bcgogo:orderPageConfigurationParam orderGroupName="order_product_condition" orderNameAndResource="product_info;commodity_code">
        <c:if test="${!order_product_condition_has_none_of_the_order_group}">
            <div class="forth_tab" id="productProperty">
                <c:if test="${order_product_condition_product_info}">
                    <input type="text" class="textbox J-productSuggestion" id="searchWord" name="searchWord" searchField="product_info" value="品名/品牌/规格/型号/适用车辆" initialValue="品名/品牌/规格/型号/适用车辆" style="width:200px;"/>
                    <input type="text" class="textbox J-productSuggestion" id="productName" name="productName" searchField="product_name" value="${inquiryCenterInitialDTO.productName!=null?inquiryCenterInitialDTO.productName:'品名'}" initialValue="品名" style="width:90px;"/>
                    <input type="text" class="textbox J-productSuggestion" id="productBrand" name="productBrand" searchField="product_brand" value="${inquiryCenterInitialDTO.productBrand!=null?inquiryCenterInitialDTO.productBrand:'品牌/产地'}" initialValue="品牌/产地" style="width:90px;"/>
                    <input type="text" class="textbox J-productSuggestion" id="productSpec" name="productSpec" searchField="product_spec" value="${inquiryCenterInitialDTO.productSpec!=null?inquiryCenterInitialDTO.productSpec:'规格'}" initialValue="规格" style="width:90px;"/>
                    <input type="text" class="textbox J-productSuggestion" id="productModel" name="productModel" searchField="product_model" value="${inquiryCenterInitialDTO.productModel!=null?inquiryCenterInitialDTO.productModel:'型号'}" initialValue="型号" style="width:90px;"/>
                    <input type="text" class="textbox J-productSuggestion" id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand" value="${inquiryCenterInitialDTO.productVehicleBrand!=null?inquiryCenterInitialDTO.productVehicleBrand:'车辆品牌'}" initialValue="车辆品牌" style="width:90px;"/>
                    <input type="text" class="textbox J-productSuggestion" id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model" value="${inquiryCenterInitialDTO.productVehicleModel!=null?inquiryCenterInitialDTO.productVehicleModel:'车型'}" initialValue="车型" style="width:90px;"/>
                </c:if>
                <c:if test="${order_product_condition_commodity_code}">
                    <input type="text" class="textbox J-productSuggestion" id="commodityCode" name="commodityCode" searchField="commodity_code" value="${inquiryCenterInitialDTO.commodityCode!=null?inquiryCenterInitialDTO.commodityCode:'商品编号'}" initialValue="商品编号" style="text-transform: uppercase;width:90px;"/>
                </c:if>
            </c:if>
        </div>
    </bcgogo:orderPageConfigurationParam>
    <bcgogo:orderPageConfigurationParam orderGroupName="order_pay_method_condition" orderNameAndResource="cash;bankCard;cheque;deposit;
        [member_balance_pay,WEB.VERSION.VEHICLE_CONSTRUCTION];not_paid;statement_account;expense_amount;[coupon,WEB.VERSION.VEHICLE_CONSTRUCTION]">
            <bcgogo:orderPageConfigurationParam orderGroupName="order_operator_condition" orderNameAndResource="operator">
                <%--结算方式--%>
                <c:if test="${!order_pay_method_condition_has_none_of_the_order_group||order_operator_condition_operator}">
                    <div class="tab_dow searchDetail"  id="settlementMethod">
                    <c:if test="${!order_pay_method_condition_has_none_of_the_order_group}">
                        <strong>结算方式：</strong>
                        <div class="chk_choose">
                            <c:if test="${order_pay_method_condition_cash}">
                                <label class="label_check chk_off"><input type="checkbox" value="CASH"  id="cash" name="payMethod"/>现金</label>
                            </c:if>
                            <c:if test="${order_pay_method_condition_bankCard}">
                                <label class="label_check chk_off"><input type="checkbox" value="BANK_CARD"  id="bankCard" name="payMethod"/>银行卡</label>
                            </c:if>
                            <c:if test="${order_pay_method_condition_cheque}">
                                <label class="label_check chk_off"><input type="checkbox" value="CHEQUE" id="cheque" name="payMethod"/>支票</label>
                            </c:if>
                            <c:if test="${order_pay_method_condition_customer_deposit}">
                                <label class="label_check chk_off"><input type="checkbox" value="CUSTOMER_DEPOSIT" id="customerDeposit" name="payMethod"/>预收款</label>
                            </c:if>
                            <c:if test="${order_pay_method_condition_deposit}">
                                <label class="label_check chk_off"><input type="checkbox" value="DEPOSIT" id="deposit" name="payMethod"/>预付款</label>
                            </c:if>
                            <c:if test="${order_pay_method_condition_member_balance_pay}">
                                <label class="label_check chk_off"><input type="checkbox" value="MEMBER_BALANCE_PAY" id="memberBalancePay" name="payMethod"/>会员消费</label>
                            </c:if>
                            <c:if test="${order_pay_method_condition_not_paid}">
                                <label class="label_check chk_off"><input type="checkbox" value="true" id="notPaid" name="notPaid"/>欠款</label>
                            </c:if>
                            <c:if test="${order_pay_method_condition_statement_account}">
                                <label class="label_check chk_off"><input type="checkbox" value="STATEMENT_ACCOUNT" id="statement_account" name="payMethod"/>对账</label>
                            </c:if>
                            <c:if test="${order_pay_method_condition_coupon}">
                                <label class="label_check chk_off"><input type="checkbox" value="COUPON" id="coupon" name="payMethod"/>消费券</label>
                                <div>
                                    <input type="text" placeholder='消费券类型' id="couponType" name="couponType" class="textbox" style="width:60px;float:left;"/>
                                </div>
                            </c:if>


                        </div>
                    </c:if>
                    <c:if test="${order_pay_method_condition_expense_amount||order_operator_condition_operator}">
                        <div class="td_show">
                            <c:if test="${order_pay_method_condition_expense_amount}">
                            <strong>消费金额：</strong>
                                <input type="text" class="mon_search textbox" name="amountLower" id="amountLower"/>~<input type="text" class="mon_search textbox" name="amountUpper" id="amountUpper"/> 元
                            </c:if>
                            <c:if test="${order_operator_condition_operator}">
                                <input type="text" value="操作人" initialValue="操作人" name="operator" id="operator" style="width:55px; margin-left:20px;color: #666666;" class="textbox">
                            </c:if>
                        </div>
                    </c:if>
                </div>
                </c:if>
        </bcgogo:orderPageConfigurationParam>
    </bcgogo:orderPageConfigurationParam>
    <div class="shigong_tab btnSearch">
        <input type="button" value="搜索" class="btn_condition" onfocus="this.blur();" id="btnSearch"/>
    </div>
</form>
<div class="i_height"></div>
<div class="tabTitle statisticalData">

<bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
  <div class="statistics">采购(<span class="qian_blue" id="counts_order_total_amount_order_type_purchase">0</span>笔<span class="qian_green" id="amounts_debt_and_settled_amount_order_type_purchase">0.0</span>元)</div>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
 <div class="statistics">入库(<span class="qian_blue" id="counts_order_total_amount_order_type_inventory">0</span>笔<span class="qian_green" id="amounts_debt_and_settled_amount_order_type_inventory">0.0</span>元)</div>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
 <div class="statistics">销售(<span class="qian_blue" id="counts_order_total_amount_order_type_sale">0</span>笔<span class="qian_red" id="amounts_debt_and_settled_amount_order_type_sale">0.0</span>元)</div>
    </bcgogo:hasPermission>

<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
    <div class="statistics">施工(<span class="qian_blue" id="counts_order_total_amount_order_type_repair">0</span>笔<span class="qian_red" id="amounts_debt_and_settled_amount_order_type_repair">0.0</span>元)</div>
</bcgogo:hasPermission>
<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
    <div class="statistics">洗车(<span class="qian_blue" id="counts_order_total_amount_order_type_wash_beauty">0</span>笔<span class="qian_red statistics" id="amounts_debt_and_settled_amount_order_type_wash_beauty">0.0</span>元)</div>

</bcgogo:hasPermission>
<bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN||WEB.TXN.PURCHASE_MANAGE.RETURN" resourceType="menu">
    <div class="statistics" style="width:150px;">入库退货(<span class="qian_blue" id="counts_order_total_amount_order_type_return">0</span>笔<span class="qian_red" id="amounts_debt_and_settled_amount_order_type_return">0.0</span>元)</div>
</bcgogo:hasPermission>
<bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN||WEB.TXN.PURCHASE_MANAGE.RETURN" resourceType="menu">
    <div class="statistics" style="width:150px;">销售退货(<span class="qian_blue" id="counts_order_total_amount_order_type_sale_return">0</span>笔<span class="qian_red" id="amounts_debt_and_settled_amount_order_type_sale_return">0.0</span>元)</div>
</bcgogo:hasPermission>
<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
  <div class="statistics">购卡(<span class="qian_blue" id="counts_order_total_amount_order_type_member_buy_card">0</span>笔<span class="qian_red" id="amounts_debt_and_settled_amount_order_type_member_buy_card">0.0</span>元)</div>
  <div class="statistics">退卡(<span class="qian_blue" id="counts_order_total_amount_order_type_return_card">0</span>笔<span class="qian_green" id="amounts_debt_and_settled_amount_order_type_member_return_card">0.0</span>元)</div>
</bcgogo:hasPermission>

<br>
<div id="itemsArea">
    <div class="cus_titleBody" style="width: 1000px;">
        <div style="float:left;width:34px;">No</div>
         <div style="float:left;width:70px;">
             <input type="button" class="ascending sort fl" id="receipt_no_sort" style="float: right;background-position: -4px 2px;" hidefocus="true" onclick="switchStyle(this,'receipt_no')"/>
            单据号
         </div>
        <div style="float:left;width:87px;">客户/供应商</div>
        <div style="float:left;width:107px;">
            <input type="button" class="ascending sort fl" id="created_time_sort" style="float: right;background-position: -4px 2px;" hidefocus="true" onclick="switchStyle(this,'created_time')"/>
            日&nbsp;&nbsp;&nbsp;期
        </div>
        <div style="float:left;width:87px;">车牌号</div>
        <div style="float:left;width:83px;">单据类型</div>
        <div style="float:left;width:349px;">施工内容/材料</div>
        <div style="float:left;width:83px;">金额</div>
        <div style="float:left;width:82px;">状态</div>
    </div>
    <div id="tableDiv" style="overflow-x:hidden;overflow-y:auto; height:100px;width: 1000px;">
        <table cellpadding="0" cellspacing="0" class="table2" id="tabList" style="table-layout:fixed;">
            <col width="30">
            <col width="75">
            <col width="80">
            <col width="100">
            <col width="80">
            <col width="80"/>
            <col width="340">
            <col width="80">
            <col width="70">
        </table>
    </div>
    <%--采购--%>
        <div class="i_history up_storage orderItems" id="purchase" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">采购单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:160px;" id="purchaseId">采购单号：<span></span></div>
                    <div style="width:200px;" id="purchaseSupplierName">供应商：<span></span></div>
                    <div style="width:110px;" id="purchaseSupplierContact">联系人：<span></span></div>
                    <div style="width:110px;" id="purchaseStatus">状态：<span></span></div>
                    <div style="width:140px;" id="purchaseDate">采购日期：<span></span></div>
                    <input type="hidden" id="purchaseSupplierId">
                </div>
                <table cellpadding="0" cellspacing="0" class="table2 inquiryItems" id="purchaseHistory">
                    <col width="60">
                    <col width="90"/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <tr class="title_his">
                        <td style="border-left:none;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td>车辆品牌</td>
                        <td>车型</td>
                        <td>单价</td>
                        <td>数量</td>
                        <td>单位</td>
                        <td style="border-right:none;">小计</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li style="line-height: 20px;" id="purchaseOrderTotalAmount">合计：<span>0.0</span>元</li>
                    <li style="line-height: 20px;" id="purchaseOrderMemo">备注：<span>无</span></li>
                    <li class="table_btn orderHandle">
                        <input name="purchaseOrderId" type="hidden" id="purchaseOrderId" class="orderHandleId"/>
                        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.PURCHASE.RETURN">
                            <input type="button" value="退货" id="purchaseOrderReturn" class="i_operate" style="display: none"
                                   handleType="purchase" url="onlineReturn.do?method=onlinePurchaseReturnEdit&purchaseOrderId="
                                   onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <input type="button" value="查看" class="i_operate" url="RFbuy.do?method=show&id=" onfocus="this.blur();"/>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE.CANCEL">
                            <input type="button" value="作废" id="repealPurchaseOrder" class="i_operate" handleType="purchase" url="RFbuy.do?method=show&operation=REPEAL&id=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE.COPY">
                            <input type="button" id="purchase_order_copy" style="display: none;" value="复制" class="i_operate"  handleType="purchase"    url="RFbuy.do?method=show&operation=COPY&id=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE.PRINT">
                            <input type="button" value="打印" class="i_operate" url="RFbuy.do?method=print&id=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                    </li>
                </ul>
                    </div>
                </div>
        <%--退货--%>
        <div class="i_history up_storage orderItems" id="return" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">入库退货单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:180px;" id="returnId">入库退货单号：<span></span></div>
                    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                        <div style="width:160px;" id="returnStorehouseName">仓库：<span></span></div>
                    </bcgogo:hasPermission>
                    <div style="width:200px;" id="returnSupplierName">供应商：<span></span></div>
                    <div style="width:110px;" id="returnSupplierContact">联系人：<span></span></div>
                    <%--<div style="width:110px;" id="returnStatus">状态：<span></span></div>--%>
                    <div style="width:140px;" id="returnDate">入库日期：<span></span></div>
                    <input type="hidden" id="returnSupplierId">
                </div>
                <table cellpadding="0" cellspacing="0" class="table2 inquiryItems" id="returnHistory">
                    <col width="60">
                    <col width="90"/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <tr class="title_his">
                        <td style="border-left:none;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                         <td>车辆品牌</td>
                        <td>车型</td>
                        <td>单价</td>
                        <td>数量</td>
                        <td>单位</td>
                        <td style="border-right:none;">小计</td>
                    </tr>
                </table>
                <div class="height clear"></div>

                <ul class="tableInfo" style="list-style: none;">

                    <li id="returnOrderMemo">备注：<span>无</span></li>
                    <li>
                        <div class="almost" id="goodsReturnOrderTotalAmount" >合计：<span>0.0</span>元</div>
                        <div class="almost" id="goodsReturnOrderSettledAmount" style="margin-left: 30px">实收：<span>0.0</span>元</div>
                        <div class="almost" id="goodsReturnOrderDiscountAmount" style="margin-left: 30px">优惠：<span>0.0</span>元</div>
                        <div class="almost" id="goodsReturnOrderDebtAmount" style="margin-left: 30px">欠款：<span>0.0</span>元</div>
                    </li>

                    <li class="table_btn orderHandle">
                        <input name="purchaseReturnId" type="hidden" id="purchaseReturnId" class="orderHandleId"/>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN.PRINT">
                            <input type="button" value="打印" class="i_operate" url="goodsReturn.do?method=printReturnStorageOrder&purchaseReturnId=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN.CANCEL">
                            <input type="button" value="作废" class="i_operate" handleType="return" url="goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <%--<input type="button" value="复制" class="i_operate" url="goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=" onfocus="this.blur();"/>--%>
                        <input type="button" value="查看" class="i_operate" url="goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=" onfocus="this.blur();"/>

                    </li>
                </ul>
                    </div>
                </div>
    <%--销售退货--%>
    <div class="i_history up_storage orderItems" id="sale_return" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
        <div class="i_arrow"></div>
        <div class="i_upLeft"></div>
        <div class="i_upCenter">
            <div class="i_note">销售退货单</div>
        </div>
        <div class="i_upRight"></div>
        <div class="i_upBody">
            <div class="more_his">
                <div style="width:180px;" id="salesReturnOrderNo">销售退货单号：<span></span></div>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                    <div style="width:160px;" id="salesReturnStorehouseName">仓库：<span></span></div>
                </bcgogo:hasPermission>
                <div style="width:200px;" id="salesReturnCustomerName">客户：<span></span></div>
                <div style="width:110px;" id="salesReturnCustomerContact">联系人：<span></span></div>
                <%--<div style="width:110px;" id="returnStatus">状态：<span></span></div>--%>
                <div style="width:140px;" id="salesReturnDate">退货日期：<span></span></div>
                <input type="hidden" id="salesReturnCustomerId">
            </div>
            <table cellpadding="0" cellspacing="0" class="table2 inquiryItems" id="salesReturnHistory" style="border:1px solid #bbbbbb;">
                <col width="60">
                <col width="90"/>
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="40">
                <col width="40">
                <col width="40">
                <col width="40">
                <tr class="title_his">
                    <td style="border-left:none;">商品编号</td>
                    <td>品名</td>
                    <td>品牌/产地</td>
                    <td>规格</td>
                    <td>型号</td>
                    <td>车辆品牌</td>
                    <td>车型</td>
                    <td>单价</td>
                    <td>数量</td>
                    <td>单位</td>
                    <td style="border-right:none;">小计</td>
                </tr>
            </table>
            <div class="height clear"></div>
            <ul class="tableInfo" style="list-style: none;">
                <li id="salesReturnOrderMemo">备注：<span>无</span></li>
                <li>
                    <div class="almost" id="salesReturnOrderTotalAmount">合计：<span>0.0</span>元</div>
                    <div class="almost" id="salesReturnOrderSettledAmount" style="margin-left: 30px">实收：<span>0.0</span>元</div>
                    <div class="almost" id="salesReturnOrderDiscountAmount" style="margin-left: 30px">优惠：<span>0.0</span>元</div>
                    <div class="almost" id="salesReturnOrderDebtAmount" style="margin-left: 30px">欠款：<span>0.0</span>元</div>
                </li>
                <li class="table_btn orderHandle">

                    <input name="salesReturnId" type="hidden" id="salesReturnId" class="orderHandleId"/>
                    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN.PRINT">
                        <input type="button" value="打印" class="i_operate" url="salesReturn.do?method=printSalesReturnOrder&salesReturnOrderId=" onfocus="this.blur();"/>
                    </bcgogo:hasPermission>
                    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN.REPEAL">
                    <input type="button" value="作废" class="i_operate" handleType="sale_return" url="salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=" onfocus="this.blur();"/>
                    </bcgogo:hasPermission>
                    <%--<input type="button" value="复制" class="i_operate" url="salesReturn.do?method=copyReturnStorage&purchaseReturnId=" onfocus="this.blur();"/>--%>
                    <input type="button" value="查看" class="i_operate" url="salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=" onfocus="this.blur();"/>

                </li>
            </ul>
        </div>
    </div>
        <%--销售--%>
        <div class="i_history up_storage orderItems" id="sale" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">销售单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:160px;" id="saleId">销售单号：<span></span></div>
                    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                        <div style="width:160px;" id="saleStorehouseName">仓库：<span></span></div>
                    </bcgogo:hasPermission>
                    <div style="width:200px;" id="saleCustomerName">客户：<span></span></div>
                    <div style="width:110px;" id="saleCustomerContact">联系人：<span></span></div>
                    <div style="width:110px;" id="saleStatus">状态：<span></span></div>
                    <div style="width:140px;" id="saleDate">销售单日期：<span></span></div>
                    <input type="hidden" id="saleCustomerId">
                </div>
                <table cellpadding="0" cellspacing="0" class="table2 inquiryItems" id="saleHistory">
                    <col width="60">
                    <col width="90"/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <tr class="title_his">
                        <td style="border-left:none;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td>车辆品牌</td>
                        <td>车型</td>
                        <td>单价</td>
                        <td>数量</td>
                        <td>单位</td>
                        <td style="border-right:none;">小计</td>
                    </tr>
                </table>
                <div class="height clear"></div>

                <table cellpadding="0" cellspacing="0" class="table2 inquiryItems" id="saleOtherIncomeHistory">
                    <col width="250">
                    <col width="150"/>
                    <col width="360">
                    <tr class="title_his">
                        <td style="border-left:none;">其他费用</td>
                        <td>价格</td>
                        <td style="border-right:none;" >备注</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li id="saleOrderMemo">备注：<span>无</span></li>
                    <li>
                    <div class="almost" id="saleOrderTotalAmount">合计：<span>0.0</span>元</div>
                    <div class="almost" id="saleOrderSettledAmount" style="margin-left: 30px">实收：<span>0.0</span>元</div>
                    <div class="almost" id="saleOrderDebt" style="margin-left: 30px">欠款：<span>0.0</span>元</div>
                    <div class="almost" id="saleOrderMemberDiscountRatio" style="margin-left: 30px">会员打折：<span>0.0</span>折</div>
                    </li>
                    <li class="table_btn orderHandle">
                        <input type="hidden" id="saleOrderDebtId">
                        <input type="hidden" id="saleOrderReceivableId">
                        <input name="saleOrderId" type="hidden" id="saleOrderId" class="orderHandleId"/>
                        <input type="button" value="查看" class="i_operate" url="sale.do?method=toSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=" onfocus="this.blur();"/>
                        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE.CANCEL">
                            <input type="button" value="作废" class="i_operate" handleType="sale"  url="sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
                            <input type="button" value="退货" class="i_operate" id="salesReturnBtn"/>
                        </bcgogo:hasPermission>
                        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE.COPY">
                            <input type="button" value="复制" class="i_operate" url="sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <bcgogo:hasPermission permissions="WEB.TXN.SALES_MANAGE.PRINT">
                            <input type="button" value="打印" class="i_operate" handleType="sale" url="sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                    </li>
                </ul>
                    </div>
                </div>
        <%--入库--%>
        <div class="i_history up_storage orderItems" id="inventory" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">入库单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:160px;" id="inventoryId">入库单号：<span></span></div>
                    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                        <div style="width:160px;" id="inventoryStorehouseName">仓库：<span></span></div>
                    </bcgogo:hasPermission>
                    <div style="width:200px;" id="inventorySupplierName">供应商：<span></span></div>
                    <div style="width:110px;" id="inventorySupplierContact">联系人：<span></span></div>
                    <div style="width:110px;" id="inventoryStatus">状态：<span></span></div>
                    <div style="width:140px;" id="inventoryDate">入库日期：<span></span></div>
                    <input type="hidden" id="inventorySupplierId">
                </div>
                <table cellpadding="0" cellspacing="0" class="table2 inquiryItems" id="inventoryHistory">
                    <col width="60">
                    <col width="90"/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <tr class="title_his">
                        <td style="border-left:none;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td>车辆品牌</td>
                        <td>车型</td>
                        <td>单价</td>
                        <td>数量</td>
                        <td>单位</td>
                        <td style="border-right:none;">小计</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li id="inventoryOrderMemo">备注：<span>无</span></li>
                    <li class="almost">
                        <div class="almost" style="padding-right:20px;" id="inventoryOrderTotalAmount">合计：<span>0.0</span>元</div>
                        <div class="almost" style="padding-right:20px;" id="inventoryOrderSettledAmount">实收：<span>0.0</span>元</div>
                        <div class="almost" style="padding-right:20px;" id="inventoryOrderDebt">欠款：<span>0.0</span>元</div>
                    </li>
                    <li class="table_btn orderHandle">
                        <input name="purchaseInventoryId" type="hidden" id="purchaseInventoryId" class="orderHandleId"/>
                        <input type="button" value="查看" class="i_operate" url="storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=" onfocus="this.blur();"/>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE.CANCEL">
                            <input type="button" value="作废" class="i_operate" handleType="inventory" url="storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE.COPY">
                            <input type="button" value="退货" class="i_operate" id="returnStorageBtn" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE.COPY">
                            <input type="button" value="复制" class="i_operate" url="storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE.PRINT">
                            <input type="button" value="打印" class="i_operate" url="storage.do?method=getPurchaseInventoryToPrint&purchaseInventoryId=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                    </li>
                </ul>
                    </div>
                </div>
        <%--会员--%>
        <div class="i_history up_storage orderItems" id="member_buy_card" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">会员购卡续卡</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:130px;" id="memberName">客户名：<span></span></div>
                    <div style="width:200px;" id="memberNo">会员卡号：<span></span></div>
                    <div style="width:230px;" id="memberStatusStr">购卡/续卡类型：<span></span></div>
                    <div style="width:120px;" id="memberOrderNum"><%--单据号：--%><span></span></div>
                    <div style="width:200px;" id="memberOrderOriginal">储值原有金额：<span>0.0</span>元</div>
                    <div style="width:200px;" id="memberOrderWorth">储值新增金额：<span>0.0</span>元</div>
                    <div style="width:180px;" id="memberBalance">储值余额：<span>0.0</span>元</div>
                </div>
                <table cellpadding="0" cellspacing="0" class="table2" id="memberHistory">
                    <col/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="120">
                    <col width="100">
                    <tr class="title_his">
                        <td style="border-left:none;">服务项目</td>
                        <td>原有次数</td>
                        <td>新增</td>
                        <td>剩余次数</td>
                        <td>限消费车牌</td>
                        <td style="border-right:none;">失效日期</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li>
                        <div style="padding-right:20px; float:left;" id="memberTotal">合计：<span>0.0</span>元</div>
                        <div style="padding-right:20px; float:left;" id="memberSettledAmount">实收：<span>0.0</span>元</div>
                        欠款：<span id="memberDebt">0.0</span>元
                    </li>
                     <li style="line-height: 20px;float: left" id="memberMemo">备注：<span>无</span></li>
                </ul>
                    </div>
                </div>
        <%--会员--%>
        <div class="i_history up_storage orderItems" id="member_return_card" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">会员退卡</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:180px;" id="returnMemberName">客户名：<span></span></div>
                    <div style="width:180px;" id="returnMemberNo">会员卡号：<span></span></div>
                    <div style="width:180px;" id="returnMemberType">退卡类型：<span></span></div>
                    <div style="width:150px;" id="returnMemberOrderNum">单据号：<span></span></div>
                    <div style="width:180px;" id="returnMemberLastBuyTotal">上次购卡金额：<span>0.0</span>元</div>
                    <div style="width:180px;" id="returnMemberLastBuyDate">上次购卡日期：<span></span></div>
                    <div style="width:180px;" id="returnMemberLastRecharge">上次储值金额：<span>0.0</span>元</div>
                    <div style="width:180px;" id="returnMemberBalance">储值余额：<span>0.0</span>元</div>
                </div>
                <table cellpadding="0" cellspacing="0" class="table2" id="returnMemberHistory">
                    <col/>
                    <col width="90">
                    <col width="120">
                    <col width="100">
                    <tr class="title_his">
                        <td style="border-left:none;">服务项目</td>
                        <td>上次购买次数</td>
                        <td>剩余次数</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <div class="tableInfo">
                    <div class="almost">
                        <div style="padding-right:20px; float:left;" id="returnMemberTotal">合计：<span>0.0</span>元</div>
                        <div style="padding-right:20px; float:left;" id="returnMemberSettledAmount">实付：<span>0.0</span>元</div>
                    </div>
                </div>
            </div>
        </div>
        <%--洗车美容--%>
        <div class="i_history up_storage orderItems" id="wash_beauty" style="display:none;left:90px;width: 770px; position:relative; z-index:10; border-bottom:1px solid #bbbbbb">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">洗车美容单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:110px;" id="washCustomerName">客户：<span></span></div>
                    <div style="width:190px;" id="washMemberCard">会员（卡）号：<span></span></div>
                    <div style="width:140px;" id="washLicence">车牌号：<span></span></div>
                    <div style="width:180px;" id="washDate">消费时间：<span></span></div>
                </div>
                <table cellpadding="0" cellspacing="0" class="table2" id="washHistory">
                    <col width="60">
                    <col/>
                    <col width="120">
                    <col width="200">
                    <tr class="title_his">
                        <td style="border-left:none;">序号</td>
                        <td>施工内容</td>
                        <td>施工人</td>
                        <td style="border-right:none;">金额</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li id="washMemo">备注：<span>无</span></li>
                    <li>
                        <div style="padding-right:20px; float:left;" id="washTotal">合计：<span>0.0</span>元</div>
                        <div style="padding-right:20px; float:left;" id="washSettledAmount">实收：<span>0.0</span>元</div>
                        欠款：<span id="washDebt">0.0</span>元
                    </li>
                    <li class="table_btn orderHandle">
                        <input name="washOrderId" type="hidden" id="washOrderId" class="orderHandleId"/>
												<input type="button" value="查看" class="i_operate" handleType="wash"
															 url="washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" onfocus="this.blur();"/>
                        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.CACEL">
                            <input type="button" value="作废" class="i_operate" handleType="wash"
                                   url="washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId="
                                   onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <%--<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.COPY">--%>
                            <input type="button" value="复制" class="i_operate" handleType="wash"
                                   url="washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" onfocus="this.blur();"/>
                        <%--</bcgogo:hasPermission>--%>
                        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.PRINT">
                            <input type="button" value="打印" class="i_operate" handleType="wash"
                                   url="washBeauty.do?method=printWashBeautyTicket&orderId=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                    </li>
                </ul>
                    </div>
                </div>
        <%--施工单--%>
        <div class="i_history up_storage orderItems" id="repair" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note" id="div_drag">施工单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:110px;" id="repairCustomerName">客户：<span></span></div>
                    <div style="width:190px;" id="repairMemberNo">会员（卡）号：<span></span></div>
                    <div style="width:140px;" id="repairLicenceNo">车牌号：<span></span></div>
                    <div style="width:120px;" id="repairOrderStatus">状态：<span></span></div>
                    <div style="width:180px;" id="startDateStr">进厂时间：<span></span></div>
                </div>
                <div class="more_his">
                    <div class="inquiryTitle">施工单</div>
                    <div class="inquiryTitle" id="repairServiceWorker">施工人：<span></span></div>
                </div>
                <table cellpadding="0" cellspacing="0" class="table2" id="repairServiceHistory">
                    <col width="60">
                    <col/>
                    <col width="120">
                    <col width="200">
                    <tr class="title_his">
                        <td style="border-left:none;">序号</td>
                        <td>施工内容</td>
                        <td>工时</td>
                        <td style="border-right:none;">备注</td>
                    </tr>
                </table>
                <div class="clear height"></div>
                <div class="more_his">
                    <div class="inquiryTitle">材料单</div>
                    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                        <div style="width:160px;" id="repairStorehouseName">仓库：<span></span></div>
                    </bcgogo:hasPermission>
                    <div class="inquiryTitle" id="repairProductSalers">销售人：<span></span></div>
                </div>
                <table cellpadding="0" cellspacing="0" class="table2" id="repairMaterialHistory">
                    <col width="60">
                    <col/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="70">
                    <col width="60">
                    <col width="40">
                    <col width="70">
                    <tr class="title_his">
                        <td style="border-left:none;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td>单价</td>
                        <td>数量</td>
                        <td>单位</td>
                        <td style="border-right:none;">小计</td>
                    </tr>
                </table>
                <div class="height clear"></div>

                <table cellpadding="0" cellspacing="0" class="table2 inquiryItems" id="repairOtherIncomeHistory">
                    <col width="250">
                    <col width="150"/>
                    <col width="360">
                    <tr class="title_his">
                        <td style="border-left:none;">其他费用</td>
                        <td>价格</td>
                        <td style="border-right:none;" >备注</td>
                    </tr>
                </table>

                <ul class="tableInfo" style="list-style: none;">
                    <li style="line-height: 20px;">预计出厂时间：<span id="endDateStr"></span></li>
                    <li style="line-height: 20px;" id="repairMemo">备注：<span>无</span></li>
                    <li  style="line-height: 20px;">
                        <div style="padding-right:20px; float:left;" id="repairTotal">合计：<span>0.0</span>元</div>
                        <div style="padding-right:20px; float:left;" id="repairSettledAmount">实收：<span>0.0</span>元</div>
                        欠款：<span id="repairDebt">0.0</span>元
                    </li>
                    <li class="table_btn orderHandle">
                        <input name="repairOrderId" type="hidden" id="repairOrderId" class="orderHandleId"/>
                        <input type="button" value="查看" class="i_operate" url="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" onfocus="this.blur();"/>
                        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.CANCEL">
                            <input type="button" value="作废" class="i_operate" handleType="repair" url="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                        <%--<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.COPY">--%>
                            <input type="button" value="复制" id="copyInput_div" class="i_operate" onfocus="this.blur();"/>
                        <%--</bcgogo:hasPermission>--%>
                        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.PRINT">
                            <input type="button" value="打印" handleType="repair" class="i_operate"  url="txn.do?method=getRepairOrderToPrint&repairOrderId=" onfocus="this.blur();"/>
                        </bcgogo:hasPermission>
                    </li>
                </ul>
                    </div>
            <div class="i_upBottom">
                <div class="i_upBottomLeft"></div>
                <div class="i_upBottomCenter"></div>
                <div class="i_upBottomRight"></div>
            </div>
        </div>
    </div>
    <div class="height"></div>

    <jsp:include page="/common/pageAJAX.jsp">
        <jsp:param name="url" value="inquiryCenter.do?method=inquiryCenterSearchOrderAction"></jsp:param>
        <jsp:param name="data" value="{startPageNo:1,maxRows:15}"></jsp:param>
        <jsp:param name="jsHandleJson" value="showResponse"></jsp:param>
        <jsp:param name="dynamical" value="inquiryCenter"></jsp:param>
        <jsp:param name="display" value="none"></jsp:param>
    </jsp:include>

    <div class="height"></div>
    <div class="operaBtn">
        <%--<input type="button" value="打印" class="btnPrint" onfocus="this.blur()"/>--%>
        <input type="button" value="关闭" class="btnPrint" id="closeInquiry" onfocus="this.blur()"/>
    </div>
</div>
</div>
</div>
<div class="i_upBottom clear">
    <div class="i_upBottomLeft "></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight "></div>
</div>
</div>

<!--mask-->
<div id="mask" style="display:block;position: absolute;">
</div>
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
    <%--@elvariable id="memberCardTypes" type="java.util.List<String>"--%>
    <c:forEach var="cardType" items='${memberCardTypes}'>
        <div style="overflow:hidden;padding:0 5px 0 5px;" title="${cardType}">${cardType}</div>
    </c:forEach>
</div>
</body>
</html>