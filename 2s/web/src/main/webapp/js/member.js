
//点击现金进入结算界面 如果不是已结算的单据 在进入结算页面 进行页面信息校验
function accountTypeChange() {
  var orderValue = jQuery("#status", window.parent.document).val();
	if (!(orderValue == "REPAIR_SETTLED" || orderValue == "REPAIR_REPEAL")) {
    if (isEmptyItem() && isEmptyService()) {
      alert("施工单和材料单均未填写");
      return;
    }
		var result = validateOrderInfo();
		if (result == true) {
      //如果品名，品牌，规格，型号为(无)，就设置为''
      jQuery("input[name$='.productName'],input[name$='.brand'],input[name$='.spec'],input[name$='.model']").each(function() {
        if (jQuery.trim(jQuery(this).val()) == '(无)') {
          jQuery(this).val('');
        }
      });

            var msg = validateOtherIncomeInfo();

            if(!GLOBAL.Lang.isEmpty(msg))
            {
                nsDialog.jAlert("其他费用列表 ："+msg);
                return;
            }

      //如果进厂里程为空，就赋值0
      if (jQuery.trim(jQuery("#input_startMileage").val()) == '') {
        jQuery("#input_startMileage").val(0);
      }
      //purchasePrice,price,total如果为空，赋值为0
      jQuery("input[name$='.purchasePrice'],input[name$='.price'],input[name$='.total'],.cPurchasePrice").each(function() {
        if (jQuery.trim(jQuery(this).val()) == '') {
          jQuery(this).val(0);
        }
      });
      //如果是空，则赋值为零
      if (jQuery.trim(jQuery("#settledAmount").val()) == '') {
        jQuery("#settledAmount").val(0);
      }
			bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox_account")[0],'src':'txn.do?method=accountDetail&customerId='+$("#customerId").val()});
		}
  } else {
		  bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox_account")[0],'src':'txn.do?method=accountDetail&customerId='+$("#customerId").val()});
	}
   accountDetailPopAdjust();

}

function accountDetailPopAdjust(){
      $("#iframe_PopupBox_account").css("top","350px");
 }

function closeWinDiv() {
		window.parent.document.getElementById("mask").style.display = "none";
		window.parent.document.getElementById("iframe_PopupBox_account").style.display = "none";
		window.parent.document.getElementById("iframe_PopupBox_account").src = "";
}

function consumeTypeChange(domObj) {
  var idPrefix = domObj.id.split(".")[0];
  if (idPrefix != null && idPrefix != undefined) {
    idPrefix = idPrefix + ".consumeType";
  }
  var consumeId = idPrefix;
  var consumeTypeValue = document.getElementById(consumeId).value;
  if (consumeTypeValue == "MONEY") {
    var nextTd = domObj.id.split(".")[0] + ".total";
    var td = document.getElementById(nextTd);
    td.disabled = false;
  } else if (consumeTypeValue == "TIMES") {
    var nextTd = domObj.id.split(".")[0] + ".total";
    var td = document.getElementById(nextTd);
    td.value = 0;
    td.disabled = true;
    checkService(domObj.id.split(".")[0]);
  }
  setTotal();
}

//判断每个服务，该客户是否有次计次划卡项目
//前台在选择服务或者更改消费类型时，后台判断该客户有无此项计次划卡项目
function checkService(idPrefix) {
  var serviceName = jQuery("#" + idPrefix + "\\.service").val();
  var customerId = jQuery("#customerId").val();
  var licenceNo = jQuery("#licenceNo").val();

  if (jQuery("#" + idPrefix + "\\.consumeType").val() == "MONEY") {
    return;
  }
  if (serviceName == null || serviceName == "") {
    return;
  }
  if (customerId == null || customerId == "") {
    return;
  }
  if (licenceNo == null || licenceNo == "") {
    return;
  }

  var ajaxUrl = "txn.do?method=judgeService&vehicleNumber=" +
      encodeURIComponent($.trim($("#licenceNo").val())) + "&customerId=" + customerId + "&serviceName=" + encodeURI(serviceName);
  bcgogoAjaxQuery.setUrlData(ajaxUrl, {});
  bcgogoAjaxQuery.ajaxQuery(function(data) {
    if (data != "success") {
      showMessage.fadeMessage("35%", "40%", "slow", 4000, data);
    }
  });
}

//判断每个服务，该客户是否有次计次划卡项目
//前台在选择服务或者更改消费类型时，后台判断该客户有无此项计次划卡项目
function checkMemberService(idPrefix) {

  var isContainService = false;
  var serviceName = jQuery("#" + idPrefix + "\\.service").val();
  var customerId = jQuery("#customerId").val();
  var licenceNo = jQuery("#licenceNo").val();
  if (serviceName == null || serviceName == "") {
    return;
  }
  if (customerId == null || customerId == "") {
    return;
  }
  if (licenceNo == null || licenceNo == "") {
    return;
  }
  var ajaxUrl = "txn.do?method=judgeService&vehicleNumber=" +
      encodeURIComponent($.trim($("#licenceNo").val())) + "&customerId=" + customerId + "&serviceName=" + encodeURI(serviceName);

    var data = APP_BCGOGO.Net.syncAjax({url:ajaxUrl
                });


    if (data == "\"success\"")
        isContainService = true;


    return isContainService;
}

