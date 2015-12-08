function achievementNotify() {
  var num = $("#totalShopAchievementConfig").text();
  if (num * 1 > 0) {
    nsDialog.jConfirm("还有" + num + "个商品未支配提成,是否继续配置?", null, function (getVal) {
      if (getVal) {
        $(this).dialog("close");
        $("#totalShopAchievementConfigSpan").click();
        return false;
      } else {
        window.location = "assistantStat.do?method=redirectAssistantStat";
      }
    },true);
    $("#jConfirm").parent().css("top", "517px");
  } else {
    window.location = "assistantStat.do?method=redirectAssistantStat";
  }
}

function setProductAchievement(salesTotalAchievementType, salesTotalAchievementAmount) {
  var idList = "";
  for (var i = 0; i < $(".sales_total_ti_chen_input").size(); i++) {
    idList = idList + $("#productDTOs" + i + "\\.productLocalInfoId").val() + ",";
    $("#productDTOs" + i + "\\.salesTotalAchievementType").val(salesTotalAchievementType);
    $("#productDTOs" + i + "\\.salesTotalAchievementType").attr("title", salesTotalAchievementType);

    $("#productDTOs" + i + "\\.salesTotalAchievementAmount").val(salesTotalAchievementAmount);
    $("#productDTOs" + i + "\\.salesTotalAchievementAmount").attr("title", salesTotalAchievementAmount);
  }
  idList.substr(0, idList.length - 1);

  $("#productDTOListForm").attr("action", "assistantStat.do?method=updateProductAchievement");
  $("#productDTOListForm").ajaxSubmit({
    url:"assistantStat.do?method=updateProductAchievement",
    dataType: "json",
    type: "POST",
    data: {
      idList: idList,
      salesTotalAchievementType: salesTotalAchievementType,
      salesTotalAchievementAmount : salesTotalAchievementAmount
    },
    success: function(result) {
      if (result) {
        if (result.success) {
          $("#totalShopAchievementConfig").text(result.data);
          $("#batchSetSalesTotal_dialog").dialog("close");
        } else {
        }
      }
    },
    error:function() {
      alert("出现异常！");
    }
  });
}


function setProductProfitAchievement(salesProfitAchievementType, salesProfitAchievementAmount) {
  var idList = "";
  for (var i = 0; i < $(".sales_profit_ti_chen_input").size(); i++) {
    idList = idList + $("#productDTOs" + i + "\\.productLocalInfoId").val() + ",";
    $("#productDTOs" + i + "\\.salesProfitAchievementType").val(salesProfitAchievementType);
    $("#productDTOs" + i + "\\.salesProfitAchievementType").attr("title", salesProfitAchievementType);

    $("#productDTOs" + i + "\\.salesProfitAchievementAmount").val(salesProfitAchievementAmount);
    $("#productDTOs" + i + "\\.salesProfitAchievementAmount").attr("title", salesProfitAchievementAmount);
  }
  idList.substr(0, idList.length - 1);

  $("#productDTOListForm").attr("action", "assistantStat.do?method=updateProductProfitAchievement");
  $("#productDTOListForm").ajaxSubmit({
    url:"assistantStat.do?method=updateProductProfitAchievement",
    dataType: "json",
    type: "POST",
    data: {
      idList: idList,
      salesProfitAchievementType: salesProfitAchievementType,
      salesProfitAchievementAmount : salesProfitAchievementAmount
    },
    success: function(result) {
      if (result) {
        if (result.success) {
          $("#totalShopAchievementConfig").text(result.data);
          $("#batchSetSalesProfit_dialog").dialog("close");
        } else {
        }
      }
    },
    error:function() {
      alert("出现异常！");
    }
  });
}

