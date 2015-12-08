$(function () {
    $("#operationModes").click(function (e) {
        if (e.target.value == "专卖店" || e.target.value == "其它") {
            $("#otherOperationMode").show();
        } else {
            $("#otherOperationMode").hide();
        }
    });
    $("input[name=shopVersionName]").click(function (e) {
        console.log(e.target.value);
        var $softPrice = $("#softPrice"),
            $softPriceInfo = $("#softPriceInfo");
        switch (e.target.value) {
            case "初级版":
                $softPrice.val("700");
                $softPriceInfo.html("700元（含短信费200元）");
                break;
            case "综合版":
                $softPrice.val("1200");
                $softPriceInfo.html("1200元（含短信费200元）");
                break;
            case "高级版":
                $softPrice.val("1700");
                $softPriceInfo.html("1700元（含短信费200元）");
                break;
            case "批发商版":
                $softPrice.val("10000");
                $softPriceInfo.html("10000元（含短信费200元）");
                break;
            default:
                console.log("shopVersionName error!")
        }
    });

    function focusAndSelect(idName) {
        $("#" + idName).focus().select();
    }

    provinceBind();
    $("#input_name").blur(function () {
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: "shop.do?method=checkshopname",
            data: {name: $("#input_name").val()},
            cache: false,
            dataType: "json",
            success: function (jsonStr) {
                if (typeof jsonStr == "string") {
                    if (jsonStr == "false") {
                        nsDialog.jAlert("本单位名称已经注册，请在店名后增加地区、路名等便于区别，谢谢！", null, function () {
                            focusAndSelect("input_name");
                        });
                    }
                }
            }
        });
    });
    $("#input_storeManagerMobile").blur(function () {
        APP_BCGOGO.Net.asyncAjax({
            type: "POST",
            url: "shop.do?method=checkStoreManagerMobile",
            data: {mobile: $("#input_storeManagerMobile").val()},
            cache: false,
            dataType: "json",
            success: function (jsonStr) {
                if (typeof jsonStr == "string") {
                    if (jsonStr == "true") {
                        nsDialog.jConfirm("该手机号码已经注册其他店面，是否继续使用此号码注册该店面？", null, function (returnVal) {
                            if (!returnVal) {
                                $("#input_storeManagerMobile").val("");
                            }
                        });
                    }
                }
            }
        });
    });
    //第一级菜单 select_province
    function provinceBind() {
        var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
            data: {parentNo: 1}, dataType: "json"});
        if (!r || r.length == 0) return;
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
        if (select.selectedIndex == 0) {
            $("#select_city")[0].style.display = "none";
            $("#select_township")[0].style.display = "none";
            $("#input_areaId")[0].value = "";
            $("#input_address")[0].value = "";

            return;
        }

        $("#select_township")[0].style.display = "none";

        while ($("#select_city")[0].options.length > 1) {
            $("#select_city")[0].remove(1);
        }

        $("#input_areaId")[0].value = select.value;
        $("#select_city")[0].style.display = "block";

        $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML;

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

    //第三级菜单 select_township
    function townshipBind(select) {
        if (select.selectedIndex == 0) {
            $("#select_township")[0].style.display = "none";
            $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML;
            return;
        }
        else {

        }
        $("#input_areaId")[0].value = select.value;
        $("#select_township")[0].style.display = "block";

        $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
            + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML;

        var r = APP_BCGOGO.Net.syncGet({"url": "shop.do?method=selectarea&parentNo=" + select.value, "dataType": "json"});
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

    function licensePlate(select) {
        if (select.selectedIndex == 0) {
            $("#licencePlate")[0].value = "";
            return;
        }
        else {

        }
        var r = APP_BCGOGO.Net.syncGet({"url": "product.do?method=searchlicenseNo&localArea=" + select.value, "dataType": "json"});
        if (r === null) {
            return;
        }
        else {
            $("#licencePlate")[0].value = r[0].platecarno;
        }
    }

    $("#select_province")[0].onchange = function () {
        cityBind(this);
    };
    $("#select_city")[0].onchange = function () {
        licensePlate(this);
        townshipBind(this);
    };
    $("#select_township")[0].onchange = function () {

        if (this.selectedIndex != 0) {

            $("#input_areaId")[0].value = this.value;

            $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
                + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML
                + $("#select_township")[0].options[$("#select_township")[0].selectedIndex].innerHTML;

        }
        else {
            $("#input_address")[0].value = $("#select_province")[0].options[$("#select_province")[0].selectedIndex].innerHTML
                + $("#select_city")[0].options[$("#select_city")[0].selectedIndex].innerHTML;
        }

    };
});