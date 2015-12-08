$(function () {
    $(".showRepair ,.showRepairPicking").bind("click", function () {
        window.open($(this).attr("url"), "_blank");
    });
    $(".repairPickingLack").bind("click",function(){
        if (!$(this).attr("storehouseId") && $(this).attr("repairPickingId")) {
           window.open("storage.do?method=getProducts&repairPickingId="+$(this).attr("repairPickingId"),"_blank");
        }else if($(this).attr("repairPickingId")){
            var date = {"orderId":$(this).attr("repairPickingId"),"orderType":"REPAIR_PICKING"};
            APP_BCGOGO.Net.syncPost({
                url: "txn.do?method=validatorLackProductTodo&"+ Math.random() * 10000000,
                dataType: "json",
                data:date,
                success: function (result) {
                    if (!result.success && result.operation == "ALLOCATE_OR_PURCHASE") {
                        $("#allocate_or_purchase_div").dialog({
                            resizable: false,
                            title: "缺料提醒！",
                            height: 150,
                            width: 288,
                            modal: true,
                            closeOnEscape: false,
                            buttons: {
                                "仓库调拨": function () {
                                    window.open("allocateRecord.do?method=createAllocateRecordByRepairPicking&id="+date.orderId,"_blank");
                                    $(this).dialog("close");
                                },
                                "商品入库": function () {
                                    window.open("storage.do?method=getProducts&repairPickingId=" + date.orderId, "_blank");
                                    $(this).dialog("close");
                                }
                            }
                        });
                    } else {
                        window.open("storage.do?method=getProducts&repairPickingId=" + date.orderId, "_blank");
                    }
                },
                error: function () {
                    nsDialog.jAlert("网络异常");
                }
            });
        }
    });

    $(".choosePickingMan").live("click focus keyup", function (event) {
        event = event || event.which;
        var keyCode = event.keyCode;
        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }
        var obj = this;
        var domID =  $(this).attr("id").split(".");
        var hiddenId = "-";
        for (var i = 0; i < domID.length - 1; i++) {
            hiddenId += domID[i]+".";
        }
        hiddenId += "pickingManId-";

        droplistLite.show({
            event: event,
            hiddenId: hiddenId,
            id: "idStr",
            name: "name",
            data: "member.do?method=getSaleMans"
        });
    });

    $(".selectedPickingMan").live("click focus keyup", function (event) {
        event = event || event.which;
        var keyCode = event.keyCode;
        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }
        var obj = this;
        var keyWord = $(this).val();
        var domID =  $(this).attr("id").split(".");   //获取领料人所对应的id值：pickingman
        var hiddenId = "-";
        for (var i = 0; i < domID.length - 1; i++) {
            hiddenId += domID[i]+".";
        }
        hiddenId += "pickingManName-";
        droplistLite.show({
            event: event,
            hiddenId: hiddenId,
            id: "idStr",
            name: "name",
            keyword:"keyWord",
            data: "txn.do?method=searchWorks"
        });
    });


    $(".chooseRepairPickingMan").live("click focus keyup", function (event) {
        event = event || event.which;
        var keyCode = event.keyCode;
        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }
        var obj = this;
        var domID =  $(this).attr("id").split(".");
        var hiddenId = "-";
        for (var i = 0; i < domID.length - 1; i++) {
            hiddenId += domID[i]+".";
        }
        hiddenId += "pickingManId-";
//        salesManDTOs
        var onSelect = function (data) {
            if (!data) {
                return;
            }
            if (getOrderType() == "REPAIR_PICKING_LIST") {

                var idPrefix = $(obj).attr("id").split(".")[0];
                var idMiddle = $(obj).attr("id").split(".")[1];
                $("#" + idPrefix + "\\." + idMiddle + "\\.pickingMan").val(data.label);
                $("#" + idPrefix + "\\." + idMiddle + "\\.pickingManId").val(data.id);
                $("#" + idPrefix + "\\.form input[id$=.pickingMan]").each(function () {
                    if (!$(this).val()) {
                        $(this).val(data.label);
                        var thisIdPrefix1 = $(this).attr("id").split(".")[1];
                        $("#" + idPrefix + "\\." + thisIdPrefix1 + "\\.pickingManId").val(data.id);
                    }
                })
            } else if (getOrderType() == "REPAIR_PICKING_INFO") {
                var idPrefix = $(obj).attr("id").split(".")[0];
                $("#" + idPrefix + "\\.pickingMan").val(data.label);
                $("#" + idPrefix + "\\.pickingManId").val(data.id);
                $("#repairPickingForm input[id$=pickingMan]").each(function () {
                    if (!$(this).val()) {
                        $(this).val(data.label);
                        var thisIdPrefix1 = $(this).attr("id").split(".")[0];
                        $("#" + thisIdPrefix1 + "\\.pickingManId").val(data.id);
                    }
                })
            }
        }
        droplistLite.show({
            event: event,
            hiddenId: hiddenId,
            id: "idStr",
            name: "name",
            data: "member.do?method=getSaleMans",
            searchValue: "",
            afterSelected:function(event, index, data, hook){
                onSelect(data);
            } ,
            afterKeySelected: function (event, index, data, hook) {
                onSelect(data);
            }

        });
    });
    $("#show_innerPicking_list_btn").click(function(){
       window.location.href="pick.do?method=showInnerPickingListPage";
    });
    $("#show_innerReturn_list_btn").click(function(){
       window.location.href="pick.do?method=showInnerReturnListPage";
    });
    $("#print_innerPicking_btn").click(function () {
        if (!$("#id").val()) {
            nsDialog.jAlert("当前单据不存在，无法打印");
            return;
        }
        var url = "pick.do?method=printInnerPicking&innerPickingId=" + $("#id").val();
        window.open(url, "_blank");
    });
    $("#print_innerReturn_btn").click(function () {
        if (!$("#id").val()) {
            nsDialog.jAlert("当前单据不存在，无法打印");
            return;
        }
        var url = "pick.do?method=printInnerReturn&innerReturnId=" + $("#id").val();
        window.open(url, "_blank");
    });
    $("#print_repairPicking_Btn").click(function () {
        if (!$("#id").val()) {
            nsDialog.jAlert("当前单据不存在，无法打印");
            return;
        }
        var url = "pick.do?method=printRepairPicking&repairPickingId=" + $("#id").val();
        window.open(url, "_blank");
    });
});