<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<%--<div class="mainTitles">
    <div class="titleWords">
        <c:choose>
            <c:when test="${currPage==\"sale\"}">待办销售单</c:when>
            <c:when test="${currPage==\"saleReturn\"}">待办销售退货单</c:when>
            <c:when test="${currPage==\"purchase\"}">待办采购单</c:when>
            <c:when test="${currPage==\"purchaseReturn\"}">待办入库退货单</c:when>
            <c:otherwise>订单中心</c:otherwise>
        </c:choose>
    </div>
</div>--%>
    <bcgogo:permissionParam permissions="WEB.SCHEDULE.REMIND_ORDERS.SALE,WEB.SCHEDULE.REMIND_ORDERS.SALE_RETURN,
            WEB.SCHEDULE.REMIND_ORDERS.PURCHASE,WEB.SCHEDULE.REMIND_ORDERS.PURCHASE_RETURN" permissionKey="remindOrdersMenu">
        <c:if test="${remindOrdersMenuPermissionCounts>1}">
            <div class="titleList" style="border:1px solid #DEDEDE; border-width: 1px 0px 1px 1px;margin-top:0px">
                <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_ORDERS.SALE">
                    <a id="todoSaleOrders" class="${currPage == 'sale'?'click':''}" action-type="menu-click" menu-name="WEB.SCHEDULE.REMIND_ORDERS.SALE"  href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS">待办销售单</a>
                    <span class="num j_todoSalesOrdersAmount"></span>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_ORDERS.SALE_RETURN">
                    <a id="todoSaleReturnOrders" class="${currPage == 'saleReturn'?'click':''}" action-type="menu-click" menu-name="WEB.SCHEDULE.REMIND_ORDERS.SALE_RETURN"  href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_RETURN_ORDERS">待办销售退货单</a>
                    <span class="num j_todoSalesReturnOrdersAmount"></span>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_ORDERS.PURCHASE">
                    <a id="todoPurchaseOrders" class="${currPage == 'purchase'?'click':''}" action-type="menu-click" menu-name="WEB.SCHEDULE.REMIND_ORDERS.PURCHASE"  href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS">待办采购单</a>
                    <span class="num j_todoPurchaseOrdersAmount"></span>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_ORDERS.PURCHASE_RETURN">
                    <a id="todoPurchaseReturnOrders" class="${currPage == 'purchaseReturn'?'click':''}" action-type="menu-click" menu-name="WEB.SCHEDULE.REMIND_ORDERS.PURCHASE_RETURN"  href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS">待办入库退货单</a>
                    <span class="num j_todoPurchaseReturnOrdersAmount"></span>
                </bcgogo:hasPermission>
            </div>
        </c:if>
    </bcgogo:permissionParam>
<%--</div>--%>
