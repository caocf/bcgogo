<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<%--<meta http-equiv="X-UA-Compativle" content="IE-EmulateIE7"/>--%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>成本</title>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/performanceStatistics<%=ConfigController.getBuildVersion()%>.css"/>
<style>
  @media print {
    #printBtn, .statisticsRight, .m_topMain, .s_topMain, .title, .mainTitles, .title_statistics a.normalOne, .title_statistics a.normalTwo, .title_statistics a.normalThree, .title_statistics a.hoverThree, .title_statistics a.hoverTwo {display:none; }
    .i_main {margin: 0;width:650px !important;}
    .statisticsMain{width:650px;}
    .titleBody{width:632px  !important;}
    .bodyMain{width:650px !important;}
    #noTitle{width:40px !important;}
    #nameTitle{width:90px !important;}
    #brandTitle{width:130px !important;}
    #modelTitle{width:100px !important;}
    #vBrandTitle{width:100px !important;}
    #amountTitle{width:90px !important;}
    #totalTitle{width:70px !important;}
  }
</style>
  
<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>

<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/modules/exporting.js"></script>
<script type="text/javascript" src="js/module/bcgogoPieChart<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/stat/statSelect<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">

    defaultStorage.setItem(storageKey.MenuUid, "COST_STAT_GET_COST_STAT");

  var year = ${costStatConditionDTO.year};
  $("#yearHidden").val(year);
  var month = ${costStatConditionDTO.month};
  $("#monthHidden").val(month);
  var allYear = ${costStatConditionDTO.allYear};

  var total = ${total};

  var nameSelected = ${costStatConditionDTO.nameSelected};
  var brandSelected = ${costStatConditionDTO.brandSelected};
  var vehicleModelSelected = ${costStatConditionDTO.vehicleModelSelected};

  var dataAry = [];

  var name = '';
  <c:forEach var="item" items="${purchaseInventoryStatDTOs}" >
  if(nameSelected){
    name = '${item.productName}';
  }
  if(brandSelected && ${item.productBrand!=""}){
    name += ' - ' +'${item.productBrand}';
  }
  if(vehicleModelSelected && (${item.vehicleBrand!="" || item.vehicleModel!=""})){
    name += ' - ' + '${item.vehicleBrand}' + '/' +'${item.vehicleModel}';
  }
  dataAry.push({
    name: name,
    y: ${item.totalStr}/total
  });
  </c:forEach>

  $(function(){
    $("#costStatSubmit").bind("click", function(){
      $("#costStatConditionForm").submit();
      $(this).attr("disabled", true);
    });

    if (dataAry != null && dataAry.length > 0) {
      dataAry.push({
        name: '${other.productName}',
        y:${other.totalStr}/total
      });
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
          chartWidth:670,
          chartHeight:400,
          legendPosition:{x:0,y:60},
          legendWidth:300,
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

<div class="i_main clear">
    <div class="mainTitles clear">
        <jsp:include page="statNavi.jsp">
            <jsp:param name="currPage" value="costStat"/>
        </jsp:include>
        <jsp:include page="costStatNavi.jsp">
            <jsp:param name="currPage" value="costStat"/>
        </jsp:include>
    </div>
  <div class="clear"></div>
  <div class="statisticsMain">
    <div class="height"></div>
    <div class="statistics_Title"><span>${costStatConditionDTO.year} 年<c:choose> <c:when test="${costStatConditionDTO.allYear}">全年</c:when><c:otherwise>${costStatConditionDTO.month} 月</c:otherwise></c:choose></span> 商品采购 TOP10</div>
    <div class="height"></div>
    <div id="chart_div" style="width:550px; height:400px; float:left; clear:both;"></div>
    <div id="noData"
         style="color:#F00;width:200px; height:200px; float:left; clear:both; padding:0px 0px 0px 150px; display:none;">
      您查询的日期沒有数据
    </div>
    <div class="statisticsRight">
    <form:form action="costStat.do?method=getCostStat" modelAttribute="costStatConditionDTO" id="costStatConditionForm">
	  <div class="statisticsTab">
        <div class="tabAll">
          统计年份:<div class="select" id="year"><span>2015年</span><a class="selImg"></a></div>
          <div id="div_years" class="div_year"  style="display:none;">
            <a>2015年</a>
            <a>2014年</a>
            <a>2013年</a>
            <a>2012年</a>
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
        <div class="tabAll">
          统计条件:
          <ul>
            <li><input type="checkbox" value="fakeName" id="fakeName" disabled="disabled" checked="checked"><label>品名</label>
              <form:checkbox path="queryFields" value="name" onclick="this.checked=!this.checked" style="display:none;"/></li>
            <li><form:checkbox path="queryFields" label="品牌/产地" value="brand" /></li>
            <li><form:checkbox path="queryFields" label="适合车型" value="vehicle_model" /></li>
          </ul>
        </div>
        <div class="tabAll" style="padding-bottom:20px;padding-left:70px;">
          <a class="buttonBig" id="costStatSubmit">统&nbsp;计</a>
      </div>
      </div>
      </form:form>


    </div>
    <div class="returnAlmost">金额总计：<span>${total}</span>元</div>
    <div class="statisticsWord">${costStatConditionDTO.year} 年 <c:choose> <c:when test="${costStatConditionDTO.allYear}">全年</c:when><c:otherwise>${costStatConditionDTO.month} 月</c:otherwise></c:choose> 商品采购TOP10</div>
    <div class="clear"></div>
    <div class="table_title">
       <div class="tab_left"></div>
       <div class="titleBody">
        <div id="noTitle" style="width:55px; float:left;">NO</div>
        <div id="nameTitle" style="width:150px; float:left;">品&nbsp;名</div>
        <div id="brandTitle" style="width:200px; float:left;">品牌/产地</div>
        <div id="modelTitle" style="width:170px; float:left;">车&nbsp;型</div>
        <div id="vBrandTitle" style="width:140px; float:left;">车辆品牌</div>
        <div id="amountTitle" style="width:120px; float:left;">数&nbsp;量</div>
        <div id="totalTitle" style="width:70px; float:left;">金&nbsp;额</div>
       </div>
       <div class="tab_right"></div>
       <table cellpadding="0" cellspacing="0" class="tab_Statistics" id="tabList" >
        <col width="75">
        <col width="160">
        <col width="200">
        <col width="180">
        <col width="130">
        <col width="120">
        <col width="130">
        <c:forEach items="${purchaseInventoryStatDTOs}" var="item" varStatus="status">
        <tr class="table-row-original">
          <td  class="txt_left">${status.count}</td>
          <td>${item.productName}</td>
          <td>${item.productBrand}</td>
          <td>${item.vehicleBrand}</td>
          <td>${item.vehicleModel}</td>
          <td>${item.amountStr}</td>
          <td>${item.totalStr}</td>
        </tr>
        </c:forEach>
      </table>
    </div>
    <div class="height"></div>
    <div class="clear"></div>
      <bcgogo:hasPermission permissions="WEB.STAT.PURCHASE_ANALYST.COST_PRINT">
        <div class="btnPrint"><a class="buttonBig" id="printBtn">打&nbsp;印</a></div>
      </bcgogo:hasPermission>
    </div>
</div>


<div id="div_month" class="selectDegree"  style="display:none;">
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