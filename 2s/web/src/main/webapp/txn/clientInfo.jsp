<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);

%>
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

<style>
    #ui-datepicker-div, .ui-datepicker {
        font-size: 70%;
    }

    .table_input .chassisNumber {
        text-transform: uppercase;
    }

    .onlineShop {
        border: none;
        color: #7e7e7e;
        margin-top: 3px;
        width: 100px;
    }
    .icon_online_shop{
        background:url("images/icon_online_shop.png") repeat-x scroll 0 0 transparent;
        cursor: pointer;
        height: 15px;
        width: 15px;
        display: inline-block;
    }
</style>
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

        $("input[name$='.dateString']")
                .live("click", function () {
                    $(this).blur();
                })
        bindDateCmp($("input[name$='.dateString']"));
    });

    function bindDateCmp($obj){
        if(G.isEmpty($obj)) return;
        $obj.datepicker({
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
    }

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


    $("#confirmBtn").live("click", function() {
        //校验车主手机号
        var validateResult=true;
        $("#table_vehicle input[id$='vehicleMobile']").each(function() {
            var mobile=$(this).val();
            if (!G.isEmpty(G.trim(mobile))){
                if (!APP_BCGOGO.Validator.stringIsMobile(mobile)) {
                    nsDialog.jAlert("车主手机号码校验错误，请确认后重新输入！");
                    validateResult=false;
                    return false;
                }
            }
            var vehicleId=$(this).closest('tr').find("input[id$='.vehicleId']").val();
            var gsmObdImei= $(this).closest('tr').find("input[id$='.gsmObdImei']").val();
            if(!G.isEmpty(vehicleId)&&!G.isEmpty(gsmObdImei)){
                APP_BCGOGO.Net.syncGet({
                    url: "customer.do?method=checkIsExistGsmObdImeiInVehicle",
                    data: {
                        gsmObdImei: gsmObdImei,
                        vehicleId: vehicleId
                    },
                    dataType: "json",
                    success: function (json) {
                        if(!json.success){
                            validateResult=false;
                            return false;
                        }
                    }
                });
            }
        });
        if(!validateResult){
            return false;
        }
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

    $(".imei_input,.sim_no_input").live('focus',function () {
        searchOBDInfo(this, $(this).val());
    }).live("input",function(){
                searchOBDInfo(this, $(this).val());
            });

    $("#addVehicleBtn").click(function(){
        var index=0;
        if($(".vehicle_information:last")){
            index=$(".vehicle_information:last").attr("index");
            index++
        }
        var $vehicleSample=getVehicleInfoSample(index);
        $(".vehicle_info").append($vehicleSample);
        bindDateCmp($("input[name$='.dateString']"));
        adjustIframeStyle();
    });
    //删除整行信息的超链
    $(".delete_vehicle_btn").live("click", function (event) {
        event.preventDefault();
        var vehicleId =$(this).closest(".vehicle_information").find("input[id$='.id']").val();
        if(G.isEmpty(vehicleId)){
            $(this).closest(".vehicle_information").remove();
            return false;
        }
        var licenceNo =$(this).closest(".vehicle_information").find("input[id$='.licenceNo']").val();
        var customerId = $("#customerId").val();
        var data = APP_BCGOGO.Net.syncGet({
            url: "txn.do?method=checkUndoneOrder",
            data: {
                licenceNo: licenceNo,
                vehicleId: vehicleId,
                customerId: customerId
            },
            dataType: "json"
        });
        var _$me=$(this);
        if (data.success) {
            nsDialog.jConfirm("是否确认要删除当前客户车辆？", null, function (flag) {
                if (flag) {
                    var result = APP_BCGOGO.Net.syncGet({
                        url: "customer.do?method=deleteCustomerVehicleById",
                        data: {
                            vehicleId: vehicleId,
                            customerId: customerId
                        },
                        dataType: "json",
                        success:function(result){
                            if (result.success) {
                                _$me.closest(".vehicle_information").remove();
                                adjustIframeStyle();
                            } else {
                                nsDialog.jAlert(result.msg);
                            }
                        }
                    });
                }
            });
        } else {
            nsDialog.jAlert(data.msg);
        }
    });

    var orderType= $("#orderType",window.parent.document).val();
    if(orderType=="repairOrder"){
        $("#iframe_moreUserInfo",window.parent.document).height(1000+$(".vehicle_information").size()*100);
    }else{
        $("#iframe_PopupBox",window.parent.document).height(900+$(".vehicle_information").size()*100);
    }
    if(orderType=="OBD_MANAGER"){
        $("#vehicles0\\.gsmObdImei").val('${obd_imei}');
        $("#vehicles0\\.gsmObdImeiMoblie").val('${sim_no}');
    }
    setInputValue();
});

function searchOBDInfo(dom, searchWord){
    var is_sim_no_input=$(dom).hasClass("sim_no_input");
    var droplist = APP_BCGOGO.Module.droplist;
    searchWord = searchWord.replace(/\s/g, '');
    var uuid = GLOBAL.Util.generateUUID();
    droplist.setUUID(uuid);
    if(G.isEmpty(searchWord)){
        return;
    }
    var data={now: new Date().getTime()};
    if(is_sim_no_input){
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
                    if(is_sim_no_input){
                        n.label = n.mobile;
                    }else{
                        n.label = n.imei;
                    }
                    return n;
                })
            },
            "onSelect": function (event, index, data, hook) {
                if(is_sim_no_input){
                    $(hook).val(data.mobile);
                    $(dom).closest("tr").find(".imei_input").val(data.imei);
                }else{
                    $(hook).val(data.imei);
                    $(dom).closest("tr").find(".sim_no_input").val(data.mobile);
                }

                droplist.hide();
            }
        });
    }, 'json');
}


