<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>后台管理系统——商品</title>
    <%-- styles --%>
  <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
  <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
  <link rel="stylesheet" type="text/css" href="styles/agent_son.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
  <script type="text/javascript" src="js/backAgentAJax.js"></script>
             <script type="text/javascript" src="js/searchDefault.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {

      var total = document.getElementsByName("monthTarget");
      var totalTarget = 0;
      for (var i = 0; i < total.length; i++) {
        totalTarget = totalTarget * 1 + total[i].value * 1;

      }
      $("#totalTarget").html(totalTarget);
      $("#yearTarget").val(totalTarget);
      $(".monthTarget").live('blur', function() {
        var totalValue = 0;
        var monthValue = document.getElementsByName("monthTarget");
        for (var i = 0; i < monthValue.length; i++) {
          totalValue = totalValue * 1 + monthValue[i].value * 1;
        }
        $("#yearTarget").val(totalValue);
        $("#totalTarget").html(totalValue);
      });


    });
  </script>
</head>
<body>
<input type="hidden" value="${agentId}" id="agentId"/>
<form id="myform" action="agents.do?method=updateAgent" method="POST">
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
    <li><a href="#" class="left_register">注册</a>
      <input type="button" class="btnNum" value="13"/>
    </li>
    <li class="left_hover"><a href="#" class="left_shopping">商品</a><input type="button" class="btnNum" value="10"/></li>
    <li><a href="#" class="left_vehicle">车辆</a><input type="button" class="btnNum" value="8"/></li>
    <li><a href="#" class="left_recruit">招聘</a><input type="button" class="btnNum" value="5"/></li>
    <li><a href="#" class="left_recharge">充值</a><input type="button" class="btnNum" value="28"/></li>
    <li><a href="#" class="left_agaent">代理商</a><input type="button" class="btnNum" value="25"/></li>
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
<div class="rightText">
  <div class="textLeft"></div>
  <div class="textBody"><input type="text" value="店铺名" id="txt_shopName"/></div>
  <div class="textRight"></div>
</div>
<div class="rightText">
  <div class="textLeft"></div>
  <div class="textBody"><input type="text" value="店主" id="txt_shopOwner"/></div>
  <div class="textRight"></div>
</div>
<div class="rightText">
  <div class="textLeft"></div>
  <div class="textAddressbody"><input type="text" value="地址" id="txt_address"/></div>
  <div class="textRight"></div>
</div>
<div class="rightText">
  <div class="textLeft"></div>
  <div class="textBody"><input type="text" value="手机/电话" id="txt_phone"/></div>
  <div class="textRight"></div>
</div>
<input type="button" class="rightSearch" value="搜 索"/>
<!--搜索结束-->
<!--内容-->
<div class="rightMain clear">
<!--代理商基本信息-->
<!--代理商-->
<div class="rightTime">
  <div class="timeLeft"></div>
  <div class="timeBody">代理商基本信息</div>
  <div class="timeRight"></div>
</div>
<!--代理商结束-->
<!--table-->

<table cellpadding="0" cellspacing="0" class="agent_tb">
  <col width="95">
  <col width="210">
  <col width="97">
  <col width="137">
  <col width="97">
  <col width="136">
  <col width="74">
  <tr>
    <td>代理商名</td>
    <td class="agent_color"> ${name}</td>
    <td>负责人</td>
    <td class="agent_color">${personInCharge}</td>
    <td>代理商编码</td>
    <td class="agent_color">${agentCode}</td>
    <td>状态</td>
  </tr>
  <tr>
    <td>地址</td>
    <td class="agent_color"> ${address}</td>
    <td>联系方法</td>
    <td class="agent_color">${mobile}</td>
    <td>负责区域</td>
    <td class="agent_color">${respArea}</td>
    <td>
      <select name="state" id="state">
          <option selected="selected" value="1">有效</option>
          <option value="0">停止</option>
      </select>
    </td>
  </tr>
