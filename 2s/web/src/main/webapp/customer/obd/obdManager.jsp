<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 14-6-25
  Time: 下午2:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>OBD管理</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/todo_new<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css"
          href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css"
          href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/obdManager<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_POSITION,WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_DRIVE_LOG">
        APP_BCGOGO.Permission.CustomerManager.VehiclePosition =${WEB_CUSTOMER_MANAGER_VEHICLE_MANAGE_VEHICLE_POSITION};
        APP_BCGOGO.Permission.CustomerManager.VehicleDriveLog =${WEB_CUSTOMER_MANAGER_VEHICLE_MANAGE_VEHICLE_DRIVE_LOG};
        </bcgogo:permissionParam>
        $(function () {
            $("#searchOBDBtn").click();
        });
    </script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=nh73VgKTDOS1LnxhSPvpz9DM"></script>
<body>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" id="orderType" value="OBD_MANAGER"/>

<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">OBD/后视镜 管理</div>
    </div>
    <div class="clear"></div>
    <div id="todo_content">
        <div>
            <form:form commandName="obdSimSearchCondition" id="searchOBDListForm"
                       action="OBDManager.do?method=getOBDList" method="post" name="thisform">
                <form:hidden path="limit" value="15"/>
                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table_search">
                    <colgroup>
                        <col width="100"/>
                        <col/>
                    </colgroup>
                    <tr>
                        <th>车辆：</th>
                        <td colspan="5">
                            <form:input autocomplete="off" maxlength="10" cssClass="txt J_initialCss J_clear_input"
                                        path="licenceNo" placeholder="车牌号"/>
                            <form:input autocomplete="off" maxlength="20"
                                        cssClass="txt J_vehicleBrandSuggestion J_initialCss J_clear_input"
                                        path="vehicleBrand" searchField="brand" placeholder="车辆品牌"/>
                            <form:input autocomplete="off" maxlength="20"
                                        cssClass="txt J_vehicleBrandSuggestion J_initialCss J_clear_input"
                                        path="vehicleModel" searchField="model" placeholder="车型"/>
                            <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input"
                                        path="chassisNumber" searchField="chassis_number" placeholder="车架号"
                                        style="width:170px;"/>
                            <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input"
                                        path="engineNo" searchField="engine_no" placeholder="发动机号"/>
                        </td>
                    </tr>
                    <tr>
                        <th>OBD/后视镜信息：</th>
                        <td>
                            <form:input autocomplete="off" maxlength="20" cssClass="txt" path="imei"
                                        placeholder="IMEI卡号"/>
                            <form:input autocomplete="off" maxlength="20" cssClass="txt" path="mobile"
                                        placeholder="SIM卡号"/></td>
                        <th>卡费状态：</th>
                        <td>
                            <label class="rad">
                                <form:checkbox path="userTypes" value="FREE"/>
                                免费</label>
                            <label class="rad">
                                <form:checkbox path="userTypes" value="NOT_FREE"/>
                                自费</label></td>
                        <th>销售状态：</th>
                        <td>
                            <select name="obdStatusList" autocomplete="off" style="width: 80px;">
                                <option value="">-所有-</option>
                                <option value="ON_SELL">待售</option>
                                <option value="SOLD">已售出</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th>日期：</th>
                        <td colspan="5">
                            <div class="divTit">
                                <a id="date_yesterday" class="btnList date_select">昨天</a>&nbsp;
                                <a id="date_today" class="btnList date_select">今天</a>&nbsp;
                                <a id="date_last_week" class="btnList date_select">最近一周</a>&nbsp;
                                <a id="date_last_month" class="btnList date_select">最近一月</a>&nbsp;
                                <a id="date_last_year" class="btnList date_select">最近一年</a>
                                <form:input path="startTimeStr" type="text" class="txt timeInput"/>
                                至
                                <form:input path="endTimeStr" type="text" class="txt timeInput"/>
                            </div>
                        </td>
                    </tr>
                </table>
                <div class="search_div">
                    <div id="searchOBDBtn" class="search_btn">查 询</div>
                    <div id="empty_opr_btn" class="empty_btn">清空条件</div>
                    <div class="clear"></div>
                </div>
                <div class="clear height"></div>
            </form:form>
        </div>
        <div class="score_list obd_m">
            <div class="all_score">
                <div id="obd_storage_btn" class="fr empty_btn">产品入库</div>
                <span class="font14">共有 <a class="shop_obd_total blue_color stat_btn">0</a> 台  其中待售 <a
                        class="shop_obd_on_sell stat_btn blue_color">0</a> 台  售出 <a
                        class="shop_obd_sold stat_btn blue_color">0</a> 台</span>
            </div>
            <table id="obdTable" width="100%" border="0" cellspacing="0" cellpadding="0" class="score_table">
                <tr>
                    <th>序号</th>
                    <th>类别</th>
                    <th>入库日期</th>
                    <th>IMEI号</th>
                    <th>SIM卡号</th>
                    <th>开通服务</th>
                    <th>服务截止</th>
                    <th>服务年限</th>
                    <th>服务状态</th>
                    <th>所属客户</th>
                    <th>车辆信息</th>
                    <th>安装日期</th>
                    <th>状态</th>
                    <th>操作</th>
                </tr>

            </table>
            <div class="page_box">
                <bcgogo:ajaxPaging
                        url="OBDManager.do?method=getOBDList"
                        data="{
                                startPageNo:1
                                 }"
                        postFn="initOBDList"
                        dynamical="_initOBDList"
                        display="none"
                        />
            </div>
        </div>
    </div>
