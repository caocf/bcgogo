/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-8
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(function () {


    // 初始化 datepicker
    $(".J-DeliveryDate")
        .datepicker({
            "numberOfMonths": 1,
            "showButtonPanel": true,
            "changeYear": true,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": "",
            "showHour": false,
            "showMinute": false,
            "onSelect": function (dateText, inst) {
                var _this = inst.input || inst.$input;
                //如果选在非当前的时间 提醒逻辑
                var newValue = _this.val();
                if (G.Lang.isEmpty(newValue)) return;
                if (GLOBAL.Util.getDate(newValue).getTime() - GLOBAL.Util.getDate(GLOBAL.Date.getCurrentFormatDate()).getTime() < 0) {
                    nsDialog.jAlert("不能选择今天之前的日期!");
                    $(_this).val($(_this).attr("lastvalue"));
                    return;
                }
                $(_this).attr("lastvalue", newValue);
            }
        })
        .live("focus", function () {
            $(this).attr("lastvalue", $(this).val());
        });
    $("#deliveryDateInput").datepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": "",
        "showHour": false,
        "showMinute": false,
        "onSelect": function (dateText, inst) {
            var _this = inst.input || inst.$input;
            //如果选在非当前的时间 提醒逻辑
            var newValue = _this.val();
            if (G.Lang.isEmpty(newValue)) return;
            if (GLOBAL.Util.getDate(newValue).getTime() - GLOBAL.Util.getDate(GLOBAL.Date.getCurrentFormatDate()).getTime() < 0) {
                nsDialog.jAlert("不能选择今天之前的日期!");
                $(_this).val('');
                return;
            }
        }

    });

    $("#clearConditionBtn").bind('click', function () {
        $(".J_clear_input").val("");
        $(".J-initialCss").placeHolder("reset");
    });

    $("#checkAll").bind("click",function() {
        $("input[name='selectProduct']").attr("checked",this.checked);
    });
    $("input[name='selectProduct']").live("click",function() {
        var checked = $("input[name='selectProduct']").length == $("input[name='selectProduct']:checked").length ? true : false;
        $("#checkAll").attr("checked",checked);
    });

    $(".J-goSupplier").live("click",function(e){
        e.preventDefault();
        var supplierId = $(this).attr("data-supplierid");
        if(!G.Lang.isEmpty(supplierId)){
            window.open("unitlink.do?method=supplier&supplierId="+supplierId, "_blank");
        }
    });

    $(".J-showAlert").live("mouseenter", function(event) {
        event.stopImmediatePropagation();
        var _currentTarget = $(event.target);
        var _alertTarget = $(event.target).next(".alert");
        _alertTarget.show();
        _alertTarget.mouseleave(function(event) {
            if($(event.relatedTarget)[0] != _currentTarget[0]) {
                _alertTarget.hide();
            }
        });
    }).live("mouseleave", function(event) {
            event.stopImmediatePropagation();
            var _alertTarget = $(event.target).next(".alert");
            if($(event.relatedTarget).closest(".alert")[0] != _alertTarget[0]) {
                _alertTarget.hide();
            }
        });

    $(".J-ModifyAmount")
        .live("blur",function () {
            if (dataTransition.simpleRounding($(this).attr("lastValue"),2) == dataTransition.simpleRounding($(this).val(),2)) {
                $(this).val(dataTransition.simpleRounding($(this).val(),2));
                return;
            }
            var amount = dataTransition.simpleRounding($(this).val(), 2);
            if(amount==0){
                $(this).val($(this).attr("lastValue"));
                nsDialog.jAlert("数量不能为0,请重新输入!");
                calculate($(this).closest("tr"));
                return false;
            }
            $(this).val(amount);
        })
        .live("focus",function () {
            $(this).attr("lastValue", $(this).val());
        })
        .live("keyup", function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
            calculate($(this).closest("tr"));
        });

    $(".J-cancelOrder").live("click",function(e){
        e.preventDefault();
        var $currentOrderBlock = $(this).closest(".J-OrderBlock");
        nsDialog.jConfirm("确认是否删除当前订单?","删除订单", function (returnVal) {
            if (returnVal) {
                $currentOrderBlock.remove();
                $(".J-floatHeightDiv").remove();
                $(".J-floatDiv").css("position","static");
                calculateAllTotal();
            }
        });

    });

    $("#submitOrderBtn").live("click",function(e){
        e.preventDefault();
        var $domObject =  $(this);
        if(G.Lang.isEmpty($domObject.attr("disabled"))){
            $domObject.attr("disabled", "disabled");
            //校验
            var validateFlag = true;
            $("#btnType").val('submitOrderBtn');
            $("#purchaseOrdersForm").find(".J-OrderBlock").each(function(){
                if(G.Lang.isEmpty($(this).find("input[name$='deliveryDateStr']").val())){
                    validateFlag = false;
                    $("#deliveryDateDialog").dialog("open");
                    return false;
                }

                //是否有重复
                var supplierProductIdArray=[];
                $(this).find(".J-ItemBody").each(function(){
                    var supplierProductId= $(this).find("input[name$='supplierProductId']").val();
                    if(G.Lang.contains(supplierProductId,supplierProductIdArray)){
                        validateFlag = false;
                        nsDialog.jAlert("当前采购单中有重复商品,请修改后重新提交!");
                        return false;
                    }else{
                        supplierProductIdArray.push(supplierProductId);
                    }
                });
            });
            if(validateFlag){
                $("#purchaseOrdersForm").ajaxSubmit({
                    url: "RFbuy.do?method=ajaxValidatorPurchaseOrderOnline",
                    dataType: "json",
                    type: "POST",
                    success: function(json) {
                        if (json.success) {
                            $("#purchaseOrdersForm").submit();
                        } else{
                            //增加 商品上下架状态 和  供应商
                            if(json.operation=="UPDATE_SUPPLIER_RELATION"){
//                                $.each(json.data,function(key,supplierDTO){
//                                    $(".J-supplierRelationWarningLink-"+supplierDTO.idStr).show();
//                                    $(".J-supplierRelationWarningLink-"+supplierDTO.idStr).parents(".J-OrderBlock").find("input[id$='supplierShopId']").val("");
//                                    if(supplierDTO.status == 'DISABLED'){
//                                        $(".J-supplierDisabledWarningLink-"+supplierDTO.idStr)
//                                            .removeClass("J-goSupplier")
//                                            .removeClass("blue_color")
//                                            .addClass("J-showAlert")
//                                            .addClass("gray_color");
//                                    }
//                                });
                            }else if(json.operation=="UPDATE_PRODUCT_SALES_STATUS"){
                                $.each(json.data,function(key,supplierProductId){
                                    $(".J-productNotInSalesWarningDiv-"+supplierProductId).show();
                                });
                            }
                            nsDialog.jAlert(json.msg);
                            $domObject.removeAttr("disabled");
                        }
                    },
                    error: function() {
                        $domObject.removeAttr("disabled");
                        nsDialog.jAlert("数据异常，请刷新页面！");
                    }
                });
            }else{
                $domObject.removeAttr("disabled");
            }
        }

    });

    $(".J-deleteItemRow").live("click",function(e){
        e.preventDefault();
        var $deleteLink = $(this);
        var $currentRow = $deleteLink.closest("tr");
        var $table = getPurchaseOnlineTableByRow($currentRow);
        var $closestRow = $currentRow.closest(".item");
        nsDialog.jConfirm("确认是否删除当前商品?","删除商品", function (returnVal) {
            if (returnVal) {
                $currentRow.remove();
                resetPromotionMap($table);
                generatePromotionsStat($table);
                if($table.find(".item").size()==0){
                    $(".J-OrderTotal").text("0");
                }
                calculateAll();
                calculateAllTotal();
            }
        });
    });

    $(".J-customPrice").live('click', function(e) {
        e.preventDefault();
        var msg = "友情提示：填写期望价格后将直接计算合计金额，不会按照促销进行优惠！是否要继续填写期望价格？";
        if(G.Lang.isEmpty($(this).closest("tr").find("input[id='promotionsId']").val())){
            msg = "友情提示：填写期望价格后将直接计算合计金额，不会按照报价进行计算！是否要继续填写期望价格？";
        }
        var $currentRow = $(this).closest("tr");
        nsDialog.jConfirm(msg,"填写期望价", function (returnVal) {
            if (returnVal) {
                $currentRow.find(".J-customPriceInput,.J-cancelCustomPrice,.J-oldPrice").show();
                $currentRow.find(".J-customPrice,.J-SaveInfo,.J-newPrice").hide();
                $currentRow.find("input[id$='customPriceFlag']").val("true");
                //
                resetPromotionMap(getPurchaseOnlineTableByRow($currentRow));
                var $block = $currentRow.parents(".J-OrderBlock");
                $block.find(".barginDiv, .mljDiv, .mjsDiv, .freeShippingDiv").hide();
                $block.find("#bargainSpan, #mljSpan, #mjsSpan").text("");
                $block.find("#promotionsTotalSpan").text("0");
                $currentRow.find(".J-oldPrice").removeClass("gray").addClass("black");
                $currentRow.find(".J-newPrice").hide();
                $currentRow.find(".J-SaveInfo").hide();
                calculateAll();
            }
        });
    });

    $(".J-cancelCustomPrice").live('click', function(e) {
        e.preventDefault();
        var $currentRow = $(this).closest("tr");
        $currentRow.find(".J-customPriceInput,.J-cancelCustomPrice").hide();
        $currentRow.find(".J-customPrice").show();
        $currentRow.find("input[id$='customPriceFlag']").val("false");
        $currentRow.find("input[id$='price']").val("");
        $currentRow.find(".J-oldPrice").removeClass("gray").addClass("black");
        $currentRow.find(".J-oldPrice").show();
        $currentRow.find(".J-newPrice").hide();
        $currentRow.find("input[id$='promotionsId']").val($currentRow.find("a.promotionsInfo").attr("promotionsidlist"));

        calculate($currentRow);
    });

    $(".J-customPriceValue")
        .live("blur",function () {
            if (dataTransition.simpleRounding($(this).attr("lastValue"),2) == dataTransition.simpleRounding($(this).val(),2)){
                $(this).val(dataTransition.simpleRounding($(this).val(),2));
                return;
            }
            var customPrice = dataTransition.simpleRounding($(this).val(), 2);
            if(customPrice==0){
                $(this).val($(this).attr("lastValue"));
                nsDialog.jAlert("价格不能为0,请重新输入!");
                //
                calculate($(this).closest("tr"));
                return false;
            }
            $(this).val(customPrice);
        })
        .live("focus",function () {
            $(this).attr("lastValue", $(this).val());
        })
        .live("keyup", function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
            calculate($(this).closest("tr"));
        });


    $(".J-modifyOrder").bind('click', function(e) {
        e.preventDefault();
        $("#btnType").val('J-modifyOrder');
        if(G.Lang.isEmpty($("#deliveryDateStr").val())){
            $("#deliveryDateDialog").dialog("open");
            return false;
        }
        var $currentOrderBlock =$(this).closest(".J-OrderBlock");
        var $orderForm = $(this).closest("form");
        var purchaseOrderId =$orderForm.find("input[id='id']").val();
        if(!G.Lang.isEmpty(purchaseOrderId)){
            nsDialog.jConfirm("确认是否修改当前订单", "修改订单", function(resultVal) {
                if(resultVal) {
                    $orderForm.ajaxSubmit({
                        dataType: "json",
                        type: "POST",
                        success: function(json) {
                            if(json.success){
                                nsDialog.jAlert("修改订单成功！");
                            }else{
                                if(json.operation=='REDIRECT_SHOW'){
                                    nsDialog.jAlert(json.msg);
                                    if($(".J-OrderBlock").length>1){
                                        $currentOrderBlock.remove();
                                        calculateAllTotal();
                                        window.open("RFbuy.do?method=show&id=" + purchaseOrderId);
                                    }else{
                                        window.location.href="RFbuy.do?method=show&id=" + purchaseOrderId;
                                    }

                                }else{
                                    //增加 商品上下架状态
                                    if(json.operation=="UPDATE_PRODUCT_SALES_STATUS"){
                                        $.each(json.data,function(key,supplierProductId){
                                            $(".J-productNotInSalesWarningDiv-"+supplierProductId).show();
                                        });
                                    }
                                    nsDialog.jAlert(json.msg);
                                }
                            }
                        },
                        error: function() {
                            nsDialog.jAlert("数据异常，请刷新页面！");
                        }
                    });
                }
            });
        }
    });

    $(".J-repealOrder").bind('click', function(e) {
        e.preventDefault();
        var confirmMsg = "";
        var $currentOrderBlock =$(this).closest(".J-OrderBlock");
        var status = $currentOrderBlock.find("input[name='status']").val();
        var id = $currentOrderBlock.find("input[name='id']").val();
        if(status == 'SELLER_PENDING') {
            confirmMsg = "友情提示：采购单作废后，将不再有效，交易会被取消！您确定要作废该采购单吗？";
        } else if(status == 'SELLER_REFUSED' || status == 'PURCHASE_SELLER_STOP') {
            confirmMsg = "友情提示：作废后该采购单不再属于待办单据，只能通过查询找出！您确定要作废该采购单吗？";
        } else {
            confirmMsg = "友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！";
        }
        nsDialog.jConfirm(confirmMsg, "作废订单", function(resultVal) {
            if(resultVal) {
                if($(".J-OrderBlock").length>1){
                    $currentOrderBlock.remove();
                    calculateAllTotal();
                    window.open("RFbuy.do?method=purchaseOrderRepeal&id=" + id);
                }else{
                    window.location = "RFbuy.do?method=purchaseOrderRepeal&id=" + id;
                }
            }
        });
    });


    /*--------------------选择商品-------------------------------------------------*/
    $(".J-initialCss").placeHolder();

    $(".J-productSuggestion")
        .bind('click', function () {
            supplierProductSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                supplierProductSuggestion($(this));
            }
        });

    $("#searchQuotationBtn").click(function (e) {
        e.preventDefault();
        $("#wholeSalerStockSearchForm").submit();
    });

    $("#wholeSalerStockSearchForm").submit(function (e) {
        e.preventDefault();
        $(".J-initialCss").placeHolder("clear");
        var param = $("#wholeSalerStockSearchForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            data[val.name] = val.value;
        });
        APP_BCGOGO.Net.syncPost({
            url: "autoAccessoryOnline.do?method=getCommodityQuotationsList",
            dataType: "json",
            data:data,
            success: function (result) {
                drawCommodityQuotationsTable(result);
                initPages(result, "commodityQuotations", "autoAccessoryOnline.do?method=getCommodityQuotationsList", '', "drawCommodityQuotationsTable", '', '', data, '');
                $(".J-initialCss").placeHolder("reset");
            },
            error: function () {
                $(".J-initialCss").placeHolder("reset");
                $("#commodityTable tr:not(:first)").remove();
                $("#commodityTable").append($("<tr><td colspan='4' style='text-align: left;margin-left: 10px;'><span style='margin-left: 10px;'>没有数据</span></td></tr>"));
                $(".J-totalNumber").text("0");
                nsDialog.jAlert("数据异常，请刷新页面！");
            }
        });

        return false;
    });


    $(".J-addProduct").live("click",function(e){
        e.preventDefault();
        var shopId = $(this).closest(".J-OrderBlock").find("input[id$='supplierShopId']").val();
        var wholesaler = $(this).closest(".J-OrderBlock").find("input[id$='supplier']").val();
        if(!G.Lang.isEmpty(shopId)){
            $("#selectProductDialog").find("input[id='shopId']").val(shopId);
            $("#selectProductDialog").dialog({
                width: 755,
                height: 610,
                title:wholesaler+"&nbsp;添加配件查询",
                modal: true,
//                resizable:false,
                autoOpen:false,
                position:'center',
                beforeclose: function(event, ui) {
                    APP_BCGOGO.Module.droplist.hide();
                    $(".J-initialCss").val("");
                    $(".J-initialCss").placeHolder("reset");
                    $("#productTable tr").remove();
                    $("#checkAll").attr("checked",false);
                    $("#commodityTable").find("input[name='selectProduct']").attr("checked",false);
                    return true;
                },
                open: function() {
                    $(this).find("input[type='text']").first().blur();
                }
            });
            $("#wholeSalerStockSearchForm").submit();
            $("#selectProductDialog").dialog("open");
        }
    });

    $("#selectProductConfirmBtn").bind("click", function () {
        var shopId = $("#selectProductDialog").find("input[id='shopId']").val();
        var $currentOrderBlock = $(".J-OrderBlock-"+shopId);
        if($currentOrderBlock){
            var dataArr = [];
            var dataFieldArr = ["commodityCode","productName","brand","spec","model","vehicleBrand","vehicleModel","quotedPrice","unit","imageSmallURL"];
            var supplierProductIdArr=new Array();
            $("input[name='selectProduct']:checked").each(function(i){
                var supplierProductId = $(this).attr("data-productlocalinfoid");
                if(!G.Lang.isEmpty(supplierProductId)){
                    var $currentRow = $(this).closest("tr");
                    var promotionsInfoJson =$currentRow.find("input[name='promotionsInfoJson']").val();
                    var promotionsId="";
                    if(!G.Lang.isEmpty(promotionsInfoJson)){
                        var promotionsDTOs = JSON.parse(promotionsInfoJson);
                        if(promotionsDTOs){
                            for(var i in promotionsDTOs){
                                promotionsId += promotionsDTOs[i].id + ",";
                            }
                            promotionsId = promotionsId.substr(0, promotionsId.length -1 );
                        }
                    }

                    var data = {
                        "supplierProductId":supplierProductId,
                        "promotionsDTO":promotionsDTOs,
                        "promotionsId":promotionsId,
                        "promotionsInfoJson":promotionsInfoJson,
                        "amount": 1
                    };
                    $.each(dataFieldArr ,function(key,val){
                        data[val]=$currentRow.find("input[name='"+val+"']").val();
                    });
                    dataArr.push(data);
                    supplierProductIdArr.push(supplierProductId);
                }

            });
            if($("#selectProductDialog").hasClass("J-PurchaseOrderOnlineModify")){
                $.each(dataArr ,function(key,data){
                    addRowInModify($currentOrderBlock,data);
                });
            }else{
                $.each(dataArr ,function(key,data){
                    addRowInCreate($currentOrderBlock,data);
                });
            }
            initOrderPromotionsDetail(supplierProductIdArr);
            calculate($(".J-ProductTable .J-ItemBody:last"));
        }
        $("#selectProductDialog").dialog("close");
    });

    $("#selectProductCloseBtn").bind("click", function () {
        $("#selectProductDialog").dialog("close");
    });



    /*************************************单个修改单据页面********************************************************/

    $("#nullifyBtn").bind('click', function(e) {
        e.preventDefault();
        var confirmMsg = "";
        var status = $("#status").val();
        var id = $("#id").val();
        if(status == 'SELLER_PENDING') {
            confirmMsg = "友情提示：采购单作废后，将不再有效，交易会被取消！您确定要作废该采购单吗？";
        } else if(status == 'SELLER_REFUSED' || status == 'PURCHASE_SELLER_STOP') {
            confirmMsg = "友情提示：作废后该采购单不再属于待办单据，只能通过查询找出！您确定要作废该采购单吗？";
        } else {
            confirmMsg = "友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！";
        }
        nsDialog.jConfirm(confirmMsg, "作废订单", function(resultVal) {
            if(resultVal) {
                window.location = "RFbuy.do?method=purchaseOrderRepeal&id=" + id;
            }
        });
    });

    $("#resetBtn").bind('click', function(e) {
        e.preventDefault();
        window.location.reload();
    });

    $("#purchaseModifyBtn").bind('click', function(e) {
        e.preventDefault();
        $("#btnType").val('purchaseModifyBtn');
        if(G.Lang.isEmpty($("#deliveryDateStr").val())){
            $("#deliveryDateDialog").dialog("open");
            return false;
        }
        if(!G.Lang.isEmpty($("#id").val())){
            nsDialog.jConfirm("确认是否修改当前订单?", "修改订单", function(resultVal) {
                if(resultVal) {
                    $("#purchaseOrdersForm").ajaxSubmit({
                        dataType: "json",
                        type: "POST",
                        success: function(json) {
                            if(json.success){
//                        nsDialog.jAlert("修改订单成功！",null, function () {
//                            window.location.reload();
//                        });
                                nsDialog.jAlert("修改订单成功！");
                            }else{
                                if(json.operation=='REDIRECT_SHOW'){
                                    nsDialog.jAlert(json.msg);
                                    window.location.href="RFbuy.do?method=show&id=" + $("#id").val();
                                }else{
                                    //增加 商品上下架状态
                                    if(json.operation=="UPDATE_PRODUCT_SALES_STATUS"){
                                        $.each(json.data,function(key,supplierProductId){
                                            $(".J-productNotInSalesWarningDiv-"+supplierProductId).show();
                                        });
                                    }
                                    nsDialog.jAlert(json.msg);
                                }
                            }
                        },
                        error: function() {
                            nsDialog.jAlert("数据异常，请刷新页面！");
                        }
                    });
                }
            });
        }
    });

    $("#printBtn").bind('click', function(e) {
        e.preventDefault();
        if($("#id").val()) {
            window.showModalDialog("RFbuy.do?method=print&id=" + $("#id").val());
        }
    });
     initPromotionsInfo();

    $(".J_QQ").each(function(){
      $(this).multiQQInvoker({
          QQ:$(this).attr("data_qq")
      })
    });

    $("#deliveryDateDialog").dialog({
        autoOpen: false,
        resizable: false,
        title: "选择期望交货日期",
        height: 130,
        width: 390,
        modal: true,
        closeOnEscape: false,
        showButtonPanel: true
    });
    $("#confirmBtn1").click(function(){
        $("input[id$='deliveryDateStr']").val($("#deliveryDateInput").val());
        $("#deliveryDateInput").val('');
        $("#deliveryDateDialog").dialog("close");
        if(!G.isEmpty($("#btnType").val())) {
            $("#" + $("#btnType").val()).click();
        }

    });
    $("#cancleBtn").click(function(){
        $("#deliveryDateInput").val('');
        $("#deliveryDateDialog").dialog("close");
    });
});

