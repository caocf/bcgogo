<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 2015-1-6
  Time: 11:44
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>车险计算器</title>
    <meta content="text/html;charset=utf-8" http-equiv="Content-Type">
    <meta content="wangchunpeng" name="author">
    <meta content="True" name="HandheldFriendly">
    <meta content="width=device-width,user-scalable=no" name="viewport">
    <meta content="yes" name="apple-mobile-web-app-capable">
    <meta content="black" name="apple-mobile-web-app-status-bar-style">
    <meta content="telephone=no" name="format-detection">
    <meta content="on" http-equiv="cleartype">
    <link rel="stylesheet" type="text/css" href="/web/styles/mobile/wx1<%=ConfigController.getBuildVersion()%>.css">

</head>
<body>
<div class="wrap">
    <div class="container">
        <div class="td">
            <p class="td-first-p">
                <label>车辆地区：</label>
                    <span class="select select-area">
                        <select id="province">
                            <option value="110000">北京市</option>
                        </select>
                    </span>
                    <span class="select select-area selectright">
                        <select id="city">
                            <option ln_prefix="苏A" dpt="2108904" value="320100">南京市</option>
                        </select>
                    </span>
            </p>
            <p>
                <label>车辆号码：</label> <input placeholder="请输入车牌号码" style="width:50%;" maxlength="7" value="" val="苏E"
                                            id="license_num" text="text">
            </p>
        </div>
        <div class="td">
            <p>
                <label class="newcarlable"></label>
                <input type="checkbox" id="baby" class="checkbox">
                <label class="baby" for="baby"><strong>新车未上牌</strong></label>
            </p>
        </div>

        <div class="td">
            <p>
                <label>车辆总价：</label> <input type="text" maxlength="4" placeholder="请输入车辆总价" id="price" class="price">
                万</p>
            <p>
                <label>车主姓名：</label> <input type="text" placeholder="请输入您的姓名" id="yourname" class="inputwidth">
            </p>
            <p>
                <label>保险公司：</label>
                <input type="checkbox" checked="checked" value="pingan" name="comp" id="comp"
                       class="checkbox"><label for="comp">
                <img src="http://img.buding.cn/common/2014/04/23/db13a3ac837ba3bb0fe60437a44a30ce.jpeg"
                     class="pinganlogo"></label>
            </p>

            <div class="sub-b-p" style="text-align:center">
                <div class="center-div">
                    <button id="submitdata">快速报价</button>
                </div>

                <div class="clear"></div>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</div>
</body>
</html>
