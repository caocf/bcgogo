$(document).ready(function() {
    //firefox
//    开始怀疑是firefox的缓存问题，在网上查过之后，发现确实有人有相似的问题，如下：
//http://forums.mozillazine.org/viewtopic.php?f=25&t=1787115
//    http://stackoverflow.com/questions/5319678/javascript-and-defaultvalue-of-hidden-input-elements
//
//        进一步搜索“firefox input 缓存”找到了下面的应对方法：http://www.mayax.net/article/program/331.htm
//
//        为input元素添加属性autocomplete="off"
//            <input class="otherId" autocomplete="off" type="hidden">
//        阻止火狐对input元素的缓存，问题解决。。。
    $("input[type='hidden']").each(function(){
        $(this).attr("autocomplete", "off");
    });

    function f_t() {
        try {
            var r = App.Net.syncGet({url:"active.do?method=returntime", dataType:"json"});
        } catch(e) {}
        window.setTimeout(f_t, 30000);
    }

    $(window).load(function(e) {
        var span_rechange = $("span_rechange")[0];
        var div_rechange = $("div_rechange")[0];

        window.document.onclick = function(e) {
            var e = e || event;
            var target = e.srcElement || e.target;
            if(!target || !target.id || (target.id != "div_rechange") && (target.id != "input_rechange") && (target.id != "selIcon") && (target.id != "span_rechange") && (null == div_rechange ? true : div_rechange.style.display == "block")) {
                null == div_rechange ? "" : div_rechange.style.display = "none";
            }
        };

        //搜索车牌到维修，保养，美容，
        if($("#input_vehicleNumber") && $("#input_vehicleNumber")[0]) {
            $("#input_vehicleNumber")[0].onclick = function () {
                if ($('#vehicleNumber')[0].value && $('#vehicleNumber')[0].value != $('#vehicleNumber')[0].defaultValue) {
                    var nameValue = $('#vehicleNumber').val();
                    var ResultStr = "";
                    var ResultStr1 = "";
                    //去除空格
                    Temp = nameValue.split(" ");
                    for (i = 0; i < Temp.length; i++)
                        ResultStr1 += Temp[i];
                    //去除横杠 "-"
                    Temp1 = ResultStr1.split("-");
                    for (i = 0; i < Temp1.length; i++)
                        ResultStr += Temp1[i];
                    if ((ResultStr.length == 5 || ResultStr.length == 6)&&APP_BCGOGO.Validator.stringIsCharacter(nameValue)) { //前缀
                        var r = App.Net.syncGet({url:"product.do?method=userLicenseNo", dataType:"json"});
                        if (r === null) {
                            return;
                        }
                        else {
                            var locaono = r[0].localCarNo;
                            $("#vehicleNumber").val((locaono + $("#vehicleNumber").val()).toString().toUpperCase());
                            locaono = '';
                        }
                    }else{
                        if(!APP_BCGOGO.Validator.stringIsLicensePlateNumber(ResultStr)){
                            alert("输入的车牌号码不符合规范，请检查！");
                            return;
                        }
                    }
                    openOrAssign('txn.do?method=getRepairOrderByVehicleNumber&task=maintain&vehicleNumber=' + ($("#vehicleNumber").val() == '车牌号' ? '' : $("#vehicleNumber").val()));
                }
                else {
                    nsDialog.jAlert("请输入车牌号码后再搜索！");
                }
            }
        }

        //如果在 iframe 中， 则不应调用 messagePopup 组件, 当然这个最好使用配给资源的形式， 即require方式， 再改吧 2013-02-06
        if (G.Display.isInIframe() === false && APP_BCGOGO.Permission.Schedule.MessageCenter.Base) {
            App.Module.messagePopup.init({
                url: "pushMessage.do?method=getNumberOfMessageCenter",
                selector: $("#messageCenterNumber"),
                inquireCallback: function (status,data) {
                    if (status == "success") {
                        $("#noticeTotalNumber").html(data['noticeTotalNumber']);
                        if(G.normalize(data['noticeTotalNumber'],"0")*1>0){
                            $("#_messageCenterNumberImg").hide();
                            $("#_messageCenterNumberImgFace").show();
                        }else{
                            $("#_messageCenterNumberImg").show();
                            $("#_messageCenterNumberImgFace").hide();
                        }
                    }
                },
                showCallback: function () {

                }
            });
            App.Module.messagePopup.inquire();
        }

        //购物车数量
        if($("#shoppingCartNumber") && $("#shoppingCartNumber").length>0){
            updateShoppingCartNumber();
        }
    });
    $(document).click(function(e) {
        var target = e.target;
        var selectorArray = [$("#div_brand_head,#input_search_pName,#product_name2_id,#product_commodity_code,#product_brand_id,#product_spec_id,#product_model_id,#pv_brand_id,#pv_model_id,#supplierInfoText,#customerInfoText,#customerVehicleSearchText"), ".productSuggestion"];
        if($(target).closest(selectorArray).length == 0) {
            $("#div_brand_head").css("display", "none");
        }
    });

    $("#changeUserPassword").click(function() {
        $("#change-password-form").dialog("open");
    });

    $("#change-password-form").dialog({
        autoOpen: false,
        height: 220,
        width: 270,
        modal: true,
        buttons: {
            "保存": function() {
                var me = this;
                if(!$("#oldUserPassword").val() || !$("#newUserPassword").val() || !$("#userPasswordAgain").val()) {
                    nsDialog.jAlert("请输入密码！");
                    return;
                }
                if($("#newUserPassword").val() != $("#userPasswordAgain").val()) {
                    nsDialog.jAlert("两次输入的密码不一致，请重新输入！");
                    return;
                }
                APP_BCGOGO.Net.syncAjax({
                    url: "user.do?method=changePassword",
                    data: {
                        oldPassword: $("#oldUserPassword").val(),
                        newPassword: $("#newUserPassword").val()
                    },
                    dataType: "json",
                    success: function(result) {
                        nsDialog.jAlert(result.info, "", function() {
                            if(result.success) {
                                $(me).dialog("close");
                            }
                        })
                    }
                });
            },
            "取消": function() {
                $(this).dialog("close");
            }
        },
        close: function() {
            $("#oldUserPassword").val("");
            $("#newUserPassword").val("");
            $("#userPasswordAgain").val("");
        }
    });
    $("#j_logout").bind("click", function () {
        clearUserGuideCookie();
        $.cookie("clientUrl", null);
        defaultStorage.clear();
        window.location.href = 'j_spring_security_logout';
    });
});

