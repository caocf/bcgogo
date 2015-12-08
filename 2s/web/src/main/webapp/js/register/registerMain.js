$().ready(function () {
    $(".divSelect").bind("click", function () {
        $(".shopSelect").each(function () {
            if ($(this).hasClass("selected")) {
                $(this).removeClass("selected").addClass("select");
            }
        });
        $(this).find(".shopSelect").removeClass("select").addClass("selected");
        var versionName = $(this).find(".shopSelect").attr("versionName");
        var offSet = $(this).offset();
        $("#showProductInfo").html($("#"+versionName+"_INFO").html()).css("top",offSet.top).css("z-index","1");
    });

    $(".nextBtn").bind("click", function () {
        if ($(".selected").size() == 0) {
            nsDialog.jAlert("请选择您要注册的版本");
        } else {
            var registerType = $(".selected").eq(0).attr("registerType");
            var versionId = $(".selected").eq(0).attr("versionId");
            if ($("#paramRegisterType").val() == "SUPPLIER_REGISTER") {
                window.location.href = "shop.do?method=shopregbasicinfo&shopVersionId=" + versionId + "&customerId="+
                $("#customerId").val()+"&paramNeedVerify="+$("#paramNeedVerify").val()+ "&paramRegisterType="
                    + $("#paramRegisterType").val();
            } else if($("#paramRegisterType").val() == "SALES_REGISTER"){
                window.location.href = "shop.do?method=shopregbasicinfo&shopVersionId=" + versionId + "&paramRegisterType="
                    + $("#paramRegisterType").val();
            } else{
                window.location.href = "shopRegister.do?method=registerDetail&registerShopType=" + registerType +
                "&shopVersionId=" + versionId + "&paramRegisterType=" + $("#paramRegisterType").val()+"&paramNeedVerify="+$("#paramNeedVerify").val();
            }
        }
    });

    if ($(".selected").size() == 0) {
        $(".divSelect").eq(1).click();
    }
//    $(".shopSelect").each(function () {
//        if ($(this).hasClass("selected")) {
//            var versionName = $(this).attr("versionName");
//            $("#showProductInfo").html($("#" + versionName + "_INFO").html());
//            return false;
//        }
//    });
});