/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-9-11
 * Time: 下午7:43
 * 依赖 suggestion.js
 */
var MyChart, CategoriesMap;
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$(document).ready(function () {
    //Bind these buttons.

    $("#resetSearchCondition").click(function () {
        //reset form

        $("#itemStatisticsForm").resetForm();
        $("#payableSearchForm").resetForm();
        $("#receivableSearchForm").resetForm();
        if(!$("#my_date_thismonth").hasClass("clicked")){
            $("#my_date_thismonth").click();
        }else{
            $("#my_date_thismonth").click();
            $("#my_date_thismonth").click();
        }


        //时间
        returnDefaultValue();
        $("#statByType").val("按商品分类统计");
        $("#businessCategory").parent().hide();
        $("#productAttr").hide();
        $('#serviceAndConstruction').parent().hide();
        $("#productCategory").parent().show();
    });

    $("#itemStatisticsForm").submit(function (e) {
        e.preventDefault();
        $(".J-initialCss").placeHolder("clear");
        $("#customerStatisticsTable tr:not(:first)").remove();
        $("#supplierStatisticsTable tr:not(:first)").remove();
        $("#recordNum").text("0");
        $("#orderTotal").text("0");
        $("#settledTotal").text("0");
        $("#debtTotal").text("0");
        $("#discountTotal").text("0");
        $("#costTotal").text("0");
        $("#grossProfitTotal").text("0");
        $("#grossProfitPercent").text("0");

        if ("---所有营业分类---" == $("#businessCategory").val()) {
            $("#businessCategory").val("");
        }
        if ("---所有商品分类---" == $("#productCategory").val()) {
            $("#productCategory").val("");
        }
        if('服务/施工内容' == $('#serviceAndConstruction').val()){
            $("#serviceAndConstruction").val('');
        }
        var param = $("#itemStatisticsForm").serializeArray();
        var paramJson = {};
        $.each(param, function (index, val) {
            paramJson[val.name] = val.value;
        });

        var url = "itemStat.do?method=getItemStatData";
        var statType = $("#statType").val();
        $("#productStatDiv").hide();
        $("#businessStatDiv").hide();
        $("#serviceStatDiv").hide();
        var statByType = $("#statByType").val();
        if (statByType == "按商品分类统计") {
            $("#statType").val("productStatistics");
            statType = "productStatistics";
            paramJson.statType = statType;
            $("#productStatDiv").show();
            $('#productName').val('');
            $('#productBrand').val('');
            $('#productSpec').val('');
            $('#productModel').val('');
            $('#productVehicleBrand').val('');
            $('#productVehicleModel').val('');
            $('#commodityCode').val('');
        } else if (statByType == "按商品统计") {
            $("#statType").val("productStatistics");
            statType = "productStatistics";
            paramJson.statType = statType;
            $("#productStatDiv").show();
            $('productCategory').val('');
        } else if (statByType == "按营业分类统计") {
            statType = "businessStatistics";
            paramJson.statType = statType;
            $("#statType").val("businessStatistics");
            $("#businessStatDiv").show();
        }else if(statByType == "服务/施工内容"){
            statType = "serviceAndConstructionStatistics";
            paramJson.statType = statType;
            $("#statType").val("serviceAndConstructionStatistics");
            $("#serviceStatDiv").show();
        }

        $("#itemStatisticsForm").ajaxSubmit({
            dataType: "json",
            success: function (data) {
                var functionName, dynamical;
                if (statType == "customerStatistics") {
                    functionName = "drawCustomerStatTable";
                    dynamical = "dynamicalCustomer";
                    drawCustomerStatTable(data);
                } else if (statType == "supplierStatistics") {
                    functionName = "drawSupplierStatTable";
                    dynamical = "dynamicalSupplier";
                    drawSupplierStatTable(data);
                } else if (statType == "productStatistics") {
                    functionName = "drawProductStatTable";
                    dynamical = "dynamicalProduct";
                    drawProductStatTable(data);
                } else if (statType == "businessStatistics") {
                    functionName = "drawBusinessStatTable";
                    dynamical = "dynamicalBusiness";
                    drawBusinessStatTable(data);
                }else if(statType == 'serviceAndConstructionStatistics'){
                    functionName = "drawServiceStatTable";
                    dynamical = "dynamicalService";
                    drawServiceStatTable(data);
                }
                 $(".J-initialCss").placeHolder("reset");
                returnDefaultValue();
                initPages(data, dynamical, url, '', functionName, '', '', paramJson, "");
            },
            error: function () {
                $(".J-initialCss").placeHolder("reset");
                clearItemStatTable();
                returnDefaultValue();
                alert("网络异常，请联系客服");
            }
        });
    });

    //客户 或者  供应商下拉框
    $("#customerName,#supplierName")
        .bind('click', function () {
            getCustomerOrSupplierSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getCustomerOrSupplierSuggestion(this, eventKeyCode);
            }

        });
    //客户车牌下拉框
    $("#vehicle").bind('click', function () {
        getVehicleLicenceNoSuggestion($(this));
    })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getVehicleLicenceNoSuggestion($(this));
            }
        });

    //产品分类下拉框
    $("#productCategory").bind('click', function () {
        $(this).val("");
        getProductKindSuggestion($(this));
    })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getProductKindSuggestion($(this));
            }
        });
    //产品分类下拉框
    $("#businessCategory").bind('click',function () {
        $(this).val("");
        getBusinessCategorySuggestion($(this));

    }).bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getBusinessCategorySuggestion(this, eventKeyCode);
            }
        });

    $("#serviceAndConstruction").bind('click',function () {
        $(this).val("");
        getServiceAndConstructionSuggestion($(this));
    }).bind('keyup', function (event) {
        var eventKeyCode = event.which || event.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            getServiceAndConstructionSuggestion(this, eventKeyCode);
        }
    });



