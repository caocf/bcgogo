<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-9-30
  Time: 上午10:25
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
<form action="user.do?method=testgroup" method="post" class="addgroup">
    <h3>Filter Group</h3>
    shop id：<input type="text" size="14" id="shopId" name="shopId"/><br>
    <input type="submit" id="filter" value="search"/>
</form>
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
<form id="addgroup" method="post" action="user.do?method=testaddgroup" class="addgroup">
    <h3>Add Group</h3>
    <label>GroupName:</label><input type="text" name="groupName"/><br>
    <label>ShopId:</label><input type="text" name="shopId" value="${}"/>

    <div><input type="submit" value="add"/></div>
</form>
</body>
</html>