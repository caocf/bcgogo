<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>欠款明细</title>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/qianKuan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreUserinfo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
	<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css" />
    <style type="text/css">
        table2 td {
            text-overflow: ellipsis;
        }

        html {
            overflow-x: hidden;
            overflow-y: hidden;
        }

        #ui-datepicker-div, .ui-datepicker {
            font-size: 90%;
        }

        .isDatepickerInited {

        }
    </style>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>

    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/clientInfo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        function prePage(pageNo, customerId) {
            if (pageNo <= 1) {
                alert("已是第一页!");
                return;
            }
            pageNo--;
            window.location = "txn.do?method=detailsArrears&pageNo=" + pageNo + "&customerId=" + customerId;
        }

        function sendSms(mobile, type, arrears, licenceNo, date, name) {        // type <!-- 0 保险  1 验车  2 生日-->
            if (mobile == null || $.trim(mobile) == "") {
                alert("手机号码不能为空!");
                return;
            }
            if (licenceNo == null || $.trim(licenceNo) == "") {
                alert("车牌号不能为空!");
                return;
            }
            if (date == null || $.trim(date) == "") {
                alert("日期不能为空!");
                return;
            }

            var dates = date.split("-");
            var month = dates[1];
            var day = dates[2];
            window.location = encodeURI("sms.do?method=smswrite&mobile=" + $.trim(mobile) + "&type=" + type + "&money=" + arrears + "&licenceNo=" + licenceNo + "&month=" + month + "&day=" + day + "&name=" + name + "&customerId=" + customerIdStr);
        }

        //author:zhangjuntao
        var time = new Array(), timeFlag1 = true, timeFlag2 = true;
        time[0] = new Date().getTime();
        time[1] = new Date().getTime();
        time[2] = new Date().getTime();
        time[3] = new Date().getTime();
        var reg = /^\d+(\.{0,1}\d*)$/;
        $(document).ready(function () {
            $("#payedAmount").keyup(function (event) {
                var filter = APP_BCGOGO.StringFilter;
                this.value = filter.inputtingPriceFilter(this.value,2);
            });
            $("#owedAmount").keyup(function (event) {
                dataTransition.amountConvert(time[2], time[3], "#owedAmount", timeFlag2);
                var totalAmount = parseFloat($("#totalAmount").val());
                var payedAmount = parseFloat($("#payedAmount").val());
                var owedAmount = totalAmount - payedAmount;
                if (parseFloat($("#owedAmount").val()) + payedAmount > totalAmount) {
                    $("#owedAmount").val(owedAmount);
                    message = "欠款和实收之和不得大于总计！";
                    showMessage.fadeMessage("35%", "40%", "slow", 2000, message);
                }
            });

            // init datepicker
            $("#startDate,#endDate")
                    .bind("click", function () {
                        $(this).blur();
                    })
                    .datetimepicker({
                        "numberOfMonths":1,
                        "showButtonPanel":true,
                        "changeYear":true,
                        "changeMonth":true,
                        "yearRange":"c-100:c+100",
                        "showSecond":true,
                        "timeFormat":"hh:mm:ss",
                        "yearSuffix":"",
                        "yearRange":"c-100:c+100"
                    });
        });

        function showDatepicker(node) {
            if ($(node).hasClass("isDatepickerInited")) {
                $(node).removeClass("isDatepickerInited");
                $(node).datepicker({
                    "changeYear":true,
                    "changeMonth":true,
                    "showButtonPanel":true,
                    "numberOfMonths":1,
                    "yearRange":"c-100:c+100",
                    "showOn":"span",
                    "yearSuffix":""
                });
            }
            $(node).datepicker("show");


            $(node).blur();
        }

    </script>

</head>

<body style="background:none repeat scroll 0 0 transparent">
<%@ include file="/common/messagePrompt.jsp" %>
<div id="div_show" class="i_supplierInfo_qian_kuan more_supplier_qian_kuan" style="overflow:hidden">


