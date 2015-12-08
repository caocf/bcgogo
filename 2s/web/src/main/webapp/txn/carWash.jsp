<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>洗车美容</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
    <style type="text/css">
        .cuSearch .tab_cuSearch tr.titBody_Bg td .txt{
            font-size : 14px;
            height: 20px;
        }

        .cuSearch .tab_cuSearch tr.titBody_Bg td {
            padding: 12px 6px 3px 0;
        }
    </style>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/invoicesolr<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/carWash<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/vehicleValidator<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "VEHICLE_CONSTRUCTION_WASH");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
        var submitResultMsg = "${resultMsg}";

        APP_BCGOGO.Permission.Version.FourSShopVersion =${empty fourSShopVersions?false:fourSShopVersions};

        $().ready(function() {
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
            $("select[id$='.serviceId']").bind("change", function(e) {
                var e = e || event;
                var target = e.srvElement || e.target;
                var idStr = $(target).attr("id");
                var idSplit = idStr.split(".");
                memberChange();
                if ($("select[name=memberServiceSelect] option[value=" + target.value + "]").length > 0) {
                    $(target).parents("tr").find("select[id$='consumeTypeStr']").val("TIMES");
                    $(target).parents("tr").find("div[id$='hiddenConsumeType']").text("TIMES");

                    if($("select[name=memberServiceSelect] option[value=" + target.value + "]").text() == '不限次'){
                        $(target).parents("tr").find(".j_remainTimes").text("不限次");
                    } else {
                        $(target).parents("tr").find(".j_remainTimes").text("还剩" + $("select[name=memberServiceSelect] option[value=" + target.value + "]").text() + "次");
                    }
                }
                else {
                    $(target).parents("tr").find("select[id$='consumeTypeStr']").val("MONEY");
                    $(target).parents("tr").find("div[id$='hiddenConsumeType']").text("MONEY");
                    $(target).parents("tr").find(".j_remainTimes").text("还剩0次");
                }
                $("#" + idSplit[0] + "\\.price").val($("select[name=serviceDTOSelect] option[value=" + target.value + "]").text());
                memberChange();
            });

            $("input[id$='couponNo']").keyup(function() {
                var couponNo = $(this).val();
                $(this).val(couponNo.replace(/[^A-Za-z0-9]+/g, ""));
            });

            $("input[id$='couponType']").keyup(function() {
                var couponType = $(this).val();
                $(this).val(couponType.replace(/[^A-Za-z0-9\u4E00-\u9FFF]+/g, ""));
            });


            setOrderDisableAttrs();
            setTotal();
            /**
             * todo for  BCSHOP-11693
             *为了fix BCSHOP-11693 临时在appoint.do?method=createOtherOrder 里加了一个flag，
             * 页面加了一个标签 <input type="hidden" id="createFromFlag" value="${createFromFlag}"/>
             * 后续要彻底解决这个问题还需要从下面那个方法原生地方去修改
             */
            if((!G.isEmpty($("#memberNumber").val())||!G.isEmpty($("#customerId").val())) && $("#createFromFlag").val() != "appointOrder"){    //从刷卡机，客户详细跳转
                APP_BCGOGO.Net.asyncAjax({
                    url: "washBeauty.do?method=ajaxGetWashBeautyOrderByParameter",
                    type: "POST",
                    cache: false,
                    data:{
                        memberNo:$("#memberNumber").val(),
                        customerId:$("#customerId").val(),
                        licenceNo:$("#licenceNo").val()
                    },
                    dataType: "json",
                    success: function (washBeautyOrderDTO) {
//                        if(G.isEmpty(G.normalize(washBeautyOrderDTO).memberDTO)){
//                            nsDialog.jAlert("会员号不存在。");
//                            $("#memberNumber").val("");
//                            return;
//                        }
                        initWashBeautyOrder(washBeautyOrderDTO);
//                        $("#customer").blur();
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                    }
                })
            }


        });

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
<input type="hidden" id="createFromFlag" value="${createFromFlag}"/>
<div class="i_main clear">
<div class="mainTitles" style="width: 99%">
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
<div class="booking-management">
<input id="memberSwitch" value="<%=isMemberSwitchOn%>" type="hidden"/>
<form:form commandName="washBeautyOrderDTO" name="thisform" id="washBeautyOrderForm"       class="J_leave_page_prompt"
           method="post" action="">
