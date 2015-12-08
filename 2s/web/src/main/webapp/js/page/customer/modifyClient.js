var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$(function () {
    $("#input_address1,#input_address2").focus(function(){
        if($(this).val() == $(this).attr("initValue")) {
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
    $(".add_supplier").hide();
    $("#mergeInfo").hide();
    $("#exist").attr("checked", true);

    $("#landline2,#landlineSecond2,#landlineThird2,#landline3,#landlineSecond3,#landlineThird3").live("blur", function(){
        if(this.value != '') {
            check.inputTelephone(this);
        }
    });
    $("#landline2,#landlineSecond2,#landlineThird2,#landline3,#landlineSecond3,#landlineThird3").keyup(function () {
        $(this).val($(this).val().replace(/[^\d\-]+/g, "").replace(/\-+/g, "-"));
        if ($(this).val().charAt(0) == '-') {
            $(this).val("");
        }
    });
    // fill supplierId for merge
    $("#supplierDatas :radio").live("click",function(){
        $("#supplierId").val($(this).val());
        $("#supplierShopId").val($(this).attr("customerOrSuppliershopId"));

    });
    $("#radExist").live("click",function () {
        $(".exist_suppliers").css("display","block");
        $(".add_supplier").css("display","none");
        $("#supplierId").val($("#supplierDatas :radio[checked]").val());
        $("#supplierShopId").val($("#supplierDatas :radio[checked]").attr("customerOrSuppliershopId"));
    });
    $("#birthdayString2").datepicker({
        "numberOfMonths" : 1,
        "changeYear":true,
        "changeMonth":true,
        "dateFormat": "mm-dd",
        "yearRange": "c-100, c",
        "yearSuffix":"",
        "showButtonPanel":true
    });

    // 新增供应商
    $("#radAdd").live("click",function () {
        $("#supplierId").val("");
        $("#supplierShopId").val("");
        $(".exist_suppliers").css("display","none");
        $(".add_supplier").css("display","block");
        $("#mergeInfo").css("display","none");
        $(".table_inputContact").show();
        if($("#parentPageType").val() == 'uncleUser') { // uncleUser.jsp use this script
            var name = $("#name").val();
            var province = $("#province").val();
            var city = $("#city").val();
            var region = $("#region").val();
            var address = $("#address").val();
            var landLine = $("#landLine").val();
            var landLineSecond=$("#landLineSecond").val();
            var landLineThird=$("#landLineThird").val();
        }  else {
            var name = $("#name").val();
            var province = $("#select_province").val();
            var city = $("#select_city").val();
            var region = $("#select_township").val();
            var address = $("#input_address").val();
            var landLine = $("#phone").val();
            var landLineSecond=$("#phoneSecond").val();
            var landLineThird=$("#phoneThird").val();
        }

        var abbr = $("#shortName").val();

        var fax = $("#fax").val();
        var bank = $("#bank").val();
        var bankAccountName = $("#bankAccountName").val();
        var account = $("#account").val();
        var settlementType = $("#settlementType").val();
        var invoiceCategory = $("#invoiceCategory").val();
        var memo = $("#memo").val();

        if(APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact && APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact ){
            // 填充联系人
            // 联系人1
            $("#contacts30\\.name").val($("#contacts\\[0\\]\\.name").val());
            $("#contacts30\\.mobile").val($("#contacts\\[0\\]\\.mobile").val());
            $("#contacts30\\.email").val($("#contacts\\[0\\]\\.email").val());
            $("#contacts30\\.qq").val($("#contacts\\[0\\]\\.qq").val());
            $("#contacts30\\.level").val($("#contacts\\[0\\]\\.level").val());
            $("#contacts30\\.mainContact").val($("#contacts\\[0\\]\\.mainContact").val());

            // 联系人2
            $("#contacts31\\.name").val($("#contacts\\[1\\]\\.name").val());
            $("#contacts31\\.mobile").val($("#contacts\\[1\\]\\.mobile").val());
            $("#contacts31\\.email").val($("#contacts\\[1\\]\\.email").val());
            $("#contacts31\\.qq").val($("#contacts\\[1\\]\\.qq").val());
            $("#contacts31\\.level").val($("#contacts\\[1\\]\\.level").val());
            $("#contacts31\\.mainContact").val($("#contacts\\[1\\]\\.mainContact").val());

            // 联系人3
            $("#contacts32\\.name").val($("#contacts\\[2\\]\\.name").val());
            $("#contacts32\\.mobile").val($("#contacts\\[2\\]\\.mobile").val());
            $("#contacts32\\.email").val($("#contacts\\[2\\]\\.email").val());
            $("#contacts32\\.qq").val($("#contacts\\[2\\]\\.qq").val());
            $("#contacts32\\.level").val($("#contacts\\[2\\]\\.level").val());
            $("#contacts32\\.mainContact").val($("#contacts\\[2\\]\\.mainContact").val());

            // 渲染主联系人图标
            $("input[id^='contacts3']").filter("input[id$='mainContact']").each(function () {
                // 主联系人
                var $thisTr =  $(this).closest("tr");
                var $contact = $thisTr.find("a[class*='connacter']");
                $contact.removeClass("icon_connacter").addClass("hover").removeClass("icon_grayconnacter");
                var $alert =  $thisTr.find("div .alert");
                if ($(this).val() === "1") {
                    $contact.addClass("icon_connacter");
                    if($alert){
                        $thisTr.remove($alert);
                    }
                }else{
                    $contact.addClass("icon_grayconnacter");
                    if(!$alert){
                        $alert = $('<div class="alert">'
                            + 'span class="arrowTop"></span><div class="alertAll">'
                            + '<div class="alertLeft"></div>'
                            + '<div class="alertBody">点击设为主联系人</div>'
                            + '<div class="alertRight"></div>'
                            + '</div>'
                            + '</div>')
                        $alert.insertAfter($contact);
                    }
                }
            });
        }else{
            $("contact3").val($("contact").val());
            $("email3").val($("email").val());
            $("mobile3").val($("mobile").val());
            $("qq3").val($("qq").val());
        }

        if (!G.Lang.isEmpty($("#customerShopId").val())) {
            var areaInfo = $("#areaInfo").text();
            $("#name3").val(name).hide();
            $("#name3_span").text(name).show();
            $("#abbr3").val(abbr).hide();
            $("#abbr3_span").text(abbr).show();
            $("#landline3").val(landLine).hide();
            $("#landLine3_span").text(landLine).show();
            $("#landlineSecond3").val(landLineSecond).hide();
            $("#landLineSecond3_span").text(landLineSecond).show();
            $("#landlineThird3").val(landLineThird).hide();
            $("#landLineThird3_span").text(landLineThird).show();
            $("#fax3").val(fax).hide();
            $("#fax3_span").text(fax).show();
            $("#select_province2").val(province).hide().change();
            $("#select_city2").val(city).hide().change();
            $("#select_township2").val(region).hide().change();
            $("#areaInfo2_span").text(areaInfo).show();
            if (address != '详细地址') {
                $("#input_address2").val(address).hide();
                $("#address2_span").text(address).show();
            } else {
                $("#input_address2").val("").hide();
                $("#address2_span").text("").show();
            }
        } else {
            $("#name3").val(name).show();
            $("#name3_span").text("").hide();
            $("#abbr3").val(abbr).show();
            $("#abbr3_span").text("").hide();
            $("#landline3").val(landLine).show();
            $("#landLine3_span").text("").hide();
            $("#landlineSecond3").val(landLineSecond).show();
            $("#landLineSecond3_span").text("").hide();
            $("#landlineThird3").val(landLineThird).show();
            $("#landLineThird3_span").text("").hide();
            $("#fax3").val(fax).show();
            $("#fax3_span").text("").hide();
            $("#areaInfo2_span").val("").hide();
            $("#select_province2").val(province).show().change();
            $("#select_city2").val(city).show().change();
            $("#select_township2").val(region).show().change();
            $("#address2_span").val("").hide();
            if (address == '' || address == '详细地址') {
                $("#input_address2").css("color", "#7e7e7e").val('详细地址').show();
            } else {
                $("#input_address2").css("color", "#000000").val(address).show();
            }
        }
        $("#landline3").val(landLine).show();
        $("#landlineSecond3").val(landLineSecond).show();
        $("#landlineThird3").val(landLineThird).show();


        $("#fax3").val(fax).show();

        $("#bank3").val(bank);
        $("#accountName3").val(bankAccountName);
        $("#account3").val(account);
        $("#settlementType3").val(settlementType);
        $("#invoiceCategory3").val(invoiceCategory);
        $("#memo3").val(memo);

        $("#birthdayString3").val($("#birthdayString").val())
        $("#customerKind3").val($("#customerKind").val())
    });
    $("#merge").click(function () {
        var supplierId = $("#supplierId").val();
        if (GLOBAL.Lang.isStringEmpty(supplierId)) {
            nsDialog.jAlert("请选择供应商!");
            return;
        }
        var supplierShopId = $("#supplierDatas :radio[checked]").attr("customerOrSupplierShopId");
        var customerShopId = $("#customerShopId").val();

        if (!G.Lang.isEmpty(customerShopId) && !G.Lang.isEmpty(supplierShopId) && customerShopId != supplierShopId) {
            nsDialog.jAlert("友情提示：双方不是同一个店铺，不允许绑定！");
            return;
        }

        $(".exist_suppliers").hide();
        $(".select_supplier").hide();
        $("#mergeInfo").show();
        $("#addOrExist").val('exist');
        $(".table_contact_gen").show();
        APP_BCGOGO.Net.asyncAjax({
            url: "supplier.do?method=getSupplierById",
            dataType: "json",
            data: {
                supplierId: supplierId
            },
            success: function (data) {
                if($("#parentPageType").val() == 'uncleUser') {
                    var name = $("#name").val();
                    var province = $("#province").val();
                    var city = $("#city").val();
                    var region = $("#region").val();
                    var address = $("#address").val();
                    var landLine = $("#landLine").val();
                    var landLineSecond=$("#landLineSecond").val();
                    var landLineThird=$("#landLineThird").val();
                    var areaInfo = $("#areaInfo").text();
                }  else {
                    var name = $("#name").val();
                    var province = $("#select_province").val();
                    var city = $("#select_city").val();
                    var region = $("#select_township").val();
                    var address = $("#input_address").val();
                    var landLine = $("#phone").val();
                    var landLineSecond=$("#phoneSecond").val();
                    var landLineThird=$("#phoneThird").val();
                }

                var abbr = $("#shortName").val();
                var fax = $("#fax").val();
                var bank = $("#bank").val();
                var bankAccountName = $("#bankAccountName").val();
                var account = $("#account").val();
                var settlementType = $("#settlementType").val();
                var invoiceCategory = $("#invoiceCategory").val();
                var memo = $("#memo").val();
                var thirdCategoryIdStr = data.thirdCategoryIdStr;
                var businessScope = data.businessScope;
                var vehicleModelContent = data.vehicleModelContent;
                var vehicleModelIdStr = data.vehicleModelIdStr;

                if(APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact && APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact ){
                    // 填充联系人信息 变量命名规则 contact2x x表示第x个联系人
                    // 联系人1
                    var contacts = new Array();

                    var contact20_name = $("#contacts\\[0\\]\\.name").val();
                    var contact20_mobile = $("#contacts\\[0\\]\\.mobile").val();
                    var contact20_email = $("#contacts\\[0\\]\\.email").val();
                    var contact20_qq = $("#contacts\\[0\\]\\.qq").val();
                    var contact20_level = $("#contacts\\[0\\]\\.level").val();
                    var contact20_mainContact = $("#contacts\\[0\\]\\.mainContact").val();
                    var contact20 =
                    {
                        id: "0",
                        name: contact20_name,
                        mobile: contact20_mobile,
                        email:contact20_email,
                        qq:contact20_qq,
                        level:contact20_level,
                        mainContact:contact20_mainContact,
                        from:"page"
                    };
                    if(isValidContact(contact20)){
                        contacts.push(contact20);
                    }

                    // 联系人2
                    var contact21_name = $("#contacts\\[1\\]\\.name").val();
                    var contact21_mobile = $("#contacts\\[1\\]\\.mobile").val();
                    var contact21_email = $("#contacts\\[1\\]\\.email").val();
                    var contact21_qq = $("#contacts\\[1\\]\\.qq").val();
                    var contact21_level = $("#contacts\\[1\\]\\.level").val();
                    var contact21_mainContact = $("#contacts\\[1\\]\\.mainContact").val();

                    var contact21 =
                    {
                        id: "0",
                        name: contact21_name,
                        mobile: contact21_mobile,
                        email:contact21_email,
                        qq:contact21_qq,
                        level:contact21_level,
                        mainContact:contact21_mainContact,
                        from:"page"
                    };
                    if(isValidContact(contact21)){
                        contacts.push(contact21);
                    }

                    // 联系人3
                    var contact22_name = $("#contacts\\[2\\]\\.name").val();
                    var contact22_mobile = $("#contacts\\[2\\]\\.mobile").val();
                    var contact22_email = $("#contacts\\[2\\]\\.email").val();
                    var contact22_qq = $("#contacts\\[2\\]\\.qq").val();
                    var contact22_level = $("#contacts\\[2\\]\\.level").val();
                    var contact22_mainContact = $("#contacts\\[2\\]\\.mainContact").val();
                    var contact22 =
                    {
                        id: "0",
                        name: contact22_name,
                        mobile: contact22_mobile,
                        email:contact22_email,
                        qq:contact22_qq,
                        level:contact22_level,
                        mainContact:contact22_mainContact,
                        from:"page"
                    };
                    if(isValidContact(contact22)){
                        contacts.push(contact22);
                    };

                    if(data.contacts){
                        if(data.contacts[0]){

                            // ---  一下为server端查询到的联系人列表信息 --//
                            var contact23_id = G.normalize(data.contacts[0].idStr);
                            var contact23_name = G.normalize(data.contacts[0].name);
                            var contact23_mobile = G.normalize(data.contacts[0].mobile);
                            var contact23_email = G.normalize(data.contacts[0].email);
                            var contact23_qq = G.normalize(data.contacts[0].qq);
                            var contact23_level = G.normalize(data.contacts[0].level);
                            var contact23_mainContact = G.normalize(data.contacts[0].mainContact);
                            // id 不填充 既是客户又是供应商的联系人 ”新增“
                            var contact23 =
                            {
                                id: "0",
                                name: contact23_name,
                                mobile: contact23_mobile,
                                email:contact23_email,
                                qq:contact23_qq,
                                level:contact23_level,
                                mainContact:contact23_mainContact,
                                from:"db"
                            };
                            if(isValidContact(contact23)){
                                contacts.push(contact23);
                            };
                        }

                        if(data.contacts[1])
                        {
                            var contact24_id = G.normalize(data.contacts[1].idStr);
                            var contact24_name = G.normalize(data.contacts[1].name);
                            var contact24_mobile = G.normalize(data.contacts[1].mobile);
                            var contact24_email = G.normalize(data.contacts[1].email);
                            var contact24_qq = G.normalize(data.contacts[1].qq);
                            var contact24_level = G.normalize(data.contacts[1].level);
                            var contact24_mainContact = G.normalize(data.contacts[1].mainContact);
                            var contact24 =
                            {
                                id: "0",
                                name: contact24_name,
                                mobile: contact24_mobile,
                                email:contact24_email,
                                qq:contact24_qq,
                                level:contact24_level,
                                mainContact:contact24_mainContact,
                                from:"db"
                            };
                            if(isValidContact(contact24)){
                                contacts.push(contact24);
                            };
                        }

                        if(data.contacts[2]){
                            var contact25_id = G.normalize(data.contacts[2].idStr);
                            var contact25_name = G.normalize(data.contacts[2].name);
                            var contact25_mobile = G.normalize(data.contacts[2].mobile);
                            var contact25_email = G.normalize(data.contacts[2].email);
                            var contact25_qq = G.normalize(data.contacts[2].qq);
                            var contact25_level = G.normalize(data.contacts[2].level);
                            var contact25_mainContact = G.normalize(data.contacts[2].mainContact);

                            var contact25 =
                            {
                                id: "0",
                                name: contact25_name,
                                mobile: contact25_mobile,
                                email:contact25_email,
                                qq:contact25_qq,
                                level:contact25_level,
                                mainContact:contact25_mainContact,
                                from:"db"
                            };
                            if (isValidContact(contact25)) {
                                contacts.push(contact25);
                            } ;
                        }
                    }

                    /* 展示所有有效联系人 */
                    // 设置主联系人标识符
                    if(!G.isEmpty(contacts)){
                        var fromIndexs;
                        var mainContactCount = countMainContact(contacts);
                        if (mainContactCount == 0) {
                            contacts[0].mainContact = "1";
                        }else if(mainContactCount == 2){
                            fromIndexs = getContactIndexByFrom(contacts);
                        }
                        if( fromIndexs && !G.isEmpty(fromIndexs.page) && !G.isEmpty(fromIndexs.db)){
                            contacts[fromIndexs.page].mainContact = "1"; // 貌似是多余的
                            contacts[fromIndexs.db].mainContact = "0";
                        }
                    }

                    if (!G.isEmpty(contacts)) {
                        var $contacts;
                        for (var index = 0; index < contacts.length; index++) {
                            var toBeContactStr = "<tr class=\"single_contact_gen\">"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].name\" id=\"contacts2" + index + ".name\" value=\"" + contacts[index].name + "\"/></td>"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].mobile\" id=\"contacts2" + index + ".mobile\" value=\"" + contacts[index].mobile + "\" style=\"width:73px;\" /></td>"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].email\" id=\"contacts2" + index + ".email\"  value=\"" + contacts[index].email + "\" style=\"width:90px;\" /></td>"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].qq\" id=\"contacts2" + index + ".qq\"  value=\"" + contacts[index].qq + "\" /></td>"
                                + "<td><input type=\"hidden\" name=\"contacts2[" + index + "].level\" id=\"contacts2" + index + ".level\" value=\"" + index + "\"/>";
                            if (contacts[index].mainContact === "1") {
                                toBeContactStr += "<a class=\"icon_connacter\"></a><a class=\"close\"></a>";
                            } else {
                                toBeContactStr += "<a class=\"icon_grayconnacter hover\"></a><a class=\"close\"></a><div class=\"alert\"><span class=\"arrowTop\"></span><div class=\"alertAll\"> <div class=\"alertLeft\"></div><div class=\"alertBody\">点击设为主联系人</div><div class=\"alertRight\"></div></div></div>";
                            }
                            toBeContactStr += "<input type=\"hidden\" name=\"contacts2[" + index + "].mainContact\" id=\"contacts2" + index + ".mainContact\" value=\"" + contacts[index].mainContact + "\"/></td>"
                            $contacts = $(toBeContactStr);
                            $(".table_contact_gen").find("tr:last").after($contacts);
                        }
                        // 补充空的联系人
                        if (contacts.length === 1||contacts.length === 2) {
                            if (contacts.length === 1) {
                                var toBeContactStr = "<tr class=\"single_contact_gen\">"
                                    + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + 1 + "].name\" id=\"contacts2" + 1 + ".name\" value=\"\"/></td>"
                                    + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + 1 + "].mobile\" id=\"contacts2" + 1 + ".mobile\" value=\"\" style=\"width:73px;\" /></td>"
                                    + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + 1 + "].email\" id=\"contacts2" + 1 + ".email\"  value=\"\" style=\"width:90px;\" /></td>"
                                    + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + 1 + "].qq\" id=\"contacts2" + 1 + ".qq\"  value=\"\" /></td>"
                                    + "<td><input type=\"hidden\" name=\"contacts2[" + 1 + "].level\" id=\"contacts2" + 1 + ".level\" value=\"" + 1 + "\"/>";
                                toBeContactStr += "<a class=\"icon_grayconnacter hover\"></a><a class=\"close\"></a><div class=\"alert\"><span class=\"arrowTop\"></span><div class=\"alertAll\"> <div class=\"alertLeft\"></div><div class=\"alertBody\">点击设为主联系人</div><div class=\"alertRight\"></div></div></div>";
                                toBeContactStr += "<input type=\"hidden\" name=\"contacts2[" + 1 + "].mainContact\" id=\"contacts2" + 1 + ".mainContact\" value=\"" + 0 + "\"/></td>"
                                $contacts = $(toBeContactStr);
                                $(".table_contact_gen").find("tr:last").after($contacts);
                            }
                            var toBeContactStr = "<tr class=\"single_contact_gen\">"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + 2 + "].name\" id=\"contacts2" + 2 + ".name\" value=\"\"/></td>"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + 2 + "].mobile\" id=\"contacts2" + 2 + ".mobile\" value=\"\" style=\"width:73px;\" /></td>"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + 2 + "].email\" id=\"contacts2" + 2 + ".email\"  value=\"\" style=\"width:90px;\" /></td>"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + 2 + "].qq\" id=\"contacts2" + 2 + ".qq\"  value=\"\" /></td>"
                                + "<td><input type=\"hidden\" name=\"contacts2[" + 2 + "].level\" id=\"contacts2" + 2 + ".level\" value=\"" + 2 + "\"/>";
                                toBeContactStr += "<a class=\"icon_grayconnacter hover\"></a><a class=\"close\"></a><div class=\"alert\"><span class=\"arrowTop\"></span><div class=\"alertAll\"> <div class=\"alertLeft\"></div><div class=\"alertBody\">点击设为主联系人</div><div class=\"alertRight\"></div></div></div>";
                                toBeContactStr += "<input type=\"hidden\" name=\"contacts2[" + 2 + "].mainContact\" id=\"contacts2" + 2 + ".mainContact\" value=\"" + 0 + "\"/></td>"
                            $contacts = $(toBeContactStr);
                            $(".table_contact_gen").find("tr:last").after($contacts);
                        }

                        if(contacts.length > 3){
                            $(".warning").show();
                        }

                    } else {
                        for (var index = 0; index < 3; index++) {
                            var toBeContactStr = "<tr class=\"single_contact_gen\">"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].name\" id=\"contacts2" + index + ".name\" value=\"\"/></td>"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].mobile\" id=\"contacts2" + index + ".mobile\" value=\"\" style=\"width:73px;\" /></td>"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].email\" id=\"contacts2" + index + ".email\"  value=\"\" style=\"width:90px;\" /></td>"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].qq\" id=\"contacts2" + index + ".qq\"  value=\"\" /></td>"
                                + "<td><input type=\"hidden\" name=\"contacts2[" + index + "].level\" id=\"contacts2" + index + ".level\" value=\"" + index + "\"/>";
                            if (index === 0) {
                                toBeContactStr += "<a class=\"icon_connacter\"></a><a class=\"close\"></a>";
                                toBeContactStr += "<input type=\"hidden\" name=\"contacts2[" + index + "].mainContact\" id=\"contacts2" + index + ".mainContact\" value=\"" + 1 + "\"/></td>"
                            } else {
                                toBeContactStr += "<a class=\"icon_grayconnacter hover\"></a><a class=\"close\"></a><div class=\"alert\"><span class=\"arrowTop\"></span><div class=\"alertAll\"> <div class=\"alertLeft\"></div><div class=\"alertBody\">点击设为主联系人</div><div class=\"alertRight\"></div></div></div>";
                                toBeContactStr += "<input type=\"hidden\" name=\"contacts2[" + index + "].mainContact\" id=\"contacts2" + index + ".mainContact\" value=\"" + 0 + "\"/></td>"
                            }
                            $contacts = $(toBeContactStr);
                            $(".table_contact_gen").find("tr:last").after($contacts);
                        }
                    }


                    initContactStyle();

                    // 渲染主联系人图标
                    $("input[id^='contacts2']").filter("input[id$='mainContact']").each(function () {
                        // 主联系人
                        var $thisTr =  $(this).closest("tr");
                        var $contact = $thisTr.find("a[class*='connacter']");
                        $contact.removeClass("icon_connacter").addClass("hover").removeClass("icon_grayconnacter");
                        var $alert =  $thisTr.find("div .alert");
                        if ($(this).val() === "1") {
                            $contact.addClass("icon_connacter");
                            if($alert){
                                $thisTr.remove($alert);
                            }
                        }else{
                            $contact.addClass("icon_grayconnacter");
                            if(!$alert){
                                $alert = $('<div class="alert">'
                                    + 'span class="arrowTop"></span><div class="alertAll">'
                                    + '<div class="alertLeft"></div>'
                                    + '<div class="alertBody">点击设为主联系人</div>'
                                    + '<div class="alertRight"></div>'
                                    + '</div>'
                                    + '</div>')
                                $alert.insertAfter($contact);
                            }
                        }
                    });
                }else{
                    var contact = $("#contact").val();
                    var mobile = $("#mobile").val();
                    var email = $("#email").val();
                    var qq = $("#qq").val();
                    if (G.isEmpty(contact)) {
                        $("#contact2").val(data.contact == null ? "" : data.contact);
                    } else {
                        $("#contact2").val(contact);
                    }
                    if (G.isEmpty(mobile)) {
                        $("#mobile2").val(data.mobile == null ? "" : data.mobile);
                    } else {
                        $("#mobile2").val(mobile);
                    }
                    if (G.isEmpty(email)) {
                        $("#email2").val(data.email == null ? "" : data.email);
                    } else {
                        $("#email2").val(email);
                    }
                    if (G.isEmpty(qq)) {
                        $("#qq2").val(data.qq == null ? "" : data.qq);
                        $("#qq2").val(data.qq == null ? "" : data.qq);
                    } else {
                        $("#qq2").val(qq);
                    }
                }
                  //客户是在线店铺
                if (!G.Lang.isEmpty(customerShopId)) {
                    $("#name2_span").show().text(name);
                    $("#name2").hide().val(name);
                    $("#abbr2").val(abbr).hide();
                    $("#abbr2_span").text(abbr).show();
                    $("#landline2").val(landLine).hide();
                    $("#landLine2_span").text(landLine).show();
                    $("#landlineSecond2").val(landLineSecond).hide();
                    $("#landLineSecond2_span").text(landLineSecond).show();
                    $("#landlineThird2").val(landLineThird).hide();
                    $("#landLineThird2_span").text(landLineThird).show();
                    $("#fax2").val(fax).hide();
                    $("#fax2_span").text(fax).show();
                    if (address != '详细地址') {
                        $("#input_address1").val(address).hide();
                        $("#address1_span").text(address).show();
                    } else {
                        $("#input_address1").val("").hide();
                        $("#address1_span").text("").show();
                    }
                    $("#select_province1").val(province).hide().change();
                    $("#select_city1").val(city).hide().change();
                    $("#select_township1").val(region).hide();
                    $("#areaInfo1_span").text(areaInfo).show();
                }else if(!G.Lang.isEmpty(supplierShopId)){
                    $("#name2_span").show().text(G.normalize(data.name));
                    $("#name2").hide().val(G.normalize(data.name));
                    $("#abbr2").val(G.normalize(data.shortName)).hide();
                    $("#abbr2_span").text(G.normalize(data.shortName)).show();
                    $("#input_address1").val(G.normalize(data.address)).hide();
                    $("#address1_span").text(G.normalize(data.address)).show();
                    $("#select_province1").val(G.normalize(data.province)).hide().change();
                    $("#select_city1").val(G.normalize(data.city)).hide().change();
                    $("#select_township1").val(G.normalize(data.region)).hide();
                    $("#areaInfo1_span").text(G.normalize(data.areaInfo)).show();
                    $("#updateBusinessScopeSpan").text(businessScope);
                    $("#updateThirdCategoryStr").val(thirdCategoryIdStr);
                    $("#updateVehicleModelContentSpan").val(vehicleModelContent);
                    $("#updateVehicleModelIdStr").val(vehicleModelIdStr);

                } else {

                    $("#name2_span").text("").hide();
                    if (name == '') {
                        $("#name2").val(data.name == null ? "" : data.name).show();
                    } else {
                        $("#name2").val(name).show();
                    }
                    $("#abbr2_span").text("").hide();
                    if (abbr == '') {
                        $("#abbr2").val(data.abbr == null ? "" : data.abbr).show();
                    } else {
                        $("#abbr2").val(abbr).show();
                    }
                    $("#landLine2_span").text("").hide();
                    var landlineAjax=data.landLine == null ? "" : data.landLine;
                    var landlineSecondAjax=data.landLineSecond == null ? "" : data.landLineSecond;
                    var landlineThirdAjax=data.landLine == null ? "" : data.landLineThird;
                    if (landLine == '') {
                        if(landlineAjax!=""&&landlineAjax!=landLineSecond&&landlineAjax!=landLineThird){
                            $("#landline2").val(landlineAjax).show();
                        }else{
                            if(landlineSecondAjax!=""&&landlineSecondAjax!=landLineSecond&&landlineSecondAjax!=landLineThird){
                                $("#landline2").val(landlineSecondAjax).show();
                            }else{
                                if(landlineThirdAjax!=""&&landlineThirdAjax!=landLineSecond&&landlineThirdAjax!=landLineThird){
                                    $("#landline2").val(landlineThirdAjax).show();
                                }
                            }
                        }
                    } else {
                        $("#landline2").val(landLine).show();
                    }
                    $("#landLineSecond2_span").text("").hide();
                    if (landLineSecond == '') {
                        if(landlineAjax!=""&&landlineAjax!=$("#landline2").val()&&landlineAjax!=landLineThird){
                            $("#landlineSecond2").val(landlineAjax).show();
                        }else{
                            if(landlineSecondAjax!=""&&landlineSecondAjax!=$("#landline2").val()&&landlineSecondAjax!=landLineThird){
                                $("#landlineSecond2").val(landlineSecondAjax).show();
                            }else{
                                if(landlineThirdAjax!=""&&landlineThirdAjax!=$("#landline2").val()&&landlineThirdAjax!=landLineThird){
                                    $("#landlineSecond2").val(landlineThirdAjax).show();
                                }
                            }
                        }
                    } else {
                        $("#landlineSecond2").val(landLineSecond).show();
                    }
                    $("#landLineThird2_span").text("").hide();
                    if (landLineThird == '') {
                        if(landlineAjax!=""&&landlineAjax!=$("#landline2").val()&&landlineAjax!=$("#landlineSecond2").val()){
                            $("#landlineThird2").val(landlineAjax).show();
                        }else{
                            if(landlineSecondAjax!=""&&landlineSecondAjax!=$("#landline2").val()&&landlineSecondAjax!=$("#landlineSecond2").val()){
                                $("#landlineThird2").val(landlineSecondAjax).show();
                            }else{
                                if(landlineThirdAjax!=""&&landlineThirdAjax!=$("#landline2").val()&&landlineThirdAjax!=$("#landlineSecond2").val()){
                                    $("#landlineThird2").val(landlineThirdAjax).show();
                                }
                            }
                        }
                    } else {
                        $("#landlineThird2").val(landLineThird).show();
                    }
                    $("#fax2_span").text("").hide();
                    if (fax == '') {
                        $("#fax2").val(data.fax == null ? "" : data.fax).show();
                    } else {
                        $("#fax2").val(fax).show();
                    }
                    $("#areaInfo1_span").text("").hide();
                    $("#select_province1").val(province).show().change();
                    $("#select_city1").val(city).show().change();
                    $("#select_township1").val(region).show();
                    $("#address1_span").text("").hide();
                    if (address == '' || address == '详细地址') {
                        if (G.Lang.isEmpty(data.address)) {
                            $("#input_address1").css("color", "#7e7e7e").val('详细地址').show();
                        } else {
                            $("#input_address1").css("color", "#000000").val(data.address).show();
                        }

                    } else {
                        $("#input_address1").css("color", "#000000").val(address).show();
                    }
                    if ($("#updateBusinessScopeSpan").text().length <= 0) {
                        $("#updateBusinessScopeSpan").text(businessScope);
                        $("#updateThirdCategoryStr").val(thirdCategoryIdStr);
                    }
                    if ($("#updateVehicleModelContentSpan").text().length <= 0) {
                        $("#updateVehicleModelContentSpan").text(vehicleModelContent);
                        $("#updateVehicleModelIdStr").val(vehicleModelIdStr);
                    }
                }
                var landlineAjax=data.landLine == null ? "" : data.landLine;
                var landlineSecondAjax=data.landLineSecond == null ? "" : data.landLineSecond;
                var landlineThirdAjax=data.landLine == null ? "" : data.landLineThird;
                if (landLine == '') {
                    if(landlineAjax!=""&&landlineAjax!=landLineSecond&&landlineAjax!=landLineThird){
                        $("#landline2").val(landlineAjax).show();
                    }else{
                        if(landlineSecondAjax!=""&&landlineSecondAjax!=landLineSecond&&landlineSecondAjax!=landLineThird){
                            $("#landline2").val(landlineSecondAjax).show();
                        }else{
                            if(landlineThirdAjax!=""&&landlineThirdAjax!=landLineSecond&&landlineThirdAjax!=landLineThird){
                                $("#landline2").val(landlineThirdAjax).show();
                            }
                        }
                    }
                } else {
                    $("#landline2").val(landLine).show();
                }
                if (landLineSecond == '') {
                    if(landlineAjax!=""&&landlineAjax!=$("#landline2").val()&&landlineAjax!=landLineThird){
                        $("#landlineSecond2").val(landlineAjax).show();
                    }else{
                        if(landlineSecondAjax!=""&&landlineSecondAjax!=$("#landline2").val()&&landlineSecondAjax!=landLineThird){
                            $("#landlineSecond2").val(landlineSecondAjax).show();
                        }else{
                            if(landlineThirdAjax!=""&&landlineThirdAjax!=$("#landline2").val()&&landlineThirdAjax!=landLineThird){
                                $("#landlineSecond2").val(landlineThirdAjax).show();
                            }
                        }
                    }
                } else {
                    $("#landlineSecond2").val(landLineSecond).show();
                }
                if (landLineThird == '') {
                    if(landlineAjax!=""&&landlineAjax!=$("#landline2").val()&&landlineAjax!=$("#landlineSecond2").val()){
                        $("#landlineThird2").val(landlineAjax).show();
                    }else{
                        if(landlineSecondAjax!=""&&landlineSecondAjax!=$("#landline2").val()&&landlineSecondAjax!=$("#landlineSecond2").val()){
                            $("#landlineThird2").val(landlineSecondAjax).show();
                        }else{
                            if(landlineThirdAjax!=""&&landlineThirdAjax!=$("#landline2").val()&&landlineThirdAjax!=$("#landlineSecond2").val()){
                                $("#landlineThird2").val(landlineThirdAjax).show();
                            }
                        }
                    }
                } else {
                    $("#landlineThird2").val(landLineThird).show();
                }
                if (fax == '') {
                    $("#fax2").val(data.fax == null ? "" : data.fax).show();
                } else {
                    $("#fax2").val(fax).show();
                }

                if (bank == '') {
                    $("#bank2").val(data.bank == null ? "" : data.bank);
                } else {
                    $("#bank2").val(bank);
                }
                if (bankAccountName == '') {
                    $("#accountName2").val(data.accountName == null ? "" : data.accountName);
                } else {
                    $("#accountName2").val(bankAccountName);
                }
                if (account == '') {
                    $("#account2").val(data.account == null ? "" : data.account);
                } else {
                    $("#account2").val(account);
                }
                if (settlementType == '') {
                    $("#settlementType2").val(data.settlementTypeId == null ? "" : data.settlementTypeId);
                } else {
                    $("#settlementType2").val(settlementType);
                }
                if (invoiceCategory == '') {
                    $("#invoiceCategory2").val(data.invoiceCategoryId == null ? "" : data.invoiceCategoryId);
                } else {
                    $("#invoiceCategory2").val(invoiceCategory);
                }
                if(memo == '') {
                    $("#memo2").val(data.memo == null ? "" : data.memo);
                } else {
                    $("#memo2").val(memo);
                }
                $("#customerKind2").val( $("#customerKind").val());
                $("#birthdayString2").val($("#birthdayString").val());
            }
        });
    });
    $("#sureMerge_jy").click(function () {
        var supplierShopId = $("#supplierShopId").val();
        var customerShopId = $("#customerShopId").val();
        var isOnlineShop = G.Lang.isEmpty(supplierShopId) || G.Lang.isEmpty(customerShopId);
        //在线店铺不更新客户名，简称，座机，传真，经营产品
        if (!isOnlineShop) {
            if (!validateProvinceCity("select_province1", "select_city1")) {
                return;
            }
            if (G.Lang.isEmpty($("#name2").val())) {
                nsDialog.jAlert("用户名必须填写");
                return;
            }

            if (!checkSupplierName($("#name2").val())) {
                return;
            }
        }

        // validate mail
        var contactMails = new Array();
        $(".single_contact_gen input[name$='mail']").each(function(index){
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
        $(".single_contact_gen input[name$='qq']").each(function(index,qq){
            contactQQs.push($(this).val());
        });
        for (var index = 0; index < contactQQs.length; index++) {
            if (!G.isEmpty(contactQQs[index]) && !APP_BCGOGO.Validator.stringIsQq(contactQQs[index])) {
                nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
                return false;
            }
        }
        /* formate validate end */

        if(APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact && APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact ){
            var $contacts2Mobiles = $("input[name^='contacts2']").filter("input[name$='mobile']");
            var contacts2Mobiles = new Array();
            $contacts2Mobiles.each(function(){
                if(!G.isEmpty($(this).val())){
                    contacts2Mobiles.push($(this).val());
                }
            });

            if (isMobileDuplicate(contacts2Mobiles)) {
                return false;
            };

            if (!G.isEmpty(G.trim($("#name2").val())) && isMobilesEmpty(contacts2Mobiles) && G.isEmpty(G.trim($("#landline2").val()))) {
                if (!checkName(G.trim($("#name2").val()))) {
                    return;
                }
            }

            if (!G.isEmpty(G.trim($("#name2").val())) && !isMobilesEmpty(contacts2Mobiles)) {
                if(!validateCustomerMobiles(contacts2Mobiles,$("#customerId").val())){
                    return;
                }
                if(!validateSupplierMobiles(contacts2Mobiles,$("#supplierId").val())){
                    return;
                }
            }

            if (!G.isEmpty(G.trim($("#name2").val())) && !G.Lang.isEmpty($("#landline2").val())) {
                if (!checkSamePhone($("#mobile2").val()) || !checkSupplierPhone($("#mobile2").val())) {
                    return;
                }
            }

            // 校验主联系人信息
            var contacts = buildNormalKeyContacts2(); //TODO 这个地方的key需要修改
            var validContactCount = countValidContact(contacts);
            if(validContactCount > 3){
                nsDialog.jAlert("联系人最多只能有3个。");
                return;
            }
            if (validContactCount == 2 || validContactCount == 3) {
                if (!mainContactIsValid(contacts)) {
                    nsDialog.jAlert("请选择主联系人。");
                    return;
                }
            } else if (validContactCount == 1) {
                var index = firstValidContactFromContacts(contacts); // 只有一个联系人的时候 设为主联系人
                if (!($("#contacts2" + index + "\\.mainContact").val() == "1")) {
                    $("#contacts2" + index + "\\.mainContact").val("1");
                    var mainIndex = getMainContactFromContacts(contacts);
                    $("#contacts2" + mainIndex + "\\.mainContact").val("0");
                }
            }

        }else{
            if (!checkName($("#name2").val()) || !checkSupplierName($("#name2").val()),true) {
                return;
            }
        }


        $(this).attr("disabled","disabled");
        if($("#parentPageType").val() == 'uncleUser') {
            $("#name").val($("#name2").val());
            $("#province").val($("#select_province1").val()).change();
            $("#city").val($("#select_city1").val()).change();
            $("#region").val($("#select_township1").val());
            $("#address").val($("#input_address1").val());
        } else {
            $("#name").val($("#name2").val());
            $("#select_province").val($("#select_province1").val()).change();
            $("#select_city").val($("#select_city1").val()).change();
            $("#select_township").val($("#select_township1").val());
            $("#input_address").val($("#input_address1").val());
            $("#phone").val($("#landline2").val());
            $("#phoneSecond").val($("#landlineSecond2").val());
            $("#phoneThird").val($("#landlineThird2").val());
        }

        if(APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact && APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact ){
            $("#contacts\\[0\\]\\.id").val("0");
            $("#contacts\\[0\\]\\.name").val(" ");
            $("#contacts\\[0\\]\\.mobile").val(" ");
            $("#contacts\\[0\\]\\.email").val(" ");
            $("#contacts\\[0\\]\\.qq").val(" ");
            $("#contacts\\[0\\]\\.level").val("0");
            $("#contacts\\[0\\]\\.mainContact").val("0");

            $("#contacts\\[1\\]\\.id").val("0");
            $("#contacts\\[1\\]\\.name").val(" ");
            $("#contacts\\[1\\]\\.mobile").val(" ");
            $("#contacts\\[1\\]\\.email").val(" ");
            $("#contacts\\[1\\]\\.qq").val(" ");
            $("#contacts\\[1\\]\\.level").val("1");
            $("#contacts\\[1\\]\\.mainContact").val("1");

            $("#contacts\\[2\\]\\.id").val("0");
            $("#contacts\\[2\\]\\.name").val(" ");
            $("#contacts\\[2\\]\\.mobile").val(" ");
            $("#contacts\\[2\\]\\.email").val(" ");
            $("#contacts\\[2\\]\\.qq").val(" ");
            $("#contacts\\[2\\]\\.level").val("2");
            $("#contacts\\[2\\]\\.mainContact").val("0");


            contacts.sort(function(objects1,object2){
               if(object2.mainContact === "1"){
                  return 1;
               }else{
                   return -1;
               }
            });
            var validIndexs = getValidContactIndexs(contacts);
            if (validIndexs.length >= 1) {
                $("#contacts\\[0\\]\\.id").val(contacts[validIndexs[0]].id);
                $("#contacts\\[0\\]\\.name").val(contacts[validIndexs[0]].name);
                $("#contacts\\[0\\]\\.mobile").val(contacts[validIndexs[0]].mobile);
                $("#contacts\\[0\\]\\.email").val(contacts[validIndexs[0]].email);
                $("#contacts\\[0\\]\\.qq").val(contacts[validIndexs[0]].qq);
                $("#contacts\\[0\\]\\.level").val(contacts[validIndexs[0]].level);
                $("#contacts\\[0\\]\\.mainContact").val(contacts[validIndexs[0]].mainContact);
            }
            if (validIndexs.length >= 2) {
                $("#contacts\\[1\\]\\.id").val(contacts[validIndexs[1]].id);
                $("#contacts\\[1\\]\\.name").val(contacts[validIndexs[1]].name);
                $("#contacts\\[1\\]\\.mobile").val(contacts[validIndexs[1]].mobile);
                $("#contacts\\[1\\]\\.email").val(contacts[validIndexs[1]].email);
                $("#contacts\\[1\\]\\.qq").val(contacts[validIndexs[1]].qq);
                $("#contacts\\[1\\]\\.level").val(contacts[validIndexs[1]].level);
                $("#contacts\\[1\\]\\.mainContact").val(contacts[validIndexs[1]].mainContact);
            }
            if (validIndexs.length >= 3) {
                $("#contacts\\[2\\]\\.id").val(contacts[validIndexs[2]].id);
                $("#contacts\\[2\\]\\.name").val(contacts[validIndexs[2]].name);
                $("#contacts\\[2\\]\\.mobile").val(contacts[validIndexs[2]].mobile);
                $("#contacts\\[2\\]\\.email").val(contacts[validIndexs[2]].email);
                $("#contacts\\[2\\]\\.qq").val(contacts[validIndexs[2]].qq);
                $("#contacts\\[2\\]\\.level").val(contacts[validIndexs[2]].level);
                $("#contacts\\[2\\]\\.mainContact").val(contacts[validIndexs[2]].mainContact);
            }

        }else{
            $("#contact").val($("#contact2").val());
            $("#mobile").val($("#mobile2").val());
            $("#email").val($("#email2").val());
            $("#qq").val($("#qq2").val());
        }


        $("#shortName").val($("#abbr2").val());
        $("#landLine").val($("#landline2").val());
        $("#landLineSecond").val($("#landlineSecond2").val());
        $("#landLineThird").val($("#landlineThird2").val());
        $("#fax").val($("#fax2").val());
        $("#bank").val($("#bank2").val());
        $("#bankAccountName").val($("#accountName2").val());
        $("#account").val($("#account2").val());
        $("#settlementType").val($("#settlementType2").val());
        $("#invoiceCategory").val($("#invoiceCategory2").val());
        $("#identity").val('isSupplier');
        $("#supplierId2").val($("#supplierId").val());
        $("#id2").val($("#supplierId").val());
        $("#supplier").val($("#supplierId").val()); // TODO zhuj supplier?
        $("#customerKind").val($("#customerKind2").val());
        $("#birthdayString").val($("#birthdayString2").val());
        $("#memo").text($("#memo2").val());

        clearDefaultAddress();
        if(!$(this).attr("lock")){
            $(this).attr("lock",true);
            if($("#parentPageType").val() == 'uncleUser') {
                $("#parentPageType2Merge").val('uncleUser');
                $("#customerId2").val($("#customerId").val());
                $("#supplierForm").ajaxSubmit(function(data){
                    if(data.success) {
                        $("#modifyClientDiv").dialog("close");
                        nsDialog.jAlert("该客户与供应商绑定成功！");
                        window.location.href = 'unitlink.do?method=customer&customerId='+$("#customerId").val();
                    }
                });
            }  else {
                $("#thirdCategoryIdStr").val($("#updateThirdCategoryStr").val());

                $("#thisform").ajaxSubmit(function (data) {
                  if (data.success) {

                    $("#addSuccessDiv").dialog({
                      width: 330,
                      title: "友情提示",
                      close: function () {
                        if (G.isNotEmpty(data.data)) {
                          window.location.href = 'unitlink.do?method=customer&customerId=' + data.data;
                        } else {
                          window.location.href = "customer.do?method=customerdata";
                        }
                      }
                    });
                  }
                });

//                $("#mask", parent.document).css("display", "none");
//                $("#iframe_PopupBox", parent.document).css("display", "none");
//                setTimeout(function(){
//                    parent.location.reload();
//                }, 500);
            }
        }

    });
    $("#sureMerge_jy2").click(function () {
        var supplierName = $("#name3").val();
        if(G.Lang.isEmpty(supplierName)) {
            nsDialog.jAlert("用户名必须填写");
            return;
        }

        if(!checkSupplierName(supplierName)){
            return;
        }

        // validate mail
        var contactMails = new Array();
        $(".single_contact input[name$='mail']").each(function(index){
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
        $(".single_contact input[name$='qq']").each(function(index,qq){
            contactQQs.push($(this).val());
        });
        for (var index = 0; index < contactQQs.length; index++) {
            if (!G.isEmpty(contactQQs[index]) && !APP_BCGOGO.Validator.stringIsQq(contactQQs[index])) {
                nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
                return false;
            }
        }
        /* formate validate end */

        //既是供应商又是客户 按照客户的strict mode 校验
        if(APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact && APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact ){

            var $contacts3Mobiles = $("input[name^='contacts3']").filter("input[name$='mobile']");

            var contacts3Mobiles = new Array();
            $contacts3Mobiles.each(function(){
                if(!G.isEmpty($(this).val())){
                    contacts3Mobiles.push($(this).val());
                }
            });

            if (isMobileDuplicate(contacts3Mobiles)) {
                return false;
            }

            if(!G.isEmpty(G.trim($("#name3").val())) && isMobilesEmpty(contacts3Mobiles) && G.isEmpty(G.trim($("#landline3").val()))){
                if (!checkName(G.trim($("#name3").val())) || !checkSupplierName(supplierName)) {
                    return;
                }
            }

            if(!G.isEmpty(G.trim($("#name3").val())) && !isMobilesEmpty(contacts3Mobiles)){
                if(!validateCustomerMobiles(contacts3Mobiles,$("#customerId").val())){
                    return;
                }
                if(!validateSupplierMobiles(contacts3Mobiles,$("#supplierId").val())){
                    return;
                }
            }

            if(!G.isEmpty(G.trim($("#name3").val())) && !G.Lang.isEmpty($("#landline3").val())){
                if(!checkSamePhone($("#landline3").val())|| !checkSupplierPhone($("#landline3").val())) {
                    return;
                }
            }

            // 校验主联系人信息
            var contacts = buildNormalKeyContacts3();
            var validContactCount = countValidContact(contacts);
            if (validContactCount == 2) {
                if (!mainContactIsValid(contacts)) {
                    nsDialog.jAlert("请选择主联系人。");
                    return;
                }
            } else if (validContactCount == 1) {
                var index = firstValidContactFromContacts(contacts); // 只有一个联系人的时候 设为主联系人
                if(!($("#contacts3" + index + "\\.mainContact").val()=="1")){
                    $("#contacts3" + index + "\\.mainContact").val("1");
                    var mainIndex = getMainContactFromContacts(contacts);
                    $("#contacts3" + mainIndex + "\\.mainContact").val("0");
                }
            }

        }else{
            if(!checkName($("#name3").val()) || !checkSupplierName($("#name3").val()),true) {
                return;
            }
            if(!G.Lang.isEmpty($("#mobile3").val())) {
                if(!checkSameMobile($("#mobile3").val()) || !checkSupplierMobile($("#mobile3").val())) {
                    return;
                }
            }
        }

        //在线商铺绑定时不能修改，故不校验
        if(G.Lang.isEmpty($("#customerShopId").val()) && !validateProvinceCity("select_province2", "select_city2")){
            return;
        }
        if($("#parentPageType").val() == 'uncleUser') {
            $("#name").val($("#name3").val());
            $("#province").val($("#select_province2").val()).change();
            $("#city").val($("#select_city2").val()).change();
            $("#region").val($("#select_township2").val());
            $("#address").val($("#input_address2").val());
        }  else {
            $("#name").val($("#name3").val());
            $("#select_province").val($("#select_province2").val()).change();
            $("#select_city").val($("#select_city2").val()).change();
            $("#select_township").val($("#select_township2").val());
            $("#input_address").val($("#input_address2").val());
            $("#phone").val($("#landline3").val());
            $("#phoneSecond").val($("#landlineSecond3").val());
            $("#phoneThird").val($("#landlineThird3").val());


        }

        if(APP_BCGOGO.Permission.Web_Version_Has_Customer_Contact && APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact ){
            // 第一个联系人
            $("#contacts\\[0\\]\\.name").val($("#contacts30\\.name").val());
            $("#contacts\\[0\\]\\.mobile").val($("#contacts30\\.mobile").val());
            $("#contacts\\[0\\]\\.email").val($("#contacts30\\.email").val());
            $("#contacts\\[0\\]\\.qq").val($("#contacts30\\.qq").val());
            $("#contacts\\[0\\]\\.level").val($("#contacts30\\.level").val());
            $("#contacts\\[0\\]\\.mainContact").val($("#contacts30\\.mainContact").val());

            // 第二个联系人
            $("#contacts\\[1\\]\\.name").val($("#contacts31\\.name").val());
            $("#contacts\\[1\\]\\.mobile").val($("#contacts31\\.mobile").val());
            $("#contacts\\[1\\]\\.email").val($("#contacts31\\.email").val());
            $("#contacts\\[1\\]\\.qq").val($("#contacts31\\.qq").val());
            $("#contacts\\[1\\]\\.level").val($("#contacts31\\.level").val());
            $("#contacts\\[1\\]\\.mainContact").val($("#contacts31\\.mainContact").val());

            // 第三个联系人
            $("#contacts\\[2\\]\\.name").val($("#contacts32\\.name").val());
            $("#contacts\\[2\\]\\.mobile").val($("#contacts32\\.mobile").val());
            $("#contacts\\[2\\]\\.email").val($("#contacts32\\.email").val());
            $("#contacts\\[2\\]\\.qq").val($("#contacts32\\.qq").val());
            $("#contacts\\[2\\]\\.level").val($("#contacts32\\.level").val());
            $("#contacts\\[2\\]\\.mainContact").val($("#contacts32\\.mainContact").val());
        }else{
            $("#contact").val($("#contact3").val());
            $("#mobile").val($("#mobile3").val());
            $("#email").val($("#email3").val());
            $("#qq").val($("#qq3").val());
        }


        $("#shortName").val($("#abbr3").val());
       $("#landLine").val($("#landline3").val());
        $("#landLineSecond").val($("#landlineSecond3").val());
        $("#landLineThird").val($("#landlineThird3").val());
        $("#fax").val($("#fax3").val());
        $("#bank").val($("#bank3").val());
        $("#bankAccountName").val($("#accountName3").val());
        $("#account").val($("#account3").val());
        $("#settlementType").val($("#settlementType3").val());
        $("#invoiceCategory").val($("#invoiceCategory3").val());
        $("#identity").val('isSupplier');
        $("#supplierId2").val('');
        $("#id2").val('');
        $("#memo").text($("#memo3").val());

        clearDefaultAddress();
        if(!$(this).attr("lock")){
            $(this).attr("lock",true);
            if($("#parentPageType").val() == 'uncleUser') {
                $("#parentPageType2Add").val('uncleUserAdd'); // add by zhuj 标识是新增客户 联系人填充的时候key以*3*获取
                $("#customerId3").val($("#customerId").val());
               $("#supplierFormAdd #thirdCategoryIdStr").val($("#newThirdCategoryStr").val());
                $("#supplierFormAdd").ajaxSubmit(function(data){
                    if(data.length > 0) {
                        $("#modifyClientDiv").dialog("close");
                        nsDialog.jAlert("该客户与供应商绑定成功！");
                        window.location.href = 'unitlink.do?method=customer&customerId='+$("#customerId").val();
                    }
                });
            } else {
                $("#thirdCategoryIdStr").val($("#newThirdCategoryStr").val());

              $("#thisform").ajaxSubmit(function (data) {

                $("#addSuccessDiv").dialog({
                  width: 330,
                  title: "友情提示",
                  close: function () {
                    if (G.isNotEmpty(data.data)) {
                      window.location.href = 'unitlink.do?method=customer&customerId=' + data.data;
                    } else {
                      window.location.href = "customer.do?method=customerdata";
                    }
                  }
                });
              });

            }
        }


    });

    provinceBind1();

    $("#select_province1,#select_province2").bind("change", function () {
        cityBind1(this);
    });
    $("#select_city1,#select_city2").bind("change", function () {
        townshipBind1(this);
    });


    $("#cancel,#cancel2").click(function () {
        // add by zhuj清除联系人div里面带入的相关联系人信息
        /*$(".single_contact input[name^='contacts2']").each(function () {
            $(this).val("");
        });*/
        $(".single_contact_gen").remove();
        $(".single_contact input[name^='contacts3']").each(function () {
            $(this).val("");
        });
        $(".warning").hide();
        $("#modifyClientDiv").dialog("close");
        $("#alsoSupplier").attr("checked",false);
    });


    $("#prev").click(function () {
        if ($("#addOrExist").val() == 'exist') {
            $("#radExist").click();
            $(".select_supplier").css("display", "block");
        } else {
            $("#radAdd").click();
            $(".select_supplier").css("display", "block");
        }

        // add by zhuj 联系人信息删除
        $(".single_contact_gen").remove();
        $(".single_contact input[name^='contacts3']").each(function () {
            $(this).val("");
        });
        $(".warning").hide();
        $("#modifyClientDiv #newBusinessScopeSpan").text($("#secondCategoryName").val());
        $("#modifyClientDiv #updateBusinessScopeSpan").text($("#secondCategoryName").val());

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
    });


    $("[action-type=search-supplier][page=modifyClient]").click(function () {
        var $supplierInfoText = $("#supplierInfoText"), searchWord = $supplierInfoText.val(),
            initialValue = $supplierInfoText.attr("initialValue");
        $("#supplierId").val("");
        $("#supplierShopId").val("");
        var ajaxData = {
            searchWord: searchWord === initialValue ? "" : searchWord,
            maxRows: $("#pageRows").val(),
            customerOrSupplier: "supplier",
            filterType: "identity"
        };
        var ajaxUrl = "supplier.do?method=searchSupplierDataAction";
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.ajaxQuery(function (data) {
            initTr(data);
            initPages(data, "supplierSuggest", "supplier.do?method=searchSupplierDataAction", '', "initTr", '', '',ajaxData,'');
        });
    });

    $("#mobile2,#mobile3").blur(function() {
        checkSameMobile($(this).val());
    });
    $("#name2,#name3").blur(function(){
        checkName($(this).val(),true);
    });

    $(".J_connector").live("mouseover", function(){
        var supplierId = $(this).attr("supplierId");
        $(".J_prompt").hide();
        $(".J_prompt[supplierId='"+supplierId+"']").css({
            left:this.offsetLeft + 'px'
        }).show();
    }).live("mouseout", function(){
        var supplierId = $(this).attr("supplierId");
        $(".J_prompt[supplierId='"+supplierId+"']").hide();
    });

    $("#mobile2,#qq2,#account2,#mobile3,#qq3,#account3").keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });
});

