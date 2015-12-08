/**
 * @依赖 js/base.js;  js/application.js;  js/module/invoiceCommon.js;  js/module/bcgogoValidate.js
 */
//TODO 入库单脚本文件
var trCount;
var idPrefixLastModified;
var flag;
var submitFlag;
//var vehicleArray = new Array();
var invoiceCommon = APP_BCGOGO.Module.wjl.invoiceCommon;
var bcgogoValidator = APP_BCGOGO.Module.wjl.bcgogoValidator;

var mySlider = new tableUtil.sliderBar({
  mainLayer: '.slider-main-area',
  mainTable: '.slider-main-table',

  subLayer: '.slider-sub-area',
  subTable: '.slider-sub-table',

  slideExtraLength: -1,
  slideExtraTop: 10,
  subLayerWidth: 450
});


$(function(){
//    $("#table tr").eq(1).nextAll().hide();
//    $("#clickShow").toggle(function(){
//            $(".supplierDetailInfo").show();
//            $(this).html("收起");
//    },function(){
//            $(".supplierDetailInfo").hide();
//        $(this).html("详细");
//    });
//    $("#clickShow").hover(function(){
//      $(".tuihuo_first table tr .xiangxi_td a #clickShow").css("color","#FD5300");
//    },function(){
//      $(".tuihuo_first table tr .xiangxi_td a #clickShow").css("color","#007CDA");
//    });
//    initDuiZhanInfo();
});

//获得查询字符串数组
var queryString = (function(a) {
    if(a == "") return {};
    var b = {};
    for(var i = 0; i < a.length; ++i) {
        var p = a[i].split('=');
        if(p.length != 2) continue;
        b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
    }
    return b;
})(window.location.search.substr(1).split('&'));

//init 入库单验证器
$(function() {
    var rules = {
        supplier: {
            required: true,
            maxlength: 30
        },
        mobile: {
            mobile: true
        }
    }
    var messages = {
        supplier: {
            required: "请输入供应商",
            maxlength: "供应商有效长度30"
        },
        mobile: {
            mobile: "输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！"
        }
    }
    bcgogoValidator.setRules(rules);
    bcgogoValidator.setMessages(messages);
    //    bcgogoValidator.setConfirmMessage("是否确认入库?");
    bcgogoValidator.setDisabledDom($("#printBtn,#cancelBtn,#saveDraftBtn"));
    bcgogoValidator.validate($("#purchaseInventoryForm")[0]);
});

//TODO 入库单提交

function inventoryOrderSubmit() {
    initBusinessScope();
    idPrefixLastModified = null;
    if($("#checkDetailPrint").attr("checked")) {
        $("#purchaseInventoryForm").attr('action', 'storage.do?method=savePurchaseInventory&print=true');
    } else {
        $("#purchaseInventoryForm").attr('action', 'storage.do?method=savePurchaseInventory');
    }
    submitFlag = "ruku";
    if(flag == 1) {
        $("#purchaseInventoryForm").submit();
        $("#inventorySaveBtn").attr("isSubmit",true);
    }
}

