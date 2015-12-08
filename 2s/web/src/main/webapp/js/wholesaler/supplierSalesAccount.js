$(document).ready(function() {
  $("#discount").css({"color":"#ADADAD"});
    $("#discount,#accountDiscount").focus(function() {
        var initValue = $("#discount").attr("initValue");
        if(initValue!=null && initValue!='') {
           if($("#discount").val() == initValue || $("#discount").val()=='无折扣' || $("#discount").val()=='全额优惠') {
              $("#discount").val('');
              $("#discount").css({"color":"#000000"});
              $("#discountWord").css({"display":"inline"});
           }else {
              $("#discount").css({"color":"#000000"});
           }
        }
    })
    .blur(function() {
        var initValue = $("#discount").attr("initValue");
        if(initValue!=null && initValue!='') {
           if($("#discount").val() == '') {
              $("#discount").val(initValue);
              $("#discount").css({"color":"#ADADAD"});
              $("#discountWord").css({"display":"none"});
           }
        }
    });
    $("#accountDiscount").keyup(function(event){
        if($(event.target).val()=='0') {
            $("#discount").css({"color":"#ADADAD"});
            $("#discount").val('无折扣');
            $("#discountWord").hide();
        } else if($(event.target).val()== '') {
            $("#discount").css({"color":"#ADADAD"});
            $("#discount").val('请输入折扣');
            $("#discountWord").hide();
        }
        else {
            $("#discount").css({"color":"#000000"});
            $("#discountWord").show();
        }
    });
  if ($("#total").val() == 0) {
    $("#cashAmount,#bankAmount,#bankCheckAmount,#accountDiscount,#accountDebtAmount,#settledAmount").val(0).attr('disabled', 'disabled');
    $("#bankCheckNo").attr('disabled', 'disabled');
    $("#huankuanTime").attr('disabled', 'disabled');
    $("#discount").val(10).attr('disabled', 'disabled');
  }

  $("#bankCheckNo").live("blur", function() {
    returnDefaultValue();
  });

  $("#bankCheckNo").bind("click", function() {
    if ("bankCheckNo" == this.id && "支票号" == $("#bankCheckNo").val()) {
      $("#bankCheckNo").val("");
      $("#bankCheckNo").css("color", "#000");
    }
  });
  $("#div_close,#cancelBtn").click(function() {
    displayFrame();
  });

  $("#settledAmount").val($("#orderTotal").text());

  $("#cashAmount").val($("#settledAmount").val());


  changeColorByMoney();
  returnDefaultValue();

  $("#settledAmount").bind("blur", function() {


    var total = $("#orderTotal").html();
    var cashAmount = $("#cashAmount").val();
    var bankAmount = $("#bankAmount").val();
    var bankCheckAmount = $("#bankCheckAmount").val();
    var accountDiscount = $("#accountDiscount").val();
    var accountDebtAmount = $("#accountDebtAmount").val();
    var settledAmount = $("#settledAmount").val();

    total = modify(total);
    cashAmount = modify(cashAmount);
    bankAmount = modify(bankAmount);
    bankCheckAmount = modify(bankCheckAmount);
    accountDiscount = modify(accountDiscount);
    accountDebtAmount = modify(accountDebtAmount);
    settledAmount = modify(settledAmount);

    if (settledAmount - total > 0.001) {
      $("#settledAmount").val(total);
      settledAmount = total;
    }

    if (accountDiscount + accountDebtAmount + settledAmount - total > 0.001) {
      $("#accountDiscount").val(dataTransition.rounding(total - settledAmount, 2));
      $("#accountDebtAmount").val("");
    }
    else {
      $("#accountDiscount").val(dataTransition.rounding(total - accountDebtAmount - settledAmount, 2));
    }

    if (bankAmount == 0 && bankCheckAmount == 0) {
      $("#cashAmount").val(settledAmount);
      $("#bankCheckNo").val("");
    }
    else if (cashAmount == 0 && bankCheckAmount == 0) {
      $("#bankAmount").val(settledAmount);
      $("#checkNo").val("");
    }
    else if (cashAmount == 0 && bankAmount == 0) {
      $("#bankCheckAmount").val(settledAmount);
    }
    changeColorByMoney();
    returnDefaultValue();
  });

});


