<%--
  Created by IntelliJ IDEA.
  User: zhoudongming
  Date: 12-7-12
  Time: 上午8:54
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>洗车美容</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/carWashFinish<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/supplierLook<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>

    <style type="text/css">
        .carWashSelectStyles {
            width: 120px;
        }

        .carWashSelectStyles, x:-moz-any-link {
            position: absolute;
            margin-top: -10px;
        }

            /*.carWashSelectStyles option{width:100px;}*/
    </style>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/invoicesolr<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/carWash<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
          src="js/statementAccount/statementAccountUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"VEHICLE_CONSTRUCTION_WASH");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
        var submitResultMsg = "${resultMsg}";
        function selChange(num, dom) {
            var idStr = dom.id.split(".");
            var price = parseFloat(jQuery("#" + idStr[0] + "\\.price").val());
            var total = parseFloat(jQuery("#total").val());
            price = isNaN(price) ? 0 : price;
            total = isNaN(total) ? 0 : total;
            if (num == "MONEY") {
                jQuery(dom).parent().next().children().eq(0).css({visibility:"visible"});
                jQuery(dom).next().css({visibility:"hidden"});
                $("#" + dom.id.split(".")[0] + "\\.hiddenConsumeType").html("MONEY");
                jQuery("#total").val(price + total);
            }
            if (num == "TIMES") {
                jQuery(dom).parent().next().children().eq(0).css({visibility:"hidden"});
                jQuery(dom).next().css({visibility:"visible"});
                $("#" + dom.id.split(".")[0] + "\\.hiddenConsumeType").html("TIMES");
                jQuery("#total").val(total - price);
            }
        }

        function memberChange() {
            var defaultItems = jQuery("select[id$='.serviceId']");
            var total = 0;
            for (var i = 0; i < defaultItems.length; i++) {

                defaultItemValue = defaultItems[i].options[defaultItems[i].options.selectedIndex].value;
                var type = $("#" + $(defaultItems[i])[0].id.split(".")[0] + "\\.hiddenConsumeType").html();
                if (null != type && "" != type) {
                    if ("TIMES" == type) {
                        jQuery(defaultItems[i]).parent().next().children().eq(0).val('TIMES');
                        jQuery(defaultItems[i]).parent().next().children().eq(1).css({visibility:"visible"});
                        jQuery(defaultItems[i]).parent().next().next().children().eq(0).css({visibility:"hidden"});
                    }
                    else {
                        jQuery(defaultItems[i]).parent().next().children().eq(0).val('MONEY');
                        jQuery(defaultItems[i]).parent().next().children().eq(1).css({visibility:"hidden"});
                        jQuery(defaultItems[i]).parent().next().next().children().eq(0).css({visibility:"visible"});
//                    var itemValue = parseFloat(jQuery("select[name=serviceDTOSelect] option[value=" + defaultItemValue + "]").text());
                        var itemValue = parseFloat($("#" + $(defaultItems[i])[0].id.split(".")[0] + "\\.price").val());
                        itemValue = isNaN(itemValue) ? 0 : itemValue;
                        total = total + itemValue;
                    }
                }
                else {
                    if ((jQuery("select[name=serviceDTOSurplusTimesSelect] option[value=" + defaultItemValue + "]").text() != 0) && (jQuery("select[name=memberServiceIsOverDueSelect] option[value=" + defaultItemValue + "]").text() == 'false')) {
                        jQuery(defaultItems[i]).parent().next().children().eq(0).val('TIMES');
                        jQuery(defaultItems[i]).parent().next().children().eq(1).css({visibility:"visible"});
                        jQuery(defaultItems[i]).parent().next().next().children().eq(0).css({visibility:"hidden"});
                    }
                    else {
                        jQuery(defaultItems[i]).parent().next().children().eq(0).val('MONEY');
                        jQuery(defaultItems[i]).parent().next().children().eq(1).css({visibility:"hidden"});
                        jQuery(defaultItems[i]).parent().next().next().children().eq(0).css({visibility:"visible"});
//                    var itemValue = parseFloat(jQuery("select[name=serviceDTOSelect] option[value=" + defaultItemValue + "]").text());
                        var itemValue = parseFloat($("#" + $(defaultItems[i])[0].id.split(".")[0] + "\\.price").val());
                        itemValue = isNaN(itemValue) ? 0 : itemValue;
                        total = total + itemValue;
                    }
                }
            }
            jQuery("#total").val(total.toFixed(2));
        }


        jQuery().ready(function() {
            if (!GLOBAL.Lang.isEmpty(submitResultMsg)) {
                if (submitResultMsg == "success") {
                    showMessage.fadeMessage("40%", "20%", 2000, 2000, "结算成功");
                } else if (submitResultMsg == "failure") {
                    nsDialog.jAlert("结算失败，请重试或联系客服。");
                }
            }
            if (!$("#id").val()) {
                memberChange();
            }
            jQuery("select[id$='.serviceId']").bind("change", function(e) {
                var e = e || event;
                var target = e.srvElement || e.target;
                var idStr = jQuery(target).attr("id");
                var idSplit = idStr.split(".");
                memberChange();
                if (jQuery("select[name=memberServiceSelect] option[value=" + target.value + "]").length > 0) {
                    jQuery("#" + idSplit[0] + "\\.t").html(jQuery("select[name=memberServiceSelect] option[value=" + target.value + "]").text());
                }
                else {
                    jQuery("#" + idSplit[0] + "\\.t").html(0);
                }
                jQuery("#" + idSplit[0] + "\\.price").val(jQuery("select[name=serviceDTOSelect] option[value=" + target.value + "]").text());
                memberChange();
            });

            $(".servicePrice").live("blur", function() {
                $(this).val(($(this).val() * 1).toFixed(2));
            });
            if ($.trim($("#customerId").val())) {
                var orderAttrs = ["brand","model","year","contact","engine","customer","mobile","landLine","fuelNumber"];
                var disableOrderAttrs = function(idName) {
                    if ($.trim($("#" + idName).val())) {
                        $("#" + idName).attr("disabled", true);
                    }
                }
                for (var i = 0,len = orderAttrs.length; i < len; i++) {
                    disableOrderAttrs(orderAttrs[i]);
                }
            }
        });
        function openNewWashBeautyOrder() {
            window.open($("#basePath").val() + "washBeauty.do?method=createWashBeautyOrder", "_blank");
        }

    </script>

    <%
        boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
    %>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<input id="orderType" name="orderType" value="washBeauty" type="hidden"/>
