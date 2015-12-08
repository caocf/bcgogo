var roundingSpan = 2;
var defaultVal = '';

$(function () {
    var message = "";
    $("#memberBalanceNum").text(dataTransition.rounding($("#memberBalanceNum").text(), roundingSpan));

    $("[name='check']").live("click", recalculateTotal);

    $("#checkAll").click(function () {
        if ($(this).attr("checked") == true) {
            var totalDebt = 0;
            $(".owedTd").each(function () {
                totalDebt += $(this).html() * 1;
            });
            totalDebt = totalDebt.toFixed(roundingSpan);
            $("#totalAmount").text(totalDebt);
            $("#payedAmount").val(totalDebt);
            $("#cashAmount").val(totalDebt);
            $("#bankAmount").val(defaultVal);
            $("#bankCheckAmount").val(defaultVal);
            $("#memberAmount").val(defaultVal);
            $("#depositAmount").val(defaultVal);
            $("#owedAmount").val(defaultVal);
            $("#discountAmount").val(defaultVal);
            $("#history input").attr("checked", true);
            var memberBalance = $("#memberBalanceNum").text() * 1;
            if (memberBalance > 0) {
                if (totalDebt > memberBalance) {
                    $("#memberAmount").val(memberBalance);
                    $("#cashAmount").val(dataTransition.rounding((totalDebt - memberBalance), roundingSpan));
                } else {
                    $("#memberAmount").val(totalDebt);
                    $("#cashAmount").val(defaultVal);
                }
            }
        } else {
            $("#totalAmount").text(defaultVal);
            $("#payedAmount").val(defaultVal);
            $("#cashAmount").val(defaultVal);
            $("#bankAmount").val(defaultVal);
            $("#bankCheckAmount").val(defaultVal);
            $("#memberAmount").val(defaultVal);
            $("#depositAmount").val(defaultVal);
            $("#owedAmount").val(defaultVal);
            $("#discountAmount").val(defaultVal);
            $("#history input").attr("checked", false);
        }
        st.moneyColor();
    });


    $("#startDate,#endDate,#huankuanTime").bind("click",function () {
        $(this).blur();
    }).datepicker({
            "numberOfMonths": 1,
            "showButtonPanel": true,
            "changeYear": true,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": ""
        });


    $("#accountMemberNo").bind("blur", function () {
        if (!$(this).val()) {
            $("#memberBalanceNum").html("");
            return;
        }
        memberNoBlur();
    });

    //绑定查询按钮
    $("#searchReceivableBtn").bind("click", function () {
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();

        var datePattern = /^[1-9]\d{3}\-(0?[1-9]|1[0-2])\-(0?[1-9]|1\d|2\d|3[0-1])$/;
        if (startDate.length > 0 && !datePattern.test(startDate)) {
            nsDialog.jAlert("开始时间的日期格式填写不正确!");
        } else if (endDate.length > 0 && !datePattern.test(endDate)) {
            nsDialog.jAlert("结束时间的日期格式填写不正确!");
        } else if (startDate > endDate) {
            nsDialog.jAlert("开始时间应小于结束日期，请重新选择!");
        } else {
            $("#searchReceivableForm").ajaxSubmit({
                url: "arrears.do?method=getReceivablesByCustomerId",
                dataType: "json",
                type: "POST",
                success: function (data) {
                    initReceivableTable(data);
                    var customerOrSupplierIdStr;
                    if (null != data[0].receivables) {
                        customerOrSupplierIdStr = data[0].receivables[0].customerIdStr
                    }
                    var paramJson = {
                        startPageNo: 1,
                        customerOrSupplierIdStr: customerOrSupplierIdStr,
                        startDateStr: startDate,
                        endDateStr: endDate,
                        orderByFlag: $("#orderByFlagInput").attr("orderByFlag"),
                        orderByField: $("#orderByFieldInput").attr("orderByField"),
                        receiptNo: $("#receiptNo").val()
                    };
                    initPages(data, "dynamical1", "arrears.do?method=getReceivablesByCustomerId", '', "initReceivableTable", '', '', paramJson, '');
                },
                error: function () {
                    nsDialog.jAlert("查询异常！");
                }
            });
        }
    });

    /*
     点击升序倒序箭头
     */
    $("#cosumeTime").live("click", function () {
        if ($(this).attr("orderbyFlag") == "ASC") {
            $(this).removeClass("arrearUp").addClass("arrearDown");
            $(this).attr("orderbyFlag", "DESC");
            $("#orderByFlagInput").attr("orderbyFlag", "DESC");
            $("#orderByFieldInput").attr("orderByField", "payTime");
        } else if ($(this).attr("orderbyFlag") == "DESC") {
            $(this).removeClass("arrearDown").addClass("arrearUp");
            $(this).attr("orderbyFlag", "ASC");
            $("#orderByFlagInput").attr("orderbyFlag", "ASC");
            $("#orderByFieldInput").attr("orderByField", "payTime");
        }
        ajaxSearchReceivableForm();
    });

    $("#repayTime").live("click", function () {
        if ($(this).attr("orderbyFlag") == "ASC") {
            $(this).removeClass("arrearUp").addClass("arrearDown");
            $(this).attr("orderbyFlag", "DESC");
            $("#orderByFlagInput").attr("orderbyFlag", "DESC");
            $("#orderByFieldInput").attr("orderByField", "remindTime");
        } else if ($(this).attr("orderbyFlag") == "DESC") {
            $(this).removeClass("arrearDown").addClass("arrearUp");
            $(this).attr("orderbyFlag", "ASC");
            $("#orderByFlagInput").attr("orderbyFlag", "ASC");
            $("#orderByFieldInput").attr("orderByField", "remindTime");
        }
        ajaxSearchReceivableForm();
    });


    function ajaxSearchReceivableForm() {
        var url = "arrears.do?method=getReceivablesByCustomerId";
        var orderByFlag = $("#orderByFlagInput").attr("orderByFlag");
        var orderByField = $("#orderByFieldInput").attr("orderByField");
        var customerId = $("#customerId").val();
        var paramJson = {
            startPageNo: 1,
            customerOrSupplierIdStr: customerId,
            orderByFlag: orderByFlag,
            orderByField: orderByField
        };
        APP_BCGOGO.Net.syncPost({
            url: url,
            dataType: "json",
            data: paramJson,
            success: function (json) {
                initReceivableTable(json);
                initPages(json, "dynamical1", "arrears.do?method=getReceivablesByCustomerId", '', "initReceivableTable", '', '', paramJson, '');
            }
        });

    }

    $("#btnSettle").click(function () {
        var name = "";
        var phone = "";
        if (parent.document.getElementById("name") != null) {
            name = parent.document.getElementById("name").value;
        }
        if (parent.document.getElementById("customer") != null) {
            name = parent.document.getElementById("customer").value;
        }
        if (parent.document.getElementById("mobile") != null) {
            phone = parent.document.getElementById("mobile").value;
        }

        if ($("#totalAmount").text() * 1 == 0) {
            alert("请选择要结算的欠款单据");
            return;
        }
        if ($("#totalAmount").text() * 1 == 0 && $("#payedAmount").val() * 1 == 0 && $("#owedAmount").val() * 1 == 0) {
            alert("请选择要结算的欠款单据");
            return;
        }


        //判断实收和总计
        if ($("#payedAmount").val() * 1 == $("#totalAmount").text() * 1) {
            message = "";
        }
        var totalAmount = Number($("#totalAmount").text());
        var payedAmount = Number($("#payedAmount").val());
        if (message != "") {
            alert(message);
        } else {

            var checkResult = checkDate();
            if (checkResult != true) {
                return;
            }


            $(this).attr('disabled', 'disabled');
            //提交表单
            var customerId = $("#customerId").val();
            var totalAmount = $("#totalAmount").text();
            var payedAmount = $("#payedAmount").val();
            var owedAmount = $("#owedAmount").val();
            if (owedAmount == null || owedAmount == "") {
                owedAmount = 0.0;
            }
            var orderIdsString = ""; //所有单子ID
            var orderTypesString = ""; //所有单子的类型
            var receivableOrderIdsString = ""; //收款单Id
            var licenseNosString = ""; //车牌
            var orderTotalsString = ""; //单子的消费额
            var orderOwedsString = ""; //单子的欠款额
            var orderPayedsString = "";
            var debtIdsString = "";

            if ($("#orderType").val()) {
                //单据结算界面的修改
                orderIdsString = $("#id", window.parent.document).val();
                orderTypesString = $("#orderType").val();
                receivableOrderIdsString = $("#receivableId").val();
                licenseNosString = $("#debtVehicle").val();
                orderTotalsString = $("#debtTotalAmount").val();
                orderOwedsString = $("#debtMoney").val();
                orderPayedsString = $("#debtOrderPayed").val();
                debtIdsString = $("#debtId").val();
                customerId = $("#debtCustomerId").val();
            }
            else {
                var count = $(".check").size();
                for (var i = 1; i <= count; i++) {
                    if ($($(".check").get(i - 1)).attr("checked") == true) {
                        var orderId = $("#orderId" + i).val();
                        var orderType = $("#orderType" + i).val();
                        ;
                        var receviableOrderId = $("#rId" + i).val();
                        ;
                        var licenseNo = $("#vechicle" + i).val();
                        ;
                        var orderOwed = $("#debt" + i).val();
                        ;
                        var orderTotal = $("#oneTotals" + i).val();
                        ;
                        var orderPayed = $("#onePayed" + i).val();
                        ;
                        var debtId = $("#debtId" + i).val();
                        ;
                        if (i == count) {
                            orderIdsString = orderIdsString + orderId;
                            orderTypesString = orderTypesString + orderType;
                            receivableOrderIdsString = receivableOrderIdsString + receviableOrderId;
                            licenseNosString = licenseNosString + licenseNo;
                            orderOwedsString = orderOwedsString + orderOwed;
                            orderTotalsString = orderTotalsString + orderTotal;
                            orderPayedsString = orderPayedsString + orderPayed;
                            debtIdsString = debtIdsString + debtId;
                        } else {
                            orderIdsString = orderIdsString + orderId + ",";
                            orderTypesString = orderTypesString + orderType + ",";
                            receivableOrderIdsString = receivableOrderIdsString + receviableOrderId + ",";
                            licenseNosString = licenseNosString + licenseNo + ",";
                            orderOwedsString = orderOwedsString + orderOwed + ",";
                            orderTotalsString = orderTotalsString + orderTotal + ",";
                            orderPayedsString = orderPayedsString + orderPayed + ",";
                            debtIdsString = debtIdsString + debtId + ",";
                        }
                    }
                }
            }

            var huankuanTime = document.getElementById("huankuanTime").value;
            huankuanTime = (huankuanTime == null ? "" : huankuanTime);


            var discount = modifyStringToNumber($("#discountAmount").val());
            var cashAmount = modifyStringToNumber($("#cashAmount").val());
            var bankAmount = modifyStringToNumber($("#bankAmount").val());
            var bankCheckAmount = modifyStringToNumber($("#bankCheckAmount").val());
            var bankCheckNo = $("#bankCheckNo").val();
            var deposit = modifyStringToNumber($("#depositAmount").val());

            var accountMemberNo = $.trim($("#accountMemberNo").val());
            var accountMemberPassword = $.trim($("#accountMemberPassword").val());
            var memberAmount = modifyStringToNumber($("#memberAmount").val());
            if ($("#bankCheckNo").val() == "支票号") {
                bankCheckNo = "";
            }
            var url = "txn.do?method=payAll&customerId=" + customerId + "&totalAmount=" + totalAmount
                + "&payedAmount=" + payedAmount + "&owedAmount=" + owedAmount + "&orderIdsString=" + orderIdsString
                + "&orderTypesString=" + orderTypesString + "&receivableOrderIdsString=" + receivableOrderIdsString
                + "&licenseNosString=" + encodeURI(licenseNosString) + "&orderTotalsString=" + orderTotalsString
                + "&orderOwedsString=" + orderOwedsString + "&orderPayedsString=" + orderPayedsString
                + "&name=" + encodeURI(name) + "&phone=" + phone + "&debtIdsString=" + debtIdsString
                + "&huankuanTime=" + huankuanTime + "&discount=" + discount + "&cashAmount=" + cashAmount
                + "&bankAmount=" + bankAmount + "&depositAmount=" + deposit + "&bankCheckAmount=" + bankCheckAmount + "&bankCheckNo=" + encodeURI(bankCheckNo)
                + "&accountMemberNo=" + encodeURI(accountMemberNo) + "&accountMemberPassword=" + encodeURI(accountMemberPassword)
                + "&memberAmount=" + memberAmount;

            APP_BCGOGO.Net.syncPost({
                url: url,
                dataType: 'json',
                success: function (json) {
                    settleCallBack(json);
                },
                error: function () {
                    nsDialog.jAlert("结算失败!");
                }
            });
        }

    });
});

