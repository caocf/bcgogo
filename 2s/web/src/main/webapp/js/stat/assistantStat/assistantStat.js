var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;


$(document).ready(function() {


  $("#startYear").val($("#startYearHidden").val());
   $("#startMonth").val($("#startMonthHidden").val());
   $("#endYear").val($("#endYearHidden").val());
   $("#endMonth").val($("#endMonthHidden").val());
   $("#achievementStatType").val($("#achievementStatTypeHidden").val());
   $("#achievementCalculateWayStr").val($("#achievementCalculateWayHidden").val());
   $("#achievementOrderTypeStr").val($("#achievementOrderTypeStrHidden").val());

   var url = "assistantStat.do?method=getAssistantStatList";

  var year = dateUtil.rangeData.nowYear();
  var month = dateUtil.rangeData.nowMonth();
  if ($("#startYear").val() == year && $("#endYear").val() == year && $("#startMonth").val() == month + 1 && $("#endMonth").val() == month + 1) {
    $("#thisMonth").click();
  }


   var ajaxDate = {}
   bcgogoAjaxQuery.setUrlData(url, ajaxDate);
   bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {

     if (stringUtil.isNotEmpty(jsonStr)) {
       if (stringUtil.isNotEmpty(jsonStr.ASSISTANT)) {
         jsonStrMap.put("salesManList", jsonStr.ASSISTANT);
         var salesManList = jsonStr.ASSISTANT;
         $("#assistantOrDepartmentIdStr option:not(:first)").remove();
         if (salesManList && salesManList != "" && salesManList.length > 0) {
           for (var i = 0; i < salesManList.length; i++) {
             var option = "<option value='" + salesManList[i].idStr + "'>" + salesManList[i].name + "</option>";
             $(option).appendTo($("#assistantOrDepartmentIdStr"));
           }
         }

       }
       if (stringUtil.isNotEmpty(jsonStr.DEPARTMENT)) {
         jsonStrMap.put("departmentList", jsonStr.DEPARTMENT);

       }
       if (stringUtil.isNotEmpty(jsonStr.service)) {
         jsonStrMap.put("serviceList", jsonStr.service);
       }

       if (stringUtil.isNotEmpty($("#assistantOrDepartmentIdStrHidden").val())) {
         if ($("#achievementStatTypeHidden").val() == "DEPARTMENT") {
           var jsonStr = jsonStrMap.get("departmentList");
           $("#assistantOrDepartmentIdStr option:not(:first)").remove();
           if (jsonStr && jsonStr != "" && jsonStr.length > 0) {
             for (var i = 0; i < jsonStr.length; i++) {
               var option = "<option value='" + jsonStr[i].idStr + "'>" + jsonStr[i].name + "</option>";
               $(option).appendTo($("#assistantOrDepartmentIdStr"));
             }
           }
           $("#assistantOrDepartmentIdStr").val($("#assistantOrDepartmentIdStrHidden").val());
         } else if ($("#achievementStatTypeHidden").val() == "ASSISTANT") {
           $("#assistantOrDepartmentIdStr").val($("#assistantOrDepartmentIdStrHidden").val());
         }
       }


       if (stringUtil.isNotEmpty($("#achievementOrderTypeStrHidden").val())) {

         $("#achievementOrderTypeStr").val($("#achievementOrderTypeStrHidden").val());
         if ($("#serviceIdStrHidden").val() == "NEW" || $("#serviceIdStrHidden").val() == "RENEW") {
           $("#serviceIdStr option:not(:first)").remove();
           var option = "<option value='NEW'>购卡</option>" + "<option value='RENEW'>续卡</option>";
           $(option).appendTo($("#serviceIdStr"));
           $("#serviceIdStr").val($("#serviceIdStrHidden").val());
         } else if ($("#achievementOrderTypeStrHidden").val() == "WASH_BEAUTY" || $("#achievementOrderTypeStrHidden").val() == "REPAIR_SERVICE") {
           $("#serviceIdStr option:not(:first)").remove();
           var jsonStr = jsonStrMap.get("serviceList");
           if (jsonStr && jsonStr != "" && jsonStr.length > 0) {

             for (var i = 0; i < jsonStr.length; i++) {
               var option = "<option value='" + jsonStr[i].idStr + "'>" + jsonStr[i].name + "</option>";
               $(option).appendTo($("#serviceIdStr"));
             }
           }
           $("#serviceIdStr").val($("#serviceIdStrHidden").val());
         }
       }

       queryDate();
     }
   });

   $("#achievementStatType").live("change",
       function (e) {
         if ($(e.target).attr("lastValue") == $(e.target).val()) {
           return;
         }
         $(this).attr("lastValue", $(this).val());

         $("#assistantOrDepartmentIdStr option:not(:first)").remove();

         var jsonStr = "";
         if ($(this).val() == "ASSISTANT") {
           jsonStr = jsonStrMap.get("salesManList");
         } else if ($(this).val() == "DEPARTMENT") {
           jsonStr = jsonStrMap.get("departmentList");
         }

         if (jsonStr && jsonStr != "" && jsonStr.length > 0) {
           for (var i = 0; i < jsonStr.length; i++) {
             var option = "<option value='" + jsonStr[i].idStr + "'>" + jsonStr[i].name + "</option>";
             $(option).appendTo($("#assistantOrDepartmentIdStr"));
           }
         }

       }).live("focus", function () {
         $(this).attr("lastValue", $(this).val());
       });

   $("#achievementOrderTypeStr").live("change",
       function (e) {
         if ($(e.target).attr("lastValue") == $(e.target).val()) {
           return;
         }
         $(this).attr("lastValue", $(this).val());

         $("#serviceIdStr option:not(:first)").remove();

         if ($("#achievementOrderTypeStr").val() == "ALL") {
           $("#serviceIdStr").val("");
           $("#serviceIdStr").css("display", "block");
           return;
         } else if ($("#achievementOrderTypeStr").val() == "WASH_BEAUTY" || $("#achievementOrderTypeStr").val() == "REPAIR_SERVICE") {
           $("#serviceIdStr").css("display", "block");
           var jsonStr = jsonStrMap.get("serviceList");
           if (jsonStr && jsonStr != "" && jsonStr.length > 0) {

             for (var i = 0; i < jsonStr.length; i++) {
               var option = "<option value='" + jsonStr[i].idStr + "'>" + jsonStr[i].name + "</option>";
               $(option).appendTo($("#serviceIdStr"));
             }
           }

         } else if ($("#achievementOrderTypeStr").val() == "MEMBER") {
           $("#serviceIdStr").css("display", "block");
           var option = "<option value='NEW'>购卡</option>" + "<option value='RENEW'>续卡</option>";
           $(option).appendTo($("#serviceIdStr"));
         } else if ($("#achievementOrderTypeStr").val() == "SALES" || $("#achievementOrderTypeStr").val() == "BUSINESS_ACCOUNT") {
           $("#serviceIdStr").val("");
           $("#serviceIdStr").css("display", "none");
         }
       }).live("focus", function () {
         $(this).attr("lastValue", $(this).val());
       });

   $("#achievementCalculateWayStr").live("change",
       function (e) {
         if ($(e.target).attr("lastValue") == $(e.target).val()) {
           return;
         }
         $(this).attr("lastValue", $(this).val());

         $("#achievementCalculateWayHidden").val($(this).val());

       }).live("focus", function () {
         $(this).attr("lastValue", $(this).val());
       });


  $("#queryAssistantStat").bind("click", function() {
    queryDate();
  });


    $("#lastMonth").bind("click", function() {
    var year = dateUtil.rangeData.nowYear();
    var month = dateUtil.rangeData.nowMonth();

    if (month == 0) {
      $("#startYear").val(year - 1);
      $("#startMonth").val(12);
      $("#endYear").val(year - 1);
      $("#endMonth").val(12);
    } else {
      $("#startYear").val(year);
      $("#startMonth").val(month);
      $("#endYear").val(year);
      $("#endMonth").val(month);
    }


  });
  $("#thisMonth").bind("click", function() {
    var year = dateUtil.rangeData.nowYear();
    var month = dateUtil.rangeData.nowMonth();

    $("#startYear").val(year);
    $("#startMonth").val(month + 1);
    $("#endYear").val(year);
    $("#endMonth").val(month + 1);

  });
  $("#thisYear").bind("click", function() {
    var year = dateUtil.rangeData.nowYear();
    var month = dateUtil.rangeData.nowMonth();
    $("#startYear").val(year);
    $("#startMonth").val(1);
    $("#endYear").val(year);
    $("#endMonth").val(month + 1);

  });

   $("#printButton").live("click",function(){
        var url;
        APP_BCGOGO.Net.syncGet({
            url:"print.do?method=getTemplates",
           data:{
                "orderType":"ASSISTENT_STAT",
                "now":new Date()
            },
            dataType:"json",
            success:function(result) {
                if(result && result.length >1){
                    var selects = "<div style='margin:10px;font-size:15px;line-height: 22px;'>" +
                        "<div style='margin-bottom:5px;'>请选择打印模板：</div>";
                    for(var i = 0; i<result.length; i++){
                        var radioId = "selectTemplate" + i;
                        selects += "<input type='radio' id='"+radioId+"' name='selectTemplate' value='"+result[i].idStr+"'";
                        if(i==0){
                            selects += " checked='checked'";
                        }
                        selects += " />" +"<label for='"+radioId+"'>"+result[i].displayName +"</label><br/>";
                    }
                    selects += "</div>";
                    nsDialog.jConfirm(selects, "请选择打印模板", function (returnVal) {
                        if (returnVal) {
                            printPageContent($("input:radio[name='selectTemplate']:checked").val());
                        }
                    });
                }else{
                    printPageContent();
                }
            }
        });
    });


    $("#exportButton").bind("click",function(){
        $(this).attr("disabled",true);
        $("#exporting").css("display","");
        if($("#departmentStatTable").find("tr").size() == 0) {
            nsDialog.jAlert("对不起，暂无数据，无法导出！");
            $(this).removeAttr("disabled");
            $("#exporting").css("display","none");
            return;
        }

        if ($("#startYear").val() >= $("#endYear").val()) {
            var endYear = $("#endYear").val();
            $("#endYear").val($("#startYear").val());
            $("#startYear").val(endYear);
        }
        var ajaxData = {
            startYear: $("#startYear").val(),
            startMonth: $("#startMonth").val(),
            endYear: $("#endYear").val(),
            endMonth: $("#endMonth").val(),
            startPageNo: $("#startPageNoHiddenHidden").val(),
            maxRows: 15,
            achievementStatTypeStr: $("#achievementStatType").val(),
            assistantOrDepartmentIdStr: $("#assistantOrDepartmentIdStr").val(),
            achievementOrderTypeStr: $("#achievementOrderTypeStr").val(),
            achievementCalculateWayStr: $("#achievementCalculateWayStr").val(),
            serviceIdStr: $("#serviceIdStr").val()
        }
        var ajaxUrl="export.do?method=exportBusinessStaffTransaction";
        bcgogoAjaxQuery.setUrlData(ajaxUrl,ajaxData);
        bcgogoAjaxQuery.ajaxQuery(function (json) {


            $("#export").removeAttr("disabled");
            $("#exporting").css("display","none");
            if(json && json.exportFileDTOList) {
                if(json.exportFileDTOList.length > 1) {
                    showDownLoadUI(json);

                } else {
                    window.open("download.do?method=downloadExportFile&exportFileName=员工业绩统计.xls&exportFileId="+ json.exportFileDTOList[0].idStr);
                }
            }
        });







        /*
        clearInitValue();
        var param = $("#itemStatisticsForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            if(!G.Lang.isEmpty(data[val.name])){
                data[val.name] = data[val.name]+","+val.value;
            }else{
                data[val.name] = val.value;
            }
        });
        data.vehicleConstructionPermission = App.Permission.Version.MemberStoredValue;
        data.memberStoredValuePermission = App.Permission.Version.MemberStoredValue;
        data.totalExportNum = $("#recordNum").text();
        data.statisticsInfo = $("#statisticsInfo").text();
        var url = "export.do?method=exportCustomerTransaction";
        bcgogoAjaxQuery.setUrlData(url, data);
        bcgogoAjaxQuery.ajaxQuery(function (json) {
            $("#export").removeAttr("disabled");
            $("#exporting").css("display","none");
            if(json && json.exportFileDTOList) {
                if(json.exportFileDTOList.length > 1) {
                    showDownLoadUI(json);

                } else {
                    window.open("download.do?method=downloadExportFile&exportFileName=客户交易统计.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                }
            }

        });*/
    })





});

