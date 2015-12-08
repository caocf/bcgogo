/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-30
 * Time: 上午11:55
 * To change this template use File | Settings | File Templates.
 */
;
function salesNewOrderTabStyleChange() {
//标签样式
    $("#salesOrder").removeClass("actived");
    $("#salesNewOrder").addClass("actived");
    clearSearchField();
    $("#onlineSaleOrderSearchForm").css("display", "none");
    $("#onlinePurchaseOrderSearchForm").css("display", "");
    $("#mySalesOrderLink").removeClass("click");
    $("#myNewSalesOrderLink").addClass("click");
    if(!$("#my_date_self_defining_new").hasClass("clicked")){
        $("#my_date_self_defining_new").click();
    }
}
function salesOrderTabStyleChange() {
    $("#salesNewOrder").removeClass("actived");
    $("#salesOrder").addClass("actived");
    clearSearchField();
    $("#onlineSaleOrderSearchForm").css("display", "");
    $("#onlinePurchaseOrderSearchForm").css("display", "none");
    $("#myNewSalesOrderLink").removeClass("click");
    $("#mySalesOrderLink").addClass("click");
    if(!$("#my_date_self_defining").hasClass("clicked")){
        $("#my_date_self_defining").click();
    }
}
$(function(){
    drayMySaleOrderNumbers();
    var currTab = GLOBAL.Util.getUrlParameter("currTab");
    var orderStatus = GLOBAL.Util.getUrlParameter("orderStatus");
    var startTimeStr = GLOBAL.Util.getUrlParameter("startTimeStr");
    var endTimeStr = GLOBAL.Util.getUrlParameter("endTimeStr");
    if(!G.isEmpty(currTab) && currTab == 'NEW'){
        salesNewOrderTabStyleChange();
        if(!G.isEmpty(orderStatus)) {
           $("#onlinePurchaseOrderSearchForm .orderStatus").val(orderStatus);
        }
        if(!G.isEmpty(startTimeStr)) {
          $("#onlinePurchaseOrderSearchForm .startDateStr").val(startTimeStr);
        }
        if(!G.isEmpty(endTimeStr)) {
            $("#onlinePurchaseOrderSearchForm .endDateStr").val(endTimeStr);
        }
        searchPurchaseOrders();
    }else{
        if(!G.isEmpty(orderStatus)) {
            $("#onlineSaleOrderSearchForm .orderStatus").val(orderStatus)
        }
        searchSaleOrders();
    }

    $(".J-productSuggestion")
        .bind('click', function () {
            productSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                productSuggestion($(this));
            }
        })
        .bind('keypress', function (event) {
            var keyName = G.keyNameFromEvent(event);
            if (keyName === "enter") {
                //触发 查询
                $("#inquiryCenterSearchForm").submit();
            }
        });
    $(".customer-info")
        .bind('click', function () {
            customerSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                customerSuggestion($(this));
            }

        })
        .bind('keypress', function (event) {
            var keyName = G.keyNameFromEvent(event);
            if (keyName === "enter") {
                //触发 模糊库存查询
                getProductWithUnknownField();
            }
        }).bind("blur",function(){
           if($(this).attr("lastValue")) {
               if($(this).attr("lastValue") != $(this).val()) {
                  $("#customerShopIds").val('');
               }
           }
        });
    $(".startDateStr,.endDateStr")
        .datepicker({
            "numberOfMonths": 1,
            "changeYear": true,
            "showHour": false,
            "showMinute": false,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": "",
            "showButtonPanel":true
        }).blur(function () {
            var tab = $(this).attr("tabInfo");
            if(tab) {
                var startDate = $(".startDateStr[tabInfo='" + tab + "']").val();
                var endDate = $(".endDateStr[tabInfo='" + tab + "']").val();
                if (startDate == "" || endDate == "") return;
                if (startDate > endDate) {
                    $(".startDateStr[tabInfo='" + tab + "']").val(endDate);
                    $(".endDateStr[tabInfo='" + tab + "']").val(startDate);
                }
            }

        }).change(function () {
           $(this).blur();
        });




    $(".button-clear").bind("click", function () {
        clearSearchField();
    });

    $("#searchOnlineSaleOrders").bind("click",function(){
        searchSaleOrders();
    });

    $("#searchOnlinePurchaseOrders").bind("click",function(){
        searchPurchaseOrders();
    });

    $(".line-info").bind("click",function(){
        if($(this).attr("tab") == 'salesNewOrder') {
            salesNewOrderTabStyleChange();

            $("#onlinePurchaseOrderSearchForm .orderStatus").val('SELLER_PENDING');

            if ($(this).attr("startDate")) {
                $("#onlinePurchaseOrderSearchForm .startDateStr").val($(this).attr("startDate"));
            }
            if ($(this).attr("endDate")) {
                $("#onlinePurchaseOrderSearchForm .endDateStr").val($(this).attr("endDate"));
            }
            searchPurchaseOrders();
        } else if($(this).attr("tab") == 'salesOrder') {
            salesOrderTabStyleChange();
            if($(this).attr("orderStatus")) {
                $("#onlineSaleOrderSearchForm .orderStatus").val($(this).attr("orderStatus"));
            }
            searchSaleOrders();
        }
    });

    $(".J_open_SaleOrder").live("click",function(){
        var data_id = $(this).attr("data_id");
        if(!G.Lang.isEmpty(data_id) && G.Lang.isNumber(data_id)){
            window.open("sale.do?method=toSalesOrder&salesOrderId="+data_id,"_blank");
        }
    });

    $(".J_open_PurchaseOrder").live("click",function(){
        var data_id = $(this).attr("data_id");
        if(!G.Lang.isEmpty(data_id) && G.Lang.isNumber(data_id)){
            window.open("sale.do?method=toOnlinePendingPurchaseOrder&purchaseOrderId="+data_id,"_blank");
        }
    });

    $(".J_open_shopDetail").live("click",function(){
        var data_id = $(this).attr("data_id");
        if(!G.Lang.isEmpty(data_id) && G.Lang.isNumber(data_id)){
            window.open("shopMsgDetail.do?method=renderShopMsgDetail&paramShopId="+data_id,"_blank");
        }
    });

    $(".J_more_saleOrderItem").live("click",function(){
        $(this).parent().prev("table").find("tr:gt(1)").show();
        $(this).addClass("hidden");
        $(this).next(".J_less_saleOrderItem").removeClass("hidden");

    });
    $(".J_less_saleOrderItem").live("click",function(){
        $(this).parent().prev("table").find("tr:gt(1)").hide();
        $(this).addClass("hidden");
        $(this).prev(".J_more_saleOrderItem").removeClass("hidden");
    });

    $(".J_open_productHistory").live("click", function () {
        var domIdArray = $(this).attr("id").split(".");
        var orderPrefix =  domIdArray[0];
        var itemPrefix =  domIdArray[1];
        var orderId = $("#" + orderPrefix + "\\.id").val();
        var itemId = $("#" + orderPrefix +"\\."+itemPrefix+ "\\.id").val();
        var orderType = $("#" + orderPrefix +"\\."+itemPrefix+ "\\.orderType").val();
        var productLocalId = "";
        if(orderType == 'PURCHASE'){
            productLocalId = $("#" + orderPrefix +"\\."+itemPrefix+ "\\.productLocalId").val();
        }
        if (!G.Lang.isEmpty(orderId) && G.Lang.isNumber(orderId)
            && !G.Lang.isEmpty(itemId) && G.Lang.isNumber(itemId)
            ) {
            var url = "shopProductDetail.do?method=toShopProductDetail&paramShopId="+selfShopId+"&productLocalId=" + productLocalId + "&orderItemId="+itemId+"&orderType=" + orderType + "&itemFrom=order";
            window.open(url, "_blank");
        }
    });

    $("#salesNewOrder").click(function(){
        salesNewOrderTabStyleChange();
        searchPurchaseOrders();
    });

    $("#salesOrder").click(function(){
        //标签样式
        salesOrderTabStyleChange();
        searchSaleOrders();
    });

    $(".J_modify_purchaseOrder").live("click", function () {
        var orderId = $(this).attr("data_id");
        var orderPrefix = $(this).attr("id").split(".")[0];
        function validatePurchaseOrderStatus(result) {
            if (result && result.success) {
                var purchaseOrderDTOs, purchaseOrderDTO, supplierDTOs, supplierDTO;
                if (result && result["results"]) {
                    purchaseOrderDTOs = result["results"];
                    if (result && result["data"] && result["data"]["supplierDTOs"]) {
                        supplierDTOs = $.extend({}, result["data"]["supplierDTOs"]);
                    }
                    if (!G.Lang.isEmpty(purchaseOrderDTOs)) {
                        for (var i = 0; i < purchaseOrderDTOs.length; i++) {
                            if (!G.Lang.isEmpty(purchaseOrderDTOs[i]) && purchaseOrderDTOs[i]["idStr"] == orderId) {
                                purchaseOrderDTO = purchaseOrderDTOs[i];
                                var supplierId = G.Lang.normalize(purchaseOrderDTO["supplierIdStr"], "");  //供应商Id
                                supplierDTO = supplierDTOs[supplierId];
                                break;
                            }
                        }
                    }
                }
                if (!G.Lang.isEmpty(purchaseOrderDTO)) {
                    var status = G.Lang.normalize(purchaseOrderDTO["status"], "");  //状态
                    if (status == "SELLER_PENDING") {
                        window.open("RFbuy.do?method=show&id=" + orderId, "_blank");
                    } else {
                        nsDialog.jAlert("当前单据已经被处理，无法修改,请做其他操作！");
                        drawSingleSaleOrder(purchaseOrderDTO, supplierDTO, orderPrefix);
                        return;
                    }
                }
            }
        }

        getPurchaseOrderById(orderId, validatePurchaseOrderStatus);
    });

    $(".J_toStorage_purchaseOrder").live("click", function () {
        var orderId = $(this).attr("data_id");
        var orderPrefix = $(this).attr("id").split(".")[0];
        function validatePurchaseOrderStatus(result) {
            if (result && result.success) {
                var purchaseOrderDTOs, purchaseOrderDTO, supplierDTOs, supplierDTO;
                if (result && result["results"]) {
                    purchaseOrderDTOs = result["results"];
                    if (result && result["data"] && result["data"]["supplierDTOs"]) {
                        supplierDTOs = $.extend({}, result["data"]["supplierDTOs"]);
                    }
                    if (!G.Lang.isEmpty(purchaseOrderDTOs)) {
                        for (var i = 0; i < purchaseOrderDTOs.length; i++) {
                            if (!G.Lang.isEmpty(purchaseOrderDTOs[i]) && purchaseOrderDTOs[i]["idStr"] == orderId) {
                                purchaseOrderDTO = purchaseOrderDTOs[i];
                                var supplierId = G.Lang.normalize(purchaseOrderDTO["supplierIdStr"], "");  //供应商Id
                                supplierDTO = supplierDTOs[supplierId];
                                break;
                            }
                        }
                    }
                }
                if (!G.Lang.isEmpty(purchaseOrderDTO)) {
                    var status = G.Lang.normalize(purchaseOrderDTO["status"], "");  //状态
                    var isShortage = purchaseOrderDTO["shortage"];   //relation salesOrder isShortage
                    if ($.inArray(status, ["SELLER_DISPATCH", "PURCHASE_ORDER_WAITING"]) >= 0 || (status == "SELLER_STOCK" && !isShortage)) {
                        window.open("storage.do?method=getProducts&type=txn&purchaseOrderId=" + orderId, "_blank");
                    } else {
                        nsDialog.jAlert("当前单据已经被处理，无法入库,请做其他操作！");
                        drawSingleSaleOrder(purchaseOrderDTO, supplierDTO, orderPrefix);
                        return;
                    }
                }
            }
        }

        getPurchaseOrderById(orderId, validatePurchaseOrderStatus);
    });

    $(".J_nullify_purchaseOrder").live('click', function () {
        var orderId = $(this).attr("data_id");
        var orderPrefix = $(this).attr("id").split(".")[0];

        function validatePurchaseOrderStatus(result) {
            if (result && result.success) {
                var purchaseOrderDTOs, purchaseOrderDTO, supplierDTOs, supplierDTO;
                if (result && result["results"]) {
                    purchaseOrderDTOs = result["results"];
                    if (result && result["data"] && result["data"]["supplierDTOs"]) {
                        supplierDTOs = $.extend({}, result["data"]["supplierDTOs"]);
                    }
                    if (!G.Lang.isEmpty(purchaseOrderDTOs)) {
                        for (var i = 0; i < purchaseOrderDTOs.length; i++) {
                            if (!G.Lang.isEmpty(purchaseOrderDTOs[i]) && purchaseOrderDTOs[i]["idStr"] == orderId) {
                                purchaseOrderDTO = purchaseOrderDTOs[i];
                                var supplierId = G.Lang.normalize(purchaseOrderDTO["supplierIdStr"], "");  //供应商Id
                                supplierDTO = supplierDTOs[supplierId];
                                break;
                            }
                        }
                    }
                }
                if (!G.Lang.isEmpty(purchaseOrderDTO)) {
                    var status = G.Lang.normalize(purchaseOrderDTO["status"], "");  //状态
                    var confirmMsg = "";
                    var isCanNullify = false;
                    if (status == 'SELLER_PENDING') {
                        isCanNullify = true;
                        confirmMsg = "友情提示：采购单作废后，将不再有效，交易会被取消！您确定要作废该采购单吗？";
                    } else if (status == 'SELLER_REFUSED' || status == 'PURCHASE_SELLER_STOP') {
                        isCanNullify = true;
                        confirmMsg = "友情提示：作废后该采购单不再属于待办单据，只能通过查询找出！您确定要作废该采购单吗？";
                    } else {
                        isCanNullify = false;
                        var statusStr = getOrderStatusStr(status);
                        if (G.Lang.isEmpty(statusStr)) {
                            confirmMsg = "友情提示：当前单据状态异常无法作废，请进行其他操作！";
                        } else {
                            confirmMsg = "友情提示：当前单据已经被操作，单据状态为【" + statusStr + "】无法作废，请进行其他操作！";
                        }

                    }

                    if (isCanNullify) {
                        nsDialog.jConfirm(confirmMsg, "作废订单", function (resultVal) {
                            if (resultVal) {
                                window.location = "RFbuy.do?method=purchaseOrderRepeal&id=" + orderId;
                            }
                        });
                    } else {
                        nsDialog.jAlert(confirmMsg);
                        drawSingleSaleOrder(purchaseOrderDTO, supplierDTO, orderPrefix);
                    }
                }
            }
        }

        getPurchaseOrderById(orderId, validatePurchaseOrderStatus);
    });

    //接受采购
    $("#acceptConfirmBtn").bind("click", function () {
        if(APP_BCGOGO.Permission.Version.StoreHouse && G.Lang.isEmpty($("#storehouseId").val())){
            nsDialog.jAlert("请选择仓库！");
            return;
        }
        //预计交货时间
        var radios = document.getElementsByName("dispatchDateRadio");
        for(var i=0;i<radios.length;i++){
            if(radios[i].checked==true){
                $("#dispatchDate").val(radios[i].value);
                if(i==3){
                    $("#dispatchDate").val($("#definedDate").val());
                }
            }
        }
        if($("#dispatchDate").val()==""){
            nsDialog.jAlert("预计交货时间必须填写！",null,function(){});
        }else{
            $("#salesAcceptForm").attr("action","sale.do?method=acceptPendingPurchaseOrder&purchaseOrderId="+$("#purchaseOrderId").val() + "&purchaseVestDate=" + $("#pruchaseVestDate").html());
            $("#salesAcceptForm").submit();
            $("#salesAcceptForm")[0].reset();
            setTimeout(function(){
                window.location.reload();
            },5000);
            $("#acceptDialog").dialog("close");
        }
    });
    $("#acceptCancelBtn").bind("click", function () {
        $("#salesAcceptForm")[0].reset();
        $("#acceptDialog").dialog("close");
    });

    //拒绝采购
    $("#refuseConfirmBtn").bind("click", function () {
        if($("#refuseMsg").val()!=""){
            $("#salesRefuseForm").ajaxSubmit(function(){
                var url = "sale.do?method=toOnlinePendingPurchaseOrder&purchaseOrderId=" + $("#refuse_id").val();
                window.open(url, "_blank");
                $("#refuseReasonDialog").dialog("close");
            });
            $("#salesRefuseForm")[0].reset();
//            setTimeout(function(){
//                window.location.reload();
//            },5000)
        }else{
            nsDialog.jAlert("拒绝理由必须填写！");
        }
    });
    $("#refuseCancelBtn").bind("click", function () {
        $("#salesRefuseForm")[0].reset();
        $("#refuseReasonDialog").dialog("close");
    });

    //发货
    $("#dispatchConfirmBtn").bind("click", function () {
        $("#dispatch_id").val($("#salesOrderId").val());
        if($("#company").val()!=""){
            $("#dispatchForm").submit();
            $("#dispatchForm")[0].reset();
            setTimeout(function(){
                window.location="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS";
            },5000);
            $("#dispatchDialog").dialog("close");
        }else{
            nsDialog.jAlert("物流公司名称必须填写！");
        }
    });
    $("#dispatchCancelBtn").bind("click", function () {
        $("#dispatchForm")[0].reset();
        $("#dispatchDialog").dialog("close");
    });

    //终止交易
    $("#repealConfirmBtn").bind("click", function () {
        if (!$.trim($("#repealMsg").val())) {
            nsDialog.jAlert("请填写作废理由", null, function () {
            });
            return;
        }
        $("#salesRepealForm").submit();
        $("#salesRepealForm")[0].reset();
        setTimeout(function () {
            window.location="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS";
        }, 5000);
        $("#repealReasonDialog").dialog("close");
    });
    $("#repealCancelBtn").bind("click", function () {
        $("#salesRepealForm")[0].reset();
        $("#repealReasonDialog").dialog("close");
    });
    $("#todoSaleOrders").addClass("hover_yinye");

    //按钮操作 待办销售单
    //接受
    $(".J_accept_salesOrder").live("click", function(){
        if($(this).attr("disabled")){
            return;
        }
        var orderId = $(this).attr("data_id");
        var vestDate = $(this).attr("purchase_vest_date");
        if(APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier){
            window.open("sale.do?method=toOnlinePendingPurchaseOrder&purchaseOrderId="+orderId);
            return;
        }
        //赋上单据ID
        $("#purchaseOrderId").val(orderId);
        $("#pruchaseVestDate").html(vestDate);
        $("#acceptDialog").dialog({ width: 570, modal:true});
    });
    //拒绝
    $(".J_refuse_salesOrder").live("click", function(){
       if($(this).attr("disabled")){
         return;
       }
        var orderId = $(this).attr("data_id");
        $("#refuse_id").val(orderId);
        $("#refuseReasonDialog").dialog({ width: 520, modal:true});
    });
    //发货
    $(".J_dispatch_salesOrder").live("click", function(){
        if ($(this).attr("disabled")) {
            return;
        }
        var orderId = $(this).attr("data_id");
        if(APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier){
            toSalesOrder(orderId);
            return;
        }
        $("#salesOrderId").val(orderId);
        App.Net.syncPost({
            url: "sale.do?method=validateDispatchSaleOrder",
            data: {salesOrderId: orderId},
            dataType: "json",
            success: function (json) {
                if(json && json.success){
                    $("#dispatch_id").val(orderId);
                    $(".shortageOperateVerfier").remove();
                    $("#dispatchDialog").dialog({ width: 520, modal: true});
                }else{
                    if(json && json.operation=="CONFIRM_ALLOCATE_RECORD"){
                        nsDialog.jConfirm(json.msg, "确认仓库调拨提示", function (returnVal) {
                            if(returnVal){
                                window.open("allocateRecord.do?method=createAllocateRecordBySaleOrderId&salesOrderId="+orderId,"_blank");
                            }
                        });
                    }else if(json && json.operation=="ALERT_SALE_LACK"){
                        nsDialog.jAlert(json.msg);
                    }else{
                        nsDialog.jAlert(json.msg);
                    }
                }
            }
        });
    });
    //终止交易
    $(".J_stop_saleOrder").live("click", function(){
//    function stopSale(orderId, purchaseOrderStatus, dom) {
        if ($(this).attr("disabled")) {
            return;
        }
        var orderId = $(this).attr("data_id");
        $("#repeal_id").val(orderId);
        $("#repealReasonDialog").dialog({
            width: 520,
            modal: true,
            beforeclose: function(event, ui) {
                return true;
            }
        });
    });
    //作废
    $(".J_nullify_saleOrder").live("click", function(){
        if ($(this).attr("disabled")) {
            return;
        }
        var orderId = $(this).attr("data_id");
        if(APP_BCGOGO.Permission.Version.StoreHouse){//这个时候作废才会操作库存
            if (!validatorStoreHouseOrderRepeal($("#_toStorehouseId").val(),"SALE",orderId)) {
                return;
            }
        }
        repealOrder(orderId);
    });

    //结算
    $(".J_account_salesOrder").live("click", function(){
       if($(this).attr("disabled")){
         return;
       }
        var orderId = $(this).attr("data_id");
        window.open("sale.do?method=toOnlineSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId="+orderId);
    });
    //缺料
