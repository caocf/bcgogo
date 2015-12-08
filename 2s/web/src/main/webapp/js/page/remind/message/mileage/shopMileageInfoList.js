$(function () {
//    reset();

  $("[ation-type=select-all]").change(function (e) {
    $("[ation-type=select-all],#shopFaultInfoTables [type=checkbox]").attr("checked", $(e.target).attr("checked"));
  });

  $("#searchAction").click(function () {
    searchShopFaultInfo();
  });

  $("#resetAction").click(function () {
    reset();
  });

  $('[area-type=time] .btnList').click(function (e) {
    if ($(this).hasClass('clicked')) {
      $(this).removeClass('clicked');
      $("#timeStart,#timeEnd").val("");
    } else {
      $('[area-type=time] .btnList').removeClass('clicked');
      $(this).addClass('clicked');
      var today = getDate(new Date(), 0);
      switch ($(this).attr("data-type")) {
        case 'yestoday':
          $("#timeStart,#timeEnd").val(getDate(new Date(), 1));
          break;
        case 'today':
          $("#timeStart,#timeEnd").val(today);
          break;
        case 'lastweek':
          $("#timeStart").val(getDate(new Date(), 6));
          $("#timeEnd").val(today);
          break;
        case 'lastmonth':
          $("#timeStart").val(getDate(new Date(), 29));
          $("#timeEnd").val(today);
          break;
        case 'lastyear':
          $("#timeStart").val(getDate(new Date(), 364));
          $("#timeEnd").val(today);
          break;
      }
    }
    searchShopFaultInfo();
    function getDate(date, beforeDays) {
      var ms = date.getTime(),
          time = ms - dateUtil.millSecondOneDay * beforeDays,
          result = new Date(time);
      return result.getFullYear() + "-" + ( result.getMonth() + 1) + "-" + result.getDate();
    }
  });

  $('[action-type=send-sms-and-client-msg]').live("click", function () {
    var $promp = $('#send_sms_and_client_msg'), me = this;
    APP_BCGOGO.Net.asyncGet({
      url: "shopFaultInfo.do?method=getFaultInfoCodeSMSTemplate",
      data: {
        "licenceNo": $(me).attr('licenceNo'),
        "code": $(me).attr('faultCode'),
        "time": $(me).attr('time'),
        "faultAlertType": $(me).attr('faultAlertType'),
        "faultAlertTypeValue": $(me).attr('faultAlertTypeValue')

      },
      dataType: "json",
      success: function (result) {
        if (result && !$.isEmptyObject(result)) {
          $promp.find('.J_prompt_textarea').html(result['content'] + result['name']);
          $promp.find('.J_prompt_textarea').attr("content", result['content']);
          $promp.find('.J_prompt_textarea').attr("name", result['name']);
          $promp.find('.J_prompt_textarea').attr("appUserNo", $(me).attr('appUserNo'));
          $promp.find('.J_prompt_textarea').attr("mobile", $(me).attr('mobile'));
          $promp.find('.J_prompt_textarea').attr("customerId", $(me).attr('customerId'));
          $promp.find('.J_prompt_textarea').attr("data-id", $(me).attr("data-id"));
          var messageSize = result['content'].length;
          var messageCount =  Math.ceil( messageSize / 67);
          $promp.find(".J_message_size").html(messageSize);
          $promp.find(".J_message_count").html(messageCount+"条");

          Mask.Login();
          $promp.show()
        } else {
          nsDialog.jAlert("数据异常!");
        }
      }
    });
  });

  $("#sendSmsAppAction").click(function () {
    var $promp = $('#send_sms_and_client_msg');
    var sendSms = $promp.find('[name=sendSms]').attr('checked'),
        sendApp = $promp.find('[name=sendApp]').attr('checked'),
        content = $promp.find('.J_prompt_textarea').attr("content"),
        id = $promp.find('.J_prompt_textarea').attr("data-id"),
        appUserNo = $promp.find('.J_prompt_textarea').attr("appUserNo"),
        mobile = $promp.find('.J_prompt_textarea').attr("mobile"),
        name = $promp.find('.J_prompt_textarea').attr("name"),
        customerId = $promp.find('.J_prompt_textarea').attr("customerId")
        ;
    if (!sendSms && !sendApp) {
      nsDialog.jAlert("请选择发送手机短信或发送手机客户端信息！");
    } else {
      APP_BCGOGO.Net.asyncGet({
        url: "shopFaultInfo.do?method=sendSMSAndAppNotice",
        data: {
          "id": id,
          "content": content,
          "appUserNo": appUserNo,
          "customerId": customerId,
          "mobile": mobile,
          "shopName": name,
          "sendSms": sendSms,
          "sendApp": sendApp
        },
        dataType: "json",
        success: function (result) {
          if (result && result.success) {
            prompReset();
            searchShopFaultInfo();
          }else if(result && !result.success) {
            nsDialog.jAlert(result.msg);
          }

        }
      });
    }
  });

  function prompReset() {
    var $promp = $('#send_sms_and_client_msg');
    Mask.Logout();
    $promp.hide();
    $promp.find('.J_prompt_textarea').html("");
    $promp.find('.J_prompt_textarea').removeAttr("data-id");
    $promp.find('.J_prompt_textarea').removeAttr("content");
    $promp.find('.J_prompt_textarea').removeAttr("customerId");
    $promp.find('.J_prompt_textarea').removeAttr("appUserNo");
    $promp.find('.J_prompt_textarea').removeAttr("mobile");
    $promp.find('.J_prompt_textarea').removeAttr("name");
  }

  $("#close_prompt_box,#cancel_prompt_box").click(function () {
    prompReset();
  });

  $("#timeStart,#timeEnd")
      .datepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "constrainInput": false,
        "yearRange": "c-100:c+100",
        "yearSuffix": ""
      })
      .bind("click", function () {
        $(this).blur();
      })
      .change(function () {
        var startDate = $("#timeStart").val();
        var endDate = $("#timeEnd").val();
        if (!endDate || !startDate) {
          return;
        }
        if (Number(startDate.replace(/\-/g, "")) > Number(endDate.replace(/\-/g, ""))) {
          $("#timeEnd").val(startDate);
          $("#timeStart").val(endDate);
        }
      });

  $("[name=vehicleNo]").bind("click keyup", function (event) {
    if (event.type == "keyup" && (GLOBAL.Interactive.keyNameFromEvent(event)).search(/^left$|^right$|^up$|^down$|^shift$|^ctrl$|^alt$/g) != -1)return;
    var obj = this,
        droplist = APP_BCGOGO.Module.droplist;
    clearTimeout(droplist.delayTimerId || 1);
    droplist.delayTimerId = setTimeout(function () {
      var droplist = APP_BCGOGO.Module.droplist;
      var keyword = $(obj).val();
      var uuid = GLOBAL.Util.generateUUID();
      droplist.setUUID(uuid);
      APP_BCGOGO.Net.asyncGet({
        url: "shopFaultInfo.do?method=getShopFaultInfoVehicleNoSuggestion",
        data: {
          "uuid": uuid,
          "keyword": keyword,
          "now": new Date()
        },
        dataType: "json",
        success: function (result) {
          droplist.show({
            "selector": $(event.currentTarget),
            "isEditable": false,
            "originalValue": {
              label: keyword
            },
            "data": result,
            "saveWarning": "保存此修改影响全局数据",
            "isDeletable": false,
            "onSelect": function (event, index, data, hook) {
              $(hook).val(data['label']);
              droplist.hide();
            }
          });
        }
      });
    }, 200);
    if (GLOBAL.Interactive.isKeyName(event, "enter")) {
      droplist.hide();
      searchShopFaultInfo();
    }
  });

  $("[name=mobile]").bind("click keyup", function (event) {
    if (event.type == "keyup" && (GLOBAL.Interactive.keyNameFromEvent(event)).search(/^left$|^right$|^up$|^down$|^shift$|^ctrl$|^alt$/g) != -1)return;
    var obj = this,
        droplist = APP_BCGOGO.Module.droplist;
    clearTimeout(droplist.delayTimerId || 1);
    droplist.delayTimerId = setTimeout(function () {
      var droplist = APP_BCGOGO.Module.droplist;
      var keyword = $(obj).val();
      var uuid = GLOBAL.Util.generateUUID();
      droplist.setUUID(uuid);
      APP_BCGOGO.Net.asyncGet({
        url: "shopFaultInfo.do?method=getShopFaultInfoMobileSuggestion",
        data: {
          "uuid": uuid,
          "keyword": keyword,
          "now": new Date()
        },
        dataType: "json",
        success: function (result) {
          droplist.show({
            "selector": $(event.currentTarget),
            "isEditable": false,
            "originalValue": {
              label: keyword
            },
            "data": result,
            "saveWarning": "保存此修改影响全局数据",
            "isDeletable": false,
            "onSelect": function (event, index, data, hook) {
              $(hook).val(data['label']);
              droplist.hide();
            }
          });
        }
      });
    }, 200);

    if (GLOBAL.Interactive.isKeyName(event, "enter")) {
      droplist.hide();
      searchShopFaultInfo();
    }
  });

  $("#generateAppointOrder").click(function () {
    var checks = $("#shopFaultInfoTables tr:gt(0)").find(":checkbox:checked"),

        ids = "";
    var isValidate = true;
    var vehicleNoSet = new Set();
    var validateMsg = "";
    $.each(checks, function (num, check) {
      if (num != 0) {
        ids += ","
      }
      vehicleNoSet.add($(check).attr("data-vehicle"));
      if (vehicleNoSet.size > 1) {
        isValidate = false;
        validateMsg += "请选择同一辆车做预约";
        return false;
      }
      ids += $(check).attr("data-id");
    });
    if (isValidate) {
      generateAppointOrder(ids);
    } else {
      nsDialog.jAlert(validateMsg);
    }

  });

  $("#exportFaultInfo").click(function () {
    var $this = $(this);
    var $exportCover = $("#exportFaultInfoCover");
    if($this.attr("lock")){
      return;
    }else{
      try{
        $this.attr("lock",true);
        $exportCover.show();
        $this.hide();
        var data ={};
        if ($("[name=isSendMessage]").attr('checked')) {
          data['isSendMessage'] = 'YES';
        }
        if ($("[name=isCreateAppointOrder]").attr('checked')) {
          data['isCreateAppointOrder'] = 'YES';
        }
        if ($("[name=isUntreated]").attr('checked')) {
          data['isUntreated'] = 'YES';
        }
        if ($("[name=isDeleted]").attr('checked')) {
          data['isDeleted'] = 'YES';
        }
        data['vehicleNo'] = $("[name=vehicleNo]").val();
        data['mobile'] = $("[name=mobile]").val();
        data['faultAlertType'] = $("[name=faultAlertType]").val();
        var timeStart = $("#timeStart").val(), timeEnd = $("#timeEnd").val();
        if (timeStart)data['timeStart'] = new Date(Date.parse(timeStart.replace(/-/g, "/"))).getTime();
        if (timeEnd)data['timeEnd'] = new Date(Date.parse((timeEnd + " 23:59").replace(/-/g, "/"))).getTime();
        var url = "export.do?method=exportShopFaultInfo";
        APP_BCGOGO.Net.asyncPost({
          url: url,
          data: data,
          dataType: "json",
          cache: false,
          success: function (json) {
            $this.removeAttr("lock");
            $exportCover.hide();
            $this.show();
            if(json && json.exportFileDTOList) {
              if(json.exportFileDTOList.length > 1) {
                showDownLoadUI(json);
              } else {
                window.open("download.do?method=downloadExportFile&exportFileName=事故故障提醒.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
              }
            }
          },
          error: function () {
            nsDialog.jAlert("数据异常!");
          }
        });
      }catch (e){
        $this.removeAttr("lock");
        $exportCover.hide();
        $this.show();
      }
    }
  });

  $("[action=generateAppointOrder]").live("click",function () {
    var ids = $(this).attr('data-id');
    generateAppointOrder(ids);
  });

  $("[action=toAppointOrder]").live("click",function () {
    var id = $(this).attr('data-id');
    window.location.href = "appoint.do?method=showAppointOrderDetail&appointOrderId=" + id;
  });

  function generateAppointOrder(ids) {
    // todo qiuxinyu 支持
    window.location.href = "/web/appoint.do?method=createAppointOrderByShopFaultCodeIds&shopFaultInfoIds="+ids;
//        APP_BCGOGO.Net.asyncGet({
//            url: "shopFaultInfo.do?method=generateAppointOrder",
//            data: {'id': ids},
//            dataType: "json",
//            success: function (result) {
//                searchShopFaultInfo();
//            }
//        });
  }

  $("#deleteAction").click(function () {
    var checks = $("#shopFaultInfoTables :checkbox:checked"), ids = "";
    $.each(checks, function (num, check) {
      var dataId = G.Lang.normalize($(check).attr("data-id"));
      if(G.Lang.isNotEmpty(dataId)){
        if (G.Lang.isNotEmpty(ids) != 0) {
          ids += ","
        }
        ids += dataId;
      }
    });
    if (G.Lang.isEmpty(ids)) {
      nsDialog.jAlert("请选择需要删除的碰撞记录！");
    } else {
      nsDialog.jConfirm("您确定要删除所选碰撞记录吗？", null, function (resultVal) {
        if (resultVal) {
          APP_BCGOGO.Net.asyncGet({
            url: "shopImpactInfo.do?method=deleteImpactInfoCode",
            data: {'id': ids},
            dataType: "json",
            success: function (result) {
              searchShopFaultInfo();
            }
          });
        }
      })
    }
  });

  function reset() {
    $("#timeStart,#timeEnd").val("");
    $("[name=vehicleNo]").val("");
    $("[name=mobile]").val("");
    $('[area-type=time] .btnList').removeClass('clicked');
    $("[name=isCreateAppointOrder]").attr("checked", false);
    $("[name=isUntreated]").attr("checked", true);
    $("[name=isSendMessage]").attr("checked", false);
    $("[name=isDeleted]").attr("checked", false);
    $("#sfi_startPageNo").val("1");
    $("#sfi_maxRows").val("15");
  }

  function searchShopFaultInfo() {
    var data = {startPageNo: $("#sfi_startPageNo").val(), maxRows: $("#sfi_maxRows").val()};
//        if ($("[name=isSendMessage]").attr('checked')) {
//            data['isSendMessage'] = 'YES';
//        }
//        if ($("[name=isCreateAppointOrder]").attr('checked')) {
//            data['isCreateAppointOrder'] = 'YES';
//        }
//    if ($("[name=isUntreated]").attr('checked')) {
//      data['isUntreated'] = 'YES';
//    }
//    if ($("[name=isDeleted]").attr('checked')) {
//      data['isDeleted'] = 'YES';
//    }
    data['vehicleNo'] = $("[name=vehicleNo]").val();
//        data['mobile'] = $("[name=mobile]").val();
        data['mileageType'] = $("[name=mileageType]").val();
//    var timeStart = $("#timeStart").val(), timeEnd = $("#timeEnd").val();
//    if (timeStart)data['timeStart'] = new Date(Date.parse(timeStart.replace(/-/g, "/"))).getTime();
//    if (timeEnd)data['timeEnd'] = new Date(Date.parse((timeEnd + " 23:59").replace(/-/g, "/"))).getTime();
    var url = "shopMileageInfo.do?method=searchShopMileageInfoList";
    APP_BCGOGO.Net.asyncPost({
      url: url,
      data: data,
      dataType: "json",
      success: function (json) {
        initPage(json, '_shopFaultInfo', url, '', 'drawShopFaultInfoTable', '', '', data, '');
        drawShopFaultInfoTable(json);
      },
      error: function () {
        nsDialog.jAlert("数据异常!");
      }
    });
  }

  $("[action=searchInMap]").live("click",function(e){
    e.preventDefault();
    var coordinateLon = $(this).attr("data-lon");
    var coordinateLat = $(this).attr("data-lat");
//        var coordinateLat=31.488557;
//        var coordinateLon=120.584905;
    if(G.isNotEmpty(coordinateLat) && G.isNotEmpty(coordinateLon)){
      $("#map_container_iframe")[0].src = "api/proxy/baidu/map/common?size=big&coordinateLat=" + coordinateLat + "&coordinateLon=" + coordinateLon;
      Mask.Login();
      $("#map_container_iframe_div").show();
    }
  });
  $("[action=close_map_container_iframe]").click(function(){
    $("#map_container_iframe_div").hide();
    Mask.Logout();
  });
});

