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


<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/todo_new<%=ConfigController.getBuildVersion()%>.css"/>
<link type="text/css" rel="stylesheet" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>



<%@include file="/WEB-INF/views/header_script.jsp" %>

<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/clientInfo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/moreUserInfo<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/clientVehicle<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/vehicleValidator<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/addNewCustomer/addNewCustomer<%=ConfigController.getBuildVersion()%>.js"></script>

<script type="text/javascript">

defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.CUSTOMER_MANAGE.ADD_NEW_CUSTOMER");

<bcgogo:permissionParam permissions="WEB.VERSION.HAS_CUSTOMER_CONTACTS,WEB.VERSION.HAS_SUPPLIER_CONTACTS">
APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact =${WEB_VERSION_HAS_CUSTOMER_CONTACTS}; // 客户多联系人
APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact =${WEB_VERSION_HAS_SUPPLIER_CONTACTS}; // 供应商多联系人
</bcgogo:permissionParam>
APP_BCGOGO.Permission.Version.WholesalerVersion =${empty wholesalerVersion?false:wholesalerVersion};
APP_BCGOGO.Permission.Version.FourSShopVersion =${empty fourSShopVersions?false:fourSShopVersions};
$(document).ready(function() {


    $("input[name='year'],input[name='qq'],input[name='maintainMileagePeriod'],input[name='maintainMileagePeriod'], input[name='obdMileage']").live("keyup",function () {
        if (this.value.length == 1) {
            this.value = this.value.replace(/[^1-9]/g, '')
        } else {
            this.value = this.value.replace(/\D/g, '')
        }
    }).live("blur", function () {
                $(this).click();
            });

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
//
//    $("#table_vehicle input[id$='.year']").keyup(function() {
//        $(this).val($(this).val().replace(/[^\d]+/g, ""));
//    });

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

    $('#account').keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });


    $("#fax").blur(function() {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($(this).val()) && $(this).val() != "") {
            nsDialog.jAlert("传真号格式错误，请确认后重新输入！");
        }
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

    $("[id$='gsmObdImei'],[id$='gsmObdImeiMoblie']").live('focus',function () {
        searchOBDInfo(this, $(this).val());
    }).live("input",function(){
                searchOBDInfo(this, $(this).val());
            });

});

function searchOBDInfo(dom, searchWord){
  var target_id=$(dom).attr("id");
    if(G.isEmpty(target_id)) return;
    var iprefix=target_id.split(".")[0]
    target_id=target_id.split(".")[1];
  var droplist = APP_BCGOGO.Module.droplist;
  searchWord = searchWord.replace(/\s/g, '');
  var uuid = GLOBAL.Util.generateUUID();
  droplist.setUUID(uuid);
  if(G.isEmpty(searchWord)){
    return;
  }
  var data={now: new Date().getTime()};
  if(target_id=="gsmObdImeiMoblie"){
    data['mobile']=searchWord;
  }else{
    data['imei']=searchWord;
  }
  $.post('OBDManager.do?method=getShopOBDSuggestion', data,function (list) {
    droplist.show({
      "selector": $(dom),
      "data": {
        uuid: uuid,
        data:$.map(list, function (n) {
          if(target_id=="gsmObdImeiMoblie"){
              n.label = n.mobile;
          }else{
              n.label = n.imei;
          }
            return n;
        })
      },
        "onSelect": function (event, index, data, hook) {
            if(target_id=="gsmObdImeiMoblie"){
                $(hook).val(data.mobile);
                $("#"+iprefix+"\\."+"gsmObdImei").val(data.imei);
            }else{
                $(hook).val(data.imei);
                $("#"+iprefix+"\\."+"gsmObdImeiMoblie").val(data.mobile);
            }

            droplist.hide();
        }
    });
  }, 'json');
}


