<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<div class="mainTitles clear">
<div class="titleWords">
    营业分析
</div>
<bcgogo:permissionParam permissions="WEB.STAT.BUSINESS_ANALYSIS.CUSTOMER_STAT,WEB.STAT.BUSINESS_ANALYSIS.SUPPLIER_STAT,
        WEB.STAT.BUSINESS_ANALYSIS.CATEGORY_STAT,WEB.STAT.BUSINESS_ANALYSIS.SALES_RETURN_STAT,WEB.STAT.BUSINESS_ANALYSIS.INVENTORY_RETURN_STAT" permissionKey="businessAnalysisMenu">
    <c:if test="${businessAnalysisMenuPermissionCounts>1}">
        <div class="titleList">
            <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_ANALYSIS.CUSTOMER_STAT">
                <a class="<c:if test='${currPage==\"customerStat\"}'>click</c:if>"  action-type="menu-click" menu-name="ITEM_STAT_CUSTOMER_STAT"  id="customerStat">客户交易</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_ANALYSIS.SUPPLIER_STAT">
                <a class="<c:if test='${currPage==\"supplierStat\"}'>click</c:if>"  action-type="menu-click" menu-name="ITEM_STAT_SUPPLIER_STAT"  id="supplierStat">供应商交易</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_ANALYSIS.CATEGORY_STAT">
                <a class="<c:if test='${currPage==\"categoryStat\"}'>click</c:if>" action-type="menu-click" menu-name="ITEM_STAT_CATEGORY_STAT"  id="categoryStat">分项销售</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_ANALYSIS.SALES_RETURN_STAT">
                <a class="<c:if test='${currPage==\"salesReturnStat\"}'>click</c:if>" action-type="menu-click" menu-name="BUSINESS_ANALYSIS_REDIRECT_SALES_RETURN_STAT"  id="salesReturnStat">销售退货</a>
            </bcgogo:hasPermission>

            <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_ANALYSIS.INVENTORY_RETURN_STAT">
                <a class="<c:if test='${currPage==\"inventoryReturnStat\"}'>click</c:if>" action-type="menu-click" menu-name="BUSINESS_ANALYSIS_REDIRECT_INVENTORY_RETURN_STAT"  id="inventoryReturnStat">
                    入库退货
                </a>
            </bcgogo:hasPermission>
        </div>
    </c:if>
</bcgogo:permissionParam>
</div>

<%--<div class="mainTitles clear">
    <div class="titleWords">
        <c:choose>
            <c:when test="${currPage==\"customerStat\"}">客户交易</c:when>
            <c:when test='${currPage==\"supplierStat\"}'>供应商交易</c:when>
            <c:when test='${currPage==\"categoryStat\"}'>分项销售</c:when>
            <c:when test='${currPage==\"salesReturnStat\"}'>销售退货</c:when>
            <c:when test='${currPage==\"inventoryReturnStat\"}'>入库退货</c:when>
            <c:otherwise>营业分析</c:otherwise>
        </c:choose>
    </div>
</div>--%>

