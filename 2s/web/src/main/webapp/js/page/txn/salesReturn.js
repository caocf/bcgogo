var message;
var invoiceCommon = APP_BCGOGO.Module.wjl.invoiceCommon;

$(document).ready(function () {
    $("#customerInfo").hover(function(){
        $(this).css("color","#fd5300");
    },function(){
        $(this).css("color","#6699CC");
    });
    initDuiZhanInfo();
    $(".itemAmount,.itemPrice").bind("change",function () {
            var format = $(this).val();
            format = dataTransition.rounding(format, 2);
            $(this).val() && $(this).val(format);
            setTotal();
        });

    if ($.trim($("#customer").val()) && $("#readOnly").val() != "false") {
        $("#customer").attr("disabled", true);
    }

    $(".itemAmount,.itemTotal").bind("blur", function () {
        dataTransition.roundingSpanNumber("totalSpan");
        var format = $(this).val();
        format = dataTransition.rounding(format, 2);
        $(this).val() && $(this).val(format);
    });
    $("#table_productNo input").bind("mouseover", function () {
        this.title = this.value;
    });

    $("#cancelBtn").click(function () {
        if ($("#id").val()) {
            window.location = "salesReturn.do?method=createSalesReturn";
        }
        else {
            var urlParams = window.location;
            window.location = urlParams + "&receiptNo=" + $("#receiptNo").val() + "&isFromDraft=true";
        }
    });


//    $(".itemUnitSpan").live("click", function () {
//        if ($(this).text() != undefined && $(this).text() != "" && $(this).text() != null) {
//            var unitIdPrefix = $(this).attr("id");
//            unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
//
//            var storageUnitVal = $("#" + unitIdPrefix + "\\.storageUnit").val();
//            var sellUnitVal = $("#" + unitIdPrefix + "\\.sellUnit").val();
//            var rateVal = $("#" + unitIdPrefix + "\\.rate").val() * 1;
//            if ($(this).text() == sellUnitVal) {
//                $(this).text(storageUnitVal);
//                $("#" + unitIdPrefix + "\\.unit").val(storageUnitVal)
//            } else {
//                $(this).text(sellUnitVal);
//                $("#" + unitIdPrefix + "\\.unit").val(sellUnitVal)
//            }
//            setTotal();
//        }
//    });
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
        if (target.className != "opera1") return;
        if ($("#id").val()) return false;

        if ($(".item").size() <= 1 && $("#originOrderId").val()) {
            nsDialog.jAlert("退货商品不能全部删除");
            return false;
        }
        var idPrefix = $(target).attr("id").split(".")[0];
        $("#"+idPrefix+"_supplierInfo").remove();
        $(target).closest("tr").remove();
        $("#" + idPrefix + "\\.upperLimit").closest("tr").remove();
        isShowAddButton();
        setTotal();
//        tableUtil.setRowBackgroundColor("#table_productNo", null, ".table_title,.s_tabelBorder", 'even');
    });

    //增加行          //TODO 加号按钮单击触发的脚本
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "opera2")  return;
        //TODO 在旧单据中不允许新增一行
        if ($("#id").val()) return false;

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
        salesReturnAdd();
    });
    $("#salesReturner").live("click", function () {
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
                error: function (XMLHttpRequest, error, errorThrown) {
                    $("#div_serviceName").css({'display': 'none'});
                },
                success: function (jsonObject) {
                    initSaleMan(obj, jsonObject);
                }
            }
        );
    });

    $("#mobile").blur(function() {
        if (this.value)
            check.inputCustomerMobileBlur2(this,$("#landline")[0]);
    });
    $("#customerInfo").bind("click", function () {
//看品名的下拉框有没有显示，如果显示则进行隐藏
        if ($("#div_brand")[0]) {
            $("#div_brand").hide();
        }
        bcgogo.checksession({"parentWindow": window.parent, 'iframe_PopupBox': $("#iframe_PopupBox")[0], 'src': "txn.do?method=clientInfo&customer="
            + encodeURIComponent($("#customer").val()) + "&mobile=" + $("#mobile").val() + "&landLine=" + $("#landline").val() + "&hiddenMobile=" + $("#hiddenMobile").val()
            + "&customerId=" + $("#customerId").val() + "&contact=" + encodeURIComponent($("#contact").val())});
    });

    $("#deleteSalesReturner").hide();

    $("#deleteSalesReturner").live("click", function () {
        $("#salesReturner").val("");
        $("#salesReturnerId").val("");
        $("#deleteSalesReturner").hide();
    });

    $("#salesReturnerDiv").mouseenter(function () {
        if ($("#salesReturner").val() && !isDisable()) {
            $("#deleteSalesReturner").show();
        }
    });

    $("#salesReturnerDiv").mouseleave(function () {
        $("#deleteSalesReturner").hide();
    });


    $("#saleReturnBtn").click(function () {    //TODO “结算”按钮被单击后的处理脚本

        if (!$.trim($("#customer").val())) {
            showMessage.fadeMessage("35%", "40%", "slow", 2000, "该客户不存在,不能退货");
            return;
        }
        if ($("#customerId").val()) {

            var r = APP_BCGOGO.Net.syncGet({async: false, url: "customer.do?method=checkCustomerStatus", data: {customerId: $("#customerId").val(), now: new Date()}, dataType: "json"});

            if (!r.success) {
                nsDialog.jAlert("此客户已被删除或合并，不能做单，请更改客户！");
                return;
            }
        }
        if ($("#customer").val() != "**客户**" && $("#mobile").attr("mustInputBySameCustomer") && G.Lang.isEmpty($.trim($("#mobile").val()))) {
            nsDialog.jAlert("当前客户存在同名客户，请填写手机号，或者修改客户名加以区分");
            return;
        }

        if($.trim($("#mobile").val()) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#mobile").val())) {
            nsDialog.jAlert("手机号码输入不规范，请重新输入","",function(){
                $("#mobile").focus();
            });
            return;
        }

        //在此之前判断手机号是否正确
        if ($("#customer").val() && !$.trim($("#mobile").val()) && APP_BCGOGO.Permission.Version.OrderMobileRemind) {
            $("#inputMobile").dialog("open");
            return;
        }

        if (validateSalesReturnInfo().validateResult) {

            $("#customerStr").val($("#customer").val());
            $("#salesReturnForm").ajaxSubmit({
                url: "salesReturn.do?method=validateSalesReturnBeforeSettle",
                dataType: "json",
                success: function (json) {
                    if (json.success) {
                        bcgogo.checksession({parentWindow: window.parent, 'iframe_PopupBox': $("#iframe_PopupBox_account")[0], 'src': 'salesReturn.do?method=accountDetail&settleType=normal&customerId=' + $("#customerId").val() + '&salesReturnId=' + $("#salesReturnForm #id").val() + '&total=' + $("#salesReturnForm #total").val()});
                    } else if (!json.success && json.operation == "confirm_deleted_product") {
                        nsDialog.jConfirm(json.msg, "提示", function (returnVal) {
                            if (returnVal) {
                                bcgogo.checksession({parentWindow: window.parent, 'iframe_PopupBox': $("#iframe_PopupBox_account")[0], 'src': 'salesReturn.do?method=accountDetail&settleType=normal&customerId=' + $("#customerId").val() + '&salesReturnId=' + $("#salesReturnForm #id").val() + '&total=' + $("#salesReturnForm #total").val()});
                            }
                        })
                    } else {
                        nsDialog.jAlert(json.msg);
                    }
                },
                error: function () {
                    nsDialog.jAlert("出现异常，结算失败！");
                }
            });
        }
    });