//TODO 用于在入库单中添加新的一行，比如加号按钮
function inventoryOrderAdd() {
    idPrefixLastModified = null;
    var tr = $(getTrSample()).clone(); //TODO 克隆模版，初始化所有的INPUT
    $(tr).find("input").val("");
    $(tr).find(".itemProductVehicleStatus").val("1");
    $(tr).find(".itemAmount,.itemPurchasePrice,.itemTotal").bind("blur", function() {
        dataTransition.roundingSpanNumber("totalSpan");
        var format = $(this).val();
        format = dataTransition.rounding(format, 2);
        $(this).val() != '' && $(this).val(format);
        setTotal();
    });
    $(tr).find("input,span,a").each(function(i) {
        //去除文本框的自动填充下拉框
        if($(this).attr("type") == "text") {
            $(this).attr("autocomplete", "off");
        }

        //TODO ID为空则跳过
        if(!this || !this.id) return;
        var idStrs = this.id.split(".");
        var idSuffix = idStrs[1];

        var tcNum = trCount;
        if(G.isEmpty(tcNum)){
            tcNum=0;
            trCount=0;
        }
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
        //TODO <-- End
        //        $(this).bind('keyup', function(e) {
        //            //过滤非法字符
//                    var data = dataTransition.stripHTML($(this).val());
        //            $(this).val(data);
//                    domKeyUp(this, e);
        //        });
        //        $(this).bind('click', function() {
        //            domClick(this);                  //TODO 下拉建议，查询所有结果
        //        });
        //        $(this).bind('change', function() {
        //            modify(this);                     //TODO 当旧列的商品信息发生变化时，弹出库存明细，让用户确认
        //        });
    });

    $(tr).appendTo("#table_productNo");


    var tr2 = $(getTrSample2()).clone(); //TODO 克隆模版，初始化所有的INPUT
    $(tr2).find("input").val("");
    $(tr2).find(".itemProductVehicleStatus").val("1");
    $(tr2).find("input,span").each(function(i) {
        //去除文本框的自动填充下拉框
        if($(this).attr("type") == "text") {
            $(this).attr("autocomplete", "off");
        }

        //TODO ID为空则跳过
        if(!this || !this.id) return;
        var idStrs = this.id.split(".");
        var idSuffix = idStrs[1];

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
    $(tr2).appendTo("#table_productDetail");
    trCount++;

    isShowAddButton();

    //动态设置每行的高度
    $('#table_productNo tr:not(:first)').each(function(i) {
        $('#table_productDetail tr:eq(' + i + ')').height($(this).outerHeight());
    });

    mySlider.setSubLayerHeight();
    mySlider.setRowHeight();

    //初始化时,是否有带跳转的产品编号
    var _scanning = queryString['scanning'],
        _productId = queryString['productIds'];
    if(_scanning && _productId && _scannerFlag) {
        var $firstCommodityCode = $('input[id$="commodityCode"]:first');
        $firstCommodityCode.val(_productId);
        commodityCodeBlur($firstCommodityCode);
        _scannerFlag = false;
    }

    return $(tr);
}
var _scannerFlag = true
$(document).ready(function() {
    var repairOrderPermission = jQuery("#repairOrderPermission").val();
    if(repairOrderPermission == "true" && $("#repairOrderId").val() != '') {
        $("#return_to_repair_div").css("display","block");
    }
    String.prototype.replaceAll = function(s1, s2) {
        return this.replace(new RegExp(s1, "gm"), s2);
    }
    trCount = $(".item").size();
    //1跳转到指定的维修单
    if(returnType == 1 && repairOrderPermission == "true") {
        $("#purchaseInventoryForm").attr('action', 'storage.do?method=updateLackGood&returnType=1');
        bcgogoValidator.setConfirmMessage(null);
        if($("#repairOrderId").val()) {
            $("#purchaseInventoryForm").ajaxSubmit({
                dataType: 'json',
                success: function(data) {
                    if(data.result == 'success') {
                        return false;
                    } else {
                        window.location.href = data.result;
                    }
                }
            });
        }
    }
    //2不跳转，提示缺料信息
    else if(returnType == 2) nsDialog.jAlert('来料不足，请继续入库');
    //3出现来料待修提示页面
    else if(returnType == 3) {
        bcgogo.checksession({
            "parentWindow": window.parent,
            'iframe_PopupBox': $("#iframe_PopupBox")[0],
            'src': "storage.do?method=showLackGoods" + "&productIds=" + productIds
        });
    } else {
        if(trCount == 1 && repairOrderId != '' && productAmount != '' && $(".item").first().find(".itemInventoryAmount").val() >= productAmount && $(".item").first().find("input[name$='productId']").val() == productIds) {
            if(confirm("库存已经满足，是否需要修改维修单缺料?")) {
                $("#purchaseInventoryForm").attr('action', 'storage.do?method=updateLackGood&returnType=1');
                $("#purchaseInventoryForm").ajaxSubmit({
                    dataType: 'json',
                    success: function(data) {
                        if(data.result == 'success') {
                            return false;
                        } else {
                            window.location.href = data.result;
                        }
                    }
                });
            }
        }
    }

    //供应商栏
    $(document).bind("keydown", function(event) {
        var target = event.target;
        if($(target).hasClass("supplierSuggestion")) return;
        if(target.type != "text" && !$(target).hasClass("j_btn_i_operate$")) {
            return;
        }
        if($(target).attr("id") && $(target).attr("id").endWith(".commodityCode")) return;
        var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
        if(keyName.search(/enter|right|left/g) != -1) {
            if($("#div_brandvehiclelicenceNo").css("display") != "none") {
                return;
            }
            invoiceCommon.keyBoardSelectTarget(event.target, keyName);
        }
    });

    $("#table_productNo").bind("keydown", function(event) {
        var target = event.target,
            isMatchProductName = false;
        $("#table_productNo input[id$='\\.productName']").each(function(index) {
            if(target.id == this.id) isMatchProductName = true;
        });
        if(isMatchProductName == false) return;

        var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
        //TODO 当库存明细页面显示时，按空格隐藏,否则显示库存页面
        if(keyName == "space") {
            //            if ($("#iframe_PopupBox").css("display") == "block") {
            //                $("#mask").hide();
            //                $("#iframe_PopupBox").hide();
            //            } else {
            //                $("#div_brand").hide();
            //                searchInventoryIndex(target);
            //            }
            //            return false;
        }
    });
    //TODO <--  End
    //button Enter键绑定
    //TODO 确认入库、打印、取消，被选择后回车，则单击该元素
    $(document).bind("keydown", function(event) {
        if(!$(event.target).hasClass("j_btn_i_operate$")) {
            return;
        }
        var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
        if(keyName == "space") {
            $(event.target).click();
        }
    });

    $("#mobile").blur(function() {
        if(this.value) {
            var landline = document.getElementById("landline");
            var mobile = document.getElementById("mobile");
            if(mobile.value != "" && mobile.value != null) {
                check.inputSupplierMobileBlur2(mobile, landline);
            }
        }
    });

    $("#inventorySaveBtn").click(function() { //TODO “确认入库”按钮被单击后的处理脚本
        var $tbody = $("#table_productNo").find("tbody"),
            $last_tr = $tbody.find("tr:last"),
            $tbody_item = $tbody.find("tr").not(".table_title");
        while(checkEmptyRow($last_tr) && $tbody_item.index($last_tr) >= 2) {
            $last_tr.find("[a[id$='.deletebutton']").click();
            $last_tr = $tbody.find("tr:last");
//            $tbody_item = $tbody.find("tr").not(".table_title");
        }
        if ($(this).attr("isSubmit")) {
            return;
        }

        if(checkSupplierInfo()==false){
            return;
        }



        //自动删除最后的空白行
        if($("#id").val()) {
            bcgogo.checksession({
                "parentWindow": window.parent,
                'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
                'src': 'storage.do?method=payDetail&supplierId=' + $("#supplierId").val() + "&id=" + $("#id").val()
            });
            return;
        }

        if(!APP_BCGOGO.Verifier.isShopIdCorrect) {
            return;
        }

        if(!validateAll()) {
            return false;
        }
        if(!checkSameCommodityCodeForPurchaseInventory()){
            return false;
        }
        if(false) { //入库单不做重复行校验
        if(trCount >= 2) if(checkTheSame()) {
            nsDialog.jAlert("单据有重复内容，请修改或删除。");
            return false;
        }
        if(invoiceCommon.checkSameCommodityCode("item")) {
            nsDialog.jAlert("商品编码有重复内容，请修改或删除。");
            return false;
        }
        }
        if(!validateNewProductMultiUnit()){
            return false;
        }

        flag = 1;
        //验证为null的置为空
        invoiceCommon.reSetDomVal($("#invoiceCategory")[0], "null", "");
        invoiceCommon.reSetDomVal($("#settlementType")[0], "null", "");

        //验证手机号码
        if($.trim($("#mobile").val())) check.saveContact(document.getElementById("mobile"));

        //TODO 库存量为空字符，则赋上默认值
        $(".itemInventoryAmount").each(function() {
            invoiceCommon.reSetDomVal(this, "", 0);
        });
        //todo 验证    应付=实付+扣款+挂账
        //        if ($("#payDetail").css("display") == "none") {
        //            if (!validatePayable()) {
        //                return false;
        //            }
        //        }
        //        $("#div_brand").hide();
        //        if (idPrefixLastModified == null) {
        //            inventoryOrderSubmit();
        //            return false;
        //        }
        //        operation = "inventoryOrderSubmit";
        //        exactSearchInventorySearchIndex();
        if(!$("#supplier").val()) {
            nsDialog.jAlert("请输入供应商");
            return;
        }

        if($("#supplier").val().length > 30) {
            nsDialog.jAlert("供应商长度不能大于30");
            return;
        }
        $("#div_brand").hide();

        initSameProductInfo();
        $("#btnType").val('inventorySaveBtn');
        if(!checkStorehouseSelected()) {
            return;
        }
        //iframe 方法有两份，维护修改的时候请注意
        if ($("#supplier").val() && !$.trim($("#mobile").val())) {
            $("#inputMobile").dialog("open");
            return;
        }

        $("#purchaseInventoryForm").ajaxSubmit({
            url: "storage.do?method=ajaxValidatorPurchaseInventoryDTOSave",
            dataType: "json",
            type: "POST",
            success: function(json) {
                if(json.success) {
                    $("#saveDraftBtn").attr('disabled', true);
                    bcgogo.checksession({
                        "parentWindow": window.parent,
                        'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
                        'src': 'storage.do?method=payDetail&supplierId=' + $("#supplierId").val() + "&id=" + $("#id").val() + "&purchaseOrderId=" + $("#purchaseOrderId").val()
                    });
                } else if(!json.success && json.operation == "confirm_deleted_product") {
                    if(confirm(json.msg)) {
                        $("#saveDraftBtn").attr('disabled', true);
                        bcgogo.checksession({
                            "parentWindow": window.parent,
                            'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
                            'src': 'storage.do?method=payDetail&supplierId=' + $("#supplierId").val() + "&id=" + $("#id").val() + "&purchaseOrderId=" + $("#purchaseOrderId").val()
                        });
                    }
                } else if(!json.success) {
                    nsDialog.jAlert(json.msg);
                    return false;
                }
            },
            error: function(json) {
                nsDialog.jAlert("网络异常，请联系客服");
            }
        });


        //        return false;
    });


    //关闭按钮    //TODO 取消按钮，重载页面
    $("#cancelBtn").click(function() {
        if($("#id").val()) {
            window.location = "storage.do?method=getProducts&type=txn";
        } else {
            window.location = "storage.do?method=getProducts&type=txn&cancle=noId&receiptNo=" + $("#receiptNo").val();
        }

    });
    //start验证    //TODO 利用$验证框架，添加验证规则
    //TODO 根据入库价和数量计算“小计”，并将“小计”四舍五入
//    $(document).bind('keyup', function(event) {
//        if(!$(event.target).hasClass("itemPurchasePrice") && !$(event.target).hasClass("itemAmount")) {
//            return;
//        }
//        setTotal();
//    });


    //增加行    //TODO 加号按钮单击触发的脚本
    $(document).bind('click', function(event) {
        var target = event.target;
        if(target.className != "opera2") return;
        //TODO 在旧单据中不允许新增一行
        if($("#id").val()) {
            return false;
        }

        var ischeck = checkVehicleInfo(target); //TODO 检查改行数据是否符合验证规则
        if(!ischeck && ischeck != null) {
            return;
        }
        //入库单检查是否相同
        if(false) {
        if(trCount >= 2) if(checkTheSame()) {
            nsDialog.jAlert("单据有重复内容，请修改或删除。");
            return false;
        }
        if(invoiceCommon.checkSameCommodityCode("item")) {
            nsDialog.jAlert("商品编码有重复内容，请修改或删除。");
            return false;
        }
        }

        if($(target).prev() && $(target).prev().attr("id")) {
            $("#div_brand").hide();
            if(idPrefixLastModified == null) {
                inventoryOrderAdd();
                return false;
            }
            operation = "inventoryOrderAdd";
            exactSearchInventorySearchIndex();
            return false;
        } else {
            inventoryOrderAdd();
            return false;
        }
    });
    //删除行      //TODO 点击减号所做的删除改行的操作
    $(document).bind('click', function(event) {
        var target = event.target;
        if(target.className != "opera1") {
            return;
        }
        if($("#id").val() != '') {
            return false;
        }

        var idPrefix = $(target).attr("id").split(".")[0];

        if(idPrefixLastModified == idPrefix) {
            idPrefixLastModified = null;
        }
        $(target).closest("tr").remove();
        $("#" + idPrefix + "\\.recommendedPrice").closest("tr").remove();
        isShowAddButton();
        setTotal();
        trCount = $(".item").size();

        //动态设置每行的高度
        $('#table_productNo tr:not(:first)').each(function(i) {
            $('#table_productDetail tr:eq(' + i + ')').height($(this).outerHeight());
        });

        mySlider.setSubLayerHeight();
        mySlider.setRowHeight();

        if($("#contactId").val()){
            contactDeal(true); // @see suggestion.js
        }else{
            contactDeal(false); // @see suggestion.js
        }
    });

    //初始化的时候判断是否显示+按钮
    isShowAddButton();

    //判断单据是否已经生成，如果已经生成将所有input锁定
    if($("#id").val()) {
        $("#saveDraftOrder_div").each(function() {
            $(this).hide();
        });
        $("#purchaseInventoryForm input").not($("#storageDebt,#printBtn,#print#cancelBtn,#nullifyBtn,#copyInput,#inventorySave_div,#returnStorageBtn")).each(function() {
            $(this).attr("disabled", true);
        });
        $("#storageDebt,#printBtn,#cancelBtn,#nullifyBtn,#print,#copyInput,#inventorySave_div","#returnStorageBtn").each(function() {
            $(this).attr("disabled", false);
        });

        $("#inventorySave_div").removeAttr("disabled");
        $("#inventorySaveBtn").removeAttr("disabled");
    }

    //去打印页面
    $("#printBtn").click(function() {
        if($("#id").val()) {
            window.open("storage.do?method=getPurchaseInventoryToPrint&purchaseInventoryId=" + $("#id").val(), '', "dialogWidth=1024px;dialogHeight=768px");
            return;
        }
        if($("#draftOrderIdStr").val()) {
            window.showModalDialog("draft.do?method=getDraftOrderToPrint&id=" + $("#draftOrderIdStr").val() + "&type=INVENTORY&now=" + new Date());
        }
    });

    if($("#print").val() == "true" && $("#id").val()) {
        $("#printBtn").click();
    }

    if($("#status").val() == 'PURCHASE_INVENTORY_DONE') {
        $(".invalidImg").show();

        if ($("#statementAccountOrderId").val()) {
          jQuery("#zuofei").removeClass("zuofei").addClass("statement_accounted").show();
          $(".invalidImg").hide();
        } else {
          if ($("#guazhang").html().replace("元", '') * 1 > 0) {
            $("#zuofei").removeClass("zuofei").addClass("debtRuKu").show();
          } else {
            $("#zuofei").removeClass("zuofei").addClass("ruku").show();
          }
        }
    } else if($("#status").val() == 'PURCHASE_INVENTORY_REPEAL') {
        $("#zuofei").show();
        $(".invalidImg").hide();
    } else if($("#status").val() == '') {
        $("#zuofei").hide();
        $(".invalidImg").hide();
    }

    //入库单作废按钮事件
    $("#nullifyBtn").bind('click', function(event) {
        /*判断次单据是否付款过
         * 如果付款过则供应商退款
         * */
        var paramJson = {
            purchaseInventoryId: $("#id").val()
        }
        APP_BCGOGO.Net.asyncPost({
            url: "storage.do?method=validateRepealStorageOrder",
            data: paramJson,
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    nsDialog.jConfirm("友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！", null, function (returnVal) {
                        if (returnVal) {
                            window.location = "storage.do?method=cancelPurchaseInventory&purchaseInventoryId=" + $("#id").val() + "&returnMoneyType=" + $("#returnMoneyType").val();
                        }
                    });
                } else {
                    if (result.operation == "update_product_inventory") {
                        $(".j_table_productNo").find(".item").each(function () {
                            var productId = $(this).find("input[id$='.productId']").val();
                            var storageUnit = $(this).find("input[id$='.storageUnit']").val();
                            var sellUnit = $(this).find("input[id$='.sellUnit']").val();
                            var rate = $(this).find("input[id$='.rate']").val();
                            var unit = $(this).find("input[id$='.unit']").val();
                            if (!G.Lang.isEmpty(productId)) {
                                var inventoryAmount = G.normalize(result.data[productId], "0")*1;
                                if(isStorageUnit(unit,sellUnit,storageUnit,rate)){
                                    inventoryAmount = inventoryAmount / rate;
                                }
                                $(this).find("input[id$='.inventoryAmount']").val(inventoryAmount);
                            }
                        });
                        checkInventoryAmount();
                    } else {
                        nsDialog.jAlert(result.msg);
                    }
                }
            }
        });
    });
    if(!GLOBAL.Lang.isStringEmpty(GLOBAL.Util.getUrlParameter("repealOperation")) && GLOBAL.Util.getUrlParameter("repealOperation") == 'true') {
        $("#nullifyBtn").trigger("click");
    }

    //显示复制按钮
    if(!GLOBAL.Lang.isEmpty($("#id").val())) {
        $(".copyInput_div").show();
    }

    $("#copyInput").bind("click", function() {
        if(GLOBAL.Lang.isEmpty($("#id").val())) {
            nsDialog.jAlert("单据ID不存在，请刷新后重试");
            return false;
        }
        APP_BCGOGO.Net.syncPost({
            url:"storage.do?method=validateCopy",
            dataType:"json",
            data:{"purchaseInventoryId" : $("#id").val()},
            success:function(result){
                if(result.success){
                    window.location = "storage.do?method=copyPurchaseInventory&purchaseInventoryId=" + jQuery("#id").val();
                }else{
                    if(result.operation == 'nsDialog.jAlert'){
                        nsDialog.jAlert(result.msg, result.title);
                    }else if(result.operation == 'CONFIRM'){
                        nsDialog.jConfirm(result.msg, result.title, function(resultVal){
                            if(resultVal){
                                window.location = "storage.do?method=copyPurchaseInventory&purchaseInventoryId=" + jQuery("#id").val();
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
    /**
     * 选择现金
     */
    jQuery("#sureCash").bind("click", function() {
        jQuery("#returnMoneyType").val("cash");
        if(checkInventoryAmount() && $("#returnMoneyType").val() != "") {
            if(confirm("友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！")) {
                window.location = "storage.do?method=cancelPurchaseInventory&purchaseInventoryId=" + $("#id").val() + "&returnMoneyType=" + $("#returnMoneyType").val();
            }
        } else {
            return;
        }
        jQuery("#sureCashOrDeposit").css("display", "none");
        jQuery("#mask").css("display", "none");
    });
    /**
     * 选择定金
     */
    jQuery("#sureDeposit").bind("click", function() {
        jQuery("#returnMoneyType").val("deposit");
        if(checkInventoryAmount() && $("#returnMoneyType").val() != "") {
            if(confirm("友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！")) {
                window.location = "storage.do?method=cancelPurchaseInventory&purchaseInventoryId=" + $("#id").val() + "&returnMoneyType=" + $("#returnMoneyType").val();
            }
        } else {
            return;
        }
        jQuery("#sureCashOrDeposit").css("display", "none");
        jQuery("#mask").css("display", "none");
    });
    /*关闭选择现金或定金弹出框
     *
     * */
    jQuery("#stroageActuallyPaid,#stroageCreditAmount,#stroageSupplierDeduction").bind("keyup", function() {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(),2));
    });

    /**
     * 输入验证
     *
     */
    jQuery("#sure_div_close").bind("click", function() {
        jQuery("#sureCashOrDeposit").css("display", "none");
        jQuery("#mask").css("display", "none");
    });


    $("#saveDraftBtn").bind('click', function(event) {
        if(isLegalTxnDataLength() && openNewOrderPage()) {
            $("#saveDraftBtn").attr("disabled", true);
            $("#purchaseInventoryForm").ajaxSubmit({
                url: "draft.do?method=savePurchaseInventoryDraft",
                dataType: "json",
                type: "POST",
                success: function(data) {
                    showMessage.fadeMessage("45%", "34%", "slow", 300, "草稿保存成功！" + getCurrentTime());
                    $("#draftOrderIdStr").val(data.idStr);
                    $("#saveDraftBtn").attr("disabled", false);
                    if(!G.isEmpty(data)){
                        $("#receiptNoSpan").text(data.receiptNo);
                        $("#receiptNo").val(data.receiptNo);
                        $("#print_div").show();
                    }
                },
                error: function() {
                    showMessage.fadeMessage("45%", "34%", "slow", 300, "保存草稿异常！" + getCurrentTime());
                    $("#saveDraftBtn").attr("disabled", false);
                }
            });
        }
    });

    // 实付，挂账双击事件
    $("#stroageActuallyPaid").bind("dblclick", function() {
        $("#stroageActuallyPaid").val(parseFloat($("#totalSpan").html()));
        $("#stroageCreditAmount").val(0);
        $("#stroageSupplierDeduction").val(0);
    });

    $("#stroageCreditAmount").bind("dblclick", function() {
        $("#stroageActuallyPaid").val(0);
        $("#stroageCreditAmount").val(parseFloat($("#totalSpan").html()));
        $("#stroageSupplierDeduction").val(0);
    });

    $(".tradePriceCheck").live("keyup", function() {
        if($(this).val()!= APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2)) {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2));
        }
    });

    $(".tradePriceCheck").live("blur", function() {
        var tradePrice = dataTransition.rounding($(this).val(), 2);
        $(this).val(tradePrice);
        var idPrefix = $(this).attr("id").split(".")[0];
        if(G.Lang.isEmpty($("#" + idPrefix + "\\.productName").val()) || $(this).val()==$(this).attr("lastValue")){
            return;
        }
        if(tradePrice<=0){
            nsDialog.jAlert("请输入正确的批发价！");
            $(this).val($(this).attr("lastValue"));
            return;
        }
        var inventoryAveragePrice = dataTransition.rounding($("#" + idPrefix + "\\.inventoryAveragePrice").val(),2);
        if(tradePrice<inventoryAveragePrice){
            showMessage.fadeMessage("35%", "40%", "slow", 2000, "该商品的批发价低于成本价" + inventoryAveragePrice + "元,请确认.");
        }
    });

    $(".tradePriceCheck").live("focus", function() {
        $(this).attr("lastValue", $(this).val());
    });

    $(".priceCheck").live("keyup", function() {
        if($(this).val != APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2)) {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2));
        }
    });

    $("#hiddenSubmitClick").live("click", function() {

        $("#div_brand").hide();
        if(idPrefixLastModified == null) {
            $("#purchaseInventoryForm").ajaxSubmit({
                url: "storage.do?method=ajaxValidatorPurchaseInventoryDTOSave",
                dataType: "json",
                type: "POST",
                success: function (json) {
                    if (json.success) {
                        inventoryOrderSubmit();
                    } else if (!json.success && json.operation == "confirm_deleted_product") {
                        if (confirm(json.msg)) {
                            inventoryOrderSubmit();
                        }

                    } else if (!json.success && json.operation == "deposit_lack") {
                      nsDialog.jAlert(json.msg);

                      $("#depositAmount").val("");
                      return false;
                    } else if (!json.success) {
                      nsDialog.jAlert(json.msg);
                      return false;
                    }
                },
                error: function (json) {
                    nsDialog.jAlert("网络异常，请联系客服");
                }
            });
            return false;
        }
        operation = "inventoryOrderSubmit";
        exactSearchInventorySearchIndex();
    });

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
        },
        open:function(){
            $("#divMobile").val("");
        }
    });

    $("#return_to_repair_div").click(function(){
        window.location.href = 'txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + $("#repairOrderId").val();
    });

    //设定联系人相关的input框为readonly
    contactDeal(!G.isEmpty($("#supplierId").val()) && !G.isEmpty($("#contactId").val()));

});

