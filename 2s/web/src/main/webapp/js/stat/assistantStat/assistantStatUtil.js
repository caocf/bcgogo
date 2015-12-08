var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;


function map() {
  var struct = function (key, value) {
    this.key = key;
    this.value = value;
  }

  var put = function (key, value) {
    for (var i = 0; i < this.arr.length; i++) {
      if (this.arr[i].key === key) {
        this.arr[i].value = value;
        return;
      }
    }
    this.arr[this.arr.length] = new struct(key, value);
  }

  var get = function (key) {
    for (var i = 0; i < this.arr.length; i++) {
      if (this.arr[i].key === key) {
        return this.arr[i].value;
      }
    }
    return null;
  }

  var remove = function (key) {
    var v;
    for (var i = 0; i < this.arr.length; i++) {
      v = this.arr.pop();
      if (v.key === key) {
        break;
      }
      this.arr.unshift(v);
    }
  }

  var size = function () {
    return this.arr.length;
  }

  var isEmpty = function () {
    return this.arr.length <= 0;
  }

  var clearMap = function () {
    this.arr = [];
  }
  this.arr = new Array();
  this.get = get;
  this.put = put;
  this.remove = remove;
  this.size = size;
  this.isEmpty = isEmpty;
  this.clearMap = clearMap;
}
var jsonStrMap = new map();


$(document).ready(function () {

});


function getColBySearchCondition(searchCondition) {

  var firstStr = "<colgroup>";

  if (searchCondition.achievementStatType == "ASSISTANT") {
    firstStr += '<col width="2px;" style="width: 2px;">' + '<col width="40">' + '<col width="40">';
  } else if (searchCondition.achievementStatType == "DEPARTMENT") {
    firstStr += '<col width="2"><col width="45">';
  }

  var repairServiceStr = "";
  var memberStr = "";
  var washBeautyStr = "";
  var salesStr = "";
  var businessAccountStr = "";
  var totalStr = "";

  if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
    repairServiceStr += '<col width="50px">';
    repairServiceStr += '<col width="60px">';
    repairServiceStr += '<col width="50px">';
    repairServiceStr += '<col width="50px">';
    repairServiceStr += '<col width="40px">';
  }
  if (APP_BCGOGO.Permission.Version.MemberStoredValue) {
    memberStr += '<col width="60px">';
    memberStr += '<col width="40px">';
  }

  if (APP_BCGOGO.Permission.Version.WASH_BEAUTY) {
    washBeautyStr += '<col width="45px">';
    washBeautyStr += '<col width="50px">';
    washBeautyStr += '<col />';
  }
  salesStr += '<col width="50px"><col width="50px"><col width="50px">' + '<col width="50px">';
  businessAccountStr += '<col width="80px">';
  totalStr += '<col width="50px">' + '<col width="50px">' + '<col width="3"></colgroup>';

  if (searchCondition.achievementOrderTypeStr == "ALL" || stringUtil.isEmpty(searchCondition.achievementOrderTypeStr)) {
    return firstStr + repairServiceStr + memberStr + washBeautyStr + salesStr + businessAccountStr + totalStr;
  } else if (searchCondition.achievementOrderTypeStr == "WASH_BEAUTY") {
    return firstStr + washBeautyStr + totalStr;
  } else if (searchCondition.achievementOrderTypeStr == "REPAIR_SERVICE") {
    return firstStr + repairServiceStr + totalStr;

  } else if (searchCondition.achievementOrderTypeStr == "MEMBER") {
    return firstStr + memberStr + totalStr;
  }

}


