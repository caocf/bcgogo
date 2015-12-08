<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", -10);
%>
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
    <script type="text/javascript" src="/web/js/mobile/mBase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="/web/js/components/ui/bcgogo-bcAlert<%=ConfigController.getBuildVersion()%>.js"></script>
    <title>行车轨迹</title>
    <script language="javascript" type="text/javascript">
        var eResultJson = '${eResult}';

        $(function () {

            if (eResultJson) {
                var eResult = JSON.parse(eResultJson);
                var bcAlert = APP_BCGOGO.Module.bcAlert;
                bcAlert.login({
                    info:eResult.errorMsg,
                    level:eResult.level
                });
                return;
            }

            $(".j_drive_log_item").click(function () {
                var driveLogId = $(this).find(".driveLogId").val();
                var startTime = Number($("#startTime").val());
                var endTime = Number($("#endTime").val());
                var openId = $("#openId").val();
                var url = "/web/mirror/wx/toDriveLogDetail/" + driveLogId + "/" + openId + "/" + startTime + "/" + endTime;
                window.location.href = url
            });

            $(".j_week_btn").click(function () {
                var startTime = Number($("#startTime").val());
                var endTime = Number($("#endTime").val());
                var _$me = $(this);
                if (_$me.attr("id") == "lastWeekBtn") {
                    startTime -= 7 * 24 * 60 * 60 * 1000;
                    endTime -= 7 * 24 * 60 * 60 * 1000;
                } else {
                    startTime += 7 * 24 * 60 * 60 * 1000;
                    endTime += 7 * 24 * 60 * 60 * 1000;
                }
                var openId = $("#openId").val();
                var appUserNo = $("#vehicleNoSelect").val();
                var url = "/web/mirror/2DriveLog/" + openId + "/" + startTime + "/" + endTime + "/" + appUserNo;
                window.location.href = url;
            });

        });

        //车辆下拉列表onchange事件
        function alert(a) {
            var appUserNo = a;
            var startTime = Number($("#startTime").val());
            var endTime = Number($("#endTime").val());
            var _$me = $(this);
            if (_$me.attr("id") == "lastWeekBtn") {
                startTime -= 7 * 24 * 60 * 60 * 1000;
                endTime -= 7 * 24 * 60 * 60 * 1000;
            } else {
                startTime += 7 * 24 * 60 * 60 * 1000;
                endTime += 7 * 24 * 60 * 60 * 1000;
            }
            var openId = $("#openId").val();
            var url = "/web/mirror/2DriveLog/" + openId + "/" + startTime + "/" + endTime + "/" + appUserNo;
            window.location.href = url

        }

    </script>
</head>

<body>
<div id="wrapper">
    <input type="hidden" id="openId" value="${openId}">
    <input type="hidden" id="startTime" value="${startTime}">
    <input type="hidden" id="endTime" value="${endTime}">
    <header id="header">

        <%--<a id="lastWeekBtn" href="#" class="j_week_btn arrow w20 fl">--%>
            <%--<img width="24" src="/web/images/wx_mirror_images/icon_left.png"/>--%>
        <%--</a>--%>
        <select id="vehicleNoSelect" onchange="alert(this.value);">
            <c:forEach items="${appWXUserDTOs}" var="appUserDTO" varStatus="status">
                <option value="${appUserDTO.appUserNo}">${appUserDTO.vehicleNo}</option>
            </c:forEach>
        </select>
        <span class="char_corl"></span>
        <input type="hidden" id="weekTime" value="0"/>
        <%--<a id="nextWeekBtn" href="#" class="j_week_btn arrow w20 fr"><img--%>
                <%--src="/web/images/wx_mirror_images/icon_right.png"/></a>--%>
    </header>
    <section class="content">
        <div class="trajectory">
            <c:forEach items="${driveLogDTOList}" var="driveLog" varStatus="status">
                <div class="j_drive_log_item trajectory_li">
                        <%--<input type="hidden" class="appUserNo" value="${driveLog.appUserNo}">--%>
                    <input type="hidden" class="driveLogId" value="${driveLog.idStr}">

                    <div class="trajectory_01 fl">
                        <h1>${driveLog.startCity}</h1>

                        <div class="address">${driveLog.startPlace}</div>
                        <div class="grey_txt">${driveLog.startTimeStr}
                        </div>
                    </div>
                    <div class="trajectory_02 fl">
                        <div class="consumption">${driveLog.distance}km</div>
                        <div class="car">
                            <div class="car_img"></div>
                        </div>
                        <div class="grey_txt fem13">${driveLog.travelTimeStr}</div>
                    </div>
                    <div class="trajectory_01 fr">
                        <h1>${driveLog.endCity}</h1>

                        <div class="address">${driveLog.endPlace}</div>
                        <div class="grey_txt">${driveLog.endTimeStr}
                        </div>
                    </div>
                    <div class="clr"></div>
                </div>
            </c:forEach>

        </div>
        <div class="statistical">共有${driveLogDTOList_num}条行车轨迹</div>
    </section>
</div>
</body>

</html>