function initPromotionsInfo(){
    $(".J-OrderBlock").each(function(){
        var $block = $(this);
        var supplierProductIdArr=new Array();
        $block.find(".item").each(function(){
            if(G.isEmpty($(this).find("input[id$='quotedPreBuyOrderItemId']").val())){
                supplierProductIdArr.push($(this).find("[name$='supplierProductId']").val());
            }
        });
        initOrderPromotionsDetail(supplierProductIdArr, $block.find("#id").val());
    });
}

//function initPromotionsInfoInDiv(){
//    var supplierProductIdArr=new Array();
//    $("#commodityTable .offerBg").each(function(){
//        supplierProductIdArr.push($(this).find("[name='selectProduct']").attr("data-productlocalinfoid"));
//    });
//    initPromotionsInfoForAddAccessory(supplierProductIdArr);
//}

//重新计算整张单据价格
function calculateAll(){
    $(".item").each(function(){
        calculate($(this));
    });
}

//重新计算单行所引起的变动
function calculate($currentRow) {
    calculatePurchaseOrderTotal($currentRow);
}

function calculatePurchaseOrderTotal($currentRow){
    var amount = $currentRow.find("input[id$='amount']").val();
    var productId=$currentRow.find("[name$='supplierProductId']").val();
    var total=0;
    var customPriceFlag = $currentRow.find("input[id$='customPriceFlag']").val();
    if(hasPromotions($currentRow)){
        var newPrice=G.rounding(calculateOrderTotal($currentRow));
        if(hasPromotionsType($currentRow,"BARGAIN")){
            $currentRow.find(".purchasePrice").val(newPrice);
            $currentRow.find(".J-customPriceValue").val(newPrice);
        }else if(hasPromotionsType($currentRow,"MLJ")){
            var productId=$currentRow.find("[name$='supplierProductId']").val();
            var promotions=getPromotions(productId,"MLJ");
            if(promotions.promotionsLimiter=="OVER_AMOUNT"){
                $currentRow.find(".purchasePrice").val(newPrice);
            }
        }
        var price=G.rounding($currentRow.find(".purchasePrice").val());
        total=G.rounding(price*amount);
    }else if(customPriceFlag && customPriceFlag == 'true') {
        var customPrice = $currentRow.find("input[id$='price']").val()*1;
        total=G.rounding(customPrice*amount);
    }else{
        var price=G.rounding($currentRow.find(".purchasePrice").val());
        total=G.rounding(price*amount);
    }
    $currentRow.find(".J-ItemTotal").text(total);

    var $currentOrderBlock = $currentRow.closest(".J-OrderBlock");
    var orderTotal = 0;
    $currentOrderBlock.find(".J-ItemTotal").each(function(){
        orderTotal+= G.rounding($(this).text(),2);
    });
    $currentOrderBlock.find(".J-OrderTotal").text(G.sub(orderTotal, G.sub($currentOrderBlock.find("#promotionsTotalSpan").text(), $currentOrderBlock.find("#bargainSpan").text())));
    calculateAllTotal();
}