function getTrBySearchCondition(searchCondition) {

  var firstStr = "";

  if (searchCondition.achievementStatType == "ASSISTANT") {
    firstStr = '<tr class="tr_title">'
        + '<td style="background:none;"><img src="images/left_yuan.png" width="2px" height="49px;"/></td>'
        + '<td><label class="bumen" style="line-height:49px;">员工</label></td>'
        + '<td><label class="bumen" style="line-height:49px;">部门</label></td>';
  } else if (searchCondition.achievementStatType == "DEPARTMENT") {
    firstStr = '<tr class="tr_title">'
        + '<td style="background:none;"><img src="images/left_yuan.png" width="2px" height="49px;"/></td>'
        + '<td><label class="bumen" style="line-height:49px;">部门</label></td>';
  }

  var repairServiceStr = "";
  var memberStr = "";
  var washBeautyStr = "";
  var salesStr = "";
  var businessAccountStr = "";
  var totalStr = "";

  if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
    repairServiceStr += '<td colspan="5">'
        + '<div class="car_shia bumen"><h1>车辆施工</h1><span><label>标准工时</label><label>标准工时费</label><label>实际工时</label><label>金额</label><label>提成</label></span></div>'
        + '</td>';
  }
  if (APP_BCGOGO.Permission.Version.MemberStoredValue) {
    memberStr += '<td colspan="2"><div class="bumen"><h1 >会员卡</h1><span>' +
        '<label class="sale_mon">销售额</label><label>提成</label></span></div></td>';
  }

  if (APP_BCGOGO.Permission.Version.WASH_BEAUTY) {
    washBeautyStr += '<td colspan="3"><div class="bumen"><h1>洗车美容</h1><span><label>次数</label>' +
        '<label class="sale_mon">销售额</label><label>提成</label></span></div></td>';
    ;
  }


  salesStr += '<td colspan="4"> <div class="bumen"><h1>商品销售</h1><span><label class="sale_mon">销售额</label><label>提成</label>' +
      '<label class="sale_mon">利润</label><label>利润提成</label></span>' +
      '</div>' +
      '</td>';

  businessAccountStr += '<td><div class="bumen"><h1>营业外记账</h1><span><label class="sale_mon">收入</label></span></div></td>';


  totalStr += '<td colspan="2"><div><h1 style="line-height:22px;">合计</h1><span>' +
      '<label class="sale_mon">销售额</label><label>提成</label></span></div></td>';
  totalStr += '<td style="background:none;"><img src="images/right_con.png" width="3px" height="49px;"/></td>'
      + '</tr>';

  if (searchCondition.achievementOrderTypeStr == "ALL" || stringUtil.isEmpty(searchCondition.achievementOrderTypeStr)) {
    return firstStr + repairServiceStr + memberStr + washBeautyStr + salesStr + businessAccountStr + totalStr;
  } else if (searchCondition.achievementOrderTypeStr == "WASH_BEAUTY") {
    return firstStr + washBeautyStr + totalStr;
  } else if (searchCondition.achievementOrderTypeStr == "REPAIR_SERVICE") {
    return firstStr + repairServiceStr + totalStr;

  } else if (searchCondition.achievementOrderTypeStr == "MEMBER") {
    return firstStr + memberStr + totalStr;
  }

}


