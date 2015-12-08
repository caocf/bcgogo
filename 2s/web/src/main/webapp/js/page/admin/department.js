$(function () {
    $("#departmentName").live("click keyup", function (event) {
        if (event.type == "keyup" && (GLOBAL.Interactive.keyNameFromEvent(event)).search(/^left$|^right$|^up$|^down$|^shift$|^ctrl$|^alt$/g) != -1)return;
        if ($(this).val() != $(this).attr("hiddenValue")) {
            $("#departmentId").val("");
            $(this).removeAttr("hiddenValue");
        }
        var obj = this,
            droplist = APP_BCGOGO.Module.droplist;
        clearTimeout(droplist.delayTimerId || 1);
        droplist.delayTimerId = setTimeout(function () {
            var droplist = APP_BCGOGO.Module.droplist;
            var departmentName = $(obj).val();
            var departmentId = $("#departmentId").val();
            var uuid = GLOBAL.Util.generateUUID();
            droplist.setUUID(uuid);
            APP_BCGOGO.Net.asyncGet({
                url:"staffManage.do?method=getDepartmentDropList",
                data:{
                    "uuid":uuid,
                    "keyWord":departmentName,
                    "now":new Date()
                },
                dataType:"json",
                success:function (result) {
                    if (result.data) {
                        if (!APP_BCGOGO.Permission.SystemSetting.StaffManager.UpdateStaff && !APP_BCGOGO.Permission.SystemSetting.StaffManager.AddStaff) {
                            for (var i = 0; i < result.data.length; i++) {
                                result.data[i].isEditable = false;
                                result.data[i].isDeletable = false;
                            }
                        }
                    }
                    droplist.show({
                        "selector":$(event.currentTarget),
                        "isEditable":true,
                        "originalValue":{
                            label:departmentName,
                            idStr:departmentId
                        },
                        "data":result,
                        "saveWarning":"保存此修改影响全局数据",
                        "isDeletable":true,
                        "onSelect":function (event, index, data, hook) {
                            var id = data.idStr;
                            var name = data.name;
                            $(hook).val(name);
                            $(hook).attr("hiddenValue", name);
                            $("#departmentId").val(id);
                            droplist.hide();
                        },

                        "onSave":function (event, index, data, hook) {
                            var id = data.idStr;
                            var name = $.trim(data.label);
                            if (name == data.name) return;
                            APP_BCGOGO.Net.syncPost({
                                url:"staffManage.do?method=updateDepartment",
                                data:{
                                    "departmentId":id,
                                    "departmentName":name,
                                    "now":new Date()
                                },
                                dataType:"json",
                                success:function (result) {
                                    if (result.duplicate) {
                                        alert("部门重复！");
                                        data.label = data.name;
                                    } else if (result.success) {
                                        data.label = name;
                                        data.name = name;
                                        //重新加载员工
                                        if ($("#searchStaff"))$("#searchStaff").click();
                                    }
                                }
                            });
                        },

                        "onDelete":function (event, index, data) {
                            var r = APP_BCGOGO.Net.syncGet({
                                url:"staffManage.do?method=deleteDepartment",
                                data:{
                                    departmentId:data.idStr,
                                    now:new Date()
                                },
                                dataType:"json"
                            });
                            if (r == null || r.hasBeUsed) {
                                alert("删除失败,此部门正在被使用！");
                            } else if (r.success) {
                                alert("删除成功！");
                            }
                        }
                    });
                }
            });
        }, 200);
    });

//    if ($("#departmentName1").val() != $("#departmentName1").attr("hiddenValue")) {
//            $("#departmentId").val("");
//            $("#departmentName1").removeAttr("hiddenValue");
//    }
//    var departmentName = $("#departmentName1").val();
//    var departmentId = $("#departmentId").val();
    var uuid = GLOBAL.Util.generateUUID();
    APP_BCGOGO.Net.asyncGet({
                url:"staffManage.do?method=getDepartmentDropList",
                data:{
                    "uuid":uuid,
                    "keyWord":"",
                    "now":new Date()
                },
                dataType:"json",
                success:function (result) {
                    if (result.data) {
                          for (var i = 0; i < result.data.length; i++) {

                                var option = "<option value='" + result.data[i].name + "' departmentId='" + result.data[i].idStr + "'>" + result.data[i].name + "</option>";
                                $(option).appendTo($("#departmentName1"));
                                $(option).appendTo($("#department"));
                                if($("#departmentName2").val() != result.data[i].name) {
                                   $(option).appendTo($("#departmentName2"));
                                } else {
                                  $("#first").attr("departmentId",result.data[i].idStr);

                                }


                          }

                    }
                }
    });
    $("#departmentName1").change(function(){
           $("#firstOption2").remove();
           $("#searchStaff").click();
    })
    .click(function(){
          $(this).removeClass("txt");
    });

})