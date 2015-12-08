/** controller */
$(document).ready(function () {

    // 商品搜索Field相关注册绑定
    productSearchFieldBind();

    // 客户信息下拉的绑定
    customerInfoSuggestionBind();

    // 时间相关的查询条件的绑定
    timeConditionBind();

    resetBind();

    doSearchBind();

    overrideFormSubmitBind();

    orderOperationBind();

    buttonSpreadRetractBind();

    $(".J_status_link").bind("click", function(){
        var status = $(this).attr("paramStatus");
        var startDate = $(this).attr("paramStartDate");
        var endDate = $(this).attr("paramEndDate");
        searchWithPreConditions(status, startDate, endDate);
    });

    // 供求中心首页跳转时带条件的处理
    var status = GLOBAL.Util.getUrlParameter("orderStatus");
    var startDate = GLOBAL.Util.getUrlParameter("startTimeStr");
    var endDate = GLOBAL.Util.getUrlParameter("endTimeStr");
    if(G.isEmpty(status)){
        status = $("#orderStatus option:contains('待办销售退货单')").val();
    }
    searchWithPreConditions(status, startDate, endDate);

});



/* handler bindings */

/**
 *  产品搜索相关field的绑定
 */
function productSearchFieldBind() {
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
}

/**
 *   用户信息下拉提示
 */
function customerInfoSuggestionBind() {
    $("#customerInfo")
        .bind('click', function (e) {
            getCustomerSuggestion($(this), e);
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getCustomerSuggestion(this, event);
            }
        })
        .bind('blur', function (event) {
            syncGetCustomerSuggestion(this, event);
        })
        .bind('change', function (event) {
            clearCustomerId();
            getCustomerSuggestion(this, event);
        })
        .bind('focus', function (event) {
            getCustomerSuggestion(this, event);
        })
        .placeHolder();
}

function clearCustomerId() {
    $('#customerId').val("");
}


/**
 * 日期查询条件的相关绑定
 */
function timeConditionBind() {

    datePickerBind();



}

