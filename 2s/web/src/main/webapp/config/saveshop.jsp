<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-10-11
  Time: 下午5:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html>
<head><title>保存店面信息</title></head>
<body>
<form:form commandName="shopDTO" action="shop.do?method=saveshop">
    <table>
        <tr>
            <td>店面名称</td>
            <td><form:input path="name" value="${shopDTO.name}"/>
                <form:hidden path="id" value="${shopDTO.id}"></form:hidden></td>
        </tr>
        <tr>
            <td>法人代表</td>
            <td><form:input path="legalRep" value="${shopDTO.legalRep}"/></td>
        </tr>
        <tr>
            <td>店面编号</td>
            <td><form:input path="no" value="${shopDTO.no}"/></td>
        </tr>
        <tr>
            <td>地区ID</td>
            <td><form:input path="areaId" value="${shopDTO.areaId}"/></td>
        </tr>
        <tr>
            <td>地址</td>
            <td><form:input path="address" value="${shopDTO.address}"/></td>
        </tr>
        <tr>
            <td>邮政编码</td>
            <td><form:input path="zip" value="${shopDTO.zip}"/></td>
        </tr>
        <tr>
            <td>联系人</td>
            <td><form:input path="contact" value="${shopDTO.contact}"/></td>
        </tr>
        <tr>
            <td>固定电话</td>
            <td><form:input path="landline" value="${shopDTO.landline}"/></td>
        </tr>
        <tr>
            <td>手机</td>
            <td><form:input path="mobile" value="${shopDTO.mobile}"/></td>
        </tr>
        <tr>
            <td>传真号码</td>
            <td><form:input path="fax" value="${shopDTO.fax}"/></td>
        </tr>
        <tr>
            <td>电子邮件</td>
            <td><form:input path="email" value="${shopDTO.email}"/></td>
        </tr>
        <tr>
            <td>QQ</td>
            <td><form:input path="qq" value="${shopDTO.qq}"/></td>
        </tr>
        <tr>
            <td>开户银行</td>
            <td><form:input path="bank" value="${shopDTO.bank}"/></td>
        </tr>
        <tr>
            <td>账号</td>
            <td><form:input path="account" value="${shopDTO.account}"/></td>
        </tr>
        <tr>
            <td>店铺类型</td>
            <td><form:input path="categoryId" value="${shopDTO.categoryId}"/></td>
        </tr>
        <tr>
            <td>银行卡户名</td>
            <td><form:input path="account_name" value="${shopDTO.account_name}"/></td>
        </tr>
        <tr>
            <td>审核人</td>
            <td><form:input path="reviewer" value="${shopDTO.reviewer}"/></td>
        </tr>
        <tr>
            <td>核验时间</td>
            <td><form:input path="reveiwDate" value="${shopDTO.reveiwDate}"/></td>
        </tr>
        <tr>
            <td>业务员</td>
            <td><form:input path="agent" value="${shopDTO.agent}"/></td>
        </tr>
        <tr>
            <td>状态</td>
            <td><form:input path="state" value="${shopDTO.state}"/></td>
        </tr>
        <tr>
            <td>备注</td>
            <td><form:input path="memo" value="${shopDTO.memo}"/></td>
        </tr>
    </table>
    <div><input type="submit" value="保存"/></div>
</form:form>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>