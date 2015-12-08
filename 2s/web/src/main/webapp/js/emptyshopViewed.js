$(function () {
    $('.content .item .info span').click(function () {
        var parent = $(this).parents('.item');
        window.open('shopProductDetail.do?method=toShopProductDetail&paramShopId=' + parent.attr('data-shopId') + '&productLocalId=' + parent.attr('data-id'));
    });
    $('.content .item .imgContainer img').click(function () {
        var parent = $(this).parents('.item');
        window.open('shopProductDetail.do?method=toShopProductDetail&paramShopId=' + parent.attr('data-shopId') + '&productLocalId=' + parent.attr('data-id'));
    });
    $(".content .shoppingButton input[value='加入购物车']").click(function(){
        var parent = $(this).parents('.item');
        var productId = parent.attr('data-id');
        var supplierShopId = parent.attr('data-shopId');
        var paramString = supplierShopId + "_" + productId + "_" + 1;
        addProductToShoppingCart(paramString);
    });
    $(".content .shoppingButton input[value='立即采购']").click(function () {
        var parent = $(this).parents('.item');
        var productId = parent.attr('data-id');
        purchaseProduct(productId);
    });

    $("#goShoppingCartBtn").bind("click", function () {
        window.location.href = "shoppingCart.do?method=shoppingCartManage";
    });
    $("#closeBtn").bind("click", function () {
        $("#returnDialog").dialog("close");
    });

    $('.tablePage .normal_btn').click(function(){
        var hidePage = $('.tablePage .hover_btn').toggleClass('hover_btn').attr('data-page');
        var showPage = $(this).toggleClass('hover_btn').attr('data-page');
        $('.content[data-page=' + hidePage + ']').toggleClass('hide');
        $('.content[data-page=' + showPage + ']').toggleClass('hide');
    });
});

function addProductToShoppingCart(paramString){
    if(!G.Lang.isEmpty(paramString)){
        APP_BCGOGO.Net.syncPost({
            url: "shoppingCart.do?method=addProductToShoppingCart",
            dataType: "json",
            data: {
                paramString:paramString
            },
            success: function (json) {
                updateShoppingCartNumber();
                $("#resultMsg").text(json.data.resultMsg);
                $("#warnMsg").text(json.data.warnMsg);
                $("#shoppingCartItemCount").text(json.data.shoppingCartItemCount);
                $("#shoppingCartTotal").text(json.data.shoppingCartTotal);
                $("#goShoppingCartBtn").text(json.data.goShoppingCartBtnName);
                $("#returnDialog").dialog({
                    width: 333,
                    minHeight:140,
                    modal: true,
                    resizable:false,
                    position:'center',
                    open: function() {
                        $(".ui-dialog-titlebar", $(this).parent()).hide();
                        $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
                    }
                });
                if(userGuide.currentStepName == "PRODUCT_PRICE_GUIDE_PURCHASE"){
                    userGuide.funLib["PRODUCT_PRICE_GUIDE"]["_PRODUCT_PRICE_GUIDE_PURCHASE_STEP3_2"]();
                }
            },
            error: function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    }
}

function purchaseProduct(productLocalInfoIds) {
    APP_BCGOGO.Net.syncPost({
        url: "autoAccessoryOnline.do?method=validatorPurchaseProduct",
        dataType: "json",
        data: {
            productLocalInfoIds: productLocalInfoIds,
            amounts: 1
        },
        success: function (json) {
            if (json.success) {
                window.location = "RFbuy.do?method=createPurchaseOrderOnlineByProduct&paramString=" + json.data.join(",");
            } else {
                nsDialog.jAlert(json.msg);
            }
        },
        error: function () {
            nsDialog.jAlert("数据异常!");
        }
    });
}



