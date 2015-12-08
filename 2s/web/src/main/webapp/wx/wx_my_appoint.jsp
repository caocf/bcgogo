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
    <script type="text/javascript"
            src="/web/js/wx/wx_mirror_appoint<%=ConfigController.getBuildVersion()%>.js"></script>
    <script language="javascript" type="text/javascript">
        //新增预约 跳转到添加预约界面
        $(function () {
            $(".j_to_mirror_appoint_item").click(function () {
                var openId = $("#openId").val();
                var appUserNo = $("#appUserNo").val();
                var url = "/web/mirror/2Appoint/" + openId + "/" + appUserNo;
                window.location.href = url
            });
        });
        //取消预约
        $(function () {
            $(".j_delete_appoint_item").click(function () {
                var openId = $("#openId").val();
                var shopId = $("#shopId").val();
                var appUserNo =  $("#vehicleNoSelect").val();
                var appointOrderId = $("#appointOrderId").val();
                var url = "/web/mirror/deleteAppoint/" + openId + "/" + shopId + "/" + appointOrderId+"/"+appUserNo;
                window.location.href = url
            });
        });

        //车辆下拉列表onchange事件
        function alert(a) {
            var openId = $("#openId").val();
            var appUserNo = a;
            var url = "/web/mirror/myAppoint/" + openId + "/" + appUserNo;
            window.location.href = url
        }

    </script>
    <title>我的预约</title>
</head>
<body>
<input type="hidden" id="openId" value="${openId}" autocomplete="off"/>
<input type="hidden" id="appUserNo" value="${appUserNo}" autocomplete="off"/>

<div id="wrapper">
    <header id="header">
        <div class="selected_bg">
            <select id="vehicleNoSelect" onchange="alert(this.value);">
                <c:forEach items="${appWXUserDTOs}" var="appUserDTO" varStatus="status">
                    <option value="${appUserDTO.appUserNo}">${appUserDTO.vehicleNo}</option>
                </c:forEach>
            </select>
            <span class="char_white"></span>
        </div>
        <a class="j_to_mirror_appoint_item blue_radius" style="float:right; margin-top:6px;">新增预约</a>

        <div class="clr"></div>
    </header>
    <section class="content">
        <div class="trajectory">
            <c:forEach items="${appointOrderDTOs}" var="appointOrderDTO" varStatus="status">
                <input type="hidden" id="appointOrderId" value="${appointOrderDTO.id}" autocomplete="off"/>
                <input type="hidden" id="shopId" value="${appointOrderDTO.shopId}" autocomplete="off"/>

                <div class="illegal_li">
                    <div class="line">
                        <div class="w30 fl">服务店面</div>
                        <div class="w70 grey_txt fr tr">${appointOrderDTO.shopName}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">服务电话</div>
                        <div class="w70 grey_txt fr tr">${appointOrderDTO.shopMobile}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">服务地址</div>
                        <div class="w70 grey_txt fr tr">${appointOrderDTO.shopAddress}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">服务时间</div>
                        <div class="w70 grey_txt fr tr">${appointOrderDTO.appointTimeStr}
                        </div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">服务车辆</div>
                        <div class="w70 grey_txt fr tr">${appointOrderDTO.vehicleNo}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">服务项目</div>
                        <div class="w70 grey_txt fr tr"><b>${appointOrderDTO.appointServiceType}</b></div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">预约状态</div>
                        <div class="w70 grey_txt fr tr">${appointOrderDTO.statusStr}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">备注</div>
                        <div class="w70 grey_txt fr tr">${appointOrderDTO.remark}</div>
                        <div class="clr"></div>
                    </div>
                        <%--<div>--%>
                        <%--<div class="w50 fl edit_btn">编辑</div>--%>
                        <%--<div class="j_delete_appoint_item w50 fl cancel_btn">取消</div>--%>
                        <%--<div class="clr"></div>--%>
                        <%--</div>--%>
                    <div class="j_delete_appoint_item add_btn_appoint">取消</div>
                </div>
            </c:forEach>
        </div>

    </section>
</div>
</body>
</html>
