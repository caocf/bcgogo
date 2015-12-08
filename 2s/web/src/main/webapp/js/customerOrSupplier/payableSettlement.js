var roundingSpan = 2;
var defaultVal = '';
$(function () {
    $("#startDate,#endDate").bind("click",function () {
        $(this).blur();
    }).datepicker({
            "numberOfMonths": 1,
            "showButtonPanel": true,
            "changeYear": true,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": ""
        });

    $("[name='check']").live("click", function (event) {
        if ($(this).attr("checked")) {
            $("#depositAmount").attr("class", "arrears_blue");
            $("#cash").attr("class", "arrears_blue");
        } else {
            $("#depositAmount").removeAttr("class", "arrears_blue");
            $("#cash").removeAttr("class", "arrears_blue");
        }
        recalculateTotal();

    });

    $("#surePay").bind("click", function () {
        var lstPayAbles = new Array();
        //获得选中的应付款记录

        if ($("#orderType").val() == "purchaseInventoryOrder") {
            var temp = {
                "idStr": $.trim($("#payableId").val())
            };

            if (lstPayAbles == null) {
                lstPayAbles = [];
            }
            lstPayAbles.push(temp);
        }
        else {
            $("[name='check']").each(function () {
                if ($(this).is(':checked')) {
                    var temp = {
                        "idStr": $.trim($(this).val())
                    };
                    if (lstPayAbles == null) {
                        lstPayAbles = [];
                    }
                    lstPayAbles.push(temp);
                }
            });
        }
        if (lstPayAbles.length == 0) {
            alert("请选择单据进行结算！");
            return;
        }
        var payTotal = dataTransition.rounding(parseFloat($("#totalCreditAmount").text() == "" ? 0 : $("#totalCreditAmount").text()), roundingSpan); //应付
        //扣款(优惠)
        var deduction = dataTransition.rounding(parseFloat($("#deduction").val() == "" ? 0 : $("#deduction").val()), roundingSpan);
        //欠款挂账
        var creditAmount = dataTransition.rounding(parseFloat($("#creditAmount").val() == "" ? 0 : $("#creditAmount").val()), roundingSpan);
        var cash = dataTransition.rounding(parseFloat($("#cash").val() == "" ? 0 : $("#cash").val()), roundingSpan);
        var bankCardAmount = dataTransition.rounding(parseFloat($("#bankCardAmount").val() == "" ? 0 : $("#bankCardAmount").val()), roundingSpan);
        var checkAmount = dataTransition.rounding(parseFloat($("#checkAmount").val() == "" ? 0 : $("#checkAmount").val()), roundingSpan);
        var checkNo = $("#checkNo").val();
        //用预付款
        var depositAmount = dataTransition.rounding(parseFloat($("#depositAmount").val() == "" ? 0 : $("#depositAmount").val()), roundingSpan);
        //实付
        var actuallyPaidPop = dataTransition.rounding(parseFloat($("#actuallyPaid").val() == "" ? 0 : $("#actuallyPaid").val()), roundingSpan);
        var actuallyPaid = dataTransition.rounding(cash + bankCardAmount + checkAmount + depositAmount, roundingSpan);
        var supplierId = $("#supplierId").val();
        //可用预付款
        var depositAvaiable = dataTransition.rounding(parseFloat($.trim($("#deposit_avaiable").text()) == "" ? 0 : $.trim($("#deposit_avaiable").text())), roundingSpan);
        /**
         * 实付金额=0
         *判断：应付<>扣款+挂账，则提示：“实付为0，是否挂账或扣款免付？”，挂扣免
         *选择挂账，则清除现金、银行、支票为空，挂账=应收-扣款
         *选择扣款免付，则清除现金、银行、支票为空，扣款=应付-挂账
         */
        if (depositAmount > depositAvaiable + 0.0001) {
            alert("可用预付款不足！请选择其他支付方式！");
            return;
        }
        if (actuallyPaid == 0) {
            if (actuallyPaid != dataTransition.rounding(payTotal - deduction - creditAmount, roundingSpan)) {
                alert("应付金额与扣款、挂账金额不符合，请修改。");
            }
            if (payTotal != dataTransition.rounding(deduction + creditAmount, roundingSpan)) {
                $("#creditDeductionBtn").css("display", "block");
                creditAmount = dataTransition.rounding($("#creditAmount").val() == "" ? 0 : $("#creditAmount").val(), roundingSpan);
                deduction = dataTransition.rounding($("#deduction").val() == "" ? 0 : $("#deduction").val(), roundingSpan);
            } else {
                if (confirm("实付为0，请再次确认是否挂账或扣款免付!")) {
                    $(this).attr('disabled', 'disabled');
                    var payableHistoryDTO = {
                        "deduction": deduction,
                        "creditAmount": creditAmount,
                        "cash": cash,
                        "bankCardAmount": bankCardAmount,
                        "checkAmount": checkAmount,
                        "checkNo": checkNo,
                        "depositAmount": depositAmount,
                        "actuallyPaid": actuallyPaid,
                        "supplierId": supplierId
                    };
                    if ($("#checkDetailPrint").attr("checked")) {
                        printPayable();
                    } else {
                        nsDialog.jConfirm("是否需要打印结算单？", "提示", function (resultValue) {
                            if (resultValue) {
                                printPayable();
                            }

                        });
                    }

                    APP_BCGOGO.Net.asyncPost({
                        url: "payable.do?method=payToSupplier",
                        data: {
                            lstPayAbles: JSON.stringify(lstPayAbles),
                            payableHistoryDTO: JSON.stringify(payableHistoryDTO)
                        },
                        cache: false,
                        dataType: "json",
                        success: function (jsonStr) {
                            if (jsonStr.message == "success") {
                                $("#payDetail").css("display", "none");
                                //更新供应商详细页面，应付金额
                                updateCreditAmount();
                                //更新供应商详细页面，付预付款
                                updateDeposit();
                                //             refreshPayableTable();
                                //                recalculateTotal();
                                setZero();
                                closeDialog();
                                nsDialog.jAlert("结算成功！", null, function () {
                                    var _pageType = window.parent.document.getElementById("pageType");
                                    if (_pageType) {
                                        if (_pageType.value == "uncleSupplier") {
                                            window.parent.location = "unitlink.do?method=supplier&supplierId=" + $("#supplierId").val();
                                        }
                                        if (_pageType.value == "supplierData") {
                                            window.parent.location = "customer.do?method=searchSuppiler";
                                        }

                                    }
                                    if ($("#orderType").val() == "purchaseInventoryOrder") {
                                        window.location.reload();
                                    }
                                });
                                if ($("#orderType").val() == "purchaseInventoryOrder") {
                                    window.parent.location.reload();

                                }
                            } else {
                                alert("结算失败！");
                            }
                        }
                    });
                }
            }
        } else { //实付金额不为0
            /**
             *       实付<>(现金+银行+支票),
             *         弹出提示： “实付金额与现金、银行、支票的金额不符，请修改。
             *        如果挂账或扣款免付，请输入 0。”确定后返回。
             */
            if (actuallyPaidPop != dataTransition.rounding(cash + bankCardAmount + checkAmount + depositAmount, roundingSpan)) {
                alert("实付金额与现金、银行、支票、预付款的金额不符，请修改。  如果挂账或扣款免付，请输入 0!");
            } else if (actuallyPaid != dataTransition.rounding(payTotal - deduction - creditAmount, roundingSpan)) {
                /**
                 * 判断：实付<>(应付-扣款-挂账),提示:“实付金额与扣款、
                 *        挂账金额不符合，请修改。” 确定后返回。
                 */
                alert("应付金额与扣款、挂账金额不符合，请修改。");
            } else {
                if (confirm("本次结算应付：" + payTotal + " 元，扣款：" + deduction + " 元，挂账" + creditAmount + " 元，实付：" + actuallyPaid + " 元（现金：" + cash + " 元，银行卡：" + bankCardAmount + " 元，支票：" + checkAmount + " 元，预付款：" + depositAmount + " 元）")) {
                    $(this).attr('disabled', 'disabled');
                    var payableHistoryDTO = {
                        "deduction": deduction,
                        "creditAmount": creditAmount,
                        "cash": cash,
                        "bankCardAmount": bankCardAmount,
                        "checkAmount": checkAmount,
                        "checkNo": checkNo,
                        "depositAmount": depositAmount,
                        "actuallyPaid": actuallyPaid,
                        "supplierId": supplierId
                    };
                    APP_BCGOGO.Net.asyncPost({
                        url: "payable.do?method=payToSupplier",
                        data: {
                            lstPayAbles: JSON.stringify(lstPayAbles),
                            payableHistoryDTO: JSON.stringify(payableHistoryDTO)
                        },
                        cache: false,
                        dataType: "json",
                        success: function (jsonStr) {
                            if (jsonStr.message == "success") {
                                if ($("#checkDetailPrint").attr("checked")) {
                                    printPayable();
                                    $("#payDetail").css("display", "none");
                                    updateCreditAmount();
                                    //更新供应商详细页面，付预付款
                                    updateDeposit();
                                    //                    recalculateTotal();
                                    setZero();
                                    closeDialog();
                                    nsDialog.jAlert("结算成功！", null, function () {
                                        var _pageType = window.parent.document.getElementById("pageType");
                                        if (_pageType) {
                                            if (_pageType.value == "uncleSupplier") {
                                                window.parent.location = "unitlink.do?method=supplier&supplierId=" + $("#supplierId").val();
                                            }
                                            if (_pageType.value == "supplierData") {
                                                window.parent.location = "customer.do?method=searchSuppiler";
                                            }

                                        }
                                        if ($("#orderType").val() == "purchaseInventoryOrder") {
                                            window.location.reload();
                                        }
                                    });
                                } else {
                                    nsDialog.jConfirm("是否需要打印结算单？", "提示", function (resultValue) {
                                        if (resultValue) {
                                            printPayable();
                                        }
                                        $("#payDetail").css("display", "none");
                                        updateCreditAmount();
                                        //更新供应商详细页面，付预付款
                                        updateDeposit();
                                        //                        recalculateTotal();
                                        setZero();
                                        closeDialog();
                                        nsDialog.jAlert("结算成功！", null, function () {
                                            if (window.parent.document.getElementById("pageType") && window.parent.document.getElementById("pageType").value == "uncleSupplier") {
                                                window.parent.location = "unitlink.do?method=supplier&supplierId=" + $("#supplierId").val();
                                            }
                                            if (window.parent.document.getElementById("pageType") && window.parent.document.getElementById("pageType").value == "supplierData") {
                                                window.parent.location = "customer.do?method=searchSuppiler";
                                            }
                                        });

                                        if ($("#orderType").val() == "purchaseInventoryOrder") {
                                            window.parent.location.reload();
                                        }

                                        if ($("#payableSearchBtn", window.parent.document)[0]) {
                                            $("#payableSearchBtn", window.parent.document).click();
                                        }
                                    }, true);
                                }

                            } else if (GLOBAL.Lang.isEmpty(jsonStr.message)) {
                                nsDialog.jAlert("结算失败！");
                            } else {
                                nsDialog.jAlert(jsonStr.message);
                            }
                        }
                    });
                }
            }
        }

    });

    function closeDialog() {
        if (parent.document.getElementById("iframe_PopupBox_1") != null) {
            parent.document.getElementById("iframe_PopupBox_1").style.display = "none";
        }
        if (parent.document.getElementById("mask") != null) {
            parent.document.getElementById("mask").style.display = "none";
        }
    }

    /**
     *     全选
     */
    $("#checkAll").bind("click", function () {
        if ($(this).is(':checked')) {
            $("[name='check']").attr("checked", 'true'); //
            var totalCreadit = 0;
            $("[name='check']").each(function () {
                if ($(this).is(':checked')) {
                    var temp = $(this).parent().parent().children('td').eq(7).html();
                    if (temp == undefined || temp == null || temp == "") temp = 0;
                    totalCreadit = totalCreadit + parseFloat(temp);
                }
            });

            $("#totalCreditAmount").text(totalCreadit);
            $("#actuallyPaid").val(totalCreadit);
            $("#cash").val(totalCreadit);
            $("#bankCardAmount").val(defaultVal);
            $("#checkAmount").val(defaultVal);
            $("#depositAmount").val(defaultVal);
            $("#creditAmount").val(defaultVal);
            $("#deduction").val(defaultVal);
            $("#history input").attr("checked", true);

            var depositAvailable = $("#deposit_avaiable").text() * 1;
            if (depositAvailable > 0) {
                if (totalCreadit > depositAvailable) {
                    $("#depositAmount").val(depositAvailable);
                    $("#cash").val(dataTransition.rounding((totalCreadit - depositAvailable), roundingSpan));
                } else {
                    $("#cash").val(defaultVal);
                    $("#depositAmount").val(totalCreadit);
                }
            }

        } else {
            $("[name='check']").removeAttr("checked"); //
            $("#totalCreditAmount").text(defaultVal);
            $("#actuallyPaid").val(defaultVal);
            $("#cash").val(defaultVal);
            $("#bankCardAmount").val(defaultVal);
            $("#checkAmount").val(defaultVal);
            $("#depositAmount").val(defaultVal);
            $("#creditAmount").val(defaultVal);
            $("#deduction").val(defaultVal);
        }
        st.moneyColor();
    });


    //绑定查询按钮
    $("#searchPayableBtn").bind("click", function () {
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();
        if (startDate > endDate) {
            nsDialog.jAlert("开始时间应小于结束日期，请重新选择!");
            return;
        }
        $("#searchPayableForm").ajaxSubmit({
            url: "arrears.do?method=getPayablesBySupplierId",
            dataType: "json",
            type: "POST",
            success: function (data) {
                var payables = data[0].payables
                initPayableTable(data);
                var paramJson = {
                    startPageNo: 1,
                    customerOrSupplierIdStr: payables.customerId,
                    startDateStr: startDate,
                    endDateStr: endDate
                };
                initPages(data, "dynamical1", "arrears.do?method=getPayablesBySupplierId", '', "initPayableTable", '', '', paramJson, '');
            },
            error: function () {
                alert("查询异常！");
            }
        });
    });

});


