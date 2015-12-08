<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 13-1-30
  Time: 下午4:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/views/includes.jsp" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>入库退货</title>

  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/businessAnalysis<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/messageManage<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>

  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/stat/itemStatistics<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/businessAnalysis/salesReturnStat<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/businessAnalysis/businessUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid, "BUSINESS_ANALYSIS_REDIRECT_INVENTORY_RETURN_STAT");
</script>

</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
  <jsp:include page="/stat/statNavi.jsp">
    <jsp:param name="currPage" value="businessAnalysis"/>
  </jsp:include>
  <jsp:include page="businessAnalysisNavi.jsp">
    <jsp:param name="currPage" value="inventoryReturnStat"/>
  </jsp:include>


  <div class="clear"></div>
  <div class="titBody">
    <div class="lineTitle">入库退货统计查询</div>
    <form id="itemStatisticsForm" name="itemStatisticsForm" action="businessAnalysis.do?method=getInventoryReturnList"
          method="post">
      <input type="hidden" name="startPageNo" id="startPageNo" value="1">
      <input type="hidden" name="maxRows" id="maxRows" value="25">
      <input type="hidden" id="statType" name="statType" value="inventoryReturnStatistics"/>

      <div class="lineBody bodys">
        <div class="divTit" style="width:100%;">
          统计日期段：
            <a class="btnList" id="my_date_today" name="my_date_select">本日</a>&nbsp;
            <a class="btnList" id="my_date_thisweek" name="my_date_select">本周</a>&nbsp;
            <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
            <a class="btnList" id="my_date_thisyear" name="my_date_select">本年</a>&nbsp;
            <a class="btnList" id="my_date_self_defining" name="my_date_select">自定义时间段</a>&nbsp;
          <input class="my_startdate" id="startDate" type="text" style="width:100px;" readonly="readonly" name="startTimeStr"/>&nbsp;至&nbsp;
          <input class="my_enddate" id="endDate" type="text" style="width:100px;" readonly="readonly" name="endTimeStr"/>
        </div>
        <div class="divTit">
          供应商：&nbsp;<input type="text" id="supplierName" name="supplierName" style="width:150px;" class="txt"
                          value="供应商名"/>&nbsp;<input
            type="text" id="mobile" class="txt" name="mobile" value="手机号"/>
        </div>

        <div class="divTit"><input class="btn" id="statistics" type="button" value="统&nbsp;计"></div>
        <div class="divTit"><input type="button" id="resetSearchCondition" class="btn" value="重&nbsp;置"/></div>
      </div>
    </form>


    <div class="lineBottom"></div>
    <div class="clear height"></div>
    <div class="lineTitle tabTitle">
      累计退货总计：<span class="blue_color" id="orderTotal">0</span>元&nbsp;&nbsp;其中（实退<span class="blue_color"
                                                                                             id="settledTotal">0</span>元&nbsp;挂账<span
        class="blue_color" id="debtTotal">0</span>元）&nbsp;&nbsp;优惠总计：<span class="blue_color" id="discountTotal">0</span>元
    </div>
    <div class="tab">
      <table id="salesReturnTable" cellpadding="0" cellspacing="0" class="tabSlip">
        <col width="80">
        <col width="110">
        <col width="130">
        <col width="200">
        <col width="130">
        <col width="130">
        <col width="100">
        <col width="100">
        <tr class="titleBg">
          <td style="padding-left:10px;">NO</td>
          <td>退货日期</td>
          <td>退货单号</td>
          <td>退货供应商</td>
          <td>应退金额</td>
          <td>实退</td>
          <td>挂账</td>
          <td>优惠</td>
        </tr>
      </table>
      <jsp:include page="/common/pageAJAX.jsp">
        <jsp:param name="url" value="businessAnalysis.do?method=getInventoryReturnList"></jsp:param>
        <jsp:param name="dynamical" value="dynamicalSalesReturn"></jsp:param>
        <jsp:param name="data" value="{startPageNo:'1',maxRows:25}"></jsp:param>
        <jsp:param name="jsHandleJson" value="initSalesReturnByJson"></jsp:param>
      </jsp:include>
      <div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
          <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_ANALYSIS.INVENTORY_RETURN_STAT_PRINT">
              <div class="btn_div_Img" id="print_div_inventoryReturn">
                  <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                  <input type="hidden" name="print" id="printHidden">

                  <div class="optWords">打印</div>
              </div>
          </bcgogo:hasPermission>
      </div>
    </div>
  </div>

</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

</body>
</html>