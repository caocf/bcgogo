<%--
  Created by IntelliJ IDEA.
  User: lw
  Date: 12-9-21
  Time: 下午2:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="java.util.Calendar" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>营业流水</title>
	<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="styles/businessStatistics<%=ConfigController.getBuildVersion()%>.css"/>
	<%@include file="/WEB-INF/views/header_script.jsp" %>
	<script type="text/javascript">
		<bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
		APP_BCGOGO.Permission.Version.VehicleConstruction = ${WEB_VERSION_VEHICLE_CONSTRUCTION};
		APP_BCGOGO.Permission.Version.MemberStoredValue = ${WEB_VERSION_VEHICLE_CONSTRUCTION};
		</bcgogo:permissionParam>
        <bcgogo:permissionParam resourceType="render" permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE">
        APP_BCGOGO.Permission.Version.CustomerDeposit = ${WEB_VERSION_CUSTOMER_DEPOSIT_USE};
		</bcgogo:permissionParam>
	</script>
	<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
	<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/modules/exporting.js"></script>
	<script type="text/javascript" src="js/initHighChart<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/runningStat<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/runningStatDetail<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/incomeExpenditureDetail<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "RUNNING_STAT_GET_RUNNING_STAT");
		<bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
		APP_BCGOGO.Permission.Version.VehicleConstruction =${WEB_VERSION_VEHICLE_CONSTRUCTION};
		</bcgogo:permissionParam>
	</script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
<div class="mainTitles clear">
<jsp:include page="statNavi.jsp">
	<jsp:param name="currPage" value="businessStat"/>
</jsp:include>
<jsp:include page="businessStatNavi.jsp">
	<jsp:param name="currPage" value="runningStat"/>
</jsp:include>
</div>

