/**
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 14-5-5
 * Time: 下午3:21
 * To change this template use File | Settings | File Templates.
 */




function searchVehicleSuggestion(domObject,keycode, searchField, brandValue, modelValue, rowIndex) { //车辆信息查询
    var searchValue = domObject.value.replace(/[\ |\\]/g, "");
    if ("brand" == searchField) {
        modelValue = "";
    }
    var dropList = App.Module.droplist;
    dropList.setUUID(G.generateUUID());
    var ajaxData = {
        searchWord: searchValue, searchField: searchField,
        vehicleBrand: searchField == 'brand' ? '' : brandValue,
        vehicleModel: searchField == 'model' ? '' : modelValue,
        uuid: dropList.getUUID()
    };

    var ajaxUrl = "product.do?method=searchVehicleSuggestionForGoodsBuy";
    App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        if(!G.isEmpty(result.data[0])){
            G.completer({
                    'domObject':domObject,
                    'keycode':keycode,
                    'title':result.data[0].label}
            );
        }
        dropList.show({
            "selector": $(domObject),
            "data": result,
            "onSelect": function (event, index, data) {
                $(domObject).val(data.label);
                $(domObject).css({"color": "#000000"});
                dropList.hide();
            }
        });
    });
}

function showDatePicker(domObj) {
    $(domObj).datepicker({
        "numberOfMonths": 1,
        "showButtonPanel": true,
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-100:c+100",
        "yearSuffix": ""
    }).datepicker("show");
}

function checkLicenceNoIsExisted(licenceVal) {
    var flag = true;
    var customerNameAjax = ""; //记录AJAX获得的当前车牌号的实际客户姓名
    var customerId; //记录AJAX获得的当前车牌号的实际客户ID
    //判断该车牌号是否已经有归属
    var r;
    APP_BCGOGO.Net.syncGet({
        url: "customer.do?method=licenceNoIsExisted",
        data: {
            licenceVal: licenceVal,
            customerName: $("#name").val()
        },
        dataType: "json",
        success: function (json) {
            r = json;
        }
    });
    if (r.length == 0) {
        flag = false;
    } else {
        customerNameAjax = r[0].customerName;
        customerId = r[0].customerId;
        if (customerNameAjax != "" && customerNameAjax.length > 0) {
            //当前客户与原车主同名，给予提示
            if ($("#name").val() == customerNameAjax && $("#customerId").val() == "") {
                nsDialog.jAlert("此车牌客户【" + customerNameAjax + "】已存在！");
                this.value = "";
            } else if ($("#name").val() != customerNameAjax) { //与原车主不同名

              if(r[0].isObd =="true"){
                nsDialog.jAlert("此车牌【" + licenceVal + "】已属于其他客户【" + customerNameAjax + "】,是OBD车辆，不能添加");
                licenceVal = "";
                customerNameAjax = "";
                customerId = "";
                $(this).val("");
                return true;
              }


              nsDialog.jConfirm("此车牌【" + licenceVal + "】已属于其他客户【" + customerNameAjax + "】，不能用于当前客户,是否转让到当前客户？", null, function (flag) {
                    if (flag) {
                        nsDialog.jConfirm("请再次确认此车的欠款是否已经结清，并办妥相关转让手续。", null, function (flag) {
                            if (flag) {
                                //将原车主的车牌关联信息删除
                                customerNameAjax = "";
                                customerId = "";
                                var j;
                                APP_BCGOGO.Net.syncGet({
                                    url: "customer.do?method=deleteCustomerLicenceNo",
                                    data: {
                                        licenceVal: licenceVal
                                    },
                                    dataType: "json",
                                    success: function (json) {
                                        j = json;
                                    }
                                });
                                if (j) {
                                    flag = false;
                                } else {
                                    nsDialog.jAlert("转让失败!");
                                }
                            } else {
                                licenceVal = "";
                                customerNameAjax = "";
                                customerId = "";
                                $(this).val("");
                            }
                        });
                    } else {
                        customerNameAjax = "";
                        licenceVal = "";
                        customerId = "";
                        $(this).val("");
                    }
                });

            } else {
                flag = false;
            }
        } else {
            customerNameAjax = "";
            licenceVal = "";
            customerId = "";
            flag = false;
        }
    }
    return flag;
}
