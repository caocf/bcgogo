<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<div class="mainTitles">
    <div class="cusTitle">
        <%--<c:choose>
            <c:when test="${currPage==\"supplierData\"}">供应商列表</c:when>
            <c:when test="${currPage==\"applierSupplierData\"}">推荐供应商</c:when>
            <c:otherwise>供应商管理</c:otherwise>
        </c:choose>--%>
            供应商列表
    </div>
    <%--<bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SEARCH.APPLY.SUPPLIER">--%>
        <%--<div class="titleList">--%>
            <%--<a class="${currPage eq 'supplierData' ? 'click' :''}" action-type="menu-click"--%>
               <%--menu-name="CUSTOMER_SEARCH_SUPPLIERS" href="customer.do?method=searchSuppiler">本店供应商</a>--%>
        <%--</div>--%>
    <%--</bcgogo:hasPermission>--%>
</div>
