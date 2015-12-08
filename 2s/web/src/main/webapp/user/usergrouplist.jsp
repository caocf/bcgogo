<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-10-10
  Time: 下午7:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head><title>getUserGroupByUserGroupId</title>
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
    <form action="user.do?method=saveusergroup&shopId=${shopId}" method="post">
        用户：
        <select name="userId">
            <c:forEach var="userDTOlt" items="${userDTOList}" varStatus="status">
                <option value="${userDTOlt.id}"><c:out value="${userDTOlt.name}"></c:out></option>
            </c:forEach>
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
        <td>组名</td>
        <td>操作</td>
    </tr>
    <%
        if (request.getAttribute("userGroupDTOList") != null) {
    %>
    <c:forEach var="userGroupDTO" items="${userGroupDTOList}" varStatus="status">
        <tr>
            <td>
                <c:out value="${userGroupDTO.name}"></c:out>
            </td>
            <td>
                <a href="user.do?method=getusergroupbyusergroupid&userGroupId=${userGroupDTO.id}&shopId=${userGroupDTO.shopId}">修改</a> |
                <a href="user.do?method=searchgroupuser&userGroupId=${userGroupDTO.id}">该组下的用户</a> |
                <a href="user.do?method=getrolebyusergroupid&userGroupId=${userGroupDTO.id}&shopId=${userGroupDTO.shopId}">该组下的角色</a>
            </td>
        </tr>
    </c:forEach>
    <% } %>
</table>
</body>
</html>