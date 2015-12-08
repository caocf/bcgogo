var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
var invoiceCommon = APP_BCGOGO.Module.wjl.invoiceCommon;
$(function () {
    $(".itemAmount,.itemPrice").each(function(){
        $(this).val(dataTransition.simpleRounding(parseFloat($(this).val()), 2));
    });
    tableUtil.setRowBackgroundColor("#insuranceItemTB", null, null, 'odd');
    tableUtil.setRowBackgroundColor("#insuranceServiceTB", null, null, 'odd');
    tableUtil.tableStyle("#insuranceItemTB", null, "odd");
    tableUtil.tableStyle("#insuranceServiceTB", null, "odd");
    $(document).click(function (e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (target && typeof(target.id) == "string" && target.id.split(".")[1] != "productName" && target.id.split(".")[1] != "brand"
            && target.id.split(".")[1] != "spec" && target.id.split(".")[1] != "model"
            && target.id.split(".")[1] != "vehicleBrand" && target.id.split(".")[1] !=
            "vehicleModel"
            && target.id.split(".")[1] != "vehicleYear" && target.id.split(".")[1] !=
            "vehicleEngine"
            && target.id != "div_brand") {
            $("#div_brand")[0].style.display = "none";
        }
    });

    $(document).click(function (e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName") {
            $("#div_serviceName").hide();
        }
    });
    //删除行 产品
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "itemMinusBtn") return;
        $(target).closest("tr").remove();
        isShowItemAddButton();
        tableUtil.setRowBackgroundColor("#insuranceItemTB", null, null, 'odd');
        setTotal();
    });

    //增加行   产品
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "itemPlusBtn")  return;

        var ischeck = checkVehicleInfo(target);       //TODO 检查改行数据是否符合验证规则
        if (!ischeck && ischeck != null) return;
        //采购单检查是否相同
        if ($(".item").size() >= 2) {
            if (invoiceCommon.checkSameItemForOrder("item")) {
                nsDialog.jAlert("单据有重复内容，请修改或删除。");
                return false;
            }
            if (invoiceCommon.checkSameCommodityCode("item")) {
                nsDialog.jAlert("商品编码有重复内容，请修改或删除。");
                return false;
            }
        }
        insuranceItemAdd();
    });

    //删除行 服务
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "serviceMinusBtn") return;
        var idPrefix = $(target).attr("id").split(".")[0];
        $(target).closest("tr").remove();
        isShowServiceAddButton();
        tableUtil.setRowBackgroundColor("#insuranceServiceTB", null, null, 'odd');
        setTotal();
    });

    //增加行        服务
    $(document).bind('click', function (event) {
        var target = event.target;
        if (target.className != "servicePlusBtn")  return;
        var idPrefix = target.id.split(".")[0];
        var service = $("#" + idPrefix + "\\.service").val();
        if (service == "") {
            alert("请输入修理项目名称!");
            return;
        }
        if (!$("#" + idPrefix + "\\.service").val()) return;
        insuranceServiceAdd();
    });

    $("#reportDateStr,#accidentDateStr,#surveyDateStr,#estimateDateStr").datepicker({
        "numberOfMonths": 1,
        "changeYear": true,
        "changeMonth": true,
        "dateFormat": "yy-mm-dd",
        "yearRange": "c-5:c+5",
        "maxDate": 0,
        "yearSuffix": "",
        "showButtonPanel": true
    });
    $("#insureEndDateStr,#insureStartDateStr").datepicker({
        "numberOfMonths": 1,
        "changeYear": true,
        "changeMonth": true,
        "dateFormat": "yy-mm-dd",
        "yearRange": "c-5:c+5",
        "yearSuffix": "",
        "showButtonPanel": true,
        "onClose": function (dateText, inst) {
            if (!$(this).val()) {
                return;
            }
            $(this).removeClass("hover_txt");
            if ($("#insureEndDateStr").val() && $("#insureEndDateStr").val()
                && G.getDate($("#insureStartDateStr").val()).getTime() > G.getDate($("#insureEndDateStr").val()).getTime()) {
                nsDialog.jAlert("保险到期日期不能早于投保日期，请修改!");
                $(this).val("");
            }

        }
    });

    $("#policyNo,#reportNo").bind("blur", function () {
        if (!$(this).val()) {
            return;
        }
        $(this).val($.trim($(this).val()));
        var data = {};
        if ($(this).attr("id") == "policyNo") {
        } else if ($(this).attr("id") == "reportNo") {
            data = {"reportNo": $(this).val(), "id": $("#id").val(), "validateScene": "CHECK_REPORT_NO"};
        } else {
            return;
        }
        APP_BCGOGO.Net.syncPost({
            url: "insurance.do?method=validateSaveInsurance&" + Math.random() * 10000000,
            dataType: "json",
            data: data,
            success: function (result) {
                if (!result.success) {
                    nsDialog.jAlert(result.msg);
                    $(this).val("");
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常");
            }
        });
    });


    $("#insuranceCompanyId").bind("change", function () {
        $("#insuranceCompany").val($(this).find("option:selected").text());
    });
    $("#saveInsurance").bind("click", function () {
        if ($("#saveInsurance").attr("lock")) {
            return;
        }
        $("#saveInsurance").attr("lock", true);
        if (!validateSaveInsuranceOnPage()) {
            $("#saveInsurance").removeAttr("lock");
            return;
        } else {
            $("#insuranceOrderForm").attr("action", "insurance.do?method=saveInsuranceOrder&dealingType=unsettled");
            $("#insuranceOrderForm").submit();
        }
    });

    $("#nullifyInsurance").bind("click",function(){
        if ($("#nullifyInsurance").attr("lock")) {
            return;
        }
        $("#nullifyInsurance").attr("lock", true);
        if (!validateSaveInsuranceOnPage()) {
            $("#nullifyInsurance").removeAttr("lock");
            return;
        } else {
            $("#insuranceOrderForm").attr("action", "insurance.do?method=nullifyOrSettledInsuranceOrder&dealingType=nullify");
            $("#insuranceOrderForm").submit();
        }

    });

    $("#settledInsurance").bind("click",function(){
        if ($("#settledInsurance").attr("lock")) {
            return;
        }
        $("#settledInsurance").attr("lock", true);
        if (!validateSaveInsuranceOnPage()) {
            $("#settledInsurance").removeAttr("lock");
            return;
        } else {
            if($("#status").val()=="UNSETTLED"){//当为新增保险单时
                $("#insuranceOrderForm").attr("action", "insurance.do?method=saveInsuranceOrder&dealingType=settled");
                $("#insuranceOrderForm").submit();
            }else{
                $("#insuranceOrderForm").attr("action", "insurance.do?method=nullifyOrSettledInsuranceOrder&dealingType=settled");
                $("#insuranceOrderForm").submit();
            }

        }

    });

    $("#toRepairOrder").bind("click", function () {
        if ($("#repairOrderId").val()) {
//            nsDialog.jAlert("当前保险单已存在关联的施工单，无法重复生成！",null,function(){
//            });
//            http://localhost:8080/web/txn.do?method=getRepairOrder&repairOrderId=10000010134223321&print=false&resultMsg=success&btnType=dispatch
            window.open("txn.do?method=getRepairOrder&repairOrderId=" + $("#repairOrderId").val()+"&insuranceStatus=unsettled");
        } else if($("#repairDraftOrderId").val()) {
            window.open("txn.do?method=getRepairOrderByDraftOrder&draftOrderId=" + $("#repairDraftOrderId").val()+"&insuranceStatus=unsettled");
        } else if ($("#id").val()) {
            if (!validateSaveInsuranceOnPage()) {
                return;
            }
            var data = {"id": $("#id").val(), "validateScene": "CHECK_REPAIR_ORDER_ID"};
            APP_BCGOGO.Net.syncPost({
                url: "insurance.do?method=validateSaveInsurance&" + Math.random() * 10000000,
                dataType: "json",
                data: data,
                success: function (result) {
                    if (result.success) {
                        $("#insuranceOrderForm").attr("action", "insurance.do?method=createRepairOrderByInsurance&insuranceStatus=unsettled");
                        $("#insuranceOrderForm").submit();
                    } else {
                        nsDialog.jAlert(result.msg);
                    }
                },
                error: function () {
                    nsDialog.jAlert("网络异常");
                }
            })
        } else {
            if (!validateSaveInsuranceOnPage()) {
                return;
            }
            $("#insuranceOrderForm").attr("action", "insurance.do?method=createRepairOrderByInsurance");
            $("#insuranceOrderForm").submit();
        }
    });

    $(".itemPrice,.itemAmount").live("blur", function () {
        setTotal();
    });

    $(".checkNumberEmpty").live("keyup blur", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
    });

    $("#claimsPercentage").live("keyup blur", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 1));
        if ($(this).val() * 1 > 100) {
            $(this).val(100);
        }
    });

    $("#personalClaims").live("keyup blur", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 1));

    });

    $("#personalClaimsPercentage").live("keyup blur", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 1));
        if ($(this).val() * 1 > 100) {
            $(this).val(100);
        }
    });


    $("#claims").live("keyup blur", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 1));
    });

    $("#cancelBtn").bind("click", function () {
        if ($("#id").val()) {
            window.location.href = "insurance.do?method=showInsuranceOrder&insuranceOrderId=" + $("#id").val();
        } else {
            window.location.href = "insurance.do?method=createInsuranceOrder";
        }
    });

    $("#print_insurance_btn").bind("click", function () {
        if ($("#id").val()) {
            window.open("insurance.do?method=printInsuranceOrder&insuranceOrderId=" + $("#id").val(), "_blank");
        }
    });
    $("#mobile").blur(function () {
        if ($(this).val()) {
            check.inputCustomerMobileBlur($(this)[0], null);
        }
    });
    $("#reporterContact").blur(function () {
        if ($(this).val()) {
            check.saveContact($(this)[0]);
        }
    })
    $("input[name$='.service']").live('click focus keyup', function (event) {

        var keyCode = event.keyCode || event.which,
            obj = event.target;
        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }

        if ($(this).val() != $(obj).attr("hiddenValue")) {
            $("#" + this.id.split(".")[0] + "\\.serviceId").val("");
            $(this).removeAttr("hiddenValue");
        }

        var _selectedHandler = function (event, index, data, hook) {

            //fill the label into the input & fill the id into the hidden.
            $(hook).val(data.label);
            $(hook).parent().find('input[id$="serviceId"]').val(data.id);

            //
            var valflag = hook.value != $(this).html() ? true : false;
            new clearItemUtil().clearByFlag(hook, valflag);
            //$(hook).val($(this).attr("title"));
            var serviceId = data.id;
            var idPrefix = hook.id.split(".")[0];

            $("#" + idPrefix + "\\.serviceId").val(serviceId);
            if ($(".service:last").find('input[id$="service"]').val()) {
                insuranceServiceAdd();
            }
            $(hook).blur();

            $("#" + idPrefix + "\\.total").removeAttr("disabled");
            $("#" + idPrefix + "\\.total").val(GLOBAL.Number.filterZero(data.price));

            var prefix = hook.id.split(".")[0];

            APP_BCGOGO.Net.syncPost({
                url: "category.do?method=getCategoryByServiceId",
                data: {
                    "serviceId": serviceId,
                    "now": new Date()
                },
                dataType: "json",
                success: function (jsonObject) {
                    if (null != jsonObject.data) {
                        var id = jsonObject.data.idStr;
                        var name = jsonObject.data.categoryName;
                        id = null == id ? "" : id;
                        name = null == name ? "" : name;
                        $("#" + prefix + "\\.businessCategoryId").val(id);
                        $("#" + prefix + "\\.businessCategoryName").val(name);
                        $("#" + prefix + "\\.businessCategoryName").attr("hiddenValue", name);

                        if (name == "洗车" || name == "美容") {
                            $("#" + prefix + "\\.businessCategoryName").attr("disabled", "disabled");
                        } else {
                            $("#" + prefix + "\\.businessCategoryName").removeAttr("disabled");
                        }
                    } else {
                        $("#" + prefix + "\\.businessCategoryId").val("");
                        $("#" + prefix + "\\.businessCategoryName").val("");
                        $("#" + prefix + "\\.businessCategoryName").removeAttr("hiddenValue");
                        $("#" + prefix + "\\.businessCategoryName").removeAttr("disabled");
                    }
                }
            });
            tableUtil.tableStyle('#table_task', '.i_tabelBorder,.table_title');
        };

        droplistLite.show({
            event: event,
            isEditable: "true",
            isDeletable: "true",
            hiddenId: "serviceId",
            id: "id",
            name: "name",
            data: "txn.do?method=searchService",
            onsave: {
                callback: function (event, index, data, hook) {

                    var _id = data.id;
                    var _name = $.trim(data.label);

                    var flag = false;
                    var result = "";

                    if (_name.length <= 0) {
                        nsDialog.jAlert("施工内容不能为空");
                        return false;
                    }

                    APP_BCGOGO.Net.syncAjax({
                        url: "category.do?method=checkServiceNameRepeat",
                        dataType: "json",
                        data: {
                            serviceName: _name,
                            serviceId: _id
                        },
                        success: function (data) {
                            result = data.resu;
                        }
                    });

                    if (result == 'error') {
                        nsDialog.jAlert("服务名已存在");
                        return false;
                    }
                    if (result == 'inUse') {
                        nsDialog.jAlert("服务正在被进行中的单据使用，无法修改");
                        return false;
                    }

                    //Check if this item is disabled.
                    var checkResult, checkServiceId;
                    APP_BCGOGO.Net.syncAjax({
                        url: "category.do?method=checkServiceDisabled",
                        dataType: "json",
                        data: {
                            serviceName: _name
                        },
                        success: function (data) {
                            checkResult = data.resu;
                            checkServiceId = data.serviceId ? data.serviceId : "";
                        }
                    });

                    if (checkResult == "serviceDisabled") {
                        nsDialog.jConfirm("此项目以前被删除过，是否恢复？", null, function (_result) {
                            if (_result) {
                                window.document.location = "category.do?method=updateServiceStatus&serviceId=" + checkServiceId;
                            }
                        });
                    } else {
                        //request save result & handler.
                        APP_BCGOGO.Net.syncPost({
                            url: "category.do?method=ajaxUpdateServiceName",
                            data: {
                                serviceId: _id,
                                serviceName: _name,
                                now: new Date()
                            },
                            dataType: "json",
                            success: function (_result) {

                                //success.
                                if (_result.success) {
                                    data.label = _name;
                                    data.categoryName = _name;

                                    //get the event
                                    var $obj = $(obj);

                                    nsDialog.jAlert(_result.msg, _result.title);
                                }

                                //fail.
                                else if (!_result.success) {
                                    nsDialog.jAlert(_result.msg, _result.title, function () {
                                        data.label = data.categoryName;
                                    });
                                }

                                //exception.
                                else {
                                    nsDialog.jAlert("数据异常！");
                                }
                            },
                            //request error.
                            error: function () {
                                nsDialog.jAlert("保存失败！");
                            }
                        });
                    }
                }
            },
            ondelete: {
                callback: function (event, index, data) {
                    var serviceId = data.id;
                    var serviceName = data.label;

                    var deleteFlag = true;
                    var url = "category.do?method=checkServiceUsed";
                    APP_BCGOGO.Net.syncPost({
                        url: url,
                        data: {
                            serviceId: serviceId
                        },
                        dataType: "json",
                        success: function (data) {
                            if ("error" != data.resu) {
                                deleteFlag = false;
                            } else {
                                nsDialog.jAlert("此服务项目已被使用，不能删除！");
                            }
                        }
                    });

                    if (!deleteFlag) {

                        //get the request result.
                        var _result = APP_BCGOGO.Net.syncGet({
                            url: "category.do?method=ajaxDeleteService",
                            data: {
                                serviceId: data.id,
                                now: new Date()
                            },
                            dataType: "json"
                        });

                        //when failed and successed.
                        if (!_result.success) {
                            nsDialog.jAlert(_result.msg, _result.title);
                        } else if (_result.success) {
                            nsDialog.jAlert(_result.msg, _result.title);
                        } else {
                            nsDialog.jAlert("数据异常！");
                        }
                    }
                }
            },
            afterSelected: function (event, index, data, hook) {
                _selectedHandler(event, index, data, hook);
            }
        });
    });


    //页面加载的时候提示上一次校验问题
    if ($("#insuranceMessage").val()) {
        showMessage.fadeMessage("40%", "20%", 2000, 2000, $("#insuranceMessage").val());
    }

    isShowItemAddButton();
    isShowServiceAddButton();
    setTotal();

    $("#policyNo,#insureStartDateStr,#licenceNo,#insureEndDateStr,#customer,#brand,#model,#chassisNumber,#engineNumber")
        .blur(function () {
            var $_this = $(this);
            if (!$_this.val()) {
                $_this.focus();
                $_this.addClass("hover_txt");
            } else {
                $_this.removeClass("hover_txt");
            }
        })
        .keyup(function () {
            var $_this = $(this);
            if (!$_this.val()) {
                $_this.focus();
                $_this.addClass("hover_txt");
            } else {
                $_this.removeClass("hover_txt");
            }
        });
    $("#licenceNo")
        .blur(function () {
            var $_this = $(this), val = $_this.val();
            if (!val || !APP_BCGOGO.Validator.stringIsLicensePlateNumber(val)) {
                $_this.focus();
//                $_this.addClass("hover_txt");
            } else {
//                $_this.removeClass("hover_txt");
            }
        })

        /*.keyup(function () {
         var $_this = $(this);
         if (!$_this.val()) {
         $_this.focus();
         $_this.addClass("hover_txt");
         } else {
         $_this.removeClass("hover_txt");
         }
         })*/;

});

