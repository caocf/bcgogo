/**
 * Created by IntelliJ IDEA.
 * User: Lucien
 * Date: 12-4-9
 * Time: 下午12:55
 * To change this template use File | Settings | File Templates.
 */

/**********************new start**************************************************************************/
/**
 * 校验新增的手机列表里面号码是否有重复
 * 重复返回true 否则false
 * @param mobiles
 */
function isMobileDuplicate(mobiles) {

  var mobilesTemp = new Array();
  for (var index in  mobiles) {
    if (!G.isEmpty(mobiles[index])) {
      if (GLOBAL.Array.indexOf(mobilesTemp, mobiles[index]) >= 0) {
        nsDialog.jAlert("手机号【" + mobiles[index] + "】重复，请重新填写。");
        return true;
      }
      mobilesTemp.push(mobiles[index]);
    }
  }
  return false;
}
function redirectRepairOrder() {
  var firstCustomerVehicleLicenceNo = "";
  $(".J_customerVehicleDiv").find("input[name='licenceNo']").each(function () {
    firstCustomerVehicleLicenceNo = $(this).val();
    return false;
  });
  window.location = "txn.do?method=getRepairOrderByVehicleNumber&task=maintain&vehicleNumber=" + firstCustomerVehicleLicenceNo + "&customerId=" + $("#customerId").val() + "&customerName=" + $("#customerName").val();
}
function redirectSalesOrder() {
  window.location = "sale.do?method=getProducts&customerId=" + $("#customerId").val() + "&customerName=" + $("#customerName").val();
}
function redirectSalesReturn() {
  window.location = "salesReturn.do?method=createSalesReturn&customerId=" + $("#customerId").val() + "&customerName=" + $("#customerName").val();
}
function carWashBeauty() {
  window.location = "washBeauty.do?method=getCustomerInfoByName&customerId=" + $("#customerId").val();
}

function selectCard() {
  if ($("#customerId").val()) {
    var r = APP_BCGOGO.Net.syncGet({
      async: false,
      url: "customer.do?method=checkCustomerStatus",
      data: {
        customerId: $("#customerId").val(),
        now: new Date()
      },
      dataType: "json"
    });
    if (!r.success) {
      nsDialog.jAlert("此客户已被删除或合并，不能购卡！");
      return;
    }
    bcgogo.checksession({
      "parentWindow": window.parent,
      'iframe_PopupBox': $("#iframe_CardList")[0],
      'src': 'member.do?method=selectCardList&time=' + new Date()
    });
  } else {
    nsDialog.jAlert("请先选择客户");
  }
}

function returnCard(pageLinkedFrom) {
  if ($("#customerId").val()) {
    bcgogo.checksession({
      "parentWindow": window.parent,
      'iframe_PopupBox': $("#iframe_returnCard")[0],
      'src': 'member.do?method=returnCard&customerId=' + $("#customerId").val() + '&pageLinkedFrom=' + pageLinkedFrom + '&time=' + new Date()
    });
  } else {
    nsDialog.jAlert("请先选择客户");
  }
}

function searchOBDInfo(dom, searchWord){
  var is_sim_no_input=$(dom).hasClass("sim_no_input");
  var droplist = APP_BCGOGO.Module.droplist;
  searchWord = searchWord.replace(/\s/g, '');
  var uuid = GLOBAL.Util.generateUUID();
  droplist.setUUID(uuid);
  if(G.isEmpty(searchWord)){
    return;
  }
  var data={now: new Date().getTime()};
//  if(target_id=="sim_no_input"||(!G.isEmpty(target_id)&&target_id.contains("gsmObdImeiMoblie"))){
  if(is_sim_no_input){
    data['mobile']=searchWord;
  }else{
    data['imei']=searchWord;
  }
  $.post('OBDManager.do?method=getShopOBDSuggestion', data,function (list) {
    droplist.show({
      "selector": $(dom),
      "data": {
        uuid: uuid,
        data:$.map(list, function (n) {
//          if(target_id=="sim_no_input"||(!G.isEmpty(target_id)&&target_id.contains("gsmObdImeiMoblie"))){
          if(is_sim_no_input){
            n.label = n.mobile;
          }else{
            n.label = n.imei;
          }
          return n;
        })
      },
      "onSelect": function (event, index, data, hook) {
//        if(target_id=="sim_no_input"||(!G.isEmpty(target_id)&&target_id.contains("gsmObdImeiMoblie"))){
        if(is_sim_no_input){
          $(hook).val(data.mobile);
//          $("#imei_input").val(data.imei);
          $(dom).closest("tr").find(".imei_input").val(data.imei);
        }else{
          $(hook).val(data.imei);
//          $("#sim_no_input").val(data.mobile);
           $(dom).closest("tr").find(".sim_no_input").val(data.mobile);
        }

        droplist.hide();
      }
    });
  }, 'json');
}


/**********************new end**************************************************************************/