//    //初始化柱形图
//    var chartType = "month";//跟页面样式同步
//    var statType = $("input[name='statType']:checked").val();
//    getCartData(chartType,statType);

    $("#print_div,#print_divD,#print_divS").bind("click", function () {
        if ("---所有营业分类---" == $("#businessCategory").val()) {
            $("#businessCategory").val("");
        }
        if ("---所有商品分类---" == $("#productCategory").val()) {
            $("#productCategory").val("");
        }
        if ('服务/施工内容' == $('#serviceAndConstruction').val()) {
            $("#serviceAndConstruction").val('');
        }
        $(".J-initialCss").placeHolder("clear");
        $("#itemStatisticsForm").ajaxSubmit({
            url: "itemStat.do?method=getItemStatDataToPrint",
//            dataType: "json",
            type: "POST",
            success: function (data) {
                 $(".J-initialCss").placeHolder("reset");
                if (!data) return;
                var printWin = window.open("", "", "width=1024,height=768");
                with (printWin.document) {
                    open("text/html", "replace");
                    write(data);
                    close();
                }
                returnDefaultValue();
            },
            error:function(){
                $(".J-initialCss").placeHolder("reset");
            }
        });
    });
     provinceBind();
    $("#provinceNo").bind("change", function() {
        $("#cityNo option").not(".default").remove();
        $("#regionNo option").not(".default").remove();
        cityBind();
        if ($(this).val() == "--所有省--") {
            $(this).css({"color": "#ADADAD"});
        }else{
            $(this).css({"color": "#000000"});
        }
    });
    $("#cityNo").bind("change", function() {
        $("#regionNo option").not(".default").remove();
        regionBind();

        if ($(this).val() == "--所有市--") {
            $(this).css({"color": "#ADADAD"});
        }else{
            $(this).css({"color": "#000000"});
        }
    });

    $("#regionNo").bind("change", function() {
        if ($(this).val() == "--所有区--") {
            $(this).css({"color": "#ADADAD"});
        }else{
            $(this).css({"color": "#000000"});
        }
    });
    $(".J-productSuggestion").bind('click', function () {
            productSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                productSuggestion($(this));
            }
        })
        .bind("change",function(){
            $("#productId").val("");
        });

    function productSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var currentSearchField =  $domObject.attr("searchField");
        var ajaxData = {
            searchWord: searchWord,
            searchField: currentSearchField,
            uuid: dropList.getUUID()
        };
        $domObject.parent().prevAll().each(function(){
            var $productSearchInput = $(this).find(".J-productSuggestion");
            if($productSearchInput && $productSearchInput.length>0){
                var val = $productSearchInput.val().replace(/[\ |\\]/g, "");
                if($productSearchInput.attr("name")!="searchWord"){
                    ajaxData[$productSearchInput.attr("name")] = val == $productSearchInput.attr("initialValue") ? "" : val;
                }
            }
        });

        var ajaxUrl = "product.do?method=getProductSuggestion";
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            if (currentSearchField == "product_info") {
                dropList.show({
                    "selector": $domObject,
                    "autoSet": false,
                    "data": result,
                    onGetInputtingData: function() {
                        var details = {};
                        $domObject.parent().nextAll().each(function () {
                            var $productSearchInput = $(this).find(".J-productSuggestion");
                            if ($productSearchInput && $productSearchInput.length > 0) {
                                var val = $productSearchInput.val().replace(/[\ |\\]/g, "");
                                details[$productSearchInput.attr("searchField")] = val == $productSearchInput.attr("initialValue") ? "" : val;
                            }
                        });
                        return {
                            details:details
                        };
                    },
                    onSelect: function (event, index, data, hook) {
                        $domObject.parent().nextAll().each(function () {
                            var $productSearchInput = $(this).find(".J-productSuggestion");
                            if ($productSearchInput && $productSearchInput.length > 0) {
                                var label = data.details[$productSearchInput.attr("searchField")];
                                if (G.Lang.isEmpty(label) && $productSearchInput.attr("initialValue")) {
                                    $productSearchInput.val($productSearchInput.attr("initialValue"));
                                    $productSearchInput.css({"color": "#ADADAD"});
                                } else {
                                    $productSearchInput.val(G.Lang.normalize(label));
                                    $productSearchInput.css({"color": "#000000"});
                                }
                            }
                        });
                        dropList.hide();
                    },
                    onKeyboardSelect: function (event, index, data, hook) {
                        $domObject.parent().nextAll().each(function () {
                            var $productSearchInput = $(this).find(".J-productSuggestion");
                            if ($productSearchInput && $productSearchInput.length > 0) {
                                var label = data.details[$productSearchInput.attr("searchField")];
                                if (G.Lang.isEmpty(label) && $productSearchInput.attr("initialValue")) {
                                    $productSearchInput.val($productSearchInput.attr("initialValue"));
                                    $productSearchInput.css({"color": "#ADADAD"});
                                } else {
                                    $productSearchInput.val(G.Lang.normalize(label));
                                    $productSearchInput.css({"color": "#000000"});
                                }
                            }
                        });
                    }
                });
            }else{
                dropList.show({
                    "selector": $domObject,
                    "data": result,
                    "onSelect": function (event, index, data) {
                        $domObject.val(data.label);
                        $domObject.css({"color": "#000000"});
                        $domObject.parent().nextAll().each(function () {
                            var $productSearchInput = $(this).find(".J-productSuggestion");
                            if ($productSearchInput) {
                                clearSearchInputValueAndChangeCss($productSearchInput[0]);
                            }
                        });
                        dropList.hide();
                    }
                });
            }
        });
    }
      $(".J-initialCss").placeHolder();
     $("#productAttr").hide();

    $("#export").click(function(){
        $(this).attr("disabled",true);
        $("#exporting").css("display","");
        if($("#recordNum").text() * 1 == 0) {
            nsDialog.jAlert("对不起，暂无数据，无法导出！");
            $(this).removeAttr("disabled");
            $("#exporting").css("display","none");
            return;
        }
        clearInitValue();
        var param = $("#itemStatisticsForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            if(!G.Lang.isEmpty(data[val.name])){
                data[val.name] = data[val.name]+","+val.value;
            }else{
                data[val.name] = val.value;
            }
        });
        data.vehicleConstructionPermission = App.Permission.Version.MemberStoredValue;
        data.memberStoredValuePermission = App.Permission.Version.MemberStoredValue;
        data.totalExportNum = $("#recordNum").text();
        data.statisticsInfo = $("#statisticsInfo").text();
        var url = "export.do?method=exportCustomerTransaction";
        bcgogoAjaxQuery.setUrlData(url, data);
        bcgogoAjaxQuery.ajaxQuery(function (json) {
            $("#export").removeAttr("disabled");
            $("#exporting").css("display","none");
            if(json && json.exportFileDTOList) {
                if(json.exportFileDTOList.length > 1) {
                    showDownLoadUI(json);

                } else {
                    window.open("download.do?method=downloadExportFile&exportFileName=客户交易统计.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                }
            }

        });
    });
});



function getVehicleLicenceNoSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    if (GLOBAL.Lang.isEmpty(searchWord) && GLOBAL.Lang.isEmpty($("#customerId").val())) return;
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxUrl = "searchInventoryIndex.do?method=getVehicleLicenceNoSuggestion";
    var ajaxData = {
        searchWord: searchWord,
        uuid: droplist.getUUID(),
        customerId: $("#customerId").val()
    };
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        droplist.show({
            "selector": $domObject,
            "data": result,
            "onSelect": function (event, index, data) {
                $domObject.val(data.label);
                $domObject.css({"color": "#000000"});
                droplist.hide();
            }
        });
    });
}

