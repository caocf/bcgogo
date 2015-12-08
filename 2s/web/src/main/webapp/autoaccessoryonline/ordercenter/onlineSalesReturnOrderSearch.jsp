<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>订单列表</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/todoOrders<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/onlineSalesReturnOrderSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SCHEDULE.REMIND_ORDERS.SALE_RETURN");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"列表");

        var shopId = '${sessionScope.shopId}';
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">

        <jsp:include page="../supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="saleReturn"/>
        </jsp:include>

        <div class="content-main sales-return">

            <%--<div class="group-notice">
                <div class="line-info">
                    <span class="info-label">新退货单: </span>
                    <span class="info-content">
                        <span class="span-info J_status_link" paramStatus="PENDING">
                        共有 <em class="number">${orderCenterDTO.saleReturnNew}</em> 条待卖家处理</span>
                        其中<span class="span-info J_status_link" paramStatus="PENDING" paramStartDate="${todayDate}" paramEndDate="${todayDate}">
                        <em class="word">今日新增</em><em class="number"> (${orderCenterDTO.saleReturnTodayNew}) </em></span>
                        <span class="span-info J_status_link" paramStatus="PENDING" paramEndDate="${yesterdayDate}">
                        <em class="word">往日新增</em><em class="number"> (${orderCenterDTO.saleReturnEarlyNew}) </em></span>
                    </span>
                </div>
                <div class="clear i_height"></div>
                <div class="line-info">
                    <span class="info-label">处理中的退货单: </span>
                    <span class="info-content">
                        <span class="span-info J_status_link" paramStatus="WAITING_STORAGE">
                        共有<em class="number"> ${orderCenterDTO.saleReturnInProgress} </em>条</span>
                        <span class="span-info J_status_link" paramStatus="WAITING_STORAGE">
                            <em class="word">其中待入库</em> <em class="number"> (${orderCenterDTO.saleReturnInProgress}) </em></span>
                    </span>
                </div>

            </div>--%>

            <div class="group-notice">
                <table>
                    <colgroup>
                        <col width="120px">
                        <col width="135px">
                        <col width="50px">
                        <col width="150px">
                        <col width="150px">
                    </colgroup>
                    <tr>
                        <td><span class="info-label">新退货单: </span></td>
                        <td><span class="line-info J_status_link" paramStatus="PENDING">共有&nbsp;<em class="number">${orderCenterDTO.saleReturnNew}</em>&nbsp;条待卖家处理</span>
                        </td>
                        <td><span class="info-content">其中</span></td>
                        <td class="info-content">
                            <a class="line-info J_status_link" paramStatus="PENDING" paramStartDate="${todayDate}" paramEndDate="${todayDate}">
                                <em class="word">今日新增</em>
                                <em class="number">(${orderCenterDTO.saleReturnTodayNew})</em>
                            </a>
                        </td>
                        <td class="info-content">
                            <a class="line-info J_status_link" paramStatus="PENDING" paramEndDate="${yesterdayDate}">
                                <em class="word">往日新增</em>
                                <em class="number">(${orderCenterDTO.saleReturnEarlyNew})</em>
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <td><span class="info-label">处理中的退货单: </span></td>
                        <td><span class="line-info J_status_link" paramStatus="WAITING_STORAGE">共有&nbsp;<em class="number">${orderCenterDTO.saleReturnInProgress}</em>&nbsp;条</span>
                        </td>
                        <td><span class="info-content">其中</span></td>
                        <td>
                            <a class="line-info J_status_link" paramStatus="WAITING_STORAGE">
                                <em class="word">待入库</em>
                                <em class="number">(${orderCenterDTO.saleReturnInProgress})</em>
                            </a>
                        </td>
                        <td>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="search-param">
                <div class="param-title">订单查询</div>
                <div class="param-content">
                    <form name="orderSearchConditionDTO" id="orderSearchForm" action="" method="post">
                        <input type="hidden" name="maxRows" id="maxRows" value="15">
                        <dl class="content-product-info">
                            <dt>商品信息:</dt>
                            <dd>
                                <input type="text"
                                       class="txt J-productSuggestion J-initialCss J_clear_input product-info search-fuzzy"
                                       name="searchWord"
                                       searchField="product_info" placeholder="品名/品牌/规格/型号/适用车辆"/>
                                <input type="text"
                                       class="txt J-productSuggestion J-initialCss J_clear_input product-name search-exact"
                                       name="productName" searchField="product_name" placeholder="品名"/>
                                <input type="text"
                                       class="txt J-productSuggestion J-initialCss J_clear_input product-brand search-exact"
                                       name="productBrand" searchField="product_brand" placeholder="品牌"/>
                                <input type="text"
                                       class="txt J-productSuggestion J-initialCss J_clear_input product-specifications search-exact"
                                       name="productSpec" searchField="product_spec" placeholder="规格"/>
                                <input type="text"
                                       class="txt J-productSuggestion J-initialCss J_clear_input product-type search-exact"
                                       name="productModel" searchField="product_model" placeholder="型号"/>
                                <input type="text"
                                       class="txt J-productSuggestion J-initialCss J_clear_input product-vehicle-brand search-exact"
                                       name="productVehicleBrand" searchField="product_vehicle_brand" placeholder="车辆品牌"/>
                                <input type="text"
                                       class="txt J-productSuggestion J-initialCss J_clear_input product-vehicle-type search-exact"
                                       name="productVehicleModel" searchField="product_vehicle_model" placeholder="车型"/>
                                <input type="text"
                                       class="txt J-productSuggestion J-initialCss J_clear_input product-number search-exact"
                                       name="commodityCode" searchField="commodity_code" placeholder="商品编号"/>
                            </dd>
                            <div class="cl"></div>
                        </dl>
                        <dl class="content-order-additional fl item-first">
                            <dt><span class="spanName">退货单号&nbsp;</span></dt>
                            <dd><input type="text" name="receiptNo" searchField="receiptNo" placeholder="退货单号" /></dd>
                            <div class="cl"></div>
                        </dl>
                        <dl class="content-order-additional fl">
                            <dt><span class="spanName">客户信息&nbsp;</span></dt>
                            <dd><input type="text" name="customerInfo" id="customerInfo" searchField="customerInfo" placeholder="客户/联系人/手机" /></dd>
                            <div class="cl"></div>
                        </dl>
                        <dl class="content-order-additional fl">
                            <dt><span class="spanName">退货状态&nbsp;</span></dt>
                            <dd>
                                <select id="orderStatus" name="orderStatus">
                                    <option value="all">全部</option>
                                    <option value="PENDING,WAITING_STORAGE">待办销售退货单</option>
                                    <option value="PENDING">待处理</option>
                                    <option value="WAITING_STORAGE">待入库</option>
                                    <option value="REFUSED">已拒绝</option>
                                    <option value="SETTLED">已结算</option>
                                    <option value="STOP">买家终止交易</option>
                                </select>
                            </dd>
                            <div class="cl"></div>
                        </dl>
                        <div class="cl"></div>
                        <dl class="content-product-info">
                            <span class="spanName">下单时间&nbsp;</span>
                            <a class="btnList" id="my_date_oneWeekBefore" name="my_date_select">近一周</a>&nbsp;
                            <a class="btnList" id="my_date_oneMonthBefore" name="my_date_select">近一个月</a>&nbsp;
                            <a class="btnList" id="my_date_threeMonthBefore" name="my_date_select">近三个月</a>&nbsp;
                            <a class="btnList clicked" id="my_date_self_defining" name="my_date_select">自定义</a>&nbsp;
                            <input id="startDate" name="startTimeStr" class="my_startdate J_hasDatePicker product-name date-area" type="text" readonly/>至
                            <input id="endDate" name="endTimeStr" class="my_enddate J_hasDatePicker product-name date-area" type="text" readonly/>
                        </dl>

                        <div class="group-button-control">
                            <span class="button-search button-blue-gradient" id="doSearch">搜&nbsp;&nbsp;索</span>
                            <span class="button-clear" id="reset">清空条件</span>
                        </div>

                    </form>
                </div>
            </div>

            <div class="search-result">
            <dl class="result-list">
                <dt class="list-title">
                <ul>
                    <li class="item-product-info width-set">商品信息</li>
                    <li class="item-product-unit-price width-set">单价(元)</li>
                    <li class="item-product-quantity width-set">数量</li>
                    <li class="item-product-price width-set">交易金额(元)</li>
                    <li class="item-product-payables width-set">退款金额(元)</li>
                    <li class="item-product-order-status width-set">退货状态</li>
                    <li class="item-product-operating width-set">操作</li>
                </ul>
                <div class="cl"></div>
                </dt>
                <dd class="list-content"></dd>
            </dl>
            <!--end result-list-->

            <div class="page-control">
              <bcgogo:ajaxPaging url="onlineSalesReturnOrder.do?method=onlineSalesReturnOrderSearch" postFn="initTable" dynamical="dynamical1" display='none'/>
              <div class="cl"></div>
            </div>

            </div>
            <!--end search-result-->

        </div>

    </div>
</div>


<div class="i_searchBrand" id="refuseReasonDialog" title="确认拒绝提示" style="display:none; width: 500px;">
    <h3>友情提示：您拒绝后，该采购请求将被驳回 ！</h3>
    <h3>您确定要拒绝该销售退货单吗？</h3>
    <form id="salesRefuseForm" target="_blank" method="post" action="salesReturn.do?method=refuseSalesReturnOrder">
        <table border="0" width="480">
            <tr>
                <td width="100" align="right"><img src="images/star.jpg">拒绝理由：</td>
                <td><textarea class="textarea" id="refuseReason" name="refuseReason" style="width:320px;" maxlength="500"></textarea></td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="btnClick" style="height:50px; line-height:50px">
                        <input type="hidden" id="refuse_id" name="id"/>
                        <input type="button" id="refuseConfirmBtn" onfocus="this.blur();" value="确&nbsp;定">
                        <input type="button" id="refuseCancelBtn" onfocus="this.blur();" value="取&nbsp;消">
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>


<div id="mask" style="display:block;position: absolute;"></div>
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id"></div>
</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1000px" frameborder="0" src="" scrolling="no"></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

</body>
</html>