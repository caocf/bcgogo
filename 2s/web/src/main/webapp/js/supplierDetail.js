$(document).ready(function () {
    var isOnlineShop = $("#isOnlineShop").val() == "true";
    if (!isOnlineShop) {
        setSupplierAreaInfo();
    }


    $("#supplierConsumerHistoryBtn,.J_supplierConsumeHistory").live("click", function (e) {
        e.preventDefault();
        openWindow("inquiryCenter.do?method=inquiryCenterIndex&pageType=customerOrSupplier&startDateStr=&customerOrSupplier="+$("#customerOrSupplierName").val());
    });

    $("#returnSupplierListBtn").live("click", function (e) {
        e.preventDefault();
        window.location.href = "customer.do?method=searchSuppiler&resetSearchCondition=true";
    });
    $(".J_customerOpt").bind("mouseenter",function (event) {
        event.stopImmediatePropagation();
        var _currentTarget = $(event.target).parent().find(".J_customerOptDetail");
        _currentTarget.show(80);

        _currentTarget.mouseleave(function (event) {
            if (event.relatedTarget != $(event.target).parent().parent().find(".J_customerOpt")[0]) {
                _currentTarget.hide(80);
            }
        });
    }).live("mouseleave", function (event) {
            event.stopImmediatePropagation();
            var _currentTarget = $(event.target).parent().find(".J_customerOptDetail");

            if (event.relatedTarget != _currentTarget[0]) {
                _currentTarget.hide(80);
            }

        });



    $("#saveSupplierAccountBtn").bind("click", function (e) {
        e.preventDefault();
        //
        var result = saveSupplierDetail();
        if (result && !G.Lang.isEmpty(result) && result.success) {
            //更新span
            $(".J_supplierAccountSpan").each(function (index) {
                $(this).text(G.normalize(result.data[$(this).attr("data-key")]));
            });
            $("#supplierAccountForm").find(".J_formreset").each(function () {
                $(this).attr("reset-value", G.Lang.normalize($(this).val()));
            });
            $("#editSupplierAccountInfo").show();
            $("#supplierAccountInfoShow").show();
            $("#supplierAccountInfoEdit").hide();
        }

    });
    $("#cancelSupplierAccountBtn").bind("click", function (e) {
        e.preventDefault();
        $("#editSupplierAccountInfo").show();
        $("#supplierAccountInfoShow").show();
        $("#supplierAccountInfoEdit").hide();
        $("#supplierAccountForm").find(".J_formreset").each(function () {
            $(this).val(G.Lang.normalize($(this).attr("reset-value")));
        });
    });
    $("#editSupplierAccountInfo").bind("click", function (e) {
        e.preventDefault();
        $(this).hide();
        $("#supplierAccountInfoShow").hide();
        $("#supplierAccountInfoEdit").show();
    });

    $("#editSupplierInfo").bind("click", function (e) {
        e.preventDefault();
        $(this).hide();
        $("#supplierBasicInfoShow").hide();
        $("#supplierBasicInfoEdit").show();
    });

    function validateSupplierBasicInfo(){
        var isOnlineShop = $("#isOnlineShop").val() == "true";
        if (!isOnlineShop) {
            var name = $("#name").val();
            if (G.isEmpty(name)) {
                nsDialog.jAlert("供应商名不能为空");
                return false;
            }
            if (G.isEmpty($("#province").val())) {
                nsDialog.jAlert("请输入省份");
                return false;
            }
            if (G.Lang.isEmpty($("#city").val()) && $("#province option").length > 1) {
                nsDialog.jAlert("请输入城市");
                return false;
            }

            var fax = $("#fax").val();
            if (!GLOBAL.Lang.isStringEmpty(fax) && !APP_BCGOGO.Validator.stringIsTelephoneNumber(fax)) {
                nsDialog.jAlert("传真号格式错误，请确认后重新输入！");
                return false;
            }
            var landLine = $("#landLine").val();
            var landLineSecond=$("#landLineSecond").val();
            var landLineThird=$("#landLineThird").val();
            if (APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact) {
                // 多联系人手机
                var contactMobiles = new Array();

                $("#supplierBasicInfoEdit input[name$='mobile']").each(function (index) {
                    if(!G.isEmpty($(this).val())){
                        contactMobiles.push($(this).val());
                    }
                });
                for (var mobileIndex in contactMobiles) {
                    if (!G.isEmpty(contactMobiles[mobileIndex]) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(contactMobiles[mobileIndex])) {
                        nsDialog.jAlert("手机号格式错误，请确认后重新输入！");
                        return false;
                    }
                }

                if(isMobileDuplicate(contactMobiles)) {
                    return false;
                }

                // validate mail
                var contactMails = new Array();
                $("#supplierBasicInfoEdit input[name$='mail']").each(function (index) {
                    if(!G.isEmpty($(this).val())){
                        contactMails.push($(this).val());
                    }
                });
                for (var mailIndex in contactMails) {
                    if (!APP_BCGOGO.Validator.stringIsEmail(contactMails[mailIndex])) {
                        nsDialog.jAlert("Email格式错误，请确认后重新输入！");
                        return false;
                    }
                }

                // validate qq
                var contactQQs = new Array();
                $("#supplierBasicInfoEdit input[name$='qq']").each(function (index, qq) {
                    if (!G.isEmpty($(this).val())) {
                        contactQQs.push($(this).val());
                    }
                });
                for (var qqIndex in contactQQs) {
                    if (!APP_BCGOGO.Validator.stringIsQq(contactQQs[qqIndex])) {
                        nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
                        return false;
                    }
                }

                if (!G.isEmpty($.trim(name)) && isMobilesEmpty(contactMobiles) && G.isEmpty($.trim(landLine))) {
                    if (!checkName($.trim(name))){
                        return false;
                    }
                }
                if (!G.isEmpty($.trim(name)) && isMobilesEmpty(contactMobiles) && G.isEmpty($.trim(landLineSecond))) {
                    if (!checkName($.trim(name))){
                        return false;
                    }
                }
                if (!G.isEmpty($.trim(name)) && isMobilesEmpty(contactMobiles) && G.isEmpty($.trim(landLineThird))) {
                    if (!checkName($.trim(name))){
                        return false;
                    }
                }

                if (!G.isEmpty($.trim(name)) && !isMobilesEmpty(contactMobiles)) {
                    if(!validateSupplierMobiles(contactMobiles,$("#supplierId").val())){
                        return false;
                    }

                }

                if (!G.isEmpty($.trim(name)) && !G.Lang.isEmpty(landLine)) {
                    //要判断是否存在同名的手机号
                    var r = APP_BCGOGO.Net.syncGet({
                        url: "customer.do?method=getSupplierByTelephone",
                        data: {
                            telephone: landLine
                        },
                        dataType: "json"
                    });
                    if (r != null && r.supplierIdStr != undefined) {
                        if ($("#supplierId").val() != r.supplierIdStr) {
                            nsDialog.jAlert('与供应商【' + r.supplier + '】的座机号相同，请重新输入！');
                            return;
                        }
                    }
                }
                if (!G.isEmpty($.trim(name)) && !G.Lang.isEmpty(landLineSecond)) {
                    //要判断是否存在同名的手机号
                    var r = APP_BCGOGO.Net.syncGet({
                        url: "customer.do?method=getSupplierByTelephone",
                        data: {
                            telephone: landLineSecond
                        },
                        dataType: "json"
                    });
                    if (r != null && r.supplierIdStr != undefined) {
                        if ($("#supplierId").val() != r.supplierIdStr) {
                            nsDialog.jAlert('与供应商【' + r.supplier + '】的座机号相同，请重新输入！');
                            return;
                        }
                    }
                }
                if (!G.isEmpty($.trim(name)) && !G.Lang.isEmpty(landLineThird)) {
                    //要判断是否存在同名的手机号
                    var r = APP_BCGOGO.Net.syncGet({
                        url: "customer.do?method=getSupplierByTelephone",
                        data: {
                            telephone: landLineThird
                        },
                        dataType: "json"
                    });
                    if (r != null && r.supplierIdStr != undefined) {
                        if ($("#supplierId").val() != r.supplierIdStr) {
                            nsDialog.jAlert('与供应商【' + r.supplier + '】的座机号相同，请重新输入！');
                            return;
                        }
                    }
                }

                // 校验主联系人信息
                var contacts = buildNormalKeyContacts();
                var validContactCount = countValidContact(contacts);
                if (validContactCount == 2) {
                    if (!mainContactIsValid(contacts)) {
                      setFirstValidToMainContact(contacts);
                    }
                } else if (validContactCount == 1) {
                    var index = firstValidContactFromContacts(contacts); // 只有一个联系人的时候 设为主联系人
                    if(!($("#contacts\\[" + index + "\\]\\.mainContact").val() == "1")){
//                        $("#contacts\\[" + index + "\\]\\.mainContact").val("1");
//                        var mainIndex = getMainContactFromContacts(contacts);
//                        $("#contacts\\[" + mainIndex + "\\]\\.mainContact").val("0");
                      setFirstValidToMainContact(contacts);
                    }
                }

            } else {
                //供应商都是多联系人
            }
        }
        return true;
    }

    function saveSupplierDetail() {
        var url = "supplier.do?method=updateSupplier";
        var param = $("#supplierBasicForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            if (!G.Lang.isEmpty(data[val.name])) {
                data[val.name] = data[val.name] + "," + val.value;
            } else {
                data[val.name] = val.value;
            }
        });
//        param = $("#supplierAccountForm").serializeArray();
//        $.each(param, function (index, val) {
//            if (!G.Lang.isEmpty(data[val.name])) {
//                data[val.name] = data[val.name] + "," + val.value;
//            } else {
//                data[val.name] = val.value;
//            }
//        });
//        param = $("#supplierBusinessForm").serializeArray();
//        $.each(param, function (index, val) {
//            if (!G.Lang.isEmpty(data[val.name])) {
//                data[val.name] = data[val.name] + "," + val.value;
//            } else {
//                data[val.name] = val.value;
//            }
//        });
        data['id'] = $("#supplierId").val();
        data['supplierId'] = $("#supplierId").val();
        data['customerId'] = $("#customerId").val();
        return APP_BCGOGO.Net.syncPost({"url": url, "data": data});
    }

    $("#saveSupplierBasicBtn").bind("click", function (e) {
        e.preventDefault();
        if (validateSupplierBasicInfo() && validateSupplierBusinessInfo()) {
        var result = saveSupplierDetail();
            if (result && !G.Lang.isEmpty(result) && result.success) {
                $("#customerOrSupplierName").val(result.data["name"]);
                //更新span
                $(".J_supplierBasicSpan").each(function (index) {
                  $(this).text(G.isEmpty(G.normalize(result.data[$(this).attr("data-key")])) ? "--" : G.normalize(result.data[$(this).attr("data-key")]));
                });
                if(G.isEmpty(result.data["identityStr"])){
                    $(".J_supplierIdentity").hide();
                }else{
                    $(".J_supplierIdentity").show();
                }
                $("#supplierBasicForm").find(".J_formreset").each(function () {
                    $(this).attr("reset-value", G.Lang.normalize($(this).val()));
                });

                $("#select_provinceInput").attr("reset-value", $("#province").val());
                $("#select_cityInput").attr("reset-value", $("#city").val());
                $("#select_regionInput").attr("reset-value", $("#region").val());

                $("#select_provinceInput").val($("#province").val());
                $("#select_cityInput").val($("#city").val());
                $("#select_regionInput").val($("#region").val());


              var contactHtmls = "";
              var otherContactHtmls = "";
              for (var i = 0; i < 3; i++) {
                var contact = G.Lang.normalize(result.data.contacts[i]);
                if (G.Lang.normalize(contact.mainContact) == 1) {
                  contactHtmls += '' +
                      '<td>主联系人：' + (G.isEmpty(G.Lang.normalize(contact.name, "")) ? "--" : G.Lang.normalize(contact.name, ""))+ '</td>' +
                      '<td>手机号：' + (G.isEmpty(G.Lang.normalize(contact.mobile, "")) ? "--" : G.Lang.normalize(contact.mobile, "")) + '</td>' +
                      '<td>QQ：' + (G.isEmpty(G.Lang.normalize(contact.qq, "")) ? "--" : G.Lang.normalize(contact.qq, "")) + '</td>' +
                      '<td>Email:' + (G.isEmpty(G.Lang.normalize(contact.email, "")) ? "--" : G.Lang.normalize(contact.email, ""))+ '</td>';
                } else {
                  otherContactHtmls += '<tr class="J_otherSupplierContactContainer">' +
                      '<td>联系人：' + (G.isEmpty(G.Lang.normalize(contact.name, "")) ? "--" : G.Lang.normalize(contact.name, "")) + '</td>' +
                      '<td>手机号：' + (G.isEmpty(G.Lang.normalize(contact.mobile, "")) ? "--" : G.Lang.normalize(contact.mobile, "")) + '</td>' +
                      '<td>QQ：' + (G.isEmpty(G.Lang.normalize(contact.qq, "")) ? "--" : G.Lang.normalize(contact.qq, "")) + '</td>' +
                      '<td>Email:' +  (G.isEmpty(G.Lang.normalize(contact.email, "")) ? "--" : G.Lang.normalize(contact.email, ""))+ '</td>' +
                      '</tr>';
                }
              }
              $("#supplierContactContainer").html(contactHtmls);
              $("#customerOtherInfo").find("table").find(".J_otherSupplierContactContainer").remove();
              $("#customerOtherInfo").find("table").prepend(otherContactHtmls);

                $("#editSupplierInfo").show();
                $("#supplierBasicInfoShow").show();
                $("#supplierBasicInfoEdit").hide();
                $("#qqTalk").multiQQInvoker({
                    QQ:$.fn.multiQQInvoker.getContactQQ()
                });
            }
        }

    });
    $("#cancelSupplierBasicBtn").bind("click", function (e) {
        e.preventDefault();
        $("#editSupplierInfo").show();
        $("#supplierBasicInfoShow").show();
        $("#supplierBasicInfoEdit").hide();
        $("#supplierBasicForm").find(".J_formreset").each(function () {
            $(this).val(G.Lang.normalize($(this).attr("reset-value")));
        });
        var isOnlineShop = $("#isOnlineShop").val() == "true";
        if (!isOnlineShop) {
            setSupplierAreaInfo();
        }
    });

    $("#saveSupplierBusinessBtn").bind("click", function (e) {
        e.preventDefault();
        //
        if(validateSupplierBusinessInfo()){
            var result = saveSupplierDetail();
            if (result && !G.Lang.isEmpty(result)) {
                if(result.success){
                    //更新span
                    $(".J_supplierBusinessSpan").each(function (index) {
                        $(this).text(G.normalize(result.data[$(this).attr("data-key")]));
                    });
                    $("#editSupplierBusinessInfo").show();
                    $("#supplierBusinessInfoShow").show();
                    $("#supplierBusinessInfoEdit").hide();
                }else{
                    nsDialog.jAlert("保存失败！");
                }
            }
        }

    });

    $("#cancelSupplierBusinessBtn").bind("click", function (e) {
        e.preventDefault();
        $("#editSupplierBusinessInfo").show();
        $("#supplierBusinessInfoShow").show();
        $("#supplierBusinessInfoEdit").hide();
        if(App.components.multiSelectTwoDialogTree){
            App.components.multiSelectTwoDialogTree.clearAllSelectedData();
            if(!G.Lang.isEmpty($("#thirdCategoryNodeListJson").val())){
                App.components.multiSelectTwoDialogTree.initSelectedData(JSON.parse(decodeURIComponent(G.Lang.normalize($("#thirdCategoryNodeListJson").val()))));
            }
        }
        if(App.components.multiSelectTwoDialog){
            App.components.multiSelectTwoDialog.clearAllSelectedData();
            if(!G.Lang.isEmpty($("#shopVehicleBrandModelDTOListJson").val())){
                App.components.multiSelectTwoDialog.initSelectedData(JSON.parse(decodeURIComponent(G.Lang.normalize($("#shopVehicleBrandModelDTOListJson").val()))));
                $("#partBrandModel").click();
            }else{
                $("#allBrandModel").click();
            }
        }
    });
    $("#editSupplierBusinessInfo").bind("click", function (e) {
        e.preventDefault();
        $(this).hide();
        $("#supplierBusinessInfoShow").hide();
        $("#supplierBusinessInfoEdit").show();
    });
    function validateSupplierBusinessInfo(){
        //设置经营范围的id
        var isOnlineShop = $("#isOnlineShop").val() == "true";
        //在线店铺不更新经营范围、服务范围
        if (!isOnlineShop) {
            var thirdCategoryIdStr = "";
            if($("#businessScopeTreeDiv")[0] && App.components.multiSelectTwoDialogTree){
                var addedData =  App.components.multiSelectTwoDialogTree.getAddedLeafDataList();
                if (addedData) {
                    $.each(addedData,function(index,val){
                        thirdCategoryIdStr+=val.idStr+",";
                    });
                    $("#thirdCategoryNodeListJson").val(encodeURIComponent(JSON.stringify(App.components.multiSelectTwoDialogTree.getAddedTreeNodeDataList())));
                }
                if(!G.Lang.isEmpty(thirdCategoryIdStr)){
                    thirdCategoryIdStr = thirdCategoryIdStr.substr(0,thirdCategoryIdStr.length-1);
                }
            }
            $("#thirdCategoryIdStr").val(thirdCategoryIdStr);
            //主营车型
            if($("#vehicleBrandModelDiv")[0] && App.components.multiSelectTwoDialog){
                var vehicleModelIdStr = "";
                var addedData =  App.components.multiSelectTwoDialog.getAddedData();
                if (addedData) {
                    $.each(addedData,function(index,val){
                        vehicleModelIdStr+=val.modelId+",";
                    });
                    $("#shopVehicleBrandModelDTOListJson").val(encodeURIComponent(JSON.stringify(addedData)));
                }
                if(!G.Lang.isEmpty(vehicleModelIdStr)){
                    vehicleModelIdStr = vehicleModelIdStr.substr(0,vehicleModelIdStr.length-1);
                }
                $("#vehicleModelIdStr").val(vehicleModelIdStr);
            }
        }
        return true;
    }

    $("#modifyClientDiv").dialog({
        autoOpen: false,
        resizable: false,
        title: "修改客户属性",
        height: 480,
        width: 820,
        modal: true,
        closeOnEscape: false,
        close: function () {
            $("#modifyClientDiv").val("");
            $("#identity").attr("checked", false);
        },
        showButtonPanel: true
    });

    $("#identity").click(function () {
        if (!$("#identity").attr("checked")) {
            var permanentDualRole = false;
            APP_BCGOGO.Net.syncAjax({
                url:"supplier.do?method=getSupplierById",
                dataType: "json",
                data: {supplierId:$("#supplierId").val()},
                success: function (data) {
                    if(data!=null && data.permanentDualRole){
                        permanentDualRole = true;
                    }
                }
            });
            if(permanentDualRole){
                nsDialog.jAlert("此供应商已经做过对账单，无法解除关系！");
                $("#identity").attr("checked", true);
                return;
            }
            nsDialog.jConfirm("是否确认解除绑定关系？", "", function (value) {
                if (!value) {
                    $("#identity").attr("checked", true);
                } else {
                    APP_BCGOGO.Net.syncPost({
                        url:"supplier.do?method=cancelSupplierBindingCustomer",
                        data:{supplierId:$("#supplierId").val()},
                        success:function(result){
                            if(result == 'success'){
                                nsDialog.jAlert("解绑成功！");
                              if (G.Lang.isEmpty($("#supplierId").val())) {
                                window.location.reload();
                              } else {
                                window.location.href = "unitlink.do?method=supplier&supplierId=" + $("#supplierId").val();
                              }
                            }else{
                                nsDialog.jAlert("解除绑定失败！")
                            }
                        },
                        error:function(){
                            nsDialog.jAlert("解除绑定失败！")
                        }
                    });
                }
            });
        } else {
            nsDialog.jConfirm("是否确认该供应商既是客户又是供应商", "", function (value) {
                if (value) {

                    //重新计算经营范围
                    $("#modifyClientDiv #newBusinessScopeSpan").text($("#businessScopeContentSpan").text());
                    $("#modifyClientDiv #updateBusinessScopeSpan").text($("#businessScopeContentSpan").text());

                    $("#modifyClientDiv #newThirdCategoryStr").val($("#thirdCategoryIdStr").val());
                    $("#modifyClientDiv #updateThirdCategoryStr").val($("#thirdCategoryIdStr").val());

                    $("#modifyClientDiv #newVehicleModelContentSpan").text($("#vehicleModelContentSpan").text());
                    $("#modifyClientDiv #updateVehicleModelContentSpan").text($("#vehicleModelContentSpan").text());

                    $("#modifyClientDiv #newVehicleModelIdStr").val($("#vehicleModelIdStr").val());
                    $("#modifyClientDiv #updateVehicleModelIdStr").val($("#vehicleModelIdStr").val());
                    var isOnlineShop = $("#isOnlineShop").val() == "true";
                    var selectBrandModel ="";
                    if(isOnlineShop){
                        selectBrandModel = $("#selectBrandModel").val();
                    }else{
                        selectBrandModel = $("input:radio[name='selectBrandModel']:checked").val();
                    }
                    $("#modifyClientDiv #newSelectBrandModel").val(selectBrandModel);
                    $("#modifyClientDiv #updateSelectBrandModel").val(selectBrandModel);

                    $("#radExist").click();
                    $(".select_supplier").show();

                    $("#modifyClientDiv").dialog({
                        beforeclose:function(){
                            // add by zhuj清除生成的联系人信息
                            $(".single_contact_gen").remove();
                            $(".warning").hide();
                            $(".single_contact input[name^='contacts3']").each(function () {
                                $(this).val("");
                            });
                        }
                    })
                    $("#modifyClientDiv").dialog("open");
                    $("#modifyClientDiv #customerId").val("");
                    var ajaxData = {
                        maxRows: $("#pageRows").val(),
                        customerOrSupplier: "customer",
                        filterType: "identity"
                    };
                    APP_BCGOGO.Net.asyncAjax({
                        url: "customer.do?method=searchCustomerDataAction",
                        dataType: "json",
                        data: ajaxData,
                        success: function (data) {
                            initTr(data);
                            initPages(data, "customerSuggest", "customer.do?method=searchCustomerDataAction", '', "initTr", '', '', ajaxData, '');
                        }
                    });
                } else {
                    $("#identity").attr("checked", false);
                }
            });
        }
    });

    //供应商评价
    if ($("#supplierShopId").val()) {

        var totalAverageScore = $("#totalAverageScore").val();
        var commentRecordCount = $("#commentRecordCount").val();
        var qualityAverageScore = $("#qualityAverageScore").val();
        var performanceAverageScore = $("#performanceAverageScore").val();
        var speedAverageScore = $("#speedAverageScore").val();
        var attitudeAverageScore = $("#attitudeAverageScore").val();
        var supplierId = $("#supplierId").val();
        var supplierShopId = $("#supplierShopId").val();

        var totalAverageScoreStr;
        if (totalAverageScore == 0 || totalAverageScore == "0") {
            totalAverageScoreStr = "暂无";
        } else {
            totalAverageScoreStr = totalAverageScore + '分';
        }
        var str = "";
        str += '<td id="supplierCommentScore" style="width:55px;" onmouseover="showSupplierCommentScore(this' + ',' + totalAverageScore + ',' + commentRecordCount + ',' + qualityAverageScore + ',' + performanceAverageScore + ',' + speedAverageScore + ',' + attitudeAverageScore
            + ');" onmouseout="scorePanelHide();"><span class="star" onclick="redirectShopCommentDetail(\'' + supplierShopId + '\')"></span><b class="color_yellow" ><span onclick="redirectShopCommentDetail(\'' + supplierShopId + '\')">' + totalAverageScoreStr + '</span></b> ';
        $("#supplierName").append($(str));

    }


    jQuery("#payable").draggable({
        handle: "div.i_upCenter"
    }); //弹出框能够drag
    jQuery("#payDetail").draggable({
        handle: "div.i_upCenter"
    });
    jQuery("#creditDeductionBtn").draggable();
    //输入过滤 //有弹出框的部分（既是客户又是 供应商）
    $("#mobile2,#mobile3,#mobile,#qq,#account").keyup(function () {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });
    //输入过滤
    $("#mobile3,#mobile2,#mobile,#qqStr,#account").keyup(function () {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });
    $("#landLine,#fax,#landLineSecond,#fax3,#landLineThird,#fax2").keyup(function () {
        $(this).val($(this).val().replace(/[^\d\-]+/g, "").replace(/\-+/g, "-"));
        if ($(this).val().charAt(0) == '-') {
            $(this).val("");
        }
    });

    //输入提示
    $("#mobile").blur(function () {
        var mobile = document.getElementById("mobileStr").value;
        if (mobile != "") {
            if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($("#mobile").val())) {
                nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                return;
            } else {
                //要判断是否存在同名的手机号
                var r = APP_BCGOGO.Net.syncGet({
                    url: "customer.do?method=getSupplierByMobile",
                    data: {
                        mobile: mobile
                    },
                    dataType: "json"
                });
                if (r != null && r.supplierIdStr != undefined) {
                    if ($("#supplierId").val() != r.supplierIdStr) {
                        nsDialog.jAlert('与供应商【' + r.supplier + '】的手机号相同，请重新输入！');
                        return;
                    }
                }
            }
        }
    });
    $("#landLine").blur(function () {
        var landline = $("#landLine").val();
        if (landline != "") {
            if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($("#landLine").val())) {
                nsDialog.jAlert("输入的座机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                return;
            } else {
                //要判断是否存在同名的手机号
                var r = APP_BCGOGO.Net.syncGet({
                    url: "customer.do?method=getSupplierByTelephone",
                    data: {
                        telephone: landline
                    },
                    dataType: "json"
                });
                if (r != null && r.supplierIdStr != undefined) {
                    if ($("#supplierId").val() != r.supplierIdStr) {
                        nsDialog.jAlert('与供应商【' + r.supplier + '】的座机号相同，请重新输入！');
                        return;
                    }
                }
            }
        }
    });

    $("#landLineSecond").blur(function () {
        var landLineSecond = $("#landLineSecond").val();
        if (landLineSecond != "") {
            if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($("#landLineSecond").val())) {
                nsDialog.jAlert("输入的座机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                return;
            } else {
                //要判断是否存在同名的手机号
                var r = APP_BCGOGO.Net.syncGet({
                    url: "customer.do?method=getSupplierByTelephone",
                    data: {
                        telephone: landLineSecond
                    },
                    dataType: "json"
                });
                if (r != null && r.supplierIdStr != undefined) {
                    if ($("#supplierId").val() != r.supplierIdStr) {
                        nsDialog.jAlert('与供应商【' + r.supplier + '】的座机号相同，请重新输入！');
                        return;
                    }
                }
            }
        }
    });

    $("#landLineThird").blur(function () {
        var landLineThird = $("#landLineThird").val();
        if (landLineThird != "") {
            if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($("#landLineThird").val())) {
                nsDialog.jAlert("输入的座机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
                return;
            } else {
                //要判断是否存在同名的手机号
                var r = APP_BCGOGO.Net.syncGet({
                    url: "customer.do?method=getSupplierByTelephone",
                    data: {
                        telephone: landLineThird
                    },
                    dataType: "json"
                });
                if (r != null && r.supplierIdStr != undefined) {
                    if ($("#supplierId").val() != r.supplierIdStr) {
                        nsDialog.jAlert('与供应商【' + r.supplier + '】的座机号相同，请重新输入！');
                        return;
                    }
                }
            }
        }
    });

    $("#fax").blur(function () {
        if (!APP_BCGOGO.Validator.stringIsTelephoneNumber($(this).val()) && $(this).val() != "") {
            nsDialog.jAlert("传真号格式错误，请确认后重新输入！");
        }
    });
    $("#email").blur(function () {
        if (!APP_BCGOGO.Validator.stringIsEmail($(this).val()) && $(this).val() != "") {
            nsDialog.jAlert("邮箱格式错误，请确认后重新输入！");
        }
    });

    //防止右击粘贴非法字符
    if (/msie/i.test(navigator.userAgent)) {
        // TODO IE下的待定
    } else {
        //document.getElementById("mobile").addEventListener("input", checkNumberInput, false);
        $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").live("change",checkNumberInput);
        document.getElementById("account").addEventListener("input", checkNumberInput, false);
        //document.getElementById("qqStr").addEventListener("input", checkNumberInput, false);
        document.getElementById("landLine").addEventListener("input", checkTelInput, false);
        document.getElementById("landLineSecond").addEventListener("input", checkTelInput, false);
        document.getElementById("landLineThird").addEventListener("input", checkTelInput, false);
        document.getElementById("fax").addEventListener("input", checkTelInput, false);
    }
    $(".single_contact input[name^='contact']").filter("input[name$='qq'],input[name$='mobile']").keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
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

    function checkNumberInput() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    }

    function checkTelInput() {
        $(this).val($(this).val().replace(/[^\d\-]+/g, "").replace(/\-+/g, "-"));
        if ($(this).val().charAt(0) == '-') {
            $(this).val("");
        }
    }

    /**
     * 页面时间控件初始化
     */
    $("#toTime,#fromTime,#startTime,#endTime").bind("click",function () {
        $(this).blur();
    }).datetimepicker({
            "showButtonPanel": !($.browser.msie && $.browser.version === "8.0"),
            "changeYear": true,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": ""
        });

    $("#btnPay").bind("click", function () {
        Mask.Login();
        $("#payable").css("display", "block");
        var supplierId = $("#supplierId").val();
        toPayableSettlement(supplierId);
    });

    $("#btnPayed").bind("click", function (e) {
        e.stopPropagation();
        Mask.Login();
        $("#balance").text(jQuery("#hiddenDeposit").val());

        var $deposit = $("#depositDiv"),
            left = $(document).width() / 2 - $deposit.width() / 2; // TODO  找到 mask 的hide 的地方
        //TODO 改用jquery dialog
        $deposit
            .css("display", "block")
            .css("left", left)
            .css("top", 150);
    });

    $("#duizhan,#duizhang").bind("click", function () {
        toCreateStatementOrder($("#supplierId").val(), "SUPPLIER_STATEMENT_ACCOUNT");
    });
    /**
     * 现金按钮Click事件，弹出结算详细界面，初始化各付款项目
     *
     */
    jQuery("#selMoney").bind("click", function () {
        var lstPayAbles = new Array();
        //获得选中的应付款记录
        jQuery("[name='check']").each(function () {
            if (jQuery(this).is(':checked')) {
                var temp = {
                    "id": jQuery(this).val()
                };
                if (lstPayAbles == null) {
                    lstPayAbles = [];
                }
                lstPayAbles.push(temp);
            }
        });
        if (lstPayAbles.length == 0) {
            nsDialog.jAlert("请选择单据进行结算！");
            return;
        }
        jQuery("#payDetail").css("display", "block");
        initPayDetail();
    });
    /**
     * 关闭弹出框
     */
    jQuery("#div_close,#cancleBtn").click(function () {
      clearCustomerDepositAddData();
    });
    jQuery("#cancleBtnPayDetail,#div_close_pay_detail").click(function () {
        jQuery(".i_upBody :text").val("");
        jQuery("#payDetail").css("display", "none");

        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch (e) {
            ;
        }
    });

    /**
     * 添加预付款确认按钮Click事件
     */
    jQuery("#sureBtn").click(function () {
        $(this).attr("disabled", true);
        //现金
        var cash = dataTransition.rounding(parseFloat(jQuery("#cashDeposit").val() == "" ? 0 : jQuery("#cashDeposit").val()), 2);
        // 银行卡
        var bankCardAmount = dataTransition.rounding(parseFloat(jQuery("#bankCardAmountDeposit").val() == "" ? 0 : jQuery("#bankCardAmountDeposit").val()), 2);
        //   支票
        var checkAmount = dataTransition.rounding(parseFloat(jQuery("#checkAmountDeposit").val() == "" ? 0 : jQuery("#checkAmountDeposit").val()), 2);
        //支票号码
        var checkNo = (jQuery("#checkNoDeposit").val() == "" || jQuery("#checkNoDeposit").val() == jQuery("#checkNoDeposit").attr("initValue")) ? 0 : jQuery("#checkNoDeposit").val();
        //实付
        var actuallyPaid = dataTransition.rounding(parseFloat(jQuery("#actuallyPaidDeposit").text() == "" ? 0 : jQuery("#actuallyPaidDeposit").text()), 2);
        //供应商ID
        var supplierId = jQuery("#supplierId").val();
        if (actuallyPaid == 0) {
            nsDialog.jAlert("实付不能为0！");
            return;
        } else if (actuallyPaid != dataTransition.rounding(cash + bankCardAmount + checkAmount, 2)) {
            nsDialog.jAlert("现金、银行卡、支票之和与实付不符！");
            return;
        }
        var memo = $("#depositMemo").val();
        var depositDTO = {
            "cash": cash,
            "bankCardAmount": bankCardAmount,
            "checkAmount": checkAmount,
            "checkNo": checkNo,
            "actuallyPaid": actuallyPaid,
            "supplierId": supplierId,
            "memo": memo
        };
        APP_BCGOGO.Net.asyncPost({
            url: "payable.do?method=addDeposit",
            data: {
                depositDTO: JSON.stringify(depositDTO),
                print: $("#depositDiv #print").attr("checked")
            },
            cache: false,
            dataType: "json",
            success: function (jsonStr) {
                if (jsonStr.success) {
                    nsDialog.jAlert("预付款添加成功！");
                    jQuery("#mask").css("display", "none");
                    jQuery("#depositDiv").css("display", "none");
                    $("#totalDepositSpan").text(jsonStr.data+"元");
                    $("#depositDiv #balance").text(jsonStr.data);
                    $("#hiddenDeposit").val(jsonStr.data);

                    if (jsonStr.operation && jsonStr.operation == 'print') {
                        window.open("payable.do?method=printDeposit&supplierId=" + supplierId + "&cashDeposit=" + cash + "&bankCardAmountDeposit=" + bankCardAmount + "&checkAmountDeposit=" + checkAmount + "&checkNoDeposit=" + checkNo + "&actuallyPaidDeposit=" + actuallyPaid + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
                    }
                } else {
                    nsDialog.jAlert("预付款添加失败！");
                }
            }
        });

        if (parent.document.getElementById("iframe_PopupBox_1") != null) {
            parent.document.getElementById("iframe_PopupBox_1").style.display = "none";
        }
        if (parent.document.getElementById("mask") != null) {
            parent.document.getElementById("mask").style.display = "none";
        }

        clearCustomerDepositAddData();


    });
    /**
     * 预付款弹出框  现金,银行卡，支票keyup事件
     *
     */
    jQuery("#cashDeposit,#bankCardAmountDeposit,#checkAmountDeposit").bind("keyup", function () {
        $(this).css("color","#000000");
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
        //现金
        var cash = dataTransition.rounding(parseFloat($("#cashDeposit").val() == "" ? 0 : $("#cashDeposit").val()), 2);
        // 银行卡
        var bankCardAmount = dataTransition.rounding(parseFloat($("#bankCardAmountDeposit").val() == "" ? 0 : $("#bankCardAmountDeposit").val()), 2);
        //   支票
        var checkAmount = dataTransition.rounding(parseFloat($("#checkAmountDeposit").val() == "" ? 0 : $("#checkAmountDeposit").val()), 2);
        jQuery("#actuallyPaidDeposit").text(dataTransition.rounding(cash + bankCardAmount + checkAmount, 2));
    });

    $("#checkNoDeposit").click(function(){
       if($(this).attr("initValue") == $(this).val()) {
           $(this).val('');
           $(this).css("color","#000000");
       }
    })
    .blur(function(){
        if(G.isEmpty($(this).val()) || $(this).attr("initValue") == $(this).val()) {
            $(this).val($(this).attr("initValue"));
            $(this).css("color","#9a9a9a");
        }
    });

    /**
     * 按消费时间排序
     */
    jQuery("#arrear").bind("click", function () {
        var fromTime = jQuery("#fromTimeStr").val();
        var toTime = jQuery("#toTimeStr").val();
        var orderByName = jQuery("#arrear").attr("name");
        var arrearClass = jQuery("#arrear").attr("class");
        var supplierId = jQuery("#supplierId").val();
        var orderByType = "desc";
        if (arrearClass == "arrearDown") {
            orderByType = "asc"
            jQuery("#arrear").removeClass("arrearDown").addClass("arrearUp");
        } else {
            jQuery("#arrear").removeClass("arrearUp").addClass("arrearDown");
        }
        APP_BCGOGO.Net.asyncPost({
            url: "payable.do?method=searchPayable",
            data: {
                startPageNo: 1,
                fromTime: fromTime,
                toTime: toTime,
                orderName: orderByName,
                orderType: orderByType,
                supplierId: supplierId
            },
            cache: false,
            dataType: "json",
            success: function (jsonStr) {
                initPayableTable(jsonStr);
                initPages(jsonStr, "dynamical3", "payable.do?method=searchPayable", '', "initPayableTable", '', '', {
                    startPageNo: 1,
                    fromTime: fromTime,
                    toTime: toTime,
                    orderName: orderByName,
                    orderType: orderByType,
                    supplierId: supplierId
                }, '');
            }
        });
    });

    jQuery("#arrearDown").bind("click", function () {
        var fromTime = jQuery("#fromTimeStr").val();
        var toTime = jQuery("#toTimeStr").val();
        var orderByName = jQuery("#arrearDown").attr("name");
        var arrearClass = jQuery("#arrearDownArrow").attr("class");
        var supplierId = jQuery("#supplierId").val();
        var orderByType = "desc";
        if (arrearClass == "arrowDown") {
            orderByType = "asc";
            jQuery("#arrearDownArrow").removeClass("arrowDown").addClass("arrowUp");
        } else {
            jQuery("#arrearDownArrow").removeClass("arrowUp").addClass("arrowDown");
        }
        APP_BCGOGO.Net.asyncPost({
            url: "payable.do?method=payHistoryRecords",
            data: {
                startPageNo: 1,
                supplierId: supplierId,
                startTime: fromTime,
                endTime: toTime,
                orderByName: orderByName,
                orderByType: orderByType
            },
            cache: false,
            dataType: "json",
            success: function (jsonStr) {
                initPayableHistoryRecord(jsonStr);
                initPages(jsonStr, "dynamical4", "payable.do?method=payHistoryRecords", '', "initPayableHistoryRecord", '', '', {
                    startPageNo: 1,
                    supplierId: supplierId,
                    startTime: fromTime,
                    endTime: toTime,
                    orderByName: orderByName,
                    orderByType: orderByType
                }, '');
            }
        });
    });

    //checkBox选择事件
    jQuery("[name='check']").live("click", recalculateTotal);


    //确认付款按钮Click事件
    jQuery("#surePay").bind("click", function () {
        var lstPayAbles = new Array();
        //获得选中的应付款记录
        jQuery("[name='check']").each(function () {
            if (jQuery(this).is(':checked')) {
                var temp = {
                    "idStr": $.trim($(this).val())
                };
                if (lstPayAbles == null) {
                    lstPayAbles = [];
                }
                lstPayAbles.push(temp);
            }
        });
        if (lstPayAbles.length == 0) {
            nsDialog.jAlert("请选择单据进行结算！");
            return;
        }
        var payTotal = dataTransition.rounding(parseFloat(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text()), 2); //应付
        //扣款
        var deduction = dataTransition.rounding(parseFloat(jQuery("#deduction").val() == "" ? 0 : jQuery("#deduction").val()), 2);
        //欠款挂账
        var creditAmount = dataTransition.rounding(parseFloat(jQuery("#creditAmount").val() == "" ? 0 : jQuery("#creditAmount").val()), 2);
        //现金
        var cash = dataTransition.rounding(parseFloat(jQuery("#cash").val() == "" ? 0 : jQuery("#cash").val()), 2);
        //银行卡
        var bankCardAmount = dataTransition.rounding(parseFloat(jQuery("#bankCardAmount").val() == "" ? 0 : jQuery("#bankCardAmount").val()), 2);
        //支票
        var checkAmount = dataTransition.rounding(parseFloat(jQuery("#checkAmount").val() == "" ? 0 : jQuery("#checkAmount").val()), 2);
        //支票号码
        var checkNo = jQuery("#checkNo").val();
        //用预付款
        var depositAmount = dataTransition.rounding(parseFloat(jQuery("#depositAmount").val() == "" ? 0 : jQuery("#depositAmount").val()), 2);
        //实付
        var actuallyPaidPop = dataTransition.rounding(parseFloat(jQuery("#actuallyPaid").val() == "" ? 0 : jQuery("#actuallyPaid").val()), 2);
        var actuallyPaid = dataTransition.rounding(cash + bankCardAmount + checkAmount + depositAmount, 2);
        //supplierId
        var supplierId = jQuery("#supplierId").val();
        //可用预付款
        var depositAvaiable = dataTransition.rounding(parseFloat($.trim($("#deposit_avaiable").text()) == "" ? 0 : $.trim($("#deposit_avaiable").text())), 2);
        /**
         * 实付金额=0
         *判断：应付<>扣款+挂账，则提示：“实付为0，是否挂账或扣款免付？”，挂扣免
         *选择挂账，则清除现金、银行、支票为空，挂账=应收-扣款
         *选择扣款免付，则清除现金、银行、支票为空，扣款=应付-挂账
         */
        if (depositAmount > depositAvaiable + 0.0001) {
            nsDialog.jAlert("可用预付款不足！请选择其他支付方式！");
            return;
        }
        if (actuallyPaid == 0) {
            if (payTotal != dataTransition.rounding(deduction + creditAmount, 2)) {
                jQuery("#creditDeductionBtn").css("display", "block");
                creditAmount = dataTransition.rounding(jQuery("#creditAmount").val() == "" ? 0 : jQuery("#creditAmount").val(), 2);
                deduction = dataTransition.rounding(jQuery("#deduction").val() == "" ? 0 : jQuery("#deduction").val(), 2);
            } else {
                if (confirm("实付为0，请再次确认是否挂账或扣款免付!")) {
                    var payableHistoryDTO = {
                        "deduction": deduction,
                        "creditAmount": creditAmount,
                        "cash": cash,
                        "bankCardAmount": bankCardAmount,
                        "checkAmount": checkAmount,
                        "checkNo": checkNo,
                        "depositAmount": depositAmount,
                        "actuallyPaid": actuallyPaid,
                        "supplierId": supplierId
                    };
                    APP_BCGOGO.Net.asyncPost({
                        url: "payable.do?method=payToSupplier",
                        data: {
                            lstPayAbles: JSON.stringify(lstPayAbles),
                            payableHistoryDTO: JSON.stringify(payableHistoryDTO)
                        },
                        cache: false,
                        dataType: "json",
                        success: function (jsonStr) {
                            if (jsonStr.message == "success") {
                                if ($("#checkDetailPrint").attr("checked")) {
                                    printPayable();
                                } else {
                                    if (confirm("是否需要打印结算单？")) {
                                        printPayable();
                                    }
                                }
                                jQuery("#payDetail").css("display", "none");
                                //更新供应商详细页面，应付金额
                                updateCreditAmount();
                                //更新供应商详细页面，付预付款
                                updateDeposit();
                                //刷新应付结算
                                refreshPayableTable();
                                recalculateTotal();
                                setZero();
                                nsDialog.jAlert("结算成功！");
                            } else {
                                nsDialog.jAlert("结算失败！");
                            }
                        }
                    });
                }
            }
        } //实付金额不为0
        else {
            /**
             *       实付<>(现金+银行+支票),
             *         弹出提示： “实付金额与现金、银行、支票的金额不符，请修改。
             *        如果挂账或扣款免付，请输入 0。”确定后返回。
             */
            if (actuallyPaidPop != dataTransition.rounding(cash + bankCardAmount + checkAmount + depositAmount, 2)) {
                nsDialog.jAlert("实付金额与现金、银行、支票的金额不符，请修改。  如果挂账或扣款免付，请输入 0!");
            } else if (actuallyPaid != dataTransition.rounding(payTotal - deduction - creditAmount, 2)) {
                /**
                 * 判断：实付<>(应付-扣款-挂账),提示:“实付金额与扣款、
                 *        挂账金额不符合，请修改。” 确定后返回。
                 */
                nsDialog.jAlert("实付金额与扣款、挂账金额不符合，请修改。");
            } else {
                if (confirm("本次结算应付：" + payTotal + " 元，扣款：" + deduction + " 元，挂账" + creditAmount + " 元，实付：" + actuallyPaid + " 元（现金：" + cash + " 元，银行卡：" + bankCardAmount + " 元，支票：" + checkAmount + " 元，预付款：" + depositAmount + " 元）")) {
                    var payableHistoryDTO = {
                        "deduction": deduction,
                        "creditAmount": creditAmount,
                        "cash": cash,
                        "bankCardAmount": bankCardAmount,
                        "checkAmount": checkAmount,
                        "checkNo": checkNo,
                        "depositAmount": depositAmount,
                        "actuallyPaid": actuallyPaid,
                        "supplierId": supplierId
                    };
                    APP_BCGOGO.Net.asyncPost({
                        url: "payable.do?method=payToSupplier",
                        data: {
                            lstPayAbles: JSON.stringify(lstPayAbles),
                            payableHistoryDTO: JSON.stringify(payableHistoryDTO)
                        },
                        cache: false,
                        dataType: "json",
                        success: function (jsonStr) {
                            if (jsonStr.message == "success") {
                                if ($("#checkDetailPrint").attr("checked")) {
                                    printPayable();
                                } else {
                                    if (confirm("是否需要打印结算单？")) {
                                        printPayable();
                                    }
                                }
                                jQuery("#payDetail").css("display", "none");
                                updateCreditAmount();
                                //更新供应商详细页面，付预付款
                                updateDeposit();
                                refreshPayableTable();
                                recalculateTotal();
                                setZero();
                                nsDialog.jAlert("结算成功！");
                            } else {
                                nsDialog.jAlert(jsonStr.message);
                            }
                        }
                    });
                }
            }
        }
    });
    /**
     *     全选
     */
    jQuery("#checkAll").bind("click", function () {
        if (jQuery(this).is(':checked')) {
            jQuery("[name='check']").attr("checked", 'true'); //
            var totalCreadit = 0;
            jQuery("[name='check']").each(function () {
                if (jQuery(this).is(':checked')) {
                    var temp = jQuery(this).parent().parent().children('td').eq(7).html();
                    if (temp == undefined || temp == null || temp == "") temp = 0;
                    totalCreadit = totalCreadit + parseFloat(temp);
                }
            });
            jQuery("#totalCreditAmount").text(totalCreadit);
            jQuery("#actually_paid").val(totalCreadit);
        } else {
            jQuery("[name='check']").removeAttr("checked"); //
            jQuery("#totalCreditAmount").text(0);
            jQuery("#actually_paid").val("");
        }

    });

    /**
     * 修改扣款数据
     */
    jQuery("#deduction").bind("keyup",function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
        editDeduction();
        //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
        $("#actually_paid").val($("#actuallyPaid").val());
        $("#credit_amount").val($("#creditAmount").val());
        $("#supplier_deduction").val($("#deduction").val());
    }).bind("dblclick", function () {
            dblclickDeduction();
            //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
            $("#actually_paid").val($("#actuallyPaid").val());
            $("#credit_amount").val($("#creditAmount").val());
            $("#supplier_deduction").val($("#deduction").val());
        });

    /**
     * 修改挂账
     */
    jQuery("#creditAmount").bind("keyup",function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
        editCreditAmount();
        //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
        $("#actually_paid").val($("#actuallyPaid").val());
        $("#credit_amount").val($("#creditAmount").val());
        $("#supplier_deduction").val($("#deduction").val());
    }).bind("dblclick", function () {
            dblclickCreditAmount();
            //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
            $("#actually_paid").val($("#actuallyPaid").val());
            $("#credit_amount").val($("#creditAmount").val());
            $("#supplier_deduction").val($("#deduction").val());
        });

    /**
     * 修改现金
     *
     */
    jQuery("#cash").bind("keyup",function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
        editPaidAmount();
        //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
        $("#actually_paid").val($("#actuallyPaid").val());
        $("#credit_amount").val($("#creditAmount").val());
        $("#supplier_deduction").val($("#deduction").val());
    }).bind("dblclick", function () {
            dblclickcash();
            //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
            $("#actually_paid").val($("#actuallyPaid").val());
            $("#credit_amount").val($("#creditAmount").val());
            $("#supplier_deduction").val($("#deduction").val());
        });

    /**
     * 修改银行卡
     *
     */
    jQuery("#bankCardAmount").bind("keyup",function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
        editPaidAmount();
        //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
        $("#actually_paid").val($("#actuallyPaid").val());
        $("#credit_amount").val($("#creditAmount").val());
        $("#supplier_deduction").val($("#deduction").val());
    }).bind("dblclick", function () {
            dblclickBankCardAmount();
            //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
            $("#actually_paid").val($("#actuallyPaid").val());
            $("#credit_amount").val($("#creditAmount").val());
            $("#supplier_deduction").val($("#deduction").val());
        });
    /**
     * 修改支票
     *
     */
    jQuery("#checkAmount").bind("keyup",function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
        editPaidAmount();
        //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
        $("#actually_paid").val($("#actuallyPaid").val());
        $("#credit_amount").val($("#creditAmount").val());
        $("#supplier_deduction").val($("#deduction").val());
    }).bind("dblclick", function () {
            dblclickCheckAmount();
            //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
            $("#actually_paid").val($("#actuallyPaid").val());
            $("#credit_amount").val($("#creditAmount").val());
            $("#supplier_deduction").val($("#deduction").val());
        });
    /**
     * 修改预付款
     *
     */
    jQuery("#depositAmount").bind("keyup",function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
        editDepositAmount();
        //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
        $("#actually_paid").val($("#actuallyPaid").val());
        $("#credit_amount").val($("#creditAmount").val());
        $("#supplier_deduction").val($("#deduction").val());
    }).bind("dblclick", function () {
            dblclickDepositAmount();
            //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
            $("#actually_paid").val($("#actuallyPaid").val());
            $("#credit_amount").val($("#creditAmount").val());
            $("#supplier_deduction").val($("#deduction").val());
        });
    /**
     *
     *修改实付：实付=0，则提示：“实付为0，是否挂账或扣款免付？”，挂账\扣款免付\取消
     *选择挂账，则清除现金、银行、支票、预付款为空，挂账=应付-扣款
     *选择扣免，则清除现金、银行、支票、预付款为空，扣款=应付-挂账
     *
     */
    jQuery("#actuallyPaid").bind("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
        editActuallyPaid();
        //modified by WeiLingfeng 实付、挂账、扣款 从详细结算弹出框，带回到应付结算页面
        $("#actually_paid").val($("#actuallyPaid").val());
        $("#credit_amount").val($("#creditAmount").val());
        $("#supplier_deduction").val($("#deduction").val());
    });

    //     选择挂账，则清除现金、银行、支票、预付款为空，挂账=应付-扣款
    jQuery("#creditAmountBtn").live("click", function () {
        if (jQuery("#type").val() == "mayPay") { //如果是应付款页面
            var payTotal = dataTransition.rounding(parseFloat(jQuery("#totalCreditAmount").text() == "" ? 0 : jQuery("#totalCreditAmount").text()), 2); //应付
            var deduction = dataTransition.rounding(parseFloat(jQuery("#supplier_deduction").val() == "" ? 0 : jQuery("#supplier_deduction").val()), 2); //扣款
            //      挂账=应付-扣款
            jQuery("#credit_amount").val(dataTransition.rounding(payTotal - deduction, 2));
            jQuery("#creditDeductionBtn").css("display", "none");
        } else if (jQuery("#type").val() == "payDetail") { //如果是付款详细界面
            var payTotal = dataTransition.rounding(parseFloat(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text()), 2); //应付
            var deduction = dataTransition.rounding(parseFloat(jQuery("#deduction").val() == "" ? 0 : jQuery("#deduction").val()), 2); //扣款
            //      挂账=应付-扣款
            jQuery("#creditAmount").val(dataTransition.rounding(payTotal - deduction, 2));
            //     清除现金、银行、支票、预付款为空
            jQuery("#checkAmount,#bankCardAmount,#cash,#depositAmount,").val(0); //支票
            jQuery("#creditDeductionBtn").css("display", "none");
        }
    });
    //选择扣免，则清除现金、银行、支票、预付款为空，扣款=应付-挂账
    jQuery("#deductionBtn").bind("click", function () {
        if (jQuery("#type").val() == "mayPay") { //如果是应付款页面
            var payTotal = dataTransition.rounding(parseFloat(jQuery("#totalCreditAmount").text() == "" ? 0 : jQuery("#totalCreditAmount").text()), 2); //应付
            var creditAmount = dataTransition.rounding(parseFloat(jQuery("#credit_amount").val() == "" ? 0 : jQuery("#credit_amount").val()), 2);
            //      扣款=应付-挂账
            jQuery("#supplier_deduction").val(dataTransition.rounding(payTotal - creditAmount, 2));
            jQuery("#creditDeductionBtn").css("display", "none");
        } else if (jQuery("#type").val() == "payDetail") { //如果是付款详细界面
            var payTotal = dataTransition.rounding(parseFloat(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text()), 2); //应付
            var creditAmount = dataTransition.rounding(parseFloat(jQuery("#creditAmount").val() == "" ? 0 : jQuery("#creditAmount").val()), 2);
            //      扣款=应付-挂账
            jQuery("#deduction").val(dataTransition.rounding(payTotal - creditAmount, 2));
            //     清除现金、银行、支票、预付款为空
            jQuery("#checkAmount,#bankCardAmount,#cash,#depositAmount").val(0); //支票
            jQuery("#creditDeductionBtn").css("display", "none");
        }


    });
    //挂账，扣款，免付弹出框close
    jQuery("#cancleCreditDeducationBtnDiv_close,#cancleCreditDeducationBtn").bind("click", function () {
        jQuery("#creditDeductionBtn").css("display", "none");

    });

    /*应付结算页面，实付blur事件*/
    jQuery("#actually_paid").bind("keyup",function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
        var totalCreditAmount = parseFloat($("#totalCreditAmount").text() == "" ? 0 : $("#totalCreditAmount").text());
        var actually_paid = parseFloat($("#actually_paid").val() == "" ? 0 : $("#actually_paid").val());
        var supplier_deduction = parseFloat($("#supplier_deduction").val() == "" ? 0 : $("#supplier_deduction").val());
        if (actually_paid >= totalCreditAmount) {
            $("#actually_paid").val(totalCreditAmount);
            $("#credit_amount").val(0);
            $("#supplier_deduction").val(0);
        } else {
            //如果扣款>0，则实付+挂账=总计-扣款
            if (supplier_deduction == 0) {
                $("#credit_amount").val(totalCreditAmount - actually_paid);
            } else if (supplier_deduction > 0) {
                $("#credit_amount").val(totalCreditAmount - supplier_deduction - actually_paid);
            }
        }
    }).bind("blur", function () {
            if ((jQuery("#actually_paid").val() == 0 || jQuery("#actually_paid").val() == "" || jQuery("#actually_paid").val() == undefined) && jQuery("#totalCreditAmount").text() > 0) {
                jQuery("#creditDeductionBtn").css("display", "block");
                jQuery("#type").val("mayPay");
            }
            var totalCreditAmount = parseFloat($("#totalCreditAmount").text() == "" ? 0 : $("#totalCreditAmount").text());
            var actually_paid = parseFloat($("#actually_paid").val() == "" ? 0 : $("#actually_paid").val());
            var supplier_deduction = parseFloat($("#supplier_deduction").val() == "" ? 0 : $("#supplier_deduction").val());
            if (actually_paid >= totalCreditAmount) {
                $("#actually_paid").val(totalCreditAmount);
                $("#credit_amount").val(0);
                $("#supplier_deduction").val(0);
            } else {
                //如果扣款=0，则实付+挂账=总计-扣款
                if (supplier_deduction == 0) {
                    $("#credit_amount").val(totalCreditAmount - actually_paid);
                } else if (supplier_deduction > 0) {
                    $("#credit_amount").val(totalCreditAmount - supplier_deduction - actually_paid);
                }
            }
        });

    /*应付结算页面，挂账blur事件*/
    jQuery("#credit_amount").bind("blur",function () {
        var totalCreditAmount = parseFloat($("#totalCreditAmount").text() == "" ? 0 : $("#totalCreditAmount").text());
        var credit_amount = parseFloat($("#credit_amount").val() == "" ? 0 : $("#credit_amount").val());
        var supplier_deduction = parseFloat($("#supplier_deduction").val() == "" ? 0 : $("#supplier_deduction").val());
        if (credit_amount >= totalCreditAmount) {
            $("#credit_amount").val(totalCreditAmount);
            $("#actually_paid").val(0);
            $("#supplier_deduction").val(0);
        } else {
            //如果扣款=0，则实付+挂账=总计-扣款
            if (supplier_deduction == 0) {
                $("#actually_paid").val(totalCreditAmount - credit_amount);
            } else if (supplier_deduction > 0) {
                $("#actually_paid").val(totalCreditAmount - supplier_deduction - credit_amount);
            }
        }
    }).bind("keyup", function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
            var totalCreditAmount = parseFloat($("#totalCreditAmount").text() == "" ? 0 : $("#totalCreditAmount").text());
            var credit_amount = parseFloat($("#credit_amount").val() == "" ? 0 : $("#credit_amount").val());
            var supplier_deduction = parseFloat($("#supplier_deduction").val() == "" ? 0 : $("#supplier_deduction").val());
            if (credit_amount >= totalCreditAmount) {
                $("#credit_amount").val(totalCreditAmount);
                $("#actually_paid").val(0);
                $("#supplier_deduction").val(0);
            } else {
                //如果扣款=0，则实付+挂账=总计-扣款
                if (supplier_deduction == 0) {
                    $("#actually_paid").val(totalCreditAmount - credit_amount);
                } else if (supplier_deduction > 0) {
                    $("#actually_paid").val(totalCreditAmount - supplier_deduction - credit_amount);
                }
            }
        });

    /*应付结算页面，扣款blur事件*/
    jQuery("#supplier_deduction").bind("blur", function () {
        var totalCreditAmount = parseFloat($("#totalCreditAmount").text() == "" ? 0 : $("#totalCreditAmount").text());
        var supplier_deduction = parseFloat($("#supplier_deduction").val() == "" ? 0 : $("#supplier_deduction").val());
        if (supplier_deduction >= totalCreditAmount) {
            $("#supplier_deduction").val(totalCreditAmount);
            $("#actually_paid").val(0);
            $("#credit_amount").val(0);
        }
    });

    /**
     * 结算按钮     Click事件
     *
     */
    jQuery("#settleAccounts").bind("click", function () {
        var lstPayAbles = new Array();
        //获得选中的应付款记录
        jQuery("[name='check']").each(function () {
            if (jQuery(this).is(':checked')) {
                var temp = {
                    "idStr": $.trim(jQuery(this).val())
                };
                if (lstPayAbles == null) {
                    lstPayAbles = [];
                }
                lstPayAbles.push(temp);
            }
        });
        if (lstPayAbles.length == 0) {
            nsDialog.jAlert("请选择单据进行结算！");
            return;
        }
        var totalCreditAmount = dataTransition.rounding(parseFloat(jQuery("#totalCreditAmount").text() == "" ? 0 : jQuery("#totalCreditAmount").text()), 2);
        var supplierId = jQuery("#supplierId").val();
        var actuallyPaid = dataTransition.rounding(parseFloat(jQuery("#actually_paid").val() == "" ? 0 : jQuery("#actually_paid").val()), 2);
        var cash = actuallyPaid;
        var creditAmount = dataTransition.rounding(parseFloat(jQuery("#credit_amount").val() == "" ? 0 : jQuery("#credit_amount").val()), 2);
        var deduction = dataTransition.rounding(parseFloat(jQuery("#supplier_deduction").val() == "" ? 0 : jQuery("#supplier_deduction").val()), 2);
        if (actuallyPaid != dataTransition.rounding(totalCreditAmount - deduction - creditAmount, 2)) {
            nsDialog.jAlert("实付金额与扣款、挂账金额不符合，请修改。");
            return;
        }
        var payableHistoryDTO = {
            "deduction": deduction,
            "creditAmount": creditAmount,
            "cash": cash,
            "bankCardAmount": 0,
            "checkAmount": 0,
            "actuallyPaid": actuallyPaid,
            "depositAmount": 0,
            "supplierId": supplierId
        };
        APP_BCGOGO.Net.asyncPost({
            url: "payable.do?method=payToSupplier",
            data: {
                lstPayAbles: JSON.stringify(lstPayAbles),
                payableHistoryDTO: JSON.stringify(payableHistoryDTO)
            },
            cache: false,
            dataType: "json",
            success: function (jsonStr) {
                if (jsonStr.message == "success") {
                    if ($("#checkPrint").attr("checked")) {
                        printPayable();
                    } else {
                        if (confirm("是否需要打印结算单？")) {
                            printPayable();
                        }
                    }
                    $("#actually_paid,#credit_amount,#supplier_deduction").val("");
                    updateCreditAmount();
                    //更新供应商详细页面，付预付款
                    updateDeposit();
                    refreshPayableTable();
                    recalculateTotal();
                    setZero();
                    nsDialog.jAlert("结算成功！");
                } else {
                    nsDialog.jAlert("结算失败！");
                }
            }
        });
    });

    // 实付，挂账，扣款双击事件
    $("#actually_paid").bind("dblclick", function () {
        $("#actually_paid").val(parseFloat($("#totalCreditAmount").html()));
        $("#credit_amount").val(0);
        $("#supplier_deduction").val(0);
    });

    $("#credit_amount").bind("dblclick", function () {
        $("#actually_paid").val(0);
        $("#credit_amount").val(parseFloat($("#totalCreditAmount").html()));
        $("#supplier_deduction").val(0);
    });

    $("#supplier_deduction").bind("dblclick", function () {
        $("#actually_paid").val(0);
        $("#credit_amount").val(0);
        $("#supplier_deduction").val(parseFloat($("#totalCreditAmount").html()));
    });

    $("input[id='name'], input[id='contact']").bind("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.stringSpaceFilter($(this).val()));
    });

    $("#startTimeStr,#endTimeStr,#fromTimeStr,#toTimeStr").datepicker({
        "numberOfMonths": 1,
        "changeYear": true,
        "changeMonth": true,
        "dateFormat": "yy-mm-dd",
        "yearRange": "c-100, c",
        "yearSuffix": "",
        "showButtonPanel": true
    });

    //时间逻辑检验
    //开始时间不能早于今天
    $("#startTimeStr").bind("change", function () {
        //所选时间的0点时刻与当前时间、结束时间对比
        var startDateStr = $("#startTimeStr").val();
        var endDateStr = $("#endTimeStr").val();
        var startDateLong;
        var endDateLong;
        if (startDateStr.length > 0) {
            var year = startDateStr.substr(0, 4);
            var month = startDateStr.substr(5, 2);
            var day = startDateStr.substr(8, 2);
            var startDate = new Date(year + "/" + month + "/" + day);
            startDateLong = startDate.getTime();
            var nowDateLong = new Date().getTime();
            if (startDateLong - nowDateLong > 0) {
                nsDialog.jAlert("开始时间不能晚于当前时间！", null, function () {
                    $("#startTimeStr").val("");
                    return;
                });
            }
            if (endDateStr.length > 0) {
                var year = endDateStr.substr(0, 4);
                var month = endDateStr.substr(5, 2);
                var day = endDateStr.substr(8, 2);
                var endDate = new Date(year + "/" + month + "/" + day);
                endDateLong = endDate.getTime();
                if (startDateLong - endDateLong > 0) {
                    nsDialog.jAlert("开始时间不能晚于结束时间！", null, function () {
                        $("#startTimeStr").val("");
                        return;
                    });
                }
            }
        }
    });
    //结束时间不能晚于开始时间
    $("#endTimeStr").bind("change", function () {
        //所选时间的0点时刻与当前时间、结束时间对比
        var startDateStr = $("#startTimeStr").val();
        var endDateStr = $("#endTimeStr").val();
        var startDateLong;
        var endDateLong;
        if (startDateStr.length > 0 && endDateStr.length > 0) {
            var year = startDateStr.substr(0, 4);
            var month = startDateStr.substr(5, 2);
            var day = startDateStr.substr(8, 2);
            var startDate = new Date(year + "/" + month + "/" + day);
            startDateLong = startDate.getTime();
            year = endDateStr.substr(0, 4);
            month = endDateStr.substr(5, 2);
            day = endDateStr.substr(8, 2);
            var endDate = new Date(year + "/" + month + "/" + day);
            endDateLong = endDate.getTime();
            if (startDateLong - endDateLong > 0) {
                nsDialog.jAlert("结束时间不能早于开始时间！", null, function () {
                    $("#endTimeStr").val("");
                    return;
                });
            }
        }
    });

    $("#fromTimeStr").bind("change", function () {
        //所选时间的0点时刻与当前时间、结束时间对比
        var startDateStr = $("#fromTimeStr").val();
        var endDateStr = $("#toTimeStr").val();
        var startDateLong;
        var endDateLong;
        if (startDateStr.length > 0) {
            var year = startDateStr.substr(0, 4);
            var month = startDateStr.substr(5, 2);
            var day = startDateStr.substr(8, 2);
            var startDate = new Date(year + "/" + month + "/" + day);
            startDateLong = startDate.getTime();
            var nowDateLong = new Date().getTime();
            if (startDateLong - nowDateLong > 0) {
                nsDialog.jAlert("开始时间不能晚于当前时间！", null, function () {
                    $("#fromTimeStr").val("");
                    return;
                });
            }
            if (endDateStr.length > 0) {
                var year = endDateStr.substr(0, 4);
                var month = endDateStr.substr(5, 2);
                var day = endDateStr.substr(8, 2);
                var endDate = new Date(year + "/" + month + "/" + day);
                endDateLong = endDate.getTime();
                if (startDateLong - endDateLong > 0) {
                    nsDialog.jAlert("开始时间不能晚于结束时间！", null, function () {
                        $("#fromTimeStr").val("");
                        return;
                    });
                }
            }
        }
    });
    //结束时间不能晚于开始时间
    $("#toTimeStr").bind("change", function () {
        //所选时间的0点时刻与当前时间、结束时间对比
        var startDateStr = $("#fromTimeStr").val();
        var endDateStr = $("#toTimeStr").val();
        var startDateLong;
        var endDateLong;
        if (startDateStr.length > 0 && endDateStr.length > 0) {
            var year = startDateStr.substr(0, 4);
            var month = startDateStr.substr(5, 2);
            var day = startDateStr.substr(8, 2);
            var startDate = new Date(year + "/" + month + "/" + day);
            startDateLong = startDate.getTime();
            year = endDateStr.substr(0, 4);
            month = endDateStr.substr(5, 2);
            day = endDateStr.substr(8, 2);
            var endDate = new Date(year + "/" + month + "/" + day);
            endDateLong = endDate.getTime();
            if (startDateLong - endDateLong > 0) {
                nsDialog.jAlert("结束时间不能早于开始时间！", null, function () {
                    $("#toTimeStr").val("");
                    return;
                });
            }
        }
    });

    //历史单据查询
    $("#orderSearchBtn").bind("click", function () {
        searchSupplierOrderHistory();
    });

    $("#searchOrder").click(function(){
        var supplierName = jQuery("#customerOrSupplierName").val();
        var startTimeStr = jQuery("#startTimeStr").val();
        var endTimeStr = jQuery("#endTimeStr").val();
        var orderType = "";
        var chks = document.getElementsByName("orderType");
        for (var i = 0; i < chks.length; i++) {
            if (chks[i].checked == true) {
                orderType = orderType + "," + chks[i].value;
            }
        }
        var url = "inquiryCenter.do?method=inquiryCenterIndex&pageType=customerOrSupplier&startDateStr=&customerOrSupplier=" + supplierName + "&startDateStr=" + startTimeStr + "&endDateStr=" + endTimeStr + "&orderTypes=" + orderType;
        window.open(url,"_blank")
    });



    $(".divTit :checkbox[name=businessScope]").click(function () {
        var currentVal = $(this).val();
        var isChecked = $(this).attr("checked");
        $(".divTit :checkbox[name=businessScope]").each(function (index, checkbox) {
            if ($(checkbox).val() == currentVal) {
                $(checkbox).attr("checked", isChecked);
            }
        });
    });

    $(".single_contact input[id$='mobile']").live("blur", function(){
        if(this.value != '') {
           check.contactMobileSupplier(this);
        }
    });
    $("#landLine").live("blur", function(){
        if(this.value != '') {
           check.inputTelephone(this);
        }
    });
    $("#landLineSecond").live("blur", function(){
        if(this.value != '') {
            check.inputTelephone(this);
        }
    });
    $("#landLineThird").live("blur", function(){
        if(this.value != '') {
            check.inputTelephone(this);
        }
    });
});