function getCustomerOrSupplierSuggestion(domObject, keycode) {
    var $domObject = $(domObject);
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    searchWord = $.trim(searchWord).toUpperCase();
    if (searchWord == "客户名" || searchWord == "供应商名") {
        searchWord = "";
    }
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        searchWord: searchWord.toUpperCase(),
        uuid: droplist.getUUID()
    };
    if ($domObject.attr("id") == "supplierName") {
        ajaxData["customerOrSupplier"] = "supplier";
        ajaxData["titles"] = "name,mobile";
    } else {
        ajaxData["customerOrSupplier"] = "customer";
        ajaxData["titles"] = "name,mobile";
    }
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
    App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        if (!G.isEmpty(result.data[0])) {
            G.completer({
                    'domObject': domObject,
                    'keycode': keycode,
                    'title': result.data[0].details.name}
            );
        }
        droplist.show({
            "selector": $domObject,
            "data": result,
            "onSelect": function (event, index, data) {
                $domObject.val(data.details.name);
                if ($domObject.attr("id") == "customerName") {
                    $("#customerId").val(data.details.id);
                    $("#mobile").val(data.details.mobile);
                    $("#mobile").css("color", "#000");

                }
                if ($domObject.attr("id") == "supplierName") {
                    $("#supplierId").val(data.details.id);
                    $("#mobile").val(data.details.mobile);
                    $("#mobile").css("color", "#000");
                }
                $domObject.css({"color": "#000000"});
                droplist.hide();
            },
            "onKeyboardSelect": function (event, index, data, hook) {
                $domObject.val(data.details.name);
                if ($domObject.attr("id") == "customerName") {
                    $("#customerId").val(data.details.id);
                    $("#mobile").val(data.details.mobile);
                    $("#mobile").css("color", "#000");

                }
                if ($domObject.attr("id") == "supplierName") {
                    $("#supplierId").val(data.details.id);
                }
            }
        });
    });
}
function getProductKindSuggestion(selector, keycode) {
    var $node = $(selector);

    if (!$node[0]) {
        return;
    }

    var searchWord = $node.val().replace(/[\ |\\]/g, "");
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        keyword: searchWord,
        uuid: droplist.getUUID()
    };
    var ajaxUrl;
    if (GLOBAL.Lang.isEmpty(searchWord)) {
        ajaxUrl = "stockSearch.do?method=getProductKindsRecentlyUsed";
    } else {
        ajaxUrl = "stockSearch.do?method=getProductKindsWithFuzzyQuery";
    }
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        droplist.show({
            "selector": $node,
            "data": result,
            "onSelect": function (event, index, data) {
                $node.val(data.label);
                $node.css({"color": "#000000"});
                droplist.hide();
            }
        });
    });
}

function getBusinessCategorySuggestion(selector, keycode) {
    var $node = $(selector);
    if (!$node[0]) {
        return;
    }

    var searchWord = $node.val().replace(/[\ |\\]/g, "");
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        keyWord: searchWord,
        uuid: droplist.getUUID()
    };
    var ajaxUrl = "category.do?method=getCategory";
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        droplist.show({
            "selector": $node,
            "data": result,
            "onSelect": function (event, index, data) {
                $node.val(data.label);
                $node.css({"color": "#000000"});
                droplist.hide();
            }
        });
    });
}

function getServiceAndConstructionSuggestion(selector, keycode){
    var $node = $(selector);
    if (!$node[0]) {
        return;
    }
    var searchWord = $node.val().replace(/[\ |\\]/g, "");
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        keyWord: searchWord,
        uuid: droplist.getUUID()
    };
    var ajaxUrl = "txn.do?method=searchService&requestType=AJAX&name=" + searchWord;
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        $.each(result.data,function(i, item){
            item.label = item.details.name;
        });
        droplist.show({
            "selector": $node,
            "data": result,
            "onSelect": function (event, index, data) {
                $node.val(data.label);
                $node.css({"color": "#000000"});
                droplist.hide();
            }
        });
    });
}

function resetFormInputInitValue() {
    $("#div_supplier,#div_customer,#div_product,#div_business").find("input[type='text']").each(function () {
        $(this).val('');
    });
    $("#includeDisabledCustomer").removeAttr("checked");
    $("#customerId").val('');
    $("#mobile").val('');
    $("#supplierId").val('');
}
function changeStatType($domObject) {
    resetFormInputInitValue();
    clearItemStatTable();
    $("#div_supplier,#div_customer,#div_product,#div_business").hide();
    $("#customerStatisticsInfo,#supplierStatisticsInfo,#productStatisticsInfo,#businessStatisticsInfo").hide();
    $("#div_supplier,#div_customer,#div_product,#div_business").find("input").attr("disabled", true);
    if ($domObject.val() == "productStatistics") {
        $("#productStatisticsInfo").show();
        $("#div_product,#div_customer").show();
        $("#div_product,#div_customer").find("input").removeAttrs("disabled");
    } else if ($domObject.val() == "businessStatistics") {
        $("#businessStatisticsInfo").show();
        $("#div_business,#div_customer").show();
        $("#div_business,#div_customer").find("input").removeAttrs("disabled");
    } else if ($domObject.val() == "customerStatistics") {
        $("#customerStatisticsInfo").show();
        $("#div_customer").show();
        $("#div_customer").find("input").removeAttrs("disabled");
    } else if ($domObject.val() == "supplierStatistics") {
        $("#supplierStatisticsInfo").show();
        $("#div_supplier").show();
        $("#div_supplier").find("input").removeAttrs("disabled");
    }
    $("#table_title").html($domObject.attr("title"));
}
function clearItemStatTable() {
    $("#customerStatisticsTable tr:not(:first)").remove();
    $("#supplierStatisticsTable tr:not(:first)").remove();
    $("#productStatisticsTable tr:not(:first)").remove();
    $("#businessStatisticsTable tr:not(:first)").remove();
    $("#serviceStatisticsTable tr:not(:first)").remove();
    $("#customerStatisticsTable tr:first").after('<tr><td colspan="' + $("#customerStatisticsTable tr:first td").size() + '">暂无交易记录</td></tr>');
    $("#supplierStatisticsTable tr:first").after('<tr><td colspan="' + $("#supplierStatisticsTable tr:first td").size() + '">暂无交易记录</td></tr>');
    $("#productStatisticsTable tr:first").after('<tr><td colspan="' + $("#productStatisticsTable tr:first td").size() + '">暂无交易记录</td></tr>');
    $("#businessStatisticsTable tr:first").after('<tr><td colspan="' + $("#businessStatisticsTable tr:first td").size() + '">暂无交易记录</td></tr>');
    $("#serviceStatisticsTable tr:first").after('<tr><td colspan="' + $("#serviceStatisticsTable tr:first td").size() + '">暂无交易记录</td></tr>');
    $("#print").hide();
    $("#print1").hide();
    $("#print2").hide();
    $("#print3").hide();
}
/**
 * 封装table
 */
