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
    <title>相关业务</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        $().ready(function() {
            $("#div_close")[0].onclick = function() {
                $("#mask", parent.document)[0].style.display = "none";
                $("#iframe_PopupBox", parent.document)[0].style.display = "none";
                //$("iframe_PopupBox", window.parent.document).src = "";

                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            }

            if ($("#span_relatedbus", parent.document)[0].value) {
                var str = $("#span_relatedbus", parent.document)[0].value.split(",");

                var checks = document.getElementsByName("checkbox");
                for (var i = 0, l = str.length; i < l; i++) {
                    if (!checkFeature(checks, str[i])) {
                        $("#check_other")[0].checked = true;
                        $("#input_other")[0].value = str[i];
                    }
                }
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

                if ($("#check_other")[0].checked && $("#input_other")[0].value) {
                    showValue += "," + $("#input_other")[0].value;
                }

                $("#span_relatedbus", parent.document)[0].value = showValue.substr(1);

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
<div class="register_relatedbus" id="div_show">
    <div class="content_title">
        <div class="content_titleleft"></div>
        <div class="content_titlebody">
            <div class="content_button" id="div_drag">
                <a href="javascript:void(0);" id="a_confirm">[确认]</a>
                <a href="javascript:void(0);" id="div_close">[取消]</a>
            </div>
            相关业务
        </div>
        <div class="content_titleright"></div>
    </div>
    <div class="relatedbus">
        <label><input type="checkbox" name="checkbox"/>救援</label>
        <label><input type="checkbox" name="checkbox"/>保险定损理赔</label>
        <label><input type="checkbox" name="checkbox"/>二手车</label>

        <div class="relatedOther"><label><input type="checkbox" id="check_other"/>其他</label><input id="input_other"
                                                                                                   type="text"
                                                                                                   class="writeother"/>
        </div>
    </div>
</div>
</body>
</html>