function getTrSampleBySearchCondition(searchCondition) {

  var firstStr = "";
  if (searchCondition.achievementOrderTypeStr == "ALL") {
    $("#departmentStatTable").css("width", "120%");
    $("#scrollDiv").attr("style","overflow-x:scroll; width:981px;");
  } else {
    $("#departmentStatTable").css("width", "100%");
    $("#scrollDiv").attr("style","");
  }

  var secondTrSample ='<tr class="tr_bottom">';

  if (searchCondition.achievementStatType == "ASSISTANT") {
    firstStr = '<tr class="tr_title">'
        + '<td rowspan="2" class="bumen"><strong>员工</strong></td>'
        + '<td rowspan="2"><strong>部门</strong></td>';
  } else if (searchCondition.achievementStatType == "DEPARTMENT") {
    firstStr = '<tr class="tr_title">'
        + '<td rowspan="2"><strong>部门</strong></td>';
  }

  if (APP_BCGOGO.Permission.Version.VehicleConstruction && (searchCondition.achievementOrderTypeStr == "REPAIR_SERVICE" || searchCondition.achievementOrderTypeStr == "ALL")) {
    firstStr += '<td colspan="5"><strong>车辆施工</strong></td>';
    secondTrSample += '<td class="left">标准工时</td><td>工时金额</td><td>实际工时</td><td>收入</td><td class="right">提成</td>';
  }
  if (APP_BCGOGO.Permission.Version.MemberStoredValue && (searchCondition.achievementOrderTypeStr == "MEMBER" || searchCondition.achievementOrderTypeStr == "ALL")) {
    firstStr += '<td colspan="2"><strong>会员卡</strong></td>';
    secondTrSample += '<td class="left">收入</td><td class="right">提成</td>';
  }

  if (APP_BCGOGO.Permission.Stat.BusinessStat.Business.WashCar && (searchCondition.achievementOrderTypeStr == "WASH_BEAUTY" || searchCondition.achievementOrderTypeStr == "ALL")) {
    firstStr += '<td colspan="3"><strong>洗车</strong></td>';
    secondTrSample += '<td class="left">次数</td><td>收入</td><td class="right">提成</td>';
  }


  if (APP_BCGOGO.Permission.Txn.SaleManage.Sale && (searchCondition.achievementOrderTypeStr == "SALES" || searchCondition.achievementOrderTypeStr == "ALL")) {
    firstStr += '<td colspan="4"><strong>商品销售</strong></td>';
    secondTrSample += '<td class="left">收入</td><td>提成</td><td>利润</td><td class="right">利润提成</td>';
  }

  if (APP_BCGOGO.Permission.Stat.NonOperatingAccount.Update && (searchCondition.achievementOrderTypeStr == "BUSINESS_ACCOUNT" || searchCondition.achievementOrderTypeStr == "ALL")) {
    firstStr += '<td><strong>营业外记账</strong></td>';
    secondTrSample += '<td class="left right">收入</td>';
  }

  if (searchCondition.achievementOrderTypeStr == "ALL") {
    firstStr += '<td colspan="4"><strong>合计</strong></td>';
    secondTrSample += '<td class="left">收入</td><td>提成</td><td>利润</td><td class="right">利润提成</td>';
  }

  return firstStr + secondTrSample;

}

