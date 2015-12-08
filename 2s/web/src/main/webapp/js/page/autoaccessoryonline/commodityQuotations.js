function isBcgogoUserGuide(target) {
    var stack = guideTipDisplayManager.getChildren();
    for (var i = 0; i < stack.length; i++) {
        if (stack[i] == target) {
            return true;
        }
    }
    return $(target).hasClass("bcgogo-shadow") || $(target).hasClass("bcgogo-shadow-border");
}
var MORE_PRODUCT_APPLY = true;

$().ready(function () {

    //促销商品查询 全选所有促销类型
    $("#allChk").click(function(){
        $(".promotionsTypes,#allChk").attr("checked",$(this).attr("checked"));
        if($(this).attr("checked")){
            var promotionsType=new Array();
            $(".promotionsTypes").each(function(){
                promotionsType.push($(this).attr("promotionsTypes"));
            });
            $("#promotionsType").val(promotionsType.toString());
        }else{
            $("#promotionsType").val("");
        }

    });


    $(".promotionsTypes").click(function(){
        var promotionsType=new Array();
        var  promotionsTypesCount=0;
        $(".promotionsTypes").each(function(){
            if($(this).attr("checked")){
                promotionsTypesCount++;
                promotionsType.push($(this).attr("promotionsTypes"))
            }
        });
        if(promotionsTypesCount==$(".promotionsTypes").size()){
            $(".promotionsTypes,#allChk").attr("checked",true);
        }else{
            $("#allChk").attr("checked",false);
        }
        $("#promotionsType").val(promotionsType.toString());

    });
    /*------------------促销信息 start------------------------------ */
    $(".J-supplier").live("mouseenter",function(event){
        event.stopImmediatePropagation();

        var _currentTarget=$(event.target).parent().find(".alert");
        _currentTarget.show(80);

        _currentTarget.mouseleave(function (event) {
            if (event.relatedTarget != $(event.target).parent().parent().find(".J-image")[0] && !isBcgogoUserGuide(event.relatedTarget)) {
                _currentTarget.hide(80);
            }
        });
        if (MORE_PRODUCT_APPLY && userGuide.currentStepName == "PRODUCT_PRICE_GUIDE_MORE_PRODUCT") {
            MORE_PRODUCT_APPLY = false;
            userGuide.caller("MORE_PRODUCT_APPLY", "PRODUCT_PRICE_GUIDE");
        }
    }).live("mouseleave",function(event){
            event.stopImmediatePropagation();
            var _currentTarget=$(event.target).parent().find(".alert");

            if(event.relatedTarget!=_currentTarget[0] && !isBcgogoUserGuide(event.relatedTarget)) {
                _currentTarget.hide(80);
            }

        });

    provinceBind();
    $("#provinceNo").bind("change",function(){
        $(this).css("color", $(this).find("option:selected").css("color"));
        $("#cityNo option").not(".default").remove();
        $("#regionNo option").not(".default").remove();
        cityBind();
        $("#cityNo").change();
    });
    $("#cityNo").bind("change",function(){
        $(this).css("color", $(this).find("option:selected").css("color"));
        $("#regionNo option").not(".default").remove();
        regionBind();
        $("#regionNo").change();
    });

    $("#regionNo").bind("change",function(){
        $(this).css("color", $(this).find("option:selected").css("color"));
    });

    $("#clearConditionBtn").bind('click', function () {
        $(".J_clear_input").val("");
        $("#productCategoryIds").val("");
        $("#productCategorySelected").empty();
        $("#provinceNo").get(0).selectedIndex=0;
        $("#cityNo").get(0).selectedIndex=0;
        $("#regionNo").get(0).selectedIndex=0;
        $("#provinceNo,#cityNo,#regionNo").change();
        $(".J-initialCss").placeHolder("reset");
        $(".promotionsTypes,#allChk").attr("checked",false);
        $("#promotionsType").val("");
    });
    $(".J-apply").live("click",function(e){
        e.preventDefault();
        if(!G.Lang.isEmpty($(this).attr("data-shop-id"))){
            applySupplierRelation($(this).attr("data-shop-id"));
        }
    });
    //供应商下拉框
    $(".J-wholesalerSuggestion")
        .bind('click', function () {
            getSupplierOnlineSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getSupplierOnlineSuggestion($(this),eventKeyCode);
            }
        });


    function getSupplierOnlineSuggestion($domObject, keycode) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");

        var dropList = App.Module.droplist;
        dropList.setUUID(G.generateUUID());
        var ajaxData = {
            searchWord: searchWord,
            customerOrSupplier: "supplierOnline",
            shopRange:$("#shopRange").val(),
            uuid: dropList.getUUID()
        };
        var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierOnlineSuggestion";
        App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            if(!G.isEmpty(result.data[0])){
                G.completer({
                        'domObject':$domObject[0],
                        'keycode':keycode,
                        'title':result.data[0].label}
                );
            }
            dropList.show({
                "selector": $domObject,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.label);
                    $domObject.css({"color": "#000000"});
                    dropList.hide();
                }
            });
        });
    }

    //
    $(".J-productSuggestion")
        .bind('click', function () {
            productSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                productSuggestion($(this));
            }
        });
    function productSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var currentSearchField =  $domObject.attr("searchField");
        var ajaxData = {
            searchWord: searchWord,
            searchField: currentSearchField,
            uuid: dropList.getUUID()
        };
        $domObject.prevAll(".J-productSuggestion").each(function () {
            var val = $(this).val().replace(/[\ |\\]/g, "");
            if($(this).attr("name")!="searchWord"){
                ajaxData[$(this).attr("name")] = val == $(this).attr("initialValue") ? "" : val;
            }
        });

        var ajaxUrl = "product.do?method=searchWholeSalerProductInfo";
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            if (currentSearchField == "product_info") {
                dropList.show({
                    "selector": $domObject,
                    "autoSet": false,
                    "data": result,
                    onGetInputtingData: function() {
                        var details = {};
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var val = $(this).val().replace(/[\ |\\]/g, "");
                            details[$(this).attr("searchField")] = val == $(this).attr("initialValue") ? "" : val;
                        });
                        return {
                            details:details
                        };
                    },
                    onSelect: function (event, index, data, hook) {
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var label = data.details[$(this).attr("searchField")];
                            if (G.Lang.isEmpty(label) && $(this).attr("initialValue")) {
                                $(this).val($(this).attr("initialValue"));
                                $(this).css({"color": "#ADADAD"});
                            } else {
                                $(this).val(G.Lang.normalize(label));
                                $(this).css({"color": "#000000"});
                            }
                        });
                        dropList.hide();
                        $("#searchQuotationBtn").click();
                    },
                    onKeyboardSelect: function (event, index, data, hook) {
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var label = data.details[$(this).attr("searchField")];
                            if (G.Lang.isEmpty(label) && $(this).attr("initialValue")) {
                                $(this).val($(this).attr("initialValue"));
                                $(this).css({"color": "#ADADAD"});
                            } else {
                                $(this).val(G.Lang.normalize(label));
                                $(this).css({"color": "#000000"});
                            }
                        });
                    }
                });
            }else{
                dropList.show({
                    "selector": $domObject,
                    "data": result,
                    "onSelect": function (event, index, data) {
                        $domObject.val(data.label);
                        $domObject.css({"color": "#000000"});
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            clearSearchInputValueAndChangeCss(this);
                        });
                        dropList.hide();
                        $("#searchQuotationBtn").click();
                    }
                });
            }
        });
    }


    $("#searchWholeSalerStockForm").submit(function (e) {
        e.preventDefault();
        $(".J-initialCss").placeHolder("clear");

        var param = $("#searchWholeSalerStockForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            data[val.name] = val.value;
        });
        APP_BCGOGO.Net.syncPost({
            url: "autoAccessoryOnline.do?method=getCommodityQuotationsList",
            dataType: "json",
            data:data,
            success: function (result) {
                drawCommodityQuotationsTable(result);
                initPages(result, "commodityQuotations", "autoAccessoryOnline.do?method=getCommodityQuotationsList", '', "drawCommodityQuotationsTable", '', '', data, '');
                $(".J-initialCss").placeHolder("reset");
            },
            error: function () {
                $(".J-initialCss").placeHolder("reset");
                $("#commodityTable").find(".J-ItemBody").remove();
                $("#commodityTable").append($("<tr class='titBody_Bg J-ItemBody'><td colspan='7' style='text-align: left;margin-left: 10px;'><span style='margin-left: 10px;'>数据异常，请刷新页面！</span></td></tr>"));
                $("#totalNumber").text("0");
                nsDialog.jAlert("数据异常，请刷新页面！");
            }
        });

        return false;
    });

    $("#searchQuotationBtn").click(function () {
        $("#productIds").val("");//m站内消息跳转来才有  其他时候都要清除
        $("#shopId").val("");
        $("#fromSource").val("");
        $("#inSalesPriceStart").val($("#inSalesPriceStartInput").val());
        $("#inSalesPriceEnd").val($("#inSalesPriceEndInput").val());
        $(".J_span_sort_commodityQuotations").each(function () {
            $(this).removeClass("hover");
            $(this).find(".J_sort_span_image").removeClass("arrowUp").addClass("arrowDown");
            $(this).attr("currentSortStatus", "Desc");
            $(this).find(".alertBody").html($(this).attr("ascContact"));
        });

        $("#searchWholeSalerStockForm").submit();
    });

    $("#checkAll").bind("click",function() {
        $("#floatBarSelectAll").attr("checked",this.checked);
        $("input[name='selectProduct']").attr("checked",this.checked);
    });
    $("#floatBarSelectAll").bind("click", function(){
        $("#checkAll").attr("checked",this.checked);
        $("input[name='selectProduct']").attr("checked",this.checked);
    });

    $("input[name='selectProduct']").live("click",function() {
        var checked = $("input[name='selectProduct']").length == $("input[name='selectProduct']:checked").length ? true : false;
        $("#checkAll").attr("checked",checked);
    });

    $(".J-addToShoppingCartSingleBtn").live("click",function(e){
        e.preventDefault();
        var $domObject = $(this).closest("tr").find("input[name='selectProduct']");
        var productId = $domObject.attr("data-productlocalinfoid");
        var amount = G.normalize($(this).closest("tr").find(".J-Amount").val(),"1");
        var supplierShopId=$domObject.attr("data-suppliershopid");
        var paramString = supplierShopId+"_"+productId+"_"+amount;
        addProductToShoppingCart(paramString);
    });

    //加入购物车
    $("#addToShoppingCartBtn, #floatBarAddToShoppingCartBtn").bind("click",function(e){
        e.preventDefault();
        //遍历页面勾选的checkbox，取出所有选中的productId
        var paramString = [];
        $("input[name='selectProduct']:checked").each(function(){
            var supplierShopId=$(this).attr("data-suppliershopid");
            var productId=$(this).attr("data-productlocalinfoid");
            var amount= G.normalize($(this).closest("tr").next(".J-ItemBody").find(".J-Amount").val(),"1");
            paramString.push(supplierShopId+"_"+productId+"_"+amount);
        });
        if (paramString.length==0) {
            nsDialog.jAlert("请选择需要加入购物车的商品!");
        } else {
            addProductToShoppingCart(paramString.join(","));
        }
    });

    $(".J-purchaseSingleBtn").live("click",function(e){
        e.preventDefault();
        var productId = $(this).closest("tr").find("input[name='selectProduct']").attr("data-productlocalinfoid");
        var amount = 1;
        if(!G.Lang.isEmpty(productId)){
            purchaseProduct(productId,amount);
        }
    });
    //下采购单
    $("#purchaseBtn, #floatBarPurchaseBtn").click(function () {
        //遍历页面勾选的checkbox，取出所有选中的productId
        var productIds = [],amounts=[];
        $("input[name='selectProduct']:checked").each(function(){
            productIds.push($(this).attr("data-productlocalinfoid"));
            amounts.push(1);
        });

        if (productIds.length==0) {
            nsDialog.jAlert("请选择需要采购的商品!");
        } else {
            purchaseProduct(productIds.join(","),amounts.join(","));
        }
    });



    $("#goShoppingCartBtn").bind("click",function(e){
        e.preventDefault();
        window.location.href="shoppingCart.do?method=shoppingCartManage";
    });
    $("#closeBtn").bind("click",function(e){
        e.preventDefault();
        $("#returnDialog").dialog("close");
    });

    $("#inSalesPriceStartInput,#inSalesPriceEndInput").bind("click",function(e){
        e.preventDefault();
        $("#inSalesPriceDiv").show();
    });
    $("#inSalesPriceClearBtn").bind("click",function(e){
        e.preventDefault();
        $("#inSalesPriceStartInput,#inSalesPriceEndInput").val("");
        $("#searchQuotationBtn").click();
    });
    $("#inSalesPriceSureBtn").bind("click",function(e){
        e.preventDefault();
        var start = $("#inSalesPriceStartInput").val();
        var end = $("#inSalesPriceEndInput").val();
        if (!G.Lang.isEmpty(start) && !G.Lang.isEmpty(end) && Number(start) > Number(end)) {
            $("#inSalesPriceStartInput").val(end);
            $("#inSalesPriceEndInput").val(start);
        }
        $("#searchQuotationBtn").click();
    });

    $("#inSalesPriceListNum>span").click(function(e) {
        var idStr = $(this).attr("id");
        if (idStr == "inSalesPrice_1") {
            $("#inSalesPriceStartInput").val("");
            $("#inSalesPriceEndInput").val(1000);
        } else if (idStr == "inSalesPrice_2") {
            $("#inSalesPriceStartInput").val(1000);
            $("#inSalesPriceEndInput").val(5000);
        } else if (idStr == "inSalesPrice_3") {
            $("#inSalesPriceStartInput").val(5000);
            $("#inSalesPriceEndInput").val(10000);
        } else if (idStr == "inSalesPrice_4") {
            $("#inSalesPriceStartInput").val(10000);
            $("#inSalesPriceEndInput").val("");
        }
        $("#searchQuotationBtn").click();
    });

    $("#inSalesPriceStartInput,#inSalesPriceEndInput").bind("keyup blur", function (event) {
        if (event.type == "focusout") {
            event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
        } else if (event.type == "keyup") {
            if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
                event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
            }
        }
    });
    $(document).click(function (event) {
        var target = event.target;
        if (!target || !target.id || (target.id != "inSalesPriceStartInput" && target.id != "inSalesPriceEndInput")) {
            $("#inSalesPriceDiv").hide();
        }
    })

    if ($("#recommendProductDiv") && $("#recommendProductDiv").length > 0) {
        APP_BCGOGO.Net.asyncPost({
            url: "supplyDemand.do?method=getProductRecommendByShopId",
            data: {pageSize:4},
            cache: false,
            dataType: "json",
            success: function (data) {
                initRecommendAccessory(data);
            }
        });

        function initRecommendAccessory(json) {
            var size = 3;
            if (json == null || json.length == 0 ) {
                $(".J_recommendProductShow").hide();
                return;
            }

            var length = json.length;
            var liSize = length < size ? 1 : parseInt((length / size));
            var ulHtml = '<ul class="JScrollGroup">';
            for (var liIndex = 0; liIndex < liSize; liIndex ++) {
                ulHtml += '<li id="li_' + liIndex + '" class="JScrollItem">';
                ulHtml += '<div class="details-group">';
                for (var index = 0; index < size; index ++) {
                    var s = liIndex * size + index;
                    if (s >= length) {
                        continue;
                    }
                    var productDTO = json[s];
                    var imageURL = productDTO.imageCenterDTO.recommendProductListImageDetailDTO.imageURL
                    var productLocalInfoIdStr = productDTO.productLocalInfoIdStr;
                    var productInfo = productDTO.productInfo;
                    var shopIdStr = productDTO.shopIdStr;
                    var shopName = productDTO.shopName;
                    var isLocalCity = productDTO.localCity;
                    var shopAreaInfo = productDTO.shopAreaInfo;
                    var hasBargain = productDTO.hasBargain;
                    var inSalesPrice = G.Lang.normalize(productDTO.inSalesPrice, "0");
                    var inSalesPriceAfterCal = G.rounding(productDTO.inSalesPriceAfterCal);
                    var unit = G.Lang.isEmpty(productDTO.unit) ? "" : ("/" + productDTO.unit);
                    //promotions info
                    var promotionsList=productDTO.promotionsDTOs;
                    var promotionsing=!G.isEmpty(promotionsList);  //判断商品是否正在促销
                    var pTitle=generateSimplePromotionsAlertTitle(productDTO,"search");
                    ulHtml += '<div class="group-item fl ">';
                    ulHtml += '<div class="item-img fl">';
                    if(promotionsing){
                        ulHtml += '<div class="img-comment '+(pTitle.indexOf("特价")>-1?"tip-note-special-offers":"tip-note-delivery")+'">'+pTitle+'</div>';
                    }
                    ulHtml += '<div class="img-main">';
                    ulHtml += '<a style="cursor: pointer" data-productlocalinfoid="'+productLocalInfoIdStr+'" data-suppliershopid="'+shopIdStr+'" class="J_showProductInfo">';
                    ulHtml += '<img src="'+imageURL+'" />';
                    ulHtml += '</a>';
                    ulHtml += '</div>';
                    ulHtml += '</div>';
                    ulHtml += '<div class="item-info fl">';
                    ulHtml += '<div class="info-name fl"><a style="word-wrap:break-word" data-productlocalinfoid="'+productLocalInfoIdStr+'" data-suppliershopid="'+shopIdStr+'" class="blue_color J_showProductInfo">'+productInfo+'</a></div>';
                    if(hasBargain){
                        ulHtml += '<div>';
                        ulHtml += '<span class="promotion-original-price">&yen;'+inSalesPrice+'</span>';
                        ulHtml += '<span style="padding-left:2px;color:#ff6600;font-weight: bold;" class="">&yen;'+inSalesPriceAfterCal+'</span>' ;
                        ulHtml += '</div>';
                    }else{
                        ulHtml += '<div class="info-price fl">&yen;'+inSalesPrice+'</div>';
                    }
                    ulHtml += '<div class="cl"></div>';
                    ulHtml += '</div>';
                    ulHtml += '<div class="cl"></div>';
                    ulHtml += '</div>';
                }

                ulHtml += '<div class="cl"></div>';
                ulHtml += '</div>';
                ulHtml += '</li>';
            }
            ulHtml += '</ul>';
            $("#recommendProductDiv").html(ulHtml);

            // scroll bar
            var scrollHorizontal = new App.Module.ScrollFlowHorizontal(),
                $content = $(".content-main.parts-quoted-price .matched-recommend"),
                contentW = $content.width();

            scrollHorizontal.init({
                selector:"#recommendProductDiv",
                width:contentW,
                height:90,
                background:"#fff",
                scrollInterval:3 * 1000
            }).startAutoScroll();

            $(".J_recommendProductShow").show();
        }
    }

    $(".J_addToShoppingCartSingleBtn").live("click",function(e){
        e.preventDefault();
        var productId = $(this).attr("data-productlocalinfoid");
        var amount = G.normalize($(this).closest("tr").find(".J-Amount").val(),"1");
        var supplierShopId=$(this).attr("data-suppliershopid");
        var paramString = supplierShopId+"_"+productId+"_"+amount;
        addProductToShoppingCart(paramString);
    });
    $(".J_showProductInfo").live("click",function(e){
        e.preventDefault();
        var viewedProductLocalInfoId = $(this).attr("data-productlocalinfoid");
        var viewedSupplierShopId = $(this).attr("data-suppliershopid");
        window.open("shopProductDetail.do?method=toShopProductDetail&paramShopId="+viewedSupplierShopId+"&productLocalId="+viewedProductLocalInfoId);
    });
});


