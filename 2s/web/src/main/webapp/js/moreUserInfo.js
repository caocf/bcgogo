/**
 * Created by IntelliJ IDEA.
 * User: zhouxiaochen
 * Date: 12-1-7
 * Time: 上午10:20
 * To change this template use File | Settings | File Templates.
 */
function saveTempDataToParent() {
    window.parent.document.getElementById("hidName").value = $("#name").val();
    window.parent.document.getElementById("hidShortName").value = $("#shortName").val();
    window.parent.document.getElementById("hidAddress").value = $("#address").val();
    window.parent.document.getElementById("hidContact").value = $("#contact").val();
    window.parent.document.getElementById("hidMobile").value = $("#mobile").val();
    window.parent.document.getElementById("hidPhone").value = $("#phone").val();
    window.parent.document.getElementById("hidFax").value = $("#fax").val();
    window.parent.document.getElementById("hidMemberNumber").value = $("#memberNumber").val();
    window.parent.document.getElementById("hidBirthdayString").value = $("#birthdayString").val();
    window.parent.document.getElementById("hidQQ").value = $("#qq").val();
    window.parent.document.getElementById("hidEmail").value = $("#email").val();
    window.parent.document.getElementById("hidBank").value = $("#bank").val();
    window.parent.document.getElementById("hidBankAccountName").value = $("#bankAccountName").val();
    window.parent.document.getElementById("hidAccount").value = $("#account").val();
}
function clearData() {
    window.parent.document.getElementById("hidName").value = "";
    window.parent.document.getElementById("hidShortName").value = "";
    window.parent.document.getElementById("hidAddress").value = "";
    window.parent.document.getElementById("hidContact").value = "";
    window.parent.document.getElementById("hidMobile").value = "";
    window.parent.document.getElementById("hidPhone").value = "";
    window.parent.document.getElementById("hidFax").value = "";
    window.parent.document.getElementById("hidMemberNumber").value = "";
    window.parent.document.getElementById("hidBirthdayString").value = "";
    window.parent.document.getElementById("hidQQ").value = "";
    window.parent.document.getElementById("hidEmail").value = "";
    window.parent.document.getElementById("hidBank").value = "";
    window.parent.document.getElementById("hidBankAccountName").value = "";
    window.parent.document.getElementById("hidAccount").value = "";
}

function getTempDataFromParent() {
    $("#name").val(window.parent.document.getElementById("hidName").value);
    $("#shortName").val(window.parent.document.getElementById("hidShortName").value);
    $("#address").val(window.parent.document.getElementById("hidAddress").value);
    $("#contact").val(window.parent.document.getElementById("hidContact").value);
    $("#mobile").val(window.parent.document.getElementById("hidMobile").value);
    $("#phone").val(window.parent.document.getElementById("hidPhone").value);
    $("#fax").val(window.parent.document.getElementById("hidFax").value);
    $("#memberNumber").val(window.parent.document.getElementById("hidMemberNumber").value);
    $("#birthdayString").val(window.parent.document.getElementById("hidBirthdayString").value);
    $("#qq").val(window.parent.document.getElementById("hidQQ").value);
    $("#email").val(window.parent.document.getElementById("hidEmail").value);
    $("#bank").val(window.parent.document.getElementById("hidBank").value);
    $("#bankAccountName").val(window.parent.document.getElementById("hidBankAccountName").value);
    $("#account").val(window.parent.document.getElementById("hidAccount").value);
}
$(document).ready(function() {
    $("#div_close,#cancleBtn").click(function() {
        //保存临时数据到父框架中
        if (window.parent.document.getElementById("hidName") != null) {
//           saveTempDataToParent();
        }
        $(window.parent.document).find("#mask").hide();
        $(window.parent.document).find("#iframe_PopupBox").hide();
        $(window.parent.document).find("#iframe_moreUserInfo").hide();
        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch(e) {
            ;
    }
    });

//    if(document.getElementById("confirmBtn")!=null){
//      document.getElementById("confirmBtn").onclick = function() {
//        $("form:first").submit();
//        //清楚临时数据
//        clearData();
//        //给父框架中的客户名，ID和手机赋值
//
////        window.parent.document.getElementById("customer").value = $("#name").val();
////        window.parent.document.getElementById("mobile").value =$("#mobile").val();
//
//        $(window.parent.document).find("#mask").hide();
//        $(window.parent.document).find("#iframe_PopupBox").hide();
//       alert("客户信息更新成功!") ;
//      window.parent.location.assign("txn.do?method=getRepairOrderByVehicleNumber&vehicleNumber="+window.parent.document.getElementById("licenceNo").value) ;
//      }
//    }


    if ($("#customerId").val() == null || $("#customerId").val() == "") {
        //获取临时数据
        if (window.parent.document.getElementById("hidName") != null) {
//           getTempDataFromParent();
        }
    }
});