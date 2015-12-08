<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-1-21
  Time: 上午11:22
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>库存盘点</title>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/inventoryCheck<%=ConfigController.getBuildVersion()%>.css"/>
  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/txn/inventoryCheck<%=ConfigController.getBuildVersion()%>.js"></script>
  <style>
    #table_productNo tr td input {
      width: 85%;
    }

  </style>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>

<div class="i_main">
  <jsp:include page="txnNavi.jsp">
    <jsp:param name="currPage" value="inventory"/>
  </jsp:include>
  <jsp:include page="inventroyNavi.jsp">
    <jsp:param name="currPage" value="inventoryCheck"/>
    <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
  </jsp:include>

  <div class="titBody">
    <div class="lineTitle">库存盘点搜索</div>
    <div class="lineBody bodys my_keyboard_message">
      <div class="divTit" style="width:100%;">
        操作日期：
          <a class="btnList" id="my_date_thisweek" name="my_date_select">本周</a>&nbsp;
          <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
          <a class="btnList" id="my_date_thisyear" name="my_date_select">本年</a>&nbsp;
          <a class="btnList" id="my_date_self_defining" name="my_date_select">自定义时间段</a>&nbsp;
          <input type="text" class="my_startdatetime txt" id="startTimeInput" value="" style="width:120px;" />&nbsp;
          至&nbsp;<input type="text" class="my_enddatetime txt" id="endTimeInput" value="${currentTime}" style="width:120px;"/>
      </div>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
            <div class="divTit">
                盘点仓库：
                <select id="storehouseId" name="storehouseId" onchange="selectInput(this)" value="">
                    <option value="">——请选择仓库——</option>
                    <c:forEach items="${storeHouseDTOList}" var="item">
                        <option value="${item.id}">${item.name}</option>
                    </c:forEach>
                </select>
                <input type="hidden" id="isHaveStoreHouseShop" isHaveStoreHouseShop="true"/>
            </div>
        </bcgogo:hasPermission>
      <div class="divTit">盘盈盘亏：
        <select class="txt" id="checkResultSelect" onchange="selectInput(this)" value="">
          <option value="0">所有盘点</option>
          <option value="1">盘盈</option>
          <option value="2">盘亏</option>
        </select>
      </div>
      <div class="divTit">操作人：<input type="text" class="txt" id="editor"/></div>
      <div class="divTit"><input id="searchBtn" class="btn_search" type="button" onfocus="this.blur();"></div>
    </div>
    <div class="lineBottom"></div>
    <div class="clear height"></div>
    <div class="lineTitle tabTitle">
      共<span class="blue_color" id="totalRecords">0</span>条记录&nbsp;&nbsp;盘点合计：<span class="green_color" id="stockAdjustPriceTotal">0</span>元
      <%--<a class="btnPan" href="inventoryCheck.do?method=createInventoryCheckByProductIds">新增盘点</a>--%>
        <input id="addNewInventoryCheck" class="addNew" type="button" value="新增盘点" onclick="window.location.href='inventoryCheck.do?method=createInventoryCheckByProductIds'">
    </div>
    <div class="tab">
      <table cellpadding="0" cellspacing="0" class="tabSlip" id="inventoryCheckTable">
        <col width="60">
        <col width="160">
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
          <col width="150">
        </bcgogo:hasPermission>
        <col width="80">
        <col width="150">
        <col width="150">
        <col width="200">
        <tr class="titleBg">
          <td  style="padding-left:10px;">NO</td>
          <td>单据号</td>
          <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
            <td>盘点仓库</td>
          </bcgogo:hasPermission>
          <td>盘盈盘亏</td>
          <td>盘点金额</td>
          <td>操作人</td>
          <td>操作日期</td>
        </tr>

      </table>
      <div class="height"></div>

      <!--分页-->
      <div class="hidePageAJAX" style="float:left">
        <jsp:include page="/common/pageAJAX.jsp">
          <jsp:param name="url" value="inventoryCheck.do?method=getInventoryChecks"></jsp:param>
          <jsp:param name="data" value="{startPageNo:1}"></jsp:param>
          <jsp:param name="jsHandleJson" value="initInventoryCheckRecordTable"></jsp:param>
          <jsp:param name="hide" value="hideComp"></jsp:param>
          <jsp:param name="dynamical" value="dynamical1"></jsp:param>
        </jsp:include>
      </div>
    </div>
  </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
