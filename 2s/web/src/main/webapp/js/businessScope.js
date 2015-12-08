var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
var newProductCache = {};


/**
 *                 var ajaxUrl = "product.do?method=searchBrandSuggestion";

 */

$(document).ready(function() {

    /**
     *  cache 当前域单位，车辆信息
     *  timeOut 表示当前key过期时间，-1 表示不cache，0，表示不会过期，>0 表示多少ms后过期
     *  syncTime 时间拿前台时间。
     */
    newProductCache = {

        defaultTimeOut: 1000 * 60 * 10,
        //cache field
        unitSource: ["个","只","台","件","条","瓶","箱","包","捆","架"],
        productVehicleBrandSource: {
            timeOut: 1000 * 60 * 10, //过期时间
            data: {"key": {syncTime: 0, source: []}}     //syncTime 同步时间
        }, productVehicleModelSource: {
            timeOut: 1000 * 60 * 10, //过期时间
            data: {"key": {syncTime: 0, source: []}}
        }, productKindSource: {
            timeOut: 1000 * 60 * 10, //过期时间
            data: {"key": {syncTime: 0, source: []}}
        },


        //目前只用来判断  productVehicleBrandSource，productVehicleModelSource 这两个 field  ,true 表示超时，false 表示未超时
        isTimeOut: function (key, cacheField) {
            if (!this[cacheField]) {
                return true;
            }
            if (this[cacheField] && this[cacheField].data && this[cacheField].data[key] && this[cacheField].data[key].syncTime) {
                var lastSyncTime = this[cacheField].data[key]["syncTime"] || 0;
                var maxTimeOut = this[cacheField].timeOut || this.defaultTimeOut || 5000;
                var timestamp = (new Date()).valueOf();
                if (timestamp - lastSyncTime < maxTimeOut) {
                    return false;
                }
            }
            return true;
        }

    };


    drawTable();

    var ajaxUrl = "businessScope.do?method=getAllBusinessScope";
    var ajaxData = {
    };
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
        initBusinessScope(json);
    });

    $("input[id$='.commodityCode']").live("blur", function () {
        $(this).val(App.StringFilter.commodityCodeFilter($(this).val()));
    });
    $("input[id$='.name'],input[id$='.brand'],input[id$='.spec'],input[id$='.model'],input[id$='.sellUnit']").live("blur", function () {

        var checkInfo = checkProductInfo();
        if (checkInfo && checkInfo != null) {
            return;
        }
        var length = $(".productItem").size();
        if (length > 10) {
            showProductErrorMessage(false, "最多只能添加10条主营产品");
            $(this).removeAttr("lock");
            return false;
        } else if (length < 5) {
            showProductErrorMessage(false, "最少要添加5条主营产品");
            $(this).removeAttr("lock");
            return false;
        }
        if (length > 1 && checkProductSame()) {
            showProductErrorMessage(false, "产品信息有重复内容，请修改或删除。");
            $(this).removeAttr("lock");
            return false;
        }

        if (length > 1 && checkCommodityCodeSame()) {
            showProductErrorMessage(false, "产品编码有重复内容，请修改或删除。");
            $(this).removeAttr("lock");
            return false;
        }
        showProductErrorMessage(true);
    });

    //删除行
    $(".deleteProduct").live("click", function(event) {
        var target = event.target;
        var idPrefix = $(target).attr("id").split(".")[0];
        $(target).closest("tr").next().remove();
        $(target).closest("tr").remove();
        var length = $(".productItem").size();
        if (length == 0) {
            addNewRow(length);
        } else if ($("#productTable tr:last").prev().find("td:last").find(".addNewProduct").size() == 0) {
          $("#productTable tr:last").prev().find("td:last").find(".deleteProduct")
              .after('&nbsp;<a id="productDTOs' + (length - 1) + '.addNewProduct" class="addNewProduct a-link">新增</a>');
        }
        drawTable();

        var length = $(".productItem").size();
        if (length > 10) {
            showProductErrorMessage(false, "最多只能添加10条主营产品");
            $(this).removeAttr("lock");
            return false;
        } else if (length < 5) {
            showProductErrorMessage(false, "最少要添加5条主营产品");
            $(this).removeAttr("lock");
            return false;
        }
        if (length > 1 && checkProductSame()) {
            showProductErrorMessage(false, "产品信息有重复内容，请修改或删除。");
            $(this).removeAttr("lock");
            return false;
        }

        if (length > 1 && checkCommodityCodeSame()) {
            showProductErrorMessage(false, "产品编码有重复内容，请修改或删除。");
            $(this).removeAttr("lock");
            return false;
        }
        showProductErrorMessage(true);

    });

    $(".addNewProduct").live("click", function(event) {
        var target = event.target;

        var checkInfo = checkProductInfo();
        if (checkInfo && checkInfo != null) {
            return;
        }

        var length = $(".productItem").size();
        if (length >= 10) {
            showProductErrorMessage(false, "最多只能添加10条主营产品");
            return false;
        }
        if (length > 1 && checkProductSame()) {
            showProductErrorMessage(false, "产品信息有重复内容，请修改或删除。");
            return false;
        }

        if (length > 1 && checkCommodityCodeSame()) {
            showProductErrorMessage(false, "产品编码有重复内容，请修改或删除。");
            return false;
        }
        addNewRow(length);

        drawTable();

        var length = $(".productItem").size();
        if (length < 5) {
            showProductErrorMessage(false, "最少要添加5条主营产品");
            $(this).removeAttr("lock");
            return false;
        }
        var checkInfo = checkProductInfo();
        if (checkInfo && checkInfo != null) {
            return;
        } else {
            showProductErrorMessage(true);
        }

    });

    $(".secondCategory").live("click", function(event) {
        var secondId = $(this).attr("value");
        showThirdCategory(secondId, this);
    });

    $(".select_close").live("click", function() {
        var id = $(this).parent().parent().attr("id");
        $("#" + id).remove();
        var idPrefix = id.split("_")[0];
        var second = $("#" + idPrefix);
        if (second.hasClass("secondThird")) {
            $("#" + idPrefix).parent().find('a').removeClass("hovered");
            $("#" + idPrefix).parent().find('a').removeClass("thirdCategoryClicked");
            $("#" + idPrefix + "_label").removeClass('clicked');
        } else if (second.hasClass("thirdCategory")) {
            $("#" + idPrefix).removeClass("thirdCategoryClicked");

            var size = $("#" + idPrefix).parent().find('.thirdCategoryClicked').length;

            var secondId = $("#" + idPrefix).parent().find('a').eq(0).attr("id");

            if ($("#" + idPrefix).parent().find('a').length == 1) {
                secondId = $("#" + idPrefix).parent().attr("id").split("_")[0];
            }

            if (size > 0) {
                if (!$("#" + secondId + "_label").hasClass("clicked")) {
                    $("#" + secondId + "_label").addClass('clicked');
                }
            } else {
                $("#" + secondId + "_label").removeClass('clicked');
            }
        }



        validateShopBusinessScope();
    });


    $("#businessScopeText").live("click focus keyup", function (event) {
        event = event || event.which;
        var keyCode = event.keyCode;

        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }

        var obj = this;

//        * {
//            *     isIgnoreMinWidth {Boolean}:false,
//                *     isEditable {Boolean}:false,
//                *     isDeletable {Boolean}:true,
//                *     isSupportKeyboard {Boolean}:true,
//                *     autoSet {Boolean}:true,
//                *     isNoticeWhenSave {Boolean}:true,
//                *     isNoticeWhenDelete {Boolean}:true,
//                *     saveWarning {String}:"",
//                *     deleteWarning {String}:"",
//                *     onSelect {function}:defaultFunction,
//                *     onSave {function}:defaultFunction,
//                *     onEdit {function}:defaultFunction,
//                *     onDelete {function}:defaultFunction,
//                *     onKeyboardSelect {function}:defaultFunction,
//                *     originalValue {String}:null,
//                *     data {Object}: need,
//                *     selector {JqueryObject}: need,
//                *     moveOnly {Boolean}: false,
//                *     // 非必须，此参数用在自定义数据时，此时需要注意，autoSet需设为false，并且同时需要自己处理 onKeyboardSelect 函数
//                    *     onGetInputtingData:function()
//        *
//            * }

        // scope: businessScope.do?method=searchBusinessScope
        var uuid = G.generateUUID();
        var droplist = App.Module.droplist;
        var $selector = $(event.currentTarget);

        // try to init
        droplist.init();
        // set uuid
        droplist.setUUID(uuid);

        // clear timer
        var timerId = 0;
        timerId = parseInt($(this).attr("data-stored-timerid")) || 0;
        clearTimeout(timerId);

        // mark current uuid
        $(this).attr("data-stored-uuid", uuid);

        timerId = setTimeout((function(uuid, $selector) {
            return function() {
                App.Net.asyncPost({
                    "dataType":"json",
                    "url":"businessScope.do?method=searchBusinessScope",
                    "data":{"uuid":uuid,"name":$selector.val()},
                    "success":function(data) {
                        if(!data || !data.data) return;

                        if(data.uuid !== $selector.attr("data-stored-uuid")) return;

                        for (var k in data.data) {
                            var o = data.data[k];
                            o["label"] = o["secondCategoryName"] + " > " + o["name"];
                        }

                        droplist.show({
                            "selector":$selector,
                            "moveOnly":false,
                            "autoSet":false,
                            "data":data,
                            "onSelect":function(event, index, data, hook) {
                                $(hook).val(data.name);

                                if (data.categoryType == "SECOND_CATEGORY") {
                                    showThirdCategory(data.id, $("#" + data.id + "_label")[0]);
                                } else if (data.categoryType == "THIRD_CATEGORY") {
                                    showThirdCategory(data.parentId, $("#" + data.parentId + "_label")[0]);
                                    $("#" + data.id).click();
                                }
                            },
                            "onKeyboardSelect":function(event, index, data, hook) {
                                $(hook).val(data.name);

                                if (data.categoryType == "SECOND_CATEGORY") {
                                    showThirdCategory(data.id, $("#" + data.id + "_label")[0]);
                                } else if (data.categoryType == "THIRD_CATEGORY") {
                                    showThirdCategory(data.parentId, $("#" + data.parentId + "_label")[0]);
                                    $("#" + data.id).click();
                                }
                            },
                            onGetInputtingData:function() {
                                return {"name": $select.val()};
                            }
                        });
                    }
                });
            };
        } (uuid, $selector)), 300);
        $(this).attr("data-stored-timerid", timerId + "");
        //  TODO  to be test...
    });


    $("input[id$='.productVehicleBrand']").live("keyup click", function (e) {
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var $thisDom = $(this);
            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
            var searchField = $thisDom.attr("searchField");
            var cacheField = $thisDom.attr("cacheField");
            var thisDomVal = $thisDom.val();
            if (newProductCache.isTimeOut($thisDom.val(), cacheField)) {
                var ajaxData = {
                    searchWord: $thisDom.val(),
                    searchField: searchField,
                    uuid: GLOBAL.Util.generateUUID()
                };
                var ajaxUrl = "product.do?method=searchBrandSuggestion";
                APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
                    var source = [];
                    if (jsonStr != null && jsonStr.length > 0) {
                        for (var i = 0, len = jsonStr.length; i < len; i++) {
                            if (!G.Lang.isEmpty(jsonStr[i].name)) {
                                source.push(APP_BCGOGO.StringFilter.inputtingProductNameFilter(jsonStr[i].name));
                            }
                        }
                    }
                    newProductCache[cacheField].data[thisDomVal] = {};
                    newProductCache[cacheField].data[thisDomVal]["source"] = source;
                    newProductCache[cacheField].data[thisDomVal]["syncTime"] = (new Date()).valueOf();
                    if ($(document.activeElement).attr("name") == $thisDom.attr("name")) {
                        $thisDom.autocomplete({
                            minLength: 0,
                            delay: 0,
                            source: source
                        });
                        $thisDom.autocomplete("search", "");
                    }
                })
            } else {
                $thisDom.autocomplete({
                    minLength: 0,
                    delay: 0,
                    source: newProductCache[cacheField].data[$thisDom.val()]["source"]
                });
                $thisDom.autocomplete("search", "");
            }
        }
    }).live("blur",function () {
            if ($(this).val() != $(this).attr("lastVal")) {
                var id = $(this).attr("id");
                var idPrefix = id.split(".")[0];
                $("#" + idPrefix + "\\.productVehicleModel").val("");
            }
        }).live("focus", function () {
            $(this).attr("lastVal", $(this).val());
        });

    $("input[id$='.productVehicleModel']").live("keyup click", function (e) {
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var $thisDom = $(this);
            var id = $thisDom.attr("id");
            var idPrefix = id.split(".")[0];

            var pos = APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $thisDom.val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($thisDom.val()));
            APP_BCGOGO.StringFilter.getCursorPosition($thisDom[0], pos);
            var searchField = $thisDom.attr("searchField");
            var cacheField = $thisDom.attr("cacheField");
            var brandVal = $("#" + idPrefix +"\\.productVehicleBrand").val();
            var sourceKey = brandVal + "_&&_" + $thisDom.val();
            if (newProductCache.isTimeOut(sourceKey, cacheField)) {
                var ajaxData = {
                    brandValue: brandVal,
                    searchWord: $thisDom.val(),
                    searchField: searchField,
                    uuid: GLOBAL.Util.generateUUID()
                };
                var ajaxUrl = "product.do?method=searchBrandSuggestion";
                APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
                    var source = [];
                    if (jsonStr != null && jsonStr.length > 0) {
                        for (var i = 0, len = jsonStr.length; i < len; i++) {
                            if (!G.Lang.isEmpty(jsonStr[i].name)) {
                                source.push(APP_BCGOGO.StringFilter.inputtingProductNameFilter(jsonStr[i].name));
                            }
                        }
                    }
                    newProductCache[cacheField].data[sourceKey] = {};
                    newProductCache[cacheField].data[sourceKey]["source"] = source;
                    newProductCache[cacheField].data[sourceKey]["syncTime"] = (new Date()).valueOf();
                    if ($(document.activeElement).attr("name") == $thisDom.attr("name")) {
                        $thisDom.autocomplete({
                            minLength: 0,
                            delay: 0,
                            source: source,
                            select: function (e, ui) {
                                $.ajax({
                                    type: "POST",
                                    data: {
                                        modelValue: ui.item.value,
                                        searchField: "brand"
                                    },
                                    url: "product.do?method=searchBrandSuggestion",
                                    cache: false,
                                    dataType: "json",
                                    error: function (XMLHttpRequest, error, errorThrown) {
                                    },
                                    success: function (jsonStr) {
                                        if (jsonStr && jsonStr.length > 0 && !G.Lang.isEmpty(jsonStr[0].name)) {
                                            $("#" + idPrefix +"\\.productVehicleBrand").val(jsonStr[0].name);
                                        }
                                    }
                                });
                            }
                        });
                        $thisDom.autocomplete("search", "");
                    }
                })
            } else {
                $thisDom.autocomplete({
                    minLength: 0,
                    delay: 0,
                    source: newProductCache[cacheField].data[sourceKey]["source"],
                    select: function (e, ui) {
                        $.ajax({
                            type: "POST",
                            data: {
                                modelValue: ui.item.value,
                                searchField: "brand"
                            },
                            url: "product.do?method=searchBrandSuggestion",
                            cache: false,
                            dataType: "json",
                            error: function (XMLHttpRequest, error, errorThrown) {
                            },
                            success: function (jsonStr) {
                                if (jsonStr && jsonStr.length > 0 && !G.Lang.isEmpty(jsonStr[0].name)) {
                                    $("#" + idPrefix +"\\.productVehicleBrand").val(jsonStr[0].name);
                                }
                            }
                        });
                    }
                });
                $thisDom.autocomplete("search", "");
            }
        }
    });

    /**
     * 大小单位输入校验
     */
    $("input[id$='.sellUnit']").live("keyup",function (e) {
        var eventKeyCode = e.which || e.keyCode;
        if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
            var pos = APP_BCGOGO.StringFilter.getCursorPosition(this, APP_BCGOGO.StringFilter.inputtingProductNameFilter);
            $(this).val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($(this).val()));
            APP_BCGOGO.StringFilter.setCursorPosition(this, pos);
            $(this).autocomplete("search", $(this).val());
        }
    }).live("click focus",function (e) {
            $(this).autocomplete({
                minLength: 0,
                delay: 0,
                source: newProductCache.unitSource,
                select: function (event, ui) {
                    $(this).val(APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter(ui.item.label));
                    newProductCache.unitSource = reSortUnitSource(ui.item.label, newProductCache.unitSource);
                }
            });
            $(this).autocomplete("search", "");
        }).live("blur", function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingProductNameFilter($(this).val()));
            var thisVal = $(this).val();
            if (!G.Lang.isEmpty(thisVal)) {
                newProductCache.unitSource = reSortUnitSource($.trim(thisVal), newProductCache.unitSource);
            }
        });

    //对输入的单位重新排序
    function reSortUnitSource(unit, unitSource) {
        if (!unitSource) {
            unitSource = [];
        }
        if (G.Lang.isEmpty(unit)) {
            return unitSource;
        }
        var newUnitSource = [];
        newUnitSource.unshift(unit);

        for (var i = 0, len = unitSource.length; i < len; i++) {
            if (!G.Lang.isEmpty(unitSource[i]) && unitSource[i] != unit) {
                newUnitSource.push(unitSource[i]);
            }
        }
        return newUnitSource;
    }


});

