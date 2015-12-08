<%--
    @author:zhangjuntao
    @notice:addCustomerDilog.jsp 同步
--%>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>新增客户信息</title>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
<link rel="stylesheet" href="js/extension/jquery/plugin/dataTables/css/jquery.dataTables_themeroller.css"/>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css"/>
<link rel="stylesheet" href="js/extension/jquery/plugin/tipsy-master/src/stylesheets/tipsy.css"/>

<link rel="stylesheet" type="text/css" href="styles/up1<%=ConfigController.getBuildVersion()%>.css"/>
<%--<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>--%>
<link rel="stylesheet" type="text/css" href="styles/moreHistory1<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moreUserinfo1<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css"
      href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
<link rel="stylesheet" type="text/css"
      href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
<link rel="stylesheet" type="text/css"
      href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<style type="css/text">
    #ui-datepicker-div, .ui-datepicker {
        font-size: 80%;
    }

    .table_input .chassisNumber {
        text-transform: uppercase;
    }
</style>
<%@include file="/WEB-INF/views/iframe_script.jsp" %>

<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/clientInfo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/moreUserInfo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/clientVehicle<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/customer/modifyClient<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehicleValidator<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/customerOrSupplier/customerSupplierBusinessScope<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">

<bcgogo:permissionParam permissions="WEB.VERSION.HAS_CUSTOMER_CONTACTS,WEB.VERSION.HAS_SUPPLIER_CONTACTS">
APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact =${WEB_VERSION_HAS_CUSTOMER_CONTACTS}; // 客户多联系人
APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact =${WEB_VERSION_HAS_SUPPLIER_CONTACTS}; // 供应商多联系人
</bcgogo:permissionParam>
APP_BCGOGO.Permission.Version.WholesalerVersion =${empty wholesalerVersion?false:wholesalerVersion};
APP_BCGOGO.Permission.Version.FourSShopVersion =${empty fourSShopVersions?false:fourSShopVersions};
$(document).ready(function() {

    // flag 判断是否唯一
    var flag = false;
    //毫秒计时，计算两次不同事件的时间间隔，防止光标还在客户名的时候，直接点保存时重复alert
    var ms = 0;

    $("#birthdayString").datepicker({
        "numberOfMonths" : 1,
        "changeYear":true,
        "changeMonth":true,
        "dateFormat": "mm-dd",
        "yearRange": "c-100, c",
        "yearSuffix":"",
        "showButtonPanel":true
    });

    // 座机、传真输入过滤
    $("#phone,#phoneSecond,#phoneThird,#fax").keyup(function() {
        $(this).val($(this).val().replace(/[^\d\-]+/g, "").replace(/\-+/g, "-"));
        if ($(this).val().charAt(0) == '-') {
            $(this).val("");
        }
    });

    $("#table_vehicle input[id$='.year']").keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });

    $("input[id$='.dateString']").live("change", function() {
        var nowTime = new Date().getTime();
        var str = $(this).val();
        str = str.replace(/-/g, "/");
        var selectTime = new Date(str).getTime();
        if (nowTime < selectTime) {
            nsDialog.jAlert("购车日期不能晚于当前日期！");
            $(this).val("");
        }
    });

    //IE或者别的浏览器
    if (/msie/i.test(navigator.userAgent)) {
        // TODO IE下的待定
    }
    else {
        $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").live("change", checkNumberInput);
        $("#account").live("change", checkNumberInput, false);
        $("#phone").live("change", checkTelInput, false);
        $("#phoneSecond").live("change", checkTelInput, false);
        $("#phoneThird").live("change", checkTelInput, false);
        $("#fax").live("change", checkTelInput, false);
    }

    $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });

    $('#account').keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });


    $("#fax").blur(function() {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($(this).val()) && $(this).val() != "") {
            nsDialog.jAlert("传真号格式错误，请确认后重新输入！");
        }
    });
    $(".single_contact input[name$='email']").blur(function() {
        if (!APP_BCGOGO.Validator.stringIsEmail($(this).val()) && $(this).val() != "") {
            nsDialog.jAlert("邮箱格式错误，请确认后重新输入！");
        }
    });
    $(".single_contact input[name$='qq']").blur(function() {
        var qq = $(this).val();
        if (!G.isEmpty(qq) && qq.length < 5) {
            nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
        }
    });

    $("#name").blur(function() {
        if (!G.isEmpty($.trim($("#name").val()))) {
            $("#confirmBtn1").attr("lock", true);
        }

        ms = new Date().getTime();
        var name = $("#name").val();
        var customerId = 0;
        // modified by zhuj 校验用户名唯一性条件　座机为空 联系人手机列表为空
        if (APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact) {
            var contactMobiles = new Array();
            $(".single_contact input[name$='mobile']").each(function(index) {
                if (!G.isEmpty($(this).val())) {
                    contactMobiles.push($(this).val());
                }
            });
            if (!G.isEmpty($.trim(name)) && G.isEmpty($.trim($("#phone").val())) && isMobilesEmpty(contactMobiles)) {
                checkCustomerName(name);
            }
            if (!G.isEmpty($.trim(name)) && G.isEmpty($.trim($("#phoneSecond").val())) && isMobilesEmpty(contactMobiles)) {
                checkCustomerName(name);
            }
            if (!G.isEmpty($.trim(name)) && G.isEmpty($.trim($("#phoneThird").val())) && isMobilesEmpty(contactMobiles)) {
                checkCustomerName(name);
            }
        } else {
            if (!G.isEmpty($.trim(name))) {
                checkCustomerName(name);
            }
        }
        if (!G.isEmpty($.trim(name))) {
            setTimeout(function () {
                $("#confirmBtn1").removeAttr("lock", true);
            }, 200);
        }

        /*else if ($.trim(name) != "" && $("#customerId").val() != "" && tempName != name) {
         APP_BCGOGO.Net.syncPost({
         url:"customer.do?method=searchCustomerByName",
         data:{
         customerName:name
         },
         cache:false,
         dataType:"json",
         success:function(jsonStr) {
         //存在重复
         if (jsonStr.results!=undefined && jsonStr.results[0] != null) {
         var customerId = jsonStr.results[0].idStr;
         if (customerId != "" && customerId != null) {
         flag = true;
         nsDialog.jConfirm("客户名【" + name + "】已存在，是否修改为当前客户？",null,function(resultValue){
         if (resultValue) {
         tempName = name;
         fillCustomerFormUsingJsonSearchByCustomerName(jsonStr);
         }
         });
         }
         }
         }
         });
         }*/
    });

    $("#confirmBtn1").live("click", function () {
        if ($(this).attr("lock")) {
            return;
        }
        checkFormData(flag, ms);
    });
    $("input[id='name']").bind("keyup", function() {
        $(this).val(APP_BCGOGO.StringFilter.stringSpaceFilter($(this).val()));
    });

    // 绑定 联系人列表 删除事件
    $('.close').live("click", delContact);
    // 绑定 联系人列表 点击成为主联系人事件
    $('.icon_grayconnacter').live("click", switchContact);
    //重新设置弹出框位置
    $("body").css("width", 800);
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


