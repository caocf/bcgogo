<%@ page import="com.bcgogo.common.WebUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>结算详细</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link rel="stylesheet" type="text/css" href="styles/up1<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
	<link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>

	<link rel="stylesheet" type="text/css" href="styles/setleNew<%=ConfigController.getBuildVersion()%>.css"/>
	<style>
		#ui-datepicker-div, .ui-datepicker {
			font-size: 90%;
		}
	</style>
	<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
	<script type="text/javascript" src="js/extension/json2/json2.js"></script>
	<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/wholesaler/supplierSalesAccount<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript">
		$(document).ready(function () {

			$("#huankuanTime")
				.bind("click", function () {
					$(this).blur();
				})
				.datepicker({
					"numberOfMonths": 1,
					"showButtonPanel": true,
					"changeYear": true,
					"changeMonth": true,
					"yearRange": "c-100:c+100",
					"yearSuffix": ""
				});
		});
	</script>
	<%
		boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
	%>
</head>
<body>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" id="isMemberSwitchOn" value="<%=isMemberSwitchOn%>">

<div class="i_searchBrand i_searchBrand-account i_history">
	<div class="i_arrow"></div>
	<div class="i_upLeft"></div>
	<div class="i_upCenter">
		<div class="i_note" id="div_drag">结算详细</div>
		<div class="i_close" id="div_close"></div>
	</div>
	<div class="i_upRight"></div>
	<div class="i_upBody">

		<form:form action="sale.do?method=saleOrderSettle" modelAttribute="accountInfoDTO"
				   id="supplierSaleAccountForm">

			<input type="hidden" id="orderId" name="orderId" value="${salesOrderDTO.id}">
			<input type="hidden" id="customerId" name="customerId" value="${salesOrderDTO.customerId}">
			<label id="cardsTitle">应收总计：<strong id="orderTotal">${salesOrderDTO.total}</strong>元</label>
			<c:if test="${salesOrderDTO.status=='SALE_DEBT_DONE'}">
				<input type="hidden" id="salesOriginTotal" value="${orderTotal}">
				<input type="hidden" id="payedAmount" value="${payedAmount}">
				<input type="hidden" id="debtId" value="${debtId}">
				<input type="hidden" id="receivableId" value="${receivableId}">
			</c:if>
			<div class="clear"></div>
			<div class="tuihuo_first">
				<table>
					<col width="70px"/>
					<col width="80px"/>
					<col width="20px"/>
					<col width="85px"/>
					<col width="110px"/>
					<col width="20px"/>
					<col width="120px"/>
					<col width="154px"/>
					<col width="120px"/>
					<tr>
						<td rowspan="3" style="padding-left:5px;">实收金额：</td>
						<td rowspan="3"><input type="text" id="settledAmount" name="settledAmount" style="width:100px;" autocomplete="off"/></td>
						<td rowspan="3">元</td>
						<td style="text-align:right; letter-spacing:3px;">现&nbsp;&nbsp;金：</td>
						<td><input type="text" id="cashAmount" name="cashAmount" style="width:100px;" autocomplete="off"/></td>
						<td colspan="3">元</td>
					</tr>
					<tr>
						<td style="text-align:right;letter-spacing:3px;">银&nbsp;&nbsp;联：</td>
						<td><input type="text" style="width:100px;" id="bankAmount" name="bankAmount" value="" autocomplete="off"/></td>
						<td colspan="3">元</td>
					</tr>
					<tr>
						<td style="text-align:right;letter-spacing:3px;">支&nbsp;&nbsp;票：</td>
						<td><input type="text" style="width:100px;" id="bankCheckAmount" name="bankCheckAmount" value="" autocomplete="off"/></td>
						<td>元</td>
						<td colspan="3"><input type="text" style="width:110px; color:#9a9a9a" value="支票号" maxlength="20"
											   id="bankCheckNo" name="bankCheckNo" autocomplete="off"/></td>
					</tr>
					<tr>
						<td style="padding-left:5px;">挂账金额：</td>
						<td><input type="text" id="accountDebtAmount" name="accountDebtAmount" style="width:100px;" autocomplete="off"/></td>
						<td>元</td>
						<td style="text-align:right;">预计还款日期：</td>
						<td colspan="5"><input type="text" id="huankuanTime" name="huankuanTime" class="tab_input"
											   style="width:100px;" autocomplete="off"/></td>
					</tr>
					<tr>
						<td style="padding-left:5px;">优惠金额：</td>
						<td><input type="text" style="width:100px;" id="accountDiscount" name="accountDiscount" autocomplete="off"/></td>
						<td>元</td>
           <td  colspan="2" style="letter-spacing:3px;">（按折扣算优惠金额：&nbsp;</td>
                    <td colspan="2"><input type="text" id="discount" name="discount" style="width:100px;margin-left: -60px" autocomplete="off" initValue="请输入折扣" value="请输入折扣"/>&nbsp;<span id="discountWord" style="display:none">折</span>）</td>
				</table>
			</div>
			<div class="height"></div>
			<div class="ti_btn clear">
				<input type="checkbox" value="true" id="print" name="print"
					   style=" background:none; width:15px; height:15px;margin:9px 0px 0px 250px; margin:7px 0px 0px 250px\9; "/>
				<label style="margin:9px 0px 0px 2px;margin-top:10px\9; display:block; float:left;">打印单据</label>
				<input type="button" class="sure_tui" onclick="checkDate();" id="confirmBtn" value="结算"/>
				<input type="button" id="cancelBtn" value="取消"/>

				<div class="clear"></div>
			</div>
		</form:form>
	</div>

