function validateProvinceCity(provinceSelectId, citySelectId){
    if(!$("#"+provinceSelectId).val() || $("#"+provinceSelectId).val()<0){
        nsDialog.jAlert("请选择省份!");
        return false;
    }
    //1032,1033 过滤掉香港和澳门
    if((!$("#"+citySelectId).val() || $("#"+citySelectId).val()<0) &&  $("#"+provinceSelectId).val() != 1032 &&  $("#"+provinceSelectId).val() != 1033){

        nsDialog.jAlert("请选择城市!");
        return false;
    }
    return true;
}

$(function(){
    /**
     * 自动绑定地区下拉框. 用法: 在省市区select中的css分别加入J_province, J_city, J_region . 并增加属性 initvalue 作为初始值.
     */
    provinceAutoBind();
    $(".J_province").bind("change", function () {
        cityAutoBind();
    });
    $(".J_city").bind("change", function () {
        regionAutoBind();
    });
    $(".J_province").each(function () {
        var _this = $(this);
        if(!GLOBAL.isEmpty(_this.attr("initvalue"))){
            _this.val(_this.attr("initvalue")).change();
        }else{
            cityAutoBind();
        }
    })
    $(".J_city, .J_region").each(function(){
        var _this = $(this);
        if(!GLOBAL.isEmpty(_this.attr("initvalue"))){
            _this.val(_this.attr("initvalue")).change();
        }
    });
    $(".J_province, .J_city, .J_region").bind("change", setRegionColor);

    $(".J_address_input").attr("placeholder", "详细地址")
        .bind("focus", function(){
            $(this).removeAttr("placeholder");
            $(this).select();
        })
        .bind("blur", function(){
            $(this).attr("placeholder", "详细地址");
        });
    setRegionColor();
});

function setRegionColor(){
    $(".J_province, .J_city, .J_region").each(function(){
        var dom = this;
        if ($(dom).find("option:selected").val() == '') {
            $(dom).css("color", "#7e7e7e");
        } else {
            $(dom).css("color", "#000000");
        }
    });
}

function provinceAutoBind(){
    var r = APP_BCGOGO.Net.syncGet({url:"shop.do?method=selectarea",
        data: {parentNo: 1}, dataType: "json"});
    if (!r || r.length == 0) return;
    else {
        $(".J_province").append("<option value=''>所有省</option>");
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            $(".J_province").append(option);
        }
    }
}

function cityAutoBind(){
    $(".J_city, .J_region").empty();
    if ($(".J_province option:selected").val()=='') {
        $(".J_city").append("<option value=''>所有市</option>");
        $(".J_region").append("<option value=''>所有区</option>");
    } else {
        var r = APP_BCGOGO.Net.syncGet({"url": "shop.do?method=selectarea", data:{parentNo:$(".J_province option:selected").val()}, "dataType": "json"});
        if (r === null) {
            return;
        }
        else {
            $(".J_city").append("<option value=''>所有市</option>");
            $(".J_region").append("<option value=''>所有区</option>");
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $(".J_city").append(option);
            }
        }
    }
}

function regionAutoBind(){
    $(".J_region").empty();
    var r = APP_BCGOGO.Net.syncGet({"url":"shop.do?method=selectarea", data:{parentNo: $(".J_city option:selected").val()}, "dataType":"json"});
    if (r === null || typeof(r) == "undefined") {
        return;
    } else if($(".J_city option:selected").val() == ""){
        $(".J_region").append("<option value=''>所有区</option>");
    } else {
        $(".J_region").append("<option value=''>所有区</option>");
        if (typeof(r) != "undefined" && r.length > 0) {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $(".J_region").append(option);
            }
        }
    }
}


//第一级菜单 select_province
function provinceBind() {
    var r = APP_BCGOGO.Net.syncGet({url:"shop.do?method=selectarea",
        data:{parentNo:1},dataType:"json"});
    if (!r||r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            $("#select_province")[0].appendChild(option);
        }
    }
}

//第二级菜单 select_city
function cityBind(select) {
    while ($("#select_city")[0].options.length > 1) {
        $("#select_city")[0].remove(1);
    }
    while ($("#select_township")[0].options.length > 1) {
        $("#select_township")[0].remove(1);
    }
    if (select.selectedIndex == 0) {
    } else {
        var r = APP_BCGOGO.Net.syncGet({"url": "shop.do?method=selectarea&parentNo=" + select.value, "dataType": "json"});
        if (r === null) {
            return;
        }
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_city")[0].appendChild(option);
            }
        }
    }
}

//第三级菜单 select_township
function townshipBind(select) {
    var r = APP_BCGOGO.Net.syncGet({"url":"shop.do?method=selectarea&parentNo=" + select.value, "dataType":"json"});
    if (r === null || typeof(r) == "undefined") {
        return;
    }
    else {
        while ($("#select_township")[0].options.length > 1) {
            $("#select_township")[0].remove(1);
        }
        if (typeof(r) != "undefined" && r.length > 0) {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_township")[0].appendChild(option);
            }
        }
    }
}

function setValues(province,city,region) {
    if(province != '') {
        $("#select_province").val(province);
        $("#select_province").change();
    }
    if(city != '') {
        $("#select_city").val(city);
        $("#select_city").change();
    }
    if(region != '') {
        $("#select_township").val(region);
    }
}

function initBusinessScope() {
    var businessScope = '';
    $(".warehouseList :checkbox[name=businessScope1]").each(function(index,checkbox){
        if($(checkbox).attr("checked"))  {
            businessScope += $(checkbox).val();
            businessScope += ',';
        }
    });
    if(businessScope != '') {
        businessScope = businessScope.substring(0,businessScope.length-1);
    }
    $("#businessScope").val(businessScope);
}

function setBusinessScope() {
    if($("#businessScope").val() != '') {
        var scopes = $("#businessScope").val().split(",");
        for(var i= 0;i < scopes.length;i++) {
            $(".warehouseList :checkbox[name=businessScope1]").each(function(index,checkbox){
                if($(checkbox).val() == scopes[i]) {
                    $(checkbox).attr("checked",true);
                    return false;
                }  else {
                    if(index == $(".warehouseList :checkbox[name=businessScope1]").length - 1) {
                        if(i == scopes.length - 1) {
                            $("#otherCheckbox").val($("#otherInput").val() + scopes[i]);
                            $("#otherCheckbox").attr("checked",true);
                            $("#otherInput").val($("#otherInput").val() + scopes[i]);
                        } else {
                            $("#otherCheckbox").val($("#otherInput").val() + scopes[i] + ',');
                            $("#otherCheckbox").attr("checked",true);
                            $("#otherInput").val($("#otherInput").val() + scopes[i] + ',');
                        }

                    }

                }
            });
        }
    }

}


