/**
 * Created by IntelliJ IDEA.
 * User: goodsStorageFinish.jsp 专用 js
 * Date: 13-3-14
 * Time: 下午2:24
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(function() {

  displayCommentStar();
  displayCommentScore();

  $("#commentCancelBtn").bind("click", function() {
    $("#supplierCommentForm")[0].reset();
    $("#supplierCommentDiv").css("display", "none").dialog("close");
  });

  $("#commentSuccessBtn").bind("click", function() {
    $("#supplierCommentForm")[0].reset();
    $("#supplierCommentDiv").css("display", "none").dialog("close");
    $("#supplierCommentSuccess").css("display", "none").dialog("close");
  });

  $("#commentSuccessBtn").bind("click", function() {
    $("#supplierCommentForm")[0].reset();
    $("#supplierCommentDiv").css("display", "none").dialog("close");
    $("#supplierCommentSuccess").css("display", "none").dialog("close");
  });

  $("#addCommentCancelBtn").bind("click", function() {
    $("#supplierCommentForm")[0].reset();
    $("#addSupplierCommentForm")[0].reset();
    $("#supplierCommentDiv").css("display", "none").dialog("close");
    $("#supplierCommentSuccess").css("display", "none").dialog("close");
    $("#addSupplierCommentDiv").css("display", "none").dialog("close");
  });


  $("#addCommentConfirmBtn").bind("click", function() {

    $("#addCommentConfirmBtn").attr("disabled", true);
    $("#addSupplierCommentForm").ajaxSubmit({
      url: "storage.do?method=addSupplierComment",
      dataType: "json",
      type: "POST",
      success: function(json) {
        if (json.success) {
          $("#addCommentConfirmBtn").attr("disabled", false);
          nsDialog.jAlert("您的评价已追加成功!", "", function () {
            $("#supplierCommentForm")[0].reset();
            $("#supplierCommentDiv").css("display", "none").dialog("close");
            $("#addSupplierCommentDiv").css("display", "none").dialog("close");
            window.location.href = "RFbuy.do?method=show&id=" + $("#id").val();

          });

        } else if (!json.success) {
          $("#addCommentConfirmBtn").attr("disabled", false);
          nsDialog.jAlert(json.msg);
          return false;
        }
      },
      error: function(json) {
        $("#addCommentConfirmBtn").attr("disabled", false);
        nsDialog.jAlert("网络异常，请联系客服");
      }
    });
  });


  $("#commentConfirmBtn").bind("click", function() {

    $("#commentConfirmBtn").attr("disabled", true);
    $("#supplierCommentForm").ajaxSubmit({

      url: "storage.do?method=saveSupplierComment",
      dataType: "json",
      type: "POST",
      success: function(json) {
        if (json.success) {
          $("#commentConfirmBtn").attr("disabled", false);
          nsDialog.jAlert("您的评价已发表成功!1天后生效", "", function () {
            $("#supplierCommentForm")[0].reset();
            $("#supplierCommentDiv").css("display", "none").dialog("close");
            window.location.href = "RFbuy.do?method=show&id=" + $("#id").val();
          });
        } else {
          $("#commentConfirmBtn").attr("disabled", false);
          nsDialog.jAlert(json.msg);
          return false;
        }
      },
      error: function(json) {
        $("#commentConfirmBtn").attr("disabled", false);
        nsDialog.jAlert("网络异常，请联系客服");
      }
    });
  });

  $("#toOnlinePurchaseReturn").bind("click",function(){
      var purchaseOrderId =  $("#id").val();
      var purchaseOrderStatus = $("#status").val();
      var purchaseOrderSupplierShopId = $("#supplierShopId").val();

      if (!GLOBAL.Lang.isEmpty(purchaseOrderSupplierShopId) && GLOBAL.Lang.isNumber(purchaseOrderSupplierShopId)
          &&!GLOBAL.Lang.isEmpty(purchaseOrderId) && GLOBAL.Lang.isNumber(purchaseOrderId)
          && purchaseOrderStatus == "PURCHASE_ORDER_DONE") {
          window.location.href = "onlineReturn.do?method=onlinePurchaseReturnEdit&purchaseOrderId=" + purchaseOrderId;
      }
  });

    var supplierProductIdArr=new Array();
    $(".item").each(function(){
    supplierProductIdArr.push($(this).find(".j_supplierProductId").text());
    });
    initOrderPromotionsDetailForShowPage(supplierProductIdArr, $(".i_mainRight"));

    $(".J_QQ").each(function (index, value) {
        $(this).multiQQInvoker({
            QQ: $(this).attr("data_qq")
        });
    });


});


function addNewSupplierComment() {
  $("#addSupplierCommentDiv").css("display", "block").dialog({ width: 520, modal: true});
  $("#addCommentContentRemain").text("500");
  $("#addCommentContent").val("");
}
function showSupplierComment() {
  $("#supplierCommentDiv").css("display", "block").dialog({ width: 520, modal: true});
  $("#supplierCommentContentRemain").text("500");
  $("#supplierCommentContent").val("");
}


function getRemainChar(object) {
  var length = $(object).val().length;
  if (length > 500) {
    $(object).val($(object).val().substring(0, 500));
    length = 500;
  }
  $("#" + $(object).attr("id") + "Remain").text(500 - length);
}

function displayCommentStar() {
    $("#addCommentContent").val("");
  $("#supplierCommentContent").val("");

  var length = $("#commentSupplier").length;
  if (length == 0) {
    return;
  }
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
}

function displayCommentScore() {

  var qualityScore = $("#qualityScore").val();
  if (qualityScore > 0) {
    var ratting = new App.Module.Ratting();
    ratting.show({
      score:{
        total:5,
        current:qualityScore
      },
      config:{
        starType:"yellow_big",
        isLocked:true,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.qualityTip
      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#qualityScoreTd"
    });
  }

  var performanceScore = $("#performanceScore").val();
  if (performanceScore > 0) {
    var ratting = new App.Module.Ratting();
    ratting.show({
      score:{
        total:5,
        current:performanceScore
      },
      config:{
        starType:"yellow_big",
        isLocked:true,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.performanceTip
      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#performanceScoreTd"
    });
  }

  var speedScore = $("#speedScore").val();
  if (speedScore > 0) {
    var ratting = new App.Module.Ratting();
    ratting.show({
      score:{
        total:5,
        current:speedScore
      },
      config:{
        starType:"yellow_big",
        isLocked:true,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.speedTip
      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#speedScoreTd"

    });
  }

  var attitudeScore = $("#attitudeScore").val();
  if (attitudeScore > 0) {
    var ratting = new App.Module.Ratting();
    ratting.show({
      score:{
        total:5,
        current:attitudeScore
      },
      config:{
        starType:"yellow_big",
        isLocked:true,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.attitudeTip
      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#attitudeScoreTd"
    });
  }

}