</div>
<div class="clear"></div>


<div class="tab_repay" id="selectBtn"
	 style="display:none;position:absolute; left:250px; top:200px; width:300px;z-index:100">
	<div class="i_arrow"></div>
	<div class="i_upLeft"></div>
	<div class="i_upCenter">
		<div class="i_note" id="div_confirm"></div>
	</div>
	<div class="i_upRight"></div>
	<div class="i_upBody">
		<div class="boxContent" style="float:none; text-align:center; color:#000">
			实收为0，是否挂账或优惠赠送？
		</div>
		<div class="sure" style="float:none; text-align:center;">
			<input type="button" value="挂账" onfocus="this.blur();" onclick="selectDebt();"/>
			<input type="button" value="赠送" onfocus="this.blur();" onclick="selectDiscount();"/>
		</div>
	</div>
	<div class="i_upBottom">
		<div class="i_upBottomLeft"></div>
		<div class="i_upBottomCenter"></div>
		<div class="i_upBottomRight"></div>
	</div>
</div>


<div class="tab_repay" id="inputMobile"
	 style="display:none;position:absolute; left:250px; top:200px; width:300px;z-index:100">
	<div class="i_arrow"></div>
	<div class="i_upLeft"></div>
	<div class="i_upCenter">
		<div class="i_note" id="div_mobile">有欠款请最好填写手机号码</div>
	</div>
	<div class="i_upRight"></div>
	<div class="i_upBody">
		<div class="boxContent" style="float:none; text-align:center; color:#000">
			<input type="text" name="mobile" id="mobile" value="${salesOrderDTO.mobile}">
		</div>
		<div class="sure" style="float:none; text-align:center;">
			<input type="hidden" type="hidden" id="flag" isNoticeMobile="false">
			<input type="button" value="确定" onfocus="this.blur();" onclick="inputMobile();"/>
			<input type="button" value="取消" onfocus="this.blur();" onclick="cancleInputMobile();"/>
		</div>
	</div>
	<div class="i_upBottom">
		<div class="i_upBottomLeft"></div>
		<div class="i_upBottomCenter"></div>
		<div class="i_upBottomRight"></div>
	</div>
</div>
</body>
</html>