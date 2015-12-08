$(function() {
    $("#settledAmount, #cashAmount").val($("#orderTotal").text());
    $("#div_close,#cancelBtn").click(function() {
        displayFrame();
    });

    $("#serviceTotal").text(dataTransition.rounding(jQuery("#serviceTotal", window.parent.document).val(), 2));
    $("#salesTotal").text(jQuery("#salesTotal", window.parent.document).val());
    $("#mobile").val(jQuery("#mobile", window.parent.document).val());

    if ($("#orderTotal").text()*1 == 0) {
        jQuery("#cashAmount,#discountAmount,#bankAmount,#bankCheckAmount,#accountDiscount,#accountDebtAmount,#settledAmount,#memberAmount,#customerDeposit").val(0).attr('disabled', 'disabled');
        $("#bankCheckNo").attr('disabled', 'disabled');
        $("#accountMemberNo").attr('disabled', 'disabled');
        $("#accountMemberPassword").attr('disabled', 'disabled');
        $("#memberDiscountCheck").attr('disabled', 'disabled');
        jQuery("#huankuanTime").attr('disabled', 'disabled');
    }

    if ($("#total", window.parent.document).val() == 0) {
        $("#cashAmount,#bankAmount,#bankCheckAmount,#bankCheckNo,#accountDiscount,#accountDebtAmount,#settledAmount,#memberAmount,#customerDeposit").val(0).attr('disabled', 'disabled');
        $("#huankuanTime").attr('disabled', 'disabled');
        $("#discount").val(10).attr('disabled', 'disabled');
        var result = false;
        $("select[name$='.consumeType']", window.parent.document).each(function() {
            if ($(this, window.parent.document).val() == "TIMES") {
                result = true;
            }
        });

        if (result == false) {
            $("#accountMemberNo,#accountMemberPassword").attr('disabled', 'disabled');
        }
    }
});

function displayFrame() {
    if (window.parent.document.getElementById("mask")) {
        window.parent.document.getElementById("mask").style.display = "none";
    }
    window.parent.document.getElementById("iframe_PopupBox_account").style.display = "none";
    window.parent.document.getElementById("iframe_PopupBox_account").src = "";
    self.frameElement.blur();
    $(window.top.document.body)
        .append($("<input type='text' id='focusBackToMe'>"))
        .find("#focusBackToMe")
        .focus()
        .select()
        .remove();
}

//更改父页面元素属性
function removeParentWindowAttr() {
    $("#brand,#model,#year,#contact,#engine,#customer,#mobile,#landLine", window.parent.document).removeAttrs("disabled");
}

