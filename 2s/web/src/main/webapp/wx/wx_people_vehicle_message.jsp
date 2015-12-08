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
    <title>人车对话</title>
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <%--<script language="javascript" type="text/javascript">--%>
        <%--$(function() {--%>

        <%--});--%>
    <%--</script>--%>
</head>
<body>
<div id="wrapper" class="wx-pv-msg">

    <header id="header">
        <div class="selected_bg" style="padding-top:1em">
            <select id="vehicleNoSelect">
                <c:forEach items="${appWXUserDTOs}" var="appUserDTO" varStatus="status">
                    <option value="${appUserDTO.appUserNo}">${appUserDTO.vehicleNo}</option>
                </c:forEach>
            </select>
        </div>

        <jsp:include page="./wx_msg_header.jsp">
            <jsp:param name="currPage" value="talkWithCat"/>
        </jsp:include>
        <div class="clr"></div>
    </header>

    <section class="content">
        <div class="trajectory">
            <div class="date_w">
                <div class="j_content_remind content-remind" style="display: ${onLine}">
                    &nbsp;&nbsp;对方不在线，可能无法立即回复，您可以给他（她）发送离线消息
                </div>
                <div id="moreBtn" class="date"><span id="loadIcon" style="display: none;margin-right:4px;"><img src="/web/images/loadinglit-2.gif"></span>查看更多消息</div>
            </div>
        </div>
        <div class="padding10">
            <div id="contentBody" class="content-body">
                <c:forEach items="${pushMessages}" var="pushMessage" varStatus="status">
                    <c:choose>
                        <c:when test="${pushMessage.type=='MSG_FROM_WX_USER_TO_MIRROR'}">
                            <div class="dialogue">
                                <div class="sj_relative">
                                    <div class="lan_absolute"><img src="/web/images/wx_mirror_images/lan_sj.png"></div>
                                    <div class="w76 fl">
                                        <div class="a1 fr">${pushMessage.content}</div>
                                    </div>
                                </div>
                                <div class="w23  fr tr">
                                    <div class="w99"><img src="${userDTO.headimgurl}">
                                    </div>
                                </div>
                                <div class="clr"></div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="dialogue">
                                <div class="sj_relative">
                                    <div class="bai_absolute"><img src="/web/images/wx_mirror_images/bai_sj.png"></div>
                                    <div class="w76 fr">
                                        <div class="a2 fr">${pushMessage.content}</div>
                                    </div>
                                </div>
                                <div class="w23  fl">
                                    <div class="w99"><img src="/web/images/wx_mirror_images/tou2.jpg"></div>
                                </div>
                                <div class="clr"></div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
            <div class="add_txt">
                <a href="#" class="w20 fl"><img width="24" src="/web/images/wx_mirror_images/xiao.jpg"/></a>

                <div class="add_txt_input w50 fl"><input id="content" name="" type="text"></div>
                <div class="w20 fr" style="margin-top:4px;"><a id="sendBtn" class="blue_radius">发送</a></div>
            </div>
        </div>
    </section>
</div>
</body>
</html>
