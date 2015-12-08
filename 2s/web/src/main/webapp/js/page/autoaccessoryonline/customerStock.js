var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
$(document).ready(function () {
    initPagerInfo();
    $("#upClickShow").live("mouseover",function(){
        $(this).css({"color":"#fd5300","textDecoration":"underline"});
    });
    $("#upClickShow").live("mouseout",function(){
        $(this).css({"color":"#0094ff","textDecoration":"none"});
    });
    $("#clearConditionBtn").bind('click', function () {
        $(".J_clear_input").val("");
        $("#stockScope")[0].selectedIndex = 0;
        $("#stockScope").change().css("color", "#BBBBBB");
        $(".J-initialCss").placeHolder("reset");
    });

    $(".J-productSuggestion")
        .bind('click', function () {
            productSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                productSuggestion($(this));
            }
        });
    //客户下拉框
    $(".J-relatedCustomerSuggestion")
        .bind('click', function () {
            relatedCustomerSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                relatedCustomerSuggestion($(this));
            }
        });

    function productSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var currentSearchField = $domObject.attr("searchField");
        var ajaxData = {
            searchWord: searchWord,
            searchField: currentSearchField,
            uuid: dropList.getUUID()
        };
        $domObject.parent().prevAll().each(function () {
            var $productSearchInput = $(this).find(".J-productSuggestion");
            if ($productSearchInput && $productSearchInput.length > 0) {
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
                        $('#searchCustomerStockBtn').click();
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
                        $('#searchCustomerStockBtn').click();
                    }
                });
            }
        });
    }

    function relatedCustomerSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var ajaxData = {
            searchWord: searchWord,
            searchField:"name",
            customerOrSupplier: "customer",
            searchStrategies:"customerOrSupplierShopIdNotEmpty",
            titles:"name",
            uuid: dropList.getUUID()
        };
        var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            dropList.show({
                "selector":$domObject,
                "data":result,
                "onSelect":function (event, index, data) {
                    $domObject.val(data.label);
                    $domObject.css({"color":"#000000"});
                    dropList.hide();
                }
            });
        });

    }

    //发送消息按钮
    $(".j_sendMessageBtn").live("click", function (e) {
        if (!GLOBAL.Lang.isEmpty($(this).attr("data-customerid"))) {
            window.open("stationMessage.do?method=createStationMessage&messageReceivers=" + $(this).attr("data-customerid") + ",", "_blank");
        }
    });


    //绑定搜索按钮
    $("#searchCustomerStockBtn").click(function () {
        $("#rowStartallCustomerStock").val(0);
        getAllCustomerStock();
    });

    $("#stockScope").bind("change", function () {
        var currentOption = $(this).find("option:selected");
        $(this).css("color", currentOption.css("color"));
        $("#inventoryAmountUp").val(currentOption.attr("inventoryAmountUp"));
        $("#inventoryAmountDown").val(currentOption.attr("inventoryAmountDown"));
    });

    //初始化  SearchConditionInput css
    $(".J-initialCss").placeHolder();

    $("#searchCustomerStockBtn").click();
});

function initMiniPagerInfo() {
    var data = {rowStart: 0, pageRows: 10, currentPage: 1};
    var dataString = JSON.stringify(data);
    $("#datacusStock0").val(dataString);
    $("#datacusStock1").val(dataString);
    $("#datacusStock2").val(dataString);
    $("#datacusStock3").val(dataString);
    $("#datacusStock4").val(dataString);
    $("#pageRowscusStock0").val(10);
    $("#pageRowscusStock1").val(10);
    $("#pageRowscusStock2").val(10);
    $("#pageRowscusStock3").val(10);
    $("#pageRowscusStock4").val(10);
    $("#rowStartcusStock0").val(0);
    $("#rowStartcusStock1").val(0);
    $("#rowStartcusStock2").val(0);
    $("#rowStartcusStock3").val(0);
    $("#rowStartcusStock4").val(0);
    $("#totalRowscusStock0").val(0);
    $("#totalRowscusStock1").val(0);
    $("#totalRowscusStock2").val(0);
    $("#totalRowscusStock3").val(0);
    $("#totalRowscusStock4").val(0);
    //针对小分页
    $(".stockPager .i_pageBtn .line").css("visibility", "visible");
}

