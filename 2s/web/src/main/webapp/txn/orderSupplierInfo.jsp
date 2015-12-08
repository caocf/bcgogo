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
<link rel="stylesheet" type="text/css"
      href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>

<%@include file="/WEB-INF/views/iframe_script.jsp" %>

<script type="text/javascript" src="js/supplierInfo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
<script type="text/javascript"
        src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">

<bcgogo:permissionParam permissions="WEB.VERSION.HAS_CUSTOMER_CONTACTS,WEB.VERSION.HAS_SUPPLIER_CONTACTS">
APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact =${WEB_VERSION_HAS_SUPPLIER_CONTACTS}; // 供应商多联系人
</bcgogo:permissionParam>

function checkFormData(flag, ms) {
    clearDefaultValue();
    //判断用户名/联系人是否为空
    var supplierName = $("#supplier").val();
    if (G.isEmpty(G.trim(supplierName))) {
        alert("供应商名称必须填写!");
        return false;
    }

    if($("#areaInfo").length == 0) {
        if (!validateProvinceCity("select_province", "select_city")) {
            return false;
        }
    }

    //validate phone
    var phone = $("#landLine").val();
    var phoneSecond=$("#landLineSecond").val();
    var phoneThird=$("#landLineThird").val();
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

    var contactMobiles = new Array();
    $("input[name$='mobile']").each(function (index) {
        if (!G.isEmpty($(this).val())) {
            contactMobiles.push($(this).val());
        }
    });
    // 手机号码格式校验
    for (var mobileIndex = 0; mobileIndex < contactMobiles.length; mobileIndex++) {
        if (!G.isEmpty(contactMobiles[mobileIndex]) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(contactMobiles[mobileIndex])) {
            nsDialog.jAlert("手机号格式错误，请确认后重新输入！");
            return false;
        }
    }

    // 邮箱和QQ格式校验
    var contactMails = new Array();
    $("input[name$='mail']").each(function (index) {
        if (!G.isEmpty($(this).val())) {
            contactMails.push($(this).val());
        }
    });
    for (var mailIndex = 0; mailIndex < contactMails.length; mailIndex++) {
        if (!G.isEmpty(contactMails[mailIndex]) && !APP_BCGOGO.Validator.stringIsEmail(contactMails[mailIndex])) {
            nsDialog.jAlert("Email格式错误，请确认后重新输入！");
            return false;
        }
    }

    var contactQQs = new Array();
    $("input[name$='qq']").each(function (index, qq) {
        contactQQs.push($(this).val());
    });
    for (var qqIndex = 0; qqIndex < contactQQs.length; qqIndex++) {
        if (!G.isEmpty(contactQQs[qqIndex]) && !APP_BCGOGO.Validator.stringIsQq(contactQQs[qqIndex])) {
            nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
            return false;
        }
    }

    //validate name  @see $("#name").blur();
    if (APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact) {

        // 校验新输入的手机是否重复
        if (isMobileDuplicate(contactMobiles)) {
            return false;
        }

        // 校验用户名是否重复
        if (!G.isEmpty($.trim(supplierName)) && G.isEmpty($.trim($("#phone").val())) && isMobilesEmpty(contactMobiles)) {
            if (!checkSupplierByName(supplierName)) {
                return;
            }
            ;
        }

        // 如果用户名存在
        // 有座机校验座机是否重复
        // 有手机校验手机是否重复
        if (!G.isEmpty(supplierName)) {
            if (!G.isEmpty(G.trim(phone))) {
                if (!checkSupplierByPhone(phone)) {
                    return;
                }
                ;
            }
            if (!G.isEmpty(G.trim(phoneSecond))) {
                if (!checkSupplierByPhone(phoneSecond)) {
                    return;
                }
                ;
            }
            if (!G.isEmpty(G.trim(phoneThird))) {
                if (!checkSupplierByPhone(phoneThird)) {
                    return;
                }
                ;
            }
            if (!isMobilesEmpty(contactMobiles)) {
                if(!validateSupplierMobiles(contactMobiles,$("#supplierId").val())){
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
            if (!checkSupplierByName(supplierName)) {
                return;
            }
            ;
        }
    }

    clearDefaultAddress();

    // 由本身的页面提交的为主联系人
    if (!$(this).attr("lock")) {
        $(this).attr("lock", true);
        $("#supplierForm").ajaxSubmit(function (jsonObj) {
            $("#mask", parent.document).css("display", "none");
            $("#iframe_PopupBox", parent.document).css("display", "none");
            if (jsonObj && !G.isEmpty(jsonObj.idStr)) {
                // 回填供应商信息到父页面
                if($("#supplierId", window.parent.document)[0]) {
                    $("#supplierId", window.parent.document).val(jsonObj.idStr); // 确保父页面提交订单是更新供应商...
                }
                if($("#customerOrSupplierId", window.parent.document)[0]) {
                    $("#customerOrSupplierId", window.parent.document).val(jsonObj.idStr); // 确保父页面提交订单是更新供应商...
                }
                fillValuesToParent();
                $("#mobile", window.parent.document).val(jsonObj.mobile);
                $("#contact", window.parent.document).val(jsonObj.contact);
                $("#supplier", window.parent.document).val(jsonObj.name);
                $("#contactId", window.parent.document).val(jsonObj.contactIdStr);
                //window.parent.location.reload(); 不需要reload
            }
        });
    }
}

function fillValuesToParent() {
    $("#abbr", window.parent.document).val($("#abbr").val());
    $("#fax", window.parent.document).val($("#fax").val());
    $("#input_address", window.parent.document).val($("#input_address").val());
    $("#select_province_input", window.parent.document).val($("#select_province").val());
    $("#select_city_input", window.parent.document).val($("#select_city").val());
    $("#select_township_input", window.parent.document).val($("#select_township").val());
    $("#landLine", window.parent.document).val($("#landLine").val());
    $("#landLineSecond", window.parent.document).val($("#landLineSecond").val());
    $("#landLineThird", window.parent.document).val($("#landLineThird").val());
    $("#bank", window.parent.document).val($("#bank").val());
    $("#accountName", window.parent.document).val($("#accountName").val());
    $("#account", window.parent.document).val($("#account").val());
    $("#settlementType", window.parent.document).val($("#settlementType").val());
    $("#invoiceCategory", window.parent.document).val($("#invoiceCategory").val());
}

function clearDefaultAddress() {
    if ($("#input_address").val() == $("#input_address").attr("initValue") && $("#input_address").val() == "详细地址") {
        $("#input_address").val('');
    }
}

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
        // supplierId存在的情况下 如果相等 通过
        var supplierId = jsonStr.supplierDTO.idString;
        if (!G.isEmpty($("#supplierId").val()) && $("#supplierId").val() != "0") {
            if (supplierId == $("#supplierId").val()) {
                return true;
            }
        }
        hrefHandler(supplierId, "name", supplierName);
        return false;
    } else {
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
        // supplierId存在的情况下 如果相等 通过
        if (!G.isEmpty($("#supplierId").val()) && $("#supplierId").val() != "0") {
            if (supplierId == $("#supplierId").val()) {
                return true;
            }
        }
        hrefHandler(supplierId, "phone", phone)
        return false;
    } else {
        return true;
    }
}