function checkFormData(flag, ms) {

    //重新计算经营产品
    resetThirdCategoryIdStr();


    var customerName = $("#name").val();
    if (G.isEmpty(customerName) || G.isEmpty($.trim(customerName))) {
        nsDialog.jAlert("用户名必须填写!");
        return false;
    }

    if (!G.isEmpty($("#select_province").attr("id"))) {
        if (!validateProvinceCity("select_province", "select_city")) {
            return false;
        }
    }

    /*formate validate start*/

    //validate phone
    var phone = $("#phone").val(); // 校验座机
    var phoneSecond=$("#phoneSecond").val();
    var phoneThird=$("#phoneThird").val();
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

    // validate fax
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
    if (APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact) {

        // validate mobile   .single_contact input[name$='mobile']
        var contactMobiles = new Array();
        $(".single_contact input[name$='mobile']").each(function (index) {
            contactMobiles.push($(this).val());
        });
        for (var index = 0; index < contactMobiles.length; index++) {
            if (!G.isEmpty(contactMobiles[index]) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(contactMobiles[index])) {
                nsDialog.jAlert("手机号格式错误，请确认后重新输入！");
                return false;
            }
        }

        // validate mail
        var contactMails = new Array();
        $(".single_contact input[name$='mail']").each(function(index) {
            contactMails.push($(this).val());
        });
        for (var index = 0; index < contactMails.length; index++) {
            if (!G.isEmpty(contactMails[index]) && !APP_BCGOGO.Validator.stringIsEmail(contactMails[index])) {
                nsDialog.jAlert("Email格式错误，请确认后重新输入！");
                return false;
            }
        }

        // validate qq
        var contactQQs = new Array();
        $(".single_contact input[name$='qq']").each(function(index, qq) {
            contactQQs.push($(this).val());
        });
        for (var index = 0; index < contactQQs.length; index++) {
            if (!G.isEmpty(contactQQs[index]) && !APP_BCGOGO.Validator.stringIsQq(contactQQs[index])) {
                nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
                return false;
            }
        }
        /* formate validate end */

        // 校验新输入的手机是否重复
        if (isMobileDuplicate(contactMobiles)) {
            return false;
        }

        // 校验用户名是否重复
        if (!G.isEmpty($.trim(customerName)) && G.isEmpty($.trim($("#phone").val())) && isMobilesEmpty(contactMobiles)) {
            if (!checkCustomerName(customerName)) {
                return;
            }
        }
        if (!G.isEmpty($.trim(customerName)) && G.isEmpty($.trim($("#phoneSecond").val())) && isMobilesEmpty(contactMobiles)) {
            if (!checkCustomerName(customerName)) {
                return;
            }
        }
        if (!G.isEmpty($.trim(customerName)) && G.isEmpty($.trim($("#phoneSecond").val())) && isMobilesEmpty(contactMobiles)) {
            if (!checkCustomerName(customerName)) {
                return;
            }
        }

        // 如果用户名存在
        // 有座机校验座机是否重复
        // 有手机校验手机是否重复
        if (!G.isEmpty(customerName)) {
            if (!G.isEmpty(G.trim(phone))) {
                if (!checkCustomerPhone(phone, $("#phone"))) {
                    return;
                }
            }
            if (!G.isEmpty(G.trim(phoneSecond))) {
                if (!checkCustomerPhone(phoneSecond, $("#phoneSecond"))) {
                    return;
                }
            }
            if (!G.isEmpty(G.trim(phoneThird))) {
                if (!checkCustomerPhone(phoneThird, $("#phoneThird"))) {
                    return;
                }
            }
            if (!isMobilesEmpty(contactMobiles)) {
                if(!validateCustomerMobiles(contactMobiles,"")){
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
                if (!($("#contacts\\[" + index + "\\]\\.mainContact").val() == "1")) {
                    $("#contacts\\[" + index + "\\]\\.mainContact").val("1");
                    var mainIndex = getMainContactFromContacts(contacts);
                    $("#contacts\\[" + mainIndex + "\\]\\.mainContact").val("0");
                }
            }

        }
    } else {
//        if (!G.isEmpty(customerName)) {
//            if(!checkCustomerName(customerName)){
//                return;
//            }
//        }
        if (!G.isEmpty(phone)) {
            if (!checkCustomerPhone(phone)) {
                return;
            }
        }
        if (!G.isEmpty(phoneSecond)) {
            if (!checkCustomerPhone(phoneSecond)) {
                return;
            }
        }
        if (!G.isEmpty(phoneThird)) {
            if (!checkCustomerPhone(phoneThird)) {
                return;
            }
        }
    }

    if (!APP_BCGOGO.Permission.Version.WholesalerVersion) {

      if (checkVehicleNo()) {
        return;
      }
    }
    //TODO 校验主联系人信息是否为空

    /*if (!APP_BCGOGO.Validator.stringIsQq($("#qq").val()) && $("#qq").val() != "") {
     nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
     return false;
     }
     if (!APP_BCGOGO.Validator.stringIsEmail($("#email").val()) && $("#email").val() != "") {
     nsDialog.jAlert("邮箱格式错误，请确认后重新输入！");
     return false;
     }*/
    if (checkAllDuplicateVehicles()) {
        nsDialog.jAlert("单据有重复内容，请修改或删除。");
        return false;
    }
    if ($("#businessScopeDiv").length > 0 && $("#businessScopeSelected").find("div").size() < 1) {
        if (nsDialog.jConfirm("确定是否选择经营产品", "", function(value) {
            if (value) {
                return false;
            } else {
                if(APP_BCGOGO.Permission.Version.WholesalerVersion){
                    infoSubmit();
                }else{
                    infoSubmit();
                }
            }
        }));
    } else {
        if(APP_BCGOGO.Permission.Version.WholesalerVersion){
            infoSubmit();
        }else{
            infoSubmit();
        }
    }

}

function infoSubmit() {
  if ($("#isSupplier").attr("checked") && !$("#isSupplier").attr("disabled")) {
    if (nsDialog.jConfirm("是否确认该客户既是客户又是供应商", "", function(value) {
      if (value) {
          $("#modifyClientDiv").dialog({
              width:820,
              beforeclose: function () {
                  // add by zhuj清除联系人div里面带入的相关联系人信息
                  /*$(".single_contact input[name^='contacts2']").each(function () {
                   $(this).val("");
                   });
                   $(".single_contact input[name^='contacts3']").each(function () {
                   $(this).val("");
                   });*/
                  $(".single_contact_gen").remove();
                  $(".single_contact input[name^='contacts3']").each(function () {
                      $(this).val("");
                  });
              }
          })

                $("#modifyClientDiv #newBusinessScopeSpan").text($("#secondCategoryName").val());
                $("#modifyClientDiv #updateBusinessScopeSpan").text($("#secondCategoryName").val());

                $("#modifyClientDiv #newThirdCategoryStr").val($("#thirdCategoryIdStr").val());
                $("#modifyClientDiv #updateThirdCategoryStr").val($("#thirdCategoryIdStr").val());

                $("#modifyClientDiv").dialog("open");
                $("#modifyClientDiv #supplierId").val("");
                var ajaxData = {
                    maxRows:$("#pageRows").val(),
                    customerOrSupplier: "supplier",
                    filterType:"identity"
                };
                APP_BCGOGO.Net.asyncAjax({
                    url:"supplier.do?method=searchSupplierDataAction",
                    dataType:"json",
                    data:ajaxData,
                    success:function(data) {
                        $("#radExist").click();
                        initTr(data);
                        initPages(data, "supplierSuggest", "supplier.do?method=searchSupplierDataAction", '', "initTr", '', '', ajaxData, '');
                    }
                });
            } else {
                $("#isSupplier").attr("checked", false);
            }
        }));
    } else {
        clearDefaultAddress();
        if (!$("#confirmBtn1").attr("lock")) {
            $("#confirmBtn1").attr("lock", true);
            try {
                $("#thisform").ajaxSubmit(function (result) {
                    if (result && result.success) {
                        $("#mask", parent.document).css("display", "none");
                        $("#iframe_PopupBox", parent.document).css("display", "none");
                        parent.location.reload();
                    } else {
                        if (!result.success) {
                            nsDialog.jAlert(result.msg);
                        }
                    }
                    $("#confirmBtn1").removeAttr("lock");
                });
            } catch (e) {
                nsDialog.jAlert("网络异常");
            } finally {
                $("#confirmBtn1").removeAttr("lock");
            }

        }

    }
}

function clearDefaultAddress() {
    if ($("#input_address").val() == $("#input_address").attr("initValue") && $("#input_address").val() == "详细地址") {
        $("#input_address").val('');
    }
    if ($("#input_address2").val() == $("#input_address2").attr("initValue") && $("#input_address2").val() == "详细地址") {
        $("#input_address2").val('');
    }
}

<!-- 以下add by zhuj -->
<!-- 联系人相关的样式初始化 -->
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
});

