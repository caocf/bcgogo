<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 2015-4-22
  Time: 17:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta content="telephone=no,email=no" name="format-detection">
    <link rel="stylesheet" type="text/css" href="/web/styles/base_mirror<%=ConfigController.getBuildVersion()%>.css" />
    <link rel="stylesheet" type="text/css" href="/web/styles/drive_mirror<%=ConfigController.getBuildVersion()%>.css" />
    <title>背景知识</title>
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script language="javascript" type="text/javascript">

    </script>
</head>
<body>
<div>
    <div class="padding10">
        <div class="red_txt tc">${errorCode}</div>
        <p>
           ${backgroundInfo}
        </p>
    </div>

</div>
</body>
</html>