<input id="disabledServiceInfoStr" name="disabledServiceInfoStr" value="${disabledServiceInfoStr}" type="hidden"/>

<input id="statementAccountOrderId" name="statementAccountOrderId" value="${washBeautyOrderDTO.statementAccountOrderId}" type="hidden"/>
<div class="i_main clear">
<div class="mainTitles" style="width: 98%">
    <bcgogo:permission>
        <bcgogo:if resourceType="logic" permissions="WEB.VERSION.DISABLE_VEHICLE_CONSTRUCTION">
            <jsp:include page="vehicleConstructionNavi.jsp">
                <jsp:param name="currPage" value="washOrder"/>
                <jsp:param name="receiptNo" value="${washBeautyOrderDTO.receiptNo}"/>
                <jsp:param name="orderId" value="${washBeautyOrderDTO.id}"/>
            </jsp:include>
        </bcgogo:if>
        <bcgogo:else>
            <jsp:include page="vehicleNavi.jsp">
                <jsp:param name="currPage" value="washOrder"/>
                <jsp:param name="receiptNo" value="${washBeautyOrderDTO.receiptNo}"/>
                <jsp:param name="orderId" value="${washBeautyOrderDTO.id}"/>
            </jsp:include>
        </bcgogo:else>
    </bcgogo:permission>
</div>

<div class="i_mainRight" id="i_mainRight">
<form:form commandName="washBeautyOrderDTO" name="thisform" id="washBeautyOrderForm"
           method="post" action="">
<form:hidden path="id" value="${washBeautyOrderDTO.id}"/>
<form:hidden path="memberDTO.id" value="${washBeautyOrderDTO.memberDTO.id}"/>
<form:checkbox id="sendMemberSms" path="sendMemberSms" style="display:none;"/>
<input type="hidden" name="print" id="isPrint" value="${washBeautyOrderDTO.print}">
<input type="hidden" id="memberCardId" value="${washBeautyOrderDTO.memberDTO.id}"/>