function drawCustomerStatTable(data) {
    if (data == null || data[0] == null || data[0].orders == null || data[0].orders == 0) {
        clearItemStatTable();
        return;
    }
    $("#customerStatisticsTable tr:not(:first)").remove();
    $.each(data[0].orders, function (index, order) {
        var orderId = G.normalize(order.orderIdStr);
        var receiptNo = G.normalize(order.receiptNo, "--");
        var orderType = G.normalize(order.orderType, "--");
        var createdTimeStr = G.normalize(order.createdTimeStr, "--");
        var customerName = G.normalize(order.customerOrSupplierName, "--");
        var customerOrSupplierId = G.normalize(order.customerOrSupplierIdStr);
        var customerStatus = G.normalize(order.customerStatus);
        var contactNum = G.normalize(order.contactNum, "--");
        var memberNo = G.normalize(order.memberNo, "--");
        var vehicle = G.normalize(order.vehicle, "--");
        var orderTypeValue = G.normalize(order.orderTypeValue, "--");
        var amount = G.normalize(order.amount, "--");
        var totalCostPrice = G.normalize(order.totalCostPrice, "--");
        var settled = G.normalize(order.settled, "--");
        var discount = dataTransition.rounding(parseFloat(order.amount) - parseFloat(order.settled) - parseFloat(order.debt), 2);
        var debt = G.normalize(order.debt, "--");//应收款
        var grossProfit = G.normalize(order.grossProfit, "--");//毛利
        var grossProfitRate = (G.isEmpty(order.grossProfitRate) ? "--" : (order.grossProfitRate) + "%");//毛利率
        var afterMemberDiscountTotal = (G.isEmpty(order.afterMemberDiscountTotal) ? amount : (dataTransition.rounding(order.afterMemberDiscountTotal, 2)));

        var tr = '<tr class="table-row-original">';
        tr += '<td style="padding-left:10px;">' + (index + 1) + '</td>';
        tr += '<td title="' + createdTimeStr + '">' + createdTimeStr + '</td>';
        if ("DISABLED" == customerStatus) {
            tr += '<td title="' + customerName + '"><span class="customer limit-span" style="color:#999999">' + customerName + '</span></td>';
        } else {
            tr += "<td title='" + customerName + "'>" + '<a href ="#" class="blue_color" onclick="openCustomer(\'' + customerOrSupplierId + '\',\'' + "customer" + '\')">' + customerName + "</a> " + "</td> ";
        }
        if (App.Permission.Version.MemberStoredValue) {
            tr += '<td title="' + memberNo + '">' + memberNo + '</td>';
        }
        tr += '<td title="' + contactNum + '">' + contactNum + '</td>';
        if (App.Permission.Version.VehicleConstruction) {
            tr += '<td title="' + vehicle + '">' + vehicle + '</td>';
        }
        tr += '<td title="' + orderTypeValue + '">' + orderTypeValue + '</td>';
        tr += '<td title="' + receiptNo + '">';
        if ("SALE" == orderType) {
            tr += '<a target="_blank" style="cursor:pointer;" href="sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=' + orderId + '">' + receiptNo + '</a>';
        } else if ("WASH_BEAUTY" == orderType) {
            tr += '<a target="_blank" style="cursor:pointer;" href="washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=' + orderId + '">' + receiptNo + '</a>';
        } else if ("REPAIR" == orderType) {
            tr += '<a target="_blank" style="cursor:pointer;" href="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderId + '">' + receiptNo + '</a>';
        } else {
            tr += receiptNo;
        }
        tr += '</td>';
        tr += '<td title="' + amount + '">' + amount + '</td>';

        tr += '<td title="' + totalCostPrice + '">' + totalCostPrice + '</td>';
        tr += '<td title="' + settled + '">' + settled + '</td>';
        tr += '<td title="' + discount + '">' + discount + '</td>';
        tr += '<td title="' + debt + '">' + debt + '</td>';
        if (!APP_BCGOGO.Permission.Version.FourSShopVersion) {
          tr += '<td title="' + grossProfit + '">' + grossProfit + '</td>';
          tr += '<td title="' + grossProfitRate + '" class="last-padding">' + grossProfitRate + '</td>';
        }
        tr += '</tr>';
        $("#customerStatisticsTable").append($(tr));
        tableUtil.limitSpanWidth($(".customer", "#customerStatisticsTable"), 10);
    });
    var lastTr = "";
    if (App.Permission.Version.MemberStoredValue && APP_BCGOGO.Permission.Version.VehicleConstruction) {
        lastTr = "<tr class='table-row-original'> <td colspan='8' style='text-align:center;'>本页小计：" + "</td>";
    } else if (App.Permission.Version.MemberStoredValue && !APP_BCGOGO.Permission.Version.VehicleConstruction) {
        lastTr = "<tr class='table-row-original'> <td colspan='7' style='text-align:center;'>本页小计：" + "</td>";
    } else if (!App.Permission.Version.MemberStoredValue && APP_BCGOGO.Permission.Version.VehicleConstruction) {
        lastTr = "<tr class='table-row-original'> <td colspan='7' style='text-align:center;'>本页小计：" + "</td>";
    } else {
        lastTr = "<tr class='table-row-original'> <td colspan='6' style='text-align:center;'>本页小计：" + "</td>";

    }
    lastTr += "<td>" + data[0].currentPageTotalAmounts.order_total_amount + "</td>";
    lastTr += "<td>" + data[0].currentPageTotalAmounts.total_cost_price + "</td>";
    lastTr += "<td>" + data[0].currentPageTotalAmounts.order_settled_amount + "</td>";
    lastTr += "<td>" + dataTransition.rounding(data[0].currentPageTotalAmounts.order_total_amount - data[0].currentPageTotalAmounts.order_settled_amount  - data[0].currentPageTotalAmounts.order_debt_amount, 2) + "</td>";
    lastTr += "<td>" + data[0].currentPageTotalAmounts.order_debt_amount + "</td>";
    if (!APP_BCGOGO.Permission.Version.FourSShopVersion) {
      lastTr += "<td>" + data[0].currentPageTotalAmounts.gross_profit + "</td>";
      var totalGrossProfit = dataTransition.rounding(data[0].currentPageTotalAmounts.gross_profit * 100 / (data[0].currentPageTotalAmounts.order_settled_amount + data[0].currentPageTotalAmounts.order_debt_amount), 1);
      totalGrossProfit += "%";
      lastTr += "<td>" + totalGrossProfit + "</td>";
    }
    lastTr += "</tr>";
    $("#customerStatisticsTable").append($(lastTr));

    tableStyleAdjuct("#customerStatisticsTable");

    $("#recordNum").text(data[0].numFound);
    $("#orderTotal").text(dataTransition.rounding(data[0].totalAmounts.ORDER_SETTLED_AMOUNT + data[0].totalAmounts.ORDER_DEBT_AMOUNT, 2));
    $("#settledTotal").text(data[0].totalAmounts.ORDER_SETTLED_AMOUNT);
    $("#debtTotal").text(data[0].totalAmounts.ORDER_DEBT_AMOUNT);
    $("#discountTotal").text(dataTransition.rounding(data[0].totalAmounts.ORDER_TOTAL_AMOUNT - data[0].totalAmounts.ORDER_SETTLED_AMOUNT - data[0].totalAmounts.ORDER_DEBT_AMOUNT, 2));
    $("#currentPage").val(data[data.length - 1].currentPage);

    $("#costTotal").text(data[0].totalAmounts.TOTAL_COST_PRICE);
    if (!APP_BCGOGO.Permission.Version.FourSShopVersion) {

      $("#grossProfitTotal").text(data[0].totalAmounts.GROSS_PROFIT);
      $("#grossProfitPercent").text(dataTransition.rounding(data[0].totalAmounts.GROSS_PROFIT * 100 / (data[0].totalAmounts.ORDER_SETTLED_AMOUNT + data[0].totalAmounts.ORDER_DEBT_AMOUNT), 1) + "%");
    }
    if (data[data.length - 1].totalRows > 0) {
        $("#print").show();
    }
    else {
        $("#print").hide();
    }
}

