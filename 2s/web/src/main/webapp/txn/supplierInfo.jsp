<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>更多供应商信息</title>
<link rel="stylesheet" type="text/css" href="styles/up2<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css" />
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>

<%@include file="/WEB-INF/views/iframe_script.jsp" %>

<script type="text/javascript" src="js/supplierInfo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/customer/modifySupplier<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/customerOrSupplier/customerSupplierBusinessScope<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">

<bcgogo:permissionParam permissions="WEB.VERSION.HAS_CUSTOMER_CONTACTS,WEB.VERSION.HAS_SUPPLIER_CONTACTS">
APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact =${WEB_VERSION_HAS_CUSTOMER_CONTACTS}; // 客户多联系人
APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact =${WEB_VERSION_HAS_SUPPLIER_CONTACTS}; // 供应商多联系人
</bcgogo:permissionParam>

$(document).ready(function () {

    // flag判断该供应商是否在数据库已经存在
    var flag = false;
    var ms = 0;

    $('#div_close,#cancelBtn').bind('click', function () {
        $('#mask', parent.document).css('display', 'none');
        $('#iframe_PopupBox', parent.document).css('display', 'none');
        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch(e) {
            ;
        }
    });
    
    /*//手机号码验证：zhangchuanlong
    $("#mobile").blur(function () {
        var landline = $("#landline");
        var mobile = $("#mobile");
        if (mobile.val() != "" && mobile.val() != null) {
            check.inputSupplierMobileBlur(mobile[0], landline[0]);
        }
    });*/

    $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });
    
    $("#account").keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });
    
    $("#landline,#landlineSecond,#landlineThird,#fax").keyup(function() {
        $(this).val($(this).val().replace(/[^\d\-]+/g, "").replace(/\-+/g, "-"));
        if ($(this).val().charAt(0) == '-') {
            $(this).val("");
        }
    });
    
    $("#table_vehicle input[id$='.year']").keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });

    //IE或者别的浏览器
    if (/msie/i.test(navigator.userAgent)) {
        // TODO IE下的待定
    } else {
        $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").live("change",checkNumberInput);
        document.getElementById("landline").addEventListener("input", checkTelInput, false);
        document.getElementById("landlineSecond").addEventListener("input", checkTelInput, false);
        document.getElementById("landlineThird").addEventListener("input", checkTelInput, false);
        document.getElementById("account").addEventListener("input", checkNumberInput, false);
        document.getElementById("fax").addEventListener("input", checkTelInput, false);
    }

    $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });

    $('#account').keyup(function(){
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });

    function checkNumberInput() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    }

    function checkTelInput() {
        $(this).val($(this).val().replace(/[^\d\-]+/g, "").replace(/\-+/g, "-"));
        if ($(this).val().charAt(0) == '-') {
            $(this).val("");
        }
    }

    $("#fax").blur(function() {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($(this).val()) && $(this).val() != "") {
            nsDialog.jAlert("传真号格式错误，请确认后重新输入！");
        }
    });
   /* $("#email").blur(function() {
        if (!APP_BCGOGO.Validator.stringIsEmail($(this).val()) && $(this).val() != "") {
            nsDialog.jAlert("邮箱格式错误，请确认后重新输入！");
        }
    });
    $("#qq").blur(function() {
        var qq = document.getElementById("qq").value;
        if (qq != "" && qq.length < 5) {
            nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
        }
    });
*/
    $("#supplier").blur(function() {
        ms = new Date().getTime();
        var supplierName = $("#supplier").val();
        if (!G.isEmpty(G.trim(supplierName))) {
            checkSupplierByName(supplierName);
        }
    });

    //表单提交，获取json返回值，给supplierId赋值
    $("#confirmBtn1").live("click", function() {
        checkFormData(flag,ms);
    });

    // 绑定 联系人列表 删除事件
    $('.close').live("click",delContact);
    // 绑定 联系人列表 点击成为主联系人事件
    $('.icon_grayconnacter').live("click",switchContact);
    $("body").css("width",800); //重新设置弹出框位置
});

