<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>后台管理系统——订单管理</title>
  <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
  <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
  <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
  <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
  <link rel="stylesheet" type="text/css" href="styles/order.css"/>
  <link rel="stylesheet" type="text/css" href="styles/style.css"/>

  <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.min.js"></script>
  <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>
  <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>

  <script src="//apps.bdimg.com/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>

  <%@include file="/WEB-INF/views/script-common.jsp"%>
  <script type="text/javascript" src="js/bcgogo.js"></script>
  <script type="text/javascript" src="js/order/order.js"></script>
  <script type="text/javascript" src="js/mask.js"></script>
</head>
<body>

<div class="main">
  <%@include file="/WEB-INF/views/header.jsp" %>
  <div class="body">
    <%@include file="/WEB-INF/views/left.jsp" %>
    <div class="right">
      <div class="navWrap">
        <div class="nav">订单管理</div>
      </div>
      <div class="searchConditionWrap">
        <label for="customerName" class="mar">客户名:</label><input type="text" placeholder="客户名" id="customerName">
        <label for="telNumber">手机号:</label><input type="text" placeholder="手机号" id="telNumber">
        <label for="vehicleNumber">车牌号:</label><input type="text" placeholder="车牌号" id="vehicleNumber">
        <input type="button" class="search" id="search" onfocus="this.blur();" value="查询" onclick="searchOrderList()"/>
        <br/>
        <label for="orderNumber" class="mar">订单号:</label><input type="text" placeholder="订单号" id="orderNumber">
        <label for="goodsName">商品名:</label><input type="text" placeholder="商品名" id="goodsName">
        <label for="orderStatus">订单状态:</label>
        <select id="orderStatus">
          <option></option>
          <option>客户提交</option>
          <option>已确认</option>
        </select>
        <%--<br/>--%>
        <%--<span>下单时间：</span><input type="text" placeholder="开始时间"><input type="text" placeholder="结束时间">--%>
        <%--<span>确认时间：</span><input type="text" placeholder="开始时间"><input type="text" placeholder="结束时间">--%>
      </div>
      <div class="searchResult">
        <table cellpadding="0" cellspacing="0" class="tableList" id="tableList">
          <tr>
            <td>订单号</td>
            <td>订单状态</td>
            <td>客户名</td>
            <td>手机号</td>
            <td>车牌号</td>
            <td>商品名</td>
            <td>订单金额</td>
            <td>操作</td>
          </tr>
        </table>

        <div class="simplePageAJAX" style="font-size:12px;">
        <jsp:include page="/common/pageAJAX.jsp">
          <jsp:param name="url" value="order.do?method=toOrderList"></jsp:param>
          <jsp:param name="data" value="{startPageNo:1}"></jsp:param>
          <jsp:param name="jsHandleJson" value="initOrderListTable"></jsp:param>
          <jsp:param name="dynamical" value="dynamical1"></jsp:param>
        </jsp:include>
      </div>


      </div>

    </div>
  </div>
</div>

</body>
</html>