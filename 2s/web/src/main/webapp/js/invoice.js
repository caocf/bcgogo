//TODO 该文件的脚本用于处理施工单
var needPhoneNum = true;
var flag;
var btnType;
var message;
var timeOutId;
var bcgogoAjaxQuery = App.Module.wjl.ajaxQuery;
var invoiceCommon = App.Module.wjl.invoiceCommon;

var changeFlag = false;
var priceChangeTimeout;

// TODO: 所有单据修改状态时加此提示.  未用到 +1
//window.onbeforeunload = function() {
//     if(changeFlag ==true){
//         if (/Firefox[\/\s](\d+)/.test(navigator.userAgent) && new Number(RegExp.$1) >= 4) {
//             if (confirm('您的内容尚未保存，确定要离开本页吗？')) {
//                 history.go();
//             } else {
//                 window.setTimeout(function() {
//                     window.stop();
//                 }, 1);
//             }
//         } else {
//             return '您的内容尚未保存，确定要离开本页吗？';
//         }
//     }
//}

function afterSubmitMsg() {
    if (getOrderType() == "INSURANCE_ORDER") {
        return;
    }
    if (G.Lang.isEmpty(submitBtnType) && G.Lang.isEmpty(submitResultMsg)) {
        submitBtnType = G.Util.getUrlParameter("btnType");
        submitResultMsg = G.Util.getUrlParameter("resultMsg");
    }
    if (G.Lang.isEmpty(submitBtnType) && G.Lang.isEmpty(submitResultMsg)) {
        return;
    }
    var resultMsgPrompt;
    if (submitResultMsg == "success") {
        if (submitBtnType == "dispatch") {
            resultMsgPrompt = "派单成功";
        } else if (submitBtnType == "change") {
            resultMsgPrompt = "改单成功";
        } else if (submitBtnType == "done") {
            resultMsgPrompt = "车辆已完工";
        } else if (submitBtnType == "settled") {
            resultMsgPrompt = "结算完成";
        } else if (submitBtnType == "repeal") {
            resultMsgPrompt = "已成功作废";
        }
        showMessage.fadeMessage("40%", "20%", 2000, 2000, resultMsgPrompt);
    } else if (submitResultMsg == "failure") {
        if (submitBtnType == "dispatch") {
            resultMsgPrompt = "派单";
        } else if (submitBtnType == "change") {
            resultMsgPrompt = "改单";
        } else if (submitBtnType == "done") {
            resultMsgPrompt = "完工操作";
        } else if (submitBtnType == "settled") {
            resultMsgPrompt = "结算";
        } else if (submitBtnType == "repeal") {
            resultMsgPrompt = "作废";
        } else {
            resultMsgPrompt = "提交";
        }
        alert(resultMsgPrompt + "失败，请重试或联系客服。");
    }
}

function jumpFromWashBeauty() {
    var isEmpty = G.Lang.isEmpty;
    var getParam = G.Util.getUrlParameter;
    if (isEmpty($("#brand").val()) && getParam("brand") != 'undefined' && isEmpty($("#model").val()) && getParam("model") != 'undefined') {
        $("#brand").val(getParam("brand"));
        $("#model").val(getParam("model"));
    }
    if (isEmpty($("#customerName").val()) && isEmpty($("#customerId").val()) && isEmpty($("#mobile").val()) && isEmpty($("#landLine").val())) {
        $("#customerName").val(getParam("customerName"));
        $("#customerId").val(getParam("customerId"));
        $("#mobile").val(getParam("mobile"));
        $("#landLine").val(getParam("landLine"));
    }
    if (isEmpty($("#vehicleContact").val()) && isEmpty($("#vehicleMobile").val())) {
        $("#vehicleContact").val(getParam("vehicleContact"));
        $("#vehicleMobile").val(getParam("vehicleMobile"));
    }
}

$(function () {
    initDuiZhanInfo();
    afterSubmitMsg();
    jumpFromWashBeauty();
    var submitHandler = function (form) {
        //验证是否都为空，都为空直接提示
        if (!$("#pageType").val()) { //
            if (isEmptyItem() && isEmptyService()) {
                alert("施工单和材料单均未填写");
                return;
            }
            //加入手机的判断
            if ($.trim($("#mobile").val())) {
                var mobile = document.getElementById("mobile");
                var telephone = document.getElementById("landLine");
                var message = check.inputMobile(mobile, telephone);
                if (message == "yes" || message == "success") {
                    $("#mobile").select();
                    $("#mobile").focus();
                    return;
                }
            }


        }
        if (btnType == "account") {
            var message = "你确定要结算吗？";
            if (!confirm(message)) {
                return;
            }
        }

        //如果品名，品牌，规格，型号为(无)，就设置为''
        $("input[name$='.productName'],input[name$='.brand'],input[name$='.spec'],input[name$='.model']").each(function () {
            if ($.trim($(this).val()) == '(无)') {
                $(this).val('');
            }
        });
        $("#saveBtn,#finishBtn,#accountBtn").attr("disabled", true);
        //purchasePrice,price,total如果为空，赋值为0
        $("input[name$='.purchasePrice'],input[name$='.price'],input[name$='.total'],.cPurchasePrice").each(function () {
            if (!$.trim($(this).val())) {
                $(this).val(0);
            }
        });
        //如果是空，则赋值为零
        if (!$.trim($("#settledAmount").val())) {
            $("#settledAmount").val(0);
        }
        form.submit();
        $("#saveDraftBtn").attr('disabled', true);

    };


    var bcgogoValidator = App.Module.wjl.bcgogoValidator;
    var rules = {
        licenceNo: {
            required: true
        },
        customer: {
            required: true
        },
        endDateStr: {
            //            required: true,
            isPreDate: [$("#startDateStr").val()]
        },
        startMileage: {
            number: true
        },
        settledAmount: {
            isBig: [0]
        }
    };
    var messages = {
        licenceNo: {
            required: "请填写车牌号"
        },
        customer: {
            required: "请填写姓名"
        },
        endDateStr: {
            //            required: "请输入预约出厂时间",
            isPreDate: "出厂时间不能早于入厂时间"
        },
        startMileage: {
            number: "进厂里程请输入数字"
        },
        settledAmount: {
            isBig: "请输入大于0的实收金额"
        }
    };
    bcgogoValidator.setRules(rules);
    bcgogoValidator.setMessages(messages);
    bcgogoValidator.setSubmitHandler(submitHandler);
    bcgogoValidator.validate($("#repairOrderForm")[0]);

    $("#addAppointBtn").bind("click",function (e) {
        e.preventDefault();
        addAppointService();
    });
    //默认添加一条预约服务
    addAppointService();
    bindAppointInputAction();

    if (getOrderType() == "REPAIR") {
        $(".bcgogo_menupanel a[href^='washBeauty.do?method=getCustomerInfoByLicenceNo'], .Jsibling li[href^='washBeauty.do?method=getCustomerInfoByLicenceNo']").live("mouseover", function() {
            var washBeautyUrl = $(this).attr("href");
            $(this).unbind().die().bind("click", function(e) {
                e.preventDefault();
                e.stopPropagation();
                addParamToWashBeautyLink(washBeautyUrl);
            });
        });
    }
});

function addAppointService() {
    var lineNum = $("#handover_info_table tr").last().attr("lineNum");
    if (stringUtil.isEmpty(lineNum)) {
        return;
    }
    lineNum++;
    var tr_appoint = '<tr style="height: 25px;" lineNum="' + lineNum + '"><td colspan="3"><input type="hidden" name="appointServiceDTOs[' + lineNum + '].idStr" value="" /><input type="text" class="checkStringEmpty txt appointName" style="color:#AEAEAE;" name="appointServiceDTOs[' + lineNum + '].appointName" value="服务名称">';
    tr_appoint += '<input type="text" class="checkStringEmpty txt appointDate" name="appointServiceDTOs[' + lineNum + '].appointDate" readonly="true" style="margin-left:16px;" /></td>';
    tr_appoint += '<td><input class="opera1 delAppointBtn" isDef="true" type="button"></td></tr>';
    $("#handover_info_table").append(tr_appoint);
    $(".appointDate").datepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": "",
        "onClose":function() {
            ajaxToDeleteAppointService(this);
        },
        "onSelect": function(dateText, inst) {
            var lastValue = $(this).attr("lastValue");
            if (lastValue == dateText) {
                return;
            }

            if (dateText) {
                var myDate = G.Date.getCurrentFormatDate();
                if (myDate.replace(/[- ]+/, "") > dateText.replace(/[- ]+/, "")) {
                    nsDialog.jAlert("请选择今天及以后的日期。");
                    $(this).val("");
                } else {
                    ajaxToSaveOrUpdateAppointService(this);
                }
                $(this).attr("lastValue", dateText);
            }
        }
    });
}

function bindAppointInputAction() {
    $(".appointName").live("focus", function() {
        if ($(this).val() == "服务名称") {
            $(this).val("");
        }
    });
    $(".appointName").live("blur", function(e) {
        var tr_appoint = $(this).closest('tr');
        var appointServiceId = $(tr_appoint.find("input").get(0)).val();
        var appointName = $(tr_appoint.find("input").get(1)).val();
        var appointDate = $(tr_appoint.find("input").get(2)).val();
        if (G.isEmpty($(this).val())) {
            $(this).val("服务名称");
        }
        if($(this).val()=="服务名称"){
            $(this).css("color","#AEAEAE");
        }else{
            $(this).css("color","#515151");
        }
        if (G.isEmpty($(this).val()) && G.isEmpty(appointServiceId)) {
            return;
        }
        if ($.trim($(this).val()).length > 50) {
            nsDialog.jAlert("服务名称过长，请重新输入！");
            return;
        }
        if (G.isNotEmpty(appointServiceId) && (G.isEmpty(appointName) || G.isEmpty(appointDate))) {
            ajaxToDeleteAppointService(this);
        } else {
            ajaxToSaveOrUpdateAppointService(this);
        }
    });
//     $(".delAppointBtn").unbind();
    $(".delAppointBtn").live("click", function(e) {
        var e = e || event;
        var target = e.srvElement || e.target;
        var tr_appoint = $(target).closest('tr');
        var tr_size = tr_appoint.closest('table').find('tr').size();

        var appointServiceId = $(tr_appoint.find("input").get(0)).val();
        if (!stringUtil.isEmpty(appointServiceId)) {
            $("<div id='delConfirmDiv' >预约服务已保存，是否确定删除？</div>").dialog({
                resizable: true,
                title: "删除确认",
                height: 150,
                width: 300,
                modal: true,
                closeOnEscape: false,
                buttons: {
                    "确定": function() {
                        $(this).dialog("close");
                        ajaxToSaveOrUpdateAppointService(target, "LOGIC_DELETE");
                        if ($(target).attr("isDef") && tr_size == 4) {
                            $(tr_appoint.find("input").get(0)).val("");
                            $(tr_appoint.find("input").get(1)).val("服务名称");
                            $(tr_appoint.find("input").get(2)).val("");
                        } else {
                            $(tr_appoint).remove();
                        }

                    },
                    "取消": function() {
                        $(this).dialog("close");
                    }
                }
            });
        } else {
            if ($(target).attr("isDef") && tr_size == 3) {
                $(tr_appoint.find("input").get(0)).val("");
                $(tr_appoint.find("input").get(1)).val("服务名称");
                $(tr_appoint.find("input").get(2)).val("");
            } else {
                $(tr_appoint).remove();
            }
        }

    });


}

function ajaxToDeleteAppointService(dom) {
    var tr_appoint = $(dom).closest('tr');
    var appointServiceId = $(tr_appoint.find("input").get(0)).val();
    var appointName = $(tr_appoint.find("input").get(1)).val();
    var appointDate = $(tr_appoint.find("input").get(2)).val();
    var tr_size = tr_appoint.closest('table').find('tr').size();
    var customerId = $("#customerId").val();
    var vehicleId = $("#vechicleId").val();
    var isDef = $(tr_appoint).find(".delAppointBtn").attr("isDef");
    if (appointName == "服务名称") appointName = "";
    if (stringUtil.isEmpty(appointServiceId) || stringUtil.isEmpty(customerId) || stringUtil.isEmpty(vehicleId)) {
        return;
    }
    if (stringUtil.isEmpty(appointName) || stringUtil.isEmpty(appointDate)) {
        var data = {
            idStr: appointServiceId,
            customerId: customerId,
            vehicleId: vehicleId,
            appointName: appointName,
            appointDate: appointDate,
            maintainTimeStr:$("#byId").val(),
            insureTimeStr:$("#bxId").val(),
            examineTimeStr:$("#ycId").val(),
            maintainMileage:$("#maintainMileage").val(),
            operateType: "LOGIC_DELETE"
        };
        var url = "unitlink.do?method=addOrUpdateCustomerVehicle&" + Math.random() * 10000000;
        App.Net.syncPost({
            url: url,
            dataType: "json",
            data: data,
            success: function (json) {
                if (jsonUtil.isEmpty(json)) {
                    return;
                }
                var appointServiceId = json.appointServiceId;
                $(tr_appoint.find("input").get(0)).val(appointServiceId);
                $(tr_appoint).find("input").attr("disabled", false);
                showMessage.fadeMessage("45%", "24%", "slow", 3000, "预约服务删除成功！");
            },
            error: function () {
                $(tr_appoint).find("input").attr("disabled", false);
                showMessage.fadeMessage("45%", "24%", "slow", 3000, "网络异常！");
            }
        });

        if (isDef && tr_size == 4) {
            $(tr_appoint.find("input").get(0)).val("");
            $(tr_appoint.find("input").get(1)).val("服务名称");
            $(tr_appoint.find("input").get(2)).val("");
        } else {
            $(tr_appoint).remove();
        }

    }
}

function ajaxToSaveOrUpdateAppointService(dom, operateType) {
    var tr_appoint = $(dom).closest('tr');
    var appointServiceId = $(tr_appoint.find("input").get(0)).val();
    var appointName = $(tr_appoint.find("input").get(1)).val();
    var appointDate = $(tr_appoint.find("input").get(2)).val();
    if (stringUtil.isEmpty(appointName) || stringUtil.isEmpty(appointDate) || appointName == "服务名称") {
        return;
    }

    var customerId = $("#customerId").val();
    var vehicleId = $("#vechicleId").val();
    if (stringUtil.isEmpty(customerId) || stringUtil.isEmpty(vehicleId)) {
        return;
    }
    $(tr_appoint).find("input").attr("disabled", true);
    if (stringUtil.isEmpty(operateType)) {
        operateType = "";
    }
    var data = {
        idStr: appointServiceId,
        customerId: customerId,
        vehicleId: vehicleId,
        appointName: appointName,
        appointDate: appointDate,
        maintainTimeStr:$("#byId").val(),
        insureTimeStr:$("#bxId").val(),
        examineTimeStr:$("#ycId").val(),
        maintainMileage:$("#maintainMileage").val(),
        operateType: operateType
    };
    var url = "unitlink.do?method=addOrUpdateCustomerVehicle&" + Math.random() * 10000000;
    App.Net.syncPost({
        url: url,
        dataType: "json",
        data: data,
        success: function (json) {
            if (jsonUtil.isEmpty(json)) {
                return;
            }
            var appointServiceId = json.appointServiceId;
            $(tr_appoint.find("input").get(0)).val(appointServiceId);
            $(tr_appoint).find("input").attr("disabled", false);
            showMessage.fadeMessage("45%", "24%", "slow", 3000, "预约服务保存成功！");
        },
        error: function () {
            $(tr_appoint).find("input").attr("disabled", false);
            showMessage.fadeMessage("45%", "24%", "slow", 3000, "网络异常！");
        }
    });
}

//TODO 维修单提交

function repairOrderSubmit() {
    if (!validateDate()) {
        return false;
    }

    idPrefixLastModified = null;
    $("#repairOrderForm").attr('action', 'repair.do?method=dispatchRepairOrder');

    if (flag == 1) { //表单标识可以提交时提交
        btnType = "save";
        repairOrderFormSubmit(btnType);
    }
}

//TODO 单据完工功能

function repairOrderFinish() {
    $("#repairOrderForm").attr('action', 'repair.do?method=finishRepairOrder');
    if (flag == 1) {
        btnType = "finish";
        repairOrderFormSubmit(btnType);
    }
}

function repairOrderAccount() {
    //添加校验
    if (flag == 1) {
        //如果品名，品牌，规格，型号为(无)，就设置为''
        $("input[name$='.productName'],input[name$='.brand'],input[name$='.spec'],input[name$='.model']").each(function() {
            if ($.trim($(this).val()) == '(无)') {
                $(this).val('');
            }
        });

        //purchasePrice,price,total如果为空，赋值为0
        $("input[name$='.purchasePrice'],input[name$='.price'],input[name$='.total'],.cPurchasePrice").each(function() {
            if ($.trim($(this).val()) == '') {
                $(this).val(0);
            }
        });
        //如果是空，则赋值为零
        if ($.trim($("#settledAmount").val()) == '') {
            $("#settledAmount").val(0);
        }

        var result = false;
        $("select[name$='.consumeType']").each(function() {
            if ($(this).val() == "TIMES") {
                bcgogo.checksession({
                    "parentWindow": window.parent,
                    'iframe_PopupBox': $("#iframe_PopupBox_account")[0],
                    'src': 'txn.do?method=accountDetail'
                });
                accountDetailPopAdjust();
                result = true;
                return;
            }
        });

        if (result == true) {
            return;
        }

        $("#cashAmount").val($("#settledAmount").val());

        $("#repairOrderForm").attr("action", "repair.do?method=accountRepairOrder");
        if (flag == 1) {
            btnType = "account";
            repairOrderFormSubmit(btnType);
        }

    }
}

function getLackProductsByOrderItems() {
    var items = $(".item1 "),
        params = {},$_item,i = 0;
    $.each(items, function (index, item) {
        $_item = $(item);
        if ($_item.find("input[name$='.productName']").val() && isThisItemLack($_item)) {
            params['productDTOs[' + i + '].productId'] = $_item.find("input[name$='.productId']").val();
            params['productDTOs[' + i + '].name'] = $_item.find("input[name$='.productName']").val();
            params['productDTOs[' + i + '].brand'] = $_item.find("input[name$='.brand']").val();
            params['productDTOs[' + i + '].spec'] = $_item.find("input[name$='.spec']").val();
            params['productDTOs[' + i + '].model'] = $_item.find("input[name$='.model']").val();
            params['productDTOs[' + i + '].commodityCode'] = $_item.find("input[name$='.commodityCode']").val();
            params['productDTOs[' + i + '].vehicleBrand'] = $_item.find("input[name$='.vehicleBrand']").val();
            params['productDTOs[' + i + '].vehicleModel'] = $_item.find("input[name$='.vehicleModel']").val();
            params['productDTOs[' + i + '].unit'] = $_item.find("input[name$='.unit']").val();
            params['productDTOs[' + i + '].amount'] = Number($_item.find("input[name$='.amount']").val()) - Number($_item.find(".itemInventoryAmount").val());
            i++;
        }
    });
    return params;
}

//校验求购商品
var validateCreatePreBuyOrder = {
    callback: null,
    fn: function (config) {
        var btnType = config['btnType'],
            callback = config['callback'];
        this.callback = callback;
        //非派单
        if (btnType != "save" || $("#id").val()) {
            callback();
        }
        //派单
        else {
            //无缺料
            var params = getLackProductsByOrderItems();
            if ($.isEmptyObject(params)) {
                callback();
                return;
            }
            App.Net.syncPost({
                url: "preBuyOrder.do?method=preBuyOrderFilter",
                dataType: "json",
                data: params,
                success: function (products) {
//                        showCreatePreBuyOrder(products);
//                     return;
                    //缺料&& 没有发布过求购
                    if (!G.isEmpty(products)){
                        var data={};
                        for(var i=0;i<products.length;i++){
                            var product=products[i];
                            var commodityCode = ( product['commodityCode'] ? product['commodityCode'] : "");
                            var productId = ( product['productId'] ? product['productId'] : "");
                            var productName = ( product['name'] ? product['name'] : "");
                            var brand = ( product['brand'] ? product['brand'] : "");
                            var model = ( product['model'] ? product['model'] : "");
                            var spec = ( product['spec'] ? product['spec'] : "");
                            var vehicleBrand = ( product['vehicleBrand'] ? product['vehicleBrand'] : "");
                            var vehicleModel = ( product['vehicleModel'] ? product['vehicleModel'] : "");
                            var unit = ( product['unit'] ? product['unit'] : "");
                            data["itemDTOs["+i+"].commodityCode"]=commodityCode;
                            data["itemDTOs["+i+"].productName"]=productName;
                            data["itemDTOs["+i+"].productId"]=productId;
                            data["itemDTOs["+i+"].brand"]=brand;
                            data["itemDTOs["+i+"].model"]=model;
                            data["itemDTOs["+i+"].spec"]=spec;
                            data["itemDTOs["+i+"].vehicleBrand"]=vehicleBrand;
                            data["itemDTOs["+i+"].vehicleModel"]=vehicleModel;
                            data["itemDTOs["+i+"].unit"]=unit;
                            data["itemDTOs["+i+"].amount"]=1;
                        }
                        APP_BCGOGO.Net.asyncAjax({
                            url: "preBuyOrder.do?method=ajaxSavePreBuyOrder",
                            type: "POST",
                            cache: false,
                            data:data,
                            dataType: "json",
                            success: function (json) {
                                console.debug("auto save lack preBuyOrder success")
                            }

                        });
                    }
                }
            });
           callback();

        }
    }
};


function repairOrderFormSubmit(btnType) {
    if ($("#storehouseId")) {
        $("#storehouseId").removeAttr("disabled");
    }
    $('#repairOrderForm').ajaxSubmit({
        url: "txn.do?method=validateRepairOrder&validateType=" + btnType,
        dataType: "json",
        type: "POST",
        success: function(result) {
            if (result) {
                if (result.success) {
                    validateCreatePreBuyOrder.fn({
                        btnType: btnType,
                        callback: function () {
                            $("#repairOrderForm").submit();
                        }
                    })
                } else {
                    if (result.operation && result.operation == "update_product_inventory" && result.data) {
                        updateProductInventory(result.data, true);
                        checkAndUpdateStorehouseStatus();
                        alert(result.msg);
                    } else if (result.operation && result.operation == "update_product_inventory+CONFIRM_ALLOCATE_RECORD") {
                        updateProductInventory(result.data, false);
                        checkAndUpdateStorehouseStatus();
                        if (confirm(result.msg)) {
                            $("#repairOrderForm").attr("target", "_blank");
                            var oldAction = $("#repairOrderForm").attr("action");
                            $("#repairOrderForm").attr("action", "allocateRecord.do?method=createAllocateRecordByRepairOrder");
                            $("#repairOrderForm").submit();
                            $("#repairOrderForm").removeAttr("target");
                            $("#repairOrderForm").attr("action", oldAction);
                        }
                    } else if (result.operation && result.operation == "refresh_repair_order" && result.data) {
                        alert(result.msg);
                        window.location = "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + result.data;
                    } else if (result.operation && result.operation == "ALERT") {
                        nsDialog.jAlert(result.msg);
                        checkAndUpdateStorehouseStatus();
                    } else if (result.operation && result.operation == "confirm_deleted_product") {
                        if (confirm(result.msg)) {
                            $("#repairOrderForm").submit();
                        } else {
                            checkAndUpdateStorehouseStatus();
                        }
                    } else if (!result.success && result.operation == "PROMPT_EXIST_REPAIR_ORDER" && !G.isEmpty(result.data)) {
                        var orderLink = "<a href='txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId="
                            + result.data.orderId + "' target='_blank' class='blue_col' style='color:#0094FF'>" + result.data.receiptNo + "</a>";
                        nsDialog.jConfirm("车牌号 " + $("#licenceNo").val() + " 当前存在未结算的施工单 " + orderLink + "，本单据不能继续操作！是否要保存为草稿？", "友情提示", function(returnVal) {
                            if (returnVal) {
                                $("#saveDraftBtn").click();
                            }
                        });
                    } else {
                        alert(result.msg);
                        checkAndUpdateStorehouseStatus();
                    }
                    $("#saveBtn,#finishBtn,#accountBtn,#saveDraftBtn").removeAttr("disabled");
                    ;
                }
            }
        },
        error: function() {
            alert("出现异常！");
        }
    });
}
//目前只有库存信息要更新