$(document).ready(function () {
  /************************new  start*************************************************************************************/
  $(".bind_obd_btn").live("click",function(){

    var data_index=$(this).attr("data-index");
    var vehicleId=$("#vehicleId"+data_index).val();
    $("#bind_vehicle_id").val(vehicleId);
    $("#vehicle_bind_okBtn").attr("data-index",data_index);
    $("#obd_bind_div").dialog({
      width: 380,
      height:170,
      modal: true,
      draggable:false,
      resizable: true,
      title: "绑定OBD",
      close:function(){
        $(".imei_input").val("");
        $(".sim_no_input").val("");
        $("#bind_vehicle_id").val("");
      }

    });

  });

  $(".imei_input,.sim_no_input,[id^='gsmObdImei']").live('focus',function () {
    searchOBDInfo(this, $(this).val());
  }).live("input",function(){
      searchOBDInfo(this, $(this).val());
    });

  $("#vehicle_bind_okBtn").click(function(){
    if($(this).attr("lock")){
      return;
    }
    var imei=$("#obd_bind_div .imei_input").val();
    var mobile=$("#obd_bind_div .sim_no_input").val();
    var vehicleId=$("#bind_vehicle_id").val();
    if(G.isEmpty(imei)){
      nsDialog.jAlert("请输入IMEI号！");
      $(this).removeAttr("lock");
      return;
    }
    if(G.isEmpty(mobile)){
      nsDialog.jAlert("请输入SIM卡号！");
      $(this).removeAttr("lock");
      return;
    }
    if(G.isEmpty(vehicleId)){
      nsDialog.jAlert("车辆信息异常！");
      $(this).removeAttr("lock");
      return;
    }
    if (!APP_BCGOGO.Validator.stringIsCharacter(imei)) {
      nsDialog.jAlert("IMEI号格式错误，请确认后重新输入！");
      $(this).removeAttr("lock");
      return;
    }
    if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
      nsDialog.jAlert("SIM卡号格式错误，请确认后重新输入！");
      $(this).removeAttr("lock");
      return;
    }
    var data_index=$(this).attr("data-index");
    $(this).attr("lock","lock");
    var _$me=$(this);
    APP_BCGOGO.Net.asyncGet({
      url:"OBDManager.do?method=gsmOBDBind",
      data:{
        imei:imei,
        mobile:mobile,
        vehicleId:vehicleId,
        now:new Date()
      },
      dataType:"json",
      success:function(result) {
        _$me.removeAttr("lock");
        if(G.isEmpty(result)){
          nsDialog.jAlert("保存异常！");
          return;
        }
        if(!result.success){
          nsDialog.jAlert(result.msg);
          return;
        }
        $("#bindSpan"+data_index).hide();
        var $show=$("#vehicleODBTableShow"+data_index);
        $show.find(".imei_span").text(imei);
        $show.find(".sim_no_span").text(mobile);
        $show.show();
        $("#obd_bind_div").dialog("close");
      },
      error:function(){
        $(this).removeAttr("lock");
      }
    });

  });

  $(".edit_obd_btn").live("click",function(){
    var data_index=$(this).attr("data-index");
    var imei=$("#vehicleODBTableShow"+data_index).find(".imei_span").text();
    var sim_no=$("#vehicleODBTableShow"+data_index).find(".sim_no_span").text();
    imei=$.trim(imei);
    sim_no=$.trim(sim_no);
    $("#obd_edit_div").dialog({
      width: 450,
      height:240,
      modal: true,
      draggable:false,
      resizable: true,
      title: "修改OBD",
      open:function(){
        $(".change_radio").click();
        $("#obd_edit_div").find(".imei_span").text(imei);
        $("#obd_edit_div").find(".sim_no_span").text(sim_no);
        $("#edit_data_index").val(data_index);
        $("#obd_edit_div .imei_input").val("");
        $("#obd_edit_div .sim_no_input").val("");
      }
    });
  });

  $("#delete_ok_opr_btn").click(function(){
    if($(this).attr("lock")){
      return;
    }
    var data_index=$("#edit_data_index").val();
    var imei=$("#vehicleODBTableShow"+data_index).find(".imei_span").text();
    var sim_no=$("#vehicleODBTableShow"+data_index).find(".sim_no_span").text();
    var _$me=$(this);
    APP_BCGOGO.Net.asyncGet({
      url:"OBDManager.do?method=unInstallGsmOBD",
      data:{
        imei:imei,
        mobile:sim_no,
        now:new Date()
      },
      dataType:"json",
      success:function(result) {
        _$me.removeAttr("lock");
        if(G.isEmpty(result)){
          nsDialog.jAlert("保存异常！");
          return;
        }
        if(!result.success){
          nsDialog.jAlert(result.msg);
          return;
        }
        $("#obd_edit_div").dialog("close");
        $("#vehicleODBTableShow"+data_index).hide();
        $("#bindSpan"+data_index).show();
      }
    });
  });

  $("#change_ok_opr_btn").click(function(){
    if($(this).attr("lock")){
      return;
    }
    var data_index=$("#edit_data_index").val();
    var vehicleId=$("#vehicleId"+data_index).val();
    var imei=$("#obd_edit_div").find(".imei_input").val();
    var mobile=$("#obd_edit_div").find(".sim_no_input").val();
    if(G.isEmpty(vehicleId)){
      nsDialog.jAlert("车辆信息异常！");
      $(this).removeAttr("lock");
      return;
    }
    var imei_old=$("#obd_edit_div").find(".imei_span").text();
    var mobile_old=$("#obd_edit_div").find(".sim_no_span").text();
    if(G.isEmpty(imei)){
      nsDialog.jAlert("请输入IMEI号！");
      $(this).removeAttr("lock");
      return;
    }
    if(G.isEmpty(mobile)){
      nsDialog.jAlert("请输入SIM卡号！");
      $(this).removeAttr("lock");
      return;
    }
    if (!APP_BCGOGO.Validator.stringIsCharacter(imei)) {
      nsDialog.jAlert("IMEI号格式错误，请确认后重新输入！");
      $(this).removeAttr("lock");
      return;
    }
    if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
      nsDialog.jAlert("SIM卡号格式错误，请确认后重新输入！");
      $(this).removeAttr("lock");
      return;
    }
    if(imei==imei_old){
      nsDialog.jAlert("要更换OBD的IMEI号和当前OBD的IMEI号重复！");
      $(this).removeAttr("lock");
      return;
    }
    if(mobile==mobile_old){
      nsDialog.jAlert("要更换OBD的SIM卡号和当前OBD的SIM卡号重复！");
      $(this).removeAttr("lock");
      return;
    }
    var _$me=$(this);
    APP_BCGOGO.Net.asyncGet({
      url:"OBDManager.do?method=gsmOBDBind",
      data:{
        imei:imei,
        mobile:mobile,
        vehicleId:vehicleId,
        opr_type:"change_opr",
        now:new Date()
      },
      dataType:"json",
      success:function(result) {
        _$me.removeAttr("lock");
        if(G.isEmpty(result)){
          nsDialog.jAlert("保存异常！");
          return;
        }
        if(!result.success){
          nsDialog.jAlert(result.msg);
          return;
        }
        $("#obd_edit_div").dialog("close");
        $("#vehicleODBTableShow"+data_index).find(".imei_span").text(imei);
        $("#vehicleODBTableShow"+data_index).find(".sim_no_span").text(mobile);
         $("#gsmObdImeiMoblie"+data_index).val(mobile);
         $("#gsmObdImei"+data_index).val(imei);
      },error:function(){
        $(this).removeAttr("lock");
      }
    });
  });

  $('[name="s_radio"]').click(function(){
    if($(this).hasClass("change_radio")){
      $("#change_obd_tr").show();
      $("#change_opr_div").show();
      $("#delete_opr_div").hide();

    }else{
      $("#change_obd_tr").hide();
      $("#change_opr_div").hide();
      $("#delete_opr_div").show();
    }
  });

  $(".cancel_opr_btn").click(function(){
    $("#obd_edit_div").dialog("close");
  });

  $("#vehicle_bind_cancelBtn").click(function(){
    $("#obd_bind_div").dialog("close");
  });

  $("#birthdayString").datepicker({
    "numberOfMonths": 1,
    "showButtonPanel": true,
    "changeYear": true,
    "changeMonth": true,
    "dateFormat": "mm-dd",
    "yearRange": "c-100, c",
    "yearSuffix": ""
  });
  $("#returnCustomerListBtn").live("click", function (e) {
    e.preventDefault();
    window.location.href = "customer.do?method=customerdata";
  });
  $("#customerConsumerHistoryBtn,.J_customerConsumeHistory").live("click", function (e) {
    e.preventDefault();
    openWindow("inquiryCenter.do?method=inquiryCenterIndex&pageType=customerOrSupplier&startDateStr=&customerOrSupplier=" + $("#customerName").val());
  });
  $("#memberConsumerHistoryBtn").live("click", function (e) {
    e.preventDefault();
    var memberNo = $(this).attr("data-member-no");
    var orderType = "WASH_BEAUTY,REPAIR,SALE";

    openWindow("inquiryCenter.do?method=inquiryCenterIndex&pageType=customerOrSupplier&startDateStr=&memberNo=" + memberNo+"&orderTypes=" + orderType);
  });

  $(".J_CustomerVehicleConsumerHistory").live("click", function (e) {
    e.preventDefault();
    var dataIndex = $(this).attr("data-index");
    var vehicleNumber = $("#licenceNo" + dataIndex).val();
    var vehicleId = $("#vehicleId" + dataIndex).val();

    var vehicleModel = $("#model" + dataIndex).val();
    var vehicleBrand = $("#brand" + dataIndex).val();
    var vehicleColor = $("#color" + dataIndex).val();

    var str = (!G.Lang.isEmpty(vehicleModel) ? '&vehicleModel= ' + vehicleModel :'') + (!G.Lang.isEmpty(vehicleBrand) ? '&vehicleBrand= ' + vehicleBrand :'') + (!G.Lang.isEmpty(vehicleColor) ? '&vehicleColor= ' + vehicleColor :'');
    openWindow("inquiryCenter.do?method=inquiryCenterIndex&pageType=customerOrSupplier&startDateStr=&vehicleNumber=" + vehicleNumber + "&vehicleId=" + vehicleId + str);
  });
  $(".J_customerVehicleDiv").find("input[name='licenceNo']").live("keyup", function () {
    this.value = $.trim(this.value.toUpperCase());
  });

  $("#registerMaintainBtn").live("click", function () {
    var dataIndex = $(this).attr("data-index");
    if(G.isEmpty(dataIndex)){
      return;
    }
    var vehicleId = $("#vehicleId" + dataIndex).val();
    var customerId = $("#customerId").val();
    var $customerVehicleDiv = $("#customerVehicleDiv" + dataIndex);
    $("#saveMaintainRegister").attr("data-index",dataIndex);
    $("#cancelMaintainRegister").attr("data-index",dataIndex);
    $("#maintainRegisterForm").find("input[name='vehicleId']").val(vehicleId);
    $("#maintainRegisterForm").find("input[name='customerId']").val(customerId);

    $("#maintainRegisterDiv").dialog({
      width: 500,
      modal: true,
      resizable: false,
      position: 'center',
      open: function () {
        $("#registerMaintainMileagePeriod").val($("#maintainMileagePeriod" + dataIndex).val());
        $("#registerMaintainTimePeriod").val($("#maintainTimePeriodStr" + dataIndex).val());

        $("#lastMaintainMileage").val($("#obdMileage" + dataIndex).val());
        $(".ui-dialog-titlebar", $(this).parent()).hide();
        $("#registerMaintainTime").val($("#today").val());
        $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
      },
      close: function () {
        $("#lastMaintainMileage").val("");
        $("#registerMaintainMileagePeriod").val("");
        $("#registerMaintainTime").val("");
        $("#registerMaintainTimePeriod").val("");
      }
    });
  });


  $("#saveMaintainRegister").live("click", function () {
    var $vehicleMaintainRegister = $("#maintainRegisterForm");
    var dataIndex = $(this).attr("data-index");
    if(G.isEmpty(dataIndex)){
      return;
    }
    $vehicleMaintainRegister.ajaxSubmit({
      dataType: "json",
      type: "POST",
      success: function (data) {
        if (data.success) {

          var $customerVehicleDiv = $("#customerVehicleDiv" +dataIndex);


          $customerVehicleDiv.find("span[data-key='lastMaintainMileage']").text(G.isEmpty($("#lastMaintainMileage").val()) ? "--" : ($("#lastMaintainMileage").val() + "公里"));
          $customerVehicleDiv.find("span[data-key='lastMaintainTimeStr']").text(G.isEmpty($("#registerMaintainTime").val()) ? "--" : $("#registerMaintainTime").val());
          $customerVehicleDiv.find("span[data-key='maintainMileagePeriod']").text(G.isEmpty($("#registerMaintainMileagePeriod").val()) ? "--" : ($("#registerMaintainMileagePeriod").val() + "公里"));

          var next = data.data.nextMaintainMileageAccessStr;
          if (G.isNotEmpty(next)) {
            $customerVehicleDiv.find("span[data-key='nextMaintainMileageAccessStr']").text(next);
          } else {
            $customerVehicleDiv.find("span[data-key='nextMaintainMileageAccessStr']").text("--");
          }
          var $customerVehicleEditDiv = $("#customerVehicleEditDiv"+dataIndex);
          $customerVehicleEditDiv.find("span[data-key='lastMaintainMileage']").text(G.isEmpty($("#lastMaintainMileage").val()) ? "--" : ($("#lastMaintainMileage").val() + "公里"));
          $customerVehicleEditDiv.find("span[data-key='lastMaintainTimeStr']").text(G.isEmpty($("#registerMaintainTime").val()) ? "--" : $("#registerMaintainTime").val());


          $("#maintainMileagePeriod" +dataIndex).val($("#registerMaintainMileagePeriod").val());
          $("#maintainTimePeriodStr"+dataIndex).val($("#registerMaintainTimePeriod").val());

          $customerVehicleEditDiv.find("span[data-key='nextMaintainMileageAccessStr']").text(next);


          if (G.isEmpty(data.data.maintainMileage)) {
            $(".J_maintainMileageUnitSpan").hide();
          } else {
            $(".J_maintainMileageUnitSpan").show();
            $("#appointServiceTableShow" +dataIndex).find("span[data-key='maintainMileage']").text(data.data.maintainMileage);
            $("#maintainMileage"+dataIndex).val(data.data.maintainMileage);
          }

          if (!G.isEmpty(data.data.maintainTimeStr)) {
            $("#appointServiceTableShow" +dataIndex).find("span[data-key='maintainTimeStr']").text(data.data.maintainTimeStr);
            $("#by"+dataIndex).val(data.data.maintainTimeStr);
          }
        }
        $("#maintainRegisterDiv").dialog("close");

      },
      error: function (json) {
        nsDialog.jAlert("网络异常，请联系客服");
      }
    });
  });
  $("#cancelMaintainRegister").live("click", function () {
    $("#maintainRegisterDiv").dialog("close");
  });


  $("#addCustomerVehicleBtn").bind("click", function (e) {
    $('div[data-node-type=slider]').find('a').removeClass("normal_btn2").addClass("hover_btn2");
    $(this).removeClass("hover_btn2").addClass("normal_btn2");

    $("#customerVehicleContainer").find(".J_customerVehicleDiv").hide();

    var containNew = false;
    $("#customerVehicleContainer").find(".J_customerVehicleDiv").each(function () {
      if (G.Lang.isEmpty($(this).find("input[name$='vehicleId']").val())) {
        $(this).show();
        containNew = true;
        return;
      }
    });
    if (containNew) {
      return;
    }

    if ($(e.target).hasClass("normal_btn") && $(e.target).parent().find('.J_customerVehicleTitle').size() != 0) {
      return;
    }

    $(e.target).parent().find('a').removeClass("normal_btn").removeClass("hover_btn").addClass("hover_btn");
    $(e.target).removeClass("normal_btn").removeClass("hover_btn").addClass("normal_btn");

    $("#customerVehicleContainer").find(".J_customerVehicleDiv").hide();

    $("#customerVehicleContainer").find(".J_customerVehicleDiv").each(function () {
      if (G.Lang.isEmpty($(this).find("input[name$='vehicleId']").val())) {
        $(this).show();
        return;
      }
    });
    var lastDataIndex = 0;
    var $customerVehicleDivs = $("#customerVehicleContainer").find(".J_customerVehicleDiv");
    var count = $customerVehicleDivs.length;
    $customerVehicleDivs.each(function () {
      var dataIndex = parseInt($(this).attr("data-index"));
      if (G.Lang.isEmpty($("#vehicleId" + dataIndex).val())) {
        lastDataIndex = -1;
        return;
      }
      if (lastDataIndex <= dataIndex) {
        lastDataIndex = dataIndex + 1;
      }
    });
    if (lastDataIndex == -1) {
      return;
    }
    var divHtml = $("#customerVehicleTemplate").clone();
    count = count+1;

    $(divHtml).find("[id]").each(function () {
      if ($(this).attr("class") != "J_addAppointService") {
        $(this).attr("id", $(this).attr("id") + lastDataIndex);
      }
    });
    $(divHtml).find("[data-index='']").each(function () {
      $(this).attr("data-index", lastDataIndex);
    });
    $("#customerVehicleContainer").append($(divHtml).html());
    e.preventDefault();
  });

  $(".J_customerVehicleDiv").find("input[name='brand'],input[name='model']")
    .live("click", function (event) {
      var rowIndex = $(this).parents(".J_customerVehicleDiv").attr("data-index");
      var searchField = $(this).attr("name");
      var brandValue = $("#brand" + rowIndex).val();
      var modelValue = $("#model" + rowIndex).val();
      var eventKeyCode = event.which || event.keyCode;
      searchVehicleSuggestion(this,eventKeyCode, searchField, brandValue, modelValue, rowIndex);
    })
    .live("keyup", function (event) {
      var rowIndex = $(this).parents(".J_customerVehicleDiv").attr("data-index");
      var searchField = $(this).attr("name");
      var brandValue = $("#brand" + rowIndex).val();
      var modelValue = $("#model" + rowIndex).val();
      var eventKeyCode = event.which || event.keyCode;
      searchVehicleSuggestion(this,eventKeyCode, searchField, brandValue, modelValue, rowIndex);
    });


  $(".J_customerVehicleBuyDate").live("change", function () {
    var nowTime = new Date().getTime();
    var str = $(this).val();
    str = str.replace(/-/g, "/");
    var selectTime = new Date(str).getTime();
    if (nowTime < selectTime) {
      nsDialog.jAlert("购车日期不能晚于当前日期！");
      $(this).val("");
    }
  });

  $(".J_saveCustomerVehicleBtn").live("click", function (event) {
    event.preventDefault();
    var dataIndex = $(this).attr("data-index");
    var vehicleId = $("#vehicleId" + dataIndex).val();
    var $customerVehicleForm = $("#customerVehicleForm" + dataIndex);
    var licenceNo = $customerVehicleForm.find("input[name='licenceNo']").val();
    var obdMileage = $customerVehicleForm.find("input[name='obdMileage']").val();
    var gsmObdImei = $customerVehicleForm.find("input[name='gsmObdImei']").val();
    var gsmObdImeiMoblie = $customerVehicleForm.find("input[name='gsmObdImeiMoblie']").val();
    var vin = $customerVehicleForm.find("input[name='vin']").val();
    var engineNo = $customerVehicleForm.find("input[name='engineNo']").val();
    var flag = false;
    if (G.Lang.isEmpty(licenceNo)) {
      nsDialog.jAlert("车牌号空缺，请完善必要的车辆信息！");
      return;
    } else if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber(licenceNo)) {
      nsDialog.jAlert("车牌号格式错误，请确认后重新输入！");
      return;
    } else if (!G.Lang.isEmpty(vin) && !APP_BCGOGO.Validator.stringIsCharacter(vin)) {
      nsDialog.jAlert("车架号格式错误，请确认后重新输入！");
      return;
    } else if (!G.Lang.isEmpty(engineNo) && !APP_BCGOGO.Validator.stringIsCharacter(engineNo)) {
      nsDialog.jAlert("发动机号格式错误，请确认后重新输入！");
      return;
    }  else if (!G.Lang.isEmpty(gsmObdImei) && !APP_BCGOGO.Validator.stringIsCharacter(gsmObdImei)) {
      nsDialog.jAlert("IMEI号格式错误，请确认后重新输入！");
      return;
    }   else if (!G.Lang.isEmpty(gsmObdImeiMoblie) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(gsmObdImeiMoblie)) {
      nsDialog.jAlert("SIM卡号格式错误，请确认后重新输入！");
      return;
    } else if (!G.Lang.isEmpty(obdMileage) && !APP_BCGOGO.Validator.stringIsStartMileage(obdMileage)) {
      nsDialog.jAlert("当前里程格式错误，请确认后重新输入！");
      return;
    }
    if ((G.isEmpty(gsmObdImei) &&!G.isEmpty(gsmObdImeiMoblie))||(!G.isEmpty(gsmObdImei) &&G.isEmpty(gsmObdImeiMoblie))) {
      nsDialog.jAlert("请将OBD信息输入完整！");
      return;
    }
    //保存前，判断是否有重名的车牌号
    var array = $("input[name='licenceNo']");
    for (var i = 0; i < array.length; i++) {
      if (licenceNo == array[i].value && ("licenceNo" + dataIndex) != array[i].id) {
        flag = true;
      }
    }
    if (flag||checkLicenceNoIsExisted(licenceNo)) {
      nsDialog.jAlert("存在重复车牌号！");
      return;
    }
    if((G.isEmpty(gsmObdImeiMoblie)&&!G.isEmpty(gsmObdImei))||(!G.isEmpty(gsmObdImeiMoblie)&&G.isEmpty(gsmObdImei))){
      nsDialog.jAlert("OBD信息不完整，请确认后重新输入！");
      return;
    }
    APP_BCGOGO.Net.syncGet({
      url: "customer.do?method=checkIsExistGsmObdImeiInVehicle",
      data: {
        gsmObdImei: gsmObdImei,
        gsmObdImeiMoblie:gsmObdImeiMoblie,
        vehicleId: vehicleId
      },
      dataType: "json",
      success: function (json) {
        if(!json.success){
          nsDialog.jAlert(json.msg);
          return true;
        }
        $customerVehicleForm.ajaxSubmit({
          dataType: "json",
          type: "POST",
          success: function (jsonStr) {
            if (!G.Lang.isEmpty(jsonStr.vehicleId)) {

              $("#customerVehicleId" + jsonStr.vehicleId).text(licenceNo);

              $customerVehicleForm.find("input[name='vehicleId']").val(jsonStr.vehicleId);
              if ($("#customerVehicleId" + jsonStr.vehicleId).length == 0) {

                $("#customerVehicleTitle").find('a').removeClass("normal_btn").removeClass("hover_btn").addClass("hover_btn");

                var vehicleDataIndex = 0;

                if ($("div[data-node-type=slider]").find("a").size() == 0) {
                  vehicleDataIndex = 0;
                }else{
                  vehicleDataIndex = parseInt($("div[data-node-type=slider]").find("a:last").attr("data-index")) + 1
                }
                if($("div[data-node-type=slider]").find("a").length == 0){
                  $("div[data-node-type=slider]").append('<a id="customerVehicleId' + jsonStr.vehicleId + '"  data-index="' + vehicleDataIndex + '" class="normal_btn2 J_customerVehicleTitle" ' +'style="margin:5px 0px 0 0px">' + jsonStr.customerVehicleResponse.licenceNo + '</a>');
                }else{
                  $("div[data-node-type=slider]").find("a:last").after('<a id="customerVehicleId' + jsonStr.vehicleId + '"  data-index="' + vehicleDataIndex + '" class="normal_btn2 J_customerVehicleTitle" ' +'style="margin:5px 0px 0 0px">' + jsonStr.customerVehicleResponse.licenceNo + '</a>');
                }
                slider.end();
              }

              var $customerVehicleDiv = $("#customerVehicleDiv" + dataIndex);
              //更新4条提醒服务
              $customerVehicleDiv.find(".J_customerVehicleAppointSpan").each(function (index) {
                $(this).text(G.isEmpty(G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")])) ? "--" : G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")]));
              });

//                                //更新 自定义的预约
              $customerVehicleDiv.find(".J_appointServiceTrShow").remove();
              $customerVehicleDiv.find(".J_appointServiceTrEdit").remove();
              var appointServiceDTOs =jsonStr.customerVehicleResponse["appointServiceDTOs"];
              if (!G.isEmpty(appointServiceDTOs)) {
                var newAppointServiceEditTrHtml = "";
                var newAppointServiceShowTrHtml = "";
                $.each(appointServiceDTOs, function (index, appointServiceDTO) {

                  if (appointServiceDTO.operateType != 'LOGIC_DELETE') {
                    if (index % 2 == 0) {
                      newAppointServiceEditTrHtml += '<tr class="J_appointServiceTrEdit">';
                      newAppointServiceShowTrHtml += '<tr class="J_appointServiceTrShow">';
                    }

                    newAppointServiceEditTrHtml += '  <td class="test2">' +
                      ' <input type="hidden" name="appointServiceDTOs[' + index + '].appointName"' +
                      ' value="' + appointServiceDTO["appointName"] + '"/>' + appointServiceDTO["appointName"] + '：</td>';
                    newAppointServiceEditTrHtml += '  <td class="J_appointServiceDateTd">';
                    newAppointServiceEditTrHtml += '      <input type="hidden" name="appointServiceDTOs[' + index + '].id" value="' + appointServiceDTO["idStr"] + '">';
                    newAppointServiceEditTrHtml += '      <input type="hidden" name="appointServiceDTOs[' + index + '].operateType" value="' + appointServiceDTO["operateType"] + '">';
                    newAppointServiceEditTrHtml += '      <input type="text" style="width:75px" onclick="showDatePicker(this);" readonly="readonly" name="appointServiceDTOs[' + index + '].appointDate" reset-value="' + appointServiceDTO["appointDate"] + '" value="' + appointServiceDTO["appointDate"] + '" class="txt J_formreset"/>';
                    newAppointServiceEditTrHtml += '   <a data-index="' + index + '" class="J_deleteAppointService"><img src="images/opera1.png"/></a>';
                    if (index == appointServiceDTOs.length - 1) {
                      newAppointServiceEditTrHtml += '   <a class="J_addAppointService" data-index="' + index + '"><img src="images/opera2.png"/></a>';
                    }
                    newAppointServiceEditTrHtml += '  </td>';
                    newAppointServiceShowTrHtml += '  <td class="">' + appointServiceDTO["appointName"] + '：' + appointServiceDTO["appointDate"] + '</td>';

                    if (index % 2 == 0 && index == appointServiceDTOs.length - 1) {
                      newAppointServiceShowTrHtml += '<td></td>';
                    }
                    if (index % 2 == 1 || index == appointServiceDTOs.length - 1) {
                      newAppointServiceEditTrHtml += '</tr>';
                      newAppointServiceShowTrHtml += '</tr>';
                    }
                  }


                });
                $("#appointServiceTableShow" + dataIndex).append(newAppointServiceShowTrHtml);
                $("#appointServiceTableEdit" + dataIndex).append(newAppointServiceEditTrHtml);
              }


              //更新span
              $customerVehicleDiv.find(".J_customerVehicleSpan").each(function (index) {
                $(this).text(G.isEmpty(G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")])) ? "--" : G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")]));
              });

              if (G.isEmpty(jsonStr.customerVehicleResponse.model) && G.isEmpty(jsonStr.customerVehicleResponse.brand)) {
                $customerVehicleDiv.find("span[data-key='brand']").parent("td").html('品牌车型：<span class="J_customerVehicleSpan" data-key="brand">-</span>' +
                  '<span class="J_customerVehicleSpan" data-key="model">-</span>');
              } else {
                $customerVehicleDiv.find("span[data-key='brand']").parent("td").html('品牌车型：<span class="J_customerVehicleSpan" data-key="brand">' + jsonStr.customerVehicleResponse.brand + '</span>' +
                  '/<span class="J_customerVehicleSpan" data-key="model">' + jsonStr.customerVehicleResponse.model + '</span>');
              }

              if (!G.isEmpty(jsonStr.customerVehicleResponse.obdMileage)) {
                $customerVehicleDiv.find("span[data-key='obdMileage']").parent("td").html('当前里程：<span class="J_customerVehicleSpan" data-key="obdMileage">' + jsonStr.customerVehicleResponse.obdMileage + '</span>公里 <a id="registerMaintainBtn" data-index="' + dataIndex + '" class="blue_color">保养登记</a>');
              } else {
                $customerVehicleDiv.find("span[data-key='obdMileage']").parent("td").html('当前里程：<span class="J_customerVehicleSpan" data-key="obdMileage">--</span><a id="registerMaintainBtn" data-index="' + dataIndex + '" class="blue_color">保养登记</a>');
              }




              $customerVehicleDiv.find("span[data-key='lastMaintainMileage']").text(G.isEmpty(jsonStr.customerVehicleResponse.lastMaintainMileage) ? "--" : (jsonStr.customerVehicleResponse.lastMaintainMileage + "公里"));
              $customerVehicleDiv.find("span[data-key='lastMaintainTimeStr']").text(G.isEmpty(jsonStr.customerVehicleResponse.lastMaintainTimeStr) ? "--" : jsonStr.customerVehicleResponse.lastMaintainTimeStr);
              $customerVehicleDiv.find("span[data-key='maintainMileagePeriod']").text(G.isEmpty(jsonStr.customerVehicleResponse.maintainMileagePeriod) ? "--" : (jsonStr.customerVehicleResponse.maintainMileagePeriod + "公里"));

              var next = jsonStr.customerVehicleResponse.nextMaintainMileageAccessStr;
              if (G.isNotEmpty(next)) {
                $customerVehicleDiv.find("span[data-key='nextMaintainMileageAccessStr']").text(next);
              } else {
                $customerVehicleDiv.find("span[data-key='nextMaintainMileageAccessStr']").text("--");
              }


              if (G.isEmpty(jsonStr.customerVehicleResponse.color) && G.isEmpty(jsonStr.customerVehicleResponse.year) && G.isEmpty(jsonStr.customerVehicleResponse.engine)) {

                $customerVehicleDiv.find("span[data-key='color']").parent("td").html('年代/排量/颜色：<span class="J_customerVehicleSpan" data-key="year">-</span>' +
                  '<span class="J_customerVehicleSpan" data-key="engine">-</span><span class="J_customerVehicleSpan" data-key="color"></span>');
              } else {
                $customerVehicleDiv.find("span[data-key='color']").parent("td").html('年代/排量/颜色：<span class="J_customerVehicleSpan" data-key="year">' + (G.isEmpty(jsonStr.customerVehicleResponse.year)?'--':jsonStr.customerVehicleResponse.year) + '</span>' +
                  '/<span class="J_customerVehicleSpan" data-key="engine">' + (G.isEmpty(jsonStr.customerVehicleResponse.engine)?'--':jsonStr.customerVehicleResponse.engine) + '</span>/<span class="J_customerVehicleSpan" data-key="color">' + (G.isEmpty(jsonStr.customerVehicleResponse.color)?'--':jsonStr.customerVehicleResponse.color) + '</span>');
              }

              if(G.isEmpty(jsonStr.customerVehicleResponse["maintainMileage"])){
                $customerVehicleDiv.find(".J_maintainMileageUnitSpan").hide();
              }else{
                $customerVehicleDiv.find(".J_maintainMileageUnitSpan").show();
              }


              $customerVehicleForm.find(".J_formreset").each(function () {
                $(this).attr("reset-value", G.Lang.normalize($(this).val()));
              });

              $("#customerVehicleAppointInfoShow" + dataIndex).show();
              $("#customerVehicleOBDInfoShow" + dataIndex).show();
              $("#customerVehicleBasicInfoShow" + dataIndex).show();
              $("#customerVehicleAppointInfoEdit" + dataIndex).hide();
              $("#customerVehicleOBDInfoEdit" + dataIndex).hide();
              $("#customerVehicleBasicInfoEdit" + dataIndex).hide();
               var gsmObdImei=jsonStr.customerVehicleResponse["gsmObdImei"];
              var gsmObdImeiMoblie=jsonStr.customerVehicleResponse["gsmObdImeiMoblie"];
              if(!G.isEmpty(gsmObdImei)&&!G.isEmpty(gsmObdImeiMoblie)){
                $("#vehicleODBTableShow"+dataIndex).show();
              }else{
                $("#bindSpan"+dataIndex).show();
              }
              showMessage.fadeMessage("45%", "24%", "slow", 3000, "更新成功！");
            } else {
              nsDialog.jAlert("更新失败！");
            }
          },
          error: function (json) {
            nsDialog.jAlert("网络异常，请联系客服");
          }
        });

      },
      error: function (json) {
        nsDialog.jAlert("网络异常，请联系客服");
      }

    });

  });

  $(".J_createAppointOrder").live("click", function (e) {
    e.preventDefault();
    var dataIndex = $(this).attr("data-index");
    var $customerVehicleForm = $("#customerVehicleForm" + dataIndex);
    var vehicleId = $customerVehicleForm.find("input[name='vehicleId']").val();
    var customerId = $("#customerId").val();
    APP_BCGOGO.Net.syncPost({
      url: "appoint.do?method=validateCreateAppointOrder",
      dataType: "json",
      success: function (result) {
        if (result && result.success) {
          var url = "appoint.do?method=createAppointOrderByCustomerInfo";
          if (G.Lang.isNotEmpty(vehicleId)) {
            url = url + "&vehicleId=" + vehicleId;
          }
          if (G.Lang.isNotEmpty(customerId)) {
            url = url + "&id=" + customerId;
          }
          window.location.href = url;
        } else if (result && !result.success) {
          nsDialog.jConfirm(result.msg, null, function (value) {
            if (value) {
              window.location.href = result.data;
            }
          });
        }
      },
      error: function () {
        nsDialog.jAlert("网络异常！");
      }
    });
  });

  $(".J_add_newOrder").live("click", function (e) {
    e.preventDefault();
    var customerId = $("#customerId").val();
    APP_BCGOGO.Net.syncPost({
      url: "appoint.do?method=validateCreateAppointOrder",
      dataType: "json",
      success: function (result) {
        if (result && result.success) {
          var url = "appoint.do?method=createAppointOrderByCustomerInfo";
          if (G.Lang.isNotEmpty(customerId)) {
            url = url + "&id=" + customerId;
          }
          window.location.href = url;
        } else if (result && !result.success) {
          nsDialog.jConfirm(result.msg, null, function (value) {
            if (value) {
              window.location.href = result.data;
            }
          });
        }
      },
      error: function () {
        nsDialog.jAlert("网络异常！");
      }
    });
  });

  $(".J_saveCustomerVehicleAppointBtn").live("click", function (event) {
    event.preventDefault();
    var dataIndex = $(this).attr("data-index");
    var $customerVehicleAppointForm = $("#customerVehicleAppointForm" + dataIndex);
    var $customerVehicleForm = $("#customerVehicleForm" + dataIndex);

    var vehicleId = $customerVehicleForm.find("input[name='vehicleId']").val();

    if (G.Lang.isEmpty(vehicleId)) {
      nsDialog.jAlert("请先保存车辆信息");
      return;
    }

    var validateFlag = false;
//    $customerVehicleAppointForm.find("input").each(function () {
//      if ($(this).attr("type") != "hidden") {
//        if (G.Lang.isEmpty($(this).val())) {
//          nsDialog.jAlert("请填写提醒服务");
//          validateFlag = true;
//          return;
//        }
//      }
//    });

    if(validateFlag == true){
      return;
    }

    $customerVehicleAppointForm.ajaxSubmit({
      dataType: "json",
      type: "POST",
      success: function (jsonStr) {
        if (!G.Lang.isEmpty(jsonStr.vehicleId)) {
          $customerVehicleForm.find("input[name='vehicleId']").val(jsonStr.vehicleId);
          var $customerVehicleDiv = $("#customerVehicleDiv" + dataIndex);
          //更新 自定义的预约

          $customerVehicleDiv.find(".J_appointServiceTrShow").remove();
          $customerVehicleDiv.find(".J_appointServiceTrEdit").remove();
          var appointServiceDTOs = jsonStr.customerVehicleResponse["appointServiceDTOs"];
          if (!G.isEmpty(appointServiceDTOs)) {
            var newAppointServiceEditTrHtml = "";
            var newAppointServiceShowTrHtml = "";
            $.each(appointServiceDTOs, function (index, appointServiceDTO) {

              if (appointServiceDTO.operateType != 'LOGIC_DELETE' && (!(G.isEmpty(appointServiceDTO.appointName) && G.isEmpty(appointServiceDTO.appointDate))) ) {
                if (index % 2 == 0) {
                  newAppointServiceEditTrHtml += '<tr class="J_appointServiceTrEdit">';
                  newAppointServiceShowTrHtml += '<tr class="J_appointServiceTrShow">';
                }

                newAppointServiceEditTrHtml += '  <td class="test2">' +
                  ' <input type="hidden" name="appointServiceDTOs[' + index + '].appointName"' +
                  ' value="' + appointServiceDTO["appointName"] + '"/>' + appointServiceDTO["appointName"] + '：</td>';
                newAppointServiceEditTrHtml += '  <td>';
                newAppointServiceEditTrHtml += '      <input type="hidden" name="appointServiceDTOs[' + index + '].id" value="' + appointServiceDTO["idStr"] + '">';
                newAppointServiceEditTrHtml += '      <input type="hidden" name="appointServiceDTOs[' + index + '].operateType" value="' + appointServiceDTO["operateType"] + '">';
                newAppointServiceEditTrHtml += '      <input type="text"  style="width:75px" onclick="showDatePicker(this);" readonly="readonly" name="appointServiceDTOs[' + index + '].appointDate" reset-value="' + appointServiceDTO["appointDate"] + '" value="' + appointServiceDTO["appointDate"] + '" class="txt J_formreset"/>';
                newAppointServiceEditTrHtml += '   <a data-index="' + index + '" class="J_deleteAppointService"><img src="images/opera1.png"/></a>';
                if (index == appointServiceDTOs.length -1) {
                  newAppointServiceEditTrHtml += '   <a class="J_addAppointService" data-index="' + index + '"><img src="images/opera2.png"/></a>';
                }
                newAppointServiceEditTrHtml += '  </td>';
                newAppointServiceShowTrHtml += '  <td>' + appointServiceDTO["appointName"] + '：' + appointServiceDTO["appointDate"] + '</td>';

                if (index % 2 == 0 && index == appointServiceDTOs.length - 1) {
                  newAppointServiceShowTrHtml += '<td></td>';
                }
                if (index % 2 == 1 || index == appointServiceDTOs.length - 1) {
                  newAppointServiceEditTrHtml += '</tr>';
                  newAppointServiceShowTrHtml += '</tr>';
                }
              }


            });
            $("#appointServiceTableShow" + dataIndex).append(newAppointServiceShowTrHtml);
            $("#appointServiceTableEdit" + dataIndex).append(newAppointServiceEditTrHtml);
          }else{

            if ($("#appointServiceTableEdit" + dataIndex).find(".J_addAppointService").size() == 0) {
              $("#appointServiceTableEdit" + dataIndex).find("input[name='yc']").after('<a class="J_addAppointService" data-index="0"><img src="images/opera2.png"/></a>');
            }

          }

          //更新span
          $customerVehicleDiv.find(".J_customerVehicleSpan").each(function (index) {
            $(this).text(G.isEmpty(G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")])) ? "--" : G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")]));
          });


          if (G.isEmpty(jsonStr.customerVehicleResponse.model) && G.isEmpty(jsonStr.customerVehicleResponse.brand)) {
            $customerVehicleDiv.find("span[data-key='brand']").parent("td").html('品牌车型：<span class="J_customerVehicleSpan" data-key="brand">-</span>' +
              '<span class="J_customerVehicleSpan" data-key="model">-</span>');
          } else {
            $customerVehicleDiv.find("span[data-key='brand']").parent("td").html('品牌车型：<span class="J_customerVehicleSpan" data-key="brand">' + jsonStr.customerVehicleResponse.brand + '</span>' +
              '/<span class="J_customerVehicleSpan" data-key="model">' + jsonStr.customerVehicleResponse.model + '</span>');
          }

          if (!G.isEmpty(jsonStr.customerVehicleResponse.obdMileage)) {
            $customerVehicleDiv.find("span[data-key='obdMileage']").parent("td").html('当前里程：<span class="J_customerVehicleSpan" data-key="obdMileage">' + jsonStr.customerVehicleResponse.obdMileage + '</span>公里');
          } else {
            $customerVehicleDiv.find("span[data-key='obdMileage']").parent("td").html('当前里程：<span class="J_customerVehicleSpan" data-key="obdMileage">--</span>');
          }

          if (G.isEmpty(jsonStr.customerVehicleResponse.color) && G.isEmpty(jsonStr.customerVehicleResponse.year) && G.isEmpty(jsonStr.customerVehicleResponse.engine)) {

            $customerVehicleDiv.find("span[data-key='color']").parent("td").html('年代/排量/颜色：<span class="J_customerVehicleSpan" data-key="year">-</span>' +
              '<span class="J_customerVehicleSpan" data-key="engine">-</span><span class="J_customerVehicleSpan" data-key="color"></span>');
          } else {
            $customerVehicleDiv.find("span[data-key='color']").parent("td").html('年代/排量/颜色：<span class="J_customerVehicleSpan" data-key="year">' + (G.isEmpty(jsonStr.customerVehicleResponse.year) ? '--' : jsonStr.customerVehicleResponse.year) + '</span>' +
              '/<span class="J_customerVehicleSpan" data-key="engine">' + (G.isEmpty(jsonStr.customerVehicleResponse.engine) ? '--' : jsonStr.customerVehicleResponse.engine) + '</span>/<span class="J_customerVehicleSpan" data-key="color">' + (G.isEmpty(jsonStr.customerVehicleResponse.color) ? '--' : jsonStr.customerVehicleResponse.color) + '</span>');
          }

          //更新4条提醒服务
          $customerVehicleDiv.find(".J_customerVehicleAppointSpan").each(function (index) {
            $(this).text(G.isEmpty(G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")])) ? "--" : G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")]));
          });

          if (G.isEmpty(jsonStr.customerVehicleResponse["maintainMileage"])) {
            $customerVehicleDiv.find(".J_maintainMileageUnitSpan").hide();
          } else {
            $customerVehicleDiv.find(".J_maintainMileageUnitSpan").show();
          }


          $customerVehicleAppointForm.find(".J_formreset").each(function () {
            $(this).attr("reset-value", G.Lang.normalize($(this).val()));
          });

          $("#customerVehicleAppointInfoShow" + dataIndex).show();
          $("#customerVehicleAppointInfoEdit" + dataIndex).hide();
          showMessage.fadeMessage("45%", "24%", "slow", 3000, "更新成功！");
        } else {
          nsDialog.jAlert("更新失败！");
        }
      },
      error: function (json) {
        nsDialog.jAlert("网络异常，请联系客服");
      }
    });
  });

  $(".J_cancelCustomerVehicleBtn").live("click", function (event) {
    event.preventDefault();
    var dataIndex = $(this).attr("data-index");
    if (G.Lang.isEmpty($("#vehicleId" + dataIndex).val())) {
      $("#customerVehicleDiv" + dataIndex).remove();
      var aFirst = $("div[data-node-type=slider]").find("a:first");
      if (aFirst.attr("id") != "addCustomerVehicleBtn") {
        slider.first();
        aFirst.click();
      }
    } else {
      $("#customerVehicleAppointInfoShow" + dataIndex).show();
      $("#customerVehicleOBDInfoShow" + dataIndex).show();
      $("#customerVehicleBasicInfoShow" + dataIndex).show();
      $("#customerVehicleOBDInfoEdit" + dataIndex).hide();
      $("#customerVehicleAppointInfoEdit" + dataIndex).hide();
      $("#customerVehicleBasicInfoEdit" + dataIndex).hide();
      $("#customerVehicleForm" + dataIndex).find(".J_formreset").each(function () {
        $(this).val(G.Lang.normalize($(this).attr("reset-value")));
      });
    }
  });

  $(".J_cancelCustomerVehicleAppointBtn").live("click", function (event) {
    event.preventDefault();
    var dataIndex = $(this).attr("data-index");
    if (G.Lang.isEmpty($("#vehicleId" + dataIndex).val())) {
      $("#customerVehicleDiv" + dataIndex).remove();
    } else {
      $("#customerVehicleAppointInfoEdit" + dataIndex).hide();
      $("#customerVehicleAppointInfoShow" + dataIndex).show();
      $("#customerVehicleAppointForm" + dataIndex).find(".J_formreset").each(function () {
        $(this).val(G.Lang.normalize($(this).attr("reset-value")));
      });
    }
  });

  $(".J_editCustomerVehicle").live("click", function (event) {
    event.preventDefault();
    var dataIndex = $(this).attr("data-index");
    var vehicleId = $("#vehicleId" + dataIndex).val();
    var obdId = $("#obdId" + dataIndex).val();
    var customerId = $("#customerId").val();
    var licenceNo = $("#licenceNo" + dataIndex).val();
    var data = APP_BCGOGO.Net.syncGet({
      url: "txn.do?method=checkUndoneOrder",
      data: {
        licenceNo: licenceNo,
        vehicleId: vehicleId,
        customerId: customerId
      },
      dataType: "json"
    });
    if (data.success) {
      $("#customerVehicleBasicInfoEdit" + dataIndex).show();
      $("#customerVehicleAppointInfoEdit" + dataIndex).show();
//      if(G.isEmpty(obdId)){
//        $("#customerVehicleOBDInfoEdit" + dataIndex).show();
//        $("#customerVehicleOBDInfoShow" + dataIndex).hide();
//      }
      $("#customerVehicleBasicInfoShow" + dataIndex).hide();
      $("#customerVehicleAppointInfoShow" + dataIndex).hide();
    } else {
      nsDialog.jAlert(data.msg);
    }
  });

  $(".J_editCustomerVehicleAppoint").live("click", function (event) {
    event.preventDefault();
    var dataIndex = $(this).attr("data-index");
    var vehicleId = $("#vehicleId" + dataIndex).val();
    var customerId = $("#customerId").val();
    var licenceNo = $("#licenceNo" + dataIndex).val();
    var data = APP_BCGOGO.Net.syncGet({
      url: "txn.do?method=checkUndoneOrder",
      data: {
        licenceNo: licenceNo,
        vehicleId: vehicleId,
        customerId: customerId
      },
      dataType: "json"
    });
    if (data.success) {
      $("#customerVehicleAppointInfoEdit" + dataIndex).show();
      $("#customerVehicleAppointInfoShow" + dataIndex).hide();
    } else {
      nsDialog.jAlert(data.msg);
    }
  });

  //删除整行信息的超链
  $(".J_deleteCustomerVehicle").live("click", function (event) {
    event.preventDefault();
    var dataIndex = $(this).attr("data-index");
    var vehicleId = $("#vehicleId" + dataIndex).val();
    var customerId = $("#customerId").val();
    var licenceNo = $("#licenceNo" + dataIndex).val();
    var data = APP_BCGOGO.Net.syncGet({
      url: "txn.do?method=checkUndoneOrder",
      data: {
        licenceNo: licenceNo,
        vehicleId: vehicleId,
        customerId: customerId
      },
      dataType: "json"
    });
    if (data.success) {
      nsDialog.jConfirm("是否确认要删除当前客户车辆？", null, function (flag) {
        if (flag) {
         APP_BCGOGO.Net.syncGet({
            url: "customer.do?method=deleteCustomerVehicleById",
            data: {
              vehicleId: vehicleId,
              customerId: customerId
            },
            dataType: "json",
            success:function(result){
              if (result.success) {
                $("#customerVehicleDiv" + dataIndex).remove();
                $("#customerVehicleId" + vehicleId).remove();
                slider.first();
                var aFirst = $("div[data-node-type=slider]").find("a:first");
                aFirst.length ? aFirst.click() : $('#addCustomerVehicleBtn').click();
                showMessage.fadeMessage("45%", "24%", "slow", 3000, "删除成功！");
              } else if(result){
                nsDialog.jAlert(result.msg);
              }
            }
          });
        }
      });
    } else {
      nsDialog.jAlert(data.msg);
    }
  });

  function validateCustomerBasicInfo(){
    var customerId = $("#customerId").val();
    var isOnlineShop = $("#isOnlineShop").val() == "true";
    var name = $.trim($("#name").val());
    //在线店铺不更新客户名，简称，座机，传真，经营产品
    if (!isOnlineShop) {
      var wholesalerVersion = $("#wholesalerVersion").val() == "true";
      if (wholesalerVersion) {
        if (G.isEmpty($("#province").val())) {
          nsDialog.jAlert("请输入省份");
          return false;
        }
        if (G.Lang.isEmpty($("#city").val()) && $("#province option").length > 1) {
          nsDialog.jAlert("请输入城市");
          return false;
        }
        if (G.isEmpty(name)) {
          nsDialog.jAlert("客户名不能为空");
          return false;
        }
      }

      // 座机、传真、手机、邮箱、QQ 格式校验
      var phone = $("#landLine").val(); // 校验座机
      var phoneSecond=$("#landLineSecond").val();
      var phoneThird=$("#landLineThird").val();
      if (!G.isEmpty(G.trim(phone))) {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber(phone)) {
          nsDialog.jAlert("座机格式校验错误，请确认后重新输入！");
          return false;
        }
      }
      if (!G.isEmpty(G.trim(phoneSecond))) {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber(phoneSecond)) {
          nsDialog.jAlert("座机格式校验错误，请确认后重新输入！");
          return false;
        }
      }
      if (!G.isEmpty(G.trim(phoneThird))) {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber(phoneThird)) {
          nsDialog.jAlert("座机格式校验错误，请确认后重新输入！");
          return false;
        }
      }
      if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($("#fax").val()) && $("#fax").val() != "") {
        nsDialog.jAlert("传真号格式错误，请确认后重新输入！");
        return false;
      }
    }
    if (APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact) {
      var contactMobiles = new Array();
      $("#customerBasicInfoEdit input[name$='mobile']").each(function (index) {
        if (!G.isEmpty($(this).val())) {
          contactMobiles.push($(this).val());
        }
      });
      for (var mobileIndex in contactMobiles) {
        if (!G.isEmpty(contactMobiles[mobileIndex]) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(contactMobiles[mobileIndex])) {
          nsDialog.jAlert("手机号格式错误，请确认后重新输入！");
          return false;
        }
      }
      if (isMobileDuplicate(contactMobiles)) {
        return false;
      }
      // validate mail
      var contactMails = new Array();
      $("#customerBasicInfoEdit input[name$='mail']").each(function (index) {
        if (!G.isEmpty($(this).val())) {
          contactMails.push($(this).val());
        }
      });
      for (var emailIndex in contactMails) {
        if (!APP_BCGOGO.Validator.stringIsEmail(contactMails[emailIndex])) {
          nsDialog.jAlert("Email格式错误，请确认后重新输入！");
          return false;
        }
      }
      // validate qq
      var contactQQs = new Array();
      $("#customerBasicInfoEdit input[name$='qq']").each(function (index, qq) {
        if (!G.isEmpty($(this).val())) {
          contactQQs.push($(this).val());
        }
      });
      for (var qqIndex in contactQQs) {
        if (!APP_BCGOGO.Validator.stringIsQq(contactQQs[qqIndex])) {
          nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
          return false;
        }
      }
      //非在线店铺，座机号联系人   手机号均为空的时候
      if (!isOnlineShop && !G.isEmpty(G.trim(name)) && isMobilesEmpty(contactMobiles) && G.isEmpty(G.trim(phone))) {
        if (!checkName(G.trim(name))) {
          return false;
        }
      }
      if (!isOnlineShop && !G.isEmpty(G.trim(name)) && isMobilesEmpty(contactMobiles) && G.isEmpty(G.trim(phoneSecond))) {
        if (!checkName(G.trim(name))) {
          return false;
        }
      }
      if (!isOnlineShop && !G.isEmpty(G.trim(name)) && isMobilesEmpty(contactMobiles) && G.isEmpty(G.trim(phoneThird))) {
        if (!checkName(G.trim(name))) {
          return false;
        }
      }
      //
      if (!G.isEmpty(G.trim(name)) && !isMobilesEmpty(contactMobiles)) {
        if (!validateCustomerMobiles(contactMobiles, customerId)) {
          return false;
        }
      }
      if (!isOnlineShop && !G.isEmpty(G.trim(name)) && !G.Lang.isEmpty(phone)) {
        if (!checkSamePhone(phone)) {
          return false;
        }
      }
      if (!isOnlineShop && !G.isEmpty(G.trim(name)) && !G.Lang.isEmpty(phoneSecond)) {
        if (!checkSamePhone(phoneSecond)) {
          return false;
        }
      }
      if (!isOnlineShop && !G.isEmpty(G.trim(name)) && !G.Lang.isEmpty(phoneThird)) {
        if (!checkSamePhone(phoneThird)) {
          return false;
        }
      }
      // 校验主联系人信息
      var contacts = buildNormalKeyContacts();
      var validContactCount = countValidContact(contacts);
      if (validContactCount == 2) {
        if (!mainContactIsValid(contacts)) {
          setFirstValidToMainContact(contacts);
        }
      } else if (validContactCount == 1) {
        var index = firstValidContactFromContacts(contacts); // 只有一个联系人的时候 设为主联系人
        if (!($("#contacts\\[" + index + "\\]\\.mainContact").val() == "1")) {
//                    $("#contacts\\[" + index + "\\]\\.mainContact").val("1");
//                    var mainIndex = getMainContactFromContacts(contacts);
//                    $("#contacts\\[" + mainIndex + "\\]\\.mainContact").val("0");
          setFirstValidToMainContact(contacts);
        }
      }
    } else {
      var mobile = $("#customerBasicForm input[name$='mobile']").val();
      if (!G.isEmpty(mobile) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
        nsDialog.jAlert("手机号格式错误，请确认后重新输入！");
        return false;
      }
      var email = $("#customerBasicForm input[name$='email']").val();
      if (!G.isEmpty(email) && !APP_BCGOGO.Validator.stringIsEmail(email)) {
        nsDialog.jAlert("Email格式错误，请确认后重新输入！");
        return false;
      }
      var qq = $("#customerBasicForm input[name$='qq']").val();
      if (!G.isEmpty(qq) && !APP_BCGOGO.Validator.stringIsQq(qq)) {
        nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
        return false;
      }
      if (!validateCustomerMobiles($("#customerBasicForm input[name$='mobile']").val(), customerId)) {
        return false;
      }
    }
    return true;
  }
  $("#saveCustomerBasicBtn").bind("click", function (e) {
    e.preventDefault();
    if(validateCustomerBasicInfo() && validateCustomerBusinessInfo()){
      var result = saveCustomerDetail();
      if (result && !G.Lang.isEmpty(result) && result.success) {
        $("#customerName").val(result.data["name"]);
        //更新span
        $(".J_customerBasicSpan").each(function (index) {
          $(this).text(G.isEmpty(G.normalize(result.data[$(this).attr("data-key")])) ? "--" : G.normalize(result.data[$(this).attr("data-key")]));
        });
        if(G.isEmpty(result.data["identityStr"])){
          $(".J_customerIdentity").hide();
        }else{
          $(".J_customerIdentity").show();
        }
        var wholesalerVersion = $("#wholesalerVersion").val() == "true";
        if (wholesalerVersion) {
          var contactHtmls = "";
          var otherContactHtmls = "";
          for (var i = 0; i < 3; i++) {
            var contact = G.Lang.normalize(result.data.contacts[i]);
            if (G.Lang.normalize(contact.mainContact) == 1) {
              contactHtmls += '' +
                '<td>主联系人：' + (G.isEmpty(G.Lang.normalize(contact.name, "")) ? "--" : G.Lang.normalize(contact.name, "")) + '</td>' +
                '<td>手机号：' + (G.isEmpty(G.Lang.normalize(contact.mobile, "")) ? "--" : G.Lang.normalize(contact.mobile, ""))+ '</td>' +
                '<td>QQ：' +(G.isEmpty(G.Lang.normalize(contact.qq, "")) ? "--" : G.Lang.normalize(contact.qq, ""))+ '</td>' +
                '<td>Email:' +   (G.isEmpty(G.Lang.normalize(contact.email, "")) ? "--" : G.Lang.normalize(contact.email, "")) + '</td>';
            } else {
              otherContactHtmls += '<tr class="J_otherCustomerContactContainer">' +
                '<td>联系人：' +(G.isEmpty(G.Lang.normalize(contact.name, "")) ? "--" : G.Lang.normalize(contact.name, ""))+ '</td>' +
                '<td>手机号：' + (G.isEmpty(G.Lang.normalize(contact.mobile, "")) ? "--" : G.Lang.normalize(contact.mobile, "")) + '</td>' +
                '<td>QQ：' + (G.isEmpty(G.Lang.normalize(contact.qq, "")) ? "--" : G.Lang.normalize(contact.qq, "")) + '</td>' +
                '<td>Email:' + (G.isEmpty(G.Lang.normalize(contact.email, "")) ? "--" : G.Lang.normalize(contact.email, ""))+ '</td>' +
                '</tr>';
            }
          }
          $("#customerContactContainer").html(contactHtmls);
          $("#customerOtherInfo").find("table").find(".J_otherCustomerContactContainer").remove();
          $("#customerOtherInfo").find("table").prepend(otherContactHtmls);

          $("#select_provinceInput").attr("reset-value", $("#province").val());
          $("#select_cityInput").attr("reset-value", $("#city").val());
          $("#select_regionInput").attr("reset-value", $("#region").val());

          $("#select_provinceInput").val($("#province").val());
          $("#select_cityInput").val($("#city").val());
          $("#select_regionInput").val($("#region").val());

        }else{
          if(G.isEmpty(G.normalize(result.data['mobile']))){
            $(".customerSMS").hide();
          }else{
            $(".customerSMS").show();
          }
        }
        $("#customerBasicForm").find(".J_formreset").each(function () {
          $(this).attr("reset-value", G.Lang.normalize($(this).val()));
        });
        $("#editCustomerInfo").show();
        $("#customerBasicInfoShow").show();
        $("#customerBasicInfoEdit").hide();
      }
    }
  });
  $("#cancelCustomerBasicBtn").bind("click", function (e) {
    e.preventDefault();
    $("#editCustomerInfo").show();
    $("#customerBasicInfoShow").show();
    $("#customerBasicInfoEdit").hide();
    $("#customerBasicForm").find(".J_formreset").each(function () {
      $(this).val(G.Lang.normalize($(this).attr("reset-value")));
    });
    $(".J_editCustomerContact").each(function(){
      if($(this).find("input[name$='mainContact']").val()=="1"){
        $(this).find(".icon_grayconnacter").click();
      }
    });

    var isOnlineShop = $("#isOnlineShop").val() == "true";
    if (!isOnlineShop && $("#isOnlineShop")[0]) {
      setCustomerAreaInfo();
    }


  });
  $("#editCustomerInfo").bind("click", function (e) {
    e.preventDefault();
    $(this).hide();
    $("#customerBasicInfoShow").hide();
    $("#customerBasicInfoEdit").show();
  });


  $("#saveCustomerAccountBtn").bind("click", function (e) {
    e.preventDefault();
    //
    var result = saveCustomerDetail();
    if (result && !G.Lang.isEmpty(result) && result.success) {
      //更新span
      $(".J_customerAccountSpan").each(function (index) {
        $(this).text(G.normalize(result.data[$(this).attr("data-key")]));
      });
      $("#customerAccountForm").find(".J_formreset").each(function () {
        $(this).attr("reset-value", G.Lang.normalize($(this).val()));
      });
      $("#editCustomerAccountInfo").show();
      $("#customerAccountInfoShow").show();
      $("#customerAccountInfoEdit").hide();
    }

  });
  $("#cancelCustomerAccountBtn").bind("click", function (e) {
    e.preventDefault();
    $("#editCustomerAccountInfo").show();
    $("#customerAccountInfoShow").show();
    $("#customerAccountInfoEdit").hide();
    $("#customerAccountForm").find(".J_formreset").each(function () {
      $(this).val(G.Lang.normalize($(this).attr("reset-value")));
    });
  });
  $("#editCustomerAccountInfo").bind("click", function (e) {
    e.preventDefault();
    $(this).hide();
    $("#customerAccountInfoShow").hide();
    $("#customerAccountInfoEdit").show();
  });

  function validateCustomerBusinessInfo(){

    var wholesalerVersion = $("#wholesalerVersion").val() == "true";
    //汽修版本不校验
    if (!wholesalerVersion) {
      return true;
    }

    var isOnlineShop = $("#isOnlineShop").val() == "true";
    //在线店铺不更新经营产品、服务范围
    if (!isOnlineShop) {
      //设置服务范围的id
      var serviceCategoryRelationIdStr = "";
      $("#customerBasicForm").find(".J_serviceCategoryCheckBox").each(function () {
        if($(this).attr("checked")){
          serviceCategoryRelationIdStr +=$(this).val()+",";
        }
      });
      if(!G.Lang.isEmpty(serviceCategoryRelationIdStr)){
        serviceCategoryRelationIdStr = serviceCategoryRelationIdStr.substr(0,serviceCategoryRelationIdStr.length-1);
      }
      if (G.isEmpty(serviceCategoryRelationIdStr)) {
        nsDialog.jAlert("请选择服务范围");
        return false;
      }
      $("#serviceCategoryRelationIdStr").val(serviceCategoryRelationIdStr);


      var thirdCategoryIdStr = "";
      if($("#businessScopeTreeDiv")[0] && App.components.multiSelectTwoDialogTree){
        var addedData =  App.components.multiSelectTwoDialogTree.getAddedLeafDataList();
        if (addedData) {
          $.each(addedData,function(index,val){
            thirdCategoryIdStr+=val.idStr+",";
          });
          $("#thirdCategoryNodeListJson").val(encodeURIComponent(JSON.stringify(App.components.multiSelectTwoDialogTree.getAddedTreeNodeDataList())));
        }
        if(!G.Lang.isEmpty(thirdCategoryIdStr)){
          thirdCategoryIdStr = thirdCategoryIdStr.substr(0,thirdCategoryIdStr.length-1);
        }
      }
      $("#thirdCategoryIdStr").val(thirdCategoryIdStr);
      //主营车型
      if($("#vehicleBrandModelDiv")[0] && App.components.multiSelectTwoDialog){
        var vehicleModelIdStr = "";
        var addedData =  App.components.multiSelectTwoDialog.getAddedData();
        if (addedData) {
          $.each(addedData,function(index,val){
            vehicleModelIdStr+=val.modelId+",";
          });
          $("#shopVehicleBrandModelDTOListJson").val(encodeURIComponent(JSON.stringify(addedData)));
        }
        if(!G.Lang.isEmpty(vehicleModelIdStr)){
          vehicleModelIdStr = vehicleModelIdStr.substr(0,vehicleModelIdStr.length-1);
        }
        $("#vehicleModelIdStr").val(vehicleModelIdStr);
      }
    }
    return true;
  }
  $("#saveCustomerBusinessBtn").bind("click", function (e) {
    e.preventDefault();
    //
    if(validateCustomerBusinessInfo()){
      var result = saveCustomerDetail();
      if (result && !G.Lang.isEmpty(result)) {
        if(result.success){
          //更新span
          $(".J_customerBusinessSpan").each(function (index) {
            $(this).text(G.normalize(result.data[$(this).attr("data-key")]));
          });
          $("#editCustomerBusinessInfo").show();
          $("#customerBusinessInfoShow").show();
          $("#customerBusinessInfoEdit").hide();
        }else{
          nsDialog.jAlert("保存失败！");
        }
      }
    }
  });

  $("#cancelCustomerBusinessBtn").bind("click", function (e) {
    e.preventDefault();
    $("#editCustomerBusinessInfo").show();
    $("#customerBusinessInfoShow").show();
    $("#customerBusinessInfoEdit").hide();
    var serviceCategoryRelationIdStr = G.Lang.normalize($("#serviceCategoryRelationIdStr").val());
    $("#customerBusinessForm").find(".J_serviceCategoryCheckBox").each(function () {
      $(this).attr("checked",serviceCategoryRelationIdStr.indexOf($(this).val())>-1);
    });
    if(App.components.multiSelectTwoDialog){
      App.components.multiSelectTwoDialog.clearAllSelectedData();
      if(!G.Lang.isEmpty($("#shopVehicleBrandModelDTOListJson").val())){
        App.components.multiSelectTwoDialog.initSelectedData(JSON.parse(decodeURIComponent(G.Lang.normalize($("#shopVehicleBrandModelDTOListJson").val()))));
        $("#partBrandModel").click();
      }else{
        $("#allBrandModel").click();
      }
    }
  });
  $("#editCustomerBusinessInfo").bind("click", function (e) {
    e.preventDefault();
    $(this).hide();
    $("#customerBusinessInfoShow").hide();
    $("#customerBusinessInfoEdit").show();
  });
  $(".J_customerOpt").bind("mouseenter",function (event) {
    event.stopImmediatePropagation();
    var _currentTarget = $(event.target).parent().find(".J_customerOptDetail");
    _currentTarget.show(80);

    _currentTarget.mouseleave(function (event) {
      if (event.relatedTarget != $(event.target).parent().parent().find(".J_customerOpt")[0]) {
        _currentTarget.hide(80);
      }
    });
  }).live("mouseleave", function (event) {
      event.stopImmediatePropagation();
      var _currentTarget = $(event.target).parent().find(".J_customerOptDetail");

      if (event.relatedTarget != _currentTarget[0]) {
        _currentTarget.hide(80);
      }

    });

  function saveCustomerDetail() {
    var url = "customer.do?method=saveOrUpdateCustomer";
    var param = $("#customerBasicForm").serializeArray();
    var data = {};
    $.each(param, function (index, val) {
      if (!G.Lang.isEmpty(data[val.name])) {
        data[val.name] = data[val.name] + "," + val.value;
      } else {
        data[val.name] = val.value;
      }
    });
//        param = $("#customerAccountForm").serializeArray();
//        $.each(param, function (index, val) {
//            if (!G.Lang.isEmpty(data[val.name])) {
//                data[val.name] = data[val.name] + "," + val.value;
//            } else {
//                data[val.name] = val.value;
//            }
//        });
//        param = $("#customerBusinessForm").serializeArray();
//        $.each(param, function (index, val) {
//            if (!G.Lang.isEmpty(data[val.name])) {
//                data[val.name] = data[val.name] + "," + val.value;
//            } else {
//                data[val.name] = val.value;
//            }
//        });
    data['id'] = $("#id").val();
    data['customerId'] = $("#customerId").val();
    data['supplierId'] = $("#supplierId").val();
    return APP_BCGOGO.Net.syncPost({"url": url, "data": data});
  }

  $("#duizhan,#duizhang").bind("click", function () {
    toCreateStatementOrder($("#customerId").val(), "CUSTOMER_STATEMENT_ACCOUNT");
  });
  $(".single_contact input[id$='mobile']").live("blur", function(){
    if(this.value != '') {
      check.inputMobileBlur(this);
    }
  });
  $("#landLine").live("blur", function(){
    if(this.value != '') {
      check.inputTelephone(this);
    }
  });
  $("#landLineSecond").live("blur", function(){
    if(this.value != '') {
      check.inputTelephone(this);
    }
  });
  $("#landLineThird").live("blur", function(){
    if(this.value != '') {
      check.inputTelephone(this);
    }
  });
  /************************new  end*************************************************************************************/

  //购卡续卡  弹出页好像用到  这写法 有点......
  $("#memberCardId").click(function () {
    bcgogo.checksession({
      "parentWindow": window.parent,
      'iframe_PopupBox': $("#iframe_buyCard")[0],
      'src': 'member.do?method=buyCard&customerId=' + $("#customerId").val() + '&cardId=' + $("#memberCardId").val() + '&time=' + new Date()
    });
  });

  //判断该车牌号是否已经有归属


  //输入过滤 //有弹出框的部分（既是客户又是 供应商）
  $("#mobile2,#mobile3,#mobile,#qq,#account").keyup(function () {
    $(this).val($(this).val().replace(/[^\d]+/g, ""));
  });
  $("#landLine,#fax,#landLineSecond,#fax3,#landLineThird,#fax2").keyup(function () {
    $(this).val($(this).val().replace(/[^\d\-]+/g, "").replace(/\-+/g, "-"));
    if ($(this).val().charAt(0) == '-') {
      $(this).val("");
    }
  });
  $("#customerVehicleContainer input[name='year']").live("keyup", function () {
    $(this).val($(this).val().replace(/[^\d]+/g, ""));
  });
  $("#customerVehicleContainer input[name='obdMileage'],#customerVehicleContainer input[name='maintainMileagePeriod'],#registerMaintainMileagePeriod,#lastMaintainMileage,#registerMaintainTimePeriod").live("keyup",function () {
    if (this.value.length == 1) {
      this.value = this.value.replace(/[^1-9]/g, '')
    } else {
      this.value = this.value.replace(/\D/g, '')
    }
  }).live("blur", function () {
      $(this).click();
    });


  $("#customerVehicleContainer input[name='vin']").live("keyup", function () {
    $(this).val($(this).val().replace(/[^\da-zA-Z]+/g, ""));
  });
  $("#customerVehicleContainer input[name='engineNo']").live("keyup", function () {
    $(this).val($(this).val().replace(/[^\da-zA-Z]+/g, ""));
  });

  $("#name").bind("keyup", function () {
    $(this).val(APP_BCGOGO.StringFilter.stringSpaceFilter($(this).val()));
  });
  $("#customerVehicleContainer input[name='contact']").bind("keyup", function () {
    $(this).val(APP_BCGOGO.StringFilter.stringSpaceFilter($(this).val()));
  });
  $("#customerVehicleContainer input[name='gsmObdImei']").live("keyup", function () {
    $(this).val(APP_BCGOGO.StringFilter.stringSpaceFilter($(this).val()));
  });
  $("#customerVehicleContainer input[name='gsmObdImeiMoblie']").live("keyup", function () {
    $(this).val($(this).val().replace(/[^\d]+/g, ""));
  });

  //IE或者别的浏览器
  if (/msie/i.test(navigator.userAgent)) {
    // TODO IE下的待定
  } else {
    $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").live("change",checkNumberInput);
    if (document.getElementById("mobile") != null) {
      document.getElementById("mobile").addEventListener("input", checkNumberInput, false);
    }
    if (document.getElementById("account") != null) {
      document.getElementById("account").addEventListener("input", checkNumberInput, false);
    }
    if (document.getElementById("qq") != null) {
      document.getElementById("qq").addEventListener("input", checkNumberInput, false);
    }
    if (document.getElementById("landLine") != null) {
      document.getElementById("landLine").addEventListener("input", checkTelInput, false);
    }
    if (document.getElementById("landLineSecond") != null) {
      document.getElementById("landLineSecond").addEventListener("input", checkTelInput, false);
    }
    if (document.getElementById("landLineThird") != null) {
      document.getElementById("landLineThird").addEventListener("input", checkTelInput, false);
    }
    if (document.getElementById("fax") != null) {
      document.getElementById("fax").addEventListener("input", checkTelInput, false);
    }

  }
  $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").keyup(function() {
    $(this).val($(this).val().replace(/[^\d]+/g, ""));
  });

  $(".single_contact input[name$='email']").blur(function() {
    if (!APP_BCGOGO.Validator.stringIsEmail($(this).val()) && $(this).val() != "") {
      nsDialog.jAlert("邮箱格式错误，请确认后重新输入！");
    }
  });
  $(".single_contact input[name$='qq']").blur(function() {
    var qq = $(this).val();
    if (!G.isEmpty(qq) && qq.length < 5) {
      nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
    }
  });

  function checkNumberInput() {
    $(this).val($(this).val().replace(/[^\d]+/g, ""));
  }

  function checkTelInput() {
    $(this).val($(this).val().replace(/[^\d\-]+/g, "").replace(/\-+/g, "-"));
    if ($(this).val().charAt(0) == '-') {
      $(this).val("");
    }
  }

  //输入提示
  $("#mobile,input[name$='mobile']").blur(function () {
    var mobile = $(this).val();
    if (mobile != "") {
      if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($(this).val())) {
        nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
      } else {
        //要判断是否存在同名的手机号
        var r = APP_BCGOGO.Net.syncGet({
          url: "customer.do?method=getCustomerByMobile",
          data: {
            mobile: mobile
          },
          dataType: "json"
        });
        if (r != null && r.customerIdStr != undefined) {
          if ($("#customerId").val() != r.customerIdStr) {
            nsDialog.jAlert('与客户【' + r.customer + '】的手机号相同，请重新输入！');
            this.val("");
          }
        }
      }
    }
  });

  $("#fax").blur(function () {
    if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($(this).val()) && $(this).val() != "") {
      alert("传真号格式错误，请确认后重新输入！");
    }
  });
  $("#email,input[name$='email']").blur(function () {
    if (!APP_BCGOGO.Validator.stringIsEmail($(this).val()) && $(this).val() != "") {
      nsDialog.jAlert("邮箱格式错误，请确认后重新输入！");
    }
  });
  $("#qq,input[name$='qq']").blur(function () {
    var qq = $(this).val();
    if (qq != "" && qq.length < 5) {
      nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
    }
  });

  $(".J_customerVehicleMobile").live("keyup", function () {
    $(this).val($(this).val().replace(/[^\d]+/g, ""));
  });
  $(".J_customerVehicleMobile").live("blur", function () {
    var $vehicleMobile = $(this);
    $vehicleMobile.val($vehicleMobile.val().replace(/[^\d]+/g, ""));
    if (!G.Lang.isEmpty($(this).val())) {
      if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($vehicleMobile.val())) {
        nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！",null,function(){
          $vehicleMobile.focus();
        });
        return;

      }
