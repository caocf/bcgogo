<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
            List agentsList = (List) request.getAttribute("agentsList");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>后台管理系统——代理商管理</title>
    <%-- styles --%>
  <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
  <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
  <script type="text/javascript" src="js/baclAgent.js"></script>
  <script type="text/javascript" src="js/backAgentPager.js"></script>
  <script type="text/javascript" src="js/backAgent.js"></script>
<script type="text/javascript" src="js/searchDefault.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      var month = $("#month").html().split("月")[0];
      var year = $("#year").html().split("年")[0];
      var year1 = $("#year1").html().split("年")[0];
      if (month * 1 > 12) {
          month = month * 1 - 12;
          year=year*1+1;
          $("#month").html( (month * 1) + "月" );
          $("#year").html( (year * 1) + "年" );
          $("#year1").html( (year * 1) + "年");
        } else {
          $("#month").html( (month * 1) + "月");
          $("#year").html( (year * 1) + "年");
          $("#year1").html( (year * 1) + "年");
        }
      if (month * 1 < 1) {
          month = month * 1 + 12;
          year=year*1-1;
          $("#month").html( (month * 1) + "月");
          $("#year").html( (year * 1) + "年");
        } else {
          $("#month").html( (month * 1) + "月");
          $("#year").html( (year * 1) + "年");
        }
      $("#monthRightIcon").click(function() {
        month = month * 1 + 1;
         if(month*1>12){
            month=month*1-12;
            year=year*1+1;
          }
        window.location="agents.do?method=getAgents&year="+year+"&month="+month+"&searchName="+$("#txt_shopOwner").val().toString().trim()+"&searchAgentCode="+$("#txt_shopName").val().toString().trim()+"&pageNo="+<%=request.getAttribute("pageNo")%>;
      });

      $("#monthLeftIcon").click(function() {
        month = month * 1 - 1;
         if(month*1<1){
            month=month*1+12;
            year=year*1-1;
          }
       window.location="agents.do?method=getAgents&year="+year+"&month="+month+"&searchName="+$("#txt_shopOwner").val().toString().trim()+"&searchAgentCode="+$("#txt_shopName").val().toString().trim()+"&pageNo="+<%=request.getAttribute("pageNo")%>;

      });
      $("#rightIconYear").live('click',function(){
           year=year*1+1;
          window.location="agents.do?method=getAgents&year="+year+"&month="+month+"&searchName="+$("#txt_shopOwner").val().toString().trim()+"&searchAgentCode="+$("#txt_shopName").val().toString().trim()+"&pageNo="+<%=request.getAttribute("pageNo")%>;
      });

       $("#leftIconYear").live('click',function(){
            year=year*1-1;
           window.location="agents.do?method=getAgents&year="+year+"&month="+month+"&searchName="+$("#txt_shopOwner").val().toString().trim()+"&searchAgentCode="+$("#txt_shopName").val().toString().trim()+"&pageNo="+<%=request.getAttribute("pageNo")%>;
      });
       $("#targetYearRight").live('click',function(){
           year1=year1*1+1;
           $("#year1").html((year1*1)+"年");
      });

       $("#targetYearLeft").live('click',function(){
            year1=year1*1-1;
           $("#year1").html((year1*1)+"年");
      });
    });

    $(document).ready(function(){
      var total=document.getElementsByName("total");
      var totalTarget=0;
      for(var i=0;i<total.length;i++){
        totalTarget=totalTarget*1+total[i].value*1;

      }
      $("#totalTarget").html(totalTarget);
    });

  </script>
</head>
<body>
<div class="main">
<!--头部-->
<div class="top">
  <div class="top_left">
    <div class="top_name">统购后台管理系统</div>
    <div class="top_image"></div>
    你好，<span>张三</span>|<a href="#">退出</a></div>
  <div class="top_right"><span>2011.11.23 14:01 星期三</span></div>
</div>
<!--头部结束-->
<div class="body">
<!--左侧列表-->
<div class="bodyLeft">
  <ul class="leftTitle">
    <li ><a href="<%=basePath%>beshop.do?method=shoplist" class="left_register">注册</a>
         <input type="button" class="btnNum" value=${shopCounts}>
    </li>
    <li><a href="#" class="left_shopping">商品</a><input type="button" class="btnNum" value="10"/></li>
    <li><a href="#" class="left_vehicle">车辆</a><input type="button" class="btnNum" value="8"/></li>
    <li><a href="#" class="left_recruit">招聘</a><input type="button" class="btnNum" value="5"/></li>
    <li><a href="<%=basePath%>beshop.do?method=getSms"  class="left_recharge">充值</a><input type="button" class="btnNum" value="28"/></li>
    <li class="left_hover"><a href="#" class="left_agaent">代理商</a><input type="button" class="btnNum" value="25"/></li>
    <li><a href="#" class="left_manage">后台管理</a><input type="button" class="btnNum" value="28"/></li>
    <li><a href="#" class="left_datamaintain">数据维护</a><input type="button" class="btnNum" value="5"/></li>
    <li><a href="shopConfig.do?method=shopIndividuation" class="left_shopConfig">店铺设置</a><input type="button" class="btnNum" value="0"/></li>

    <li><a href="print.do?method=toLeadPage" class="left_print">打印模板</a><input type="button" class="btnNum" value="0"/></li>
  </ul>
