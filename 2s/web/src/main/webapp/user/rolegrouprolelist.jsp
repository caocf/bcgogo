<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-10-9
  Time: 下午8:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>
    <title>角色</title>
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
</head>
<body>
<div>
    <form action="user.do?method=saverolegroup&shopId=${shopId}" method="post">
        角色：
        <select name="roleId">
            <option value="1">SUPERADMIN</option>
            <option value="2">ADMIN</option>
            <option value="4">BUYER</option>
            <option value="8">SALES</option>
            <option value="16">ACCOUNTANT</option>
            <option value="32">INVENTORY</option>
            <option value="64">SERVICE</option>
            <option value="128">VIEWER</option>
        </select>
        用户组：
        <select name="userGroupId">
            <c:forEach var="userGrouplt" items="${userGroupList}" varStatus="status">
                <option value="${userGrouplt.id}"><c:out value="${userGrouplt.name}"></c:out></option>
            </c:forEach>
        </select>
        <input type="submit" value="添加"/>
    </form>
</div>

<table class="tableGroup">
    <tr>
        <td>角色名</td>
        <td>操作</td>
    </tr>
    <%
        if (request.getAttribute("userGroupRoleList") != null) {
    %>
    <c:forEach var="userGroupRole" items="${userGroupRoleList}" varStatus="status">
        <tr>
            <td>
                <c:choose>
                    <c:when test="${userGroupRole.roleId == 1}">
                     SUPERADMIN
                    </c:when>
                    <c:when test="${userGroupRole.roleId == 2}">
                     ADMIN
                    </c:when>
                    <c:when test="${userGroupRole.roleId == 4}">
                     BUYER
                    </c:when>
                    <c:when test="${userGroupRole.roleId == 8}">
                     SALES
                    </c:when>
                    <c:when test="${userGroupRole.roleId == 16}">
                     ACCOUNTANT
                    </c:when>
                    <c:when test="${userGroupRole.roleId == 32}">
                     INVENTORY
                    </c:when>
                    <c:when test="${userGroupRole.roleId == 64}">
                     SERVICE
                    </c:when>
                    <c:when test="${userGroupRole.roleId == 128}">
                     VIEWER
                    </c:when>
                </c:choose>
            </td>
            <td>
                <a href="user.do?method=unassignrolefromgroup&roleId=${userGroupRole.roleId}&userGroupId=${userGroupId}&shopId=${shopId}">删除</a>
            </td>
        </tr>
    </c:forEach>
    <% } %>
</table>
</body>
</html>