function checkFormData(flag,ms) {
    //重新计算经营产品
    resetThirdCategoryIdStr();

    //判断用户名/联系人是否为空
    var supplierName = $("#supplier").val();
    if (G.isEmpty(G.trim(supplierName))) {
        alert("供应商名称必须填写!");
        return false;
    }
    if (!validateProvinceCity("select_province", "select_city")) {
        return false;
    }



    //validate phone
    var phone = $("#landline").val();
    var phoneSecond=$("#landlineSecond").val();
    var phoneThird=$("#landlineThird").val();
    if (!G.isEmpty(G.trim(phone))) {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber(phone)) {
            nsDialog.jAlert("座机格式校验错误，请确认后重新输入！");
            return false;
        }
    }
    if (!G.isEmpty(G.trim(phoneSecond))) {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber(phoneSecond)) {
            nsDialog.jAlert("座机格式校验错误，请确认后重新输入！");
            return false;
        }
    }
    if (!G.isEmpty(G.trim(phoneThird))) {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber(phoneThird)) {
            nsDialog.jAlert("座机格式校验错误，请确认后重新输入！");
            return false;
        }
    }
    if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($("#fax").val()) && $("#fax").val() != "") {
        nsDialog.jAlert("传真号格式错误，请确认后重新输入！");
        return false;
    }

    if (!G.isEmpty($("#mobile").val()) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#mobile").val())) {
        nsDialog.jAlert("手机号格式错误，请确认后重新输入！");
        return false;
    }

    if (!G.isEmpty($("#email").val()) && !APP_BCGOGO.Validator.stringIsEmail($("#email").val())) {
        nsDialog.jAlert("Email格式错误，请确认后重新输入！");
        return false;
    }

    if (!G.isEmpty($("#qq").val()) && !APP_BCGOGO.Validator.stringIsQq($("#qq").val())) {
        nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
        return false;
    }

    //validate name  @see $("#name").blur();
    if (APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact) {

        var contactMobiles = new Array();
        $(".single_contact  input[name$='mobile']").each(function (index) {
            if (!G.isEmpty($(this).val())) {
                contactMobiles.push($(this).val());
            }
        });
        // 手机号码格式校验
        for (var mobileIndex =0; mobileIndex<contactMobiles.length; mobileIndex++) {
            if (!G.isEmpty(contactMobiles[mobileIndex]) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(contactMobiles[mobileIndex])) {
                nsDialog.jAlert("手机号格式错误，请确认后重新输入！");
                return false;
            }
        }

        // 邮箱和QQ格式校验
        var contactMails = new Array();
        $(".single_contact input[name$='email']").each(function (index) {
            if(!G.isEmpty($(this).val())){
                contactMails.push($(this).val());
            }
        });
        for (var mailIndex =0; mailIndex< contactMails.length; mailIndex++) {
            if (!G.isEmpty(contactMails[mailIndex]) && !APP_BCGOGO.Validator.stringIsEmail(contactMails[mailIndex])) {
                nsDialog.jAlert("Email格式错误，请确认后重新输入！");
                return false;
            }
        }

        var contactQQs = new Array();
        $(".single_contact  input[name$='qq']").each(function (index, qq) {
            contactQQs.push($(this).val());
        });
        for (var qqIndex =0; qqIndex<contactQQs.length; qqIndex++) {
            if (!G.isEmpty(contactQQs[qqIndex]) && !APP_BCGOGO.Validator.stringIsQq(contactQQs[qqIndex])) {
                nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
                return false;
            }
        }

        // 校验新输入的手机是否重复
        if(isMobileDuplicate(contactMobiles)){
            return false;
        }

        if(!checkSupplierByName(supplierName)){
            return;
        };

        // 如果用户名存在
        // 有座机校验座机是否重复
        // 有手机校验手机是否重复
        if (!G.isEmpty(supplierName)) {
            if (!G.isEmpty(G.trim(phone))) {
                if(!checkSupplierByPhone(phone)){
                    return;
                };
            }
            if (!G.isEmpty(G.trim(phoneSecond))) {
                if(!checkSupplierByPhone(phoneSecond)){
                    return;
                };
            }
            if (!G.isEmpty(G.trim(phoneThird))) {
                if(!checkSupplierByPhone(phoneThird)){
                    return;
                };
            }
            if (!isMobilesEmpty(contactMobiles)) {
                if(!validateSupplierMobiles(contactMobiles,"")){
                    return;
                }
            }

            // 校验主联系人信息
            var contacts = buildNormalKeyContacts();
            var validContactCount = countValidContact(contacts);
            if (validContactCount == 2) {
                if (!mainContactIsValid(contacts)) {
                    nsDialog.jAlert("请选择主联系人。");
                    return;
                }
            } else if (validContactCount == 1) {
                var index = firstValidContactFromContacts(contacts); // 只有一个联系人的时候 设为主联系人
                if(!($("#contacts\\[" + index + "\\]\\.mainContact").val() == "1")){
                    $("#contacts\\[" + index + "\\]\\.mainContact").val("1");
                    var mainIndex = getMainContactFromContacts(contacts);
                    $("#contacts\\[" + mainIndex + "\\]\\.mainContact").val("0");
                }
            }
        }
    } else {
        if (!G.isEmpty(supplierName)) {
            if(!checkSupplierByName(supplierName)){
                return;
            };
        }
        /*if ($("#supplierId").val() == "" && flag == true) {
         if(ms!=0 && new Date().getTime()-ms<200){
         ms = 0;
         }else{
         nsDialog.jAlert("存在重复的供应商，请确认后重新输入！");
         }
         return false;
         }*/
    }


    if ($("#businessScopeSelected").find("div").size() < 1) {
      if (nsDialog.jConfirm("确定是否选择经营产品", "", function(value) {
        if (value) {
          return false;
        } else {
          infoSubmit();
        }
      }));
    } else {
      infoSubmit();
    }
}