function clearCustomerDepositAddData(){
  jQuery(".tabTotal :text").val(""); //输入清空
  jQuery(".i_upBody :text").val("");
  jQuery(".productDetails :text").val("");
  jQuery("#checkNoDeposit").val($("#checkNoDeposit").attr("initValue")).css("color","#9a9a9a");
  jQuery("[name='check']").removeAttr("checked");
  jQuery("#checkAll").removeAttr("checked");
  jQuery("[name='print']").removeAttr("checked");
  jQuery("#totalCreditAmount").text(0);
  jQuery("#depositDiv").css("display", "none");
  jQuery("#payable,#deposit,#payDetail").css("display", "none");
  jQuery("#mask").css("display", "none");
  jQuery("#depositMemo").val("");
  jQuery("#actuallyPaidDeposit").text("0");
  try {
      $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
  } catch (e) {
      ;
  }
}

function setSupplierAreaInfo(){
    provinceBind();
    $("#province").bind("change", function () {
        cityBind(this);
    });
    $("#city").bind("change", function () {
        townshipBind(this);
    });
    $("#province").val($("#select_provinceInput").val());
    $("#province").change();
    $("#city").val($("#select_cityInput").val());
    $("#city").change();
    $("#region").val($("#select_regionInput").val());
}

/**
 * 应付结算弹出框初始化
 */
