function doPage(url) {
    //top.location.href = url;
    top.window.open(url, "newwindow", "");
}
(function() {

    window.Mask = {version:1.0};
    var D = new Function('obj', 'return document.getElementById(obj);');
    var oevent = new Function('e', 'if (!e) e = window.event;return e');

    Mask.Login = function() {
        $(window.parent.document).find("#mask").attr("style", "display:block;position:absolute;top:0px;left:0px;width:100%;height:150%;z-index:3;filter:alpha(opacity=40);opacity:0.4;background-color:#000000;");
        $(window.parent.document).find("#mask").addClass("b");
    }

})();

function clearDefaultAddress(){
    if ($("#input_address").val() == $("#input_address").attr("initValue") && $("#input_address").val() == "详细地址") {
        $("#input_address").val('');
    }
    if ($("#input_address1").val() == $("#input_address1").attr("initValue") && $("#input_address1").val() == "详细地址") {
        $("#input_address1").val('');
    }
    if ($("#input_address2").val() == $("#input_address2").attr("initValue") && $("#input_address2").val() == "详细地址") {
        $("#input_address2").val('');
    }
}

function changePassword() {
    jQuery.ajax({
        type: "POST",
        url: "user.do?method=changeMemberPassword",
        data: {
            memberId: $("#memberId").val(),
            oldPw: $("#oldPw").val(),
            sendSms: $("#sendSms").attr("checked"),
            userName: $("#name").val(),
            //用户名
            memberNo: $("#memberNo").html(),
            //会员号
            mobile: $("#mobile").val(),
            //手机
            newPw: $("#newPw").val()
        },
        cache: false,
        success: function(data) {
            if(data == "CONFIRM_PASSWORD_FAIL") {
                nsDialog.jAlert("输入的旧密码不正确");
            } else if(data == "CHANGE_PASSWORD_SUCCESS") {
                nsDialog.jAlert("密码修改成功", null, function() {
                    $("#chPasswordShow").dialog("close");
    });
            } else {
                nsDialog.jAlert("密码修改失败");
            }
            $("#oldPw").val("");
            $("#newPw").val("");
            $("#cfNewPw").val("");
        }
    });
}

