<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<div class="titleWords">
    采购分析<br/>
</div>
<bcgogo:permissionParam permissions="WEB.STAT.PURCHASE_ANALYST.COST,WEB.STAT.PURCHASE_ANALYST.PRICE,WEB.STAT.PURCHASE_ANALYST.SUPPLIER,WEB.STAT.PURCHASE_ANALYST.RETURN" permissionKey="purchaseAnalystMenu">
    <c:if test="${purchaseAnalystMenuPermissionCounts>1}">
        <div class="titleList">
            <bcgogo:hasPermission permissions="WEB.STAT.PURCHASE_ANALYST.COST">
                <a  class="<c:choose><c:when test="${currPage==\"costStat\"}">click</c:when><c:otherwise></c:otherwise></c:choose>" action-type="menu-click" menu-name="COST_STAT_GET_COST_STAT" href="costStat.do?method=getCostStat">成本</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.PURCHASE_ANALYST.PRICE">
                <a class="<c:choose><c:when test="${currPage==\"priceStat\"}">click</c:when><c:otherwise></c:otherwise></c:choose>" action-type="menu-click" menu-name="COST_STAT_GET_PRICE_STAT" href="costStat.do?method=getPriceStat">价格波动</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.PURCHASE_ANALYST.SUPPLIER">
                <a class="<c:choose><c:when test="${currPage==\"supplierStat\"}">click</c:when><c:otherwise></c:otherwise></c:choose>" action-type="menu-click" menu-name="COST_STAT_GET_SUPPLIER_STAT" href="costStat.do?method=getSupplierStat">供应商交易</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.PURCHASE_ANALYST.RETURN">
                <a class="<c:choose><c:when test="${currPage==\"returnStat\"}">click</c:when><c:otherwise></c:otherwise></c:choose>" action-type="menu-click" menu-name="COST_STAT_GET_RETURN_STAT" href="costStat.do?method=getReturnStat">退货</a>
            </bcgogo:hasPermission>
        </div>
    </c:if>
</bcgogo:permissionParam>


<%--<div class="mainTitles clear">
    <div class="titleWords">
        <c:choose>
            <c:when test="${currPage==\"costStat\"}">成本</c:when>
            <c:when test='${currPage==\"priceStat\"}'>价格波动</c:when>
            <c:when test='${currPage==\"supplierStat\"}'>供应商交易</c:when>
            <c:when test='${currPage==\"returnStat\"}'>退货</c:when>
            <c:otherwise>采购分析</c:otherwise>
        </c:choose>
    </div>
</div>--%>
