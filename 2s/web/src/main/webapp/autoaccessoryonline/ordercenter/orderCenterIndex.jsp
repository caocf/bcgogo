<%@ page import="com.bcgogo.enums.OrderStatus" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page import="com.bcgogo.utils.DateUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>汽配在线——订单中心</title>
<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addPlan<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/accept<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/orderCenter<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE_ORDER_CENTER");
    </script>
</head>
<%
    //今天的时间点
    String todayTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", System.currentTimeMillis());
    //昨天的时间点
    String yestodayTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd",System.currentTimeMillis()-24*3600*1000);
    //销售单
    Long sale_today_new = (Long)request.getAttribute("sale_today_new");
    Long sale_early_new = (Long)request.getAttribute("sale_early_new");
    Long sale_new = (Long)request.getAttribute("sale_new");
    Long sale_stocking = (Long)request.getAttribute("sale_stocking");
    Long sale_dispatch = (Long)request.getAttribute("sale_dispatch");
    Long sale_sale_debt_done = (Long)request.getAttribute("sale_sale_debt_done");
    Long sale_in_progress = (Long)request.getAttribute("sale_in_progress");
    //销售退货单
    Long sale_return_today_new = (Long)request.getAttribute("sale_return_today_new");
    Long sale_return_early_new = (Long)request.getAttribute("sale_return_early_new");
    Long sale_return_new = (Long)request.getAttribute("sale_return_new");
    Long sale_return_in_progress = (Long)request.getAttribute("sale_return_in_progress");
    //采购单
    Long purchase_today_new = (Long)request.getAttribute("purchase_today_new");
    Long purchase_early_new = (Long)request.getAttribute("purchase_early_new");
    Long purchase_new = (Long)request.getAttribute("purchase_new");
    Long purchase_seller_stock = (Long)request.getAttribute("purchase_seller_stock");
    Long purchase_seller_dispatch = (Long)request.getAttribute("purchase_seller_dispatch");
    Long purchase_seller_refused = (Long)request.getAttribute("purchase_seller_refused");
    Long purchase_seller_stop = (Long)request.getAttribute("purchase_seller_stop");
    Long purchase_in_progress = (Long)request.getAttribute("purchase_in_progress");
    Long purchase_today_done = (Long)request.getAttribute("purchase_today_done");
    Long purchase_early_done = (Long)request.getAttribute("purchase_early_done");
    Long purchase_done = (Long)request.getAttribute("purchase_done");
    //入库退货单
    Long purchase_return_today_new = (Long)request.getAttribute("purchase_return_today_new");
    Long purchase_return_early_new = (Long)request.getAttribute("purchase_return_early_new");
    Long purchase_return_new = (Long)request.getAttribute("purchase_return_new");
    Long purchase_return_seller_accept = (Long)request.getAttribute("purchase_return_seller_accept");
    Long purchase_return_seller_refused = (Long)request.getAttribute("purchase_return_seller_refused");
    Long purchase_return_in_progress = (Long)request.getAttribute("purchase_return_in_progress");
%>
<body class="bodyMain">