<div class="i_arrow"></div>

<div class="i_upCenter_qian_kuan">
    <div class="i_note_qian_kuan" id="div_drag">欠款结算</div>
    <div class="i_close" id="div_close1" style="display: block;"></div>
</div>
<div class="height"></div>
<div class="i_upBody_qian_kuan">
<div id="div_arrear" class="clear">
<div class="more_his"
     style="color: #000;font-weight:bold;font-size:14px;margin-bottom:5px;padding-left:15px;float:left ">
    <!-- TODO 增加大小值的判断 -->
    <form id="from_searchArrear" action="txn.do?method=detailsArrears" method="post">
        共有<span class="hover">${pager.totalRows}</span>条历史记录
        <!-- 搜索日期-->
        <input type="hidden" id="customerId" name="customerId" value="${customerId}"/>
        <input type="hidden" id="memberNumber" name="memberNumber" value="${memberNumber}"/>
        <input type="hidden" id="memberBalance" name="memberBalance" value="${memberBalance}"/>
        <!-- datepicker -->
        <input id="startDate" name="startDate" class="txt" value="" type="text"
               style="width:155px;border:1px solid #7F9DB9;line-height:18px;"/>
        <input id="endDate" name="endDate" value="" class="txt" type="text"
               style="width:155px;border:1px solid #7F9DB9;line-height:18px;"/>

        <input id="searchBtn" name="searchBtn" value="搜索" type="submit" class="searcg_button"
               onfocus="this.blur();">
    </form>
