var trCount;
var invoiceCommon = APP_BCGOGO.Module.wjl.invoiceCommon;

$(document).ready(function () {

  var errorMsg = $("#errorMsg").html();
  if (!GLOBAL.Lang.isEmpty(errorMsg) && errorMsg!="true") {
      nsDialog.jAlert(errorMsg);
  }

  if (errorMsg != "" && errorMsg != null) {
    alert(errorMsg);
  }

  if ($("#status").val() == 'REPEAL') {
    $(".zuofei").show();
  }
    if ($("#status").val() == 'SELLER_ACCEPTED') {
        $(".acceptedImg").show();
    }
    if ($("#status").val() == 'SELLER_REFUSED') {
        $(".refusedImg").show();
    }
    if($("#status").val() == 'SETTLED'){
      if ($("#statementAccountOrderId").val()) {
        jQuery(".jie_suan").removeClass("jie_suan").addClass("statement_accounted").show();
        $(".zuofei").hide();
        $(".debtSettleImg").hide();
      } else {

        if ($("#hiddenDebt").val() && $("#hiddenDebt").val() * 1 > 0) {
          $(".debtSettleImg").show();
        }
        else {
          $(".jie_suan").show();
        }
      }
    }

    $("#copyInput").bind("click", function () {
        var orderId = $("#id").val();
        if (GLOBAL.Lang.isEmpty($("#id").val())) {
            nsDialog.jAlert("单据ID不存在，请刷新后重试");
            return false;
        }
        APP_BCGOGO.Net.syncPost({
            url:"goodsReturn.do?method=validateCopy",
            dataType:"json",
            data:{"purchaseReturnId" : $("#id").val()},
            success:function(result){
                if(result.success){
                    window.location.href = "goodsReturn.do?method=copyReturnStorage&purchaseReturnId=" + jQuery("#id").val();
                }else{
                    if(result.operation == 'ALERT'){
                        nsDialog.jAlert(result.msg, result.title);
                    }else if(result.operation == 'CONFIRM'){
                        nsDialog.jConfirm(result.msg, result.title, function(resultVal){
                            if(resultVal){
                                window.location.href = "goodsReturn.do?method=copyReturnStorage&purchaseReturnId=" + jQuery("#id").val();
                            }
                        });
                    }
                }
            },
            error:function(){
                nsDialog.jAlert("验证时产生异常，请重试！");
            }
        });
    });

    $("#nullifyBtn").bind("click", function () {
        repealOrder($("#id").val());
    });

    //结算
    $("#accountBtn").bind("click", function () {
        bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_account")[0], 'src':'goodsReturn.do?method=returnAccountDetail&supplierId=' + $("#supplierId").val() + '&purchaseReturnId=' + $("#purchaseReturnForm #id").val()});
    });

    //去打印页面
    $("#printBtn").click(function () {
        if ($("#id").val()) {
            window.showModalDialog("goodsReturn.do?method=printReturnStorageOrder&purchaseReturnId=" + $("#id").val(), '', "dialogWidth=1024px;dialogHeight=768px");
            return;
        }
        if ($("#draftOrderIdStr").val()) {
            window.showModalDialog("draft.do?method=getDraftOrderToPrint&id=" + $("#draftOrderIdStr").val() + "&type=RETURN&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
        }
    });

    if ($("#id").val() && "true" == $("#print").val()) {
        $("#printBtn").click();
    }

    //去打印页面
    $("#addRowBtn").click(function () {
        //TODO 在旧单据中不允许新增一行
        if ($("#id").val()) return;
        returnStorageOrderAdd();
    });
    $("#duizhan").bind("click",function(){
        toCreateStatementOrder($("#supplierId").val(), "SUPPLIER_STATEMENT_ACCOUNT");
    });
    $(".J_QQ").each(function (index, value) {
        $(this).multiQQInvoker({
            QQ: $(this).attr("data_qq")
        });
    });
});
function newOtherOrder(url) {
  window.open(url, "_blank");
}
function newReturnStorageOrder() {
  window.open($("#basePath").val() + "goodsReturn.do?method=createReturnStorage", "_blank");
}

function repealOrder(orderId){
    var offlineRepeal = true;
    if($("#supplierShopId")[0] && !G.isEmpty($("#supplierShopId").val())){
        offlineRepeal = false;
    }
    if(offlineRepeal){
        var enoughDeposit = false;
        var needConfirmRestoreSupplier = false;
        if (APP_BCGOGO.Permission.Version.StoreHouse) {
            if (!validatorStoreHouseOrderRepeal($("#_toStorehouseId").val(), getOrderType(), orderId)) {
                $("#nullifyBtn").removeAttr("status");
                return;
            }
        }
        APP_BCGOGO.Net.syncAjax({
            url: "goodsReturn.do?method=validateRepeal",
            type: "POST",
            dataType: "json",
            data: {
                purchaseReturnId: orderId
            },
            success: function (json) {
                if (json.success) {
                    enoughDeposit = true;
                    if(json.operation && json.operation == 'CONFIRM_RESTORE_SUPPLIER'){
                        needConfirmRestoreSupplier = true;
                    }
                }else{
                    nsDialog.jAlert(json.msg);
                }
            },
            error: function(){
                nsDialog.jAlert("验证异常!");
            }
        });
        if(!enoughDeposit){
            return;
        }
        if(needConfirmRestoreSupplier){
            nsDialog.jConfirm("入库退货时的供应商已不存在，若确认作废，则将恢复该供应商！", null, function(confirmRestore){
                if(confirmRestore){
                    repealOrderAction(offlineRepeal, orderId);
                }
            });
        }else{
            repealOrderAction(offlineRepeal, orderId);
        }
    }else{
        if(APP_BCGOGO.Permission.Version.StoreHouse){
            if((!G.isEmpty($("#supplierShopId").val()) && $("#status").val()=="PENDING")
                || G.isEmpty($("#supplierShopId").val() && $("#status").val() == "SETTLED")){       //这个时候作废才会操作库存
                if (!validatorStoreHouseOrderRepeal($("#_toStorehouseId").val(),getOrderType(),orderId)) {
                    return;
                }
            }
        }
        repealOrderAction(offlineRepeal, orderId);
    }
}

function repealOrderAction(offlineRepeal, orderId){
    var url = "goodsReturn.do?method=repealReturnStorage&purchaseReturnId=";
    if(offlineRepeal){
        url = "goodsReturn.do?method=repeal&purchaseReturnId=";
    }
    nsDialog.jConfirm("是否确认作废?", null, function (returnVal) {
        if (returnVal && !GLOBAL.Lang.isEmpty(orderId)) {

          nsDialog.jConfirm("友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！", null, function (returnVal) {
            if (returnVal) {

              url = url + orderId;
              if (APP_BCGOGO.Permission.Version.StoreHouse) {
                url += "&toStorehouseId=" + $("#_toStorehouseId").val();
              }
              window.location = url;
            }
          });

        }else{
            if(APP_BCGOGO.Permission.Version.StoreHouse){
                $("#_toStorehouseId").val("");
            }
        }
    });
}