function datePickerBind() {
    $("#startDate,#endDate")
        .datepicker({
            "numberOfMonths": 1,
            "showButtonPanel": false,
            "changeYear": true,
            "showHour": false,
            "showMinute": false,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": ""
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
}


/**
 * 查询条件重置
 */
function resetBind() {
    $("#reset").bind("click", function () {
        $("#orderSearchForm").resetForm();
        if(!$("#my_date_self_defining").hasClass("clicked")){
            $("#my_date_self_defining").click();
        }else{
            $("#my_date_self_defining").click();
            $("#my_date_self_defining").click();
        }
    });
}

/**
 * 搜索
 */
function doSearchBind() {
    $("#doSearch").click(function () {
        $("#orderSearchForm").submit();
    });
}

function overrideFormSubmitBind() {
    $("#orderSearchForm").submit(function (e) {
        e.preventDefault();
        e.stopPropagation();
        var paramJson = prepareQueryCondition();
        var url = "/web/onlineSalesReturnOrder.do?method=onlineSalesReturnOrderSearch";
        APP_BCGOGO.Net.syncPost({
            url: url,
            dataType: "json",
            data: paramJson,
            success: function (data) {
                $(".content-item").remove();
                if (!G.isEmpty(data)) {
                    initTable(data);
                    // maybe init fields not in table
                    var functionName = "initTable", dynamical = "dynamical1";
                    initPage(data, dynamical, url, '', functionName, '', '', paramJson, '');
                }
            },
            error: function () {
                alert("网络异常，请联系客服");
            }
        });
    });
}

function prepareQueryCondition() {
    var param = $("#orderSearchForm").serializeArray(); // parse to json
    var paramJson = {};
    $.each(param, function (index, val) {
        paramJson[val.name] = val.value;
    });
    return paramJson;
}

function searchWithPreConditions(status, startDate, endDate){
    $("#orderSearchForm").resetForm();
    $("#orderStatus").val(status);
    $("#startDate").val(startDate);
    $("#endDate").val(endDate);
    $("#doSearch").trigger("click");
}

function initTable(data) {
    var orderList = "";
    if (data && data.results) {
        for (var i = 0; i < data.results.length; i++) {
            var item = data['results'][i];
            var customerShopIdStr = item.customerShopIdStr;
            var shopDTO = data['data']['shopDTOs'][customerShopIdStr];
            var orderHtml = renderSalesReturnOrder(item, shopDTO);
            if (orderHtml) {
                orderList += orderHtml;
            }
        }
    }
    if (orderList) {
        $('.list-content').append($(orderList));
    }
}

function renderSalesReturnOrder(jsonOrder, shopDTO) {

    if (jsonOrder) {
        var template = '<dl class="content-item">'
        template += '<dt>' +
            '<div class="bg-top-hr"></div><div class="bar-tab">退货单号：<span class="word-blue"><a target="_blank" href="salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId='+jsonOrder.idStr+'" class="blue_color">' + jsonOrder.receiptNo + '</a>' +
            '</span>&nbsp;下单时间：' + jsonOrder.vestDateStr + ' &nbsp;';
        template += '客户：<span class="word-blue"><a target="_blank" class="blue_color" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=' + jsonOrder.customerShopIdStr + '">' + jsonOrder.customer + '</a></span>&nbsp;';
        template += '<a class="icon_contacts J_showContactsTip" shop_id="' + jsonOrder.customerShopIdStr + '"></a>';
        template += generateContactTip(shopDTO);
        template += '<div class="cl"></div>';
        template += '</div>' +
            '</dt>'
            + '<dd>' +
            '<table class="item-content" cellpadding="0" cellspacing="0">';
        var items = jsonOrder.itemDTOs;
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            var imageURL = item.imageCenterDTO.productListSmallImageDetailDTO.imageURL;
            var productInfoStr = contactProductInfo(item.commodityCode, item.productName, item.brand, item.spec, item.model, item.vehicleModel, item.vehicleBrand);
            if (i > 1) {
                template += '<tr style="display: none" class="j_display_none">'
            } else {
                template += '<tr>'
            }
            template += '<td class="item-product-info width-set">' +
                '<a class="info-icon" href="shopProductDetail.do?method=toShopProductDetail&paramShopId='+ shopId +'&productLocalId=&orderItemId='+item.idStr+'&orderType=SALE_RETURN&itemFrom=order"><img src="' + imageURL + '"/></a>' +
                '<a target="_blank" class="info-details word-blue" href="shopProductDetail.do?method=toShopProductDetail&paramShopId='+ shopId +'&productLocalId=&orderItemId='+item.idStr+'&orderType=SALE_RETURN&itemFrom=order">'
                + productInfoStr + '</a><div class="cl"></div></td><td class="item-product-unit-price width-set">' + item.price + '</td>' +
                '<td class="item-product-quantity width-set">' + item.amount + " " + (item.unit == null ? "" : item.unit) + '</td>' +
                '<td class="item-product-price width-set"><div class="price-value">' + item.total + '</div></td>';
            if (i === 0) {
                template += '<td class="item-product-payables width-set" rowspan="999"><div class="payables-original-noline">' + jsonOrder.total + '</div></td>'
                    + '<td class="item-product-order-status width-set" rowspan="999"><div class="status-description">' + getOrderStatusStr(jsonOrder.status) + '</div><div class="order-details word-blue a-button  j_order_details" order_id="'+jsonOrder.idStr+'">订单详情</div></td><td class="item-product-operating width-set" rowspan="999">';
                if (jsonOrder.status === 'PENDING') {
                    template += '<div class="button-storage button-yellow-deep-gradient j_sales_return_order_accept" order_id="' + jsonOrder.idStr + '">接受</div><div class="button-storage button-yellow-deep-gradient j_sales_return_order_refuse" order_id="' + jsonOrder.idStr + '">拒绝</div>';
                } else if (jsonOrder.status === 'WAITING_STORAGE') {
                    template += '<div class="button-storage button-yellow-deep-gradient j_sales_return_order_settle" order_id="' + jsonOrder.idStr + '">结算</div>';
                }/* else if (jsonOrder.status === 'SETTLED') {
                    template += '<div class="button-storage button-yellow-deep-gradient j_sales_return_order_repeal" order_id="' + jsonOrder.idStr + '">作废</div>';
                }*/
                template += '</td>';
            }
            template += '</tr>';
        }
        if(jsonOrder.itemDTOs.length>2){
            template += '</table><div class="item-control-bar"><span class="button-spread">共' + jsonOrder.itemDTOs.length + '种商品，点击展开更多</span><span class="button-retract hidden">共有' + jsonOrder.itemDTOs.length + '种商品，点击收起</span><div class="cl"></div></div></dd></dl>';
        }else{
            template += '</table><div class="item-control-bar"><span class="button-spread">共' + jsonOrder.itemDTOs.length + '种商品</span><span class="button-retract hidden">共' + jsonOrder.itemDTOs.length + '种商品</span><div class="cl"></div></div></dd></dl>';
        }
        return template;
    }

}

function buttonSpreadRetractBind(){
    $(".button-spread").live("click",function(){
        var $item = $(this).closest(".content-item");
        $item.find(".j_display_none").show();
        $(this).addClass("hidden");
        $item.find(".button-retract").removeClass("hidden");
    });
    $(".button-retract").live("click",function(){
        var $item = $(this).closest(".content-item");
        $item.find(".j_display_none").hide();
        $(this).addClass("hidden");
        $item.find(".button-spread").removeClass("hidden");
    });

}

function contactProductInfo(commodityCode, productName, brand, spec, model, vehicleModel, vehicleBrand) {
    var productInfo = "";
    for (var i = 0; i < arguments.length; i++) {
        if (!G.isEmpty(arguments[i])) {
            productInfo += G.trim(arguments[i]) + " ";
        }
    }
    return productInfo;
}

function orderOperationBind() {

    $("#refuseConfirmBtn").bind("click", function () {
        if ($("#refuseReason").val() != "") {
            $("#salesRefuseForm").submit();
            $("#salesRefuseForm")[0].reset();
            setTimeout(function () {
                window.location.reload();
            }, 5000);
            $("#refuseReasonDialog").dialog("close");
        } else {
            nsDialog.jAlert("拒绝理由必须填写！");
        }
    });
    $("#refuseCancelBtn").bind("click", function () {
        $("#salesRefuseForm")[0].reset();
        $("#refuseReasonDialog").dialog("close");
    });

    //绑定按钮操作 button-storage button-yellow-deep-gradient

    //绑定按钮操作 button-storage button-yellow-deep-gradient
    $(".j_sales_return_order_accept").live("click",function(){acceptSaleReturn($(this).attr("order_id"))});
    $(".j_sales_return_order_refuse").live("click",function(){refuseSaleReturn($(this).attr("order_id"))});
    $(".j_sales_return_order_settle").live("click",function(){settleSaleReturn($(this).attr("order_id"))});

    $(".j_order_details").live("click",function(){
        showSaleReturn($(this).attr("order_id"));
    });
//    $(".j_purchase_order_cancel").live("click",function(){purchaseReturnCancel($(this).attr("orderId"))});

}


/* handlers below  */

/**
 * 产品搜索提示
 * @param $domObject
 */
function productSuggestion($domObject) {

    var searchWord = $domObject.val().replace(/[\ |\\]/g, ""); // 替换空格和\
    var dropList = APP_BCGOGO.Module.droplist;
    dropList.setUUID(GLOBAL.Util.generateUUID());
    var currentSearchField = $domObject.attr("searchField");
    var ajaxData = {
        searchWord: searchWord,
        searchField: currentSearchField,
        uuid: dropList.getUUID()
    };

    // 这里依赖于页面Element的平级或者子级结构  $domObject.parent().find(".J-productSuggestion")
    $domObject.parent().find(".J-productSuggestion").each(function () {
        var val = $(this).val().replace(/[\ |\\]/g, "");
        // 排除同一级的name为searchWord的元素
        if ($(this).attr("name") != "searchWord") {
            ajaxData[$(this).attr("name")] = val == $(this).attr("initialValue") ? "" : val;
        }
    });

    var ajaxUrl = "product.do?method=searchWholeSalerProductInfo";
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        if (currentSearchField == "product_info") {
            dropList.show({
                "selector": $domObject,
                "autoSet": false,
                "data": result,
                onGetInputtingData: function () {
                    var details = {};
                    $domObject.parent().find(".J-productSuggestion").each(function () {
                        var val = $(this).val().replace(/[\ |\\]/g, "");
                        details[$(this).attr("searchField")] = val == $(this).attr("initialValue") ? "" : val;
                    });
                    return {
                        details: details
                    };
                },
                onSelect: function (event, index, data) {
                    $domObject.parent().find(".J-productSuggestion").each(function () {
                        var label = data.details[$(this).attr("searchField")];
                        if (G.Lang.isEmpty(label) && $(this).attr("initialValue")) {
                            $(this).val($(this).attr("initialValue"));
                            $(this).css({"color": "#ADADAD"});
                        } else {
                            $(this).val(G.Lang.normalize(label));
                            $(this).css({"color": "#000000"});
                        }
                    });
                    dropList.hide();
//                    $("#searchShopOnlineProductBtn").click(); TODO 选中进行搜索
                },
                onKeyboardSelect: function (event, index, data) {
                    $domObject.parent().find(".J-productSuggestion").each(function () {
                        var label = data.details[$(this).attr("searchField")];
                        if (G.Lang.isEmpty(label) && $(this).attr("initialValue")) {
                            $(this).val($(this).attr("initialValue"));
                            $(this).css({"color": "#ADADAD"});
                        } else {
                            $(this).val(G.Lang.normalize(label));
                            $(this).css({"color": "#000000"});
                        }
                    });
                }
            });
        } else {
            dropList.show({
                "selector": $domObject,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.label);
                    $domObject.css({"color": "#000000"});
                    $domObject.nextAll().find(".J-productSuggestion").each(function () {
                        clearSearchInputValueAndChangeCss($(this)[0]);
                    });
                    dropList.hide();
//                    $("#searchShopOnlineProductBtn").click();  TODO
                }
            });
        }
    });

}