function calculateAllTotal(){
    var total = 0;
    $(".J-OrderTotal").each(function(){
        total+= G.rounding($(this).text());
    });
    $(".J-AllTotal").text(G.rounding(total));
}


function getTrSample(itemDTO) {
    var trSample = '<tr class="bg J-ItemBody item">';
    trSample += '   <input type="hidden" id="itemDTOs0.productName" name="itemDTOs[0].productName" value="'+G.Lang.normalize(itemDTO.productName)+'"/>';
    trSample += '   <input type="hidden" id="itemDTOs0.brand" name="itemDTOs[0].brand" value="'+G.Lang.normalize(itemDTO.brand)+'"/>';
    trSample += '   <input type="hidden" id="itemDTOs0.spec" name="itemDTOs[0].spec" value="'+G.Lang.normalize(itemDTO.spec)+'"/>';
    trSample += '   <input type="hidden" id="itemDTOs0.model" name="itemDTOs[0].model" value="'+G.Lang.normalize(itemDTO.model)+'"/>';
    trSample += '   <input type="hidden" id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand" value="'+G.Lang.normalize(itemDTO.vehicleBrand)+'"/>';
    trSample += '   <input type="hidden" id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel" value="'+G.Lang.normalize(itemDTO.vehicleModel)+'"/>';
    trSample += '   <input type="hidden" id="itemDTOs0.supplierProductId" name="itemDTOs[0].supplierProductId" value="'+G.Lang.normalize(itemDTO.supplierProductId)+'"/>';
    trSample += '   <input type="hidden" id="itemDTOs0.unit" name="itemDTOs[0].unit" value="'+G.Lang.normalize(itemDTO.unit)+'"/>';
    trSample += '   <input type="hidden" id="itemDTOs0.promotionsId" name="itemDTOs[0].promotionsId" class="j_promotions_id" value="' + itemDTO.promotionsId + '"/>';
    trSample += '   <td style="padding-left:10px;">';
    trSample += '       <div style="float: left">';
    trSample += '           <div class="J-productNotInSalesWarningDiv-'+G.Lang.normalize(itemDTO.supplierProductId)+'" style="margin: 10px 0;float:left;display:none">';
    trSample += '           <a class="ban J-showAlert" style="margin: 2px 5px 0 0;"></a>';
    trSample += '           <div class="alert" style="display: none">';
    trSample += '           <a class="arrowTop"></a>';
    trSample += '           <div class="alertAll">';
    trSample += '           <div class="alertLeft"></div>';
    trSample += '           <div class="alertBody">';
    trSample += '           商品已下架';
    trSample += '           </div>';
    trSample += '           <div class="alertRight"></div>';
    trSample += '           </div>';
    trSample += '           </div>';
    trSample += '       </div>';
    trSample += '       <div style="margin: 10px 0;float: left">';
    trSample += '           <a style="width: 60px;height: 60px;cursor: pointer">';
    trSample += '               <img src="'+itemDTO.imageSmallURL+'" style="width: 60px;height: 60px">';
    trSample += '           </a>';
    trSample += '       </div>';
    trSample += '       <div style="width: 430px;margin: 10px 5px;float: left">';
    trSample +=             G.Lang.normalize(itemDTO.commodityCode)+(G.Lang.isEmpty(itemDTO.commodityCode)?"":"&nbsp;");
    trSample += '           <span style="font-weight: bold;">'+G.Lang.normalize(itemDTO.productName)+(G.Lang.isEmpty(itemDTO.productName)?"":"&nbsp;")+G.Lang.normalize(itemDTO.brand)+(G.Lang.isEmpty(itemDTO.brand)?"":"&nbsp;")+'</span>';
    trSample +=             G.Lang.normalize(itemDTO.spec)+(G.Lang.isEmpty(itemDTO.spec)?"":"&nbsp;");
    trSample +=             G.Lang.normalize(itemDTO.model)+(G.Lang.isEmpty(itemDTO.model)?"":"&nbsp;");
    trSample +=             G.Lang.normalize(itemDTO.vehicleBrand)+(G.Lang.isEmpty(itemDTO.vehicleBrand)?"":"&nbsp;");
    trSample +=             G.Lang.normalize(itemDTO.vehicleModel);
    trSample += '       </div>';


    trSample += '       </div>';
    trSample += '       <div style="float: left">';
    trSample += '           <input type="hidden" id="promotionsInfoJson" value="'+G.Lang.normalize(itemDTO.promotionsInfoJson)+'"/>';
    trSample += '           <input type="hidden" id="promotionsId" value="'+itemDTO.promotionsId+'"/>';
    trSample += '       </div>';
    trSample += '   </td>';
    trSample += '   <td>';
    trSample += '       <div>';
    trSample += '           <a class="J-oldPrice" style="color: #848484">'+itemDTO.quotedPrice+'</a>&nbsp;';
    trSample += '           <a class="J-newPrice yellow_color" style="display: none"></a>';
    trSample += '           <input type="hidden" class="purchasePrice" value="'+ itemDTO.quotedPrice +'"/>';
    trSample += '           <input type="hidden" class="bargainSaveValue" value="0"/>';
    trSample += '           <span class="save J-SaveInfo" style="display: none">';
    trSample += '               <div class="saveLeft"></div>';
    trSample += '               <div class="saveBody J-SaveValue"> 省'+(itemDTO.quotedPrice - itemDTO.price)+'</div>';
    trSample += '               <div class="saveRight"></div>';
    trSample += '           </span>';
    trSample += '       </div>';
    trSample += '       <div style="margin-left: 2px;">';
    trSample += '           <span class="J-customPriceInput" style="display: none">';
    trSample += '               <input type="text" id="itemDTOs0.price" name="itemDTOs[0].price" class="J-customPriceValue txt" style="width: 50px;height: 16px;" maxlength="7"/>';
    trSample += '               <input type="hidden" id="itemDTOs0.customPriceFlag" name="itemDTOs[0].customPriceFlag" value="false"/>';
    trSample += '           </span>';
    trSample += '           <a class="yiPrice J-showAlert J-customPrice"></a>';
    trSample += '           <div class="alert" style="margin: -5px 0px 0px -5px; display: none;">';
    trSample += '               <a class="arrowTop" style="margin-left:10px;"></a>';
    trSample += '               <div class="alertAll">';
    trSample += '                   <div class="alertLeft"></div>';
    trSample += '                   <div class="alertBody"> 期望价</div>';
    trSample += '                   <div class="alertRight"></div>';
    trSample += '               </div>';
    trSample += '           </div>';
    trSample += '           <a class="yiPriceBtn J-cancelCustomPrice" style="display:none">取消</a>';
    trSample += '       </div>';
    trSample += '   </td>';
    trSample += '   <td><input type="text" id="itemDTOs0.amount" name="itemDTOs[0].amount" value="'+itemDTO.amount+'" maxlength="6" class="txt J-ModifyAmount" style="width:50px; height:16px;"/>&nbsp;'+G.Lang.normalize(itemDTO.unit)+'</td>';
    trSample += '   <td class="promotions_info_td"></td>';
    trSample += '   <td><b class="yellow_color J-ItemTotal"></b></td>';
    trSample += '   <td><a class="blue_col J-deleteItemRow">删除</a></td>';
    trSample += '</tr>';
    return trSample;
}
function addRowInModify($currentOrderBlock,data){
    var $currentTable = $currentOrderBlock.find(".J-ProductTable");
    var isExist = false;
    $currentTable.find(".J-ItemBody").each(function(){
        if($(this).find("input[name$='.supplierProductId']").val()==data.supplierProductId){
            var amount =$(this).find("input[name$='.amount']").val()*1;
            $(this).find("input[name$='.amount']").val(dataTransition.rounding(amount+1,1));
            calculate($(this));
            isExist = true;
            return false;
        }
    });
    if(!isExist){
        var tr = $(getTrSample(data)).clone();
        $(tr).find("input").each(function (i) {
            //去除文本框的自动填充下拉框
            if ($(this).attr("type") == "text") {
                $(this).attr("autocomplete", "off");
            }
            if (!this || !this.id) return;

            var idSuffix = this.id.split(".")[1];
            var tcNum = $currentTable.find(".J-ItemBody").length;
            //计算行号，如果目标行号对应的元素存在，则该行号加一
            while (checkThisDom(tcNum, idSuffix)) {
                tcNum+=1;
            }
            //组装新的ID和NAME
            var newId = "itemDTOs" + tcNum + "." + idSuffix;
            $(this).attr("id", newId);
            if ($(this).attr("name").split(".")[1]) {
                var newName = "itemDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
                $(this).attr("name", newName);
            }
        });
        $currentTable.find(".J-ItemBottom").before($(tr));
        calculate($(tr));
    }

}
function addRowInCreate($currentOrderBlock,data){
    var $currentTable = $currentOrderBlock.find(".J-ProductTable");
    var isExist = false;
    $currentTable.find(".J-ItemBody").each(function(){
        if($(this).find("input[name$='.supplierProductId']").val()==data.supplierProductId){
            var amount =$(this).find("input[name$='.amount']").val()*1;
            $(this).find("input[name$='.amount']").val(dataTransition.rounding(amount+1,1));
            calculate($(this));
            isExist = true;
            return false;
        }
    });
    if(!isExist){
        var index = $currentOrderBlock.find(".J-Index").val();
        var tr = $(getTrSample(data)).clone();
        $(tr).find("input").each(function (i) {
            //去除文本框的自动填充下拉框
            if ($(this).attr("type") == "text") {
                $(this).attr("autocomplete", "off");
            }
            if (!this || !this.id) return;
            var idSuffix = this.id.split(".")[1];
            var tcNum = $currentTable.find(".J-ItemBody").length;
            //计算行号，如果目标行号对应的元素存在，则该行号加一
            while (checkThisDom(tcNum, idSuffix)) {
                tcNum+=1;
            }
            //组装新的ID和NAME
            var newId = "purchaseOrderDTOs"+index+"."+"itemDTOs" + tcNum + "." + idSuffix;
            $(this).attr("id", newId);
            if ($(this).attr("name").split(".")[1]) {
                var newName = "purchaseOrderDTOs["+index+"]."+"itemDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
                $(this).attr("name", newName);
            }
        });
        $currentTable.find(".J-ItemBottom").before($(tr));
        calculate($(tr));
    }
}

