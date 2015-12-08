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
    <title>我的消息</title>
</head>

<body>
<div id="wrapper">
    <header id="header">
        <div class="selected_bg">
            <select id="vehicleNoSelect">
                <c:forEach items="${appWXUserDTOs}" var="appUserDTO" varStatus="status">
                    <option value="${appUserDTO.appUserNo}">${appUserDTO.vehicleNo}</option>
                </c:forEach>
            </select>
        </div>

        <jsp:include page="./wx_msg_header.jsp">
            <jsp:param name="currPage" value="myMessage"/>
        </jsp:include>
        <div class="clr"></div>
    </header>
    <div id="messageContain"></div>
    <section class="content">
        <div class="trajectory">
            <c:forEach items="${messageList}" var="message" varStatus="status">
                <div class="message_li">
                    <div class="top">
                        <div class="w30 fl">${message.title}</div>
                        <div class="w70 fr tr">发送时间：${message.sendTimeStr}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="m_bottom">
                            ${message.content}
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
</div>
</body>
</html>
