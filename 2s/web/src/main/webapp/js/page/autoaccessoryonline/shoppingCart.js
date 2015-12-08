/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 下午5:57
 */

$(document).ready(function () {

    //商品链接
    $(".J_open_productHistory").live("click", function(){
        var _this = $(this);
        var _td = _this.closest("td");
        var paramShopId =  _td.find("input[name='paramShopId']").val();
        var productLocalId =  _td.find("input[name='productLocalId']").val();
        if (!G.Lang.isEmpty(paramShopId) && G.Lang.isNumber(paramShopId)
            && !G.Lang.isEmpty(productLocalId) && G.Lang.isNumber(productLocalId)) {
            var url = "shopProductDetail.do?method=toShopProductDetail&paramShopId="+paramShopId+"&productLocalId=" + productLocalId;
            window.open(url, "_blank");
        }
    });

    /*------------------checkbox  全选  start------------------------------ */
    $("#floatBarSelectAll").live("click",function() {
        $("#selectAll").attr("checked",this.checked);
        $(".J-ForShop").attr("checked",this.checked);
        $(".J-CheckBoxItem").attr("checked",this.checked);
    });
    $("#selectAll").live("click",function() {
        $("#floatBarSelectAll").attr("checked",this.checked);
        $(".J-ForShop").attr("checked",this.checked);
        $(".J-CheckBoxItem").attr("checked",this.checked);
    });
    $(".J-ForShop").live("click",function() {
        $(this).closest(".J-ShopDataBlock").find(".J-CheckBoxItem").attr("checked",this.checked);
        var isSelectAll = $(".J-ForShop").length == $(".J-ForShop:checked").length ? true : false;
        $("#selectAll").attr("checked",isSelectAll);
        $("#floatBarSelectAll").attr("checked",isSelectAll);
    });
    $(".J-CheckBoxItem").live("click",function() {
        var $currentShopDataBlock = $(this).closest(".J-ShopDataBlock");
        $currentShopDataBlock.find(".J-ForShop").attr("checked",$currentShopDataBlock.find(".J-CheckBoxItem").length == $currentShopDataBlock.find(".J-CheckBoxItem:checked").length ? true : false);
        var isSelectAll = $(".J-ForShop").length == $(".J-ForShop:checked").length ? true : false;
        $("#selectAll").attr("checked",isSelectAll);
        $("#floatBarSelectAll").attr("checked",isSelectAll);
    });
    /*------------------checkbox  全选  end------------------------------ */


    $(".J-showAlert").live("mouseenter", function(event) {
        event.stopImmediatePropagation();
        var _currentTarget = $(event.target);
        var _alertTarget = $(event.target).next(".alert");
        _alertTarget.show();
        _alertTarget.mouseleave(function(event) {
            if($(event.relatedTarget)[0] != _currentTarget[0]) {
                _alertTarget.hide();
            }
        });
    }).live("mouseleave", function(event) {
            event.stopImmediatePropagation();
            var _alertTarget = $(event.target).next(".alert");
            if($(event.relatedTarget).closest(".alert")[0] != _alertTarget[0]) {
                _alertTarget.hide();
            }
        });


    $(".J-goSupplier").live("click",function(e){
        e.preventDefault();
        var supplierShopId = $(this).attr("data_supplierShopId");
        if (!G.Lang.isEmpty(supplierShopId) && G.Lang.isNumber(supplierShopId)) {
            window.open("shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=" + supplierShopId, "_blank");
        }
    });

    $(".J-ClearInvalidItem").live("click",function(e){
        e.preventDefault();
        APP_BCGOGO.Net.syncPost({
            url: "shoppingCart.do?method=clearInvalidShoppingCartItems",
            dataType: "json",
            success: function (json) {
                if(json.success){
                    nsDialog.jAlert("清除失效商品成功！","友情提示",function(){
                        window.location.reload();
                    });
                }else{
                    nsDialog.jAlert("清除失效商品失败，请刷新页面！");
                }
            },
            error: function () {
                nsDialog.jAlert("数据异常，请刷新页面！");
            }
        });
    });

    $(".J-ModifyAmount")
        .live("blur",function () {
            if (dataTransition.simpleRounding($(this).attr("lastValue"),1) == dataTransition.simpleRounding($(this).val(),1)) {
                $(this).val(dataTransition.simpleRounding($(this).val(),1));
                return;
            }
            var amount = dataTransition.simpleRounding($(this).val(), 1);
            if(amount==0){
                $(this).val($(this).attr("lastValue"));
                nsDialog.jAlert("数量不能为0,请重新输入!");
                calculateShoppingCart($(this).closest("tr"));
                return false;
            }
            $(this).val(amount);
            var shoppingCartItemId = $(this).closest("tr").attr("data-shoppingcartitemid");
            if (!shoppingCartItemId) {
                return;
            }
            APP_BCGOGO.Net.syncPost({
                url: "shoppingCart.do?method=updateShoppingCartItemAmount",
                data:{
                    shoppingCartItemId: shoppingCartItemId,
                    amount:amount
                },
                dataType: "json",
                success: function (json) {
                    if(!json.success){
                        nsDialog.jAlert("保存失败，请刷新页面！");
                    }else{
                        shoppingCartButton();
                    }
                },
                error: function () {
                    nsDialog.jAlert("数据异常，请刷新页面！");
                }
            });

        })
        .live("focus",function () {
            $(this).attr("lastValue", $(this).val());
        })
        .live("keyup", function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 1));
            calculateShoppingCart($(this).closest("tr"));
        });


    $(".J-deleteShoppingCartItem").live("click",function(e){
        e.preventDefault();
        var shoppingCartItemId = $(this).closest("tr").attr("data-shoppingcartitemid");
        var $currentRow = $(this).closest(".J-ItemBody");
        var $table = $currentRow.parents(".J-purchaseOnlineTable");
        if(!G.Lang.isEmpty(shoppingCartItemId)){
            nsDialog.jConfirm("确认是否从购物车删除当前商品?", "删除商品", function (returnVal) {
                if (returnVal) {
                    APP_BCGOGO.Net.syncPost({
                        url: "shoppingCart.do?method=deleteShoppingCartItemById",
                        data:{
                            shoppingCartItemId: shoppingCartItemId
                        },
                        dataType: "json",
                        success: function (json) {
                            if(json.success){
                                removeShoppingCartItemHtml($currentRow);
                                json.data.shoppingCartItemCount == 0 && window.location.reload();
                                $(".J-ShoppingCartStatus").text(json.data.shoppingCartItemCount+"/"+json.data.shoppingCartMaxCapacity);
                                $(".J-ShoppingCartStatusImage").width((json.data.shoppingCartItemCount*100/json.data.shoppingCartMaxCapacity)+"%");
                            }else{
                                nsDialog.jAlert("删除失败，请刷新页面！");
                            }
                            updateShoppingCartNumber();
                            resetPromotionMap($table);
                            $(".J-AllTotal").text("");
                            $(".item").each(function(){
                                calculateShoppingCart($(this));
                            });
                            emptyShoppingCartRender();
                        },
                        error: function () {
                            nsDialog.jAlert("数据异常，请刷新页面！");
                        }
                    });
                }
            });
        }
    });

    $(".J-DeleteMultipleShoppingCartItem").live("click",function(e){
        e.preventDefault();
        var shoppingCartItemIds = [];
        var $domCheckBox = [];
        $(".J-CheckBoxItem:checked").each(function(){
            $domCheckBox.push($(this));
            shoppingCartItemIds.push($(this).closest("tr").attr("data-shoppingcartitemid"));
        });
        if(shoppingCartItemIds.length==0){
            nsDialog.jAlert("请选择需要删除的商品！");
        }else{
            nsDialog.jConfirm("确认是否从购物车删除当前选中的商品?","删除商品", function (returnVal) {
                if (returnVal) {
                    APP_BCGOGO.Net.syncPost({
                        url: "shoppingCart.do?method=deleteShoppingCartItemById",
                        data:{
                            shoppingCartItemId: shoppingCartItemIds.join(",")
                        },
                        dataType: "json",
                        success: function (json) {
                            if(json.success){
                                $.each($domCheckBox,function(key,$domObject){
                                    removeShoppingCartItemHtml($domObject.closest(".J-ItemBody"));
                                });
                                $(".J-ShoppingCartStatus").text(json.data.shoppingCartItemCount+"/"+json.data.shoppingCartMaxCapacity);
                                $(".J-ShoppingCartStatusImage").width((json.data.shoppingCartItemCount*100/json.data.shoppingCartMaxCapacity)+"%");
                                $(".J-purchaseOnlineTable").each(function(){
                                    resetPromotionMap($(this));
                                });
                                $(".J-AllTotal").text("");
                                $(".item").each(function () {
                                    calculateShoppingCart($(this));
                                });
                                nsDialog.jAlert("删除成功！", '', function () {
                                    window.location.reload();
                                });
                            }else{
                                nsDialog.jAlert("删除失败，请刷新页面！");
                            }
                            updateShoppingCartNumber();
                            calculateAllTotal();
                        },
                        error: function () {
                            nsDialog.jAlert("数据异常，请刷新页面！");
                        }
                    });
                }
            });
        }
    });

    $(".J-CreatePurchaseOrder").live("click",function(e){
        e.preventDefault();
        var shoppingCartItemIds = [];
        var checkFlag = true;
        $(".J-CheckBoxItem:checked").each(function(){
            var $domGoSupplier = $(this).closest("table").find(".J-ItemHead").find(".J-goSupplier");
            if(!$domGoSupplier[0]){
                checkFlag = false;
                return false;
            }
            shoppingCartItemIds.push($(this).closest("tr").attr("data-shoppingcartitemid"));
        });
        if(!checkFlag){
            nsDialog.jAlert("您选择的商品中有未关联供应商的商品,请关联供应商后或者删除此商品后继续下单！");
            return false;
        }
        if(shoppingCartItemIds.length==0){
            nsDialog.jAlert("请选择需要购买的商品！");
        }else{
            window.location = "RFbuy.do?method=createPurchaseOrderOnlineByShoppingCart&shoppingCartItemIds=" + shoppingCartItemIds.join(",");
        }
    });

    $(".J_QQ").each(function () {
        $(this).multiQQInvoker({
            QQ: $(this).attr("data_qq")
        })
    });

});
function removeShoppingCartItemHtml($currentRow){
    var $currentTable = $currentRow.closest(".J-ShopDataBlock");
    $currentRow.remove();
    if($currentTable.find(".J-ItemBody").length==0){
        if($currentTable.closest("tr").next("tr") && $currentTable.closest("tr").next("tr").hasClass("J-Separator")){
            $currentTable.closest("tr").next("tr").remove();
        }
        $currentTable.closest("tr").remove();
    }
}