function updateProductInventory(data, isShowMsg) {
    $("#table_productNo_2").find(".item1").each(function() {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            $(this).find("input[id$='.inventoryAmount']").val(G.normalize(data[productId], "0"));
            $(this).find("span[id$='.inventoryAmountSpan']").html(G.normalize(data[productId], "0"));
            $(this).find("span[id$='.inventoryAmountSpan']").show();
            $(this).find("span[id$='.inventoryAmountSpan']").parents("td").find(".j_new").hide();

        }
    });
    isLack();
}
function updateProductStorageBin(data) {
    $("#table_productNo_2").find(".item1").each(function () {
        var productId = $(this).find("input[id$='.productId']").val();
        if (!G.Lang.isEmpty(productId)) {
            $(this).find("span[id$='.storageBinSpan']").html(G.normalize(data[productId], ""));
        }
    });
}

function accountDetailPopAdjust() {
    $("#iframe_PopupBox_account").css("top", "350px");
}
//进厂里程提醒次数
var startMileageTimes = 0;
// 添加验证方法 (验证日期前后顺序)
$.validator.addMethod("isPreDate", function(value, element, param) {
    return this.optional(element) || isPreDate(value, $("#startDateStr").val())
}, "出厂时间不能早于入厂时间");

// 添加验证方法 (固定电话)
$.validator.addMethod("isTel", function(value, element) {
    return this.optional(element) || isTel(value);
}, "请输入正确的电话号码");

function isTel(s) { //TODO 验证是否为手机号码
    var patrn = /^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})(\d{7,8})|(\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$/;
    return patrn.exec(s);
}

// 添加验证方法 (实收大于0)
$.validator.addMethod("isBig", function(value, element, param) {
    return this.optional(element) || isBig(value, param[0]);
}, "请输入大于0的实收金额");

function isBig(value, i) { //TODO 判断value是否大于i
    if ($("#repairOrderForm").attr('action') == 'repair.do?method=accountRepairOrder') {
        var a = parseFloat(value);
        var b = parseFloat(i);
        return a >= b;
    }
    return true;
}

function reMoveAttrs() { //TODO 移除相关元素的disabled状态，例如修改了维修单的车牌号后需要执行的操作
    $("#brand").removeAttrs("disabled");
    $("#model").removeAttrs("disabled");
    $("#year").removeAttrs("disabled");
    $("#contact").removeAttrs("disabled");
    $("#engine").removeAttrs("disabled");
    $("#customerName").removeAttrs("disabled");
    $("#mobile").removeAttrs("disabled");
    $("#landLine").removeAttrs("disabled");
    $("input[id$='businessCategoryName']").removeAttr("disabled");
    $("#vehicleContact").removeAttr("disabled");
    $("#vehicleMobile").removeAttr("disabled");
    $('#vehicleColor').removeAttr("disabled");
    $('#vehicleChassisNo').removeAttr("disabled");
    $('#vehicleEngineNo').removeAttr("disabled");
}

$(document).ready(function() {
    checkAndUpdateStorehouseStatus();


    //TODO 键盘上下快速选取   待重写
    // $(":text,.i_operate")

    $("#startMileage").bind("keyup blur", function() {
        $("#startMileage").val(App.StringFilter.inputtingPriceFilter($("#startMileage").val(), 1));
    });
    //手机号码验证：zhangchuanlong
    $("#mobile").blur(function() {
        var landline = document.getElementById("landLine");
        var mobile = document.getElementById("mobile");
        if (mobile.value) {
            check.inputCustomerMobileBlur(mobile, landline);
        }
    });
    $("#landLine").blur(function() {
        if (this.value) {
            check.inputCustomerLandlineBlur($("#landLine")[0]);
        }
    });
    $("#vehicleMobile").live("keyup", function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });
    $("#vehicleMobile").live("blur", function() {
        var $vehicleMobile = $(this);
        $vehicleMobile.val($vehicleMobile.val().replace(/[^\d]+/g, ""));
        if (!G.Lang.isEmpty($(this).val())) {
            if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($vehicleMobile.val())) {
                nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！",null,function(){
                    $vehicleMobile.focus();
                });
                return;

            }
        }
//        if (this.value != "") {
//            var vehicleMobile = document.getElementById('vehicleMobile');
//            check.inputVehicleMobileBlur(vehicleMobile);
//        }
    });
    //btnType用于标识提交时的提示信息
    isLack();
    $("#input_makeTime").hide(); //默认隐藏设置还款时间
    //2011-12-13 表格绑定回车键事件
    $($(".table_title")[0]).next().addClass("item table-row-original");
    $($(".table_title")[1]).next().addClass("item1 table-row-original");

    $("select[id$='.consumeType']").each(function() {
        $("#" + this.id.split(".")[0] + "\\.consumeType").val(this.value);
    });


    //库存如果为负，就变0
    $(".itemInventoryAmount").each(function(i) {
        if ($(this).val() <= 0) {
            var count = 0;
            var count1 = count.toFixed(1);
            $(this).val(count1);
        }
    });

    //如果还没结算过，实收=总计
    if ($("#settledAmountHid").val() <= 0) {
        if ($("#settledAmountHid").val() * 1 == 0 && $("#status").val() == "REPAIR_SETTLED") {
            $("#settledAmount").val(0);
        } else {
            $("#settledAmount").val($("#total").val());
            $("#debt").val(0);
        }
    }

    //判断是否不是新增维修单
    if ($("#id")[0] && $("#id")[0].value && $("#id").val().length > 0) {
        //如果不是新增，把“派单”改成“改单”
        $("#saveA").html("改单");
        $("#saveBtn").removeClass("sendSingle").addClass("changeSingle");
    }
    //判断维修单状态是否是“已结算”
    if ($("#status").val() == "REPAIR_SETTLED") {
        //如果是“已结算”，锁定“改单”和“完工” 和 '结算'按钮
        $("#saveBtn").attr('disabled', 'disabled');
        $("#finishBtn").attr('disabled', 'disabled');
        //        $("#accountBtn").attr('disabled', 'disabled');
        //        $("#saveDraftBtn,#saveDraft").hide().attr('disabled', 'disabled');
        //施工项目、材料单、预计交车日期、实收金额、欠款、设置还款日期等不能再标记
        //如果进厂里程为空，就赋值0
        $("#repairOrderForm input").not($("#repairDebtAccount,#accountBtn,#printBtn,#cancelBtn,#nullifyBtn,#copyInput,#isPrint,#startMileage,#vehicleHandover,#createSalesReturn,#toInsuranceBtn")).attr('disabled', 'disabled');
        $("#repairOrderForm select").not($("#btnMoney")).attr('disabled', 'disabled');
        $("#repairDebtAccount").removeAttr("disabled");
        $("#copyInput").removeAttr("disabled");
        if ($.trim($("#startMileage").val()) > 0) {
            $("#startMileage").attr('disabled', 'disabled');
        } else {
            $("#startMileage").live('blur', function() {
                if ($("#status").val() != "REPAIR_SETTLED") {
                    return;
                }

                if ($("#id").val() == "" || $("#id").val() == null) {
                    return;
                }

                if ($("#startMileage").val() == null || $("#startMileage").val() == undefined || $("#startMileage").val() == "") {
                    $('.startMileInput').hide();
                    $('.startMile').show();
                    return;
                }
                $("#startMileage").val(App.StringFilter.inputtingPriceFilter($("#startMileage").val(), 1));
                $.ajax({
                    type: "POST",
                    url: "txn.do?method=updateStartMileage&" + Math.random() * 10000000,
                    cache: false,
                    data: {
                        repairOrderIdStr: $("#id").val(),
                        startMileageStr: $("#startMileage").val()
                    },
                    success: function(data) {
                        showMessage.fadeMessage("45%", "24%", "slow", 3000, "进厂里程数补录成功！");

                        $("#startMileage").attr('disabled', 'disabled');
                        $('.startMileInput').hide();
                        $('.startMile').text($("#startMileage").val());
                        $('.startMile').show();
                        $('.td_startMile').attr({
                            'title': $("#startMileage").val() + '公里'
                        });
                        $('.td_startMile').tooltip({
                            delay: 0
                        });
                    },
                    error: function(e) {
                        showMessage.fadeMessage("45%", "24%", "slow", 3000, "进厂里程数补录失败！");
                    }
                });

            });
        }
    }
    //判断维修单状态是否是“已作废”
    if ($("#status").val() == "REPAIR_REPEAL") {
        $("#saveBtn").attr('disabled', 'disabled');
        $("#finishBtn").attr('disabled', 'disabled');
        $("#accountBtn,").attr('disabled', 'disabled');
        //施工项目、材料单、预计交车日期、实收金额、欠款、设置还款日期等不能再标记
        $("#repairOrderForm input").not($("#printBtn,#cancelBtn,#nullifyBtn,#washWorker,#normalCash,#chargeCash,#chargeTimes,#normalWashBtn,#chargeBtn,#sureWashBtn,#toInsuranceBtn")).attr('disabled', 'disabled');
        $("#repairOrderForm select").not($("#btnMoney")).attr('disabled', 'disabled');
        $("#copyInput").removeAttr("disabled");
    }
    //初始化页面，如果是销售，就隐藏施工单
    //    if ($("input[name='serviceType']:checked").val() == 2) $("#table_task").hide();
    if ($("#shoppingSell").hasClass("title_hover")) {
        $("#table_task").hide();
        $("#saveBtn,#finishBtn").hide();
        $("#saveA,#saveB").hide();
    }
    $("#carWash,#carMaintain,#shoppingSell").click(function(event) {
        var target = $(event.target);
        initInvoiceJSP(target, false);
    });
