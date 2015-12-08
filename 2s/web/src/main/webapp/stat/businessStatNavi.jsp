<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<div class="titleWords">
    营业统计<br/>
</div>
    <bcgogo:permissionParam permissions="WEB.STAT.BUSINESS_STAT.BUSINESS.BASE,WEB.STAT.BUSINESS_STAT.RUNNING,WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.BASE" permissionKey="businessMenu">
        <c:if test="${businessMenuPermissionCounts>1}">
            <div class="titleList">
                <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BUSINESS.BASE">
                    <a class="<c:if test='${currPage==\"businessStat\"}'>click</c:if>" action-type="menu-click" menu-name="BUSINESS_STAT_GET_BUSINESS_STAT" id="first_cont">营业额</a>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.RUNNING">
                    <a class="<c:if test='${currPage==\"runningStat\"}'>click</c:if>" action-type="menu-click" menu-name="RUNNING_STAT_GET_RUNNING_STAT" id="runningStat">营业流水</a>
                </bcgogo:hasPermission>
                <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.MEMBER_CONSUME.BASE">
                    <a class="<c:if test='${currPage==\"memberStat\"}'>click</c:if>" action-type="menu-click" menu-name="MEMBER_MEMBER_STAT" id="memberStat">会员消费</a>
                </bcgogo:hasPermission>
                <a class="<c:if test='${currPage==\"couponConsumeStat\"}'>click</c:if>" <%--src="couponConsume.do?method=couponConsumeStat"--%> action-type="menu-click" menu-name="COUPON_CONSUME_STAT"<%--待修改--%> id="couponConsumeStat">代金券消费</a>

            </div>
        </c:if>
    </bcgogo:permissionParam>

<%--<div class="mainTitles clear">
    <div class="titleWords">
        <c:choose>
            <c:when test="${currPage==\"businessStat\"}">营业额</c:when>
            <c:when test='${currPage==\"runningStat\"}'>营业流水</c:when>
            <c:when test='${currPage==\"memberStat\"}'>会员消费</c:when>
            <c:otherwise>营业统计</c:otherwise>
        </c:choose>
    </div>
</div>--%>

