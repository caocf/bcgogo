var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$(function () {
    tableUtil.tableStyle(".tabSlip",null,"odd");
    $("#out_return_btn").click(function(){
        if($(this).attr("submitLock")){
            return;
        }
        $(this).attr("submitLock",true);
        if (checkEmptySelected() && checkToHandleSelected() > 0) {
            nsDialog.jAlert("请选择领料或者退料内容！");
            $("#out_return_btn").removeAttr("submitLock");
            return;
        }
        if(checkLackPickMan()){
            nsDialog.jAlert("请填写领/退料人！");
            $("#out_return_btn").removeAttr("submitLock");
            return;
        }
        $("#repairPickingForm").attr("action","pick.do?method=validatorRepairPicking");
        $("#repairPickingForm").ajaxSubmit({
            url: "pick.do?method=validatorRepairPicking",
            dataType: "json",
            type: "POST",
            success: function (json) {
                if(!json.success){
                    if(json.operation == "ALERT"){
                        nsDialog.jAlert(json.msg);
                    }else if(json.operation == "CONFIRM"){
                        nsDialog.jConfirm(json.msg, "确认出库/退料提示", function (returnVal) {
                          if(returnVal){
                              window.open(json.data,"_blank");
                          }
                        });
                    }else if(json.operation == "TO_CHOOSE_STOREHOUSE"){
                        $("#selectStorehouseDialog").dialog({
                             width: 350,
                             modal: true,
                             resizable: false,
                             beforeclose: function(event, ui) {
                                 $("#_storehouseId").attr("data-order-id","");
                                 return true;
                             },
                             open: function() {
                                 $("#_storehouseId").attr("data-order-id",$("#id").val());
                                 //获取 仓库选项
                                 APP_BCGOGO.Net.syncAjax({
                                     url: "storehouse.do?method=getAllStoreHouseDTOs",
                                     type: "POST",
                                     dataType: "json",
                                     success: function (data) {
                                         if (data) {
                                             $("#_storehouseId option").remove();
                                             $.each(data, function(i, item){
                                                 $("#_storehouseId").append("<option value='"+item.idStr+"'>"+item.name+"</option>");
                                             });
                                         }
                                     }
                                 });
                             }
                         });

                    }
                }else{
                    nsDialog.jConfirm("友情提示：请确认是否出库/退料？","确认出库/退料提示",function(returnVal){
                       if(returnVal){
                           $("#repairPickingForm").attr("action","pick.do?method=handleRepairPicking");
                           $("#repairPickingForm").submit();
                       }
                    });
                }
                $("#out_return_btn").removeAttr("submitLock");
            },
            error: function (json) {
                nsDialog.jAlert(json.msg);
                $("#out_return_btn").removeAttr("submitLock");
            }
        });
    });

    $(".chks_item").bind("change",function(){
        checkAllCheck();
    });
    $("#checkAll").bind("change", function () {
        var check = $(this).attr("checked");
        $(".chks_item").each(function () {
            $(this).attr("checked", check);
        });
    });
    //页面加载的时候提示上一次校验问题
    if ($("#repairPickingMessage").val()) {
        if ($("#repairPickingMessage").attr("resultOperation") == "ALERT") {
            nsDialog.jAlert($("#repairPickingMessage").val());
        } else if ($("#repairPickingMessage").attr("resultOperation") == "CONFIRM") {
            nsDialog.jConfirm($("#repairPickingMessage").val(), "出库/退料失败", function (returnVal) {
                if (returnVal) {
                    window.open($("#repairPickingMessage").attr("resultDate"), "_blank");
                }
            });
        }
    }
})
function getHandledItemDetail(){
    var tbSize = 0;
    $(".handledItemDTO_tb").each(function(){
        tbSize += $(this).find("tr").size()+1;
    });

    var height = tbSize > 10 ? 620: 'auto';
    $("#handledItem_dialog").dialog({
        resizable: false,
        title:"商品领/退料流水记录",
        width:750,
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

//全选/反选
function checkAllCheck(){
    //全选
    var isAllCheck = true;
    var isAllNotCheck =true;
    $(".chks_item").each(function(){
       if($(this).attr("checked")){
           isAllNotCheck = false;
       }else{
           isAllCheck = false;
       }
    });

    if ($("#checkAll").attr("checked")) {
        if(isAllNotCheck){
            $("#checkAll").attr("checked",false);
        }
    } else {
        if(isAllCheck){
            $("#checkAll").attr("checked",true);
        }
    }
}

//检查选中项是否领料人是否为空
function checkLackPickMan(){
    var submitCheck = false;
    $(".chks_item").each(function () {
        if($(this).attr("checked")){
            var idPrefix = $(this).attr("id").split(".")[0];
            if(!$.trim($("#"+idPrefix + "\\.pickingMan").val())){
                submitCheck = true;
                return false;
            }
        }
    });
    return submitCheck;
}

//检查是否为空
function checkEmptySelected() {
    var submitCheck = true;
    $(".chks_item").each(function () {
        if ($(this).attr("checked")) {
            submitCheck = false;
            return false;
        }
    });
    return submitCheck;
}

//检查是否有可操作选项
function checkToHandleSelected() {
  return $(".chks_item").size()>0;
}