function printReceivable() {
    var checkSize = $(".check").size();
    var customerId = $("#customerId").val();
    var totalAmount = $("#totalAmount").text();
    var payedAmount = $("#payedAmount").val();
    var orderId = "";

    if ($("#orderType").val()) {
        orderId = "," + $("#id", window.parent.document).val();
        customerId = $("#debtCustomerId").val();
    }
    else {
        for (var i = 1; i <= checkSize; i++) {
            var test = $($(".check").get(i)).attr("checked");
            if ($($(".check").get(i - 1)).attr("checked") == true) {
                orderId += "," + $("#orderId" + i).val();
            }
        }
    }

    window.showModalDialog("txn.do?method=printDebtArrears&customerId=" + customerId + "&orderId=" + orderId + "&totalAmount=" + totalAmount + "&payedAmount=" + payedAmount + "&now=" + new Date(), "", "dialogWidth=1024px;dialogHeight=768px");
}


function initReceivableTable(jsonStr) {
    var dialogHeight = 620; //dialogHeight用于动态控制弹出框高度
    $("#history tr:not(:first)").remove();
    jsonStr = jsonStr[0].receivables;
    if (jsonStr == null) {
        return;
    }
    var jsonLen = jsonStr.length;
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length; i++) {
            var receivableIdStr = jsonStr[i].receivableIdStr == null ? " " : jsonStr[i].receivableIdStr;
            var vehicleNumber = jsonStr[i].vehicleNumber == null ? " " : jsonStr[i].vehicleNumber;
            var id = jsonStr[i].id == null ? " " : jsonStr[i].id;
            var receiptNo = jsonStr[i].receiptNo == null ? " " : jsonStr[i].receiptNo;
            var orderIdStr = jsonStr[i].orderIdStr == null ? " " : jsonStr[i].orderIdStr;
            var orderTimeStr = jsonStr[i].orderTimeStr == null ? " " : jsonStr[i].orderTimeStr;
            var vehicleNo = jsonStr[i].vehicleNumber == null ? " " : jsonStr[i].vehicleNumber;
            var content = jsonStr[i].content == null ? " " : jsonStr[i].content;
            var totalAmount = jsonStr[i].totalAmount == null ? " " : jsonStr[i].totalAmount;
            var settledAmount = jsonStr[i].settledAmount == null ? " " : jsonStr[i].settledAmount;
            var discount = jsonStr[i].discount == null ? " " : jsonStr[i].discount;
            var debt = jsonStr[i].debt == null ? " " : jsonStr[i].debt;
            var remindTimeStr = jsonStr[i].remindTimeStr == null ? " " : jsonStr[i].remindTimeStr;
            var orderType = jsonStr[i].orderType == null ? " " : jsonStr[i].orderType;
            totalAmount = dataTransition.rounding(totalAmount, roundingSpan);
            settledAmount = dataTransition.rounding(settledAmount, roundingSpan);
            discount = dataTransition.rounding(discount, roundingSpan);
            debt = dataTransition.rounding(debt, roundingSpan);
            var tr = '<tr class="table-row-original">';
            //       tr +='<td><input type="checkbox" class="check" name="check" value="'+debt+'" onclick="chooseArrear(\'' + debt+'\',\''+jsonLen+'\',this)"  id=check'+(i+1) + '/></td>';
            tr += '<td><input type="checkbox" class="check" name="check" value="' + debt + '"  id=check' + (i + 1) + ' style="margin-left:5px;" /></td>';
            tr += '<input type="hidden" id="orderId' + (i + 1) + '" value="' + orderIdStr + '" name="orderId"/>';
            tr += '<input type="hidden" id="orderType' + (i + 1) + '" value="' + orderType + '" name="orderType"/>';
            tr += '<input type="hidden" id="rId' + (i + 1) + '" value="' + receivableIdStr + '" name="rId"/>';
            tr += '<input type="hidden" id="vechicle' + (i + 1) + '" value="' + vehicleNumber + '" name="vechicle"/>';
            tr += '<input type="hidden" id="debt' + (i + 1) + '" value="' + debt + '" name="debt" />';
            tr += '<input type="hidden" id="oneTotals' + (i + 1) + '" value="' + totalAmount + '" name="oneTotals"/>';
            tr += '<input type="hidden" id="onePayed' + (i + 1) + '" value="' + settledAmount + '" name="settledAmount"/>';
            tr += '<input type="hidden" id="debtId' + (i + 1) + '" value="' + id + '" name="debtId"/>';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td>' + '<a href ="#" style="color:#005DB7" onclick="openTxnOrder(\'' + orderIdStr + '\',\'' + orderType + '\')">' + receiptNo + '</a> ' + '</td> ';
            tr += '<td>' + orderTimeStr + '</td>';

            if (!APP_BCGOGO.Permission.Version.VehicleConstruction) {
                tr += '<td style="display: none" title="' + vehicleNo + '">' + vehicleNo + '</td> ';
            } else {
                tr += '<td>' + vehicleNo + '</td>';
            }
            tr += '<td>' + content + '</td>';
            tr += '<td title=\'' + totalAmount + '\'>' + totalAmount + '</td>';
            tr += '<td title=\'' + settledAmount + '\'>' + settledAmount + '</td>';
            tr += '<td title=\'' + discount + '\'>' + discount + '</td>';
            tr += '<td class="owedTd" style="color: #FF0000;" title=\'' + debt + '\'>' + debt + '</td>';
            if (remindTimeStr <= getCurrentTime()) {
                tr += '<td style="color: #FF0000;">' + remindTimeStr + '</td>';
            } else {
                tr += '<td>' + remindTimeStr + '</td>';
            }
            tr += '</tr >';
            $("#history").append($(tr));
            dialogHeight += 55;
        }
    }
    window.parent.document.getElementById("iframe_PopupBox_2").style.height = dialogHeight + "px";
    resetAllInput();
    tableUtil.tableStyle("#history", '.title_settle');
}

