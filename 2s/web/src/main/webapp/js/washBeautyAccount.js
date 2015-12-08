$(function () {

    $("#div_close,#cancelBtn").click(function () {
        window.parent.document.getElementById("mask").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox_account").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox_account").src = "";

        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch (e) {
        }
        self.frameElement.blur();
        $(window.top.document.body).append($("<input type='text' id='focusBackToMe'>")).find("#focusBackToMe").focus().select().remove();
    });
    var orderTotal = window.parent.document.getElementById("total").value;
    //代金券金额
    var couponAmount;
    if($("#couponAmount", window.parent.document).val()==null||$("#couponAmount", window.parent.document).val()==""||0>$("#couponAmount", window.parent.document).val()*1){
        couponAmount=0;
    }else {
        couponAmount=$("#couponAmount", window.parent.document).val();
    }
    $("#serviceTotal").html(orderTotal);
    $("#couponAmount").html(couponAmount);
    $("#orderTotal").text(orderTotal-couponAmount);
    $("#hiddenTotal").val($("#orderTotal").text());
    if (!$("#id", window.parent.document).val()) {
        var cashAmount = window.parent.document.getElementById("cashAmount").value;
        $("#settledAmount").val(orderTotal-couponAmount);
        if ($("#memberBalance").html() && $("#memberBalance").html() * 1 > 0) {
            if ($("#settledAmount").val() * 1 > $("#memberBalance").html() * 1) {
                $("#memberAmount").val($("#memberBalance").html());
                $("#cashAmount").val(dataTransition.rounding($("#settledAmount").val() - $("#memberBalance").html() * 1, 2));

            }
            else {
                $("#memberAmount").val($("#settledAmount").val());
            }
        }
        else {
            $("#cashAmount").val($("#settledAmount").val());
        }

        if ($("#memberAmount").val() && $("#memberAmount").val() * 1 > 0) {
            $("#sendMessage")[0].checked = true;
            $("#sendMessage").removeAttr("disabled");
        }

    }

    if ($("#orderTotal").text() == 0) {
        $("#accountDiscount,#settledAmount,#accountDebtAmount,#cashAmount,#bankAmount,#bankCheckAmount,#memberAmount,#bankCheckNo,#huankuanTime").attr("disabled", "true");
        $("#discount").val(10).attr("disabled", "true");
        var result = false;
        $("select[name$='.consumeTypeStr']", window.parent.document).each(function () {
            if ($(this, window.parent.document).val() == "TIMES") {
                result = true;
            }
        });

        if (result == false) {
            $("#accountMemberNo,#accountMemberPassword").attr('disabled', 'disabled');
        }
    }
    // init date picker
    $("#huankuanTime").bind("click", function () {
            $(this).blur();
        }).datepicker({
            "numberOfMonths": 1,
            "showButtonPanel": true,
            "changeYear": true,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": ""
        });
});

$(function () {
    if ($("#id", window.parent.document).val() != "") {
        $("#washBeautyAccountForm input").not($("#cancelBtn")).each(function () {
            $(this).attr("disabled", "disabled");
            $(this).unbind("dbclick");

        });
        $("#accountMemberNo,#bankCheckNo").unbind("click");
    }
    var cashAmount = window.parent.document.getElementById("cashAmount").value;
    var bankAmount = window.parent.document.getElementById("bankAmount").value;
    var bankCheckAmount = window.parent.document.getElementById("bankCheckAmount").value;
    var bankCheckNo = window.parent.document.getElementById("bankCheckNo").value;
    var accountDiscount = window.parent.document.getElementById("orderDiscount").value;
    var accountDebtAmount = window.parent.document.getElementById("debt").value;
    var accountMemberPassword = window.parent.document.getElementById("accountMemberPassword").value;
    var memberAmount = window.parent.document.getElementById("memberAmount").value;
    var huankuanTime = window.parent.document.getElementById("huankuanTime").value;
    cashAmount = modifyStringToNumber(cashAmount);
    bankAmount = modifyStringToNumber(bankAmount);
    bankCheckAmount = modifyStringToNumber(bankCheckAmount);
    accountDiscount = modifyStringToNumber(accountDiscount);
    accountDebtAmount = modifyStringToNumber(accountDebtAmount);
    memberAmount = modifyStringToNumber(memberAmount);

    $("#mobile").val(window.parent.document.getElementById("mobile").value);
    cashAmount > 0 && $("#cashAmount").val(cashAmount);
    bankAmount > 0 && $("#bankAmount").val(bankAmount);
    bankCheckAmount > 0 && $("#bankCheckAmount").val(bankCheckAmount);
    bankCheckNo > 0 && $("#bankCheckNo").val(bankCheckNo);
    accountDiscount > 0 && $("#accountDiscount").val(accountDiscount);
    accountDebtAmount > 0 && $("#accountDebtAmount").val(accountDebtAmount);
    $("#accountMemberPassword").val(accountMemberPassword);
    memberAmount > 0 && $("#memberAmount").val(memberAmount);
    $("#huankuanTime").val(huankuanTime);
    if ($("#status", window.parent.document).val() == 3) {
        disableAttr();
    }

    $("#accountMemberNo").bind("blur", function () {
        memberNoBlur();
    });
    $("#memberAmount").bind("change", memberNoBlur);
    memberNoBlur();
});