function initPayableTable(jsonStr) {
    var dialogHeight = 550; //dialogHeight用于动态控制弹出框高度
    $("#totalCreditAmount").text(0);
    $("#checkAll").removeAttr("checked");
    $("#history tr:not(:first)").remove();
    jsonStr = jsonStr[0].payables;
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length; i++) {
            var idStr = jsonStr[i].idStr == null ? " " : jsonStr[i].idStr;
            var receiptNo = jsonStr[i].receiptNo == null ? " " : jsonStr[i].receiptNo;
            var purchaseInventoryIdStr = jsonStr[i].purchaseInventoryIdStr == null ? " " : jsonStr[i].purchaseInventoryIdStr;
            var payTimeStr = jsonStr[i].payTimeStr == null ? " " : jsonStr[i].payTimeStr; //todo 入库时间？
            var materialName = jsonStr[i].materialName == null ? " " : jsonStr[i].materialName;
            var amount = jsonStr[i].amount == null ? " " : jsonStr[i].amount;
            var paidAmount = jsonStr[i].paidAmount == null ? " " : jsonStr[i].paidAmount;
            var creditAmount = jsonStr[i].creditAmount == null ? " " : jsonStr[i].creditAmount;
            var tr = '<tr class="table-row-original">';
            tr += '<td><input type="checkbox"  name="check" class="check" value=" ' + idStr + '" id=check' + (i + 1) + ' /></td>';
            tr += '<input type="hidden" id="payableId' + i + '" value="' + idStr + '" name="payableId"/></td>';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td>' + '<a href ="#" style="color:#005DB7" onclick="openInventory(\'' + purchaseInventoryIdStr + '\')">' + receiptNo + '</a> ' + '</td> ';
            tr += '<td>' + payTimeStr + '</td>';
            tr += '<td title=\'' + materialName + '\'>' + materialName.substring(0, 25) + '</td>';
            tr += '<td title=\'' + amount + '\'>' + amount + '</td>';
            tr += '<td title=\'' + paidAmount + '\'>' + paidAmount + '</td>';
            tr += '<td title=\'' + creditAmount + '\'>' + creditAmount + '</td>';
            tr += '</tr>';
            $("#history").append($(tr));
            dialogHeight += 70;
        }
    }
    window.parent.document.getElementById("iframe_PopupBox_1").style.height = dialogHeight + "px";
}

