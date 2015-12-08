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
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta content="telephone=no,email=no" name="format-detection">
    <link rel="stylesheet" type="text/css" href="/web/styles/base_mirror<%=ConfigController.getBuildVersion()%>.css" />
    <link rel="stylesheet" type="text/css" href="/web/styles/drive_mirror<%=ConfigController.getBuildVersion()%>.css" />
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
      <script type="text/javascript" src="/web/js/mobile/mBase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="/web/js/components/ui/bcgogo-bcAlert<%=ConfigController.getBuildVersion()%>.js"></script>
    <title>车况检查</title>
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
            $(".j_vehicle_data_item").click(function () {
                var openId = $("#openId").val();
                var appUserNo =$("#vehicleNoSelect").val();
                var url = "/web/mirror/gvData/"+openId+"/"+appUserNo ;
                window.location.href = url
            });
        });

        //车辆下拉列表onchange事件
        function alert(a) {
            var openId = $("#openId").val();
            var appUserNo =  a;
            var url = "/web/mirror/gvData/"+openId+"/"+appUserNo ;
            window.location.href = url

        }
    </script>
</head>
<body>
<input type="hidden" id="openId" value="${openId}" autocomplete="off"/>
<div id="wrapper">
    <header id="header">
        <div class="selected_bg">
            <select id="vehicleNoSelect" onchange="alert(this.value);">
                <c:forEach items="${appWXUserDTOs}" var="appUserDTO" varStatus="status">
                    <option value="${appUserDTO.appUserNo}">${appUserDTO.vehicleNo}</option>
                </c:forEach>
            </select>
            <span class="char_white"></span></div>
        <div class="j_vehicle_data_item condition"><a href="#"><img src="/web/images/wx_mirror_images/refresh.png"/></a> 刷新数据</div>
        <div class="clr"></div>
    </header>
    <section class="content">
        <div class="trajectory">
            <div class="message_li">
                <div class="top">
                    <div class="w70">车辆状态检测报告</div>
                    <div class="w70 fem8">检测时间:${gsmVehicleDataDTO.upLoadTimeStr}</div>
                    <div class="clr"></div>
                </div>
                <div class="m_bottom">
                    <div class="w75 fl">

                        <div class="padding10">
                            <div class="che_relative">
                                <%--<div class="men1"></div>--%>
                                <%--<div class="men2"></div>--%>
                                <%--<div class="men3"></div>--%>
                                <%--<div class="men4"></div>--%>
                                <%--<div class="hou_g"></div>--%>
                            </div>
                            <div class="che_s"></div>
                        </div>
                    </div>
                    <div class="w25 fr">
                        <div class="condition_li fl">
                            <div class="title">当前里程</div>
                            <div class="txt">${gsmVehicleDataDTO.curMil}km</div>
                        </div>
                        <%--<div class="condition_li fl">--%>
                            <%--<div class="title">油耗</div>--%>
                            <%--<div class="txt">${gsmVehicleDataDTO.aOilWear}L/100km</div>--%>
                        <%--</div>--%>
                        <%--<div class="condition_li fl">--%>
                            <%--<div class="title">剩余油量</div>--%>
                            <%--<div class="txt">${gsmVehicleDataDTO.rOilMass}</div>--%>
                        <%--</div>--%>
                        <%--<c:if test="${door eq 0}">--%>
                            <%--<div class="condition_li2 fl">--%>
                                <%--您有车门未关--%>
                            <%--</div>--%>
                        <%--</c:if>--%>
                    </div>
                    <div class="clr"></div>
                </div>
            </div>
            <div class="illegal_li">
                <%--String sPowerStr = "";--%>
                <%--String sFlowStr = "";--%>
                <%--String sCoolingStr = "";--%>
                <%--String sBlowoffStr = "";--%>
                <c:choose>
                <c:when test="${sPowerStr eq 0}">
                    <div class="line">
                        <div class="w25 fl">电源系统</div>
                        <div class="w50 grey_txt fl"><span class="red_txt">${gsmVehicleDataDTO.spwr}</span><span class="fem8">(13.2V-14.8V)</span></div>
                        <div class="w25 fr tr"><a class="blue_txt">背景知识</a></div>
                        <div class="clr"></div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="line">
                        <div class="w25 fl">电源系统</div>
                        <div class="w50 grey_txt fl"><span class="green_txt">${gsmVehicleDataDTO.spwr}</span><span class="fem8">(13.2V-14.8V)</span></div>
                        <div class="w25 fr tr"><a class="blue_txt">背景知识</a></div>
                        <div class="clr"></div>
                    </div>
                </c:otherwise>
                </c:choose>

                <c:choose>
                <c:when test="${sFlowStr eq 0}">
                    <div class="line">
                        <div class="w25 fl">进气系统</div>
                        <div class="w50 grey_txt fl"><span class="red_txt">${gsmVehicleDataDTO.throttlePosition}</span><span class="fem8">(0～100%)</span></div>
                        <div class="w25 fr tr"><a class="blue_txt">背景知识</a></div>
                        <div class="clr"></div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="line">
                        <div class="w25 fl">进气系统</div>
                        <div class="w50 grey_txt fl"><span class="green_txt">${gsmVehicleDataDTO.throttlePosition}</span><span class="fem8">(0～100%)</span></div>
                        <div class="w25 fr tr"><a class="blue_txt">背景知识</a></div>
                        <div class="clr"></div>
                    </div>
                </c:otherwise>
                </c:choose>

                <c:choose>
                <c:when test="${sCoolingStr eq 0}">
                    <div class="line">
                        <div class="w25 fl">冷却系统</div>
                        <div class="w50 grey_txt fl"><span class="red_txt">${gsmVehicleDataDTO.wbtm}</span><span class="fem8">(0～120℃)</span></div>
                        <div class="w25 fr tr"><a class="blue_txt">背景知识</a></div>
                        <div class="clr"></div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="line">
                        <div class="w25 fl">冷却系统</div>
                        <div class="w50 grey_txt fl"><span class="green_txt">${gsmVehicleDataDTO.wbtm}</span><span class="fem8">(0～120℃)</span></div>
                        <div class="w25 fr tr"><a class="blue_txt">背景知识</a></div>
                        <div class="clr"></div>
                    </div>
                </c:otherwise>
                </c:choose>

                <c:choose>
                <c:when test="${sBlowoffStr eq 0}">
                    <div class="line">
                        <div class="w25 fl">排放系统</div>
                        <div class="w50 grey_txt fl"><span class="red_txt">${gsmVehicleDataDTO.voltageForOxygenSensor}<span class="fem8">(0～1v)</span></span></div>
                        <div class="w25 fr tr"><a class="blue_txt">背景知识</a></div>
                        <div class="clr"></div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="line">
                        <div class="w25 fl">排放系统</div>
                        <div class="w50 grey_txt fl"><span class="green_txt">${gsmVehicleDataDTO.voltageForOxygenSensor}<span class="fem8">(0～1v)</span></span></div>
                        <div class="w25 fr tr"><a class="blue_txt">背景知识</a></div>
                        <div class="clr"></div>
                    </div>
                </c:otherwise>
                </c:choose>
            </div>
        </div>
        <div class="statistical">共有${i}处检测异常</div>
    </section>
</div>
</body>
</html>