<%--<ul class="yinye_title clear clearfix">--%>
    <%--<li id="fencount">洗车美容</li>--%>
    <%--<li class="danju">--%>
        <%--单据号： <strong>${washBeautyOrderDTO.receiptNo}</strong>--%>
    <%--</li>--%>
    <%--<c:if test="${washBeautyOrderDTO.appointOrderId != null}">--%>
        <%--<a class="blue_color" style="float:right;font-size: 14px;margin-top: 5px;" id="showAppointOrderDetail" href="appoint.do?method=showAppointOrderDetail&appointOrderId=${washBeautyOrderDTO.appointOrderId}">查看预约单></a>--%>
    <%--</c:if>--%>
<%--</ul>--%>

<div class="tuihuo_tb">

<table>
    <colgroup>
        <col width="80">
    </colgroup>
    <tbody>
    <tr>
        <td>车辆信息</td>
    </tr>
    </tbody>
</table>

<table class="clear" id="tb_tui">
    <colgroup>
        <col width="100">
        <col width="140">
        <col width="100">
        <col width="140">
        <col width="100">
        <col width="130">
        <col width="100">
        <col>
    </colgroup>
    <tbody>
    <tr>
        <td class="td_title">车牌号</td>
        <td>
                ${washBeautyOrderDTO.licenceNo}
            <form:hidden path="licenceNo" value="${washBeautyOrderDTO.licenceNo}" style="text-transform:uppercase;"
                         autocomplete="off"/>
        </td>
        <td class="td_title">品牌</td>
        <td>${washBeautyOrderDTO.brand}</td>
        <td class="td_title">车型</td>
        <td>${washBeautyOrderDTO.model}</td>
        <td class="td_title">购车日期</td>
        <td>${vehicleDTO.carDateStr}</td>
    </tr>

    <tr>
        <td class="td_title">发动机号</td>
        <td>
            <span id="vehicleEngineNo" title="${washBeautyOrderDTO.vehicleEngineNo}">${washBeautyOrderDTO.vehicleEngineNo}</span>
                <script type="text/javascript">
                    $('#vehicleEngineNo').text() && $('#vehicleEngineNo').text().length > 15 && $('#vehicleEngineNo').text($('#vehicleEngineNo').text().substr(0, 14) + '...');
                </script>
        </td>
        <td class="td_title">车架号</td>
        <td>${washBeautyOrderDTO.vehicleChassisNo}</td>
        <td class="td_title">车辆颜色</td>
        <td>${washBeautyOrderDTO.vehicleColor}</td>
        <td></td>
        <td></td>
    </tr>

    <tr>
        <td class="td_title">客户名</td>
        <td>
                ${washBeautyOrderDTO.customer}
            <input type="hidden" id="customer" value="${washBeautyOrderDTO.customer}"/>
            <input type="hidden" id="customerId" value="${washBeautyOrderDTO.customerId}"/>
            <input type="hidden" id="contact" value="${washBeautyOrderDTO.contact}"/>
            <input type="hidden" id="vehicleContact" value="${washBeautyOrderDTO.vehicleContact}"/>
            <input type="hidden" id="vehicleMobile" value="${washBeautyOrderDTO.vehicleMobile}"/>
            <input type="hidden" id="brand" value="${washBeautyOrderDTO.brand}"/>
            <input type="hidden" id="model" value="${washBeautyOrderDTO.model}"/>
        </td>
        <td class="td_title">手机</td>
        <td>${washBeautyOrderDTO.mobile}
            <input type="hidden" id="mobile" value="${washBeautyOrderDTO.mobile}"/>
        </td>
        <td class="td_title">车主</td>
        <td>${washBeautyOrderDTO.vehicleContact}</td>
        <td class="td_title">车主手机</td>
        <td>${washBeautyOrderDTO.vehicleMobile}</td>
    </tr>

    <tr>
        <c:choose>
            <c:when test="${totalDebt != null && totalDebt != 0}">
                <td class="td_title">座机</td>
                <td>${washBeautyOrderDTO.landLine}
                    <input type="hidden" id="landLine" value="${washBeautyOrderDTO.landLine}"/>
                </td>
                <td class="td_title">联系地址</td>
                <td colspan="3">${customerDTO.address}</td>
                <td class="td_title">当前应收应付</td>
                <td class="qian_red">
                    <div class="pay" id="duizhan" style="text-align:left;margin-left: 5px">
                        <c:choose>
                            <c:when test="${totalDebt >0}">
                                <span class="red_color payMoney">收¥${totalDebt}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="gray_color fuMoney">收¥0</span>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${washBeautyOrderDTO.totalReturnDebt > 0}">
                                <span class="green_color fuMoney">付¥${washBeautyOrderDTO.totalReturnDebt}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="gray_color fuMoney">付¥0</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </td>
            </c:when>

            <c:otherwise>
                <td class="td_title">座机</td>
                <td>${washBeautyOrderDTO.landLine}
                    <input type="hidden" id="landLine" value="${washBeautyOrderDTO.landLine}"/>
                </td>
                <td class="td_title">联系地址</td>
                <td colspan="5">${customerDTO.address}</td>
            </c:otherwise>

        </c:choose>
    </tr>

    <c:if test="<%=isMemberSwitchOn%>">
        <!-- 会员卡信息 begin -->
        <tr>
            <td class="td_title">会员类型</td>
            <td>
                <span id="memberType">${washBeautyOrderDTO.memberDTO.type}</span>
                <form:hidden path="memberDTO.type" value="${washBeautyOrderDTO.memberDTO.type}"/>
            </td>
            <td class="td_title">会员卡号</td>
            <td>
                <span id="memberNumber">${washBeautyOrderDTO.memberDTO.memberNo}</span>
                <form:hidden path="memberDTO.memberNo" cssClass="checkStringEmpty"
                             value="${washBeautyOrderDTO.memberDTO.memberNo}"/>
            </td>
            <td class="td_title">卡内余额</td>
            <td>
                <div id="memberRemainAmount">${washBeautyOrderDTO.memberDTO.balanceStr}</div>
                <form:hidden path="memberDTO.balance"/>
            </td>
            <td class="td_title">状态</td>
            <td>
                <span id="memberStatus">${washBeautyOrderDTO.memberDTO.statusStr}</span>
                <form:hidden path="memberDTO.statusStr" value="${washBeautyOrderDTO.memberDTO.statusStr}"/>
            </td>
        </tr>
        <!-- 会员卡信息 end -->
    </c:if>

    </tbody>
