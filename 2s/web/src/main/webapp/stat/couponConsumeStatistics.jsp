<%--
  Created by IntelliJ IDEA.
  User: LiTao
  Date: 2015/11/5
  Time: 15:20
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <%--<meta http-equiv="X-UA-Compativle" content="IE-EmulateIE7"/>--%>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>代金券消费</title>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/stat/couponConsumeStatistics<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid, "COUPON_CONSUME_STAT");
  </script>

  <style type="text/css">
    .customer {
      -moz-border-bottom-colors: none;
      -moz-border-left-colors: none;
      -moz-border-right-colors: none;
      -moz-border-top-colors: none;
      border-color: -moz-use-text-color #CCCCCC #CCCCCC;
      border-image: none;
      border-style:none;
      border-right: none;
      border-width:0;
      font-size: 12px;
      padding: 10px;
    }
    .customer{width:auto;}

    .serviceTable {
      text-align: center;
    }

  </style>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
  <div class="mainTitles clear">
    <jsp:include page="statNavi.jsp">
      <jsp:param name="currPage" value="businessStat"/>
    </jsp:include>
    <jsp:include page="businessStatNavi.jsp">
      <jsp:param name="currPage" value="couponConsumeStat"/>
    </jsp:include>
  </div>
  <div class="clear"></div>
  <div class="num_show" style="margin:0px;">
    <div class="count_info">
      <form id="couponConsumeStatisticsForm" name="couponConsumeStatisticsForm" action="couponConsume.do?method=getCouponConsumeStatData"
            method="post">
        <input type="hidden" name="startPageNo" id="startPageNo" value="1">
        <input type="hidden" name="maxRows" id="maxRows" value="25">

        <div class="height"></div>

        <div class="divCount">

          <label class="lbl">统计日期段：</label>

          <a class="btnList" id="my_date_thisweek" name="my_date_select">本周</a>&nbsp;
          <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
          <a class="btnList" id="my_date_thisyear" name="my_date_select">本年</a>&nbsp;
          <a class="btnList" id="my_date_self_defining" name="my_date_select">自定义时间段</a>&nbsp;


          <input class="my_startdate" id="startDate" type="text" readonly="readonly" name="startTimeStr"/>

          &nbsp;至&nbsp;

          <input class="my_enddate" id="endDate" type="text" readonly="readonly" name="endTimeStr"/>

        </div>
        <div class="height"></div>

        <div class="divCount" id="div_customer">
          <label class="lbl">消费方式：</label>
          <select id="orderTypeStr" name="orderTypeStr"
                  style="width:100px; height:19px;float:left;text-transform:uppercase;">
            <option value="">全部</option>
            <option value="施工销售">施工销售</option>
            <option value="洗车美容">洗车美容</option>
          </select>
          <%--<label class="chk"><input type="checkbox" />不显示已删除客户</label>--%>
          <label class="lbl">客户信息：</label>
          <!-- customerInfo 包括appUser.name 或 appVehicle.vehicleNo -->
          <input id="customerInfo" name="customerInfo" type="text"
                 style="width:150px; height:19px;text-transform:uppercase;" autocomplete="off" class="textbox" >
        </div>
        <div class="height"></div>
        <div class="height"></div>
        <div class="height"></div>
        <div class="divCount btnCount">
          <input type="button" id="statistics" value="统&nbsp;计" onfocus="this.blur();" class="btn"/>
          <input type="button" id="resetSearchCondition" value="重&nbsp;置" onfocus="this.blur();" class="btn"/>
        </div>
      </form>
    </div>
    <div class="clear"></div>
    <div class="height"></div>
    <div class="height"></div>

    <div class="contentTitle cont_title">
      <%--<bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.BUY_CARD">--%>
        <a id="income" class="big_hover_title">现金券收入(0)</a>
      <%--</bcgogo:hasPermission>--%>
      <%--<bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.CARD_CONSUME">--%>
        <a id="expenses">现金券支出(0)</a>
      <%--</bcgogo:hasPermission>--%>
    </div>

    <%--<bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.BUY_CARD">--%>
      <div class="bus_stock add tb_add" id="incomeRecord">
        <table cellpadding="0" cellspacing="0" class="serviceTable infoCard" id="incomeInfo" style="table-layout:auto;">
          <col width="30">
          <col width="160">
          <col width="90">
          <col width="90">
          <col width="90">
          <col width="140">
          <col width="110">
          <col width="100">
          <tr class="tab_title">
            <td class="first-padding">NO</td>
            <td>客户信息</td>
            <td>消费方式</td>
            <td>代金券金额</td>
            <td>消费时间</td>
            <td>车辆施工单号</td>
            <td>车辆消费总金额</td>
            <td class="last-padding">操作</td>
          </tr>
        </table>

        <div class="clear"></div>
        <div class="i_height"></div>
        <div style="color:#000000; font-weight:bold;">
          <div style="float:right; padding-right:20px;">总计：&nbsp;现金券总额：<span id="incomeTotal" class="j_clear_span">0</span>元</div>
          <div style="float:right; width: 50px;">&nbsp;</div>
        </div>
        <div class="clear"></div>
        <div class="height"></div>
        <jsp:include page="/common/pageAJAX.jsp">
          <jsp:param name="url" value="couponConsume.do?method=couponConsumeIncome"></jsp:param>
          <jsp:param name="dynamical" value="dynamical1"></jsp:param>
          <jsp:param name="data" value="{startPageNo:'1',maxRows:25}"></jsp:param>
          <jsp:param name="jsHandleJson" value="initCouponIncomeByJson"></jsp:param>
        </jsp:include>
      </div>
    <%--</bcgogo:hasPermission>--%>

    <%--<bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.CARD_CONSUME">--%>
      <div class="bus_stock add tb_add" id="expensesRecord" style="display:none;">
        <table cellpadding="0" cellspacing="0" class="serviceTable infoCard" id="expensesInfo" style="table-layout:auto;">
          <col width="30">
          <col width="240">
          <col width="120">
          <col width="120">
          <col width="120">
          <col width="240">
          <col width="160">
          <tr class="tab_title">
            <td class="first-padding">NO</td>
            <td>购买商品</td>
            <td>数量</td>
            <td>代金券抵用金额</td>
            <td>购买时间</td>
            <td>订单号</td>
            <td class="last-padding">订单总金额</td>
          </tr>
        </table>

        <div class="clear"></div>
        <div class="i_height"></div>
        <div style="color:#000000; font-weight:bold;">
          <div style="float:right; padding-right:20px;">总计：&nbsp;现金券总额：<span id="expensesTotal" class="j_clear_span">0</span>元</div>
          <div style="float:right; width: 50px;">&nbsp;</div>
        </div>
        <div class="clear"></div>
        <div class="height"></div>
        <jsp:include page="/common/pageAJAX.jsp">
          <jsp:param name="url" value="couponConsume.do?method=couponConsumeExpenses"></jsp:param>
          <jsp:param name="dynamical" value="dynamical2"></jsp:param>
          <jsp:param name="data" value="{startPageNo:'1',maxRows:25}"></jsp:param>
          <jsp:param name="jsHandleJson" value="initCouponExpensesByJson"></jsp:param>
        </jsp:include>
      </div>
    <%--</bcgogo:hasPermission>--%>
    <div class="height"></div>
    <div class="clear"></div>
  </div>
  <%@ include file="/common/messagePrompt.jsp" %>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
