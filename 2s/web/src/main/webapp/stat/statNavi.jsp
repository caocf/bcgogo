<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>

        <%--<div class="titleWords">
            <br/>
        </div>--%>
    <%--<div class="titleWords">财务统计</div>
    <div class="i_mainTitle stock_search">
        <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.BASE">
            <a id="carWash" href="navigator.do?method=businessStat" class="<c:if test='${currPage==\"businessStat\"}'>title_hover</c:if>">营业统计</a>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.STAT.NONOPERATING_ACCOUNT.BASE">
            <a class="<c:if test='${currPage==\"businessAccountSearch\"}'>title_hover</c:if>" id="businessAccount" href="businessAccount.do?method=initBusinessAccountSearch">营业外记账</a>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_ANALYSIS.BASE">
            <a class="<c:if test='${currPage==\"businessAnalysis\"}'>title_hover</c:if>" id="businessAnalysis" href="itemStat.do?method=getItemStat&type=customerStat">营业分析</a>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.STAT.RECEIVABLE_PAYABLE_STAT.BASE">
            <a id="recOrPayStat" href="navigator.do?method=arrearsStat" class="<c:if test='${currPage==\"toReceivableStat\"}'>title_hover</c:if>">应付应收统计</a>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.STAT.PURCHASE_ANALYST.BASE">
            <a id="costStat" href="navigator.do?method=costStat" class="<c:if test='${currPage==\"costStat\"}'>title_hover</c:if>">采购分析</a>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.STAT.AGENT_ACHIEVEMENTS.BASE">
            <a id="carWash2" href="bizstat.do?method=agentAchievements&month=thisMonth" class="<c:if test='${currPage==\"agentAchievements\"}'>title_hover</c:if>">员工业绩统计</a>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.STAT.GOOD_BAD_STAT.BASE">
            <a id="goodBadSaleStat" href="navigator.do?method=salesStat" class="<c:if test='${currPage==\"goodSaleCost\"}'>title_hover</c:if>">畅销/滞销品统计</a>
        </bcgogo:hasPermission>
        <a id="carWash3" href="#" style="color:#cccccc;" onclick="notOpen()">客/供应商户统计</a>
    </div>--%>
<%--</div>--%>
<%--<div class="clear"></div>--%>

