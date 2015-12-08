function searchVehicleWithAjax(domObj, searchfield, domNum, flag) {
    var elementBrand = document.getElementById(domNum + "_pvBrand");
    var elementModel = document.getElementById(domNum + "_pvModel");
    var elementYear = document.getElementById(domNum + "_pvYear");
    var elementEngine = document.getElementById(domNum + "_pvEngine");

    elementBrand.value = elementBrand.value.replace(/[\ |\\]/g, "");
    elementModel.value = elementModel.value.replace(/[\ |\\]/g, "");
    elementYear.value = elementYear.value.replace(/[\ |\\]/g, "");
    elementEngine.value = elementEngine.value.replace(/[\ |\\]/g, "");
    searchVehicleSuggestionForGoodsBuy(domObj, searchfield, elementBrand.value,
        elementModel.value, elementYear.value, elementEngine.value, domNum, flag);

}
function vehicleAdjustment(selectVehicle, trcount, valflag) {
    if (selectVehicle == 'brand' && valflag) {
        document.getElementById((trcount) + "_pvModel").value = "";
        document.getElementById((trcount) + "_pvYear").value = "";
        document.getElementById((trcount) + "_pvEngine").value = "";
    } else if (selectVehicle == 'model' && valflag) {
        document.getElementById((trcount) + "_pvYear").value = "";
        document.getElementById((trcount) + "_pvEngine").value = "";
    } else if (selectVehicle == "year" && valflag) {
        document.getElementById((trcount) + "_pvEngine").value = "";
    }
    if (valflag) {
        document.getElementById((trcount) + "_brandId").value = "";
        document.getElementById((trcount) + "_modelId").value = "";
        document.getElementById((trcount) + "_yearId").value = "";
        document.getElementById((trcount) + "_engineId").value = "";
    }
}
function searchVehicleSuggestionForGoodsBuy(domObject, searchField, brandValue, modelValue, yearValue, engineValue, trcount, eventStr) { //车辆信息查询
    var searchValue = '';
    if (eventStr == 'notClick') {
        searchValue = domObject.value.replace(/[\ |\\]/g, "");
    }
    if ("brand" == searchField) {
        modelValue = "";
        yearValue = "";
        engineValue = "";
    } else if ("model" == searchField) {
        yearValue = "";
        engineValue = "";
    } else if ("year" == searchField) {
        engineValue = "";
    }
    jQuery.ajax({
            type:"POST",
            url:"product.do?method=searchVehicleSuggestionForGoodsBuy",
            async:true,
            data:{
                searchWord:searchValue,searchField:searchField,
                brandValue:searchField == 'brand' ? '' : brandValue,
                modelValue:searchField == 'model' ? '' : modelValue,
                yearValue:searchField == 'year' ? '' : yearValue,
                engineValue:searchField == 'engine' ? '' : engineValue
            },
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                ajaxStyleForGoodsBuyVehicle(domObject, searchField, jsonStr, trcount);
            }
        }
    );
}

function ajaxStyleForGoodsBuyVehicle(domObject, searchField, jsonStr, trcount) {
    var offset = jQuery(domObject).offset();
    var offsetHeight = jQuery(domObject).height();
    var offsetWidth = jQuery(domObject).width();

    jQuery("#div_brand").css({
        'display':'block','position':'absolute',
        'left':offset.left + 'px',
        'top':offset.top + offsetHeight + 3 + 'px'
    });
    jQuery("#Scroller-Container_id").html("");
    selectmore = jsonStr.length;
    for (var i = 0; i < jsonStr.length; i++) {
        var a = jQuery("<a id='selectItem" + i + "' style=\"overflow: hidden;color:#000000\"></a>");
        a.html(jsonStr[i].name);
        a.mouseover(function() {
            jQuery("#Scroller-Container_id > a").removeAttr("class");
            jQuery(this).attr("class", "hover");
            selectItemNum = parseInt(this.id.substring(10));
        });
        a.click(function() {
            var valflag = domObject.value != jQuery(this).html() ? true : false;
            vehicleAdjustment(searchField, trcount, valflag);
            jQuery(domObject).val(jQuery(this).html());
            jQuery(domObject).blur();
            jQuery("#div_brand").css({'display':'none'});
            selectItemNum = -1;
        });
        jQuery("#Scroller-Container_id").append(a);
    }

    //弹出复选框的最后一项
    if ((searchField == 'brand' || searchField == 'model') && jsonStr.length == 10) {
        var a = jQuery("<a style='color: #000000' id='selectItem" + (selectmore) + "'></a>");
        a.html("更多");
        a.mouseover(function() {
            jQuery("#Scroller-Container_id > a").removeAttr("class");
            jQuery(this).attr("class", "hover");
            selectItemNum = parseInt(this.id.substring(10));
        });
        a.click(function() {
            jQuery("#div_brand").css({'display':'none'});
            jQuery("#iframe_AddCarPopupBox").attr("src", encodeURI("product.do?" +
                "method=createsearchvehicleinfo&domtitle=" + searchField + "GoodsBuy" + trcount
                + "&fwdflag=0&brandvalue=" + document.getElementById((trcount) + "_pvBrand").value));
        });
        jQuery("#Scroller-Container_id").append(a);
    }
}
