<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-10-10
  Time: 上午11:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html>
<head>
    <title>Simple jsp page</title>
</head>

<body>
    <form action="user.do?method=updategroup&shopId=${shopId}&userGroupId=${userGroupId}" method="post">
       GroupName:<input type="text" name="name" value="${userGroupDTO.name}"/><br>
       Memo:<input type="text" name="memo" value="${userGroupDTO.memo}"/>  <br>
        <input type="submit" value="update">
    </form>
</body>

</html>