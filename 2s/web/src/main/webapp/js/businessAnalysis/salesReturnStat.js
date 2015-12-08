function map() {
    var struct = function(key, value) {
        this.key = key;
        this.value = value;
    }

    var put = function(key, value) {
            for(var i = 0; i < this.arr.length; i++) {
                if(this.arr[i].key === key) {
                this.arr[i].value = value;
                return;
            }
        }
        this.arr[this.arr.length] = new struct(key, value);
    }

    var get = function(key) {
            for(var i = 0; i < this.arr.length; i++) {
                if(this.arr[i].key === key) {
                return this.arr[i].value;
            }
        }
        return null;
    }

    var remove = function(key) {
        var v;
            for(var i = 0; i < this.arr.length; i++) {
            v = this.arr.pop();
                if(v.key === key) {
                break;
            }
            this.arr.unshift(v);
        }
    }

    var size = function() {
        return this.arr.length;
    }

    var isEmpty = function() {
        return this.arr.length <= 0;
    }

    var clearMap = function() {
        this.arr = [];
    }
    this.arr = new Array();
    this.get = get;
    this.put = put;
    this.remove = remove;
    this.size = size;
    this.isEmpty = isEmpty;
    this.clearMap = clearMap;
}
var jsonStrMap = new map();


var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$(document).ready(function() {

  $("#statistics").click(function(e) {
    $("#salesReturnTable tr:not(:first)").remove();
    $("#orderTotal").text("0");
    $("#settledTotal").text("0");
    $("#debtTotal").text("0");
    $("#discountTotal").text("0");
    e.preventDefault();
    var param = $("#itemStatisticsForm").serializeArray();
    var paramJson = {};
    $.each(param, function(index, val) {
      paramJson[val.name] = val.value;
    });

    initSalesReturn(paramJson);
  });

  $("#print_div_saleReturn,#print_div_inventoryReturn").bind("click",function(){
      var jsonData = null;
      var name=""
      if(this.id=="print_div_saleReturn")
      {
          jsonData = jsonStrMap.get("salesReturnData");
          name = $("#customerName").val();
          if("客户名"==name)
          {
              name = "";
          }
      }
      else
      {
          jsonData = jsonStrMap.get("inventoryReturnData");

          name = $("#supplierName").val();
          if("供应商名"==name)
          {
              name = "";
          }
      }
      if(!jsonData || jsonData.length == 0 || !jsonData.orders)
      {
        nsDialog.jAlert("无数据，不能打印")
      }

      var mobile = $("#mobile").val();
      if("手机号"==mobile)
      {
          mobile = "";
      }
      var data = {
          dataList:JSON.stringify(jsonData),
          name:name,
          mobile:mobile,
          startTimeStr:$("#startDate").val(),
          endTimeStr:$("#endDate").val(),
          now:new Date()
      };

      var url = "businessAnalysis.do?method=getDataToPrint";
      $.ajax({
          url: url,
          data: data,
          type: "POST",
          cache: false,
          success: function(data) {
              if(!data) return;

              var printWin = window.open("", "", "width=1024,height=768");

              with(printWin.document) {
                  open("text/html", "replace");
                  write(data);
                  close();
              }
          }
      });
  });
});

function initSalesReturn(paramJson) {

  var ajaxUrl = "businessAnalysis.do?method=getSalesReturnList";

  if($("#statType").val() =="inventoryReturnStatistics"){
      ajaxUrl = "businessAnalysis.do?method=getInventoryReturnList";
  }else if($("#statType").val() =="salesReturnStatistics"){
     ajaxUrl = "businessAnalysis.do?method=getSalesReturnList";
  }else{
    return;
  }

  bcgogoAjaxQuery.setUrlData(ajaxUrl, paramJson);
  bcgogoAjaxQuery.ajaxQuery(function(json) {
    initSalesReturnByJson(json);
    initPages(json, "dynamicalSalesReturn", ajaxUrl, '', "initSalesReturnByJson", '', '', paramJson, '');
  });
}

