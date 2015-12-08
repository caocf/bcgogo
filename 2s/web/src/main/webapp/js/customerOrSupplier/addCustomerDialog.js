var trCount = $("#customerVehicleTable").find(".titBody_Bg").length;

function customerSelectHandler(data) {
    var result = APP_BCGOGO.Net.syncGet({
        url: "customer.do?method=getCustomerById",
        data: {
            customerId: data['id']
        },
        dataType: "json"
    });
    var vehicleList = APP_BCGOGO.Net.syncGet({
        url: "customer.do?method=getVehicleDTOListByCustomerId",
        data: {
            customerIdStr: data['id']
        },
        dataType: "json"
    });

    setCustomerInfo(result, data);
    setVehicle(vehicleList, data['licenseNo']);

    function setVehicle(vehicleList, licenseNo) {
        $("#select_vehicleNo").empty();
        $("#vehicleBrandModel").html("").removeAttr("title");
        if (G.Lang.isEmpty(vehicleList) || vehicleList.length <= 1) {
             var vehicleId = "",vehicleNo = "",vehicleBrand = "",vehicleModel = "",currentMileage = "",vehicleContact="",vehicleMobile = "";
            if(vehicleList && vehicleList.length == 1){
                 vehicleId = G.Lang.normalize(vehicleList[0]['idStr']);
                vehicleNo = G.Lang.normalize(vehicleList[0]['licenceNo']);
                vehicleBrand = G.Lang.normalize(vehicleList[0]['brand']);
                vehicleModel = G.Lang.normalize(vehicleList[0]['model']);
                currentMileage = G.Lang.normalize(vehicleList[0]['obdMileage']);
                vehicleContact = G.Lang.normalize(vehicleList[0]['contact']);
                vehicleMobile = G.Lang.normalize(vehicleList[0]['mobile']);
            }
            $("#vehicleId").val(vehicleId);
            $("#vehicleNo").val(vehicleNo);
            $("#sp_vehicleNo").html(vehicleNo);
            $("#vehicleBrandModel").html(vehicleBrand + ' ' +vehicleModel).attr("title", vehicleBrand + ' ' +vehicleModel);
            $("#currentMileage").val(currentMileage);
            $("#vehicleBrand").val(vehicleBrand);
            $("#vehicleModel").val(vehicleModel);
            $("#vehicleContact").val(vehicleContact);
            $("#vehicleMobile").val(vehicleMobile);
            $("#sp_vehicleMobile").text(vehicleMobile);
            $("#select_vehicleNo").hide();
            $("#sp_vehicleNo").show();
        } else {
            $.each(vehicleList, function (i, n) {
                var option = $("<option>")[0];
                option.value = n['idStr'];
                option.innerHTML = n['licenceNo'];
                $(option).attr('brand', G.Lang.normalize(n['brand']));
                $(option).attr('model', G.Lang.normalize(n['model']));
                $(option).attr('contact',G.Lang.normalize(n['contact']));
                $(option).attr('mobile',G.Lang.normalize(n['mobile']));
                $(option).attr('currentMileage',G.Lang.normalize(n['obdMileage']));
                if (n['licenceNo'] == licenseNo) {
                    option.selected = true;
                    $("#vehicleNo").val(G.Lang.normalize(n['licenceNo']));
                    $("#vehicleBrandModel").html(G.Lang.normalize(n['brand']) + ' ' + G.Lang.normalize(n['model']))
                        .attr("title", G.Lang.normalize(n['brand']) + ' ' + G.Lang.normalize(n['model']));
                    $("#vehicleId").val(G.Lang.normalize(n['idStr']));
                    $("#currentMileage").val(G.Lang.normalize(G.Lang.normalize(n['obdMileage'])));
                    $("#vehicleBrand").val(G.Lang.normalize(n['brand']));
                    $("#vehicleModel").val(G.Lang.normalize(n['model']));
                    $("#vehicleContact").val(G.Lang.normalize(n['contact']));
                    $("#vehicleMobile").val(G.Lang.normalize(n['mobile']));
                    $("#sp_vehicleMobile").text(G.Lang.normalize(n['mobile']));
                    $("#sp_vehicleNo").hide();
                    $("#select_vehicleNo").show();
                }
                $("#select_vehicleNo")[0].appendChild(option);
            });
        }
    }

    function setCustomerInfo(info, data) {
        $("#customerId").val(G.Lang.normalize(data['id']));
        $("#sp_customerName").html(G.Lang.normalize(info['name']));
        $("#customer").val(G.Lang.normalize(info['name']));
        $("#sp_customerMobile").html(G.Lang.normalize(data['mobile']));
        $("#customerMobile").val(G.Lang.normalize(data['mobile']));
    }

}
$(function () {
    $("#modifyCustomerBtn").click(function () {
        recoverCustomerInfo();
        var customerId = $("#customerId").val();
        if (customerId) {
            clearCustomerInfo();
            $("#dialogCustomerId").val(customerId);
            function _setVehicle(vehicleList) {
                $("#customerVehicleTable .titBody_Bg").remove();
                $.each(vehicleList, function (i, n) {
                    var trSample = "" +
                        "<tr class=\"vehic bg titBody_Bg\">" +
                        "    <td style=\"padding-left:10px;\">" +
                        "        <input class=\"txt validationDuplicate\" maxlength=\"9\" type=\"text\"" +
                        "               name=\"vehicles[" + i + "].licenceNo\" id=\"vehicles" + i + ".licenceNo\"" +
                        "               value=\"" + n['licenceNo'] + "\"/>" +
                        "        <input type=\"hidden\" name=\"vehicles[" + i + "].id\" id=\"vehicles" + i + ".id\" value=\"" + n['idStr'] + "\"/>" +
                        "    </td>" +
                        "    <td>" +
                        "        <input class=\"txt\" maxlength=\"20\" style=\"\" type=\"text\" value=\"" + (n['contact'] ? n['contact'] : "") + "\"" +
                        "               name=\"vehicles[" + i + "].contact\" id=\"vehicles" + i + ".vehicleContact\" />" +
                        "   </td>" +
                        "    <td>" +
                        "        <input class=\"txt\" maxlength=\"11\" type=\"text\"" +
                        "               name=\"vehicles[" + i + "].mobile\" id=\"vehicles" + i + ".vehicleMobile\" value=\"" + (n['mobile'] ? n['mobile'] : "") + "\"/>" +
                        "    </td>" +
                        "    <td>" +
                        "        <input class=\"txt\" maxlength=\"8\" type=\"text\"" +
                        "               name=\"vehicles[" + i + "].brand\" id=\"vehicles" + i + ".vehicleBrand\"  pagetype=\"customerVehicle\" value=\"" + (n['brand'] ? n['brand'] : "") + "\"/>" +
                        "    </td>" +
                        "    <td>" +
                        "        <input class=\"txt\" maxlength=\"8\" type=\"text\"" +
                        "               name=\"vehicles[" + i + "].model\" id=\"vehicles" + i + ".vehicleModel\"  pagetype=\"customerVehicle\" value=\"" + (n['model'] ? n['model'] : "") + "\"/>" +
                        "    </td>" +
                        "    <td>" +
                        "        <input class=\"txt\" maxlength=\"4\" style=\"width:38px\" type=\"text\"" +
                        "               name=\"vehicles[" + i + "].year\" id=\"vehicles" + i + ".year\"" +
                        "               value=\"" + (n['year'] ? n['year'] : "") + "\"/>" +
                        "    </td>" +
                        "    <td>" +
                        "        <input class=\"txt\" maxlength=\"12\" style=\"width:38px\" type=\"text\"" +
                        "               name=\"vehicles[" + i + "].engine\" id=\"vehicles" + i + ".engine\" value=\"" + n['engine'] + "\"/>" +
                        "    </td>" +
                        "    <td>" +
                        "        <input class=\"txt\" maxlength=\"8\" type=\"text\" name=\"vehicles[" + i + "].color\"" +
                        "               id=\"vehicles" + i + ".vehicleColor\" value=\"" + (n['color'] ? n['color'] : "") + "\"/>" +
                        "    </td>" +
                        "    <td>" +
                        "        <input readonly class=\"txt datePicker\" type=\"text\"" +
                        "               name=\"vehicles[" + i + "].dateString\" id=\"vehicles" + i + ".dateString\"" +
                        "               style=\"width:100%\" value=\"" + (n['carDateStr'] ? n['carDateStr'] : "") + "\"/>" +
                        "    </td>" +
                        "    <td>" +
                        "        <input class=\"txt\" maxlength=\"8\" type=\"text\" name=\"vehicles[" + i + "].engineNo\"" +
                        "               id=\"vehicles" + i + ".vehicleEngineNo\" value=\"" + (n['engineNo'] ? n['engineNo'] : "") + "\"/>" +
                        "    </td>" +
                        "    <td><input class=\"txt\" maxlength=\"17\" type=\"text\"" +
                        "               name=\"vehicles[" + i + "].chassisNumber\" " +
                        "               id=\"vehicles" + i + ".chassisNumber\" value=\"" + (n['chassisNumber'] ? n['chassisNumber'] : "") + "\"/>" +
                        "    </td>" +
                        "    <td>" +
                        "        <a class=\"blue_color\" id=\"vehicles" + i + ".deletebutton\" action-type=\"deleteRow\">删除</a>" +
                        "    </td>" +
                        "</tr>";
                    $("#customerVehicleTable .titBottom_Bg").before($(trSample).clone());
                });
                trCount = $("#customerVehicleTable").find(".titBody_Bg").length;
                var tr = initTr(getTrSample());
                $("#customerVehicleTable .titBottom_Bg").before($(tr));
                trCount++;
                isShowAddRowButton();
                initDatePickersAndPlateUpperCase();
            }

            function _setCustomerInfo(result) {
                $("#dialogDetailCustomerName").val(result['name']);
                var contactMobile = "", contactQQ = "",
                    contactEmail = "", contactName = "",
                    landLine = result['landLine'] ? result['landLine'] : "",
                    fax = result['fax'] ? result['fax'] : "",
                    customerKind = result['customerKind'] ? result['customerKind'] : "";
                if (result['contacts'][0]) {
                    contactMobile = result['contacts'][0]['mobile'];
                    contactMobile = contactMobile ? contactMobile : "";
                    contactQQ = result['contacts'][0]['qq'];
                    contactQQ = contactQQ ? contactQQ : "";
                    contactEmail = result['contacts'][0]['email'];
                    contactEmail = contactEmail ? contactEmail : "";
                    contactName = result['contacts'][0]['name'];
                    contactName = contactName ? contactName : "";
                }
                $("#dialogDetailCustomerMobile").val(contactMobile);
                $("#dialogDetailCustomerQQ").val(contactQQ);
                $("#dialogDetailCustomerEmail").val(contactEmail);
                $("#dialogDetailCustomerContact").val(contactName);
                $("#dialogDetailCustomerPhone").val(landLine);
                $("#dialogDetailCustomerFax").val(fax);
                if (customerKind)$("#dialogDetailCustomerKind").val(customerKind);
                if (result['province']) {
                    $("#select_province").val(result['province']);
                }
                cityBind($("#select_province"));
                if (result['city']) {
                    $("#select_city").find('option[value=' + result['city'] + ']').attr("selected", true);
                }
                townshipBind($("#select_city"));
                if (result['region']) {
                    $("#select_township").find('option[value=' + result['region'] + ']').attr("selected", true);
                }
                $("#input_address").val(result['address']);
                $("#dialogDetailCustomerShortName").val(result['shortName']);
                $("#dialogDetailCustomerNameKind").select(result['customerKind']);
            }

            var result = APP_BCGOGO.Net.syncGet({
                url: "customer.do?method=getCustomerById",
                data: {
                    customerId: customerId
                },
                dataType: "json"
            });
            var vehicleList = APP_BCGOGO.Net.syncGet({
                url: "customer.do?method=getVehicleDTOListByCustomerId",
                data: {
                    customerIdStr: customerId
                },
                dataType: "json"
            });

            _setCustomerInfo(result);
            _setVehicle(vehicleList);

            Mask.Login();
            $("#addCustomerContainer").show();
            $("#addCustomerContainer .prompt_box .title").html('<div style="padding-right: 10px;" class="turn_off" id="closeCustomerDetail"></div>修改客户');
        } else {
            nsDialog.jAlert("请选择选择客户信息！");
        }
    });

    clearCustomerInfo();

    function clearCustomerInfo() {
        $("#dialogCustomerId").val("");
        $("#customerVehicleTable .titBody_Bg").remove();
        trCount = 0;
        var tr = initTr(getTrSample());
        $("#customerVehicleTable .titBottom_Bg").before($(tr));
        trCount++;
        isShowAddRowButton();
        initDatePickersAndPlateUpperCase();

        $("#dialogDetailCustomerName").val("");
        $("#dialogDetailCustomerMobile").val("");
        $("#dialogDetailCustomerQQ").val("");
        $("#dialogDetailCustomerEmail").val("");
        $("#dialogDetailCustomerContact").val("");
        $("#dialogDetailCustomerPhone").val("");
        $("#dialogDetailCustomerFax").val("");
        $("#select_province").val("");
        $("#select_city").val("");
        $("#select_township").val("");
        $("#input_address").val("");
        $("#dialogDetailCustomerShortName").val("");
        $("#dialogDetailCustomerKind").val("");
    }

    $("#addNewCustomer").click(function () {
        $("#addCustomerContainer .prompt_box .title").html('<div style="padding-right: 10px;" class="turn_off" id="closeCustomerDetail"></div>新增客户');
        clearCustomerInfo();
        Mask.Login();
        simplifyCustomerInfo();
        $("#addCustomerContainer").show();
    });

    $("#moreCustomerInfo").click(function () {
        recoverCustomerInfo();
    });

    function recoverCustomerInfo() {
        $('[custoemr-detail]').show();
        $('[custoemr-simplifier]').hide();
        $("#customerDetailPromptBox").css({
            "width": "800px"
        });
        $("#customerDetailPromptBoxTitle").css({
            "width": "782px"
        });
        $("#customerVehicleTable").css({
            "width": "750px"
        });
        $('#simplifierFlag').val("false");
//        $("#addCustomerContainer").css({
//            "left": "250px"
//        });
    }

    function simplifyCustomerInfo() {
        $('[custoemr-detail]').hide();
        $('[custoemr-simplifier]').show();
        $('#simplifierFlag').val("true");
        $("#customerDetailPromptBox").css({
            "width": "500px"
        });
//        $("#addCustomerContainer").css({
//            "left": "400px"
//        });
        $("#customerDetailPromptBoxTitle").css({
            "width": "482px"
        });
        $("#customerVehicleTable").css({
            "width": "450px"
        });
    }

    function isSimplifyCustomerInfo() {
        if ($('#simplifierFlag').val() == "true") {
            simplifyCustomerInfo();
        }
    }

    $("#closeCustomerDetail,#cancelCustomerDetailBtn").live("click", function () {
        Mask.Logout();
        $("#addCustomerContainer").hide();
    });

    $("#confirmBtn").click(function () {
        var $me = $(this), $form = $("#thisform");
        if (!$me.attr("lock")) {
            if (checkFormData($form)) {
                $me.attr("lock", true);
                $form.ajaxSubmit(function (result) {
                    if (result && result.success) {
                        nsDialog.jAlert("客户信息保存成功！", null, function () {
                            setAppointOrderCustomerInfo(result);
                        });
                    } else {
                        if (!result.success) {
                            nsDialog.jAlert(result.msg);
                        }
                    }
                    clearCustomerInfo();
                    Mask.Logout();
                    $("#addCustomerContainer").hide();
                    $("#confirmBtn").removeAttr("lock");
                });
            }
        }
    });

    $("[action-type=deleteRow]").live('click', function () {
        var vehicleIdInput = $(this).closest("tr").find("input[id$='id']");
        var vehicleNoInput = $(this).closest("tr").find("input[id$='licenceNo']");

        if ($('[action-type=deleteRow]').size() > 1 && vehicleIdInput.val() == "") {
            $(this).parent().parent().empty().remove();
            isShowAddRowButton();
        }

        if (vehicleIdInput[0] && !G.isEmpty(vehicleIdInput.val()) && !G.isEmpty(vehicleNoInput.val()) && !G.isEmpty($("#dialogCustomerId").val())) {
            var data = APP_BCGOGO.Net.syncGet({
                url: "txn.do?method=checkUndoneOrder",
                data: {
                    licenceNo: vehicleNoInput.val(),
                    vehicleId: vehicleIdInput.val(),
                    customerId: $("#dialogCustomerId").val()
                },
                dataType: "json"
            });
            if (data.success) {
                $(this).closest("tr").remove();
                isShowAddRowButton();
            } else {
                nsDialog.jAlert(data.msg);
            }
        }
    });

    $("[action-type=addRow]").live('click', function () {
        addCustomerVehicleRow(this);
    });

    function setAppointOrderCustomerInfo(saveOrUpdateCustomerResult) {
        if (saveOrUpdateCustomerResult && saveOrUpdateCustomerResult.success && saveOrUpdateCustomerResult.data) {
            var customerDTO = saveOrUpdateCustomerResult.data["customerDTO"];
            var vehicleList = saveOrUpdateCustomerResult.data["vehicleDTOs"];
            var customerId = "", customerName = "", customerMobile = "";
            if (customerDTO) {
                customerId = G.Lang.normalize(customerDTO["idStr"]);
                customerName = G.Lang.normalize(customerDTO["name"]);
                customerMobile = G.Lang.normalize(customerDTO["mobile"]);
            }
            $("#customerId").val(customerId);
            $("#sp_customerName").html(customerName);
            $("#customer").val(customerName);
            $("#sp_customerMobile").html(customerMobile);
            $("#customerMobile").val(customerMobile);


            $("#select_vehicleNo").empty();
            $("#vehicleBrandModel").html("").removeAttr("title");
            if (G.Lang.isEmpty(vehicleList) || vehicleList.length <= 1) {
                var vehicleId = "", vehicleNo = "", vehicleBrand = "", vehicleModel = "", currentMileage = "", vehicleContact = "", vehicleMobile = "";
                if (vehicleList && vehicleList.length == 1) {
                    vehicleId = G.Lang.normalize(vehicleList[0]['idStr']);
                    vehicleNo = G.Lang.normalize(vehicleList[0]['licenceNo']);
                    vehicleBrand = G.Lang.normalize(vehicleList[0]['brand']);
                    vehicleModel = G.Lang.normalize(vehicleList[0]['model']);
                    currentMileage = G.Lang.normalize(vehicleList[0]['obdMileage']);
                    vehicleContact = G.Lang.normalize(vehicleList[0]['contact']);
                    vehicleMobile = G.Lang.normalize(vehicleList[0]['mobile']);
                }
                $("#vehicleId").val(vehicleId);
                $("#vehicleNo").val(vehicleNo);
                $("#sp_vehicleNo").html(vehicleNo);
                $("#vehicleBrandModel").html(vehicleBrand + ' ' + vehicleModel).attr("title", vehicleBrand + ' ' + vehicleModel);
                $("#currentMileage").val(currentMileage);
                $("#vehicleBrand").val(vehicleBrand);
                $("#vehicleModel").val(vehicleModel);
                $("#vehicleContact").val(vehicleContact);
                $("#vehicleMobile").val(vehicleMobile);
                $("#sp_vehicleMobile").text(vehicleMobile);
                $("#select_vehicleNo").hide();
                $("#sp_vehicleNo").show();
            } else {
                $.each(vehicleList, function (i, n) {
                    var option = $("<option>")[0];
                    option.value = n['idStr'];
                    option.innerHTML = n['licenceNo'];
                    $(option).attr('brand', G.Lang.normalize(n['brand']));
                    $(option).attr('model', G.Lang.normalize(n['model']));
                    $(option).attr('contact', G.Lang.normalize(n['contact']));
                    $(option).attr('mobile', G.Lang.normalize(n['mobile']));
                    $(option).attr('currentMileage', G.Lang.normalize(n['obdMileage']));
                    if (i == 0) {
                        option.selected = true;
                        $("#vehicleNo").val(G.Lang.normalize(n['licenceNo']));
                        $("#vehicleBrandModel").html(G.Lang.normalize(n['brand']) + ' ' + G.Lang.normalize(n['model']))
                            .attr("title", G.Lang.normalize(n['brand']) + ' ' + G.Lang.normalize(n['model']));
                        $("#vehicleId").val(G.Lang.normalize(n['idStr']));
                        $("#currentMileage").val(G.Lang.normalize(G.Lang.normalize(n['obdMileage'])));
                        $("#vehicleBrand").val(G.Lang.normalize(n['brand']));
                        $("#vehicleModel").val(G.Lang.normalize(n['model']));
                        $("#vehicleContact").val(G.Lang.normalize(n['contact']));
                        $("#vehicleMobile").val(G.Lang.normalize(n['mobile']));
                        $("#sp_vehicleMobile").text(G.Lang.normalize(n['mobile']));
                        $("#sp_vehicleNo").hide();
                        $("#select_vehicleNo").show();
                    }
                    $("#select_vehicleNo")[0].appendChild(option);
                });
            }
        }
    }

    function getTrSample() {
        return "" +
            "<tr class=\"vehic bg titBody_Bg\">" +
            "    <td style=\"padding-left:10px;\">" +
            "        <input class=\"txt validationDuplicate\" maxlength=\"9\" type=\"text\"" +
            "               name=\"vehicles[0].licenceNo\" id=\"vehicles0.licenceNo\"" +
            "               value=\"\"/>" +
            "        <input type=\"hidden\" name=\"vehicles[0].id\" id=\"vehicles0.id\" value=\"\"/>" +
            "    </td>" +
            "    <td custoemr-detail>" +
            "        <input class=\"txt\" maxlength=\"20\" style=\"\" type=\"text\"" +
            "               name=\"vehicles[0].contact\" id=\"vehicles0.vehicleContact\" />" +
            "   </td>" +
            "    <td custoemr-detail>" +
            "        <input class=\"txt\" maxlength=\"11\" type=\"text\"" +
            "               name=\"vehicles[0].mobile\" id=\"vehicles0.vehicleMobile\" />" +
            "    </td>" +
            "    <td>" +
            "        <input class=\"txt J_checkVehicleBrandModel\" maxlength=\"8\" type=\"text\"" +
            "               name=\"vehicles[0].brand\" pagetype=\"customerVehicle\" id=\"vehicles0.vehicleBrand\" />" +
            "    </td>" +
            "    <td>" +
            "        <input class=\"txt J_checkVehicleBrandModel\" maxlength=\"8\" type=\"text\"" +
            "               name=\"vehicles[0].model\" pagetype=\"customerVehicle\" id=\"vehicles0.vehicleModel\"/>" +
            "    </td>" +
            "    <td custoemr-detail>" +
            "        <input class=\"txt\" maxlength=\"4\" style=\"width:38px\" type=\"text\"" +
            "               name=\"vehicles[0].year\" id=\"vehicles0.year\"" +
            "               />" +
            "    </td>" +
            "    <td custoemr-detail>" +
            "        <input class=\"txt\" maxlength=\"12\" style=\"width:38px\" type=\"text\"" +
            "               name=\"vehicles[0].engine\" id=\"vehicles0.engine\"/>" +
            "    </td>" +
            "    <td custoemr-detail>" +
            "        <input class=\"txt\" maxlength=\"8\" type=\"text\" name=\"vehicles[0].color\"" +
            "               id=\"vehicles0.vehicleColor\"/>" +
            "    </td>" +
            "    <td custoemr-detail>" +
            "        <input readonly class=\"txt datePicker\" type=\"text\"" +
            "               name=\"vehicles[0].dateString\" id=\"vehicles0.dateString\"" +
            "               style=\"width:100%\"/>" +
            "    </td>" +
            "    <td custoemr-detail><input class=\"txt\" maxlength=\"17\" type=\"text\"" +
            "               name=\"vehicles[0].chassisNumber\" " +
            "               id=\"vehicles0.chassisNumber\"/>" +
            "    </td>" +
            "    <td custoemr-detail>" +
            "        <input class=\"txt\" maxlength=\"8\" type=\"text\" name=\"vehicles[0].engineNo\"" +
            "               id=\"vehicles0.vehicleEngineNo\" />" +
            "    </td>" +
            "    <td>" +
            "        <a class=\"blue_color\" id=\"vehicles0.deletebutton\" action-type=\"deleteRow\">删除</a>" +
            "        <a class=\"blue_color\" id=\"vehicles0.plusbutton\" action-type=\"addRow\">新增</a>" +
            "    </td>" +
            "</tr>";
    }

    function initTr(trSample) {
        var tr = $(trSample).clone();
        $(tr).find("input").val("");

        $.each($(tr).find("input"), function (i, n) {
            _replace(this);
        });

        $.each($(tr).find("a"), function (i, n) {
            _replace(this);
        });
        return tr;
    }

    function addCustomerVehicleRow(me) {
        var isCheck = checkVehicle(me);
        if (!isCheck && isCheck != null) {
            return;
        }

        if (trCount >= 2) {
            if (checkSame(trCount)) {
                alert("输入车辆信息有重复，请重新输入！");
                return false;
            }
        }
        var tr = initTr(getTrSample());

        $(tr).find(".validationDuplicate").bind("change", function () {
            if (trCount >= 2) {
                if (checkDuplicateVehicles(me)) {
                    alert("单据有重复内容，请修改或删除。");
                    $(me).select().focus();
                    return;
                }
            }
        });
        $("#customerVehicleTable .titBottom_Bg").before(tr);
        trCount++;
        isShowAddRowButton();
        initDatePickersAndPlateUpperCase();
        isSimplifyCustomerInfo();
    }

    function _replace(me) {
        //replace id
        var idStr = $(me).attr("id");
        var idStrs = idStr.split(".");
        var newId = "vehicles" + trCount + "." + idStrs[1];
        $(me).attr("id", newId);
        var nameStr = $(me).attr("name");
        var nameStrs = nameStr.split(".");
        var newName = "vehicles[" + trCount + "]." + nameStrs[1];
        $(me).attr("name", newName);
    }

    //绑定下拉列表的值        select_province    select_city    select_township
    provinceBind();

    $("#select_province").bind("change", function () {
        cityBind(this);
    });
    $("#select_city").bind("change", function () {
        townshipBind(this);
    });

    //第一级菜单 select_province
    function provinceBind() {
        var r = APP_BCGOGO.Net.syncGet({
            url: "shop.do?method=selectarea",
            data: {
                parentNo: 1
            },
            dataType: "json"
        });
        if (!r || r.length == 0) return;
        else {
            for (var i = 0, l = r.length; i < l; i++) {
                var option = $("<option>")[0];
                option.value = r[i].no;
                option.innerHTML = r[i].name;
                $("#select_province")[0].appendChild(option);
            }
        }
    }

    //第二级菜单 select_city
    function cityBind(select) {
        while ($("#select_city")[0].options.length > 1) {
            $("#select_city")[0].remove(1);
        }
        while ($("#select_township")[0].options.length > 1) {
            $("#select_township")[0].remove(1);
        }
        if (select.selectedIndex == 0) {
        } else {
            var r = APP_BCGOGO.Net.syncGet({"url": "shop.do?method=selectarea&parentNo=" + $(select).val(), "dataType": "json"});
            if (r === null) {
                return;
            }
            else {
                for (var i = 0, l = r.length; i < l; i++) {
                    var option = $("<option>")[0];
                    option.value = r[i].no;
                    option.innerHTML = r[i].name;
                    $("#select_city")[0].appendChild(option);
                }
            }
        }
    }

    //第三级菜单 select_township
    function townshipBind(select) {
        if (select.selectedIndex == 0) {
            return;
        }
        var r = APP_BCGOGO.Net.syncGet({"url": "shop.do?method=selectarea&parentNo=" + $(select).val(), "dataType": "json"});
        if (r === null || typeof(r) == "undefined") {
            return;
        }
        else {
            while ($("#select_township")[0].options.length > 1) {
                $("#select_township")[0].remove(1);
            }
            if (typeof(r) != "undefined" && r.length > 0) {
                for (var i = 0, l = r.length; i < l; i++) {
                    var option = $("<option>")[0];
                    option.value = r[i].no;
                    option.innerHTML = r[i].name;
                    $("#select_township")[0].appendChild(option);
                }
            }
        }
    }

    isShowAddRowButton();
    initDatePickersAndPlateUpperCase();

    function isShowAddRowButton() {
        //如果初始化的话就默认加一行
        if ($(".vehic").size() <= 0) {
            $("[action-type=addRow]").trigger("click");
        }
        $(".vehic [action-type=addRow]").remove();
        var deleteOperaId = $(".vehic:last").find("td:last>a[action-type=deleteRow]").attr("id");
        if (deleteOperaId == null || deleteOperaId == "") {
            return;
        }
        $(".vehic:last").find("td:last>a[action-type=deleteRow]")
            .after('<a class="blue_color" id="vehicles' + (deleteOperaId.split(".")[0].substring(8)) + '.plusbutton" action-type="addRow">&nbsp新增</a>');
    }

    function checkFormData($form) {
        $.each($form.find('input[type=text]'), function (i, n) {
            $(n).val($.trim($(n).val()));
        });
        var $customerName = $form.find('[name=name]'),
            customerName = $customerName.val(),
            $phone = $form.find('[name=phone]'), // 校验座机
            phone = $phone.val(),
            $mobile = $form.find('[name=mobile]'),
            $fax = $form.find('[name=fax]'),
            fax = $fax.val(),
            mobile = $mobile.val(),
            email = $form.find('[name=email]').val(),
            customerId = $form.find('[name=customerId]').val(),
            qq = $form.find('[name=qq]').val();

        if (G.isEmpty(customerName) || G.isEmpty($.trim(customerName))) {
            nsDialog.jAlert("用户名必须填写!");
            return false;
        }
        if (!G.isEmpty(G.trim(phone))) {
            if (!APP_BCGOGO.Validator.stringIsTelephoneNumber(phone)) {
                nsDialog.jAlert("座机格式校验错误，请确认后重新输入！");
                return false;
            }
        }
        if (!G.isEmpty(mobile) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
            nsDialog.jAlert("手机号格式错误，请确认后重新输入！");
            return false;
        }
        if (!G.isEmpty(email) && !APP_BCGOGO.Validator.stringIsEmail(email)) {
            nsDialog.jAlert("Email格式错误，请确认后重新输入！");
            return false;
        }
        if (!G.isEmpty(qq) && !APP_BCGOGO.Validator.stringIsQq(qq)) {
            nsDialog.jAlert("QQ号格式错误，请确认后重新输入！");
            return false;
        }
        if (!G.isEmpty(fax) && !APP_BCGOGO.Validator.stringIsTelephoneNumber(fax)) {
            nsDialog.jAlert("传真号格式错误，请确认后重新输入！");
        }
        if (!checkCustomerNameSuccess(customerName, $customerName)) {
            return false;
        }
        if (!G.isEmpty(mobile) && !checkCustomerMobileSuccess(mobile, $mobile)) {
            return false;
        }
        if (!G.isEmpty(phone) && !checkCustomerPhoneSuccess(phone, $phone)) {
            return false;
        }
        checkAllDuplicateVehicles();
        if (checkCustomerVehicleNo()) {
            return false;
        }
        checkAddress();
        return true;
    }

    function checkAddress() {

    }

    function checkCustomerVehicleNo() {
        var flag = false;
        var customerNameAjax = "";  //记录AJAX获得的当前车牌号的实际客户姓名
        var customerId;             //记录AJAX获得的当前车牌号的实际客户ID
        var licenceNoInput;         //记录触发事件的是哪个车牌号文本框
        $("#customerVehicleTable input[id$='.licenceNo']").each(function () {
            var me = this;
            var licenceVal = $(this).val();
            var licenceDomId = $(this).attr("id");
            var resultStr = licenceVal.replace(/\s|\-/g, "");
            if (!checkhanzi(resultStr) && (resultStr.length == 5 || resultStr.length == 6)) {
                APP_BCGOGO.Net.syncGet({
                    url: "product.do?method=userLicenseNo",
                    dataType: "json",
                    success: function (json) {
                        if (json === null) {
                            flag = true;
                            return;
                        }
                        $(me).val((json[0]['localCarNo'] + $(me).val()).toUpperCase());
                    }
                });
            }
            licenceVal = $(this).val();
            if (licenceVal == "") {
                return;
            } else if (APP_BCGOGO.Validator.stringIsLicensePlateNumber(licenceVal)) {
                //判断该车牌号是否已经有归属
                var r = APP_BCGOGO.Net.syncPost({url: "customer.do?method=licenceNoIsExisted",
                    data: {licenceVal: licenceVal, customerName: $("#dialogDetailCustomerName").val()}, dataType: "json"
                });
                if (!r || r.length == 0) {
                    return;
                } else {
                    customerNameAjax = r[0].customerName;
                    customerId = r[0].customerId;
                    var dialogCustomerId = $("#dialogCustomerId").val();
                    if (customerNameAjax != "" && customerNameAjax.length > 0) {
                        //当前客户与原车主同名，给予提示
                        if ($("#name").val() == customerNameAjax && $("#dialogCustomerId").val() == "") {
                            if (confirm("此车牌客户【" + customerNameAjax + "】已存在，是否修改客户资料为当前信息？")) {
                                $("#dialogCustomerId").val(customerId);
                                licenceNoInput = $(this);
                            } else {
                                licenceVal = "";
                                customerId = "";
                                $("#dialogCustomerId").val(customerId);
                            }
                        }
                        //与原车主不同名
                        else if ($("#name").val() != customerNameAjax && (!dialogCustomerId || (dialogCustomerId && customerId != dialogCustomerId))) {


                            if (r[0].isObd == "true") {
                              nsDialog.jAlert("此车牌【" + licenceVal + "】已属于其他客户【" + customerNameAjax + "】,是OBD车辆，不能添加");
                              licenceVal = "";
                              customerNameAjax = "";
                              customerId = "";
                              $(this).val("");
                              return;
                            }


                          if (confirm("此车牌【" + licenceVal + "】已属于其他客户【" + customerNameAjax + "】，不能用于当前客户,是否转让到当前客户？")) {
                                if (confirm("请再次确认此车的欠款是否已经结清，并办妥相关转让手续。")) {
                                    //将原车主的车牌关联信息删除
                                    customerNameAjax = "";
                                    customerId = "";
                                    var j;
                                    APP_BCGOGO.Net.syncGet({url: "customer.do?method=deleteCustomerLicenceNo", data: {licenceVal: licenceVal}, dataType: "json",
                                        success: function (json) {
                                            j = json;
                                        }
                                    });
                                } else {
                                    licenceVal = "";
                                    customerNameAjax = "";
                                    customerId = "";
                                    $(this).val("");
                                }
                            } else {
                                customerNameAjax = "";
                                licenceVal = "";
                                customerId = "";
                                $(this).val("");
                            }
                        }
                    } else {
                        customerNameAjax = "";
                        licenceVal = "";
                        customerId = "";
                    }
                }
            } else {
                flag = true;
                alert("输入的车牌号码不符合规范，请检查！");
                if (document.getElementById(licenceDomId)) {
                    document.getElementById(licenceDomId).value = "";
                }
            }

        });

        return flag;
    }


    function checkCustomerPhoneSuccess(phone, phoneDom) {
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
            var dialogCustomerId = $("#dialogCustomerId").val();
            if (!dialogCustomerId || (dialogCustomerId && customerId != dialogCustomerId)) {
                hrefHandler(customerId, "phone", phone, phoneDom);
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    function checkCustomerNameSuccess(name, nameDom) {
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
            var dialogCustomerId = $("#dialogCustomerId").val();
            if (!dialogCustomerId || (dialogCustomerId && customerId != dialogCustomerId)) {
                hrefHandler(customerId, "name", name, nameDom);
                return false
            }
            return true;
        } else {
            return true;
        }
    }

    function checkCustomerMobileSuccess(mobile, mobileDome) {
        var jsonStr = APP_BCGOGO.Net.syncPost({
            url: "customer.do?method=getCustomerJsonDataByMobile",
            data: {
                mobile: mobile
            },
            cache: false,
            dataType: "json"
        });
        if (!G.isEmpty(jsonStr.data) && !G.isEmpty(jsonStr.data.idStr)) {
            var customerId = jsonStr.data.idStr;
            var dialogCustomerId = $("#dialogCustomerId").val();
            if (!dialogCustomerId || (dialogCustomerId && customerId != dialogCustomerId)) {
                hrefHandler(customerId, "mobile", mobile, mobileDome);
                return false
            }
            return true;
        } else {
            return true;
        }
    }

    function hrefHandler(customerId, searchFieldType, searchField, fieldDom) {
        if (customerId && !G.isEmpty(customerId)) {
            var dialogCustomerId = $("#dialogCustomerId").val();
            if (!dialogCustomerId || (dialogCustomerId && customerId != dialogCustomerId)) {
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
                            } else {
                                if ($(fieldDom)[0]) {
                                    $(fieldDom).val("").focus();
                                }
                            }
                        });
                    } else if (G.trim(searchFieldType) === "phone") {
                        nsDialog.jConfirm("座机为【" + searchField + "】的客户已存在，是否跳转到客户页面进行修改？", null, function (resultValue) {
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
    }
});