function getVehicleInfoSample(index){
    index=G.isEmpty(index)?0:index;
    var vehicleStr= '<div class="vehicle_information" index="'+index+'">'+
            '<div class="delete_vehicle_btn title_close"></div>'+
            '<table id="table_vehicle"  width="100%" border="0" cellspacing="0" cellpadding="0"> '+
            '<tr> '+
            '<td><span class="red_color">*</span>车牌号</td>'+
            '<td>'+
            '<input type="text" class="txt" style="width:85px;"  maxlength="9"'+
            ' name="vehicles['+index+'].licenceNo" id="vehicles'+index+'.licenceNo"/>'+
            '<input type="hidden" name="vehicles['+index+'].id" id="vehicles'+index+'.id"/>'+
            '<input type="hidden" name="vehicles['+index+'].startMileage" id="vehicles'+index+'.startMileage"/>'+
            '</td> '+
            '<td>车主</td> '+
            '<td>'+
            '<input type="text" class="txt" style="width:85px;"   maxlength="20" '+
            'name="vehicles['+index+'].contact" id="vehicles'+index+'.vehicleContact"/>'+
            '</td>'+
            '<td>车主手机</td> '+
            '<td>'+
            '<input type="text" class="txt" style="width:85px;" maxlength="11"  '+
            'name="vehicles['+index+'].mobile" id="vehicles'+index+'.vehicleMobile"/>  '+
            '</td> '+
            '<td>购车日期</td>   '+
            '<td><input readonly="true" type="text" name="vehicles['+index+'].dateString"'+
            'id="vehicles'+index+'.dateString" style="width:85px" class="txt"/></td> '+
            '</tr> '+
            '<tr> '+
            '<td>车辆品牌</td> '+
            '<td> '+
            '<input type="text" class="txt" style="width:85px;" maxlength="8"'+
            'name="vehicles['+index+'].brand" id="vehicles'+index+'.vehicleBrand" pagetype="customerVehicle"/> '+
            '</td> '+
            '<td>车型</td>'+
            '<td>'+
            '<input type="text" class="txt" style="width:85px;" maxlength="8"'+
            'name="vehicles['+index+'].model" id="vehicles'+index+'.vehicleModel" pagetype="customerVehicle"/> '+
            '</td>'+
            '<td>年代</td> '+
            '<td> '+
            '<input type="text" class="txt" style="width:85px;" maxlength="4" '+
            'name="vehicles['+index+'].year" id="vehicles'+index+'.year"/>'+
            '</td>'+
            '<td>排量</td> '+
            '<td>'+
            '<input type="text" class="txt" style="width:85px;" maxlength="12"'+
            'name="vehicles['+index+'].engine" id="vehicles'+index+'.engine"/> '+
            '</td> '+
            '</tr>'+
            '<tr> '+
            '<td>车身颜色</td> '+
            '<td> '+
            '<input type="text" class="txt" style="width:85px;"  maxlength="15"'+
            'name="vehicles['+index+'].color" id="vehicles'+index+'.vehicleColor"/> '+
            ' </td> '+
            ' <td>发动机号</td>'+
            ' <td>'+
            ' <input type="text" class="txt" style="width:85px;"'+
            ' name="vehicles['+index+'].engineNo" id="vehicles'+index+'.vehicleEngineNo"/> '+
            '</td> '+
            '<td>车架号</td>'+
            '<td colspan="3">'+
            '<input type="text" class="txt" style="width:248px;" maxlength="17" '+
            'name="vehicles['+index+'].chassisNumber" id="vehicles'+index+'.chassisNumber"/> '+
            '</td>'+
            '</tr>'+
            '<tr>'+
            '<td>当前里程</td>'+
            '<td><input type="text" class="txt" style="width:85px;" name="vehicles['+index+'].obdMileage" id="vehicles'+index+'.obdMileage" /></td>'+
            '<td>保养周期</td>'+
            '<td><input type="text" class="txt" style="width:85px;" name="vehicles['+index+'].maintainMileagePeriod" id="vehicles'+index+'.maintainMileagePeriod"/></td> ';
    if(APP_BCGOGO.Permission.Version.FourSShopVersion){
        vehicleStr+= '<td>OBD信息</td>'+
                '<td colspan="3"><input type="text" class="txt imei_input" style="width:130px;" placeHolder="IMIE号" name="vehicles['+index+'].gsmObdImei" id="vehicles'+index+'.gsmObdImei"/> '+
                '<input type="text" class="txt sim_no_input" style="width:108px;" placeHolder="SIM卡号" name="vehicles['+index+'].gsmObdImeiMoblie" id="vehicles'+index+'.gsmObdImeiMoblie"/></td>';
    }else{

    }
    vehicleStr+= '</tr>'+
            '</table> '+
            '</div>';
    return vehicleStr;
}

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
        if (parentPageType == "APPOINT_ORDER") {
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
        }else  if (parentPageType == "OBD_MANAGER") {
            $("#iframe_moreUserInfo",window.parent.document).hide();
            $("#mask",window.parent.document).hide();
            $("#searchOBDBtn",window.parent.document).click();
            return true;
        }else {
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
    $alert.hide();
    $mainContact.parent().next('input').val("0"); // 修改非主联系人
    $mainContact.parent().prev('input').val(currentLevel);
    $mainContact.live("click", switchContact);

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
    var orderType= $("#orderType",window.parent.document).val();
    if(orderType=="repairOrder"){
        $("#iframe_moreUserInfo",window.parent.document).height($(".alertMain").height()+100);
    }else{
        $("#iframe_PopupBox",window.parent.document).height($(".alertMain").height()+100);
    }
}

