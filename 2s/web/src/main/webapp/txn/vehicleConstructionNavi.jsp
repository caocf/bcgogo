<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<c:set var="receiptNo" value="<%=request.getParameter(\"receiptNo\")%>"/>
<c:set var="orderId" value="<%=request.getParameter(\"orderId\")%>"/>
<div class="mainTitles">
    <div class="titleWords">
        <c:choose>
            <c:when test="${currPage==\"washOrder\"}">洗车美容
               <div style="color: #666666;font-size: 14px;font-weight: normal;margin-top:-30px;">
                    <c:if test="${empty receiptNo}">
                        （单据号由系统自动生成）
                    </c:if>
                    <c:if test="${not empty receiptNo}">
                        <span style="color: #B70000;">${receiptNo} <a href="javascript:showOrderOperationLog('${orderId}','wash')"  class="blue_col">操作记录</a></span>
                    </c:if>
                </div>
            </c:when>
            <c:when test='${currPage==\"invoicingOrder\"}'>
                <div>施工销售</div>
                <div style="float: left;color: #666666;font-size: 14px;font-weight: normal;margin-top:-30px">
                    <span id="receiptNoStr">
                        <c:choose>
                            <c:when test="${not empty repairOrderDTO && not empty repairOrderDTO.receiptNo}">
                               <span><strong>单据号：<strong class="red_color">${repairOrderDTO.receiptNo}
                                   <c:if test="${empty repairOrderDTO.draftOrderIdStr}">
                                       <a href="javascript:showOrderOperationLog('${orderId}','repair')"  class="blue_col">操作记录</a>
                                   </c:if>
                               </strong></strong></span>
                            </c:when>
                            <c:otherwise>
                                （单据号由系统自动生成）
                            </c:otherwise>
                        </c:choose>
                    </span>
                     <span id="showAppointOrderSpan">
                                <c:if test="${not empty repairOrderDTO && not empty repairOrderDTO.appointOrderId}">
                                    <span>|</span><a class="blue_color" id="showAppointOrderBtn"><img src="images/icon_right.png"/>预约单</a></span>
                    </c:if>
                    </span>
                    <span id="showInsuranceSpan">
                        <c:if test="${not empty repairOrderDTO && not empty repairOrderDTO.insuranceOrderDTO}">
                            <span>|</span><a class="blue_color" id="toInsuranceBtn"><img src="images/icon_right.png"/>保险理赔${repairOrderDTO.insuranceOrderDTO.policyNo}</a>
                        </c:if>
                    </span>
                    <span id="showQualifiedSpan">
                        <c:if test="${not empty repairOrderDTO && not empty repairOrderDTO.qualifiedNo}">
                            <span>|</span><a class="blue_color" id="createQualified"><img src="images/icon_right.png"/>合格证${repairOrderDTO.qualifiedNo}</a>
                        </c:if>
                    </span>
                    <c:if test="${repairOrderSecondaryDTO != null}">
                        <span>|</span><a class="blue_color" id="secondary" data-value="${repairOrderSecondaryDTO.id}"><img src="images/icon_right.png"/>结算附表<c:if test="${repairOrderSecondaryDTO.status == 'REPAIR_REPEAL'}"><span style="color:#666666">(废)</span></c:if></a>
                    </c:if>
                </div>
            </c:when>
            <c:when test='${currPage==\"setProject\"}'>项目设置</c:when>
            <c:when test='${currPage==\"insuranceOrder\"}'>保险理赔</c:when>
            <c:otherwise>车辆施工</c:otherwise>
        </c:choose>
    </div>
    <div style="width: 200px;float: right;margin-top: 28px;">
        <c:if test="${currPage == 'invoicingOrder'}">
            <div class="cartRight"></div>
            <div class="caogao_v1">
                <a href="javascript:newRepairOrder()">新增施工单</a>&nbsp;|&nbsp;<a href="javascript:getDraftOrderBox()">草稿箱</a>
            </div>
            <div class="cartLeft"></div>
        </c:if>
        <c:if test="${currPage == 'washOrder'}">
            <div class="cartRight"></div>
            <div class="caogao_v1" style=" width: 75px;">
                <a href="javascript:newWashBeautyOrder()" >新增洗车美容</a>
            </div>
            <div class="cartLeft"></div>
        </c:if>
    </div>
</div>
