<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>车辆管理</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/customerTooltip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/customer/vehicleManage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/exportExcel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_LIST");

        <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_DETAIL,WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY,WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_POSITION,WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_DRIVE_LOG">
        APP_BCGOGO.Permission.CustomerManager.CustomerModify =${WEB_CUSTOMER_MANAGER_CUSTOMER_MODIFY};
        APP_BCGOGO.Permission.CustomerManager.VehicleDetail =${WEB_CUSTOMER_MANAGER_VEHICLE_MANAGE_VEHICLE_DETAIL};
        APP_BCGOGO.Permission.CustomerManager.VehiclePosition =${WEB_CUSTOMER_MANAGER_VEHICLE_MANAGE_VEHICLE_POSITION};
        APP_BCGOGO.Permission.CustomerManager.VehicleDriveLog =${WEB_CUSTOMER_MANAGER_VEHICLE_MANAGE_VEHICLE_DRIVE_LOG};
        </bcgogo:permissionParam>
        APP_BCGOGO.Permission.Version.FourSShopVersion =${empty fourSShopVersions?false:fourSShopVersions};
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/sms/enterPhone.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <jsp:include page="customerNavi.jsp">
            <jsp:param name="currPage" value="vehicleManage"/>
        </jsp:include>
    </div>
    <div class="titBody">
        <div class="lineTitle">车辆查询</div>
        <div class="lineTop"></div>
        <div class="lineBody lineAll">
            <div class="i_height"></div>
            <form:form commandName="vehicleSearchConditionDTO" id="searchVehicleListForm" action="customer.do?method=getVehicleList" method="post" name="thisform">
                <input type="hidden" name="maxRows" id="maxRows" value="15">
                <input type="hidden" name="searchStrategies" id="searchStrategies" value="">
                <div class="divTit">
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td height="30"><span class="spanName">车辆信息：</span></td>
                            <td>
                                <form:input autocomplete="off" maxlength="10" cssClass="txt J_initialCss J_clear_input" initialValue="车牌号" path="licenceNo" cssStyle="width:140px;text-transform:uppercase;"/>
                                <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input" path="engineNo" searchField="engine_no" initialValue="发动机号" cssStyle="width:170px;"/>
                                <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input" path="chassisNumber" searchField="chassis_number" initialValue="车架号" cssStyle="width:170px;"/>
                                <form:input autocomplete="off" maxlength="20" cssClass="txt J_vehicleBrandSuggestion J_initialCss J_clear_input" path="vehicleBrand" searchField="brand" initialValue="车辆品牌"/>
                                <form:input autocomplete="off" maxlength="20" cssClass="txt J_vehicleBrandSuggestion J_initialCss J_clear_input" path="vehicleModel" searchField="model" initialValue="车型"/>
                                <form:input autocomplete="off" maxlength="20" cssClass="txt J_initialCss J_clear_input" path="vehicleColor" initialValue="车身颜色" cssStyle="width:70px;"/>
                            </td>
                        </tr>
                        <tr>

                          <td><span class="spanName">客户：</span></td>
                          <td>
                              <form:input autocomplete="off" maxlength="50" cssClass="txt J_initialCss J_clear_input" path="customerInfo" initialValue="客户/联系人/手机/会员号" cssStyle="width: 140px"/>&nbsp;距下次保养：
                              <form:input autocomplete="off" maxlength="15" cssStyle="width:50px;" cssClass="txt J_clear_input" path="maintainIntervalsMileage"/>&nbsp;km以内&nbsp;距下次保养：
                              <form:input autocomplete="off" maxlength="5" cssClass="txt J_clear_input" path="maintainIntervalsDay" cssStyle="width: 52px"/><label>&nbsp;天以内</label>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="divTit">
                    <span class="spanName" style="width: 85px">最后消费日期：&nbsp; </span>
                    <a class="btnList" id="my_date_yesterday" pagetype="vehicleData" name="my_date_select">昨天</a>
                    <a class="btnList" id="my_date_today" pagetype="vehicleData" name="my_date_select">今天</a>
                    <a class="btnList" id="my_date_oneWeekBefore" pagetype="vehicleData" name="my_date_select">最近一周</a>
                    <a class="btnList" id="my_date_oneMonthBefore" pagetype="vehicleData" name="my_date_select">最近一月</a>
                    <a class="btnList" id="my_date_oneYearBefore" pagetype="vehicleData" name="my_date_select">最近一年</a>
                    <form:input autocomplete="off" cssClass="txt J_clear_input my_startdate" path="vehicleLastConsumeTimeStartStr" readonly="readonly"/>
                    &nbsp;至&nbsp;
                    <form:input autocomplete="off" cssClass="txt J_clear_input my_enddate" path="vehicleLastConsumeTimeEndStr" readonly="readonly"/>
                </div>
                <div class="divTit button_conditon button_search">
                    <a class="blue_color clean" id="clearConditionBtn">清空条件</a>
                    <a class="button" id="searchVehicleBtn">查 询</a>
                </div>
            </form:form>
        </div>
        <div class="lineBottom"></div>
        <div class="clear i_height" style="height:5px"></div>
    </div>
    <div class="supplier group_list2 listStyle">
        <form action="sms.do?method=smswrite" id="sendMsgForm" method="post" target="_blank">
            <input type="hidden" id="contactIds" name="contactIds" value="">
        </form>
        <div class="left">
            <p><strong>车辆统计：</strong>
                <span>共有<b class="blue_color" id="totalNumber">0</b>&nbsp;辆</span>&nbsp;&nbsp;
                <span class="lineConnect">手机车主：<b class="blue_color" id="totalHasMobileNumber">0</b>&nbsp;名<a data-type="mobile" class="phone J_sendMsg"></a></span>&nbsp;&nbsp;
                <%--<span class="lineConnect">OBD车主：<b class="blue_color" id="totalHasOBDNumber">0</b>&nbsp;名<a data-type="obd" class="phone J_sendMsg"></a></span>&nbsp;&nbsp;--%>
                <span>累计消费：<b class="yellow_color" id="totalConsumeAmount">0</b>&nbsp;元</span>

            </p>
        </div>
    </div>
    <div class="clear i_height" style="height:5px"></div>
    <div class="cuSearch">
        <div class="cartTop"></div>
        <div class="cartBody">
            <div class="line_develop list_develop" style="width:980px;border-right:1px solid #C5C5C5">
                <span class="sorting-total">排序方式：</span>
                <a class="J_span_sort_vehicleManageList" sortFiled="vehicleLastConsumeTime" currentSortStatus="Desc" ascContact="点击后按最后消费日期升序排列！"  descContact="点击后按最后消费日期降序排列！">最后消费日期<span class="arrowDown J_sort_span_image"></span>
                    <div class="J_sort_div_info alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                        <span class="arrowTop" style="margin-left:20px;"></span>
                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody J_sort_div_info_val">
                                点击后按最后消费日期升序排列！
                            </div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                </a>
                <a class="J_span_sort_vehicleManageList" sortFiled="vehicleCreatedTime" currentSortStatus="Desc" ascContact="点击后按录入时间升序排列！"  descContact="点击后按录入时间降序排列！">录入时间<span class="arrowDown J_sort_span_image"></span>
                    <div class="J_sort_div_info alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                        <span class="arrowTop" style="margin-left:20px;"></span>
                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody J_sort_div_info_val">
                                点击后按录入时间升序排列！
                            </div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                </a>
                <a class="J_span_sort_vehicleManageList" sortFiled="vehicleMaintainTime" currentSortStatus="Desc" ascContact="点击后按下次保养日期升序排列！"  descContact="点击后按下次保养日期降序排列！">下次保养日期<span class="arrowDown J_sort_span_image"></span>
                    <div class="J_sort_div_info alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                        <span class="arrowTop" style="margin-left:20px;"></span>
                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody J_sort_div_info_val">
                                点击后按下次保养日期升序排列！
                            </div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                </a>
                <a class="J_span_sort_vehicleManageList" sortFiled="vehicleInsureTime" currentSortStatus="Desc" ascContact="点击后按下次保险日期升序排列！"  descContact="点击后按下次保险日期降序排列！">下次保险日期<span class="arrowDown J_sort_span_image"></span>
                    <div class="J_sort_div_info alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                        <span class="arrowTop" style="margin-left:20px;"></span>
                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody J_sort_div_info_val">
                                点击后按下次保险日期升序排列！
                            </div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                </a>
            </div>
            <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="vehicleListTable">
                <col width="90">
                <col width="120">
                <col width="140">
                <col width="120">
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="90">
                <col width="110">
                <col width="90">
                <col width="80">
                <tr class="titleBg">
                    <td style="padding-left:10px;">车牌号</td>
                    <td>车主信息</td>
                    <td>所属客户</td>
                    <td>车辆信息</td>
                    <td>累计消费</td>
                    <td>最后消费日期</td>
                    <td>当前里程</td>
                    <td>下次保养</td>
                    <td>距下次保养</td>
                    <td>下次保险</td>
                    <td>操作</td>
                </tr>
                <tr class="space">
                    <td colspan="11"></td>
                </tr>
            </table>
            <div class="height"></div>
            <!--分页控制部分，use common code snip-->
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="customer.do?method=getVehicleList"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,maxRows:15}"></jsp:param>
                <jsp:param name="jsHandleJson" value="drawVehicleListTable"></jsp:param>
                <jsp:param name="dynamical" value="vehicleManageList"></jsp:param>
                <jsp:param name="display" value="none"></jsp:param>
            </jsp:include>
            <div class="i_height clear"></div>
            <a id="exportVehicle" style="margin-left:10px" class="btnMerger">导出</a>
            <img id="exportExportVehicleCover" style=" width: 21px;margin-bottom: -6px; margin-left: 10px;display: none;" title="正在导出" alt="正在导出" src="images/loadinglit.gif">

            <div class="clear"></div>
        </div>
        <div class="cartBottom"></div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

