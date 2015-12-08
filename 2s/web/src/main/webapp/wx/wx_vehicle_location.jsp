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
    <style type="text/css">
        body, html {
            width: 100%;
            height: 100%;
            margin: 0;
            font-family: "微软雅黑";
        }

        #l-map {
            height: 300px;
            width: 100%;
        }

        #r-result, #r-result table {
            width: 100%;
        }
    </style>
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=${ak}"></script>
    <script type="text/javascript" src="http://developer.baidu.com/map/jsdemo/demo/convertor.js"></script>
    <script type="text/javascript" src="/web/js/mobile/mBase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="/web/js/components/ui/bcgogo-bcAlert<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/wx/vehicle_location<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">

        var eResultJson = '${eResult}';
        var ticketSignJson = '${ticketSignJson}';

        //车辆下拉列表onchange事件
        function alert(a) {
            var appUserNo = a;
            var openId = $("#openId").val();
            var url = "/web/mirror/2VLocation/" + openId + "/" + appUserNo;
            window.location.href = url
        }


    </script>
    <title>车辆定位</title>
</head>
<body>
<input type="hidden" id="openId" value="${openId}">
<header id="header">
    <input type="hidden" class="j_vehicle_location" lon="${gsmVehicleDataDTO.lon}" lat="${gsmVehicleDataDTO.lat}"/>

    <div class="selected_bg">
        <select id="vehicleNoSelect " onchange="alert(this.value);">
            <c:forEach items="${appWXUserDTOs}" var="appUserDTO" varStatus="status">
                <option value="${appUserDTO.appUserNo}">${appUserDTO.vehicleNo}</option>
            </c:forEach>
        </select>
        <span class="char_white"></span></div>
    <%--<a id="findCarBtn" class="blue_radius">--%>
        <%--<img src="/web/images/wx_mirror_images/positioning.png">车辆定位</a>--%>
    <%--<a id="calDistance" class="blue_radius light_blue"><img src="/web/images/wx_mirror_images/distance.png">人车距离</a>--%>
    <%--<input type="text" id="errorMsg">--%>
</header>
<div id="container" class="b-map"></div>
<div id="r-result"></div>
<div id="walkingPath" style="display: none" class="statistical">您的爱车离您<span id="distance">0公里</span>，步行需要<span
        id="duration">0分钟</span></div>
</body>
</html>
