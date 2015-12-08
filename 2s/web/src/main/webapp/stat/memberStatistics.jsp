<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <%--<meta http-equiv="X-UA-Compativle" content="IE-EmulateIE7"/>--%>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>会员消费</title>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/stat/memberStatistics<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
      defaultStorage.setItem(storageKey.MenuUid, "MEMBER_MEMBER_STAT");
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
    <jsp:param name="currPage" value="memberStat"/>
</jsp:include>
</div>
<div class="clear"></div>
<div class="num_show" style="margin:0px;">
<div class="count_info">
  <form id="memberStatisticsForm" name="memberStatisticsForm" action="member.do?method=getMemberStatData"
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
      <label class="lbl">客户名：</label>
      <input id="customerName" name="customerName" type="text"
             style="width:150px; height:19px; float:left; " autocomplete="off" class="textbox">
      <input type="hidden" id="customerId" name="customerId"/>
      <%--<label class="chk"><input type="checkbox" />不显示已删除客户</label>--%>
      <label class="lbl">车牌号：</label>
      <input id="vehicle" name="vehicle" type="text"
             style="width:150px; height:19px;text-transform:uppercase;" autocomplete="off" class="textbox" >
    </div>
    <div class="height"></div>
    <div class="divCount" id="div_business">
      <label class="lbl">会员号：</label>
      <label>
        <input type="text" id="customerMemberNo" name="accountMemberNo" pagetype="memberstatistics" initialValue=""
               style="width:150px;height:19px;" autocomplete="off" class="textbox" />
      </label>
    </div>
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
    <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.BUY_CARD">
  <a id="buy" class="big_hover_title">购卡续卡记录(0)</a>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.CARD_CONSUME">
  <a id="member">会员卡消费详细(0)</a>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.RETURN_CARD">
  <a id="back">退卡记录(0)</a>
    </bcgogo:hasPermission>
</div>

<bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.BUY_CARD">
<div class="bus_stock add tb_add" id="recordBuy">
  <table cellpadding="0" cellspacing="0" class="serviceTable infoCard" id="infoCard" style="table-layout:auto;">
    <col width="30">
    <col width="80">
    <col width="80">
    <col width="120">
    <col width="140">
    <col width="60">
    <col width="60">
    <col width="60">
    <col width="60">
    <col width="120">
    <tr class="tab_title">
      <td class="first-padding">NO</td>
      <td>客户</td>
      <td>会员卡号</td>
      <td>储值（变更）</td>
      <td>服务内容（变更）</td>
      <td>应收金额</td>
      <td>实收</td>
      <td>优惠</td>
      <td>挂账</td>
      <td class="last-padding">操作日期</td>
    </tr>
  </table>

  <div class="clear"></div>
  <div class="i_height"></div>
  <div style="color:#000000; font-weight:bold;">
    <div style="float:left; padding-right:20px;">总计：应收总额：<span id="buyCardTotal" class="j_clear_span">0</span>元</div>
    <div style="float:left; padding-right:20px;">实收总额：<span id="buyCardTotalSettledAmount" class="j_clear_span">0</span>元
    </div>
    <div style="float:left; padding-right:20px;">本页小计：应收<span id="pageBuyCardTotal" class="j_clear_span">0</span>元</div>
    <div style="float:left; padding-right:20px;">实收：<span id="pageBuyCardSettledAmount" class="j_clear_span">0</span>元
    </div>
    <div style="float:left; padding-right:20px;">优惠：<span id="pageBuyCardDiscount" class="j_clear_span">0</span>元</div>
    挂账：<span id="pageBuyCardDebt" class="j_clear_span">0</span>元
    <span id="print" orderType="memberCardOrder" style="color:#6699cc; float:right; text-decoration:underline;cursor: pointer;">打印</span>
  </div>
  <div class="clear"></div>
  <div class="height"></div>
  <jsp:include page="/common/pageAJAX.jsp">
    <jsp:param name="url" value="member.do?method=getMemberCardOrder"></jsp:param>
    <jsp:param name="dynamical" value="dynamicalMemberBuyCard"></jsp:param>
    <jsp:param name="data" value="{startPageNo:'1',maxRows:25}"></jsp:param>
    <jsp:param name="jsHandleJson" value="initMemberCardByJson"></jsp:param>
  </jsp:include>
