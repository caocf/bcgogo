var trCount;
var invoiceCommon = APP_BCGOGO.Module.wjl.invoiceCommon;
$(function() {
    $(".xi_show").toggle(function() {
        $(".supplierDetailInfo").show();
    }, function() {
        $(".supplierDetailInfo").hide();
    });
    $("#clickShow").hover(function(){
        $(".tuihuo_first table tr .xiangxi_td a #clickShow").css("color","#FD5300");
    },function(){
        $(".tuihuo_first table tr .xiangxi_td a #clickShow").css("color","#007CDA");
    });
    var errorMsg = $("#errorMsg").html();
    if(!GLOBAL.Lang.isEmpty(errorMsg) && errorMsg != "true") {
        nsDialog.jAlert(errorMsg);
    }

    initDuiZhanInfo();
//    tableUtil.tableStyle('#table_productNo', '.title_tb,.heji_tb,.heji_tb,.memo_bg,.plain_bg');

    $("#supplier").live("blur", function() {
        var supplierId = $("#supplierId").val();
        if(supplierId != '' && supplierId != null) {
            APP_BCGOGO.Net.syncAjax({
                url: "goodsReturn.do?method=getSupplierDetailInfo",
                dataType: "json",
                data: {
                    supplierId: supplierId
                },
                success: function(json) {
                    if(json.idString) {
                        $("#abbr").val(json.abbr);
                        $("#category").val(json.category);
                        $(".mobile").val(json.mobile);
                        $("#landline").val(json.landLine);
                        $("#email").val(json.email);
                        $("#qq").val(json.qq);
                        $("#fax").val(json.fax);
                        $("#settlementType").val(json.settlementType);
                        $("#bank").val(json.bank);
                        $("#accountName").val(json.accountName);
                        $("#account").val(json.account);
                        $("#invoiceCategory").val(json.invoiceCategory);
                        $("#businessScope").val(json.businessScope);
                    }
                }
            });
        }
    });

    //设定联系人相关的input框为readonly
    contactDeal(!G.isEmpty($("#supplierId").val()) && !G.isEmpty($("#contactId").val()));
    $('#itemDTOs0\\.amount').length && Number($('#itemDTOs0\\.amount').val()) == 0 && $('#itemDTOs0\\.amount').val('');
});

function isShowAddButton() {
    //如果初始化的话就默认加一行
    if($(".item").size() <= 0) {
        $(".opera2").trigger("click");
    }
    $(".item .opera2").remove();
    var opera1Id = $(".item:last").find("td:last>a[class='dele_a']").attr("id");
    if(!opera1Id) return;
    if(G.isEmpty($("#originOrderId").val())){   //退货的新增按钮不显示
        $(".item:last").find("td:last>a[class='dele_a']").after('<a class="opera2" ' + ' id="itemDTOs' + (opera1Id.split(".")[0].substring(8)) + '.plusbutton">增加</a>');
    }
}
//TODO 用于在退货单中添加新的一行

function returnStorageOrderAdd() {

    var tr = $(getTrSample()).clone(); //TODO 克隆模版，初始化所有的INPUT
    $(tr).find("input").val("");
    $(tr).find(".itemTotal,.itemPrice,.inventoryAmount,.itemAmount").val("");
    $(tr).find("input,span,a").each(function(i) {
        //TODO ID为空则跳过
        if(!this || !this.id) return;
        var idSuffix = this.id.split(".")[1];

        var tcNum = trCount;
        while(checkThisDom(tcNum, idSuffix)) { //TODO 计算行号，如果目标行号对应的元素存在，则该行号加一
            tcNum = ++tcNum;
        }

        //TODO 组装新的ID和NAME  Begin-->
        var newId = "itemDTOs" + tcNum + "." + idSuffix;
        $(this).attr("id", newId);
        if($(this).attr("name").split(".")[1]) {
            var newName = "itemDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
            $(this).attr("name", newName);
        }
    });
    $("#table_productNo").append(tr);
//    $(tr).insertBefore(".heji_tb");
    $(tr).find("input[type='text']").each(function(i) {
        //去除文本框的自动填充下拉框
        $(this).attr("autocomplete", "off");
    });
    isShowAddButton();
    trCount++;
    setTotal();
    return $(tr);
}


