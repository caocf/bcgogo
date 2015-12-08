;
/*
data :{licenceNo:XXX}
 return :{
    customerDTO:{},
    vehicleDTO:{},
    memberDTO:{}
 }
 */
function getCustomerInfoByLicenceNo(data) {
    var returnJson;
    APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=ajaxGetCustomerInfo",
        dataType: "json",
        data: data,
        success: function (json) {
            returnJson = json;
        },
        error: function () {
            nsDialog.jAlert("网络异常！");
        }
    });
    return returnJson;
}

/*
根据车型带出车辆品牌
model :"" string
 return :["",""] array
 */
function getVehicleBrandByModel(model) {
    var vehicleArray = [];
    if (!G.Lang.isEmpty(model)) {
        var ajaxData = {
            modelValue: model,
            searchField: "brand"
        };
        var ajaxUrl = "product.do?method=searchBrandSuggestion";
        APP_BCGOGO.Net.syncPost({
            url: ajaxUrl,
            dataType: "json",
            data: ajaxData,
            success: function (json) {
                $.each(json, function (index, value) {
                    if (!G.Lang.isEmpty(value) && !G.Lang.isEmpty(value["name"])) {
                        vehicleArray.push(value["name"]);
                    }
                })
            },
            error: function () {
                nsDialog.jAlert("网络异常！");
            }
        });
    }
    return vehicleArray;
}

/*
data :{customerId:XXX}
 return :{
    customerDTO:{},
    vehicleDTOs:[{},{}],
    memberDTO:{}
 }
 */
function getCustomerInfoById(data){
    var returnJson;
    APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=ajaxGetCustomerInfoById",
        dataType: "json",
        data: data,
        success: function (json) {
            returnJson = json;
        },
        error: function () {
            nsDialog.jAlert("网络异常！");
        }
    });
    return returnJson;
}

/*
data :{memberNo:XXX}
 return :{
    customerDTO:{},
    vehicleDTOs:[{},{}],
    memberDTO:{}
 }
 */
function getCustomerInfoByMemberNo(data){
    var returnJson;
    APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=ajaxGetCustomerInfoByMemberNo",
        dataType: "json",
        data: data,
        success: function (json) {
            returnJson = json;
        },
        error: function () {
            nsDialog.jAlert("网络异常！");
        }
    });
    return returnJson;
}

/*
data :{customerMobile:XXX}
 return :{
    customerDTO:{},
    vehicleDTOs:[{},{}],
    memberDTO:{}
 }
 */
function getCustomerInfoByCustomerMobile(data){
    var returnJson;
    APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=ajaxGetCustomerInfoByCustomerMobile",
        dataType: "json",
        data: data,
        success: function (json) {
            returnJson = json;
        },
        error: function () {
            nsDialog.jAlert("网络异常！");
        }
    });
    return returnJson;
}

/*
data :{customerLandLine:XXX}
 return :{
    customerDTO:{},
    vehicleDTOs:[{},{}],
    memberDTO:{}
 }
 */
function getCustomerInfoByCustomerLandLine(data){
    var returnJson;
    APP_BCGOGO.Net.syncPost({
        url: "customer.do?method=ajaxGetCustomerInfoByCustomerLandLine",
        dataType: "json",
        data: data,
        success: function (json) {
            returnJson = json;
        },
        error: function () {
            nsDialog.jAlert("网络异常！");
        }
    });
    return returnJson;
}


