var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$(document).ready(function() {


  if ($("#businessScopeDiv").length > 0) {
    var ajaxUrl = "businessScope.do?method=getAllBusinessScope";
    var ajaxData = {
    };
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
      initBusinessScope(json);
    });

    $(".secondCategory").live("click", function(event) {
      var secondId = $(this).attr("value");
      showThirdCategory(secondId, this);
    });

    $(".select_close").live("click", function() {
      var id = $(this).parent().parent().attr("id");
      $("#" + id).remove();
      var idPrefix = id.split("_")[0];
      var second = $("#" + idPrefix);
      if (second.hasClass("secondThird")) {
        $("#" + idPrefix).parent().find('a').removeClass("hovered");
        $("#" + idPrefix).parent().find('a').removeClass("thirdCategoryClicked");
        $("#" + idPrefix + "_label").removeClass('clicked');
      } else if (second.hasClass("thirdCategory")) {
        $("#" + idPrefix).removeClass("thirdCategoryClicked");
        var size = $("#" + idPrefix).parent().find('.thirdCategoryClicked').length;

        var secondId = $("#" + idPrefix).parent().find('a').eq(0).attr("id");

        if ($("#" + idPrefix).parent().find('a').length == 1) {
          secondId = $("#" + idPrefix).parent().attr("id").split("_")[0];
        }

        if (size > 0) {
          if (!$("#" + secondId + "_label").hasClass("clicked")) {
            $("#" + secondId + "_label").addClass('clicked');
          }
        } else {
          $("#" + secondId + "_label").removeClass('clicked');
        }
      }
      validateShopBusinessScope();
    });
  }
});

$(document).bind('click', function(event) {
  var target = event.target;
  if ($(target).hasClass("secondCategory") || $(target).hasClass("thirdCategory") || $(target).hasClass("secondThird")) {
    return;
  }
  hideThirdCategory();
});

function initBusinessScope(json) {
  var secondDivWith = ($("#businessScopeDiv").width()>735?$("#businessScopeDiv").width()-33:735);
  if (json == null || json == undefined) {
    return;
  }
  var firstChildren = json.children;
  var tr = '';
  tr += '';

  var thirdCategoryIdStr ="";
  if ($("#thirdCategoryIdStr").length > 0 && stringUtil.isNotEmpty($("#thirdCategoryIdStr").val())) {
    thirdCategoryIdStr = $("#thirdCategoryIdStr").val();
  }


  for (var i = 0; i < firstChildren.length; i++) {
    var firstNode = firstChildren[i];
    var secondChildren = firstNode.children;
    for (var j = 0; j < secondChildren.length; j++) {
      var secondNode = secondChildren[j];
      var secondName = secondNode.text;
      var secondId = secondNode.idStr;

      tr += '<label id="' + secondId + '_label" value="' + secondId + '" class="list secondCategory">' + secondName + '</label>';
      var thirdChildren = secondNode.children;
      tr += '<div id="' + secondId + '_div" style="width:'+secondDivWith+'px;display:none;"  class="hide_content">';

      var thirdChildrenLength = thirdChildren.length;
      if (thirdChildrenLength > 1) {
        tr += '<a onclick="secondCategoryClick(this)" id="' + secondId + '" value="' + secondName + '" class="secondThird">' + secondName + '</a>';
      }

      for (var k = 0; k < thirdChildren.length; k++) {
        var thirdNode = thirdChildren[k];
        var thirdName = thirdNode.text;
        var thirdId = thirdNode.idStr;

        if (thirdCategoryIdStr.indexOf(thirdId) == -1) {
          tr += '<a onclick="thirdCategoryClick(this)" id="' + thirdId + '" value="' + thirdName + '" class="thirdCategory">' + thirdName + '</a>';
        } else {
          tr += '<a onclick="thirdCategoryClick(this)" id="' + thirdId + '" value="' + thirdName + '" class="thirdCategory thirdCategoryClicked">' + thirdName + '</a>';
        }
      }

      tr += "</div>";
    }
  }
  $("#businessScopeDiv").html(tr);

  if (!G.Lang.isEmpty(thirdCategoryIdStr)) {
    var thirdCategoryIdArray = $("#thirdCategoryIdStr").val().split(",");
    if (thirdCategoryIdArray.length > 0) {

      $("#businessScopeSelected").html("");
      var str = "";

      $.each(thirdCategoryIdArray, function (index, value) {
        if (stringUtil.isNotEmpty(value)) {
          var obj = "#" + value;
          var size = $(obj).parent().find("a").size();
          var secondIdStr = $(obj).parent().attr("id").split("_")[0];
          $("#" + secondIdStr + "_label").addClass('clicked');

          if (str.indexOf(secondIdStr) == -1) {
            var length = $(obj).parent().find('.thirdCategoryClicked').length;

            if (size == 1 || (length != size - 1)) {
              var thirdName = $(obj).attr("value");
              str += '<div id="' + value + '_selectDiv" class="select">' +
                  '<span class="select_left"></span>' +
                  '<span class="select_body">' + thirdName + '<a class="select_close"></a></span><span class="select_right"></span></div>';
            } else {
              var secondValue = $("#" + secondIdStr + "_label").text();
              $("#" + secondIdStr).addClass("hovered");
              str += '<div id="' + secondIdStr + '_selectDiv" class="select">' +
                  '<span class="select_left"></span>' +
                  '<span class="select_body">' + secondValue + '<a class="select_close"></a></span><span class="select_right"></span></div>';
            }
          }

        }
      });

      $("#businessScopeSelected").html(str);

    }
  }

}