function disableAttr() {
  $("#cashAmount,#bankAmount,#bankCheckAmount,#bankCheckNo,#accountDiscount,#accountDebtAmount,#settledAmount").attr('disabled', 'disabled');
  $("#confirmBtn,#huankuanTime,#print").attr('disabled', 'disabled');
}

function modify(value) {
  if (value == null || value == undefined || value == "") {
    value = 0;
  }
  return value * 1;
}

function settleCallBack(json) {
    if (!json) {
        GLOBAL.log(("json is null after txn.do?method=payAll"));
        return;
    }
    if (json.result == "fail") {
        if (json.failMsg) {
            nsDialog.jAlert(json.failMsg);
        } else {
            nsDialog.jAlert("当前客户欠款有更新,请重新打开窗口后结算！");
        }
    } else if(json.result == 'success') {
        nsDialog.jAlert("结算成功！", "结算成功", function(){
            window.parent.location.reload();
        });
    }
}

//提交之前进行校验
function checkDate() {

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
  var huankuanTime = $("#huankuanTime").val();
  if ("支票号" == bankCheckNo) {
    bankCheckNo = "";
  }

  var validateResult = validate();
  if (validateResult != true) {
    $("#confirmBtn").removeAttr("disabled");
    return false;
  }

  cashAmount = modify(cashAmount);
  bankAmount = modify(bankAmount);
  bankCheckAmount = modify(bankCheckAmount);
  accountDiscount = modify(accountDiscount);
  accountDebtAmount = modify(accountDebtAmount);
  settledAmount = modify(settledAmount);

  var orderStatus = $("#status", window.parent.document).val();
  if(orderStatus == 'DISPATCH'){
      $("#supplierSaleAccountForm").ajaxSubmit({
        url:"sale.do?method=saleOrderSettle",
        dataType: "json",
        type: "POST",
        success: function(json) {
          if (json.success) {
            showMessage.fadeMessage("45%", "34%", "slow", 300, "结算成功");
            displayFrame();
            window.parent.location.href = "sale.do?method=toOnlineSalesOrder&salesOrderId=" + $("#orderId").val();
          } else if (!json.success) {
            showMessage.fadeMessage("45%", "34%", "slow", 300, json.msg);
          }
        },
        error:function() {
          showMessage.fadeMessage("45%", "34%", "slow", 300, "网络异常");
        }
      });
  }else if(orderStatus == 'SALE_DEBT_DONE'){
      var customerId = $("#customerId").val();
      var totalAmount = $("#orderTotal").text();
      var orderId = $("#orderId").val();
      var orderTotalAmount = $("#salesOriginTotal").val();
      var payedAmount = $("#payedAmount").val();
      var debtId = $("#debtId").val();
      var customerName = $("#customerName", window.parent.document).text();
      var customerMobile = $("#customerMobile", window.parent.document).text();
      var receivableId = $("#receivableId").val();
      $.post("txn.do?method=payAll&customerId=" + customerId + "&totalAmount=" + totalAmount + "&payedAmount=" + settledAmount
          + "&owedAmount=" + accountDebtAmount + "&orderIdsString=" + orderId + "&orderTypesString=SALE"
          + "&receivableOrderIdsString=" + receivableId + "&orderTotalsString=" + orderTotalAmount + "&orderOwedsString=" + totalAmount
          + "&orderPayedsString=" + payedAmount + "&debtIdsString=" + debtId + "&huankuanTime=" + huankuanTime
          + "&discount=" + accountDiscount + "&cashAmount=" + cashAmount + "&bankAmount=" + bankAmount + "&bankCheckAmount=" + bankCheckAmount
          + "&bankCheckNo=" + encodeURI(bankCheckNo) +"&licenseNosString=&name=" + customerName + "&phone=" + customerMobile, function(json) {
        settleCallBack(json)
      }, 'json');
  }
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


jQuery(document).ready(function() {
   jQuery("#discount").live("keyup blur",function(event){
        var value =  event.target.value;
        if(event.type == "focusout") {
           value = APP_BCGOGO.StringFilter.inputtingPriceFilter(value,2);
           if(value.charAt(value.length-1)=='.') {
             value = value.slice(0,value.length-1);
           }
        }

        else if(event.type == "keyup") {
          if(value != APP_BCGOGO.StringFilter.inputtingPriceFilter(value,2)) {
            value = APP_BCGOGO.StringFilter.inputtingPriceFilter(value,2);
          }
          if(value.length==2) {
             if(value.charAt(1)!='.') {
               value = value.slice(0,1);
             }
          }
          if(value == 0) {
            $("#accountDiscount").val(0).blur();
            event.target.value = value;
            return;
          }
          var discountAmount = (1-value/10)*$("#orderTotal").text();
          discountAmount = dataTransition.rounding(discountAmount,2)
          $("#accountDiscount").val(discountAmount).blur();
        }
        event.target.value = value;
    });

  jQuery("#accountDiscount,#accountDiscount,#accountDebtAmount,#cashAmount").live("keyup blur", function(event) {
    if (event.type == "focusout")
      event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
    else if (event.type == "keyup")
      if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
        event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
      }
    changeColorByMoney();
    returnDefaultValue();
  });

  jQuery("#bankAmount,#bankCheckAmount,#settledAmount").live("keyup blur", function(event) {
    if (event.type == "focusout")
      event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
    else if (event.type == "keyup")
      if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
        event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
      }
    changeColorByMoney();
    returnDefaultValue();
  });
});


