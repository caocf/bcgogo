/** controller */
$(document).ready(function () {

    // 商品搜索Field相关注册绑定
    productSearchFieldBind();

    // 客户信息下拉的绑定
    supplierInfoSuggestionBind();

    // 时间相关的查询条件的绑定
    timeConditionBind();

    resetBind();

    doSearchBind();

    overrideFormSubmitBind();

    orderOperationBind();

    buttonSpreadRetractBind();
    orderDetailBind();

    $(".J_status_link").bind("click", function(){
        var status = $(this).attr("paramStatus");
        var startDate = $(this).attr("paramStartDate");
        var endDate = $(this).attr("paramEndDate");
        searchWithPreConditions(status, startDate, endDate);
    });
    $("#my_date_self_defining").click();
    // 供求中心首页跳转时带条件的处理
    var status = GLOBAL.Util.getUrlParameter("orderStatus");
    var startDate = GLOBAL.Util.getUrlParameter("startTimeStr");
    var endDate = GLOBAL.Util.getUrlParameter("endTimeStr");
    if(G.isEmpty(status)){
        status = $("#orderStatus option:contains('待办入库退货单')").val();
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
function supplierInfoSuggestionBind() {
    $("#supplierInfo")
        .bind('click', function (e) {
            getSupplierSuggestion($(this), e);
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getSupplierSuggestion(this, event);
            }
        })
        .bind('blur', function (event) {
            syncGetSupplierSuggestion(this, event);
        })
        .bind('change', function (event) {
            clearSupplierId();
            getSupplierSuggestion(this, event);
        })
        .bind('focus', function (event) {
            getSupplierSuggestion(this, event);
        })
        .placeHolder();
}

function clearSupplierId() {
    $('#supplierId').val("");
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
        var url = "/web/onlinePurchaseReturnOrder.do?method=onlinePurchaseReturnOrderSearch";
        APP_BCGOGO.Net.syncPost({
            url: url,
            dataType: "json",
            data: paramJson,
            success: function (data) {
                $(".content-item").remove();
                if (!G.isEmpty(data)) {
                    initTable(data);
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
    if(status == 'inProgress'){
        status = "SELLER_ACCEPTED,SELLER_REFUSED";
    }
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
            var supplierShopIdStr = item.supplierShopIdStr;
            var shopDTO = data['data']['shopDTOs'][supplierShopIdStr];
            var orderHtml = renderPurchaseReturnOrder(item, shopDTO);
            if (orderHtml) {
                orderList += orderHtml;
            }
        }
    }
    if (orderList) {
        $('.list-content').append($(orderList));
    }
    $(".J_QQ").each(function (index, value) {
        $(this).multiQQInvoker({
            QQ: $(this).attr("data_qq")
        });
    });
}

function renderPurchaseReturnOrder(jsonOrder, shopDTO) {

    if (jsonOrder) {
        var template = '<dl class="content-item">'
            + '<dt><div class="bg-top-hr"></div><div class="bar-tab">退货单号：<span class="word-blue"><a target="_blank" href="goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId='+jsonOrder.idStr+'" class="blue_color">' + jsonOrder.receiptNo + '</a></span>&nbsp;';
        template += '下单时间：' + jsonOrder.vestDateStr + ' &nbsp;';
        template += '供应商：<span class="word-blue"><a target="_blank" class="blue_color" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=' + jsonOrder.supplierShopIdStr + '" >' + jsonOrder.supplier + '</a></span>';

        var qqArray = getShopContactQQ(shopDTO);
        template += '<a class="J_QQ" data_qq="' + qqArray + '"></a>';

        template += '<div class="cl"></div></div></dt>'
            + '<dd><table class="item-content" cellpadding="0" cellspacing="0">';
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
            template += '<td class="item-product-info width-set"><a class="info-icon" href=""><img src="' + imageURL + '"/></a><a target="_blank" class="info-details word-blue" href="shopProductDetail.do?method=toShopProductDetail&paramShopId=' + jsonOrder.supplierShopIdStr + '&productLocalId=&orderItemId=' + item.idStr + '&orderType=RETURN&itemFrom=order">' + productInfoStr + '</a><div class="cl"></div></td><td class="item-product-unit-price width-set">' + item.price + '</td><td class="item-product-quantity width-set">' + item.amount + " " + item.unit + '</td><td class="item-product-price width-set"><div class="price-value">' + item.total + '</div></td>';
            if (i === 0) {
                template += '<td class="item-product-payables width-set" rowspan="999"><div class="payables-original-noline">' + jsonOrder.total + '</div></td>'
                    + '<td class="item-product-order-status width-set" rowspan="999"><div class="status-description">' + getOrderStatusStr(jsonOrder.status) + '</div><div class="order-details word-blue a-button j_order_details" order_id="' + jsonOrder.idStr + '">订单详情</div></td><td class="item-product-operating width-set" rowspan="999">';
                if (jsonOrder.status === 'SELLER_PENDING') {
                    template += '<div class="button-storage button-yellow-deep-gradient j_purchase_order_modify" orderId="' + jsonOrder.idStr + '">修改</div><div class="button-storage button-yellow-deep-gradient j_purchase_order_repeal" orderId="' + jsonOrder.idStr + '">作废</div>';
                } else if (jsonOrder.status === 'SELLER_REFUSED') {
                    template += '<div class="button-storage button-yellow-deep-gradient j_purchase_order_cancel" orderId="' + jsonOrder.idStr + '">作废</div>';
                } else if (jsonOrder.status === 'SELLER_ACCEPTED') {
                    template += '<div class="button-storage button-yellow-deep-gradient j_purchase_order_settle" orderId="' + jsonOrder.idStr + '">结算</div>';
                }
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

function orderDetailBind() {
    $(".j_order_details").live("click", function () {
        orderDetail($(this).attr("order_id"));
    });
}

function buttonSpreadRetractBind() {
    $(".button-spread").live("click", function () {
        var $item = $(this).closest(".content-item");
        $item.find(".j_display_none").show();
        $(this).addClass("hidden");
        $item.find(".button-retract").removeClass("hidden");
    });
    $(".button-retract").live("click", function () {
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
    $(".j_purchase_order_modify").live("click", function () {
        purchaseReturnModify($(this).attr("orderId"))
    });
    $(".j_purchase_order_repeal").live("click", function () {
        purchaseReturnRepeal($(this).attr("orderId"))
    });
    $(".j_purchase_order_settle").live("click", function () {
        purchaseReturnSettle($(this).attr("orderId"))
    });
    $(".j_purchase_order_cancel").live("click", function () {
        purchaseReturnCancel($(this).attr("orderId"))
    });

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
function getSupplierSuggestion(domObject, event) {

    var keycode = event ? event.which || event.keyCode : null;
    var $domObject = $(domObject);
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    searchWord = $.trim(searchWord).toUpperCase();
    if (searchWord == "供应商/联系人/手机") {
        searchWord = "";
    }
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        searchWord: searchWord.toUpperCase(),
        uuid: droplist.getUUID()
    };
    ajaxData["customerOrSupplier"] = "supplier";
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
                    $("#supplierId").val(data.details.id);
                    droplist.hide();
                },
                "onKeyboardSelect": function (event, index, data, hook) {
                    if (data && data.details) {
                        $domObject.val(data.details.name);
                        $("#supplierId").val(data.details.id);
                        droplist.hide();
                    }
                }
            });
        }
        if (event && event.type == 'blur' && result && result.data) {
            for (var i = 0; i < result.data.length; i++) {  // 下拉存在 并且 blur
                if ($domObject.val() == result.data[i].details.name) { // 名字精确匹配带出信息 否则查询信息为空
                    $("#supplierId").val(result.data[i].details.id);
                    break;
                }
            }
        }
    });
}

function syncGetSupplierSuggestion(domObject, event) {
    var keycode = event ? event.which || event.keyCode : null;
    var $domObject = $(domObject);
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    searchWord = $.trim(searchWord).toUpperCase();
    if (searchWord == "供应商/联系人/手机") {
        searchWord = "";
    }
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        searchWord: searchWord.toUpperCase(),
        uuid: droplist.getUUID()
    };
    ajaxData["customerOrSupplier"] = "supplier";
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
                        $("#supplierId").val(result.data[i].details.id);
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
                        $("#supplierId").val(data.details.id);
                        $domObject.css({"color": "#000000"});
                        droplist.hide();
                    },
                    "onKeyboardSelect": function (event, index, data, hook) {
                        if (data && data.details) {
                            $domObject.val(data.details.name);
                            $("#supplierId").val(data.details.id);
                        }
                    }
                });
            }
        }
    });
}





