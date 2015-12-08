/**
 * 所有单据的父类 包含单据公用的js
 */
$(document).ready(function () {
    var currentPayColor,currentFuColor;
    $("#duizhan").hover(function(){
        currentPayColor = $(".payMoney").css("color");
        currentFuColor = $(".fuMoney").css("color");
        $(".payMoney,.fuMoney").css("color","#ffffff");
    },function(){
        $(".payMoney").css("color",currentPayColor);
        $(".fuMoney").css("color",currentFuColor);
    });
    //各个单据时间修改
    $("#orderVestDate").datetimepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": "",
        "onSelect": function (dateText, inst) {
            $("#dialog-confirm-text").html("单据日期与当前日期不一致，是否确定修改单据日期？");
            var This = inst.input || inst.$input;
            var orderId = This.attr("orderid");
            var orderType = This.attr("ordertype");
            //判断单据类型
            if (orderType) {
                //如果选在非当前的时间 提醒逻辑
                var newValue = This.val();
                if (!newValue) return;
                if (GLOBAL.Util.getDate(newValue).getTime() - GLOBAL.Util.getDate(GLOBAL.Date.getCurrentFormatDate()).getTime() > 0) {
                    $(This).datetimepicker("hide");

                    // 这里使用异步处理 为了临时 fixed bug 7617
                    nsDialog.jAlert("请选择今天之前的时间!", "信息提示", function(){
                        setTimeout(function(){
                            var date = new Date();
                            $(This).val(GLOBAL.Date.getCurrentFormatDate() + " " + date.getHours() + ":" + date.getMinutes());
                        }, 200);
                    });
                    return;
                }
                if ((GLOBAL.Util.getDate(GLOBAL.Date.getCurrentFormatDate()).getTime() - GLOBAL.Util.getDate(newValue).getTime() > 0)) {
                    if(orderType=="sale"||orderType=="purchase"||orderType=="REPAIR"){
                         var date = new Date();
                        $("#confirm_account_date_span").text(newValue.split(" ")[0]);
                        $("#confirm_current_date_span").text(GLOBAL.Date.getCurrentFormatDate());
                        $("#dialog-confirm-account-date").dialog({
                            resizable: true,
                            title: "提醒",
                            height: 180,
                            width: 380,
                            modal: true,
                            closeOnEscape: false
                        });
                    }else{
                        $("#dialog-confirm").dialog('open');
                    }

                }
                $(This).attr("lastvalue", newValue);
            } else {
                $(This).blur();
            }
        }
    });

    //确认单据时间
    $("#dialog-confirm-account-date .ok_btn").click(function(){
        $("#dialog-confirm-account-date").dialog("close");
         var date = new Date();
        if($("#confirm_account_date_radio").attr("checked")){
            $("#confirm_account_date").val($("#confirm_account_date_span").text()+ " " + dateUtil.getCurrentTime().split(" ")[1]);
        }else{
            $("#confirm_account_date").val(dateUtil.getCurrentTime());
        }
        if(getOrderType()=="REPAIR"&&$.isFunction(doAccount)){
            doAccount();
        }
    });

    $("#dialog-confirm-account-date .cancel_btn").click(function(){
        $("#dialog-confirm-account-date").dialog("close");
        var date = new Date();
        $("#orderVestDate").val(GLOBAL.Date.getCurrentFormatDate() + " " + date.getHours() + ":" + date.getMinutes());
//        $("#endDateStr").focus();
    });

    $("#dialog-confirm").dialog({
        resizable: false,
        autoOpen: false,
        height: 140,
        modal: true,
        buttons: {
            "确定": function () {
                $(this).dialog("close");
            }, "取消": function () {
                $("#orderVestDate").val(GLOBAL.Date.getCurrentFormatDateMin());
                $(this).dialog("close");
            }
        },
        close: function () {
//            $("#orderVestDate").val(GLOBAL.Date.getCurrentFormatDate());
        }
    });
    $("#dialog:ui-dialog").dialog("destroy");

    //输入过滤
    $("#mobile,#qq,#account").bind("keyup click", function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });
    $("#landline,#fax").bind("keyup click", function() {
        $(this).val($(this).val().replace(/[^\d\-]+/g, "").replace(/\-+/g, "-"));
        if ($(this).val().charAt(0) == '-') {
            $(this).val("");
        }
    });

    //防止鼠标粘贴非法字符
    if(/msie/i.test(navigator.userAgent)) {
        // TODO IE下的待定
    } else {
        if(document.getElementById("mobile")) {
            document.getElementById("mobile").addEventListener("input", checkNumberInput, false);
        }
        if(document.getElementById("account")) {
            document.getElementById("account").addEventListener("input", checkNumberInput, false);
        }
        if(document.getElementById("qq")) {
            document.getElementById("qq").addEventListener("input", checkNumberInput, false);
        }
        if(document.getElementById("landline")) {
            document.getElementById("landline").addEventListener("input", checkTelInput, false);
        }
        if(document.getElementById("fax")) {
            document.getElementById("fax").addEventListener("input", checkTelInput, false);
        }
    }
    function checkNumberInput() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    }
    function checkTelInput() {
        $(this).val($(this).val().replace(/[^\d\-]+/g, "").replace(/\-+/g, "-"));
        if($(this).val().charAt(0) == '-') {
            $(this).val("");
        }
    }

    $("#landline").blur(function() {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($(this).val()) && $(this).val() != "") {
            alert("输入的座机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
        }
    });
    $("#fax").blur(function() {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($(this).val()) && $(this).val() != "") {
            alert("输入的传真号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
        }
    });
    $("#email").blur(function() {
        if (!APP_BCGOGO.Validator.stringIsEmail($(this).val()) && $(this).val() != "") {
            alert("输入的邮箱可能不正确，为了不影响您的后续使用，请确认后重新输入！");
        }
    });
    $("#qq").blur(function() {
        var qq = document.getElementById("qq").value;
        if (qq != "" && qq.length < 5) {
            alert("输入的QQ号可能不正确，为了不影响您的后续使用，请确认后重新输入！");
        }
    });
    if(!G.isEmpty($("#draftOrderIdStr").val())){
        $("#print_div").show();
    }
});

