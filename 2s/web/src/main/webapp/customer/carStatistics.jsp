
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    //权限
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>车型统计</title>

  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/performanceStatistics<%=ConfigController.getBuildVersion()%>.css"/>

  <style>
    @media print {
      #printBtn, .statisticsRight, .m_topMain, .s_topMain, .title, .cusTitle, .i_mainTitle, .mainTitles, .title_statistics a.normalOne, .title_statistics a.normalTwo, .title_statistics a.normalThree, .title_statistics a.hoverThree, .title_statistics a.hoverTwo {display:none; }
      .i_main {margin: 0;width:650px !important;}
      .statisticsMain{width:650px;}
      .titleBody{width:632px  !important;}
      .bodyMain{width:650px !important;}
      #noTitle{width:40px !important;}
      #vsBrandTitle{width:140px !important;}
      #vsModelTitle{width:140px !important;}
      #vsWashTimesTitle{width:100px !important;}
      #vsRepairTimesTitle{width:100px !important;}
      #vsTotalTimesTitle{width:100px !important;}
    }
  </style>
  
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/modules/exporting.js"></script>
  <script type="text/javascript" src="js/module/bcgogoPieChart<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/stat/statSelect<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>

  <script type="text/javascript">
      defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.VEHICLE_STAT");

    var year = ${costStatConditionDTO.year};
    $("#yearHidden").val(year);
    var month = ${costStatConditionDTO.month};
    $("#monthHidden").val(month);
    var allYear = ${costStatConditionDTO.allYear};

    var total = ${total};

    var name = '';
    var dataAry = [];
    <c:forEach var="item" items="${vehicleServeMonthStatDTOs}" >
    name = '${item.brand} - ${item.model}';
    dataAry.push({
      name: name,
      y: ${item.totalTimes}/total
    });
    </c:forEach>

    $(function(){
      if (dataAry != null && dataAry.length >0) {
        if(${other.totalTimes}>0){
          dataAry.push({
            name: '${other.brand}',
            y:${other.totalTimes}/total
          });
        }
        var myChart = new pieChart({
          chartBasicConfig:{
            //用于显示图表的div
            container: 'chart_div',
            //标题文本
            titleText: '',
            //数据数组
            data: dataAry
          },
          //设置图表样式
          chartStyleConfig: {
            chartWidth:600,
            chartHeight:400,
            legendPosition:{x:-30,y:60},
            //条目间距
            itemMargin:{
              top:0,
              bottom:3
            }
          },
          seriseConfig:{
    //        name:'占比'
          }
        });
      }else{
        $("#chart_div").hide();
        $("#noData").show();
      }
      $("#carStatSubmit").bind("click", function(){
        $("#carStatConditionForm").submit();
        $(this).attr("disabled", true);
      });

      $("#printBtn").bind("click", function(){
        GLOBAL.Util.loadJsCssFile("styles/costStatPrint.css", "css");
        window.print();
      });

      window.onafterprint = function(){
        GLOBAL.Util.removeJsCssFile("styles/costStatPrint.css", "css");
      }
    });
  </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.SMS_SEND">
  <input type="hidden" id="smsSendPermission" value="${WEB_CUSTOMER_MANAGER_SMS_SEND}"/>
</bcgogo:permissionParam>

<input type="hidden" id="quanxian" value="<%=WebUtil.getUserGroupType(request)%>">
<div class="title">
    <div class="title_label">
        <ul>
        </ul>
    </div>
</div>
<div class="i_main clear">
  <jsp:include page="customerNavi.jsp" >
    <jsp:param name="currPage" value="vehicleServeStat" />
  </jsp:include>
  <div class="height"></div>
  <div class="clear"></div>
  <div class="statisticsMain">
    <div class="height"></div>
    <div class="statistics_Title"><span>${costStatConditionDTO.year} 年 <c:choose> <c:when test="${costStatConditionDTO.allYear}">全年</c:when><c:otherwise>${costStatConditionDTO.month} 月</c:otherwise></c:choose></span> 车型服务次数 TOP10</div>
    <div class="height"></div>
    <div id="chart_div" style="width:550px; height:400px; float:left; clear:both;"></div>
    <div id="noData"
         style="color:#F00;width:200px; height:200px; float:left; clear:both; padding:0px 0px 0px 150px; display:none;">
      您查询的日期沒有数据
    </div>
    <div class="statisticsRight">
      <form:form action="vehicleStat.do?method=carStatistics" modelAttribute="costStatConditionDTO" id="carStatConditionForm">
      <div class="statisticsTab">
        <div class="tabAll">
          统计年份:<div class="select" id="year"><span>2012年</span><a class="selImg"></a></div>
          <div id="div_years" class="div_year"  style="display:none;">
            <c:forEach items="${costStatConditionDTO.allYearOptions}" var="item">
            <a>${item}</a>
            </c:forEach>
          </div>
          <form:hidden id="yearHidden" path="year" />
        </div>
        <div class="tabAll">
          统计月份:<div class="select" id="month"><span>所有月份</span><a class="selImg"></a></div>
          <div id="div_months" class="div_year"  style="display:none;">
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
          <form:hidden id="monthHidden" path="monthStr" />
        </div>
        <div class="tabAll" style="padding:0 0 10px 70px;"><a class="buttonBig" id="carStatSubmit" >统&nbsp;计</a></div>
      </div>
      </form:form>

    </div>
    <div class="returnAlmost">次数总计：<span>${total}</span>次</div>
    <div class="clear"></div>
    <div class="statisticsWord">车型服务次数统计</div>
    <div class="clear"></div>
    <div class="table_title">
      <div class="tab_left"></div>
      <div class="titleBody">
        <div style="width:57px; float:left;" id="noTitle">NO</div>
        <div style="width:210px; float:left;" id="vsBrandTitle">车辆品牌</div>
        <div style="width:210px; float:left;" id="vsModelTitle">车型</div>
        <div style="width:160px; float:left;" id="vsWashTimesTitle">汽车美容次数</div>
        <div style="width:160px; float:left;" id="vsRepairTimesTitle">施工销售次数</div>
        <div style="width:160px; float:left;" id="vsTotalTimesTitle">总次数</div>
      </div>
      <div class="tab_right"></div>
      <table cellpadding="0" cellspacing="0" class="tab_Statistics" id="tabList">
        <col width="75">
        <col width="200">
        <col width="200">
        <col width="150">
        <col width="150">
        <col width="150">
        <c:forEach items="${vehicleServeMonthStatDTOs}" var="item" varStatus="status">
        <tr class="table-row-original">
          <td class="txt_left">${status.count}</td>
          <td>${item.brand}</td>
          <td>${item.model}</td>
          <td>${item.washTimes}</td>
          <td>${item.repairTimes}</td>
          <td>${item.totalTimes}</td>
        </tr>
        </c:forEach>
      </table>
    </div>
    <div class="clear"></div>
    <div class="height"></div>
      <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.VEHICLE_STAT_PRINT">
          <div class="btnPrint"><a id="printBtn" class="buttonBig">打&nbsp;印</a></div>
      </bcgogo:hasPermission>

  </div>
</div>


<div id="div_month" class="selectDegree" style="display:none;">
  <a>01</a>
  <a>02</a>
  <a>03</a>
  <a>04</a>
  <a>05</a>
  <a>06</a>
  <a>07</a>
  <a>08</a>
  <a>09</a>
  <a>10</a>
  <a>11</a>
  <a>12</a>
</div>

<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>