function printPageContent(templateId){
     var data="";
    data+="&startYear="+$("#startYear").val();
    data+="&startMonth="+$("#startMonth").val();
    data+="&endYear="+$("#endYear").val();
    data+="&endMonth="+$("#endMonth").val();
    data+="&startPageNo=" + $("#currentPagedynamicalAssistantDepartStat").val();
    data+="&maxRows=15";
    data+="&achievementStatTypeStr="+$("#achievementStatType").val();
    data+="&assistantOrDepartmentIdStr="+$("#assistantOrDepartmentIdStr").val();
    data+="&achievementOrderTypeStr="+$("#achievementOrderTypeStr").val();
    data+="&achievementCalculateWayStr="+$("#achievementCalculateWayStr").val();
    data+="&serviceIdStr="+$("#serviceIdStr").val();

    window.showModalDialog("assistantStat.do?method=printAssistantStat"+data+"&templateId=" + templateId + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
    return;
}

function initAssistantStat(data) {
  $("#assistantStatTable colgroup,#assistantStatTable tbody").remove();
  if (APP_BCGOGO.Permission.Version.VehicleConstruction || APP_BCGOGO.Permission.Version.MemberStoredValue) {

    var tr = '<col width="2">' +
        '<col width="50">' +
        '<col width="50">';
    if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
      tr += '<col width="60">';
      tr += '<col width="70">';
      tr += '<col width="65">';
      tr += '<col width="75">';
      tr += '<col />';

    }
    if (APP_BCGOGO.Permission.Version.MemberStoredValue) {
      tr += '<col width="80">';
      tr += '<col />';
    }
    if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
      tr += '<col width="40">';
      tr += '<col width="75">';
      tr += '<col />';
    }

    tr += '<col width="80">' +
        '<col />' +
        '<col width="80">' +
        '<col />' +
        '<col width="3">';


    tr += '<tr class="tr_title">'
        + '<td style="background:none;"><img src="images/left_yuan.png" width="2px" height="49px;"/></td>'
        + '<td width="50px;"><label class="bumen" style="line-height:49px;">员工</label></td>'
        + '<td width="50px;"><label class="bumen" style="line-height:49px;">所属部门</label></td>';

    if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
      tr += '<td colspan="5" width="320px;">'
      +'<div class="car_shia bumen"><h1>车辆施工</h1><span><label style="width:60px;">标准工时</label><label>标准工时费</label><label style="width:55px;">实际工时</label><label style="width:70px">金额</label><label>提成 </label></span></div>'
      + '</td>';
    }
    if (APP_BCGOGO.Permission.Version.MemberStoredValue) {
      tr += '<td colspan="2" width="132px;"><div class="bumen"><h1 >会员卡</h1><span><label style="width:65px;" class="sale_mon">销售额</label><label style="width:45px;">提成</label></span></div></td>';
    }

    if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
      tr += '<td colspan="3" width="160px;"><div class="bumen"><h1 >洗车美容</h1><span><label>次数</label><label class="sale_mon">销售额</label><label>提成</label></span></div></td>';
    }


    tr += '<td colspan="2" width="132px;"> <div class="bumen"><h1 >商品销售</h1><span><label class="sale_mon">销售额</label><label>提成</label></span></div></td>'
       +'<td colspan="2" width="132px;"><div><h1 style="line-height:22px;">合计</h1><span><label class="sale_mon">销售额</label><label>提成</label></span></div></td>'
    +'<td style="background:none;"><img src="images/right_con.png" width="3px" height="49px;"/></td>'
        +'</tr>';


    $("#assistantStatTable").append($(tr));


    if (data == null || data == "" || data[0].assistantAchievementStatDTOList.length == 0) {
      $("#printButton").css("display", "none");
      $("#exportButton").css("display", "none");
      return;
    } else {
      $("#printButton").css("display", "block");
      $("#exportButton").css("display", "block");
    }

    var str = '<tr class="space"><td colspan="18"></td></tr>';
    $("#assistantStatTable").append(str);

    var startTime = data[0].startTime;
    var endTime = data[0].endTime;


    var standardHoursTotal = 0,
        standardServiceTotal = 0,
        actualHoursTotal = 0,
        actualServiceTotal = 0,
        serviceAchievementTotal = 0,
        saleTotal = 0,
        saleAchievementTotal = 0,
        washTotal = 0,
        washTimesTotal = 0,
        washAchievementTotal = 0,
        memberTotal = 0,
        memberAchievementTotal = 0,
        statSumTotal = 0,
        achievementSumTotal = 0;
    $.each(data[0].assistantAchievementStatDTOList, function (index, assistantStatDTO) {
      var departmentId = (!assistantStatDTO.departmentIdStr ? "--" : assistantStatDTO.departmentIdStr),
          departmentName = assistantStatDTO.departmentName ? assistantStatDTO.departmentName : "",
          assistantId = assistantStatDTO.assistantIdStr ? assistantStatDTO.assistantIdStr : "",

          standardHours = assistantStatDTO.standardHours ? assistantStatDTO.standardHours : 0,
          standardService = assistantStatDTO.standardService ? assistantStatDTO.standardService : 0,
          actualHours = assistantStatDTO.actualHours ? assistantStatDTO.actualHours : 0,
          actualService = assistantStatDTO.actualService ? assistantStatDTO.actualService : 0,
          serviceAchievement = assistantStatDTO.serviceAchievement ? assistantStatDTO.serviceAchievement : 0,

          sale = assistantStatDTO.sale ? assistantStatDTO.sale : 0,
          saleAchievement = assistantStatDTO.saleAchievement ? assistantStatDTO.saleAchievement : 0,

          wash = assistantStatDTO.wash ? assistantStatDTO.wash : 0,
          washTimes = assistantStatDTO.washTimes ? assistantStatDTO.washTimes : 0,
          washAchievement = assistantStatDTO.washAchievement ? assistantStatDTO.washAchievement : 0,

          member = assistantStatDTO.member ? assistantStatDTO.member : 0,
          memberAchievement = assistantStatDTO.memberAchievement ? assistantStatDTO.memberAchievement : 0,
          statSum = assistantStatDTO.statSum ? assistantStatDTO.statSum : 0,
          achievementSum = assistantStatDTO.achievementSum ? assistantStatDTO.achievementSum : 0,

          achievementStatType = assistantStatDTO.achievementStatType ? assistantStatDTO.achievementStatType : "",

          assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";

      var tr = '<tr class="titBody_Bg">';
      tr += '<td></td>';

      tr += '<td title="' + assistantName + '">' + assistantName + '</td>';
      tr += '<td title="' + departmentName + '">' + departmentName + '</td>';

      if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
        tr += '<td style="width:60px;" title="' + standardHours + '">' + standardHours + '</td>';
        tr += '<td style="width:70px;" title="' + standardService + '">' + standardService + '</td>';
        tr += '<td style="width:65px;" title="' + actualHours + '">' + actualHours + '</td>';
        tr += '<td style="width:75px;" title="' + actualService + '">' + actualService + '</td>';
        tr += '<td><a class="blue_color" href="#" onclick="openServiceRecord(\'' + achievementStatType + '\',\'' + assistantId + '\',\'' + startTime + '\',\'' + endTime + '\');">' + serviceAchievement + '</a></td>';
      }
      if (APP_BCGOGO.Permission.Version.MemberStoredValue) {
        tr += '<td style="width:80px;" title="' + member + '">' + member + '</td>';
        tr += '<td title="' + memberAchievement + '"><a class="blue_color" href="#" onclick="openMemberRecord(\'' + achievementStatType + '\',\'' + assistantId + '\',\'' + startTime + '\',\'' + endTime + '\');">' + memberAchievement + '</a></td>';
      }
      if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
        tr += '<td style="width:40px;" title="' + washTimes + '">' + washTimes + '</td>';
        tr += '<td style="width:75px;" title="' + wash + '">' + wash + '</td>';
        tr += '<td title="' + washAchievement + '"><a class="blue_color" href="#" onclick="openWashRecord(\'' + achievementStatType + '\',\'' + assistantId + '\',\'' + startTime + '\',\'' + endTime + '\');">' + washAchievement + '</a></td>';
      }
      tr += '<td style="width:80px;" title="' + sale + '">' + sale + '</td>';
      tr += '<td title="' + saleAchievement + '"><a class="blue_color" href="#" onclick="openProductRecord(\'' + achievementStatType + '\',\'' + assistantId + '\',\'' + startTime + '\',\'' + endTime + '\');">' + saleAchievement + '</a></td>';

      tr += '<td style="width:80px;" title="' + statSum + '">' + statSum + '</td>';
      tr += '<td title="' + achievementSum + '">' + achievementSum + '</td>';

      tr += '<td></td>';

      tr += '</tr>';

      tr += '<tr class="titBottom_Bg"><td colspan="18"></td></tr>';
      $("#assistantStatTable").append(tr);
    });

    if (data[0].totalAssistantStatDTO != null) {
      var assistantStatDTO = data[0].totalAssistantStatDTO;
      var standardHours = assistantStatDTO.standardHours ? assistantStatDTO.standardHours : 0,
          standardService = assistantStatDTO.standardService ? assistantStatDTO.standardService : 0,
          actualHours = assistantStatDTO.actualHours ? assistantStatDTO.actualHours : 0,
          actualService = assistantStatDTO.actualService ? assistantStatDTO.actualService : 0,
          serviceAchievement = assistantStatDTO.serviceAchievement ? assistantStatDTO.serviceAchievement : 0,

          sale = assistantStatDTO.sale ? assistantStatDTO.sale : 0,
          saleAchievement = assistantStatDTO.saleAchievement ? assistantStatDTO.saleAchievement : 0,

          wash = assistantStatDTO.wash ? assistantStatDTO.wash : 0,
          washTimes = assistantStatDTO.washTimes ? assistantStatDTO.washTimes : 0,
          washAchievement = assistantStatDTO.washAchievement ? assistantStatDTO.washAchievement : 0,

          member = assistantStatDTO.member ? assistantStatDTO.member : 0,
          memberAchievement = assistantStatDTO.memberAchievement ? assistantStatDTO.memberAchievement : 0,
          statSum = assistantStatDTO.statSum ? assistantStatDTO.statSum : 0,
          achievementSum = assistantStatDTO.achievementSum ? assistantStatDTO.achievementSum : 0,

          achievementStatType = assistantStatDTO.achievementStatType ? assistantStatDTO.achievementStatType : "",

          assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";

      var tr = '<tr class="titBody_Bg">';
      tr += '<td></td>';
      tr += '<td style="text-align:right" colspan="2">合计：</td> ';

      if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
        tr += '<td title="' + standardHours + '">' + standardHours + '</td>';
        tr += '<td title="' + standardService + '">' + standardService + '</td>';
        tr += '<td title="' + actualHours + '">' + actualHours + '</td>';
        tr += '<td title="' + actualService + '">' + actualService + '</td>';
        tr += '<td title="' + serviceAchievement + '">' + serviceAchievement + '</td>';
      }
      if (APP_BCGOGO.Permission.Version.MemberStoredValue) {
        tr += '<td title="' + member + '">' + member + '</td>';
        tr += '<td title="' + memberAchievement + '">' + memberAchievement + '</td>';
      }
      if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
        tr += '<td title="' + washTimes + '">' + washTimes + '</td>';
        tr += '<td title="' + wash + '">' + wash + '</td>';
        tr += '<td title="' + washAchievement + '">' + washAchievement + '</td>';
      }
      tr += '<td title="' + sale + '">' + sale + '</td>';
      tr += '<td title="' + saleAchievement + '">' + saleAchievement + '</td>';

      tr += '<td title="' + statSum + '">' + statSum + '</td>';
      tr += '<td title="' + achievementSum + '">' + achievementSum + '</td>';

      tr += '<td></td>';

      tr += '</tr>';
      $("#assistantStatTable").append(tr);
    }
  } else {
    var tr = '<colgroup>' +
        '<col />' +
        '<col />' +
        '<col />' +
        '<col width="175">' +
        '</colgroup>';


    tr += '<tr class="titleBg">'
        + '  <td style="padding-left:10px;">员工</td>'
        + '  <td>所属部门</td>'
        + '  <td>销售额</td>'
        + '  <td>提成</td></tr>';


    $("#assistantStatTable").append($(tr));


    if (data == null || data == "" || data[0].assistantAchievementStatDTOList.length == 0) {
      $("#printButton").css("display", "none");
        $("#exportButton").css("display", "none");
      return;
    } else {
      $("#printButton").css("display", "block");
        $("#exportButton").css("display", "block");
    }

    var str = '<tr class="space"><td colspan="18"></td></tr>';
    $("#assistantStatTable").append(str);

    var startTime = data[0].startTime;
    var endTime = data[0].endTime;


    var standardHoursTotal = 0,
        standardServiceTotal = 0,
        actualHoursTotal = 0,
        actualServiceTotal = 0,
        serviceAchievementTotal = 0,
        saleTotal = 0,
        saleAchievementTotal = 0,
        washTotal = 0,
        washTimesTotal = 0,
        washAchievementTotal = 0,
        memberTotal = 0,
        memberAchievementTotal = 0,
        statSumTotal = 0,
        achievementSumTotal = 0;
    $.each(data[0].assistantAchievementStatDTOList, function (index, assistantStatDTO) {
      var departmentId = (!assistantStatDTO.departmentIdStr ? "--" : assistantStatDTO.departmentIdStr),
          departmentName = assistantStatDTO.departmentName ? assistantStatDTO.departmentName : "",
          assistantId = assistantStatDTO.assistantIdStr ? assistantStatDTO.assistantIdStr : "",

          sale = assistantStatDTO.sale ? assistantStatDTO.sale : 0,
          saleAchievement = assistantStatDTO.saleAchievement ? assistantStatDTO.saleAchievement : 0,

          achievementStatType = assistantStatDTO.achievementStatType ? assistantStatDTO.achievementStatType : "",

          assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";

      var tr = '<tr class="titBody_Bg">';

      tr += '<td  style="text-align:left" title="' + assistantName + '">' + assistantName + '</td>';
      tr += '<td  style="text-align:left" title="' + departmentName + '">' + departmentName + '</td>';
      tr += '<td  style="text-align:left" title="' + sale + '">' + sale + '</td>';
      tr += '<td style="text-align:left" title="' + saleAchievement + '"><a class="blue_color" href="#" onclick="openProductRecord(\'' + achievementStatType + '\',\'' + assistantId + '\',\'' + startTime + '\',\'' + endTime + '\');">' + saleAchievement + '</a></td>';

      tr += '</tr>';

      tr += '<tr class="titBottom_Bg"><td colspan="4"></td></tr>';
      $("#assistantStatTable").append(tr);
    });

    if (data[0].totalAssistantStatDTO != null) {
      var assistantStatDTO = data[0].totalAssistantStatDTO;
      var sale = assistantStatDTO.sale ? assistantStatDTO.sale : 0,
          saleAchievement = assistantStatDTO.saleAchievement ? assistantStatDTO.saleAchievement : 0;

      var tr = '<tr class="titBody_Bg">';
      tr += '<td style="text-align:right" colspan="2">合计：</td> ';

      tr += '<td  style="text-align:left" title="' + sale + '">' + sale + '</td>';
      tr += '<td  style="text-align:left" title="' + saleAchievement + '">' + saleAchievement + '</td>';
      tr += '</tr>';
      $("#assistantStatTable").append(tr);
    }
  }
}