function drawCommodityQuotationsTable(json) {
    $("#commodityTable tr:not(:first)").remove();
    $("#checkAll").attr("checked",false);
    var noDataMsg = "没有数据";
    if (json == null || json[0] == null || json[0].data == null) {
        $("#commodityTable").append($("<tr><td colspan='4' style='text-align: left;margin-left: 10px;'><span style='margin-left: 10px;'>"+noDataMsg+"</span></td></tr>"));
        $(".J-totalNumber").text("0");
        return;
    }
    if(json[0].data.products == null || json[0].data.products.length == 0 ){
        $("#commodityTable").append($("<tr><td colspan='4' style='text-align: left;margin-left: 10px;'><span style='margin-left: 10px;'>"+noDataMsg+"</span></td></tr>"));
        $(".J-totalNumber").text("0");
        return;
    }
    $(".J-totalNumber").text(json[0].data.numFound);
    $.each(json[0].data.products, function (index, productDTO) {
        var commodityCode = productDTO.commodityCode == null ? "" : productDTO.commodityCode+"&nbsp;";
        var productLocalInfoIdStr = productDTO.productLocalInfoIdStr;
        var productName = productDTO.name == null ? "" : productDTO.name+"&nbsp;";
        var productBrand = productDTO.brand == null ? "" : productDTO.brand+"&nbsp;";
        var productSpec = productDTO.spec == null ? "" : productDTO.spec+"&nbsp;";
        var productModel = productDTO.model == null ? "" : productDTO.model;
        var productVehicleBrand = productDTO.productVehicleBrand == null ? "" : productDTO.productVehicleBrand+"&nbsp;";
        var productVehicleModel = productDTO.productVehicleModel == null ? "" : productDTO.productVehicleModel;
        var inSalesAmount = productDTO.inSalesAmount == null ? "" : dataTransition.rounding(productDTO.inSalesAmount, 1);
        var inSalesPrice = productDTO.inSalesPrice == null ? "" : dataTransition.rounding(productDTO.inSalesPrice, 2);
        var sellUnit = productDTO.sellUnit == null ? "" : productDTO.sellUnit;
        var productInfo = commodityCode+"<span style='font-weight: bold;'>"+productName+productBrand+"</span>"+productSpec+productModel+productVehicleBrand+productVehicleModel;
        var imageSmallURL = productDTO.imageCenterDTO.productListSmallImageDetailDTO.imageURL;
         //promotions info
        var promotionsList=productDTO.promotionsDTOs;
        var promotionsing=!G.isEmpty(promotionsList);  //判断商品是否正在促销

        var tr = '<tr class="offerBg">';
        tr += '   <input type="hidden" name="commodityCode" value="'+G.Lang.normalize(productDTO.commodityCode)+'"/>';
        tr += '   <input type="hidden" name="productName" value="'+G.Lang.normalize(productDTO.name)+'"/>';
        tr += '   <input type="hidden" name="brand" value="'+G.Lang.normalize(productDTO.brand)+'"/>';
        tr += '   <input type="hidden" name="spec" value="'+G.Lang.normalize(productDTO.spec)+'"/>';
        tr += '   <input type="hidden" name="model" value="'+G.Lang.normalize(productDTO.model)+'"/>';
        tr += '   <input type="hidden" name="vehicleBrand" value="'+G.Lang.normalize(productDTO.productVehicleBrand)+'"/>';
        tr += '   <input type="hidden" name="vehicleModel" value="'+G.Lang.normalize(productDTO.productVehicleModel)+'"/>';
        tr += '   <input type="hidden" name="promotionsInfoJson" value=\''+JSON.stringify(productDTO.promotionsDTOs)+'\'>';
        tr += '   <input type="hidden" name="price" value="'+inSalesPrice+'">';
        tr += '   <input type="hidden" name="quotedPrice" value="'+inSalesPrice+'">';
        tr += '   <input type="hidden" name="unit" value="'+sellUnit+'">';
        tr += '   <input type="hidden" name="imageSmallURL" value="'+imageSmallURL+'">';

        tr += '<td colspan="2" class="promotions_info_td">';
        tr += '   <input type="checkbox" class="chk" name="selectProduct" data-productlocalinfoid="' + productLocalInfoIdStr + '" />' + productInfo;
          if(promotionsing){
              tr+=  generatePromotionsAlertTitle(productDTO, 'search');
          }
        tr += '</td>';
        if(inSalesAmount==-1){
             tr += '<td>有货</td>';
        }else{
            tr += '<td>'+inSalesAmount + '&nbsp;' + sellUnit+'</td>';
        }
        tr += '<td><span class="arialFont">&yen;</span>'+inSalesPrice + '</td>';
        tr += '</tr>';
        $("#commodityTable").append($(tr));
//        tableUtil.tableStyle('#commodityTable','.tabTitle');
    });
}