/**
 * 用户信息提示
 * 用户名/联系人/手机号
 * @param $customerInfo
 */
function getCustomerSuggestion(domObject, event) {

    var keycode = event ? event.which || event.keyCode : null;
    var $domObject = $(domObject);
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    searchWord = $.trim(searchWord).toUpperCase();
    if (searchWord == "客户/联系人/手机") {
        searchWord = "";
    }
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        searchWord: searchWord.toUpperCase(),
        uuid: droplist.getUUID()
    };
    ajaxData["customerOrSupplier"] = "customer";
    ajaxData["titles"] = "name,contact,mobile";
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        if (!G.isEmpty(result.data[0])) {
            G.completer({
                    'domObject': domObject,
                    'keycode': keycode,
                    'title': result.data[0].details.name}
            );
        }
        if (event && !(event.type == 'blur')) { // blur 不需要下拉
            droplist.show({
                "selector": $domObject,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.details.name);
                    $("#customerId").val(data.details.id);
                    droplist.hide();
                },
                "onKeyboardSelect": function (event, index, data, hook) {
                    if (data && data.details) {
                        $domObject.val(data.details.name);
                        $("#customerId").val(data.details.id);
                        droplist.hide();
                    }
                }
            });
        }
        if (event && event.type == 'blur' && result && result.data) {
            for (var i = 0; i < result.data.length; i++) {  // 下拉存在 并且 blur
                if ($domObject.val() == result.data[i].details.name) { // 名字精确匹配带出信息 否则查询信息为空
                    $("#customerId").val(result.data[i].details.id);
                    break;
                }
            }
        }
    });
}