</div>
<table cellpadding="0" cellspacing="0" class="table2" id="history" style="margin:0px 15px;">
    <col width="35"/>
    <col width="49"/>
    <col width="120"/>
    <col width="96"/>
    <col width="115"/>
    <col width="86"/>
    <col width="81"/>
    <col width="61"/>
    <col width="66"/>
    <col width="66"/>
    <tr class="title_his">
        <td style="border-left:none;"></td>
        <td>NO</td>
        <td>消费时间</td>
        <td>车牌号</td>
        <td>内容</td>
        <td>施工</td>
        <td>材料/品名</td>
        <td>消费金额</td>
        <td>实收金额</td>
        <td style="border-right:none;">欠款金额</td>
    </tr>
    <c:if test="${DebtDTOs != null}">
        <%int i = 1;%>
        <c:forEach items="${DebtDTOs}" var="debt">
            <tr>
                <td style="border-left:none;">
                    <div class="chk"><input type="checkbox" id="check<%=i%>" name="check" class="check"/>
                        <input type="hidden" id="orderId<%=i%>" value="${debt.orderId}" name="orderId"/>
                        <input type="hidden" id="orderType<%=i%>" value="${debt.orderType}"
                               name="orderType"/>
                        <input type="hidden" id="rId<%=i%>" value="${debt.recievableId}" name="rId"/>
                        <input type="hidden" id="vechicle<%=i%>" value="${debt.vehicleNumber}"
                               name="vechicle"/>
                        <input type="hidden" id="debt<%=i%>" value="${debt.debt}" name="debt"/>
                        <input type="hidden" id="oneTotals<%=i%>" value="${debt.totalAmount}"
                               name="oneTotals"/>
                        <input type="hidden" id="onePayed<%=i%>" value="${debt.settledAmount}"
                               name="settledAmount"/>
                        <input type="hidden" id="debtId<%=i%>" value="${debt.id}" name="debtId"/>

                    </div>
                </td>
                <td><%=i++%>
                </td>
                <td class="blue" title="${debt.date}">
                    <c:if test="${debt.orderType=='SALE'}">
                        <a href="#"
                           onclick="doPage('sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=${debt.orderId}&type=txn&vehicleNumber=${debt.vehicleNumber}');">${debt.date}</a>
                    </c:if>
                    <c:if test="${debt.orderType =='REPAIR'}">
                        <a href="#"
                           onclick="doPage('txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=${debt.orderId}');">${debt.date}</a>
                    </c:if>
                    <c:if test="${debt.orderType=='MEMBER_BUY_CARD'}">
                        <span style="color:#000">${debt.date}</span>
                    </c:if>
                    <c:if test="${debt.orderType=='WASH_BEAUTY'}">
                        <a href="#"
                           onclick="doPage('washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=${debt.orderId}');">${debt.date}</a>
                    </c:if>
                </td>
                <td class="blue" title="${debt.vehicleNumber}">

                    <c:if test="${debt.orderType=='SALE'}">
                        <a href="#"
                           onclick="doPage('sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=${debt.orderId}&type=txn&vehicleNumber=${debt.vehicleNumber}');">${debt.vehicleNumber}</a>
                    </c:if>
                    <c:if test="${debt.orderType =='REPAIR'}">
                        <a href="#"
                           onclick="doPage('txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=${debt.orderId}');">${debt.vehicleNumber}</a>
                    </c:if>
                    <c:if test="${debt.orderType=='MEMBER_BUY_CARD'}">
                        ${debt.vehicleNumber}
                    </c:if>
                    <c:if test="${debt.orderType=='WASH_BEAUTY'}">
                        <a href="#"
                           onclick="doPage('washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=${debt.orderId}');">${debt.vehicleNumber}</a>
                    </c:if>
                </td>
                <td title="${debt.content}">${debt.content}</td>
                <td title="${debt.service}">${debt.service}</td>
                <td title="${debt.material}">${debt.shortMaterialStr}</td>
                <td class="totalTd" title="${debt.totalAmount}">${debt.totalAmount}</td>
                <td class="payedTd" title="${debt.settledAmount}">${debt.settledAmount}</td>
                <td style="border-right:none;color:red;" class="owedTd" title="${debt.debt}">
                    <c:if test="${debt.orderType =='SALE'}">
                        <a href="#" style="color:red;"
                           onclick="doPage('sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=${debt.orderId}&type=txn&vehicleNumber=${debt.vehicleNumber}');">${debt.debt}</a>
                    </c:if>
                    <c:if test="${debt.orderType =='REPAIR'}">
                        <a href="#" style="color:red;"
                           onclick="doPage('txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=${debt.orderId}');">${debt.debt}</a>
                    </c:if>
                    <c:if test="${debt.orderType=='MEMBER_BUY_CARD'}">
                        <a style="color:#000">${debt.debt}</a>
                    </c:if>

                    <c:if test="${debt.orderType=='WASH_BEAUTY'}">
                        <a href="#" style="color:red;"
                           onclick="doPage('washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=${debt.orderId}');">${debt.debt}</a>
                    </c:if>

                </td>
            </tr>
        </c:forEach>
    </c:if>
</table>
<div class="clear"></div>
<!--分页-->
<div class="his_bottom" style="margin-left: 12px">
    <div class="chk" id="all_chk"><input type="checkbox" id="checkAll" name="checkAll"/></div>
    <strong style="color:#000">全选</strong>
    <bcgogo:paging url="txn.do?method=detailsArrears&customerId=${customerId}&startDate=${startDate}&endDate=${endDate}" />
    <%--<jsp:include page="/common/paging.jsp">
        <jsp:param name="url"
                   value="txn.do?method=detailsArrears&customerId=${customerId}&startDate=${startDate}&endDate=${endDate}"></jsp:param>
    </jsp:include>--%>

    <%--<div class="more_his">共有<span class="hover">${DebtCount}</span>条历史记录</div>--%>
    <div class="clear"></div>
</div>