function drawSupplierStatTable(data) {
    if (data == null || data[0] == null || data[0].orders == null || data[0].orders == 0) {
        clearItemStatTable();
        return;
    }
    $("#supplierStatisticsTable tr:not(:first)").remove();
    $.each(data[0].orders, function (index, order) {
        var orderId = G.normalize(order.orderIdStr);
        var receiptNo = G.normalize(order.receiptNo, "--");
        var orderType = G.normalize(order.orderType, "--");
        var orderTypeValue = G.normalize(order.orderTypeValue, "--");
        var createdTimeStr = G.normalize(order.createdTimeStr, "--");
        var supplierName = G.normalize(order.customerOrSupplierName, "--");
        var contactNum = G.normalize(order.contactNum, "--");
        var contact = G.normalize(order.contact, "--");
        var amount = G.normalize(order.amount, "--");
        var discount = G.normalize(order.discount, "--");
        var settled = G.normalize(order.settled, "--");
        var debt = G.normalize(order.debt, "--");//挂账
        var customerOrSupplierId = G.normalize(order.customerOrSupplierIdStr);
        var tr = '<tr class="table-row-original">';
        tr += '<td class="first-padding">' + (index + 1) + '</td>';
        tr += '<td title="' + createdTimeStr + '">' + createdTimeStr + '</td>';

        tr += "<td title='" + supplierName + "'>" + '<a href ="#" class="blue_color" onclick="openCustomer(\'' + customerOrSupplierId + '\',\'' + "supplier" + '\')">' + supplierName + "</a> " + "</td> ";

        tr += '<td title="' + contact + '">' + contact + '</td>';
        tr += '<td title="' + contactNum + '">' + contactNum + '</td>';
        tr += '<td title="' + receiptNo + '">';
        if ("INVENTORY" == orderType) {
            tr += '<a target="_blank" style="cursor: pointer;color: #6699CC;"  href="storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=' + orderId + '">' + receiptNo + '</a>';
        } else if ("RETURN" == orderType) {
            tr += '<a target="_blank" style="cursor: pointer;color: #6699CC;" href="goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=' + orderId + '">' + receiptNo + '</a>';
        } else {
            tr += receiptNo;
        }
        tr += '</td>';
        tr += '<td title="' + orderTypeValue + '">' + orderTypeValue + '</td>';
        tr += '<td title="' + amount + '">' + amount + '</td>';
        tr += '<td title="' + settled + '">' + settled + '</td>';
        tr += '<td title="' + debt + '">' + debt + '</td>';
        tr += '<td title="' + discount + '" class="last-padding">' + discount + '</td>';
        tr += '</tr>';
        $("#supplierStatisticsTable").append($(tr));
        tableUtil.limitSpanWidth($(".customer", "#supplierStatisticsTable"), 10);
    });


    var lastTr = "<tr class='table-row-original'> <td colspan='7' style='text-align:center;'>本页小计：" + "</td>";
    lastTr += "<td>" + data[0].currentPageTotalAmounts.order_total_amount + "</td>";
    lastTr += "<td>" + data[0].currentPageTotalAmounts.order_settled_amount + "</td>";
    lastTr += "<td>" + data[0].currentPageTotalAmounts.order_debt_amount + "</td>";
    lastTr += "<td>" + data[0].currentPageTotalAmounts.discount + "</td>";
    lastTr += "</tr>";
    $("#supplierStatisticsTable").append($(lastTr));

    $("#recordNum").text(data[0].numFound);
    $("#orderTotal").text(dataTransition.rounding(data[0].totalAmounts.ORDER_SETTLED_AMOUNT + data[0].totalAmounts.ORDER_DEBT_AMOUNT, 2));
    $("#settledTotal").text(data[0].totalAmounts.ORDER_SETTLED_AMOUNT);
    $("#debtTotal").text(data[0].totalAmounts.ORDER_DEBT_AMOUNT);
    $("#discountTotal").text(data[0].totalAmounts.DISCOUNT);
    $("#currentPage").val(data[data.length - 1].currentPage);

    tableStyleAdjuct("#supplierStatisticsTable");

    $("#currentPage").val(data[data.length - 1].currentPage);
    if (data[data.length - 1].totalRows > 0) {
        $("#print1").show();
    }
    else {
        $("#print1").hide();
    }
}