//更改父页面元素属性
function removeParentWindowAttr() {
    $("#brand", window.parent.document).removeAttr("disabled");
    $("#model", window.parent.document).removeAttr("disabled");
    $("#contact", window.parent.document).removeAttr("disabled");
    $("#customer", window.parent.document).removeAttr("disabled");
    $("#mobile", window.parent.document).removeAttr("disabled");
    $("#landLine", window.parent.document).removeAttr("disabled");
    $("#vehicleContact", window.parent.document).removeAttr("disabled");
    $("#vehicleMobile", window.parent.document).removeAttr("disabled");

    $("#vehicleColor", window.parent.document).removeAttr("disabled");
    $("#vehicleEngineNo", window.parent.document).removeAttr("disabled");
    $("#vehicleChassisNo", window.parent.document).removeAttr("disabled");

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

    var memberDiscountFalg = true;
    var hiddenTotal = $("#hiddenTotal").val() * 1;
    var total = $("#orderTotal").text() * 1;
    var couponAmount = $("#couponAmount").text()*1;
    if ($("#memberDiscountCheck")[0].checked) {
        if (!$.trim($("#accountMemberNo").val())) {
            memberDiscountFalg = false;
            $("#confirmBtn").removeAttr("disabled");
            $("#memberDiscountCheck")[0].checked = false;
            nsDialog.jAlert("请输入会员号!");
            return;
        } else {
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
                        if (!memberDiscountPage || memberDiscountPage == "无折扣") {
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
    }
    else {
        if (!G.Number.equalsTo(hiddenTotal, total)) {
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
    $("select[name$='.consumeTypeStr']", window.parent.document).each(function () {
        if ($(this, window.parent.document).val() == "TIMES") {
            result = true;
        }
    });
    var memberAmount = $("#memberAmount").val();
    if (G.rounding(memberAmount)> 0) {
        result = true;
    }
    if (result == true) {
        if (accountMemberNo == null || accountMemberNo == undefined || accountMemberNo == "") {
            showMessage.fadeMessage("35%", "40%", "slow", 2000, "请输入会员卡号");
            result = true;
            $("#confirmBtn").removeAttr("disabled");
            return;
        } else {
            var parentWinMemberNo = $('#accountMemberNo').attr('data-memberNo');
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
    cashAmount = modifyStringToNumber(cashAmount);
    bankAmount = modifyStringToNumber(bankAmount);
    bankCheckAmount = modifyStringToNumber(bankCheckAmount);
    accountDiscount = modifyStringToNumber(accountDiscount);
    accountDebtAmount = modifyStringToNumber(accountDebtAmount);
    settledAmount = modifyStringToNumber(settledAmount);
    memberAmount = modifyStringToNumber(memberAmount);
    var accountMemberNo = $('#accountMemberNo').val();
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
        window.parent.document.getElementById("isPrint").value = true;
    }
    window.parent.document.getElementById("sendMemberSms").checked = $("#sendMessage").attr('checked');

    $("#washBeautyOrderForm", window.parent.document).attr('action', 'washBeauty.do?method=validateWashBeautyOrder');

    $("#washBeautyOrderForm", window.parent.document).ajaxSubmit(function (data) {
        if (data == "success") {
            window.parent.document.getElementById("mask").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox_account").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox_account").src = "";
            removeParentWindowAttr();
            $("#washBeautyOrderForm", window.parent.document).removeAttr('action');
            $("#washBeautyOrderForm", window.parent.document).attr('action', 'washBeauty.do?method=saveWashBeautyOrder');
            $(".businessCategoryName", window.parent.document).removeAttr("disabled");
            $("#washBeautyOrderForm", window.parent.document).submit();
            $("#confirmBtn", window.parent.document).attr("disabled", true);
            $("#confirmBtn").removeAttr("disabled");
        } else {
            alert(data);
            $("#confirmBtn").removeAttr("disabled");
            //清空父页面数据
            window.parent.document.getElementById("cashAmount").value = 0;
            window.parent.document.getElementById("bankAmount").value = 0;
            window.parent.document.getElementById("bankCheckAmount").value = 0;
            window.parent.document.getElementById("bankCheckNo").value = '';
            window.parent.document.getElementById("orderDiscount").value = 0;
            window.parent.document.getElementById("debt").value = 0;
            window.parent.document.getElementById("settledAmount").value = 0;
            window.parent.document.getElementById("accountMemberPassword").value = '';
            window.parent.document.getElementById("memberAmount").value = 0;
            window.parent.document.getElementById("huankuanTime").value = '';
            window.parent.document.getElementById("afterMemberDiscountTotal").value = '';
            window.parent.document.getElementById("isPrint").value = '';
            window.parent.document.getElementById("sendMemberSms").checked = false;
            window.parent.document.getElementById("memberDiscountRatio").value = '';
            window.parent.document.getElementById("couponAmount").value = 0;
        }
    });


}

function disableAttr() {
    $("#cashAmount").attr('disabled', 'disabled');
    $("#bankAmount").attr('disabled', 'disabled');
    $("#bankCheckAmount").attr('disabled', 'disabled');
    $("#bankCheckNo").attr('disabled', 'disabled');
    $("#accountDiscount").attr('disabled', 'disabled');
    $("#accountDebtAmount").attr('disabled', 'disabled');
    $("#settledAmount").attr('disabled', 'disabled');
    $("#accountMemberNo").attr('disabled', 'disabled');
    $("#accountMemberPassword").attr('disabled', 'disabled');
    $("#memberAmount").attr('disabled', 'disabled');
    $("#confirmBtn").attr('disabled', 'disabled');
    $("#huankuanTime").attr('disabled', 'disabled');
}


function modifyStringToNumber(value) {
    if (value == null || value == undefined || value == "") {
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
}

//实收为0，选择挂账
function selectDebt() {
    var discount = $("#accountDiscount").val();
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
    $("#accountDebtAmount").val(dataTransition.rounding(total - discount, 2));

    $("#selectBtn").hide();
    st.moneyColor();
}

//实收为0，选择优惠
function selectDiscount() {
    var discount = $("#accountDiscount").val();
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
    $("#accountDiscount").val(dataTransition.rounding(total - debt, 2));

    $("#selectBtn").hide();
    st.moneyColor();
}

function validate() {
    var foo = APP_BCGOGO.Validator;

    var discount = $("#accountDiscount").val() ? $("#accountDiscount").val() * 1 : 0;
    if (null != discount && "" != discount && !foo.stringIsPrice("" + discount)) {
        alert("请填写正确的价格！");
        $("#accountDiscount").val("");
        return false;
    }

    var total = $("#orderTotal").html() * 1;
    var debt = $("#accountDebtAmount").val() ? $("#accountDebtAmount").val() * 1 : 0;
    if (null != debt && "" != debt && !foo.stringIsPrice("" + debt)) {
        alert("请填写正确的价格！");
        $("#accountDebtAmount").val("");
        return false;
    }
    var cash = $("#cashAmount").val() ? $("#cashAmount").val() * 1 : 0;
    if (null != cash && "" != cash && !foo.stringIsPrice("" + cash)) {
        alert("请填写正确的价格！");
        $("#cashAmount").val("");
        return false;
    }
    var bankCard = $("#bankAmount").val() ? $("#bankAmount").val() * 1 : 0;
    if (null != bankCard && "" != bankCard && !foo.stringIsPrice("" + bankCard)) {
        alert("请填写正确的价格！");
        $("#bankAmount").val("");
        return false;
    }

    var memberAmount = $("#memberAmount").val() ? $("#memberAmount").val() * 1 : 0;
    if (null != memberAmount && "" != memberAmount && !foo.stringIsPrice("" + memberAmount)) {
        alert("请填写正确的价格！");
        $("#memberAmount").val("");
        return false;
    }

    var check = $("#bankCheckAmount").val() ? $("#bankCheckAmount").val() * 1 : 0;
    if (null != check && "" != check && !foo.stringIsPrice("" + check)) {
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
    discount = discount * 1;
    debt = debt * 1;
    cash = cash * 1;
    bankCard = bankCard * 1;
    check = check * 1;
    memberAmount = memberAmount * 1;
    if (debt > 0) {
        if (!$("#mobile").val() && $("#flag").attr("isNoticeMobile") == "false") {
            $("#div_mobile").html("有欠款请最好填写手机号码");
            $("#inputMobile").show();
            return false;
        }
//		if (null == $("#huankuanTime").val() || "" == $("#huankuanTime").val()) {
//			alert("有欠款请输入大于今天的还款日期");
//			return false;
//		}
        if ($("#huankuanTime").val()) {
            var huankuanDate = $("#huankuanTime").val().replace(/[^\d]+/g, "");
            var year = new Date().getFullYear();
            var month = new Date().getMonth() + 1;
            var day = new Date().getDate();
            var nowDate = "" + year + (month.toString().length == 1 ? ("0" + month) : month) + (day.toString().length == 1 ? ("0" + day) : day);

            if (huankuanDate * 1 < nowDate * 1) {
                alert("还款时间必须大于当前时间！");
//				$("#huankuanTime").val("");
                return false;
            }
        }
    }
    else {
        $("#huankuanTime").val("");
    }

    if (total > 0) {
        if (GLOBAL.Number.equalsTo($("#settledAmount").val(), 0) && GLOBAL.Number.equalsTo(discount + debt, total)) {
            if (!confirm("实收为0，请确认是否挂账或优惠赠送。")) {
                return false;
            }
        }

        if (!GLOBAL.Number.equalsTo($("#settledAmount").val() * 1, cash + bankCard + check + memberAmount)) {
            var msg = "实收金额与现金、银行";
            if ("true" == $("#isMemberSwitchOn").val()) {
                msg += "、会员储值";
            }
            msg += "或支票的金额不符，请修改。如果挂账或优惠赠送，请输入0。";
            alert(msg);
            return false;
        }

        if (GLOBAL.Number.equalsTo($("#settledAmount").val() * 1, cash + bankCard + check + memberAmount) && !GLOBAL.Number.equalsTo($("#settledAmount").val(), total - discount - debt)) {
            alert("实收金额与优惠、挂账金额不符合，请修改。");
            return false;
        }

        if (GLOBAL.Number.equalsTo($("#settledAmount").val() * 1, cash + bankCard + check + memberAmount) && GLOBAL.Number.equalsTo($("#settledAmount").val(), total - discount - debt)) {
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
                msg += check > 0 ? "支票：" + check + "元)" : ")";
                if (msg.substring(msg.length - 2, msg.length - 1) == ',') {
                    msg = msg.substring(0, msg.length - 2) + ")";
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
// 与accountDetail.js中相同，改变的话要同步改变。
function memberNoBlur() {
    if (GLOBAL.Lang.isEmpty($("#accountMemberNo").val())) {
        $("#memberDiscountRatio").html("").parent().hide();
        $("#memberBalance").html("0.0");
        enableOrDisableMsgCheck('disable');
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

            if (window.parent.document.getElementById("total").value == 0) {
                $("#memberDiscountCheck").attr("disabled", true);
            }

            enableOrDisableMsgCheck('enable');
            if (json.idStr != $("#customerId", window.parent.document).val() && (null == json.mobile || !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(json.mobile))) {
                $("#sendMessage").removeAttr("checked");
                $("#sendMessage").unbind().bind("click", function () {
                    alert("此会员号并无手机号码，无法发送短信。");
                    return false;
                });
            } else {
                if($("#smsSwitch").val() == 'true') {
                    $("#sendMessage").attr("checked", "checked");
                }
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
 * 短信框是否disable, accountDetail.js中有相同方法
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
    var usingCard = false;
    if ($("#memberAmount").val() > 0) {
        usingCard = true;
    }
    $("select[name$='.consumeTypeStr']", window.parent.document).each(function () {
        if ($(this).val() == "TIMES") {
            usingCard = true;
            return false;
        }
    });
    enableOrDisableMsgCheck(usingCard ? 'enable' : 'disable');
}

function initAccountDate(total) {
    $("#orderTotal").text(total);
    $("#settledAmount,#cashAmount").val(total);
    $("#accountDiscount,#accountDebtAmount,#memberAmount,#bankAmount,#bankCheckAmount,#bankCheckNo,#huankuanTime").val("");
    st.moneyColor();
}

$(function(){
    st.start();
    enableOrDisableMsgCheck();
    $('#sendMessage').attr('checked', $('#smsSwitch').val() == 'true');
    var map = {};
    $("select[name$='.consumeTypeStr']", window.parent.document).each(function () {
        map[$(this).val()] = true;
    });
    map['COUPON'] && !map['MONEY'] && !map['TIMES'] && $('#sendMessage').attr('checked', false) && $('#sendMessage').attr('disabled', true);
    $('#sendMessage').attr('disabled') && $('#sendMessage').attr('checked', false);
    console.log('洗车美容');
});