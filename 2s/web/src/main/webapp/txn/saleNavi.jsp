<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<%--<div class="mainTitles">
    <div class="titleWords">
        <c:choose>
            <c:when test="${currPage==\"goodsSale\"}">销售单</c:when>
            <c:when test='${currPage==\"saleReturn\"}'>销售退货单</c:when>
            <c:otherwise>销售管理</c:otherwise>
        </c:choose>
    </div>
</div>--%>
<bcgogo:permissionParam permissions="WEB.TXN.SALE_MANAGE.SALE,WEB.TXN.SALE_MANAGE.RETURN" permissionKey="saleMenu">
    <c:if test="${saleMenuPermissionCounts>1}">
        <div class="titleList">
            <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
                <a class="${currPage == 'goodsSale'?'click':''}" action-type="menu-click"
                   menu-name="WEB.TXN.SALE_MANAGE.SALE"
                   url="javascript:newOtherOrder('sale.do?method=getProducts&type=txn')">销售</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN" resourceType="menu">
                <a class="${currPage == 'saleReturn'?'click':''}" action-type="menu-click"
                   menu-name="WEB.TXN.SALE_MANAGE.RETURN"
                   url="javascript:newOtherOrder('salesReturn.do?method=createSalesReturn')">销售退货</a>
            </bcgogo:hasPermission>
        </div>
    </c:if>
</bcgogo:permissionParam>

<c:if test="${currPage == 'goodsSale'}">
    <div class="j_draft">
        <div class="j_draft_left"></div>
        <div class="j_draft_body">
            <a class="Rightline" href="javascript:newSaleOrder()">新增销售</a>
            <a href="javascript:getDraftOrderBox();">草稿箱</a>
        </div>
        <div class="j_draft_right"></div>
    </div>
</c:if>
<c:if test="${currPage == 'saleReturn'}">
    <div class="j_draft">
        <div class="j_draft_left"></div>
        <div class="j_draft_body">
            <a class="Rightline" href="javascript:newSaleReturnOrder()">新增退货</a>
            <a href="javascript:getDraftOrderBox();">草稿箱</a>
        </div>
        <div class="j_draft_right"></div>
    </div>
</c:if>
</div>