//    tableUtil.tableStyle('#table_task', '.i_tabelBorder,.table_title');
//    tableUtil.tableStyle('#table_productNo_2', '.i_tabelBorder,.table_title');


    if (!customerId) {
        $(".table_btnAll").hide(); //隐藏客户信息  , 预约服务
    }
    //todo 验证工时费
    $(".serviceTotal").blur(function() {
        if (!$(this).val() * 1) {
            if (!confirm("施工内容的工时费为0，确认吗？")) {
                $(this).focus();
                return;
            }
        }

    });

    $(".consumeType").change(function() {
        consumeTypeChange(this);
    });

    $("#saveBtn").click(function() { //TODO 派单单击事件
        reMoveAttrs(); //TODO 移除相关元素的disabled状态
        $("#repairOrderForm").attr("action", "repair.do?method=dispatchRepairOrder");
        if (!App.Validator.stringIsLicensePlateNumber($('#licenceNo').val().replace(/\s|\-/g, ""))) {
            alert("输入的车牌号码不符合规范，请检查！");
            return false;
        }

        if (isEmptyItem() && isEmptyService()) {
            alert("施工单和材料单均未填写");
            return false;
        } else {
            $("#btnType").val('saveBtn');
            if (!isEmptyItem() && !checkStorehouseSelected()) {
                return;
            }
        }
        if (isMaterialSame()) {
            alert('材料单有重复项目或者空行，请修改或删除。');
            return false;
        }
        if (isRepairSame()) {
            alert('施工单有重复项目或者空行，请修改或删除。');
            return false;
        }
        if (isCommodityCodeSame("item1")) {
            alert('材料单商品编码有重复内容，请修改或删除。');
            return false;
        }
        $("#fuelNumber").removeAttrs("disabled");

        $("#table_task :text").rules("remove");
        $("#table_productNo_2 :text").rules("remove");

        if (endDateStrValidate($("#startDateStr").val()) == true) {
            alert("请输入正确的进厂时间！");
            return false;
        }
        if ($("#endDateStr").val() && $("#startDateStr").val() > $("#endDateStr").val()) {
            alert("预约出厂时间不能早于进厂时间，请修改!");
            return false;
        }
        var licenceNo = document.getElementById("licenceNo").value;
        var customer = document.getElementById("customerName").value;
        if (!licenceNo) {
            alert("请输入车牌号!");
            document.getElementById("licenceNo").focus();
            return false;
        }
        if (licenceNo && !customer) { //TODO 客户未填，以车牌号为客户名
            document.getElementById("customerName").value = licenceNo;
        }
        //        if (needPhoneNum == true && ! $.trim($("#mobile").val()) && !$.trim($("#landLine").val())) {
        //            needPhoneNum = confirm("为联系方便，手机和座机请输入一项。");
        //            if (needPhoneNum) {
        //                $("#mobile").focus();
        //                return;
        //            }
        //        }
        //进厂里程为空,自动算0
        if (!$.trim($("#startMileage").val())) {
            // 洗车时去掉验证，将里程数改为0
            document.getElementById("startMileage").value = "0.0";
        }

        flag = 1; //标识表单是否可以提交  1可以，0不可以
        var reg1 = /^([0]|([1-9]+[0-9]*.{1}[0-9]+)|([0].{1}[1-9]+[0-9]*)|([1-9][0-9]*)|([0][.][0-9]+[1-9]*))$/; //正数
        if (!isEmptyItem()) {
            $(".itemAmount").each(function() {
                //验证输入的是正整数
                if (!reg1.test($.trim($(this).val()))) {
                    flag = 0;
                    alert("请输入正确的数量！");
                    return;
                }
            });
        }
        //实收验证
        if ($.trim($("#settledAmount").val()) && !reg1.test($.trim($("#settledAmount").val()))) {
            alert("实收只能输入0~9和小数点（.），请重新修改！");
            return;
        }
        //欠款验证
        if ($.trim($("#debt").val()) && !reg1.test($.trim($("#debt").val()))) {
            alert("欠款只能输入0~9和小数点（.），请重新修改！");
            this.focus();
            return;
        }
        if (!isEmptyService()) {
            $(".serviceTotal").each(function() {
                //验证输入的是正数
                if ($.trim($(this).val()) && !reg1.test($.trim($(this).val()))) {
                    flag = 0;
                    alert("工时费请输入正确的价格！");
                    return;
                }
            });
        }
        //        }
        //加入手机的判断
        if ($.trim($("#mobile").val())) {
            var mobile = document.getElementById("mobile");
            var telephone = document.getElementById("landLine");
            var message = check.inputMobile(mobile, telephone);
            if (message == "yes" || message == "success") {
                return;
            }
        }
        //加入座机的判断
        if ($.trim($("#landLine").val())) {
            var mobile = document.getElementById("mobile");
            var telephone = document.getElementById("landLine");
            var message = check.inputTelephone(telephone, mobile);
            if (message == "yes" || message == "success") {
                return;
            }
        }
        //验证单价不为空，验证数量不为空
        if (!isEmptyItem()) {
            $(".itemPrice").each(function() {
                if ($(this).val()) {
                    $(this).rules("add", {
                        number: true,
                        min: 0,
                        messages: {
                            number: "第" + ($(this).index(".itemPrice") + 1) + "行商品单价输入数字",
                            min: "第" + ($(this).index(".itemPrice") + 1) + "行商品销售价为0，请补充价格"
                        }
                    });
                }
                if (!reg1.test($.trim($(this).val()))) {
                    flag = 0;
                    alert("请输入正确的销售价！");
                    return;
                }
            });
        }

        //验证商品品名不能为空
        if (!isEmptyItem()) {
            $("input[name$='.productName']").each(function() {
                if (!$.trim($(this).val())) {
                    $(this).rules("add", {
                        required: true,
                        messages: {
                            required: "第" + ($(this).index("input[name$='.productName']") + 1) + "行材料无品名，无法处理，请补充完整"
                        }
                    });
                }
            });
        }

        //验证单位是否为空
        if (!isEmptyItem()) {
            $("input[name$='.unit']").each(function() {
                if (!$.trim($(this).val())) {
                    flag = 0;
                    var unitIndex = $(this).index("input[name$='.unit']") + 1;
                    nsDialog.jAlert("第" + unitIndex + "行请输入商品的单位");
                    return;
                }
            });
        }


        //验证施工单内容，如果工时费或者备注有填写，就内容必填                    #table_task input[name$='.service'],#table_task input[name$='.total'],#table_task input[name$='.memo']
        if (!isEmptyService()) {
            $("#table_task input[name$='.service']").each(function() {
                if (!$.trim($(this).val()) && ($("#table_task input[name$='.total']").eq($(this).index("#table_task input[name$='.service']")).val() > 0 || $("#table_task input[name$='.memo']").eq($(this).index("#table_task input[name$='.service']")).val() != '')) {
                    $(this).rules("add", {
                        required: true,
                        messages: {
                            required: "第" + ($(this).index("#table_task input[name$='.service']") + 1) + "行请输入施工内容"
                        }
                    });
                }
            });
        }
        if (!isEmptyService()) {
            $("#table_task input[name$='.service']").each(function() {
                if ($.trim($(this).val()).length > 200) {
                    $(this).rules("add", {
                        maxlength: 200,
                        messages: {
                            maxlength: "第" + ($(this).index("#table_task input[name$='.service']") + 1) + "行请输入施工内容长度超过200字符"
                        }
                    });
                }
            });
        }


        //验证采购量大于0
        if (!isEmptyItem()) {
            $(".itemAmount").each(function() {
                if ($(this).val() <= 0) {
                    $(this).rules("add", {
                        required: true,
                        min: 0.001,
                        number: true,
                        messages: {
                            required: "第" + ($(this).index(".itemAmount") + 1) + "行商品数量为0，此行内容无意义，请补充或删除",
                            min: "第" + ($(this).index(".itemAmount") + 1) + "行商品数量为0，此行内容无意义，请补充或删除",
                            number: "第" + ($(this).index(".itemAmount") + 1) + "行商品数量请输入数字"
                        }
                    });
                }

                if (!reg1.test($.trim($(this).val()))) {
                    flag = 0;
                    alert("请输入正确的数量！");
                    return;
                }
            });
        }


        var msg = validateOtherIncomeInfo();
        if (!G.Lang.isEmpty(msg)) {
            nsDialog.jAlert("其他费用列表：" + msg);
            return;
        }

        $("#div_brand").css({
            'display': 'none'
        });
        $("#div_works").css({
            'display': 'none'
        });
        var vehicleBrand = document.getElementById("brand").value;
        var vehicleModel = document.getElementById("model").value;
        if (debug) {
            if (!confirm("[save] last:" + idPrefixLastModified)) {
                return false;
            }
        }
        if (!idPrefixLastModified) {
            repairOrderSubmit();
            return false;
        }
        var productName = document.getElementById(idPrefixLastModified + ".productName").value;
        if (productName == null || productName == "") {
            repairOrderSubmit();
            return false;
        }
        if ($(".item1:last").find("input[class='opera1']")[0]) {
            idPrefixLastModified = $(".item1:last").find("input[class='opera1']")[0].id.split(".")[0];
            exactSearchInventorySearchIndex();
        }
        repairOrderSubmit();
        return false;
    });

    //TODO 取消按钮，重载页面
    $("#cancelBtn").click(function() {
        if ($("#id").val()) {
            window.location = "txn.do?method=getRepairOrderByVehicleNumber&task=maintain";
        } else {
            window.location = "txn.do?method=getRepairOrderByVehicleNumber&cancle=noId&receiptNo=" + $("#receiptNo").val();
        }

    });
    //todo <-- End
    $("#finishBtn").click(function() {
        $("#btnType").val('finishBtn');
        if (!isEmptyItem() && !checkStorehouseSelected()) {
            return;
        }
        if (isMaterialSame()) {
            alert('材料单有重复项目或者空行，请修改或删除。');
            return false;
        }
        if (isRepairSame()) {
            alert('施工单有重复项目或者空行，请修改或删除。');
            return false;
        }
        if (isCommodityCodeSame("item1")) {
            alert('材料单商品编码有重复内容，请修改或删除。');
            return false;
        }
        if (validateDate() == false) {
            return false;
        }
        if (checkRepairPickingSwitchOn()) {
            if (!isEmptyItem() && !isReservedEnough()) {
                alert("您有商品未出库，无法完工，请先出库！");
                return false;
            }
        } else if (!isEmptyItem() && (isLack() == "true")) {
            if (!App.Permission.Version.StoreHouse) {
                alert("对不起,库存不足!");
                return false;
            }
        } else if (!isEmptyItem() && isnew() == "true") {
            alert("对不起,库存不足!");
            return false;
        }


        if (endDateStrValidate($("#startDateStr").val()) == true) {
            alert("请输入正确的进厂时间！");
            return false;
        }
        if ($("#endDateStr").val() && $("#startDateStr").val() > $("#endDateStr").val()) {
            alert("预约出厂时间不能早于进厂时间，请修改!");
            return false;
        }
        $("#fuelNumber").removeAttrs("disabled");
        $("#table_task :text").rules("remove");
        $("#table_productNo_2 :text").rules("remove");
        flag = 1;
        var reg1 = /^([0]|([1-9]+[0-9]*.{1}[0-9]+)|([0].{1}[1-9]+[0-9]*)|([1-9][0-9]*)|([0][.][0-9]+[1-9]*))$/; //正数
        var reg2 = /^[0-9]*[1-9][0-9]*$/; //正整数
        //TODO 验证工时费
        if (!isEmptyService()) {
            $(".serviceTotal").each(function() {
                //验证输入的是正数
                if ($.trim($(this).val()) && !reg1.test($.trim($(this).val()))) {
                    flag = 0;
                    alert("工时费请输入正确的价格！");
                    return;
                }
            });
        }

        var licenceNo = document.getElementById("licenceNo").value;
        var customer = document.getElementById("customerName").value;
        if (!licenceNo) {
            alert("请输入车牌号!");
            document.getElementById("licenceNo").focus();
            return false;
        }
        if (licenceNo && !customer) {
            document.getElementById("customerName").value = licenceNo;
        }

        //加入手机的判断
        if ($.trim($("#mobile").val())) {
            var mobile = document.getElementById("mobile");
            var telephone = document.getElementById("landLine");
            var message = check.inputMobile(mobile, telephone);
            if (message == "yes" || message == "success") {
                return;
            }
        }

        //实收验证
        if ($.trim($("#settledAmount").val()) && !reg1.test($.trim($("#settledAmount").val()))) {
            alert("实收只能输入0~9和小数点（.），请重新修改！");
            return;
        }
        //欠款验证
        if ($.trim($("#debt").val()) && !reg1.test($.trim($("#debt").val()))) {
            alert("欠款只能输入0~9和小数点（.），请重新修改！");
            this.focus();
            return;
        }


        //加入座机的判断
        if ($.trim($("#landLine").val())) {
            var mobile = document.getElementById("mobile");
            var telephone = document.getElementById("landLine");
            var message = check.inputTelephone(telephone, mobile);
            if (message == "yes" || message == "success") {
                return;
            }
        }
        //验证施工单内容，如果工时费或者备注有填写，就内容必填                    #table_task input[name$='.service'],#table_task input[name$='.total'],#table_task input[name$='.memo']
        if (!isEmptyService()) {
            $("#table_task input[name$='.service']").each(function() {
                if (!$.trim($(this).val()) && ($("#table_task input[name$='.total']").eq($(this).index("#table_task input[name$='.service']")).val() > 0 || $("#table_task input[name$='.memo']").eq($(this).index("#table_task input[name$='.service']")).val() != '')) {
                    $(this).rules("add", {
                        required: true,
                        messages: {
                            required: "第" + ($(this).index("#table_task input[name$='.service']") + 1) + "行请输入施工内容"
                        }
                    });
                }
            });
        }
        //验证是否缺料
        if (!isEmptyItem()) {
            //验证单价不为空，验证数量不为空
            $(".itemPrice").each(function() {
                if ($(this).val()) {
                    $(this).rules("add", {
                        number: true,
                        min: 0,
                        messages: {
                            number: "第" + ($(this).index(".itemPrice") + 1) + "行商品购买数量请输入数字",
                            min: "第" + ($(this).index(".itemPrice") + 1) + "行商品销售价为0，请补充价格"
                        }
                    });
                }
                if (!reg1.test($.trim($(this).val()))) {
                    flag = 0;
                    alert("请输入正确的销售价！");
                    return;
                }
            });

            //验证商品的数量
            $("input[name$='.unit']").each(function() {
                if (!$.trim($(this).val())) {
                    flag = 0;
                    var unitIndex = $(this).index("input[name$='.unit']") + 1;
                    nsDialog.jAlert("第" + unitIndex + "行请输入商品的单位");
                    return;
                }
            })


            //验证单价不为空，验证数量不为空
            $(".itemAmount").each(function() {
                if ($(this).val() <= 0) {
                    $(this).rules("add", {
                        required: true,
                        min: 0.001,
                        number: true,
                        messages: {
                            required: "第" + ($(this).index(".itemAmount") + 1) + "行商品数量为0，此行内容无意义，请补充或删除",
                            min: "第" + ($(this).index(".itemAmount") + 1) + "行商品数量为0，此行内容无意义，请补充或删除",
                            number: "第" + ($(this).index(".itemAmount") + 1) + "行商品数量请输入数字"
                        }
                    });
                }
                if (!reg1.test($.trim($(this).val()))) {
                    flag = 0;
                    alert("请输入正确的数量！");
                    return;
                }
            });
        }

        var msg = validateOtherIncomeInfo();

        if (!G.Lang.isEmpty(msg)) {
            nsDialog.jAlert(msg);
            return;
        }


        $("#div_brand").css({
            'display': 'none'
        });
        $("#div_works").css({
            'display': 'none'
        });
        if (debug) {
            if (!confirm("[finish] last:" + idPrefixLastModified)) {
                return false;
            }
        }
        reMoveAttrs();
        repairOrderFinish();
    });
    //TODO 结算按钮功能
    $("#accountBtn").click(function() {
        $("#btnType").val('accountBtn');
        if (!isEmptyItem() && !checkStorehouseSelected()) {
            return;
        }
        if ($.trim($("#endDateStr").val()) == '') {
            nsDialog.jConfirm("友情提示：出厂时间未填写！出厂时间是否设置为当前时间","提醒",function(flag){
                if(flag){
                    var serverTime = App.Net.syncGet({url:"active.do?method=returntime", dataType:"json"});
                    $("#endDateStr").val(G.getCurrentFormatDateMin(serverTime));
                     $("#confirm_account_date").val(dateUtil.getCurrentTime());
                    doAccount();
                }else{
                    $("#endDateStr").focus();
                    return;
                }
            });
        }else{
            var newValue=$("#endDateStr").val().split(" ")[0];
            if (GLOBAL.Util.getDate(newValue).getTime() - GLOBAL.Util.getDate(GLOBAL.Date.getCurrentFormatDate()).getTime() > 0) {
                $("<div><span>友情提示：出厂日期为本日之后的日期，结算后系统会自动把结算日期记为今天！</span></div>").dialog({
                    height: 150,
                    width: 350,
                    buttons:{
                        "确定":function(){
                            $(this).dialog( "close" );
                            $("#confirm_account_date").val(dateUtil.getCurrentTime());
                               doAccount();
                        },
                        "取消":function(){
                             $(this).dialog( "close" );
                            return;
                        }
                    }
                });
            }else{
                if (G.getCurrentFormatDate() != newValue) {
                    $("#confirm_account_date_span").text(newValue.split(" ")[0]);
                    $("#confirm_current_date_span").text(GLOBAL.Date.getCurrentFormatDate());
                    $("#dialog-confirm-account-date").dialog({
                        resizable: true,
                        title: "提醒",
                        height: 180,
                        width: 420,
                        modal: true,
                        closeOnEscape: false
                    });
                }else{
                   doAccount();
                }
            }
        }


    });


    $(".serviceTotal").live('change blur', function() {
        var count = getServiceTotal();
        if (" " == count || null == count) {
            count = 0;
        }
        count = dataTransition.rounding(count, 2);
        $("#totalSpan").text(count);
        $("#total").val(count);
        //如果还没结算过，实收=总计
        if ($("#settledAmountHid").val() <= 0) {
            $("#settledAmount").val(count);
        }
        $("#debt").val($("#total").val() - $("#settledAmount").val());
    });
    $(".itemPrice,.itemAmount").live('change', function() {
        setTotal(); //todo 冗余代码 zhangjuntao
    });

    //TODO 单价失去焦点时，设置总计，并比对当前销售价是否低于采购价
    $(".itemPrice").live('change', function () {
        setTotal();
        var idPrefix = $(this).attr("id").split(".")[0];
        var inventoryAveragePrice = $("#" + idPrefix + "\\.inventoryAveragePrice").val();
        if (Number(inventoryAveragePrice) > $(this).val()) {
            priceChangeTimeout = setTimeout(function() {
                nsDialog.jAlert("友情提示：该商品的销售价低于成本均价" + dataTransition.rounding(inventoryAveragePrice, 2) + "元，请核查是否输入正确！");
            }, 100);
        } else if (Number(inventoryAveragePrice) == $(this).val()) {
            priceChangeTimeout = setTimeout(function() {
                nsDialog.jAlert("友情提示：该商品的销售价等于成本均价" + dataTransition.rounding(inventoryAveragePrice, 2) + "元，请核查是否输入正确！");
            }, 100);
        }
        activeRecommendSupplierTip($(this).parent().parent(), getOrderType());
    });

    //TODO 当删除某一行时，实时计算总计和欠款
    $(".opera1").live('click', function() {
        if (this.disabled) return;
        setTotal();
    });

    //TODO 变更数量时，实时计算总计、欠款、缺料等
    $(".itemAmount").live('blur', function() {
        //计算库存量
        isLack();
        setTotal();
        activeRecommendSupplierTip($(this).parent().parent(), getOrderType());
    });

    $("#settledAmount").bind('blur keyup', function() {
        $(this).val(App.StringFilter.inputtingPriceFilter($(this).val(), 2));
        message = calculate.subtraction("#total", "#settledAmount", "#debt", "#input_makeTime", message);
    });

    $("#debt").keyup(
        function(event) {
            event.target.value = App.StringFilter.inputtingPriceFilter(event.target.value, 2);
            var total = Number($("#total").val()); //总额
            var settledAmount = Number($("#settledAmount").val()); //实收
            var debt = Number($("#debt").val()); //欠款
            if (debt + settledAmount > total) {
                $("#debt").val(total);
                $("#settledAmount").val(0);
                message = "请输入合适的欠款金额。";
                showMessage.fadeMessage("35%", "40%", "slow", 2000, message);
                $("#input_makeTime_sale").show();
            }
        }).blur(function(event) {
            if ("" == $("#debt").val()) {
                $("#debt").val(0);
            } else {
                event.target.value = App.StringFilter.priceFilter(event.target.value, 2);
            }
        });

    //历史查询
    $(".user_name a").click(function() {
        $(this).addClass("hover");
        $(".user_name a").not($(this)).removeClass("hover");
        $("#searchType").val($(this).attr("id"));
    });

    //会员卡洗车
    $("#sureWashBtn").click(function() {
        if ($("#remainWashTimes").text() == 0) {
            alert("洗车卡剩余次数为0，不能洗车！");
            return;
        }

        if ($("#todayWashTimes").val() >= 1) {
            if (!confirm("是否再次洗车？")) {
                return;
            }
        }
        //进厂里程为空，提示信息
        if (!$.trim($("#startMileage").val())) {
            // 洗车时去掉验证，将里程数改为0
            document.getElementById("startMileage").value = "0.0";

        }

        $("#repairOrderForm").attr('action', 'wash.do?method=washCar&washType=member&task=wash&mobileStr=' + $("#mobile").val() + '&washWorkerStr=' + $("#washWorker").val() + '&customerStr=' + $("#customerName").val());
        if ($("#customerName").val() == "") {
            $("#customerName").val($("#licenceNo").val());
        }
        $("#repairOrderForm").submit();
    });

    //非会员洗车或者会员付款洗车
    $("#normalWashBtn").click(function() {
        var reg = /^(([1-9]+[0-9]*.{1}[0-9]+)|([0].{1}[1-9]+[0-9]*)|([1-9][0-9]*)|([0][.][0-9]+[1-9]*))$/; //正数
        if ($("#normalCash").val() != '' && $("#normalCash").val() != '0' && !reg.test($.trim($("#normalCash").val()))) {
            alert("请输入正确的金额！");
            return;
        }
        if (!$("#normalCash").val() || $("#normalCash").val() == '0') {
            var b = confirm("是否赠送一次洗车？");
            if (b) {
                $("#normalCash").val(0);
            } else {
                return;
            }
        }
        //进厂里程为空，提示信息
        if (!$.trim($("#startMileage").val())) {
            // modify by  miao.liu
            // 洗车时去掉验证，将里程数改为0
            document.getElementById("startMileage").value = "0.0";
        }
        if ($("#todayWashTimes").val() >= 1) {
            if (!confirm("是否再次洗车？")) {
                return;
            }
        }
        $("#repairOrderForm").attr('action', 'wash.do?method=washCar&washType=normal&task=wash&normalCash=' + $("#normalCash").val() + '&washWorkerStr=' + $("#washWorker").val() + '&mobileStr=' + $("#mobile").val() + '&customerStr=' + $("#customerName").val());
        //单存洗车就把用户名和手机号更新为默认的
        if (!$("#customerName").val()) {
            $("#customerName").val($("#licenceNo").val());
        }
        $("#repairOrderForm").submit();
    });

    //办洗车卡或充值
    $("#chargeBtn").click(function() {
        //  验证
        var reg = /^(([1-9]+[0-9]*.{1}[0-9]+)|([0].{1}[1-9]+[0-9]*)|([1-9][0-9]*)|([0][.][0-9]+[1-9]*))$/; //正数
        var reg2 = /^[0-9]*[1-9][0-9]*$/; //正整数
        if ($("#chargeCash").val() && $("#chargeCash").val() != "0" && !reg.test($.trim($("#chargeCash").val()))) {
            alert("请输入正确的金额！");
            return;
        }
        if (!reg2.test($.trim($("#chargeTimes").val()))) {
            alert("请输入正确的数字！");
            return;
        }

        if (!$.trim($("#chargeTimes").val()) || $.trim($("#chargeTimes").val()) == 0) {
            return;
        }

        //充值金额可为0,次数不为0,弹出框"是否赠送洗车nn次？"，确定和取消。
        if ((!$.trim($("#chargeCash").val()) || $.trim($("#chargeCash").val()) == 0) && $.trim($("#chargeTimes").val()) != 0) {
            if (!confirm("是否赠送洗车" + $.trim($("#chargeTimes").val()) + "次？")) {
                return;
            }
        }
        //充值金额有数值的情况下，次数不能为0次,弹出框"充值xx元，请填写购买洗车次数。"任意点击消失
        if ($.trim($("#chargeCash").val()) > 0 && $.trim($("#chargeTimes").val()) == 0) {
            alert("充值" + $.trim($("#chargeCash").val()) + "元，请填写购买洗车次数");
            return;
        }

        if (!$.trim($("#chargeTimes").val())) $("#chargeTimes").val(0);
        if (!$.trim($("#chargeCash").val())) $("#chargeCash").val(0);
        //充值确定后，再次弹出框"充值xx元，购买洗车nn次。"，确定或取消。
        if (!confirm("确认是否充值？")) {
            return;
        }
        $("#repairOrderForm").attr('action', 'wash.do?method=saveOrUpdateWashCard&task=wash&mobileStr=' + $("#mobile").val() + '&washWorkerStr=' + $("#washWorker").val() + '&customerStr=' + $("#customerName").val());
        //单存洗车就把用户名和手机号更新为默认的
        if (!$("#customerName").val()) {
            $("#customerName").val($("#licenceNo").val());
        }
        //如果里程数为空，设置为默认0
        if (!document.getElementById("startMileage").value) document.getElementById("startMileage").value = "0.0";
        $("#repairOrderForm").submit();
    });

    if ($("#carWash").hasClass("title_hover")) {
        initInvoiceJSP($("#carWash"), true);
    }

    $('#secondaryBtn').click(function(){
        nsDialog.jConfirm("生成的结算附表是本施工单的另一套账目，结算后只做查询用途，不计入财务账目！是否确认生成结算附表？", "友情提示", function (val) {
            val && window.open('repairOrderSecondary.do?method=createRepairOrderSecondary&repairOrderId='+$('#id').val());
        });
    });

    $('#secondary').click(function () {
        window.open('repairOrderSecondary.do?method=showRepairOrderSecondary&repairOrderSecondaryId=' + $('#secondary').attr('data-value'));
    });

    //去打印页面
    $("#printBtn").click(function() {
        if (!$("#id").val() && !$("#draftOrderIdStr").val()) {
            return;
        }
        APP_BCGOGO.Net.syncGet({
            url:"print.do?method=getTemplates",
            data:{
                "orderType":"REPAIR",
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
                            repairPrint($("input:radio[name='selectTemplate']:checked").val());
                        }
                    });
                }else{
                    repairPrint();
                }
            }
        });
    });

    if (repairOrderId && $("#isPrint").val() == "true") {
        $("#printBtn").click();
    }

    //弹出施工人下拉框
    $("input[name$='.workers']")._dropdownlist("worker");

    $("#productSaler")._dropdownlist("worker");


    //2012-1-29 车辆维修单，如果是老车牌的情况下，
    // 不能修改车型信息和客户名信息，避免出现一个车牌多种车型的情况
    if ($.trim($("#vechicleId").val())) {
        var orderAttrs = ["brand", "model","vehicleColor","vehicleChassisNo","vehicleEngineNo", "year", "contact", "engine", "customerName", "mobile", "landLine", "fuelNumber","vehicleContact","vehicleMobile"];
        disableOrderElement(orderAttrs);
    }
    if (!$("#status").val()) {
        $("#orderStatusImag").hide();
        $(".invalidImg").hide();
        $("#copyInput_div").hide();
    }
    if ($("#status").val() == 'REPAIR_DISPATCH') {
        $(".invalidImg").show();
        $("#copyInput_div").hide();
        $("#orderStatusImag").removeClass("zuofei").addClass("shigong").show();
    }
    if ($("#status").val() == 'REPAIR_DONE' && $("#serviceType").val() == "REPAIR") {
        $(".invalidImg").show();
        $("#copyInput_div").hide();
        $("#orderStatusImag").removeClass("zuofei").addClass("wangong").show();
    } else if ($("#status").val() == 'REPAIR_SETTLED' && $("#serviceType").val() == "REPAIR") {
        $(".invalidImg").show();
        $("#copyInput_div").show();

        if ($("#statementAccountOrderId").val()) {
            $("#orderStatusImag").removeClass("zuofei").addClass("statement_accounted").show();
        } else {
            if ($("#orderDebt").attr("debtVal") * 1 > 0) {
                $("#orderStatusImag").removeClass("zuofei").addClass("repairDebtSettleImg").show();
            } else {
                $("#orderStatusImag").removeClass("zuofei").addClass("jie_suan_repair").show();
            }
        }

    } else if ($("#status").val() == 'REPAIR_REPEAL' && $("#serviceType").val() == "REPAIR") {
        $("#orderStatusImag").removeClass("zuofei").addClass("zuofei_repair").show();
        $(".invalidImg").hide();
        $("#copyInput_div").show();
    } else if ($("#status").val() == '' || $("#serviceType").val() == "WASH") {
        $("#orderStatusImag").hide();
        $(".invalidImg").hide();
        $("#copyInput_div").hide();
    }

    $("#nullifyBtn").live('click', function() {
        if ($(this).attr("status") == "checkDeleteProduct") {
            return;
        }
        repealOrder($("#id").val());
    });
    $("#copyInput_div").bind('click', function() {
        if (G.Lang.isEmpty($("#id").val())) {
            nsDialog.jAlert("单据ID不存在，请刷新后重试");
            return false;
        }
        App.Net.syncPost({
            url:"txn.do?method=validateCopyRepairOrder",
            dataType:"json",
            data:{"repairOrderId" : $("#id").val()},
            success:function(result) {
                if (result.success) {
                    window.location.href = "txn.do?method=getCopyRepairOrder&repairOrderId=" + $("#id").val();
                } else {
                    if (result.operation == 'ALERT') {
                        nsDialog.jAlert(result.msg, result.title);
                    } else if (result.operation == 'CONFIRM') {
                        nsDialog.jConfirm(result.msg, result.title, function(resultVal) {
                            if (resultVal) {
                                window.location.href = "txn.do?method=getCopyRepairOrder&repairOrderId=" + $("#id").val();
                            }
                        });
                    }
                }
            },
            error:function() {
                nsDialog.jAlert("验证时产生异常，请重试！");
            }
        });
    });

    $("#createSalesReturn").bind('click', function() {
        window.open("salesReturn.do?method=createSalesReturn&orderId=" + $("#id").val() + "&orderType=repair");
    });
    $("#saveDraftBtn").bind('click', function(event) {
        if (isLegalTxnDataLength() && openNewOrderPage()) {
            var orderAttrs = ["vehicleContact","vehicleMobile","brand", "model", "vehicleChassisNo", "vehicleEngineNo", "vehicleColor", "year", "contact", "engine", "customerName", "mobile", "landLine", "fuelNumber","storehouseId"];
            var orderAttrsDisableStatus = [];
            for (var i = 0, len = orderAttrs.length; i < len; i++) {
                orderAttrsDisableStatus.push($("#" + orderAttrs[i]).attr("disabled"));
            }
            enableOrderElement(orderAttrs); //TODO 移除相关元素的disabled状态
            $("#saveDraftBtn").attr("disabled", true);
            $("#repairOrderForm").ajaxSubmit({
                url: "draft.do?method=saveRepairOrderDraft",
                dataType: "json",
                type: "POST",
                success: function(data) {
                    if (data && data.result && !data.result.success) {
                        nsDialog.jAlert(data.result.msg);
                    } else {
                        $("#draftOrderIdStr").val(data.id);
                        showMessage.fadeMessage("45%", "34%", "slow", 300, "草稿保存成功！ " + getCurrentTime());
                        if (!G.isEmpty(data)) {
                            $("#receiptNoStr").html('<span><strong>单据号：<strong class="red_color">'+data.receiptNo+'</strong></strong></span>');
                            $("#receiptNo").val(data.receiptNo);
                            $("#print_div").show();
                        }
                    }

                    $("#saveDraftBtn").attr("disabled", false);
                },
                error: function() {
                    showMessage.fadeMessage("45%", "34%", "slow", 300, "保存草稿异常！ " + getCurrentTime());
                    $("#saveDraftBtn").attr("disabled", false);
                }
            });
            for (var i = 0, len = orderAttrs.length; i < len; i++) {
                $("#" + orderAttrs[i]).attr("disabled", orderAttrsDisableStatus[i]);
            }
        }
    });

    //点击模板名称 ，加载模板具体内容
    $(".J_TemplateName").live("click", function(e) {
        var _$me =$(this);

        var templateName =  $(this).closest("li").attr("data-template-name");
        if (G.isEmpty(templateName)) return;
        APP_BCGOGO.Net.asyncGet({
            url: "txn.do?method=getRepairOrderTemplateByTemplateName",
            data: {
                isSimple:false,
                repairOrderTemplateName: templateName,
                storehouseId: !G.isEmpty($("#storehouseId").val()) && G.isNumber($("#storehouseId").val()) ? $("#storehouseId").val() : ""
            },
            dataType: "json",
            success: function(result) {
                if(G.isNotEmpty(result.data)){
                    var repairOrderTemplateDTO = result.data;
                    $("#table_task tr").detach(".item");
                    $("#table_productNo_2 tr").detach(".item1");
                    $("#table_otherIncome tr").detach(".item2");

                    var templateServiceDTOs = repairOrderTemplateDTO.repairOrderTemplateServiceDTOs;
                    if(G.isNotEmpty(templateServiceDTOs)){
                        var serviceDTOs = [];
                        $.each(templateServiceDTOs,function(index,templateServiceDTO){
                            serviceDTOs.push(templateServiceDTO.repairOrderServiceDTO);
                        });
                        initServiceDTOs(serviceDTOs);
                    }else{
                        taskaddNewRow();
                        isShowAddButton();
                    }

                    var templateItemDTOs = repairOrderTemplateDTO.repairOrderTemplateItemDTOs;
                    if(G.isNotEmpty(templateItemDTOs)){
                        var itemDTOs = [];
                        $.each(templateItemDTOs,function(index,templateItemDTO){
                            itemDTOs.push(templateItemDTO.repairOrderItemDTO);
                        });
                        initItemDTOs(itemDTOs);
                        trCount1 = $("#table_productNo_2 tr").size() - 2;
                    }else{
                        trCount1 = 0;
                        addNewRow(0);
                        isShowAddButton2();
                    }

                    if(G.isNotEmpty(repairOrderTemplateDTO.repairOrderDTO) && G.isNotEmpty(repairOrderTemplateDTO.repairOrderDTO.otherIncomeItemDTOList)){
                        initOtherIncomeItems(repairOrderTemplateDTO.repairOrderDTO.otherIncomeItemDTOList);
                        trCount3 = $("#table_otherIncome tr").size() - 1;
                    }else{
                        trCount3 = 0;
                        otherIncomeAddNewRow();
                        isShowAddButton3();
                    }

                    $("#currentRepairOrderTemplateName").val(templateName);
                    $("#currentRepairOrderTemplateId").val(repairOrderTemplateDTO.idStr);

                    $("#saveA").html("派单");
                    $("#saveBtn").removeClass("changeSingle").addClass("sendSingle");
                    setTotal();
                    _$me.closest("ul").find("li").each(function () {
                        if ($(this).hasClass("construction_template_li2")) {
                            $(this).removeClass("construction_template_li2").addClass("construction_template_li1");
                        }
                    });
                    _$me.closest("li").removeClass("construction_template_li1").addClass("construction_template_li2");
                }else{
                    showMessage.fadeMessage("45%", "34%", "slow", 3000, "施工模板内容加载失败！ ");
                    return;
                }

            },
            error: function() {
                showMessage.fadeMessage("45%", "34%", "slow", 3000, "施工模板内容加载失败！ ");
                return;
            }
        });
    });

    function saveRepairOrderTemplate(isNewRepairOrderTemplate) {
        $("#repairOrderTemplateName").val($("#repairOrderTemplateNameInput").val());
        $(".serviceCategory").removeAttr("disabled");
        $("#repairOrderForm").ajaxSubmit({
            url: "txn.do?method=saveRepairOrderTemplate",
            dataType: "json",
            type: "POST",
            success: function(data) {
                $("#repairOrderTemplateName").val("");
                $("#currentRepairOrderTemplateId").val(data.idStr);
                $("#currentRepairOrderTemplateName").val(data.templateName);
                setTemplateServiceIdStr(data);
                setTemplateItemIdStr(data);
                if (isNewRepairOrderTemplate) {
                    $("#templateContainer ul li").each(function () {
                        if ($(this).hasClass("construction_template_li2")) {
                            $(this).removeClass("construction_template_li2").addClass("construction_template_li1");
                        }
                    });
                    var liHtml = '<li class="construction_template_li2" data-template-name="'+data.templateName+'" data-template-id="'+data.idStr+'">';
                    liHtml += '<div class="shut_down_02 J_DeleteTemplate" style="display: none;"></div>';
                    liHtml += '<div class="template_name J_TemplateName" title="' + data.templateName + '">'+data.templateName+'</div>';
                    liHtml += '<input class="txt J_RenameTemplateInput" maxlength="20" type="text" style="display: none;width: 60px" autocomplete="off"/>';
                    liHtml += '</li>';
                    $("#templateContainer ul").append(liHtml);
                }
                showMessage.fadeMessage("45%", "34%", "slow", 3000, "模板保存成功！ " + getCurrentTime());

            },
            error: function() {
                showMessage.fadeMessage("45%", "34%", "slow", 3000, "模板保存异常！ " + getCurrentTime());

            }
        });
        $(".serviceCategory").each(function() {
            if ($(this).val() == "洗车" || $(this).val() == "美容") {
                $(this).attr("disabled", "disabled");
            }
        });

    }

    function saveRepairOrderTemplateWithExistingSameNameRepairOrderTemplate() {
        $("#repairOrderTemplateName").val($("#repairOrderTemplateNameInput").val());
        $(".serviceCategory").removeAttr("disabled");
        $("#repairOrderForm").ajaxSubmit({
            url: "txn.do?method=saveRepairOrderTemplateWithExistingSameNameRepairOrderTemplate",
            dataType: "json",
            type: "POST",
            success: function(data) {
                $("#currentRepairOrderTemplateName").val(data.templateName);
                setTemplateServiceIdStr(data);
                setTemplateItemIdStr(data);

                $("#templateContainer ul li").each(function () {
                    if ($(this).hasClass("construction_template_li2")) {
                        $(this).removeClass("construction_template_li2").addClass("construction_template_li1");
                    }
                });

                $("#templateContainer ul li").each(function() {
                    if ($(this).attr("data-template-name") == data.templateName) {
                        $(this).attr("data-template-id", data.idStr);
                        $(this).removeClass("construction_template_li1").addClass("construction_template_li2");
                    }
                });
                showMessage.fadeMessage("45%", "34%", "slow", 3000, "模板保存成功！ " + getCurrentTime());
            },
            error: function() {
                showMessage.fadeMessage("45%", "34%", "slow", 3000, "模板保存异常！ " + getCurrentTime());
            }
        });

        $(".serviceCategory").each(function() {
            if ($(this).val() == "洗车" || $(this).val() == "美容") {
                $(this).attr("disabled", "disabled");
            }
        });

    }


    function renameRepairOrderTemplate(repairOrderTemplateId,newRepairOrderTemplateName,isOverrideSameNameRepairOrderTemplate) {
        APP_BCGOGO.Net.syncPost({
            url: "txn.do?method=renameRepairOrderTemplate",
            data: {
                repairOrderTemplateId: repairOrderTemplateId,
                newRepairOrderTemplateName: newRepairOrderTemplateName
            },
            dataType: "json",
            success: function(json) {
                if (isOverrideSameNameRepairOrderTemplate) {
                    $("#templateContainer ul li").each(function () {
                        if (newRepairOrderTemplateName == $(this).attr("data-template-name")) {
                            $(this).remove();
                        }
                    });
                }
                $("#templateContainer ul li").each(function () {
                    if (repairOrderTemplateId == $(this).attr("data-template-id")) {
                        $(this).attr("data-template-name",newRepairOrderTemplateName);
                        $(this).find(".J_TemplateName").attr("title",newRepairOrderTemplateName);
                        $(this).find(".J_TemplateName").text(newRepairOrderTemplateName);
                    }
                });
                showMessage.fadeMessage("45%", "34%", "slow", 3000, "施工模板重命名成功！ ");
                return;
            },
            error: function() {
                showMessage.fadeMessage("45%", "34%", "slow", 3000, "施工模板重命名失败！ ");
                return;
            }
        });
    }

    $("#templateContainer ul li").live('dblclick', function(event) {
        $(this).find("input").val($(this).closest("li").attr("data-template-name"));
        $(this).find("input").show();
        $(this).find("input").select();
        $(this).find("div").hide();
    });
    $(".J_RenameTemplateInput").live('blur', function(event) {
        var repairOrderTemplateName = $(this).closest("li").attr("data-template-name");
        var repairOrderTemplateId = $(this).closest("li").attr("data-template-id");
        var newRepairOrderTemplateName = $(this).val();
        if (G.isEmpty(newRepairOrderTemplateName)) {
            nsDialog.jAlert("模板名称不能为空！");
        }else if (newRepairOrderTemplateName!= repairOrderTemplateName) {
            APP_BCGOGO.Net.syncGet({
                url: "txn.do?method=getRepairOrderTemplateByTemplateName",
                data: {
                    isSimple:true,
                    repairOrderTemplateName: newRepairOrderTemplateName,
                    storehouseId: !G.isEmpty($("#storehouseId").val()) && G.isNumber($("#storehouseId").val()) ? $("#storehouseId").val() : ""
                },
                dataType: "json",
                success: function (result) {
                    if(G.isNotEmpty(result.data)){
                        nsDialog.jConfirm("有同名施工模板，是否覆盖？", "", function(resultVal) {
                            if (resultVal) {
                                renameRepairOrderTemplate(repairOrderTemplateId,newRepairOrderTemplateName,true);
                            }
                        });
                    }else{
                        renameRepairOrderTemplate(repairOrderTemplateId,newRepairOrderTemplateName,false);
                    }

                }
            });
        }
        $(this).hide();
        $(this).closest("li").find(".J_TemplateName").show();
    });

    $("#saveTemplateBtn").bind('click', function(event) {
        if (isEmptyItem() && isEmptyService() && isEmptyOtherIncome()) {
            nsDialog.jAlert("施工单,材料单和其它费用均未填写");
            return;
        }
        if (isMaterialSame()) {
            nsDialog.jAlert('材料单有重复项目或者空行，请修改或删除。');
            return false;
        }
        if (isRepairSame()) {
            nsDialog.jAlert('施工单有重复项目或者空行，请修改或删除。');
            return false;
        }
        if (isCommodityCodeSame("item1")) {
            nsDialog.jAlert('材料单商品编码有重复内容，请修改或删除。');
            return false;
        }
        setTotal();
        $("#repairOrderTemplateNameInput").val($("#currentRepairOrderTemplateName").val());
        $("#templateNameInputDiv").dialog({
            height: 160,
            width: 250,
            modal: true,
            buttons: {
                "确定": function() {
                    var repairOrderTemplateName = $("#repairOrderTemplateNameInput").val();
                    if (G.isEmpty(repairOrderTemplateName)) {
                        nsDialog.jAlert("施工单模板名称不能为空！");
                        return;
                    }
                    APP_BCGOGO.Net.asyncGet({
                        url: "txn.do?method=getRepairOrderTemplateByTemplateName",
                        data: {
                            isSimple:true,
                            repairOrderTemplateName: repairOrderTemplateName,
                            storehouseId: !G.isEmpty($("#storehouseId").val()) && G.isNumber($("#storehouseId").val()) ? $("#storehouseId").val() : ""
                        },
                        dataType: "json",
                        success: function(result) {
                            if(G.isNotEmpty(result.data)){
                                if (repairOrderTemplateName == $("#currentRepairOrderTemplateName").val()) {
                                    saveRepairOrderTemplate(false);
                                } else {
                                    nsDialog.jConfirm("有同名施工模板，是否覆盖？", "", function(resultVal) {
                                        if (resultVal) {
                                            saveRepairOrderTemplateWithExistingSameNameRepairOrderTemplate();
                                        }
                                    });
                                }
                            }else{
                                saveRepairOrderTemplate(true);
                            }

                        }
                    });
                    $(this).dialog("close");
                },
                "取消": function() {
                    $(this).dialog("close");
                }
            }

        });
    });

});

