<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta content="telephone=no,email=no" name="format-detection">
    <link rel="stylesheet" type="text/css" href="/web/styles/base_mirror<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="/web/styles/drive_mirror<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=1.5&ak=${ak}"></script>

    <%--<script type="text/javascript" src="http://developer.baidu.com/map/jsdemo/demo/convertor.js"></script>--%>
    <script type="text/javascript" src="/web/js/wx/drive_detail<%=ConfigController.getBuildVersion()%>.js"></script>
    <title>轨迹详情</title>
    <style type="text/css">
        html {
            height: 100%;
            width: 100%;
        }
    </style>
    <script type="text/javascript">
        var placeNotesStr = '${driveLogDTO.placeNotes}';
        initPoints(placeNotesStr);
    </script>
</head>

<body>
<div id="wrapper" class="drive-detail">
    <input type="hidden" id="openId" value="${openId}">
    <input type="hidden" id="startTime" value="${startTime}">
    <input type="hidden" id="endTime" value="${endTime}">
    <input type="hidden" id="appUserNo" value="${appUserNo}">
    <input id="driveLogId" type="hidden" value="${driveLogDTO.idStr}">
    <header id="header">
        <a id="play" type="button" class="blue_radius">轨迹回放</a>
        <a class="d-inter"></a>
        <a class="j_delete_btn blue_radius light_blue">删除轨迹</a>
    </header>
    <%--<section class="content">--%>
        <%--<div class="trajectory">--%>
            <%--<div class="piecewise">--%>
                <%--<div class="piecewise_li fl">--%>
                    <%--<div class="title">里程</div>--%>
                    <%--<div class="txt">${driveLogDTO.distance}KM</div>--%>
                <%--</div>--%>
                <%--<div class="m1 fl"></div>--%>
                <%--<div class="piecewise_li fl">--%>
                    <%--<div class="title">时长</div>--%>
                    <%--<div class="txt">${driveLogDTO.travelTimeStr}</div>--%>
                <%--</div>--%>
                <%--<div class="m1 fl"></div>--%>
                <%--<div class="piecewise_li fl">--%>
                    <%--<div class="title">金额</div>--%>
                    <%--<div class="txt"><span class="fem5">￥</span>${driveLogDTO.totalOilMoney}</div>--%>
                <%--</div>--%>
                <%--<div class="m1 fl"></div>--%>
                <%--&lt;%&ndash;<div class="piecewise_li fl">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="title">油耗</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="txt">${driveLogDTO.oilCost}L</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                <%--<div class="clr"></div>--%>
            <%--</div>--%>
            <%--<div class="comprehensive">--%>
                <%--&lt;%&ndash;<div class="cooprehensive_top">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="comprehensive_01 fl">最差</div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="comprehensive_02 fl">&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<div class="red_ribbon fl" style="width:50%"></div>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;${appVehicle.worstOilWear}<span class="fem5">L/100KM</span></div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="clr"></div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="comprehensive_01 fl">综合平均</div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="comprehensive_02 fl">&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<div class="grey_ribbon fl" style="width:30%"></div>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;${appVehicle.avgOilWear}<span class="fem5">L/100KM</span></div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="clr"></div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="comprehensive_01 fl">本次</div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="comprehensive_02 fl">&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<div class="blue_ribbon fl" style="width:60%"></div>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;${driveLogDTO.oilCost}<span class="fem5">L/100KM</span></div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="clr"></div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="comprehensive_01 fl">最佳</div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="comprehensive_02 fl">&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<div class="green_ribbon fl" style="width:25%"></div>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;${appVehicle.bestOilWear}<span class="fem5">L/100KM</span></div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="clr"></div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<c:if test="${driveLogDTO.oilCost > appVehicle.avgOilWear}">&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="piecewise_bottom">&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="piecewise_b_l fl">&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<div class="address">本次行程 <br>&ndash;%&gt;--%>
                                <%--&lt;%&ndash;平均油耗&ndash;%&gt;--%>
                            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<div class="grey_txt"> ${driveLogDTO.oilCost}<span class="fem5">L/100KM</span></div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="piecewise_b_c fl"> 高于<img src="/web/images/wx_mirror_images/above.png"></div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="piecewise_b_l fr">&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<div class="address">行程 <br>&ndash;%&gt;--%>
                                <%--&lt;%&ndash;综合油耗&ndash;%&gt;--%>
                            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<div class="grey_txt">${appVehicle.avgOilWear}<span class="fem5">L/100KM</span></div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<div class="clr"></div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                <%--&lt;%&ndash;</c:if>&ndash;%&gt;--%>
                <%--<div class="clr"></div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</section>--%>
</div>

<div id="container" class="b-map"></div>
</body>

</html>