//            check.inputVehicleMobileBlur(this);
    }
  });

  $("#changePassword").bind("click", function (e) {
    e.preventDefault();
    var mobile = $("input[name$='mobile']").val();
    if (mobile) {
      $("#sendSms").attr("checked", true);
    } else {
      $("#sendSms").attr("checked", false);
    }
    if ($("#memberId").val() == "") {
      return false;
    }
    //$("#chPasswordShow").show();
    $("#chPasswordShow").dialog({
      modal: true,
      resizable: false,
      draggable: false,
      buttons: {
        "确定": function () {
          var obj = this;
          if ($("#newPw").val() != $("#cfNewPw").val()) {
            nsDialog.jAlert("两次输入的密码不一致");
          } else {
            if ($("#sendSms").attr("checked") == true) {
              if (mobile == null || $.trim(mobile) == "") {
                $("#enterPhoneCustomerId").val($("#customerId").val());
                $("#enterPhoneScene").val("uncleUser_member_change_password");
                Mask.Login();
//                                $("#chPasswordShow").dialog("close");
                $("#enterPhoneSetLocation").fadeIn("slow");
              } else {
                changePassword();
              }
            } else {
              changePassword();
            }
          }
        },
        "取消": function () {
          $(this).dialog("close");
          $("#oldPw").val("");
          $("#newPw").val("");
          $("#cfNewPw").val("");
        }
      },
      open: function (event, ui) {

      },
      close: function () {
        $("#oldPw").val("");
        $("#newPw").val("");
        $("#cfNewPw").val("");
      }
    });
  });

  $(".btn_div_Img").click(function () {
    window.location.href = 'customer.do?method=customerdata&resetSearchCondition=true';
  });

  $("[name='maintainMileage']").live("keyup",function () {
    $(this).val(App.StringFilter.inputtingIntFilter($(this).val()));
  }).live("blur",function () {
      $(this).val(App.StringFilter.inputtingIntFilter($(this).val()));
    });

});


