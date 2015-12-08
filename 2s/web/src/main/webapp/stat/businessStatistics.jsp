<%@ page import="java.util.Calendar" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>营业额</title>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
      <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
  <%--<script type="text/javascript" src="js/ChartInit<%=ConfigController.getBuildVersion()%>.js"></script>--%>
  <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/modules/exporting.js"></script>
  <script type="text/javascript" src="js/initHighChart<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/businessStatistics<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/businessStatDetail<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/exportExcel<%=ConfigController.getBuildVersion()%>.js"></script>
  <%
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
  %>

    <script type="text/javascript">
 		APP_BCGOGO.Permission.Version.FourSShopVersion =${empty fourSShopVersions?false:fourSShopVersions};
  
        defaultStorage.setItem(storageKey.MenuUid, "BUSINESS_STAT_GET_BUSINESS_STAT");
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
    <jsp:param name="currPage" value="businessStat"/>
</jsp:include>
</div>
<div class="num_show first_cont clear">
<div class="staticLeft">
<div class="count_info counts">

<!--营销项目统计-->
<!--商品库存统计-->
<div class="bus_stock stockAdd">
  <div class="bus_title">
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
  </div>
  <div class="almost statSum">
    <div style="float:left;">营业额：</div>
    <div id="dayStatSum"
         style="float:left; padding-left:12px; width:80px; text-align:right; color:#6699cc;cursor:pointer;">
        0
    </div>
    <div id="monthStatSum" style="float:left;width:98px; text-align:right; color:#6699cc; cursor:pointer;">
        0
    </div>
    <div id="yearStatSum" style="float:left;width:130px; text-align:right; color:#6699cc;cursor:pointer;">
        0
    </div>
  </div>
  <form id="myForm" action="businessStat.do?method=saveExpendDetail" method="POST">
    <input type="hidden" id="dayHid" name="dayHid" value="<%=Calendar.getInstance().get(Calendar.DATE)%>"/>
    <input type="hidden" id="monthHid" name="monthHid"
           value="<%=Calendar.getInstance().get(Calendar.MONTH)+1%>"/>
    <input type="hidden" id="yearHid" name="yearHid"
           value="<%=Calendar.getInstance().get(Calendar.YEAR)%>"/>
    <input type="hidden" id="queryType" name="queryType" value=""/>
    <table cellpadding="0" cellspacing="0" class="busTable" id="busTable">
        <col width="80"/>
        <col width="70"/>
        <col width="100"/>
        <col width="90"/>

        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
            <c:if test="<%=isMemberSwitchOn%>">
              <tr class="member" style="display:none">
                <td style="padding-left:20px;">会员卡</td>
                <td id="dayMemberStat" class="busNum clickNum">0</td>
                <td id="monthMemberStat" class="busNum addWidth clickNum">0</td>
                <td id="yearMemberStat" class="busNum clickNum">0</td>
              </tr>
            </c:if>
        </bcgogo:hasPermission>


        <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BUSINESS.WASH_CAR">
            <tr class="wash">
              <td style="padding-left:20px;">洗车</td>
              <td id="dayWashStat" class="busNum clickNum">0</td>
              <td id="monthWashStat" class="busNum addWidth clickNum">0</td>
              <td id="yearWashStat" class="busNum clickNum">0</td>
            </tr>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BUSINESS.CONSTRUCTION">
            <tr class="service">
              <td style="padding-left:20px;">车辆施工</td>
              <td id="dayRepairStat" class="busNum clickNum">0</td>
              <td id="monthRepairStat" class="busNum addWidth clickNum">0</td>
              <td id="yearRepairStat" class="busNum clickNum">0</td>
            </tr>
        </bcgogo:hasPermission>




        <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BUSINESS.SALE">
                <tr class="sales">
                    <td style="padding-left:20px;">商品销售</td>
                    <td id="daySaleStat" class="busNum clickNum">0</td>
                    <td id="monthSaleStat" class="busNum addWidth clickNum">0</td>
                    <td id="yearSaleStat" class="busNum clickNum">0</td>
                </tr>
        </bcgogo:hasPermission>

        <c:if test="${!fourSShopVersions}">

          <tr class="productCost">
            <td style="padding-left:20px;">商品成本</td>
            <td id="dayProductStat" class="busNums">0</td>
            <td id="monthProductStat" class="busNums addWidth">0</td>
            <td id="yearProductStat" class="busNums">0</td>
          </tr>

          <tr class="orderOtherCost">
            <td style="padding-left:20px;">其他成本</td>
            <td id="dayorderOtherIncomeCost" class="busNums">0</td>
            <td id="monthorderOtherIncomeCost" class="busNums addWidth">0</td>
            <td id="yearorderOtherIncomeCost" class="busNums">0</td>
          </tr>

          <tr class="otherIncome">
            <td>营业外收入<input type="button" class="downArrow" id="otherIncomeArrow" onfocus="this.blur();"/></td>
            <td id="dayOtherIncome" class="busNums">0</td>
            <td id="monthOtherIncome" class="busNums addWidth">0</td>
            <td id="yearOtherIncome" class="busNums">0</td>
          </tr>
        </c:if>



      </table>


    <c:if test="${!fourSShopVersions}">

    <div id="otherIncomeDiv" class="busTable" style="width:395px;height:100px;overflow-y:auto;overflow-x:hidden;display:none;">
      <table cellpadding="0" cellspacing="0" class="busTable" id="incomeTable">
        <col width="80"/>
        <col width="70"/>
        <col width="100"/>
        <col width="90"/>


      </table>
    </div>

    <table cellpadding="0" cellspacing="0" class="busTable">
      <col width="80"/>
      <col width="70"/>
      <col width="100"/>
      <col width="90"/>
      <tr class="outDetail">
        <td>营业外支出<input type="button" class="downArrow" id="otherExpenditureArrow" onfocus="this.blur();"/></td>
        <td id="dayTotalHid" class="busNums">0</td>
        <td id="monthTotalHid" class="busNums addWidth">0</td>
        <td id="yearTotalHid" class="busNums">0</td>
      </tr>
    </table>

    <div id="otherExpenditureDiv" class="busTable" style="width:395px;height:100px;overflow-y:auto;overflow-x:hidden;display:none;">
      <table cellpadding="0" cellspacing="0" class="busTable" id="expenditureTable">
        <col width="80"/>
        <col width="70"/>
        <col width="100"/>
        <col width="90"/>
      </table>

    </div>

            <%--<tr class="rent">--%>
              <%--<td style="padding-left:30px;">房租</td>--%>
              <%--<td id="dayRentHid" class="busNums">0</td>--%>
              <%--<td id="monthRentHid" class="busNums addWidth">0</td>--%>
              <%--<td id="yearRentHid" class="busNums">0</td>--%>
            <%--</tr>--%>
            <%--<tr class="labor">--%>
              <%--<td style="padding-left:30px;">工资提成</td>--%>
              <%--<td id="dayLaborHid" class="busNums">0</td>--%>
              <%--<td id="monthLaborHid" class="busNums addWidth">0</td>--%>
              <%--<td id="yearLaborHid" class="busNums">0</td>--%>
            <%--</tr>--%>

            <%--<tr class="other">--%>
              <%--<td style="padding-left:30px;">水电杂项</td>--%>
              <%--<td id="dayOtherHid" class="busNums">0</td>--%>
              <%--<td id="monthOtherHid" class="busNums addWidth">0</td>--%>
              <%--<td id="yearOtherHid" class="busNums">0</td>--%>
            <%--</tr>--%>

            <%--<tr class="otherFee">--%>
              <%--<td style="padding-left:30px;">其他支出</td>--%>
              <%--<td id="dayStatOtherId" class="busNums">0</td>--%>
              <%--<td id="monthStatOtherId" class="busNums addWidth">0</td>--%>
              <%--<td id="yearStatOtherId" class="busNums">0</td>--%>
            <%--</tr>--%>

    <table cellpadding="0" cellspacing="0" class="busTable" id="profitTable">

      <col width="80"/>
      <col width="70"/>
      <col width="100"/>
      <col width="90"/>
        <tr class="rate">
            <td>毛利</td>
            <td class="busNumber">0</td>
            <td class="busNumber addWidth">0</td>
            <td class="busNumber">0</td>
        </tr>
        <tr class="rateCent">
            <td>毛利率</td>
            <td class="busNumber">0</td>
            <td class="busNumber addWidth">0</td>
            <td class="busNumber">0</td>
        </tr>
    </table>
    </c:if>


  </form>
