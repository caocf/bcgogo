//todo 主要用于 添加客户时处理Solr搜索的脚本
var selectItemNum = -1;
var selectmore = -1;
var domTitle;

//todo 为车辆的四个属性添加键盘和单击事件
$().ready(function() {
    var elementBrand = document.getElementById("brand");
    var elementModel = document.getElementById("model");
    var elementYear = document.getElementById("year");
    var elementEngine = document.getElementById("engine");
    $("#brand,#model,#year,#engine").bind("keyup", function(e) {
        webChange(this, "notclick");
    });
    $("#brand,#model,#year,#engine").bind("click", function() {
        webChange(this, "click");
    });

    function webChange(thisObj, eventStr) {
        searchSuggestion(thisObj, elementBrand.value, elementModel.value, elementYear.value, elementEngine.value, eventStr);
    }
});


//todo 处理包含车辆信息的JSON，让下拉建议获得数据，并显示
function ajaxStyle(domObject, jsonStr) {

    //todo 定位下拉建议位置
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();
    domTitle = domObject.id;

    $("#div_brand").css({
        'display':'block','position':'absolute',
        'left':offset.left + 'px',
        'top':offset.top + offsetHeight + 3 + 'px'
    });
    $("#Scroller-Container_id").html("");
    selectmore = jsonStr.length;
    for (var i = 0; i < jsonStr.length; i++) {
        var a = $("<a id='selectItem" + i + "'></a>");
        a.html(jsonStr[i].name);
        a.mouseover(function() {
            $("#Scroller-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
            selectItemNum = parseInt(this.id.substring(10));
        });
        a.click(function() {
            var valflag = domObject.value != $(this).html() ? true : false;
            $(domObject).val($(this).html());
            $(domObject).blur();
            vehicleAdjustment(domObject, valflag);
            $("#div_brand").css({'display':'none'});
            selectItemNum = -1;
        });
        $("#Scroller-Container_id").append(a);
    }

    //弹出复选框的最后一项
    if (domTitle == 'brand' || domTitle == 'model') { //TODO 下拉的是车辆品牌或者车型，则下拉建议需要包含“更多”
        var a = $("<a id='selectItem" + (selectmore) + "'></a>");
        a.html("更多");
        a.mouseover(function() {
            $("#Scroller-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
            selectItemNum = parseInt(this.id.substring(10));
        });
        a.click(function() {
            $("#div_brand").css({'display':'none'}); //TODO 点击“更多”，弹出包含更多车辆的IFRAME
            parent.$("#iframe_PopupBox_1").attr("src", encodeURI("product.do?method=createsearchvehicleinfoforaddclient&domtitle="
                + domTitle + "&brandvalue=" + $("#brand").val()));
            parent.$("#iframe_PopupBox_1").css({'display':'block'});
        });
        $("#Scroller-Container_id").append(a);
    }
}


//TODO 用ajax查询车辆信息
function searchSuggestion(domObject, brandValue, modelValue, yearValue, engineValue, eventStr) { //车辆信息查询
    var searchWord;
    if (eventStr == "click") {
        searchWord = "";
    } else {
        searchWord = domObject.value;
    }
    var searchField = domObject.id;
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
    var ajaxUrl = "product.do?method=searchBrandSuggestion";
    var ajaxData = {
        searchWord:searchWord,searchField:searchField,
        brandValue: brandValue,
        modelValue: modelValue,
        yearValue:yearValue,
        engineValue: engineValue
    };
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function(jsonStr) {
        ajaxStyle(domObject, jsonStr);
    });
}

//TODO 下拉建议外围单击，隐藏下拉建议
document.onclick = function(e) {
    var e = e || event;
    var target = e.srcElement || e.target;
    if (!target || !target.id) {
        $("#div_brand")[0].style.display = "none";
        selectItemNum = -1;
    }
}


//TODO 车型变更后，清理相应的字段信息
function vehicleAdjustment(domObject, valflag) {
    var selectVehicle = domObject.id;
    if (selectVehicle == 'brand' && valflag) {
        $("#model").val("");
        $("#year").val("");
        $("#engine").val("");
    } else if (selectVehicle == 'model' && valflag) {
        $("#year").val("");
        $("#engine").val("");
        var Obj1 = document.getElementById("brand");
        searchSuggestionForVehicleAdjustment(Obj1, $("#brand").val(), $("#model").val(), $("#year").val(), $("#engine").val());
    } else if (selectVehicle == "year" && valflag) {
        $("#engine").val("");
    }
}

//TODO 选择车型，自动带出车辆品牌
function searchSuggestionForVehicleAdjustment(domObject, brandValue, modelValue, yearValue, engineValue) { //车辆信息查询
    var searchWord = domObject.value;
    var searchField = domObject.id;

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
            $(domObject).val(jsonStr[0].name);
        }
    });
}
