<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<div class="mainTitles">
	<div class="titleWords">
        <c:choose>
            <c:when test="${currPage==\"inventory\"}">库存管理</c:when>
            <c:when test='${currPage==\"purchase\"}'>入库管理</c:when>
            <c:when test='${currPage==\"goodsSale\"}'>销售管理</c:when>
            <c:when test='${currPage==\"pendingPurchaseOrder\"}'>采购订单</c:when>
            <c:when test='${currPage==\"repair\"}'>车辆施工</c:when>
            <c:otherwise>进销存</c:otherwise>
        </c:choose>
    </div>