function initAchievementStatTable(data) {
  $("#departmentStatTable colgroup,#departmentStatTable tbody").remove();
  if (data == null || data == "" || data[0].assistantAchievementStatDTOList.length == 0) {
    $("#printButton").css("display", "none");
      $("#exportButton").css("display", "none");
    var tr = getTrSampleBySearchCondition(data[0]);
    $("#departmentStatTable").css("width", "100%");
    $("#scrollDiv").attr("style","");
    $("#departmentStatTable").append($(tr));
    $("#noDataSpan").css("display","block");
    return;
  } else {
    $("#printButton").css("display", "block");
      $("#exportButton").css("display", "block");
    $("#noDataSpan").css("display","none");
  }
  var tr = getTrSampleBySearchCondition(data[0]);

  $("#departmentStatTable").append($(tr));


  var str = '<tr class="space"><td colspan="21"></td></tr>';
  $("#departmentStatTable").append(str);

  var startTime = data[0].startTime;
  var endTime = data[0].endTime;


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
        salesProfit = assistantStatDTO.salesProfit ? assistantStatDTO.salesProfit : 0,
        salesProfitAchievement = assistantStatDTO.salesProfitAchievement ? assistantStatDTO.salesProfitAchievement : 0,

        wash = assistantStatDTO.wash ? assistantStatDTO.wash : 0,
        washTimes = assistantStatDTO.washTimes ? assistantStatDTO.washTimes : 0,
        washAchievement = assistantStatDTO.washAchievement ? assistantStatDTO.washAchievement : 0,

        member = assistantStatDTO.member ? assistantStatDTO.member : 0,
        memberAchievement = assistantStatDTO.memberAchievement ? assistantStatDTO.memberAchievement : 0,


        memberRenew = assistantStatDTO.memberRenew ? assistantStatDTO.memberRenew : 0,
        memberRenewAchievement = assistantStatDTO.memberRenewAchievement ? assistantStatDTO.memberRenewAchievement : 0,

        businessAccount = assistantStatDTO.businessAccount ? assistantStatDTO.businessAccount : 0,

        statSum = assistantStatDTO.statSum ? assistantStatDTO.statSum : 0,
        achievementSum = assistantStatDTO.achievementSum ? assistantStatDTO.achievementSum : 0,

        achievementStatType = assistantStatDTO.achievementStatType ? assistantStatDTO.achievementStatType : "",

        assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";


        var idStr = "";
        if (data[0].achievementStatType == "ASSISTANT") {
          idStr = assistantId;
        } else if (data[0].achievementStatType == "DEPARTMENT") {
          idStr = departmentId;
        }

    if (data[0].achievementCalculateWayStr == "CALCULATE_BY_ASSISTANT") {
      serviceAchievement = assistantStatDTO.serviceAchievementByAssistant ? assistantStatDTO.serviceAchievementByAssistant : 0;
      saleAchievement = assistantStatDTO.salesAchievementByAssistant ? assistantStatDTO.salesAchievementByAssistant : 0;
      salesProfitAchievement = assistantStatDTO.salesProfitAchievementByAssistant ? assistantStatDTO.salesProfitAchievementByAssistant : 0;
      washAchievement = assistantStatDTO.washAchievementByAssistant ? assistantStatDTO.washAchievementByAssistant : 0;
      memberAchievement = assistantStatDTO.memberAchievementByAssistant ? assistantStatDTO.memberAchievementByAssistant : 0;
      memberRenewAchievement = assistantStatDTO.memberRenewAchievementByAssistant ? assistantStatDTO.memberRenewAchievementByAssistant : 0;
      achievementSum = assistantStatDTO.achievementSumByAssistant ? assistantStatDTO.achievementSumByAssistant : 0;
    }

    var tr = '<tr class="titBody_Bg">';

    tr += '';

    var assistantNameShort = G.isEmpty(assistantName) ? "" : (assistantName.length > 3 ? (assistantName.substring(0, 3) + "...") : assistantName);
    if (data[0].achievementStatType == "ASSISTANT") {
      tr += '<td title="' + assistantName + '"><a class="blue_color" href="#" onclick="openServiceRecord(\'' + achievementStatType + '\',\'' + idStr + '\',\'' + startTime + '\',\'' + endTime  + '\',\'' + data[0].achievementOrderTypeStr + '\',\'' + data[0].achievementCalculateWayStr + '\',\'' + data[0].serviceIdStr + '\');">' + assistantName + '</a></td>';
      tr += '<td title="' + departmentName + '">' + departmentName + '</td>';
    } else if (data[0].achievementStatType == "DEPARTMENT") {
      tr += '<td title="' + departmentName + '"><a class="blue_color" href="#" onclick="openServiceRecord(\'' + achievementStatType + '\',\'' + idStr + '\',\'' + startTime + '\',\'' + endTime  + '\',\'' + data[0].achievementOrderTypeStr + '\',\'' + data[0].achievementCalculateWayStr + '\',\'' + data[0].serviceIdStr + '\');">' + departmentName + '</a></td>';
    }



    if (APP_BCGOGO.Permission.Version.VehicleConstruction && ( data[0].achievementOrderTypeStr == "ALL" || data[0].achievementOrderTypeStr == "REPAIR_SERVICE")) {
      tr += '<td title="' + standardHours + '">' + standardHours + '</td>';
      tr += '<td title="' + standardService + '">' + standardService + '</td>';
      tr += '<td title="' + actualHours + '">' + actualHours + '</td>';
      tr += '<td title="' + actualService + '">' + actualService + '</td>';
      tr += '<td title="' + serviceAchievement + '">' + serviceAchievement + '</td>';
    }
    if (APP_BCGOGO.Permission.Version.MemberStoredValue && ( data[0].achievementOrderTypeStr == "ALL" || data[0].achievementOrderTypeStr == "MEMBER")) {
      if (data[0].serviceIdStr == "") {
        tr += '<td title="' + dataTransition.rounding(member + memberRenew, 2) + '">' + dataTransition.rounding(member + memberRenew, 2) + '</td>';
        tr += '<td title="' + dataTransition.rounding(memberAchievement + memberRenewAchievement, 2) + '">' + dataTransition.rounding(memberAchievement + memberRenewAchievement, 2) + '</td>';
      } else if (data[0].serviceIdStr == "NEW") {
        tr += '<td title="' + member + '">' + member + '</td>';
        tr += '<td title="' + memberAchievement + '">' + memberAchievement + '</td>';
      } else if (data[0].serviceIdStr == "RENEW") {
        tr += '<td title="' + memberRenew + '">' + memberRenew + '</td>';
        tr += '<td title="' + memberRenewAchievement + '">' + memberRenewAchievement + '</td>';
      }
    }
    if (APP_BCGOGO.Permission.Stat.BusinessStat.Business.WashCar && ( data[0].achievementOrderTypeStr == "ALL" || data[0].achievementOrderTypeStr == "WASH_BEAUTY")) {
      tr += '<td title="' + washTimes + '">' + washTimes + '</td>';
      tr += '<td title="' + wash + '">' + wash + '</td>';
      tr += '<td title="' + washAchievement + '">' + washAchievement + '</td>';
    }

    if (APP_BCGOGO.Permission.Txn.SaleManage.Sale && (data[0].achievementOrderTypeStr == "SALES" || data[0].achievementOrderTypeStr == "ALL")) {
      tr += '<td title="' + sale + '">' + sale + '</td>';
      tr += '<td title="' + saleAchievement + '">' + saleAchievement + '</td>';

      tr += '<td title="' + salesProfit + '">' + salesProfit + '</td>';
      tr += '<td title="' + salesProfitAchievement + '">' + salesProfitAchievement + '</td>';
    }

    if (APP_BCGOGO.Permission.Stat.NonOperatingAccount.Update && (data[0].achievementOrderTypeStr == "BUSINESS_ACCOUNT" || data[0].achievementOrderTypeStr == "ALL")) {
      tr += '<td title="' + businessAccount + '">' + businessAccount + '</td>';
    }

    if (data[0].achievementOrderTypeStr == "ALL") {
      tr += '<td title="' + statSum + '">' + statSum + '</td>';
      tr += '<td title="' + achievementSum + '">' + achievementSum + '</td>';
      tr += '<td title="' + salesProfit + '">' + salesProfit + '</td>';
      tr += '<td title="' + salesProfitAchievement + '">' + salesProfitAchievement + '</td>';
    }

    tr += '';

    tr += '</tr>';

    tr += '<tr class="space"><td colspan="21"></td></tr>';
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
        salesProfit = assistantStatDTO.salesProfit ? assistantStatDTO.salesProfit : 0,
        salesProfitAchievement = assistantStatDTO.salesProfitAchievement ? assistantStatDTO.salesProfitAchievement : 0,

        wash = assistantStatDTO.wash ? assistantStatDTO.wash : 0,
        washTimes = assistantStatDTO.washTimes ? assistantStatDTO.washTimes : 0,
        washAchievement = assistantStatDTO.washAchievement ? assistantStatDTO.washAchievement : 0,

        member = assistantStatDTO.member ? assistantStatDTO.member : 0,
        memberAchievement = assistantStatDTO.memberAchievement ? assistantStatDTO.memberAchievement : 0,

        memberRenew = assistantStatDTO.memberRenew ? assistantStatDTO.memberRenew : 0,
        memberRenewAchievement = assistantStatDTO.memberRenewAchievement ? assistantStatDTO.memberRenewAchievement : 0,

        businessAccount = assistantStatDTO.businessAccount ? assistantStatDTO.businessAccount : 0,

        statSum = assistantStatDTO.statSum ? assistantStatDTO.statSum : 0,
        achievementSum = assistantStatDTO.achievementSum ? assistantStatDTO.achievementSum : 0,

        achievementStatType = assistantStatDTO.achievementStatType ? assistantStatDTO.achievementStatType : "",

        assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";

    if (data[0].achievementCalculateWayStr == "CALCULATE_BY_ASSISTANT") {
      serviceAchievement = assistantStatDTO.serviceAchievementByAssistant ? assistantStatDTO.serviceAchievementByAssistant : 0;
      saleAchievement = assistantStatDTO.salesAchievementByAssistant ? assistantStatDTO.salesAchievementByAssistant : 0;
      salesProfitAchievement = assistantStatDTO.salesProfitAchievementByAssistant ? assistantStatDTO.salesProfitAchievementByAssistant : 0;
      washAchievement = assistantStatDTO.washAchievementByAssistant ? assistantStatDTO.washAchievementByAssistant : 0;
      memberAchievement = assistantStatDTO.memberAchievementByAssistant ? assistantStatDTO.memberAchievementByAssistant : 0;
      memberRenewAchievement = assistantStatDTO.memberRenewAchievementByAssistant ? assistantStatDTO.memberRenewAchievementByAssistant : 0;
      achievementSum = assistantStatDTO.achievementSumByAssistant ? assistantStatDTO.achievementSumByAssistant : 0;
    }


    var tr = '<tr class="titBody_Bg" style="font-weight:bold;">';
    tr += '';

    if (data[0].achievementStatType == "ASSISTANT") {
      tr += '<td style="text-align:right" colspan="2">合计：</td> ';
    } else if (data[0].achievementStatType == "DEPARTMENT") {
      tr += '<td style="text-align:right" colspan="1">合计：</td> ';
    }


    if (APP_BCGOGO.Permission.Version.VehicleConstruction && ( data[0].achievementOrderTypeStr == "ALL" || data[0].achievementOrderTypeStr == "REPAIR_SERVICE")) {
      tr += '<td title="' + standardHours + '">' + standardHours + '</td>';
      tr += '<td title="' + standardService + '">' + standardService + '</td>';
      tr += '<td title="' + actualHours + '">' + actualHours + '</td>';
      tr += '<td title="' + actualService + '">' + actualService + '</td>';
      tr += '<td title="' + serviceAchievement + '">' + serviceAchievement + '</td>';
    }
    if (APP_BCGOGO.Permission.Version.MemberStoredValue && ( data[0].achievementOrderTypeStr == "ALL" || data[0].achievementOrderTypeStr == "MEMBER")) {


      if (data[0].serviceIdStr == "") {
        tr += '<td title="' + dataTransition.rounding(member + memberRenew, 2) + '">' + dataTransition.rounding(member + memberRenew, 2) + '</td>';
        tr += '<td title="' + dataTransition.rounding(memberAchievement + memberRenewAchievement, 2) + '">' + dataTransition.rounding(memberAchievement + memberRenewAchievement, 2) + '</td>';
      } else if (data[0].serviceIdStr == "NEW") {
        tr += '<td title="' + member + '">' + member + '</td>';
        tr += '<td title="' + memberAchievement + '">' + memberAchievement + '</td>';
      } else if (data[0].serviceIdStr == "RENEW") {
        tr += '<td title="' + memberRenew + '">' + memberRenew + '</td>';
        tr += '<td title="' + memberRenewAchievement + '">' + memberRenewAchievement + '</td>';
      }

    }
    if (APP_BCGOGO.Permission.Stat.BusinessStat.Business.WashCar && ( data[0].achievementOrderTypeStr == "ALL" || data[0].achievementOrderTypeStr == "WASH_BEAUTY")) {
      tr += '<td title="' + washTimes + '">' + washTimes + '</td>';
      tr += '<td title="' + wash + '">' + wash + '</td>';
      tr += '<td title="' + washAchievement + '">' + washAchievement + '</td>';
    }


    if (APP_BCGOGO.Permission.Txn.SaleManage.Sale && (data[0].achievementOrderTypeStr == "SALES" || data[0].achievementOrderTypeStr == "ALL")) {
      tr += '<td title="' + sale + '">' + sale + '</td>';
      tr += '<td title="' + saleAchievement + '">' + saleAchievement + '</td>';

      tr += '<td title="' + salesProfit + '">' + salesProfit + '</td>';
      tr += '<td title="' + salesProfitAchievement + '">' + salesProfitAchievement + '</td>';
    }

    if (APP_BCGOGO.Permission.Stat.NonOperatingAccount.Update && (data[0].achievementOrderTypeStr == "BUSINESS_ACCOUNT" || data[0].achievementOrderTypeStr == "ALL")) {
      tr += '<td title="' + businessAccount + '">' + businessAccount + '</td>';
    }

    if (data[0].achievementOrderTypeStr == "ALL") {
      tr += '<td title="' + statSum + '">' + statSum + '</td>';
      tr += '<td title="' + achievementSum + '">' + achievementSum + '</td>';

      tr += '<td title="' + salesProfit + '">' + salesProfit + '</td>';
      tr += '<td title="' + salesProfitAchievement + '">' + salesProfitAchievement + '</td>';
    }



    tr += '';

    tr += '</tr>';
    $("#departmentStatTable").append(tr);
  }
}
