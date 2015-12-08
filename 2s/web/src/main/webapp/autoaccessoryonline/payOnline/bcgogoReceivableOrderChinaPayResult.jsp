<%--
  支付成功页面
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <title>支付结果</title>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SYSTEM_SETTINGS.BCGOGO_RECEIVABLE");
    </script>
</head>
<body>
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <jsp:include page="../autoAccessoryOnlineNavi.jsp">
        <jsp:param name="currPage" value="bcgogoOnlineOrderPay"/>
    </jsp:include>
    <div class="i_main">
        <div class="i_mainRight">
            <div class="sms_rechange">
                <div class="sms_rechangeBody">
                    <table cellpadding="0" cellspacing="0" class="sms_rechangeTable">
                        <col width="100">
                        <col/>
                        <tr>
                            <td style="color:#000000;vertical-align:middle">日期</td>
                            <td style="vertical-align:middle">${chinaPayResultDTO.paymentTimeStr}</td>
                        </tr>
                        <tr>
                            <td style="color:#000000;vertical-align:middle">费用类型</td>
                            <td style="vertical-align:middle"><span>${chinaPayResultDTO.paymentTypeStr}</span></td>
                        </tr>
                        <tr>
                            <td style="color:#000000;vertical-align:middle">付款金额</td>
                            <td style="vertical-align:middle"><span class="price">${chinaPayResultDTO.currentPaidAmount}</span>元</td>
                        </tr>
                        <c:choose>
                            <c:when test="${not empty chinaPayResultDTO.errorInfo}">
                                <tr>
                                    <td style="color:#000000;vertical-align:middle">支付结果</td>
                                    <td style="color:red;vertical-align:middle">${chinaPayResultDTO.errorInfo}</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td style="color:#000000;vertical-align:middle">支付结果</td>
                                    <td style="vertical-align:middle" class="rechangeComplete"><input  style="cursor: auto" type="button" value="支付成功"></td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        <tr>
                            <td colspan="2" style="vertical-align:middle">
                                <c:if test="${not empty chinaPayResultDTO.bcgogoReceivableOrderId}">
                                    <a href="bcgogoReceivable.do?method=bcgogoReceivableOrderDetail&bcgogoReceivableOrderId=${chinaPayResultDTO.bcgogoReceivableOrderId}" style="cursor: pointer" class="blue_color">查看订单详细信息</a>&nbsp;&nbsp;&nbsp;
                                </c:if>
                                <a href="bcgogoReceivable.do?method=bcgogoReceivableOrderList" style="cursor: pointer" class="blue_color">返回订单列表</a>&nbsp;&nbsp;&nbsp;
                                <a href="javascript:window.close();" style="cursor: pointer" class="blue_color">关闭当前页</a>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>