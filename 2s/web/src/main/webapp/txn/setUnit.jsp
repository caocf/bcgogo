<%@ page import="com.bcgogo.config.ConfigController" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>设定销售单位</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/setSale<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            document.getElementById("div_close").onclick = function () {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox_unit").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox_unit").src = "";
                try {
               		 window.parent.cancelSetUnit();
//                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    throw new Error("cancelSetUnit Error");
                }
				self.frameElement.blur();
				$(window.top.document.body)
					.append($("<input type='text' id='focusBackToMe'>"))
					.find("#focusBackToMe")
					.focus()
					.select()
					.remove();
            }

            var radio1 = document.getElementById("radio_unit1");
            var radio2 = document.getElementById("radio_unit2");
            var rate1 = document.getElementById("input_rate1");
            var rate2 = document.getElementById("input_rate2");
            var rateVale;
            radio1.onclick = function () {
                rate1.disabled = false;
                rate1.select();
                rate1.focus();
                radio2.checked = false;
                rate2.disabled = true;
                rate1.value = "";
                rate2.value = "";
                rateVale = '';
            }
            radio2.onclick = function () {
                rate2.disabled = false;
                rate2.select();
                rate2.focus();
                radio1.checked = false;
                rate1.disabled = true;
                rate1.value = "";
                rate2.value = "";
                rateVale = '';
            }
            radio1.click();

            rate1.onkeyup = function (evt) {
                evt = evt || event;
                var k = evt.keyCode || evt.which;
                if (isNaN(rate1.value)) {
                    rate1.value = rateVale;
                }
                if (rate1.value == '0') {
                    rate1.value = '';
                    return;
                }
                if ((k >= 96 && k <= 105) || (k >= 48 && k <= 57)) {
                    if (rate1.value.substring(0, 1) == '0') {
                        rate1.value = rateVale;
                        return;
                    }
                    rateVale = rate1.value;
                } else if (k == 8) {
                    rateVale = rate1.value;
                } else if (k == 13 || k == 108) {
                    document.getElementById("submit").click();
                }
                else {
                    rate1.value = rateVale;
                }
            }
            rate2.onkeyup = function (evt) {
                evt = evt || event;
                var k = evt.keyCode || evt.which;
                if (isNaN(rate2.value)) {
                    rate2.value = rateVale;
                }
                if (rate2.value == '0') {
                    rate2.value = '';
                    return;
                }
                if ((k >= 96 && k <= 105) || (k >= 48 && k <= 57)) {
                    if (k == 96 || k == 48) {
                        if (rate1.value.substring(0, 1) == '0') {
                            rate1.value = rateVale;
                            return;
                        }
                    }
                    rateVale = rate1.value;
                } else if (k == 8) {
                    rateVale = rate2.value;
                } else if (k == 13 || k == 108) {
                    document.getElementById("submit").click();
                } else {
                    rate2.value = rateVale;
                }
            }


            document.getElementById("submit").onclick = function () {
                if (isNaN(rate1.value) || isNaN(rate2.value)) {
                    return false;
                }
                if (rate1.value != "" || rate2.value != "") {
                    if (radio1.checked) {
                        window.parent.submitSetUnit(jQuery.trim($("#storageUnit1").text()),
                                jQuery.trim($("#sellUnit1").text()), $("#input_rate1").val());
                    } else if (radio2.checked) {
                        window.parent.submitSetUnit(jQuery.trim($("#storageUnit2").text()),
                                jQuery.trim($("#sellUnit2").text()), $("#input_rate2").val())
                    }
                    window.parent.document.getElementById("mask").style.display = "none";
                    window.parent.document.getElementById("iframe_PopupBox_unit").style.display = "none";
                    window.parent.document.getElementById("iframe_PopupBox_unit").src = "";
					self.frameElement.blur();
					$(window.top.document.body)
						.append($("<input type='text' id='focusBackToMe'>"))
						.find("#focusBackToMe")
						.focus()
						.select()
						.remove();
                } else {
                    return;
                }
            }
            document.getElementById("cancel").onclick = function () {
                document.getElementById("div_close").click();
            }

            initUnitSpan();
            function initUnitSpan() {
                var unitIdPrefix = $("#unitId_hidden").val();
                unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
                $("#storageUnit1").text($("#" + unitIdPrefix + "\\.storageUnit", parent.document).val());
                $("#sellUnit1").text($("#" + unitIdPrefix + "\\.unit", parent.document).val());
                $("#storageUnit2").text($("#" + unitIdPrefix + "\\.unit", parent.document).val());
                $("#sellUnit2").text($("#" + unitIdPrefix + "\\.storageUnit", parent.document).val());
            }
        });
    </script>
</head>
<body>
<div class="tab_repay tab_modify clear" id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter" style="width:180px">
        <div class="i_note" id="div_drag" style="width:130px"><span>销售单位换算</span></div>
        <div class="i_close" id="div_close" style="float:right"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody" style="width:162px">
        <input id="unitId_hidden" type="hidden" value="${unitId}"/>

        <div class="boxContent_unit">
            <input id="radio_unit1" type="radio" name="setUnit" checked="checked"/>
            1<span id="storageUnit1"></span>=
            <input id="input_rate1" value="" type="text" class="jiajia rate_input_color"/>
            <span id="sellUnit1"></span>
        </div>
        <div class="boxContent_unit">
            <input id="radio_unit2" type="radio" name="setUnit"/>
            1<span id="storageUnit2"></span>=
            <input id="input_rate2" type="text" value="" class="jiajia"/>
            <span id="sellUnit2"></span>
            <br>
            <span style="color:red;">数量单位仅能修改一次</span>
        </div>
        <div class="addInput" align="right">
            <input id="submit" type="button" value="确定" class="cancel" onfocus="this.blur();"/>
            <input id="cancel" type="button" value="取消" class="cancel" onfocus="this.blur();"/>
        </div>
    </div>
</div>
</body>
</html>