/**
 * 应付结算弹出框初始化
 */

function initPayableTable(jsonStr) {
    var dialogHeight = 550; //dialogHeight用于动态控制弹出框高度
    $("#history tr:not(:first)").remove();
    jsonStr = jsonStr[0].payables;
    if (jsonStr == null) {
        return;
    }
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length; i++) {
            var idStr = jsonStr[i].idStr == null ? " " : jsonStr[i].idStr;
            var receiptNo = jsonStr[i].receiptNo == null ? " " : jsonStr[i].receiptNo;
            var purchaseInventoryIdStr = jsonStr[i].purchaseInventoryIdStr == null ? " " : jsonStr[i].purchaseInventoryIdStr;
            var payTimeStr = jsonStr[i].payTimeStr == null ? " " : jsonStr[i].payTimeStr; //todo 入库时间？
            var materialName = jsonStr[i].materialName == null ? " " : jsonStr[i].materialName;
            var amount = jsonStr[i].amount == null ? " " : jsonStr[i].amount;
            var paidAmount = jsonStr[i].paidAmount == null ? " " : jsonStr[i].paidAmount;
            var creditAmount = jsonStr[i].creditAmount == null ? " " : jsonStr[i].creditAmount;
            var deduction = jsonStr[i].deduction == null ? " " : jsonStr[i].deduction;
            paidAmount = dataTransition.rounding(paidAmount, roundingSpan);
            amount = dataTransition.rounding(amount, roundingSpan);
            creditAmount = dataTransition.rounding(creditAmount, roundingSpan);
            deduction = dataTransition.rounding(deduction, roundingSpan);
            var str = "'" + purchaseInventoryIdStr + "'";
            var tr = '<tr class="table-row-original">';
            tr += '<td><input type="checkbox" style="margin-left:5px;" name="check" class="check" value="' + idStr + '" id=check' + (i + 1) + ' /></td>';
            tr += '<input type="hidden" id="payableId' + i + '" value="' + idStr + '" name="payableId"/></td>';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td>' + '<a href ="#" style="color:#005DB7" onclick="openInventory(\'' + purchaseInventoryIdStr + '\')">' + receiptNo + '</a> ' + '</td> ';
            tr += '<td>' + payTimeStr + '</td>';
            tr += '<td title=\'' + materialName + '\'>' + materialName.substring(0, 25) + '</td>';
            tr += '<td title=\'' + amount + '\'>' + amount + '</td>';
            tr += '<td title=\'' + paidAmount + '\'>' + paidAmount + '</td>';
            tr += '<td class="owedTd" title=\'' + creditAmount + '\'>' + creditAmount + '</td>';
            //        tr += '<td title=\'' + deduction + '\'>' + deduction + '</td>';
            tr += '</tr>';
            $("#history").append($(tr));
            dialogHeight += 70;
        }
    }
    window.parent.document.getElementById("iframe_PopupBox_1").style.height = dialogHeight + "px";
    resetAllInput();
    tableUtil.tableStyle("#history", '.title_settle');
}