</table>
<!--table结束-->
<!--代理商基本信息结束-->
<!--业务员信息-->
<!--代理商-->
<div class="rightTime  salesman clear">
  <div class="timeLeft"></div>
  <div class="timeBody">
    <div style="float:left;width:435px;">业务员信息</div>
    <div style="float:left;width:185px;">
      <div class="leftIcon" id="monthLeft"><input type="button" onfocus="this.blur();"/></div>
      <span id="month">1月</span>

      <div class="rightIcon" id="monthRight"><input type="button" onfocus="this.blur();"/></div>
    </div>
    <div style="float:left;width:110px;">
      <div class="leftIcon" id="yearLeft"><input type="button" onfocus="this.blur();"/></div>
      <span id="year">2012年</span>

      <div class="rightIcon" id="yearRight"><input type="button" onfocus="this.blur();"/></div>
    </div>
  </div>
  <div class="timeRight"></div>
</div>
<!--代理商结束-->
<!--table-->
<table cellpadding="0" cellspacing="0" class="back_tabSale" id="salesManTable">
  <col width="118">
  <col width="108">
  <col width="158">
  <col width="200">
  <col width="200">
  <col width="63">
  <thead>
  <tr>
    <th>业务员编码</th>
    <th>姓名</th>
    <th>联系方式</th>
    <th>
      <div class="target">目标</div>
      <div class="target">完成</div>
    </th>
    <th>
      <div class="target">目标</div>
      <div class="target">完成</div>
    </th>
    <th>状态</th>
  </tr>
  </thead>
  <tbody>
  <%--<c:forEach items="${salesManDTOList}" var="salesMans" varStatus="status">--%>
    <%--<c:choose>--%>
      <%--<c:when test="${status.index%2==1}">--%>
        <%--<tr class="agent_bg">--%>
      <%--</c:when>--%>
      <%--<c:otherwise>--%>
        <%--<tr>--%>
      <%--</c:otherwise>--%>
    <%--</c:choose>--%>

    <%--<td>${salesMans.salesManCode}</td>--%>
    <%--<td>${salesMans.name}</td>--%>
    <%--<td>${salesMans.mobile}</td>--%>
    <%--<td>${salesMans.monthTarget}&nbsp;100</td>--%>
    <%--<td>${salesMans.yearTarget}&nbsp;1000</td>--%>
    <%--<td>${salesMans.state}</td>--%>

    <%--</tr>--%>
  <%--</c:forEach>--%>
  <%--<tr>--%>
  <%--<td>A0001</td>--%>
  <%--<td> 张梓健</td>--%>
  <%--<td>13738940920</td>--%>
  <%--<td>--%>
  <%--<div class="target">2</div>--%>
  <%--<div class="target">2</div>--%>
  <%--</td>--%>
  <%--<td>--%>
  <%--<div class="target">2</div>--%>
  <%--<div class="target">2</div>--%>
  <%--</td>--%>
  <%--<td>在职</td>--%>
  <%--</tr>--%>
  <%--<tr class="agent_bg">--%>
  <%--<td>A0002</td>--%>
  <%--<td> 辛东峰</td>--%>
  <%--<td>13983920980</td>--%>
  <%--<td>--%>
  <%--<div class="target">2</div>--%>
  <%--<div class="target">2</div>--%>
  <%--</td>--%>
  <%--<td>--%>
  <%--<div class="target">2</div>--%>
  <%--<div class="target">2</div>--%>
  <%--</td>--%>
  <%--<td>离职</td>--%>
  <%--</tr>--%>
  </tbody>
</table>
<!--table结束-->
<!--分页-->
<%--<div class="i_leftBtn">--%>
  <%--  <div class="fist_page"></div>
<div class="">1</div>
<div class="i_leftCountHover">2</div>
<div class="">3</div>
<div class="">4</div>
<div class="">5</div>
<div class="last_page"></div>   --%>
  <%--<%--%>
    <%--Integer pageCount = (Integer) request.getAttribute("pageCount");--%>
    <%--Integer pageNo = (Integer) request.getAttribute("pageNo");--%>
  <%--%>--%>
  <%--<div class="lastPage"--%>
       <%--onclick="prePage1('<%=pageNo%>')"></div>--%>
  <%--<%--%>

    <%--if (pageCount != null && pageNo != null) {--%>
      <%--for (int m = 1; m <= pageCount; m++) {--%>
        <%--if (m == pageNo) {--%>
  <%--%>--%>
  <%--<div class="i_leftCountHover"><%=m%>--%>
  <%--</div>--%>
  <%--<%--%>
        <%--}--%>
      <%--}--%>
    <%--}--%>
  <%--%>--%>
  <%--<div class="nextPage"--%>
       <%--onclick="nextPage1('<%=pageNo%>','<%=pageCount%>')"></div>--%>