//第一级菜单 select_province
function provinceBind1() {
    var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
        data: {parentNo: 1}, dataType: "json"});
    if (!r || r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            if($("#select_province1").length > 0) {
                $("#select_province1")[0].appendChild(option);
            }
        }
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            if($("#select_province2").length > 0) {
                $("#select_province2")[0].appendChild(option);
            }

        }
    }
}

//第二级菜单 select_city
function cityBind1(select) {
    while ($("#select_city1")[0].options.length > 1) {
        $("#select_city1")[0].remove(1);
    }
    while ($("#select_city2")[0].options.length > 1) {
        $("#select_city2")[0].remove(1);
    }
    while ($("#select_township1")[0].options.length > 1) {
        $("#select_township1")[0].remove(1);
    }
    while ($("#select_township2")[0].options.length > 1) {
        $("#select_township2")[0].remove(1);
    }
    if (select.selectedIndex != 0) {
        var r = APP_BCGOGO.Net.syncGet({"url": "shop.do?method=selectarea&parentNo=" + select.value, "dataType": "json"});
        if (r === null) {
            return;
        }
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_city1")[0].appendChild(option);
            }
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_city2")[0].appendChild(option);
            }
        }
    }
}

//第三级菜单 select_township
function townshipBind1(select) {
    if (select.selectedIndex == 0) {
        return;
    }
    var r = APP_BCGOGO.Net.syncGet({"url": "shop.do?method=selectarea&parentNo=" + select.value, "dataType": "json"});
    if (r === null || typeof(r) == "undefined") {
        return;
    }
    else {
        while ($("#select_township1")[0].options.length > 1) {
            $("#select_township1")[0].remove(1);
        }
        while ($("#select_township2")[0].options.length > 1) {
            $("#select_township2")[0].remove(1);
        }
        if (typeof(r) != "undefined" && r.length > 0) {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_township1")[0].appendChild(option);
            }
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_township2")[0].appendChild(option);
            }
        }
    }
}

