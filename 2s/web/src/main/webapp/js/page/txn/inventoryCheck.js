var trCount = 1;
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$().ready(function() {
//  tableUtil.tableStyle('.inventoryCheck_show','.title_tb');
    $("#storehouseId").val("")
    $("#backBtn").click(function() {
        window.location.href = "inventoryCheck.do?method=toInventoryCheckRecord";
    });

    $("#startTimeInput,#endTimeInput,#inventoryCheckTime").bind("click",
            function() {
                $(this).blur();
            }).datetimepicker({
                "numberOfMonths": 1,
                "showButtonPanel": true,
                "changeYear": true,
                "changeMonth": true,
                "yearRange":"c-100:c+100",
                "showHour":true,
                "showMinute":true,
                "dateFormat":"yy-mm-dd",
                "yearSuffix": "",
                "onClose": function(dateText, inst) {
                    if (!$(this).val()) {
                        return;
                    }
                    if ($("#startTimeInput").val() >= $("#endTimeInput").val()) {
                        nsDialog.jAlert("开始时间应小于结束时间，请重新选择！");
                        $(this).val(inst.lastVal);
                    }
                },
                "onSelect": function(dateText, inst) {
                    if (inst.lastVal == dateText) {
                        return;
                    }

                    $(this).val(dateText);
                }
            });



    $("#my_date_self_defining").click();
    $("#searchBtn").click(function() {
        var data = {
            startTimeStr:$("#startTimeInput").val(),
            endTimeStr:$("#endTimeInput").val(),
            checkResultFlag:$("#checkResultSelect").val(),
            storehouseId:$("#storehouseId").val(),
            editor:$("#editor").val(),
            startPageNo:1
        };

        App.Net.asyncPost({
            url:"inventoryCheck.do?method=getInventoryChecks",
            data:data,
            cache:false,
            dataType:"json",
            success:function(json) {
                initInventoryCheckRecordTable(json);
                initPages(json, "dynamical1", "inventoryCheck.do?method=getInventoryChecks", '', "initInventoryCheckRecordTable", '', '', data, '');
            },
            error:function() {
                nsDialog.jAlert("查询盘点记录异常！");
            }
        });

    });

    $("#print").bind("click", function() {
        if ($("#id").val()) {
            window.open("inventoryCheck.do?method=print&id=" + $("#id").val(), '', "dialogWidth=1024px;dialogHeight=768px");
            return;
        }
    });

    $("#editor").live("click focus keyup", function (event) {
        event = event || event.which;
        var keyCode = event.keyCode;

        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }

        var obj = this;
        droplistLite.show({
            event: event,
            id: "id",
            name: "name",
            data: "member.do?method=getSaleMans"
        });
    });
    trCount = $(".item").size();
    setTotal();
    if ($("#id").val()) {
        $(".inventoryCheckedImg").show();
        $("input:not(:button)").attr("disabled", "disabled");
        $(".searchCenter input").removeAttr("disabled");
        $("select").attr("disabled", "disabled");
        $("#saveBtn").attr("disabled", "disabled");
        $("#cancelBtn").attr("disabled", "disabled");
        $("#saveBtn").hide();
        $(".addRowBtn").die();
        $(".dele_a").die();
        $("#storehouseDiv select").remove();
        var storehouseName = $("#storehouseName").val();
        $("#storehouseDiv").append('<input value="' + storehouseName + '" style="width:100px" readonly="true"/>');
        if (!G.isEmpty($("#receiptNo").val())) {
            $("#receiptNoSpan").text($("#receiptNo").val());
        }
    }
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "opera1")  return;
        if ($("#id").val()) return false;
        $(target).closest("tr").remove();
        var iPrefixId = $(target).attr("id").split(".")[0];
        $("#" + iPrefixId + "_supplierInfo").remove();
        showAddBtn();
        trCount = $(".item").size();
        setTotal();

    });

    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "opera2")  return;
        if ($("#id").val()) return false;
        if ($(".item").size() == 0) {
            inventoryCheckItemAdd();
            return;
        } else {
            var productName = $("#table_productNo .item:last").find("[id$='.productName']").val();
            if (jQuery.trim(productName) == "") {
                alert("请输入或者选择品名！");
                return;
            }
            inventoryCheckItemAdd();
        }
    });

    $('[id$=".actualInventoryAmount"]').live("keyup",
            function() {
                setTotal();
            }).live("blur", function() {
                var amount = G.rounding($(this).val(), 2);
                if (amount < 0) {
                    $(this).val(0);
                    nsDialog.jAlert("盘点数量不能为负！");
                    return;
                }
                $(this).val() != '' && $(this).val(amount);
                setTotal();
            });

