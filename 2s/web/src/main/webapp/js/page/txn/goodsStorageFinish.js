/**
 * Created by IntelliJ IDEA.
 * User:lw
 * Date: 13-3-14
 * Time: 下午2:24
 * To change this template use File | Settings | File Templates.
 */
$().ready(function() {

  //显示供应商点评div
  if(returnType != 3){
    showSupplierComment();
  }


  $("#supplierCommentCancelBtn").bind("click", function() {
    $("#supplierCommentForm")[0].reset();
    $("#supplierCommentNotice").css("display", "none").dialog("close");
    $("#supplierCommentContent").val("");
    $("#supplierCommentContentRemain").text("500");
  });
  $("#supplierCommentConfirmBtn").bind("click", function() {
    $("#supplierCommentNotice").css("display", "none").dialog("close");
    $("#supplierCommentForm")[0].reset();
    $("#supplierCommentDiv").css("display", "block").dialog({ width: 520, modal: true});
    $("#supplierCommentContent").val("");
    $("#supplierCommentContentRemain").text("500");
  });

  $("#commentCancelBtn").bind("click", function() {
    $("#supplierCommentNotice").css("display", "none").dialog("close");
    $("#supplierCommentForm")[0].reset();
    $("#supplierCommentDiv").css("display", "none").dialog("close");
    $("#supplierCommentContent").val("");
    $("#supplierCommentContentRemain").text("500");
  });

  $("#commentSuccessBtn").bind("click", function() {
    $("#supplierCommentNotice").css("display", "none").dialog("close");
    $("#supplierCommentForm")[0].reset();
    $("#supplierCommentDiv").css("display", "none").dialog("close");
    $("#supplierCommentSuccess").css("display", "none").dialog("close");
    $("#supplierCommentContent").val("");
    $("#supplierCommentContentRemain").text("500");
  });

  $("#redirectPurchaseOrder").bind("click", function() {
    window.location.href = "RFbuy.do?method=show&id=" + $("#purchaseOrderIdStr").val();
    $("#supplierCommentContent").val("");
    $("#supplierCommentContentRemain").text("500");
  });


  $("#commentConfirmBtn").bind("click", function() {

    $("#commentConfirmBtn").attr("disabled",true);
    $("#supplierCommentForm").ajaxSubmit({
      url: "storage.do?method=saveSupplierComment",
      dataType: "json",
      type: "POST",
      success: function(json) {
        if (json.success) {
          $("#commentConfirmBtn").attr("disabled",false);

          $("#supplierCommentNotice").css("display", "none").dialog("close");
          $("#supplierCommentForm")[0].reset();
          $("#supplierCommentDiv").css("display", "none").dialog("close");
          $("#supplierCommentSuccess").css("display", "block").dialog({ width: 520, modal: true});

        } else{
          $("#commentConfirmBtn").attr("disabled",false);
          nsDialog.jAlert(json.msg);
        }
      },
      error: function(json) {
        $("#commentConfirmBtn").attr("disabled",false);
        nsDialog.jAlert("网络异常，请联系客服");
      }
    });
  });

    //生成入库退货单
    $("#returnStorageBtn").live("click", function(){
        var productIds=new Array();
        $(".item").each(function(){
            var $productId=$(this).find("[id$='.productId']");
            if(!G.isEmpty($productId)&&!G.isEmpty($productId.val())){
                productIds.push($productId.val());
            }
        });
        var purchaseOrderId=$("#id").val();
        var supplierId=$("#supplierId").val();
        if (productIds.length>0&&!G.isEmpty(purchaseOrderId)&&!G.isEmpty(supplierId)){
            window.open("goodsReturn.do?method=createReturnStorageByProductId&productIds=" + productIds.toString() +
                "&purchaseOrderId="+purchaseOrderId+"&supplierId="+supplierId+"&isToSalesReturn=true");
        }
    });
    $(".J_QQ").each(function (index, value) {
        $(this).multiQQInvoker({
            QQ: $(this).attr("data_qq")
        });
    });

});


function getRemainChar(object) {
  var length = $(object).val().length;
  if (length > 500) {
    $(object).val($(object).val().substring(0, 500));
    length = 500;
  }
  $("#" + $(object).attr("id") + "Remain").text(500 - length);
}

function showSupplierComment() {
  if ($("#purchaseSupplierShopId").val()) {
    $("#supplierCommentContent").val("");
    $("#supplierCommentContentRemain").text("500");

    var qualityScoreRatting = new App.Module.Ratting();
    qualityScoreRatting.show({
      score:{
        total:10,
        current:0
      },
      config:{
        starType:"yellow_big",
        isLocked:false,
        isOneOff:false,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.qualityTip
      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#qualityScoreDiv",
      hiddenScore:"#qualityScoreDivHidden"
    });


    var performanceScoreRatting = new App.Module.Ratting();
    performanceScoreRatting.show({
      score:{
        total:10,
        current:0
      },
      config:{
        starType:"yellow_big",
        isLocked:false,
        isOneOff:false,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.performanceTip

      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#performanceScoreDiv",
      hiddenScore:"#performanceScoreDivHidden"
    });

    var speedScoreRatting = new App.Module.Ratting();
    speedScoreRatting.show({
      score:{
        total:10,
        current:0
      },
      config:{
        starType:"yellow_big",
        isLocked:false,
        isOneOff:false,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.speedTip

      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#speedScoreDiv",
      hiddenScore:"#speedScoreDivHidden"
    });

    var attitudeScoreRatting = new App.Module.Ratting();
    attitudeScoreRatting.show({
      score:{
        total:10,
        current:0
      },
      config:{
        starType:"yellow_big",
        isLocked:false,
        isOneOff:false,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.attitudeTip

      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#attitudeScoreDiv",
      hiddenScore:"#attitudeScoreDivHidden"

    });


    $("#supplierCommentNotice").css("display", "block").dialog({ width: 520, modal: true});
  }
}
