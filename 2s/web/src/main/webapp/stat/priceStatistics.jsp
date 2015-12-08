<%@ page import="com.bcgogo.txn.dto.PriceFluctuationStatDTO" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    List<PriceFluctuationStatDTO> result = (List<PriceFluctuationStatDTO>) request.getAttribute("priceFluctuationStatDTOList");
    List<Map<String,String>> otherList = (List<Map<String,String>>)request.getAttribute("productInfoList");
    String dateRange = (String)request.getAttribute("dateRange");
    String jsonChartData = (String)request.getAttribute("chartData");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>价格波动</title>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/performanceStatistics<%=ConfigController.getBuildVersion()%>.css"/>
<style type="text/css">
    #tabList{
        width: 100%;
        cursor: pointer;
    }
    #tabList td{
        overflow: hidden;
        white-space:nowrap;
        text-overflow:ellipsis;
    }
</style>
<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.src.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/modules/exporting.js"></script>
<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogoPieChart<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid, "COST_STAT_GET_PRICE_STAT");

var preIndex = -1;
var chart;

$().ready(function(){
    //默认加载折线图
    chart = new Highcharts.Chart({
        chart: {
            renderTo: 'LineChart_div',
            type: 'spline'
        },
        title: {
            text: '<%=dateRange%> 采购价格波动图'
        },
        subtitle: {
            text: '0表示当月未采购'
        },
        xAxis: {
            type: 'datetime',
            dateTimeLabelFormats: {
                month: '%e. %b',
                year: '%b'
            }
        },
        yAxis: {
            title: {
                text: '日平均价 (元)'
            },
            min: 0
        },
        tooltip: {
            formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                    Highcharts.dateFormat('%e. %b', this.x) +': '+ this.y +'元';
            }
        },
        series: [{
            name: '<%=otherList.size()==0?"":otherList.get(0).get("name")%>',
            data: <%=jsonChartData%>
        }]
    });
    tableUtil.tableStyle('#tabList','.table_title');

    $("#printBtn").bind("click", function(){
      chart.setSize(650,240,true);
      GLOBAL.Util.loadJsCssFile("styles/costStatPrint.css", "css");
      setTimeout(function(){
          window.print();
      },300);
    });

    window.onafterprint = function(){
      chart.setSize(981,300,true);
      GLOBAL.Util.removeJsCssFile("styles/costStatPrint.css", "css");
    }
});

$("tr").live("click",function(){

    //获取当前行的品名
    var name = $(this).children().eq(2).html();
    //获取当前行的productId
    var productId = $(this).children().eq(0).children().eq(0).val();
    var jsonStr = APP_BCGOGO.Net.syncGet({url:"costStat.do?method=getPriceFluctuationLineChartData", data:{productId:productId}, dataType:"json"});
    //点击加载折线图
    chart = new Highcharts.Chart({
        chart: {
            renderTo: 'LineChart_div',
            type: 'spline'
        },
        title: {
            text: '<%=dateRange%> 采购价格波动图'
        },
        subtitle: {
            text: '0表示当月未采购'
        },
        xAxis: {
            type: 'datetime',
            dateTimeLabelFormats: {
                month: '%e. %b',
                year: '%b'
            }
        },
        yAxis: {
            title: {
                text: '日平均价 (元)'
            },
            min: 0
        },
        tooltip: {
            formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                    Highcharts.dateFormat('%e. %b', this.x) +': '+ this.y +'元';
            }
        },
        series: [{
            name: name,
            data: jsonStr
        }]
    });
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
            <jsp:param name="currPage" value="priceStat"/>
        </jsp:include>
    </div>
  <div class="clear"></div>
  <div class="statisticsMain">
    <div class="height"></div>
    <div class="statistics_Title"><%=dateRange%> 采购商品TOP10列表</div>
    <div style="width:980px;" class="titleBody">
       <table cellpadding="0" cellspacing="0" class="tab_Statistics" id="tabList" style="float:left;table-layout:fixed;">
        <col width="6%">
        <col width="10%">
        <col width="13%">
        <col width="13%">
        <col width="19%">
        <col width="19%">
        <col width="10%">
        <col width="10%">
          <tr class="table_title">
            <th style="padding-left: 15px;">NO</th>
            <th>商品编号</th>
            <th>品名</th>
            <th>品牌/产地</th>
            <th>规格/型号</th>
            <th>车辆品牌/车型</th>
            <th>采购次数</th>
            <th>采购总金额</th>
          </tr>
        <%
            for(int i=0;i<result.size();i++){
        %>
           <tr class="table-row-original">
               <td style="padding-left: 15px;"><%=i+1%><input type="hidden" value="<%=result.get(i).getProductId()%>"/></td>
               <td><%=otherList.get(i).get("commodityCode")%></td>
               <td><%=otherList.get(i).get("name")%></td>
               <td><%=otherList.get(i).get("brand")%></td>
               <td><%=otherList.get(i).get("spec")%>/<%=otherList.get(i).get("model")%></td>
               <td><%=otherList.get(i).get("vehicleBrand")%>/<%=otherList.get(i).get("vehicleModel")%></td>
               <td><%=result.get(i).getTimes()%></td>
               <td><%=String.format("%.2f",result.get(i).getTotal()).toString()%></td>
           </tr>
        <%
            }
        %>
      </table>
      <div class="height"></div>
      <div class="clear"></div>
      <div id="LineChart_div" style="float: left;"></div>
    </div>
    <div class="height"></div>
    <div class="clear"></div>
    <bcgogo:hasPermission permissions="WEB.STAT.PURCHASE_ANALYST.PRICE_PRINT">
        <div class="btnPrint"><a class="buttonBig" id="printBtn">打&nbsp;印</a></div>
        <div class="height"></div>
        <div class="clear"></div>
    </bcgogo:hasPermission>
  </div>
</div>

<!--弹出框-->
<div id="mask"  style="display:block;position: absolute;">
</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="900px" height="450px" frameborder="0" src=""></iframe>

<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>