function openInventory(idStr) {
    window.open('storage.do?method=getPurchaseInventory&purchaseInventoryId=' + idStr + '&type=txn&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE');
}
function openInventoryReturn(idStr) {
    window.open('goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=' + idStr);
}

//付款历史记录分页显示初始化
function initPayableHistoryRecord(jsonStr) {
    jQuery("#pay_history_record tr:not(:first)").remove();
    if (jsonStr.length > 0) {
//        jQuery("#pay_history_record").append($('<tr class="space"><td colspan="16"></td></tr>'));
        for (var i = 0; i < jsonStr.length - 1; i++) {
            var creditAmount = jsonStr[i].creditAmount == null ? " " : dataTransition.rounding(jsonStr[i].creditAmount, 2);
            var cash = jsonStr[i].amount == null ? " " : dataTransition.rounding(jsonStr[i].cash, 2);
            var bankCardAmount = jsonStr[i].bankCardAmount == null ? " " : dataTransition.rounding(jsonStr[i].bankCardAmount, 2);
            var checkAmount = jsonStr[i].checkAmount == null ? " " : dataTransition.rounding(jsonStr[i].checkAmount, 2);
            var checkNo = jsonStr[i].checkNo == null ? " " : jsonStr[i].checkNo;
            var depositAmount = jsonStr[i].depositAmount == null ? " " : dataTransition.rounding(jsonStr[i].depositAmount, 2);
            var actuallyPaid = jsonStr[i].actuallyPaid == null ? " " : dataTransition.rounding(jsonStr[i].actuallyPaid, 2);
            var purchaseInventoryIdStr = jsonStr[i].purchaseInventoryIdStr == null ? " " : jsonStr[i].purchaseInventoryIdStr;
            var receiptNo = jsonStr[i].receiptNo == null ? " " : jsonStr[i].receiptNo;
            var materialName = jsonStr[i].materialName == null ? " " : jsonStr[i].materialName;
            var amount = jsonStr[i].amount == null ? " " : dataTransition.rounding(jsonStr[i].amount, 2);
            var paidAmount = jsonStr[i].paidAmount == null ? " " : dataTransition.rounding(jsonStr[i].paidAmount, 2);
            var deduction = jsonStr[i].deduction == null ? " " : dataTransition.rounding(jsonStr[i].deduction, 2);
            var paidTimeStr = jsonStr[i].paidTimeStr == null ? " " : jsonStr[i].paidTimeStr;
            var orderType = jsonStr[i].orderType == null ? " " : jsonStr[i].orderType;
            var status = jsonStr[i].status == "REPEAL" ? "已作废" : "可用";
            var tr = '<tr class="titBody_Bg">';
            tr += '<td style="padding-left:10px;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td>' + paidTimeStr + '</td>';

            if (orderType == "INVENTORY") {
                tr += '<td>' + '<a href ="#" class="blue_color" onclick="openInventory(\'' + purchaseInventoryIdStr + '\')">' + receiptNo + '</a> ' + '</td> ';
            } else if (orderType = "RETURN") {
                tr += '<td>' + '<a href ="#" class="blue_color" onclick="openInventoryReturn(\'' + purchaseInventoryIdStr + '\')">' + receiptNo + '</a> ' + '</td> ';
            } else {
                tr += '<td title=  \'' + receiptNo + '\'><a class="blue_color">' + receiptNo + '</a></td>';
            }
            var orderTypeStr = APP_BCGOGO.OrderTypes[orderType];
            tr += '<td title=  \'' + orderTypeStr + '\'>' + orderTypeStr + '</td>';
            tr += '<td title=  \'' + amount + '\'>' + amount + '</td>';
            tr += '<td title=  \'' + paidAmount + '\'>' + paidAmount + '</td>';
            tr += '<td title=  \'' + cash + '\'>' + cash + '</td>';
            tr += '<td title=  \'' + bankCardAmount + '\'>' + bankCardAmount + '</td>';
            tr += '<td title=  \'' + checkAmount + '\'>' + checkAmount + '</td>';
            tr += '<td title=  \'' + depositAmount + '\'>' + depositAmount + '</td>';
            tr += '<td title=  \'' + deduction + '\'>' + deduction + '</td>';
            tr += '<td title=  \'' + checkNo + '\'>' + checkNo + '</td>';
            tr += '<td title=  \'' + actuallyPaid + '\'>' + actuallyPaid + '</td>';
            tr += '<td title=  \'' + creditAmount + '\'>' + creditAmount + '</td>';
            tr += '<td title=  \'' + status + '\'>' + status + '</td>';
            tr += '</tr >';
            tr += '<tr class="titBottom_Bg"><td colspan="16"></td></tr>';
            jQuery("#pay_history_record").append(jQuery(tr));
        }
        jQuery("#pay_history_record").append(jQuery('<tr class="titBottom_Bg"><td colspan="16"></td></tr>'));
    }
}