function changePassword() {
  var mobile = $("#customerMobileSpan").text();
  jQuery.ajax({
    type: "POST",
    url: "user.do?method=changeMemberPassword",
    data: {
      memberId: $("#memberId").val(),
      oldPw: $("#oldPw").val(),
      sendSms: $("#sendSms").attr("checked"),
      userName: $("#unit").val(),
      //用户名
      memberNo: $("#memberNo").html(),
      //会员号
      mobile: mobile,
      //手机
      newPw: $("#newPw").val()
    },
    cache: false,
    success: function (data) {
      if (data == "CONFIRM_PASSWORD_FAIL") {
        nsDialog.jAlert("输入的旧密码不正确");
      } else if (data == "CHANGE_PASSWORD_SUCCESS") {
        nsDialog.jAlert("密码修改成功", null, function () {
          $("#chPasswordShow").dialog("close");
//                    location.href = "unitlink.do?method=customer&customerId=" + $("#customerId").val();
        });
      } else {
        nsDialog.jAlert("密码修改失败");
      }
      $("#oldPw").val("");
      $("#newPw").val("");
      $("#cfNewPw").val("");
    }
  });
}

function deleteCustomer() {
  if (!validateDeleteCustomer($("#customerId").val())) {
    return;
  }
  if (!validateMember()) {
    $("<div id='delConfirmDiv' >该客户是会员且有储值金额，是否办理退卡服务？点击确认前往退卡业务</div>").dialog({
      resizable: true,
      title: "删除确认",
      height: 150,
      width: 300,
      modal: true,
      closeOnEscape: false,
      buttons: {
        "确定": function () {
          $(this).dialog("close");
          returnCard("deleteCustomer");
        },
        "取消": function () {
          $(this).dialog("close");
        }
      }
    });
    return;
  }
  var isDualRole = false;
  var isPermanentDualRole = false;  //有关联对账单标志
  var isHaveCustomerShop = false;  //在线关联店铺的标志
  var relatedSupplierIdStr = "";
  var customerId = $("#customerId").val();
  APP_BCGOGO.Net.syncAjax({
    url: "customer.do?method=getCustomerById",
    dataType: "json",
    data: {customerId: customerId},
    success: function (data) {
      if (data == null) {
        return;
      }
      if (!G.isEmpty(data.supplierId)) {
        isDualRole = true;
        relatedSupplierIdStr = data.supplierIdStr;
        if (data.permanentDualRole) {
          isPermanentDualRole = true;
        }

      }
      if (!G.isEmpty(data.customerShopIdStr)) {
        isHaveCustomerShop = true;
      }

    }
  });

  var msg = "";
  var url = "customer.do?method=deleteCustomer&customerId=" + customerId;
  if (isHaveCustomerShop) {
    msg = "友情提示：该客户是在线商铺，并且已经与本店建立关联关系，删除后将接受不到该店铺发送的站内消息！<br>";
  }
  if (isDualRole) {
    msg += "他既是客户又是供应商，"
    if (isPermanentDualRole) {
      msg += "若删除客户，则将同时删除绑定的供应商！<br>"
      url += "&alsoDeleteSupplier=true";
    } else {
      msg += "若删除客户，则取消绑定并保留供应商资料！<br>"
    }
  }
  msg += "您确定要删除该客户吗？"

  nsDialog.jConfirm(msg, null, function (returnVal) {
    if (returnVal) {
      if (isPermanentDualRole) {
        APP_BCGOGO.Net.syncAjax({
          url: "txn.do?method=validateDeleteSupplier",
          data: {idStr: relatedSupplierIdStr},
          dataType: "json",
          success: function (json) {
            if (json && json.success) {
              window.location = url;
            } else {
              nsDialog.jAlert(json.msg);
            }
          },
          error: function () {
            nsDialog.jAlert("验证删除供应商时失败！");
          }
        });
      } else {
        if (!stringUtil.isEmpty(customerId)) {
          window.location = url;
        }
      }
    }
  });
}

