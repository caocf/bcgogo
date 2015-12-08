/**
 * @依赖 js/base.js;  js/application.js;  js/module/ajaxQuery.js;
 */
//TODO 处理入库单的SOLR搜索

var selectItemNum = -1;
var selectmore = -1;
var gsBrandId = -1;
var gsModelId = -1;
var gsYearId = -1;
var gsEngineId = -1;
var modelflag = new Array();
var brandModelMap = new Array();

var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

//TODO 处理包含商品信息的JSON，组装成下拉建议
function ajaxStyle(domObject, field, jsonStr, trcount) {
    OrderDropDownList.productNameAndBrand(domObject, field, jsonStr, trcount,"setDefaultAssociateInputValue");
}

//TODO 处理包含车型信息的JSON，组装成下拉建议
function ajaxStyleForGoodsBuyVehicle(domObject, searchField, jsonStr, trcount) {
    OrderDropDownList.vehicleBrand(domObject, searchField, jsonStr, trcount);
}

//TODO 相关方法已抽取到common.js下面的clearItemUtil下，(当用户选择车型后，带出其车辆品牌)
function searchSuggestionForVehicleAdjustment(domObject, brandValue, modelValue, yearValue, engineValue, trcount) { //车辆信息查询
    var searchWord = domObject.value;
    var searchField;
    var domSuffix = domObject.name.split(".")[1];
    if (domSuffix == "vehicleBrand") {
        searchField = "brand";
    } else if (domSuffix == "vehicleModel") {
        searchField = "model";
    } else if (domSuffix == "vehicleYear") {
        searchField = "year";
    } else if (domSuffix == "vehicleEngine") {
        searchField = "engine";
    }
    var ajaxUrl = "product.do?method=searchBrandSuggestion";
    var ajaxData = {
        searchWord:searchWord,searchField:searchField,
        brandValue:searchField == 'brand' ? '' : brandValue,
        modelValue:searchField == 'model' ? '' : modelValue,
        yearValue:searchField == 'year' ? '' : yearValue,
        engineValue:searchField == 'engine' ? '' : engineValue
    };
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function(jsonStr) {
        if (jsonStr.length > 0) {
            brandModelMap[trcount] = new Array();
            for (var jsLength = 0; jsLength < jsonStr.length; jsLength++)
                brandModelMap[trcount][jsLength] = new Array(jsonStr[jsLength].name, modelValue);
            jQuery(domObject).val(jsonStr[0].name);
            modelflag[trcount] = true;
        }
        else if (jsonStr.length == 0)
            modelflag[trcount] = false;
    });
}



