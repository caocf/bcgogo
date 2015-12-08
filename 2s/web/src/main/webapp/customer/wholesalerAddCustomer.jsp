<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>


<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>新增客户信息</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/todo_new<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <link type="text/css" rel="stylesheet" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
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
        defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.CUSTOMER_MANAGE.ADD_NEW_CUSTOMER");
    </script>
    <script type="text/javascript">

    defaultStorage.setItem(storageKey.MenuUid, "WEB.CUSTOMER_MANAGER.CUSTOMER_MANAGE.ADD_NEW_CUSTOMER");


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
        $('.close').live("click", delContact)
        // 绑定 联系人列表 点击成为主联系人事件
        $('.icon_grayconnacter').live("click", switchContact);
        $('.J_setMainCont').live("click", wholesalerSwitchContact);

    });




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

        //重新计算经营产品
        resetThirdCategoryIdStr();


        var customerName = $("#name").val();
        if (G.isEmpty(customerName) || G.isEmpty($.trim(customerName))) {
            nsDialog.jAlert("用户名必须填写!");
            return false;
        }

        // 汽配版新增客户应该可以重名 by qxy 2014-09-12
//        if (!G.isEmpty($.trim(customerName))) {
//            var result = checkCustomerName(customerName);
//            if(!result){
//                return;
//            }
//        }


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
            if (!G.isEmpty($.trim(customerName))
                    && G.isEmpty($.trim($("#phone").val()))
                    && G.isEmpty($.trim($("#phoneSecond").val()))
                    && G.isEmpty($.trim($("#phoneThird").val()))
                    && isMobilesEmpty(contactMobiles)) {
                if (!checkCustomerName(customerName)) {
                    return;
                }
            }

