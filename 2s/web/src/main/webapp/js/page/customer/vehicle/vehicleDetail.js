/**
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 14-5-5
 * Time: 下午3:21
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(function () {

    $("#change_ok_opr_btn").click(function(){
        if($(this).attr("lock")){
            return;
        }
        var imei=$("#obd_edit_div").find(".imei_input").val();
        var mobile=$("#obd_edit_div").find(".sim_no_input").val();
         var imei_old=$("#customerVehicleDiv .imei_span").text();
        var mobile_old=$("#customerVehicleDiv .sim_no_span").text();
        if(G.isEmpty(imei)){
            nsDialog.jAlert("请输入IMEI号！");
            $(this).removeAttr("lock");
            return;
        }
        if(G.isEmpty(mobile)){
            nsDialog.jAlert("请输入SIM卡号！");
            $(this).removeAttr("lock");
            return;
        }
        if (!APP_BCGOGO.Validator.stringIsCharacter(imei)) {
            nsDialog.jAlert("IMEI号格式错误，请确认后重新输入！");
            $(this).removeAttr("lock");
            return;
        }
        if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
            nsDialog.jAlert("SIM卡号格式错误，请确认后重新输入！");
            $(this).removeAttr("lock");
            return;
        }
        if(imei==imei_old){
            nsDialog.jAlert("要更换OBD的IMEI号和当前OBD的IMEI号重复！");
            $(this).removeAttr("lock");
            return;
        }
        if(mobile==mobile_old){
            nsDialog.jAlert("要更换OBD的SIM卡号和当前OBD的SIM卡号重复！");
            $(this).removeAttr("lock");
            return;
        }
        var _$me=$(this);
        APP_BCGOGO.Net.asyncGet({
            url:"OBDManager.do?method=gsmOBDBind",
            data:{
                imei:imei,
                mobile:mobile,
                vehicleId:vehicleId,
                opr_type:"change_opr",
                now:new Date()
            },
            dataType:"json",
            success:function(result) {
                _$me.removeAttr("lock");
                if(G.isEmpty(result)){
                    nsDialog.jAlert("保存异常！");
                    return;
                }
                if(!result.success){
                    nsDialog.jAlert(result.msg);
                    return;
                }
                $("#obd_edit_div").dialog("close");
                window.location.reload();
            },error:function(){
                $(this).removeAttr("lock");
            }
        });
    });

    $(".imei_input,.sim_no_input,[id^='gsmObdImei']").live('focus',function () {
        searchOBDInfo(this, $(this).val());
    }).live("input",function(){
            searchOBDInfo(this, $(this).val());
        });

    $('[name="s_radio"]').click(function(){
        if($(this).hasClass("change_radio")){
            $("#change_obd_tr").show();
            $("#change_opr_div").show();
            $("#delete_opr_div").hide();

        }else{
            $("#change_obd_tr").hide();
            $("#change_opr_div").hide();
            $("#delete_opr_div").show();
        }
    });


    $(".cancel_opr_btn").click(function(){
        $("#obd_edit_div").dialog("close");
    });

    $("#delete_ok_opr_btn").click(function(){
        if($(this).attr("lock")){
            return;
        }
        var data_index=$("#edit_data_index").val();
        var imei=$("#customerVehicleDiv .imei_span").text();
        var sim_no=$("#customerVehicleDiv .sim_no_span").text();
        var _$me=$(this);
        APP_BCGOGO.Net.asyncGet({
            url:"OBDManager.do?method=unInstallGsmOBD",
            data:{
                imei:imei,
                mobile:sim_no,
                now:new Date()
            },
            dataType:"json",
            success:function(result) {
                _$me.removeAttr("lock");
                if(G.isEmpty(result)){
                    nsDialog.jAlert("保存异常！");
                    return;
                }
                if(!result.success){
                    nsDialog.jAlert(result.msg);
                    return;
                }
                $("#obd_edit_div").dialog("close");
                window.location.reload();
            }
        });
    });

    $(".edit_obd_btn").live("click",function(){
        var data_index=$(this).attr("data-index");
        var imei=$("#customerVehicleDiv .imei_span").text();
        var sim_no=$("#customerVehicleDiv .sim_no_span").text();
        imei=$.trim(imei);
        sim_no=$.trim(sim_no);
        $("#obd_edit_div").dialog({
            width: 450,
            height:240,
            modal: true,
            draggable:false,
            resizable: true,
            title: "修改OBD",
            open:function(){
                $(".change_radio").click();
                $("#obd_edit_div").find(".imei_span").text(imei);
                $("#obd_edit_div").find(".sim_no_span").text(sim_no);
                $("#edit_data_index").val(data_index);
                $("#obd_edit_div .imei_input").val("");
                $("#obd_edit_div .sim_no_input").val("");
            }
        });
    });

    $("#vehicle_bind_cancelBtn").click(function(){
        $("#obd_bind_div").dialog("close");
    });

    $("#vehicle_bind_okBtn").click(function(){
        if($(this).attr("lock")){
            return;
        }
        var imei=$("#obd_bind_div .imei_input").val();
        var mobile=$("#sim_no_input").val();
        var vehicleId=$("#bind_vehicle_id").val();
        if(G.isEmpty(imei)){
            nsDialog.jAlert("请输入IMEI号！");
            $(this).removeAttr("lock");
            return;
        }
        if(G.isEmpty(mobile)){
            nsDialog.jAlert("请输入SIM卡号！");
            $(this).removeAttr("lock");
            return;
        }
        if(G.isEmpty(vehicleId)){
            nsDialog.jAlert("车辆信息异常！");
            $(this).removeAttr("lock");
            return;
        }
        if (!APP_BCGOGO.Validator.stringIsCharacter(imei)) {
            nsDialog.jAlert("IMEI号格式错误，请确认后重新输入！");
            $(this).removeAttr("lock");
            return;
        }
        if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber(mobile)) {
            nsDialog.jAlert("SIM卡号格式错误，请确认后重新输入！");
            $(this).removeAttr("lock");
            return;
        }
        $(this).attr("lock","lock");
        var _$me=$(this);
        APP_BCGOGO.Net.asyncGet({
            url:"OBDManager.do?method=gsmOBDBind",
            data:{
                imei:imei,
                mobile:mobile,
                vehicleId:vehicleId,
                now:new Date()
            },
            dataType:"json",
            success:function(result) {
                _$me.removeAttr("lock");
                if(G.isEmpty(result)){
                    nsDialog.jAlert("保存异常！");
                    return;
                }
                if(!result.success){
                    nsDialog.jAlert(result.msg);
                    return;
                }
                $("#bind_opr_tr").hide();
                $("#bind_show_tr .imei_span").text(imei);
                $("#bind_show_tr .sim_no_span").text(mobile);
                $("#bind_show_tr").show();
                $("#obd_bind_div").dialog("close");

            },
            error:function(){
                $(this).removeAttr("lock");
            }
        });

    });

    $(".bind_obd_btn").live("click",function(){
        var vehicleId=$("#vehicleId").val();
        $("#bind_vehicle_id").val(vehicleId);
        $("#obd_bind_div").dialog({
            width: 380,
            height:170,
            modal: true,
            draggable:false,
            resizable: true,
            title: "绑定OBD",
            close:function(){
                $(".imei_input").val("");
                $(".sim_no_input").val("");
                $("#bind_vehicle_id").val("");
            }

        });

    });

    $("#imei_input,#sim_no_input").bind('focus',function () {
        searchOBDInfo(this, $(this).val());
    }).bind("input",function(){
            searchOBDInfo(this, $(this).val());
        });


    $(".J_deleteAppointService").live("click", function () {

        var table = $(this).parents(".J_vehicleAppointTable");

        $(this).parent().find("input[name$='operateType']").val("LOGIC_DELETE");
        $(this).parent().hide();

        if (G.isEmpty($(this).parent().find("input[name$='id']").val())) {
            $(this).parent().remove();
        }

        $(this).remove();
        table.find(".J_addAppointService").remove();
        if (table.find(".J_deleteAppointService").size() != 0) {
            var lastDelete = table.find(".J_deleteAppointService:last");
            var deleteIndex = lastDelete.attr("data-index");
            lastDelete.after('<a class="J_addAppointService" data-index="' + deleteIndex + '"><img src="images/opera2.png"/></a>');
        } else {
            table.find("input[name='yc']").after('<a class="J_addAppointService" data-index="0"><img src="images/opera2.png"/></a>');
        }


    });

    $(".J_addAppointService").live("click", function () {

        var memberTable = $(this).parents(".J_vehicleAppointTable");

        var isFirst = false;
        if (memberTable.find(".J_deleteAppointService").size() == 0) {
            isFirst = true;
        }

        var validateFlag = false;

        if (!isFirst) {
            $(this).parents("tr").find("input").each(function () {
                    if (!validateFlag) {
                        if ($(this).attr("type") != "hidden" && G.Lang.isEmpty($(this).val()) && $(this).parent('td').css('display') != 'none') {
                            if ($(this).attr('name').indexOf("appointName") != -1) {
                                nsDialog.jAlert("请输入提醒服务内容!");
                                validateFlag = true;
                                return;
                            } else if ($(this).attr('name').indexOf("appointDate") != -1) {
                                nsDialog.jAlert("请输入提醒服务时间!");
                                validateFlag = true;
                                return;
                            }

                            return;
                        }
                    }
                }
            );
            if (validateFlag) {
                return;
            }
        }
        var tdSize = $(this).parents('tr').find('td').size();
        $(this).parents('tr').find('td').each(function () {
            if ($(this).css('display') == 'none') {
                tdSize = tdSize - 1;
            }
        });

        $(this).hide();

        var addTdIndex = memberTable.find("input[name$='.appointName']").size();
        var tdHtml = '<td class="test2"><input type="text" style="width:80px"' +
            ' name="appointServiceDTOs[' + addTdIndex + '].appointName"' +
            ' reset-value="" value=""' +
            ' class="txt J_formreset"/>：<input type="hidden" name="appointServiceDTOs[' + addTdIndex + '].id" value="">' +
            '<input type="hidden" name="appointServiceDTOs[' + addTdIndex + '].operateType"' +
            ' value="">' +
            '<input type="text" style="width:75px;" onclick="showDatePicker(this);" readonly="readonly"' +
            ' name="appointServiceDTOs[' + addTdIndex + '].appointDate"' +
            ' reset-value="" value=""' +
            ' class="txt J_formreset"/>' +
            ' <a data-index="' + addTdIndex + '" class="J_deleteAppointService"><img src="images/opera1.png"/></a>' +
            ' <a class="J_addAppointService" data-index="' + addTdIndex + '"><img src="images/opera2.png"/></a>' +
            ' </td>';
        if (!isFirst) {
            if (tdSize != 4) {
                $(this).parents('tr').find('td:last').after(tdHtml);
            } else {
                var trHtml = '<tr class="J_appointServiceTrEdit">' + tdHtml + '</tr>';
                memberTable.append(trHtml);
            }
        } else {
            var trHtml = '<tr class="J_appointServiceTrEdit">' + tdHtml + '</tr>';
            memberTable.append(trHtml);
        }
        $(this).remove();

    });


    $("#saveCustomerAppointBtn").live("click", function (event) {
        event.preventDefault();
        var $customerVehicleAppointForm = $("#customerVehicleAppointForm");
        var $customerVehicleForm = $("#customerVehicleForm");
        var vehicleId = $customerVehicleForm.find("input[name='vehicleId']").val();

        if (G.Lang.isEmpty(vehicleId)) {
            nsDialog.jAlert("请先保存车辆信息");
            return;
        }

        $customerVehicleAppointForm.ajaxSubmit({
            dataType: "json",
            type: "POST",
            success: function (jsonStr) {
                if (!G.Lang.isEmpty(jsonStr.vehicleId)) {

                    updateCustomerVehicleInfo(jsonStr);

                    $("#customerVehicleAppointInfoShow").show();
                    $("#customerVehicleAppointInfoEdit").hide();
                    showMessage.fadeMessage("45%", "24%", "slow", 3000, "更新成功！");
                } else {
                    nsDialog.jAlert("更新失败！");
                }
            },
            error: function (json) {
                nsDialog.jAlert("网络异常，请联系客服");
            }
        });
    });


    $("#cancelCustomerAppointBtn").live("click", function (event) {
        event.preventDefault();

        $("#customerVehicleAppointInfoEdit").hide();
        $("#customerVehicleAppointInfoShow").show();
        $("#customerVehicleAppointForm").find(".J_formreset").each(function () {
            $(this).val(G.Lang.normalize($(this).attr("reset-value")));
        });
    });

    $("#registerMaintainBtn").live("click", function () {
        $("#maintainRegisterDiv").dialog({
            width: 500,
            modal: true,
            resizable: false,
            position: 'center',
            open: function () {
                $("#registerMaintainMileagePeriod").val($("#maintainMileagePeriod").val());
                $("#registerMaintainTimePeriod").val($("#maintainTimePeriodStr").val());

                $("#lastMaintainMileage").val($("#obdMileage").val());
                $(".ui-dialog-titlebar", $(this).parent()).hide();
                $("#registerMaintainTime").val($("#today").val());
                $(this).removeClass("ui-dialog-content").removeClass("ui-widget-content");
            },
            close: function () {
                $("#lastMaintainMileage").val("");
                $("#registerMaintainMileagePeriod").val("");
                $("#registerMaintainTime").val("");
                $("#registerMaintainTimePeriod").val("");
            }
        });
    });

    $("#saveMaintainRegister").live("click", function () {
        var $vehicleMaintainRegister = $("#maintainRegisterForm");

        $vehicleMaintainRegister.ajaxSubmit({
            dataType: "json",
            type: "POST",
            success: function (data) {
                if (data.success) {

                    var $customerVehicleDiv = $("#customerVehicleDiv");


                    $customerVehicleDiv.find("span[data-key='lastMaintainMileage']").text(G.isEmpty($("#lastMaintainMileage").val()) ? "--" : ($("#lastMaintainMileage").val() + "公里"));
                    $customerVehicleDiv.find("span[data-key='lastMaintainTimeStr']").text(G.isEmpty($("#registerMaintainTime").val()) ? "--" : $("#registerMaintainTime").val());
                    $customerVehicleDiv.find("span[data-key='maintainMileagePeriod']").text(G.isEmpty($("#registerMaintainMileagePeriod").val()) ? "--" : ($("#registerMaintainMileagePeriod").val() + "公里"));

                    var next = data.data.nextMaintainMileageAccessStr;
                    if (G.isNotEmpty(next)) {
                        $customerVehicleDiv.find("span[data-key='nextMaintainMileageAccessStr']").text(next);
                    } else {
                        $customerVehicleDiv.find("span[data-key='nextMaintainMileageAccessStr']").text("--");
                    }
                    var $customerVehicleEditDiv = $("#customerVehicleEditDiv");
                    $customerVehicleEditDiv.find("span[data-key='lastMaintainMileage']").text(G.isEmpty($("#lastMaintainMileage").val()) ? "--" : ($("#lastMaintainMileage").val() + "公里"));
                    $customerVehicleEditDiv.find("span[data-key='lastMaintainTimeStr']").text(G.isEmpty($("#registerMaintainTime").val()) ? "--" : $("#registerMaintainTime").val());


                    $("#maintainMileagePeriod").val($("#registerMaintainMileagePeriod").val());
                    $("#maintainTimePeriodStr").val($("#registerMaintainTimePeriod").val());

                    $customerVehicleEditDiv.find("span[data-key='nextMaintainMileageAccessStr']").text(next);


                    if (G.isEmpty(data.data.maintainMileage)) {
                        $(".J_maintainMileageUnitSpan").hide();
                    } else {
                        $(".J_maintainMileageUnitSpan").show();
                        $("#appointServiceTableShow").find("span[data-key='maintainMileage']").text(data.data.maintainMileage);
                        $("#maintainMileage").val(data.data.maintainMileage);
                        $("#maintainMileage").attr("reset-vale",data.data.maintainMileage);
                    }                                        maintainMileagePeriod

                    if (!G.isEmpty(data.data.maintainTimeStr)) {
                        $("#appointServiceTableShow").find("span[data-key='maintainTimeStr']").text(data.data.maintainTimeStr);
                        $("#by").val(data.data.maintainTimeStr);
                        $("#by").attr("reset-vale",data.data.maintainTimeStr);
                    }

                    $("#appointServiceTableShow")
                }
                $("#maintainRegisterDiv").dialog("close");

            },
            error: function (json) {
                nsDialog.jAlert("网络异常，请联系客服");
            }
        });
    });
    $("#cancelMaintainRegister").live("click", function () {
        $("#maintainRegisterDiv").dialog("close");
    });


    $("#customerVehicleEditDiv input[name='year'],#customerVehicleEditDiv input[name='maintainMileagePeriod'],#registerMaintainMileagePeriod,#customerVehicleEditDiv input[name='obdMileage'],#lastMaintainMileage").live("keyup",function () {
        if (this.value.length == 1) {
            this.value = this.value.replace(/[^1-9]/g, '')
        } else {
            this.value = this.value.replace(/\D/g, '')
        }
    }).live("blur", function () {
            $(this).click();
        });


    $("#customerVehicleEditDiv input[name='vin']").live("keyup", function () {
        $(this).val($(this).val().replace(/[^\da-zA-Z]+/g, ""));
    });
    $("#customerVehicleEditDiv input[name='engineNo']").live("keyup", function () {
        $(this).val($(this).val().replace(/[^\da-zA-Z]+/g, ""));
    });

    $("#customerVehicleEditDiv input[name='contact']").bind("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.stringSpaceFilter($(this).val()));
    });
    $("#customerVehicleEditDiv input[name='gsmObdImei']").live("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.stringSpaceFilter($(this).val()));
    });
    $("#customerVehicleEditDiv input[name='gsmObdImeiMoblie']").live("keyup", function () {
        $(this).val($(this).val().replace(/[^\d]+/g, ""));
    });


    //输入提示
    $("#mobile,input[name$='mobile']").blur(function () {
        var mobile = $(this).val();
        if (mobile != "") {
            if (!APP_BCGOGO.Validator.stringIsMobilePhoneNumber($(this).val())) {
                nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！");
            } else {
                //要判断是否存在同名的手机号
                var r = APP_BCGOGO.Net.syncGet({
                    url: "customer.do?method=getCustomerByMobile",
                    data: {
                        mobile: mobile
                    },
                    dataType: "json"
                });
                if (r != null && r.customerIdStr != undefined) {
                    if ($("#customerId").val() != r.customerIdStr) {
                        nsDialog.jAlert('与客户【' + r.customer + '】的手机号相同，请重新输入！');
                        this.val("");
                    }
                }
            }
        }
    });


    $("#registerMaintainMileagePeriod,#maintainMileagePeriod")
        .live("click", function (event) {
            var object = $(this);
            var dropList = App.Module.droplist;
            dropList.setUUID(G.generateUUID());
            var result = {
                "data": [
                    {"details": {"name": "3000"}, "label": "3000", "type": "option"},
                    {"details": {"name": "5000"}, "label": "5000", "type": "option"},
                    {"details": {"name": "10000"}, "label": "10000", "type": "option"}
                ],
                "uuid": dropList.getUUID()
            };
            dropList.show({
                "selector": $(this),
                "data": result,
                "onSelect": function (event, index, data) {
                    object.val(data.label);
                    object.css({"color": "#000000"});
                    dropList.hide();
                }
            });

        })
        .live("keyup", function (event) {
            $(this).click();
        });

    $("#registerMaintainTimePeriod")
        .live("click", function (event) {
            var object = $(this);
            var dropList = App.Module.droplist;
            dropList.setUUID(G.generateUUID());
            var result = {
                "data": [
                    {"details": {"name": "2"}, "label": "2", "type": "option"},
                    {"details": {"name": "3"}, "label": "3", "type": "option"},
                    {"details": {"name": "6"}, "label": "6", "type": "option"}
                ],
                "uuid": dropList.getUUID()
            };
            dropList.show({
                "selector": $(this),
                "data": result,
                "onSelect": function (event, index, data) {
                    object.val(data.label);
                    object.css({"color": "#000000"});
                    dropList.hide();
                }
            });

        })
        .live("keyup", function (event) {
            $(this).click();
        });


    $("#customerVehicleEditDiv").find("input[name='brand'],input[name='model']")
        .live("click", function (event) {
            var searchField = $(this).attr("name");
            var brandValue = $("#brand").val();
            var modelValue = $("#model").val();
            var eventKeyCode = event.which || event.keyCode;
            searchVehicleSuggestion(this, eventKeyCode, searchField, brandValue, modelValue, 0);
        })
        .live("keyup", function (event) {
            var searchField = $(this).attr("name");
            var brandValue = $("#brand").val();
            var modelValue = $("#model").val();
            var eventKeyCode = event.which || event.keyCode;
            searchVehicleSuggestion(this, eventKeyCode, searchField, brandValue, modelValue, 0);
        });

    $(".J_saveCustomerVehicleBtn").live("click", function (event) {
        event.preventDefault();

        if ($(this).attr("disabled")) {
            return;
        }
        $(this).attr("disabled", true);


        var vehicleId = $("#vehicleId").val();
        var $customerVehicleForm = $("#customerVehicleForm");
        var licenceNo = $customerVehicleForm.find("input[name='licenceNo']").val();
        var obdMileage = $customerVehicleForm.find("input[name='obdMileage']").val();
        var gsmObdImei = $customerVehicleForm.find("input[name='gsmObdImei']").val();
        var gsmObdImeiMoblie = $customerVehicleForm.find("input[name='gsmObdImeiMoblie']").val();
        var vin = $customerVehicleForm.find("input[name='vin']").val();
        var engineNo = $customerVehicleForm.find("input[name='engineNo']").val();
        if (G.Lang.isEmpty(licenceNo)) {
            $(this).removeAttr("disabled");
            nsDialog.jAlert("车牌号空缺，请完善必要的车辆信息！");
            return;
        } else if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber(licenceNo)) {
            $(this).removeAttr("disabled");
            nsDialog.jAlert("车牌号格式错误，请确认后重新输入！");
            return;
        } else if (!G.Lang.isEmpty(vin) && !APP_BCGOGO.Validator.stringIsCharacter(vin)) {
            $(this).removeAttr("disabled");
            nsDialog.jAlert("车架号格式错误，请确认后重新输入！");
            return;
        } else if (!G.Lang.isEmpty(engineNo) && !APP_BCGOGO.Validator.stringIsCharacter(engineNo)) {
            $(this).removeAttr("disabled");
            nsDialog.jAlert("发动机号格式错误，请确认后重新输入！");
            return;
        } else if (!G.Lang.isEmpty(gsmObdImei) && !APP_BCGOGO.Validator.stringIsCharacter(gsmObdImei)) {
            $(this).removeAttr("disabled");
            nsDialog.jAlert("IMEI号格式错误，请确认后重新输入！");
            return;
        } else if (!G.Lang.isEmpty(gsmObdImeiMoblie) && !APP_BCGOGO.Validator.stringIsMobilePhoneNumber(gsmObdImeiMoblie)) {
            $(this).removeAttr("disabled");
            nsDialog.jAlert("IMEI卡号格式错误，请确认后重新输入！");
            return;
        } else if (!G.Lang.isEmpty(obdMileage) && !APP_BCGOGO.Validator.stringIsStartMileage(obdMileage)) {
            $(this).removeAttr("disabled");
            nsDialog.jAlert("当前里程格式错误，请确认后重新输入！");
            return;
        }

        if (checkLicenceNoIsExisted(licenceNo)) {
            $(this).removeAttr("disabled");
            return;
        }

        APP_BCGOGO.Net.syncGet({
            url: "customer.do?method=checkIsExistGsmObdImeiInVehicle",
            data: {
                gsmObdImei: gsmObdImei,
                vehicleId: vehicleId
            },
            dataType: "json",
            success: function (json) {
                if (json.success) {
                    $customerVehicleForm.ajaxSubmit({
                        dataType: "json",
                        type: "POST",
                        success: function (jsonStr) {
                            if (!G.Lang.isEmpty(jsonStr.vehicleId)) {

                                updateCustomerVehicleInfo(jsonStr);

                                $("#customerVehicleEditDiv").hide();
                                $("#customerVehicleDiv").show();
                                showMessage.fadeMessage("45%", "24%", "slow", 3000, "更新成功！");
                            } else {
                                nsDialog.jAlert("更新失败！");
                            }
                        },
                        error: function (json) {
                            nsDialog.jAlert("网络异常，请联系客服");
                        }
                    });
                } else {
                    nsDialog.jAlert(json.msg);
                }
            },
            error: function (json) {
                nsDialog.jAlert("网络异常，请联系客服");
            }

        });

        $(this).removeAttr("disabled");
    });


    $(".J_cancelCustomerVehicleBtn").live("click", function (event) {
        event.preventDefault();

        $("#customerVehicleEditDiv").hide();
        $("#customerVehicleDiv").show();

        $("#customerVehicleForm").find(".J_formreset").each(function () {
            $(this).val(G.Lang.normalize($(this).attr("reset-value")));
        });
    });

});

function searchOBDInfo(dom, searchWord){
    var is_imei_input=$(dom).hasClass("imei_input");
    var droplist = APP_BCGOGO.Module.droplist;
    searchWord = searchWord.replace(/\s/g, '');
    var uuid = GLOBAL.Util.generateUUID();
    droplist.setUUID(uuid);
    if(G.isEmpty(searchWord)){
        return;
    }
    var data={now: new Date().getTime()};
    if(is_imei_input){
        data['imei']=searchWord;
    }else{
        data['mobile']=searchWord;
    }
    $.post('OBDManager.do?method=getShopOBDSuggestion', data,function (list) {
        droplist.show({
            "selector": $(dom),
            "data": {
                uuid: uuid,
                data:$.map(list, function (n) {
                    if(is_imei_input){
                        n.label = n.imei;
                    }else{
                        n.label = n.mobile;
                    }
                    return n;
                })
            },
            "onSelect": function (event, index, data, hook) {
                if(is_imei_input){
                    $(hook).val(data.imei);
                   $(dom).closest(".prompt_box").find(".sim_no_input").val(data.mobile);
                }else{
                    $(hook).val(data.mobile);
                    $(dom).closest(".prompt_box").find(".imei_input").val(data.imei);
                }

                droplist.hide();
            }
        });
    }, 'json');
}


function editVehicleBasicInfo() {
    $("#customerVehicleDiv").hide();
    $("#customerVehicleEditDiv").show();

    var obdId = $("#obdId").val();
    if (G.isEmpty(obdId)) {
        $("#obdImeiTrEdit").show();
        $("#obdImeiTrShow").hide();
    } else {
        $("#obdImeiTrShow").show();
        $("#obdImeiTrEdit").hide();
    }
}

function editVehicleAppointInfo() {
    $("#customerVehicleAppointInfoShow").hide();
    $("#customerVehicleAppointInfoEdit").show();
}


function updateCustomerVehicleInfo(jsonStr) {
    var $customerVehicleAppointForm = $("#customerVehicleAppointForm");
    var $customerVehicleForm = $("#customerVehicleForm");

    $customerVehicleForm.find("input[name='vehicleId']").val(jsonStr.vehicleId);
    $customerVehicleAppointForm.find("input[name='vehicleId']").val(jsonStr.vehicleId);

    $customerVehicleForm.find(".J_formreset").each(function () {
        $(this).attr("reset-value", G.Lang.normalize($(this).val()));
    });
    $customerVehicleAppointForm.find(".J_formreset").each(function () {
        $(this).attr("reset-value", G.Lang.normalize($(this).val()));
    });

    $("#customerVehicleAppointInfoShow").find(".J_appointServiceTrShow").remove();
    $("#customerVehicleAppointInfoEdit").find(".J_appointServiceTrEdit").remove();
    var appointServiceDTOs = jsonStr.customerVehicleResponse["appointServiceDTOs"];
    if (!G.isEmpty(appointServiceDTOs)) {
        var newAppointServiceEditTrHtml = "";
        var newAppointServiceShowTrHtml = "";
        $.each(appointServiceDTOs, function (index, appointServiceDTO) {

            if (appointServiceDTO.operateType != 'LOGIC_DELETE' && (!(G.isEmpty(appointServiceDTO.appointName) && G.isEmpty(appointServiceDTO.appointDate)))) {
                if (index % 4 == 0) {
                    newAppointServiceEditTrHtml += '<tr class="J_appointServiceTrEdit">';
                    newAppointServiceShowTrHtml += '<tr class="J_appointServiceTrShow">';
                }

                newAppointServiceEditTrHtml += '  <td class="test2">' +
                    ' <input type="hidden" name="appointServiceDTOs[' + index + '].appointName"' +
                    ' value="' + appointServiceDTO["appointName"] + '"/>' + appointServiceDTO["appointName"] + '：';
                newAppointServiceEditTrHtml += '      <input type="hidden" name="appointServiceDTOs[' + index + '].id" value="' + appointServiceDTO["idStr"] + '">';
                newAppointServiceEditTrHtml += '      <input type="hidden" name="appointServiceDTOs[' + index + '].operateType" value="' + appointServiceDTO["operateType"] + '">';
                newAppointServiceEditTrHtml += '      <input type="text"  style="width:75px" onclick="showDatePicker(this);" readonly="readonly" name="appointServiceDTOs[' + index + '].appointDate" reset-value="' + appointServiceDTO["appointDate"] + '" value="' + appointServiceDTO["appointDate"] + '" class="txt J_formreset"/>';
                newAppointServiceEditTrHtml += '   <a data-index="' + index + '" class="J_deleteAppointService"><img src="images/opera1.png"/></a>';
                if (index == appointServiceDTOs.length - 1) {
                    newAppointServiceEditTrHtml += '   <a class="J_addAppointService" data-index="' + index + '"><img src="images/opera2.png"/></a>';
                }
                newAppointServiceEditTrHtml += '  </td>';
                newAppointServiceShowTrHtml += '  <td>' + appointServiceDTO["appointName"] + '：' + appointServiceDTO["appointDate"] + '</td>';

                if (index % 2 == 0 && index == appointServiceDTOs.length - 1) {
                    newAppointServiceShowTrHtml += '<td></td><td></td><td></td>';
                } else if (index % 2 == 1 && index == appointServiceDTOs.length - 1) {
                    newAppointServiceShowTrHtml += '<td></td><td></td>';
                } else if (index % 2 == 2 && index == appointServiceDTOs.length - 1) {
                    newAppointServiceShowTrHtml += '<td></td>';
                }

                if (index % 4 == 3 || index == appointServiceDTOs.length - 1) {
                    newAppointServiceEditTrHtml += '</tr>';
                    newAppointServiceShowTrHtml += '</tr>';
                }
            }
        });
        $("#appointServiceTableShow").append(newAppointServiceShowTrHtml);
        $("#appointServiceTableEdit").append(newAppointServiceEditTrHtml);
    } else {
        if ($("#appointServiceTableEdit").find(".J_addAppointService").size() == 0) {
            $("#appointServiceTableEdit").find("input[name='yc']").after('<a class="J_addAppointService" data-index="0"><img src="images/opera2.png"/></a>');
        }

    }

    var $customerVehicleDiv = $("#customerVehicleDiv");
    var $customerVehicleAppointInfoShow = $("#customerVehicleAppointInfoShow");
    //更新span
    $customerVehicleDiv.find(".J_customerVehicleSpan").each(function (index) {
        $(this).text(G.isEmpty(G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")])) ? "--" : G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")]));
    });

    if (G.isEmpty(jsonStr.customerVehicleResponse.model) && G.isEmpty(jsonStr.customerVehicleResponse.brand)) {
        $customerVehicleDiv.find("span[data-key='brand']").parent("td").html('品牌车型：<span class="J_customerVehicleSpan" data-key="brand">-</span>' +
            '<span class="J_customerVehicleSpan" data-key="model">-</span>');
    } else {
        $customerVehicleDiv.find("span[data-key='brand']").parent("td").html('品牌车型：<span class="J_customerVehicleSpan" data-key="brand">' + jsonStr.customerVehicleResponse.brand + '</span>' +
            '/<span class="J_customerVehicleSpan" data-key="model">' + jsonStr.customerVehicleResponse.model + '</span>');
    }

    if (!G.isEmpty(jsonStr.customerVehicleResponse.obdMileage)) {
        $customerVehicleDiv.find("span[data-key='obdMileage']").parent("td").html('当前里程：<span class="J_customerVehicleSpan" data-key="obdMileage">' + jsonStr.customerVehicleResponse.obdMileage + '</span>公里 <a id="registerMaintainBtn" class="blue_color">保养登记</a>');
    } else {
        $customerVehicleDiv.find("span[data-key='obdMileage']").parent("td").html('当前里程：<span class="J_customerVehicleSpan" data-key="obdMileage">--</span><a id="registerMaintainBtn" class="blue_color">保养登记</a>');
    }

    if (G.isEmpty(jsonStr.customerVehicleResponse.color) && G.isEmpty(jsonStr.customerVehicleResponse.year) && G.isEmpty(jsonStr.customerVehicleResponse.engine)) {

        $customerVehicleDiv.find("span[data-key='color']").parent("td").html('年代/排量/颜色：<span class="J_customerVehicleSpan" data-key="year">-</span>' +
            '<span class="J_customerVehicleSpan" data-key="engine">-</span><span class="J_customerVehicleSpan" data-key="color"></span>');
    } else {
        $customerVehicleDiv.find("span[data-key='color']").parent("td").html('年代/排量/颜色：<span class="J_customerVehicleSpan" data-key="year">' + (G.isEmpty(jsonStr.customerVehicleResponse.year) ? '--' : jsonStr.customerVehicleResponse.year) + '</span>' +
            '/<span class="J_customerVehicleSpan" data-key="engine">' + (G.isEmpty(jsonStr.customerVehicleResponse.engine) ? '--' : jsonStr.customerVehicleResponse.engine) + '</span>/<span class="J_customerVehicleSpan" data-key="color">' + (G.isEmpty(jsonStr.customerVehicleResponse.color) ? '--' : jsonStr.customerVehicleResponse.color) + '</span>');
    }

    if (!G.isEmpty(jsonStr.customerVehicleResponse.nextMaintainMileageAccessStr)) {
        $customerVehicleDiv.find("span[data-key='nextMaintainMileageAccessStr']").text(jsonStr.customerVehicleResponse.nextMaintainMileageAccessStr);
    } else {
        $customerVehicleDiv.find("span[data-key='nextMaintainMileageAccessStr']").text('--');
    }

    if (!G.isEmpty(jsonStr.customerVehicleResponse.maintainMileagePeriod)) {
        $customerVehicleDiv.find("span[data-key='maintainMileagePeriod']").text(jsonStr.customerVehicleResponse.maintainMileagePeriod + "公里");
    } else {
        $customerVehicleDiv.find("span[data-key='maintainMileagePeriod']").text('--');
    }

    //更新4条提醒服务
    $customerVehicleAppointInfoShow.find(".J_customerVehicleAppointSpan").each(function (index) {
        $(this).text(G.isEmpty(G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")])) ? "--" : G.Lang.normalize(jsonStr.customerVehicleResponse[$(this).attr("data-key")]));
    });

    if (G.isEmpty(jsonStr.customerVehicleResponse["maintainMileage"])) {
        $customerVehicleAppointInfoShow.find(".J_maintainMileageUnitSpan").hide();
    } else {
        $customerVehicleAppointInfoShow.find(".J_maintainMileageUnitSpan").show();
    }

}

function redirectVehiclePosition() {
    var vehicleId = $("#vehicleId").val();
    var customerId = $("#customerId").val();
    var licenceNo = $("#licenceNo").val();
    window.location.href = "vehicleManage.do?method=toVehiclePosition&customerIdStr=" + customerId +"&vehicleIdStr=" + vehicleId +"&licenceNo="+licenceNo;
}

function redirectVehicleDriveLog() {
    var vehicleId = $("#vehicleId").val();
    var customerId = $("#customerId").val();
    var licenceNo = $("#licenceNo").val();
    window.location.href = "vehicleManage.do?method=toVehicleDriveLog&customerIdStr=" + customerId +"&vehicleIdStr=" + vehicleId +"&licenceNo="+licenceNo;

}

//删除整行信息的超链
function deleteVehicle() {
    var vehicleId = $("#vehicleId").val();
    var customerId = $("#customerId").val();
    var licenceNo = $("#licenceNo").val();
    var data = APP_BCGOGO.Net.syncGet({
        url: "txn.do?method=checkUndoneOrder",
        data: {
            licenceNo: licenceNo,
            vehicleId: vehicleId,
            customerId: customerId
        },
        dataType: "json"
    });
    if (data.success) {
        nsDialog.jConfirm("是否确认要删除当前客户车辆？", null, function (flag) {
            if (flag) {
                APP_BCGOGO.Net.syncGet({
                    url: "customer.do?method=deleteCustomerVehicleById",
                    data: {
                        vehicleId: vehicleId,
                        customerId: customerId
                    },
                    dataType: "json",
                    success:function(result){
                        if (result.success) {
                            window.location.href = "customer.do?method=vehicleManageList";
                        } else if(result){
                            nsDialog.jAlert(result.msg);
                        }
                    }
                });

            }
        });
    } else {
        nsDialog.jAlert(data.msg);
    }
}

function addNewAppoint() {
    var vehicleId = $("#vehicleId").val();
    var customerId = $("#customerId").val();
    APP_BCGOGO.Net.syncPost({
        url: "appoint.do?method=validateCreateAppointOrder",
        dataType: "json",
        success: function (result) {
            if (result && result.success) {
                var url = "appoint.do?method=createAppointOrderByCustomerInfo";
                if (G.Lang.isNotEmpty(vehicleId)) {
                    url = url + "&vehicleId=" + vehicleId;
                }
                if (G.Lang.isNotEmpty(customerId)) {
                    url = url + "&id=" + customerId;
                }
                window.location.href = url;
            } else if (result && !result.success) {
                nsDialog.jConfirm(result.msg, null, function (value) {
                    if (value) {
                        window.location.href = result.data;
                    }
                });
            }
        },
        error: function () {
            nsDialog.jAlert("网络异常！");
        }
    });
}