$(document).bind('click', function(event) {
    var target = event.target;
    if(target.id =="businessScopeText"){
        return;
    }

    if ($(target).hasClass("secondCategory") || $(target).hasClass("thirdCategory")
        || $(target).hasClass("secondThird")|| $(target).hasClass("ui-bcgogo-droplist-option-staticText")
        || $(target).hasClass("ui-bcgogo-droplist-option")) {
        return;
    }
    hideThirdCategory();
});

function initBusinessScope(json) {
    if (json == null || json == undefined) {
        return;
    }
    var firstChildren = json.children;
    var tr = '';
    tr += '<div id="businessScopeDiv"  class="promotion_content promotion_set">';
    for (var i = 0; i < firstChildren.length; i++) {
        var firstNode = firstChildren[i];
        var secondChildren = firstNode.children;
        for (var j = 0; j < secondChildren.length; j++) {
            var secondNode = secondChildren[j];
            var secondName = secondNode.text;
            var secondId = secondNode.idStr;

            tr += '<label id="' + secondId + '_label" value="' + secondId + '" class="list secondCategory">' + secondName + '</label>';
            var thirdChildren = secondNode.children;
            var thirdChildrenLength = thirdChildren.length;

            var width = 450;
            if(thirdChildrenLength <= 5){
                tr += '<div style="width:'+ width + 'px;display:none;" id="' + secondId + '_div" class="hide_content">';
            }else{
                tr += '<div style="width:500px;display:none;" id="' + secondId + '_div" class="hide_content">';
            }

            if (thirdChildrenLength > 1) {
                tr += '<a onclick="secondCategoryClick(this)" id="' + secondId + '" value="' + secondName + '" class="secondThird">' + '全选' + '</a>';
            }

            for (var k = 0; k < thirdChildren.length; k++) {
                var thirdNode = thirdChildren[k];
                var thirdName = thirdNode.text;
                var thirdId = thirdNode.idStr;
                tr += '<a onclick="thirdCategoryClick(this)" id="' + thirdId + '" value="' + thirdName + '" class="thirdCategory">' + thirdName + '</a>';
            }

            tr += "</div>";
        }
    }
    tr += '</div>';
    $("#businessScopeTd").html(tr);
}


