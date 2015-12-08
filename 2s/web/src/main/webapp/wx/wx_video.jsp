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
    <title>碰撞视频</title>
    <script type="text/javascript">
        $(function() {
           $(".j_video_item").click(function(){
               window.open("/web/mirror/2VideoP/"+$(this).attr("impact_video_id"));
           });

        });
    </script>

</head>
<body>
<div id="wrapper" class="t-wp t-video">
    <section class="content">
        <div class="trajectory">
            <c:forEach items="${impactVideoExpDTOs}" var="impactVideoExpDTO" varStatus="status">
                <div class="j_video_item illegal_li" impact_video_id="${impactVideoExpDTO.impactVideoIdStr}">
                    <div class="line">
                        <div class="w30 fl">碰撞车辆</div>
                        <div class="w70 fr tr">${impactVideoExpDTO.vehicleNo}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">碰撞时间</div>
                        <div class="w70 fr tr">${impactVideoExpDTO.uploadTimeDateStr}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">碰撞地点</div>
                        <div class="w70 fr tr">${impactVideoExpDTO.address}</div>
                        <div class="clr"></div>
                    </div>
                    <div class="line">
                        <div class="w30 fl">视频上传状态</div>
                        <div class="w70 fr tr">${impactVideoExpDTO.uploadStatus}</div>
                        <div class="clr"></div>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div class="statistical">共有${impact_num}个碰撞视频</div>
    </section>
</div>
</body>
</html>