function getTrSample() {
    var permissionInventoryAlarmSettings = jQuery("#permissionInventoryAlarmSettings").val();
    var permissionSalePriceSettings = jQuery("#permissionSalePriceSettings").val();
    var trSample = '<tr class="bg item table-row-original">';
    trSample += '<td style="border-left:none;">' + '<input id="itemDTOs0.commodityCode" name="itemDTOs[0].commodityCode" type="text" class="table_input checkStringEmpty" value="" maxlength="20"/>' + '</td>';
    trSample += '<td>' + '<input id="itemDTOs0.id" name="itemDTOs[0].id" type="hidden" value=""/>' + '<input id="itemDTOs0.productVehicleStatus" name="itemDTOs[0].productVehicleStatus" value="" type="hidden" class="itemProductVehicleStatus"/>'
        + '<input id="itemDTOs0.hidden_productVehicleStatus" name="itemDTOs[0].hidden_productVehicleStatus" value="" type="hidden" class="itemProductVehicleStatus"/>' + '<input id="itemDTOs0.isOldProduct" name="itemDTOs[0].isOldProduct" type="hidden" value=""/>'
        + '<input id="itemDTOs0.vehicleBrandId" name="itemDTOs[0].vehicleBrandId" value="" type="hidden"/>' + '<input id="itemDTOs0.vehicleModelId" name="itemDTOs[0].vehicleModelId" value="" type="hidden"/>'
        + '<input id="itemDTOs0.vehicleYearId" name="itemDTOs[0].vehicleYearId" value="" type="hidden"/>' + '<input id="itemDTOs0.vehicleEngineId" name="itemDTOs[0].vehicleEngineId" value="" type="hidden"/>'
        + '<input id="itemDTOs0.productId" name="itemDTOs[0].productId" value="" type="hidden"/>' + '<input id="itemDTOs0.hidden_productId" name="itemDTOs[0].hidden_productId" value="" type="hidden"/>'
        + '<input id="itemDTOs0.productName" name="itemDTOs[0].productName" class="table_input checkStringEmpty" value="" type="text" style="width:85%"/>'
        + '<input type="hidden" name="itemDTOs[0].hidden_productName" id="itemDTOs0.hidden_productName" />'
        + '<input type="hidden" id="itemDTOs0.lastPrice" name="itemDTO[0].lastPrice"/>'
        + '<input type="hidden" id="itemDTOs0.lastPurchasePrice" name="itemDTO[0].lastPurchasePrice"/>'
        + '<input type="button" class="edit" onfocus="this.blur();" id="itemDTOs0.editbutton" name="itemDTOs[0].editbutton" onclick="searchInventoryIndex(this)" />' + '</td>';
    trSample += '<td>' + '<input id="itemDTOs0.brand" name="itemDTOs[0].brand" class="table_input checkStringEmpty" value="" type="text"/>' + '<input type="hidden" name="itemDTOs[0].hidden_brand"  id="itemDTOs0.hidden_brand"  /></td>';
    trSample += '<td>' + '<input id="itemDTOs0.spec" name="itemDTOs[0].spec" class="table_input checkStringEmpty" value="" type="text"/>' + '<input type="hidden" name="itemDTOs[0].hidden_spec" id="itemDTOs0.hidden_spec" /></td>'
        + '<td>' + '<input id="itemDTOs0.model" name="itemDTOs[0].model" class="table_input checkStringEmpty" value="" type="text"/>' + '<input type="hidden" name="itemDTOs[0].hidden_model" id="itemDTOs0.hidden_model" /></td>'
        + '<td><input id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel" class="table_input checkStringEmpty" maxlength="200" type="text"/>'
        + '<input type="hidden" id="itemDTOs0.hidden_vehicleModel" name="itemDTOs[0].hidden_vehicleModel" /></td>'
        + '<td><input id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand" class="table_input checkStringEmpty" maxlength="200" type="text"/>'
        + '<input type="hidden" id="itemDTOs0.hidden_vehicleBrand" name="itemDTOs[0].hidden_vehicleBrand" /></td>'
        + '<td style="display:none"><input id="itemDTOs0.vehicleYear" name="itemDTOs[0].vehicleYear" class="table_input" value="" type="text"/>'
        + '<input type="hidden" id="itemDTOs0.hidden_vehicleYear" name="itemDTOs[0].hidden_vehicleYear" /></td>'
        + '<td style="display:none"><input id="itemDTOs0.vehicleEngine" name="itemDTOs[0].vehicleEngine" class="table_input" value="" type="text"/>'
        + '<input type="hidden" id="itemDTOs0.hidden_vehicleEngine" name="itemDTOs[0].hidden_vehicleEngine" /></td>'
        + '<td style="color:#FF6700;">'
        + '<input id="itemDTOs0.purchasePrice" name="itemDTOs[0].purchasePrice" value="" class="itemPurchasePrice table_input checkNumberEmpty" type="text" data-filter-zero="true"/>' + '</td>'
        + '<td style="color:#FF0000;">' + '<input id="itemDTOs0.amount" name="itemDTOs[0].amount" value="" class="itemAmount table_input checkNumberEmpty" type="text" data-filter-zero="true"/>'
        + '<input id="itemDTOs0.amountHid" name="itemDTOs[0].amountHid" class="amountHid" value="" type="hidden"/>' + '</td>'
        + '<td>' + '<input id="itemDTOs0.unit" name="itemDTOs[0].unit" value="" class="itemUnit table_input checkStringEmpty" style="width: 80%" type="text"/>'
        + '<input type="hidden" id="itemDTOs0.storageUnit" name="itemDTOs[0].storageUnit" value="" class="itemStorageUnit table_input"/>'
        + '<input type="hidden" id="itemDTOs0.sellUnit" name="itemDTOs[0].sellUnit" value="" class="itemSellUnit table_input"/>'
        + '<input type="hidden" id="itemDTOs0.rate" name="itemDTOs[0].rate" value="" class="itemRate table_input"/>' + '</td>'
        + '<td>' + '<span id="itemDTOs0.total_span" name="itemDTOs[0].total" class="itemTotalSpan"></span><input type="hidden" id="itemDTOs0.total" name="itemDTOs[0].total" class="itemTotal" />'
        + '<td>' + '<span id="itemDTOs0.inventoryAmountSpan" name="itemDTOs[0].inventoryAmountSpan" style="display: block;"></span>'
        + '<input type="hidden" id="itemDTOs0.inventoryAmount" name="itemDTOs[0].inventoryAmount" value="" class="itemInventoryAmount table_input" readonly="readonly" type="text"/>'
        + '<span style="display: none;">新</span>' + '</td>';
    trSample += '<td style="border-right:none;">' + '<a class="opera1" id="itemDTOs0.deletebutton" name="itemDTOs[0].deletebutton">删除</a>' + '</td>';
    trSample += '</tr>';
    return trSample;
}


