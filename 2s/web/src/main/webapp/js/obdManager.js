
$(function(){
  $(".J_vehicleBrandSuggestion")
      .bind('click', function () {
        getVehicleBrandSuggestion($(this));
      })
      .bind('keyup', function (event) {
        var eventKeyCode = event.which || event.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
          getVehicleBrandSuggestion($(this),eventKeyCode);
        }
      });


  function getVehicleBrandSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var currentSearchField =  $domObject.attr("searchField");
    var dropList = App.Module.droplist;
    dropList.setUUID(G.generateUUID());
    var ajaxData = {
      searchWord: searchWord,
      searchField:currentSearchField,
      uuid: dropList.getUUID()
    };
    var ajaxUrl = "product.do?method=searchVehicleSuggestionForGoodsBuy";
    App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
      dropList.show({
        "selector": $domObject,
        "data": result,
        "onSelect": function (event, index, data) {
          $domObject.val(data.label);
          $domObject.css({"color": "#000000"});
          dropList.hide();
          searchVehicleList();
        }
      });
    });
  }


  $("#licenceNo")
      .bind('click', function () {
        getVehicleLicenceNoSuggestion($(this));
      })
      .bind('keyup', function (event) {
        var eventKeyCode = event.which || event.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
          getVehicleLicenceNoSuggestion($(this));
        }
      });

  $("#engineNo,#chassisNumber")
      .bind('click', function () {
        getVehicleEngineNoClassNoSuggestion($(this));
      })
      .bind('keyup', function (event) {
        var eventKeyCode = event.which || event.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
          getVehicleEngineNoClassNoSuggestion($(this));
        }
      });

  function getVehicleEngineNoClassNoSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var currentSearchField = $domObject.attr("searchField");

    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxUrl = "searchInventoryIndex.do?method=getVehicleEngineNoClassNoSuggestion";
    var ajaxData = {
      searchWord: searchWord,
      searchField: currentSearchField,
      uuid: droplist.getUUID()
    };
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
      droplist.show({
        "selector": $domObject,
        "data": result,
        "onSelect": function (event, index, data) {
          $domObject.val(data.label);
          $domObject.css({"color": "#000000"});
          droplist.hide();
          searchVehicleList();
        }
      });
    });
  }



  function getVehicleLicenceNoSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
//        if (GLOBAL.Lang.isEmpty(searchWord)) return;
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxUrl = "searchInventoryIndex.do?method=getVehicleLicenceNoSuggestion";
    var ajaxData = {
      searchWord: searchWord,
      uuid: droplist.getUUID()
    };
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
      droplist.show({
        "selector": $domObject,
        "data": result,
        "onSelect": function (event, index, data) {
          $domObject.val(data.label);
          $domObject.css({"color": "#000000"});
          droplist.hide();
          searchVehicleList();
        }
      });
    });
  }

  function searchVehicleList(){
    $("#searchOBDBtn").click();
  }

  function tooltip(id, customer){
    if(G.isNotEmpty($('#'+id))){
      var memberInfo = customer.memberDTO;
      var node = setTooltipHtml(id, customer.idStr);
      setTooltipContent(node,memberInfo);
    }
  }

  function setTooltipHtml(id , customerId){
    var root = $('#'+id);
    var body = $('<div class="tooltipBody"></div>').append('<a class="icon_close"></a>') .append('<div class="title"><div style="float: left;width: 60px;"><strong>会员信息</strong></div><div style="float: right;width: 72px;"><a href="javascript:void(0)">查看更多资料</a></div></div>').append('<div class="prompt-left"></div>').append('<div class="prompt-right"></div>')
    var node = $('<div class="tooltip" style="margin:0px 0px 0px -12px;display: none;"></div>').append('<div class="tooltipTop"></div>').append(body).append('<div class="tooltipBottom"></div>')
    root.append(node).bind('mouseover',function(e){
      node.show();
    }).bind('mouseout',function(){
          node.hide();
        });
    $('.icon_close',node).click(function(){
      node.hide();
    });
    $('a', node).last().click(function(){
      window.open('unitlink.do?method=customer&customerId=' + customerId);
    });
    return node;
  }

  function setTooltipContent(node,memberInfo){
    var leftList = [{name:'卡号：',val:'memberNo'}, {name:'卡类型：',val:'type'}, {name:'入会日期：',val:'joinDateStr'}, {name:'过期日期：',val:'serviceDeadLineStr'}, {name:'会员储值：',val:'balanceStr'}];
    var left = $('.prompt-left',node);
    var right = $('.prompt-right',node);
    $.each(leftList,function(i,n){
      var val = memberInfo[n.val];
      if(val && val.length>11){
        val = '<span title="' + val + '">' + val.substr(0,8) + '...</span>';
      } else {
        val = G.normalize(val);
      }
      left.append('<div class="clear"><div class="left">' + n.name + '</div><div class="tipRight">' + val + '</div></div>');
    });
    var memberServices = memberInfo.memberServiceDTOs;
    if (memberServices) {
      right.append('<div>服务项目(共' + memberServices.length + '项)</div>');
      $.each(memberServices, function (i, memberService) {
        var name = memberService.serviceName && memberService.serviceName.length > 11 ? '<span title="' + memberService.serviceName + '">' + memberService.serviceName.substr(0, 8) + '...</span>' : memberService.serviceName;
        right.append('<div style="overflow: hidden;"><div class="div left">'+ name +'</div><div class="div tipRight">' + memberService.timesStr + '</div></div>');
      });
    }else{
      right.append('<div class="gray_color">暂无服务项目）</div>');
    }
  }

});




