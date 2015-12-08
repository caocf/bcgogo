<%--
  Created by IntelliJ IDEA.
  User: cfl
  Date: 12-6-5
  Time: 下午2:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.bcgogo.config.ConfigController" %>
<!--[if IE]>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<![endif]-->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>打印-查询日期</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <style>
        #ui-datepicker-div, .ui-datepicker {
            font-size: 85%;
        }
    </style>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery-ui-i18n.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $.datepicker.setDefaults($.datepicker.regional[ "zh-CN" ]);
            $("#startDateStr, #endDateStr").datepicker({
                "numberOfMonths":1,
                "showButtonPanel":true,
                "changeYear":true,
                "changeMonth":true,
                "yearRange":"c-100:c+100",
                "yearSuffix":"",
                "dateFormat":"yy-mm-dd",
                "onClose":function(dateText, inst) {
                    if (dateText === "") {
                        alert("日期不能为空");
                        inst.input.val(inst.lastVal);
                    }
                }
            });

            $("#startDateStr, #endDateStr").bind("click", function(e) {
                 $(this).blur();
            });

            $("#div_close,#cancle").click(function () {
                $("#showqueryDate,#mask", parent.document).hide();

                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }

                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            });

            $("#queryBusinessStatData").click(function () {
                var startDateStr = $("#startDateStr").val();
                var endDateStr = $("#endDateStr").val();
                var startYear = startDateStr.substring(0, 4);
                var startMonth = startDateStr.substring(5, 7);
                var startDay = startDateStr.substring(8, 10);
                var endYear = endDateStr.substring(0, 4);
                var endMonth = endDateStr.substring(5, 7);
                var endDay = endDateStr.substring(8, 10);
                var startDate = new Date(startYear, startMonth, startDay);
                var endDate = new Date(endYear, endMonth, endDay);
                if (startYear * 1 != endYear * 1) {
                    alert("暂时不支持跨年查询");
                    return;
                }
                if (startDate.valueOf() > endDate.valueOf()) {
                    alert("开始时间不能大于结束时间");
                    return;
                }
                window.showModalDialog("businessStat.do?method=getBusinessStatToPrint&startDateStr=" + startDateStr + "&endDateStr=" + endDateStr + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
                $("#div_close").click();
            });
        });

    </script>
</head>
<body>
<div class="tab_repay tab_modify clear">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">选择查询日期</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <div class="boxContent">
            <span>从</span>
            <div class="startTime">
                <input id="startDateStr" type="text" name="startDateStr" value="${queryDateStr}" readonly="true" />
            </div>
            <span>至</span>
            <div class="startTime">
                <input id="endDateStr" type="text" name="endDateStr" value="${queryDateStr}" readonly="true" />
            </div>
        </div>
        <div class="addInput">
            <input type="button" id="queryBusinessStatData" value="确定" class="cancel" onfocus="this.blur();"/>
            <input type="button" id="cancle" value="取消" class="cancel" onfocus="this.blur();"/>
        </div>
    </div>
    <div class="i_upBottom clear">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
</body>
</html>