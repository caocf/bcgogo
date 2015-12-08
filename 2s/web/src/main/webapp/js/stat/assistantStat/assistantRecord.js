var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;


$(document).ready(function() {
  $("#backButton").live("click", function() {
      window.location.href="assistantStat.do?method=redirectAssistantStat&startTime="+$('#startTime').val()
          +"&endTime="+$('#endTime').val() +"&achievementStatType="+$('#achievementStatTypeStr').val()+"&startPageNoHiddenHidden=1"
          +"&achievementCalculateWay="+$('#achievementCalculateWayHidden').val() +"&assistantOrDepartmentIdStrHidden="
          +$('#assistantOrDepartmentId').val()+"&achievementOrderTypeStrHidden="+$('#achievementOrderTypeStrHidden').val()+"&serviceIdStrHidden="+$('#serviceIdStrHidden').val();

  });
});

function initData(pageType){

  var url = "";
  var orderType = "";
  if (pageType == "assistantMemberRecord") {
    url = "assistantStat.do?method=getAssistantMemberByPage";
  } else if (pageType == "assistantProductRecord") {
    url = "assistantStat.do?method=getAssistantProductByPage";
  } else if (pageType == "assistantServiceRecord") {
    url = "assistantStat.do?method=getAssistantServiceByPage";
    orderType = "repair";
  } else if (pageType == "assistantWashRecord") {
    url = "assistantStat.do?method=getAssistantServiceByPage";
    orderType = "washBeauty"
  } else if (pageType == "assistantBusinessAccountRecord") {
    url = "assistantStat.do?method=getAssistantBusinessAccountByPage";
    orderType = "businessAccount"
  } else {
    return;
  }

  var data = {
    assistantOrDepartmentIdStr:$('#assistantOrDepartmentId').val(),
    achievementStatTypeStr:$('#achievementStatTypeStr').val(),
    startTimeStr:$('#startTime').val(),
    endTimeStr:$('#endTime').val(),
    startPageNo:1,
    maxRows:15,
    orderType:orderType,
    achievementOrderTypeStr:$("#achievementOrderTypeStrHidden").val(),
    achievementCalculateWayStr:$("#achievementCalculateWayHidden").val(),
    serviceIdStr:$("#serviceIdStrHidden").val()

  }
  bcgogoAjaxQuery.setUrlData(url, data);
  bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {

    if (jsonStr && jsonStr != "") {

      if (pageType == "assistantMemberRecord") {
        initAssistantMember(jsonStr);
        initPages(jsonStr, 'dynamicalassistantMemberRecord', url, '', 'initAssistantMember', '', '', data, '');
      } else if (pageType == "assistantProductRecord") {
        initAssistantProduct(jsonStr);
        initPages(jsonStr, 'dynamicalassistantProductRecord', url, '', 'initAssistantProduct', '', '', data, '');
      } else if (pageType == "assistantServiceRecord") {
        initAssistantService(jsonStr);
        initPages(jsonStr, 'dynamicalassistantServiceRecord', url, '', 'initAssistantService', '', '', data, '');
      } else if (pageType == "assistantWashRecord") {
        initAssistantWash(jsonStr);
        initPages(jsonStr, 'dynamicalassistantWashRecord', url, '', 'initAssistantWash', '', '', data, '');
      } else if (pageType == "assistantBusinessAccountRecord") {
        initAssistantBusinessAccount(jsonStr);
        initPages(jsonStr, 'dynamicalassistantBusinessAccountRecord', url, '', 'initAssistantBusinessAccount', '', '', data, '');
      } else {
        return;
      }
    }
  });
}


