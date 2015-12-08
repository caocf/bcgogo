$(function(){
  var uuid = GLOBAL.Util.generateUUID();
        APP_BCGOGO.Net.asyncGet({
                    url:"/web/staffManage.do?method=getUserGroupDropList",
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
                              $(option).appendTo($("#userGroup3"));
                            }
                            var userGroupId = window.parent.document.getElementById('userGroupId').value;
                            var userId = window.parent.document.getElementById('userId').value;
                            var userNo = window.parent.document.getElementById('userNo').value;
                            $("#userGroupId").val(userGroupId);
                            $("#userGroup3").val($("#userGroup3").find('option[userGroupId=' + userGroupId + ']').val());
                            $("#userNo").val(userNo=='--'?"":userNo);
                            $("#userId").val(userId);
                        }
                    }
        });
  $("#cancel,#div_close").click(function(){
      window.parent.document.getElementById("mask").style.display = "none";
      window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
      window.parent.document.getElementById("iframe_PopupBox").src = "";
  });
  $("#save").click(function(){

                if ($("#userNo").val() == '') {
                    nsDialog.jAlert("请输入账户名！");
                    return;
                }
                if($("#userGroupId").val() == '') {
                    nsDialog.jAlert("请选择职位权限！");
                    return;
                }
                APP_BCGOGO.Net.syncAjax({
                    url:"/web/user.do?method=checkUserNo",
                    data:{
                        userNo:$("#userNo").val()
                    },
                    dataType:"json",
                    success:function (result) {
                        if (result.isDuplicate) {
                            nsDialog.jAlert("账户名已被使用！");
                        } else {
                            nsDialog.jConfirm("确认为该用户分配账户？", null, function (yes) {
                                if (yes) {
                                    var salesManId = window.parent.document.getElementById("salesManId").value;
                                    APP_BCGOGO.Net.syncAjax({
                                        url:"/web/user.do?method=allocatedUserNo",
                                        data:{
                                            salesManId:salesManId,
                                            userNo:$("#userNo").val(),
                                            userGroupId:$("#userGroupId").val(),
                                            userId:$("#userId").val()
                                        },
                                        dataType:"json",
                                        success:function (result) {
                                            if (result.success) {
                                                nsDialog.jAlert("密码重置成功！初始密码为123456！", "", function () {
                                                  $(window.parent.document.getElementById("searchStaff")).click();

                                                  $("#cancel").click();
                                                });
                                            } else {
                                                nsDialog.jAlert(result.message, "", function () {
                                                    $("#cancel").click();
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
  });

  $("#userGroup3").change(function(){
    $("#userGroupId").val($("#userGroup3").find('option[value="' + $("#userGroup3").val() + '"]').attr("userGroupId"));
  });
});