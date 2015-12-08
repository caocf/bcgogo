/**
 * 对账单结算页面专用js
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-9
 * Time: 下午5:31
 * To change this template use File | Settings | File Templates.
 */

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

$(document).ready(function () {
    $("#div_close,#cancelBtn").click(function () {
        displayFrame();
    });

    $("#paymentTimeStr")
        .bind("click", function () {
            $(this).blur();
        })
        .datepicker({
            "numberOfMonths": 1,
            "showButtonPanel": true,
            "changeYear": true,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": ""
        });
});

$(function () {
    var totalDebtText = $("#totalDebtText", window.parent.document).text();
    if (!totalDebtText) {
        return;
    }
    var orderDebtType = $("#orderDebtType", window.parent.document).val();
    var totalDebt = $("#totalDebt", window.parent.document).text();

    if (orderDebtType == "CUSTOMER_DEBT_PAYABLE") {
        $("#cardsTitle").text("应付总计：");
        $("#div_drag").text("对账结算(支出)");
        $("#sendMessage").css("display", "none");
        $("#sendMessageText").css("display", "none");
        $("#receivableText").text("实付总额");
    } else if (orderDebtType == "CUSTOMER_DEBT_RECEIVABLE") {
        $("#cardsTitle").text("应收总计：");
        $("#div_drag").text("对账结算(收入)");
        $("#receivableText").text("实收总额");
        $("#sendMessageText").css("display", "block");
        $("#sendMessage").css("display", "block");
    } else if (orderDebtType == "SUPPLIER_DEBT_RECEIVABLE") {
        $("#cardsTitle").text("应收总计：");
        $("#div_drag").text("对账结算(收入)");
        $("#receivableText").text("实收总额");
    } else if (orderDebtType == "SUPPLIER_DEBT_PAYABLE") {
        $("#cardsTitle").text("应付总计：");
        $("#div_drag").text("对账结算(支出)");
        $("#receivableText").text("实付总额");
    }

    $("#totalDebt").text(totalDebt);
    $("#hiddenTotal").val(totalDebt);
    $("#settledAmount").val(totalDebt);

    $("#mobile").val($("#mobile", window.parent.document).val());

    if (totalDebt == 0) {
        $("#cashAmount,#bankAmount,#bankCheckAmount,#bankCheckNo,#accountDiscount,#accountDebtAmount,#settledAmount,#memberAmount,#depositAmount").val(0).attr('disabled', 'disabled');
        $("#paymentTimeStr").attr('disabled', 'disabled');
    }

    if (orderDebtType == "CUSTOMER_DEBT_RECEIVABLE") {
        if ($("#memberBalance").html() && $("#memberBalance").html() * 1 > 0) {
            if ($("#settledAmount").val() * 1 > $("#memberBalance").html() * 1) {
                $("#memberAmount").val($("#memberBalance").html());
                $("#cashAmount").val(($("#settledAmount").val() - $("#memberBalance").html() * 1).toFixed(2));
            } else {
                $("#memberAmount").val($("#settledAmount").val());
            }
        } else {
            var deposit_avaiable = $("#deposit_avaiable").html(); // add by zhuj　没有会员账户的时候 先使用预收款
            deposit_avaiable = modifyStringToNumber(deposit_avaiable);
            if (deposit_avaiable * 1 >= totalDebt) {
                $("#depositAmount").val(totalDebt);
            } else if (deposit_avaiable * 1 > 0 && deposit_avaiable * 1 < totalDebt) {
                $("#cashAmount").val(dataTransition.rounding(totalDebt - deposit_avaiable, 2));
                $("#depositAmount").val(deposit_avaiable);
            } else if (deposit_avaiable * 1 == 0) {
                $("#cashAmount").val(totalDebt);
            }
            $("#settleAmount").val(totalDebt);
        }
        if ($("#memberNo") != null) {
            $("#accountMemberNo").val($("#memberNo").text());
        }
        memberNoBlur();
        $("#accountMemberNo").bind("blur", function () {
            memberNoBlur();
        });
        $("#memberAmount").bind("change", memberNoBlur);

    } else if (orderDebtType == "SUPPLIER_DEBT_PAYABLE") {
        var deposit_avaiable = $("#deposit_avaiable").html();
        deposit_avaiable = modifyStringToNumber(deposit_avaiable);

        if (deposit_avaiable * 1 >= totalDebt) {

            $("#depositAmount").val(totalDebt);
        } else if (deposit_avaiable * 1 > 0 && deposit_avaiable * 1 < totalDebt) {

            $("#cashAmount").val(dataTransition.rounding(totalDebt - deposit_avaiable, 2));

            $("#depositAmount").val(deposit_avaiable);
        } else if (deposit_avaiable * 1 == 0) {
            $("#cashAmount").val(totalDebt);
        }
    } else {
        $("#cashAmount").val(totalDebt);
    }
});

