/*
 * 下拉建议自动补全（head 商品搜索框，首页商品搜索框，库存6框）
 * @author zhangjuntao
 */
//绑定事件
$(document).ready(function() {

    //显示初始值.
    $("#input_search_pName,#product_name2_id,#product_commodity_code,#product_brand_id,#product_spec_id,#product_model_id,#pv_brand_id,#pv_model_id,#supplierSearchText,.productSuggestion").placeHolder();

    //库存品名框.
    $("#input_search_pName,#product_name2_id,#product_commodity_code,#product_brand_id,#product_spec_id,#product_model_id,#pv_brand_id,#pv_model_id,#supplierSearchText,.productSuggestion").bind('click',function(e) {
        var _id = $(e.target).attr("id");
         var keycode= e.which || e.keyCode;
        if (_id == "input_search_pName" || _id == "product_name2_id" || $(this).hasClass("productSuggestion")) {
            productInfoSuggestionSearch(this, $(e.target).attr("inputtype"));
        } else {
            stockSuggestionSearch(this, $(e.target).attr("inputtype"));
        }
    }).bind('keyup',function(e) {
        if ($(this).attr("id").search(/^product_commodity_code$/g) != -1) {
            $(this).val(APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter($(this).val()));
        }
        if ((GLOBAL.Interactive.keyNameFromEvent(e)).search(/^left$|^right$|^up$|^down$|^shift$|^ctrl$|^alt$/g) != -1) return "";
        LazySearcherForProduct.inputSearchProductInfo(this, e);

        var strin = null;
    });
});

//inputType 判断哪个页面的input
var LazySearcherForProduct = function() {
        var inputtingTimerId;
        return {
            inputSearchProductInfo: function(domObj, e) {
                var inputType = $(e.target).attr("inputtype");
                var pageType = $("#pageType").val();
                var id = $(domObj).attr("id");
                if(!GLOBAL.Interactive.isKeyName(e, "enter")) {
                    if(inputtingTimerId) clearTimeout(inputtingTimerId);
                    inputtingTimerId = setTimeout(function() {
                        if(domObj != document.activeElement) return;
                        var firstProductJson = null;
                        //判断事件类型
                        if(id == "input_search_pName" || id == "product_name2_id" || $(domObj).hasClass("productSuggestion")) {
                            firstProductJson = productInfoSuggestionSearch(domObj, inputType);
                        } else {
                            firstProductJson = stockSuggestionSearch(domObj, inputType);
                        }
                        highLightForProduct(domObj, e, firstProductJson);
                    }, 400);
                    //回车 按键触发事件
                } else {
                    //查询中心
                    if(inputType == "head") {
                        //head商品中的搜索框
                        searchProductInfo(domObj);
                    } else if(inputType == "stocksearch") {
                        //触发 模糊库存查询
                        stockSearchBoxsAdjust(domObj);
                        initPagersOfStockSearch("StockSearchWithUnknownField");
                        getProductWithUnknownField();
                    }else{
                        if(id == "input_search_pName" || id == "product_name2_id" || $(domObj).hasClass("productSuggestion")) {
                            productInfoSuggestionSearch(domObj, inputType);
                        } else {
                            stockSuggestionSearch(domObj, inputType);
                        }
                    }
                }
            }
        }
    }();

function isBrowserSupportHighLight() {
    //    if (!(($.browser.msie && $.browser.version.search(/^9.0$/g) != -1 ) || $.browser.mozilla))return;
    return !$.browser.msie;

}
//高亮 自动补齐