function checkSupplierByMobile(mobile) {
    var jsonStr = APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=getSupplierJsonDataByMobile",
        data: {
            mobile: mobile
        },
        cache: false,
        dataType: "json"
    });
    if (!G.isEmpty(jsonStr) && !G.isEmpty(jsonStr.idStr)) {
        var supplierId = jsonStr.idStr;
        // supplierId存在的情况下 如果相等 通过
        if (!G.isEmpty($("#supplierId").val()) && $("#supplierId").val() != "0") {
            if (supplierId == $("#supplierId").val()) {
                return true;
            }
        }
        // supplierId不存在的情况下 直接不通过
        hrefHandler(supplierId, "mobile", mobile);
        return false;
    } else {
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
    var $mainContact = $(this).closest("tr").siblings().find('.icon_connacter');
    $mainContact.removeClass('icon_connacter').addClass('icon_grayconnacter').addClass('hover'); // 主联系人灰化

    $(this).removeClass('icon_grayconnacter').removeClass('hover').addClass('icon_connacter'); // 当前联系人转变为主联系人

    var $alert = $(this).siblings(".alert"); // 保存alert div
    $(this).siblings(".alert").remove();

    $(this).parent().find("input[id$='mainContact']").val("1"); // 设置为主联系人1

    var currentLevel = $(this).parent().find("input[id$='level']").val();
    $(this).parent().find("input[id$='level']").val("0");

    $(this).unbind("click");

    $alert.insertAfter($mainContact).hide(); // 添加alert

    $mainContact.parent().find("input[id$='mainContact']").val("0"); // 修改非主联系人
    $mainContact.parent().find("input[id$='level']").val(currentLevel);
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

// 判断当前的联系人手机号信息是否为空
function isMobilesEmpty(contactMobiles) {
    if (G.isEmpty(contactMobiles)) {
        return true;
    }
    if (contactMobiles.constructor == Array) {
        return contactMobiles.every(function (item, index, obj) {
            return G.isEmpty(item);
        });
    }
    return true;
}


$(document).ready(function () {
    <!-- 绑定省市区信息 -->
    $("#select_province").val(${supplierDTO.province});
    $("#select_province").change();
    $("#select_city").val(${supplierDTO.city});
    $("#select_city").change();
    $("#select_township").val(${supplierDTO.region});

    // flag判断该供应商是否在数据库已经存在
    var flag = false;
    var ms = 0;

    $('#div_close,#cancelBtn').bind('click', function () {
        $('#mask', parent.document).css('display', 'none');
        $('#iframe_PopupBox', parent.document).css('display', 'none');
        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch (e) {
            ;
        }
    });

    $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").keyup(function () {
        var inValue = $(this).val()
        var outValue = inValue.replace(/[^\d]+/g, "");

        if(inValue !== outValue) {
            $(this).val(outValue);
        }
    });

    $("#account").keyup(function () {
        var inValue = $(this).val();
        var outValue = inValue.replace(/[^\d]+/g, "");

        if(inValue !== outValue) {
            $(this).val(outValue);
        }
    });

    $("#landLine,#landLineSecond,#landLineThird,#fax").keyup(function () {
        var inValue = $(this).val();
        var outValue = inValue.replace(/[^\d\-]+/g, "").replace(/\-+/g, "-");

        if(inValue !== outValue) {
            $(this).val(outValue);
        }

        if ($(this).val().charAt(0) == '-') {
            $(this).val("");
        }
    });

    $("#table_vehicle input[id$='.year']").keyup(function () {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });

    //IE或者别的浏览器
    if (/msie/i.test(navigator.userAgent)) {
        // TODO IE下的待定
    } else {
        $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").live("change", checkNumberInput);
        if(document.getElementById("landLine")) {
            document.getElementById("landLine").addEventListener("input", checkTelInput, false);
        }
        if(document.getElementById("landLineSecond")) {
            document.getElementById("landLineSecond").addEventListener("input", checkTelInput, false);
        }
        if(document.getElementById("landLineThird")) {
            document.getElementById("landLineThird").addEventListener("input", checkTelInput, false);
        }
        if(document.getElementById("account")) {
            document.getElementById("account").addEventListener("input", checkNumberInput, false);
        }
        if(document.getElementById("fax")) {
            document.getElementById("fax").addEventListener("input", checkTelInput, false);
        }

    }

    $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").keyup(function () {
        var inValue = $(this).val();
        var outValue = inValue.replace(/[^\d]+/g, "");

        if(inValue !== outValue) {
            $(this).val(outValue);
        }
    });

    $('#account').keyup(function () {
        var inValue = $(this).val();
        var outValue = inValue.replace(/[^\d]+/g, "");

        if(inValue !== outValue) {
            $(this).val(outValue);
        }
    });

    function checkNumberInput() {
        var inValue = $(this).val();
        var outValue = inValue.replace(/[^\d]+/g, "");

        if(inValue !== outValue) {
            $(this).val(outValue);
        }
    }

    function checkTelInput() {
        var inValue = $(this).val();
        var outValue = inValue.replace(/[^\d\-]+/g, "").replace(/\-+/g, "-");

        if(inValue !== outValue) {
            $(this).val(outValue);
        }

        if ($(this).val().charAt(0) == '-') {
            $(this).val("");
        }
    }

    $("#fax").blur(function () {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($(this).val()) && $(this).val() != "") {
            nsDialog.jAlert("传真号格式错误，请确认后重新输入！");
        }
    });

    $("#supplier").blur(function () {
        ms = new Date().getTime();
        var supplierName = $("#supplier").val();
        if (!G.isEmpty(G.trim(supplierName))) {
            checkSupplierByName(supplierName);
        }
    });

    //表单提交，获取json返回值，给supplierId赋值
    $("#confirmBtn1").live("click", function () {
        checkFormData(flag, ms);
    });

    // 绑定 联系人列表 删除事件
    $('.close').live("click", delContact);
    // 绑定 联系人列表 点击成为主联系人事件
    $('.icon_grayconnacter').live("click", switchContact);
    //在线店铺设置所属区域的值
    setInputValue();

    $(".J_QQ").each(function (index, value) {
        $(this).multiQQInvoker({
            QQ: $(this).attr("data_qq")
        });
    });
});