</div>

<!--商品库存统计结束-->


</div>
  <a id="printBusinessStat"
     style="color:#6699cc; float:right; display:block; margin:10px 0px 0px 0px; text-decoration:underline; font-weight:bold; cursor:pointer;">打印</a>

<a id="runningStatSum"
     style="color:#6699cc; float:left; display:block; margin:10px 100px 0px 0px; text-decoration:underline; font-weight:bold; cursor:pointer;">本日收支结余:<span id="dayRunningSum">0</span>元</a>


<c:if test="<%=isMemberSwitchOn%>">
    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
      <a id="memberStatSpan"
         style="color:#6699cc; float:left; display:block; margin:10px 0px 0px 0px; text-decoration:underline; font-weight:bold; cursor:pointer;">会员卡消费</a>
    </bcgogo:hasPermission>
</c:if>



<iframe id="showqueryDate" style="position:absolute;z-index:8;top:210px;left:300px;display:none; "
        allowtransparency="true" width="450px" height="400px" frameborder="0" src="" scrolling="no"></iframe>
</div>

<div class="showRight">
    <!--柱形图-->
    <span class="num_left"></span>

    <div class="num_center center">
        <div class="btnClick">
            <div id="radDay" class="rad_off">按日</div>
            <div class="rad_off" id="radMonth">按月</div>
            <div class="rad_off" id="radYear">按年</div>
        </div>
        <div id="chart_div" style="margin-top: -45px;"></div>
        <div id="noData"
             style="color:#F00;width:100%; height:50px; float:left; clear:both;display:none; position:relative; text-align:center;  padding:80px 0 0; display:none;">
            您查询的日期沒有数据
        </div>

    </div>
    <span class="num_right"></span>