function supplierProductSuggestion($domObject) {
    var shopId = $domObject.closest("form").find("input[name='shopId']").val();
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var dropList = APP_BCGOGO.Module.droplist;
    dropList.setUUID(GLOBAL.Util.generateUUID());
    var currentSearchField =  $domObject.attr("searchField");
    var ajaxData = {
        shopId:shopId,
        searchWord: searchWord,
        searchField: currentSearchField,
        uuid: dropList.getUUID()
    };
    $domObject.parent().prevAll().each(function(){
        var $productSearchInput = $(this).find(".J-productSuggestion");
        if($productSearchInput && $productSearchInput.length>0){
            var val = $productSearchInput.val().replace(/[\ |\\]/g, "");
            if($productSearchInput.attr("name")!="searchWord"){
                ajaxData[$productSearchInput.attr("name")] = val == $productSearchInput.attr("initialValue") ? "" : val;
            }
        }
    });
    var ajaxUrl = "product.do?method=searchWholeSalerProductInfo";
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        if (currentSearchField == "product_info") {
            dropList.show({
                "selector": $domObject,
                "autoSet": false,
                "data": result,
                onGetInputtingData: function() {
                    var details = {};
                    $domObject.parent().nextAll().each(function () {
                        var $productSearchInput = $(this).find(".J-productSuggestion");
                        if ($productSearchInput && $productSearchInput.length > 0) {
                            var val = $productSearchInput.val().replace(/[\ |\\]/g, "");
                            details[$productSearchInput.attr("searchField")] = val == $productSearchInput.attr("initialValue") ? "" : val;
                        }
                    });
                    return {
                        details:details
                    };
                },
                onSelect: function (event, index, data, hook) {
                    $domObject.parent().nextAll().each(function () {
                        var $productSearchInput = $(this).find(".J-productSuggestion");
                        if ($productSearchInput && $productSearchInput.length > 0) {
                            var label = data.details[$productSearchInput.attr("searchField")];
                            if (G.Lang.isEmpty(label) && $productSearchInput.attr("initialValue")) {
                                $productSearchInput.val($productSearchInput.attr("initialValue"));
                                $productSearchInput.css({"color": "#ADADAD"});
                            } else {
                                $productSearchInput.val(G.Lang.normalize(label));
                                $productSearchInput.css({"color": "#000000"});
                            }
                        }
                    });
                    dropList.hide();
                },
                onKeyboardSelect: function (event, index, data, hook) {
                    $domObject.parent().nextAll().each(function () {
                        var $productSearchInput = $(this).find(".J-productSuggestion");
                        if ($productSearchInput && $productSearchInput.length > 0) {
                            var label = data.details[$productSearchInput.attr("searchField")];
                            if (G.Lang.isEmpty(label) && $productSearchInput.attr("initialValue")) {
                                $productSearchInput.val($productSearchInput.attr("initialValue"));
                                $productSearchInput.css({"color": "#ADADAD"});
                            } else {
                                $productSearchInput.val(G.Lang.normalize(label));
                                $productSearchInput.css({"color": "#000000"});
                            }
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
                    $domObject.parent().nextAll().each(function () {
                        var $productSearchInput = $(this).find(".J-productSuggestion");
                        if ($productSearchInput) {
                            clearSearchInputValueAndChangeCss($productSearchInput[0]);
                        }
                    });
                    dropList.hide();
                }
            });
        }
    });
}
