<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<c:set var="isRepairPickingSwitchOn" value="<%=request.getParameter(\"isRepairPickingSwitchOn\")%>"/>


<%--如果只有一个菜单不显示--%>
<bcgogo:permissionParam permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH,WEB.TXN.INVENTORY_MANAGE.INVENTORY_PRICE_ADJUSTMENT.BASE,
        WEB.TXN.INVENTORY_MANAGE.REPAIR_PICKING,WEB.TXN.INVENTORY_MANAGE.INNER_PICKING,WEB.TXN.INVENTORY_MANAGE.INNER_RETURN,
        WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD,WEB.TXN.INVENTORY_MANAGE.BORROW_ORDER" permissionKey="inventoryMenu">
    <c:if test="${inventoryMenuPermissionCounts>1}">
        <div class="titleList">
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH" resourceType="menu">
                <a class="${currPage == 'inventory'?'click':''}" action-type="menu-click"
                   menu-name="TXN_INVENTORY_MANAGE_STOCK_SEARCH"
                   url="stockSearch.do?method=getStockSearch&type=txn">库存查询</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INVENTORY_PRICE_ADJUSTMENT.BASE"
                                  resourceType="menu">
                <a class="${currPage == 'inventoryCheck'?'click':''}" action-type="menu-click"
                   menu-name="WEB.TXN.INVENTORY_MANAGE.INVENTORY_PRICE_ADJUSTMENT.BASE"
                   url="inventoryCheck.do?method=toInventoryCheckRecord">库存盘点</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.REPAIR_PICKING" resourceType="menu">
                <c:if test="${isRepairPickingSwitchOn}">
                    <a id="repairPickingTab" action-type="menu-click" url="pick.do?method=showRepairPickingListPage"
                       menu-name="TXN_INVENTORY_MANAGE_REPAIR_PICKING"
                       class="${currPage == 'repairPicking'?'click':''}">
                        维修领料<span class="iconClose" style="display: none;" id="closeRepairPicking"></span>
                    </a>

                    <div class="tixing"
                         style="position: absolute;left: 429px;margin-top: 24px;width: 500px;display: none; ">
                        <div class="tiTop"></div>
                        <div class="tiLeft"></div>
                        <div class="tiBody"> 点击后关闭领料流程</div>
                        <div class="tiRight"></div>
                    </div>
                </c:if>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INNER_PICKING" resourceType="menu">
                <a class="${currPage == 'innerPicking'?'click':''}" action-type="menu-click"
                   menu-name="WEB.TXN.INVENTORY_MANAGE.INNER_PICKING"
                   url="pick.do?method=showInnerPickingListPage">内部领料</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INNER_RETURN" resourceType="menu">
                <a class="${currPage == 'innerReturn'?'click':''}" action-type="menu-click"
                   menu-name="WEB.TXN.INVENTORY_MANAGE.INNER_RETURN"
                   url="pick.do?method=showInnerReturnListPage">内部退料</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD" resourceType="menu">
                <a class="${currPage == 'allocateRecord'?'click':''}" action-type="menu-click"
                   menu-name="WEB.TXN.INVENTORY_MANAGE.ALLOCATE_RECORD"
                   url="allocateRecord.do?method=allocateRecordList">仓库调拨</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.BORROW_ORDER" resourceType="menu">
                <a class="${currPage == 'borrowOrder'?'click':''}" action-type="menu-click"
                   menu-name="WEB.TXN.INVENTORY_MANAGE.BORROW_ORDER"
                   url="borrow.do?method=toBorrowOrderList">外部借调</a>
            </bcgogo:hasPermission>
        </div>
    </c:if>
</bcgogo:permissionParam>

</div>
