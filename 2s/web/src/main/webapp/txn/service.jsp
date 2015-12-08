<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>任务弹出框</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extensin/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/service<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">

        (function() {
            $(document).ready(function() {
                var checks = document.getElementsByName("checkbox");
                for (var i = 0; i < checks.length; i++) {
                    checks[i].onclick = function() {
                        if (this.checked) {
                            this.parentNode.className = "label_check c_off";
                        }
                        else {
                            this.parentNode.className = "label_check c_on";
                        }
                    }
                }
            });
        })();

        $().ready(function() {
            $("#datetime").datetimepicker({
                "numberOfMonths" : 1,
                "showButtonPanel": true,
                "changeYear":true,
                "changeMonth":true
            });
        });

    </script>
</head>

<body>
<form:form commandName="scheduleServiceEventDTO" action="txn.do?method=updateService" method="post">
    <form:hidden path="customerId" value="${scheduleServiceEventDTO.customerId}"/>
    <form:hidden path="vechicleId" value="${scheduleServiceEventDTO.vechicleId}"/>
    <form:hidden path="shopId" value="${repairRemindEventDTO.shopId}"/>
    <form:hidden path="id" value="${scheduleServiceEventDTO.id}"/>
    <form:hidden path="serviceDateStr" value=""/>
    <div class="tab_repay">
        <div class="i_upLeft"></div>
        <div class="i_upCenter">
            <div class="i_note">任 务</div>
            <div class="i_close" id="div_close"></div>
        </div>
        <div class="i_upRight"></div>
        <div class="i_upBody">
            <div class="tab_repayTime">时间：<span
                    id="datetime">${scheduleServiceEventDTO.serviceDateStr}</span></div>
            <div class="tab_content">
                <div class="contentTitle">主要内容：</div>
                <form:input path="content" value="${scheduleServiceEventDTO.content}"/></div>
            <div class="tab_content">
                <div class="matter">事项类别：</div>
                <div class="i_checkBox"><form:checkbox path="serviceTypeArr" value="1"/>保养</div>
                <div class="i_checkBox"><form:checkbox path="serviceTypeArr" value="2"/>维修</div>
                <div class="i_checkBox"><form:checkbox path="serviceTypeArr" value="3"/>美容</div>
                <div class="i_checkBox"><form:checkbox path="serviceTypeArr" value="4"/>保险</div>
                <div class="i_checkBox"><form:checkbox path="serviceTypeArr" value="5"/>销售</div>
                <div class="i_checkBox"><form:checkbox path="serviceTypeArr" value="6"/>其他</div>
            </div>
            <div class="sure"><input id="confirmBtn" type="button" value="确定" onfocus="this.blur();"/></div>
        </div>
        <div class="i_upBottom">
            <div class="i_upBottomLeft"></div>
            <div class="i_upBottomCenter"></div>
            <div class="i_upBottomRight"></div>
        </div>
    </div>
</form:form>
</body>
</html>