//    tableUtil.tableStyle("#table_productNo", ".table_title,.s_tabelBorder");
    if ($(".item").size() == 0) {
        salesReturnAdd();
    }
    $("#duizhan").bind("click",function(){

        toCreateStatementOrder($("#customerId").val(), "CUSTOMER_STATEMENT_ACCOUNT");
    });
    $("#duizhanFinish").bind("click",function(){

        toCreateStatementOrder($("#customerId").html(), "CUSTOMER_STATEMENT_ACCOUNT");
    });

    $("#saveDraftBtn").bind('click', function (event) {
        $("#customerStr").val($("#customer").val());
        if (isLegalTxnDataLength() && openNewOrderPage()) {
            $("#saveDraftBtn").attr("disabled", true);
            $("#salesReturnForm").ajaxSubmit({
                url: "draft.do?method=saveSalesReturnDraft",
                dataType: "json",
                type: "POST",
                success: function (data) {
                    showMessage.fadeMessage("45%", "34%", "slow", 300, "草稿保存成功！  " + getCurrentTime());
                    $("#draftOrderIdStr").val(data.idStr);
                    $("#saveDraftBtn").attr("disabled", false);
                    if(!G.isEmpty(data)){
                        $("#receiptNoSpan").text(data.receiptNo);
                        $("#receiptNo").val(data.receiptNo);
//                            $("#print_div").show();
                    }
                },
                error: function () {
                    showMessage.fadeMessage("45%", "34%", "slow", 300, "保存草稿异常！  " + getCurrentTime());
                    $("#saveDraftBtn").attr("disabled", false);
                }
            });
        }
    });
    //从草稿箱带出可编辑的退货单默认添加一行
    var $productId = $("#itemDTOs0" + "\\.productId");
    if ($productId.size() != 0 && $productId.val() && $("#readOnly").val() == "false") {
        salesReturnAdd();
    }

    $("#inputMobile").dialog({
        autoOpen:false,
        resizable: false,
        title:"手机号码未填写，请填写手机号，方便以后联系沟通！",
        height:150,
        width:355,
        modal: true,
        closeOnEscape: false,
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
                    var r = APP_BCGOGO.Net.syncGet({
                        url:"customer.do?method=getCustomerJsonDataByMobile",
                        data:{mobile: $("#divMobile").val(), customerId: $("#customerId").val()},
                        dataType:"json"
                    });
                    if(r.success && r.data)
                    {
                        var obj = r.data;
                        if (obj.msg == 'customer' && $("#customer").val() == obj.name) {
                            $("#customerId").val(obj.idStr);
                        } else if(obj.msg == 'customer') {
                            $("#inputMobile").dialog("close");
                            $("#mobile").blur();
                            return;
                        }else if(obj.msg == 'supplier'){
                            $("#divMobile").val("");
                            nsDialog.jAlert("填写的手机号与已存在供应商【"+obj.name + "】的手机号相同，请重新输入！");
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
    setTotal();

    if(verifyProductThroughOrderVersion(getOrderType())){
        $(".item").each(function(){
//            $(this).find(".itemAmount ").val(0);
            var $productId=$(this).find("[id$='.productId']");
            if(!G.isEmpty($productId.val())){
                var idPrefix=$productId.attr("id").split(".")[0];
                initAndbindSupplierInventoryList(idPrefix);
            }
        });
        $("[id$='_supplierInfo'] .useRelatedAmount").blur();
    }
});

function newSaleReturnOrder() {
    if (openNewOrderPage()) {
        window.open($("#basePath").val() + "salesReturn.do?method=createSalesReturn", "_blank");
    }
}


//检查单据是否相同
function checkTheSame() {
    return invoiceCommon.checkSameItemForOrder("item");
}

//单据结算前进行校验
function validateSalesReturnInfo() {

    var $last_tr = $("#table_productNo").find("tbody").find("tr:last");
    if ($(".item").size() <= 1 && checkEmptyRow($last_tr)) {
        nsDialog.jAlert("请选择商品进行退货");
        return {validateResult: false};
    }

    //自动删除最后的空白行
    while ($last_tr.index() >= 3 && checkEmptyRow($last_tr)) {
        $last_tr.find("[a[id$='.deletebutton']").click();
        $last_tr = $("#table_productNo .item:last");
    }
    //
    if (checkTheSame()) {
        showMessage.fadeMessage("35%", "40%", "slow", 2000, "单据有重复内容，请修改或删除。");
        return {validateResult: false};

    }
    if (invoiceCommon.checkSameCommodityCode("item")) {
        showMessage.fadeMessage("35%", "40%", "slow", 2000, "商品编码有重复内容，请修改或删除。");
        return {validateResult: false};
    }

    //验证采购量大于0
    $(".itemPrice").each(function () {
        if (!$(this).val()) {
            $(this).val(0)
        }
    });

    //验证数量不为0
    var amountMsg = "";
    $(".itemAmount").each(function () {
        if (!$(this).val()) {
            $(this).val(0)
        }
        if (parseFloat($.trim($(this).val())) * 1 == 0) {
            amountMsg = amountMsg + "\n第" + ($(this).index(".itemAmount") + 1) + "行商品数量为0，此行内容无意义，请补充或删除";
        }

    });
    if (amountMsg != "") {
        showMessage.fadeMessage("35%", "40%", "slow", 2000, amountMsg);
        return {validateResult: false};
    }

    //验证单位
    var unitMsg = "";

    $("input[name$='.unit']").each(function() {
        if (!$.trim($(this).val())) {
            flag = 0;
            var unitIndex = $(this).index("input[name$='.unit']") + 1;

            if (unitMsg == "") {
                unitMsg = unitMsg + "第" + unitIndex + "行请输入商品的单位";
            } else {
                unitMsg = unitMsg + "<br>第" + unitIndex + "行请输入商品的单位";
            }
        }
    });
    if (unitMsg != "") {
        nsDialog.jAlert(unitMsg);
        return {validateResult: false};
    }


    $(".itemAmountHid").each(function () {
        invoiceCommon.reSetDomVal(this, "", 0);
    });
    if ($.trim($("#mobile").val())) {
        var contract = document.getElementById("mobile");
        check.saveContact(contract);
    }

    var str = "";
    var msg = "";
    $(".itemPrice").each(function (i) {

        if (parseFloat($.trim($(this).val())) * 1 == 0) {
            str = str + "\n第" + ($(this).index(".itemPrice") + 1) + "行商品退货价为0，是否确认退货";
        }

        var idPrefix = $(this).attr("id").split(".")[0];
        var productName = $("#" + idPrefix + "\\.productName").val();
        if (productName == "") {
            msg = "第" + (i + 1) + "行，缺少品名";
        }
        if ($("#originOrderId").val()) {
            var originSaleTotal = parseFloat($("#" + idPrefix + "\\.originSaleTotal").val());
            var total = $("#" + idPrefix + "\\.totalSpan").text();
            if (parseFloat(total) > originSaleTotal) {
                str = str + "\n第" + ($(this).index(".itemPrice") + 1) + "行商品退货总额高于销售总额，是否确认退货";
            }
        }
    });
    if (msg != "") {
        msg += "，请补充完整。<br>";
        nsDialog.jAlert(msg);
        return {validateResult: false};
    }
    if (str != "") {
        if (!confirm(str)) {
            return {validateResult: false};
        }
    }
    setTotal();
    $("#btnType").val('saleReturnBtn');
    if(!checkStorehouseSelected()) {
        return {validateResult: false};
    }
    return {validateResult: true};
}


function initSaleMan(domObject, jsonObject) {
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();
    var domTitle = domObject.name;
    var position = domObject.getBoundingClientRect();
    var x = position.left;
    var y = position.top;
    var selectmore = jsonObject.length;
    if (selectmore <= 0) {
        $("#div_serviceName").css({'display': 'none'});
    }
    else {
        $("#div_serviceName").css({
            'display': 'block', 'position': 'absolute',
            'left': x + 'px',
            'top': y + offsetHeight + 4 + 'px',
            'width': '100px',
            overflowY: "scroll",
            overflowX: "hidden"
        });

        $("#Scroller-Container_ServiceName").html("");

        for (var i = 0; i < jsonObject.length; i++) {
            var id = jsonObject[i].idStr;
            var a = $("<a id=" + id + "></a>");
            $(a).css("color", "#000");
            a.html(jsonObject[i].name + "<br>");
            $(a).bind("mouseover", function () {
                $("#Scroller-Container_ServiceName > a").removeAttr("class");
                $(this).attr("class", "hover");
                selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name;// $(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            $(a).click(function () {
                var sty = this.id;

                $("#salesReturnerId").val(sty);
                $("#editorId").val(sty);
                $(domObject).val(selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name); //取的第一字符串

                selectItemNum = -1;
            });

            $("#Scroller-Container_ServiceName").append(a);
        }
    }
}

function newOtherOrder(url) {
    if (openNewOrderPage()) {
        window.open(url, "_blank");
    } else {
        openOrAssign(url);
    }
}

function getSalesReturnTrSample() {
    var trSample = '<tr class="bg item table-row-original">' +
        '<td style="border-left:none;padding-left:10px;">' +
        '   <input id="itemDTOs0.commodityCode" name="itemDTOs[0].commodityCode" type="text" class="table_input checkStringEmpty" value="" maxlength="20"/>' +
        '</td>' +
        '<td>' +
        '	  <input id="itemDTOs0.id" name="itemDTOs[0].id" type="hidden" value=""/>' +
        '   <input id="itemDTOs0.productId" name="itemDTOs[0].productId" value="" type="hidden"/>' +
        '	  <input id="itemDTOs0.purchasePrice" name="itemDTOs[0].purchasePrice" value="" type="hidden"/>' +
        '	  <input id="itemDTOs0.productName" name="itemDTOs[0].productName"  class="table_input checkStringEmpty" value="" type="text" style="width:80%"/>' +

//
//        '    <input id="itemDTOs0.productVehicleStatus" name="itemDTOs[0].productVehicleStatus" value="" type="hidden" class="itemProductVehicleStatus"/>' +
//        '    <input id="itemDTOs0.hidden_productVehicleStatus" name="itemDTOs[0].hidden_productVehicleStatus" value="" type="hidden" class="itemProductVehicleStatus"/>' +
//        '    <input id="itemDTOs0.isOldProduct" name="itemDTOs[0].isOldProduct" type="hidden" value=""/>' +
//        '    <input id="itemDTOs0.vehicleBrandId" name="itemDTOs[0].vehicleBrandId" type="hidden" value=""/>' +
//        '    <input id="itemDTOs0.vehicleModelId" name="itemDTOs[0].vehicleModelId" type="hidden" value=""/>' +
//        '    <input id="itemDTOs0.vehicleYearId" name="itemDTOs[0].vehicleYearId" type="hidden" value=""/>' +
//        '    <input id="itemDTOs0.vehicleEngineId" name="itemDTOs[0].vehicleEngineId" type="hidden" value=""/>' +
//        '	<input id="itemDTOs0.productType" name="itemDTOs[0].productType" type="hidden" value=""/>' +
//        '	<input id="itemDTOs0.purchasePrice" name="itemDTOs[0].purchasePrice" value="" type="hidden"/>' +
//        '	<input id="itemDTOs0.productName" name="itemDTOs[0].productName"  class="table_input checkStringEmpty" value="" type="text" style="width:80%"/>' +
//        '<input type="button" class="edit1" onfocus="this.blur();" id="itemDTOs0.editbutton" name="itemDTOs[0].editbutton" onclick="searchInventoryIndex(this)" style="margin-left: 6px"/>' +
        '</td>' +
        '<td>' +
        '	  <input id="itemDTOs0.brand" name="itemDTOs[0].brand" maxlength="100" class="table_input checkStringEmpty" value="" type="text"/>' +
        '</td>' +
        '<td>' +
        '   <input id="itemDTOs0.spec" name="itemDTOs[0].spec"  class="table_input checkStringEmpty" value="" type="text"/>' +
        '</td>' +
        '<td>' +
        '   <input id="itemDTOs0.model" name="itemDTOs[0].model"  class="table_input checkStringEmpty" value="" type="text"/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel" maxlength="50"  value="" class="itemVehicleModel table_input checkStringEmpty"  type="text"/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand" maxlength="50"  value="" class="itemVehicleBrand table_input checkStringEmpty"  type="text"/>' +
        '</td>' +
        '<td>' +
        '<input type="hidden"  name="itemDTOs0.originSalesPrice" id="itemDTOs0.originSalesPrice"/> ' +
        '<span id="itemDTOs0.originSalesPriceSpan" name="itemDTOs[0].originSalesPriceSpan"></span> ' +
        '</td>' +
        '<td style="display: none">' +
        '</td>' +
        '<td style="color:#FF6700;">' +
        '	  <input id="itemDTOs0.price"  name="itemDTOs[0].price" onblur="checkPrice(this)"  value="" class="itemPrice table_input checkNumberEmpty" type="text" data-filter-zero="true"/>' +
        '</td>' +
        '<td style="color:#FF0000;">' +
        '   <input id="itemDTOs0.amount"  name="itemDTOs[0].amount"  value="" class="itemAmount table_input checkNumberEmpty" type="text" data-filter-zero="true"/>' +
        '</td>' +
//        '<td>' +
//        '   <span id="itemDTOs0.saleReturnUnitSpan" name="itemDTOs[0].saleReturnUnitSpan" class="itemUnitSpan"></span> ' +
//        '   <input id="itemDTOs0.unit" name="itemDTOs[0].unit"  value="" class="itemUnit table_input checkStringEmpty" type="hidden"/>' +
//        '   <input type="hidden" id="itemDTOs0.storageUnit" name="itemDTOs[0].storageUnit" value="" class="itemStorageUnit table_input"/>' +
//        '   <input type="hidden" id="itemDTOs0.sellUnit" name="itemDTOs[0].sellUnit" value="" class="itemSellUnit table_input"/>' +
//        '   <input type="hidden" id="itemDTOs0.rate" name="itemDTOs[0].rate" value="" class="itemRate table_input"/>' +
//        '</td>' +

        '<td>' +
        '<input id="itemDTOs0.unit" name="itemDTOs[0].unit"  value="" class="itemUnit table_input checkStringEmpty" style="width:20px;"  type="text"/>' +
        '<input type="hidden" id="itemDTOs0.storageUnit" name="itemDTOs[0].storageUnit" value="" class="itemStorageUnit table_input"/>' +
        '<input type="hidden" id="itemDTOs0.sellUnit" name="itemDTOs[0].sellUnit" value="" class="itemSellUnit table_input"/>' +
        '<input type="hidden" id="itemDTOs0.rate" name="itemDTOs[0].rate" value="" class="itemRate table_input"/>' +
        '</td>' +

        '<td>' +
        '   <span id="itemDTOs0.totalSpan" name="itemDTOs[0].totalSpan" class="itemTotalSpan"></span>' +
        '   <input id="itemDTOs0.total" name="itemDTOs[0].total"  value="" class="itemTotal" type="hidden"/>' +
        '</td>' +
        '    <td style="display:none">' +
        '	<input id="itemDTOs0.vehicleYear" name="itemDTOs[0].vehicleYear"  value="" class="itemVehicleYear table_input"  type="text"/>' +
        '    <input type="hidden" id="itemDTOs0.hidden_vehicleYear" name="itemDTOs[0].hidden_vehicleYear" /></td>' +
        '    <td style="display:none">' +
        '	<input id="itemDTOs0.vehicleEngine" name="itemDTOs[0].vehicleEngine"  value="" class="itemVehicleEngine table_input"  type="text"/>' +
        '    <input type="hidden" id="itemDTOs0.hidden_vehicleEngine" name="itemDTOs[0].hidden_vehicleEngine" /></td>' +
        '   <td style="display: none">' +
        '   </td>' +
        '    <td style="border-right:1px solid #BBBBBB;">' +
        '	<a class="opera1" id="itemDTOs0.deletebutton" name="itemDTOs[0].deletebutton">删除</a>' +
        '    </td>' +
        '</tr>';
    return trSample;
}

function salesReturnAdd() {
    var tr = $(getSalesReturnTrSample()).clone();     //TODO 克隆模版，初始化所有的INPUT
    $(tr).find("input").val("");
    $(tr).find(".itemAmount,.itemPrice").bind("blur",function () {
        var format = $(this).val();
        format = dataTransition.rounding(format, 2);
        $(this).val() && $(this).val(format);
        setTotal();
    }).bind("change", function () {
            setTotal();
        });
    $(tr).find(".itemProductVehicleStatus").val("1");
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
    $(tr).appendTo("#table_productNo");
//    tableUtil.tableStyle("#table_productNo", ".table_title,.s_tabelBorder", 'even');
    isShowAddButton();
    return $(tr);
}
//判断是否显示+按钮
function isShowAddButton() {
    //如果初始化的话就默认加一行
    if ($(".item").size() <= 0) {
        salesReturnAdd();
    }
    $(".item .opera2").remove();
    var opera1Id = $(".item:last").find("td:last>a[class='opera1']").attr("id");
    if (!opera1Id) return;
    if (!$("#originOrderId").val()) {
        $(".item:last").find("td:last>a[class='opera1']").after(' <a class="opera2" ' +
            ' id="itemDTOs' + (opera1Id.split(".")[0].substring(8)) + '.plusbutton"' +
            'name="itemDTOs[' + (opera1Id.split(".")[0].substring(8)) + '].plusbutton" ' + '>增加</a>');
    }

}

//TODO 如果所填单价低于采购价，需要弹出提示
function checkPrice(domObj) {
    var domid = domObj.id;
    var domValue = parseFloat(domObj.value);
    var idPrefix = domid.split(".")[0];
    var newValue = $("#" + idPrefix + "\\.originSalesPriceSpan").text();

    $(domObj).val() && $(domObj).val(dataTransition.rounding($(domObj).val(), 2));
    domValue = parseFloat(domObj.value);
    if (domValue > newValue) {
        if (!confirm("该商品的退货价高于销售价" + dataTransition.rounding(newValue, 2) + "元，是否确认退货?")) {
            invoiceCommon.selectAndFocusByNode($(this));
            return false;
        }
    }
    setTotal();
}

//TODO 将“总计”加入页面元素
function setTotal() {
    var totalCount = 0;
    $(".itemPrice").each(function (i) {
        var price = $(this).val();
        if (!price) {
            price = "0";
        }
        var idPrefix = $(this).attr("id").split(".")[0];
        if ($.trim(price)) {
            price = APP_BCGOGO.StringFilter.inputtingPriceFilter(price, 2);
            $(".itemPrice").eq(i).val() && $(".itemPrice").eq(i).val(price);
        }
        var amount = APP_BCGOGO.StringFilter.inputtingPriceFilter($("#" + idPrefix + "\\.amount").val(), 2);
        $("#" + idPrefix + "\\.amount").val() && $("#" + idPrefix + "\\.amount").val(amount);
        var count = parseFloat(price * amount);
        count = dataTransition.rounding(count, 2);
        $("#" + idPrefix + "\\.total").val(count);
        $("#" + idPrefix + "\\.totalSpan").text(count ? count : '');
        $("#" + idPrefix + "\\.totalSpan").attr("title", count);
        totalCount += count;
        htmlNumberFilter($("#" + idPrefix + "\\.price").add("#" + idPrefix + "\\.amount").add("#" + idPrefix + "\\.totalSpan").add("#" + idPrefix + "\\.originSalesPriceSpan"), true);
    });
    totalCount = dataTransition.rounding(totalCount, 2);
    $("#totalSpan").text(totalCount);
    $("#total").val(totalCount);
}


function getCursorPosition(ctrl) {//获取光标位置函数
    var CaretPos = 0;
    // IE Support
    if (ctrl.type != "text") {
        return;
    }
    if (document.selection) {
        ctrl.focus();
        var Sel = document.selection.createRange();
        Sel.moveStart('character', -ctrl.value.length);
        CaretPos = Sel.text.length;
    }
    // Firefox support
    else if (ctrl.selectionStart != NaN || ctrl.selectionStart == '0')
        CaretPos = ctrl.selectionStart;
    return (CaretPos);
}

function setCursorPosition(ctrl, pos) {//设置光标位置函数
    if (ctrl.type != "text") {
        return;
    }
    if (ctrl.setSelectionRange) {
        ctrl.focus();
        ctrl.setSelectionRange(pos, pos);
    }
    else if (ctrl.createTextRange) {
        var range = ctrl.createTextRange();
        range.collapse(true);
        range.moveEnd('character', pos);
        range.moveStart('character', pos);
        range.select();
    }
}


//override unit.jsp   funciton initUnitTd()
//function initUnitTd(dom) {
//    var unitIdPrefix = $(dom).attr("id");
//    unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
//    if ($("#" + unitIdPrefix + "\\.sellUnit").val() != '') {
//        $("#" + unitIdPrefix + "\\.saleReturnUnitSpan").text($("#" + unitIdPrefix + "\\.sellUnit").val());
//    }
//}

function submitAfterInputMobile()
{
    if (validateSalesReturnInfo().validateResult) {

        $("#customerStr").val($("#customer").val());
        $("#salesReturnForm").ajaxSubmit({
            url: "salesReturn.do?method=validateSalesReturnBeforeSettle",
            dataType: "json",
            success: function (json) {
                if (json.success) {
                    bcgogo.checksession({parentWindow: window.parent, 'iframe_PopupBox': $("#iframe_PopupBox_account")[0], 'src': 'salesReturn.do?method=accountDetail&settleType=normal&customerId=' + $("#customerId").val() + '&salesReturnId=' + $("#salesReturnForm #id").val() + '&total=' + $("#salesReturnForm #total").val()});
                } else if (!json.success && json.operation == "confirm_deleted_product") {
                    nsDialog.jConfirm(json.msg, "提示", function (returnVal) {
                        if (returnVal) {
                            bcgogo.checksession({parentWindow: window.parent, 'iframe_PopupBox': $("#iframe_PopupBox_account")[0], 'src': 'salesReturn.do?method=accountDetail&settleType=normal&customerId=' + $("#customerId").val() + '&salesReturnId=' + $("#salesReturnForm #id").val() + '&total=' + $("#salesReturnForm #total").val()});
                        }
                    })
                } else {
                    nsDialog.jAlert(json.msg);
                }
            },
            error: function () {
                nsDialog.jAlert("出现异常，结算失败！");
            }
        });
    }
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

$(function () {
    $('span[data-filter-zero-advanced=true]').each(function () {
        var text = $(this).text();
        var val = /\d*\.*\d*/.exec(text);
        if (val.length > 0) {
            val = GLOBAL.Number.filterZero(val[0]);
            text = text.replace(/\d*\.*\d*/, val);
            $(this).text(text);
        }
    });
});