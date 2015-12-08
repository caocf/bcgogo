$(function () {
    var isSalesManCodeDuplicated = false, isUserNoDuplicated = false;
    $("#searchStaff")
        .click(function () {
            var url = 'staffManage.do?method=getStaffByCondition',
                params = {startPageNo: 1, maxRows: 15},
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
            APP_BCGOGO.Net.asyncAjax({
                type: "POST",
                url: url,
                data: params,
                cache: false,
                dataType: "json",
                success: function (data) {
                    initPage(data, '_staff_manage', url, '', 'drawStaffTable', '', '', params, '');
                    drawStaffTable(data);
                }
            });
        });

    $("#addNewStaff").click(function () {
        bcgogo.checksession({"parentWindow": window.parent, 'iframe_PopupBox': $("#iframe_PopupBox")[0],
            'src': 'staffManage.do?method=getSaleManInfoById&salesManId=&ts='
                + new Date().getTime()})
    });

    $("a[id$='.update']").live("click", function (e) {
        var $tr = $(e.target).parent().parent(),
            salesManId = $tr.find("input[name='salesManId']").val();
        bcgogo.checksession({"parentWindow": window.parent, 'iframe_PopupBox': $("#iframe_PopupBox")[0],
            'src': 'staffManage.do?method=getSaleManInfoById&salesManId=' + salesManId + '&ts=' + new Date().getTime()})
    });

    $("a[id$='.resetPass']").live("click", function (e) {
        var $tr = $(e.target).parent().parent(),
            userId = $tr.find("input[name='userId']").val();
        nsDialog.jConfirm("确认密码重置？", null, function (yes) {
            if (yes) {
                APP_BCGOGO.Net.syncAjax({
                    url: "user.do?method=resetUserPassword",
                    data: {
                        userId: userId
                    },
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                            nsDialog.jAlert("密码重置成功！初始密码为123456！");
                        }
                    }
                });
            }
        });
    });

    $("a[id$='.delete']").live("click", function (e) {
        nsDialog.jConfirm("确认删除用户？", null, function (yes) {
            if (yes) {
                var $tr = $(e.target).parent().parent(),
                    salesManId = $tr.find("input[name='salesManId']").val(),
                    userId = $tr.find("input[name='userId']").val();
                APP_BCGOGO.Net.syncAjax({
                    url: "staffManage.do?method=deleteStaff",
                    data: {
                        salesManId: salesManId,
                        userId: userId
                    },
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                            nsDialog.jAlert("删除成功！", "", function () {
                                $("#searchStaff").click();
                            });
                        }
                    }
                });
            }
        });
    });

    $("a[id$='.updateUsersStatus']").live("click", function (e) {
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
                    url: "user.do?method=updateUsersStatus",
                    data: {
                        status: userStatus,
                        ids: userId
                    },
                    dataType: "json",
                    success: function (result) {
                        if (result.success) {
                            nsDialog.jAlert(message + "账号成功！", "", function () {
                                $("#searchStaff").click();
                            });
                        }
                    }
                });
            }
        });

    });

    $("a[id$='.allocatedAccount']").live("click", function (e) {
        var $tr = $(e.target).parent().parent(),
            salesManId = $tr.find("input[name='salesManId']").val(),
            userGroupId = $tr.find("input[name='userGroupId']").val(),
            userId = $tr.find("input[name='userId']").val(),
            userNo = $tr.find('td:eq(2)').html();
        $("#salesManId").val(salesManId);
        $("#userGroupId").val(userGroupId);
        $("#userId").val(userId);
        $("#userNo").val(userNo);
//          $("#dialog-form").dialog("open");
        $("#iframe_PopupBox").css("display", "block");
        Mask.Login();
        $("#iframe_PopupBox").attr("src", "admin/staffManage/distributionAccount.jsp");

    });

    $("#dialog-form").dialog({
        autoOpen: false,
        height: 200,
        width: 250,
        modal: true,
        open: function () {
            var $this = $(this);
            $this.keypress(function (event) {
                if (event.keyCode == $.ui.keyCode.ENTER) {
                    $this.parent().find('.ui-dialog-buttonpane button:first').click();
                    return false;
                }
            });
        },
        buttons: {
            "保存": function () {
                var me = this;
                if (!$("#userNo").val()) {
                    nsDialog.jAlert("请输入账户名！");
                    return;
                }
                APP_BCGOGO.Net.syncAjax({
                    url: "user.do?method=checkUserNo",
                    data: {
                        userNo: $("#userNo").val()
                    },
                    dataType: "json",
                    success: function (result) {
                        if (result.isDuplicate) {
                            nsDialog.jAlert("账户名已被使用！");
                        } else {
                            nsDialog.jConfirm("确认为该用户分配账户？", null, function (yes) {
                                if (yes) {
                                    APP_BCGOGO.Net.syncAjax({
                                        url: "user.do?method=allocatedUserNo",
                                        data: {
                                            salesManId: $("#salesManId").val(),
                                            userNo: $("#userNo").val()
                                        },
                                        dataType: "json",
                                        success: function (result) {
                                            if (result.success) {
                                                nsDialog.jAlert("密码重置成功！初始密码为123456！", "", function () {
                                                    $("#searchStaff").click();
                                                    $(me).dialog("close");
                                                });
                                            } else {
                                                nsDialog.jAlert(result.message, "", function () {
                                                    $(me).dialog("close");
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            },
            "取消": function () {
                $(this).dialog("close");
            }
        },
        close: function () {
            $("#userNo").val("");
        }
    });
    $("#all,#active,#inActive").click(function () {
        $("#userStatus").val(this.id);
        $("#searchStaff").click();
    });

    $("#status").change(function () {
        $("#firstOption3").remove();
        $("#searchStaff").click();
    })
        .click(function () {
            $(this).removeClass("txt");
        });

    $("#sex").change(function () {
        $("#searchStaff").click();
    })
        .click(function () {
            $(this).removeClass("txt");
        });


    $("#resetButtion").click(function () {
      var id = $("#id").val();
      if(G.Lang.isEmpty(id)){
        return;
      }
      drawSalesManInfo(id);
    });

    $("#updateSalesMan").click(function () {
        if ($.trim($("#salesManName").val()) == '') {
            alert("姓名不能为空");
            return;
        }

        if ($("#userNo2").attr("id") != undefined) {
            if ($.trim($("#userNo2").val()) == '') {
                alert("账户名不能为空,若您想禁用该账号，请进行禁用账号操作！");
                return;
            }
        }

        if (checkFormData()) {
            var id = $("#id").val();
            var name = $.trim($("#salesManName").val());
            var mobile = $("#mobile").val();
            var departmentName = $("#department").val();
            var departmentId = $("#departmentId2").val();
            var identityCard = $("#identityCard").val();
            var qq = $("#qq").val();
            var salesManCode = $("#salesManCode").val();
            var salary = $("#salary").val();
            var allowance = $("#allowance").val();
            var careerDateStr = $("#careerDate").val();
            var email = $("#email").val();
            var sexStr;
            if ($("#male").attr("checked")) {
                sexStr = 'MALE';
            } else {
                sexStr = 'FEMALE';
            }
            var statusValue;
            if ($("#INSERVICE").attr("checked")) {
                statusValue = 'INSERVICE';
            } else if ($("#DEMISSION").attr("checked")) {
                statusValue = 'DEMISSION';
            } else {
                statusValue = 'ONTRIAL';
            }

            var userNo = $("#userNo2").val();
            var userGroupName = $("#userGroup").val();
            var userGroupId = $("#userGroup2").val();
            var memo = $("#memo").val();

            var washBeautyAchievement = $("#washBeautyAchievement").length <= 0 ? "" : $("#washBeautyAchievement").val();
            var serviceAchievement = $("#serviceAchievement").length <= 0 ? "" : $("#serviceAchievement").val();
            var salesAchievement = $("#salesAchievement").length <= 0 ? "" : $("#salesAchievement").val();
            var salesProfitAchievement = $("#salesProfitAchievement").length <= 0 ? "" : $("#salesProfitAchievement").val();
            var memberNewType = $("#memberNewType").length <= 0 ? "" : $("#memberNewType").val();
            var memberNewAchievement = $("#memberNewAchievement").length <= 0 ? "" : $("#memberNewAchievement").val();
            var memberRenewType = $("#memberRenewType").length <= 0 ? "" : $("#memberRenewType").val();
            var memberReNewAchievement = $("#memberReNewAchievement").length <= 0 ? "" : $("#memberReNewAchievement").val();


            var ajaxData = {
                id: id,
                name: name,
                mobile: mobile,
                departmentName: departmentName,
                departmentId: departmentId,
                identityCard: identityCard,
                qq: qq,
                salesManCode: salesManCode,
                salary: salary,
                allowance: allowance,
                careerDateStr: careerDateStr,
                email: email,
                sexStr: sexStr,
                statusValue: statusValue,
                userNo: userNo,
                userGroupName: userGroupName,
                userGroupId: userGroupId,
                memo: memo,
                washBeautyAchievement: washBeautyAchievement,
                serviceAchievement: serviceAchievement.replace("%", ""),
                salesAchievement: salesAchievement.replace("%", ""),
                salesProfitAchievement: salesProfitAchievement.replace("%", ""),
                memberNewType: memberNewType,
                memberNewAchievement: memberNewAchievement.replace("%", ""),
                memberRenewType: memberRenewType,
                memberReNewAchievement: memberReNewAchievement.replace("%", "")

            };
            APP_BCGOGO.Net.asyncAjax({
                url: "staffManage.do?method=saveSalesManInfo",
                data: ajaxData,
                dataType: "json",
                success: function (json) {
                    if (json.result == 'success') {
                        window.location = 'staffManage.do?method=showStaffManagePage';
                    }

                }
            });
        }


    });
    $("#userGroup").change(function () {
        $("#userGroup2").val($("#userGroup").find('option[value="' + $("#userGroup").val() + '"]').attr("userGroupId"));
    });
    $("#department").change(function () {
        $("#departmentId2").val($("#department").find('option[value="' + $("#department").val() + '"]').attr("departmentId"));
    });

    $("#nameSort,#userNoSort").click(function () {
        if ($(this).hasClass('ascending')) {
            $("#sortStr").val(this.id + "Ascending");
            $(this).removeClass('ascending').addClass('descending');
        } else {
            $("#sortStr").val(this.id + "Descending");
            $(this).removeClass('descending').addClass('ascending');
        }
        $("#searchStaff").click();
    });

    $(".quanHelp").click(function () {
        window.location.href = 'help.do?method=toHelper&title=staffConfigHelper';
    });

    $("#mobile,#qq").keyup(function (e) {
        e.target.value = dataFilter.replace(e.target.value, regExpPattern.notDigital, "");
    });

    $("#email").keyup(function (e) {
        e.target.value = dataFilter.replace(e.target.value, regExpPattern.emailPattern, "");
    });

    $("#salesManCode")
        .blur(function (e) {
            if (e.target.value) {
                APP_BCGOGO.Net.asyncAjax({
                    url: "staffManage.do?method=checkSalesManCode",
                    data: {
                        salesManCode: e.target.value,
                        salesManId: $("#id").val()
                    },
                    dataType: "json",
                    success: function (isDuplicate) {
                        if (isDuplicate) {
                            nsDialog.jConfirm("工号重复，请重新输入！", "", function (yes) {
                                if (yes) {
                                    $("#salesManCode").focus();
                                    $("#salesManCode").select();
                                    isSalesManCodeDuplicated = true;
                                } else {
                                    $("#salesManCode").val("");
                                }
                            });
                        } else {
                            isSalesManCodeDuplicated = false;
                        }
                    }
                });
            } else {
                isSalesManCodeDuplicated = false;
            }
        })
        .keyup(function () {
            isSalesManCodeDuplicated = false;
        });

    $("#userNo2").live('blur', function (e) {
        if (e.target.value) {
            APP_BCGOGO.Net.asyncAjax({
                url: "user.do?method=checkUserNo",
                data: {
                    userNo: e.target.value,
                    salesManId: $("#id").val()
                },
                dataType: "json",
                success: function (result) {
                    if (result.isDuplicate) {
                        nsDialog.jConfirm("账号重复，请重新输入！", "", function (yes) {
                            if (yes) {
                                $("#userNo").focus();
                                $("#userNo").select();
                            } else {
                                $("#userNo").val("");
                            }
                            isUserNoDuplicated = true;
                        });
                    } else {
                        isUserNoDuplicated = false;
                    }
                }
            });
        } else {
            isUserNoDuplicated = false;
        }
    });
    $("#salary,#allowance,#washBeautyAchievement,#serviceAchievement,#salesAchievement," +
        "#salesProfitAchievement,#memberNewAchievement,#memberReNewAchievement").live("keyup blur", function (event) {
        if (event.type == "focusout")
            event.target.value = APP_BCGOGO.StringFilter.inputtedPriceFilter(event.target.value);
        else if (event.type == "keyup")
            if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value)) {
                event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value);
            }
    });

    $("#salary,#allowance").live("keyup blur", function (event) {
        if (event.type == "focusout")
            event.target.value = App.StringFilter.inputtedPriceFilter(event.target.value);
        else if (event.type == "keyup")
            if (event.target.value != App.StringFilter.inputtingPriceFilter(event.target.value)) {
                event.target.value = App.StringFilter.inputtingPriceFilter(event.target.value);
            }
    });
    $("#careerDate")
        .bind("click", function () {
            $(this).blur();
        })
        .datepicker({
            "numberOfMonths": 1,
            "showButtonPanel": true,
            "changeYear": true,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": ""
        });

    function checkFormData() {

        if ($("#mobile").val()) {
            if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#mobile").val())) {
                alert("请填写正确的手机号！");
                return false;
            }
        } else {
            alert("手机号不能为空！");
            return false;
        }

        if ($("#email").val() && !APP_BCGOGO.Validator.stringIsEmail($("#email").val())) {
            alert("邮箱格式错误，请确认后重新输入！");
            $("#email").focus();
            return false;
        }
        if ($("#identityCard").val()) {
            var result = idCardValidate.checkCardId($("#identityCard").val());
            if (!result.success) {
                alert(result.message);
                $("#identityCard").focus();
                return false;
            }

        }

        if (!$("#department").val()) {
            alert("请选择部门！");
            $("#department").focus();
            return false;
        }

        if (isSalesManCodeDuplicated) {
            alert("工号重复，请重新输入！");
            return false;
        }

        if (isUserNoDuplicated) {
            alert("账号重复，请重新输入！");
            return false;
        }
        return true;
    }

    $("#memberNewType").live("change",
        function (e) {
          if ($(e.target).attr("lastValue") == $(e.target).val()) {
            return;
          }
          $(this).attr("lastValue", $(this).val());

          if ($(this).val() == "CARD_AMOUNT") {
            $("#memberNewAchievement").val($("#memberNewAchievement").val().replace("%", ""));
          } else if ($(this).val() == "CARD_TOTAL") {
            if (G.isNotEmpty($("#memberNewAchievement").val())) {
              $("#memberNewAchievement").val($("#memberNewAchievement").val().replace("%", "") + "%");
            }
          }

          if (stringUtil.isEmpty($("#memberNewAchievement").val())) {
            return;
          }
          if (stringUtil.isNotEmpty($("#memberReNewAchievement").val())) {
            return;
          }

          $("#memberReNewAchievement").val($("#memberNewAchievement").val());
          $("#memberRenewType").val($("#memberNewType").val());

        }).live("focus", function () {
          $(this).attr("lastValue", $(this).val());
        });

    $("#memberNewAchievement").live('blur',function (e) {
      if (e.target.value) {
        if (stringUtil.isEmpty($("#memberNewAchievement").val())) {
          return;
        }

        if ($("#memberNewType").val() == "CARD_AMOUNT") {
          $(this).val($(this).val().replace("%", ""));
        } else if ($("#memberNewType").val() == "CARD_TOTAL") {
          $(this).val($(this).val().replace("%", "") + "%");
        }

        if (stringUtil.isNotEmpty($("#memberReNewAchievement").val())) {
          return;
        }

        $("#memberReNewAchievement").val($("#memberNewAchievement").val());
        $("#memberRenewType").val($("#memberNewType").val());
      }

    }).live("focus", function () {
          $(this).val($(this).val().replace("%", ""));
        });

  $("#serviceAchievement,#salesAchievement,#salesProfitAchievement").live("blur",
        function (e) {

          if (G.isEmpty($(this).val())) {
            return;
          }
          $(this).val($(this).val().replace("%", "") + "%");
          $(this).attr("lastValue", $(this).val());
        }).live("focus", function () {
          $(this).attr("lastValue", $(this).val());
          $(this).val($(this).val().replace("%", ""));
        });

    $("#memberReNewAchievement").live("blur",
        function (e) {

          if (G.isEmpty($(this).val())) {
            return;
          }
          if ($("#memberRenewType").val() == "CARD_AMOUNT") {
            $(this).val($(this).val().replace("%", ""));
          } else if ($("#memberRenewType").val() == "CARD_TOTAL") {
            $(this).val($(this).val().replace("%", "") + "%");
          }
        }).live("focus", function () {
          $(this).val($(this).val().replace("%", ""));
        });


   $("#memberRenewType").live("change",
       function (e) {
         if ($(e.target).attr("lastValue") == $(e.target).val()) {
           return;
         }
         $(this).attr("lastValue", $(this).val());

         if ($(this).val() == "CARD_AMOUNT") {
           $("#memberReNewAchievement").val($("#memberReNewAchievement").val().replace("%", ""));
         } else if ($(this).val() == "CARD_TOTAL") {
           if(G.isNotEmpty($("#memberReNewAchievement").val())){
             $("#memberReNewAchievement").val($("#memberReNewAchievement").val().replace("%", "") + "%");
           }
         }

       }).live("focus", function () {
         $(this).attr("lastValue", $(this).val());
       });


});

//侦听当前click事件
var preRow, currRow, currentRowIndex, currentColor, hasShow;
$(document).click(function (e) {
    var e = e || event;
    var target = e.srcElement || e.target;
    if (target.tagName != 'TD' && target.tagName != 'TR' || $(target).parents('tr').attr('id') == 'table_title' || $(target).parents('#upInfo').length > 0) {
        if ($(target).parents('#upInfo').attr('id') != 'upInfo' && $(e.target).parents(".ui-dialog").find("div[id='jConfirm']").length <= 0 && $(e.target).parents(".ui-datepicker").attr("id") == undefined) {
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
            var objStr = '#staffTable tr:eq(' + preRow + ') td';
            hasShow = false;
            if ($(target).attr('id') != 'upInfo' && $(target).parents('#upInfo').length <= 0) {
                currentColor = $(target).parents('tr').css('background-color');
                currentRowIndex = $(target).parents('tr').prevAll().length;
                var top = $('#staffTable tr:eq(' + currentRowIndex + ')').offset().top + $('#staffTable tr:eq(' + currentRowIndex + ')').outerHeight();
                var left = $('#staffTable tr:eq(' + currentRowIndex + ')').offset().left;
                var objStr = '#staffTable tr:eq(' + currentRowIndex + ') td';
                hasShow = true;
                jQuery(".upInfo").slideUp('fast', function () {
                    bindSalesManDetails(currentRowIndex);
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

function drawStaffTable(data) {
    $("#all").html(data.totals.countStaffAll);
    $("#active").html(data.totals.countStaffActive);
    $("#inActive").html(data.totals.countStaffInactive);
    $("#staffTable tr:not(`:first)").remove();
    $.each(data["results"], function (index, staff) {
        var salesManCode = (!staff.salesManCode ? "--" : staff.salesManCode),
            isBoss = staff.userGroupName && staff.userGroupName == "老板/财务", //todo 系统默认分配账号管理
            hasUserNo = staff.userNo && staff.userIdStr ? true : false,
            name = staff.name,
            salary = staff.salary ? staff.salary : "",
            allowance = staff.allowance ? staff.allowance : "",
            identityCard = staff.identityCard ? staff.identityCard : "",
            departmentName = staff.departmentName ? staff.departmentName : "",
            userGroupName = staff.userGroupName ? staff.userGroupName : "",
            memo = staff.memo ? staff.memo : "",
            mobile = staff.mobile ? staff.mobile : "",
            userNo = staff.userNo ? staff.userNo : "--",
            careerDate = staff.careerDateStr ? staff.careerDateStr : "",
            salesManId = staff.idStr,
            userType = staff.userType,
            userStatus,
            status = staff.statusValue,
            userId = staff.userIdStr ? staff.userIdStr : "",
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
        var tr = '<tr class="table-row-original"><input type="hidden" name="salesManId" value="' + salesManId + '"><input type="hidden" name="userStatus" value="' + staff.userStatusStr + '"><input type="hidden" name="userId" value="' + userId + '"><input type="hidden" name="userGroupId" value="' + userGroupId + '">';
        tr += '<td style="padding-left:10px;">' + (index + 1) + '</td>';
        tr += '<td title="' + name + '">' + name + '</td>';
        tr += '<td title="' + userNo + '">' + userNo + '</td>';
        tr += '<td title="' + userStatus + '">' + userStatus + '</td>';
        tr += '<td title="' + sex + '">' + sex + '</td>';
        tr += '<td title="' + departmentName + '">' + departmentName + '</td>';
        tr += '<td title="' + userGroupName + '">' + userGroupName + '</td>';
        tr += '<td title="' + mobile + '">' + mobile + '</td>';
        tr += '<td title="' + careerDate + '">' + careerDate + '</td>';
        tr += '<td title="' + status + '">' + status + '</td>';
        tr += '<td>';

        if (APP_BCGOGO.Permission.SystemSetting.StaffManager.DeleteStaff) {
            if (userType != "SYSTEM_CREATE") {    //注册生成的不能删除
                tr += '<a class="clickNum" id="staff[' + (index + 1) + '].delete">删除</a>&nbsp;';
            }
        }

        if (!hasUserNo || userGroupId == '') {
            if (APP_BCGOGO.Permission.SystemSetting.StaffManager.AllocatedAccountStaff) {
                tr += '<a class="clickNum" id="staff[' + (index + 1) + '].allocatedAccount">分配账号</a>&nbsp;';
            }
        } else {
            if (userType != "SYSTEM_CREATE" && hasUserNo && APP_BCGOGO.Permission.SystemSetting.StaffManager.EnableDisableStaff) {
                if (staff.userStatusStr == "active") {
                    tr += '<a class="clickNum" id="staff[' + (index + 1) + '].updateUsersStatus">禁用账号</a>&nbsp;';
                } else {
                    tr += '<a class="clickNum"  id="staff[' + (index + 1) + '].updateUsersStatus">启用账号</a>&nbsp;';
                }
            }
            if (APP_BCGOGO.Permission.SystemSetting.StaffManager.ResetStaffPassword) {
                tr += '<a class="clickNum" id="staff[' + (index + 1) + '].resetPass">重置密码</a>&nbsp;';
            }
        }
        tr += '</td></tr>';
        $("#staffTable").append(tr);

        tableUtil.tableStyle('#staffTable', '#table_title');
        $("#userStatus").val('');
        $("#sortStr").val('');
    });

//    var uuid = GLOBAL.Util.generateUUID();

//    APP_BCGOGO.Net.asyncGet({
//        url: "staffManage.do?method=getDepartmentDropList",
//        data: {
//            "uuid": uuid,
//            "keyWord": "",
//            "now": new Date()
//        },
//        dataType: "json",
//        success: function (result) {
//            if (result.data) {
//                $("#departmentName1").find('option').not('#firstOption2').not('#second').remove();
//                $("#department").find('option').remove();
//                for (var i = 0; i < result.data.length; i++) {
//                    var option = "<option value='" + result.data[i].name + "' departmentId='" + result.data[i].idStr + "'>" + result.data[i].name + "</option>";
//                    $(option).appendTo($("#departmentName1"));
//                    $(option).appendTo($("#department"));
//
//                }
//
//            }
//        }
//    });
}


function drawSalesManInfo(salesManId){
  var url = "staffManage.do?method=getSalesManInfo";

  var data = {
      salesManId: salesManId,
      now: new Date()
  };
  APP_BCGOGO.Net.asyncAjax({
      url: url,
      type: "POST",
      cache: false,
      data: data,
      dataType: "json",
      success: function (jsonObj) {
          $("#id").val(salesManId);
          $("#salesManName").val(jsonObj.name == null ? "" : jsonObj.name);
          $("#mobile").val(jsonObj.mobile == null ? "" : jsonObj.mobile);
          $("#department").val(jsonObj.departmentName == null ? "" : jsonObj.departmentName);
          $("#departmentId2").val(jsonObj.departmentIdStr);
          $("#salesManCode").val(jsonObj.salesManCode == null ? "" : jsonObj.salesManCode);
          $("#identityCard").val(jsonObj.identityCard == null ? "" : jsonObj.identityCard);
          $("#qq").val(jsonObj.qq == null ? "" : jsonObj.qq);
          $("#salary").val(jsonObj.salary == null ? "" : jsonObj.salary);
          $("#allowance").val(jsonObj.allowance == null ? "" : jsonObj.allowance);
          $("#careerDate").val(jsonObj.careerDateStr == null ? "" : jsonObj.careerDateStr);
          $("#email").val(jsonObj.email == null ? "" : jsonObj.email);
          $("#male").attr("checked", false);
          $("#female").attr("checked", false);
          if (jsonObj.sexStr == '男') {
              $("#male").attr("checked", true);
          } else if (jsonObj.sexStr == '女') {
              $("#female").attr("checked", true);
          }
          if (jsonObj.statusValue == '在职') {
              $("#INSERVICE").attr("checked", true);
          } else if (jsonObj.statusValue == '离职') {
              $("#DEMISSION").attr("checked", true);
          } else if (jsonObj.statusValue == '试用') {
              $("#ONTRIAL").attr("checked", true);
          }

          $("#washBeautyAchievement").val(jsonObj.washBeautyAchievement == null ? "" : jsonObj.washBeautyAchievement);
          $("#serviceAchievement").val(jsonObj.serviceAchievement == null ? "" : jsonObj.serviceAchievement + "%");
          $("#salesAchievement").val(jsonObj.salesAchievement == null ? "" : jsonObj.salesAchievement + "%");
          $("#salesProfitAchievement").val(jsonObj.salesProfitAchievement == null ? "" : jsonObj.salesProfitAchievement + "%");
          $("#memberNewType").val(jsonObj.memberNewType == null ? "CARD_AMOUNT" : jsonObj.memberNewType);

          if (jsonObj.memberNewType == "CARD_TOTAL") {
            $("#memberNewAchievement").val(jsonObj.memberNewAchievement == null ? "" : jsonObj.memberNewAchievement + "%");
          } else {
            $("#memberNewAchievement").val(jsonObj.memberNewAchievement == null ? "" : jsonObj.memberNewAchievement);
          }

          $("#memberRenewType").val(jsonObj.memberRenewType == null ? "CARD_AMOUNT" : jsonObj.memberRenewType);

          if (jsonObj.memberRenewType == "CARD_TOTAL") {
            $("#memberReNewAchievement").val(jsonObj.memberReNewAchievement == null ? "" : jsonObj.memberReNewAchievement + "%");
          } else {
            $("#memberReNewAchievement").val(jsonObj.memberReNewAchievement == null ? "" : jsonObj.memberReNewAchievement);
          }


        if (jsonObj.userNo == null || jsonObj.userGroupId == null) {
              $("#userInfo td:lt(4)").hide();
              $("#userInfo td:eq(0)").show().html('');
              $("#userInfo td:eq(1)").show().html('未分配账号');
          } else {
              $("#userInfo td:lt(4)").show();
              $("#userInfo td:eq(0)").html('用户名<span class="red_color">*</span>：');
              $("#userInfo td:eq(1)").html('<input type="text" class="txt" id="userNo2" name="userNo"/>');
              $("#userNo2").val(jsonObj.userNo == null ? "" : jsonObj.userNo);
              $("#userGroup").val(jsonObj.userGroupName == null ? "" : jsonObj.userGroupName);
              //系统分配的账号 角色不能修改
              if (jsonObj['userType'] == "SYSTEM_CREATE" && jsonObj['userGroupName'] == "老板/财务") {
                  $("#userGroup").attr("disabled", "disabled");
                  $("#userNo2").attr("disabled", "disabled");
              }else{
                  $("#userGroup").removeAttr("disabled");
                  $("#userNo2").removeAttr("disabled");
              }
              $("#userGroup2").val(jsonObj.userGroupIdStr);
          }
          $("#memo").val(jsonObj.memo == null ? "" : jsonObj.memo);
      }
  });
}

function bindSalesManDetails(index) {
  var salesManId = $('#staffTable tr:eq(' + index + ') input[name=salesManId]').val();
  drawSalesManInfo(salesManId);
}


function drawStaffConfigTable(data) {


    var uuid = GLOBAL.Util.generateUUID();

    var departmentData = {};

    APP_BCGOGO.Net.syncGet({
        url: "staffManage.do?method=getDepartmentDropList",
        data: {
            "uuid": uuid,
            "keyWord": "",
            "now": new Date()
        },
        dataType: "json",
        success: function (result) {
            if (result.data) {

                departmentData = result.data;
            }
        }
    });

    var str = '<tr class="space"><td colspan="4"></td></tr>';
    $("#staffTable").append(str);
    $("#staffTable tr:not(`:first)").remove();
    $.each(data["results"], function (index, staff) {
        var salesManCode = (!staff.salesManCode ? "--" : staff.salesManCode),
            isBoss = staff.userGroupName && staff.userGroupName == "老板/财务", //todo 系统默认分配账号管理
            hasUserNo = staff.userNo && staff.userIdStr ? true : false,
            name = staff.name,
            salary = staff.salary ? staff.salary : "",
            allowance = staff.allowance ? staff.allowance : "",
            identityCard = staff.identityCard ? staff.identityCard : "",
            departmentName = staff.departmentName ? staff.departmentName : "",
            departmentId = staff.departmentId ? staff.departmentId : "",
            userGroupName = staff.userGroupName ? staff.userGroupName : "",
            memo = staff.memo ? staff.memo : "",
            mobile = staff.mobile ? staff.mobile : "",
            userNo = staff.userNo ? staff.userNo : "--",
            careerDate = staff.careerDateStr ? staff.careerDateStr : "",
            salesManId = staff.idStr,
            userType = staff.userType,
            userStatus,
            status = staff.statusValue,
            userId = staff.userIdStr ? staff.userIdStr : "",
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

        var tr = '<tr class="titBody_Bg">';
        tr += '<td style="padding-left:10px;" title="' + name + '">' + name + '</td>';
        tr += '<td title="' + sex + '">' + sex + '</td>';
        tr += '<td title="' + status + '">' + status + '</td>';

        tr += '<td>' + '<select class="txt" style="width:111px;" class="departmentSelect" name="departmentName">'


        for (var i = 0; i < departmentData.length; i++) {
            var option = "<option value='" + departmentData[i].name + "' departmentId='" + departmentData[i].idStr + "'>" + departmentData[i].name + "</option>";
            tr += option;
        }
        tr += '</select>';
        tr += '<input type="hidden" class="departmentSelect"/></td>';

        tr += '</tr>';
        $("#staffTable").append(tr);

        $("#userStatus").val('');
        $("#sortStr").val('');
    });
}