var trCount;
var trSample;
$().ready(function () {
//    tableUtil.tableStyle('#table_vehicle','.table_title');
    var customerNameAjax = "";  //记录AJAX获得的当前车牌号的实际客户姓名
    var customerId;             //记录AJAX获得的当前车牌号的实际客户ID
    var licenceNoInput;         //记录触发事件的是哪个车牌号文本框
    $("#name").live("blur", function() {
        if ($("#name").val() != "") {
            //与原车主不同名
            if ($("#name").val() != customerNameAjax && customerNameAjax != "") {
                if (licenceNoInput.val() != "" && licenceNoInput.val() != null) {
                    if (confirm("此车牌【" + licenceNoInput.val() + "】已属于其他客户【" + customerNameAjax + "】，不能用于当前客户,是否转让到当前客户？")) {
                        if (confirm("请再次确认此车的欠款是否已经结清，并办妥相关转让手续。")) {
                            //将原车主的车牌删除
                            $("#customerId").val("");
                            var j;
                            APP_BCGOGO.Net.syncGet({url:"customer.do?method=deleteCustomerLicenceNo",data:{licenceVal:licenceNoInput.val()},dataType:"json",
                                success:function(json) {
                                    j = json;
                                }
                            });
                        } else {
                            customerNameAjax = "";
                            customerId = "";
                            licenceNoInput.val("");
                        }
                    } else {
                        customerNameAjax = "";
                        customerId = "";
                        licenceNoInput.val("");
                    }
                }
            }
            //与原车主同名
            else if (customerNameAjax != "") {
                if ($("customerId").val() == "") {
                    if (confirm("此车牌客户【" + customerNameAjax + "】已存在，是否修改客户资料为当前信息？")) {
                        $("#customerId").val(customerId);
                    } else {
                        customerNameAjax = "";
                        customerId = "";
                        $("#customerId").val(customerId);
                    }
                }
            }
        } else {
//            alert("用户名必须填写!");
        }
    });
    $("#table_vehicle input[id$='.chassisNumber']").live("keyup", function() {
        $(this).val($(this).val().toUpperCase());
        $(this).val(APP_BCGOGO.StringFilter.inputtingVinFilter($(this).val()));
    });

    trCount = $(".vehic").size();
    //增加行
//    jQuery("#table_vehicle input[id$='.id']").each(function(i) {       //todo 代码冗余？ add by dongnan
//        if (jQuery(this).val() != null && jQuery(this).val() != "") {
//            var idStr = jQuery(this).attr("id");
//            var idStrs = idStr.split(".");
//            var newId = idStrs[0] + ".deletebutton";
//            jQuery(this).parent().next().next().next().next().next().next().next().children().eq(0).hide();
////	        $("#"+idStrs[0] + "\\.licenceNo").attr("readonly",true);
//        }
//    });
    $(".validationDuplicate").bind("change", function() {
        if (trCount >= 2) {
            if (checkDuplicateVehicles(this)) {
                alert("单据有重复内容，请修改或删除。");
                $(this).select().focus();
                return;
            }
        }
    });

    $("#table_vehicle input[id$='.licenceNo']").live("keyup", function() {
        $(this).val($(this).val().toUpperCase().replace(/[^A-Z\d\u4e00-\u9fa5\-_\(\)]+/g, ""));
    });

    jQuery(".opera2").live('click', function() {
        trSample = '<tr class="vehic">' +
                '<td><input maxlength="9" type="text" class="txt validationDuplicate" name="vehicles[0].licenceNo" id="vehicles0.licenceNo" value=""/>' +
                '<input type="hidden" name="vehicles[0].id" id="vehicles0.id" value=""/></td>' +
                '<td><input class="txt" maxlength="20" type="text" name="vehicles[0].contact" id="vehicles0.contact" value="" /></td>' +
                '<td><input class="txt" maxlength="11" type="text" name="vehicles[0].mobile" id="vehicles0.mobile" value="" /></td>' +
                '<td><input class="txt" maxlength="8" type="text" name="vehicles[0].brand" pagetype="customerVehicle" id="vehicles0.vehicleBrand" value="" /></td>' +
                '<td><input class="txt" maxlength="8" type="text" name="vehicles[0].model" pagetype="customerVehicle" id="vehicles0.vehicleModel" value="" /></td>' +
                '<td><input style="width:30px;" class="txt" maxlength="12" type="text" name="vehicles[0].year" id="vehicles0.year" value=""/></td>' +
                '<td><input style="width:30px;" class="txt" maxlength="4" type="text" name="vehicles[0].engine" id="vehicles0.engine" value=""/></td>' +
                '<td><input readonly="true" type="text" name="vehicles[0].dateString" id="vehicles0.dateString" value="" class="txt datePicker"/></td>' +
                '<td><input class="txt" maxlength="17" type="text" name="vehicles[0].chassisNumber" id="vehicles0.chassisNumber" value=""/></td>' +

                '<td><input class="txt" maxlength="30" type="text" name="vehicles[0].engineNo" id="vehicles0.engineNo" value=""/></td>' +
                '<td><input class="txt" maxlength="15" type="text" name="vehicles[0].color" id="vehicles0.color" value=""/></td>' +

                '<td><input class="opera1" type="button" id="vehicles0.deletebutton" name="vehicles[0].deletebutton"/></td>' +
                '</tr>';
        var ischeck = checkVehicle(this);
        if (!ischeck && ischeck != null) {
            return;
        }
        if (trCount >= 2)
            if (checkSame(trCount)) {
                alert("输入车辆信息有重复，请重新输入！");
                return false;
            }
        var tr = $(trSample).clone();
        $(tr).find("input").val("");
        $(tr).find("input").each(function (i) {
            //replace id
            var idStr = $(this).attr("id");
            var idStrs = idStr.split(".");
            var newId = "vehicles" + trCount + "." + idStrs[1];
            $(this).attr("id", newId);
            var nameStr = $(this).attr("name");
            var nameStrs = nameStr.split(".");
            var newName = "vehicles[" + trCount + "]." + nameStrs[1];
            $(this).attr("name", newName);
        });
        $(tr).find(".validationDuplicate").bind("change", function() {
            if (trCount >= 2) {
                if (checkDuplicateVehicles(this)) {
                    alert("单据有重复内容，请修改或删除。");
                    $(this).select().focus();
                    return;
                }
            }
        });
        $(tr).appendTo("#table_vehicle");
        trCount++;
        isShowAddButton();
        initDatePickersAndPlateUpperCase();
//        tableUtil.tableStyle('#table_vehicle', '.table_title');
    });
    //删除行
    $(".opera1").live('click', function () {
        var vehicleIdInput = $(this).closest("tr").find("input[id$='id']");
        var vehicleNoInput = $(this).closest("tr").find("input[id$='licenceNo']");

        $('.opera1').size() > 1 && vehicleIdInput.val() == '' && $(this).parent().parent().empty().remove() && isShowAddButton();

        if (vehicleIdInput[0] && !G.isEmpty(vehicleIdInput.val()) && !G.isEmpty(vehicleNoInput.val()) && !G.isEmpty($("#customerId").val())) {
            var data = APP_BCGOGO.Net.syncGet({
                url: "txn.do?method=checkUndoneOrder",
                data: {
                    licenceNo: vehicleNoInput.val(),
                    vehicleId: vehicleIdInput.val(),
                    customerId: $("#customerId").val()
                },
                dataType: "json"
            });
            if (data.success) {
                $(this).closest("tr").remove();
                isShowAddButton();
            } else {
                nsDialog.jAlert(data.msg);
            }
        }
    });
    isShowAddButton();
    initDatePickersAndPlateUpperCase();
});

