<%@ page import="com.bcgogo.common.WebUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>" />
<div class="cusTitle">
    <c:choose>
        <c:when test="${currPage==\"vehicleManage\"}">车辆管理</c:when>
        <c:when test="${currPage==\"customerData\"}">客户查询</c:when>
        <c:when test='${currPage==\"smsManage\"}'>短信管理</c:when>
        <c:when test='${currPage==\"smsRecharge\"}'>短信充值</c:when>
        <c:when test='${currPage==\"cardManage\"}'>会员套餐管理</c:when>
        <c:when test='${currPage==\"cardAdd\"}'>会员套餐</c:when>
        <c:when test='${currPage==\"vehicleServeStat\"}'>车型统计</c:when>
        <c:otherwise>客户管理</c:otherwise>
    </c:choose>
</div>

