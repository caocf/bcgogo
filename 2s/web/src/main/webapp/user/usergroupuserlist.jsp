<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-10-9
  Time: 下午6:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>
    <title>Simple jsp page</title>
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
<table class="tableGroup">
    <tr>
        <td>User Name</td>
        <td>Name</td>
        <td>Mobile</td>
        <td>qq</td>
        <td>Operations</td>
    </tr>
    <%
        if (request.getAttribute("userGroupUserList") != null) {
    %>
    <c:forEach var="userGroupUser" items="${userGroupUserList}" varStatus="status">
        <tr>
            <td>
                <c:out value="${userGroupUser.userName}"></c:out>
            </td>
            <td>
                <c:out value="${userGroupUser.name}"></c:out>
            </td>
            <td>
                <c:out value="${userGroupUser.mobile}"></c:out>
            </td>
            <td>
                <c:out value="${userGroupUser.qq}"></c:out>
            </td>
            <td>
                <a href="user.do?method=getuserbyuserid&userId=${userGroupUser.id}">Edit</a> |
                <a href="user.do?method=removeuserfromgroup&userId=${userGroupUser.id}&userGroupId=${userGroupId}">从该组中删除用户</a>
            </td>
        </tr>
    </c:forEach>
    <% } %>
</table>
</body>
</html>