function getTrSample() {

    var trSample = '<tr class="bg item table-row-original" >';
    trSample += '<td style="padding-left:10px;">' + ' <input id="itemDTOs0.commodityCode" name="itemDTOs[0].commodityCode" type="text" class="checkStringEmpty textbox" value="" maxlength="20" style="width:80%"/>' + '</td>';
    trSample += '<td>' + '<input id="itemDTOs0.productId" name="itemDTOs[0].productId" type="hidden" value=""/>' + '<input id="itemDTOs0.productName" class="checkStringEmpty textbox" name="itemDTOs[0].productName" style="width:80%" type="text" value=""/>' + '</td>';
    trSample += '<td>' + '<input id="itemDTOs0.brand" name="itemDTOs[0].brand" class="checkStringEmpty textbox" maxlength="100" style="width:80%" type="text" value=""/>' + '</td>';
    trSample += '<td>' + '<input id="itemDTOs0.spec" name="itemDTOs[0].spec" class="checkStringEmpty textbox" style="width:80%;" type="text" value=""/>' + '</td>';
    trSample += '<td>' + '<input id="itemDTOs0.model" name="itemDTOs[0].model" class="checkStringEmpty textbox" style="width:80%;" type="text" value=""/>' + '</td>';
    trSample += '<td>' + '<input id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand" class="checkStringEmpty textbox" maxlength="200" style="width:80%;" type="text"/>' + '</td>';
    trSample += '<td>' + '<input id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel" class="checkStringEmpty textbox" maxlength="200" style="width:80%;" type="text"/>' + '</td>';
    trSample += '<td>' + '<span  id="itemDTOs0.inventoryAveragePrice"  name="itemDTOs[0].inventoryAveragePrice"></span>'+'</td>';
    trSample += '<td>' + '<input id="itemDTOs0.price" name="itemDTOs[0].price" style="width:80%;" title=""  class="itemPrice checkNumberEmpty textbox" type="text" value="" data-filter-zero="true"/>' + '<input id="itemDTOs0.purchasePrice" name="itemDTOs[0].purchasePrice" type="hidden" value=""/>' + '</td>';
    trSample += '<td>' + '<input id="itemDTOs0.amount" name="itemDTOs[0].amount" style="width:30px;" title=""  class="itemAmount checkNumberEmpty textbox" type="text" value="" data-filter-zero="true"/>' + ' / ' + '<input id="itemDTOs0.inventoryAmount" name="itemDTOs[0].inventoryAmount" style="width:30px;border: 0 none;background-color:transparent;" class="inventoryAmount" readonly="readonly" type="text" value=""/>' + '<input id="itemDTOs0.returnAbleAmount" name="itemDTOs[0].returnAbleAmount" type="hidden" value=""/>'+ '<input id="itemDTOs0.reserved" name="itemDTOs[0].reserved" type="hidden" value=""/>' + '</td>';
    trSample += '<td>' + '<input id="itemDTOs0.unit" name="itemDTOs[0].unit" style="width:80%;" class="itemUnit checkStringEmpty textbox" type="text" value=""/>' + '<input id="itemDTOs0.storageUnit" name="itemDTOs[0].storageUnit" class="itemStorageUnit" type="hidden" value=""/>' + '<input id="itemDTOs0.sellUnit" name="itemDTOs[0].sellUnit" class="itemSellUnit" type="hidden" value=""/>' + '<input id="itemDTOs0.rate" name="itemDTOs[0].rate"  class="itemRate" type="hidden" value=""/>' + '</td>';
    trSample += '<td>' + '<input id="itemDTOs0.total" name="itemDTOs[0].total" style="width:60px;border: 0 none;;background-color:transparent;" title="0"  class="itemTotal textbox" readonly="readonly" type="text" value=""/>' + '</td>';
    trSample += '<td>';
    trSample += '<a class="dele_a" id="itemDTOs0.deletebutton" name="itemDTOs[0].deletebutton">删除</a>';
    trSample += '</td>';
    trSample += '</tr>';


    return trSample;
}

//验证表单内的信息是否完整


function checkInfoIntegrity(_lastRow) {

    var _idPrefix = _lastRow.find('input[id$="commodityCode"]').attr("id").split(".")[0];
    if(_idPrefix == "" || _idPrefix == "itemDTOs-1") {
        return false;
    } else {
        var _productName = $("#" + _idPrefix + "\\.productName").val(),
            _price = $("#" + _idPrefix + "\\.price").val(),
            _amount = $("#" + _idPrefix + "\\.amount").val();

        //验证品名
        if(_productName == "") {
            nsDialog.jAlert("请输入或选择品名!");
            return false;
        }
        //验证退货价
        else if(_price == "") {
            nsDialog.jAlert("请输入退货价!");
            return false;
        }
        //验证退货数量
        else if(_amount == "") {
            nsDialog.jAlert("请输入退货数量!");
            return false;
        }

        return true;
    }
}