function initAssistantDepartStat(data) {
  $("#departmentStatTable colgroup,#departmentStatTable tbody").remove();

   if (APP_BCGOGO.Permission.Version.VehicleConstruction || APP_BCGOGO.Permission.Version.MemberStoredValue) {

    var tr = '<col width="2">' +
        '<col width="100">';
    if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
      tr += '<col width="60">';
      tr += '<col width="70">';
      tr += '<col width="65">';
      tr += '<col width="75">';
      tr += '<col />';

    }
    if (APP_BCGOGO.Permission.Version.MemberStoredValue) {
      tr += '<col width="80">';
      tr += '<col />';
    }
    if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
      tr += '<col width="40">';
      tr += '<col width="75">';
      tr += '<col />';
    }

    tr += '<col width="80">' +
        '<col />' +
        '<col width="80">' +
        '<col />' +
        '<col width="3">';


    tr += '<tr class="tr_title">'
        + '<td style="background:none;"><img src="images/left_yuan.png" width="2px" height="49px;"/></td>'
        + '<td width="100px;"><label class="bumen" style="line-height:49px;">所属部门</label></td>';

    if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
      tr += '<td colspan="5" width="320px;">'
      +'<div class="car_shia bumen"><h1>车辆施工</h1><span><label style="width:60px;">标准工时</label><label>标准工时费</label><label style="width:55px;">实际工时</label><label style="width:70px">金额</label><label>提成 </label></span></div>'
      + '</td>';
    }
    if (APP_BCGOGO.Permission.Version.MemberStoredValue) {
      tr += '<td colspan="2" width="132px;"><div class="bumen"><h1 >会员卡</h1><span><label style="width:65px;" class="sale_mon">销售额</label><label style="width:45px;">提成</label></span></div></td>';
    }

    if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
      tr += '<td colspan="3" width="160px;"><div class="bumen"><h1 >洗车美容</h1><span><label>次数</label><label class="sale_mon">销售额</label><label>提成</label></span></div></td>';
    }


    tr += '<td colspan="2" width="132px;"> <div class="bumen"><h1 >商品销售</h1><span><label class="sale_mon">销售额</label><label>提成</label></span></div></td>'
       +'<td colspan="2" width="132px;"><div><h1 style="line-height:22px;">合计</h1><span><label class="sale_mon">销售额</label><label>提成</label></span></div></td>'
    +'<td style="background:none;"><img src="images/right_con.png" width="3px" height="49px;"/></td>'
        +'</tr>';

    $("#departmentStatTable").append($(tr));


    if (data == null || data == "" || data[0].assistantAchievementStatDTOList.length == 0) {
      $("#printButton").css("display", "none");
        $("#exportButton").css("display", "none");
      return;
    } else {
      $("#printButton").css("display", "block");
        $("#exportButton").css("display", "block");
    }

    var str = '<tr class="space"><td colspan="18"></td></tr>';
    $("#departmentStatTable").append(str);

    var startTime = data[0].startTime;
    var endTime = data[0].endTime;


    var standardHoursTotal = 0,
        standardServiceTotal = 0,
        actualHoursTotal = 0,
        actualServiceTotal = 0,
        serviceAchievementTotal = 0,
        saleTotal = 0,
        saleAchievementTotal = 0,
        washTotal = 0,
        washTimesTotal = 0,
        washAchievementTotal = 0,
        memberTotal = 0,
        memberAchievementTotal = 0,
        statSumTotal = 0,
        achievementSumTotal = 0;
    $.each(data[0].assistantAchievementStatDTOList, function (index, assistantStatDTO) {
      var departmentId = (!assistantStatDTO.departmentIdStr ? "--" : assistantStatDTO.departmentIdStr),
          departmentName = assistantStatDTO.departmentName ? assistantStatDTO.departmentName : "",
          assistantId = assistantStatDTO.assistantIdStr ? assistantStatDTO.assistantIdStr : "",

          standardHours = assistantStatDTO.standardHours ? assistantStatDTO.standardHours : 0,
          standardService = assistantStatDTO.standardService ? assistantStatDTO.standardService : 0,
          actualHours = assistantStatDTO.actualHours ? assistantStatDTO.actualHours : 0,
          actualService = assistantStatDTO.actualService ? assistantStatDTO.actualService : 0,
          serviceAchievement = assistantStatDTO.serviceAchievement ? assistantStatDTO.serviceAchievement : 0,

          sale = assistantStatDTO.sale ? assistantStatDTO.sale : 0,
          saleAchievement = assistantStatDTO.saleAchievement ? assistantStatDTO.saleAchievement : 0,

          wash = assistantStatDTO.wash ? assistantStatDTO.wash : 0,
          washTimes = assistantStatDTO.washTimes ? assistantStatDTO.washTimes : 0,
          washAchievement = assistantStatDTO.washAchievement ? assistantStatDTO.washAchievement : 0,

          member = assistantStatDTO.member ? assistantStatDTO.member : 0,
          memberAchievement = assistantStatDTO.memberAchievement ? assistantStatDTO.memberAchievement : 0,
          statSum = assistantStatDTO.statSum ? assistantStatDTO.statSum : 0,
          achievementSum = assistantStatDTO.achievementSum ? assistantStatDTO.achievementSum : 0,

          achievementStatType = assistantStatDTO.achievementStatType ? assistantStatDTO.achievementStatType : "",

          assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";

      var tr = '<tr class="titBody_Bg">';
      tr += '<td></td>';
      tr += '<td style="width:100px;" title="' + departmentName + '">' + departmentName + '</td>';

      if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
        tr += '<td style="width:60px;" title="' + standardHours + '">' + standardHours + '</td>';
        tr += '<td style="width:70px;" title="' + standardService + '">' + standardService + '</td>';
        tr += '<td style="width:65px;" title="' + actualHours + '">' + actualHours + '</td>';
        tr += '<td style="width:75px;" title="' + actualService + '">' + actualService + '</td>';
        tr += '<td><a class="blue_color" href="#" onclick="openServiceRecord(\'' + achievementStatType + '\',\'' + departmentId + '\',\'' + startTime + '\',\'' + endTime + '\');">' + serviceAchievement + '</a></td>';
      }
      if (APP_BCGOGO.Permission.Version.MemberStoredValue) {
        tr += '<td style="width:80px;" title="' + member + '">' + member + '</td>';
        tr += '<td title="' + memberAchievement + '"><a class="blue_color" href="#" onclick="openMemberRecord(\'' + achievementStatType + '\',\'' + departmentId + '\',\'' + startTime + '\',\'' + endTime + '\');">' + memberAchievement + '</a></td>';
      }
      if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
        tr += '<td style="width:40px;" title="' + washTimes + '">' + washTimes + '</td>';
        tr += '<td style="width:75px;" title="' + wash + '">' + wash + '</td>';
        tr += '<td title="' + washAchievement + '"><a class="blue_color" href="#" onclick="openWashRecord(\'' + achievementStatType + '\',\'' + departmentId + '\',\'' + startTime + '\',\'' + endTime + '\');">' + washAchievement + '</a></td>';
      }
      tr += '<td style="width:80px;" title="' + sale + '">' + sale + '</td>';
      tr += '<td title="' + saleAchievement + '"><a class="blue_color" href="#" onclick="openProductRecord(\'' + achievementStatType + '\',\'' + departmentId + '\',\'' + startTime + '\',\'' + endTime + '\');">' + saleAchievement + '</a></td>';

      tr += '<td style="width:80px;" title="' + statSum + '">' + statSum + '</td>';
      tr += '<td title="' + achievementSum + '">' + achievementSum + '</td>';

      tr += '<td></td>';

      tr += '</tr>';

      tr += '<tr class="titBottom_Bg"><td colspan="17"></td></tr>';
      $("#departmentStatTable").append(tr);
    });

    if (data[0].totalAssistantStatDTO != null) {
      var assistantStatDTO = data[0].totalAssistantStatDTO;
      var standardHours = assistantStatDTO.standardHours ? assistantStatDTO.standardHours : 0,
          standardService = assistantStatDTO.standardService ? assistantStatDTO.standardService : 0,
          actualHours = assistantStatDTO.actualHours ? assistantStatDTO.actualHours : 0,
          actualService = assistantStatDTO.actualService ? assistantStatDTO.actualService : 0,
          serviceAchievement = assistantStatDTO.serviceAchievement ? assistantStatDTO.serviceAchievement : 0,

          sale = assistantStatDTO.sale ? assistantStatDTO.sale : 0,
          saleAchievement = assistantStatDTO.saleAchievement ? assistantStatDTO.saleAchievement : 0,

          wash = assistantStatDTO.wash ? assistantStatDTO.wash : 0,
          washTimes = assistantStatDTO.washTimes ? assistantStatDTO.washTimes : 0,
          washAchievement = assistantStatDTO.washAchievement ? assistantStatDTO.washAchievement : 0,

          member = assistantStatDTO.member ? assistantStatDTO.member : 0,
          memberAchievement = assistantStatDTO.memberAchievement ? assistantStatDTO.memberAchievement : 0,
          statSum = assistantStatDTO.statSum ? assistantStatDTO.statSum : 0,
          achievementSum = assistantStatDTO.achievementSum ? assistantStatDTO.achievementSum : 0,

          achievementStatType = assistantStatDTO.achievementStatType ? assistantStatDTO.achievementStatType : "",

          assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";

      var tr = '<tr class="titBody_Bg">';
      tr += '<td></td>';
      tr += '<td style="text-align:right" colspan="1">合计：</td> ';

      if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
        tr += '<td title="' + standardHours + '">' + standardHours + '</td>';
        tr += '<td title="' + standardService + '">' + standardService + '</td>';
        tr += '<td title="' + actualHours + '">' + actualHours + '</td>';
        tr += '<td title="' + actualService + '">' + actualService + '</td>';
        tr += '<td title="' + serviceAchievement + '">' + serviceAchievement + '</td>';
      }
      if (APP_BCGOGO.Permission.Version.MemberStoredValue) {
        tr += '<td title="' + member + '">' + member + '</td>';
        tr += '<td title="' + memberAchievement + '">' + memberAchievement + '</td>';
      }
      if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
        tr += '<td title="' + washTimes + '">' + washTimes + '</td>';
        tr += '<td title="' + wash + '">' + wash + '</td>';
        tr += '<td title="' + washAchievement + '">' + washAchievement + '</td>';
      }
      tr += '<td title="' + sale + '">' + sale + '</td>';
      tr += '<td title="' + saleAchievement + '">' + saleAchievement + '</td>';

      tr += '<td title="' + statSum + '">' + statSum + '</td>';
      tr += '<td title="' + achievementSum + '">' + achievementSum + '</td>';

      tr += '<td></td>';

      tr += '</tr>';
      $("#departmentStatTable").append(tr);
    }
  } else {
    var tr = '<colgroup>' +
        '<col />' +
        '<col />' +
        '<col width="175">' +
        '</colgroup>';


    tr += '<tr class="titleBg">'
        + '  <td style="padding-left:10px;">部门</td>'
        + '  <td>销售额</td>'
        + '  <td>提成</td></tr>';


    $("#departmentStatTable").append($(tr));


    if (data == null || data == "" || data[0].assistantAchievementStatDTOList.length == 0) {
      $("#printButton").css("display", "none");
        $("#exportButton").css("display", "none");
      return;
    } else {
      $("#printButton").css("display", "block");
        $("#exportButton").css("display", "block");
    }

    var str = '<tr class="space"><td colspan="4"></td></tr>';
    $("#departmentStatTable").append(str);

    var startTime = data[0].startTime;
    var endTime = data[0].endTime;


    var standardHoursTotal = 0,
        standardServiceTotal = 0,
        actualHoursTotal = 0,
        actualServiceTotal = 0,
        serviceAchievementTotal = 0,
        saleTotal = 0,
        saleAchievementTotal = 0,
        washTotal = 0,
        washTimesTotal = 0,
        washAchievementTotal = 0,
        memberTotal = 0,
        memberAchievementTotal = 0,
        statSumTotal = 0,
        achievementSumTotal = 0;
    $.each(data[0].assistantAchievementStatDTOList, function (index, assistantStatDTO) {
      var departmentId = (!assistantStatDTO.departmentIdStr ? "--" : assistantStatDTO.departmentIdStr),
          departmentName = assistantStatDTO.departmentName ? assistantStatDTO.departmentName : "",
          assistantId = assistantStatDTO.assistantIdStr ? assistantStatDTO.assistantIdStr : "",

          sale = assistantStatDTO.sale ? assistantStatDTO.sale : 0,
          saleAchievement = assistantStatDTO.saleAchievement ? assistantStatDTO.saleAchievement : 0,

          achievementStatType = assistantStatDTO.achievementStatType ? assistantStatDTO.achievementStatType : "",

          assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";

      var tr = '<tr class="titBody_Bg">';

      tr += '<td  style="text-align:left" title="' + departmentName + '">' + departmentName + '</td>';
      tr += '<td  style="text-align:left" title="' + sale + '">' + sale + '</td>';
      tr += '<td  style="text-align:left" title="' + saleAchievement + '"><a class="blue_color" href="#" onclick="openProductRecord(\'' + achievementStatType + '\',\'' + departmentId + '\',\'' + startTime + '\',\'' + endTime + '\');">' + saleAchievement + '</a></td>';

      tr += '</tr>';

      tr += '<tr class="titBottom_Bg"><td colspan="4"></td></tr>';
      $("#departmentStatTable").append(tr);
    });

    if (data[0].totalAssistantStatDTO != null) {
      var assistantStatDTO = data[0].totalAssistantStatDTO;
      var sale = assistantStatDTO.sale ? assistantStatDTO.sale : 0,
          saleAchievement = assistantStatDTO.saleAchievement ? assistantStatDTO.saleAchievement : 0;

      var tr = '<tr class="titBody_Bg">';
      tr += '<td style="text-align:right" colspan="1">合计：</td> ';

      tr += '<td  style="text-align:left" title="' + sale + '">' + sale + '</td>';
      tr += '<td  style="text-align:left" title="' + saleAchievement + '">' + saleAchievement + '</td>';
      tr += '</tr>';
      $("#departmentStatTable").append(tr);
    }
  }
}

