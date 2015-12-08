$(document).ready(function () {
  $("#memberNewType").live("change",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }
        $(this).attr("lastValue", $(this).val());

        if (stringUtil.isEmpty($("#memberNewAchievement").val())) {
          return;
        }

        if ($(this).val() == "CARD_AMOUNT") {
           $("#memberNewAchievement").val($("#memberNewAchievement").val().replace("%", ""));
         } else if ($(this).val() == "CARD_TOTAL") {
          if (G.isNotEmpty($("#memberNewAchievement").val())) {
            $("#memberNewAchievement").val($("#memberNewAchievement").val().replace("%", "") + "%");
          }
         }

        if (stringUtil.isNotEmpty($("#memberReNewAchievement").val())) {
          return;
        }

        $("#memberReNewAchievement").val($("#memberNewAchievement").val());
        $("#memberRenewType").val($("#memberNewType").val());

      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });

  $("#memberNewAchievement").live('blur',function (e) {
    if (e.target.value) {
      if (stringUtil.isEmpty($("#memberNewAchievement").val())) {
        return;
      }

      if ($("#memberNewType").val() == "CARD_AMOUNT") {
        $(this).val($(this).val().replace("%", ""));
      } else if ($("#memberNewType").val() == "CARD_TOTAL") {
        $(this).val($(this).val().replace("%", "") + "%");
      }

      if (stringUtil.isNotEmpty($("#memberReNewAchievement").val())) {
        return;
      }

      $("#memberReNewAchievement").val($("#memberNewAchievement").val());
      $("#memberRenewType").val($("#memberNewType").val());
    }

  }).live("focus", function () {
        $(this).val($(this).val().replace("%", ""));
      });


  $("#serviceAchievement,#salesAchievement,#salesProfitAchievement").live("blur",
      function (e) {

        if (G.isEmpty($(this).val())) {
          return;
        }
        $(this).val($(this).val().replace("%", "") + "%");
        $(this).attr("lastValue", $(this).val());
      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
        $(this).val($(this).val().replace("%", ""));
      });;

  $("#memberReNewAchievement").live("blur",
      function (e) {

        if (G.isEmpty($(this).val())) {
          return;
        }
        if ($("#memberRenewType").val() == "CARD_AMOUNT") {
          $(this).val($(this).val().replace("%", ""));
        } else if ($("#memberRenewType").val() == "CARD_TOTAL") {
          $(this).val($(this).val().replace("%", "") + "%");
        }
      }).live("focus", function () {
        $(this).val($(this).val().replace("%", ""));
      });


  $("#memberRenewType").live("change",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }
        $(this).attr("lastValue", $(this).val());

        if ($(this).val() == "CARD_AMOUNT") {
          $("#memberReNewAchievement").val($("#memberReNewAchievement").val().replace("%", ""));
        } else if ($(this).val() == "CARD_TOTAL") {
          if(G.isNotEmpty($("#memberReNewAchievement").val())){
            $("#memberReNewAchievement").val($("#memberReNewAchievement").val().replace("%", "") + "%");
          }
        }

      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });

  $("#submitBtn").bind("click",function(){
    if ($("#serviceAchievement").length > 0) {
      $("#serviceAchievement").val($("#serviceAchievement").val().replace("%", ""));
    }
    if ($("#salesAchievement").length > 0) {
      $("#salesAchievement").val($("#salesAchievement").val().replace("%", ""));
    }
    if ($("#salesProfitAchievement").length > 0) {
      $("#salesProfitAchievement").val($("#salesProfitAchievement").val().replace("%", ""));
    }

    if ($("#memberNewAchievement").length > 0) {
      $("#memberNewAchievement").val($("#memberNewAchievement").val().replace("%", ""));
    }
    if ($("#memberReNewAchievement").length > 0) {
      $("#memberReNewAchievement").val($("#memberReNewAchievement").val().replace("%", ""));
    }
     $("#salesManForm").submit();
  });

});
