<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-10-11
  Time: 下午2:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html>
<head>
    <title>Shop</title>
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

        .dd {
            padding-top: 5px;
            padding-bottom: 5px;
            border-top: 1px solid #DDD;
        }

        .tablelist {
            width: 100%;
            border-top: none;
            border-left: none;
            border-right: none;
            border-bottom: none;
            border-collapse: collapse;
            table-layout: fixed;
        }

        .tablelist .head td {
            border-bottom: 1px solid #666666;
        }

        .tablelist  td {
            padding-top: 5px;
            padding-bottom: 5px;
            border-bottom: 1px solid #DDD;
        }
    </style>
</head>
<body><!--shoplist
<a href="user.do?method=selectgroupbyshopid">link</a><br>-->
<!--<div><a href="shop.do?method=saveshop">新增店面</a></div>-->
<form action="" class="addgroup">
    <table class="tablelist">
        <tr class="head">
            <td>店面名称</td>
            <td>法人代表</td>
            <td>店面编号</td>
            <td>地址</td>
            <td>邮政编码</td>
            <td>固定电话</td>
            <td>状态</td>
            <td>备注</td>
            <td>操作</td>
        </tr>
        <%
            if (request.getAttribute("shopList") != null) {
        %>
        <c:forEach var="shoplt" items="${shopList}" varStatus="status">
            <tr>
                <td><c:out value="${shoplt.name}"></c:out>
                </td>
                <td><c:out value="${shoplt.legalRep}"></c:out>
                </td+>
                <td><c:out value="${shoplt.no}"></c:out>
                </td>
                <td><c:out value="${shoplt.address}"></c:out>
                </td>
                <td><c:out value="${shoplt.zip}"></c:out>
                </td>
                <td><c:out value="${shoplt.landline}"></c:out>
                </td>
                <td>
                    <c:if test="${shoplt.state == null}">待审核</c:if>
                    <c:if test="${shoplt.state == 1}">已审核</c:if>
                </td>
                <td><c:out value="${shoplt.memo}"></c:out>
                </td>
                <td>
                    <c:if test="${shoplt.state == null}"><a
                            href="shop.do?method=activateshop&shopId=${shoplt.id}">激活</a></c:if>
                </td>
            </tr>
        </c:forEach>
        <% } %>
    </table>
</body>
</html>