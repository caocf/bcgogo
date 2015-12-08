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
    <title>故障查询</title>
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script language="javascript" type="text/javascript">
        //已处理
        $(function () {
            $(".j_vehicle_fault_item").click(function () {
                var openId = $("#openId").val();
                var idStr = $(this).find(".idStr").val();
                var url = "/web/mirror/updateFaultCode/" + openId + "/" + idStr;
                window.location.href = url
            });
        });
        //历史故障
        $(function () {
            $(".j_vehicle_fault_history_item").click(function () {
                var openId = $("#openId").val();
                var appUserNo = $("#vehicleNoSelect").val();
                var status = "FIXED";
                var url = "/web/mirror/faultCode/" + appUserNo + "/" + openId + "/" + status;
                window.location.href = url
            });
        });
        //当前故障
        $(function () {
            $(".j_vehicle_fault_now_item").click(function () {
                var openId = $("#openId").val();
                var appUserNo = $("#vehicleNoSelect").val();
                var status = "UNTREATED";
                var url = "/web/mirror/faultCode/" + appUserNo + "/" + openId + "/" + status;
                window.location.href = url
            });
        });
        //背景知识
        $(function () {
            $(".j_vehicle_backgroundInfo_fault_item").click(function () {
                var appUserNo = $("#vehicleNoSelect").val();
                var errorCode = $(this).find(".errorCode").val();
                var url = "/web/mirror/backgroundInfo/" + errorCode + "/" + appUserNo;
                window.location.href = url
            });
        });

        //车辆下拉列表onchange事件
        function alert(a) {
            var appUserNo = a;
            var openId = $("#openId").val();
            var status = $("#status").val();
            var url = "/web/mirror/faultCode/" + appUserNo + "/" + openId + "/" + status;
            window.location.href = url

        }
    </script>
</head>
<body>
<input type="hidden" id="status" value="${status}">
<input type="hidden" id="openId" value="${openId}">

<div id="wrapper">
    <header id="header">
        <div class="selected_bg">
            <select id="vehicleNoSelect" onchange="alert(this.value);">
                <c:forEach items="${appWXUserDTOs}" var="appUserDTO" varStatus="status">
                    <option value="${appUserDTO.appUserNo}">${appUserDTO.vehicleNo}</option>
                </c:forEach>
            </select>
            <span class="char_white"></span></div>

        <c:choose>
            <c:when test="${button_flag eq 'UNTREATED'||button_flag == null}">
                <a class="j_vehicle_fault_now_item blue_radius">当前故障</a> <a
                    class="j_vehicle_fault_history_item light_blue">历史故障</a>
            </c:when>
            <c:otherwise>
                <a class="j_vehicle_fault_now_item light_blue">当前故障</a> <a
                    class="j_vehicle_fault_history_item blue_radius">历史故障</a>
            </c:otherwise>
        </c:choose>


        <div class="clr"></div>
    </header>
    <section class="content">
        <div class="trajectory">
            <c:forEach items="${appVehicleFaultInfoDTOList}" var="appVehicleFaultInfoDTO" varStatus="status">

                <div class="illegal_li">
                    <div class="line">
                        <div class="w30 fl">故障时间</div>
                        <div class="w70 grey_txt fr tr">${appVehicleFaultInfoDTO.reportTimeStr}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">故障码</div>
                        <div class="w70 grey_txt fr tr red_txt">${appVehicleFaultInfoDTO.errorCode}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">故障类型</div>
                        <div class="w70 grey_txt fr tr">${appVehicleFaultInfoDTO.category}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">故障描述</div>
                        <div class="w70 grey_txt fr tr fem8">${appVehicleFaultInfoDTO.content}
                        </div>
                        <div class="clr"></div>
                    </div>
                    <div class="blue_line">
                        <c:if test="${appVehicleFaultInfoDTO.flag eq 'UNTREATED'}">
                            <div class="j_vehicle_fault_item w30 fl">已处理<input type="hidden" class="idStr"
                                                                               value="${appVehicleFaultInfoDTO.idStr}">
                            </div>
                        </c:if>
                        <div class="j_vehicle_backgroundInfo_fault_item w70 fr tr">背景知识<input type="hidden"
                                                                                              class="errorCode"
                                                                                              value="${appVehicleFaultInfoDTO.errorCode}">
                        </div>
                        <div class="clr"></div>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div class="statistical">共有${appVehicleFaultInfoDTO_num}条故障记录</div>
    </section>
</div>
</body>
</html>