<div class="clear"></div>
<div style="padding-left:15px;width:100%; ">
    <div class="postTitle clear" style="border:none;line-height:20px;color: #000000;margin-top:-10px;">
        <span style="font-weight:bold;">欠款回笼</span>
    </div>


    <div class="clear"></div>
    <table cellpadding="0" cellspacing="0" class="tabTotal" style="padding:0">
        <col width="190"/>
        <col/>
        <tr class="title">
            <td style="padding:0px;">总&nbsp;&nbsp;计:<span id="totalAmount" name="totalAmount" class="span">0</span></td>
            <td>
                优 惠:<input type="text" id="discountAmount" name="discountAmount" class="tab_input" value="" style="width:100px;"/>
            </td>
            <td>
                挂 账:<input type="text" name="owedAmount" id="owedAmount" class="tab_input" value="" style="width:100px;"/>
            </td>

            <td>
                <span style="float:left;">还款日期:</span><input type="text" autocomplete="off" readonly="true"
                                                             id="huankuanTime"
                                                             name="huankuanTime" class="tab_input isDatepickerInited"
                                                             style="width:84px; float:left;margin-top:5px;"
                                                             onclick="showDatepicker(this)"/>
            </td>
        </tr>
        <tr>
            <td>现&nbsp;&nbsp;&nbsp;&nbsp;金：<input id="cashAmount" name="cashAmount" type="text" style="width:120px;"/></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>银行卡：<input type="text" id="bankAmount" name="bankAmount" style="width:120px;"/></td>
            <td></td>
        </tr>
        <tr>
            <td>
                支&nbsp;&nbsp;&nbsp;&nbsp;票：<input type="text" id="bankCheckAmount" name="bankCheckAmount" style="width:120px;"/>

                <div class="divNum">
                    号&nbsp;&nbsp;&nbsp;&nbsp;码：<input class="tab_input" id="bankCheckNo" name="bankCheckNo" type="text" style="width:97px;">
                </div>
            </td>
            <td colspan="3" style="font-weight:bold; font-size:14px; text-align:right; padding-right:10px;">
                <span class="words">实 &nbsp;&nbsp;收</span>:
                <input id="payedAmount" name="payedAmount" type="text" class="tab_input" value="" style="width:120px;"/>
            </td>

        </tr>
        <tr>
            <td colspan="2">会员储值：<input type="text" id="memberAmount" name="memberAmount" style="width:120px;"/></td>
            <td></td>
        </tr>
    </table>


    <div style="padding-left:30px; background:#FBECB9; color:#000000;">
        <div class="clear height"></div>
        <div style="float:left;padding-right:10px;">请刷卡/输入卡号：<input type="text" id="accountMemberNo" name="accountMemberNo" style="width:100px;"/></div>
        密&nbsp;&nbsp;码：<input type="password" id="accountMemberPassword" name="accountMemberPassword" style="width:100px;"/>
    </div>


    <div class="tableInfo" style="padding:0px 15px;margin-top: 5px">
        <div class="i_operaPic">
            <div class="i_settlement" style="width: 50px;float: right;margin-top: 25px;">
                <input type="button" onfocus="this.blur();" id="btnSettle"/>

                <div style="width: 50px;color: #000000;text-align: center;"><a style="color: #000000">结算</a></div>
            </div>

        </div>
        <div class="table_btn" style="position: relative;float: right;width:auto;padding-top:25px">
            <input type="button" value="取消" id="cancelButton" class="i_operate" onfocus="this.blur();"/>
            <input type="button" value="打印" class="i_operate" id="debtPrintBtn" onfocus="this.blur();"/>
        </div>
        <div class="height"></div>

        <div class="tableInfo" style="padding:0px 0px;margin-top: 110px;overflow:hidden">
        </div>


    </div>

</div>


</div>
</div>
</div>

<div id="mask" style="display:block;position: absolute;">
</div>
<iframe id="iframe_PopupBoxMakeTime" style="position:absolute;z-index:10;display:none; " allowtransparency="true"
        width="350px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<iframe name="iframe_PopupBox" id="iframe_PopupBox"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none; " allowtransparency="true" width="1000px"
        height="800px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:6;top:210px;left:87px;display:none; "
        allowtransparency="true" width="1000px" height="800px" frameborder="0" src=""></iframe>

<div id="systemDialog"></div>


</body>
</html>