//    function toShotageInventory(salesOrderId,dom){
//      if($(dom).attr("disabled")=="true"){
//        return;
//      }
//      if(stringUtil.isEmpty(salesOrderId)){
//        return;
//      }
//      var url="storage.do?method=getProducts&type=good&goodsindexflag=gi&salesOrderId=";
//      url+=salesOrde
//      window.open(url);
//    }

});

//selectStoreHouse.jsp中js  调用  名称不能修改
function repealOrder(orderId){
    nsDialog.jConfirm("友情提示：作废后可能会发生库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！您确定要作废该销售单吗？", null, function (returnVal) {
        if (returnVal) {
            var url = "sale.do?method=saleOrderRepeal&salesOrderId=" + orderId;
            if(APP_BCGOGO.Permission.Version.StoreHouse){
                url +="&toStorehouseId="+$("#_toStorehouseId").val();
            }
            window.open(url);
            setTimeout(function () {
                window.location.reload();
            }, 5000);
        }
    });
}

var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

function clearSearchField() {
    $(".J-productSuggestion").val("");
    $(".order-info,.customer-info,.J_hasDatePicker").val("");
    $(".date_default").each(function(){
        $(this).attr("checked","checked");
    });
    $(".orderStatus").each(function(){
        $(this).find("option").eq(0).attr("selected", "selected");
    });
    if(!$("#my_date_self_defining").hasClass("clicked")){
        $("#my_date_self_defining").click();
    }else{
        $("#my_date_self_defining_new").click();

    }



}