function insuranceOrderVehicleAdjustment() {
    if (getOrderType() != "INSURANCE_ORDER") {
        return;
    }
    var $_model = $("#model");
    var $_brand = $("#brand");
    if ($_model.val()) {
        $_model.removeClass("hover_txt");
    } else {
        $_model.addClass("hover_txt");
    }
    if ($_brand.val()) {
        $_brand.removeClass("hover_txt");
    } else {
        $_brand.addClass("hover_txt");
    }
}

function validateSaveInsuranceOnPage() {
    var isValidateSuccess = true;
    var validateMsg = "",
        id="";
    if (!$("#policyNo").val()) {
        isValidateSuccess = false;
        validateMsg += "对不起，【保险单号】信息您没填写！<br>";
        id = "policyNo";
    }
    if (!$("#insureStartDateStr").val()) {
        isValidateSuccess = false;
        validateMsg += "对不起，【投保时间】信息您没填写！<br>";
        if (!id) {
            id = "insureStartDateStr";
        }
    }
    if (!$("#insureEndDateStr").val()) {
        isValidateSuccess = false;
        validateMsg += "对不起，【到期日期】信息您没填写！<br>";
        if (!id) {
            id = "insureEndDateStr";
        }
    }
    if (!$("#customer").val()) {
        isValidateSuccess = false;
        validateMsg += "对不起，【被保险人】信息您没填写！<br>";
        if (!id) {
            id = "customer";
        }
    }
    if (!$("#licenceNo").val()) {
        isValidateSuccess = false;
        validateMsg += "对不起，【车牌号】信息您没填写！<br>";
        if (!id) {
            id = "licenceNo";
        }
    }
    if (!$("#brand").val()) {
        isValidateSuccess = false;
        validateMsg += "对不起，【车辆品牌】信息您没填写！<br>";
        if (!id) {
            id = "brand";
        }
    }
    if (!$("#model").val()) {
        isValidateSuccess = false;
        validateMsg += "对不起，【车型】信息您没填写！<br>";
        if (!id) {
            id = "model";
        }
    }
    if (!$("#chassisNumber").val()) {
        isValidateSuccess = false;
        validateMsg += "对不起，【车架号】信息您没填写！<br>";
        if (!id) {
            id = "chassisNumber";
        }
    }
    if (!$("#engineNumber").val()) {
        isValidateSuccess = false;
        validateMsg += "对不起，【发动机号】信息您没填写！<br>";
        if (!id) {
            id = "engineNumber";
        }
    }
    if (isMaterialSame()) {
        isValidateSuccess = false;
        validateMsg += "更换项目有重复项目或者空行，请修改或删除！<br>"
    }
    if (isRepairSame()) {
        isValidateSuccess = false;
        validateMsg += "修理项目有重复项目或者空行，请修改或删除！<br>"
    }
    if (!APP_BCGOGO.Validator.stringIsLicensePlateNumber($("#licenceNo").val())) {
        isValidateSuccess = false;
        validateMsg += "输入的车牌号码不符合规范，请检查！<br>";
        if (!id) {
            id = "licenceNo";
        }
    }
    if (!isValidateSuccess) {
        nsDialog.jAlert(validateMsg, null, function () {
            if(id != '') {
                $("#" + id).focus();
            }
        });
    }

    return isValidateSuccess;
}

