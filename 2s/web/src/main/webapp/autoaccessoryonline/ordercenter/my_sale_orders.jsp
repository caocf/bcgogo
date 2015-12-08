<%--
  Created by IntelliJ IDEA.
  User: king
  Date: 13-8-29
  Time: 上午9:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>在线销售订单</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/accept<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/todoOrders<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/my_sale_orders<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        var selfShopId = "${sessionScope.shopId}";
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SCHEDULE.REMIND_ORDERS.SALE");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"列表");
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
<div class="titBody">
<jsp:include page="../supplyCenterLeftNavi.jsp">
    <jsp:param name="currPage" value="sale"/>
</jsp:include>
<div class="content-main self-purchase-order">
<div class="group-notice">
    <table>
        <colgroup>
            <col width="88px">
            <col width="135px">
            <col width="50px">
            <col width="120px">
            <col width="120px">
            <col width="120px">
            <col width="110px">
        </colgroup>
        <tr>
            <td><span class="info-label">新订单: </span></td>
            <td><span class="line-info" tab="salesNewOrder" orderStatus="SELLER_PENDING">共有&nbsp;<em class="number" id="sale_new">0</em>&nbsp;条待卖家处理</span></td>
            <td><span class="info-content">其中</span></td>
            <td class="info-content">
                <a class="line-info" tab="salesNewOrder" startDate="${todayDateStr}" endDate="${todayDateStr}">
                    <em class="word">今日新增</em>
                    <em class="number" id="sale_today_new">(0)</em>
                </a>
            </td>
            <td class="info-content">
                <a class="line-info" tab="salesNewOrder" startDate="" endDate="${yesterdayDateStr}">
                    <em class="word">往日新增</em>
                    <em class="number" id="sale_early_new">(0)</em>
                </a>
            </td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td><span class="info-label">处理中的订单: </span></td>
            <td><span class="line-info" tab="salesOrder">共有&nbsp;<em class="number" id="sale_in_progress">0</em>&nbsp;条</span></td>
            <td><span class="info-content">其中</span></td>
            <td style="width:120px;">
                <a class="line-info" tab="salesOrder" orderStatus="STOCKING">
                    <em class="word">备货中待发货</em>
                    <em class="number" id="sale_seller_stock">(0)</em>
                </a>
            </td>
            <td>
                <a class="line-info" tab="salesOrder" orderStatus="DISPATCH">
                    <em class="word">已发货待结算</em>
                    <em class="number" id="sale_seller_dispatch">(0)</em>
                </a>
            </td>
            <td>
                <a class="line-info" tab="salesOrder" orderStatus="SALE_DEBT_DONE">
                    <em class="word">欠款结算</em>
                    <em class="number" id="sale_debt_account">(0)</em>
                </a>
            </td>
        </tr>

    </table>
</div><!--end group-notice-->
    <div class="group-tab">
        <div class="tab-item actived" id="salesOrder">我的销售订单</div>
        <div class="tab-item" id="salesNewOrder">我的新订单</div>
        <div class="cl"></div>
    </div><!--end group-tab-->