function drayMySaleOrderNumbers() {
    var url = "orderCenter.do?method=getOnlineSaleOrderCount";
    bcgogoAjaxQuery.setUrlData(url, {});
    bcgogoAjaxQuery.ajaxQuery(function (json) {
        if (json && json.success) {
            var result = json.data;
            $("#sale_today_new").text("(" + G.Lang.normalize(result["sale_today_new"], "0") + ")");
            $("#sale_early_new").text("(" + G.Lang.normalize(result["sale_early_new"], "0") + ")");
            $("#sale_new").text(G.Lang.normalize(result["sale_new"], "0"));
            $("#sale_seller_stock").text("(" + G.Lang.normalize(result["sale_stocking"], "0") + ")");
            $("#sale_seller_dispatch").text("(" + G.Lang.normalize(result["sale_dispatch"], "0") + ")");
            $("#sale_debt_account").text("(" + G.Lang.normalize(result["sale_sale_debt_done"], "0") + ")");
            $("#sale_in_progress").text(G.Lang.normalize(result["sale_in_progress"], "0"));

        }
    });
}

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
        if($(this).attr("name")!="searchWord"){
            ajaxData[$(this).attr("name")] = val;
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
                        details[$(this).attr("searchField")] = val;
                    });
                    return {
                        details:details
                    };
                },
                onSelect: function (event, index, data, hook) {
                    $domObject.nextAll(".J-productSuggestion").each(function () {
                        var label = data.details[$(this).attr("searchField")];
                        if (G.Lang.isEmpty(label)) {
                            $(this).val("");
                        } else {
                            $(this).val(G.Lang.normalize(label));
                        }
                    });
                    dropList.hide();
                },
                onKeyboardSelect: function (event, index, data, hook) {
                    $domObject.nextAll(".J-productSuggestion").each(function () {
                        var label = data.details[$(this).attr("searchField")];
                        if (G.Lang.isEmpty(label)) {
                            $(this).val("");
                        } else {
                            $(this).val(G.Lang.normalize(label));
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
                    $domObject.nextAll(".J-productSuggestion").each(function () {
                        clearSearchInputValueAndChangeCss(this);
                    });
                    dropList.hide();
                }
            });
        }
    });
}

function customerSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var dropList = APP_BCGOGO.Module.droplist;
    dropList.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        searchWord: searchWord,
        customerOrSupplier: "customer",
        titles: "name,contact,mobile",
        uuid: dropList.getUUID()
    };
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        dropList.show({
            "selector": $domObject,
            "data": result,
            "onSelect": function (event, index, data) {
                $domObject.val(data.details["name"]);
                $domObject.css({"color": "#000000"});
                $("#customerShopIds").val(data.details["customerOrSupplierShopId"]);
                $domObject.attr("lastValue",data.details["name"]);
                dropList.hide();
            }, "onKeyboardSelect": function (event, index, data) {
                $domObject.val(data.details["name"]);
            }
        });
    });
}


function searchSaleOrders() {
    var param = $("#onlineSaleOrderSearchForm").serializeArray();
    var data = {};
    $.each(param, function (index, val) {
        if (!G.Lang.isEmpty(data[val.name])) {
            data[val.name] = data[val.name] + "," + val.value;
        } else {
            data[val.name] = val.value;
        }
    });
    APP_BCGOGO.Net.syncPost({
        url: "orderCenter.do?method=getOnlineSaleOrders",
        dataType: "json",
        data: data,
        success: function (result) {
            drawSaleOrderList(result);
            $("#salesOrderPage").css("display","");
            $("#salesNewOrderPage").css("display","none");
            initPage(result, "saleOrderList", "orderCenter.do?method=getOnlineSaleOrders", '', "drawSaleOrderList", '', '', data, '');
        },
        error: function () {
            nsDialog.jAlert("数据异常，请刷新页面！");
        }
    });
}

