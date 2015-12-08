/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-24
 * Time: 上午11:10
 * 结算详细JS 逻辑控制
 *
 */

/**
 * 弹出框遮罩，关闭按钮操作
 */

$(function () {
    $("#huankuanTime")
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

    $("#div_close_pay_detail,#cancleBtnPayDetail").bind("click", function () {
        closeWindow();
    });
});

function closeWindow() {
    // fixed bug 5925, when iframe closed, focus window.top --> first div
    with (window.top.document) {
        getElementById("mask").style.display = "none";
        getElementById("iframe_PopupBox_account").style.display = "none";
        getElementById("saveDraftBtn").disabled = false;
    }
    self.frameElement.blur();
    $(window.top.document.body)
        .append($("<input type='text' id='focusBackToMe'>"))
        .find("#focusBackToMe")
        .focus()
        .select()
        .remove();
}

$(function () {
    var total = jQuery("#total", window.parent.document).val();
    //优惠
    var deduction = jQuery("#stroageSupplierDeduction", window.parent.document).val();
    //欠款
    var creditAmount = jQuery("#stroageCreditAmount", window.parent.document).val();
    var cash = jQuery("#cash", window.parent.document).val();
    var bankCardAmount = jQuery("#bankCardAmount", window.parent.document).val();
    var checkAmount = jQuery("#checkAmount", window.parent.document).val();
    var checkNo = jQuery("#checkNo", window.parent.document).val();
    var actuallyPaid = jQuery("#stroageActuallyPaid", window.parent.document).val();
    //定金
    var depositAmount = jQuery("#depositAmount", window.parent.document).val();


    total = modifyStringToNumber(total);
    deduction = modifyStringToNumber(deduction);
    creditAmount = modifyStringToNumber(creditAmount);
    cash = modifyStringToNumber(cash);
    bankCardAmount = modifyStringToNumber(bankCardAmount);
    checkAmount = modifyStringToNumber(checkAmount);
    actuallyPaid = modifyStringToNumber(actuallyPaid);
    depositAmount = modifyStringToNumber(depositAmount);

    $("#pay_total").html(total);

    if (jQuery("#id", window.parent.document).val() != "") {

        $("#deduction").val(deduction);
        $("#creditAmount").val(creditAmount);
        $("#cash").val(cash);
        $("#bankCardAmount").val(bankCardAmount);
        $("#checkAmount").val(checkAmount);
        $("#actuallyPaid").val(actuallyPaid);
        $("#depositAmount").val(depositAmount);
        $("#checkNo").val(checkNo);

        var chongzhang = $("#chongzhang").html();

        if (chongzhang && chongzhang * 1 > 0) {
            $("#chongzhangTd").show();
        }

        jQuery("input").not(jQuery("#div_close_pay_detail,#cancleBtnPayDetail")).each(function () {
            jQuery(this).attr("disabled", "disabled");
            $(this).unbind("dblclick");
        });
        $("#checkNo").unbind("click");
    } else {
        $("#actuallyPaid").val(total);
        //供应商定金
        var deposit_avaiable = $("#deposit_avaiable").html();
        deposit_avaiable = modifyStringToNumber(deposit_avaiable);

        if (deposit_avaiable * 1 >= total) {
            $("#depositAmount").val(total);
        }
        else if (deposit_avaiable * 1 > 0 && deposit_avaiable * 1 < total) {

            $("#cash").val(dataTransition.rounding(total - deposit_avaiable, 2));

            $("#depositAmount").val(deposit_avaiable);
        }
        else if (deposit_avaiable * 1 == 0) {
            $("#cash").val(total);
        }
    }

    //     选择挂账，则清除现金、银行、支票、定金为空，挂账=应付-扣款
    jQuery("#creditAmountBtn").bind("click", function () {
        if (jQuery("#type").val() == "mayPay") {             //如果是应付款页面
            var payTotal = dataTransition.rounding(parseFloat(jQuery("#totalCreditAmount").text() == "" ? 0 : jQuery("#totalCreditAmount").text()), 2);       //应付
            var deduction = dataTransition.rounding(parseFloat(jQuery("#supplier_deduction").val() == "" ? 0 : jQuery("#supplier_deduction").val()), 2);       //扣款
//      挂账=应付-扣款
            jQuery("#creditAmount").val(dataTransition.rounding(payTotal - deduction, 2));
            jQuery("#creditDeductionBtn").css("display", "none");
        } else if (jQuery("#type").val() == "payDetail") {         //如果是付款详细界面
            var payTotal = dataTransition.rounding(parseFloat(jQuery("#totalCreditAmount").text() == "" ? 0 : jQuery("#totalCreditAmount").text()), 2);       //应付
            var deduction = dataTransition.rounding(parseFloat(jQuery("#deduction").val() == "" ? 0 : jQuery("#deduction").val()), 2);       //扣款
//      挂账=应付-扣款
            jQuery("#creditAmount").val(dataTransition.rounding(payTotal - deduction, 2));
//     清除现金、银行、支票、定金为空
            jQuery("#checkAmount,#bankCardAmount,#cash,#depositAmount,").val(0);                  //支票
            jQuery("#creditDeductionBtn").css("display", "none");
        }
    });
    //选择扣免，则清除现金、银行、支票、定金为空，扣款=应付-挂账
    jQuery("#deductionBtn").bind("click", function () {
        if (jQuery("#type").val() == "mayPay") {             //如果是应付款页面
            var payTotal = dataTransition.rounding(parseFloat(jQuery("#totalCreditAmount").text() == "" ? 0 : jQuery("#totalCreditAmount").text()), 2);       //应付
            var creditAmount = dataTransition.rounding(parseFloat(jQuery("#creditAmount").val() == "" ? 0 : jQuery("#creditAmount").val()), 2);
//      扣款=应付-挂账
            jQuery("#supplier_deduction").val(dataTransition.rounding(payTotal - creditAmount, 2));
            jQuery("#creditDeductionBtn").css("display", "none");
        } else if (jQuery("#type").val() == "payDetail") {         //如果是付款详细界面
            var payTotal = dataTransition.rounding(parseFloat(jQuery("#totalCreditAmount").text() == "" ? 0 : jQuery("#totalCreditAmount").text()), 2);       //应付
            var creditAmount = dataTransition.rounding(parseFloat(jQuery("#creditAmount").val() == "" ? 0 : jQuery("#creditAmount").val()), 2);
//      扣款=应付-挂账
            jQuery("#deduction").val(dataTransition.rounding(payTotal - creditAmount, 2));
//     清除现金、银行、支票、定金为空
            jQuery("#checkAmount,#bankCardAmount,#cash,#depositAmount").val(0);                  //支票
            jQuery("#creditDeductionBtn").css("display", "none");
        }


    });
    //挂账，扣款，免付弹出框close
    jQuery("#cancleCreditDeducation_div_close,#cancleCreditDeducationBtn").bind("click", function () {
        jQuery("#creditDeductionBtn").css("display", "none");
    });

    //挂账
    //     选择挂账，则清除现金、银行、支票、定金为空，挂账=应付-扣款
    jQuery("#creditAmountBtn").bind("click", function () {
        var payTotal = dataTransition.rounding(parseFloat(jQuery("#totalCreditAmount").text() == "" ? 0 : jQuery("#totalCreditAmount").text()), 2);       //应付
        var deduction = dataTransition.rounding(parseFloat(jQuery("#deduction").val() == "" ? 0 : jQuery("#deduction").val()), 2);       //扣款
//      挂账=应付-扣款
        jQuery("#creditAmount").val(dataTransition.rounding(payTotal - deduction, 2));
        jQuery("#creditDeductionBtn").css("display", "none");
    });
    //扣款
    //选择扣免，则清除现金、银行、支票、定金为空，扣款=应付-挂账
    jQuery("#deductionBtn").bind("click", function () {
        var payTotal = dataTransition.rounding(parseFloat(jQuery("#totalCreditAmount").text() == "" ? 0 : jQuery("#totalCreditAmount").text()), 2);       //应付
        var creditAmount = dataTransition.rounding(parseFloat(jQuery("#creditAmount").val() == "" ? 0 : jQuery("#creditAmount").val()), 2);       //扣款
//      挂账=应付-扣款
        jQuery("#deduction").val(dataTransition.rounding(payTotal - creditAmount, 2));
        jQuery("#creditDeductionBtn").css("display", "none");
    });
});

