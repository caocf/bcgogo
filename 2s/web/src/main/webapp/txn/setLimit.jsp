<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=7"/>
    <title>设定库存下限、上限告警值</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            document.getElementById("div_close").onclick = function () {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox_Limit").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox_Limit").src = "";
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            }
        });

        $(function () {
            var lowerLimitVal = "", upperLimitVal = "";
            $("#submit").bind("click", function () {
                lowerLimitVal = $("#lowerLimit").val() * 1;
                upperLimitVal = $("#upperLimit").val() * 1;
                if (upperLimitVal < lowerLimitVal && upperLimitVal != 0 && lowerLimitVal != 0) {
                    var temp = lowerLimitVal;
                    lowerLimitVal = upperLimitVal;
                    upperLimitVal = temp;
                } else if (upperLimitVal == lowerLimitVal && upperLimitVal != 0 && lowerLimitVal != 0) {
                    upperLimitVal++;
                }
                $("input[id$='.lowerLimit']", window.parent.document).each(function () {
                    $(this).val(lowerLimitVal);
                });
                $("input[id$='.upperLimit']", window.parent.document).each(function () {
                    $(this).val(upperLimitVal);
                });
                if ($("#task").val() == "true") {
                    window.parent.updateLimit();
                }
                upperLimitVal = "";
                lowerLimitVal = "";
                $("#lowerLimit").val("");
                $("#upperLimit").val("");
                $("#div_close").click();
            });

            $("#cancel").bind("click", function () {
                upperLimitVal = "";
                lowerLimitVal = "";
                $("#lowerLimit").val("");
                $("#upperLimit").val("");
                $("#div_close").click();
            });

	        $("#lowerLimit,#upperLimit").bind("keydown", function (evt) {
		        jQuery(this).val(APP_BCGOGO.StringFilter.priceFilter(jQuery(this).val(), 0));
	        }).bind("blur", function () {
		        jQuery(this).val(APP_BCGOGO.StringFilter.priceFilter(jQuery(this).val(), 0));
	        });
        });

    </script>
</head>
<body>
<input id="task" value="${task}" type="hidden"/>

<div class="tab_repay_limit">
    <div class="i_arrow_limit"></div>
    <div class="i_upLeft_limit"></div>
    <div class="i_upCenter_limit">
        <div class="i_note_limit" id="div_drag">设定库存下限、上限告警值</div>
        <div class="i_close_limit" id="div_close"></div>
    </div>
    <div class="i_upRight_limit"></div>
    <div class="i_upBody_limit">
        <div class="boxContent"><span>下限</span><input id="lowerLimit" type="text" class="txtLimit"/></div>
        <div class="boxContent"><span>上限</span><input id="upperLimit" type="text" class="txtLimit"/></div>
        <div class="boxlimit">输入告警数量，负数无效；输入"0",清除告警设置</div>
        <div class="addInput">
            <input id="cancel" type="button" value="取消" class="cancel" onfocus="this.blur();"/>
            <input id="submit" type="button" value="确定" class="cancel" onfocus="this.blur();"/>
        </div>
    </div>
</div>
</body>
</html>