function setTotal() {
    var total = 0;
    $(".itemPrice").each(function (i) {
        var price = $(this).val();
        var idPrefix = $(this).attr("id").split(".")[0];
        var amount = $("#" + idPrefix + "\\.amount").val();
        if ($.trim(price) && $.trim(amount)) {
            var count = parseFloat(price * amount) * 1;
            $("#" + idPrefix + "\\.total").val(dataTransition.rounding(count, 2));
            $("#" + idPrefix + "\\.totalLbl").text(dataTransition.rounding(count, 2));
        }
        htmlNumberFilter($("#" + idPrefix + "\\.price").add("#" + idPrefix + "\\.amount").add("#" + idPrefix + "\\.totalLbl"), true);
    })
}

function isShowItemAddButton() {
    //如果初始化的话就默认加一行
    if ($(".item").size() <= 0) {
        insuranceItemAdd();
    }
    $(".item .itemPlusBtn").remove();
    var opera1Id = $(".item:last").find("td:last>input[id$='itemMinusBtn']").attr("id");
    if (!opera1Id) return;
    $(".item:last").find("td:last>input[id$='itemMinusBtn']").after(' <input class="itemPlusBtn" ' +
        ' id="itemDTOs' + (opera1Id.split(".")[0].substring(8)) + '.itemPlusBtn"' +
        'name="itemDTOs[' + (opera1Id.split(".")[0].substring(8)) + '].itemPlusBtn" ' + ' type="button"/>');
}

