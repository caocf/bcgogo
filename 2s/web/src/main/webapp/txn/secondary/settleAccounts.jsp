<%@ page import="com.bcgogo.common.WebUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>结算详细</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="styles/up1<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <link rel="stylesheet" type="text/css" href="styles/setleNew<%=ConfigController.getBuildVersion()%>.css"/>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/secondary/settleAccounts<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/json2/json2.js"></script>
</head>
<body>
<%@ include file="/common/messagePrompt.jsp" %>

<div class="i_searchBrand i_searchBrand-account i_history" style="width: 440px;">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter" style="width: 428px;">
        <div class="i_note" id="div_drag" style="width: 380px;">结算详细</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody" style="width: 400px;">
        <div>应收总计：<strong id="orderTotal" style="color: #C00000;">0</strong>元</div>
        <c:if test="${!debt}">
            <div>（工时费:<strong id="serviceTotal" style="color: #C00000;">0</strong>元，材料费：<strong id="salesTotal" style="color: #C00000;">0</strong>元,其他费用：<strong id="otherTotal" style="color: #C00000;">0</strong>元）</div>
        </c:if>
        <div class="clear"></div>
        <div class="common_settlement">
            <table>
                <col width="130px"/>
                <tr>
                    <td>
                        <div>
                            <label>实收金额：</label><input id="settledAmount" name="settledAmount" autocomplete="off"/>元
                        </div>
                        <div>
                            <label>挂账金额：</label><input id="accountDebtAmount" name="accountDebtAmount" autocomplete="off"/>元
                        </div>
                        <div>
                            <label>优惠金额：</label><input id="accountDiscount" name="accountDiscount" autocomplete="off"/>元
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div class="total">合&nbsp;&nbsp;&nbsp;&nbsp;计：<span id="totalLabel" class="num">0</span>元</div>
                        <div class="errorInfo" id="errorInfo"></div>
                    </td>
                </tr>
            </table>
        </div>

        <div class="height"></div>
        <div class="ti_btn clear">
            <input type="checkbox" value="true" id="print" name="print" style=" background:none; width:15px; height:15px;margin:9px 0px 0px 50px;"/>
            <label style="margin:9px 0px 0px 2px; display:block; float:left;">打印单据</label>
            <input type="button" class="sure_tui" id="confirmBtn" value="结算"/>
            <input type="button" id="cancelBtn" value="取消"/>
            <div class="clear"></div>
        </div>
    </div>

</div>
<div class="clear"></div>
</div>

</body>
</html>