function initAssistantService(data) {


  $("#assistantServiceRecord tr:not(`:first)").remove();

  if (data == null || data == "" || data[0].serviceRecordDTOList.length == 0) {
    $("#repairNoDataSpan").css("display", "block");
    $("#assistantServiceRecordNum").val('0');
    return;
  } else {
    $("#repairNoDataSpan").css("display", "none");
  }

  var str = '<tr class="space"><td colspan="13"></td></tr>';
  $("#assistantServiceRecord").append(str);

  var startTime = data[0].startTime;
  var endTime = data[0].endTime;
  $("#assistantServiceRecordNum").val(data[1].totalRows);
  $.each(data[0].serviceRecordDTOList, function (index, assistantStatDTO) {
    var departmentId = (!assistantStatDTO.departmentIdStr ? "--" : assistantStatDTO.departmentIdStr),
        departmentName = assistantStatDTO.departmentName ? assistantStatDTO.departmentName : "",
        assistantId = assistantStatDTO.assistantIdStr ? assistantStatDTO.assistantIdStr : "",

        vestDateStr = assistantStatDTO.vestDateStr ? assistantStatDTO.vestDateStr : "",
        orderTypeStr = assistantStatDTO.orderTypeStr ? assistantStatDTO.orderTypeStr : "",
        customer = assistantStatDTO.customer ? assistantStatDTO.customer : "",
        vehicle = assistantStatDTO.vehicle ? assistantStatDTO.vehicle : "",
        serviceName = assistantStatDTO.serviceName ? assistantStatDTO.serviceName : "",

        standardHours = assistantStatDTO.standardHours ? assistantStatDTO.standardHours : 0,
        standardService = assistantStatDTO.standardService ? assistantStatDTO.standardService : 0,
        actualHours = assistantStatDTO.actualHours ? assistantStatDTO.actualHours : 0,
        actualService = assistantStatDTO.actualService ? assistantStatDTO.actualService : 0,

        achievement = assistantStatDTO.achievement ? assistantStatDTO.achievement : 0,
        orderIdStr = assistantStatDTO.orderIdStr ? assistantStatDTO.orderIdStr : "",
        receiptNo = assistantStatDTO.receiptNo ? assistantStatDTO.receiptNo : "",
        url = assistantStatDTO.url ? assistantStatDTO.url : "",
        calculateWay = assistantStatDTO.achievementCalculateWay ? assistantStatDTO.achievementCalculateWay : "",
        assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";

    if (data[0].achievementCalculateWayStr == "CALCULATE_BY_ASSISTANT") {
      achievement = assistantStatDTO.achievementByAssistant ? assistantStatDTO.achievementByAssistant : 0;
      calculateWay = assistantStatDTO.achievementByAssistantCalculateWay ? assistantStatDTO.achievementByAssistantCalculateWay : "";
    }
    var calculateWayShort = calculateWay.length > 10 ? calculateWay.substring(0, 10) + "..." : calculateWay;

    var tr = '<tr class="titBody_Bg">';
    tr += '<td style="padding-left:10px;" title="' + assistantName + '">' + assistantName + '</td>';
    tr += '<td title="' + departmentName + '">' + departmentName + '</td>';

    tr += '<td title="' + vestDateStr + '">' + vestDateStr + '</td>';
    tr += '<td title="' + vehicle + '">' + vehicle + '</td>';
    tr += '<td title="' + customer + '">' + customer + '</td>';
    tr += '<td title="' + serviceName + '">' + serviceName + '</td>';

    tr += '<td title="' + standardHours + '">' + standardHours + '</td>';
    tr += '<td title="' + standardService + '">' + standardService + '</td>';
    tr += '<td title="' + actualHours + '">' + actualHours + '</td>';
    tr += '<td title="' + actualService + '">' + actualService + '</td>';
    tr += '<td title="' + achievement + '">' + achievement + '</td>';
    tr += '<td title="' + receiptNo + '"><a class="blue_color" href="#" onclick="detail(\'' + url + '\');">' + receiptNo + '</a></td>';

    tr += '</tr>';
    tr += '<tr class="titBottom_Bg"><td colspan="13"></td></tr>';
    $("#assistantServiceRecord").append(tr);
  });
}



