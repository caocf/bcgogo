$(document).ready(function () {

    tableUtil.tableStyle(".tabSlip",".titleBg","odd");
    $(function () {
        $(".up").toggle(
            function () {
                $(this).parent().parent().parent().find("tr:gt(0)").css("display", "");
                $(this).html("收拢");
                $(this).removeClass().addClass("down");
            },
            function () {
                $(this).parent().parent().parent().find("tr:gt(0)").css("display", "none");
                $(this).html("更多");
                $(this).removeClass().addClass("up");
            }
        );
        $(".down").toggle(
            function () {
                $(this).parent().parent().parent().find("tr:gt(0)").css("display", "none");
                $(this).html("更多");
                $(this).removeClass().addClass("up");
            },
            function () {
                $(this).parent().parent().parent().find("tr:gt(0)").css("display", "");
                $(this).html("收拢");
                $(this).removeClass().addClass("down");
            }
        );
    })
    $("#searchBtn").click(function () {
        /*$("#pickingMan").val($("#pickingMan_forPending").val());*/
        $("#thisform").attr("action","pick.do?method=showRepairPickingListPage");
        $("#thisform").submit();

    });



    $("#startTimeStr,#endTimeStr").datepicker({
        "numberOfMonths": 1,
        "changeYear": true,
        "changeMonth": true,
        "dateFormat": "yy-mm-dd",
        "yearRange": "c-100, c",
        "yearSuffix": "",
        "showButtonPanel": true
    });
    $(".outStorage,.returnStorage").bind("click", function () {
        var $thisDom = $(this);
        if ($thisDom.attr("submitLock")) {
            return;
        }
        $thisDom.attr("submitLock", true);
        var idPrefix = $thisDom.attr("id").split(".")[0];
        var isPickingManEmpty = false;
        $("#" + idPrefix + "\\.form input[name$='status']").each(function () {
            if ($(this).val() == "WAIT_OUT_STORAGE" ||$(this).val() == "WAIT_RETURN_STORAGE") {
                var itemIdPrefix = $(this).attr("id").split(".")[1];
                if (!$("#" + idPrefix + "\\." + itemIdPrefix + "\\.pickingMan").val()) {
                    isPickingManEmpty = true;
                    return false;
                }
            }
        });
        if (isPickingManEmpty) {
            nsDialog.jAlert("请填写领/退料人！");
            $thisDom.removeAttr("submitLock");
            return;
        }
        var operationType;
        if($(this).hasClass("outStorage")){
            operationType = "OUT_STORAGE";
        }else if($(this).hasClass("returnStorage")){
            operationType = "RETURN_STORAGE";
        }
        $("#"+idPrefix+"\\.form").attr("action", "pick.do?method=validatorRepairPicking&operationType="+operationType);
        $("#"+idPrefix+"\\.form").ajaxSubmit({
            url: "pick.do?method=validatorRepairPicking&operationType="+operationType,
            dataType: "json",
            type: "POST",
            success: function (json) {
                if (!json.success) {
                    if (json.operation == "ALERT") {
                        nsDialog.jAlert(json.msg);
                    } else if (json.operation == "CONFIRM") {
                        nsDialog.jConfirm(json.msg, "确认出库/退料提示", function (returnVal) {
                            if (returnVal) {
                                window.open(json.data, "_blank");
                            }
                        });
                    } else if (json.operation == "TO_CHOOSE_STOREHOUSE") {
                        $("#selectStorehouseDialog").dialog({
                            width: 350,
                            modal: true,
                            resizable: false,
                            beforeclose: function (event, ui) {
                                $("#_storehouseId").attr("data-order-id", "");
                                $("#_storehouseId").attr("repair_Picking_idPrefix", "");
                                return true;
                            },
                            open: function () {
                                $("#_storehouseId").attr("data-order-id", $("#"+idPrefix + "\\.form").find("input[name='id']").val());
                                $("#_storehouseId").attr("repair_Picking_idPrefix", idPrefix);
                                //获取 仓库选项
                                APP_BCGOGO.Net.syncAjax({
                                    url: "storehouse.do?method=getAllStoreHouseDTOs",
                                    type: "POST",
                                    dataType: "json",
                                    success: function (data) {
                                        if (data) {
                                            $("#_storehouseId option").remove();
                                            $.each(data, function (i, item) {
                                                $("#_storehouseId").append("<option value='" + item.idStr + "'>" + item.name + "</option>");
                                            });
                                        }
                                    }
                                });
                            }
                        });

                    }
                } else {
                    var msg ;
                    if(operationType == "OUT_STORAGE"){
                        msg = "友情提示：出库确认后，本单中所有未出库材料将全部出库！<br><br><div align='center'>您确定要全部出库吗？</div>";
                    }else if(operationType == "RETURN_STORAGE"){
                        msg = "友情提示：退料确认后，本单中所有未退料材料将全部退料！<br><br><div align='center'>您确定要全部退料吗？</div>";
                    }
                    nsDialog.jConfirm(msg,"确认出库/退料提示", function (returnVal) {
                        if (returnVal) {
                            itemFormSubmit(idPrefix,operationType);
                        }
                    });
                }
                $thisDom.removeAttr("submitLock");
            },
            error: function (json) {
                nsDialog.jAlert(json.msg);
                $thisDom.removeAttr("submitLock");
            }
        });
    });

    $("#receiptNo").bind("keyup",function(){
        $(this).val($(this).val().toUpperCase());
    }).bind("blur", function(){
        $(this).val($(this).val().toUpperCase());
    });

    $("#clearCondition").bind("click", function() {
        $("#receiptNo").val("");
        $("#repairOrderReceiptNo").val("");
        $("#productSeller").val("");
        $("#startTimeStr").val("");
        $("#endTimeStr").val("");
        $("#pickingMan").val("");
        $("#pickingMan_forPending").val("");
        $("#warehouse").val("");
        isHavePickingMan("PENDING");
    });

})

function itemFormSubmit(idPrefix,operationType) {
    $("#" + idPrefix + "\\.form").attr("action", "pick.do?method=ajaxHandleRepairPicking&operationType="+operationType);
    $("#" + idPrefix + "\\.form").ajaxSubmit({
        url: "pick.do?method=ajaxHandleRepairPicking&operationType="+operationType,
        dataType: "json",
        type: "POST",
        success: function (json) {
            if (!json.success) {
                if (json.operation == "ALERT") {
                    nsDialog.jAlert(json.msg);
                } else if (json.operation == "CONFIRM") {
                    nsDialog.jConfirm(json.msg, "确认出库/退料提示", function (returnVal) {
                        if (returnVal) {
                            window.open(json.data, "_blank");
                        }
                    });
                }
            } else {
//                window.open(json.data, "_blank");
                $("#thisform").submit();
            }
        },
        error: function (json) {
            nsDialog.jAlert("网络异常");
        }
    });
}

function isHavePickingMan(paramInitCondition){
    var userChooseStatus=$("#repairStatus");
    if(paramInitCondition!=null){
        if(paramInitCondition=="PENDING"){
            userChooseStatus.val(paramInitCondition);
            $("#pickingMan_showOrHidden").css("display","none");
            $("#pickingMan").val("");
        }
        if(paramInitCondition=="ALL"){
            userChooseStatus.val(paramInitCondition);
            $("#pickingMan_showOrHidden").css("display","block");
        }
    }else{
        if(userChooseStatus.val()=="ALL"){
            $("#pickingMan_showOrHidden").css("display","block");
        }
        if(userChooseStatus.val()=="PENDING"){
            $("#pickingMan_showOrHidden").css("display","none");
            $("#pickingMan").val("");
        }

    }

}