function searchPurchaseOrders() {
    var param = $("#onlinePurchaseOrderSearchForm").serializeArray();
    var data = {};
    $.each(param, function (index, val) {
        if (!G.Lang.isEmpty(data[val.name])) {
            data[val.name] = data[val.name] + "," + val.value;
        } else {
            data[val.name] = val.value;
        }
    });
    APP_BCGOGO.Net.syncPost({
        url: "orderCenter.do?method=getOnlineSaleNewOrders",
        dataType: "json",
        data: data,
        success: function (result) {
            drawPurchaseOrderList(result);
            $("#salesOrderPage").css("display","none");
            $("#salesNewOrderPage").css("display","");
            initPage(result, "purchaseOrderList", "orderCenter.do?method=getOnlineSaleNewOrders", '', "drawPurchaseOrderList", '', '', data, '');
        },
        error: function () {
            nsDialog.jAlert("数据异常，请刷新页面！");
        }
    });
}

/**
 * data：{
 * results:[],
 * data:[]
 * }
 * @param data
 */
function drawSaleOrderList(data){
    $(".J-SaleOrdersList").empty();
    var saleOrders,customerDTOs, shopDTOs;
    if(data && data["results"]){
        saleOrders = data["results"];
    }

    if(data && data["data"]){
        if(data["data"]["customerDTOs"]){
            customerDTOs = $.extend({}, data["data"]["customerDTOs"]);
        }
        if(data["data"]["shopDTOs"]){
            shopDTOs = data["data"]["shopDTOs"];
        }
    }
    if(G.Lang.isEmpty(saleOrders)){
        var emptyHtml = '<dl class="content-item"><dd><table class="item-content"><tr><td>对不起，没有找到您要的单据信息!</td></tr></table></dd></dl>';   //
        $(".J-SaleOrdersList").append(emptyHtml);
    }else{
        for (var i = 0; i < saleOrders.length; i++) {
            var html = '';
            var saleOrder =  saleOrders[i];
            var orderPrefix = "orderDTOs" + i;
            var customerId = G.Lang.normalize(saleOrder["customerIdStr"], "");  //供应商Id
            var customerDTO =  customerDTOs[customerId];
            var shopDTO = shopDTOs[customerDTO.customerShopIdStr];
            html += '<dl class="content-item" id="' + orderPrefix + '.orderContent">';
            html += buildSingleSaleOrderContentHtml(saleOrder, customerDTO, shopDTO, orderPrefix);
            html += '</dl>';
            $(".J-SaleOrdersList").append(html);
        }
    }
}

function drawPurchaseOrderList(data) {
    $(".J-SaleOrdersList").empty();
    var purchaseOrders,shopDTOs;
    if(data && data["results"]){
        purchaseOrders = data["results"];
    }

    if(data && data["data"]){

        if(data["data"]["shopDTOs"]){
            shopDTOs = data["data"]["shopDTOs"];
        }
    }
    if(G.Lang.isEmpty(purchaseOrders)){
        var emptyHtml = '<dl class="content-item"><dd><table class="item-content"><tr><td>对不起，没有找到您要的单据信息!</td></tr></table></dd></dl>';   //
        $(".J-SaleOrdersList").append(emptyHtml);
    }else{
        for (var i = 0; i < purchaseOrders.length; i++) {
            var html = '';
            var purchaseOrder =  purchaseOrders[i];
            var orderPrefix = "orderDTOs" + i;
            var customerShopId = G.Lang.normalize(purchaseOrder["shopIdStr"], "");
            var shopDTO = shopDTOs[customerShopId];
            html += '<dl class="content-item" id="' + orderPrefix + '.orderContent">';
            html += buildSinglePurchaseOrderContentHtml(purchaseOrder, shopDTO, orderPrefix);
            html += '</dl>';
            $(".J-SaleOrdersList").append(html);
        }
    }
}

function getCustomerContactQQArray(customerDTO){
    var qqArray = [];
    if(customerDTO && customerDTO["contacts"]){
        var contacts =   customerDTO["contacts"];
        for(var i= 0,len = contacts.length;i<len;i++){
            if(contacts[i] && !G.Lang.isEmpty(contacts[i]["qq"])){
                qqArray.push(contacts[i]["qq"]);
            }
        }
    }
    return qqArray;
}

function getSaleOrderItemProductInfo(itemDTO) {
    var productInfo = "";
    var isStart = false;
    if (itemDTO) {
        if (!G.Lang.isEmpty(itemDTO["commodityCode"])) {
            isStart = true;
            productInfo += itemDTO["commodityCode"];
        }
        if (!G.Lang.isEmpty(itemDTO["productName"])) {
            if (isStart) {
                productInfo += "&nbsp;"
            } else {
                isStart = true;
            }
            productInfo += itemDTO["productName"];
        }
        if (!G.Lang.isEmpty(itemDTO["brand"])) {
            if (isStart) {
                productInfo += "&nbsp;"
            } else {
                isStart = true;
            }
            productInfo += itemDTO["brand"];
        }
        if (!G.Lang.isEmpty(itemDTO["model"])) {
            if (isStart) {
                productInfo += "&nbsp;"
            } else {
                isStart = true;
            }
            productInfo += itemDTO["model"];
        }
        if (!G.Lang.isEmpty(itemDTO["spec"])) {
            if (isStart) {
                productInfo += "&nbsp;"
            } else {
                isStart = true;
            }
            productInfo += itemDTO["spec"];
        }
        if (!G.Lang.isEmpty(itemDTO["vehicleBrand"])) {
            if (isStart) {
                productInfo += "&nbsp;"
            } else {
                isStart = true;
            }
            productInfo += itemDTO["vehicleBrand"];
        }
        if (!G.Lang.isEmpty(itemDTO["vehicleModel"])) {
            if (isStart) {
                productInfo += "&nbsp;"
            } else {
                isStart = true;
            }
            productInfo += itemDTO["vehicleModel"];
        }
    }
    return productInfo;
}


function getPurchaseOrderById(orderId,callBack){
    if(G.Lang.isEmpty(orderId) || !G.Lang.isNumber(orderId)){
        return null;
    }
    var data = {};
    data["orderId"] = orderId;
    APP_BCGOGO.Net.syncPost({
        url: "orderCenter.do?method=getOnlinePurchaseOrders",
        dataType: "json",
        data: data,
        success: function (result) {
            if(callBack != null && typeof callBack === "function"){
                callBack(result);
            }
        },
        error: function () {
            nsDialog.jAlert("数据异常，请刷新页面！");
        }
    });
}