<div class="num_show liushui clear">
<div class="staticLeft">
	<div class="count_info counts">
		<div class="bus_stock stockAdd">
			<div class="bus_title">
				<div class="bus_left cont_left"></div>
				<div class="bus_center cont_center">
					<div style="float:left;width:80px;">
						项目
					</div>
					<div style="float:left;width:80px;text-align:right;">
						日：<select id="selectDay" style="margin-top:2px;outline:0;"></select>
					</div>
					<div style="float:left;width:90px;text-align:right;">
						月：<select id="selectMonth" style="margin-top:2px;outline:0;"></select>
					</div>
					<div style="float:left;width:120px;text-align:right;">
						年：<select id="selectYear" style="margin-top:2px;outline:0;"></select>
					</div>
				</div>
				<div class="bus_right cont_right"></div>
			</div>
			<input type="hidden" id="dayHid" name="dayHid" value="<%=Calendar.getInstance().get(Calendar.DATE)%>"/>
			<input type="hidden" id="selectDayHid" name="selectDayHid"
				   value="<%=Calendar.getInstance().get(Calendar.DATE)%>"/>
			<input type="hidden" id="monthHid" name="monthHid" value="<%=Calendar.getInstance().get(Calendar.MONTH)+1%>"/>
			<input type="hidden" id="selectMonthHid" name="selectMonthHid"
				   value="<%=Calendar.getInstance().get(Calendar.MONTH)+1%>"/>
			<input type="hidden" id="yearHid" name="yearHid" value="<%=Calendar.getInstance().get(Calendar.YEAR)%>"/>
			<input type="hidden" id="selectYearHid" name="selectYearHid"
				   value="<%=Calendar.getInstance().get(Calendar.YEAR)%>"/>
			<table class="clear" id="busStatic">
				<col width="90px"/>
				<col width="70px"/>
				<col width="100px"/>
				<col width="110px"/>
				<tr class="title_r">
					<td class="txt_left">收支结余：</td>
					<td id="dayRunningSum" class="bei_jin ">0</td>
					<td id="monthRunningSum" class="bei_jin ">0</td>
					<td id="yearRunningSum" class="bei_jin">0</td>
				</tr>
				<tr class="red_r">
					<td class="txt_left"><label style="font-weight:bold;">收入</label></td>
					<td class="bei_jin"></td>
					<td class="bei_jin"></td>
					<td class="bei_jin"></td>
				</tr>
				<tr class="red_r">
					<td class="right_td txt_left"><label>现金</label></td>
					<td id="dayCashIncome" class="bei_jin">0</td>
					<td id="monthCashIncome" class="bei_jin">0</td>
					<td id="yearCashIncome" class="bei_jin">0</td>
				</tr>
				<tr class="red_r">
					<td class="right_td txt_left"><label>银联</label></td>
					<td id="dayUnionPayIncome">0</td>
					<td id="monthUnionPayIncome">0</td>
					<td id="yearUnionPayIncome">0</td>
				</tr>
				<tr class="red_r">
					<td class="right_td txt_left"><label>支票</label></td>
					<td id="dayChequeIncome">0</td>
					<td id="monthChequeIncome">0</td>
					<td id="yearChequeIncome">0</td>
				</tr>
				<tr class="red_r">
					<td class="right_td txt_left"><label>代金券</label></td>
					<td id="dayCouponIncome">0</td>
					<td id="monthCouponIncome">0</td>
					<td id="yearCouponIncome">0</td>
				</tr>
      <c:if test="${!fourSShopVersions}">

				<tr class="blue_r">
					<td class="txt_left"><label style="font-weight:bold;">支出</label></td>
					<td class="bei_jin"></td>
					<td class="bei_jin"></td>
					<td class="bei_jin"></td>
				</tr>
				<tr class="blue_r">
					<td class="right_td txt_left"><label>现金</label></td>
					<td id="dayCashExpenditure">0</td>
					<td id="monthCashExpenditure">0</td>
					<td id="yearCashExpenditure">0</td>
				</tr>
				<tr class="blue_r">
					<td class="right_td txt_left"><label>银联</label></td>
					<td id="dayUnionPayExpenditure">0</td>
					<td id="monthUnionPayExpenditure">0</td>
					<td id="yearUnionPayExpenditure">0</td>
				</tr>
				<tr class="blue_r">
					<td class="right_td txt_left"><label>支票</label></td>
					<td id="dayChequeExpenditure">0</td>
					<td id="monthChequeExpenditure">0</td>
					<td id="yearChequeExpenditure">0</td>
				</tr>
			    <tr class="blue_r">
				    <td class="right_td txt_left"><label>代金券</label></td>
				    <td id="dayCouponExpenditure">0</td>
				    <td id="monthCouponExpenditure">0</td>
				    <td id="yearCouponExpenditure">0</td>
			    </tr>
					<tr class="hui_r" style="display:none;">
					<td colspan="4 " class="txt_left">应收欠款：<span
						id="currentDebtSum">0</span></td>
				</tr>
				<tr style="display:none;">
					<td class="right_td txt_left"><label>本期新增</label></td>
					<td id="dayDebtNew">0</td>
					<td id="monthDebtNew">0</td>
					<td id="yearDebtNew">0</td>
				</tr>
				<tr style="display:none;">
					<td class="right_td txt_left"><label>本期回笼</label></td>
					<td id="dayDebtWithdrawal">0</td>
					<td id="monthDebtWithdrawal">0</td>
					<td id="yearDebtWithdrawal">0</td>
				</tr>
				<tr style="display:none;">
					<td class="right_td txt_left"><label>欠款优惠</label></td>
					<td id="dayDebtDiscount">0</td>
					<td id="monthDebtDiscount">0</td>
					<td id="yearDebtDiscount">0</td>
				</tr>
      </c:if>

			</table>
		</div>
	</div>