function getTrSample2() {
    var permissionInventoryAlarmSettings = $("#permissionInventoryAlarmSettings").val();
    var permissionSalePriceSettings = $("#permissionSalePriceSettings").val();
    var trSample = '<tr class="item2 table-row-original">';
    trSample += '<td class="storage_bin_td">' + '<input id="itemDTOs0.storageBin" name="itemDTOs[0].storageBin" value="" class="table_input" type="text" style="width:90%"/> ' + '</td>';
    trSample += '<td class="txt_right">' + '<input id="itemDTOs0.productKind" name="itemDTOs[0].productKind" maxlength="50" value="" class="table_input checkStringEmpty" type="text" style="width:90%"/> ' + '</td>';
    if(permissionInventoryAlarmSettings == "true") {
        trSample += '<td class="txt_right">' + '<input type="text" id="itemDTOs0.lowerLimit" class="table_input order_input_lowerLimit checkNumberEmpty" name="itemDTOs[0].lowerLimit" value="" style="width:100%;">' + '</td>' + '<td class="txt_right">' + '<input type="text" id="itemDTOs0.upperLimit" class="table_input order_input_upperLimit checkNumberEmpty" name="itemDTOs[0].upperLimit" value="" style="width:100%;">' + '</td>';
    } else {
        trSample += '<td class="txt_right">' + '<input type="text" id="itemDTOs0.lowerLimit" class="table_input order_input_lowerLimit checkNumberEmpty" name="itemDTOs[0].lowerLimit" disabled="true" value="" style="width:100%;">' + '</td>' + '<td class="txt_right">' + '<input type="text" id="itemDTOs0.upperLimit" class="table_input order_input_upperLimit checkNumberEmpty" name="itemDTOs[0].upperLimit" disabled="true" value="" style="width:100%;">' + '</td>';
    }
    if($("#permissionGoodsSale").val() == "true") {
        trSample += '<td class="trade_price_td">' + '<input id="itemDTOs0.tradePrice" name="itemDTOs[0].tradePrice" value="" class="table_input checkNumberEmpty tradePriceCheck"  type="text" style="width:90%"/>' + '<input id="itemDTOs0.inventoryAveragePrice" value="" class="table_input"  type="hidden" />'+ '</td>';
    }
    if(permissionSalePriceSettings == "true") {
        trSample += '<td>' + '<input id="itemDTOs0.recommendedPrice" name="itemDTOs[0].recommendedPrice" class="itemRecommendedPrice table_input checkNumberEmpty priceCheck" type="text" value=""  />' + '</td>';
    }
    trSample += '</tr>';
    return trSample;

}