//    $('input[name$="actualInventoryAveragePrice"]').live("blur",
//        function() {
//            if (!$(this).val()) {
//                $(this).val(0);
//            }
//            $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 2));
//
//            var prefix = $(this).attr("id").split(".")[0];
//            var inventoryAveragePrice = parseFloat($("#" + prefix + "\\.inventoryAveragePrice").val());
//            var actualInventoryAveragePrice = parseFloat($("#" + prefix + "\\.actualInventoryAveragePrice").val());
//
//            if(isNaN(inventoryAveragePrice)||isNaN(actualInventoryAveragePrice))
//                return;
//            var inventoryAveragePriceAdjustment = (actualInventoryAveragePrice-inventoryAveragePrice).toFixed(2);;
//            $("#" + prefix + "\\.inventoryAveragePriceAdjustment").val(inventoryAveragePriceAdjustment);
//            setTotal();
//        }).live('keyup', function() {
//            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
//        });

    $("#saveBtn").live('click', function () {
        if ($(this).attr("disabled") == "disabled") {
            return;
        }
        deleteEmptyItem();
        var alertStr = hasNewProduct();
        if (!G.isEmpty(alertStr)) {
            alert(alertStr + "无法盘点，请删除！");
            return;
        }
        var msg = "";
        $(".item").each(function (i) {
            if (isEmptyOrderItem($(this))) {
                msg += "<br>第" + (i + 1) + "行，单据内容不能为空!";
            }
        });
        if (!G.isEmpty(msg)) {
            nsDialog.jAlert(msg);
            return;
        }
        if (APP_BCGOGO.Module.wjl.invoiceCommon.checkSameItemForOrder("item")) {
            nsDialog.jAlert("单据有重复内容，请修改或删除。");
            return false;
        }
        if (APP_BCGOGO.Permission.Version.StoreHouse) {
            if ($("#storehouseId").val() == "") {
                nsDialog.jAlert("请选择仓库!");
                return;
            }
        }
        resetActualInventoryAmount();//实际库存为空时，设置为0
        if (confirm("确认盘点结果？")) {
            if (APP_BCGOGO.Permission.Version.StoreHouse) {
                APP_BCGOGO.Net.syncPost({
                    url: "inventoryCheck.do?method=validateInventoryCheck",
                    dataType: "json",
                    data: {"storehouseId": $("#storehouseId").val()},
                    success: function (result) {
                        if (result.success) {
                            $("#inventoryCheckForm").submit();
                        } else {
                            nsDialog.jAlert(result.msg);
                        }
                    },
                    error: function () {
                        nsDialog.jAlert("验证时产生异常，请重试！");
                    }
                });
            } else {
                $("#inventoryCheckForm").submit();
            }

        }

    });

    $("#cancelBtn").live('click', function() {

        $(".item").remove();
        inventoryCheckItemAdd();
        trCount = 1;
    });
});


function setTotal() {
    var adjustPriceTotal = 0;
    $(".item").each(function(i) {
        var idPrefix = $(this).find("[id$='.productId']").attr("id").split(".")[0];
        var price = parseFloat($("#" + idPrefix + "\\.inventoryAveragePrice").val());
        var inventoryAmount = parseFloat($("#" + idPrefix + "\\.inventoryAmount").val());
        var actualInventoryAmount = parseFloat($("#" + idPrefix + "\\.actualInventoryAmount").val());
        isNaN(price) && (price = 0);
        isNaN(inventoryAmount) && (inventoryAmount = 0);
        isNaN(actualInventoryAmount) && (actualInventoryAmount = 0);
        var amount = actualInventoryAmount - inventoryAmount;
        $("#" + idPrefix + "\\.inventoryAmountAdjustment").val(amount !=  0 ? G.rounding(amount, 2) : '');
        var itemTotal = G.rounding(amount * price, 2);
        $("#" + idPrefix + "\\.inventoryAdjustmentPrice").val(itemTotal != 0 ? itemTotal : '');
        adjustPriceTotal += itemTotal;
    });
    adjustPriceTotal = G.rounding(adjustPriceTotal, 2);
    $("#adjustPriceTotal").val(adjustPriceTotal);
    if (adjustPriceTotal >= 0) {
        $("#checkResult").text("盘盈" + adjustPriceTotal);
    } else {
        $("#checkResult").text("盘亏" + adjustPriceTotal * (-1));
    }

    var total = 0;
    $.each($('.itemAmount'), function(i, n) {
        var val = $(n).val();
        total += (isNaN(val) ? 0 : Number(val));
    });
    $('#countModifyInventoryAmount').text(GLOBAL.Number.filterZero(total));

    var inventoryTotal = 0;
    $("input[id$='inventoryAmount']").each(function(i, n) {
        var val = $(n).val();
        inventoryTotal += (isNaN(val) ? 0 : Number(val));
    });
    $("#countInventoryAmount").text(GLOBAL.Number.filterZero(inventoryTotal));
}

