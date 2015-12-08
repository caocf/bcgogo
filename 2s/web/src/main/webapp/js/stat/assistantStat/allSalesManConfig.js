var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;


function achievementNotify() {
  var num = $("#totalConfigNum").text();
  if (num * 1 > 0) {
    nsDialog.jConfirm("还有" + num + "个员工未支配部门,是否继续配置?", null, function (getVal) {
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
      url: "assistantStat.do?method=getProductConfigNum",
      dataType: "json",
      data:data,
      success: function(json) {
        if (json && json.success && json.data * 1 > 0) {
          nsDialog.jConfirm("还有" + json.data + "个商品未支配提成,是否继续配置?", null, function (getVal) {
            if (getVal) {
              $(this).dialog("close");
              window.location = "assistantStat.do?method=redirectProductConfig&type=unConfig";
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

function setAssistantDepartment() {
  if ($(".salesManHidden").size() <= 0) {
    return;
  }

  $("#dispatchDialog").dialog({
            resizable: false,
            title: "批量修改员工的部门",
            height: 150,
            width: 250,
            modal: true,
            closeOnEscape: false,
            close:function(){

            }
        });

}

$(document).ready(function() {

  $("#confirmBtn").bind("click", function () {

    if ($(".salesManHidden").size() <= 0) {
      return;
    }

    var departmentId = $("#setDepartmentName").find('option:selected').attr("departmentId");
    var departmentName = $("#setDepartmentName").val();

    var idList = "";
    for (var i = 0; i < $(".salesManHidden").size(); i++) {
      idList = idList + $("#salesManHidden" + i + "\\.salesManId").val() + ",";
    }
    idList.substr(0, idList.length - 1);

    var ajaxData = {
      idListStr: idList,
      departmentIdStr : departmentId,
      departmentName: departmentName
    };
    APP_BCGOGO.Net.asyncAjax({
      url:"assistantStat.do?method=updateSalesManInfoDepartment",
      data:ajaxData,
      dataType:"json",
      success:function(json) {
        if (json && json.success) {
          $("#dispatchDialog").dialog("close");
          $(".departmentSelect").val(departmentName);
          $("#totalConfigNum").text(json.data);
        }
      }
    });
  });
  $("#cancelBtn").bind("click", function () {
    $("#dispatchDialog").dialog("close");
  });


  $("#totalConfigNumSpan").live("click", function() {
    $("#totalNumDiv").css("display", "none");
    $("#totalSpanNumDiv").css("display", "block");

    $("#staffConfigAchievementTable tr:not(`:first)").remove();


    var data = {
      maxRows :25,
      startPageNo:1
    }
    var url = "assistantStat.do?method=getAssistantAchievementByPager";

    bcgogoAjaxQuery.setUrlData(url, data);
    bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {

      if (jsonStr && jsonStr != "") {
        drawStaffConfigAchievementTable(jsonStr);
        initPage(jsonStr, 'dynamicalConfigStaff', url, '', 'drawStaffConfigAchievementTable', '', '', data, '');
      }
    });
  });

  $("#totalNumSpan").live("click", function() {
    $("#totalNumDiv").css("display", "block");
    $("#totalSpanNumDiv").css("display", "none");

    $("#staffTable tr:not(`:first)").remove();

    var url = 'staffManage.do?method=getStaffByCondition',
        params = {startPageNo:1, maxRows:25};
    APP_BCGOGO.Net.asyncAjax({
      type:"POST",
      url:url,
      data:params,
      cache:false,
      dataType:"json",
      success:function (jsonStr) {
        if (jsonStr && jsonStr != "") {
          initPage(jsonStr, 'dynamicalStaff', url, '', 'drawStaffConfigTable', '', '', params, '');
          drawStaffConfigTable(jsonStr);
        }
      }
    });
  });


  $("#departmentName1,#status,#sex").change(function() {
    $("#searchStaff").click();
  })
      .click(function() {
        $(this).removeClass("txt");
      });


  $(".departmentSelect").live("change",
      function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
          return;
        }

        if ($(e.target).val() == "请选择") {
          return;
        }

        var id = $(e.target).attr("id").split("_")[0];

        var departmentId = $(e.target).find('option:selected').attr("departmentId");

        var departmentName = $(e.target).val();
        var ajaxData = {
          idListStr: id,
          departmentIdStr : departmentId,
          departmentName: departmentName
        };
        APP_BCGOGO.Net.asyncAjax({
          url:"assistantStat.do?method=updateSalesManInfoDepartment",
          data:ajaxData,
          dataType:"json",
          success:function(json) {
            if (json && json.success) {
              $("#totalConfigNum").text(json.data);
            }
          }
        });
        $(this).attr("lastValue", $(this).val());
      }).live("focus", function () {
        $(this).attr("lastValue", $(this).val());
      });

  $("#searchStaff")
      .click(function () {
        $("#totalNumDiv").css("display", "block");
        $("#totalSpanNumDiv").css("display", "none");

        var url = 'staffManage.do?method=getStaffByCondition',
            params = {startPageNo:1, maxRows:25},
            sex = $("#sex").val(), status = $("#status").val(), userStatus = $("#userStatus").val();
        switch (status) {
          case "离职":
            status = "DEMISSION";
            break;
          case "在职":
            status = "INSERVICE";
            break;
          case "试用":
            status = "ONTRIAL";
            break;
          default:
            status = "";
            break;
        }
        if (sex == "男") sex = "MALE";
        else if (sex == "女") sex = "FEMALE";
        else sex = "";
        params['departmentName'] = $("#departmentName1").val();
        params['userGroupName'] = $("#userGroupName1").val();
        params['userGroupId'] = $("#userGroupName1").attr("userGroupId");
        params['name'] = $("#name").val();
        params['sex'] = sex;
        params['status'] = status;
        if (userStatus != null && userStatus != '') {
          params['userStatus'] = userStatus;
        }
        if ($("#sortStr").val() != '') {
          params['sortStr'] = $("#sortStr").val();
        }
        $("#staffTable tr:not(`:first)").remove();
        APP_BCGOGO.Net.asyncAjax({
          type:"POST",
          url:url,
          data:params,
          cache:false,
          dataType:"json",
          success:function (data) {
            initPage(data, 'dynamicalStaff', url, '', 'drawStaffConfigTable', '', '', params, '');
            drawStaffConfigTable(data);
          }
        });
      });

  if ($("#unConfig").val() == "unConfig") {
    $("#totalConfigNumSpan").click();
  } else {
    $("#totalNumSpan").click();
  }
});


//侦听当前click事件
var preRow, currRow, currentRowIndex, currentColor, hasShow;
$(document).click(function(e) {
  var e = e || event;
  var target = e.srcElement || e.target;
});


function drawStaffConfigTable(data) {
  $("#staffTable tr:not(`:first)").remove();

  var uuid = GLOBAL.Util.generateUUID();

  var departmentData = {};

  APP_BCGOGO.Net.syncGet({
    url:"staffManage.do?method=getDepartmentDropList",
    data:{
      "uuid":uuid,
      "keyWord":"",
      "now":new Date()
    },
    dataType:"json",
    success:function (result) {
      if (result.data) {

        departmentData = result.data;


        if ($("#departmentName1").find('option').size() <= 2) {
          $("#departmentName1").find('option').not('#firstOption2').not('#second').remove();
          $("#setDepartmentName").find('option').not('#firstOption2').not('#second').remove();

          for (var i = 0; i < result.data.length; i++) {
            var option = "<option value='" + result.data[i].name + "' departmentId='" + result.data[i].idStr + "'>" + result.data[i].name + "</option>";
            $(option).appendTo($("#departmentName1"));
            $(option).appendTo($("#setDepartmentName"));
          }
        }

      }
    }
  });

  if (data == null || data == "" || data["results"] == null) {
    return;
  }

  var str = '<tr class="space"><td colspan="4"></td></tr>';
  $("#staffTable").append(str);
  $.each(data["results"], function (index, staff) {
    var salesManCode = (!staff.salesManCode ? "--" : staff.salesManCode),
        name = staff.name,
        departmentName = staff.departmentName ? staff.departmentName : "",
        departmentId = staff.departmentId ? staff.departmentId : "",
        userGroupName = staff.userGroupName ? staff.userGroupName : "",
        userStatus,
        salesManId = staff.idStr,

        status = staff.statusValue,

        userGroupId = staff.userGroupIdStr ? staff.userGroupIdStr : "",
        sex = staff.sexStr ? staff.sexStr : "";
    if (sex == 'MALE') {
      sex = '男';
    } else if (sex == 'FEMALE') {
      sex = '女';
    }
    if (staff.userNo && userGroupId != '') {
      if (staff.userStatusStr == "inActive") {
        userStatus = "禁用中";
      } else {
        userStatus = "启用中"
      }
    } else {
      userStatus = "未分配"
    }

    var tr = '<tr class="titBody_Bg"><input class="salesManHidden" id="salesManHidden' + index + '.salesManId" type="hidden" name="salesManId" value="' + salesManId + '">';
    tr += '<td style="padding-left:10px;" title="' + name + '">' + name + '</td>';
    tr += '<td title="' + sex + '">' + sex + '</td>';
    tr += '<td title="' + userGroupName + '">' + userGroupName + '</td>';
    tr += '<td title="' + status + '">' + status + '</td>';

    tr += '<td>' + '<select id="' + salesManId + '_' + departmentName + '" style="width:111px;" class="departmentSelect" name="departmentName">'


    if (departmentName == "" || departmentName == null) {
      var option = "<option value='请选择' departmentId=''>请选择</option>";
      tr += option;
    }
    for (var i = 0; i < departmentData.length; i++) {
      option = "<option value='" + departmentData[i].name + "' departmentId='" + departmentData[i].idStr + "'>" + departmentData[i].name + "</option>";
      tr += option;
    }
    tr += '</select>';
    tr += '<input type="hidden" class="departmentSelect"/></td>';

    tr += '</tr>';

    tr += '<tr class="titBottom_Bg"><td colspan="5"></td></tr>';
    $("#staffTable").append(tr);

    $("#userStatus").val('');
    $("#sortStr").val('');
  });

  $.each(data["results"], function (index, staff) {
    var departmentName = staff.departmentName ? staff.departmentName : "",
        salesManId = staff.idStr;

    if (departmentName == "" || departmentName == null) {
      return;
    }

    $("#" + salesManId + "_" + departmentName).val(departmentName)
  });
}


function drawStaffConfigAchievementTable(data) {
  $("#staffConfigAchievementTable tr:not(`:first)").remove();

  var uuid = GLOBAL.Util.generateUUID();

  var departmentData = {};

  APP_BCGOGO.Net.syncGet({
    url:"staffManage.do?method=getDepartmentDropList",
    data:{
      "uuid":uuid,
      "keyWord":"",
      "now":new Date()
    },
    dataType:"json",
    success:function (result) {
      if (result.data) {

        departmentData = result.data;

        if ($("#departmentName1").find('option').size() <= 2) {
          $("#departmentName1").find('option').not('#firstOption2').not('#second').remove();
          $("#setDepartmentName").find('option').not('#firstOption2').not('#second').remove();

          for (var i = 0; i < result.data.length; i++) {
            var option = "<option value='" + result.data[i].name + "' departmentId='" + result.data[i].idStr + "'>" + result.data[i].name + "</option>";
            $(option).appendTo($("#departmentName1"));
            $(option).appendTo($("#setDepartmentName"));
          }
        }
      }
    }
  });

  if (data == null || data == "" || data["results"] == null) {
    return;
  }

  var str = '<tr class="space"><td colspan="4"></td></tr>';
  $("#staffConfigAchievementTable").append(str);
  $.each(data["results"], function (index, staff) {
    var salesManCode = (!staff.salesManCode ? "--" : staff.salesManCode),
        name = staff.name,
        departmentName = staff.departmentName ? staff.departmentName : "",
        departmentId = staff.departmentId ? staff.departmentId : "",
        userGroupName = staff.userGroupName ? staff.userGroupName : "",
        userStatus,
        salesManId = staff.idStr,

        status = staff.statusValue,

        userGroupId = staff.userGroupIdStr ? staff.userGroupIdStr : "",
        sex = staff.sexStr ? staff.sexStr : "";
    if (sex == 'MALE') {
      sex = '男';
    } else if (sex == 'FEMALE') {
      sex = '女';
    }
    if (staff.userNo && userGroupId != '') {
      if (staff.userStatusStr == "inActive") {
        userStatus = "禁用中";
      } else {
        userStatus = "启用中"
      }
    } else {
      userStatus = "未分配"
    }

    var tr = '<tr class="titBody_Bg"><input class="salesManHidden" id="salesManHidden' + index + '.salesManId" type="hidden" name="salesManId" value="' + salesManId + '">';
    tr += '<td style="padding-left:10px;" title="' + name + '">' + name + '</td>';
    tr += '<td title="' + sex + '">' + sex + '</td>';
    tr += '<td title="' + status + '">' + status + '</td>';

    tr += '<td>' + '<select id="' + salesManId + '_' + departmentName + '" style="width:111px;" class="departmentSelect" name="departmentName">'


    if (departmentName == "" || departmentName == null) {
      var option = "<option value='请选择' departmentId=''>请选择</option>";
      tr += option;
    }
    for (var i = 0; i < departmentData.length; i++) {
      option = "<option value='" + departmentData[i].name + "' departmentId='" + departmentData[i].idStr + "'>" + departmentData[i].name + "</option>";
      tr += option;
    }
    tr += '</select>';
    tr += '<input type="hidden" class="departmentSelect"/></td>';

    tr += '</tr>';

    tr += '<tr class="titBottom_Bg"><td colspan="4"></td></tr>';
    $("#staffConfigAchievementTable").append(tr);

    $("#userStatus").val('');
    $("#sortStr").val('');
  });

  $.each(data["results"], function (index, staff) {
    var departmentName = staff.departmentName ? staff.departmentName : "",
        salesManId = staff.idStr;

    if (departmentName == "" || departmentName == null) {
      return;
    }

    $("#" + salesManId + "_" + departmentName).val(departmentName)
  });
}