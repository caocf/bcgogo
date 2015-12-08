/**
 * @依赖 js/base.js;  js/application.js;  js/module/invoiceCommon.js;  js/module/bcgogoValidate.js
 */
//TODO 销售单页面脚本
var trCount;
var trCount2;
var message;
var time = new Array();
time[0] = new Date().getTime();
time[1] = new Date().getTime();
//添加验证方法 (实收大于0)
//jQuery.validator.addMethod("isBig", function(value, element, param) {
//    return this.optional(element) || isBig(value, param[0]);
//}, "请输入大于0的实收金额");

var invoiceCommon = APP_BCGOGO.Module.wjl.invoiceCommon;
var priceChangeTimeout;

//init 销售单验证器
$(function() {
    var bcgogoValidator = APP_BCGOGO.Module.wjl.bcgogoValidator;
    var rules = {
        customer:{
            required: true,
            maxlength:30
        },
        mobile:{
            mobile:true
        }
    }
    var messages = {
        customer:{
            required: "请输入单位/客户",
            maxlength:"单位/客户有效长度30"
        },
        mobile:{
            mobile:"您的联系方式不正确,,请检查!"
        }
    }
    bcgogoValidator.setRules(rules);
    bcgogoValidator.setMessages(messages);
//    bcgogoValidator.setConfirmMessage("是否确认销售?");
    bcgogoValidator.validate($("#salesOrderForm")[0]);

    initDuiZhanInfo();
    $("#customerInfo").hover(function(){
        $(this).css("color","#fd5300");
    },function(){
        $(this).css("color","#6699CC");
    });
});

//TODO 工具方法，比较value是否大于i;
function isBig(value, i) {
    var a = parseFloat(value);
    var b = parseFloat(i);
    return a >= b;
}