<div class="search-param">
    <div class="param-title">
        订单查询
    </div>

    <div class="param-content">
        <form id="onlineSaleOrderSearchForm">
            <dl class="content-product-info">
                <dt >商品信息:</dt>
                <dd>
                    <input class="product-info search-fuzzy J-productSuggestion"  searchField="product_info" name="searchWord" type="text" placeholder="品名/品牌/规格/型号/车辆品牌/车型/商品编号"/>
                    <input class="product-name search-exact J-productSuggestion"  name="productName" searchField="product_name" type="text" placeholder="品名"/>
                    <input class="product-brand search-exact J-productSuggestion"  name="productBrand" searchField="product_brand" type="text" placeholder="品牌"/>
                    <input class="product-specifications search-exact J-productSuggestion"  name="productSpec" searchField="product_spec" type="text" placeholder="规格"/>
                    <input class="product-type search-exact J-productSuggestion"   name="productModel" searchField="product_model" type="text" placeholder="型号"/>
                    <input class="product-vehicle-brand search-exact J-productSuggestion"  name="productVehicleBrand" searchField="product_vehicle_brand" type="text" placeholder="车辆品牌"/>
                    <input class="product-vehicle-type search-exact J-productSuggestion"  name="productVehicleModel" searchField="product_vehicle_model" type="text" placeholder="车型"/>
                    <input class="product-number search-exact J-productSuggestion"  name="commodityCode" searchField="commodity_code" type="text" placeholder="商品编号"/>
                </dd>
                <div class="cl"></div>
            </dl>
            <dl class="content-product-info">
                <dt>订单信息:</dt>
                <dd>
                    <input  name="receiptNo" class="product-name order-info" type="text" placeholder="订单号"/>
                </dd>
            </dl>
            <dl class="content-product-info">
                <dt style="line-height: 20px">客户信息:</dt>
                <dd>
                    <input  name="customerOrSupplierInfo" class="product-name customer-info" type="text" placeholder="客户名/联系人/手机"/>
                </dd>
            </dl>
            <dl class="content-product-info">
                <dt >订单状态:</dt>
                <dd>
                    <select class="orderStatus"  name="orderStatus">
                        <option value="STOCKING,DISPATCH,SALE_DEBT_DONE">处理中的销售单</option>
                        <option value="STOCKING">备货中</option>
                        <option value="DISPATCH">已发货</option>
                        <option value="SELLER_STOP">终止销售</option>
                        <option value="SALE_REPEAL">已作废</option>
                        <option value="SALE_DEBT_DONE">欠款结算</option>
                        <option value="SALE_DONE">已结算</option>
                        <option value="STOCKING,DISPATCH,SALE_DEBT_DONE,SELLER_STOP,SALE_REPEAL,SALE_DONE">全部</option>
                    </select>
                </dd>
                <div class="cl"></div>
            </dl>
            <dl class="content-product-info">
                <dt>交易时间:</dt>
                <dd>
                    <a class="btnList" id="my_date_oneWeekBefore" name="my_date_select">近一周</a>&nbsp;
                    <a class="btnList" id="my_date_oneMonthBefore" name="my_date_select">近一个月</a>&nbsp;
                    <a class="btnList" id="my_date_threeMonthBefore" name="my_date_select">近三个月</a>&nbsp;
                    <a class="btnList" id="my_date_self_defining" name="my_date_select">自定义</a>&nbsp;


    <input  name="startTimeStr" tabInfo="salesOrder" id="startDate" class="my_startdate startDateStr J_hasDatePicker product-name date-area" type="text" readonly/>至
                    <input  name="endTimeStr" tabInfo="salesOrder" id="endDate" class="my_enddate endDateStr J_hasDatePicker product-name date-area" type="text" readonly/>
                </dd>
            </dl>

            <div class="group-button-control">
                <span id="searchOnlineSaleOrders" class="button-search button-blue-gradient">搜&nbsp;&nbsp;索</span>
                <span class="button-clear">清空条件</span>
            </div>
        </form>
        <form id="onlinePurchaseOrderSearchForm" style="display: none;">
            <dl class="content-product-info">
                <dt >商品信息:</dt>
                <dd>
                    <input class="product-info search-fuzzy J-productSuggestion"  searchField="product_info" name="searchWord" type="text" placeholder="品名/品牌/规格/型号/车辆品牌/车型/商品编号"/>
                    <input class="product-name search-exact J-productSuggestion"  name="productName" searchField="product_name" type="text" placeholder="品名"/>
                    <input class="product-brand search-exact J-productSuggestion"  name="productBrand" searchField="product_brand" type="text" placeholder="品牌"/>
                    <input class="product-specifications search-exact J-productSuggestion"  name="productSpec" searchField="product_spec" type="text" placeholder="规格"/>
                    <input class="product-type search-exact J-productSuggestion"   name="productModel" searchField="product_model" type="text" placeholder="型号"/>
                    <input class="product-vehicle-brand search-exact J-productSuggestion"  name="productVehicleBrand" searchField="product_vehicle_brand" type="text" placeholder="车辆品牌"/>
                    <input class="product-vehicle-type search-exact J-productSuggestion"  name="productVehicleModel" searchField="product_vehicle_model" type="text" placeholder="车型"/>
                    <input class="product-number search-exact J-productSuggestion"  name="commodityCode" searchField="commodity_code" type="text" placeholder="商品编号"/>
                </dd>
                <div class="cl"></div>
            </dl>
            <dl class="content-product-info">
                <dt style="line-height: 20px">客户信息:</dt>
                <dd>
                    <input  name="customerOrSupplierInfo" class="product-name customer-info" type="text" />
                    <input type="hidden" id="customerShopIds" name="customerShopIds" />
                </dd>
            </dl>
            <dl class="content-product-info">
                <dt >订单状态:</dt>
                <dd>
                    <select class="orderStatus"  name="orderStatus">
                        <option value="SELLER_PENDING">待处理</option>
                        <option value="SELLER_REFUSED">已拒绝</option>
                        <option value="PURCHASE_ORDER_REPEAL">买家终止交易</option>
                        <option value="SELLER_PENDING,SELLER_REFUSED,PURCHASE_ORDER_REPEAL">全部</option>
                    </select>
                </dd>
                <div class="cl"></div>
            </dl>
            <%--<dl class="content-product-info">--%>
            <%--<dt>评价状态:</dt>--%>
            <%--<dd>--%>
            <%--<select class="order-status" id="commentStatus">--%>
            <%--<option value="">全部</option>--%>
            <%--<option value="commented">已评价</option>--%>
            <%--<option value="uncommented">未评价</option>--%>
            <%--</select>--%>
            <%--</dd>--%>
            <%--<div class="cl"></div>--%>
            <%--</dl>--%>
            <dl class="content-product-info">
                <dt>交易时间:</dt>
                <dd>

                    <a class="btnList select2" id="my_date_oneWeekBefore" name="my_date_select">近一周</a>&nbsp;
                    <a class="btnList select2" id="my_date_oneMonthBefore" name="my_date_select">近一个月</a>&nbsp;
                    <a class="btnList select2" id="my_date_threeMonthBefore" name="my_date_select">近三个月</a>&nbsp;
                    <a class="btnList select2" id="my_date_self_defining_new" name="my_date_select">自定义</a>&nbsp;

    <input  id="startdate" name="startTimeStr" tabInfo="purchaseOrder" class="my_startdate2 startDateStr J_hasDatePicker product-name date-area" type="text" readonly/>至
                    <input  id="enddate" name="endTimeStr" tabInfo="purchaseOrder" class="my_enddate2 endDateStr J_hasDatePicker product-name date-area" type="text" readonly/>
                </dd>
            </dl>

            <div class="group-button-control">
                <span id="searchOnlinePurchaseOrders" class="button-search button-blue-gradient">搜&nbsp;&nbsp;索</span>
                <span class="button-clear">清空条件</span>
            </div>
        </form>
    </div>