</table>

<c:if test="<%=isMemberSwitchOn%>">
    <!-- 会员卡服务信息 begin -->
    <table>
        <colgroup>
            <col width="150">
        </colgroup>
        <tbody>
        <tr>
            <td>会员卡服务信息</td>
        </tr>
        </tbody>
    </table>

    <table class="clear" id="tb_tui">
        <colgroup>
            <col width="80">
            <col width="80">
            <col width="80">
            <col width="80">
        </colgroup>
        <tbody>
        <tr class="tab_title">
            <td>序号</td>
            <td>项目</td>
            <td>剩余次数</td>
            <td>失效日期</td>
        </tr>
        <c:forEach items="${washBeautyOrderDTO.memberDTO.memberServiceDTOs}" var="memberService" varStatus="mS">
            <tr class="item">
                <td>${mS.index+1}</td>
                <td>
                        ${memberService.serviceName}
                    <form:hidden path="memberDTO.memberServiceDTOs[${mS.index}].id" value="${memberService.id}"/>
                    <form:hidden path="memberDTO.memberServiceDTOs[${mS.index}].serviceId"
                                 value="${memberService.serviceId}"/>
                    <form:hidden path="memberDTO.memberServiceDTOs[${mS.index}].serviceName"
                                 value="${memberService.serviceName}"/>
                </td>
                <td>
                        ${memberService.timesStr}
                    <form:hidden path="memberDTO.memberServiceDTOs[${mS.index}].times" value="${memberService.times}"/>
                    <form:hidden path="memberDTO.memberServiceDTOs[${mS.index}].timesStr"
                                 value="${memberService.timesStr}"/>
                </td>
                <td>
                        ${memberService.deadlineStr}
                    <form:hidden path="memberDTO.memberServiceDTOs[${mS.index}].deadline"
                                 value="${memberService.deadline}"/>
                    <form:hidden path="memberDTO.memberServiceDTOs[${mS.index}].deadlineStr"
                                 value="${memberService.deadlineStr}"/>
                </td>
            </tr>
        </c:forEach>

        </tbody>
    </table>
    <!-- 会员卡服务信息 end -->
