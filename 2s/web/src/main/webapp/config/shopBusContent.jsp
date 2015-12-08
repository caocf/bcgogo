<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-17
  Time: 下午6:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>经营内容</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        $().ready(function() {
            $("#div_close")[0].onclick = function() {
                $("#mask", window.parent.document)[0].style.display = "none";
                $("#iframe_PopupBox", window.parent.document)[0].style.display = "none";
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            }

            //绑定选中的复选框
            if ($("#hidden_busScope", window.parent.document)[0].value) {
                var buscope = $("#hidden_busScope", window.parent.document)[0].value.split(",");

                if (buscope && buscope.length > 0) {
                    for (var i = 0, l = buscope.length; i < l; i++) {
                        check(buscope[i]);
                    }
                }
            }

            $("#input_other")[0].onfocus = function() {
                $("#checkbox_other")[0].checked = true;
            }

            $("#input_mainproduct")[0].onfocus = function() {
                $("#check_mainproduct")[0].checked = true;
            }

            $("#a_confirm")[0].onclick = function() {
                var checks = document.getElementsByName("checkbox");
                var showValue = "";
                var value = "";

                for (var i = 0, l = checks.length; i < l; i++) {
                    if (checks[i].checked) {
                        showValue += "," + checks[i].parentNode.lastChild.nodeValue;
                        value += "," + checks[i].value;
                    }
                }

                if ($("#checkbox_other")[0].checked && $("#input_other")[0].value) {
                    showValue += "," + $("#input_other")[0].value;
                    value += "," + $("#checkbox_other")[0].value + $("#input_other")[0].value;
                }

                if ($("#check_mainproduct")[0].checked && $("#input_mainproduct")[0].value) {
                    showValue += "," + $("#input_mainproduct")[0].value;
                    value += "," + $("#check_mainproduct")[0].value + $("#input_mainproduct")[0].value;
                }

                if (!value) {
                    nsDialog.jAlert("请至少选择一个");
                }
                else {
                    $("#span_busScope", window.parent.document)[0].value = showValue.substr(1);
                    $("#hidden_busScope", window.parent.document)[0].value = value.substr(1);
                    $("#div_close")[0].onclick();
                }
            }

            //window.parent.addHandle($('div_drag'), window);
        });

        function check(value) {
            var checks = document.getElementsByName("checkbox");

            for (var i = 0, l = checks.length; i < l; i++) {
                if (value == checks[i].value) {
                    checks[i].checked = true;
                }
                else if (value.substring(0, 2) && value.substring(0, 2) == 33) {
                    $("#checkbox_other")[0].checked = true;
                    $("#input_other")[0].value = value.substr(2);
                }
                else if (value.substring(0, 2) && value.substring(0, 2) == 35) {
                    $("#check_mainproduct")[0].checked = true;
                    $("#input_mainproduct")[0].value = value.substr(2);
                }
            }
        }

    </script>
</head>