function secondCategoryClick(obj) {
    var thisId = $(obj).attr("id");
    var value = $(obj).attr("value");

    var length = $(obj).parent().find('a').length;

    if (!$(obj).hasClass("hovered")) {
        $(obj).addClass("hovered");
        $(obj).nextAll().addClass('thirdCategoryClicked');
        $("#" + thisId + "_label").addClass('clicked');

        for (var index = 1; index < length; index++) {
            var thirdId = $(obj).parent().find('a').eq(index).attr("id");
            $("#" + thirdId + "_selectDiv").remove();
        }
        if ($("#" + thisId + "_selectDiv").length == 0) {
            var str = '<div id="' + thisId + '_selectDiv" class="select_blue">' +
                '<span class="select_left"></span>' +
                '<span class="select_body">' + value + '<a class="select_close"></a></span><span class="select_right"></span></div>';
            var selectedHtml = $("#businessScopeSelectdTd").html();
            selectedHtml += str;
            $("#businessScopeSelectdTd").html(selectedHtml);
        }

    } else {
        $(obj).removeClass("hovered");
        $(obj).nextAll().removeClass('thirdCategoryClicked');
        $("#" + thisId + "_label").removeClass('clicked');

        for (var index = 1; index < length; index++) {
            var thirdId = $(obj).parent().find('a').eq(index).attr("id");
            $("#" + thirdId + "_selectDiv").remove();
        }

        if ($("#" + thisId + "_selectDiv").length > 0) {
            $("#" + thisId + "_selectDiv").remove();
        }
    }
    validateShopBusinessScope();
}