function isShowServiceAddButton() {
    //如果初始化的话就默认加一行
    if ($(".service").size() <= 0) {
        insuranceServiceAdd();
    }
    $(".service .servicePlusBtn").remove();
    var opera1Id = $(".service:last").find("td:last>input[id$='serviceMinusBtn']").attr("id");
    if (!opera1Id) return;
    $(".service:last").find("td:last>input[id$='serviceMinusBtn']").after(' <input class="servicePlusBtn" ' +
        ' id="serviceDTOs' + (opera1Id.split(".")[0].substring(11)) + '.servicePlusBtn"' +
        'name="serviceDTOs[' + (opera1Id.split(".")[0].substring(11)) + '].servicePlusBtn" ' + ' type="button"/>');
}

function insuranceItemAdd() {
    var tr = $(getInsuranceItemTrSample()).clone();     //TODO 克隆模版，初始化所有的INPUT
    $(tr).find("input").val("");
    $(tr).find("input,span,label").each(function () {
        //去除文本框的自动填充下拉框
        if ($(this).attr("type") == "text") {
            $(this).attr("autocomplete", "off");
        }
        if (!$(this) || !$(this).attr("id")) {
            return true;
        }
        //replace id
        var idStrs = $(this).attr("id").split(".");
        var tcNum = $(".item").size();
        while (checkThisDom(tcNum, idStrs[1])) {
            tcNum++;
        }
        var newId = "itemDTOs" + tcNum + "." + idStrs[1];
        $(this).attr("id", newId);
        //replace name
        var nameStr = $(this).attr("name");
        if (nameStr == undefined || nameStr == '') {
            return true;
        }
        if ($(this).attr("name").split(".")[1]) {
            var newName = "itemDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
            $(this).attr("name", newName);
        }
    });
    $("#insuranceItemTB tr:last").before($(tr));
    isShowItemAddButton();
    tableUtil.tableStyle("#insuranceItemTB", null, "odd");
    tableUtil.setRowBackgroundColor("#insuranceItemTB", null, null, 'odd');
    return $(tr);
}

