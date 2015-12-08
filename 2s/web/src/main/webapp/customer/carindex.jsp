<%@ page import="com.bcgogo.common.Pager" %>
<%@ page import="com.bcgogo.remind.RepairRemindResponse" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<%--
  User: monrove
  Date: 11-12-28n
  Time: 下午1:36
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>车辆-引导页</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/userGuid<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/userDesic<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/carIndex<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dropnoe<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/carindex<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="js/dragiframe<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <script type="text/javascript" src="js/permission<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"VEHICLE_CONSTRUCTION");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/common/messagePrompt.jsp" %>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<input type="hidden" value="" class="voucher_isFirstPage">
<input type="hidden" value="" class="voucher_isLastPage">
<input type="hidden" value="" class="voucher_isNextPage">
<input type="hidden" value="" class="voucher_isPrevPage">
<div class="clear i_main">
    <div class="mainTitles">
        <div class="titleWords">车辆施工</div>
        <jsp:include page="../txn/vehicleIndexNavi.jsp"/>
    </div>
</div>
<div class="i_main clear">

    <div class="car_head">
        <div class="car_line">
            <div class="car_div title">车辆施工类：</div>
            <div class="car_all_div">
                <div class="car_a car_status_a hover" data-condition-order="ALL">共<span id="total" class="number blue_color">0</span>条待处理记录</div>
                <div class="car_a car_status_a" data-condition-order="REPAIR_DISPATCH">施工中共<span id="dispatchTotal" class="number blue_color">0</span>条 </div>
                <bcgogo:permissionParam permissions="WEB.VERSION.IGNORE_VERIFIER_INVENTORY,WEB.TXN.INVENTORY_MANAGE.REPAIR_PICKING">
                    <c:if test="${!WEB_VERSION_IGNORE_VERIFIER_INVENTORY || (WEB_TXN_INVENTORY_MANAGE_REPAIR_PICKING && isRepairPickingSwitchOn)}">
                        其中(
                        <c:if test="${!WEB_VERSION_IGNORE_VERIFIER_INVENTORY && !(WEB_TXN_INVENTORY_MANAGE_REPAIR_PICKING && isRepairPickingSwitchOn)}">
                            <div class="car_a car_status_a" data-condition-order="REPAIR_DISPATCH" data-condition-item="LACK">
                                缺料待修<span id="lackTotal" class="number blue_color">0</span>条
                            </div>
                            <div class="car_a car_status_a" data-condition-order="REPAIR_DISPATCH" data-condition-item="INCOMING">
                                来料待修<span id="incomingTotal" class="number blue_color">0</span>条
                            </div>
                            <div class="car_a car_status_a" data-condition-order="REPAIR_DISPATCH" data-condition-item="NORMAL">
                                正常施工<span id="normalTotal" class="number blue_color">0</span>条
                            </div>
                        </c:if>
                        <c:if test="${WEB_TXN_INVENTORY_MANAGE_REPAIR_PICKING && isRepairPickingSwitchOn}">
                            <div class="car_a car_status_a" data-condition-order="REPAIR_DISPATCH"  data-condition-item="WAIT_OUT_STORAGE">
                                待领料<span id="waitOutStorageTotal" class="number blue_color">0</span>条
                            </div>
                            <div class="car_a car_status_a" data-condition-order="REPAIR_DISPATCH" data-condition-item="OUT_STORAGE">
                                领料待修<span id="outStorageTotal"  class="number blue_color">0</span>条
                            </div>
                        </c:if>
                        )
                    </c:if>
                </bcgogo:permissionParam>
                <div class="car_a car_status_a" data-condition-order="REPAIR_DONE">已完工<span id="pendingTotal" class="number blue_color">0</span>条</div>
            </div>
        </div>
        <div class="car_line">
            <div class="car_div title">车辆服务记录：</div>
            <div class="car_all_div">
                <div data-event="yesterday" class="<c:if test="${serviceYsterdayTimes > 0}">car_a</c:if>">昨日服务<span class="number blue_color">${serviceYsterdayTimes}</span>辆</div>
                <div data-event="today" class="<c:if test="${serviceTodayTimes > 0}">car_a</c:if>">今日服务<span class="number blue_color">${serviceTodayTimes}</span>辆</div>
                <div data-event="add" class="<c:if test="${todayNewUserNumber > 0}">car_a</c:if>">其中新增<span class="number blue_color">${todayNewUserNumber}</span>辆</div>
            </div>
        </div>
    </div>
    <div>
        <strong style="font-size:1.3em;">
            代金券:共<span class="num_1">--</span>笔
            <%--应收总计:<span class="total_1">--</span>元--%>
            <%--待交付:共<span class="num_2">--</span>笔--%>
            <%--应收总计:<span class="total_2">--</span>元--%>
        </strong>
    </div>
    <div style="margin-bottom:55px;">

        <div id="voucher_list"></div>
        <div style="float: right;margin-top:15px;">
            <bcgogo:ajaxPaging url="customer.do?method=getCouponList" dynamical="voucher_list_page" postFn="we" data="{startPageNo:1,maxRows:5}"></bcgogo:ajaxPaging>
        </div>
    </div>

    <hr style="height:3px;color:orange;background-color:orange;border-radius:3px;"/>





    <%--施工中。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。--%>


    <div style="margin-bottom: 15px;margin-top:15px;font-size: 1.3em;float:none;">
        <strong>施工中：共<span id="dispatchTotal2" class="yellow_color">0</span>笔 应收总计<span id="dispatchFee" class="yellow_color">0</span>元 待交付：共<span id="pendingTotal2" class="yellow_color">0</span>笔 应收总计<span id="pendingFee" class="yellow_color">0</span>元</strong>
    </div>

    <div>
        <div id="invoice_list"></div>
        <div style="float: right;">
            <bcgogo:ajaxPaging url="customer.do?method=getCarConstructionInvoiceList" dynamical="invoice_list_page" postFn="showList" data="{startPageNo:1,maxRows:5}"></bcgogo:ajaxPaging>
        </div>
    </div>



    <div style="display: none;">
        <!-- 车辆施工单列表中的每列信息以此为模板 -->
        <div id="template" class="car_item">
            <div class="title">
                <strong>施工单号：</strong><span data-mark="receiptNo" class="car_a blue_color">--</span>
                <strong>车牌号：</strong><span data-mark="licenceNo">--</span>
                <strong>客户信息：</strong><span data-mark="customerInfo" class="car_a blue_color"><span data-mark="customerName">--</span> <span data-mark="mobile">--</span></span>
                <strong>车主信息：</strong><span data-mark="vehicleContact">--</span> <span data-mark="vehicleMobile">--</span>
                <strong>状态：</strong><span data-mark="statusStr">--</span>
                <strong>进厂时间：</strong><span data-mark="startDateStr">--</span>
            </div>
            <div class="content">
                <div>
                    <strong>单据金额：</strong>￥<span data-mark="total">0</span>
                    <strong>预计出厂时间：</strong><span data-mark="endDateStr">--</span>
                    <strong>施工内容：</strong><span data-mark="service">--</span>
                </div>
                <div data-mark="materialDetails"></div>
                <table class="table" cellpadding="0" cellspacing="0">
                    <colgroup>
                        <col width="80px">
                        <col width="100px">
                        <col width="100px">
                        <col width="140px">
                        <col width="140px">
                        <col width="100px">
                        <col>
                        <col width="130px">
                        <col width="100px">
                    </colgroup>
                    <tr class="table_title">
                        <th>商品编号</th>
                        <th>品名</th>
                        <th>品牌</th>
                        <th>规格</th>
                        <th>型号</th>
                        <th>车辆品牌</th>
                        <th>车型</th>
                        <th>提醒状态</th>
                        <th>操作</th>
                    </tr>
                    <tr>
                        <td colspan="9">
                            <div>共有材料 <span data-mark="itemCount">0</span> 条
                                <bcgogo:permissionParam permissions="WEB.VERSION.IGNORE_VERIFIER_INVENTORY">
                                    <c:if test="${!WEB_VERSION_IGNORE_VERIFIER_INVENTORY}">
                                        其中待处理材料 <span data-mark="waitHandle">0</span> 条
                                    </c:if>
                                </bcgogo:permissionParam>
                                <a data-mark="expand" href="javascript:void(0)">点击展开</a><a class="car_hide" data-mark="collapse" href="javascript:void(0)">点击收起</a>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>


<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>

</html>
