    //车牌号侦听输入
$().ready(function () {


//车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
    $("#txt_shopName")[0].onfocus = function () {
        if ($("#txt_shopName")[0].value == $("#txt_shopName")[0].defaultValue) {
            $("#txt_shopName")[0].value = "";
            $("#txt_shopName")[0].style.color = "#000000";
        }
        else {
            $("#txt_shopName")[0].style.color = "#000000";
        }
    }
    $("#txt_shopName")[0].onblur = function () {
        if (!$("#txt_shopName")[0].value || $("#txt_shopName")[0].value == $("#txt_shopName")[0].defaultValue) {
            $("#txt_shopName")[0].style.color = "#999999";
            $("#txt_shopName")[0].value = $("#txt_shopName")[0].defaultValue;
        }
    }


//车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
    $("#txt_shopOwner")[0].onfocus = function () {
        if ($("#txt_shopOwner")[0].value == $("#txt_shopOwner")[0].defaultValue) {
            $("#txt_shopOwner")[0].value = "";
            $("#txt_shopOwner")[0].style.color = "#000000";
        }
        else {
            $("#txt_shopOwner")[0].style.color = "#000000";
        }
    }
    $("#txt_shopOwner")[0].onblur = function () {
        if (!$("#txt_shopOwner")[0].value || $("#txt_shopOwner")[0].value == $("#txt_shopOwner")[0].defaultValue) {
            $("#txt_shopOwner")[0].style.color = "#999999";
            $("#txt_shopOwner")[0].value = $("#txt_shopOwner")[0].defaultValue;
        }
    }

    //车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
    $("#txt_address")[0].onfocus = function () {
        if ($("#txt_address")[0].value == $("#txt_address")[0].defaultValue) {
            $("#txt_address")[0].value = "";
            $("#txt_address")[0].style.color = "#000000";
        }
        else {
            $("#txt_address")[0].style.color = "#000000";
        }
    }
    $("#txt_address")[0].onblur = function () {
        if (!$("#txt_address")[0].value || $("#txt_address")[0].value == $("#txt_address")[0].defaultValue) {
            $("#txt_address")[0].style.color = "#999999";
            $("#txt_address")[0].value = $("#txt_address")[0].defaultValue;
        }
    }


    //车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
    $("#txt_phone")[0].onfocus = function () {
        if ($("#txt_phone")[0].value == $("#txt_phone")[0].defaultValue) {
            $("#txt_phone")[0].value = "";
            $("#txt_phone")[0].style.color = "#000000";
        }
        else {
            $("#txt_phone")[0].style.color = "#000000";
        }
    }
    $("#txt_phone")[0].onblur = function () {
        if (!$("#txt_phone")[0].value || $("#txt_phone")[0].value == $("#txt_phone")[0].defaultValue) {
            $("#txt_phone")[0].style.color = "#999999";
            $("#txt_phone")[0].value = $("#txt_phone")[0].defaultValue;
        }
    }


});