function insuranceServiceAdd() {
    var tr = $(getInsuranceServiceTrSample()).clone();     //TODO 克隆模版，初始化所有的INPUT
    $(tr).find("input").val("");
    $(tr).find("input,span").each(function () {
        //去除文本框的自动填充下拉框
        if ($(this).attr("type") == "text") {
            $(this).attr("autocomplete", "off");
        }
        if (!$(this) || !$(this).attr("id")) {
            return true;
        }
        //replace id
        var idStrs = $(this).attr("id").split(".");
        var tcNum = $(".service").size();
        while (checkServiceDom(tcNum, idStrs[1])) {
            tcNum++;
        }
        var newId = "serviceDTOs" + tcNum + "." + idStrs[1];
        $(this).attr("id", newId);
        //replace name
        var nameStr = $(this).attr("name");
        if (nameStr == undefined || nameStr == '') {
            return true;
        }
        if ($(this).attr("name").split(".")[1]) {
            var newName = "serviceDTOs[" + tcNum + "]." + $(this).attr("name").split(".")[1];
            $(this).attr("name", newName);
        }
    });
    $("#insuranceServiceTB tr:last").before($(tr));
    isShowServiceAddButton();
    tableUtil.tableStyle("#insuranceServiceTB", null, "odd");
    tableUtil.setRowBackgroundColor("#insuranceServiceTB", null, null, 'odd');
    return $(tr);
}