$(".dele_a").live('click', function(event){

    //TODO 在旧单据中不允许删一行
    if($("#id").val() && $("#status").val() != 'SELLER_PENDING') {
        return;
    }
    if ($(".item").size() <= 1 && $("#originOrderId").val()) {
        nsDialog.jAlert("退货商品不能全部删除");
        return false;
    }
    var rowIndex = this.parentNode.parentNode.rowIndex;
    $("#table_productNo")[0].deleteRow(rowIndex);
    var iPrefixId = $(event.target).attr("id");
    iPrefixId = iPrefixId.substring(0, iPrefixId.indexOf("."));
    $("#"+iPrefixId+"_supplierInfo").remove();
    isShowAddButton();
    setTotal();
    //当没有行的时候新增空行
    if($('.item','#table_productNo').length<1){
        returnStorageOrderAdd();
    }
    trCount=$(".item").size();

});

function setItemTotal() {
    $(".itemPrice").each(function(i) {
        var idPrefix = $(this).attr("id").split(".")[0];
        var price = $(this).val();
        var amount = $(".itemAmount").eq(i).val();
        if($.trim(price) != '' && $.trim(amount) != '') {
            var count = parseFloat(price * amount);
            $(".itemTotal").eq(i).val(count ? dataTransition.rounding(count, 2) : '');
            $(".itemTotal").attr("title", count.toString());
        }else{
            $(".itemTotal").eq(i).val('');
            $(".itemTotal").attr("title", '');
        }
        htmlNumberFilter($("#" + idPrefix + "\\.price").add("#" + idPrefix + "\\.amount").add("#" + idPrefix + "\\.total").add("#" + idPrefix + "\\.inventoryAmount"), true);
    });
}

function setTotal() {
    setItemTotal();
    var total = getServiceTotal();
    var totalReturnAmount = getReturnAmountTotal();
    total = dataTransition.rounding(total, 2);
    totalReturnAmount = dataTransition.rounding(totalReturnAmount, 2);
    $("#total").val(total);
    $("#totalSpan").text(total);
    $("#totalReturnAmount").val(totalReturnAmount);
}

function getServiceTotal() {
    var count = 0;
    $(".itemTotal").each(function(i) {
        var txt = $(this);
        if($.trim(txt.val()) != '') count += parseFloat(txt.val());
    });
    return count;
}

function getReturnAmountTotal() {

    var count = 0;
    $(".itemAmount").each(function(i) {
        var txt = $(this);
        if($.trim(txt.val()) != '') count += parseFloat(txt.val());
    });
    return count;
}

$(".itemAmount").live("blur", function() {
    $(this).val() != '' && $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 2));
    var num = Number($.trim($(this).val()));
    var returnNum = parseFloat($.trim($(this).next().val()));
    if(num > returnNum - 0.0001) {
        $(this).val() && $(this).val(returnNum);
    }
    setTotal();
}).live('change', function(){
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
        var num = $.trim($(this).val());
        var returnNum = parseFloat($.trim($(this).next().val()));
        if(num > returnNum - 0.0001) {
            $(this).val(returnNum);
        }
        setTotal();
    });

$("#total").live("blur", function() {
    if(!$(this).val()) {
        $(this).val(0);
    }
    $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 2));
}).live('keyup', function() {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));

    });

$(".itemPrice").live("change", function() {
    $(this).val() != '' && $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 2));
    setTotal();
});


function checkPrice() {
    var result = true;
    $(".itemPrice").each(function(index) {
        var num = $.trim($(this).val());
        var flag = checkNum(num);
        if(flag == false) {
            result = false;
            return;
        }
    });
    return result;
}

function checkNum(num) {
    var reg = /^(([1-9]+[0-9]*.{1}[0-9]+)|([0].{1}[1-9]+[0-9]*)|([1-9][0-9]*)|([0][.][0-9]+[1-9]*))$/;
    if(!reg.test(num)) {
        nsDialog.jAlert("请输入正确的价格或数量");
        return false;
    }
    return true;
}