//-----------------------------------------

/**
 * 校验新增的手机列表里面号码是否有重复
 * 重复返回true 否则false
 * @param mobiles
 */
function isMobileDuplicate(mobiles) {

    var mobilesTemp = new Array();
    for (var index in  mobiles) {
        if (!G.isEmpty(mobiles[index])) {
            if (GLOBAL.Array.indexOf(mobilesTemp, mobiles[index]) >= 0) {
                nsDialog.jAlert("手机号【" + mobiles[index] + "】重复，请重新填写。");
                return true;
            }
            mobilesTemp.push(mobiles[index]);
        }
    }
    return false;
}

function delContact() {
    var $single_contacts = $(this).closest("tr").siblings(".single_contact_gen").andSelf();
    if ($single_contacts && $single_contacts.length > 3) {
        $(this).closest("tr").remove();
        if ($single_contacts.length - 1 <= 3) {
            $(".warning").hide();
        }
    } else {
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

function checkCustomerName(name) {
    var jsonStr = APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=searchCustomerByName",
        data: {
            customerName: name
        },
        cache: false,
        dataType: "json"
    });
    if (!G.isEmpty(jsonStr.results) && !G.isEmpty(jsonStr.results[0])) {
        var customerId = jsonStr.results[0].idStr;
        hrefHandler(customerId, "name", name);
        return false
    } else {
        return true;
    }
}

