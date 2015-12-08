<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>订单详情</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/bcgogoReceivable<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/page/autoaccessoryonline/bcgogoReceivableOrder<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SYSTEM_SETTINGS.BCGOGO_RECEIVABLE");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"订单详情");
        $(document).ready(function(){
            $("#qqTalk").multiQQInvoker({
                QQ:[${bcgogoQQ}]
            });
            <c:if test="${not empty receivableErrorInfo}">
            nsDialog.jAlert("${receivableErrorInfo}");
            </c:if>
        });
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>

<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
<jsp:include page="../autoAccessoryOnlineNavi.jsp">
    <jsp:param name="currPage" value="bcgogoOnlineOrder"/>
</jsp:include>
<div class="titBody">
<c:choose>
    <c:when test="${'NON_PAYMENT' eq bcgogoReceivableOrderDTO.status}">
        <div class="step-02">
            <ul>
                <li>1.采购下单</li>
                <li><span>2.付款</span></li>
                <li>3.卖家发货</li>
            </ul>
        </div>
    </c:when>
    <c:when test="${'FULL_PAYMENT' eq bcgogoReceivableOrderDTO.status || 'SHIPPED' eq bcgogoReceivableOrderDTO.status}">
        <div class="step-03">
            <ul>
                <li>1.采购下单</li>
                <li>2.付款</li>
                <li><span>3.卖家发货</span></li>
            </ul>
        </div>
    </c:when>
</c:choose>

<div style="width:100%;font-size: 12px" class="added-management">
    <c:choose>
        <c:when test="${'NON_PAYMENT' eq bcgogoReceivableOrderDTO.status}">
            <div style="padding:10px; line-height:22px;" class="group-notice">
                <div> <strong>当前订单状态：<span class="yellow_color">待支付</span></strong> </div>
                <div> 1.商品采购下单成功，请尽快付款，否则卖家不会安排发货。 </div>
                <div> 2.点击这里<a class="payButton J_hardwarePayBcgogoReceivableOrderBtn">付款</a> </div>
                <div> 3.如果不想继续此订单，您可以<a id="cancelBcgogoReceivableOrderBtn" class="blue_color">取消交易</a>。 </div>
            </div>
        </c:when>
        <c:when test="${'FULL_PAYMENT' eq bcgogoReceivableOrderDTO.status}">
            <div style="padding:10px; line-height:22px;" class="group-notice">
                <div> <strong>当前订单状态：<span class="yellow_color">付款成功，等待卖家发货</span></strong> </div>
                <div> 支付成功，卖家备货中，等待卖家发货。 </div>
            </div>
        </c:when>
        <c:when test="${'SHIPPED' eq bcgogoReceivableOrderDTO.status}">
            <div style="padding:10px; line-height:22px;" class="group-notice">
                <div> <strong>当前订单状态：<span class="yellow_color">卖家已发货</span></strong> </div>
                <div> 卖家已发货，请注意查收。 </div>
            </div>
        </c:when>
        <c:when test="${'CANCELED' eq bcgogoReceivableOrderDTO.status}">
            <div style="padding:10px; line-height:22px;" class="group-notice">
                <div> <strong>当前订单状态：<span class="yellow_color">交易已取消</span></strong> </div>
                <c:if test="${not empty bcgogoReceivableOrderDTO.cancelOptInfo}">
                    <div> 取消类型：${bcgogoReceivableOrderDTO.cancelOptInfo} </div>
                </c:if>
                <div> 取消原因：${bcgogoReceivableOrderDTO.cancelReason} </div>
            </div>
        </c:when>
    </c:choose>