function initSupplierTr(data) {
    $("#supplierDatas tr").not(":first").remove();
    $("#totalRows").val(data.numFound);
    for (var i = 0; i < data.supplierDTOs.length; i++) {
        var tr = '<tr><td><input type="radio" value="' + data.supplierDTOs[i].idStr + '" name="supplier"/></td><td>';
        tr += data.supplierDTOs[i].name;
        tr += '<a class="connecter"></a></td><td></td><td>';
        if (data.supplierDTOs[i].address != null) {
            tr += data.supplierDTOs[i].address;
        }
        tr += '</td>';
        $("#supplierDatas").append($(tr));
    }
}
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

function checkSupplierMobile(mobile) {
    //要判断是否存在同名的手机号
    var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getSupplierJsonDataByMobile",
        data:{mobile:mobile},dataType:"json"});
    if (r && r.name) {
        var supplierId = r.idString;
        if (supplierId != "" && supplierId != null && supplierId != $("#supplierId").val()) {
            nsDialog.jAlert("与已存在供应商【"+ r.name+"】的手机号相同，请重新输入");
            return false;
        } else {
            return true;
        }
    } else {
        return true;
    }
}


function checkSupplierPhone(phone){
    //要判断是否存在同名的手机号
    var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getSupplierJsonDataByTelephone",
        data:{telephone:phone},dataType:"json"});
    if (r && r.name) {
        var supplierId = r.idString;
        if (supplierId != "" && supplierId != null && supplierId != $("#supplierId").val()) {
            nsDialog.jAlert("与已存在供应商【"+ r.name+"】的座机号相同，请重新输入");
            return false;
        } else {
            return true;
        }
    } else {
        return true;
    }
}