function drawShopFaultInfoTable(json) {
  $("#shopFaultInfoTables tr:not(:first)").remove();
  if (!G.isEmpty(json)) {
    //只有出现大于等于两组数据的时候才显示分组的条件
    var isShowTitle = false;
    var countGt2 = 0;
    if(json['todayShopMileageInfoList'] &&json['todayShopMileageInfoList'].length>0 ){
      countGt2 ++;
    }
    if(json['yesterdayShopFaultInfoList'] && json['yesterdayShopFaultInfoList'].length >0){
      countGt2 ++;
    }
    if(json['moreShopFaultInfoList'] && json['moreShopFaultInfoList'].length >0){
      countGt2 ++;
    }
    if(countGt2 >1){
      isShowTitle = true;
    }
    drawTableArea(json['todayShopMileageInfoList'], isShowTitle ? json['todayTotalRows'] : "","今天");
//        drawTableArea(json['yesterdayShopFaultInfoList'], isShowTitle ? json['yesterdayTotalRows']:"","昨天");
//        drawTableArea(json['moreShopFaultInfoList'],isShowTitle ? json['moreTotalRows']:"","更多");
    if (countGt2 == 0) {
      var  html = '<tr class="titBody_Bg"><td colspan="9"><span class="gray_color">未找到您搜索的相关提醒！</span></td></tr>';
      $("#shopFaultInfoTables").append(html);
      $("#multiOperationContainer").hide();
    }else{
      $("#multiOperationContainer").show();
    }
  }
  function drawTableArea(datas, numberStr,titleDayStr) {
    var tr = "";
    if (!G.isEmpty(datas) && !G.isEmpty(datas[0])) {
      if (G.Lang.isNotEmpty(numberStr)) {
        tr += '<tr>';
        tr += '   <td colspan="9" style=" border-right:0;border-bottom:#2B496B 3px solid; padding-left:0; padding-top:10px; font-size:14px; "> <b>'+
            titleDayStr+'（<a class="orange_color">'+numberStr+'</a>条）</b></td>';
        tr += '</tr>';
      }

      $.each(datas, function (index, value) {
        var vehicleNo = G.Lang.normalize(value['vehicleNo']);
        var mobile = G.Lang.normalize(value['mobile']);
        var contact = G.Lang.normalize(value['contact']);
        var customerName = G.Lang.normalize(value['customerName']);
        var customerMobile = G.Lang.normalize(value['customerMobile']);
        var nextMaintainMileage = G.Lang.normalize(value['nextMaintainMileage']);
        var currentMileage = G.Lang.normalize(value['currentMileage']);
        var toNextMaintainMileage =  G.Lang.normalize(value['toNextMaintainMileage']);
        var customerId =  G.Lang.normalize(value['customerId']);
        var appUserNo =  G.Lang.normalize(value['appUserNo']);
        if(index%2==0){
          tr += '<tr>';
        }else{
          tr += '<tr class="greyGround">';
        }
//        tr += '    <td class="score_table2_left"><input type="checkbox" data-id="' + toNextMaintainMileage + '" data-vehicle="'+value['toNextMaintainMileage']+'"/></td>';
        tr += '    <td class="score_table2_left">' + vehicleNo + '</td>';
        tr += '    <td>' + '<a class="blue_color" action="searchCustomerDetail" data-id="'+customerId+'">'+contact+' '+mobile  +'</a>'+ '</td>';
        tr += '    <td>' + '<a class="blue_color" action="searchCustomerDetail" data-id="'+customerId+'">'+customerName+' '+customerMobile  +'</a>'+ '</td>';
        tr += '    <td>' + nextMaintainMileage + '</td>';
        tr += '    <td>' + currentMileage + '</td>';
//                if(G.Lang.isEmpty(value['mobile'])){
//                    tr += '<td></td>';
//                }else{
//                    tr += '    <td><span class="sms_phone">' + G.Lang.normalize(value['mobile']) + '</span><a style="cursor: pointer" class="btn_sms" data-id="' + id + '"  customerId="' + customerId + '" appUserNo="' + value['appUserNo'] + '" mobile="' + value['mobile'] + '"  licenceNo="' + value['vehicleNo'] + '" faultCode="' + faultCode + '" faultAlertType="' + value['faultAlertType'] + '" faultAlertTypeValue="' + value['faultAlertTypeValue'] + '" time="' + value['faultCodeReportTimeStr'] + '" action-type="send-sms-and-client-msg"></a></td>';
//                }

//                tr += '    <td title="' + customerName + '">'+(customerName.length>11?customerName.substr(0,11)+'...':customerName)+'<br>'+customerMobile + '</td>';
        tr += '    <td>' + toNextMaintainMileage + ' </td>';
        tr += '<td>';
        if (value['isCreateAppointOrder'] == 'YES') {
          tr += '<a class="blue_color" action="toAppointOrder" data-id="' + vehicleNo + '">查看预约单</a>';
        } else {
          tr += '<a class="blue_color" action="createAppointOrderByCustomerIdAndAppUserNo" data-id1="' + appUserNo + '" data-id2="' + customerId + '">生成预约</a>';
        }
//        if(G.Lang.isNotEmpty(lon) && G.Lang.isNotEmpty(lat)){
//          tr += '<br/><a class="blue_color" action="searchInMap" data-lon="'+lon+'" data-lat="'+lat+'">查看位置</a>';
//        }
        tr += '</td>';
        tr += '</tr>';
      });
      $("#shopFaultInfoTables tr:last").after(tr);
    }
  }
}

$("[action=searchCustomerDetail]").live("click",function () {
  var id = $(this).attr('data-id');
  window.location.href = "unitlink.do?method=customer&customerId=" + id;
});

$("[action=createAppointOrderByCustomerIdAndAppUserNo]").live("click",function () {
  var appUserNo = $(this).attr('data-id1');
  var customerId = $(this).attr('data-id2');
  window.location.href = "appoint.do?method=createAppointOrderByCustomerIdAndAppUserNo&customerId=" + customerId+"&appUserNo="+appUserNo;
});