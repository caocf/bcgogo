<%--
  同client.jsp 汽配版.
  User: ndong
  Date: 14-6-17
  Time: 上午11:38
  To change this template use File | Settings | File Templates.
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
<title>更多客户信息</title>

<style type="text/css">
    .divWidth{
        width: 200px;
    }
    .div2Width{
        width: 285px;
    }
</style>
<link rel="stylesheet" type="text/css" href="styles/up1<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moreHistory1<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/moreUserinfo1<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css"
      href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
<link rel="stylesheet" type="text/css"
      href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
<link rel="stylesheet" type="text/css"
      href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css"/>
<link rel="stylesheet" href="js/extension/jquery/plugin/tipsy-master/src/stylesheets/tipsy.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>


<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/dataTables/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript"
        src="js/components/ui/bcgogo-searchBar<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-highlightcomplete<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-autocomplete<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-autocomplete-multiselect<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-detailsList<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-detailsList-multiselect<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-searchcomplete<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-searchcomplete-multiselect<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript"
        src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-qqInvoker<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/utils/arrayUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/clientInfo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/moreUserInfo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/clientVehicle<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehicleValidator<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
    APP_BCGOGO.Permission.Version.FourSShopVersion =${empty fourSShopVersions?false:fourSShopVersions};
    <bcgogo:permissionParam permissions="WEB.VERSION.HAS_CUSTOMER_CONTACTS,WEB.VERSION.HAS_SUPPLIER_CONTACTS">
    APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact =${WEB_VERSION_HAS_CUSTOMER_CONTACTS}; // 客户多联系人
    </bcgogo:permissionParam>

    $().ready(function () {
        $(".J_QQ").each(function (index, value) {
            $(this).multiQQInvoker({
                QQ: $(this).attr("data_qq")
            });
        });
        $("#birthdayString").datepicker({
            "numberOfMonths":1,
            "showButtonPanel":true,
            "changeYear":false,
            "changeMonth":true,
            "yearRange":"c-100:c+100",
            "yearSuffix":"",
            "onClose":function(dateText, inst) {
                if (dateText) {
                    dateText = dateText.replace(/^\d{4}\-/g, "");
                    this.value = dateText;
                }
            }
        });

        jQuery("input[name$='.dateString']")
                .live("click", function () {
                    jQuery(this).blur();
                })
                .datepicker({
                    "numberOfMonths":1,
                    "showButtonPanel":true,
                    "changeYear":true,
                    "changeMonth":true,
                    "yearRange":"c-100:c+100",
                    "yearSuffix":""
                })
                .live("change", function() {
                    var nowTime = new Date().getTime();
                    var str = $(this).val();
                    str = str.replace(/-/g, "/");
                    var selectTime = new Date(str).getTime();
                    if (nowTime < selectTime) {
                        nsDialog.jAlert("购车日期不能晚于当前日期！", null, function() {
                            $(this).val("");
                        });
                    }
                });
    });