jQuery(document).ready(function() {

  jQuery("#accountDiscount,#accountDebtAmount,#cashAmount,#bankAmount,#bankCheckAmount,#settledAmount").live("blur", function() {
    var foo = APP_BCGOGO.Validator;

    if (this.value) {
      this.value = APP_BCGOGO.StringFilter.priceFilter(this.value, 2);
      if (!this.value) {
        alert("请填写正确的价格");
      }
      var money = this.value;
      if (null != money && "" != money && !foo.stringIsPrice(money)) {
        this.value = "";
      }
    }

    changeColorByMoney();
    returnDefaultValue();
  });
  jQuery("#accountDebtAmount").live("blur", function() {
    var discount = jQuery("#accountDiscount").val();
    var total = jQuery("#orderTotal").html() * 1;
    var debt = jQuery("#accountDebtAmount").val();
    var cash = jQuery("#cashAmount").val();
    var bankCard = jQuery("#bankAmount").val();
    var check = jQuery("#bankCheckAmount").val();
    var settledAmount = jQuery("#settledAmount").val();
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

    if (null == settledAmount || "" == settledAmount) {
      settledAmount = 0;
    }

    discount = discount * 1;
    debt = debt * 1;
    cash = cash * 1;
    bankCard = bankCard * 1;
    check = check * 1;
    settledAmount = settledAmount * 1;
    if (debt > total) {
      jQuery("#accountDebtAmount").val(total.toFixed(2));
      jQuery("#accountDiscount").val("");
      clear();
    }

    if (debt == total) {
      clear();
      jQuery("#accountDiscount").val("");
    }

    if (debt < total) {

      if (Math.abs(total - debt - discount - settledAmount) < 0.0001 && Math.abs(settledAmount - check  - cash - bankCard) < 0.0001) {
        return;
      }

      if (null != jQuery("#accountDiscount").val() && "" != jQuery("#accountDiscount").val()) {
        discount = (total - debt) > discount ? discount : (total - debt);
        jQuery("#accountDiscount").val(dataTransition.rounding(discount, 2));
      }

      if (cash + bankCard + check == 0) {
        jQuery("#cashAmount").val(dataTransition.rounding(total - discount - debt, 2));
        jQuery("#bankAmount,#bankCheckAmount,#bankCheckNo").val("");

        if (jQuery("#cashAmount").val() * 1 == 0) {
          jQuery("#cashAmount").val("");
        }
      }

      if (cash + bankCard + check > 0) {
        if (cash > 0) {
          jQuery("#cashAmount").val(dataTransition.rounding(total - discount - debt, 2));
          jQuery("#bankAmount,#bankCheckAmount,#bankCheckNo").val("");
        }
        else if (bankCard > 0) {
          jQuery("#cashAmount,#bankCheckAmount,#bankCheckNo").val("");
          jQuery("#bankAmount").val(dataTransition.rounding(total - discount - debt, 2));
        }
        else {
          jQuery("#cashAmount,#bankAmount").val("");
          jQuery("#bankCheckAmount").val(dataTransition.rounding(total - discount - debt, 2));
        }
      }
    }

    cash = jQuery("#cashAmount").val();
    bankCard = jQuery("#bankAmount").val();
    check = jQuery("#bankCheckAmount").val();

    if (null == cash || "" == cash) {
      cash = 0;
    }
    if (null == bankCard || "" == bankCard) {
      bankCard = 0;
    }

    if (null == check || "" == check) {
      check = 0;
    }

    cash = cash * 1;
    bankCard = bankCard * 1;
    check = check * 1;
    jQuery("#settledAmount").val(dataTransition.rounding(cash + bankCard + check, 2));
    changeColorByMoney();
    returnDefaultValue();
  });


  jQuery("#accountDiscount").live("blur keyup", function() {
    var discount = jQuery("#accountDiscount").val();
    var total = jQuery("#orderTotal").html() * 1;
    var debt = jQuery("#accountDebtAmount").val();
    var cash = jQuery("#cashAmount").val();
    var bankCard = jQuery("#bankAmount").val();
    var check = jQuery("#bankCheckAmount").val();
    var settledAmount = jQuery("#settledAmount").val();
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

    if (null == settledAmount || "" == settledAmount) {
      settledAmount = 0;
    }
    discount = discount * 1;
    debt = debt * 1;
    cash = cash * 1;
    bankCard = bankCard * 1;
    check = check * 1;
    settledAmount = settledAmount * 1;
    if (discount > total) {
      jQuery("#accountDiscount").val(total.toFixed(2));
      jQuery("#accountDebtAmount").val("");
      debt = 0;
      jQuery("#discount").css({"color":"#ADADAD"}).val('全额优惠');
      jQuery("#discountWord").hide();
      clear();
    }

    if (discount == total) {
      clear();
      jQuery("#accountDebtAmount").val("");
    }

    if (discount < total) {

      if (Math.abs(total - debt - discount - settledAmount) < 0.0001 && Math.abs(settledAmount - check  - cash - bankCard) < 0.0001) {
        return;
      }

      if (null != jQuery("#accountDebtAmount").val() && "" != jQuery("#accountDebtAmount").val()) {
        debt = (total - discount) > debt ? debt : (total - discount);
        jQuery("#accountDebtAmount").val(dataTransition.rounding(debt, 2));
      }

      if (cash + bankCard + check  == 0) {
        jQuery("#cashAmount").val(dataTransition.rounding(total - discount - debt, 2));
        jQuery("#bankAmount").val("");
        jQuery("#check").val("");
        jQuery("#checkNo").val("");

        if (jQuery("#cashAmount").val() * 1 == 0) {
          jQuery("#cashAmount").val("");
        }
      }

      if (cash + bankCard + check  > 0) {
        if (cash > 0) {
          jQuery("#cashAmount").val(dataTransition.rounding(total - discount - debt, 2));
          jQuery("#bankAmount").val("");
          jQuery("#bankCheckAmount").val("");
          jQuery("#bankCheckNo").val("");
        }
        else if (bankCard > 0) {
          jQuery("#cashAmount").val("");
          jQuery("#bankAmount").val(dataTransition.rounding(total - discount - debt, 2));
          jQuery("#bankCheckAmount").val("");
          jQuery("#bankCheckNo").val("");
        }
        else {
          jQuery("#cashAmount").val("");
          jQuery("#bankAmount").val("");
          jQuery("#bankCheckAmount").val(dataTransition.rounding(total - discount - debt, 2));
        }
      }
    }

    cash = jQuery("#cashAmount").val();
    bankCard = jQuery("#bankAmount").val();
    check = jQuery("#bankCheckAmount").val();
    if (null == cash || "" == cash) {
      cash = 0;
    }
    if (null == bankCard || "" == bankCard) {
      bankCard = 0;
    }

    if (null == check || "" == check) {
      check = 0;
    }
    cash = cash * 1;
    bankCard = bankCard * 1;
    check = check * 1;
    jQuery("#settledAmount").val(dataTransition.rounding(cash + bankCard + check, 2));
    if(discount == 0)  {
      return;
    }
    $("#discount").val(dataTransition.rounding((total - discount)/total*10,2));
    if(total == discount) {
       jQuery("#discount").css({"color":"#ADADAD"}).val('全额优惠');
       jQuery("#discountWord").hide();

    }
    changeColorByMoney();
    returnDefaultValue();
  });

  jQuery("#cashAmount,#bankAmount,#bankCheckAmount").live("blur", function() {
    var cash = jQuery("#cashAmount").val();
    var bankCard = jQuery("#bankAmount").val();
    var check = jQuery("#bankCheckAmount").val();
    if (null == cash || "" == cash) {
      cash = 0;
    }
    if (null == bankCard || "" == bankCard) {
      bankCard = 0;
    }

    if (null == check || "" == check) {
      check = 0;
    }

    cash = cash * 1;
    bankCard = bankCard * 1;
    check = check * 1;
    jQuery("#settledAmount").val(dataTransition.rounding(cash + bankCard + check, 2));
    changeColorByMoney();
    returnDefaultValue();
  });

  jQuery("#settledAmount").live("blur", function() {
    if (!jQuery("#settledAmount").val()) {
      jQuery("#settledAmount").val(0);
    }

  });

  jQuery("#accountDiscount,#accountDebtAmount,#cashAmount,#bankAmount,#bankCheckAmount").bind("dblclick", function() {
    jQuery(this).val(jQuery("#orderTotal").html());
    if (this == jQuery("#accountDiscount")[0]) {
      jQuery("#accountDebtAmount,#cashAmount,#bankAmount,#bankCheckAmount,#bankCheckNo,#discount").val("");
      jQuery("#settledAmount").val(0);
      $("#discount").css({"color":"#ADADAD"}).val('全额优惠');
      $("#discountWord").hide();
      changeColorByMoney();
      returnDefalutValue();
      return;
    }
    if (this == jQuery("#accountDebtAmount")[0]) {
      jQuery("#accountDiscount,#cashAmount,#bankAmount,#bankCheckAmount,#bankCheckNo,#discount").val("");
      jQuery("#settledAmount").val(0);
    }
    if (this == jQuery("#cashAmount")[0]) {
      jQuery("#accountDiscount,#accountDebtAmount,#bankAmount,#bankCheckAmount,#bankCheckNo,#discount").val("");
      jQuery("#settledAmount").val(jQuery("#orderTotal").text());
    }
    if (this == jQuery("#bankAmount")[0]) {
      jQuery("#accountDiscount,#accountDebtAmount,#cashAmount,#bankCheckAmount,#bankCheckNo,#discount").val("");
      jQuery("#settledAmount").val(jQuery("#orderTotal").text());
    }
    if (this == jQuery("#bankCheckAmount")[0]) {
      jQuery("#accountDiscount,#accountDebtAmount,#cashAmount,#bankAmount,#discount").val("");
      jQuery("#settledAmount").val(jQuery("#orderTotal").text());
    }
    $("#discount").css({"color":"#ADADAD"}).val('无折扣');
    $("#discountWord").hide();
    changeColorByMoney();
    returnDefaultValue();
  });

});