function calculateShoppingCart($currentRow) {
   var amount=G.rounding($currentRow.find(".J-ModifyAmount").val());
    var oldPrice =G.rounding( $currentRow.closest("tr").find(".J-oldPrice").text());
    var itemTotal=0;
    if(hasPromotions($currentRow)){
      var newPrice=G.rounding(calculateOrderTotal($currentRow));
        if(hasPromotionsType($currentRow,"BARGAIN")){
            $currentRow.find(".purchasePrice").val(newPrice);
        }else if(hasPromotionsType($currentRow,"MLJ")){
            var productId=$currentRow.find(".productId").val();
            var promotions=getPromotions(productId,"MLJ");
            if(promotions.promotionsLimiter=="OVER_AMOUNT"){
                $currentRow.find(".purchasePrice").val(newPrice);
            }
        }
        var price=G.rounding($currentRow.find(".purchasePrice").val());
        itemTotal=G.rounding(price*amount);
    }else{
        itemTotal=G.rounding(oldPrice*amount);
    }
     $currentRow.find(".J-ItemTotal").text(itemTotal);
    calculateAllTotal();
}

function calculateAllTotal(){
    var total = 0;
    $("#promotionsTotal").val(0);
     $(".J-ShopDataBlock").each(function(){
         var blockTotal=0;
         var $table = $(this);

         $(this).find(".J-oldPrice").each(function(){
           var amount=G.rounding($(this).closest("tr").find(".J-ModifyAmount").val());
            blockTotal+= G.rounding($(this).text())*amount;
         });
         var promotionsTotal = calculatePromotionsTotalForShoppingCart($table);
         $("#promotionsTotal").val(G.add($("#promotionsTotal").val(), promotionsTotal));
         total += blockTotal;
     });
    total -= G.rounding($("#promotionsTotal").val());
    if($("#promotionsTotal").val() > 0){
        $("#promotionsTotalSpan").text($("#promotionsTotal").val());
        $("#promotionsTotalDiv").show();
    }else{
        $("#promotionsTotalDiv").hide();
    }
    $(".J-AllTotal").text(G.rounding(total));
}

function calculatePromotionsTotalForShoppingCart($table){
    var allPromotionsTotal = 0;
    var mljMap = $table.data("mljMap");
    //mlj
    var mljTotal=0;
    if(!G.isEmpty(mljMap)){
        var keyList=mljMap.keySet();
        for(var i=0;i<keyList.length;i++ ){
            mljTotal+=G.rounding(mljMap.get(keyList[i]));
        }
    }
    //bargain
    var bargainTotal=0;
    $table.find(".bargainSaveValue").each(function(){
         var amount=G.rounding($(this).closest("tr").find(".J-ModifyAmount").val());
         bargainTotal += $(this).val()*amount;
    });
    //特价和满立减的总和
    var promotionsTotal=G.add(mljTotal,bargainTotal);
    allPromotionsTotal += promotionsTotal;
    return allPromotionsTotal;
}

function emptyShoppingCartRender(){
    if($("#mainCartTable > tbody > tr").length > 1){
        return;
    }
    $("#mainCartTable > tbody").append($("<tr class='bg'><td colspan='7' style='text-align:center;'>购物车没有添加任何商品</td></tr>"));
    $("#bottomFloatBar").hide();
}
