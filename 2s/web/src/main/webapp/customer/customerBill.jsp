<%--
  Created by IntelliJ IDEA.
  User: liuWei
  Date: 13-1-9
  Time: 上午10:48
  To change this template use File | Settings | File Templates.
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/views/includes.jsp" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <meta http-equiv="X-UA-Compativle" content="IE-EmulateIE7"/>
  <title>对账单</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/uncleUser<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreUserinfo1<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreUserinfo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>


    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/statementAccount/customerBill<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        <c:if test="${orderTypeStr=='客户对账单'}">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.CUSTOMER_MANAGER.BASE");
        </c:if>
        <c:if test="${orderTypeStr=='供应商对账单'}">
        defaultStorage.setItem(storageKey.MenuUid,"CUSTOMER_SEARCH_SUPPLIERS");
        </c:if>
        defaultStorage.setItem(storageKey.MenuCurrentItem,"对账单");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="title"></div>
<div class="i_main clear">
    <c:if test="${empty customerOrSupplierId}">
        <div class="cusTitle">对账查询</div>
    </c:if>
    <c:if test="${!empty customerOrSupplierId}">
        <c:if test="${orderType == 'CUSTOMER_STATEMENT_ACCOUNT'}">
            <div class="cusTitle">客户资料</div>
        </c:if>
        <c:if test="${orderType == 'SUPPLIER_STATEMENT_ACCOUNT'}">
            <div class="cusTitle">供应商资料</div>
        </c:if>
        <div class="titBodys">
            <c:if test="${orderType == 'CUSTOMER_STATEMENT_ACCOUNT'}">
                <a class="normal_btn" href="#" data-page-info="customerOrSupplierInfo" onclick="redirectUncleUser('customerBill')">客户详细信息</a>
                <a class="hover_btn" href="#" onclick="redirectCustomerBill('customerBill')">客户对账单</a>
            </c:if>
            <c:if test="${orderType == 'SUPPLIER_STATEMENT_ACCOUNT'}">
                <a class="normal_btn" href="#" data-page-info="customerOrSupplierInfo" onclick="redirectUncleUser('supplierBill')">供应商详细信息</a>
                <a class="hover_btn" href="#" onclick="redirectCustomerBill('supplierBill')">供应商对账单</a>
            </c:if>
        </div>
    </c:if>

    <%@include file="/customerDetail/customerDetailBill.jsp"%>

</div>
<%@ include file="/sms/enterPhone.jsp" %>
<div id="div_brand" class="i_scroll" style="display:none;width:140px;">
    <div class="Container">
        <div id="Scroller-1">
            <div class="Scroller-Container" id="Scroller-Container_id">
            </div>
        </div>
    </div>
</div>
<iframe id="iframe_PopupBox_2" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1500px" frameborder="0" src="" scrolling="no"></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>