//清除：挂账、现金、银行、支票为空，实收=0
function clear() {
  jQuery("#cashAmount").val("");
  jQuery("#bankAmount").val("");
  jQuery("#bankCheckAmount").val("");
  jQuery("#bankCheckNo").val("");
  jQuery("#settledAmount").val(0);
}

//实收为0，选择挂账
function selectDebt() {
  var discount = jQuery("#accountDiscount").val();
  var total = jQuery("#orderTotal").html() * 1;
  var debt = jQuery("#accountDebtAmount").val();
  if (null == discount || "" == discount) {
    discount = 0;
  }
  if (null == debt || "" == debt) {
    debt = 0;
  }

  discount = discount * 1;
  debt = debt * 1;
  clear();
  jQuery("#accountDebtAmount").val((total - discount).toFixed(2));

  jQuery("#selectBtn").hide();
  changeColorByMoney();
  returnDefaultValue();
}

//实收为0，选择优惠
function selectDiscount() {
  var discount = jQuery("#accountDiscount").val();
  var total = jQuery("#orderTotal").html() * 1;
  var debt = jQuery("#accountDebtAmount").val();
  if (null == discount || "" == discount) {
    discount = 0;
  }
  if (null == debt || "" == debt) {
    debt = 0;
  }

  discount = discount * 1;
  debt = debt * 1;
  clear();
  jQuery("#accountDiscount").val((total - debt).toFixed(2));

  jQuery("#selectBtn").hide();
  changeColorByMoney();
  returnDefaultValue();
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
  discount = discount * 1;
  debt = debt * 1;
  cash = cash * 1;
  bankCard = bankCard * 1;
  check = check * 1;
  if (debt > 0) {
    if (null == $("#mobile").val() || "" == $("#mobile").val() && $("#flag").attr("isNoticeMobile") == "false") {
      $("#div_mobile").html("有欠款请最好填写手机号码");
      $("#inputMobile").show();
      return false;
    }
    if ($("#huankuanTime").val()) {
      var huankuanDate = jQuery("#huankuanTime").val().replace(/[^\d]+/g, "");
      var year = new Date().getFullYear();
      var month = new Date().getMonth() + 1;
      var day = new Date().getDate();
      var nowDate = "" + year + (month.toString().length == 1 ? ("0" + month) : month) + (day.toString().length == 1 ? ("0" + day) : day);

      if (huankuanDate * 1 < nowDate * 1) {
        alert("还款时间必须大于当前时间！");
        return false;
      }
    }
  }
  else {
    jQuery("#huankuanTime").val("");
  }
  if (total != 0) {

    if (GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), 0) && GLOBAL.Number.equalsTo(discount + debt, total)) {
      if (!confirm("实收为0，请确认是否挂账或优惠赠送。")) {
        return false;
      }
    }

    if (!GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), cash + bankCard + check )) {
      var msg = "实收金额与现金、银行";
      if ("true" == jQuery("#isMemberSwitchOn").val()) {
        msg += "、会员储值";
      }
      msg += "或支票的金额不符，请修改。如果挂账或优惠赠送，请输入0。";
      alert(msg);
      return false;
    }

    if (GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), cash + bankCard + check ) &&
        !GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), total - discount - debt)) {
      alert("实收金额与优惠、挂账金额不符合，请修改。");
      return false;
    }

    if (GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), cash + bankCard + check) &&
        GLOBAL.Number.equalsTo(jQuery("#settledAmount").val(), total - discount - debt)) {
      var msg = "本次结算应收：" + total + "元"
      if (discount > 0) {
        msg += ",优惠：" + discount + "元";
      }
      if (debt > 0) {
        msg += ",挂账" + debt + "元";
      }
      if (jQuery("#settledAmount").val() > 0) {
        msg += "\n\n";
        msg += "实收：" + jQuery('#settledAmount').val() + "元(";
        msg += cash > 0 ? "现金：" + cash + "元," : "";
        msg += bankCard > 0 ? "银行卡：" + bankCard + "元," : "";

        msg += check > 0 ? "支票：" + check + "元," : "";
        if (msg.substring(msg.length - 1, msg.length) == ',') {
          msg = msg.substring(0, msg.length - 1) + ")";
        }
      }
      if (!confirm(msg)) {
        return false;
      }
    }
  }

  if (null != jQuery("#mobile").val() && "" != jQuery("#mobile").val() &&
      !foo.stringIsMobilePhoneNumber(jQuery("#mobile").val())) {
    alert("请输入正确的手机号");
    jQuery("#mobile").val("");
    jQuery("#mobile").select();
    jQuery("#mobile").focus();
    return false;
  }

  handleMoneyInputEmpty();
  return true;
}