function drawNumberOfMessageCenterByType(cssClass, deleteNum) {
    var classArray = cssClass.split(",");
    if (classArray.length > 0) {
        for (var j = 0, max = classArray.length; j < max; j++) {
            var $dom = $("." + classArray[j]), num;
            if ($dom.length == 0) return;
            for (var i = 0, len = $dom.length; i < len; i++) {
                num = parseFloat($($dom).html()) - deleteNum;
                $($dom).html(num < 0 ? 0 : num);
            }
        }
    }
}

//检查输入中的非法字符

function checkChar(InString) {
    for(Count = 0; Count < InString.length; Count++) {
        TempChar = InString.substring(Count, Count + 1);
        if(!checkshuzi(TempChar) && !checkzimu(TempChar) && !checkhanzi(TempChar)) {
            return(true);
        }
    }
    return(false);
}

//判断数字

function checkshuzi(shuziString) {
    return shuziString.match(/\d/g) != null;
}

//判断字母

function checkzimu(zimuString) {
    return zimuString.match(/[a-z]/ig) != null;
}

//判断汉字

function checkhanzi(hanziString) {
    return hanziString.match(/[^ -~]/g) != null;
}


var isedit = false;
$(function() {
    var testForm = $('repairOrderForm')[0];
    if(testForm) {
        var elements = testForm.elements;
        var formChange = function() {
            isedit = true;
        }
        for(var i = 0; i < elements.length; i++) {
            elements[i].onchange = formChange;
        }
    }
});