function checkCustomerPhone(phone, phoneDom) {
    var jsonStr = APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=getCustomerJsonDataByTelephone",
        data: {
            telephone: phone
        },
        cache: false,
        dataType: "json"
    });
    if (!G.isEmpty(jsonStr) && !G.isEmpty(jsonStr.idStr)) {
        var customerId = jsonStr.idStr;
        hrefHandler(customerId, "phone", phone, phoneDom);
        return false;
    } else {
        return true;
    }
}

function checkCustomerMobile(mobile, mobileDome) {
    var jsonStr = APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=getCustomerJsonDataByMobile",
        data: {
            mobile: mobile
        },
        cache: false,
        dataType: "json"
    });
    if (!G.isEmpty(jsonStr) && !G.isEmpty(jsonStr.idStr)) {
        var customerId = jsonStr.idStr;
        hrefHandler(customerId, "mobile", mobile, mobileDome);
        return false
    } else {
        return true;
    }
}

// 根据customerId是否存在提示，跳转到客户详情页面
function hrefHandler(customerId, searchFieldType, searchField, fieldDom) {
    if (customerId && !G.isEmpty(customerId)) {
        if (G.isString(searchFieldType)) {
            if (G.trim(searchFieldType) === "name") {
                nsDialog.jConfirm("客户名【" + searchField + "】已存在，是否跳转到客户页面进行修改？", null, function (resultValue) {
                    if (resultValue) {
                        window.parent.location = "unitlink.do?method=customer&customerId=" + customerId;
                    }
                });
            } else if (G.trim(searchFieldType) === "mobile") {
                nsDialog.jConfirm("手机为【" + searchField + "】的客户已存在，是否跳转到客户页面进行修改？", null, function(resultValue) {
                    if (resultValue) {
                        window.parent.location = "unitlink.do?method=customer&customerId=" + customerId;
                    } else {
                        if ($(fieldDom)[0]) {
                            $(fieldDom).val("").focus();
                        }
                    }
                });
            } else if (G.trim(searchFieldType) === "phone") {
                nsDialog.jConfirm("座机为【" + searchField + "】的客户已存在，是否跳转到客户页面进行修改？", null, function(resultValue) {
                    if (resultValue) {
                        window.parent.location = "unitlink.do?method=customer&customerId=" + customerId;
                    } else {
                        if ($(fieldDom)[0]) {
                            $(fieldDom).val("").focus();
                        }
                    }
                });
            }
        }
    }
}

