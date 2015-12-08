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
  <title>客户交易</title>
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
  <script type="text/javascript" src="js/businessAnalysis/businessUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/exportExcel<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "ITEM_STAT_CUSTOMER_STAT");

        <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
        APP_BCGOGO.Permission.Version.VehicleConstruction=${WEB_VERSION_VEHICLE_CONSTRUCTION};
        APP_BCGOGO.Permission.Version.MemberStoredValue=${WEB_VERSION_MEMBER_STORED_VALUE};
        </bcgogo:permissionParam>

        <bcgogo:permissionParam permissions="WEB.VERSION.FOUR_S_VERSION_BASE">
        APP_BCGOGO.Permission.Version.FourSShopVersion =${WEB_VERSION_FOUR_S_VERSION_BASE};
        </bcgogo:permissionParam>


    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
  <jsp:include page="/stat/statNavi.jsp">
    <jsp:param name="currPage" value="businessAnalysis"/>
  </jsp:include>
  <jsp:include page="businessAnalysisNavi.jsp">
    <jsp:param name="currPage" value="customerStat"/>
  </jsp:include>

  <div class="titBody">


    <div class="lineTitle">客户交易统计查询</div>
    <form id="itemStatisticsForm" name="itemStatisticsForm"
          action="itemStat.do?method=getItemStatData"
          method="post">
      <input type="hidden" id="customerId" name="customerId"/>
      <input type="hidden" id="statType" name="statType" value="customerStatistics"/>
      <input type="hidden" name="startPageNo" id="startPageNo" value="1">
      <input type="hidden" name="maxRows" id="maxRows" value="25">
      <input type="hidden" name="currentPage" id="currentPage" value="1">
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
          客户：&nbsp;<input type="text" id="customerName" name="customerName" class="txt" style="width:150px;"
                          value="客户名" initValue="客户名"/>&nbsp;<input
            type="text" id="mobile" class="txt" name="mobile" value="手机号" initValue="手机号"/>
        </div>
          <div class="divTit" <c:if test="${!wholesalerVersion}">style="display: none;"</c:if>>
              所属区域：
              <select id="select_province" name="province" class="txt select J_province"><option value="">所有省</option></select>
              <select id="select_city" name="city" class="txt select J_city"><option value="">所有市</option></select>
              <select id="select_township" name="region" class="txt select J_region"><option value="">所有区</option></select>
          </div>
        <div class="divTit" <c:if test="${wholesalerVersion}">style="margin-left: 400px;"</c:if>><input class="btn" id="statistics" type="submit" onfocus="this.blur();" value="统&nbsp;计">
        </div>
        <div class="divTit"><input type="button" id="resetSearchCondition" class="btn" value="重&nbsp;置"/></div>
      </div>
    </form>


    <div class="lineBottom"></div>
    <div class="clear height"></div>
    <div class="lineTitle tabTitle" id="statisticsInfo">
      累计交易记录：<span class="blue_color" id="recordNum">0</span>条&nbsp;&nbsp;累计交易总计：<span class="blue_color" id="orderTotal">0</span>元&nbsp;&nbsp;其中（实收<span class="blue_color"
                                                                                      id="settledTotal">0</span>元&nbsp;挂账<span
        class="blue_color" id="debtTotal">0</span>元）&nbsp;&nbsp;优惠总计：<span class="blue_color"
                                                                           id="discountTotal">0</span>元
      &nbsp;成本总计：<span class="blue_color" id="costTotal">0</span>元
      <bcgogo:permissionParam permissions="WEB.VERSION.FOUR_S_VERSION_BASE">
        <c:if test="${!WEB_VERSION_FOUR_S_VERSION_BASE}">
      &nbsp;毛利总计：<span class="blue_color" id="grossProfitTotal">0</span>元
      &nbsp;毛利率：<span class="blue_color" id="grossProfitPercent">0</span>
        </c:if>
      </bcgogo:permissionParam>
    </div>

    <div class="tab" style="width:981px;">
      <table cellpadding="0" cellspacing="0" class="tabSlip tabPick" id="customerStatisticsTable">

        <col width="30"/>
        <col width="70"/>
        <col>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
        <col>
        </bcgogo:hasPermission>
        <col width="80"/>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
        <col width="55"/>
        </bcgogo:hasPermission>
        <col width="70"/>
        <col width="80"/>
        <col>
        <col>
        <col>
        <col>
        <col>
        <bcgogo:permissionParam permissions="WEB.VERSION.FOUR_S_VERSION_BASE">
          <c:if test="${!WEB_VERSION_FOUR_S_VERSION_BASE}">
            <col width="60"/>
            <col width="60"/>
          </c:if>
        </bcgogo:permissionParam>

        <tr class="titleBg">
          <td style="padding-left:10px;">NO</td>
          <td>消费时间</td>
          <td>客户</td>
          <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
            <td>会员名</td>
          </bcgogo:hasPermission>
          <td>手机</td>
          <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
            <td>消费车牌</td>
          </bcgogo:hasPermission>
          <td>消费类型</td>
          <td>单据号</td>
          <td>消费总额</td>
          <td>成本</td>
          <td>实收</td>
          <td>优惠</td>
          <td>挂账</td>
          <bcgogo:permissionParam permissions="WEB.VERSION.FOUR_S_VERSION_BASE">
            <c:if test="${!WEB_VERSION_FOUR_S_VERSION_BASE}">
              <td>毛利</td>
              <td>毛利率</td>
            </c:if>
          </bcgogo:permissionParam>

        </tr>
      </table>
      <div class="lineBottom" style="width:983px;"></div>
      <div class="clear height"></div>
      <jsp:include page="/common/pageAJAX.jsp">
        <jsp:param name="url" value="itemStat.do?method=getItemStatData"></jsp:param>
        <jsp:param name="dynamical" value="dynamicalCustomer"></jsp:param>
        <jsp:param name="data" value="{startPageNo:'1',maxRows:25,statType:'customerStatistics'}"></jsp:param>
        <jsp:param name="jsHandleJson" value="drawCustomerStatTable"></jsp:param>
      </jsp:include>

      <div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
          <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_ANALYSIS.CUSTOMER_STAT_PRINT">
              <div class="btn_div_Img" id="print_div">
                  <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                  <input type="hidden" name="print" id="printHidden">

                  <div class="optWords">打印</div>
              </div>
          </bcgogo:hasPermission>
      </div>
      <div class="shopping_btn" style="float:left; clear:left; margin-top:16px;">
                <div class="btn_div_Img" id="exportDiv">
                    <input id="export" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                    <div class="optWords">导出</div>
                </div>
          <img id="exporting" style="margin-left: 30px; display: none;" title="正在导出" alt="正在导出" src="images/loadinglit.gif">
      </div>

    </div>
  </div>
  <%@ include file="/common/messagePrompt.jsp" %>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>