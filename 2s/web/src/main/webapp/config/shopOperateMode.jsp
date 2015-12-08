<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-17
  Time: 下午6:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>经营方式</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        $().ready(function () {
            $("#div_close")[0].onclick = function () {
                $("#mask", window.parent.document)[0].style.display = "none";
                $("#iframe_PopupBox", window.parent.document)[0].style.display = "none";
                //$("#iframe_PopupBox", window.parent.document).src = "";
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
            }
            };

            var hidden_radio = $("#span_operate", window.parent.document)[0].value;
            if (hidden_radio) {
                var radios = document.getElementsByName("radio");
                for (var i = 0, l = radios.length; i < l; i++) {
                    if (hidden_radio == radios[i].parentNode.lastChild.innerHTML) {
                        radios[i].checked = true;
                    }
                }

                if (hidden_radio.substring(0, 3) == "专卖店") {
                    $("#radio_check3")[0].checked = true;
                    $("#input_carbrand")[0].value = hidden_radio.substring(4, hidden_radio.length - 2);
                }

                if (hidden_radio.substring(0, 2) == "其他") {
                    $("#radio_check5")[0].checked = true;
                    $("#input_other")[0].value = hidden_radio.substring(3);
                }
            }


            $("#input_carbrand")[0].onfocus = function () {
                $("#radio_check3")[0].checked = true;
            };

            $("#input_other")[0].onfocus = function () {
                $("#radio_check5")[0].checked = true;
            };

            $("#a_confirm")[0].onclick = function () {
                if ($("#radio_check1")[0].checked) {
                    $("#span_operate", window.parent.document)[0].value = $("#span_rdo1")[0].innerHTML;
                }
                else if ($("#radio_check2")[0].checked) {
                    $("#span_operate", window.parent.document)[0].value = $("#span_rdo2")[0].innerHTML;
                }
                else if ($("#radio_check3")[0].checked) {
                    $("#span_operate", window.parent.document)[0].value = "专卖店:" + $("#input_carbrand")[0].value + "品牌";
                }
                else if ($("#radio_check4")[0].checked) {
                    $("#span_operate", window.parent.document)[0].value = $("#span_rdo4")[0].innerHTML;
                }
                else if ($("#radio_check5")[0].checked) {
                    $("#span_operate", window.parent.document)[0].value = "其他:" + $("#input_other")[0].value;
                }

                $("#div_close")[0].onclick();
            }
        });
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
            经营方式
        </div>
        <div class="content_titleright"></div>
    </div>
    <div class="storeCharacter">
        <label><input type="radio" name="radio" id="radio_check1"/><span id="span_rdo1">加盟连锁</span></label>
        <label><input type="radio" name="radio" id="radio_check2"/><span id="span_rdo2">有限公司</span></label>
        <label><input type="radio" name="radio" id="radio_check3"/>专卖店</label>

        <div class="brand"><input type="text" class="carbrand" id="input_carbrand"/>品牌</div>
        <label><input type="radio" name="radio" id="radio_check4"/><span id="span_rdo4">个体</span></label>

        <div class="brand"><label><input type="radio" name="radio" id="radio_check5"/>其他</label><input id="input_other"
                                                                                                       type="text"
                                                                                                       class="carbrand"/>
        </div>
    </div>
</div>
</body>
</html>