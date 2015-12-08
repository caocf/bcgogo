<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>确认订单</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/bcgogoReceivable<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/page/autoaccessoryonline/bcgogoReceivableOrder<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SYSTEM_SETTINGS.BCGOGO_RECEIVABLE");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"确认订单");
        $(document).ready(function(){
            $("#qqTalk").multiQQInvoker({
                QQ:[${bcgogoQQ}]
            });
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
        <div class="step-01">
            <ul>
                <li><span>1.采购下单</span></li>
                <li>2.付款</li>
                <li>3.卖家发货</li>
            </ul>
        </div>
        <div class="booking-management">
            <form:form commandName="bcgogoReceivableOrderDTO" id="bcgogoReceivableOrderForm" action="bcgogoReceivable.do?method=saveBcgogoReceivableOrder" method="post" name="thisform">
                <div class="document-content">
                    <h1><span class="font12-normal fr yellow_color">有任何疑问可咨询：<a id="onlineServiceQQ" class="blue_color">在线客服</a></span> 确认订单信息</h1>
                    <div class="roundRadius5">
                        <div>
                            <div style="float:left;width: 80px;text-align: right">
                                <strong>收货地址：</strong>
                            </div>
                            <div>
                                <select class="txt" style="width:130px;height: 20px;" id="provinceNo" name="provinceNo" autocomplete="off" ></select>
                                <select class="txt" style="width:130px;height: 20px;" id="cityNo" name="cityNo" autocomplete="off" ></select>
                                <select class="txt" style="width:130px;height: 20px;" id="regionNo" name="regionNo" autocomplete="off" ></select>
                                <form:hidden path="province" autocomplete="off" />
                                <form:hidden path="city" autocomplete="off" />
                                <form:hidden path="region" autocomplete="off" />
                                <form:input path="address" cssClass="txt" cssStyle="width: 300px" maxlength="200" autocomplete="off" />
                            </div>
                        </div>
                        <div style="margin-top: 5px;height: 20px">
                            <div style="float:left;width: 80px;text-align: right"><strong>收件人：</strong> </div> <div style="float:left;"><form:input path="contact" cssClass="txt" cssStyle="width: 100px" maxlength="10" autocomplete="off" /> </div>
                            <div style="float:left;width: 80px;text-align: right"><strong>手机号：</strong> </div> <div style="float:left;"><form:input path="mobile" cssClass="txt" cssStyle="width: 100px" maxlength="11" autocomplete="off" /> </div>
                        </div>
                    </div>
                    <div class="roundRadius5"><strong>卖家信息：</strong>苏州统购 <strong>联系信息：</strong>电话：${bcgogoPhone}  QQ：${bcgogoQQ}<a id="qqTalk"></a></div>
                    <div class="added-management" style="width:972px;">
                        <div class="group-content">
                            <div class="group-display" style="margin-bottom: 10px">
                                <!--end search-param-->
                                <div class="search-result">
                                    <div class="i_height"></div>
                                    <table cellspacing="0" cellpadding="0" class="list-result" style="width:953px;">
                                        <colgroup>
                                            <col>
                                            <col width="150">
                                            <col width="150">
                                            <col width="150">
                                        </colgroup>
                                        <thead>
                                        <tr>
                                            <th style="padding-left:10px;">商品信息</th>
                                            <th>单价（元）</th>
                                            <th>购买量</th>
                                            <th>小计（元）</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="bcgogoReceivableOrderItemDTO" items="${bcgogoReceivableOrderDTO.bcgogoReceivableOrderItemDTOList}" varStatus="status">
                                            <tr>
                                                <input type="hidden" id="bcgogoReceivableOrderItemDTOList${status.index}.productId" name="bcgogoReceivableOrderItemDTOList[${status.index}].productId" value="${bcgogoReceivableOrderItemDTO.productId}" autocomplete="off" />
                                                <input type="hidden" id="bcgogoReceivableOrderItemDTOList${status.index}.productPropertyId" name="bcgogoReceivableOrderItemDTOList[${status.index}].productPropertyId" value="${bcgogoReceivableOrderItemDTO.productPropertyId}" autocomplete="off" />
                                                <input type="hidden" class="J_itemPrice" value="${bcgogoReceivableOrderItemDTO.price}" autocomplete="off" />
                                                <td class="item-product-infomation" style="width:auto;padding-left:10px;padding-bottom: 10px;">
                                                    <div class="product-icon"><a class="blue_color" href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId=${bcgogoReceivableOrderItemDTO.productId}" target="_blank"><img style="width: 60px;height: 60px" src="${bcgogoReceivableOrderItemDTO.imageUrl}"></a></div>
                                                    <div class="product-info-details"><a class="blue_color" href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId=${bcgogoReceivableOrderItemDTO.productId}" target="_blank">${bcgogoReceivableOrderItemDTO.productName}<br>${bcgogoReceivableOrderItemDTO.productKind} <c:if test="${not empty bcgogoReceivableOrderItemDTO.productType}">【${bcgogoReceivableOrderItemDTO.productType}】</c:if></a> </div>
                                                    <div class="cl"></div>
                                                </td>
                                                <td style="vertical-align:middle;">${bcgogoReceivableOrderItemDTO.price}</td>
                                                <td style="vertical-align:middle;">

                                                    <div class="card-number" style="padding:0;margin: 0">
                                                        <div class="J_subtractBtn" style="cursor: pointer;float: left;width: 10px;text-align: center;-moz-user-select:none;">-</div>
                                                        <div style="float: left"><input type="text" class="J_ModifyAmount" maxlength="5"  id="bcgogoReceivableOrderItemDTOList${status.index}.amount" name="bcgogoReceivableOrderItemDTOList[${status.index}].amount" value="<fmt:formatNumber  value="${bcgogoReceivableOrderItemDTO.amount}"  pattern="###.##"/>" autocomplete="off" /></div>
                                                        <div class="J_addBtn" style="cursor: pointer;float: left;width: 10px;text-align: center;-moz-user-select:none;">+</div>
                                                    </div>

                                                </td>
                                                <td style="vertical-align:middle;" class="J_itemTotal"><fmt:formatNumber  value="${bcgogoReceivableOrderItemDTO.total}"  pattern="###.##"/></td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="clear"></div>
                    <div style="float: right">
                        <div class="payYellow" style="padding-left:50px;float:none;width: auto;max-width: 500px">
                            <div class="price">应付金额合计：
                                <span class="yellow_color" style="font-size:16px" id="orderTotalAmountSpan"><fmt:formatNumber  value="${bcgogoReceivableOrderDTO.totalAmount}"  pattern="###.##"/></span> 元
                            </div>
                            <div>
                                <strong>寄送至：</strong><span id="addressDetailSpan">${bcgogoReceivableOrderDTO.addressDetail}</span>
                            </div>
                            <div>
                                <strong>收货人：</strong><span id="contactInfoSpan">${bcgogoReceivableOrderDTO.contact} ${bcgogoReceivableOrderDTO.mobile}</span>
                            </div>
                        </div>
                        <div class="pay_pay_btn" id="hardwareSubmitBcgogoReceivableOrderBtn" style="cursor: pointer">提交订单</div>
                    </div>
                    <div class="clear"></div>
                </div>
            </form:form>

            <div class="clear"></div>
        </div>
    </div>
</div>


<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>