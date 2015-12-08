<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-9-29
  Time: 下午2:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>

<head>
    <title>Simple jsp page</title>
</head>

<body>
<form action="user.do?method=saverolegroup" method="post">
    Role Id<input type="text" name="roleId"/><br>
    userGroupId<input type="text" name="userGroupId"/><br>
    <input type="submit" value="Add"/><br>
</form>
</body>

</html>