<%@ page import="com.bcgogo.config.ConfigController" %>
<!--[if IE]>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<![endif]-->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="/WEB-INF/views/includes.jsp" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>无标题文档</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <style type="text/css">
        #ui-datepicker-div, .ui-datepicker {
            font-size: 90%;
        }
    </style>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/makeTime<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#div_close").bind("click", function () {
                $("#mask", parent.document).css("display", "none");
                $("#iframe_PopupBoxMakeTime", parent.document).css("display", "none");
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            });
//            window.parent.addHandle(document.getElementById('div_drag'), window);

            $("#datetime")
                    .bind("click", function () {
                        $(this).blur();
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
<form:form commandName="" action="" method="post">
    <div class="tab_repay" id="div_show">
        <div class="i_arrow"></div>
        <div class="i_upLeft"></div>
        <div class="i_upCenter">
            <div class="i_note" id="div_drag">设置还款时间</div>
            <div class="i_close" id="div_close"
                 style="background: url('images/close.jpg') no-repeat scroll 0px 6px transparent; width: 17px; height: 28px; cursor: pointer; left: 253px; display: inline; margin-top: 2px;"></div>
        </div>
        <div class="i_upRight"></div>
        <div class="i_upBody">
            <div class="tab_repayTime">时间：<input id="datetime" style="width: 190px" type="text" readonly="true"/></div>
            <div class="sure"><input type="button" id="confirmBtn" style="margin-right:20px" value="确定"
                                     onfocus="this.blur();"/></div>
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
