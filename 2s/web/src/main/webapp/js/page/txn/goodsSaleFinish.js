/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-11-23
 * Time: 上午10:13
 * To change this template use File | Settings | File Templates.
 */


$(document).ready(function () {
    //采购查看页面用
    $(".J-priceInfo").live("mouseenter", function(event) {
        event.stopImmediatePropagation();

        var _currentTarget = $(event.target).find(".alert");
        _currentTarget.show();

        _currentTarget.mouseleave(function(event) {
            if($(event.relatedTarget)[0] != $(event.target).parents(".J-priceInfo")[0]) {
                _currentTarget.hide();
            }
        });

    }).live("mouseleave", function(event) {
            event.stopImmediatePropagation();
            var _currentTarget = $(event.target).find(".alert");
            if(event.relatedTarget != _currentTarget[0]) {
                _currentTarget.hide();
            }
        });


    $("#nullifyBtn").bind('click', function (event) {
        if ($("#nullifyBtn").attr("status") == "checkDeleteProduct") {
            return;
        }
        repealOrder($("#id").val());
    });

    var supplierProductIdArr=new Array();
    $(".item").each(function(){
        supplierProductIdArr.push($(this).find("input[id$='productId']").val());
    });
    initOrderPromotionsDetailForShowPage(supplierProductIdArr, $("#i_mainRight"));

    $(".J_QQ").each(function (index, value) {
        $(this).multiQQInvoker({
            QQ: $(this).attr("data_qq")
        });
    });
});

function repealOrder(orderId){
    if(APP_BCGOGO.Permission.Version.StoreHouse){
        if (!validatorStoreHouseOrderRepeal($("#_toStorehouseId").val(),getOrderType(), orderId)) {
            $("#nullifyBtn").removeAttr("status");
            return;
        }
    }
    $("#nullifyBtn").attr("status", "checkDeleteProduct");
    if (validateRepealStrikeSettled("SALE",orderId)) {
        $("#nullifyBtn").removeAttr("status");
        return;
    }

    var ajaxUrl = "txn.do?method=validatorDeletedProductOrderRepeal";
    var ajaxData = {orderId: orderId, orderType: getOrderType()};
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function (json) {
        $("#nullifyBtn").removeAttr("status");
        var url = "sale.do?method=saleOrderRepeal&salesOrderId=" + orderId;
        if(APP_BCGOGO.Permission.Version.StoreHouse){
            url +="&toStorehouseId="+$("#_toStorehouseId").val();
        }
        if (json.success) {
            if (confirm("友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！")) {
                window.location = url;
            }else{
                if(APP_BCGOGO.Permission.Version.StoreHouse){
                    $("#_toStorehouseId").val("");
                }
            }
        } else if (!json.success && json.operation == "confirm_deleted_product") {
            if (confirm(json.msg)) {
                window.location = url;
            }else{
                if(APP_BCGOGO.Permission.Version.StoreHouse){
                    $("#_toStorehouseId").val("");
                }
            }
        } else if (!json.success) {
            if(APP_BCGOGO.Permission.Version.StoreHouse){
                $("#_toStorehouseId").val("");
            }
            alert(json.msg);
        }
    }, function (json) {
        $("#nullifyBtn").removeAttr("status");
        alert("网络异常，请联系客服!");
    });
}

function toShotageInventory(salesOrderId) {
    window.open("storage.do?method=getProducts&type=good&goodsindexflag=gi&salesOrderId="+salesOrderId);
}