//提交之前进行校验
function checkDate() {

    if ($("#orderStatus").text() == 'SETTLED') {
        nsDialog.jAlert("单据状态不可结算。", "无法结算");
        return;
    }
    if ($("#confirmBtn").attr("disabled")) {
        return;
    }

    $("#confirmBtn").attr("disabled", true);
    var cashAmount = $("#cashAmount").val();
    var bankAmount = $("#bankAmount").val();
    var bankCheckAmount = $("#bankCheckAmount").val();
    var bankCheckNo = $("#bankCheckNo").val();
    var customerDeposit = $("#customerDeposit").val();
    var discountAmount = $("#discountAmount").val();
    var accountDebtAmount = $("#accountDebtAmount").val();
    var settledAmount = $("#settledAmount").val();
    var totalReceivable = $("#receivableTotal").text();
    var strikeAmount = $("#strikeAmount").val();

    if ("支票号" == bankCheckNo) {
        bankCheckNo = "";
        $("#bankCheckNo").val("");
    }

    var validateResult = validate();
    if (validateResult != true) {
        $("#confirmBtn").removeAttr("disabled");
        return false;
    }

    cashAmount = modify(cashAmount);
    bankAmount = modify(bankAmount);
    bankCheckAmount = modify(bankCheckAmount);
    customerDeposit = modify(customerDeposit);
    discountAmount = modify(discountAmount);
    accountDebtAmount = modify(accountDebtAmount);
    settledAmount = modify(settledAmount);
    strikeAmount = modify(strikeAmount);

    setValueToParentWindow();


    if ($("#settleType").val() == "normal") {
        $("#salesReturnForm", window.parent.document).ajaxSubmit({
            url: "salesReturn.do?method=settleForNormal",
            dataType: "json",
            success: function (json) {
                if (json.success) {
                    if ($("#print")[0].checked) {
                        window.parent.location.href = "salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId" + "&salesReturnOrderId=" + json.msg + "&print=true";
                    } else {
                        window.parent.location.href = "salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId" + "&salesReturnOrderId=" + json.msg;
                    }
                } else {
                    nsDialog.jAlert(json.msg);
                }
            },
            error: function () {
                nsDialog.jAlert("出现异常，结算失败！");
            }
        });

    } else {
        $("#salesReturnForm", window.parent.document).ajaxSubmit({
            url: "salesReturn.do?method=settleForWholesaler",
            dataType: "json",
            success: function (json) {
                if (json.success) {
                    nsDialog.jAlert(json.msg, "结算成功", function () {
                        if ($("#print")[0].checked) {
                            window.parent.location.href = window.parent.location.href + "&print=true"
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
    }
}

function setValueToParentWindow() {
    $("#settledAmount", window.parent.document).val($("#settledAmount").val());
    $("#cashAmount", window.parent.document).val($("#cashAmount").val());
    $("#bankAmount", window.parent.document).val($("#bankAmount").val());
    $("#bankCheckAmount", window.parent.document).val($("#bankCheckAmount").val());
    $("#bankCheckNo", window.parent.document).val($("#bankCheckNo").val());
    $("#customerDeposit", window.parent.document).val($("#customerDeposit").val());
    $("#strikeAmount", window.parent.document).val($("#strikeAmount").val());
    $("#discountAmount", window.parent.document).val($("#discountAmount").val());
    $("#accountDebtAmount", window.parent.document).val($("#accountDebtAmount").val());
}

function disableAttr() {
    $("#cashAmount,#bankAmount,#bankCheckAmount,#bankCheckNo,#discountAmount,#accountDebtAmount,#settledAmount").attr('disabled', 'disabled');
    $("#accountMemberNo,#accountMemberPassword,#strikeAmount,#confirmBtn,#huankuanTime").attr('disabled', 'disabled');
}


function clearAccountDetailInfo() {
    window.parent.document.getElementById("cashAmount").value = 0;
    window.parent.document.getElementById("bankAmount").value = 0;
    window.parent.document.getElementById("bankCheckAmount").value = 0;
    window.parent.document.getElementById("bankCheckNo").value = "";
    window.parent.document.getElementById("customerDeposit").value = "";
    window.parent.document.getElementById("accountMemberNo").value = "";
    window.parent.document.getElementById("accountMemberPassword").value = "";
    window.parent.document.getElementById("strikeAmount").value = 0;
    window.parent.document.getElementById("huankuanTime").value = "";
}


function modify(value) {
    if (value == null || value == undefined || value == "") {
        value = 0;
    }
    return value;
}

//清除：挂账、现金、银行、支票为空，实付=0
function clear() {
    $("#cashAmount").val("");
    $("#bankAmount").val("");
    $("#bankCheckAmount").val("");
    $("#bankCheckNo").val("");
    $("#customerDeposit").val("");
    $("#strikeAmount").val("")
    $("#settledAmount").val(0);
}

//实付为0，选择挂账
function selectDebt() {
    var discount = $("#discountAmount").val();
    var total = $("#orderTotal").html() * 1;
    var debt = $("#accountDebtAmount").val();
    if (null == discount || "" == discount) {
        discount = 0;
    }
    if (null == debt || "" == debt) {
        debt = 0;
    }

    discount = discount * 1;
    debt = debt * 1;
    clear();
    $("#accountDebtAmount").val((total - discount).toFixed(2));

    $("#selectBtn").hide();
    st.moneyColor();
}

//实付为0，选择优惠
function selectDiscount() {
    var discount = $("#discountAmount").val();
    var total = $("#orderTotal").html() * 1;
    var debt = $("#accountDebtAmount").val();
    if (null == discount || "" == discount) {
        discount = 0;
    }
    if (null == debt || "" == debt) {
        debt = 0;
    }

    discount = discount * 1;
    debt = debt * 1;
    clear();
    $("#discountAmount").val((total - debt).toFixed(2));

    $("#selectBtn").hide();
    st.moneyColor();
}

function validate() {
    var foo = APP_BCGOGO.Validator;
    var total = $("#orderTotal").html() * 1;

    var discount = $("#discountAmount").val();
    if (null != discount && "" != discount && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(discount == 0.0 ? "0" : discount, 2))) {
        alert("请填写正确的价格！");
        $("#discountAmount").val("");
        return false;
    }

    var debt = $("#accountDebtAmount").val();
    if (null != debt && "" != debt && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(debt == 0.0 ? "0" : debt, 2))) {
        alert("请填写正确的价格！");
        $("#accountDebtAmount").val("");
        return false;
    }
    var cash = $("#cashAmount").val();
    if (null != cash && "" != cash && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(cash == 0.0 ? "0" : cash, 2))) {
        alert("请填写正确的价格！");
        $("#cashAmount").val("");
        return false;
    }
    var bankCard = $("#bankAmount").val();
    if (null != bankCard && "" != bankCard && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(bankCard == 0.0 ? "0" : bankCard, 2))) {
        alert("请填写正确的价格！");
        $("#bankAmount").val("");
        return false;
    }

    var strikeAmount = $("#strikeAmount").val();
    if (null != strikeAmount && "" != strikeAmount && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(strikeAmount == 0.0 ? "0" : strikeAmount, 2))) {
        alert("请填写正确的价格！");
        $("#strikeAmount").val("");
        return false;
    }

    var check = $("#bankCheckAmount").val();
    if (null != check && "" != check && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(check == 0.0 ? "0" : check, 2))) {
        alert("请填写正确的价格！");
        $("#bankCheckAmount").val("");
        return false;
    }

    // add by zhuj
    var customerDeposit = $("#customerDeposit").val();
    if (null != customerDeposit && "" != customerDeposit && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(customerDeposit == 0.0 ? "0" : customerDeposit, 2))) {
        alert("请填写正确的价格！");
        $("#customerDeposit").val("");
        return false;
    }

    if (null == discount || "" == discount) {
        discount = 0;
    }
    if (null == debt || "" == debt) {
        debt = 0;
    }
    if (null == cash || "" == cash) {
        cash = 0;
    }
    if (null == bankCard || "" == bankCard) {
        bankCard = 0;
    }

    if (null == check || "" == check) {
        check = 0;
    }
    customerDeposit = modify(customerDeposit);

    if (null == strikeAmount || "" == strikeAmount) {
        strikeAmount = 0;
    }
    discount = discount * 1;
    debt = debt * 1;
    cash = cash * 1;
    bankCard = bankCard * 1;
    check = check * 1;
    customerDeposit = customerDeposit * 1;
    strikeAmount = strikeAmount * 1;
    if (total != 0) {
        if (GLOBAL.Number.equalsTo($("#settledAmount").val(), 0) && GLOBAL.Number.equalsTo(discount + debt, total)) {
            if (!confirm("实付为0，请确认是否优惠。")) {
                return false;
            }
        }

        if (!GLOBAL.Number.equalsTo($("#settledAmount").val(), cash + bankCard + check + customerDeposit + strikeAmount)) {
            var msg = "实付金额与现金、银行、支票或冲帐的金额不符，请修改。如果优惠，请输入0。";
            alert(msg);
            return false;
        }

        if (GLOBAL.Number.equalsTo($("#settledAmount").val(), cash + bankCard + check + customerDeposit + strikeAmount) && !GLOBAL.Number.equalsTo($("#settledAmount").val(), total - discount - debt)) {
            alert("实付金额与优惠金额不符合，请修改。");
            return false;
        }

        if (GLOBAL.Number.equalsTo($("#settledAmount").val(), cash + bankCard + check + customerDeposit + strikeAmount) &&
            GLOBAL.Number.equalsTo($("#settledAmount").val(), total - discount - debt)) {
            var msg = "本次结算应付：" + total + "元"
            if (discount > 0) {
                msg += ",优惠：" + discount + "元";
            }
            if (debt > 0) {
                msg += ",挂账" + debt + "元";
            }
            if ($("#settledAmount").val() > 0) {
                msg += "\n\n";
                msg += "实付：" + $('#settledAmount').val() + "元(";
                msg += cash > 0 ? "现金：" + cash + "元," : "";
                msg += bankCard > 0 ? "银行卡：" + bankCard + "元," : "";
                msg += check > 0 ? "支票：" + check + "元," : "";
                msg += customerDeposit > 0 ? "预收款：" + customerDeposit + "元," : "";
                msg += strikeAmount > 0 ? "冲账：" + strikeAmount + "元," : "";
                if (msg.substring(msg.length - 1, msg.length) == ',') {
                    msg = msg.substring(0, msg.length - 1) + ")";
                }
            }
            if (!confirm(msg)) {
                return false;
            }
        }
    }

    handleMoneyInputEmpty();
    return true;
}