function isTyping() {
    var typing = false;
    $("#supplier").each(function() {
        if($(this).val() != "") {
            typing = true;
        }
    });
    $("#contact").each(function() {
        if($(this).val() != "") {
            typing = true;
        }
    });
    $("#mobile").each(function() {
        if($(this).val() != "") {
            typing = true;
        }
    });
    $("#address").each(function() {
        if($(this).val() != "") {
            typing = true;
        }
    });
    $("#customer").each(function() {
        if($(this).val() != "") {
            typing = true;
        }
    });
    $("#licenceNo").each(function() {
        if($(this).val() != "") {
            typing = true;
        }
    });
    $(".table_input").each(function() {
        if($(this).val() != "" && $(this).val() != "0" && $(this).val() != "0.0") {
            typing = true;
        }
    });
    $("#saveBtn").each(function() {
        if($(this).attr("disabled")) {
            typing = false;
        }
    });
    $("#carWash").each(function() {
        if($(this).attr("class") == "title_hover") {
            typing = false;
        }
    });

    $("#saveA").each(function() {
        if($(this).html() == "改单" && !isedit) {
            typing = false;
        }
    });
    return typing;
}

function openOrAssign(url) {
    window.location.assign(encodeURI(url));
}

var openInquiryCenter = function (data) { //误删 防止需求改动
    if (data == null || typeof(data.pageType) == "undefined") {
        GLOBAL._log("go to inquiry center data is null!");
        return;
    }

    var paramArr = [
            "customerOrSupplier",
            "contact",
            "mobile",
            "vehicleId",
            "vehicleNumber",
            "vehicleModel",
            "vehicleBrand",
            "salesman"
        ],
        stringParam = "&pageType=" + data.pageType;

    var appendParamToStringIfNecessary = function (strParam, paramArr, data) {
        for (var i = 0, len = paramArr.length; i < len; i++) {
            if (data[paramArr[i]] && data[paramArr[i]].length && data[paramArr[i]] !== "--") {
                strParam += "&" + paramArr[i] + "=" + data[paramArr[i]];
            }
        }
        return strParam;
    };

    stringParam = appendParamToStringIfNecessary(stringParam, paramArr, data);
    openWindow("inquiryCenter.do?method=inquiryCenterIndex" + stringParam);
};

//管理被打开的标签页面
var win;
function openWindow(url) {
    var name = '查询中心' + new Date().getTime();
    if(win) {
        win.close();
    }
    //    win =window.open(encodeURI(url), name, 'width=940,height=1000,top=0,scrollbars=yes, left=' + (Math.round((document.body.scrollWidth - 860) / 2)).toString());
    win = window.open(encodeURI(url));
}

function openIFrameOfInquiryCenter(data) {
    if(data == null || typeof(data.pageType) == "undefined") {
        GLOBAL.error("go to inquiry center data is null!");
        return;
    }

    var paramArr = [
            "customerOrSupplier",
            "contact",
            "mobile",
            "vehicleId",
            "vehicleNumber",
            "vehicleModel",
            "vehicleBrand",
            "productName",
            "productBrand",
            "productSpec",
            "productModel",
            "productVehicleModel",
            "productVehicleBrand",
            "commodityCode"
        ],
        stringParam = "&pageType=" + data.pageType;

    var appendParamToStringIfNecessary = function (strParam, paramArr, data) {
        for (var i = 0, len = paramArr.length; i < len; i++) {
            if (data[paramArr[i]] && data[paramArr[i]].length && data[paramArr[i]] !== "--") {
                strParam += "&" + paramArr[i] + "=" + data[paramArr[i]];
            }
        }
        return strParam;
    };

    stringParam = appendParamToStringIfNecessary(stringParam, paramArr, data);

    if(data.pageType=='wash_beauty'){
        if(!data['washBeautyPageCustomizerConfig']){
            nsDialog.jAlert("请在自定义配置中选中查询中的洗车美容项！");
            return;
        }
    }


    bcgogo.checksession({
        "parentWindow": window.parent,
        'iframe_PopupBox': $("#iframe_PopupBox_inquiry_center")[0],
        'src': "inquiryCenter.do?method=inquiryCenterIndex" + encodeURI(stringParam)
    });
}