//供应商详细信息应付款刷新
function updateCreditAmount() {
    var supplierId = jQuery("#supplierId").val();
    APP_BCGOGO.Net.asyncPost({
        url: "payable.do?method=getCreditAmountBySupplierId",
        data: {supplierId: supplierId},
        cache: false,
        dataType: "json",
        success: function (jsonStr) {
            jQuery("#btnPay").val("").val("应付:" + jsonStr.message + "元")
        }
    });
}

//入库单，供应商详细信息 ；定金刷新
function updateDeposit() {
    var supplierId = jQuery("#supplierId").val();
    APP_BCGOGO.Net.asyncPost({
        url: "payable.do?method=getSumDepositBySupplierId",
        data: {supplierId: supplierId},
        cache: false,
        dataType: "json",
        success: function (jsonStr) {
            jQuery("#btnPayed").val("").val("付定金:" + jsonStr.message + "元")
            jQuery("#deposit_avaiable").text("").text(dataTransition.rounding(jsonStr.message, 2));
        }
    });
}

function modifyStringToNumber(value) {
    if (value == null || value == undefined || value == "") {
        value = 0;
    }
    return value * 1;
}

$(function () {
    st.initDom = function () {
        st.dom.orderTotal = $('#pay_total');
        st.dom.cashAmount = $('#cash');
        st.dom.bankAmount = $('#bankCardAmount');
        st.dom.bankCheckAmount = $('#checkAmount');
        st.dom.settledAmount = $('#actuallyPaid');
        st.dom.accountDebtAmount = $('#creditAmount');
        st.dom.accountDiscount = $('#deduction');
        st.dom.discount = $('#discount');
        st.dom.confirmBtn = $('#surePayStroage');
        st.dom.discountWord = $('#discountWord');
        st.dom.memberAmount.length || (st.dom.memberAmount = $('#depositAmount'));
    };
    st.start(function () {
        $('#depositAmount').length && st.dom.memberAmount.bind('input',function () {
            var available = getNumber($('#deposit_avaiable').text());
            var prepaid = st.dom.memberAmount.val();
            prepaid > available && st.dom.memberAmount.val(available);
        }).unbind('dblclick').bind('dblclick', function () {
                var amount = st.getAmount();
                var available = getNumber($('#deposit_avaiable').text());
                if(available >= amount.orderTotal){
                    $.proxy(st.handle.dblclick, this)();
                }else{
                     alert('可用定金不足！请重新输入！');
                }
            });
    });
    console.log('商品入库');
});