</div>
<!--统计信息-->
<div class="clear"></div>
<div class="height"></div>


<div class="contentTitle cont_title">
    <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BUSINESS.CONSTRUCTION">
        <a class="big_hover_title" id="serviceTitle">车辆施工</a>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BUSINESS.SALE">
        <a class="big_unhover_title" id="goodsSaleTitle">商品销售</a>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BUSINESS.WASH_CAR">
        <a class="big_unhover_title" id="carWashTitle">洗车</a>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
        <c:if test="<%=isMemberSwitchOn%>">
            <a class="big_unhover_title" id="memberTitle" style="display:none">会员</a>
        </c:if>
    </bcgogo:hasPermission>
</div>

<div class="clear"></div>
<bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BUSINESS.CONSTRUCTION">
    <div class="bus_stock add tb_add" id="serviceInfo">
      <table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="repairTable">
        <col width="70">
        <col width="80">
        <col width="70">
        <col width="50">
        <col width="55">
        <col width="50">
        <col width="55">
        <col width="75">
        <col width="70">
        <col width="60">
        <col width="50">
        <col width="50">
        <c:if test="${!fourSShopVersions}">
        <col width="50">
        <col width="50">
        </c:if>

        <col width="40">
        <tr class="tab_title">
          <td id="timeSort" class="Descending first-padding">单据日期<input type="button" onfocus="this.blur();"/></td>
          <td>单据号</td>
          <td>车辆</td>
          <td>施工费</td>
          <td>施工成本</td>
          <td>材料费</td>
          <td>材料成本</td>
          <td>其他费用/成本</td>
          <td id="brandSort" class="Descending">
            <div>单据总额<input type="button" onfocus="this.blur();"/></div>
          </td>
          <td>总成本</td>
          <td>实收</td>
          <td>挂账</td>
          <c:if test="${!fourSShopVersions}">

          <td>毛利</td>
          <td>毛利率</td>
          </c:if>
          <td style="border-right:none;" class="last-padding">优惠</td>
        </tr>
      </table>
      <div class="clear"></div>
      <div class="i_height"></div>
      <div style="color:#000000; font-weight:bold;">
        <div style="float:left;width:150px;">单据总额：<span id="repairTotal">0</span>元</div>

        本页小计:单据总额：<span id="repairPageTotal">0</span>元
        成本<span id="repairCostTotal">0</span>元
        实收<span id="repairSettleTotal">0</span>元
        挂账<span id="repairDebtTotal">0</span>元
        优惠<span id="repairDiscountTotal">0</span>元
        <c:if test="${!fourSShopVersions}">
        毛利<span id="repairProfitTotal">0</span>元
        </c:if>
          <span style="color:#6699cc; float:right; text-decoration:underline;cursor: pointer;" orderType="repair" id="print">打印</span>
      </div>
      <div class="height"></div>

      <jsp:include page="/common/pageAJAX.jsp">
        <jsp:param name="url" value="businessStat.do?method=getRepairOrderDetail"></jsp:param>
        <jsp:param name="jsHandleJson" value="initRepairOrder"></jsp:param>
        <jsp:param name="dynamical" value="dynamical1"></jsp:param>
        <jsp:param name="data"
                   value="{startPageNo:'1',maxRows:25,type:'day',dateStr:jQuery('#yearHid').val() + '-'+jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val(),arrayType:'timeDesc'}"></jsp:param>
      </jsp:include>
    </div>