<div class="prompt_box" style="width:430px;display: none" id="sendMsgPrompt">
    <div class="prompt_title">
        <div class="turn_off J_closeSendMsgPrompt"></div>
        发送保养信息</div>
    <div class="content">
        <form action="customer.do?method=sendVehicleMsg" id="sendMsgPromptForm" method="post">
            <input type="hidden" name="type" value="7" autocomplete="off">
            <input type="hidden" id="licenceNo" name="licenceNo" value="" autocomplete="off">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td>发送至：</td>
                    <td>
                        <input class="prompt_input txt" id="mobile" name="mobile" maxlength="11" autocomplete="off" >
                    </td>
                    <td style="text-align:left; line-height:15px;width: 100px">
                        <div style="float:left;display: none" id="mobileWrongInfo"><a class="prompt_right"></a><span class="red_color">格式不正确！</span></div>
                        <div style="float:left;display: none" id="mobileEmptyInfo"><span style="color:#aaa">请输入手机号！</span></div>
                        <div style="float:left;display: none" id="mobileRightInfo"><a class="prompt_right"></a></div>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" height="5" style="line-height:normal">&nbsp;</td>
                </tr>
                <tr>
                    <td valign="top">发送内容：</td>
                    <td>
                        <div class="prompt_textarea" style="width:220px;" id="vehicleMsgContent"></div>
                    </td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>
                        <div class="fl">
                            <label><input name="smsFlag" type="checkbox" value="true" disabled="disabled" checked="checked" autocomplete="off" />发送短信</label>
                        </div>
                    </td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td colspan="2">
                        <div class="fl">
                            <label><input name="appFlag" type="checkbox" value="true" autocomplete="off" />发送手机客户端信息</label>
                        </div>
                        <span class="gray_color">(仅对已装手机客户端的手机号有效)</span>
                    </td>
                </tr>
            </table>
            <div class="clear"></div>
            <div class="wid275">
                <div class="addressList"> <a id="sendMsgPromptBtn">发 送</a> <a class="J_closeSendMsgPrompt">取 消</a></div>
            </div>
        </form>
        <div class="clear"></div>
    </div>
</div>

<div id="obd_bind_div" class="prompt_box" style="width:320px;display: none">
        <div class="content">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <input type="hidden" id="bind_vehicle_id"/>
                    <td align="right">绑定OBD信息：</td>
                    <td><input id="imei_input" type="text" class="txt" placeholder="IMEI号" style="width: 120px"/></td>
                    <td><input id="sim_no_input" type="text" class="txt" placeholder="SIM卡号"/></td>
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
</body>
</html>