$(function(){

  $("#cancelBtn").click(function(){
    $("#obd_storage_div").dialog("close");
  });

  $("#obd_storage_btn").click(function(){
    $("#obd_storage_div").dialog({
      width: 650,
      height:250,
      modal: true,
      resizable: true,
      title: "入库OBD/后视镜列表",
      open:function(){
        $(".item").remove();
        obdBindAdd();
      }
    });
  });

  $(".btnPlus").live("click",function(){
    obdBindAdd();
  });

  $(".btnMinus").live("click",function(){
    $(this).closest("tr").remove();
    isShowAddButton();
  });

  $('#obdStorageTable input[name$="mobile"],#obdStorageTable input[name$="imei"]').live("blur",function(){
    var _$tr=$(this).closest("tr");
    var mobile=_$tr.find('input[name$="mobile"]').val();
    var imei=_$tr.find('input[name$="imei"]').val();
    if(!G.isEmpty(mobile)&&!G.isEmpty(imei)){
      APP_BCGOGO.Net.asyncGet({
        url:"OBDManager.do?method=getOBDByImeiAndSimNo",
        data:{
          imei:imei,
          mobile:mobile,
          now:new Date()
        },
        dataType:"json",
        success:function(obdSimBindDTO) {
          var useDateStr="";
          var obdId="";
          var useEndDateStr="";
          var usePeriod="";
          if(obdSimBindDTO){
            useDateStr=obdSimBindDTO.useDateStr;
            obdId=obdSimBindDTO.obdId;
            useEndDateStr=obdSimBindDTO.useEndDateStr;
            usePeriod=obdSimBindDTO.usePeriod+"年";
          }
          _$tr.find(".use_date").text(useDateStr);
          _$tr.find("[name$='obdId']").val(obdId);
          _$tr.find(".use_end_date").text(useEndDateStr);
          _$tr.find(".use_period").text(usePeriod);
        }
      });
    }
  });

  $(".storage_operate #okBtn").click(function(){
    var index=0;
    var msg="";
    $(".item").each(function(){
      index++;
      var imei=$(this).find('[name$="imei"]').val();
      if(G.isEmpty(imei)){
        msg="第"+index+"行，imei号不应为空!";
        return true;
      }
      var mobile=$(this).find('[name$="mobile"]').val();
      if(G.isEmpty(mobile)){
        msg="第"+index+"行，sim卡号不应为空!";
        return true;
      }

    });
    if(!G.isEmpty(msg)){
      nsDialog.jAlert(msg);
      return;
    }

    $("#obdStorageForm").ajaxSubmit({
      url:"OBDManager.do?method=OBDStorage",
      dataType: "json",
      type: "POST",
      success: function(result){
        if(!result){
          nsDialog.jAlert("保存异常！");
          return;
        }
        if(!result.success){
          nsDialog.jAlert(result.msg);
          return;
        }
        $("#searchOBDBtn").click();
        $("#obd_storage_div").dialog("close");
      }
    });

  });

  $('.timeInput').bind("click", function(){
    $(this).blur();
  }).datepicker({
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-5:c+5",
        "yearSuffix": "",
        "showButtonPanel": true,
        "onClose": function(dateText, inst) {
//                    if($(this).hasClass("startTimeStr")){
//                        $(".date_select:checked").click();
//                    }
          var $form=$(this).closest("form");
          var startTimeStr=$form.find("[name='startTimeStr']").val();
          var endTimeStr=$form.find("[name='endTimeStr']").val();
          if(!G.isEmpty(startTimeStr)&&!G.isEmpty(endTimeStr)&& GLOBAL.Util.getExactDate(startTimeStr) >= GLOBAL.Util.getExactDate(endTimeStr)) {
            nsDialog.jAlert("开始时间应小于结束时间，请重新选择！");
            $(this).val(inst.lastVal);
          }
        },
        "onSelect": function(dateText, inst) {
          if(inst.lastVal == dateText) {
            return;
          }
          $(this).val(dateText);
        }
      });

  $(".date_select").click(function(){
    $(".date_select").removeClass("clicked");
    $(this).addClass("clicked");
    var idStr=$(this).attr("id");
    if(idStr=="date_yesterday"){
      $("#startTimeStr").val(dateUtil.getYesterday());
      $("#endTimeStr").val(dateUtil.getYesterday());
    }else if(idStr=="date_today"){
      $("#startTimeStr").val(dateUtil.getToday());
      $("#endTimeStr").val(dateUtil.getToday());
    }else if(idStr=="date_last_week"){
      $("#startTimeStr").val(dateUtil.getOneWeekBefore());
      $("#endTimeStr").val(dateUtil.getToday());
    }else if(idStr=="date_last_month"){
      $("#startTimeStr").val(dateUtil.getOneMonthBefore());
      $("#endTimeStr").val(dateUtil.getToday());
    }else if(idStr=="date_last_year"){
      $("#startTimeStr").val(dateUtil.getOneYearBefore());
      $("#endTimeStr").val(dateUtil.getToday());
    }
    $("#searchOBDBtn").click();
  });

  $('[name="userTypes"],[name="obdStatusList"]').change(function(){
    $("#searchOBDBtn").click();
  });


  $("#empty_opr_btn").click(function(){
    $("#licenceNo").val("");
    $("#vehicleBrand").val("");
    $("#vehicleModel").val("");
    $("#chassisNumber").val("");
    $("#engineNo").val("");
    $("#imei").val("");
    $("#mobile").val("");

    $("#obdStatusList1,#obdStatusList2,#userTypes1,#userTypes2").attr("checked","");
    $(".timeInput").val("");
    $(".date_select").removeClass("clicked");
    $("#searchOBDBtn").click();
  });

  $("#searchOBDBtn").click(function(){
    var paramForm = $("#searchOBDListForm").serializeArray();
    var param = {};
    $.each(paramForm, function (index, val) {
      var _value=$.trim(param[val.name]);
      _value=G.isEmpty(_value)?_value:(_value+",")
      param[val.name] =_value+$.trim(val.value);
    });
    var url="OBDManager.do?method=getOBDList";
    APP_BCGOGO.Net.syncPost({
      url:url,
      dataType: "json",
      data:param,
      type: "POST",
      success: function(json){
        if(!json){
          return;
        }
        initOBDList(json);
        initPage(json, "_initOBDList",url, null, "initOBDList", '', '',param,null);
      }
    });

  });

  $(".stat_btn").click(function(){
    var paramForm = $("#searchOBDListForm").serializeArray();
    var param = {};
    $.each(paramForm, function (index, val) {
      var _value=$.trim(param[val.name]);
      _value=G.isEmpty(_value)?_value:(_value+",")
      param[val.name] =_value+$.trim(val.value);    });

    if($(this).hasClass("shop_obd_on_sell")){
      param['obdStatusList']="ON_SELL";
    }else  if($(this).hasClass("shop_obd_sold")){
      param['obdStatusList']="SOLD";
    }else{
      param['obdStatusList']="";
    }
    var url="OBDManager.do?method=getOBDList";
    $('[name="obdStatusList"]').attr("checked",false);
    APP_BCGOGO.Net.syncPost({
      url:url,
      dataType: "json",
      data:param,
      type: "POST",
      success: function(json){
        if(!json){
          return;
        }
        initOBDList(json,"stat");
        initPage(json, "_initOBDList",url, null, "initOBDList", '', '',param,null);
      }
    });
  });

  $(".vehicle_bind_btn").live("click",function(){
    var obd_id= $(this).closest("tr").find(".obd_id").val();
    var obd_imei= $(this).closest("tr").find(".obd_imei").val();
    var sim_no= $(this).closest("tr").find(".sim_no").val();
    $("#obd_bind_id").val(obd_id);
    $("#obd_bind_imei").text(obd_imei);
    $("#obd_bind_confirm_cancelBtn").attr("obd_imei",obd_imei);
    $("#obd_bind_confirm_cancelBtn").attr("sim_no",sim_no);
    $("#obd_bind_confirm").dialog({
      width: 400,
      height:200,
      modal: true,
      resizable: true,
      title: "车辆安装提示"

    });
  });

  $("#obd_bind_confirm_okBtn").click(function(){
    $("#obd_bind_confirm").dialog("close");
    $("#obd_bind_div").dialog({
      width: 400,
      height:200,
      modal: true,
      resizable: true,
      title: "选择车辆安装"

    });
  });

  $("#obd_bind_confirm_cancelBtn").click(function(){
    $("#obd_bind_confirm").dialog("close");
    var obd_imei=$(this).attr("obd_imei");
    var sim_no=$(this).attr("sim_no");
    bcgogo.checksession({
      "parentWindow": window.parent,
      'iframe_PopupBox': $("#iframe_moreUserInfo")[0],
      'src': "txn.do?method=clientInfo&obd_imei="+obd_imei+"&sim_no="+sim_no
    });
  });

  $("#vehicle_bind_cancelBtn").click(function(){
    $("#obd_bind_div").dialog("close");
  });

  $("#vehicle_bind_okBtn").click(function(){
    var licenceNo=$('#licenceNo_input').val().replace(/-/g, '');
    if(G.isEmpty(licenceNo)){
      nsDialog.jAlert("请输入车辆安装的车牌号！");
      return;
    }
    if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber(licenceNo)) {
      nsDialog.jAlert("输入的车牌号码不符合规范，请检查！", null, function () {
        $("#licenceNo_input").focus();
      });
      return;
    }
    var obdId=$("#obd_bind_id").val();
    APP_BCGOGO.Net.asyncGet({
      url:"OBDManager.do?method=OBDInstall",
      data:{
        obdId:obdId,
        licenceNo:licenceNo
      },
      dataType:"json",
      success:function(result) {
        if(!result) return;
        if(!result.success){
          nsDialog.jAlert(result.msg);
          return;
        }
        $("#obd_bind_id").val("");
        $("#licenceNo_input").val("");
        $("#obd_bind_imei").text("");
        $("#obd_bind_div").dialog("close");
        $("#searchOBDBtn").click();
//        nsDialog.jAlert("安装成功！");
      }
    });

  });


  $('#licenceNo_input').bind('focus',function () {
    searchLicenceNo(this, $(this).val());
  }).bind('keyup',function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingLicenseNoFilter($(this).val()));
        $(this).val() != '' && searchLicenceNo(this, $(this).val());
      }).bind('blur', function () {
        if ($(this).val() == '') {
          $('#customerId').val("");
        } else {
          setTimeout(function () {
            if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber($('#licenceNo_input').val().replace(/-/g, ''))) {
              nsDialog.jAlert("输入的车牌号码不符合规范，请检查！", null, function () {
                $("#licenceNo_input").focus();
              });
            }
          }, 500);
        }
      });
});

