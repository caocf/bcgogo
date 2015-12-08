//TODO 处理车辆施工单的SOLR搜索
var selectmore = -1;
var domTitle;
window.eme;
var modelflag = false;

var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$().ready(function() {
    //todo 为车辆的四个属性添加键盘和单击事件
    var elementBrand = document.getElementById("brand");
    var elementModel = document.getElementById("model");
    var elementYear = document.getElementById("year");
    var elementEngine = document.getElementById("engine");
    $("#brand,#model,#year,#engine").bind("keyup", function(e) {
         var keycode= e.which || e.keyCode;
        webChange(this,keycode);
    });

    $("#brand,#model,#year,#engine").bind("focus", function() {
        if(getOrderType() == "INSURANCE_ORDER"){
          return;
        }
        if (this.name == "brand") {
            $("#input_brandname").val(this.value);
        } else if (this.name == "model") {
            $("#input_modelname").val(this.value);
        } else if (this.name == "year") {
            if (!brandModelInfo(this, "span_year")) {
                return;
            }
            $("#input_yearname").val(this.value);
        } else if (this.name == "engine") {
            if (!brandModelInfo(this, "span_engine")) {
                return;
            }
        }
        eme = this;
    });

    //TODO 当车辆信息填写完毕后，判断它的值是否变化，以便确定是否清空下一等级的车型信息
    $("#brand,#model,#year,#engine").bind("blur", function() {
        this.value = $.trim(this.value);
        var vflag = false;
        if (this.name == "brand") {
            if (getOrderType() != "INSURANCE_ORDER") {
                vflag = this.value != $("#input_brandname").val();
            } else {
                vflag = this.value != $(this).attr("last-value")
            }
        } else if (this.name == "model") {
            vflag = this.value != $("#input_modelname").val();
        } else if (this.name == "year") {
            vflag = this.value != $("#input_yearname").val();
        }
        vehicleAdjustment(this, vflag, this.value, true);

        eme = null;
    });

    if (getOrderType() == "INSURANCE_ORDER") {
        $("#brand,#model").bind("click", function (e) {
             var keycode= e.which || e.keyCode;
            searchSuggestion($(this)[0], $("#brand").val(), $("#model").val(), $("#year").val(), $("#engine").val(), "click",keycode);
        });
    }

    function webChange(thisObj,keycode) {
//        if(elementBrand)
//        elementBrand.value = elementBrand.value.replace(/[\ |\\]/g, "");
//        if(elementModel)
//        elementModel.value = elementModel.value.replace(/[\ |\\]/g, "");
//        if(elementYear)
//        elementYear.value = elementYear.value.replace(/[\ |\\]/g, "");
//        if(elementEngine)
//        elementEngine.value = elementEngine.value.replace(/[\ |\\]/g, "");
        searchSuggestion(thisObj, elementBrand.value, elementModel.value, elementYear==null?"":elementYear.value, elementEngine==null?"":elementEngine.value, "notclick",keycode);
    }
});

//TODO 处理包含车辆信息的JSON，组装成下拉建议
function ajaxStyle2(domObject, jsonStr) {
    if(getOrderType() == "INSURANCE_ORDER"){

    }else if (typeof(eme) == "undefined" || !eme || eme != domObject) {
        return;
    }
    var offsetHeight = $(domObject).height();
    domTitle = domObject.name;
    suggestionPosition(domObject, 0, offsetHeight + 3);
    $("#Scroller-Container_id").html("");
    var showMore = false;
    for (var i = 0; i < jsonStr.length; i++) {
        if (i == 15) {
            showMore = true;
            break;
        }
        var a = $("<a id='selectItem" + i + "'></a>");
        a.html(jsonStr[i].name);
        a.mouseover(function() {
            $("#Scroller-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
        });
        a.click(function() {
            var valflag = domObject.value != $(this).html() ? true : false;
            $(domObject).val($(this).html());
            vehicleAdjustment(domObject, valflag, $(this).html(), true); //TODO 清空下一等级的车辆信息
            $(domObject).blur();
            $("#div_brand").hide();
        });
        $("#Scroller-Container_id").append(a);
    }

    //弹出复选框的最后一项
    if ((domTitle == 'brand' || domTitle == 'model') && showMore && !APP_BCGOGO.Permission.Version.FourSShopVersion) {   //TODO 如果是车辆品牌或车型的下拉建议，则必须包含“更多”
        var a = $("<a id='selectItem" + (jsonStr.length) + "'></a>");
        a.html("更多");
        a.mouseover(function() {
            $("#Scroller-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
        });
        a.click(function() {
            $("#div_brand").hide();
            $("#iframe_PopupBox").attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle="
                + domTitle + "&brandvalue=" + $("#brand").val()));
            Mask.Login();
        });
        $("#Scroller-Container_id").append(a);
    }
}
/**
 *
 * @param domObject
 * @param valflag
 * @param selectvalue
 * @param isselect     true:由下拉建议中选择  false：直接输入
 */