function drawProductStatTable(data) {
    if (data == null || data[0] == null || data[0].orderItems == null || data[0].orderItems == 0) {
        clearItemStatTable();
        return;
    }
    $("#productStatisticsTable tr:not(:first)").remove();
    $.each(data[0].orderItems, function (index, orderItem) {
        var orderId = G.normalize(orderItem.orderIdStr);
        var orderReceiptNo = G.normalize(orderItem.orderReceiptNo, "--");
        var orderType = G.normalize(orderItem.orderType, "--");
        var orderTypeValue = G.normalize(orderItem.orderTypeValue, "--");
        var createdTimeStr = G.normalize(orderItem.createdTimeStr, "--");
        var customerName = G.normalize(orderItem.customerOrSupplierName, "--");
        var customerStatus = G.normalize(orderItem.customerStatus);
        var vehicle = G.normalize(orderItem.vehicle, "--");
        var itemPrice = G.normalize(orderItem.itemPrice, "--");
        var itemTotalCostPrice = G.normalize(orderItem.itemTotalCostPrice, "--");
        var itemCount = G.normalize(orderItem.itemCount, "--");
        var itemTotal = G.normalize(orderItem.itemTotal, "--");
        var productInfo = G.normalize(orderItem.productInfo, "--");
        var productInfoTitle = productInfo;
        if (productInfo.length >= 14) {
            productInfo = productInfo.substring(0, 14) + "...";
        }
        var customerOrSupplierId = G.normalize(orderItem.customerOrSupplierIdStr);

        var tr = '<tr class="table-row-original">';
        tr += '<td class="first-padding">' + (index + 1) + '</td>';
        tr += '<td title="' + createdTimeStr + '">' + createdTimeStr + '</td>';
        tr += '<td title="' + orderReceiptNo + '">';
        if ("SALE" == orderType) {
            tr += '<a target="_blank" style="cursor: pointer;color: #6699CC;" href="sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=' + orderId + '">' + orderReceiptNo + '</a>';
        } else if ("REPAIR" == orderType) {
            tr += '<a target="_blank" style="cursor: pointer;color: #6699CC;" href="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderId + '">' + orderReceiptNo + '</a>';
        } else {
            tr += orderReceiptNo;
        }
        tr += '</td>';
        tr += '<td title="' + orderTypeValue + '">' + orderTypeValue + '</td>';
        if ("DISABLED" == customerStatus) {
            tr += '<td style="text-align: center;color:#999999" title="' + customerName + '"><span class="customer limit-span">' + customerName + '</span></td>';
        } else {
            tr += "<td title='" + customerName + "'>" + '<a href ="#" class="blue_color" onclick="openCustomer(\'' + customerOrSupplierId + '\',\'' + "customer" + '\')">' + customerName + "</a> " + "</td> ";
        }
        if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
            tr += '<td title="' + vehicle + '">' + vehicle + '</td>';
        }
        tr += '<td  title="' + productInfoTitle + '">' + productInfo + '</td>';
        tr += '<td title="' + itemPrice + '">' + itemPrice + '</td>';
        tr += '<td title="' + itemTotalCostPrice + '">' + itemTotalCostPrice + '</td>';
        tr += '<td title="' + itemCount + '">' + itemCount + '</td>';
        tr += '<td title="' + itemTotal + '" class="last-padding">' + itemTotal + '</td>';
        tr += '</tr>';
        $("#productStatisticsTable").append($(tr));
        tableUtil.limitSpanWidth($(".customer", "#productStatisticsTable"), 10);
    });
    var lastTr = "";
    if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
        lastTr = "<tr class='table-row-original'> <td colspan='7' style='text-align:center;'>本页小计：" + "</td>";
    } else {
        lastTr = "<tr class='table-row-original'> <td colspan='6' style='text-align:center;'>本页小计：" + "</td>";
    }

    lastTr += "<td>" + data[0].currentPageTotalAmounts.item_price + "</td>";
    lastTr += "<td>" + data[0].currentPageTotalAmounts.item_total_cost_price + "</td>";
    lastTr += "<td>" + data[0].currentPageTotalAmounts.item_count + "</td>";
    lastTr += "<td>" + data[0].currentPageTotalAmounts.item_total + "</td>";
    lastTr += "</tr>";
    $("#productStatisticsTable").append($(lastTr));

    $("#recordNum").text(data[0].itemNumFound);
    $("#orderTotal").text(data[0].totalAmounts.ITEM_TOTAL);
    $("#settledTotal").text(data[0].totalAmounts.ITEM_COUNT);
    $("#debtTotal").text(data[0].totalAmounts.ITEM_TOTAL_COST_PRICE);


    tableStyleAdjuct("#productStatisticsTable");

    $("#currentPage").val(data[data.length - 1].currentPage);
    if (data[data.length - 1].totalRows > 0) {
        $("#print2").show();
    }
    else {
        $("#print2").hide();
    }
}

function drawServiceStatTable(data) {
    if (data == null || data[0] == null || data[0].orderItems == null || data[0].orderItems == 0) {
        clearItemStatTable();
        return;
    }
    $("#serviceStatisticsTable tr:not(:first)").remove();
    var serviceTotal = 0;
    var productTotal = 0;
    var itemCostPriceTotal = 0;
    var itemCountTotal = 0;
    var itemTotalStr = 0;

    $.each(data[0].orderItems, function (index, orderItem) {
        var orderId = G.normalize(orderItem.orderIdStr);
        var orderReceiptNo = G.normalize(orderItem.orderReceiptNo, "--");
        var orderType = G.normalize(orderItem.orderType, "--");
        var orderTypeValue = G.normalize(orderItem.orderTypeValue, "--");
        var createdTimeStr = G.normalize(orderItem.createdTimeStr, "--");
        var customerName = G.normalize(orderItem.customerOrSupplierName, "--");
        var customerStatus = G.normalize(orderItem.customerStatus);
        var vehicle = G.normalize(orderItem.vehicle, "--");
        var itemPrice = G.normalize(orderItem.itemPrice, "--");
        var itemTotalCostPriceStr = G.normalize(orderItem.itemTotalCostPrice, "--");
        var itemTotalCostPrice = (orderItem.itemTotalCostPrice == null ? 0 : (orderItem.itemTotalCostPrice));
        var itemCount = G.normalize(orderItem.itemCount, "--");
        var itemTotal = G.normalize(orderItem.itemTotal, "--");
        var serivce = G.normalize(orderItem.service, "--");
        var serivceTitle = serivce;
        if (serivce.length >= 10) {
            serivce = serivce.substring(0, 10) + "...";
        }
        var customerOrSupplierId = G.normalize(orderItem.customerOrSupplierIdStr);

        serviceTotal += itemTotal;
        productTotal += itemPrice;
        itemCostPriceTotal += itemTotalCostPrice;
        itemCountTotal += itemCount;
        itemTotalStr += itemTotal;


        var productInfo = G.normalize(orderItem.productInfo, "--");
        var productInfoTitle = productInfo;
        if (productInfo.length >= 9) {
            productInfo = productInfo.substring(0, 9) + "...";
        }

        var tr = '<tr class="table-row-original">';
        tr += '<td class="first-padding">' + (index + 1) + '</td>';
        tr += '<td title="' + createdTimeStr + '">' + createdTimeStr + '</td>';
        tr += '<td title="' + orderReceiptNo + '">';
        if ("SALE" == orderType) {
            tr += '<a target="_blank" style="cursor: pointer;color: #6699CC;" href="sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=' + orderId + '">' + orderReceiptNo + '</a>';
        } else if ("WASH_BEAUTY" == orderType) {
            tr += '<a target="_blank" style="cursor: pointer;color: #6699CC;" href="washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=' + orderId + '">' + orderReceiptNo + '</a>';
        } else if ("REPAIR" == orderType) {
            tr += '<a target="_blank" style="cursor: pointer;color: #6699CC;" href="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderId + '">' + orderReceiptNo + '</a>';
        } else {
            tr += orderReceiptNo;
        }
        tr += '</td>';
        tr += '<td title="' + orderTypeValue + '">' + orderTypeValue + '</td>';
        if ("DISABLED" == customerStatus) {
            tr += '<td style="text-align: center;color:#999999" title="' + customerName + '"><span class="customer limit-span">' + customerName + '</span></td>';
        } else {
            tr += "<td title='" + customerName + "'>" + '<a href ="#" class="blue_color" onclick="openCustomer(\'' + customerOrSupplierId + '\',\'' + "customer" + '\')">' + customerName + "</a> " + "</td> ";
        }
        if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
            tr += '<td title="' + vehicle + '">' + vehicle + '</td>';
            tr += '<td title="' + serivceTitle + '">' + serivce + '</td>';
            tr += '<td>' + itemPrice + '</td>';
            tr += '<td>' + itemCount + '</td>';
            tr += '<td title="' + itemTotal + '" class="last-padding">' + itemTotal + '</td>';
        }
        tr += '</tr>';
        $("#serviceStatisticsTable").append($(tr));
        tableUtil.limitSpanWidth($(".customer", "#serviceStatisticsTable"), 10);
    });

    var lastTr = "";
    lastTr = "<tr class='table-row-original'> <td colspan='8' style='text-align:center;'>本页小计：" + "</td>";
    lastTr += "<td>" + dataTransition.rounding(itemCountTotal, 2) + "</td>";
    lastTr += "<td>" + dataTransition.rounding(itemTotalStr, 2) + "</td>";

    lastTr += "</tr>";
    $("#serviceStatisticsTable").append($(lastTr));

    tableStyleAdjuct("#serviceStatisticsTable");

    $("#recordNum").text(data[0].itemNumFound);
    $("#orderTotal").text(data[0].totalAmounts.ITEM_TOTAL);
    $("#currentPage").val(data[data.length - 1].currentPage);
}