<form:hidden path="id" value="${washBeautyOrderDTO.id}"/>
<form:hidden path="memberDTO.id" value="${washBeautyOrderDTO.memberDTO.id}"/>
<form:checkbox id="sendMemberSms" path="sendMemberSms" style="display:none;"/>
<form:hidden path="appointOrderId"/>
<input type="hidden" id="memberCardId" value="${washBeautyOrderDTO.memberDTO.id}"/>
<div class="titBody">
    <div  style="width:58%; float:left; ">
        <div class="wash-content-item shelvesed clear" style="width:100%">
            <div class="topTitle">
                车辆信息
            </div>
            <div class="customer" style="border:0; font-size:12px;">
                <table  width="100%"cellpadding="0" cellspacing="0" class="table1">
                    <tr>
                        <td class="i_tableLeft">车牌号<a class="red_color">*</a></td>
                        <td>
                            <form:input path="licenceNo" autocomplete="off" value="${washBeautyOrderDTO.licenceNo}" type="text" class="checkStringEmpty txt" kissfocus="on" />
                            <input id="vechicleId" value="${washBeautyOrderDTO.vechicleId}" type="hidden" autocomplete="off">
                            <bcgogo:orderPageConfigurationParam orderGroupName="order_type_condition"
                                                                orderNameAndResource="[wash_beauty_order,WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE]">
                                <div id="history_id" class="history"
                                     onclick="redirectToInquiryCenterTemp({
                                             pageType:'wash_beauty',vehicleNumber:$('#licenceNo').val(),vehicleModel:$('#model').val(),vehicleBrand:$('#brand').val(),customerOrSupplier:
                                             $('#customer').val(),contact:$('#contact').val(),mobile:$('#mobile').val(),
                                             washBeautyPageCustomizerConfig:${order_type_condition_wash_beauty_order}
                                             });">
                                </div>
                            </bcgogo:orderPageConfigurationParam>
                        </td>
                        <td>车辆品牌</td>
                        <td id="td_brand">
                            <form:input path="brand" value="${washBeautyOrderDTO.brand}" type="text"  class="J_checkVehicleBrandModel txt checkStringEmpty" autocomplete="off"/>
                            <input type="hidden" id="input_brandname" value="${washBeautyOrderDTO.brand}"/>
                        </td>
                        <td>车型</td>
                        <td id="td_model">
                            <form:input path="model" value="${washBeautyOrderDTO.model}" type="text" class="J_checkVehicleBrandModel txt checkStringEmpty" autocomplete="off"/>
                            <input type="hidden" id="input_modelname" value="${washBeautyOrderDTO.model}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>车架号</td>
                        <td>
                            <form:input path="vehicleChassisNo" value="${washBeautyOrderDTO.vehicleChassisNo}" type="text" class="txt vehicleChassisNo chassisNumber"
                                       maxlength="17" style="width:100px;" autocomplete="off"/>
                        </td>
                        <td>发动机号</td>
                        <td><form:input path="vehicleEngineNo" value="${washBeautyOrderDTO.vehicleEngineNo}" type="text"  class="txt"
                                       maxlength="30" autocomplete="off"/></td>
                        <td>车身颜色</td>
                        <td><form:input path="vehicleColor" value="${washBeautyOrderDTO.vehicleColor}" type="text"  class="txt" autocomplete="off"/></td>
                    </tr>
                </table>
            </div>
        </div>
        <div class="clear"></div>
        <div class="wash-content-item shelvesed clear" style="width:100%;border-top: 0 none;padding-bottom: 3px">
            <div class="topTitle"><a class="i_clickClient title-r blue_color" style="margin-right: 5px;cursor: pointer">客户详细信息&gt;</a> 客户信息</div>
            <div class="customer" style="border:0; font-size:12px;">
                <table  width="100%"cellpadding="0" cellspacing="0" class="table1">
                    <tr>
                        <td class="i_tableLeft">客户名<a class="red_color">*</a></td>
                        <td>
                            <form:input path="customer" value="${washBeautyOrderDTO.customer}" autocomplete="off"
                                        maxlength="15" kissfocus="on" type="text" class="txt" size="15"/>
                            <form:hidden path="customerId" value="${washBeautyOrderDTO.customerId}" autocomplete="off"/>
                            <form:hidden path="contact" value="${washBeautyOrderDTO.contact}" autocomplete="off"/>
                            <form:hidden path="contactId" value="${washBeautyOrderDTO.contactId}" autocomplete="off"/>
                            <form:hidden path="qq" value="${washBeautyOrderDTO.qq}" autocomplete="off"/>
                            <form:hidden path="email" value="${washBeautyOrderDTO.email}" autocomplete="off"/>
                            <form:hidden path="address" value="${washBeautyOrderDTO.address}" autocomplete="off"/>
                            <form:hidden path="company" value="${washBeautyOrderDTO.company}" autocomplete="off"/>
                        </td>
                        <td>手机</td>
                        <td>
                            <form:input path="mobile" value="${washBeautyOrderDTO.mobile}" type="text"  class="txt checkStringEmpty" size="10" maxlength="11" autocomplete="off"/>
                            <span id="hiddenMobile" style="display:none">${washBeautyOrderDTO.mobile}</span>
                        </td>
                        <td>座机</td>
                        <td><form:input path="landLine" type="text" class="txt checkStringEmpty" value="${washBeautyOrderDTO.landLine}" size="15" autocomplete="off"/></td>
                    </tr>
                    <tr>
                        <td>车主</td>
                        <td>
                            <form:input path="vehicleContact" value="${washBeautyOrderDTO.vehicleContact}" maxlength="20" type="text" class="txt" style="width:100px;"  kissfocus="on" autocomplete="off"/>
                        </td>
                        <td>车主手机</td>
                        <td>
                            <form:input path="vehicleMobile" value="${washBeautyOrderDTO.vehicleMobile}" maxlength="11" type="text"  class="txt" autocomplete="off"/>
                        </td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>消费时间</td>
                        <td>
                                <%--<input type="text" class="txt" style="width:100px;"/>--%>
                             <span>
                                <form:input path="vestDateStr" ordertype="wash" id="orderVestDate" size="15" readonly="true"
                                            value="${washBeautyOrderDTO.vestDateStr}" lastvalue="${washBeautyOrderDTO.vestDateStr}"
                                            initordervestdatevalue="${washBeautyOrderDTO.vestDateStr}"   style="width:100px;"
                                            cssClass="checkStringChanged textbox txt" autocomplete="off"/>
                             </span>
                        </td>
                        <td colspan="4">

                            <div>
                                累计消费 <span class="arialFont">&yen;</span><span id="totalConsumeSpan">${washBeautyOrderDTO.totalConsume == 0 ? 0 : washBeautyOrderDTO.totalConsume}</span>
                                应    收<span class="red_color"> <span class="arialFont">&yen;</span><span id="totalReceivableSpan">${washBeautyOrderDTO.totalReceivable == 0 ? 0 : washBeautyOrderDTO.totalReceivable}</span></span>
                                应    付 <span class="arialFont">&yen;</span><span id="totalReturnDebtSpan">${washBeautyOrderDTO.totalReturnDebt == 0 ? 0 : washBeautyOrderDTO.totalReturnDebt}</span>
                                 <a id="duizhan"  class="blue_color" style="margin:0px 5px 3px 5px;cursor: pointer">对账 &gt;</a>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="clear i_height"></div>
        </div>
    </div>
    <div style="width:40%; float:left; margin-left:2px;">
        <c:if test="<%=isMemberSwitchOn%>">
            <div class="wash-content-item shelvesed shelves" style="width:100%">
                <div class="topTitle"> 会员信息</div>
                <div class="customer" style="border:0; font-size:12px;">
                    <table  width="100%"cellpadding="0" cellspacing="0" class="table1">
                        <tr>
                            <td class="i_tableLeft">
                                会员卡号
                            </td>
                            <td>
                                <a id="doMemberCard" class="title-r blue_color" style="cursor: pointer;"></a>
                                <input id="memberNumber" name="memberDTO.memberNo" value="${washBeautyOrderDTO.memberDTO.memberNo}" kissfocus="on"
                                       class="checkStringEmpty txt" maxlength="15" autocomplete="off"/>
                                <span id="memberNumberSpan" style="display: none">--</span>
                                <%--<form:hidden  cssClass="checkStringEmpty" value="${washBeautyOrderDTO.memberDTO.memberNo}"/>--%>
                            </td>
                            <td>状态</td>
                            <td>
                                <span id="memberStatus">${washBeautyOrderDTO.memberDTO.statusStr}</span>
                                <form:hidden path="memberDTO.statusStr" value="${washBeautyOrderDTO.memberDTO.statusStr}"/>
                            </td>
                        </tr>
                        <tr>
                            <td>会员类型</td>
                            <td>
                           <span id="memberType">${washBeautyOrderDTO.memberDTO.type}
                           <form:hidden path="memberDTO.type" value="${washBeautyOrderDTO.memberDTO.type}"/>
                            </td>
                            <td>卡内余额</td>
                            <td>
                                <div id="memberRemainAmount" style="float:left;">${washBeautyOrderDTO.memberDTO.balanceStr}</div>
                                <form:hidden path="memberDTO.balance"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div class="clear"></div>
            <div class="shelvesed shelves" style="width:100%;border-top: 0 none;">
                <div class="topTitle">会员卡项目</div>
                <div class="service-item-div customer" style="border:0; font-size:12px;">
                    <table id="memberServiceTable" cellpadding="0" cellspacing="0" class="table1" width="100%">
                        <col />
                        <col width="100" />
                        <col width="100" />
                        <tr>
                            <td class="i_tableLeft">项目</td>
                            <td>剩余次数</td>
                            <td>失效日期</td>
                        </tr>
                        <c:forEach items="${washBeautyOrderDTO.memberDTO.memberServiceDTOs}" var="memberService" varStatus="status">
                            <tr>
                                <td>
                                        ${memberService.serviceName}
                                    <%--<form:hidden path="memberDTO.memberServiceDTOs[${status.index}].id" value="${memberService.id}"/>--%>
                                    <%--<form:hidden path="memberDTO.memberServiceDTOs[${status.index}].serviceId" value="${memberService.serviceId}"/>--%>
                                    <%--<form:hidden path="memberDTO.memberServiceDTOs[${status.index}].serviceName" value="${memberService.serviceName}"/>--%>
                                </td>
                                <td>
                                        ${memberService.timesStr}
                                    <%--<form:hidden path="memberDTO.memberServiceDTOs[${status.index}].times" value="${memberService.times}"/>--%>
                                    <%--<form:hidden path="memberDTO.memberServiceDTOs[${status.index}].timesStr" value="${memberService.timesStr}"/>--%>
                                </td>
                                <td>
                                        ${memberService.deadlineStr}
                                    <%--<form:hidden path="memberDTO.memberServiceDTOs[${status.index}].deadline" value="${memberService.deadline}"/>--%>
                                    <%--<form:hidden path="memberDTO.memberServiceDTOs[${status.index}].deadlineStr" value="${memberService.deadlineStr}"/>--%>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
        </c:if>
    </div>
    <div class="clear i_height"></div>