function infoSubmit(){

    if($("#isCustomer").attr("checked") && !$("#isCustomer").attr("disabled") ) {
        nsDialog.jConfirm("是否确认该供应商既是客户又是供应商","",function(value){
           if(value) {

               $("#modifyClientDiv").dialog({
                   width:820,
                   beforeclose:function(){
                       // add by zhuj清除生成的联系人信息
                       $(".single_contact_gen").remove();
                       $(".single_contact input[name^='contacts3']").each(function () {
                           $(this).val("");
                       });
                   }
               })
               $("#modifyClientDiv").dialog("open");
               $("#modifyClientDiv #newBusinessScopeSpan").text($("#secondCategoryName").val());
               $("#modifyClientDiv #updateBusinessScopeSpan").text($("#secondCategoryName").val());

             $("#modifyClientDiv #newThirdCategoryStr").val($("#thirdCategoryIdStr").val());
               $("#modifyClientDiv #updateThirdCategoryStr").val($("#thirdCategoryIdStr").val());

               $("#modifyClientDiv #customerId").val("");
               var ajaxData = {
                           maxRows:$("#pageRows").val(),
                           customerOrSupplier: "customer",
                           filterType:"identity"
                       };
               APP_BCGOGO.Net.asyncAjax({
                   url:"customer.do?method=searchCustomerDataAction",
                   dataType:"json",
                   data:ajaxData,
                   success:function(data) {
                       $("#radExist").click();
                       initTr(data);
                       initPages(data, "customerSuggest", "customer.do?method=searchCustomerDataAction", '', "initTr", '', '',ajaxData,'');
                   }
               });
           }
        });
    }  else {
        clearDefaultAddress();
        if(!$(this).attr("lock")){
            $(this).attr("lock",true);
            $("#supplierForm").ajaxSubmit(function(data) {
                $("#mask", parent.document).css("display", "none");
                $("#iframe_PopupBox", parent.document).css("display", "none");
                var jsonObj = JSON.parse(data);
                if (!G.Lang.isEmpty(jsonObj.supplierId)) {
                    window.parent.location.reload();
                }
            });
        }

    }

}

