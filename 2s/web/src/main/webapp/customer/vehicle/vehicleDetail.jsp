<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>车辆详情</title>

    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/register<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/vehicleValidator<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/components/ui/bcgogo-droplist.js"></script>
    <script type="text/javascript"
            src="js/page/customer/vehicle/customerVehicleBasicFunction<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript"
            src="js/page/customer/vehicle/vehicleDetail<%=ConfigController.getBuildVersion()%>.js"></script>


    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_LIST");

        APP_BCGOGO.Permission.Version.FourSShopVersion =${empty fourSShopVersions?false:fourSShopVersions};


        <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY,WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_POSITION,WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_DRIVE_LOG">
                APP_BCGOGO.Permission.CustomerManager.CustomerModify =${WEB_CUSTOMER_MANAGER_CUSTOMER_MODIFY};
        APP_BCGOGO.Permission.CustomerManager.VehiclePosition =${WEB_CUSTOMER_MANAGER_VEHICLE_MANAGE_VEHICLE_POSITION};
        APP_BCGOGO.Permission.CustomerManager.VehicleDriveLog =${WEB_CUSTOMER_MANAGER_VEHICLE_MANAGE_VEHICLE_DRIVE_LOG};
        </bcgogo:permissionParam>
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<input type="hidden" value="vehicleDetail" id="pageType">
<input type="hidden" value="${edit}" id="edit">
<input type="hidden" value="${today}" id="today">

<input type="hidden" value="${customerDTO.name}" id="name">
<input type="hidden" value="${customerVehicleResponse.maintainTimePeriodStr}" id="maintainTimePeriodStr">


<div class="i_main clear">
<div class="mainTitles">
    <div class="titleWords">车辆信息</div>
    <c:if test="${fromPage =='vehicleList'}">
        <div class="title-r" style="padding-top:48px;"><a href="customer.do?method=vehicleManageList">返回列表></a></div>
    </c:if>
</div>

<div class="booking-management">
<div class="titBody">
<div class="lineTitle"><span style="float:left">车辆基本信息</span>

    <div id="editCustomerVehicle" onclick="editVehicleBasicInfo();" class="editButton">编 辑</div>