</c:if>
<table>
    <colgroup>
        <col width="80">
        <col width="150">
    </colgroup>
    <tbody>
    <tr>
        <td>施工信息</td>
    </tr>
    </tbody>
</table>

<table class="clear" id="tb_tui">
    <colgroup>
        <col width="80">
        <col width="200">
        <col width="80">
        <col width="80">
        <col width="80">
        <col width="80">
        <col width="80">
        <col/>
    </colgroup>
    <tbody>
    <tr class="tab_title">
        <td>序号</td>
        <td>服务内容</td>
        <td>消费方式</td>
        <td>施工人</td>
        <td>金额</td>
        <td>消费券类型</td>
        <td>消费券号</td>
    </tr>

    <c:forEach items="${washBeautyOrderDTO.washBeautyOrderItemDTOs}" var="itemDTOs" varStatus="status">
        <tr class="item">
            <td>${status.index+1}</td>
            <td>
                    ${itemDTOs.serviceName}
            </td>
            <td>
                <c:choose>
                    <c:when test='${itemDTOs.consumeTypeStr eq "MONEY"}'>
                        金额
                    </c:when>
                    <c:when test='${itemDTOs.consumeTypeStr eq "TIMES"}'>
                        <c:if test="<%=isMemberSwitchOn%>">
                            计次划卡
                        </c:if>
                    </c:when>
                    <c:when test='${itemDTOs.consumeTypeStr eq "COUPON"}'>
                        消费券
                    </c:when>
                </c:choose>
            </td>
            <td>${itemDTOs.salesMan}</td>
            <td>
                <c:choose>
                    <c:when test='${itemDTOs.consumeTypeStr eq "MONEY"}'>
                        ${itemDTOs.price}
                    </c:when>
                    <c:when test='${itemDTOs.consumeTypeStr eq "TIMES"}'>
                        <c:if test="<%=isMemberSwitchOn%>">
                            计次划卡一次
                            <%--还剩余${itemDTOs.surplusTimes}次--%>
                        </c:if>
                    </c:when>
                </c:choose>
            </td>
            <td>${itemDTOs.couponType}</td>
            <td>${itemDTOs.couponNo}</td>
        </tr>
    </c:forEach>
    <tr>
        <td colspan="4" style="text-align:right; padding-right:20px;">合计：</td>
        <td>${washBeautyOrderDTO.total}</td>
        <td colspan="2"></td>
    </tr>
    </tbody>
</table>

