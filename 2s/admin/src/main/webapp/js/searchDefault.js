    //车牌号侦听输入
$().ready(function () {

//车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
    $("#txt_shopName").focus( function () {
        if ($("#txt_shopName").val() == $("#txt_shopName")[0].defaultValue) {
            $("#txt_shopName").val( "" );
            $("#txt_shopName").css("color", "#000000");
        }
        else {
            $("#txt_shopName").css("color", "#000000");
        }
    });
    $("#txt_shopName").blur( function () {
        if (!$("#txt_shopName").val() || $("#txt_shopName").val() == $("#txt_shopName")[0].defaultValue) {
            $("#txt_shopName").css("color", "#999999");
            $("#txt_shopName").val( $("#txt_shopName")[0].defaultValue );
        }
    });


//车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
    $("#txt_shopOwner").focus( function () {
        if ($("#txt_shopOwner").val() == $("#txt_shopOwner")[0].defaultValue) {
            $("#txt_shopOwner").val("");
            $("#txt_shopOwner").css("color", "#000000");
        }
        else {
            $("#txt_shopOwner").css("color", "#000000");
        }
    });
    $("#txt_shopOwner").blur( function () {
        if (!$("#txt_shopOwner").val() || $("#txt_shopOwner").val() == $("#txt_shopOwner")[0].defaultValue) {
            $("#txt_shopOwner").css("color", "#999999");
            $("#txt_shopOwner").val( $("#txt_shopOwner")[0].defaultValue );
        }
    });

    //车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
    $("#txt_address").focus( function () {
        if ($("#txt_address").val() == $("#txt_address")[0].defaultValue) {
            $("#txt_address").val("");
            $("#txt_address").css("color", "#000000");
        }
        else {
            $("#txt_address").css("color", "#000000");
        }
    });
    $("#txt_address").blur( function () {
        if (!$("#txt_address").val() || $("#txt_address").val() == $("#txt_address")[0].defaultValue) {
            $("#txt_address").css("color", "#999999");
            $("#txt_address").val( $("#txt_address")[0].defaultValue );
        }
    });


    //车牌号//车主姓名、手机号/用品配件品名（简写缩写）/
    $("#txt_phone").focus( function () {
        if ($("#txt_phone").val() == $("#txt_phone")[0].defaultValue) {
            $("#txt_phone").val("");
            $("#txt_phone").css("color", "#000000");
        }
        else {
            $("#txt_phone").css("color", "#000000");
        }
    });
    $("#txt_phone").blur( function () {
        if (!$("#txt_phone").val() || $("#txt_phone").val() == $("#txt_phone")[0].defaultValue) {
            $("#txt_phone").css("color", "#999999");
            $("#txt_phone").val( $("#txt_phone")[0].defaultValue );
        }
    });


});
