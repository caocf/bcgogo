/**
 * 标准车辆品牌、车型专用js
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-24
 * Time: 下午10:14
 * To change this template use File | Settings | File Templates.
 */

var ajaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$(document).ready(function() {


    if ($("#vehicleBrandModelDiv").length > 0) {
        var ajaxUrl = "businessScope.do?method=getAllStandardVehicleBrandModel";
        var ajaxData = {
        };
        ajaxQuery.setUrlData(ajaxUrl, ajaxData);
        ajaxQuery.ajaxQuery(function (data) {
            var multiSelectTwoDialog = new App.Module.MultiSelectTwoDialog();
            App.namespace("components.multiSelectTwoDialog");
            App.components.multiSelectTwoDialog = multiSelectTwoDialog;

            multiSelectTwoDialog.init({
                "data": data,
                "selector": "#vehicleBrandModelDiv"
            });
        })

        $("input[name='model_select']").bind("click", function () {
            if ($(this).val() == "allBrandModel") {
                $("#vehicleBrandModelDiv").css("display", "none");
            } else if ($(this).val() == "partBrandModel") {
                $("#vehicleBrandModelDiv").css("display", "block");
            }
        });
        $("#allBrandModel").click();
    }
});



/*
data 是原先form已经封装好的数据格式：data:{key:value}
newData 是要往form里添加的数据格式： newData:[{key1:value,key2,value},{}...]
dataPrefix 是要往form里添加的数据格式key的前缀： newData:[{key1:value,key2,value},{}...]
 */
function arrayObjectAppendToData(data, dataPrefix, newArrayObject) {

  if($('input:radio[name="model_select"]:checked').val() == "allBrandModel"){
    data.selectAllBrandModel = true;
    return;
  }else{
    data.selectAllBrandModel = false;
  }

  if (!dataPrefix) {
    return;
  }
  if (!data) {
    data = {};
  }

  if (newArrayObject) {
    for (var i = 0, len = newArrayObject.length; i < len; i++) {
      var newData = newArrayObject[i];
      for (var key in newData) {
        if (key && newData[key]) {
          data[dataPrefix + "[" + i + "]." + key] = newData[key];
        }
      }

    }
  }
}