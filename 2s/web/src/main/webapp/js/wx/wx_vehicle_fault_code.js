;
$(function () {



//    $("#shop").change(function(){
//      var $option=$("#shop option:selected");
//        var mobile=$option.attr("mobile");
//        var landline=$option.attr("landline");
//        $("#contact_mobile").attr("href", "tel:"+!G.isEmpty(landline)?landline:mobile);
//        $("#contact_sms").attr("href","sms:"+mobile)
//    });

    $("#submitBtn").click(function () {
        var mask = APP_BCGOGO.Module.waitMask;
        mask.login({dev: "wx"});
        var shopId = $("#shop").val();
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


