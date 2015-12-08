//TODO 处理库存查询页面的SOLR搜索       @deprecated by zhangjuntao

var searchVehicleSuggestion;

var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$().ready(function() {
    //TODO 绑定click和keyUp事件，调用搜索方法  Begin-->
    var elementProduct = document.getElementById("product_name2_id");
    var elementBrand = document.getElementById("pv_brand_id");
    var elementModel = document.getElementById("pv_model_id");

    var elementProduct2 = document.getElementById("product_name2_id");
    var elementBrand2 = document.getElementById("product_brand_id");
    var elementSpec2 = document.getElementById("product_spec_id");
    var elementModel2 = document.getElementById("product_model_id");
    var elementSupplier2 = document.getElementById("pv_supplier_id");

    $("#product_name_id,#pv_brand_id,#pv_model_id,#pv_year_id,#pv_engine_id").bind("keyup", function(e) {
        vehicleAdjustment2(this.id, true);
        webChange(this, "notClick");
    });
    $("#product_name_id,#pv_brand_id,#pv_model_id,#pv_year_id,#pv_engine_id").bind("click", function() {
        webChange(this, "click");
    });

    $("#product_name2_id,#product_brand_id,#product_spec_id,#product_model_id").bind("keyup", function(e) {
        vehicleAdjustment2(this.id, true);
        webChange2(this, "notClick");
    });
    $("#product_name2_id,#product_brand_id,#product_spec_id,#product_model_id").bind("click", function() {
        webChange2(this, "click");
    });

    //TODO <-- End
    function webChange(thisObj, eventStr, brandValue, modelValue) {
//        vehicleids = "";
        if( eventStr === "notClick" ) {
            var position = getCursorPosition(thisObj);
        }

        GLOBAL.debug("webChange --> position : " + position);

        var regReplace = /\s|\\/g;
        if(elementProduct.value.search(regReplace) !== -1) {
            elementProduct.value = elementProduct.value.replace(regReplace, "");
        }
        if(elementBrand.value.search(regReplace) !== -1){
            elementBrand.value = elementBrand.value.replace(regReplace, "");
        }
        if(elementModel.value.search(regReplace) !== -1) {
            elementModel.value = elementModel.value.replace(regReplace, "");
        }
        searchSuggestion(thisObj, $.trim(elementProduct.value) == "品名" ? "" : elementProduct.value,
            !brandValue && $.trim(elementBrand.value) == "车辆品牌" ? "" : ( brandValue || elementBrand.value),
            !modelValue && $.trim(elementModel.value) == "车型" ? "" : (modelValue || elementModel.value),
            "", "", eventStr);

        if( eventStr === "notClick" ) {
            setCursorPosition(thisObj, position);
        }
    }

    searchVehicleSuggestion = webChange;

    function webChange2(thisObj, eventStr) {
        if(eventStr !== "click") {
            var position = getCursorPosition(thisObj);
        }
//        var position = GLOBAL.Interactive.getCursorPosition($(thisObj));
        GLOBAL.debug("webChange2 --> position : " + position);

        var regReplace = /[\s|\\]/g;
        if(elementProduct2.value.search(regReplace) !== -1) {
            elementProduct2.value = elementProduct2.value.replace(regReplace, "");
        }
        if(elementBrand2.value.search(regReplace) !== -1){
            elementBrand2.value = elementBrand2.value.replace(regReplace, "");
        }
        if(elementSpec2.value.search(regReplace) !== -1){
            elementSpec2.value = elementSpec2.value.replace(regReplace, "");
        }
        if(elementModel2.value.search(regReplace) !== -1){
            elementModel2.value = elementModel2.value.replace(regReplace, "");
        }
        searchSuggestion2(thisObj, $.trim(elementProduct2.value) == "品名" ? "" : elementProduct2.value,
            $.trim(elementBrand2.value) == "品牌" ? "" : elementBrand2.value,
            $.trim(elementSpec2.value) == "规格" ? "" : elementSpec2.value,
            $.trim(elementModel2.value) == "型号" ? "" : elementModel2.value,
            eventStr);
//            elementSupplier2.value.replace(/(^\s*)|(\s*$)/g, "") == "供应商" ? "" : elementSupplier2.value);

        if(eventStr !== "click") {
            setCursorPosition(thisObj, position);
        }
    }

    $(".stock_txtClick").each(function(i) {
        this.onclick = function() {
//            $(this).prev().find("input").focus();
            $(this).prev().find("input").click();
        };
    });
});

