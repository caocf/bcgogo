$(function(){
    $("#q_clear_condition_btn").bind('click', function () {
        $(".J_clear_input").val("");
        $(".J-initialCss").placeHolder("reset");
    });
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
        $domObject.prevAll(".J-productSuggestion").each(function(){
            var val = $(this).val().replace(/[\ |\\]/g, "");
            if($(this).attr("name")!="searchWord"){
                ajaxData[$(this).attr("name")] = val == $(this).attr("initialValue") ? "" : val;
            }
        });

        var ajaxUrl = "product.do?method=getProductSuggestion";
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
                        $("#searchConditionBtn").click();
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
                        $("#searchConditionBtn").click();
                    }
                });
            }
        });
    }
    $(".J-initialCss").placeHolder();
});

var newProductCache = {};
$(function(){

    $(".quoted-product-detail [name='commodityCode']").live("keyup",function () {
        var pos = APP_BCGOGO.StringFilter.getCursorPosition(this, APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter);
        $(this).val(APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter($(this).val()));
        APP_BCGOGO.StringFilter.setCursorPosition(this, pos);
    }).live("blur", function () {
            var $thisDom = $(this);
            $thisDom.val(APP_BCGOGO.StringFilter.commodityCodeFilter($thisDom.val()));
            var validateCommodityCode = $thisDom.val();
            if ($(this).val() && $(this).val() != $(this).attr("lastValue")) {
                var ajaxUrl = "stockSearch.do?method=ajaxToGetProductByCommodityCode";
                var ajaxData = {
                    commodityCode: validateCommodityCode
                };
                APP_BCGOGO.Net.syncAjax({
                    url: ajaxUrl,
                    dataType: "json",
                    data: ajaxData,
                    success: function (jsonStr) {
                        if (jsonStr[0] && jsonStr[0].productIdStr) {
                            nsDialog.jAlert("商品编码【" + validateCommodityCode + "】已经使用，请重新输入商品编码", null, function () {
                                $thisDom.val("");
                            });
                        }
                    },
                    error: function () {
                        nsDialog.jAlert("网络异常，请联系管理员！", null, function () {
                            $thisDom.val("");
                        })
                    }
                });
            }
        });


    $('.quoted-product-detail [name="vehicleBrand"]').live("keyup click", function (e) {
        e.stopImmediatePropagation();
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
            var searchField = $thisDom.attr("searchField");
            var cacheField = $thisDom.attr("cacheField");
            var thisDomVal = $thisDom.val();
            if (newProductCache.isTimeOut($thisDom.val(), cacheField)) {
                var ajaxData = {
                    searchWord: $thisDom.val(),
                    searchField: searchField,
                    uuid: GLOBAL.Util.generateUUID()
                };
                var ajaxUrl = "product.do?method=searchBrandSuggestion";
                APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
                    var source = [];
                    if (jsonStr && jsonStr.length > 0) {
                        for (var i = 0, len = jsonStr.length; i < len; i++) {
                            if (!G.Lang.isEmpty(jsonStr[i].name)) {
                                source.push(APP_BCGOGO.StringFilter.inputtingProductNameFilter(jsonStr[i].name));
                            }
                        }
                    }
                    newProductCache[cacheField].data[thisDomVal] = {};
                    newProductCache[cacheField].data[thisDomVal]["source"] = source;
                    newProductCache[cacheField].data[thisDomVal]["syncTime"] = (new Date()).valueOf();
                    if ($(document.activeElement).attr("name") == $thisDom.attr("name")) {
                        $thisDom.autocomplete({
                            minLength: 0,
                            delay: 0,
                            source: source
                        });
                        $thisDom.autocomplete("search", "");
                    }
                })
            } else {
                $thisDom.autocomplete({
                    minLength: 0,
                    delay: 0,
                    source: newProductCache[cacheField].data[$thisDom.val()]["source"]
                });
                $thisDom.autocomplete("search", "");
            }
        }
    }).live("blur",function () {
            if ($(this).val() != $(this).attr("lastVal")) {
                $('[name="productVehicleModel"]').val("");
            }
        }).live("focus", function () {
            $(this).attr("lastVal", $(this).val());
        });

    $(".quoted-product-detail [name='vehicleModel']").live("keyup click",function(e){
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
            var searchField = $thisDom.attr("searchField");
            var cacheField = $thisDom.attr("cacheField");
            var brandVal = $('[name="productVehicleBrand"]').val();
            var sourceKey = brandVal + "_&&_" + $thisDom.val();
            if (newProductCache.isTimeOut(sourceKey, cacheField)) {
                var ajaxData = {
                    brandValue: brandVal,
                    searchWord: $thisDom.val(),
                    searchField: searchField,
                    uuid: GLOBAL.Util.generateUUID()
                };
                var ajaxUrl = "product.do?method=searchBrandSuggestion";
                APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
                    var source = [];
                    if (jsonStr && jsonStr.length > 0) {
                        for (var i = 0, len = jsonStr.length; i < len; i++) {
                            if (!G.Lang.isEmpty(jsonStr[i].name)) {
                                source.push(APP_BCGOGO.StringFilter.inputtingProductNameFilter(jsonStr[i].name));
                            }
                        }
                    }
                    newProductCache[cacheField].data[sourceKey] = {};
                    newProductCache[cacheField].data[sourceKey]["source"] = source;
                    newProductCache[cacheField].data[sourceKey]["syncTime"] = (new Date()).valueOf();
                    if ($(document.activeElement).attr("name") == $thisDom.attr("name")) {
                        $thisDom.autocomplete({
                            minLength: 0,
                            delay: 0,
                            source: source,
                            select:function(e,ui){
                                $.ajax({
                                    type: "POST",
                                    data:{
                                        modelValue:ui.item.value,
                                        searchField:"brand"
                                    },
                                    url: "product.do?method=searchBrandSuggestion",
                                    cache: false,
                                    dataType: "json",
                                    error: function (XMLHttpRequest, error, errorThrown) {
                                    },
                                    success: function (jsonStr) {
                                        if (jsonStr && jsonStr.length > 0 && !G.Lang.isEmpty(jsonStr[0].name)) {
                                            $('[name="productVehicleBrand"]').val(jsonStr[0].name);
                                        }
                                    }
                                });
                            }
                        });
                        $thisDom.autocomplete("search", "");
                    }
                })
            } else {
                $thisDom.autocomplete({
                    minLength: 0,
                    delay: 0,
                    source: newProductCache[cacheField].data[sourceKey]["source"]  ,
                    select: function (e, ui) {
                        $.ajax({
                            type: "POST",
                            data: {
                                modelValue: ui.item.value,
                                searchField: "brand"
                            },
                            url: "product.do?method=searchBrandSuggestion",
                            cache: false,
                            dataType: "json",
                            error: function (XMLHttpRequest, error, errorThrown) {
                            },
                            success: function (jsonStr) {
                                if (jsonStr && jsonStr.length > 0 && !G.Lang.isEmpty(jsonStr[0].name)) {
                                    $('[name="productVehicleBrand"]').val(jsonStr[0].name);
                                }
                            }
                        });
                    }
                });
                $thisDom.autocomplete("search", "");
            }
        }
    });

    $("#searchConditionForm .search-product-Btn").click(function () {
        $(".J-initialCss").placeHolder("clear");
        var param = $("#searchConditionForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            if (!G.Lang.isEmpty(data[val.name])) {
                data[val.name] = data[val.name] + "," + val.value;
            } else {
                data[val.name] = val.value;
            }
        });
        $(".J-initialCss").placeHolder("reset");
        var ajaxUrl = "preBuyOrder.do?method=quotedSelectProduct";

        APP_BCGOGO.Net.syncPost({
            url: ajaxUrl,
            data: data,
            dataType: "json",
            success: function (json) {
                _drawProductTable(json);
                initPages(json, "InSalesProduct", ajaxUrl, '', "_drawProductTable", '', '', data, '');
            }
        });
    });

    $(".J_doQuoted").live("click", function () {
        var preShopId = $(this).attr("data-shopId");
      var shopId=$("#shopId").val();
      if(preShopId==shopId){
        nsDialog.jAlert("不能给自己店铺报价!");
        return;
      }
        var preBuyOrderItemId = $(this).attr("data-itemId");
        if(G.isEmpty(preBuyOrderItemId)){
            return;
        }
        $("#selectQuotedProductDiv .pageNum").hide();
        var $tr = $(this).closest("tr");
        _selectQuotedProduct($tr,preBuyOrderItemId);

    });

    $("#quotedForNewProduct").live("click",function(){
        doQuotedPreBuyOrder();
    });

    function _selectQuotedProduct($tr,preBuyOrderItemId){
        $("#selectQuotedProductDiv").dialog({
            width: 730,
            zIndex: 20,
            modal: true,
            position:'top',
            beforeclose: function(event, ui) {
                $("#inSalesProductTable tr:not('.J_title')").remove();
                var dropList = APP_BCGOGO.Module.droplist;
                dropList.hide();
                $(".J_clear_input").val("");
                $(".j_clear_input").blur();
                $("#currentPreBuyOrderItemId").val("");
                return true;
            },
            open: function() {
                $(this).find("input[type='text']").first().blur();
                $("#currentPreBuyOrderItemId").val(preBuyOrderItemId);
                $('#searchConditionForm [name="productName"]').val($tr.find(".productName").val());
                $('#searchConditionForm [name="productBrand"]').val($tr.find(".brand").val());
                $('#searchConditionForm [name="productSpec"]').val($tr.find(".spec").val());
                $('#searchConditionForm [name="productModel"]').val($tr.find(".model").val());
                $('#searchConditionForm [name="productVehicleBrand"]').val($tr.find(".vehicleBrand").val());
                $('#searchConditionForm [name="productVehicleModel"]').val($tr.find(".vehicleModel").val());
                $('#searchConditionForm [name="commodityCode"]').val($tr.find(".commodityCode").val());
                $("#searchConditionForm .search-product-Btn").click();
            }
        });
    }

    $("input[name='arrivalTimeRadio']").bind("click",function(){
        if(!G.Lang.isEmpty($(this).val())){
            $("#arrivalTime").val($(this).val());
        }
    });

    $("#quotedBackSelectProductBtn").bind("click",function(e){
        var currentPreBuyOrderItemId = $("#quotedPreBuyOrderForm").find("input[id='preBuyOrderItemId']").val();
        $("#quotedPreBuyOrderDiv").dialog( "close" );

        $("#selectQuotedProductDiv").dialog({
            width: 730,
            zIndex: 20,
            modal: true,
            position:'top',
            beforeclose: function(event, ui) {
                $("#inSalesProductTable tr:not('.J_title')").remove();
                var dropList = APP_BCGOGO.Module.droplist;
                dropList.hide();
                $(".J_clear_input").val("");
                $(".j_clear_input").blur();
                $("#currentPreBuyOrderItemId").val("");
                return true;
            },
            open: function() {
                $(this).find("input[type='text']").first().blur();
                $("#currentPreBuyOrderItemId").val(currentPreBuyOrderItemId);

                $("#searchConditionBtn").click();
            }
        });
    });

    $("#quotedFirstNextBtn").bind("click",function(e){
        var productLocalInfoId = $("input[name='quotedProductId']:checked").val();
        if(G.Lang.isEmpty(productLocalInfoId)){
            nsDialog.jAlert("请选择报价商品!");
            return;
        }
        doQuotedPreBuyOrder(productLocalInfoId);
    });

    $('[field="inSalesAmount"]').blur(function(){
        $('[name="inSalesAmount"]').val(G.rounding($(this).val()));
    });

    $('[name="radio-inSalesAmount"]').click(function(){
        if($(this).attr("field")=="radio-inSalesAmount-exist"){
            $('[name="inSalesAmount"]').val(-1);
        }else{
            $('[name="inSalesAmount"]').val($('[field="inSalesAmount"]').val());
        }
    });

    $("#saveQuotedBtn").bind("click",function(e){
        if($(this).attr("disabled")){
            return;
        }
        $(this).attr("disabled", true);

        var productId=$("#quotedPreBuyOrderForm").find("input[id='productId']").val();
        //新商品
        if(G.isEmpty(productId)){
            var productName= $('.product-info-new [name="productName"]').val();
            if(G.isEmpty(productName)){
                nsDialog.jAlert("商品名不能为空。");
                $(this).removeAttr("disabled");
                return;
            }
        }
        if($("#salesStatus").val()!="InSales"){
            var inSalesAmount=G.rounding($('[name$="inSalesAmount"]').val());
            if($('[field="radio-inSalesAmount-amount"]').attr("checked")&&inSalesAmount<=0){
                nsDialog.jAlert("上架量应大于0。");
                $(this).removeAttr("disabled");
                return;
            }
            var unit=$('[name="unit"]').val();
            if(G.isEmpty(unit)){
                nsDialog.jAlert("上架单位不能为空。");
                $(this).removeAttr("disabled");
                return;
            }
            var categoryName = $("#productCategorySelector #_productCategorySelectedName").val();
            if(G.isEmpty(categoryName)){
                nsDialog.jAlert("请选择二级或者三级商品分类！");
                $(this).removeAttr("disabled");
                return;
            }
        }
        if(G.Lang.isEmpty($(".J_QuotedPrice").val())){
            nsDialog.jAlert("请填写具体报价！");
            $(this).removeAttr("disabled");
            return;
        }
        if(G.rounding($("#arrivalTime").val())==0){
            nsDialog.jAlert("请填写到货时间！");
            $(this).removeAttr("disabled");
            return;
        }
        var selectDataInfo = $("#productCategorySelector #_productCategorySelectedInfo").text();
        var categoryId = $("#productCategorySelector #_productCategorySelectedId").val();
        var categoryName = $("#productCategorySelector #_productCategorySelectedName").val();
        var categoryType = $("#productCategorySelector #_productCategorySelectedType").val();
        setSelectedProductCategoryData(selectDataInfo,categoryId,categoryName,categoryType);
        $("#quotedPreBuyOrderForm").ajaxSubmit({
            dataType: "json",
            success: function (json){
                if(!G.isEmpty(json)){
                    if (!json.success) {
                        nsDialog.jAlert(json.msg);
                        return;
                    }
                    $("#quotedPreBuyOrderDiv").dialog( "close" );
                    window.location.reload();
                }
            },
            error: function () {
                nsDialog.jAlert("出现异常，保存失败！");
                $(this).removeAttr("disabled");
            }
        });
    });



});