</div>
<div class="clear"></div>
<div class="customer" style="width: 997px;" id="customerVehicleDiv">
    <table width="100%" border="0" class="order-table">
        <colgroup>
            <col width="250"/>
            <col width="250"/>
            <col width="200"/>
            <col width="200"/>
        </colgroup>
        <tr>
            <td>车牌号：<span class="J_customerVehicleSpan" data-key="licenceNo">${customerVehicleResponse.licenceNo}</span>
            </td>
            <td>联系人：<span class="J_customerVehicleSpan"
                          data-key="contact">${(customerVehicleResponse.contact ==null || customerVehicleResponse.contact =='')?'--':customerVehicleResponse.contact}</span>
            </td>
            <td>联系方式：<span class="J_customerVehicleSpan"
                           data-key="mobile">${(customerVehicleResponse.mobile ==null || customerVehicleResponse.mobile =='')?'--':customerVehicleResponse.mobile}</span>
            </td>
            <td><a
                    href="inquiryCenter.do?method=inquiryCenterIndex&pageType=vehicleManageList&startDateStr=&vehicleNumber=${customerVehicleResponse.licenceNo}"
                    target="_blank"
                    class="blue_color">累计消费：${customerVehicleResponse.consumeTimes}次 ${customerVehicleResponse.totalConsume}元</a>
            </td>
        </tr>
        <tr>
            <td>品牌/车型：<span class="J_customerVehicleSpan"
                            data-key="brand">${(customerVehicleResponse.brand ==null || customerVehicleResponse.brand =='')?'--':customerVehicleResponse.brand}</span>/<span
                    class="J_customerVehicleSpan"
                    data-key="model">${(customerVehicleResponse.model ==null || customerVehicleResponse.model =='')? '--':customerVehicleResponse.model}</span>
            </td>
            <td>年代/排量/颜色：<span class="J_customerVehicleSpan"
                               data-key="year">${(customerVehicleResponse.year ==null || customerVehicleResponse.year =='')?'--':(customerVehicleResponse.year)}${(customerVehicleResponse.year ==null || customerVehicleResponse.year =='')?'':'年'}</span>/<span
                    class="J_customerVehicleSpan"
                    data-key="engine">${(customerVehicleResponse.engine ==null || customerVehicleResponse.engine =='')?'--':customerVehicleResponse.engine}</span>/<span
                    class="J_customerVehicleSpan"
                    data-key="color">${(customerVehicleResponse.color ==null || customerVehicleResponse.color =='')?'--':customerVehicleResponse.color}</span>
            </td>
            <td>发动机号：<span class="J_customerVehicleSpan"
                           data-key="engineNo">${(customerVehicleResponse.engineNo ==null || customerVehicleResponse.engineNo =='')?'--':(customerVehicleResponse.engineNo)}</span>
            </td>
            <td>车架号：<span class="J_customerVehicleSpan"
                          data-key="vin">${(customerVehicleResponse.vin ==null || customerVehicleResponse.vin =='')?'--':(customerVehicleResponse.vin)}</span>
            </td>

        </tr>
        <tr>
            <td>购买日期：<span class="J_customerVehicleSpan"
                           data-key="carDateStr">${(customerVehicleResponse.carDateStr ==null || customerVehicleResponse.carDateStr =='')?'--':(customerVehicleResponse.carDateStr)}</span>
            </td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td colspan="4">
                <div class="line-dashed"></div>
            </td>
        </tr>

        <tr>
            <td>当前里程：<span class="J_customerVehicleSpan"
                           data-key="obdMileage">${(customerVehicleResponse.obdMileage ==null || customerVehicleResponse.obdMileage =='')?'--':(customerVehicleResponse.obdMileage)}${(customerVehicleResponse.obdMileage ==null || customerVehicleResponse.obdMileage =='')?'':'公里'}</span>
                <a id="registerMaintainBtn" class="blue_color">保养登记</a>
            </td>
            <td>上次保养：<span class="J_customerVehicleSpan"
                           data-key="lastMaintainMileage">${(customerVehicleResponse.lastMaintainMileage ==null || customerVehicleResponse.lastMaintainMileage =='')?'--':(customerVehicleResponse.lastMaintainMileage)}${(customerVehicleResponse.lastMaintainMileage ==null || customerVehicleResponse.lastMaintainMileage =='')?'':'公里'}</span>/<span
                    class="J_customerVehicleSpan" data-key="lastMaintainTimeStr">
                ${(customerVehicleResponse.lastMaintainTimeStr ==null || customerVehicleResponse.lastMaintainTimeStr =='')?'--':(customerVehicleResponse.lastMaintainTimeStr)}</span>
            </td>
            <td>保养周期：<span class="J_customerVehicleSpan"
                           data-key="maintainMileagePeriod">${(customerVehicleResponse.maintainMileagePeriod ==null || customerVehicleResponse.maintainMileagePeriod =='')?'--':(customerVehicleResponse.maintainMileagePeriod)}${(customerVehicleResponse.maintainMileagePeriod ==null || customerVehicleResponse.maintainMileagePeriod =='')?'':'公里'}</span>
            </td>
            <td>距下次保养：<span class="J_customerVehicleSpan"
                            data-key="nextMaintainMileageAccessStr">${(customerVehicleResponse.nextMaintainMileageAccessStr ==null || customerVehicleResponse.nextMaintainMileageAccessStr =='')?'--':(customerVehicleResponse.nextMaintainMileageAccessStr)}</span>
            </td>
        </tr>

        <tr>
            <td colspan="4">
                <div class="line-dashed"></div>
            </td>
        </tr>

        <c:if test="${fourSShopVersions}">
            <tr id="bind_opr_tr" style="<c:choose><c:when test="${empty customerVehicleResponse.gsmObdImei && empty customerVehicleResponse.gsmObdImeiMoblie}"> display:block;</c:when><c:otherwise>display:none;</c:otherwise></c:choose>">
            <td colspan="4">
                暂时未绑定OBD/后视镜，我要<a  class="blue_color bind_obd_btn">马上绑定</a>！

            </td>
            </tr>

            <tr id="bind_show_tr" style="<c:if test='${empty customerVehicleResponse.gsmObdImei && empty customerVehicleResponse.gsmObdImeiMoblie}'>display:none;</c:if>">
                <td>IMIE号：<span class="J_customerVehicleSpan imei_span"
                                data-key="gsmObdImei">${(customerVehicleResponse.gsmObdImei ==null || customerVehicleResponse.gsmObdImei =='')?'--':customerVehicleResponse.gsmObdImei}</span>
                </td>
                <td>SIM卡号：<span class="J_customerVehicleSpan sim_no_span"
                                data-key="gsmObdImeiMoblie">${(customerVehicleResponse.gsmObdImeiMoblie ==null || customerVehicleResponse.gsmObdImeiMoblie =='')?'--':customerVehicleResponse.gsmObdImeiMoblie}</span>
                </td>
                <td><a class="blue_color edit_obd_btn">修改OBD/后视镜</a></td>
                <td>&nbsp;</td>
            </tr>
        </c:if>


    </table>