// TODO 初始化 、 判断 、 删除 ， 不能混在一起 ，这个方法  功能和名字相距甚远， 暴汗， 得拆分, 并且此方法得重命名
//判断是否显示+按钮

function isShowAddButton() {
    // TODO set root node , then check if table>tr is 0?   then do something .
    //如果初始化的话就默认加一行
    if($("tr[class~='item']").size() <= 0) {
//        $(".opera2").trigger("click");
        $(".opera2").click();
//        inventoryOrderAdd();
    }
    $(".item .opera2").remove();
    var opera1Id = $(".item:last").find("td:last>a[class='opera1']").attr("id");
    if(!opera1Id) {
        return;
    }
    $(".item:last").find("td:last>a[class='opera1']").after('<a class="opera2" ' + ' id="itemDTOs' + (opera1Id.split(".")[0].substring(8)) + '.plusbutton">增加</a>');
}

//TODO 获得总金额（“合计”）

function getServiceTotal() {
    var count = 0;
    $(".itemTotal").each(function(i) {
        var txt = $(this);
        if($.trim(txt.val())) {
            count += parseFloat(txt.val());
        }
    });
    return count;
}

//TODO 计算“小计”

function setItemTotal() {
    $(".itemPurchasePrice").each(function(i) {
        var purchasePrice = $(this).val();
        if(!purchasePrice) {
            purchasePrice = "0";
        }
        if($.trim(purchasePrice)) {
            purchasePrice = G.rounding(purchasePrice, 2)
            $(".itemPurchasePrice").eq(i).val() != '' && $(".itemPurchasePrice").eq(i).val(purchasePrice);
        }
        var idPrefix = $(this).attr("id").split(".")[0];
        var amount = G.rounding($("#" + idPrefix + "\\.amount").val(), 2);
        $("#" + idPrefix + "\\.amount").val() != '' && $("#" + idPrefix + "\\.amount").val(amount)
        var count = parseFloat(purchasePrice * amount);
        count = G.rounding(count, 2);
        if ($(this).val() == '') {
            $("#" + idPrefix + "\\.total_span").text('');
            $("#" + idPrefix + "\\.total").val(0);
            $("#" + idPrefix + "\\.total_span").attr("title", '');
        } else {
            $("#" + idPrefix + "\\.total_span").text(count);
            $("#" + idPrefix + "\\.total").val(count);
            $("#" + idPrefix + "\\.total_span").attr("title", count.toString());
        }
        htmlNumberFilter($(this).add("#" + idPrefix + "\\.amount").add("#" + idPrefix + "\\.total_span").add("#" + idPrefix + "\\.inventoryAmountSpan"), true);
    });
}
//TODO 将“总计”加入页面元素

function setTotal() {
    setItemTotal();
    var count = dataTransition.rounding(getServiceTotal(), 2);
    if(count != $("#total").val()) {
        $("#totalSpan").text(count);
        $("#total").val(count);
        //modified by WeiLingfeng 默认挂账额等于合计额
        $("#stroageCreditAmount").val("");
        $("#stroageActuallyPaid").val(count);
    }
}
//TODO 打开历史记录页面，需要带入供应商

function getGoodsHistory(supplierName) {
    bcgogo.checksession({
        "parentWindow": window.parent,
        'iframe_PopupBox': $("#iframe_PopupBox")[0],
        'src': "goodsHistory.do?method=createGoodsHistory&orderType=INVENTORY&supplierName=" + encodeURIComponent(supplierName)
    });
}

function checkTheSame() {
    return invoiceCommon.checkSameItemForOrder("item");
}