function repairPrint(templateId){
//    if(G.isEmpty(repairOrderId)){
//        nsDialog.jAlert("数据异常，不能打印。");
//        return;
//    }

    if($("#id").val()) {
        var settledAmount = $("#settledAmount").val();
        var debt = $("#debt").val();
        window.showModalDialog("txn.do?method=getRepairOrderToPrint&repairOrderId=" + repairOrderId + "&templateId=" + templateId + "&settledAmount=" + settledAmount + "&debt=" + debt + "&now=" + new Date(), '维修美容单', "dialogWidth=1024px;dialogHeight=768px,status=no;help=no");
        return;
    } else if($("#draftOrderIdStr").val()) {
        window.showModalDialog("draft.do?method=getDraftOrderToPrint&id=" + $("#draftOrderIdStr").val() + "&templateId=" + templateId + "&type=REPAIR&now=" + new Date());
        return;
    }

    var settledAmount = $("#settledAmount").val();
    var debt = $("#debt").val();
    window.showModalDialog("txn.do?method=getRepairOrderToPrint&repairOrderId=" + repairOrderId + "&templateId=" + templateId + "&settledAmount=" + settledAmount + "&debt=" + debt + "&now=" + new Date(), '维修美容单', "dialogWidth=1024px;dialogHeight=768px,status=no;help=no");
}

$(".J_DeleteTemplate").live('click', function(event) {
    var _$me = $(this);
    var repairOrderTemplateId = $(this).closest("li").attr("data-template-id");
    var repairOrderTemplateName = $(this).closest("li").attr("data-template-name");
    if (G.isEmpty(repairOrderTemplateId)) {
        nsDialog.jAlert("数据错误,请刷新页面！");
        return;
    }
    var confirmContent="确认删除施工模板：" + repairOrderTemplateName + " ？";
    if(repairOrderTemplateId==$("#currentRepairOrderTemplateId").val()){
        confirmContent="当前施工单正在使用此模板，确认删除施工模板：" + repairOrderTemplateName + " ？";
    }
    nsDialog.jConfirm(confirmContent, "删除模板", function(resultVal) {
        if (resultVal) {
            APP_BCGOGO.Net.syncPost({
                url: "txn.do?method=deleteRepairOrderTemplate",
                data: {
                    repairOrderTemplateId: repairOrderTemplateId
                },
                dataType: "json",
                success: function(json) {
                    _$me.closest("li").remove();
                    if(repairOrderTemplateId==$("#currentRepairOrderTemplateId").val()){
                        $("#table_task tr").remove(".item");
                        $("#table_productNo_2 tr").remove(".item1");
                        $("#table_otherIncome tr").remove(".item2")
                        taskaddNewRow();
                        isShowAddButton();
                        trCount1 = 0;
                        addNewRow(0);
                        isShowAddButton2();

                        trCount3 = 0;
                        otherIncomeAddNewRow(0);
                        isShowAddButton3();
                        $("#currentRepairOrderTemplateId").val("");
                        $("#currentRepairOrderTemplateName").val("");
                    }
                    showMessage.fadeMessage("45%", "34%", "slow", 3000, "施工模板删除成功！ ");
                },
                error: function() {
                    showMessage.fadeMessage("45%", "34%", "slow", 3000, "施工模板删除失败！ ");
                    return;
                }
            });
        }
    });
});

$(".otherIncomePrice").live("keyup", function() {
    $(this).val(App.StringFilter.inputtingPriceFilter($(this).val(), 2));
    setTotal();
});

function setTemplateServiceIdStr(json) {
    var templateServiceDTOs = json.repairOrderTemplateServiceDTOs;
    if (templateServiceDTOs.length < 1) return;

    $(".item").each(function(i) {
        var idPrefix = $(this).children().eq(1).children(1).eq(0).attr("id").split(".")[0];
        var service = $.trim($("#" + idPrefix + "\\.service").val());
        for (j = 0; j < templateServiceDTOs.length; j++) {
            if (templateServiceDTOs[j].serviceName == service) {
                $("#" + idPrefix + "\\.templateServiceIdStr").val(templateServiceDTOs[j].idStr);
            }
        }

    });

}


function setTemplateItemIdStr(json) {
    var templateItemDTOs = json.repairOrderTemplateItemDTOs;
    if (templateItemDTOs.length < 1) return;

    $(".item1").each(function(i) {
        var idPrefix = $(this).children().eq(0).children(1).eq(0).attr("id").split(".")[0];
        var productName = $.trim($("#" + idPrefix + "\\.productName").val());
        var brand = $.trim($("#" + idPrefix + "\\.brand").val());
        var spec = $.trim($("#" + idPrefix + "\\.spec").val());
        var model = $.trim($("#" + idPrefix + "\\.model").val());
        for (j = 0; j < templateItemDTOs.length; j++) {
            if (templateItemDTOs[j].repairOrderItemDTO.productName == productName && templateItemDTOs[j].repairOrderItemDTO.brand == brand && templateItemDTOs[j].repairOrderItemDTO.spec == spec && templateItemDTOs[j].repairOrderItemDTO.model == model) {
                $("#" + idPrefix + "\\.templateItemIdStr").val(templateItemDTOs[j].idStr);
                if (templateItemDTOs[j].repairOrderItemDTO && templateItemDTOs[j].repairOrderItemDTO.productIdStr
                    && !$("#" + idPrefix + "\\.productId").val()) {
                    $("#" + idPrefix + "\\.productId").val(templateItemDTOs[j].repairOrderItemDTO.productIdStr);
                }
            }
        }

    });
}


function initAppointService(jsonStr) {
    if (!jsonUtil.isEmpty(jsonStr) && !jsonUtil.isEmpty(jsonStr[0])) {
        var appointServices = jsonStr[0].appointServiceDTOs;
        var table = $("#handover_info_table")[0];
        while(table && table.rows.length > 2){
            table.deleteRow(2);
        }
        for (var i = 0; i < appointServices.length; i++) {
            var appointService = appointServices[i];
            var appointServiceId = appointService.idStr;
            var appointName = appointService.appointName;
            var appointDate = appointService.appointDate;
            var appointTr = '<tr style="height: 25px;" lineNum="' + i + '"><td colspan="3">';
            appointTr += '<input type="hidden" name="appointServiceDTOs[' + i + '].idStr" value="' + appointServiceId + '" />';
            appointTr += '<input type="text" class="checkStringEmpty txt appointName" name="appointServiceDTOs[' + i + '].appointName" value="' + appointName + '" />';
            appointTr += '<input type="text" class="checkStringEmpty txt appointDate" name="appointServiceDTOs[' + i + '].appointDate" readonly="true" value="' + appointDate + '" style="margin-left:16px;"/></td>';
            appointTr += '<td><input class="opera1 delAppointBtn" isDef="false" type="button"></td></tr>';
            $("#handover_info_table").append(appointTr);
        }
        addAppointService();
    }
}
//车牌号修改后，刷新维修单

function refreshRepairOrder() {
    $("#insuranceOrderId").val('');
    $("#vechicleId").val('');
    $("#contact").val('');
    $("#address").val('');
    $("#engine").val('');
    $("#vehicleEngineNo").val('');
    $("#vehicleColor").val('');
    $("#vehicleBuyDate").val('');
    $("#vehicleChassisNo").val('');
    $("#hiddenStorehouseId").val('');
    $("#appointOrderId").val('');

    $("#showAppointOrderSpan").empty();
    $("#customerMemberInfoImg").hide();
    $("#gouka").text("购卡");
    reMoveAttrs();
    $("#fuelNumber").removeAttrs("disabled");
    $("#saveDraftOrder_div").show();

    if (G.isEmpty($("#customerId").val()) || vechicleAndCustomerStatus != 1) {
        $(".J_RepairOrderClearDiv :input").not($("#startDateStr,#startDate,#licenceNo,.appointName")).val('');
        $("#memberType,#memberStatus,#memberNumber,#memberServiceDeadLineStr,#memberJoinDateStr").html("");
        $("#customerConsume,#memberRemainAmount,#memberServiceCount").html("0");
        $("#customerName")[0].title = "";
        $("#receivable").html("0");
        $("#payable").html("0");
        initDuiZhanInfo();
        $("#memo").val("");
    }
    $("#span_year,#span_engine,#orderNum").empty();
    $(".stock_bottom").hide();
    $("#lastWashTime,#remainWashTimes").text('');
    $("#washRemain,#todayWashTimes").val('');

    $("#span_year,#span_engine,#orderNum").empty();
    $(".stock_bottom").hide();
    $("#lastWashTime,#remainWashTimes").text('');
    $("#washRemain,#todayWashTimes").val('');
    repairOrderId = '';
    customerId = '';
    customer = '';
    vehicleId = '';
    licenceNo = '';
}

function isEmptyService() {
    //    var attrs = ["service", "workers", "total"];
    var attrs = ["service"];

    function getObjectValue(name) {
        return $("#table_task tr:last").find("input[name$='." + name + "']").val();
    }

    if ($("#table_task tr").size() != 3 || getObjectValue(attrs[0])) {
        return false;
    }
    return true;
}

function isEmptyOtherIncome() {
    //    var attrs = ["service", "workers", "total"];
    var attrs = ["name"];

    function getObjectValue(name) {
        return $("#table_otherIncome tr:last").find("input[name$='." + name + "']").val();
    }

    if ($("#table_otherIncome tr").size() != 2 || getObjectValue(attrs[0])) {
        return false;
    }
    return true;
}

//TODO 判断某一行的产品是否为空

function isEmptyItem() {
    var isEmpity = true;
    $("#table_productNo_2 .item1").each(function() {
        if (!checkEmptyRepairMaterialRow($(this))) {
            isEmpity = false;
            return false;
        }
    });
    return isEmpity;
}

function initServiceDTOs(serviceDTOs) {
    $("#table_task tr").detach(".item");
    if (serviceDTOs && serviceDTOs.length > 0) {
        for (var i = 0, len = serviceDTOs.length; i < len; i++) {
            var idPrefix = "serviceDTOs" + i;
            var namePrefix = "serviceDTOs[" + i + "]";
            var memo = (null == serviceDTOs[i].memo || "null" == serviceDTOs[i].memo) ? " " : serviceDTOs[i].memo;
            var businessCategoryName = (null == serviceDTOs[i].businessCategoryName || "null" == serviceDTOs[i].businessCategoryName) ? "" : serviceDTOs[i].businessCategoryName;
            var businessCategoryId = (null == serviceDTOs[i].businessCategoryIdStr || "null" == serviceDTOs[i].businessCategoryIdStr) ? "" : serviceDTOs[i].businessCategoryIdStr;
            var serviceHistoryId = G.Lang.isEmpty(serviceDTOs[i].serviceHistoryIdStr) ? '' : serviceDTOs[i].serviceHistoryIdStr;
            var id =    serviceDTOs[i].idStr;
            var service =  serviceDTOs[i].service;
            var serviceId = serviceDTOs[i].serviceIdStr;
            var standardHours = G.Lang.isEmpty(serviceDTOs[i].standardHours) ? "" : serviceDTOs[i].standardHours;
            var standardUnitPrice = G.Lang.isEmpty(serviceDTOs[i].standardUnitPrice) ? "" : serviceDTOs[i].standardUnitPrice;
            var actualHours = G.Lang.isEmpty(serviceDTOs[i].actualHours) ? "" : serviceDTOs[i].actualHours;
            var total =  serviceDTOs[i].total;
            var trSample = '<tr class="bg titBody_Bg item table-row-original">'
                + '<td style="border-left: medium none;"><input id="' + idPrefix + '.id" name="' + namePrefix + '.id" value="' + id + '" type="hidden">'
                + '<input id="' + idPrefix + '.serviceId" type="hidden" value="' + serviceId + '" name="' + namePrefix + '.serviceId">';
            trSample += '<input id="' + idPrefix + '.serviceHistoryId" type="hidden" value="' + serviceHistoryId + '" name="' + namePrefix + '.serviceHistoryId">';
            trSample += '<input id="' + idPrefix + '.service" name="' + namePrefix + '.service" title="' + service + '" value="' + service + '" class="table_input" type="text" size="20">'
                + '</td>'
                + '<td style="display:none">' + '<select id="' + idPrefix + '.consumeType" name="' + namePrefix + '.consumeType" value="MONEY" style="width:120px;" onchange="consumeTypeChange(this)">';
            trSample += '<option value ="MONEY" selected="selected">金额</option></select>';

            trSample += '</td>';
            trSample += '<td>';
            if (isCanEditStandardHours()) {
                trSample += '<input id="' + idPrefix + '.standardHours" name="' + namePrefix + '.standardHours" class="table_input standardHours checkStringEmpty" style="width:70px" type="text" value="' + standardHours + '"/>';
                trSample += '</td>';
                trSample += '<td>';
                trSample += '<input id="' + idPrefix + '.standardUnitPrice" name="' + namePrefix + '.standardUnitPrice" class="table_input standardUnitPrice checkStringEmpty" style="width:70px" type="text" value="' + standardUnitPrice + '" />';
            } else {
                trSample += '<input id="' + idPrefix + '.standardHours" name="' + namePrefix + '.standardHours" readOnly="readOnly" style="border:none;background-color:transparent;width:70px;text-align: center;" type="text" value="' + standardHours + '"/>';
                trSample += '</td>';
                trSample += '<td>';
                trSample += '<input id="' + idPrefix + '.standardUnitPrice" name="' + namePrefix + '.standardUnitPrice" readOnly="readOnly" style="border:none;background-color:transparent;width:70px;text-align: center;" type="text" value="' + standardUnitPrice + '" />';

            }
            trSample += '</td>';
            trSample += '<td>';
            trSample += '<input id="' + idPrefix + '.actualHours" name="' + namePrefix + '.actualHours" class="table_input actualHours checkStringEmpty" type="text" value="' + actualHours + '" size="4"/>';
            trSample += '</td>';
            trSample    =   trSample + '<td><input id="' + idPrefix + '.total" name="' + namePrefix + '.total" title="' + total + '" value="' + total + '" class="serviceTotal table_input" type="text">'
                + '<input id="' + idPrefix + '.costPrice" name="' + namePrefix + '.costPrice" value="' + serviceDTOs[i].costPrice + '" type="hidden">'
                + '<input id="' + idPrefix + '.templateServiceIdStr" name="' + namePrefix + '.templateServiceIdStr" value="' + serviceDTOs[i].templateServiceIdStr + '" type="hidden"/>'
                + '</td>'
                + '<td><span class = "workersSpan"><input id="' + idPrefix + '.workers" name="' + namePrefix + '.workers" title="' + serviceDTOs[i].workers + '" value="' + serviceDTOs[i].workers + '" class="table_input" type="text" size="20">'
                + '<img src="images/list_close.png" class="deleteWorkers" style="width:10px;cursor:pointer;display:none;">'
                + '<input id="' + idPrefix + '.workerId" type="hidden" value="' + G.normalize(serviceDTOs[i].workerId) + '" name="' + namePrefix + '.workerId">'
                + '</span>'
                + '</td>'
                + '<td>'
                + '<input id="' + idPrefix + '.businessCategoryName" name="' + namePrefix + '.businessCategoryName" hiddenValue="' + businessCategoryName
                + '" title="' + businessCategoryName + '" value="' + businessCategoryName + '" class="table_input businessCategoryName serviceCategory" type="text" />'
                + '<input id="' + idPrefix + '.businessCategoryId" name="' + namePrefix + '.businessCategoryId" value="' + businessCategoryId + '" type="hidden" />'
                + '</td>'
                + '<td>'
                + '<input id="' + idPrefix + '.memo" name="' + namePrefix + '.memo" title="' + memo + '" value="' + memo + '" class="table_input checkStringEmpty" type="text">'
                + '</td>';
            trSample += '<td style="border-right: medium none;"><a class="opera1" id="'+idPrefix + '.deletebutton" name="'+namePrefix + '.deletebutton">删除</a></td>' + '</tr>';
            $("#table_task tbody").append(trSample);

            if (businessCategoryName == "洗车" || businessCategoryName == "美容") {
                $("#" + idPrefix + "\\.businessCategoryName").attr("disabled", "disabled");
            }
        }
    } else {
        taskaddNewRow();
    }
    isShowAddButton();
}

function initInsuranceInfo(insuranceOrderDTO) {
    var $_toInsuranceDiv = $("#toInsuranceDiv"),$_showInsuranceSpan = $("#showInsuranceSpan");
    $_toInsuranceDiv.empty();
    $_showInsuranceSpan.empty();
    if (G.isEmpty(insuranceOrderDTO)) {
        $_toInsuranceDiv.append('<input type="button" id="toInsuranceBtn" class="sureInventory" value="" style="margin: 12px 0 0;" onfocus="this.blur();"/>');
        $_toInsuranceDiv.append('<div style="width:100%; ">生成保险理赔</div>');
    }else{
        $_showInsuranceSpan.append('<span class="blue_color">|</span>');
        $_showInsuranceSpan.append('<a class="blue_color" id="toInsuranceBtn"><img src="images/icon_right.png"/>保险理赔'+insuranceOrderDTO.policyNo+'</a>');
    }
}

