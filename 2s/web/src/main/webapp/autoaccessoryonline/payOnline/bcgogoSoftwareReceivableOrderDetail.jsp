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

        <c:if test="${not empty receivableErrorInfo}">
            nsDialog.jAlert("${receivableErrorInfo}");
        </c:if>
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
        <div class="shoppingCart copy_shoppingCart">
            <div class="divTits">订单号：${bcgogoReceivableOrderDTO.receiptNo}</div>
            <div class="divTits">购买时间：<span>：${bcgogoReceivableOrderDTO.createdTimeStr}</span></div>
            <div class="divTits" style="float:right;"><b class="yellow_color">有任何疑问可咨询：</b><a id="onlineServiceQQ" class="blue_color">在线客服</a></div>
            <div class="cuSearch">
                <div class="cartTop"></div>
                <div class="cartBody">
                    <table cellspacing="0" cellpadding="0" id="myPreBuyOrderTable" class="tab_cuSearch tabSales">
                        <colgroup>
                            <col>
                            <col width="100">
                            <col width="100">
                            <col width="200">
                            <col width="150">
                        </colgroup>
                        <tbody>
                        <tr class="titleBg J_title">
                            <td style="padding-left: 10px">商品信息</td>
                            <td>单价（元）</td>
                            <td>购买量</td>
                            <td style="text-align: center">应付总额（元）</td>
                            <td>付款情况</td>
                        </tr>
                        <tr class="space J_title">
                            <td colspan="5"></td>
                        </tr>
                        <c:forEach var="bcgogoReceivableOrderItemDTO" items="${bcgogoReceivableOrderDTO.bcgogoReceivableOrderItemDTOList}" varStatus="status">
                            <tr class="titBody_Bg" style="cursor:auto">
                                <td style="padding-left:10px;padding-bottom: 10px;vertical-align:middle;">
                                    <div style="float: left"><img style="width: 60px;height: 60px" src="${bcgogoReceivableOrderItemDTO.imageUrl}"></div>
                                    <div style="float: left;margin-left: 10px">${bcgogoReceivableOrderItemDTO.productName}<br>${bcgogoReceivableOrderItemDTO.productKind}</div>
                                </td>
                                <td style="vertical-align:middle;">
                                ${bcgogoReceivableOrderItemDTO.price}
                                    <c:if test="${'YEARLY' eq bcgogoReceivableOrderDTO.chargeType}">
                                        <div class="yellow_color">(按年收费)</div>
                                    </c:if>
                                </td>
                                <td style="vertical-align:middle;"><fmt:formatNumber  value="${bcgogoReceivableOrderItemDTO.amount}"  pattern="###.##"/></td>
                                <td style="vertical-align:middle;text-align: center">
                                    <strong><span class="yellow_color"><fmt:formatNumber  value="${bcgogoReceivableOrderDTO.totalAmount}"  pattern="###.##"/></span></strong>
                                    <c:choose>
                                        <c:when test="${'ONE_TIME' eq bcgogoReceivableOrderDTO.chargeType}">
                                            <c:if test="${'PARTIAL_PAYMENT' eq bcgogoReceivableOrderDTO.status && 'INSTALLMENT' eq bcgogoReceivableOrderDTO.bcgogoReceivableOrderToBePaidRecordDTO.receivableMethod}">
                                                <div class="gray_color">(本次应付第${bcgogoReceivableOrderDTO.currentPeriodNumberInfo}期,共<fmt:formatNumber  value="${bcgogoReceivableOrderDTO.currentPayableAmount}"  pattern="###.##"/>元)</div>
                                            </c:if>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="gray_color">(下一年应付)</div>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="vertical-align:middle;border-right:1px solid #DEDEDE;">
                                    <c:choose>
                                        <c:when test="${'ONE_TIME' eq bcgogoReceivableOrderDTO.chargeType}">
                                            <c:choose>
                                                <c:when test="${'NON_PAYMENT' eq bcgogoReceivableOrderDTO.status || 'PARTIAL_PAYMENT' eq bcgogoReceivableOrderDTO.status}">
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
                                        </c:when>
                                        <c:otherwise>
                                            <strong><span class="yellow_color">第1年免费,第2年待支付</span></strong>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <tr style="border-bottom:1px solid #DEDEDE;">
                            <td colspan="5"></td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="clear i_height"></div>
                    <c:if test="${'ONE_TIME' eq bcgogoReceivableOrderDTO.chargeType}">
                        <c:choose>
                            <c:when test="${'NON_PAYMENT' eq bcgogoReceivableOrderDTO.status}">
                                <div class="paySucess-content" style="width:960px;margin-bottom:16px;border:0px;overflow-x:auto;color: #000000">
                                    <div style="width:90px;float: left">
                                        选择付款方式：
                                    </div>
                                    <div style="width:870px;float: left">
                                        <div style="height: 20px;line-height: 20px">
                                            <label style="width: 200px"><input type="radio" autocomplete="off" name="receivableMethod" value="FULL" checked="true"/>全额付款</label>
                                        </div>
                                        <div style="height: 20px;line-height: 20px">
                                            <label style="width: 200px"><input type="radio" autocomplete="off" name="receivableMethod" value="INSTALLMENT"/>分期付款</label>
                                            <label id="instalmentPaySelect" style="display:none;" class="lblPay">分期期数：
                                                <input id="instalmentSelectName" value="" type="hidden"/>
                                                <select id="instalmentSelect" class="txt" autocomplete="off">
                                                    <c:forEach items="${algorithmList}" var="item">
                                                        <option value="${item.id}" title="${item.name}">${item.name}</option>
                                                    </c:forEach>
                                                </select>
                                            </label>
                                        </div>
                                    </div>
                                    <div id="instalmentPlanAlgorithmDiv" style="display: none">

                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${not empty bcgogoReceivableOrderDTO.instalmentPlanDTO && not empty bcgogoReceivableOrderDTO.instalmentPlanDTO.instalmentPlanItemDTOList}">
                                    <div class="paySucess-content"  style="width:960px;margin-bottom:16px;border:0px;overflow-x:auto;color: #000000">
                                        <div style="float: left;width: 70px"><h1>付款期数</h1></div><div>共<span style="font-weight:bold">${bcgogoReceivableOrderDTO.instalmentPlanDTO.periods}</span>期,已付总额:<span style="color: #008000;font-weight:bold">${bcgogoReceivableOrderDTO.receivedAmount}</span>元,未付总额:<span class="yellow_color" style="font-weight:bold">${bcgogoReceivableOrderDTO.receivableAmount}</span>元.</div>
                                        <table  style="table-layout:fixed;width: 100%" border="0" cellspacing="0" class="equal2">
                                            <colgroup>
                                                <col width="70">
                                                <c:forEach var="instalmentPlanItemDTO" items="${bcgogoReceivableOrderDTO.instalmentPlanDTO.instalmentPlanItemDTOList}">
                                                    <col width="70">
                                                </c:forEach>
                                            </colgroup>
                                            <tr>
                                                <td class="equal2_title">期    数</td>
                                                <c:forEach var="instalmentPlanItemDTO" items="${bcgogoReceivableOrderDTO.instalmentPlanDTO.instalmentPlanItemDTOList}">
                                                    <td>${instalmentPlanItemDTO.periodNumber}期</td>
                                                </c:forEach>
                                            </tr>
                                            <tr>
                                                <td class="equal2_title">支付比例</td>
                                                <c:forEach var="instalmentPlanItemDTO" items="${bcgogoReceivableOrderDTO.instalmentPlanDTO.instalmentPlanItemDTOList}">
                                                    <td>${instalmentPlanItemDTO.proportion * 100}%</td>
                                                </c:forEach>
                                            </tr>
                                            <tr>
                                                <td class="equal2_title">付款金额</td>
                                                <c:forEach var="instalmentPlanItemDTO" items="${bcgogoReceivableOrderDTO.instalmentPlanDTO.instalmentPlanItemDTOList}">
                                                    <td>&yen;<fmt:formatNumber  value="${instalmentPlanItemDTO.currentAmount}"  pattern="###.##"/></td>
                                                </c:forEach>
                                            </tr>
                                            <tr>
                                                <td class="equal2_title">付款状态</td>
                                                <c:forEach var="instalmentPlanItemDTO" items="${bcgogoReceivableOrderDTO.instalmentPlanDTO.instalmentPlanItemDTOList}">
                                                    <c:choose>
                                                        <c:when test="${'FULL_PAYMENT' eq instalmentPlanItemDTO.status}">
                                                            <td><span class="paySucess">已付</span></td>
                                                        </c:when>
                                                        <c:when test="${'PARTIAL_PAYMENT' eq instalmentPlanItemDTO.status}">
                                                            <td><strong><span style="color: #008000">已付&yen;<fmt:formatNumber  value="${instalmentPlanItemDTO.paidAmount}"  pattern="###.##"/></span></strong></td>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <td>待付</td>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </tr>
                                            <tr>
                                                <td class="equal2_title" style="line-height:18px">付款截止</td>
                                                <c:forEach var="instalmentPlanItemDTO" items="${bcgogoReceivableOrderDTO.instalmentPlanDTO.instalmentPlanItemDTOList}">
                                                    <c:choose>
                                                        <c:when test="${'FULL_PAYMENT' eq instalmentPlanItemDTO.status}">
                                                            <td style="line-height:18px">--</td>
                                                        </c:when>
                                                        <c:when test="${'PARTIAL_PAYMENT' eq instalmentPlanItemDTO.status}">
                                                            <td style="line-height:18px">${instalmentPlanItemDTO.endTimeStr}</td>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <td style="line-height:18px">${instalmentPlanItemDTO.endTimeStr}</td>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </tr>
                                        </table>
                                    </div>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${not empty bcgogoReceivableOrderDTO.bcgogoReceivableOrderPaidRecordDTOList}">
                            <div class="paySucess-content" style="color: #000000;width: 960px;border:0px;margin-top:-15px">
                                <h1>付款记录</h1>
                                <div>
                                    <ul>
                                        <c:forEach var="bcgogoReceivableOrderPaidRecordDTO" items="${bcgogoReceivableOrderDTO.bcgogoReceivableOrderPaidRecordDTOList}">
                                            <li>${bcgogoReceivableOrderPaidRecordDTO.recordPaymentTimeStr}&nbsp;&nbsp;${"DOOR_CHARGE" eq bcgogoReceivableOrderPaidRecordDTO.paymentMethod?"现金支付":"银联支付"}&nbsp;<fmt:formatNumber  value="${bcgogoReceivableOrderPaidRecordDTO.recordPaidAmount}"  pattern="###.##"/>元；</li>
                                        </c:forEach>
                                    </ul>
                                </div>

                            </div>
                        </c:if>
                    </c:if>
                </div>
                <div class="cartBottom"></div>
            </div>
            <c:if test="${'ONE_TIME' eq bcgogoReceivableOrderDTO.chargeType}">
                <c:if test="${not empty bcgogoReceivableOrderDTO.bcgogoReceivableOrderToBePaidRecordDTO && ('NON_PAYMENT' eq bcgogoReceivableOrderDTO.status || 'PARTIAL_PAYMENT' eq bcgogoReceivableOrderDTO.status)}">
                    <div class="clear"></div>
                    <c:if test="${'NON_PAYMENT' eq bcgogoReceivableOrderDTO.status}">
                        <div class="payYellow" style="width: 150px" id="fullPaymentTotalDiv">
                            <div class="price">应付金额：
                                <a class="yellow_color" style="font-size:16px"><fmt:formatNumber  value="${bcgogoReceivableOrderDTO.currentPayableAmount}"  pattern="###.##"/></a> 元
                            </div>
                        </div>
                        <div class="payYellow" style="display: none" id="installmentPaymentTotalDiv">
                            <div class="price">本期应付：
                                <a class="yellow_color" style="font-size:16px" id="firstInstallmentPayment"></a> 元  &nbsp;&nbsp;&nbsp;&nbsp;
                                我要支付：<input name="paymentAmount" style="width: 60px" type="text" value="" autocomplete="off" maxlength="7"/> 元
                            </div>
                            <div class="pay-warning">您可以支付本期应付金额，也可以提前还款！</div>
                        </div>
                        <div class="clear"></div>
                        <div class="pay_pay_btn" id="firstSoftwarePayBcgogoReceivableOrderBtn" style="cursor: pointer">付款</div>
                    </c:if>
                    <c:if test="${'INSTALLMENT' eq bcgogoReceivableOrderDTO.bcgogoReceivableOrderToBePaidRecordDTO.receivableMethod && 'PARTIAL_PAYMENT' eq bcgogoReceivableOrderDTO.status && not empty bcgogoReceivableOrderDTO.instalmentPlanDTO}">
                        <div class="payYellow">
                            <div class="price">本期应付：
                                <a class="yellow_color" style="font-size:16px"><fmt:formatNumber  value="${bcgogoReceivableOrderDTO.currentPayableAmount}"  pattern="###.##"/></a> 元  &nbsp;&nbsp;&nbsp;&nbsp;
                                我要支付：<input name="paymentAmount" style="width: 60px" type="text" value="<fmt:formatNumber  value="${bcgogoReceivableOrderDTO.currentPayableAmount}"  pattern="###.##"/>" autocomplete="off" maxlength="7" /> 元
                            </div>
                            <div class="pay-warning">您可以支付本期应付金额，也可以提前还款！</div>
                        </div>
                        <div class="clear"></div>
                        <div class="pay_pay_btn" id="installmentSoftwarePayBcgogoReceivableOrderBtn" style="cursor: pointer">付款</div>
                    </c:if>
                    <c:if test="${'UNCONSTRAINED' eq bcgogoReceivableOrderDTO.bcgogoReceivableOrderToBePaidRecordDTO.receivableMethod && 'PARTIAL_PAYMENT' eq bcgogoReceivableOrderDTO.status}">
                        <div class="payYellow" style="width: 150px">
                            <div class="price">应付金额：<a class="yellow_color" style="font-size:16px"><fmt:formatNumber  value="${bcgogoReceivableOrderDTO.currentPayableAmount}"  pattern="###.##"/></a> 元</div>
                        </div>
                        <div class="clear"></div>
                        <div class="pay_pay_btn" id="otherSoftwarePayBcgogoReceivableOrderBtn" style="cursor: pointer">付款</div>
                    </c:if>
                </c:if>
            </c:if>

        </div>
        <div class="height"></div>
    </div>
</div>

<jsp:include page="commonPayOnline.jsp">
    <jsp:param name="bcgogoReceivableOrderDTO" value="${bcgogoReceivableOrderDTO}"/>
</jsp:include>

<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>