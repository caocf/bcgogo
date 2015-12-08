/**
 * 我的供应商列表专用js
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-4-23
 * Time: 下午6:48
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(function() {


  $(".J_supplier_sort").bind("mouseover",
      function () {
        $(this).find(".alert_develop").show();
      }).bind("mouseout",
      function () {
        $(this).find(".alert_develop").hide();
      }).bind("click", function () {
        $(".J_supplier_sort").each(function() {
          $(this).removeClass("hover");
        });
        $(this).addClass("hover");
        var currentSortStatus = $(this).attr("currentSortStatus");
        if (currentSortStatus == "Desc") {
          $(this).attr("currentSortStatus", "Asc");
          $(this).find(".alertBody").html($(this).attr("descContact"));
        } else {
          $(this).attr("currentSortStatus", "Desc");
          $(this).find(".alertBody").html($(this).attr("ascContact"));
        }
      });

  $(".J-initialCss").placeHolder();

  $(".J-productSuggestion")
      .bind('click', function () {
        productSuggestion($(this));
      })
      .bind('keyup', function (event) {
        var eventKeyCode = event.which || event.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
          productSuggestion($(this));
        }
      });

  function productSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var dropList = APP_BCGOGO.Module.droplist;
    dropList.setUUID(GLOBAL.Util.generateUUID());
    var currentSearchField = $domObject.attr("searchField");
    var ajaxData = {
      searchWord: searchWord,
      searchField: currentSearchField,
      uuid: dropList.getUUID()
    };
    $domObject.prevAll(".J-productSuggestion").each(function () {
      var val = $(this).val().replace(/[\ |\\]/g, "");
      if ($(this).attr("name") != "searchWord") {
        ajaxData[$(this).attr("name")] = val == $(this).attr("initialValue") ? "" : val;
      }
    });

    var ajaxUrl = "product.do?method=getProductSuggestion";
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
      if (currentSearchField == "product_info") {
        dropList.show({
          "selector": $domObject,
          "autoSet": false,
          "data": result,
          onGetInputtingData: function() {
            var details = {};
            $domObject.nextAll(".J-productSuggestion").each(function () {
              var val = $(this).val().replace(/[\ |\\]/g, "");
              details[$(this).attr("searchField")] = val == $(this).attr("initialValue") ? "" : val;
            });
            return {
              details:details
            };
          },
          onSelect: function (event, index, data, hook) {
            $domObject.nextAll(".J-productSuggestion").each(function () {
              var label = data.details[$(this).attr("searchField")];
              if (G.Lang.isEmpty(label) && $(this).attr("initialValue")) {
                $(this).val($(this).attr("initialValue"));
                $(this).css({"color": "#ADADAD"});
              } else {
                $(this).val(G.Lang.normalize(label));
                $(this).css({"color": "#000000"});
              }
            });
            dropList.hide();
            // 页面搜索的回调钩子
            doCustomerOrSupplierProductSearch();
          },
          onKeyboardSelect: function (event, index, data, hook) {
            $domObject.nextAll(".J-productSuggestion").each(function () {
              var label = data.details[$(this).attr("searchField")];
              if (G.Lang.isEmpty(label) && $(this).attr("initialValue")) {
                $(this).val($(this).attr("initialValue"));
                $(this).css({"color": "#ADADAD"});
              } else {
                $(this).val(G.Lang.normalize(label));
                $(this).css({"color": "#000000"});
              }
            });
          }
        });
      } else {
        dropList.show({
          "selector": $domObject,
          "data": result,
          "onSelect": function (event, index, data) {
            $domObject.val(data.label);
            $domObject.css({"color": "#000000"});
            $domObject.nextAll(".J-productSuggestion").each(function () {
              clearSearchInputValueAndChangeCss(this);
            });
            dropList.hide();
            // 各个页面search的回调
            doCustomerOrSupplierProductSearch();
          }
        });
      }

    });
  }

  $("#totalTradeAmountStart,#totalTradeAmountEnd,#totalReceivableStart,#totalReceivableEnd,#debtAmountStart,#debtAmountEnd").live("keyup blur", function(event) {
    if (event.type == "focusout")
      event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
    else if (event.type == "keyup")
      if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
        event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
      }
  });

  $(document).click(function (event) {
    var target = event.target;
    if (!target || !target.id || (target.type != "text")) {
      $("#totalTradeAmountDiv").css("display", "none");
      $("#debtAmountDiv").css("display", "none");
      $("#totalReceivableDiv").css("display", "none");
      $(".txtTransaction").css("z-index", "0");

      var idStr = target.id;
      if (idStr && idStr.length > 0) {
        var temp = idStr.split("_");
        if (temp[1] && temp[1].length > 0 && temp[1] == "connector") {
        } else {
          $(".prompt").css("display", "none");
        }
      } else {
        $(".prompt").css("display", "none");
      }
    }

    var targetId = target.id;
    if( targetId == "startDate" || targetId == "endDate"){
      $("#totalTradeAmountDiv").css("display", "none");
      $("#debtAmountDiv").css("display", "none");
      $("#totalReceivableDiv").css("display", "none");
      $(".txtTransaction").css("z-index", "0");
    }
  })



  provinceBind();
  $("#provinceNo").bind("change", function() {
    $("#cityNo option").not(".default").remove();
    $("#regionNo option").not(".default").remove();
    cityBind();
    if ($(this).val() == "--所有省--") {
      $(this).css({"color": "#ADADAD"});
    }else{
      $(this).css({"color": "#000000"});
    }
    if ($("#customerSearchBtn").length > 0) {
      $("#customerSearchBtn").click();
    }
    if ($("#supplierSearchBtn").length > 0) {
      $("#supplierSearchBtn").click();
    }

  });
  $("#cityNo").bind("change", function() {
    $("#regionNo option").not(".default").remove();
    regionBind();

    if ($(this).val() == "--所有市--") {
      $(this).css({"color": "#ADADAD"});
    }else{
      $(this).css({"color": "#000000"});
    }
    if ($("#customerSearchBtn").length > 0) {
      $("#customerSearchBtn").click();
    }
    if ($("#supplierSearchBtn").length > 0) {
      $("#supplierSearchBtn").click();
    }

  });

  $("#regionNo").bind("change", function() {
    if ($(this).val() == "--所有区--") {
      $(this).css({"color": "#ADADAD"});
    }else{
      $(this).css({"color": "#000000"});
    }
    if ($("#customerSearchBtn").length > 0) {
      $("#customerSearchBtn").click();
    }
    if ($("#supplierSearchBtn").length > 0) {
      $("#supplierSearchBtn").click();
    }
  });



  $("input[name='customerOrSupplierType']").bind("click", function() {
    $("#relationType").val($(this).val());
  });

  $("#date_self_define").click();


  $("#startDate,#endDate")
      .datepicker({
        "numberOfMonths":1,
        "showButtonPanel":false,
        "changeYear":true,
        "showHour":false,
        "showMinute":false,
        "changeMonth":true,
        "yearRange":"c-100:c+100",
        "yearSuffix":""
      })
      .blur(function() {
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();
        if (startDate == "" || endDate == "") return;
        if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
          return;
        } else {
          if (startDate > endDate) {
            $("#endDate").val(startDate);
            $("#startDate").val(endDate);
          }
        }
      })
      .bind("click", function() {
        $(".txtTransaction").css("z-index", "0");
        $(this).blur();
      })
      .change(function() {
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();
        $(".good_his > .today_list").removeClass("hoverList");
        if (endDate == "" || startDate == "") {
          return;
        }
        if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
          return;
        } else {
          if (startDate > endDate) {
            $("#endDate").val(startDate);
            $("#startDate").val(endDate);
          }
        }
      });


  $("#totalTradeAmountStart,#totalTradeAmountEnd").click(function(e) {
    $("#totalTradeAmountDiv").css("display", "block");
    $("#debtAmountDiv").css("display", "none");
    $("#totalReceivableDiv").css("display", "none");
    $(".txtTransaction").css("z-index", "2");
  });

  $("#totalTradeAmountClear").click(function(e) {
    $("#totalTradeAmountStart").val("");
    $("#totalTradeAmountEnd").val("");
    searchBtnClick();
  });

  $("#totalTradeAmountSure").click(function(e) {

    var start ,end;
    start = $("#totalTradeAmountStart").val();
    end = $("#totalTradeAmountEnd").val();
    if ((!start && !end) || (start.length == 0 && end.length == 0)) {
      alert("请输入累计交易金额");
      return;
    }
    if ((start && end) && (start.length != 0 && end.length != 0) && Number(start) > Number(end)) {
      $("#totalTradeAmountStart").val(end);
      $("#totalTradeAmountEnd").val(start);
    }

    $("#totalTradeAmountDiv").css("display", "none");
    searchBtnClick();
  });

  $("#totalTradeListNum>span").click(function(e) {
    var idStr = $(this).attr("id");
    if (idStr == "totalTradeAmount_1") {
      $("#totalTradeAmountStart").val("");
      $("#totalTradeAmountEnd").val(1000);
    } else if (idStr == "totalTradeAmount_2") {
      $("#totalTradeAmountStart").val(1000);
      $("#totalTradeAmountEnd").val(5000);
    } else if (idStr == "totalTradeAmount_3") {
      $("#totalTradeAmountStart").val(5000);
      $("#totalTradeAmountEnd").val(10000);
    } else if (idStr == "totalTradeAmount_4") {
      $("#totalTradeAmountStart").val(10000);
      $("#totalTradeAmountEnd").val("");
    }
    searchBtnClick();
  });


  $("#debtAmountStart,#debtAmountEnd").click(function(e) {
    $("#debtAmountDiv").css("display", "block");
    $("#totalTradeAmountDiv").css("display", "none");
    $("#totalReceivableDiv").css("display", "none");
    $(".txtTransaction").css("z-index", "2");
  });

  $("#debtAmountClear").click(function(e) {
    $("#debtAmountStart").val("");
    $("#debtAmountEnd").val("");
    searchBtnClick();
  });

  $("#debtAmountSure").click(function(e) {

    var start ,end;
    start = $("#debtAmountStart").val();
    end = $("#debtAmountEnd").val();
    if ((!start && !end) || (start.length == 0 && end.length == 0)) {
      alert("请输入应付金额");
      return;
    }
    if ((start && end) && (start.length != 0 && end.length != 0) && Number(start) > Number(end)) {
      $("#debtAmountStart").val(end);
      $("#debtAmountEnd").val(start);
    }

    $("#debtAmountDiv").css("display", "none");
    searchBtnClick();
  });

  $("#debtAmountListNum>span").click(function(e) {
    var idStr = $(this).attr("id");
    if (idStr == "debtAmount_1") {
      $("#debtAmountStart").val("");
      $("#debtAmountEnd").val(1000);
    } else if (idStr == "debtAmount_2") {
      $("#debtAmountStart").val(1000);
      $("#debtAmountEnd").val(5000);
    } else if (idStr == "debtAmount_3") {
      $("#debtAmountStart").val(5000);
      $("#debtAmountEnd").val(10000);
    } else if (idStr == "debtAmount_4") {
      $("#debtAmountStart").val(10000);
      $("#debtAmountEnd").val("");
    }
    searchBtnClick();
  });


  $("#totalReceivableStart,#totalReceivableEnd").click(function(e) {
    $("#totalReceivableDiv").css("display", "block");
    $("#debtAmountDiv").css("display", "none");
    $("#totalTradeAmountDiv").css("display", "none");
    $(".txtTransaction").css("z-index", "2");
  });

  $("#totalReceivableClear").click(function(e) {
    $("#totalReceivableStart").val("");
    $("#totalReceivableEnd").val("");
    searchBtnClick();
  });

  $("#totalReceivableSure").click(function(e) {


    var start ,end;
    start = $("#totalReceivableStart").val();
    end = $("#totalReceivableEnd").val();
    if ((!start && !end) || (start.length == 0 && end.length == 0)) {
      alert("请输入应收金额");
      return;
    }
    if ((start && end) && (start.length != 0 && end.length != 0) && Number(start) > Number(end)) {
      $("#totalReceivableStart").val(end);
      $("#totalReceivableEnd").val(start);
    }

    $("#totalReceivableDiv").css("display", "none");
    searchBtnClick();
  });

  $("#totalReceivableListNum>span").click(function(e) {
    var idStr = $(this).attr("id");
    if (idStr == "totalReceivable_1") {
      $("#totalReceivableStart").val("");
      $("#totalReceivableEnd").val(1000);
    } else if (idStr == "totalReceivable_2") {
      $("#totalReceivableStart").val(1000);
      $("#totalReceivableEnd").val(5000);
    } else if (idStr == "totalReceivable_3") {
      $("#totalReceivableStart").val(5000);
      $("#totalReceivableEnd").val(10000);
    } else if (idStr == "totalReceivable_4") {
      $("#totalReceivableStart").val(10000);
      $("#totalReceivableEnd").val("");
    }
    searchBtnClick();
  });


  $("#depositSort").click(function(e) {

    var sortStr = "";
    if ($("#depositSortSpan").hasClass("arrowDown")) {
      $("#depositSortSpan").addClass("arrowUp").removeClass("arrowDown");
      sortStr = " total_deposit asc ";
    } else {
      $("#depositSortSpan").addClass("arrowDown").removeClass("arrowUp");
      sortStr = " total_deposit desc ";
    }
    $("#sortStatus").val(sortStr);
    searchBtnClick();


  });


  $("#createdTimeSort").click(function(e) {
    var sortStr = "";
    if ($("#createdTimeSortSpan").hasClass("arrowDown")) {
      $("#createdTimeSortSpan").addClass("arrowUp").removeClass("arrowDown");
      sortStr = " created_time asc ";
    } else {
      $("#createdTimeSortSpan").addClass("arrowDown").removeClass("arrowUp");
      sortStr = " created_time desc ";
    }
    $("#sortStatus").val(sortStr);
    searchBtnClick();

  });


  $("#supplierInfoText").bind("change", function() {
    $("#supplierId").val("");
  });
});


function showProductThroughDetail(id, type) {
  var url = "productThrough.do?method=redirectProductThroughDetail&"
  if (type == "customer") {
    url += "customerId=" + id;
  } else if (type == "supplier") {
    url += "supplierId=" + id;
  } else if (type == "product") {
    url += "productId=" + id;
  } else {
    return;
  }
  window.open(url);
}


function cityBind() {
  var r = APP_BCGOGO.Net.asyncGet({url: "shop.do?method=selectarea",
    data: {"parentNo": $("#provinceNo").val()}, dataType: "json"});
  if (!r || r.length == 0) return;
  else {
    for (var i = 0, l = r.length; i < l; i++) {
      var option = $("<option>")[0];
      option.value = r[i].no;
      option.innerHTML = r[i].name;
      option.style.color = "#000000";
      $("#cityNo")[0].appendChild(option);
    }
  }
}

function regionBind() {
  var r = APP_BCGOGO.Net.asyncGet({url: "shop.do?method=selectarea",
    data: {"parentNo": $("#cityNo").val()}, dataType: "json"});
  if (!r || r.length == 0) return;
  else {
    for (var i = 0, l = r.length; i < l; i++) {
      var option = $("<option>")[0];
      option.value = r[i].no;
      option.innerHTML = r[i].name;
      option.style.color = "#000000";
      $("#regionNo")[0].appendChild(option);
    }
  }
}
function provinceBind() {
    if($("#provinceNo")[0]){
        var r = APP_BCGOGO.Net.asyncGet({url: "shop.do?method=selectarea",
            data: {"parentNo":"1"}, dataType: "json"});
        if (!r || r.length == 0) return;
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                option.style.color = "#000000";
                if ($("#provinceNo")[0]) {
                    $("#provinceNo")[0].appendChild(option);
                }
            }
        }
    }

}