function secondCategoryClick(obj) {
  var thisId = $(obj).attr("id");
  var value = $(obj).attr("value");

  var length = $(obj).parent().find('a').length;


  if (!$(obj).hasClass("hovered")) {
    $(obj).addClass("hovered");
    $(obj).nextAll().addClass('thirdCategoryClicked');
    $("#" + thisId + "_label").addClass('clicked');

    for (var index = 1; index < length; index++) {
      var thirdId = $(obj).parent().find('a').eq(index).attr("id");
      $("#" + thirdId + "_selectDiv").remove();
    }

    if ($("#" + thisId + "_selectDiv").length == 0) {
      var str = '<div id="' + thisId + '_selectDiv" class="select">' +
          '<span class="select_left"></span>' +
          '<span class="select_body">' + value + '<a class="select_close"></a></span><span class="select_right"></span></div>';
      var selectedHtml = $("#businessScopeSelected").html();
      selectedHtml += str;
      $("#businessScopeSelected").html(selectedHtml);
    }

  } else {
    $(obj).removeClass("hovered");
    $(obj).nextAll().removeClass('thirdCategoryClicked');
    $("#" + thisId + "_label").removeClass('clicked');


    for (var index = 1; index < length; index++) {
      var thirdId = $(obj).parent().find('a').eq(index).attr("id");
      $("#" + thirdId + "_selectDiv").remove();
    }

    if ($("#" + thisId + "_selectDiv").length > 0) {
      $("#" + thisId + "_selectDiv").remove();
    }
  }
  validateShopBusinessScope();
}

function validateShopBusinessScope() {
//  if ($("#businessScopeSelected").find("div").size() < 1) {
//    nsDialog.jAlert("请选择经营产品");
//  }
}


function resetThirdCategoryIdStr() {
  var thirdCategoryIdStr = "";
  var secondCategoryName = "";
  $(".thirdCategoryClicked").each(function () {
    if ($(this).hasClass("thirdCategory")) {
      var id = $(this).attr("id");
      thirdCategoryIdStr += id + ",";

      var secondName = $("#" + id).parent().find('a').eq(0).attr("value");
      if (secondCategoryName.indexOf(secondName) == -1) {
        secondCategoryName += $("#" + id).parent().find('a').eq(0).attr("value") + ",";
      }
    }
  });
  if(!G.Lang.isEmpty(thirdCategoryIdStr)){
      thirdCategoryIdStr = thirdCategoryIdStr.substring(0,thirdCategoryIdStr.length-1);
  }
  $("#thirdCategoryIdStr").val(thirdCategoryIdStr);

  /**
   * 新增客户页面
   */
  if ($("#secondCategoryName").length > 0) {

    if(secondCategoryName.length > 0){
     secondCategoryName = secondCategoryName.substring(0,secondCategoryName.length - 1);
    }
    $("#secondCategoryName").val(secondCategoryName);
  }

}

function resetCheckedClassByThirdCategoryIdStr(thirdCategoryIdStr) {
    if (!G.Lang.isEmpty(thirdCategoryIdStr)) {
        var thirdCategoryIdStrs = thirdCategoryIdStr.split(",");
        $.each(thirdCategoryIdStrs, function (index, value) {
            if(index && $("#"+index)[0]){
                thirdCategoryClick($("#"+index)[0]);
            }
        });
    }
}