<!-- add end-->

</script>
<style type="text/css">
    .divWidth{
        width: 200px;
    }
    .div2Width{
        width: 285px;
    }
</style>
</head>

<body>
<input type="hidden" id="orderType" value="addClientInfo">
<input type="hidden" id="secondCategoryName" value="">

<form:form name="thisform" id="thisform" commandName="customerRecordDTO" action="customer.do?method=addcustomer"
           method="post">
<form:hidden path="shopId" value="${customerRecordDTO.shopId}"/>
<form:hidden path="thirdCategoryIdStr" value=""/>
<form:hidden path="id" value="${customerRecordDTO.id}"/>
<form:hidden path="licenceNo" value="${customerRecordDTO.licenceNo}"/>
<form:hidden path="customerId" value="${customerRecordDTO.customerId}"/>
<input type="hidden" id="pageRows" value="10"/>
<input type="hidden" id="rowStart" value="1"/>
<input type="hidden" name="pageTip" id="pageTip" value="<%=(request.getAttribute("fromPage"))%>"/>
<input type="hidden" name="identity" id="identity"/>
<input type="hidden" name="supplierId" id="supplierId2"/>
<input type="hidden" name="businessScope" id="businessScope"/>

<div class="alertMain newCustomers" id="div_show" style="width: 900px;">
<div class="alert_title">
    <div class="left"></div>
    <div class="body" style="width: 896px;">新增客户</div>
    <div class="right"></div>
