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
    <title>一键救援</title>
</head>
<body>
<div id="wrapper" class="t-wp">
    <section class="content">
    <c:forEach items="${oneKeyRescueDTOList}" var="oneKeyRescueDTOList" varStatus="status" >
        <div class="trajectory">
            <div class="illegal_li">
                <div class="line">
                    <div class="w45 fl">车牌号</div>
                    <div class="w55 grey_txt fr tr">${oneKeyRescueDTOList.vehicleNo}</div>
                    <div class="clr"></div>
                </div>
                <div class="line">
                <c:forEach items="${oneKeyRescueDTOList.accident_mobile}" var="accident_mobile" varStatus="status" >
                    <div class="line2">
                        <div class="w45 fl">事故专员电话</div>
                        <div class="w55 grey_txt fr tr">${accident_mobile}<a href="tel:${accident_mobile}" class="w20 fr"><img src="/web/images/wx_mirror_images/phone.png"/></a></div>
                        <div class="clr"></div>
                    </div>
                </c:forEach>
                </div>
                <div class="line">
                    <div class="w45 fl">后视镜问题反馈</div>
                    <div class="w55 grey_txt fr tr">${oneKeyRescueDTOList.mirror_mobile}<a href="tel:${oneKeyRescueDTOList.mirror_mobile}" class="w20 fr"><img src="/web/images/wx_mirror_images/phone.png"/></a></div>
                    <div class="clr"></div>
                </div>
                <c:if test="${oneKeyRescueDTOList.insuranceCompanyDTO.mobile!=null}">
                    <div class="line">
                        <div class="w45 fl">${oneKeyRescueDTOList.insuranceCompanyDTO.name}</div>
                        <div class="w55 grey_txt fr tr">${oneKeyRescueDTOList.insuranceCompanyDTO.mobile}<a href="tel:${oneKeyRescueDTOList.insuranceCompanyDTO.mobile}" class="w20 fr"><img src="/web/images/wx_mirror_images/phone.png"/></a></div>
                        <div class="clr"></div>
                    </div>
                </c:if>
                <c:forEach items="${oneKeyRescueDTOList.insuranceCompanyDTOs}" var="insuranceCompanyDTO" varStatus="status" >
                <div class="line">
                    <div class="w45 fl">${insuranceCompanyDTO.name}</div>
                    <div class="w55 grey_txt fr tr">${insuranceCompanyDTO.mobile}<a href="tel:${insuranceCompanyDTO.mobile}" class="w20 fr"><img src="/web/images/wx_mirror_images/phone.png"/></a></div>
                    <div class="clr"></div>
                </div>
                </c:forEach>
            </div>
        </div>
    </c:forEach>
    </section>
</div>
</body>
</html>
