<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title id="title">车辆施工</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/secondary/vehicleConstructionFinish<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/secondary/vehicleConstructionFinish<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
</head>

<body>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<div class="i_main">

    <div class="body">
        <div>
            <h1 class="inlineBlock">施工结算附表</h1>
            <div class="inlineBlock">
                <span style="font-family: '宋体'; color: #666666;font-size: 14px; font-weight: bold;">单据号：</span>
                <span style="color: #CB0000;font-weight: bold;">${repairOrderSecondaryDTO.receipt}</span>&#160;&#160;
                <a style="color: #007CDA;font-weight: bold;" href="txn.do?method=getRepairOrder&repairOrderId=${repairOrderSecondaryDTO.repairOrderId}" target="_blank">查看原单</a>
            </div>
        </div>
        <input id="id" name="id" value="${repairOrderSecondaryDTO.id}" type="hidden" autocomplete="off">
        <input id="isPrint" name="isPrint" value="false" type="hidden" autocomplete="off">
        <input id="settledAmount" name="settledAmount" type="hidden" autocomplete="off">
        <input id="accountDebtAmount" name="accountDebtAmount" type="hidden" autocomplete="off">
        <input id="accountDiscount" name="accountDiscount" type="hidden" autocomplete="off">
        <input id="total" name="total" value="${repairOrderSecondaryDTO.accountDebtAmount}" type="hidden" autocomplete="off">

        <div style="border: 1px solid #BEBEBE;padding: 0px 0px 10px 0px;" class="float_clear">
            <div class="title">车辆信息</div>
            <table cellpadding="0" cellspacing="0" width="100%" class="content">
                <colgroup>
                    <col width="10%">
                    <col width="15%">
                    <col width="10%">
                    <col width="15%">
                    <col width="10%">
                    <col width="15%">
                    <col width="10%">
                    <col>
                </colgroup>
                <tr>
                    <td class="left">车牌号</td>
                    <td class="right">${repairOrderSecondaryDTO.vehicleLicense}</td>
                    <td class="left">品牌</td>
                    <td class="right">${repairOrderSecondaryDTO.vehicleBrand}</td>
                    <td class="left">车型</td>
                    <td class="right">${repairOrderSecondaryDTO.vehicleModel}</td>
                    <td class="left">购车日期</td>
                    <td class="right">${repairOrderSecondaryDTO.vehicleBuyDateStr}</td>
                </tr>
                <tr>
                    <td class="left">发动机号</td>
                    <td class="right">${repairOrderSecondaryDTO.vehicleEngineNo}</td>
                    <td class="left">车架号</td>
                    <td class="right">${repairOrderSecondaryDTO.vehicleChassisNo}</td>
                    <td class="left">车辆颜色</td>
                    <td class="right" colspan="3">${repairOrderSecondaryDTO.vehicleColor}</td>
                </tr>
                <tr>
                    <td class="left">客户名</td>
                    <td class="right">${repairOrderSecondaryDTO.customerName}</td>
                    <td class="left">客户手机</td>
                    <td class="right">${repairOrderSecondaryDTO.customerMobile}</td>
                    <td class="left">车主</td>
                    <td class="right">${repairOrderSecondaryDTO.vehicleContact}</td>
                    <td class="left">车主手机</td>
                    <td class="right">${repairOrderSecondaryDTO.vehicleMobile}</td>
                </tr>
                <tr>
                    <td class="left">座机</td>
                    <td class="right">${repairOrderSecondaryDTO.customerLandline}</td>
                    <td class="left">联系地址</td>
                    <td class="right" colspan="5">${repairOrderSecondaryDTO.customerAddress}</td>
                </tr>
                <tr>
                    <td class="left">进厂时间</td>
                    <td class="right">${repairOrderSecondaryDTO.startDateStr}</td>
                    <td class="left">预计出厂</td>
                    <td class="right">${repairOrderSecondaryDTO.endDateStr}</td>
                    <td class="left">车辆里程</td>
                    <td class="right">${repairOrderSecondaryDTO.startMileage}</td>
                    <td class="left">剩余油量</td>
                    <td class="right">${repairOrderSecondaryDTO.fuelNumber}</td>
                </tr>
            </table>

            <div class="title">故障说明</div>
            <div class="content">
                <textarea name="description" autocomplete="off" style="width:600px; height: 161px;padding: 10px;" readonly="readonly">${repairOrderSecondaryDTO.description}</textarea>
                <img src="images/discription.png" alt="图片">
            </div>

            <div class="title">
               <div class="inlineBlock">施工信息</div>
               <div class="inlineBlock" style="margin-left: 30px;">接车人：${repairOrderSecondaryDTO.vehicleHandover}</div>
            </div>
            <table cellpadding="0" cellspacing="0" width="100%" class="content">
                <colgroup>
                    <col width="10%">
                    <col width="15%">
                    <col width="15%">
                    <col width="15%">
                    <col width="15%">
                    <col>
                </colgroup>
                <tr>
                    <td class="left">序号</td>
                    <td class="left">施工内容</td>
                    <td class="left">实际工时</td>
                    <td class="left">工时费</td>
                    <td class="left">施工人</td>
                    <td class="left">备注</td>
                </tr>
                <c:forEach items="${repairOrderSecondaryDTO.serviceDTOs}" var="serviceDTO" varStatus="status">
                    <tr>
                        <td class="right">${status.index}</td>
                        <td class="right">${serviceDTO.service}</td>
                        <td class="right">${serviceDTO.actualHours}</td>
                        <td class="right">${serviceDTO.total}</td>
                        <td class="right">${serviceDTO.workers}</td>
                        <td class="right">${serviceDTO.memo}</td>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="2" class="right" style="text-align: right;padding-right: 10px;">合计：</td>
                    <td class="right">${actualHoursTotal}</td>
                    <td class="right" style="border-right-width: 0px;">${serviceTotal}</td>
                    <td colspan="3"></td>
                </tr>
            </table>

            <div class="title">
                <div class="inlineBlock">销售信息</div>
                <div class="inlineBlock" style="margin-left: 30px;">销售人：${repairOrderSecondaryDTO.productSaler}</div>
            </div>

            <table cellpadding="0" cellspacing="0" width="100%" class="content">
                <colgroup>
                    <col width="15%">
                    <col width="20%">
                    <col width="12%">
                    <col width="9%">
                    <col width="9%">
                    <col width="8%">
                    <col width="8%">
                    <col width="8%">
                    <col>
                </colgroup>
                <tr>
                    <td class="left">商品编号</td>
                    <td class="left">品名</td>
                    <td class="left">品牌/产地</td>
                    <td class="left">规格</td>
                    <td class="left">型号</td>
                    <td class="left">单价</td>
                    <td class="left">数量</td>
                    <td class="left">单位</td>
                    <td class="left">金额</td>
                </tr>
                <c:forEach items="${repairOrderSecondaryDTO.itemDTOs}" var="itemDTO" varStatus="status">
                    <tr>
                        <td class="right">${itemDTO.commodityCode}</td>
                        <td class="right">${itemDTO.productName}</td>
                        <td class="right">${itemDTO.brand}</td>
                        <td class="right">${itemDTO.spec}</td>
                        <td class="right">${itemDTO.model}</td>
                        <td class="right">${itemDTO.price}</td>
                        <td class="right">${itemDTO.amount}</td>
                        <td class="right">${itemDTO.unit}</td>
                        <td class="right">${itemDTO.total}</td>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="5" class="right" style="text-align: right;padding-right: 10px;">合计：</td>
                    <td colspan="6" class="right">${repairOrderSecondaryDTO.salesTotal}</td>
                </tr>
                <tr>
                    <td class="left">备注</td>
                    <td class="right" colspan="10">${repairOrderSecondaryDTO.memo}</td>
                </tr>
            </table>

            <div class="title">其他费用信息</div>
            <table cellpadding="0" cellspacing="0" width="100%" class="content">
                <colgroup>
                    <col width="30%">
                    <col width="30%">
                    <col>
                </colgroup>
                <tr>
                    <td class="left">其他费用</td>
                    <td class="left">金额</td>
                    <td class="left">备注</td>
                </tr>
                <c:forEach items="${repairOrderSecondaryDTO.otherIncomeItemDTOs}" var="itemDTO" varStatus="status">
                    <tr>
                        <td class="right">${itemDTO.name}</td>
                        <td class="right">${itemDTO.price}</td>
                        <td class="right">${itemDTO.memo}</td>
                    </tr>
                </c:forEach>
                <tr>
                    <td class="right" style="text-align: right;padding-right: 10px;">合计：</td>
                    <td class="right" style="border-right-width:0px;">${repairOrderSecondaryDTO.otherIncomeTotal}</td>
                    <td></td>
                </tr>
            </table>
            <div class="title">结算信息</div>
            <div class="float_left float_clear content" style="border:1px solid #D5D4D4;width: 940px;padding: 20px;background: none repeat scroll 0 0 #F2F2F2;">
                <div class="line">应收总额： ${repairOrderSecondaryDTO.total == null ? 0.0 : repairOrderSecondaryDTO.total}元</div>
                <div class="line">实收总计： ${income}元</div>
                <div class="line">挂账金额： ${repairOrderSecondaryDTO.accountDebtAmount == null ? 0.0 : repairOrderSecondaryDTO.accountDebtAmount}元</div>
                <div class="line">优惠总计： ${discount}元</div>
                <table cellpadding="0" cellspacing="0" width="950px;">
                    <colgroup>
                        <col width="12%">
                        <col width="12%">
                        <col width="12%">
                        <col width="12%">
                        <col width="12%">
                        <col width="12%">
                        <col>
                    </colgroup>
                    <tr>
                        <td class="left">结算日期</td>
                        <td class="left">结算人</td>
                        <td class="left">上次结余</td>
                        <td class="left">本次实收</td>
                        <td class="left">本次优惠</td>
                        <td class="left">本次挂账</td>
                    </tr>
                    <c:forEach items="${repairOrderSecondaryDTO.repairOrderSettlementSecondaryDTOs}" var="itemDTO" varStatus="status">
                        <tr>
                            <td>${itemDTO.dateStr}</td>
                            <td>${itemDTO.name}</td>
                            <td>${itemDTO.balance == null ? '--' : itemDTO.balance}</td>
                            <td>${itemDTO.income == null ? '--' : itemDTO.income}</td>
                            <td>${itemDTO.discount == null ? '--' : itemDTO.discount}</td>
                            <td>${itemDTO.debt == null ? '--' : itemDTO.debt}</td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
            <c:if test="${repairOrderSecondaryDTO.status == 'REPAIR_SETTLED'}">
                <div class="jie_suan_repair" style="display: block;"></div>
            </c:if>
            <c:if test="${repairOrderSecondaryDTO.status == 'REPAIR_DEBT'}">
                <div class="repairDebtSettleImg" style="display: block;"></div>
            </c:if>
            <c:if test="${repairOrderSecondaryDTO.status == 'REPAIR_REPEAL'}">
                <div class="zuofei_repair" style="display: block;"></div>
            </c:if>
        </div>

        <div class="float_clear">
            <div class="operate float_left" id="again">
                <img src="images/re-entry.png" alt="重录">
                <div>重录</div>
            </div>
            <c:if test="${repairOrderSecondaryDTO.status != 'REPAIR_REPEAL'}">
                <div class="operate float_left" id="invalid">
                    <img src="images/invalid.png" alt="作废">
                    <div>作废</div>
                </div>
            </c:if>
            <div class="operate float_right" id="print">
                <img src="images/printInput.png" alt="打印">
                <div>打印</div>
            </div>
            <c:if test="${repairOrderSecondaryDTO.status == 'REPAIR_DEBT'}">
                <div class="operate float_right" id="settlement">
                    <img src="images/settlement.jpg" alt="欠款结算">
                    <div>欠款结算</div>
                </div>
            </c:if>
        </div>
    </div>
</div>

<div>
    <iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:800px; top:500px; display:none;" allowtransparency="true" width="450" height="450px" frameborder="0" src="" scrolling="no"></iframe>
</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>