function syncGetCustomerSuggestion(domObject, event) {
    var keycode = event ? event.which || event.keyCode : null;
    var $domObject = $(domObject);
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    searchWord = $.trim(searchWord).toUpperCase();
    if (searchWord == "客户/联系人/手机") {
        searchWord = "";
    }
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        searchWord: searchWord.toUpperCase(),
        uuid: droplist.getUUID()
    };
    ajaxData["customerOrSupplier"] = "customer";
    ajaxData["titles"] = "name,contact,mobile";
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
    APP_BCGOGO.Net.syncAjax({
        url: ajaxUrl,
        dataType: 'json',
        data: ajaxData,
        success: function (result) {
            if (!G.isEmpty(result.data[0])) {
                G.completer({
                        'domObject': domObject,
                        'keycode': keycode,
                        'title': result.data[0].details.name}
                );
            }
            // blur 事件不需要下拉
            if (event && event.type == 'blur' && result && result.data) {
                for (var i = 0; i < result.data.length; i++) {  // 数据存在 并且 blur
                    if ($domObject.val() == result.data[i].details.name) { // 名字精确匹配带出信息 否则查询信息为空
                        $("#customerId").val(result.data[i].details.id);
                        $domObject.val(result.data[i].details.name).css({"color": "#000000"});
                        break;
                    }
                }
            } else {
                droplist.show({
                    "selector": $domObject,
                    "data": result,
                    "onSelect": function (event, index, data) {
                        $domObject.val(data.details.name);
                        $("#customerId").val(data.details.id);
                        $domObject.css({"color": "#000000"});
                        droplist.hide();
                    },
                    "onKeyboardSelect": function (event, index, data, hook) {
                        if (data && data.details) {
                            $domObject.val(data.details.name);
                            $("#customerId").val(data.details.id);
                        }
                    }
                });
            }
        }
    });
}




//接受
function acceptSaleReturn(orderId, dom) {
    if ($(dom).attr("disabled")) {
        return;
    }
    window.open("salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=" + orderId + "&accept=true");
}
//拒绝
function refuseSaleReturn(orderId, dom) {
    if ($(dom).attr("disabled")) {
        return;
    }
    $("#refuse_id").val(orderId);
    $("#refuseReasonDialog").dialog({ width: 520, modal: true});
}
//待入库
function settleSaleReturn(orderId, dom) {
    if ($(dom).attr("disabled")) {
        return;
    }
    window.open("salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=" + orderId);
}

function showSaleReturn(orderId) {
    window.open("salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=" + orderId, "_blank");
}