<!-- add by zhuj -->
$(function () {
    $(".tabRecord tr").not(".tabTitle").css({"border": "1px solid #bbbbbb", "border-width": "1px 0px"});
    $(".tabRecord tr:nth-child(odd)").not(".tabTitle").css("background", "#eaeaea");
    $(".tabRecord tr").not(".tabTitle").hover(
            function () {
                $(this).find("td").css({"background": "#fceba9", "border": "1px solid #ff4800", "border-width": "1px 0px"});

                $(this).css("cursor", "pointer");
            },
            function () {
                $(this).find("td").css({"background-Color": "#FFFFFF", "border": "1px solid #bbbbbb", "border-width": "1px 0px 0px 0px"});
                $(".tabRecord tr:nth-child(odd)").not(".tabTitle").find("td").css("background", "#eaeaea");
            }
    );
    $(".close").hide();
    $(".table_inputContact").not("tr:first").hover(
            function () {
                $(".close").show();
            },
            function () {
                $(".close").hide();
            }
    );
    $(".alert").hide();
    $(".hover").live("hover", function (event) {
        var _currentTarget = $(event.target).parent().find(".alert");
        _currentTarget.show();
        //因为有2px的空隙,所以绑定在parent上.
        _currentTarget.parent().mouseleave(function (event) {
            event.stopImmediatePropagation();

            if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
                _currentTarget.hide();
            }
        });
    }, function (event) {
        var _currentTarget = $(event.target).parent().find(".alert");
        if ($(event.relatedTarget).find(".alert")[0] != _currentTarget[0]) {
            $(event.target).parent().find(".alert").hide();
        }
    });
    var allLevelZero = true;
    $("#contactInputTable input[id$='level']").each(function () {
        if (G.isEmpty($(this).val()) && $(this).val() != '0') {
            allLevelZero = false;
            return;
        }
    });
    if (allLevelZero) {
        $("#contactInputTable input[id$='level']").each(function (i) {
            $(this).val(i);
        });
    }
    var noMainContact = true;
    $("#contactInputTable input[id$='mainContact']").each(function () {
        if (G.isEmpty($(this).val()) && $(this).val() != '1') {
            noMainContact = false;
            return;
        }
    });
    if (noMainContact) {
        $("#contactInputTable input[id$='mainContact']").each(function (i) {
            if (i == 0) {
                $(this).val(1);
                $(this).parent("td").find(".icon_grayconnacter").click();
            }
        });
    }
});


