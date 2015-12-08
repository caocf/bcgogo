$().ready(function () {
    $(".J_QQ").each(function (index, value) {
        $(this).multiQQInvoker({
            QQ: $(this).attr("data_qq")
        });
    });
    var errorMsg = $("#errorMsg").html();
    if (!GLOBAL.Lang.isEmpty(errorMsg) && errorMsg != "true") {
        nsDialog.jAlert(errorMsg);
    }
    if ($("#onlinePurchaseReturnMessage").val()) {
        if ($("#onlinePurchaseReturnMessage").attr("resultOperation") == "ALERT_REDIRECT") {
            nsDialog.jAlert($("#onlinePurchaseReturnMessage").val(), null, function () {
                window.location.href = "onlineReturn.do?method=toOnlinePurchaseReturnSelect";
            });
        }else{
            nsDialog.jAlert($("#onlinePurchaseReturnMessage").val());
        }
    }
    $(".btnMinus").live('click', function (event) {
        if ($("#table_purchaseReturn .item").length > 1) {
            var rowIndex = this.parentNode.parentNode.rowIndex;
            $("#table_purchaseReturn")[0].deleteRow(rowIndex);
            setTotal();
        }else{
            nsDialog.jAlert("在线退货不能为空，无法全部删除商品！");
        }
    });
    $("#mobile").bind("blur", function () {
        if ($(this).val()) {
            if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($(this).val())) {
                nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！", null, function () {
                    $("#mobile").focus();
                });
                return false;
            } else {
                //要判断是否存在同名的手机号
                var r = APP_BCGOGO.Net.syncGet({url: "customer.do?method=getSupplierJsonDataByMobile",
                    data: {mobile: $(this).val()}, dataType: "json"});
                if (!r || !r.name) return;
                if ($("#supplierId").val() != r.idString) {
                    nsDialog.jAlert("已有【" + r.name + "】的手机号相同，请确认后重新输入！", null, function () {
                        $("#mobile").focus();
                    });
                    return false;
                }
            }
        }
    });
    $(".itemPrice").live("blur",function () {
        if (!$(this).val()) {
            $(this).val(0);
        }
        $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 2));
        setTotal();
    }).live('keyup', function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
            setTotal();
        });
    $(".itemAmount").live("blur",function () {
        if (!$(this).val()) {
            $(this).val(0);
        }
        var $thisDome = $(this);
        $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 2));
        //blur 的时候不要提醒
//        var itemAmount = $.trim($(this).val()) * 1;
//        var idPrefix = $(this).attr("id").split(".")[0];
//
//        var purchaseAmount = $("#" + idPrefix + "\\.purchaseAmount").val() * 1;
//        var inventoryAmount = $("#" + idPrefix + "\\.inventoryAmount").val() * 1;
//        var purchaseUnit = $("#" + idPrefix + "\\.purchaseUnit").val();
//        var itemUnit = $("#" + idPrefix + "\\.unit").val();
//        var sellUnit = $("#" + idPrefix + "\\.sellUnit").val();
//        var storageUnit = $("#" + idPrefix + "\\.storageUnit").val();
//        var rate = $("#" + idPrefix + "\\.rate").val() * 1;
//        if (itemAmount > inventoryAmount) {
//            nsDialog.jAlert("退货量不能大于库存量，请重新输入退货数量。", null, function () {
//                $thisDome.focus();
//            })
//            return false;
//        }
//        if (isStorageUnit(purchaseUnit, sellUnit, storageUnit, rate)) {
//            if (!isStorageUnit(itemUnit, sellUnit, storageUnit, rate)) {
//                purchaseAmount = purchaseAmount * rate;
//            }
//        } else {
//            if (isStorageUnit(itemUnit, sellUnit, storageUnit, rate)) {
//                purchaseAmount = purchaseAmount / rate;
//            }
//        }
//        if (itemAmount > purchaseAmount) {
//            nsDialog.jAlert("退货量不能大于采购量，请重新输入退货数量。", null, function () {
//                $thisDome.focus();
//            })
//            return false;
//        }
        setTotal();
    }).live('keyup', function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
            setTotal();
        });

    //确认退货，改单按钮
    $("#confirmReturn,#confirmReturnGoodsBtn").click(function () {
        if (APP_BCGOGO.Permission.Version.StoreHouse && G.Lang.isEmpty($("#storehouseId").val())) {
            nsDialog.jAlert("请选择仓库！");
            return;
        }
        if (checkSupplierInfo() == false) {
            return;
        }
        if (!validateReturnStorageOrder()) {
            return false;
        }

        nsDialog.jConfirm("确认退货?", null, function (returnVal) {
            if (returnVal) {
                $('#purchaseReturnForm').ajaxSubmit({
                    url: "onlineReturn.do?method=validateReturnOrder",
                    dataType: "json",
                    type: "POST",
                    success: function (result) {
                        if (result) {
                            if (result.success) {
                                $("#purchaseReturnForm").submit();
                            } else {
                                if (result.operation && result.operation == "update_product_inventory") {
                                    updateProductInventory(result.data, true);
                                } else {
                                    nsDialog.jAlert(result.msg);
                                }
                            }
                        }
                    },
                    error: function () {
                        nsDialog.jAlert("出现异常！");
                    }
                });
            }
        });
    });
    //编辑状态取消
    $("#cancelReturn").click(function(){
        window.location.href = "onlineReturn.do?method=toOnlinePurchaseReturnSelect";
    })

    //改单状态重置
    $("#cancelBtn").click(function () {
        if ($("#id").val()) {
            location.reload();
        } else {
            window.location = "goodsReturn.do?method=createReturnStorage&cancle=noId&receiptNo=" + $("#receiptNo").val();
        }
    });
    //作废
    $("#nullifyBtn").bind("click", function () {
        repealOrder($("#id").val());
    });
    //打印
    $("#printBtn").click(function() {
        if ($("#id").val()) {
            window.showModalDialog("goodsReturn.do?method=printReturnStorageOrder&purchaseReturnId=" + $("#id").val(), '', "dialogWidth=1024px;dialogHeight=768px");
            return;
        }
    });
    $(".J-toSupplier").bind("click",function(){
         var supplierId = $("#supplierId").val();
         if(!GLOBAL.Lang.isEmpty(supplierId) && GLOBAL.Lang.isNumber(supplierId)){
             window.open("unitlink.do?method=supplier&supplierId="+supplierId,"_blank");
         }
     });
});

