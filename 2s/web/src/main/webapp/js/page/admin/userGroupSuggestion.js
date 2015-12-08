$(function () {
//    $("#userGroupName")
//        .live("click keyup", function (event) {
//            if (event.type == "keyup") {
//                if ((GLOBAL.Interactive.keyNameFromEvent(event)).search(/^left$|^right$|^up$|^down$|^shift$|^ctrl$|^alt$/g) != -1)return;
//                else {
//                    $("#userGroupName").attr("userGroupId", "")
//                }
//            }
//            if ($(this).val() != $(this).attr("hiddenValue")) {
//                $("#userGroupId").val("");
//                $(this).removeAttr("hiddenValue");
//            }
//            var obj = this,
//                droplist = APP_BCGOGO.Module.droplist;
//            clearTimeout(droplist.delayTimerId || 1);
//            droplist.delayTimerId = setTimeout(function () {
//                var droplist = APP_BCGOGO.Module.droplist;
//                var userGroupName = $(obj).val();
//                var userGroupId = $("#userGroupId").val();
//                var uuid = GLOBAL.Util.generateUUID();
//                droplist.setUUID(uuid);
//                APP_BCGOGO.Net.asyncGet({
//                    url:"staffManage.do?method=getUserGroupDropList",
//                    data:{
//                        "uuid":uuid,
//                        "keyWord":userGroupName,
//                        "isFuzzyMatching":'true',
//                        "now":new Date()
//                    },
//                    dataType:"json",
//                    success:function (result) {
//                        if (result.data) {
//                            for (var i = 0; i < result.data.length; i++) {
//                                result.data[i].isEditable = false;
//                                result.data[i].isDeletable = false;
//                            }
//                        }
//                        droplist.show({
//                            "selector":$(event.currentTarget),
//                            "isEditable":true,
//                            "originalValue":{
//                                label:userGroupName,
//                                idStr:userGroupId
//                            },
//                            "data":result,
//                            "saveWarning":"保存此修改影响全局数据",
//                            "isDeletable":true,
//                            "onSelect":function (event, index, data, hook) {
//                                var id = data.idStr;
//                                var name = data.name;
//                                $(hook).val(name);
//                                $(hook).attr("hiddenValue", name);
//                                $("#userGroupId").val(id);
//                                $("#userGroupName").attr("userGroupId", id);
//                                droplist.hide();
//                            }
//                        });
//                    }
//                });
//            }, 200);
//        });

        var uuid = GLOBAL.Util.generateUUID();
        APP_BCGOGO.Net.asyncGet({
                    url:"staffManage.do?method=getUserGroupDropList",
                    data:{
                        "uuid":uuid,
                        "keyWord":"",
                        "isFuzzyMatching":'true',
                        "now":new Date()
                    },
                    dataType:"json",
                    success:function (result) {
                        if (result.data) {
                            for (var i = 0; i < result.data.length; i++) {
                              var option = "<option value='" + result.data[i].name + "' userGroupId='" + result.data[i].idStr +"'>" + result.data[i].name + "</option>";
                              $(option).appendTo($("#userGroupName1"));
                              $(option).appendTo($("#userGroup"));
                              $(option).appendTo($("#userGroup3"));
                              $(option).appendTo($("#userGroup5"));
                              if(result.data[i].name != $("#userGroupName2").val()) {
                                $(option).appendTo($("#userGroupName2"));
                              } else {
                                  $("#first").attr("userGroupId",result.data[i].idStr);

                              }

                            }
                          if($("#userGroupId").val()!='') {
                                $("#copyDiv").hide();
                              } else {
                                $("#copyDiv").show();

                                if($("#copy").attr("checked")) {
                                  $("#userGroup5").removeAttr("disabled");
                                }

                                if($("#copyUserGroupId").val()!='') {
                                  $("#copy").attr("checked","true").attr("disabled","true");

                                  $("#userGroup5").val($("#userGroup5").find('option[usergroupid='+$("#copyUserGroupId").val()+']').val());
                                }
                          }


                        }
                    }
        });
        $("#userGroupName1").change(function(){
            if ($("#pageType").val() != "allSalesManConfig") {
                $("#firstOption1").remove();
            }

            $("#searchStaff").click();
        })
        .click(function(){
          $(this).removeClass("txt");
        });
})