function checkSupplierName(name) {
    var result = false;
    APP_BCGOGO.Net.syncPost({
        url:"RFSupplier.do?method=getSupplierByNameAndShopId",
        data:{
            name:name
        },
        cache:false,
        dataType:"json",
        success:function(jsonStr) {
            if(!jsonStr.exactMatch){
                result = true;
                return;
            }
            //存在重复
            if (jsonStr!= null && jsonStr.supplierDTO!=null) {
                var supplierId = jsonStr.supplierDTO.idString;
                if (supplierId != "" && supplierId != null && supplierId != $("#supplierId").val()) {
                    nsDialog.jAlert("供应商名【" + name + "】已存在，请重新输入");
                    result = false;
                } else {
                    result = true;
                }
            } else {
                result = true;
            }
        }
    });
    return result;
}


function checkSameMobile(mobile) {
    //要判断是否存在同名的手机号
    var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomerJsonDataByMobile",
        data:{mobile:mobile},dataType:"json"});
    if (r.success && r.data) {
        var obj = r.data;
        var customerId = obj.idStr;
        if (customerId != "" && customerId != null && customerId != $("#customerId").val()) {
            nsDialog.jAlert("与已存在客户【"+ obj.name+"】的手机号相同，请重新输入");
            return false;
        } else {
            return true;
        }
    } else {
        return true;
    }
}

