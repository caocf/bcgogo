var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
var invoiceCommon = APP_BCGOGO.Module.wjl.invoiceCommon;
$(function () {
//    tableUtil.tableStyle("#table_productNo","#totalRowTR","odd");
    $("#storehouseId").val("");
    $("#vestDateStr").datetimepicker({
        "numberOfMonths": 1,
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-20, c",
        "yearSuffix": "",
        "showButtonPanel": true
    });
    $("#pickingMan").live("click focus keyup", function (event) {
        event = event || event.which;
        var keyCode = event.keyCode;
        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }
        droplistLite.show({
            event: event,
            hiddenId: "pickingManId",
            id: "idStr",
            name: "name",
            data: "member.do?method=getSaleMans"
        });
    });

    $("#deleteSaler").hide();
    $("#deleteSaler").live("click", function () {
        $("#pickingMan").val("");
        $("#pickingManId").val("");
        $("#deleteSaler").hide();
    });
    $("#pickingManDiv").mouseenter(function () {
        if ($("#pickingMan").val() && !isDisable()) {
            $("#deleteSaler").show();
        }
    });
    $("#pickingManDiv").mouseleave(function () {
        $("#deleteSaler").hide();
    });
    $(document).click(function (e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (target && typeof(target.id) == "string" && target.id.split(".")[1] != "productName" && target.id.split(".")[1] != "brand"
            && target.id.split(".")[1] != "spec" && target.id.split(".")[1] != "model"
            && target.id.split(".")[1] != "vehicleBrand" && target.id.split(".")[1] !=
            "vehicleModel"
            && target.id.split(".")[1] != "vehicleYear" && target.id.split(".")[1] !=
            "vehicleEngine"
            && target.id != "div_brand") {
            $("#div_brand")[0].style.display = "none";
        }
    });

    $(document).click(function (e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName") {
            $("#div_serviceName").hide();
        }
    });
    //删除行  //TODO 点击减号所做的删除改行的操作
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "btnMinus") return;
        if ($("#id").val()) return false;
        var idPrefix = $(target).attr("id").split(".")[0];
        $("#"+idPrefix+"_supplierInfo").remove();
        $(target).closest("tr").remove();
        isShowAddButton();
//           tableUtil.setRowBackgroundColor("#table_productNo", null, "#totalRowTR", 'odd');
        setTotal();
    });

    //增加行          //TODO 加号按钮单击触发的脚本
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "btnPlus")  return;

        var ischeck = checkVehicleInfo(target);       //TODO 检查改行数据是否符合验证规则
        if (!ischeck && ischeck != null) return;
        //采购单检查是否相同
        if ($(".item").size() >= 2) {
            if (invoiceCommon.checkSameItemForOrder("item")) {
                nsDialog.jAlert("单据有重复内容，请修改或删除。");
                return false;
            }
            if (invoiceCommon.checkSameCommodityCode("item")) {
                nsDialog.jAlert("商品编码有重复内容，请修改或删除。");
                return false;
            }
        }
        innerPickingAdd();
    });
    $(".itemAmount").live("blur",function () {
            var format = $(this).val();
            format = G.rounding(format, 2);
            $(this).val() && $(this).val(format);
            setTotal();
        }).live("change", function () {
            setTotal();
        });
    $("#picking_btn").bind("click", function () {
        if ($(this).attr("submitLock")) {
            return;
        }
        $(this).attr("submitLock", true);
        deleteEmptyItem();
        var alertStr = hasNewProduct();
        if (!G.isEmpty(alertStr)) {
            alert(alertStr);
            return;
        }
        setTotal();
        $("#btnType").val('picking_btn');
        if (validateInnerPicking()) {
            $('#innerPickingForm').ajaxSubmit({
                url: "pick.do?method=validatorInnerPicking",
                dataType: "json",
                type: "POST",
                success: function (result) {
                    if (result) {
                        if (result.success) {
                            nsDialog.jConfirm("确认领料？", "内部领料提示", function (returnVal) {
                                if (returnVal) {
                                    $("#innerPickingForm").attr("action", "pick.do?method=saveInnerPicking");
                                    $("#innerPickingForm").submit();
                                }
                            });
                        } else {
                            if (result.operation && result.operation == "update_product_inventory") {
                                updateProductInventory(result.data);
                            } else {
                                nsDialog.jAlert(result.msg);
                            }
                        }
                    }
                    $("#picking_btn").removeAttr("submitLock");
                },
                error: function () {
                    nsDialog.jAlert("出现异常！");
                    $("#picking_btn").removeAttr("submitLock");
                }
            });
        }
        $("#picking_btn").removeAttr("submitLock");
    });
    isShowAddButton();
    setTotal();
})


