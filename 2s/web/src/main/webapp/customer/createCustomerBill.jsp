<%--
  生成客户对账单页面
  Created by IntelliJ IDEA.
  User: LiuWei
  Date: 13-1-8
  Time: 下午3:48
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/views/includes.jsp" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>对账单</title>

	<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
	<%@include file="/WEB-INF/views/header_script.jsp" %>
	<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
	<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript"
			src="js/statementAccount/createCustomerBill<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript"
			src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        if (GLOBAL.Util.getUrlParameter("orderType") == "CUSTOMER_STATEMENT_ACCOUNT") {
            defaultStorage.setItem(storageKey.MenuUid,"WEB.CUSTOMER_MANAGER.BASE");
        } else if (GLOBAL.Util.getUrlParameter("orderType") == "SUPPLIER_STATEMENT_ACCOUNT") {
            defaultStorage.setItem(storageKey.MenuUid,"CUSTOMER_SEARCH_SUPPLIERS");
        }
        defaultStorage.setItem(storageKey.MenuCurrentItem,"对账单");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="title">
</div>
<div class="i_main clear">

	<c:if test="${orderType=='CUSTOMER_STATEMENT_ACCOUNT'}">
        <div class="cusTitle">客户资料</div>
		<div class="titBodys">
			<a class="normal_btn" href="#" onclick="redirectUncleUser('customerBill')">客户详细信息</a>
			<a class="hover_btn" href="#" onclick="redirectCustomerBill('customerBill')">客户对账单</a>
		</div>
	</c:if>
	<c:if test="${orderType=='SUPPLIER_STATEMENT_ACCOUNT'}">
        <div class="cusTitle">供应商资料</div>
		<div class="titBodys">
			<a class="normal_btn" href="#" onclick="redirectUncleUser('supplierBill')">供应商详细信息</a>
			<a class="hover_btn" href="#" onclick="redirectCustomerBill('supplierBill')">供应商对账单</a>

      <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_COMMENT.RECORD">
        <c:if test="${supplierShopId!=null }">
            <a class="normal_btn" href="supplier.do?method=redirectSupplierComment&paramShopId=${supplierShopId}">供应商评价详情</a>
        </c:if>
      </bcgogo:hasPermission>
		</div>
	</c:if>

	<form:form commandName="statementAccountOrderDTO" id="statementOrderAccountForm"
			   action="statementAccount.do?method=settleStatementAccountOrder" method="post"
			   name="thisform">
		<input type="hidden" name="startPageNo" id="startPageNo" value="1">
		<input type="hidden" name="maxRows" id="maxRows" value="15">
		<input type="hidden" name="customerOrSupplierId" id="customerOrSupplierId" value=${customerOrSupplierId}>
		<input type="hidden" name="orderType" id="orderType" value=${orderType}>
		<input type="hidden" name="orderDebtType" id="orderDebtType" value=${orderDebtType}>
		<input type="hidden" name="mobile" id="mobile" value=${mobile}>
		<input type="hidden" name="memberNumber" id="memberNumber" value=${memberNumber}>
		<form:hidden path="cashAmount" value="${statementAccountOrderDTO.cashAmount}"/>
		<form:hidden path="bankAmount" value="${statementAccountOrderDTO.bankAmount}"/>
		<form:hidden path="bankCheckAmount" value="${statementAccountOrderDTO.bankCheckAmount}"/>
		<form:hidden path="bankCheckNo" value="${statementAccountOrderDTO.bankCheckNo}"/>
        <!-- TODO zhuj  -->
		<form:hidden path="discount" value="${statementAccountOrderDTO.discount}"/>
		<form:hidden path="accountMemberNo" value="${statementAccountOrderDTO.accountMemberNo}"/>
		<form:hidden path="accountMemberPassword" value="${statementAccountOrderDTO.accountMemberPassword}"/>
		<form:hidden path="memberAmount" value="${statementAccountOrderDTO.memberAmount}"/>
		<input type="hidden" id="isPrint" value="${statementAccountOrderDTO.print}" name="print">
		<form:hidden path="paymentTimeStr" value="${statementAccountOrderDTO.paymentTimeStr}"/>
		<form:hidden path="settledAmount" value="${statementAccountOrderDTO.settledAmount}"/>
		<form:hidden path="debt" value="${statementAccountOrderDTO.debt}"/>
		<form:hidden path="discount" value="${statementAccountOrderDTO.discount}"/>
		<form:hidden path="jsonStr" value="${statementAccountOrderDTO.jsonStr}"/>
		<form:hidden path="total" value="${statementAccountOrderDTO.total}"/>
		<form:hidden path="startDateStr" value="${statementAccountOrderDTO.startDateStr}"/>
		<form:hidden path="endDateStr" value="${statementAccountOrderDTO.endDateStr}"/>
		<form:hidden path="receiptNo" value="${statementAccountOrderDTO.receiptNo}"/>
		<form:hidden path="depositAmount" value="${statementAccountOrderDTO.depositAmount}"/>
		<form:hidden path="totalReceivable" value="${statementAccountOrderDTO.totalReceivable}"/>
		<form:hidden path="totalPayable" value="${statementAccountOrderDTO.totalPayable}"/>
        <form:hidden path="identity" value="${identity}" />
		<form:checkbox id="sendMemberSms" path="sendMemberSms" style="display:none;"/>
		<div class="titBody">
			<div class="allocation"><h1></h1></div>
			<div class="dui_time clear">
				<label>对账截止时间： </label>

				<div class="selectTime" style="height:19px;">
					<input id="endTimeStr" name="endTimeStr" type="text" readonly="readonly" value="${endTimeStr}"
						   style="height:19px;width:100px;">
				</div>
				<input id="createStatementAccount" class="btn_searchr" style="display: none;" type="button">

				<c:if test="${orderType=='CUSTOMER_STATEMENT_ACCOUNT'}">
					<a href="#" onclick="redirectCustomerBill('customerBill')" class="blue_color">查看更多往期对账</a>
				</c:if>
				<c:if test="${orderType=='SUPPLIER_STATEMENT_ACCOUNT'}">
					<a href="#" onclick="redirectCustomerBill('supplierBill')" class="blue_color">查看更多往期对账</a>
				</c:if>


			</div>

			<div class="shelvesed clear">
				<div class="topTitle">收入单据列表</div>
				<div id="noReceivableList" style="width:480px;height:400px;display: none;text-align:center;">暂无收入单据！</div>
				<div id="receivableDiv" style="width:480px;height:400px;overflow-y:auto;overflow-x:hidden;">
					<table id="receivableList" cellpadding="0" cellspacing="0" class="tabShelvesed duizhang_td"
						   style="width:100%;">
            <colgroup>
                        <col width="25">
                        <col width="65">
                        <col width="80">
                        <col width="80">
                        <col width="55">
                        <col width="55">
                        <col width="55">
                        <col width="55">
            </colgroup>
						<tr class="bg" style="text-align: left;">
              <td style="padding-left:6px;">
                <input class="lblChk" type="checkbox" checked="checked" title="全选">
              </td>
							<td style="padding-left:8px;text-align: left;">日期</td>
							<td style="text-align: left;">类型</td>
							<td style="text-align: left;">单据号</td>
							<td style="text-align: left;">单据金额</td>
							<td style="text-align: left;">实收</td>
							<td style="text-align: left;">优惠</td>
							<td style="text-align: left;">挂账</td>
						</tr>
					</table>
				</div>
				<p class="heji">应收合计：<strong id="totalReceivableSpan" class="red_color">0</strong>元</p>

				<div class="height clear"></div>
			</div>
			<div class="shelvesed shelves">
				<div class="topTitle">支出单据列表</div>
				<div id="noPayableList" style="width:480px;height:400px;display: none;text-align:center;">暂无支出单据！</div>
				<div id="payableDiv" style="width:480px;height:400px;overflow-y:auto;overflow-x:hidden;">
					<table id="payableList" cellpadding="0" cellspacing="0" class="tabShelvesed duizhang_td" style="width:100%;">
						<colgroup>
                        <col width="25">
                        <col width="65">
                        <col width="80">
                        <col width="80">
                        <col width="55">
                        <col width="55">
                        <col width="55">
                        <col width="55">
            </colgroup>
						<tr class="bg">
              <td style="padding-left:6px;">
                <input class="lblChk" type="checkbox" checked="checked" title="全选">
              </td>
							<td style="padding-left:8px;">日期</td>
							<td style="text-align: left;">类型</td>
							<td style="text-align: left;">单据号</td>
							<td style="text-align: left;">单据金额</td>
							<td style="text-align: left;">实付</td>
							<td style="text-align: left;">优惠</td>
							<td style="text-align: left;">挂账</td>
						</tr>
					</table>
				</div>
				<p class="heji">应付合计：<strong id="totalPayableSpan" class="green_color">0</strong>元</p>

				<div class="height clear"></div>
			</div>
			<p class="total_statement_account clear"><label>对账合计：</label><label id="totalDebtText"></label> <strong
				id="totalDebt" class="red_color">0</strong> 元</p>
			<input id="statementAccountBtn" style="margin-left: 450px;display:none;" type="button" value="结算" class="jieCount"/>
		</div>
    <input type="hidden" id="receptNoListStr" name="receptNoListStr"/>
	</form:form>
</div>
<%@ include file="/sms/enterPhone.jsp" %>
<div id="mask" style="display:block;position: absolute;"></div>
<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:180px; top:300px; display:none;"
		allowtransparency="true" width="900px" height="450px" frameborder="0" src="" scrolling="no"></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>