function initAssistantWash(data) {


  $("#assistantWashBeautyRecord tr:not(`:first)").remove();

  if (data == null || data == "" || data[0].serviceRecordDTOList.length == 0) {
    $("#washNoDataSpan").css("display", "block");
    $("#assistantWashRecordNum").val('0');
    return;
  } else {
    $("#washNoDataSpan").css("display", "none");
  }

  var str = '<tr class="space"><td colspan="12"></td></tr>';
  $("#assistantWashBeautyRecord").append(str);
  $("#assistantWashRecordNum").val(data[1].totalRows);
  $.each(data[0].serviceRecordDTOList, function (index, assistantStatDTO) {
    var departmentId = (!assistantStatDTO.departmentIdStr ? "--" : assistantStatDTO.departmentIdStr),
        departmentName = assistantStatDTO.departmentName ? assistantStatDTO.departmentName : "",
        assistantId = assistantStatDTO.assistantIdStr ? assistantStatDTO.assistantIdStr : "",

        vestDateStr = assistantStatDTO.vestDateStr ? assistantStatDTO.vestDateStr : "",
        orderTypeStr = assistantStatDTO.orderTypeStr ? assistantStatDTO.orderTypeStr : "",
        customer = assistantStatDTO.customer ? assistantStatDTO.customer : "",
        vehicle = assistantStatDTO.vehicle ? assistantStatDTO.vehicle : "",
        serviceName = assistantStatDTO.serviceName ? assistantStatDTO.serviceName : "",

        actualService = assistantStatDTO.actualService ? assistantStatDTO.actualService : 0,

        achievement = assistantStatDTO.achievement ? assistantStatDTO.achievement : 0,
        orderIdStr = assistantStatDTO.orderIdStr ? assistantStatDTO.orderIdStr : "",
        receiptNo = assistantStatDTO.receiptNo ? assistantStatDTO.receiptNo : "",
        url = assistantStatDTO.url ? assistantStatDTO.url : "",
        calculateWay = assistantStatDTO.achievementCalculateWay ? assistantStatDTO.achievementCalculateWay : "",
        assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";

    if (data[0].achievementCalculateWayStr == "CALCULATE_BY_ASSISTANT") {
      achievement = assistantStatDTO.achievementByAssistant ? assistantStatDTO.achievementByAssistant : 0;
      calculateWay = assistantStatDTO.achievementByAssistantCalculateWay ? assistantStatDTO.achievementByAssistantCalculateWay : "";
    }
    var calculateWayShort = calculateWay.length > 10 ? calculateWay.substring(0, 10) + "..." : calculateWay;

    var tr = '<tr class="titBody_Bg">';
    tr += '<td style="padding-left:10px;" title="' + assistantName + '">' + assistantName + '</td>';
    tr += '<td title="' + departmentName + '">' + departmentName + '</td>';

    tr += '<td title="' + vestDateStr + '">' + vestDateStr + '</td>';
    tr += '<td title="' + vehicle + '">' + vehicle + '</td>';
    tr += '<td title="' + customer + '">' + customer + '</td>';
    tr += '<td title="' + serviceName + '">' + serviceName + '</td>';

    tr += '<td title="' + actualService + '">' + actualService + '</td>';
    tr += '<td title="' + achievement + '">' + achievement + '</td>';
    tr += '<td title="' + receiptNo + '"><a class="blue_color" href="#" onclick="detail(\'' + url + '\');">' + receiptNo + '</a></td>';

    tr += '</tr>';
    tr += '<tr class="titBottom_Bg"><td colspan="13"></td></tr>';
    $("#assistantWashBeautyRecord").append(tr);
  });
}