function innerPickingAdd() {
    var tr = $(getInnerPickingTrSample()).clone();     //TODO 克隆模版，初始化所有的INPUT
    $(tr).find("input").val("");
    $(tr).find("input,span,a").each(function (i) {
        //去除文本框的自动填充下拉框
        if ($(this).attr("type") == "text") {
            $(this).attr("autocomplete", "off");
        }
        if (!this || !this.id)return;
        //replace id
        var idStrs = this.id.split(".");
        var idPrefix = idStrs[0];
        var idSuffix = idStrs[1];
        var domrows = parseInt(idPrefix.substring(8, idPrefix.length));

        var tcNum = $(".item").size();
        while (checkThisDom(tcNum, idStrs[1])) {
            tcNum = ++tcNum;                       //TODO 计算行号，如果目标行号对应的元素存在，则该行号加一
        }
        //TODO 组装新的ID和NAME  Begin-->
        var newId = "itemDTOs" + tcNum + "." + idStrs[1];
        $(this).attr("id", newId);
        //replace name
        var nameStr = $(this).attr("name");
        if (nameStr == undefined || nameStr == '') {
            return true;
        }
        if ($(this).attr("name").split(".")[1]) {
            var newName = "itemDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
            $(this).attr("name", newName);
        }
        //TODO <-- End
        $(this).attr("autocomplete", "off");
    });
    $("#table_productNo tr:last").before($(tr));
    isShowAddButton();
//    tableUtil.tableStyle("#table_productNo","#totalRowTR","odd");
//    tableUtil.setRowBackgroundColor("#table_productNo", null, "#totalRowTR", 'odd');
    return $(tr);
}

