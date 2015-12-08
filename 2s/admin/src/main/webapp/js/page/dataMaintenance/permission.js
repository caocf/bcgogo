$(function() {
    $("#user-group-operate").click(function() {
        bcgogo.checksession({"parentWindow":window.parent,'iframe_PopupBox':$("#iframe_PopupBox")[0],'src':"permission.do?method=showAllUserGroup"});
    });
});
function ajaxForRoleAction() {
        $.ajax({
            type:"POST",
            url:"permission.do?method=showRoles",
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                initRole(jsonStr);
            }
        });
    }

function initRole(jsonStr){

}

function permissionFreshResources(method) {
    $.get("permission.do?method=" + method, function(json) {
        if (json.result == "success")
            alert("操作成功!");
        else if (json.result == "exception")
            alert("出现异常!");
        else
            alert("无响应!");
    }, "json");
}