function redirectToInquiryCenterTemp(data) {
    if(data == null || typeof(data.pageType) == "undefined") {
        GLOBAL._log("go to inquiry center data is null!");
        return;
    }

    var paramArr = [
            "customerOrSupplier",
            "contact",
            "mobile",
            "vehicleId",
            "vehicleNumber",
            "vehicleModel",
            "vehicleBrand",
            "productName",
            "productBrand",
            "productSpec",
            "productModel",
            "productVehicleModel",
            "productVehicleBrand",
            "commodityCode"
        ],
        stringParam = "&pageType=" + data.pageType;

    var appendParamToStringIfNecessary = function (strParam, paramArr, data) {
        for (var i = 0, len = paramArr.length; i < len; i++) {
            if (data[paramArr[i]] && data[paramArr[i]].length && data[paramArr[i]] !== "--") {
                strParam += "&" + paramArr[i] + "=" + data[paramArr[i]];
            }
        }
        return strParam;
    };

    stringParam = appendParamToStringIfNecessary(stringParam, paramArr, data);
    window.open("inquiryCenter.do?method=inquiryCenterIndex" + encodeURI(stringParam));

}

function getOrderType() {
    var orderEnum = {
            "purchaseInventoryOrder": "INVENTORY",
            "purchaseOrder": "PURCHASE",
            "repairOrder": "REPAIR",
            "goodsSaleOrder": "SALE",
            "washBeauty": "WASH_BEAUTY",
            "purchaseReturnOrder": "RETURN",
            "inventoryCheckOrder": "INVENTORY_CHECK",
            "salesReturnOrder": "SALE_RETURN",
            "allocateRecord": "ALLOCATE_RECORD",
            "innerPicking": "INNER_PICKING",
            "innerReturn": "INNER_RETURN",
            "insuranceOrder": "INSURANCE_ORDER",
            "repairPickingList": "REPAIR_PICKING_LIST",
            "repairPickingInfo": "REPAIR_PICKING_INFO",
            "onlinePurchaseReturnOrder": "ONLINE_PURCHASE_RETURN",
            "borrowOrder":"BORROW_ORDER",
            "returnOrder":"RETURN_ORDER",
            "preBuyOrder":"PRE_BUY_ORDER"
        },
        orderValue = $("#orderType").val();
    return orderEnum[orderValue] || orderValue;
}

function getOrderStatus() {
    return $("#orderStatus").val();
}

function getOrderStatusStr(OrderStatus) {
    var orderStatusEnum = {
        "REPAIR_DISPATCH": "施工中",
        "REPAIR_CHANGE": "改单",
        "REPAIR_DONE": "已完工",
        "REPAIR_SETTLED": "已结算",
        "REPAIR_REPEAL": "已作废",
        "WASH_SETTLED": "已结算",
        "WASH_REPEAL": "已作废",
        "STATEMENT_ACCOUNTED": "已对账",
        "STOCKING": "备货中",
        "DISPATCH": "已发货",
        "SELLER_STOP": "终止销售",
        "SALE_DONE": "已结算",
        "SALE_DEBT_DONE": "欠款结算",
        "SALE_REPEAL": "已作废",
        "PURCHASE_INVENTORY_DONE": "已入库",
        "PURCHASE_INVENTORY_REPEAL": "已作废",
        "SELLER_STOCK": "卖家备货中",
        "SELLER_DISPATCH": "卖家已发货",
        "PURCHASE_SELLER_STOP": "卖家终止销售",
        "PURCHASE_ORDER_WAITING": "待入库",
        "PURCHASE_ORDER_DONE": "已入库",
        "PURCHASE_ORDER_REPEAL": "已作废",
        "MEMBERCARD_ORDER_STATUS": "已结算",
        "SETTLED": "已结算",
        "REPEAL": "已作废",
        "PENDING": "待处理",
        "SELLER_PENDING": "待处理",
        "WAITING_STORAGE": "待入库",
        "SELLER_ACCEPTED": "卖家已接受",
        "REFUSED": "已拒绝",
        "SELLER_REFUSED": "已拒绝",
        "STOP": "买家终止交易",
        "OUT_STORAGE": "已出库",
        "RETURN_STORAGE": "已退料",
        "WAIT_OUT_STORAGE": "未出库",
        "WAIT_RETURN_STORAGE": "未退料"
    };
    return orderStatusEnum[OrderStatus]||"";
}