function validateReturnStorageOrder() {
    var msg = "";
    var deleteRows = new Array();
    var length = 0;
    $(".item").each(function (i) {
        var idPrefix = $(this).find("input[id$='.productId']").attr("id").split(".")[0];
        var price = $("#" + idPrefix + "\\.price").val();
//        var amount = $("#" + idPrefix + "\\.amount").val();
        var amount = parseFloat($("#" + idPrefix + "\\.amount").val());
        var unit = G.normalize($.trim($("#" + idPrefix + "\\.unit").val()));
        var inventoryAmount = parseFloat($("#" + idPrefix + "\\.inventoryAmount").val());
        var purchaseAmount = parseFloat($("#" + idPrefix + "\\.purchaseAmount").val());
        var purchaseUnit = $("#" + idPrefix + "\\.purchaseUnit").val();
        var itemUnit = $("#" + idPrefix + "\\.unit").val();
        var sellUnit = $("#" + idPrefix + "\\.sellUnit").val();
        var storageUnit = $("#" + idPrefix + "\\.storageUnit").val();
        var rate = parseFloat($("#" + idPrefix + "\\.rate").val());
        if (isStorageUnit(purchaseUnit, sellUnit, storageUnit, rate)) {
            if (!isStorageUnit(itemUnit, sellUnit, storageUnit, rate)) {
                purchaseAmount = purchaseAmount * rate;
            }
        } else {
            if (isStorageUnit(itemUnit, sellUnit, storageUnit, rate)) {
                purchaseAmount = purchaseAmount / rate;
            }
        }
        var msgi = "第" + (i + 1) + "行";
        if (isNaN(price) || price < 0) {
            msgi += "，退货价不正确";
        }
        if (isNaN(amount) || amount < 0.001) {
            msgi += "，退货数量不正确";
        }
        if (isNaN(inventoryAmount) || inventoryAmount < 0.001) {
            msgi += "，库存数量为0不能退货";
        }
        if (amount > inventoryAmount) {
            msgi += "，退货数大于库存量不能退货";
        }
        if (amount > purchaseAmount) {
            msgi += "，退货数大于采购量不能退货";
        }
        if (msgi != "第" + (i + 1) + "行") {
            msg += msgi + "，请补充完整或确认。<br>"
        }
    });

    if (msg != "") {
        var msgArray = msg.split("<br>");
        var newMsg = "";
        for (var q = 0; q < msgArray.length; q++) {
            if (msgArray[q] != '') {
                var num = msgArray[q].substring(1, 2);
                var actualRowNum = num - getPistion(deleteRows, num);
                var x = msgArray[q].replace(num, actualRowNum);
                newMsg = newMsg + x + "<br>";

            }
        }
        nsDialog.jAlert(newMsg);
        return false;
    }
    return true;
}

