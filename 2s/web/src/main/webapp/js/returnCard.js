$(document).ready(function() {
  var originTotal = $("#returnTotal").val();
  if ($("#div_close") != null) {
    $("#div_close").click(function() {
      closeWindow();
    });
  }
  if ($("#cancelBtn") != null) {
    $("#cancelBtn").click(function() {
      closeWindow();
    });
  }

  $("#returnCash,#returnSettleAmount").val($("#returnTotal").val());

  $("#returnCardSubmitBtn").bind("click", function(){
    $("#returnCardSubmitBtn").attr("disabled", true);
    var valid = validateForm();
    if(!valid){
      return;
    }
    $("#memberCardReturnForm").ajaxSubmit({
      url:"member.do?method=saveReturnCard",
      dataType: "json",
      type: "POST",
      success: function(result) {
        if (result.resu == "success") {
          var orderId = result.orderId;
          alert("退卡成功!");
          if($("#pageLinkedFrom").val()=="deleteCustomer"){
          //如果打印复选框被选中就调用打印
          if (jQuery("#print")[0].checked) {
            $("#hiddenPrintBtn").bind("click", function(){
              window.open("member.do?method=printMemberReturn&orderId=" + orderId + "&now=" + new Date(), "", "width=250px,height=768px");
            });
            $("#hiddenPrintBtn").trigger("click");
            }
            parent.document.getElementById("iframe_returnCard").style.display = "none";
            $(window.parent.document).find("#mask").hide();
            $(window.parent.document.body).getElementById("deleteCustomerButton").click();
          }else{
            //如果打印复选框被选中就调用打印
            if (jQuery("#print")[0].checked) {
              $("#hiddenPrintBtn").bind("click", function(){
                window.open("member.do?method=printMemberReturn&orderId=" + orderId + "&now=" + new Date(), "", "width=250px,height=768px");
              });
              $("#hiddenPrintBtn").trigger("click");
              //window.open("member.do?method=printMemberReturn&orderId=" + orderId + "&now=" + new Date(), "", "width=250px,height=768px");
          }
          window.parent.location.reload();
          }
        } else if (result.resu == "error") {
          alert("退卡失败!");
          $("#returnCardSubmitBtn").removeAttr("disabled");
          closeWindow();
        }

      },
      error:function() {
        alert("退卡失败！");
        $("#returnCardSubmitBtn").removeAttr("disabled");
        closeWindow();
      }
    });
  });

  $("#returnTotal,#returnCash,#returnBank,#returnCheck,#returnSettleAmount").bind("keyup", function() {
      if ($(this).val != APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2)) {
          $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2));
      }
  });
  
  $("#returnTotal,#returnCash,#returnBank,#returnCheck,#returnSettleAmount").bind("blur", function() {
      var foo = APP_BCGOGO.Validator;

      if (this.value) {
          this.value = APP_BCGOGO.StringFilter.priceFilter(this.value, 1);
          if (!this.value) {
              alert("请填写正确的价格");
          }
          var money = this.value;
          if (null != money && "" != money && !foo.stringIsPrice(money)) {
              this.value = "";
          }
      }
  });

  $("#returnCash,#returnBank,#returnCheck").bind("dblclick", function(e) {
      $("#returnCash,#returnBank,#returnCheck").not($(this)).val("");
      $(this).val($("#returnTotal").val());
      $("#returnSettleAmount").val($("#returnTotal").val());
  });

  $("#returnCash,#returnBank,#returnCheck").bind("blur", function(e) {
      var cash = dataTransition.rounding($("#returnCash").val(), 2);
      var bankCard = dataTransition.rounding($("#returnBank").val(), 2);
      var check = dataTransition.rounding($("#returnCheck").val(), 2);
      $("#returnSettleAmount").val(cash+bankCard+check);
  });
  
  $("#returnTotal").bind("blur", function(){
      if(GLOBAL.Number.equalsTo($(this).val(), originTotal)){
        return;
      }
      originTotal = $(this).val();
      $("#returnCash").val($("#returnTotal").val());
      $("#returnSettleAmount").val($("#returnTotal").val());
      $("#returnBank,#returnCheck").val("");
  });

});

function validateForm(){
  var cash = dataTransition.rounding($("#returnCash").val(), 2);
  var bankCard = dataTransition.rounding($("#returnBank").val(), 2);
  var check = dataTransition.rounding($("#returnCheck").val(), 2);
  var total = dataTransition.rounding($("#returnSettleAmount").val(),2);

  if (!GLOBAL.Number.equalsTo(jQuery("#returnSettleAmount").val(), cash + bankCard + check)) {
    alert("实收金额与现金、银行或支票的金额不符，请修改。");
    $("#returnCardSubmitBtn").removeAttr("disabled");
    return false;
  }

  if (GLOBAL.Number.equalsTo(jQuery("#returnSettleAmount").val(), cash + bankCard + check)
      && !GLOBAL.Number.equalsTo(jQuery("#returnSettleAmount").val(), $("#returnTotal").val())) {
    alert("退卡金额与实付不符，请修改。");
    $("#returnCardSubmitBtn").removeAttr("disabled");
    return false;
  }

  var msg = "本次退卡应付：" + total + "元";
  if (jQuery("#returnSettleAmount").val() > 0) {
    msg += "\n\n";
    msg += "实付：" + jQuery('#returnSettleAmount').val() + "元(";
    msg += cash > 0 ? "现金：" + cash + "元," : "";
    msg += bankCard > 0 ? "银行卡：" + bankCard + "元," : "";
    msg += check > 0 ? "支票：" + check + "元)" : ")";
    if (msg.substring(msg.length - 2, msg.length - 1) == ',') {
      msg = msg.substring(0, msg.length - 2) + ")";
    }
  }

  if (!confirm(msg)) {
    $("#returnCardSubmitBtn").removeAttr("disabled");
    return false;
  }else{
    return true;
  }
}

function closeWindow() {
  $(window.parent.document).find("#mask").hide();
  $(window.parent.document).find("#iframe_returnCard").hide();
  try {
    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
  } catch(e) {
    ;
  }
}