function validateShopBusinessScope() {
    if ($("#businessScopeSelectdTd").find("div").size() < 1) {
        $("#businessScopeSelectdTd").attr("colSpan",3);
        if($("#businessScopes_tips_td").length < 1){
            $("#businessScopeSelectdTd").after('<td id="businessScopes_tips_td" style="text-align:left;"><div class="tips" node-type="businessScopes_tips"></div></td>');
        }
        $("div[node-type='businessScopes_tips']").html(register["businessScopes"].empty);
    } else {
        $("#businessScopeSelectdTd").attr("colSpan",4);
        $("#businessScopes_tips_td").remove();
    }
}

function showProductErrorMessage(isRight, errorMessage) {
    if (isRight) {
        $("span[node-type='product_tips']").html("<a class='right'></a>");
    } else {
        $("span[node-type='product_tips']").html('<a class="wrong"></a><span class="red_color">' + errorMessage + '</span>');
    }
}

function resetThirdCategoryIdStr() {
    var thirdCategoryIdStr = "";
    $(".thirdCategoryClicked").each(function () {
        if ($(this).hasClass("thirdCategory")) {
            var id = $(this).attr("id");
            thirdCategoryIdStr += id + ",";
        }
    });
    $("#thirdCategoryIdStr").val(thirdCategoryIdStr);
}

