$(function () {

    $("#supplierName").html($("#supplier", window.parent.document).val());

    if ("true" == $("print", window.parent.document).val()) {
        $("print")[0].checked == true;
    }

    var total = $("#total", window.parent.document)[0] ? $("#total", window.parent.document).val() : $("#total").val();

    total = modify(total);
    $("#returnTotal").html(total);
    $("#settledAmount").val(total);
    $("#cash").val(total);
    var depositAmount = $("#depositAmount", window.parent.document).val();
    var totalPayable = $("#totalPayable").html();
    depositAmount = modify(depositAmount);
    totalPayable = modify(totalPayable);

    $("#totalPayable").html(APP_BCGOGO.StringFilter.inputtingPriceFilter($("#totalPayable").html(), 2));

    if ($("#purchaseReturnId").val() != '') {
        $("#surePayStroage").bind("click", function () {
            if ($("#surePayStroage").attr("disabled")) {
                return;
            }

            $("#surePayStroage").attr("disabled", true);

            var total = $("#returnTotal").html();
            total = modify(total);

            var cash = $("#cash").val();
            var depositAmount = $("#depositAmount").val();

            var bankAmount = $("#bankAmount").val();

            var bankCheckAmount = $("#bankCheckAmount").val();
            cash = modify(cash);
            depositAmount = modify(depositAmount);
            bankAmount = modify(bankAmount);
            bankCheckAmount = modify(bankCheckAmount);
            var accountDebtAmount = modify($("#accountDebtAmount").val());
            var accountDiscount = modify($("#accountDiscount").val());
            var settledAmount = modify($("#settledAmount").val());

            if (!GLOBAL.Number.equalsTo(total, settledAmount + accountDebtAmount + accountDiscount)) {
                nsDialog.jAlert("实收+挂账+优惠 不等于 总额，请修改");
                $("#surePayStroage").removeAttr("disabled");
                return;
            }

            if (!GLOBAL.Number.equalsTo(settledAmount, cash + depositAmount + bankAmount + bankCheckAmount)) {
                nsDialog.jAlert("现金+转预付款+银联+支票 不等于 实收，请修改");
                $("#surePayStroage").removeAttr("disabled");
                return;
            }

            var msg = "本次结算应收：" + total + "元"
            if (accountDiscount > 0) {
                msg += ",优惠：" + accountDiscount + "元";
            }
            if (accountDebtAmount > 0) {
                msg += ",挂账" + accountDebtAmount + "元";
            }
            if (settledAmount > 0) {
                msg += "\n\n";
                msg += "实收：" + settledAmount + "元(";
                msg += cash > 0 ? "现金：" + cash + "元," : "";
                msg += bankAmount > 0 ? "银联：" + bankAmount + "元," : "";
                msg += bankCheckAmount > 0 ? "支票：" + bankCheckAmount + "元," : "";
                msg += depositAmount > 0 ? "转预付款：" + depositAmount + "元," : "";

                msg = msg.substring(0, msg.length - 1) + ")";
            }

            if (!confirm(msg)) {
                $("#surePayStroage").removeAttr("disabled");
                return;
            }

            if (bankCheckAmount == 0) {
                $("#bankCheckNo").val("");
            }
            $("#returnAccountDetailSettleForm").ajaxSubmit({
                dataType: "json",
                success: function (json) {
                    if (json.success) {
                        nsDialog.jAlert(json.msg, "结算成功", function () {
                            if ($("#print")[0].checked) {
                                window.parent.location.href = window.parent.location.href + "&print=true";
                            } else {
                                window.parent.location.reload();
                            }
                        });
                    } else {
                        nsDialog.jAlert(json.msg);
                    }
                },
                error: function () {
                    nsDialog.jAlert("出现异常，结算失败！");
                }
            });
        });
    } else {
        $("#surePayStroage").bind("click", function () {

            if ($("#id", window.parent.document).val()) {
                return;
            }

            if ($("#surePayStroage").attr("disabled")) {
                return;
            }

            $("#surePayStroage").attr("disabled", true);

            var total = $("#returnTotal").html();
            total = modify(total);

            var cash = $("#cash").val();
            var depositAmount = $("#depositAmount").val();

            var bankAmount = $("#bankAmount").val();

            var bankCheckAmount = $("#bankCheckAmount").val();

            cash = modify(cash);
            depositAmount = modify(depositAmount);
            bankAmount = modify(bankAmount);
            bankCheckAmount = modify(bankCheckAmount);
            var accountDebtAmount = modify($("#accountDebtAmount").val());
            var accountDiscount = modify($("#accountDiscount").val());
            var settledAmount = modify($("#settledAmount").val());
            var huankuanTime = $("#huankuanTime").val();
            if (!G.isEmpty(huankuanTime)) {
                var huankuanDate = huankuanTime.replace(/[^\d]+/g, "");
                var year = new Date().getFullYear();
                var month = new Date().getMonth() + 1;
                var day = new Date().getDate();
                var nowDate = "" + year + (month.toString().length == 1 ? ("0" + month) : month) + (day.toString().length == 1 ? ("0" + day) : day);
                if (huankuanDate * 1 < nowDate * 1) {
                    alert("还款时间必须大于当前时间！");
                    $("#surePayStroage").removeAttr("disabled");
                    return false;
                    N
                }
            }

            if (!GLOBAL.Number.equalsTo(total, settledAmount + accountDebtAmount + accountDiscount)) {
                nsDialog.jAlert("实收+挂账+优惠 不等于 总额，请修改");
                $("#surePayStroage").removeAttr("disabled");
                return;
            }

            if (!GLOBAL.Number.equalsTo(settledAmount, cash + depositAmount + bankAmount + bankCheckAmount)) {
                nsDialog.jAlert("现金+预付款+银联+支票 不等于 实收，请修改");
                $("#surePayStroage").removeAttr("disabled");
                return;
            }

            var msg = "本次结算应收：" + total + "元"
            if (accountDiscount > 0) {
                msg += ",优惠：" + accountDiscount + "元";
            }
            if (accountDebtAmount > 0) {
                msg += ",挂账" + accountDebtAmount + "元";
            }
            if (settledAmount > 0) {
                msg += "\n\n";
                msg += "实收：" + settledAmount + "元(";
                msg += cash > 0 ? "现金：" + cash + "元," : "";
                msg += bankAmount > 0 ? "银联：" + bankAmount + "元," : "";
                msg += bankCheckAmount > 0 ? "支票：" + bankCheckAmount + "元," : "";
                msg += depositAmount > 0 ? "转付款：" + depositAmount + "元," : "";

                msg = msg.substring(0, msg.length - 1) + ")";
            }

            if (!confirm(msg)) {
                $("#surePayStroage").removeAttr("disabled");
                return;
            }

            if (bankCheckAmount == 0) {
                $("#bankCheckNo").val("");
            }

            //把现金，冲账，定金赋给父页面提交
            assignmentToParentValue(cash, depositAmount, bankAmount, bankCheckAmount, settledAmount, accountDebtAmount, accountDiscount, huankuanTime);

            if ($("#print")[0].checked) {
                $("#print", window.parent.document).val("true");
            }
            var $parent = $(window.parent.document);
            $parent.find("#supplier").attr("disabled", false)
            $("#purchaseReturnForm", window.parent.document).submit();

            if ("null" == $("#supplierShopId", window.parent.document).val()) {
                $("#supplierShopId", window.parent.document).val("");
            }
        });
    }
});


