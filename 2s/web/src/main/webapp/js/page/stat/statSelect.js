$(function() {
  tableUtil.tableStyle('#tabList','.table_title');
  $(".tabList tr").click(function() {
    $("#tabList").css({
      "display": "none"
    });
    $(".i_pageBtn").css({
      "display": "none"
    });
    $(".tabInfo").css({
      "display": "block"
    });
  })
  $(".tabClose").click(function() {
    $("#tabList").css({
      "display": ""
    });
    $(".i_pageBtn").css({
      "display": "block"
    });
    $(".tabInfo").css({
      "display": "none"
    });
  })

  $("#year").click(function() {
    $("#div_years").css({
      width: $("#year").width()
    });

    if($("#div_years").css("display") == 'none') {
      $("#div_years").show();
    } else {
      $("#div_years").hide();
    }
    $("#div_years a").click(function() {
      var text = $(this).text();
      $("#year span").text(text).end();
      $("#div_years").hide();
      $("#yearHidden").val(text.substring(0, 4));
      $("#queryPeriodStr").val(text);
    });
  });
  $("#month").click(function() {
    $("#div_months").css({
      width: $("#month").width()
    });
    if($("#div_months").css("display") == 'none') {
      $("#div_months").show();
    } else {
      $("#div_months").hide();
    }
    $("#div_months a").click(function() {
      var text = $(this).text();
      $("#month span").text(text).end();
      $("#div_months").hide();
      $("#monthHidden").val(text);
    });
  })
  $("#way").click(function() {
    $("#div_way").css({
      width: $("#way").width()
    });
    if($("#div_way").css("display") == 'none') {
      $("#div_way").show();
    } else {
      $("#div_way").hide();
    }
    $("#div_way a").click(function() {
      var text = $(this).text();
      $("#way span").text(text).end();
      $("#div_way").hide();
      $("#queryWay").val(text);
    });
  });
  $("#day").click(function() {
    $("#div_day").css({
      "display": "block"
    });
    $("#div_day").css({
      width: $(this).width()
    });
    $("#div_day a").click(function() {
      var text = $(this).text();
      $("#day span").text(text).end();
      $("#div_day").hide();
    })
  })
  $("#pan").click(function() {
    $("#div_pan").toggle();
    $("#div_pan").css({
      width: $(this).width()
    });
    $("#div_pan a").click(function() {
      var text = $(this).text();
      $("#pan span").text(text).end();
      $("#div_pan").hide();
    })
  })
  $(document).click(function(e) {
    if(e.target.id != "month" && $("#month .selImg")[0] != e.target && $("#month span")[0] != e.target) $("#div_months").hide();
    if(e.target.id != "year" && $("#year .selImg")[0] != e.target && $("#year span")[0] != e.target) $("#div_years").hide();
    if(e.target.id != "way" && $("#way .selImg")[0] != e.target && $("#way span")[0] != e.target) {
      $("#div_way").hide();
    }
  });

  if(("undefined" != typeof year) && ("undefined" != typeof allYear) && ("undefined" != typeof month)) {
    $("#year span").text(year + "年").end();
    if(allYear) {
      $("#month span").text("所有月份").end();
    } else {
      $("#month span").text(month + "月").end();
    }
  }
})