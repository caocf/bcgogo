var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$(function(){
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
    $("[action-type=search-customer][page=modifySupplier]").click(function() {
        var $customerInfoText = $("#customerInfoText"), searchWord = $customerInfoText.val(),
            initialValue = $customerInfoText.attr("initialValue");
        $("#customerId").val("");
        var ajaxData = {
            searchWord: searchWord === initialValue ? "" : searchWord,
            maxRows: $("#pageRows").val(),
            customerOrSupplier: "customer",
            filterType: "identity"
        };
        var ajaxUrl = "customer.do?method=searchCustomerDataAction";
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.ajaxQuery(function(data) {
            initTr(data);
            initPages(data, "customerSuggest", "customer.do?method=searchCustomerDataAction", '', "initTr", '', '',ajaxData,'');
        });
    });
    $(".tabRecord tr").not(".tabTitle").css({"border":"1px solid #bbbbbb","border-width":"1px 0px"});
    $(".tabRecord tr:nth-child(odd)").not(".tabTitle").css("background","#eaeaea");

    $(".tabRecord tr").not(".tabTitle").hover(
        function () {
            $(this).find("td").css({"background":"#fceba9","border":"1px solid #ff4800","border-width":"1px 0px"});

            $(this).css("cursor","pointer");
        },
        function () {
            $(this).find("td").css({"background-Color":"#FFFFFF","border":"1px solid #bbbbbb","border-width":"1px 0px 0px 0px"});
            $(".tabRecord tr:nth-child(odd)").not(".tabTitle" ).find("td").css("background","#eaeaea");
        }
    );
    $("#birthdayString2,#birthdayString3").datepicker({
        "numberOfMonths" : 1,
        "changeYear":true,
        "changeMonth":true,
        "dateFormat": "mm-dd",
        "yearRange": "c-100, c",
        "yearSuffix":"",
        "showButtonPanel":true
    });
    $("#exist").attr("checked", true);
    $("#customerDatas :radio").live("click",function(){
        $("#customerId").val($(this).val());
    });
    $(".add_supplier").hide();
    $("#mergeInfo").hide();
    $("#radExist").live("click",function(){
        $(".exist_suppliers").css("display","block");
        $(".add_supplier").css("display","none");
        $("#customerId").val($("#customerDatas :radio[checked]").val());
    });
    $("#radAdd").live("click",function(){
        $("#customerId").val("");
        $("#customerShopId").val("");
        $(".exist_suppliers").css("display","none");
        $(".add_supplier").css("display","block");
        $("#mergeInfo").css("display","none");
        $(".table_inputContact").show();
        var address = "";
        // 供应商详细页面的js
        if($("#pageType").val() == 'uncleSupplier') {
            address = $("#address").val();

            if (!G.Lang.isEmpty($("#supplierShopId").val())) {
                var name = $("#name").val();
                var abbr = $("#abbr").val();
                var landLine = $("#landline").val();
                var landLineSecond=$("#landlineSecond").val();
                var landLineThird=$("#landlineThird").val();
                var fax = $("#fax").val();
                var province = $("#province").val();
                var city = $("#city").val();
                var region = $("#region").val();
                var areaInfo = $("#areaInfo").text();
                $("#name3").val(name).hide();
                $("#name3_span").text(name).show();
                $("#abbr3").val(abbr).hide();
                $("#abbr3_span").text(abbr).show();
                $("#landline3").val(landLine).hide();
                $("#landline3_span").text(landLine).show();
                $("#landlineSecond3").val(landLineSecond).hide();
                $("#landlineSecond3_span").text(landLineSecond).show();
                $("#landlineThird3").val(landLineThird).hide();
                $("#landlineThird3_span").text(landLineThird).show();
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
                $("#name3").val($("#name").val()).show();
                $("#name3_span").text("").hide();
                $("#abbr3").val($("#abbr").val()).show();
                $("#abbr3_span").text("").hide();
                $("#landline3").val($("#landLine").val()).show();
                $("#landLine3_span").text("").hide();
                $("#landlineSecond3").val($("#landLineSecond").val()).show();
                $("#landLineSecond3_span").text("").hide();
                $("#landlineThird3").val($("#landLineThird").val()).show();
                $("#landLineThird3_span").text("").hide();
                $("#fax3").val($("#fax").val()).show();
                $("#fax3_span").text("").hide();
                $("#areaInfo2_span").val("").hide();
                $("#select_province2").val($("#province").val()).show().change();
                $("#select_city2").val($("#city").val()).show().change();
                $("#select_township2").val($("#region").val()).show().change();
                $("#address2_span").val("").hide();
                if (address == '' || address == '详细地址') {
                    $("#input_address2").css("color", "#7e7e7e").val('详细地址').show();
                } else {
                    $("#input_address2").css("color", "#000000").val(address).show();
                }
            }
            $("#bank3").val($("#bank").val());
            $("#accountName3").val($("#accountName").val());
            $("#account3").val($("#account").val());
        }   else {
            $("#name3").val($("#supplier").val());
            $("#abbr3").val($("#abbr").val());
            $("#landline3").val($("#landline").val());
            $("#landlineSecond3").val($("#landlineSecond").val());
            $("#landlineThird3").val($("#landlineThird").val());
            $("#fax3").val($("#fax").val());
            $("#bank3").val($("#bank").val());
            $("#accountName3").val($("#accountName").val());
            $("#account3").val($("#account").val());

            $("#select_province2").val($("#select_province").val());
            $("#select_province2").change();
            $("#select_city2").val($("#select_city").val());
            $("#select_city2").change();
            $("#select_township2").val($("#select_township").val());
            $("#select_township2").change();
            address = $("#input_address").val();
        }

        if (APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact) {
            // 填充联系人
            // 要分页面情况??
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
                var $thisTr = $(this).closest("tr");
                var $contact = $thisTr.find("a[class*='connacter']");
                $contact.removeClass("icon_connacter").addClass("hover").removeClass("icon_grayconnacter");
                var $alert = $thisTr.find("div .alert");
                if ($(this).val() === "1") {
                    $contact.addClass("icon_connacter");
                    if ($alert) {
                        $thisTr.remove($alert);
                    }
                } else {
                    $contact.addClass("icon_grayconnacter");
                    if (!$alert) {
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
            if ($("#pageType").val() == 'uncleSupplier') {
                $("#contact3").val($("#contact").val());
                $("#mobile3").val($("#mobile").val());
                $("#qq3").val($("#qq").val());
                $("#email3").val($("#email").val());
            } else {
                $("#contact3").val($("#contact").val());
                $("#mobile3").val($("#mobile").val());
                $("#email3").val($("#email").val());
                $("#qq3").val($("#qq").val());
            }
        }


        $("#memo3").val($("#memo").val());
        var  settlementType = $("#settlementTypeId").val();
        var invoiceCategory = $("#invoiceCategoryId").val();

        $("#settlementType3").val(settlementType);
        $("#invoiceCategory3").val(invoiceCategory);

        if(address == '' || address == '详细地址') {
            $("#input_address2").css("color","#7e7e7e");
            $("#input_address2").val('详细地址');
        } else {
            $("#input_address2").css("color","#000000");
            $("#input_address2").val(address);
        }

    });
    $("#merge").click(function(){
        var customerId = $("#customerId").val();
        if (GLOBAL.Lang.isStringEmpty(customerId)) {
            nsDialog.jAlert("请选择客户!");
            return;
        }
        var customerShopId = $("#customerDatas :radio[checked]").attr("customerOrSupplierShopId");
        var supplierShopId = $("#supplierShopId").val();
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
            url:"customer.do?method=getCustomerById",
            dataType:"json",
            data:{
                customerId: customerId
            },
            success:function(data) {
                if($("#pageType").val() == 'uncleSupplier') {
                    var name = $("#name").val();
                    var province = $("#province").val();
                    var city = $("#city").val();
                    var region = $("#region").val();
                    var address = $("#address").val();
                    var abbr = $("#abbr").val();
                    var phone = $("#landLine").val();
                    var phoneSecond=$("#landLineSecond").val();
                    var phoneThird=$("#landLineThird").val();
                    var fax = $("#fax").val();
                    var bank = $("#bank").val();
                    var bankAccountName = $("#accountName").val();
                    var account = $("#account").val();
                    var areaInfo = $("#areaInfo").text();
                }   else {
                    var name = $("#supplier").val();
                    var province = $("#select_province").val();
                    var city = $("#select_city").val();
                    var region = $("#select_township").val();
                    var address = $("#input_address").val();
                    var abbr = $("#abbr").val();
                    var phone = $("#landline").val();
                    var phoneSecond=$("#landlineSecond").val();
                    var phoneThird=$("#landlineThird").val();
                    var fax = $("#fax").val();
                    var bank = $("#bank").val();
                    var bankAccountName = $("#accountName").val();
                    var account = $("#account").val();

                }

                // 填充多联系人信息
                if(APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact){

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

                    // ---  一下为server端查询到的联系人列表信息 --//
                    if(data.contacts){
                        if(data.contacts[0]){
                            var contact23_id = G.normalize(data.contacts[0].idStr);
                            var contact23_name = G.normalize(data.contacts[0].name);
                            var contact23_mobile = G.normalize(data.contacts[0].mobile);
                            var contact23_email = G.normalize(data.contacts[0].email);
                            var contact23_qq = G.normalize(data.contacts[0].qq);
                            var contact23_level = G.normalize(data.contacts[0].level);
                            var contact23_mainContact = G.normalize(data.contacts[0].mainContact);
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
                    }

                    if(data.contacts[1]){
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
                        };
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
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].mobile\" maxlength=\"11\"  id=\"contacts2" + index + ".mobile\" value=\"" + contacts[index].mobile + "\" style=\"width:83px;\" /></td>"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].email\" id=\"contacts2" + index + ".email\"  value=\"" + contacts[index].email + "\" style=\"width:90px;\" /></td>"
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].qq\" id=\"contacts2" + index + ".qq\"  value=\"" + contacts[index].qq + "\" /></td>"
                                + "<td><input type=\"hidden\" name=\"contacts2[" + index + "].level\" id=\"contacts2" + index + ".level\" value=\"" + index + "\"/>";
                            if (contacts[index].mainContact === "1") {
                                toBeContactStr += "<a class=\"icon_connacter\"></a><a class=\"close\"></a>";
                            } else {
                                toBeContactStr += "<a class=\"icon_grayconnacter hover\"></a><a class=\"close\"></a><div class=\"alert\"><span class=\"arrowTop\"></span><div class=\"alertAll\"> <div class=\"alertLeft\"></div><div class=\"alertBody\">点击设为主联系人</div><div class=\"alertRight\"></div></div></div>";
                            }
                            toBeContactStr+="<input type=\"hidden\" name=\"contacts2[" + index + "].mainContact\" id=\"contacts2" + index + ".mainContact\" value=\"" + contacts[index].mainContact + "\"/></td>"
                            $contacts = $(toBeContactStr);
                            $(".table_contact_gen").find("tr:last").after($contacts);
                        }
                        // 补充空的联系人
                        if (contacts.length === 1 || contacts.length === 2) {
                            if (contacts.length === 1) {
                                var toBeContactStr = "<tr class=\"single_contact_gen\">"
                                    + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + 1 + "].name\" id=\"contacts2" + 1 + ".name\" value=\"\"/></td>"
                                    + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + 1 + "].mobile\"  maxlength=\"11\" id=\"contacts2" + 1 + ".mobile\" value=\"\" style=\"width:83px;\" /></td>"
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
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + 2 + "].mobile\"  maxlength=\"11\" id=\"contacts2" + 2 + ".mobile\" value=\"\" style=\"width:83px;\" /></td>"
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
                                + "<td><input type=\"text\" class=\"txt\" name=\"contacts2[" + index + "].mobile\"  maxlength=\"11\"  id=\"contacts2" + index + ".mobile\" value=\"\" style=\"width:83px;\" /></td>"
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
                    var contact ;
                    var mobile;
                    var email;
                    var qq;
                    if ($("#pageType").val() == 'uncleSupplier') {
                        contact = $("#contact").val();
                        mobile = $("#mobile").val();
                        email = $("#email").val();
                        qq = $("#qq").val();
                    } else {
                        contact = $("#contact").val();
                        mobile = $("#mobile").val();
                        email = $("#email").val();
                        qq = $("#qq").val();
                    }
                    if (contact == '') {
                        $("#contact2").val(data.contact == null ? "" : data.contact);
                    } else {
                        $("#contact2").val(contact);
                    }
                    if (mobile == '') {
                        $("#mobile2").val(data.mobile == null ? "" : data.mobile);
                    } else {
                        $("#mobile2").val(mobile);
                    }
                    if (email == '') {
                        $("#email2").val(data.email == null ? "" : data.email);
                    } else {
                        $("#email2").val(email);
                    }
                    if (qq == '') {
                        $("#qq2").val(data.qq == null ? "" : data.qq);
                    } else {
                        $("#qq2").val(qq);
                    }
                }


                var thirdCategoryIdStr = data.thirdCategoryIdStr;
                var businessScope = data.businessScopeStr;
                var vehicleModelContent = data.vehicleModelContent;
                var vehicleModelIdStr = data.vehicleModelIdStr;

                var  settlementType = $("#settlementTypeId").val();
                var invoiceCategory = $("#invoiceCategoryId").val();
                var memo = $("#memo").val();
                //供应商是在线店铺
                if(!G.Lang.isEmpty(supplierShopId)){
                    $("#name2_span").show().text(name);
                    $("#name2").hide().val(name);
                    $("#abbr2").val(abbr).hide();
                    $("#abbr2_span").text(abbr).show();
                    $("#landline2").val(phone).hide();
                    $("#landLine2_span").text(phone).show();
                    $("#landlineSecond2").val(phoneSecond).hide();
                    $("#landLineSecond2_span").text(phoneSecond).show();
                    $("#landlineThird2").val(phoneThird).hide();
                    $("#landLineThird2_span").text(phoneThird).show();
                    if(address != '详细地址'){
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

                } else if (!G.Lang.isEmpty(customerShopId)) {     //客户是在线店铺
                    $("#name2_span").show().text(G.normalize(data.name));
                    $("#name2").hide().val(G.normalize(data.name));
                    $("#abbr2").val(G.normalize(data.shortName)).hide();
                    $("#abbr2_span").text(G.normalize(data.shortName)).show();
                    $("#input_address1").val(G.normalize(data.address)).hide();
                    $("#address1_span").text(G.normalize(data.address)).show();

                    $("#landline2").val(G.normalize(data.landLine)).hide();
                    $("#landline2_span").text(G.normalize(data.landLine)).show();
                    $("#landlineSecond2").val(G.normalize(data.landLineSecond)).hide();
                    $("#landlineSecond2_span").text(G.normalize(data.landLineSecond)).show();
                    $("#landlineThird2").val(G.normalize(data.landLineThird)).hide();
                    $("#landlineThird2_span").text(G.normalize(data.landLineThird)).show();

                    $("#select_province1").val(G.normalize(data.province)).hide().change();
                    $("#select_city1").val(G.normalize(data.city)).hide().change();
                    $("#select_township1").val(G.normalize(data.region)).hide();
                    $("#areaInfo1_span").text(G.normalize(data.areaInfo)).show();
                    $("#updateBusinessScopeSpan").text(businessScope);
                    $("#updateThirdCategoryStr").val(thirdCategoryIdStr);
                    $("#updateVehicleModelContentSpan").text(vehicleModelContent);
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
                        $("#abbr2").val(data.shortName == null ? "" : data.shortName).show();
                    } else {
                        $("#abbr2").val(abbr).show();
                    }
                    $("#landLine2_span").text("").hide();
                    var landlineAjax=data.landLine == null ? "" : data.landLine;
                    var landlineSecondAjax=data.landLineSecond == null ? "" : data.landLineSecond;
                    var landlineThirdAjax=data.landLine == null ? "" : data.landLineThird;
                    if (phone == '') {
                        if(landlineAjax!=""&&landlineAjax!=phoneSecond&&landlineAjax!=phoneThird){
                            $("#landline2").val(landlineAjax).show();
                        }else{
                            if(landlineSecondAjax!=""&&landlineSecondAjax!=phoneSecond&&landlineSecondAjax!=phoneThird){
                                $("#landline2").val(landlineSecondAjax).show();
                            }else{
                                if(landlineThirdAjax!=""&&landlineThirdAjax!=phoneSecond&&landlineThirdAjax!=phoneThird){
                                    $("#landline2").val(landlineThirdAjax).show();
                                }
                            }
                        }

                    } else {
                        $("#landline2").val(phone).show();
                    }
                    $("#landLineSecond2_span").text("").hide();
                    if (phoneSecond == '') {
                        if(landlineAjax!=""&&landlineAjax!=$("#landline2").val()&&landlineAjax!=phoneThird){
                            $("#landlineSecond2").val(landlineAjax).show();
                        }else{
                            if(landlineSecondAjax!=""&&landlineSecondAjax!=$("#landline2").val()&&landlineSecondAjax!=phoneThird){
                                $("#landlineSecond2").val(landlineSecondAjax).show();
                            }else{
                                if(landlineThirdAjax!=""&&landlineThirdAjax!=$("#landline2").val()&&landlineThirdAjax!=phoneThird){
                                    $("#landlineSecond2").val(landlineThirdAjax).show();
                                }
                            }
                        }
                    } else {
                        $("#landlineSecond2").val(phoneSecond).show();
                    }
                    $("#landLineThird2_span").text("").hide();
                    if (phoneThird == '') {
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
                        $("#landlineThird2").val(phoneThird).show();
                    }
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
                        $("#supplierForm #thirdCategoryIdStr").val(thirdCategoryIdStr);

                    }
                    if ($("#updateVehicleModelContentSpan").text().length <= 0) {
                        $("#updateVehicleModelContentSpan").text(vehicleModelContent);
                        $("#updateVehicleModelIdStr").val(vehicleModelIdStr);
                    }

                    $("#areaInfo1_span").text("").hide();
                    $("#select_province1").val(province).change().show();
                    $("#select_city1").val(city).change().show();
                    $("#select_township1").val(region).show();
                }


                if(bank == '') {
                    $("#bank2").val(data.bank == null ? "" : data.bank);
                }  else {
                    $("#bank2").val(bank);
                }
                var landlineAjax=data.landLine == null ? "" : data.landLine;
                var landlineSecondAjax=data.landLineSecond == null ? "" : data.landLineSecond;
                var landlineThirdAjax=data.landLine == null ? "" : data.landLineThird;
                if(phone == '') {
                    if(landlineAjax!=""&&landlineAjax!=phoneSecond&&landlineAjax!=phoneThird){
                        $("#landline2").val(landlineAjax).show();
                    }else{
                        if(landlineSecondAjax!=""&&landlineSecondAjax!=phoneSecond&&landlineSecondAjax!=phoneThird){
                            $("#landline2").val(landlineSecondAjax).show();
                        }else{
                            if(landlineThirdAjax!=""&&landlineThirdAjax!=phoneSecond&&landlineThirdAjax!=phoneThird){
                                $("#landline2").val(landlineThirdAjax).show();
                            }
                        }
                    }
                }  else {
                    $("#landLine2").val(phone);
                }
                if(phoneSecond == '') {
                    if(landlineAjax!=""&&landlineAjax!=$("#landline2").val()&&landlineAjax!=phoneThird){
                        $("#landlineSecond2").val(landlineAjax).show();
                    }else{
                        if(landlineSecondAjax!=""&&landlineSecondAjax!=$("#landline2").val()&&landlineSecondAjax!=phoneThird){
                            $("#landlineSecond2").val(landlineSecondAjax).show();
                        }else{
                            if(landlineThirdAjax!=""&&landlineThirdAjax!=$("#landline2").val()&&landlineThirdAjax!=phoneThird){
                                $("#landlineSecond2").val(landlineThirdAjax).show();
                            }
                        }
                    }
                }  else {
                    $("#landLineSecond2").val(phoneSecond);
                }
                if(phoneThird == '') {
                    if(landlineAjax!=""&&landlineAjax!=$("#landline2").val()&&landlineAjax!=$("#landlineSecond2").val()){
                        $("#landlineThird2").val(landlineAjax).show();
                    }else{
                        if(landlineSecondAjax!=""&&landlineSecondAjax!=$("#landline2").val()&&$("#landlineSecond2").val()){
                            $("#landlineThird2").val(landlineSecondAjax).show();
                        }else{
                            if(landlineThirdAjax!=""&&landlineThirdAjax!=$("#landline2").val()&&$("#landlineSecond2").val()){
                                $("#landlineThird2").val(landlineThirdAjax).show();
                            }
                        }
                    }
                }  else {
                    $("#landLineThird2").val(phoneThird);
                }
                if (fax == '') {
                    $("#fax2").val(data.fax == null ? "" : data.fax);
                } else {
                    $("#fax2").val(fax);
                }
                if(bankAccountName == '') {
                    $("#accountName2").val(data.bankAccountName == null ? "" : data.bankAccountName);
                } else {
                    $("#accountName2").val(bankAccountName);
                }
                if(account == '') {
                    $("#account2").val(data.account == null ? "" : data.account);
                } else {
                    $("#account2").val(account);
                }
                if(settlementType == '') {
                    $("#settlementType2").val(data.settlementType == null ? "" : data.settlementType);
                } else {
                    $("#settlementType2").val(settlementType);
                }
                if(invoiceCategory == '') {
                    $("#invoiceCategory2").val(data.invoiceCategory == null ? "" : data.invoiceCategory);
                } else {
                    $("#invoiceCategory2").val(invoiceCategory);
                }

                if(memo == '') {
                    $("#memo2").val(data.memo == null ? "" : data.memo);
                } else {
                    $("#memo2").val(memo);
                }
                $("#birthdayString3").val(data.birthdayString);
                $("#customerKind3").val(data.customerKind == null ? "" : data.customerKind);


            }

        });
    });
    $("#sureMerge_jy").click(function(){
        if(!validateProvinceCity("select_province1", "select_city1")){
            return;
        }
        if(G.Lang.isEmpty($("#name2").val())) {
            nsDialog.jAlert("用户名必须填写");
            return;
        }
        if (!checkName($("#name2").val())) {
            return;
        }

        if (!G.Lang.isEmpty($("#mobile2").val())) {
            if (!checkSameMobile($("#mobile2").val())|| !checkCustomerMobile($("#mobile2").val())) {
                return;
            }
        }

        if($("#parentPageType").val() == 'uncleSupplier') {
            $("#name").val($("#name2").val());
            $("#province").val($("#select_province1").val()).change();
            $("#city").val($("#select_city1").val()).change();
            $("#region").val($("#select_township1").val());
            $("#address").val($("#input_address1").val());
            $("#abbr").val($("#abbr2").val());
            $("#landline").val($("#landline2").val());
            $("#landlineSecond").val($("#landlineSecond2").val());
            $("#landlineThird").val($("#landlineThird2").val());
            $("#fax").val($("#fax2").val());
            $("#bank").val($("#bank2").val());
            $("#accountName").val($("#accountName2").val());
            $("#account").val($("#account2").val());
        }  else {
            $("#supplier").val($("#name2").val());
            $("#select_province").val($("#select_province1").val()).change();
            $("#select_city").val($("#select_city1").val()).change();
            $("#select_township").val($("#select_township1").val());
            $("#input_address").val($("#input_address1").val());
            $("#abbr").val($("#abbr2").val());
            $("#landline").val($("#landline2").val());
            $("#landlineSecond").val($("#landlineSecond2").val());
            $("#landlineThird").val($("#landlineThird2").val());
            $("#fax").val($("#fax2").val());
            $("#bank").val($("#bank2").val());
            $("#accountName").val($("#accountName2").val());
            $("#account").val($("#account2").val());
        }

        var contactMsgFlag,contactMsgFlagSecond,contactMsgFlagThird;
        if (APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact) {

            // validate mail
            var contactMails = new Array();
            $(".single_contact_gen input[name$='mail']").each(function(){
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
            $(".single_contact_gen input[name$='qq']").each(function(){
                contactQQs.push($(this).val());
            });
            for (var index = 0; index < contactQQs.length; index++) {
                if (!G.isEmpty(contactQQs[index]) && !APP_BCGOGO.Validator.stringIsQq(contactQQs[index])) {
                    nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
                    return false;
                }
            }
            /* formate validate end */

            var $contacts2Mobiles = $(".single_contact_gen input[name$='mobile']");
            var contacts2Mobiles = new Array();
            $contacts2Mobiles.each(function(){
                if(!G.isEmpty($(this).val())){
                    contacts2Mobiles.push($(this).val());
                }
            });

            if (isMobileDuplicate(contacts2Mobiles)) {
                return false;
            };

            contactMsgFlag = isMobilesEmpty(contacts2Mobiles) && G.isEmpty(G.trim($("#landline2").val()));
            contactMsgFlagSecond = isMobilesEmpty(contacts2Mobiles) && G.isEmpty(G.trim($("#landlineSecond2").val()));
            contactMsgFlagThird = isMobilesEmpty(contacts2Mobiles) && G.isEmpty(G.trim($("#landlineThird2").val()));
            if(!G.isEmpty(G.trim($("#name2").val())) && contactMsgFlag){
                if(!checkCustomerName($("#name2").val())&&contactMsgFlag){
                    return;
                }
            }
            if(!G.isEmpty(G.trim($("#name2").val())) && contactMsgFlagSecond){
                if(!checkCustomerName($("#name2").val())&&contactMsgFlagSecond){
                    return;
                }
            }
            if(!G.isEmpty(G.trim($("#name2").val())) && contactMsgFlagThird){
                if(!checkCustomerName($("#name2").val())&&contactMsgFlagThird){
                    return;
                }
            }

            if(!G.isEmpty(G.trim($("#name2").val())) && !isMobilesEmpty(contacts2Mobiles)){
                if(!validateCustomerMobiles(contacts2Mobiles,$("#customerId").val())){
                    return;
                }
                if(!validateSupplierMobiles(contacts2Mobiles,$("#supplierId").val())){
                    return;
                }
            }

            if(!G.isEmpty(G.trim($("#name2").val())) && !G.Lang.isEmpty($("#landline2").val())){
                if(!checkSamePhone($("#landline2").val())|| !checkSupplierPhone($("#landline2").val())) {
                    return;
                }
            }
            if(!G.isEmpty(G.trim($("#name2").val())) && !G.Lang.isEmpty($("#landlineSecond2").val())){
                if(!checkSamePhone($("#landlineSecond2").val())|| !checkSupplierPhone($("#landlineSecond2").val())) {
                    return;
                }
            }
            if(!G.isEmpty(G.trim($("#name2").val())) && !G.Lang.isEmpty($("#landlineThird2").val())){
                if(!checkSamePhone($("#landlineThird2").val())|| !checkSupplierPhone($("#landlineThird2").val())) {
                    return;
                }
            }

            // 校验主联系人信息
            var contacts = buildNormalKeyContacts2();
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
                if(!($("#contacts2" + index + "\\.mainContact").val() == "1")){
                    $("#contacts2" + index + "\\.mainContact").val("1");
                    var mainIndex = getMainContactFromContacts(contacts);
                    $("#contacts2" + mainIndex + "\\.mainContact").val("0");
                }
            }

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

        } else {
            contactMsgFlag = G.Lang.isEmpty($("#mobile2").val()) && G.Lang.isEmpty($("#landline2").val());
            contactMsgFlagSecond = G.Lang.isEmpty($("#mobile2").val()) && G.Lang.isEmpty($("#landlineSecond2").val());
            contactMsgFlagThird = G.Lang.isEmpty($("#mobile2").val()) && G.Lang.isEmpty($("#landlineThird2").val());
            // 校验用户名
            if(!checkCustomerName($("#name2").val()&&contactMsgFlag)) {
                return;
            }
            if(!checkCustomerName($("#name2").val()&&contactMsgFlagSecond)) {
                return;
            }
            if(!checkCustomerName($("#name2").val()&&contactMsgFlagThird)) {
                return;
            }
            if ($("#parentPageType").val() == 'uncleSupplier') {
                $("#contact").val($("#contact2").val());
                $("#mobile").val($("#mobile2").val());
                $("#email").val($("#email2").val());
                $("#qq").val($("#qq2").val());
            } else {
                $("#contact").val($("#contact2").val());
                $("#mobile").val($("#mobile2").val());
                $("#email").val($("#email2").val());
                $("#qq").val($("#qq2").val());
            }
        }


        $("#settlementTypeId").val($("#settlementType2").val());
        $("#invoiceCategoryId").val($("#invoiceCategory2").val());
        $("#identity").val('isCustomer');
        $("#customerId2").val($("#customerId").val());
        $("#id2").val($("#customerId").val());
        $("#birthdayString").val($("#birthdayString3").val());
        $("#customerKind").val($("#customerKind3").val());
        $("#memo").text($("#memo2").val());
        clearDefaultAddress();
        if(!$(this).attr("lock")){
            $(this).attr("lock",true);
            if($("#parentPageType").val() == 'uncleSupplier') {
                $("#supplierId2").val($("#supplierId").val());
                $("#customerId2").val($("#customerId").val());
                $("#id2").val($("#customerId").val());
                $("#parentPageType2Merge").val('uncleSupplier'); // add by zhuj 设置标识符
                $("#customerForm").ajaxSubmit(function(data){
                    if(data.success) {
                        $("#modifyClientDiv").dialog("close");
                        nsDialog.jAlert("绑定供应商与客户成功");
                        $("#identity").attr("checked",true);
                        window.location.href = 'unitlink.do?method=supplier&supplierId='+$("#supplierId").val();
                    }
                });
            } else {
                $("#supplierForm").ajaxSubmit(function(data) {
                    $("#mask", parent.document).css("display", "none");
                    $("#iframe_PopupBox", parent.document).css("display", "none");
                    var jsonObj = JSON.parse(data);
                    setTimeout(function(){
                        if (!G.Lang.isEmpty(jsonObj.supplierId)) {
                            parent.location.reload();
                        }
                    }, 500);

                });
            }

        }

    });
    $("#sureMerge_jy2").click(function(){
        if(!validateProvinceCity("select_province2", "select_city2")){
            return;
        }
        if(G.Lang.isEmpty($("#name3").val())) {
            nsDialog.jAlert("用户名必须填写");
            return;
        }

        if (!checkName(G.trim($("#name3").val()))) {
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
        // 标识重要联系人信息是否为空
        var contactMsgFlag,contactMsgFlagSecond,contactMsgFlagThird;
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
            };

            contactMsgFlag = isMobilesEmpty(contacts3Mobiles) && G.isEmpty(G.trim($("#landline3").val()));
            contactMsgFlagSecond = isMobilesEmpty(contacts3Mobiles) && G.isEmpty(G.trim($("#landlineSecond3").val()));
            contactMsgFlagThird = isMobilesEmpty(contacts3Mobiles) && G.isEmpty(G.trim($("#landlineThird3").val()));
            if(!G.isEmpty(G.trim($("#name3").val())) && contactMsgFlag){
                if(!checkCustomerName($("#name3").val())&&contactMsgFlag){
                    return;
                }
            }
            if(!G.isEmpty(G.trim($("#name3").val())) && contactMsgFlagSecond){
                if(!checkCustomerName($("#name3").val())&&contactMsgFlagSecond){
                    return;
                }
            }
            if(!G.isEmpty(G.trim($("#name3").val())) && contactMsgFlagThird){
                if(!checkCustomerName($("#name3").val())&&contactMsgFlagThird){
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
            if(!G.isEmpty(G.trim($("#name3").val())) && !G.Lang.isEmpty($("#landlineSecond3").val())){
                if(!checkSamePhone($("#landlineSecond3").val())|| !checkSupplierPhone($("#landlineSecond3").val())) {
                    return;
                }
            }
            if(!G.isEmpty(G.trim($("#name3").val())) && !G.Lang.isEmpty($("#landlineThird3").val())){
                if(!checkSamePhone($("#landlineThird3").val())|| !checkSupplierPhone($("#landlineThird3").val())) {
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
            contactMsgFlag = G.Lang.isEmpty($("#mobile3").val()) && G.isEmpty($("#landline3").val());
            contactMsgFlagSecond = G.Lang.isEmpty($("#mobile3").val()) && G.isEmpty($("#landlineSecond3").val());
            contactMsgFlagThird = G.Lang.isEmpty($("#mobile3").val()) && G.isEmpty($("#landlineThird3").val());
            if(!checkCustomerName($("#name3").val()&&contactMsgFlag)) {
                return;
            }
            if(!checkCustomerName($("#name3").val()&&contactMsgFlagSecond)) {
                return;
            }
            if(!checkCustomerName($("#name3").val()&&contactMsgFlagThird)) {
                return;
            }
            if(!G.Lang.isEmpty($("#mobile3").val())) {
                if(!checkSameMobile($("#mobile3").val()) || !checkCustomerMobile($("#mobile3").val())) {
                    return;
                }
            }
        }

        if($("#parentPageType").val() == 'uncleSupplier') {
            $("#name").val($("#name3").val());
            $("#province").val($("#select_province2").val()).change();
            $("#city").val($("#select_city2").val()).change();
            $("#region").val($("#select_township2").val());
            $("#address").val($("#input_address2").val());
            $("#abbr").val($("#abbr3").val());
            $("#landline").val($("#landline3").val());
            $("#landlineSecond").val($("#landlineSecond3").val());
            $("#landlineThird").val($("#landlineThird3").val());
            $("#fax").val($("#fax3").val());
            $("#bank").val($("#bank3").val());
            $("#accountName").val($("#accountName3").val());
            $("#account").val($("#account3").val());
        } else {
            $("#supplier").val($("#name3").val());
            $("#select_province").val($("#select_province2").val()).change();
            $("#select_city").val($("#select_city2").val()).change();
            $("#select_township").val($("#select_township2").val());
            $("#input_address").val($("#input_address2").val());
            $("#abbr").val($("#abbr3").val());
            $("#landline").val($("#landline3").val());
            $("#landlineSecond").val($("#landlineSecond3").val());
            $("#landlineThird").val($("#landlineThird3").val());
            $("#fax").val($("#fax3").val());
            $("#bank").val($("#bank3").val());
            $("#accountName").val($("#accountName3").val());
            $("#account").val($("#account3").val());
        }

        if (APP_BCGOGO.Permission.Web_Version_Has_Supplier_Contact) {
            // modified by zhuj  将contact3的联系人信息写入 客户联系人信息
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
            $("#contacts\\[2\\]\\..name").val($("#contacts32\\.name").val());
            $("#contacts\\[2\\]\\.mobile").val($("#contacts32\\.mobile").val());
            $("#contacts\\[2\\]\\.email").val($("#contacts32\\.email").val());
            $("#contacts\\[2\\]\\.qq").val($("#contacts32\\.qq").val());
            $("#contacts\\[2\\]\\.level").val($("#contacts32\\.level").val());
            $("#contacts\\[2\\]\\.mainContact").val($("#contacts32\\.mainContact").val());
        }else{
            if ($("#parentPageType").val() == 'uncleSupplier') {
                $("#contact").val($("#contact3").val());
                $("#mobile").val($("#mobile3").val());
                $("#email").val($("#email3").val());
                $("#qq").val($("#qq3").val());
            } else {
                $("#contact").val($("#contact3").val());
                $("#mobile").val($("#mobile3").val());
                $("#email").val($("#email3").val());
                $("#qq").val($("#qq3").val());
            }
         }

        $("#settlementType").val($("#settlementType3").val());
        $("#invoiceCategory").val($("#invoiceCategory3").val());
        $("#identity").val('isCustomer');
        $("#customerId2").val('');
        $("#birthdayString").val($("#birthdayString2").val());
        $("#customerKind").val($("#customerKind2").val());
        $("#memo").text($("#memo3").val());
        clearDefaultAddress();
        if(!$(this).attr("lock")){
            $(this).attr("lock",true);
            if($("#parentPageType").val() == 'uncleSupplier') {
                $("#parentPageType2Add").val('uncleSupplierAdd'); // add by zhuj 设置标识符
                $("#supplierId3").val($("#supplierId").val());
                $("#customerFormAdd").ajaxSubmit(function(data){
                    if(data.success) {
                        $("#modifyClientDiv").dialog("close");

                        nsDialog.jAlert("绑定供应商与客户成功");
                        $("#identity").attr("checked",true);
                        window.location.href = 'unitlink.do?method=supplier&supplierId='+$("#supplierId").val();

                    }
                });
            } else {
                $("#supplierForm").ajaxSubmit(function(data) {
                    $("#mask", parent.document).css("display", "none");
                    $("#iframe_PopupBox", parent.document).css("display", "none");
                    var jsonObj = JSON.parse(data);
                    if (!G.Lang.isEmpty(jsonObj.supplierId)) {
                        if (!G.Lang.isEmpty(jsonObj.supplierId)) {
                            parent.location.reload();
                        }
                    }

                });
            }
        }


    });
    provinceBind1();
    $("#select_province1,#select_province2").bind("change",function(){
        cityBind1(this);
    });
    $("#select_city1,#select_city2").bind("change",function(){
        townshipBind1(this);
    });
    $("#cancel,#cancel2").click(function(){
        // 清除联系人div里面带入的相关联系人信息
        /*$(".single_contact input[name^='contacts2']").each(function () {
            $(this).val("");
        });*/
        $(".single_contact_gen").remove();
        $(".single_contact input[name^='contacts3']").each(function () {
            $(this).val("");
        });
        $(".warning").hide();
        $("#modifyClientDiv").dialog("close");
    });

    $("#prev").click(function(){
        if($("#addOrExist").val() == 'exist')  {
            $("#radExist").click();
            $(".select_supplier").css("display","block");
        } else {
            $("#radAdd").click();
            $(".select_supplier").css("display","block");
        }

        // add by zhuj 联系人信息删除
        $(".single_contact_gen").remove();
        $(".warning").hide();
        $(".single_contact input[name^='contacts3']").each(function () {
            $(this).val("");
        });
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

    $("#mobile2,#mobile3").blur(function() {
        if($(this).val() != '') {
            checkSameMobile($(this).val());
        }
    });
    $("#name2,#name3").blur(function(){
        checkName($(this).val());
    });
    $(".J_connector").live("mouseover", function(){
        var customerId = $(this).attr("customerId");
        $(".J_prompt").hide();
        $(".J_prompt[customerId='"+customerId+"']").css({
            left:this.offsetLeft + 'px'
        }).show();
    }).live("mouseout", function(){
        var customerId = $(this).attr("customerId");
        $(".J_prompt[customerId='"+customerId+"']").hide();
    });

    $("#mobile2,#qq2,#account2,#mobile3,#qq3,#account3").keyup(function() {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });
});


function checkSamePhone(phone){
    //要判断是否存在同名的手机号
    var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomerJsonDataByTelephone",
        data:{telephone:phone},dataType:"json"});
    if (r && r.name) {
        var customerId = r.idStr;
        if (customerId != "" && customerId != null && customerId != $("#customerId").val()) {
            nsDialog.jAlert("已存在与【"+ r.name+"】相同的客户座机号，请重新输入");
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
            nsDialog.jAlert("已存在与【"+ r.name+"】相同的供应商座机号，请重新输入");
            return false;
        } else {
            return true;
        }
    } else {
        return true;
    }
}


//第一级菜单 select_province
function provinceBind1() {
    var r = APP_BCGOGO.Net.syncGet({url:"shop.do?method=selectarea",
        data:{parentNo:1},dataType:"json"});
    if (!r||r.length == 0) return;
    else {
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            $("#select_province1")[0].appendChild(option);
        }
        for (var i = 0, l = r.length; i < l; i++) {
            var option = $("<option>")[0];
            option.value = r[i].no;
            option.innerHTML = r[i].name;
            $("#select_province2")[0].appendChild(option);
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
    var r = APP_BCGOGO.Net.syncGet({"url":"shop.do?method=selectarea&parentNo=" + select.value, "dataType":"json"});
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
    for(var i = 0; i< data.supplierDTOs.length; i++) {
        var tr = '<tr><td><input type="radio" value="' + data.supplierDTOs[i].idStr + '" name="supplier"/></td><td>';
        tr+= data.supplierDTOs[i].name;
        tr+='<a class="connecter"></a></td><td></td><td>';
        if(data.supplierDTOs[i].address != null) {
            tr+= data.supplierDTOs[i].address;
        }
        tr+='</td>';
        $("#supplierDatas").append($(tr));
    }
}

function checkSameMobile(mobile) {
    //要判断是否存在同名的手机号
    var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getSupplierJsonDataByMobile",
        data:{mobile:mobile},dataType:"json"});
    if (r && r.name) {
        var supplierId = r.idString;
        if (supplierId != "" && supplierId != null && supplierId != $("#supplierId").val()) {
            nsDialog.jAlert("已存在与【"+ r.name+"】相同的供应商手机号，请重新输入");
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
function checkCustomerMobile(mobile) {
    //要判断是否存在同名的手机号
    var r = APP_BCGOGO.Net.syncGet({url:"customer.do?method=getCustomerJsonDataByMobile",
        data:{mobile:mobile},dataType:"json"});
    if (r && r.name) {
        var customerId = r.idStr;
        if (customerId != "" && customerId != null && customerId != $("#customerId").val()) {
            nsDialog.jAlert("已存在与【"+ r.name+"】相同的客户手机号，请重新输入");
            return false;
        } else {
            return true;
        }
        return false;
    } else {
        return true;
    }
}

function checkCustomerName(name,contactMsgFlag) {
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
                var customerId = jsonStr.results[0].idStr;
                if (customerId != "" && customerId != null && customerId != $("#customerId").val()) {
                    // BUGFIX  add by zhuj 客户存在 and (客户联系人信息不存在 or 既是供应商)
                    var existContactMsgContact = G.isEmpty(jsonStr.results[0].mobile) && G.isEmpty(jsonStr.results[0].telephone);
                    if(!G.isEmpty(jsonStr.results[0].identity) || (contactMsgFlag && existContactMsgContact)){
                        nsDialog.jAlert("客户名【" + name + "】已存在，请重新输入");
                        result = false;
                    }else{
                        result = true;
                    }
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
        if (GLOBAL.Array.indexOf(mobilesTemp, mobiles[index]) >= 0) {
            nsDialog.jAlert("手机号【" + mobiles[index] + "】重复，请重新填写。");
            return true;
        }
        mobilesTemp.push(mobiles[index]);
    }
    return false;
}


