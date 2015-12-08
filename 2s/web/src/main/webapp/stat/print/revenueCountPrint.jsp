<%--
  Created by IntelliJ IDEA.
  User: cfl
  Date: 12-6-6
  Time: 上午10:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="/WEB-INF/views/includes.jsp" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <%--<meta http-equiv="X-UA-Compatible" content="IE=7"/>--%>
    <title>营业统计打印单</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/print<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/printShow<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/revenueCount<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $().ready(function() {
            window.print();
            window.close();
        });

    </script>
</head>
<body class="bodyMain">
<!--头部-->
<div id="div_brand" class="i_scroll"  style="display:none;">
        <div class="Container">
          <div id="Scroller-1">
           <div class="Scroller-Container">
             <a>凯美瑞</a>
             <a>凯美瑞</a>
             <a>凯美瑞</a>
             <a id="div_alertmore">更多</a>
<!--             <a>凯美瑞</a>
             <a>凯美瑞</a>
             <a>凯美瑞</a>
             <a>凯美瑞</a>
             <a>凯美瑞</a>
             <a>凯美瑞</a>-->
           </div>
          </div>
        </div>
          <div class="i_scrollBar" id="Scrollbar-Container">
            <div class="Scrollbar-Up"></div>
            <div  class="Scrollbar-Track">
              <img src="images/b_03.png" class="Scrollbar-Handle"/>
            </div>
            <div class="scroll-bottom"></div>
          </div>
</div>
<!--头部结束-->
<!--内容-->
<div class="print_cont reve_count">
	<h3>${shopName}</h3>
	<!--第一部分-->
    <h5>营收统计</h5>
  <div class="i_searchTitle  clear">
    	</label><label class="time_rev">${startDateStr} 至 ${endDateStr}</label>
    </div>
    <!--第一部分结束-->

   <!--第二部分-->
      <table cellpadding="0" cellspacing="0" class="table2">
        <col />
        <col width="65" />
        <col width="65" />
        <col width="65" />
        <col width="59" />
        <col width="70" />
        <col width="70" />
        <col width="70" />
        <col width="100" />
        <col width="100" />
        <tr>
        <td rowspan="2">项目</td>
        <td colspan="3">营业收入</td>
         <td rowspan="2">商品成本</td>
        <td  colspan="3">支出明细</td>
         <td rowspan="2">毛利</td>
         <td rowspan="2">毛利润</td>
        </tr>
        <tr>
        <td>洗车</td>
         <td>车辆施工</td>
        <td>商品销售</td>
         <td>房租</td>
         <td>人工</td>
        <td>水电杂项</td>
        </tr>
         <tr class="count_tb">
        <td >金额<br/>(单位：元)</td>
        <td>${bizStatPrintDTO.wash}</td>
         <td>${bizStatPrintDTO.service}</td>
        <td>${bizStatPrintDTO.sales}</td>
         <td>${bizStatPrintDTO.productCost}</td>
        <td>${bizStatPrintDTO.rent}</td>
        <td>${bizStatPrintDTO.labor}</td>
        <td>${bizStatPrintDTO.other}</td>
        <td>${bizStatPrintDTO.grossProfit}</td>
        <td>${bizStatPrintDTO.grossProfitPercent}</td>
        </tr>
        </table>
<!--内容结束-->
</div>

</body>
</html>