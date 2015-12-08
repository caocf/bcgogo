<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-10-14
  Time: 下午4:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html>
<head>
    <title>客户车辆资料列表</title>

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
<div><a href="user.do?method=addvehicle">新增客户车辆资料</a></div>

<form action="user.do?method=getvehiclebylicenceno" class="addgroup" method="post">
    <h3>根据条件查询客户车辆资料</h3>
    车牌号:<input type="text" name="licenceNo" value=""/>
    <div class="dd"><input type="submit" value="查找"/></div>
</form>

    <div class="dd"> </div>
    <table class="tablelist">
      <tr class="head">
        <td>车牌地区</td>
        <td>车牌号</td>
        <td>原厂编号</td>
        <td>车型</td>
        <td>车型年代</td>
        <td>车型配置</td>
        <td>备注</td>
        <td>操作</td>
      </tr>
      <%
        if (request.getAttribute("vehicleList") != null) {
    %>
      <c:forEach var="vehicleListlt" items="${vehicleList}" varStatus="status">
        <tr>
          <td><c:out value="${vehicleListlt.licenceAreaId}"></c:out>
          </td>
          <td><c:out value="${vehicleListlt.licenceNo}"></c:out>
          </td>
          <td><c:out value="${vehicleListlt.vin}"></c:out>
          </td>
          <td><c:out value="${vehicleListlt.model}"></c:out>
          </td>
          <td><c:out value="${vehicleListlt.year}"></c:out>
          </td>
          <td><c:out value="${vehicleListlt.trim}"></c:out>
          </td>
          <td><c:out value="${vehicleListlt.memo}"></c:out>
          </td>
          <td>
              <a href="user.do?method=getvehiclebyid&Id=${vehicleListlt.id}">修改</a> |
          </td>
        </tr>
      </c:forEach>
      <% } %>
    </table>
</body>
</html>