function validateMember() {
  var flag = true;

  $.ajax({
    type: "POST",
    url: "member.do?method=checkMemberBalanceExist",
    async: false,
    data: {
      customerId: $("#customerId").val(),
      tsLog: 10000000000 * (1 + Math.random())
    },
    cache: false,
    dataType: "json",
    success: function (jsonObject) {
      var resu = jsonObject.resu;
      if (resu == "error") {
        flag = false;
      }
    }
  });

  return flag;
}


/**
 * 在uncleSupplier.js 有一份拷贝，修改的时候请注意同步
 * @param customerId
 * @returns {boolean}
 */
function validateDeleteCustomer(customerId) {
  var flag = true;
  $.ajax({
    type: "POST",
    url: "customer.do?method=validateDeleteCustomer",
    async: false,
    data: {
      customerId: customerId,
      tsLog: 10000000000 * (1 + Math.random())
    },
    cache: false,
    dataType: "json",
    success: function (result) {
      if (result && !result.success) {
        flag = false;
        var orders = result.data;
        var html = "<div>";
        var isHaveUnsettledOrders = false;
        if (orders && orders["repair"] && orders["repair"].length > 0) {
          var repairOrders = orders["repair"];
          isHaveUnsettledOrders = true;
          for (var i = 0, len = repairOrders.length; i < len; i++) {
            var url = "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + repairOrders[i].idStr;
            html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + url + "'>"
              + repairOrders[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
            if (i % 2 == 1) {
              html += "<br/>";
            }
          }
        }
        if (orders && orders["sale"] && orders["sale"].length > 0) {
          isHaveUnsettledOrders = true;
          var salesOrders = orders["sale"];
          for (var i = 0, len = salesOrders.length; i < len; i++) {
            var url = "sale.do?method=getSalesOrder&salesOrderId=" + salesOrders[i].idStr;
            html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + url + "'>"
              + salesOrders[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
            if (i % 2 == 1) {
              html += "<br/>";
            }
          }
        }

        if (orders && orders["saleReturn"] && orders["saleReturn"].length > 0) {
          isHaveUnsettledOrders = true;
          var salesReturnOrders = orders["saleReturn"];
          for (var i = 0, len = salesReturnOrders.length; i < len; i++) {
            var url = "salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=" + salesReturnOrders[i].idStr;
            html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + url + "'>"
              + salesReturnOrders[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
            if (i % 2 == 1) {
              html += "<br/>";
            }
          }
        }
        html += "</div>";
        if (isHaveUnsettledOrders) {
//                    $("#deleteReceiptNo").html(html);
          $(html).dialog({
            resizable: false,
            title: "该客户还有未结算的单据，请结算完再删除!",
            height: 150,
            width: 330,
            modal: true,
            closeOnEscape: false,
            buttons: {
              "确定": function () {
                $(this).dialog("close");
              }
            }
          });
        } else {
          nsDialog.jAlert(result.msg);
        }
      }
    }
  });

  return flag;
}


//客户升级为店铺
function updateToShop() {
  var customerId = $("#customerId").val();
  if (!customerId)return;
  if ($(this).attr("lock")) {
    return;
  }
  $(this).attr("lock", true);
  var $thisDom = $(this);
  bcgogoAjaxQuery.setUrlData("shop.do?method=validateShopRegBasicInfo", {'customerId': customerId});
  bcgogoAjaxQuery.ajaxQuery(function (result) {
    if (result.success) {
      window.location.href = "shopRegister.do?method=registerMain&registerType=SUPPLIER_REGISTER&customerId=" + customerId;
    } else {
      nsDialog.jAlert(result.msg);
    }
  }, function (result) {
    nsDialog.jAlert("网络异常！");
  });
  $thisDom.removeAttr("lock");
}

//第一级菜单 select_province
function provinceBind() {
  APP_BCGOGO.Net.syncGet({
    url: "shop.do?method=selectarea",
    dataType: "json",
    data: {parentNo: 1},
    success: function (data) {
      if(!G.Lang.isEmpty(data)){
        for (var i = 0, l = data.length; i < l; i++) {
          var option = $("<option>")[0];
          option.value = data[i].no;
          option.innerHTML = data[i].name;
          $("#province")[0].appendChild(option);
        }
      }
    }
  });
}

//第二级菜单 select_city
function cityBind(select) {
  while ($("#city")[0].options.length > 1) {
    $("#city")[0].remove(1);
  }
  while ($("#region")[0].options.length > 1) {
    $("#region")[0].remove(1);
  }
  if (select.selectedIndex == 0) {

  } else {
    APP_BCGOGO.Net.syncGet({
      url: "shop.do?method=selectarea",
      dataType: "json",
      data: {parentNo: select.value},
      success: function (data) {
        if(!G.Lang.isEmpty(data)){
          for (var i = 0, l = data.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = data[i].no;
            option.innerHTML = data[i].name;
            $("#city")[0].appendChild(option);
          }
        }
      }
    });
  }
}

//第三级菜单 select_township
function townshipBind(select) {
  if (select.selectedIndex == 0) {
    return;
  }
  APP_BCGOGO.Net.syncGet({
    url: "shop.do?method=selectarea",
    dataType: "json",
    data: {parentNo: select.value},
    success: function (data) {
      if(!G.Lang.isEmpty(data)){
        while ($("#region")[0].options.length > 1) {
          $("#region")[0].remove(1);
        }
        for (var i = 0, l = data.length; i < l; i++) {
          var option = $("<option>")[0];
          option.value = data[i].no;
          option.innerHTML = data[i].name;
          $("#region")[0].appendChild(option);
        }
      }
    }
  });
}
/***
 * 这种命名 太坑
 * @param data
 */
function initTr(data) {
  $("#supplierDatas tr").not(":first").remove();
  if (data && data[0]) {
    $("#totalRows").val(data[0].numFound);
    if (data[0].supplierDTOs != undefined) {
      for (var i = 0; i < data[0].supplierDTOs.length; i++) {
        var supplier = data[0].supplierDTOs[i];
        var contact = supplier.contact == null ? '暂无' : supplier.contact;
        var mobile = supplier.mobile == null ? '暂无' : supplier.mobile;
        var customerOrSupplierShopId = G.Lang.normalize(supplier.supplierShopIdString, "");
        var tr = '<tr><td><input type="radio" value="' + data[0].supplierDTOs[i].idStr + '" name="supplier" customerOrSupplierShopId="' + customerOrSupplierShopId + '"/></td><td>';
        tr += data[0].supplierDTOs[i].name;
        tr += '<a class="connecter J_connector" supplierId="' + supplier.idStr + '"></a>' +
          '<div class="prompt J_prompt" supplierId="' + supplier.idStr + '" style="margin:0 0 0 30px; display:none;">' +
          '<div class="promptTop"></div>' +
          '<div class="promptBody">' +
          '<div class="lineList">联系人&nbsp;' + contact + '&nbsp;' + mobile + '</div>' +
          '</div>' +
          '<div class="promptBottom"></div>' +
          '</div>';
        if (!G.Lang.isEmpty(customerOrSupplierShopId)) {
          tr += '<a style="cursor:pointer;margin:2px 0 0 5px;display: inline-block;vertical-align: top"> ' +
            '<img src="images/icon_online_shop.png"> ' +
            '</a>';
        }
        tr += '</td>';
        tr += '<td>' + data[0].supplierDTOs[i]['areaInfo'] + '</td><td>';
        if (data[0].supplierDTOs[i].address != null) {
          tr += data[0].supplierDTOs[i].address;
        }
        tr += '</td>';
        $("#supplierDatas").append($(tr));
      }

    }
  }
}

function enterPhoneSendSms(objEnterPhoneMobile) {
  smsHistory(objEnterPhoneMobile,$("#customerId").val());
}
function smsHistory(mobile, customerId) {
  if (G.Lang.isEmpty(mobile)) {
    $("#enterPhoneCustomerId").val(customerId);
    Mask.Login();
    $("#enterPhoneSetLocation").fadeIn("slow");
    return;
  }
  window.location = encodeURI("sms.do?method=smswrite&customerId="+customerId+"&mobile=" + mobile);
}

function detailsArrears(customerId) {
  toReceivableSettle(customerId);
}

function setCustomerAreaInfo(){
  provinceBind();
  $("#province").bind("change", function () {
    cityBind(this);
  });
  $("#city").bind("change", function () {
    townshipBind(this);
  });
  $("#province").val($("#select_provinceInput").val());
  $("#province").change();
  $("#city").val($("#select_cityInput").val());
  $("#city").change();
  $("#region").val($("#select_regionInput").val());
}

// 判断当前的联系人手机号信息是否为空
function isMobilesEmpty(contactMobiles){
  if(G.isEmpty(contactMobiles)){
    return true;
  }
  if(contactMobiles.constructor == Array){
    return contactMobiles.every(function(item,index,obj){
      return G.isEmpty(item);
    });
  }
  return true;
}

$(function () {
  var prevNode = $('div[data-node-type=prev]');
  var nextNode = $('div[data-node-type=next]');
  var sliderNode = $('div[data-node-type=slider]');
  var containerNode = sliderNode.parent();
  var i = 1, limit = 10;
  prevNode.click(function () {
    if(prevNode.attr('data-readonly') != 'true'){
      nextNode.attr('data-readonly', 'false');
      --i == 1 && prevNode.attr('data-readonly', 'true');
      sliderNode.animate({
        left: -containerNode.width() * (i - 1)
      }, 1000);
    }
  });
  nextNode.click(function () {
    if(nextNode.attr('data-readonly') != 'true'){
      prevNode.attr('data-readonly', 'false');
      ++i == slider.getPage() && nextNode.attr('data-readonly', 'true');
      sliderNode.animate({
        left: i == slider.getPage() ? containerNode.width() - sliderNode.width() : -containerNode.width() * (i - 1)
      }, 1000);
    }
  });
  window.slider = {
    first: function () {
      $('a', sliderNode).length > limit ? containerNode.width(80 * limit) : containerNode.width(80 * $('a', sliderNode).length);
      sliderNode.width(80 * $('a', sliderNode).length);
      if($('a', sliderNode).length <= limit){
        prevNode.hide();
        nextNode.hide();
      }else{
        prevNode.show();
        nextNode.show();
      }
      i = 1;
      prevNode.attr('data-readonly', 'true');
      slider.getPage() > 1 && nextNode.attr('data-readonly', 'false');
      sliderNode.animate({
        left: 0
      }, 1000);
    },
    end: function () {
      $('a', sliderNode).length > limit ? containerNode.width(80 * limit) : containerNode.width(80 * $('a', sliderNode).length);
      sliderNode.width(80 * $('a', sliderNode).length);
      if($('a', sliderNode).length > limit){
        prevNode.show();
        nextNode.show();
        i = slider.getPage();
        prevNode.attr('data-readonly', 'false');
        nextNode.attr('data-readonly', 'true');
        sliderNode.animate({
          left: i == slider.getPage() ? containerNode.width() - sliderNode.width() : -containerNode.width() * (i - 1)
        }, 1000);
      }else{
        prevNode.hide();
        nextNode.hide();
      }
    },
    getPage: function () {
      return Math.ceil($('a', sliderNode).length / limit);
    }
  };
});