</script>
</head>

<body>
<%@ include file="/common/messagePrompt.jsp" %>
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
        <form:input path="name" type="text" class="txt" style="width:272px;"  maxlength="20" autocomplete="off" value="${customerRecordDTO.name}"/>
    </div>
    <div class="divTit">
        <span class="name name_left">地址</span>

    </div>
    <div class="divTit">
        <input id="input_address2" name="address" type="text" class="txt"  style="width:272px;"
               value="${customerRecordDTO.address}" placeHolder="详细地址"/>
    </div>
    <div class="divTit">
        <span class="name name_left">联系人</span>
        <form:input path="contact" maxlength="20" autocomplete="off" type="text" class="txt" style="width:111px;" value="${customerRecordDTO.contact}"/>
    </div>
    <div class="divTit">
        <span class="name name_left" style="width:40px;">手机</span>
        <form:input path="mobile" type="text" class="txt" style="width:111px;" value="${customerRecordDTO.mobile}" maxlength="11" autocomplete="off"/>
    </div>
    <div class="divTit">
        <span class="name name_left">Email</span>
        <form:input path="email" value="${customerRecordDTO.email}" type="text" class="txt" style="width:111px;"  maxlength="50"/>
    </div>
    <div class="divTit">
        <span class="name name_left" style="width:40px;">QQ</span>
        <form:input path="qq" maxlength="10" value="${customerRecordDTO.qq}" type="text" class="txt" style="width:111px;"/>
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
    <div class="divTit">
        <span class="name name_left">客户类别</span>
        <form:select path="customerKind" class="txt select" style="width:116px;">
            <form:options items="${customerTypeMap}"/>
        </form:select>
    </div>
    <div class="divTit">
        <span class="name name_left" style="width:40px;">简称</span>
        <form:input path="shortName" value="${customerRecordDTO.shortName}" type="text" class="txt" style="width:111px;" maxlength="10" autocomplete="off"/>
    </div>