</div>
<div class="clear height"></div>
<div class="left_customer" <c:if test="${wholesalerVersion}">style="width:420px;"</c:if> <c:if test="${!wholesalerVersion}">style="width:896px;"</c:if>>
    <label class="lblTitle">基本信息</label>

    <c:if test="${!wholesalerVersion}" var="isWholesalerVersion">
        <div class="divTit">
            <span class="name"><span class="red_color">*</span>客户名称</span>&nbsp;<form:input path="name"
                                                                                            value="${customerRecordDTO.name}"
                                                                                            class="txt" maxlength="20"
                                                                                            autocomplete="off"
                                                                                            style="width:330px;"/>
        </div>
    </c:if>

    <c:if test="${wholesalerVersion}" var="isWholesalerVersion">
        <div class="divTit">
            <span class="name"><span class="red_color">*</span>客户名称</span>&nbsp;<form:input path="name"
                                                                                            value="${customerRecordDTO.name}"
                                                                                            class="txt" maxlength="20"
                                                                                            autocomplete="off"
                                                                                            style="width:310px;"/>
        </div>
        <div class="divTit">
            <span class="name"><span class="red_color">*</span>所属区域</span>&nbsp;<select id="select_province"
                                                                                        name="province"
                                                                                        class="txt select"
                                                                                        style="color:#7e7e7e;">
            <option value="">所有省</option>
        </select>
            <select id="select_city" name="city" class="txt select" style="color:#7e7e7e;">
                <option value="">所有市</option>
            </select>
            <select id="select_township" name="region" class="txt select" style="color:#7e7e7e;">
                <option value="">所有区</option>
            </select>
        </div>
        <div class="divTit"><span class="name">详细地址</span>&nbsp;<input id="input_address" name="address" type="text"
                                                                       class="txt" value="详细地址" initValue="详细地址"
                                                                       style="width:310px;"/>
        </div>

    </c:if>
    <c:if test="${!isWholesalerVersion}">
        <div class="divTit">
            <span class="name" style="width: 58px;">地址</span>
            <input id="input_address2" name="address" type="text" class="txt" value="详细地址" initValue="详细地址"  placeHolder="详细地址"
                   style="width:330px;"/>
        </div>
    </c:if>

    <!-- modified by zhuj 微型版和汽修版 -->
    <bcgogo:permission>
        <bcgogo:if resourceType="logic"
                   permissions="WEB_VERSION_HAS_CUSTOMER_CONTACTS">
            <!-- 拥有多联系人权限 -->
        </bcgogo:if>
        <bcgogo:else>
            <div class="divTit divWidth">
                <span class="name">联系人</span>&nbsp;<form:input path="contact" value="${customerRecordDTO.contact}"
                                                               id="contact" class="txt" maxlength="20"
                                                               autocomplete="off" style="width:130px;"/>
            </div>
            <div class="divTit divWidth">
                <span class="name">手机</span>&nbsp;<form:input path="mobile" value="${customerRecordDTO.mobile}"
                                                              id="mobile" maxlength="11" autocomplete="off"
                                                              class="txt" style="width:130px;"/>
            </div>
            <div class="divTit divWidth">
                <span class="name">Email</span>&nbsp;<form:input path="email" value="${customerRecordDTO.email}"
                                                                 id="email" maxlength="50" class="txt" style="width:130px;"/>
            </div>
            <div class="divTit divWidth">
                <span class="name">QQ</span>&nbsp;<form:input path="qq" value="${customerRecordDTO.qq}" id="qq"
                                                              maxlength="10" class="txt" style="width:130px;"/>
            </div>
        </bcgogo:else>
    </bcgogo:permission>



    <div class="divTit">
        <span class="name">座机</span>&nbsp;<form:input
            path="phone" value="${customerRecordDTO.phone}" maxlength="14"
            autocomplete="off" class="txt" style="width:96px;"/>
        <form:input
                path="phoneSecond" value="${customerRecordDTO.phoneSecond}" maxlength="14"
                autocomplete="off" class="txt" style="width:96px;"/>
        <form:input
                path="phoneThird" value="${customerRecordDTO.phoneThird}" maxlength="14"
                autocomplete="off" class="txt" style="width:96px;"/>
    </div>
    <div class="divTit divWidth">
        <span class="name">传真</span>&nbsp;<form:input path="fax" value="${customerRecordDTO.fax}" maxlength="20"
                                                      class="txt" style="width:130px;"/>
    </div>
    <div class="divTit divWidth">
        <span class="name" <c:if test="${wholesalerVersion}">style="width:38px;"</c:if>>生日</span>&nbsp;<form:input
            path="birthdayString" value="${customerRecordDTO.birthdayString}"
            class="txt" readonly="true" style="width:130px;"/>
    </div>

    <div class="divTit divWidth">
        <span class="name">客户类别</span>
    <span id="input_settle">
        <form:select path="customerKind" cssStyle="width:135px;" class="txt select">
            <form:options items="${customerTypeMap}"/>
        </form:select>
    </span>
    </div>
    <div class="divTit divWidth">
        <span class="name" <c:if test="${wholesalerVersion}">style="width:38px;"</c:if>>简称</span>&nbsp;<form:input path="shortName" value="${customerRecordDTO.shortName}"
                                                      class="txt"
                                                      maxlength="10" autocomplete="off" style="width:130px;"/>
    </div>
</div>