function drawCommodityQuotationsTable(json) {
    $("#commodityTable").find(".J-ItemBody").remove();
    $("#checkAll").attr("checked",false);
    if(json==null || json[0].data==null || json[0].data.products == null || json[0].data.products.length == 0 ){
        $("#commodityTable").append($("<tr class='titBody_Bg J-ItemBody'><td colspan='7' style='text-align: left;margin-left: 10px;'><span style='margin-left: 10px;'>暂无符合条件的商品报价!</span></td></tr>"));
        $("#totalNumber").text("0");
        return;
    }
    $("#totalNumber").text(json[0].data.numFound);
    $.each(json[0].data.products, function (index, productDTO) {

        var imageCenterDTO = productDTO.imageCenterDTO;
        var imageSmallURL = imageCenterDTO.productListSmallImageDetailDTO.imageURL;
        var commodityCode = productDTO.commodityCode == null ? "" : productDTO.commodityCode+" ";
        var productLocalInfoIdStr = productDTO.productLocalInfoIdStr;
        var productName = productDTO.name == null ? "" : productDTO.name+" ";
        var productBrand = productDTO.brand == null ? "" : productDTO.brand+" ";
        var productSpec = productDTO.spec == null ? "" : productDTO.spec+" ";
        var productModel = productDTO.model == null ? "" : productDTO.model;
        var productVehicleBrand = productDTO.productVehicleBrand == null ? "" : productDTO.productVehicleBrand+" ";
        var productVehicleModel = productDTO.productVehicleModel == null ? "" : productDTO.productVehicleModel;
        var inSalesAmount = productDTO.inSalesAmount == null ? "" : dataTransition.rounding(productDTO.inSalesAmount, 1);
        var inSalesPrice = productDTO.inSalesPrice == null ? "" : dataTransition.rounding(productDTO.inSalesPrice, 2);
        var sellUnit = productDTO.sellUnit == null ? "" : productDTO.sellUnit;
        var inSalesUnit = productDTO.inSalesUnit == null? "" : productDTO.inSalesUnit;
        var shopName = productDTO.shopName == null ? "" : productDTO.shopName;
        var shopAreaInfo = productDTO.shopAreaInfo == null ? "" : productDTO.shopAreaInfo;
        var shopId = productDTO.shopIdStr==null?"":productDTO.shopIdStr;
        var shopContactQQs = productDTO.shopContactQQs;

//        var productInfo = commodityCode+"<span style='font-weight: bold;'>"+productName+productBrand+"</span>"+productSpec+productModel+productVehicleBrand+productVehicleModel;
        var productInfo = commodityCode+productName+productBrand+productSpec+productModel+" "+productVehicleBrand+productVehicleModel;

        var supplierCommentStatDTO = productDTO.supplierCommentStatDTO;
        var totalAverageScore = supplierCommentStatDTO?(supplierCommentStatDTO.totalScore ? App.StringFilter.priceFilter(supplierCommentStatDTO.totalScore, 1) : "0"):"0";
        var qualityAverageScore = supplierCommentStatDTO?(supplierCommentStatDTO.qualityTotalScore ? App.StringFilter.priceFilter(supplierCommentStatDTO.qualityTotalScore, 1) : "0"):"0";
        var performanceAverageScore = supplierCommentStatDTO?(supplierCommentStatDTO.performanceTotalScore ? App.StringFilter.priceFilter(supplierCommentStatDTO.performanceTotalScore, 1) : "0"):"0";
        var speedAverageScore = supplierCommentStatDTO?(supplierCommentStatDTO.speedTotalScore ? App.StringFilter.priceFilter(supplierCommentStatDTO.speedTotalScore, 1) : "0"):"0";
        var attitudeAverageScore = supplierCommentStatDTO?(supplierCommentStatDTO.attitudeTotalScore ? App.StringFilter.priceFilter(supplierCommentStatDTO.attitudeTotalScore, 1) : "0"):"0";
        var commentRecordCount = supplierCommentStatDTO?(supplierCommentStatDTO.recordAmount ? App.StringFilter.priceFilter(supplierCommentStatDTO.recordAmount, 1) : "0"):"0";
        var totalAverageScoreStr = totalAverageScore + '分';
        var idStr = "relatedSupplier" + index;
        //promotions info
        filterUsingPromotion(productDTO,"search");
        var promotionsList=productDTO.promotionsDTOs;
        var promotionsing=!G.isEmpty(promotionsList);  //判断商品是否正在促销
        var tr = '<tr class="J-ItemBody">';
        tr += '<td class="item-checkbox">';
        tr += '    <input type="checkbox" name="selectProduct" data-suppliershopid="'+shopId+'" data-productlocalinfoid="' + productLocalInfoIdStr + '" />';
        tr += '</td>';
        tr += '<td class="item-product-information">';
        tr += '  <div class="product-icon">';
        tr += '      <a style="cursor: pointer" class="J_showProductInfo" data-suppliershopid="'+shopId+'" data-productlocalinfoid="' + productLocalInfoIdStr + '"><img src="'+imageSmallURL+'"/></a>';
        tr += '  </div>';
        tr += '  <div class="product-info-details">';
        tr += '  <a class="blue_color line J_showProductInfo" data-suppliershopid="'+shopId+'" data-productlocalinfoid="' + productLocalInfoIdStr + '" title="'+productInfo.trim()+'">'+productInfo.trim()+'</a>';
        tr += '  </div>';
        tr += '  <div class="cl"></div>';
        tr += '</td>';
        tr += '<td class="item-product-price promotions_info_td">';
        if(promotionsing){
            var newPrice = _calculateBargainPrice(productDTO, inSalesPrice);
            if(newPrice < inSalesPrice){
                tr += '<a style="color: #848484;text-decoration: line-through;">&yen;'+inSalesPrice+'</a>';
                tr += '<a style="margin-left:5px;" class="J-newPrice yellow_color">&yen;'+ newPrice + '</a>';
            }else{
                tr += '<span class="arialFont yellow_color">&yen;'+inSalesPrice+'</span><br>';
            }
            var pTitle=generatePromotionsAlertTitle(productDTO,"search");
            tr += pTitle;
        }else{
            tr += '<span class="arialFont yellow_color">&yen;'+inSalesPrice+'</span><br>';
        }
        tr += '</td>';
        tr += '<td class="item-product-supplier">';
//        tr += '  <div style="margin-top: 10px;margin-bottom: 10px;">';
        tr += '<a class="blue_color line" title="' + shopName + '" onclick="redirectShopCommentDetail(\'' + shopId + '\')">'+shopName+'</a>&nbsp;';
        tr += '<a class="J_QQ"></a>';
        if(supplierCommentStatDTO && totalAverageScore*1>0.001){
            tr += '<div class="divStar" style="cursor: pointer;" id="' + idStr + '" onmouseover="showSupplierCommentScore(this' + ',' + totalAverageScore + ','
                + commentRecordCount + ',' + qualityAverageScore + ',' + performanceAverageScore + ',' + speedAverageScore
                + ',' + attitudeAverageScore + ');" onmouseout="scorePanelHide();" ' + 'onclick="redirectShopCommentDetail(\'' + shopId + '\', \'comment\')">';
            tr += '<a class="picStar" style=" background-position: 0px -'+supplierCommentStatDTO.totalScoreSpan+'px;"></a>';
            tr += '<b class="yellow_color" >' + totalAverageScoreStr + '</b>&nbsp;<span class="gray_color">共<span>'+commentRecordCount+'</span>人评分</span>';
            tr += '</div>';
        }else{
            tr += '<div class="gray_color line_gray">';
            tr += '暂无评分';
            tr += '</div>';
        }
//        tr += '  </div>';
        tr += '</td>';
        tr += '<td class="item-product-area">'+shopAreaInfo+'</td>';
        if(inSalesAmount==-1){
            tr += '<td class="item-product-added-count">'+'有货'+'</td>';
        }else{
            tr += '<td class="item-product-added-count">'+inSalesAmount + '&nbsp;' + inSalesUnit+'</td>';
        }
        tr += '<td class="item-product-operating">';
        tr += ' <div class="button-purchase button-grey-light-gradient txtc J-purchaseSingleBtn">立即采购</div>';
        tr += ' <div class="button-shopping-cart-added button-grey-light-gradient txtc J-addToShoppingCartSingleBtn">加入购物车</div>';
        tr += '</td>';
        tr += '</tr>';


        $("#commodityTable").append($(tr));
        $("#commodityTable").find("tr:last").find(".J_QQ").multiQQInvoker({
            QQ:shopContactQQs
        });
    });
//    $(".J_QQ").each(function(index,val){
//       $(this).multiQQInvoker({
//           QQ:$.fn.multiQQInvoker.getContactQQ()
//       });
//    });
//initPromotionsInfo();
}