<!-- add end -->
</script>
</head>
<body style='overflow: hidden;color:#000000;'>

<form name="supplierForm" id="supplierForm" action="txn.do?method=updateSupplier" method="post">
<input type="hidden" name="supplierId" id="supplierId" value="${supplierDTO.idStr}"/>

<div class="alertMain newCustomers" id="div_show">
<div class="alert_title">
            <div class="left"></div>
            <div class="body">供应商详细信息</div>
            <div class="right"></div>
</div>
<div class="height"></div>
<div class="left_customer">
            <label class="lblTitle">基本信息</label>
            <c:if test="${supplierDTO.isOnlineShop}" var="onlineShop">
                <div class="divTit">
                    <span class="name name_left">供应商名称</span>
                    <input id="supplier" name="supplier"  value="${supplierDTO.name}" readonly="true" class="onlineShop" style="width:248px;"/>
                    <span style="padding-left:75px;">
                    <a class="icon_online_shop" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${supplierDTO.supplierShopId}"></a>
                    <a class="J_QQ" data_qq="${supplierDTO.qqArray}"></a>
                    </span>
                </div>
                <div class="divTit">
                    <span class="name name_left">地址</span>
                    <select id="select_province" name="province"  style="display: none;">
                        <option value="">所有省</option>
                    </select>
                    <select id="select_city" name="city"  style="display: none;">
                        <option value="">所有市</option>
                    </select>
                    <select id="select_township" name="region"  style="display: none;">
                        <option value="">所有区</option>
                    </select>
                    <input id="areaInfo"  readonly="true" class="onlineShop" style="width:248px;"/>
                </div>
                <div class="divTit" style="margin-left:75px;display:none;" id="addressDiv">
                    <input  id="input_address" name="address"  class="onlineShop" value="${supplierDTO.address}"
                           readonly="true" style="width:248px;" />
                </div>
                <div class="divTit">
                    <span class="name name_left">简称</span>
                    <input  id="abbr" name="abbr" class="onlineShop" value="${supplierDTO.abbr}" readonly="true" style="width:98px;"/>

                </div>
                <div class="divTit">
                    <span class="name name_left" style="width:40px;">座机</span>
                    <input id="landLine" maxlength="14"  name="landLine" autocomplete="off" class="txt" style="width:98px;"
                           value="${supplierDTO.landLine}"/>
                </div>
                <div class="divTit">
                    <span class="name name_left">传真</span>
                    <input id="fax" name="fax" maxlength="20" class="txt" style="width:98px;" value="${supplierDTO.fax}"/>
                </div>
            </c:if>
            <c:if test="${!onlineShop}" >
                <div class="divTit">
                    <span class="name name_left"><span class="red_color">*</span>供应商名称</span>
                    <input name="supplier" id="supplier" class="txt" maxlength="20" autocomplete="off" style="width:248px;"
                           value="${supplierDTO.name}"/>
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
                    <input id="input_address" name="address" type="text" class="txt" value="${supplierDTO.address}"
                           initValue="详细地址" style="width:248px;"/>
                </div>

                <div class="divTit">
                    <span class="name name_left">座机</span>
                    <input id="landLine" maxlength="14" name="landLine" autocomplete="off" class="txt" style="width:76px;"
                           value="${supplierDTO.landLine}"/>
                    <input id="landLineSecond" maxlength="14" name="landLineSecond" autocomplete="off" class="txt" style="width:76px;"
                           value="${supplierDTO.landLineSecond}"/>
                    <input id="landLineThird" maxlength="14" name="landLineThird" autocomplete="off" class="txt" style="width:75px;"
                           value="${supplierDTO.landLineThird}"/>
                </div>
                <div class="divTit">
                    <span class="name name_left">传真</span>
                    <input id="fax" name="fax" maxlength="20" class="txt" style="width:98px;" value="${supplierDTO.fax}"/>
                </div>

                <div class="divTit">
                    <span class="name name_left" style="width:40px;">简称</span>
                    <input id="abbr" name="abbr" class="txt" maxlength="20" autocomplete="off" style="width:98px;"
                           value="${supplierDTO.abbr}"/>
                </div>
            </c:if>