</div>
<div id="customerVehicleEditDiv" class="customer" style="width: 997px;display: none;">
    <form action="vehicleManage.do?method=updateVehicleInfo" id="customerVehicleForm" method="post">

        <input type="hidden" id="vehicleId" name="vehicleId" value="${customerVehicleResponse.vehicleId}"/>
        <input type="hidden" id="customerId" name="customerId" value="${customerVehicleResponse.customerId}"/>
        <input type="hidden" id="obdId" name="obdId" value="${customerVehicleResponse.obdId}"/>


        <table width="100%" border="0" class="order-table">
            <colgroup>
                <col width="250"/>
                <col width="250"/>
                <col width="200"/>
                <col width="200"/>
            </colgroup>
            <tr>
                <td>车牌号：<input type="text" maxlength="10" id="licenceNo" name="licenceNo"
                               reset-value="${customerVehicleResponse.licenceNo}"
                               value="${customerVehicleResponse.licenceNo}"
                               class="txt_short J_formreset"/></td>
                <td>联系人：
                    <input type="text" maxlength="20" id="contact" name="contact"
                           reset-value="${customerVehicleResponse.contact}" value="${customerVehicleResponse.contact}"
                           class="txt_short J_formreset"/></td>
                <td>联系方式：
                    <input type="text" maxlength="11" id="mobile" name="mobile"
                           reset-value="${customerVehicleResponse.mobile}" value="${customerVehicleResponse.mobile}"
                           class="txt_short J_formreset J_customerVehicleMobile"/></td>
                <td><a href="#" class="blue_color">累计消费：${customerVehicleResponse.consumeTimes}次</a> <a href="#"
                                                                                                        class="blue_color">${customerVehicleResponse.totalConsume}元</a>
                </td>
            </tr>
            <tr>
                <td>品牌/车型：
                    <input type="text" maxlength="10" id="brand" name="brand"
                           reset-value="${customerVehicleResponse.brand}" value="${customerVehicleResponse.brand}"
                           class="txt_short J_formreset J_checkVehicleBrandModel"/>
                    <input type="text" maxlength="10" id="model" name="model"
                           reset-value="${customerVehicleResponse.model}" value="${customerVehicleResponse.model}"
                           class="txt_shorter J_formreset J_checkVehicleBrandModel"/>
                <td>年代/排量/颜色：
                    <input type="text" maxlength="4" style="width:40px;" id="year" name="year"
                           reset-value="${customerVehicleResponse.year}" value="${customerVehicleResponse.year}"
                           class="txt_shorter J_formreset"/>年
                    <input type="text" maxlength="6" id="engine" name="engine"
                           reset-value="${customerVehicleResponse.engine}" value="${customerVehicleResponse.engine}"
                           class="txt_shortest J_formreset"/>
                    <input type="text" maxlength="10" id="color" name="color"
                           reset-value="${customerVehicleResponse.color}" value="${customerVehicleResponse.color}"
                           class="txt_shortest J_formreset"/>
                </td>
                <td>发动机号：
                    <input type="text" maxlength="20" id="engineNo" name="engineNo"
                           reset-value="${customerVehicleResponse.engineNo}" value="${customerVehicleResponse.engineNo}"
                           class="txt_short J_formreset"/>
                <td>车架号：
                    <input type="text" maxlength="17" id="vin" name="vin"
                           reset-value="${customerVehicleResponse.vin}" value="${customerVehicleResponse.vin}"
                           class="txt_short J_formreset chassisNumber" style="text-transform:uppercase;"/>
            </tr>

            <tr>
                <td>购买日期：
                    <input type="text" onclick="showDatePicker(this);" maxlength="10" readonly="readonly"
                           id="carDateStr" name="carDateStr"
                           reset-value="${customerVehicleResponse.carDateStr}"
                           value="${customerVehicleResponse.carDateStr}"
                           class="J_customerVehicleBuyDate txt_short J_formreset"/>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td colspan="4">
                    <div class="line-dashed"></div>
                </td>
            </tr>

            <tr>
                <td>当前里程：
                    <input type="text" style="width:70px;" maxlength="10" id="obdMileage" name="obdMileage"
                           reset-value="${customerVehicleResponse.obdMileage}"
                           value="${customerVehicleResponse.obdMileage}"
                           class="txt_shorter J_formreset"/>
                    公里
                </td>
                <td>上次保养：<span class="J_customerVehicleSpan"
                               data-key="lastMaintainMileage">${(customerVehicleResponse.lastMaintainMileage ==null || customerVehicleResponse.lastMaintainMileage =='')?'--':(customerVehicleResponse.lastMaintainMileage)}${(customerVehicleResponse.lastMaintainMileage ==null || customerVehicleResponse.lastMaintainMileage =='')?'':'公里'}</span>/<span
                        class="J_customerVehicleSpan" data-key="lastMaintainTimeStr">
                    ${(customerVehicleResponse.lastMaintainTimeStr ==null || customerVehicleResponse.lastMaintainTimeStr =='')?'--':(customerVehicleResponse.lastMaintainTimeStr)}</span>
                </td>
                <td>保养周期：
                    <input type="text" maxlength="10" id="maintainMileagePeriod" name="maintainMileagePeriod"
                           reset-value="${customerVehicleResponse.maintainMileagePeriod}"
                           value="${customerVehicleResponse.maintainMileagePeriod}"
                           class="txt_short J_formreset"/>公里
                <td>距下次保养：<span class="J_customerVehicleSpan"
                                data-key="nextMaintainMileageAccessStr">${(customerVehicleResponse.nextMaintainMileageAccessStr ==null || customerVehicleResponse.nextMaintainMileageAccessStr =='')?'--':(customerVehicleResponse.nextMaintainMileageAccessStr)}</span>
                </td>
            </tr>

            <tr>
                <td colspan="4">
                    <div class="line-dashed"></div>
                </td>
            </tr>

            <c:if test="${fourSShopVersions}">

                <tr id="obdImeiTrEdit">
                    <td>IMIE号：
                        <input type="text" style="width:75px" maxlength="20" id="gsmObdImei${status.index}"
                               name="gsmObdImei" reset-value="${customerVehicleResponse.gsmObdImei}"
                               value="${customerVehicleResponse.gsmObdImei}" class="txt_short J_formreset"/>
                    </td>
                    <td>SIM卡号：
                        <input type="text" style="width:75px" id="gsmObdImeiMoblie${status.index}"
                               name="gsmObdImeiMoblie"
                               reset-value="${customerVehicleResponse.gsmObdImeiMoblie}"
                               value="${customerVehicleResponse.gsmObdImeiMoblie}" class="txt_short J_formreset"/>
                    </td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>

                <tr id="obdImeiTrShow" style="display: none;">
                    <td>IMIE号：<span class="J_customerVehicleSpan"
                                    data-key="gsmObdImei">${(customerVehicleResponse.gsmObdImei ==null || customerVehicleResponse.gsmObdImei =='')?'--':customerVehicleResponse.gsmObdImei}</span>
                    </td>
                    <td>SIM卡号：<span class="J_customerVehicleSpan"
                                    data-key="gsmObdImeiMoblie">${(customerVehicleResponse.gsmObdImeiMoblie ==null || customerVehicleResponse.gsmObdImeiMoblie =='')?'--':customerVehicleResponse.gsmObdImeiMoblie}</span>
                    </td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
            </c:if>

        </table>
        <div class="padding10">
            <input name="" type="button" class="query-btn J_saveCustomerVehicleBtn" value="保存"/>
            <input name="" type="button" class="query-btn J_cancelCustomerVehicleBtn" value="取消"/>
        </div>
    </form>