</div>

<div class="height"></div>
<c:if test="<%=isMemberSwitchOn %>">
    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
        <div class="right_customer">
            <label class="lblTitle">会员信息
                <a id="changePassword" class="blue_color" style=" font-size:12px; font-weight:normal">修改密码</a>
            </label>
            <table cellpadding="0" cellspacing="0" class="table_contact table_inputContact">
                <col width="65">
                <col width="40">
                <col width="80">
                <col width="90">
                <tr>
                    <td style="text-align:right;">会员号：</td>
                    <td>${customerRecordDTO.memberDTO.memberNo}
                        <input type="hidden" id="memberId" value="${customerRecordDTO.memberDTO.id}"/></td>
                    <td style="text-align:right;">类型：</td>
                    <td>${customerRecordDTO.memberDTO.type}</td>
                </tr>
                <tr>

                    <td style="text-align:right;">入会日期：</td>
                    <td>${customerRecordDTO.memberDTO.joinDateStr}</td>
                    <td style="text-align:right;">失效日期：</td>
                    <td>${customerRecordDTO.memberDTO.serviceDeadLineStr}</td>
                </tr>
                <tr>
                    <td style="text-align:right;">储值金额：</td>
                    <td>${customerRecordDTO.memberDTO.balance}</td>

                    <td style="text-align:right;">上次购卡金额：</td>
                    <td>${customerRecordDTO.lastChargeAmount} </td>
                </tr>
            </table>
        </div>
        <div class="right_customer">
            <label class="lblTitle">&nbsp;</label>
            <table cellpadding="0" cellspacing="0" class="table_contact table_inputContact">
                <col width="150">
                <col width="70">
                <col width="90">
                <tr>
                    <td bgcolor="#eee" style="padding-left:10px;">服务项目</td>
                    <td bgcolor="#eee">剩余次数</td>
                    <td bgcolor="#eee">失效日期</td>
                </tr>

            </table>
            <div class="service">
                <table cellpadding="0" cellspacing="0" class="table_contact table_inputContact">
                    <col width="150">
                    <col width="70">
                    <col width="90">
                    <c:forEach items="${customerRecordDTO.memberDTO.memberServiceDTOs}" var="memberService">
                        <tr class="serviceDetail">
                            <td width="57%" style="padding-left:10px;">${memberService.serviceName}</td>
                            <td width="17%">${memberService.timesStr}</td>
                            <td width="26%">${memberService.deadlineStr}</td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
        <div id="chPasswordShow" class="i_scroll_percentage" style="width:284px;display:none;"
             title="修改密码">
            <table cellpadding="0" cellspacing="0" class="table2">
                <col width="85"/>
                <col/>
                <tr>
                    <td style="border:0;">原密码：</td>
                    <td style="border:0;padding:2px;"><input type="password" id="oldPw" value=""
                                                             style="border:1px solid #416885;padding:2px;"/>
                    </td>
                </tr>
                <tr>
                    <td style="border:0;">新密码：</td>
                    <td style="border:0;padding:2px;"><input type="password" id="newPw" value=""
                                                             style="border:1px solid #416885;padding:2px;"/>
                    </td>
                </tr>
                <tr>
                    <td style="border:0;">确认新密码：</td>
                    <td style="border:0;padding:2px;"><input type="password" id="cfNewPw" value=""
                                                             style="border:1px solid #416885;padding:2px;"/>
                    </td>
                </tr>
                <tr>
                    <td style="border:0;"></td>
                    <td style="border:0;padding:2px;">
                        <label><input type="checkbox" name="sendSms" value="true"
                                      id="sendSms"/>发送短信</label>
                    </td>
                </tr>
            </table>
        </div>
    </bcgogo:hasPermission>