<c:if test="${!fourSShopVersions}">
	<div class="clear payMoneys">
    <bcgogo:hasPermission permissions="WEB.STAT.RECEIVABLE_PAYABLE_STAT.RECEIVABLE">
      <div>
        <span onclick="redirectReceivable();" class="red_color">应收款:</span>
        <label>
      <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.BASE" resourceType="menu">
          <span id="customerTotalReceivableSpan" class="red_color">客户<span class="arialFont">¥</span>0</span>
      </bcgogo:hasPermission>

      <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.BASE" resourceType="menu">
          <span id="supplierTotalReceivableSpan" class="red_color">供应商<span class="arialFont">¥</span>0</span>
      </bcgogo:hasPermission>


        </label>
      </div>
    </bcgogo:hasPermission>

    <bcgogo:hasPermission permissions="WEB.STAT.RECEIVABLE_PAYABLE_STAT.PAYABLE">
      <div>
        <span onclick="redirectPayable();" class="green_color">应付款：</span>
        <label>
      <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.BASE" resourceType="menu">
          <span id="customerTotalPayableSpan" class="green_color">客户<span class="arialFont">¥</span>0</span>
      </bcgogo:hasPermission>

      <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.BASE" resourceType="menu">
          <span id="supplierTotalPayableSpan" class="green_color">供应商<span class="arialFont">¥</span>0</span>
      </bcgogo:hasPermission>

        </label>
      </div>
    </bcgogo:hasPermission>

	</div>
</c:if>

</div>


<div class="showRight" style="position:relative">
	<!--柱形图-->
	<span class="num_left"></span>

	<div class="num_center center">
		<div class="btnClick">
			<div class="rad_off" id="radDay">按日</div>
			<div class="rad_off" id="radMonth">按月</div>
			<div class="rad_off" id="radYear">按年</div>
		</div>

		<div id="chart_div" style="margin-top: -45px;"></div>
		<div id="noData"
			 style="color:#F00;width:100%; height:50px; float:left; clear:both;display:none; position:relative; text-align:center;  padding:80px 0 0; display:none;">
			您查询的日期沒有数据
		</div>
	</div>
	<span class="num_right"></span></div>
<!--统计信息-->
<div class="clear"></div>
<div class="height"></div>
<div class="contentTitle cont_title">

<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.BASE" resourceType="menu">
	<label style="width:110px;">客户交易明细:</label>
	<a id="dayIncome" class="hover_title"><span
		id="currentDayIncome"><%=Calendar.getInstance().get(Calendar.MONTH) + 1%>月<%=Calendar.getInstance().get(
		Calendar.DATE)%>日</span></a>
	<a id="monthIncome"><span
		id="currentMonthIncome"><%=Calendar.getInstance().get(Calendar.YEAR)%>年<%=Calendar.getInstance().get(
		Calendar.MONTH) + 1%>月</span></a>
	<a id="yearIncome"><span id="currentYearIncome"><%=Calendar.getInstance().get(Calendar.YEAR)%>年</span></a>
</bcgogo:hasPermission>

<bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.BASE" resourceType="menu">
	<a class="right_count" id="yearExpenditure"><span
		id="currentYearExpenditure"><%=Calendar.getInstance().get(Calendar.YEAR)%>年</span></a>
	<a id="monthExpenditure" class="right_count"><span
		id="currentMonthExpenditure"><%=Calendar.getInstance().get(Calendar.YEAR)%>年<%=Calendar.getInstance().get(
		Calendar.MONTH) + 1%>月</span></a>
	<a id="dayExpenditure" class="right_count"><span
		id="currentDayExpenditure"><%=Calendar.getInstance().get(
		Calendar.MONTH) + 1%>月<%=Calendar.getInstance().get(Calendar.DATE)%>日</span></a>
	<label class="right_count" style="width:120px;">供应商交易明细:</label>
</bcgogo:hasPermission>