//单据历史记录分页显示初始化
function initSupplierOrderHistory(json) {
    var jsonStr = json[0][0];
    $("#supplierOrderHistoryTab tr:not(:first)").remove();
    if (jsonStr && jsonStr.length > 0) {
        $("#supplierOrderHistoryTab").append($('<tr class="space"><td colspan="8"></td></tr>'));
        for (var i = 0; i < jsonStr.length; i++) {
            var consumeDateStr = jsonStr[i].consumeDateStr == null ? "" : jsonStr[i].consumeDateStr;
            var receiptNo = jsonStr[i].receiptNo == null ? "" : jsonStr[i].receiptNo;
            var orderType = jsonStr[i].orderType == null ? "" : jsonStr[i].orderType;
            if (orderType == "PURCHASE") {
                orderType = "采购单";
            } else if (orderType == "INVENTORY") {
                orderType = "入库单";
            } else if (orderType == "RETURN") {
                orderType = "入库退货单";
            }
            var productAmount = jsonStr[i].productAmount == null ? "" : jsonStr[i].productAmount;
            var totalMoney = jsonStr[i].totalMoney == null ? "" : dataTransition.rounding(jsonStr[i].totalMoney, 2);
            var material = jsonStr[i].material == null ? "" : jsonStr[i].material;
            if (material.length > 32) {
                material = material.substr(0, 30) + "..."
            }
            var url = jsonStr[i].url == null ? "" : jsonStr[i].url;
            var tr = '<tr class="titBody_Bg">';
            tr += '<td style="padding-left:10px;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td>' + consumeDateStr + '</td>';
            tr += '<td>' + receiptNo + '</td>';
            tr += '<td>' + orderType + '</td>';
            tr += '<td>' + material + '</td>';
            tr += '<td>' + productAmount + '</td>';
            tr += '<td>' + totalMoney + '</td>';
            tr += '<td><a class="blue_color" href="' + url + '">点击详情</a></td>';
            tr += '</tr >';
            tr += '<tr class="titBottom_Bg"><td colspan="8"></td></tr>';
            $("#supplierOrderHistoryTab").append($(tr));
        }
        $("#supplierOrderHistoryTab").append($('<tr class="titBottom_Bg"><td colspan="8"></td></tr>'));
        $("#totalMoneyInfo").text(json[0][1]);
    } else {
        $("#totalMoneyInfo").text("0 元");
    }
}


