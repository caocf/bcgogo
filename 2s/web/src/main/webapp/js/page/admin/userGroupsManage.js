$(function () {
    //页面逻辑
    $("#addUserGroup").click(function () {
        openOrAssign('permissionManager.do?method=showPermissionConfig');
    });

    $("#searchUserGroup").click(function () {
        var url = 'userGroupsManage.do?method=getUserGroupsByCondition',
            params = {startPageNo:1, maxRows:15},
            userGroupVariety = $("#userGroupVariety").val(),
            userGroupVariety2 = $("#hiddenCondition").val();
        if (userGroupVariety == "系统默认") userGroupVariety = "SYSTEM_DEFAULT";
        else if (userGroupVariety == "自定义") userGroupVariety = "CUSTOM";
        else userGroupVariety = "";
        if(userGroupVariety == "SYSTEM_DEFAULT") {
          if($("#hiddenCondition").val() == 'custom') {
             $("#userGroupTable tr").not('.divSlip').remove();
              return;
          }
        } else if(userGroupVariety == "CUSTOM") {
          if($("#hiddenCondition").val() == 'countSystemDefault') {
             $("#userGroupTable tr").not('.divSlip').remove();
              return;
          }
        }

        params['name'] = $("#userGroupName").val();
        params['userGroupNo'] = $("#userGroupNo").val();
        params['variety'] = userGroupVariety;
        params['variety2'] = userGroupVariety2;
        APP_BCGOGO.Net.asyncAjax({
            type:"POST",
            url:url,
            data:params,
            cache:false,
            dataType:"json",
            success:function (data) {
                initPages(data, 'usergroups', url, '', 'drawUserGroupTable', '', '', params, '');
                drawUserGroupTable(data);
            }
        });
    });

    $("a[id$='.userList']").live("click", function (e) {
        var $tr = $(e.target).parent().parent(),
            userGroupName = $tr.find("td[class='userGroupName']").html(),
            userGroupId = $tr.find("input[name='userGroupId']").val();
        openOrAssign('staffManage.do?method=showStaffManagePage&userGroupName=' + userGroupName + "&userGroupId=" + userGroupId);
    });

    $("a[id$='.updateUserGroup']").live("click", function (e) {
        var $tr = $(e.target).parent().parent(),
            userGroupId = $tr.find("input[name='userGroupId']").val();
        openOrAssign('permissionManager.do?method=showPermissionConfig&userGroupId=' + userGroupId);
    });

    $("a[id$='.showPermission']").live("click", function (e) {
        var $tr = $(e.target).parent().parent(),
            userGroupId = $tr.find("input[name='userGroupId']").val();
        openOrAssign('permissionManager.do?method=showPermissionConfig&userGroupId=' + userGroupId + "&defaultSysUserGroup=true");
    });

    $("a[id$='.copyPermission']").live("click", function (e) {
        var $tr = $(e.target).parent().parent(),
            userGroupId = $tr.find("input[name='userGroupId']").val();
        openOrAssign(('permissionManager.do?method=showPermissionConfig&userGroupId=' + userGroupId + "&copyPermission=true"));
    });

    $("a[id$='.delete']").live("click", function (e) {
        nsDialog.jConfirm("确认删除职位？", null, function (yes) {
        if(yes) {
          var $tr = $(e.target).parent().parent(),
        userGroupId = $tr.find("input[name='userGroupId']").val();

        APP_BCGOGO.Net.syncAjax({
            url:"userGroupsManage.do?method=deleteUserGroup",
            data:{
                userGroupId:userGroupId
            },
            dataType:"json",
            success:function (result) {
                if (result.success) {
                    nsDialog.jAlert("删除成功！", "", function () {
                        $("#searchUserGroup").click();
                    });
                } else {
                    nsDialog.jAlert(result.message);
                }
            }
        });
        }

        });
    });

    $(".blue_col").click(function(){
        $("#hiddenCondition").val(this.id);
        $("#searchUserGroup").click();
    });

    $(".quanHelp").click(function(){
       window.location.href = 'help.do?method=toHelper&title=staffConfigHelper';
    });
});

//侦听当前click事件
var preRow, currRow, currentRowIndex, currentColor, hasShow;
$(document).click(function(e) {
  var e = e || event;
  var target = e.srcElement || e.target;
  if (target.tagName != 'TD' && target.tagName != 'TR' || $(target).parents('tr').attr('id') == 'table_title' || $(target).parents('.upInfo').length > 0) {
    if ($(target).parents('#upInfo').attr('id') != 'upInfo' && $(e.target).parents(".ui-dialog").find("div[id='jConfirm']").length <= 0 && $(e.target).parents(".ui-dialog").find("div[id='jAlert']").length <= 0) {
      var objStr = '#userGroupTable tr:eq(' + currentRowIndex + ') td';
        $('#upInfo').slideUp('fast');
        hasShow = false;
        preRow = undefined;
        currRow = undefined;
    }
  } else {
  preRow = currRow;
  currRow = $(target).parents('tr').prevAll().length;
  if (preRow == currRow) {

    $('.upInfo').slideUp('fast');
    hasShow = false;
    preRow = undefined;
    currRow = undefined;
  } else {
    if ($(target).attr('id') != 'upInfo' && $(target).parents('#upInfo').length <= 0) {
    currentRowIndex = $(target).parents('tr').prevAll().length;
    var top = $('#userGroupTable tr:eq(' + currentRowIndex + ')').offset().top + $('#userGroupTable tr:eq(' + currentRowIndex + ')').outerHeight();
    var left = $('#userGroupTable tr:eq(' + currentRowIndex + ')').offset().left;

    hasShow = true;
    jQuery(".upInfo").slideUp('fast', function() {
      bindUserGroupDetails(currentRowIndex);
      $('.upInfo').css({
        'position': 'absolute',
        'z-index': '3',
        'top': top + 'px',
        'left': left + 'px'
      }).slideDown('fast');
    });
    }
  }
  }
});