function setTotal() {
    var total = 0;
    $(".itemPrice").each(function (i) {
        if (GLOBAL.isEmpty($(this).val())) {
            $(this).val(0);
        }
        var price = $(this).val();
        var idPrefix = $(this).attr("id").split(".")[0];
        var $itemAmount = $("#" + idPrefix + "\\.amount");
        if (GLOBAL.isEmpty($itemAmount.val())) {
            $itemAmount.val(0);
        }
        var amount = $itemAmount.val();
        var count = parseFloat(price * amount);
        total += count;
        $("#" + idPrefix + "\\.total").val(dataTransition.rounding(count, 2));
    });
    total = dataTransition.rounding(total, 2);
    $("#total_span").text(total);
    $("#total").val(total);
}

//目前只有库存信息要更新
function updateProductInventory(data, isShowMsg) {
    var lackMsg = "";
    $("#table_purchaseReturn").find(".item").each(function(i) {
        var productId = $(this).find("input[id$='.productId']").val();
        if(!GLOBAL.Lang.isEmpty(productId) && !GLOBAL.Lang.isEmpty(data[productId])) {
            var inventoryAmountWithItemUnit =  G.normalize(data[productId],"0")*1;
            if(APP_BCGOGO.Permission.Version.StoreHouse){
                if(G.Lang.isEmpty($("#storehouseId").val()) || $("#storehouseId").val()==$("#oldStorehouseId").val()){
                    $(this).find("input[id$='.inventoryAmount']").val(dataTransition.simpleRounding(inventoryAmountWithItemUnit+$(this).find("input[id$='.reserved']").val()*1,2));
                    $(this).find("span[id$='.inventoryAmountSpan']").text(dataTransition.simpleRounding(inventoryAmountWithItemUnit+$(this).find("input[id$='.reserved']").val()*1));
                }else{
                    $(this).find("input[id$='.inventoryAmount']").val(dataTransition.simpleRounding(inventoryAmountWithItemUnit,2));
                    $(this).find("span[id$='.inventoryAmountSpan']").text(dataTransition.simpleRounding(inventoryAmountWithItemUnit,2));
                }
            }else{
                $(this).find("input[id$='.inventoryAmount']").val(dataTransition.simpleRounding(inventoryAmountWithItemUnit+$(this).find("input[id$='.reserved']").val()*1,2));
                $(this).find("span[id$='.inventoryAmountSpan']").text(dataTransition.simpleRounding(inventoryAmountWithItemUnit+$(this).find("input[id$='.reserved']").val()*1,2));
            }

            if(Number($(this).find("input[id$='.inventoryAmount']").val()) - Number($(this).find("input[id$='.amount']").val()) < 0) {
                lackMsg = lackMsg + "第" +(i+1) + "行商品库存不足，无法退货<br>";
            }
        }
    });

    if (isShowMsg && !GLOBAL.Lang.isEmpty(lackMsg)) {
        nsDialog.jAlert(lackMsg);
    }
}

function isStorageUnit(unit, sellUnit, storageUnit, rate) {
    if (!GLOBAL.isEmpty(sellUnit) && !GLOBAL.isEmpty(storageUnit) && GLOBAL.isNumber(rate) && rate * 1 > 0) {
        if (unit === storageUnit) {
            return true;
        }
    }
    return false;
}

function getPistion(array, value) {
    var position;
    if (array.length <= 0) {
        position = 0;
    } else {
        for (i = 0; i < array.length; i++) {
            if (array[i] > value) {
                position = i;
                break;
            } else {
                position = array.length;
            }
        }
    }
    return position;
}

function repealOrder(orderId){
    if(APP_BCGOGO.Permission.Version.StoreHouse && $("#status").val()=="SELLER_PENDING"){//这个时候作废才会操作库存
        if (!validatorStoreHouseOrderRepeal($("#_toStorehouseId").val(),"RETURN", orderId)) {
            return;
        }
    }
    if (confirm("友情提示：入库退货单作废后，将不再有效，交易会被取消！您确定要作废该入库退货单吗？")) {
        if (!GLOBAL.Lang.isEmpty(orderId)) {
            var url = "goodsReturn.do?method=repealReturnStorage&purchaseReturnId=" + orderId;
            if(APP_BCGOGO.Permission.Version.StoreHouse){
                url +="&toStorehouseId="+$("#_toStorehouseId").val();
            }
            window.location = url;
        }
    }else{
        if(APP_BCGOGO.Permission.Version.StoreHouse){
            $("#_toStorehouseId").val("");
        }
    }
}



