<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>行车日志</title>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/todo_new<%=ConfigController.getBuildVersion()%>.css"/>

    <style>
        .clicked {
        border: 1px solid #FF6600;
        color: #FF6600;
        background: #f5f5f5 url(images/icon_range.png) no-repeat right bottom;
    }
    </style>

    <%--<script src="http://api.map.baidu.com/api?v=1.2"></script>--%>
    <%--<script type="text/javascript"--%>
            <%--src="js/page/customer/vehicle/lushu-min<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="js/extension/json2/json2.min.js"></script>--%>

    <script type="text/javascript"
            src="js/page/customer/vehicle/vehicleDriveLog<%=ConfigController.getBuildVersion()%>.js"></script>


    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_DRIVE_LOG");
    </script>

</head>

<body class="bodyMain">

<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden"  id="vehicleIdStr" value="${vehicleIdStr}">
<input type="hidden"  id="coordinateLat" value="${coordinateLat}">
<input type="hidden"  id="coordinateLon" value="${coordinateLon}">
<input type="hidden"  id="city" value="${city}">






<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">行车日志</div>
    </div>
    <div class="clear"></div>
    <div id="todo_content">
        <form:form commandName="vehicleSearchConditionDTO" id="searchVehicleListForm"
                   action="vehicleManage.do?method=queryVehicleDriveLog"
                   method="post" name="thisform">
            <input type="hidden" name="maxRows" id="maxRows" value="7">
            <input type="hidden" name="startPageNo"  id="startPageNo" value="1">

            <input type="hidden" name="searchStrategies" id="searchStrategies" value="">

            <div>
                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table_search">
                    <colgroup>
                        <col width="100"/>
                        <col/>
                    </colgroup>
                    <tr>
                        <th>车牌号：</th>
                        <td><form:input autocomplete="off" maxlength="10" cssClass="txt J_initialCss J_clear_input" value="${vehicleDTO.licenceNo}" searchField="licence_no" initialValue="车牌号" path="licenceNo" cssStyle="width:140px;text-transform:uppercase;"/>
                            <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input" value="${vehicleDTO.engineNo}" searchField="engine_no" path="engineNo" initialValue="发动机号" cssStyle="width:140px;"/>
                            <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input" value="${vehicleDTO.chassisNumber}" searchField="chassis_number" path="chassisNumber" initialValue="车架号" cssStyle="width:170px;"/>
                    </tr>
                    <tr>
                        <th>OBD信息：</th>
                        <td><form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input" value="${vehicleDTO.gsmObdImei}" searchField="gsm_obd_imei" path="gsmObdImei" initialValue="IMIE卡号" cssStyle="width:140px;"/>
                            <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input" value="${vehicleDTO.gsmObdImeiMoblie}" searchField="gsm_obd_imei_mobile" path="gsmObdImeiMoblie" initialValue="SIM卡号" cssStyle="width:140px;"/>
                    </tr>
                    <tr>
                        <th>行驶时间：</th>
                        <td><a class="btnList" id="my_date_yesterday" pagetype="vehicleData" name="my_date_select">昨天</a>
                            <a class="btnList" id="my_date_today" pagetype="vehicleData" name="my_date_select">今天</a>
                            <a class="btnList" id="my_date_oneWeekBefore" pagetype="vehicleData" name="my_date_select">最近一周</a>
                            <a class="btnList" id="my_date_oneMonthBefore" pagetype="vehicleData" name="my_date_select">最近一月</a>
                            <a class="btnList" id="my_date_oneYearBefore" pagetype="vehicleData" name="my_date_select">最近一年</a>
                                <form:input autocomplete="off" cssClass="txt J_clear_input my_startdate" path="lastDriveTimeStartStr" readonly="readonly"/>
                            &nbsp;至&nbsp;
                                <form:input autocomplete="off" cssClass="txt J_clear_input my_enddate" path="lastDriveTimeEndStr" readonly="readonly"/>
                    </tr>
                </table>
                <div class="search_div">
                    <div id="searchVehicleBtn" class="search_btn">查 询</div>
                    <div class="empty_btn" id="clearConditionBtn">清空条件</div>
                    <div class="clear"></div>
                </div>
            </div>
        </form:form>
        <div class="clear i_height"></div>
        <div id="driving_log">
            <div class="driving_left"  id="drive_record" style="display: none;"></div>

            <div class="driving_left" id="noDateDiv">
                <div class="addrPage">
                    <img src="images/ku.png" /> <span>请输入条件进行查询</span>
                    可根据车辆信息或OBD信息进行查询！
                    <div class="clear"></div>
                </div>
            </div>

            <dd>
                <iframe src="" id="map_container_iframe" style="width: 700px;height: 520px;" scrolling="no"
                        frameborder="0" allowtransparency="true"></iframe>
            </dd>

            <div class="clear"></div>
        </div>
    </div>
</div>

<%@ include file="/txn/appointOrder/appointOrderDialog.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