function thirdCategoryClick(obj) {
    var thisId = $(obj).attr("id");
    var value = $(obj).attr("value");
    var size = $(obj).parent().find("a").size();

    var secondIdStr = $(obj).parent().find('a').eq(0).attr("id");
    var secondValue = $(obj).parent().find('a').eq(0).attr("value");

    if ($(obj).parent().find('a').length == 1) {
        secondIdStr = $(obj).parent().attr("id").split("_")[0];
        secondValue = $("#" + secondIdStr + "_label").text();
    }

    var selectedHtml = "";


    if (!$(obj).hasClass("thirdCategoryClicked")) {
        $(obj).addClass("thirdCategoryClicked");

        if (!$("#" + secondIdStr + "_label").hasClass("clicked")) {
            $("#" + secondIdStr + "_label").addClass('clicked');
        }

        var length = $(obj).parent().find('.thirdCategoryClicked').length;
        if (size > 1) {
            if (length == size - 1) {
                if (!$(obj).parent().find('a').eq(0).hasClass("hovered")) {
                    $(obj).parent().find('a').eq(0).addClass("hovered");
                }

                var divLength = length = $(obj).parent().find('a').length;
                for (var index = 1; index < divLength; index++) {
                    var thirdId = $(obj).parent().find('a').eq(index).attr("id");
                    $("#" + thirdId + "_selectDiv").remove();
                }
                if ($("#" + secondIdStr + "_selectDiv").length == 0) {
                    var str = '<div id="' + secondIdStr + '_selectDiv" class="select_blue">' +
                        '<span class="select_left"></span>' +
                        '<span class="select_body">' + secondValue + '<a class="select_close"></a></span><span class="select_right"></span></div>';
                    selectedHtml = $("#businessScopeSelectdTd").html();
                    selectedHtml += str;
                    $("#businessScopeSelectdTd").html(selectedHtml);
                }
            } else {
                var str = '<div id="' + thisId + '_selectDiv" class="select">' +
                    '<span class="select_left"></span>' +
                    '<span class="select_body">' + value + '<a class="select_close"></a></span><span class="select_right"></span></div>';
                selectedHtml = $("#businessScopeSelectdTd").html();
                selectedHtml += str;
                $("#businessScopeSelectdTd").html(selectedHtml);
            }
        } else {
            var str = '<div id="' + thisId + '_selectDiv" class="select">' +
                '<span class="select_left"></span>' +
                '<span class="select_body">' + value + '<a class="select_close"></a></span><span class="select_right"></span></div>';
            selectedHtml = $("#businessScopeSelectdTd").html();
            selectedHtml += str;
            $("#businessScopeSelectdTd").html(selectedHtml);
        }
    } else {
        $(obj).removeClass("thirdCategoryClicked");
        if (size > 1) {
            $(obj).parent().find('a').eq(0).removeClass("hovered");
            if ($("#" + secondIdStr + "_selectDiv").length > 0) {
                $("#" + secondIdStr + "_selectDiv").remove();
            }

        }

        var clickedSize = $(obj).parent().find('.thirdCategoryClicked').length;
        if (clickedSize > 0) {
            if (!$("#" + secondIdStr + "_label").hasClass("clicked")) {
                $("#" + secondIdStr + "_label").addClass('clicked');
            }
        } else {
            $("#" + secondIdStr + "_label").removeClass('clicked');
        }

        if ($("#" + thisId + "_selectDiv").length > 0) {
            $("#" + thisId + "_selectDiv").remove();
        }


        var htmlStr = "";
        for (var index = 0; index < size; index++) {

            if ($(obj).parent().find('a').eq(index).hasClass("thirdCategoryClicked")) {
                var thirdId = $(obj).parent().find('a').eq(index).attr("id");
                $("#" + thirdId + "_selectDiv").remove();
                var thirdValue = $(obj).parent().find('a').eq(index).attr("value");
                htmlStr += '<div id="' + thirdId + '_selectDiv" class="select">' +
                    '<span class="select_left"></span>' +
                    '<span class="select_body">' + thirdValue + '<a class="select_close"></a></span><span class="select_right"></span></div>';
            }
        }
        selectedHtml = $("#businessScopeSelectdTd").html();
        selectedHtml += htmlStr;
        $("#businessScopeSelectdTd").html(selectedHtml);
    }
    validateShopBusinessScope();
}

