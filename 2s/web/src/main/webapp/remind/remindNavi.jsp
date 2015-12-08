<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript">
    $(function () {
        $(".J_todoRemindNaviAmount").click(function() {
            window.location.href = "remind.do?method=newtodo";
        });
        $(".J_todoPlansRemindAmount").click(function() {
            window.location.href = "remind.do?method=toPlansRemind";
        });
        $(".J_todoAppointAmount").click(function() {
            window.location.href = "appoint.do?method=showAppointOrderList";
        });
        //代办事项导航栏提醒数字
        APP_BCGOGO.Net.asyncGet({
            url: "remind.do?method=getRemindNaviTitlePromptNums",
            data: {
                "now": new Date().getTime()
            },
            dataType: "json",
            success: function (result) {
                if (!result || !result.success || result.data == null) {
                    return;
                }
                var data = result.data;
                var $J_remind_navi_menu = $(".J_remind_navi_menu");
                for (var p in data) {
                    if (data[p] > 0) {
                        var css = ".J_" + p;
                        $J_remind_navi_menu.find(css).text(data[p]).show();
                    }
                }
            },
            error: function () {
                GLOBAL.error("remind.do?method=getTitlePromptNums error");
            }
        });
    })
</script>
<c:set var="currPage" value='<%=request.getParameter("currPage")%>'/>
<div class="titleList J_remind_navi_menu">
    <ul>
        <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_TODO.BASE" resourceType="menu">
            <li>
                <div class="nav-relative">
                    <div class="nav-num J_todoRemindNaviAmount"></div>
                </div>
                <a class="<c:if test='${currPage==\"newToDoNaviMenu\"}'>click</c:if>" href="remind.do?method=newtodo">待办首页</a>
            </li>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.VERSION.FOUR_S_VERSION_BASE_MENU" resourceType="menu">
            <li>
                <div class="nav-relative">
                    <div class="nav-num J_faultSearch"></div>
                </div>
                <a class="<c:if test='${currPage==\"mileageSearchMenu\"}'>click</c:if>"
                   href="shopMileageInfo.do?method=showShopMileageInfoList">里程查询</a>
            </li>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.VERSION.FOUR_S_VERSION_BASE_MENU" resourceType="menu">
            <li>
                <div class="nav-relative">
                    <div class="nav-num J_faultSearch"></div>
                </div>
                <a class="<c:if test='${currPage==\"impactSearchMenu\"}'>click</c:if>"
                   href="shopImpactInfo.do?method=showShopImpactInfoList">碰撞查询</a>
            </li>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.VERSION.FOUR_S_VERSION_BASE_MENU" resourceType="menu">
            <li>
                <div class="nav-relative">
                    <div class="nav-num J_faultSearch"></div>
                </div>
                <a class="<c:if test='${currPage==\"faultSearchMenu\"}'>click</c:if>"
                   href="shopFaultInfo.do?method=showShopFaultInfoList">故障查询</a>
            </li>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.VERSION.FOUR_S_VERSION_BASE_MENU" resourceType="menu">
            <li>
                <div class="nav-relative">
                    <div class="nav-num J_faultSearch"></div>
                </div>
                <a class="<c:if test='${currPage==\"talkSearchMenu\"}'>click</c:if>"
                   href="shopTalkMessageInfo.do?method=showShopTalkMessageInfoList">互动查询</a>
            </li>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.VERSION.FOUR_S_VERSION_BASE_MENU" resourceType="menu">
            <li>
                <div class="nav-relative">
                    <div class="nav-num J_faultSearch"></div>
                </div>
                <a class="<c:if test='${currPage==\"rescueSearchMenu\"}'>click</c:if>"
                   href="shopSosInfo.do?method=showShopSosInfoList">救援查询</a>
            </li>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.APPOINT_ORDER_LIST" resourceType="menu">
            <li>
                <div class="nav-relative">
                    <div class="nav-num J_todoAppointAmount"></div>
                </div>
                <a class="<c:if test='${currPage==\"appointNaviMenu\"}'>click</c:if>"
                   href="appoint.do?method=showAppointOrderList">预约服务</a>
            </li>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.VEHICLE_APPUSER_COMMENTRECORD_LIST" resourceType="menu">
            <li>
                <a class="<c:if test='${currPage==\"commentNaviMenu\"}'>click</c:if>"
                   href="supplier.do?method=showAppShopCommentList">评价中心</a>
            </li>
        </bcgogo:hasPermission>

        <bcgogo:hasPermission permissions="WEB.SCHEDULE.CUSTOM_REMIND.BASE" resourceType="menu">
            <li>
                <div class="nav-relative">
                    <div class="nav-num J_todoPlansRemindAmount"></div>
                </div>
                <a class="<c:if test='${currPage==\"plansRemindNaviMenu\"}'>click</c:if>"
                   href="remind.do?method=toPlansRemind">自定义提醒</a>
            </li>
        </bcgogo:hasPermission>


        <%--        <bcgogo:hasPermission permissions="WEB.SYSTEM_SETTINGS.SHOP_BASIC_COMMENT" resourceType="menu">--%>

        <%--        </bcgogo:hasPermission>--%>
        <%--<bcgogo:hasPermission permissions="//todo 询价比价" resourceType="menu">--%>
        <%--<li >--%>
        <%--<div class="nav-relative">--%>
        <%--<div class="nav-num J_todoAppointAmount"></div>--%>
        <%--</div>--%>
        <%--<a class="<c:if test='${currPage==\"appointNaviMenu\"}'>click</c:if>" href="appoint.do?method=showAppointOrderList">预约服务</a>--%>
        <%--</li>--%>
        <%--</bcgogo:hasPermission>--%>
    </ul>
</div>