</div>
<!-- add by zhuj -->
<div class="right_customer go_customer">
            <label class="lblTitle">联系人信息<span class="icon_connacter">为主联系人</span></label>
            <table cellpadding="0" cellspacing="0" class="table_contact table_inputContact tb_go"
                   id="contactInputTable">
                <col width="50">
                <col width="80">
                <col>
                <col width="90">
                <col width="50">
                <tr class="title_top">
                    <td>姓名</td>
                    <td>手机</td>
                    <td>Email</td>
                    <td>QQ</td>
                    <td></td>
                </tr>
                <c:forEach items="${supplierDTO.contacts}" var="contact" varStatus="status">
                    <tr class="single_contact">
                        <input type="hidden" name="contacts[${status.index}].id" id="contacts[${status.index}].id"
                               value="${contact.idStr}"/>
                        <td><input type="text" class="txt" name="contacts[${status.index}].name"
                                   id="contacts[${status.index}].name" value="${contact.name}" style="width:50px;"/></td>
                        <td><input type="text" class="txt" name="contacts[${status.index}].mobile"
                                   id="contacts[${status.index}].mobile" value="${contact.mobile}" style="width:78px;"
                                   maxlength="11"/></td>
                        <td><input type="text" class="txt" name="contacts[${status.index}].email"
                                   id="contacts[${status.index}].email" value="${contact.email}" style="width:115px;"/>
                        </td>
                        <td><input type="text" class="txt" name="contacts[${status.index}].qq"
                                   id="contacts[${status.index}].qq" value="${contact.qq}" style="width:70px;"/></td>
                        <td><input type="hidden" name="contacts[${status.index}].level"
                                   id="contacts[${status.index}].level" value="${status.index}"/>
                            <c:choose>
                                <c:when test="${contact.mainContact == 1}">
                                    <a class="icon_connacter"></a>
                                    <input type="hidden" name="contacts[${status.index}].mainContact"
                                           id="contacts[${status.index}].mainContact" value="1"/>
                                </c:when>
                                <c:otherwise>
                                    <a class="icon_grayconnacter hover"></a>
                                    <input type="hidden" name="contacts[${status.index}].mainContact"
                                           id="contacts[${status.index}].mainContact" value="0"/>

                                    <div class="alert" style="display:none;">
                                        <span class="arrowTop"></span>

                                        <div class="alertAll">
                                            <div class="alertLeft"></div>
                                            <div class="alertBody">点击设为主联系人</div>
                                            <div class="alertRight"></div>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <a class="close"></a>
                        </td>
                    </tr>
                </c:forEach>
            </table>