function showThirdCategory(id, obj) {
    $(".secondCategory").not($(this)).removeClass("hover");
    if (!$(obj).hasClass("hover")) {
        $(obj).addClass("hover");
    }

    $(".hide_content").css("display", "none");
    $("#" + id + "_div").css("display", "block");
}

function hideThirdCategory() {
    $(".hide_content").css("display", "none");
    $(".secondCategory").removeClass("hover");
}


function addNewRow(length) {
    $("#productTable tr:last").prev().find("td:last").find(".addNewProduct").remove();
    var lenghStr = "[" + length + "]";
    var tr = '<tr class="productItem titBody_Bg" >' +
        '<td style="padding-left:5px;"><input type="text" id="productDTOs' + length + '.commodityCode" name="productDTOs' + lenghStr + '.commodityCode" class="txt"/></td>' +
        '<td><input type="text" id="productDTOs' + length + '.name" name="productDTOs' + lenghStr + '.name" class="txt"/></td>' +
        '<td><input type="text" id="productDTOs' + length + '.brand" name="productDTOs' + lenghStr + '.brand" class="txt"/></td>' +
        '<td><input type="text" id="productDTOs' + length + '.spec" name="productDTOs' + lenghStr + '.spec" class="txt"/></td>' +
        '<td><input type="text" id="productDTOs' + length + '.model" name="productDTOs' + lenghStr + '.model" class="txt"/></td>' +
        '<td><input type="text" id="productDTOs' + length + '.productVehicleBrand" searchField="brand" cacheField="productVehicleBrandSource" name="productDTOs' + lenghStr + '.productVehicleBrand" class="txt"/></td>' +
        '<td><input type="text" id="productDTOs' + length + '.productVehicleModel" searchField="model" cacheField="productVehicleModelSource" name="productDTOs' + lenghStr + '.productVehicleModel" class="txt"/></td>' +
        '<td><input type="text" id="productDTOs' + length + '.sellUnit" name="productDTOs' + lenghStr + '.sellUnit" class="txt"/></td>';

    tr += '<td><a id="productDTOs' + length + '.deleteProduct" class="deleteProduct a-link">删除</a>&nbsp;' +
        '<a id="productDTOs' + length + '.addNewProduct" class="addNewProduct a-link">新增</a></td>';
    tr += '</tr>';
    tr+='<tr class="titBottom_Bg"><td colspan="10"></td></tr>';
    $("#productTable").append(tr);
}


