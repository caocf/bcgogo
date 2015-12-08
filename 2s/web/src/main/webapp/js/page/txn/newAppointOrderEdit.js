;
var timeOutId;
function setServicePrice($dom) {
    var idPrefix = $dom.attr("id").split(".")[0];
    var standardHours = $("#" + idPrefix + "\\.standardHours").val();
    var standardUnitPrice = $("#" + idPrefix + "\\.standardUnitPrice").val();
    var count = standardHours * standardUnitPrice;
    count = dataTransition.simpleRounding(count, 2);
    $("#" + idPrefix + "\\.total").val(count);
    setTotal();
}

function setItemTotal() {
    $(".itemPrice").each(function (i) {
        var $this = $(this);
        var price = $this.val();
        if (!price) {
            price = "0";
        }
        var idPrefix = $this.attr("id").split(".")[0];
        var $amount = $("#" + idPrefix + "\\.amount");
        if ($.trim(price)) {
            price = App.StringFilter.inputtingPriceFilter(price, 2);
            $this.val(price);
        }

        if ($amount) {
            $amount.val(App.StringFilter.inputtingPriceFilter($amount.val(), 2))
        }
        var amount = $amount.val();
        if ($.trim(price) && $.trim(amount)) {
            var count = parseFloat(price * amount);
            var count1 = dataTransition.simpleRounding(count,2);
            $("#"+idPrefix + "\\.total").val(count1);
            $("#"+idPrefix + "\\.total_span").text(count1);
        }
    });
}

function setTotal() {
    setItemTotal();
    var total = 0, serviceTotal = 0, materialTotal = 0;
    $("#serviceDetail_tb").find("[name$='.total']").each(function () {
        serviceTotal += $(this).val() * 1;
    });
    $("#serviceTotalSpan").text(serviceTotal);

    $("#material_tb").find("[name$='.total']").each(function () {
        materialTotal += $(this).val() * 1;
    });
    $("#salesTotalSpan").text(materialTotal);
    $("#totalSpan").text(serviceTotal + materialTotal);
    $("#total").val(serviceTotal + materialTotal);
}

function getServiceItemNextIndex() {
    var biggestCnt = 0;
    var exist = false;
    var $tb = $("#serviceDetail_tb");
    $tb.find("input[name$='service']").each(function () {
        exist = true;
        var id = $(this).attr("id");
        var no = id.substring(11, id.indexOf("."));
        if (biggestCnt < no) {
            biggestCnt = no;
        }
    });
    return exist == false ? 0 : ++biggestCnt;
}

function addServiceItem() {
    var $tb = $("#serviceDetail_tb");
    var $trItem = $tb.find("tr").eq(1).clone();
    var $trItemBg = $tb.find("tr").eq(2).clone();
    var nextIndex = getServiceItemNextIndex();
    $trItem.find("input").each(function () {
        var $thisInput = $(this);
        var suffix = $thisInput.attr("id").split(".")[1];
        $(this).attr("id", "serviceDTOs" + nextIndex + "." + suffix)
            .attr("name", "serviceDTOs[" + nextIndex + "]." + suffix)
            .val("");
    });
    $trItem.find("a").each(function () {
        var $thisInput = $(this);
        var suffix = $thisInput.attr("id").split(".")[1];
        $(this).attr("id", "serviceDTOs" + nextIndex + "." + suffix);
        $(this).attr("name", "serviceDTOs[" + nextIndex + "]." + suffix);
    });
    $tb.append($trItem).append($trItemBg);
}

function deleteServiceItem() {

}

function showServiceItemAddOrDeleteBtn() {
    var $tb = $("#serviceDetail_tb");
    $tb.find("[name$=.addService]").each(function () {
        $(this).hide();
    });
    $tb.find("[name$=.addService]:last").show();
}

