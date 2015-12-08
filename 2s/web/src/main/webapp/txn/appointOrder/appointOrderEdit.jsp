<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" pagetype='order'>
<head xmlns="http://www.w3.org/1999/xhtml">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title >预约服务</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/appointOrderEdit<%=ConfigController.getBuildVersion()%>.js"></script>
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

    </script>
</head>
<body style="color: #000000;position: absolute;">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
<div class="mainTitles">
     <div class="titleWords">预约服务</div>
 </div>
    <input type="hidden" id="orderType" value="APPOINT_ORDER">
    <input type="hidden" id="createFromFlag" value="${createFromFlag}">
    <div class="booking-management">
        <form:form commandName="appointOrderDTO" id="appointOrderDTOForm" method="post" name="thisform" class="J_leave_page_prompt">
            <form:hidden path="customerId"/>
            <form:hidden path="vehicleId"/>
            <form:hidden path="id"/>
            <div class="titBody">
                <div class="lineTitle"> 车辆信息</div>
                <div class="clear"></div>
                <div class="addOrder">
                    <table width="100%" border="0" class="order-table">
                        <tr>
                            <th><span class="red_color">*</span>车牌号</th>
                            <td width="22%"><form:input path="vehicleNo" cssClass="txt"/></td>
                            <th>车辆品牌</th>
                            <td width="22%"><form:input path="vehicleBrand" cssClass="txt"/></td>
                            <th>车型</th>
                            <td width="22%"><form:input path="vehicleModel" cssClass="txt"/></td>
                        </tr>
                        <tr>
                            <th>联系人</th>
                            <td><form:input path="vehicleContact" cssClass="txt"/></td>
                            <th>联系方式</th>
                            <td><form:input path="vehicleMobile" cssClass="txt"/></td>
                            <th>&nbsp;</th>
                            <td>&nbsp;</td>
                        </tr>
                    </table>
                </div>
            </div>
            <div class="clear i_height"></div>
            <div class="titBody">
                <div class="lineTitle">
                    <div class="title-r">
                        <c:if test="${empty appointOrderDTO.id}">
                            <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.BASE">
                                <a id="toMoreCustomerInfo" style="cursor: pointer;" href="#" class="blue_color ">更多客户信息></a>
                            </bcgogo:hasPermission>
                        </c:if>
                    </div>
                    客户信息
                </div>
                <div class="clear"></div>
                <div class="addOrder">
                    <table width="100%" border="0" class="order-table">
                        <tr>
                            <th width="15%">客户名</th>
                            <td width="22%"><form:input path="customer" cssClass="txt" maxlength="30"/></td>
                            <th>手机号</th>
                            <td width="22%"><form:input path="customerMobile" cssClass="txt" maxlength="11"/></td>
                            <th>座机</th>
                            <td width="22%"><form:input path="customerLandLine" cssClass="txt" maxlength="20"/></td>
                        </tr>
                        <tr>
                            <th>会员卡号</th>
                            <td><form:input path="memberNo" cssClass="txt" maxlength="20"/></td>
                            <th>会员类型</th>
                            <td style="line-height: 30px">&nbsp;&nbsp;<span id="memberType">${appointOrderDTO.memberType}</span></td>
                            <th>卡内余额</th>
                            <td style="line-height: 30px">&nbsp;&nbsp;<span id="memberBalance">${appointOrderDTO.memberBalance}</span></td>
                        </tr>
                    </table>
                </div>
            </div>
            <div class="clear i_height"></div>
            <div class="titBody">
                <div class="lineTitle"> 预约信息</div>
                <div class="clear"></div>
                <div class="addOrder">
                    <table width="100%" border="0" class="order-table">
                        <tr>
                            <th>服务类型</th>
                            <td width="22%">
                                <form:select path="serviceItemDTOs[0].serviceId" cssClass="txt">
                                    <form:options items="${serviceScope}"/>
                                </form:select>
                            </td>
                            <th><span class="red_color">*</span>预计时间</th>
                            <td width="22%"><form:input path="appointTimeStr" cssClass="txt" ordertype="APPOINT_ORDER"/></td>
                            <th>预约人</th>
                            <td width="22%"><form:input path="appointCustomer" cssClass="txt" maxlength="20"/></td>
                        </tr>
                        <tr>
                            <th>接待人</th>
                            <td>
                                <form:input path="assistantMan" cssClass="txt" maxlength="20"/>
                                <form:hidden path="assistantId"/>
                            </td>
                            <th>预约方式</th>
                            <td>
                                <c:choose>
                                    <c:when test='${appointOrderDTO.appointWay == "APP"}'>
                                        &nbsp;<span>${appointOrderDTO.appointWayStr}</span>
                                        <form:hidden path="appointWay"/>
                                    </c:when>
                                    <c:otherwise>
                                        <select id="appointWay" name="appointWay" class="txt">
                                            <option value="SHOP" <c:if test="${appointOrderDTO.appointWay == 'SHOP'}">selected='selected'</c:if>>现场预约</option>
                                            <option value="PHONE" <c:if test="${appointOrderDTO.appointWay == 'PHONE'}">selected='selected'</c:if>>电话预约</option>
                                        </select>
                                    </c:otherwise>
                                </c:choose>

                            </td>
                            <th>&nbsp;</th>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <th valign="top">备注</th>
                            <td colspan="5">
                                <form:textarea path="remark" cssClass="text" maxlength="500"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div class="clear height"></div>
        </form:form>
        <div class="divTit " style="text-align: center; ">
            <div class="clear i_height"></div>
            <c:choose>
                <c:when test="${empty appointOrderDTO.id}">
                    <a class="button" id="saveAppointOrder" style="float: none">确定</a>
                    <a class="button2" id="clearAppointOrder" style="float: none">清空</a>
                </c:when>
                <c:otherwise>
                    <a class="button" id="modifyAppointOrder" style="float: none">保存</a>
                    <a class="button2" id="cancelModifyBtn" style="float: none">取消</a>
                </c:otherwise>
            </c:choose>
        </div>
    <%--<div class="shopping_btn">--%>
        <%--<c:if test='${!empty appointOrderDTO.id and (appointOrderDTO.status == "PENDING" || appointOrderDTO.status == "ACCEPTED")}'>--%>
            <%--<div class="divImg" id="modifyAppointOrder"><img src="images/luru.png"/>--%>
                <%--<div class="sureWords">保存</div>--%>
            <%--</div>--%>
        <%--</c:if>--%>

        <%--&lt;%&ndash;<c:if test='${!empty appointOrderDTO.id}'>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div class="divImg"><img src="images/copyInput.png"/>&ndash;%&gt;--%>

                <%--&lt;%&ndash;<div class="sureWords">复制</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;</c:if>&ndash;%&gt;--%>
        <%--&lt;%&ndash;<c:if test='${!empty appointOrderDTO.id and appointOrderDTO.status == "PENDING"}'>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div class="divImg"><img src="images/sureStorage.jpg"/>&ndash;%&gt;--%>

                <%--&lt;%&ndash;<div class="sureWords">接受</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div class="divImg"><img src="images/return.png"/>&ndash;%&gt;--%>

                <%--&lt;%&ndash;<div class="sureWords">拒绝</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;</c:if>&ndash;%&gt;--%>
        <%--<c:if test='${!empty appointOrderDTO.id and (appointOrderDTO.status == "PENDING" || appointOrderDTO.status == "ACCEPTED")}'>--%>
            <%--<div class="divImg"><img src="images/invalid.png"/>--%>

                <%--<div class="sureWords">取消</div>--%>
            <%--</div>--%>
        <%--</c:if>--%>
        <%--&lt;%&ndash;<c:if test='${!empty appointOrderDTO.id and appointOrderDTO.status == "ACCEPTED"}'>&ndash;%&gt;--%>
        <%--&lt;%&ndash;<div class="divImg"><img src="images/generate.jpg"/>&ndash;%&gt;--%>

            <%--&lt;%&ndash;<div class="sureWords">生成单据</div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;</c:if>&ndash;%&gt;--%>
        <%--&lt;%&ndash;<c:if test='${!empty appointOrderDTO.id}'>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div class="divImg" id="printBtn"><img src="images/print.png"/>&ndash;%&gt;--%>

                <%--&lt;%&ndash;<div class="sureWords">打印</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--&lt;%&ndash;</c:if>&ndash;%&gt;--%>
    <%--</div>--%>
    <div class="clear height"></div>
</div>
</div>
<iframe id="iframe_moreUserInfo"
        style="position:absolute;z-index:7; left:200px; top:200px; display:none;overflow:hidden;"
        allowtransparency="true" width="840px" height="800px" frameborder="0" scrolling="no" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>