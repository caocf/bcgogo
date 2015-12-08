;
$(function(){
    drayMyPurchaseOrderNumbers();

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
    $("#supplierInfoSearchText")
        .bind('click', function () {
            supplierSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                supplierSuggestion($(this));
            }
        })
        .bind('keypress', function (event) {
            var keyName = G.keyNameFromEvent(event);
            if (keyName === "enter") {
                //触发 模糊库存查询
                getProductWithUnknownField();
            }
        });
    $("#startDate,#endDate")
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
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            if (startDate == "" || endDate == "") return;
            if (startDate > endDate) {
                $("#endDate").val(startDate);
                $("#startDate").val(endDate);
            }
        }).click(function () {
            $(this).blur();
        }).change(function () {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            if (endDate == "" || startDate == "") {
                return;
            }
            if (startDate > endDate) {
                $("#endDate").val(startDate);
                $("#startDate").val(endDate);
            }
        });

    $("#clearSearchField").bind("click", function () {
        clearSearchField();
    });

    $("#my_date_self_defining").click();



    $("#searchOnlinePurchaseOrders").bind("click",function(){
        $("#inventoryVestStartDate,#inventoryVestEndDate").val("");
        searchPurchaseOrders();
    });

     var linkIdArray = ["purchase_new","purchase_today_new","purchase_early_new","purchase_in_progress",
         "purchase_seller_stock","purchase_seller_dispatch","purchase_seller_stop","purchase_seller_refused",
         "purchase_done","purchase_today_done","purchase_early_done"];

    $.each(linkIdArray,function(index,value){
     $("#"+value).parent().bind("click",function(){
         clearSearchField();
         var $orderStatus =  $("#orderStatus");
         switch (value) {
             case "purchase_today_new":
                 $orderStatus.val("SELLER_PENDING");
                  $("#startDate,#endDate").val(dateUtil.getToday());
                 break;
             case "purchase_early_new":
                 $orderStatus.val("SELLER_PENDING");
                 $("#endDate").val(dateUtil.getYesterday());
                 break;
             case  "purchase_new":
                 $orderStatus.val("SELLER_PENDING");
                 break;
             case  "purchase_in_progress":
                 $orderStatus.val("SELLER_STOCK,SELLER_DISPATCH,PURCHASE_SELLER_STOP,SELLER_REFUSED");
                 break;
             case  "purchase_seller_stock":
                 $orderStatus.val("SELLER_STOCK");
                 break;
             case  "purchase_seller_dispatch":
                 $orderStatus.val("SELLER_DISPATCH");
                 break;
             case  "purchase_seller_stop":
                 $orderStatus.val("PURCHASE_SELLER_STOP");
                 break;
             case  "purchase_seller_refused":
                 $orderStatus.val("SELLER_REFUSED");
                 break;
             case  "purchase_done":
                 $orderStatus.val("PURCHASE_ORDER_DONE");
                 break;
             case  "purchase_today_done":
                 $("#inventoryVestStartDate,#inventoryVestEndDate").val(dateUtil.getToday());
                 $orderStatus.val("PURCHASE_ORDER_DONE");
                 break;
             case  "purchase_early_done":
                 $("#inventoryVestEndDate").val(dateUtil.getYesterday());
                 $orderStatus.val("PURCHASE_ORDER_DONE");
                 break;
         }
         searchPurchaseOrders();
     });
    });

    $(".J_open_purchaseOrder").live("click",function(){
        var data_id = $(this).attr("data_id");
       if(!G.Lang.isEmpty(data_id) && G.Lang.isNumber(data_id)){
           window.open("RFbuy.do?method=show&id="+data_id,"_blank");
       }
    });

    $(".J_open_purchaseInventoryOrder").live("click",function(){
        var data_id = $(this).attr("data_id");
        if (!G.Lang.isEmpty(data_id) && G.Lang.isNumber(data_id)) {
            window.open("storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=" + data_id, "_blank");
        }
    });

    $(".J_open_shopDetail").live("click",function(){
        var data_id = $(this).attr("data_id");
       if(!G.Lang.isEmpty(data_id) && G.Lang.isNumber(data_id)){
           window.open("shopMsgDetail.do?method=renderShopMsgDetail&paramShopId="+data_id,"_blank");
       }
    });

    $(".J_more_purchaseOrderItem").live("click",function(){
        $(this).parent().prev("table").find("tr:gt(1)").show();
        $(this).addClass("hidden");
        $(this).next(".J_less_purchaseOrderItem").removeClass("hidden");

    });
    $(".J_less_purchaseOrderItem").live("click",function(){
        $(this).parent().prev("table").find("tr:gt(1)").hide();
        $(this).addClass("hidden");
        $(this).prev(".J_more_purchaseOrderItem").removeClass("hidden");
    });

    $(".J_open_productHistory").live("click", function () {
        var domIdArray = $(this).attr("id").split(".");
        var orderPrefix =  domIdArray[0];
        var itemPrefix =  domIdArray[1];
        var orderId = $("#" + orderPrefix + "\\.id").val();
        var itemId = $("#" + orderPrefix +"\\."+itemPrefix+ "\\.id").val();
        var paramShopId = $("#" + orderPrefix +"\\."+itemPrefix+ "\\.paramShopId").val();
        var productLocalId = $("#" + orderPrefix +"\\."+itemPrefix+ "\\.productLocalId").val();
        var orderType = "PURCHASE";
        if (!G.Lang.isEmpty(orderId) && G.Lang.isNumber(orderId)
            && !G.Lang.isEmpty(itemId) && G.Lang.isNumber(itemId)
            ) {
            var url = "shopProductDetail.do?method=toShopProductDetail&paramShopId="+paramShopId+"&productLocalId="+ productLocalId +"&orderItemId="+itemId+"&orderType=PURCHASE&itemFrom=order";
            window.open(url, "_blank");
        }
    });

    $(".J_modify_purchaseOrder").live("click", function () {
        var orderId = $(this).attr("data_id");
        var orderPrefix = $(this).attr("id").split(".")[0];
        function validatePurchaseOrderStatus(result) {
            if (result && result.success) {
                var purchaseOrderDTOs, purchaseOrderDTO, supplierDTOs, supplierDTO, shopDTOs, shopDTO;
                if (result && result["results"]) {
                    purchaseOrderDTOs = result["results"];
                    if (result && result["data"]){
                        if(result["data"]["supplierDTOs"]) {
                            supplierDTOs = $.extend({}, result["data"]["supplierDTOs"]);
                        }
                        if(result["data"]["shopDTOs"]){
                            shopDTOs = result["data"]["shopDTOs"];
                        }
                    }
                    if (!G.Lang.isEmpty(purchaseOrderDTOs)) {
                        for (var i = 0; i < purchaseOrderDTOs.length; i++) {
                            if (!G.Lang.isEmpty(purchaseOrderDTOs[i]) && purchaseOrderDTOs[i]["idStr"] == orderId) {
                                purchaseOrderDTO = purchaseOrderDTOs[i];
                                var supplierId = G.Lang.normalize(purchaseOrderDTO["supplierIdStr"], "");  //供应商Id
                                supplierDTO = supplierDTOs[supplierId];
                                shopDTO = shopDTOs[purchaseOrderDTO.supplierShopIdStr];
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
                        drawSinglePurchaseOrder(purchaseOrderDTO, supplierDTO, shopDTO, orderPrefix);
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
                var purchaseOrderDTOs, purchaseOrderDTO, supplierDTOs, supplierDTO, shopDTOs, shopDTO;
                if (result && result["results"]) {
                    purchaseOrderDTOs = result["results"];
                    if (result && result["data"]){
                        if(result["data"]["supplierDTOs"]) {
                            supplierDTOs = $.extend({}, result["data"]["supplierDTOs"]);
                        }
                        if(result["data"]["shopDTOs"]){
                            shopDTOs = result["data"]["shopDTOs"];
                        }
                    }
                    if (!G.Lang.isEmpty(purchaseOrderDTOs)) {
                        for (var i = 0; i < purchaseOrderDTOs.length; i++) {
                            if (!G.Lang.isEmpty(purchaseOrderDTOs[i]) && purchaseOrderDTOs[i]["idStr"] == orderId) {
                                purchaseOrderDTO = purchaseOrderDTOs[i];
                                var supplierId = G.Lang.normalize(purchaseOrderDTO["supplierIdStr"], "");  //供应商Id
                                supplierDTO = supplierDTOs[supplierId];
                                shopDTO = shopDTOs[purchaseOrderDTO.supplierShopIdStr];
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
                        drawSinglePurchaseOrder(purchaseOrderDTO, supplierDTO, shopDTO, orderPrefix);
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
                var purchaseOrderDTOs, purchaseOrderDTO, supplierDTOs, supplierDTO, shopDTOs, shopDTO;
                if (result && result["results"]) {
                    purchaseOrderDTOs = result["results"];
                    if (result && result["data"]){
                        if(result["data"]["supplierDTOs"]) {
                            supplierDTOs = $.extend({}, result["data"]["supplierDTOs"]);
                        }
                        if(result["data"]["shopDTOs"]){
                            shopDTOs = result["data"]["shopDTOs"];
                        }
                    }
                    if (!G.Lang.isEmpty(purchaseOrderDTOs)) {
                        for (var i = 0; i < purchaseOrderDTOs.length; i++) {
                            if (!G.Lang.isEmpty(purchaseOrderDTOs[i]) && purchaseOrderDTOs[i]["idStr"] == orderId) {
                                purchaseOrderDTO = purchaseOrderDTOs[i];
                                var supplierId = G.Lang.normalize(purchaseOrderDTO["supplierIdStr"], "");  //供应商Id
                                supplierDTO = supplierDTOs[supplierId];
                                shopDTO = shopDTOs[purchaseOrderDTO.supplierShopIdStr];
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
                                window.open("RFbuy.do?method=purchaseOrderRepeal&id=" + orderId);
                                setTimeout(function(){
                                    $("#searchOnlinePurchaseOrders").click();
                                }, 1000);
                            }
                        });
                    } else {
                        nsDialog.jAlert(confirmMsg);
                        drawSinglePurchaseOrder(purchaseOrderDTO, supplierDTO, shopDTO, orderPrefix);
                    }
                }
            }
        }

        getPurchaseOrderById(orderId, validatePurchaseOrderStatus);
    });

    $(".J_return_purchaseOrder").live("click",function(){
        var orderId = $(this).attr("data_id");
        if(!G.Lang.isEmpty(orderId) && GLOBAL.Lang.isNumber(orderId)){
            window.open("onlineReturn.do?method=onlinePurchaseReturnEdit&purchaseOrderId=" + orderId,"_blank");
        }
    });

    $(".J_return_item_purchaseOrder").live("click",function(){
        var itemId = $(this).attr("data_item_id");
        var orderId = $(this).attr("data_order_id");
        if(!G.Lang.isEmpty(orderId) && GLOBAL.Lang.isNumber(orderId) &&!G.Lang.isEmpty(itemId) && GLOBAL.Lang.isNumber(itemId) ){
            window.open("onlineReturn.do?method=onlinePurchaseReturnEdit&purchaseOrderId=" + orderId + "&purchaseOrderItemIds=" + itemId, "_blank");
        }
    });


    //添加评论
    $(".J_supplier_comment").live("click", function () {

        var idPrefix = $(this).attr("id").split(".")[0];
        var purchaseInventoryId = $("#" + idPrefix + "\\.purchaseInventoryId").val();
        if (!G.Lang.isEmpty(purchaseInventoryId)) {
            initSupplierComment();
            $("#purchaseInventoryIdStr").val(purchaseInventoryId);
            $("#supplierCommentForm")[0].reset();
            $("#supplierCommentDiv").dialog({
                width: 520,
                modal: true
            });
//            $("#supplierCommentDiv").css("display", "block").dialog({ width: 520, modal: true});
            $("#supplierCommentContent").val("");
            $("#supplierCommentContentRemain").text("500");
        }
    });

    //评价供应商取消按钮
    $("#commentCancelBtn").bind("click", function () {
        $("#supplierCommentForm")[0].reset();
        $("#supplierCommentDiv").css("display", "none").dialog("close");
    });
    //提交评论
    $("#commentConfirmBtn").bind("click", function () {
        $("#commentConfirmBtn").attr("disabled", true);
        $("#supplierCommentForm").ajaxSubmit({
            url: "storage.do?method=saveSupplierComment",
            dataType: "json",
            type: "POST",
            success: function (json) {
                if (json.success) {
                    $("#commentConfirmBtn").attr("disabled", false);
                    nsDialog.jAlert("您的评价已发表成功!1天后生效", "", function () {
                        $("#supplierCommentForm")[0].reset();
                        $("#supplierCommentDiv").css("display", "none").dialog("close");
                        searchPurchaseOrders();
//                        window.location.href = "RFbuy.do?method=show&id=" + $("#id").val();
                    });
                } else {
                    $("#commentConfirmBtn").attr("disabled", false);
                    nsDialog.jAlert(json.msg);
                    return false;
                }
            },
            error: function (json) {
                $("#commentConfirmBtn").attr("disabled", false);
                nsDialog.jAlert("网络异常，请联系客服");
            }
        });
    });

    //追加评论
    $(".J_add_comment").live("click", function () {
        $("#supplierCommentRecordIdStr").val($(this).attr("data_id"));
        $("#addSupplierCommentDiv").dialog({ width: 520, modal: true});
        $("#addCommentContentRemain").text("500");
        $("#addCommentContent").val("");
    });

    //取消追加评论
    $("#addCommentCancelBtn").bind("click", function() {
       $("#addSupplierCommentForm")[0].reset();
       $("#supplierCommentDiv").css("display", "none").dialog("close");
       $("#supplierCommentSuccess").css("display", "none").dialog("close");
       $("#addSupplierCommentDiv").css("display", "none").dialog("close");
     });

    //确定追加评论
     $("#addCommentConfirmBtn").bind("click", function() {

       $("#addCommentConfirmBtn").attr("disabled", true);
       $("#addSupplierCommentForm").ajaxSubmit({
         url: "storage.do?method=addSupplierComment",
         dataType: "json",
         type: "POST",
         success: function(json) {
           if (json.success) {
             $("#addCommentConfirmBtn").attr("disabled", false);
             nsDialog.jAlert("您的评价已追加成功!", "", function () {
               $("#supplierCommentForm")[0].reset();
               $("#supplierCommentDiv").css("display", "none").dialog("close");
               $("#addSupplierCommentDiv").css("display", "none").dialog("close");
                 searchPurchaseOrders();
//               window.location.href = "RFbuy.do?method=show&id=" + $("#id").val();

             });

           } else if (!json.success) {
             $("#addCommentConfirmBtn").attr("disabled", false);
             nsDialog.jAlert(json.msg);
             return false;
           }
         },
         error: function(json) {
           $("#addCommentConfirmBtn").attr("disabled", false);
           nsDialog.jAlert("网络异常，请联系客服");
         }
       });
     });

    var urlParam = GLOBAL.Util.getUrlParameter("filterParam");
    if(!G.isEmpty(urlParam)){
        $("#"+urlParam).parent().click();
    }else{
        $("#orderStatus option:contains('待办采购单')").attr("selected", true);
        searchPurchaseOrders();
    }

});
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

function clearSearchField() {
    $("#inventoryVestStartDate,#inventoryVestEndDate").val("");
    $(".J-productSuggestion").val("");
    $("#receiptNo,#supplierInfoSearchText,#startDate,#endDate").val("");
    $("#date_default").attr("checked", "checked");
    $("#orderStatus").find("option").eq(0).attr("selected", "selected");
    $("#commentStatus").find("option").eq(0).attr("selected", "selected");
    if(!$("#my_date_self_defining").hasClass("clicked")){
        $("#my_date_self_defining").click();
    }else{
        $("#my_date_self_defining").click();
        $("#my_date_self_defining").click();
    }
}

function drayMyPurchaseOrderNumbers() {
    var url = "orderCenter.do?method=getOnlinePurchaseOrderCount";
    bcgogoAjaxQuery.setUrlData(url, {});
    bcgogoAjaxQuery.ajaxQuery(function (json) {
        if (json && json.success) {
            var result = json.data;
            $("#purchase_today_new").text("(" + G.Lang.normalize(result["purchase_today_new"], "0") + ")");
            $("#purchase_early_new").text("(" + G.Lang.normalize(result["purchase_early_new"], "0") + ")");
            $("#purchase_new").text(G.Lang.normalize(result["purchase_new"], "0"));
            $("#purchase_seller_stock").text("(" + G.Lang.normalize(result["purchase_seller_stock"], "0") + ")");
            $("#purchase_seller_dispatch").text("(" + G.Lang.normalize(result["purchase_seller_dispatch"], "0") + ")");
            $("#purchase_seller_stop").text("(" + G.Lang.normalize(result["purchase_seller_stop"], "0") + ")");
            $("#purchase_seller_refused").text("(" + G.Lang.normalize(result["purchase_seller_refused"], "0") + ")");
            $("#purchase_in_progress").text(G.Lang.normalize(result["purchase_in_progress"], "0"));
            $("#purchase_today_done").text("(" + G.Lang.normalize(result["purchase_today_done"], "0") + ")");
            $("#purchase_early_done").text("(" + G.Lang.normalize(result["purchase_early_done"], "0") + ")");
            $("#purchase_done").text(G.Lang.normalize(result["purchase_done"], "0"));
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

function supplierSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var dropList = APP_BCGOGO.Module.droplist;
    dropList.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        searchWord: searchWord,
        customerOrSupplier: "supplier",
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
                dropList.hide();
            }, "onKeyboardSelect": function (event, index, data) {
                $domObject.val(data.details["name"]);
            }
        });
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
    data["maxRows"] = 5;
    APP_BCGOGO.Net.syncPost({
        url: "orderCenter.do?method=getOnlinePurchaseOrders",
        dataType: "json",
        data: data,
        success: function (result) {
            drawPurchaseOrderList(result);
            initPage(result, "purchaseOrderList", "orderCenter.do?method=getOnlinePurchaseOrders", '', "drawPurchaseOrderList", '', '', data, '');
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
function drawPurchaseOrderList(data){
   $(".J-PurchaseOrdersList").empty();
    var purchaseOrders,purchaseInventoryOrders,supplierDTOs, shopDTOs;
    if(data && data["results"]){
        purchaseOrders = data["results"];
    }
//    if(data && data["data"] && data["data"]["purchaseInventoryOrders"]){
//        purchaseInventoryOrders = data["data"]["purchaseInventoryOrders"];
//    }
    if(data && data["data"]){
        if(data["data"]["supplierDTOs"]){
            supplierDTOs = $.extend({}, data["data"]["supplierDTOs"]);
        }
        if(data["data"]["shopDTOs"]){
            shopDTOs = data["data"]["shopDTOs"];
        }
    }
    if(G.Lang.isEmpty(purchaseOrders)){
        var emptyHtml = '<dl class="content-item"><dd><table class="item-content"><tr><td>对不起，没有找到您要的单据信息!</td></tr></table></dd></dl>';   //
        $(".J-PurchaseOrdersList").append(emptyHtml);
    }else{
        for (var i = 0; i < purchaseOrders.length; i++) {
            var html = '';
            var purchaseOrder =  purchaseOrders[i];
            var orderPrefix = "orderDTOs" + i;
            var supplierId = G.Lang.normalize(purchaseOrder["supplierIdStr"], "");  //供应商Id
            var supplierDTO =  supplierDTOs[supplierId];
            var shopDTO = shopDTOs[purchaseOrder["supplierShopIdStr"]];
            html += '<dl class="content-item" id="' + orderPrefix + '.orderContent">';
            html += buildSinglePurchaseOrderContentHtml(purchaseOrder, supplierDTO, shopDTO, orderPrefix);
            html += '</dl>';
            $(".J-PurchaseOrdersList").append(html);
        }
        $(".J_QQ").each(function (index, value) {
            $(this).multiQQInvoker({
                QQ: $(this).attr("data_qq")
            });
        });
    }
}

function getPurchaseOrderItemProductInfo(itemDTO) {
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

function buildSinglePurchaseOrderContentHtml(purchaseOrder, supplierDTO, shopDTO, orderPrefix) {
    if (G.Lang.isEmpty(purchaseOrder) || G.Lang.isEmpty(orderPrefix)) {
            return;
        }
        var html = '';
    var receiptNo = G.Lang.normalize(purchaseOrder["receiptNo"], "--");
    var vestDateStr = G.Lang.normalize(purchaseOrder["vestDateStr"], "");  //下单时间
    var deliveryDateStr = G.Lang.normalize(purchaseOrder["deliveryDateStr"], "");  //期望交货时间
    var preDispatchDateStr = G.Lang.normalize(purchaseOrder["preDispatchDateStr"], "");  //预计送达时间
    var status = G.Lang.normalize(purchaseOrder["status"], "");  //状态
    var statusStr = '';
    if('SELLER_PENDING' == status) {
        statusStr = '待卖家处理';
    } else if('SELLER_REFUSED' == status) {
        statusStr = '卖家已拒绝';
    } else {
        statusStr = getOrderStatusStr(status);
    }
    var purchaseInventoryOrderId = G.Lang.normalize(purchaseOrder["purchaseInventoryIdStr"], "");  //关联入库单的Id
    var purchaseInventoryReceiptNo = G.Lang.normalize(purchaseOrder["purchaseInventoryReceiptNo"], "");  //关联入库单的单据号
    var orderId = G.Lang.normalize(purchaseOrder["idStr"], "");  //采购单的Id
    var supplierId = G.Lang.normalize(purchaseOrder["supplierIdStr"], "");  //供应商Id
    var supplierShopId = G.Lang.normalize(purchaseOrder["supplierShopIdStr"], "");  //供应商Id
    var qqArray = null;
    if(G.Lang.isNotEmpty(supplierDTO)){
        qqArray =  getSupplierContactQQ(supplierDTO);
    }else{
        qqArray = getShopContactQQ(shopDTO);
    }

    var supplierName = G.Lang.normalize(purchaseOrder["supplier"], "");  //供应商Id
    var isHasComment = !G.Lang.isEmpty(purchaseOrder["supplierCommentRecordIdStr"]); //是否已经有评价了 true 表示有
    var supplierCommentRecordId = G.Lang.normalize(purchaseOrder["supplierCommentRecordIdStr"]);
    var isAddContent = purchaseOrder["addContent"]; //是否可以追加评价
    var orderTotal = dataTransition.rounding(purchaseOrder["total"]);
    var promotionsInfoDTO = purchaseOrder["promotionsInfoDTO"];
    var bargainTotal, mljTotal, mjsStr, freeShippingStr, promotionsTotal;
    var originalTotal = orderTotal;
    var isHavePromotion = false;
    var isShortage = purchaseOrder["shortage"];   //relation salesOrder isShortage

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
    html += '<input type="hidden" id="' + orderPrefix + '.purchaseInventoryId" value = "' + purchaseInventoryOrderId + '">';
    html += '采购单号：<span class="button-spread J_open_purchaseOrder" data_id = "' + orderId + '">' + receiptNo + '</span>&nbsp;';
    html += '下单时间：' + vestDateStr + '&nbsp;';
    //对方已经发货显示预计送达时间，已经入库显示入库单号
    if (status == "SELLER_DISPATCH" || status == "SELLER_STOCK") {
        html += '预计送达时间：' + preDispatchDateStr + '&nbsp;';
    } else if (status == "PURCHASE_ORDER_DONE") {
        html += '入库单号：<span class="button-spread J_open_purchaseInventoryOrder" data_id = "' + purchaseInventoryOrderId + '">' + purchaseInventoryReceiptNo + '</span>&nbsp;';
    }
    html += '供应商：<span class="button-spread J_open_shopDetail" data_id ="' + supplierShopId + '">' + supplierName + '</span>&nbsp;';
    html += '<a class="J_QQ" data_qq="' + qqArray + '"></a>';
    if (status == "PURCHASE_ORDER_DONE") {
        if (isAddContent) {
            html += '<span class="button-rate-additional J_add_comment blue_color" id="' + orderPrefix + '.addComment" data_id ="' + supplierCommentRecordId + '">追加评论</span>';
        } else if (!isHasComment) {
            html += '<span class="button-rate button-yellow-deep-gradient J_supplier_comment" id="' + orderPrefix + '.supplierComment">评价</span>';
        }
    }
    html += '  <div class="cl"></div>';
    html += '</div>';
    html += '</dt>';
    html += '<dd>';
    html += '<table class="item-content" cellspacing="0" cellpadding="0">';
    var purchaseInventoryItemDTOs = purchaseOrder["itemDTOs"];

    if (!G.Lang.isEmpty(purchaseInventoryItemDTOs)) {
        var itemCount = purchaseInventoryItemDTOs.length;
        for (var n = 0; n < itemCount; n++) {
            var itemDTO = purchaseInventoryItemDTOs[n];
            var productInfo = getPurchaseOrderItemProductInfo(itemDTO);
            var quotedPrice = dataTransition.simpleRounding(itemDTO["quotedPrice"], 2);  // 销售价
            var price = dataTransition.simpleRounding(itemDTO["price"], 2);     //成交价
            var customPriceFlag = !!itemDTO["customPriceFlag"];     //客户议价的标志
            var quotedPreBuyFlag = !G.Lang.isEmpty(itemDTO["quotedPreBuyOrderItemId"]);     //卖家报价标志
            var amountUnit = dataTransition.simpleRounding(itemDTO["amount"], 2) + G.Lang.normalize(itemDTO["unit"]);
            var itemId = G.Lang.normalize(itemDTO["idStr"], "");
            var imageURL =itemDTO.imageCenterDTO.productListSmallImageDetailDTO.imageURL;
            var supplierProductId = itemDTO.supplierProductIdStr;
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
            html += '<a class="info-details button-spread J_open_productHistory" id="' + orderPrefix + '.' + itemPrefix + '.productInfo" >' + productInfo + '</a>';
            html += '<input type="hidden" id="' + orderPrefix + '.' + itemPrefix + '.id" value = "' + itemId + '">';
            html += '<input type="hidden" id="' + orderPrefix + '.' + itemPrefix + '.paramShopId" value = "' + supplierShopId + '">';
            html += '<input type="hidden" id="' + orderPrefix + '.' + itemPrefix + '.productLocalId" value = "' + supplierProductId + '">';
            html += '<div class="cl"></div>';
            html += '</td>';
            html += '<td class="item-product-unit-price width-set">' + quotedPrice + '</td>';
            html += '<td class="item-product-quantity width-set">' + amountUnit + '</td>';
            html += '<td class="item-product-price width-set">';
            html += '<div class="price-value">' + price + '</div>';
            if (quotedPreBuyFlag) {
                html += '<div class="price-description">卖家报价</div>';
            } else if (customPriceFlag) {
                html += '<div class="price-description">我的期望价</div>';
            }
            html += '</td >';
            html += '<td class="item-product-goods-of-return width-set">';
            if (status == "PURCHASE_ORDER_DONE") {
                html += ' <a class="button-goods-of-return word-blue a-button J_return_item_purchaseOrder" data_item_id="'
                    + itemId + '" data_order_id="' + orderId + '">退货</a>';
            } else {
                html += '--';
            }
            html += '</td>';
            if (n == 0) {
                html += '<td class="item-product-payables width-set" rowspan="999">';
                if (originalTotal > orderTotal) {
                    html += '<div class="payables-original">' + dataTransition.rounding(originalTotal) + '</div>';
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
                html += '<div class="order-details word-blue a-button J_open_purchaseOrder" data_id="' + orderId + '">订单详情</div>';
                html += '</td>';
                html += '<td class="item-product-operating width-set" rowspan="999">';
                if ($.inArray(status, ["SELLER_DISPATCH", "PURCHASE_ORDER_WAITING"]) >= 0 || (status == "SELLER_STOCK" && !isShortage)) {
                    html += '<div class="button-storage button-yellow-deep-gradient J_toStorage_purchaseOrder" data_id=' + orderId + ' id="' + orderPrefix + '.toStorage">入库</div>';
                }
                if ($.inArray(status, ["SELLER_PENDING"]) >= 0) {
                    html += '<div class="button-edit button-blue-gradient J_modify_purchaseOrder" data_id = "' + orderId + '" id="' + orderPrefix + '.modify">修改</div>';
                }
                if ($.inArray(status, ["PURCHASE_ORDER_DONE"]) >= 0) {
                    html += '<div class="button-confirm-receipt button-blue-gradient J_return_purchaseOrder" data_id="'+orderId+'" id="' + orderPrefix + '.modify">确认退货</div>';
                }
                if ($.inArray(status, ["SELLER_PENDING", "SELLER_REFUSED", "PURCHASE_SELLER_STOP"]) >= 0) {
                    html += '<div class="button-void J_nullify_purchaseOrder" data_id = "' + orderId + '" id="' + orderPrefix + '.nullify">作废</div>';
                }
                html += '</td>';
            }
            html += '</tr>';
        }
    }
    html += '</table>';
    html += '<div class="item-control-bar">';
    if (itemCount > 2) {
        html += '<span class="button-spread J_more_purchaseOrderItem">共' + itemCount + '种商品，点击展开更多</span> ';
        html += '<span class="button-retract hidden J_less_purchaseOrderItem">共有' + itemCount + '种商品，点击收起</span>';
    } else {
        html += '<span class="word-blue">共' + itemCount + '种商品</span>';
    }
    html += '<div class="cl"></div>';
    html += '</div>';
    html += '</dd>';

    return html;

//        $("#" + orderPrefix + "//.orderContent").empty();
//        $("#" + orderPrefix + "//.orderContent").append(html);
//
//        $("#" + orderPrefix + "//.orderContent").find(".J_QQ").each(function (index, value) {
//            $(this).multiQQInvoker({
//                QQ: $(this).attr("data_qq")
//            });
//        });
}

function drawSinglePurchaseOrder(purchaseOrder, supplierDTO, shopDTO, orderPrefix) {
    if (G.Lang.isEmpty(purchaseOrder) || G.Lang.isEmpty(orderPrefix)) {
        return;
    }
    var html = buildSinglePurchaseOrderContentHtml(purchaseOrder, supplierDTO,shopDTO, orderPrefix);
    var $orderContentDom = $("#" + orderPrefix + "//.orderContent");
    $orderContentDom.empty();
    $orderContentDom.append(html);
    $orderContentDom.find(".J_QQ").each(function (index, value) {
        $(this).multiQQInvoker({
            QQ: $(this).attr("data_qq")
        });
    });
}

function getRemainChar(object) {
  var length = $(object).val().length;
  if (length > 500) {
    $(object).val($(object).val().substring(0, 500));
    length = 500;
  }
  $("#" + $(object).attr("id") + "Remain").text(500 - length);
}

function initSupplierComment() {
    $("#supplierCommentContent").val("");
    $("#supplierCommentContentRemain").text("500");
    $("#qualityScoreDiv,#performanceScoreDiv,#speedScoreDiv,#attitudeScoreDiv").empty();
    $("#qualityScoreDivHidden,#performanceScoreDivHidden,#speedScoreDivHidden,#attitudeScoreDivHidden").val("");
    var qualityScoreRatting = new App.Module.Ratting();
    qualityScoreRatting.show({
      score:{
        total:10,
        current:0
      },
      config:{
        starType:"yellow_big",
        isLocked:false,
        isOneOff:false,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.qualityTip
      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#qualityScoreDiv",
      hiddenScore:"#qualityScoreDivHidden"
    });


    var performanceScoreRatting = new App.Module.Ratting();
    performanceScoreRatting.show({
      score:{
        total:10,
        current:0
      },
      config:{
        starType:"yellow_big",
        isLocked:false,
        isOneOff:false,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.performanceTip

      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#performanceScoreDiv",
      hiddenScore:"#performanceScoreDivHidden"
    });

    var speedScoreRatting = new App.Module.Ratting();
    speedScoreRatting.show({
      score:{
        total:10,
        current:0
      },
      config:{
        starType:"yellow_big",
        isLocked:false,
        isOneOff:false,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.speedTip

      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#speedScoreDiv",
      hiddenScore:"#speedScoreDivHidden"
    });

    var attitudeScoreRatting = new App.Module.Ratting();
    attitudeScoreRatting.show({
      score:{
        total:10,
        current:0
      },
      config:{
        starType:"yellow_big",
        isLocked:false,
        isOneOff:false,
        isPrompt:true,
        tip:App.Module.Data.rattingTipContents.attitudeTip

      },
      onRate:function(event, data) {
        G.info(data);
      },
      selector:"#attitudeScoreDiv",
      hiddenScore:"#attitudeScoreDivHidden"
    });

}


