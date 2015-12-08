<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>修改配置</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <%-- styles --%>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <link rel="stylesheet" type="text/css" href="styles/config.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
</head>
<script type="text/javascript">
    jQuery(function() {
//        window.parent.addHandle(document.getElementById('div_drag'), window);
        jQuery("#div_close,#cancleBtn").click(function() {
            window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
            window.parent.document.getElementById("mask").style.display = "none";
        });

        jQuery("#confirmBtn").click(function() {
            if (checkFormData() == false) {
                return;
            }
            window.parent.document.getElementById("mask").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
            jQuery('#modifyConfig_table').ajaxSubmit(function(data) {
                if (data == "succ") {
                    window.parent.nextPageNo = 1;
                    window.parent.searchConfig();
                }
        });
    });
    });
    function checkFormData() {
        var config_value = jQuery("#config_value").val().replace("", " ");
        if (config_value.toLowerCase() == "value") {
            alert("请使用非value的变量名！");
            return false;
        }
    }
</script>
<%--onsubmit="return checkFormData();"--%>

<body>
<form:form commandName="configDTO" id="modifyConfig_table" action="dataMaintenance.do?method=saveOrUpdateConfig"
           method="post">
<form:hidden path="shopId" value="${configDTO.shopId}"/>
<div  id="div_show">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="config_title" id="div_drag">修改配置</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <table cellpadding="0" id="configTable" cellspacing="0" class="configTable">
            <col width="100">
            <col  width="100"/>

            <tr>
                <td class="label">key</td>
                <td>
                    <span>${configDTO.name}</span>
                    <form:hidden path="name" value="${configDTO.name}"  id="config_key" class="txt"/>
                </td>
            </tr>
            <tr>
                <td class="label">value</td>
                <td><form:input path="value" value="${configDTO.value}" id="config_value" class="txt"/></td>
            </tr>
            <tr>
                <td class="label">描述</td>
                <td><form:input path="description" value="${configDTO.description}" class="txt"/></td>
            </tr>
                <%--<tr>--%>
                <%--<td class="label">店铺</td>--%>
                <%--<td>--%>
                <%--<label><input type="radio" name="radio" id="radio_check1"/>所有</label>--%>
                <%--<label><input type="radio" name="radio" id="radio_check2"/>单个店铺</label>--%>
                <%--</td>--%>
                <%--</tr>--%>
        </table>
        <div class="more_his">
            <input type="button" value="确认" onfocus="this.blur();" class="btn" id="confirmBtn"/>
            <input type="button" value="取消" onfocus="this.blur();" class="btn" id="cancleBtn"/>
        </div>
    </div>
    </form:form>


</body>
</html>