<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <%--<jsp:include page="todoOrderNavi.jsp">--%>
        <%--<jsp:param name="currPage" value="null"/>--%>
    <%--</jsp:include>--%>
    <div class="clear i_height"></div>
    <div class="titBody">
    <jsp:include page="../supplyCenterLeftNavi.jsp">
        <jsp:param name="currPage" value="orderCenterIndex"/>
    </jsp:include>
    <div class="bodyLeft">
        <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_ORDERS.SALE">
        <div class="orders">
            <div class="title_blue">在线销售订单<a class="all" href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS">查看所有</a></div>
            <div class="order_titles">
                <div class="order_list">
                    <span class="list_top"></span>
                    <div class="list_body">
                        <div class="title"><span class="yellow_color">新订单</span><span class="list_all">共有
                            <a class="blue_color number" <c:if test="<%=sale_new<=0%>">onclick="javascript:return false;"</c:if>
                                href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&currTab=NEW&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>"><%=sale_new%></a>条</span>
                        </div>
                        <div class="listMsg">
                            <a class="blue_color" <c:if test="<%=sale_today_new<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&currTab=NEW&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>&startTimeStr=<%=todayTimeStr%>">
                                今日新增：<span class="yellow_color"><%=sale_today_new%></span>条</a>
                            <a class="blue_color" <c:if test="<%=sale_early_new<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&currTab=NEW&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>&endTimeStr=<%=yestodayTimeStr%>">
                                往日新增：<span class="yellow_color"><%=sale_early_new%></span>条</a>
                        </div>
                    </div>
                    <span class="list_bottom"></span>
                </div>
                <div class="order_list">
                    <span class="list_top"></span>
                    <div class="list_body">
                        <div class="title"><span class="yellow_color">处理中订单</span><span class="list_all">共有
                            <a class="blue_color number" <c:if test="<%=sale_in_progress<=0%>">onclick="javascript:return false;"</c:if>
                                href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=inProgress"><%=sale_in_progress%></a>条</span></div>
                        <div class="listMsg">
                            <a class="blue_color" <c:if test="<%=sale_stocking<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=<%=OrderStatus.STOCKING.toString()%>">
                                备货中待发货：<span class="yellow_color"><%=sale_stocking%></span>条</a>
                            <a class="blue_color" <c:if test="<%=sale_dispatch<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=<%=OrderStatus.DISPATCH.toString()%>">
                                已发货待结算：<span class="yellow_color"><%=sale_dispatch%></span>条</a>
                            <a class="blue_color" <c:if test="<%=sale_sale_debt_done<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&orderStatus=<%=OrderStatus.SALE_DEBT_DONE.toString()%>">
                                欠款结算：<span class="yellow_color"><%=sale_sale_debt_done%></span>条</a>
                        </div>
                    </div>
                    <span class="list_bottom"></span>
                </div>
            </div>
        </div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_ORDERS.SALE_RETURN">
        <div class="orders">
            <div class="title_blue title_yellow">在线销售退货单<a class="all" href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_RETURN_ORDERS">查看所有</a></div>
            <div class="order_titles">
                <div class="order_list">
                    <span class="list_top"></span>
                    <div class="list_body">
                        <div class="title"><span class="yellow_color">新退货单</span><span class="list_all">共有
                            <a class="blue_color number" <c:if test="<%=sale_return_new<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_RETURN_ORDERS&orderStatus=<%=OrderStatus.PENDING.toString()%>"><%=sale_return_new%></a>条</span></div>
                        <div class="listMsg">
                            <a class="blue_color" <c:if test="<%=sale_return_today_new<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_RETURN_ORDERS&orderStatus=<%=OrderStatus.PENDING.toString()%>&startTimeStr=<%=todayTimeStr%>">
                                今日新增：<span class="yellow_color"><%=sale_return_today_new%></span>条</a>
                            <a class="blue_color" <c:if test="<%=sale_return_early_new<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_RETURN_ORDERS&orderStatus=<%=OrderStatus.PENDING.toString()%>&endTimeStr=<%=yestodayTimeStr%>">
                                往日新增：<span class="yellow_color"><%=sale_return_early_new%></span>条</a>
                        </div>
                    </div>
                    <span class="list_bottom"></span>
                </div>
                <div class="order_list">
                    <span class="list_top"></span>
                    <div class="list_body">
                        <div class="title"><span class="yellow_color">处理中退货单</span><span class="list_all">共有
                            <a class="blue_color number" <c:if test="<%=sale_return_in_progress<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_RETURN_ORDERS&orderStatus=<%=OrderStatus.WAITING_STORAGE.toString()%>"><%=sale_return_in_progress%></a>条</span></div>
                        <div class="listMsg">
                            <a class="blue_color" <c:if test="<%=sale_return_in_progress<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_RETURN_ORDERS&orderStatus=<%=OrderStatus.WAITING_STORAGE.toString()%>">待入库结算：<span class="yellow_color"><%=sale_return_in_progress%></span>条</a>
                            <%--<a class="blue_color">欠款结算：<span class="yellow_color">4</span>条</a>--%>
                        </div>
                    </div>
                    <span class="list_bottom"></span>
                </div>
            </div>
        </div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_ORDERS.PURCHASE">
        <div class="orders">
            <div class="title_blue title_green">在线采购订单<a class="all" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS">查看所有</a></div>
            <div class="order_titles">
                <div class="order_list">
                    <span class="list_top"></span>
                    <div class="list_body" style="height: 100px;">
                        <div class="title"><span class="yellow_color">新订单</span><span class="list_all">共有
                            <a class="blue_color number" <c:if test="<%=purchase_new<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>"><%=purchase_new%></a>条</span></div>
                        <div class="listMsg">
                            <a class="blue_color" <c:if test="<%=purchase_today_new<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>&startTimeStr=<%=todayTimeStr%>">
                                今日新增：<span class="yellow_color"><%=purchase_today_new%></span>条</a>
                            <a class="blue_color" <c:if test="<%=purchase_early_new<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>&endTimeStr=<%=yestodayTimeStr%>">
                                往日新增：<span class="yellow_color"><%=purchase_early_new%></span>条</a>
                        </div>
                    </div>
                    <span class="list_bottom"></span>
                </div>
                <div class="order_list">
                    <span class="list_top"></span>
                    <div class="list_body" style="height: 100px">
                        <div class="title"><span class="yellow_color">处理中订单</span><span class="list_all">共有
                            <a class="blue_color number" <c:if test="<%=purchase_in_progress<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=inProgress"><%=purchase_in_progress%></a>条</span></div>
                        <div class="listMsg">
                            <a class="blue_color" <c:if test="<%=purchase_seller_stock<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_STOCK.toString()%>">
                                卖家备货中：<span class="yellow_color"><%=purchase_seller_stock%></span>条</a>
                            <a class="blue_color" <c:if test="<%=purchase_seller_dispatch<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_DISPATCH.toString()%>">
                                卖家已发货：<span class="yellow_color"><%=purchase_seller_dispatch%></span>条</a>
                            <a class="blue_color" <c:if test="<%=purchase_seller_stop<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.PURCHASE_SELLER_STOP.toString()%>">
                                卖家中止销售：<span class="yellow_color"><%=purchase_seller_stop%></span>条</a>
                            <a class="blue_color" <c:if test="<%=purchase_seller_refused<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.SELLER_REFUSED.toString()%>">
                                卖家拒绝销售：<span class="yellow_color"><%=purchase_seller_refused%></span>条</a>
                        </div>
                    </div>
                    <span class="list_bottom"></span>
                </div>
                <div class="order_list">
                    <span class="list_top"></span>
                    <div class="list_body">
                        <div class="title"><span class="yellow_color">已入库</span><span class="list_all">共有
                            <a class="blue_color number" <c:if test="<%=purchase_done<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.PURCHASE_ORDER_DONE.toString()%>"><%=purchase_done%></a>条</span></div>
                        <div class="listMsg">
                            <a class="blue_color" <c:if test="<%=purchase_today_done<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.PURCHASE_ORDER_DONE.toString()%>&startTimeStr=<%=todayTimeStr%>&timeField=inventoryVestDate">
                                今日入库：<span class="yellow_color"><%=purchase_today_done%></span>条</a>
                            <a class="blue_color" <c:if test="<%=purchase_early_done<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=<%=OrderStatus.PURCHASE_ORDER_DONE.toString()%>&endTimeStr=<%=yestodayTimeStr%>&timeField=inventoryVestDate">
                                往日入库：<span class="yellow_color"><%=purchase_early_done%></span>条</a>
                        </div>
                    </div>
                    <span class="list_bottom"></span>
                </div>

            </div>
        </div>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.SCHEDULE.REMIND_ORDERS.PURCHASE_RETURN">
        <div class="orders">
            <div class="title_blue title_purple">在线入库退货单<a class="all" href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS">查看所有</a></div>
            <div class="order_titles">
                <div class="order_list">
                    <span class="list_top"></span>
                    <div class="list_body">
                        <div class="title"><span class="yellow_color">新退货单</span><span class="list_all">共有
                            <a class="blue_color number" <c:if test="<%=purchase_return_new<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>"><%=purchase_return_new%></a>条</span></div>
                        <div class="listMsg">
                            <a class="blue_color" <c:if test="<%=purchase_return_today_new<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>&startTimeStr=<%=todayTimeStr%>">
                                今日新增：<span class="yellow_color"><%=purchase_return_today_new%></span>条</a>
                            <a class="blue_color" <c:if test="<%=purchase_return_early_new<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=<%=OrderStatus.SELLER_PENDING.toString()%>&endTimeStr=<%=yestodayTimeStr%>">
                                往日新增：<span class="yellow_color"><%=purchase_return_early_new%></span>条</a>
                        </div>
                    </div>
                    <span class="list_bottom"></span>
                </div>
                <div class="order_list">
                    <span class="list_top"></span>
                    <div class="list_body">
                        <div class="title"><span class="yellow_color">处理中退货单</span><span class="list_all">共有
                            <a class="blue_color number" <c:if test="<%=purchase_return_in_progress<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=inProgress"><%=purchase_return_in_progress%></a>条</span></div>
                        <div class="listMsg">
                            <a class="blue_color" <c:if test="<%=purchase_return_seller_accept<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=<%=OrderStatus.SELLER_ACCEPTED.toString()%>">
                                卖家已接受：<span class="yellow_color"><%=purchase_return_seller_accept%></span>条</a>
                            <a class="blue_color" <c:if test="<%=purchase_return_seller_refused<=0%>">onclick="javascript:return false;"</c:if>
                               href="orderCenter.do?method=getTodoOrders&type=TODO_PURCHASE_RETURN_ORDERS&orderStatus=<%=OrderStatus.SELLER_REFUSED.toString()%>">
                                卖家已拒绝：<span class="yellow_color"><%=purchase_return_seller_refused%></span>条</a>
                        </div>
                    </div>
                    <span class="list_bottom"></span>
                </div>
            </div>
        </div>
        </bcgogo:hasPermission>
    </div>
</div>
<div id="mask"  style="display:block;position: absolute;">
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