</div>


<div class="height"></div>
<label class="lblTitle">账户信息</label>

<div class="divTit" style="width: 230px;">
            <span class="name">开户行</span>&nbsp;<input id="bank" maxlength="20" name="bank"
                                                      class="txt" style="width:160px;" value="${supplierDTO.bank}"/>
</div>
<div class="divTit" style="width: 230px;">
            <span class="name">开户名</span>&nbsp;
            <input id="accountName" name="accountName" maxlength="20" class="txt" style="width:150px;"
                   value="${supplierDTO.accountName}"/>
</div>
<div class="divTit" style="width: 230px;">
            <span class="name">账号</span>&nbsp;
            <input id="account" name="account" maxlength="20" class="txt" style="width:150px;"
                   value="${supplierDTO.account}"/>
</div>
<div class="divTit" style="width: 230px;">
            <span class="name">结算方式</span>&nbsp;
            <select id="settlementType" class="txt select" name="settlementType"
                    style="color:#7e7e7e;width: 120px; margin-left: -6px;">
                <option value="">--请选择--</option>
                <c:forEach items="${settlementTypeList}" var="item">
                    <option value="${item.key}">${item.value}</option>
                </c:forEach>
            </select>
</div>
<div class="divTit" style="width: 230px;">
            <span class="name">发票类型</span>
            <select id="invoiceCategory" class="txt select" name="invoiceCategory" style="color:#7e7e7e;width: 120px;">
                <option value="">--请选择--</option>
                <c:forEach items="${invoiceCategoryList}" var="item">
                    <option value="${item.key}">${item.value}</option>
                </c:forEach>
            </select>
</div>
<div class="divTit" style="width:100%;">
            <span class="name" style="vertical-align:top;">备注</span>&nbsp;
            <textarea rows="2" cols="40" name="memo" id="memo" maxlength="400" class="txt textarea">${supplierDTO.memo}</textarea>
</div>
<div class="clear"></div>
<div class="button" style="width:100%">
            <a class="btnSure" id="confirmBtn1">确 定</a>
            <a class="btnSure" id="cancelBtn">取 消</a>
</div>
<div class="clear"></div>
</div>
</form>

</body>
</html>
