<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>统购后台CRM管理系统</title>
    <%
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    %>
    <%--Ext base css--%>
    <link rel="stylesheet" type="text/css" href="app/ext4.1/resources/css/ext-all-debug.css"/>
    <%--Ext ux css--%>
    <link rel="stylesheet" type="text/css" href="app/js/ux/css/CheckHeader.css"/>

    <link rel="stylesheet" type="text/css" href="app/styles/icon.css"/>
    <link rel="stylesheet" type="text/css" href="app/styles/main.css"/>

    <link rel="stylesheet" type="text/css" href="app/styles/sys/ModuleList.css"/>

    <link rel="stylesheet" type="text/css" href="app/styles/product/product.css"/>

    <link rel="stylesheet" type="text/css" href="app/styles/page/finance/SoftwareReceivableForm.css"/>

    <%--<script type="text/javascript" src="app/ext4.1/ext-all.js"></script>--%>
    <script type="text/javascript" src="app/ext4.1/ext-all-debug.js"></script>
    <script type="text/javascript" src="app/ext4.1/locale/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="app/js/app.js"></script>
    <script type="text/javascript" src="app/js/utils/ValidateUtils.js"></script>
</head>
<body>
<input type="hidden" value="${userName}" id="userNameForHeader">
</body>
</html>
