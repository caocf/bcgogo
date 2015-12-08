<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>对账单查看</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/statementOrderFinish<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $().ready(function () {
            if ($("#print").val() == "true") {
                redirectPrintBill();
            }
        });
    </script>
</head>

<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" id="id" value="${statementAccountOrderDTO.id}">
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<input type="hidden" id="customerOrSupplierId" name="customerOrSupplierId"
       value="${statementAccountOrderDTO.customerOrSupplierId}"/>

<div class="i_main">

<c:if test="${orderType=='CUSTOMER_STATEMENT_ACCOUNT'}">
    <jsp:include page="/customer/customerNavi.jsp">
        <jsp:param name="currPage" value="customerData"/>
    </jsp:include>
    <div class="titBodys">
        <a class="normal_btn" href="#" onclick="redirectUncleUser('customerBill')">客户详细信息</a>
        <a class="hover_btn" href="#" onclick="redirectCustomerBill('customerBill')">客户对账单</a>
    </div>
</c:if>
<c:if test="${orderType=='SUPPLIER_STATEMENT_ACCOUNT'}">
    <jsp:include page="/customer/supplierNavi.jsp">
        <jsp:param name="currPage" value="searchSupplier"/>
    </jsp:include>
    <div class="titBodys">
        <a class="normal_btn" href="#" onclick="redirectUncleUser('supplierBill')">供应商详细信息</a>
        <a class="hover_btn" href="#" onclick="redirectCustomerBill('supplierBill')">供应商对账单</a>

        <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_COMMENT.RECORD">
            <c:if test="${supplierShopId!=null }">
                <a class="normal_btn" href="supplier.do?method=redirectSupplierComment&paramShopId=${supplierShopId}">供应商评价详情</a>
            </c:if>
        </bcgogo:hasPermission>
    </div>
</c:if>

<div class="i_mainRight" id="i_mainRight">
<ul class="yinye_title clear">
    <li class="danju">
        单据号： <strong>${statementAccountOrderDTO.receiptNo}</strong>
    </li>
</ul>
<div class="clear"></div>
<div class="tuihuo_tb">
<table>
    <col width="80"/>
    <tr>
        <c:if test="${orderType=='SUPPLIER_STATEMENT_ACCOUNT'}">
            <td>供应商信息</td>
        </c:if>
        <c:if test="${orderType=='CUSTOMER_STATEMENT_ACCOUNT'}">
            <td>客户信息</td>
        </c:if>

    </tr>
</table>
<table class="clear" id="tb_tui">
    <col width="10%"/>
    <col width="20%"/>
    <col width="10%"/>
    <col width="20%"/>
    <col width="10%"/>
    <col width="20%"/>
    <col/>
    <c:if test="${orderType=='SUPPLIER_STATEMENT_ACCOUNT'}">
        <tr>
            <td class="tabBg">供应商</td>
            <td>${statementAccountOrderDTO.customerOrSupplier}</td>
            <td class="tabBg">联系人</td>
            <td>${statementAccountOrderDTO.contact}</td>
            <td class="tabBg">联系电话</td>
            <td>${statementAccountOrderDTO.mobile}</td>
        </tr>
        <tr>
            <td class="td_title">地址</td>
            <td colspan="5">${statementAccountOrderDTO.address}</td>
        </tr>
    </c:if>
    <c:if test="${orderType=='CUSTOMER_STATEMENT_ACCOUNT'}">
        <tr>
            <td class="tabBg">客户名</td>
            <td>${statementAccountOrderDTO.customerOrSupplier}</td>
            <td class="tabBg">联系人</td>
            <td>${statementAccountOrderDTO.contact}</td>
            <td class="tabBg">联系电话</td>
            <td>${statementAccountOrderDTO.mobile}</td>
        </tr>
        <tr>
            <td class="td_title">地址</td>
            <td colspan="5">${statementAccountOrderDTO.address}</td>
        </tr>
    </c:if>
</table>
<table>
    <col width="80"/>
    <col width="200"/>
    <tr>
        <td>对账单据信息</td>
        <td>对账合计：${statementAccountOrderDTO.orderTotalStr}</td>
    </tr>
</table>