//提交之前进行校验
function checkDate() {
    var orderDebtType = $("#orderDebtType", window.parent.document).val();

    var totalDebtText = $("#totalDebtText", window.parent.document).text();
    if (!totalDebtText) {
        return;
    }

    if ($("#confirmBtn").attr("disabled")) {
        return;
    }

    $("#confirmBtn").attr("disabled", true);
    $("#cancelBtn").attr("disabled", true);
    var cashAmount = $("#cashAmount").val();
    var bankAmount = $("#bankAmount").val();
    var bankCheckAmount = $("#bankCheckAmount").val();
    var bankCheckNo = $("#bankCheckNo").val();
    var accountDiscount = $("#accountDiscount").val();
    var accountDebtAmount = $("#accountDebtAmount").val();
    var settledAmount = $("#settledAmount").val();
    var accountMemberNo = $("#accountMemberNo").val();
    var accountMemberPassword = $("#accountMemberPassword").val();
    var memberAmount = $("#memberAmount").val();
    var paymentTimeStr = $("#paymentTimeStr").val();

    var depositAmount = $("#depositAmount").val();
    var totalReceivable = $("#totalReceivableSpan", window.parent.document).html() * 1;
    var totalPayable = $("#totalPayableSpan", window.parent.document).html() * 1;
    if (orderDebtType == "CUSTOMER_DEBT_RECEIVABLE") {
        if ("支票号" == bankCheckNo) {
            $("#bankCheckNo").val("");
            bankCheckNo = "";
        }
        if ("请刷卡/输入卡号" == accountMemberNo) {
            $("#accountMemberNo").val("");
            accountMemberNo = "";
        }
        var total = $("#hiddenTotal").val();

        var result = false;
        if (memberAmount != null && memberAmount != undefined && memberAmount != "" && memberAmount != 0) {
            result = true;
        }

        if (result == true) {
            if (accountMemberNo == null || accountMemberNo == undefined || jQuery.trim(accountMemberNo) == "") {
                showMessage.fadeMessage("35%", "40%", "slow", 2000, "请输入会员卡号");
                result = true;
                $("#confirmBtn").removeAttr("disabled");
                $("#cancelBtn").removeAttr("disabled");
                return;
            } else {
                var parentWinMemberNo = jQuery.trim($("#memberNo").text());
                if (accountMemberNo != parentWinMemberNo) {
                    if (!confirm("此会员号非本客户的会员号码,是否确认结算")) {
                        $("#confirmBtn").removeAttr("disabled");
                        $("#cancelBtn").removeAttr("disabled");
                        return false;
                    }
                }
            }
        }
    }


    var validateResult = validate();
    if (validateResult != true) {
        $("#confirmBtn").removeAttr("disabled");
        $("#cancelBtn").removeAttr("disabled");
        return false;
    }

    cashAmount = modifyStringToNumber(cashAmount);
    bankAmount = modifyStringToNumber(bankAmount);
    bankCheckAmount = modifyStringToNumber(bankCheckAmount);
    accountDiscount = modifyStringToNumber(accountDiscount);
    accountDebtAmount = modifyStringToNumber(accountDebtAmount);
    settledAmount = modifyStringToNumber(settledAmount);
    memberAmount = modifyStringToNumber(memberAmount);
    depositAmount = modifyStringToNumber(depositAmount);

    if (accountMemberNo == "请刷卡/输入卡号") {
        accountMemberNo = "";
    }
    if (bankCheckNo == "支票号") {
        bankCheckNo = "";
    }
    window.parent.document.getElementById("cashAmount").value = cashAmount;
    window.parent.document.getElementById("bankAmount").value = bankAmount;
    window.parent.document.getElementById("bankCheckAmount").value = bankCheckAmount;

    window.parent.document.getElementById("bankCheckNo").value = bankCheckNo;
    window.parent.document.getElementById("discount").value = accountDiscount;
    window.parent.document.getElementById("debt").value = accountDebtAmount;
    window.parent.document.getElementById("settledAmount").value = settledAmount;
    window.parent.document.getElementById("accountMemberNo").value = accountMemberNo;
    window.parent.document.getElementById("accountMemberPassword").value = accountMemberPassword;
    window.parent.document.getElementById("memberAmount").value = memberAmount;
    window.parent.document.getElementById("paymentTimeStr").value = paymentTimeStr;
    window.parent.document.getElementById("depositAmount").value = depositAmount;
    window.parent.document.getElementById("totalReceivable").value = totalReceivable;
    window.parent.document.getElementById("totalPayable").value = totalPayable;
    //获得哪些单据被选中
    var receptNoListStr = '';
    $(":checkbox[class=subBox]", window.parent.document).each(function () {
        if ($(this).attr("checked")) {
            receptNoListStr += $(this).attr("id");
            receptNoListStr += ',';
        }
    });

    receptNoListStr = receptNoListStr.substring(0, receptNoListStr.length - 1);

    window.parent.document.getElementById("receptNoListStr").value = receptNoListStr;
    if ($("#print")[0].checked) {
        window.parent.document.getElementById("isPrint").value = true;
    }
    window.parent.document.getElementById("sendMemberSms").checked = $("#sendMessage").attr('checked');
    $("#confirmBtn").attr("disabled", true);
    $("#cancelBtn").attr("disabled", true);

    $("#statementOrderAccountForm", window.parent.document).ajaxSubmit({
        url: "statementAccount.do?method=settleStatementAccountOrder",
        dataType: "json",
        type: "POST",
        success: function (result) {
            if (result) {
                $("#confirmBtn").removeAttr("disabled");
                $("#cancelBtn").removeAttr("disabled");
                if (result.success) {
                    nsDialog.jAlert("对账成功！");
                    window.parent.document.getElementById("mask").style.display = "none";
                    window.parent.document.getElementById("iframe_PopupBox_account").style.display = "none";
                    window.parent.document.getElementById("iframe_PopupBox_account").src = "";
                    var orderId = result.operation;
                    var print = "false";
                    if ($("#print")[0].checked) {
                        print = "true";
                    }
                    window.parent.location.href = "statementAccount.do?method=showStatementAccountOrderById&statementOrderId=" + orderId + "&print=" + print;
                } else {
                    nsDialog.jAlert(result.msg);
                }
            }
        },
        error: function () {
            $("#confirmBtn").removeAttr("disabled");
            $("#cancelBtn").removeAttr("disabled");

            alert("出现异常！");
        }
    });
}