var searchLicenceNo = function (dom, searchWord) {
  var droplist = APP_BCGOGO.Module.droplist;
  searchWord = searchWord.replace(/\s/g, '');
  var uuid = GLOBAL.Util.generateUUID();
  droplist.setUUID(uuid);
  searchWord != '' && $.post('product.do?method=searchlicenseplate', {
    now: new Date().getTime(),
    plateValue: searchWord
  }, function (list) {
    droplist.show({
      "selector": $(dom),
      "data": {
        uuid: uuid,
        data: $.map(list, function (n) {
          n.label = n.licenceNo;
          return n;
        })
      },
      "onSelect": function (event, index, data, hook) {
        $(hook).val(data.licenceNo);
        droplist.hide();
      }
    });
  }, 'json');
}


function initOBDList(result,from){
  $("#obdTable tr:gt(0)").remove();
  if(G.isEmpty(result)||G.isEmpty(result.results)){
    var  tr='<tr><td colspan="15" style="text-align:center;">无数据！</td></tr>';
    $("#obdTable").append(tr);
    return;
  }
  var obdSimBinds=result.results;
  var tableStr="";
  for(var i=0;i<obdSimBinds.length;i++){
    var obdSimBind=obdSimBinds[i];
    var obdIdStr=obdSimBind.obdIdStr;
    var storageTimeStr=G.normalize(obdSimBind.storageTimeStr);
    var mobile=G.normalize(obdSimBind.mobile);
    var imei=G.normalize(obdSimBind.imei);
    var useDateStr=obdSimBind.useDateStr;
    var useEndDateStr=obdSimBind.useEndDateStr;
    var usePeriod=obdSimBind.usePeriod;
    var payStatus="免费期";
    //obd和后视镜区分
    var obd_sim_typeStr = "";
    var obd_sim_type = obdSimBind.obdSimType;
    if("COMBINE_GSM_OBD_SIM" == obd_sim_type){
      obd_sim_typeStr = "OBD";
    }else if("COMBINE_GSM_OBD_SSIM" == obd_sim_type){
      obd_sim_typeStr = "(2s)OBD";
    }else{
      obd_sim_typeStr = "后视镜"
    }
    //
    if(useEndDateStr<dateUtil.getToday()){
      payStatus="自费期";
    }
    var vehicleId=G.normalize(obdSimBind.vehicleIdStr);
    var licenceNo=G.normalize(obdSimBind.licenceNo);
    var vehicleBrand=G.normalize(obdSimBind.vehicleBrand);
    var vehicleModel=G.normalize(obdSimBind.vehicleModel);
    vehicleBrand+=" "+vehicleModel;
    var customerId=obdSimBind.customerIdStr;
    var customerName=G.normalize(obdSimBind.customerName);
    var customerMobile=G.normalize(obdSimBind.customerMobile);
    var sellTimeStr=G.normalize(obdSimBind.sellTimeStr);
    var obdStatusStr=G.normalize(obdSimBind.obdStatusStr);
    if(i%2==0){
      tableStr+='<tr>';
    }else{
      tableStr+='<tr class="greyGround">';
    }
    tableStr+='<td>'+(i+1)+'</td>'+
        '<td>'+obd_sim_typeStr+'</td>'+
        '<td>'+storageTimeStr+'</td>'+
        '<td>'+imei+'</td>'+
        '<td>'+mobile+'</td>'+
        '<td>'+useDateStr+'</td>'+
        '<td>'+useEndDateStr+'</td>'+
        '<td>'+usePeriod+'年</td>'+
        '<td>'+payStatus+'</td>'+
        '<td>'+
        '<a target="_blank" href="unitlink.do?method=customer&customerId='+customerId+'" class="blue_color">'+customerName+'</a><br />'+customerMobile+'</td>'+
        '<td>'+
        '<a target="_blank" href="vehicleManage.do?method=toVehicleDetail&customerIdStr='+customerId+'&vehicleIdStr='+vehicleId+'" class="blue_color">'+licenceNo+'</a><br />'+vehicleBrand+'</td>'+
        '<td>'+sellTimeStr+'</td>'+
        '<td>'+obdStatusStr+'</td>'+
        '<td>';
    if(G.isEmpty(vehicleId)){
      tableStr+='<a class="blue_color vehicle_bind_btn" obdId="'+obdIdStr+'">车辆安装</a><br/>';
    }
    tableStr+='<input type="hidden" class="obd_id" value="'+obdIdStr+'"/>';
    tableStr+='<input type="hidden" class="obd_imei" value="'+imei+'"/>';
    tableStr+='<input type="hidden" class="sim_no" value="'+mobile+'"/>';
    if(APP_BCGOGO.Permission.CustomerManager.VehiclePosition && G.Lang.isNotEmpty(customerId) && G.Lang.isNotEmpty(vehicleId)){
      tableStr+='<a class="blue_color" href="vehicleManage.do?method=toVehiclePosition&customerIdStr='+customerId+'&vehicleIdStr='+vehicleId+'">智能定位</a><br/>';
    }
    if(APP_BCGOGO.Permission.CustomerManager.VehicleDriveLog && G.Lang.isNotEmpty(customerId) && G.Lang.isNotEmpty(vehicleId)){
      tableStr+='<a class="blue_color" href="vehicleManage.do?method=toVehicleDriveLog&customerIdStr='+customerId+'&vehicleIdStr='+vehicleId+'">行车日志</a>';
    }
    tableStr+'</td></tr>';
  }
  $("#obdTable").append(tableStr);
  if(from!="stat"){
    var data=result.data;
    $(".shop_obd_total").text(data.shop_obd_total);
    $(".shop_obd_on_sell").text(data.shop_obd_on_sell);
    $(".shop_obd_sold").text(data.shop_obd_sold);
  }

}

