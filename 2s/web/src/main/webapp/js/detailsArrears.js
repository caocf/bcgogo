$(document).ready(function() {


  //各个欠款页面关闭按钮
  $("#cancelButton,#div_close").click(function () {

    $(window.parent.document).find("#mask").hide();
    //车辆页面
    if (parent.document.getElementById("iframe_qiankuan") != null) {
      parent.document.getElementById("iframe_qiankuan").style.display = "none";
    }
    if ($(window.parent.document).find(".tableInfo2") != null) {
      $(window.parent.document).find(".tableInfo2").show();
    }
    if ($(window.parent.document).find("#table_task") != null) {
      $(window.parent.document).find("#table_task").show();
    }
    if ($(window.parent.document).find("#table_productNo_2") != null) {
      $(window.parent.document).find("#table_productNo_2").show();
    }
    if ($(window.parent.document).find("#div_tablereservateTime") != null) {
      $(window.parent.document).find("#div_tablereservateTime").show();
    }
     if (parent.document.getElementById("iframe_PopupBox") != null) {
      parent.document.getElementById("iframe_PopupBox").style.display = "none";
    }
    //客户管理页面
    if (parent.document.getElementById("iframe_PopupBox_1") != null) {
      parent.document.getElementById("iframe_PopupBox_1").style.display = "none";
    }
    if (parent.document.getElementById("iframe_PopupBox_2") != null) {
      parent.document.getElementById("iframe_PopupBox_2").style.display = "none";
    }
    if (parent.document.getElementById("mask") != null) {
      parent.document.getElementById("mask").style.display = "none";
    }
    //销售页面
    if ($(window.parent.document).find(".table_title") != null) {
      $(window.parent.document).find(".table_title").show();
    }
    if ($(window.parent.document).find(".item") != null) {
      $(window.parent.document).find(".item").show();
    }
    if ($(window.parent.document).find(".tableInfo") != null) {
      $(window.parent.document).find(".tableInfo").show();
    }
    try {
      $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
    } catch(e) {
      ;
    }
  });

});


function modifyStringToNumber(value) {
	if (value == null || value == undefined || value == "") {
		value = 0;
	}
	return isNaN(value * 1)?0:value*1;
}




