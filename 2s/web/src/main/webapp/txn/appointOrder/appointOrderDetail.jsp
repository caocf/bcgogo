<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: king
  Date: 13-9-5
  Time: 下午6:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>预约服务</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/appointOrderDetail<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"VEHICLE_CONSTRUCTION_APPOINT_ORDER_LIST");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">预约详情</div>
    </div>
    <div class="booking-management">
        <input type="hidden" id="appointOrderId" value="${appointOrderDTO.idStr}"/>
        <input type="hidden" id="orderType" value="${appointOrderDTO.orderType}"/>
        <input type="hidden" id="coordinateLat" value="${appointOrderDTO.coordinateLat}"/>
        <input type="hidden" id="coordinateLon" value="${appointOrderDTO.coordinateLon}"/>

        <div class="document-no">
            <c:if test="${'TO_DO_REPAIR' eq appointOrderDTO.status || 'HANDLED' eq appointOrderDTO.status}">
                <c:choose>
                    <c:when test="${appointOrderDTO.orderType eq 'WASH_BEAUTY'}">
                        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE">
                            <a class="blue_color" style="float:right" id="showWashOrRepairOrder">查看单据></a>
                        </bcgogo:hasPermission>
                    </c:when>
                    <c:otherwise>
                        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
                            <a class="blue_color" style="float:right" id="showWashOrRepairOrder">查看单据></a>
                        </bcgogo:hasPermission>
                    </c:otherwise>
                </c:choose>
            </c:if>
            单据号：<span>${appointOrderDTO.receiptNo}</span>
        </div>
        <div class="document-content">
            <h1>基本信息</h1>
            <table width="100%" border="0" cellspacing="0" class="equal2">
                <colgroup>
                    <col width="100">
                    <col width="150">
                    <col width="150">
                    <col width="150">
                    <col width="150">
                </colgroup>
                <tr>
                    <td class="equal2_title">客户名</td>
                    <td class="equal2_title">客户手机</td>
                    <td class="equal2_title">车牌号</td>
                    <td class="equal2_title">品牌车型</td>
                    <td class="equal2_title">车主手机</td>
                </tr>
                <tr>
                    <td>${appointOrderDTO.customer}</td>
                    <td>${appointOrderDTO.customerMobile}</td>
                    <td>${appointOrderDTO.vehicleNo}</td>
                    <td>${appointOrderDTO.vehicleBrand}&nbsp;${appointOrderDTO.vehicleModel}</td>
                    <td>${appointOrderDTO.vehicleMobile}</td>
                </tr>
                <tr>
                    <td class="equal2_title">服务类型</td>
                    <td class="equal2_title">预计服务时间</td>
                    <td class="equal2_title">当前里程</td>
                    <td class="equal2_title">下单时间</td>
                    <td class="equal2_title">状态</td>
                </tr>
                <tr>
                    <td>${appointOrderDTO.appointServiceType}</td>
                    <td>${appointOrderDTO.appointTimeStr}</td>
                    <td><fmt:formatNumber value="${appointOrderDTO.currentMileage}" pattern="#"/></td>
                    <td>${appointOrderDTO.createTimeStr}</td>
                    <td>${appointOrderDTO.status.name}</td>
                </tr>
                <tr>
                    <td class="equal2_title">备注</td>
                    <td colspan="4">
                        <div class="refuse_reason_02">${appointOrderDTO.remark}</div>
                    </td>
                </tr>
                <c:choose>
                    <c:when test="${'REFUSED' eq appointOrderDTO.status}">
                        <tr>
                            <td class="equal2_title">拒绝理由</td>
                            <td colspan="4">
                                <div class="refuse_reason_02">${appointOrderDTO.refuseMsg}</div>
                            </td>
                        </tr>
                    </c:when>
                    <c:when test="${'CANCELED' eq appointOrderDTO.status}">
                        <tr>
                            <td class="equal2_title">取消理由</td>
                            <td colspan="4">
                                <div class="refuse_reason_02">${appointOrderDTO.cancelMsg}</div>
                            </td>
                        </tr>
                    </c:when>
                </c:choose>

            </table>
            <c:if test="${not empty appointOrderDTO.serviceDTOs}">
                <h1>服务项目</h1>
                <table width="100%" border="0" cellspacing="0" class="equal2">
                    <colgroup>
                        <col width="100">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                    </colgroup>
                    <tr>
                        <td class="equal2_title">项目</td>
                        <td class="equal2_title">标准工时（时）</td>
                        <td class="equal2_title">工时单价（元）</td>
                        <td class="equal2_title">金额（元）</td>
                    </tr>
                    <c:forEach items="${appointOrderDTO.serviceDTOs}" var="serviceDTO">
                        <tr>
                            <td>${serviceDTO.service}</td>
                            <td><fmt:formatNumber value="${serviceDTO.standardHours}" pattern="#.##"/></td>
                            <td><fmt:formatNumber value="${serviceDTO.standardUnitPrice}" pattern="#.##"/></td>
                            <td><fmt:formatNumber value="${serviceDTO.total}" pattern="#.##"/></td>
                        </tr>
                    </c:forEach>
                </table>
                <div class="clear total-of font12-normal">项目合计：<span
                        class="yellow_color"><fmt:formatNumber value="${appointOrderDTO.serviceTotal}" pattern="#.##"/></span>元
                </div>
            </c:if>
            <c:if test="${not empty appointOrderDTO.itemDTOs}">
                <h1>商品材料</h1>
                <table width="100%" border="0" cellspacing="0" class="equal2">
                    <colgroup>
                        <col width="100">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                    </colgroup>
                    <tr>
                        <td class="equal2_title">商品编号</td>
                        <td class="equal2_title">品名</td>
                        <td class="equal2_title">品牌/产地</td>
                        <td class="equal2_title">规格</td>
                        <td class="equal2_title">型号</td>
                        <td class="equal2_title">单价（元）</td>
                        <td class="equal2_title">数量</td>
                        <td class="equal2_title">单位</td>
                        <td class="equal2_title">金额（元）</td>
                    </tr>
                    <c:forEach items="${appointOrderDTO.itemDTOs}" var="itemDTO">
                    <tr>
                        <td>${itemDTO.commodityCode}</td>
                        <td>${itemDTO.productName}</td>
                        <td>${itemDTO.brand}</td>
                        <td>${itemDTO.spec}</td>
                        <td>${itemDTO.model}</td>
                        <td><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></td>
                        <td><fmt:formatNumber value="${itemDTO.amount}" pattern="#.##"/></td>
                        <td>${itemDTO.unit}</td>
                        <td><fmt:formatNumber value="${itemDTO.total}" pattern="#.##"/></td>
                    </tr>
                    </c:forEach>
                </table>
                <div class="clear total-of  font12-normal">材料合计：<span class="yellow_color"><fmt:formatNumber value="${appointOrderDTO.itemTotal}" pattern="#.##"/></span>元</div>
            </c:if>
            <c:if test="${not empty appointOrderDTO.serviceDTOs or not empty appointOrderDTO.itemDTOs}">
                <div class="clear total-of"> 单据总额：<span class="yellow_color"><fmt:formatNumber value="${appointOrderDTO.total}" pattern="#.##"/></span>元 </div>
            </c:if>



            <%--<div class="equal">--%>
                <%--<div class="row">--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">车牌号</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.vehicleNo}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">车辆品牌</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.vehicleBrand}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">车型</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.vehicleModel}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">联系人</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.vehicleContact}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">联系方式</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.vehicleMobile}</div>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<h1>客户信息</h1>--%>
            <%--<div class="equal">--%>
                <%--<div class="row">--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">客户名</div>--%>
                        <%--&lt;%&ndash;加一个空格，否则样式会挂掉，以下所有相同&ndash;%&gt;--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.customer}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">手机号</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.customerMobile}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">会员卡号</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.memberNo}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">会员类型</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.memberType}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">卡内余额</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.memberBalance}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">状态</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.memberStatus}</div>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<h1>服务信息</h1>--%>
            <%--<div class="equal">--%>
                <%--<div class="row">--%>
                    <%--<div class="one" style="width:130px;">--%>
                        <%--<div class="title">服务类型</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.appointServiceType}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">下单时间</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.createTimeStr}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">预计服务时间</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.appointTimeStr}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">预约人</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.appointCustomer}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">接待人</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.assistantMan}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">预约方式</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.appointWay.name}</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">状态</div>--%>
                        <%--<div class="td">&nbsp;${appointOrderDTO.status.name}</div>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <%--<div class="row2">--%>
                    <%--<div class="one">--%>
                        <%--<div class="title">备注</div>--%>
                    <%--</div>--%>
                    <%--<div class="one">--%>
                        <%--<div class="td" style="width:838px; left:145px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;" title="${appointOrderDTO.remark}">&nbsp;${appointOrderDTO.remark}</div>--%>
                    <%--</div>--%>
                    <%--<c:choose>--%>
                        <%--<c:when test="${'REFUSED' eq appointOrderDTO.status}">--%>
                            <%--<div class="one">--%>
                                <%--<div class="title">拒绝理由</div>--%>
                            <%--</div>--%>
                            <%--<div class="one">--%>
                                <%--<div class="td" style="width:838px; left:145px; border:1px solid #BEBEBE;top:420px;">&nbsp;${appointOrderDTO.refuseMsg}</div>--%>
                            <%--</div>--%>
                        <%--</c:when>--%>
                        <%--<c:when test="${'CANCELED' eq appointOrderDTO.status}">--%>
                            <%--<div class="one">--%>
                                <%--<div class="title">取消理由</div>--%>
                            <%--</div>--%>
                            <%--<div class="one">--%>
                                <%--<div class="td" style="width:838px; left:145px; border:1px solid #BEBEBE;top:420px;">&nbsp;${appointOrderDTO.cancelMsg}</div>--%>
                            <%--</div>--%>
                        <%--</c:when>--%>
                    <%--</c:choose>--%>

                <%--</div>--%>
            <%--</div>--%>
            <%--<c:if test="${not empty appointOrderDTO.coordinateLat && not empty appointOrderDTO.coordinateLon}">--%>
                <%--<h1>预约地点</h1>--%>
                <%--<iframe id="appointOrderPositionIframe" width="420" height="210" frameborder="0">--%>
                <%--</iframe>--%>
                <%--<div class="map_btn">--%>
                    <%--<div class="map_view" id="viewBigPositionBtn"><img src="images/common/ico/sp-common.png"/>查看完整地图</div>--%>
                    <%--&lt;%&ndash;<div class="map_view" id="sendPositionBtn">发送到手机xia</div>&ndash;%&gt;--%>
                <%--</div>--%>
                <%--<div class="clear"></div>--%>
            <%--</c:if>--%>
        </div>
        <div class="clear i_height"></div>
        <div class="shopping_btn">
            <bcgogo:hasPermission resourceType="render" permissions="WEB.TXN.APPOINT_ORDER_MANAGER">
            <c:if test="${'ACCEPTED' eq appointOrderDTO.status}">
                <div class="divImg" id="modifyBtn">
                    <input  class="modify" type="button" onfocus="this.blur();">
                    <div class="sureWords">改单</div>
                </div>
            </c:if>
            <%--<div class="divImg">--%>
                <%--<input id="copyBtn" class="copy" type="button" onfocus="this.blur();">--%>
                <%--<div class="sureWords">复制</div>--%>
            <%--</div>--%>
            <c:if test="${'PENDING' eq appointOrderDTO.status}">
            <div class="divImg" id="acceptBtn">
                <input class="accept" type="button" onfocus="this.blur();">
                <div class="sureWords">接受</div>
            </div>
            </c:if>

            <c:if test="${'PENDING' eq appointOrderDTO.status}">
            <div class="divImg" id="refuseBtn">
                <input  class="refuse" type="button" onfocus="this.blur();">
                <div class="sureWords">拒绝</div>
            </div>
            </c:if>
            <c:if test="${'PENDING' eq appointOrderDTO.status || 'ACCEPTED' eq appointOrderDTO.status}">
            <div class="divImg" id="cancelBtn">
                <input  class="invalid" type="button" onfocus="this.blur();">
                <div class="sureWords">取消</div>
            </div>
             </c:if>
                <c:if test="${'ACCEPTED' eq appointOrderDTO.status}">
                    <c:choose>
                        <c:when test="${appointOrderDTO.appointServiceType == '洗车'}">
                            <bcgogo:hasPermission resourceType="render" permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE">
                                <div class="divImg" id="createOtherOrder">
                                    <input class="save" type="button" onfocus="this.blur();">

                                    <div class="sureWords">生成单据</div>
                                </div>
                            </bcgogo:hasPermission>
                        </c:when>
                        <c:otherwise>
                            <bcgogo:hasPermission resourceType="render" permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
                                <div class="divImg" id="createOtherOrder">
                                    <input class="save" type="button" onfocus="this.blur();">
                                    <div class="sureWords">生成单据</div>
                                </div>
                            </bcgogo:hasPermission>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission resourceType="render" permissions="WEB.TXN.APPOINT_ORDER_PRINT">
            <div class="divImg" id="printBtn">
                <input class="print" type="button" onfocus="this.blur();">
                <div class="sureWords">打印</div>
            </div>
            </bcgogo:hasPermission>
            <div class="divImg" id="returnBtn">
                <input class="return" type="button" onfocus="this.blur();">
                <div class="sureWords">返回列表</div>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="900px" height="450px" frameborder="0" src=""></iframe>