//TODO 当车辆信息发生变化时，清空下一等级的车辆信息
function vehicleAdjustment(domObject, valflag, selectvalue, isselect) {
    var selectVehicle = domObject.name;
    if (selectVehicle == 'brand') {
        $(domObject).attr("last-value", domObject.value);
    }
    if (selectVehicle == 'brand' && valflag && modelflag) {
        $("#model").val("");
        $("#year").val("");
        $("#span_year").html("");
        $("#engine").val("");
        $("#span_engine").html("");
    }else if(selectVehicle == 'brand' && valflag && getOrderType() == "INSURANCE_ORDER"){
        $("#model").val("");
        insuranceOrderVehicleAdjustment();
    } else if (selectVehicle == 'model' && valflag) {
        $("#year").val("");
        $("#span_year").html("");
        $("#engine").val("");
        $("#span_engine").html("");
        var Obj1 = document.getElementById("brand");
        searchSuggestionForVehicleAdjustment(Obj1, "", $("#model").val(),
            $("#year").val(), $("#engine").val());
    } else if (selectVehicle == "year" && valflag) {
        $("#engine").val("");
        $("#span_engine").html("");
    }
}

//TODO AJAX查询车辆信息
function searchSuggestion(domObject, brandValue, modelValue, yearValue, engineValue, eventStr,keycode) { //车辆信息查询
    var searchWord = (eventStr == "click" ? "" : domObject.value);
    var searchField = domObject.name;
    if (searchField == 'brand') {
        brandValue = '';
        modelValue = '';
        yearValue = '';
        engineValue = '';
    } else if (searchField == 'model') {
        modelValue = '';
        yearValue = '';
        engineValue = '';
    } else if (searchField == 'year') {
        yearValue = '';
        engineValue = '';
    } else if (searchField == 'engine') {
        engineValue = '';
    }
    //只有新车牌或者是洗车未输入车型和客户信息的车牌才执行查询。
    var ajaxUrl = "product.do?method=searchBrandSuggestion";
    var ajaxData = {
        searchWord:searchWord,searchField:searchField,
        brandValue:brandValue,
        modelValue: modelValue,
        yearValue:yearValue,
        engineValue:engineValue
    };
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function(jsonStr) {
        if(!G.isEmpty(jsonStr[0])){
            if(jsonStr[0]){
                G.completer({
                        'domObject':domObject,
                        'keycode':keycode,
                        'title':jsonStr[0].name}
                );
            }
        }
        ajaxStyle2(domObject, jsonStr);
    });
}

//TODO 选择车型后带出车辆品牌
function searchSuggestionForVehicleAdjustment(domObject, brandValue, modelValue, yearValue, engineValue) { //车辆信息查询
    var searchWord = "";                 //domObject.value
    var searchField = domObject.name;
    if (!modelValue) {
        return;
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
	          for(var i=0,len = jsonStr.length;i<len;i++){
		          if(jsonStr[i].name && $("#brand").val() === jsonStr[i].name){
                      if(typeof(insuranceOrderVehicleAdjustment) == 'function'){
                        insuranceOrderVehicleAdjustment();
                      }
			          return;
		          }
	          }
            $("#brand").val(jsonStr[0].name);
            modelflag = true;
        }
        else if (jsonStr.length == 0) {
            modelflag = false;
        }
        if(typeof(insuranceOrderVehicleAdjustment) == 'function'){
            insuranceOrderVehicleAdjustment();
        }
    });
}

//TODO AJAX查询商品信息
function searchSuggestion3(domObjects, inputPosition, searchValue, searchField, pnamevalue, pbrandvalue, pspecvalue, pmodelvalue, trRow, functionStatus) {  //材料单查询
    var mybrand = document.getElementById("brand").value;
    var mymodel = document.getElementById("model").value;
    var myyear = document.getElementById("year").value;
    var myengine = document.getElementById("engine").value;

    var ajaxUrl = "product.do?method=searchmaterial";
    var ajaxData = {
        searchWord:searchValue,searchField:searchField,
        productNameValue:searchField == 'product_name' ? '' : pnamevalue,
        productBrandValue:searchField == 'product_brand' ? '' : pbrandvalue,
        productSpecValue:searchField == 'product_spec' ? '' : pspecvalue,
        productModelValue:searchField == 'product_model' ? '' : pmodelvalue,
        vehicleBrand:mybrand,vehicleModel:mymodel,vehicleYear:myyear,vehicleEngine:myengine
    };
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function(jsonStr) {
        ajaxStyleForSingleProductField(domObjects, searchField, inputPosition, jsonStr, trRow);
    });
}

