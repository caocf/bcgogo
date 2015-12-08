<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<div class="mainTitles">
    <div class="titleWords">
        <%--<c:choose>
            <c:when test="${currPage==\"messageSwitch\"}">功能配置</c:when>
            <c:when test="${currPage==\"storehouseManager\"}">仓库管理</c:when>
            <c:when test="${currPage==\"pageCustomizerConfig\"}">页面配置</c:when>
            <c:otherwise>自定义配置</c:otherwise>
        </c:choose>--%>
        自定义配置
    </div>
    <div class="titleList">
        <bcgogo:permissionParam permissions="WEB.SYSTEM_SETTINGS.FUNCTIONAL_CONFIG,WEB.SYSTEM_SETTINGS.STORE_MANAGE,WEB.SYSTEM_SETTINGS.CUSTOM_CONFIG.PAGE.CONFIG"
                                permissionKey="functionalConfigMenu">
            <c:if test="${functionalConfigMenuPermissionCounts>1}">
                <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.FUNCTIONAL_CONFIG">
                    <a class="<c:if test='${currPage==\"messageSwitch\"}'>click</c:if>" action-type="menu-click"
                       menu-name="WEB.SYSTEM_SETTINGS.FUNCTIONAL_CONFIG"
                       href="admin.do?method=messageSwitch">功能配置</a>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.STORE_MANAGE">
                    <a class="<c:if test='${currPage==\"storehouseManager\"}'>click</c:if>" action-type="menu-click"
                       menu-name="WEB.SYSTEM_SETTINGS.STORE_MANAGE" href="storehouse.do?method=storehouseManager">仓库管理</a>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.CUSTOM_CONFIG.PAGE.CONFIG">
                    <bcgogo:hasConfig configValue="CUSTOMIZER_CONFIG" expectResult="ON">
                        <a class="<c:if test='${currPage==\"pageCustomizerConfig\"}'>click</c:if>" action-type="menu-click"
                           menu-name="WEB_SYSTEM_SETTINGS_CUSTOM_CONFIG_PAGE_CONFIG" href="/web/pageCustomizerConfig.do?method=show">页面配置</a>
                    </bcgogo:hasConfig>
                </bcgogo:hasPermission>
            </c:if>
        </bcgogo:permissionParam>
    </div>

</div>