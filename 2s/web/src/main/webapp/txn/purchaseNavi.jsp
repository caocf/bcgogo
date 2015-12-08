<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<c:set var="isOnline" value="<%=request.getParameter(\"isOnline\")%>"/>
<%--<div class="mainTitles">
    <div class="titleWords">
        <c:choose>
            <c:when test="${currPage==\"goodsBuy\"}">采购单</c:when>
            <c:when test='${currPage==\"goodsStorage\"}'>入库单</c:when>
            <c:when test='${currPage==\"purchaseReturn\"}'>入库退货单</c:when>
            <c:otherwise>入库管理</c:otherwise>
        </c:choose>
    </div>
</div>--%>

<bcgogo:permissionParam permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE,WEB.TXN.PURCHASE_MANAGE.STORAGE,WEB.TXN.PURCHASE_MANAGE.RETURN" permissionKey="purchaseMenu">
    <c:if test="${purchaseMenuPermissionCounts>1}">
        <div class="titleList">
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
                <a class="${currPage == 'goodsBuy'?'click':''}" action-type="menu-click"
                   menu-name="WEB.TXN.PURCHASE_MANAGE.PURCHASE"
                   href="javascript:newOtherOrder('RFbuy.do?method=create')">采购单</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
                <a class="${currPage == 'goodsStorage'?'click':''}" action-type="menu-click"
                   menu-name="WEB.TXN.PURCHASE_MANAGE.STORAGE"
                   href="javascript:newOtherOrder('storage.do?method=getProducts&type=txn')">入库单</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN" resourceType="menu">
                <a class="${currPage == 'purchaseReturn'?'click':''}" action-type="menu-click"
                   menu-name="WEB.TXN.PURCHASE_MANAGE.RETURN"
                   href="javascript:newOtherOrder('goodsReturn.do?method=createReturnStorage')">入库退货单</a>
            </bcgogo:hasPermission>
        </div>
    </c:if>
</bcgogo:permissionParam>
<c:if test="${currPage == 'goodsBuy'}">
    <div class="j_draft">
        <div class="j_draft_left"></div>
        <div class="j_draft_body">
            <a class="Rightline" href="javascript:newPurchaseOrder()">新增采购</a>
            <a href="javascript:getDraftOrderBox();">草稿箱</a>
        </div>
        <div class="j_draft_right"></div>
    </div>
</c:if>
<c:if test="${currPage == 'goodsStorage'}">
    <div class="j_draft">
        <div class="j_draft_left"></div>
        <div class="j_draft_body">
            <a class="Rightline" href="javascript:newStorageOrder()">新增入库</a>
            <a href="javascript:getDraftOrderBox();">草稿箱</a>
        </div>
        <div class="j_draft_right"></div>
    </div>
</c:if>
<c:if test="${currPage == 'purchaseReturn'}">
    <div class="j_draft">
        <div class="j_draft_left"></div>
        <div class="j_draft_body">
            <a class="Rightline" href="javascript:newReturnStorageOrder()">新增退货</a>
            <a href="javascript:getDraftOrderBox();">草稿箱</a>
        </div>
        <div class="j_draft_right"></div>
    </div>
</c:if>
</div>