function continueAddNewCustomer(){
    window.location.href = "customer.do?method=addClient";
}
function customerList(){
    window.location.href = "customer.do?method=customerdata";
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


function checkFormData(flag, ms) {


    var customerName = $("#name").val();
    if (G.isEmpty(customerName) || G.isEmpty($.trim(customerName))) {
        nsDialog.jAlert("用户名必须填写!");
        return false;
    }

// 此处客户名应该可以重复的，暂时先拿掉以下代码。by qxy 2014-09-12
//    if (!G.isEmpty($.trim(customerName))) {
//        var result = checkCustomerName(customerName);
//        if(!result){
//            return;
//        }
//    }

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


    if (checkVehicleNo()) {
        return;
    }
    if (checkAllDuplicateVehicles()) {
        nsDialog.jAlert("车牌号有重复，请修改或删除。");
        return false;
    }

    infoSubmit();

}

function infoSubmit() {

    clearDefaultAddress();
    if (!$("#confirmBtn1").attr("lock")) {
        $("#confirmBtn1").attr("lock", true);
        try {
            $("#thisform").ajaxSubmit(function (result) {
                if (result && result.success) {
                    $("#addSuccessDiv").dialog({
                        width:330,
                        modal:true,
                        title:"友情提示",
                        close: function () {
                            if(G.isNotEmpty(result.data)){
                                window.location.href = 'unitlink.do?method=customer&customerId=' + result.data;
                            }else{
                                window.location.href = "customer.do?method=customerdata";
                            }
                        }
                    });

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

function clearDefaultAddress() {
    if ($("#input_address").val() == $("#input_address").attr("initValue") && $("#input_address").val() == "详细地址") {
        $("#input_address").val('');
    }
    if ($("#input_address2").val() == $("#input_address2").attr("initValue") && $("#input_address2").val() == "详细地址") {
        $("#input_address2").val('');
    }
}


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
</head>

<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>


<input type="hidden" id="orderType" value="addClientInfo">
<input type="hidden" id="secondCategoryName" value="">


<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">新增客户</div>
    </div>
    <div class="clear"></div>

    <form:form name="thisform" id="thisform" commandName="customerRecordDTO" action="customer.do?method=addcustomer"
               method="post">
    <form:hidden path="shopId" value="${customerRecordDTO.shopId}"/>
    <form:hidden path="thirdCategoryIdStr" value=""/>
    <form:hidden path="id" value="${customerRecordDTO.id}"/>
    <form:hidden path="customerId" value="${customerRecordDTO.customerId}"/>
    <input type="hidden" id="pageRows" value="10"/>
    <input type="hidden" id="rowStart" value="1"/>
    <input type="hidden" name="pageTip" id="pageTip" value="<%=(request.getAttribute("fromPage"))%>"/>
    <input type="hidden" name="identity" id="identity"/>
    <input type="hidden" name="supplierId" id="supplierId2"/>
    <input type="hidden" name="businessScope" id="businessScope"/>

    <div id="todo_content">
        <div>
            <div class="lblTitle">基本信息</div>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table_search">
                <colgroup>
                    <col  width="100"/>
                    <col />
                    <col  width="100"/>
                    <col />
                    <col  width="100"/>
                    <col />
                </colgroup>
                <tr>
                    <th><span class="red_color">*</span>客户名称： </th>
                    <td><form:input path="name"
                                    value="${customerRecordDTO.name}"
                                    class="txt" maxlength="20"
                                    autocomplete="off"
                                    style="width:170px;"/>
                    </td>
                    <th>手机：</th>
                    <td>
                        <form:input path="mobile" value="${customerRecordDTO.mobile}"
                                    id="mobile" maxlength="11" autocomplete="off"
                                    class="txt" style="width:170px;"/>
                    </td>
                    <th>联系人：</th>
                    <td><form:input path="contact" value="${customerRecordDTO.contact}"
                                    id="contact" class="txt" maxlength="20"
                                    autocomplete="off" style="width:170px;"/></td>
                </tr>
                <tr>
                    <th>地址： </th>
                    <td><input id="input_address2" name="address" type="text" class="txt" initValue="详细地址"  placeHolder="详细地址"
                               style="width:170px;"/></td>
                    <th>Email：</th>
                    <td><form:input path="email" autocomplete="off" value="${customerRecordDTO.email}"
                                    id="email" maxlength="50" class="txt" style="width:170px;"/></td>
                    <th>QQ：</th>
                    <td><form:input path="qq" autocomplete="off" value="${customerRecordDTO.qq}" id="qq"
                                    maxlength="10" class="txt" style="width:170px;"/></td>
                </tr>
                <tr>
                    <th>传真： </th>
                    <td><form:input path="fax" autocomplete="off" value="${customerRecordDTO.fax}" maxlength="20"
                                    class="txt" style="width:170px;"/></td>
                    <th>生日：</th>
                    <td><form:input
                            path="birthdayString" value="${customerRecordDTO.birthdayString}"
                            class="txt" readonly="true" style="width:170px;"/></td>
                    <th>简称：</th>
                    <td><form:input path="shortName" value="${customerRecordDTO.shortName}"
                                    class="txt"
                                    maxlength="10" autocomplete="off" style="width:170px;"/></td>
                </tr>
                <tr>
                    <th>客户类别： </th>
                    <td>
                        <form:select path="customerKind" cssStyle="width:175px;" class="txt">
                            <form:options items="${customerTypeMap}"/>
                        </form:select>
                    </td>
                    <th>座机：</th>
                    <td colspan="3">
                        <form:input
                                path="phone" value="${customerRecordDTO.phone}" maxlength="14"
                                autocomplete="off" class="txt" style="width:165px;"/>
                        <form:input
                                path="phoneSecond" value="${customerRecordDTO.phoneSecond}" maxlength="14"
                                autocomplete="off" class="txt" style="width:165px;"/>
                        <form:input
                                path="phoneThird" value="${customerRecordDTO.phoneThird}" maxlength="14"
                                autocomplete="off" class="txt" style="width:165px;"/>
                    </td>
                </tr>
            </table>
            <div class="clear height"></div>
            <div class="lblTitle">账户信息</div>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table_search">
                <colgroup>
                    <col  width="100"/>
                    <col />
                    <col  width="100"/>
                    <col />
                    <col  width="100"/>
                    <col />
                </colgroup>
                <tr>
                    <th>开户行： </th>
                    <td><form:input path="bank" value="${customerRecordDTO.bank}" autocomplete="off" maxlength="20"
                                    class="txt" cssStyle="width:170px;"/></td>
                    <th>开户名：</th>
                    <td><form:input path="bankAccountName" maxlength="20"
                                    value="${customerRecordDTO.bankAccountName}"
                                    class="txt" cssStyle="width:170px;"/></td>
                    <th>账号：</th>
                    <td><form:input path="account" maxlength="20" autocomplete="off" value="${customerRecordDTO.account}"
                                    class="txt" cssStyle="width:170px;"/></td>
                </tr>
                <tr>
                    <th>结算方式： </th>
                    <td>
                        <form:select path="settlementType" class="txt" cssStyle="width:175px;">
                            <form:option value="" label="--请选择--"/>
                            <form:options items="${settlementTypeMap}"/>
                        </form:select>
                    </td>
                    <th>发票类型：</th>
                    <td>
                        <form:select path="invoiceCategory" class="txt" cssStyle="width:175px;">
                            <form:option value="" label="--请选择--"/>
                            <form:options items="${invoiceCatagoryMap}"/>
                        </form:select>
                    </td>
                    <th>&nbsp;</th>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <th>备注：</th>
                    <td colspan="5">
                        <textarea name="memo" id="memo" autocomplete="off"
                                  maxlength="400" class="textarea_90"
                                  style="margin:5px 0;">${customerRecordDTO.memo}</textarea>
                    </td>
                </tr>
            </table>
            <div class="clear height"></div>
            <div class="lblTitle"><span class="title_add"><a href="#" id="J_addNewVehicle">添加车辆</a></span>车辆信息</div>

            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
            <div id="table_vehicle">

            <c:forEach items="${customerRecordDTO.vehicles}" var="vehicle" varStatus="status">
            <div id="customerVehicleInfo${status.index}" data-index="${status.index}">
            <div class="vehicle_information" data-index="${status.index}">
                <div class="title_close" id="customerVehicleClose${status.index}" data-index="${status.index}"></div>
                <table  width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><span class="red_color">*</span>车牌号</td>
                        <td>
                            <input class="txt validationDuplicate" maxlength="9" type="text" autocomplete="off"
                                   name="vehicles[${status.index}].licenceNo" id="vehicles${status.index}.licenceNo"
                                   value="${vehicle.licenceNo!=null?vehicle.licenceNo:''}"/>
                            <input type="hidden" name="vehicles[${status.index}].id" id="vehicles${status.index}.id"
                                   value="${vehicle.id!=null?vehicle.id:''}"/>
                        </td>
                        <td>车主</td>
                        <td>
                            <input class="txt" maxlength="20" type="text"
                                   name="vehicles[${status.index}].contact" id="vehicles${status.index}.vehicleContact"
                                   value="${vehicle.contact!=null?vehicle.contact:''}"/>
                        </td>
                        <td>车主手机</td>
                        <td>
                            <input class="txt" maxlength="11" type="text"
                                   name="vehicles[${status.index}].mobile" id="vehicles${status.index}.vehicleMobile"
                                   value="${vehicle.mobile!=null?vehicle.mobile:''}"/>
                        </td>
                        <td>购车日期</td>
                        <td>
                            <input readonly="true" class="txt datePicker" type="text"
                                   name="vehicles[${status.index}].dateString" id="vehicles${status.index}.dateString"
                                   value="${vehicle.dateString!=null?vehicle.dateString:''}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>车辆品牌</td>
                        <td>
                            <input class="txt" maxlength="8" type="text"
                                   name="vehicles[${status.index}].brand" id="vehicles${status.index}.vehicleBrand" pagetype="customerVehicle"
                                   value="${vehicle.brand!=null?vehicle.brand:''}"/>
                        </td>
                        <td>车型</td>
                        <td><input class="txt" maxlength="8" type="text"
                                   name="vehicles[${status.index}].model" id="vehicles${status.index}.vehicleModel" pagetype="customerVehicle"
                                   value="${vehicle.model!=null?vehicle.model:''}"/></td>
                        <td>车架号</td>
                        <td><input class="txt chassisNumber" maxlength="17" type="text"
                                   name="vehicles[${status.index}].chassisNumber" id="vehicles${status.index}.chassisNumber"
                                   value="${vehicle.chassisNumber!=null?vehicle.chassisNumber:''}"/></td>
                        <td>发动机号</td>
                        <td colspan="3"><input class="txt" maxlength="20" type="text" name="vehicles[${status.index}].engineNo"
                                               id="vehicles${status.index}.vehicleEngineNo"
                                               value="${vehicle.engineNo!=null?vehicle.engineNo:''}"/></td>
                    </tr>
                    <tr>
                        <td>年代</td>
                        <td><input class="txt" maxlength="4" type="text"
                                   name="vehicles[${status.index}].year" id="vehicles${status.index}.year"
                                   value="${vehicle.year!=null?vehicle.year:''}"/></td>
                        <td>排量</td>
                        <td><input class="txt" maxlength="12"  type="text"
                                   name="vehicles[${status.index}].engine" id="vehicles${status.index}.engine"
                                   value="${vehicle.engine!=null?vehicle.engine:''}"/></td>
                        <td>车身颜色</td>
                        <td><input class="txt" maxlength="8" type="text" name="vehicles[${status.index}].color"
                                   id="vehicles${status.index}.vehicleColor" value="${vehicle.color!=null?vehicle.color:''}"/></td>
                        <td>当前里程</td>
                        <td><input class="txt" maxlength="8" type="text" name="vehicles[${status.index}].obdMileage"
                                   id="vehicles${status.index}.obdMileage" value="${vehicle.obdMileage!=null?vehicle.obdMileage:''}"/>
                            公里</td>
                    </tr>
                    <tr>
                        <td>保养周期</td>
                        <td><input class="txt" maxlength="8" type="text" name="vehicles[${status.index}].maintainMileagePeriod"
                                   id="vehicles${status.index}.maintainMileagePeriod" value="${vehicle.maintainMileagePeriod!=null?vehicle.maintainMileagePeriod:''}"/>
                            公里</td>

                        <c:choose>
                            <c:when test="${fourSShopVersions}">
                                <td>IMIE号</td>
                                <td><input class="txt" maxlength="15" type="text"
                                           name="vehicles[${status.index}].gsmObdImei"
                                           id="vehicles${status.index}.gsmObdImei"
                                           value="${vehicle.gsmObdImei!=null?vehicle.gsmObdImei:''}"/></td>
                                <td>SIM号</td>
                                <td><input class="txt" maxlength="11" type="text"
                                           name="vehicles[${status.index}].gsmObdImeiMoblie"
                                           id="vehicles${status.index}.gsmObdImeiMoblie"
                                           value="${vehicle.gsmObdImeiMoblie!=null?vehicle.gsmObdImeiMoblie:''}"/></td>
                            </c:when>
                            <c:otherwise>
                                <td>&nbsp;</td>
                                <td>&nbsp;</td>
                                <td>&nbsp;</td>
                                <td>&nbsp;</td>
                            </c:otherwise>
                        </c:choose>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
                </table>
            </div>
            <div class="clear"></div>
                </div>
                </c:forEach>
                </div>
                  </bcgogo:hasPermission>


            <div class="search_div">
                <div class="search_btn" id="confirmBtn1">确 定</div>
                <div class="empty_btn" id="cancleBtn">清 空</div>
                <div class="clear"></div>
            </div>
        </div>
    </div>

    </form:form>

    <div class="clear i_height"></div>
</div>


<div id="addSuccessDiv" style="display:none">
    <%@include file="addCustomerSuccess.jsp" %>
</div>

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

<%@include file="/WEB-INF/views/footer_html.jsp" %>


</body>
</html>