jQuery().ready(function() {

    if (returnType == 1) {
        bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':"storage.do?method=showLackGoods" +
            "&inventoryAmount=" + (parseFloat($(".item").eq(returnIndex).find(".itemAmount").val())
            + parseFloat($(".item").eq(returnIndex).find(".itemInventoryAmount").val())) + "&productIds=" + $(".item").eq(returnIndex).find("input[name$='productId']").val()});
    }

    //供应商栏
    $(document).bind("keydown", function (event) {
        var target = event.target;
        if($(target).hasClass("customerSuggestion")) return;
        if (target.type != "text" && target.className != "i_operate") return;
        if ($(target).attr("id") && $(target).attr("id").endWith(".commodityCode")) return;
        var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
        if (keyName == "enter" || keyName == "right" || keyName == "left" ) {
            if ($("#div_brandvehiclelicenceNo").css("display") != "none")return;
            invoiceCommon.keyBoardSelectTarget(event.target, keyName);
        }
    });

    //商品绑定键盘事件
    $(document).bind("keydown", function (event) {
        var target = event.target;
        if ((!target.parentElement || !target.parentElement.parentElement || target.parentElement.parentElement.className != "item")
            && target.type == "text") return;
        //TODO 当库存明细页面显示时，按空格隐藏,否则显示库存页面
        var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
        if (keyName == "space") {
//            if ($("#iframe_PopupBox").css("display") == "block") {
//                $("#mask").hide();
//                $("#iframe_PopupBox").hide();
//            } else {
//                $("#div_brand").hide();
//                searchInventoryIndex(target);
//            }
//            return false;
        } else if (keyName == "enter" && $(".item :text:last").attr("id") == this.id) {
            $(this).parent().next().children(".opera2").trigger("click");
            invoiceCommon.selectAndFocusByNode($($(this).parent().parent().next().children("td").get(1)).children(":text"));
        }
    });

    //button Enter键绑定
    //TODO 结算、打印、取消，被选择后回车，则单击该元素
    $(document).bind("keydown", function (event) {
        if (event.target.className != "i_operate")return;
        var keyName = GLOBAL.Interactive.keyNameFromEvent(event);
        if (keyName == "enter") {
            $(event.target).click();
        }
    });

    //销售单ID存在则置灰框子和结算按钮
    if ($.trim($("#id").val())) {
        $("#saveDraftOrder_div").each(function(){
            $(this).hide();
        });
//        $("#saleAccountBtn").attr("disabled", "disabled");
        $("#saveDraftBtn").attr("disabled", "disabled");
        $("#trCustomer input,.item input,.total input,#memo").not("#saleAccountBtn,#printBtn").attr("disabled", "disabled");
    }
    //如果还没结算过，实收=总计
    if ($("#settledAmountHid").val() < 0) {
        $("#settledAmount").val($("#total").val());
    }

    if (salesOrderId == '' || debt == '0.0') {
        $("#input_makeTime_sale").hide();  //隐藏设置还款时间
    }

    //手机号码验证：zhangchuanlong
    $("#mobile").blur(function() {
        if (this.value)
            check.inputCustomerMobileBlur2(this,$("#landline")[0]);
    });


    $("#saleAccountBtn").click(function(){    //TODO “结算”按钮被单击后的处理脚本

        if ($("#customerId").val()) {
            var r = APP_BCGOGO.Net.syncGet({async:false,url:"customer.do?method=checkCustomerStatus",data:{customerId: $("#customerId").val(),now:new Date()},dataType:"json"});
            if (!r.success) {
                alert("此客户已被删除或合并，不能做单，请更改客户！");
                return;
            }
        }
        if($("#customer").val()!="**客户**" && $("#mobile").attr("mustInputBySameCustomer") && G.Lang.isEmpty($.trim($("#mobile").val()))){
            nsDialog.jAlert("当前客户存在同名客户，请填写手机号，或者修改客户名加以区分");
            return;
        }
        if($.trim($("#mobile").val()) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#mobile").val())) {
            nsDialog.jAlert("手机号码输入不规范，请重新输入","",function(){
                $("#mobile").focus();
            });
            return;
        }

        if ($.trim($("#vehicleContact").val()) != '' || $.trim($("#vehicleMobile").val()) != '') {
            if ($.trim($("#licenceNo").val()) == '') {
                nsDialog.jConfirm("车辆信息不完整！需要清空车辆信息吗？", "", function (bool) {
                    if(bool){
                        clearVehicleInfo();
                    }else{
                        $("#licenceNo").focus();
                    }
                });
                return;
            }
        }
        $("#btnType").val('saleAccountBtn');
        if(!checkStorehouseSelected()) {
            return;
        }
        //在此之前判断手机号是否正确
        if ($("#customer").val() && !$.trim($("#mobile").val()) && APP_BCGOGO.Permission.Version.OrderMobileRemind) {
            $("#inputMobile").dialog("open");
            return;
        }

        var msg = validateOtherIncomeInfo();
        if (!GLOBAL.Lang.isEmpty(msg)) {
            nsDialog.jAlert("费用列表 ：" + msg);
            return;
        }

        //结算校验逻辑放到  accountTypeChange 里面了
        accountTypeChange();
    });

    //TODO 为“更多客户信息”添加单击事件，可以打开“更多客户信息”窗口
    if ($("#customerInfo")[0]) {
        $("#customerInfo").bind("click", function() {
            //看品名的下拉框有没有显示，如果显示则进行隐藏
            if ($("#div_brand")[0]) {
                $("#div_brand").hide();
            }
            var vehicleId = $('#vehicleId').val();
            if (Number(vehicleId) > 0) {
                APP_BCGOGO.Net.asyncGet({
                    url: 'customer.do?method=getVehicleInfoByVehicleId',
                    data: {
                        "vehicleId": vehicleId,
                        "now": new Date()
                    },
                    dataType: "json",
                    success: function (response) {
                        if(response){
                            response.contact = $("#vehicleContact").val();
                            response.mobile = $("#vehicleMobile").val();
                            for(var key in response){
                                (response[key] == null || response[key] == 'null') && (response[key] = '');
                            }
                            bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':"txn.do?method=clientInfo&customer="
                                + $("#customer").val() + "&mobile=" + $("#mobile").val() + "&landLine=" + $("#landline").val() + "&hiddenMobile=" + $("#hiddenMobile").val()
                                + "&customerId=" + $("#customerId").val() + "&contact=" + $("#contact").val() + '&licenceNo=' +response.licenceNo + '&vehicleContact=' + response.contact +
                                '&vehicleMobile=' + response.mobile + '&brand=' + response.brand + '&model=' +response.model + '&chassisNumber=' + response.chassisNumber + '&engineNo=' + response.engineNo +
                                '&color=' + response.color
                            });
                        }
                    }
                });
            } else {
                bcgogo.checksession({"parentWindow": window.parent, 'iframe_PopupBox': $("#iframe_PopupBox")[0], 'src': "txn.do?method=clientInfo&customer="
                    + $("#customer").val() + "&mobile=" + $("#mobile").val() + "&landLine=" + $("#landline").val() + "&hiddenMobile=" + $("#hiddenMobile").val()
                    + "&customerId=" + $("#customerId").val() + "&contact=" + $("#contact").val()
                });
            }
        });
    }
    //欠款结算
    if ($("#a_qkjs")[0]) {
        $("#a_qkjs").bind("click", function() {
            $(".tableInfo").hide();
            $(".table_title").hide();
            $(".item").hide();
            $("#iframe_qiankuan").show();//欠款
            $("#iframe_qiankuan")[0].src = "txn.do?method=detailsArrears&customerId=" + $("#customerId").val();
        });
    }
    //end   //TODO 取消按钮，重新加载页面
    $("#cancelBtn").click(function () {
        if($("#id").val())
        {
            window.location = "sale.do?method=getProducts&type=txn";
        }
        else
        {
            window.location = "sale.do?method=getProducts&type=txn&cancle=noId&receiptNo="+$("#receiptNo").val();
        }

    });
    //TODO 单价和数量变化时，总计也要联动
    $("#table_productNo").bind('keyup', function(event) {
        if (!$(event.target).hasClass("itemPrice") && !$(event.target).hasClass("itemAmount"))  return;
        var $thisDom =  $(event.target);
        if($(event.target).hasClass("itemPrice")){
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingPriceFilter($thisDom.val(),2));
        }else if($(event.target).hasClass("itemAmount")){
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingPriceFilter($thisDom.val(),2));
        }
    });

    $('.itemPrice').live('change', function () {
        setTotal();
    });

    $('.itemAmount').live('change', function () {
        setTotal();
    });

    //TODO 销售单列表单行模版
    trCount = $(".item").size();

    trCount2 = $(".item2").size();

    //增加行          //TODO 加号按钮单击触发的脚本
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "opera2")  return;
        //TODO 在旧单据中不允许新增一行
        if ($("#id").val()) return false;

        var ischeck = checkVehicleInfo(target);       //TODO 检查改行数据是否符合验证规则
        if (!ischeck && ischeck != null) return;
        //采购单检查是否相同
        if (trCount >= 2)
            if (checkTheSame()) {
                alert("单据有重复内容，请修改或删除。");
                return false;
            }
        if (invoiceCommon.checkSameCommodityCode("item")) {
            alert("商品编码有重复内容，请修改或删除。");
            return false;
        }
        inventoryOrderAdd();
    });

    $(".operaAdd").live("click",function(){
        var id = $(this)[0].id;
        var prefix = id.split(".")[0];
        if ($("#id").val()) return false;
        if($(".item2").size()==0)
        {
            inventoryOrderAdd2();
            return;
        }

        var ischeck = checkOtherInComeInfo(prefix);
        if (!ischeck && ischeck != null) return;
        inventoryOrderAdd2();
    });


    //删除行    //TODO 点击减号所做的删除改行的操作
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "opera1")  return;
        if ($("#id").val()) return false;
        var idPrefix = $(target).attr("id").split(".")[0];
        $("#"+idPrefix+"_supplierInfo").remove();
        $(target).closest("tr").remove();
        setTotal();
        if ($("#debt").val() * 1 <= 0) {
            $("#input_makeTime_sale").hide();
            $("#isMakeTime").val("0");
        }
        isShowAddButton();
        trCount = $(".item").size();
    });

    // $(".operaMinus").live("click",function(event){
    $(document).bind("click",function(event){
        if(event.target === document.lastChild) return;

        if($(event.target).hasClass("operaMinus")) {
            var id = $(event.target).attr("id");
            var prefix = id.split(".");
            if (!$("#id").val()){
                $(event.target).parents("tr").remove();
                setTotal();
                isShowOtherIncomeAddButton2();
            }

        }
        trCount2 = $(".item2").size();
    });

    //初始化的时候判断是否显示+按钮
    isShowAddButton();
    isShowOtherIncomeAddButton2();
    $("#settledAmount").bind('blur keyup', function() {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(),2));
        message = calculate.subtraction("#total", "#settledAmount", "#debt", "#input_makeTime_sale", message);
    });

    $("#debt").keyup(function(event) {
        event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
        var total = Number($("#total").val());  //总额
        var settledAmount = Number($("#settledAmount").val());//实收
        var debt = Number($("#debt").val());           //欠款
        if (debt + settledAmount > total) {
            $("#debt").val(total);
            $("#settledAmount").val(0);
            message = "请输入合适的欠款金额。";
            showMessage.fadeMessage("35%", "40%", "slow", 2000, message);
            $("#input_makeTime_sale").show();
        }
    })
        .blur(function(event) {
            if ("" == $("#debt").val()) {
                $("#debt").val(0);
            } else {
                event.target.value = APP_BCGOGO.StringFilter.priceFilter(event.target.value, 2);
            }
        });

    $(".item").bind("click", function() {
        if ($("#returnInfo")) {
            $("#returnInfo").remove();
        }
    });

    //设置还款
    $("#input_makeTime_sale").click(function() {
        bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBoxMakeTime")[0],'src':"txn.do?method=makeTime&orderId=" + salesOrderId});
    });

    //历史查询
    jQuery(".g_select a").click(function () {
        jQuery(this).addClass("hover");
        jQuery(".g_select a").not(jQuery(this)).removeClass("hover");
        jQuery("#searchType").val(jQuery(this).attr("id"));
    });


    //打印页面
    $("#printBtn").click(function() {
        if (!$("#id").val() && !$("#draftOrderIdStr").val()) {
            return;
        }
        APP_BCGOGO.Net.syncGet({
            url:"print.do?method=getTemplates",
            data:{
                "orderType":"SALE",
                "now":new Date()
            },
            dataType:"json",
            success:function(result) {
                if(result && result.length >1){
                    var selects = "<div style='margin:10px;font-size:15px;line-height: 22px;'>" +
                        "<div style='margin-bottom:5px;'>请选择打印模板：</div>";
                    for(var i = 0; i<result.length; i++){
                        var radioId = "selectTemplate" + i;
                        selects += "<input type='radio' id='"+radioId+"' name='selectTemplate' value='"+result[i].idStr+"'";
                        if(i==0){
                            selects += " checked='checked'";
                        }
                        selects += " />" +"<label for='"+radioId+"'>"+result[i].displayName +"</label><br/>";
                    }
                    selects += "</div>";
                    nsDialog.jConfirm(selects, "请选择打印模板", function (returnVal) {
                        if (returnVal) {
                            printSalesOrder($("input:radio[name='selectTemplate']:checked").val());
                        }
                    });
                }else{
                    printSalesOrder();
                }
            }
        });
    });

    if($("#id").val() && "true" == $("#print").val()){
        $("#printBtn").click();
    }

    if ($("#status").val() == "PENDING") {
        jQuery("#zuofei").removeClass("zuofei").addClass("pendingImg").show();
    } else if ($("#status").val() == "STOCKING") {
        jQuery("#zuofei").removeClass("zuofei").addClass("stockingImg").show();
    } else if ($("#status").val() == "DISPATCH") {
        jQuery("#zuofei").removeClass("zuofei").addClass("dispatchImg").show();
    } else if ($("#status").val() == "REFUSED") {
        jQuery("#zuofei").removeClass("zuofei").addClass("refusedImg").show();
    } else if ($("#status").val() == 'SALE_DONE') {
        $(".invalidImg").show();
        jQuery(".copyInput_div").show();

        if ($("#statementAccountOrderId").val()) {
            jQuery("#zuofei").removeClass("zuofei").addClass("statement_accounted").show();
        } else {

            if ($("#orderDebt").html().replace("元", '') * 1 > 0) {
                jQuery("#zuofei").removeClass("zuofei").addClass("debtSettleImg").show();
            }
            else {
                jQuery("#zuofei").removeClass("zuofei").addClass("jie_suan_sale").show();
            }
        }


    }  else if ($("#status").val() == 'SALE_REPEAL') {
        $("#zuofei").show();
        $(".invalidImg").hide();
        jQuery(".copyInput_div").show();
    } else if ($("#status").val() == "SELLER_STOP") {
        jQuery("#zuofei").removeClass("zuofei").addClass("stopImg").show();
    } else if ($("#status").val() == "STOP") {
        jQuery("#zuofei").removeClass("zuofei").addClass("stopImg").show();
    } else if ($("#status").val() == '') {
        $("#zuofei").hide();
        $(".invalidImg").hide();
        jQuery(".copyInput_div").hide();
    }


    //销售单复制
    jQuery("#copyInput").bind("click", function() {
        if(GLOBAL.Lang.isEmpty($("#id").val())) {
            nsDialog.jAlert("单据ID不存在，请刷新后重试");
            return false;
        }
        APP_BCGOGO.Net.syncPost({
            url:"sale.do?method=validateCopy",
            dataType:"json",
            data:{"salesOrderId" : $("#id").val()},
            success:function(result){
                if(result.success){
                    window.location.href = "sale.do?method=copyGoodSale&salesOrderId=" + $("#id").val();
                }else{
                    if(result.operation == 'ALERT'){
                        nsDialog.jAlert(result.msg, result.title);
                    }else if(result.operation == 'CONFIRM'){
                        nsDialog.jConfirm(result.msg, result.title, function(resultVal){
                            if(resultVal){
                                window.location.href = "sale.do?method=copyGoodSale&salesOrderId=" + $("#id").val();
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

    //生成销售退货单
    jQuery("#salesReturn").bind("click", function() {
        if (!GLOBAL.Lang.isEmpty($("#id").val())) {
            window.open("salesReturn.do?method=createSalesReturn&orderId=" + $("#id").val() +"&orderType=sale");
        }
    });

    $("#saveDraftBtn").bind('click', function (event) {
        if (isLegalTxnDataLength()&&openNewOrderPage()) {
            $("#saveDraftBtn").attr("disabled",true);
            var listFun = rememberReadOnly();
            clearReadOnly();
            $("#salesOrderForm").ajaxSubmit({
                url:"draft.do?method=saveSalesOrderDraft",
                dataType: "json",
                type: "POST",
                success: function(data){
                    listFun();
                    showMessage.fadeMessage("45%", "34%", "slow", 300,"草稿保存成功！  "+getCurrentTime());
                    $("#draftOrderIdStr").val(data.idStr);
                    $("#saveDraftBtn").attr("disabled",false);
                    if(!G.isEmpty(data)){
                        $("#receiptNoSpan").text(data.receiptNo);
                        $("#receiptNo").val(data.receiptNo);
                        $("#print_div").show();
                    }
                },
                error:function(){
                    listFun();
                    showMessage.fadeMessage("45%", "34%", "slow", 300,"保存草稿异常！  "+getCurrentTime());
                    $("#saveDraftBtn").attr("disabled",false);
                }
            });
        }
    });


    $(".otherIncomePrice").live("keyup",function(){
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(),2));
        setTotal();
    });
    $("input[name$='memo']").live("keyup",function(){
        $(this).val(APP_BCGOGO.StringFilter.inputtingOtherCostNameFilter($(this).val()));
    });

    $("input[name$='memo']").live("blur",function(){
        $(this).val($.trim($(this).val()));
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

                    var r = APP_BCGOGO.Net.syncGet({
                        url:"customer.do?method=getCustomerJsonDataByMobile",
                        data:{mobile: $("#divMobile").val(), customerId:$("#customerId").val()},
                        dataType:"json"
                    });
                    var obj = r.data;
                    if(r.success){
                        if(r.msg == 'customer' && obj.idStr)
                        {
                            if ($("#customer").val() == obj.name) {
                                $("#customerId").val(obj.idStr);
                            } else {
                                $("#inputMobile").dialog("close");
                                $("#mobile").blur();
                                return;
                            }
                        }else if(r.msg == 'supplier'){
                            nsDialog.jAlert("填写的手机号与已存在供应商【"+obj.name + "】的手机号相同，请重新输入！");
                            $("#divMobile").val("");
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

    // 客户 快速输入功能强化
    $("#customer")
            .attr("warning", "请先输入")
            .tipsy({title: "warning", delay: 0, gravity: "s", html: true, trigger: 'hover'})
            .bind("focus", function () {
                $(this).tipsy("hide");
            $(this).attr("lastValue",$(this).val());
            });

    // add by zhuj 联系人下拉菜单
    // 绑定搜索下拉事件
    $("#contact")
        .bind('click focus', function (e) {
            e.stopImmediatePropagation();//可以阻止掉同一事件的其他优先级较低的侦听器的处理
            if (!GLOBAL.Lang.isEmpty($("#customerId").val())) {
                getContactListByIdAndType($("#customerId").val(), "customer", $(this)); //@see js/contact.js
            }
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                //clear by qxy  why do we need this?
//                getContactListByIdAndType($("#customerId").val(), "customer", $(this), eventKeyCode); //@see js/contact.js
            }
        });

    // add by zhuj 绑定customerName blur事件 替代change事件
    //blur 事件延时处理，select 下拉之后不触发blur事件
    $("#customer").blur(function(e){
        var $customer  =  $(this);
        setTimeout(function () {
            if($customer.attr("blurLock")){
                $customer.removeAttr("blurLock");
                return;
            }
            if($customer.val() == ''){
                clearCustomerInfo($('#vehicleId').val() == '');
            }
            if($customer.val() == $customer.attr("lastValue")){
                return;
            }
            $("#mobile").removeAttr("mustInputBySameCustomer");
            var name = $customer.val();
            var jsonCustomers = getCustomerByName(name);
            if (!G.isEmpty(jsonCustomers) && !G.isEmpty(jsonCustomers.results)) {
                // 渲染tip页面
                var contactList = new Array();
                for (var customerIndex = 0; customerIndex < jsonCustomers.results.length; customerIndex++) {
                    var customerId = jsonCustomers.results[customerIndex].idStr;
                    var contacts = jsonCustomers.results[customerIndex].contacts;
                    if (!G.isEmpty(contacts)) {
                        contacts = filterNullObjInArray(contacts);
                    }

                    if (G.isEmpty(contacts)) {
                        var customer = {
                            customerId: customerId,
                            contactId: "",
                            contact: "",
                            mobile: ""
                        };
                        contactList.push(customer);
                    } else {
                        for (var contactIndex = 0; contactIndex <contacts.length; contactIndex++) {
                            if (contacts[contactIndex] && !G.Lang.isEmpty(contacts[contactIndex].idStr)) {
                                var customer = {
                                    customerId: customerId,
                                    contactId: contacts[contactIndex].idStr,
                                    contact: contacts[contactIndex].name,
                                    mobile: contacts[contactIndex].mobile
                                };
                                contactList.push(customer);
                            }
                        }
                    }
                }

                if (!G.isEmpty(contactList)) {
                    for (var contactIndex in contactList) {
                        var $sin_contact = $('<div class="sin_contact"><label class="rad"><input type="radio" name="sin_contact"/>'
                            + name +
                            '</label>&nbsp;&nbsp;<span>'
                            + G.normalize(contactList[contactIndex].contact, "")
                            + '</span>&nbsp;&nbsp;<span>'
                            + G.normalize(contactList[contactIndex].mobile, "")
                            + '</span><br>'
                            + '<input type="hidden" class="contactName" value="' + G.normalize(contactList[contactIndex].contact, "") + '"/>'
                            + '<input type="hidden" class="contactMobile" value="' + G.normalize(contactList[contactIndex].mobile, "") + '"/>'
                            + '<input type="hidden" class="contactId" value="' + G.normalize(contactList[contactIndex].contactId, "") + '"/>'
                            + '<input type="hidden" class="customerId" value="' + G.normalize(contactList[contactIndex].customerId, "") + '"/>'
                            + '</div>');
                        $sin_contact.insertAfter($("#oldCustomers > div:last"));
                    }
                }

                $("#nameDupTip").dialog({
                    resizable: false,
                    height:200,
                    width:400,
                    title: "友情提示",
                    modal: true,
                    closeOnEscape: false,
                    close: function () {
                        $(".sin_contact").remove();
                        $("input[type=radio]").attr("checked", "");
                        $("#oldCustomers").hide();
                        $("#cusDupTip").show();
                    },
                    open: function (event, ui) {
                        $(event.target).parent().find(".ui-dialog-titlebar-close").hide();
                    }
                });
            }else{
                var customerId = $('#customerId').val();
                if (customerId != '') {
                    var customer = $('#customer').val();
                    clearCustomerInfo($('#vehicleId').val() == '');
                    $('#customer').val(customer);
                }
            }
        }, 200);


    });

    $("#newCustomer").click(function(){
        //$("#nameDupTip").dialog("close");
        $("#isAdd").val("true");
        $("#customerId").val(""); // 后台默认以这个值为判断是否为新增的依据
    });

    $("#oldCustomer").click(function(){
        $("#oldCustomers").show();
        $("#cusDupTip").hide();
        $("#isAdd").val("false");
        $("#nameDupTip .J_return").show();
    });

    $(".sin_contact").live("click", function () {
        $("#contact").val($(this).find("input[class=contactName]").val());
        $("#mobile").val($(this).find("input[class=contactMobile]").val());
        $("#contactId").val($(this).find("input[class=contactId]").val());
        $("#customerId").val($(this).find("input[class=customerId]").val());
    });



    $("#nameDupTip .J_btnSure").click(function(){
        if ($("#cusDupTip").css("display") == "block") {
            if (!$("#newCustomer").attr("checked") && !$("#oldCustomer").attr("checked")) {
                nsDialog.jAlert("请选择新客户或者老客户！");
                return;
            }else{
                $("#mobile").attr("mustInputBySameCustomer",true);
            }
        }
        if ($("#oldCustomers").css("display") == "block") {
            var oldChecked = false;
            $("#oldCustomers input[name='sin_contact']").each(function () {
                if ($(this).attr("checked")) {
                    oldChecked = true;
                }
            });
            if (!oldChecked) {
                nsDialog.jAlert("请在客户列表中选择！");
                return;
            }
        }
        if ($("#newCustomer").attr("checked")) {
            $("#isAdd").val("true");
            $("#customerId").val(""); // 后台默认以这个值为判断是否为新增的依据
        }
        $(".sin_contact").remove();
        $("#nameDupTip").dialog("close");
        $("input[type=radio]").attr("checked", "");
        $("#oldCustomers").hide();
        $("#cusDupTip").show();
        $("#customerId").val() != '' && showLicenceNo(null, $("#customerId").val());
    });

    $("#nameDupTip .J_return").bind("click",function(){
        $("#oldCustomers").find("[name='sin_contact']").each(function(){
            $(this).attr("checked","");
        });
        $("#mobile").removeAttr("mustInputBySameCustomer");
        $("#oldCustomers").hide();
        $("#cusDupTip").find("[type='radio']").each(function () {
            $(this).attr("checked","");
        });
        $("#cusDupTip").show();
        $(this).hide();
        $("#contact").val("");
        $("#mobile").val("");
        $("#contactId").val("");
        $("#customerId").val("");
    });
});

function acceptSale() {
    if ($("#acceptMemo").val() == $("#acceptMemo").attr("initialValue")) {
            $("#acceptMemo_hidden").val("");
    } else {
            $("#acceptMemo_hidden").val($("#acceptMemo").val());
        }
        $("#goodsSaler_hidden").val($("#goodsSaler").val());
        $("#goodsSalerId_hidden").val($("#goodsSalerId").val());
        $("#acceptDialog").dialog({ width: 520, modal: true,
            beforeclose: function (event, ui) {
                $("#acceptConfirmBtn").removeAttr("lock");
                return true;
            }});
}
function printSalesOrder(templateId){
    if ($("#id").val()) {
        window.showModalDialog("sale.do?method=getSalesOrderToPrint&salesOrderId=" + $("#id").val() +"&templateId=" + templateId + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
        return;
    }
    if ($("#draftOrderIdStr").val()) {
        window.showModalDialog("draft.do?method=getDraftOrderToPrint&id=" + $("#draftOrderIdStr").val() + "&type=SALE&now=" + "&templateId="+templateId + new Date());
    }
}
//终止交易
function stopSale() {
    if($("#purchaseOrderDTOStatus") && $("#purchaseOrderDTOStatus").val() == "PURCHASE_ORDER_DONE"){
        nsDialog.jAlert("买家已入库，不能终止交易！");
    }else{
        $("#repealReasonDialog").dialog({ width: 520, modal:true});
    }

}
function refuseSale() {
    if($("#acceptMemo").val()==$("#acceptMemo").attr("initialValue")){
        $("#saleMemo_hidden").val("");
    }else{
        $("#saleMemo_hidden").val($("#acceptMemo").val());
    }
    $("#refuseReasonDialog").dialog({ width: 520, modal:true});
}
function dispatch(){
    App.Net.syncPost({
        url: "sale.do?method=validateDispatchSaleOrder",
        data: {salesOrderId: $("#salesOrderId").val()},
        dataType: "json",
        success: function (json) {
            if(json && json.success){
                $("#dispatchDialog").dialog({ width: 520, modal:true});
            }else{
                if(json && json.operation=="CONFIRM_ALLOCATE_RECORD"){
                    nsDialog.jConfirm(json.msg, "确认仓库调拨提示", function (returnVal) {
                        if(returnVal){
                            window.open("allocateRecord.do?method=createAllocateRecordBySaleOrderId&salesOrderId="+$("#salesOrderId").val(),"_blank");
                        }
                    });
                }else if(json && json.operation=="ALERT_SALE_LACK"){
                    nsDialog.jAlert(json.msg);
                }else{
                    nsDialog.jAlert(json.msg);
                }
            }
        }
    });
}
//目前只有库存信息要更新
function updateProductInventory(data,isShowMsg) {
    $("#table_productNo").find(".item").each(function () {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            $(this).find("input[id$='.inventoryAmount']").val(G.rounding(data[productId],2));
            $(this).find("span[id$='.inventoryAmountSpan']").html(G.rounding(data[productId],2));
        }
    });
    var lackMsg = "";
    $(".itemAmount").each(function () {
        if ($(".itemInventoryAmount").eq($(this).index('.itemAmount')).val() * 1 - $(this).val() * 1 < 0) {
            lackMsg = lackMsg + "\n第" + ($(this).index(".itemAmount") + 1) + "行商品库存不足，无法销售";
        }
    });
    if (!APP_BCGOGO.Permission.Version.IgnorVerifierInventory && isShowMsg) {
        if (lackMsg) {
            alert(lackMsg);
            return false;
        } else {
            return true;
        }
    }
}

function updateProductStorageBin(data) {
    $("#table_productNo").find(".item").each(function () {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            $(this).find("input[id$='.storageBin']").val(G.normalize(data[productId],""));
            $(this).find("span[id$='.storageBinSpan']").html(G.normalize(data[productId],""));
        }
    });
}
function getTrSample() {
    var trSample = '<tr class="bg item table-row-original" style="border：none">' +
        '<td style="border-left:none;padding-left: 10px;">' +
        '   <input id="itemDTOs0.commodityCode" name="itemDTOs[0].commodityCode" style="width:85%" type="text" class="table_input checkStringEmpty" value="" maxlength="20"/>' +
        '</td>' +
        '    <td>' +
        '	<input id="itemDTOs0.id" name="itemDTOs[0].id" type="hidden" value=""/>' +
        '    <input id="itemDTOs0.productVehicleStatus" name="itemDTOs[0].productVehicleStatus" value="" type="hidden" class="itemProductVehicleStatus"/>' +
        '    <input id="itemDTOs0.hidden_productVehicleStatus" name="itemDTOs[0].hidden_productVehicleStatus" value="" type="hidden" class="itemProductVehicleStatus"/>' +
        '    <input id="itemDTOs0.isOldProduct" name="itemDTOs[0].isOldProduct" type="hidden" value=""/>' +
        '    <input id="itemDTOs0.vehicleBrandId" name="itemDTOs[0].vehicleBrandId" type="hidden" value=""/>' +
        '    <input id="itemDTOs0.vehicleModelId" name="itemDTOs[0].vehicleModelId" type="hidden" value=""/>' +
        '    <input id="itemDTOs0.vehicleYearId" name="itemDTOs[0].vehicleYearId" type="hidden" value=""/>' +
        '    <input id="itemDTOs0.vehicleEngineId" name="itemDTOs[0].vehicleEngineId" type="hidden" value=""/>' +
        '    <input id="itemDTOs0.productId" name="itemDTOs[0].productId" value="" type="hidden"/>' +
        '    <input id="itemDTOs0.hidden_productId" name="itemDTOs[0].hidden_productId" value="" type="hidden"/>' +
        '	<input id="itemDTOs0.productType" name="itemDTOs[0].productType" type="hidden" value=""/>' +
        '	<input id="itemDTOs0.purchasePrice" name="itemDTOs[0].purchasePrice" value="" type="hidden"/>' +
        '	<input id="itemDTOs0.inventoryAveragePrice" name="itemDTOs[0].inventoryAveragePrice" value="" type="hidden"/>' +
        '	<input id="itemDTOs0.productName" name="itemDTOs[0].productName"  class="table_input checkStringEmpty" value="" type="text" style="width:80%"/>' +
        '    <input type="hidden" name="itemDTOs[0].hidden_productName" />' +
        '<input type="button" class="edit1" onfocus="this.blur();" id="itemDTOs0.editbutton" name="itemDTOs[0].editbutton" onclick="searchInventoryIndex(this)" style="margin-left: 6px"/>' +
        '</td>' +
        '    <td>' +
        '	<input id="itemDTOs0.brand" name="itemDTOs[0].brand"  class="table_input checkStringEmpty" maxlength="100" value="" type="text"/>' +
        '    <input type="hidden" name="itemDTOs[0].hidden_brand" /></td>' +
        '    <td>' +
        '	<input id="itemDTOs0.spec" name="itemDTOs[0].spec"  class="table_input checkStringEmpty" value="" type="text"/>' +
        '    <input type="hidden" name="itemDTOs[0].hidden_spec" /></td>' +
        '    <td>' +
        '	<input id="itemDTOs0.model" name="itemDTOs[0].model"  class="table_input checkStringEmpty" value="" type="text"/>' +
        '    <input type="hidden" name="itemDTOs[0].hidden_model" /></td>' +
        '    <td>' +
        '	<input id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel" maxlength="50" value="" class="itemVehicleModel table_input checkStringEmpty"  type="text"/>' +
        '    <input type="hidden" id="itemDTOs0.hidden_vehicleModel" name="itemDTOs[0].hidden_vehicleModel" /></td>' +
        '    <td>' +
        '	<input id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand" maxlength="50" value="" class="itemVehicleBrand table_input checkStringEmpty"  type="text"/>' +
        '    <input type="hidden" id="itemDTOs0.hidden_vehicleBrand" name="itemDTOs[0].hidden_vehicleBrand" /></td>' +
        '    <td style="color:#FF6700;">' +
        '	<input id="itemDTOs0.price" kissfocus="on" name="itemDTOs[0].price" onchange="checkPrice(this)"  value="" class="itemPrice table_input checkNumberEmpty" type="text" data-filter-zero="true"/>' +
        '    </td>';
    if(APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier){
        trSample+='<td style="color:#FF0000;">'+
        '	<input id="itemDTOs0.amount" onclick="this.select()" name="itemDTOs[0].amount"  value="" class="itemAmount table_input checkNumberEmpty" type="text" data-filter-zero="true"/>' +
        '	<input id="itemDTOs0.amountHid" name="itemDTOs[0].amountHid" value="" class="itemAmountHid " type="hidden"/>' +
            '    </td>';
    }else{
        trSample+='<td style="color:#FF0000;">'+
            '	<input id="itemDTOs0.amount" kissfocus="on" name="itemDTOs[0].amount"  value="" class="itemAmount table_input checkNumberEmpty" type="text" data-filter-zero="true"/>' +
            '	<input id="itemDTOs0.amountHid" name="itemDTOs[0].amountHid" value="" class="itemAmountHid " type="hidden"/>' +
            '    </td>';
    }

    trSample+='<td>' +
        '       <input id="itemDTOs0.unit" name="itemDTOs[0].unit"  value="" class="itemUnit table_input checkStringEmpty" type="text"/>' +
        '       <input type="hidden" id="itemDTOs0.storageUnit" name="itemDTOs[0].storageUnit" value="" class="itemStorageUnit table_input"/>' +
        '       <input type="hidden" id="itemDTOs0.sellUnit" name="itemDTOs[0].sellUnit" value="" class="itemSellUnit table_input"/>' +
        '       <input type="hidden" id="itemDTOs0.rate" name="itemDTOs[0].rate" value="" class="itemRate table_input"/>' +
        '   </td>' +
        '    <td>' +
        '	<span id="itemDTOs0.total_span" name="itemDTOs[0].total"  class="itemTotalSpan" /></span><input type="hidden" id="itemDTOs0.total" name="itemDTOs[0].total" class="itemTotal"/>' +
        '    </td>' +
        '<td class="storage_bin_td">' +
        '<span id="itemDTOs0.storageBinSpan"  name="itemDTOs[0].storageBinSpan"></span>'+
        '<input id="itemDTOs0.storageBin" name="itemDTOs[0].storageBin" value="" class="table_input" type="hidden" style="width:90%"/> '+
        '</td>'+
        '    <td>' +
        '       <span id="itemDTOs0.inventoryAmountSpan" style="display: block;"></span>' +
        '       <input type="hidden" id="itemDTOs0.inventoryAmount" name="itemDTOs[0].inventoryAmount" value="" class="itemInventoryAmount table_input" readonly="readonly" type="text"/>' +
        '   </td>' +
        '   <td>' +
        '   <input id=itemDTOs0.businessCategoryName name="itemDTOs[0].businessCategoryName" type="text" class="table_input businessCategoryName" />' +
        '   <input id=itemDTOs0.businessCategoryId name="itemDTOs[0].businessCategoryId" type="hidden" class="table_input" />' +
        '   </td>'+
//        '    <td>' +
//        '	<input id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel"  value="" class="itemVehicleModel table_input"  type="text"/>' +
//        '    <input type="hidden" id="itemDTOs0.hidden_vehicleModel" name="itemDTOs[0].hidden_vehicleModel" /></td>' +
//        '    <td>' +
//        '	<input id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand"  value="" class="itemVehicleBrand table_input"  type="text"/>' +
//        '    <input type="hidden" id="itemDTOs0.hidden_vehicleBrand" name="itemDTOs[0].hidden_vehicleBrand" /></td>' +
        '    <td style="display:none">' +
        '	<input id="itemDTOs0.vehicleYear" name="itemDTOs[0].vehicleYear"  value="" class="itemVehicleYear table_input"  type="text"/>' +
        '    <input type="hidden" id="itemDTOs0.hidden_vehicleYear" name="itemDTOs[0].hidden_vehicleYear" /></td>' +
        '    <td style="display:none">' +
        '	<input id="itemDTOs0.vehicleEngine" name="itemDTOs[0].vehicleEngine"  value="" class="itemVehicleEngine table_input"  type="text"/>' +
        '    <input type="hidden" id="itemDTOs0.hidden_vehicleEngine" name="itemDTOs[0].hidden_vehicleEngine" /></td>' +
        '    <td style="border-right:none;">' +
        '	<a class="opera1" type="button" id="itemDTOs0.deletebutton" name="itemDTOs[0].deletebutton">删除</a>' +
        '    </td>' +
        '</tr>';

    return trSample;
}


function getTrSample2() {
    var trSample = '<tr class="item2 table-row-original bg">' +
        '    <td style="color:#6D8FB9;padding-left: 9px;">' +
        '	<input id="otherIncomeItemDTOList0.name" maxlength="50" name="otherIncomeItemDTOList[0].name" value="" class="table_input otherIncomeKindName checkStringEmpty" type="text"/>' +
        '    </td>' +

        '    <td>' +
        '	<input id="otherIncomeItemDTOList0.price" name="otherIncomeItemDTOList[0].price" maxlength="10" value="" style="width:80%;" class="table_input itemTotal otherIncomePrice checkStringEmpty" type="text" data-filter-zero="true"/>' +
        '    </td>' +
        '<td>' +
          '<input id="otherIncomeItemDTOList0.otherIncomeCostPriceCheckbox" name="otherIncomeItemDTOList[0].checkbox"'+
        ' maxlength="100" type="checkbox" class="otherIncomeCostPriceCheckbox" style="float:left; margin-right:4px;margin-top:3px;" />' +
          '<label style="float:left; margin-right:4px;"/></label>计入成本<span style="display:none;" id="otherIncomeItemDTOList0.otherIncomeSpan">' +
         '<input id="otherIncomeItemDTOList0.otherIncomeCostPrice" name="otherIncomeItemDTOList[0].otherIncomeCostPrice" value="" type="text"'+
         'class="table_input otherIncomeCostPrice checkStringEmpty" style="width:70px;" data-filter-zero="true"/></span>' +
          '</td> ' +
        '    <td>' +
        '	<input id="otherIncomeItemDTOList0.memo" maxlength="100" name="otherIncomeItemDTOList[0].memo" value="" class="table_input checkStringEmpty" type="text"/>' +
        '    </td>' +
        '    <td style="border-right:none;">' +
        '	<a class="operaMinus"  id="otherIncomeItemDTOList0.deletebutton" name="otherIncomeItemDTOList[0].deletebutton">删除</a>' +
        '    </td>' +
        '</tr>';
    return trSample;
}

function inventoryOrderAdd() {
    var tr = $(getTrSample()).clone();     //TODO 克隆模版，初始化所有的INPUT
    $(tr).find("input").val("");
    $(tr).find(".itemAmount,.itemTotal").bind("blur", function() {
        dataTransition.roundingSpanNumber("totalSpan");
        var format = $(this).val();
        format = dataTransition.rounding(format, 2);
        $(this).val() && $(this).val(format);
    });
    $(tr).find(".itemProductVehicleStatus").val("1");
    $(tr).find("input,span,a").each(function(i) {
        //去除文本框的自动填充下拉框
        if($(this).attr("type")=="text"){
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
    $(tr).appendTo("#table_productNo");
//    tableUtil.tableStyle('#table_productNo','.table_title,.s_tabelBorder');
    trCount++;
    isShowAddButton();
    return $(tr);
}

function inventoryOrderAdd2() {
    var tr = $(getTrSample2()).clone();     //TODO 克隆模版，初始化所有的INPUT
    $(tr).find("input").val("");

    $(tr).find("input,span,a").each(function(i) {
        //去除文本框的自动填充下拉框
        if($(this).attr("type")=="text"){
            $(this).attr("autocomplete", "off");
        }
        if (!this || !this.id)return;
        //replace id
        var idStrs = this.id.split(".");
        var idPrefix = idStrs[0];
        var idSuffix = idStrs[1];
        var domrows = parseInt(idPrefix.substring(22, idPrefix.length));

        var tcNum = trCount2;
        while (checkThisDom2(tcNum, idStrs[1])) {
            tcNum = ++tcNum;                       //TODO 计算行号，如果目标行号对应的元素存在，则该行号加一
        }
        //TODO 组装新的ID和NAME  Begin-->
        var newId = "otherIncomeItemDTOList" + tcNum + "." + idStrs[1];
        $(this).attr("id", newId);
        //replace name
        var nameStr = $(this).attr("name");
        if (nameStr == undefined || nameStr == '') {
            return true;
        }
        if ($(this).attr("name").split(".")[1]) {
            var newName = "otherIncomeItemDTOList[" + tcNum + "]." + $(this).attr("name").split(".")[1];
            $(this).attr("name", newName);
        }
        //TODO <-- End
        $(this).attr("autocomplete", "off");
    });
    $(tr).appendTo("#table_sale_otherIncome");
//    tableUtil.tableStyle('#table_sale_otherIncome','.table_title,.s_tabelBorder');
    trCount2++;
    isShowOtherIncomeAddButton2();
    return $(tr);
}

//判断是否显示+按钮
function isShowAddButton() {
    //如果初始化的话就默认加一行
    if ($(".item").size() <= 0) {
        $(".opera2").trigger("click");
    }
    $(".item .opera2").remove();
    var opera1Id = $(".item:last").find("td:last>a[class='opera1']").attr("id");
    if (!opera1Id) return;

    $(".item:last").find("td:last>a[class='opera1']").after('<a class="opera2" ' +
        ' id="itemDTOs' + (opera1Id.split(".")[0].substring(8)) + '.plusbutton">增加</a>');
}

//判断是否显示+按钮
function isShowOtherIncomeAddButton2() {
    //如果初始化的话就默认加一行
    if ($(".item2").size() <= 0) {
        $(".operaAdd").trigger("click");
    }
    $(".item2 .operaAdd").remove();
    var opera1Id = $(".item2:last").find("td:last>a[class='operaMinus']").attr("id");
    if (!opera1Id) return;

    $(".item2:last").find("td:last>a[class='operaMinus']").after(' <a class="operaAdd" ' +
        ' id="otherIncomeItemDTOList' + (opera1Id.split(".")[0].substring(22)) + '.plusbutton">增加</a>');
}



//TODO 如果所填单价低于采购价，需要弹出提示
function checkPrice(domObj) {
    if ($("#originOrderId") && $("#originOrderId").val()) {
        var domid = domObj.id;
        var domValue = parseFloat(domObj.value);
        var idPrefix = domid.split(".")[0];
        var newid = idPrefix + ".originSalesPrice"//".purchasePrice";
        var newValue = document.getElementById(newid).value;
        $(domObj).val(dataTransition.rounding($(domObj).val(), 2));
        domValue = parseFloat(domObj.value);
        setTotal();
        if (domValue < newValue) {
            if (!confirm("该商品的退货价低于销售价" + dataTransition.rounding(newValue, 2) + "元，是否确认退货?")) {
                invoiceCommon.selectAndFocusByNode($(this));
                return false;
            }
        }
        setTotal();
    } else {
        var domid = domObj.id;
        var domValue = parseFloat(domObj.value);
        var idPrefix = domid.split(".")[0];
        var newid = idPrefix + ".inventoryAveragePrice"//".purchasePrice";
        var newValue = document.getElementById(newid).value;
        $(domObj).val(dataTransition.rounding($(domObj).val(), 2));
        domValue = parseFloat(domObj.value);
        setTotal();
        if (domValue < newValue) {
            priceChangeTimeout = setTimeout(function(){
                nsDialog.jAlert("友情提示：该商品的销售价低于成本均价" + dataTransition.rounding(newValue, 2) + "元，请核查是否输入正确！");
            }, 100);
        }else if(domValue == newValue){
            priceChangeTimeout = setTimeout(function(){
                nsDialog.jAlert("友情提示：该商品的销售价等于成本均价" + dataTransition.rounding(newValue, 2) + "元，请核查是否输入正确！");
            }, 100);
        }
        setTotal();
    }



}

//TODO 计算“总额”
function setItemTotal() {
    var product_total=0;
    var amount_total = 0;
    $(".itemPrice").each(function (i) {
        var price = $(this).val();
        price = G.rounding(price, 2);
        var idPrefix = $(this).attr("id").split(".")[0];
        var amount = G.rounding($("#" + idPrefix + "\\.amount").val(), 2);
        var count = G.rounding(price * amount, 2);
        $("#" + idPrefix + "\\.total").val(count);
        $("#" + idPrefix + "\\.total_span").text(count ? count : '');
        $("#" + idPrefix + "\\.total_span").attr("title", count);
        product_total += count;
        amount_total += amount;
        htmlNumberFilter($("#" + idPrefix + "\\.amount").add("#" + idPrefix + "\\.total_span").add("#" + idPrefix + "\\.inventoryAmountSpan").add("#" + idPrefix + "\\.price"), true);
    });
    $("#productTotal").text(G.rounding(product_total,2));
    $("#amountTotal").text(G.rounding(amount_total, 2));
}

//TODO 将“总计”加入页面元素
function setTotal() {
    setItemTotal();
   $("#otherIncomeTotal").text(G.rounding(getOtherIncomeTotal(),2));
    var itemTotal = G.rounding(getItemTotal(),2);
    $("#total").text(itemTotal);
    $("#totalSpan").text(itemTotal);
    if (itemTotal != $("#total").val()) {
        $("#totalSpan").text(itemTotal);
        $("#total").val(itemTotal);
        //如果还没结算过，实收=总计
        if ($("#settledAmountHid").val() <= 0) {
            $("#settledAmount").val(itemTotal);
        }
        jQuery("#debt").val(G.rounding((jQuery("#total").val() - jQuery("#settledAmount").val()), 2));
        if ($("#debt").val() * 1 <= 0) {
            $("#input_makeTime_sale").hide();
            $("#huankuanTime").val("");
        }
    }
}

//TODO 打开历史记录页面，需要带入供应商
function getGoodsHistory(supplierName) {
    bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':"goodsHistory.do?method=createGoodsHistory&orderType=SALE&supplierName=" + encodeURIComponent(supplierName)});
}

//检查单据是否相同
function checkTheSame() {
    return invoiceCommon.checkSameItemForOrder("item");
}
function newOtherOrder(url) {
    if (openNewOrderPage()) {
        window.open(url, "_blank");
    } else {
        openOrAssign(url);
    }
}

function newSaleOrder(){
    if (openNewOrderPage()) {
        window.open($("#basePath").val() + "sale.do?method=getProducts&type=txn","_blank");
    }
}


function accountTypeChange() {

    var orderValue = jQuery("#status", window.parent.document).val();
    if (orderValue == "SALE_DONE" || orderValue == "SALE_DEBT_DONE"  || orderValue == "SALE_REPEAL") {
        bcgogo.checksession({parentWindow:window.parent,iframe_PopupBox:$("#iframe_PopupBox_account")[0],src:"sale.do?method=accountDetail&customerId="+$("#customerId").val()});
    } else {
        if (validateSalesInfo().validateResult) {
            bcgogo.checksession({parentWindow:window.parent,iframe_PopupBox:$("#iframe_PopupBox_account")[0],src:"sale.do?method=accountDetail&customerId="+$("#customerId").val()});
        }else{
            return {validateResult: false}
        }
    }
}

//单据结算前进行校验
function validateSalesInfo() {

    //自动删除最后的空白行
    var $last_tr = $("#table_productNo").find("tbody").find("tr:last");
    while ($("#table_productNo .item").size() != 1 && checkEmptyRow($last_tr)) {
        $last_tr.find("[a[id$='.deletebutton']").click();
        $last_tr = $("#table_productNo").find("tbody").find("tr:last");
    }
    //
    if (checkTheSame()) {
        alert("单据有重复内容，请修改或删除。");
        return {validateResult: false};

    }
    if (invoiceCommon.checkSameCommodityCode("item")) {
        alert("商品编码有重复内容，请修改或删除。");
        return {validateResult: false};
    }
    if (!$.trim($("#customer").val())) {
        $("#customer").val("**客户**");
    }
    var flag = 1;
    var reg = /^(([1-9]+[0-9]*.{1}[0-9]+)|([0].{1}[1-9]+[0-9]*)|([1-9][0-9]*)|([0][.][0-9]+[1-9]*))$/;//正数
    var checkItemPrice = false;
    //验证采购量大于0
    $(".itemPrice").each(function() {
        if (!$(this).val()) {
            $(this).val(0)
        }
        //验证输入的是正数
        if (!reg.test($.trim($(this).val())) || parseFloat($.trim($(this).val())) * 1 == 0) {
            checkItemPrice = 1;
            return {validateResult: false};
        }

    });

    //验证数量不为0
    var amountMsg = "";
    jQuery(".itemAmount").each(function() {
        if (!$(this).val()) {
            $(this).val(0)
        }
        if (parseFloat(jQuery.trim(jQuery(this).val())) * 1 == 0) {
            amountMsg = amountMsg + "\n第" + (jQuery(this).index(".itemAmount") + 1) + "行商品数量为0，此行内容无意义，请补充或删除";
            flag = 0;
        }

  });
  if (amountMsg != "") {
    alert(amountMsg);
    return {validateResult: false};
  }
  var lackMsg = "";

    //校验新商品
    $(".itemAmount").each(function() {
        if (G.Lang.isEmpty($(this).parents("tr").find("input[id$='productId']").val())) {
            lackMsg = lackMsg + "\n第" + ($(this).index(".itemAmount") + 1) + "行商品库存不足，无法销售";
        }
    });
    if (!APP_BCGOGO.Permission.Version.IgnorVerifierInventory) {
        if (lackMsg != "") {
            alert(lackMsg);
            return {validateResult: false};
        }
    }
    lackMsg = "";
    //todo 在找到更好方法前，暂时嵌套全局变量来控制不同店铺版本不同操作
    //如果是仓库   通过后台校验
    if (!APP_BCGOGO.Permission.Version.IgnorVerifierInventory && !APP_BCGOGO.Permission.Version.StoreHouse) {
        if (lackMsg != "") {
            alert(lackMsg);
            return {validateResult: false};
        }
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

    $(".itemAmountHid").each(function() {
        invoiceCommon.reSetDomVal(this, "", 0);
    });
    if ($.trim($("#mobile").val())) {
        var contract = document.getElementById("mobile");
        check.saveContact(contract);
    }

    //无号码欠款不能结算
    if ($("#debt").val() * 1 > 0 && !$.trim($("#mobile").val())) {
        alert("有欠款，且手机未设置，不能结算。");
        return {validateResult: false};
    }

    message = "您确定要结算吗？";
    if ($("#settledAmount").val() * 1 == 0) {
        message = "实收金额为0，您确定要结算吗？";
    }
    if (checkItemPrice) {
        var str = "";
        var msg = "";
        $(".itemPrice").each(function(i) {

            if (parseFloat($.trim($(this).val())) * 1 == 0) {
                str = str + "\n第" + ($(this).index(".itemPrice") + 1) + "行商品销售价为0，是否确认销售";
            }

            var idPrefix = $(this).attr("id").split(".")[0];
            var productName = $("#" + idPrefix + "\\.productName").val();
            if (productName == "") {
                msg = "第" + (i + 1) + "行，缺少品名";
            }
        });
        if (msg != "") {
            msg += "，请补充完整。\n"
            alert(msg);
            return {validateResult: false};
        }
        if (!confirm(str)) {
            return {validateResult: false};
        }
    }

    return {validateResult:true,flag:flag};
}

//设置结算相关信息
function normalAccount(){
    $("#cashAmount").val($("#settledAmount").val());//默认实收为现金
    $("#bankAmount,#bankCheckAmount,#memberAmount").val(0);
    $("#bankCheckNo,#accountMemberNo,#accountMemberPassword").val("");
}


$().ready(function(){

    $(".businessCategoryName").live("click focus keyup",function(event){

        event = event || event.which;

        var keyCode = event.keyCode;

        if(keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40)
        {
            return;
        }

        if($(this).val() != $(this).attr("hiddenValue"))
        {
            $("#"+this.id.split(".")[0]+"\\.businessCategoryId").val("");
            $(this).removeAttr("hiddenValue");
        }

        var obj = this;
        askForAssistDroplist(event,obj);
    });

    function askForAssistDroplist(event,obj) {
        var droplist = APP_BCGOGO.Module.droplist;
        clearTimeout(droplist.delayTimerId || 1);
        droplist.delayTimerId = setTimeout(function () {
            var droplist = APP_BCGOGO.Module.droplist;
            // 我们dummy 一个数据集， 这个数据集即符合 droplist 的需要。
            var categoryName=$(obj).val();
            var categoryId = $("#"+obj.id.split(".")[0]+"\\.businessCategoryId").val();
            var uuid = GLOBAL.Util.generateUUID();
            droplist.setUUID(uuid);
            APP_BCGOGO.Net.asyncGet({
                url:"category.do?method=getCategory",
                data:{
                    "uuid":uuid,
                    "keyWord":categoryName,
                    "now":new Date()
                },
                dataType:"json",
                success:function(result) {

                    if(null != result.data)
                    {
                        for(var i =0;i<result.data.length;i++)
                        {
                            if(result.data[i].label=="洗车" || result.data[i].label=="美容" || result.data[i].label=="精品"
                                || result.data[i].label=="机修" || result.data[i].label=="装潢" || result.data[i].label=="音响"
                                || result.data[i].label=="油漆" || result.data[i].label=="精洗" || result.data[i].label=="膜"
                                || result.data[i].label=="轮胎")
                            {
                                result.data[i].isEditable = false;
                                result.data[i].isDeletable = false;
                            }
                        }

                    }
                    droplist.show({
                        "selector":$(event.currentTarget),
                        "isEditable":true,
                        "originalValue":{label:categoryName,idStr:categoryId},
                        "data":result,
                        "saveWarning":"保存此修改影响全局数据",
                        "isDeletable":true,
                        "onSelect":function (event, index, data,hook) {
                            var id = data.idStr;
                            var name=data.categoryName;

                            $(hook).val(name);
                            $(hook).attr("hiddenValue",name);
                            $("#"+hook.id.split(".")[0]+"\\.businessCategoryId").val(id);
                            droplist.hide();
                        },

                        "onEdit":function (event, index, data,hook) {
//                            nsDialog.jAlert("修改此营业分类，其他使用此分类的服务或者商品也会随之修改！", null, function() {});
                        },

                        "onSave":function (event, index, data,hook) {
                            var id = data.idStr;
                            var name=$.trim(data.label);
//                            // TODO 保存数据到服务器端 AJAX 请求
                            APP_BCGOGO.Net.syncPost({
                                url:"category.do?method=updateCategoryName",
                                data:{
                                    "categoryId":id,
                                    "categoryName":name,
                                    "now":new Date()
                                },
                                dataType:"json",
                                success:function(jsonObject) {
                                    if(jsonObject.resu == "success")
                                    {
                                        data.label = name;
                                        data.categoryName = name;
                                        if("it is self"!=jsonObject.msg)
                                        {
                                            //遍历item 把此id的name 和hiddenvalue都变为最新的name
                                            $(".businessCategoryName").each(function(){
                                                var categoryId =  $("#"+this.id.split(".")[0]+"\\.businessCategoryId").val();

                                                if(categoryId == id)
                                                {
                                                    $(this).val(name);
                                                    $(this).attr("hiddenValue",name);
                                                }
                                            });
                                        }
                                    }
                                    else
                                    {
                                        nsDialog.jAlert("营业分类中已经有此分类！",null,function(){
                                            data.label = data.categoryName;
                                        });
                                    }
                                }
                            });
                        },

                        "onDelete":function(event, index, data) {
                            var r = APP_BCGOGO.Net.syncGet({
                                url: "category.do?method=deleteCategory",
                                data: {
                                    categoryId: data.idStr,
                                    now:new Date()
                                },
                                dataType: "json"
                            });
                            if(r == null || r.resu == "error") {
                                nsDialog.jAlert("删除失败！");
                            } else if(r.resu == "success") {
                                nsDialog.jAlert("删除成功！",null,function(){
                                    $("input[id$='.businessCategoryName']").each(function() {
                                        var idPrefix = this.id.split(".")[0];
                                        if($.trim($(this).val()) == $.trim(data.label)) {
                                            $(this).val("");
                                            $(this).removeAttr("hiddenValue");
                                            $("#"+idPrefix+"\\.businessCategoryId").val("");
                                        }
                                    });
                                });
                            }
                        },
                        "onKeyboardSelect":function(event, index, data, hook) {
                            // TODO;
                            $(hook).val(data.label);
                            if(null != data.idStr)
                            {
                                $("#"+hook.id.split(".")[0]+"\\.businessCategoryId").val(data.idStr);
                                $(hook).attr("hiddenValue",data.label);
                            }
                            else
                            {
                                $("#"+hook.id.split(".")[0]+"\\.businessCategoryId").val("");
                                $(hook).removeAttr("hiddenValue")
                            }
                        }
                    });
                }
            });
        }, 200);
    }

    $("#duizhan").bind("click",function(){

        toCreateStatementOrder($("#customerId").val(), "CUSTOMER_STATEMENT_ACCOUNT");
    });
});


function checkOtherInComeInfo(prefix)
{
    //检查name是否为空，money是否为空，money的有效性
    var name = $("#"+prefix+"\\.name").val();
    var price = $("#"+prefix+"\\.price").val();

    if(!name)
    {
        nsDialog.jAlert("请填写费用名称");
        return false;
    }

    if(!price)
    {
        nsDialog.jAlert("请填写费用金额");
        return false;
    }

    if(!APP_BCGOGO.Validator.stringIsPrice(price))
    {
        nsDialog.jAlert("请填写正确的金额！");
        return false;
    }
}

function checkEmptyRow2($tr) {
    var propertys = ["name"];
    var itemInfo = "";
    for (var i = 0,len = propertys.length; i < len; i++) {
        itemInfo += $tr.find("input[id$='." + propertys[i] + "']").val();
    }
    if (GLOBAL.Lang.isEmpty(itemInfo)) {
        return true;
    }
    return false;
}

function validateOtherIncomeInfo()
{

    //自动删除最后的空白行
    var $last_tr = $("#table_sale_otherIncome").find("tbody").find("tr:last");
    while ($last_tr.index() >=4  && checkEmptyRow2($last_tr)) {
        $last_tr.find("input[id$='.deletebutton']").click();
        $last_tr = $("#table_sale_otherIncome").find("tbody").find("tr:last");
    }

    var msg = "";
    $(".otherIncomeKindName").each(function(index){
        var name = $(this).val();
        var prefix = $(this)[0].id.split(".")[0];
        var price = $("#"+prefix+"\\.price").val();
        index ++;
        if($(".otherIncomeKindName").size()==1)
        {
            if(!name)
            {
                $last_tr.find("input[id$='.deletebutton']").click();
                $last_tr = $("#table_sale_otherIncome").find("tbody").find("tr:last");
            }
            else
            {
                if(!price)
                {
                    msg += "第"+index+"行缺少费用金额\n";
                }
                else
                {
                    if(!APP_BCGOGO.Validator.stringIsPrice(price))
                    {
                        msg += "第"+index+"行费用金额格式不对\n";
                    }
                }
            }
        }
        else
        {
            if(!name)
            {
                msg += "第"+index+"行缺少费用名称";
            }

            if(!price)
            {
                if(!name)
                {
                    msg += ",缺少费用金额\n"
                }
                else
                {
                    msg += "第"+index+"行缺少费用金额\n";
                }
            }
            else
            {
                if(!APP_BCGOGO.Validator.stringIsPrice(price))
                {
                    if(!name)
                    {
                        msg += ",费用金额格式不对\n"
                    }
                    else
                    {
                        msg += "第"+index+"行费用金额格式不对\n";
                    }
                }
            }
        }
    });
    return msg;
}

function checkThisDom2(tn, idstr) {
    if ($("#otherIncomeItemDTOList" + tn + "\\." + idstr)[0]) {
        return true;
    } else {
        return false;
    }
}

function submitAfterInputMobile()
{

    var msg = validateOtherIncomeInfo();

    if (!GLOBAL.Lang.isEmpty(msg)) {
        nsDialog.jAlert("费用列表 ：" + msg);
        return;
    }
    //结算校验逻辑放到  accountTypeChange 里面了
    accountTypeChange();
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

var showLicenceNo = function (dom, customerId, vehicleId) {
    var droplist = APP_BCGOGO.Module.droplist;
    var uuid = GLOBAL.Util.generateUUID();
    droplist.setUUID(uuid);
    APP_BCGOGO.Net.asyncGet({
        url: 'customer.do?method=getVehicleDTOListByCustomerId',
        data: {
            "customerIdStr": customerId,
            "now": new Date()
        },
        dataType: "json",
        success: function (response) {
            if (response && response.length) {
                var vehicleInfo = null;
                if (dom == null) {
                    if (vehicleId != null) {
                        $.each(response.length, function () {
                            this.id == vehicleId && (vehicleInfo = this);
                        });
                    } else if (response.length == 1) {
                        vehicleInfo = response[0];
                    }
                    if (vehicleInfo != null) {
                        $('#vehicleId').val(vehicleInfo.idStr);
                        $('#licenceNo').val(vehicleInfo.licenceNo);
                        $('#vehicleContact').val(vehicleInfo.contact);
                        $('#vehicleMobile').val(vehicleInfo.mobile);
                        setReadOnly()
                    }
                } else {
                     droplist.show({
                        "selector": $(dom),
                        "data": {
                            uuid: uuid,
                            data: $.map(response, function (n) {
                                n.label = n.licenceNo;
                                return n;
                            })
                        },
                        "onSelect": function (event, index, data, hook) {
                            $(hook).val(data.licenceNo);
                            getCustomerInfoByVehicleId(data.idStr);
                            droplist.hide();
                        }
                    });
                }
            }else{
                clearVehicleInfo();
            }
        }
    });
}

var searchLicenceNo = function (dom, searchWord) {
    var droplist = APP_BCGOGO.Module.droplist;
    searchWord = searchWord.replace(/\s/g, '');
    var uuid = GLOBAL.Util.generateUUID();
    droplist.setUUID(uuid);
    searchWord != '' && $.post('product.do?method=searchlicenseplate', {
        now: new Date().getTime(),
        plateValue: searchWord
    }, function (list) {
        droplist.show({
            "selector": $(dom),
            "data": {
                uuid: uuid,
                data: $.map(list, function (n) {
                    n.label = n.licenceNo;
                    return n;
                })
            },
            "onSelect": function (event, index, data, hook) {
                $(hook).val(data.licenceNo);
                getCustomerInfoByVehicleId(data.id);
                droplist.hide();
            }
        });
    }, 'json');
}


var getCustomerInfoByVehicleId = function(vehicleId) {
    APP_BCGOGO.Net.asyncGet({
        url: 'customer.do?method=searchCustomerByVehicleId',
        data: {
            "vehicleId": vehicleId,
            "now": new Date()
        },
        dataType: "json",
        success: function (response) {
            response && setCustomerInfo(response);
        }
    });
}

var getCustomerInfoByLicenceNo = function(licenceNo) {
    APP_BCGOGO.Net.asyncPost({
        url: 'customer.do?method=searchCustomerByLicenceNo',
        data: {
            "licenceNo": licenceNo,
            "now": new Date()
        },
        dataType: "json",
        success: function (response) {
            if (response) {
                setCustomerInfo(response);
            } else {
                var licenceNo = $('#licenceNo').val();
                $('#customerId').val() != '' && clearCustomerInfo();
                $('#licenceNo').val(licenceNo);
            }
        }
    });
}

var setCustomerInfo = function(customerInfo) {
    $('#customer').val(customerInfo.name);
    $('#customerId').val(customerInfo.idStr);
    $('#contact').val(customerInfo.contact);
    $('#mobile').val(customerInfo.mobile);
    if (customerInfo.vehicleDTOList != null) {
        setVehicleInfo(customerInfo.vehicleDTOList[0]);
    }else{
        clearVehicleInfo();
    }
    setReadOnly();
}

var clearCustomerInfo = function (notClearVehicleInfo) {
    $("#customer, #customerId, #contactId, #contact, #mobile, #hiddenMobile, #landline").val("");
    $("#receivable, #payable").text("0");
    $("#mobile").removeAttr("mustInputBySameCustomer");
    notClearVehicleInfo || clearVehicleInfo();
    clearReadOnly();
}

var setVehicleInfo = function(vehicle) {
    $('#vehicleId').val(vehicle.idStr);
    $('#vehicleContact').val(vehicle.contact);
    $('#vehicleMobile').val(vehicle.mobile);
    setReadOnly();
}

var clearVehicleInfo = function (notClearVehicleLicenceNo) {
    $('#vehicleId').val('');
    notClearVehicleLicenceNo || $('#licenceNo').val('');
    $('#vehicleContact').val('');
    $('#vehicleMobile').val('');
    setReadOnly();
}

var clearReadOnly = function () {
    $('#contact, #mobile, #vehicleContact, #vehicleMobile').removeAttr('disabled');
}

var rememberReadOnly = function () {
    var list = [];
    $('#contact, #mobile, #vehicleContact, #vehicleMobile').each(function () {
        if ($(this).attr('disabled')) {
            list.push(this);
        }
    });
    return function () {
        list.length && $.each(list, function () {
            $(this).attr('disabled', true);
        });
    }
}

var setReadOnly = function () {
    var list = $('#mobile, #vehicleContact, #vehicleMobile');
    if ($('#wholesalerVersionNode').val() == 'false') {
        list = list.add('#contact');
    }
    $('#customerId').val() != '' && list.each(function () {
        if($(this).val() != ''){
            $(this).attr('disabled', true);
        }else{
           $(this).removeAttr('disabled');
        }
    });
}

var init = function () {
    if (window.location.href.indexOf('method=getOrderByDraftOrderId') > 0) {
        var customerId = $('#customerId').val();
        var vehicleId = $('#vehicleId').val();
        customerId && APP_BCGOGO.Net.asyncGet({
            url: 'sale.do?method=searchCustomerById',
            data: {
                "customerId": customerId,
                "now": new Date()
            },
            dataType: "json",
            success: function (response) {
                if(response.success && response.infos && response.infos.length){
                    var customerInfo =  response.infos[0];
                    G.Lang.isNotEmpty(customerInfo.contact) && $('#contact').attr('disabled',true);
                    G.Lang.isNotEmpty(customerInfo.mobile) && $('#mobile').attr('disabled',true);
                }
            }
        });
        vehicleId && APP_BCGOGO.Net.asyncGet({
            url: 'customer.do?method=searchCustomerByVehicleId',
            data: {
                "vehicleId": vehicleId,
                "now": new Date()
            },
            dataType: "json",
            success: function (response) {
                response && response.vehicleDTOList && $.each(response.vehicleDTOList,function(){
                    if(this.idStr == vehicleId){
                        G.Lang.isNotEmpty(this.contact) && $('#vehicleContact').attr('disabled',true);
                        G.Lang.isNotEmpty(this.mobile) && $('#vehicleMobile').attr('disabled',true);
                    }
                })
            }
        });
    } else {
        $('#customerId').val() != '' && showLicenceNo(null, $('#customerId').val());
        setReadOnly();
    }
}

var initEventBind = function () {
    $('#licenceNo').bind('focus',function () {
        var customerId = $("#customerId").val();
        if (customerId != '') {
            showLicenceNo(this, customerId);
        } else {
            searchLicenceNo(this, $(this).val());
        }
    }).bind('input',function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingLicenseNoFilter($(this).val()));
            $(this).val() != '' && searchLicenceNo(this, $(this).val());
        }).change(function () {
            $(this).val() != '' && getCustomerInfoByLicenceNo($(this).val());
        }).bind('blur', function () {
            if ($(this).val() == '') {
                $('#customerId').val() != '' && clearVehicleInfo();
            } else {
                setTimeout(function () {
                    if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber($('#licenceNo').val().replace(/-/g, ''))) {
                        nsDialog.jAlert("输入的车牌号码不符合规范，请检查！", null, function () {
                            $("#licenceNo").focus();
                        });
                    }
                }, 500);
            }
        });

    $('#vehicleContact').bind('input', function () {
        $(this).val($(this).val().replace(/[^\u4e00-\u9fa5a-zA-Z0-9\-]/g, ''));
    });
    $('#vehicleMobile').bind('input',function () {
        $(this).val($(this).val().replace(/[^0-9]/g, ''));
    }).bind('change', function () {
            $(this).val() != '' && check.inputVehicleMobileBlur(this);
        });
}

$(function () {
    initEventBind();
    init();
});