function getInsuranceItemTrSample() {
    var trSample = '<tr class="item">' +
        '<td style="padding-left:10px;"></td>' +
        '<td >' +
        '   <input id="itemDTOs0.commodityCode" name="itemDTOs[0].commodityCode" type="text" class="txt checkStringEmpty" value="" maxlength="20"/>' +
        '   <input id="itemDTOs0.id" name="itemDTOs[0].id" type="hidden" value=""/>' +
        '   <input id="itemDTOs0.productId" name="itemDTOs[0].productId" type="hidden" value=""/>' +
        '   <input id="itemDTOs0.vehicleBrand" name="itemDTOs[0].vehicleBrand" type="hidden" value=""/>' +
        '   <input id="itemDTOs0.vehicleModel" name="itemDTOs[0].vehicleModel" type="hidden" value=""/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.productName" name="itemDTOs[0].productName"  class="txt checkStringEmpty" value="" type="text" maxlength="50"/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.brand" name="itemDTOs[0].brand"  class="txt checkStringEmpty" value="" type="text" maxlength="50"/>' +
        '</td>' +
        '<td>' +
        '   <input id="itemDTOs0.spec" name="itemDTOs[0].spec"  class="txt checkStringEmpty" value="" type="text" maxlength="50"/>' +
        '</td>' +
        '<td>' +
        '   <input id="itemDTOs0.model" name="itemDTOs[0].model"  class="txt checkStringEmpty" value="" type="text" maxlength="50"/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.price" name="itemDTOs[0].price"  value="" class="txt checkNumberEmpty itemPrice"  type="text" maxlength="20" data-filter-zero="true"/>' +
        '</td>' +
        '<td>' +
        '	<input id="itemDTOs0.amount" name="itemDTOs[0].amount"  value="" class="txt checkNumberEmpty itemAmount"  type="text" maxlength="20" data-filter-zero="true"/>' +
        '</td>' +
        '<td>' +
        '   <label id="itemDTOs0.unitLbl" name="itemDTOs[0].unitLbl"></label> ' +
        '   <input id="itemDTOs0.unit" name="itemDTOs[0].unit"  value="" type="hidden" class="itemUnit"/>' +
        '   <input type="hidden" id="itemDTOs0.storageUnit" name="itemDTOs[0].storageUnit" class="itemStorageUnit" value=""/>' +
        '   <input type="hidden" id="itemDTOs0.sellUnit" name="itemDTOs[0].sellUnit" class="itemSellUnit" value=""/>' +
        '   <input type="hidden" id="itemDTOs0.rate" name="itemDTOs[0].rate" class="itemRate" value=""/>' +
        '</td>' +
        '<td>' +
        '   <label id="itemDTOs0.totalLbl" name="itemDTOs[0].totalLbl"></label>' +
        '   <input id="itemDTOs0.total" name="itemDTOs[0].total" class="itemTotal"   value="" type="hidden"/>' +
        '</td>' +
        '   <td style="border-right:none;">' +
        '	<input class="itemMinusBtn" type="button" id="itemDTOs0.itemMinusBtn" name="itemDTOs[0].itemMinusBtn">' +
        '   </td>' +
        '</tr>';
    return trSample;
}
function getInsuranceServiceTrSample() {
    var trSample = '<tr class="service">' +
        '<td style="padding-left:10px;"></td>' +
        '<td >' +
        '   <input id="serviceDTOs0.service" name="serviceDTOs[0].service" type="text" class="txt checkStringEmpty" value="" maxlength="50"/>' +
        '   <input id="serviceDTOs0.id" name="serviceDTOs[0].id" type="hidden" value=""/>' +
        '   <input id="serviceDTOs0.serviceId" name="serviceDTOs[0].serviceId" type="hidden" value=""/>' +
        '</td>' +
        '<td>' +
        '	<input id="serviceDTOs0.total" name="serviceDTOs[0].total"  class="txt checkNumberEmpty serviceTotal" value="" type="text" maxlength="50" data-filter-zero="true"/>' +
        '</td>' +
        '   <td style="border-right:none;">' +
        '	<input class="serviceMinusBtn" type="button" id="serviceDTOs0.serviceMinusBtn" name="serviceDTOs[0].serviceMinusBtn">' +
        '   </td>' +
        '</tr>';
    return trSample;
}