function getTrSample(){
  return '<tr class="item">'+
      '<td>'+
      '<input type="hidden" name="obdSimBindDTOs[0].obdId"/>'+
      '<span class="seq_sp">1</span>'+
      '</td>'+
      '<td><input type="text" name="obdSimBindDTOs[0].imei" class="txt" /></td>'+
      '<td><input type="text" name="obdSimBindDTOs[0].mobile" class="txt" /></td>'+
      '<td><span class="use_date"></span></td>'+
      '<td><span class="use_end_date"></span></td>'+
      '<td><span class="use_period"></span></td>'+
      '<td><a index="0" class="btnMinus blue_color" style="color: #007cda;">删除</a></td>'+
      '</tr>';
}

function isShowAddButton() {
  if ($(".item").size() <= 0) {
    obdBindAdd();
  }
  $(".item .btnPlus").remove();
  $(".item:last").find(".btnMinus").after('<a class="btnPlus blue_color" style="color: #007cda;margin-left: 3px;">增加</a>');

}


function obdBindAdd(){
  var index=0;
  if($(".item").size()!=0){
    index=Number($(".item:last").find(".btnMinus").attr("index"))+1;
  }
  var _$tr=$(getTrSample());
  _$tr.find(".seq_sp").text(index+1);
  _$tr.find(".btnMinus").attr("index",index);
  _$tr.find('input[name$="obdId"]').attr("name","obdSimBindDTOs["+index+"].obdId");
  _$tr.find('input[name$="imei"]').attr("name","obdSimBindDTOs["+index+"].imei");
  _$tr.find('input[name$="mobile"]').attr("name","obdSimBindDTOs["+index+"].mobile");
  $("#obdStorageTable tr:last").after(_$tr);
  isShowAddButton();
  return _$tr;
}