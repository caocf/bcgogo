<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<div class="mainTitles">
    <div class="titleWords">
        <c:choose>
            <c:when test="${currPage==\"shopBasicInfo\"}">基本资料</c:when>
            <c:when test="${currPage==\"staffManagerNaviMenu\"}">员工管理</c:when>
            <c:when test="${currPage==\"customConfigNaviMenu\"}">自定义配置</c:when>
            <c:when test="${currPage==\"dataImportNaviMenu\"}">数据导入</c:when>
            <c:otherwise>系统设置</c:otherwise>
        </c:choose>
    </div>
<%--</div>--%>

