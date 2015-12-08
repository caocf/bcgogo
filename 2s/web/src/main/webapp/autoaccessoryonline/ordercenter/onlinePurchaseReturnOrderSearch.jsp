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
    <script type="text/javascript" src="js/page/autoaccessoryonline/onlinePurchaseReturnOrderSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SCHEDULE.REMIND_ORDERS.PURCHASE_RETURN");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"列表");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">

        <jsp:include page="../supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="purchaseReturn"/>
        </jsp:include>

        <div class="content-main purchase-return">

            <%--<div class="group-notice">
                <div class="line-info">
                    <span class="info-label">新退货单: </span>
                    <span class="info-content">
                        <span class="span-info J_status_link" paramStatus="SELLER_PENDING">
                        共有 <em class="number">${orderCenterDTO.purchaseReturnNew}</em> 条待卖家处理</span>
                        其中<span class="span-info J_status_link" paramStatus="SELLER_PENDING" paramStartDate="${todayDate}" paramEndDate="${todayDate}">
                        <em class="word">今日新增</em> <em class="number">(${orderCenterDTO.purchaseReturnTodayNew})</em> </span>
                        <span class="span-info J_status_link" paramStatus="SELLER_PENDING" paramEndDate="${yesterdayDate}">
                            <em class="word">往日新增</em> <em class="number">(${orderCenterDTO.purchaseReturnEarlyNew})</em> </span>
                    </span>
                </div>
                <div class="clear i_height"></div>
                <div class="line-info">
                    <span class="info-label">处理中的退货单: </span>
                    <span class="info-content">
                        <span class="span-info J_status_link" paramStatus="SELLER_ACCEPTED,SELLER_REFUSED">
                        共有 <em class="number">${orderCenterDTO.purchaseReturnInProgress}</em> 条</span>
                        其中<span class="span-info J_status_link" paramStatus="SELLER_ACCEPTED"><em class="word">卖家已接受</em> <em class="number">(${orderCenterDTO.purchaseReturnSellerAccept})</em></span>
                        <span class="span-info J_status_link" paramStatus="SELLER_REFUSED"><em class="word">其中卖家已拒绝</em> <em class="number">(${orderCenterDTO.purchaseReturnSellerRefused})</em></span>
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
                        <td><span class="line-info J_status_link" paramStatus="SELLER_PENDING">共有&nbsp;<em class="number">${orderCenterDTO.purchaseReturnNew}</em>&nbsp;条待卖家处理</span>
                        </td>
                        <td><span class="info-content">其中</span></td>
                        <td class="info-content">
                            <a class="line-info J_status_link" paramStatus="SELLER_PENDING" paramStartDate="${todayDate}" paramEndDate="${todayDate}">
                                <em class="word">今日新增</em>
                                <em class="number">(${orderCenterDTO.purchaseReturnTodayNew})</em>
                            </a>
                        </td>
                        <td class="info-content">
                            <a class="line-info J_status_link" paramStatus="SELLER_PENDING" paramEndDate="${yesterdayDate}">
                                <em class="word">往日新增</em>
                                <em class="number">(${orderCenterDTO.purchaseReturnEarlyNew})</em>
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <td><span class="info-label">处理中的退货单: </span></td>
                        <td><span class="line-info J_status_link" paramStatus="SELLER_ACCEPTED,SELLER_REFUSED">共有&nbsp;<em class="number">${orderCenterDTO.purchaseReturnInProgress}</em>&nbsp;条</span>
                        </td>
                        <td><span class="info-content">其中</span></td>
                        <td>
                            <a class="line-info J_status_link" paramStatus="SELLER_ACCEPTED">
                                <em class="word">卖家已接受</em>
                                <em class="number">(${orderCenterDTO.purchaseReturnSellerAccept})</em>
                            </a>
                        </td>
                        <td>
                            <a class="line-info J_status_link" paramStatus="SELLER_REFUSED">
                                <em class="word">卖家已拒绝</em>
                                <em class="number">(${orderCenterDTO.purchaseReturnSellerRefused})</em>
                            </a>
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
                        <dl class="content-order-additional item-first fl">
                            <dt><span class="spanName">退货单号&nbsp;</span></dt>
                            <dd><input type="text" name="receiptNo" searchField="receiptNo" placeholder="退货单号" /></dd>
                            <div class="cl"></div>
                        </dl>
                        <dl class="content-order-additional fl">
                            <dt><span class="spanName">供应商信息&nbsp;</span></dt>
                            <dd><input type="text" name="supplierInfo" id="supplierInfo" searchField="supplierInfo" placeholder="供应商/联系人/手机" /></dd>
                            <div class="cl"></div>
                        </dl>
                        <dl class="content-order-additional fl">
                            <dt>
                                <span class="spanName">退货状态&nbsp;</span>
                            </dt>
                            <dd>
                                <select id="orderStatus" name="orderStatus">
                                    <option value="all">全部</option>
                                    <option value="SELLER_PENDING,SELLER_ACCEPTED,SELLER_REFUSED">待办入库退货单</option>
                                    <option value="SELLER_ACCEPTED,SELLER_REFUSED">卖家处理中</option>
                                    <option value="SELLER_PENDING">待卖家处理</option>
                                    <option value="SELLER_ACCEPTED">卖家已接受</option>
                                    <option value="SELLER_REFUSED">卖家已拒绝</option>
                                    <option value="SETTLED">已结算</option>
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
                            <a class="btnList" id="my_date_self_defining" name="my_date_select">自定义</a>&nbsp;
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
                <bcgogo:ajaxPaging url="onlinePurchaseReturnOrder.do?method=onlinePurchaseReturnOrderSearch" postFn="initTable" dynamical="dynamical1" display="none" data='{startPageNo:1,maxRows:15}'/>
                <div class="cl"></div>
            </div>

            </div>
            <!--end search-result-->



        </div>

    </div>
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