/**
 * bcgogoReceivableRecord.jsp
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-27
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$(document).ready(function() {

  if ($("#receivableErrorInfo").val()) {
    nsDialog.jAlert($("#receivableErrorInfo").val());
  }


  //初始化待支付记录
  getPayableRecordTable();
  $("#payableAmount,#installmentPayAmount,#otherPayableAmount").live("keyup blur", function (event) {
    if (event.type == "focusout") event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 0);
    else if (event.type == "keyup") if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 0)) {
      event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 0);
    }
  });

  $("input[name='date_select']").bind("click", function() {
    var now = new Date();
    var year = now.getFullYear();
    if ($(this).val() == "this_week") {
      $("#startDate").val(dateUtil.getWeekStartDate());
      $("#endDate").val(dateUtil.getToday());
    } else if ($(this).val() == "this_month") {
      $("#startDate").val(dateUtil.getOneMonthBefore());
      $("#endDate").val(dateUtil.getToday());
    } else if ($(this).val() == "one_year") {
      $("#startDate").val(dateUtil.getOneYearBefore());
      $("#endDate").val(dateUtil.getToday());
    } else if ($(this).val() == "this_day") {
      $("#startDate").val(dateUtil.getToday());
      $("#endDate").val(dateUtil.getToday());
    } else if ($(this).val() == "three_month") {
      $("#startDate").val(dateUtil.getThreeMonthStartDate());
      $("#endDate").val(dateUtil.getToday());
    } else if ($(this).val() == "half_year") {
      $("#startDate").val(dateUtil.getSixMonthStartDate());
      $("#endDate").val(dateUtil.getToday());
    }

  });
  $("#date_this_month").click();

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


  $("#searchBtn").click(function() {
    getPaidRecordTable();
  });

  $("#fullPay").click(function() {
    $("#instalmentPayDiv").css("display", "none");
    $("#instalmentPaySelect").css("display", "none");
    $("#firstPayMethod").val("fullPay");
  });

  $("#instalmentPay").click(function() {
    $("#instalmentPayDiv").css("display", "block");
    $("#instalmentPaySelect").css("display", "block");
    $("#firstPayMethod").val("instalmentPay");
    $("#instalmentSelect").click();

  });
  $("#fullPay").click();


  $("#cancelBtn,#hardwareCancelBtn,#installmentCancelBtn,#otherCancelBtn").click(function() {
    $("#instalmentPayDiv").css("display", "none");
    $("#instalmentPaySelect").css("display", "none");
    $("#softwarePayDiv").css("display", "none").dialog("close");
    $("#hardwarePayDiv").css("display", "none").dialog("close");
    $("#installmentPayDiv").css("display", "none").dialog("close");
    $("#softwareOtherPayDiv").css("display", "none").dialog("close");
    $("#installmentPayAmount").val("");
    $("#payableAmount").val("");

    $("#secondInstallment").css("display", "none");
    $("#firstInstalmentTable").css("display", "none");
    $("#secondInstalmentTable").css("display", "none");
  });

//  $("#hardwareCancelBtn").click(function() {
//    $("#instalmentPayDiv").css("display", "none");
//    $("#instalmentPaySelect").css("display", "none");
//    $("#softwarePayDiv").css("display", "none").dialog("close");
//    $("#hardwarePayDiv").css("display", "none").dialog("close");
//    $("#firstInstalmentTable").css("display", "none");
//    $("#secondInstalmentTable").css("display", "none");
//  });

  $("#instalmentSelect").click(function() {


    var planId = $("#instalmentSelect").val();
    if (planId == "") {
      return;
    }


    var url = "bcgogoReceivable.do?method=getInstalmentPlanAlgorithmsById";
    var data = {instalmentPlanAlgorithmIdStr:planId};
    bcgogoAjaxQuery.setUrlData(url, data);
    bcgogoAjaxQuery.ajaxQuery(function (json) {
      if (json == null) {
        return;
      }

      if (json.name == "6期") {
        $("#secondInstalmentTable").css("display", "none");
        $("#firstInstalmentTable").css("display", "block");
        $("#instalmentSelectName").val(json.name);
        $("#payableAmount").val( $("#firstAmountTdSix").text());

      } else if (json.name == "12期") {
        $("#secondInstalmentTable").css("display", "block");
        $("#firstInstalmentTable").css("display", "none");
        $("#instalmentSelectName").val(json.name);
        $("#payableAmount").val( $("#firstAmountTd").text());
      }
    });


  });

  $("#confirmBtn").click(function() {
    $("#confirmBtn").attr("disabled", "disabled");
    var payableAmount = $("#payableAmount").val();

    if (payableAmount == "" || payableAmount == null) {
      $("#confirmBtn").attr("disabled", false);
      nsDialog.jAlert("请输入实付款金额");
      return;
    }
    payableAmount = payableAmount * 1;

    if (payableAmount <= 0) {
      $("#confirmBtn").attr("disabled", false);
      nsDialog.jAlert("实付金额必须大于0");
      return;
    }

    var orderTotalAmount = $("#orderTotalAmount").text() * 1;


    if (payableAmount > orderTotalAmount && !GLOBAL.Number.equalsTo(payableAmount, orderTotalAmount)) {
      $("#confirmBtn").attr("disabled", false);
      nsDialog.jAlert("实付金额不能大于软件总价");
      return;
    }

    var pay = 0;
    if ($("#firstPayMethod").val() == "fullPay") {
      $("#receivableMethod").val("FULL");

      pay = orderTotalAmount;
    } else if ($("#firstPayMethod").val() == "instalmentPay") {
      $("#receivableMethod").val("INSTALLMENT");

      if ($("#instalmentSelectName").val() == "6期") {
        pay = $("#firstAmountTdSix").text();
      } else if ($("#instalmentSelectName").val() == "12期") {
        pay = $("#firstAmountTd").text();
      }
    }

    if (pay == "" || pay == null || pay <= 0) {
      $("#confirmBtn").attr("disabled", false);
      nsDialog.jAlert("请选择付款方式");
      return;
    }

    pay = pay * 1;

    if ($("#firstPayMethod").val() == "fullPay" && !GLOBAL.Number.equalsTo(payableAmount, pay)) {
      $("#confirmBtn").attr("disabled", false);
      nsDialog.jAlert("实付金额必须等于软件总价");
      return;
    } else if ($("#firstPayMethod").val() == "instalmentPay" && payableAmount < pay) {
      $("#confirmBtn").attr("disabled", false);
      nsDialog.jAlert("实付金额必须大于第一期金额");
      return;
    }

    $("#instalmentPlanAlgorithmId").val($("#instalmentSelect").val());
    $("#relationId").val($("#firstPayRelationId").val());
    $("#payTotalAmount").val(orderTotalAmount);
    $("#paidAmount").val(payableAmount);
    $("#paymentMethod").val("ONLINE_PAYMENT");

    $("#payForm").attr('action', 'bcgogoReceivable.do?method=softwareOnlineReceivable');
    $("#payForm").submit();
  });


  $("#hardwareConfirmBtn").click(function() {
    $("#hardwareConfirmBtn").attr("disabled", true);

    $("#payForm").submit();

  });


  $("#installmentConfirmBtn").click(function() {
    $("#installmentConfirmBtn").attr("disabled", true);
    var payableAmount = $("#installmentPayAmount").val();

    if (payableAmount == "" || payableAmount == null) {
      $("#installmentConfirmBtn").attr("disabled", false);
      nsDialog.jAlert("请输入实付款金额");
      return;
    }
    payableAmount = payableAmount * 1;

    if (payableAmount <= 0) {
      $("#installmentConfirmBtn").attr("disabled", false);
      nsDialog.jAlert("实付金额必须大于0");
      return;
    }

    var orderTotalAmount = dataTransition.rounding($("#installmentPeriodPayRemain").val(), 2);
    var installmentPeriodPay = dataTransition.rounding($("#installmentPeriodPay").val(), 2);
    payableAmount = dataTransition.rounding(payableAmount, 2)

    if (payableAmount > orderTotalAmount && !GLOBAL.Number.equalsTo(payableAmount, orderTotalAmount)) {
      $("#confirmBtn").attr("disabled", false);
      nsDialog.jAlert("实付金额不能大于剩余总应付款");
      return;
    }

    if (payableAmount < installmentPeriodPay && !GLOBAL.Number.equalsTo(payableAmount, installmentPeriodPay)) {
      $("#confirmBtn").attr("disabled", false);
      nsDialog.jAlert("本次至少付款" + installmentPeriodPay + "元");
      return;
    }

    $("#relationId").val($("#installmentPayRelationId").val());
    $("#payTotalAmount").val(orderTotalAmount);
    $("#paidAmount").val(payableAmount);
    $("#paymentMethod").val("ONLINE_PAYMENT");

    $("#payForm").attr('action', 'bcgogoReceivable.do?method=instalmentOnLineReceivable');
    $("#payForm").submit();

  });

  $("#otherConfirmBtn").click(function() {
    $("#otherConfirmBtn").attr("disabled", "disabled");
    var payableAmount = $("#otherPayableAmount").val();

    if (payableAmount == "" || payableAmount == null) {
      $("#otherConfirmBtn").attr("disabled", false);
      nsDialog.jAlert("请输入实付款金额");
      return;
    }
    payableAmount = payableAmount * 1;

    if (payableAmount <= 0) {
      $("#otherConfirmBtn").attr("disabled", false);
      nsDialog.jAlert("实付金额必须大于0");
      return;
    }

    var orderTotalAmount = $("#softTotalAmount").text() * 1;


    if (payableAmount > orderTotalAmount && !GLOBAL.Number.equalsTo(payableAmount, orderTotalAmount)) {
      $("#otherConfirmBtn").attr("disabled", false);
      nsDialog.jAlert("实付金额不能大于应付金额");
      return;
    }

    $("#payTotalAmount").val(orderTotalAmount);
    $("#paidAmount").val(payableAmount);
    $("#paymentMethod").val("ONLINE_PAYMENT");
    $("#receivableMethod").val("UNCONSTRAINED");

    $("#payForm").attr('action', 'bcgogoReceivable.do?method=softwareOnlineReceivable');
    $("#payForm").submit();
  });


});


function getSearchDate() {
  var data = {
    "currentPage": 1
  }
  return data;
}

function getPaidRecordTable() {
  $("#paidRecordTable tr:not(:first)").remove();
  $("#paidRecordCount").text("0");
  $("#paidRecordTotal").text("0");
  var url = "bcgogoReceivable.do?method=getBcgogoReceivedList";
  var data = {startDateStr:$("#startDate").val(),endDateStr:$("#endDate").val()};
  bcgogoAjaxQuery.setUrlData(url, data);
  bcgogoAjaxQuery.ajaxQuery(function (json) {
    initPaidRecordTable(json);
  });
}


function getPayableRecordTable() {

  $("#payableRecordCount").text("0");
  $("#payableRecordTotal").text("0");
  $("#payableRecordTable tr:not(:first)").remove();

  var url = "bcgogoReceivable.do?method=getBcgogoReceivableList";
  var data = {};
  bcgogoAjaxQuery.setUrlData(url, data);
  bcgogoAjaxQuery.ajaxQuery(function (json) {
    initPayableRecordTable(json);
  });
}


function displayHardWarePayDiv(orderTotalAmount, content, bcgogoReceivableOrderRecordRelationIdStr) {
  orderTotalAmount = (orderTotalAmount ?dataTransition.simpleRounding(orderTotalAmount,2) : "0");

  $("#hardwareTotalAmount").text(orderTotalAmount);
  $("#hardwareContent").text(content);

  $("#relationId").val(bcgogoReceivableOrderRecordRelationIdStr);
  $("#payTotalAmount").val(orderTotalAmount);
  $("#paidAmount").val(orderTotalAmount);
  $("#paymentMethod").val("ONLINE_PAYMENT");

  $("#hardwarePayDiv").css("display", "block").dialog({ width: 600, modal: true});
}


function displayOtherPayDiv(orderTotalAmount, content, bcgogoReceivableOrderRecordRelationIdStr) {
  orderTotalAmount = (orderTotalAmount ?dataTransition.simpleRounding(orderTotalAmount,2) : "0");

  $("#softTotalAmount").text(orderTotalAmount);
  $("#otherPayableAmount").val(orderTotalAmount);

  $("#relationId").val(bcgogoReceivableOrderRecordRelationIdStr);
  $("#payTotalAmount").val(orderTotalAmount);
  $("#paymentMethod").val("ONLINE_PAYMENT");

  $("#softwareOtherPayDiv").css("display", "block").dialog({ width: 600, modal: true});
}



function displayInstallmentPayDiv(orderTotalAmount, orderReceivableAmount, instalmentPlanId, bcgogoReceivableOrderRecordRelationIdStr) {

  $("#firstInstallmentTable tr:not(:first)").remove();
  $("#secondInstallmentTable tr:not(:first)").remove();
  $("#installmentPayRelationId").val(bcgogoReceivableOrderRecordRelationIdStr);

  var url = "bcgogoReceivable.do?method=getInstalmentPlanDetails";
  var data = {instalmentPlanId: instalmentPlanId};
  bcgogoAjaxQuery.setUrlData(url, data);
  bcgogoAjaxQuery.ajaxQuery(function (json) {
    initInstallmentTable(orderTotalAmount, orderReceivableAmount, json);
  });
}


function initInstallmentTable(orderTotalAmount, orderReceivableAmount, json) {
  if (json == null || !json.success) {
    return;
  }

  var totalPeriods = json.total;

  $("#softwareTotalAmount").text(orderTotalAmount);
  $("#totalPeriods").text(totalPeriods);
  $("#installmentPeriodPayRemain").val(orderReceivableAmount);

  var installmentPeriodPay = 0;
  var getPeriodPay = false;

  if (totalPeriods == 6) {
    $("#secondInstallment").css("display", "none");
    var firstPercentTdStr = "<tr id='firstPercentTd'><td class='tabTit'>支付比例</td>",firstEachPayStr = "<tr id='firstEachPay'><td class='tabTit'>付款金额</td>",
        firstPayStatusStr = "<tr id='firstPayStatus'><td class='tabTit'>付款状态</td>",firstDeadlineStr = "<tr id='firstDeadline'><td class='tabTit'>付款截止</td>";
    $.each(json.data, function(index, order) {
      firstPercentTdStr += "<td>" + order.proportion * 100 + "%</td>";
      firstEachPayStr += "<td>" + order.currentAmount + "</td>";
      if (order.status == "FULL_PAYMENT") {
        firstPayStatusStr += "<td>" + order.statusValue + "</td>";
        firstDeadlineStr += "<td>" + "--" + "</td>";
      } else if (order.status == "PARTIAL_PAYMENT") {

        if (!getPeriodPay) {
          installmentPeriodPay = order.payableAmount;
          getPeriodPay = true;
        }

        firstPayStatusStr += "<td><b class='red_color'>" + order.statusValue + "</b></td>";
        firstDeadlineStr += "<td><b class='red_color'>" + order.endTimeStr + "</b></td>";
      } else if (order.status == "NON_PAYMENT") {

        if (!getPeriodPay) {
          installmentPeriodPay = order.payableAmount;
          getPeriodPay = true;
        }
        firstPayStatusStr += "<td>" + order.statusValue + "</td>";
        firstDeadlineStr += "<td>" + order.endTimeStr + "</td>";
      }
    });
    firstPercentTdStr += "</tr>";
    firstEachPayStr += "</tr>";
    firstPayStatusStr += "</tr>";
    firstDeadlineStr += "</tr>";

    $("#firstInstallmentTable").append($(firstPercentTdStr));
    $("#firstInstallmentTable").append($(firstEachPayStr));
    $("#firstInstallmentTable").append($(firstPayStatusStr));
    $("#firstInstallmentTable").append($(firstDeadlineStr));

  } else if (totalPeriods == 12) {
    $("#secondInstallment").css("display", "block");
    var firstPercentTdStr = "<tr id='firstPercentTd'><td class='tabTit'>支付比例</td>",firstEachPayStr = "<tr id='firstEachPay'><td class='tabTit'>付款金额</td>",
        firstPayStatusStr = "<tr id='firstPayStatus'><td class='tabTit'>付款状态</td>",firstDeadlineStr = "<tr id='firstDeadline'><td class='tabTit'>付款截止</td>";
    var secondPercentTdStr = "<tr id='secondPercentTd'><td class='tabTit'>支付比例</td>",secondEachPayStr = "<tr id='secondEachPay'><td class='tabTit'>付款金额</td>",
        secondPayStatusStr = "<tr id='secondPayStatus'><td class='tabTit'>付款状态</td>",secondDeadlineStr = "<tr id='secondDeadline'><td class='tabTit'>付款截止</td>";

    $.each(json.data, function(index, order) {

      if (index <= 5) {
        firstPercentTdStr += "<td>" + order.proportion * 100 + "%</td>";
        firstEachPayStr += "<td>" + order.currentAmount + "</td>";
        if (order.status == "FULL_PAYMENT") {
          firstPayStatusStr += "<td>" + order.statusValue + "</td>";
          firstDeadlineStr += "<td>" + "--" + "</td>";
        } else if (order.status == "PARTIAL_PAYMENT") {

          if (!getPeriodPay) {
            installmentPeriodPay = order.payableAmount;
            getPeriodPay = true;
          }

          firstPayStatusStr += "<td><b class='red_color'>" + order.statusValue + "</b></td>";
          firstDeadlineStr += "<td><b class='red_color'>" + order.endTimeStr + "</b></td>";
        } else if (order.status == "NON_PAYMENT") {

          if (!getPeriodPay) {
            installmentPeriodPay = order.payableAmount;
            getPeriodPay = true;
          }
          firstPayStatusStr += "<td>" + order.statusValue + "</td>";
          firstDeadlineStr += "<td>" + order.endTimeStr + "</td>";
        }
      } else {
        secondPercentTdStr += "<td>" + order.proportion * 100 + "%</td>";
        secondEachPayStr += "<td>" + order.currentAmount + "</td>";

        if (order.status == "FULL_PAYMENT") {
          secondPayStatusStr += "<td>" + order.statusValue + "</td>";
          secondDeadlineStr += "<td>" + "--" + "</td>";
        } else if (order.status == "PARTIAL_PAYMENT") {

          if (!getPeriodPay) {
            installmentPeriodPay = order.payableAmount;
            getPeriodPay = true;
          }

          secondPayStatusStr += "<td><b class='red_color'>" + order.statusValue + "</b></td>";
          secondDeadlineStr += "<td><b class='red_color'>" + order.endTimeStr + "</b></td>";
        } else if (order.status == "NON_PAYMENT") {

          if (!getPeriodPay) {
            installmentPeriodPay = order.payableAmount;
            getPeriodPay = true;
          }
          secondPayStatusStr += "<td>" + order.statusValue + "</td>";
          secondDeadlineStr += "<td>" + order.endTimeStr + "</td>";
        }
      }
    });


    $("#installmentPeriodPay").val(installmentPeriodPay);
    $("#installmentPayAmount").val(installmentPeriodPay);
    firstPercentTdStr += "</tr>";
    firstEachPayStr += "</tr>";
    firstPayStatusStr += "</tr>";
    firstDeadlineStr += "</tr>";

    secondPercentTdStr += "</tr>";
    secondEachPayStr += "</tr>";
    secondPayStatusStr += "</tr>";
    secondDeadlineStr += "</tr>";

    $("#firstInstallmentTable").append($(firstPercentTdStr));
    $("#firstInstallmentTable").append($(firstEachPayStr));
    $("#firstInstallmentTable").append($(firstPayStatusStr));
    $("#firstInstallmentTable").append($(firstDeadlineStr));

    $("#secondInstallmentTable").append($(secondPercentTdStr));
    $("#secondInstallmentTable").append($(secondEachPayStr));
    $("#secondInstallmentTable").append($(secondPayStatusStr));
    $("#secondInstallmentTable").append($(secondDeadlineStr));


  }

  $("#installmentPayDiv").css("display", "block").dialog({ width: 700, modal: true});
}


function displaySoftWarePayDiv(orderTotalAmount, bcgogoReceivableOrderRecordRelationIdStr) {
  orderTotalAmount = (orderTotalAmount ? dataTransition.simpleRounding(orderTotalAmount,2) : "0");

  $("#fullPay").click();

  $("#orderTotalAmount").text(orderTotalAmount);

  $("#firstPayRelationId").val(bcgogoReceivableOrderRecordRelationIdStr);
  var firstAmount = dataTransition.simpleRounding(orderTotalAmount * 0.12,0);
  var secondAmount = dataTransition.simpleRounding(orderTotalAmount * 0.08,0);

  var tr = "";

  tr += "<tr>";
  tr += '<td class="tabTit">支付比例</td>';
  tr += '<td>12%</td>';
  tr += '<td>8%</td>';
  tr += '<td>8%</td>';
  tr += '<td>8%</td>';
  tr += '<td>8%</td>';
  tr += '<td>8%</td>';
  tr += '<td>8%</td>';
  tr += '<td>8%</td>';
  tr += '<td>8%</td>';
  tr += '<td>8%</td>';
  tr += '<td>8%</td>';
  tr += '<td>8%</td>';
  tr += '</tr>';

  tr += "<tr class='tabTit'>";
  tr += "<td>应付金额</td>";
  tr += "<td id='firstAmountTd'>" + firstAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";

  tr += "</tr>";
  tr += "<tr><td class='tabTit'>合计</td><td colspan='12'><span class='yellow_color'>" + orderTotalAmount + "</span></td></tr>";

  $("#secondInstalmentTable tr:not(:first)").remove();
  $("#secondInstalmentTable").append($(tr));
  firstAmount =  dataTransition.simpleRounding(orderTotalAmount * 0.20,0);
  secondAmount =   dataTransition.simpleRounding(orderTotalAmount * 0.16,0);
  tr = "";
  tr += "<tr>";
  tr += '<td class="tabTit">支付比例</td>';
  tr += '<td>20%</td>';
  tr += '<td>16%</td>';
  tr += '<td>16%</td>';
  tr += '<td>16%</td>';
  tr += '<td>16%</td>';
  tr += '<td>16%</td>';
  tr += '</tr>';

  tr += "<tr class='tabTit'>";
  tr += "<td>应付金额</td>";
  tr += "<td id='firstAmountTdSix'>" + firstAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "<td>" + secondAmount + "</td>";
  tr += "</tr>";

  tr += "</tr>";
  tr += "<tr><td class='tabTit'>合计</td><td colspan='6'><span class='yellow_color'>" + orderTotalAmount + "</span></td></tr>";
  $("#firstInstalmentTable tr:not(:first)").remove();
  $("#firstInstalmentTable").append($(tr));


  $("#softwarePayDiv").css("display", "block").dialog({ width: 700, modal: true});
}

function initPaidRecordTable(json) {
  $("#paidRecordTable tr:not(:first)").remove();

  if (json == null || !json.success) {
    var tr = '<tr><td colspan="4">对不起，暂无已支付记录！</td></tr>';
    $("#paidRecordTable").append($(tr));
    return;
  }
  $("#paidRecordCount").text(json.total);
  $("#paidRecordTotal").text(json.title);

  $.each(json.data, function(index, order) {
    var tr = "";
    tr += "<tr class='payBg'>";
    tr += "<td style='padding-left:10px;'>" + (index + 1) + "</td>";

//    if (order.receivableMethod == "UNCONSTRAINED") {
//
//
//      tr += "<td>" + dateUtil.formatDate(new Date(order.submitTime), dateUtil.dateStringFormatDayHourMin) + "</td>";
//    } else {
//      tr += "<td>" + dateUtil.formatDate(new Date(order.auditTime), dateUtil.dateStringFormatDayHourMin) + "</td>";
//    }
      tr += "<td>" + dateUtil.formatDate(new Date(order.recordPaymentTime), dateUtil.dateStringFormatDay) + "</td>";


    if (order.paymentType == "SOFTWARE") {
      tr += "<td>软件费用</td>";
    } else if (order.paymentType == "HARDWARE") {
      tr += "<td>硬件费用</td>";
    } else if (order.paymentType == "SMS_RECHARGE") {
      tr += "<td>短信充值</td>";
    } else {
      tr += "<td>其他费用</td>";
    }
    tr += "<td>" + order.receivableContent + "</td>";

    if (order.receivableMethod == "FULL") {
      tr += "<td>全额付款</td>";
    } else if (order.receivableMethod == "INSTALLMENT") {
      tr += "<td>分期付款</td>";
    }else if (order.receivableMethod == "UNCONSTRAINED") {
      tr += "<td>其他付款</td>";
    } else {
      tr += "<td>--</td>";
    }

    tr += "<td>" + order.recordPaidAmount + "</td>";
    tr += "<td>已支付</td>";
    tr += "</tr>";

    $("#paidRecordTable").append($(tr));
  });
  var str = '<tr class="space"><td colspan="9"></td></tr>';
  $("#paidRecordTable").append($(str));


}


function initPayableRecordTable(json) {

  if (json == null || !json.success) {
    var tr = '<tr><td colspan="4">对不起，暂无待支付记录！</td></tr>';
    $("#payableRecordTable").append($(tr));
    return;
  }
  $("#payableRecordCount").text(json.total);
  $("#payableRecordTotal").text(json.title);

  $.each(json.data, function(index, order) {
    var tr = "";
    tr += "<tr class='payBg'>";
    tr += "<td style='padding-left:10px;'>" + (index + 1) + "</td>";

    if (order.paymentType == "SOFTWARE") {

      if (order.receivableMethod == "UNCONSTRAINED") {
        tr += "<td>--</td>";
      } else {
        tr += "<td>" + dateUtil.formatDate(new Date(order.currentInstalmentPlanEndTime), dateUtil.dateStringFormatDayHourMin) + "</td>";
      }

      tr += "<td>软件费用</td>";
    } else if (order.paymentType == "HARDWARE") {
      tr += "<td>--</td>";
      tr += "<td>硬件费用</td>";
    } else if (order.paymentType == "SMS_RECHARGE") {
      tr += "<td>" + order.time + "</td>";
      tr += "<td>短信充值</td>";
    } else {
      tr += "<td>--</td>";
      tr += "<td>其他费用</td>";
    }

    var lastPeriod = order.periodNumber - 1;
    if (order.receivableMethod == "FULL") {
      tr += "<td>" + order.receivableContent + "</td>";

      tr += "<td><div>首次付款</div>";
      tr += "<div>应付金额：¥" + order.orderTotalAmount + "</div></td>";
    } else if (order.receivableMethod == "INSTALLMENT") {
      tr += "<td>" + order.receivableContent + "<div>【已付:¥" + order.orderReceivedAmount + "】" + "【剩余:¥" + order.orderReceivableAmount + "】</div>";
      tr += "<div>分期付款：共" + order.periods + "期,已付" + lastPeriod + "期</td>";

      tr += "<td><div>分期付款：第" + order.periodNumber + "期 共¥" + order.recordPaymentAmount + "</div>";
      tr += "<div>已付金额:¥" + order.orderReceivedAmount + "</div>" + "<div>应付金额:" + order.recordPaymentAmount + "</div>"
      tr += "</td>";

    } else if (order.receivableMethod == "UNCONSTRAINED") {
      tr += "<td>" + order.receivableContent + "<div>【已付:¥" + order.orderReceivedAmount + "】" + "【剩余:¥" + order.orderReceivableAmount + "】</div></td>";
      tr += "<td><div>已付金额:¥" + order.orderReceivedAmount + "</div>" + "<div>应付金额:" + order.recordPaymentAmount + "</div></td>";
    }



    tr += "<td>待支付</td>";

    var str = "'" + order.receivableContent + "'";
    var planId = "'" + order.instalmentPlanIdStr + "'";
    var bcgogoReceivableOrderRecordRelationIdStr = "'" + order.bcgogoReceivableOrderRecordRelationIdStr + "'";
    if (order.paymentType == "SOFTWARE" && order.receivableMethod == "FULL") {
      tr += '<td><a class="blue_color"  onclick="displaySoftWarePayDiv(' + order.orderTotalAmount + ',' + bcgogoReceivableOrderRecordRelationIdStr + ');">付款</a></td>';
    } else if (order.paymentType == "SOFTWARE" && order.receivableMethod == "INSTALLMENT") {


      tr += '<td><a class="blue_color" href="#" onclick="displayInstallmentPayDiv(' + order.orderTotalAmount + ',' + order.orderReceivableAmount + ',' + planId + ',' + bcgogoReceivableOrderRecordRelationIdStr + ');">付款</a></td>';
    } else if (order.paymentType == "HARDWARE") {
      tr += '<td><a class="blue_color" href="#" onclick="displayHardWarePayDiv(' + order.orderTotalAmount + ',' + str + ',' + bcgogoReceivableOrderRecordRelationIdStr + ');">付款</a></td>';
    } else if (order.paymentType == "SMS_RECHARGE") {
      tr += "<td></td>";
    } else if(order.paymentType == "SOFTWARE" && order.receivableMethod == "UNCONSTRAINED") {
      tr += '<td><a class="blue_color" href="#" onclick="displayOtherPayDiv(' + order.recordPaymentAmount + ',' + str + ',' + bcgogoReceivableOrderRecordRelationIdStr + ');">付款</a></td>';
    } else {
      tr += "<td></td>";
    }

    tr += "</tr>";

    $("#payableRecordTable").append($(tr));
  });

  var str = '<tr class="space"><td colspan="9"></td></tr>';
  $("#payableRecordTable").append($(str));


}

