/**
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 14-1-15
 * Time: 上午11:49
 * To change this template use File | Settings | File Templates.
 */

var isShow = false; //客户资料遮盖区域是否显示

$(document).click(function (e) {
  if (!isShow) {
    return;
  }

  var e = e || event;
  var target = e.srcElement || e.target;

  if ($(target).attr("id") == "showDetailInfo" || $(target).hasClass("J_showCustomerOtherInfo")
      || $(target).parent().hasClass("J_showCustomerOtherInfo") || $(target).hasClass("J_showCustomerOtherInfo")) {
    return;
  }

  if ($(target).parents(".customerOtherInfo").attr("id") == "customerOtherInfo") {
    return;
  }
  $('#customerOtherInfo').slideUp('fast');
  $("#showDetailInfo").css("display","block");
  isShow = false;
});

$(document).ready(function () {
  $("#customerInfoTitle,#customerAppointTitle,#customerStatementTitle,#customerPhotoTitle").live("click", function () {
    if ($(this).hasClass("arrer")) {
      return;
    }
    $("#customerInfoTitle,#customerAppointTitle,#customerStatementTitle,#customerPhotoTitle").removeClass("arrer");
    $(this).addClass("arrer");

    if ($(this).attr("id") == "customerInfoTitle") {
      $("#customerDetailAppoint,#customerDetailStatement,#customerDetailPhoto").css("display", "none");
      $("#customerDetailMember,#customerDetailVehicle,#customerDetailConsume").css("display", "block");
    } else if ($(this).attr("id") == "customerAppointTitle") {
      $("#customerDetailMember,#customerDetailVehicle,#customerDetailConsume,#customerDetailStatement,#customerDetailPhoto").css("display", "none");
      $("#customerDetailAppoint").css("display", "block");
    } else if ($(this).attr("id") == "customerStatementTitle") {
      $("#customerDetailMember,#customerDetailVehicle,#customerDetailConsume,#customerDetailAppoint,#customerDetailPhoto").css("display", "none");
      $("#customerDetailStatement").css("display", "block");
    } else if ($(this).attr("id") == "customerPhotoTitle") {
      $("#customerDetailMember,#customerDetailVehicle,#customerDetailConsume,#customerDetailAppoint,#customerDetailStatement").css("display", "none");
      $("#customerDetailPhoto").css("display", "block");
    }

  });

  var title = G.Util.getUrlParameter("title");
  if (title == "statementAccount") {
    $("#customerStatementTitle").click();
  }


  $("input[name$='licenceNo']").bind("blur", function () {
    $(this).val($(this).val().toUpperCase());
    var nameValue = $(this).val();
    if (G.isEmpty(nameValue)) {
      return;
    }

    var resultStr = nameValue;
    //去空格，去短横
    resultStr = nameValue.split(" ").join("").split("-").join("");
    if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber(resultStr)) {
      nsDialog.jAlert("输入的车牌号码不符合规范，请检查！", null, function () {
        $(this).focus();
      });
      return;
    }
    if ($(this).val() != null || $(this).val() != '') {
      if (resultStr.length == 5 && !checkhanzi(resultStr)) { //前缀
        var r;
        APP_BCGOGO.Net.syncGet({url: "product.do?method=userLicenseNo",
          dataType: "json", success: function (json) {
            r = json;
          }
        });
        if (r.length == 0) return;
        else {
          var locaono = r[0].localCarNo;
          $(this).val((locaono + $(this).val()).toUpperCase());
        }
        locaono = '';
      }
    }
  });



  $(".J_customerVehicleTitle").live("click", function (event) {
      event.preventDefault();
    $(event.target).parent().find('a').removeClass("normal_btn2").addClass("hover_btn2");
    $(event.target).addClass("normal_btn2");
    $('#addCustomerVehicleBtn').removeClass("normal_btn2").addClass("hover_btn2");
    $("#customerVehicleContainer").find(".J_customerVehicleDiv").css("display", "none");
    var dataIndex = $(this).attr("data-index");
    $("#customerVehicleDiv" + dataIndex).show();
  });

  $("#moreConsumeInfo").live("click", function (event) {

    if ($("#queryCondition").css("display") == "none") {
      $("#queryCondition").css("display", "block");
      $(this).html('查询条件<img src="images/rightTop.png" style="float:right; margin:12px 0 0 5px;"/>');
    } else {
      $("#queryCondition").css("display", "none");
      $(this).html('查询条件<img src="images/rightArrow.png" style="float:right; margin:12px 0 0 5px;"/>');
    }
  });


  $(".J_deleteAppointService").live("click", function () {

    var table = $(this).parents(".J_vehicleAppointTable");

    $(this).parent().find("input[name$='operateType']").val("LOGIC_DELETE");
    $(this).parent().hide();
    $(this).parent().prev().hide();

    if (G.isEmpty($(this).parent().find("input[name$='id']").val())) {
      $(this).parent().prev().remove();
      $(this).parent().remove();
    }

    $(this).remove();
    table.find(".J_addAppointService").remove();
    if (table.find(".J_deleteAppointService").size() != 0) {
      var lastDelete = table.find(".J_deleteAppointService:last");
      var deleteIndex = lastDelete.attr("data-index");
      lastDelete.after('<a class="J_addAppointService" data-index="' + deleteIndex + '"><img src="images/opera2.png"/></a>');
    } else {
      table.find("input[name='yc']").after('<a class="J_addAppointService" data-index="0"><img src="images/opera2.png"/></a>');
    }


  });


  $("#orderStatusRepeal").live("click", function () {
    $("#btnSearch").click();
  });

  $(".J_addAppointService").live("click", function () {

    var memberTable = $(this).parents(".J_vehicleAppointTable");

    var isFirst = false;
    if (memberTable.find(".J_deleteAppointService").size() == 0) {
      isFirst = true;
    }

    var validateFlag = false;

    if (!isFirst) {
      $(this).parents("tr").find("input").each(function () {
            if (!validateFlag) {
              if ($(this).attr("type") != "hidden" && G.Lang.isEmpty($(this).val()) && $(this).parent('td').css('display')!='none') {
                if ($(this).attr('name').indexOf("appointName") != -1) {
                  nsDialog.jAlert("请输入提醒服务内容!");
                  validateFlag = true;
                  return;
                } else if ($(this).attr('name').indexOf("appointDate") != -1) {
                  nsDialog.jAlert("请输入提醒服务时间!");
                  validateFlag = true;
                  return;
                }

                return;
              }
            }
          }
      );
      if (validateFlag) {
        return;
      }
    }
    var tdSize = $(this).parents('tr').find('td').size();
    $(this).parents('tr').find('td').each(function () {
      if ($(this).css('display') == 'none') {
        tdSize = tdSize - 1;
      }
    });

    $(this).hide();

    var addTdIndex = memberTable.find("input[name$='.appointName']").size();
    var tdHtml = '<td class="test2"><input type="text" style="width:44px"' +
        ' name="appointServiceDTOs[' + addTdIndex + '].appointName"' +
        ' reset-value="" value=""' +
        ' class="txt J_formreset"/>：</td>' +
        ' <td class="J_appointServiceDateTd">' +
        ' <input type="hidden" name="appointServiceDTOs[' + addTdIndex + '].id"' +
        ' value="">' +
        ' <input type="hidden" name="appointServiceDTOs[' + addTdIndex + '].operateType"' +
              ' value="">' +
        ' <input type="text" style="width:75px;" onclick="showDatePicker(this);" readonly="readonly"' +
        ' name="appointServiceDTOs[' + addTdIndex + '].appointDate"' +
        ' reset-value="" value=""' +
        ' class="txt J_formreset"/>' +
        ' <a data-index="' + addTdIndex + '" class="J_deleteAppointService"><img src="images/opera1.png"/></a>' +
        ' <a class="J_addAppointService" data-index="' + addTdIndex + '"><img src="images/opera2.png"/></a>' +
        ' </td>';
    if (!isFirst) {
      if (tdSize == 2) {
        $(this).parents('tr').find('td:last').after(tdHtml);
      } else {
        var trHtml = '<tr class="J_appointServiceTrEdit">' + tdHtml + '</tr>';
        memberTable.append(trHtml);
      }
    } else {
      var trHtml = '<tr class="J_appointServiceTrEdit">' + tdHtml + '</tr>';
      memberTable.append(trHtml);
    }
    $(this).remove();

  });


//  $(".J_showCustomerOtherInfo").live("mouseenter",function (e) {
//    $("#showDetailInfo").show();
//  }).live("mouseleave",function (e) {
//        if ($(e.relatedTarget).parents(".J_showCustomerOtherInfo").length > 0 || $(e.relatedTarget).parents(".titBottom_Bg").length > 0
//            || $(e.relatedTarget).parents(".clear").length > 0) {
//          return;
//        }
//
//        $("#showDetailInfo").hide();
//      }).live("click", function () {
//        if (isShow) {
//          $('#customerOtherInfo').slideUp('fast');
//          isShow = false;
//        } else {
//          showDetailInfo();
//          isShow = true;
//        }
//      });
});


