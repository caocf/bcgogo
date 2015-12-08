$(function () {
    $("#orderTotal").text($("#total", window.parent.document).val());
    $("#mobile").val($("#mobile", window.parent.document).val());
    $("#hiddenTotal").val($("#orderTotal").text());


    if ($("#total", window.parent.document).val() == 0) {
        $("#cashAmount,#bankAmount,#bankCheckAmount,#accountDiscount,#accountDebtAmount,#settledAmount,#memberAmount,#customerDeposit").val('').attr('disabled', 'disabled');
        $("#bankCheckNo").attr('disabled', 'disabled');
        $("#accountMemberNo").attr('disabled', 'disabled');
        $("#accountMemberPassword").attr('disabled', 'disabled');
        $("#memberDiscountCheck").attr('disabled', 'disabled');
        $("#discount").val(10).attr('disabled', 'disabled');
        $("#huankuanTime").attr('disabled', 'disabled');
    }

    $("#div_close,#cancelBtn").click(function () {
        clearAccountDetailInfo();
        displayFrame();
    });

    var orderValue = $("#status", window.parent.document).val();
    if (orderValue == "SALE_DONE" || orderValue == "SALE_REPEAL" || orderValue == "SALE_DEBT_DONE") {
        $("#cashAmount").val($("#cashAmount", window.parent.document).val());
        $("#bankAmount").val($("#bankAmount", window.parent.document).val());
        $("#bankCheckAmount").val($("#bankCheckAmount", window.parent.document).val());
        $("#bankCheckNo").val($("#bankCheckNo", window.parent.document).val());
        $("#customerDeposit").val($("#customerDeposit", window.parent.document).val()); // add by zhuj
        $("#accountDebtAmount").val($("#debt", window.parent.document).val());
        $("#settledAmount").val($("#settledAmount", window.parent.document).val());
        $("#accountMemberNo").val($("#accountMemberNo", window.parent.document).val());
        $("#accountMemberPassword").val($("#accountMemberPassword", window.parent.document).val());
        $("#memberAmount").val($("#memberAmount", window.parent.document).val());

        var huankuanTime = window.parent.document.getElementById("repayDateStr");
        if (huankuanTime != null && huankuanTime != undefined) {
            var huankuanTimeVal = huankuanTime.value;
            $("#huankuanTime").val(huankuanTimeVal);
        }
        $("#orderDiscount", window.parent.document).val() > 0 && $("#accountDiscount").val($("#orderDiscount", window.parent.document).val());
        disableAttr();
    } else {
        $("#settledAmount").val($("#settledAmount", window.parent.document).val());
        $("#debt", window.parent.document).val() > 0 && $("#accountDebtAmount").val($("#debt", window.parent.document).val());
        var huankuanTime = window.parent.document.getElementById("huankuanTime");
        if (huankuanTime != null && huankuanTime != undefined) {
            var huankuanTimeVal = huankuanTime.value;
            $("#huankuanTime").val(huankuanTimeVal);
        }

        var discount = $("#total", window.parent.document).val()
            - $("#settledAmount", window.parent.document).val()
            - $("#debt", window.parent.document).val();
        if (discount != 0) {
            $("#accountDiscount").val(dataTransition.rounding(discount, 2));
        } else {
            $("#accountDiscount").val('');
        }

        if ($("#memberBalance").html() && $("#memberBalance").html() * 1 > 0) {
            if ($("#settledAmount").val() * 1 > $("#memberBalance").html() * 1) {
                $("#memberAmount").val($.trim($("#memberBalance").html()));
                $("#cashAmount").val(dataTransition.rounding($("#settledAmount").val() - $("#memberBalance").html() * 1, 2));

            } else {
                $("#memberAmount").val($.trim($("#settledAmount").val()));
            }
        } else if ($("#depositAvailable").text() && $("#depositAvailable").text() * 1 > 0) {
            if ($("#settledAmount").val() * 1 > $("#depositAvailable").text() * 1) {
                $("#customerDeposit").val($.trim($("#depositAvailable").text()));
                $("#cashAmount").val(dataTransition.rounding($("#settledAmount").val() - $("#depositAvailable").text() * 1, 2));
            } else {
                $("#customerDeposit").val($.trim($("#settledAmount").val()));
            }
        } else {
            $("#cashAmount").val($("#settledAmount").val());
        }

        if ($("#memberAmount").val() && $("#memberAmount").val() * 1 > 0) {
            $("#sendMessage")[0].checked = true;
        }
    }

    $("#accountMemberNo").bind("blur", function () {
        memberNoBlur();
    });
    $("#memberAmount").bind("change", memberNoBlur);
    memberNoBlur();
});

