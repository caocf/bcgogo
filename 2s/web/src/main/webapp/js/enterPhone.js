var enterPhoneMobile;
$(document).ready(function () {
    $("#divEnterPhoneClose,#cancelEnterPhoneBtn").live('click', function () {
        $("#enterPhoneSetLocation").fadeOut("slow");
        jQuery("#enterPhoneMobile").val("");
        $("#mask").hide();
        if ('uncleUser_member_change_password' == $("#enterPhoneScene").val()) {
            $("#chPasswordShow").dialog("open");
        }
        return;
    });
    $("#enterPhoneSupplierId").val("");
});
function submitEnterPhoneBtn() {
    if (!checkMobile(jQuery("#enterPhoneMobile").val())) {
        alert("手机号码输入有误！请重新输入！");
        return;
    }
    if (!sameMobile(jQuery("#enterPhoneMobile").val())) {
        alert("手机号码重复！请重新输入！.");
        return;
    }
    else {
        $.ajax({
            type:"POST",
            url:"customer.do?method=updateMobile",
            data:{supplierId:$("#enterPhoneSupplierId").val(), customerId:$("#enterPhoneCustomerId").val(), mobile:$("#enterPhoneMobile").val(), licenceNo: $('#licenceNo').val()},
            cache:false,
            async:true,
            success:function () {
                $("#enterPhoneSetLocation").fadeOut("slow", function () {
                    if ('uncleUser_member_change_password' == $("#enterPhoneScene").val()) {
                         $("#mobile").val(enterPhoneMobile);
                         $("#span_mobile").html(enterPhoneMobile);
                         changePassword();
                        $("#chPasswordShow").dialog("close");
                    } else{
                       enterPhoneSendSms(enterPhoneMobile);
                    }
                });
            },
            error:function () {
                $("#enterPhoneSetLocation").fadeOut("fast");
                showMessage.fadeMessage("35%", "40%", "fast", 3000, "网络错误手机更新失败！");
            }
        });
        enterPhoneMobile = $("#enterPhoneMobile").val();
        $("#enterPhoneMobile").val("");
        $('#licenceNo').val('');
        $("#mask").hide();
    }
}
function checkMobile(mobile) {
    return APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile);
}

function sameMobile(moble) {
    //判断供应商手机号码重复
    if (jQuery("#enterPhoneCustomerId").val() != "") {
        return APP_BCGOGO.Net.syncGet({"url": "customer.do?method=getCustomerByMobile", data: {"mobile": moble}, dataType: "json"}).length == 0;
    }
    //判断供应商手机号码重复
      if(jQuery("#enterPhoneSupplierId").val()!=""){
         var r = APP_BCGOGO.Net.syncGet({"url":"customer.do?method=getSupplierByMobile",data:{"mobile":moble},dataType:"json"});
        if (r.length == 0) {
            return true;
        } else {
            return false;
        }
      }

}
//Esc键捕获 关闭enterPhone页面
document.onkeydown = function (e) {
    e = e || window.event;
    var key = e.which || e.keyCode;
    if (key == 27) {
        $("#enterPhoneSetLocation").fadeOut("slow");
        $("#mask").hide();
    }
}