$(function(){
  $("#div_close").bind("click", function(){
    window.parent.document.getElementById("mask").style.display = "none";
      window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
      window.parent.document.getElementById("iframe_PopupBox").src = "";
  });

  $("#returnPermissionList").bind("click", function(){
    window.parent.location.href = '/web/userGroupsManage.do?method=showUserGroupsManage';
  });

  $("#selectEmployee").bind("click", function(){
    window.parent.location.href = '/web/staffManage.do?method=showStaffManagePage';
  });

  $("#addNewStaff").bind("click", function () {
        window.parent.bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':window.parent.document.getElementById("iframe_PopupBox"),
            'src':'staffManage.do?method=getSaleManInfoById&salesManId=&ts='
                + new Date().getTime()});
  });
});