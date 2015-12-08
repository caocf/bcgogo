<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>合并记录</title>

  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/mergeRecord<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/customerOrSupplier/mergeSupplierRecord<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
  <div class="i_search">
    <jsp:include page="/customer/supplierNavi.jsp">
      <jsp:param name="currPage" value="searchSupplier"/>
    </jsp:include>
    <div class="i_main clear">
      <div class="titBody">
        <div class="wordTitle">合并记录查询</div>
        <div class="allocation">
          <div class="allocation_left"></div>
          <div class="allocation_center">
            <div class="height"></div>
            <div class="divTit">供应商名：<input type="text" class="txt" id="supplierName" /></div>
            <div class="divTit">操作人：<input type="text" class="txt" id="operator" /></div>
            <div class="divTit">时间：
              <input type="text" id="startTimeInput" class="txt timeInput" readonly="true"/>&nbsp;至&nbsp;
              <input type="text" class="txt timeInput" id="endTimeInput" readonly="true" value="${endTime}"/>
            </div>
            <div class="searchMergeRecordDiv" style="padding-left: 60px;"><input class="btn_so" type="button" id="searchMergeRecordBtn" style="float: left"></div>
          </div>
          <div class="allocation_right"></div>
        </div>

        <div class="wordTitle">
          合并记录共<span id="totalMergeRecord" style="color:#0094FF;"></span>条
        </div>
        <div class="clear"></div>
        <table cellpadding="0" cellspacing="0" class="mergeRecordTable tabSlip tabPick">
          <col width="190">
          <col width="180">
          <col width="180">
          <col width="222">
          <col width="150">
          <tr class="tab_title">
            <td>合并操作时间</td>
            <td>操作人</td>
            <td>保留供应商</td>
            <td>合并详细</td>
            <td>操作</td>
          </tr>

        </table>
        <!--分页-->
        <div class="hidePageAJAX" style="float:left;padding-top: 10px">
          <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="supplier.do?method=getSupplierMergeRecords"></jsp:param>
            <jsp:param name="data" value="{startPageNo:1}"></jsp:param>
            <jsp:param name="jsHandleJson" value="initMergeSupplierRecord"></jsp:param>
            <jsp:param name="hide" value="hideComp"></jsp:param>
            <jsp:param name="dynamical" value="dynamical1"></jsp:param>
          </jsp:include>
        </div>
        <div class="height"></div>
        <div class="height"></div>
        <input type="button" value="返回供应商" class="jieCount" id="toSupplierDataBtn"/>
      </div>
    </div>
  </div>
</div>


<!-- 合并普通供应商弹出框-->
<div id="mergeSupplierDetail" style="display: none;">
  <jsp:include page="mergeSupplierDetail.jsp"></jsp:include>
</div>

<!-- 合并批发商弹出框-->
<div id="mergeWholesalerDetail" style="display: none;">
  <jsp:include page="mergeWholesalerDetail.jsp"></jsp:include>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>