function queryDate() {
  if ($("#startYear").val() >= $("#endYear").val()) {
    var endYear = $("#endYear").val();
    $("#endYear").val($("#startYear").val());
    $("#startYear").val(endYear);
  }

  var ajaxDate = {
    startYear: $("#startYear").val(),
    startMonth: $("#startMonth").val(),
    endYear: $("#endYear").val(),
    endMonth: $("#endMonth").val(),
    startPageNo: $("#startPageNoHiddenHidden").val(),
    maxRows: 15,
    achievementStatTypeStr: $("#achievementStatType").val(),
    assistantOrDepartmentIdStr: $("#assistantOrDepartmentIdStr").val(),
    achievementOrderTypeStr: $("#achievementOrderTypeStr").val(),
    achievementCalculateWayStr: $("#achievementCalculateWayStr").val(),
    serviceIdStr: $("#serviceIdStr").val()
  }

  var url = "assistantStat.do?method=getAssistantStatByPage";

  bcgogoAjaxQuery.setUrlData(url, ajaxDate);
  bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {


    if (jsonStr && jsonStr != "") {

      initAchievementStatTable(jsonStr);

      initPages(jsonStr, 'dynamicalAssistantDepartStat', url, '', 'initAchievementStatTable', '', '', ajaxDate, '');
    }else{
      $("#noDataSpan").css("display","block");
    }
  });

}


