;
$(function () {
    $(".j_vehicle_item").click(function () {
        var appUserNo = $("#appUserNo").val();
        var maintainPeriod = $("#maintainPeriod").val();
        var mobile = $("#mobile").val();
        var nextMaintainMileage = $("#nextMaintainMileage").val();
        var nextMaintainTimeStr = $("#nextMaintainTimeStr").val();
        var nextExamineTimeStr = $("#nextExamineTimeStr").val();
        var vehicleId = $("#vehicleId").val();
        var appVehicleDTO = {
          appUserNo: appUserNo,
          maintainPeriod: maintainPeriod,
          mobile: mobile,
          nextMaintainMileage: nextMaintainMileage,
          nextMaintainTimeStr: nextMaintainTimeStr,
          nextExamineTimeStr: nextExamineTimeStr,
          vehicleId:vehicleId
        };
        $.ajax({
            type: "POST",
            url: "/web/oAuth/mirror/updateAppVehicleDTO",
            data: JSON.stringify(appVehicleDTO),
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