function modifyStringToNumber(value) {
    if (value == null || value == undefined || value == "") {
        value = 0;
    }
    return value * 1;
}

function getDomObjectValue(domId) {
    var value = $("#" + domId).val();
    if (null == value || "" == value || undefined == value) {
        value = 0;
    }
    return value * 1;
}

//清除：挂账、现金、银行、支票为空，实收=0
function clear() {
    $("#cashAmount").val("");
    $("#bankAmount").val("");
    $("#bankCheckAmount").val("");
    $("#bankCheckNo").val("");
    $("#memberAmount").val("")
    $("#settledAmount").val(0);
    $("#depositAmount").val("");
}

function validate() {
    var foo = APP_BCGOGO.Validator;
    var total = $("#hiddenTotal").val();
    var orderType = $("#orderType", window.parent.document).val(); // add by zhuj

    var discount = $("#accountDiscount").val();
    if (null != discount && "" != discount && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(discount == 0.0 ? "0" : discount, 2))) {
        alert("请填写正确的价格！");
        $("#accountDiscount").val("");
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

    var memberAmount = $("#memberAmount").val();
    if (null != memberAmount && "" != memberAmount && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(memberAmount == 0.0 ? "0" : memberAmount, 2))) {
        alert("请填写正确的价格！");
        $("#memberAmount").val("");
        return false;
    }

    var depositAmount = $("#depositAmount").val();
    if (null != depositAmount && "" != depositAmount && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(depositAmount == 0.0 ? "0" : depositAmount, 2))) {
        alert("请填写正确的价格！");
        $("#depositAmount").val("");
        return false;
    }

    var check = $("#bankCheckAmount").val();
    if (null != check && "" != check && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(check == 0.0 ? "0" : check, 2))) {
        alert("请填写正确的价格！");
        $("#bankCheckAmount").val("");
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

    if (null == memberAmount || "" == memberAmount) {
        memberAmount = 0;
    }

    if (null == depositAmount || "" == depositAmount) {
        depositAmount = 0;
    }

    discount = discount * 1;
    debt = debt * 1;
    cash = cash * 1;
    bankCard = bankCard * 1;
    check = check * 1;
    memberAmount = memberAmount * 1;
    depositAmount = depositAmount * 1;

    if (debt > 0) {
        if (null == $("#mobile").val() || "" == $("#mobile").val() && $("#flag").attr("isNoticeMobile") == "false") {
            $("#div_mobile").html("有欠款请最好填写手机号码");
            $("#inputMobile").show();
            return false;
        }
        if ($("#paymentTimeStr").val()) {
            var huankuanDate = $("#paymentTimeStr").val().replace(/[^\d]+/g, "");
            var year = new Date().getFullYear();
            var month = new Date().getMonth() + 1;
            var day = new Date().getDate();
            var nowDate = "" + year + (month.toString().length == 1 ? ("0" + month) : month) + (day.toString().length == 1 ? ("0" + day) : day);

            if (huankuanDate * 1 < nowDate * 1) {
                alert("还款时间必须大于当前时间！");
                return false;
            }
        }
    } else {
        $("#paymentTimeStr").val("");
    }
    if (total != 0) {

        if (GLOBAL.Number.equalsTo($("#settledAmount").val(), 0) && GLOBAL.Number.equalsTo(discount + debt, total)) {
            if (!confirm("实收为0，请确认是否挂账或优惠赠送。")) {
                return false;
            }
        }

        // 预收款和预付款是否足够
        var $depositAvailable = dataTransition.rounding(parseFloat($("#deposit_avaiable").text()), 2);
        if (depositAmount && depositAmount > 0 && depositAmount > $depositAvailable) {
            if (orderType != 'CUSTOMER_STATEMENT_ACCOUNT') {
                alert("预付款余额不足,请充值。");
            } else {
                alert("预收款余额不足,请充值。");
            }
            return false;
        }

        if (memberAmount > 0 && depositAmount > 0) {
            if (!GLOBAL.Number.equalsTo($("#settledAmount").val(), cash + bankCard + check + memberAmount + depositAmount)) { // modify by zhuj
                if (orderType != 'CUSTOMER_STATEMENT_ACCOUNT') {
                    var msg = "实收金额与现金、银行";
                    msg += "、会员储值";
                    msg += "、预付款";
                    msg += "或支票的金额不符，请修改。如果挂账或优惠赠送，请输入0。";

                } else {
                    var msg = "实收金额与现金、银行";
                    msg += "、会员储值";
                    msg += "、预收款";
                    msg += "或支票的金额不符，请修改。如果挂账或优惠赠送，请输入0。";
                }
                alert(msg);
                return false;
            }
        } else if (depositAmount > 0 && !(memberAmount > 0)) {
            if (!GLOBAL.Number.equalsTo($("#settledAmount").val(), cash + bankCard + check + depositAmount)) {
                if (orderType != 'CUSTOMER_STATEMENT_ACCOUNT') {
                    var msg = "实收金额与现金、银行";
                    msg += "、预付款";
                    msg += "或支票的金额不符，请修改。如果挂账或优惠赠送，请输入0。";

                } else {
                    var msg = "实收金额与现金、银行";
                    msg += "、预收款";
                    msg += "或支票的金额不符，请修改。如果挂账或优惠赠送，请输入0。";
                }
                alert(msg);
                return false;
            }
        } else if (!(depositAmount > 0) && memberAmount > 0) {
            if (!GLOBAL.Number.equalsTo($("#settledAmount").val(), cash + bankCard + check + memberAmount)) {
                var msg = "实收金额与现金、银行";
                msg += "、会员储值";
                msg += "或支票的金额不符，请修改。如果挂账或优惠赠送，请输入0。";
                alert(msg);
                return false;
            }
        }
        else {
            if (!GLOBAL.Number.equalsTo($("#settledAmount").val(), cash + bankCard + check)) {
                var msg = "实收金额与现金、银行";
                msg += "、支票";
                msg += "的金额不符，请修改。如果挂账或优惠赠送，请输入0。";
                alert(msg);
                return false;
            }
        }

        if (!GLOBAL.Number.equalsTo($("#settledAmount").val(), total - discount - debt)) {
            alert("实收金额与优惠、挂账金额不符合，请修改。");
            return false;
        }

        if (GLOBAL.Number.equalsTo($("#settledAmount").val(), cash + bankCard + check + memberAmount + depositAmount) && GLOBAL.Number.equalsTo($("#settledAmount").val(), total - discount - debt)) {
            var msg = "本次结算应收：" + total + "元";
            if ($("#receivableText").text() == "实付总额") {
                msg = "本次结算应付：" + total + "元";
            }
            if (discount > 0) {
                msg += ",优惠：" + discount + "元";
            }
            if (debt > 0) {
                msg += ",挂账" + debt + "元";
            }
            if ($("#settledAmount").val() > 0) {
                msg += "\n\n";
                if ($("#receivableText").text() == "实付总额") {
                    msg += "实付：" + $('#settledAmount').val() + "元(";
                } else {
                    msg += "实收：" + $('#settledAmount').val() + "元(";
                }
                msg += cash > 0 ? "现金：" + cash + "元," : "";
                msg += bankCard > 0 ? "银行卡：" + bankCard + "元," : "";
                msg += memberAmount > 0 ? "会员储值" + memberAmount + "元," : "";
                if (orderType == 'CUSTOMER_STATEMENT_ACCOUNT') { // add by zhuj
                    msg += depositAmount > 0 ? "预收款" + depositAmount + "元," : "";
                } else {
                    msg += depositAmount > 0 ? "预付款" + depositAmount + "元," : "";
                }
                msg += check > 0 ? "支票：" + check + "元)" : ")";
                if (msg.substring(msg.length - 1, msg.length) == ',') {
                    msg = msg.substring(0, msg.length - 1) + ")";
                }
            }
            if (!confirm(msg)) {
                return false;
            }
        }
    } else {
        $("#cashAmount,#bankAmount,#bankCheckAmount,#bankCheckNo,#accountDiscount,#accountDebtAmount,#settledAmount,#memberAmount,#depositAmount").val(0).attr('disabled', 'disabled');
        $("#paymentTimeStr").attr('disabled', 'disabled');
    }

    if (null != $("#mobile").val() && "" != $("#mobile").val() && !foo.stringIsMobilePhoneNumber($("#mobile").val())) {
        alert("请输入正确的手机号");
        $("#mobile").val("");
        $("#mobile").select();
        $("#mobile").focus();
        return false;
    }

    handleMoneyInputEmpty();

    // 勾上发送短信，且此客户无号码时，须填写手机号码
    if (GLOBAL.Lang.isEmpty($("#mobile").val()) && $("#flag").attr("isNoticeMobile") == "false" && $("#sendMessage").attr('checked')) {
        $("#div_mobile").html("发送短信必须填写手机号码");
        $("#inputMobile").show();
        return false;
    }
    return true;
}

