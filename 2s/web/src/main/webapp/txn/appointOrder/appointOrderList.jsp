<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" pagetype='order'>
<head xmlns="http://www.w3.org/1999/xhtml">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title >预约服务</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        <bcgogo:permissionParam resourceType="render" permissions="WEB.TXN.APPOINT_ORDER_MANAGER,WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
        APP_BCGOGO.Permission.VehicleConstruction.AppointOrder.Manager = ${WEB_TXN_APPOINT_ORDER_MANAGER};
        APP_BCGOGO.Permission.VehicleConstruction.WashBeauty.Base = ${WEB_VEHICLE_CONSTRUCTION_WASH_BEAUTY_BASE};
        APP_BCGOGO.Permission.VehicleConstruction.Base = ${WEB_VEHICLE_CONSTRUCTION_CONSTRUCT_BASE};
        </bcgogo:permissionParam>
    </script>
    <script type="text/javascript" src="js/page/txn/appointOrderList<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/search/customerOrVehicleBaseSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"VEHICLE_CONSTRUCTION_APPOINT_ORDER_LIST");
    </script>
</head>
<body style="color: #000000;position: absolute;">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
<div class="mainTitles">
     <div class="titleWords">预约管理</div>
    <bcgogo:hasPermission resourceType="render" permissions="WEB.TXN.APPOINT_ORDER_MANAGER">
        <div class="add_appointment J_add_newOrder" style="margin-top:23px; margin-left:0px;">
            <img style="margin:5px; float:left" src="images/add_r2_c3.jpg">
            <span style="float:left">新增预约</span>
        </div>
    </bcgogo:hasPermission>
 </div>
    <input type="hidden" id="orderType" value="APPOINT_ORDER">
    <div class="titBody">
        <div class="lineTitle">
            <span>预约查询</span>
        </div>
        <div class="lineBody bodys">
            <form id="appointOrderListForm">
                <input autocomplete="off" type="hidden" id="maxRows" value="15">
                <table  border="0" cellpadding="0" cellspacing="0" class="order-management">
                    <tr>
                        <td>客户</td>
                        <td colspan="2">
                            <input autocomplete="off" type="text" id="customerSearchWord" name="customerSearchWord" class="txt txt-long"
                                   placeHolder="客户名/手机号" maxlength="20"/>
                            <input autocomplete="off" type="hidden" id="customerIds">
                        </td>
                        <td>车牌号</td>
                        <td colspan="2"><input autocomplete="off" type="text" class="txt " id="vehicleNo" name="vehicleNo" maxlength="10"/></td>
                        <td>预计时间</td>
                        <td><input autocomplete="off" type="text" class="txt" id="appointTimeStartStr" name="appointTimeStartStr"/>
                            &nbsp;到&nbsp;
                            <input autocomplete="off" type="text" class="txt" id="appointTimeEndStr" name="appointTimeEndStr"/></td>
                            <td>状态
                                <select autocomplete="off" class="txt txt_color" id="appointOrderStatus" name="appointOrderStatus">
                                    <option value="">所有</option>
                                    <option value="PENDING">待确认</option>
                                    <option value="ACCEPTED">已接受</option>
                                    <option value="TO_DO_REPAIR">待施工</option>
                                    <option value="HANDLED">已施工</option>
                                    <option value="REFUSED">已拒绝</option>
                                    <option value="CANCELED">已取消</option>
                                </select>
                            </td>
                    </tr>
                </table>
                <%--<table border="0" cellpadding="0" cellspacing="0" class="order-management">--%>
                    <%--<tr>--%>
                        <%--<td>客户</td>--%>
                        <%--<td colspan="2">--%>
                            <%--<input type="text" id="customerSearchWord" name="customerSearchWord" class="txt txt-long" placeHolder="客户名/手机号" maxlength="20"/>--%>
                            <%--<input type="hidden" id="customerIds">--%>
                        <%--</td>--%>
                        <%--<td>车牌号</td>--%>
                        <%--<td><input type="text" class="txt txt-long" id="vehicleNo" name="vehicleNo" maxlength="10"/></td>--%>
                        <%--<td>单据号</td>--%>
                        <%--<td><input type="text" id="receiptNo" name="receiptNo" class="txt txt-long" maxlength="20"/></td>--%>
                   <%--</tr>--%>
                    <%--<tr>--%>
                        <%--<td>预约方式</td>--%>
                        <%--<td><select class="txt txt_color" id="appointWay" name="appointWay">--%>
                            <%--<option value="">所有</option>--%>
                            <%--<option value="APP">在线预约</option>--%>
                            <%--<option value="SHOP">现场预约</option>--%>
                            <%--<option value="PHONE">电话预约</option>--%>
                        <%--</select></td>--%>
                        <%--<td>状态--%>
                            <%--<select class="txt txt_color" id="appointOrderStatus" name="appointOrderStatus">--%>
                                <%--<option value="">所有</option>--%>
                                <%--<option value="PENDING">待确认</option>--%>
                                <%--<option value="ACCEPTED">已接受</option>--%>
                                <%--<option value="TO_DO_REPAIR">待施工</option>--%>
                                <%--<option value="HANDLED">已施工</option>--%>
                                <%--<option value="REFUSED">已拒绝</option>--%>
                                <%--<option value="CANCELED">已取消</option>--%>
                            <%--</select>--%>
                        <%--</td>--%>
                        <%--<td>下单时间</td>--%>
                        <%--<td>--%>
                            <%--<input type="text" class="txt" id="createTimeStartStr" name="createTimeStartStr"/>--%>
                            <%--&nbsp;到&nbsp;--%>
                            <%--<input type="text" class="txt" id="createTimeEndStr" name="createTimeEndStr"/>--%>
                        <%--</td>--%>
                        <%--<td>预计时间</td>--%>
                        <%--<td>--%>
                            <%--<input type="text" class="txt" id="appointTimeStartStr" name="appointTimeStartStr"/>--%>
                            <%--&nbsp;到&nbsp;--%>
                            <%--<input type="text" class="txt" id="appointTimeEndStr" name="appointTimeEndStr"/>--%>
                        <%--</td>--%>
                    <%--</tr>--%>
                <%--</table>--%>
                <%--<table width="100%" border="0" style="line-height:20px;">--%>
                    <%--<tr>--%>
                        <%--<td width="5%" valign="top">服务类型</td>--%>
                        <%--<td style="word-break:break-all">--%>
                            <%--<span class="margin-right">--%>
                                <%--<label class="lbl">--%>
                                    <%--<input id="allServiceCategory" type="checkbox"/>--%>
                                    <%--所有--%>
                                <%--</label>--%>
                            <%--</span>--%>
                            <%--<c:forEach items="${serviceScope}" var="item">--%>
                                <%--<span class="margin-right">--%>
                                <%--<label class="lbl">--%>
                                    <%--<input name="serviceCategoryIds" type="checkbox" value="${item.key}"/>--%>
                                        <%--${item.value}--%>
                                <%--</label>--%>
                                <%--</span>--%>
                            <%--</c:forEach>--%>
                        <%--</td>--%>
                    <%--</tr>--%>
                <%--</table>--%>
                <div class="clear height"></div>
                <div class="divTit button_conditon button_search" style="padding-bottom: 0px">
                    <a class="blue_color clean" id="clearSearchCondition">清空条件</a>
                    <a class="button" id="searchAppointOrderBtn">查 询</a>
                </div>
            </form>
        </div>
          <div class="lineBottom"></div>
          <div class="clear height"></div>
      </div>
    <div class="cuSearch">
        <div class="cartTop"></div>
        <div class="cartBody">
            <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="appointOrderListTb">
            <colgroup>
                <col width="100">
                <col>
                <col width="80">
                <col width="100">
                <col width="100">
                <col width="116">
                <col width="116">
                <col width="80">
                <col width="80">
                <col width="100">
            </colgroup>
            <tr class="titleBg">
                <td style="padding-left:10px;">单据号</td>
                <td>客户名</td>
                <td>车牌号</td>
                <td>手机号</td>
                <td>服务类型</td>
                <td>下单时间</td>
                <td>预计服务时间</td>
                <td>预约方式</td>
                <td>状态</td>
                <td>操作</td>
            </tr>
            <tr class="space"><td colspan="10"></td></tr>
            </table>
          <div class="clear i_height"></div>
          <bcgogo:ajaxPaging url="appoint.do?method=searchAppointOrder" dynamical="appointOrderList"
                             data='{startPageNo:1,maxRows:15${!empty scene ? ",scene:\'" : ""}${scene}${!empty scene ? "\'" : ""}}'
                             postFn="drawAppointOrderList"/>
    	</div>
        <div class="cartBottom"></div>
    </div>

</div>

<%@ include file="appointOrderDialog.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>