function customerBlur() {
    $("#customer").attr("title", $("#customer").val());
    if ($("#customer").attr("lastValue") == $("#customer").val()) {
        return;
    }
    if ($("#customerId").val()) {
        $("#customerId").val("");
        $("#mobile").val("");
    }
    if ($("#vehicleId").val()) {
        $("#vehicleId").val("");
        $("#licenceNo").val("");
        $("#brand").val("");
        $("#model").val("");
        $("#chassisNumber").val("");
        $("#engineNumber").val("");
    }
}

function customerFocus() {
    $("#customer").attr("lastValue", $("#customer").val());
}

function initInsuranceCustomerAndVehicle(json) {
    if (json && json.customerDTO) {
        var customerDTO = json.customerDTO;
        $("#customer").val(customerDTO.name);
        $("#customer").attr("title", customerDTO.name);
        $("#mobile").val(customerDTO.mobile);
        $("#customerId").val(customerDTO.idStr);
    }

    if (json && json.vehicleDTO) {
        var vehicleDTO = json.vehicleDTO;
        $("#licenceNo").val(vehicleDTO.licenceNo);
        $("#vehicleId").val(vehicleDTO.idStr);
        $("#brand").val(vehicleDTO.brand);
        $("#model").val(vehicleDTO.model);
        $("#chassisNumber").val(vehicleDTO.chassisNumber);
        $("#engineNumber").val(vehicleDTO.engineNo);
    } else {
        if ($("#vehicleId").val()) {
            $("#vehicleId").val("");
            $("#brand").val("");
            $("#model").val("");
            $("#chassisNumber").val("");
            $("#engineNumber").val("");
        }
    }
    var checkDom = $("#policyNo,#insureStartDateStr,#insureEndDateStr,#customer,#brand,#model,#chassisNumber,#engineNumber");
    for (var i = 0; i < checkDom.length; i++) {
        var $_dom = $(checkDom[i]);
        if ($_dom.val()) {
            $_dom.removeClass("hover_txt");
        }
    }
}

