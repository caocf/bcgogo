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
<head><title>保存客户资料</title></head>
<body>
<form:form commandName="customerDTO" action="user.do?method=savecustomer" method="post">
    <table cellspacing="0" cellpadding="6" border="0">
        <tbody>
        <tr>
            <td align="left">姓名:</td>
            <td align="left">
                <form:hidden path="id" value="${customerDTO.id}"></form:hidden>
                <form:hidden path="shopId" size="14" value="${customerDTO.shopId}"/>
                <form:input path="name" size="14" value="${customerDTO.name}"/>
            </td>
        </tr>
        <tr>
            <td align="left">单位:</td>
            <td align="left">
                <form:input path="company" size="14" value="${customerDTO.company}"/>
            </td>
        </tr>
        <tr>
            <td align="left">身份证号:</td>
            <td align="left">
                <form:input path="identifierNo" size="14" value="${customerDTO.identifierNo}"/>
            </td>
        </tr>
        <tr>
            <td align="left">性别:</td>
            <td align="left">
                <form:input path="gender" size="14" value="${customerDTO.gender}"/>
            </td>
        </tr>
        <tr>
            <td align="left">生日:</td>
            <td align="left">
                <form:input path="birthday" size="14" value="${customerDTO.birthday}"/>
            </td>
        </tr>
        <tr>
            <td align="left">手机:</td>
            <td align="left">
                <form:input path="mobile" size="14" value="${customerDTO.mobile}"/>
            </td>
        </tr>
        <tr>
            <td align="left">固定电话:</td>
            <td align="left">
                <form:input path="landLine" size="14" value="${customerDTO.landLine}"/>
            </td>
        </tr>
        <tr>
            <td align="left">传真:</td>
            <td align="left">
                <form:input path="fax" size="14" value="${customerDTO.fax}"/>
            </td>
        </tr>
        <tr>
            <td align="left">电子邮件:</td>
            <td align="left">
                <form:input path="email" size="14" value="${customerDTO.email}"/>
            </td>
        </tr>
        <tr>
            <td align="left">邮编:</td>
            <td align="left">
                <form:input path="zip" size="14" value="${customerDTO.zip}"/>
            </td>
        </tr>
        <tr>
            <td align="left">地址:</td>
            <td align="left">
                <form:input path="address" size="14" value="${customerDTO.address}"/>
            </td>
        </tr>
        <tr>
            <td align="left">QQ:</td>
            <td align="left">
                <form:input path="qq" size="14" value="${customerDTO.qq}"/>
            </td>
        </tr>
        <tr>
            <td align="left">开户银行:</td>
            <td align="left">
                <form:input path="bank" size="14" value="${customerDTO.bank}"/>
            </td>
        </tr>
        <tr>
            <td align="left">银行账号:</td>
            <td align="left">
                <form:input path="account" size="14" value="${customerDTO.account}"/>
            </td>
        </tr>
        <tr>
            <td align="left">税号:</td>
            <td align="left">
                <form:input path="taxNo" size="14" value="${customerDTO.taxNo}"/>
            </td>
        </tr>
        <tr>
            <td align="left">开票地址:</td>
            <td align="left">
                <form:input path="billingAddress" size="14" value="${customerDTO.billingAddress}"/>
            </td>
        </tr>
        <tr>
            <td align="left">发票类别:</td>
            <td align="left">
                <form:input path="invoiceCategory" size="14" value="${customerDTO.invoiceCategory}"/>
            </td>
        </tr>
        <tr>
            <td align="left">结算方式:</td>
            <td align="left">
                <form:input path="settlementType" size="14" value="${customerDTO.settlementType}"/>
            </td>
        </tr>
        <tr>
            <td align="left">发票抬头:</td>
            <td align="left">
                <form:input path="invoiceTitle" size="14" value="${customerDTO.invoiceTitle}"/>
            </td>
        </tr>
        <tr>
            <td align="left">部门:</td>
            <td align="left">
                <form:input path="dept" size="14" value="${customerDTO.dept}"/>
            </td>
        </tr>
        <tr>
            <td align="left">客户专员:</td>
            <td align="left">
                <form:input path="agent" size="14" value="${customerDTO.agent}"/>
            </td>
        </tr>
        <tr>
            <td align="left">备注:</td>
            <td align="left">
                <form:input path="memo" size="14" value="${customerDTO.memo}"/>
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