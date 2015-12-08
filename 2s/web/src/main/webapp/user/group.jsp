<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-9-23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>
    <title>保存用户组信息</title>
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

<form:form action="user.do?method=savegroup" commandName="userGroupDTO">
    <form:hidden path="shopId" value="${shopId}"></form:hidden>
    <form:hidden path="id" value="${userGroupDTO.id}"></form:hidden>
    组名：<form:input path="name" /><br>
    备注：<form:input path="memo" /><br>
    <input type="submit" value="保存"/>
</form:form>


</body>
</html>