</div>

<div class="clear i_height"></div>
</div>
<div class="titBody">
    <div class="lineTitle"><span style="float:left">下次提醒服务</span>

        <div class="editButton" onclick="editVehicleAppointInfo();">编 辑</div>
    </div>
    <div class="clear"></div>
    <div class="customer" style="width: 997px;" id="customerVehicleAppointInfoShow">
        <table width="100%" border="0" class="order-table" id="appointServiceTableShow">
            <colgroup>
                <col width="200"/>
                <col width="200"/>
                <col width="200"/>
                <col width="200"/>
            </colgroup>
            <tr>
                <td>下次保养里程：
          <span class="J_customerVehicleAppointSpan"
                data-key="maintainMileage">${(customerVehicleResponse.maintainMileage ==null || customerVehicleResponse.maintainMileage =='')?'--':customerVehicleResponse.maintainMileage}</span>
                                              <span class="J_maintainMileageUnitSpan"
                                                    style="display: ${empty customerVehicleResponse.maintainMileage?'none':''}">公里</span>
                </td>
                <td>下次保养日期：<span class="J_customerVehicleAppointSpan"
                                 data-key="maintainTimeStr">${(customerVehicleResponse.maintainTimeStr ==null || customerVehicleResponse.maintainTimeStr =='')?'--':customerVehicleResponse.maintainTimeStr}</span>
                </td>
                <td>下次保险日期：<span class="J_customerVehicleAppointSpan"
                                 data-key="insureTimeStr">${(customerVehicleResponse.insureTimeStr ==null || customerVehicleResponse.insureTimeStr =='')?'--':customerVehicleResponse.insureTimeStr}</span>
                </td>
                <td>下次验车日期：<span class="J_customerVehicleAppointSpan"
                                 data-key="examineTimeStr">${(customerVehicleResponse.examineTimeStr ==null || customerVehicleResponse.examineTimeStr =='')?'--':customerVehicleResponse.examineTimeStr}</span>
                </td>
            </tr>


            <c:if test="${not empty customerVehicleResponse.appointServiceDTOs}">
                <c:forEach items="${customerVehicleResponse.appointServiceDTOs}" var="appointServiceDTO"
                           varStatus="appointStatus">
                    <c:if test="${appointStatus.index%4==0}">
                        <tr class="J_appointServiceTrShow">
                    </c:if>
                    <td>${appointServiceDTO.appointName}：${appointServiceDTO.appointDate}</td>
                    <c:if test="${appointStatus.index%3==3 || appointStatus.last}">
                        </tr>
                    </c:if>
                </c:forEach>
            </c:if>
        </table>
    </div>

    <form action="customer.do?method=ajaxAddOrUpdateVehicleAppoint" id="customerVehicleAppointForm" method="post">

        <input type="hidden" name="vehicleId" value="${customerVehicleResponse.vehicleId}"/>
        <input type="hidden" name="customerId" value="${customerVehicleResponse.customerId}"/>

        <div class="customer" style="width: 997px;display: none;" id="customerVehicleAppointInfoEdit">
            <table width="100%" border="0" class="order-table J_vehicleAppointTable" id="appointServiceTableEdit">
                <colgroup>
                    <col width="200"/>
                    <col width="200"/>
                    <col width="200"/>
                    <col width="200"/>
                </colgroup>
                <tr>
                    <td class="test2">下次保养里程：<input type="text" style="width:75px" maxlength="6" id="maintainMileage"
                                                    name="maintainMileage"
                                                    reset-value="${customerVehicleResponse.maintainMileage}"
                                                    value="${customerVehicleResponse.maintainMileage}"
                                                    class="txt_shorter J_formreset"/>公里
                    </td>

                    <td class="test2">下次保养日期：<input type="text" style="width:75px" onclick="showDatePicker(this);"
                                                    readonly="readonly"
                                                    id="by" name="by"
                                                    reset-value="${customerVehicleResponse.maintainTimeStr}"
                                                    value="${customerVehicleResponse.maintainTimeStr}"
                                                    class="txt_short J_formreset"/>
                    </td>

                    <td class="test2">下次保险日期：<input type="text" style="width:75px" onclick="showDatePicker(this);"
                                                    readonly="readonly"
                                                    id="bx" name="bx"
                                                    reset-value="${customerVehicleResponse.insureTimeStr}"
                                                    value="${customerVehicleResponse.insureTimeStr}"
                                                    class="txt_short J_formreset"/>
                    </td>

                    <td class="test2">下次验车日期：<input type="text" style="width:75px" onclick="showDatePicker(this);"
                                                    readonly="readonly"
                                                    id="yc" name="yc"
                                                    reset-value="${customerVehicleResponse.examineTimeStr}"
                                                    value="${customerVehicleResponse.examineTimeStr}"
                                                    class="txt_short J_formreset"/>
                        <c:if test="${empty customerVehicleResponse.appointServiceDTOs}">
                            <a class="J_addAppointService" data-index="0"><img src="images/opera2.png"/></a>
                        </c:if>
                    </td>
                </tr>


                <c:if test="${not empty customerVehicleResponse.appointServiceDTOs}">
                    <c:forEach items="${customerVehicleResponse.appointServiceDTOs}" var="appointServiceDTO"
                               varStatus="appointStatus">
                        <c:if test="${appointStatus.index%4==0}">
                            <tr class="J_appointServiceTrEdit">
                        </c:if>
                        <td class="test2">
                            <input type="hidden" name="appointServiceDTOs[${appointStatus.index}].appointName"
                                   value="${appointServiceDTO.appointName}"/>${appointServiceDTO.appointName}：
                            <input type="hidden" name="appointServiceDTOs[${appointStatus.index}].id"
                                   value="${appointServiceDTO.id}">
                            <input type="hidden" name="appointServiceDTOs[${appointStatus.index}].operateType"
                                   value="${appointServiceDTO.operateType}">

                            <input type="text" style="width:75px" onclick="showDatePicker(this);" readonly="readonly"
                                   name="appointServiceDTOs[${appointStatus.index}].appointDate"
                                   reset-value="${appointServiceDTO.appointDate}"
                                   value="${appointServiceDTO.appointDate}"
                                   class="txt J_formreset"/>
                            <a data-index="${appointStatus.index}" class="J_deleteAppointService"><img
                                    src="images/opera1.png"/></a>

                            <c:if test="${appointStatus.last}">
                                <a class="J_addAppointService" data-index="${appointStatus.index}"><img
                                        src="images/opera2.png"/></a>
                            </c:if>
                        </td>

                        <c:if test="${appointStatus.index%4==3 || appointStatus.last}">
                            </tr>
                        </c:if>
                    </c:forEach>
                </c:if>
            </table>

            <div class="padding10">
                <input name="" type="button" id="saveCustomerAppointBtn" class="query-btn" value="保存"/>
                <input name="" type="button" id="cancelCustomerAppointBtn" class="query-btn" value="取消"/>
            </div>
            <div class="clear"></div>
        </div>
    </form>