</div>
<div class="clear"></div>
<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.BASE" resourceType="menu">
<div class="bus_stock add tb_add" id="dayRunningInfo" style="display:none;">
	<table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="runningInfoDay">
		<col />
		<col />
		<col />
		<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
			<col />
		</bcgogo:hasPermission>

		<col />
		<col />
		<col/>
		<col/>
		<col/>
		<col/>
		<col/>
        <bcgogo:hasPermission resourceType="render" permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE">
			<col/>
		</bcgogo:hasPermission>
		<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
			<col/>
		</bcgogo:hasPermission>
		<col/>
		<col/>
		<tr class="tab_title">
			<td class="first-padding">No</td>
			<td>日期</td>
			<td>客户</td>
			<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION"> <!-- add by zhuj -->
				<td>车牌</td>
			</bcgogo:hasPermission>
			<td>类别</td>
			<td>内容</td>
			<td>单据总额</td>
			<td>实收</td>
			<td>现金</td>
			<td>银联</td>
			<td>支票</td>
			<td>代金券</td>
            <bcgogo:hasPermission resourceType="render" permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE">
			    <td>用预收款</td>
		    </bcgogo:hasPermission>
			<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
				<td>会员储值</td>
			</bcgogo:hasPermission>
			<td>优惠</td>
			<td class="last-padding">欠款挂账</td>
		</tr>
	</table>

	<div class="height"></div>
	<div>
		<jsp:include page="/common/pageAJAX.jsp">
			<jsp:param name="url" value="runningStat.do?method=getIncomeDetailByDay"></jsp:param>
			<jsp:param name="jsHandleJson" value="initDayIncomeStat"></jsp:param>
			<jsp:param name="dynamical" value="dynamical1"></jsp:param>
			<jsp:param name="data"
					   value="{startPageNo:'1',maxRows:25,type:'month',dateStr:jQuery('#selectYearHid').val() + '-'+jQuery('#selectMonthHid').val() + '-' + jQuery('#selectDayHid').val(),arrayType:'timeDesc'}"></jsp:param>
		</jsp:include>
        <a class="statPrintBtn" runningType="dayIncome" id="printBtn1">打印</a>
	</div>
	<div style="float:left; width:100%; color:#000000;margin-top:5px">
		<div style="float:left; width:80px;font-weight: bold;">
			日收入总计
		</div>
		<div style="float:left; width:520px;">
			收入总额:<span id="dayIncomeTotal">0</span>元
			其中:（现金:<span id="dayIncomeCash">0</span>元
			银联:<span id="dayIncomeUnionPay">0</span>元
			支票:<span id="dayIncomeCheque">0</span>元
			代金券:<span id="dayIncomeCoupon">0</span>元）
		</div>
        <bcgogo:hasPermission resourceType="render" permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE"> <!-- add by zhuj-->
            <div style="float:left; width:120px;">
                用预收款:<span id="dayCustomerDepositExpenditure">0</span>元
            </div>
        </bcgogo:hasPermission>
		<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
			<div style="float:left; width:120px;">
				会员储值:<span id="dayIncomeMemberPay">0</span>元
			</div>
		</bcgogo:hasPermission>
		<div style="float:left; width:110px;">
			新增欠款:<span id="dayIncomeDebt">0</span>元
		</div>
	</div>

</div>


