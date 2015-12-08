<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: lw
  Date: 12-10-30
  Time: 下午8:08
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>畅销商品</title>

  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/personnelRecruit<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/performanceStatistics<%=ConfigController.getBuildVersion()%>.css"/>

  <style>
  @media print {
    #printBtn, .statisticsRight, .m_topMain, .s_topMain, .title, .mainTitles, .title_statistics a.normalOne, .title_statistics a.normalTwo, .title_statistics a.normalThree, .title_statistics a.hoverThree, .title_statistics a.hoverTwo {display:none; }
    .i_main {margin: 0;width:650px !important;}
    .statisticsMain{width:650px;}
    .titleBody{width:632px  !important;}
    .bodyMain{width:650px !important;}

      #noTitle{width:30px !important;}
      #codeTitle{width:90px !important;}
      #nameTitle{width:90px !important;}
      #brandTitle{width:90px !important;}
      #modelTile{width:90px !important;}
      #vehicleTitle{width:90px !important;}
      #salesAmount{width:50px !important;}
      #salesTotal{width:70px !important;}
  }
  </style>
  <%@include file="/WEB-INF/views/header_script.jsp" %>

  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/modules/exporting.js"></script>
  <script type="text/javascript" src="js/goodSalesStatistics<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/stat/statSelect<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/module/bcgogoPieChart<%=ConfigController.getBuildVersion()%>.js"></script>

  <script type="text/javascript">
      defaultStorage.setItem(storageKey.MenuUid, "SALES_STAT_GOOD_SALE_COST");

    var year = ${salesStatCondition.year};
    $("#yearHidden").val(year);

    var type = jQuery("#way").text();
    var result = ${result};

    var dataAry = [];
    <c:forEach var="item" items="${itemDTOs}" >
    dataAry.push({
      name: '${item.queryResultStr}',
      y: ${item.queryResult}/result
    });
    </c:forEach>

    <c:forEach var="item" items="${other}" >
    dataAry.push({
      name: '${item.queryResultStr}',
      y: ${item.queryResult}/result
    });
    </c:forEach>
    $(function() {
      if (dataAry != null && dataAry.length > 0) {

        var myChart = new pieChart({
          chartBasicConfig:{
            //用于显示图表的div
            container: 'chart_div',
            //标题文本
            titleText: '',
            //数据数组
            data: dataAry
          },

          chartStyleConfig: {
            chartWidth:680,
            chartHeight:400,
            legendPosition:{x:0,y:50},
            legendWidth:320,
            //条目间距
            itemMargin:{
              top:0,
              bottom:3
            }
          },
          seriseConfig:{
          }
        });
        myChart.setSize(650,400);
      } else {
        $("#chart_div").css('display', 'none');
        $("#noData").css('display', 'block');
      }
    });

  </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <div class="mainTitles clear">
    <jsp:include page="statNavi.jsp">
        <jsp:param name="currPage" value="goodSaleCost"/>
    </jsp:include>
    <div class="titleWords">畅销/滞销品</div>
    <bcgogo:hasPermission permissions="WEB.STAT.GOOD_BAD_STAT.GOOD&&WEB.STAT.GOOD_BAD_STAT.BAD">
        <div class="titleList">
            <a class="click" action-type="menu-click" menu-name="SALES_STAT_GOOD_SALE_COST" id="goodSales">畅销商品</a>
            <a class="" action-type="menu-click" menu-name="SALES_STAT_BAD_SALE_COST" id="badSales">滞销商品</a>
        </div>
    </bcgogo:hasPermission>

  </div>
  <div class="clear"></div>
  <div class="statisticsMain">
    <div class="height"></div>
    <div class="statistics_Title"><span>${salesStatCondition.year} 年<c:choose> <c:when
        test="${salesStatCondition.allYear}">全年</c:when><c:otherwise>${salesStatCondition.monthStr}</c:otherwise></c:choose></span>畅销品TOP10
    </div>
    <div class="height"></div>
    <div id="chart_div" style="width:550px; height:400px; float:left; clear:both;"></div>
    <div id="noData"
         style="color:#F00;width:200px; height:200px; float:left; clear:both; padding:0px 0px 0px 150px; display:none;">
      您查询的日期沒有数据
    </div>
    <div class="statisticsRight">
      <form:form action="salesStat.do?method=getGoodSaleCost" modelAttribute="salesStatCondition"
                 id="salesStatConditionForm">
        <div class="statisticsTab">
          <div class="tabAll">
            统计年份:
            <div class="select" id="year"><span>${salesStatCondition.year}年</span><a class="selImg"></a></div>
            <div id="div_years" class="div_year" style="display:none;">
              <a>2015年</a>
              <a>2014年</a>
              <a>2013年</a>
              <a>2012年</a>
            </div>
            <form:hidden id="yearHidden" path="year"/>
          </div>
          <div class="tabAll">
            统计月份:
            <div class="select" id="month"><span>${salesStatCondition.monthStr}</span><a class="selImg"></a></div>
            <div id="div_months" class="div_year" style="display:none;">
              <a>1月</a>
              <a>2月</a>
              <a>3月</a>
              <a>4月</a>
              <a>5月</a>
              <a>6月</a>
              <a>7月</a>
              <a>8月</a>
              <a>9月</a>
              <a>10月</a>
              <a>11月</a>
              <a>12月</a>
              <a>所有月份</a>
            </div>
            <form:hidden id="monthHidden" path="monthStr" value="${salesStatCondition.monthStr}"/>
          </div>

          <div class="tabAll">
            统计方式:
            <div class="select" id="way"><span>${salesStatCondition.moneyOrAmount}</span><a class="selImg"></a></div>
            <div id="div_way" class="div_year" style="display:none;">
              <a>按金额统计</a>
            </div>
            <form:hidden id="queryWay" path="moneyOrAmount"/>
          </div>

          <div class="tabAll" style="padding-bottom:20px;padding-left:70px;"><a class="buttonBig" id="costStatSubmit">统&nbsp;计</a></div>
        </div>
      </form:form>

    </div>
    <div class="goodSalesStat">销售总金额/数量：<span>${total}</span>元/<span id="totalAmount">${totalAmount}</span></div>
    <div class="statisticsWord"><span>${salesStatCondition.year} 年<c:choose> <c:when
        test="${salesStatCondition.allYear}">全年</c:when><c:otherwise>${salesStatCondition.monthStr}</c:otherwise></c:choose></span>畅销品销售额/销售量TOP10
    </div>
    <div class="clear"></div>
      <table cellpadding="0" cellspacing="0" class="tab_Statistics" id="tabList">
        <col width="37">
        <col width="100">
        <col width="200">
        <col width="115">
        <col width="200">
        <col width="165">
        <col width="100">
        <col width="90">
          <tr class="table_title">
            <th class="txt_left first-padding">NO</th>
            <th class="txt_left">商品编号</th>
            <th class="txt_left">品 名</th>
            <th class="txt_left">品牌/产地</th>
            <th class="txt_left">规格/型号</th>
            <th class="txt_left">车型/车辆品牌</th>
            <th>销售量</th>
            <th class="last-padding">销售金额</th>
          </tr>
        <c:forEach items="${itemDTOs}" var="item" varStatus="status">
          <tr class="table-row-original">
            <td title="${status.count}" class="txt_left first-padding">${status.count}</td>
            <td title="${item.commodityCode}" class="txt_left">${item.commodityCode}</td>
            <td title="${item.name}" class="txt_left">${item.name}</td>
            <td title="${item.brand}" class="txt_left">${item.brand}</td>
            <td title="${item.spec}" class="txt_left">${item.spec}/${item.model}</td>
            <td title="${item.productVehicleModel}" class="txt_left">${item.productVehicleModel}/${item.productVehicleBrand}</td>
            <td title="${item.salesAmount}" align="right">${item.salesAmount}</td>
            <td title="${item.salesTotal}" align="right" class="last-padding">${item.salesTotal}</td>
          </tr>
        </c:forEach>
      </table>
    <div class="clear"></div>
    <div class="height"></div>
      <bcgogo:hasPermission permissions="WEB.STAT.GOOD_BAD_STAT.GOOD_PRINT">
          <div class="btnPrint" id="printBtn"><a class="buttonBig">打&nbsp;印</a></div>
      </bcgogo:hasPermission>
  </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