</div><!--end search-param-->


<div class="search-result">
    <dl class="result-list ">
        <dt class="list-title">
        <ul>
            <!--<li class="item-checkbox"><input type="checkbox" /></li>-->
            <li class="item-product-info width-set">商品信息</li>
            <li class="item-product-unit-price width-set">单价(元)</li>
            <li class="item-product-quantity width-set">数量</li>
            <li class="item-product-price width-set">成交价(元)</li>
            <li class="item-product-payables width-set">实收款(元)</li>
            <li class="item-product-order-status width-set">订单状态</li>
            <li class="item-product-operating width-set">操作</li>
        </ul>
        <div class="cl"></div>
        </dt>
        <dd class="list-content J-SaleOrdersList">
        </dd>

    </dl><!--end result-list-->

    <!--插入你喜爱的分页控件 snips-->
    <div class="page-control" id="salesOrderPage">

        <bcgogo:ajaxPaging url="orderCenter.do?method=getOnlineSaleOrders" dynamical="saleOrderList"
                           data='{startPageNo:1,maxRows:5}' postFn="drawSaleOrderList" display="none"/>
        <div class="cl"></div>
    </div>

    <div class="page-control" id="salesNewOrderPage" style="display: none;">

        <bcgogo:ajaxPaging url="orderCenter.do?method=getOnlineSaleNewOrders" dynamical="purchaseOrderList"
                           data='{startPageNo:1,maxRows:5}' postFn="drawPurchaseOrderList" display="none"/>
        <div class="cl"></div>
    </div>
    <%--<div class="page-control">--%>
    <%--add your paging control snips--%>
    <%--</div>--%>

</div>
<!--end search-result-->