function drawUserGroupTable(data) {
    $("#countSystemDefault").html(data[1]);
    $("#custom").html(data[2]);
    $("#all").html(data[1]+data[2]);
    $("#userGroupTable tr:not(:first)").remove();
    $.each(data[0], function (index, group) {
        var userGroupNo = (!group.userGroupNo ? "--" : group.userGroupNo),
            isSysDefault = group.variety && group.variety == "SYSTEM_DEFAULT",
            variety = ( isSysDefault ? "系统默认" : "自定义"),
            name = group.name,
            id = group.idStr,
            memo = group.memo;
        var tr = '<tr class="table-row-original"><input type="hidden" name="userGroupId" value="' + id + '">';
        tr += '<td style="padding-left:10px;">' + (index + 1) + '</td>';
        tr += '<td class="userGroupName">' + name + '</td>';
        tr += '<td>' + variety + '</td>';
        tr += '<td  title="' + memo + '">' + memo + '</td>';
        tr += '<td>';
        if (APP_BCGOGO.Permission.SystemSetting.PermissionManager.CopyPermission) {
            tr += '<a class="clickNum" href="#" id="userGroup[' + (index + 1) + '].copyPermission">复制权限</a>&nbsp;';
        }

        if (isSysDefault) {
            tr += '<a class="clickNum" href="#" id="userGroup[' + (index + 1) + '].showPermission">查看权限</a>';
        } else {
            if (APP_BCGOGO.Permission.SystemSetting.PermissionManager.UpdatePermission) {
                tr += '<a class="clickNum" href="#" id="userGroup[' + (index + 1) + '].updateUserGroup">修改</a>&nbsp;';
            }
            if (APP_BCGOGO.Permission.SystemSetting.PermissionManager.DeletePermission) {
                tr += '<a class="clickNum" id="userGroup[' + (index + 1) + '].delete">删除</a>&nbsp;';
            }
        }
        tr += '</td></tr>';
        $("#userGroupTable").append(tr);

        tableUtil.tableStyle('#userGroupTable', '.divSlip');
    });
    $("#hiddenCondition").val('');
}

function bindUserGroupDetails(index) {
  var url = "staffManage.do?method=getStaffByCondition";
  var userGroupId = $('#userGroupTable tr:eq(' + index + ') input[name=userGroupId]').val();
  var userGroupName = $('#userGroupTable tr:eq(' + index + ') td[class=userGroupName]').html();
  var data = {
        userGroupId:userGroupId,
        userGroupName:userGroupName,
        startPageNo:1,
        maxRows:15
  }
  APP_BCGOGO.Net.asyncAjax({
        url: url,
        type: "POST",
        cache: false,
        data: data,
        dataType: "json",
        success: function(jsonObj) {
          $("#tabUserTable tr").not('.bgTit').remove();
          var number = 1;
          $.each(jsonObj.results, function(index,staff){
            var userStatus,
            sex = staff.sexStr ? staff.sexStr : "",
            departmentName = staff.departmentName ? staff.departmentName : "",
            userGroupName = staff.userGroupName ? staff.userGroupName : "",
            mobile = staff.mobile ? staff.mobile : "",
            careerDate = staff.careerDateStr ? staff.careerDateStr : "",
            userId = staff.userIdStr ? staff.userIdStr : "",
            status = staff.statusValue;
            if(sex == 'MALE') {
              sex = '男';
            } else if(sex == 'FEMALE') {
              sex = '女';
            }
            if (staff.userNo) {
              if (staff.userStatusStr == "inActive") {
                userStatus = "禁用中";
              } else {
                userStatus = "启用中"
              }
            } else {
              userStatus = "未分配"
            }
            if(staff.userNo && staff.userStatusStr == "active") {
              var tr = '<tr><input type="hidden" name="userStatus" value="' + staff.userStatusStr + '"><input type="hidden" name="userId" value="' + userId + '"> <td style="padding-left:6px;">' + number + '</td><td>' + staff.name
                     + '</td> <td>' + staff.userNo + '</td> <td>' + userStatus + '</td> <td>' +
                     sex + '</td> <td>' + departmentName + '</td> <td>' + userGroupName + '</td> <td>' +
                     mobile + '</td> <td>' + careerDate + '</td> <td>' + status + '</td>';
              if(staff.userType != 'SYSTEM_CREATE') {
                  tr += '<td><a class="blue_col">禁用账户</a></td></tr>'
              } else {
                  tr += '<td></td></tr>';
              }

              $("#tabUserTable").append(tr);
              number++;
            }

          });

        $("#upInfo .blue_col").live("click", function (e) {
        var $tr = $(e.target).parent().parent(), message,
            userStatus = $tr.find("input[name='userStatus']").val(),
            userId = $tr.find("input[name='userId']").val();
        if (userStatus == "active") {
            userStatus = 'inActive';
            message = "禁用";
        } else {
            userStatus = 'active';
            message = "启用";
        }
        nsDialog.jConfirm("确认" + message + "用户？", null, function (yes) {
            if (yes) {
                APP_BCGOGO.Net.syncAjax({
                    url:"user.do?method=updateUsersStatus",
                    data:{
                        status:userStatus,
                        ids:userId
                    },
                    dataType:"json",
                    success:function (result) {
                        if (result.success) {
                            nsDialog.jAlert(message + "账号成功！", "", function () {
                                $tr.remove();
                            });
                        }
                    }
                });
            }
        });

    });
        }
  });
}