<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-9-29
  Time: 下午3:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
        <td>Group Name</td>
        <td>Operations</td>
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
                <a href="user.do?">Edit Members</a>
            </td>
        </tr>
    </c:forEach>
    <% } %>
</table>
<br>
<a href="#">返回查询页面</a>
</body>
</html>