</div>
<div class="booking-management">
    <div class="document-content">
        <h1><span class="font12-normal fr yellow_color">有任何疑问可咨询：<a id="onlineServiceQQ" class="blue_color">在线客服</a></span> 订单信息</h1>
        <div class="roundRadius5"><strong>收货地址：</strong>${bcgogoReceivableOrderDTO.addressDetail}（${bcgogoReceivableOrderDTO.contact} 收）${bcgogoReceivableOrderDTO.mobile}</div>
        <div class="roundRadius5"><strong>卖家信息：</strong>苏州统购 <strong>联系信息：</strong>电话：${bcgogoPhone}  QQ：${bcgogoQQ}<a id="qqTalk"></a></div>
        <div class="roundRadius5"><strong>订单号：</strong>${bcgogoReceivableOrderDTO.receiptNo} <strong>采购方：</strong>${sessionScope.shopName} <strong>购买时间：</strong>${bcgogoReceivableOrderDTO.createdTimeStr} </div>
        <div class="added-management" style="width:972px;">
            <div class="group-content">
                <div class="group-display" style="margin-bottom: 10px;padding-bottom:5px">
                    <!--end search-param-->
                    <div class="search-result">
                        <div class="i_height"></div>
                        <table cellspacing="0" cellpadding="0" class="list-result" style="width:953px;">
                            <colgroup>
                                <col>
                                <col width="100">
                                <col width="100">
                                <col width="100">
                                <col width="130">
                                <%--<col width="100">--%>
                                <col width="100">
                            </colgroup>
                            <thead>
                            <tr>
                                <th style="padding-left:10px;">商品信息</th>
                                <th>单价（元）</th>
                                <th>购买量</th>
                                <th>小计（元）</th>
                                <th style="text-align:center;">应付总额</th>
                                <th style="text-align:center;">付款情况</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="bcgogoReceivableOrderItemDTO" items="${bcgogoReceivableOrderDTO.bcgogoReceivableOrderItemDTOList}" varStatus="status">
                                <tr>
                                    <td class="item-product-infomation" style="width:auto;padding-left:10px;padding-bottom: 10px;">
                                        <div class="product-icon"><a class="blue_color" href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId=${bcgogoReceivableOrderItemDTO.productId}" target="_blank"><img style="width: 60px;height: 60px" src="${bcgogoReceivableOrderItemDTO.imageUrl}"></a></div>
                                        <div class="product-info-details"><a class="blue_color" href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId=${bcgogoReceivableOrderItemDTO.productId}" target="_blank">${bcgogoReceivableOrderItemDTO.productName}<br>${bcgogoReceivableOrderItemDTO.productKind} <c:if test="${not empty bcgogoReceivableOrderItemDTO.productType}">【${bcgogoReceivableOrderItemDTO.productType}】</c:if></a> </div>
                                        <div class="cl"></div>
                                    </td>
                                    <td style="vertical-align:middle;"><fmt:formatNumber  value="${bcgogoReceivableOrderItemDTO.price}"  pattern="###.##"/></td>
                                    <td style="vertical-align:middle;"><fmt:formatNumber  value="${bcgogoReceivableOrderItemDTO.amount}"  pattern="###.##"/></td>
                                    <td style="vertical-align:middle;"><fmt:formatNumber  value="${bcgogoReceivableOrderItemDTO.total}"  pattern="###.##"/></td>
                                    <c:if test="${status.first}">
                                        <td rowspan="${fn:length(bcgogoReceivableOrderDTO.bcgogoReceivableOrderItemDTOList)}" style="text-align: left;padding-left: 5px;border-left: 1px solid #DBDADA;vertical-align:middle;">
                                            数量：<strong><span class="yellow_color"><fmt:formatNumber  value="${bcgogoReceivableOrderDTO.total}"  pattern="###.##"/></span></strong>
                                            <br>
                                            金额：<strong><span class="yellow_color"><fmt:formatNumber  value="${bcgogoReceivableOrderDTO.totalAmount}"  pattern="###.##"/></span></strong>元
                                        </td>
                                        <td rowspan="${fn:length(bcgogoReceivableOrderDTO.bcgogoReceivableOrderItemDTOList)}" style="text-align:center;border-left: 1px solid #DBDADA;vertical-align:middle;border-right:1px solid #DEDEDE;">
                                            <c:choose>
                                                <c:when test="${'NON_PAYMENT' eq bcgogoReceivableOrderDTO.status}">
                                                    <strong><span class="yellow_color">待支付</span></strong>
                                                </c:when>
                                                <c:when test="${'FULL_PAYMENT' eq bcgogoReceivableOrderDTO.status}">
                                                    <strong><span style="color: #008000">已支付</span></strong>
                                                </c:when>
                                                <c:when test="${'SHIPPED' eq bcgogoReceivableOrderDTO.status}">
                                                    <strong><span style="color: #008000">已发货</span></strong>
                                                </c:when>
                                                <c:when test="${'CANCELED' eq bcgogoReceivableOrderDTO.status}">
                                                    <strong><span style="color: #666666">交易已取消</span></strong>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${not empty bcgogoReceivableOrderDTO.bcgogoReceivableOrderPaidRecordDTOList}">
                        <div class="search-result" style="margin-left: 9px;margin-bottom:8px;">
                            <h1 style="color: #4D4D4D;font-size: 12px;">付款记录</h1>
                            <div style="font-size: 12px;">
                                <ul>
                                    <c:forEach var="bcgogoReceivableOrderPaidRecordDTO" items="${bcgogoReceivableOrderDTO.bcgogoReceivableOrderPaidRecordDTOList}">
                                        <li>${bcgogoReceivableOrderPaidRecordDTO.recordPaymentTimeStr}&nbsp;&nbsp;${bcgogoReceivableOrderPaidRecordDTO.paymentMethodValue}&nbsp;<fmt:formatNumber  value="${bcgogoReceivableOrderPaidRecordDTO.recordPaidAmount}"  pattern="###.##"/>元；</li>
                                    </c:forEach>
                                </ul>
                            </div>

                        </div>
                    </c:if>
                </div>
            </div>
        </div>
        <div class="clear"></div>
        <c:if test="${not empty bcgogoReceivableOrderDTO.bcgogoReceivableOrderToBePaidRecordDTO && 'NON_PAYMENT' eq bcgogoReceivableOrderDTO.status}">
            <div style="float: right">
                <div class="payYellow" style="float:none;width:auto;padding-left:50px">
                    <div class="price">应付金额：
                        <a class="yellow_color" style="font-size:16px"><fmt:formatNumber  value="${bcgogoReceivableOrderDTO.currentPayableAmount}"  pattern="###.##"/></a> 元
                    </div>
                </div>
                <div class="pay_pay_btn J_hardwarePayBcgogoReceivableOrderBtn" style="cursor: pointer">付款</div>
            </div>
        </c:if>
        <c:if test="${'FULL_PAYMENT' eq bcgogoReceivableOrderDTO.status ||'SHIPPED' eq bcgogoReceivableOrderDTO.status}">
            <div class="payYellow" style="width:auto;padding-left:50px">
                <div class="price">实付金额：
                    <a class="yellow_color" style="font-size:16px"><fmt:formatNumber  value="${bcgogoReceivableOrderDTO.receivedAmount}"  pattern="###.##"/></a> 元
                </div>
            </div>
        </c:if>
        <c:if test="${'CANCELED' eq bcgogoReceivableOrderDTO.status}">
            <div class="payYellow" style="width:auto;padding-left:50px">
                <div class="price">总额：
                    <a class="yellow_color" style="font-size:16px"><fmt:formatNumber  value="${bcgogoReceivableOrderDTO.totalAmount}"  pattern="###.##"/></a> 元
                </div>
            </div>
        </c:if>

        <div id="bcgogoReceivableOrderPrintBtn" class="shopping_btn" data-order-id="${bcgogoReceivableOrderDTO.id}" style="float:left;">
            <div class="divImg"> <img src="images/print.png" />
                <div class="sureWords" style=" font-size: 12px;font-weight: bold;">打印</div>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="clear"></div>
</div>
    <div class="added-management" style="width: 100%">
        <div class="accessoriesLeft" style="width:100%;">
            <div class="title" style="width:100%;padding: 0"><span style="margin-left: 10px">可购产品一览</span></div>
            <div class="content" style="width:100%">
                <div class="payNodata-ul">
                    <ul>
                        <c:forEach items="${bcgogoProductDTOList}" var="bcgogoProductDTO">
                            <li>
                                <a target="_blank" href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId=${bcgogoProductDTO.id}"><img style="width: 110px;height: 110px;" src="${bcgogoProductDTO.imageCenterDTO.bcgogoProductListImageURL}"/></a>
                                <a target="_blank" href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId=${bcgogoProductDTO.id}" class="blue_color">${bcgogoProductDTO.name}</a>
                            </li>
                        </c:forEach>
                        <div class="clear"></div>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
</div>

<jsp:include page="commonPayOnline.jsp">
    <jsp:param name="bcgogoReceivableOrderDTO" value="${bcgogoReceivableOrderDTO}"/>
</jsp:include>

<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>