/** 订单处理 */

//修改
function purchaseReturnModify(orderId) {
    window.open("onlineReturn.do?method=modifyReturnStorage&purchaseReturnId=" + orderId);
}

//未被批发商接受前的作废
function purchaseReturnCancel(orderId) {
    if (APP_BCGOGO.Permission.Version.StoreHouse) {//这个时候作废才会操作库存
        if (!validatorStoreHouseOrderRepeal($("#_toStorehouseId").val(), "RETURN", orderId)) {
            return;
        }
    }
    repealOrder(orderId);
}

//selectStoreHouse.jsp中js  调用  名称不能修改
function repealOrder(orderId) {
    nsDialog.jConfirm("友情提示：入库退货单作废后，将不再有效，交易会被取消！您确定要作废该入库退货单吗？", null, function (returnVal) {
        if (returnVal) {
            var url = "goodsReturn.do?method=repealReturnStorage&purchaseReturnId=" + orderId;
            if (APP_BCGOGO.Permission.Version.StoreHouse) {
                url += "&toStorehouseId=" + $("#_toStorehouseId").val();
            }
            window.open(url);
            setTimeout(function () {
                window.location.reload();
            }, 5000);
        }
    });
}

//作废
function purchaseReturnRepeal(orderId) {
    nsDialog.jConfirm("友情提示：是否确认作废？", null, function (returnVal) {
        if (returnVal) {
            window.open("goodsReturn.do?method=repealReturnStorage&purchaseReturnId=" + orderId);
            setTimeout(function () {
                window.location.reload();
            }, 5000);
        }
    });
}
//结算
function purchaseReturnSettle(orderId) {
    window.open("goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=" + orderId);
}

// 跳转到采购单详细
function orderDetail(orderId) {
    window.open("goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=" + orderId, "_blank");
}