function checkServiceItemSameOrEmpty($tr) {
    if (!$tr) {
        return false;
    }
    var serviceNameSet = new Set();
    var isSameOrEmpty = false;
    $(".service").each(function () {
        var setSize = serviceNameSet.size;
        var thisVal = $(this).val();
        if (G.Lang.isEmpty(thisVal)) {
            isSameOrEmpty = true;
            return false;
        }
        serviceNameSet.add(thisVal);
        if (setSize == serviceNameSet.size) {
            isSameOrEmpty = true;
            return false;
        }
    });
    return isSameOrEmpty;
}

function checkMaterialItemSameOrEmpty($tr) {
    if (!$tr) {
        return false;
    }
    var productNameSet = new Set();
    var isSameOrEmpty = false;
    $(".productName").each(function () {
        var setSize = productNameSet.size;
        var $this = $(this);
        var productName = $this.val();
        var splitVal = "_@_";
        var idPrefix = $this.attr("id").split(".")[0];
        if (G.Lang.isEmpty(productName)) {
            isSameOrEmpty = true;
            return false;
        }
        var checkVal = $("#" + idPrefix + "\\.commodityCode").val() + splitVal +
                productName + splitVal +
                $("#" + idPrefix + "\\.brand").val() + splitVal +
                $("#" + idPrefix + "\\.spec").val() + splitVal +
                $("#" + idPrefix + "\\.vehicleBrand").val() + splitVal +
                $("#" + idPrefix + "\\.vehicleModel").val() + splitVal
            ;
        productNameSet.add(checkVal);
        if (setSize == productNameSet.size) {
            isSameOrEmpty = true;
            return false;
        }
    });
    return isSameOrEmpty;
}

function getMaterialItemNextIndex() {
    var biggestCnt = 0;
    var exist = false;
    var $tb = $("#material_tb");
    $tb.find("input[name$='.productName']").each(function () {
        exist = true;
        var id = $(this).attr("id");
        var no = id.substring(8, id.indexOf("."));
        if (biggestCnt < no) {
            biggestCnt = no;
        }
    });
    return exist == false ? 0 : ++biggestCnt;
}

function addMaterialItem() {
    var $tb = $("#material_tb");
    var $trItem = $tb.find("tr").eq(1).clone();
    var $trItemBg = $tb.find("tr").eq(2).clone();
    var nextIndex = getMaterialItemNextIndex();
    $trItem.find("input").each(function () {
        var $thisInput = $(this);
        var suffix = $thisInput.attr("id").split(".")[1];
        $(this).attr("id", "itemDTOs" + nextIndex + "." + suffix)
            .attr("name", "itemDTOs[" + nextIndex + "]." + suffix)
            .val("");
    });
    $trItem.find("a").each(function () {
        var $thisInput = $(this);
        var suffix = $thisInput.attr("id").split(".")[1];
        $(this).attr("id", "itemDTOs" + nextIndex + "." + suffix);
        $(this).attr("name", "itemDTOs[" + nextIndex + "]." + suffix);
    });
    $trItem.find("span").each(function () {
        var $thisInput = $(this);
        var suffix = $thisInput.attr("id").split(".")[1];
        $(this).attr("id", "itemDTOs" + nextIndex + "." + suffix);
        $(this).attr("name", "itemDTOs[" + nextIndex + "]." + suffix);
        $(this).text("");
    });
    $tb.append($trItem).append($trItemBg);
    showMaterialItemAddOrDeleteBtn();
    return $trItem;
}

function deleteMaterialItem() {

}

function showMaterialItemAddOrDeleteBtn() {
    var $tb = $("#material_tb");
    $tb.find("[name$=.addMaterial]").each(function () {
        $(this).hide();
    });
    $tb.find("[name$=.addMaterial]:last").show();
}