function thirdCategoryClick(obj) {
  var thisId = $(obj).attr("id");
  var value = $(obj).attr("value");
  var size = $(obj).parent().find("a").size();

  var secondIdStr = $(obj).parent().find('a').eq(0).attr("id");
  var secondValue = $(obj).parent().find('a').eq(0).attr("value");

  if ($(obj).parent().find('a').length == 1) {
    secondIdStr = $(obj).parent().attr("id").split("_")[0];
    secondValue = $("#" + secondIdStr + "_label").text();

  }

  var selectedHtml = "";


  if (!$(obj).hasClass("thirdCategoryClicked")) {
    $(obj).addClass("thirdCategoryClicked");
    if (!$("#" + secondIdStr + "_label").hasClass("clicked")) {
      $("#" + secondIdStr + "_label").addClass('clicked');
    }

    //找客户找供应商页面
    if($("#pageType").val()=="applyCustomerList" ||  $("#pageType").val()=="applySupplierList"){
      showThirdCategory(secondIdStr, $("#" + secondIdStr +"_label"));
    }

    var length = $(obj).parent().find('.thirdCategoryClicked').length;
    if (size > 1) {
      if (length == size - 1) {

        if (!$(obj).parent().find('a').eq(0).hasClass("hovered")) {
          $(obj).parent().find('a').eq(0).addClass("hovered");
        }

        var divLength  = size;
        for (var index = 1; index < divLength; index++) {
          var thirdId = $(obj).parent().find('a').eq(index).attr("id");
          $("#" + thirdId + "_selectDiv").remove();
        }
        if ($("#" + secondIdStr + "_selectDiv").length == 0) {
          var str = '<div id="' + secondIdStr + '_selectDiv" class="select">' +
              '<span class="select_left"></span>' +
              '<span class="select_body">' + secondValue + '<a class="select_close"></a></span><span class="select_right"></span></div>';
          selectedHtml = $("#businessScopeSelected").html();
          selectedHtml += str;
          $("#businessScopeSelected").html(selectedHtml);

        }
      } else {
        var str = '<div id="' + thisId + '_selectDiv" class="select">' +
            '<span class="select_left"></span>' +
            '<span class="select_body">' + value + '<a class="select_close"></a></span><span class="select_right"></span></div>';
        selectedHtml = $("#businessScopeSelected").html();
        selectedHtml += str;
        $("#businessScopeSelected").html(selectedHtml);
      }
    } else {
      var str = '<div id="' + thisId + '_selectDiv" class="select">' +
          '<span class="select_left"></span>' +
          '<span class="select_body">' + value + '<a class="select_close"></a></span><span class="select_right"></span></div>';
      selectedHtml = $("#businessScopeSelected").html();
      selectedHtml += str;
      $("#businessScopeSelected").html(selectedHtml);
    }
  } else {
    $(obj).removeClass("thirdCategoryClicked");

    if (size > 1) {
      $(obj).parent().find('a').eq(0).removeClass("hovered");
      if ($("#" + secondIdStr + "_selectDiv").length > 0) {
        $("#" + secondIdStr + "_selectDiv").remove();
      }

    }
    if ($("#" + thisId + "_selectDiv").length > 0) {
      $("#" + thisId + "_selectDiv").remove();
    }
    var clickedSize = $(obj).parent().find('.thirdCategoryClicked').length;
    if (clickedSize > 0) {
      if (!$("#" + secondIdStr + "_label").hasClass("clicked")) {
        $("#" + secondIdStr + "_label").addClass('clicked');
      }
    } else {
      $("#" + secondIdStr + "_label").removeClass('clicked');
    }

    var htmlStr = "";
    for (var index = 0; index < size; index++) {

      if ($(obj).parent().find('a').eq(index).hasClass("thirdCategoryClicked")) {
        var thirdId = $(obj).parent().find('a').eq(index).attr("id");
        $("#" + thirdId + "_selectDiv").remove();
        var thirdValue = $(obj).parent().find('a').eq(index).attr("value");
        htmlStr += '<div id="' + thirdId + '_selectDiv" class="select">' +
            '<span class="select_left"></span>' +
            '<span class="select_body">' + thirdValue + '<a class="select_close"></a></span><span class="select_right"></span></div>';
      }
    }
    selectedHtml = $("#businessScopeSelected").html();
    selectedHtml += htmlStr;
    $("#businessScopeSelected").html(selectedHtml);
  }
  validateShopBusinessScope();
}

function showThirdCategory(id, obj) {
  $(".secondCategory").not($(this)).removeClass("hover");
  if (!$(obj).hasClass("hover")) {
    $(obj).addClass("hover");
  }

  $(".hide_content").css("display", "none");
  $("#" + id + "_div").css("display", "block");
}

function hideThirdCategory() {
  $(".hide_content").css("display", "none");
  $(".secondCategory").removeClass("hover");
}
