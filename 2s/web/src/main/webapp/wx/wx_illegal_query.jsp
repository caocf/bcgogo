<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", -10);
%>
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
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta content="telephone=no,email=no" name="format-detection">
    <link rel="stylesheet" type="text/css" href="/web/styles/base_mirror<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="/web/styles/drive_mirror<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <title>违章查询</title>
    <script language="javascript" type="text/javascript">
        //车辆下拉列表onchange事件
        function alert(a) {
            var appUserNo = a;
            var openId = $("#openId").val();
            var url = "/web/mirror/violate/" + openId + "/" + appUserNo;
            window.location.href = url
        }
    </script>
</head>
<body>
<input type="hidden" id="openId" value="${openId}">

<div id="wrapper">
    <header id="header">
        <select id="vehicleNoSelect" onchange="alert(this.value);">
            <c:forEach items="${appWXUserDTOs}" var="appUserDTO" varStatus="status">
                <option value="${appUserDTO.appUserNo}">${appUserDTO.vehicleNo}</option>
            </c:forEach>
        </select>
        <span class="char_corl"></span>
    </header>
    <section class="content">
        <div class="trajectory">
            <c:forEach items="${recordDTOs}" var="recordDTO" varStatus="status">
                <div class="illegal_li">
                    <div class="line">
                        <div class="w30 fl">违章时间</div>
                        <div class="w70 fr tr">${recordDTO.date}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">违章地点</div>
                        <div class="w70 fr tr">${recordDTO.area}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">违章内容</div>
                        <div class="w70 fr tr">${recordDTO.act}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="red_line">
                        <div class="w30 fl">罚款：-${recordDTO.money}元</div>
                        <div class="w70 fr tr">扣分：-${recordDTO.fen}分</div>
                        <div class="clr"></div>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${not empty reason}">
                <div class="reason_li">${reason}</div>
            </c:if>
        </div>
        <div class="statistical">共有${empty recordDTOs_size?0:recordDTOs_size}条违章记录；
            总计：扣款${empty recordDTOs_money?0:recordDTOs_money}元；扣${empty recordDTOs_fen?0:recordDTOs_fen}分。
        </div>
    </section>
</div>
</body>
</html>