</div>
<div class="clear i_height"></div>


<c:if test="${not empty customerVehicleResponse.gsmObdImei}">
    <div class="titBody">
        <div class="lineTitle">
            <span>实时行车信息</span>
        </div>
        <div class="clear"></div>
        <div class="customer" style="width: 997px;">
            <table width="100%" border="0" class="order-table">
                <colgroup>
                    <col width="200"/>
                    <col width="200"/>
                    <col width="200"/>
                    <col width="200"/>
                </colgroup>
                <tr>
                    <c:choose>
                        <c:when test="${gsmVehicleInfoDTO.state eq 2}">
                            <td>ACC状态：行驶中</td>
                        </c:when>
                        <c:otherwise>
                            <td>ACC状态：${gsmVehicleInfoDTO.state =='1'?'开(点火)':'关（熄火）'}</td>
                        </c:otherwise>
                    </c:choose>
                    <td>车速：${gsmVehicleInfoDTO.vss} km/h</td>

                    <td>
                        当前里程：${gsmVehicleInfoDTO.curMil}${empty gsmVehicleInfoDTO.curMil?"":'公里'}</td>
                    <td>平均油耗：${gsmVehicleInfoDTO.cacafe} L/100km</td>
                </tr>
                <tr>
                    <td title="${faultCodes}">当前故障代码：${empty faultCodesShort?"--":faultCodesShort}</td>
                    <td>
                        历史故障：${totalFaultNum} <a href="shopFaultInfo.do?method=showShopFaultInfoList&scene=ALL&vehicleNo=${customerVehicleResponse.licenceNo}" class="blue_color">查看</a>
                    </td>
                    <td>电瓶电压：${gsmVehicleInfoDTO.spwr} V</td>
                    <td>引擎水温：${gsmVehicleInfoDTO.ect} ℃</td>
                </tr>
                <tr>
                    <td>引擎转速：${gsmVehicleInfoDTO.rpm} r/min</td>
                    <td>最大转速：${gsmVehicleInfoDTO.maxr} r/min
                    </td>
                    <td>行程最大车速：${gsmVehicleInfoDTO.maxs} r/min</td>
                    <td>急加减速次数：${gsmVehicleInfoDTO.badh} 次</td>
                </tr>
                <tr>
                    <td>发动机运行时间：${gsmVehicleInfoDTO.drit} 分</td>
                    <td>MIL亮起后行驶距离：${gsmVehicleInfoDTO.milDist} km</td>
                    <td>燃油液位：${gsmVehicleInfoDTO.fuelLvl} L</td>
                    <td>控制模块电压：${gsmVehicleInfoDTO.vpwr} V</td>
                </tr>
                <tr>
                    <td>瞬时油耗：${gsmVehicleInfoDTO.ife} h/L</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
            </table>


        </div>
        <div class="clear height"></div>
    </div>
    <div class="clear i_height"></div>
