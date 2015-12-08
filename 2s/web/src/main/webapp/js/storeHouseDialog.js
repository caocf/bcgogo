$(function(){
    $("#storeHouseDialog").dialog({
        autoOpen: false,
        resizable: false,
        title: "选择仓库信息",
        height: 130,
        width: 390,
        modal: true,
        closeOnEscape: false,
        showButtonPanel: true
    });
    $("#confirmBtn1").click(function(){
        $("#storehouseId").val($("#storehouseDiv").val());
        $("#storehouseId").change();
        $("#storehouseDiv").val('');
        $("#storeHouseDialog").dialog("close");
        if(!G.isEmpty($("#btnType").val())) {
            $("#" + $("#btnType").val()).click();
        }

    });
    $("#cancleBtn").click(function(){
        $("#storehouseDiv").val('');
        $("#storeHouseDialog").dialog("close");
    });
});


function checkStorehouseSelected() {
    if (App.Permission.Version.StoreHouse && G.Lang.isEmpty($("#storehouseId").val())) {
        $("#storeHouseDialog").dialog("open");
        return false;
    } else {
        return true;
    }
}