<div class="height"></div>
<strong class="jie_info clear">结算信息</strong>
        <div class="jie_detail clear almost">
          <c:if test="${washBeautyOrderDTO.afterMemberDiscountTotal != null && washBeautyOrderDTO.total - washBeautyOrderDTO.afterMemberDiscountTotal > 0}" var="isMember">
           <div>应收总额：&nbsp;<span class="borders">${washBeautyOrderDTO.total}元</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${washBeautyOrderDTO.memberDiscountRatio}折后价格：<span>${washBeautyOrderDTO.afterMemberDiscountTotal}元</span></div>
          </c:if>
          <c:if test="${!isMember}">
           <div>应收总额：&nbsp;<span>${washBeautyOrderDTO.total}元</span></div>
          </c:if>
            <div style="">实收总计：&nbsp;<span>${washBeautyOrderDTO.settledAmount}元</span></div>
            <div style="">优惠总计：&nbsp;<span>
              <c:if test="${washBeautyOrderDTO.memberDiscountRatio != null}" var="isMember">

                  ${afterMemberDeduction}元
              </c:if>
              <c:if test="${!isMember}">
                ${washBeautyOrderDTO.orderDiscount}元
              </c:if>
            </span></div>
            <div style="">挂账金额：&nbsp;<span class="red_color">${washBeautyOrderDTO.debt}元</span></div>
            <c:if test="${washBeautyOrderDTO.couponAmount!=null}">
                <div style="">代金券：&nbsp;<span style="opacity:0;width:auto;">空</span><span class="red_color">${washBeautyOrderDTO.couponAmount}元</span></div>
            </c:if>
        <table cellpadding="0" cellspacing="0" class="tabDan clear tabRu">
        <col width="120">
        <col width="60">
        <col width="90">
        <col width="90">
        <col width="70">
        <col width="70">
        <col>

        <tr  class="tabBg">
        	<td>结算日期</td>
            <td >结算人</td>
            <td >上次结余</td>
            <td >本次实收</td>
        	<td >本次优惠</td>
            <td >本次挂账</td>
            <td >附加信息</td>
        </tr>
      <c:forEach items="${receptionRecordDTOs}" var="receptionRecordDTO" varStatus="status">

        <tr>
        	<td>${receptionRecordDTO.receptionDateStr}</td>
            <td>${receptionRecordDTO.payee}</td>

            <td>
          <c:if test="${status.index==0}" var="isFirstRecord">
                       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--
          </c:if>
          <c:if test="${!isFirstRecord}">
            ${receptionRecordDTO.originDebt}
          </c:if>
            </td>
            <td>${receptionRecordDTO.amount}</td>
            <td>${receptionRecordDTO.discount}</td>
            <td>${receptionRecordDTO.remainDebt}</td>
            <td>
              <c:if test="${receptionRecordDTO.cash>0}">
                    现金：¥${receptionRecordDTO.cash}<br />
              </c:if>

              <c:if test="${receptionRecordDTO.bankCard>0}">
                    银联：¥${receptionRecordDTO.bankCard}<br />
              </c:if>
              <c:if test='${"CUSTOMER_STATEMENT_DEBT" eq receptionRecordDTO.orderTypeEnum}'>
                    对账结算：¥${receptionRecordDTO.statementAmount}
                    对账单号：<a class="blue_color" onclick="openStatementOrder('${washBeautyOrderDTO.statementAccountOrderId}');" href="#">${receiveNo}</a>
                <br />
              </c:if>

              <c:if test="${receptionRecordDTO.cheque>0}">
                    支票：¥${receptionRecordDTO.cheque}
                    使用支票号${receptionRecordDTO.chequeNo}<br/>
              </c:if>
              <c:if test="${receptionRecordDTO.memberId != null && receptionRecordDTO.memberBalancePay>0}">
                    会员卡：¥${receptionRecordDTO.memberBalancePay}
                    卡号：${receptionRecordDTO.memberNo}
                <c:if test="${washBeautyOrderDTO.afterMemberDiscountTotal != washBeautyOrderDTO.total}">
                     ${receptionRecordDTO.memberDiscountRatio*10}折优惠<br />
                </c:if>
              </c:if>
              <c:if test="${not empty receptionRecordDTO.toPayTimeStr}">
                 预计还款日期：${receptionRecordDTO.toPayTimeStr}
              </c:if>
            </td>
        </tr>

      </c:forEach>


        <%--<tr>--%>
        	<%--<td>2012-12-12 12:23</td>--%>
            <%--<td>张三</td>--%>
            <%--<td>4700.0</td>--%>
            <%--<td>10000.0</td>--%>
            <%--<td>83.0</td>--%>
            <%--<td>4700.0</td>--%>
            <%--<td>--%>
                <%--对账结算：¥700.0&nbsp;&nbsp;对账单号：<a class="blue_color" href="#">GZ3232323232</a>--%>
            <%--</td>--%>
        <%--</tr>--%>
        </table>
        </div></div>

</div>

<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.CACEL">
<c:if test='${"WASH_SETTLED" eq washBeautyOrderDTO.status}'>
  <c:if test="${ washBeautyOrderDTO.statementAccountOrderId == null}">
    <div class="invalidImg" style="display: block;">
      <input id="nullifyBtn" type="button" onfocus="this.blur();">

      <div id="invalidWords" class="invalidWords">作废</div>
    </div>
  </c:if>
</c:if>
</bcgogo:hasPermission>
<%--<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.COPY">--%>
<c:if test='${"WASH_SETTLED" eq washBeautyOrderDTO.status or "WASH_REPEAL" eq washBeautyOrderDTO.status}'>
    <div id="copyInput_div" class="copyInput_div" style="display: block;">
        <input id="copyInput" type="button" onfocus="this.blur();">

        <div id="copyInput_text" class="copyInput_text_div">复制</div>
    </div>
</c:if>
<%--</bcgogo:hasPermission>--%>
<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.PRINT">
<div class="btn_div_Img" id="print_div" style="float:right;">
    <div>
        <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
    </div>
    <div class="optWords">打印</div>