function initPagerInfo() {
    var data = {rowStart: 0, pageRows: 5, currentPage: 1};
    var dataString = JSON.stringify(data);
    $("#pageRowsallCustomerStock").val(5);
    $("#rowStartallCustomerStock").val(0);
    $("#totalRowsallCustomerStock").val(0);
    $("#dataallCustomerStock").val(dataString);
    initMiniPagerInfo();
}

function initAllCustomerStockTables(json) {
    $("table[id^=cusStock] tr").not(".J-title").remove();
    initMiniPagerInfo();
    if(!G.Lang.isEmpty(json)){
        var count = 0;
        for (var key in json) {
            var result = json[key];
            var customer = result[0];
            if (customer == null) {
                continue;
            }

            var products = result[1].products;
            var numFound = result[1].numFound;
            var idStr = customer.idStr;
            var name = customer.name;
            var contact = G.Lang.normalize(customer.contact);
            var mobile = G.Lang.normalize(customer.mobile);
            var tr = '<tr class="title_Bg J-customerInfo">';
            tr += '      <td colspan="5">';
            tr += '      <div class="titLbl">客户：<a href="#">' + name + '</a></div>';
            tr += '      <div class="titLbl">联系人：<span>' + contact + '</span></div>';
            tr += '      <div class="titLbl">联系方式：<span>' + mobile + '</span></div>';
            tr += '      <div class="titLbl">购买种类：<label class="yellow_color">' + numFound + '种</label></div>';
            tr += '      <a class="up" id="upClickShow">更多</a>';
            tr += '      <a class="btns j_sendMessageBtn" data-customerid="' + idStr + '">发送消息</a>';
            tr += '      </td>';
            tr += '</tr>';
            $("#cusStock" + count).find(".J-title").after($(tr));
            $("#customerShopId" + count).val(customer.customerShopIdStr);

            for (var i = 0; i < products.length; i++) {
                var commodityCode = G.Lang.normalize(products[i].commodityCode) + (G.Lang.isEmpty(products[i].commodityCode) ? "" : "&nbsp;");
                var productName = G.Lang.normalize(products[i].name) + (G.Lang.isEmpty(products[i].name) ? "" : "&nbsp;");
                var productBrand = G.Lang.normalize(products[i].brand) + (G.Lang.isEmpty(products[i].brand) ? "" : "&nbsp;");
                var productSpec = G.Lang.normalize(products[i].spec) + (G.Lang.isEmpty(products[i].spec) ? "" : "&nbsp;");
                var productModel = G.Lang.normalize(products[i].model) + (G.Lang.isEmpty(products[i].model) ? "" : "&nbsp;");
                var productVehicleBrand = G.Lang.normalize(products[i].productVehicleBrand) + (G.Lang.isEmpty(products[i].productVehicleBrand) ? "" : "&nbsp;");
                var productVehicleModel = G.Lang.normalize(products[i].productVehicleModel) + (G.Lang.isEmpty(products[i].productVehicleModel) ? "" : "&nbsp;");
                var inventoryNum = G.Lang.normalize(products[i].inventoryNum);
                var sellUnit = G.Lang.normalize(products[i].sellUnit);
                var productInfo = commodityCode + "<span style='font-weight: bold;'>" + productName + productBrand + "</span>" + productSpec + productModel + productVehicleBrand + productVehicleModel;
                var lastPurchaseDateStr = G.Lang.normalize(products[i].lastPurchaseDateStr, "");
                var lastPurchaseAmount = G.Lang.normalize(dataTransition.simpleRounding(products[i].lastPurchaseAmount, 1), "");
                var lastPurchasePrice = G.Lang.normalize(dataTransition.simpleRounding(products[i].lastPurchasePrice, 2), "");
                tr = '<tr class="J-itemBody">';
                tr += '  <td style="padding-left:10px;">' + productInfo + '</td>';
                tr += '  <td>' + inventoryNum + '&nbsp;' + sellUnit + '</td>';
                tr += '  <td>' + lastPurchasePrice + '元</td>';
                tr += '  <td>' + lastPurchaseAmount + '&nbsp;' + sellUnit + '</td>';
                tr += '  <td>' + lastPurchaseDateStr + '</td>';
                tr += '</tr>';
                $("#cusStock" + count).append($(tr));
            }
            $("#totalRowscusStock" + count).val(numFound);
            initPagesForSolr("cusStock" + count, null, "getCustomerStock" + count);
            initCustomerStockStyle(count);
            $("#cusStock" + count + " tr:gt(6)").hide();
            $(".stockPager").hide();
            $("#noData").hide();
            $("#cusStock" + count).show();
            $("#allCustomerStockPager").show();
            count++;
        }
    }else{
        $("table[id^='cusStock']").hide();
        $("#allCustomerStockPager").hide();
        $("#noData").show();
    }


}


