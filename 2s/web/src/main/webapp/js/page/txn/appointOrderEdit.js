;
/**
 * @param $dom   车牌号输入框的jquery对象
 * 输入内容为空的时候不查询
 * 只根据车牌号输入去下拉查找，不关联客户ID
 */
function licenceNoDropDown($dom) {
    var vehicleNo = $($dom).val();
//    vehicleNo = App.StringFilter.inputtingLicenseNoFilter(vehicleNo);
//    $dom.val(vehicleNo);
    if (G.Lang.isEmpty(vehicleNo)) {
        return;
    } else {
        var dropList = APP_BCGOGO.Module.droplist;
        var uuid = GLOBAL.Util.generateUUID();
        dropList.setUUID(uuid);
        var ajaxUrl = "product.do?method=searchlicenseplate";
        var ajaxData = {
            plateValue: vehicleNo,
            now: new Date(),
            uuid: uuid
        };
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            var drawData = {};
            var data = [];
            if (!G.Lang.isEmpty(result)) {
                $.each(result, function (index, value) {
                    if (!G.Lang.isEmpty(value) && !G.Lang.isEmpty(value))
                        var item = {
                            details: value,
                            label: value["licenceNo"]
                        };
                    data.push(item);
                });
            }
            drawData["data"] = data;
            drawData["uuid"] = uuid;
            dropList.show({
                "selector": $dom,
                "data": drawData,
                "onSelect": function (event, index, data) {
                    $dom.val(data.details["licenceNo"]);
                    dropList.hide();
                    if (!G.Lang.isEmpty(data.details["licenceNo"])) {
                        var customerInfo = getCustomerInfoByLicenceNo({licenceNo: data.details["licenceNo"]});
                        if (!G.Lang.isEmpty(customerInfo)) {
                            drawCustomerInfo(customerInfo["customerDTO"], customerInfo["vehicleDTO"], customerInfo["memberDTO"]);
                        }
                    }

                }, "onKeyboardSelect": function (event, index, data) {
                    $dom.val(data.details["licenceNo"]);
                }
            });
        });
    }
}

/*
 查某个客户下所有的车辆信息
 */
function getLicenceNoByCustomerId($dom, customerId) {
    if ($dom && !G.Lang.isEmpty(customerId)) {
        var dropList = APP_BCGOGO.Module.droplist;
        var uuid = GLOBAL.Util.generateUUID();
        dropList.setUUID(uuid);
        var ajaxUrl = "product.do?method=searchLicenceNoByCustomerId";
        var ajaxData = {customerId: customerId};
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            var drawData = {};
            var data = [];
            if (!G.Lang.isEmpty(result)) {
                $.each(result, function (index, value) {
                    if (!G.Lang.isEmpty(value) && !G.Lang.isEmpty(value))
                        var item = {
                            details: value,
                            label: value["licenceNo"]
                        };
                    data.push(item);
                });
            }
            drawData["data"] = data;
            drawData["uuid"] = uuid;
            dropList.show({
                "selector": $dom,
                "data": drawData,
                "onSelect": function (event, index, data) {
                    $dom.val(data.details["licenceNo"]);
                    dropList.hide();
                    if (!G.Lang.isEmpty(data.details["licenceNo"])) {
                        var customerInfo = getCustomerInfoByLicenceNo({licenceNo: data.details["licenceNo"]});
                        if (!G.Lang.isEmpty(customerInfo)) {
                            drawCustomerInfo(customerInfo["customerDTO"], customerInfo["vehicleDTO"], customerInfo["memberDTO"]);
                        }
                    }

                }, "onKeyboardSelect": function (event, index, data) {
                    $dom.val(data.details["licenceNo"]);
                }
            });
        });

    }
}