function initDatePickersAndPlateUpperCase() {
    $(".datePicker").datepicker({
        "numberOfMonths" : 1,
        "showButtonPanel": true,
        "changeYear":true,
        "changeMonth":true,
        "yearRange":"c-100:c+100",
        "yearSuffix":""
    });
    $("input[id$='licenceNo']").blur(function() {
        $(this).val($(this).val().toUpperCase());
    });
}
//判断是否显示+按钮
function isShowAddButton() {
    //如果初始化的话就默认加一行
    if ($(".vehic").size() <= 0) {
//        var tr = $(trSample).clone();
//        $(tr).appendTo("#table_productNo");
        $(".opera2").trigger("click");
    }
//    $(".item .opera2").attr("class", "opera1");
    $(".vehic .opera2").remove();
//    $(".item:last").find("td:last :button").remove(".opera2,.opera1");
//    var size = $(".item").size();
    var opera1Id = $(".vehic:last").find("td:last>input[class='opera1']").attr("id");
    if (opera1Id == null || opera1Id == "") {
        return;
    }

    $(".vehic:last").find("td:last>input[class='opera1']").after(' <input class="opera2" ' +
            ' id="vehicles' + (opera1Id.split(".")[0].substring(8)) + '.plusbutton" type="button"/>');
}
function checkSame(trCount) {
    var trs = $(".vehic");
    if (!trs)
        return false;
    if (trs.length < 2)
        return false;
    //先获取最后一个
    var cur = '';//当前最后添加的一条记录
    var curLicenceNo = '';
    for (var i = trs.length - 1; i >= 0; i--) {
        var inputs = trs[i].getElementsByTagName("input");
        if (!inputs)
            continue;
        var index = inputs[0].name.split(".")[0].substring(inputs[0].name.indexOf('[') + 1, inputs[0].name.indexOf(']'));

        if (i == trs.length - 1) {
//            最后添加的一个
            curLicenceNo = document.getElementById("vehicles" + index + ".licenceNo").value;
            cur += document.getElementById("vehicles" + index + ".licenceNo").value;
            cur += document.getElementById("vehicles" + index + ".vehicleBrand").value;
            cur += document.getElementById("vehicles" + index + ".vehicleModel").value;
            cur += document.getElementById("vehicles" + index + ".year").value;
            cur += document.getElementById("vehicles" + index + ".engine").value;
            cur += document.getElementById("vehicles" + index + ".dateString").value;
            cur += document.getElementById("vehicles" + index + ".chassisNumber").value;

        } else {
            var older = '';
            var olderLicenceNo = '';
            olderLicenceNo = document.getElementById("vehicles" + index + ".licenceNo").value;
            older += document.getElementById("vehicles" + index + ".licenceNo").value;
            older += document.getElementById("vehicles" + index + ".vehicleBrand").value;
            older += document.getElementById("vehicles" + index + ".vehicleModel").value;
            older += document.getElementById("vehicles" + index + ".year").value;
            older += document.getElementById("vehicles" + index + ".engine").value;
            older += document.getElementById("vehicles" + index + ".dateString").value;
            older += document.getElementById("vehicles" + index + ".chassisNumber").value;

            if (curLicenceNo == olderLicenceNo || cur == older) {
                return true;
            }
        }
    }
    return false;
}
function checkVehicle(domObj) {
    var idPrefix = domObj.id.split(".")[0];
    if (idPrefix == "" || idPrefix == "vehicles-1") {
        return null;
    }
    var licenceNo = document.getElementById(idPrefix + ".licenceNo").value;
    if (licenceNo == "") {
        alert("请输入车牌号!");
        return false;
    }
}