function buildSingleSaleOrderContentHtml(saleOrder, customerDTO, shopDTO, orderPrefix) {
    if (G.Lang.isEmpty(saleOrder) || G.Lang.isEmpty(orderPrefix)) {
        return;
    }
    var html = '';
    var receiptNo = G.Lang.normalize(saleOrder["receiptNo"], "--");
    var vestDateStr = G.Lang.normalize(saleOrder["vestDateStr"], "");  //下单时间
    var purchaseVestDate = G.Lang.normalize(saleOrder["purchaseVestDate"], "");  //采购单要求送货时间
    var preDispatchDateStr = G.Lang.normalize(saleOrder["preDispatchDateStr"], "");  //预计发货时间
    var status = G.Lang.normalize(saleOrder["status"], "");  //状态
    var statusStr = getOrderStatusStr(status);
    var notPaid = saleOrder["unPaid"]

    var orderId = G.Lang.normalize(saleOrder["idStr"], "");  //采购单的Id
    var customerId = G.Lang.normalize(saleOrder["customerIdStr"], "");  //客户Id
    var customerShopId = G.Lang.normalize(customerDTO["customerShopIdStr"], "");  //客户店铺Id
    var customerName = G.Lang.normalize(saleOrder["customer"], "");  //客户名字
    var orderTotal = dataTransition.rounding(saleOrder["total"],2);
    var promotionsInfoDTO = saleOrder["promotionsInfoDTO"];
    var bargainTotal, mljTotal, mjsStr, freeShippingStr, promotionsTotal;
    var originalTotal = orderTotal;
    var isHavePromotion = false;
    var isShortage = saleOrder["shortage"];   //relation salesOrder isShortage

    if (!G.Lang.isEmpty(promotionsInfoDTO)) {
        if (promotionsInfoDTO["bargain"] && G.Lang.isNumber(promotionsInfoDTO["bargain"])) {
            isHavePromotion = true;
            bargainTotal = dataTransition.rounding(promotionsInfoDTO["bargain"], 2);
        }
        if (promotionsInfoDTO["mlj"] && G.Lang.isNumber(promotionsInfoDTO["mlj"])) {
            isHavePromotion = true;
            mljTotal = dataTransition.rounding(promotionsInfoDTO["mlj"], 2);
        }
        if (!G.Lang.isEmpty(promotionsInfoDTO["mjs"])) {
            isHavePromotion = true;
            mjsStr = promotionsInfoDTO["mjs"];
        }
        if (!G.Lang.isEmpty(promotionsInfoDTO["free_SHIPPING"])) {
            isHavePromotion = true;
            freeShippingStr = promotionsInfoDTO["free_SHIPPING"];
        }
        if (promotionsInfoDTO["promotionsTotal"] && G.Lang.isNumber(promotionsInfoDTO["promotionsTotal"])) {
            promotionsTotal = dataTransition.rounding(promotionsInfoDTO["promotionsTotal"], 2);
            originalTotal += promotionsTotal;
        }
    }

//            html += '<dl class="content-item" id="' + orderPrefix  + '.orderContent">';
    html += '<dt>';
    html += '<div class="bg-top-hr"></div>';
    html += '<div class="bar-tab">';
    html += '<input type="hidden" id="' + orderPrefix + '.id" value = "' + orderId + '">';
    html += '销售单号：<span class="button-spread J_open_SaleOrder" data_id = "' + orderId + '">' + receiptNo + '</span>&nbsp;';
    html += '下单时间：' + vestDateStr + '&nbsp;';
    html += '买家期望交货：' + purchaseVestDate + '&nbsp;';

    html += '客户：<span class="button-spread J_open_shopDetail" data_id ="' + customerShopId + '">' + customerName + '</span>&nbsp;';

    html += '<a class="icon_contacts J_showContactsTip" shop_id="' + customerShopId + '"></a>';
    if(shopDTO) {
        html += generateContactTip(shopDTO);
    }

    html += '  <div class="cl"></div>';
    html += '</div>';
    html += '</dt>';
    html += '<dd>';
    html += '<table class="item-content" cellspacing="0" cellpadding="0">';
    var salesItemDTOs = saleOrder["itemDTOs"];

    if (!G.Lang.isEmpty(salesItemDTOs)) {
        var itemCount = salesItemDTOs.length;
        for (var n = 0; n < itemCount; n++) {
            var itemDTO = salesItemDTOs[n];
            var productInfo = getSaleOrderItemProductInfo(itemDTO);
            var quotedPrice = dataTransition.simpleRounding(itemDTO["quotedPrice"], 2);  // 销售价
            var price = dataTransition.simpleRounding(itemDTO["price"], 2);     //成交价
            var customPriceFlag = !!itemDTO["customPriceFlag"];     //客户议价的标志
            var quotedPreBuyFlag = !G.Lang.isEmpty(itemDTO["quotedPreBuyOrderItemId"]);     //卖家报价标志
            var amountUnit = dataTransition.simpleRounding(itemDTO["amount"], 2) + G.Lang.normalize(itemDTO["unit"]);
            var productLocalId = itemDTO.productIdStr;
            var shortageAmount = '';
            if(G.isNumber(itemDTO["shortage"]) && Number(itemDTO["shortage"]) != 0) {
                shortageAmount =dataTransition.simpleRounding(itemDTO["shortage"], 2)  + G.Lang.normalize(itemDTO["unit"]);
            }
            var itemId = G.Lang.normalize(itemDTO["idStr"], "");
            var imageURL =itemDTO.imageCenterDTO.productListSmallImageDetailDTO.imageURL;
            var itemPrefix = "itemDTOs" + n;
            if (n > 1) {
                html += '<tr style="display: none">';
            } else {
                html += '<tr>';
            }
            html += '<td class="item-product-info width-set">';
            html += '<a class="info-icon J_open_productHistory" id="' + orderPrefix + '.' + itemPrefix + '.image">';        //todo need to add img
            html += '<img src="'+imageURL+'"/>';
            html += '</a>';
            html += '<a class="info-details button-spread J_open_productHistory" id="' + orderPrefix + '.' + itemPrefix + '.productInfo">' + productInfo + '</a>';
            html += '<input type="hidden" id="' + orderPrefix + '.' + itemPrefix + '.id" value = "' + itemId + '">';
            html += '<input type="hidden" id="' + orderPrefix + '.' + itemPrefix + '.orderType" value = "SALE">';
            html += '<div class="cl"></div>';
            html += '</td>';
            html += '<td class="item-product-unit-price width-set">' + quotedPrice + '</td>';
            html += '<td class="item-product-quantity width-set">' + amountUnit;
            if(shortageAmount != '' && 'STOCKING' == status) {
                html += '<div class="price-description" style="color:#FF0000;margin-left:-5px;cursor:pointer;" onclick="toShotageInventory(\'' + orderId + '\',\'' + productLocalId + '\')">缺料' + shortageAmount + '</div>';
            }
            html += '</td>'
            html += '<td class="item-product-price width-set">';
            html += '<div class="price-value">' + price + '</div>';
            if (quotedPreBuyFlag) {
                html += '<div class="price-description">我的报价</div>';
            } else if (customPriceFlag) {
                html += '<div class="price-description">买家期望价</div>';
            }
            html += '</td >';

            if (n == 0) {
                html += '<td class="item-product-payables width-set" rowspan="999">';
                if (originalTotal > orderTotal) {
                    html += '<div class="payables-original">' + dataTransition.rounding(originalTotal,2) + '</div>';
                }

                html += '<div class="payables-current">' + orderTotal + '</div>';
                if (isHavePromotion) {
                    html += '<div class="payables-description">';
                    html += '<div>促销优惠</div>';
                    if (bargainTotal > 0) {
                        html += '<div><span>特价商品：</span>优惠<span class="arialFont">&yen;</span> <span>' + bargainTotal + '</span></div>';
                    }
                    if (mljTotal > 0) {
                        html += '<div><span>满立减：</span>优惠<span class="arialFont">&yen;</span> <span>'+mljTotal+'</span></div>';
                    }
                    if (!G.Lang.isEmpty(mjsStr)) {
                        html += '<div><span>满就送：' + mjsStr + '</span></div>';
                    }
                    if (!G.Lang.isEmpty(freeShippingStr)) {
                        html += '<div><span>送货上门</span></div>';
                    }
                    html += '</div>';
                }
                html += '</td>';
                html += '<td class="item-product-order-status width-set" rowspan="999">';
                html += '<div class="status-description">' + statusStr + '</div>';
                html += '<div class="button-spread  word-blue J_open_SaleOrder" data_id="' + orderId + '">订单详情</div>';
                if(isShortage && 'STOCKING' == status) {
                    html += '<div class="price-description" style="color:#FF0000;cursor:pointer;" onclick="toShotageInventory(\'' + orderId + '\',\'\'' +  ')">缺料</div>';
                }
                html += '</td>';
                html += '<td class="item-product-operating width-set" rowspan="999">';
                if(status == 'STOCKING') {
                    html += '<div class="button-edit button-blue-gradient J_dispatch_salesOrder" data_id = "' + orderId + '" id="' + orderPrefix + '.dispatch">发货</div>';
                }
                if($.inArray(status, ["SALE_DEBT_DONE","DISPATCH"]) >= 0) {
                    html += '<div class="button-edit button-blue-gradient J_account_salesOrder" data_id = "' + orderId + '" id="' + orderPrefix + '.account">结算</div>';
                }
                if($.inArray(status, ["SALE_DEBT_DONE","SALE_DONE"]) >= 0) {
                    html += '<div class="button-spread button-void J_nullify_saleOrder" data_id = "' + orderId + '" id="' + orderPrefix + '.nullify">作废</div>';
                }
                if($.inArray(status, ["STOCKING","DISPATCH"]) >= 0) {
                    html += '<div class="button-spread button-void J_stop_saleOrder" data_id = "' + orderId + '" id="' + orderPrefix + '.stop">终止销售</div>';
                }

                html += '</td>';
            }
            html += '</tr>';
        }
    }
    html += '</table>';
    html += '<div class="item-control-bar">';
    if (itemCount > 2) {
        html += '<span class="button-spread J_more_saleOrderItem">共' + itemCount + '种商品，点击展开更多</span> ';
        html += '<span class="button-retract hidden J_less_saleOrderItem">共有' + itemCount + '种商品，点击收起</span>';
    } else {
        html += '<span class="word-blue">共' + itemCount + '种商品</span>';
    }
    html += '<div class="cl"></div>';
    html += '</div>';
    html += '</dd>';

    return html;
}