function validate() {
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

  discount = discount * 1;
  debt = debt * 1;
  cash = cash * 1;
  bankCard = bankCard * 1;
  check = check * 1;
  memberAmount = memberAmount * 1;
  memberAmount = isNaN(memberAmount)?0:memberAmount;
  if (debt > 0) {

    if (jQuery("#huankuanTime").val()) {
      var huankuanDate = jQuery("#huankuanTime").val().replace(/[^\d]+/g, "");
      var year = new Date().getFullYear();
      var month = new Date().getMonth() + 1;
      var day = new Date().getDate();
      var nowDate = "" + year + (month.toString().length == 1 ? ("0" + month) : month) + (day.toString().length == 1 ? ("0" + day) : day);

      if (huankuanDate * 1 < nowDate * 1) {
        showMessage.fadeMessage("35%", "40%", "slow", 2000, "还款时间必须大于当前时间！");
        jQuery("#huankuanTime").val("");
        return false;
      }
    }
  }
  else {
    jQuery("#huankuanTime").val("");
  }

  if (total <= 0) {
    alert("请选择要结算的欠款单据");
      return false;
  }

  if (total != 0) {

    if (!GLOBAL.Number.equalsTo(jQuery("#payedAmount").val(), cash + bankCard + check + memberAmount)) {
      var msg = "实收金额与现金、银行、会员储值";
      msg += "或支票的金额不符，请修改。如果挂账或优惠赠送，请输入0";
      alert(msg);
      return false;
    }

    if (GLOBAL.Number.equalsTo(jQuery("#payedAmount").val(), cash + bankCard + check + memberAmount) &&
        !GLOBAL.Number.equalsTo(jQuery("#payedAmount").val(), total - discount - debt)) {
      alert("实收金额与优惠、挂账金额不符合，请修改。");
      return false;
    }

    if (GLOBAL.Number.equalsTo(jQuery("#payedAmount").val(), cash + bankCard + check + memberAmount) &&
        GLOBAL.Number.equalsTo(jQuery("#payedAmount").val(), total - discount - debt)) {
      var msg = "本次结算应收：" + total + "元"
      if (discount > 0) {
        msg += ",优惠：" + discount + "元";
      }
      if (debt > 0) {
        msg += ",挂账" + debt + "元";
      }
      if (jQuery("#payedAmount").val() > 0) {
        msg += "\n\n";
        msg += "实收：" + jQuery('#payedAmount').val() + "元(";
        msg += cash > 0 ? "现金：" + cash + "元," : "";
        msg += bankCard > 0 ? "银行卡：" + bankCard + "元," : "";
        msg += memberAmount > 0 ? "会员储值" + memberAmount + "元," : "";
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

  handleMoneyInputEmpty();

  return true;
}

var defaultVal="0.0";
function handleMoneyInputEmpty() {
  if (jQuery("#discountAmount").val() == null || jQuery("#discountAmount").val() == "") {
    jQuery("#discountAmount").val(defaultVal);
  }
  if (jQuery("#owedAmount").val() == null || jQuery("#owedAmount").val() == "") {
    jQuery("#owedAmount").val(defaultVal);
  }
  if (jQuery("#cashAmount").val() == null || jQuery("#cashAmount").val() == "") {
    jQuery("#cashAmount").val(defaultVal);
  }
  if (jQuery("#bankAmount").val() == null || jQuery("#bankAmount").val() == "") {
    jQuery("#bankAmount").val(defaultVal);
  }
  if (jQuery("#bankCheckAmount").val() == null || jQuery("#bankCheckAmount").val() == "") {
    jQuery("#bankCheckAmount").val(defaultVal);
  }
  if (jQuery("#payedAmount").val() == null || jQuery("#payedAmount").val() == "") {
    jQuery("#payedAmount").val(defaultVal);
  }
  if (jQuery("#memberAmount").val() == null || jQuery("#memberAmount").val() == "") {
    jQuery("#memberAmount").val(defaultVal);
  }

  if (jQuery("#payedAmount").val() == null || jQuery("#payedAmount").val() == "") {
    jQuery("#payedAmount").val(defaultVal);
  }
}






//------------------------------------------改为应收和应付------------------------------------
function toReceivableSettle(customerId,orderType) {
  $('.debt_btn').blur();
  if (customerId == null || $.trim(customerId) == "" || $.trim(customerId) == "null") {
    alert("customerId为空!");
    return;
  }

  var orderId = null;

  if(orderType && undefined != orderType)
  {
      orderId = $("#id").val();
  }
  else
  {
    orderType="";
  }
  bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_2")[0], 'src':" arrears.do?method=toReceivableSettle&customerId="
      + $.trim(customerId) +"&orderType="+orderType+"&orderId="+orderId});
}

//客户或者供应商跳转到对账单
//param :customerOrSupplierId :客户或者供应商id
//param :orderType :客户传值:CUSTOMER_STATEMENT_ACCOUNT 供应商传值:SUPPLIER_STATEMENT_ACCOUNT
function toCreateStatementOrder(customerOrSupplierId, orderType) {
  if (customerOrSupplierId == null || $.trim(customerOrSupplierId) == "" || $.trim(customerOrSupplierId) == "null") {
    return;
  }
  if (orderType == null || $.trim(orderType) == "" || $.trim(orderType) == "null") {
    return;
  }
  window.open("statementAccount.do?method=redirectCreateCustomerBill&customerOrSupplierIdStr=" + customerOrSupplierId + "&orderType=" + orderType);
}


function toPayableSettlement(supplierId,orderType) {
  $('.debt_btn').blur();
  if (supplierId == null || $.trim(supplierId) == "" || $.trim(supplierId) == "null") {
    alert("supplierId为空!");
    return;
  }
  //单个单据界面欠款结算
  var orderId = null;
  if(orderType=="purchaseInventoryOrder")
  {
    orderType = $("#orderType").val();
    orderId = $("#id").val();
  }
  else
  {
      orderType="";
  }

  bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_1")[0], 'src':" arrears.do?method=toPayableSettlement&supplierId="
      + $.trim(supplierId) +"&orderType="+orderType+"&orderId="+orderId});
}

function toExportList(repairOrderId) {
//  if (repairOrderId == null || $.trim(repairOrderId) == "" || $.trim(repairOrderId) == "null") {
//    return;
//  }

  window.open("txn.do?method=exportRepairOrder&repairOrderId=" + repairOrderId );
}