$(document).ready(function() {
    var message = "";
    if ($("#contact") != null) {

        if ($("#contact").val() != null && $.trim($("#contact").val()) == "") {
            $("#contact").val($.trim($(window.parent.document).find("#contact").text()));
        }
    }

    $("#changePassword").bind("click", function(e) {
        if ($("#memberId").val() == "") {
            return false;
        }
        $("#oldPw").val("");
        $("#newPw").val("");
        $("#cfNewPw").val("");$("#chPasswordShow").dialog({
            modal: true,
            resizable: false,
            draggable: false,
            buttons: {
                "确定": function() {
                    var obj = this;
                    if($("#newPw").val() != $("#cfNewPw").val()) {
                        nsDialog.jAlert("两次输入的密码不一致");
                    } else {
                        var mobile = $("#mobile").val();
                        if($("#sendSms").attr("checked") == true) {
                            if(mobile == null || $.trim(mobile) == "") {
                                $("#enterPhoneCustomerId").val($("#customerId").val());
                                $("#enterPhoneScene").val("uncleUser_member_change_password");
                                $("#enterPhoneSetLocation").fadeIn("slow");
                            } else {
                                changePassword();
                            }
                        } else {
                            changePassword();
                        }
                    }
                },
                "取消": function() {
                    $(this).dialog("close");
                    $("#oldPw").val("");
                    $("#newPw").val("");
        $("#cfNewPw").val("");
                }
            },
            close:function() {
                $("#oldPw").val("");
                $("#newPw").val("");
                $("#cfNewPw").val("");
            }
    });
    });

    $("#cancelPassword").bind("click", function(e) {
       $("#chPasswordShow").hide();
    });

    $("#savePassword").bind("click", function(e) {
        if ($("#newPw").val() != $("#cfNewPw").val()) {
            alert("两次输入的密码不一致");
            return;
        }
        jQuery.ajax({
            type:"POST",
            url:"user.do?method=changeMemberPassword",
            data:{memberId: $("#memberId").val(),oldPw: $("#oldPw").val(),newPw: $("#newPw").val()},
            cache:false,
            success:function(data) {
                if (data == "CONFIRM_PASSWORD_FAIL") {
                    alert("输入的旧密码不正确");
                } else if (data == "CHANGE_PASSWORD_SUCCESS") {
                    alert("密码修改成功");
                    $("#chPasswordShow").hide();
                } else {
                    alert("密码修改失败");
                    $("#chPasswordShow").hide();
                }
            }
        });
    });

     $("#input_makeTime").click(function() {
        //获取选中的记录的单子Id和单子类型。和客户Id
        var customerId = $("#customerId").val();
        var count = $(".check").size();
        var oids = "";//所有单子ID
        var orderTypes = "";//所有单子的类型
        for (var i = 0; i < count; i++) {
            if ($($(".check").get(i)).attr("checked") == true) {
                var oid = $($(".check").get(i)).next().val();
                var orderType = $($(".check").get(i)).next().next().val();
                if (i + 1 == count) {
                    oids = oids + oid;
                    orderTypes = orderTypes + orderType;
                } else {
                    oids = oids + oid + ",";
                    orderTypes = orderTypes + orderType + ",";
                }
            }
        }
        bcgogo.checksession({"parentWindow":window,'iframe_PopupBox':document.getElementById("iframe_PopupBoxMakeTime"),'src':"txn.do?method=makeAllTime&orderId=" + oids});
    });


    if ($("#div_close1") != null) {
        $("#div_close1").click(function() {
            $(window.parent.document).find("#mask").hide();
            $(window.parent.document).find("#iframe_PopupBox_1").hide();
            $(window.parent.document).find("#iframe_PopupBox_2").hide();
            $(window.parent.document).find("#iframe_qiankuan").hide();
            try {
                $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
            } catch(e) {
                ;
            }
        });
    }
//begin add by zhouxiaochen 2011-12-16
    $("#checkAll").click(function() {
        var count = $(".check").size();
        if ($(this).attr("checked") == true) {
            //总金额，实付金额
            var size = $(".owedTd").size();
            var newTotalAmount = 0;
            for (var i = 0; i < size; i++) {

                newTotalAmount = newTotalAmount + $($(".owedTd").get(i)).children().html() * 1;
            }
            newTotalAmount = newTotalAmount.toFixed(2);
            $("#totalAmount").text(newTotalAmount);
            $("#cashAmount").val(newTotalAmount);
            $("#payedAmount").val(newTotalAmount);
            $("#owedAmount").val(0);
            //选中所有的
            for (var i = 0; i < count; i++) {
                $($(".check").get(i)).attr("checked", true);
            }
        }
        else {
            $("#totalAmount").text(0);
            $("#cashAmount").val(0);
            $("#payedAmount").val(0);
            $("#owedAmount").val(0);
            //不选中所有的
            for (var i = 0; i < count; i++) {
                $($(".check").get(i)).attr("checked", false);
            }
        }
        $("#input_makeTime").hide();
    });

    $("#history .check").bind("click", function() {
        $(this).blur();
    });

//关闭按钮
    $("#cancelButton").click(function () {

        $(window.parent.document).find("#mask").hide();
        //车辆页面
        if (parent.document.getElementById("iframe_qiankuan") != null) {
            parent.document.getElementById("iframe_qiankuan").style.display = "none";
        }
        if ($(window.parent.document).find(".tableInfo2") != null) {
            $(window.parent.document).find(".tableInfo2").show();
        }
        if ($(window.parent.document).find("#table_task") != null) {
            $(window.parent.document).find("#table_task").show();
        }
        if ($(window.parent.document).find("#table_productNo_2") != null) {
            $(window.parent.document).find("#table_productNo_2").show();
        }
        if ($(window.parent.document).find("#div_tablereservateTime") != null) {
            $(window.parent.document).find("#div_tablereservateTime").show();
        }
        //客户管理页面
        if (parent.document.getElementById("iframe_PopupBox_1") != null) {
            parent.document.getElementById("iframe_PopupBox_1").style.display = "none";
        }
        if (parent.document.getElementById("iframe_PopupBox_2") != null) {
            parent.document.getElementById("iframe_PopupBox_2").style.display = "none";
        }
        if (parent.document.getElementById("mask") != null) {
            parent.document.getElementById("mask").style.display = "none";
        }
        //销售页面
        if ($(window.parent.document).find(".table_title") != null) {
            $(window.parent.document).find(".table_title").show();
        }
        if ($(window.parent.document).find(".item") != null) {
            $(window.parent.document).find(".item").show();
        }
        if ($(window.parent.document).find(".tableInfo") != null) {
            $(window.parent.document).find(".tableInfo").show();
        }
        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch(e) {
            ;
        }
    });

    $("#div_close").click(function() {
        if (parent.document.getElementById("mask") != null) {
            $(window.parent.document).find("#mask").hide();
        }
        if (parent.document.getElementById("iframe_moreUserInfo") != null) {
            $(window.parent.document).find("#iframe_moreUserInfo").hide();
        }
        if (parent.document.getElementById("iframe_PopupBox_1") != null) {
            $(window.parent.document).find("#iframe_PopupBox_1").hide();
        }
        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch(e) {
            ;
        }
    });
    //输入手机号之后查看是否存在同名客户
    //手机号码验证：zhangchuanlong
    $("#mobile").blur(function() {
        console.log("mobile blur1");
        var phone = document.getElementById("phone");
        var phoneSecond=document.getElementById("phoneSecond");
        var phoneThird=document.getElementById("phoneThird");
        var mobile = document.getElementById("mobile");
        if ($("#confirmBtn1")[0] && !G.isEmpty($.trim($("#mobile").val()))) {
            $("#confirmBtn1").attr("lock", true);
        }

        if (mobile.value != "" && mobile.value != null) {
            var result = check.inputCustomerMobileBlur(mobile, phone);
            if(result){
                $("#confirmBtn1").removeAttr("lock");
            }else{
                setTimeout(function(){
                    $("#confirmBtn1").removeAttr("lock");
                },200);
            }
        }
        if (mobile.value != "" && mobile.value != null) {
            var result = check.inputCustomerMobileBlur(mobile, phoneSecond);
            if(result){
                $("#confirmBtn1").removeAttr("lock");
            }else{
                setTimeout(function(){
                    $("#confirmBtn1").removeAttr("lock");
                },200);
            }
        }
        if (mobile.value != "" && mobile.value != null) {
            var result = check.inputCustomerMobileBlur(mobile, phoneThird);
            if(result){
                $("#confirmBtn1").removeAttr("lock");
            }else{
                setTimeout(function(){
                    $("#confirmBtn1").removeAttr("lock");
                },200);
            }
        }
    });
    $("#phone").blur(function() {
        console.log("phone blur1");
        var phone = document.getElementById("phone");
        var mobile = document.getElementById("mobile");
        if (phone.value != "" && phone.value != null) {
            check.inputTelephone(phone, mobile);
            console.log("phone blur2");
        }
    });
    $("#phoneSecond").blur(function() {
        console.log("phoneSecond blur1");
        var phoneSecond = document.getElementById("phoneSecond");
        var mobile = document.getElementById("mobile");
        if (phoneSecond.value != "" && phoneSecond.value != null) {
            check.inputTelephone(phoneSecond, mobile);
            console.log("phoneSecond blur2");
        }
    });
    $("#phoneThird").blur(function() {
        console.log("phoneThird blur1");
        var phoneThird = document.getElementById("phoneThird");
        var mobile = document.getElementById("mobile");
        if (phoneThird.value != "" && phoneThird.value != null) {
            check.inputTelephone(phoneThird, mobile);
            console.log("phoneThird blur2");
        }
    });

    //生日不允许手动输入
    $("#birthdayString").keyup(function() {
        $(this).val('');
    });

    $("input[id='name'], input[id='contact']").bind("keyup", function(){
        $(this).val(APP_BCGOGO.StringFilter.stringSpaceFilter($(this).val()));
    });
});