function initSalesReturnByJson(data) {
  $("#salesReturnTable tr:not(:first)").remove();
  if (data == null || data[0] == null || data[0].orders == null || data[0].orders == 0) {
    return;
  }

  if(data[0].orders[0].orderType == "SALE_RETURN")
  {
    jsonStrMap.put("salesReturnData",data[0]);
  }
  else
  {
    jsonStrMap.put("inventoryReturnData",data[0]);
  }

  $("#orderTotal").text(data[0].totalAmounts.ORDER_TOTAL_AMOUNT);
  $("#settledTotal").text(data[0].totalAmounts.ORDER_SETTLED_AMOUNT);
  $("#debtTotal").text(data[0].totalAmounts.ORDER_DEBT_AMOUNT);
  $("#discountTotal").text(data[0].totalAmounts.DISCOUNT);

  var orderTypeStr = null;
  $.each(data[0].orders, function(index, order) {

    var customerName = (!order.customerOrSupplierName ? "--" : order.customerOrSupplierName);
    var customerOrSupplierId = (!order.customerOrSupplierIdStr ? "" : order.customerOrSupplierIdStr);
    var orderIdStr = (!order.orderIdStr ? "" : order.orderIdStr);
    var orderType = (!order.orderType ? "" : order.orderType);
    var receiptNo = (!order.receiptNo ? "--" : order.receiptNo);
    var total = (order.amount == null ? "--" : order.amount);
    var settledAmount = (order.settled == null ? "--" : order.settled );
    var discount = (order.discount == null ? "--" : order.discount );
    var debt = (order.debt == null ? "--" : order.debt);
    var customerStatus = (!order.customerStatus ? "" : order.customerStatus);
    var vestDateStr = (!order.vestDateStr ? "" : order.vestDateStr);

    var originOrderIdStr = (!order.originOrderIdStr ? "" : order.originOrderIdStr);
    var originOrderType = (!order.originOrderType ? "" : order.originOrderType);
    var originReceiptNo = (!order.originReceiptNo ? "--" : order.originReceiptNo);

    orderTypeStr = orderType;
    var tr = '<tr class="table-row-original">';
    tr += "<td class='first-padding'>" + (index + 1) + "</td>";
    tr += "<td title='" + vestDateStr + "'>" + vestDateStr + "</td>";


    tr += "<td title='" + receiptNo + "'>" + '<a href ="#" class="blue_color" onclick="openTxnOrder(\'' + orderIdStr + '\',\'' + orderType + '\')">' + receiptNo + "</a> " + "</td> ";

    if ("DISABLED" == customerStatus) {
      tr += "<td  title='" + customerName + "'><span class=''>" + customerName + "</span></td>";
    } else {
      var type = "customer";
      if (orderType == "RETURN") {
        type = "supplier";
      }
      tr += "<td title='" + customerName + "'>" + '<a href ="#" class="blue_color" onclick="openCustomer(\'' + customerOrSupplierId + '\',\'' + type + '\')">' + customerName + "</a> " + "</td> ";
    }

    if (orderType == "SALE_RETURN") {
      if (order.originReceiptNo) {
        tr += "<td title='" + originReceiptNo + "'>" + '<a href ="#" class="blue_color" onclick="openTxnOrder(\'' + originOrderIdStr + '\',\'' + originOrderType + '\')">' + originReceiptNo + "</a> " + "</td> ";
      } else {
        tr += "<td>" + originReceiptNo + "</td>";
      }
    }


    tr += "<td title='" + total + "'>" + total + "</td>";
    tr += "<td title='" + settledAmount + "'>" + settledAmount + "</td>";
    tr += "<td title='" + debt + "'>" + debt + "</td>";
    tr += "<td title='" + discount + "'>" + discount + "</td>";
    tr += "</tr>";
    $("#salesReturnTable").append($(tr));
  });
  var lastStr = "";
  if (orderTypeStr == "SALE_RETURN") {
    lastStr = "<tr> <td colspan='5' style='text-align:center;'>本页小计：" + "</td>";

  } else if (orderTypeStr == "RETURN") {
    lastStr = "<tr> <td colspan='4' style='text-align:center;'>本页小计：" + "</td>";
  }

  lastStr += "<td>" + data[0].currentPageTotalAmounts.order_total_amount + "</td>";
  lastStr += "<td>" + data[0].currentPageTotalAmounts.order_settled_amount + "</td>";
  lastStr += "<td>" + data[0].currentPageTotalAmounts.order_debt_amount + "</td>";
  lastStr += "<td>" + data[0].currentPageTotalAmounts.discount + "</td> </tr>";
  $("#salesReturnTable").append($(lastStr));

  tableUtil.limitSpanWidth($(".customer", "#salesReturnTable"), 10);
  tableUtil.tableStyle('#salesReturnTable', '.titleBg');
}