function initAssistantMember(data) {


  $("#assistantMemberRecord tr:not(`:first)").remove();

  if (data == null || data == "" || data[0].memberRecordDTOList.length == 0) {
    $("#memberNoDataSpan").css("display","block");
    $("#assistantMemberRecordNum").val('0');
    return;
  }else{
    $("#memberNoDataSpan").css("display","none");
  }

  var str = '<tr class="space"><td colspan="13"></td></tr>';
  $("#assistantMemberRecord").append(str);
  $("#assistantMemberRecordNum").val(data[1].totalRows);
  $.each(data[0].memberRecordDTOList, function (index, assistantStatDTO) {
    var departmentId = (!assistantStatDTO.departmentIdStr ? "--" : assistantStatDTO.departmentIdStr),
        departmentName = assistantStatDTO.departmentName ? assistantStatDTO.departmentName : "",
        assistantId = assistantStatDTO.assistantIdStr ? assistantStatDTO.assistantIdStr : "",
        orderType = assistantStatDTO.orderType ? assistantStatDTO.orderType : "",
        vestDateStr = assistantStatDTO.vestDateStr ? assistantStatDTO.vestDateStr : "",
        orderTypeStr = assistantStatDTO.orderTypeStr ? assistantStatDTO.orderTypeStr : "",
        memberOrderTypeStr = assistantStatDTO.memberOrderTypeStr ? assistantStatDTO.memberOrderTypeStr : "",
        customer = assistantStatDTO.customer ? assistantStatDTO.customer : "",
        memberNo = assistantStatDTO.memberNo ? assistantStatDTO.memberNo : "",
        memberCardName = assistantStatDTO.memberCardName ? assistantStatDTO.memberCardName : "",
        memberCardTypeStr = assistantStatDTO.memberCardTypeStr ? assistantStatDTO.memberCardTypeStr : "",
        memberCardTotal = assistantStatDTO.memberCardTotal ? assistantStatDTO.memberCardTotal : "",
        memberCardTypeStr = assistantStatDTO.memberCardTypeStr ? assistantStatDTO.memberCardTypeStr : "",

        total = assistantStatDTO.total ? assistantStatDTO.total : 0,

        achievement = assistantStatDTO.achievement ? assistantStatDTO.achievement : 0,
        orderIdStr = assistantStatDTO.orderIdStr ? assistantStatDTO.orderIdStr : "",
        calculateWay = assistantStatDTO.achievementCalculateWay ? assistantStatDTO.achievementCalculateWay : "",
        assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";


    if (data[0].achievementCalculateWayStr == "CALCULATE_BY_ASSISTANT") {
      achievement = assistantStatDTO.achievementByAssistant ? assistantStatDTO.achievementByAssistant : 0;
      calculateWay = assistantStatDTO.achievementByAssistantCalculateWay ? assistantStatDTO.achievementByAssistantCalculateWay : 0;
    }
    var calculateWayShort = calculateWay.length > 10 ? calculateWay.substring(0, 10) + "..." : calculateWay;
    var tr = '<tr class="titBody_Bg">';
    tr += '<td style="padding-left:10px;" title="' + assistantName + '">' + assistantName + '</td>';
    tr += '<td title="' + departmentName + '">' + departmentName + '</td>';

    tr += '<td title="' + vestDateStr + '">' + vestDateStr + '</td>';
    tr += '<td title="' + memberNo + '">' + memberNo + '</td>';
    tr += '<td title="' + memberCardName + '">' + memberCardName + '</td>';
    tr += '<td title="' + memberCardTypeStr + '">' + memberCardTypeStr + '</td>';
    tr += '<td title="' + memberCardTotal + '">' + memberCardTotal + '</td>';
    tr += '<td title="' + customer + '">' + customer + '</td>';

    if(orderType == "MEMBER_RETURN_CARD"){
        tr += '<td title="' + '退卡' + '">' + '退卡' + '</td>';
    }else{
        tr += '<td title="' + memberOrderTypeStr + '">' + memberOrderTypeStr + '</td>';
    }

    tr += '<td title="' + total + '">' + total + '</td>';
    tr += '<td title="' + achievement + '">' + achievement + '</td>';
    tr += '</tr>';
    tr += '<tr class="titBottom_Bg"><td colspan="13"></td></tr>';
    $("#assistantMemberRecord").append(tr);
  });
}