</div>


<div id="obd_storage_div" class="prompt_box" style="width:600px;display: none">
    <div class="content">
        <form:form action="OBDManager.do?method=OBDStorage" id="obdStorageForm" commandName="borrowOrderDTO"
                   method="post">
            <table id="obdStorageTable" width="100%" border="0" cellspacing="0" cellpadding="0" class="operation_table">
                <tr>
                    <th>序号</th>
                    <th>IMEI号</th>
                    <th>SIM卡号</th>
                    <th>开通年月</th>
                    <th>免费截止</th>
                    <th>免费期</th>
                    <th>操作</th>
                </tr>
            </table>
        </form:form>
        <div class="clear"></div>
        <div class="wid275">
            <div class="addressList storage_operate" style="float:none; margin:0 auto; text-align:center">
                <a id="okBtn" style="float:none">确 定</a>
                <a id="cancelBtn" style="float:none">取 消</a>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</div>

<%--<div id="obd_bind_confirm" class="prompt_box" style="width:600px;display: none">--%>
<%--<span>--%>
<%--友情提示：本操作需要输入安装OBD的车辆信息！--%>
<%--请确认安装车辆是否已经录入系统？--%>
<%--</span>--%>
<%--<div class="wid275">--%>
<%--<div class="addressList storage_operate" style="float:none; margin:0 auto; text-align:center">--%>
<%--<a id="obd_bind_confirm_okBtn" style="float:none">是</a>--%>
<%--<a id="obd_bind_confirm_cancelBtn" style="float:none">否，我要录入新车</a>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>

<div id="obd_bind_confirm" class="prompt_box" style="width:320px;display: none">

    <div class="content">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>本操作需要输入安装OBD/后视镜的车辆信息！
                    请确认安装车辆是否已经录入系统？
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
        </table>
        <div class="clear"></div>
        <div class="wid275" style="margin-left:50px;">
            <div class="addressList">
                <div id="obd_bind_confirm_okBtn" class="search_btn">是</div>
                <div id="obd_bind_confirm_cancelBtn" class="empty_btn">否，我要录入新车</div>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</div>

<%--<div id="obd_bind_div" class="prompt_box" style="width:600px;display: none">--%>
<%--<div>--%>
<%--<input type="hidden" id="obd_bind_id"/>--%>
<%--OBD编号：<span id="obd_bind_imei"></span>--%>
<%--</div>--%>
<%--<div>车牌号:<input id="licenceNo_input" type="text" placeholder="请输入安装OBD的车牌号"/></div>--%>
<%--<div class="wid275">--%>
<%--<div class="addressList storage_operate" style="float:none; margin:0 auto; text-align:center">--%>
<%--<a id="vehicle_bind_okBtn" style="float:none">确 定</a>--%>
<%--<a id="vehicle_bind_cancelBtn" style="float:none">取 消</a>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>

<div id="obd_bind_div" class="prompt_box" style="width:320px;display: none">

    <div class="content">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <input type="hidden" id="obd_bind_id"/>
                <td align="right">OBD/后视镜编号：</td>
                <td><span id="obd_bind_imei"></span></td>
            </tr>
            <tr>
                <td align="right">车牌号：</td>
                <td><input id="licenceNo_input" type="text" class="txt" placeholder="请输入安装OBD/后视镜的车牌号"
                           style="width: 136px"/></td>
            </tr>

        </table>

        <div class="clear"></div>
        <div class="wid275" style="margin-left:50px;">
            <div class="addressList">
                <div id="vehicle_bind_okBtn" class="search_btn">确 定</div>
                <div id="vehicle_bind_cancelBtn" class="empty_btn">取 消</div>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</div>

<iframe id="iframe_moreUserInfo" style="position:absolute;z-index:5; left:200px; top:50px; display:none;"
        allowtransparency="true" width="1000px" height="900px" scrolling="no" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>