function hideTip() {
    $("#div_brandvehiclelicenceNo").css({'display':'none'});
}

$(document).ready(function() {


    $("input[id$='.vehicleMobile']").live("keyup",function(){
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });
    $("input[id$='.vehicleMobile']").live("blur", function() {
        $("#table_vehicle input[id$='vehicleMobile']").each(function() {
                var mobile=$(this).val();
                if (!G.isEmpty(G.trim(mobile))){
                    if (!APP_BCGOGO.Validator.stringIsMobile(mobile)) {
                        nsDialog.jAlert("车主手机号码校验错误，请确认后重新输入！");
                        return false;
                    }
                }
            })
     });

});

$(document).ready(function() {

    if($("#select_province")[0]){
        provinceBind();
        $("#select_province").bind("change",function(){
            cityBind(this);
        });
        $("#select_city").bind("change",function(){
            townshipBind(this);
        });

        $("#input_address,#input_address2").focus(function(){
            if($(this).val() == $(this).attr("initValue") && $(this).val() == '详细地址') {
                $(this).val('');
                $(this).css("color","#000000");
            }
        })
        .blur(function(){
           if($(this).val() == '') {
               $(this).css("color","#7e7e7e");
               $(this).val('详细地址');
           }
        });
        $("#select_province,#select_city,#select_township").click(function(){
           $(this).css("color","#000000");
        });
    }
    $("#modifyClientDiv").dialog({
        autoOpen:false,
        resizable: false,
        title:"修改客户属性",
        height:539,
        width:820,
        modal: true,
        closeOnEscape: false,
        close:function() {
            $("#modifyClientDiv").val("");
            $("#alsoSupplier").attr("checked",false);
        },
        showButtonPanel:true
    });
});