function initAssistantProduct(data) {


  $("#assistantProductRecord tr:not(`:first)").remove();

  if (data == null || data == "" || data[0].productRecordDTOList.length == 0) {
    $("#productNoDataSpan").css("display","block");
    $("#assistantProductRecordNum").val('0');
    return;
  }else{
    $("#productNoDataSpan").css("display","none");
  }

  var str = '<tr class="space"><td colspan="13"></td></tr>';
  $("#assistantProductRecord").append(str);

  var startTime = data[0].startTime;
  var endTime = data[0].endTime;
  $("#assistantProductRecordNum").val(data[1].totalRows);

  $.each(data[0].productRecordDTOList, function (index, assistantStatDTO) {
    var departmentId = (!assistantStatDTO.departmentIdStr ? "--" : assistantStatDTO.departmentIdStr),
        departmentName = assistantStatDTO.departmentName ? assistantStatDTO.departmentName : "",
        assistantId = assistantStatDTO.assistantIdStr ? assistantStatDTO.assistantIdStr : "",

        vestDateStr = assistantStatDTO.vestDateStr ? assistantStatDTO.vestDateStr : "",
        orderTypeStr = assistantStatDTO.orderTypeStr ? assistantStatDTO.orderTypeStr : "",
        customer = assistantStatDTO.customer ? assistantStatDTO.customer : "",
        productName = assistantStatDTO.productName ? assistantStatDTO.productName : "",
        amount = assistantStatDTO.amount ? assistantStatDTO.amount : 0,

        price = assistantStatDTO.price ? assistantStatDTO.price : 0,
        unit = assistantStatDTO.unit ? assistantStatDTO.unit : "",

        total = assistantStatDTO.total ? assistantStatDTO.total : 0,
        achievement = assistantStatDTO.achievement ? assistantStatDTO.achievement : 0,
        orderIdStr = assistantStatDTO.orderIdStr ? assistantStatDTO.orderIdStr : "",
        calculateWay = assistantStatDTO.achievementCalculateWay ? assistantStatDTO.achievementCalculateWay : "",

        profit = assistantStatDTO.profit ? assistantStatDTO.profit : 0,
        profitAchievement = assistantStatDTO.profitAchievement ? assistantStatDTO.profitAchievement : 0,
        profitCalculateWay = assistantStatDTO.profitCalculateWay ? assistantStatDTO.profitCalculateWay : "",

        receiptNo = assistantStatDTO.receiptNo ? assistantStatDTO.receiptNo : "",
        url = assistantStatDTO.url ? assistantStatDTO.url : "",

        assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";
    var amountStr = amount + unit;

    if (data[0].achievementCalculateWayStr == "CALCULATE_BY_ASSISTANT") {
      achievement = assistantStatDTO.achievementByAssistant ? assistantStatDTO.achievementByAssistant : 0;
      calculateWay = assistantStatDTO.achievementByAssistantCalculateWay ? assistantStatDTO.achievementByAssistantCalculateWay : "";
      profitAchievement = assistantStatDTO.profitAchievementByAssistant ? assistantStatDTO.profitAchievementByAssistant : "";
      profitCalculateWay = assistantStatDTO.profitByAssistantCalculateWay ? assistantStatDTO.profitByAssistantCalculateWay : "";
    }
    var calculateWayShort = calculateWay.length > 10 ? calculateWay.substring(0, 10) + "..." : calculateWay;
    var profitCalculateWayShort = profitCalculateWay.length > 10 ? profitCalculateWay.substring(0, 10) + "..." : profitCalculateWay;

    var tr = '<tr class="titBody_Bg">';
    tr += '<td style="padding-left:10px;" title="' + assistantName + '">' + assistantName + '</td>';
    tr += '<td title="' + departmentName + '">' + departmentName + '</td>';

    tr += '<td title="' + vestDateStr + '">' + vestDateStr + '</td>';
    tr += '<td title="' + orderTypeStr + '">' + orderTypeStr + '</td>';
    tr += '<td title="' + customer + '">' + customer + '</td>';
    tr += '<td title="' + productName + '">' + productName + '</td>';

    tr += '<td title="' + amountStr + '">' + amountStr + '</td>';
    tr += '<td title="' + price + '">' + price + '</td>';
    tr += '<td title="' + total + '">' + total + '</td>';
    tr += '<td title="' + achievement + '">' + achievement + '</td>';
    tr += '<td title="' + profit + '">' + profit + '</td>';
    tr += '<td title="' + profitAchievement + '">' + profitAchievement + '</td>';
    tr += '<td title="' + receiptNo + '"><a class="blue_color" href="#" onclick="detail(\'' + url + '\');">' + receiptNo + '</a></td>';

    tr += '</tr>';
    tr += '<tr class="titBottom_Bg"><td colspan="13"></td></tr>';
    $("#assistantProductRecord").append(tr);
  });
}