function clearDefaultAddress(){
    if ($("#input_address").val() == $("#input_address").attr("initValue") && $("#input_address").val() == "详细地址") {
        $("#input_address").val('');
    }
}

<!-- add by zhuj -->

$(function() {
    $(".tabRecord tr").not(".tabTitle").css({"border":"1px solid #bbbbbb","border-width":"1px 0px"});
    $(".tabRecord tr:nth-child(odd)").not(".tabTitle").css("background", "#eaeaea");
    $(".tabRecord tr").not(".tabTitle").hover(
            function () {
                $(this).find("td").css({"background":"#fceba9","border":"1px solid #ff4800","border-width":"1px 0px"});

                $(this).css("cursor", "pointer");
            },
            function () {
                $(this).find("td").css({"background-Color":"#FFFFFF","border":"1px solid #bbbbbb","border-width":"1px 0px 0px 0px"});
                $(".tabRecord tr:nth-child(odd)").not(".tabTitle").find("td").css("background", "#eaeaea");
            }
    );
    $(".close").hide();
    $(".table_inputContact").find("tr:last").hover(
            function() {
                $(".close").show();
            },
            function() {
                $(".close").hide();
            }
    )
    $(".alert").hide();
    $(".hover").live("hover", function(event) {
        var _currentTarget = $(event.target).parent().find(".alert");
        _currentTarget.show();
        //因为有2px的空隙,所以绑定在parent上.
        _currentTarget.parent().mouseleave(function(event) {
            event.stopImmediatePropagation();

            if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
                _currentTarget.hide();
            }
        });
    }, function(event) {
        var _currentTarget = $(event.target).parent().find(".alert");
        if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
            $(event.target).parent().find(".alert").hide();
        }
    });
})

//-----------------------------------------

function checkSupplierByName(supplierName) {
    var jsonStr = APP_BCGOGO.Net.syncPost({
        url: "RFSupplier.do?method=getSupplierByNameAndShopId",
        data: {
            name: supplierName
        },
        cache: false,
        dataType: "json"
    });
    if (!G.isEmpty(jsonStr) && !G.isEmpty(jsonStr.supplierDTO)) {
        if (!jsonStr.exactMatch) {
            return true;
        }
        var supplierId = jsonStr.supplierDTO.idString;
        hrefHandler(supplierId, "name", supplierName);
        return false;
    }else{
        return true;
    }
}

function checkSupplierByPhone(phone) {
    var jsonStr = APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=getSupplierJsonDataByTelephone",
        data: {
            telephone: phone
        },
        cache: false,
        dataType: "json"
    });
    if (!G.isEmpty(jsonStr) && !G.isEmpty(jsonStr.idStr)) {
        var supplierId = jsonStr.idStr;
        hrefHandler(supplierId, "phone", phone)
        return false;
    } else {
        return true;
    }
}

function checkSupplierByMobile(mobile) {
    var jsonStr = APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=getSupplierJsonDataByMobile",
        data:{
            mobile:mobile
        },
        cache:false,
        dataType:"json"
    });
    if (!G.isEmpty(jsonStr) && !G.isEmpty(jsonStr.idStr)) {
        var supplierId = jsonStr.idStr;
        hrefHandler(supplierId, "mobile", mobile);
        return false;
    }else{
        return true;
    }
}

/**
* 校验新增的手机列表里面号码是否有重复
 * 重复返回true 否则false
* @param mobiles
 */
function isMobileDuplicate(mobiles) {

    var mobilesTemp = new Array();
    for (var index =0; index< mobiles.length; index++) {
        if(!G.isEmpty(mobiles[index])){
            if (GLOBAL.Array.indexOf(mobilesTemp, mobiles[index]) >= 0) {
                nsDialog.jAlert("手机号【" + mobiles[index] + "】重复，请重新填写。");
                return true;
            }
            mobilesTemp.push(mobiles[index]);
        }
    }
    return false;
}

