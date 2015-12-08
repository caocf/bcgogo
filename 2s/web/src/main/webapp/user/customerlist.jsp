<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-10-14
  Time: 下午2:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html>
<head>
    <title>客户资料列表</title>
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
    <div><a href="user.do?method=addcustomer&shopId=${shopId}">新增客户</a></div>
    <div class="dd"> </div>
    <table class="tablelist">
      <tr class="head">
        <td>客户姓名</td>
        <td>单位</td>
        <td>身份证号码</td>
        <td>性别</td>
        <td>生日</td>
        <td>固定电话</td>
        <td>备注</td>
        <td>操作</td>
      </tr>
      <%
        if (request.getAttribute("customerList") != null) {
    %>
      <c:forEach var="customerlt" items="${customerList}" varStatus="status">
        <tr>
          <td><c:out value="${customerlt.name}"></c:out>
          </td>
          <td><c:out value="${customerlt.company}"></c:out>
          </td>
          <td><c:out value="${customerlt.identifierNo}"></c:out>
          </td>
          <td><c:out value="${customerlt.gender}"></c:out>
          </td>
          <td><c:out value="${customerlt.birthday}"></c:out>
          </td>
          <td><c:out value="${customerlt.landLine}"></c:out>
          </td>
          <td><c:out value="${customerlt.memo}"></c:out>
          </td>
          <td>
              <a href="user.do?method=getcustomerbyid&Id=${customerlt.id}&shopId=${shopId}">修改</a> |
          </td>
        </tr>
      </c:forEach>
      <% } %>
    </table>
</body>
</html>