function detail(url) {
  window.open(encodeURI(url));
}


function initAssistantBusinessAccount(data) {


  $("#assistantBusinessAccountRecord tr:not(`:first)").remove();

  if (data == null || data == "" || data[0].businessAccountRecordDTOList.length == 0) {
    $("#businessAccountNoDataSpan").css("display", "block");
    $("#assistantBusinessAccountRecordNum").val('0');
    return;
  } else {
    $("#businessAccountNoDataSpan").css("display", "none");
  }

  var str = '<tr class="space"><td colspan="8"></td></tr>';
  $("#assistantBusinessAccountRecord").append(str);
  $("#assistantBusinessAccountRecordNum").val(data[1].totalRows);
  $.each(data[0].businessAccountRecordDTOList, function (index, assistantStatDTO) {
    var departmentId = (!assistantStatDTO.departmentIdStr ? "--" : assistantStatDTO.departmentIdStr),
        departmentName = assistantStatDTO.departmentName ? assistantStatDTO.departmentName : "",
        assistantId = assistantStatDTO.assistantIdStr ? assistantStatDTO.assistantIdStr : "",

        vestDateStr = assistantStatDTO.vestDateStr ? assistantStatDTO.vestDateStr : "",
        accountCategory = assistantStatDTO.accountCategory ? assistantStatDTO.accountCategory : "",
        docNo = assistantStatDTO.docNo ? assistantStatDTO.docNo : "",
        content = assistantStatDTO.content ? assistantStatDTO.content : "",

        businessCategory = assistantStatDTO.businessCategory ? assistantStatDTO.businessCategory : "",

        total = assistantStatDTO.total ? assistantStatDTO.total : 0,
        calculateWay = assistantStatDTO.achievementCalculateWay ? assistantStatDTO.achievementCalculateWay : "",
        assistantName = assistantStatDTO.assistantName ? assistantStatDTO.assistantName : "";


    var tr = '<tr class="titBody_Bg">';
    tr += '<td style="padding-left:10px;" title="' + departmentName + '">' + departmentName + '</td>';
    tr += '<td title="' + assistantName + '">' + assistantName + '</td>';

    tr += '<td title="' + vestDateStr + '">' + vestDateStr + '</td>';
    tr += '<td title="' + accountCategory + '">' + accountCategory + '</td>';
    tr += '<td title="' + docNo + '">' + docNo + '</td>';
    tr += '<td title="' + content + '">' + content + '</td>';

    tr += '<td title="' + businessCategory + '">' + businessCategory + '</td>';
    tr += '<td title="' + total + '">' + total + '</td>';
    tr += '</tr>';
    tr += '<tr class="titBottom_Bg"><td colspan="8"></td></tr>';
    $("#assistantBusinessAccountRecord").append(tr);
  });
}

function displayButton(display) {
  $("#printButton").css("display", display);
  $("#exportButton").css("display", display);
}
