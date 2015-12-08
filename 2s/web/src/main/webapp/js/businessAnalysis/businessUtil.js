/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-1-30
 * Time: 下午3:46
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(function() {
  $("#customerName").css("color", "#9a9a9a");
  $("#mobile").css("color", "#9a9a9a");
  $("#supplierName").css("color", "#9a9a9a");
  $("#businessCategory").css("color", "#9a9a9a");
  $("#productCategory").css("color", "#9a9a9a");
  $("#my_date_thismonth").click();

  $("#startDate,#endDate")
      .datepicker({
        "numberOfMonths":1,
        "showButtonPanel":false,
        "changeYear":true,
        "showHour":false,
        "showMinute":false,
        "changeMonth":true,
        "yearRange":"c-100:c+100",
        "yearSuffix":"",
        "onSelect" :function(dateText, inst){
            if($("a[name='my_date_select']").length > 0) {
                $("a[name='my_date_select']").each(function(){
                    $(this).removeClass("clicked");
                });
                $("#my_date_self_defining").click();

            }

         }
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


  $("#customerName,#mobile,#supplierName,#businessCategory,#productCategory").bind("click", function() {
    if ("customerName" == this.id && "客户名" == $("#customerName").val()) {
      $("#customerName").val("");
      $("#customerName").css("color", "#000");
    }
    if ("mobile" == this.id && "手机号" == $("#mobile").val()) {
      $("#mobile").val("");
      $("#mobile").css("color", "#000");
    }
    if ("supplierName" == this.id && "供应商名" == $("#supplierName").val()) {
      $("#supplierName").val("");
      $("#supplierName").css("color", "#000");
    }
    if ("businessCategory" == this.id && "---所有营业分类---" == $("#businessCategory").val()) {
      $("#businessCategory").val("");
      $("#businessCategory").css("color", "#000");
    }
    if ("productCategory" == this.id && "---所有商品分类---" == $("#productCategory").val()) {
      $("#productCategory").val("");
      $("#productCategory").css("color", "#000");
    }

  });

  $("#customerName,#supplierName").bind('change', function () {
//    $("#mobile").val('');
    $("#customerId").val('');
    $("#supplierId").val('');
  });

    $("#statByType").bind("change", function() {
        $("#businessCategory").val('');
        $("#productCategory").val('');
        returnDefaultValue();
        $("#customerId").val('');
        $("#supplierId").val('');
        $("#businessCategory").parent().hide();
        $("#productAttr").hide();
        $('#serviceAndConstruction').parent().hide();
        $("#productCategory").parent().hide();
        $('#serviceNotItem').show();
        if ($(this).val() == "按商品分类统计") {
            $("#productCategory").parent().show();
        }else if ($(this).val() == "按营业分类统计"){
            $("#businessCategory").parent().show();
        }else if ($(this).val() == "按商品统计"){
            $("#productAttr").show();
        }else if($(this).val() == "服务/施工内容"){
            $('#serviceAndConstruction').parent().show();
            $('#serviceNotItem').hide();
        }
    });

  $("#customerName,#mobile,#supplierName,#statByType,#businessCategory, #serviceAndConstruction").bind("blur", function() {
    returnDefaultValue();
  });


  $("#customerStat").click(function() {
    window.location.href = "itemStat.do?method=getItemStat&type=customerStat";
  });

  $("#supplierStat").click(function() {
    window.location.href = "itemStat.do?method=getItemStat&type=supplierStat";
  });

  $("#categoryStat").click(function() {
    window.location.href = "itemStat.do?method=getItemStat&type=categoryStat";
  });
  $("#salesReturnStat").click(function() {
    window.location.href = "businessAnalysis.do?method=redirectSalesReturnStat";
  });
  $("#inventoryReturnStat").click(function() {
    window.location.href = "businessAnalysis.do?method=redirectInventoryReturnStat";
  });
});

function openTxnOrder(orderId, orderType) {
  if (orderType == "REPAIR") {
    window.open('txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderId);
  } else if (orderType == "SALE") {
    window.open('sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=' + orderId);
  } else if (orderType == "WASH_BEAUTY") {
    window.open('washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=' + orderId);
  } else if (orderType == "RETURN") {
    window.open('goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=' + orderId);
  } else if (orderType == "SALE_RETURN") {
    window.open('salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=' + orderId);
  }
}

function openCustomer(customerOrSupplierId, type) {
  if (type == "customer") {
    window.open('unitlink.do?method=customer&customerId=' + customerOrSupplierId);
  } else if (type == "supplier") {
    window.open('unitlink.do?method=supplier&supplierId=' + customerOrSupplierId);
  }
}


function returnDefaultValue() {
  if (!$("#customerName").val() || "客户名" == $("#customerName").val()) {
    $("#customerName").val("客户名");
    $("#customerName").css("color", "#9a9a9a");
  } else {
    $("#customerName").css("color", "#000");
  }

  if (!$("#mobile").val() || "手机号" == $("#mobile").val()) {
    $("#mobile").val("手机号");
    $("#mobile").css("color", "#9a9a9a");
  } else {
    $("#mobile").css("color", "#000");
  }

  if (!$("#supplierName").val() || "供应商名" == $("#supplierName").val()) {
    $("#supplierName").val("供应商名");
    $("#supplierName").css("color", "#9a9a9a");
  } else {
    $("#supplierName").css("color", "#000");
  }

  if (!$("#businessCategory").val() || "---所有营业分类---" == $("#businessCategory").val()) {
    $("#businessCategory").val("---所有营业分类---");
    $("#businessCategory").css("color", "#9a9a9a");
  } else {
    $("#businessCategory").css("color", "#000");
  }

    if (!$("#productCategory").val() || "---所有商品分类---" == $("#productCategory").val()) {
        $("#productCategory").val("---所有商品分类---");
        $("#productCategory").css("color", "#9a9a9a");
    } else {
        $("#productCategory").css("color", "#000");
    }

    if (!$("#serviceAndConstruction").val() || "服务/施工内容" == $("#serviceAndConstruction").val()) {
        $("#serviceAndConstruction").val("服务/施工内容");
        $("#serviceAndConstruction").css("color", "#9a9a9a");
    } else {
        $("#serviceAndConstruction").css("color", "#000");
    }

}



