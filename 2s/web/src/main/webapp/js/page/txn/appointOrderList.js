;
$(function () {

        $("#customerSearchWord")
            .bind("click focus keyup", function (event) {
                if (G.contains(G.keyNameFromEvent(event), ["up", "down", "left", "right"])) {
                    return;
                }
                customerNameOrMobileSuggestion($(this));
            }).bind("focus",function(){
                $(this).attr("lastVal",$(this).val());
            }).bind("blur",function(){
                if($(this).val() != $(this).attr("lastVal")){
                    $("#customerIds").val("");
                }
            });


    $("#createTimeStartStr,#createTimeEndStr")
        .attr("readOnly","readOnly")
        .datepicker({
            "numberOfMonths": 1,
            "changeYear": true,
            "showHour": false,
            "showMinute": false,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": "",
            "showButtonPanel": true
        }).blur(function () {
            var startDate = $("#createTimeStartStr").val();
            var endDate = $("#createTimeEndStr").val();
            if (startDate == "" || endDate == "") return;
            if (startDate > endDate) {
                $("#createTimeEndStr").val(startDate);
                $("#createTimeStartStr").val(endDate);
            }
        }).click(function () {
            $(this).blur();
        }).change(function () {
            var startDate = $("#createTimeStartStr").val();
            var endDate = $("#createTimeEndStr").val();
            if (endDate == "" || startDate == "") {
                return;
            }
            if (startDate > endDate) {
                $("#createTimeEndStr").val(startDate);
                $("#createTimeStartStr").val(endDate);
            }
        });

    $("#appointTimeStartStr,#appointTimeEndStr")
           .attr("readOnly","readOnly")
           .datepicker({
               "numberOfMonths": 1,
               "changeYear": true,
               "showHour": false,
               "showMinute": false,
               "changeMonth": true,
               "yearRange": "c-100:c+100",
               "yearSuffix": "",
               "showButtonPanel": true
           }).blur(function () {
               var startDate = $("#appointTimeStartStr").val();
               var endDate = $("#appointTimeEndStr").val();
               if (startDate == "" || endDate == "") return;
               if (startDate > endDate) {
                   $("#createTimeEndStr").val(startDate);
                   $("#appointTimeStartStr").val(endDate);
               }
           }).click(function () {
               $(this).blur();
           }).change(function () {
               var startDate = $("#appointTimeStartStr").val();
               var endDate = $("#appointTimeEndStr").val();
               if (endDate == "" || startDate == "") {
                   return;
               }
               if (startDate > endDate) {
                   $("#appointTimeEndStr").val(startDate);
                   $("#appointTimeStartStr").val(endDate);
               }
           });

    $("#allServiceCategory").change(function(){
        if($(this).attr("checked")){
         $("[name='serviceCategoryIds']").attr("checked",true);
        }else{
            $("[name='serviceCategoryIds']").attr("checked",false);
        }
    });

    $("[name='serviceCategoryIds']").change(function(){
        var $serviceCategoryIdsArray = $("[name='serviceCategoryIds']");
        var $serviceCategoryIdsArrayChecked = $("[name='serviceCategoryIds']:checked");
        if ($serviceCategoryIdsArrayChecked.size() == 0) {
            $("#allServiceCategory").attr("checked", false);
        } else if ($serviceCategoryIdsArrayChecked.size() == $serviceCategoryIdsArray.size()) {
            $("#allServiceCategory").attr("checked", true);
        }

    });
    $("#searchAppointOrderBtn").click(function(){
        searchAppointOrder();
    });
    $("#clearSearchCondition").click(function(e){
       $("#appointOrderListForm").resetForm();
        if(!$(e.target).attr("myCustomerDetail")){
            $("#customerIds").val("");
        }
    });

    $(".J_add_newOrder").click(function(){
        APP_BCGOGO.Net.syncPost({
            url: "appoint.do?method=validateCreateAppointOrder",
            dataType: "json",
            success: function (result) {
                if (result && result.success) {
                    window.location.href = "appoint.do?method=createAppointOrder";
                } else if (result && !result.success) {
                    nsDialog.jConfirm(result.msg,null,function(value){
                       if(value){
                           window.location.href = result.data;
                       }
                    });
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
            }
        });

    });

    $(".J_to_appointOrder").live("click", function () {
        var orderPrefix = $(this).attr("id").split(".")[0];
        var orderId = $("#" + orderPrefix + "\\.id").val();
        if (!G.Lang.isEmpty(orderId)) {
            window.location.href = "appoint.do?method=showAppointOrderDetail&appointOrderId=" + orderId;
        }
    });

    $(".J_acceptOrder").live("click",function(){
        var orderPrefix = $(this).attr("id").split(".")[0];
        var orderId = $("#" + orderPrefix + "\\.id").val();
        if (G.Lang.isEmpty(orderId)) {
            return;
        }
        var $acceptBtn = $(this);
        if ($acceptBtn.attr("lock")) {
            return;
        }
        $acceptBtn.attr("lock", true);

        APP_BCGOGO.Net.syncPost({
            url: "appoint.do?method=validateAcceptAppointOrder",
            dataType: "json",
            data: {
                id: orderId
            },
            success: function (result) {
                if (result && result.success) {
                    nsDialog.jConfirm("确定接受当前预约单？", null, function (value) {
                        if (value) {
                            window.location.href = "appoint.do?method=acceptAppointOrder&id=" + orderId;
                        } else {
                            $acceptBtn.removeAttr("lock");
                        }
                    })
                } else if (result && !result.success) {
                    nsDialog.jAlert(result.msg);
                    $acceptBtn.removeAttr("lock");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
                $acceptBtn.removeAttr("lock");
            }
        });
    });

    $(".J_refuseOrder").live("click", function () {
        var orderPrefix = $(this).attr("id").split(".")[0];
        var orderId = $("#" + orderPrefix + "\\.id").val();
        if (G.Lang.isEmpty(orderId)) {
            return;
        }

        var $refuseBtn = $(this);
        if ($refuseBtn.attr("lock")) {
            return;
        }
        $refuseBtn.attr("lock", true);
        $("#selectedToRefuseOrderId").val(orderId);
        APP_BCGOGO.Net.syncPost({
                   url: "appoint.do?method=validateRefuseAppointOrder",
                   dataType: "json",
                   data: {
                       id: orderId
                   },
                   success: function (result) {
                       if (result && result.success) {
                           var html = '';
                           $("#refuseDialog").dialog({
                               resizable: false,
                               title: "拒绝理由",
                               height: 200,
                               width: 445,
                               modal: true,
                               closeOnEscape: false,
                              close:function(){
                                  $refuseBtn.removeAttr("lock");
                                  $("#refuseMsg").val("");
                                  $("#selectedToRefuseOrderId").val("");
                              }
                           });
                       } else if (result && !result.success) {
                           nsDialog.jAlert(result.msg);
                           $refuseBtn.removeAttr("lock");
                       }
                   },
                   error: function () {
                       nsDialog.jAlert("网络异常！");
                       $refuseBtn.removeAttr("lock");
                   }
               });
    });

    $("#confirmRefuse").click(function () {
        var $confirmRefuse = $(this);
        if ($confirmRefuse.attr("lock")) {
            return;
        }
        $confirmRefuse.attr("lock", true);
        var orderId = $("#selectedToRefuseOrderId").val();
        if (G.Lang.isEmpty(orderId)) {
            nsDialog.jAlert("需要拒绝的单据不存在，请刷新页面重试！");
            $confirmRefuse.removeAttr("lock");
            return;
        }
        var refuseMsg = $("#refuseMsg").val();
        if (G.Lang.isEmpty(refuseMsg)) {
            nsDialog.jAlert("请填写拒绝理由！");
            $confirmRefuse.removeAttr("lock");
            return;
        }
        window.location.href = "appoint.do?method=refuseAppointOrder&id=" + orderId + "&refuseMsg=" + refuseMsg;
    });

    $("#cancelRefuse").click(function () {
        $("#refuseDialog").dialog("close");
    });

    $(".J_cancelOrder").live("click",function () {
        var orderPrefix = $(this).attr("id").split(".")[0];
        var orderId = $("#" + orderPrefix + "\\.id").val();
        if (G.Lang.isEmpty(orderId)) {
            return;
        }

        var $cancelBtn = $(this);
        if ($cancelBtn.attr("lock")) {
            return;
        }
        $cancelBtn.attr("lock", true);
        $("#selectedToCancelOrderId").val(orderId);

        APP_BCGOGO.Net.syncPost({
            url: "appoint.do?method=validateCancelAppointOrder",
            dataType: "json",
            data: {
                id: orderId
            },
            success: function (result) {
                if (result && result.success) {
                    $("#cancelDialog").dialog({
                        resizable: false,
                        title: "取消理由",
                        height: 200,
                        width: 445,
                        modal: true,
                        closeOnEscape: false,
                        close: function () {
                            $cancelBtn.removeAttr("lock");
                            $("#cancelMsg").val("");
                            $("#selectedToCancelOrderId").val("");
                        }
                    });
                } else if (result && !result.success) {
                    nsDialog.jAlert(result.msg);
                    $cancelBtn.removeAttr("lock");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
                $cancelBtn.removeAttr("lock");
            }
        });
    });

    $("#confirmCancel").click(function(){
        var $confirmCancel = $(this);
        if ($confirmCancel.attr("lock")) {
            return;
        }
        $confirmCancel.attr("lock", true);
        var orderId = $("#selectedToCancelOrderId").val();
        if(G.Lang.isEmpty(orderId)){
            nsDialog.jAlert("需要取消的单据不存在，请刷新页面重试！");
            $confirmCancel.removeAttr("lock");
             return;
        }
         var cancelMsg = $("#cancelMsg").val();
         if(G.Lang.isEmpty(cancelMsg)){
             nsDialog.jAlert("请填写取消理由！");
             $confirmCancel.removeAttr("lock");
             return;
         }
        window.location.href = "appoint.do?method=cancelAppointOrder&id=" + orderId + "&cancelMsg=" + cancelMsg;
    });

    $("#cancelCancel").click(function(){
        $("#cancelDialog").dialog("close");
    });

    $(".J_toRelatedOrder").live("click",function(){
        var orderPrefix = $(this).attr("id").split(".")[0];
        var appointOrderId =  $("#"+orderPrefix+"\\.id").val();
        if (!G.Lang.isEmpty(appointOrderId)) {
            APP_BCGOGO.Net.asyncAjax({
                url: "appoint.do?method=validateDraftOrderAndAppointOrderStatus",
                type: "POST",
                data: {appointOrderId: appointOrderId},
                cache: false,
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        if (result.data) {
                            var orderIdStr = result.data.orderIdStr == null ? "" : result.data.orderIdStr;
                            var orderType = result.data.orderType == null ? "" : result.data.orderType;
                            var appointOrderStatus = result.data.status;
                            if ('WASH_BEAUTY' == orderType) {
                                window.location.href = "washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" + orderIdStr;
                            } else if ('REPAIR' == orderType) {
                                if ('TO_DO_REPAIR' == appointOrderStatus) {
                                    window.location.href = "txn.do?method=getRepairOrderByDraftOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&draftOrderId=" + orderIdStr;
                                } else if ('HANDLED' == appointOrderStatus) {
                                    window.location.href = "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + orderIdStr;
                                }
                            } else if('REPAIR_DRAFT_ORDER' == orderType) {
                                window.location.href = "txn.do?method=getRepairOrderByDraftOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&draftOrderId=" + orderIdStr;
                            }
                        }
                    } else {
                        nsDialog.jAlert(result.msg);
                    }
                },
                error: function (result) {
                    nsDialog.jAlert(result.msg);
                }
            });
        }
    });

    $(".J_createOtherOrder").live("click", function () {
        var orderPrefix = $(this).attr("id").split(".")[0];
        var orderId = $("#" + orderPrefix + "\\.id").val();

        if (G.Lang.isEmpty(orderId)) {
            return;
        }
        var $createOtherOrder = $(this);
        if ($createOtherOrder.attr("lock")) {
            return;
        }
        $createOtherOrder.attr("lock", true);
        APP_BCGOGO.Net.syncPost({
            url: "appoint.do?method=validateCreateOtherOrder",
            dataType: "json",
            data: {
                id: orderId
            },
            success: function (result) {
                if (result && result.success) {
                    window.location.href = "appoint.do?method=createOtherOrder&id=" + orderId;
                } else if (result && !result.success) {
                    if (!G.Lang.isEmpty(result["operation"]) && result["operation"] == "CONFIRM") {
                        nsDialog.jConfirm(result["msg"], null, function (value) {
                            if (value) {
                                window.location.href = "txn.do?method=getRepairOrder&repairOrderId=" + result["data"];
                            }
                        });
                    } else {
                        nsDialog.jAlert(result.msg);
                    }
                    $createOtherOrder.removeAttr("lock");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
                $createOtherOrder.removeAttr("lock");
            }
        });
    });
});

function searchAppointOrder(){
  var data = {};
    data["customerSearchWord"] = G.trim($("#customerSearchWord").val());
    data["receiptNo"] = G.trim($("#receiptNo").val());
    data["appointWay"] = $("#appointWay").val();
    data["appointOrderStatus"] = $("#appointOrderStatus").val();
    data["createTimeStartStr"] = $("#createTimeStartStr").val();
    data["createTimeEndStr"] = $("#createTimeEndStr").val();
    data["appointTimeStartStr"] = $("#appointTimeStartStr").val();
    data["appointTimeEndStr"] = $("#appointTimeEndStr").val();
    data["vehicleNo"] = G.trim($("#vehicleNo").val());
    data["maxRows"] = $("#maxRows").val();

    if(!G.Lang.isEmpty($("#customerIds").val())){
        data["customerIds"] = $("#customerIds").val();
    }
    var serviceCategoryIdStr = "";
    $('[name="serviceCategoryIds"]:checked').each(function () {
        if (G.Lang.isEmpty(serviceCategoryIdStr)) {
            serviceCategoryIdStr = $(this).val();
        } else {
            serviceCategoryIdStr = serviceCategoryIdStr + "," + $(this).val();
        }
    });
    data["serviceCategoryIds"] = serviceCategoryIdStr;
    APP_BCGOGO.Net.syncPost({
        url: "appoint.do?method=searchAppointOrder",
        dataType: "json",
        data: data,
        success: function (result) {
            drawAppointOrderList(result);
            initPage(result, "appointOrderList", "appoint.do?method=searchAppointOrder", '', "drawAppointOrderList", '', '', data, '');
        },
        error: function () {
            nsDialog.jAlert("数据异常，请刷新页面！");
        }
    });
}

function drawAppointOrderList(result){
   $("#appointOrderListTb tr:gt(1)").remove();
   var appointOrderDTOs = [];
    if(result && !G.Lang.isEmpty(result["data"])){
        appointOrderDTOs = result["data"];
    }
    if(G.Lang.isEmpty(appointOrderDTOs)){
        var emptyHtml = '<tr><td colspan="10">对不起，没有找到您要的单据信息!</td></tr>';
        $("#appointOrderListTb").append(emptyHtml);
    } else {
        for (var i = 0; i < appointOrderDTOs.length; i++) {
            var html = '';
            var appointOrderDTO = appointOrderDTOs[i];
            var orderPrefix = "appointOrderDTOs" + i;
            html += '<tr class="titBody_Bg" id="' + orderPrefix + '.orderContent">';
            html += buildSingleAppointContentHtml(appointOrderDTO, orderPrefix);
            html += '</tr>';
            html += '<tr class="titBottom_Bg"><td colspan="10"></td></tr>';
            $("#appointOrderListTb").append(html);
        }
    }
}


function drawSingleAppointOrder(appointOrderDTO, orderPrefix) {
    if (G.Lang.isEmpty(appointOrderDTO) || G.Lang.isEmpty(orderPrefix)) {
        return;
    }
    var html = buildSingleAppointContentHtml(appointOrderDTO,orderPrefix);
    var $orderContentDom = $("#" + orderPrefix + "//.orderContent");
    $orderContentDom.empty();
    $orderContentDom.append(html);
}

function buildSingleAppointContentHtml(appointOrderDTO ,orderPrefix){
    if (G.Lang.isEmpty(appointOrderDTO) || G.Lang.isEmpty(orderPrefix)) {
            return;
        }

    var receiptNo = G.Lang.normalize(appointOrderDTO["receiptNo"], "--");
    var customerName = G.Lang.normalize(appointOrderDTO["customer"], "--");
    var vehicleNo = G.Lang.normalize(appointOrderDTO["vehicleNo"], "--");  //下单时间
    var customerMobile = G.Lang.normalize(appointOrderDTO["customerMobile"], "--");  //客户手机号
    var appointServiceType = G.Lang.normalize(appointOrderDTO["appointServiceType"], "--");  //服务类型
    var createTimeStr = G.Lang.normalize(appointOrderDTO["createTimeStr"], "--");  //下单时间
    var appointTimeStr = G.Lang.normalize(appointOrderDTO["appointTimeStr"], "--");  //预约时间
    var appointWayStr = G.Lang.normalize(appointOrderDTO["appointWayStr"], "--");  //预约方式
    var statusStr = G.Lang.normalize(appointOrderDTO["statusStr"], "--");  //单据状态文案
    var status = G.Lang.normalize(appointOrderDTO["status"], "");  //单据状态枚举
    var id = G.Lang.normalize(appointOrderDTO["idStr"], "");  //单据id
    var orderType =G.Lang.normalize(appointOrderDTO["orderType"], "");//预约单关联单据的类型
    var orderIdStr =G.Lang.normalize(appointOrderDTO["orderIdStr"], "");//预约单关联单据的id
    var isCanToRelatedOrder  = false;
    if ('WASH_BEAUTY' == orderType) {
        if (APP_BCGOGO.Permission.VehicleConstruction.WashBeauty.Base) {
            isCanToRelatedOrder = true;
        }
    } else if ('REPAIR' == orderType) {
        if ('TO_DO_REPAIR' == status) {
            if (APP_BCGOGO.Permission.VehicleConstruction.Base) {
                isCanToRelatedOrder = true;
            }
        } else if ('HANDLED' == status) {
            if (APP_BCGOGO.Permission.VehicleConstruction.Base) {
                isCanToRelatedOrder = true;
            }
        }
    } else if ('REPAIR_DRAFT_ORDER' == orderType) {
        if (APP_BCGOGO.Permission.VehicleConstruction.Base) {
            isCanToRelatedOrder = true;
        }
    }
    var isCanCreateOrderPermission = false;
    if (appointServiceType == '洗车') {
        if (APP_BCGOGO.Permission.VehicleConstruction.WashBeauty.Base) {
            isCanCreateOrderPermission = true;
        }
    } else if (APP_BCGOGO.Permission.VehicleConstruction.Base) {
        isCanCreateOrderPermission = true;
    }
    var html = '';
    html += '<td style="padding-left:10px;">';
    html += '<a class="blue_color J_to_appointOrder" id="' + orderPrefix + '.receiptNo">' + receiptNo + '</a>';
    html += '<input type="hidden" id="' + orderPrefix + '.id" value="' + id + '">';
    html += '<input type="hidden" id="' + orderPrefix + '.orderType" value="' + orderType + '">';
    html += '<input type="hidden" id="' + orderPrefix + '.orderIdStr" value="' + orderIdStr + '">';
    html += '</td>';
    html += '<td>' + customerName + '</td>';
    html += ' <td>' + vehicleNo + '</td>';
    html += ' <td>' + customerMobile + '</td>';
    html += ' <td>' + appointServiceType + '</td>';
    html += ' <td>' + createTimeStr + '</td>';
    html += ' <td>' + appointTimeStr + '</td>';
    html += ' <td>' + appointWayStr + '</td>';
    html += ' <td>' + statusStr + '</td>';
    html += ' <td>';

    if (status == "PENDING" && APP_BCGOGO.Permission.VehicleConstruction.AppointOrder.Manager) {
        html += '<a class="blue_color J_acceptOrder" id="' + orderPrefix + '.accept">接受</a>&nbsp;';
        html += '<a class="blue_color J_refuseOrder" id="' + orderPrefix + '.refuse">拒绝</a>&nbsp;';
    }
    if (status == "ACCEPTED" && APP_BCGOGO.Permission.VehicleConstruction.AppointOrder.Manager && isCanCreateOrderPermission) {
        html += '<a class="blue_color J_createOtherOrder" id="' + orderPrefix + '.createOtherOrder">生成单据</a>&nbsp;';
    }
    if ((status == "HANDLED" || status == "TO_DO_REPAIR") && isCanToRelatedOrder) {
        html += '<a class="blue_color J_toRelatedOrder" id="' + orderPrefix + '.toRelatedOrder">查看单据</a>&nbsp;';
    }
    if ((status == "PENDING" || status == "ACCEPTED") && APP_BCGOGO.Permission.VehicleConstruction.AppointOrder.Manager) {
        html += '<a class="blue_color J_cancelOrder" id="' + orderPrefix + '.cancel">取消</a>&nbsp;';
    }
    html += '</td>';
    return html;
}

//客户名+手机号下拉选择
function customerNameOrMobileSuggestion($customer) {
    if ($customer) {
        var uuid = GLOBAL.Util.generateUUID();
        var searchWord = $customer.val();
        var ajaxUrl = "customer.do?method=getCustomerOrSupplierSuggestion";
        var ajaxData = {
            searchWord: searchWord,
            customerOrSupplier: "customer",
            uuid:uuid
        };
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(uuid);
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            var drawData = {};
            var data = [];
            if (!G.Lang.isEmpty(result)) {
                $.each(result, function (index, value) {
                    if (!G.Lang.isEmpty(value) && !G.Lang.isEmpty(value))
                    var suggestionEntry = value["suggestionEntry"];
                    var customerDetail = {};
                    $.each(value["suggestionEntry"],function(itemIndex,itemValue){
                        customerDetail[itemValue[0]] = itemValue[1];
                    });
                    var customerName = G.Lang.normalize(customerDetail["name"]);
                    var customerMobile = G.Lang.normalize(customerDetail["mobile"]);
                    var label = "";
                    if (!G.Lang.isEmpty(customerName)) {
                        label = customerName;
                    }
                    if (!G.Lang.isEmpty(label) && !G.Lang.isEmpty(customerMobile)) {
                        label += "+";
                    }
                    if (!G.Lang.isEmpty(customerMobile)) {
                        label += customerMobile;
                    }
                    var item = {
                        details: customerDetail,
                        label: label
                    };
                    data.push(item);
                });
            }
            drawData["data"] = data;
            drawData["uuid"] = uuid;
            dropList.show({
                "selector": $customer,
                "data": drawData,
                "onSelect": function (event, index, data) {
                    if (!G.Lang.isEmpty(data) && !G.Lang.isEmpty(data.details)) {
                        var selectCustomerName = data.details["name"];
                        $customer.val(selectCustomerName);
                        var customerId = G.Lang.normalize(data.details["id"]);
                        $("#customerIds").val(customerId);
                    }
                    $customer.removeAttr("onKeyboardSelect");
                    dropList.hide();
                }, "onKeyboardSelect": function (event, index, data) {
                    if (!G.Lang.isEmpty(data) && !G.Lang.isEmpty(data.details)) {
                        var selectCustomerName = G.Lang.normalize(data.details["name"]);
                        $customer.val(selectCustomerName);
                        $customer.attr("onKeyboardSelect", true);
                        var customerId = G.Lang.normalize(data.details["id"]);
                        $("#customerIds").val(customerId);
                    } else {
                        $customer.removeAttr("onKeyboardSelect");
                        $("#customerIds").val("");
                    }
                }
            });
        });
    }
}