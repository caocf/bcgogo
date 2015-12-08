<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
  Created by IntelliJ IDEA.
  User: wjl
  Date: 11-9-30
  Time: 上午9:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="/WEB-INF/views/includes.jsp" %>
<head>
    <title>产品最新采购价</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript">
    </script>
</head>
<body>
<div class="i_productRate">
    <div class="i_price">
        <div class="i_rateTop"></div>
        <div class="i_rateCenter"></div>
        <div class="i_rateBody">
            <div class="i_rateLogo">产品最新采购价</div>
            <div class="addClose"></div>
            <div class="price"><span>400</span>元</div>
        </div>
        <div class="i_rateLeft"></div>
        <div class="i_priceMiddle">
            <div class="i_rateArrow">
            </div>
        </div>
        <div class="i_rateRight"></div>
    </div>

</div>
</body>
</html>
