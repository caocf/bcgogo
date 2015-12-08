<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-9-23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>    <title>Simple jsp page</title>
	<style type="text/css">
	.addgroup
	{
		margin: 10px 0;
		line-height:1.4;
	}
	.addgroup h3
	{
		border-bottom:1px solid #DDD;
	}
	.tableGroup
	{
		width:100%;
		border-top:none;
		border-left:none;
		border-right:none;
	 	border-bottom:none;
		border-collapse: collapse;
		table-layout: fixed;
	}
	.tableGroup td
	{
		border-bottom:1px solid #DDD;
		padding-top:5px;
	}
	</style>
</head>

<body>

<form id="addgroup" method="post" action="" class="addgroup">
    <h3>Add Role</h3>
    <label>Role Name:</label><input type="text"  name="name"/>  <br>
    <label>Memo:</label><input type="text"  name="name"/>
<div><input type="submit" value="Submit" /></div>
</form>

<table class="tableGroup">
	<tr>
		<td>Role Name</td>
		<td>Description</td>
		<td>Operations</td>
	</tr>
	<tr>
		<td>Administrators</td>
		<td>A project role that represents administrators in a project</td>
		<td><a href="#">Delete</a> | <a href="#">Edit Members</a></td>
	</tr>
	<tr>
		<td>Developers</td>
		<td>3</td>
		<td><a href="#">View Usage</a> | <a href="#">Manage Default Members</a> | <a href="#">Edit Delete</a></td>  
	</tr>
</table>
</body>
</html>