function initItemDTOs(itemDTOs) {
    $("#table_productNo_2 tr").detach(".item1");
    if (itemDTOs && itemDTOs.length > 0) {
        for (var i = 0, len = itemDTOs.length; i < len; i++) {
            var idPrefix = "itemDTOs" + i;
            var namePrefix = "itemDTOs[" + i + "]";
            var businessCategoryName = (null == itemDTOs[i].businessCategoryName || "null" == itemDTOs[i].businessCategoryName) ? "" : itemDTOs[i].businessCategoryName;
            var businessCategoryId = (null == itemDTOs[i].businessCategoryIdStr || "null" == itemDTOs[i].businessCategoryIdStr) ? "" : itemDTOs[i].businessCategoryIdStr;
            var productType = G.Lang.normalize(itemDTOs[i].productType);
            var trSample = '<tr class="bg titBody_Bg item1 table-row-original">' + '<td style="borderLeft:none;min-width:100px;">'
                + '<input id="' + idPrefix + '.commodityCode" name="' + namePrefix + '.commodityCode" class="table_input checkStringEmpty" style="width:85%" value="' + itemDTOs[i].commodityCode + '" type="text" title="' + itemDTOs[i].commodityCode + '" autocomplete="off" maxlength="20">'
                + '<input id="' + idPrefix + '.id" name="' + namePrefix + '.id" value="' + itemDTOs[i].idStr + '" type="hidden">'
                + '<input id="' + idPrefix + '.productType" name="' + namePrefix + '.productType" value="' + productType + '" type="hidden">'
                + '<input id="' + idPrefix + '.purchasePrice" name="' + namePrefix + '.purchasePrice" value="' + itemDTOs[i].purchasePrice + '" class="cPurchasePrice" type="hidden">'
                + '<input id="' + idPrefix + '.productId" name="' + namePrefix + '.productId" value="' + itemDTOs[i].productIdStr + '" type="hidden">';
            trSample += '<input id="' + idPrefix + '.productHistoryId" name="' + namePrefix + '.productHistoryId" value="' + G.Lang.isEmpty(itemDTOs[i].productHistoryIdStr) ? '' : itemDTOs[i].productHistoryIdStr + '" type="hidden">';
            trSample += '<input id="' + idPrefix + '.vehicleBrand" name="' + namePrefix + '.vehicleBrand" type="hidden" value="' + itemDTOs[i].vehicleBrand + '">'
                + '<input id="' + idPrefix + '.vehicleModel" name="' + namePrefix + '.vehicleModel" type="hidden" value="' + itemDTOs[i].vehicleModel + '">'
                + '<input id="' + idPrefix + '.vehicleYear" name="' + namePrefix + '.vehicleYear" type="hidden" value="">'
                + '<input id="' + idPrefix + '.vehicleEngine" name="' + namePrefix + '.vehicleEngine" type="hidden" value="">'
                + '<input id="' + idPrefix + '.isNewAdd" type="hidden" name="' + namePrefix + '.isNewAdd" value=""></td>'
                + '<td ><input id="' + idPrefix + '.productName" name="' + namePrefix + '.productName" class="table_input" style="width:85%" value="' + itemDTOs[i].productName + '" type="text" title="' + itemDTOs[i].productName + '" autocomplete="off" lastvalue="' + itemDTOs[i].productName + '">'
                + '<input class="edit1" type="button" id="' + idPrefix + '.editbutton" name="' + namePrefix + '.editbutton" onclick="searchInventoryIndex(this)" style="margin-left:6px" value="">'
                + '<input type="hidden" name="lack" id="lack" value=""></td>'
                + '<td ><input style="width:100%;" id="' + idPrefix + '.brand" name="' + namePrefix + '.brand" class="table_input" type="text" value="' + itemDTOs[i].brand + '"></td>'
                + '<td ><input style="width:100%;" id="' + idPrefix + '.spec" name="' + namePrefix + '.spec" class="table_input" type="text" value="' + itemDTOs[i].spec + '"></td>'
                + '<td ><input style="width:100%;" id="' + idPrefix + '.model" name="' + namePrefix + '.model" class="table_input" type="text" value="' + itemDTOs[i].model + '"></td>'
                + '<td ><input style="width:100%;" id="' + idPrefix + '.price" name="' + namePrefix + '.price" class="itemPrice table_input" title="' + itemDTOs[i].price + '" value="' + itemDTOs[i].price + '" type="text" maxlength="8"></td>'
                + '<td ><input style="width:100%;" id="' + idPrefix + '.amount" name="' + namePrefix + '.amount" class="itemAmount table_input" title="' + itemDTOs[i].amount + '" value="' + itemDTOs[i].amount + '" type="text"></td>'
                + '<td ><input style="width:100%;" id="' + idPrefix + '.unit" name="' + namePrefix + '.unit" class="itemUnit table_input" type="text" value="' + itemDTOs[i].unit + '">'
                + '<input id="' + idPrefix + '.storageUnit" name="' + namePrefix + '.storageUnit" class="itemStorageUnit table_input" type="hidden" value="' + itemDTOs[i].storageUnit + '">'
                + '<input id="' + idPrefix + '.sellUnit" name="' + namePrefix + '.sellUnit" class="itemSellUnit table_input" type="hidden" value="' + itemDTOs[i].sellUnit + '">'
                + '<input id="' + idPrefix + '.rate" name="' + namePrefix + '.rate" class="itemRate table_input" type="hidden" value="' + itemDTOs[i].rate + '"></td>'
                + '<td ><input id="' + idPrefix + '.total" name="' + namePrefix + '.total" value="' + itemDTOs[i].total + '" class="itemTotal table_input" type="hidden"><span id="' + idPrefix + '.total_span" name="' + namePrefix + '.total" class="itemTotalSpan" style="width:45px">itemDTOs[i].total</span></td>'
                + '<td class="storage_bin_td" ><span id="' + idPrefix + '.storageBinSpan" name="' + namePrefix + '.storageBinSpan"   style="display: block;">' + itemDTOs[i].storageBin + '</span></td>'
                + '<td ><span id="' + idPrefix + '.inventoryAmountSpan" name="' + namePrefix + '.inventoryAmountSpan">' + itemDTOs[i].inventoryAmount + '</span>'
                + '<input id="' + idPrefix + '.inventoryAmount" name="' + namePrefix + '.inventoryAmount" class="itemInventoryAmount table_input" title="' + itemDTOs[i].inventoryAmount + '" value="' + itemDTOs[i].inventoryAmount + '" readonly="true" type="hidden">' + '<input type="hidden" name="' + namePrefix + '.inventoryAmountHid" id="' + idPrefix + '.inventoryAmountHid" value="' + itemDTOs[i].inventoryAmountHid + '">'
                + '<span style="display: none;" class="j_new">新</span></td>' + '<td style="text-overflow:clip;">' + '<span class="reserved" id="' + idPrefix + '.reservedSpan" name="' + namePrefix + '.reservedSpan">' + itemDTOs[i].reserved + '</span>' + '<input type="button" name="lackMaterial" class="lackMaterial" id="' + idPrefix + '.yuliu" style="' + (itemDTOs[i].inventoryAmount + itemDTOs[i].reserved >= itemDTOs[i].amount ? 'display:none' : '') + '" value="">' + '<input id="' + idPrefix + '.reserved" name="' + namePrefix + '.reserved" value="' + itemDTOs[i].reserved + '" type="hidden"></td>'
                + '<input id="' + idPrefix + '.templateItemIdStr" name="' + namePrefix + '.templateItemIdStr" value="' + itemDTOs[i].templateItemIdStr + '" type="hidden">' + '<td >'
                + '<input id="' + idPrefix + '.businessCategoryName" name="' + namePrefix + '.businessCategoryName" hiddenValue="' + businessCategoryName + '" title="' + businessCategoryName + '" value="' + businessCategoryName + '" class="table_input businessCategoryName" type="text" />'
                + '<input id="' + idPrefix + '.businessCategoryId" name="' + namePrefix + '.businessCategoryId" value="' + businessCategoryId + '" type="hidden" />'
                + '</td>' + '<td style="border-right: medium none;">' + '<a class="opera1" id="' + idPrefix + '.opera1Btn">删除</a>' + (i == len - 1 ? '<a class="opera2">增加</a>' : '')
                + '</td></tr>'
            $("#table_productNo_2 tbody").append(trSample);
            activeRecommendSupplierTip($("#" + idPrefix + "\\.commodityCode").parent().parent(), getOrderType());
        }
        $("#saveA").html("改单");
        $("#saveBtn").removeClass("sendSingle").addClass("changeSingle");
    } else {
        trCount1 = 0;
        addNewRow(0);
        isShowAddButton2();
    }
}

function checkDraftOrderBox() {
    var vehicleId = $("#vechicleId").val();
    if (G.isEmpty($("#licenceNo").val()) || G.isEmpty(vehicleId)) {
        return;
    }
    //dialog初次加载时会加载div, 导致vehicleId参数无法传入，只能在请求前事先渲染。
//    $("#draftOrder_dialog").dialog({
//        resizable: true,
//        height: 400,
//        width: 900,
//        modal: true,
//        closeOnEscape: false
//    }).dialog("close");
//    var url = "draft.do?method=getDraftOrders";
//    var orderType = getOrderType();
//    APP_BCGOGO.Net.syncPost({
//        url: url,
//        dataType: "json",
//        data: {
//            startPageNo: 1,
//            orderTypes: orderType,
//            vehicleId: vehicleId
//        },
//        success: function(json) {
//            var draftNum = json[0].draftOrderData.length;
//            if (draftNum > 0) {
//                nsDialog.jConfirm("车牌号 " + $("#licenceNo").val() + " 有施工单草稿 " + draftNum + " 份，是否要使用草稿？", "友情提示", function(returnVal) {
//                    if (returnVal) {
//                        if (draftNum == 1) {
//                            window.location.href = "txn.do?method=getRepairOrderByDraftOrder&draftOrderId=" + json[0].draftOrderData[0].idStr;
//                        } else {
//                            initReapirOrderDraftTable(json);
//                            //下面是对jQuery dialog组件的改造
//                            var dialog_title = "施工单草稿箱";
//                            var titleDom = "<div id=\"div_drag_DraftTitle\" class=\"i_note more_title\">" + dialog_title + "(双击打开草稿)" + "</div>";
//                            $("#draftOrder_dialog").dialog({
//                                resizable: true,
//                                title: titleDom,
//                                height: 400,
//                                width: 900,
//                                modal: true,
//                                closeOnEscape: false
//                            });
//                        }
//                    }
//                });
//                $('.ui-dialog-buttonset :button').blur();
//            }
//        }
//    });
    $("#repairAndDraftOrder_dialog").dialog({
        resizable: true,
        height: 400,
        width: 900,
        modal: true,
        closeOnEscape: false
    }).dialog("close");
    var url = "txn.do?method=getRepairAndDraftOrders";
    var orderType = getOrderType();
    APP_BCGOGO.Net.syncPost({
        url: url,
        dataType: "json",
        data: {
            startPageNo: 1,
            orderTypes: orderType,
            vehicleId: vehicleId
        },
        success: function(json) {
            var countDraftOrders = json[0].countDraftOrders;
            var countRepairOrders = json[0].countRepairOrders
            if (countDraftOrders > 0 && countRepairOrders == 0) {
                nsDialog.jConfirm("车牌号 " + $("#licenceNo").val() + " 有施工单草稿 " + countDraftOrders + " 份，是否要使用草稿？", "友情提示", function(returnVal) {
                    if (returnVal) {
                        if (countDraftOrders == 1) {
                            window.location.href = "txn.do?method=getRepairOrderByDraftOrder&draftOrderId=" + json[0].draftOrderData[0].idStr;
                        } else {
                            initReapirAndDraftOrderTable(json);
                            //下面是对jQuery dialog组件的改造
                            var dialog_title = "施工单草稿箱";
                            var titleDom = "<div id=\"div_drag_DraftTitle\" class=\"i_note more_title\">" + dialog_title + "(双击打开草稿)" + "</div>";
                            $("#repairAndDraftOrder_dialog").dialog({
                                resizable: true,
                                title: titleDom,
                                height: 400,
                                width: 900,
                                modal: true,
                                closeOnEscape: false
                            });
                        }
                    }
                });
                $('.ui-dialog-buttonset :button').blur();
            } else if(countDraftOrders == 0 && countRepairOrders > 0) {
                nsDialog.jConfirm("车牌号 " + $("#licenceNo").val() + " 有未结算的施工单 " + countRepairOrders + " 份，是否使用施工单？", "友情提示", function(returnVal) {
                    if (returnVal) {
                        if (countRepairOrders == 1) {
                            window.location.href = "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + json[0].draftOrderData[0].idStr;
                        } else {
                            initReapirAndDraftOrderTable(json);
                            //下面是对jQuery dialog组件的改造
                            var dialog_title = "施工单";
                            var titleDom = "<div id=\"div_drag_DraftTitle\" class=\"i_note more_title\">" + dialog_title + "(双击打开施工单)" + "</div>";
                            $("#repairAndDraftOrder_dialog").dialog({
                                resizable: true,
                                title: titleDom,
                                height: 400,
                                width: 900,
                                modal: true,
                                closeOnEscape: false
                            });
                        }
                    }
                });
            } else if(countDraftOrders > 0 && countRepairOrders > 0) {
                nsDialog.jConfirm("车牌号 " + $("#licenceNo").val() + " 有施工单 " + countRepairOrders + " 份，草稿单" + countDraftOrders + "份，是否要使用施工单或草稿单？", "友情提示", function(returnVal) {
                    if (returnVal) {
                        initReapirAndDraftOrderTable(json);
                        //下面是对jQuery dialog组件的改造
                        var dialog_title = "施工单或草稿单";
                        var titleDom = "<div id=\"div_drag_DraftTitle\" class=\"i_note more_title\">" + dialog_title + "(双击打开施工单或草稿单)" + "</div>";
                        $("#repairAndDraftOrder_dialog").dialog({
                            resizable: true,
                            title: titleDom,
                            height: 400,
                            width: 900,
                            modal: true,
                            closeOnEscape: false
                        });
                    }
                });
            }
        }
    });
}

function initCustomerVehicleAndOrder(jsonStr) {
//    refreshRepairOrder();

    var _$createQualifiedDiv = $("#createQualifiedDiv");
    var _$showQualifiedSpan = $("#showQualifiedSpan");

    if (!jsonStr|| !jsonStr[0] || !jsonStr[0].idStr) {
        _$createQualifiedDiv.empty();
        _$createQualifiedDiv.append('<input type="button" id="createQualified" class="qualified" value="" style="margin: 12px 0 0;" onfocus="this.blur();"/>');
        _$createQualifiedDiv.append('<div style="width:100%; ">生成合格证</div>');
        _$showQualifiedSpan.empty();
    }

    if (!G.isEmpty($("#receiptNo").val())) {
        $("#print_div").show();
    } else {
        $("#print_div").hide();
    }
    if (!jsonStr || !jsonStr[0].idStr) {   //新输入的车牌号没有未结算的单据
        $("#receiptNoStr").text('（单据号由系统自动生成）');
        $("#receiptNo").val("");
        $("#description").val('');
        $("#repairPickingReceiptNo").text("");
        $("#repairPickingId").val("");
        $("#vehicleHandover").val($("#vehicleHandover").attr("initvehiclehandover"));
        $("#vehicleHandoverId").val($("#vehicleHandover").attr("initvehiclehandoverid"));
        $("#print_div").hide();
        initInsuranceInfo(null);
        if(!G.Lang.isEmpty($("#id").val())) {
            $("#id").val("");
            $("#status").val("");
            //TODO
            $("#draftOrderIdStr").val("");
            $("#productSaler").val("");
            $("#saveA").html("派单");
            $("#saveBtn").removeClass("changeSingle").addClass("sendSingle");

            $("#table_productNo_2 tr").detach(".item1");
            trCount1 = 0;
            addNewRow(0);
            isShowAddButton2();
            $("#table_task tr").detach(".item");
            taskaddNewRow();
            isShowAddButton();

            $("#table_otherIncome tr").detach(".item2");
            trCount3 = 0;
            otherIncomeAddNewRow();
            isShowAddButton3();

            refreshRepairOrder();
            $("#settledAmount").val(0);
            $("#debt").val(0);
            /*隐藏作废图标和作废按钮*/
            $(".invalidImg").hide();
            $(".reInput_div").hide();
            $("#orderStatusImag").hide();
            $("#receiptNo").val('');
            $("#receiptNoStr").text('');
        } else if ($("#customerId").val()) { //当前单据不存在有id的item ，但是有客户Id
            refreshRepairOrder();
        }
    }else{
        $("#vehicleHandover").val(G.Lang.normalize(jsonStr[0].vehicleHandover));
        $("#vehicleHandoverId").val(G.Lang.normalize(jsonStr[0].vehicleHandoverId));
    }
    setTotal();
    var customer = "";
    if (jsonStr && jsonStr[0]) {
        if(G.Lang.isEmpty(jsonStr[0].appointOrderIdStr)){
            $("#showAppointOrderSpan").empty();
        }
        repairOrderId = jsonStr[0].idStr;
        mybrandid = jsonStr[0].brandId;
        mymodelid = jsonStr[0].modelId;
        myyearid = jsonStr[0].yearId;
        myengineid = jsonStr[0].engineId;
        customerId = jsonStr[0].customerIdStr;
        customer = jsonStr[0].customer;
        vehicleId = jsonStr[0].vechicleIdStr;
        licenceNo = jsonStr[0].licenceNo;
        if (jsonStr[0].fuelNumber) $("#fuelNumber").attr("disabled", true);
        if (!$("#customerId").val() || (customerId && customerId != $("#customerId").val())) {
            $("#memberType").html(jsonStr[0].memberType);
            $("#memberNumber").html(jsonStr[0].memberNo);
            $("#memberServiceDeadLineStr").html(G.normalize(jsonStr[0].memberServiceDeadLineStr));
            $("#memberJoinDateStr").html(G.normalize(jsonStr[0].memberJoinDateStr));
            $("#memberStatus").html(jsonStr[0].memberStatus);
            if (!G.Lang.isEmpty(jsonStr[0].memberType)) {
                $("#memberRemainAmount").html(jsonStr[0].memberRemainAmount == '' ? '0' : dataTransition.rounding(parseFloat(jsonStr[0].memberRemainAmount), 2));
            } else {
                $("#memberRemainAmount").html(jsonStr[0].memberRemainAmount);
            }
            $("#customerConsume").html(G.normalize(jsonStr[0].total,"0"));
            $("#memberServiceInfo").empty();
            if(G.isNotEmpty(jsonStr[0].memberServiceDTOs)){
                $("#memberServiceCount").html(jsonStr[0].memberServiceDTOs.length);
                $.each(jsonStr[0].memberServiceDTOs,function(index,memberServiceDTO){
                    $("#memberServiceInfo").append('<div style="overflow: hidden;"><div style="text-overflow :ellipsis;overflow: hidden;white-space: nowrap;" title="'+memberServiceDTO.serviceName+'" class="div left">'+memberServiceDTO.serviceName+'</div><div class="div right">'+memberServiceDTO.timesStr+'</div></div>');
                });
            }else{
                $("#memberServiceCount").html(0);
            }
        }

        if(!G.Lang.isEmpty(jsonStr[0].memberNo)){
            $("#customerMemberInfoImg").css("display","inline-block");
            $("#gouka").text("续卡");
        }else{
            $("#gouka").text("购卡");
        }

        document.title = licenceNo;
        $("#vechicleId").val(jsonStr[0].vechicleIdStr);
        if (jsonStr[0].vechicleIdStr) {
            vechicleAndCustomerStatus = 2;
            $("#vehicleContact").val(jsonStr[0].vehicleContact);
            $("#vehicleMobile").val(jsonStr[0].vehicleMobile);
            $("#brand").val(jsonStr[0].brand);
            $("#input_brandname").val(jsonStr[0].brand);
            $("#brandId").val(jsonStr[0].brandId);

            $("#input_modelname").val(jsonStr[0].model);
            $("#model").val(jsonStr[0].model);
            $("#modelId").val(jsonStr[0].modelId);

            $("#span_year").text(jsonStr[0].year);
            $("#input_yearname").val(jsonStr[0].year);
            $("#year").val(jsonStr[0].year);
            $("#yearId").val(jsonStr[0].yearId);

            $("#span_engine").text(jsonStr[0].engine);
            $("#input_enginename").val(jsonStr[0].engine);
            $("#engine").val(jsonStr[0].engine);
            $("#engineId").val(jsonStr[0].engineId);
            $("#vehicleEngineNo").val(jsonStr[0].vehicleEngineNo);
            $("#vehicleColor").val(jsonStr[0].vehicleColor);
            $("#vehicleBuyDate").val(jsonStr[0].vehicleBuyDate);
            $("#vehicleChassisNo").val(jsonStr[0].vehicleChassisNo);
            $('#color').val(jsonStr[0].vehicleColor);
            $('#chassisNumber').val(jsonStr[0].vehicleChassisNo);
            $('#engineNo').val(jsonStr[0].vehicleEngineNo);
            $("#contact").val(jsonStr[0].contact);
            $("#address").val(jsonStr[0].address);
            $("#customerName").val(jsonStr[0].customerName);
            $("#customerId").val(jsonStr[0].customerIdStr);
            $("#mobile").val(jsonStr[0].mobile);
            $("#landLine").val(jsonStr[0].landLine);
            $("#qq").val(jsonStr[0].qq);
            $("#email").val(jsonStr[0].email);
            $("#contactId").val(jsonStr[0].contactIdStr);

            $("#byId").val(jsonStr[0].maintainTimeStr);
            $("#byId").attr("lastValue", jsonStr[0].maintainTimeStr);
            $("#bxId").val(jsonStr[0].insureTimeStr);
            $("#bxId").attr("lastValue", jsonStr[0].insureTimeStr);
            $("#ycId").val(jsonStr[0].examineTimeStr);
            $("#ycId").attr("lastValue", jsonStr[0].examineTimeStr);
            $("#maintainMileage").val(G.Lang.normalize(jsonStr[0].maintainMileage));
            $("#startMileage").val(G.Lang.normalize(jsonStr[0].startMileage));
        }
        if (G.isNotEmpty($("#customerId").val()) && G.isNotEmpty(jsonStr[0].vechicleIdStr)) {
            $("#receivable").html(G.normalize(jsonStr[0].debt,"0"));
            $("#payable").html(G.normalize(jsonStr[0].totalReturnDebt,"0"));
            initDuiZhanInfo();
            $("#customerConsume").html(G.normalize(jsonStr[0].total,"0"));
        }
        //洗车
        if (jsonStr[0].customerCard == true) { //TODO 显示洗车卡的信息，包括最近洗车时间和剩余洗车次数
            $(".stock_bottom").show();
            $("#lastWashTime").text(jsonStr[0].lastWashTime);
            $("#remainWashTimes").text(jsonStr[0].remainWashTimes);
            $("#washTimes").html(jsonStr[0].remainWashTimes);
            $("#washRemain").val(jsonStr[0].remainWashTimes);
        }
        if ($("#washTimes").html() != "" && $("#washTimes").html() != null) { /*隐藏作废图标和作废按钮*/
            if ($("#carWash").attr("class") == "title_hover") {
                $(".invalidImg").hide();
                $(".reInput_div").hide();
                $("#orderStatusImag").hide();
            }
            $("#chargeCash").focus();
        } else { /*隐藏作废图标和作废按钮*/
            if ($("#carWash").attr("class") == "title_hover") {
                $(".invalidImg").hide();
                $(".reInput_div").hide();
                $("#orderStatusImag").hide();
            }
            $("#normalCash").focus();
        }
        $("#todayWashTimes").val(jsonStr[0].todayWashTimes);
    }
    initAppointService(jsonStr);
    if (customerId) { //TODO 当客户不为空时，如下元素需要disabled
        var orderAttrs = ["brand", "model", "vehicleColor","vehicleChassisNo","vehicleEngineNo","year", "contact", "engine", "customerName", "mobile", "landLine","vehicleContact","vehicleMobile"];
        disableOrderElement(orderAttrs);
    } else {
        var orderAttrs = ["brand", "model","vehicleColor","vehicleChassisNo","vehicleEngineNo", "year", "contact", "engine", "customerName", "mobile", "landLine","vehicleContact","vehicleMobile"];
        enableOrderElement(orderAttrs);
    }
    trCount1 = $("#table_productNo_2 tr").size() - 2;
    trCount3 = $("#table_otherIncome tr").size() - 1;
    checkAndUpdateStorehouseStatus();
    checkDraftOrderBox();
}
/**
 * 有ID就为false
 */

function judgeItemExsit() {
    var flag = false;
    $(".item input[id$='.id']").each(function() {
        if ($(this).val()) {
            flag = true;
            return false;
        }
    });
    if (!flag) {
        $(".item1 input[id$='.id']").each(function() {
            if ($(this).val()) {
                flag = true;
                return false;
            }
        });
    }
    return flag;
}

function vehicleIsEmptyBlur() {
    var flag = judgeItemExsit();
    if (flag) {
        var isAdd1 = false,
            isAdd2 = false;
        $("#saveA").html("派单");
        $("#saveBtn").removeClass("changeSingle").addClass("sendSingle");
        $(".item1").each(function() {
            isAdd1 = $(this).find("td").first().find("input:first").val() ? true : false;
            return;
        });
        $(".item").each(function() {
            isAdd2 = $(this).find("td").eq(1).find("input:first").val() ? true : false;
            return;
        });
        if (isAdd1) {
            $("#table_productNo_2 tr").detach(".item1");
            trCount1 = 0;
            addNewRow(0);
            isShowAddButton2();
        }
        if (isAdd2) {
            $("#table_task tr").detach(".item");
            taskaddNewRow();
            isShowAddButton();
        }
        $("#settledAmount").val(0);
        $("#debt").val(0);
        /*隐藏作废图标和作废按钮*/
        $(".invalidImg").hide();
        $(".reInput_div").hide();
        $("#orderStatusImag").hide();
    }
    if ($("#washTimes").html() != "" && $("#washTimes").html() != null) { /*隐藏作废图标和作废按钮*/
        if ($("#carWash").attr("class") == "title_hover") {
            $(".invalidImg").hide();
            $(".reInput_div").hide();
            $("#orderStatusImag").hide();
        }
        $("#chargeCash").focus();
    } else { /*隐藏作废图标和作废按钮*/
        if ($("#carWash").attr("class") == "title_hover") {
            $(".invalidImg").hide();
            $(".reInput_div").hide();
            $("#orderStatusImag").hide();
        }
        $("#normalCash").focus();
    }
}

function judgeRepeat(existVal, selectVal) {
    if (!existVal) {
        return false;
    }
    var existVals = existVal.split(",");
    var flag = false;
    for (var i in existVals) {
        if (existVals[i] && typeof(existVals[i]) != "function" && $.trim(existVals[i]) == selectVal) {
            flag = true;
            break;
        }
    }
    return flag;
}