//根据查询得到的结果填充页面输入框，span
function drawCustomerInfo(customerDTO, vehicleDTO, memberDTO) {
    clearCustomerInfo();
    if (!G.Lang.isEmpty(customerDTO)) {
        var customerId = G.Lang.normalize(customerDTO["idStr"]);
        var customerName = G.Lang.normalize(customerDTO["name"]);
        var customerMobile = G.Lang.normalize(customerDTO["mobile"]);
        var customerLandLine = G.Lang.normalize(customerDTO["landLine"]);
        var $customerId = $("#customerId");
        var $customer = $("#customer");
        var $customerMobile = $("#customerMobile");
        var $customerLandLine = $("#customerLandLine");
        var $appointCustomer = $("#appointCustomer");
        $customerId.val(customerId);
        $customer.val(customerName);
        $appointCustomer.val(customerName);
        if (!G.Lang.isEmpty(customerName)) {
            $customer.attr("disabled", "disabled");
        }
        $customerMobile.val(customerMobile);
        if (!G.Lang.isEmpty(customerMobile)) {
            $customerMobile.attr("disabled", "disabled");
        }
        $customerLandLine.val(customerLandLine);
        if (!G.Lang.isEmpty(customerLandLine)) {
            $customerLandLine.attr("disabled", "disabled");
        }

    }
    if (!G.Lang.isEmpty(vehicleDTO)) {
        var vehicleId = G.Lang.normalize(vehicleDTO["idStr"]);
        var licenceNo = G.Lang.normalize(vehicleDTO["licenceNo"]);
        var brand = G.Lang.normalize(vehicleDTO["brand"]);
        var model = G.Lang.normalize(vehicleDTO["model"]);
        var contact = G.Lang.normalize(vehicleDTO["contact"]);
        var vehicleMobile = G.Lang.normalize(vehicleDTO["mobile"]);
        var $vehicleId = $("#vehicleId");
        var $vehicleBrand = $("#vehicleBrand");
        var $vehicleModel = $("#vehicleModel");
        var $vehicleContact = $("#vehicleContact");
        var $vehicleMobile = $("#vehicleMobile");
        $("#vehicleNo").val(licenceNo);
        $vehicleId.val(vehicleId);
        $vehicleBrand.val(brand);
        if (!G.Lang.isEmpty(brand)) {
            $vehicleBrand.attr("disabled", "disabled");
        }
        $vehicleModel.val(model);
        if (!G.Lang.isEmpty(model)) {
            $vehicleModel.attr("disabled", "disabled");
        }
        $vehicleContact.val(contact);
        if (!G.Lang.isEmpty(contact)) {
            $vehicleContact.attr("disabled", "disabled");
        }
        $vehicleMobile.val(vehicleMobile);
        if (!G.Lang.isEmpty(vehicleMobile)) {
            $vehicleMobile.attr("disabled", "disabled");
        }
    }
    if (!G.Lang.isEmpty(memberDTO)) {
        var memberType = G.Lang.normalize(memberDTO["type"]);
        var memberNo = G.Lang.normalize(memberDTO["memberNo"]);
        var balance = G.rounding(memberDTO["balance"], 2);
        balance += "元";
        var $memberNo = $("#memberNo");
        var $memberType = $("#memberType");
        var $memberBalance = $("#memberBalance");
        $memberNo.val(memberNo);
        if (!G.Lang.isEmpty(memberNo)) {
            $memberNo.attr("disabled", "disabled");
        }
        $memberType.text(memberType);
        $memberBalance.text(balance);
    }
}

function disabledCustomerInfo(){
    var $customerId = $("#customerId");
    var $customer = $("#customer");
    var $customerMobile = $("#customerMobile");
    var $customerLandLine = $("#customerLandLine");
    var $memberNo = $("#memberNo");
    if (!G.Lang.isEmpty($customerId.val())) {
        if (!G.Lang.isEmpty($customer.val())) {
            $customer.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($customerMobile.val())) {
            $customerMobile.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($customerLandLine.val())) {
            $customerLandLine.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($memberNo.val())) {
            $memberNo.attr("disabled", "disabled");
        }
    }

    var $vehicleId = $("#vehicleId");
    var $vehicleBrand = $("#vehicleBrand");
    var $vehicleModel = $("#vehicleModel");
    var $vehicleContact = $("#vehicleContact");
    var $vehicleMobile = $("#vehicleMobile");
    if (!G.Lang.isEmpty($vehicleId.val())) {
        if (!G.Lang.isEmpty($vehicleBrand.val())) {
            $vehicleBrand.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($vehicleModel.val())) {
            $vehicleModel.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($vehicleContact.val())) {
            $vehicleContact.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($vehicleMobile.val())) {
            $vehicleMobile.attr("disabled", "disabled");
        }
    }

}

function clearCustomerInfo() {
    $("#vehicleId").val("");
    $("#customerId").val("");
    $("#customer").val("").removeAttr("disabled");
    $("#appointCustomer").val("");
    $("#customerMobile").val("").removeAttr("disabled");
    $("#customerLandLine").val("").removeAttr("disabled");
    $("#vehicleBrand").val("").removeAttr("disabled");
    $("#vehicleModel").val("").removeAttr("disabled");
    $("#vehicleContact").val("").removeAttr("disabled");
    $("#vehicleMobile").val("").removeAttr("disabled");
    $("#memberNo").val("").removeAttr("disabled");
    $("#memberType").text("");
    $("#memberBalance").text("");
    $("#remark").val("");
    $("#appointTimeStr").val("");
}

//判断是否要去获取车牌号前缀
function isToGetLicenceNoPrefix(licenceNo) {
    if (!G.Lang.isEmpty(licenceNo) && licenceNo.length == 5 && !checkhanzi(licenceNo)
        && App.Validator.stringIsLicensePlateNumber(licenceNo)) {
        return true;
    }
    return false;
}

//车辆品牌下拉建议
function searchVehicleBrandSuggestion($vehicleBrand) {
    if ($vehicleBrand) {
        var searchWord = $vehicleBrand.val();
        var ajaxData = {
            searchWord: searchWord,
            searchField: "brand"
        };
        var ajaxUrl = "product.do?method=searchBrandSuggestion";
        var dropList = APP_BCGOGO.Module.droplist;
        var uuid = GLOBAL.Util.generateUUID();
        dropList.setUUID(uuid);
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            var drawData = {};
            var data = [];
            if (!G.Lang.isEmpty(result)) {
                $.each(result, function (index, value) {
                    if (!G.Lang.isEmpty(value) && !G.Lang.isEmpty(value))
                        var item = {
                            details: value,
                            label: value["name"]
                        };
                    data.push(item);
                });
            }
            drawData["data"] = data;
            drawData["uuid"] = uuid;
            dropList.show({
                "selector": $vehicleBrand,
                "data": drawData,
                "onSelect": function (event, index, data) {
                    var selectVehicleBrand = data.details["name"];
                    $vehicleBrand.val(selectVehicleBrand);
                    if (selectVehicleBrand != $vehicleBrand.attr("lastValue")) {
                        $("#vehicleModel").val("");
                    }
                    dropList.hide();
                }
            });
        });
    }

}