</div>
</bcgogo:hasPermission>

<bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.CARD_CONSUME">
<div class="bus_stock add tb_add" id="recordMember" style="display:none;">
  <table cellpadding="0" cellspacing="0" class="serviceTable infoCard" id="consumeCard" style="table-layout:auto;">
    <col width="30">
    <col width="70">
    <col width="160">
    <col width="100">
    <col width="100">
    <col width="80">
    <col width="120">
    <col width="150">
    <col width="200">
    <tr class="tab_title">
      <td class="first-padding">NO</td>
      <td>消费日期</td>
      <td>消费类型</td>
      <td>单据号</td>
      <td>客户</td>
      <td>消费车牌</td>
      <td>消费方式</td>
      <td>消费卡号</td>
      <td class="last-padding">刷卡金额/次数</td>
    </tr>
  </table>

  <div class="clear"></div>
  <div class="i_height"></div>
  <div style="color:#000000; font-weight:bold;">
    <div style="float:left;padding-right:20px;">刷卡金额总计：<span id="consumeTotal">0</span>元</div>
    本页小计：刷卡金额<span id="pageConsumeTotal">0</span>元
      <span id="print2" orderType="memberConsume" style="color:#6699cc; float:right; text-decoration:underline;cursor: pointer;">打印</span>
  </div>
  <div class="clear"></div>
  <div class="height"></div>
  <jsp:include page="/common/pageAJAX.jsp">
    <jsp:param name="url" value="member.do?method=getMemberConsume"></jsp:param>
    <jsp:param name="dynamical" value="dynamicalMemberConsume"></jsp:param>
    <jsp:param name="data" value="{startPageNo:'1',maxRows:25}"></jsp:param>
    <jsp:param name="jsHandleJson" value="initMemberConsumeByJson"></jsp:param>
  </jsp:include>
</div>
</bcgogo:hasPermission>

<bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.RETURN_CARD">
<div class="bus_stock add tb_add" id="cardInfo" style="display:none;">
  <table cellpadding="0" cellspacing="0" class="serviceTable infoCard" id="returnCard" style="table-layout:auto;">
    <col width="50">
    <col width="150">
    <col width="150">
    <col width="80">
    <col width="300">
    <col width="80">
    <col width="80">
    <col width="100">
    <tr class="tab_title">
      <td class="first-padding">NO</td>
      <td>客户</td>
      <td>会员卡号</td>
      <td>会员类型</td>
      <td>服务内容</td>
      <td>储值金额</td>
      <td>退卡金额</td>
      <td class="last-padding">操作日期</td>
    </tr>
  </table>
  <div class="clear"></div>
  <div class="i_height"></div>
  <div style="color:#000000; font-weight:bold;">
    <div style="float:left;padding-right:20px;">退卡总额：<span id="returnTotal">0</span>元</div>
    本页小计：退卡总额<span id="pageReturnTotal">0</span>元
      <span id="print3" ordertype="memberReturn" style="color:#6699cc; float:right; text-decoration:underline;cursor: pointer;">打印</span>
  </div>
  <div class="clear"></div>
  <div class="height"></div>
  <jsp:include page="/common/pageAJAX.jsp">
    <jsp:param name="url" value="member.do?method=getMemberReturnOrder"></jsp:param>
    <jsp:param name="dynamical" value="dynamicalMemberReturnCard"></jsp:param>
    <jsp:param name="data" value="{startPageNo:'1',maxRows:25}"></jsp:param>
    <jsp:param name="jsHandleJson" value="initMemberCardReturnByJson"></jsp:param>
  </jsp:include>
</div>
</bcgogo:hasPermission>
<div class="height"></div>
<div class="clear"></div>
</div>
<%@ include file="/common/messagePrompt.jsp" %>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>