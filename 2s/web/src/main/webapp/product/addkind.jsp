<%--
  Created by IntelliJ IDEA.
  User: wjl
  Date: 11-9-30
  Time: 上午9:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.bcgogo.product.dto.KindDTO" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head><title>Simple jsp page</title>
    <% KindDTO kindDTO = (KindDTO) request.getAttribute("command");
        String flag = (String) request.getAttribute("flag");
    %>
</head>
<body>
<table style="margin: 0 auto;">
    <%if ("savefailure".equals(flag)) {%>
    <label style="font-size: 30px; color:#ff3333;">SAVE FAILURE</label>
    <%} else if ("savesuccess".equals(flag)) {%>
    <tr>
        <td><label>获取产品种类--Kind</label></td>
    </tr>
    <tr>
        <td>
            <hr/>
        </td>
    </tr>
    <tr>
        <td><label>产品分类ID</label></td>
        <td><%=kindDTO.getCategoryId() %>
        </td>
    </tr>
    <tr>
        <td><label>产品种类名称</label></td>
        <td><%=kindDTO.getName() %>
        </td>
    </tr>
    <tr>
        <td><label>产品种类英文名</label></td>
        <td><%=kindDTO.getNameEn()%>
        </td>
    </tr>
    <tr>
        <td><label>产品状态</label></td>
        <td><%=kindDTO.getState() %>
        </td>
    </tr>
    <tr>
        <td><label>商店ID</label></td>
        <td><%=kindDTO.getShopId() %>
        </td>
    </tr>
    <tr>
        <td><label>备注</label></td>
        <td><%=kindDTO.getMemo()%>
        </td>
    </tr>
    <%} else {%>
    <form:form method="post" name="thisform" action="product.do?method=addkind" commandName="command">
        <tr>
            <td><label>增加产品种类--Kind</label></td>
        </tr>
        <tr>
            <td>
                <hr/>
            </td>
        </tr>
        <tr>
            <td><label>商店ID</label></td>
            <td><form:input path="shopId"/></td>
        </tr>
        <tr>
            <td><label>产品分类ID</label></td>
            <td><form:input path="categoryId"/></td>
        </tr>
        <tr>
            <td><label>产品种类名称</label></td>
            <td><form:input path="name"/></td>
        </tr>
        <tr>
            <td><label>产品种类英文名</label></td>
            <td><form:input path="nameEn"/></td>
        </tr>
        <tr>
            <td><label>产品状态</label></td>
            <td><form:input path="state"/></td>
        </tr>
        <tr>
            <td><label>备注</label></td>
            <td><form:textarea path="memo"/></td>
        </tr>
        <tr>
            <td><input type="submit" value="submit"/></td>
            <td><input type="reset" value="reset"/></td>
        </tr>
    </form:form>
    <%}%>
</table>
</body>
</html>