function resetAllInput() {
    $("#checkAll").removeAttr("checked");
    $("#totalCreditAmount").text(defaultVal);
    $("#actuallyPaid").val(defaultVal);
    $("#cash").val(defaultVal);
    $("#bankCardAmount").val(defaultVal);
    $("#checkAmount").val(defaultVal);
    $("#depositAmount").val(defaultVal);
    $("#creditAmount").val(defaultVal);
    $("#deduction").val(defaultVal);
    st.moneyColor();
}

function openInventory(idStr) {
    window.open('storage.do?method=getPurchaseInventory&purchaseInventoryId=' + idStr + '&type=txn&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE');
}

function recalculateTotal() {
    //获得选中的应付款记录
    var totalCreadit = 0;
    var count = 0;
    $("[name='check']").each(function () {
        if ($(this).is(':checked')) {
            var temp = $(this).parent().parent().children('td').eq(7).html();
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

    $("#totalCreditAmount").text(dataTransition.rounding(totalCreadit, roundingSpan));
    $("#actuallyPaid").val(dataTransition.rounding(totalCreadit, roundingSpan));
    $("#creditAmount").val(defaultVal);
    $("#deduction").val(defaultVal);

    var depositAvailable = $("#deposit_avaiable").text() * 1;

    if (totalCreadit > depositAvailable) {
        $("#depositAmount").val(depositAvailable);
        $("#cash").val(dataTransition.rounding((totalCreadit - depositAvailable), roundingSpan));
        $("#bankCardAmount").val(defaultVal);
        $("#checkAmount").val(defaultVal);
        $("#creditAmount").val(defaultVal);
        $("#deduction").val(defaultVal);

    } else {
        $("#depositAmount").val(totalCreadit);
        $("#cash").val(defaultVal);
        $("#bankCardAmount").val(defaultVal);
        $("#checkAmount").val(defaultVal);
        $("#creditAmount").val(defaultVal);
        $("#deduction").val(defaultVal);
    }
    st.moneyColor();
}

/*每次结算后，页面金额清零，避免影响以后的打印结果*/

function setZero() {
    //父页面
    $("#actuallyPaid").val(defaultVal);
    $("#creditAmount").val(defaultVal);
    //弹出框
    $("#deduction").val(defaultVal);
    $("#creditAmount").val(defaultVal);
    $("#cash").val(defaultVal);
    $("#bankCardAmount").val(defaultVal);
    $("#checkAmount").val(defaultVal);
    $("#depositAmount").val(defaultVal);
}

function printPayable() {
    var supplierId = $("#supplierId").val(); //供应商ID
    var totalAmount = parseFloat($("#totalCreditAmount").text() == "" ? "0" : $("#totalCreditAmount").text()); //总金额
    var payedAmount = parseFloat($("#actuallyPaid").val() == "" ? "0" : $("#actuallyPaid").val()); //实付金额
    var deduction = parseFloat($("#deduction").val() == "" ? "0" : $("#deduction").val()); //扣款
    var creditAmount = parseFloat($("#creditAmount").val() == "" ? "0" : $("#creditAmount").val()); //挂账
    //详细结算页面，会有以下4个参数
    var cash = parseFloat($("#cash").val() == "" ? "0" : $("#cash").val()); //现金
    var bankCardAmount = parseFloat($("#bankCardAmount").val() == "" ? "0" : $("#bankCardAmount").val()); //银行卡
    var checkAmount = parseFloat($("#checkAmount").val() == "" ? "0" : $("#checkAmount").val()); //支票
    var depositAmount = parseFloat($("#depositAmount").val() == "" ? "0" : $("#depositAmount").val()); //预付款
    var payableId = ""; //单据ID字符串
    //要结算的单据数量
    var checkSize = $(".check").size();
    //单据ID组合字符串，材料名+数量

    if ($("#orderType").val() == "purchaseInventoryOrder") {
        payableId = "," + $("#payableId").val();
    }
    else {
        for (var i = 0; i < checkSize; i++) {
            if ($($(".check").get(i)).attr("checked") == true) {
                payableId = payableId + "," + $($(".check").get(i)).val();
            }
        }
    }

    //打印单显示
    window.open("payable.do?method=printPayable&supplierId=" + supplierId + "&payableId=" + payableId + "&totalAmount=" + totalAmount + "&payedAmount=" + payedAmount + "&deduction=" + deduction + "&creditAmount=" + creditAmount + "&cash=" + cash + "&bankCardAmount=" + bankCardAmount + "&checkAmount=" + checkAmount + "&depositAmount=" + depositAmount, "", "dialogWidth=1024px;dialogHeight=768px");
}

//供应商详细信息应付款刷新
function updateCreditAmount() {
    var supplierId = $("#supplierId").val();
    APP_BCGOGO.Net.asyncPost({
        url: "payable.do?method=getCreditAmountBySupplierId",
        data: {
            supplierId: supplierId
        },
        cache: false,
        dataType: "json",
        success: function (jsonStr) {
            $("#btnPay").val("").val("应付:" + jsonStr.message + "元")
        }
    });
}
//入库单，供应商详细信息 ；预付款刷新

function updateDeposit() {
    var supplierId = $("#supplierId").val();
    APP_BCGOGO.Net.asyncPost({
        url: "payable.do?method=getSumDepositBySupplierId",
        data: {
            supplierId: supplierId
        },
        cache: false,
        dataType: "json",
        success: function (jsonStr) {
            $("#btnPayed").val("").val("付预付款:" + jsonStr.message + "元")
            $("#deposit_avaiable").text("").text(dataTransition.rounding(jsonStr.message, roundingSpan));
        }
    });
}

$(function () {
    if ($("#orderType").val() == "purchaseInventoryOrder") {
        var totalCreadit = $("#debtMoney").val();
        if (!$("#debtMoney").val()) {
            totalCreadit = 0.0;
        }
        totalCreadit = totalCreadit * 1;
        $("#totalCreditAmount").text(dataTransition.rounding(totalCreadit, roundingSpan));
        $("#actuallyPaid").val(dataTransition.rounding(totalCreadit, roundingSpan));
        $("#creditAmount").val(defaultVal);
        $("#deduction").val(defaultVal);

        var depositAvailable = $("#deposit_avaiable").text() * 1;
        if (totalCreadit > depositAvailable) {
            depositAvailable == 0 && (depositAvailable = '');
            $("#depositAmount").val(depositAvailable);
            $("#cash").val(dataTransition.rounding((totalCreadit - depositAvailable), roundingSpan));
            $("#bankCardAmount").val(defaultVal);
            $("#checkAmount").val(defaultVal);
            $("#creditAmount").val(defaultVal);
            $("#deduction").val(defaultVal);

        } else {
            totalCreadit == 0 && (totalCreadit = '');
            $("#depositAmount").val(totalCreadit);
            $("#cash").val(defaultVal);
            $("#bankCardAmount").val(defaultVal);
            $("#checkAmount").val(defaultVal);
            $("#creditAmount").val(defaultVal);
            $("#deduction").val(defaultVal);
        }
    }
});

$(function () {
    st.initDom = function () {
        st.dom.orderTotal = $('#totalCreditAmount');
        st.dom.cashAmount = $('#cash');
        st.dom.bankAmount = $('#bankCardAmount');
        st.dom.bankCheckAmount = $('#checkAmount');
        st.dom.memberAmount = $('#depositAmount');
        st.dom.settledAmount = $('#actuallyPaid');
        st.dom.settledAmountLabel = $('#settledAmountLabel');
        st.dom.accountDebtAmount = $('#creditAmount');
        st.dom.accountDiscount = $('#deduction');
        st.dom.discount = $('#discount');
        st.dom.confirmBtn = $('#surePay');
        st.dom.discountWord = $('#discountWord');
    }
    st.start();
    console.log('应付结算');
});