function initTr(data) {
    $("#supplierDatas tr").not(":first").remove();
    if (data && data[0]) {
        $("#totalRows").val(data[0].numFound);
        if (data[0].supplierDTOs != undefined) {
            for (var i = 0; i < data[0].supplierDTOs.length; i++) {
                var supplier = data[0].supplierDTOs[i];
                var contact = supplier.contact==null?'暂无':supplier.contact; //TODO zhuj 
                var mobile = supplier.mobile==null?'暂无':supplier.mobile;
                var tr = '<tr><td><input type="radio" value="' + data[0].supplierDTOs[i].idStr + '" name="supplier"/></td><td>';
                tr += data[0].supplierDTOs[i].name;
                tr += '<a class="connecter J_connector" supplierId="' + supplier.idStr + '"></a>' +
                    '<div class="prompt J_prompt" supplierId="' + supplier.idStr + '" style="margin:0 0 0 30px; display:none;">' +
                    '<div class="promptTop"></div>' +
                    '<div class="promptBody">' +
                    '<div class="lineList">联系人&nbsp;'+ contact +'&nbsp;'+ mobile + '</div>' +
                    '</div>' +
                    '<div class="promptBottom"></div>' +
                    '</div></td>';
                tr += '<td>'+data[0].supplierDTOs[i]['areaInfo']+'</td><td>';
                if (data[0].supplierDTOs[i].address != null) {
                    tr += data[0].supplierDTOs[i].address;
                }
                tr += '</td>';
                $("#supplierDatas").append($(tr));
            }

        }
    }
}
//第一级菜单 select_province
function provinceBind() {
    var r = APP_BCGOGO.Net.syncGet({url:"shop.do?method=selectarea",
        data:{parentNo:1},dataType:"json"});
    if (!r||r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            $("#select_province")[0].appendChild(option);
        }
    }
}

//第二级菜单 select_city
function cityBind(select) {
    while ($("#select_city")[0].options.length > 1) {
        $("#select_city")[0].remove(1);
    }
    while ($("#select_township")[0].options.length > 1) {
        $("#select_township")[0].remove(1);
    }
    if (select.selectedIndex == 0) {

    } else {
        var r = APP_BCGOGO.Net.syncGet({"url": "shop.do?method=selectarea&parentNo=" + select.value, "dataType": "json"});
        if (r === null) {
            return;
        }
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_city")[0].appendChild(option);
            }
        }
    }
}

//第三级菜单 select_township
function townshipBind(select) {
    if (select.selectedIndex == 0) {
        return;
    }
    var r = APP_BCGOGO.Net.syncGet({"url":"shop.do?method=selectarea&parentNo=" + select.value, "dataType":"json"});
    if (r === null || typeof(r) == "undefined") {
        return;
    }
    else {
        while ($("#select_township")[0].options.length > 1) {
            $("#select_township")[0].remove(1);
        }
        if (typeof(r) != "undefined" && r.length > 0) {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_township")[0].appendChild(option);
            }
        }
    }
}

function getSelectAddress1(){
    var selectAddress = '';
    if($("select[name='province']").eq(0).val()){
        selectAddress += $("select[name='province']").find("option:selected").text();
    }
    if($("select[name='city']").eq(0).val()){
        selectAddress += $("select[name='city']").find("option:selected").text();
    }
    if($("select[name='region']").eq(0).val()){
        selectAddress += $("select[name='region']").find("option:selected").text();
    }
    return selectAddress;
}

function setInputValue() {
    var areaInfo = '';
    if(!G.isEmpty($("#select_province").val())) {
        areaInfo += $("#select_province option:selected").text();
    }

    if(!G.isEmpty($("#select_city").val())) {
        areaInfo += $("#select_city option:selected").text();
    }

    if(!G.isEmpty($("#select_township").val())) {
        areaInfo += $("#select_township option:selected").text();
    }

    if($("#areaInfo")[0]) {
        $("#areaInfo").val(areaInfo);
    }

    if(!G.isEmpty($("#input_address").val()) && !G.isEmpty($("#select_province").val())) {
        $("#addressDiv").css("display","");
    }

    if(G.isEmpty($("#select_province").val())) {
        if(!G.isEmpty($("#input_address").val())) {
            $("#areaInfo").val($("#input_address").val());
        }
    }

    $("#customerKindInput").val($("#customerKind option:selected").text());

}