</div>
<!--左侧列表结束-->
<!--右侧内容-->
<div class="bodyRight">
<!--搜索-->
<!--搜索结束-->
<!--内容-->
<div class="rightMain clear">
<!--代理商-->
<div class="i_height"></div>
<div class="rightText">
  <div class="textLeft"></div>
  <div class="textBody"><input type="text" value="代理商编号" id="txt_shopName"/></div>
  <div class="textRight"></div>
</div>
<div class="rightText">
  <div class="textLeft"></div>
  <div class="textBody"><input type="text" value="代理商名称" id="txt_shopOwner"/></div>
  <div class="textRight"></div>
</div>
<div class="rightText">
  <div class="textLeft"></div>
  <%--<div class="textBody"><input type="text" value="业务员编号" id="txt_shopOwner"/></div>--%>
  <div class="textRight"></div>
</div>
<div class="rightText">
  <div class="textLeft"></div>
  <%--<div class="textBody"><input type="text" value="业务员姓名" id="txt_shopOwner"/></div>--%>
  <div class="textRight"></div>
</div>
<input type="button" class="rightSearch" value="搜 索" id="searchButton"/>
<input type="button" class="addAgent" value="新增代理商" id="addAgent"/>
<script type="text/javascript">
        $(document).ready(function() {
          $("#addAgent").click(function() {
            window.location = "agents.do?method=addAgent";
          });
        });
      </script>

<div class="i_height"></div>
<div class="rightTime">
  <div class="timeLeft"></div>
  <div class="timeBody">

    <div style="float:left;width:570px;">代理商</div>
    <div style="float:left;width:100px;">
      <div class="leftIcon" id="monthLeftIcon"><input type="button" onfocus="this.blur();"/></div>
      <span id="month">${month}月</span>

      <div class="rightIcon" id="monthRightIcon"><input type="button" onfocus="this.blur();"/></div>
    </div>
    <div style="float:left;width:110px;">
      <div class="leftIcon" id="leftIconYear"><input type="button" onfocus="this.blur();"/></div>
      <span id="year">${year}年</span>

      <div class="rightIcon" id="rightIconYear"><input type="button" onfocus="this.blur();"/></div>
    </div>

  </div>
  <div class="timeRight"></div>
</div>
<!--代理商结束-->
<!--table-->
<table cellpadding="0" cellspacing="0" class="back_tab">
  <col width="94">
        <col width="100">
        <col width="70">
        <col width="90">
        <col width="140">
        <col width="80">
        <col width="110">
        <col width="110">
        <col width="70">
  <thead>
  <tr>
    <th>代理商编码</th>
    <th>代理商名称</th>
    <th>负责人</th>
    <th>联系方式</th>
    <th>地址</th>
    <th>负责区域</th>
    <th>
      <div class="target">目标</div>
      <div class="target">完成</div>
    </th>
    <th>
      <div class="target">目标</div>
      <div class="target">完成</div>
    </th>
    <th style=" background-image:none;">操作</th>
  </tr>
  </thead>
            <tbody>

          <c:forEach items="${agentsList}" var="agents" varStatus="status">
            <c:choose>
              <c:when test="${status.index%2==1}">
                <tr class="agent_bg">
              </c:when>
              <c:otherwise>
                <tr>
              </c:otherwise>
            </c:choose>
            <%--<td>${status.index+1}</td>--%>
            <td title="${agents.agentId}">${agents.agentCode}</td>
            <td title="${agents.name}">${agents.name}</td>
            <%--<td title="${agents.address}">${agents.address}</td>--%>
            <td title="${agents.personInCharge}">${agents.personInCharge}</td>
            <td title="${agents.mobile}">${agents.mobile}</td>
            <td title="${agents.address}">${agents.address}</td>
            <td title="${agents.respArea}">${agents.respArea}</td>
            <td title="${agents.monthTarget}">${agents.monthTarget}</td>
            <%--<td title="${agents.seasonTarget}">${agents.seasonTarget}</td>--%>
            <td title="${agents.yearTarget}">${agents.yearTarget}</td>
            <td>
              <a class="font"
                 href="agents.do?method=detailAgents&agentId=${agents.id}&name=${agents.name}