<div class="shelvesed clear" style="width:480px;margin-left: 10px;">
    <div class="topTitle">收入单据列表</div>
    <div id="receivableDiv" style="width:480px;height:250px;overflow-y:auto;overflow-x:hidden;">
        <table cellpadding="0" cellspacing="0" class="tabShelvesed duizhang_td" style="width:100%;margin:0">
            <col width="75">
            <col width="75">
            <col width="95">
            <col width="60">
            <col width="50">
            <col width="50">
            <col width="50">
            <tr class="bg">
                <td style="padding-left:8px;">日期</td>
                <td style="text-align: left;">类型</td>
                <td style="text-align: left;">单据号</td>
                <td style="text-align: left;">单据金额</td>
                <td style="text-align: left;">实收</td>
                <td style="text-align: left;">优惠</td>
                <td style="text-align: left;">挂账</td>
            </tr>
            <c:forEach items="${statementAccountOrderDTO.orderDTOList}" var="itemDTO" varStatus="status">
                <c:if
                        test="${itemDTO.orderDebtType == 'CUSTOMER_DEBT_RECEIVABLE' || itemDTO.orderDebtType == 'SUPPLIER_DEBT_RECEIVABLE'}">
                    <tr class="bg">
                        <td>
                                ${itemDTO.vestDateStr}
                        </td>
                        <td>
                                ${itemDTO.orderTypeStr}
                        </td>
                        <td>
                                ${itemDTO.receiptNo}
                        </td>
                        <td>
                                ${itemDTO.total}
                        </td>
                        <td>
                                ${itemDTO.settledAmount}
                        </td>
                        <td>
                                ${itemDTO.discount}
                        </td>
                        <td>
                                ${itemDTO.statementAmount}
                        </td>
                    </tr>
                </c:if>
            </c:forEach>
        </table>
    </div>
    <p class="heji">应收合计：<strong class="red_color">${statementAccountOrderDTO.totalReceivable}</strong>元</p>

    <div class="height clear"></div>
</div>
<div class="shelvesed shelves" style="width:480px;">
    <div class="topTitle">支出单据列表</div>
    <div id="payableDiv" style="width:480px;height:250px;overflow-y:auto;overflow-x:hidden;">
        <table cellpadding="0" cellspacing="0" class="tabShelvesed duizhang_td " style="width:100%;margin:0">
            <col width="75">
            <col width="75">
            <col width="95">
            <col width="60">
            <col width="50">
            <col width="50">
            <col width="50">
            <tr class="bg">
                <td style="padding-left:8px;">日期</td>
                <td style="text-align: left;">类型</td>
                <td style="text-align: left;">单据号</td>
                <td style="text-align: left;">单据金额</td>
                <td style="text-align: left;">实收</td>
                <td style="text-align: left;">优惠</td>
                <td style="text-align: left;">挂账</td>
            </tr>
            <c:forEach items="${statementAccountOrderDTO.orderDTOList}" var="itemDTO" varStatus="status">
                <c:if
                        test="${itemDTO.orderDebtType == 'CUSTOMER_DEBT_PAYABLE' || itemDTO.orderDebtType == 'SUPPLIER_DEBT_PAYABLE'}">
                    <tr class="bg">
                        <td>
                                ${itemDTO.vestDateStr}
                        </td>
                        <td>
                                ${itemDTO.orderTypeStr}
                        </td>
                        <td>
                                ${itemDTO.receiptNo}
                        </td>
                        <td>
                                ${itemDTO.total}
                        </td>
                        <td>
                                ${itemDTO.settledAmount}
                        </td>
                        <td>
                                ${itemDTO.discount}
                        </td>
                        <td>
                                ${itemDTO.statementAmount}
                        </td>
                    </tr>
                </c:if>
            </c:forEach>
        </table>
    </div>
    <p class="heji">应付合计：<strong class="blue_color">${statementAccountOrderDTO.totalPayable}</strong>元</p>

    <div class="height clear"></div>
</div>


<table>
    <col width="80"/>
    <tr>
        <td>结算信息</td>
    </tr>
</table>