function isNeedInitUnitTd() {
    if(getOrderType() != "ALLOCATE_RECORD" && getOrderType() != "INNER_PICKING" && getOrderType() != "INNER_RETURN" && getOrderType() != "INSURANCE_ORDER"&&getOrderType()!="INVENTORY_CHECK") {
        return true;
    } else {
        return false;
    }
}
function isCanEditUnit() {
    if(getOrderType() == "INVENTORY"
        || getOrderType() == "PURCHASE"
        || getOrderType() == "REPAIR"
        || getOrderType() == "SALE"
        || getOrderType() == "RETURN"
        || getOrderType() == "SALE_RETURN"
        || getOrderType() == "INNER_RETURN"
        || getOrderType() == "BORROW_ORDER"
        || getOrderType() == "PRE_BUY_ORDER"
        || getOrderType() == "APPOINT_ORDER"
      ) {
        return true;
    } else {
        return false;
    }
}
function checkRepairPickingSwitchOn() {
    if(typeof isRepairPickingSwitchOn == "boolean" && isRepairPickingSwitchOn) {
        return true;
    } else if(typeof isRepairPickingSwitchOn == "string" && isRepairPickingSwitchOn == "true") {
        return true;
    } else {
        return false;
    }
}

function updateShoppingCartNumber(){
    shoppingCartButton();
}

function clearUserGuideCookie() {
    $.cookie("excludeFlowName", null);
    $.cookie("currentStepName", null);
    $.cookie("currentFlowName", null);
    $.cookie("currentStepStatus", null);
    $.cookie("nextStepName", null);
    $.cookie("currentStepIsHead", null);
    $.cookie("url", null);
    $.cookie("hasUserGuide",null);
    $.cookie("isContinueGuide",null);
    $.cookie("keepCurrentStep",null);
}


function bindShoppingCartEvent(root) {
    root.unbind().bind('mouseover', function () {
        $('.detailed-scb', this).show();
        this.id == 'headerShoppingCartDiv' && $('.content-scb',this).addClass('hover-scb');
    }).bind('mouseout', function () {
        $('.detailed-scb', this).hide();
        this.id == 'headerShoppingCartDiv' && $('.content-scb',this).removeClass('hover-scb');
    });
    $('.detailed-scb .bottom-scb img.settlement-scb', root).unbind().click(function () {
        location.href = 'shoppingCart.do?method=shoppingCartManage';
    });
    $('.detailed-scb .empty-scb .buttonLine-scb span', root).unbind().click(function () {
        location.href = 'shoppingCart.do?method=shoppingCartManage';
    });
    $('.content-scb', root).unbind().click(function () {
        location.href = 'shoppingCart.do?method=shoppingCartManage';
    });
};

function shoppingCartButton() {
    var html = '<div class="items-scb"></div><div class="bottom-scb"><div class="total-scb">共&#160;<span class="number-scb" data-mark="total">0</span>&#160件商品&#160;&#160;共计&#160;<span class="number-scb" data-mark="amount">&yen;0</span></div><img class="settlement-scb" src="images/shoppingCart_03.png"></div><div class="empty-scb">您购物车里还没有任何商品<div class="buttonLine-scb"><span class="button-scb">查看我的购物车</span></div></div>';
    var root = $('#shoppingCartButton.shoppingCart').add('#headerShoppingCartDiv.shoppingCart');
    root.length && root.each(function () {
        $('.detailed-scb', this).empty().append(html);
        updateShoppingCartData($(this));
        bindShoppingCartEvent($(this));
    });
}