<body>
<div class="register_busContent" id="div_show">
    <div class="content_title">
        <div class="content_titleleft"></div>
        <div class="content_titlebody">
            <div class="content_button" id="div_drag">
                <a href="javascript:void(0);" id="a_confirm">[确认]</a>
                <a href="javascript:void(0);" id="div_close">[取消]</a>
            </div>
            经营产品
        </div>
        <div class="content_titleright"></div>
    </div>
    <table cellpadding="0" cellspacing="0" class="content_table">
        <tr>
            <td class="carMake">汽车维修</td>
            <td class="carBorder">
                <c:forEach var="businessDTOlt" items="${businessDTOList2}" varStatus="status">
                    <label style="display:inline-block;"><input type="checkbox" name="checkbox"
                                                                value="${businessDTOlt.id}"/><c:out
                            value="${businessDTOlt.content}"/></label>
                </c:forEach>
                <!--<label><input type="checkbox" name="checkbox"/>汽车保养</label>
                <label><input type="checkbox" name="checkbox"/>钣金喷漆</label>
                <label><input type="checkbox" name="checkbox"/>轮胎</label>
                <label><input type="checkbox" name="checkbox"/>汽车快修</label>
                <label><input type="checkbox" name="checkbox"/>汽车小修</label>
                <label><input type="checkbox" name="checkbox"/>汽车中修</label>
                <label><input type="checkbox" name="checkbox"/>汽车大修</label>-->
            </td>
        </tr>
        <tr>
            <td class="carDirecation">汽车装潢</td>
            <td class="carBorder">
                <c:forEach var="businessDTOlt" items="${businessDTOList10}" varStatus="status">
                    <c:choose>
                        <c:when test="${businessDTOlt.no != 33}">
                            <label style="display:inline-block;"><input type="checkbox" name="checkbox"
                                                                        value="${businessDTOlt.id}"/><c:out
                                    value="${businessDTOlt.content}"/></label>
                        </c:when>
                        <c:when test="${businessDTOlt.no == 33}">
                            <label style="display:inline-block;"><input type="checkbox" id="checkbox_other"
                                                                        value="${businessDTOlt.id}"/><c:out
                                    value="${businessDTOlt.content}"/></label>
                            <input type="text" class="carOther" id="input_other"/>
                        </c:when>
                    </c:choose>

                </c:forEach>
                <!--<label><input type="checkbox" name="checkbox"/>电脑洗车</label>
                <label><input type="checkbox" name="checkbox"/>人工洗车</label>
                <label><input type="checkbox" name="checkbox"/>车身彩贴</label>
                <label><input type="checkbox" name="checkbox"/>新车开腊</label>
                <label><input type="checkbox" name="checkbox"/>封釉美容</label>
                <label><input type="checkbox" name="checkbox"/>漆面打蜡</label>
                <label><input type="checkbox" name="checkbox"/>漆面抛光</label>
                <label><input type="checkbox" name="checkbox"/>漆面划痕修复</label>
                <label><input type="checkbox" name="checkbox"/>内部装饰</label>
                <label><input type="checkbox" name="checkbox"/>内饰桑拿</label>
                <label><input type="checkbox" name="checkbox"/>真皮座椅</label>
                <label><input type="checkbox" name="checkbox"/>中央门锁</label>
                <label><input type="checkbox" name="checkbox"/>DVD导航</label>
                <label><input type="checkbox" name="checkbox"/>车内真皮制品护理</label>
                <label><input type="checkbox" name="checkbox"/>便携导航</label>
                <label><input type="checkbox" name="checkbox"/>倒车雷达</label>
                <label><input type="checkbox" name="checkbox"/>汽车隔音</label>
                <label><input type="checkbox" name="checkbox"/>底盘装甲</label>
                <label><input type="checkbox" name="checkbox"/>汽车防爆膜</label>
                <label><input type="checkbox" name="checkbox"/>光触媒杀菌消毒</label>
                <label><input type="checkbox" name="checkbox"/>轮胎翻新</label>
                <label><input type="checkbox" name="checkbox"/>防盗器</label>
                <label><input type="checkbox" id="checkbox_other"/>其他</label>
                <input type="text" class="carOther" id="input_other"/>-->
            </td>
        </tr>
        <tr>
            <td class="carBuy">批发零售</td>
            <td style="padding:0px 10px 0px 10px;">
                <c:forEach var="businessDTOlt" items="${businessDTOList34}" varStatus="status">
                    <label style="display:inline-block;"><input type="checkbox" id="check_mainproduct"
                                                                value="${businessDTOlt.id}"/><c:out
                            value="${businessDTOlt.content}"/></label>
                </c:forEach>
                <!--<label><input type="checkbox" id="check_mainproduct"/>主要产品</label>-->
                <input type="text" class="carOther" style="width:300px;" id="input_mainproduct"/>
            </td>
        </tr>
    </table>

</div>
</body>
</html>