function showDetailInfo() {
  if (!isShow) {
      var $customerDetailTableTrs = $('#customerDetailTable').find("tr");
      var $CurrentRow =  $customerDetailTableTrs.eq($customerDetailTableTrs.size() -1);
      var top = $CurrentRow.offset().top
          + $CurrentRow.outerHeight() + 4 - $(".i_main").eq(0).offset().top;
    $("#customerOtherInfo").slideUp('fast', function () {
      $('#customerOtherInfo').css({
        'position': 'absolute',
        'z-index': '1',
        'top': top + 'px'
      }).slideDown('fast');
    });
    isShow = true;
    $("#showDetailInfo").css("display","none");
  } else {
    $('#customerOtherInfo').slideUp('fast');
    $("#showDetailInfo").css("display","block");
    isShow = false;
  }
}

function customerConsume(consumeType) {
  $("#customerInfoTitle").click();

  $("#resetSearchCondition").click();

  if (consumeType == "totalConsume") {
    $("#saleLabel").find("input").attr("checked", true);
    $("#repairLabel").find("input").attr("checked", true);
    $("#washLabel").find("input").attr("checked", true);
  } else if (consumeType == "salesReturn") {
    $("#saleReturnLabel").find("input").attr("checked", true);
  } else if (consumeType == "totalReceivable") {
    $("#saleLabel").find("input").attr("checked", true);
    $("#repairLabel").find("input").attr("checked", true);
    $("#washLabel").find("input").attr("checked", true);
    $("#memberLabel").find("input").attr("checked", true);
    $("#debtLabel").find("input").attr("checked", true);
    $("#debtType").val("RECEIVABLE");
  } else if (consumeType == "totalReturn") {
    $("#saleReturnLabel").find("input").attr("checked", true);
    $("#memberReturnLabel").find("input").attr("checked", true);
    $("#debtLabel").find("input").attr("checked", true);
    $("#debtType").val("PAYABLE");
  } else if (consumeType == "memberConsume") {
    $("#saleLabel").find("input").attr("checked", true);
    $("#repairLabel").find("input").attr("checked", true);
    $("#washLabel").find("input").attr("checked", true);
    $("#accountMemberNo").val($("#customerMemberNoSpan").text());
  } else if (consumeType == "vehicleConsume") {
    $("#saleLabel").find("input").attr("checked", true);
    $("#repairLabel").find("input").attr("checked", true);
    $("#washLabel").find("input").attr("checked", true);

    var vehicleNum = $("#customerVehicleTitle").find(".normal_btn2").text();
    if (G.Lang.isNotEmpty(vehicleNum)) {
      $("#vehicleNumber").val(vehicleNum);
      $("#vehicleNumber").css({"color":"#000000"});
    } else {
      return;
    }
  } else if (consumeType == "supplierTotalConsume") {
    $("#inventoryLabel").find("input").attr("checked", true);
  } else if (consumeType == "purchaseReturn") {
    $("#returnLabel").find("input").attr("checked", true);
  } else if (consumeType == "supplierTotalReceivable") {
    $("#returnLabel").find("input").attr("checked", true);
    $("#debtLabel").find("input").attr("checked", true);
    $("#debtType").val("RECEIVABLE");
  } else if (consumeType == "supplierTotalReturn") {
    $("#inventoryLabel").find("input").attr("checked", true);
    $("#debtLabel").find("input").attr("checked", true);
    $("#debtType").val("PAYABLE");
  }

  $("#btnSearch").click();
  $("html,body").animate({scrollTop: $("#customerDetailConsume").offset().top}, 10);
  $("#debtType").val("");
  $("#accountMemberNo").val("");

}
