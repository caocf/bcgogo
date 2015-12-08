<%--
  Created by IntelliJ IDEA.
  User: liuWei
  Date: 13-1-30
  Time: 下午4:37
  To change this template use File | Settings | File Templates.
--%>

<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>供应商交易</title>
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
  <script type="text/javascript" src="js/page/stat/itemStatistics<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/businessAnalysis/businessUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
      defaultStorage.setItem(storageKey.MenuUid, "ITEM_STAT_SUPPLIER_STAT");
  </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
  <jsp:include page="/stat/statNavi.jsp">
    <jsp:param name="currPage" value="businessAnalysis"/>
  </jsp:include>
  <jsp:include page="businessAnalysisNavi.jsp">
    <jsp:param name="currPage" value="supplierStat"/>
  </jsp:include>
  <div class="clear"></div>
  <div class="titBody">
    <div class="lineTitle">供应商交易统计查询</div>
    <form id="itemStatisticsForm" name="itemStatisticsForm" action="itemStat.do?method=getItemStatData"
          method="post">
      <input type="hidden" name="startPageNo" id="startPageNo" value="1">
      <input type="hidden" name="maxRows" id="maxRows" value="25">
      <input type="hidden" name="currentPage" id="currentPage" value="1">
      <input type="hidden" id="statType" name="statType" value="supplierStatistics"/>


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
          <div class="divTit">
              所属区域：
              <select id="select_province" name="province" class="txt select J_province"><option value="">所有省</option></select>
              <select id="select_city" name="city" class="txt select J_city"><option value="">所有市</option></select>
              <select id="select_township" name="region" class="txt select J_region"><option value="">所有区</option></select>
          </div>
        <div class="divTit" style="margin-left: 400px;"><input class="btn" id="statistics" type="submit" onfocus="this.blur();" value="统&nbsp;计">
        </div>
        <div class="divTit"><input type="button" id="resetSearchCondition" class="btn" value="重&nbsp;置"/></div>
      </div>

    </form>
  </div>

  <div class="lineBottom"></div>
  <div class="clear height"></div>
  <div class="lineTitle tabTitle">
    累计交易记录：<span class="blue_color" id="recordNum">0</span>条&nbsp;&nbsp;累计交易总计：<span class="blue_color" id="orderTotal">0</span>元&nbsp;&nbsp;其中（实付<span
      class="blue_color"
      id="settledTotal">0</span>元&nbsp;挂账<span
      class="blue_color" id="debtTotal">0</span>元）&nbsp;&nbsp;优惠总计：<span class="blue_color" id="discountTotal">0</span>元
  </div>

  <div class="tab">
    <table cellpadding="0" cellspacing="0" class="tabSlip tabPick" id="supplierStatisticsTable">

      <col width="40"/>
      <col width="70"/>
      <col>
      <col width="80"/>
      <col width="80"/>
      <col width="90"/>
      <col width="80"/>
      <col width="100"/>
      <col width="80"/>
      <col width="80"/>
      <col width="80"/>
      <tr class="titleBg">
        <td class="first-padding">NO</td>
        <td>交易时间</td>
        <td>供应商</td>
        <td>联系人</td>
        <td>联系方式</td>
        <td>单据编号</td>
        <td>单据类型</td>
        <td>交易总额</td>
        <td>实付金额</td>
        <td>挂账</td>
        <td>优惠</td>
      </tr>
    </table>
    <jsp:include page="/common/pageAJAX.jsp">
      <jsp:param name="url" value="itemStat.do?method=getItemStatData"></jsp:param>
      <jsp:param name="dynamical" value="dynamicalSupplier"></jsp:param>
      <jsp:param name="data" value="{startPageNo:'1',maxRows:25,statType:'supplierStatistics'}"></jsp:param>
      <jsp:param name="jsHandleJson" value="drawSupplierStatTable"></jsp:param>
    </jsp:include>

    <div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
        <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_ANALYSIS.SUPPLIER_STAT_PRINT">
            <div class="btn_div_Img" id="print_div">
                <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                <input type="hidden" name="print" id="printHidden">
                <div class="optWords">打印</div>
            </div>
        </bcgogo:hasPermission>
    </div>
  </div>
</div>
<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>