</script>
<script type="text/javascript">
// flag判断该客户名是否在数据库已经存在
var flag = false;
// previousName用于存前一次输入的在数据库存在重复的客户名
var previousName = "";
//毫秒计时，计算两次不同事件的时间间隔，防止光标还在客户名的时候，直接点保存时重复alert
var ms = 0;
//当出现客户重名，确定修改为当前用户之后，保存ID与对应的客户名，如果客户名再次修改，保存的ID清空
var tempName = "";
jQuery(document).ready(function() {
    $("#select_province").val(${customerRecordDTO.province});
    $("#select_province").change();
    $("#select_city").val(${customerRecordDTO.city});
    $("#select_city").change();
    $("#select_township").val(${customerRecordDTO.region});

    //输入过滤
    $("#account").keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });
    $("#phone,#phoneSecond,#phoneThird,#fax").keyup(function() {
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
        $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").live("change", checkNumberInput);
        document.getElementById("account").addEventListener("input", checkNumberInput, false);
        document.getElementById("phone").addEventListener("input", checkTelInput, false);
        document.getElementById("phoneSecond").addEventListener("input", checkTelInput, false);
        document.getElementById("phoneThird").addEventListener("input", checkTelInput, false);
        document.getElementById("fax").addEventListener("input", checkTelInput, false);
    }
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

    $(".single_contact input[name$='email']").blur(function () {
        if (!APP_BCGOGO.Validator.stringIsEmail($(this).val()) && $(this).val() != "") {
            nsDialog.jAlert("邮箱格式错误，请确认后重新输入！");
        }
    });
    $(".single_contact input[name$='qq']").blur(function () {
        var qq = $(this).val();
        if (!G.isEmpty(qq) && qq.length < 5) {
            nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
        }
    });


    jQuery("#confirmBtn").live("click", function() {
        if (APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact) { // add by zhuj
            var customerName = $("#name").val();
            var currCustomerId = $("#customerId").val();
            if (G.isEmpty(customerName) || G.isEmpty($.trim(customerName))) {
                nsDialog.jAlert("用户名必须填写!");
                return false;
            }
            if (!$("#areaInfo")[0] && !G.isEmpty($("#select_province").attr("id"))) {
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

            // validate mobile
            var contactMobiles = new Array();
            $("input[name$='mobile']").each(function (index) {
                if (!G.isEmpty($(this).val())) {
                    contactMobiles.push($(this).val());
                }
            });
            for (var index in contactMobiles) {
                if (!G.isEmpty(contactMobiles[index]) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(contactMobiles[index])) {
                    nsDialog.jAlert("手机号格式错误，请确认后重新输入！");
                    return false;
                }
            }

            // validate mail
            var contactMails = new Array(3);
            $("input[name$='mail']").each(function (index) {
                contactMails.push($(this).val());
            });
            for (var index in contactMails) {
                if (!G.isEmpty(contactMails[index]) && !APP_BCGOGO.Validator.stringIsEmail(contactMails[index])) {
                    nsDialog.jAlert("Email格式错误，请确认后重新输入！");
                    return false;
                }
            }

            // validate qq
            var contactQQs = new Array(3);
            $("input[name$='qq']").each(function (index, qq) {
                contactQQs.push($(this).val());
            });
            for (var index in contactQQs) {
                if (!G.isEmpty(contactQQs[index]) && !APP_BCGOGO.Validator.stringIsQq(contactQQs[index])) {
                    nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
                    return false;
                }
            }

            // 校验新输入的手机是否重复
            if (isMobileDuplicate(contactMobiles)) {
                return false;
            }

            // 校验用户名是否重复
            if (!G.isEmpty($.trim(customerName)) && G.isEmpty($.trim($("#phone").val())) && isMobilesEmpty(contactMobiles)) {
                if (!checkCustomerName(customerName, currCustomerId)) {
                    return;
                }
            }
            if (!G.isEmpty($.trim(customerName)) && G.isEmpty($.trim($("#phoneSecond").val())) && isMobilesEmpty(contactMobiles)) {
                if (!checkCustomerName(customerName, currCustomerId)) {
                    return;
                }
            }
            if (!G.isEmpty($.trim(customerName)) && G.isEmpty($.trim($("#phoneThird").val())) && isMobilesEmpty(contactMobiles)) {
                if (!checkCustomerName(customerName, currCustomerId)) {
                    return;
                }
            }

            // 如果用户名存在
            // 有座机校验座机是否重复
            // 有手机校验手机是否重复
            if (!G.isEmpty(customerName)) {
                if (!G.isEmpty(G.trim(phone))) {
                    if (!checkCustomerPhone(phone, currCustomerId)) {
                        return;
                    }
                }
                if (!G.isEmpty(G.trim(phoneSecond))) {
                    if (!checkCustomerPhone(phoneSecond, currCustomerId)) {
                        return;
                    }
                }
                if (!G.isEmpty(G.trim(phoneThird))) {
                    if (!checkCustomerPhone(phoneThird, currCustomerId)) {
                        return;
                    }
                }
                if (!isMobilesEmpty(contactMobiles)) {
                    if(!validateCustomerMobiles(contactMobiles,currCustomerId)){
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
            parentValueFillAndSubmit(); // modified by zhuj

        } else {
            var licenceNos = [];
            var duplicate = false;
            $("#table_vehicle input[id$='licenceNo']").each(function() {
                if ($.inArray($(this).val(), licenceNos) != -1) {
                    duplicate = true;
                    return;
                }
                licenceNos.push($(this).val());
            });

            if (duplicate) {
                nsDialog.jAlert("存在重复车牌！");
                return;
            }
            if (checkVehicleNo()) {
                return;
            }

            var customerName = $("#name").val();
            if ($.trim(customerName) != "" && $("#customerId").val() != "") {
                APP_BCGOGO.Net.syncGet({
                    url: "customer.do?method=searchCustomerByName",
                    data: {
                        customerName: customerName
                    },
                    cache: false,
                    dataType: "json",
                    success: function (jsonStr) {
                        if (jsonStr.results != undefined && jsonStr.results[0] != null && jsonStr.results[0] != "[]") {
                            var customerDTOs = jsonStr.results;
                            var currentCustomerId = $("#customerId").val();
                            var isHaveSameCustomer = false;
                            for (var i = 0, len = customerDTOs.length; i < len; i++) {
                                var customerId = customerDTOs [i].idStr;
                                if (customerId != "" && customerId != null && customerId == currentCustomerId) {
                                    isHaveSameCustomer = true;
                                    break;
                                }
                            }
                            if (!isHaveSameCustomer) {
                                nsDialog.jConfirm("客户名【" + customerName + "】已存在，请修改客户名或者添加手机号予以区分，继续保存客户信息？", "", function (booleanVal) {
                                    if (booleanVal) {
                                        checkThisForm();
                                    } else {
                                        return;
                                    }
                                });
                            }
                            else {
                                checkThisForm();
                            }
                        }
                        else {
                            checkThisForm();
                        }
                    },
                    error: function () {
                        nsDialog.jAlert("网络异常！");
                    }
                });
            } else {
                checkThisForm();
            }
        }
    });

    // 绑定 联系人列表 删除事件
    $('.close').live("click", delContact);
    // 绑定 联系人列表 点击成为主联系人事件
    $('.icon_grayconnacter').live("click", switchContact);

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
    var allLevelZero = true;
    $("#contactInputTable input[id$='level']").each(function() {
        if (G.isEmpty($(this).val()) && $(this).val() != '0') {
            allLevelZero = false;
            return;
        }
    });
    if (allLevelZero) {
        $("#contactInputTable input[id$='level']").each(function(i) {
            $(this).val(i);
        });
    }
    var noMainContact = true;
    $("#contactInputTable input[id$='mainContact']").each(function() {
        if (G.isEmpty($(this).val()) && $(this).val() != '1') {
            noMainContact = false;
            return;
        }
    });
    if (noMainContact) {
        $("#contactInputTable input[id$='mainContact']").each(function(i) {
            if (i == 0) {
                $(this).val(1);
                $(this).parent("td").find(".icon_grayconnacter").click();
            }
        });
    }

    if (APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact) {
        $("#saveOrUpdateCustomer input[id$='.mobile']").bind("blur",
                function() {
                    var $thisDom = $(this);
                    $thisDom.val($.trim($thisDom.val()));
                    var mobile = $thisDom.val();
                    if (!G.Lang.isEmpty(mobile)) {
                        if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
                            nsDialog.jAlert("对不起手机号码格式不正确，请重新输入！", null, function() {
                                $thisDom.val("");
                                $thisDom.focus();
                            });
                        }
                    }
                }).bind("keyup", function (e) {
                    $(this).val($(this).val().replace(/[^\d]+/g, ""));
                });
    }

    setInputValue();
});

function checkThisForm() {

    //判断用户名/联系人是否为空
    var customerName = $("#name").val();
    if (customerName == null || $.trim(customerName) == "") {
        nsDialog.jAlert("用户名必须填写!");
        return false;
    }

    if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#mobile").val()) && $("#mobile").val() != "") {
        nsDialog.jAlert("手机号格式错误，请确认后重新输入！");
        return false;
    }
    if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($("#phone").val()) && $("#phone").val() != "") {
        nsDialog.jAlert("座机号格式错误，请确认后重新输入！");
        return false;
    }
    if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($("#phoneSecond").val()) && $("#phoneSecond").val() != "") {
        nsDialog.jAlert("座机号格式错误，请确认后重新输入！");
        return false;
    }
    if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($("#phoneThird").val()) && $("#phoneThird").val() != "") {
        nsDialog.jAlert("座机号格式错误，请确认后重新输入！");
        return false;
    }
    if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($("#fax").val()) && $("#fax").val() != "") {
        nsDialog.jAlert("传真号格式错误，请确认后重新输入！");
        return false;
    }
    if (!APP_BCGOGO.Validator.stringIsQq($("#qq").val()) && $("#qq").val() != "") {
        nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
        return false;
    }
    if (!APP_BCGOGO.Validator.stringIsEmail($("#email").val()) && $("#email").val() != "") {
        nsDialog.jAlert("邮箱格式错误，请确认后重新输入！");
        return false;
    }
    if (checkAllDuplicateVehicles()) {
        nsDialog.jAlert("单据有重复内容，请修改或删除。");
        return false;
    }
    parentValueFillAndSubmit(); // modified by zhuj
    return false;
}


