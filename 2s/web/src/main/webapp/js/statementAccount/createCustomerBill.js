/**
 * 创建对账单页面专用js
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-9
 * Time: 上午9:46
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(function() {

  $("#endDatdddeStr,#startDateddStr").datetimepicker({
        "numberOfMonths":1,
        "showButtonPanel":true,
        "changeYear":true,
        "changeMonth":true,
        "yearRange":"c-100:c+100",
        "yearSuffix":"",
        "onClose":function (dateText, inst) {
            if (!$(this).val()) {
                return;
            }
            if ($("#endDateStr").val() && $("#startDateStr").val() > $("#endDateStr").val()) {
                alert("预约出厂时间不能早于进厂时间，请修改!");
                $("#endDateStr").val($("#startDateStr").val());
                return;
            }
            if (G.getDate($("#startDateStr").val()).getTime() - G.getDate(G.getCurrentFormatDate()).getTime() > 0) {
                alert("请选择今天之前的时间。");
                $("#startDateStr").val(G.getCurrentFormatDate());
                return;
            }
        },
        "onSelect":function (dateText, inst) {
            if (inst.lastVal == dateText) {
                return;
            }
            $(this).val(dateText);
            var This = inst.input;
            if (inst.id == "startDateStr") {
                //如果选在非当前的时间 提醒逻辑
                if (!This.val()) return;
                if ((G.getDate(G.getCurrentFormatDate()).getTime() - new Date(Date.parse(This.val().replace(/-/g, "/"))).getTime() > 0)) {
                    $("#dialog-confirm-invoicing").dialog('open');
                }
            }
        }
    });

  $("#endTimeStr").datepicker({
        "numberOfMonths":1,
        "showButtonPanel":false,
        "changeYear":true,
        "showHour":false,
        "showMinute":false,
        "changeMonth":true,
        "yearRange":"c-100:c+100",
        "yearSuffix":"",
        "onSelect":function (dateText, inst) {
          $(this).blur();
          $("#createStatementAccount").click();
        }
      });

  $("#createStatementAccount").bind("click", function() {
    $("#statementAccountBtn").css("display", "none");
    $("#jsonStr").val("");
    var param = $("#statementOrderAccountForm").serializeArray();
    var paramJson = {};
    $.each(param, function(index, val) {
      paramJson[val.name] = val.value;
    });
    APP_BCGOGO.Net.asyncAjax({
      url:"statementAccount.do?method=getCurrentStatementAccountOrder",
      data:paramJson,
      cache:false,
      dataType:"json",
      success:function(json) {
        initReceivablePayable(json);
      }
    });
  });
  $("#createStatementAccount").click();


  $("#statementAccountBtn").live("click", function() {
    var totalDebtText = $("#totalDebtText").text();

    if(totalDebtText !="应收" && totalDebtText !="应付"){
      nsDialog.jAlert("不能结算！");
      return;
    }

    bcgogo.checksession({
      parentWindow:window.parent,
      iframe_PopupBox:$("#iframe_PopupBox_account")[0],
      src:"statementAccount.do?method=statementOrderAccount&customerId=" + $("#customerOrSupplierId").val() +"&orderDebtType=" +$("#orderDebtType").val()});
  });

  $(".lblChk").click(function(){
    var $tbody = $(this).parent().parent().parent();
    if($(this).attr("checked")) {
      $tbody.find('input[type=checkbox]').not('.lblChk').attr("checked",true);
    } else {
      if($tbody.children().eq(1).children().eq(2).attr("title") == '上期对账余额') {
        $tbody.find('input[type=checkbox]').not('.lblChk').not(':first').attr("checked",false);
      } else {
        $tbody.find('input[type=checkbox]').not('.lblChk').attr("checked",false);
      }

    }

  });

  $("input[class=subBox]").live("click",function(){
     var $tbody = $(this).parent().parent().parent();
     var $subCheckbox =  $tbody.find('input[type=checkbox]').not('.lblChk');

     $subCheckbox.each(function(index){
        if($(this).attr("checked") == false) {
          $tbody.find('.lblChk').attr("checked",false);
          return false;
        } else {
          if(index == $subCheckbox.length - 1) {
            $tbody.find('.lblChk').attr("checked",true);
          }
        }
     });


  });

  $("input[type=checkbox]").live("click",function(){
     var totalReceivable = 0.0;
     var totalPayable = 0.0;
     var total;
     $("#receivableList input[class=subBox]").each(function(){
        if($(this).attr("checked")) {
           totalReceivable += ($(this).parent().parent().children().eq($(this).parent().parent().children().length-1).attr("title")*1);
        }
     });
     $("#payableList input[class=subBox]").each(function(){
        if($(this).attr("checked")) {
           totalPayable += ($(this).parent().parent().children().eq($(this).parent().parent().children().length-1).attr("title")*1);
        }
     });
     $("#totalReceivableSpan").html(totalReceivable);
     $("#totalPayableSpan").html(totalPayable);
     total =  totalReceivable - totalPayable;
     if(total >= 0) {
       $("#totalDebtText").html('应收');
       $("#totalDebt").html(total.toFixed(1));
       $("#total").val(total.toFixed(1));

       if ($("#orderType").val() == "CUSTOMER_STATEMENT_ACCOUNT") {
         $("#orderDebtType").val("CUSTOMER_DEBT_RECEIVABLE");
       } else if ($("#orderType").val() == "SUPPLIER_STATEMENT_ACCOUNT") {
         $("#orderDebtType").val("SUPPLIER_DEBT_RECEIVABLE");
       }


     } else {
       $("#totalDebtText").html('应付');
       $("#totalDebt").html((totalPayable - totalReceivable).toFixed(1));
       $("#total").val((totalPayable - totalReceivable).toFixed(1));

       if ($("#orderType").val() == "CUSTOMER_STATEMENT_ACCOUNT") {
         $("#orderDebtType").val("CUSTOMER_DEBT_PAYABLE");
       } else if ($("#orderType").val() == "SUPPLIER_STATEMENT_ACCOUNT") {
         $("#orderDebtType").val("SUPPLIER_DEBT_PAYABLE");
       }
     }

  });


});


function initReceivablePayable(data) {
  $("#receivableList tr:not(:first)").remove();
  $("#payableList tr:not(:first)").remove();
  $("#totalReceivableSpan").text("0");
  $("#totalPayableSpan").text("0");
  $("#totalDebt").text("0");

  if (data == null || data[0] == null) {
    return;
  }
  if (data[1] != undefined) {
    $("#jsonStr").val(data[1].toString());
  }
  var height = "0px";
  if (data[0].resultSize <= 10) {
    height = (data[0].resultSize * 40 + 10 ) + "px" ;
  } else {
    height = "400px";
  }
  $("#noPayableList").css("height", height);
  $("#payableDiv").css("height", height);
  $("#noReceivableList").css("height", height);
  $("#receivableDiv").css("height", height);

  if (data[0].receivableList == null || data[0].receivableList.length == 0) {
    $("#noReceivableList").css("display", "block");
    $("#receivableDiv").css("display", "none");
  } else {
    $("#statementAccountBtn").css("display", "block");
    $("#noReceivableList").css("display", "none");
    $("#receivableDiv").css("display", "block");
    initTable(data[0].receivableList, data[0].totalReceivable, "#receivableList", "#totalReceivableSpan");
    $("#totalReceivable").val(data[0].totalReceivable);
  }
  if (data[0].payList == null || data[0].payList.length == 0) {
    $("#noPayableList").css("display", "block");
    $("#payableDiv").css("display", "none");
  } else {
    $("#noPayableList").css("display", "none");
    $("#payableDiv").css("display", "block");
    $("#statementAccountBtn").css("display", "block");
    initTable(data[0].payList, data[0].totalPayable, "#payableList", "#totalPayableSpan");
    $("#totalPayable").val(data[0].totalPayable);
  }
  $("#orderDebtType").val(data[0].orderDebtType);
  if (data[0].totalDebt >= 0) {
    $("#totalDebtText").text("应收");
  } else {
    data[0].totalDebt = 0 - data[0].totalDebt;
    $("#totalDebtText").text("应付");
  }
  $("#total").val(data[0].totalDebt);
  $("#totalDebt").text(data[0].totalDebt);
  $("#startDateStr").val(data[0].startDate);
  $("#endDateStr").val(data[0].endDate);
}

function initTable(data, total, tableId, totalId) {
  $.each(data, function(index, order) {

    var orderTypeStr = (!order.orderTypeStr ? "--" : order.orderTypeStr);
    var receiptNo = (!order.receiptNo ? "--" : order.receiptNo);
    var orderIdStr = (!order.orderIdStr ? "--" : order.orderIdStr);
    var orderTotal = (order.total == null ? "--" : order.total);
    var settledAmount = (order.settledAmount == null ? "--" : order.settledAmount );
    var discount = (order.discount == null ? "--" : order.discount );
    var debt = (order.debt == null ? "--" : order.debt);
    var vestDateStr = (!order.vestDateStr ? "" : order.vestDateStr);
    var url = getUrlByData(order.orderIdStr, order.orderType);
    var tr = "<tr>";
    if(orderTypeStr == '上期对账余额') {
      tr += '<td style="padding-left:8px;"><input id=' + orderIdStr +' type="checkbox" class="subBox" checked="checked" disabled="true"></td>';
    } else {
      tr += '<td style="padding-left:8px;"><input id=' + orderIdStr +' type="checkbox" class="subBox" checked="checked"></td>';
    }
    tr += "<td title='" + vestDateStr + "'>" + vestDateStr + "</td>";
    tr += "<td title='" + orderTypeStr + "'>" + orderTypeStr + "</td>";
    if(url != null){
        tr += "<td title='" + receiptNo + "'><a href='"+ url +"' target='_blank' class='blue_col'>" + receiptNo + "</a></td>";
    }else{
        tr += "<td title='" + receiptNo + "'>" + receiptNo + "</td>";
    }
    tr += "<td title='" + orderTotal + "' style='text-align:center'>" + orderTotal + "</td>";
    tr += "<td title='" + settledAmount + "'>" + settledAmount + "</td>";
    tr += "<td title='" + discount + "'>" + discount + "</td>";
    tr += "<td class='red_color' title='" + debt + "'>" + debt + "</td>";
    tr += "</tr>";
    $(tableId).append($(tr));
  });
  $(totalId).text(total);
}

function getUrlByData(orderId, orderType){
    var orderTypeUrlMapping = {
        PURCHASE: "RFbuy.do?method=show&id=",
        INVENTORY: "storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=",
        RETURN: "goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=",
        SALE: "sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=",
        SALE_RETURN: "salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=",
        REPAIR:"txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=",
        WASH_BEAUTY: "washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=",
        CUSTOMER_STATEMENT_ACCOUNT: "statementAccount.do?method=showStatementAccountOrderById&statementOrderId=",
        SUPPLIER_STATEMENT_ACCOUNT: "statementAccount.do?method=showStatementAccountOrderById&statementOrderId="
    };
    if(orderTypeUrlMapping[orderType]){
        return orderTypeUrlMapping[orderType] + orderId;
    }else{
        return null;
    }
}