function delContact(){
    var $single_contacts = $(this).closest("tr").siblings(".single_contact_gen").andSelf();
    if ($single_contacts && $single_contacts.length > 3) {
        $(this).closest("tr").remove();
        if ($single_contacts.length - 1 <= 3) {
            $(".warning").hide();
        }
    }else{
        $(this).parent().siblings().children('input').val(" "); // 清空该联系人所有信息
    }
}

function switchContact() {

    var $mainContact = $(this).closest("tr").siblings().find('.icon_connacter');
    $mainContact.removeClass('icon_connacter').addClass('icon_grayconnacter').addClass('hover'); // 主联系人灰化

    $(this).removeClass('icon_grayconnacter').removeClass('hover').addClass('icon_connacter'); // 当前联系人转变为主联系人

    var $alert = $(this).siblings(".alert"); // 保存alert div
    $(this).siblings(".alert").remove();

    $(this).parent().next('input').val("1"); // 设置为主联系人1

    var currentLevel = $(this).parent().prev().val();
    $(this).parent().prev('input').val("0");

    $(this).unbind("click");

    $alert.insertAfter($mainContact).hide(); // 添加alert

    $mainContact.parent().next('input').val("0"); // 修改非主联系人

    $mainContact.parent().prev('input').val(currentLevel);

    $mainContact.live("click", switchContact);

}

// 根据supplierId是否存在提示，跳转到供应商详情页面
function hrefHandler(supplierId, searchFieldType, searchField) {
    if (supplierId && !G.isEmpty(supplierId)) {
        if (G.isString(searchFieldType)) {
            if (G.trim(searchFieldType) === "name") {
                nsDialog.jConfirm("供应商名【" + searchField + "】已存在，是否跳转到详细页面进行修改？", null, function (resultValue) {
                    if (resultValue) {
                        window.parent.location = "unitlink.do?method=supplier&supplierId=" + supplierId;
                    }
                });
            } else if (G.trim(searchFieldType) === "phone") {
                nsDialog.jConfirm("座机为【" + searchField + "】的供应商已存在，是否跳转到详细页面进行修改？", null, function (resultValue) {
                    if (resultValue) {
                        window.parent.location = "unitlink.do?method=supplier&supplierId=" + supplierId;
                    }
                });
            } else if (G.trim(searchFieldType) === "mobile") {
                nsDialog.jConfirm("手机为【" + searchField + "】的供应商已存在，是否跳转到详细页面进行修改？", null, function (resultValue) {
                    if (resultValue) {
                        window.parent.location = "unitlink.do?method=supplier&supplierId=" + supplierId;
                    }
                });
            }
        }
    }
}



<!-- add end -->
</script>
</head>
<body style='overflow: hidden;color:#000000;'>
<input type="hidden" id="secondCategoryName" value="">

<form name="supplierForm" id="supplierForm" action="unitlink.do?method=newSupplier" method="post">
<input type="hidden" name="identity" id="identity"/>
<input type="hidden" name="customerId" id="customerId2"/>
<input type="hidden" name="id" id="id2"/>
<input type="hidden" id="thirdCategoryIdStr" name="thirdCategoryIdStr" value="">