//库存检查，库存小于单据入库量 return false；否则return true； 目前只可以用于入库作废库存校验
function checkInventoryAmount() {
    var checkAmount = true;
    var cancelMessage = "\"";
    var Map = APP_BCGOGO.wjl.Collection.Map;
    var inventoryMap = new Map();

    $(".j_table_productNo").find(".item").each(function(i) {
        var productId = $("#itemDTOs" + i + "\\.productId").val();
        var storageUnit = $("#itemDTOs" + i + "\\.storageUnit").val();
        var sellUnit = $("#itemDTOs" + i + "\\.sellUnit").val();
        var rate = $("#itemDTOs" + i + "\\.rate").val();
        var unit = $("#itemDTOs" + i + "\\.unit").val();
        var itemAmount =  $("#itemDTOs" + i + "\\.amount").val() * 1;
        var inventoryAmount = inventoryMap.get(productId);
        if(GLOBAL.Lang.isEmpty(inventoryAmount)){
            inventoryAmount =  $("#itemDTOs" + i + "\\.inventoryAmount").val()*1;
            if(isStorageUnit(unit,sellUnit,storageUnit,rate)){
                inventoryAmount = inventoryAmount * rate;
                itemAmount = itemAmount * rate;
            }
        }
        if (itemAmount > inventoryAmount) {
            cancelMessage = cancelMessage + jQuery("#itemDTOs" + i + "\\.productName").val() + ",";
            checkAmount = false;
        }
        inventoryAmount = inventoryAmount -  itemAmount;
        inventoryMap.put(productId,inventoryAmount);

//
//        if($("#itemDTOs" + i + "\\.amount").val() * 1 > jQuery("#itemDTOs" + i + "\\.inventoryAmount").val() * 1) {
//            cancelMessage = cancelMessage + jQuery("#itemDTOs" + i + "\\.productName").val() + ",";
//            checkAmount = false;
//        }
    });
    if(cancelMessage != '') {
        cancelMessage = cancelMessage.substr(0, cancelMessage.length - 1);
        cancelMessage = cancelMessage + "\"";
    }
    if(!checkAmount) {
        if(APP_BCGOGO.Permission.Version.StoreHouse){
            nsDialog.jAlert("单据商品：" + cancelMessage + "在本仓库库存不足，无法作废，其他商品可用入库退货单处理。");
        }else{
            nsDialog.jAlert("单据商品：" + cancelMessage + "库存不足，无法作废，其他商品可用入库退货单处理。");
        }

    }
    return checkAmount;
}

function newOtherOrder(url) {
    if(openNewOrderPage()) {
        window.open(url, "_blank");
    } else {
        openOrAssign(url);
    }
}

function newStorageOrder() {
    if(openNewOrderPage()) {
        window.open($("#basePath").val() + "storage.do?method=getProducts&type=txn", "_blank");
    }
}

//验证 应付=实付+扣款+挂账

function validatePayable() {
    //总额
    var total = parseFloat($("#total").val() == "" ? 0 : $("#total").val());
    //实付
    var stroageActuallyPaid = parseFloat($("#stroageActuallyPaid").val() == "" ? 0 : $("#stroageActuallyPaid").val());
    //挂帐、
    var stroageCreditAmount = parseFloat($("#stroageCreditAmount").val() == "" ? 0 : $("#stroageCreditAmount").val());
    //扣款
    var stroageSupplierDeduction = parseFloat($("#stroageSupplierDeduction").val() == "" ? 0 : $("#stroageSupplierDeduction").val());
    if(Math.abs(total - (stroageActuallyPaid + stroageCreditAmount)) > 0.001) {
        nsDialog.jAlert("实付金额与扣款、挂账金额不符合，请修改。");
        return false;
    }
    return true;
}

//校验同一张入库单，多条相同商品为新商品，且单位不同。
function validateNewProductMultiUnit() {
    var properties = ["commodityCode", "productName", "brand", "spec", "model", "vehicleBrand", "vehicleModel"];

//    var Set = APP_BCGOGO.wjl.Collection.Set;
//    var itemIndexSet = new Set();

    var CollectionMap = APP_BCGOGO.wjl.Collection.Map;
    var productInfoMap = new CollectionMap();
    $("#table_productNo").find(".item ").each(function (i) {
        var $thisTr = $(this);
        if (GLOBAL.Lang.isEmpty($thisTr.find("[id$='.productId']").val())
            && !GLOBAL.Lang.isEmpty($thisTr.find("[id$='.productName']").val())) {

            var thisItemProductInfo = '';
            var thisItemUnit = $thisTr.find("[id$='.unit']").val();
            for (var j = 0, len = properties.length; j < len; j++) {
                thisItemProductInfo += $thisTr.find("input[id$='." + properties[j] + "']").val() + "__&&__$$";
            }
            var productInfoDetailMap =  productInfoMap.get(thisItemProductInfo);
            if (productInfoDetailMap) {
                if (productInfoDetailMap.getKeys().length > 1) {
                    productInfoDetailMap.put(thisItemProductInfo, thisItemUnit);
                } else if (productInfoDetailMap.getKeys().length == 1) {
                    var itemUnit = productInfoDetailMap.get(productInfoDetailMap.getKeys()[0]);
                    if (itemUnit && thisItemUnit != itemUnit) {
                        productInfoDetailMap.put(i + 1, itemUnit);
                    }
                }
            } else {
                productInfoDetailMap = new CollectionMap();
                productInfoDetailMap.put(i + 1, thisItemUnit);
            }
            productInfoMap.put(thisItemProductInfo,productInfoDetailMap);
        }
    });
    var values = productInfoMap.values();
    var resultMsg = '';
    for (var m = 0, len = values.length; m < len; m++) {
        var keys = values[m].getKeys();
        if (keys.length > 1) {
            for (var n = 0; n < keys.length; n++) {
                resultMsg += '第' + keys[n] + '行商品是新商品，<br>';
            }
        }
    }
    if (!GLOBAL.Lang.isEmpty(resultMsg)) {
        resultMsg += '在同一入库单中只能有一个单位。请修改相应商品的单位！';
        nsDialog.jAlert(resultMsg);
        return false;
    } else {
        return true;
    }

//    $("#table_productNo").find(".item ").each(function (i) {
//        if (itemIndexSet.contains(i + 1)) {
//            return true;
//        }
//        var $thisTr = $(this);
//
//        if (GLOBAL.Lang.isEmpty($thisTr.find("[id$='.productId']").val())) {
//            var thisItemProductInfo = '';
//            var thisItemUnit = $thisTr.find("[id$='.unit']").val();
//            for (var j = 0, len = properties.length; j < len; j++) {
//                thisItemProductInfo += $thisTr.find("input[id$='." + properties[j] + "']").val() + "__&&__$$";
//            }
//            $thisTr.nextAll(".item ").each(function (m) {
//                if (itemIndexSet.contains(i + m + 2)) {
//                    return true;
//                }
//                var $flowItemTr = $(this);
//                var flowItemProductInfo = '';
//                var flowItemUnit = $flowItemTr.find("[id$='.unit']").val();
//                for (var j = 0, len = properties.length; j < len; j++) {
//                    flowItemProductInfo += $flowItemTr.find("input[id$='." + properties[j] + "']").val() + "__&&__$$";
//                }
//                if (thisItemProductInfo == flowItemProductInfo && thisItemUnit != flowItemUnit) {
//                    itemIndexSet.add(i + 1);
//                    itemIndexSet.add(i + m + 2);
//                }
//            });
//        }
//    });
//
//
//    if (itemIndexSet.size() > 0) {
//        var resultMsg = '';
//        for (var i = 0; i < itemIndexSet.size(); i++) {
//            resultMsg += '第' + itemIndexSet.getObject(i) + '行商品是新商品，<br>'
//        }
//        resultMsg+='在同一入库单中只能有一个单位。请修改相应商品的单位！';
//        nsDialog.jAlert(resultMsg);
//        return false;
//    } else {
//        return true;
//    }
}