function recalculateTotal() {
    //获得选中的应付款记录
    var totalCreadit = 0;
    var count = 0;
    jQuery("[name='check']").each(function () {
        if (jQuery(this).is(':checked')) {
            var temp = jQuery(this).parent().parent().children('td').eq(7).html();
            if (temp == undefined || temp == null || temp == "") temp = 0;
            totalCreadit = totalCreadit + parseFloat(temp);
            count = count + 1;
        }
    });
    if (count == $("[name='check']").size()) {
        jQuery("#checkAll").attr("checked", 'true'); //
    } else {
        jQuery("#checkAll").removeAttr("checked"); //
    }
    jQuery("#totalCreditAmount").text(totalCreadit);
    jQuery("#actually_paid").val(totalCreadit);
    $("#credit_amount").val(0);
    $("#supplier_deduction").val(0);
}

/**
 * 结算详细弹出框初始化
 */

function initPayDetail() {
    //modified by WeiLingfeng 实付、挂账、扣款带入详细结算弹出框
    $("#actuallyPaid").val($("#actually_paid").val());
    $("#creditAmount").val($("#credit_amount").val());
    $("#deduction").val($("#supplier_deduction").val());
    $("#depositAmount").val(0);
    //结算详细显示应付
    jQuery("#pay_total").text(jQuery("#totalCreditAmount").text());
    //预付款
    jQuery("#deposit_avaiable").text(dataTransition.rounding(jQuery("#hiddenDeposit").val(), 2));

    var payTotal = dataTransition.rounding(parseFloat(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text()), 2); //应付
    var deduction = dataTransition.rounding(parseFloat(jQuery("#deduction").val() == "" ? 0 : jQuery("#deduction").val()), 2); //扣款
    var creditAmount = dataTransition.rounding(parseFloat(jQuery("#creditAmount").val() == "" ? 0 : jQuery("#creditAmount").val()), 2); //挂账
    var cash = dataTransition.rounding(parseFloat(jQuery("#cash").val() == "" ? 0 : jQuery("#cash").val()), 2); //现金
    var bankCardAmount = dataTransition.rounding(parseFloat(jQuery("#bankCardAmount").val() == "" ? 0 : jQuery("#bankCardAmount").val()), 2); //银行卡
    var checkAmount = dataTransition.rounding(parseFloat(jQuery("#checkAmount").val() == "" ? 0 : jQuery("#checkAmount").val()), 2); //支票
    var depositAmount = dataTransition.rounding(parseFloat(jQuery("#depositAmount").val() == "" ? 0 : jQuery("#depositAmount").val()), 2); //预付款
    //如果应付为0，输入框全部disable
    if (payTotal == 0) {
        jQuery("#payDetail input:text").attr("disabled", true);
        return;
    }
    //  现金：=应付-扣款-挂账
    jQuery("#cash").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2));
    cash = dataTransition.rounding(payTotal - deduction - creditAmount, 2);
    //实付：=现金+银行+支票+用预付款，为空情况下要显示0
    jQuery("#actuallyPaid").val(dataTransition.rounding(cash + bankCardAmount + checkAmount + depositAmount, 2));

}

/**
 *     扣款修改JS控制
 *判断： 扣款>应付，则自动修改为扣款=应付；
 *扣款=应付，则清除： 挂账、现金、银行、支票、用预付款，实付=0
 *扣款<应付，
 *挂账：非空，则挂账= 挂账与(应付-扣款)相比,取小值
 *付款处理：
 *判断：（现金+银行+支票+预付款=0，全部为空）
 *则现金=应付-扣款-挂账
 * 判断：（现金+银行+支票+预付款）>0，部分或全部有数值)
 *现金：非空，则现金=应付-扣款-挂账，清除银、支、定为0
 *银行：非空，则银行=应付-扣款-挂账，清除现、支、定为0
 *支票：非空，则支票=应付-扣款-挂账，清除现、银、定为0
 *预付款：非空，则预付款=应付-扣款-挂账，清空现、银、支为0
 */

function editDeduction() {
    var payTotal = dataTransition.rounding(parseFloat(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text()), 2); //应付
    var deduction = dataTransition.rounding(parseFloat(jQuery("#deduction").val() == "" ? 0 : jQuery("#deduction").val()), 2); //扣款
    var creditAmount = dataTransition.rounding(parseFloat(jQuery("#creditAmount").val() == "" ? 0 : jQuery("#creditAmount").val()), 2); //挂账
    var cash = dataTransition.rounding(parseFloat(jQuery("#cash").val() == "" ? 0 : jQuery("#cash").val()), 2); //现金
    var bankCardAmount = dataTransition.rounding(parseFloat(jQuery("#bankCardAmount").val() == "" ? 0 : jQuery("#bankCardAmount").val()), 2); //银行卡
    var checkAmount = dataTransition.rounding(parseFloat(jQuery("#checkAmount").val() == "" ? 0 : jQuery("#checkAmount").val()), 2); //支票
    var depositAmount = dataTransition.rounding(parseFloat(jQuery("#depositAmount").val() == "" ? 0 : jQuery("#depositAmount").val()), 2); //预付款
    if (deduction >= payTotal) {
        jQuery("#deduction").val(payTotal);
        //挂账      //现金    //银行卡   //支票  //预付款清空
        jQuery("#creditAmount,#cash,#bankCardAmount,#checkAmount,#depositAmount,#depositAmount,#actuallyPaid").val(0);
    } else if (deduction < payTotal) {
        if (creditAmount > 0) {
            var temp = dataTransition.rounding(payTotal - deduction, 2);
            if (creditAmount > temp) {
                jQuery("#creditAmount").val(temp);
                creditAmount = temp;
            }
        }
        //：（现金+银行+支票+预付款=0，全部为空）
        if (dataTransition.rounding(cash + bankCardAmount + checkAmount + depositAmount, 2) == 0) {
            //则现金=应付-扣款-挂账
            jQuery("#cash").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2));
            jQuery("#actuallyPaid").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2));
        } else {
            //               现金：非空，则现金=应付-扣款-挂账，清除银、支、定为0
            if (cash > 0) {
                jQuery("#cash").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2));
                jQuery("#actuallyPaid").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2));
                //银行卡         //支票      //预付款
                jQuery("#bankCardAmount,#checkAmount,#depositAmount").val(0);
                //银行：非空，则银行=应付-扣款-挂账，清除现、支、定为0
            } else if (bankCardAmount > 0) {
                jQuery("#bankCardAmount").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2)); //银行卡
                jQuery("#actuallyPaid").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2));
                //现金     //支票         //预付款
                jQuery("#cash,#checkAmount,#depositAmount").val(0);
                //支票：非空，则支票=应付-扣款-挂账，清除现、银、定为0
            } else if (checkAmount > 0) {
                jQuery("#checkAmount").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2)); //支票
                jQuery("#actuallyPaid").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2));
                //银行卡 //现金  //预付款
                jQuery("#bankCardAmount,#cash,#depositAmount").val(0); //银行卡
                //预付款：非空，则预付款=应付-扣款-挂账，清空现、银、支为0
            }
            //            else {
            //                jQuery("#depositAmount").val(payTotal - deduction - creditAmount);            //预付款
            //                jQuery("#actuallyPaid").val(payTotal - deduction - creditAmount);
            //                jQuery("#checkAmount,#bankCardAmount,#cash").val(0);
            //            }
        }
    }

}
/**
 * 修改挂账
 * 判断： 挂账>应付，则自动修改为挂账=应付；
 *挂账=应付，则清除：挂账、现金、银行、支票为空，实收=0
 *挂账<应付，
 *扣款：非空，则扣款= 扣款与(应付-挂账)相比,取小值
 *付款处理：
 *判断：（现金+银行+支票+预付款=0，全部为空）
 *则现金=应付-扣款-挂账
 *判断：（现金+银行+支票+预付款）>0，部分或全部有数值)
 *现金：非空，则现金=应付-扣款-挂账，清除银、支、定为0
 *银行：非空，则银行=应付-扣款-挂账，清除现、支、定为0
 *支票：非空，则支票=应付-扣款-挂账，清除现、银、定为0
 *预付款：非空，则预付款=应付-扣款-挂账，清空现、银、支为0
 */

function editCreditAmount() {
    var payTotal = dataTransition.rounding(parseFloat(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text()), 2); //应付
    var deduction = dataTransition.rounding(parseFloat(jQuery("#deduction").val() == "" ? 0 : jQuery("#deduction").val()), 2); //扣款
    var creditAmount = dataTransition.rounding(parseFloat(jQuery("#creditAmount").val() == "" ? 0 : jQuery("#creditAmount").val()), 2); //挂账
    var cash = dataTransition.rounding(parseFloat(jQuery("#cash").val() == "" ? 0 : jQuery("#cash").val()), 2); //现金
    var bankCardAmount = dataTransition.rounding(parseFloat(jQuery("#bankCardAmount").val() == "" ? 0 : jQuery("#bankCardAmount").val()), 2); //银行卡
    var checkAmount = dataTransition.rounding(parseFloat(jQuery("#checkAmount").val() == "" ? 0 : jQuery("#checkAmount").val()), 2); //支票
    var depositAmount = dataTransition.rounding(parseFloat(jQuery("#depositAmount").val() == "" ? 0 : jQuery("#depositAmount").val()), 2); //预付款
    // 挂账>应付，则自动修改为挂账=应付；
    if (creditAmount >= payTotal) {
        //挂账=应付，则清除：挂账、现金、银行、支票为空，实收=0
        jQuery("#creditAmount").val(payTotal);
        jQuery("#deduction,#cash,#bankCardAmount,#checkAmount,#depositAmount,#actuallyPaid").val(0); //扣款            //现金     //银行卡         //支票       //预付款
        //挂账<应付
    } else if (creditAmount < payTotal) {
        if (deduction > 0) {
            var temp = dataTransition.rounding(payTotal - creditAmount, 2);
            //则扣款= 扣款与(应付-挂账)相比,取小值
            if (deduction > temp) {
                jQuery("#deduction").val(temp);
                deduction = temp;
            }
        }
        //付款处理：
        if (dataTransition.rounding(cash + bankCardAmount + checkAmount + depositAmount, 2) == 0) {
            jQuery("#cash").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2));
        } else {
            //               现金：非空，则现金=应付-扣款-挂账，清除银、支、定为0
            if (cash > 0) {
                jQuery("#cash").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2)); //现金
                jQuery("#actuallyPaid").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2));
                jQuery("#bankCardAmount,#checkAmount,#depositAmount").val(0);
                //银行：非空，则银行=应付-扣款-挂账，清除现、支、定为0
            } else if (bankCardAmount > 0) {
                jQuery("#bankCardAmount").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2)); //银行卡
                jQuery("#actuallyPaid").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2));
                jQuery("#cash,#checkAmount,#depositAmount").val(0);
                //支票：非空，则支票=应付-扣款-挂账，清除现、银、定为0
            } else if (checkAmount > 0) {
                jQuery("#checkAmount").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2)); //支票
                jQuery("#actuallyPaid").val(dataTransition.rounding(payTotal - deduction - creditAmount, 2));
                jQuery("#bankCardAmount,#cash,#depositAmount").val(0);
                //预付款：非空，则预付款=应付-扣款-挂账，清空现、银、支为0
            } else {
                jQuery("#depositAmount").val(payTotal - deduction - creditAmount); //预付款
                jQuery("#actuallyPaid").val(payTotal - deduction - creditAmount);
                jQuery("#checkAmount,#bankCardAmount,#cash").val(0);
            }
        }
    }
}