<!-- 联系人信息 -->
<bcgogo:hasPermission permissions="WEB_VERSION_HAS_CUSTOMER_CONTACTS">
    <div class="right_customer go_customer">
        <label class="lblTitle">联系人信息<span class="icon_connacter">为主联系人</span></label>
        <table cellpadding="0" cellspacing="0" class="table_contact table_inputContact tb_go">
        <col width="50">
        <col width="80">
        <col>
        <col width="90">
        <col width="32">
        <tr class="title_top">
            <td>姓名</td>
            <td>手机</td>
            <td>Email</td>
            <td>QQ</td>
            <td></td>
        </tr>
        <c:forEach items="${customerRecordDTO.contacts}" var="contact" varStatus="status">
            <tr class="single_contact">
                <td><input type="text" class="txt" name="contacts[${status.index}].name" maxlength="11"
                           id="contacts[${status.index}].name" style="width:62px;"/></td>
                <td><input type="text" class="txt" name="contacts[${status.index}].mobile"
                           id="contacts[${status.index}].mobile" style="width:82px;" maxlength="11"/></td>
                <td><input type="text" class="txt" name="contacts[${status.index}].email"
                           id="contacts[${status.index}].email" style="width:120px;" maxlength="50"/></td>
                <td><input type="text" class="txt" name="contacts[${status.index}].qq" maxlength="15"
                           id="contacts[${status.index}].qq"/></td>
                <input type="hidden" name="contacts[${status.index}].level" id="contacts[${status.index}].level"
                       value="${status.index}"/>
                <c:choose>
                    <c:when test="${status.index eq 0}">
                        <td style="width:40px;"><a class="icon_connacter"></a><a class="close"></a></td>
                        <input type="hidden" name="contacts[${status.index}].mainContact"
                               id="contacts[${status.index}].mainContact" value="1"/>
                    </c:when>
                    <c:otherwise>
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
                            <input type="hidden" name="contacts[${status.index}].mainContact"
                                   id="contacts[${status.index}].mainContact" value="0"/>
                        </c:otherwise>
                    </c:choose>
                </tr>
            </c:forEach>
        </table>
    </div>
</bcgogo:hasPermission>
<!-- add end-->

<div class="height"></div>
<label class="lblTitle">账户信息</label>

<div class="divTit div2Width">
    <span class="name">开户行</span>&nbsp;<form:input path="bank" value="${customerRecordDTO.bank}" maxlength="20"
                                                   class="txt" cssStyle="width:160px;"/>
</div>
<div class="divTit div2Width">
    <span class="name">开户名</span>&nbsp;<form:input path="bankAccountName" maxlength="20"
                                                   value="${customerRecordDTO.bankAccountName}"
                                                   class="txt" cssStyle="width:160px;"/>
</div>
<div class="divTit div2Width">
    <span class="name">账号</span>&nbsp;<form:input path="account" maxlength="20" value="${customerRecordDTO.account}"
                                                  class="txt" cssStyle="width:160px;"/>
</div>
<div class="divTit div2Width">
    <span class="name">结算方式</span><span id="input_sett1">
<form:select path="settlementType" class="txt select" cssStyle="width:165px;">
    <form:option value="" label="--请选择--"/>
    <form:options items="${settlementTypeMap}"/>
</form:select>
</span>
</div>
<div class="divTit div2Width">
    <span class="name">发票类型</span><span id="input_sett2">
<form:select path="invoiceCategory" class="txt select" cssStyle="width:165px;">
    <form:option value="" label="--请选择--"/>
    <form:options items="${invoiceCatagoryMap}"/>
</form:select>
</span>
</div>
<div class="divTit" style="width:100%;">
    <span class="name" style="vertical-align:top;">备注</span>&nbsp;<textarea rows="2" cols="40" name="memo" id="memo"
                                                                            maxlength="400" class="txt textarea"
                                                                            style="width:730px;">${customerRecordDTO.memo}</textarea>
</div>


<c:if test="${wholesalerVersion}" var="isWholesalerVersion">
    <div class="divTit" style="width:100%;">经营产品</div>
    <div id="businessScopeSelected" class="selectList"><b>已选择</b></div>
    <div class="businessRange" id="businessScopeDiv"></div>
</c:if>