function initSameProductInfo(){
    var productProperties = [ "productName", "brand", "spec", "model", "vehicleBrand", "vehicleModel"];
    var stringProperties = ["storageBin", "productKind"];
    var numberProperties = ["lowerLimit", "upperLimit", "tradePrice", "recommendedPrice"];
     var Map = APP_BCGOGO.wjl.Collection.Map;
     var productInfoMap = new Map();
    $("#table_productNo").find(".item ").each(function (i) {
        var $thisTr = $(this);
        var idPrefix = $(this).find("input[id$='productId']").attr("id").split(".")[0];

        var productNameInfo = '';
        for (var j = 0, len = productProperties.length; j < len; j++) {
            productNameInfo += $thisTr.find("input[id$='." + productProperties[j] + "']").val() + "__&&__$$";
        }
        var product = productInfoMap.get(productNameInfo);
        var thisItemProductObject = {};
        for (var j = 0, len = stringProperties.length; j < len; j++) {
            thisItemProductObject[stringProperties[j]] = $("#" + idPrefix + "\\." + stringProperties[j] ).val();
        }
        for (var j = 0, len = numberProperties.length; j < len; j++) {
            thisItemProductObject[numberProperties[j]] =  $("#" + idPrefix + "\\." + numberProperties[j] ).val();
        }
        if (GLOBAL.Lang.isObject(product) && product != null) {
            for (var j = 0, len = stringProperties.length; j < len; j++) {
                if (GLOBAL.Lang.isEmpty(product[stringProperties[j]]) && !GLOBAL.Lang.isEmpty(thisItemProductObject[stringProperties[j]])) {
                    product[stringProperties[j]] = thisItemProductObject[stringProperties[j]];
                }
            }
            for (var j = 0, len = numberProperties.length; j < len; j++) {
                if ((GLOBAL.Lang.isEmpty(product[numberProperties[j]])||product[numberProperties[j]]*1 <0.0001 )
                    && !GLOBAL.Lang.isEmpty(thisItemProductObject[numberProperties[j]]) && thisItemProductObject[numberProperties[j]] * 1>0) {
                    product[numberProperties[j]] = thisItemProductObject[numberProperties[j]];
                }
            }
        } else {
            product = thisItemProductObject;
        }
        productInfoMap.put(productNameInfo, product);
    });
    $(productInfoMap.elements).map(function(key,value){
        var upperLimit =  value.value.upperLimit * 1;
        var lowerLimit =  value.value.lowerLimit * 1;
        if(upperLimit > 0 && upperLimit == lowerLimit){
            value.value.upperLimit =  upperLimit+1;
        }else if (lowerLimit >0 && upperLimit>0 && lowerLimit > upperLimit){
            value.value.lowerLimit = upperLimit;
            value.value.upperLimit = lowerLimit;
        }
    });
    $("#table_productNo").find(".item").each(function (i) {
        var $thisTr = $(this);
        var productNameInfo = '';
        var idPrefix = $(this).find("input[id$='productId']").attr("id").split(".")[0];
        for (var j = 0, len = productProperties.length; j < len; j++) {
            productNameInfo += $thisTr.find("input[id$='." + productProperties[j] + "']").val() + "__&&__$$";
        }
        var product = productInfoMap.get(productNameInfo);
        if (product) {
            for (var j = 0, len = stringProperties.length; j < len; j++) {
                if (!GLOBAL.Lang.isEmpty(product[stringProperties[j]])) {
                    $("#" + idPrefix + "\\." + stringProperties[j] ).val(product[stringProperties[j]]);
                }
            }
            for (var j = 0, len = numberProperties.length; j < len; j++) {
                if (!GLOBAL.Lang.isEmpty(product[numberProperties[j]]) && product[numberProperties[j]]*1>0) {
                    $("#" + idPrefix + "\\." + numberProperties[j] ).val(product[numberProperties[j]]);
                }
            }
        }
    });
}


$().ready(function() {

    mySlider.setSubLayerHeight();

    //动态设置每行的高度
    mySlider.setRowHeight();
    //推拉按钮
    mySlider.bindButton();

        var droplist = APP_BCGOGO.Module.droplist;
    $("input[id$='.productKind']").live("click focus", function(event) {
        askForAssistDroplist(event, null);
    }).live("keyup", function(event) {
            askForAssistDroplist(event, "enter");
        });

    function askForAssistDroplist(event, action) {
        var keyName = G.keyNameFromEvent(event);
        if(G.contains(keyName, ["up", "down", "left", "right"])) {
            return;
        }
        var uuid = GLOBAL.Util.generateUUID();
        droplist.setUUID(uuid);
        //ajax获得最近15次使用的商品分类
        var jsonStr = null;
        if(action == null) {
            jsonStr = APP_BCGOGO.Net.syncGet({
                url: "stockSearch.do?method=getProductKindsRecentlyUsed",
                data: {
                    uuid: uuid
                },
                dataType: "json"
            });
        } else if(action == "enter" && event.target.value != "") {
            jsonStr = APP_BCGOGO.Net.syncGet({
                url: "stockSearch.do?method=getProductKindsWithFuzzyQuery",
                data: {
                    uuid: uuid,
                    keyword: $.trim(event.target.value)
                },
                dataType: "json"
            });
        }
        var inputId = event.target;
        var result = {
            uuid: uuid,
            data: (jsonStr && jsonStr.data) ? jsonStr.data : ""
        };
        if(jsonStr && uuid == jsonStr.uuid) {
            droplist.show({
                "selector": $(event.currentTarget),
                "isEditable": true,
                "data": result,
                "onSelect": function(event, index, data) {
                    inputId.value = $.trim(data.label);
                    if(data.label != "") {
                        var idPrefix = inputId.id.split(".")[0];
                        var rowId = idPrefix.substring(11, idPrefix.length);
                        //库存列表处发生
                        if(document.getElementById("productDTOs" + rowId + ".productLocalInfoId") != null) {
                            var productId = document.getElementById("productDTOs" + rowId + ".productLocalInfoId").value;
                            if(!productId) {
                                return;
                            }
                            APP_BCGOGO.Net.asyncGet({
                                url: "stockSearch.do?method=ajaxSaveProductKind",
                                data: {
                                    kindName: $.trim(data.label),
                                    productId: productId
                                },
                                dataType: "json"
                            });
                        }
                    }
                    droplist.hide();
                },
                "onEdit": function(event, index, data) {
                    //记下修改前的分类名称
                    $("#oldKindName").val(data.label);
                },
                "onSave": function(event, index, data) {
                    var newKindName = $.trim(data.label);
                    if($.trim(newKindName) == "") {
                        droplist.hide();
                        nsDialog.jAlert("空字符串不能保存！");
                    } else if(newKindName != $("#oldKindName").val()) {
                        var oldKindName = $("#oldKindName").val();
                        var r = APP_BCGOGO.Net.syncGet({
                            url: "stockSearch.do?method=saveOrUpdateProductKind",
                            data: {
                                oldKindName: $("#oldKindName").val(),
                                newKindName: newKindName
                            },
                            dataType: "json"
                        });
                        if(r == null || r.flag == undefined) {
                            event.target.value = $("#oldKindName").val();
                            droplist.hide();
                            nsDialog.jAlert("保存失败！");
                        } else if(r.flag == "false") {
                            nsDialog.jAlert("分类名“" + newKindName + "”已经存在！");
                        } else if(r.flag == "true") {
                            nsDialog.jAlert("修改成功！", null, function() {
                            $("input[id$='.productKind']").each(function() {
                                if($.trim($(this).val()) == $.trim(oldKindName)) {
                                    $(this).val($.trim(newKindName));
                                }
                            });
                            });
                        }
                        //保存后清空，避免影响下次保存
                        $("#oldKindName").val("");
                    }
                },
                "onDelete": function(event, index, data) {
                    var r = APP_BCGOGO.Net.syncGet({
                        url: "stockSearch.do?method=deleteProductKind",
                        data: {
                            kindName: data.label
                        },
                        dataType: "json"
                    });
                    if(r == null || r.flag == undefined) {
                        nsDialog.jAlert("删除失败！");
                    } else if(r.flag == "true") {
                        nsDialog.jAlert("删除成功！", null, function() {
                        $("input[id$='.productKind']").each(function() {
                            if($.trim($(this).val()) == $.trim(data.label)) {
                                $(this).val("");
                            }
                        });
                        });
                    }
                }
            });
        }
    }
    $("#duizhan").bind("click",function(){
        toCreateStatementOrder($("#supplierId").val(), "SUPPLIER_STATEMENT_ACCOUNT");
    });
});

function showSetproductKind() {
    bcgogo.checksession({
        "parentWindow": window.parent,
        'iframe_PopupBox': $("#iframe_PopupBox_Kind")[0],
        'src': "txn.do?method=setProductKind"
    });
}

function setProducKind(kind) {
    $("input[id$='.productKind']").each(function() {
        $(this).val(kind);
    });
}

//目前只有库存信息要更新
function updateProductInventory(data,isShowMsg) {
    $("#table_productNo").find(".item").each(function () {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            var inventoryAmount =  dataTransition.simpleRounding(G.normalize(data[productId],"0"),2);
            $(this).find("input[id$='.inventoryAmount']").val(inventoryAmount);
            $(this).find("span[id$='.inventoryAmountSpan']").html(inventoryAmount);
        }
    });
}
function updateProductStorageBin(data){
    $("#table_productNo").find(".item").each(function() {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            var idPrefix = $(this).find("input[id$='.productId']").attr("id").split(".")[0];
            $("#" + idPrefix + "\\.storageBin").val(G.normalize(data[productId],""));
        }
    });
}

