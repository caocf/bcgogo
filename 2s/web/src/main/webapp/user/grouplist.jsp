<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-9-29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head><title>Get Shop Group</title></head>
<style type="text/css">
    .addgroup {
        margin: 10px 0;
        line-height: 1.4;
    }

    .addgroup h3 {
        border-bottom: 1px solid #DDD;
    }

    .tableGroup {
        width: 100%;
        border-top: none;
        border-left: none;
        border-right: none;
        border-bottom: none;
        border-collapse: collapse;
        table-layout: fixed;
    }

    .tableGroup td {
        border-bottom: 1px solid #DDD;
        padding-top: 5px;
    }
</style>
<body>
<div><a href="user.do?method=addgroup&shopId=${shopId}">新增用户组</a></div>
<table class="tableGroup">
    <tr>
        <td>组名</td>
        <td>操作</td>
    </tr>
    <%
        if (request.getAttribute("userGroupList") != null) {
    %>
    <c:forEach var="userGroup" items="${userGroupList}" varStatus="status">
        <tr>
            <td>
                <c:out value="${userGroup.name}"></c:out>
            </td>
            <td>
                <a href="user.do?method=getusergroupbyusergroupid&userGroupId=${userGroup.id}&shopId=${shopId}">修改</a> |
                <a href="user.do?method=searchgroupuser&userGroupId=${userGroup.id}&shopId=${shopId}">该组下的用户</a> |
                <a href="user.do?method=getrolebyusergroupid&userGroupId=${userGroup.id}&shopId=${shopId}">该组下的角色</a>
            </td>
        </tr>
    </c:forEach>
    <% } %>
</table>
</body>
</html>