function resetAllInput() {
    $("#checkAll").attr("checked", false);
    $("#totalAmount").text(defaultVal);
    $("#payedAmount").val(defaultVal);
    $("#cashAmount").val(defaultVal);
    $("#bankAmount").val(defaultVal);
    $("#bankCheckAmount").val(defaultVal);
    $("#depositAmount").val(defaultVal);
    $("#memberAmount").val(defaultVal);
    $("#owedAmount").val(defaultVal);
    $("#discountAmount").val(defaultVal);
    st.moneyColor();
}

//提交之前进行校验
function checkDate() {

    var cashAmount = $("#cashAmount").val();
    var bankAmount = $("#bankAmount").val();
    var bankCheckAmount = $("#bankCheckAmount").val();
    var bankCheckNo = $("#bankCheckNo").val();
    var depositAmount = $("#depositAmount").val();

    var accountMemberNo = $("#accountMemberNo").val();
    var accountMemberPassword = $("#accountMemberPassword").val();

    var memberAmount = $("#memberAmount").val();
    var huankuanTime = $("#huankuanTime").val();
    var result = false;
    var message = "";

    if (memberAmount != null && memberAmount != undefined && memberAmount != "" && memberAmount != 0) {
        result = true;
    }

    if (result == true) {
        if (accountMemberNo == null || accountMemberNo == undefined || $.trim(accountMemberNo) == "") {
            message = "请输入会员卡号";
        } else {
            var parentWinMemberNo = $('#accountMemberNo').attr('data-memberNo');
            if (accountMemberNo != parentWinMemberNo) {
                if (!confirm("此会员号非本客户的会员号码,是否确认结算")) {
                    message = "此会员号非本客户的会员号码,是否确认结算";
                }
            } else {
                var memberBalance = modifyStringToNumber($("#memberBalance").val());
                if (parseFloat(memberAmount) > parseFloat(memberBalance)) {
                    message = "该客户会员余额不足,无法结算";
                }
            }

            var ajaxUrl = "member.do?method=checkMemberBalance";
            var ajaxData = {
                memberNo: accountMemberNo,
                memberPasswordStr: accountMemberPassword,
                memberAmountStr: memberAmount
            };

            APP_BCGOGO.Net.syncAjax({
                url: ajaxUrl,
                dataType: "json",
                data: ajaxData,
                success: function (json) {
                    var checkResult = json.resu;
                    if (checkResult == "success") {
                        result = false;
                    } else {
                        message = checkResult;
                    }
                }
            });
        }
    }
    if (message != "") {
        showMessage.fadeMessage("35%", "40%", "slow", 3000, message);
        return false;
    }
    var validateResult = validateLocal();
    if (validateResult != true) {
        return false;
    }
    return true;
}