//TODO 处理包含产品和车型的JSON，初始化下拉建议
function ajaxStyle(domObject, jsonStr, domId, eventStr) {
    var offsetHeight = $(domObject).height();
    suggestionPosition(domObject, 0, 3 + offsetHeight);

    var domTitle = domObject.id;
    $("#Scroller-Container_id").html("");
    var showMore = false;
    for (var i = 0; i < jsonStr.length; i++) {
        var a = $("<a id='selectItem" + i + "'></a>");
        a.html(jsonStr[i].name);
        a.mouseover(function() {
            $("#Scroller-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
        });
        a.click(function() {
            if (jQuery("#editGoodsInfo_id")[0]) {
                var edit_id = "";
                if (domObject.id == "edit_vehicle_brand_id")
                    edit_id = "brand";
                else if (domObject.id == "edit_vehicle_model_id")
                    edit_id = "model";
                if (edit_id)EditGoodsILnfo.modify(edit_id, jQuery(this).html());
            }
            var valflag = domObject.value != $(this).html() ? true : false;
            vehicleAdjustment2(domId, valflag);
            $(domObject).val($(this).html());
            $(domObject).blur();
            $("#div_brand").css({'display':'none'});
        });
        $("#Scroller-Container_id").append(a);
        if (i == 9) {
            showMore = true;
            break;
        }
    }

    if ((domTitle == 'pv_brand_id' || domTitle == 'pv_model_id') && showMore) {
        var field = 'brand';
        if (domTitle == 'pv_model_id') {
            field = 'model';
        }
        var a = $("<a id='selectItem10'></a>");
        a.html("更多");
        a.mouseover(function() {
            $("#Scroller-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
        });
        a.click(function() {
            $("#div_brand").css({'display':'none'});
            var brandValue = $("#pv_brand_id").val() == "车辆品牌" ? "" : $("#pv_brand_id").val();
            bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],
                'src':encodeURI("product.do?method=createsearchvehicleinfo&domtitle="
                    + field + "stockSearch&brandvalue=" + brandValue)});
            Mask.Login();
        });
        $("#Scroller-Container_id").append(a);
    }
}

//TODO 产品和车型发生变动，则清空下一等级的产品信息
function vehicleAdjustment2(selectVehicle, valflag) {
//    var selectVehicle = domObject.name;
    if (selectVehicle == 'product_name_id' && valflag) {
        nextPageNo = 1;
    } else if (selectVehicle == 'product_name2_id' && valflag) {
        nextPageNo = 1;
        document.getElementById("product_brand_id").value = "品牌";
        document.getElementById("product_spec_id").value = "规格";
        document.getElementById("product_model_id").value = "型号";
    } else if (selectVehicle == 'product_brand_id' && valflag) {
        nextPageNo = 1;
        document.getElementById("product_spec_id").value = "规格";
        document.getElementById("product_model_id").value = "型号";
    } else if (selectVehicle == "product_spec_id" && valflag) {
        nextPageNo = 1;
        document.getElementById("product_model_id").value = "型号";
    } else if (selectVehicle == "pv_brand_id" && valflag) {
        nextPageNo = 1;
        document.getElementById("pv_model_id").value = "车型";
    } else if (selectVehicle == 'pv_model_id' && valflag) {
        nextPageNo = 1;
    }
}

//TODO 以前的库存搜索页面，分为两个部分。分别是车辆属性和商品属性，现在都合并了，所以以下两个方法需要整合  Begin-->
function searchSuggestion(domObject, productValue, brandValue, modelValue, yearValue, engineValue, eventStr) { //根据车辆信息查询商品信息
    var searchWord = domObject.value;
    var domid = domObject.id;
    var searchField = null;
    if (domid == "product_name_id") {
        searchField = "product_name";
    } else if (domid == "pv_brand_id" || domid == "edit_vehicle_brand_id") {
        searchField = "brand";
    } else if (domid == "pv_model_id" || domid == "edit_vehicle_model_id") {
        searchField = "model";
    } else if (domid == "pv_year_id") {
        searchField = "year";
    } else if (domid == "pv_engine_id") {
        searchField = "engine";
    } else {
        return;
    }
    if (eventStr == "click") {
        searchWord = "";
    }
    if (searchField == "brand") {
        modelValue = "";
        yearValue = "";
        engineValue = "";
    } else if (searchField == "model") {
        yearValue = "";
        engineValue = "";
    } else if (searchField == "year") {
        engineValue = "";
    }
    var ajaxUrl = "product.do?method=searchProductForStockSearch";
    var ajaxData = {
        searchWord:searchWord,searchField:searchField,
        productValue:searchField == 'product_name' ? '' : productValue,
        brandValue:searchField == 'brand' ? '' : brandValue,
        modelValue:searchField == 'model' ? '' : modelValue,
        yearValue:searchField == 'year' ? '' : yearValue,
        engineValue:searchField == 'engine' ? '' : engineValue
    };
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function(jsonStr) {
        ajaxStyle(domObject, jsonStr, domid);
    });
}

function searchSuggestion2(domObject, productValue, brandValue, specValue, modelValue, eventStr) { //根据商品属性查询商品信息
    var searchWord = domObject.value;
    var searchField = null;
    var domid = domObject.id;
    if (domid == "product_name2_id") {
        searchField = "product_name";
    } else if (domid == "product_brand_id") {
        searchField = "product_brand";
    } else if (domid == "product_spec_id") {
        searchField = "product_spec";
    } else if (domid == "product_model_id") {
        searchField = "product_model";
    } else {
        return;
    }
    //BCSHOP-2991 光标移回原本搜索空字符串, 改为继续用品名关键字搜索.
//    if (eventStr == "click") {
//        searchWord = "";
//    }
    if (searchField == "product_name") {
        brandValue = "";
        specValue = "";
        modelValue = "";
    } else if (searchField == "product_brand") {
        specValue = "";
        modelValue = "";
    } else if (searchField == "product_spec") {
        modelValue = "";
    }
    var ajaxUrl = "product.do?method=searchProductInfoForStockSearch";
    var ajaxData = {
        searchWord:searchWord,searchField:searchField,
        productValue:searchField == 'product_name' ? '' : productValue,
        brandValue:searchField == 'product_brand' ? '' : brandValue,
        specValue:searchField == 'product_spec' ? '' : specValue,
        modelValue:searchField == 'product_model' ? '' : modelValue
    };
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function(jsonStr) {
        ajaxStyle(domObject, jsonStr, domid, eventStr);
    });
}
//TODO <--End