function disableAttr() {
    $("#cashAmount,#bankAmount,#bankCheckAmount,#bankCheckNo,#accountDiscount,#accountDebtAmount,#settledAmount,#customerDeposit").attr('disabled', 'disabled'); //zhuj
    $("#accountMemberNo,#accountMemberPassword,#memberAmount,#confirmBtn,#huankuanTime,#print,#sendMessage").attr('disabled', 'disabled');
}

function modifyStringToNumber(value) {
    if (value == null || value == undefined || value == "") {
        value = 0;
    }
    return value * 1;
}

//提交之前进行校验
function checkDate() {
    if ($("#id", window.parent.document).val()) {
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
    var accountDiscount = $("#accountDiscount").val();
    var accountDebtAmount = $("#accountDebtAmount").val();
    var settledAmount = $("#settledAmount").val();
    var accountMemberNo = $("#accountMemberNo").val();
    var accountMemberPassword = $("#accountMemberPassword").val();
    var memberAmount = $("#memberAmount").val();
    var huankuanTime = $("#huankuanTime").val();
    var customerDeposit = $("#customerDeposit").val();    //add by zhuj 获取预收款的使用
    var memberDiscountFalg = true;
    var hiddenTotal = $("#hiddenTotal").val() * 1;
    var total = $("#orderTotal").text() * 1;
    if ($("#memberDiscountCheck")[0].checked) {
        if (!$.trim($("#accountMemberNo").val())) {
            memberDiscountFalg = false;
            $("#confirmBtn").removeAttr("disabled");
            $("#memberDiscountCheck")[0].checked = false;
            nsDialog.jAlert("请输入会员号!");
            return;
        }
        else {
            APP_BCGOGO.Net.syncAjax({
                url: "member.do?method=getMemberDiscountAndValidatePassword",
                dataType: "json",
                data: {memberNo: $("#accountMemberNo").val(), password: $("#accountMemberPassword").val(), now: new Date()},
                success: function (json) {
                    if (json.resu == "error") {
                        memberDiscountFalg = false;

                        if (json.msg == "passwordError") {
                            nsDialog.jAlert("密码不正确，请修改!");
                        }

                        else if (json.msg == "noMember" || json.msg == "noCustomer" || json.msg == "customerDelete") {
                            //按钮不勾选变灰，充值
                            nsDialog.jAlert("会员号不存在!", null, function () {
                                $("#memberDiscountCheck")[0].checked = false;
                                $("#memberDiscountCheck").attr("disabled", true);
                                $("#memberDiscountRatio").html("").parent().hide();
                                initAccountDate(hiddenTotal);
                            });
                        }
                    }
                    else {
                        var memberDiscount = json.memberDiscount ? json.memberDiscount * 1 : 10;
                        var memberDiscountRatio = json.memberDiscount ? json.memberDiscount * 1 / 10 : 1;
                        var memberDiscountPage = $("#memberDiscountRatio").html();
                        if (!memberDiscountPage) {
                            memberDiscountPage = 10;
                        }

                        if (G.Number.equalsTo(memberDiscountPage * 1, memberDiscount) && memberDiscountPage == 10) {
                            $("#memberDiscountCheck")[0].checked = false;
                            $("#memberDiscountCheck").attr("disabled", true);
                            $("#memberDiscountRatio").html("").parent().hide();
                        }

                        if (!G.Number.equalsTo(memberDiscountPage * 1, memberDiscount)) {
                            memberDiscountFalg = false;
                            //按钮去掉勾选，填写折扣
                            nsDialog.jAlert("会员信息已更新", null, function () {
                                if (memberDiscount == 10) {
                                    $("#memberDiscountRatio").html("").parent().hide();
                                    $("#memberDiscountCheck")[0].checked = false;
                                    $("#memberDiscountCheck").attr("disabled", true);
                                    if (!G.Number.equalsTo(hiddenTotal, total)) {
                                        initAccountDate(hiddenTotal);
                                    }
                                }
                                else {
                                    $("#memberDiscountRatio").html(memberDiscount).parent().show();
                                    $("#memberDiscountCheck")[0].checked = false;
                                    $("#memberDiscountCheck").removeAttr("disabled");
                                    initAccountDate(hiddenTotal);
                                }
                            });
                            return;
                        }
                    }
                }
            });
        }
    } else {
        if (!GLOBAL.Number.equalsTo(hiddenTotal, total)) {
            memberDiscountFalg = false;
            nsDialog.jAlert("会员折扣未勾选,应收总额与实际应收总额不相符!", null, function () {
                initAccountDate(hiddenTotal);
            });
        }
    }

    if (!memberDiscountFalg) {
        $("#confirmBtn").removeAttr("disabled");
        return;
    }

    var result = false;
    if (memberAmount != null && memberAmount != undefined && memberAmount != "" && memberAmount != 0) {
        result = true;
    }

    if (result == true) {
        if (accountMemberNo == null || accountMemberNo == undefined || $.trim(accountMemberNo) == "") {
            showMessage.fadeMessage("35%", "40%", "slow", 2000, "请输入会员卡号");
            result = true;
            $("#confirmBtn").removeAttr("disabled");
            return;
        } else {
            var parentWinMemberNo = $.trim($("#hiddenSelfMemberNo").val());
            if (accountMemberNo != parentWinMemberNo) {
                if (!confirm("此会员号非本客户的会员号码,是否确认结算")) {
                    $("#confirmBtn").removeAttr("disabled");
                    return false;
                }
            }
        }
    }

    var validateResult = validate();
    if (validateResult != true) {
        $("#confirmBtn").removeAttr("disabled");
        return false;
    }
    var total = $("#orderTotal").text() * 1;
    total = modifyStringToNumber(total);
    cashAmount = modifyStringToNumber(cashAmount);
    bankAmount = modifyStringToNumber(bankAmount);
    bankCheckAmount = modifyStringToNumber(bankCheckAmount);
    accountDiscount = modifyStringToNumber(accountDiscount);
    accountDebtAmount = modifyStringToNumber(accountDebtAmount);
    settledAmount = modifyStringToNumber(settledAmount);
    memberAmount = modifyStringToNumber(memberAmount);
    customerDeposit = modifyStringToNumber(customerDeposit);    // add by zhuj

    window.parent.document.getElementById("cashAmount").value = cashAmount;
    window.parent.document.getElementById("bankAmount").value = bankAmount;
    window.parent.document.getElementById("bankCheckAmount").value = bankCheckAmount;
    window.parent.document.getElementById("bankCheckNo").value = bankCheckNo;
    window.parent.document.getElementById("orderDiscount").value = accountDiscount;
    window.parent.document.getElementById("debt").value = accountDebtAmount;
    window.parent.document.getElementById("settledAmount").value = settledAmount;
    window.parent.document.getElementById("accountMemberNo").value = accountMemberNo;
    window.parent.document.getElementById("accountMemberPassword").value = accountMemberPassword;
    window.parent.document.getElementById("memberAmount").value = memberAmount;
    window.parent.document.getElementById("huankuanTime").value = huankuanTime;
    window.parent.document.getElementById("afterMemberDiscountTotal").value = total;
    window.parent.document.getElementById("customerDeposit").value = customerDeposit; // add by zhuj

    if (!G.Number.equalsTo(hiddenTotal, total) && $("#memberDiscountCheck")[0].checked) {
        var memberDiscount = $("#memberDiscountRatio").html();
        if (memberDiscount) {
            memberDiscount = dataTransition.rounding(memberDiscount / 10, 2);
        }
        else {
            memberDiscount = 1;
        }
        window.parent.document.getElementById("memberDiscountRatio").value = memberDiscount;
    }

    if ($("#print")[0].checked) {
        window.parent.document.getElementById("print").value = $("#print").val();
    }

    window.parent.clearReadOnly();
    $("#salesOrderForm", window.parent.document).ajaxSubmit({
        url: "sale.do?method=validateSalesOrder&validateType=accountDetail",
        dataType: "json",
        type: "POST",
        success: function (result) {
            if (result) {
                if (result.success) {
                    var vFlag = true;
                    $(window.parent.document).find("[id$='_supplierInfo'] .useRelatedAmount").each(function () {
                        if ($(this).attr("vFlag") == "false") {
                            vFlag = false;
                        }
                    });
                    if (!vFlag) {
                        nsDialog.jAlert("存在供应商库存不足的商品！");
                        return;
                    }
                    window.parent.document.getElementById("mask").style.display = "none";
                    window.parent.document.getElementById("iframe_PopupBox_account").style.display = "none";
                    window.parent.document.getElementById("iframe_PopupBox_account").src = "";
                    $("#salesOrderForm", window.parent.document).submit();
                } else {
                    window.parent.setReadOnly();
                    clearAccountDetailInfo();
                    if (result.operation && result.operation == "update_product_inventory") {
                        var submitFlag = window.parent.updateProductInventory(result.data, true);
                        if (submitFlag) {
                            $("#salesOrderForm", window.parent.document).submit();
                        }
                    } else if (result.operation && result.operation == "update_product_inventory+CONFIRM_ALLOCATE_RECORD") {
                        window.parent.updateProductInventory(result.data, false);
                        if (confirm(result.msg)) {
                            $("#salesOrderForm", window.parent.document).attr("target", "_blank");
                            var oldAction = $("#salesOrderForm", window.parent.document).attr("action");
                            $("#salesOrderForm", window.parent.document).attr("action", "allocateRecord.do?method=createAllocateRecordBySaleOrder");
                            $("#salesOrderForm", window.parent.document).submit();
                            $("#salesOrderForm", window.parent.document).removeAttr("target");
                            $("#salesOrderForm", window.parent.document).attr("action", oldAction);
                        }
                        $("#confirmBtn").removeAttr("disabled");
                    }  else if (result.operation && result.operation == "confirm_deleted_product") {
                        if (confirm(result.msg)) {
                            $("#salesOrderForm", window.parent.document).submit();
                        } else {
                            $("#confirmBtn").removeAttr("disabled");
                        }
                    } else {
                        alert(result.msg);
                        $("#confirmBtn").removeAttr("disabled");
                    }
                }
            }
        },
        error: function () {
            alert("出现异常！");
            window.parent.setReadOnly();
            $("#confirmBtn").removeAttr("disabled");
        }
    });
}

function clearAccountDetailInfo() {
    window.parent.document.getElementById("cashAmount").value = 0;
    window.parent.document.getElementById("bankAmount").value = 0;
    window.parent.document.getElementById("bankCheckAmount").value = 0;
    window.parent.document.getElementById("bankCheckNo").value = "";
    window.parent.document.getElementById("accountMemberNo").value = "";
    window.parent.document.getElementById("accountMemberPassword").value = "";
    window.parent.document.getElementById("memberAmount").value = 0;
    window.parent.document.getElementById("huankuanTime").value = "";
    window.parent.document.getElementById("customerDeposit").value = 0;

}

function displayFrame() {
    window.parent.document.getElementById("mask").style.display = "none";
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

//清除：挂账、现金、银行、支票为空，实收=0
function clear() {
    $("#cashAmount").val("");
    $("#bankAmount").val("");
    $("#bankCheckAmount").val("");
    $("#bankCheckNo").val("");
    $("#memberAmount").val("")
    $("#settledAmount").val(0);
    $("#customerDeposit").val("");// add by zhuj
}

function validate() {
    var foo = APP_BCGOGO.Validator;
    var total = $("#orderTotal").html() * 1;

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

    var check = $("#bankCheckAmount").val();
    if (null != check && "" != check && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(check == 0.0 ? "0" : check, 2))) {
        alert("请填写正确的价格！");
        $("#bankCheckAmount").val("");
        return false;
    }
    // add by zhuj
    var customerDeposit = $('#customerDeposit').val();
    if (customerDeposit && !foo.stringIsPrice(APP_BCGOGO.StringFilter.priceFilter(customerDeposit == 0.0 ? "0" : customerDeposit, 2))) {
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

    if (null == memberAmount || "" == memberAmount) {
        memberAmount = 0;
    }

    if (null == customerDeposit || "" == customerDeposit) { //add by zhuj
        customerDeposit = 0;
    }
    discount = discount * 1;
    debt = debt * 1;
    cash = cash * 1;
    bankCard = bankCard * 1;
    check = check * 1;
    memberAmount = memberAmount * 1;
    customerDeposit = customerDeposit * 1; //add by zhuj
    if (debt > 0) {
        if (null == $("#mobile").val() || "" == $("#mobile").val() && $("#flag").attr("isNoticeMobile") == "false") {
            $("#div_mobile").html("有欠款请最好填写手机号码");
            $("#inputMobile").show();
            return false;
        }
        if ($("#huankuanTime").val()) {
            var huankuanDate = $("#huankuanTime").val().replace(/[^\d]+/g, "");
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
        $("#huankuanTime").val("");
    }
    if (total != 0) {
        if (GLOBAL.Number.equalsTo($("#settledAmount").val(), 0) && GLOBAL.Number.equalsTo(discount + debt, total)) {
            if (!confirm("实收为0，请确认是否挂账或优惠赠送。")) {
                return false;
            }
        }

        if (!GLOBAL.Number.equalsTo($("#settledAmount").val(), cash + bankCard + check + memberAmount + customerDeposit)) { // modified by zhuj
            var msg = "实收金额与现金、银行";
            if ("true" == $("#isMemberSwitchOn").val()) {
                msg += "、会员储值";
            }
            msg += "、支票";
            if (APP_BCGOGO.Permission.Version.CustomerDeposit) { //modified by zhuj
                msg += "或预收款";
            }
            msg += "的金额不符，请修改。如果挂账或优惠赠送，请输入0。";
            alert(msg);
            return false;
        }

        if (GLOBAL.Number.equalsTo($("#settledAmount").val(), cash + bankCard + check + memberAmount + customerDeposit) && !GLOBAL.Number.equalsTo($("#settledAmount").val(), total - discount - debt)) {
            alert("实收金额与优惠、挂账金额不符合，请修改。");
            return false;
        }

        // add by zhuj 校验预收款
        if (APP_BCGOGO.Permission.Version.CustomerDeposit) {
            var depositAvailable = $("#depositAvailable").text() * 1;
            if (customerDeposit > depositAvailable) {
                alert("可用预收款不足，请修改。");
                return false;
            }
        }

        if (GLOBAL.Number.equalsTo($("#settledAmount").val(), cash + bankCard + check + memberAmount + customerDeposit) &&
            GLOBAL.Number.equalsTo($("#settledAmount").val(), total - discount - debt)) {
            var msg = "本次结算应收：" + total + "元"
            if (discount > 0) {
                msg += ",优惠：" + discount + "元";
            }
            if (debt > 0) {
                msg += ",挂账" + debt + "元";
            }
            if ($("#settledAmount").val() > 0) {
                msg += "\n\n";
                msg += "实收：" + $('#settledAmount').val() + "元(";
                msg += cash > 0 ? "现金：" + cash + "元," : "";
                msg += bankCard > 0 ? "银行卡：" + bankCard + "元," : "";
                if ("true" == $("#isMemberSwitchOn").val()) {
                    msg += memberAmount > 0 ? "会员储值" + memberAmount + "元," : "";
                }
                msg += check > 0 ? "支票：" + check + "元," : "";

                if (APP_BCGOGO.Permission.Version.CustomerDeposit) { //add by zhuj
                    msg += customerDeposit > 0 ? "预收款：" + customerDeposit + "元," : "";
                }

                if (msg.substring(msg.length - 1, msg.length) == ',') {
                    msg = msg.substring(0, msg.length - 1) + ")";
                }
            }
            if (!confirm(msg)) {
                return false;
            }
        }
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
    if (GLOBAL.Lang.isEmpty($("#mobile").val()) && $("#flag").attr("isNoticeMobile") == "false"
        && $("#sendMessage").attr('checked')) {
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
    //add by zhuj
    if ($("#customerDeposit").val() == null || $("#customerDeposit").val() == "") {
        $("#customerDeposit").val(0);
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
    APP_BCGOGO.Net.syncAjax({url: url, dataType: "json", data: {customerId: $("#customerId").val(), mobile: $("#mobile").val()}, success: function (json) {
        if ("hasCustomer" == json.resu) {
            alert("拥有此手机的用户已存在，请重新填写");
            isMobileExist = true;
        }
        else if ("hasCustomerGtOne" == json.resu) {
            alert("系统中此手机号已有多个用户，请重新填写");
            isMobileExist = true;
        }
    }});

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
        $("#memberDiscountRatio").html("").parent().hide();
        $("#memberBalance").html("0.0");
        enableOrDisableMsgCheck("disable");
        return;
    }
    APP_BCGOGO.Net.syncAjax({
        url: "member.do?method=ajaxGetCustomerWithMember",
        dataType: "json",
        data: {memberNo: $("#accountMemberNo").val(), now: new Date()},
        success: function (json) {

            var hiddenTotal = $("#hiddenTotal").val() * 1;
            var total = $("#orderTotal").text() * 1;

            if ((null == json || null == json.memberDTO || null == json.memberDTO.memberDiscount)) {
                $("#memberDiscountCheck")[0].checked = false;
                $("#memberDiscountCheck").attr("disabled", true);
                $("#memberDiscountRatio").html("").parent().hide();
                if (!G.Number.equalsTo(hiddenTotal, total)) {
                    initAccountDate(hiddenTotal);
                }
            }
            else {
                $("#memberDiscountCheck").removeAttr("disabled", true);
                if (!$("#memberDiscountCheck")[0].checked) {
                    $("#memberDiscountRatio").html(dataTransition.rounding(json.memberDTO.memberDiscount * 10, 1)).parent().show();
                }
                else {
                    var memberDiscountTotal = dataTransition.rounding(hiddenTotal * json.memberDTO.memberDiscount, 2);
                    if (!G.Number.equalsTo(memberDiscountTotal, total) || $("#memberDiscountRatio").html() != dataTransition.rounding(json.memberDTO.memberDiscount * 10, 1)) {
                        $("#memberDiscountRatio").html(dataTransition.rounding(json.memberDTO.memberDiscount * 10, 1)).parent().show();
                        $("#memberDiscountCheck")[0].checked = false;
                        initAccountDate(hiddenTotal);
                    }
                }

            }
            if ($("#total", window.parent.document).val() == 0) {
                $("#memberDiscountCheck").attr("disabled", true);
            }
            enableOrDisableMsgCheck("enable");
            if (json.idStr != $("#customerId", window.parent.document).val() && (null == json.mobile || !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(json.mobile))) {
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
            enableOrDisableMsgCheck("disable");
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
        if (flag == "disable") {
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
    enableOrDisableMsgCheck(usingCard ? "enable" : "disable");
}

function initAccountDate(total) {
    $("#orderTotal").text(total);
    $("#settledAmount,#cashAmount").val(total);
    $("#accountDiscount,#accountDebtAmount,#memberAmount,#bankAmount,#bankCheckAmount,#bankCheckNo,#huankuanTime,#customerDeposit").val(""); //modified by zhuj
    st.moneyColor();
}

$(function () {
    st.initDom = function () {
        st.dom.orderTotal = $('#orderTotal');
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
        st.dom.memberAmount.length || (st.dom.memberAmount = $('#customerDeposit'));
    }
    st.start(function () {
        $('#customerDeposit').length && st.dom.memberAmount.bind('input',function () {
            var available = getNumber($('#depositAvailable').text());
            var prepaid = st.dom.memberAmount.val();
            prepaid > available && st.dom.memberAmount.val(available);
        }).unbind('dblclick').bind('dblclick', function () {
                var amount = st.getAmount();
                var available = getNumber($('#depositAvailable').text());
                if(available >= amount.orderTotal){
                    $.proxy(st.handle.dblclick, this)();
                }else{
                    alert('可用预收款不足，请修改。');
                }
            });
    });
    console.log('商品销售');
});