&address=${agents.address}&mobile=${agents.mobile}&respArea=${agents.respArea}&personInCharge=${agents.personInCharge}
&monthTarget=${agents.monthTarget}&yearTarget=${agents.yearTarget}&agentCode=${agents.agentCode}&state=${agents.state}&year=${agents.year}">
                点击详情
              </a>
            </td>
            <%--<td><a href="beshop.do?method=shopaudit&shopId=${agents.id}" class="font">点击详情
</a></td>--%>

            </tr>
          </c:forEach>
          </tbody>
</table>
<!--table结束-->
<!--分页-->
<div class="i_leftBtn">
  <%--  <div class="fist_page"></div>
<div class="">1</div>
<div class="i_leftCountHover">2</div>
<div class="">3</div>
<div class="">4</div>
<div class="">5</div>
<div class="last_page"></div>   --%>
  <%
    Integer pageCount = (Integer) request.getAttribute("pageCount");
    Integer pageNo = (Integer) request.getAttribute("pageNo");
  %>
  <div class="fist_page"
       onclick="prePage('<%=request.getAttribute("agentId")%>','<%=request.getAttribute
("name")%>','<%=pageNo%>')"></div>
  <%

    if (pageCount != null && pageNo != null) {
      for (int m = 1; m <= pageCount; m++) {
        if (m == pageNo) {
  %>
  <div class="i_leftCountHover"><%=m%>
  </div>
  <%
        }
      }
    }
  %>
  <div class="last_page"
       onclick="nextPage('<%=request.getAttribute("agentId")%>','<%=request.getAttribute
("name")%>','<%=pageNo%>','<%=pageCount%>')"></div>
</div>

<!--分页结束-->
<div class="rightTime clear">
  <div class="timeLeft"></div>
  <div class="timeBody">
    整体目标及完成情况
    <div style="float:right;margin-right:10px;">
      <div class="leftIcon" id="targetYearLeft"><input type="button" onfocus="this.blur();"/></div>
      <span id="year1">2012年</span>

      <div class="rightIcon" id="targetYearRight"><input type="button" onfocus="this.blur();"/></div>
    </div>
  </div>
  <div class="timeRight"></div>
</div>
<!--代理商结束-->
<!--table-->
<table cellpadding="0" cellspacing="0" id="back_tab" class="back_tab">
  <col width="98">
  <col width="65">
  <col width="65">
  <col width="65">
  <col width="65">
  <col width="65">
  <col width="65">
  <col width="65">
  <col width="65">
  <col width="65">
  <col width="75">
  <col width="75">
  <col width="75">
  <col width="95">
  <thead>
  <tr>
    <th>季度</th>
    <th colspan="3">第一季度</th>
    <th colspan="3">第二季度</th>
    <th colspan="3">第三季度</th>
    <th colspan="3">第四季度</th>
    <th style=" background-image:none;"></th>
  </tr>
  <tr>
    <th>月份</th>
    <th>1月</th>
    <th>2月</th>
    <th>3月</th>
    <th>4月</th>
    <th>5月</th>
    <th>6月</th>
    <th>7月</th>
    <th>8月</th>
    <th>9月</th>
    <th>10月</th>
    <th>11月</th>
    <th>12月</th>
    <th style=" background-image:none;">累计</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td>目标</td>
    <c:forEach items="${totalList}" var="totals">
    <td>${totals.totalMonthTarget}</td>
    <input type="hidden" name="total" value="${totals.totalMonthTarget}"/>
    </c:forEach>
    <td id="totalTarget">0</td>
  </tr>
  <tr>
    <td>完成套数</td>
    <td><a href="#">12</a></td>
    <td><a href="#">35</a></td>
    <td><a href="#">50</a></td>
    <td><a href="#">35</a></td>
    <td><a href="#">25</a></td>
    <td><a href="#">15</a></td>
    <td><a href="#">20</a></td>
    <td><a href="#">13</a></td>
    <td><a href="#">24</a></td>
    <td><a href="#">34</a></td>
    <td><a href="#">35</a></td>
    <td><a href="#">35</a></td>
    <td>111</td>
  </tr>
  </tbody>
</table>
<!--table结束-->
</div>
<!--分页结束-->
<!--内容结束-->
<!--圆角-->

<div class="bottom_crile clear">
  <div class="crile"></div>
  <div class="bottom_x"></div>
  <div style="clear:both;"></div>
</div>
</div>
<!--圆角结束-->
</div>
<!--右侧内容结束-->
</div>
</div>
</body>
</html>