$(document).ready(function () {

  $("#salesTotalAchievementAmount,#salesProfitAchievementAmount,.sales_total_ti_chen_input,.sales_profit_ti_chen_input,#memberNewAmount,#memberRenewAmount").live("keyup blur", function (event) {
    if (event.type == "focusout")
      event.target.value = APP_BCGOGO.StringFilter.inputtedPriceFilter(event.target.value);
    else if (event.type == "keyup")
      if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value)) {
        event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value);
      }
  });


  $("#totalShopAchievementConfigSpan").live("click", function() {

    $("#searchType").val("shopAchievementConfig");

    var data = {
      includeBasic: false,
      maxRows :25,
      sort : $("#sortStatus").val(),
      startPageNo:1
    }
    var url = "assistantStat.do?method=getProductAchievementByPager";

    bcgogoAjaxQuery.setUrlData(url, data);
    bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
      initTrForSearchAchievement(jsonStr);
      initPage(jsonStr, "_stock_search", url, url, "stockSearchShowResponse", '', '', data, data);
      $("#searchType").val("");
    });
  });


  $("#memberNewAmount").live("blur",
      function (e) {

        if ($("#memberNewAchievement").val() == "按比率") {

          if ($(this).val() > 100 || $(this).val() < 0) {
            alert("会员卡提成比率只能是0到100");
            $(this).val($(this).attr("lastValue"));
            return;
          }

          $(this).val($(this).val().replace("%", "") + "%");

        } else {
          $(this).val($(this).val().replace("%", ""));
        }

        var price = $("#memberNewAmount").val().replace("%", "");
        if (isNaN(price) || price < -0.0001 || price == "") {
          if ($("#memberNewAchievement").val() == "按比率") {
            $(this).val("0%");
          } else {
            $(this).val("0");
          }
        }
        price = $("#memberNewAmount").val().replace("%", "");
        if (!$.trim(price)) price = 0.0;
        price = dataTransition.simpleRounding(price, 2);
        var ajaxUrl = "assistantStat.do?method=updateMemberAchievement";
        var ajaxData = {
          memberNewSelect: $("#memberNewSelect").val(),
          memberNewAchievement: $("#memberNewAchievement").val(),
          memberOrderType:'购卡',
          memberNewAmount: price
        };
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.setAsyncAndCache(false, false);
        bcgogoAjaxQuery.ajaxQuery(function (result) {
        });
        $(this).attr("lastValue", $(this).val());
      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
        $(this).val($(this).val().replace("%", ""));
      });

  $("#memberNewSelect").live("change",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }
        $(this).attr("lastValue", $(this).val());

        if($(this).val() =="按销售量"){
          $("#memberNewAchievement").val("按金额");
        }else{
          $("#memberNewAchievement").val("按比率");
        }

        $("#memberNewAmount").focus();

      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });

  $("#memberRenewAmount").live("blur",
      function (e) {

        if ($("#memberRenewAchievement").val() == "按比率") {
          if ($(this).val() > 100 || $(this).val() < 0) {
            alert("会员卡提成比率只能是0到100");
            $(this).val($(this).attr("lastValue"));
            return;
          }
          $(this).val($(this).val().replace("%", "") + "%");

        } else {
          $(this).val($(this).val().replace("%", ""));
        }


        var price = $("#memberRenewAmount").val().replace("%", "");
        if (isNaN(price) || price < -0.0001 || price == "") {
          if ($("#memberRenewAchievement").val() == "按比率") {
            $(this).val("0%");
          } else {
            $(this).val("0");
          }
        }
        price = $("#memberRenewAmount").val().replace("%", "");
        if (!$.trim(price)) price = 0.0;
        price = dataTransition.simpleRounding(price, 2);
        var ajaxUrl = "assistantStat.do?method=updateMemberAchievement";
        var ajaxData = {
          memberNewSelect: $("#memberRenewSelect").val(),
          memberNewAchievement: $("#memberRenewAchievement").val(),
          memberNewAmount: price,
          memberOrderType:'续卡'
        };
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.setAsyncAndCache(false, false);
        bcgogoAjaxQuery.ajaxQuery(function (result) {
        });
        $(this).attr("lastValue", $(this).val());
      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
        $(this).val($(this).val().replace("%", ""));

      });


  $("#memberRenewSelect").live("change",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }
        $(this).attr("lastValue", $(this).val());

        if ($(this).val() == "按销售量") {
          $("#memberRenewAchievement").val("按金额");
        } else {
          $("#memberRenewAchievement").val("按比率");
        }

        $("#memberRenewAmount").focus();
      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });

  $(".sales_total_ti_chen_input").live("blur",
      function (e) {
        var prefix = $(e.target).attr("id").split(".")[0];

        if ($("#" + prefix + "\\.salesTotalAchievementType").val() == "RATIO") {
          if ($("#" + prefix + "\\.salesTotalAchievementAmount").val() > 100 || $("#" + prefix + "\\.salesTotalAchievementAmount").val() < 0) {
            alert("销售利润提成比率只能是0到100");
            $(this).val($(this).attr("lastValue"));
            return;
          }
          $("#" + prefix + "\\.salesTotalAchievementAmount").val($("#" + prefix + "\\.salesTotalAchievementAmount").val().replace("%", "") + "%");
        } else {
          $("#" + prefix + "\\.salesTotalAchievementAmount").val($("#" + prefix + "\\.salesTotalAchievementAmount").val().replace("%", ""));
        }

        var price = $("#" + prefix + "\\.salesTotalAchievementAmount").val().replace("%", "");
        if (isNaN(price) || price < -0.0001 || price == "") {
          if ($("#" + prefix + "\\.salesTotalAchievementType").val() == "RATIO") {
            $(this).val("0%");
          } else {
            $(this).val("0");
          }
        }
        price = $("#" + prefix + "\\.salesTotalAchievementAmount").val().replace("%", "");
        if (!$.trim(price)) price = 0.0;
        price = dataTransition.simpleRounding(price, 2);
        var ajaxUrl = "assistantStat.do?method=updateProductAchievement";
        var ajaxData = {
          idList: $("#" + prefix + "\\.productLocalInfoId").val(),
          salesTotalAchievementType: $("#" + prefix + "\\.salesTotalAchievementType").val(),
          salesTotalAchievementAmount: price
        };
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.setAsyncAndCache(false, false);
        bcgogoAjaxQuery.ajaxQuery(function (result) {
          if (result) {
            if (result.success) {
              $("#totalShopAchievementConfig").text(result.data);
            } else {
            }
          }
        });
        $(this).attr("lastValue", $(this).val());
      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
        $(this).val($(this).val().replace("%", ""));
      });


  $(".sales_profit_ti_chen_input").live("blur",
      function (e) {
        var prefix = $(e.target).attr("id").split(".")[0];

        if ($("#" + prefix + "\\.salesProfitAchievementType").val() == "RATIO") {

          if ($("#" + prefix + "\\.salesProfitAchievementAmount").val() > 100 || $("#" + prefix + "\\.salesProfitAchievementAmount").val() < 0) {
            alert("销售利润提成比率只能是0到100");
            $(this).val($(this).attr("lastValue"));
            return;
          }

          $("#" + prefix + "\\.salesProfitAchievementAmount").val($("#" + prefix + "\\.salesProfitAchievementAmount").val().replace("%", "") + "%");
        } else {
          $("#" + prefix + "\\.salesProfitAchievementAmount").val($("#" + prefix + "\\.salesProfitAchievementAmount").val().replace("%", ""));
        }

        var price = $("#" + prefix + "\\.salesProfitAchievementAmount").val().replace("%", "");
        if (isNaN(price) || price < -0.0001 || price == "") {
          if ($("#" + prefix + "\\.salesProfitAchievementType").val() == "RATIO") {
            $(this).val("0%");
          } else {
            $(this).val("0");
          }
        }
        price = $("#" + prefix + "\\.salesProfitAchievementAmount").val().replace("%", "");
        if (!$.trim(price)) price = 0.0;
        price = dataTransition.simpleRounding(price, 2);
        var ajaxUrl = "assistantStat.do?method=updateProductProfitAchievement";
        var ajaxData = {
          idList: $("#" + prefix + "\\.productLocalInfoId").val(),
          salesProfitAchievementType: $("#" + prefix + "\\.salesProfitAchievementType").val(),
          salesProfitAchievementAmount: price
        };
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.setAsyncAndCache(false, false);
        bcgogoAjaxQuery.ajaxQuery(function (result) {
          if (result) {
            if (result.success) {
              $("#totalShopAchievementConfig").text(result.data);
            } else {
            }
          }
        });
        $(this).attr("lastValue", $(this).val());
      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
        $(this).val($(this).val().replace("%", ""));
      });

  $(".salesProfitAchievementTypeSelect").live("change",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }
        $(this).attr("lastValue", $(this).val());
        var prefix = $(e.target).attr("id").split(".")[0];
        $("#" + prefix + "\\.salesProfitAchievementAmount").focus();

      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });

  $(".salesTotalAchievementTypeSelect").live("change",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }
        $(this).attr("lastValue", $(this).val());
        var prefix = $(e.target).attr("id").split(".")[0];
        $("#" + prefix + "\\.salesTotalAchievementAmount").focus();

      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });


  $("#batchSetSalesProfit").bind("click", function () {
      $("#batchSetSalesProfit_dialog").dialog({
          title: "设置商品利润提成",
          width: 250,
          modal: true,
          resizable: false,
          draggable: false,
          close: function () {
            $("#salesProfitAchievementType").val("AMOUNT");
            $("#salesProfitAchievementAmount").val("");
          },
          open: function () {
            $("#salesProfitAchievementType").val("AMOUNT");
            $("#salesProfitAchievementAmount").val("");
          }
        })
  });

  $("#batchSetSalesProfitSubmit").bind("click", function () {
    var $thisDom = $(this);
    if ($thisDom.attr("lock")) {
        return;
    }
    $thisDom.attr("lock", true);
    if ($("#salesProfitAchievementAmount").val() == "" || $("#salesProfitAchievementAmount").val() == null) {
      alert("请输入商品利润提成");
      $thisDom.removeAttr("lock");
      return;
    }

    if ($("#salesProfitAchievementType").val() == "RATIO") {
      if ($("#salesProfitAchievementAmount").val() > 100 || $("#salesProfitAchievementAmount").val() < 0) {
        alert("销售提成比率只能是0到100");
        $thisDom.removeAttr("lock");
        return;
      }
      $("#salesProfitAchievementAmount").val($("#salesProfitAchievementAmount").val().replace("%", "") + "%");
    }

    setProductProfitAchievement($("#salesProfitAchievementType").val(), $("#salesProfitAchievementAmount").val());
    $thisDom.removeAttr("lock");

  });

  $("#batchSetSalesProfitCancel").bind("click", function () {
      $("#batchSetSalesProfit_dialog").dialog("close");
    });



  $("#batchSetSalesTotal").bind("click", function () {
      $("#batchSetSalesTotal_dialog").dialog({
          title: "设置商品销售提成",
          width: 250,
          modal: true,
          resizable: false,
          draggable: false,
          close: function () {
            $("#salesTotalAchievementType").val("AMOUNT");
            $("#salesTotalAchievementAmount").val("");
          },
          open: function () {
            $("#salesTotalAchievementType").val("AMOUNT");
            $("#salesTotalAchievementAmount").val("");
          }
        })
  });

  $("#batchSetSalesTotalSubmit").bind("click", function () {

    var $thisDom = $(this);
    if ($thisDom.attr("lock")) {
        return;
    }
    $thisDom.attr("lock", true);


    if ($("#salesTotalAchievementAmount").val() == "" || $("#salesTotalAchievementAmount").val() == null) {
      alert("请输入商品销售提成");
      $thisDom.removeAttr("lock");
      return;
    }

    if ($("#salesTotalAchievementType").val() == "RATIO") {
      if ($("#salesTotalAchievementAmount").val() > 100 || $("#salesTotalAchievementAmount").val() < 0) {
        alert("销售利润提成比率只能是0到100");
        $thisDom.removeAttr("lock");
        return;
      }
      $("#salesTotalAchievementAmount").val($("#salesTotalAchievementAmount").val().replace("%", "") + "%");
    }

    setProductAchievement($("#salesTotalAchievementType").val(), $("#salesTotalAchievementAmount").val());
    $thisDom.removeAttr("lock");
  });

  $("#batchSetSalesTotalCancel").bind("click", function () {
      $("#batchSetSalesTotal_dialog").dialog("close");
    });

  if ($("#unConfig").val() == "unConfig") {
    $("#totalShopAchievementConfigSpan").click();
  }
});