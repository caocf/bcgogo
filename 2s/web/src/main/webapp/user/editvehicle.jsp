<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-10-14
  Time: 下午4:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head><title>编辑客户车辆列表</title></head>
<body>
<form:form commandName="vehicleDTO" action="user.do?method=savevehicle" method="post">
    <table cellspacing="0" cellpadding="6" border="0">
        <tbody>
        <tr>
            <td align="left">店面ID:</td>
            <td align="left">
                <form:hidden path="id" value="${vehicleDTO.id}"></form:hidden>
                <form:input path="shopId" size="14" value="${vehicleDTO.shopId}"/>
            </td>
        </tr>
        <tr>
            <td align="left">车牌地区:</td>
            <td align="left">
                <form:input path="licenceAreaId" size="14" value="${vehicleDTO.licenceAreaId}"/>
            </td>
        </tr>
        <tr>
            <td align="left">车牌号:</td>
            <td align="left">
                <form:input path="licenceNo" size="14" value="${vehicleDTO.licenceNo}"/>
            </td>
        </tr>
        <tr>
            <td align="left">客户ID:</td>
            <td align="left">
                <form:input path="customerId" size="14" value="${vehicleDTO.customerId}"/>
            </td>
        </tr>
        <tr>
            <td align="left">客户:</td>
            <td align="left">
                <form:input path="customer" size="14" value="${vehicleDTO.customer}"/>
            </td>
        </tr>
        <tr>
            <td align="left">发动机号:</td>
            <td align="left">
                <form:input path="engineNo" size="14" value="${vehicleDTO.engineNo}"/>
            </td>
        </tr>
        <tr>
            <td align="left">原厂编号:</td>
            <td align="left">
                <form:input path="vin" size="14" value="${vehicleDTO.vin}"/>
            </td>
        </tr>
        <tr>
            <td align="left">车型ID:</td>
            <td align="left">
                <form:input path="carId" size="14" value="${vehicleDTO.carId}"/>
            </td>
        </tr>
        <tr>
            <td align="left">品牌:</td>
            <td align="left">
                <form:input path="brand" size="14" value="${vehicleDTO.brand}"/>
            </td>
        </tr>
        <tr>
            <td align="left">品牌厂商:</td>
            <td align="left">
                <form:input path="mfr" size="14" value="${vehicleDTO.mfr}"/>
            </td>
        </tr>
        <tr>
            <td align="left">车型:</td>
            <td align="left">
                <form:input path="model" size="14" value="${vehicleDTO.model}"/>
            </td>
        </tr>
        <tr>
            <td align="left">车型年代:</td>
            <td align="left">
                <form:input path="year" size="14" value="${vehicleDTO.year}"/>
            </td>
        </tr>
        <tr>
            <td align="left">车型配置:</td>
            <td align="left">
                <form:input path="trim" size="14" value="${vehicleDTO.trim}"/>
            </td>
        </tr>
        <tr>
            <td align="left">颜色:</td>
            <td align="left">
                <form:input path="color" size="14" value="${vehicleDTO.color}"/>
            </td>
        </tr>
        <tr>
            <td align="left">备注:</td>
            <td align="left">
                <form:input path="memo" size="14" value="${vehicleDTO.memo}"/>
            </td>
        </tr>
        <tr>
            <td align="center" colspan="2"><input type="submit" value="save"/><input type="reset" value="reset"/></td>
        </tr>
        </tbody>
    </table>
</form:form>
</body>
</html>