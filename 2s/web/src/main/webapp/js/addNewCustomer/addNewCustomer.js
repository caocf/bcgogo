$(document).ready(function () {
  $("#J_addNewVehicle").bind("click", function () {


    var index = 0;
    $("#table_vehicle").find(".vehicle_information").each(function () {
      var dataIndex = $(this).attr("data-index");
      index = index > parseInt(dataIndex) ? index : parseInt(dataIndex);
    });

    if(G.isEmpty($("#vehicles" + index +"\\." +"licenceNo").val())){
      nsDialog.jAlert("请先输入上一辆车车牌号");
      return;
    }

    var flag =checkGsmObdImei(index);
    if(flag){
      return;
    }


    var div = $("#customerVehicleInfo" + index).clone();

    index = index + 1;
    $(div).attr("id","customerVehicleInfo" + index).attr("data-index",index);
    $(div).find("input").val("");
    $(div).find(".vehicle_information").attr("data-index",index);
    $(div).find(".title_close").attr("data-index",index);

    $(div).find("input").each(function (i) {
      //replace id
      var idStr = $(this).attr("id");
      var idStrs = idStr.split(".");
      var newId = "vehicles" + index + "." + idStrs[1];
      $(this).attr("id", newId);
      var nameStr = $(this).attr("name");
      var nameStrs = nameStr.split(".");
      var newName = "vehicles[" + index + "]." + nameStrs[1];
      $(this).attr("name", newName);
    });
    $(div).appendTo("#table_vehicle");
  });

  $("#table_vehicle .title_close").live("click", function () {
    var index = $(this).attr("data-index");
    var size = $("#table_vehicle").find(".vehicle_information").size();
    if (size == 1) {
      $("#table_vehicle").find("input").val("");
    } else {
      $("#customerVehicleInfo" + index).remove();
    }
  });

});


function checkGsmObdImei(index) {

  var flag = false;

  if (!G.isEmpty($("#vehicles" + index + "\\." + "gsmObdImei").val())) {
    APP_BCGOGO.Net.syncGet({
      url: "customer.do?method=checkIsExistGsmObdImeiInVehicle",
      data: {
        gsmObdImei: $("#vehicles" + index + "\\." + "gsmObdImei").val()
      },
      dataType: "json",
      success: function (json) {
        if (!json.success) {
          nsDialog.jAlert(json.msg);
          flag = true;
        }
      },
      error: function (json) {
        nsDialog.jAlert("网络异常，请联系客服");
      }

    });
  }
  return flag;
}