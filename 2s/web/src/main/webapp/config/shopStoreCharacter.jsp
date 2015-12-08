<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-17
  Time: 下午6:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>店面特色</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        $().ready(function() {
            $("#div_close")[0].onclick = function() {
                $("#mask", parent.document)[0].style.display = "none";
                $("#iframe_PopupBox", parent.document)[0].style.display = "none";
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            }

            if ($("#span_storeCharacter", parent.document)[0].value) {
                var str = $("#span_storeCharacter", parent.document)[0].value.split(",");

                var checks = document.getElementsByName("checkbox");

                for (var i = 0, l = str.length; i < l; i++) {
                    if (checkFeature(checks, str[i])) {
                    }
                    else {
                        if (str[i].substring(0, 2) == "车型") {
                            $("#check_carModel")[0].checked = true;
                            $("#input_carModel")[0].value = str[i].substr(2);
                        }
                        else if (str[i].substring(0, 2) == "其他") {
                            $("#check_other")[0].checked = true;
                            $("#input_other")[0].value = str[i].substr(2);
                        }
                    }
                }
            }

            $("#input_carModel")[0].onfocus = function() {
                $("#check_carModel")[0].checked = true;
            }

            $("#input_other")[0].onfocus = function() {
                $("#check_other")[0].checked = true;
            }

            $("#a_confirm")[0].onclick = function() {
                var checks = document.getElementsByName("checkbox");
                var showValue = "";
                for (var i = 0, l = checks.length; i < l; i++) {
                    if (checks[i].checked) {
                        showValue += "," + checks[i].parentNode.lastChild.nodeValue;
                    }
                }

                if ($("#check_carModel")[0].checked && $("#input_carModel")[0].value) {
                    showValue += ",车型" + $("#input_carModel")[0].value;
                }

                if ($("#check_other")[0].checked && $("#input_other")[0].value) {
                    showValue += ",其他" + $("#input_other")[0].value;
                }

                $("#span_storeCharacter", parent.document)[0].value = showValue.substr(1);

                $("#div_close")[0].onclick();
            }
        });

        function checkFeature(checks, value) {
            for (var i = 0, l = checks.length; i < l; i++) {
                if (checks[i].parentNode.lastChild.nodeValue == value) {
                    checks[i].checked = true;
                    return true;
                }
            }
        }
    </script>
</head>

<body>
<div class="register_storeCharacter" id="div_show">
    <div class="content_title">
        <div class="content_titleleft"></div>
        <div class="content_titlebody">
            <div class="content_button" id="div_drag">
                <a href="javascript:void(0);" id="a_confirm">[确认]</a>
                <a href="javascript:void(0);" id="div_close">[取消]</a>
            </div>
            店面特色
        </div>
        <div class="content_titleright"></div>
    </div>
    <div class="storeCharacter">
        <label><input type="checkbox" name="checkbox"/>高档车</label>
        <label><input type="checkbox" name="checkbox"/>中档车</label>
        <label><input type="checkbox" name="checkbox"/>低档车</label>

        <div class="models"><label><input type="checkbox" id="check_carModel"/>车型</label><input id="input_carModel"
                                                                                                type="text"
                                                                                                class="carModels"/>
        </div>
        <label><input type="checkbox" name="checkbox"/>上门服务</label>
        <label><input type="checkbox" name="checkbox"/>免费车接送</label>

        <div class="models"><label><input type="checkbox" id="check_other"/>其他</label><input id="input_other"
                                                                                             type="text"
                                                                                             class="carModels"/></div>
    </div>
</div>
</body>
</html>