//-----------------------------------------
function parentValueFillAndSubmit() {
    var notDeleteVehicleInfo  = false;
    clearDefaultAddress();

    jQuery("#saveOrUpdateCustomer").ajaxSubmit(function(result) {
        if (!result.success) {
            nsDialog.jAlert(result.msg);
            return;
        }

        var parentPageType = $("#orderType",window.parent.document).val();
        if (!G.Lang.isEmpty(parentPageType) && parentPageType == "APPOINT_ORDER") {
            window.parent.document.getElementById("mask").style.display = "none";
            if (window.parent.document.getElementById("iframe_moreUserInfo")) {
                window.parent.document.getElementById("iframe_moreUserInfo").style.display = "none";
            }
            if (!G.Lang.isEmpty(result) && !G.Lang.isEmpty(result["data"]) && !G.Lang.isEmpty(result["data"]["id"])) {
                var customerId = result["data"]["id"];
                var customerInfo = window.parent.getCustomerInfoById({customerId: customerId});
                if (!G.Lang.isEmpty(customerInfo)) {
                    var vehicleDTOs = customerInfo["vehicleDTOs"];
                    var vehicleDTO = null;
                    var parentPageVehicleId = $("#vehicleId",window.parent.document).val();
                    if (!G.Lang.isEmpty(vehicleDTOs)) {
                        if (!G.Lang.isEmpty(parentPageVehicleId) && vehicleDTOs.length > 1) {
                            for (var i = 0, len = vehicleDTOs.length; i < len; i++) {
                                if(!G.Lang.isEmpty(vehicleDTOs[i]) && vehicleDTOs[i]["idStr"] == parentPageVehicleId){
                                    vehicleDTO = vehicleDTOs[i];
                                    break;
                                }
                            }
                        }
                        if(vehicleDTO == null && vehicleDTOs.length == 1){
                            vehicleDTO = vehicleDTOs[0];
                        }
                    }
                    window.parent.drawCustomerInfo(customerInfo["customerDTO"], vehicleDTO, customerInfo["memberDTO"]);
                }
            }
        } else {
            //<--下面这段代码之前是在ajax提交之前就做了
            var isInvoicing = window.parent.document.getElementById("isInvoicing");
            if (isInvoicing != null && isInvoicing.value == "true") {
                window.parent.document.getElementById("customerName").value = document.getElementById("name").value;
            } else {
                window.parent.document.getElementById("customer").value = document.getElementById("name").value;
            }
            if (APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact) {
                if ($("#contacts\\[0\\]\\.mobile").val()) {
                    window.parent.document.getElementById("mobile").value = $("#contacts\\[0\\]\\.mobile").val();
                }
                if ($("#contacts\\[0\\]\\.name").val()) {
                    window.parent.document.getElementById("contact").value = $("#contacts\\[0\\]\\.name").val();
                }
            } else {
                window.parent.document.getElementById("mobile").value = document.getElementById("mobile").value;
                window.parent.document.getElementById("contact").value = document.getElementById("contact").value;
            }
            if (window.parent.document.getElementById("address")) {
                window.parent.document.getElementById("address").value = document.getElementById("input_address2").value;
            }
            $("input[id$='\\.licenceNo']").each(function() {
                var lineNo = this.id.substr(8, this.id.length - 18);
                var vehicleId = $("#vehicles" + lineNo + "\\.id").val();
                var parentPageVehicleId = $("#vechicleId", window.parent.document).val();
                if(parentPageVehicleId == null){
                    parentPageVehicleId = $("#vehicleId", window.parent.document).val();
                }
                if (vehicleId == parentPageVehicleId) {
                    notDeleteVehicleInfo = true;
                    var vehicleBrand = "#vehicles" + lineNo + "\\.vehicleBrand";
                    var vehicleModel = "#vehicles" + lineNo + "\\.vehicleModel";
                    var vehicleYear  =  "#vehicles" + lineNo + "\\.year";
                    var vehicleEngine  =  "#vehicles" + lineNo + "\\.engine";
                    var vehicleColor =   "#vehicles" + lineNo + "\\.vehicleColor";
                    var vehicleEngineNo =   "#vehicles" + lineNo + "\\.vehicleEngineNo";
                    var vehicleChassisNo  =  "#vehicles" + lineNo + "\\.chassisNumber";
                    $("#licenceNo", window.parent.document).val($(this).val());
                    $("#vehicleContact", window.parent.document).val($("#vehicles" + lineNo + "\\.vehicleContact").val());
                    $("#vehicleMobile", window.parent.document).val($("#vehicles" + lineNo + "\\.vehicleMobile").val());
                    $("#brand", window.parent.document).val($(vehicleBrand).val()).attr("disabled", !G.Lang.isEmpty($(vehicleBrand).val()));
                    if(!G.Lang.isEmpty($(vehicleBrand).val())){
                        $("#brand", window.parent.document).attr("disabled", true);
                    }
                    $("#model", window.parent.document).val($(vehicleModel).val()).attr("disabled", !G.Lang.isEmpty($(vehicleModel).val()));
                    $("#year", window.parent.document).val($(vehicleYear).val());
                    $("#engine", window.parent.document).val($(vehicleEngine).val());
                    $("#vehicleColor", window.parent.document).val($(vehicleColor).val()).attr("disabled", !G.Lang.isEmpty($(vehicleColor).val()));
                    $("#vehicleEngineNo", window.parent.document).val($(vehicleEngineNo).val()).attr("disabled", !G.Lang.isEmpty($(vehicleEngineNo).val()));
                    $("#vehicleChassisNo", window.parent.document).val($(vehicleChassisNo).val()).attr("disabled", !G.Lang.isEmpty($(vehicleChassisNo).val()));
                }
            });
            if (window.parent.document.getElementById("landline") != null) {
                window.parent.document.getElementById("landline").value = document.getElementById("phone").value;
            }
            if (window.parent.document.getElementById("landLine") != null) {
                window.parent.document.getElementById("landLine").value = document.getElementById("phone").value;
            }
            window.parent.document.getElementById("mask").style.display = "none";
           if (window.parent.document.getElementById("iframe_moreUserInfo")) {
                window.parent.document.getElementById("iframe_moreUserInfo").style.display = "none";
            }
           if (window.parent.document.getElementById("iframe_PopupBox")) {
                window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
            }
            //-->

            var jsonObj = result.data;
            var licenceNo = jsonObj.licenceNo;
            if (window.parent.document.getElementById("customerId")) {
                window.parent.document.getElementById("customerId").value = jsonObj.id;
            }
            if (window.parent.document.getElementById("customerOrSupplierId")) {
                window.parent.document.getElementById("customerOrSupplierId").value = jsonObj.id;
            }

            if (window.parent.document.getElementById("licenceNo") && !window.parent.document.getElementById("licenceNo").value) {
                if (!G.isEmpty(licenceNo)) {
                    window.parent.document.getElementById("licenceNo").value = licenceNo;
                    window.parent.document.getElementById("vechicleId").value = jsonObj.vehicleId;
                } else {
                    window.parent.document.getElementById("licenceNo").value = $(".vehic").eq(0).children().eq(0).children().val();
                }
            }else if(window.parent.document.getElementById("vechicleId") && window.parent.document.getElementById("licenceNo") && window.parent.document.getElementById("licenceNo").value==licenceNo){
                window.parent.document.getElementById("vechicleId").value = jsonObj.vehicleId;
            }

            if (typeof window.parent.checkCustomerById == "function" && !G.Lang.isEmpty(jsonObj.id)) {
                window.parent.checkCustomerById(jsonObj.id, notDeleteVehicleInfo);
            }

            APP_BCGOGO.Net.asyncAjax({
                url:"sale.do?method=getTotalDebts",
                data:{
                    customerId:jsonObj.id
                },
                dataType:"json",
                success:function(data) {
                    $("#allDebt", window.parent.document).html(data.totalDebt);
                    $("#allReturnDebt", window.parent.document).html(data.totalReturnDebt);
                    $("#customerConsume", window.parent.document).html(data.totalAmount);
                    data.totalDebt * 1 > 0 ? $("#qiankuangWrap", window.parent.document).show() : $("#qiankuangWrap", window.parent.document).hide();
                }
            });
        }
    });
}


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
    $(this).parent().siblings().children('input').val(" "); // 清空该联系人所有信息
}