function applySupplierRelation(supplerShopIds){
    if(!G.Lang.isEmpty(supplerShopIds)){
        APP_BCGOGO.Net.syncPost({
            url: "apply.do?method=applySupplierRelation",
            dataType: "json",
            data: {
                supplerShopId:supplerShopIds
            },
            success: function (json) {
                if (json.success) {
                    //
                    $(".J-ItemBody").each(function(e){
                        if(supplerShopIds.indexOf(",")>-1){
                            if(supplerShopIds.indexOf($(this).find(".J-apply").attr("data-shop-id"))>-1){
                                $(this).find(".alert").hide(80);
                                $(this).find(".J-applyAlertBody").html("已经发送关联申请！");
                            };
                        }else{
                            if(supplerShopIds==$(this).find(".J-apply").attr("data-shop-id")){
                                $(this).find(".alert").hide(80);
                                $(this).find(".J-applyAlertBody").html("已经发送关联申请！");
                            };
                        }

                    });
                    nsDialog.jAlert("您提交的申请成功,请等待对方同意!");
                } else {
                    nsDialog.jAlert(json.msg);
                }
            },
            error: function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    }
}
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

//以前非关联的供应商不能立即采购
//function purchaseProduct(productLocalInfoIds,amounts){
//    APP_BCGOGO.Net.syncPost({
//        url: "autoAccessoryOnline.do?method=validatorPurchaseProduct",
//        dataType: "json",
//        data: {
//            productLocalInfoIds: productLocalInfoIds,
//            amounts:amounts
//        },
//        success: function (json) {
//            if (json.success) {
//                if(userGuide.currentStepName == "PRODUCT_PRICE_GUIDE_PURCHASE"){
//                    window.location = "RFbuy.do?method=createPurchaseOrderOnlineByProduct&paramString="
//                        + json.data.join(",") + "&currentStepName=PRODUCT_PRICE_GUIDE_PURCHASE";
//                }else{
//                    window.location = "RFbuy.do?method=createPurchaseOrderOnlineByProduct&paramString=" + json.data.join(",");
//                }
//
//            } else {
//                if (json.operation=="CONFIRM_RELATED_SUPPLIER") {
//                    nsDialog.jConfirm(json.msg, null, function (returnVal) {
//                        if (returnVal) {
//                            applySupplierRelation(json.data);
//                        }
//                    });
//                }else{
//                    nsDialog.jAlert(json.msg);
//                }
//            }
//        },
//        error: function () {
//            nsDialog.jAlert("数据异常!");
//        }
//    });
//}

function purchaseProduct(productLocalInfoIds,amounts){
    APP_BCGOGO.Net.syncPost({
        url: "autoAccessoryOnline.do?method=validatorPurchaseProduct",
        dataType: "json",
        data: {
            productLocalInfoIds: productLocalInfoIds,
            amounts:amounts
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

function cityBind() {
    var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
        data: {"parentNo": $("#provinceNo").val()}, dataType: "json"});
    if (!r || r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            option.style.color="#000000";
            $("#cityNo")[0].appendChild(option);
        }
    }
}

function regionBind() {
    var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
        data: {"parentNo": $("#cityNo").val()}, dataType: "json"});
    if (!r || r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            option.style.color="#000000";
            $("#regionNo")[0].appendChild(option);
        }
    }
}
function provinceBind() {
    var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
        data: {"parentNo":"1"}, dataType: "json"});
    if (!r || r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            option.style.color="#000000";
            if($("#provinceNo")[0]){
                $("#provinceNo")[0].appendChild(option);
            }
        }
    }
}