<div class="bus_stock add tb_add" id="monthRunningInfo" style="display:none;">
	<table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="runningInfoMonth">
		<col/>
		<col/>
		<col/>
		<col/>
		<col/>
		<col/>
        <bcgogo:hasPermission resourceType="render" permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE"> <!-- add by zhuj-->
			<col/>
		</bcgogo:hasPermission>
		<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
			<col/>
		</bcgogo:hasPermission>

		<col/>
		<col/>
		<tr class="tab_title">
			<td class="first-padding">No</td>
			<td>日期</td>
			<td>收入总额</td>
			<td>现金</td>
			<td>银联</td>
			<td>支票</td>
			<td>代金券</td>
            <bcgogo:hasPermission resourceType="render"
                                  permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE"> <!-- add by zhuj-->
                <td>用预收款</td>
            </bcgogo:hasPermission>
			<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
				<td>会员储值</td>
			</bcgogo:hasPermission>
			<td>新增欠款</td>
			<td class="last-padding">欠款回笼</td>
		</tr>
	</table>

	<div class="height"></div>

	<div>
		<jsp:include page="/common/pageAJAX.jsp">
			<jsp:param name="url" value="runningStat.do?method=getIncomeDetailByMonth"></jsp:param>
			<jsp:param name="jsHandleJson" value="initMonthIncomeStat"></jsp:param>
			<jsp:param name="dynamical" value="dynamical2"></jsp:param>
			<jsp:param name="data"
					   value="{startPageNo:'1',maxRows:25,type:'day',dateStr:$('#selectYearHid').val() + '-' + $('#selectMonthHid').val() + '-' + $('#selectDayHid').val(),arrayType:'timeDesc'}"></jsp:param>
		</jsp:include>
        <a class="statPrintBtn" runningType="monthIncome" id="printBtn2">打印</a>
	</div>
	<div style="float:left; width:100%; color:#000000;margin-top:5px">
		<div style="float:left; width:80px;font-weight: bold;">
			月收入总计
		</div>
		<div style="float:left; width:520px;">
			收入总额:<span id="monthIncomeTotal">0</span>元
			其中:（现金:<span id="monthIncomeCash">0</span>元
			银联:<span id="monthIncomeUnionPay">0</span>元
			支票:<span id="monthIncomeCheque">0</span>元
			代金券:<span id="monthIncomeCoupon">0</span>元）
		</div>
        <bcgogo:hasPermission resourceType="render" permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE"> <!-- add by zhuj-->
            <div style="float:left; width:120px;">
                用预收款:<span id="monthCustomerDepositExpenditure">0</span>元
            </div>
        </bcgogo:hasPermission>
		<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
			<div style="float:left; width:120px;">
				会员储值:<span id="monthIncomeMemberPay">0</span>元
			</div>
		</bcgogo:hasPermission>
		<div style="float:left; width:110px;">
			新增欠款:<span id="monthIncomeDebt">0</span>元
		</div>
	</div>

</div>


<div class="bus_stock add tb_add" id="yearRunningInfo" style="display:none;">
	<table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="runningInfoYear">
		<col width="80"/>
		<col width="100"/>
		<col width="100"/>
		<col width="100"/>
		<col width="100"/>
		<col width="100"/>
        <bcgogo:hasPermission resourceType="render"
                              permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE"> <!-- add by zhuj-->
            <col wdth="100" />
        </bcgogo:hasPermission>
		<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
			<col width="100"/>
		</bcgogo:hasPermission>
		<col width="100"/>
		<col width="100"/>
		<tr class="tab_title">
			<td class="first-padding">No</td>
			<td>日期</td>
			<td>收入总额</td>
			<td>现金</td>
			<td>银联</td>
			<td>支票</td>
			<td>代金券</td>
            <bcgogo:hasPermission resourceType="render"
                                  permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE"> <!-- add by zhuj-->
                <td>用预收款</td>
            </bcgogo:hasPermission>
			<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
				<td>会员储值</td>
			</bcgogo:hasPermission>
			<td>新增欠款</td>
			<td class="last-padding">欠款回笼</td>
		</tr>
	</table>

	<div class="height"></div>
	<div>
		<jsp:include page="/common/pageAJAX.jsp">
			<jsp:param name="url" value="runningStat.do?method=getIncomeDetailByYear"></jsp:param>
			<jsp:param name="jsHandleJson" value="initYearIncomeStat"></jsp:param>
			<jsp:param name="dynamical" value="dynamical3"></jsp:param>
			<jsp:param name="data"
					   value="{startPageNo:'1',maxRows:25,type:'month',dateStr:jQuery('#yearHid').val() + '-'+jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val(),arrayType:'timeDesc'}"></jsp:param>
		</jsp:include>
        <a class="statPrintBtn" runningType="yearIncome" id="printBtn3">打印</a>
	</div>

	<div style="float:left; width:100%; color:#000000;margin-top:5px">
		<div style="float:left; width:80px;font-weight: bold;">
			年收入总计
		</div>
		<div style="float:left; width:520px;">
			收入总额:<span id="yearIncomeTotal">0</span>元
			其中:（现金:<span id="yearIncomeCash">0</span>元
			银联:<span id="yearIncomeUnionPay">0</span>元
			支票:<span id="yearIncomeCheque">0</span>元
			代金券:<span id="yearIncomeCoupon">0</span>元）
		</div>
        <bcgogo:hasPermission resourceType="render"
                              permissions="WEB.VERSION.CUSTOMER.DEPOSIT.USE"> <!-- add by zhuj-->
            <div style="float:left; width:120px;">
				用预收款:<span id="yearCustomerDepositExpenditure">0</span>元
			</div>
        </bcgogo:hasPermission>
		<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
			<div style="float:left; width:120px;">
				会员储值:<span id="yearIncomeMemberPay">0</span>元
			</div>
		</bcgogo:hasPermission>
		<div style="float:left; width:110px;">
			新增欠款:<span id="yearIncomeDebt">0</span>元
		</div>
	</div>