$(function () {
    showServiceItemAddOrDeleteBtn();
    showMaterialItemAddOrDeleteBtn();
    $("#select_vehicleNo").bind("change", function () {
        var $this = $(this);
        var vehicleMobile = "";
        var vehicleBrand = "";
        var vehicleModel = "";
        var vehicleContact = "";
        var vehicleBrandAndModel = "";
        var vehicleId = "";
        var currentMileage = "";
        var vehicleNo = "";
        if (G.Lang.isNotEmpty($this.val())) {
            vehicleId = $this.val();
            var $thisOption = $this.find("option:selected");
            vehicleMobile = G.Lang.normalize($thisOption.attr("mobile"));
            vehicleBrand = G.Lang.normalize($thisOption.attr("brand"));
            vehicleModel = G.Lang.normalize($thisOption.attr("model"));
            vehicleContact = G.Lang.normalize($thisOption.attr("contact"));
            vehicleBrandAndModel = (G.Lang.isEmpty(vehicleBrand) ? "" : vehicleBrand + " ") + vehicleModel;
            currentMileage = G.Lang.normalize($thisOption.attr("currentMileage"));
            vehicleNo = G.Lang.normalize($thisOption.text());
        }
        $("#vehicleId").val(vehicleId);
        $("#vehicleMobile").val(vehicleMobile);
        $("#vehicleBrand").val(vehicleBrand);
        $("#vehicleModel").val(vehicleModel);
        $("#vehicleContact").val(vehicleContact);
        $("#sp_vehicleMobile").text(vehicleMobile);
        $("#vehicleBrandModel").text(vehicleBrandAndModel).attr("title", vehicleBrandAndModel);
        $("#currentMileage").val(currentMileage);
        $("#vehicleNo").val(vehicleNo);
    });

    $("input[name$='.service']").live('click focus keyup',
        function (event) {

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
                var valflag = hook.value != $(this).html();
                new clearItemUtil().clearByFlag(hook, valflag);
                //$(hook).val($(this).attr("title"));
                var serviceId = data.id;
                var idPrefix = hook.id.split(".")[0];

                $("#" + idPrefix + "\\.total")
                    .removeAttr("disabled")
                    .val(data.price)
                    .data("recommendedPrice", data.price);

                setTotal();
                var $last_tr = $("#serviceDetail_tb").find(".service:last");
                if (G.Lang.isNotEmpty($last_tr.val())) {
                    addServiceItem();
                    showServiceItemAddOrDeleteBtn();
                }
                var prefix = hook.id.split(".")[0];

                //            tableUtil.tableStyle('#table_task', '.i_tabelBorder,.table_title');

//                if (!G.Lang.isEmpty($(hook).val())) {
//                    setTimeout(function () {
//                        var searchcompleteMultiselect = App.Module.searchcompleteMultiselect;
//                        searchOrderSuggestion(searchcompleteMultiselect, hook, "");
//                    }, 100);
//                }
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

                        var _id = data.id,
                            _name = $.trim(data.label),
                            result = "";

                        if (_name.length <= 0) {
                            nsDialog.jAlert("服务项目不能为空");
                            return false;
                        }

                        App.Net.syncAjax({
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
                        App.Net.syncAjax({
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
                            App.Net.syncPost({
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

                                        //遍历item 把此id的name 和hiddenvalue都变为最新的name
                                        // $obj.each(function() {
                                        //     var categoryId = _hiddenValue;

                                        //     if(categoryId == _id) {
                                        //         $(this).val(_name);
                                        //         $(this).attr("hiddenValue", _name);
                                        //     }
                                        // });

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
                        var serviceId = data.id,
                            deleteFlag = true,
                            url = "category.do?method=checkServiceUsed";

                        App.Net.syncPost({
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
                            var _result = App.Net.syncGet({
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
        }).live("blur", function (e) {               //手动输入，或下拉后，补全营业分类
            //         G.debug("change");
            //        $("#div_brand").css({'display':'none'});

            var serviceID = this.id;
            timeOutId = setTimeout(function () {
                if (!document.getElementById(serviceID)) {        //被删除
                    return;
                }
                var serviceName = document.getElementById(serviceID).value;
                serviceName = serviceName.replace(/(^\s*)|(\s*$)/g, "");
                if (serviceName == null || serviceName == "") return;
                $("#" + serviceID.split(".")[0] + "\\.total").val(0);

                var ajaxUrl = "txn.do?method=getServiceByServiceName&serviceName=" + encodeURI(serviceName);

                App.Net.syncAjax({
                    url: ajaxUrl,
                    dataType: "json",
                    success: function (json) {
                        var prefix = serviceID.split(".")[0];
                        if (json && json.length > 0) {
                            $("#" + serviceID.split(".")[0] + "\\.serviceId").val(json[0].idStr);
//                            $("#" + serviceID.split(".")[0] + "\\.serviceHistoryId").val("");
                            $("#" + serviceID.split(".")[0] + "\\.total").val(json[0].price);

                            $("#" + prefix + "\\.standardHours").val(json[0].standardHours != null ? json[0].standardHours : "");
                            $("#" + prefix + "\\.standardUnitPrice").val(json[0].standardUnitPrice != null ? json[0].standardUnitPrice : "");
                            $("#" + prefix + "\\.actualHours").val(json[0].standardHours != null ? json[0].standardHours : "");

                            App.Net.syncPost({
                                url: "category.do?method=getCategoryByServiceId",
                                data: {
                                    "serviceId": json[0].idStr,
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
                        } else {
                            $("#" + serviceID.split(".")[0] + "\\.serviceId").val("");
                            $("#" + serviceID.split(".")[0] + "\\.serviceHistoryId").val("");
                            $("#" + prefix + "\\.businessCategoryId").val("");
                            $("#" + prefix + "\\.businessCategoryName").val("");
                            $("#" + prefix + "\\.businessCategoryName").removeAttr("hiddenValue");
                            $("#" + prefix + "\\.businessCategoryName").removeAttr("disabled");
                        }

                    }
                });

                setTotal();
            }, 200);
        });

    $("a[name$='.addService']").live("click", function () {
        var $tr = $(this).parents("tr");
        if (checkServiceItemSameOrEmpty($tr)) {
            nsDialog.jAlert("预约单服务项目内容重复或为空，请修改或删除");
        } else {
            addServiceItem();
            showServiceItemAddOrDeleteBtn();
        }
    });
    $("a[name$='.deleteService']").live("click", function () {
        var $tr = $(this).parents("tr");
        var $trNext = $tr.next("tr");
        if ($(".service").size() > 1) {
            $tr.remove();
            $trNext.remove();
            showServiceItemAddOrDeleteBtn();
        } else {
            $tr.find("input").each(function () {
                $(this).val("");
            })
        }
        setTotal();
    });
    $("a[name$='.addMaterial']").live("click", function () {
        var $tr = $(this).parents("tr");
        if (checkMaterialItemSameOrEmpty($tr)) {
            nsDialog.jAlert("预约单商品材料内容重复或为空，请修改或删除");
        } else {
            addMaterialItem();
        }
    });
    $("a[name$='.deleteMaterial']").live("click", function () {
        var $tr = $(this).parents("tr");
        var $trNext = $tr.next("tr");
        if ($(".productName").size() > 1) {
            $tr.remove();
            $trNext.remove();
            showMaterialItemAddOrDeleteBtn();
        } else {
            $tr.find("input").each(function () {
                $(this).val("");
            });
            $tr.find("span").each(function () {
                $(this).text("");
            });
        }
        setTotal();
    });

    $(".standardHours,.standardUnitPrice").live("keyup blur", function (event) {
        if (event.type == "focusout") {
            var filterVal = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
            event.target.value = dataTransition.simpleRounding(filterVal, 2);
            setServicePrice($(this));
        } else if (event.type == "keyup") {
            if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
                event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
            }
        }
    });
    $(".serviceTotal,.itemPrice,.itemAmount").live("keyup blur", function (event) {
        if (event.type == "focusout") {
            var filterVal = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
            event.target.value = dataTransition.simpleRounding(filterVal, 2);
            setTotal();
        } else if (event.type == "keyup") {
            if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2)) {
                event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 2);
            }
        }
    });

    $("#currentMileage").live("keyup blur", function (event) {
        if (event.type == "focusout") {
            var filterVal = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value,0);
            event.target.value = dataTransition.simpleRounding(filterVal,0);
        } else if (event.type == "keyup") {
            if (event.target.value != APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value, 0)) {
                event.target.value = APP_BCGOGO.StringFilter.inputtingPriceFilter(event.target.value,0);
            }
        }
    });

});

