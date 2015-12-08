$(function () {
    var isSalesManCodeDuplicated = false, isUserNoDuplicated = false;
    $("input[type='text']").attr("autocomplete", "off");
    //主要控制分配的账号
    if ($("#userGroupName").val() == "老板/财务" && $("#userType").val() == "SYSTEM_CREATE") {
        $("#userGroupName").attr("disabled", true);
    }
    $("#salary,#allowance,#washBeautyAchievement,#serviceAchievement,#salesAchievement," +
            "#salesProfitAchievement,#memberNewAchievement,#memberReNewAchievement").live("keyup blur", function (event) {
        if (event.type == "focusout")
            event.target.value = APP_BCGOGO.StringFilter.inputtedPriceFilter(event.target.value);
        else if (event.type == "keyup")
            if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value)) {
                event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value);
            }
    });

    $("#div_close,#cancelBtn").click(function () {
        window.parent.document.getElementById("mask").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox").src = "";
        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch (e) {
        }
    });

    $("#careerDateStr")
        .bind("click", function () {
            $(this).blur();
        })
        .datepicker({
            "numberOfMonths":1,
            "showButtonPanel":true,
            "changeYear":true,
            "changeMonth":true,
            "yearRange":"c-100:c+100",
            "yearSuffix":""
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
                    url:"staffManage.do?method=checkSalesManCode",
                    data:{
                        salesManCode:e.target.value,
                        salesManId:$("#id").val()
                    },
                    dataType:"json",
                    success:function (isDuplicate) {
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

    $("#userNo").blur(function (e) {
        if (e.target.value) {
            APP_BCGOGO.Net.asyncAjax({
                url:"user.do?method=checkUserNo",
                data:{
                    userNo:e.target.value,
                    salesManId:$("#id").val()
                },
                dataType:"json",
                success:function (result) {
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

  $("#salesManForm").submit(function () {
    var $submitBtn = $("#submitBtn"), $salesManForm = $("#salesManForm");
    $submitBtn.attr("disabled", "disabled");
    if (checkFormData()) {
      $salesManForm.attr('action', 'staffManage.do?method=checkAndSaveSalesManInfo');
      var options = {
        type: 'post', dataType: "json", url: "staffManage.do?method=checkAndSaveSalesManInfo", success: function (data) {
          if (data.info == "success") {
            if (window.parent.document.getElementById("searchStaff") == null) {
                if($("#userGroupId").val() != '' && $("#userNo").val() != '') {
                    alert("员工已新增并且分配账号成功！账号初始密码为123456");
                } else {
                    alert("员工已新增成功！");
                }
              window.parent.location.href = 'staffManage.do?method=showStaffManagePage';
            } else {
                if ($("#userGroupId").val() != '' && $("#userNo").val() != '') {
                    nsDialog.jAlert("员工已新增并且分配账号成功！账号初始密码为123456", null, function () {
                        window.parent.document.getElementById("mask").style.display = "none";
                        window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                        window.parent.document.getElementById("iframe_PopupBox").src = "";
                        $(window.parent.document.getElementById("searchStaff")).click();
                    });
                } else {
                    nsDialog.jAlert("员工已新增成功！", null, function () {
                        window.parent.document.getElementById("mask").style.display = "none";
                        window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                        window.parent.document.getElementById("iframe_PopupBox").src = "";
                        $(window.parent.document.getElementById("searchStaff")).click();
                    });
                }


            }


          } else {
            nsDialog.jAlert(data.info);
          }
          $submitBtn.removeAttr("disabled");
        }
      };
      $salesManForm.ajaxSubmit(options);
    } else {
      $submitBtn.removeAttr("disabled");
    }
    return false;
  });

    $("#userGroupName2").change(function(){
       $("#userGroupId").val($("#userGroupName2").find('option[value="' + $("#userGroupName2").val() + '"]').attr("userGroupId"));
    });
    $("#departmentName2").change(function(){
       $("#departmentId").val($("#departmentName2").find('option[value="' + $("#departmentName2").val() + '"]').attr("departmentId"));
    });

    function checkUserGroupName(value) {
        var success = true;
        if (value) {
            APP_BCGOGO.Net.syncAjax({
                url:"staffManage.do?method=getUserGroupDropList",
                data:{
                    keyWord:value,
                    "isFuzzyMatching":'false'
                },
                dataType:"json",
                success:function (result) {
                    if (result.data && result.data.length == 1) {
                        $("#userGroupId").val(result.data[0].idStr);
                        success = true;
                    } else {
                        success = false;
                        nsDialog.jConfirm("请输入正确的职位名！", "", function (yes) {
                            if (yes) {
                                $("#userGroupName").focus();
                                $("#userGroupName").select();
                            } else {
                                $("#userGroupName").val("");
                            }
                        });
                    }
                }
            });
        }
        return success;
    }

    function checkFormData() {
        var name = $("#name").val();
        if ($.trim(name) == "") {
            nsDialog.jAlert("姓名不能为空,请输入姓名");
            $("#name").focus();
            return false;
        }
        if ($("#mobile").val()) {
            if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#mobile").val())) {
                nsDialog.jAlert("请填写正确的手机号！");
                $("#mobile").focus();
                return false;
            }
        } else {
            nsDialog.jAlert("手机号不能为空！");
            $("#mobile").focus();
            return false;
        }

        if ($("#email").val() && !APP_BCGOGO.Validator.stringIsEmail($("#email").val())) {
            nsDialog.jAlert("邮箱格式错误，请确认后重新输入！");
            $("#email").focus();
            return false;
        }
        if ($("#identityCard").val()) {
            var result = idCardValidate.checkCardId($("#identityCard").val());
            if (!result.success) {
                nsDialog.jAlert(result.message);
                $("#identityCard").focus();
                return false;
            }

        }

        if ($("#departmentName").val() == '') {
            nsDialog.jAlert("请选择部门！");
            $("#departmentName2").focus();
            return false;
        }

        if($("#userNo").val() != '' && $("#userGroupId").val() == '') {
           nsDialog.jAlert("请选择职位权限！");
           return false;
        }

        if($("#userNo").val() == '' && $("#userGroupId").val() != '') {
           nsDialog.jAlert("请输入账户名！");
           return false;
        }

        if (isSalesManCodeDuplicated) {
            nsDialog.jAlert("工号重复，请重新输入！");
            $("#salesManCode").focus();
            return false;
        }

        if (isUserNoDuplicated) {
            nsDialog.jAlert("账号重复，请重新输入！");
            $("#userNo").focus();
            return false;
        }
        return true;
    }

});