function updateShoppingCartData(root) {
    App.Net.asyncPost({
        url: "shoppingCart.do?method=getShoppingCartData",
        cache: false,
        dataType: "json",
        success: function (response) {
            var node = $('.detailed-scb .items-scb', root).empty();
            var bottom = $('.detailed-scb .bottom-scb', root);
            var empty = $('.detailed-scb .empty-scb', root);
            var discount = 0;
            if (response && response.shoppingCartDetailMap) {
                for (var key in response.shoppingCartDetailMap) {
                    var detailList = response.shoppingCartDetailMap[key];
                    var productListTemp = {};
                    var promotionsObjectTemp = {};
                    $.each(detailList, function (i, item) {
                        var html = $('<div class="item-scb"></div>').appendTo(node);
                        var imgNode = $('<img class="img-scb">').attr('src', item.imageCenterDTO.productListSmallImageDetailDTO.imageURL).bind('click', item,function (e) {
                            window.open('shopProductDetail.do?method=toShopProductDetail&paramShopId=' + e.data.supplierShopIdStr + '&productLocalId=' + e.data.productLocalInfoIdStr);
                        });
                        var rightNode = $('<div class="itemRight-scb"></div>');
                        var infoNode = $('<div class="info-scb info40-scb"></div>').append($('<span></span>').text(item.productInfoStr).bind('click', item, function (e) {
                            window.open('shopProductDetail.do?method=toShopProductDetail&paramShopId=' + e.data.supplierShopIdStr + '&productLocalId=' + e.data.productLocalInfoIdStr);
                        }));
                        var info20Node = $('<div class="info-scb info20-scb"></div>');
                        var deleteNode = $('<div class="delete-scb">删除</div>').bind('click', item, function (e) {
                            deleteShoppingCart(e.data.idStr);
                        });
                        html.append(imgNode).append(rightNode.append(infoNode).append(info20Node));
                        if (item.salesStatus == 'InSales') {
                            var price = item.price;
                            item.promotionsDTOList && $.each(item.promotionsDTOList, function (i, n) {
                                if (n.type == 'BARGAIN' && (!n.promotionsProductDTOList[0].limitFlag || (n.promotionsProductDTOList[0].limitFlag && n.promotionsProductDTOList[0].limitAmount >= item.amount))) {
                                    discount = discount + (price - n.promotionsProductDTOList[0].discountAmount) * item.amount;
                                    price = n.promotionsProductDTOList[0].discountAmount;
                                } else if (n.type == 'MLJ') {
                                    var mlj_discount = 0;
                                    productListTemp[n.idStr] == null && (productListTemp[n.idStr] = []);
                                    productListTemp[n.idStr].push(item);
                                    promotionsObjectTemp[n.idStr] = n;
                                    n.promotionsRuleDTOList && n.promotionsRuleDTOList.length && $.each(n.promotionsRuleDTOList, function () {
                                        if (this.promotionsRuleType == 'REDUCE_FOR_OVER_AMOUNT') {//满数量减金额
                                            item.amount >= this.minAmount && mlj_discount < this.discountAmount && (mlj_discount = this.discountAmount);
                                        }  else if (this.promotionsRuleType == 'DISCOUNT_FOR_OVER_AMOUNT') {//满数量打折扣
                                            item.amount >= this.minAmount && mlj_discount < this.discountAmount && (mlj_discount = price * item.amount * (10 - this.discountAmount) * 0.1);
                                        }
                                    });
                                    discount += mlj_discount;
                                }
                            });
                            info20Node.append('<div class="price-scb number-scb">&yen;' + price + '*' + item.amount + '</div>');
                            item.promotionsTitle && info20Node.append('<div class="promotions-scb">' + (item.promotionsTitle ? item.promotionsTitle[0] : '') + '</div>');
                        } else {
                            info20Node.append('<div class="offShelf-scb">商品已下架</div>');
                        }
                        info20Node.append(deleteNode);
                    });
                    for (var pt in productListTemp) {
                        var productList = productListTemp[pt];
                        var promotions = promotionsObjectTemp[pt];
                        var sum = 0;
                        $.each(productList, function () {
                            sum += this.price * this.amount
                        });
                        var mlj_discount = 0;
                        promotions.promotionsRuleDTOList && promotions.promotionsRuleDTOList.length && $.each(promotions.promotionsRuleDTOList, function () {
                            if (this.promotionsRuleType == 'DISCOUNT_FOR_OVER_MONEY') {//满金额打折扣
                                sum >= this.minAmount && mlj_discount < this.discountAmount &&  (mlj_discount = sum * (10 - this.discountAmount) * 0.1);
                            } else if (this.promotionsRuleType == 'REDUCE_FOR_OVER_MONEY') {//满金额减金额
                                sum >= this.minAmount && mlj_discount < this.discountAmount && (mlj_discount = this.discountAmount);
                            }
                        });
                        discount += mlj_discount;
                    }
                }
                node.show();
                bottom.show();
                empty.hide();
                $('.bubble-scb', root).text(response.shoppingCartItemCount);
                $('span[data-mark=total]', bottom).text(G.normalize(response.shoppingCartItemCount, "0"));
                $("#shoppingCartNumber").text(G.normalize(response.shoppingCartItemCount, "0"));
                $('span[data-mark=amount]', bottom).html('&yen;' + (Number(response.total) - Number(discount)).toFixed(2));
            } else {
                node.hide();
                bottom.hide();
                empty.show();
            }
        },
        error: function () {
            $('.detailed-scb .items-scb', root).empty().hide();
            $('.detailed-scb .bottom-scb', root).hide();
            $('.detailed-scb .empty-scb', root).empty().text('无法取得购物车中商品，请刷新页面重试');
        }
    });
}