function getInnerPickingTrSample() {
    var trSample = '<tr class="bg item table-row-original">' +
        '<td style="padding-left:10px;">' +
        '   <input id="itemDTOs0.commodityCode" name="itemDTOs[0].commodityCode" type="text" class="txt checkStringEmpty" value="" maxlength="20"/>' +
        '   <input id="itemDTOs0.id" name="itemDTOs[0].id" type="hidden" value=""/>' +
        '   <input id="itemDTOs0.productId" name="itemDTOs[0].productId" type="hidden" value=""/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.productName" name="itemDTOs[0].productName"  class="txt checkStringEmpty" value="" type="text" maxlength="50"/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.brand" name="itemDTOs[0].brand"  class="txt checkStringEmpty" value="" type="text" maxlength="50"/>' +
        '</td>' +
        '<td>' +
        '   <input id="itemDTOs0.spec" name="itemDTOs[0].spec"  class="txt checkStringEmpty" value="" type="text" maxlength="50"/>' +
        '</td>' +
        '<td>' +
        '   <input id="itemDTOs0.model" name="itemDTOs[0].model"  class="txt checkStringEmpty" value="" type="text" maxlength="50"/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand"  value="" class="txt checkStringEmpty"  type="text" maxlength="200"/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel"  value="" class="txt checkStringEmpty"  type="text" maxlength="200"/>' +
        '</td>' +
        '<td>' +
        '<span id="itemDTOs0.price_span" name="itemDTOs[0].price_span"></span> ' +
        '<input type="hidden"  name="itemDTOs0.price" id="itemDTOs0.price" class="itemPrice"/> ' +
        '</td>' +
        '<td>' +
//        '   <span id="itemDTOs0.unit_span" name="itemDTOs[0].unit_span"></span> ' +
        '   <input id="itemDTOs0.unit" name="itemDTOs[0].unit"  value="" type="hidden"/>' +
        '   <input type="hidden" id="itemDTOs0.storageUnit" name="itemDTOs[0].storageUnit" value=""/>' +
        '   <input type="hidden" id="itemDTOs0.sellUnit" name="itemDTOs[0].sellUnit" value=""/>' +
        '   <input type="hidden" id="itemDTOs0.rate" name="itemDTOs[0].rate" value=""/>' +
        '</td>' +
        '<td >' +
        '   <input id="itemDTOs0.amount"  name="itemDTOs[0].amount"  value="" class="itemAmount txt checkNumberEmpty" style="width: 40px;" type="text" data-filter-zero="true"/>/' +
        '   <span id="itemDTOs0.inventoryAmountSpan" name="itemDTOs[0].inventoryAmountSpan"></span> ' +
        '   <input type="hidden" id="itemDTOs0.inventoryAmount" name="itemDTOs[0].inventoryAmount" value=""/>' +
        '</td>' +
        '<td>' +
        '   <span id="itemDTOs0.total_span" name="itemDTOs[0].total_span"></span>' +
        '   <input id="itemDTOs0.total" name="itemDTOs[0].total"  value="" type="hidden"/>' +
        '</td>' +
        '   <td style="border-right:none;">' +
        '	<a class="btnMinus" id="itemDTOs0.btnMinus" name="itemDTOs[0].btnMinus">删除</a>' +
        '   </td>' +
        '</tr>';
    return trSample;
}

function isShowAddButton() {
    //如果初始化的话就默认加一行
    if ($(".item").size() <= 0) {
        innerPickingAdd();
    }
    $(".item .btnPlus").remove();
    var opera1Id = $(".item:last").find("td:last>a[class='btnMinus']").attr("id");
    if (!opera1Id) return;
    $(".item:last").find("td:last>a[class='btnMinus']").after(' <a class="btnPlus" ' +
        ' id="itemDTOs' + (opera1Id.split(".")[0].substring(8)) + '.btnPlus"' +
        'name="itemDTOs[' + (opera1Id.split(".")[0].substring(8)) + '].btnPlus" ' + '>增加</a>');
}

function setTotal() {
    var total = 0;
    var itemAmountTotal = 0;
    var inventoryAmountTotal = 0;
    $(".itemPrice").each(function (i) {
        var price = $(this).val();
        var idPrefix = $(this).attr("id").split(".")[0];
        if ($("#" + idPrefix + "\\.amount")) {
            itemAmountTotal += $("#" + idPrefix + "\\.amount").val() * 1;
        }
        if ($("#" + idPrefix + "\\.inventoryAmount")) {
            inventoryAmountTotal += $("#" + idPrefix + "\\.inventoryAmount").val()*1;
        }
        var amount = G.rounding($("#" + idPrefix + "\\.amount").val(),2);
        var count = parseFloat(price * amount) * 1;
        total += count;
        var count1 =G.rounding(count,2);
        $("#" + idPrefix + "\\.totalItemAmount_span").val(count1);
        $("#" + idPrefix + "\\.total").val(count1);
        $("#" + idPrefix + "\\.total_span").text(count1 == 0 ? '':count1);
        htmlNumberFilter($("#" + idPrefix + "\\.price_span").add("#" + idPrefix + "\\.amount").add("#" + idPrefix + "\\.inventoryAmountSpan").add("#" + idPrefix + "\\.total_span"),true)
    })
    $("#total_span").text(G.rounding(total,2));
    $("#total").val(G.rounding(total,2));
    $("#totalItemAmount_span").text(G.rounding(itemAmountTotal,2));
    $("#totalInventoryAmount_span").text(G.rounding(inventoryAmountTotal,2));
}
function checkEmptyRow($tr) {
    var propertys = ["productName"];
    var itemInfo = "";
    for(var i = 0, len = propertys.length; i < len; i++) {
        itemInfo += $tr.find("input[id$='." + propertys[i] + "']").val();
    }
    if(GLOBAL.Lang.isEmpty(itemInfo)) {
        return true;
    }
    return false;
}