function switchContact() {
    $(".main_info").remove();
    $(this).closest("tr").find("td:first").append('<div class="main_info"></div>');
    var tIndex=$(this).closest("td").attr("index");
    var count_level=1;
    $(".opt_contact").each(function(){
        var index=$(this).closest("td").attr("index");
        var vStr="";
        if(tIndex==index){
            vStr='主联系人'+
                    '<input type="hidden" name="contacts['+tIndex+'].mainContact"'+
                    'id="contacts['+tIndex+'].mainContact" value="1"/>';
            $(this).closest("tr").find('input[id$=".level"]').val(0);
        }else{
            vStr= '<a class="blue_color icon_grayconnacter">设为主联系人</a>'+
                    '<input type="hidden" name="contacts['+index+'].mainContact"'+
                    'id="contacts['+index+'].mainContact" value="0"/>';
            $(this).closest("tr").find('input[id$=".level"]').val(count_level);
            count_level++;
        }
        $(this).text("");
        $(this).append(vStr);
    });

//    $(this).removeClass('icon_grayconnacter').removeClass('hover').addClass('icon_connacter'); // 当前联系人转变为主联系人
//
//    var $alert = $(this).siblings(".alert"); // 保存alert div
//    $(this).siblings(".alert").remove();
//
//    $(this).parent().next('input').val("1"); // 设置为主联系人1
//
//    var currentLevel = $(this).parent().prev().val();
//    $(this).parent().prev('input').val("0");
//
//    $(this).unbind("click");
//
//    $alert.insertAfter($mainContact).hide(); // 添加alert
//    $alert.hide();
//    $mainContact.parent().next('input').val("0"); // 修改非主联系人
//    $mainContact.parent().prev('input').val(currentLevel);
//    $mainContact.live("click", switchContact);

}

