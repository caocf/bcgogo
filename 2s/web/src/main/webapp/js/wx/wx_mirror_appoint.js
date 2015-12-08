;
$(function () {
    $("#submitBtn").click(function () {
//        var mask = APP_BCGOGO.Module.waitMask;
//        mask.login({dev: "wx"});
        var shopId = $("#shopId").val();
        var vehicleNo = $("#vehicleNo").val();
//        var appointDate = $("#appointDate").val();
//        if (G.isEmpty(appointDate)) {
//            mask.open();
//            $("#errorMsg").text("请选择预约日期");
//            return;
//        }
//        appointDate=appointDate.replace("/","-").replace("/","-");
//        var appointTime = $("#appointTime").val();
//        if (G.isEmpty(appointTime)) {
//            mask.open();
//            $("#errorMsg").text("请选择预约时间");
//            return;
//        }
//        appointDate+=" "+appointTime;
        var appServiceDTO = {
            shopId: shopId,
            vehicleNo: vehicleNo,
            contact: $("#contact").val(),
            mobile: $("#mobile").val(),
            serviceCategoryId: $("#serviceCategoryId").val(),
//            appointTimeStr: appointDate,
            remark: $("#remark").val()
        };
        $.ajax({
            type: "POST",
            url: "/web/oAuth/mirror/saveAppoint",
            data: JSON.stringify(appServiceDTO),
            contentType: "application/json",
            dataType: "json",
            //async: false,
            success: function (result) {
                if (!result.success) {
                    $("#errorMsg").text(result.msg);
                    mask.open();
                    return;
                }
                var weChat=new APP_BCGOGO.Module.WeChat();
                weChat.closeWindow();
            }
        });
    });

});