//            if (!G.isEmpty($.trim(customerName)) && G.isEmpty($.trim($("#phone").val())) && isMobilesEmpty(contactMobiles)) {
//                if (!checkCustomerName(customerName)) {
//                    return;
//                }
//            }
//            if (!G.isEmpty($.trim(customerName)) && G.isEmpty($.trim($("#phoneSecond").val())) && isMobilesEmpty(contactMobiles)) {
//                if (!checkCustomerName(customerName)) {
//                    return;
//                }
//            }
//            if (!G.isEmpty($.trim(customerName)) && G.isEmpty($.trim($("#phoneSecond").val())) && isMobilesEmpty(contactMobiles)) {
//                if (!checkCustomerName(customerName)) {
//                    return;
//                }
//            }

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
                            $("#addSuccessDiv").dialog({
                                width: 330,
                                  modal:true,
                                title: "友情提示",
                                close: function () {
                                    if (G.isNotEmpty(result.data)) {
                                        window.location.href = 'unitlink.do?method=customer&customerId=' + result.data;
                                    } else {
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

    function wholesalerSwitchContact() {

        $(this).closest("table").find(".J_setMainCont").css("display","block");
        $(this).css("display","none");

        $(this).closest("table").find(".grey_txt").css("display","none");
        $(this).closest("tr").find('.grey_txt').css("display","block");



        $(this).closest("table").find(".icon_connacter").css("display","none");
        $(this).closest("tr").find('.icon_connacter').css("display","block");

        $(this).closest("table").find("input[name$='.mainContact']").val("0");
        $(this).closest("tr").find("input[name$='.mainContact']").val("1");
        $(this).closest("tr").find("input[name$='.level']").val("0");
        $(this).closest("table").find("input[name$='.level']").not($(this)).eq(0).val("1");
        $(this).closest("table").find("input[name$='.level']").not($(this)).eq(1).val("2");

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
            <div class="lblTitle" style="float: none;">基本信息</div>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table_search">
                <colgroup>
                    <col width="100"/>
                    <col/>
                    <col width="100"/>
                    <col/>
                    <col width="100"/>
                    <col/>
                </colgroup>
                <tr>
                    <th><span class="red_color">*</span>客户名称：</th>
                    <td><form:input path="name"
                                    value="${customerRecordDTO.name}"
                                    class="txt" maxlength="20"
                                    autocomplete="off"
                                    style="width:170px;"/></td>
                        <th>所属区域：</th>
                        <td><span class="divTit">
                        <select id="select_province"
                        name="province"
                        class="txt select"
                        style="color:#7e7e7e;width:80px;">
                        <option value="">所有省</option>
                        </select>
                        <select id="select_city" name="city" class="txt select" style="color:#7e7e7e;width:80px;">
                        <option value="">所有市</option>
                        </select><select id="select_township" name="region" class="txt select"
                                                   style="color:#7e7e7e;width:80px;">
                                               <option value="">所有区</option>
                                           </select>
                        </span>
                        </td>
                    <th>详细地址：</th>
                    <td>
                        <input id="input_address" name="address" type="text"
                               class="txt"  initValue="详细地址" placeHolder="详细地址"
                               style="width:170px;"/>
                    </td>
                </tr>
                <tr>
                    <th>传真：</th>
                    <td>
                        <form:input path="fax" value="${customerRecordDTO.fax}" maxlength="20"
                                    class="txt" style="width:170px;"/>
                    </td>
                    <th>生日：</th>
                    <td>
                        <form:input
                                path="birthdayString" value="${customerRecordDTO.birthdayString}"
                                class="txt" readonly="true" style="width:170px;"/>
                    </td>
                    <th>简称：</th>
                    <td>
                        <form:input path="shortName" value="${customerRecordDTO.shortName}"
                                    class="txt"
                                    maxlength="10" autocomplete="off" style="width:170px;"/>
                    </td>
                </tr>
                <tr>
                    <th>客户类别：</th>
                    <td>
                        <span id="input_settle">
                            <form:select path="customerKind" cssStyle="width:175px;" class="txt select">
                                <form:options items="${customerTypeMap}"/>
                            </form:select>
                        </span>
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
            <div class="lblTitle">联系人信息<span class="main_i">为主联系人</span></div>
            <div class="newCustomers2">
                <div class="contact_info">
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width="3%">&nbsp;</td>
                            <td width="18%">姓名</td>
                            <td width="18%">手机</td>
                            <td width="18%">Email</td>
                            <td width="18%">QQ</td>
                            <td width="18%">&nbsp;</td>
                        </tr>

                        <c:forEach items="${customerRecordDTO.contacts}" var="contact" varStatus="status">
                            <tr class="single_contact">
                                <c:choose>
                                    <c:when test="${status.index eq 0}">
                                        <td style="width:40px;"><a class="icon_connacter"></a></td>
                                        <input type="hidden" name="contacts[${status.index}].mainContact"
                                               id="contacts[${status.index}].mainContact" value="1"/>
                                    </c:when>
                                    <c:otherwise>
                                        <td style="width:40px;"><a class="icon_connacter" style="display: none;"></a></td>


                                        <input type="hidden" name="contacts[${status.index}].mainContact"
                                               id="contacts[${status.index}].mainContact" value="0"/>
                                    </c:otherwise>
                                </c:choose>
                                <td><input type="text" class="txt" name="contacts[${status.index}].name" maxlength="11"
                                           id="contacts[${status.index}].name" style="width:170px;"/></td>
                                <td><input type="text" class="txt" name="contacts[${status.index}].mobile"
                                           id="contacts[${status.index}].mobile" style="width:170px;" maxlength="11"/>
                                </td>
                                <td><input type="text" class="txt" name="contacts[${status.index}].email"
                                           id="contacts[${status.index}].email" style="width:170px;" maxlength="50"/>
                                </td>
                                <td><input type="text" class="txt" style="width:170px;" name="contacts[${status.index}].qq" maxlength="15"
                                           id="contacts[${status.index}].qq"/></td>
                                <input type="hidden" name="contacts[${status.index}].level"
                                       id="contacts[${status.index}].level"
                                       value="${status.index}"/>
                                <c:choose>
                                    <c:when test="${status.index eq 0}">
                                        <td><a class="blue_color J_setMainCont" style="display: none;">设为主联系人</a>
                                            <span class="grey_txt">主联系人</span>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td><a class="blue_color J_setMainCont">设为主联系人</a>
                                            <span class="grey_txt" style="display:none; ">主联系人</span>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
            <div class="clear height"></div>
            <div class="lblTitle">账户信息</div>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table_search">
                <colgroup>
                    <col width="100"/>
                    <col/>
                    <col width="100"/>
                    <col/>
                    <col width="100"/>
                    <col/>
                </colgroup>
                <tr>
                    <th>开户行：</th>
                    <td><form:input path="bank" value="${customerRecordDTO.bank}" maxlength="20"
                                    class="txt" cssStyle="width:170px;"/></td>
                    <th>开户名：</th>
                    <td><form:input path="bankAccountName" maxlength="20"
                                    value="${customerRecordDTO.bankAccountName}"
                                    class="txt" cssStyle="width:170px;"/></td>
                    <th>账号：</th>
                    <td><form:input path="account" maxlength="20" value="${customerRecordDTO.account}"
                                    class="txt" cssStyle="width:170px;"/></td>
                </tr>
                <tr>
                    <th>结算方式：</th>
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
                        <textarea name="memo" id="memo"
                                  maxlength="400" class="textarea_90"
                                  style="margin:5px 0;">${customerRecordDTO.memo}</textarea>
                    </td>
                </tr>
            </table>
            <div class="clear height"></div>
            <div id="businessScopeSelected" class="selectList"><b style="font-size:14px;margin-top: 5px;">经营产品</b></div>
            <div class="businessRange" style="margin-top:10px;"id="businessScopeDiv"></div>
            <div class="clear"></div>
            <div class="search_div">
                <div class="search_btn" id="confirmBtn1">确 定</div>
                <div class="empty_btn" id="cancleBtn">清 空</div>
                <c:if test="${wholesalerVersion}">
                    <input type="checkbox" style="margin-top:6px;margin-left:5px;" id="isSupplier"/>&nbsp;也是供应商
                    <input type="hidden" id="existRelation" value="false"/>
                </c:if>
                <div class="clear"></div>
            </div>

        </div>
    </div>
    </form:form>

    <div class="clear i_height"></div>
</div>

<%@include file="/WEB-INF/views/footer_html.jsp" %>



<div id="modifyClientDiv" class="alertMain newCustomers" style="display:none">
    <%@include file="modifyClient.jsp" %>
</div>

<div id="addSuccessDiv" style="display:none">
    <%@include file="addCustomerSuccess.jsp" %>
</div>

</body>
</html>