function getTrSample() {
    var trSample = '<tr class="bg item">' +
            '<td style="border-left:none;padding-left: 10px;">' +
            '<input type="text" value=""  name="itemDTOs[0].commodityCode" id="itemDTOs0.commodityCode" autocomplete="off">' +
            '</td>' +
            '<td>' +
            '<input type="text" value=""  name="itemDTOs[0].productName" id="itemDTOs0.productName" autocomplete="off" >' +
            '<input type="hidden" value="" name="itemDTOs[0].productId" id="itemDTOs0.productId">' +

            '<td>' +
            '<input type="text" value=""  name="itemDTOs[0].brand" id="itemDTOs0.brand" autocomplete="off">' +
            '</td>' +
            '<td>' +
            '<input type="text" value=""  name="itemDTOs[0].spec" id="itemDTOs0.spec" autocomplete="off">' +
            '</td>' +
            '<td>' +
            '<input type="text" value="" name="itemDTOs[0].model" id="itemDTOs0.model" autocomplete="off" >' +
            '</td>' +
            '<td>' +
            '<input type="text" value="" name="itemDTOs[0].vehicleBrand" id="itemDTOs0.vehicleBrand"  autocomplete="off" maxlength="200">' +
            '</td>' +
            '<td>' +
            '<input type="text" value="" name="itemDTOs[0].vehicleModel" id="itemDTOs0.vehicleModel"  autocomplete="off" maxlength="200">' +
            '</td>' +
            '<td>' +
            '<input type="text"  name="itemDTOs[0].inventoryAveragePrice" class="checkNumberEmpty text_style" readonly="true" ' +
            'id="itemDTOs0.inventoryAveragePrice" autocomplete="off" autocomplete="off">' +
            '</td>' +
            '<td>' +
            '<input type="text"  name="itemDTOs[0].inventoryAmountUnit" id="itemDTOs0.inventoryAmountUnit" class="checkNumberEmpty text_style" autocomplete="off" readonly="true">' +
            '<input type="hidden"  name="itemDTOs[0].inventoryAmount" id="itemDTOs0.inventoryAmount" class="checkNumberEmpty text_style" >' +
            '<input type="hidden"  name="itemDTOs[0].unit" id="itemDTOs0.unit" class="checkNumberEmpty text_style" >' +
            '</td>' +

            '<td align="center">' +
            '<input type="text" name="itemDTOs[0].actualInventoryAmount" class="checkNumberEmpty itemAmount"  id="itemDTOs0.actualInventoryAmount" autocomplete="off" data-filter-zero="true">' +
            '</td>' +
            '<td>' +
            '<input type="text" name="itemDTOs[0].inventoryAmountAdjustment" id="itemDTOs0.inventoryAmountAdjustment" class="checkNumberEmpty text_style"  readonly="true">' +
            '</td>' +
            '<td>' +
            '<input type="text" name="itemDTOs[0].inventoryAdjustmentPrice" class="checkNumberEmpty text_style" style="width:120%" id="itemDTOs0.inventoryAdjustmentPrice" readonly="true" autocomplete="off" autocomplete="off">' +
            '</td>' +
            '<td style="text-align:center;">' +
            '<a id="itemDTOs0.deletebutton" class="opera1" onfocus="this.blur();">删除</a></td>' +
            '</tr>';


    return trSample;
}