//车型的下拉建议
function searchVehicleModelSuggestion($vehicleBrand, $vehicleModel) {
    if ($vehicleBrand && $vehicleModel) {
        var model = $vehicleModel.val();
        var brand = $vehicleBrand.val();
        var ajaxData = {
            brandValue: brand,
            searchWord: model,
            searchField: "model"
        };
        var ajaxUrl = "product.do?method=searchBrandSuggestion";
        var dropList = APP_BCGOGO.Module.droplist;
        var uuid = GLOBAL.Util.generateUUID();
        dropList.setUUID(uuid);
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            var drawData = {};
            var data = [];
            if (!G.Lang.isEmpty(result)) {
                $.each(result, function (index, value) {
                    if (!G.Lang.isEmpty(value) && !G.Lang.isEmpty(value))
                        var item = {
                            details: value,
                            label: value["name"]
                        };
                    data.push(item);
                });
            }
            drawData["data"] = data;
            drawData["uuid"] = uuid;
            dropList.show({
                "selector": $vehicleModel,
                "data": drawData,
                "onSelect": function (event, index, data) {
                    var selectVehicleModel = data.details["name"];
                    $vehicleModel.val(selectVehicleModel);
                    if (selectVehicleModel != $vehicleBrand.attr("lastValue")) {
                        var brandArray = getVehicleBrandByModel(selectVehicleModel);
                        if (!G.Lang.isEmpty(brandArray)) {
                            $vehicleBrand.val(brandArray[0]);
                        }
                    }
                    dropList.hide();
                }
            });
        });
    }

}

//客户名+手机号下拉选择
function searchCustomerByNameOrMobile($customer) {
    if ($customer && !G.Lang.isEmpty($customer.val())) {
        var searchWord = $customer.val();
        var ajaxUrl = "txn.do?method=getCustomerName";
        var ajaxData = {
            name: searchWord
        };

        var dropList = APP_BCGOGO.Module.droplist;
        var uuid = GLOBAL.Util.generateUUID();
        dropList.setUUID(uuid);
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            var drawData = {};
            var data = [];
            if (!G.Lang.isEmpty(result)) {
                $.each(result, function (index, value) {
                    if (!G.Lang.isEmpty(value) && !G.Lang.isEmpty(value))
                        var customerName = G.Lang.normalize(value["name"]);
                    var customerMobile = G.Lang.normalize(value["mobile"]);
                    var label = "";
                    if (!G.Lang.isEmpty(customerName)) {
                        label = customerName;
                    }
                    if (!G.Lang.isEmpty(label) && !G.Lang.isEmpty(customerMobile)) {
                        label += "+";
                    }
                    if (!G.Lang.isEmpty(customerMobile)) {
                        label += customerMobile;
                    }
                    var item = {
                        details: value,
                        label: label
                    };
                    data.push(item);
                });
            }
            drawData["data"] = data;
            drawData["uuid"] = uuid;
            dropList.show({
                "selector": $customer,
                "data": drawData,
                "onSelect": function (event, index, data) {
                    if (!G.Lang.isEmpty(data) && !G.Lang.isEmpty(data.details)) {
                        var selectCustomerName = data.details["name"];
                        $customer.val(selectCustomerName);
                        var customerId = G.Lang.normalize(data.details["idStr"]);
                        $("#customerId").val(customerId);
                        var customerInfo = getCustomerInfoById({customerId: customerId});
                        if (!G.Lang.isEmpty(customerInfo)) {
                            var vehicleDTOs = customerInfo["vehicleDTOs"];
                            var vehicleDTO = null;
                            if (!G.Lang.isEmpty(vehicleDTOs) && vehicleDTOs.length == 1) {
                                vehicleDTO = vehicleDTOs[0];
                            }
                            drawCustomerInfo(customerInfo["customerDTO"], vehicleDTO, customerInfo["memberDTO"]);
                        }
                    }
                    $customer.removeAttr("onKeyboardSelect");
                    dropList.hide();
                }, "onKeyboardSelect": function (event, index, data) {
                    if (!G.Lang.isEmpty(data) && !G.Lang.isEmpty(data.details)) {
                        var selectCustomerName = G.Lang.normalize(data.details["name"]);
                        $customer.val(selectCustomerName);
                        $customer.attr("onKeyboardSelect", true);
                        var customerId = G.Lang.normalize(data.details["idStr"]);
                        $("#customerId").val(customerId);
                        var selectMobile = G.Lang.normalize(data.details["mobile"]);
                        $("#customerMobile").val(selectMobile);
                        var selectLandLine = G.Lang.normalize(data.details["landLine"]);
                        $("#customerLandLine").val(selectLandLine);
                    } else {
                        $customer.removeAttr("onKeyboardSelect");
                        $("#customerId").val("");
                        $("#customerMobile").val("");
                        $("#customerLandLine").val("");
                    }
                }
            });
        });
    }
}