</c:if>
<div class="height"></div>

<label class="lblTitle">车辆信息</label>
<div><span class="title_add"><a id="addVehicleBtn" href="#">添加车辆</a></span></div>
<div class="clear"></div>
<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
    <div class="vehicle_info">
        <c:forEach items="${customerRecordDTO.vehicles}" var="vehicle" varStatus="status">
            <div class="vehicle_information" index="${status.index}">
                <div class="delete_vehicle_btn title_close"></div>
                <table id="table_vehicle" width="100%" border="0" cellspacing="0" cellpadding="0">

                    <tr>
                        <td><span class="red_color">*</span>车牌号</td>
                        <td>
                            <input type="text" class="txt" style="width:85px;"  maxlength="9"
                                   name="vehicles[${status.index}].licenceNo" id="vehicles${status.index}.licenceNo"
                                   value="${vehicle.licenceNo!=null?vehicle.licenceNo:''}"/>
                            <input type="hidden" name="vehicles[${status.index}].id" id="vehicles${status.index}.id"
                                   value="${vehicle.id!=null?vehicle.id:''}"/>
                            <input type="hidden" name="vehicles[${status.index}].startMileage" id="vehicles${status.index}.startMileage"
                                   value="${vehicle.startMileage!=null?vehicle.startMileage:''}"/>
                        </td>
                        <td>车主</td>
                        <td>
                            <input type="text" class="txt" style="width:85px;"   maxlength="20"
                                   name="vehicles[${status.index}].contact" id="vehicles${status.index}.vehicleContact"
                                   value="${vehicle.contact!=null?vehicle.contact:''}"/>
                        </td>
                        <td>车主手机</td>
                        <td>
                            <input type="text" class="txt" style="width:85px;" maxlength="11"
                                   name="vehicles[${status.index}].mobile" id="vehicles${status.index}.vehicleMobile"
                                   value="${vehicle.mobile!=null?vehicle.mobile:''}"/>
                        </td>
                        <td>购车日期</td>
                        <td>
                            <input readonly="true" type="text" name="vehicles[${status.index}].dateString"
                                   id="vehicles${status.index}.dateString"
                                   value="${vehicle.dateString!=null?vehicle.dateString:''}"
                                   style="width:85px" class="txt"/>
                        </td>
                    </tr>
                    <tr>
                        <td>车辆品牌</td>
                        <td>
                            <input type="text" class="txt" style="width:85px;" maxlength="8"
                                   name="vehicles[${status.index}].brand" id="vehicles${status.index}.vehicleBrand" pagetype="customerVehicle"
                                   value="${vehicle.brand!=null?vehicle.brand:''}"/>
                        </td>
                        <td>车型</td>
                        <td>
                            <input type="text" class="txt" style="width:85px;" maxlength="8"
                                   name="vehicles[${status.index}].model" id="vehicles${status.index}.vehicleModel" pagetype="customerVehicle"
                                   value="${vehicle.model!=null?vehicle.model:''}"/>
                        </td>
                        <td>年代</td>
                        <td>
                            <input type="text" class="txt" style="width:85px;" maxlength="4"
                                   name="vehicles[${status.index}].year" id="vehicles${status.index}.year"
                                   value="${vehicle.year!=null?vehicle.year:''}"/>
                        </td>
                        <td>排量</td>
                        <td>
                            <input type="text" class="txt" style="width:85px;" maxlength="12"
                                   name="vehicles[${status.index}].engine" id="vehicles${status.index}.engine"
                                   value="${vehicle.engine!=null?vehicle.engine:''}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>车身颜色</td>
                        <td>
                            <input type="text" class="txt" style="width:85px;"  maxlength="15"
                                   name="vehicles[${status.index}].color" id="vehicles${status.index}.vehicleColor"
                                   value="${vehicle.color!=null?vehicle.color:''}"/>
                        </td>
                        <td>发动机号</td>
                        <td>
                            <input type="text" class="txt" style="width:85px;"
                                   name="vehicles[${status.index}].engineNo" id="vehicles${status.index}.vehicleEngineNo"
                                   value="${vehicle.engineNo!=null?vehicle.engineNo:''}"/>
                        </td>
                        <td>车架号</td>
                        <td colspan="3">
                            <input type="text" class="txt" style="width:248px;" maxlength="17"
                                   name="vehicles[${status.index}].chassisNumber" id="vehicles${status.index}.chassisNumber"
                                   value="${vehicle.chassisNumber!=null?vehicle.chassisNumber:''}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>当前里程</td>
                        <td>
                            <input type="text" class="txt" style="width:85px;"
                                   name="vehicles[${status.index}].obdMileage" id="vehicles${status.index}.obdMileage"
                                   value="${vehicle.obdMileage!=null?vehicle.obdMileage:''}"/>
                        </td>
                        <td>保养周期</td>
                        <td>
                            <input type="text" class="txt" style="width:85px;"
                                   name="vehicles[${status.index}].maintainMileagePeriod" id="vehicles${status.index}.maintainMileagePeriod"
                                   value="${vehicle.maintainMileagePeriod!=null?vehicle.maintainMileagePeriod:''}"/>
                        </td>
                        <%--<c:if test="${fourSShopVersions}">--%>
                            <td>OBD/后视镜信息</td>
                            <td colspan="3">
                                <c:choose>
                                    <c:when test="${not empty vehicle.gsmObdImei and not empty vehicle.gsmObdImeiMoblie}">
                                        <input type="text" class="txt imei_input" style="width:130px;background-color:#dbdbd6" placeHolder="IMIE号" readonly="true"
                                               name="vehicles[${status.index}].gsmObdImei" id="vehicles${status.index}.gsmObdImei"
                                               value="${vehicle.gsmObdImei!=null?vehicle.gsmObdImei:''}"/>
                                        <input type="text" class="txt sim_no_input" style="width:108px;background-color:#dbdbd6" placeHolder="SIM卡号" readonly="true"
                                               name="vehicles[${status.index}].gsmObdImeiMoblie" id="vehicles${status.index}.gsmObdImeiMoblie"
                                               value="${vehicle.gsmObdImeiMoblie!=null?vehicle.gsmObdImeiMoblie:''}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="text" class="txt imei_input" style="width:130px;" placeHolder="IMIE号"
                                               name="vehicles[${status.index}].gsmObdImei" id="vehicles${status.index}.gsmObdImei"
                                               value="${vehicle.gsmObdImei!=null?vehicle.gsmObdImei:''}"/>
                                        <input type="text" class="txt sim_no_input" style="width:108px;" placeHolder="SIM卡号"
                                               name="vehicles[${status.index}].gsmObdImeiMoblie" id="vehicles${status.index}.gsmObdImeiMoblie"
                                               value="${vehicle.gsmObdImeiMoblie!=null?vehicle.gsmObdImeiMoblie:''}"/>
                                    </c:otherwise>
                                </c:choose>


                            </td>
                            <td>&nbsp;</td>
                        <%--</c:if>--%>
                    </tr>

                </table>

            </div>
        </c:forEach>
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
    <textarea class="txt textarea" style="width:633px;" name="memo"  maxlength="400" id="memo" rows="2" cols="40">
            ${customerRecordDTO.memo}
    </textarea>
</div>
<div class="height"></div>
<div class="height"></div>
<div class="button">
    <!--<label class="chk"><input type="checkbox" />又是供应商</label>-->
    <a id="confirmBtn" class="btnSure">确 定</a>
    <a id="cancleBtn" class="btnSure">取 消</a>
</div>
</div>
</form:form>
</body>
</html>