</div>
<div class="titBody">
    <div class="lineTitle">单据信息</div>
    <div class="lineBody bodys">
        <div class="cuSearch">
            <div class="gray-radius" style="margin:0;">
                <table id="table_services" class="wash-content-item tab_cuSearch" cellpadding="0" cellspacing="0" style="width:950px;font-size:14px;">
                    <colgroup>
                        <col  width="200"/>
                        <col  width="170"/>
                        <col  width="100"/>
                        <col  width="100"/>
                        <col  width="100"/>
                        <col  width="100"/>
                        <col  width="120"/>
                        <col  width="100"/>
                    </colgroup>
                    <tr class="titleBg">
                        <td style="padding-left:10px;" >服务内容</td>
                        <td>消费方式</td>
                        <td>营业分类</td>
                        <td>施工人</td>
                        <td>金额</td>
                        <td>消费券类型</td>
                        <td>消费券号</td>
                        <td>操作<input class="opera2" type="button" style="display:none;"></td>
                    </tr>
                    <tr class="space">
                        <td colspan="8"></td>
                    </tr>
                    <c:forEach items="${washBeautyOrderDTO.washBeautyOrderItemDTOs}" var="itemDTO" varStatus="status">
                        <tr class="item bg titBody_Bg">
                            <td style="padding-left:10px;">
                                <form:input path="washBeautyOrderItemDTOs[${status.index}].serviceName" value="${itemDTO.serviceName}"
                                            class="txt checkStringEmpty J-hide-empty-droplist J-bcgogo-droplist-on" autocomplete="off" kissfocus="on"
                                            initialValue="${itemDTO.serviceName}"/>
                                <form:hidden path="washBeautyOrderItemDTOs[${status.index}].serviceId" value="${itemDTO.serviceIdStr}"/>
                            </td>
                            <td class="surplus">
                                <form:select path="washBeautyOrderItemDTOs[${status.index}].consumeTypeStr"   style="width: 80px"  autocomplete="off"
                                             value="${itemDTO.consumeTypeStr}" cssClass="payMethod txt" onchange="selChange(this.value,this)">
                                    <form:option value="MONEY" label="金额"/>
                                    <c:if test="<%=isMemberSwitchOn%>">
                                        <form:option value="TIMES" label="计次划卡"/>
                                    </c:if>
                                    <form:option value="COUPON" label="消费券"/>
                                </form:select>

                                <span class="j_remainTimes"></span>
                            </td>
                            <td>
                                <form:input path="washBeautyOrderItemDTOs[${status.index}].businessCategoryName" value="${itemDTO.businessCategoryName}"
                                            class="businessCategoryName txt"   autocomplete="off" hiddenValue="${itemDTO.businessCategoryName}"/>
                                <form:hidden path="washBeautyOrderItemDTOs[${status.index}].businessCategoryId" value="${itemDTO.businessCategoryId}"/>
                            </td>
                            <td>
                                <form:hidden path="washBeautyOrderItemDTOs[${status.index}].salesManIds"
                                             value="${itemDTO.salesManIds}"/>
                                <form:input path="washBeautyOrderItemDTOs[${status.index}].salesMan" cssClass="checkStringEmpty txt"
                                            value="${itemDTO.salesMan}" cssStyle="width:80px;" autocomplete="off"/>
                                <div id="washBeautyOrderItemDTOs${status.index}.hiddenConsumeType"
                                     style="display:none">${itemDTO.consumeTypeStr}</div>
                            </td>
                            <td>
                                <form:input path="washBeautyOrderItemDTOs[${status.index}].price"
                                            cssClass="servicePrice txt" value="${itemDTO.price}"
                                            cssStyle="width:80px;" autocomplete="off" data-filter-zero="true"/>
                            </td>
                            <td>
                                <form:input path="washBeautyOrderItemDTOs[${status.index}].couponType" value="${itemDTO.couponType}"
                                            cssClass="txt" cssStyle="display:none;" autocomplete="off"/>
                            </td>
                            <td>
                                <form:input path="washBeautyOrderItemDTOs[${status.index}].couponNo" value="${itemDTO.couponNo}"
                                            cssClass="txt" cssStyle="display:none;" autocomplete="off"/>
                            </td>
                            <td>
                                <a id="washBeautyOrderItemDTOs${status.index}.deleteBtn" class="blue_color opera1">删除</a>
                                <a id="washBeautyOrderItemDTOs[${status.index}].plusBtn" class="blue_color opera2">新增</a>
                            </td>
                        </tr>
                        <tr class="titBottom_Bg">
                            <td colspan="8"></td>
                        </tr>
                    </c:forEach>
                </table>
                <div class="clear total-of"> <span style="padding-left:10px;"></span>
                    合计：<span id="total_span" class="yellow_color">0</span>元
                    <form:hidden path="total" value="${washBeautyOrderDTO.total}"/>
                    <form:hidden path="cashAmount" value="${washBeautyOrderDTO.cashAmount}"/>
                    <form:hidden path="bankAmount" value="${washBeautyOrderDTO.bankAmount}"/>
                    <form:hidden path="bankCheckAmount" value="${washBeautyOrderDTO.bankCheckAmount}"/>
                    <form:hidden path="bankCheckNo" value="${washBeautyOrderDTO.bankCheckNo}"/>
                    <form:hidden path="orderDiscount" value="${washBeautyOrderDTO.orderDiscount}"/>
                    <form:hidden path="accountMemberNo" value="${washBeautyOrderDTO.accountMemberNo}"/>
                    <form:hidden path="accountMemberPassword" value="${washBeautyOrderDTO.accountMemberPassword}"/>
                    <form:hidden path="settledAmount" value="${washBeautyOrderDTO.settledAmount}"/>
                    <form:hidden path="debt" value="${washBeautyOrderDTO.debt}"/>
                    <form:hidden path="memberAmount" value="${washBeautyOrderDTO.memberAmount}"/>
                    <form:hidden path="memberDiscountRatio"/>
                    <form:hidden path="afterMemberDiscountTotal"/>
                    <input type="hidden" name="print" id="isPrint" value="${washBeautyOrderDTO.print}">
                    <form:hidden path="huankuanTime" value="${washBeautyOrderDTO.huankuanTime}"/>
                    <form:hidden path="consumingRecordId" value="${washBeautyOrderDTO.consumingRecordId}"/>
                    <input type="hidden" id="couponAmount" name="couponAmount" autocomplete="off"  value="${washBeautyOrderDTO.couponAmount}"/>
                </div>



                <div class="clear i_height"></div>
            </div>
            <div class="clear i_height"></div>
        </div>

    </div>
    <div class="lineBottom"></div>