function validateLocal() {
    var foo = APP_BCGOGO.Validator;
    var total = $("#totalAmount").text() * 1;

    var discount = $("#discountAmount").val();
    if (null != discount && "" != discount && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(discount == 0.0 ? "0" : discount, 2))) {
        showMessage.fadeMessage("35%", "40%", "slow", 2000, "请填写正确的价格");
        $("#discountAmount").val("");
        return false;
    }

    var debt = $("#owedAmount").val();
    if (null != debt && "" != debt && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(debt == 0.0 ? "0" : debt, 2))) {
        showMessage.fadeMessage("35%", "40%", "slow", 2000, "请填写正确的价格");
        $("#owedAmount").val("");
        return false;
    }
    var cash = $("#cashAmount").val();
    if (null != cash && "" != cash && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(cash == 0.0 ? "0" : cash, 2))) {
        showMessage.fadeMessage("35%", "40%", "slow", 2000, "请填写正确的价格");
        $("#cashAmount").val("");
        return false;
    }
    var bankCard = $("#bankAmount").val();
    if (null != bankCard && "" != bankCard && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(bankCard == 0.0 ? "0" : bankCard, 2))) {
        showMessage.fadeMessage("35%", "40%", "slow", 2000, "请填写正确的价格");
        $("#bankAmount").val("");
        return false;
    }

    var memberAmount = $("#memberAmount").val();
    if (null != memberAmount && "" != memberAmount && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(memberAmount == 0.0 ? "0" : memberAmount, 2))) {
        showMessage.fadeMessage("35%", "40%", "slow", 2000, "请填写正确的价格");
        $("#memberAmount").val("");
        return false;
    }

    var check = $("#bankCheckAmount").val();
    if (null != check && "" != check && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(check == 0.0 ? "0" : check, 2))) {
        showMessage.fadeMessage("35%", "40%", "slow", 2000, "请填写正确的价格");
        $("#bankCheckAmount").val("");
        return false;
    }

    var deposit = $("#depositAmount").val() || 0;
    if (null != deposit && "" != deposit && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(deposit == 0.0 ? "0" : deposit, 2))) {
        showMessage.fadeMessage("35%", "40%", "slow", 2000, "请填写正确的价格");
        $("#deposit").val("");
        return false;
    }

    discount = discount * 1;
    debt = debt * 1;
    cash = cash * 1;
    bankCard = bankCard * 1;
    check = check * 1;
    memberAmount = memberAmount * 1;
    memberAmount = isNaN(memberAmount) ? 0 : memberAmount;
    deposit = deposit * 1;
    if (debt > 0) {

        if ($("#huankuanTime").val()) {
            var huankuanDate = $("#huankuanTime").val().replace(/[^\d]+/g, "");
            var year = new Date().getFullYear();
            var month = new Date().getMonth() + 1;
            var day = new Date().getDate();
            var nowDate = "" + year + (month.toString().length == 1 ? ("0" + month) : month) + (day.toString().length == 1 ? ("0" + day) : day);

            if (huankuanDate * 1 < nowDate * 1) {
                showMessage.fadeMessage("35%", "40%", "slow", 2000, "还款时间必须大于当前时间！");
                $("#huankuanTime").val("");
                return false;
            }
        }
    }
    else {
        $("#huankuanTime").val("");
    }

    if (total <= 0) {
        alert("请选择要结算的欠款单据");
        return false;
    }

    if (total != 0) {

        if (!GLOBAL.Number.equalsTo($("#payedAmount").val(), cash + bankCard + check + memberAmount + deposit)) {
            var msg = "实收金额与现金、银行、会员储值";
            msg += "或支票的金额不符，请修改。如果挂账或优惠赠送，请输入0";
            alert(msg);
            return false;
        }

        if (GLOBAL.Number.equalsTo($("#payedAmount").val(), cash + bankCard + check + memberAmount + deposit) && !GLOBAL.Number.equalsTo($("#payedAmount").val(), total - discount - debt)) {
            alert("实收金额与优惠、挂账金额不符合，请修改。");
            return false;
        }

        if (GLOBAL.Number.equalsTo($("#payedAmount").val(), cash + bankCard + check + memberAmount + deposit) &&
            GLOBAL.Number.equalsTo($("#payedAmount").val(), total - discount - debt)) {
            var msg = "本次结算应收：" + total + "元"
            if (discount > 0) {
                msg += ",优惠：" + discount + "元";
            }
            if (debt > 0) {
                msg += ",挂账" + debt + "元";
            }
            if ($("#payedAmount").val() > 0) {
                msg += "\n\n";
                msg += "实收：" + $('#payedAmount').val() + "元(";
                msg += cash > 0 ? "现金：" + cash + "元," : "";
                msg += bankCard > 0 ? "银行卡：" + bankCard + "元," : "";
                msg += memberAmount > 0 ? "会员储值" + memberAmount + "元," : "";
                msg += deposit > 0 ? "预收款" + deposit + "元," : "";
                msg += check > 0 ? "支票：" + check + "元)" : ")";
                if (msg.substring(msg.length - 2, msg.length - 1) == ',') {
                    msg = msg.substring(0, msg.length - 2) + ")";
                }
            }
            if (!confirm(msg)) {
                return false;
            } else {
                return true;
            }
        }
    }
}