</bcgogo:hasPermission>
<bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BUSINESS.SALE">
    <div class="bus_stock add tb_add" id="goodsSaleInfo" style="display: none;">
      <table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="salesOrder">
        <col width="70">
        <col width="80">
        <col width="65">
        <col width="70">
        <col/>
        <col width="55">
        <col width="55">
        <col width="85">
        <col width="70">
        <col width="50">
        <col width="50">
        <col width="50">
        <col width="50">
        <col width="50">
        <col width="40">
        <tr class="tab_title">
          <td id="timeSort2" class="Descending first-padding">单据日期<input type="button" onfocus="this.blur();"/></td>
          <td>单据号</td>
          <td>单据类型</td>
          <td>客户</td>
          <td>商品</td>
          <td>商品金额</td>
          <td>商品成本</td>
          <td>其他费用/成本</td>
          <td id="brandSort2" class="Ascending">
            <div>单据总额<input type="button" onfocus="this.blur();"/></div>
          </td>
          <td>实收</td>
          <td>挂账</td>
          <td>总成本</td>
          <td>毛利</td>
          <td>毛利率</td>
          <td style="border-right:none;" class="last-padding">优惠</td>
        </tr>
      </table>
      <div class="clear"></div>
      <div class="i_height"></div>
      <div style="color:#000000; font-weight:bold;">
        <div style="float:left;width:150px;">单据总额：<span id="saleTotal">0</span>元</div>
        本页小计:单据总额<span id="salePageTotal">0</span>元
        成本<span id="saleCostTotal">0</span>元
        实收<span id="saleSettleTotal">0</span>元
        挂账<span id="saleDebtTotal">0</span>元
        优惠<span id="saleDiscountTotal">0</span>元
        毛利<span id="saleProfitTotal">0</span>元
          <span style="color:#6699cc; float:right; text-decoration:underline;cursor: pointer;"  orderType="sale" id="print2">打印</span>
      </div>
      <div class="height"></div>
      <jsp:include page="/common/pageAJAX.jsp">
        <jsp:param name="url" value="businessStat.do?method=getSalesOrderDetail"></jsp:param>
        <jsp:param name="jsHandleJson" value="initSaleOrder"></jsp:param>
        <jsp:param name="dynamical" value="dynamical2"></jsp:param>
        <jsp:param name="data"
                   value="{startPageNo:'1',maxRows:25,type:'day',dateStr:jQuery('#yearHid').val() + '-'+jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val(),arrayType:'timeDesc'}"></jsp:param>
      </jsp:include>
</div>
</bcgogo:hasPermission>
<bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BUSINESS.WASH_CAR">
    <div class="bus_stock add tb_add" id="carWashInfo" style="display:none;">
      <table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="washCar">
        <col width="80">
        <col width="78">
        <col width="70">
        <col/>
        <col width="100">
        <col width="50">
        <col width="50">
        <col width="50">
        <col width="50">
        <tr class="tab_title">
          <td id="timeSort3" class="Descending first-padding">单据日期<input type="button" onfocus="this.blur();"/></td>
          <td>单据号</td>
          <td>车牌</td>
          <td>消费内容</td>
          <td>消费会员卡号</td>
          <td id="brandSort3" class="Ascending">
            <div>总额<input type="button" onfocus="this.blur();"/></div>
          </td>
          <td>实收</td>
          <td>挂账</td>
          <td style="border-right:none;" class="last-padding">优惠</td>
        </tr>
      </table>

      <div class="clear"></div>
      <div class="i_height"></div>
      <div style="color:#000000; font-weight:bold;">
        <div style="float:left;width:150px;">单据总额：<span id="washTotal">0</span>元</div>

        本页小计:单据总额<span id="washPageTotal">0</span>元
        成本<span id="washCostTotal">0</span>元
        实收<span id="washSettleTotal">0</span>元
        挂账<span id="washDebtTotal">0</span>元
        优惠<span id="washDiscountTotal">0</span>元
        <c:if test="${!fourSShopVersions}">
        毛利<span id="washProfitTotal">0</span>元
        </c:if>

          <span style="color:#6699cc; float:right; text-decoration:underline;cursor: pointer;" orderType="wash" id="print3">打印</span>
      </div>
      <div class="height"></div>

      <jsp:include page="/common/pageAJAX.jsp">
        <jsp:param name="url" value="businessStat.do?method=getWashOrderDetail"></jsp:param>
        <jsp:param name="jsHandleJson" value="initCarWash"></jsp:param>
        <jsp:param name="dynamical" value="dynamical3"></jsp:param>
        <jsp:param name="data"
                   value="{startPageNo:'1',maxRows:25,type:'day',dateStr:jQuery('#yearHid').val() + '-'+jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val(),arrayType:'timeDesc'}"></jsp:param>
      </jsp:include>
    </div>
</bcgogo:hasPermission>
<input type="hidden" id="totalRepairNum" />
<input type="hidden" id="totalSalesNum" />
<input type="hidden" id="totalWashNum" />
<div class="clear"></div>
<div class="shopping_btn" style="float:left; clear:left; margin-top:16px;margin-left: -26px;">
    <div class="btn_div_Img" id="exportDiv">
        <input id="export" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
        <div class="optWords">导出</div>
    </div>
    <img id="exporting" style="margin-left: 30px; display: none;" title="正在导出" alt="正在导出" src="images/loadinglit.gif">
</div>
</div>
</div>
<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>