//加载的时候设置页面可以编辑的输入框
function setCustomerInfoDisabled() {
    var $id = $("#id");
    if (!G.Lang.isEmpty($id.val())) {
        var $customer = $("#customer");
        var $customerMobile = $("#customerMobile");
        var $customerLandLine = $("#customerLandLine");
        var $memberNo = $("#memberNo");

        if (!G.Lang.isEmpty($customer.val())) {
            $customer.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($customerMobile.val())) {
            $customerMobile.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($customerLandLine.val())) {
            $customerLandLine.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($memberNo.val())) {
            $memberNo.attr("disabled", "disabled");
        }

        var $vehicleBrand = $("#vehicleBrand");
        var $vehicleModel = $("#vehicleModel");
        var $vehicleContact = $("#vehicleContact");
        var $vehicleMobile = $("#vehicleMobile");
        var $vehicleNo = $("#vehicleNo");
        if (!G.Lang.isEmpty($vehicleBrand.val())) {
            $vehicleBrand.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($vehicleModel.val())) {
            $vehicleModel.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($vehicleContact.val())) {
            $vehicleContact.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($vehicleMobile.val())) {
            $vehicleMobile.attr("disabled", "disabled");
        }
        if (!G.Lang.isEmpty($vehicleNo.val())) {
            $vehicleNo.attr("disabled", "disabled");
        }
        if($("#appointWay").val() =="APP"){
            $customer.attr("disabled", "disabled");
            $customerMobile.attr("disabled", "disabled");
            $customerLandLine.attr("disabled", "disabled");
            $memberNo.attr("disabled", "disabled");
            $vehicleBrand.attr("disabled", "disabled");
            $vehicleModel.attr("disabled", "disabled");
            $vehicleContact.attr("disabled", "disabled");
            $vehicleMobile.attr("disabled", "disabled");
            $vehicleNo.attr("disabled", "disabled");
        }

//         $("#customer").attr("disabled", "disabled");
//         $("#customerMobile").attr("disabled", "disabled");
//         $("#customerLandLine").attr("disabled", "disabled");
//         $("#memberNo").attr("disabled", "disabled");
//         $("#vehicleBrand").attr("disabled", "disabled");
//         $("#vehicleModel").attr("disabled", "disabled");
//         $("#vehicleContact").attr("disabled", "disabled");
//         $("#vehicleMobile").attr("disabled", "disabled");
//         $("#vehicleNo").attr("disabled", "disabled");

    }
}

function generateAppointOrder(){
   var $customerName = $("#customer");
    if(G.Lang.isEmpty($customerName.val())){
        $customerName.val($("#vehicleNo").val());
    }
}

function validateSaveAppointOrder() {
    var isSuccess = true;
    var resultMsg = "";
//    var vehicleNo = $("#vehicleNo").val();
//    if(G.Lang.isEmpty(vehicleNo)){
//        isSuccess = false;
//        resultMsg += "车牌号不能为空！<br>"
//    } else if (!App.Validator.stringIsLicensePlateNumber(vehicleNo)) {
//        isSuccess = false;
//        resultMsg += "输入的车牌号码不符合规范！<br>"
//    }
    if (G.Lang.isEmpty($("#customerId").val())) {
        isSuccess = false;
        resultMsg += "请选择或者新增客户！<br>"
    }
    if(G.Lang.isEmpty($("#serviceItemDTOs0\\.serviceId").val())){
        isSuccess = false;
        resultMsg += "服务类型不能为空！<br>"
    }
    if (G.Lang.isEmpty($("#appointTimeStr").val())) {
        isSuccess = false;
        resultMsg += "预计服务时间不能为空！<br>"
    }


    if(!isSuccess){
        resultMsg += "请检查";
        nsDialog.jAlert(resultMsg);
    }
    return isSuccess;
}
function validateModifyAppointOrder() {
    var isSuccess = true;
    var resultMsg = "";
//    var vehicleNo = $("#vehicleNo").val();
//    if(G.Lang.isEmpty(vehicleNo)){
//        isSuccess = false;
//        resultMsg += "车牌号不能为空！<br>"
//    } else if (!App.Validator.stringIsLicensePlateNumber(vehicleNo)) {
//        isSuccess = false;
//        resultMsg += "输入的车牌号码不符合规范！<br>"
//    }
    if(G.Lang.isEmpty($("#serviceItemDTOs0\\.serviceId").val())){
        isSuccess = false;
        resultMsg += "服务类型不能为空！<br>"
    }
    if (G.Lang.isEmpty($("#appointTimeStr").val())) {
        isSuccess = false;
        resultMsg += "预计时间不能为空！<br>"
    }
    if(!isSuccess){
        resultMsg += "请检查";
        nsDialog.jAlert(resultMsg);
        return false;
    }
    var result;
       APP_BCGOGO.Net.syncPost({
           url: "appoint.do?method=validateUpdateAppointOrder",
           dataType: "json",
           data: {
               id:$("#id").val()
           },
           success: function (json) {
               result = json;
           },
           error: function () {
               nsDialog.jAlert("网络异常！");
               isSuccess = false;
           }
       });
    if (result && !result.success) {
        nsDialog.jAlert(result.msg);
        isSuccess = false;
    }

    return isSuccess;
}