/**
 * 刷新应付款表
 *
 */
function refreshPayableTable() {
    var supplierId = $("#supplierId", window.parent.document).val();
    APP_BCGOGO.Net.syncPost({
        url: "goodsReturn.do?method=getTotalPayable",
        data: {supplierId: supplierId, now: new Date()},
        cache: false,
        async: false,
        dataType: "json",
        success: function (jsonStr) {

            if (jsonStr.resu == "0.0") {
                $("#totalPayable").html("0.0");
            }
            else {
                $("#totalPayable").html(APP_BCGOGO.StringFilter.inputtingPriceFilter(jsonStr.resu, 2));
            }
        }
    });
}

function modify(value) {
    if (value == null || value == undefined || value == "") {
        value = 0;
    }
    return value * 1;
}

function assignmentToParentValue(cash, depositAmount, bankAmount, bankCheckAmount, settledAmount, accountDebtAmount, accountDiscount, huankuanTime) {
    $("#cash", window.parent.document).val(modify(cash));
    $("#strikeAmount", window.parent.document).val(0);
    $("#depositAmount", window.parent.document).val(modify(depositAmount));
    $("#bankAmount", window.parent.document).val(modify(bankAmount));
    $("#bankCheckAmount", window.parent.document).val(modify(bankCheckAmount));

    $("#bankCheckNo", window.parent.document).val($("#bankCheckNo").val());
    $("#accountDiscount", window.parent.document).val(modify(accountDiscount));
    $("#accountDebtAmount", window.parent.document).val(modify(accountDebtAmount));
    $("#settledAmount", window.parent.document).val(modify(settledAmount));
    $("#huankuanTime", window.parent.document).val(huankuanTime);
}

function clear() {
    $("#cash").val("");
    $("#settledAmount").val(0);
    $("#depositAmount").val("");
    $("#bankCheckAmount").val("");
    $("#bankAmount").val("");
    $("#discountWord").hide();
}

$(function () {
    st.initDom = function () {
        st.dom.orderTotal = $('#returnTotal');
        st.dom.cashAmount = $('#cash');
        st.dom.bankAmount = $('#bankAmount');
        st.dom.bankCheckAmount = $('#bankCheckAmount');
        st.dom.prepaidAmount = $('#depositAmount');
        st.dom.settledAmount = $('#settledAmount');
        st.dom.accountDebtAmount = $('#accountDebtAmount');
        st.dom.accountDiscount = $('#accountDiscount');
        st.dom.discount = $('#discount');
        st.dom.confirmBtn = $('#surePayStroage');
        st.dom.discountWord = $('#discountWord');
        st.dom.memberAmount.length || (st.dom.memberAmount = $('#depositAmount'));
    };
    st.start();
    console.log('入库退货');
});