function highLightForProduct(obj, e, firstProductJson) {
    if(!isBrowserSupportHighLight()) return;
    if((GLOBAL.Interactive.keyNameFromEvent(e)).search(/^left$|^right$|^up$|^down$|^backspace$|^shift$|^ctrl$|^alt$/g) != -1) return;
    var currentProduct = $(obj).val();
    var value = "";
    var firstProduct = "";
    if(firstProductJson != null) {
        // 循环取出匹配的值 自动补齐
        for(var j = 0; j < firstProductJson.length; j++) {
            if(firstProductJson[j][0] == "product_name") {
                firstProduct += " " + firstProductJson[j][1];
                if(isCompletelyMatching(currentProduct, firstProductJson[j][1])) {
                    value = firstProductJson[j][1];
                }
            }
            if(firstProductJson[j][0] == "product_brand") {
                firstProduct += " " + firstProductJson[j][1];
                if(isCompletelyMatching(currentProduct, firstProductJson[j][1])) {
                    value = firstProductJson[j][1];
                }
            }
            if(firstProductJson[j][0] == "product_spec") {
                firstProduct = " " + firstProductJson[j][1];
                if(isCompletelyMatching(currentProduct, firstProductJson[j][1])) {
                    value = firstProductJson[j][1];
                }
            }
            if(firstProductJson[j][0] == "product_model") {
                firstProduct = " " + firstProductJson[j][1];
                if(isCompletelyMatching(currentProduct, firstProductJson[j][1])) {
                    value = firstProductJson[j][1];
                }
            }
            if(firstProductJson[j][0] == "product_vehicle_brand") {
                firstProduct += " " + firstProductJson[j][1];
                if(isCompletelyMatching(currentProduct, firstProductJson[j][1])) {
                    value = firstProductJson[j][1];
                }
            }
            if(firstProductJson[j][0] == "product_vehicle_model") {
                firstProduct += " " + firstProductJson[j][1];
                if(isCompletelyMatching(currentProduct, firstProductJson[j][1])) {
                    value = firstProductJson[j][1];
                }
            }
            if(firstProductJson[j][0] == "commodity_code") {
                firstProduct += " " + firstProductJson[j][1];
                if(isCompletelyMatching(currentProduct, firstProductJson[j][1])) {
                    value = firstProductJson[j][1];
                }
            }
        }
        //匹配所有的field    防止需求修改
        //        if (firstProduct != null && isCompletelyMatching(currentProduct, firstProduct.trim())) {
        //            value = $.trim(firstProduct);
        //        }
        //默认匹配第一个值
        if(value != null && value.length > 0) {
            $(obj).val(value);
            obj.selectionStart = currentProduct.length;
        }
    }
}

//productInfo 下拉建议 搜索
function productInfoSuggestionSearch(domObject, inputType) {
    var searchWord = domObject.value;
    var searchField = $(domObject).attr("searchfield");
    if(!searchField) {
        searchField = "product_info";
    }
    var firstProduct = null;
    $.ajax({
        type: "POST",
        url: "product.do?method=searchProductInfoForStockSearch",
        async: false,
        data: {
            searchWord: searchWord,
            searchField: searchField
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            firstProduct = ajaxStyleForProduct(domObject, jsonStr, inputType);
        }

    });
    return firstProduct;
}

//库存 下拉建议
function stockSuggestionSearch(domObject, inputType,keycode) {
    var productName = $.trim($("#product_name2_id").val());
    var commodityCode = $.trim($("#product_commodity_code").val());
    var productBrand = $.trim($("#product_brand_id").val());
    var productSpec = $.trim($("#product_spec_id").val());
    var productModel = $.trim($("#product_model_id").val());
    var pvBrand = $.trim($("#pv_brand_id").val());
    var pvModel = $.trim($("#pv_model_id").val());
    productName = (productName == $("#product_name2_id").attr("initialValue") ? "" : productName);
    commodityCode = (commodityCode == $("#product_commodity_code").attr("initialValue") ? "" : commodityCode);
    productBrand = (productBrand == $("#product_brand_id").attr("initialValue") ? "" : productBrand);
    productSpec = (productSpec == $("#product_spec_id").attr("initialValue") ? "" : productSpec);
    productModel = (productModel == $("#product_model_id").attr("initialValue") ? "" : productModel);
    pvBrand = (pvBrand == $("#pv_brand_id").attr("initialValue") ? "" : pvBrand);
    pvModel = (pvModel == $("#pv_model_id").attr("initialValue") ? "" : pvModel);
    var searchWord = $.trim(domObject.value);
    var searchField = "";
    var domId = domObject.id;
    if(domId == "product_name2_id") {
        searchField = "product_info";
        productBrand = "";
        productSpec = "";
        productModel = "";
        commodityCode = "";
        productKind = "";
    } else if (domId == "product_brand_id") {
        searchField = "product_brand";
        productSpec = "";
        productModel = "";
        commodityCode = "";
        productKind = "";
    } else if (domId == "product_spec_id") {
        searchField = "product_spec";
        productModel = "";
        commodityCode = "";
        productKind = "";
    } else if (domId == "product_model_id") {
        searchField = "product_model";
    } else if(domId == "pv_brand_id") {
        searchField = "product_vehicle_brand";
        pvModel = "";
        commodityCode = "";
        productKind = "";
    } else if (domId == "pv_model_id") {
        searchField = "product_vehicle_model";
        productKind = "";
        commodityCode = "";
    } else if (domId == "product_commodity_code") {
        searchField = "commodity_code";
    } else {
        return null;
    }
    var firstProduct = null;
    if("product_name" == $(domObject).attr("searchfield")) {
        searchField = "product_name";
    }
    $.ajax({
        type:"POST",
        url:"product.do?method=searchProductInfoForStockSearch",
        async:false,
        data:{
            searchWord:searchWord,searchField:searchField,
            commodityCode:searchField == 'commodity_code' ? '' : commodityCode,
            productName:searchField == 'product_info' ? '' : productName,
            productBrand:searchField == 'product_brand' ? '' : productBrand,
            productSpec:searchField == 'product_spec' ? '' : productSpec,
            productModel:searchField == 'product_model' ? '' : productModel ,
            vehicleBrand:searchField == 'product_vehicle_brand' ? '' : pvBrand ,
            vehicleModel:searchField == 'product_vehicle_model' ? '' : pvModel
        },
        cache: false,
        dataType: "json",
        success: function(jsonStr) {
            if(!G.isEmpty(jsonStr[0])){
                G.completer({
                        'domObject':domObject,
                        'keycode':keycode,
                        'title':jsonStr[0].suggestionEntry[0][1]}
                );
            }
            firstProduct = ajaxStyleForProduct(domObject, jsonStr, inputType);
        }
    });
    return firstProduct;
}