</div>
</bcgogo:hasPermission>
<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DATA.ARREARS">
<c:if test="${washBeautyOrderDTO.status=='WASH_SETTLED' && washBeautyOrderDTO.debt>0}">
    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_DATA.ARREARS">
    <div class="btn_div_Img" style="float:right;margin:0px 0 0;">
        <input type="button" id="washDebtAccount" class="saleAccount j_btn_i_operate"
               onclick="toReceivableSettle('${washBeautyOrderDTO.customerId}','washBeauty')"
               onfocus="this.blur();"/>

        <div class="optWords">欠款结算</div>
    </div>
    </bcgogo:hasPermission>
</c:if>
</bcgogo:hasPermission>
</form:form>
</div>


<c:if test='${"WASH_SETTLED" eq washBeautyOrderDTO.status}'>
    <c:choose>
        <c:when test="${washBeautyOrderDTO.debt>0}">
            <div class="washDebtSettleImg" id="zuofei" style="display: block;"></div>
        </c:when>
        <c:when test="${washBeautyOrderDTO.statementAccountOrderId != null}">
            <div class="statement_accounted" id="zuofei" style="display: block;"></div>
        </c:when>
        <c:otherwise>
            <div class="jie_suan_wash" id="zuofei" style="display: block;"></div>
        </c:otherwise>
    </c:choose>

</c:if>
<c:if test='${"WASH_REPEAL" eq washBeautyOrderDTO.status}'>
    <div class="zuofei_wash " id="zuofei" style="display: block;"></div>
</c:if>

</div>


<div id="mask" style="display:block;position: absolute;">
</div>
<div id="div_brand" class="i_scroll" style="display:none;">
    <div class="Container">
        <div id="Scroller-1">
            <div class="Scroller-Container" id="Scroller-Container_id">
            </div>
        </div>
    </div>
</div>
<!-- 车牌号下拉菜单 zhangchuanlong-->
<div id="div_brandvehiclelicenceNo" class="i_scroll" style="display:none;width:150px;">
    <div class="Container" style="width:150px;">
        <div id="Scroller-1licenceNo" style="width:150px;">
            <div class="Scroller-Containerheader" id="Scroller-Container_idlicenceNo">
            </div>
        </div>
    </div>
</div>
<!-- 客户商下拉菜单 zhangchuanlong-->
<div id="div_brandCustomer" class="i_scroll" style="display:none;width:300px;">
    <div class="Container" style="width:300px;">
        <div id="Scroller-1licenceNo1" style="width:300px;">
            <div class="Scroller-ContainerSupplier" id="Scroller-Container_idCustomer">
            </div>
        </div>
    </div>
</div>
<!-- 施工人下拉 -->
<div id="div_works" class="i_scroll" style="display:none;">
    <div class="Container">
        <div id="div_works-1">
            <div class="Scroller-Container" id="works-Container_id">
            </div>
        </div>
    </div>
</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:6;top:210px;left:87px;display:none; "
        allowtransparency="true" width="1000px" height="100%" frameborder="0" scrolling="no" src=""></iframe>
<iframe id="iframe_qiankuan" style="position:absolute; left:0px; top:300px; display:none;z-index:8;"
        allowtransparency="true" width="1000px" height="900px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_CardList" style="position:absolute;z-index:9; left:300px; top:200px; display:none;"
        allowtransparency="true" width="850px" height="300px" frameborder="0" src=""></iframe>
<iframe id="iframe_buyCard" style="position:absolute;z-index:7; left:300px; top:10px; display:none;"
        allowtransparency="true" width="800px" height="740px" scrolling="no" frameborder="0" src=""></iframe>
<iframe id="iframe_addService" style="position:absolute;z-index:10; left:300px; top:30px; display:none;"
        allowtransparency="true" width="780px" height="650px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:300px; display:none;"
        allowtransparency="true" width="900px" height="450px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_moreUserInfo"
        style="position:absolute;z-index:7; left:200px; top:200px; display:none;overflow-x:hidden;overflow-y:auto;"
        allowtransparency="true" width="840px" height="600px" scrolling="no" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_2"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<div id="mask" style="display:block;position: absolute;"></div>
<%@ include file="/sms/enterPhone.jsp" %>
<jsp:include page="/txn/orderOperationLog.jsp" />
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>