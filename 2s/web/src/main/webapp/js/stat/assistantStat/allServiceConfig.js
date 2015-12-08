var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;


function achievementNotify() {
  var num = $("#totalConfigNum").text();
  if (num * 1 > 0) {
    nsDialog.jConfirm("还有" + num + "个项目未支配提成,是否继续配置?", null, function (getVal) {
      if (getVal) {
        $(this).dialog("close");
        $("#totalConfigNumSpan").click();
        return false;
      } else {
        window.location = "assistantStat.do?method=redirectAssistantStat";
      }
    });
  } else {

    var data = {};
    APP_BCGOGO.Net.asyncPost({
      url: "assistantStat.do?method=getSalesManConfigNum",
      dataType: "json",
      data:data,
      success: function(json) {
        if (json && json.success && json.data * 1 > 0) {
          nsDialog.jConfirm("还有" + json.data + "个员工未支配部门,是否继续配置?", null, function (getVal) {
            if (getVal) {
              $(this).dialog("close");
              window.location = "assistantStat.do?method=searchSalesManConfig&type=unConfig";
            } else {
              window.location = "assistantStat.do?method=redirectAssistantStat";
            }
          });
        }else{
          window.location = "assistantStat.do?method=redirectAssistantStat";
        }
      },
      error: function() {
        window.location = "assistantStat.do?method=redirectAssistantStat";
      }
    });
  }
}


$(document).ready(function() {

  $("#totalConfigNumSpan").live("click", function() {
    var data = {
      startPageNo:1,
      maxRows:25
    }
    var url = "assistantStat.do?method=getServiceAchievementByPager";
    $("#tb_construction tr:gt(1)").remove();
    var html = '';
    html += '<tr class="titBody_Bg ">';
    html += '<td colspan="7">数据加载中...</td>';
    html += '</tr>';
    html += '<tr class="titBottom_Bg">';
    html += '<td colspan="7"></td>';
    html += '</tr>';
    $("#tb_construction ").append(html);
    APP_BCGOGO.Net.asyncPost({
      url: url,
      dataType: "json",
      data:data,
      success: function(json) {
        initServiceItem(json);
        initPage(json, "setConstructions", url, null, "initServiceItem", null, null, data, null);
      }
    });
  });

  $("#totalNumSpan").live("click", function() {

    var data = {
      serviceName : "",
      categoryName: "",
      categoryServiceType:"",
      startPageNo:1
    }
    var url = "category.do?method=getCategoryItemSearch";
    $("#tb_construction tr:gt(1)").remove();
    var html = '';
    html += '<tr class="titBody_Bg ">';
    html += '<td colspan="7">数据加载中...</td>';
    html += '</tr>';
    html += '<tr class="titBottom_Bg">';
    html += '<td colspan="7"></td>';
    html += '</tr>';
    $("#tb_construction ").append(html);
    APP_BCGOGO.Net.asyncPost({
      url: url,
      dataType: "json",
      data:data,
      success: function(json) {
        initServiceItem(json);
        initPage(json, "setConstructions", url, null, "initServiceItem", null, null, data, null);
      }
    })
  });
});


function initServiceAchievementTable(json, tableId) {

  if (tableId == undefined || tableId == null) {
    tableId = "serviceAchievementTable";
  }
  $("#" + tableId + " tr:not(:first)").remove();

  if (json == null || json[0] == null) {
    return;
  }

  var tr = ' <tr class="space"><td colspan="6"></td></tr>';
  $("#" + tableId).append($(tr));

  var serviceDTOs = json[0].serviceDTOs;
  var str = '';

  for (var i = 0; i < serviceDTOs.length; i++) {
    var serviceDTO = serviceDTOs[i];

    var serviceId = serviceDTO.idStr;
    var name = serviceDTO.name;
    var categoryName = G.normalize(serviceDTOs.categoryName, "");
    var standardHours = G.normalize(serviceDTOs.standardHours, "");
    var standardUnitPrice = G.normalize(serviceDTOs.standardUnitPrice, "");
    var achievementType = G.normalize(serviceDTOs.achievementType, "");
    var achievementAmount = G.normalize(serviceDTOs.achievementAmount, "");
    str += '<tr class="titBody_Bg">';

    str += '<td style="padding-left:10px;"> ' + name + '</td>'
    str += '<input id="serviceDTOs' + i + '.serviceId" name="serviceDTOs[' + i + '].serviceId" type="hidden"  value="' + serviceId + '"/>'
        + '<td>' + categoryName + '</td>'
        + '<td>' + standardHours + '</td>'
        + '<td>' + standardUnitPrice + '</td>';
    if (achievementType == 'AMOUNT') {
      str += '<td><select id="serviceDTOs' + i + '.achievementType" class="txt selec_jin achievementTypeSelect"><option>按金额</option><option>按比率</option>';
    } else {
      str += '<td><select id="serviceDTOs' + i + '.achievementType" class="txt selec_jin achievementTypeSelect" class="txt selec_jin"><option>按比率</option><option>按金额</option>';
    }
    str += '<td><input type="text"  id="serviceDTOs' + i + '.achievementAmount" class="ti_chen_input" ';
    if (achievementAmount != null && achievementAmount != "") {
      str += ' value = ' + achievementAmount;
    }
    str += 'class="txt ti_chen"/></td>';

  }

  $("#" + tableId).append($(str));

}


function initServiceConfigAchievementTable(json, tableId) {

  if (tableId == undefined || tableId == null) {
    tableId = "serviceConfigAchievementTable";
  }
  $("#" + tableId + " tr:not(:first)").remove();

  if (json == null || json[0] == null) {
    return;
  }

  var tr = ' <tr class="space"><td colspan="6"></td></tr>';
  $("#" + tableId).append($(tr));

  var serviceDTOs = json[0].serviceDTOs;
  var str = '';

  for (var i = 0; i < serviceDTOs.length; i++) {
    var serviceDTO = serviceDTOs[i];

    var serviceId = serviceDTO.idStr;
    var name = serviceDTO.name;
    var categoryName = G.normalize(serviceDTOs.categoryName, "");
    var standardHours = G.normalize(serviceDTOs.standardHours, "");
    var standardUnitPrice = G.normalize(serviceDTOs.standardUnitPrice, "");
    var achievementType = G.normalize(serviceDTOs.achievementType, "");
    var achievementAmount = G.normalize(serviceDTOs.achievementAmount, "");
    str += '<tr class="titBody_Bg">';

    str += '<td style="padding-left:10px;"> ' + name + '</td>'
    str += '<input id="serviceDTOs' + i + '.serviceId" name="serviceDTOs[' + i + '].serviceId" type="hidden"  value="' + serviceId + '"/>'
        + '<td>' + categoryName + '</td>'
        + '<td>' + standardHours + '</td>'
        + '<td>' + standardUnitPrice + '</td>';
    if (achievementType == 'AMOUNT') {
      str += '<td><select  id="serviceDTOs' + i + '.achievementType" class="txt selec_jin achievementTypeSelect"><option>按金额</option><option>按比率</option>';
    } else {
      str += '<td><select id="serviceDTOs' + i + '.achievementType" class="txt selec_jin achievementTypeSelect"><option>按比率</option><option>按金额</option>';
    }
    str += '<td><input type="text" id="serviceDTOs' + i + '.achievementAmount" class="ti_chen_input"';
    if (achievementAmount != null && achievementAmount != "") {
      str += ' value = ' + achievementAmount;
    }
    str += 'class="txt ti_chen"/></td>';

  }

  $("#" + tableId).append($(str));

}