function handleMoneyInputEmpty() {
    if ($("#accountDiscount").val() == null || $("#accountDiscount").val() == "") {
        $("#accountDiscount").val(0);
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
    if ($("#settledAmount").val() == null || $("#settledAmount").val() == "") {
        $("#settledAmount").val(0);
    }
    if ($("#memberAmount").val() == null || $("#memberAmount").val() == "") {
        $("#memberAmount").val(0);
    }
    if ($("#depositAmount").val() == null || $("#depositAmount").val() == "") {
        $("#depositAmount").val(0);
    }
}


function inputMobile() {
    var foo = APP_BCGOGO.Validator;
    if (null != $("#mobile").val() && "" != $("#mobile").val() && !foo.stringIsMobilePhoneNumber($("#mobile").val())) {
        alert("请输入正确的手机号");
        $("#mobile").val("");
        $("#mobile").select();
        $("#mobile").focus();
        return;
    }

    var url = "member.do?method=checkMobileDifferentCustomer";
    var isMobileExist = false;
    APP_BCGOGO.Net.syncAjax({
        url: url,
        dataType: "json",
        data: {
            customerId: $("#customerId").val(),
            mobile: $("#mobile").val()
        },
        success: function (json) {
            if ("hasCustomer" == json.resu) {
                alert("拥有此手机的用户已存在，请重新填写");
                isMobileExist = true;
            } else if ("hasCustomerGtOne" == json.resu) {
                alert("系统中此手机号已有多个用户，请重新填写");
                isMobileExist = true;
            }
        }
    });

    if (isMobileExist) {
        return;
    }
    $("#mobile", window.parent.document).val($("#mobile").val());
    $("#flag").attr("isNoticeMobile", true);
    $("#inputMobile").hide();
}

function cancleInputMobile() {
    $("#mobile").val("");
    if ($("#div_mobile").html() == '发送短信必须填写手机号码') {
        $("#sendMessage").removeAttr('checked');
    } else {
        $("#flag").attr("isNoticeMobile", true);
    }
    $("#inputMobile").hide();
}

// 短信框是否选中，是否可选中。会员卡余额。
// 与washBeautyAccount.js中相同，改变的话要同步改变。
function memberNoBlur() {
    if (GLOBAL.Lang.isEmpty($("#accountMemberNo").val())) {
        enableOrDisableMsgCheck('disable');
        return;
    }
    APP_BCGOGO.Net.syncAjax({
        url: "member.do?method=ajaxGetCustomerWithMember",
        dataType: "json",
        data: {memberNo: $("#accountMemberNo").val()},
        success: function (json) {

            var hiddenTotal = $("#hiddenTotal").val() * 1;
            var total = $("#hiddenTotal").text() * 1;

            enableOrDisableMsgCheck('enable');
            if (json.idStr != $("#customerOrSupplierId", window.parent.document).val() && (null == json.mobile || !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(json.mobile))) {
                $("#sendMessage").removeAttr("checked");
                $("#sendMessage").unbind().bind("click", function () {
                    alert("此会员号并无手机号码，无法发送短信。");
                    return false;
                });
            } else {
                $("#sendMessage").attr("checked", "checked");
                $("#sendMessage").unbind();
                enableOrDisableMsgCheck();
            }
            if (null != json.memberDTO && json.memberDTO.balanceStr != null) {
                $("#memberBalance").html(json.memberDTO.balanceStr);
            } else {
                $("#memberBalance").html("0.0");
            }
        },
        error: function () {
            enableOrDisableMsgCheck('disable');
            $("#memberBalance").html("0.0");
        }
    });
}

/**
 * 短信框是否disable， washBeautyAccount.js中有相同方法
 * @param flag 如果为disable, 则disable, 否则enable.
 */
function enableOrDisableMsgCheck(flag) {
    if (flag != null && flag != undefined) {
        if (flag == 'disable') {
            $("#sendMessage").removeAttr("checked");
            $("#sendMessage").attr("disabled", true);
        } else {
            $("#sendMessage").removeAttr("disabled");
        }
        return;
    }
    // 如果没用到会员卡，disable短信发送
    var usingCard = false;
    if ($("#memberAmount").val() > 0) {
        usingCard = true;
    }
    enableOrDisableMsgCheck(usingCard ? 'enable' : 'disable');
}

$(function () {
    st.initDom = function () {
        st.dom.orderTotal = $('#totalDebt');
        st.dom.cashAmount = $('#cashAmount');
        st.dom.bankAmount = $('#bankAmount');
        st.dom.bankCheckAmount = $('#bankCheckAmount');
        st.dom.memberAmount = $('#memberAmount');
        st.dom.settledAmount = $('#settledAmount');
        st.dom.accountDebtAmount = $('#accountDebtAmount');
        st.dom.accountDiscount = $('#accountDiscount');
        st.dom.discount = $('#discount');
        st.dom.confirmBtn = $('#confirmBtn');
        st.dom.memberDiscountCheck = $('#memberDiscountCheck');
        st.dom.discountWord = $('#discountWord');
        st.dom.memberAmount.length || (st.dom.memberAmount = $('#depositAmount'));//特殊处理
    }
    st.start();
    console.log('对账结算');
});