function openServiceRecord(achievementStatType, id, startTime, endTime, achievementOrderTypeStr, achievementCalculateWayStr, serviceIdStr) {
  window.location.href = encodeURI("assistantStat.do?method=redirectServiceRecord&achievementStatTypeStr=" + achievementStatType + "&assistantOrDepartmentIdStr=" + id + "&startTimeStr=" + startTime + "&endTimeStr=" + endTime + "&startPageNo=" + $("#currentPagedynamicalAssistantDepartStat").val()
      + "&achievementOrderTypeStr=" + achievementOrderTypeStr + "&achievementCalculateWayStr=" + achievementCalculateWayStr + "&serviceIdStr=" + serviceIdStr);
}

function openWashRecord(achievementStatType, id, startTime, endTime, achievementOrderTypeStr, achievementCalculateWayStr, serviceIdStr) {
  window.location.href = encodeURI("assistantStat.do?method=redirectWashRecord" + "&achievementStatTypeStr=" + achievementStatType + "&assistantOrDepartmentIdStr=" + id + "&startTimeStr=" + startTime + "&endTimeStr=" + endTime + "&startPageNo=" + $("#currentPagedynamicalAssistantDepartStat").val()
      + "&achievementOrderTypeStr=" + achievementOrderTypeStr + "&achievementCalculateWayStr=" + achievementCalculateWayStr + "&serviceIdStr=" + serviceIdStr);

}