function checkAmount() {
    var result = true;
    $(".itemAmount").each(function(index) {
        var num = $.trim($(this).val());
        var flag = checkNum(num);
        var returnNum = parseFloat($.trim($(this).next().next().val()));
        var price = parseFloat($.trim($(this).parent().prev().children().val()));
        if(num > returnNum) {
            $(this).val(returnNum);
            $(this).parent().next().next().children().val(returnNum * price);
            setTotal();
            return true;
        }
        if(flag == false) {
            result = false;
            return false;
        }
    });
    return result;
}

function getPistion(array, value) {
    var position;
    if(array.length <= 0) {
        position = 0;
    } else {
        for(i = 0; i < array.length; i++) {
            if(array[i] > value) {
                position = i;
                break;
            } else {
                position = array.length;
            }
        }
    }
    return position;
}


//TODO 校验单据的信息完整与正确性

function validateReturnStorageOrder() {
    var msg = "";
    var deleteRows = new Array();
    var length = 0;
    $(".item").each(function (i) {

        var idPrefix = $(this).children(1).children(1).attr("id").split(".")[0];
        var productName = $.trim($("#" + idPrefix + "\\.productName").val());
        var price = $("#" + idPrefix + "\\.price").val();
        a = $("#" + idPrefix + "\\.amount").val();
        var amount = parseFloat($("#" + idPrefix + "\\.amount").val());
        var unit = G.normalize($.trim($("#" + idPrefix + "\\.unit").val()));
        var brand = $("#" + idPrefix + "\\.brand").val();
        var spec = $("#" + idPrefix + "\\.spec").val();
        var model = $("#" + idPrefix + "\\.model").val();
        var vehicleBrand = $("#" + idPrefix + "\\.vehicleBrand").val();
        var vehicleModel = $("#" + idPrefix + "\\.vehicleModel").val();
        var inventoryAmount = parseFloat($("#" + idPrefix + "\\.inventoryAmount").val());
        var total = $("#" + idPrefix + "\\.total").val();

        var msgi = "第" + (i + 1) + "行";
        if(productName == '' && brand == '' && spec == '' && model == '' && vehicleBrand == '' && vehicleModel == '' && price == 0 && (amount == 0 || isNaN(amount)) && (inventoryAmount == 0 || isNaN(inventoryAmount)) && total == 0 && G.isEmpty(unit)) {
            //记录table中需要删除的行号
            deleteRows[length] = this.rowIndex;
            length++;
        } else {
            if(productName == "") {
                msgi += "，缺少品名";
            }
            if(isNaN(price) || price < 0) {
                msgi += "，退货价不正确";
            }
            if(isNaN(amount) || amount < 0.001) {
                msgi += "，退货数量不正确";
            }
            if(isNaN(inventoryAmount) || inventoryAmount < 0.001) {
                msgi += "，库存数量为0不能退货";
            }
            if(amount > inventoryAmount) {
                msgi += "，退货数大于库存量不能退货";
            }
            if (unit == "") {
                msgi += "，缺少单位";
            }

            if(msgi != "第" + (i + 1) + "行") {
                msg += msgi + "，请补充完整或确认。<br>"
            }
        }
    });
    if(deleteRows.length > 0) {
        for(j = 0; j < deleteRows.length; j++) {
            $("#table_productNo")[0].deleteRow(deleteRows[j] - j);
            trCount--;
        }
        isShowAddButton();
        setTotal();
    }



    if(trCount == 0) {
        nsDialog.jAlert("请填写退货明细！");
        returnStorageOrderAdd();
        return false;
    }

    if(msg != "") {

        if(deleteRows.length == 0) {
            nsDialog.jAlert(msg);
            return false;
        } else {
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

    }
    return true;
}


$(document).ready(function() {
    $(document).click(function(e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if(target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName") {
            $("#div_serviceName").hide();
        }
    });

    trCount = $(".item").size();
    $("#confirmReturnGoodsBtn").click(function() {

        if ($.trim($("#supplier").val()) == "") {
            nsDialog.jAlert("供应商名称不能为空！");
            $("#supplier").focus();
            return false;
        }
        if(checkSupplierInfo()==false){
            return;
        }
        var currentTimeMillis = (new Date()).getTime();
        var currentTimeStr = GLOBAL.Date.getCurrentFormatDate();
        var editDateMillis = Date.parse($("#orderVestDate").val());
        if(currentTimeStr != $("#orderVestDate").val()) {
            if(editDateMillis > currentTimeMillis) {
                nsDialog.jAlert("退货日期不能大于当前日期！");
                $("#editDateStr").focus();
                return false;
            }
        }
        if(!validateReturnStorageOrder()) {
            return false;
        }
        if(invoiceCommon.checkSameItemForOrder("item")) {
            nsDialog.jAlert("有重复退货内容，请确认！");
            return false;
        }
        if(invoiceCommon.checkSameCommodityCode("item")) {
            nsDialog.jAlert("商品编码有重复内容，请修改或删除。");
            return false;
        }
        $("#btnType").val('confirmReturnGoodsBtn');
        if(!checkStorehouseSelected()) {
            return;
        }
        if($("#supplier").val() && !$.trim($("#mobile").val()))
        {
            $("#inputMobile").dialog("open");
            return;
        }
        var bsArray=new Array();
        $(".warehouseList :checkbox[name=businessScope1]").each(function(){
            if($(this).attr("checked")) {
                bsArray.push($(this).val());
            }
        });
        $("#businessScope").val(bsArray.toString());

        nsDialog.jConfirm("确认退货?", null, function (returnVal) {
            $("#supplier").attr("disabled",false);
            if (returnVal) {
                bcgogo.checksession({
                    "parentWindow": window.parent,
                    'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
                    'src': 'goodsReturn.do?method=returnAccountDetail&supplierId=' + $("#supplierId").val()
                });
            }
        });
    });

    $("#saveDraftBtn").bind('click', function(event) {
        if(isLegalTxnDataLength() && openNewOrderPage()) {
            $("#saveDraftBtn").attr("disabled", true);
            var supplierDisabledStatus = $("#supplier").attr("disabled");
            $("#supplier").removeAttr("disabled");
            $("#purchaseReturnForm").ajaxSubmit({
                url: "draft.do?method=saveReturnStorageOrderDraft",
                dataType: "json",
                type: "POST",
                success: function(data) {
                    showMessage.fadeMessage("45%", "34%", "slow", 300, "草稿保存成功！" + getCurrentTime());
                    $("#draftOrderIdStr").val(data.idStr);
                    $("#saveDraftBtn").attr("disabled", false);
                    $("#supplier").attr("disabled", supplierDisabledStatus);
                    if(!G.isEmpty(data)){
                        $("#receiptNoSpan").text(data.receiptNo);
                        $("#receiptNo").val(data.receiptNo);
                        $("#print_div").show();
                    }
                },
                error: function() {
                    showMessage.fadeMessage("45%", "34%", "slow", 300, "草稿保存异常！" + getCurrentTime());
                    $("#saveDraftBtn").attr("disabled", false);
                    $("#supplier").attr("disabled", supplierDisabledStatus);
                }
            });
        }
    });
    $("#no").focus(function() {
        if($(this).val() == '可以自行填写，若不填则系统自动生成') {
            $(this).val('');
        }
    });
    $("#no").blur(function() {
        var no = $(this).val();
        no = $.trim(no);
        if(no == '') {
            $(this).val('可以自行填写，若不填则系统自动生成');
        } else {
            APP_BCGOGO.Net.syncAjax({
                url: "goodsReturn.do?method=getReturnStorageByPurchaseReturnNo",
                dataType: "json",
                data: {
                    no: no
                },
                success: function(json) {
                    if(json.no) {
                        nsDialog.jAlert("系统已经存在相同的入库退货单号，请重新填写！");
                    }
                }
            });
        }
    });

    //显示退货标签
    if($("#id").val() && $("#status").val() != 'SELLER_PENDING') {
        $("input:not(:button)").attr("disabled", "disabled");
        $(".searchCenter input").removeAttr("disabled");
        $("select").attr("disabled", "disabled");
        $("#resetBtn").attr("disabled", "disabled");
        //    $("#confirmReturnGoodsBtn").attr("disabled", "disabled");
        $("#saveDraftBtn").attr("disabled", "disabled");
        $("#editor").unbind();
        $("#print").removeAttr("disabled");
    }

    if($("#status").val() == 'SETTLED') {
        $(".tuihuo").show();
        $(".invalidImg").show();
        $(".copyInput_div").show();
    }

    if($("#status").val() == 'REPEAL') {
        $(".zuofei").css("left", "500px");
        $(".zuofei").css("top", "150px");
        $(".zuofei").show();
        $(".copyInput_div").show();
        $(".tuihuo").hide();
    }
    if($("#status").val() == 'SELLER_PENDING') {
        //        $(".pendingImg").css("left","220px");
        //        $(".pendingImg").css("top","210px");
        //        $(".pendingImg").show();
    }

    $("#copyInput").bind("click", function () {
        var orderId = $("#id").val();
        if (GLOBAL.Lang.isEmpty($("#id").val())) {
            nsDialog.jAlert("单据ID不存在，请刷新后重试");
            return false;
        }
        APP_BCGOGO.Net.syncPost({
            url:"goodsReturn.do?method=validateCopy",
            dataType:"json",
            data:{"purchaseReturnId" : $("#id").val()},
            success:function(result){
                if(result.success){
                    window.location.href = "goodsReturn.do?method=copyReturnStorage&purchaseReturnId=" + jQuery("#id").val();
                }else{
                    if(result.operation == 'ALERT'){
                        nsDialog.jAlert(result.msg, result.title);
                    }else if(result.operation == 'CONFIRM'){
                        nsDialog.jConfirm(result.msg, result.title, function(resultVal){
                            if(resultVal){
                                window.location.href = "goodsReturn.do?method=copyReturnStorage&purchaseReturnId=" + jQuery("#id").val();
                            }
                        });
                    }
                }
            },
            error:function(){
                nsDialog.jAlert("验证时产生异常，请重试！");
            }
        });
    });

    $("#nullifyBtn").bind("click", function () {
        repealOrder($("#id").val());
    });

    $("#cancelBtn").click(function() {
        if($("#id").val()) {
            location.reload();
        } else {
            window.location = "goodsReturn.do?method=createReturnStorage&cancle=noId&receiptNo=" + $("#receiptNo").val();
        }
    });

    //去打印页面
    $("#printBtn").click(function() {
        if($("#id").val()) {
            window.showModalDialog("goodsReturn.do?method=printReturnStorageOrder&purchaseReturnId=" + $("#id").val(), '', "dialogWidth=1024px;dialogHeight=768px");
            return;
        }
        if($("#draftOrderIdStr").val()) {
            window.showModalDialog("draft.do?method=getDraftOrderToPrint&id=" + $("#draftOrderIdStr").val() + "&type=RETURN&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
        }
    });

    if($("#id").val() && "true" == $("#print").val()) {
        $("#printBtn").click();
    }

    //检查手机号码
    $("#mobile").blur(function() {
        if(this.value) {
            var landline = document.getElementById("landline");
            var mobile = document.getElementById("mobile");
            if(mobile.value != "" && mobile.value != null) {
                check.inputSupplierMobileBlur2(mobile, landline);
            }
        }
    });

    $(".opera2").live("click",function() {
        //TODO 在旧单据中不允许新增一行
        if($("#id").val() && $("#status").val() != 'SELLER_PENDING') return;
        //检查最后一行是否完整.

        if($(".item:last").length<1) {
            return;
        }

        if(checkInfoIntegrity($('.item:last'))) {
            returnStorageOrderAdd();
        }
    });

    $("input[id$='.price']").live("blur", function() {
        if(this.value == 0) {
            nsDialog.jAlert(" 确定0元退货！");
        }
    });

    $("#duizhan").bind("click",function(){
        toCreateStatementOrder($("#supplierId").val(), "SUPPLIER_STATEMENT_ACCOUNT");
    });

    $("#inputMobile").dialog({
        autoOpen:false,
        resizable: false,
        title:"手机号码未填写，请填写手机号，方便以后联系沟通！",
        height:150,
        width:355,
        modal: true,
        closeOnEscape: false,
        open:function(){
            $("#divMobile").val("");
        },
        buttons:{
            "确定":function() {
                if($("#divMobile").val())
                {
                    //验证格式 NO -- 给出提醒
                    if(!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#divMobile").val()))
                    {
                        nsDialog.jAlert("手机号格式不正确");
                        return;
                    }
                    //验证同名 Y-- div影藏 赋值给父窗口mobile。blur

                    $("#mobile").val($("#divMobile").val());

                    var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getSupplierByMobile",data:{mobile: $("#divMobile").val()},dataType:"json"});
                    if(r && r.supplierIdStr)
                    {
                        if ($("#supplier").val() == r.supplier) {
                            $("#supplierId").val(r.supplierIdStr);
                        } else {
                            $("#inputMobile").dialog("close");
                            $("#mobile").blur();
                            return;
                        }
                    }

                    $("#inputMobile").dialog("close");
                    submitAfterInputMobile();

                    //
                }
                else
                {
                    $("#inputMobile").dialog("close");
                    submitAfterInputMobile();
                }

            },
            "取消":function(){
                $("#inputMobile").dialog("close");
                submitAfterInputMobile();
            }
        },
        close:function() {
            $("#divMobile").val("");
        }
    });
    if(!G.isEmpty($("#originOrderId").val())){
        $("#supplier").attr("disabled","disabled");
    }
    if(verifyProductThroughOrderVersion(getOrderType())){
        $(".item").each(function(){
            var $productId=$(this).find("[id$='.productId']");
            if(!G.isEmpty($productId.val())){
                var idPrefix=$productId.attr("id").split(".")[0];
                initAndbindSupplierInventoryList(idPrefix);
            }
        });
        $("[id$='_supplierInfo'] .useRelatedAmount").blur();
    }
    setTotal();
});

//目前只有库存信息要更新
function updateProductInventory(data,isShowMsg) {
    var lackMsg = "";
    $("#table_productNo").find(".item").each(function() {
        var productId = $(this).find("input[id$='.productId']").val();
        if(!GLOBAL.Lang.isEmpty(productId) && !GLOBAL.Lang.isEmpty(data[productId])) {
            if(APP_BCGOGO.Permission.Version.StoreHouse){
                if(G.Lang.isEmpty($("#storehouseId").val()) || $("#storehouseId").val()==$("#oldStorehouseId").val()){
                    $(this).find("input[id$='.inventoryAmount']").val(G.normalize(data[productId],"0")*1+$(this).find("input[id$='.reserved']").val()*1);
                }else{
                    $(this).find("input[id$='.inventoryAmount']").val(G.normalize(data[productId],"0"));
                }
            }else{
                $(this).find("input[id$='.inventoryAmount']").val(G.normalize(data[productId],"0")*1+$(this).find("input[id$='.reserved']").val()*1);
            }

            if(Number($(this).find("input[id$='.inventoryAmount']").val()) - Number($(this).find("input[id$='.amount']").val()) < 0) {
                lackMsg = lackMsg + "第" + ($(this).index($("#table_productNo").find(".item"))+1) + "行商品库存不足，无法退货<br>";
            }
        }
    });
    if (isShowMsg && !GLOBAL.Lang.isEmpty(lackMsg)) {
        nsDialog.jAlert(lackMsg);
    }
}

function newOtherOrder(url) {
    if(openNewOrderPage()) {
        window.open(url, "_blank");
    } else {
        openOrAssign(url);
    }
}

function newReturnStorageOrder() {
    if(openNewOrderPage()) {
        window.open($("#basePath").val() + "goodsReturn.do?method=createReturnStorage", "_blank");
    }
}
if(!isDisable()) {
    $("#editor").live("click", function(event) {
        var obj = this;
        $.ajax({
            type: "POST",
            url: "member.do?method=getSaleMans",
            async: true,
            data: {
                now: new Date()
            },
            cache: false,
            dataType: "json",
            error: function(XMLHttpRequest, error, errorThrown) {
                $("#div_serviceName").css({
                    'display': 'none'
                });
            },
            success: function(jsonObject) {
                initSaleMan(obj, jsonObject);
            }
        });
    });
}


function initSaleMan(domObject, jsonObject) {
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();
    var domTitle = domObject.name;
    //  var position = domObject.getBoundingClientRect();
    var position = getElementPos(domObject.id);
    //  var x = position.left;
    //  var y = position.top;
    var x = position.x;
    var y = position.y;
    var selectmore = jsonObject.length;
    if(selectmore <= 0) {
        $("#div_serviceName").css({
            "display": "none"
        });
    } else {
        $("#div_serviceName").css({
            "display": "block",
            "position": "absolute",
            "left": x + "px",
            "top": y + offsetHeight + "px",
            "width": "225px",
            "overflowY": "scroll",
            "overflowX": "hidden"
        });
        $("#Scroller-Container_ServiceName").html("");

        for(var i = 0; i < jsonObject.length; i++) {
            var id = jsonObject[i].idStr;
            var a = $("<a id=" + id + "></a>");
            a.css("width", "228px")
            a.css("color", "#000").html(jsonObject[i].name + "<br>").bind("mouseenter", function() {
                $("#Scroller-Container_ServiceName > a").removeAttr("class");
                $(this).attr("class", "hover");
                selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name;
                selectItemNum = parseInt(this.id.substring(10));
            }).click(function() {
                    $(domObject).val(selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name);
                    $("#editorId").val(jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].idStr);
                    selectItemNum = -1;
                    $("#div_serviceName").hide();
                });
            $("#Scroller-Container_ServiceName").append(a);
        }
    }
}

function getElementPos(elementId) {
    var ua = navigator.userAgent.toLowerCase();
    var isOpera = (ua.indexOf('opera') != -1);
    var isIE = (ua.indexOf('msie') != -1 && !isOpera); // not opera spoof
    var el = document.getElementById(elementId);
    if(el.parentNode === null || el.style.display == 'none') {
        return false;
    }
    var parent = null;
    var pos = [];
    var box;
    if(el.getBoundingClientRect) //IE
    {
        box = el.getBoundingClientRect();
        var scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
        var scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
        return {
            x: box.left + scrollLeft,
            y: box.top + scrollTop
        };
    } else if(document.getBoxObjectFor) // gecko
    {
        box = document.getBoxObjectFor(el);
        var borderLeft = (el.style.borderLeftWidth) ? parseInt(el.style.borderLeftWidth) : 0;
        var borderTop = (el.style.borderTopWidth) ? parseInt(el.style.borderTopWidth) : 0;
        pos = [box.x - borderLeft, box.y - borderTop];
    } else    // safari & opera
    {
        pos = [el.offsetLeft, el.offsetTop];
        parent = el.offsetParent;
        if(parent != el) {
            while(parent) {
                pos[0] += parent.offsetLeft;
                pos[1] += parent.offsetTop;
                parent = parent.offsetParent;
            }
        }
        if(ua.indexOf('opera') != -1 || (ua.indexOf('safari') != -1 && el.style.position == 'absolute')) {
            pos[0] -= document.body.offsetLeft;
            pos[1] -= document.body.offsetTop;
        }
    }
    if(el.parentNode) {
        parent = el.parentNode;
    } else {
        parent = null;
    }
    while(parent && parent.tagName != 'BODY' && parent.tagName != 'HTML') { // account for any scrolled ancestors
        pos[0] -= parent.scrollLeft;
        pos[1] -= parent.scrollTop;
        if(parent.parentNode) {
            parent = parent.parentNode;
        } else {
            parent = null;
        }
    }
    return {
        x: pos[0],
        y: pos[1]
    };
}
function repealOrder(orderId){
    if(APP_BCGOGO.Permission.Version.StoreHouse && $("#status").val()=="PENDING"){//这个时候作废才会操作库存
        if (!validatorStoreHouseOrderRepeal($("#_toStorehouseId").val(), getOrderType(), orderId)) {
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

function submitAfterInputMobile()
{   initBusinessScope();
    nsDialog.jConfirm("确认退货?", null, function (returnVal) {
        if (returnVal) {

            $("#supplier").removeAttr("disabled");
            var supplierDisabledStatus = $("#supplier").attr("disabled");
            bcgogo.checksession({
                "parentWindow": window.parent,
                'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
                'src': 'goodsReturn.do?method=returnAccountDetail&supplierId=' + $("#supplierId").val()
            });
        }
    });
}

function initDuiZhanInfo()
{
    if(!$("#receivable").html() || $("#receivable").html()*1 == 0 )
    {
        $("#receivableDiv").css("display","none");
    }
    else
    {
        $("#receivableDiv").css("display","inline");
    }

    if(!$("#payable").html() || $("#payable").html()*1 == 0 )
    {
        $("#payableDiv").css("display","none");
    }
    else
    {
        $("#payableDiv").css("display","inline");
    }

    if($("#receivableDiv").css("display") == "none" && $("#payableDiv").css("display") == "none")
    {
        $("#duizhan").hide();
    }
    else
    {
        $("#duizhan").show();
    }
}

$(function(){
    $('#itemDTOs0\\.price').val() == '0' && $('#itemDTOs0\\.price').val('');
    $('#itemDTOs0\\.inventoryAmount').val() == '0' && $('#itemDTOs0\\.inventoryAmount').val('');
});