//function editPaidAmount() {
//    var cash = dataTransition.rounding(parseFloat(jQuery("#cash").val() == "" ? 0 : jQuery("#cash").val()), 2);         //现金
//    var bankCardAmount = dataTransition.rounding(parseFloat(jQuery("#bankCardAmount").val() == "" ? 0 : jQuery("#bankCardAmount").val()), 2);        //银行卡
//    var checkAmount = dataTransition.rounding(parseFloat(jQuery("#checkAmount").val() == "" ? 0 : jQuery("#checkAmount").val()), 2);                  //支票
//    var depositAmount = dataTransition.rounding(parseFloat(jQuery("#depositAmount").val() == "" ? 0 : jQuery("#depositAmount").val()), 2);            //预付款
//    jQuery("#actuallyPaid").val(dataTransition.rounding(cash + bankCardAmount + checkAmount + depositAmount, 2));
//}
/**
 * 修改预付款
 *
 */

function editDepositAmount() {
    var cash = dataTransition.rounding(parseFloat(jQuery("#cash").val() == "" ? 0 : jQuery("#cash").val()), 2); //现金
    var bankCardAmount = dataTransition.rounding(parseFloat(jQuery("#bankCardAmount").val() == "" ? 0 : jQuery("#bankCardAmount").val()), 2); //银行卡
    var checkAmount = dataTransition.rounding(parseFloat(jQuery("#checkAmount").val() == "" ? 0 : jQuery("#checkAmount").val()), 2); //支票
    var depositAmount = dataTransition.rounding(parseFloat(jQuery("#depositAmount").val() == "" ? 0 : jQuery("#depositAmount").val()), 2); //预付款
    var depositAvaiable = dataTransition.rounding(parseFloat(jQuery("#deposit_avaiable").text() == "" ? 0 : jQuery("#deposit_avaiable").text()), 2);
    if (depositAvaiable < depositAmount) {
        nsDialog.jAlert("可用预付款不足！请重新输入！");
        $("#depositAmount").val(0);
        $("#actuallyPaid").val(0);
        return;
    }
    jQuery("#actuallyPaid").val(dataTransition.rounding(cash + bankCardAmount + checkAmount + depositAmount, 2));
}
/**
 * 修改实付
 */

function editActuallyPaid() {
    if (jQuery("#actuallyPaid").val() == 0) {
        jQuery("#creditDeductionBtn").css("display", "block");
        jQuery("#type").val("payDetail");
    }
}
/**
 *    双击扣款
 */

function dblclickDeduction() {
    jQuery("#deduction").val(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text());
    //     清除现金、银行、支票、预付款为空
    jQuery("#checkAmount,#bankCardAmount,#cash,#depositAmount,#creditAmount").val(0);
    editDeduction();
}
/**
 *  双击挂账
 */

function dblclickCreditAmount() {
    jQuery("#creditAmount").val(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text());
    //     清除现金、银行、支票、预付款为空
    jQuery("#deduction,#checkAmount,#bankCardAmount,#cash,#depositAmount").val(0);
    editCreditAmount();
}
/**
 * 双击现金
 */

function dblclickcash() {
    jQuery("#cash").val(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text());
    //     清除现金、银行、支票、预付款为空
    jQuery("#deduction,#checkAmount,#bankCardAmount,#depositAmount,#creditAmount").val(0);
    editPaidAmount();
}
/**
 * 双击银行卡
 */

function dblclickBankCardAmount() {
    jQuery("#bankCardAmount").val(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text());
    //     清除现金、银行、支票、预付款为空
    jQuery("#deduction,#checkAmount,#cash,#depositAmount,#creditAmount").val(0);
    editPaidAmount();
}
/**
 * 双击支票
 *
 */

function dblclickCheckAmount() {
    jQuery("#checkAmount").val(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text());
    //     清除现金、银行、支票、预付款为空
    jQuery("#deduction,#bankCardAmount,#cash,#depositAmount,#creditAmount").val(0);
    editPaidAmount();
}
/**
 * 双击用户预付款
 */

function dblclickDepositAmount() {
    jQuery("#depositAmount").val(jQuery("#pay_total").text() == "" ? 0 : jQuery("#pay_total").text());
    //     清除现金、银行、支票、预付款为空
    jQuery("#deduction,#bankCardAmount,#cash,#checkAmount,#creditAmount").val(0);
    editDepositAmount();
}


//刷新应付款表
function refreshPayableTable() {
    var supplierId = jQuery("#supplierId").val();
    APP_BCGOGO.Net.syncPost({
        url: "payable.do?method=searchPayable",
        data: {
            startPageNo: 1,
            supplierId: supplierId
        },
        cache: false,
        dataType: "json",
        success: function (jsonStr) {
            initPayableTable(jsonStr);
            initPages(jsonStr, "dynamical3", "payable.do?method=searchPayable", '', "initPayableTable", '', '', {
                startPageNo: 1,
                supplierId: supplierId
            }, '');
            $("#totalCountPayable").html(jsonStr[jsonStr.length - 1].totalRows);
        }
    });
}


//刷新单据历史记录
function searchSupplierOrderHistory() {
    var supplierId = jQuery("#supplierId").val();
    var startTimeStr = jQuery("#startTimeStr").val();
    var endTimeStr = jQuery("#endTimeStr").val();
    var orderType = "";
    var chks = document.getElementsByName("orderType");
    for (var i = 0; i < chks.length; i++) {
        if (chks[i].checked == true) {
            orderType = orderType + "," + chks[i].value;
        }
    }
    APP_BCGOGO.Net.asyncPost({
        url: "unitlink.do?method=getSupplierOrderHistory",
        data: {
            startPageNo: 1,
            supplierId: supplierId,
            startTimeStr: startTimeStr,
            endTimeStr: endTimeStr,
            orderType: orderType
        },
        cache: false,
        dataType: "json",
        success: function (jsonStr) {
            initSupplierOrderHistory(jsonStr);
            initPages(jsonStr, "dynamical1", "unitlink.do?method=getSupplierOrderHistory", '', "initSupplierOrderHistory", '', '', {
                startPageNo: 1,
                supplierId: supplierId,
                startTimeStr: startTimeStr,
                endTimeStr: endTimeStr,
                orderType: orderType
            }, '');
        }
    });
}


//应付预付款刷新
function updateCreditAmount() {
    var supplierId = jQuery("#supplierId").val();
    APP_BCGOGO.Net.asyncPost({
        url: "payable.do?method=getCreditAmountBySupplierId",
        data: {
            supplierId: supplierId
        },
        cache: false,
        dataType: "json",
        success: function (jsonStr) {
            jQuery("#btnPay").val("").val("应付:" + jsonStr.message + "元")
        }
    });
}
//预付款刷新


function updateDeposit() {
    var supplierId = jQuery("#supplierId").val();
    APP_BCGOGO.Net.asyncPost({
        url: "payable.do?method=getSumDepositBySupplierId",
        data: {
            supplierId: supplierId
        },
        cache: false,
        dataType: "json",
        success: function (jsonStr) {
            jQuery("#btnPayed").val("").val("付预付款:" + jsonStr.message + "元")
            jQuery("#hiddenDeposit").val("").val(jsonStr.message);
        }
    });
}

$(document).ready(function () {
    //预付款结算打印
    $("#depositPrintBtn").live('click', function () {
        var supplierId = $("#supplierId").val(); //供应商ID
        var cashDeposit = parseFloat($("#cashDeposit").val() == "" ? "0" : $("#cashDeposit").val()); //现金
        var bankCardAmountDeposit = parseFloat($("#bankCardAmountDeposit").val() == "" ? "0" : $("#bankCardAmountDeposit").val()); //银行卡
        var checkAmountDeposit = parseFloat($("#checkAmountDeposit").val() == "" ? "0" : $("#checkAmountDeposit").val()); //支票
        var checkNoDeposit = $("#checkNoDeposit").val(); //号码
        var actuallyPaidDeposit = parseFloat($("#actuallyPaidDeposit").val() == "" ? "0" : $("#actuallyPaidDeposit").val()); //实付
        //打印单显示
        window.open("payable.do?method=printDeposit&supplierId=" + supplierId + "&cashDeposit=" + cashDeposit + "&bankCardAmountDeposit=" + bankCardAmountDeposit + "&checkAmountDeposit=" + checkAmountDeposit + "&checkNoDeposit=" + checkNoDeposit + "&actuallyPaidDeposit=" + actuallyPaidDeposit, "", "dialogWidth=1024px;dialogHeight=768px");
    });
});

function printPayable() {
    var supplierId = $("#supplierId").val(); //供应商ID
    var totalAmount = parseFloat($("#totalCreditAmount").text() == "" ? "0" : $("#totalCreditAmount").text()); //总金额
    var payedAmount = parseFloat($("#actually_paid").val() == "" ? "0" : $("#actually_paid").val()); //实付金额
    var deduction = parseFloat($("#supplier_deduction").val() == "" ? "0" : $("#supplier_deduction").val()); //扣款
    var creditAmount = parseFloat($("#credit_amount").val() == "" ? "0" : $("#credit_amount").val()); //挂账
    //详细结算页面，会有以下4个参数
    var cash = parseFloat($("#cash").val() == "" ? "0" : $("#cash").val()); //现金
    var bankCardAmount = parseFloat($("#bankCardAmount").val() == "" ? "0" : $("#bankCardAmount").val()); //银行卡
    var checkAmount = parseFloat($("#checkAmount").val() == "" ? "0" : $("#checkAmount").val()); //支票
    var depositAmount = parseFloat($("#depositAmount").val() == "" ? "0" : $("#depositAmount").val()); //预付款
    var payableId = ""; //单据ID字符串
    //要结算的单据数量
    var checkSize = $(".check").size();
    //单据ID组合字符串，材料名+数量
    for (var i = 0; i < checkSize; i++) {
        if ($($(".check").get(i)).attr("checked") == true) {
            payableId = payableId + "," + $($(".check").get(i)).next().val();
        }
    }
    //打印单显示
    window.open("payable.do?method=printPayable&supplierId=" + supplierId + "&payableId=" + payableId + "&totalAmount=" + totalAmount + "&payedAmount=" + payedAmount + "&deduction=" + deduction + "&creditAmount=" + creditAmount + "&cash=" + cash + "&bankCardAmount=" + bankCardAmount + "&checkAmount=" + checkAmount + "&depositAmount=" + depositAmount, "", "dialogWidth=1024px;dialogHeight=768px");
}

/*每次结算后，页面金额清零，避免影响以后的打印结果*/

function setZero() {
    //父页面
    $("#actually_paid").val(0);
    $("#credit_amount").val(0);
    $("#supplier_deduction").val(0);
    //弹出框
    $("#deduction").val(0);
    $("#creditAmount").val(0);
    $("#cash").val(0);
    $("#bankCardAmount").val(0);
    $("#checkAmount").val(0);
    $("#depositAmount").val(0);
    $("#actuallyPaid").val(0);
}

function openPurchaseOrder(purchaseId) {
    window.open('RFbuy.do?method=show&id=' + String(purchaseId));
}

/**
 * 在uncleUser.js 有一份拷贝，修改的时候请注意同步
 * @param customerId
 * @returns {boolean}
 */