function handleMoneyInputEmpty() {
  if (jQuery("#accountDiscount").val() == null || jQuery("#accountDiscount").val() == "") {
    jQuery("#accountDiscount").val(0);
  }
  if (jQuery("#accountDebtAmount").val() == null || jQuery("#accountDebtAmount").val() == "") {
    jQuery("#accountDebtAmount").val(0);
  }
  if (jQuery("#cashAmount").val() == null || jQuery("#cashAmount").val() == "") {
    jQuery("#cashAmount").val(0);
  }
  if (jQuery("#bankAmount").val() == null || jQuery("#bankAmount").val() == "") {
    jQuery("#bankAmount").val(0);
  }
  if (jQuery("#bankCheckAmount").val() == null || jQuery("#bankCheckAmount").val() == "") {
    jQuery("#bankCheckAmount").val(0);
  }
  if (jQuery("#settledAmount").val() == null || jQuery("#settledAmount").val() == "") {
    jQuery("#settledAmount").val(0);
  }
}


function inputMobile() {
  var foo = APP_BCGOGO.Validator;
  if (null != jQuery("#mobile").val() && "" != jQuery("#mobile").val() &&
      !foo.stringIsMobilePhoneNumber(jQuery("#mobile").val())) {
    alert("请输入正确的手机号");
    jQuery("#mobile").val("");
    jQuery("#mobile").select();
    jQuery("#mobile").focus();
    return;
  }

  var url = "member.do?method=checkMobileDifferentCustomer";
  var isMobileExist = false;
  APP_BCGOGO.Net.syncAjax({url:url, dataType:"json",data:{customerId:$("#customerId").val(),mobile:$("#mobile").val()}, success: function(json) {
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
  jQuery("#flag").attr("isNoticeMobile", true);
  jQuery("#inputMobile").hide();
}

function cancleInputMobile() {
    jQuery("#mobile").val("");
    if ($("#div_mobile").html() == '发送短信必须填写手机号码') {
        $("#sendMessage").removeAttr('checked');
    } else {
        jQuery("#flag").attr("isNoticeMobile", true);
    }
    jQuery("#inputMobile").hide();
}

function changeColorByMoney() {
  if ($("#cashAmount").val() && $("#cashAmount").val() * 1 > 0) {
    $("#cashAmount").attr("class", "lan_txt");
  }
  else {
    $("#cashAmount").removeAttr("class");
  }

  if ($("#bankAmount").val() && $("#bankAmount").val() * 1 > 0) {
    $("#bankAmount").attr("class", "lan_txt");
  }
  else {
    $("#bankAmount").removeAttr("class");
  }

  if ($("#bankCheckAmount").val() && $("#bankCheckAmount").val() * 1 > 0) {
    $("#bankCheckAmount").attr("class", "lan_txt");
  }
  else {
    $("#bankCheckAmount").removeAttr("class");
  }

}

function returnDefaultValue() {
  if (!$("#bankCheckNo").val() || "支票号" == $("#bankCheckNo").val()) {
    $("#bankCheckNo").val("支票号");
    $("#bankCheckNo").css("color", "#9a9a9a");
  }
  else {
    $("#bankCheckNo").css("color", "#000");
  }
}