function initCustomerStockStyle(dynamicalID) {
    $("#cusStock" + dynamicalID).find(".J-itemBody").css({"border": "1px solid #bbbbbb", "border-width": "1px 0px"});
    $("#cusStock" + dynamicalID).find(".J-itemBody:nth-child(even)").css("background", "#eaeaea");

    $("#cusStock" + dynamicalID).find(".J-itemBody").hover(
        function () {
            $(this).find("td").css({"background": "#fceba9", "border": "1px solid #ff4800", "border-width": "1px 0px"});

            $(this).css("cursor", "pointer");
        },
        function () {
            $(this).find("td").css({"background-Color": "#FFFFFF", "border": "1px solid #bbbbbb", "border-width": "1px 0px 0px 0px"});
            $("#cusStock" + dynamicalID).find(".J-itemBody:nth-child(even)").find("td").css("background", "#eaeaea");
        }
    );
    $(".up").toggle(
        function () {
            $(this).parents("table").find("tr:gt(6)").show();
            $("#stockPager" + dynamicalID).css("display", "");
            $(this).html("收拢");
            $(this).removeClass().addClass("down");
        },
        function () {
            $(this).parents("table").find("tr:gt(6)").hide();
            $("#stockPager" + dynamicalID).css("display", "none");
            $(this).html("更多");
            $(this).removeClass().addClass("up");
        }
    );
}

function getAllCustomerStock() {
    $(".J-initialCss").placeHolder("clear");
    var param = $("#searchCustomerStockForm").serializeArray();
    var data = {};
    $.each(param, function (index, val) {
        data[val.name] = val.value;
    });

    data["start"] = $("#rowStartallCustomerStock").val();
    data["rows"] = $("#pageRowsallCustomerStock").val();
    var ajaxUrl = "autoAccessoryOnline.do?method=getAllRelatedCustomerStock";
    bcgogoAjaxQuery.setUrlData(ajaxUrl, data);
    bcgogoAjaxQuery.ajaxQuery(function (json) {
        $(".J-initialCss").placeHolder("reset");
        initAllCustomerStockTables(json[0]);
        $("#totalRowsallCustomerStock").val(json[1]);
        initPagesForSolr("allCustomerStock", null, "getAllCustomerStock");
    });
}

function getCustomerStock0() {
    getCustomerStock($("#customerShopId0").val(), 0);
}
function getCustomerStock1() {
    getCustomerStock($("#customerShopId1").val(), 1);
}
function getCustomerStock2() {
    getCustomerStock($("#customerShopId2").val(), 2);
}
function getCustomerStock3() {
    getCustomerStock($("#customerShopId3").val(), 3);
}
function getCustomerStock4() {
    getCustomerStock($("#customerShopId4").val(), 4);
}