/**
 * 如果含有冲帐，返回true, 不含冲帐返回false
 * @param orderType SALE, REPAIR, WASH
 * @param orderId
 */
function validateRepealStrikeSettled(orderType, orderId) {
    var hasStrike = false;
    APP_BCGOGO.Net.syncAjax({
        url: "txn.do?method=validateRepealStrikeSettled",
        type: "POST",
        dataType: "json",
        data: {
            orderType: orderType,
            orderId: orderId
        },
        success: function (json) {
            if (json.success) {
                hasStrike = false;
            } else {
                nsDialog.jAlert(json.msg, json.title);
                hasStrike = true;
            }
        },
        error: function () {
            nsDialog.jAlert("验证冲账信息时出错，请重试。");
            hasStrike = true;
        }
    });
    return hasStrike;
}

/**
 * 如果仓库不存在返回true,
 * @param orderType SALE, REPAIR,
 * @param orderId
 */
function validatorStoreHouseOrderRepeal(toStorehouseId, orderType, orderId) {
    var flag = false;
    APP_BCGOGO.Net.syncAjax({
        url: "txn.do?method=validatorStoreHouseOrderRepeal",
        type: "POST",
        dataType: "json",
        data: {
            orderType: orderType,
            orderId: orderId,
            toStorehouseId:toStorehouseId
        },
        success: function (json) {
            if (json.success) {
                flag = true;
                if ($("#selectStorehouseDialog").dialog("isOpen") === true) {
                    $("#selectStorehouseDialog").dialog("close");
                }
            } else {
                if ($("#selectStorehouseDialog").dialog("isOpen") === true) {
                    $("#_storehouseId").attr("data-order-id", orderId);
                    //获取 仓库选项
                    APP_BCGOGO.Net.syncAjax({
                        url: "storehouse.do?method=getAllStoreHouseDTOs",
                        type: "POST",
                        dataType: "json",
                        success: function (data) {
                            if (data) {
                                $("#_storehouseId option").remove();
                                $.each(data, function(i, item) {
                                    $("#_storehouseId").append("<option value='" + item.idStr + "'>" + item.name + "</option>");
                                });
                            }
                        }
                    });
                } else {
                    $("#selectStorehouseDialog").dialog({
                        width: 350,
                        modal: true,
                        resizable: false,
                        beforeclose: function(event, ui) {
                            $("#_storehouseId").attr("data-order-id", "");
                            return true;
                        },
                        open: function() {
                            $("#_storehouseId").attr("data-order-id", orderId);
                            //获取 仓库选项
                            APP_BCGOGO.Net.syncAjax({
                                url: "storehouse.do?method=getAllStoreHouseDTOs",
                                type: "POST",
                                dataType: "json",
                                success: function (data) {
                                    if (data) {
                                        $("#_storehouseId option").remove();
                                        $.each(data, function(i, item) {
                                            $("#_storehouseId").append("<option value='" + item.idStr + "'>" + item.name + "</option>");
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
                flag = false;
            }
        },
        error: function () {
            nsDialog.jAlert("数据异常");
            flag = false;
        }
    });
    return flag;
}

function checkSupplierInfo() {
    if (!GLOBAL.isEmpty($("#mobile").val()) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#mobile").val())) {
        alert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
        return false;
    }
    if (!GLOBAL.isEmpty($("#landline").val()) && !APP_BCGOGO.Validator.stringIsTelephoneNumber($("#landline").val()) ) {
        alert("输入的座机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
        return false;
    }
    if (!GLOBAL.isEmpty($("#fax").val()) && !APP_BCGOGO.Validator.stringIsTelephoneNumber($("#fax").val())) {
        alert("输入的传真号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
        return false;
    }
    if (!GLOBAL.isEmpty($("#qq").val()) && !APP_BCGOGO.Validator.stringIsQq($("#qq").val())) {
        alert("QQ号格式错误，为了不影响您的后续使用，请确认后重新输入！");
        return false;
    }
    if (!GLOBAL.isEmpty($("#email").val()) && !APP_BCGOGO.Validator.stringIsEmail($("#email").val())) {
        alert("邮箱格式错误，为了不影响您的后续使用，请确认后重新输入！");
        return false;
    }
    return true;
}

function showOperationLog(){
    var tbSize = 0;
    $(".showOperationLog_div").each(function(){
        tbSize += $(this).find("tr").size() + 1;
    });
    var height = tbSize > 10 ? 620: 'auto';
    $("#showOperationLog_div").dialog({
        resizable: false,
        width:500,
        height: height,
        modal: true,
        closeOnEscape: false,
        buttons:{
            "关闭":function(){
                $(this).dialog("close");
            }
        }
    });
}

function isEmptyOrderItem($item){
    var flag=false;
    if(G.isEmpty($item)){
        return true;
    }
    var idPrefix = '';
    if($($item).find("[id$='.productName']").attr("id") != undefined) {
        idPrefix = $($item).find("[id$='.productName']").attr("id").split(".")[0];
    }

    var productId = $.trim($("#" + idPrefix + "\\.productId").val());
    var productName = $.trim($("#" + idPrefix + "\\.productName").val());
    var brand = $.trim($("#" + idPrefix + "\\.brand").val());
    var spec = $.trim($("#" + idPrefix + "\\.spec").val());
    var model = $.trim($("#" + idPrefix + "\\.model").val());
    var inventoryAmount = $.trim($("#" + idPrefix + "\\.inventoryAmount").val()); //todo ?
    if (productName == '' && brand == '' && spec == '' && model == '' && inventoryAmount == 0 ) {
        flag=true;
    }
    return flag;
}

/**
 *
 */
function deleteEmptyItem(){
    if( $(".item").size()<=1){
        return;
    }
    $(".item").each(function(i) {
        if(isEmptyOrderItem($(this))){
            $(this).remove();
        }
    });
    if(getOrderType()=="INVENTORY_CHECK"){
        showAddBtn();
    }else{
        isShowAddButton();
    }
}

function hasNewProduct(){
    var msg = "";
    if($(".item").size()==1&&isEmptyOrderItem($(".item"))){
        return "";
    }
    $(".item").each(function(i) {
        var productId = $.trim($(this).find("[id$='.productId']").val());
        if (G.isEmpty(productId)) {
            msg += "第" + (i+1) + "行，包含新产品";
            addBgColor(i);
        }
    });
    return msg;
}

//背景颜色高亮
function addBgColor(i){
    if(getOrderType() == 'INVENTORY_CHECK'){
        $("#table_productNo").find("tr").eq(i+2).addClass("red_color");
    }
}

//在线销售单
function toSalesOrder(salesOrderId){
    window.open("sale.do?method=toSalesOrder&salesOrderId="+salesOrderId);
}