</div>
</bcgogo:hasPermission>

<bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.BASE" resourceType="menu">
<div class="bus_stock add tb_add" id="dayExpenditureInfo" style="display:none;">
	<table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="expenditureInfoDay">
		<col/>
		<col/>
		<col/>
		<col/>
		<col/>
		<col/>
		<col/>
		<%--<col/>--%>
		<col/>
		<col/>
		<col/>
		<tr class="tab_title">
			<td class="first-padding">No</td>
			<td>日期</td>
			<td>类别</td>
			<td>内容</td>
			<td>单位</td>
			<td>单据总额</td>
			<td>实付</td>
			<td>现金</td>
			<td>银联</td>
			<td>支票</td>
			<%--<td>代金券</td>--%>
			<td>折扣</td>
			<td>用预付款</td>
			<td>挂账</td>
		</tr>
	</table>

	<div class="height"></div>
	<div>
		<jsp:include page="/common/pageAJAX.jsp">
			<jsp:param name="url" value="runningStat.do?method=getExpenditureDetailByDay"></jsp:param>
			<jsp:param name="jsHandleJson" value="initDayExpenditureStat"></jsp:param>
			<jsp:param name="dynamical" value="dynamical4"></jsp:param>
			<jsp:param name="data"
					   value="{startPageNo:'1',maxRows:25,type:'day',dateStr:jQuery('#selectYearHid').val() + '-'+jQuery('#selectMonthHid').val() + '-' + jQuery('#selectDayHid').val(),arrayType:'timeDesc'}"></jsp:param>
		</jsp:include>
        <a class="statPrintBtn" runningType="dayExpend" id="printBtn4">打印</a>
	</div>
	<div style="float:left; width:100%; color:#000000;margin-top:5px">
		<div style="float:left; width:80px;font-weight: bold;">
			日支出总计
		</div>
		<div style="float:left; width:520px;">
			支出总额:<span id="dayExpenditureTotal">0</span>元
			其中:（现金:<span id="dayExpenditureCash">0</span>元
			银联:<span id="dayExpenditureUnionPay">0</span>
			支票:<span id="dayExpenditureCheque">0</span>元）
			<%--代金券:<span id="dayExpenditureCoupon">0</span>元）--%>
		</div>

		<div style="float:left; width:120px;">
			用预付款:<span id="dayExpenditureDeposit">0</span>元
		</div>
		<div style="float:left; width:120px;">
			新增欠款:<span id="dayExpenditureDebt">0</span>元
		</div>
	</div>
</div>