<div class="cl"></div>
<div class="height"></div>
<div class="i_searchBrand" id="acceptDialog" title="确认接受提示" style="display:none; width:500px;">
        <h3>您确定要接受该销售吗？</h3>
        <form id="salesAcceptForm" target="_blank" method="post" action="sale.do?method=acceptSaleOrder">
            <table border="0" width="550">
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                    <tr>
                        <td align="right">仓库：</td>
                        <td>
                            <select id="storehouseId" name="storehouseId" style="width:140px;height:21px;border: 1px solid #BBBBBB;" autocomplete="off">
                                <option value="">——请选择仓库——</option>
                                <c:forEach items="${storeHouseDTOList}" var="item">
                                    <option value="${item.id}">${item.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                </bcgogo:hasPermission>
                <tr>
                    <td width="120" align="right">买家要求到货日期：</td>
                    <td><span id="pruchaseVestDate"></span></td>
                </tr>
                <tr>
                    <td align="right"><img src="images/star.jpg">预计交货日期：</td>
                    <td>
                        <input type="radio" name="dispatchDateRadio" value="today" checked="checked" />&nbsp;本日&nbsp;&nbsp;
                        <input type="radio" name="dispatchDateRadio" value="tomorrow" />&nbsp;明日&nbsp;&nbsp;
                        <input type="radio" name="dispatchDateRadio" value="innerThreeDays" />&nbsp;三天内&nbsp;&nbsp;
                        <input type="radio" name="dispatchDateRadio" value="definedDate" />&nbsp;自定义日期&nbsp;
                        <input id="definedDate" type="text" class="txt" style="width:75px;" readonly="readonly" />
                        <input id="dispatchDate" name="preDispatchDateStr" type="hidden"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <div class="btnClick" style="height:50px; line-height:50px">
                            <input type="hidden" id="purchaseOrderId"/>
                            <input type="button" id="acceptConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                            <input type="button" id="acceptCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                        </div>
                    </td>
                </tr>
            </table>
        </form>
</div>

<div class="i_searchBrand" id="refuseReasonDialog" title="确认拒绝提示" style="display:none; width: 500px;">
    <h3>友情提示：您拒绝后，该采购请求将被驳回 ！</h3>
    <h3>您确定要拒绝该销售单吗？</h3>
    <form id="salesRefuseForm" method="post" action="sale.do?method=refusePendingPurchaseOrder">
        <table border="0" width="480">
            <tr>
                <td width="100" align="right"><img src="images/star.jpg">拒绝理由：</td>
                <td><textarea class="textarea" id="refuseMsg" name="refuseMsg" style="width:320px;" maxlength="500"></textarea></td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="hidden" id="refuse_id" name="purchaseOrderId"/>
                        <input type="button" id="refuseConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                        <input type="button" id="refuseCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>

<div class="i_searchBrand" id="repealReasonDialog" title="确认终止销售提示" style="display:none; width: 500px;">
    <h3>友情提示：销售单终止销售后，将不再有效，交易会被取消 ！</h3>
    <h3>您确定要终止该销售单吗？</h3>
    <form id="salesRepealForm" target="_blank" method="post" action="sale.do?method=saleOrderRepeal">
        <table border="0" width="480">
            <tr>
                <td width="100" align="right"><img src="images/star.jpg">终止销售理由：</td>
                <td><textarea class="textarea" id="repealMsg" name="repealMsg" style="width:320px;" maxlength="500"></textarea></td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="hidden" id="repeal_id" name="salesOrderId"/>
                        <input type="button" id="repealConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                        <input type="button" id="repealCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>

<div class="i_searchBrand" id="dispatchDialog" title="确认发货提示" style="display:none; width: 500px;">
    <h3>请填写发货的物流信息</h3>
    <form id="dispatchForm" target="_blank" method="post" action="sale.do?method=dispatchSaleOrder">
        <table border="0" width="480">
            <tr>
                <td width="100" align="right">物流公司：</td>
                <td><input type="text" id="company" name="company" style="width:320px;height:20px;" maxlength="40" /></td>
            </tr>
            <tr>
                <td width="100" align="right">运单号：</td>
                <td><input type="text" id="waybill_id" name="waybills" style="width:320px;height:20px;"  maxlength="100"/></td>
            </tr>
            <tr>
                <td width="100" align="right">备注：</td>
                <td><textarea id="dispatch_memo" name="dispatchMemo" style="width:320px;"  maxlength="500"></textarea></td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="hidden" id="dispatch_id" name="id"/>
                        <input type="button" id="dispatchConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                        <input type="button" id="dispatchCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>

<div id="mask"  style="display:block;position: absolute;">
</div>
<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <jsp:include page="../../txn/common/selectStoreHouse.jsp"/>
</bcgogo:hasPermission>
</div>
<!--end content-main-->

</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>