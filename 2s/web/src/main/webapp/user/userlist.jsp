<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-9-28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>

<head>
    <title>用户信息增删改查</title>

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
            padding-top: 5px;
            padding-bottom: 10px;
        }
		.dd{
		padding-top:5px;
		padding-bottom:5px;
        border-top: 1px solid #DDD;
		}
		.tablelist{
            width: 100%;
            border-top: none;
            border-left: none;
            border-right: none;
            border-bottom: none;
            border-collapse: collapse;
            table-layout: fixed;
		}
		.tablelist .head td{
        border-bottom: 1px solid #666666;
		}
		.tablelist  td{
            padding-top: 5px;
            padding-bottom: 5px;
        border-bottom: 1px solid #DDD;
		}
    </style>
</head>

<body>
<div><a href="user.do?method=add&shopId=${shopId}">新增用户</a></div>
<div class="dd"> </div>
<table class="tableGroup">
	<tr>
		<td>店面编号</td>
		<td>用户编号</td>
		<td>用户名</td>
		<td>邮件地址</td>
		<td>手机号</td>
		<td>备注</td>
		<td>操作</td>
	</tr>
    <%
    if (request.getAttribute("userList") != null)
    {
    %>
     <c:forEach var="userlt" items="${userList}" varStatus="status">
	<tr>
		<td><c:out value="${userlt.shopId}"/></td>
		<td><c:out value="${userlt.userNo}"/></td>
		<td><c:out value="${userlt.userName}"/></td>
		<td><c:out value="${userlt.email}"/></td>
		<td><c:out value="${userlt.mobile}"/></td>
		<td><c:out value="${userlt.memo}"/></td>
		<td>
                <a href="user.do?method=getuserbyuserid&userId=${userlt.id}&shopId=${shopId}">修改</a> |
                <a href="user.do?method=getusergroupbyuserid&userId=${userlt.id}&shopId=${shopId}">所属用户组</a>
        </td>
	</tr>
    </c:forEach>
    <% } %>
</table>

</body>

</html>