<div class="bus_stock add tb_add" id="monthExpenditureInfo" style="display:none;">
	<table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="expenditureInfoMonth">
		<col/>
		<col/>
		<col/>
		<col/>
		<%--<col/>--%>
		<col/>
		<col/>
		<col/>
		<tr class="tab_title">
			<td class="first-padding">No</td>
			<td>日期</td>
			<td>支出总额</td>
			<td>现金</td>
			<td>银联</td>
			<td>支票</td>
			<%--<td>代金券</td>--%>
			<td>用预付款</td>
			<td>新增欠款</td>
			<td>欠款回笼</td>
		</tr>
	</table>
	<div class="height"></div>
	<div>
		<jsp:include page="/common/pageAJAX.jsp">
			<jsp:param name="url" value="runningStat.do?method=getExpenditureDetailByMonth"></jsp:param>
			<jsp:param name="jsHandleJson" value="initMonthExpenditureStat"></jsp:param>
			<jsp:param name="dynamical" value="dynamical5"></jsp:param>
			<jsp:param name="data"
					   value="{startPageNo:'1',maxRows:25,type:'day',dateStr:$('#selectYearHid').val() + '-' + $('#selectMonthHid').val() + '-' + $('#selectDayHid').val(),arrayType:'timeDesc'}"></jsp:param>
		</jsp:include>
        <a class="statPrintBtn" runningType="monthExpend" id="printBtn5">打印</a>
	</div>
	<div style="float:left; width:100%; color:#000000;margin-top:5px">
		<div style="float:left; width:80px;font-weight: bold;">
			月支出总计
		</div>
		<div style="float:left; width:520px;">
			支出总额:<span id="monthExpenditureTotal">0</span>元
			其中:（现金:<span id="monthExpenditureCash">0</span>元
			银联:<span id="monthExpenditureUnionPay">0</span>元
			支票:<span id="monthExpenditureCheque">0</span>元）
			<%--代金券:<span id="monthExpenditureCoupon">0</span>元）--%>
		</div>

		<div style="float:left; width:120px;">
			用预付款:<span id="monthExpenditureDeposit">0</span>元
		</div>
		<div style="float:left; width:120px;">
			新增欠款:<span id="monthExpenditureDebt">0</span>元
		</div>
	</div>
</div>


<div class="bus_stock add tb_add" id="yearExpenditureInfo" style="display:none;">
	<table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="expenditureInfoYear">
		<col/>
		<col/>
		<col/>
		<col/>
		<col/>
		<%--<col/>--%>
		<col/>
		<col/>
		<col/>
		<tr class="tab_title">
			<td class="first-padding">No</td>
			<td>日期</td>
			<td>支出总额</td>
			<td>现金</td>
			<td>银联</td>
			<td>支票</td>
			<%--<td>代金券</td>--%>
			<td>用预付款</td>
			<td>新增欠款</td>
			<td>欠款回笼</td>
		</tr>
	</table>
	<div class="height"></div>
	<div>
		<jsp:include page="/common/pageAJAX.jsp">
			<jsp:param name="url" value="runningStat.do?method=getExpenditureDetailByYear"></jsp:param>
			<jsp:param name="jsHandleJson" value="initYearExpenditureStat"></jsp:param>
			<jsp:param name="dynamical" value="dynamical6"></jsp:param>
			<jsp:param name="data"
					   value="{startPageNo:'1',maxRows:25,type:'month',dateStr:jQuery('#yearHid').val() + '-'+jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val(),arrayType:'timeDesc'}"></jsp:param>
		</jsp:include>
        <a class="statPrintBtn" runningType="yearExpend" id="printBtn6">打印</a>
	</div>
	<div style="float:left; width:100%; color:#000000;margin-top:5px">
		<div style="float:left; width:80px;font-weight: bold;">
			年支出总计
		</div>
		<div style="float:left; width:520px;">
			支出总额:<span id="yearExpenditureTotal">0</span>元
			其中:（现金:<span id="yearExpenditureCash">0</span>元
			银联:<span id="yearExpenditureUnionPay">0</span>元
			支票:<span id="yearExpenditureCheque">0</span>元）
			<%--代金券:<span id="yearExpenditureCoupon">0</span>元）--%>
		</div>

		<div style="float:left; width:110px;">
			用预付款:<span id="yearExpenditureDeposit">0</span>元
		</div>
		<div style="float:left; width:120px;">
			新增欠款:<span id="yearExpenditureDebt">0</span>元
		</div>
	</div>
</div>
</bcgogo:hasPermission>
<div class="clear"></div>
</div>
</div>
<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
