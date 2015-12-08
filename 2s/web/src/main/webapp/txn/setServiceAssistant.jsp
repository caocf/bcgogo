<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: lw
  Date: 12-10-16
  Time: 下午2:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>批量设定员工提成</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/memberSet<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/setConstruction<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-droplist-lite<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        jQuery(document).ready(function() {
            jQuery("#assistantPrice").live("keyup blur", function(event) {
                if (event.type == "focusout")
                    event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
                else if (event.type == "keyup")
                    if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
                        event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
                    }
            });
            jQuery("#div_close,#cancelBtn").click(function() {
                display();
            });


            jQuery("#saveBtn").click(function() {
                if (jQuery("#assistantPrice").val() == "") {
                    display();
                    return false;
                }
                if (isNaN(jQuery("#assistantPrice").val()) || jQuery("#assistantPrice").val() < 0) {
                    display();
                    return false;
                }

                $(".price", window.parent.document).each(function() {
                    if (!$(this).val()) {
                        $(this).val(0);
                    }
                });

                $(".percentageAmount", window.parent.document).each(function() {
                    if (!$(this).val()) {
                        $(this).val(0);
                    }
                });

                jQuery("#categoryServiceSearchForm", window.parent.document).attr("action", "category.do?method=updateServicePercentage" +
                        "&percentageAmount=" + jQuery("#assistantPrice").val() +
                        "&pageNo=" + jQuery("#pageNo", window.parent.document).val() +
                        "&totalRows=" + jQuery("#totalRows", window.parent.document).val());
                jQuery("#categoryServiceSearchForm", window.parent.document).submit();
            });
        });
        function display() {
            window.parent.document.getElementById("mask").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox_setServiceCategory").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox_setServiceCategory").src = "";
        }

    </script>
</head>
<body>
<div class="i_searchBrand i_searchBrand-washBeautyAccount">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">批量设定员工提成</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody">
        <table cellpadding="0" cellspacing="0" class="table2">
            <tr>
                <td>员工提成:</td>
                <td><input class="table_input" id="assistantPrice" maxlength="6" autocomplete="off"/>
                </td>
            </tr>
        </table>
        <div class="clear height"></div>
        <div class="btnInput">
            <input id="saveBtn" type="button" onfocus="this.blur();" value="确认">
            <input id="cancelBtn" type="button" onfocus="this.blur();" value="取消">
        </div>
    </div>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
</body>
</html>