function submitAfterInputMobile()
{
    //自动删除最后的空白行
    if($("#id").val()) {
        bcgogo.checksession({
            "parentWindow": window.parent,
            'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
            'src': 'storage.do?method=payDetail&supplierId=' + $("#supplierId").val() + "&id=" + $("#id").val()
        });
        return;
    }

    if(!APP_BCGOGO.Verifier.isShopIdCorrect) {
        return;
    }

    var $last_tr = $("#table_productNo").find("tbody").find("tr:last");
    while($last_tr.index() >= 3 && checkEmptyRow($last_tr)) {
        $last_tr.find("[input[id$='.deletebutton']").click();
        $last_tr = $("#table_productNo").find("tbody").find("tr:last");
    }
    if(!validateAll()) {
        return false;
    }

    if(false) { //入库单不做重复行校验
        if(trCount >= 2) if(checkTheSame()) {
            nsDialog.jAlert("单据有重复内容，请修改或删除。");
            return false;
        }
        if(invoiceCommon.checkSameCommodityCode("item")) {
            nsDialog.jAlert("商品编码有重复内容，请修改或删除。");
            return false;
        }
    }

    flag = 1;
    //验证为null的置为空
    invoiceCommon.reSetDomVal($("#category")[0], "null", "");
    invoiceCommon.reSetDomVal($("#invoiceCategory")[0], "null", "");
    invoiceCommon.reSetDomVal($("#settlementType")[0], "null", "");

    //验证手机号码
    if($.trim($("#mobile").val())) check.saveContact(document.getElementById("mobile"));

    //TODO 库存量为空字符，则赋上默认值
    $(".itemInventoryAmount").each(function() {
        invoiceCommon.reSetDomVal(this, "", 0);
    });

    if(!$("#supplier").val()) {
        nsDialog.jAlert("请输入供应商");
        return;
    }

    if($("#supplier").val().length > 30) {
        nsDialog.jAlert("供应商长度不能大于30");
        return;
    }
    $("#div_brand").hide();
    //iframe 方法有两份，维护修改的时候请注意

    $("#purchaseInventoryForm").ajaxSubmit({
        url: "storage.do?method=ajaxValidatorPurchaseInventoryDTOSave",
        dataType: "json",
        type: "POST",
        success: function(json) {
            if(json.success) {
                $("#saveDraftBtn").attr('disabled', true);
                bcgogo.checksession({
                    "parentWindow": window.parent,
                    'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
                    'src': 'storage.do?method=payDetail&supplierId=' + $("#supplierId").val() + "&id=" + $("#id").val() + "&purchaseOrderId=" + $("#purchaseOrderId").val()
                });
            } else if(!json.success && json.operation == "confirm_deleted_product") {
                if(confirm(json.msg)) {
                    $("#saveDraftBtn").attr('disabled', true);
                    bcgogo.checksession({
                        "parentWindow": window.parent,
                        'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
                        'src': 'storage.do?method=payDetail&supplierId=' + $("#supplierId").val() + "&id=" + $("#id").val() + "&purchaseOrderId=" + $("#purchaseOrderId").val()
                    });
                }
            } else if(!json.success) {
                nsDialog.jAlert(json.msg);
                return false;
            }
        },
        error: function(json) {
            nsDialog.jAlert("网络异常，请联系客服");
        }
    });

}

function initDuiZhanInfo()
{
//    if(!$("#receivable").html() || $("#receivable").html()*1 == 0 )
//    {
//        $("#receivableDiv").css("display","none");
//    }
//    else
//    {
//        $("#receivableDiv").css("display","inline");
//    }
//
//    if(!$("#payable").html() || $("#payable").html()*1 == 0 )
//    {
//        $("#payableDiv").css("display","none");
//    }
//    else
//    {
//        $("#payableDiv").css("display","inline");
//    }
//
//    if($("#receivableDiv").css("display") == "none" && $("#payableDiv").css("display") == "none")
//    {
//        $("#duizhan").hide();
//    }
//    else
//    {
//        $("#duizhan").show();
//    }
}
 //校验入库单是否有两个不同商品有同一个商品编码
//function checkSameCommodityCodeForPurchaseInventory(){
//    var hasSameCommodityCode = false;
//    var properties = [ "productName", "brand", "spec", "model", "vehicleBrand", "vehicleModel", "vehicleYear", "vehicleEngine"];
//    $(".item").each(function(i){
//        var itemCommodityCode = $(this).find("input[id$='.commodityCode']").val();
//        var itemInfo = "";
//        for (var m = 0, len = properties.length; m < len; m++) {
//            itemInfo += $(this).find("input[id$='." + properties[m] + "']").val() + "__&&__$$";
//        }
//        if(!GLOBAL.isEmpty(itemCommodityCode)){
//            $(".item:gt("+i+")").each(function(j){
//                var checkedItemCommodityCode = $(this).find("input[id$='.commodityCode']").val();
//                if(!GLOBAL.isEmpty(checkedItemCommodityCode) && itemCommodityCode === checkedItemCommodityCode){
//                    var checkedItemInfo = "";
//                    for (var m = 0, len = properties.length; m < len; m++) {
//                        checkedItemInfo += $(this).find("input[id$='." + properties[m] + "']").val() + "__&&__$$";
//                    }
//                    if(itemInfo!=checkedItemInfo){
//                        hasSameCommodityCode = true;
//                        return false;
//                    }
//                }
//            });
//        }
//        if(hasSameCommodityCode){
//            return false;
//        }
//    });
//    return hasSameCommodityCode;
//}

//校验同一张入库单，多条相同商品为新商品，且单位不同。
function checkSameCommodityCodeForPurchaseInventory() {
    var properties = [ "productName", "brand", "spec", "model", "vehicleBrand", "vehicleModel"];

//    var Set = APP_BCGOGO.wjl.Collection.Set;
//    var itemIndexSet = new Set();

    var CollectionMap = APP_BCGOGO.wjl.Collection.Map;
    var productInfoMap = new CollectionMap();
    //将单据信息组装成Map<commodityCode,map<index,productInfo>> 的数据结构
    $("#table_productNo").find(".item ").each(function (i) {
        var $thisTr = $(this);
        var itemCommodityCode = $thisTr.find("input[id$='.commodityCode']").val();
        if (!GLOBAL.Lang.isEmpty($thisTr.find("input[id$='.productName']").val()) && !GLOBAL.Lang.isEmpty(itemCommodityCode)) {
            var thisItemProductInfo = '';
            var productInfoDetailMap = productInfoMap.get(itemCommodityCode);
            for (var j = 0, len = properties.length; j < len; j++) {
                thisItemProductInfo = thisItemProductInfo + $thisTr.find("input[id$='." + properties[j] + "']").val() + "__&&__$$";
            }
            if (!productInfoDetailMap) {
                productInfoDetailMap = new CollectionMap();
            }
            productInfoDetailMap.put(i + 1, thisItemProductInfo);
            productInfoMap.put(itemCommodityCode,productInfoDetailMap);
        }
    });

    var productInfoDetailMapList = productInfoMap.values();
    var resultMsg = '';

    for (var m = 0, len = productInfoDetailMapList.length; m < len; m++) {
        var productInfoDetailMap = productInfoDetailMapList[m];
        var keys = productInfoDetailMap.getKeys();
        if (keys.length > 1) {
            var firstProductInfo =  productInfoDetailMap.get(keys[0]);
            var isHaveDiffProductInfo = false;
            for (var n = 1; n < keys.length; n++) {
                var itemProductInfo = productInfoDetailMap.get(keys[n]);
                if(itemProductInfo != firstProductInfo){
                    isHaveDiffProductInfo = true;
                    break;
                }
            }
            if(isHaveDiffProductInfo){
                for (var n = 0; n < keys.length; n++) {
                    resultMsg += '第' + keys[n] + '行商品编码有重复内容，请修改或删除。<br>';
                }
            }
        }
    }
    if (!GLOBAL.Lang.isEmpty(resultMsg)) {
        nsDialog.jAlert(resultMsg);
        return false;
    } else {
        return true;
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