function checkAllDuplicateVehicles() {
    var flag = false;
    $(".validationDuplicate").each(function() {
        flag = checkDuplicateVehicles(this);
        if (flag) {
            return false; //跳出循环
        }

    });
    return flag;
}
function checkDuplicateVehicles(domObj) {
    var flag = false;
    $(".validationDuplicate").each(function() {
        if (domObj != this && $(domObj).val() == $(this).val()) {
            flag = true;
            return false; //跳出循环
        }
    });
    return flag;
}

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


function checkVehicleNo(){
  var flag = false;


  $("#table_vehicle input[id$='.licenceNo']").each(function () {
    var licenceVal = $(this).val();
    if ((licenceVal == "" || G.isEmpty(licenceVal))&& $("#table_vehicle input[id$='.licenceNo']").size() != 1)  {
      nsDialog.jAlert("请填写车牌号");
      flag = true;
      return;

    }
  });
  if(flag){
    return flag;
  }

  var r = null;
  APP_BCGOGO.Net.syncGet({
    url: "product.do?method=userLicenseNo",
    dataType: "json",
    success: function (json) {
      r = json;
    }
  });
  if (r === null) {
    flag = true;
    return;
  }

  var licenceNoInput;         //记录触发事件的是哪个车牌号文本框

  var maxDateIndex = 0;

  var localCarNo = r[0].localCarNo;

  $("#table_vehicle input[id$='.licenceNo']").each(function () {

    var licenceVal = $(this).val();
    var licenceDomId = $(this).attr("id");
    var resultStr = licenceVal.replace(/\s|\-/g, "");
    if (!checkhanzi(resultStr) && (resultStr.length == 5 || resultStr.length == 6)) {
      var locaono = localCarNo;
      $(this).val((locaono + $(this).val()).toUpperCase());
    }
    licenceVal = $(this).val();
    var index = licenceDomId.split(".")[0].split("vehicles")[1];
    if (G.isEmpty(licenceVal)) {
      return ;
    }
    if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber(licenceVal)) {
      flag = true;
      maxDateIndex = maxDateIndex > parseInt(index) ? maxDateIndex : parseInt(index);
      alert("输入的车牌号码不符合规范，请检查！");
      if (document.getElementById(licenceDomId)) {
        document.getElementById(licenceDomId).value = "";
      }
      return false;
    }
    maxDateIndex = maxDateIndex > parseInt(index) ? maxDateIndex : parseInt(index);
    //判断该车牌号是否已经有归属
    var r;
    APP_BCGOGO.Net.syncGet({url: "customer.do?method=licenceNoIsExisted", data: {licenceVal: licenceVal, customerName: $("#name").val()}, dataType: "json",
      success: function (json) {
        r = json;
      }
    });
    var  customerNameAjax = r[0].customerName;
    var customerId = r[0].customerId;
    if (!r || r.length == 0||G.isEmpty(customerNameAjax)) {
      return true;
    }

    //当前客户与原车主同名，给予提示
    if ($("#name").val() == customerNameAjax && $("#customerId").val() == "") {
      if (confirm("此车牌客户【" + customerNameAjax + "】已存在，是否修改客户资料为当前信息？")) {
        $("#customerId").val(customerId);
        licenceNoInput = $(this);
      } else {
        licenceVal = "";
        customerId = "";
        $("#customerId").val(customerId);
      }
    }else if ($("#name").val() != customerNameAjax) {  //与原车主不同名

      if(r[0].isObd =="true"){
        nsDialog.jAlert("此车牌【" + licenceVal + "】已属于其他客户【" + customerNameAjax + "】,是OBD车辆，不能添加");
        licenceVal = "";
        customerNameAjax = "";
        customerId = "";
        $(this).val("");
        flag=true;
        return false;
      }

      if (confirm("此车牌【" + licenceVal + "】已属于其他客户【" + customerNameAjax + "】，不能用于当前客户,是否转让到当前客户？")) {
        if (confirm("请再次确认此车的欠款是否已经结清，并办妥相关转让手续。")) {
          //将原车主的车牌关联信息删除
          customerNameAjax = "";
          customerId = "";
          var j;
          APP_BCGOGO.Net.syncGet({url: "customer.do?method=deleteCustomerLicenceNo", data: {licenceVal: licenceVal}, dataType: "json",
            success: function (json) {
              j = json;
            }
          });
        } else {
          licenceVal = "";
          customerNameAjax = "";
          customerId = "";
          $(this).val("");
           flag=false;
        return false;
        }
      } else {
        customerNameAjax = "";
        licenceVal = "";
        customerId = "";
        $(this).val("");
        flag=true;
        return false;
      }
    }

  });

//  if(!flag){
//    flag =checkGsmObdImei(maxDateIndex);
//  }

  return flag;
}
