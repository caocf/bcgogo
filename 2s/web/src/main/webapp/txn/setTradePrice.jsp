<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: zyj
  Date: 12-4-12
  Time: 上午11:25
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Simple jsp page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/setSale<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            document.getElementById("div_close").onclick = function () {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox_Limit").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox_Limit").src = "";
                if (jQuery("#btnSaleja", parent.document)) {
                    jQuery("#btnSaleja", parent.document).focus().select();
                }
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            }
//			window.parent.addHandle(document.getElementById('div_drag'), window);

            var radioPercent = document.getElementById("radio_percent");
            var radioValue = document.getElementById("radio_value");
            var inputPercent = document.getElementById("input_percent");
            var inputValue = document.getElementById("input_value");
            radioPercent.onclick = function () {
                inputPercent.disabled = false;
                inputPercent.select();
                inputPercent.focus();
                radioValue.checked = false;
                inputValue.disabled = true;
                inputValue.value = "";
            }
            radioValue.onclick = function () {
                inputValue.disabled = false;
                inputValue.select();
                inputValue.focus();
                radioPercent.checked = false;
                inputPercent.disabled = true;
                inputPercent.value = "";
            }
            radioPercent.click();

            document.getElementById("submit").onclick = function () {
                if ((dataTransition.simpleRounding(inputPercent.value, 1) == 0) &&
                        (dataTransition.simpleRounding(inputValue.value, 1) == 0)) {
                    return;
                }
                if (isNaN(inputPercent.value) || isNaN(inputValue.value)) {
                    document.getElementById("input_percent").value = "";
                    document.getElementById("input_value").value = "";
                    if (radioPercent.checked) {
                        document.getElementById("input_percent").focus();
                    } else if (radioValue.checked) {
                        document.getElementById("input_value").focus();
                    }
                    alert("请输入正确的数字!");
                    return false;
                }
                if ((dataTransition.simpleRounding(inputPercent.value, 1) < 0) ||
                        (dataTransition.simpleRounding(inputValue.value, 1) < 0)) {

                    alert("请输入大于0的数字!");
                    return false;
                }
                window.parent.setMultipleTradePrice(inputPercent.value, inputValue.value);
                document.getElementById("div_close").click();
            }
            document.getElementById("cancel").onclick = function () {
                document.getElementById("div_close").click();
            }
        });

        /**
         *    设置销售价精度
         */
        jQuery(document).ready(function() {
            jQuery("#input_percent,#input_value").live("blur", function() {
                jQuery(this).val(dataTransition.simpleRounding(jQuery(this).val(), 1));
                if (jQuery(this).val() == "0") {
                    jQuery(this).val("");
                }
            });
        });
    </script>
</head>
<body>
<div class="tabSale" id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">设定批发价</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <div class="boxContent"><input id="radio_percent" type="radio" name="setMoney"/>加价<input id="input_percent"
                                                                                                 type="text"
                                                                                                 class="jiajia"/>%
        </div>
        <div class="boxContent"><input id="radio_value" type="radio" name="setMoney"/>加价<input id="input_value"
                                                                                               type="text"
                                                                                               class="jiajia"/>元
        </div>
        <div class="addInput">
            <input id="submit" type="button" value="确定" class="cancel" onfocus="this.blur();"/>
            <input id="cancel" type="button" value="取消" class="cancel" onfocus="this.blur();"/>
        </div>
    </div>
    <%--<div class="i_upBottom clear">--%>
    <%--<div class="i_upBottomLeft"></div>--%>
    <%--<div class="i_upBottomCenter"></div>--%>
    <%--<div class="i_upBottomRight"></div>--%>
    <%--</div>--%>
</div>
</body>
</html>