function checkCustomerName(name, currCustomerId) {
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
        if (customerId == currCustomerId) {
            return true;
        }
        hrefHandler(customerId, "name", name);
        return false
    } else {
        return true;
    }
}

function checkCustomerPhone(phone, currCustomerId) {
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
        if (customerId == currCustomerId) {
            return true;
        }
        hrefHandler(customerId, "phone", phone);
        return false;
    } else {
        return true;
    }
}

function checkCustomerMobile(mobile, currCustomerId) {
    var jsonStr = APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=getCustomerJsonDataByMobile",
        data: {
            mobile: mobile,
            customerId: currCustomerId
        },
        cache: false,
        dataType: "json"
    });
    if (!jsonStr.success)       //无匹配返回false
        return true;
    var obj = jsonStr.data;
    if (jsonStr.success && jsonStr.msg == 'customer' && !G.isEmpty(obj.idStr)) {
        var customerId = obj.idStr;
        if (currCustomerId == customerId) {
            return true;
        }
        hrefHandler(customerId, "mobile", mobile);
        return false;
    }else if(jsonStr.msg == 'supplier') {
        nsDialog.jAlert("手机为【"+mobile+"】的供应商已存在，请重新输入。");
        $("input[id$='mobile']").each(function(){
            if($(this).val() == mobile){
                $(this).val("");
                return;
            }
        });
        return false;
    }  else {
        return true;
    }
}

