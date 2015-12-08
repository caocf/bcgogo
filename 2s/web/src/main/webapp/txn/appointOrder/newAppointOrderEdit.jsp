<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>预约管理</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/appointOrderEdit<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/newAppointOrderEdit<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/search/customerOrVehicleBaseSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"VEHICLE_CONSTRUCTION_APPOINT_ORDER_LIST");
        <c:choose>
        <c:when test="${not empty appointOrderDTO.id}">
            defaultStorage.setItem(storageKey.MenuCurrentItem,"编辑");
        </c:when>
        <c:otherwise>
            defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
        </c:otherwise>
        </c:choose>
        APP_BCGOGO.Permission.Version.FourSShopVersion =${empty fourSShopVersions?false:fourSShopVersions};
    </script>


</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">

    <div class="mainTitles">
        <div class="titleWords">预约服务</div>
    </div>
    <input type="hidden" id="orderType" value="APPOINT_ORDER">
    <div class="booking-management">
    <jsp:include page="../unit.jsp"/>
        <form:form commandName="appointOrderDTO" id="appointOrderDTOForm" method="post" name="thisform" class="J_leave_page_prompt">

            <form:hidden autocomplete="off" path="customerId" />
            <form:hidden autocomplete="off" path="vehicleId"/>
            <form:hidden autocomplete="off" path="id"/>
            <form:hidden autocomplete="off" path="vehicleNo"/>
            <form:hidden autocomplete="off" path="vehicleBrand"/>
            <form:hidden autocomplete="off" path="vehicleModel"/>
            <form:hidden autocomplete="off" path="vehicleContact"/>
            <form:hidden autocomplete="off" path="vehicleMobile"/>
            <form:hidden autocomplete="off" path="customer"/>
            <form:hidden autocomplete="off" path="customerMobile"/>
            <form:hidden autocomplete="off" path="customerLandLine"/>
            <form:hidden autocomplete="off" path="appointWay"/>
            <form:hidden autocomplete="off" path="total"/>
            <c:forEach items="${appointOrderDTO.faultInfoToShopDTOs}" var="faultInfoToShopDTO" varStatus="status">
                <form:hidden autocomplete="off" path="faultInfoToShopDTOs[${status.index}].id" value="${faultInfoToShopDTO.id}"/>
            </c:forEach>
            <div>
                <span style="margin-top:8px; color:#000; float:left">客户查询：</span>
                <input type="text" class="txt_border2" autocomplete="off" id="customerInfoText"  pagetype="appointOrder" placeHolder="客户名/车牌号/手机号"/>
                <%--<div class="txt_border2">--%>
                    <%--<input type="text" autocomplete="off" id="customerInfoText"  pagetype="appointOrder" placeHolder="客户名/车牌号/手机号"/>--%>
                    <%--<div class="i_search"></div>--%>
                <%--</div>--%>
                <div class="add_appointment" id="addNewCustomer"><img src="images/add_r2_c4.jpg" style="margin:5px; float:left"/><span style="float:left">新增客户</span></div>
                <%--<div class="editButton" id="addNewCustomer">新增客户</div>--%>
                <div class="clear i_height"></div>
            </div>
            <div class="titBody">
                <div class="lineTitle">
                    <span style="float:left;">基本信息</span>
                    <div class="modify_btn" id="modifyCustomerBtn">修改客户资料</div>
                </div>
                <div class="clear"></div>
                <div class="lineBody bodys" style="font-size:14px">
                    <table width="100%" border="0" class="order-table">
                        <colgroup>
                            <col width="80"/>
                            <col width=""/>
                            <col width="108"/>
                            <col width=""/>
                            <col width="73"/>
                            <col width=""/>
                            <col width="73"/>
                            <col width="120"/>
                        </colgroup>
                        <tr>
                            <th class="test1">客户名：</th>
                            <td><span id="sp_customerName">${appointOrderDTO.customer}</span>
                            </td>
                            <th class="test1">客户手机：</th>
                            <td><span id="sp_customerMobile">${appointOrderDTO.customerMobile}</span></td>
                            <th class="test1">车牌号：</th>
                            <td>
                                <c:choose>
                                    <c:when test="${fn:length(vehicleDTOs) > 1}">
                                        <c:set var="vehicleSpanStyle" value="display: none"/>
                                        <c:set var="vehicleSelectStyle" value="display: block"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="vehicleSpanStyle" value="display: block"/>
                                        <c:set var="vehicleSelectStyle" value="display: none"/>
                                    </c:otherwise>
                                </c:choose>

                                <span id="sp_vehicleNo" style="${vehicleSpanStyle}">${appointOrderDTO.vehicleNo}</span>
                                <select autocomplete="off"  id="select_vehicleNo" style="${vehicleSelectStyle};width: 107px" class="txt">
                                    <option value="">请选择预约车辆</option>
                                    <c:forEach items="${vehicleDTOs}" var="vehicleDTO" >
                                        <option <c:if test="${appointOrderDTO.vehicleNo == vehicleDTO.licenceNo}">selected="selected"</c:if>
                                                value="${vehicleDTO.id}" model="${vehicleDTO.model}" brand="${vehicleDTO.brand}"
                                                contact="${vehicleDTO.contact}" mobile="${vehicleDTO.mobile}">${vehicleDTO.licenceNo}</option>
                                    </c:forEach>
                                </select>
                            </td>
                            <th class="test1">品牌车型：</th>
                            <td>
                                <div style="width: 100px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                                    <span id="vehicleBrandModel" title="${appointOrderDTO.vehicleBrand} ${appointOrderDTO.vehicleModel}">${appointOrderDTO.vehicleBrand}&nbsp;${appointOrderDTO.vehicleModel}</span>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <th class="test1" valign="top"><span class="red_color">*</span>服务类型：</th>
                            <td valign="top">
                                <form:select autocomplete="off"  path="serviceItemDTOs[0].serviceId" cssClass="txt" cssStyle="margin-top: 5px">
                                    <form:option value="">请选择</form:option>
                                       <form:options items="${serviceScope}"/>
                                   </form:select>
                            </td>
                            <th class="test1" valign="top"><span class="red_color">*</span>预计服务时间：</th>
                            <td><form:input autocomplete="off"  path="appointTimeStr" cssClass="txt" ordertype="APPOINT_ORDER" cssStyle="width: 100px"/></td>
                            <th class="test1">当前里程：</th>
                            <fmt:formatNumber value='${appointOrderDTO.currentMileage}' pattern='#' var="fmtCurrentMileage"/>
                            <td><form:input autocomplete="off"  path="currentMileage" value="${fmtCurrentMileage}"  cssClass="txt" style="width:74px;" maxlength="6"/>公里</td>
                            <th class="test1">车主手机：</th>
                            <td><span id="sp_vehicleMobile">${appointOrderDTO.vehicleMobile}</span></td>
                        </tr>
                        <tr>
                            <th class="test1" valign="top">备 注：</th>
                            <td colspan="7" valign="top">
                                <form:textarea autocomplete="off"  path="remark" cssClass="textarea_90" maxlength="500"/>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="lineBottom"></div>
                <div class="clear height"></div>
            </div>
            <div class="titBody">
                <div class="lineTitle">
                    单据内容
                </div>
                <div class="lineBody bodys">
                    <div class="cuSearch">
                        <div class="gray-radius" style="margin:0;">
                            <h3 class="titleName">服务项目</h3>
                            <table id="serviceDetail_tb" class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:950px;">
                                <colgroup>
                                    <col width="130"/>
                                    <col width="150"/>
                                    <col width="150"/>
                                    <col width="150"/>
                                    <col width="100"/>
                                </colgroup>
                                <tr class="titleBg">
                                    <td style="padding-left:10px;">项目</td>
                                    <td>标准工时(时)</td>
                                    <td>工时单价(元)</td>
                                    <td>金额(元)</td>
                                    <td>操作</td>
                                </tr>
                                <c:forEach items="${appointOrderDTO.serviceDTOs}" var="serviceDTO" varStatus="status">
                                    <tr class="bg titBody_Bg">
                                        <td style="padding-left:10px;">
                                            <form:hidden autocomplete="off"  path="serviceDTOs[${status.index}].id"
                                                         value="${serviceDTO.id}"/>
                                            <%--<form:hidden path="serviceDTOs[${status.index}].serviceHistoryId"--%>
                                                         <%--value="${serviceDTO.serviceHistoryId}"/>--%>
                                            <form:hidden autocomplete="off"  path="serviceDTOs[${status.index}].serviceId"
                                                         value="${serviceDTO.serviceId}"/>
                                            <form:input autocomplete="off"
                                                        path="serviceDTOs[${status.index}].service"
                                                        value="${serviceDTO.service}"
                                                        class="txt checkStringEmpty J-hide-empty-droplist service"
                                                        size="50"/>
                                        </td>
                                        <td>
                                            <form:input autocomplete="off"
                                                        path="serviceDTOs[${status.index}].standardHours"
                                                        value="${serviceDTO.standardHours}"
                                                        cssClass="txt standardHours checkStringEmpty"/>
                                        </td>
                                        <td>
                                            <form:input autocomplete="off"
                                                        path="serviceDTOs[${status.index}].standardUnitPrice"
                                                        value="${serviceDTO.standardUnitPrice}"
                                                        cssClass="txt standardUnitPrice checkStringEmpty"/>
                                        </td>
                                        <td>
                                            <form:input autocomplete="off"
                                                        path="serviceDTOs[${status.index}].total"
                                                        value="${serviceDTO.total}"
                                                        cssClass="serviceTotal txt checkNumberEmpty "/>
                                        </td>
                                        <td>
                                            <a id="serviceDTOs${status.index}.deleteService"
                                               name="serviceDTOs[${status.index}].deleteService"
                                               class="blue_color">删除</a>
                                            <a id="serviceDTOs${status.index}.addService"
                                               name="serviceDTOs[${status.index}].addService"
                                               class="blue_color">新增</a>
                                        </td>
                                    </tr>
                                    <tr class="titBottom_Bg">
                                        <td colspan="5"></td>
                                    </tr>
                                </c:forEach>
                            </table>
                            <div class="clear total-of font12-normal"> 项目合计：<span class="yellow_color " id="serviceTotalSpan">${appointOrderDTO.serviceTotal}</span>元</div>
                            <h3 class="titleName">商品材料</h3>
                            <table id="material_tb" class="tab_cuSearch" cellpadding="0" cellspacing="0" style="width:950px;">
                                <colgroup>
                                    <col width="130"/>
                                    <col width="150"/>
                                    <col width="100"/>
                                    <col width="100"/>
                                    <col width="100"/>
                                    <col width="80"/>
                                    <col width="80"/>
                                    <col width="70"/>
                                    <col width="100"/>
                                    <col width="100"/>
                                </colgroup>
                                <tr class="titleBg">
                                    <td style="padding-left:10px;">商品编号</td>
                                    <td>品名</td>
                                    <td>品牌/产地</td>
                                    <td>规格</td>
                                    <td>型号</td>
                                    <td>单价(元)</td>
                                    <td>数量</td>
                                    <td>单位</td>
                                    <td>金额(元)</td>
                                    <td>操作</td>
                                </tr>
                                <c:forEach items="${appointOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">
                                    <tr class="bg titBody_Bg item">
                                        <td style="padding-left:10px;">
                                            <form:input autocomplete="off" class="txt checkStringEmpty"
                                                        path="itemDTOs[${status.index}].commodityCode"
                                                        value='${itemDTO.commodityCode}' maxlength="20"/>
                                            <form:hidden autocomplete="off"  path="itemDTOs[${status.index}].id"
                                                         value="${itemDTO.id}"/>
                                            <form:hidden autocomplete="off"  path="itemDTOs[${status.index}].productId"
                                                         value="${itemDTO.productId}"/>
                                            <%--<form:hidden path="itemDTOs[${status.index}].productHistoryId"--%>
                                                    <%--value="${itemDTO.productHistoryId}"/>--%>
                                            <form:hidden autocomplete="off"
                                                         path="itemDTOs[${status.index}].vehicleBrand"
                                                         value="${itemDTO.vehicleBrand}"/>
                                            <form:hidden autocomplete="off"
                                                         path="itemDTOs[${status.index}].vehicleModel"
                                                         value="${itemDTO.vehicleModel}"/>
                                        </td>
                                        <td>
                                            <form:input autocomplete="off" path="itemDTOs[${status.index}].productName"
                                                        value="${itemDTO.productName}" maxlength="100"
                                                        cssClass="txt checkStringEmpty productName "/>
                                        </td>
                                        <td>
                                            <form:input autocomplete="off" path="itemDTOs[${status.index}].brand"
                                                        value="${itemDTO.brand}"  maxlength="100"
                                                        cssClass="txt checkStringEmpty" />
                                        </td>
                                        <td>
                                            <form:input autocomplete="off" path="itemDTOs[${status.index}].spec"
                                                        value="${itemDTO.spec}" maxlength="100"
                                                        cssClass="txt checkStringEmpty" />
                                        </td>
                                        <td>
                                            <form:input autocomplete="off" path="itemDTOs[${status.index}].model"
                                                        value="${itemDTO.model}"  maxlength="100"
                                                        cssClass="txt checkStringEmpty"/>
                                        </td>
                                        <td><form:input autocomplete="off" path="itemDTOs[${status.index}].price"
                                                        value="${itemDTO.price}" maxlength="8"
                                                        cssClass="itemPrice txt checkNumberEmpty"/>
                                        </td>
                                        <td>
                                            <form:input autocomplete="off" path="itemDTOs[${status.index}].amount"
                                                        value="${itemDTO.amount}" maxlength="8"
                                                        cssClass="itemAmount txt checkNumberEmpty"/>
                                        </td>
                                        <td>
                                            <form:input autocomplete="off" path="itemDTOs[${status.index}].unit"
                                                        value="${itemDTO.unit}"
                                                        cssClass="itemUnit txt checkStringEmpty"
                                                        title="${itemDTO.unit}"/>
                                            <form:hidden autocomplete="off"  path="itemDTOs[${status.index}].storageUnit"
                                                         value="${itemDTO.storageUnit}"
                                                         class="itemStorageUnit table_input"/>
                                            <form:hidden autocomplete="off"  path="itemDTOs[${status.index}].sellUnit"
                                                         value="${itemDTO.sellUnit}"
                                                         class="itemSellUnit table_input"/>
                                            <form:hidden autocomplete="off" path="itemDTOs[${status.index}].rate" value="${itemDTO.rate}"
                                                         class="itemRate table_input"/>
                                        </td>
                                        <td>
                                            <span id="itemDTOs${status.index}.total_span"
                                                  class="itemTotalSpan">${itemDTO.total}</span>
                                            <form:hidden autocomplete="off" path="itemDTOs[${status.index}].total" value="${itemDTO.total}"
                                                         class="itemTotal"/>
                                        </td>
                                        <td>
                                            <a id="itemDTOs${status.index}.deleteMaterial" class="blue_color"
                                               name="itemDTOs[${status.index}].deleteMaterial">删除</a>
                                            <a id="itemDTOs${status.index}.addMaterial" class="blue_color"
                                               name="itemDTOs[${status.index}].addMaterial">新增</a>
                                        </td>
                                    </tr>
                                    <tr class="titBottom_Bg">
                                        <td colspan="10"></td>
                                    </tr>
                                </c:forEach>
                            </table>
                            <div class="clear total-of font12-normal"> 材料合计：<span class="yellow_color" id="salesTotalSpan">${appointOrderDTO.itemTotal}</span>元
                            </div>
                            <div class="clear total-of">单据总额：<span class="yellow_color" id="totalSpan">${appointOrderDTO.total}</span>元</div>
                            <div class="clear"></div>
                        </div>
                        <div class="clear i_height"></div>
                    </div>
                </div>
                <div class="lineBottom"></div>
                <div class="clear height"></div>
            </div>
            <div class="clear height"></div>
            <div class="padding10">

                <c:choose>
                    <c:when test="${empty appointOrderDTO.id}">
                        <input name="" type="button" class="query-btn" value="保存" id="saveAppointOrder"/>
                        <input name="" id="clearAppointOrder" type="button" class="query-btn" value="取消"/>
                    </c:when>
                    <c:otherwise>
                        <input name="" type="button" class="query-btn" value="保存" id="modifyAppointOrder"/>
                        <input name="" id="cancelModifyBtn" type="button" class="query-btn" value="取消"/>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="height"></div>
        </form:form>
    </div>
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="900px" height="450px" frameborder="0" src=""></iframe>
<!-- 增加下拉建议框 -->
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="addCustomerContainer" style="z-index: 4;position: relative;display: none;top:-500px;">
    <%@ include file="/common/addCustomerDialog.jsp"%>
</div>
<div id="div_brand" class="i_scroll" style="display:none;">
    <div class="Container">
        <div id="Scroller">
            <div class="Scroller-Container" id="Scroller-Container_id">
            </div>
        </div>
    </div>
</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
