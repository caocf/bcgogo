<%@ page import="com.bcgogo.config.ConfigController" %>
<!--[if IE]>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<![endif]-->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title></title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(window).load(function() {
            var id = '${id}';
            var isSubmit = '${isSubmit}';
            var flag = '${flag}';
            document.getElementById("div_close").onclick = function() {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBoxMakeTime").style.display = "none";
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            }
            document.getElementById("thisCar").onclick = function() {
                window.parent.document.getElementById(id + ".productType").value = "3";
                if (window.parent.document.getElementById(id + ".isNewAdd") != null) {
                    window.parent.document.getElementById(id + ".isNewAdd").value = "1";
                }
                //判断是否缺料和数量为0
                if (window.parent.document.getElementById(id + ".amount").value * 1 == 0) {
                    alert("数量不能为零，请重新输入。");
                    window.parent.document.getElementById("mask").style.display = "none";
                    window.parent.document.getElementById("iframe_PopupBoxMakeTime").style.display = "none";
                    return false;
                }
                var lackNum = window.parent.document.getElementById(id + ".inventoryAmount").value * 1 -
                        window.parent.document.getElementById(id + ".amount").value * 1;
                if (lackNum < 0) {
                    alert("本商品缺料" + (lackNum * -1) + "件，请尽快安排采购入库。");
                }
                window.parent.document.getElementById("iframe_PopupBoxMakeTime").style.display = "none";
                window.parent.document.getElementById("mask").style.display = "none";
                if (isSubmit == "true") {
                    window.parent.document.getElementById("idAddRow").onclick = submitThisForm(flag);
                } else if (isSubmit == "add") {
                    window.parent.document.getElementById("idAddRow").click();
                } else if (isSubmit == "search") {
                    window.parent.confirmModification(id);
                }
            }
            document.getElementById("multiple").onclick = function() {
                window.parent.document.getElementById(id + ".productType").value = "0";
                if (window.parent.document.getElementById(id + ".isNewAdd") != null) {
                    window.parent.document.getElementById(id + ".isNewAdd").value = "1";
                }
                //判断是否缺料和数量为0
                if (window.parent.document.getElementById(id + ".amount").value * 1 == 0) {
                    alert("数量不能为零，请重新输入。");
                    window.parent.document.getElementById("mask").style.display = "none";
                    window.parent.document.getElementById("iframe_PopupBoxMakeTime").style.display = "none";
                    return false;
                }
                var lackNum = window.parent.document.getElementById(id + ".inventoryAmount").value * 1 -
                        window.parent.document.getElementById(id + ".amount").value * 1;
                if (lackNum < 0) {
                    alert("本商品缺料" + (lackNum * -1) + "件，请尽快安排采购入库。");
                }
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBoxMakeTime").style.display = "none";
                if (isSubmit == "true") {
                    window.parent.document.getElementById("idAddRow").onclick = submitThisForm(flag);
                } else if (isSubmit == "add") {
                    window.parent.document.getElementById("idAddRow").click();
                } else if (isSubmit == "search") {
                    window.parent.confirmModification(id);
                }
            }
            window.parent.document.getElementById("iframe_PopupBoxMakeTime").style.display = "block";
//            window.parent.addHandle(document.getElementById('div_drag'), window);
        });
    </script>
    <link rel="stylesheet" type="text/css" href="style/up<%=ConfigController.getBuildVersion()%>.css"/>
</head>
<body>
<div class="tab_repay" id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">提示信息</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <div class="tab_repayTime">新增此商品，请确认适用于</div>
        <div class="sure">
            <input type="button" id="thisCar" value="本车型"/>
            <input type="button" id="multiple" value="多款"/>
        </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
</body>
</html>