function setValue(customerId, mobile, landLine) {
    $("#mobile").val(mobile);
    $("#landLine").val(landLine);

    $("#customerId").val(customerId);
    $("#returnInfo").remove();
    //根据customerId判断是否欠款
    $.ajax({
        type: "POST",
        url: "customer.do?method=getDebtByCustomerId",
        async: true,
        data: {
            customerId: customerId
        },
        cache: false,
        dataType: "json",
        error: function(XMLHttpRequest, error, errorThrown) {

        },
        success: function(data) {
            //隐藏欠款结算

            $("#receivable").html("0");

            $("#payable").html("0");

            initDuiZhanInfo();
            $("#rmbTag").hide();


            if (data != null && data != undefined && data.length == 1) {
                var totalDebt = data[0].totalReceivable;
                var totalConsume = data[0].totalConsume;
                var totalReturnDebt = data[0].totalDebt;
                $("#receivable").html(G.normalize(totalDebt,"0"));
                $("#payable").html(G.normalize(totalReturnDebt,"0"));

                initDuiZhanInfo();

                $("#customerConsume").html(G.normalize(totalConsume,"0"));

            }
        }
    });
}

function getServiceTotal() {
    var count = 0;
    var serviceTotal = 0;
    var salesTotal = 0;
    $(".serviceTotal").each(function(i) {
        var txt = $(this);
        if ($(this).attr('disabled') == false && $.trim(txt.val()) != '') {
            count += parseFloat(txt.val());
            serviceTotal += parseFloat(txt.val());
        }

    });
    $(".itemTotal").each(function(i) {
        var txt = $(this);
        if ($(this).attr('disabled') == false && $.trim(txt.val()) != '') {
            count += parseFloat(txt.val());
            salesTotal += parseFloat(txt.val());
        }

    });
    $("#serviceTotal").val(dataTransition.rounding(serviceTotal, 2));
    $("#salesTotal").val(dataTransition.rounding(salesTotal, 2));
    $("#serviceTotalSpan").text(dataTransition.rounding(serviceTotal, 2));
    $("#salesTotalSpan").text(dataTransition.rounding(salesTotal, 2));


    var otherTotal = 0;
    $(".otherIncomePrice").each(function (i) {
        var txt = $(this);
        var idPrefix = $(this).attr("id").split(".")[0];

        if ($("#" + idPrefix + "\\.name").val() == "材料管理费" && $("#" + idPrefix + "\\.otherIncomePriceByRate").attr("checked")) {
            $("#" + idPrefix + "\\.otherIncomePriceSpan").text(dataTransition.rounding($("#" + idPrefix + "\\.otherIncomePriceRate").val() * $("#salesTotalSpan").text() / 100,2));
            $("#" + idPrefix + "\\.price").val($("#" + idPrefix + "\\.otherIncomePriceSpan").text());


            $("#" + idPrefix + "\\.otherIncomeCalculateWay").val("RATIO");
            if ($("#" + idPrefix + "\\.otherIncomeCostPriceCheckbox").attr("checked")) {
                $("#" + idPrefix + "\\.otherIncomeCostPrice").val($("#" + idPrefix + "\\.price").val());
            }

            if ($("#" + idPrefix + "\\.otherIncomePriceRate").val() != "请输入比率") {
                $("#" + idPrefix + "\\.otherIncomeRate").val($("#" + idPrefix + "\\.otherIncomePriceRate").val());
            }

        } else if ($("#" + idPrefix + "\\.name").val() == "材料管理费" && $("#" + idPrefix + "\\.otherIncomePriceByAmount").attr("checked")) {
            $("#" + idPrefix + "\\.price").val(dataTransition.rounding($("#" + idPrefix + "\\.otherIncomePriceText").val(),2));
            $("#" + idPrefix + "\\.otherIncomeCalculateWay").val("AMOUNT");
        }else  if($("#" + idPrefix + "\\.name").val() != "材料管理费"){
            $("#" + idPrefix + "\\.otherIncomeRate").val("");
            $("#" + idPrefix + "\\.otherIncomeCalculateWay").val("");
        }

        if(!$("#" + idPrefix + "\\.otherIncomeCostPriceCheckbox").attr("checked")){
            $("#" + idPrefix + "\\.otherIncomeCostPrice").val("");
        }else{
            $("#" + idPrefix + "\\.otherIncomeCostPrice").val(dataTransition.rounding($("#" + idPrefix + "\\.otherIncomeCostPrice").val(),2));
        }

        if ($(this).attr('disabled') == false && $.trim(txt.val()) != '') {
            otherTotal += parseFloat(txt.val());
        }
    });
    otherTotal =dataTransition.rounding(otherTotal,2);
    count += parseFloat(otherTotal);
    $("#otherTotalSpan").text(otherTotal);

    return count;
}


function setOtherFeeTotal() {
    var otherTotal = 0;
    $(".otherIncomePrice").each(function (i) {
        var txt = $(this);
        if ($(this).attr('disabled') == false && $.trim(txt.val()) != '') {
            if($("#" + $(this).attr("id").split(".")[0] +"\\.name").val() == "材料管理费"){
                var idPrefix = $(this).attr("id").split(".")[0];
                $("#" + idPrefix + "\\.otherIncomePriceSpan").text(App.StringFilter.inputtingPriceFilter($(this).val() * $("#salesTotalSpan").text()/100, 2));
                $(this).val($("#" + idPrefix + "\\.otherIncomePriceSpan").text());
            }
            otherTotal += parseFloat(txt.val());
        }
    });
    $("#otherTotalSpan").text(otherTotal);
}


//TODO 设置总计

function setTotal() {
    setItemTotal();
    var count = getServiceTotal();
    count = dataTransition.rounding(count, 2);
    if (count != $("#total").val()) {
        $("#totalSpan").text(count);
        $("#total").val(count);
        if ($("#settledAmountHid").val() <= 0) {
            $("#settledAmount").val(count);
        }
        $("#debt").val(dataTransition.rounding(($("#total").val() - $("#settledAmount").val()), 2));
        if ($("#debt").val() * 1 <= 0) {
            $("#input_makeTime").hide();
            $("#huankuanTime").val("");
        }
    }
}

//TODO 设置小计

function setItemTotal() {
    $(".itemPrice").each(function (i) {
        var price = $.trim($(this).val());
        var idPrefix = $(this).attr("id").split(".")[0];
        var amount = $("#" + idPrefix + "\\.amount").val();
        if ($.trim(price) && $.trim(amount)) {
            var count = parseFloat(price * amount) * 1;
            var count1 = GLOBAL.Number.filterZero(count, 2);
            $(".itemTotal").eq(i).val(count1);
            $(".itemTotalSpan").eq(i).text(count1 ? count1 : '');
            $(".itemTotalSpan").eq(i).attr("title", count1.toString());
        } else {
            $(".itemTotal").eq(i).val(0);
            $(".itemTotalSpan").eq(i).text('');
            $(".itemTotalSpan").eq(i).attr("title", '');
        }
        htmlNumberFilter($('#' + idPrefix + '\\.inventoryAmountSpan').add('#' + idPrefix + '\\.reservedSpan').add('#' + idPrefix + '\\.price').add('#' + idPrefix + '\\.amount'), true);
    });
}

function setServicePrice(event) {
    $(".actualHours").each(function(i) {
        var actualHours = $(this).val();
        var idPrefix = $(this).attr("id").split(".")[0];
        var standardUnitPrice = $("#" + idPrefix + "\\.standardUnitPrice").val();
        if (!G.Lang.isEmpty(actualHours) && !G.Lang.isEmpty(standardUnitPrice)) {
            if ($.trim(actualHours)) {
                actualHours = App.StringFilter.inputtingPriceFilter(actualHours, 2)
                $(".actualHours").eq(i).val(actualHours);
            }
            if ($.trim(standardUnitPrice)) {
                standardUnitPrice = App.StringFilter.inputtingPriceFilter(standardUnitPrice, 2)
                $(".standardUnitPrice").eq(i).val(standardUnitPrice);
            }
            var count = GLOBAL.Number.filterZero(parseFloat(actualHours * standardUnitPrice) * 1,2);
            if (event.target.id.split(".")[0] == idPrefix) {
                $("#" + idPrefix + "\\.total").val(count);
            }
            htmlNumberFilter($("#" + idPrefix + "\\.standardHours").add("#" + idPrefix + "\\.standardUnitPrice").add("#" + idPrefix + "\\.actualHours").add("#" + idPrefix + "\\.total"), true);
        }
    });
    setTotal();
}


//TODO 判断是否缺料

function isLack() {
    //todo 在找到更好方法前，暂时嵌套全局变量来控制不同店铺版本不同操作
    if (App.Permission.Version.IgnorVerifierInventory) {
        $(".lackMaterial").unbind("click");
        $(".lackMaterial").hide();
        return "false";
    }
    var lack = "false";
    var permissionGoodsStorage = $("#permissionGoodsStorage").val();
    $(".itemAmount").each(function(i) {
        var amount = $(".itemAmount").eq(i).val(); //TODO 所填的销售数量
        var itemInventoryAmount = $(".itemInventoryAmount").eq(i).val(); //TODO 库存量
        var lackMaterial = !$.trim($(".reserved").eq(i).html()) ? 0 * 1 : $(".reserved").eq(i).html() * 1;
        var inventoryTotalAmount = '';//计算后的库存总量 1当不改变仓库时，其值等于库存量 + 预留量  2改变仓库时，其值等于库存量
        if ($("#hiddenStorehouseId").val() != $("#storehouseId").val()) {
            //2改变仓库时，其值等于库存量
            inventoryTotalAmount = itemInventoryAmount * 1;
        } else {
            //1当不改变仓库时，其值等于库存量 + 预留量
            inventoryTotalAmount = itemInventoryAmount * 1 + lackMaterial;
        }
        if ((amount * 1 > inventoryTotalAmount) && ($.trim($(".reserved").eq(i).html()) != "") && ($($(".itemInventoryAmount").eq(i)).next().next().css('display') == 'none')) {
            $(".lackMaterial").eq(i).css('display', '');
            if (permissionGoodsStorage == "false") {
                $(".lackMaterial").eq(i).unbind("click");
                $(".lackMaterial").eq(i).css('cursor', 'default');
            }
            lack = "true";
        }
        if (amount * 1 <= inventoryTotalAmount) {
            $(".lackMaterial").eq(i).css('display', 'none');
            return "error";
        }

    });

    return lack;
}

function isThisItemLack($row) {
    if (App.Permission.Version.IgnorVerifierInventory) {
        $(".lackMaterial").unbind("click").hide();
        return false;
    }
    if ($row.find(".j_new").css('display') != 'none') {
        return true;
    }
    var permissionGoodsStorage = $("#permissionGoodsStorage").val();
    var amount = $row.find(".itemAmount").val(), //TODO 所填的销售数量
        $lackMaterial = $row.find(".lackMaterial"),
        $reserved = $row.find(".reserved"),
        $itemInventoryAmount = $row.find(".itemInventoryAmount");

    var itemInventoryAmount = $itemInventoryAmount.val(); //TODO 库存量
    var lackMaterial = !$.trim($reserved.html()) ? 0 : Number($reserved.html());

    if ((Number(amount) > (Number(itemInventoryAmount) + lackMaterial)) && ($.trim($reserved.html()) != "") && ($itemInventoryAmount.next().next().css('display') == 'none')) {
        $lackMaterial.css('display', '');
        if (permissionGoodsStorage == "false") {
            $lackMaterial.unbind("click")
            $lackMaterial.css('cursor', 'default')
        }
        return true;
    }
    if (Number(amount) <= Number(itemInventoryAmount) + lackMaterial) {
        $lackMaterial.css('display', 'none');
    }
    return false;
}


function isReservedEnough() {
    var enough = true;
    var permissionGoodsStorage = $("#permissionGoodsStorage").val();
    $(".itemAmount").each(function(i) {
        var amount = $(this).val() * 1;
        var idPrefix =  $(this).attr("id").split(".")[0];
        var reservedAmount = $("#" + idPrefix + "\\.reserved").val() * 1;
        if (amount > reservedAmount) {
            enough = false;
            return false;
        }

    });

    return  enough;
}

//TODO 有个SPAN标签，显示表示新产品，隐藏表示老商品，亟待修改

function isnew() {
//todo 在找到更好方法前，暂时嵌套全局变量来控制不同店铺版本不同操作
    if (App.Permission.Version.StoreHouse) {//ajax后台校验
        return "false";
    } else {
        if (App.Permission.Version.IgnorVerifierInventory) {
            return "false";
        }
    }
    var newProduct = "false";
    $("#table_productNo_2").find("tr").each(function() {
        if ($(this).find("td:nth-child(11)").find("span").eq(1).css("display") == "block") {
            newProduct = "true";
        }
    });
    return newProduct;
}


/* 检查是否在制定日期之前*/

function isPreDate(dateStr1, dateStr2) {
    var d1 = stringToDate($.trim(dateStr1), true);
    var d2 = stringToDate($.trim(dateStr2), true);
    return d1 >= d2;
}
/**
 * 验证预约出厂时间格式
 */

function endDateStrValidate(dateStr) {
    var reg = /^(\d{1,4})(-)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2})$/;
    var r = dateStr.match(reg);
    if (r == null) return true
    else return false;
}

/* 检查字符串是否是时间*/

function isDateString(sDate) {
    var iaMonthDays = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
    var iaDate = new Array(3);
    var year, month, day;

    if (arguments.length != 1) return false;
    iaDate = sDate.toString().split("-");
    if (iaDate.length != 3) return false;
    if (iaDate[1].length > 2 || iaDate[2].length > 2) return false;

    year = parseFloat(iaDate[0]);
    month = parseFloat(iaDate[1]);
    day = parseFloat(iaDate[2]);

    if (year < 1900 || year > 2100) return false;
    if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) iaMonthDays[1] = 29;
    if (month < 1 || month > 12) return false;
    if (day < 1 || day > iaMonthDays[month - 1]) return false;
    return true;
}

/* 字符串转换成日期*/

function stringToDate(sDate, bIgnore) {
    var bValidDate, year, month, day, hour, minite, second;
    var iaDate = new Array(2);
    var dateStr = new Array(3);
    var timeStr = new Array(3);

    if (bIgnore) bValidDate = isDateString(sDate);
    else bValidDate = true;

    if (bValidDate) {
        iaDate = sDate.toString().split(" ");
        dateStr = iaDate[0].toString().split("-");
        timeStr = iaDate[1].toString().split(":");
        year = parseFloat(dateStr[0]);
        month = parseFloat(dateStr[1]) - 1;
        day = parseFloat(dateStr[2]);
        hour = parseFloat(timeStr[0]);
        minite = parseFloat(timeStr[1]);

        return(new Date(year, month, day, hour, minite));
    } else return(new Date(1900, 1, 1, 0, 0, 0));
}

//TODO 打开车辆历史记录页面

function getCarHistory(licenceNo) {
    if (!licenceNo) {
        alert("请输入车牌号!");
        return;
    }
    bcgogo.checksession({
        "parentWindow": window.parent,
        'iframe_PopupBox': $("#iframe_PopupBox")[0],
        'src': "goodsHistory.do?method=createCarHistory&orderType=REPAIR&licenceNo=" + encodeURI(licenceNo) + "&issubmit=true"
    });

}

