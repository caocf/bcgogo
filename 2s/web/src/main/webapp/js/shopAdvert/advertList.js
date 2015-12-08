/**
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 14-4-17
 * Time: 下午4:46
 * To change this template use File | Settings | File Templates.
 */




$(document).ready(function () {

//
//  tr += '<td>' + '<a data-id="' + orderId + '" class="blue_color previewAdvert">预览</a>';
//
//  if (status == "OVERDUE" || status == "REPEALED") {
//    tr += '<a class="blue_color copyAdvert">复制</a>';
//
//  } else if (status == "WAIT_PUBLISH") {
//    tr += '<a data-id="' + orderId + '" class="blue_color publishAdvert">发布</a>';
//    tr += '<a data-id="' + orderId + '" class="blue_color editAdvert">编辑</a>';
//    tr += '<a data-id="' + orderId + '" class="blue_color repealAdvert">作废</a>';
//  }

  $(".editAdvert").live("click", function () {
    $("#addOrUpdateAdvert").text("编辑宣传");
    var orderId = $(this).attr("data-id");
    APP_BCGOGO.Net.asyncAjax({
      url: "advert.do?method=getAdvertDetail",
      type: "POST",
      cache: false,
      dataType: "json",
      data: {
        idStr: orderId
      },
      success: function (data) {
        if (data.success) {
          showAddAdvertDiv(data.data);
        }
      },
      error: function () {
        nsDialog.jAlert("网络异常！");
      }
    });
  });

  $(".copyAdvert").live("click", function () {
    $("#addOrUpdateAdvert").text("新增宣传");
    var orderId = $(this).attr("data-id");
    APP_BCGOGO.Net.asyncAjax({
      url: "advert.do?method=getAdvertDetail",
      type: "POST",
      cache: false,
      dataType: "json",
      data: {
        idStr: orderId
      },
      success: function (data) {
        if (data.success) {
          data.data.idStr = "";
          showAddAdvertDiv(data.data);
        }
      },
      error: function () {
        nsDialog.jAlert("网络异常！");
      }
    });
  });


  $(".publishAdvert").live("click", function () {
    var orderId = $(this).attr("data-id");
    APP_BCGOGO.Net.asyncAjax({
      url: "advert.do?method=publishAdvert",
      type: "POST",
      cache: false,
      dataType: "json",
      data: {
        idStr: orderId
      },
      success: function (data) {
        if (data.success) {
          nsDialog.jAlert("发布成功！");
        } else {
          nsDialog.jAlert("发布失败！");
        }
        $("#searchBtn").click();
      },
      error: function () {
        nsDialog.jAlert("网络异常！");
        $("#searchBtn").click();
      }
    });
  });


  $(".repealAdvert").live("click", function () {
    var orderId = $(this).attr("data-id");
    APP_BCGOGO.Net.asyncAjax({
      url: "advert.do?method=repealAdvert",
      type: "POST",
      cache: false,
      dataType: "json",
      data: {
        idStr: orderId
      },
      success: function (data) {
        if (data.success) {
          nsDialog.jAlert("作废成功！");
        } else {
          nsDialog.jAlert("作废失败！");
        }
        $("#searchBtn").click();
      },
      error: function () {
        nsDialog.jAlert("网络异常！");
        $("#searchBtn").click();
      }
    });
  });


//  UE.getEditor('productDescriptionEditor');


  $("#addAdvertDiv .turn_off").click(function () {
    $("#addAdvertDiv").dialog("close");
  });

  $("#saveDraftBtn").click(function () {
    var validateResult = validateBeforeSave();
    if (G.isNotEmpty(validateResult)) {
      nsDialog.jAlert(validateResult);
      return;
    }

    $("#status").val("WAIT_PUBLISH");
    $("#addAdvertForm").ajaxSubmit({
      dataType: "json",
      success: function (data) {
          $("#addAdvertDiv").dialog("close");
          if (data && data.success) {
              previewAdvert(data.data);
          }
        $("#searchBtn").click();
      },
      error: function () {
        alert("网络异常，请联系客服");
      }
    });

  });

  $("#publishBtn").click(function () {

    var validateResult = validateBeforeSave();
    if (G.isNotEmpty(validateResult)) {
      nsDialog.jAlert(validateResult);
      return;
    }

    $("#status").val("ACTIVE");
    $("#addAdvertForm").ajaxSubmit({
      dataType: "json",
      success: function (data) {
        $("#addAdvertDiv").dialog("close");
        $("#searchBtn").click();
      },
      error: function () {
        alert("网络异常，请联系客服");
      }
    });

  });

  $("#addAdvert").click(function () {
    $("#addOrUpdateAdvert").text("新增宣传");
    $("#beginDateStr").val($("#beginDate").val());
    showAddAdvertDiv(null);
  });


  $("#clearBtn").click(function () {
    $("#selectDate").find("a").removeClass("clicked");
    $("#WAIT_PUBLISH").attr("checked", "checked");
    $("#ACTIVE").attr("checked", "checked");
    $("#OVERDUE").attr("checked", "checked");
    $("#REPEALED").attr("checked", "");
    $("#startDate").val("");
    $("#endDate").val("");
  });


  $("#beginDateStr,#endDateStr")
      .datepicker({
        "numberOfMonths": 1,
        "showButtonPanel": false,
        "changeYear": true,
        "showHour": false,
        "showMinute": false,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": "",
        "onSelect": function (dateText, inst) {
          $(this).blur();
        }
      })
      .blur(function () {
        var startDate = $("#beginDateStr").val();
        var endDate = $("#endDateStr").val();
        if (startDate == "" || endDate == "") return;
        if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
          return;
        } else {
          if (startDate > endDate) {
            $("#endDateStr").val(startDate);
            $("#beginDateStr").val(endDate);
          }
        }
      })
      .bind("click", function () {
        $(this).blur();
      })
      .change(function () {
        var startDate = $("#beginDateStr").val();
        var endDate = $("#endDateStr").val();
        $(".good_his > .today_list").removeClass("hoverList");
        if (endDate == "" || startDate == "") {
          return;
        }
        if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
          return;
        } else {
          if (startDate > endDate) {
            $("#endDateStr").val(startDate);
            $("#beginDateStr").val(endDate);
          }
        }
      })


  $("#startDate,#endDate")
      .datepicker({
        "numberOfMonths": 1,
        "showButtonPanel": false,
        "changeYear": true,
        "showHour": false,
        "showMinute": false,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": "",
        "onSelect": function (dateText, inst) {
          if ($("a[name='my_date_select']").length > 0) {
            $("a[name='my_date_select']").each(function () {
              $(this).removeClass("clicked");
            });
            $("#my_date_self_defining").click();

          }

        }
      })
      .blur(function () {
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
      .bind("click", function () {
        $(this).blur();
      })
      .change(function () {
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


  $("#searchBtn").click(function () {

    $("#noAdvertList").css("display", "none");

    var param = $("#shopAdvertSearchForm").serializeArray();
    var paramJson = {};
    $.each(param, function (index, val) {
      if (val.name == "advertStatus") {
        if (paramJson[val.name] == undefined) {
          paramJson[val.name] = val.value;
        } else {
          paramJson[val.name] += "," + val.value;
        }
      } else {
        paramJson[val.name] = val.value;
      }
    });

    $("#shopAdvertSearchForm").ajaxSubmit({
      dataType: "json",
      success: function (data) {
        if (data.length == 2) {
          initShopAdvert(data);
          initPages(data, "dynamicalAdvert", "", '', "initShopAdvert", '', '', paramJson, "");
        }
      },
      error: function () {
        alert("网络异常，请联系客服");
      }
    });

  });

  $("#searchBtn").click();

    $(".previewAdvert").live("click",function(){
        var dataId = $(this).attr("data-id");
        if(G.Lang.isNotEmpty(dataId)){
            APP_BCGOGO.Net.asyncAjax({
                url: "advert.do?method=getAdvertDetail",
                type: "POST",
                cache: false,
                dataType: "json",
                data: {
                    idStr: dataId
                },
                success: function (data) {
                    if (data.success) {
                        previewAdvert(data.data);
                    }
                },
                error: function () {
                    nsDialog.jAlert("网络异常！");
                }
            });
        }
    });

    $(".J_preview_close").click(function(){
        previewCover.unCover();
        $(".J_preview_container").hide();
        $("#addAdvertDiv").dialog("close");
    });

    $(".J_publishAdvert").click(function(){
        var orderId = $(this).attr("data-id");
        previewCover.unCover();
        $(".J_preview_container").hide();
        APP_BCGOGO.Net.asyncAjax({
            url: "advert.do?method=publishAdvert",
            type: "POST",
            cache: false,
            dataType: "json",
            data: {
                idStr: orderId
            },
            success: function (data) {
                if (data.success) {
                    nsDialog.jAlert("发布成功！");
                } else {
                    nsDialog.jAlert("发布失败！");
                }
                $("#searchBtn").click();
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
                $("#searchBtn").click();
            }
        });
    });

    $(".J_editAdvert").click(function(){
        var orderId = $(this).attr("data-id");
        previewCover.unCover();
        $(".J_preview_container").hide();
        $("#addOrUpdateAdvert").text("编辑宣传");
        APP_BCGOGO.Net.asyncAjax({
            url: "advert.do?method=getAdvertDetail",
            type: "POST",
            cache: false,
            dataType: "json",
            data: {
                idStr: orderId
            },
            success: function (data) {
                if (data.success) {
                    showAddAdvertDiv(data.data);
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
            }
        });
    });

    $(".J_repealAdvert").click(function () {
        var orderId = $(this).attr("data-id");
        previewCover.unCover();
        $(".J_preview_container").hide();
        APP_BCGOGO.Net.asyncAjax({
            url: "advert.do?method=repealAdvert",
            type: "POST",
            cache: false,
            dataType: "json",
            data: {
                idStr: orderId
            },
            success: function (data) {
                if (data.success) {
                    nsDialog.jAlert("作废成功！");
                } else {
                    nsDialog.jAlert("作废失败！");
                }
                $("#searchBtn").click();
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
                $("#searchBtn").click();
            }
        });
    });

    $(".J_copyAdvert").click(function () {
        $("#addOrUpdateAdvert").text("新增宣传");
        var orderId = $(this).attr("data-id");
        previewCover.unCover();
        $(".J_preview_container").hide();
        APP_BCGOGO.Net.asyncAjax({
            url: "advert.do?method=getAdvertDetail",
            type: "POST",
            cache: false,
            dataType: "json",
            data: {
                idStr: orderId
            },
            success: function (data) {
                if (data.success) {
                    data.data.idStr = "";
                    showAddAdvertDiv(data.data);
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
            }
        });
    });

});

var previewCover = {
    _$Cover:null,
    cover: function() {
        if(this._$Cover == null){
            this._$Cover = $("<div></div>");
            $("body").append( this._$Cover);
        }
        this._$Cover.css({
                'display': 'block',
                'position': 'fixed',
                'top': '0px',
                'left': '0px',
                'width':"100%",
                "height":"100%",
                'z-index': this.getMaxZIndex()+10,
                'filter': "alpha(opacity=40)",
                'opacity': '0.4',
                'background-color': '#000000'
            });

    },

    unCover: function () {
        if(this._$Cover!=null){
            this._$Cover.remove();
            this._$Cover = null;
        }
    },
    getZIndex:function(){
        if(this._$Cover != null){
            return parseInt(this._$Cover.css('z-index'))||1
        }
        return 1;
    },
    getMaxZIndex:function(){
        var divs = document.getElementsByTagName("div");
        for(var i=0, max=0; i<divs.length; i++){
            max = Math.max( max,divs[i].style.zIndex || 0 );
        }
        return max;
    }
};

function previewAdvert(data){
    if(G.Lang.isNotEmpty(data)){
        previewCover.cover();
        var $previewContainer = $(".J_preview_container");
        var orderId = G.normalize(data.idStr);
        var title = G.normalize(data.title, "--");
        var dateHtml = '(';
        if(G.Lang.isNotEmpty(data.beginDateStr)){
            dateHtml += data.beginDateStr;
        }
        if(G.Lang.isNotEmpty(data.endDateStr)){
            dateHtml += '至' + data.endDateStr;
        }else{
            dateHtml += '起永久有效';
        }
        dateHtml += ')&nbsp;';
        var statusStr = G.normalize(data.statusStr, "");
        var status = G.normalize(data.status, "");
        var description = G.normalize(data.description, "");
        var statusHtml = '';
        if(status == 'ACTIVE'){
            statusHtml += '<div class="ysx">';
        }else if(status == 'WAIT_PUBLISH'){
            statusHtml += '<div class="dfb">';
        }else if(status == 'OVERDUE' || status == 'REPEALED'){
            statusHtml += '<div class="ygq">';
        }else{
            statusHtml += '<div>';
        }
        statusHtml += statusStr +'</div>';
        $previewContainer.find(".J_preview_status").html(statusHtml);
        $previewContainer.find(".J_preview_title").html(title);
        $previewContainer.find(".J_preview_date").html(dateHtml);
        $previewContainer.find(".J_preview_date_description").html(description);
        $previewContainer.find(".public_relative ").css("z-index",previewCover.getZIndex()+2);
        var $publishAdvert = $previewContainer.find(".J_publishAdvert");
        $publishAdvert.attr("data-id",orderId);
        var $editAdvert = $previewContainer.find(".J_editAdvert");
        $editAdvert.attr("data-id",orderId);
        var $repealAdvert = $previewContainer.find(".J_repealAdvert");
        $repealAdvert.attr("data-id",orderId);
        var $copyAdvert = $previewContainer.find(".J_copyAdvert");
        $copyAdvert.attr("data-id",orderId);

        if(status == 'ACTIVE'){
            $publishAdvert.hide();
            $editAdvert.hide();
            $repealAdvert.hide();
            $copyAdvert.hide();
        }else if(status == 'WAIT_PUBLISH'){
            $publishAdvert.show();
            $editAdvert.show();
            $repealAdvert.show();
            $copyAdvert.hide();
        }else if(status == 'OVERDUE' || status == 'REPEALED'){
            $publishAdvert.hide();
            $editAdvert.hide();
            $repealAdvert.hide();
            $copyAdvert.show();
        }else{
            $publishAdvert.show();
            $editAdvert.hide();
            $repealAdvert.hide();
            $copyAdvert.hide();
        }
        $previewContainer.css("z-index",previewCover.getZIndex()+1). show();

        //滚动条初始化

        new Scrolling.Scrollbar($("#dv_scroll_bar")[0],
            new Scrolling.Scroller($("#dv_scroll")[0], 400, 400, false),
            new Scrolling.ScrollTween());

    }



}

function validateBeforeSave() {

  if (G.isEmpty($("#advertTitle").val())) {
    return "请填写标题！";
  }

  var productDescriptionEditor = UE.getEditor('productDescriptionEditor');


  if (G.isEmpty(productDescriptionEditor.getContent())) {
    return "请填写宣传描述！";
  }
  if (G.isEmpty($("#beginDateStr").val())) {
    return "请选择有效期开始时间";
  }

  if (!G.isEmpty($("#beginDateStr").val()) && !G.isEmpty($("#endDateStr").val()) && $("#beginDateStr").val() > $("#endDateStr").val()) {
    return "开始时间应小于结束时间";
  }


  if (!G.isEmpty($("#endDateStr").val())) {
    var myDate = G.Date.getCurrentFormatDate();
    if (myDate > $("#endDateStr").val()) {
      return  "有效期截止时间请选择今天及以后的日期";
    }

  }


}

function showDate(data) {
//  var productDescriptionEditor = UE.getEditor('productDescriptionEditor');
////  productDescriptionEditor.ready(function () {
//    productDescriptionEditor.setContent(data.description);
////  });
  $("#advertTitle").val(data.title);
  $("#beginDateStr").val(data.beginDateStr);
  $("#advertId").val(data.idStr);

  $("#endDateStr").val(data.endDateStr);

}

function showAddAdvertDiv(data) {

  var productDescriptionEditor = UE.getEditor('productDescriptionEditor');
  productDescriptionEditor.ready(function () {
    $("#addAdvertDiv").dialog({
      width: 830,
      modal: true,
      resizable: false,
      position: 'center',
      open: function () {
        $(".ui-dialog-titlebar", $(this).parent()).hide();
        $(this).parent().css("z-index", "901");
        $(this).parent().parent().find(".ui-widget-overlay").css("z-index", "900");

        $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
      },
      close: function () {
        $("#addAdvertForm")[0].reset();
        var productDescriptionEditor = UE.getEditor('productDescriptionEditor');
        productDescriptionEditor.ready(function () {
          productDescriptionEditor.setContent("");
        });

        $("#advertTitle").val("");
        $("#beginDateStr").val("");
        $("#advertId").val("");
        $("#endDateStr").val("");
      }
    });
    if (data != null) {
      productDescriptionEditor.setContent(data.description);
      showDate(data);
    }
    nsDialog.jAlert("");
    $("#jAlert").dialog("close");

  });

}


function initShopAdvert(data) {
  $("#advertTable tr:not(:first)").remove();
  if (data == null || data[0] == null) {
    $("#noAdvertList").css("display", "block");
    return;
  }
  var tr = '';

  $.each(data[0], function (index, order) {
    var orderId = G.normalize(order.idStr);
    var editDateStr = G.normalize(order.editDateStr, "--");
    var title = G.normalize(order.title, "--");
    var beginDateStr = G.normalize(order.beginDateStr, "--");
    var endDateStr = G.normalize(order.endDateStr, "永久有效");
    var user = G.normalize(order.user, "--");
    var statusStr = G.normalize(order.statusStr, "--");
    var status = G.normalize(order.status, "--");
    var yesNo = G.normalize(order.containImage, "--");

    tr += '<tr>';
    if (index % 2 != 0) {
      tr += '<tr class="greyGround">';
    }


    tr += '<td>' + (index + 1) + '</td>';
    tr += '<td title="' + editDateStr + '">' + editDateStr + '</td>';
    tr += '<td title="' + title + '"><a class="blue_color previewAdvert" data-id = "' + orderId + '">' + title + '</a>';

    if (yesNo == "YES") {
      tr += '&nbsp;<img src="images/pic_r2_c2.jpg"/>';
    }

    tr += '</td>';
    tr += '<td>' + beginDateStr + '至' + endDateStr + '</td>';
    tr += '<td title="' + user + '">' + user + '</td>';

    tr += '<td title="' + statusStr + '">' + statusStr + '</td>';

    tr += '<td>' + '<a data-id="' + orderId + '" class="blue_color previewAdvert">预览</a>';

    if (status == "OVERDUE" || status == "REPEALED") {
      tr += '&nbsp;<a data-id="' + orderId + '" class="blue_color copyAdvert">复制</a>';

    } else if (status == "WAIT_PUBLISH") {
      tr += '&nbsp;<a data-id="' + orderId + '" class="blue_color publishAdvert">发布</a>';
      tr += '&nbsp;<a data-id="' + orderId + '" class="blue_color editAdvert">编辑</a>';
      tr += '&nbsp;<a data-id="' + orderId + '" class="blue_color repealAdvert">作废</a>';
    }

    tr += '</td></tr>';

  });

  $("#advertTable").append($(tr));

}