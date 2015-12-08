bcgogoAjaxQuery  =   APP_BCGOGO.Module.wjl.ajaxQuery;
$().ready(function () {
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
        var currentSearchField =  $domObject.attr("searchField");
        var ajaxData = {
            searchWord: searchWord,
            searchField: currentSearchField,
            uuid: dropList.getUUID()
        };
        $domObject.parent().prevAll().each(function(){
            var $productSearchInput = $(this).find(".J-productSuggestion");
            if($productSearchInput && $productSearchInput.length>0){
                var val = $productSearchInput.val().replace(/[\ |\\]/g, "");
                if($productSearchInput.attr("name")!="searchWord"){
                    ajaxData[$productSearchInput.attr("name")] = val == $productSearchInput.attr("initialValue") ? "" : val;
                }
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
                        $domObject.parent().nextAll().each(function () {
                            var $productSearchInput = $(this).find(".J-productSuggestion");
                            if ($productSearchInput && $productSearchInput.length > 0) {
                                var val = $productSearchInput.val().replace(/[\ |\\]/g, "");
                                details[$productSearchInput.attr("searchField")] = val == $productSearchInput.attr("initialValue") ? "" : val;
                            }
                        });
                        return {
                            details:details
                        };
                    },
                    onSelect: function (event, index, data, hook) {
                        $domObject.parent().nextAll().each(function () {
                            var $productSearchInput = $(this).find(".J-productSuggestion");
                            if ($productSearchInput && $productSearchInput.length > 0) {
                                var label = data.details[$productSearchInput.attr("searchField")];
                                if (G.Lang.isEmpty(label) && $productSearchInput.attr("initialValue")) {
                                    $productSearchInput.val($productSearchInput.attr("initialValue"));
                                    $productSearchInput.css({"color": "#ADADAD"});
                                } else {
                                    $productSearchInput.val(G.Lang.normalize(label));
                                    $productSearchInput.css({"color": "#000000"});
                                }
                            }
                        });
                        dropList.hide();
                        $("#searchOnlinePurchaseOrderBtn").click(); // add by zhuj
                    },
                    onKeyboardSelect: function (event, index, data, hook) {
                        $domObject.parent().nextAll().each(function () {
                            var $productSearchInput = $(this).find(".J-productSuggestion");
                            if ($productSearchInput && $productSearchInput.length > 0) {
                                var label = data.details[$productSearchInput.attr("searchField")];
                                if (G.Lang.isEmpty(label) && $productSearchInput.attr("initialValue")) {
                                    $productSearchInput.val($productSearchInput.attr("initialValue"));
                                    $productSearchInput.css({"color": "#ADADAD"});
                                } else {
                                    $productSearchInput.val(G.Lang.normalize(label));
                                    $productSearchInput.css({"color": "#000000"});
                                }
                            }
                        });
                    }
                });
            }else{
                dropList.show({
                    "selector": $domObject,
                    "data": result,
                    "onSelect": function (event, index, data) {
                        $domObject.val(data.label);
                        $domObject.css({"color": "#000000"});
                        $domObject.parent().nextAll().each(function () {
                            var $productSearchInput = $(this).find(".J-productSuggestion");
                            if ($productSearchInput) {
                                clearSearchInputValueAndChangeCss($productSearchInput[0]);
                            }
                        });
                        dropList.hide();
                        $("#searchOnlinePurchaseOrderBtn").click(); // add by zhuj
                    }
                });
            }
        });
    }


    $("#startTimeStr,#endTimeStr").datepicker({
        "numberOfMonths": 1,
        "changeYear": true,
        "changeMonth": true,
        "dateFormat": "yy-mm-dd",
        "yearRange": "c-5, c",
        "yearSuffix": "",
        "showButtonPanel": true,
        "maxDate": 0,
        "onSelect":function(){
            $("#date_default").click();
        }
    });
    $("[name='date_select']").bind("click", function () {
        var id = $(this).attr("id");
        var now = new Date();
        var year = now.getFullYear();
        if(id == "date_this_day"){
            $("#startTimeStr").val(dateUtil.getToday());
            $("#endTimeStr").val(dateUtil.getToday());
        }else if (id == "date_this_week") {
            $("#startTimeStr").val(dateUtil.getWeekStartDate() );
            $("#endTimeStr").val(dateUtil.getWeekEndDate() );
        } else if (id == "date_this_month") {
            $("#startTimeStr").val(dateUtil.getMonthStartDate() );
            $("#endTimeStr").val(dateUtil.getMonthEndDate() );
        } else if (id == "date_this_year") {
            $("#startTimeStr").val(year + "-01-01");
            $("#endTimeStr").val(year + "-12-31");
        }
    });
    if ($("[name='date_select'][selected='checked']").size()<1) {
        $("#date_default").click();
    }



    $("#searchOnlinePurchaseOrderBtn").bind("click",function(){
        $(".J-initialCss").placeHolder("clear");
        var url = "onlineReturn.do?method=getOnlinePurchaseList";
        var data = getSearchCondition();
        bcgogoAjaxQuery.setUrlData(url, data);
              bcgogoAjaxQuery.ajaxQuery(function(json) {
                  drawPurchaseOrders(json);
                  initPages(json, "onlinePurchaseOrder", "onlineReturn.do?method=getOnlinePurchaseList", '', "drawPurchaseOrders", '', '',data, '');
              },function(){
                  drawPurchaseOrders();
                   initPages(null, "onlinePurchaseOrder", "onlineReturn.do?method=getOnlinePurchaseList", '', "drawPurchaseOrders", '', '',data, '');
              });
        $(".J-initialCss").placeHolder("reset");
    });
    $("#reset_btn").bind("click",function(){
        $("#product_commodity_code").val("");
        $("#supplierInfoText").val("");
        $("#mobile").val("");

        $("#searchWord").val("");
        $("#productBrand").val("");
        $("#productModel").val("");
        $("#productName").val("");
        $("#productSpec").val("");
        $("#productVehicleBrand").val("");
        $("#productVehicleModel").val("");
        $("#receiptNo").val("");

        $("#startTimeStr").val(dateUtil.getNDayCloseToday(-30));
        $("#endTimeStr").val(dateUtil.getToday());
        $("#date_default").click();
        $("#searchOnlinePurchaseOrderBtn").click();
    });
    $(".J-up-down").live("click",function(){


        if($(this).hasClass("up")){
            $(".down").click();
           $(this).removeClass("up").addClass("down").html("收拢");
            var $thisTable =  $(this).parent().parent().parent().parent();
            $thisTable.find("tr").not(".J-purchaseOrder-title").show();

            tableUtil.tableStyle("#"+$thisTable.attr("id"), ".J-purchaseOrder-title", "odd");
            tableUtil.setRowBackgroundColor("#"+$thisTable.attr("id"), null, ".J-purchaseOrder-title", "odd");
        }else if($(this).hasClass("down")){
            $(this).removeClass("down").addClass("up").html("展开");
            $(this).parent().parent().parent().find("tr").not(".J-purchaseOrder-title").hide();
        }
    });
    $(".J-jump-purchaseOrder").live("click",function(){
        var purchaseOrderId = $(this).attr("orderId");
        if(!GLOBAL.Lang.isEmpty(purchaseOrderId) && GLOBAL.Lang.isNumber(purchaseOrderId)){
            window.open("RFbuy.do?method=show&id="+purchaseOrderId,"_blank");
        }
    });
    $(".J-jump-onlineReturn").live("click",function(){
        var purchaseOrderId = $(this).attr("orderId");
        if(!GLOBAL.Lang.isEmpty(purchaseOrderId) && GLOBAL.Lang.isNumber(purchaseOrderId)){
            window.location.href = "onlineReturn.do?method=onlinePurchaseReturnEdit&purchaseOrderId="+purchaseOrderId   ;
        }
    });
    $(".J-jump-purchaseInventory").live("click",function(){
        var purchaseInventoryId = $(this).attr("orderId");
        if(!GLOBAL.Lang.isEmpty(purchaseInventoryId) && GLOBAL.Lang.isNumber(purchaseInventoryId)){
            window.open("storage.do?method=getPurchaseInventory&purchaseInventoryId="+purchaseInventoryId+"&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE","_blank");
        }
    });
    $("#searchOnlinePurchaseOrderBtn").click();
});
function drawPurchaseOrders(json){
    $("#purchaseOrderShow").html("");
    if (!json || !json[0] || !json[0]['orders']|| json[0]['orders'].length == 0) {
        $("#purchaseOrderShow").html("<span style='color: #000000'> 对不起，没有找到您要的单据信息！</span>");
        return;
    }
    var orders =  json[0].orders;
    var purchaseOrderHTML = '';
    for (var i = 0; i < orders.length; i++) {
        var receiptNo =  G.normalize(orders[i].receiptNo);
        var createdTimeStr =  G.normalize(orders[i].createdTimeStr);
        var supplierName =  G.normalize(orders[i].customerOrSupplierName);
        var orderStatusValue =  G.normalize(orders[i].orderStatusValue);
        var orderTotal = orders[i].amount;
        var purchaseInventoryReceiptNo =  G.normalize(orders[i].purchaseInventoryReceiptNo);
        var purchaseOrderId =  G.normalize(orders[i].orderIdStr);
        var purchaseInventoryId =  G.normalize(orders[i].purchaseInventoryIdStr);
        var isFirstResult = i == 0;
        purchaseOrderHTML += '<table cellpadding="0" cellspacing="0" class="tabSlip J-purchaseOrder" id="purchaseOrderTb' + i + '">';
        purchaseOrderHTML += '<col>';
        purchaseOrderHTML += '<col width="90">';
        purchaseOrderHTML += '<col width="90">';
        purchaseOrderHTML += '<col width="100">';
        purchaseOrderHTML += '<tr class="titleBg titleService J-purchaseOrder-title">';
        purchaseOrderHTML += '<td colspan="4">';
        purchaseOrderHTML += '<div class="titLbl" style="width:150px">单号：<a class="blue_color J-jump-purchaseOrder" orderId="'+purchaseOrderId+'">' + receiptNo + '</a></div>';
        purchaseOrderHTML += '<div class="titLbl" style="width:190px">下单时间：<span>' + createdTimeStr + '</span></div>';
        purchaseOrderHTML += '<div class="titLbl" style="width:200px">供应商：<span>' + supplierName + '</span></div>';
        purchaseOrderHTML += '<div class="titLbl" style="width:100px">状态：<span style="color: #FF5E04;">' + orderStatusValue + '</span></div>';
        purchaseOrderHTML += '<div class="titLbl">入库单号：<a class="blue_color J-jump-purchaseInventory" orderId="'+purchaseInventoryId+'">'+purchaseInventoryReceiptNo+'</a></div>';
        if(isFirstResult){
            purchaseOrderHTML += '<a class="down J-up-down">收拢</a>';
        }else{
            purchaseOrderHTML += '<a class="up J-up-down">展开</a>';
        }
        purchaseOrderHTML += '<a class="btns J-jump-onlineReturn" orderId="'+purchaseOrderId+'">退&nbsp;货</a>';
        purchaseOrderHTML += '</td>';
        purchaseOrderHTML += '</tr>';
        var items = orders[i].itemIndexDTOs;
        if(items && items.length>0){
            for(var j=0;j<items.length;j++){
                var  commodityCode = GLOBAL.Lang.isEmpty(items[j].commodityCode) ? "" : items[j].commodityCode + '&nbsp';
                var  itemName = GLOBAL.Lang.isEmpty(items[j].itemName) ? "" : items[j].itemName + '&nbsp';
                var  itemBrand = GLOBAL.Lang.isEmpty(items[j].itemBrand) ? "" : items[j].itemBrand + '&nbsp';
                var  itemSpec = GLOBAL.Lang.isEmpty(items[j].itemSpec) ? "" : items[j].itemSpec + '&nbsp';
                var  itemModel = GLOBAL.Lang.isEmpty(items[j].itemModel) ? "" : items[j].itemModel + '&nbsp';
                var  vehicleBrand = GLOBAL.Lang.isEmpty(items[j].vehicleBrand) ? "" : items[j].vehicleBrand + '&nbsp';
                var  vehicleModel = GLOBAL.Lang.isEmpty(items[j].vehicleModel) ? "" : items[j].vehicleModel + '&nbsp';
                var itemPrice = APP_BCGOGO.StringFilter.priceFilter(items[j].itemPrice, 2);
                var itemCount = APP_BCGOGO.StringFilter.priceFilter(items[j].itemCount, 1);
                var itemTotal = APP_BCGOGO.StringFilter.priceFilter(itemPrice * itemCount,2);
                var unit = items[j].unit == null? "" : items[j].unit;
                purchaseOrderHTML += '<tr ' + (isFirstResult ? 'style="display:block"' : 'style="display:none"') + '>';
                purchaseOrderHTML += '<td style="padding-left:10px;">' + commodityCode + itemName + itemBrand + itemSpec + itemModel + vehicleBrand + vehicleModel + '</td>';
                purchaseOrderHTML += '<td>' + itemPrice + '</td>';
                purchaseOrderHTML += '<td>' + itemCount + unit + '</td>';
                purchaseOrderHTML += '<td>' + itemTotal + '</td>';
                purchaseOrderHTML += '</tr>';
            }
        }
        purchaseOrderHTML += '</table>';
        purchaseOrderHTML += '<div class="slipInfo">';
        purchaseOrderHTML += '合计商品&nbsp;<span>' + items.length + '</span>&nbsp;种&nbsp;&nbsp;应付：<span>' + orderTotal + '</span>元';
        purchaseOrderHTML += '</div>';
        purchaseOrderHTML += '<div class="i_height"></div> ';
    }
    $("#purchaseOrderShow").append(purchaseOrderHTML);
    if($("#purchaseOrderTb0")){
        tableUtil.tableStyle("#purchaseOrderTb0",".J-purchaseOrder-title","odd");
        tableUtil.setRowBackgroundColor("#purchaseOrderTb0", null, ".J-purchaseOrder-title", "odd");
    }
}

function getSearchCondition(){
  return {
      commodityCode:$("#commodityCode").val(),
      customerOrSupplierName:$("#supplierInfoText").val(),
      contactNum:$("#mobile").val(),
      endTimeStr:$("#endTimeStr").val(),
      searchWord:$("#searchWord").val(),
      productBrand:$("#productBrand").val(),
      productModel:$("#productModel").val(),
      productName:$("#productName").val(),
      productSpec:$("#productSpec").val(),
      productVehicleBrand:$("#productVehicleBrand").val(),
      productVehicleModel:$("#productVehicleModel").val(),
      receiptNo:$("#receiptNo").val(),
      startTimeStr: $("#startTimeStr").val()
  }
}