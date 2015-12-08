;
$(function () {

    $("#appointDate").val();

    $("#shop").change(function(){
      var $option=$("#shop option:selected");
        var mobile=$option.attr("mobile");
        var landline=$option.attr("landline");
        $("#contact_mobile").attr("href", "tel:"+!G.isEmpty(landline)?landline:mobile);
        $("#contact_sms").attr("href","sms:"+mobile)
    });

    $("#submitBtn").click(function () {
        var mask = APP_BCGOGO.Module.waitMask;
        mask.login({dev: "wx"});
        var shopId = $("#shop").val();
        if (G.isEmpty(shopId)) {
            mask.open();
            $("#errorMsg").text("请选择服务的店铺");
            return;
        }
        var vehicleNo = $("#vehicleNo").val();
        if (G.isEmpty(vehicleNo)) {
            mask.open();
            $("#errorMsg").text("请选择绑定的车辆，若您还没有绑定，请绑定后再试。");
            return;
        }
        var appointDate = $("#appointDate").val();
        if (G.isEmpty(appointDate)) {
            mask.open();
            $("#errorMsg").text("请选择预约日期");
            return;
        }
        appointDate=appointDate.replace("/","-").replace("/","-");
        var appointTime = $("#appointTime").val();
        if (G.isEmpty(appointTime)) {
            mask.open();
            $("#errorMsg").text("请选择预约时间");
            return;
        }
        appointDate+=" "+appointTime;
        var appoint = {
            shopId: shopId,
            openId: $("#openId").val(),
            vehicleNo: vehicleNo,
            contact: $("#contact").val(),
            mobile: $("#mobile").val(),
            serviceCategoryId: $("#serviceCategoryId").val(),
            appointTimeStr: appointDate,
            remark: $("#remark").val()
        };
        $.ajax({
            type: "POST",
            url: "/api/wx/appointment",
            data: JSON.stringify(appoint),
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