<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
    <div class="clear height"></div>
    <table cellpadding="0" cellspacing="0" class="tabRecord" id="table_vehicle">
        <colgroup>
            <col width="80">
            <col>
            <col width="95">
            <col width="75">
            <col width="75">
            <col width="50">
            <col width="50">
            <col width="85">
            <col width="90">
            <col>
            <col width="70">
            <col width="50">
        </colgroup>
        <tr class="tabTitle">
            <td>车牌号</td>
            <td>车主</td>
            <td>车主手机</td>
            <td>车辆品牌</td>
            <td>车型</td>
            <td>年代</td>
            <td>排量</td>
            <td>购车日期</td>
            <td>车架号</td>
            <td>发动机号</td>
            <td>车身颜色</td>
            <td>操作<input class="opera2" type="button" style="display:none;"></td>
        </tr>

        <c:forEach items="${customerRecordDTO.vehicles}" var="vehicle" varStatus="status">
            <tr class="vehic">
                <td>
                    <input class="table_input validationDuplicate" maxlength="9" style="width:65px" type="text"
                           name="vehicles[${status.index}].licenceNo" id="vehicles${status.index}.licenceNo"
                           value="${vehicle.licenceNo!=null?vehicle.licenceNo:''}"/>
                    <input type="hidden" name="vehicles[${status.index}].id" id="vehicles${status.index}.id"
                           value="${vehicle.id!=null?vehicle.id:''}"/>
                </td>
                <td><input class="table_input" maxlength="20" style="" type="text"
                           name="vehicles[${status.index}].contact" id="vehicles${status.index}.vehicleContact"
                           value="${vehicle.contact!=null?vehicle.contact:''}"/></td>
                <td><input class="table_input" maxlength="11" style="width:80px" type="text"
                           name="vehicles[${status.index}].mobile" id="vehicles${status.index}.vehicleMobile"
                           value="${vehicle.mobile!=null?vehicle.mobile:''}"/></td>
                <td><input class="table_input" maxlength="8" style="width:80px" type="text"
                           name="vehicles[${status.index}].brand" id="vehicles${status.index}.vehicleBrand" pagetype="customerVehicle"
                           value="${vehicle.brand!=null?vehicle.brand:''}"/></td>
                <td><input class="table_input" maxlength="8" style="width:80px" type="text"
                           name="vehicles[${status.index}].model" id="vehicles${status.index}.vehicleModel" pagetype="customerVehicle"
                           value="${vehicle.model!=null?vehicle.model:''}"/></td>
                <td><input class="table_input" maxlength="4" style="width:38px" type="text"
                           name="vehicles[${status.index}].year" id="vehicles${status.index}.year"
                           value="${vehicle.year!=null?vehicle.year:''}"/></td>
                <td><input class="table_input" maxlength="12" style="width:38px" type="text"
                           name="vehicles[${status.index}].engine" id="vehicles${status.index}.engine"
                           value="${vehicle.engine!=null?vehicle.engine:''}"/></td>
                <td>
                    <input readonly="true" class="table_input datePicker" type="text"
                           name="vehicles[${status.index}].dateString" id="vehicles${status.index}.dateString"
                           value="${vehicle.dateString!=null?vehicle.dateString:''}" style="width:100%"/>
                </td>
                <td><input class="table_input chassisNumber" maxlength="17" style="width:80px" type="text"
                           name="vehicles[${status.index}].chassisNumber" id="vehicles${status.index}.chassisNumber"
                           value="${vehicle.chassisNumber!=null?vehicle.chassisNumber:''}"/></td>
                <td>
                    <input class="txt" maxlength="8" type="text" name="vehicles[${status.index}].engineNo"
                           id="vehicles${status.index}.vehicleEngineNo"
                           value="${vehicle.engineNo!=null?vehicle.engineNo:''}"/>
                </td>
                <td>
                    <input class="txt" maxlength="8" type="text" name="vehicles[${status.index}].color"
                           id="vehicles${status.index}.vehicleColor" value="${vehicle.color!=null?vehicle.color:''}"/>
                </td>
                <td>
                    <input class="opera1" type="button" id="vehicles${status.index}.deletebutton"
                           name="vehicles[${status.index}].deletebutton"/>
                </td>
            </tr>
        </c:forEach>
    </table>
</bcgogo:hasPermission>

<div class="clear height"></div>
<div class="his_bottom" style="width:700px;">
    <div class="i_leftBtn" style="display:none">
        <div class="">&lt;</div>
        <div class="onlin_his">1</div>
        <div class="">2</div>
        <div class="">3</div>
        <div class="">4</div>
        <div class="">5</div>
        <div class="">&gt;</div>
    </div>

    <div class="button btnSupplier" style="width:100%">
        <a class="btnSure" id="confirmBtn1">确 定</a>
        <a class="btnSure" id="cancleBtn">取消</a>
        <c:if test="${wholesalerVersion}">
            <input type="checkbox" id="isSupplier"/>&nbsp;也是供应商
            <input type="hidden" id="existRelation" value="false"/>
        </c:if>
    </div>
    <div class="clear"></div>
</div>

</div>
</form:form>
<div id="div_brand" class="i_scroll" style="display:none;">
    <div class="Container">
        <div id="Scroller">
            <div class="Scroller-Container" id="Scroller-Container_id">
            </div>
        </div>
    </div>
</div>
<div id="div_brand_head" class="i_scroll" style="display:none;z-index: 2000">
    <div class="Scroller-Container" id="Scroller-Container_id_head" style="width:100%;padding:0;margin:0;"></div>
</div>

<div id="modifyClientDiv" class="alertMain newCustomers" style="display:none">
    <%@include file="modifyClient.jsp" %>
</div>
</body>
</html>