function handleMoneyInputEmpty() {
    if ($("#discountAmount").val() == null || $("#discountAmount").val() == "") {
        $("#discountAmount").val(0);
    }
    if ($("#accountDebtAmount").val() == null || $("#accountDebtAmount").val() == "") {
        $("#accountDebtAmount").val(0);
    }
    if ($("#cashAmount").val() == null || $("#cashAmount").val() == "") {
        $("#cashAmount").val(0);
    }
    if ($("#bankAmount").val() == null || $("#bankAmount").val() == "") {
        $("#bankAmount").val(0);
    }
    if ($("#bankCheckAmount").val() == null || $("#bankCheckAmount").val() == "") {
        $("#bankCheckAmount").val(0);
    }
    if ($("#customerDeposit").val() == null || $("#customerDeposit").val() == "") {
        $("#customerDeposit").val(0);
    }
    if ($("#settledAmount").val() == null || $("#settledAmount").val() == "") {
        $("#settledAmount").val(0);
    }
    if ($("#strikeAmount").val() == null || $("#strikeAmount").val() == "") {
        $("#strikeAmount").val(0);
    }
}

$(function () {
    st.initDom = function () {
        st.dom.orderTotal = $('#orderTotal');
        st.dom.cashAmount = $('#cashAmount');
        st.dom.bankAmount = $('#bankAmount');
        st.dom.bankCheckAmount = $('#bankCheckAmount');
        st.dom.settledAmount = $('#settledAmount');
        st.dom.accountDebtAmount = $('#accountDebtAmount');
        st.dom.accountDiscount = $('#discountAmount');
        st.dom.discount = $('#discount');
        st.dom.confirmBtn = $('#confirmBtn');
        st.dom.memberDiscountCheck = $('#memberDiscountCheck');
        st.dom.discountWord = $('#discountWord');
        st.dom.memberAmount.length || (st.dom.memberAmount = $('#customerDeposit'));
    }
    st.start();
    console.log('商品退货');
});