function getCustomerStock(relatedCustomerShopId, count) {
    if (stringUtil.isEmpty(relatedCustomerShopId) || relatedCustomerShopId < 0) {
        nsDialog.jAlert("店铺信息异常！");
        return;
    }
    var relatedCustomerShopIds = relatedCustomerShopId;
    $(".J-initialCss").placeHolder("clear");
    var param = $("#searchCustomerStockForm").serializeArray();
    var data = {};
    $.each(param, function (index, val) {
        data[val.name] = val.value;
    });

    data["start"] = $("#rowStartcusStock" + count).val();
    data["rows"] = $("#pageRowscusStock" + count).val();
    data["relatedCustomerShopIds"] = relatedCustomerShopId;

    var ajaxUrl = "autoAccessoryOnline.do?method=getRelatedCustomerStock";
    bcgogoAjaxQuery.setUrlData(ajaxUrl, data);
    bcgogoAjaxQuery.ajaxQuery(function (json) {
        $(".J-initialCss").placeHolder("reset");
        initCusStock(json, count);
        initPagesForSolr("cusStock" + count, null, "getCustomerStock" + count);
        initCustomerStockStyle(count);
    });
}

function initCusStock(json, dynamicalID) {
    $("#cusStock" + dynamicalID + " tr").not(".J-title,.J-customerInfo").remove();
    var products = json.products;
    for (var i = 0; i < products.length; i++) {
        var commodityCode = G.Lang.normalize(products[i].commodityCode) + (G.Lang.isEmpty(products[i].commodityCode) ? "" : "&nbsp;");
        var productName = G.Lang.normalize(products[i].name) + (G.Lang.isEmpty(products[i].name) ? "" : "&nbsp;");
        var productBrand = G.Lang.normalize(products[i].brand) + (G.Lang.isEmpty(products[i].brand) ? "" : "&nbsp;");
        var productSpec = G.Lang.normalize(products[i].spec) + (G.Lang.isEmpty(products[i].spec) ? "" : "&nbsp;");
        var productModel = G.Lang.normalize(products[i].model) + (G.Lang.isEmpty(products[i].model) ? "" : "&nbsp;");
        var productVehicleBrand = G.Lang.normalize(products[i].productVehicleBrand) + (G.Lang.isEmpty(products[i].productVehicleBrand) ? "" : "&nbsp;");
        var productVehicleModel = G.Lang.normalize(products[i].productVehicleModel) + (G.Lang.isEmpty(products[i].productVehicleModel) ? "" : "&nbsp;");
        var inventoryNum = G.Lang.normalize(products[i].inventoryNum);
        var sellUnit = G.Lang.normalize(products[i].sellUnit);
        var productInfo = commodityCode + "<span style='font-weight: bold;'>" + productName + productBrand + "</span>" + productSpec + productModel + productVehicleBrand + productVehicleModel;
        var lastPurchaseDateStr = G.Lang.normalize(products[i].lastPurchaseDateStr, "");
        var lastPurchaseAmount = G.Lang.normalize(dataTransition.simpleRounding(products[i].lastPurchaseAmount, 1), "");
        var lastPurchasePrice = G.Lang.normalize(dataTransition.simpleRounding(products[i].lastPurchasePrice, 2), "");

        var tr = '<tr class="J-itemBody">';
        tr += '  <td style="padding-left:10px;">' + productInfo + '</td>';
        tr += '  <td>' + inventoryNum + '&nbsp;' + sellUnit + '</td>';
        tr += '  <td>' + lastPurchasePrice + '元</td>';
        tr += '  <td>' + lastPurchaseAmount + '&nbsp;' + sellUnit + '</td>';
        tr += '  <td>' + lastPurchaseDateStr + '</td>';
        tr += '</tr>';
        $("#cusStock" + dynamicalID).append($(tr));
    }
}