function drawBusinessStatTable(data) {
    if (data == null || data[0] == null || data[0].orderItems == null || data[0].orderItems == 0) {
        clearItemStatTable();
        return;
    }
    $("#businessStatisticsTable tr:not(:first)").remove();

    var serviceTotal = 0;
    var productTotal = 0;
    var itemCostPriceTotal = 0;
    var itemCountTotal = 0;
    var itemTotalStr = 0;

    $.each(data[0].orderItems, function (index, orderItem) {
        var orderId = G.normalize(orderItem.orderIdStr);
        var orderReceiptNo = G.normalize(orderItem.orderReceiptNo, "--");
        var orderType = G.normalize(orderItem.orderType, "--");
        var orderTypeValue = G.normalize(orderItem.orderTypeValue, "--");
        var createdTimeStr = G.normalize(orderItem.createdTimeStr, "--");
        var customerName = G.normalize(orderItem.customerOrSupplierName, "--");
        var customerStatus = G.normalize(orderItem.customerStatus);
        var vehicle = G.normalize(orderItem.vehicle, "--");
        var itemPrice = G.normalize(orderItem.itemPrice, "--");
        var itemTotalCostPriceStr = G.normalize(orderItem.itemTotalCostPrice, "--");
        var itemTotalCostPrice = (orderItem.itemTotalCostPrice == null ? 0 : (orderItem.itemTotalCostPrice));
        var itemCount = G.normalize(orderItem.itemCount, "--");
        var itemTotal = G.normalize(orderItem.itemTotal, "--");
        var serivce = G.normalize(orderItem.service, "--");
        var serivceTitle = serivce;
        if (serivce.length >= 10) {
            serivce = serivce.substring(0, 10) + "...";
        }
        var customerOrSupplierId = G.normalize(orderItem.customerOrSupplierIdStr);
        var servicePriceStr = G.normalize(orderItem.itemPrice, "--");
        var servicePrice = (orderItem.itemPrice == null ? 0 : (orderItem.itemPrice));
        if (orderItem.itemType == "SERVICE" && orderItem.consumeType == "TIMES") {
            servicePriceStr = "计次划卡";
            itemTotalCostPriceStr = "--";
            itemPrice = 0;
            servicePrice = 0;
            itemTotal = 0;
        } else if (orderItem.itemType == "SERVICE") {
            itemPrice = 0;
        }
        else {
            servicePrice = 0;
            servicePriceStr = 0;
        }

        serviceTotal += servicePrice;
        productTotal += itemPrice;
        itemCostPriceTotal += itemTotalCostPrice;
        itemCountTotal += itemCount;
        itemTotalStr += itemTotal;


        var productInfo = G.normalize(orderItem.productInfo, "--");
        var productInfoTitle = productInfo;
        if (productInfo.length >= 9) {
            productInfo = productInfo.substring(0, 9) + "...";
        }

        var tr = '<tr class="table-row-original">';
        tr += '<td class="first-padding">' + (index + 1) + '</td>';
        tr += '<td title="' + createdTimeStr + '">' + createdTimeStr + '</td>';
        tr += '<td title="' + orderReceiptNo + '">';
        if ("SALE" == orderType) {
            tr += '<a target="_blank" style="cursor: pointer;color: #6699CC;" href="sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=' + orderId + '">' + orderReceiptNo + '</a>';
        } else if ("WASH_BEAUTY" == orderType) {
            tr += '<a target="_blank" style="cursor: pointer;color: #6699CC;" href="washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=' + orderId + '">' + orderReceiptNo + '</a>';
        } else if ("REPAIR" == orderType) {
            tr += '<a target="_blank" style="cursor: pointer;color: #6699CC;" href="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderId + '">' + orderReceiptNo + '</a>';
        } else {
            tr += orderReceiptNo;
        }
        tr += '</td>';
        tr += '<td title="' + orderTypeValue + '">' + orderTypeValue + '</td>';
        if ("DISABLED" == customerStatus) {
            tr += '<td style="text-align: center;color:#999999" title="' + customerName + '"><span class="customer limit-span">' + customerName + '</span></td>';
        } else {
            tr += "<td title='" + customerName + "'>" + '<a href ="#" class="blue_color" onclick="openCustomer(\'' + customerOrSupplierId + '\',\'' + "customer" + '\')">' + customerName + "</a> " + "</td> ";
        }
        if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
            tr += '<td title="' + vehicle + '">' + vehicle + '</td>';
            tr += '<td title="' + serivceTitle + '">' + serivce + '</td>';
            tr += '<td title="' + servicePriceStr + '">' + servicePriceStr + '</td>';
        }
        tr += '<td title="' + productInfoTitle + '">' + productInfo + '</td>';
        tr += '<td title="' + itemPrice + '">' + itemPrice + '</td>';
        tr += '<td title="' + itemTotalCostPriceStr + '">' + itemTotalCostPriceStr + '</td>';
        tr += '<td title="' + itemCount + '">' + itemCount + '</td>';
        tr += '<td title="' + itemTotal + '" class="last-padding">' + itemTotal + '</td>';
        tr += '</tr>';
        $("#businessStatisticsTable").append($(tr));
        tableUtil.limitSpanWidth($(".customer", "#businessStatisticsTable"), 10);
    });

    var lastTr = "";

    if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
        lastTr = "<tr class='table-row-original'> <td colspan='7' style='text-align:center;'>本页小计：" + "</td>";
        lastTr += "<td>" + serviceTotal + "</td>";
        lastTr += "<td>" + "--" + "</td>";

    } else {
        lastTr = "<tr class='table-row-original'> <td colspan='5' style='text-align:center;'>本页小计：" + "</td>";
        lastTr += "<td>" + "--" + "</td>";

    }
    lastTr += "<td>" + dataTransition.rounding(productTotal, 2) + "</td>";
    lastTr += "<td>" + dataTransition.rounding(itemCostPriceTotal, 2) + "</td>";
    lastTr += "<td>" + dataTransition.rounding(itemCountTotal, 2) + "</td>";
    lastTr += "<td>" + dataTransition.rounding(itemTotalStr, 2) + "</td>";

    lastTr += "</tr>";
    $("#businessStatisticsTable").append($(lastTr));

    tableStyleAdjuct("#businessStatisticsTable");
    $("#recordNum").text(data[0].itemNumFound);
    $("#orderTotal").text(data[0].totalAmounts.ITEM_TOTAL);
    $("#settledTotal").text(data[0].totalAmounts.ITEM_COUNT);
    $("#debtTotal").text(data[0].totalAmounts.ITEM_TOTAL_COST_PRICE);


    $("#currentPage").val(data[data.length - 1].currentPage);
    if (data[data.length - 1].totalRows > 0) {
        $("#print3").show();
    }
    else {
        $("#print3").hide();
    }
}