//清除：挂账、现金、银行、支票为空，实收=0

function clear() {
    $("#cashAmount").val(defaultVal);
    $("#bankAmount").val(defaultVal);
    $("#bankCheckAmount").val(defaultVal);
    $("#memberAmount").val(defaultVal)
    $("#depositAmount").val(defaultVal);
    $("#payedAmount").val(defaultVal);
}

function openTxnOrder(orderId, orderType) {
    if (orderType == "REPAIR") {
        window.open('txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderId);
    } else if (orderType == "SALE") {
        window.open('sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=' + orderId);
    } else if (orderType == "WASH_BEAUTY") {
        window.open('washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=' + orderId);
    }
}

function recalculateTotal() {
    //获得选中的应付款记录
    var totalCreadit = 0;
    var count = 0;
    $("[name='check']").each(function () {
        if ($(this).is(':checked')) {
            var temp = $(this).val() * 1;
            if (temp == undefined || temp == null || temp == "") temp = 0;
            totalCreadit = totalCreadit + parseFloat(temp);
            count = count + 1;
        }
    });
    if (count == $("[name='check']").size()) {
        $("#checkAll").attr("checked", 'true'); //
    } else {
        $("#checkAll").removeAttr("checked"); //
    }

    $("#totalAmount").text(dataTransition.rounding(totalCreadit, roundingSpan));
    $("#payedAmount").val(dataTransition.rounding(totalCreadit, roundingSpan));
    $("#owedAmount").val(defaultVal);
    $("#discountAmount").val(defaultVal);
    var memberBalance = $("#memberBalanceNum").text() * 1;
    if (totalCreadit > memberBalance) {
        $("#memberAmount").val(memberBalance);
        $("#cashAmount").val(dataTransition.rounding((totalCreadit - memberBalance), roundingSpan));
        $("#bankAmount").val(defaultVal);
        $("#bankCheckAmount").val(defaultVal);
        $("#depositAmount").val(defaultVal);
        $("#owedAmount").val(defaultVal);
        $("#discountAmount").val(defaultVal);

    } else {
        $("#memberAmount").val(totalCreadit);
        $("#cashAmount").val(defaultVal);
        $("#bankAmount").val(defaultVal);
        $("#bankCheckAmount").val(defaultVal);
        $("#depositAmount").val(defaultVal);
        $("#owedAmount").val(defaultVal);
        $("#discountAmount").val(defaultVal);
    }
    st.moneyColor();
}

/**
 * 处理有会员处理的情况
 */

function memberBalanceHandler(totalCreadit, memberBalance) {

}

/**
 * 获取当前时间
 */

function getCurrentTime() {
    var now = new Date();
    var year = now.getFullYear();
    var month = now.getMonth() + 1;
    var day = now.getDate();
    return year + "-" + month + "-" + day;
}

function freshPage(json) {

    //修改 totalDept
    var totalDebt = json.totalDebt;
    if (parent.document.getElementById("allDebt")) {
        parent.document.getElementById("allDebt").innerHTML = (totalDebt);
        if (totalDebt == 0) {
            $(window.parent.document).find("#allDebt").css("display", "none");
            $(window.parent.document).find("#a_jiesuan").css("display", "none");
        }
    }

    //车辆页面
    if (parent.document.getElementById("iframe_qiankuan") != null) {
        parent.document.getElementById("iframe_qiankuan").style.display = "none";
    }
    if (parent.document.getElementById("iframe_PopupBox_2") != null) {
        parent.document.getElementById("iframe_PopupBox_2").style.display = "none";
    }

    $(window.parent.document).find("#mask").hide();

    //判断是否为"待办事项"页面
    if (parent.$.url && parent.$.url().param('method') == 'newtodo') {
        parent.location.reload();
    }
}

function settleCallBack(json) {
    if (!json) {
        GLOBAL.log(("json is null after txn.do?method=payAll"));
        return;
    }

    if (json.result == "fail") {
        if (json.failMsg) {
            nsDialog.jAlert(json.failMsg);
        }
        else {
            nsDialog.jAlert("当前客户欠款有更新,请重新打开窗口后结算！");
        }
    } else {
        if ($("#receivalePrintBtn").attr("checked")) {
            printReceivable();
            if ($("#orderType").val()) {
                if (parent.document.getElementById("iframe_PopupBox_2") != null) {
                    parent.document.getElementById("iframe_PopupBox_2").style.display = "none";
                }

                $(window.parent.document).find("#mask").hide();
                window.parent.location.reload();
                return;
            }
            if ($("#receivableSearchBtn", window.parent.document)[0]) {
                $("#receivableSearchBtn", window.parent.document).click();
                if (parent.document.getElementById("iframe_PopupBox_2") != null) {
                    parent.document.getElementById("iframe_PopupBox_2").style.display = "none";
                }

                $(window.parent.document).find("#mask").hide();
            }
            else {
                freshPage(json);
            }

        } else {
            nsDialog.jConfirm("是否需要打印结算单？", "提示", function (resultValue) {
                if (resultValue) {
                    printReceivable();
                }
                if ($("#orderType").val()) {
                    if (parent.document.getElementById("iframe_PopupBox_2") != null) {
                        parent.document.getElementById("iframe_PopupBox_2").style.display = "none";
                    }

                    $(window.parent.document).find("#mask").hide();
                    window.parent.location.reload();
                    return;
                }
                if ($("#receivableSearchBtn", window.parent.document)[0]) {
                    $("#receivableSearchBtn", window.parent.document).click();
                    if (parent.document.getElementById("iframe_PopupBox_2") != null) {
                        parent.document.getElementById("iframe_PopupBox_2").style.display = "none";
                    }

                    $(window.parent.document).find("#mask").hide();
                }
                else {
                    alert("结算成功!");
                    freshPage(json);
                }
            });
        }
    }
}

function memberNoBlur() {

    APP_BCGOGO.Net.syncAjax({
        url: "member.do?method=ajaxGetCustomerWithMember",
        dataType: "json",
        data: {memberNo: $("#accountMemberNo").val()},
        success: function (json) {
            if (json.memberDTO.balanceStr != null) {
                $("#memberBalanceNum").html(json.memberDTO.balanceStr);
            } else {
                $("#memberBalanceNum").html("0.0");
            }
        },
        error: function () {
            $("#memberBalanceNum").html("0.0");
        }
    });
}

$(function () {
    if ($("#orderType").val()) {
        var totalCreadit = $("#debtMoney").val();
        totalCreadit = totalCreadit * 1;
        $("#totalAmount").text(dataTransition.rounding(totalCreadit, roundingSpan));
        $("#payedAmount").val(dataTransition.rounding(totalCreadit, roundingSpan));
        $("#owedAmount").val(defaultVal);
        $("#discountAmount").val(defaultVal);
        var memberBalance = $("#memberBalanceNum").text() * 1;
        if (totalCreadit > memberBalance) {
            memberBalance == 0 && (memberBalance = '');
            $("#memberAmount").val(memberBalance);
            $("#cashAmount").val(dataTransition.rounding((totalCreadit - memberBalance), roundingSpan));
            $("#bankAmount").val(defaultVal);
            $("#bankCheckAmount").val(defaultVal);
            $("#depositAmount").val(defaultVal);
            $("#owedAmount").val(defaultVal);
            $("#discountAmount").val(defaultVal);
        } else {
            totalCreadit == 0 && (totalCreadit = '');
            $("#memberAmount").val(totalCreadit);
            $("#cashAmount").val(defaultVal);
            $("#bankAmount").val(defaultVal);
            $("#bankCheckAmount").val(defaultVal);
            $("#depositAmount").val(defaultVal);
            $("#owedAmount").val(defaultVal);
            $("#discountAmount").val(defaultVal);
        }
    }
});

$(function () {
    st.initDom = function () {
        st.dom.orderTotal = $('#totalAmount');
        st.dom.cashAmount = $('#cashAmount');
        st.dom.bankAmount = $('#bankAmount');
        st.dom.bankCheckAmount = $('#bankCheckAmount');
        st.dom.memberAmount = $('#memberAmount');
        st.dom.settledAmount = $('#payedAmount');
        st.dom.settledAmountLabel = $('#settledAmountLabel');
        st.dom.accountDebtAmount = $('#owedAmount');
        st.dom.accountDiscount = $('#discountAmount');
        st.dom.discount = $('#discount');
        st.dom.confirmBtn = $('#btnSettle');
        st.dom.memberDiscountCheck = $('#memberDiscountCheck');
        st.dom.discountWord = $('#discountWord');
        st.dom.memberAmount.length || (st.dom.memberAmount = $('#depositAmount'));
    }
    st.start(function(){
        $('#depositAmount').length && st.dom.memberAmount.bind('input',function () {
            var available = getNumber($('#depositAvailable').text());
            var prepaid = st.dom.memberAmount.val();
            prepaid > available && st.dom.memberAmount.val(available);
        }).unbind('dblclick').bind('dblclick', function () {
                var amount = st.getAmount();
                var available = getNumber($('#depositAvailable').text());
                if(available >= amount.orderTotal){
                    $.proxy(st.handle.dblclick, this)();
                }else{
                    alert('可用定金不足！请重新输入！');
                }
            });
    });
    console.log('应收结算');
});