<%--</div>--%>
  <%--<div class="i_leftBtn i_bottom" id="pageNo_id">--%>
      <%--<div class="lastPage">上一页</div>--%>
      <%--<div class="onlin_his" id="thisPageNo">1</div>--%>
      <%--<div class="nextPage">下一页</div>--%>
    <%--</div>--%>
    <div class="i_leftBtn" id="pageNo_id">
          <div class="lastPage">上一页</div>
          <div id="thisPageNo">1</div>
          <div class="nextPage">下一页</div>
    </div>
<!--分页结束-->
<!--业务员信息息结束-->
<!--目标完成情况-->
<!--代理商-->
<div class="rightTime clear">
  <div class="timeLeft"></div>
  <div class="timeBody">目标完成情况
    <div style="float:right;margin-right:10px;">
      <div class="leftIcon" id="yearLeft1">
        <input type="button" onfocus="this.blur();">
      </div>
      <span id="year2">2012年</span>
      <input type="hidden" id="year1" value="2012"/>
      <div class="rightIcon" id="yearRight1">
        <input type="button" onfocus="this.blur();">
      </div>
    </div>
  </div>
  <div class="timeRight"></div>
</div>
<!--代理商结束-->
<!--table-->
<table cellpadding="0" cellspacing="0" id="back_tab" class="son_back clear" id="monthTable">
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
  <td><input type="text" value="0" id="month1" class="monthTarget" name="monthTarget"/></td>
  <td><input type="text" value="0" id="month2" class="monthTarget" name="monthTarget"/></td>
  <td><input type="text" value="0" id="month3" class="monthTarget" name="monthTarget"/></td>
  <td><input type="text" value="0" id="month4" class="monthTarget" name="monthTarget"/></td>
  <td><input type="text" value="0" id="month5" class="monthTarget" name="monthTarget"/></td>
  <td><input type="text" value="0" id="month6" class="monthTarget" name="monthTarget"/></td>
  <td><input type="text" value="0" id="month7" class="monthTarget" name="monthTarget"/></td>
  <td><input type="text" value="0" id="month8" class="monthTarget" name="monthTarget"/></td>
  <td><input type="text" value="0" id="month9" class="monthTarget" name="monthTarget"/></td>
  <td><input type="text" value="0"id="month10" class="monthTarget" name="monthTarget"/></td>
  <td><input type="text" value="0" id="month11" class="monthTarget" name="monthTarget"/></td>
  <td><input type="text" value="0" id="month12" class="monthTarget" name="monthTarget"/></td>
  <td id="totalTarget">0</td>
  <%--<tr id="writeMonthTarget">--%>
    <%--<td>目标</td>--%>
    <%--<c:forEach items="${totalList}" var="totals">--%>
      <%--<td><input type="text" value="${totals.monthTarget}" name="monthTarget" class="monthTarget"/></td>--%>
      <%--<input type="hidden" name="total" value="${totals.monthTarget}"/>--%>

    <%--</c:forEach>--%>
    <%--<td id="totalTarget">0</td>--%>
    <input type="hidden" name="agentId" value="${agentId}"/>
    <input type="hidden" name="yearTarget" id="yearTarget" value="${yearTarget}"/>
    <input type="hidden" name="year" value="${year}"/>
  </tr>
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
<div class="registerButton">
  <input type="button" value="修改" onfocus="this.blur();" id="btnInput"/>
  <input type="button" value="取消" onfocus="this.blur();"/>
</div>
<script type="text/javascript">
  function submitIt() {
    $("#myform")[0].submit();
  }
</script>
<!--目标完成情况结束-->
</div>
<!--内容结束-->
<!--圆角-->
<div class="bottom_crile clear">
  <div class="crile"></div>
  <div class="bottom_x"></div>
  <div style="clear:both;"></div>
</div>
<!--圆角结束-->
</div>
<!--右侧内容结束-->
</div>
</div>
</form>
</body>
</html>