//下拉建议 渲染 ajaxStyleForMain
function ajaxStyleForProduct(domObject, jsonStr, inputType) {
    var id = $(domObject).attr("id");
    var firstProduct = null;
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var width = $(domObject).width() + 50;
    $("#div_brand_head").css({
        'display': 'block',
        'position': 'absolute',
        'left': offset.left + 'px',
        'top': offset.top + offsetHeight + 3 + 'px',
        'color': "#000000",
        'height': 320 + 'px',
        'width': width + 'px'
    });
    $("#Scroller-Container_id_head").html("").css("width", "100%");
    for(var i = 0; i < jsonStr.length; i++) {
        var a = $("<a id=\"selectItem" + i + "\"></a>");
        var product = "";
        for(var j = 0; j < jsonStr[i].suggestionEntry.length; j++) {
            product += jsonStr[i].suggestionEntry[j][1] + " ";
        }
        if(i == 0) {
            firstProduct = jsonStr[i].suggestionEntry;
        }
        a.html($.trim(product)).css({
            "overflow": "hidden",
            "white-space": "nowrap",
            "margin": "0"
        }).attr("title", $.trim(product)).tooltip({
            "delay": 0
        });
        $(a).attr("productInfo", JSON.stringify(jsonStr[i].suggestionEntry)).attr("inputtype", $(domObject).attr("inputtype"));
        a.mouseover(function() {
            $("#Scroller-Container_id_head > a").removeAttr("class");
            $(this).attr("class", "hover");
        });
        a.click(function() {
            var product = $.parseJSON($(this).attr("productInfo"));
            var inputType = $(this).attr("inputtype");
            $(domObject).blur();
            $("#div_brand_head").css({
                'display': 'none'
            });
            //查询中心
            stockSearchBoxsAdjust(domObject);
            setToProductSearchInputs(product, inputType);
            if(inputType == "inquiryCenter") {
                //                stockSearchBoxsAdjust(domObject);
            } else if("stocksearch,head".search(inputType) != -1) {
                if(inputType == "stocksearch") {
                    if($(domObject).hasClass("productSuggestion")) {
                        $(domObject).val($(this).html());
                        $(domObject).css({
                            "color": "#000000"
                        });
                        $(domObject).change();
                    } else {
                        stockSearchBoxsAdjust(domObject);
                        setToProductSearchInputs(product, pageType);
                        iniProductWithCertainField();
                    }
                } else if(inputType == "head") {
                    //head 库存 逻辑
                    var param = "";
                    for(var j = 0; j < product.length; j++) {
                        if(product[j][0] == "product_name") {
                            param += '&productName=' + product[j][1];
                        }
                        if(product[j][0] == "product_brand") {
                            param += '&productBrand=' + product[j][1];
                        }
                        if(product[j][0] == "product_spec") {
                            param += '&productSpec=' + product[j][1];
                        }
                        if(product[j][0] == "product_model") {
                            param += '&productModel=' + product[j][1];
                        }
                        if(product[j][0] == "product_vehicle_brand") {
                            param += '&productVehicleBrand=' + product[j][1];
                        }
                        if(product[j][0] == "product_vehicle_model") {
                            param += '&productVehicleModel=' + product[j][1];
                        }
                        if (product[j][0] == "commodity_code") {
                            param += '&commodityCode=' + product[j][1];
                        }
                    }
                    var url = 'stockSearch.do?method=getStockSearch';
                    if($.trim(param) != "") {
                        url += $.trim(param);
                    }
                    window.location.assign(encodeURI(url));
                }
            }
        });
        if($(a).html() != "") {
            $("#Scroller-Container_id_head").append(a);
        }
    }
    return firstProduct;
}

