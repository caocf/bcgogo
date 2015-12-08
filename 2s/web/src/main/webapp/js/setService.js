/**
 * 设定施工项目
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-10-15
 * Time: 下午5:03
 * To change this template use File | Settings | File Templates.
 */
//
//jQuery(function() {
//  jQuery(".tuihuo_first table tr").eq(1).nextAll().hide();
//
//  var urlParams = window.location.search;
//
//  if(urlParams && urlParams.split("=") && urlParams.split("=")[1] == "getCategoryItemSearch") {
//    $("#fencount").addClass("hover_yinye");
//  } else if(urlParams && urlParams.split("=") && urlParams.split("=")[1] == "getServiceNoCategory") {
//    $("#noCategory").addClass("hover_yinye");
//  } else if(urlParams && urlParams.split("=") && urlParams.split("=")[1] == "getServiceCategory") {
//    $("#first_cont").addClass("hover_yinye");
//  }
//
//  tableUtil.tableStyle('#tb_tui', '.tab_title');
//
//});

function editService(domObject) {
  var id = jQuery(domObject).attr("id");
  var idStr = id.split(".");

  $("#" + idStr[0] + "\\.nameSpan").css("display", "none");
  $("#" + idStr[0] + "\\.name").css("display", "block");

  $("#" + idStr[0] + "\\.categoryNameSpan").css("display", "none");
  $("#" + idStr[0] + "\\.categoryName").css("display", "block");

  $("#" + idStr[0] + "\\.priceSpan").css("display", "none");
  $("#" + idStr[0] + "\\.price").css("display", "block");

  $("#" + idStr[0] + "\\.percentageAmountSpan").css("display", "none");
  $("#" + idStr[0] + "\\.percentageAmount").css("display", "block");

  $("#" + idStr[0] + "\\.edit").css("display", "none");
  $("#" + idStr[0] + "\\.save").css("display", "");
}