function deleteShoppingCart(shoppingCartItemId) {
    if (!G.Lang.isEmpty(shoppingCartItemId)) {
        nsDialog.jConfirm("确认是否从购物车删除当前商品?", "删除商品", function (returnVal) {
            if (returnVal) {
                APP_BCGOGO.Net.syncPost({
                    url: "shoppingCart.do?method=deleteShoppingCartItemById",
                    data: {
                        shoppingCartItemId: shoppingCartItemId
                    },
                    dataType: "json",
                    success: function (json) {
                        if (json.success) {
                            updateShoppingCartNumber();
                        } else {
                            nsDialog.jAlert("删除失败，请刷新页面！");
                        }
                    },
                    error: function () {
                        nsDialog.jAlert("数据异常，请刷新页面！");
                    }
                });
            }
        });
    }
}

$(function(){
    window.htmlNumberFilter = function (node, isZeroShowBlank) {
        node && node.length && node.each(function () {
            var tagName = this.tagName.toUpperCase();
            if (tagName == 'INPUT') {
                //input元素不执行isZeroShowBlank逻辑
                var val = GLOBAL.Number.filterZero($(this).val());
                $(this).val() && $(this).val(val);
            } else if (tagName == 'DIV' || tagName == 'SPAN') {
                var val = GLOBAL.Number.filterZero($(this).text());
                isZeroShowBlank && val == 0 && (val = '');
                $(this).text() && $(this).text(val);
            }
        });
    }
    htmlNumberFilter($('span[data-filter-zero=true]').add('div[data-filter-zero=true]'));
    $('input[data-filter-zero=true]').each(function(){
        var result = '',val = $(this).val();
        if (val != '' && !isNaN(val)) {
            result = new Number(val).toFixed(2);
            var exec = /0*$/.exec(result);
            if (exec != null) {
                result = Number(result.substring(0, exec.index));
            }
        }
        $(this).val(result);
    });
    $('input[data-filter-zero=true]').live('blur',function () {
        var result = '',val = $(this).val();
        if (val != '' && !isNaN(val)) {
            result = new Number(val).toFixed(2);
            var exec = /0*$/.exec(result);
            if (exec != null) {
                result = Number(result.substring(0, exec.index));
            }
        }
        $(this).val(result);
    }).live('focus', function () {
            var val = $(this).val().replace(/\s/, '');
            if (val == '0') {
                $(this).val('');
            } else {
                if (Number(val) == 0) {
                    $(this).val('');
                }
            }
        });
});