// 根据customerId是否存在提示，跳转到客户详情页面
function hrefHandler(customerId, searchFieldType, searchField) {
    if (customerId && !G.isEmpty(customerId)) {
        if (G.isString(searchFieldType)) {
            if (G.trim(searchFieldType) === "name") {
                nsDialog.jConfirm("客户名【" + searchField + "】已存在，是否跳转到客户页面进行修改？", null, function (resultValue) {
                    if (resultValue) {
                        window.parent.location = "unitlink.do?method=customer&customerId=" + customerId;
                    }
                });
            } else if (G.trim(searchFieldType) === "mobile") {
                nsDialog.jConfirm("手机为【" + searchField + "】的客户已存在，是否跳转到客户页面进行修改？", null, function (resultValue) {
                    if (resultValue) {
                        window.parent.location = "unitlink.do?method=customer&customerId=" + customerId;
                    }
                });
            } else if (G.trim(searchFieldType) === "phone") {
                nsDialog.jConfirm("座机为【" + searchField + "】的客户已存在，是否跳转到客户页面进行修改？", null, function (resultValue) {
                    if (resultValue) {
                        window.parent.location = "unitlink.do?method=customer&customerId=" + customerId;
                    }
                });
            }
        }
    }
}

// 判断当前的联系人手机号信息是否为空
function isMobilesEmpty(contactMobiles) {
    if (G.isEmpty(contactMobiles)) {
        return true;
    }
    if (contactMobiles.constructor == Array) {
        return contactMobiles.every(function(item, index, obj) {
            return G.isEmpty(item);
        });
    }
    return true;
}

