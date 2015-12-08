$(document).ready(function () {
    $(".J_smallImageSwitch").bind("click",function(){
        var index = $(this).attr("data-index");
        $(".J_smallImageSwitch").closest("div").removeClass("actived");
        $(this).closest("div").addClass("actived");
        $(".J_bigImageSwitch").hide();
        $("#bigImageSwitch_"+index).show();
    });


    var $productSalesPromotionFloat = $(".product-sales-promotion-float");
    $productSalesPromotionFloat.hide();

    var $buttonPromotionDetails = $(".product-control .content-button-hover");
    $buttonPromotionDetails
        .bind("mouseenter", function () {
        var position = $(this).position(),
            height = $(this).height();
        $productSalesPromotionFloat
            .css({
            top: position.top + height,
            left: 210,
            width: 550
        })
            .show();
    })
        .bind("mouseleave", function () {
            $productSalesPromotionFloat.hide();
        });

    $(".product-details .info-quantity .info-content .content-count").spinner({
        min: 1,
        max: 100000000000
    });

    // 渲染促销内容
    renderPromotionsContent();

    purchaseAndShoppingCarRelatedBind();

    $('#productQuickSearchBtn').click(function () {
        $("#productSimpleSearchForm").submit();
    });

});

/**
 * 渲染促销内容
 */
function renderPromotionsContent() {
    var promotionContent = $("#promotionContent").val();
    var promotionTitle = $("#promotionTitle").val();
    if (!G.isEmpty(promotionContent)) {
        var contents = promotionContent.split("_");
        var titles = promotionTitle.split("+");
        var domFloat = '<ul class="fl">';
        var domStatic = '<ul class="fl">';
        for (var i = 0; i < contents.length; i++) {
            var content = contents[i];
            if (!G.isEmpty(content)) {
                var contentParts = content.split("|");
                domFloat += '<div class="details-label fl">' + titles[i] + '活动：</div>';
                domFloat += '<div class="details-info fl"> '+emDigit(contentParts[0])+'<div class="info-comment">' +contentParts[1]+'</div></div>';
                domFloat += '<div class="cl"></div>';
                domStatic += '<div class="details-note-img fl"><img src="images/promotions/' + titles[i] + '.png"></div>';
                domStatic += '<div class="details-info fl"> '+emDigit(contentParts[0])+'<div class="info-comment">' +contentParts[1]+'</div></div>';
                domStatic += '<div class="cl"></div>';
            }
        }
        domFloat += '</ul><div class="cl"></div>';
        domStatic += '</ul><div class="cl"></div>';
        $(".j_promotions_content").append($(domFloat));
        $(".j_promotions_content_static").append($(domStatic));
    }

}

function emDigit(str) {
    var pattern = new RegExp("[0-9]+(?:\\.[0-9]*)?", "g");
    var result;
    var lastIndex = 0;
    var resultStr = "";
    while ((result = pattern.exec(str)) != null) {
        if (!lastIndex) {
            resultStr += str.slice(0, result.index) + "<em>" + result + "</em>";
        } else {
            resultStr += str.slice(lastIndex, result.index) + "<em>" + result + "</em>";
        }
        lastIndex = pattern.lastIndex;
        //str = (str.slice(0, result.index) + "<em>" + result + "</em>" + str.slice(pattern.lastIndex, str.length));
    }
    if (lastIndex < str.length)
        resultStr += str.slice(lastIndex, str.length);
    return resultStr;
}

/* event handler registers below  */
function purchaseAndShoppingCarRelatedBind() {

    // 去购物车结算
    $("#goShoppingCartBtn").bind("click", function (e) {
        e.preventDefault();
        window.location.href = "shoppingCart.do?method=shoppingCartManage";
    });

    // 继续采购（隐藏购物车）
    $("#closeBtn").bind("click", function (e) {
        e.preventDefault();
        $("#returnDialog").dialog("close");
    });

    // 单笔加入购物车
    $(".J-addToShoppingCartSingleBtn").live("click", function (e) {
        if($("#fromSource").val()=="goodsInSalesEditor" || selfShopId == $("#productShopId").val()){
            nsDialog.jAlert("不能将自己店铺的商品加入购物车。");
            return;
        }
        e.preventDefault();
        var productId = $("#productLocalInfoId").val();
        var amount = G.normalize($("#purchaseNum").val());
        var supplierShopId = $("#paramShopId").val();
        var paramString = supplierShopId + "_" + productId + "_" + amount;
        addProductToShoppingCart(paramString);
    });

    // 立即采购（单笔采购）
    $(".J-purchaseSingleBtn").live("click", function (e){
        if($("#fromSource").val()=="goodsInSalesEditor" || selfShopId == $("#productShopId").val()){
            nsDialog.jAlert("不能采购自己店铺的商品。");
            return;
        }
        e.preventDefault();
        var productId = $("#productLocalInfoId").val();
        var amount = G.normalize($("#purchaseNum").val());
        if (!G.Lang.isEmpty(productId)) {
            purchaseProduct(productId, amount);
        }
    });

    // 全选按钮
    $("#checkAll").bind("click", function () {
        $("#floatBarSelectAll").attr("checked", this.checked);
        $("input[name='selectProduct']").attr("checked", this.checked);
    });
    $("input[name='selectProduct']").live("click", function () {
        var checked = $("input[name='selectProduct']").length == $("input[name='selectProduct']:checked").length ? true : false;
        $("#checkAll").attr("checked", checked);
    });

}



/* handlers below*/
/**
 * 添加到购物车
 * @param paramString
 */
function addProductToShoppingCart(paramString) {
    if (!G.Lang.isEmpty(paramString)) {
        APP_BCGOGO.Net.syncPost({
            url: "shoppingCart.do?method=addProductToShoppingCart",
            dataType: "json",
            data: {
                paramString: paramString
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
                    minHeight: 140,
                    modal: true,
                    resizable: false,
                    position: 'center',
                    open: function () {
                        $(".ui-dialog-titlebar", $(this).parent()).hide();
                        $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
                    }
                });
                if (userGuide.currentStepName == "PRODUCT_PRICE_GUIDE_PURCHASE") {
                    userGuide.funLib["PRODUCT_PRICE_GUIDE"]["_PRODUCT_PRICE_GUIDE_PURCHASE_STEP3_2"]();
                }
            },
            error: function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    }
}

/**
 * 采购产品
 * @param productLocalInfoIds
 * @param amounts
 */
function purchaseProduct(productLocalInfoIds, amounts) {
    APP_BCGOGO.Net.syncPost({
        url: "autoAccessoryOnline.do?method=validatorPurchaseProduct",
        dataType: "json",
        data: {
            productLocalInfoIds: productLocalInfoIds,
            amounts: amounts
        },
        success: function (json) {
            if (json.success) {
                if (userGuide.currentStepName == "PRODUCT_PRICE_GUIDE_PURCHASE") {
                    window.location = "RFbuy.do?method=createPurchaseOrderOnlineByProduct&paramString="
                        + json.data.join(",") + "&currentStepName=PRODUCT_PRICE_GUIDE_PURCHASE";
                } else {
                    window.location = "RFbuy.do?method=createPurchaseOrderOnlineByProduct&paramString=" + json.data.join(",");
                }

            } else {
                if (json.operation == "CONFIRM_RELATED_SUPPLIER") {
                    nsDialog.jConfirm(json.msg, null, function (returnVal) {
                        if (returnVal) {
                            applySupplierRelation(json.data);
                        }
                    });
                } else {
                    nsDialog.jAlert(json.msg);
                }
            }
        },
        error: function () {
            nsDialog.jAlert("数据异常!");
        }
    });
}