$(document).ready(function() {

    $(".standardHours,.standardUnitPrice,.actualHours").live("keyup blur", function(event) {
        if (event.type == "focusout")
            event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
        else if (event.type == "keyup")
            if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
                event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
            }
    });

    $(".standardUnitPrice,.actualHours").live("blur", function(event) {
        setServicePrice(event);
    });

    $("#settledAmount").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/right|down/) != -1) {
            if ($("#saveBtn").css("display") === "none") {
                $("#debt").select().focus();
            } else {
                var target = $("#accountBtn");
                target.select().focus();
            }
        }
    });

    $("#debt").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/right|down/) != -1) {
            $("#saveBtn").select().focus();
        }
    });

    $("#saveBtn").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/right|down/) != -1) {
            $("#finishBtn").select().focus();
        }
    });

    $("#finishBtn").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/right|down/) != -1) {
            $("#accountBtn").select().focus();
        }
    });

    $("#accountBtn").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/right|down/) != -1) {
            $("#cancelBtn").select().focus();
        }
    });
    $("#cancelBtn").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/right|down/) != -1) {
            $("#printBtn").select().focus();
        }
    });
    $("#printBtn").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/right|down/) != -1) {
            var selector = ".item" + $("#table_task").css("display") !== "none" ? "" : "1" + " :text:first";
            $(selector).select().focus();
        }
    });

    //向左上
    $("#printBtn").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/left|top/) != -1) {
            $("#cancelBtn").select().focus();
        }
    });
    $("#cancelBtn").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/left|top/) != -1) {
            $("#accountBtn").select().focus();
        }
    });
    $("#accountBtn").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/left|top/) != -1) {
            $("#finishBtn").select().focus();
        }
    });

    $("#finishBtn").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/left|top/) != -1) {
            $("#saveBtn").select().focus();
        }
    });
    $("#saveBtn").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/left|top/) != -1) {
            $("#debt").select().focus();
        }
    });

    $("#debt").live('keydown', function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/left|top/) != -1) {
            $("#settledAmount").select().focus();
        }
    });
    $("#settledAmount").live("keydown", function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName.search(/left|top/) != -1) {
            $(".item1:last>td:eq(9)>input[type='text']").select().focus();
        }
    });


    $(".i_operate").live("keydown", function(event) {
        var keyName = G.Interactive.keyNameFromEvent(event);
        if (keyName == "enter") {
            $(this).click();
        }
    });

    $("input[name$='memo']").live("keyup", function() {
        $(this).val(App.StringFilter.inputtingOtherCostNameFilter($(this).val()));
    });

    $("input[name$='memo']").live("blur", function() {
        $(this).val($.trim($(this).val()));
    });
    $("#repairPickingReceiptNo").bind("click", function() {
        if ($("#repairPickingId").val()) {
            window.open("pick.do?method=showRepairPicking&repairPickingId=" + $("#repairPickingId").val(), "_blank");
        }
    });

    var fromDraftOrderUrls = ["method=getRepairOrderByDraftOrder"];
    if (G.Lang.isNotEmpty(fromDraftOrderUrls)) {
        var isNeedToCheckDraftOrder = false;
        var currentUrl = window.location.href;
        for (var i = 0, len = fromDraftOrderUrls.length; i < len; i++) {
            if (currentUrl.search(fromDraftOrderUrls[i] > -1)) {
                isNeedToCheckDraftOrder = true;
            }
        }
        var draftOrderId = $("#draftOrderIdStr").val();
        var customerId = $("#customerId").val();
        if (isNeedToCheckDraftOrder && G.Lang.isNotEmpty(draftOrderId)) {
            App.Net.syncPost({
                url: "customer.do?method=ajaxGetCustomerInfoById",
                dataType: "json",
                data: {customerId:customerId},
                success: function (customerInfo) {
                    if (!G.Lang.isEmpty(customerInfo)) {
                        var vehicleDTOs = customerInfo["vehicleDTOs"];
                        var customerDTO =  customerInfo["customerDTO"];
                        var memberDTO = customerInfo["memberDTO"];
                        var isCustomerInfoSame = true;
                        var msg = "";
                        if(G.Lang.isNotEmpty(customerDTO)){
                            if (!G.Lang.compareStrSame($("#customerName").val(), customerDTO["name"])) {
                                isCustomerInfoSame = false;
                                msg += '【客户名】';
                            }
                            if (!G.Lang.compareStrSame($("#landLine").val(), customerDTO["landLine"])) {
                                isCustomerInfoSame = false;
                                msg += '【座机】';
                            }
                            if (!G.Lang.compareStrSame($("#contact").val(), customerDTO["contact"])) {
                                isCustomerInfoSame = false;
                                msg += '【联系人】';
                            }
                            if (!G.Lang.compareStrSame($("#mobile").val(), customerDTO["mobile"])) {
                                isCustomerInfoSame = false;
                                msg += '【手机】';
                            }
                            var draftOrderVehicleId = $("#vechicleId").val();
                            var draftOrderVehicleNo = $("#licenceNo").val();
                            var vehicleDTO = null;
                            if(G.Lang.isNotEmpty(vehicleDTOs) && G.Lang.isNotEmpty(draftOrderVehicleId)){
                                for(var i= 0,len = vehicleDTOs.length;i<len;i++){
                                    if(G.Lang.isNotEmpty(vehicleDTOs[i]) && vehicleDTOs[i]["idStr"] == draftOrderVehicleId){
                                        vehicleDTO = vehicleDTOs[i];
                                        break;
                                    }
                                }
                            }
                            if (vehicleDTO != null) {
                                if (!G.Lang.compareStrSame($("#vehicleContact").val(), vehicleDTO["contact"])) {
                                    isCustomerInfoSame = false;
                                    msg += '【车主】';
                                }
                                if (!G.Lang.compareStrSame($("#vehicleMobile").val(), vehicleDTO["mobile"])) {
                                    isCustomerInfoSame = false;
                                    msg += '【车主手机】';
                                }
                                if (!G.Lang.compareStrSame($("#brand").val(), vehicleDTO["brand"])) {
                                    isCustomerInfoSame = false;
                                    msg += '【品牌】';
                                }
                                if (!G.Lang.compareStrSame($("#model").val(), vehicleDTO["model"])) {
                                    isCustomerInfoSame = false;
                                    msg += '【车型】';
                                }
                                if (!G.Lang.compareStrSame($("#vehicleChassisNo").val(), vehicleDTO["chassisNumber"])) {
                                    isCustomerInfoSame = false;
                                    msg += '【车架号】';
                                }
                                if (!G.Lang.compareStrSame($("#vehicleEngineNo").val(), vehicleDTO["engineNo"])) {
                                    isCustomerInfoSame = false;
                                    msg += '【发动机号】';
                                }
                                if (!G.Lang.compareStrSame($("#vehicleColor").val(), vehicleDTO["color"])) {
                                    isCustomerInfoSame = false;
                                    msg += '【车身颜色】';
                                }
                                var isAppointOrderInfoSame = true;
                                if (!G.Lang.compareStrSame($("#byId").val(), vehicleDTO["maintainTimeStr"])) {
                                    isCustomerInfoSame = false;
                                    isAppointOrderInfoSame = false;
                                }
                                if (!G.Lang.compareStrSame($("#bxId").val(), vehicleDTO["insureTimeStr"])) {
                                    isCustomerInfoSame = false;
                                    isAppointOrderInfoSame = false;
                                }
                                if (!G.Lang.compareStrSame($("#ycId").val(), vehicleDTO["examineTimeStr"])) {
                                    isCustomerInfoSame = false;
                                    isAppointOrderInfoSame = false;
                                }
                                if (!G.Lang.compareStrSame($("#maintainMileage").val(),String(vehicleDTO["maintainMileage"]))) {
                                    isCustomerInfoSame = false;
                                    isAppointOrderInfoSame = false;
                                }
                                if(!isAppointOrderInfoSame){
                                    msg += '【预约信息】';
                                }
                            }
                            if(!isCustomerInfoSame){
                                nsDialog.jConfirm("友情提示：草稿中的客户、车辆信息" + msg + "与客户最新信息不一致！是否使用客户最新信息？",null,function(result){
                                    if(result){
                                        $("#customerName").val(G.Lang.normalize(customerDTO["name"])).attr("disabled", G.Lang.isNotEmpty(customerDTO["name"]));
                                        $("#landLine").val(G.Lang.normalize(customerDTO["landLine"])).attr("disabled", G.Lang.isNotEmpty(customerDTO["landLine"]));
                                        $("#contact").val(G.Lang.normalize(customerDTO["contact"])).attr("disabled", G.Lang.isNotEmpty(customerDTO["contact"]));
                                        $("#mobile").val(G.Lang.normalize(customerDTO["mobile"])).attr("disabled", G.Lang.isNotEmpty(customerDTO["mobile"]));
                                        if (vehicleDTO != null) {
                                            $("#vehicleContact").val(G.Lang.normalize(vehicleDTO["contact"])).attr("disabled", G.Lang.isNotEmpty(vehicleDTO["contact"]));
                                            $("#vehicleMobile").val(G.Lang.normalize(vehicleDTO["mobile"])).attr("disabled", G.Lang.isNotEmpty(vehicleDTO["mobile"]));
                                            $("#brand").val(G.Lang.normalize(vehicleDTO["brand"])).attr("disabled", G.Lang.isNotEmpty(vehicleDTO["brand"]));
                                            $("#model").val(G.Lang.normalize(vehicleDTO["model"])).attr("disabled", G.Lang.isNotEmpty(vehicleDTO["model"]));
                                            $("#vehicleChassisNo").val(G.Lang.normalize(vehicleDTO["chassisNumber"])).attr("disabled", G.Lang.isNotEmpty(vehicleDTO["chassisNumber"]));
                                            $("#vehicleEngineNo").val(G.Lang.normalize(vehicleDTO["engineNo"])).attr("disabled", G.Lang.isNotEmpty(vehicleDTO["engineNo"]));
                                            $("#vehicleColor").val(G.Lang.normalize(vehicleDTO["color"])).attr("disabled", G.Lang.isNotEmpty(vehicleDTO["color"]));
                                            $("#byId").val(G.Lang.normalize(vehicleDTO["maintainTimeStr"]));
                                            $("#bxId").val(G.Lang.normalize(vehicleDTO["insureTimeStr"]));
                                            $("#ycId").val(G.Lang.normalize(vehicleDTO["examineTimeStr"]));
                                            $("#maintainMileage").val(G.Lang.normalize(vehicleDTO["maintainMileage"]));
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            });
        }
    }
});

function checkNumInTwo(num) {
    var reg = /^\d+\.?\d{0,2}$/;
    return reg.exec(num);
}

//TODO 该工具类是为了AJAX查询洗车卡使用状况（最近洗车时间，剩余洗车次数）

function ajaxForInvoiceUtil() {
    this.ajaxToGetWashTimes = function(customerId) {
        var ajaxUrl = "txn.do?method=queryWashOrder";
        var ajaxData = {
            customerId: customerId
        };
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.ajaxQuery(function(jsonStr) {
            if (jsonStr.length > 0) initWashTimes(jsonStr);
        });
    }

    var initWashTimes = function(jsonStr) {
        $("#lastWashTime").html(formatJson(jsonStr[0].lastModifiedStr, true));
        $("#remainWashTimes").html(formatJson(jsonStr[0].washRemain, false));
        $("#stock_bottom_wash").show();
    }

    var formatJson = function(v, flag) {
        if (!v) {
            return flag ? "" : 0;
        }
        return v;
    }
}

function initInvoiceJSP(target, flag) {
    //如果单子状态为3，即已经结算的，再点击，则生成一张新单子
    if ($("#status").val() == "REPAIR_SETTLED" || $("#status").val() == "REPAIR_REPEAL") {
        //根据车牌重新生成一张单子
        if (target.is("#carWash")) {
            window.location.href = 'txn.do?method=getRepairOrderByVehicleNumber&vehicleNumber=' + encodeURIComponent($.trim($("#licenceNo").val())) + "&task=wash";
        } else if (target.is("#carMaintain")) {
            window.location.href = 'txn.do?method=getRepairOrderByVehicleNumber&vehicleNumber=' + encodeURIComponent($.trim($("#licenceNo").val())) + "&task=maintain";
        } else {
            window.location.href = 'txn.do?method=getRepairOrderByVehicleNumber&vehicleNumber=' + encodeURIComponent($.trim($("#licenceNo").val()));
        }
    }
    $("#div_brand").hide();
    $("#div_works").hide();
    target.addClass("title_hover");
    $(".i_mainTitle a").not(target).removeClass();
    if (target.is("#shoppingSell")) {

        $("#pageType").val('sale');
        //remove red star
        $(".i_tableStar").show();

        $("#repairOrderForm #serviceType").val("SALES");
        $("#iframe_qiankuan").hide(); //欠款
        //end
        $("#table_task").hide();
        $("#table_carWash").hide();
        $("#washHistory").hide();
        $("#vehicleHistory").show();
        $("#table_productNo_2").show();
        $(".tableInfo2").show();
        $(".tableInfo").each(function() {
            $(this).show();
        });
        $("#table_task input[name$='.id'],#table_task input[name$='.service'],#table_task input[name$='.total'],#table_task input[name$='.memo']").attr('disabled', 'disabled');
        $("#saveBtn,#finishBtn").hide();
        $("#saveA,#saveB").hide();
        //生成新的商品销售单
        $("#table_productNo_2 tr").not($(".i_tabelBorder,.table_title")).remove();
        addNewRow("repairSale");
        isShowAddButton2();

    } else if (target.is("#carMaintain")) {
        $("#pageType").val('');
        //remove red star
        $(".i_tableStar").show();
        $("#repairOrderForm #serviceType").val("REPAIR");

        $("#iframe_qiankuan").hide(); //欠款
        $(".tableInfo2").show();

        $("#table_task").show();
        $("#table_carWash").hide();
        $("#washHistory").hide();
        $("#vehicleHistory").show();
        $("#table_productNo_2").show();
        $(".tableInfo").each(function() {
            $(this).show();
        });
        if ($("#status").val() == 'REPAIR_DISPATCH') {
            $(".invalidImg,#print_div").show();
            $(".reInput_div").show();
            $("#orderStatusImag").removeClass("zuofei").addClass("shigong").show();
        }
        if ($("#status").val() == 'REPAIR_DONE') {
            $(".invalidImg,#print_div").show();
            $(".reInput_div").show();
            $("#orderStatusImag").removeClass("zuofei").addClass("wangong").show();
        }
        if ($("#status").val() == 'REPAIR_SETTLED') {
            $(".invalidImg,#print_div").show();
            $(".reInput_div").show();
            $("#orderStatusImag").removeClass("zuofei").addClass("jie_suan").show();
        }
        if ($("#status").val() == 'REPAIR_REPEAL') {
            $("#orderStatusImag").show();
            $(".reInput_div,#print_div").show();
            $(".invalidImg").hide();
        }
        if ($("#status").val() == '') {
            $("#orderStatusImag,#print_div").hide();
            $(".reInput_div").hide();
            $(".invalidImg").hide();
        }
        $("#table_task input[name$='.id'],#table_task input[name$='.service'],#table_task input[name$='.total'],#table_task input[name$='.memo']").removeAttr('disabled');
        $("#save_div").css("display", "block");
        $("#finish_div").css("display", "block");
        $("#account_div").css("display", "block");
        $("#saveBtn,#finishBtn").show();
        $("#saveA,#saveB").show();
    } else if (target.is("#carWash")) {
        $("#pageType").val('washcar');
        //remove red star
        $(".i_tableStar").hide();
        $(".tableInfo2").hide();
        //show the first
        $($(".i_tableStar").get(0)).show();
        $("#orderStatusImag").hide();
        $(".invalidImg").hide();
        $(".reInput_div").hide();
        $("#repairOrderForm #serviceType").val("WASH");

        $("#iframe_qiankuan").hide(); //欠款
        $("#table_task").hide();
        $("#table_productNo_2").hide();
        $("#vehicleHistory").hide();
        $("#washHistory").show();
        $(".tableInfo").each(function() {
            $(this).hide();
        });
        $("#table_carWash").show();
        $("#table_task input[name$='.id'],#table_task input[name$='.service'],#table_task input[name$='.total'],#table_task input[name$='.memo']").removeAttr('disabled');
        //非会员，光标自动停留本次洗车金额框内
        if ($(".stock_bottom").css("display") == "none") {
            $("#normalCash").focus();
        } else {
            $("#chargeCash").focus();
        }
        $("#saveBtn,#finishBtn").show();
        $("#saveA,#saveB").show();

        if (flag) {
            // 如果车牌不为空异步获取洗车信息
            if ($.trim($("#licenceNo").val())) {
                var ajaxUrl = "txn.do?method=getRepairOrderByVehicleNumber&type=ajax&btnType=washcar&vehicleNumber=" + encodeURIComponent($.trim($("#licenceNo").val()));
                App.Net.syncAjax({
                    url: ajaxUrl,
                    dataType: "json",
                    success: function(json) {
                        initCustomerVehicleAndOrder(json);
                    }
                });
            }
        } else {
            new ajaxForInvoiceUtil().ajaxToGetWashTimes($("#customerId").val());
        }
    }

    var count = getServiceTotal();
    $("#totalSpan").text(count);
    $("#total").val(count);
    //如果还没结算过，实收=总计
    if ($("#settledAmountHid").val() <= 0) {
        $("#settledAmount").val(count);
    }
    $("#debt").val(($("#total").val() * 1).toFixed(2) - ($("#settledAmount").val() * 1).toFixed(2));
}


function validateDate() {
    var myDate = G.Date.getCurrentFormatDate();
    if ($.trim($("#byId").val())) {
        if (myDate > $("#byId").val()) {
            alert("保养日期请选择今天及以后的日期。");
            return false;
        }
    }
    if ($.trim($("#bxId").val())) {
        if (myDate > $("#bxId").val()) {
            alert("保险日期请选择今天及以后的日期。");
            return false;
        }
    }
    if ($.trim($("#ycId").val())) {
        if (myDate > $("#ycId").val()) {
            alert("验车请选择今天及以后的日期。");
            return false;
        }
    }
    return true;
}

function disableOrderElement(orderAttrs) {
    var disableOrderAttrs = function(idName) {
        if ($.trim($("#" + idName).val())) {
            $("#" + idName).attr("disabled", true);
        }
    }
    for (var i = 0, len = orderAttrs.length; i < len; i++) {
        disableOrderAttrs(orderAttrs[i]);
    }
}

function enableOrderElement(orderAttrs) {
    var disableOrderAttrs = function(idName) {
        var val = $.trim($("#" + idName).val())
        val && val != '' && $("#" + idName).removeAttr("disabled");
    }
    for (var i = 0, len = orderAttrs.length; i < len; i++) {
        disableOrderAttrs(orderAttrs[i]);
    }
}

/**
 * 有ID就为false
 */

function judgeItemExsit() {
    var flag = false;
    $(".item input[id$='.id']").each(function() {
        if ($(this).val()) {
            flag = true;
            return false;
        }
    });
    if (!flag) {
        $(".item1 input[id$='.id']").each(function() {
            if ($(this).val()) {
                flag = true;
                return false;
            }
        });
    }
    return flag;
}

function validateOrderInfo() {
    if (!isEmptyItem() && !checkStorehouseSelected()) {
        return;
    }
    if (!App.Validator.stringIsLicensePlateNumber($('#licenceNo').val().replace(/\s|\-/g, ""))) {
        alert("输入的车牌号码不符合规范，请检查！");
        return false;
    }
    if (isEmptyItem() && isEmptyService()) {
        alert("施工单和材料单均未填写");
        return false;
    }
    if (isMaterialSame()) {
        alert('材料单有重复项目，请修改或删除。');
        return false;
    }
    if (isRepairSame()) {
        alert('施工单有重复项目，请修改或删除。');
        return false;
    }
    if (isCommodityCodeSame("item1")) {
        alert('材料单商品编码有重复内容，请修改或删除。');
        return false;
    }
    if (validateDate() == false) {
        return false;
    }
    if (checkRepairPickingSwitchOn()) {
        if (!isEmptyItem() && !isReservedEnough()) {
            alert("您有商品未出库，无法结算，请先出库。");
            return false;
        }
    } else if (!isEmptyItem() && isLack() == "true") {
        if (!App.Permission.Version.StoreHouse) {
            alert("库存不足，无法结算。");
            return false;
        }
    } else if (!isEmptyItem() && isnew() == "true") {
        alert("库存不足，无法结算。");
        return false;
    }

    //        reMoveAttrs();
    $("#fuelNumber").removeAttrs("disabled");
    $("#table_task :text").rules("remove");
    $("#table_productNo_2 :text").rules("remove");
    flag = 1;

    var regPositiveNumber = /^\d$|^[1-9]\d+$|^\d\.\d*[1-9]$|^[1-9]\d+\.\d*[1-9]$/g;
    var regPositiveInt = /^\d$|^[1-9]\d+$/g;

    var licenceNo = document.getElementById("licenceNo").value;
    var customer = document.getElementById("customerName").value;
    if (licenceNo == "") {
        alert("请输入车牌号!");
        document.getElementById("licenceNo").focus();
        return false;
    }
    if (licenceNo != "" && customer == "") {
        document.getElementById("customerName").value = licenceNo;
    }

    //加入手机的判断
    if ($.trim($("#mobile").val()) != "") {
        var mobile = document.getElementById("mobile");
        var telephone = document.getElementById("landLine");
        var message = check.inputMobile(mobile, telephone);
        if (message == "yes" || message == "success") {
            return false;
        }
    }
    //加入座机的判断
    if ($.trim($("#landLine").val()) != "") {
        var mobile = document.getElementById("mobile");
        var telephone = document.getElementById("landLine");
        var message = check.inputTelephone(telephone, mobile);
        if (message == "yes" || message == "success") {
            return false;
        }
    }
    //验证出厂时间格式
    if (endDateStrValidate($("#startDateStr").val()) == true) {
        alert("请输入正确的进厂时间！");
        return false;
    }
    if ($("#endDateStr").val() && $("#startDateStr").val() > $("#endDateStr").val()) {
        alert("预约出厂时间不能早于进厂时间，请修改!");
        return false;
    }


    //实收验证
    if ($.trim($("#settledAmount").val()) != "" && !App.Validator.stringIsPrice(App.StringFilter.priceFilter($.trim($("#settledAmount").val())))) {
        alert("实收只能输入0~9和小数点（.），请重新修改！");
        return false;
    }
    //欠款验证
    if ($.trim($("#debt").val()) != "" && !App.Validator.stringIsPrice(App.StringFilter.priceFilter($.trim($("#debt").val())))) {
        alert("欠款只能输入0~9和小数点（.），请重新修改！");
        this.focus();
        return false;
    }
    //判断实收和总计
    if (parseFloat($("#settledAmount").val()) > parseFloat($("#total").val())) {
        alert("实收大于总计，请重新输入");
        $("#settledAmount").val($("#total").val());
        $("#debt").val(0);
        $("#settledAmount").focus();
        return false;
    }
    $(".serviceTotal").each(function() {
        //验证输入的是正数
        if ($.trim($(this).val()) != "" && !App.Validator.stringIsPrice(App.StringFilter.priceFilter($.trim($(this).val())))) {
            flag = 0;
            alert("工时费请输入正确的价格！");
            return false;
        }
    });

    var result = true;
    //验证施工单内容，如果工时费或者备注有填写，就内容必填                    #table_task input[name$='.service'],#table_task input[name$='.total'],#table_task input[name$='.memo']
    if (!isEmptyService()) {
        $("#table_task input[name$='.service']").each(function() {
            if ($.trim($(this).val()) == '' && ($("#table_task input[name$='.total']").eq($(this).index("#table_task input[name$='.service']")).val() > 0 || $("#table_task input[name$='.workers']").eq($(this).index("#table_task input[name$='.service']")).val() != '')) {
                alert("第" + ($(this).index("#table_task input[name$='.service']") + 1) + "行请输入施工内容");
                result = false;
            }
        });
    }

    if (result == false) {
        return false;
    }
    //判断如果欠款，是否设置了还款时间
    //  if ($("#debt").val() > 0 && $("#isMakeTime").val() == 0) {
    //      alert("还款时间未设置，不能结算！");
    //      return false;
    //  }
    //验证数量不为空
    var amountMsg = "";
    var unitMsg = "";
    if (!isEmptyItem()) {
        $(".itemAmount").each(function() {
            if ($.trim($(this).val()) * 1 == 0) {
                amountMsg = amountMsg + "\n第" + ($(this).index(".itemAmount") + 1) + "行商品数量为0，此行内容无意义，请补充或删除";
                flag = 0;
            }

        });
        if (amountMsg != "") {
            alert(amountMsg);
            return false;
        }

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
            return false;
        }
    }


    var validateItemPrice = false;

    //验证价格为0
    $(".itemPrice").each(function() {
        //验证输入的是正数
        if (!regPositiveNumber.test($.trim($(this).val()))) {
            validateItemPrice = 1;
            return false;
        } else if (parseFloat($.trim($(this).val())) * 1 == 0) {
            validateItemPrice = 1;
            return false;
        }
    });
    if (!isEmptyItem()) {
        if (validateItemPrice) {
            var str = "";
            var altStr = ""
            $(".itemPrice").each(function() {
                if (!$(this).val()) {
                    $(this).val(0);
                }
                if (parseFloat($.trim($(this).val())) * 1 == 0) {
                    str = str + "\n第" + ($(this).index(".itemPrice") + 1) + "行商品销售价为0，是否确认销售";
                } else {
                    if (isNaN($.trim($(this).val()))) {
                        altStr = altStr + "\n第" + ($(this).index(".itemPrice") + 1) + "行商品销售价输入有误，请重新输入";
                    }
                }
            });
            if (altStr != "") {
                alert(altStr);
                return false;
            }
            if (str && isCheckPriceFlag) {
                if (!confirm(str)) {
                    return false;
                } else {
                    isCheckPriceFlag = false;
                }
            }
        }
    }


    if (!flag) {
        return false;
    }

    $("#div_brand").css({
        'display': 'none'
    });
    $("#div_works").css({
        'display': 'none'
    });
    if (debug) {
        if (!confirm("[finish] last:" + idPrefixLastModified)) {
            return false;
        }
    }

    return true;
}

//跳转到洗车单信息带入
function addParamToWashBeautyLink(url) {
    url = url + encodeURIComponent($("#licenceNo").val()) + "&brand=" + encodeURIComponent($("#brand").val())
        + "&model=" + encodeURIComponent($("#model").val()) + "&customer=" + encodeURIComponent($("#customerName").val())
        + "&customerId=" + encodeURIComponent($("#customerId").val()) + "&mobile=" + encodeURIComponent($("#mobile").val())
        + "&landLine=" + encodeURIComponent($("#landLine").val()) + "&vehicleContact=" + encodeURIComponent($("#vehicleContact").val())
        + "&vehicleMobile=" + encodeURIComponent($("#vehicleMobile").val());
    if (openNewOrderPage()) {
        window.open(url, "_blank");
    } else {
        window.location.href = url;
    }
}


var objEnterPhoneType, objEnterPhoneArrears, objEnterPhoneLicenceNo, objEnterPhoneDate, objEnterPhoneName, objEnterPhoneCustomerIdStr;

function enterPhoneSendSms(objEnterPhoneMobile) {
    sendSms(objEnterPhoneMobile, objEnterPhoneType, objEnterPhoneArrears, objEnterPhoneLicenceNo, objEnterPhoneDate, objEnterPhoneName, objEnterPhoneCustomerIdStr);
}

function sendSms(mobile, type, arrears, licenceNo, date, name, customerIdStr) { // type <!-- 0 保险  1 验车  2 生日-->
    if (mobile == null || $.trim(mobile) == "") {
        if (customerIdStr == "" || customerIdStr == null) {
            alert("请先查询客户信息！");
            return;
        }

        $("#enterPhoneCustomerId").val(customerIdStr);
        Mask.Login();
        $("#enterPhoneSetLocation").fadeIn("slow");
        objEnterPhoneType = type;
        objEnterPhoneArrears = arrears;
        objEnterPhoneLicenceNo = licenceNo;
        objEnterPhoneDate = date;
        objEnterPhoneName = name;
        objEnterPhoneCustomerIdStr = customerIdStr;
        return;
    }

    if (arrears * 1 == 0.0) {
        window.location = "sms.do?method=smswrite&customerId="+customerIdStr+"&mobile=" + mobile;
    } else {
        var dates = date.split("-");
        var month = dates[1];
        var day = dates[2];
        window.location = "sms.do?method=smswrite&mobile=" + $.trim(mobile) + "&type=" + type + "&arrears=" + arrears + "&licenceNo=" + encodeURI(licenceNo) + "&month=" + month + "&day=" + day + "&name=" + encodeURI(name) + "&money=" + arrears+"&customerId="+customerIdStr;
    }
}

function newOtherOrder(url) {
    if (openNewOrderPage()) {
        window.open(url, "_blank");
    } else {
        openOrAssign(url);
    }
}

function newRepairOrder() {
    if (openNewOrderPage()) {
        window.open($("#basePath").val() + "txn.do?method=getRepairOrderByVehicleNumber&task=maintain", "_blank");
    }
}

$(function() {

    $(".businessCategoryName")._dropdownlist("businessCategary");

    $("input[name$='.service']").live('click focus keyup',
        function(event) {

            var keyCode = event.keyCode || event.which,
                obj = event.target;
            if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
                return;
            }

            if ($(this).val() != $(obj).attr("hiddenValue")) {
                $("#" + this.id.split(".")[0] + "\\.serviceId").val("");
                $(this).removeAttr("hiddenValue");
            }

            var _selectedHandler = function(event, index, data, hook) {

                //fill the label into the input & fill the id into the hidden.
                $(hook).val(data.label);
                $(hook).parent().find('input[id$="serviceId"]').val(data.id);

                //
                var valflag = hook.value != $(this).html() ? true : false;
                new clearItemUtil().clearByFlag(hook, valflag);
                //$(hook).val($(this).attr("title"));
                var serviceId = data.id;
                var idPrefix = hook.id.split(".")[0];

                $("#" + idPrefix + "\\.consumeType").val("MONEY");
                var consumeTypeValue = $("#" + idPrefix + "\\.consumeType").val();

                $("#" + idPrefix + "\\.total")
                    .removeAttr("disabled")
                    .val(data.price)
                    .data("recommendedPrice", data.price);

                setTotal();
                var $last_tr = $("#table_task").find("tbody").find("tr:last");
                if ($last_tr.find("input[type='text']:first").val() && $last_tr.find("input[type='text']:first").val() != "") {
                    taskaddNewRow();
                    isShowAddButton();
                }

                var prefix = hook.id.split(".")[0];

//            tableUtil.tableStyle('#table_task', '.i_tabelBorder,.table_title');

                if (!G.Lang.isEmpty($(hook).val())) {
                    setTimeout(function() {
                        var searchcompleteMultiselect = App.Module.searchcompleteMultiselect;
                        searchOrderSuggestion(searchcompleteMultiselect, hook, "");
                    }, 100);
                }
            };

            droplistLite.show({
                event: event,
                isEditable: "true",
                isDeletable: "true",
                hiddenId: "serviceId",
                id: "id",
                name: "name",
                data: "txn.do?method=searchService",
                onsave: {
                    callback: function(event, index, data, hook) {

                        var _id = data.id,
                            _name = $.trim(data.label),
                            result = "";

                        if (_name.length <= 0) {
                            nsDialog.jAlert("施工内容不能为空");
                            return false;
                        }

                        App.Net.syncAjax({
                            url: "category.do?method=checkServiceNameRepeat",
                            dataType: "json",
                            data: {
                                serviceName: _name,
                                serviceId: _id
                            },
                            success: function(data) {
                                result = data.resu;
                            }
                        });

                        if (result == 'error') {
                            nsDialog.jAlert("服务名已存在");
                            return false;
                        }
                        if (result == 'inUse') {
                            nsDialog.jAlert("服务正在被进行中的单据使用，无法修改");
                            return false;
                        }

                        //Check if this item is disabled.
                        var checkResult,checkServiceId;
                        App.Net.syncAjax({
                            url: "category.do?method=checkServiceDisabled",
                            dataType: "json",
                            data: {
                                serviceName: _name
                            },
                            success: function(data) {
                                checkResult = data.resu;
                                checkServiceId = data.serviceId ? data.serviceId : "";
                            }
                        });

                        if (checkResult == "serviceDisabled") {
                            nsDialog.jConfirm("此项目以前被删除过，是否恢复？", null, function(_result) {
                                if (_result) {
                                    window.document.location = "category.do?method=updateServiceStatus&serviceId=" + checkServiceId;
                                }
                            });
                        } else {
                            //request save result & handler.
                            App.Net.syncPost({
                                url: "category.do?method=ajaxUpdateServiceName",
                                data: {
                                    serviceId: _id,
                                    serviceName: _name,
                                    now: new Date()
                                },
                                dataType: "json",
                                success: function(_result) {

                                    //success.
                                    if (_result.success) {
                                        data.label = _name;
                                        data.categoryName = _name;

                                        //get the event
                                        var $obj = $(obj);

                                        //遍历item 把此id的name 和hiddenvalue都变为最新的name
                                        // $obj.each(function() {
                                        //     var categoryId = _hiddenValue;

                                        //     if(categoryId == _id) {
                                        //         $(this).val(_name);
                                        //         $(this).attr("hiddenValue", _name);
                                        //     }
                                        // });

                                        nsDialog.jAlert(_result.msg, _result.title);
                                    }

                                    //fail.
                                    else if (!_result.success) {
                                        nsDialog.jAlert(_result.msg, _result.title, function() {
                                            data.label = data.categoryName;
                                        });
                                    }

                                    //exception.
                                    else {
                                        nsDialog.jAlert("数据异常！");
                                    }
                                },
                                //request error.
                                error: function() {
                                    nsDialog.jAlert("保存失败！");
                                }
                            });
                        }
                    }
                },
                ondelete: {
                    callback: function(event, index, data) {
                        var serviceId = data.id,
                            deleteFlag = true,
                            url = "category.do?method=checkServiceUsed";

                        App.Net.syncPost({
                            url: url,
                            data: {
                                serviceId: serviceId
                            },
                            dataType: "json",
                            success: function(data) {
                                if ("error" != data.resu) {
                                    deleteFlag = false;
                                } else {
                                    nsDialog.jAlert("此服务项目已被使用，不能删除！");
                                }
                            }
                        });

                        if (!deleteFlag) {

                            //get the request result.
                            var _result = App.Net.syncGet({
                                url: "category.do?method=ajaxDeleteService",
                                data: {
                                    serviceId: data.id,
                                    now: new Date()
                                },
                                dataType: "json"
                            });

                            //when failed and successed.
                            if (!_result.success) {
                                nsDialog.jAlert(_result.msg, _result.title);
                            } else if (_result.success) {
                                nsDialog.jAlert(_result.msg, _result.title);
                            } else {
                                nsDialog.jAlert("数据异常！");
                            }
                        }
                    }
                },
                afterSelected: function(event, index, data, hook) {
                    _selectedHandler(event, index, data, hook);
                }
            });
        }).live("blur", function(e) {               //手动输入，或下拉后，补全营业分类
            //         G.debug("change");
            //        $("#div_brand").css({'display':'none'});

            var serviceID = this.id;
            timeOutId = setTimeout(function() {
                if (!document.getElementById(serviceID)) {        //被删除
                    return;
                }
                var serviceName = document.getElementById(serviceID).value;
                serviceName = serviceName.replace(/(^\s*)|(\s*$)/g, "");
                if (serviceName == null || serviceName == "") return;
                $("#" + serviceID.split(".")[0] + "\\.total").val(0);

                var ajaxUrl = "txn.do?method=getServiceByServiceName&serviceName=" + encodeURI(serviceName);

                App.Net.syncAjax({
                    url: ajaxUrl,
                    dataType: "json",
                    success: function(json) {
                        var prefix = serviceID.split(".")[0];
                        if (json && json.length > 0) {
                            $("#" + serviceID.split(".")[0] + "\\.serviceId").val(json[0].idStr);
                            $("#" + serviceID.split(".")[0] + "\\.serviceHistoryId").val("");
                            $("#" + serviceID.split(".")[0] + "\\.total").val(json[0].price);

                            $("#" + prefix + "\\.standardHours").val(json[0].standardHours != null ? json[0].standardHours : "");
                            $("#" + prefix + "\\.standardUnitPrice").val(json[0].standardUnitPrice != null ? json[0].standardUnitPrice : "");
                            $("#" + prefix + "\\.actualHours").val(json[0].standardHours != null ? json[0].standardHours : "");

                            App.Net.syncPost({
                                url: "category.do?method=getCategoryByServiceId",
                                data: {
                                    "serviceId": json[0].idStr,
                                    "now": new Date()
                                },
                                dataType: "json",
                                success: function(jsonObject) {
                                    if (null != jsonObject.data) {
                                        var id = jsonObject.data.idStr;
                                        var name = jsonObject.data.categoryName;
                                        id = null == id ? "" : id;
                                        name = null == name ? "" : name;
                                        $("#" + prefix + "\\.businessCategoryId").val(id);
                                        $("#" + prefix + "\\.businessCategoryName").val(name);
                                        $("#" + prefix + "\\.businessCategoryName").attr("hiddenValue", name);
                                        if (name == "洗车" || name == "美容") {
                                            $("#" + prefix + "\\.businessCategoryName").attr("disabled", "disabled");
                                        } else {
                                            $("#" + prefix + "\\.businessCategoryName").removeAttr("disabled");
                                        }
                                    } else {
                                        $("#" + prefix + "\\.businessCategoryId").val("");
                                        $("#" + prefix + "\\.businessCategoryName").val("");
                                        $("#" + prefix + "\\.businessCategoryName").removeAttr("hiddenValue");
                                        $("#" + prefix + "\\.businessCategoryName").removeAttr("disabled");
                                    }
                                }
                            });
                        } else {
                            $("#" + serviceID.split(".")[0] + "\\.serviceId").val("");
                            $("#" + serviceID.split(".")[0] + "\\.serviceHistoryId").val("");
                            $("#" + prefix + "\\.businessCategoryId").val("");
                            $("#" + prefix + "\\.businessCategoryName").val("");
                            $("#" + prefix + "\\.businessCategoryName").removeAttr("hiddenValue");
                            $("#" + prefix + "\\.businessCategoryName").removeAttr("disabled");
                        }

                    }
                });

                setTotal();
            }, 200);
        });

    $(".serviceCategory").each(function() {
        var name = $(this).val();

        if (name == "洗车" || name == "美容") {
            $(this).attr("disabled", "disabled");
        }
    });

    $("#duizhan").bind("click", function(e) {
        e.preventDefault();
        toCreateStatementOrder($("#customerId").val(), "CUSTOMER_STATEMENT_ACCOUNT");
    });
    $("#createQualified").live("click", function() {
        if (!$("#id").val()) {
            nsDialog.jAlert("当前单据还未派单无法生成合格证，请先派单!");
            return;
        }
        var url = "txn.do?method=getQualifiedCredentials"
        App.Net.syncAjax({
            url:url,
            dataType:"json",
            data:{orderId:$("#id").val()},
            success: function(json) {
                var _$createQualifiedDiv = $("#createQualifiedDiv");
                var _$showQualifiedSpan = $("#showQualifiedSpan");
                _$createQualifiedDiv.empty();
                _$showQualifiedSpan.empty();
                if (null != json && json.no) {
                    _$showQualifiedSpan.append('<span class="blue_color">|</span>');
                    _$showQualifiedSpan.append('<a class="blue_color" id="createQualified"><img src="images/icon_right.png"/>合格证'+json.no+'</a>');

                    var url = "txn.do?method=createQualified&repairOrderId=" + $("#id").val();
                    window.open(url, "_blank");
                } else {
                    _$createQualifiedDiv.append('<input type="button" id="createQualified" class="qualified" value="" style="margin: 12px 0 0;" onfocus="this.blur();"/>');
                    _$createQualifiedDiv.append('<div style="width:100%; ">生成合格证</div>');

                    var url = "txn.do?method=createQualified&repairOrderId=" + $("#id").val();
                    window.open(url, "_self");
                }
            }
        });
    });

});

function validateOtherIncomeInfo() {

    //自动删除最后的空白行
    var $last_tr = $("#table_otherIncome").find("tbody").find("tr:last");
    while ($last_tr.index() > 2  && checkEmptyRow2($last_tr)) {
        $last_tr.find(".opera1").click();
        $last_tr = $("#table_otherIncome").find("tbody").find("tr:last");
    }

    var msg = "";
    $(".otherIncomeKindName").each(function(index) {
        var name = $(this).val();
        var prefix = $(this)[0].id.split(".")[0];
        var price = $("#" + prefix + "\\.price").val();
        index++;
        if ($(".otherIncomeKindName").size() == 1) {
            if (!name) {
                $last_tr.find("[input[id$='.deletebutton']").click();
                $last_tr = $("#table_otherIncome").find("tbody").find("tr:last");
            } else {
                if (!price) {
                    msg += "第" + index + "行缺少费用金额\n";
                } else {
                    if (!App.Validator.stringIsPrice(price)) {
                        msg += "第" + index + "行费用金额格式不对\n";
                    }
                }
            }
        } else {
            if (!name) {
                msg += "第" + index + "行缺少费用名称";
            }

            if (!price) {
                if (!name) {
                    msg += ",缺少费用金额\n"
                } else {
                    msg += "第" + index + "行缺少费用金额\n";
                }
            } else {
                if (!App.Validator.stringIsPrice(price)) {
                    if (!name) {
                        msg += ",费用金额格式不对\n"
                    } else {
                        msg += "第" + index + "行费用金额格式不对\n";
                    }
                }
            }
        }
    });
    return msg;
}

function checkEmptyRow2($tr) {
    var propertys = ["name"];
    var itemInfo = "";
    for (var i = 0, len = propertys.length; i < len; i++) {
        itemInfo += $tr.find("input[id$='." + propertys[i] + "']").val();
    }
    if (G.Lang.isEmpty(itemInfo)) {
        return true;
    }
    return false;
}

function initOtherIncomeItems(otherItems) {
    $("#table_otherIncome tr").detach(".item2");
    if (otherItems && otherItems.length > 0) {
        for (var i = 0, len = otherItems.length; i < len; i++) {
            var idPrefix = "otherIncomeItemDTOList" + i;
            var namePrefix = "otherIncomeItemDTOList[" + i + "]";
            var price = otherItems[i].price;
            price = (null == price ? "" : price);
            var idStr = (null == otherItems[i].idStr) ? "" : otherItems[i].idStr;

            var trSample = '<tr class="titBody_Bg item2 table-row-original">'
                + '<td><input id="' + idPrefix + '.name" maxLength="50" name="' + namePrefix + '.name" class="table_input otherIncomeKindName checkStringEmpty" value="' + otherItems[i].name + '" type="text">' + '</td>';


            trSample += '<td><input id="' + idPrefix + '.id" name="' + namePrefix + '.id" title="' + '" value="' + idStr + '"  type="hidden"/>';
            trSample += '<input id="' + idPrefix + '.templateIdStr" name="' + namePrefix + '.templateIdStr" value="' + otherItems[i].templateIdStr + '" type="hidden"/>';
            trSample += '<input id="' + idPrefix + '.templateId" name="' + namePrefix + '.templateId" value="' + otherItems[i].templateIdStr + '" type="hidden"/>';
            if (otherItems[i].name == "材料管理费") {

                trSample += '<input id="' + idPrefix + '.price" name="' + namePrefix + '.price" class="table_input otherIncomePrice checkStringEmpty" type="hidden" value="' + price + '"/>';

                trSample += '<input id="' + idPrefix + '.otherIncomeRate" name="' + namePrefix + '.otherIncomeRate" value="' + otherItems[i].otherIncomeRate + '" type="hidden"/>'
                    + '<input id="' + idPrefix + '.otherIncomeCalculateWay" name="' + namePrefix + '.otherIncomeCalculateWay"  type="hidden" value="' + otherItems[i].otherIncomeCalculateWay + '"/>';

                if (otherItems[i].otherIncomeCalculateWay == 'RATIO') {
                    trSample += '<input class="otherIncomePriceByRate" id="' + idPrefix + '.otherIncomePriceByRate" type="radio" checked ="checked" name="' + namePrefix + '.priceCheckBox" style="float:left; margin-right:4px;">';
                    trSample += '<label></label>按材料费比率计算';
                    trSample += '<input class="txt otherIncomePriceRate txt_color" style="width:70px;" value="' + otherItems[i].otherIncomeRate + '" id="' + idPrefix + '.otherIncomePriceRate">';
                    trSample += '&nbsp;%&nbsp;&nbsp;<span id="' + idPrefix + '.otherIncomePriceSpan">' + otherItems[i].otherIncomePriceSpan + '</span>元';
                    trSample += '<div class="clear i_height"></div>';
                    trSample += '<input class="otherIncomePriceByAmount" id="' + idPrefix + '.otherIncomePriceByAmount" type="radio" name="' + namePrefix + '.priceCheckBox" style="float:left; margin-right:4px;">';
                    trSample += '<label></label>按固定金额计算&nbsp;&nbsp';
                    trSample += '<input class="txt otherIncomePriceText txt_color" id="' + idPrefix + '.otherIncomePriceText" value="请输入金额" style="width:100px;color:#9a9a9a;">元';
                } else {
                    trSample += '<input class="otherIncomePriceByRate" id="' + idPrefix + '.otherIncomePriceByRate" type="radio" name="' + namePrefix + '.priceCheckBox" style="float:left; margin-right:4px;">';
                    trSample += '<label></label>按材料费比率计算';
                    trSample += '<input class="txt otherIncomePriceRate txt_color" style="width:70px;color:#9a9a9a;" value="请输入比率" id="' + idPrefix + '.otherIncomePriceRate">';
                    trSample += '&nbsp;%&nbsp;&nbsp;<span id="' + idPrefix + '.otherIncomePriceSpan">0</span>元';
                    trSample += '<div class="clear i_height"></div>';
                    trSample += '<input class="otherIncomePriceByAmount" value="' + otherItems[i].price + '" id="' + idPrefix + '.otherIncomePriceByAmount" type="radio"  checked ="checked" name="' + namePrefix + '.priceCheckBox" style="float:left; margin-right:4px;">';
                    trSample += '<label></label>按固定金额计算&nbsp;&nbsp';
                    trSample += '<input class="txt otherIncomePriceText txt_color" value="' + otherItems[i].price + '" id="' + idPrefix + '.otherIncomePriceText" style="width:100px;">元';
                }
            }else{
                trSample += '<input id="' + idPrefix + '.price" name="' + namePrefix + '.price" class="table_input otherIncomePrice checkStringEmpty" type="text" value="' + price + '" maxlength="8"/>';
            }
            trSample += '</td>';


            if (otherItems[i].calculateCostPrice == "TRUE") {
                trSample += '<td><input id="' + idPrefix + '.otherIncomeCostPriceCheckbox" name="' + namePrefix + '.checkbox" type="checkbox" checked ="checked"' +
                    'type="hidden" class="otherIncomeCostPriceCheckbox" style="float:left; margin-right:4px; margin-top:3px;">' + '<label style="float:left; margin-right:4px;"/></label>计入成本';
                trSample += '<span id="' + idPrefix + '.otherIncomeSpan">';
                trSample += '<input id="' + idPrefix + '.otherIncomeCostPrice" class="table_input otherIncomeCostPrice checkStringEmpty" style="width:70px;"' +
                    'name="' + namePrefix + '.otherIncomeCostPrice" value="' + otherItems[i].otherIncomeCostPrice + '"></span>';


            } else {
                trSample += '<td><input id="' + idPrefix + '.otherIncomeCostPriceCheckbox" name="' + namePrefix + '.checkbox"  type="checkbox"' +
                    'type="hidden" class="otherIncomeCostPriceCheckbox" style="float:left; margin-right:4px; margin-top:3px;">' + '<label style="float:left; margin-right:4px;"/></label>计入成本';
                trSample += '<span style="display:none;" id="' + idPrefix + '.otherIncomeSpan">';
                trSample += '<input id="' + idPrefix + '.otherIncomeCostPrice" class="table_input otherIncomeCostPrice checkStringEmpty" style="width:70px;"' +
                    'name="' + namePrefix + '.otherIncomeCostPrice" value="' + otherItems[i].otherIncomeCostPrice + '"></span>';
            }

            trSample += '<td>' + '<input id="' + idPrefix + '.memo" maxLength="100" name="' + namePrefix + '.memo"  value="' + otherItems[i].memo + '" class="table_input memo checkStringEmpty" type="text" /></td>'
                + '<td style="border-right: medium none;"><a style="margin-left: 5px;" class="opera1" id="'+idPrefix + '.deletebutton" name="'+namePrefix + '.deletebutton">删除</a></td>' + '</tr>';
            $("#table_otherIncome tbody").append(trSample);

        }
    } else {
        trCount3 = 0;
        otherIncomeAddNewRow();
    }
    isShowAddButton3();
}


function repealOrder(orderId) {
    if (App.Permission.Version.StoreHouse) {
        if (!checkRepairPickingSwitchOn()) {
            if (!validatorStoreHouseOrderRepeal($("#_toStorehouseId").val(), getOrderType(), orderId)) {
                $("#nullifyBtn").removeAttr("status");
                return;
            }
        }
    }
    $(this).attr("status", "checkDeleteProduct");
    if (validateRepealStrikeSettled("REPAIR", orderId)) {
        $("#nullifyBtn").removeAttr("status");
        return;
    }
    var ajaxUrl = "txn.do?method=validatorDeletedProductOrderRepeal";
    var ajaxData = {
        orderId: orderId,
        orderType: getOrderType()
    };
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
        $("#nullifyBtn").removeAttr("status");
        var url = "txn.do?method=repairOrderRepeal&repairOrderId=" + orderId;
        if (App.Permission.Version.StoreHouse) {
            url += "&toStorehouseId=" + $("#_toStorehouseId").val();
        }
        if (json.success) {
            if (confirm("友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！")) {
                window.location = url;
            } else {
                if (App.Permission.Version.StoreHouse) {
                    $("#_toStorehouseId").val("");
                }
            }
        } else if (!json.success && json.operation == "confirm_deleted_product") {
            if (confirm(json.msg)) {
                window.location = url;
            } else {
                if (App.Permission.Version.StoreHouse) {
                    $("#_toStorehouseId").val("");
                }
            }
        } else if (!json.success) {
            if (App.Permission.Version.StoreHouse) {
                $("#_toStorehouseId").val("");
            }
            alert(json.msg);
        }
    }, function(json) {
        $("#nullifyBtn").removeAttr("status");
        alert(json.msg);
    });
}

//维修领料开关打开之后派单之后不允许修改仓库
function checkAndUpdateStorehouseStatus() {
    if (!$("#storehouseId")) {
        return;
    }
    if ($("#id").val() && checkRepairPickingSwitchOn()
        && $("#repairPickingId").val()) {
        $("#storehouseId").attr("disabled", "disabled");
    } else {
        $("#storehouseId").removeAttr("disabled");
    }
}

function initDuiZhanInfo() {
    var receivableValue = parseFloat($("#receivable").html()),
        payableValue = parseFloat($("#payable").html()),
        isReceivalbeEmpty = G.isEmpty(receivableValue),
        isPayableEmpty = G.isEmpty(payableValue);

    $("#receivableDiv").css("display", isReceivalbeEmpty ? "none" : "inline");
    $("#payableDiv").css("display", isPayableEmpty ? "none" : "inline");

    $("#duizhan").toggle(!isReceivalbeEmpty || !isPayableEmpty);
}

$(document).ready(function(){
    $("#showAppointOrderBtn").live("click",function(e){
        e.preventDefault();
        if(!G.isEmpty($("#appointOrderId").val())){
            window.location.href="appoint.do?method=showAppointOrderDetail&appointOrderId=" + $("#appointOrderId").val();
        }
    });

    $("#showProblemDescriptionBtn").bind("click",function(e){
        e.preventDefault();
        $(this).closest("div").hide();
        $("#problemdescriptionDiv").show();
        $("#hideProblemDescriptionBtn").closest("div").show();
    });
    $("#hideProblemDescriptionBtn").bind("click",function(e){
        e.preventDefault();
        $("#problemdescriptionDiv").hide();
        $(this).closest("div").hide();
        $("#showProblemDescriptionBtn").closest("div").show();
    });
    $("#showCustomerInfoBtn").bind("click",function(e){
        e.preventDefault();
        $(this).closest("div").hide();
        $(".J_CustomerInfoDiv").show();
        $("#hideCustomerInfoBtn").closest("div").show();
    });
    $("#hideCustomerInfoBtn").bind("click",function(e){
        e.preventDefault();
        $(".J_CustomerInfoDiv").hide();
        $(this).closest("div").hide();
        $("#showCustomerInfoBtn").closest("div").show();
    });
    $("#showCustomerVehicleHandoverInfoBtn").bind("click",function(e){
        e.preventDefault();
        $(this).closest("div").hide();
        $(".J_CustomerVehicleHandoverInfo").show();
        $("#hideCustomerVehicleHandoverInfoBtn").closest("div").show();
    });
    $("#hideCustomerVehicleHandoverInfoBtn").bind("click",function(e){
        e.preventDefault();
        $(".J_CustomerVehicleHandoverInfo").hide();
        $(this).closest("div").hide();
        $("#showCustomerVehicleHandoverInfoBtn").closest("div").show();
    });
    if(G.isNotEmpty($("#customerMemberInfoImg"))){
        $("#customerMemberInfoImg").bind('mouseover',function (e) {
            if (G.isNotEmpty($("#memberNumber").text())) {
                $("#customerMemberInfoDiv").show();
            }
        }).bind('mouseout', function () {
                $("#customerMemberInfoDiv").hide();
            });
        $('.icon_close',$("#customerMemberInfoDiv")).click(function(){
            $("#customerMemberInfoDiv").hide();
        });
    }

    $("#deleteVehicleHandoverBtn").live("click", function () {
        $("#vehicleHandover").val("");
        $("#vehicleHandoverId").val("");
        $("#deleteVehicleHandoverBtn").hide();
    });

    $("#vehicleHandoverDiv").mouseenter(function () {
        if ($("#vehicleHandover").val() && !isDisable()) {
            $("#deleteVehicleHandoverBtn").show();
        }
    });

    $("#vehicleHandoverDiv").mouseleave(function () {
        $("#deleteVehicleHandoverBtn").hide();
    });
    $("#vehicleHandover").live("click focus keyup", function (event) {
        event = event || event.which;
        var keyCode = event.keyCode;

        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }

        droplistLite.show({
            event: event,
            hiddenId:"vehicleHandoverId",
            id: "id",
            name: "name",
            data: "member.do?method=getSaleMans"
        });
    });
});

/**
 * 初始化施工单和草稿单列表
 * @param json
 */
function initReapirAndDraftOrderTable(json) {
    var draftOrderJson = json[0].draftOrderData;
    if(draftOrderJson && draftOrderJson.length > 0) {
        $("#repair_draft_table tr:not(:first)").remove();
        for(var i = 0; i < draftOrderJson.length; i++) {
            var material = draftOrderJson[i].material == null ? "" : draftOrderJson[i].material;
            var serviceContent = draftOrderJson[i].serviceContent == null ? "" : draftOrderJson[i].serviceContent;
            var orderTypeStr = '';
            if(draftOrderJson[i].orderType == 'DRAFT') {
                orderTypeStr = '草稿单';
            } else {
                orderTypeStr = '施工单';
            }
            var tr = "<tr class='table-row-original' draftOrderId='" + draftOrderJson[i].idStr + "'><td></td>";
            tr += ("<td style='border-left:none;' class='first-padding'>" + (i + 1) + "</td>");
            tr += ("<td>" + (draftOrderJson[i].receiptNo) + "</td>");
            tr += ("<td>" + (draftOrderJson[i].saveTimeStr == null ? "" : draftOrderJson[i].saveTimeStr) + "</td>");
            tr += ("<td>" + (draftOrderJson[i].customerName == null ? "" : draftOrderJson[i].customerName) + "</td>");
            tr += ("<td>" + (draftOrderJson[i].vechicle == null ? "" : draftOrderJson[i].vechicle) + "</td>");
            tr += ('<td title= \'' + serviceContent + '\'>' + (draftOrderJson[i].serviceContentStr == null ? "" : draftOrderJson[i].serviceContentStr) + "</td>");
            tr += ('<td title= \'' + material + '\' class="last-padding">' + (draftOrderJson[i].materialStr == null ? "" : draftOrderJson[i].materialStr) + "</td>");
            tr += ("<td>" + (orderTypeStr) + "</td>");
            tr += "<td></td></tr>";
            var $tr = $(tr);
            $("#repair_draft_table").append($tr);
            if(draftOrderJson[i].orderType == 'DRAFT') {
                $tr.dblclick(function(event) {
                    loadRepairOrDraftOrder($(this).attr("draftOrderId"),'DRAFT');
                });

            } else {
                $tr.dblclick(function(event) {
                    loadRepairOrDraftOrder($(this).attr("draftOrderId"),'REPAIR');
                });
            }

        }
    }
    draftTableStyleAdjuct();
}

function loadRepairOrDraftOrder(orderIdStr,type) {
    if(type == 'DRAFT') {
        if($("#draftOrderIdStr").val() == orderIdStr){
            modifyDraftOrder(orderIdStr);
        } else {
            if(openNewOrderPage()) {
                window.open("draft.do?method=getOrderByDraftOrderId&flag=" + $("#draft_table").attr("flag") + "&newOpen=true&draftOrderIdStr=" + orderIdStr, "_blank");
            } else {
                modifyDraftOrder(orderIdStr);
            }
        }
    } else {
        window.open("txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + orderIdStr, "_blank");
    }


}

 function doAccount(){
            if ($("#customerId").val()) {
                var r = App.Net.syncGet({
                    async: false,
                    url: "customer.do?method=checkCustomerStatus",
                    data: {
                        customerId: $("#customerId").val(),
                        now: new Date()
                    },
                    dataType: "json"
                });

                if (!r.success) {
                    alert("此客户已被删除或合并，不能做单，请更改客户！");
                    return;
                }
            }
            accountTypeChange();
        }