function inventoryCheckItemAdd() {
    var tr = $(getTrSample()).clone();     //TODO 克隆模版，初始化所有的INPUT
    $(tr).find("input").val("");
    $(tr).find("[id^=itemDTOs]").each(function(i) {
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

        var tcNum = trCount;
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
    $(("#table_productNo")).append(tr);
    trCount++;

//  $("#table_productNo tr:even").not($(".title_tb")[0]).css("background", "#E5E5E5");
    showAddBtn();
    return $(tr);
}


$().ready(function () {


});

//目前只有库存信息要更新
function updateProductInventory(data, isShowMsg) {
    $("#table_productNo").find(".item").each(function () {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            $(this).find("input[id$='.inventoryAmount']").val(G.rounding(data[productId], 2));
            $(this).find("input[id$='.actualInventoryAmount']").val(G.rounding(data[productId], 2)).keyup(); //触发事件,重新计算盘点数量与金额
            $(this).find("input[id$='.inventoryAmountUnit']").val(G.rounding(data[productId], 2) + $(this).find("input[id$='.unit']").val());
        }
    });
}

function getInventoryCheckRecord(inventoryCheckId) {
    window.open('inventoryCheck.do?method=getInventoryCheck&inventoryCheckId=' + inventoryCheckId);
}

function initInventoryCheckRecordTable(json) {
    $("#inventoryCheckTable tr:not(:first)").remove();
    $("#totalRecords").text(0);
    $("#stockAdjustPriceTotal").text(0);
    $("#stockAdjustPriceTotal").removeClass("red_color");
    $("#stockAdjustPriceTotal").addClass("green_color");
    if (stringUtil.isEmpty(json[0])) {
        return;
    }
    var inventoryChecks = json[0][0];
    if (stringUtil.isEmpty(inventoryChecks) || inventoryChecks.length == 0) {
        return;
    }
    var inventoryCheck;
    var trStr = "";
    for (var i = 0; i < inventoryChecks.length; i++) {
        inventoryCheck = inventoryChecks[i];
        var editor = G.normalize(inventoryCheck.editor);
        var editDateStr = G.normalize(inventoryCheck.editDateStr);
        var storehouseName = G.normalize(inventoryCheck.storehouseName);
        var receiptNo = G.normalize(inventoryCheck.receiptNo);
        var adjustPriceTotal = dataTransition.rounding(inventoryCheck.adjustPriceTotal, 2);
        var inventoryCheckId = inventoryCheck.idStr;
        trStr = '<tr>';
        trStr += '<td style="padding-left: 10px;">' + (i + 1) + '</td>';
        trStr += '<td>' + '<a href ="#" class="blue_col" onclick="getInventoryCheckRecord(\'' + inventoryCheckId + '\')">' + receiptNo + '</a> ' + '</td>';
        if ($("#isHaveStoreHouseShop").attr("isHaveStoreHouseShop") == 'true') {
            trStr += '<td>' + storehouseName + '</td>';
        }
        if (adjustPriceTotal > 0) {
            trStr += '<td class="green_color">' + "盘盈" + '</td>'
            trStr += '<td class="green_color">' + adjustPriceTotal + '</td>';
        } else if (adjustPriceTotal < 0) {
            trStr += '<td class="red_color">' + "盘亏" + '</td>'
            trStr += '<td class="red_color">' + adjustPriceTotal + '</td>';
        } else {
            trStr += '<td>' + "--" + '</td>'
            trStr += '<td>' + 0 + '</td>';
        }
        trStr += '<td>' + editor + '</td>';
        trStr += '<td>' + editDateStr + '</td>';
        trStr += '</tr>';
        $("#inventoryCheckTable").append(trStr);
    }
    $("#totalRecords").text(json[0][1]);
    var stockAdjustPriceTotal = dataTransition.rounding(json[0][2], 2);
    if (stockAdjustPriceTotal < 0) {
        $("#stockAdjustPriceTotal").text("盘亏" + String(stockAdjustPriceTotal *= (-1)));
        $("#stockAdjustPriceTotal").removeClass("green_color").addClass("red_color");
    } else if (stockAdjustPriceTotal > 0) {
        $("#stockAdjustPriceTotal").text("盘盈" + String(stockAdjustPriceTotal));
        $("#stockAdjustPriceTotal").removeClass("red_color").addClass("green_color");
    }
    tableUtil.tableStyle('#inventoryCheckTable', '.titleBg');
}


function selectInput(dom) {
    var value = dom.options[dom.selectedIndex].value;
    $(dom).val(value);
}

function showAddBtn() {
    if ($(".item").size() <= 0) {
        $(".opera2").trigger("click");
    }
    $(".item .opera2").remove();
//    var opera1Id = $(".item:last").find("td:last>a[class='opera1']").attr("id");
//    if(!opera1Id) return
    $(".item:last").find("td:last>a[class='opera1']").append('<a class="opera2" onfocus="this.blur();">增加</a>');
}

function resetActualInventoryAmount() {
    $("input[id$='.actualInventoryAmount']").each(function(){
         if(G.isEmpty($(this).val())) {
             $(this).val(0);
         }
    });
}

$(function () {
    $('input[data-filter-zero-advanced=true]').each(function () {
        var text = $(this).val();
        var val = /\d*\.*\d*/.exec(text);
        if (val.length > 0) {
            val = GLOBAL.Number.filterZero(val[0]);
            text = text.replace(/\d*\.*\d*/, val);
            if(text == '0'){
                text = '';
            }
            $(this).val(text);
        }
    });
});