function openProductRecord(achievementStatType, id, startTime, endTime, achievementOrderTypeStr, achievementCalculateWayStr, serviceIdStr) {
  window.location.href = encodeURI("assistantStat.do?method=redirectProductRecord" + "&achievementStatTypeStr=" + achievementStatType + "&assistantOrDepartmentIdStr=" + id + "&startTimeStr=" + startTime + "&endTimeStr=" + endTime + "&startPageNo=" + $("#currentPagedynamicalAssistantDepartStat").val()
      + "&achievementOrderTypeStr=" + achievementOrderTypeStr + "&achievementCalculateWayStr=" + achievementCalculateWayStr + "&serviceIdStr=" + serviceIdStr);

}

function openMemberRecord(achievementStatType, id, startTime, endTime, achievementOrderTypeStr, achievementCalculateWayStr, serviceIdStr) {
  window.location.href = encodeURI("assistantStat.do?method=redirectMemberRecord" + "&achievementStatTypeStr=" + achievementStatType + "&assistantOrDepartmentIdStr=" + id + "&startTimeStr=" + startTime + "&endTimeStr=" + endTime + "&startPageNo=" + $("#currentPagedynamicalAssistantDepartStat").val()
      + "&achievementOrderTypeStr=" + achievementOrderTypeStr + "&achievementCalculateWayStr=" + achievementCalculateWayStr + "&serviceIdStr=" + serviceIdStr);
}

function openBusinessAccountRecord(achievementStatType, id, startTime, endTime, achievementOrderTypeStr, achievementCalculateWayStr, serviceIdStr) {
  window.location.href = encodeURI("assistantStat.do?method=redirectBusinessAccountRecord" + "&achievementStatTypeStr=" + achievementStatType + "&assistantOrDepartmentIdStr=" + id + "&startTimeStr=" + startTime + "&endTimeStr=" + endTime + "&startPageNo=" + $("#currentPagedynamicalAssistantDepartStat").val()
      + "&achievementOrderTypeStr=" + achievementOrderTypeStr + "&achievementCalculateWayStr=" + achievementCalculateWayStr + "&serviceIdStr=" + serviceIdStr);
}