function checkSamePhone(phone){
    //要判断是否存在同名的手机号
    var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomerJsonDataByTelephone",
        data:{telephone:phone},dataType:"json"});
    if (r && r.name) {
        var customerId = r.idStr;
        if (customerId != "" && customerId != null && customerId != $("#customerId").val()) {
            nsDialog.jAlert("与已存在客户【"+ r.name+"】的座机号相同，请重新输入");
            return false;
        } else {
            return true;
        }
    } else {
        return true;
    }
}

function checkName(name) {
    var result = false;
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
                var customerIds = new Array();
                for(index in jsonStr.results){
                    if(!G.isEmpty(jsonStr.results[index])){
                        customerIds.push(jsonStr.results[index].idStr);
                    }
                }
                if(customerIds.length == 1){
                    var customerId = customerIds[0];
                    if (customerId != "" && customerId != null && customerId != $("#customerId").val()) {
                        nsDialog.jAlert("客户名【" + name + "】已存在，请重新输入");
                        result = false;
                    } else {
                        result = true;
                    }
                }
                if(customerIds.length > 1){
                    nsDialog.jAlert("客户名【" + name + "】已存在，请重新输入");
                    result = false;
                }

            } else {
                result = true;
            }
        }
    });
    return result;
}

function validateProvinceCity(provinceSelectId, citySelectId){
    if(!$("#"+provinceSelectId).val() || $("#"+provinceSelectId).val()<0){
        nsDialog.jAlert("请选择省份!");
        return false;
    }
    //1032,1033 过滤掉香港和澳门
    if((!$("#"+citySelectId).val() || $("#"+citySelectId).val()<0) &&  $("#"+provinceSelectId).val() != 1032 &&  $("#"+provinceSelectId).val() != 1033){

        nsDialog.jAlert("请选择城市!");
        return false;
    }
    return true;
}

// 判断当前的联系人手机号信息是否为空
function isMobilesEmpty(contactMobiles){
    if(G.isEmpty(contactMobiles)){
        return true;
    }
    if(contactMobiles.constructor == Array){
        return contactMobiles.every(function(item,index,obj){
            return G.isEmpty(item);
        });
    }
    return true;
}

/**
 * 判断列表中的手机是否重复
 * 重复返回true 否则false
 * @param mobiles
 * @returns {boolean}
 */
function isMobileDuplicate(mobiles) {
    var mobilesTemp = new Array();
    for (var index in  mobiles) {
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

