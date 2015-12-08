$().ready(function(){

    initText();

    $("#no").bind("focus",function(){
        if($("#no").val() == $("#no").attr("hiddenValue"))
        {
            $("#no").val("");
        }
    }).bind("blur",function(){
            if(!$("#no").val())
            {
                $("#no").val($("#no").attr("hiddenValue"));
            }
        });
    //不允许反斜杠
    $("input").bind("keyup",function(){
        $(this).val($(this).val().replace(/[\s|\\]/g, ""));
    });

    $("#brand,#model").bind("click", function () {
        if($(this).val() == $(this).attr("hiddenValue"))
        {
            $(this).val("");
        }
        var model = $("#model").val()==$("#model").attr("hiddenValue")?"":$("#model").val();
        var brand = $("#brand").val()==$("#brand").attr("hiddenValue")?"":$("#brand").val();
        searchSuggestionTwo($(this)[0], brand, model, "", "", "click");
    }).bind("focus",function(){
            if($(this).val() == $(this).attr("hiddenValue"))
            {
                $(this).val("");
            }
        }).bind("blur",function(){
                if(!$(this).val())
                {
                    $(this).val($(this).attr("hiddenValue"));
                }
        }).bind("keyup",function(){
            webChangeTwo(this);
        });

    //发动机号车架号
    $("#engineNo,#chassisNumber").bind("keyup",function(){
        var  str = $(this).val().replace(/[^\da-zA-Z]+/g, "");
        if(str)
        {
            str = str.toUpperCase();
        }

        $(this).val(str);
    });

    $("#travelLength").bind("keyup",function(){
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(),2));
    });

    $("#travelDate").bind("keyup",function(){
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(),2));
    });

    $("#save,#print").bind("click",function(){
        var model = $("#model").val()==$("#model").attr("hiddenValue")?"":$("#model").val();
        var brand = $("#brand").val()==$("#brand").attr("hiddenValue")?"":$("#brand").val();
        var no = $("#no").val()==$("#no").attr("hiddenValue")?"":$("#no").val();

        if(!no || !$("#customer").val() || !$("#repairContractNo").val()
             || !$("#qualityInspectors").val() || !$("#shopName").val() || !$("#startDateStr").val() || !$("#endDateStr").val()
            || !$("#travelLength").val() || !$("#travelDate").val()  || $("#repairType").val() == "----请选择----")
        {
            nsDialog.jAlert('对不起，请输入所有打" * "的必填项！');
            return;
        }
        var obj = this;
        $("#form").ajaxSubmit(function(json){

//            json = JSON.parse(json);

            if(json.saveResult == "success")
            {
                if($(obj).attr("btn") == "print")
                {
                    var url = "txn.do?method=printQualifiedCredentials&orderId=" + $("#orderId").val();
                    window.open(url, "_blank");
                }
                else
                {
                    nsDialog.jAlert("保存成功");
                }
            }
            else
            {
                nsDialog.jAlert("保存失败");
            }
        });
    });

    function webChangeTwo(thisObj) {
        var model = $("#model").val()==$("#model").attr("hiddenValue")?"":$("#model").val();
        var brand = $("#brand").val()==$("#brand").attr("hiddenValue")?"":$("#brand").val();
        searchSuggestionTwo(thisObj, brand, model, "", "", "notclick");
    }
});

function initText()
{
    if(!$("#no").val())
    {
        $("#no").val($("#no").attr("hiddenValue"));
    }

    if(!$("#brand").val())
    {
        $("#brand").val($("#brand").attr("hiddenValue"));
    }

    if(!$("#model").val())
    {
        $("#model").val($("#model").attr("hiddenValue"));
    }
    if(!$("#travelLength").val())
    {
        $("#travelLength").val($("#travelLength").attr("hiddenValue"));
    }
    if(!$("#travelDate").val())
    {
        $("#travelDate").val($("#travelDate").attr("hiddenValue"));
    }
}

//TODO AJAX查询车辆信息
function searchSuggestionTwo(domObject, brandValue, modelValue, yearValue, engineValue, eventStr) { //车辆信息查询
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
        ajaxStyleTwo(domObject, jsonStr);
    });
}

function ajaxStyleTwo(domObject, jsonStr) {
//    if(getOrderType() == "INSURANCE_ORDER"){
//
//    }else if (typeof(eme) == "undefined" || !eme || eme != domObject) {
//        return;
//    }
    var offsetHeight = $(domObject).height();
    domTitle = domObject.name;
    suggestionPosition(domObject, 0, offsetHeight + 3);
    $("#Scroller-Container_id").html("");
    var showMore = false;
    for (var i = 0; i < jsonStr.length; i++) {
        if (i == 9) {
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
            vehicleAdjustmentTwo(domObject, valflag, $(this).html(), true); //TODO 清空下一等级的车辆信息
            $(domObject).blur();
            $("#div_brand").hide();
        });
        $("#Scroller-Container_id").append(a);
    }

    //弹出复选框的最后一项
    if ((domTitle == 'brand' || domTitle == 'model') && showMore) {   //TODO 如果是车辆品牌或车型的下拉建议，则必须包含“更多”
        var a = $("<a id='selectItem" + (jsonStr.length) + "'></a>");
        a.html("更多");
        a.mouseover(function() {
            $("#Scroller-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
        });
        var brand = $("#brand").val();
        if(brand == $("#brand").attr("hiddenValue"))
        {
            brand = ""
        }
        a.click(function() {
            $("#div_brand").hide();
            $("#iframe_PopupBox").attr("src", encodeURI("product.do?method=createsearchvehicleinfo&domtitle="
                + domTitle + "&brandvalue=" + brand));
            Mask.Login();
        });
        $("#Scroller-Container_id").append(a);
    }
}

function vehicleAdjustmentTwo(domObject, valflag, selectvalue, isselect) {
    var selectVehicle = domObject.name;
    if (selectVehicle == 'brand' && valflag && modelflag) {
        $("#model").val("");
    }else if(selectVehicle == 'brand' && valflag && getOrderType() == "INSURANCE_ORDER"){
        $("#model").val("");
    } else if (selectVehicle == 'model' && valflag) {
        var Obj1 = document.getElementById("brand");
        searchSuggestionForVehicleAdjustmentTwo(Obj1, "", $("#model").val(),
            "", "");
    }
}

function searchSuggestionForVehicleAdjustmentTwo(domObject, brandValue, modelValue, yearValue, engineValue) { //车辆信息查询
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
			          return;
		          }
	          }
            $("#brand").val(jsonStr[0].name);
            modelflag = true;
        }
        else if (jsonStr.length == 0) {
            modelflag = false;
        }
    });
}

function vehicleAdjustment(domObject, valflag, selectvalue, isselect) {
    var selectVehicle = domObject.name;
    if (selectVehicle == 'brand' && valflag && modelflag) {
        $("#model").val("");
    }else if(selectVehicle == 'brand' && valflag && getOrderType() == "INSURANCE_ORDER"){
        $("#model").val("");
    } else if (selectVehicle == 'model' && valflag) {
        var Obj1 = document.getElementById("brand");
        searchSuggestionForVehicleAdjustmentTwo(Obj1, "", $("#model").val(),
            $("#year").val(), $("#engine").val());
    }
}