</div>
<div class="height"></div>
<div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
                        <div class="btn_div_Img divImg" id="saleSave_div">
                            <input id="carwashBtn" type="button" class="saleAccount j_btn_i_operate" onfocus="this.blur();"/>

                            <div class="optWords">结算</div>
                        </div>
                        <div class="btn_div_Img divImg" id="print_div">
                            <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                            <div class="optWords">打印</div>
                        </div>
                    </div>
</form:form>
</div>
</div>
<div id="mask" style="display:block;position: absolute;">
</div>
<div id="div_brand" class="i_scroll c-drop-list" style="display:none;">
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

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:50px; display:none;"
        allowtransparency="true" width="1000px" height="900px" scrolling="no" frameborder="0" src=""></iframe>
<iframe id="iframe_qiankuan" style="position:absolute; left:0px; top:300px; display:none;z-index:8;"
        allowtransparency="true" width="1000px" height="900px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_CardList" style="position:absolute;z-index:9; left:300px; top:200px; display:none;"
        allowtransparency="true" width="850px" height="300px" frameborder="0" src=""></iframe>
<iframe id="iframe_buyCard" style="position:absolute;z-index:7; left:300px; top:10px; display:none;"
        allowtransparency="true" width="800px" height="740px" scrolling="no" frameborder="0" src=""></iframe>
<iframe id="iframe_addService" style="position:absolute;z-index:10; left:300px; top:30px; display:none;"
        allowtransparency="true" width="780px" height="650px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:200px; display:none;"
        allowtransparency="true" width="900" height="450px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_moreUserInfo"
        style="position:absolute;z-index:15; left:200px; top:10px; display:none;overflow-x:hidden;overflow-y:auto;"
        allowtransparency="true" width="840px" height="900px" scrolling="no" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_2"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<div id="mask" style="display:block;position: absolute;"></div>
<%@ include file="/sms/enterPhone.jsp" %>
<div id="dialog-confirm" title="提醒" style="display:none">
    <p>
        <span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>

    <div id="dialog-confirm-text"></div>
    </p>
</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
