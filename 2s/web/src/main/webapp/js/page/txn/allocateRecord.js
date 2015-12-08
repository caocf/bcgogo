var trCount;
$(document).ready(function () {
    if ($("#id").val() && "REPAIR" == $("#returnType").val() && "REPAIR" == $("#updateLackOrderType").val() && $("#updateLackOrderId").val()) {
        window.location = "txn.do?method=getRepairOrder&repairOrderId=" + $("#updateLackOrderId").val();
    }


    trCount = $(".item").size();

//    tableUtil.tableStyle('#table_productNo', '.title_tb,.heji_tb,.memo_bg');
    $("#outStorehouseId").val("");
    $("[id$='.addRowBtn']").live('click', function () {
        var idPrefix = $(this).attr("id").split(".")[0];
        if (GLOBAL.Lang.isEmpty($("#" + idPrefix + "\\.productName").val())) {
            nsDialog.jAlert("请输入或选择品名!");
            return false;
        }
        if (!GLOBAL.Lang.isNumber($("#" + idPrefix + "\\.amount").val())) {
            nsDialog.jAlert("请输入正确的数量！");
            return false;
        }
        //采购单检查是否相同
        if (trCount >= 2) if (checkSameItem()) {
            nsDialog.jAlert("单据有重复内容，请修改或删除!");
            return false;
        }
        if (checkSameCommodityCode()) {
            nsDialog.jAlert("商品编码有重复内容，请修改或删除!");
            return false;
        }
        addItemRow();
    });

    $("[id$='.deleteRowBtn']").live('click', function (event) {
        var target = event.target;
        $(target).closest("tr").remove();
        var iPrefixId = $(target).attr("id").split(".")[0];
        $("#" + iPrefixId + "_supplierInfo").remove();
        isShowAddButton()
        trCount = $(".item").size();
        setTotal();
    });
    $(".itemAmount")
        .live("blur", function () {
            if (!$(this).val()) {
                $(this).val(0);
            }
            var val = APP_BCGOGO.StringFilter.priceFilter($(this).val(), 2);
            $(this).val(val > 0 ? val : '');
            var num = $.trim($(this).val());
            var returnNum = parseFloat($.trim($(this).next().val()));
            if (num > returnNum - 0.0001) {
                $(this).val(returnNum > 0 ? returnNum : '');
            }
            setTotal();
        })
        .live('keyup', function () {
            var setVal = "";
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
            var num = $.trim($(this).val());
            var returnNum = parseFloat($.trim($(this).next().val()));
            if (num > returnNum - 0.0001) {
                setVal = returnNum + "";
                $(this).val(returnNum);
            } else {
                setVal = num + "";
            }
            setTotal();

            $(this).val(setVal);
        });

    $("#returnListBtn").bind('click', function (e) {
        window.location = "allocateRecord.do?method=allocateRecordList";
    });
    $("#confirmBtn").bind('click', function (e) {
        if (validateAllocateRecord()) {
            nsDialog.jConfirm("确认调拨?", null, function (returnVal) {
                if (returnVal) {
                    $('#allocateRecordForm').ajaxSubmit({
                        url: "allocateRecord.do?method=validateAllocateRecord",
                        dataType: "json",
                        type: "POST",
                        success: function (result) {
                            if (result) {
                                if (result.success) {
                                    $("#allocateRecordForm").submit();
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
        }
    });

    $("#printBtn").bind("click", function () {
        var allocateRecordId = $("#id").val();
        if (!GLOBAL.Lang.isEmpty(allocateRecordId)) {
            var url = "allocateRecord.do?method=print&allocateRecordId=" + allocateRecordId;
            window.open(url, "_blank");

        }
    });
    $('#itemDTOs0\\.costPrice').length && Number($('#itemDTOs0\\.costPrice').val()) == 0 && $('#itemDTOs0\\.costPrice').val('');
    $('#itemDTOs0\\.amount').length && Number($('#itemDTOs0\\.amount').val()) == 0 && $('#itemDTOs0\\.amount').val('');
    $('#itemDTOs0\\.inventoryAmount').length && Number($('#itemDTOs0\\.inventoryAmount').val()) == 0 && $('#itemDTOs0\\.inventoryAmount').val('');
    $('#itemDTOs0\\.totalCostPrice').length && Number($('#itemDTOs0\\.totalCostPrice').val()) == 0 && $('#itemDTOs0\\.totalCostPrice').val('');

//    $("#inStorehouseId").change(function(e) {
//        var productLocalInfoIds = new Array();
//        $(".item").each(function (i) {
//            var productLocalInfoId = $.trim($(this).find("input[id$='.productId']").val());
//            if (!G.Lang.isEmpty(productLocalInfoId)) {
//                productLocalInfoIds.push(productLocalInfoId);
//            }
//        });
//        if (productLocalInfoIds.length == 0) {
//            return;
//        }
//        var paramJson = {
//            storehouseId:$(this).val(),
//            productLocalInfoIds:productLocalInfoIds.join(",")
//        };
//        APP_BCGOGO.Net.syncPost({
//            url: "product.do?method=getProductStorehouseStorageBinByProductLocalInfoIds",
//            data: paramJson,
//            dataType: "json",
//            success: function (json) {
//                updateProductInStorageBin(json.StorehouseStorageBinMap);
//            },
//            error:function() {
//                nsDialog.jAlert("数据异常！");
//            }
//        });
//    });
});
//校验单据的信息完整与正确性
function validateAllocateRecord() {
    var msg = "";
    deleteEmptyItem();
    var alertStr = hasNewProduct();
    if (!G.isEmpty(alertStr)) {
        alert(alertStr + "无法调拨，请删除！");
        return;
    }
    $(".item").each(function (i) {
        var productId = $.trim($(this).find("input[id$='.productId']").val());
        var productName = $.trim($(this).find("input[id$='.productName']").val());
        if ($(".item").size() == 1 && checkEmptyRow($(this))) {
            msg = ",单据内容不能为空!";
            return false;
        }
    });
    if (!G.Lang.isEmpty(msg)) {
        nsDialog.jAlert("对不起" + msg);
        return false;
    }

    if (G.Lang.isEmpty($("#outStorehouseId").val()) || G.Lang.isEmpty($("#inStorehouseId").val())) {
        nsDialog.jAlert("对不起，商品的调出与调入仓库不能为空，无法调拨！");
        return false;
    }
    if ($("#outStorehouseId").val() == $("#inStorehouseId").val()) {
        nsDialog.jAlert("对不起，商品的调出与调入仓库相同，无法调拨！");
        return false;
    }
    //检查是否相同
    if ($(".item").size() >= 2) if (checkSameItem()) {
        nsDialog.jAlert("对不起，单据有重复内容，请修改或删除!");
        return false;
    }
    var amountMsg = "", lackMsg = "";
    $(".itemAmount").each(function (i) {
        if (!$(this).val()) {
            $(this).val(0)
        }
        if (parseFloat($.trim($(this).val())) * 1 == 0) {
            amountMsg = amountMsg + "<br>第" + (i + 1) + "行商品数量为0，请填写正确的商品数量！";
        }
        var idPrefix = $(this).attr("id").split(".")[0];
        if ($("#" + idPrefix + "\\.inventoryAmount").val() * 1 - $(this).val() * 1 < 0) {
            lackMsg = lackMsg + "<br>第" + (i + 1) + "行商品库存不足，请修改！";
        }
    });
    if (!G.Lang.isEmpty(amountMsg)) {
        nsDialog.jAlert("对不起" + amountMsg);
        return false;
    }
    if (!G.Lang.isEmpty(lackMsg)) {
        nsDialog.jAlert("对不起" + lackMsg);
        return false;
    }
    return true;
}
//目前只有库存信息要更新
function updateProductInventory(data, isShowMsg) {
    var lackMsg = "";
    $("#table_productNo").find(".item").each(function (i) {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            $(this).find("input[id$='.inventoryAmount']").val(G.normalize(data[productId], "0"));
            if (Number($(this).find("input[id$='.inventoryAmount']").val()) - Number($(this).find("input[id$='.amount']").val()) < 0) {
                lackMsg = lackMsg + "<br>第" + (i + 1) + "行商品库存不足，无法调拨";
            }
        }
    });
    if (isShowMsg && !G.Lang.isEmpty(lackMsg)) {
        nsDialog.jAlert("对不起" + lackMsg);
        return false;
    }
}
function updateProductInStorageBin(data) {
    $("#table_productNo").find(".item").each(function () {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            $(this).find("input[id$='.inStorageBin']").val(G.normalize(data[productId], ""));
        }
    });
}
function setItemTotal() {
    $(".itemCostPrice").each(function (i) {
        var price = $(this).val();
        var idPrefix = $(this).attr("id").split(".")[0];
        if ($.trim(price)) {
            price = G.rounding(price, 2)
            $(".itemCostPrice").eq(i).val(price > 0 ? price : '');
        }
        var amount = G.rounding($("#" + idPrefix + "\\.amount").val(), 2);
        $("#" + idPrefix + "\\.amount").val(amount > 0 ? amount  :  '');
        var count = parseFloat(price * amount);
        count = G.rounding(count, 2);
        $("#" + idPrefix + "\\.totalCostPrice").val(count > 0 ? count : '');
    });
}
function setTotal() {
    setItemTotal();
    var totalAmount = 0;
    $(".itemAmount").each(function (i) {
        if (!GLOBAL.Lang.isEmpty($(this).val())) {
            totalAmount += parseFloat($(this).val());
        } else {
            $(this).val("");
        }
    });
    totalAmount = dataTransition.rounding(totalAmount, 2);
    $("#totalAmount").val(totalAmount);

    var totalCostPrice = 0;
    $(".itemTotalCostPrice").each(function (i) {
        if (!GLOBAL.Lang.isEmpty($(this).val())) {
            totalCostPrice += parseFloat($(this).val());
        } else {
            $(this).val("");
        }
    });
    totalCostPrice = dataTransition.rounding(totalCostPrice, 2);
    $("#totalCostPrice").val(totalCostPrice);

}

function addItemRow() {
    var tr = $(getTrSample()).clone(); //克隆模版，初始化所有的INPUT
    $(tr).find("input,a").each(function (i) {
        //ID为空则跳过
        if (!this || !this.id)return;
        var idSuffix = this.id.split(".")[1];
        var tcNum = trCount;
        while (checkThisDom(tcNum, idSuffix)) {     //计算行号，如果目标行号对应的元素存在，则该行号加一
            tcNum = ++tcNum;
        }
        //组装新的ID和NAME  Begin-->
        var newId = "itemDTOs" + tcNum + "." + idSuffix;
        $(this).attr("id", newId);
        if ($(this).attr("name").split(".")[1]) {
            var newName = "itemDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
            $(this).attr("name", newName);
        }
    });
    $(tr).insertBefore(".heji_tb");
    $(tr).find("input[type='text']").each(function (i) {
        //去除文本框的自动填充下拉框
        $(this).attr("autocomplete", "off");
    });
    trCount++;
    isShowAddButton();
//    tableUtil.tableStyle('#table_productNo', '.title_tb,.heji_tb,.memo_bg');
    setTotal();
    //end
    return $(tr);
}

function getTrSample() {
    var trSample = '<tr class="bg item table-row-original" >';
    trSample += '<td style="padding-left:10px;">' +
        '<input id="itemDTOs0.commodityCode" name="itemDTOs[0].commodityCode" type="text" class="textbox" maxlength="20" />' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.id" name="itemDTOs[0].id" type="hidden" autocomplete="off"/>' +
        '<input id="itemDTOs0.productId" name="itemDTOs[0].productId" type="hidden" autocomplete="off"/>' +
        '<input id="itemDTOs0.productName" name="itemDTOs[0].productName" type="text" class="textbox"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.brand" name="itemDTOs[0].brand" class="textbox" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.spec" name="itemDTOs[0].spec" class="textbox" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.model" name="itemDTOs[0].model" class="textbox" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand" class="textbox" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel" class="textbox" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.costPrice" name="itemDTOs[0].costPrice" style="width:60px;border: 0 none;background-color:transparent;" class="itemCostPrice txt" value="" readonly="readonly" type="text" />' +
        '</td>';
//    trSample+='<td class="storage_bin_td">'+
//        '<input id="itemDTOs0.inStorageBin" name="itemDTOs[0].inStorageBin" style="width:40px;" class="textbox" maxlength="10"/>'+
//        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.amount" name="itemDTOs[0].amount" style="width:30px;" class="itemAmount textbox" type="text" value="" data-filter-zero="true"/>' +
        ' / ' +
        '<input id="itemDTOs0.inventoryAmount" name="itemDTOs[0].inventoryAmount" style="width:40px;border: 0 none;background-color:transparent;display: inline;" class="inventoryAmount textbox" value="" readonly="readonly" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.unit" name="itemDTOs[0].unit" class="textbox" style="border: 0 none;background-color:transparent;" readonly="readonly" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<input id="itemDTOs0.totalCostPrice" name="itemDTOs[0].totalCostPrice" style="width:60px;border: 0 none;background-color:transparent;" class="itemTotalCostPrice txt" value="" readonly="readonly" type="text"/>' +
        '</td>';
    trSample += '<td>' +
        '<a class="opera1" id="itemDTOs0.deleteRowBtn" onfocus="this.blur();">删除</a>' +
        '</td>';
    trSample += '</tr>';
    return trSample;
}
//检查单据是否相同

function checkSameItem() {
    return APP_BCGOGO.Module.wjl.invoiceCommon.checkSameItemForOrder("item");
}
//检查商品编码是否相同
function checkSameCommodityCode() {
    return APP_BCGOGO.Module.wjl.invoiceCommon.checkSameCommodityCode("item");
}
//判断是否显示+按钮
function isShowAddButton() {
    //如果初始化的话就默认加一行
    if ($(".item").size() <= 0) {
        addItemRow();
    }
    $(".item .opera2").remove();
    var opera1Id = $(".item:last").find("td:last>a[class='opera1']").attr("id");
    if (!opera1Id) return;
    $(".item:last").find("td:last>a[class='opera1']").after('<a class="opera2" ' + ' id="itemDTOs' + (opera1Id.split(".")[0].substring(8)) + '.addRowBtn">增加</a>');
}