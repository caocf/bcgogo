<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-9-23
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

    </style>

</head>

<body>
<%--	<div style="float:left;">
		<div>
			<div>Group</div>
			<select style="width:330px;" multiple="multiple" size="5">
                <%
                if (request.getAttribute("userGroupList") != null)
                {
                %>
                    <c:forEach var="userGroup" items="${userGroupList}" varStatus="status">
                       <option value="${userGroup.Id}">
                                  <c:out value="${userGroup.name}"></c:out>
                       </option>
                    </c:forEach>
                <% } %>
			</select>
		</div>
		<div> </div>
	</div>

	<div style="float:left;">
		<div>
		  <div><label style=" margin-left:50px;"></label>Group Member</div>
			<select style="width:330px; margin-left:50px;" multiple="multiple" size="5">
                <%
                if (request.getAttribute("userList") != null)
                {
                %>
                    <c:forEach var="userGroup" items="${userGroupList}" varStatus="status">
                       <option value="${userGroup.Id}">
                                  <c:out value="${userGroup.name}"></c:out>
                       </option>
                    </c:forEach>
                <% } %>
			</select>
		</div>
		<div><input type="button"  style=" margin-left:50px;" value="Leave <<"/></div>
	</div>

	<div>
		<div>
			<div><label style=" margin-left:50px;">Add members to selected group(s)</label></div>
			<select style="width:330px; margin-left:50px;" multiple="multiple" size="5">
			</select><a id="a_click" href="void(0);">add user</a>
		</div>
		<div><input type="button"  style=" margin-left:50px;" value="Join <<"/></div>
	</div>
--%>

<form action="user.do?method=saveusergroup" method="post">
    userId：<input name="userId"/> <br>
    userGroupId：<input name="userGroupId"/> <br>
    <input type="submit" value="Add"/>
</form>

</body>
</html>