//是否完全匹配
function isCompletelyMatching(str, regExp) {
    if(!regExp || !str || str.length > regExp.length) return false;
    else {
        regExp = regExp.substring(0, str.length);
        return regExp == str;
    }
}

//库存对应filed填充
function setToProductSearchInputs(product, inputType) {
    for(var j = 0; j < product.length; j++) {
        if(product[j][0] == "product_name") {
            $("#product_name2_id").val(product[j][1]);
            $("#product_name2_id").blur();
        }
        if(product[j][0] == "commodity_code") {
            $("#product_commodity_code").val(product[j][1]);
            $("#product_commodity_code").blur();
        }
        if(product[j][0] == "product_brand") {
            $("#product_brand_id").val(product[j][1]);
            $("#product_brand_id").blur();
        }
        if(product[j][0] == "product_spec") {
            $("#product_spec_id").val(product[j][1]);
            $("#product_spec_id").blur();
        }
        if(product[j][0] == "product_model") {
            $("#product_model_id").val(product[j][1]);
            $("#product_model_id").blur();
        }
        if(product[j][0] == "product_vehicle_brand") {
            $("#pv_brand_id").val(product[j][1]);
            $("#pv_brand_id").blur();
        }
        if(product[j][0] == "product_vehicle_model") {
            $("#pv_model_id").val(product[j][1]);
            $("#pv_model_id").blur();
        }
        if (product[j][0] == "commodity_code") {
            $("#product_commodity_code").val(product[j][1]);
            $("#product_commodity_code").blur();
        }
    }
}

//调整搜索框value 如果为空则 默认赋值（提示语）
function stockSearchBoxsAdjust(domObject) {
    if($(domObject).attr("id") == 'product_name2_id') {
        $("input[id='product_brand_id'],input[id='product_spec_id'],input[id='product_model_id'],input[id='pv_brand_id'],input[id='pv_model_id'],input[id='product_commodity_code']").each(function() {
            clearSearchInputValueAndChangeCss(this);
        })
    } else if($(domObject).attr("id") == 'product_brand_id') {
        $("input[id='product_spec_id'],input[id='product_model_id'],input[id='pv_brand_id'],input[id='pv_model_id'],input[id='product_commodity_code']").each(function() {
            clearSearchInputValueAndChangeCss(this);
        });
    } else if($(domObject).attr("id") == "product_spec_id") {
        $("input[id='product_model_id'],input[id='pv_brand_id'],input[id='pv_model_id'],input[id='product_commodity_code']").each(function() {
            clearSearchInputValueAndChangeCss(this);
        });
    } else if($(domObject).attr("id") == "product_model_id") {
        $("input[id='pv_brand_id'],input[id='pv_model_id'],input[id='product_commodity_code']").each(function() {
            clearSearchInputValueAndChangeCss(this);
        });
    } else if($(domObject).attr("id") == "pv_brand_id") {
        $("input[id='pv_model_id'],input[id='product_commodity_code']").each(function() {
            clearSearchInputValueAndChangeCss(this);
        });
    } else if($(domObject).attr("id") == "pv_model_id") {
        $("input[id='product_commodity_code']").each(function() {
            clearSearchInputValueAndChangeCss(this);
        });
    }
}

function clearSearchInputValueAndChangeCss(domObject) {
    if($(domObject).attr("initialValue")) {
        $(domObject).val($(domObject).attr("initialValue"));
        $(domObject).css({
            "color": "#ADADAD"
        });
    } else {
        $(domObject).val("");
    }
}

function clearLastSearchKey() {
    $("#product_name2_id").val("");
    $("#product_brand_id").val("");
    $("#product_spec_id").val("");
    $("#product_model_id").val("");
    $("#pv_brand_id").val("");
    $("#pv_model_id").val("");
}

//单击head商品搜索框
function searchProductInfo(dom) {
    var searchWord = $(dom).val() == $(dom).attr("initialvalue") ? "" : $(dom).val();
    window.location.href = encodeURI('stockSearch.do?method=getStockSearch&fuzzyMatchingFlag=true&searchWord=' + searchWord);
}