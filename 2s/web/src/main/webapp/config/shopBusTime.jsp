<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-17
  Time: 下午6:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>营业时间</title>
<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">
    (function () {
        $(function () {
            $("#div_close").click(function () {
                parent.$("#mask").css({'display':'none'});
                parent.$("#iframe_PopupBox").css({'display':'none'});
                //window.parent.document.getElementById("iframe_PopupBox").src = "";

                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            });

            var hidden_busTime = $("#span_busTime", window.parent.document).val();
            if (hidden_busTime) {
                if (hidden_busTime == "24小时营业") {
                    $("#radio_check1")[0].checked = true;
                }
                else {
                    var str = hidden_busTime;
                    str = str.substring(0, str.length - 2);
                    var str1 = str.split("~");
                    var str2 = str1[0].split("：");
                    var str3 = str1[1].split("：");
                    $("#input_starttime_hour")[0].value = str2[0];
                    $("#input_starttime_minu")[0].value = str2[1];
                    $("#input_endtime_hour")[0].value = str3[0];
                    $("#input_endtime_minu")[0].value = str3[1];
                    $("#radio_check2")[0].checked = true;
                }
            }
//                 $("#input_starttime_hour").onkeydown(function(){
//                     nsDialog.jAlert("key down");
//                 });

            $("#input_starttime_hour")[0].onfocus = function () {
                $("#radio_check2")[0].checked = true;
            };

            $("#input_starttime_minu")[0].onfocus = function () {
                $("#radio_check2")[0].checked = true;
            };

            $("#input_endtime_hour")[0].onfocus = function () {
                $("#radio_check2")[0].checked = true;
            };

            $("#input_endtime_minu")[0].onfocus = function () {
                $("#radio_check2")[0].checked = true;
            };
//              $("#input_starttime_hour").bind("keydown",f1);
            $("#input_starttime_hour").bind("blur", f1);
            $("#input_starttime_minu").bind("blur", f2);
            $("#input_endtime_hour").bind("blur", f1);
            $("#input_endtime_minu").bind("blur", f2);

            $("#a_confirm").click(function () {
                if ($("#radio_check1")[0].checked) {
                    $("#span_busTime", window.parent.document)[0].value = $("#radio_check1")[0].parentNode.lastChild.nodeValue + "营业";
                }
                else if ($("#radio_check2")[0].checked) {
                    if (!f3($("#input_starttime_hour")[0])) {
//                            nsDialog.jAlert("输入非法字符，");
                        return;
                    }
                    if (!f4($("#input_starttime_minu")[0])) {
                        return;
                    }
                    if (!f3($("#input_endtime_hour")[0])) {
                        return;
                    }
                    if (!f4($("#input_endtime_minu")[0])) {
                        return;
                    }

                    $("#span_busTime", window.parent.document)[0].value = $("#input_starttime_hour")[0].value + "：" +
                            $("#input_starttime_minu")[0].value + "~" +
                            $("#input_endtime_hour")[0].value + "：" +
                            $("#input_endtime_minu")[0].value + "营业";
                }
                $("#div_close").click();
            });
        });

        var re = /^[0-5]+[0-9]$/;

        function f1() {
            var value = this.value;
            if (!value||isNaN(value)) {
                nsDialog.jAlert("请输入数字！");
                return false;
            }

            if (!(value >= 0 && value <= 23)) {
                nsDialog.jAlert("0~23之间的数字(包括0和23)！");
                return false;
            }
            return true;
        }

        function f2() {
            var value = this.value;
            if (!value) {
                nsDialog.jAlert("请输入数字！");
                return false;
            }
            if (!re.test(value)) {
                nsDialog.jAlert("0~59之间的数字(包括0和59)！");
                return false;
            }
            return true;
        }

        function f3(obj) {
            var value = obj.value;
            if (!value||isNaN(value)) {
                nsDialog.jAlert("请输入数字！");
                return false;
            }

            if (!(value >= 0 && value <= 23)) {
                nsDialog.jAlert("小时数在0~23之间的数字(包括0和23)！");
                return false;
            }
            return true;
        }

        function f4(obj) {
            var value = obj.value;
            if (!value) {
                nsDialog.jAlert("请输入数字！");
                return false;
            }
            if (!re.test(value)) {
                nsDialog.jAlert("0~59之间的数字(包括0和59)！");
                return false;
            }
            return true;
        }
    })();
    var reg1 = /^(([1-9]+[0-9]*.{1}[0-9]+)|([0].{1}[1-9]+[0-9]*)|([1-9][0-9]*)|([0][.][0-9]+[1-9]*))$/;//正数
    var re = /^[0-5]+[0-9]$/;

    function validateInputData() {
        var srcId;
        //判断浏览器是否属于IE
        if (window.ActiveXObject) {
            srcId = event.srcElement.id;
        }
        //判断浏览器是否属于Mozilla,Safari
        else if (window.XMLHttpRequest) {
            srcId = event.target.id;
        }
        if (srcId == "input_starttime_hour") {
            var s_hour = $("#input_starttime_hour").val();
            var flag = (!reg1.test(s_hour));
            if (flag) {
                $("#input_starttime_hour")[0].value = "";
                nsDialog.jAlert("请输入正确数字！");
            }
        }
        else if (srcId == "input_starttime_minu") {
            var s_hour = $("#input_starttime_minu").val();
            var flag = (!re.test(s_hour));
            if (flag) {
                $("#input_starttime_minu")[0].value = "";
                nsDialog.jAlert("请输入正确数字！");
            }
        }
        else if (srcId == "input_endtime_hour") {
            var s_hour = $("#input_endtime_hour")[0].value;
            var flag = (!reg1.test(s_hour));
            if (flag) {
                $("#input_endtime_hour")[0].value = "";
                nsDialog.jAlert("请输入正确数字！");
            }
        }
        else if (srcId == "input_endtime_minu") {
            var s_hour = $("#input_endtime_minu")[0].value;
            var flag = (!re.test(s_hour));
            if (flag) {
                $("#input_endtime_minu")[0].value = "";
                nsDialog.jAlert("请输入正确数字！");
            }
        }
    }
</script>
</head>

<body>
<div class="register_bustime" id="div_show">
    <div class="content_title">
        <div class="content_titleleft"></div>
        <div class="content_titlebody">
            <div class="content_button" id="div_drag">
                <a href="javascript:void(0);" id="a_confirm">[确认]</a>
                <a href="javascript:void(0);" id="div_close">[取消]</a>
            </div>
            营业时间
        </div>
        <div class="content_titleright"></div>
    </div>
    <div class="bus_time">
        <label><input type="radio" name="radio_check" id="radio_check1"/>24小时</label>

        <div class="make_time">
            <label><input type="radio" name="radio_check" id="radio_check2"/></label>

            <div class="make_starttime"><input id="input_starttime_hour" type="text" style="width:23px;"
                                               onblur="validateInputData()" maxlength="2"/>：
                <input id="input_starttime_minu" type="text" style="width:23px;" onblur="validateInputData()"
                       maxlength="2"/></div>
            <span>~</span>

            <div class="make_endtime"><input id="input_endtime_hour" type="text" style="width:23px;"
                                             onblur="validateInputData()" maxlength="2"/>：<input
                    id="input_endtime_minu" type="text" style="width:23px;" onblur="validateInputData()" maxlength="2"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>