function buildSinglePurchaseOrderContentHtml(purchaseOrder, shopDTO, orderPrefix) {
    if (G.Lang.isEmpty(purchaseOrder) || G.Lang.isEmpty(orderPrefix)) {
        return;
    }
    var html = '';
    var vestDateStr = G.Lang.normalize(purchaseOrder["vestDateStr"], "");  //下单时间
    var purchaseVestDate = G.Lang.normalize(purchaseOrder["deliveryDateStr"], "");  //买家期望交货
    var status = G.Lang.normalize(purchaseOrder["status"], "");  //状态
    var statusStr = '';
    if('PURCHASE_ORDER_REPEAL' == status) {
        statusStr = '买家终止交易';
    } else {
        statusStr = getOrderStatusStr(status);
    }


    var orderId = G.Lang.normalize(purchaseOrder["idStr"], "");  //采购单的Id
    var customerShopId = G.Lang.normalize(purchaseOrder["shopIdStr"],"");   //客户店铺Id
    var customerName = G.Lang.normalize(purchaseOrder["shopName"], "");  //客户名字
    var orderTotal = dataTransition.rounding(purchaseOrder["total"]);
    var promotionsInfoDTO = purchaseOrder["promotionsInfoDTO"];
    var bargainTotal, mljTotal, mjsStr, freeShippingStr, promotionsTotal;
    var originalTotal = orderTotal;
    var isHavePromotion = false;
    var isShortage = purchaseOrder["shortage"];

    if (!G.Lang.isEmpty(promotionsInfoDTO)) {
        if (promotionsInfoDTO["bargain"] && G.Lang.isNumber(promotionsInfoDTO["bargain"])) {
            isHavePromotion = true;
            bargainTotal = dataTransition.rounding(promotionsInfoDTO["bargain"], 2);
        }
        if (promotionsInfoDTO["mlj"] && G.Lang.isNumber(promotionsInfoDTO["mlj"])) {
            isHavePromotion = true;
            mljTotal = dataTransition.rounding(promotionsInfoDTO["mlj"], 2);
        }
        if (!G.Lang.isEmpty(promotionsInfoDTO["mjs"])) {
            isHavePromotion = true;
            mjsStr = promotionsInfoDTO["mjs"];
        }
        if (!G.Lang.isEmpty(promotionsInfoDTO["free_SHIPPING"])) {
            isHavePromotion = true;
            freeShippingStr = promotionsInfoDTO["free_SHIPPING"];
        }
        if (promotionsInfoDTO["promotionsTotal"] && G.Lang.isNumber(promotionsInfoDTO["promotionsTotal"])) {
            promotionsTotal = dataTransition.rounding(promotionsInfoDTO["promotionsTotal"], 2);
            originalTotal += promotionsTotal;
        }
    }

//            html += '<dl class="content-item" id="' + orderPrefix  + '.orderContent">';
    html += '<dt>';
    html += '<div class="bg-top-hr"></div>';
    html += '<div class="bar-tab">';
    html += '<input type="hidden" id="' + orderPrefix + '.id" value = "' + orderId + '">';
    html += '下单时间：' + vestDateStr + '&nbsp;';
    html += '买家期望交货：' + purchaseVestDate + '&nbsp;';

    html += '客户：<span class="button-spread J_open_shopDetail" data_id ="' + customerShopId + '">' + customerName + '</span>&nbsp;';

    html += '<a class="icon_contacts J_showContactsTip" shop_id="' + customerShopId + '"></a>';
    if(shopDTO) {
        html += generateContactTip(shopDTO);
    }
    html += '  <div class="cl"></div>';
    html += '</div>';
    html += '</dt>';
    html += '<dd>';
    html += '<table class="item-content" cellspacing="0" cellpadding="0">';
    var salesItemDTOs = purchaseOrder["itemDTOs"];

    if (!G.Lang.isEmpty(salesItemDTOs)) {
        var itemCount = salesItemDTOs.length;
        for (var n = 0; n < itemCount; n++) {
            var itemDTO = salesItemDTOs[n];
            var productInfo = getSaleOrderItemProductInfo(itemDTO);
            var quotedPrice = dataTransition.simpleRounding(itemDTO["quotedPrice"], 2);  // 销售价
            var price = dataTransition.simpleRounding(itemDTO["price"], 2);     //成交价
            var customPriceFlag = !!itemDTO["customPriceFlag"];     //客户议价的标志
            var quotedPreBuyFlag = !G.Lang.isEmpty(itemDTO["quotedPreBuyOrderItemId"]);     //卖家报价标志
            var amountUnit = dataTransition.simpleRounding(itemDTO["amount"], 2) + G.Lang.normalize(itemDTO["unit"]);
            var itemId = G.Lang.normalize(itemDTO["idStr"], "");
            var imageURL =itemDTO.imageCenterDTO.productListSmallImageDetailDTO.imageURL;
            var productLocalId = itemDTO.supplierProductIdStr;
            var itemPrefix = "itemDTOs" + n;
            if (n > 1) {
                html += '<tr style="display: none">';
            } else {
                html += '<tr>';
            }
            html += '<td class="item-product-info width-set">';
            html += '<a class="info-icon J_open_productHistory" id="' + orderPrefix + '.' + itemPrefix + '.image">';        //todo need to add img
            html += '<img src="'+imageURL+'"/>';
            html += '</a>';
            html += '<a class="info-details button-spread J_open_productHistory" id="' + orderPrefix + '.' + itemPrefix + '.productInfo">' + productInfo + '</a>';
            html += '<input type="hidden" id="' + orderPrefix + '.' + itemPrefix + '.id" value = "' + itemId + '">';
            html += '<input type="hidden" id="' + orderPrefix + '.' + itemPrefix + '.productLocalId" value = "' + productLocalId + '">';
            html += '<input type="hidden" id="' + orderPrefix + '.' + itemPrefix + '.orderType" value = "PURCHASE">';
            html += '<div class="cl"></div>';
            html += '</td>';
            html += '<td class="item-product-unit-price width-set">' + quotedPrice + '</td>';
            html += '<td class="item-product-quantity width-set">';
            html += '<div class="price-value">' + amountUnit + '</div>';
            html += '</td >';
            html += '<td class="item-product-price width-set">';
            html += '<div class="price-value">' + price + '</div>';
            if (quotedPreBuyFlag) {
                html += '<div class="price-description">我的报价</div>';
            } else if (customPriceFlag) {
                html += '<div class="price-description">买家期望价</div>';
            }
            html += '</td >';

            if (n == 0) {
                html += '<td class="item-product-payables width-set" rowspan="999">';
                if (originalTotal > orderTotal) {
                    html += '<div class="payables-original">' + dataTransition.rounding(originalTotal,2) + '</div>';
                }

                html += '<div class="payables-current">' + orderTotal + '</div>';
                if (isHavePromotion) {
                    html += '<div class="payables-description">';
                    html += '<div>促销优惠</div>';
                    if (bargainTotal > 0) {
                        html += '<div><span>特价商品：</span>优惠<span class="arialFont">&yen;</span> <span>' + bargainTotal + '</span></div>';
                    }
                    if (mljTotal > 0) {
                        html += '<div><span>满立减：</span>优惠<span class="arialFont">&yen;</span> <span>'+mljTotal+'</span></div>';
                    }
                    if (!G.Lang.isEmpty(mjsStr)) {
                        html += '<div><span>满就送：' + mjsStr + '</span></div>';
                    }
                    if (!G.Lang.isEmpty(freeShippingStr)) {
                        html += '<div><span>送货上门</span></div>';
                    }
                    html += '</div>';
                }
                html += '</td>';
                html += '<td class="item-product-order-status width-set" rowspan="999">';
                html += '<div class="status-description">' + statusStr + '</div>';
                html += '<div class="button-spread word-blue J_open_PurchaseOrder" data_id="' + orderId + '">订单详情</div>';
                html += '</td>';
                html += '<td class="item-product-operating width-set" rowspan="999">';
                if(status == 'SELLER_PENDING') {
                    html += '<div class="button-edit button-blue-gradient J_accept_salesOrder" data_id = "' + orderId + '" id="' + orderPrefix + '.dispatch" purchase_vest_date = "' + purchaseVestDate + '">接受</div>';
                    html += '<div class="button-edit button-blue-gradient J_refuse_salesOrder" data_id = "' + orderId + '" id="' + orderPrefix + '.dispatch">拒绝</div>';
                }
                html += '</td>';
            }
            html += '</tr>';
        }
    }
    html += '</table>';
    html += '<div class="item-control-bar">';
    if (itemCount > 2) {
        html += '<span class="button-spread J_more_saleOrderItem">共' + itemCount + '种商品，点击展开更多</span> ';
        html += '<span class="button-retract hidden J_less_saleOrderItem">共有' + itemCount + '种商品，点击收起</span>';
    } else {
        html += '<span class="word-blue">共' + itemCount + '种商品</span>';
    }
    html += '<div class="cl"></div>';
    html += '</div>';
    html += '</dd>';

    return html;
}

function drawSingleSaleOrder(saleOrder, customerDTO, orderPrefix) {
    if (G.Lang.isEmpty(saleOrder) || G.Lang.isEmpty(orderPrefix)) {
        return;
    }
    var html = buildSingleSaleOrderContentHtml(saleOrder, customerDTO, orderPrefix);
    var $orderContentDom = $("#" + orderPrefix + "//.orderContent");
    $orderContentDom.empty();
    $orderContentDom.append(html);
    $orderContentDom.find(".J_QQ").each(function (index, value) {
        $(this).multiQQInvoker({
            QQ: $(this).attr("data_qq")
        });
    });
}

//缺料
function toShotageInventory(salesOrderId,productId){
    if(stringUtil.isEmpty(salesOrderId) && stringUtil.isEmpty(productId)){
        return;
    }
    var url= "storage.do?method=getProducts&goodsindexflag=gi&productId=" + productId + "&salesOrderId=" + salesOrderId;
    window.open(url);
}
