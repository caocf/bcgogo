<%@ page import="com.bcgogo.service.ServiceManager" %>
<%@ page import="com.bcgogo.config.service.IShopConfigService" %>
<%@ page import="com.bcgogo.common.WebUtil" %>
<%@ page import="com.bcgogo.enums.user.UserGroupType" %>
<%@ page import="com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils" %>
<%@ page import="com.bcgogo.user.service.IUserService" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>

<%
    response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    response.setDateHeader("Expires", 0); //prevents caching at the proxy server
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
  boolean isRepairPickingSwitchOn = ServiceManager.getService(IUserService.class).isRepairPickingSwitchOn(WebUtil.getShopId(request));//维修领料开关
%>