<div class="alertMain addProducts" id="refuseDialog" style="display: none;">
    <div class="height"></div>
    <table cellpadding="0" cellspacing="0" class="tab_product">
        <col width="60px">
        <col>
        <tr>
            <td valign="top">拒绝理由：</td>
            <td style="text-align:left;">
                <textarea class="txt" style=" height:100px; width:330px;" maxlength="150" id="refuseMsg"></textarea>
            </td>
        </tr>
    </table>
    <div class="height"></div>
    <div class="button">
        <a class="btnSure" id="confirmRefuse">保&nbsp;存</a>
        <a class="btnSure" id="cancelRefuse">取&nbsp;消</a>
    </div>
</div>

<div class="alertMain addProducts" id="cancelDialog" style="display: none;">
    <div class="height"></div>
    <table cellpadding="0" cellspacing="0" class="tab_product">
        <col width="60px">
        <col>
        <tr>
            <td valign="top">取消理由：</td>
            <td style="text-align:left;">
                <textarea class="txt" style=" height:100px; width:330px;" maxlength="150" id="cancelMsg"></textarea>
            </td>
        </tr>
    </table>
    <div class="height"></div>
    <div class="button">
        <a class="btnSure" id="confirmCancel">保&nbsp;存</a>
        <a class="btnSure" id="cancelCancel">取&nbsp;消</a>
    </div>
</div>

<div>
    <div class="img_dialog_mask" id="bigMapContainer" style="display: none"></div>
    <div class="img_dialog_layer" id="bigMapContainerLayer" style="display: none">
        <div style="width:840px; margin:0 auto;">
            <div class="lay-main lay-map">
        <div class="hd"> <a class="close" id="bigMapContainerClose" title="关闭"></a> </div>
                <div class="lay-con">
                    <div class="map">
                <div class="map-cont" style=" width:820px; height:620px; overflow: hidden;">
                    <iframe id="iframe_big_map" width="820" height="620" frameborder="0" src=""></iframe>
                </div>
                <div class="map-note">注：地图位置标注仅供参考，具体情况以实际道路标实信息为准</div>
            </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
