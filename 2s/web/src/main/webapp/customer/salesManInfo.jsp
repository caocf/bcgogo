<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: lw
  Date: 12-17-13
  Time: 下午5:15
  @Deprecated by zhangjuntao
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>新增/修改员工信息</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/employerInfo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <style>
        #ui-datepicker-div, .ui-datepicker {
            font-size: 90%;
        }
    </style>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/salesManInfo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/member<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        jQuery(document).ready(function() {

            jQuery("#div_close,#cancelBtn").click(function() {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox").src = "";

                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            });

            jQuery("#careerDateStr")
                    .bind("click", function () {
                        jQuery(this).blur();
                    })
                    .datepicker({
                        "numberOfMonths":1,
                        "showButtonPanel":true,
                        "changeYear":true,
                        "changeMonth":true,
                        "yearRange":"c-100:c+100",
                        "yearSuffix":""
                    });

        });
    </script>
</head>


<body>

<div class="i_searchBrand">
    <form:form commandName="salesManDTO" id="salesManForm" action="member.do?method=updateSalesManInfo"
               method="post" onsubmit="return checkFormData()">
        <form:hidden path="shopId" value="${salesManDTO.shopId}"/>
        <form:hidden path="id" value="${salesManDTO.id}"/>
        <div class="i_arrow"></div>
        <div class="i_upLeft"></div>
        <div class="i_upCenter">
            <div class="i_note" id="div_drag">新增/修改员工信息</div>
            <div class="i_close" id="div_close"></div>
        </div>
        <div class="i_upRight"></div>
        <div class="i_upBody">
            <table cellpadding="0" cellspacing="0" class="tabEmp">
                <col width="280">
                <col/>
                <tr>
                    <td>工&nbsp;&nbsp;&nbsp;&nbsp;号：<form:input path="salesManCode" value="${salesManDTO.salesManCode}"
                                                               maxlength="30" class="textbox" /></td>
                    <td>姓&nbsp;&nbsp;&nbsp;&nbsp;名：<form:input path="name" value="${salesManDTO.name}"
                                                               maxlength="6" class="textbox" /></td>
                </tr>
                <tr>
                    <td>性&nbsp;&nbsp;&nbsp;&nbsp;别：
                        <form:radiobutton path="sex" value="MALE"/>男
                        <form:radiobutton path="sex" value="FEMALE"/>女
                    </td>
                    <td>手机号码：<form:input id="mobile" path="mobile" value="${salesManDTO.mobile}" maxlength="20" class="textbox" /></td>
                </tr>
                <tr>
                    <td>部&nbsp;&nbsp;&nbsp;&nbsp;门：<form:input path="department" value="${salesManDTO.department}"
                                                               maxlength="30" class="textbox" /></td>
                    <td>职&nbsp;&nbsp;&nbsp;&nbsp;位：<form:input path="position" value="${salesManDTO.position}"
                                                               maxlength="30" class="textbox" /></td>
                </tr>
                <tr>
                    <td>基本工资：<form:input path="salary" value="${salesManDTO.salary}" maxlength="10" class="textbox" /></td>
                    <td>津&nbsp;&nbsp;&nbsp;&nbsp;贴：<form:input path="allowance" value="${salesManDTO.allowance}"
                                                               maxlength="10" class="textbox" /></td>
                </tr>
                <tr>
                    <td>身份证号：<form:input path="identityCard" value="${salesManDTO.identityCard}" maxlength="18" class="textbox" /></td>
                    <td>状&nbsp;&nbsp;&nbsp;&nbsp;态：
                        <form:radiobutton path="status" value="INSERVICE"/>在职
                        <form:radiobutton path="status" value="DEMISSION"/>离职
                        <form:radiobutton path="status" value="ONTRIAL"/>试用
                    </td>
                </tr>
                <tr>
                    <td>入职日期：<form:input id="careerDateStr" path="careerDateStr" name="careerDateStr"
                                    value="${salesManDTO.careerDateStr}" class="textbox" />
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><span class="remark">备&nbsp;&nbsp;&nbsp;&nbsp;注：</span>
                        <textarea rows="2" cols="40" name="memo" id="memo"
                                  value="${salesManDTO.memo}" class="textarea">${salesManDTO.memo}</textarea>
                    </td>
                </tr>
            </table>
            <div class="clear height"></div>
            <div class="btnInput">
                <input type="button" id="submitBtn" onclick="thisSubmit()" onfocus="this.blur();" value="确认">
                <input type="button" id="cancelBtn" onfocus="this.blur();" value="取消">
            </div>
        </div>
        <div class="i_upBottom">
            <div class="i_upBottomLeft"></div>
            <div class="i_upBottomCenter"></div>
            <div class="i_upBottomRight"></div>
        </div>
    </form:form>
</div>
</body>
</html>