//TODO 处理包含商品信息的JSON，组装成下拉建议
function ajaxStyleForSingleProductField(domObjects, searchField, inputPosition, jsonStr, trRow) {
    var offset = $(domObjects[inputPosition - 1]).offset();
    var offsetHeight = $(domObjects[inputPosition - 1]).height();
    var offsetWidth = $(domObjects[inputPosition - 1]).width();
    if (searchField == "product_brand" || searchField == "product_name") {
        $("#div_brand").css({
            'display':'block','position':'absolute',
            'left':offset.left + 'px',
            'top':offset.top + offsetHeight + 3 + 'px',
            'overflow-x':"hidden" ,
            'overflow-y':"auto" ,
            'height':250 + 'px',
            'padding-left': 0 + 'px'
        });
        if (searchField == "product_brand") {
            $("#div_brand").css({
                'width': offsetWidth + 70 + 'px'
            });
        } else {
            $("#div_brand").css({
                'width': offsetWidth + 'px'
            });
        }
        $("#Scroller-Container_id").html("");
        selectmore = jsonStr.length;
        for (var i = 0; i < jsonStr.length; i++) {
            var a = $("<a id='selectItem" + i + "' style=\"overflow: hidden;\"></a>");
            //校验<A>或<汉字>
            var pattern = /^<[A-Z]|[\u4e00-\u9fa5]>$/;
            if (pattern.test(jsonStr[i])) {
                jsonStr[i] = jsonStr[i].replace(/</g, "").replace(/>/g, "");
                if (searchField == "product_brand") {
                    a.html(jsonStr[i]).css({'padding-left':'5px','color':'#6699cc','font-style': 'italic', 'font-size': '10px','width': offsetWidth + 57 + 'px','height': 15 + 'px','background-color':'#e5e5e5','font-weight':'bold'});
                } else {
                    a.html(jsonStr[i]).css({'padding-left':'5px','color':'#6699cc','font-style': 'italic', 'font-size': '10px','width': offsetWidth - 13 + 'px','height': 15 + 'px','background-color':'#e5e5e5','font-weight':'bold'});
                }
            } else {
                a.mouseover(function() {
                    $("#Scroller-Container_id > a").removeAttr("class");
                    $(this).attr("class", "hover");
                    if (searchField == "product_brand") {
                        $(this).css({ 'width': offsetWidth + 57 + 'px'});
                    } else {
                        $(this).css({ 'width': offsetWidth - 13 + 'px'});
                    }
                });
                a.click(function() {
                    var valflag = domObjects.value != $(this).html() ? true : false;
                    new clearItemUtil().clearByFlag(domObjects[inputPosition - 1], searchField, valflag);
                    $(domObjects[inputPosition - 1]).val($(this).html());
                    $(domObjects[inputPosition - 1]).blur();
                    $("#div_brand").hide();
                });
                a.html(stringMethod.substring(jsonStr[i], 10));
                a.attr("title", jsonStr[i]);
            }
            if ($(a).html() != "") {
                $("#Scroller-Container_id").append(a);
            }
        }
    } else {
        $("#div_brand").css({
            'display':'block','position':'absolute',
            'left':offset.left + 'px',
            'top':offset.top + offsetHeight + 3 + 'px',
            'overflow-x':"hidden" ,
            'overflow-y':"hidden"
        });
        $("#Scroller-Container_id").html("");
        selectmore = jsonStr.length;
        var showMore = false;
        for (var i = 0; i < jsonStr.length; i++) {
            if (i == 9) {
                showMore = true;
                break;
            }
            var a = $("<a id='selectItem" + i + "' style=\"overflow: hidden;\"></a>");
            a.html(jsonStr[i]);
            a.attr("title", jsonStr[i]);
            a.mouseover(function() {
                $("#Scroller-Container_id > a").removeAttr("class");
                $(this).attr("class", "hover");
            });
            a.click(function() {
                var valflag = domObjects[inputPosition - 1].value != $(this).html() ? true : false;
                new clearItemUtil().clearByFlag(domObjects[inputPosition - 1], searchField, valflag);
                $(domObjects[inputPosition - 1]).val($(this).html());
                $(domObjects[inputPosition - 1]).blur();
                $("#div_brand").hide();
            });
            $("#Scroller-Container_id").append(a);
        }
    }

    if (searchField == 'product_name' && showMore) {
        //弹出复选框的最后一项
        var a = $("<a id='selectItem" + (selectmore) + "'></a>");
        a.html("更多");
        a.mouseover(function() {
            $("#Scroller-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
        });
        a.click(function() {
            $("#div_brand").hide();
            $("#iframe_PopupBox").attr("src", encodeURI("product.do?method=searchproductinfo&domtitle=" + searchField
                + "&inputPosition=" + (trRow - 1)));
            Mask.Login();
        });
        $("#Scroller-Container_id").append(a);
    }
    if ($("#Scroller-Container_id>a").size() > 0) {
        $("#div_brand").css({
            'display':'block','position':'absolute',
            'left':offset.left + 'px',
            'top':offset.top + offsetHeight + 3 + 'px'
        });
    }
    else {
        $("#div_brand").hide();
    }
}

//维修单获取 车辆信息
function getVehicleInfo() {
    var brand = document.getElementById("brand").value;
    var model = document.getElementById("model").value;
    var year = document.getElementById("year").value;
    var engine = document.getElementById("engine").value;
    return new Array(brand, model, year, engine);
}

//TODO 在填写年代、排量时，判断车辆品牌和车型是否为空
function brandModelInfo(domObj, spanId) {
    return true;
    if (!$("#brand").val() || !$("#model").val()) {
        alert("请先输入或选择车辆品牌和车型信息!");
        return false;
    }
    return true;
}