function checkEmptyRepairMaterialRow($tr) {
    var propertys = ["productName", "brand", "spec", "model"];
    var itemInfo = "";
    for (var i = 0, len = propertys.length; i < len; i++) {
        itemInfo += $tr.find("input[id$='." + propertys[i] + "']").val();
    }
    return G.isEmpty(itemInfo);
}

function isMaterialSame() {
    //自动删除最后的空白行
    var $last_tr = $(".item:last");
    while ($last_tr.index() >= 1 && checkEmptyRepairMaterialRow($last_tr)) {
        $last_tr.find(".itemMinusBtn").click();
        $last_tr = $(".item:last");
    }
    var isSame = false;
    var cur = "", $lastItem = $(".item").last();
    cur += $lastItem.find("input[id$='.commodityCode']").eq(0).val() + " , ";
    cur += $lastItem.find("input[id$='.productName']").eq(0).val() + " , ";
    cur += $lastItem.find("input[id$='.brand']").eq(0).val() + " , ";
    cur += $lastItem.find("input[id$='.model']").eq(0).val() + " , ";
    cur += $lastItem.find("input[id$='.spec']").eq(0).val() + " , ";
    cur += $lastItem.find("input[id$='.vehicleBrand']").eq(0).val() + " , ";
    cur += $lastItem.find("input[id$='.vehicleModel']").eq(0).val() + " , ";
//    if ($(".item").size() > 1 && $lastItem.find("td").eq(1).find("input").eq(0).val() == "") {
//        return true;
//    }
    var item1s = [];
    var productIds = [];
    var productIdIndex = 0;
    $(".item").each(function (i) {
        var older = "", $this = $(this);
        older += $this.find("input[id$='.commodityCode']").eq(0).val() + " , ";
        older += $this.find("input[id$='.productName']").eq(0).val() + " , ";
        older += $this.find("input[id$='.brand']").eq(0).val() + " , ";
        older += $this.find("input[id$='.model']").eq(0).val() + " , ";
        older += $this.find("input[id$='.spec']").eq(0).val() + " , ";
        older += $this.find("input[id$='.vehicleBrand']").eq(0).val() + " , ";
        older += $this.find("input[id$='.vehicleModel']").eq(0).val() + " , ";
        if ($.inArray(older, item1s) >= 0) {
            isSame = true;
            return true;
        }
        item1s[i] = older;

        var itemProductId = $(".item").eq(i).find("input[id$='.productId']").eq(0).val();
        if (itemProductId) {
            if ($.inArray(itemProductId, productIds) >= 0) {
                isSame = true;
                return true;
            }
            productIds[productIdIndex++] = itemProductId;
        }
    });
    return isSame;
}

function isRepairSame() {
    //自动删除最后的空白行
    var $last_tr = $(".service:last");
    while ($last_tr.index() >= 1 && checkEmptyRepairServiceRow($last_tr)) {
        $last_tr.find(".serviceMinusBtn").click();
        $last_tr = $(".service:last");
    }
    var isSame = false;
    var cur = "";
    cur = cur + $(".service").last().find("td").eq(1).find("input[id$='service']").val();      //施工单的service输入框的值
    if ($(".service").size() > 1 && $(".service").last().find("td").eq(1).find("input[id$='service']").val() == "") {
        return true;
    }
    var items = [];
    $(".service").each(function (i) {
        var older = "";
        older = older + jQuery(".service").eq(i).find("input[id$='service']").val();
        if ($.inArray(older, items) >= 0) {
            isSame = true;
            return true;
        }
        items[i] = older;
    });
    return isSame;
}

function checkEmptyRepairServiceRow($tr) {
    var propertys = ["service", "total"];
    var itemInfo = "";
    for (var i = 0, len = propertys.length; i < len; i++) {
        itemInfo += $tr.find("input[id$='." + propertys[i] + "']").val();
    }
    return G.isEmpty(itemInfo);
}