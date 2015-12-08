<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>订单列表</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/bcgogoReceivable<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/page/autoaccessoryonline/bcgogoReceivableOrder<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SYSTEM_SETTINGS.BCGOGO_RECEIVABLE");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"订单列表");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>

<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="../supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="bcgogoOrderCenterIndex"/>
        </jsp:include>

        <div class="added-management">
            <div class="accessoriesLeft" style="width:816px;margin: 0">
                <div class="title" style="width:794px;">可购产品一览</div>
                <div class="content">
                    <div class="payNodata-ul">
                        <ul>
                            <c:forEach items="${bcgogoProductDTOList}" var="bcgogoProductDTO">
                                <li>
                                    <a target="_blank" href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId=${bcgogoProductDTO.id}"><img style="width: 110px;height: 110px;" src="${bcgogoProductDTO.imageCenterDTO.bcgogoProductListImageURL}"/></a>
                                    <a target="_blank" href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId=${bcgogoProductDTO.id}" class="blue_color">${bcgogoProductDTO.name}</a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
        <div class="content-main self-purchase-order" style="font-size:12px;margin-top: 10px">
            <div class="group-notice" style="padding: 10px 20px;margin-bottom:10px;height:16px">
                <div style="float: right">有任何疑问可咨询：<a id="onlineServiceQQ" class="blue_color">在线客服</a></div>
                <div style="float: left;" class="info-label"> 我的交易提醒：</div>
                <div style="float: left;width: 80px;line-height:normal;" class="line-info"><span class="info-content" id="searchNonPaymentOrderBtn"><em class="word">待支付</em><em class="number">(${statMap.NON_PAYMENT+statMap.PARTIAL_PAYMENT})</em></span></div>
                <div style="float: left;width: 80px;line-height:normal;" class="line-info"><span class="info-content" id="searchFullPaymentOrderBtn"><em class="word">已支付</em><em class="number">(${statMap.FULL_PAYMENT})</em></span></div>
                <div style="float: left;width: 80px;line-height:normal;" class="line-info"><span class="info-content" id="searchShippedOrderBtn"><em class="word">已发货</em><em class="number">(${statMap.SHIPPED})</em></span></div>
                <div style="float: left;width: 100px;line-height:normal;" class="line-info"><span class="info-content" id="searchCanceledOrderBtn"><em class="word">交易取消</em><em class="number">(${statMap.CANCELED})</em></span></div>

            </div>
            <!--end group-notice-->
            <div class="search-result">
                <dl class="result-list">
                <dt class="list-title">
                <ul>
                    <li class="item-product-info" style="width: 300px">购买内容</li>
                    <li class="item-product-unit-price" style="width: 80px">单价(元)</li>
                    <li class="item-product-quantity" style="width: 80px">数量</li>
                    <li class="item-product-payables" style="width: 150px">应付总额(元)</li>
                    <li class="item-product-order-status" style="width: 120px">交易状态</li>
                    <li class="item-product-operating" style="width: 80px">操作</li>
                </ul>
                <div class="cl"></div>
                </dt>
                <form id="searchBcgogoReceivableOrderForm">
                    <div class="data-bottom" style="margin-bottom: 10px;margin-top: -11px;padding: 0 10px;">
                        <input id="checkAllOrderBtn" type="checkbox" value=""/> 全选 应付合计：
                        <span class="yellow_color" id="totalPayReceivableAmount">0</span>元 &nbsp;&nbsp; <a class="blue_color" id="doCombinedPayBcgogoReceivableOrderBtn">合并付款</a>


                        <input type="hidden" name="maxRows" id="maxRows" value="10">
                        <div style="float: right;width: 140px">
                            <select id="searchMonths" name="searchMonths" autocomplete="off" class="fr" style="margin-top:3px;width: 120px">
                                <option value="">显示全部日期</option>
                                <option value="1">最近一个月</option>
                                <option value="3">最近三个月</option>
                                <option value="6">最近半年</option>
                                <option value="12">最近一年</option>
                            </select>
                        </div>
                        <div style="float: right;width: 140px">
                            <select id="paymentStatuses" name="paymentStatuses" class="fr" style="margin-top:3px;width: 120px">
                                <option value="">显示全部状态</option>
                                <option value="PARTIAL_PAYMENT,NON_PAYMENT">待支付</option>
                                <option value="FULL_PAYMENT">已支付</option>
                                <option value="SHIPPED">已发货</option>
                                <option value="CANCELED">交易取消</option>
                            </select>
                        </div>


                    </div>
                </form>
                <dd class="list-content" id="bcgogoReceivableOrder_dd">
                <!--add item you need-->
                </dd>
                <div class="no-data-details" id="payNoDataDiv" style="background-color:#ffffff;display: none">
                    <div class="payNodata"><img src="images/z_r2_c2.jpg" />您还没有任何购买付费记录，赶紧行动吧！
                        <div class="clear"></div>
                    </div>
                </div>
                </dl>
                <div class="clear"></div>

            </div>
            <div class="height"></div>
            <!--分页控制部分，use common code snip-->
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="bcgogoReceivable.do?method=searchBcgogoReceivableOrderList"></jsp:param>
                <jsp:param name="data" value="{startPageNo:1,maxRows:10}"></jsp:param>
                <jsp:param name="jsHandleJson" value="drawBcgogoReceivableOrderHtml"></jsp:param>
                <jsp:param name="dynamical" value="bcogogReceivaleOrders"></jsp:param>
                <jsp:param name="display" value="none"></jsp:param>
            </jsp:include>
        </div>
        <div class="height"></div>
    </div>
</div>
<jsp:include page="commonPayOnline.jsp"/>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>