</c:if>

<div class="car_message">
    <div class="delect_btn" onclick="deleteVehicle();">删 除</div>
    <div class="add_btn" onclick="addNewAppoint();">新增预约</div>
    <c:if test="${not empty customerVehicleResponse.obdId}">
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_POSITION">
            <div class="add_btn" onclick="redirectVehiclePosition();">智能定位</div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.VEHICLE_MANAGE.VEHICLE_DRIVE_LOG">
            <div class="add_btn" onclick="redirectVehicleDriveLog();">行车日志</div>
        </bcgogo:hasPermission>

    </c:if>
    <div class="clear"></div>
</div>
</div>
<div class="clear i_height"></div>
</div>

<%@include file="vehicleMaintainRegister.jsp" %>

<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

<c:if test="${fourSShopVersions}">
    <div id="obd_bind_div" class="prompt_box" style="width:320px;display: none">
        <div class="content">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <input type="hidden" id="bind_vehicle_id"/>
                    <td align="right">绑定OBD/后视镜信息：</td>
                    <td><input id="imei_input" type="text" class="txt imei_input" placeholder="IMEI号" style="width: 120px"/></td>
                    <td><input id="sim_no_input" type="text" class="txt sim_no_input" placeholder="SIM卡号"/></td>
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

    <div id="obd_edit_div" class="prompt_box" style="width:380px;display: none">

        <div class="content">
            <input type="hidden" id="edit_data_index"/>
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td height="24" align="right">IMIE 号：</td>
                    <td> <span class="imei_span"></span></td>
                </tr>
                <tr>
                    <td height="24" align="right">SIM卡号：</td>
                    <td> <span class="sim_no_span"></span></td>
                </tr>
                <tr>
                    <td height="24" align="right">类型：</td>
                    <td>
                        <input class="change_radio" name="s_radio" type="radio" id="radio" value="radio" checked="checked" />
                        <label for="radio"></label>OBD/后视镜故障，更换OBD/后视镜
                    </td>
                </tr>
                <tr>
                    <td height="24" align="right">&nbsp;</td>
                    <td>
                        <input class="delete_radio" type="radio" name="s_radio" id="radio2" value="radio" />
                        <label for="radio2"></label>登记错误，本车不需要安装OBD/后视镜
                    </td>
                </tr>
                <tr id="change_obd_tr">
                    <td height="24" align="right">更换OBD/后视镜信息：</td>
                    <td>
                        <input type="text" class="txt imei_input" style="width:120px; margin-right:5px;" />
                        <input type="text" class="txt sim_no_input" />
                    </td>
                </tr>

            </table>

            <div class="clear"></div>
            <div id="change_opr_div" class="wid275" style="margin-left:50px;display: none">
                <div class="addressList">
                    <div id="change_ok_opr_btn" class="search_btn">确 定</div>
                    <div class="cancel_opr_btn empty_btn">取 消</div>
                </div>
            </div>
            <div id="delete_opr_div" class="wid275" style="margin-left:50px;">
                <div class="addressList">
                    <div id="delete_ok_opr_btn" class="search_btn">清空OBD信息</div>
                    <div class="cancel_opr_btn empty_btn">取 消</div>
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </div>
</c:if>
</body>
</html>