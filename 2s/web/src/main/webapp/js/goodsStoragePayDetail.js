/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-24
 * Time: 上午10:13
 * 入库单结算详细
 */
$(document).ready(function() {

     jQuery("#paidtype").val("surPay");
    /**
     * 确认付款按钮Click事件
     */
    jQuery("#surePayStroage").bind("click", function() {

	      if($("#surePayStroage").attr("disabled")){
		      return;
	      }
	      if($("#surePayStroage").attr("submitStatus") == "submit"){
		      return;
	      }
	      $("#surePayStroage").attr("submitStatus", "submit");
        /**
         * 实付金额=0
         *判断：应付<>扣款+挂账，则提示：“实付为0，是否挂账或扣款免付？”，挂扣免
         *选择挂账，则清除现金、银行、支票为空，挂账=应收-扣款
         *选择扣款免付，则清除现金、银行、支票为空，扣款=应付-挂账
         */
        var payTotal = dataTransition.rounding(parseFloat(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text()), 2);       //应付
        //扣款
        var deduction = dataTransition.rounding(parseFloat(jQuery("#deduction").val() == "" ? 0 : jQuery("#deduction").val()), 2);
        //欠款挂账
        var creditAmount = dataTransition.rounding(parseFloat(jQuery("#creditAmount").val() == "" ? 0 : jQuery("#creditAmount").val()), 2);
        //现金
        var cash = dataTransition.rounding(parseFloat(jQuery("#cash").val() == "" ? 0 : jQuery("#cash").val()), 2);
        //银行卡
        var bankCardAmount = dataTransition.rounding(parseFloat(jQuery("#bankCardAmount").val() == "" ? 0 : jQuery("#bankCardAmount").val()), 2);
        //支票
        var checkAmount = dataTransition.rounding(parseFloat(jQuery("#checkAmount").val() == "" ? 0 : jQuery("#checkAmount").val()), 2);
        //支票号码
        var checkNo = jQuery("#checkNo").val() == "" ? "" : jQuery("#checkNo").val();
        //用定金
        var depositAmount = dataTransition.rounding(parseFloat(jQuery("#depositAmount").val() == "" ? 0 : jQuery("#depositAmount").val()), 2);
        //实付
        var actuallyPaidPop = dataTransition.rounding(parseFloat(jQuery("#actuallyPaid").val() == "" ? 0 : jQuery("#actuallyPaid").val()), 2);

        var actuallyPaid = dataTransition.rounding(cash + bankCardAmount + checkAmount + depositAmount, 2);
	      //可用定金
        var depositAvaiable = dataTransition.rounding(parseFloat($.trim($("#deposit_avaiable").text()) == "" ? 0 : $.trim($("#deposit_avaiable").text())), 2);

        var paidtype = $("#paidtype").val();
        //supplierId
        var supplierId = jQuery("#supplierId",window.parent.document).val();

        if (depositAmount > depositAvaiable + 0.0001) {
            alert("可用预付款不足！请选择其他支付方式！");
	          jQuery("#surePayStroage").removeAttr("submitStatus");
            return false;
        }
        if (actuallyPaid == 0) {
            if (payTotal != dataTransition.rounding(deduction + creditAmount, 2)) {
                jQuery("#creditDeductionBtn").css("display", "block");
                creditAmount = dataTransition.rounding(jQuery("#creditAmount").val() == "" ? 0 : jQuery("#creditAmount").val(), 2);
                deduction = dataTransition.rounding(jQuery("#deduction").val() == "" ? 0 : jQuery("#deduction").val(), 2);

                alert("总额与实收，挂账，优惠不符，请重新填写金额");
                jQuery("#surePayStroage").removeAttr("submitStatus");
                return;

            } else {
                if (confirm("实付为0，请再次确认是否挂账或扣款!")) {
                    assignmentToParentValue(actuallyPaidPop,deduction,creditAmount,cash,bankCardAmount,checkAmount,depositAmount,checkNo,paidtype);
                    $("#hiddenSubmitClick",window.parent.document).click();
	                if ($("#purchaseInventoryForm", window.parent.document).attr("submitStatus") == "submitCancel") {
		                $("#surePayStroage").removeAttr("submitStatus");
		                $("#purchaseInventoryForm", window.parent.document).removeAttr("submitStatus");
	                }
                }else{
	                 jQuery("#surePayStroage").removeAttr("submitStatus");
                }
            }
        } else {  //实付金额不为0
            /**
             *       实付<>(现金+银行+支票),
             *         弹出提示： “实付金额与现金、银行、支票的金额不符，请修改。
             *        如果挂账或扣款免付，请输入 0。”确定后返回。
             */
            if (actuallyPaidPop != dataTransition.rounding(cash + bankCardAmount + checkAmount + depositAmount, 2)) {
                alert("取用预付金额与现金、银行、支票的金额不符，请修改!");
	            jQuery("#surePayStroage").removeAttr("submitStatus");
            } else if (actuallyPaid != dataTransition.rounding(payTotal - deduction - creditAmount, 2)) {
                /**
                 * 判断：实付<>(应付-扣款-挂账),提示:“实付金额与扣款、
                 *        挂账金额不符合，请修改。” 确定后返回。
                 */
                alert("实付金额与优惠、挂账金额不符合，请修改。");
	            jQuery("#surePayStroage").removeAttr("submitStatus");
            } else {
                if (confirm("本次结算应付：" + payTotal + " 元，优惠：" + deduction + " 元，挂账" + creditAmount + " 元，实付：" + actuallyPaid + " 元\n（现金：" + cash + " 元，银行卡：" + bankCardAmount + " 元，支票：" + checkAmount + " 元，取用预付款：" + depositAmount + " 元）")) {

                    assignmentToParentValue(actuallyPaidPop,deduction,creditAmount,cash,bankCardAmount,checkAmount,depositAmount,checkNo,paidtype);
                    $("#hiddenSubmitClick",window.parent.document).click();
										if ($("#purchaseInventoryForm", window.parent.document).attr("submitStatus") == "submitCancel") {
											$("#surePayStroage").removeAttr("submitStatus");
											$("#purchaseInventoryForm", window.parent.document).removeAttr("submitStatus");
										}
                }else{
	                 jQuery("#surePayStroage").removeAttr("submitStatus");
                }
            }
        }
    });
});

function assignmentToParentValue(actuallyPaidPop,deduction,creditAmount,cash,bankCardAmount,checkAmount,depositAmount,checkNo,paidType)
{
    if("支票号"==checkNo || null ==checkAmount || ""==$.trim(checkAmount.toString()) || 0== checkAmount*1)
    {
        checkNo="";
    }

    $("#actuallyPaid",window.parent.document).val(actuallyPaidPop);
    $("#deduction",window.parent.document).val(deduction);
    $("#creditAmount",window.parent.document).val(creditAmount);
    $("#cash",window.parent.document).val(cash);
    $("#bankCardAmount",window.parent.document).val(bankCardAmount);
    $("#checkAmount",window.parent.document).val(checkAmount);
    $("#depositAmount",window.parent.document).val(depositAmount);
    $("#checkNo",window.parent.document).val(checkNo);
    $("#paidType",window.parent.document).val(paidType);
    $("#stroageActuallyPaid",window.parent.document).val(actuallyPaidPop);
    $("#stroageCreditAmount",window.parent.document).val(creditAmount);
    $("#stroageSupplierDeduction",window.parent.document).val(deduction);

    if($("#checkDetailPrint")[0].checked)
    {
        $("#print",window.parent.document).val("true");
    }
}