function adjustIframeStyle(){
    $("#iframe_PopupBox",window.parent.document).height($(".alertMain").height()+100);
//    $(document.getElementById("iframe_PopupBox").contentWindow.document).find(".alertMain").height()
}

</script>
</head>

<body>
<input type="hidden" id="orderType" value="clientInfo">
<form:form commandName="customerRecordDTO" action="txn.do?method=updateCustomer" method="post"
           id="saveOrUpdateCustomer">
    <form:hidden path="shopId" value="${customerRecordDTO.shopId}"/>
    <form:hidden path="id" value="${customerRecordDTO.id}"/>
    <form:hidden path="licenceNo" value="${customerRecordDTO.licenceNo}"/>
    <form:hidden path="customerId" value="${customerRecordDTO.customerId}"/>
    <c:set var="detailAddress"
           value="${(customerRecordDTO.address==null || customerRecordDTO.address == '')?'详细地址':customerRecordDTO.address}"/>
    <div class="alertMain newCustomers">
        <div class="alert_title">
            <div class="left"></div>
            <div class="body">更多客户信息</div>
            <div class="right"></div>
        </div>
        <div class="height"></div>
        <div>
            <label class="lblTitle">基本信息</label>
            <div class="divTit">
                <span class="name name_left"><span class="red_color">*</span>客户名称</span>
                <form:input path="name" value="${customerRecordDTO.name}" class="onlineShop" style="width:272px;" readonly="true"/>
            </div>
            <div class="divTit">
                <span class="name name_left">客户类别</span>
                <form:select path="customerKind" class="txt select" style="width:119px;">
                    <form:options items="${customerTypeMap}"/>
                </form:select>
            </div>
            <div class="divTit">
                <span class="name name_left" style="width:40px;">简称</span>
                <form:input path="shortName" value="${customerRecordDTO.shortName}" type="text" class="txt" style="width:111px;" maxlength="10" autocomplete="off"/>
            </div>
            <div class="divTit">
                <span class="name name_left"><span class="red_color">*</span>地址</span>
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
            <div class="divTit">
                <input id="input_address2" name="address" type="text" class="txt"  style="width:274px; margin-left:8px;"
                       value="${customerRecordDTO.address}" placeHolder="详细地址"/>
            </div>
            <div class="divTit">
                <span class="name name_left">座机</span>
                <form:input path="phone" value="${customerRecordDTO.phone}" type="text"  maxlength="14" autocomplete="off" class="txt" style="width:84px;"/>
                <form:input path="phoneSecond" value="${customerRecordDTO.phoneSecond}"  maxlength="14" autocomplete="off" type="text" class="txt" style="width:83px;"/>
                <form:input path="phoneThird" value="${customerRecordDTO.phoneThird}"  maxlength="14" autocomplete="off" type="text" class="txt" style="width:83px;"/>
            </div>
            <div class="divTit">
                <span class="name name_left">传真</span>
                <form:input path="fax" value="${customerRecordDTO.fax}" maxlength="20" type="text" class="txt" style="width:111px;"/>
            </div>
            <div class="divTit">
                <span class="name name_left" style="width:40px;">生日</span>
                <form:input path="birthdayString" value="${customerRecordDTO.birthdayString}" type="text" class="txt" style="width:111px;" readonly="true"/>
            </div>

        </div>

        <div class="height"></div>
        <bcgogo:hasPermission permissions="WEB_VERSION_HAS_CUSTOMER_CONTACTS">
            <label class="lblTitle" style=" padding-bottom:5px;">联系人信息</label>
            <div class="clear"></div>
            <div class="contact_info">
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td width="3%">&nbsp;</td>
                        <td width="18%">姓名</td>
                        <td width="18%">手机</td>
                        <td width="18%">Email</td>
                        <td width="18%">QQ</td>
                        <td width="16%">&nbsp;</td>
                    </tr>
                    <c:forEach items="${customerRecordDTO.contacts}" var="contact" varStatus="status">
                        <tr>
                            <c:choose>
                                <c:when test="${status.index == 0}">
                                    <td><div class="main_info"></div></td>
                                </c:when>
                                <c:otherwise>
                                    <td>&nbsp;</td>
                                </c:otherwise>
                            </c:choose>

                            <td>
                                <input type="text" class="txt" maxlength="20" name="contacts[${status.index}].name"
                                       id="contacts[${status.index}].name" value="${contact.name}" style="width:125px;" />
                                <input type="hidden" name="contacts[${status.index}].id" id="contacts[${status.index}].id"
                                       value="${contact.id}"/>
                            </td>
                            <td>
                                <input type="text" class="txt" maxlength="11" name="contacts[${status.index}].mobile"
                                       id="contacts[${status.index}].mobile" style="width:125px;" value="${contact.mobile}"/>
                            </td>
                            <td>
                                <input type="text" class="txt" name="contacts[${status.index}].email"
                                       id="contacts[${status.index}].email" maxlength="50" style="width:125px;"
                                       value="${contact.email}"/>
                            </td>
                            <td>
                                <input type="text" class="txt" name="contacts[${status.index}].qq"  style="width:125px;"
                                       id="contacts[${status.index}].qq" maxlength="20" value="${contact.qq}"/>
                            </td>
                            <input type="hidden" name="contacts[${status.index}].level" id="contacts[${status.index}].level"
                                   value="${status.index}"/>
                            <c:choose>
                                <c:when test="${status.index == 0}">
                                    <td class="opt_contact" index="${status.index}">
                                        主联系人
                                        <input type="hidden" name="contacts[${status.index}].mainContact"
                                               id="contacts[${status.index}].mainContact" value="1"/>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td class="opt_contact" index="${status.index}">
                                        <a class="blue_color icon_grayconnacter">设为主联系人</a>
                                        <input type="hidden" name="contacts[${status.index}].mainContact"
                                               id="contacts[${status.index}].mainContact" value="0"/>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </bcgogo:hasPermission>
        <div class="height"></div>
        <label class="lblTitle">账户信息</label>
        <div class="divTit">
            <span class="name">开户行</span>
            <form:input path="bank" value="${customerRecordDTO.bank}" maxlength="20"
                        type="text" class="txt" style="width:165px;" />
        </div>
        <div class="divTit">
            <span class="name">开户名</span>
            <form:input path="bankAccountName" maxlength="20" value="${customerRecordDTO.bankAccountName}"
                        type="text" class="txt" style="width:165px;" />
        </div>
        <div class="divTit">
            <span class="name">账&nbsp;号</span>
            <form:input path="account" maxlength="20" value="${customerRecordDTO.account}"
                        type="text" class="txt" style="width:165px;" />
        </div>
        <div class="divTit">
            <span class="name">结算方式</span>
            <form:select path="settlementType" class="txt select" cssStyle="width:170px;">
                <form:option value="" label="--请选择--"/>
                <form:options items="${settlementTypeMap}"/>
            </form:select>
        </div>
        <div class="divTit">
            <span class="name">发票类型</span>
            <form:select path="invoiceCategory" class="txt select" cssStyle="width:165px;">
                <form:option value="" label="--请选择--"/>
                <form:options items="${invoiceCatagoryMap}"/>
            </form:select>
        </div>
        <div class="divTit" style="width:100%;">
            <span class="name" style="vertical-align:top;">备注</span>
            <textarea class="txt textarea" style="width:633px;" name="memo"  maxlength="400" id="memo" rows="2" cols="40">${customerRecordDTO.memo}</textarea>
        </div>
        <div class="height"></div>
        <div class="height"></div>
        <div class="button">
            <a id="confirmBtn" class="btnSure">确 定</a>
            <a id="cancleBtn" class="btnSure">取 消</a>
        </div>
    </div>
</form:form>
</body>
</html>