function _drawProductTable(data) {
    $("#inSalesProductTable tr:not('.J_title')").remove();
    var bottomTr='<tr class="titBottom_Bg"><td colspan="7"></td></tr>';
    if (data == null || data[0] == null || data[0].products == null || data[0].products.length == 0) {
        $("#inSalesProductTable").append("<tr class='titBody_Bg'><td style='padding-left:10px;' colspan='7'>对不起，没有您所需的商品，您可以新增商品报价！新增的商品将同时存入库存并上架！" +
            "<input type='button' class='txt' id='quotedForNewProduct' style='height: 22px; width: 80px; cursor: pointer;' value='新增商品报价'></td></tr>");
        $("#inSalesProductTable").append($(bottomTr));
        $("#quotedFirstNextBtn").hide();
        return;
    }
    $("#quotedFirstNextBtn").show();
    $.each(data[0].products, function(index, product) {
        var productLocalInfoIdStr = (!product.productLocalInfoIdStr ? "" : product.productLocalInfoIdStr);
        var commodityCode = (!product.commodityCode ? "" : product.commodityCode+" ");
        var productName = (!product.name ? "" : product.name+" ");
        var productBrand = (!product.brand ? "" : product.brand+" ");
        var productSpec = (!product.spec ? "" : product.spec+" ");
        var productModel = (!product.model ? "" : product.model+" ");
        var productVehicleBrand = (!product.productVehicleBrand ? "" : product.productVehicleBrand+" ");
        var productVehicleModel = (!product.productVehicleModel ? "" : product.productVehicleModel+" ");

        var productInfo =commodityCode+productName+productBrand+productSpec+productModel+productVehicleBrand+productVehicleModel;
        var inventoryNum = dataTransition.simpleRounding(G.Lang.normalize(product.inventoryNum,"0"), 1);
        var storageUnit = G.normalize(product.storageUnit);
        var sellUnit = G.normalize(product.sellUnit);
        var inSalesUnit = G.normalize(product.inSalesUnit);
        var rate = G.rounding(product.rate);
        var salesStatus = G.Lang.normalize(product.salesStatus,"");
        var salesStatusStr=(salesStatus=="InSales")?"已上架":"未上架";
        var inSalesPrice=G.rounding(product.inSalesPrice);
        var inventoryAveragePrice = dataTransition.simpleRounding(G.Lang.normalize(product.inventoryAveragePrice,"0"), 2);
        var tradePrice = dataTransition.simpleRounding(G.Lang.normalize(product.tradePrice,"0"), 2);
        var productCategoryId="";
        var productCategoryName="";
        var productCategoryType="";
        var selectDataInfo="";
        if(!G.isEmpty(product.productCategoryDTO)){
            productCategoryId=product.productCategoryDTO.idStr;
            productCategoryName=product.productCategoryDTO.name;
            productCategoryType=product.productCategoryDTO.categoryType;
            selectDataInfo=G.normalize(product.productCategoryInfo);
        }
        var tr = "<tr class='titBody_Bg'> ";
        tr +='<input type="hidden" name="unit" value="'+sellUnit+'">';
        tr +='<input type="hidden" name="sellUnit" value="'+sellUnit+'">';
        tr +='<input type="hidden" name="storageUnit" value="'+storageUnit+'">';
        tr +='<input type="hidden" name="rate" value="'+rate+'">';
        tr +='<input type="hidden" name="productCategoryId" value="'+productCategoryId+'">';
        tr +='<input type="hidden" name="productCategoryName" value="'+productCategoryName+'">';
        tr +='<input type="hidden" name="productCategoryType" value="'+productCategoryType+'">';
        tr +='<input type="hidden" name="selectDataInfo" value="'+selectDataInfo+'">';
        tr += "<td><input type='radio'class='radio' name='quotedProductId' data-sales-status='"+salesStatus+"' value='"+productLocalInfoIdStr+"'/></td>";
        tr += "<td style='padding-left:10px;' title='"+productInfo+"'>"+productInfo+"<input type='hidden' name='productInfo' value='"+productInfo+"'></td>";
        tr += "<td>"+inventoryNum+sellUnit+"<input type='hidden' name='inventoryNum' value='"+inventoryNum+"'></td>";
        tr += "<td><span class='arialFont'>&yen;</span>"+inventoryAveragePrice+"<input type='hidden' name='inventoryAveragePrice' value='"+inventoryAveragePrice+"'></td>";
        tr += "<td><span class='arialFont'>&yen;</span>"+tradePrice+"<input type='hidden' name='tradePrice' value='"+tradePrice+"'></td>";
        tr+="<td>"+salesStatusStr+"</td>";
        tr+="<td>"+inSalesPrice+"</td>";
        tr += "</tr>";

        $("#inSalesProductTable").append($(tr));
        $("#inSalesProductTable").append($(bottomTr));
    });
}