/**
 * 调整table样式
 */
function tableStyleAdjuct(tableObject) {
    tableUtil.tableStyle(tableObject, '.titleBg');
}
/*****柱*****/

function checkedChartTypeRadio(type) {
    switch (type) {
        case 'day':
            $("#radMonth")[0].className = "rad_off";
            $("#radDay")[0].className = "r_on";
            break;
        case 'month':
            $("#radMonth")[0].className = "r_on";
            $("#radDay")[0].className = "rad_off";
            break;
    }
//    var statType = $("input[name='statType']:checked").val();
//    getCartData(type,statType);
}
;

//Ajax:get the data according to the date.
function getCartData(chartType, statType) {
    $.ajax({
        url: "itemStat.do?method=getChartData",
        type: "POST",
        dataType: "json",
        data: {
            chartType: chartType,
            statType: statType
        },
        success: function (json) {
            try {
                drawChart(json);
            } catch (e) {
                GLOBAL.debug(e);
                alert("数据异常,请联系客服人员");
            }
        },
        error: function () {
            alert("数据异常,请联系客服人员");
        }
    });
}
;

function drawChart(json) {
    if (json && json != null) {
        $("#noData").css('display', 'none');
        $("#chart_div").css('display', 'block');

        //Fill the color array.
        var title = $("input[name='statType']:checked").attr("title");
//        var a = createHighchart(title,json.categories,json.categoriesMap,json.data,json.categoryName);
//        a.redraw();
        CategoriesMap = json.categoriesMap;
        if (!MyChart) {
            MyChart = createHighchart(title, json.categories, json.data, json.categoryName);
            MyChart.redraw();
        } else {
            MyChart.setTitle({
                text: title,
                style: {
                    color: '#000',
                    fontSize: '16px',
                    fontWeight: 'bold'
                }
            });
            MyChart.xAxis[0].setCategories(json.categories, false);
            if (MyChart.series && MyChart.series[0]) {
                MyChart.series[0].remove(false);
            }
            MyChart.addSeries({
                name: json.categoryName,
                data: json.data
            }, false);
            MyChart.redraw();
        }
    } else {
        $("#chart_div").css('display', 'none');
        $("#noData").css('display', 'block');
    }
}
;

//Build a chart.
function createHighchart(title, categories, data, name) {
    return new Highcharts.Chart({
        chart: {
            renderTo: 'chart_div',
            type: 'column',
            backgroundColor: {
                linearGradient: [0, 0, 0, 500],
                stops: [
                    [0, 'rgb(255, 255, 255)'],
                    [1, 'rgb(230, 230, 230)']
                ]
            },
            width: 550,
            height: 270
        },
        title: {
            text: title,
            style: {
                color: '#000',
                fontSize: '16px',
                fontWeight: 'bold'
            }
        },
        xAxis: {
            categories: categories
//            labels: {
//                rotation: -45,
//                align: 'right',
//                style: {font: 'normal 13px 宋体'}
//            }
        },
        yAxis: {
            title: {text: ''} //销售额（元）
        },
        plotOptions: {
            column: {
                cursor: 'pointer'
            }
        },
        tooltip: {
            formatter: function () {
//                GLOBAL.debug(this.point);
//                GLOBAL.debug(this.point.category);
//                GLOBAL.debug(this.point.x);
                return CategoriesMap[this.x] + ':<br />' + '¥<strong>' + this.y + '</strong>元';
            }
        },
        series: [
            {
                name: name,
                data: data
            }
        ],
        exporting: {
            enabled: false
        },
        legend: {
            borderWidth: 0
        }
    });
}

function notOpen() {
    var time = new Array(), timeFlag = true;
    time[0] = new Date().getTime();
    time[1] = new Date().getTime();
    var reg = /^(\d+)$/;
    time[1] = new Date().getTime();
    if (time[1] - time[0] > 3000 || timeFlag) {
        time[0] = time[1];
        timeFlag = false;
        showMessage.fadeMessage("35%", "40%", "slow", 3000, "此功能稍后开放！");     // top left fadeIn fadeOut message
    }
}

function provinceBind() {
  var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
    data: {"parentNo":"1"}, dataType: "json"});
  if (!r || r.length == 0) return;
  else {
    for (var i = 0, l = r.length; i < l; i++) {
      var option = $("<option>")[0];
      option.value = r[i].no;
      option.innerHTML = r[i].name;
      option.style.color = "#000000";
      if ($("#provinceNo")[0]) {
        $("#provinceNo")[0].appendChild(option);
      }
    }
  }
}

function cityBind() {
  var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
    data: {"parentNo": $("#provinceNo").val()}, dataType: "json"});
  if (!r || r.length == 0) return;
  else {
    for (var i = 0, l = r.length; i < l; i++) {
      var option = $("<option>")[0];
      option.value = r[i].no;
      option.innerHTML = r[i].name;
      option.style.color = "#000000";
      $("#cityNo")[0].appendChild(option);
    }
  }
}

function regionBind() {
  var r = APP_BCGOGO.Net.syncGet({url: "shop.do?method=selectarea",
    data: {"parentNo": $("#cityNo").val()}, dataType: "json"});
  if (!r || r.length == 0) return;
  else {
    for (var i = 0, l = r.length; i < l; i++) {
      var option = $("<option>")[0];
      option.value = r[i].no;
      option.innerHTML = r[i].name;
      option.style.color = "#000000";
      $("#regionNo")[0].appendChild(option);
    }
  }
}

function clearInitValue() {
    var inputs = $(".lineBody input[type='text']");
    var initialValue;
    var value;
    for (var i = 0, max = inputs.length; i < max; i++) {
        initialValue = $(inputs[i]).attr("initValue");
        value = inputs[i].value;
        if (!initialValue) {
            continue;
        }
        if (value == initialValue) {
            $(inputs[i]).val("");
        }
    }
}