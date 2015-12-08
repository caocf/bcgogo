<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<link rel="stylesheet" type="text/css" href="${ctx}/styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="${ctx}/styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="${ctx}/styles/bcgogo<%=ConfigController.getBuildVersion()%>.css"/>

<script type="text/javascript" src="${ctx}/js/extension/jquery/jquery-1.4.2.min.js"> </script>
<script type="text/javascript" src="${ctx}/js/extension/json2/json2.min.js"></script>
<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