function validateDeleteCustomer(customerId){
    var flag = true;
    $.ajax({
        type: "POST",
        url: "customer.do?method=validateDeleteCustomer",
        async: false,
        data: {
            customerId: customerId,
            tsLog: 10000000000 * (1 + Math.random())
        },
        cache: false,
        dataType: "json",
        success: function (result) {
            if (result && !result.success) {
                flag = false;
                var orders = result.data;
                var html ="<div>";
                var isHaveUnsettledOrders = false;
                if(orders["repair"] && orders["repair"].length>0){
                    var repairOrders = orders["repair"];
                    isHaveUnsettledOrders = true;
                    for (var i = 0, len = repairOrders.length; i < len; i++) {
                        var url = "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + repairOrders[i].idStr;
                        html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + url + "'>"
                            + repairOrders[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
                        if (i % 2 == 1) {
                            html += "<br/>";
                        }
                    }
                }
                if(orders["sale"] && orders["sale"].length>0){
                    isHaveUnsettledOrders = true;
                    var salesOrders = orders["sale"];
                    for (var i = 0, len = salesOrders.length; i < len; i++) {
                        var url = "sale.do?method=getSalesOrder&salesOrderId=" + salesOrders[i].idStr;
                        html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + url + "'>"
                            + salesOrders[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
                        if (i % 2 == 1) {
                            html += "<br/>";
                        }
                    }
                }

                if(orders["saleReturn"] && orders["saleReturn"].length>0){
                    isHaveUnsettledOrders = true;
                    var salesReturnOrders = orders["saleReturn"];
                    for (var i = 0, len = salesReturnOrders.length; i < len; i++) {
                        var url = "salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=" + salesReturnOrders[i].idStr;
                        html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + url + "'>"
                            + salesReturnOrders[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
                        if (i % 2 == 1) {
                            html += "<br/>";
                        }
                    }
                }
                html += "</div>";
                if (isHaveUnsettledOrders) {
//                    $("#deleteReceiptNo").html(html);
                    $(html).dialog({
                        resizable: false,
                        title: "该客户还有未结算的单据，请结算完再删除!",
                        height: 150,
                        width: 330,
                        modal: true,
                        closeOnEscape: false,
                        buttons: {
                            "确定": function () {
                                $("#deleteReceiptNo").html("");
                                $("#deleteCustomer_dialog").dialog("close");
                            }
                        },
                        close: function () {
                            $("#deleteReceiptNo").html("");
                        }
                    });
                }else{
                    nsDialog.jAlert(result.msg);
                }
            }
        }
    });

    return flag;
}


// copy end.
function deleteSupplierAction(supplierId, isDualRole, needDeleteCustomer,isHaveSupplierShop){
    $.ajax({
        type: "POST",
        url: "txn.do?method=validateDeleteSupplier",
        async: false,
        data: {idStr: supplierId},
        cache: false,
        dataType: "json",
        success: function (json) {
            if (jsonUtil.isEmpty(json)) {
                nsDialog.jAlert("删除供应商出现异常！");
                return;
            }
            if (!json.success) {
                var orders = json.data;
                var html = "<div>";
                var isHaveUnsettledOrders = false;
                if (orders && orders["purchaseOrder"] && orders["purchaseOrder"].length > 0) {
                    var purchaseOrders = orders["purchaseOrder"];
                    isHaveUnsettledOrders = true;
                    for (var i = 0, len = purchaseOrders.length; i < len; i++) {
                        var url = "RFbuy.do?method=show&id=" + purchaseOrders[i].idStr;
                        html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + url + "'>"
                            + purchaseOrders[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
                        if (i % 2 == 1) {
                            html += "<br/>";
                        }
                    }
                }
                if (orders && orders["purchaseReturn"] && orders["purchaseReturn"].length > 0) {
                    isHaveUnsettledOrders = true;
                    var purchaseReturns = orders["purchaseReturn"];
                    for (var i = 0, len = purchaseReturns.length; i < len; i++) {
                        var url = "goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=" + purchaseReturns[i].idStr;
                        html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + url + "'>"
                            + purchaseReturns[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
                        if (i % 2 == 1) {
                            html += "<br/>";
                        }
                    }
                }
                html += "</div>";
                if (isHaveUnsettledOrders) {
                    $(html).dialog({
                        resizable: false,
                        title: "该供应商还有未结算的单据，请结算完再删除!",
                        height: 150,
                        width: 330,
                        modal: true,
                        closeOnEscape: false,
                        buttons: {
                            "确定": function () {
                                $(this).dialog("close");
                            }
                        }
                    });
                } else {
                    nsDialog.jAlert(json.msg);
                }
            } else {
                var msg = "";
                if(isHaveSupplierShop){
                    msg = "友情提示：该供应商是在线商铺，并且已经与本店建立关联关系，删除后将接受不到该店铺发送的站内消息！<br>";
                }
                if(isDualRole){
                    msg += "他既是供应商又是客户，"
                    if(needDeleteCustomer){
                        msg += "若删除供应商，则将同时删除绑定的客户！<br>"
                    }else{
                        msg += "若删除供应商，则取消绑定并保留客户资料！<br>"
                    }
                }
                msg +="请确认是否删除？"

//                if(isDualRole && !needDeleteCustomer){
//                    msg = "他既是供应商又是客户，是否确认删除？若删除供应商，则取消绑定并保留客户资料！";
//                }else if(isDualRole && needDeleteCustomer){
//                    msg = "他既是供应商又是客户，是否确认删除？若删除供应商，则将同时删除绑定的客户！";
//                }
                nsDialog.jConfirm(msg, null, function (returnVal) {
                    if (returnVal) {
                        var data = {idStr: supplierId};
                        if (needDeleteCustomer) {
                            data["alsoDeleteCustomer"] = true;
                        }
                        $.ajax({
                            type: "POST",
                            url: "supplier.do?method=deleteSupplier",
                            async: false,
                            data: data,
                            cache: false,
                            dataType: "json",
                            success: function (json) {
                                if (jsonUtil.isEmpty(json)) {
                                    nsDialog.jAlert("删除供应商出现异常！");
                                }
                                if (!json.success) {
                                    nsDialog.jAlert(json.msg);
                                } else {
                                    window.location = "customer.do?method=searchSuppiler";
                                }
                            },
                            error: function (json) {
                                nsDialog.jAlert("删除供应商出现异常！");
                            }
                        });
                    }
                });
            }
        }
    });
}

function deleteSupplier() {
    var supplierId = $("#supplierId").val();
    if (stringUtil.isEmpty(supplierId)) {
        return;
    }
    var isDualRole = false;
    var isPermanentDualRole = false;   //做过对账单的标记，做过对账单删除的时候一起删除
    var relatedCustomerIdStr = "";
    var isHaveSupplierShop = false;
    APP_BCGOGO.Net.syncAjax({
        url:"supplier.do?method=getSupplierById",
        dataType: "json",
        data: {supplierId:$("#supplierId").val()},
        success: function (data) {
            if(data==null){
                return;
            }
            if(!G.isEmpty(data.customerId)){
                isDualRole = true;
                relatedCustomerIdStr = data.customerIdStr;
                if(data.permanentDualRole){
                    isPermanentDualRole = true;
                }
            }
            if(!G.isEmpty(data.relationType) && data.relationType != 'UNRELATED'){
                isHaveSupplierShop = true;
            }
        }
    });
    if(isDualRole){
        if(isPermanentDualRole){
             //此处不校验会员，既是客户又是供应商的版本目前是无会员的，如果以后又了还请添加相关校验。
            if (!validateDeleteCustomer(relatedCustomerIdStr)) {
                return;
            } else {
                deleteSupplierAction(supplierId, isDualRole, isPermanentDualRole, isHaveSupplierShop);
            }
        }else{
            deleteSupplierAction(supplierId,isDualRole, isPermanentDualRole,isHaveSupplierShop);
        }
    }else {
        deleteSupplierAction(supplierId,isDualRole, isPermanentDualRole,isHaveSupplierShop);
    }
}
//第一级菜单 select_province
function provinceBind() {
    var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
        data: {parentNo: 1}, dataType: "json"});
    if (!r || r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            $("#province")[0].appendChild(option);
        }
    }
}

//第二级菜单 select_city
function cityBind(select) {
    while ($("#city")[0].options.length > 1) {
        $("#city")[0].remove(1);
    }
    while ($("#region")[0].options.length > 1) {
        $("#region")[0].remove(1);
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
                $("#city")[0].appendChild(option);
            }
        }
    }
}

//第三级菜单 select_township
function townshipBind(select) {
    if (select.selectedIndex == 0) {
        return;
    }
    var r = APP_BCGOGO.Net.syncGet({"url": "shop.do?method=selectarea&parentNo=" + select.value, "dataType": "json"});
    if (r === null || typeof(r) == "undefined") {
        return;
    }
    else {
        while ($("#region")[0].options.length > 1) {
            $("#region")[0].remove(1);
        }
        if (typeof(r) != "undefined" && r.length > 0) {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#region")[0].appendChild(option);
            }
        }
    }
}

function initTr(data) {
    $("#customerDatas tr").not(":first").remove();
    $("#totalRows").val(data[0].numFound);
    if (data && data[0] && data[0].customerSuppliers != undefined) {
        for (var i = 0; i < data[0].customerSuppliers.length; i++) {
            var customerSupplier = data[0].customerSuppliers[i];
            var contact = customerSupplier.contact == null ? '暂无' : customerSupplier.contact;
            var mobile = customerSupplier.mobile == null ? '暂无' : customerSupplier.mobile;
            var customerOrSupplierShopId =  G.Lang.normalize(customerSupplier.customerOrSupplierShopId,"");

            var tr = '<tr><td><input type="radio" value="' + data[0].customerSuppliers[i].idStr + '" name="customer" customerOrSupplierShopId="'+customerOrSupplierShopId+'"/></td><td>';
            tr += data[0].customerSuppliers[i].name;
            tr += '<a class="connecter J_connector" customerId="' + customerSupplier.idStr + '"></a>' +
                '<div class="prompt J_prompt" customerId="' + customerSupplier.idStr + '" style="margin:0 0 0 30px; display:none;">' +
                '<div class="promptTop"></div>' +
                '<div class="promptBody">' +
                '<div class="lineList">联系人&nbsp;' + contact + '&nbsp;' + mobile + '</div>' +
                '</div>' +
                '<div class="promptBottom"></div>' +
                '</div>';
            if (!G.Lang.isEmpty(customerOrSupplierShopId)) {
                tr += '<a style="cursor:pointer;margin:2px 0 0 5px;display: inline-block;vertical-align: top"> ' +
                    '<img src="images/icon_online_shop.png"> ' +
                    '</a>';
            }
            tr +='</td>';
            tr += '<td>' + data[0].customerSuppliers[i]['areaInfo'] + '</td><td>';
            if (data[0].customerSuppliers[i].address != null) {
                tr += data[0].customerSuppliers[i].address;
            }
            tr += '</td>';
            $("#customerDatas").append($(tr));
        }

    }


}

<!-- 预付款取用记录js  add by zhuj-->
$(document).ready(function () {

    $("#queryDepositOrders").bind("click", function (e) {
        var startPageNo = 1;
        var inOutFlag = 0;
        var supplierId = $('#supplierId').val();
        queryDepositOrders(startPageNo, inOutFlag, supplierId);// 进行页面默认查询
        e.stopPropagation();
    });

    // 绑定checkbox的事件 从事件上下文中获取id
    $("#inFlag,#outFlag").bind("click", function (e) {
        if ($(this) && $(this).val()) {
            $(this).val('');
        } else {
            if (e.target.id === 'inFlag') {
                $(this).val('1');
            } else if (e.target.id === 'outFlag') {
                $(this).val('2');
            }
        }
        var startPageNo = 1;
        var inOutFlag = getInOutFlag();
        var supplierId = $('#supplierId').val();
        queryDepositOrders(startPageNo, inOutFlag, supplierId);// 进行页面默认查询
        //阻止事件冒泡
        e.stopPropagation();
    });


    // 绑定表格列标头点击事件 时间 金额
    $("#depositOrdersTime,#depositOrdersMoney").bind("click", function (e) {
        var startPageNo = 1;
        var inOutFlag = getInOutFlag();
        var supplierId = $('#supplierId').val();
        var sortName;
        var sortFlag;
        var sortObj = {};
        sortFlag = e.target.className === 'descending' ? 'ascending' : 'descending';
        if (e.target.id === 'depositOrdersTime') {
            sortName = 'time';
            $('#depositOrdersTime').attr('class', sortFlag);
        } else if (e.target.id === 'depositOrdersMoney') {
            sortName = 'money';
            $('#depositOrdersMoney').attr('class', sortFlag);
        }
        sortObj[sortName] = sortFlag;
        queryDepositOrders(startPageNo, inOutFlag, supplierId, sortObj);// 进行页面默认查询
        e.stopPropagation();
    });


});

function getInOutFlag() {
    var inOutFlag;
    var inFlag = $("#inFlag").val(); // 获取到的为字符串
    var outFlag = $("#outFlag").val();
    if (inFlag && inFlag === '1' && outFlag && outFlag === '2') {
        inOutFlag = '0';
    } else {
        if (inFlag && inFlag === '1')inOutFlag = inFlag;
        if (outFlag && outFlag === '2')inOutFlag = outFlag;
    }
    return  inOutFlag;
}

function queryDepositOrders(startPageNo, inOutFlag, supplierId, sort) {
    var url = "payable.do?method=queryDepositOrdersShopIdAndSupplierId";
    var dataContent;
    if (sort && sort['time']) {
        dataContent = {
            startPageNo: startPageNo,
            inOutFlag: inOutFlag,
            supplierId: supplierId,
            sortName: 'time',
            sortFlag: sort['time']
        };
    } else if (sort && sort['money']) {
        dataContent = {
            startPageNo: startPageNo,
            inOutFlag: inOutFlag,
            supplierId: supplierId,
            sortName: 'money',
            sortFlag: sort['money']
        };
    } else {
        dataContent = {
            startPageNo: startPageNo,
            inOutFlag: inOutFlag,
            supplierId: supplierId
        }
    }
    APP_BCGOGO.Net.syncPost({
        url: url,
        dataType: "json",
        data: dataContent,
        success: function (json) {
            if (json) {
                initDepositOrdersTable(json);// initPage里面的回调函数只有在点击分页组件的时候会调用 第一次初始化自己调用一次
                 // 和ajaxPaging 标签配合使用 初始化查询条件
                initPage(json, "dynamical2", url, '', 'initDepositOrdersTable', '', '', dataContent, '');
            }
            //TODO 这里检测dialog的状态 已经open则关闭 重新弹出?
                var dialogTitle = "<div id=\ \" class=\"\">预付款充值/消费记录</div>";
                $("#depositOrders").dialog({
                    resizable: true,
                    title: dialogTitle,
                    width: 900,
                    modal: true,
                    closeOnEscape: false
                });

        }
    });
}

function initDepositOrdersTable(jsonStr) {
    depositOrdersTableContentInit(jsonStr);
    //depositOrdersTableStyleInit();
}

function depositOrdersTableContentInit(json) {
    var data = json.results;
    $("#deposit_orders_table tr:not(:first)").remove(); // remove掉已经存在的表格数据
    if (data && data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var tr = "<tr class='table-row-original' depositOrderId='" + data[i].id + "'>";
            tr += '<td style="padding-left:10px;">' + data[i].createdTime + '</td>';
            tr += '<td>' + dataTransition.rounding(data[i].actuallyPaid, 2) + '元</td>';
            if (data[i].inOut && data[i].inOut === 1) {
                tr += '<td>' + '收款' + '</td>';
            }
            else {
                tr += '<td>' + '取用' + '</td>';
            }
            var depositTypes = data[i].depositType.split("|");
            tr += '<td>' + depositTypes[1] + '</td>';
            if (data[i].relatedOrderNo) {
                tr += '<td><a href="' + genUrlByDepositTypeAndId(depositTypes[0], data[i].relatedOrderIdStr) + '">' + data[i].relatedOrderNo + '</a></td>'; //TODO 这边根据单据的类型生成URL
            } else {
                tr += '<td>' + '-' + '</td>';
            }
            tr += '<td title="' + data[i].operator + '">' + data[i].operator + '</td>';
            tr += '<td title="' + (data[i].memo == null ? "无" : data[i].memo) + '">' + (data[i].memo == null ? "无" : data[i].memo) + '</td>';
            tr += '</tr>';
            var $tr = $(tr);
            $("#deposit_orders_table").append($tr);
        }
    }
}

function genUrlByDepositTypeAndId(type, id) {
    var urlPrefix;
    if (type) {
        if (type == "SALES" || type == "SALES_REPEAL") {
            urlPrefix = 'sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=';
        }
        if (type == "SALES_BACK" || type == "SALES_BACK_REPEAL") {
            urlPrefix = " salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=";
        }
        if (type == "INVENTORY" || type == "INVENTORY_REPEAL") {
            urlPrefix = "storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=";
        }
        if (type == "INVENTORY_BACK" || type == "INVENTORY_BACK_REPEAL") {
            urlPrefix = "goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=";
        }
        if (type == "COMPARE") {
            urlPrefix = "statementAccount.do?method=showStatementAccountOrderById&statementOrderId=";
        }

    }
    return urlPrefix + id;
}




<!-- add end-->