function checkProductInfo() {

    var result = false;
    $("input[id$='.commodityCode']").each(function () {
        var idPrefix = $(this).attr("id").split(".")[0];
        var index = (idPrefix.split("s")[1]) * 1 + 1;
        var productName = $("#" + idPrefix + "\\.name").val();
        var sellUnit = $("#" + idPrefix + "\\.sellUnit").val();
        if (productName.trim() == "") {
            showProductErrorMessage(false, "第" + index + "行请输入品名!");
            result = true;
            return false;
        }
        if (sellUnit.trim() == "") {
            showProductErrorMessage(false, "第" + index + "行请输入单位!");
            result = true;
            return false;
        }
    });
    return result;
}

function checkCommodityCodeSame() {
    var ItemSet = APP_BCGOGO.wjl.Collection.Set;
    var propertys = ["commodityCode"];
    var orderItems = $(".productItem");
    var newItemSet = new ItemSet();
    var commodityCodeSize = 0;
    orderItems.each(function () {
        var itemInfo = $(this).find("input[id$='." + propertys[0] + "']").val();
        if (itemInfo) {
            commodityCodeSize++;
            newItemSet.add(itemInfo);
        }
    });
    if (commodityCodeSize != newItemSet.size()) {
        return true;
    } else {
        return false;
    }
}


