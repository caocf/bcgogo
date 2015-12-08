<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no"/>

    <title>智能定位</title>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/todo_new<%=ConfigController.getBuildVersion()%>.css"/>
	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=nh73VgKTDOS1LnxhSPvpz9DM"></script>

    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>

    <%--<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=760f39e8b3f09ae5a4d3a0c7b97bc504"></script>--%>

    <script type="text/javascript"
            src="js/page/customer/vehicle/vehicleCurrentPosition<%=ConfigController.getBuildVersion()%>.js"></script>


    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_POSITION");
    </script>

</head>

<body class="bodyMain">

<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" id="vehicleIdStr" value="${vehicleIdStr}">
<input type="hidden" id="coordinateLat" value="${coordinateLat}">
<input type="hidden" id="coordinateLon" value="${coordinateLon}">
<input type="hidden" id="city" value="${city}">

<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">智能定位</div>
    </div>
    <div class="clear"></div>
    <div id="todo_content">
        <form:form commandName="vehicleSearchConditionDTO" id="searchVehicleListForm"
                   action="vehicleManage.do?method=queryVehiclePosition"
                   method="post" name="thisform">
            <div>
                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table_search">
                    <colgroup>
                        <col width="100"/>
                        <col/>
                    </colgroup>
                    <tr>
                        <th>车牌号：</th>
                        <td>
                                <form:input autocomplete="off" maxlength="10" cssClass="txt J_initialCss J_clear_input"
                                            searchField="licence_no" initialValue="车牌号" value="${vehicleDTO.licenceNo}"
                                            path="licenceNo" cssStyle="width:160px;text-transform:uppercase;"/>
                                <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input"
                                            searchField="engine_no" value="${vehicleDTO.engineNo}" path="engineNo"
                                            initialValue="发动机号" cssStyle="width:160px;"/>
                                <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input"
                                            searchField="chassis_number" value="${vehicleDTO.chassisNumber}"
                                            path="chassisNumber" initialValue="车架号" cssStyle="width:160px;"/>
                    </tr>
                    <tr>
                        <th>OBD/后视镜信息：</th>
                        <td>
                                <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input"
                                            searchField="gsm_obd_imei" value="${vehicleDTO.gsmObdImei}"
                                            path="gsmObdImei" initialValue="IMIE卡号" cssStyle="width:160px;"/>
                                <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input"
                                            searchField="gsm_obd_imei_mobile" value="${vehicleDTO.gsmObdImeiMoblie}"
                                            path="gsmObdImeiMoblie" initialValue="SIM卡号" cssStyle="width:160px;"/>
                    </tr>

                    <tr>

                        <th>客户：</th>
                        <td>
                            <form:input autocomplete="off" maxlength="50" cssClass="txt J_initialCss J_clear_input"
                                        value="${customerName}"
                                        path="customerInfo" initialValue="客户/联系人/手机/会员号" cssStyle="width: 160px"/>
                        </td>
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
        <%--<div id="noDateDiv" style="display:none;width: 100%;height: 20px;;overflow: hidden;margin:0"></div>--%>
        <dd>
            <%--<iframe src="" id="map_container_iframe" style="width: 100%;height: 500px;;overflow: hidden;margin:0" scrolling="no"--%>
            <%--frameborder="0" allowtransparency="true"></iframe>--%>
            <div id="allmap" style="width:100%;height:500px;overflow:hidden;margin:0;border:5px green solid"></div>
        </dd>
    </div>
</div>

<%@ include file="/txn/appointOrder/appointOrderDialog.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