$(function () {

        //新的预约单不需要这个逻辑了
//    setCustomerInfoDisabled();

    $("#vehicleNo").bind("click",function (e) {
        //1.客户id，无内容 点击下拉查询当前客户名下的车辆
        //2.无客户Id，无内容 不查询
        //3.无客户Id，有内容；有客户Id，有内容；查询逻辑
        var keyCode = e.which || e.keyCode;
        var customerId = $("#customerId").val();
        var licenceNo = $(this).val();
        licenceNo = App.StringFilter.inputtingLicenseNoFilter(licenceNo);
        $(this).val(licenceNo);
        if (G.Lang.isEmpty(customerId) || !G.Lang.isEmpty(licenceNo)) {
            licenceNoDropDown($(this));
        } else if (!G.Lang.isEmpty(customerId)) {
            getLicenceNoByCustomerId($(this), customerId);
        }
    }).bind("keyup",function (e) {
            //1，回车键
            //2，上下键
            //3，不是上下左右的时候 需要查询
            var $vehicleNo = $(this);
            var licenceNo = $vehicleNo.val();
            licenceNo = App.StringFilter.inputtingLicenseNoFilter(licenceNo);
            $vehicleNo.val(licenceNo);
            var eventKeyCode = e.which || e.keyCode;
            var keyName = GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode);
            if (keyName.search(/left|up|right|down/g) == -1 && !(keyName === "enter" && isToGetLicenceNoPrefix(licenceNo))) {
                licenceNoDropDown($vehicleNo);
            } else if (keyName === "enter" && isToGetLicenceNoPrefix(licenceNo)) {
                //取默认车牌号前缀
                var licenceNoPrefix = "";
                APP_BCGOGO.Net.syncGet({url: "product.do?method=userLicenseNo",
                    dataType: "json", success: function (json) {
                        licenceNoPrefix = json;
                    }
                });
                if (!G.Lang.isEmpty(licenceNoPrefix) && !G.Lang.isEmpty(licenceNoPrefix[0]["localCarNo"])) {
                    licenceNo = licenceNoPrefix[0]["localCarNo"] + licenceNo;
                    $vehicleNo.val(licenceNo);
                }
                $vehicleNo.attr("lastValue", licenceNo);
                clearCustomerInfo();
                if (!G.Lang.isEmpty(licenceNo)) {
                    var customerInfo = getCustomerInfoByLicenceNo({licenceNo: licenceNo});
                    if (!G.Lang.isEmpty(customerInfo)) {
                        drawCustomerInfo(customerInfo["customerDTO"], customerInfo["vehicleDTO"], customerInfo["memberDTO"]);
                    }
                }
            }
        }).bind("blur",function (e) {
            var $vehicleNo = $(this);
            var licenceNo = $vehicleNo.val();
            licenceNo = App.StringFilter.inputtingLicenseNoFilter(licenceNo);
            $vehicleNo.val(licenceNo);
            if (licenceNo == $vehicleNo.attr("lastValue")) {
                return;
            }
            if (isToGetLicenceNoPrefix(licenceNo)) {
                var licenceNoPrefix = "";
                APP_BCGOGO.Net.syncGet({url: "product.do?method=userLicenseNo",
                    dataType: "json", success: function (json) {
                        licenceNoPrefix = json;
                    }
                });
                if (!G.Lang.isEmpty(licenceNoPrefix) && !G.Lang.isEmpty(licenceNoPrefix[0]["localCarNo"])) {
                    licenceNo = licenceNoPrefix[0]["localCarNo"] + licenceNo;
                    $vehicleNo.val(licenceNo);
                }
            }
            clearCustomerInfo();
//            if(!G.Lang.isEmpty(licenceNo) && !App.Validator.stringIsLicensePlateNumber(licenceNo)){
//                nsDialog.jAlert("输入的车牌号码不符合规范，请检查！",null,function(){
//                    $vehicleNo.val("");
//                });
//                return false;
//            }

            if (!G.Lang.isEmpty(licenceNo) && App.Validator.stringIsLicensePlateNumber(licenceNo)) {
                var customerInfo = getCustomerInfoByLicenceNo({licenceNo: licenceNo});
                if (!G.Lang.isEmpty(customerInfo)) {
                    drawCustomerInfo(customerInfo["customerDTO"], customerInfo["vehicleDTO"], customerInfo["memberDTO"]);
                }
            }


        }).bind("focus", function () {
            var $vehicleNo = $(this);
            var licenceNo = $vehicleNo.val();
            licenceNo = App.StringFilter.inputtingLicenseNoFilter(licenceNo);
            $vehicleNo.val(licenceNo).attr("lastValue", licenceNo);
        });

    $("#vehicleBrand").bind("focus",function (e) {
        var $vehicleBrand = $(this);
        var brand = $vehicleBrand.val();
        var brandFiltered = App.StringFilter.inputtingProductNameFilter(brand);
        if (brandFiltered != brand) {
            brand = brandFiltered;
            $vehicleBrand.val(brand);
        }
        $vehicleBrand.attr("lastValue", brand);
        searchVehicleBrandSuggestion($vehicleBrand);
    }).bind("keyup",function (e) {
            var $vehicleBrand = $(this);
            var brand = $vehicleBrand.val();
            var brandFiltered = App.StringFilter.inputtingProductNameFilter(brand);
            if (brandFiltered != brand) {
                brand = brandFiltered;
                $vehicleBrand.val(brand);
            }

            var eventKeyCode = e.which || e.keyCode;
            var keyName = GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode);
            if (keyName.search(/left|up|right|down/g) == -1) {
                searchVehicleBrandSuggestion($vehicleBrand);
            }
        }).bind("blur", function () {
            var $vehicleBrand = $(this);
            var brand = $vehicleBrand.val();
            var brandFiltered = App.StringFilter.inputtingProductNameFilter(brand);
            if (brandFiltered != brand) {
                brand = brandFiltered;
                $vehicleBrand.val(brand);
            }
            if (brand != $vehicleBrand.attr("lastValue")) {
                $("#vehicleModel").val("");
            }
        });

    $("#vehicleModel").bind("focus",function () {
        var $vehicleModel = $(this);
        var $vehicleBrand = $("#vehicleBrand");
        var model = $vehicleModel.val();
        var modelFiltered = App.StringFilter.inputtingProductNameFilter(model);
        if (modelFiltered != model) {
            model = modelFiltered;
            $vehicleModel.val(model);
        }
        $vehicleModel.attr("lastValue", model);
        searchVehicleModelSuggestion($vehicleBrand, $vehicleModel);
    }).bind("keyup",function () {
            var $vehicleModel = $(this);
            var $vehicleBrand = $("#vehicleBrand");
            var model = $vehicleModel.val();
            var modelFiltered = App.StringFilter.inputtingProductNameFilter(model);
            if (modelFiltered != model) {
                model = modelFiltered;
                $vehicleModel.val(model);
            }
            var eventKeyCode = e.which || e.keyCode;
            var keyName = GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode);
            if (keyName.search(/left|up|right|down/g) == -1) {
                searchVehicleModelSuggestion($vehicleBrand, $vehicleModel);
            }
        }).bind("blur", function () {
            var $vehicleModel = $(this);
            var $vehicleBrand = $("#vehicleBrand");
            var model = $vehicleModel.val();
            model = App.StringFilter.inputtingProductNameFilter(model);
            $vehicleModel.val(model);
            if (model != $vehicleBrand.attr("lastValue")) {
                var brandArray = getVehicleBrandByModel(model);
                if (!G.Lang.isEmpty(brandArray)) {
                    $vehicleBrand.val(brandArray[0]);
                }
            }
        });

    $("#vehicleMobile").bind("keyup",function () {
        var $vehicleMobile = $(this);
        var mobile = $vehicleMobile.val();
        var mobileFiltered = App.StringFilter.inputtingNumberFilter(mobile);
        if (mobileFiltered != mobile) {
            $vehicleMobile.val(mobileFiltered);
        }

    }).bind("blur", function () {
            var $vehicleMobile = $(this);
            var mobile = $vehicleMobile.val();
            var mobileFiltered = App.StringFilter.inputtingNumberFilter(mobile);
            if (mobileFiltered != mobile) {
                mobile = mobileFiltered;
                $vehicleMobile.val(mobile);
            }
            if (!G.Lang.isEmpty(mobile) && !App.Validator.stringIsMobilePhoneNumber(mobile)) {
                nsDialog.jAlert("输入的手机号码可能不正确，为了不影响您的后续使用，请确认后重新输入！", null, function () {
                    $vehicleMobile.val("");
                });
            }
        });

    $("#customer").bind("keyup",function (e) {
        var $customer = $(this);
        var customerSearchWord = $customer.val();
        var customerSearchWordFiltered = App.StringFilter.inputtingBlankFilter(customerSearchWord);
        if (customerSearchWordFiltered != customerSearchWord) {
            customerSearchWord = customerSearchWordFiltered;
            $customer.val(customerSearchWord);
        }
        if (!G.Lang.isEmpty(customerSearchWord)) {
            var eventKeyCode = e.which || e.keyCode;
            var keyName = GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode);
            if (keyName.search(/left|up|right|down/g) == -1) {
                searchCustomerByNameOrMobile($customer)
            }
        }
    }).bind("blur", function () {
            var $customer = $(this);
            if ($customer.attr("onKeyboardSelect")) {
                $customer.removeAttr("onKeyboardSelect");
                var customerId = $("#customerId").val();
                if (!G.Lang.isEmpty(customerId)) {
                    var customerInfo = getCustomerInfoById({customerId: customerId});
                    if (!G.Lang.isEmpty(customerInfo)) {
                        var vehicleDTOs = customerInfo["vehicleDTOs"];
                        var vehicleDTO = null;
                        if (!G.Lang.isEmpty(vehicleDTOs) && vehicleDTOs.length == 1) {
                            vehicleDTO = vehicleDTOs[0];
                        }
                        drawCustomerInfo(customerInfo["customerDTO"], vehicleDTO, customerInfo["memberDTO"]);
                    }
                }
            }
        });

    $("#memberNo").bind("keyup",function (e) {
        var eventKeyCode = e.which || e.keyCode;
        var keyName = GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode);
        if (keyName.search(/enter/g) > -1) {
            var $memberNo = $(this);
            var memberNo = $memberNo.val();
            if (!G.Lang.isEmpty(memberNo)) {
                var customerInfo = getCustomerInfoByMemberNo({memberNo: memberNo});
                if (!G.Lang.isEmpty(customerInfo) && !G.Lang.isEmpty(customerInfo["customerDTO"])) {
                    var vehicleDTOs = customerInfo["vehicleDTOs"];
                    var vehicleDTO = null;
                    if (!G.Lang.isEmpty(vehicleDTOs) && vehicleDTOs.length == 1) {
                        vehicleDTO = vehicleDTOs[0];
                    }
                    drawCustomerInfo(customerInfo["customerDTO"], vehicleDTO, customerInfo["memberDTO"]);
                } else {
                    nsDialog.jAlert("当前会员卡无关联客户信息，请重新输入？", null, function () {
                        $memberNo.val("");
                    })
                }
            }
        }
    }).bind("blur", function () {

            var $memberNo = $(this);
            var memberNo = $memberNo.val();
            if (!G.Lang.isEmpty(memberNo)) {
                var customerInfo = getCustomerInfoByMemberNo({memberNo: memberNo});
                if (!G.Lang.isEmpty(customerInfo) && !G.Lang.isEmpty(customerInfo["customerDTO"])) {
                    var vehicleDTOs = customerInfo["vehicleDTOs"];
                    var vehicleDTO = null;
                    if (!G.Lang.isEmpty(vehicleDTOs) && vehicleDTOs.length == 1) {
                        vehicleDTO = vehicleDTOs[0];
                    }
                    drawCustomerInfo(customerInfo["customerDTO"], vehicleDTO, customerInfo["memberDTO"]);
                } else {
                    nsDialog.jAlert("当前会员卡无关联客户信息，请重新输入？", null, function () {
                        $memberNo.val("");
                    })
                }
            }
        });

    $("#customerMobile").bind("keyup",function () {
        var $customerMobile = $(this);
        var customerMobile = $customerMobile.val();
        var customerMobileFiltered = App.StringFilter.inputtingIntFilter(customerMobile);
        if (customerMobileFiltered != customerMobile) {
            customerMobile = customerMobileFiltered;
            $customerMobile.val(customerMobile);
        }
    }).bind("blur",function () {
            var $customerMobile = $(this);
            var customerMobile = $customerMobile.val();
            var orderId = $("#id").val();
            if ($customerMobile.attr("lastValue") != customerMobile) {
                var customerInfo = getCustomerInfoByCustomerMobile({customerMobile: customerMobile});
                if (!G.Lang.isEmpty(customerInfo) && !G.Lang.isEmpty(customerInfo["customerDTO"])) {
                    var customerDTO = customerInfo["customerDTO"];
                    var customerName = G.Lang.normalize(customerDTO["name"]);

                    if (G.Lang.isEmpty(orderId)) {
                        nsDialog.jConfirm("与已存在客户【" + customerName + "】的手机号相同，是否使用该客户资料?", null, function (returnVal) {
                            if (returnVal) {
                                var vehicleDTOs = customerInfo["vehicleDTOs"];
                                var vehicleDTO = null;
                                if (!G.Lang.isEmpty(vehicleDTOs) && vehicleDTOs.length == 1) {
                                    vehicleDTO = vehicleDTOs[0];
                                }
                                drawCustomerInfo(customerDTO, vehicleDTO, customerInfo["memberDTO"]);
                            } else {
                                $customerMobile.val("");
                            }
                        });
                    } else {
                        nsDialog.jAlert("与已存在客户【"+customerName+"】的手机号相同，请重新输入!",null,function(){
                            $customerMobile.val("");
                        });
                    }
                }

            }
        }).bind("focus", function () {
            var $customerMobile = $(this);
            var customerMobile = $customerMobile.val();
            $customerMobile.attr("lastValue", customerMobile);
        });

    $("#customerLandLine").bind("blur",function () {
        var $customerLandLine = $(this);
        var customerLandLine = $customerLandLine.val();
        if ($customerLandLine.attr("lastValue") != customerLandLine) {
            var customerInfo = getCustomerInfoByCustomerLandLine({customerLandLine: customerLandLine});
            if (!G.Lang.isEmpty(customerInfo) && !G.Lang.isEmpty(customerInfo["customerDTO"])) {
                var customerDTO = customerInfo["customerDTO"];
                var customerName = G.Lang.normalize(customerDTO["name"]);
                nsDialog.jConfirm("与已存在客户【" + customerName + "】的座机号相同，是否使用该客户资料?", null, function (returnVal) {
                    if (returnVal) {
                        var vehicleDTOs = customerInfo["vehicleDTOs"];
                        var vehicleDTO = null;
                        if (!G.Lang.isEmpty(vehicleDTOs) && vehicleDTOs.length == 1) {
                            vehicleDTO = vehicleDTOs[0];
                        }
                        drawCustomerInfo(customerDTO, vehicleDTO, customerInfo["memberDTO"]);
                    } else {
                        $customerLandLine.val("");
                    }
                });
            }

        }
    }).bind("focus", function () {
            var $customerLandLine = $(this);
            var customerLandLine = $customerLandLine.val();
            $customerLandLine.attr("lastValue", customerLandLine);
        });



    $("#appointTimeStr").datetimepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": "",
        "showHour":true,
        "showMinute":true,
        "onSelect": function (dateText, inst) {
            var lastValue = $(this).attr("lastValue");
            if (lastValue == dateText) {
                return;
            }
            if (dateText) {
                var myDate = G.Date.getCurrentFormatDate();
                if (myDate.replace(/[- ]+/, "") > dateText.replace(/[- ]+/, "")) {
                    nsDialog.jAlert("请选择今天及以后的日期。");
                    $(this).val("");
                }
            }
        }
    });

    $("#assistantMan").bind("click focus keyup", function (event) {
        event = event || event.which;
        var keyCode = event.keyCode;
        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }
        droplistLite.show({
            event: event,
            hiddenId: "assistantId",
            id: "id",
            name: "name",
            data: "txn.do?method=searchWorks"
        });
    });

    $("#toMoreCustomerInfo").click(function () {
        if ($("#customerId").val()) {
            var r = APP_BCGOGO.Net.syncGet({async: false, url: "customer.do?method=checkCustomerStatus",
                data: {customerId: $("#customerId").val(), now: new Date()}, dataType: "json"});
            if (!r.success) {
                nsDialog.jAlert("此客户已被删除或合并，不能看信息！");
                return;
            }
        }
        bcgogo.checksession({
            "parentWindow": window.parent,
            'iframe_PopupBox': $("#iframe_moreUserInfo")[0],
            'src': encodeURI("txn.do?method=clientInfo"
                + "&customer=" + $("#customer").val()
                + "&mobile=" + $("#customerMobile").val()
                + "&customerId=" + $("#customerId").val()
                + "&landLine=" + $("#customerLandLine").val()
                + "&vehicleContact=" + $("#vehicleContact").val()
                + "&licenceNo=" + $("#vehicleNo").val()
                + "&vehicleMobile=" + $("#vehicleMobile").val()
                + "&brand=" + $("#vehicleBrand").val()
                + "&model=" + $("#vehicleModel").val()
            )
        });
    });

    $("#clearAppointOrder").bind("click",function(){
        window.location.href = "appoint.do?method=showAppointOrderList";
//        $("#vehicleNo").val("").removeAttr("disabled");
//      clearCustomerInfo();
    });

    $("#saveAppointOrder").bind("click", function () {
        var $saveAppointOrder = $("#saveAppointOrder");
        if ($saveAppointOrder.attr("lock")) {
            return;
        }
        $saveAppointOrder.attr("lock", true);
        if (validateSaveAppointOrder()) {
            try {
                generateAppointOrder();
                $("#appointOrderDTOForm input").removeAttr("disabled");
                $("#appointOrderDTOForm").attr("action", "appoint.do?method=saveAppointOrder").submit();
            } catch (e){
                $saveAppointOrder.removeAttr("lock");
            }
        } else {
            $saveAppointOrder.removeAttr("lock");
        }
    });

    $("#modifyAppointOrder").bind("click",function(){
        var $modifyAppointOrder = $("#modifyAppointOrder");
        if ($modifyAppointOrder.attr("lock")) {
            return;
        }
        $modifyAppointOrder.attr("lock", true);
        if (validateModifyAppointOrder()) {
            try {
                generateAppointOrder();
                $("#appointOrderDTOForm input").removeAttr("disabled");
                $("#appointOrderDTOForm").attr("action", "appoint.do?method=updateAppointOrder").submit();
            } catch (e){
                $modifyAppointOrder.removeAttr("lock");
            }
        } else {
            $modifyAppointOrder.removeAttr("lock");
        }
    });

    $("#printBtn").live("click",function(){
        window.showModalDialog("appoint.do?method=getAppointOrderToPrint&appointOrderId=" + $("#id").val()  +
            "&now=" + new Date(), '预约单', "dialogWidth=1024px;dialogHeight=768px,status=no;help=no");
    });

    $("#cancelModifyBtn").bind("click", function () {
        var appointOrderId = G.Lang.normalize($("#id").val());
        if (G.Lang.isNotEmpty(appointOrderId)) {
            window.location.href = "appoint.do?method=showAppointOrderDetail&appointOrderId=" + appointOrderId;
        } else {
            window.location.href = "appoint.do?method=showAppointOrderList";
        }
    });

    //新的预约单不需要这个逻辑了
//    var createFromFlag = $("#createFromFlag").val();
//    if(G.Lang.isNotEmpty(createFromFlag) && createFromFlag == "customerDetail"){
//        disabledCustomerInfo();
//    }
});