<input type="hidden" name="customerKind" id="customerKind" />
<input type="hidden" name="birthdayString" id="birthdayString" />
<div class="alertMain newCustomers" id="div_show">
    <div class="alert_title">
        <div class="left"></div>
        <div class="body">新增供应商</div>
        <div class="right"></div>
    </div>
    <div class="height"></div>
    <div class="left_customer">
    <label class="lblTitle">基本信息</label>
        <div class="divTit">
            <span class="name name_left"><span class="red_color">*</span>供应商名称</span>
            <input name="supplier" id="supplier" class="txt" maxlength="20" autocomplete="off" style="width:248px;"/>
            <input id="supplierId" name="supplierId" type="hidden" value="${supplierId}"/>
        </div>
        <div class="divTit">
            <span class="name name_left"><span class="red_color">*</span>所属区域</span>
            <select id="select_province" name="province" class="txt select" style="color:#7e7e7e;">
                <option value="">所有省</option>
            </select>
            <select id="select_city" name="city" class="txt select" style="color:#7e7e7e;">
                <option value="">所有市</option>
            </select>
            <select id="select_township" name="region" class="txt select" style="color:#7e7e7e;">
                <option value="">所有区</option>
            </select>
        </div>
        <div class="divTit" style="margin-left:75px;">
            <input id="input_address" name="address" type="text" class="txt" value="详细地址" initValue="详细地址" style="width:248px;"/>
        </div>

        <div class="divTit">
            <span class="name name_left">座机</span>
            <input id="landline" maxlength="14"  value="${supplierDTO.landLine}" name="landLine" autocomplete="off" class="txt" style="width:76px;"/>
            <input id="landlineSecond" value="${supplierDTO.landLineSecond}" maxlength="14" name="landLineSecond" autocomplete="off" class="txt" style="width:76px;"/>
            <input id="landlineThird" value="${supplierDTO.landLineThird}" maxlength="14"  name="landLineThird" autocomplete="off" class="txt" style="width:75px;"/>
        </div>
        <div class="divTit">
            <span class="name name_left">传真</span>
            <input id="fax" name="fax"  maxlength="20" class="txt" style="width:98px;"/>
        </div>

        <div class="divTit">
            <span class="name name_left" style="width:40px;">简称</span>
            <input id="abbr" name="abbr" class="txt" maxlength="20" autocomplete="off" style="width:98px;"/>
        </div>
    </div>
    <!-- add by zhuj -->
        <div class="right_customer go_customer">
            <label class="lblTitle">联系人信息<span class="icon_connacter">为主联系人</span></label>
            <table cellpadding="0" cellspacing="0" class="table_contact table_inputContact tb_go">
                <col width="55">
                <col width="80">
                <col>
                <col width="90">
                <col width="37">
                <tr class="title_top">
                    <td style="padding-left:5px;">姓名</td>
                    <td>手机</td>
                    <td>Email</td>
                    <td>QQ</td>
                    <td></td>
                </tr>
                <tr class="single_contact">
                    <td><input type="text" class="txt" name="contacts[0].name" id="contacts[0].name" maxlength="11" style="width:53px;"/></td>
                    <td><input type="text" class="txt" name="contacts[0].mobile" id="contacts[0].mobile" style="width:82px;" maxlength="11"/></td>
                    <td><input type="text" class="txt" name="contacts[0].email" id="contacts[0].email" style="width:115px;" maxlength="50"/></td>
                    <td><input type="text" class="txt" name="contacts[0].qq" id="contacts[0].qq" maxlength="15"/></td>
                    <input type="hidden" name="contacts[0].level" id="contacts[0].level" value="0"/>
                    <td><a class="icon_connacter"></a><a class="close"></a></td>
                    <input type="hidden" name="contacts[0].mainContact" id="contacts[0].mainContact" value="1"/>
                </tr>
                <tr class="single_contact">
                    <td><input type="text" class="txt" name="contacts[1].name" id="contacts[1].name" maxlength="11" style="width:53px;"/></td>
                    <td><input type="text" class="txt" name="contacts[1].mobile" id="contacts[1].mobile" style="width:82px;" maxlength="11"/></td>
                    <td><input type="text" class="txt" name="contacts[1].email" id="contacts[1].email" style="width:115px;" maxlength="50"/></td>
                    <td><input type="text" class="txt" name="contacts[1].qq" id="contacts[1].qq" maxlength="15"/></td>
                    <input type="hidden" name="contacts[1].level" id="contacts[1].level" value="1"/>
                    <td>
                        <a class="icon_grayconnacter hover"></a><a class="close"></a>
                        <div class="alert" style="display: none;">
                            <span class="arrowTop"></span>
                            <div class="alertAll">
                                <div class="alertLeft"></div>
                                <div class="alertBody">点击设为主联系人</div>
                                <div class="alertRight"></div>
                            </div>
                        </div>
                    </td>
                    <input type="hidden" name="contacts[1].mainContact" id="contacts[1].mainContact" value="0"/>
                </tr>
                <tr class="single_contact">
                    <td><input type="text" class="txt" name="contacts[2].name" id="contacts[2].name" maxlength="11" style="width:53px;"/></td>
                    <td><input type="text" class="txt" name="contacts[2].mobile" id="contacts[2].mobile" style="width:82px;" maxlength="11"/></td>
                    <td><input type="text" class="txt" name="contacts[2].email" id="contacts[2].email" style="width:115px;" maxlength="50"/></td>
                    <td><input type="text" class="txt" name="contacts[2].qq" id="contacts[2].qq" maxlength="15"/></td>
                    <input type="hidden" name="contacts[2].level" id="contacts[2].level" value="2"/>
                    <td>
                        <a class="icon_grayconnacter hover"></a><a class="close"></a>
                        <div class="alert">
                            <span class="arrowTop"></span>
                            <div class="alertAll">
                                <div class="alertLeft"></div>
                                <div class="alertBody">点击设为主联系人</div>
                                <div class="alertRight"></div>
                            </div>
                        </div>
                    </td>
                    <input type="hidden" name="contacts[2].mainContact" id="contacts[2].mainContact" value="0"/>
                </tr>
            </table>
        </div>



        <div class="divTit" style="width:100%;">经营产品</div>

        <div id="businessScopeSelected" class="selectList"><b>已选择</b></div>
        <div class="businessRange" id="businessScopeDiv"></div>




        <div class="height"></div>
        <label class="lblTitle">账户信息</label>

        <div class="divTit"  style="width: 230px;">
            <span class="name">开户行</span>&nbsp;<input id="bank"  maxlength="20"  name="bank"
                                                           class="txt" style="width:160px;"/>
        </div>
        <div class="divTit"  style="width: 230px;">
            <span class="name">开户名</span>&nbsp;<input id="accountName" name="accountName" maxlength="20"
                                                           class="txt" style="width:160px;"/>
        </div>
        <div class="divTit"  style="width: 230px;">
            <span class="name">账号</span>&nbsp;<input id="account" name="account" maxlength="20"
                                                          class="txt" style="width:160px;"/>
        </div>
        <div class="divTit"  style="width: 230px;">
            <span class="name">结算方式</span>&nbsp;
            <select id="settlementType" class="txt select" name="settlementType" style="color:#7e7e7e;width: 120px; margin-left: -6px;">
                <option value="">--请选择--</option>
                <c:forEach items="${settlementTypeList}" var="item">
                    <option value="${item.key}">${item.value}</option>
                </c:forEach>
            </select>

        </div>
        <div class="divTit"  style="width: 230px;">
            <span class="name">发票类型</span>
            <select id="invoiceCategory" class="txt select" name="invoiceCategory" style="color:#7e7e7e;width: 120px;">
                <option value="">--请选择--</option>
                <c:forEach items="${invoiceCategoryList}" var="item">
                    <option value="${item.key}">${item.value}</option>
                </c:forEach>
            </select>

        </div>
    <div class="divTit" style="width:100%;">
        <span class="name" style="vertical-align:top;">备注</span>&nbsp;<textarea rows="2" cols="40" maxlength="400" name="memo" id="memo" class="txt textarea"></textarea>
    </div>
    <div class="clear"></div>
    <div class="button" style="width:100%">
        <a class="btnSure" id="confirmBtn1">确 定</a>
        <a class="btnSure" id="cancelBtn">取 消</a>
        <c:if test="${wholesalerVersion}">
            <input type="checkbox" id="isCustomer"/>&nbsp;也是客户
        </c:if>
    </div>
    <div class="clear"></div>
</div>
</form>
<div id="modifyClientDiv" style="display:none" class="alertMain newCustomers">
    <jsp:include page="modifySupplier.jsp"></jsp:include>
</div>
</body>
</html>