//校验单据的信息完整与正确性
function validateInnerPicking() {
    if(!$("#pickingMan").val()){
        nsDialog.jAlert("请选择领料人！");
        return false;
    }
    if(!$("#vestDateStr").val()){
        nsDialog.jAlert("请输入领料时间！");
        return false;
    }
    var msg = "";
    var length = 0;
    var trCount =  $("#table_productNo tr .item").size();
    $(".item").each(function (i) {
        var productId = $.trim($(this).find("input[id$='.productId']").val());
        var productName = $.trim($(this).find("input[id$='.productName']").val());
        if (trCount == 1 && checkEmptyRow($(this))) {
            msg=",单据内容不能为空!";
            return false;
        } else if (trCount>1 && checkEmptyRow($(this))) {
            length++;
        } else if (G.Lang.isEmpty(productName)) {
            msg += "<br>第" + ($(this).index()) + "行，缺少品名，请补充完整.";
        }else if (G.Lang.isEmpty(productId)) {
            msg += "<br>第" + ($(this).index()) + "行，商品不存在，请修改.";
        }
    });
    if(!G.Lang.isEmpty(msg)){
        nsDialog.jAlert("对不起"+msg);
        return false;
    }

    //检查是否相同
    if (trCount >= 2) if (invoiceCommon.checkSameItemForOrder("item")) {
        nsDialog.jAlert("对不起，单据有重复内容，请修改或删除!");
        return false;
    }
    var amountMsg = "",lackMsg = "";
    $(".itemAmount").each(function () {
        if (!$(this).val()) {
            $(this).val(0)
        }
        if (parseFloat($.trim($(this).val())) * 1 == 0) {
            amountMsg = amountMsg + "<br>第" + ($(this).index(".itemAmount") + 1) + "行商品数量为0，请填写正确的商品数量！";
        }
        var idPrefix = $(this).attr("id").split(".")[0];
        if ($("#" + idPrefix + "\\.inventoryAmount").val()* 1 - $(this).val() * 1 < 0) {
            lackMsg = lackMsg + "<br>第" + ($(this).index(".itemAmount") + 1) + "行商品库存不足，请修改！";
        }
    });
    if(!G.Lang.isEmpty(amountMsg)){
        nsDialog.jAlert("对不起"+amountMsg);
        return false;
    }
    if(!G.Lang.isEmpty(lackMsg)){
        nsDialog.jAlert("对不起"+lackMsg);
        return false;
    }
    if(!checkStorehouseSelected()) {
        return false;
    }
    return true;
}

//目前只有库存信息要更新
function updateProductInventory(data) {
    var lackMsg = "";
    $("#table_productNo").find(".item").each(function() {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            $(this).find("input[id$='.inventoryAmount']").val(G.normalize(data[productId],"0"));
            $(this).find("span[id$='.inventoryAmountSpan']").text(G.normalize(data[productId],"0"));
            if (Number($(this).find("input[id$='.inventoryAmount']").val()) - Number($(this).find("input[id$='.amount']").val()) < 0) {
                lackMsg = lackMsg + "<br>第" + ($(this).index()+1) + "行商品库存不足，无法领料";
            }
        }
    });
    setTotal();
    if(!G.Lang.isEmpty(lackMsg)){
        nsDialog.jAlert("对不起"+lackMsg);
        return false;
    }
}