function checkProductSame() {
    var ItemSet = APP_BCGOGO.wjl.Collection.Set;
    var itemNum = $(".productItem").size();
    var propertys = [ "commodityCode","name", "brand", "spec", "model", "productVehicleBrand", "productVehicleModel"];
    var newItemSet = new ItemSet();
    $(".productItem").each(function (i) {
        var itemInfo = "";
        for (var i = 0, len = propertys.length; i < len; i++) {
            itemInfo += $(this).find("input[id$='." + propertys[i] + "']").val() + "__&&__$$";
        }
        newItemSet.add(itemInfo);
    });
    if (itemNum != newItemSet.size()) {
        return true;
    }
    return false;
}

function drawTable() {
    return;
    $(".tab_cuSearch tr").not(".titleBg").css({"border":"1px solid #bbbbbb","border-width":"1px 0px"});
    $(".tab_cuSearch tr:nth-child(odd)").not(".titleBg").css("background", "#eaeaea");

    $(".tab_cuSearch tr").not(".titleBg").hover(
        function () {
            $(this).find("td").css({"background":"#fceba9","border":"1px solid #ff4800","border-width":"1px 0px"});

            $(this).css("cursor", "pointer");
        },
        function () {
            $(this).find("td").css({"background-Color":"#FFFFFF","border":"1px solid #bbbbbb","border-width":"1px 0px 0px 0px"});
            $(".tab_cuSearch tr:nth-child(odd)").not(".titleBg").find("td").css("background", "#eaeaea");
        }
    );
}