<div class="info-info clearfix">
    <table class="fl info-outline">
        <tr>
            <td class="info-title">结算人：</td>
            <td>${statementAccountOrderDTO.salesMan}
            </td>
        </tr>
        <tr>
            <td class="info-title">结算日期：</td>
            <td>${statementAccountOrderDTO.vestDateStr}</td>
        </tr>
        <tr>
            <td class="info-title">
                <c:if test="${statementAccountOrderDTO.bankCheckAmount>0}">
                    附加信息：
                </c:if>
            </td>
            <td>
                <c:if test="${statementAccountOrderDTO.bankCheckAmount>0}">
                    使用的支票号：${statementAccountOrderDTO.bankCheckNo}<br/>
                </c:if>
            </td>
        </tr>
    </table>

    <div class="fr info-summary">
        <table>
            <tr>
                <td class="info-title">对账总计：</td>
                <td colspan="2"
                    class="money">${statementAccountOrderDTO.total!=null?statementAccountOrderDTO.orderTotalStr:""}</td>
                <td class="money-unit">元</td>
            </tr>
            <tr>
                <td class="info-title">本期优惠：</td>
                <td colspan="2" class="money">${statementAccountOrderDTO.discount}</td>
                <td class="money-unit">元</td>
            </tr>
            <tr>
                <td class="info-title">本期挂账：</td>
                <td class="money" colspan="2" id="guazhang">${statementAccountOrderDTO.debt}</td>
                <td class="money-unit">元</td>
            </tr>
            <tr>
                <td class="info-title">本期收支：</td>
                <td colspan="2" class="money"
                    style="color:#FF6700;font-weight:bold;">${statementAccountOrderDTO.settledAmountStr}</td>
                <td class="money-unit">元</td>
            </tr>
            <tr>
                <td class="info-title" style="font-weight:normal;">
                    <c:if
                            test="${statementAccountOrderDTO.cashAmount>0||statementAccountOrderDTO.bankAmount>0||statementAccountOrderDTO.bankCheckAmount>0||statementAccountOrderDTO.depositAmount>0||statementAccountOrderDTO.memberAmount>0||statementAccountOrderDTO.statementAmount>0}">
                        其中：
                    </c:if>
                </td>
                <td class="info-sub-title">
                    <c:if test="${statementAccountOrderDTO.cashAmount>0}">
                        现金：<br />
                    </c:if>
                    <c:if test="${statementAccountOrderDTO.bankAmount>0}">
                        银行卡：<br />
                    </c:if>
                    <c:if test="${statementAccountOrderDTO.bankCheckAmount>0}">
                        支票：<br />
                    </c:if>
                    <c:if test="${statementAccountOrderDTO.depositAmount>0}">
                        预付款：<br />
                    </c:if>
                    <c:if test="${statementAccountOrderDTO.memberAmount>0}">
                        会员：<br />
                    </c:if>
                    <c:if test="${statementAccountOrderDTO.statementAmount>0}">
                        对账：<br />
                    </c:if>
                </td>
                <td class="money">
                    <c:if test="${statementAccountOrderDTO.cashAmount>0}">
                        ¥${statementAccountOrderDTO.cashAmount}<br/>
                    </c:if>
                    <c:if test="${statementAccountOrderDTO.bankAmount>0}">
                        ¥${statementAccountOrderDTO.bankAmount}<br/>
                    </c:if>
                    <c:if test="${statementAccountOrderDTO.bankCheckAmount>0}">
                        ¥${statementAccountOrderDTO.bankCheckAmount}<br/>
                    </c:if>
                    <c:if test="${statementAccountOrderDTO.depositAmount>0}">
                        ¥${statementAccountOrderDTO.depositAmount}<br/>
                    </c:if>
                    <c:if test="${statementAccountOrderDTO.memberAmount>0}">
                        ¥${statementAccountOrderDTO.memberAmount}<br/>
                    </c:if>
                    <c:if test="${statementAccountOrderDTO.statementAmount>0}">
                        ¥${statementAccountOrderDTO.statementAmount}<br/>
                    </c:if>
                </td>
                <td></td>

            </tr>
        </table>
    </div>
</div>

</div>
<div class="clear"></div>


<div class="shopping_btn" style="float:right; clear:right;">

    <c:if test="${orderType=='CUSTOMER_STATEMENT_ACCOUNT'}">

        <div class="return_list_div" id="returnListDiv" onclick="redirectCustomerBill('customerBill')">
            <input type="button" onfocus="this.blur();"/>

            <div class="return_list_text_div" id="createSalesReturn">返回</div>
        </div>
        <div class="btn_div_Img" id="print_div" onclick="redirectPrintBill()">
            <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
            <input type="hidden" name="print" id="print" value="${statementAccountOrderDTO.print}">

            <div class="optWords">打印</div>
        </div>
    </c:if>
    <c:if test="${orderType=='SUPPLIER_STATEMENT_ACCOUNT'}">
        <div class="return_list_div" onclick="redirectCustomerBill('supplierBill')">
            <input type="button" onfocus="this.blur();"/>

            <div class="return_list_text_div">返回</div>
        </div>
        <div class="btn_div_Img" id="print_div" onclick="redirectPrintBill()">
            <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
            <input type="hidden" name="print" id="print" value="${statementAccountOrderDTO.print}">

            <div class="optWords">打印</div>
        </div>
    </c:if>


</div>


</div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
