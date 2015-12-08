var LazySearcher = App.wjl.LazySearcher;
//todo ckeck键盘事件，值未变化不提交请求
var lastvalue;

$(window).bind("load",function(){
    var recommend = $("[action-type=supplier-active-recommend]");
    for (var i = 0; i < recommend.length; i++) {
        $(recommend[i]).bubbleTips();
    }
});

function checkKeyUp(domObj, domEvent) {
    var keyName = G.keyNameFromEvent(G.getEvent(domEvent));
    if (G.contains(keyName, ["up", "down"])) { //||eventKeyCode == 32 拿掉空格键的keyup事件
        return false;
    }

    var currentVal = domObj.value;
    if (currentVal === lastvalue) {
        return false;
    }

    lastvalue = currentVal;
    return true;
}


//TODO 为所有单据添加验证功能

function productValidate() {
    function addRule(e, maxLen, isNum) {
        var event = G.getEvent(e);
        $(event.target).rules("add", {
            maxlength: maxLen,
            digits: isNum,
            messages: {
                maxlength: "内容不可超过" + maxLen + "个字符!",
                digits: "年代不合法,请输入正确数值!"
            }
        });
    }

    if ($("#brand")[0]) {
        $("#brand").attr("maxlength", "20");
    }
    if ($("#model")[0]) {
        $("#model").attr("maxlength", "20");
    }
    if ($("#engine")[0]) {
        $("#engine").attr("maxlength", "20");
    }
    $("input[id$='.productName']").live("focus", function (e) {
        addRule(e, 100, false);
    });

    $("input[id$='.brand']").live("focus", function (e) {
        addRule(e, 100, false);
    });

    $("input[id$='.spec']").live("focus", function (e) {
        addRule(e, 50, false);
    });

    $("input[id$='.model']").live("focus", function (e) {
        addRule(e, 50, false);
    });

    $("input[id$='.vehicleBrand']").live("focus", function (e) {
        addRule(e, 200, false);
    });

    $("input[id$='.vehicleModel']").live("focus", function (e) {
        addRule(e, 200, false);
    });

    $("input[id$='.vehicleYear']").live("focus", function (e) {
        $(this).attr("maxlength", "4");
        addRule(e, 4, true);
    });

    $("input[id$='.vehicleEngine']").live("focus", function (e) {
        addRule(e, 20, false);
    });
}

//TODO tn:行号；idstr：后缀。用于判断某行的某个INPUT是否存在

function checkThisDom(tn, idstr) {
    return !!$("#itemDTOs" + tn + "\\." + idstr)[0]
}

function checkWashBeautyItemDom(tn, idstr) {
    return !!$("#washBeautyOrderItemDTOs" + tn + "\\." + idstr)[0]
}

function checkServiceDom(tn, idstr) {
    return !!$("#serviceDTOs" + tn + "\\." + idstr)[0]
}

function checkThisDom2(tn, idstr) {
    return !!($("#otherIncomeItemDTOList" + tn + "\\." + idstr)[0])
}

//TODO 排除键盘的上下左右键的事件

function ruleOutDirectionKeys(domEvent) {
    var keyName = G.keyNameFromEvent(G.getEvent(domEvent));
    return G.contains(keyName, ["esc", "up", "right", "down"]);
}


function checkHistoryEvent(e) {
    var event = G.getEvent(e),
        keyName = G.keyNameFromEvent(event),
        target = event.target,
        idScope = ["getPagenull", "customerOrSupplierName", "itemName", "itemBrand", "itemSpec", "itemModel", "licenceNo_id", "services"];
    if ((!target || !target.id || !(target.className == "notBanBackspace" || G.contains(target.id, idScope))) && keyName === "backspace") {
        event.preventDefault();
    }
}


//TODO 该对象只负责联动清理功能

function clearItemUtil() {
    this.clear = function (target) {
        if (!target) {
            return;
        }
        var idSuffix = target.id;
        if (target.id.indexOf(".") > 0) {
            idSuffix = target.id.split(".")[1];
        }
        var ids = "";
        if (idSuffix == "productName") {
            ids = "input[id$='.productName']";
        } else if (idSuffix == "brand") {
            ids = "input[id$='.productName'],input[id$='.brand']";
        } else if (idSuffix == "spec") {
            ids = "input[id$='.productName'],input[id$='.brand'],input[id$='.spec']";
        } else if (idSuffix == "model") {
            ids = "input[id$='.productName'],input[id$='.brand'],input[id$='.spec'],input[id$='.model']";
        } else if (idSuffix == "vehicleModel") {
            ids = ",input[id$='.productName'],input[id$='.brand'],input[id$='.spec'],input[id$='.model'],input[id$='.vehicleModel'],input[id$='.vehicleBrand']";
        } else if (idSuffix == "vehicleBrand") {
            ids = ",input[id$='.productName'],input[id$='.brand'],input[id$='.spec'],input[id$='.model'],input[id$='.vehicleModel'],input[id$='.vehicleBrand']";
        } else if (idSuffix == "itemName") {
            ids = "input[id='itemName']";
        } else if (idSuffix == "itemBrand") {
            ids = "input[id='itemBrand'],input[id='itemName']";
        } else if (idSuffix == "itemSpec") {
            ids = "input[id='itemBrand'],input[id='itemName'],input[id='itemSpec']";
        } else {
            return;
        }
        clearItem(target, ids);
    };

    this.clearByFlag = function (target, flag) {
        if (flag) this.clear(target);
    };

    var getClearScope = function (dom) {
        var parentTable = $(dom).parents("table")[0];
        if (parentTable) {
            var className = ".item";
            if (parentTable && parentTable.id && parentTable.id == "table_productNo_2") {
                className = ".item1";
            }
            return className;
        } else {
            return ".searchInwareHistoryItem";
        }

    };

    var clearItem = function (node, ids) {
        var className = getClearScope(node);
        var className2 = className + "2";
        var $recommendedPrice = $("#" + node.id.split(".")[0] + "\\.recommendedPrice");
        if ($recommendedPrice) {
            $recommendedPrice.parents(className2).find("input").each(function () {
                this.value = "";
            });
        }
        var $upperLimit = $("#" + node.id.split(".")[0] + "\\.upperLimit");

        if ($upperLimit) {
            $upperLimit.parents(className2).find("input").each(function () {
                this.value = "";
            });
        }
        var exArray = "input[id$='.productVehicleStatus'],"+
                "input[id$='.hidden_productVehicleStatus'],"+
                "input[id$='.hidden_vehicleModel'],"+
                "input[id$='.hidden_vehicleBrand'],"+
                "input[id$='.vehicleYear'],"+
                "input[id$='.hidden_vehicleYear'],"+
                "input[id$='.vehicleEngine'],"+
                "input[id$='.hidden_vehicleEngine'],"+
                "input[id$='.productInfoImagePath'],"+
                "input[id='customerOrSupplierName'],"+
                ids ;

        var $classNameNode = $(node).parents(className);
        $classNameNode
            .find("input").not(exArray)
            .each(function () {
                this.value = "";
            });
        $classNameNode.find("[action-type=supplier-active-recommend]").remove();
        $classNameNode.find("span[id$='.inventoryAmountSpan']").html("0");
        $classNameNode.find("input[id$='.amountHid']").val("0");
        $classNameNode.find("span[id$='.reservedSpan']").html("0");
        $classNameNode.find("span[id$='.price_span']").html("");
        $classNameNode.find("span[id$='.unit_span']").html("");
        $classNameNode.find("span[id$='.total_span']").html("");
        $classNameNode.find("span[id$='.inventoryAmountSpan']").html("");
        $classNameNode.find("label[id$='.unitLbl']").html("");
        $classNameNode.find("label[id$='.totalLbl']").html("");

        $classNameNode.find("span[id$='.originSalesPriceSpan']").html("");
//        $classNameNode.find("span[id$='.saleReturnUnitSpan']").html("");

        var unitSpan = $classNameNode.find("span[id$='.unitSpan']")[0];
        if (unitSpan) {
            $(unitSpan).remove();
            $(node).parents(className).find("input[id$='.unit']").show();
        }
    };
}

//TODO 如果target包含指定的字符串，则返回空字符，否则返回本身

function commonStrUtil() {
    this.excludeSpecifiedStr = function (specifiedStr, target) {
        if (target == specifiedStr) return "";
        return target;
    }
}

//下拉建议元素定位

function suggestionPosition(node, left, top) {
    var dropDownList = "div_brand";
    if (arguments[3]) dropDownList = arguments[3];
    var offset = $(node).offset();
    $("#" + dropDownList).css({
        "display": "block",
        "position": "absolute",
        "left": offset.left + left + "px",
        "top": offset.top + top + "px"
    });
}


$(document).ready(function () {
    $(".J_checkVehicleBrandModel,#table_vehicle input[id$='.vehicleBrand'],#table_vehicle input[id$='.vehicleModel']").live("blur", function() {
        var $obj = $(this);
        if(APP_BCGOGO.Permission.Version.FourSShopVersion && G.isNotEmpty($obj.val())){
            var type="",msg="";
            if ($obj.attr("id").toLowerCase().indexOf("model")>-1) {
                type = "model";
                msg="请选择标准车型！";
            }
            if ($obj.attr("id").toLowerCase().indexOf("brand")>-1) {
                type = "brand";
                msg="请选择标准车辆品牌！";
            }
            APP_BCGOGO.Net.syncPost({
                url: "product.do?method=checkVehicleBrandModel",
                dataType: "json",
                data:{"type":type,"value":$obj.val()},
                success: function (result) {
                    if(!result){
//                        nsDialog.jAlert(msg,null,function(){
//                            $obj.val("");
//                        });
                        $obj.val("");
                    }
                },
                error: function () {
                    nsDialog.jAlert("数据异常，请刷新页面！");
                }
            });
        }
    });

    $('[action-type="repair-purchase"]').live("click",function(e){
        if (checkRepairPickingSwitchOn()) {
            e.preventDefault();
            nsDialog.jAlert("友情提示：本店已经开通维修领料流程，在本单据中不能直接采购商品！");
        }
    });
    $("[action-type=purchase-recommend-product]").live("click", function () {
        var productId = $(this).attr("product-id"),
            me=this;
        if (!G.isEmpty(productId)) {
            App.Net.syncPost({
                url: "autoAccessoryOnline.do?method=validatorPurchaseProduct",
                dataType: "json",
                data: {
                    productLocalInfoIds: productId,
                    amounts: 1
                },
                success: function (json) {
                    if (json.success) {
                        window.open("RFbuy.do?method=createPurchaseOrderOnlineByProduct&paramString=" + json.data.join(","));
                    } else {
                        nsDialog.jConfirm(json.msg, null, function (returnVal) {
                            if (returnVal) {
                                var supplerShopIds = json.data;
                                if (!G.isEmpty(supplerShopIds)) {
                                    App.Net.syncPost({
                                        url: "apply.do?method=applySupplierRelation",
                                        dataType: "json",
                                        data: {
                                            supplerShopId: supplerShopIds
                                        },
                                        success: function (json) {
                                            if (json.success) {
                                                nsDialog.jAlert("您提交的申请成功,请等待对方同意!");
                                            } else {
                                                nsDialog.jAlert(json.msg);
                                            }
                                        },
                                        error: function () {
                                            nsDialog.jAlert("数据异常!");
                                        }
                                    });
                                }
                                $('a[product-id=' + $(me).attr("target-id") + '][action-type=supplier-active-recommend]').hide();
                            }
                        });
                    }
                },
                error: function () {
                    nsDialog.jAlert("数据异常!");
                }
            });
        }
    });

    /**
     * "ctrl" keyboard-event handler, to show/hide "Goods Storage Price"
     */
    $(document).keydown(function (e) {
        var browser = G.Browser;

        // If mobile browser, disable "ctrl" keyboard-event
        // TODO to test
        if (browser.isIOS || browser.isAndroid || browser.isWphone) {
            return;
        }

        var keyCode = e.which;
        var tradePriceFlag = $("#tradePriceTag").val();
        var limitOrPriceSwitch = $("#limitOrPriceSwitch").val();
        if (keyCode == 17) {
            var $node;
            if (tradePriceFlag == "false" || (tradePriceFlag == "true" && limitOrPriceSwitch == "limit")) {
                $node = $(".span_purchase_price,.span_average_price,.inventory_average_price,.actual_Inventory_AveragePrice,#_minPurchasePrice,#_maxPurchasePrice");
            } else if (tradePriceFlag == "true" && limitOrPriceSwitch == "price") {
                $node = $(".span_purchase_price,.span_average_price,.inventory_average_price,.actual_Inventory_AveragePrice,#_minPurchasePrice,#_maxPurchasePrice");
            } else {
                $node = $(".span_purchase_price,.span_average_price,.span_trade_price,.inventory_average_price,.actual_Inventory_AveragePrice,#_minPurchasePrice,#_maxPurchasePrice");
            }
            var ip = $("#iframe_PopupBox")[0];      //在iframe 中点到遮盖层的时候需要做的操作
            if (ip) {
                var ipCss = $(ip).css("display");
                var contentDoc = ip.contentWindow.document;
                tradePriceFlag = $(contentDoc).contents().find("#tradePriceTag").eq(0).val();
                if (ipCss == "inline") {
                    if (tradePriceFlag == "false") {
                        $node = $(contentDoc).find(".span_purchase_price,.span_average_price");
                    } else {
                        $node = $(contentDoc).find(".span_purchase_price,.span_average_price,.span_trade_price");
                    }
                }
            }
            var cssStyle = $node.eq(0).css("display");
            // css "display"    block --> none,  none --> block
            cssStyle = (cssStyle == "none") ? "inline" : (cssStyle == "inline") ? "none" : cssStyle;
            $node.each(function (i) {
                $(this).css("display", cssStyle);
            });
        }
    });


    productValidate();
    //todo 以下代码已死，引以为戒。by qxy
//    var selectNum = -1;
//    var $domObject;
//    $(document).keydown(function (e) {
//        var keyName = G.keyNameFromEvent(e),
//            target = e.target;
//        $(target).blur(function () {
//            selectNum = -1;
//        });
//        //公共的向下向上 选择
//        if ($("#div_brand").css("display") == "block") {
//            var $foo = $("#Scroller-Container_id>a"),
//                size = $foo.size();
//            $foo.removeAttr("class");
//            if (keyName === "up") {
//                selectNum = selectNum <= 0 ? size - 1 : selectNum - 1;
//                if (selectNum == (size - 1)) {
//                    $domObject = $foo.last();
//                } else {
//                    $domObject = $domObject.prev();
//                }
//                $domObject.attr("class", "hover");
//            } else if (keyName === "down") {
//                selectNum = selectNum >= (size - 1) ? 0 : selectNum + 1;
//                if (selectNum == 0) {
//                    $domObject = $foo.first();
//                } else {
//                    $domObject = $domObject.next();
//                }
//                $domObject.attr("class", "hover");
//            }
//            if (keyName === "enter" && selectNum > -1) {
//                $("#selectItem" + (selectNum)).click();
//                selectNum = -1;
//            }
//        }
//    });
    //TODO 此处为了应对不同分辨率下，页面的偏移和错位问题
//    var screenwidth = screen.width;
//    $("body:first").css({
//        "overflow_y": "scroll",
//        "width": screenwidth,
//        "margin": "0 auto"
//    });


    //进销存,商品品目等属性绑定事件
    if (!isDisable()) {
        //去除库存盘点中text_style的绑定事件，JQuery live的bug导致.not不起作用，只能在原始选择器中加:not
        var productItemsSelector = "input[id$='.productName']:not(.text_style)" +
            ",input[id$='.brand']:not(.text_style)" +
            ",input[id$='.model']:not(.text_style)" +
            ",input[id$='.spec']:not(.text_style)" +
            ",input[id$='.vehicleModel']:not(.text_style)" +
            ",input[id$='.vehicleBrand']:not(.text_style)";

        $(productItemsSelector)
            .live("click", function (e) {
                var searchcomplete = App.Module.searchcomplete || null;
                if (searchcomplete && searchcomplete._relInst) {
                    if ($(this).parent().parent()[0] == $(searchcomplete._relInst).parent().parent()[0]) {
                        clearSearchWord($(this).parent().parent(), false);
                    } else {
                        clearSearchWord($(searchcomplete._relInst).parent().parent(), true);
                    }
                }

                domClick(this);
            })
            .live("keyup",function (event) {
                //TODO 为了修复 bug 在 searchOrderSuggestion() 和 searchVehliceSuggestion() 中增加了 $(node).attr("lastvalueforie", thisvalue);
                //TODO 但是建议 这个功能 重设功能放在 统一的地方， 现在 放在 keyup 处可能有 副作用， 此代码问题我来修复
//                console.log("this:")
//                console.log("\"" + $(this).val() + "\"");
//                console.log("last");
//                console.log("\"" + $(this).attr("lastValueForIe") + "\"" );

                if ($(this).val() != $(this).attr("lastValueForIe")) { //不能   使用"lastValue"
                    $(this).attr("lastValueForIe", $(this).val());
                    domKeyUp(this, event.which);
                }
            }).live("blur", function () {
                modify(this);
                //判断是否需要自动增加一行空行
                autoAddBlankRow(this);
            })
            .live("focus", function () {
                $(this).attr("lastValue", $(this).val());
                $(this).attr("lastValueForIe", $(this).val());
            });

        $("input[id$='.commodityCode']")
            .live("click", function (e) {
                var searchcomplete = App.Module.searchcomplete;
                if ($(this).parent().parent()[0] == $(searchcomplete._relInst).parent().parent()[0]) {
                    clearSearchWord($(this).parent().parent(), false);
                } else {
                    clearSearchWord($(searchcomplete._relInst).parent().parent(), true);
                }
                domClick(this);
            })
            .live("keyup",function (event) {
                var pos = getCursorPosition(this);
                var startLength = $(this).val().length;
                $(this).val(App.StringFilter.inputtingCommodityCodeFilter($(this).val()));
                var endLength = $(this).val().length;
                setCursorPosition(this, pos + (endLength - startLength));

                if ($(this).val() != $(this).attr("lastValueForIe")) { //不能   使用"lastValue"
                    $(this).attr("lastValueForIe", $(this).val());
                    domKeyUp(this, event.which);
                }

            }).live("blur", function () {
                var $dom = $(this);
                var idPrefix = $dom.attr("id").split(".")[0];
                setTimeout(function () {

                    if ( !$dom.attr("blurLock") && $dom.attr("lastvalue") != $dom.val()) {
                        commodityCodeBlur($dom);
                        autoAddBlankRow($dom);
                    }
                    $("#" + idPrefix + "\\.commodityCode").removeAttr("blurLock");
                }, 200);
            })
            .live("focus", function () {
                $(this).removeAttr("blurLock");
                $(this).attr("lastValue", $(this).val());
                $(this).attr("lastValueForIe", $(this).val());
            });

    }

    $(document).click(function (e) {
        var target = e.target;
        if (!target || !target.id || (target.type != "text" && target.id != "div_brand" && target.id != "div_brandvehiclelicenceNo")) {
            $("#div_brand").css("display", "none");
            $("#div_brandvehiclelicenceNo").css("display", "none");
        }
    });

    $("input[id='supplier'], input[id='customer'], input[id='contact']").bind("keyup", function () {
        $(this).val(App.StringFilter.stringSpaceFilter($(this).val()));
    });

    $(".j_checkStoreHouse").live("change", function (e) {
        batchSynProductInventoryAmount($(this).val());
        if(verifyProductThroughOrderVersion(getOrderType())){
            if(getOrderStatus()=="DISPATCH"){
                return;
            }
            if(!$(this).val()){
                nsDialog.jAlert("请选择仓库！");
                $(this).val($(this).attr("lastVal"));
                $(".j_checkStoreHouse").change();
                return;
            }
            if (getOrderType() == "RETURN" && G.isEmpty($("#supplierId").val())) {
                return;
            }
            $(this).attr("lastVal", $(this).val());
            batchSynSupplierInventory($(this).val());
            if (getOrderType() == "RETURN_ORDER") {
                borrowOrderReturnStyleAdjust();
            }
        }
    });
    initStorehouse();

    var operation = G.Util.getUrlParameter("operation");
    if (!G.isEmpty(operation)) {
        if (operation == 'REPEAL') {
            if ($("#statementAccountOrderId").val()) {
                nsDialog.jAlert("单据已冲账，不能作废！");
            } else {
                setTimeout(function () {
                    $("#nullifyBtn").click();
                }, 100);
            }

        } else if (operation == 'COPY') {
            setTimeout(function () {
                $("#copyInput").click();
            }, 100);
        } else if ( operation == "PRINT"){
            setTimeout(function () {
                $("#printBtn").click();
            }, 100);
        }
    }

    $("#repairPickingTab")
        .bind("mouseover", function (e) {
            $("#closeRepairPicking").css("display", "inline-block");
        })
        .bind("mouseleave", function (e) {
            $("#closeRepairPicking").hide();
        })
        .bind("click", function (event) {
//            event.preventDefault();
//            event.stopPropagation();
        });

    $("#closeRepairPicking").hover(
        function () {
            $(".tixing").show();
        },
        function () {
            $(".tixing").hide();
        }
    ).click(function (event) {
//            event.preventDefault();
            event.stopPropagation();
            var url = "admin.do?method=changeUserSwitch";
            $.ajax({
                type: "POST",
                url: url,
                data: {
                    scene: "REPAIR_PICKING",
                    status: "OFF"
                },
                cache: false,
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        nsDialog.jAlert("您已关闭维修领料流程！");
                    } else {
                        nsDialog.jAlert(result.msg);
                    }
                }
            });
        });

    if ($.isFunction(window.getOrderType) && (getOrderType() == "SALE" || getOrderType() == "REPAIR" )) {
        $("input[id$='price']").live("click", function (event) {
            var _priceDom = $(this);
            var productId = _priceDom.closest("tr").find("input[id$='productId']").val();
            var customerId = $("#customerId").val();
            if (G.isEmpty(productId) || G.isEmpty(customerId)) {
                return;
            }
            var hasLastPrice = false;
            App.Net.syncGet({
                url: "txn.do?method=getLatestCustomerPrice",
                data: {customerId: customerId, productId: productId},
                dataType: "json",
                success: function (json) {
                    if (json.success && json.data) {
                        _priceDom.data("lastSalePrice", json.data.itemPrice);
                        _priceDom.data("lastSalePriceUnit", json.data.unit);
                        hasLastPrice = true;
                    } else if (json.success && json.data == null) {
                        _priceDom.removeData("lastSalePrice");
                    }
                },
                error: function () {
                    G.error("数据异常！");
                }
            });

            if (!hasLastPrice) {
                return;
            }
            if (G.isEmpty(_priceDom.data("recommendedPrice"))) {
                App.Net.syncGet({
                    url: "searchInventoryIndex.do?method=ajaxInventorySearchIndex",
                    data: {productId: productId},
                    dataType: "json",
                    success: function (json) {
                        _priceDom.data("recommendedPrice", json.recommendedPrice);
                        _priceDom.data("recommendedPriceUnit", json.sellUnit);
                    }
                });
            }
            var uuid = G.generateUUID();
            var recommendPriceStr = (G.isEmpty(_priceDom.data("recommendedPrice")) ? 0 : _priceDom.data("recommendedPrice")) + "元" + (G.isEmpty(_priceDom.data("recommendedPriceUnit")) ? "" : ("/" + _priceDom.data("recommendedPriceUnit")));
            var recommendPriceVal = (G.isEmpty(_priceDom.data("recommendedPrice")) ? 0 : _priceDom.data("recommendedPrice")) + (G.isEmpty(_priceDom.data("recommendedPriceUnit")) ? "" : ("/" + _priceDom.data("recommendedPriceUnit")));

            var lastSalePriceStr = (G.isEmpty(_priceDom.data("lastSalePrice")) ? 0 : _priceDom.data("lastSalePrice")) + "元" + (G.isEmpty(_priceDom.data("lastSalePriceUnit")) ? "" : ("/" + _priceDom.data("lastSalePriceUnit")));
            var lastSalePriceVal = (G.isEmpty(_priceDom.data("lastSalePrice")) ? 0 : _priceDom.data("lastSalePrice")) + (G.isEmpty(_priceDom.data("lastSalePriceUnit")) ? "" : ("/" + _priceDom.data("lastSalePriceUnit")));
            var result = {
                data: [
                    {"id": 0, "label": "设定销售价: " + recommendPriceStr, "value": recommendPriceVal},
                    {"id": 1, "label": "上次销售价: " + lastSalePriceStr, "value": lastSalePriceVal}
                ],
                uuid: uuid
            };
            var droplist = App.Module.droplist;
            droplist.setUUID(uuid);
            setTimeout(function(){
                droplist.show({
                    "selector": $(event.currentTarget),
                    "isEditable": false,
                    "data": result,
                    "isDeletable": false,
                    "height": 50,
                    "onSelect": function (event, index, data, hook) {
                        if(priceChangeTimeout){
                            clearTimeout(priceChangeTimeout);
                        }
                        if (!G.isEmpty(data.value.split("/")[1])) {
                            if ($(hook).closest("tr").find("span[id$='unitSpan']").text() != data.value.split("/")[1]) {
                                $(hook).closest("tr").find("span[id$='unitSpan']").click();
                            }
                        }
                        $(hook).val(data.value.split("/")[0]);
                        droplist.hide();
                        $(hook).change();
                    }
                });
            }, 10);
        });
    }

    if ($.isFunction(window.getOrderType) && (getOrderType() == "REPAIR" || getOrderType() == "WASH_BEAUTY")) {
        $("#table_task input[id$='total'], #table_carWash input[id$='price']").live("click", function (event) {
            var _priceDom = $(this);
            var serviceId, serviceName;
            if (getOrderType() == "REPAIR") {
                serviceName = _priceDom.closest("tr").find("input[id$='service']").val();
                serviceId = _priceDom.closest("tr").find("input[id$='serviceId']").val();
            } else if (getOrderType() == "WASH_BEAUTY") {
                serviceName = _priceDom.closest("tr").find("select[id$='serviceId']").find("option:selected").text();
                serviceId = _priceDom.closest("tr").find("select[id$='serviceId']").val();
            }
            var customerId = $("#customerId").val();
            if (G.isEmpty(serviceId) || G.isEmpty(customerId)) {
                return;
            }
            var hasLastPrice = false;
            App.Net.syncGet({
                url: "txn.do?method=getLatestCustomerPrice",
                data: {customerId: customerId, serviceId: serviceId},
                dataType: "json",
                success: function (json) {
                    if (json.success && json.data) {
                        _priceDom.data("lastServicePrice", json.data.itemPrice);
                        hasLastPrice = true;
                    } else if (json.success && json.data == null) {
                        _priceDom.removeData("lastServicePrice");
                    }
                },
                error: function () {
                    G.error("数据异常！");
                }
            });

            if (!hasLastPrice) {
                return;
            }
            if (G.isEmpty(_priceDom.data("recommendedPrice"))) {
                App.Net.syncGet({
                    url: "txn.do?method=getServiceByServiceName",
                    data: {serviceName: serviceName},
                    dataType: "json",
                    success: function (json) {
                        if (json.length > 0) {
                            _priceDom.data("recommendedPrice", G.isEmpty(json[0].price) ? 0 : json[0].price);
                        }
                    }
                });
            }
            var uuid = G.generateUUID();
            var result = {
                data: [
                    {"id": 0, "label": "设定工时费: " + (G.isEmpty(_priceDom.data("recommendedPrice")) ? 0 : _priceDom.data("recommendedPrice")), "value": (G.isEmpty(_priceDom.data("recommendedPrice")) ? 0 : _priceDom.data("recommendedPrice"))},
                    {"id": 1, "label": "上次工时费: " + (G.isEmpty(_priceDom.data("lastServicePrice")) ? 0 : _priceDom.data("lastServicePrice")), "value": (G.isEmpty(_priceDom.data("lastServicePrice")) ? 0 : _priceDom.data("lastServicePrice"))}
                ],
                uuid: uuid
            };
            var droplist = App.Module.droplist;
            droplist.setUUID(uuid);
            droplist.show({
                "selector": $(event.currentTarget),
                "isEditable": false,
                "data": result,
                "isDeletable": false,
                "height": 50,
                "onSelect": function (event, index, data, hook) {
                    $(hook).val(data.value);
                    droplist.hide();
                    $(hook).change();
                }
            });
        });
    }

    $("#clickMore").toggle(
        function () {
            if(getOrderType()=="RETURN"){
                $(".elivate tr:gt(2)").show();
            }else{
                $(".elivate tr:gt(1)").show();
            }
            $("#clickMore").html("收拢");
            $("#clickMore").removeClass("down").addClass("up");
        },
        function () {
            if(getOrderType()=="RETURN"){
                $(".elivate tr:gt(2)").hide();
            }else{
                $(".elivate tr:gt(1)").hide();
            }
            $("#clickMore").html("详细");
            $("#clickMore").removeClass("up").addClass("down");
        }
    );



});

$().ready(function(){
    $(".price-input").live("keyup",function(){
        $(this).val(G.round($(this).val()));
    });
     $(".jRoundNumber").live("keyup",function(){
        $(this).val(G.rounding($(this).val(),0));
    });
});


function isDisable() {    // true 商品input 的下拉建议失效
    var orderType = $("#orderType").val();
    var status = $("#status").val();
    var id = $("#id").val();
    if (G.contains(orderType, [null, ""])) {
        return  true;
    }
    if (G.contains(orderType, ["clientInfo", "addClientInfo", "innerPicking", "innerReturn", "insuranceOrder"])) {
        return false;
    }
    if (orderType === "repairOrder" && G.contains(status, ["REPAIR_SETTLED", "REPAIR_REPEAL"])) {
        return true;
    }
    if (orderType === "purchaseOrder" && G.contains(status, ["PURCHASE_ORDER_WAITING", "PURCHASE_ORDER_DONE", "PURCHASE_ORDER_REPEAL"])) {
        return true;
    }
    if (orderType == "purchaseInventoryOrder" && G.contains(status, ["PURCHASE_INVENTORY_DONE", "PURCHASE_INVENTORY_REPEAL"])) {
        return true;
    }
    if (orderType == "goodsSaleOrder" && G.contains(status, ["SALE_DONE", "SALE_DEBT_DONE", "SALE_REPEAL"])) {
        return true;
    }
    if (orderType == "purchaseReturnOrder" && !(G.isEmpty(status) || status == "SELLER_PENDING")) { //todo && id != null && id !="" 退货单都无法操作by qxy 临时代码
        return true;
    }
    if (orderType == "salesReturnOrder" && (status == "SETTLED")) {
        return true;
    }
    return false;
}

function modify(node) {
    if (isDisable()) {
        return;
    }

    if (!$(node).attr("lastValue")) {
        $(node).attr("lastValue", "");
    }
    var lastValue = $(node).attr("lastValue");
    if (lastValue != null && lastValue == $(node).val()) {
        return;
    }
    //todo 后续添加，修改代码注意！代码需要放在此标注后面，这里modify的意图是执行chang操作，如果需要执行bulr，放在之前。by qxy
    $(node).attr("lastValue", $(node).val());
    //    选择车型后自动将车辆品牌填充
    if ($(node).attr("id").endWith("vehicleModel") || $(node).attr("id").endWith("vehicleBrand")) {
        searchVehicleOfModel(node);
    }
    var thisObjId = node.id.split(".")[1];
    if (thisObjId != "productName" && thisObjId != "brand" && thisObjId != "spec" && thisObjId != "model" && thisObjId != "vehicleModel" && thisObjId != "vehicleBrand"){
      return;
    }
    var parent1 = $(node).parents("#table_productNo")[0];
    var parent2 = $(node).parents("#table_productNo_2")[0];
    if (parent1 || parent2) {
        var idprefix = $(node).attr("id");
        idprefix = idprefix.substring(0, idprefix.indexOf("."));
        var productId = $("#" + idprefix + "\\.productId").val();
        if (productId != null && productId != "") {
            new clearItemUtil().clear(node);
            if ($.isFunction(window.setTotal)) {
                setTotal();//每个模块都有自定义的
            }
        }else{
            if ($("#" + idprefix + "\\.amount").val() * 1 > 0) {
                $("#" + idprefix + "\\.amount").val("0");
                if ($.isFunction(window.setTotal)) {
                    setTotal();
                }
            }
        }
    }
}


//TODO 用于文本框的单击下拉
function domClick(thisObj) {
    if (verifyProductThroughOrderVersion(getOrderType())) {
        if (!isSelectStorehouseForThroughOrder()) {
            return;
        }
        if (getOrderType() == "RETURN" && G.isEmpty($("#supplierId").val())) {
            nsDialog.jAlert("请选择供应商！");
            return;
        }
    }
    domActiveDistribution(thisObj, null);
}

/**
 *  当只有一个仓库时，这个仓库自动选为缺省的仓库
 */
function initStorehouse() {
    var storehouseSize = 0;
    var lastVal = "";
    $(".j_checkStoreHouse option").each(function () {
        if (!G.isEmpty($(this).val())) {
            storehouseSize++;
            lastVal = $(this).val();
        }
    });
    if (storehouseSize == 1 && G.isEmpty($(".j_checkStoreHouse").eq(0).val())) {
        $(".j_checkStoreHouse").val(lastVal);
        $(".j_checkStoreHouse").change();
    }

}
function isSelectStorehouseForThroughOrder(){

    if(verifyProductThroughOrderVersion(getOrderType())&&G.isEmpty($(".j_checkStoreHouse").val())){
        disFlag=true;
        var storeDiv="<div>";
        if(getOrderType()=="RETURN"&&G.isEmpty($("#supplierId").val())){
//             storeDiv+="<div style='font-weight: bold;'>请选择供应商</div><div style='margin-top: 10px;margin-left: 30px'><span>供应商：<input class='supplierSuggestion' /></span></div>";
        }
        storeDiv += "<div style='font-weight: bold;'>请选择仓库</div><div style='margin-top: 10px;margin-left: 30px'><span>仓库：<select>";
        storeDiv += $(".j_checkStoreHouse").html();
        storeDiv += "</select></span></div>";
        storeDiv += "</div>";
        $(storeDiv).dialog({
            resizable: true,
            title: "提示",
            height: 150,
            width: 270,
            modal: true,
            closeOnEscape: false,
            buttons: {
                "确定": function () {
                    $(this).dialog("close");
                    $(".j_checkStoreHouse").val($(this).find("select").val());
                    $(".j_checkStoreHouse").attr("lastVal", $(this).find("select").val());
                    $(".j_checkStoreHouse").change();
                },
                "取消": function () {
                    $(this).dialog("close");
                }
            }
        });
        return false;
    } else {
        return true;
    }

}

//commodityCodeBlur
/**
 * 商品编码失去焦点逻辑
 * 1，当前item是老商品或者新商品，修改成已经 “存在”的商品编码，提示：“替换当前商品”/“取消”。关掉当前的dialog当作“取消”
 * 2，当前item是老商品，修改成已经 “不存在” 的商品编码，提示：“修改商品编码”/“取消”。关掉当前dialog当作取消
 * 3，当前item是老商品，修改“空的” 的商品编码，提示：“您是否要修改本商品的商品编号？”“确定”/“取消”。关掉当前dialog当作取消
 * 4，入库单，失去焦点相同商品不做数量+1
 * 5，豪华版也不加1
 * @param node
 */

function commodityCodeBlur(node) {
    if ($(node).attr("getCommodityCodeFlag") == "lock") {
        return;
    }
    $(node).val(App.StringFilter.commodityCodeFilter($(node).val()));
    var domId = $(node).attr("id");
    var idPrefix = domId.split(".")[0];
    var rePlaceFlag = true;
    if ($(node).val()) {
        var ajaxUrl = "stockSearch.do?method=ajaxToGetProductByCommodityCode";
        var ajaxData = {
            commodityCode: $(node).val()
        };
        if (App.Permission.Version.StoreHouse) {
            if (getOrderType() == "ALLOCATE_RECORD" && $("#outStorehouseId")) {
                ajaxData["storehouseId"] = $("#outStorehouseId").val();
            } else if ($("#storehouseId")) {
                ajaxData["storehouseId"] = $("#storehouseId").val();
            }
        }

        App.Net.syncAjax({
            cache: false,
            url: ajaxUrl,
            data: ajaxData,
            dataType: "json",
            success: function (json) {
                var addAmount = false;
                if (json && json[0] && json[0].productIdStr) {
                    if (getOrderType() == "INVENTORY") {
                        addAmount = false;
                    } else {
                        $("input[id$='.productId']").each(function () {
                            if ($(this).val() == json[0].productIdStr) {
                                var productIdDomIdPrefix = $(this).attr("id").split(".")[0];
                                if(!verifyProductThroughOrderVersion(getOrderType())){
                                    $("#" + productIdDomIdPrefix + "\\.amount").val($("#" + productIdDomIdPrefix + "\\.amount").val() * 1 + 1);
                                }
                                //豪华版不加1，其余处理逻辑都一样
                                addAmount = true;

                                // >>>>>> oh ， 这段代码在 initorderlr 中已有 ，但是为什么我们将他们抽出呢， 上帝救救这段代码吧， 上帝说： 拯救当从自我做起， 加油代码骑士!
                                if (json && json[0] && !G.isEmpty(json[0])) {
                                    $("#" + productIdDomIdPrefix + "\\.originSalesPrice").val(G.normalize(json[0].recommendedPrice));
                                    $("#" + productIdDomIdPrefix + "\\.originSalesPriceSpan").html(G.normalize(json[0].recommendedPrice));
                                }
                                // <<<<<<
                                if (productIdDomIdPrefix != idPrefix) {
//                                    $("#" + idPrefix + "\\.commodityCode").val("");
                                    $("#" + idPrefix + "\\.commodityCode").val($("#" + idPrefix + "\\.commodityCode").attr("lastValue"));
                                }
                                return false;
                            }
                        });
                    }
                } else {
                    if (getOrderType() == "ALLOCATE_RECORD" || getOrderType() == "INNER_PICKING" || getOrderType() == "INNER_RETURN") {
                        nsDialog.jAlert("当前商品编号不存在,请重新输入!");
                        $("#" + idPrefix + "\\.commodityCode").val($("#" + idPrefix + "\\.commodityCode").attr("lastValue"));
                        return;
                    } else {
                        if ($("#" + idPrefix + "\\.productId").val()) {
                            var msg = "<strong>友情提示：</strong>库存中不存在商品编号为【" + $(node).val() + "】的商品!<br><br>";
                            msg += "您是否要把此商品编号修改为【" + $(node).val() + "】？";
                            $("#commodityCode_dialog_msg").html(msg);
                            $("#commodityCode_dialog").dialog({
                                resizable: false,
                                title: "友情提示！",
                                height: 150,
                                width: 360,
                                modal: true,
                                closeOnEscape: false,
                                buttons: {
                                    "确定": function () {
                                        rePlaceFlag = false;
                                        $(this).dialog("close");
                                    },
                                    "取消": function () {
                                        rePlaceFlag = true;
                                        $("#" + idPrefix + "\\.commodityCode").val($("#" + idPrefix + "\\.commodityCode").attr("lastValue"));
                                        $(this).dialog("close");
                                    }
                                },
                                close: function () {
                                    App.Verifier.isShopIdCorrect = true;
                                    if (rePlaceFlag) {
                                        $("#" + idPrefix + "\\.commodityCode").val($("#" + idPrefix + "\\.commodityCode").attr("lastValue"));
                                    }
                                }
                            });
                        }
                    }
                }
                if (addAmount) {
                    return;
                }
                if (!$("#" + idPrefix + "\\.productId").val() && json) {
                    if (json[0] && json[0].id) {
                        initOrderIr(domId, $("#orderType").val(), json[0]);
                        getSupplierInventory($("#" + idPrefix + "\\.productId"));
                        activeRecommendSupplierTip($("#" + idPrefix + "\\.inventoryAmount").parent().parent(), getOrderType());
                    }
                } else if (json && json.length > 0) {
                    var initTrFlag = true;
                    var productId = $("#" + idPrefix + "\\.productId").val();
                    for (var i = 0, len = json.length; i < len; i++) {
                        if (json[i].productIdStr == productId) {
                            initTrFlag = false;
                            break;
                        }
                    }
                    if (initTrFlag) {
                        App.Verifier.isShopIdCorrect = false;
                        var msg = "<strong>友情提示：</strong>已存在编码为【" + json[0].commodityCode + "】的商品!<br>商品信息为：";
                        if (json[0].productName) {
                            msg += json[0].productName;
                        }
                        if (json[0].productBrand) {
                            msg += "&nbsp;" + json[0].productBrand;
                        }
                        if (json[0].productSpec) {
                            msg += "&nbsp;" + json[0].productSpec;
                        }
                        if (json[0].productModel) {
                            msg += "&nbsp;" + json[0].productModel;
                        }
                        msg += "<br><br>您是否要用此商品替换单据中商品?"
                        $("#commodityCode_dialog_msg").html(msg);
                        $("#commodityCode_dialog").dialog({
                            resizable: false,
                            title: "友情提示！",
                            height: 150,
                            width: 288,
                            modal: true,
                            closeOnEscape: false,
                            buttons: {
                                "确定": function () {
                                    rePlaceFlag = false;
                                    initOrderIr(domId, $("#orderType").val(), json[0]);
                                    getSupplierInventory($("#" + idPrefix + "\\.productId"));
                                    activeRecommendSupplierTip($("#" + idPrefix + "\\.inventoryAmount").parent().parent(), getOrderType());
                                    $(this).dialog("close");
                                },
                                "取消": function () {
                                    rePlaceFlag = true;
                                    $("#" + idPrefix + "\\.commodityCode").val($("#" + idPrefix + "\\.commodityCode").attr("lastValue"));
                                    $(this).dialog("close");
                                }
                            },
                            close: function () {
                                App.Verifier.isShopIdCorrect = true;
                                if (rePlaceFlag) {
                                    $("#" + idPrefix + "\\.commodityCode").val($("#" + idPrefix + "\\.commodityCode").attr("lastValue"));
                                }
                            }
                        });
                    }
                }
            },
            error: function () {
                G.error("AJAX 请求失败!");
            }
        });
        // end App.Net.syncAjax()
    }else{
        if (!G.Lang.isEmpty($("#" + idPrefix + "\\.productId").val())) {

            //下面是productId为空的情况
            var subIdNameArr = [
                ".commodityCode",
                ".productName",
                ".brand",
                ".spec",
                ".model",
                ".vehicleBrand",
                ".vehicleModel",
                ".vehicleYear",
                ".vehicleEngine"
            ];
            var dataMapNameArr = [
                "commodityCode",
                "productName",
                "productBrand",
                "productSpec",
                "productModel",
                "productVehicleBrand",
                "productVehicleModel"
            ];
            var prepareData = {};
            for (var i = 0, len = subIdNameArr.length; i < len; i++) {
                if (!$("#" + idPrefix + "\\.productName").val()) {
                    return;
                }
                prepareData[dataMapNameArr[i]] = $("#" + idPrefix + "\\" + subIdNameArr[i]).val();
            }


            var storehouseId = "";
            if ($(".j_checkStoreHouse")) {
                storehouseId = $(".j_checkStoreHouse").val();
            }
            prepareData["storehouseId"] = storehouseId;
            APP_BCGOGO.Net.syncPost({
                url: "searchInventoryIndex.do?method=ajaxInventorySearchIndex",
                data: prepareData,
                dataType: "json",
                cache: false,
                success: function (data) {
                    if (G.isUndefined(data) || G.isNull(data.productId)) {
                        var msg = "<strong>友情提示：</strong>您是否要修改本商品的商品编号？";
                        $("#commodityCode_dialog_msg").html(msg);
                        $("#commodityCode_dialog").dialog({
                            resizable: false,
                            title: "友情提示！",
                            height: 150,
                            width: 288,
                            modal: true,
                            closeOnEscape: false,
                            buttons: {
                                "确定": function () {
                                    rePlaceFlag = false;
                                    $(this).dialog("close");
                                },
                                "取消": function () {
                                    rePlaceFlag = true;
                                    $("#" + idPrefix + "\\.commodityCode").val($("#" + idPrefix + "\\.commodityCode").attr("lastValue"));
                                    $(this).dialog("close");
                                }
                            },
                            close: function () {
                                App.Verifier.isShopIdCorrect = true;
                                if (rePlaceFlag) {
                                    $("#" + idPrefix + "\\.commodityCode").val($("#" + idPrefix + "\\.commodityCode").attr("lastValue"));
                                }
                            }
                        });
                    } else {
                        var productId = $("#" + idPrefix + "\\.productId").val();
                        if (data.productIdStr != productId) {
                            App.Verifier.isShopIdCorrect = false;
                            var msg = "<strong>友情提示：</strong>已存在商品信息为：【";
                            if (data.productName) {
                                msg += data.productName;
                            }
                            if (data.productBrand) {
                                msg += "&nbsp;" + data.productBrand;
                            }
                            if (data.productSpec) {
                                msg += "&nbsp;" + data.productSpec;
                            }
                            if (data.productModel) {
                                msg += "&nbsp;" + data.productModel;
                            }
                            msg +=  "】的商品!"
                            msg += "<br><br>您是否要用此商品替换单据中商品?"
                            $("#commodityCode_dialog_msg").html(msg);
                            $("#commodityCode_dialog").dialog({
                                resizable: false,
                                title: "友情提示！",
                                height: 150,
                                width: 288,
                                modal: true,
                                closeOnEscape: false,
                                buttons: {
                                    "确定": function () {
                                        rePlaceFlag = false;
                                        initOrderIr(domId, $("#orderType").val(), data);
                                        getSupplierInventory($("#" + idPrefix + "\\.productId"));
                                        activeRecommendSupplierTip($("#" + idPrefix + "\\.inventoryAmount").parent().parent(), getOrderType());
                                        $(this).dialog("close");
                                    },
                                    "取消": function () {
                                        rePlaceFlag = true;
                                        $("#" + idPrefix + "\\.commodityCode").val($("#" + idPrefix + "\\.commodityCode").attr("lastValue"));
                                        $(this).dialog("close");
                                    }
                                },
                                close: function () {
                                    App.Verifier.isShopIdCorrect = true;
                                    if (rePlaceFlag) {
                                        $("#" + idPrefix + "\\.commodityCode").val($("#" + idPrefix + "\\.commodityCode").attr("lastValue"));
                                    }
                                }
                            });
                        }
                    }

//                    if (verifyProductThroughOrderVersion(getOrderType())) {
//                        getSupplierInventory($("#" + idPrefix + "\\.productId"));
//                    }
//                    activeRecommendSupplierTip($("#" + idPrefix + "\\.inventoryAmount").parent().parent(".item "), getOrderType());
                },
                error: function () {
                    alert("网络异常，请重新尝试!");
                }
            });
        }
    }
    //判断是否需要自动增加一行空行
    if (G.Lang.endWith($(node).attr("id"), ".commodityCode") && !G.isEmpty($(node).val())) {
        var orderType = $("#orderType").val();
        var flag=isEmptyOrderItem($("#table_productNo .item:last"));
        if(!flag){
            if (orderType == "purchaseOrder") {
                purchaseOrderAdd();
            } else if (orderType == "repairOrder") {
                addNewRow(0);
                isShowAddButton2();
            } else if (orderType == "purchaseReturnOrder") {
                returnStorageOrderAdd();
            } else if (orderType == "salesReturnOrder") {
                salesReturnAdd();
            } else if (orderType == "allocateRecord") {
                addItemRow();
            } else if (orderType == "preBuyOrder") {
                preBuyOrderAddItemRow();
            }  else {
                inventoryOrderAdd();
            }
        }
    }
    if ($.isFunction(window.setTotal)) {
        setTotal();//每个模块都有自定义的
    }
}
function isEmptyOrderItem($item){
    var flag=false;
    if(G.isEmpty($item)){
        return true;
    }
    var idPrefix = '';
    if($($item).find("[id$='.productName']").attr("id") != undefined) {
        idPrefix = $($item).find("[id$='.productName']").attr("id").split(".")[0];
    }

    var productId = $.trim($("#" + idPrefix + "\\.productId").val());
    var productName = $.trim($("#" + idPrefix + "\\.productName").val());
    var brand = $.trim($("#" + idPrefix + "\\.brand").val());
    var spec = $.trim($("#" + idPrefix + "\\.spec").val());
    var model = $.trim($("#" + idPrefix + "\\.model").val());
    var inventoryAmount = $.trim($("#" + idPrefix + "\\.inventoryAmount").val()); //todo ?
    if (productName == '' && brand == '' && spec == '' && model == '' && inventoryAmount == 0 ) {
        flag=true;
    }
    return flag;
}
function getProductInfoByCommodityCode(eventKeyCode) {
    $(eventKeyCode).val(App.StringFilter.commodityCodeFilter($(eventKeyCode).val()));
    var domId = $(eventKeyCode).attr("id");
    if ($(eventKeyCode).val() && $(eventKeyCode).val() != $(eventKeyCode).attr("lastValue")) {
        $(eventKeyCode).attr("getCommodityCodeFlag", "lock");
        var ajaxUrl = "stockSearch.do?method=ajaxToGetProductByCommodityCode";
        var ajaxData = {
            commodityCode: $(eventKeyCode).val()
        };
        if (App.Permission.Version.StoreHouse) {
            if (getOrderType() == "ALLOCATE_RECORD" && $("#outStorehouseId")) {
                ajaxData["storehouseId"] = $("#outStorehouseId").val();
            } else if ($("#storehouseId")) {
                ajaxData["storehouseId"] = $("#storehouseId").val();
            }
        }

        App.Net.syncAjax({
            cache: false,
            url: ajaxUrl,
            data: ajaxData,
            dataType: "json",
            success: function (json) {
                var idPrefix = domId.split(".")[0];
                var idSuffix = domId.split(".")[1];
                var addAmount = false;
                if (json && json[0] && json[0].productIdStr) {
                    if (getOrderType() == "INVENTORY") {
                        addAmount = false;
                    } else {
                        $("input[id$='.productId']").each(function () {
                            if ($(this).val() == json[0].productIdStr) {
                                var productIdDomIdPrefix = $(this).attr("id").split(".")[0];
                                if(!verifyProductThroughOrderVersion(getOrderType())){
                                    $("#" + productIdDomIdPrefix + "\\.amount").val($("#" + productIdDomIdPrefix + "\\.amount").val() * 1 + 1);
                                }
                                //豪华版不加1，其余处理逻辑都一样
                                addAmount = true;
                                if (productIdDomIdPrefix != idPrefix) {
                                    $("#" + idPrefix + "\\.commodityCode").val("");
                                }
                                return false;
                            }
                        });
                    }
                }
                if (addAmount) {
                    if ($.isFunction(window.setTotal)) {
                        setTotal();//每个模块都有自定义的
                    }
                    return;
                }

                $(eventKeyCode).removeAttr("getCommodityCodeFlag");
                if (json && json[0] && json[0].id) {
                    initOrderIr(domId, $("#orderType").val(), json[0]);
                    activeRecommendSupplierTip($("#" + idPrefix + "\\.productName").parent().parent(), getOrderType());
                }
                getSupplierInventory($("#" + idPrefix + "\\.productId"));
            },
            error: function () {
                $(eventKeyCode).removeAttr("getCommodityCodeFlag");
            }
        });
        // end App.Net.syncAjax()
    }
}
var vehicleArray = new Array();
//TODO 当文本框中有键盘输入时，下拉提示

function domKeyUp(thisObj, keyCode) {
    if ($("#iframe_PopupBox").css("display") == "block") return false;
    if (isDisable()) {
        return;
    }
    domActiveDistribution(thisObj, keyCode);
}


function domActiveDistribution(thisObj, keyCode) {
    var invoiceCommon = App.Module.wjl.invoiceCommon;
    var pos = getCursorPosition(thisObj);
    var idPrefix = thisObj.id.split(".")[0];
    var idSuffix = thisObj.id.split(".")[1];
    var domrows = parseInt(idPrefix.substring(8, idPrefix.length));
    if ($(thisObj).attr("id").endWith("vehicleModel") || $(thisObj).attr("id").endWith("vehicleBrand")) {
        searchVehicleOfModel(thisObj);
    }
    //TODO 不允许输入反斜杠和空格
    invoiceCommon.excludeSpaceSlash(idPrefix);
    var populateResult = invoiceCommon.populateSearchParams(idPrefix, idSuffix);
    var positionNum = populateResult.positionNum;
    var valueObject = populateResult.valueObject;
    if (!keyCode) {
        changeVehicle(thisObj, domrows, idPrefix, idSuffix, positionNum);
    }
    // TODO 待测试 ， 当按下按键为 up,down,left,right 时， 不做后续逻辑 ， zhen.pan 这样可以增加对键盘操作的支持
    var keyName = G.keyNameFromKeyCode(keyCode + "");
    if (G.contains(keyName, ["up", "down", "left", "right"])) {
        return;
    }
    if($(thisObj).attr("pagetype")=="customerVehicle"){
         var pageType=$("#pageType").val();
      var searchType="";
      if(pageType=="applySupplierList"||pageType=="applySupplierIndex"){
              searchType="standard";
      }
        var searchValue = thisObj.value.replace(/[\ |\\]/g, "");
        $(thisObj).attr("lastvalueforie", searchValue);
        var dropList = App.Module.droplist;
        dropList.setUUID(G.generateUUID());
        var ajaxData = {
            searchWord: searchValue,
            searchField: positionNum,
            uuid: dropList.getUUID(),
            vehicleBrand: positionNum == "product_vehicle_brand" ? "" : valueObject.vehicleBrandValue,
            vehicleModel: positionNum == "product_vehicle_model" ? "" : valueObject.vehicleModelValue,
            searchType:searchType
        };
        var ajaxUrl = "product.do?method=searchVehicleSuggestionForGoodsBuy";
        App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            if(!G.isEmpty(result.data[0])){
                G.completer({
                        'domObject':thisObj,
                        'keycode':keyCode,
                        'title':result.data[0].label}
                );
            }
            dropList.show({
                "selector": $(thisObj),
                "data": result,
                "onSelect": function (event, index, data) {
                    $(thisObj).val(data.label);
                    $(thisObj).css({"color": "#000000"});
                    dropList.hide();
                }
            });
        });
    }else{
        if(isVehicleSuggestion(positionNum)){
            searchVehicleSuggestionForGoodsBuy(thisObj, positionNum, valueObject.vehicleBrandValue, valueObject.vehicleModelValue, valueObject.vehicleYearValue, valueObject.vehicleEngineValue, domrows + 1, keyCode);
        }else {
            searchProductSuggestion(thisObj, keyCode, positionNum,
                valueObject.commodityCodeValue, valueObject.productValue, valueObject.brandValue, valueObject.specValue, valueObject.modelValue,valueObject.vehicleBrandValue,valueObject.vehicleModelValue);
        }
    }
    setCursorPosition(thisObj, pos);
}

function isVehicleSuggestion(searchField){
   return searchField== "year" || searchField=="engine"||
        getOrderType()!="SALE"&&getOrderType()!="SALE_RETURN"&&(searchField=="product_vehicle_brand"||searchField=="product_vehicle_model");
}


function changeVehicle(thisObj, domrows, idPrefix, idSuffix, positionNum) {
    if (G.contains(vehicleArray[domrows], [null, undefined]) && G.contains(idSuffix, ["vehicleBrand", "vehicleModel", "vehicleYear"])) {
        vehicleArray[domrows] = new Array(thisObj.value);
    }
}

function getIncludeBasic() {
    if (App.Permission.Version.IgnorVerifierInventory) {
        return true;
    }

    var orderType = $("#orderType").val();
    return !orderType || !G.contains(orderType, ["goodsSaleOrder", "purchaseReturnOrder", "salesReturnOrder", "repairOrder", "innerPicking", "innerReturn"]);
}

function searchProductSuggestion(node, eventKeyCode, searchField, commodityCodeValue, productValue, brandValue, specValue, modelValue,vehicleBrandValue,vehicleModelValue) {
    var searchValue = node.value.replace(/[\ |\\]/g, "");
    $(node).attr("lastvalueforie", searchValue);

    var searchcomplete = App.Module.searchcomplete;
    searchcomplete.lastSearchWord = searchValue;
    var orderType = getOrderType();
    var arrayFieldTitle = [];
    if(G.contains(orderType, ["INVENTORY", "REPAIR", "SALE"])) {
        arrayFieldTitle = [
            {
                "name":"commodityInfo",
                "title":"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;商 品 信 息&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;",
                "charLength":30
                //            ,
                //            "bEnableHidden":true
            },
            {
                "name" : "product_vehicle_brand,product_vehicle_model",
                "title" : "车辆品牌/车型",
                "charLength": 20
            }
        ];


        if(APP_BCGOGO.Permission.Version.StoreHouse) {
            arrayFieldTitle.push({
                "name": "inventoryNum,singleInventoryNum",
                "title": "总库存量/分库存量",
                "charLength": "12"
            });
        }else {
            arrayFieldTitle.push({
                "name": "inventoryNum",
                "title": "总库存量",
                "charLength": "6"
            });
        }
        if(APP_BCGOGO.PersonalizedConfiguration.StorageBinTag) {
            arrayFieldTitle.push({
                "name": "storageBin",
                "title": "货位"
            });
        }


            arrayFieldTitle.push({
                "name": "inventoryAveragePrice",
                "title": "入库均价",
                "charLength": "6",
                "bEnableHidden":true
            });

        if(APP_BCGOGO.PersonalizedConfiguration.TradePriceTag) {
            arrayFieldTitle.push({
                "name": "tradePrice,recommendedPrice",
                "title": "批发价/零售价",
                "charLength": "13"
            });
        } else {
            arrayFieldTitle.push({
                "name": "recommendedPrice",
                "title": "零售价",
                "charLength": "6"
            });
        }
    } else {
        arrayFieldTitle = [
            {
                "name": "commodity_code",
                "title": "商品编码"
            },
            {
                "name": "product_name,product_brand",
                "title": "品名 / 品牌",
                "charLength": "18"
            },
            {
                "name": "product_spec",
                "title": "规格"
            },
            {
                "name": "product_model",
                "title": "型号"
            },
            {
                "name": "product_vehicle_brand",
                "title": "车辆品牌"
            },
            {
                "name": "product_vehicle_model",
                "title": "车型"
            }

        ];

        if (orderType != "PURCHASE") {
            arrayFieldTitle.push({
                "name": "inventoryNum",
                "title": "库存",
                "charLength": "6"
            });
        }

        if (orderType == "PURCHASE") {
            arrayFieldTitle.push({
                "name": "inventoryNum",
                "title": "库存",
                "charLength": "6"
            });
            arrayFieldTitle.push({
                "name": "purchasePrice",
                "title": "入库价",
                "charLength": "6"
            });
        } else if (G.contains(orderType, ["INVENTORY", "RETURN"])) {
            arrayFieldTitle.push({
                "name": "purchasePrice",
                "title": "入库价",
                "charLength": "6"
            });
        } else if (G.contains(orderType, ["REPAIR", "SALE"])) {
            arrayFieldTitle.push({
                "name": "recommendedPrice",
                "title": "设定销售价",
                "charLength": "6"
            });
        } else if (orderType == "INVENTORY_CHECK") {
            arrayFieldTitle.push({
                "name": "inventoryAveragePrice",
                "title": "库存均价",
                "charLength": "6"
            });
        } else if (orderType == "ALLOCATE_RECORD" || orderType == "INNER_PICKING" || orderType == "INNER_RETURN") {
            arrayFieldTitle.push({
                "name": "inventoryAveragePrice",
                "title": "成本",
                "charLength": "6"
            });
        }
    }

    //以下单据只显示选中的仓库中入过库的库存商品
    var showCurrentStorehouseOrders = ["REPAIR", "SALE", "RETURN", "ALLOCATE_RECORD", "INNER_PICKING", "BORROW_ORDER"];
    var showAllStorehouseProducts = true;

    var storehouseId = $(".j_checkStoreHouse").val();
    if($.inArray(getOrderType(), showCurrentStorehouseOrders) != -1){
        showAllStorehouseProducts = false;
    }

    searchcomplete.changeThemes(searchcomplete.themes.FIX_WIDTH, arrayFieldTitle);
    var searchcompleteThemes = searchcomplete.themes.FIX_WIDTH;

    searchcomplete.changeThemes(searchcompleteThemes, arrayFieldTitle);

    searchcomplete.myClickTimes = 0;
    searchcomplete.hide();
    searchcomplete.hide("autocomplete");
    searchcomplete.hide("detailsList");
    var uuid = G.generateUUID();
    searchcomplete.setUUID(uuid);
    searchcomplete.moveFollow({
        node: node
    });
    var ajaxData = {
        //        searchFields:"product_name,product_brand,product_spec,product_model",
        orderType: orderType,
        includeBasic: getIncludeBasic(),
        searchWord: searchValue,
        searchField: searchField,
        uuid: uuid,
        supplierName: $("#supplier").val(),
        commodityCode: searchField == 'commodity_code' ? '' : commodityCodeValue,
        productName: searchField == 'product_info' ? '' : productValue,
        productBrand: searchField == 'product_brand' ? '' : brandValue,
        productSpec: searchField == 'product_spec' ? '' : specValue,
        productModel: searchField == 'product_model' ? '' : modelValue,
        vehicleBrand: searchField == 'product_vehicle_brand' ? '' : vehicleBrandValue,
        vehicleModel: searchField == 'product_vehicle_model' ? '' : vehicleModelValue,
        storehouseId: storehouseId,
        showAllStorehouseProducts : showAllStorehouseProducts,
        start: searchcomplete.getPageIndex() * searchcomplete.getPageSize(),
        rows: searchcomplete.getPageSize()
    };
    var ajaxUrl = "product.do?method=getProductSuggestionAndHistory";
    //如果是键盘输入
    if (eventKeyCode){
        LazySearcher.lazySearch(ajaxUrl, ajaxData, function (json) {
            //默认匹配第一个值  自动补全  高亮
            ///不接受 backspace/上/下/左/右键
            if (json && json.dropDown && json.dropDown.data.length > 0 && $(node)[0] == $(document.activeElement)[0]) {
                var inputtingTimerId = 0;
                clearTimeout(inputtingTimerId);
                if (!G.contains(G.keyNameFromKeyCode(eventKeyCode), ["left", "up", "right", "down", "enter", "backspace"])) {
                    inputtingTimerId = setTimeout(function () {
                        if (searchField == "product_info") {
                            searchField = "product_name"
                        }
                        App.Module.highlightcomplete.complete({
                            "selector": $(node),
                            "value": json.dropDown.data[0].details[searchField]
                        });
                    }, 300);
                }
            }

            searchcomplete.show();
            searchcomplete.show("autocomplete");
            searchcomplete.show("detailsList");
            searchcomplete.draw(json);
        });
    } else {
        App.Net.syncPost({
            url: ajaxUrl,
            data: ajaxData,
            dataType: "json",
            success: function (json) {
                searchcomplete.show();
                searchcomplete.show("autocomplete");
                searchcomplete.show("detailsList");
                searchcomplete.draw(json);
            }
        });
    }
}


function searchVehicleSuggestionForGoodsBuy(domObject, searchField, brandValue, modelValue, yearValue, engineValue, trcount, eventKeyCode) { //车辆信息查询
    var searchValue = domObject.value.replace(/[\ |\\]/g, "");
    $(domObject).attr("lastvalueforie", searchValue);
    var ajaxUrl = "product.do?method=searchVehicleSuggestionForGoodsBuy";
    var ajaxData = {
        searchWord: searchValue,
        searchField: searchField,
        vehicleBrand: searchField == "brand" ? "" : brandValue,
        vehicleModel: searchField == "model" ? "" : modelValue,
        vehicleYear: searchField == "year" ? "" : yearValue,
        vehicleEngine: searchField == "engine" ? "" : engineValue
    };
    LazySearcher.lazySearch(ajaxUrl, ajaxData, function (jsonStr) {
        var filterWord = ['<A>', '<B>', '<C>', '<D>', '<E>', '<F>', '<G>', '<H>', '<I>', '<J>', '<K>', '<L>', '<M>', '<N>', '<O>', '<P>', '<Q>', '<R>', '<S>', '<T>', '<U>', '<V>', '<W>', '<X>', '<Y>', '<Z>'];
        var title = jsonStr[0];
        if (arrayUtil.contains(filterWord, title)) {
            title = jsonStr[1];
        }
        if (!G.isEmpty(title)) {
            G.completer({
                'domObject': domObject,
                'keycode': eventKeyCode,
                'title': title
            });
        }
        var brandValue = G.Lang.normalize($("#itemDTOs" + (trcount) + "\\.vehicleBrand").val());
        var domTitleType = "GoodsBuy";
        OrderDropDownList.vehicleBrand(domObject, searchField, jsonStr, trcount - 1,brandValue,domTitleType);
    });
}

function checkEmptyRow($tr) {
    var propertys = ["productName", "brand", "spec", "model", "vehicleBrand", "vehicleModel"];
    var itemInfo = "";
    for (var i = 0, len = propertys.length; i < len; i++) {
        itemInfo += $tr.find("input[id$='." + propertys[i] + "']").val();
    }
    return G.isEmpty(itemInfo);
}

//Connected with bcgogo-scanning.js
$(document).ready(function () {
    if (App.Module.scanning) {
        //Source: "keyboard" | "card" | "barcode".
        //CardId: card number.
        App.Module.scanning.sourceDetect(function (source, targetId, interval) {
            G.info(interval);
            if (source == "card" && App.Permission.VehicleConstruction.Base) { //施工单 刷卡权限控制
                G.info("这是刷卡机！");
                window.top.location = "washBeauty.do?method=getCustomerInfoByMemberNo&memberNo=" + targetId;
            } else if (source == "barcode") {
                // 在 iframe 弹出时 不做操作， 但是如下的判断方法真 urgly， 得想办法更换用 class 来判断。。。
                var $mask = $("#mask");
                if (G.isInIframe() || $mask[0] && $mask.css("display") !== "none" && $mask.height() > 2 && $mask.width() > 2) {
                    return;
                }

                // TODO 扫描枪功能， 待测试回归 , BCSHOP 5394
                // 1) 明确 ， 扫描所得的条码到底是干什么用的， 是 商品编码吗？  V
                // 2) 在单据页面， 增加 标记 ，我们约定， 标记都加在 <html> 标签上， 形如 <html pagetype="order">  V
                // 3) 然后 ，在非单据页面做跳转功能，  V
                // 4) 修复原先扫描枪逻辑上的问题，  并同时从时间间隔和字符串长度上来判断是 什么设备输入   V
                // 5) 在单据页面做自动增加条目的功能    V
                G.info("这是扫描枪！")
                // 扫描枪我们过滤换行符 "\n", "\r", "\n\r"
                targetId = targetId.replace(/[\n\r]+/g, "");

                var $body = $(window.top.document.body),
                    $originOrder = $body.find("input[id='originOrderId']");
                if ($body.attr("ordertype") && (!$originOrder[0] || G.isEmpty($originOrder.val()))) {

                    //当没有空行时新增空行
                    var $lastCommodityCodeNode = $("[id$='commodityCode']").last();
                    var _isBlankRow = function () {
                        var $lastRow = $lastCommodityCodeNode.closest('tr');
                        var $allTextbox = $("[id$='commodityCode']").last().closest('tr').find('input[type="text"]:visible').filter('[readonly!="readonly"]');
                        $allTextbox.each(function (i) {
                            if ($allTextbox.eq(i).val().length > 0) {
                                return false;
                            }
                        });
                        return true;
                    };

                    if (_isBlankRow) {
                        autoAddBlankRow($lastCommodityCodeNode);
                    }

                    var $lastCommodityCodeNode = $("[id$='commodityCode']").last();

                    // 最后一行为空的话 ，自动加一行
                    var $lastTr = $lastCommodityCodeNode.closest("tr"),
                        joinedValue = "";
                    $lastTr.find("input[type='text']").each(function (index, value) {
                        joinedValue += G.normalize($(this).val());
                    });
                    if ((G.isEmpty(joinedValue) === false && $.trim(joinedValue).search(/[^0\.]/g) !== -1) || !$lastTr[0]) {
                        // TODO 增加一行
                        var orderType = $("#orderType").val();
                        if (orderType == "purchaseOrder") {
                            purchaseOrderAdd();
                        } else if (orderType == "repairOrder") {
                            addNewRow(0);
                            isShowAddButton2();
                        } else if (orderType == "purchaseReturnOrder") {
                            returnStorageOrderAdd();
                        } else if (orderType == "salesReturnOrder") {
                            salesReturnAdd();
                        } else {
                            inventoryOrderAdd();
                        }

                        // $lastCommodityCodeNode 重新 set 为 last node, 因为之前 node 发生了变化
                        $lastCommodityCodeNode = $lastTr[0] ? $lastTr.next().find("[id$='commodityCode']") : $("[id$='commodityCode']").last();
                    }

                    $lastCommodityCodeNode.val(targetId);
                    commodityCodeBlur($lastCommodityCodeNode);
                    G.info("扫描枪， 自动带出数据至最后一行， 并在最后一行之后， 再增加一空行");

                } else if ($body.attr("ordertype")) {
                    // 如果 $originOrder.val() 有值， 那么表明它是一个 非自由单据， 此时我们弹出一个新建一个窗口， 并执行 非单据页面的 扫描逻辑
                    App.Net.syncPost({
                        url: "stockSearch.do?method=ajaxToGetProductByCommodityCode",
                        dataType: "json",
                        data: {
                            "commodityCode": targetId
                        },
                        success: function (json) {
                            var srvData = json ? json[0] : null;
                            if (srvData && srvData.hasOwnProperty("amount") && srvData.amount > 0) {
                                G.info("有库存");
                                window.top.open("sale.do?method=getProducts&type=txn&productIds=" + srvData.productIdStr, "_blank");
                            } else {
                                G.info("没库存");
                                window.top.open("storage.do?method=getProducts&type=txn&scanning=" + source + "&productIds=" + targetId, "_blank");
                            }
                        },
                        error: function () {
                            G.error("扫描枪触发的库存查询错误!");
                            App.Module.noticeDialog.jAlert("网络错误， 可能是登陆超时， 请重新登陆尝试", "警告");
                        }
                    });
                } else {
                    // 非单据页面
                    App.Net.syncPost({
                        url: "stockSearch.do?method=ajaxToGetProductByCommodityCode",
                        dataType: "json",
                        data: {
                            "commodityCode": targetId
                        },
                        success: function (json) {
                            var srvData = json ? json[0] : null;
                            if (srvData && srvData.hasOwnProperty("amount") && srvData.amount > 0) {
                                G.info("有库存");
                                window.top.location = "sale.do?method=getProducts&type=txn&productIds=" + srvData.productIdStr;
                            } else {
                                G.info("没库存");
                                window.top.location = "storage.do?method=getProducts&type=txn&scanning=" + source + "&productIds=" + targetId;
                            }
                        },
                        error: function () {
                            G.error("扫描枪触发的库存查询错误!");
                            App.Module.noticeDialog.jAlert("警告", "网络错误， 可能是登陆超时， 请重新登陆尝试");
                        }
                    });
                }
            }
            // --end if
        });
    }
    /******顶部搜索框 begin******/
    if ($("#_searchInputButton") && $("#_searchInputButton")[0]) {
        $("#_searchInputButton").bind('click', function (event) {
            if ($(this).attr("disabled") == undefined || !$(this).attr("disabled")) {
                //根据所选标签执行函数.
                var _textVal = $.trim($('#_searchInputText').val());
                var _methodId = $('#_searchMethod').val();
                var _initVal = $('#_searchInputText').attr('initialValue');
                saveSearchBoxDataToWebStorage(_textVal == _initVal ? '' : _textVal, _methodId);
                //menu
                App.Menu.Function.doMenu(defaultStorage.getItem(storageKey.MenuUid));
                switch (_methodId) {
                    case 'licenceNo':
                        //文本框内是否有填写(若为初始属性也视为没填写).
                        if (_textVal && _textVal != _initVal) {
                            //去掉空字符和"-".
                            _textVal = _textVal.replace(/[\-|\s]/g, '');
                            //判断车牌号是否合法.
                            if ((_textVal.length == 5 || _textVal.length == 6) && App.Validator.stringIsCharacter(_textVal)) { //前缀
                                var r = App.Net.syncGet({url: "product.do?method=userLicenseNo", dataType: "json"});
                                if (r === null) {
                                    return;
                                } else {
                                    var locaono = r[0].localCarNo;
                                    $("#_searchInputText").val((locaono + _textVal).toString().toUpperCase());
                                }
                                _textVal = $("#_searchInputText").val();
                                saveSearchBoxDataToWebStorage(_textVal == _initVal ? '' : _textVal, _methodId);
                                openOrAssign('txn.do?method=getRepairOrderByVehicleNumber&task=maintain&vehicleNumber=' + (_textVal == _initVal ? '' : _textVal));
                            } else {
                                if (!App.Validator.stringIsLicensePlateNumber(_textVal)) {
                                    nsDialog.jAlert("输入的车牌号码不符合规范，请检查！");
                                } else {
                                    _textVal = $("#_searchInputText").val();
                                    openOrAssign('txn.do?method=getRepairOrderByVehicleNumber&task=maintain&vehicleNumber=' + (_textVal == _initVal ? '' : _textVal));
                                }
                            }
                        } else {
                            openOrAssign('customer.do?method=carindex');
                        }
                        break;

                    case 'customerOrSupplier':
                        getByKeyWord(_textVal == _initVal ? '' : _textVal);
                        break;
                    case 'accessoryName':
                        searchProductInfo($('#_searchInputText'));
                        break;
                    case 'supplierOnline':
                        openOrAssign('apply.do?method=getApplySuppliersPage&supplierName=' + (_textVal == _initVal ? '' : _textVal));
                        break;

                    case 'accessoryOnline':
                        openOrAssign('autoAccessoryOnline.do?method=toCommodityQuotations&searchWord=' + (_textVal == _initVal ? '' : _textVal));
                        break;

                    case 'customerInventoryOnline':
                        openOrAssign('autoAccessoryOnline.do?method=toRelatedCustomerStock&searchWord=' + (_textVal == _initVal ? '' : _textVal));
                        break;

                    case 'customerOnline':
                        openOrAssign('apply.do?method=getApplyCustomersPage&customerName=' + (_textVal == _initVal ? '' : _textVal));
                        break;
                }
            }
        });
    }
    else {
        G.warning("Can't find #_searchInputButton");
    }

    //判断是否存在该对象
    if ($("#_searchInputText") && $("#_searchInputText")[0]) {
        //keyup只出下拉建议  回车搜索功能用  keypress  捕捉    避免汉字输入法输入英文问题（ie  firefox  下）
        $("#_searchInputText").bind('keypress', function (event) {
            //根据所选标签执行函数.
            var _textVal = $.trim($('#_searchInputText').val());
            var _methodId = $('#_searchMethod').val();
            var _initVal = $('#_searchInputText').attr('initialValue');

            var eventKeyCode = event.which || event.keyCode;
            var keyName = G.keyNameFromEvent(event);
            switch (_methodId) {
                case 'licenceNo':
                    //大小写转换
                    $(event.target).val($(event.target).val().toUpperCase().replace(/[^A-Z\d\u4e00-\u9fa5\-_\(\)]+/g, ""));
                    if (keyName === "enter" && (!$("#_searchInputButton").attr("disabled") || $("#_searchInputButton").attr("disabled") == undefined)) {
                        //判断车牌号是否合法.
                        if ((_textVal.length == 5 || _textVal.length == 6) && App.Validator.stringIsCharacter(_textVal)) { //前缀
                            var r = App.Net.syncGet({url: "product.do?method=userLicenseNo", dataType: "json"});
                            if (r === null) {
                                return;
                            } else {
                                var locaono = r[0].localCarNo;
                                _textVal = (locaono + _textVal).toString().toUpperCase();
                                $(event.target).val(_textVal);
                            }
                            saveSearchBoxDataToWebStorage(_textVal == _initVal ? '' : _textVal, _methodId);
                            openOrAssign('txn.do?method=getRepairOrderByVehicleNumber&task=maintain&vehicleNumber=' + (_textVal == _initVal ? '' : _textVal));
                        } else {
                            var _initVal = $(event.target).attr('initialValue');
                            if (_textVal && _textVal != _initVal) {
                                if (!App.Validator.stringIsLicensePlateNumber(_textVal)) {
                                    nsDialog.jAlert("输入的车牌号码不符合规范，请检查！");
                                } else {
                                    saveSearchBoxDataToWebStorage(_textVal == _initVal ? '' : _textVal, _methodId);
                                    openOrAssign('txn.do?method=getRepairOrderByVehicleNumber&task=maintain&vehicleNumber=' + (_textVal == _initVal ? '' : _textVal));
                                }
                            } else {
                                nsDialog.jAlert("请输入车牌号码后再搜索！");
                            }

                        }
                    }
                    break;

                case 'customerOrSupplier':
                    //去除空白字符串和特殊符号
                    var reg = new RegExp("[`~!@#$^&*()=|\\\\{\\}%_\\+\\-\"':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g");
                    var _textVal = _textVal.replace(/(^\s*)|(\s*$)/g, "").replace(reg, "");
                    $(event.target).val(_textVal);
                    if (keyName === "enter" && (!$("#_searchInputButton").attr("disabled") || $("#_searchInputButton").attr("disabled") == undefined)) {
                        saveSearchBoxDataToWebStorage(_textVal == _initVal ? '' : _textVal, _methodId);
                        getByKeyWord(_textVal == _initVal ? '' : _textVal);
                    }
                    break;

                case 'accessoryName':
                    if (keyName === "enter" && (!$("#_searchInputButton").attr("disabled") || $("#_searchInputButton").attr("disabled") == undefined)) {
                        saveSearchBoxDataToWebStorage(_textVal == _initVal ? '' : _textVal, _methodId);
                        searchProductInfo($(event.target));
                    }
                    break;

                case 'supplierOnline':
                    if (keyName === "enter" && (!$("#_searchInputButton").attr("disabled") || $("#_searchInputButton").attr("disabled") == undefined)) {
                        saveSearchBoxDataToWebStorage(_textVal == _initVal ? '' : _textVal, _methodId);
                        openOrAssign('apply.do?method=getApplySuppliersPage&supplierName=' + (_textVal == _initVal ? '' : _textVal));
                    }
                    break;

                case 'accessoryOnline':
                    if (keyName === "enter" && (!$("#_searchInputButton").attr("disabled") || $("#_searchInputButton").attr("disabled") == undefined)) {
                        saveSearchBoxDataToWebStorage(_textVal == _initVal ? '' : _textVal, _methodId);
                        openOrAssign('autoAccessoryOnline.do?method=toCommodityQuotations&searchWord=' + (_textVal == _initVal ? '' : _textVal));
                    }
                    break;

                case 'customerInventoryOnline':
                    if (keyName === "enter" && (!$("#_searchInputButton").attr("disabled") || $("#_searchInputButton").attr("disabled") == undefined)) {
                        saveSearchBoxDataToWebStorage(_textVal == _initVal ? '' : _textVal, _methodId);
                        openOrAssign('autoAccessoryOnline.do?method=toRelatedCustomerStock&searchWord=' + (_textVal == _initVal ? '' : _textVal));
                    }
                    break;

                case 'customerOnline':
                    if (keyName === "enter" && (!$("#_searchInputButton").attr("disabled") || $("#_searchInputButton").attr("disabled") == undefined)) {
                        saveSearchBoxDataToWebStorage(_textVal == _initVal ? '' : _textVal, _methodId);
                        openOrAssign('apply.do?method=getApplyCustomersPage&customerName=' + (_textVal == _initVal ? '' : _textVal));
                    }
                    break;
            }
        });
        //keyup只出下拉建议  回车搜索功能用  keypress  捕捉    避免汉字输入法输入英文问题（ie  firefox  下）
        $("#_searchInputText").bind('keyup', function (event) {
            //根据所选标签执行函数.
            var _textVal = $.trim($('#_searchInputText').val());
            var _methodId = $('#_searchMethod').val();
            var _initVal = $('#_searchInputText').attr('initialValue');
            var eventKeyCode = event.which || event.keyCode;
            var keyName = G.keyNameFromEvent(event);
            switch (_methodId) {
                case 'licenceNo':
                    //大小写转换
                    $(event.target).val($(event.target).val().toUpperCase().replace(/[^A-Z\d\u4e00-\u9fa5\-_\(\)]+/g, ""));
                    if (keyName.search(/left|up|right|down/g) == -1) {
                        getVehicleLicenceNoSuggestionInHead(this);
                    }
                    break;

                case 'customerOrSupplier':
                    //去除空白字符串和特殊符号
                    var reg = new RegExp("[`~!@#$^&*()=|\\\\{\\}%_\\+\\-\"':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g");
                    var _textVal = _textVal.replace(/(^\s*)|(\s*$)/g, "").replace(reg, "");
                    $(event.target).val(_textVal);
                    if (keyName.search(/left|up|right|down/g) == -1) {
                        getCustomerOrSupplierSuggestionInHead($(this));
                    }
                    break;

                case 'accessoryName':
                    if (keyName.search(/left|up|right|down/g) == -1) {
                        var ajaxUrl = "product.do?method=getProductSuggestion";
                        var url = "stockSearch.do?method=getStockSearch";
                        getProductSuggestionInHead($(this), ajaxUrl, url);
                    }
                    break;

                case 'supplierOnline':
                    if (keyName.search(/left|up|right|down/g) == -1) {
                        getCustomerOrSupplierOnlineSuggestionInHead($(this));
                    }
                    break;

                case 'accessoryOnline':
                    if (keyName.search(/left|up|right|down/g) == -1) {
                        var ajaxUrl = "product.do?method=searchWholeSalerProductInfo";
                        var url = "autoAccessoryOnline.do?method=toCommodityQuotations";
                        getProductSuggestionInHead($(this), ajaxUrl, url);
                    }
                    break;

                case 'customerInventoryOnline':
                    if (keyName.search(/left|up|right|down/g) == -1) {
                        var ajaxUrl = "product.do?method=getProductSuggestion";
                        var url = "autoAccessoryOnline.do?method=toRelatedCustomerStock";
                        getProductSuggestionInHead($(this), ajaxUrl, url);
                    }
                    break;

                case 'customerOnline':
                    if (keyName.search(/left|up|right|down/g) == -1) {
                        getCustomerOrSupplierOnlineSuggestionInHead($(this));
                    }
                    break;
            }
        });
        $("#_searchInputText").bind('click', function () {
            var _methodId = $('#_searchMethod').val();
            switch (_methodId) {
                case 'licenceNo':
                    getVehicleLicenceNoSuggestionInHead($(this));
                    break;
                case 'customerOrSupplier':
                    getCustomerOrSupplierSuggestionInHead($(this));
                    break;
                case 'accessoryName':
                    var ajaxUrl = "product.do?method=getProductSuggestion";
                    var url = "stockSearch.do?method=getStockSearch";
                    getProductSuggestionInHead($(this), ajaxUrl, url);
                    break;
                case 'supplierOnline':
                    getCustomerOrSupplierOnlineSuggestionInHead($(this));
                    break;
                case 'accessoryOnline':
                    var ajaxUrl = "product.do?method=searchWholeSalerProductInfo";
                    var url = "autoAccessoryOnline.do?method=toCommodityQuotations";
                    getProductSuggestionInHead($(this), ajaxUrl, url);
                    break;
                case 'customerInventoryOnline':
                    var ajaxUrl = "product.do?method=getProductSuggestion";
                    var url = "autoAccessoryOnline.do?method=toRelatedCustomerStock";
                    getProductSuggestionInHead($(this), ajaxUrl, url);
                    break;
                case 'customerOnline':
                    getCustomerOrSupplierOnlineSuggestionInHead($(this));
                    break;
            }
        });
    } else {
        G.warning("Can't find _searchInputText");
    }

    if ($("#_searchNameBar") && $("#_searchNameBar")[0]) {
        $("#_searchNameBar")
            .bind("mouseenter", function (event) {
                event.stopImmediatePropagation();

                var $searchNameList = $("#_searchNameList");
                $("#_searchNameList").show();
            })
            .bind("mouseleave", function (event) {
                var $this = $(event.currentTarget),
                    pageY = event.pageY,
                    targetOffsetY = $this.offset().top + $this.height();

                if (pageY < targetOffsetY) {
                    $("#_searchNameList").hide();
                }
            });

        $("#_searchNameList")
            .bind("mouseleave", function (event) {
                var $this = $(event.currentTarget),
                    pageY = event.pageY,
                    targetOffsetY = $this.offset().top;

                if (targetOffsetY < pageY) {
                    $this.hide();
                }
            })
    }
    function initSearchBoxJsonData() {
        var searchBoxJson = {};
        if (App.Permission.VehicleConstruction.Construct.Base) {
            searchBoxJson["searchMethod"] = "licenceNo";
        } else if (App.Permission.Txn.InventoryManage.StockSearch.Base) {
            searchBoxJson["searchMethod"] = "accessoryName";
        } else if (App.Permission.CustomerManager.Base) {
            searchBoxJson["searchMethod"] = "customerOrSupplier";
        } else if (App.Permission.AutoAccessoryOnline.RelatedCustomerStock) {
            searchBoxJson["searchMethod"] = "accessoryOnline";
        } else if (App.Permission.AutoAccessoryOnline.CommodityQuotations) {
            searchBoxJson["searchMethod"] = "customerInventoryOnline";
        } else if (App.Permission.AutoAccessoryOnline.ApplyCustomer) {
            searchBoxJson["searchMethod"] = "customerOnline";
        } else if (App.Permission.AutoAccessoryOnline.ApplySupplier) {
            searchBoxJson["searchMethod"] = "supplierOnline";
        }
        return searchBoxJson;
    }

    //搜索in head
    if ($("#_searchInputText") && $("#_searchInputText")[0]) {//判断是否存在该对象
        $('#_searchInputText').placeHolder();
        var searchBoxJson = JSON.parse(defaultStorage.getItem(storageKey.SearchBox));
        if (G.isEmpty(searchBoxJson)) {
            searchBoxJson = initSearchBoxJsonData();
            saveSearchBoxDataToWebStorage("", searchBoxJson["searchMethod"]);
        } else {
            //去除没有权限的 搜索选项   防止老数据干扰
            if ((!App.Permission.VehicleConstruction.Construct.Base && searchBoxJson["searchMethod"] == "licenceNo")
                || (!App.Permission.Txn.InventoryManage.StockSearch.Base && searchBoxJson["searchMethod"] == "accessoryName")
                || (!App.Permission.CustomerManager.Base && searchBoxJson["searchMethod"] == "customerOrSupplier")
                || (!App.Permission.AutoAccessoryOnline.CommodityQuotations && searchBoxJson["searchMethod"] == "accessoryOnline")
                || (!App.Permission.AutoAccessoryOnline.RelatedCustomerStock && searchBoxJson["searchMethod"] == "customerInventoryOnline")
                || (!App.Permission.AutoAccessoryOnline.ApplyCustomer && searchBoxJson["searchMethod"] == "customerOnline")
                || (!App.Permission.AutoAccessoryOnline.ApplySupplier && searchBoxJson["searchMethod"] == "supplierOnline")
                || G.isEmpty(searchBoxJson["searchMethod"])) {
                defaultStorage.removeItem(storageKey.SearchBox);
                searchBoxJson = initSearchBoxJsonData();
                saveSearchBoxDataToWebStorage("", searchBoxJson["searchMethod"]);
            }
        }
        changeSearchTab(searchBoxJson["searchKeyWord"], searchBoxJson["searchMethod"]);

        $('#_searchInputText').bind("blur", function () {
            saveSearchBoxDataToWebStorage($(this).val() == $(this).attr('initialValue') ? '' : $(this).val(), $("#_searchMethod").val());
        });

    }
    if ($(".J-selectOption") && $(".J-selectOption")[0]) {
        $(".J-selectOption").bind("click", function () {
            var searchBoxJson = JSON.parse(defaultStorage.getItem(storageKey.SearchBox));
            changeSearchTab(searchBoxJson["searchKeyWord"], $(this).attr("searchMethod"));
            defaultStorage.setItem(storageKey.MenuUid, $(this).attr("menu-name"));
            $("#_searchNameList").hide();
        });
    }

    function changeSearchTab(_searchKeyWord, _methodId) {
        $('#_searchInputText').val(G.Lang.normalize(_searchKeyWord));
        $("#_searchMethod").val(_methodId);
        saveSearchBoxDataToWebStorage($('#_searchInputText').val() == $('#_searchInputText').attr('initialValue') ? '' : $('#_searchInputText').val(), $("#_searchMethod").val());
        switch (_methodId) {
            case 'licenceNo':
                $("#_searchName").text("车牌号");
                if ("#_searchDescription") $("#_searchDescription").text("可新增施工、查询服务车辆及施工状态");
                if (App.Permission.VehicleConstruction.Construct.Base) {
                    $('#_searchInputText').attr('initialValue', '请输入车牌号');
                    $('#_searchInputText').removeAttr("disabled");
                    $('#_searchInputButton').removeAttr("disabled");
                } else {
                    $('#_searchInputText').val("");
                    $('#_searchInputText').attr('initialValue', '您没有此权限！');
                    $('#_searchInputText').attr('disabled', 'disabled');
                    $('#_searchInputButton').attr('disabled', 'disabled');
                }
                break;

            case 'customerOrSupplier':
                $("#_searchName").text("客户/供应商");
                if ("#_searchDescription") $("#_searchDescription").text("可查询您本店的客户与供应商资料");
                if (App.Permission.CustomerManager.Base) {
                    $('#_searchInputText').attr('initialValue', '请输入单位/联系人/手机号/会员号');
                    $('#_searchInputText').removeAttr("disabled");
                    $('#_searchInputButton').removeAttr("disabled");
                } else {
                    $('#_searchInputText').val("");
                    $('#_searchInputText').attr('initialValue', '您没有此权限！');
                    $('#_searchInputText').attr('disabled', 'disabled');
                    $('#_searchInputButton').attr('disabled', 'disabled');
                }
                break;

            case 'accessoryName':
                $("#_searchName").text("商品库存");
                if ("#_searchDescription") $("#_searchDescription").text("可查询您本店的库存商品");
                if (App.Permission.Txn.InventoryManage.StockSearch.Base) {
                    $('#_searchInputText').attr("initialValue", "品名/品牌/规格/型号/适用车辆");
                    $('#_searchInputText').removeAttr("disabled");
                    $('#_searchInputButton').removeAttr("disabled");
                } else {
                    $('#_searchInputText').val("");
                    $('#_searchInputText').attr("initialValue", "您没有此权限！");
                    $('#_searchInputText').attr("disabled", "disabled");
                    $('#_searchInputButton').attr("disabled", "disabled");
                }
                break;
            case 'supplierOnline':
                $("#_searchName").text("推荐供应商");
                if ("#_searchDescription") $("#_searchDescription").text("可查询使用本软件的供应商店铺");
                if (App.Permission.AutoAccessoryOnline.ApplySupplier) {
                    $('#_searchInputText').attr("initialValue", "请输入供应商名称");
                    $('#_searchInputText').removeAttr("disabled");
                    $('#_searchInputButton').removeAttr("disabled");
                } else {
                    $('#_searchInputText').val("");
                    $('#_searchInputText').attr("initialValue", "您没有此权限！");
                    $('#_searchInputText').attr("disabled", "disabled");
                    $('#_searchInputButton').attr("disabled", "disabled");
                }
                break;

            case 'accessoryOnline':
                $("#_searchName").text("配件报价");
                if ("#_searchDescription") $("#_searchDescription").text("可查询配件供应商上架商品的报价信息");
                if (App.Permission.AutoAccessoryOnline.CommodityQuotations) {
                    $('#_searchInputText').attr("initialValue", "品名/品牌/规格/型号/适用车辆");
                    $('#_searchInputText').removeAttr("disabled");
                    $('#_searchInputButton').removeAttr("disabled");
                } else {
                    $('#_searchInputText').val("");
                    $('#_searchInputText').attr("initialValue", "您没有此权限！");
                    $('#_searchInputText').attr("disabled", "disabled");
                    $('#_searchInputButton').attr("disabled", "disabled");
                }
                break;

            case 'customerInventoryOnline':
                $("#_searchName").text("客户库存");
                if ("#_searchDescription") $("#_searchDescription").text("可查询您销售给关联客户的商品库存量");
                if (App.Permission.AutoAccessoryOnline.RelatedCustomerStock) {
                    $('#_searchInputText').attr("initialValue", "品名/品牌/规格/型号/适用车辆");
                    $('#_searchInputText').removeAttr("disabled");
                    $('#_searchInputButton').removeAttr("disabled");
                } else {
                    $('#_searchInputText').val("");
                    $('#_searchInputText').attr("initialValue", "您没有此权限！");
                    $('#_searchInputText').attr("disabled", "disabled");
                    $('#_searchInputButton').attr("disabled", "disabled");
                }
                break;

            case 'customerOnline':
                $("#_searchName").text("推荐客户");
                if ("#_searchDescription") $("#_searchDescription").text("可查询使用本软件的客户店铺");
                if (App.Permission.AutoAccessoryOnline.ApplyCustomer) {
                    $('#_searchInputText').attr("initialValue", "请输入客户名称");
                    $('#_searchInputText').removeAttr("disabled");
                    $('#_searchInputButton').removeAttr("disabled");
                } else {
                    $('#_searchInputText').val("");
                    $('#_searchInputText').attr("initialValue", "您没有此权限！");
                    $('#_searchInputText').attr("disabled", "disabled");
                    $('#_searchInputButton').attr("disabled", "disabled");
                }
                break;
        }
        $('#_searchInputText').placeHolder('reset');

    }
});
//检查单据是否为空单据

function checkNumberEmpty() {
    var isEmpty = true;
    $(".checkNumberEmpty").each(function () {
        if (!G.isEmpty(this.value) && this.value > 0) {
            G.debug(this.id + ":" + this.value);
            isEmpty = false;
            return false;
        }
    });
    return isEmpty;
}

function checkStringEmpty() {
    var isEmpty = true;
    $(".checkStringEmpty").each(function () {
        if (!G.isEmpty($.trim(this.value))) {
            G.debug(this.id + ":" + this.value);
            isEmpty = false;
            return false;
        }
    });
    return isEmpty;
}

function checkStringChanged() {
    var isChanged = false;
    $(".checkStringChanged").each(function () {
        if ($(this).val() != $(this).attr("init" + this.id.toLowerCase() + "value")) {
            isChanged = true;
            return false;
        }
    });
    return isChanged;
}

function checkSelectChanged() {
    var isChanged = false;
    $(".checkSelectChanged").each(function () {
        var test1 = $(this).val();
        var test2 = $(this).find("option:first").val();
        if ($(this).val() != $(this).find("option:first").val()) {
            isChanged = true;
            return false;
        }
    });
    return isChanged;
}
function newOtherOrder(url) {
    url = url.replace("undefined", "");
    if (openNewOrderPage()) {
        window.open(url, "_blank");
    } else {
        openOrAssign(url);
    }
}
function openNewOrderPage() {
    return !checkNumberEmpty() || !checkStringEmpty() || checkStringChanged() || checkSelectChanged();
}

/**
 * 检测采购，入库，销售，施工，退货输入框长度是否过长
 */

function isLegalTxnDataLength() {
    var ilegal_str_length = 100;
    var ilegal_num_length = 21;
    var flag = true;
    $(".checkStringEmpty").each(function () {
        if ($(this).attr("id") == "memo" && $(this).val().length > 450) {
            alert("备注信息过长，请修改！");
            flag = false;
            return false;
        } else if ($(this).attr("id") != "memo" && $(this).val().length > ilegal_str_length) {
            msg = $(this).val().substring(0, 20);
            alert("您输入的" + msg + "... 信息过长，请修改！");
            flag = false;
            return false;
        }
    });
    $(".checkNumberEmpty").each(function () {
        if ($(this).val().length > ilegal_num_length) {
            msg = $(this).val().substring(0, 20);
            alert("您输入的" + msg + "... 信息过长，请修改！");
            flag = false;
            return false;
        }
    });
    return flag;
}

/**
 * 商品编号添加一行商品信息
 * @param domId  需要初始化商品所在行的dom对象的Id
 * @param json  InventorySearchIndexDTO 格式
 */

function initOrderIr(domId, orderType, data) {
    if (data && domId) {
        var idPrefix = domId.split(".")[0];
        idPrefix = "#" + idPrefix;
        if ($(idPrefix + "\\.productName")) {
            new clearItemUtil().clearByFlag($(idPrefix + "\\.productName")[0], true);
        }
        var inventoryAveragePrice=G.rounding(data.inventoryAveragePrice,2);
        var recommendedPrice=G.rounding(data.recommendedPrice,2);
        $(idPrefix + "\\.id").val("");
        $(idPrefix + "\\.commodityCode").val(G.normalize(data.commodityCode));
        $(idPrefix + "\\.productId").val(G.normalize(data.productIdStr));
        $(idPrefix + "\\.productName").val(G.normalize(data.productName)).attr("lastValue", G.normalize(data.productName)).attr("lastvalueforie", G.normalize(data.productName));
        $(idPrefix + "\\.brand").val(G.normalize(data.productBrand)).attr("lastValue", G.normalize(data.productBrand)).attr("lastvalueforie", G.normalize(data.productBrand));
        $(idPrefix + "\\.model").val(G.normalize(data.productModel)).attr("lastValue", G.normalize(data.productModel)).attr("lastvalueforie", G.normalize(data.productModel));
        $(idPrefix + "\\.spec").val(G.normalize(data.productSpec)).attr("lastValue", G.normalize(data.productSpec)).attr("lastvalueforie", G.normalize(data.productSpec));
        $(idPrefix + "\\.vehicleBrand").val(G.normalize(data.brand));
        $(idPrefix + "\\.vehicleModel").val(G.normalize(data.model));
        $(idPrefix + "\\.productType").val(G.normalize(data.productVehicleStatus));
        $(idPrefix + "\\.productVehicleStatus").val(G.normalize(data.productVehicleStatus));
        $(idPrefix + "\\.purchasePrice").val(G.normalize(data.purchasePrice));
        $(idPrefix + "\\.inventoryAmount").val(data.amount);
        $(idPrefix + "\\.inventoryAmountSpan").text(G.rounding(data.amount, 2));
        if (!verifyProductThroughOrderVersion(getOrderType())) {
            $(idPrefix + "\\.amount").val(1);
        } else {
            $(idPrefix + "\\.amount").val(0);
        }

        $(idPrefix + "\\.recommendedPrice").val(G.normalize(data.recommendedPrice));
        if(G.isEmpty(data.sellUnit) && isCanEditUnit()){
            $(idPrefix + "\\.unit").val(G.normalize(data.unit));
        }else{
            $(idPrefix + "\\.unit").val(G.normalize(data.sellUnit));
        }
        $(idPrefix + "\\.storageUnit").val(G.normalize(data.storageUnit));
        $(idPrefix + "\\.sellUnit").val(G.normalize(data.sellUnit));
        $(idPrefix + "\\.rate").val(G.normalize(data.rate));
        $(idPrefix + "\\.lowerLimit").val(G.normalize(data.lowerLimit));
        $(idPrefix + "\\.upperLimit").val(G.normalize(data.upperLimit));
        $(idPrefix + "\\.tradePrice").val(G.normalize(data.tradePrice));
        $(idPrefix + "\\.storageBin").val(G.normalize(data.storageBin));
        $(idPrefix + "\\.storageBinSpan").text(G.normalize(data.storageBin));
        //施工维修单
        if (orderType == "repairOrder") {
            $(idPrefix + "\\.price").val(G.normalize(data.recommendedPrice));
            $(idPrefix + "\\.productKind").val(G.normalize(data.kindName));
            isLack();
        }

        //保险单
        if (orderType == "insuranceOrder") {
            $(idPrefix + "\\.price").val(G.normalize(data.recommendedPrice));
        }
        //商品销售单
        if (orderType == "goodsSaleOrder") {
            $(idPrefix + "\\.price").val(G.normalize(data.recommendedPrice));
            $(idPrefix + "\\.businessCategoryName").val(G.normalize(data.businessCategoryName));
        }
        if (orderType == "purchaseReturnOrder") {
            $(idPrefix + "\\.price").val(G.normalize(data.purchasePrice));
            $(idPrefix + "\\.inventoryAveragePrice").text(inventoryAveragePrice);
        }
        //入库单
        if (orderType == "purchaseInventoryOrder") {
            $(idPrefix + "\\.productKind").val(G.normalize(data.kindName));
            $(idPrefix + "\\.storageBinSpan").parent().find("span").hide();
            $(idPrefix + "\\.storageBinSpan").show();
        }
        if (orderType == "allocateRecord") {
            $(idPrefix + "\\.costPrice").val(G.rounding(data.inventoryAveragePrice, 2));
        }
        if (orderType == "innerPicking" || orderType == "innerReturn") {
            $(idPrefix + "\\.price").val(G.normalize(data.inventoryAveragePrice, "0"));
            $(idPrefix + "\\.price_span").text(G.normalize(data.inventoryAveragePrice, "0"));
            $(idPrefix + "\\.unit_span").text(G.normalize(data.sellUnit));
        }
        if (orderType == "inventoryCheckOrder") {
            $(idPrefix + "\\.actualInventoryAmount").val(G.rounding(data.amount, 2));
            $(idPrefix + "\\.inventoryAveragePrice").val(G.normalize(data.inventoryAveragePrice, "0"));
            $(idPrefix + "\\.actualInventoryAveragePrice").val(G.normalize(data.inventoryAveragePrice, "0"));
            $(idPrefix + "\\.inventoryAmountAdjustment").val(0);
            $(idPrefix + "\\.inventoryAdjustmentPrice").val(0);
            $(idPrefix + "\\.inventoryAveragePriceAdjustment").val(0);
            $(idPrefix + "\\.inventoryAmountUnit").val(G.rounding(data.amount, 2) + G.normalize(data.sellUnit));
        }
        //采购单
        if (orderType == "purchaseOrder") {
            $(idPrefix + "\\.price").val(G.normalize(data.purchasePrice, "0.0"));
        }
        //销售退货单
        if (orderType == "salesReturnOrder") {
            if(!G.isEmpty($("#customerId").val())) {
                APP_BCGOGO.Net.syncPost({
                    url: "txn.do?method=getLatestCustomerPrice",
                    data: {
                        "customerId": $("#customerId").val(),
                        "productId": data.productId
                    },
                    dataType: "json",
                    success: function(result) {
                        if(result.success && result.data != null) {
                            data.recommendedPrice = result.data.itemPrice;
                        }
                    }
                });
            }
            $(idPrefix + "\\.price").val(G.normalize(data.recommendedPrice));
            $(idPrefix + "\\.originSalesPrice").val(G.normalize(data.recommendedPrice));
            $(idPrefix + "\\.originSalesPriceSpan").text(G.normalize(data.recommendedPrice));
            $(idPrefix + "\\.total").val(G.normalize(data.recommendedPrice));
            $(idPrefix + "\\.totalSpan").text(G.normalize(data.recommendedPrice));
        }
        if (orderType == "borrowOrder") {
            $(idPrefix + "\\.inventoryAmountSpan").css('display', "inline");
            $(idPrefix + "\\.price_span").text(inventoryAveragePrice);
            $(idPrefix + "\\.price").val(inventoryAveragePrice);
            $(idPrefix + "\\.unit").val(G.normalize(data.sellUnit));
        }

        if (isNeedInitUnitTd()) {
            initUnitTd($(idPrefix + "\\.unit"));
        }

        // 商品分类设置, 针对 "销售单"、 "采购单" , fixed bug: 5902
        if (G.contains($(document.body).attr("ordertype"), [App.OrderTypes.INVENTORY, App.OrderTypes.PURCHASE])) {
            $(idPrefix + "\\.productKind").val(G.normalize(data.kindName))
        }

        // 营业分类设置, 针对 "施工单"、 "销售单" , fixed bug: 5936
        if (G.contains($(document.body).attr("ordertype"), [App.OrderTypes.REPAIR, App.OrderTypes.SALE])) {
            $(idPrefix + "\\.businessCategoryName").val(G.normalize(data.businessCategoryName));
        }

        // 销售价设置， 针对 "销售退货单" , fixed bug : 5910
        //各位伟大的代码骑士， 当你拿起的 "无毁的湖光" 在重构时， 歼灭让众人痛苦的腐烂 code 时， 顺便带这块代码一程吧
//        if (G.contains($(document.body).attr("ordertype"), [App.OrderTypes.SALE_RETURN])) {              //todo bug8345 这里什么意思？
//            var amount = $(idPrefix + "\\.amount").val();
//            $(idPrefix + "\\.originSalesPrice").val(G.normalize(data.recommendedPrice * amount));
//            $(idPrefix + "\\.originSalesPriceSpan").html(G.normalize(data.recommendedPrice * amount));
//        }

        if ($.isFunction(window.setTotal)) {
            setTotal();//每个模块都有自定义的
        }
    }

}

function autoAddBlankRow(thisDom) {
    if ((G.Lang.endWith($(thisDom).attr("id"), ".productName") || G.Lang.endWith($(thisDom).attr("id"), ".commodityCode")) && !G.isEmpty($(thisDom).val())) {
        var orderType = $("#orderType").val(),
            value = {};
        value.table_productNo__tr__productName = $("#table_productNo tbody tr:last input[id$='.productName']").val();
        value.table_productNo_2__tr__productName = $("#table_productNo_2 tbody tr:last input[id$='.productName']").val();
        value.table_productNo__item__productName = $("#table_productNo tbody .item:last input[id$='.productName']").val();
        value.table_productNo__tr__commodityCode = $("#table_productNo tbody tr:last input[id$='.commodityCode']").val();
        value.table_productNo_2__tr__commodityCode = $("#table_productNo_2 tbody tr:last input[id$='.commodityCode']").val();
        value.table_productNo__item__commodityCode = $("#table_productNo tbody .item:last input[id$='.commodityCode']").val();

        if (orderType == "purchaseOrder") {
            if (!G.isEmpty(value.table_productNo__tr__productName) || !G.isEmpty(value.table_productNo__tr__commodityCode)) {
                purchaseOrderAdd();
            }
        } else if (orderType == "repairOrder") {
            if (!G.isEmpty(value.table_productNo_2__tr__productName) || !G.isEmpty(value.table_productNo_2__tr__commodityCode)) {
                addNewRow(0);
                isShowAddButton2();
            }
        } else if (orderType == "purchaseReturnOrder") {
            if (!G.isEmpty(value.table_productNo__item__productName) || !G.isEmpty(value.table_productNo__item__commodityCode)) {
                returnStorageOrderAdd();
            }
        } else if (orderType == "inventoryCheckOrder") {
            if (!G.isEmpty(value.table_productNo__item__productName) || !G.isEmpty(value.table_productNo__item__commodityCode)) {
                inventoryCheckItemAdd();
            }
        } else if (orderType == "salesReturnOrder") {
            if (!G.isEmpty(value.table_productNo__item__productName) || !G.isEmpty(value.table_productNo__item__commodityCode)) {
                salesReturnAdd();
            }
        } else if (orderType == "allocateRecord") {
            if (!G.isEmpty(value.table_productNo__item__productName) || !G.isEmpty(value.table_productNo__item__commodityCode)) {
                addItemRow();
            }
        } else if (orderType == "innerPicking") {
            if (!G.isEmpty(value.table_productNo__item__productName) || !G.isEmpty(value.table_productNo__item__commodityCode)) {
                innerPickingAdd();
            }
        } else if (orderType == "innerReturn") {
            if (!G.isEmpty(value.table_productNo__item__productName) || !G.isEmpty(value.table_productNo__item__commodityCode)) {
                innerReturnAdd();
            }
        } else if (orderType == "borrowOrder") {
            if (!G.isEmpty(value.table_productNo__item__productName) || !G.isEmpty(value.table_productNo__item__commodityCode)) {
                borrowOrderAdd();
            }
        } else {
            if (!G.isEmpty(value.table_productNo__tr__productName)) {
                inventoryOrderAdd();
            }
        }
    }
}

//借调弹出框 动态的改变高度
function borrowOrderReturnStyleAdjust() {
    var height = $("#returnOrderTable").height();
    height += 55;
    $("#returnOrder_dialog .cartBody").css("height", String(height) + "px");
}

function batchSynSupplierInventory(storehouseId) {
    var productLocalInfoIds = new Array();
    var $items = $(".item");
    var productLocalInfoId = "";
    $items.each(function (i) {
        productLocalInfoId = $.trim($(this).find("input[id$='.productId']").val());
        var unit = $.trim($(this).find("input[id$='.unit']").val());
        if (!G.isEmpty(productLocalInfoId)) {
            productLocalInfoIds.push(productLocalInfoId);
        }
    });
    if (productLocalInfoIds.length == 0) {
        return;
    }
    $('[id$="_supplierInfo"]').remove();
    $('[id$="amount"],[id$="returnAmount"],[id$="actualInventoryAmount"]').val("");
    $('[id$="_supplierInfo"]').die();
//    var orderType = getOrderType();

//    if(orderType == "pendingPurchaseOrder"){
//        orderType = "SALE";
//    }
    var paramJson = {
        storehouseId: storehouseId,
        productIds: productLocalInfoIds.join(","),
        orderType: getOrderType()
    };
    App.Net.syncPost({
        url: "txn.do?method=getSupplierInventoryByStorehouse",
        data: paramJson,
        dataType: "json",
        success: function (data) {
            var inventoryMap = new Map();
            if (!G.isEmpty(data)) {
                for (var i = 0; i < productLocalInfoIds.length; i++) {
                    inventoryMap.put(productLocalInfoIds[i], data[productLocalInfoIds[i]]);
                }
            }
            var supplierInventorys="";
            $items.each(function(i){
                productLocalInfoId= $(this).find("input[id$='.productId']").val();
                var pId = $(this).find("input[id$='.productId']").attr("id");
                var idPrefix=pId.substring(0, pId.indexOf("."));
                if(!G.isEmpty(productLocalInfoId)){
                    var out_Prefix = "itemDTOs["+i+"].outStorageRelationDTOs[";
                    var trStr='<tr id="'+idPrefix+'_supplierInfo" class="supplierInfo"><td colspan="15"><div class="trList">';
                    trStr+='<div class="t_title" style="text-align: left;">商品的供应商库存信息</div>';
                    supplierInventorys=inventoryMap.get(productLocalInfoId);
                    if(!G.isEmpty(supplierInventorys)){
                        for(var i=0;i<supplierInventorys.length;i++){
                            var supplierInventory=supplierInventorys[i];
                            if(G.isEmpty(supplierInventory)){

                                continue;
                            }
                            var supplierName = G.normalize(supplierInventory.supplierName);
                            var supplierId = supplierInventory.supplierIdStr;
                            var remainAmount = G.rounding(supplierInventory.remainAmount, 2);
                            if (G.isEmpty(supplierId)) {
                                supplierId = "";
                            }
                            if (G.isEmpty(remainAmount)) {
                                remainAmount = "";
                            }
                            if (getOrderType() == "RETURN" && $("#supplierId").val() != supplierId) {
                                continue
                            }
                            if(getOrderType()=='RETURN'&&getOrderStatus()=="SELLER_DISPATCH"){
                                continue;
                            }
                            var relatedSupplierAveragePrice =  dataTransition.rounding(supplierInventory.averageStoragePrice, 2) ;
                            trStr+='<div class="divList"><input type="hidden" value="'+supplierId+'" name="'+out_Prefix+i+'].relatedSupplierId" />' +
                                '<input type="hidden" value="'+supplierInventory.supplierName+'" name="'+out_Prefix+i+'].relatedSupplierName" />' +
                                '<input type="hidden" value="'+relatedSupplierAveragePrice+'" name="'+out_Prefix+i+'].relatedSupplierAveragePrice" />' +
                                '<input type="hidden" value="'+remainAmount+'" name="'+out_Prefix+i+'].relatedSupplierInventory" />' +
                                '<input type="hidden" value="'+supplierInventory.supplierType+'" name="'+out_Prefix+i+'].supplierType" /><div style="width: 120px" class="supplierName" title="'+supplierName+'">'+tableUtil.limitLen(supplierName,10)+'</div>' ;
                            if(getOrderStatus()=='PENDING'){
                                trStr+='<div style="width: 90px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">本次出货&nbsp;';
                                trStr+='<input type="text"  remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                            }else if(getOrderStatus()=='STOCKING'){
                                trStr+='<div style="width: 80px">剩余：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 80px">已出货:<label class="tempAmount">'+tempAmount+'</label></div><div style="width: 120px">本次出货&nbsp;';
                                trStr+='<input type="text" tempAmount="'+tempAmount+'" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                            }else if(getOrderType()=='SALE'){
                                trStr+='<div style="width: 100px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">销售数量&nbsp;';
                                trStr+='<input type="text" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                            } else if(getOrderType()=='SALE_RETURN'){
                                trStr+='<div style="width: 100px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">退货数量&nbsp;';
                                trStr+='<input type="text" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                            } else if(getOrderType()=='INNER_PICKING'){
                                trStr+='<div style="width: 100px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">领料数量&nbsp;';
                                trStr+='<input type="text" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                            } else if(getOrderType()=='INNER_RETURN'){
                                trStr+='<div style="width: 100px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">退料数量&nbsp;';
                                trStr+='<input type="text" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                            } else if(getOrderType()=='BORROW_ORDER'){
                                trStr+='<div style="width: 100px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">借料数量&nbsp;';
                                trStr+='<input type="text" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                            } else if(getOrderType()=='RETURN_ORDER'){
                                trStr+='<div style="width: 100px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">归还数量&nbsp;';
                                trStr+='<input type="text" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                            }else if(getOrderType()=='RETURN'){
                                trStr+='<div style="width: 100px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">退货数量&nbsp;';
                                trStr+='<input type="text" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                            }else if(getOrderType()=='ALLOCATE_RECORD'){
                                trStr+='<div style="width: 100px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">调拨数量&nbsp;';
                                trStr+='<input type="text" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                            } else if(getOrderType()=='INVENTORY_CHECK'){
                                trStr+='<div style="width: 100px">账面库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">实际库存&nbsp;';
                                trStr+='<input type="text" value="'+remainAmount+'" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                            }
                            trStr+='</div><div class="rightIcon" style="display: none;width:19px;"></div><div class="wrongIcon" style="display: none;width:60px;">库存不足!</div></div>';
                        }
                        trStr += '</div></td></tr>';
                        $(this).after(trStr);
                    }
                    initAndbindSupplierInventoryList(idPrefix);
                }

            });
            if (!$(".j_checkStoreHouse").val()) {
                $('[id$="_supplierInfo"]').live("click", function () {
                    nsDialog.jAlert("请先选择仓库！");
                });
            }

        },
        error: function () {
            alert("网络异常，请重新尝试!");
        }
    });
}

function batchSynProductInventoryAmount(storehouseId) {
    var productLocalInfoIds = new Array();
    var units = new Array();
    var $items = $(".item");
    if (getOrderType() == "REPAIR") {
        $items = $(".item1");
        if (!G.isEmpty($("#storehouseId").val())) {
            $("#storehouseSelectTip").hide();
        }
    }
    $items.each(function (i) {
        var productLocalInfoId = $.trim($(this).find("input[id$='.productId']").val());
        var unit = $.trim($(this).find("input[id$='.unit']").val());
        if (!G.isEmpty(productLocalInfoId)) {
            productLocalInfoIds.push(productLocalInfoId);
            units.push(G.isEmpty(unit) ? " " : unit);
        }
    });
    if (productLocalInfoIds.length == 0) {
        return;
    }
    var paramJson = {
        storehouseId: storehouseId,
        productLocalInfoIds: productLocalInfoIds.join(","),
        units: units.join(",")
    };
    App.Net.syncPost({
        url: "product.do?method=getProductStorehouseInventoryByProductLocalInfoIds",
        data: paramJson,
        dataType: "json",
        success: function (json) {
            if ($.isFunction(window.updateProductInventory)) {
                updateProductInventory(json.StoreHouseInventoryMap, true);//每个模块都有自定义的
            }
            if ($.isFunction(window.updateProductStorageBin)) {
                updateProductStorageBin(json.StorehouseStorageBinMap);//每个模块都有自定义的
            }
        },
        error: function () {
            nsDialog.jAlert("数据异常！");
        }
    });
}

function getCursorPosition(ctrl) {//获取光标位置函数
    var CaretPos = 0;
    // IE Support
    if (ctrl.type != "text") {
        return;
    }
    if (document.selection) {
        ctrl.focus();
        var Sel = document.selection.createRange();
        Sel.moveStart('character', -ctrl.value.length);
        CaretPos = Sel.text.length;
    }
    // Firefox support
    else if (ctrl.selectionStart != NaN || ctrl.selectionStart == '0')
        CaretPos = ctrl.selectionStart;
    return (CaretPos);
}

function setCursorPosition(ctrl, pos) {//设置光标位置函数
    if (ctrl.type != "text") {
        return;
    }
    if (ctrl.setSelectionRange) {
        ctrl.focus();
        ctrl.setSelectionRange(pos, pos);
    }
    else if (ctrl.createTextRange) {
        var range = ctrl.createTextRange();
        range.collapse(true);
        range.moveEnd('character', pos);
        range.moveStart('character', pos);
        range.select();
    }
}


//文本框.模拟placeholder(jQuery对象)
(function () {
    var _placeHolder = {
        init: function () {
            var color = null;
            if (arguments && arguments[1]) {
                color = arguments[1].color;
            } else {
                color = "#ADADAD";
            }
            //得到当前jQuery对象.
            var $obj = $(this);

            $obj.data("customBackgroundColor", color);
            //是否存在initialValue属性.
            $obj.each(function (i, obj) {
                if ($(obj).attr("initialValue") && (G.isEmpty($(obj).val()) || $.trim($(obj).val()) == $(obj).attr('initialValue'))) {
                    $(obj).css({"color": color});
                    $(obj).val($(obj).attr('initialValue'));
                } else {
                    $(obj).css({"color": "#000000"});
                }
            });

            //给当前jQuery对象绑定事件.
            $obj.click(function (event) {
                if ($(event.target).val() != '' && !$(event.target).hasClass("J-active")) {
                    $(event.target).select();
                    $(event.target).addClass("J-active");
                }
            }).blur(function (event) {
                    var _initialValue = $(event.target).attr("initialValue");
                    if (_initialValue != null && _initialValue != "") {
                        if ($.trim(event.target.value).length == 0 || $.trim(event.target.value) == _initialValue) {
                            event.target.value = _initialValue;
                            $(event.target).css({"color": color});
                        } else {
                            $(event.target).css({"color": "#000000"});
                        }
                    }
                    $(event.target).removeClass("J-active");
                }).focus(function (event) {
                    var _initialValue = $(event.target).attr("initialValue");
                    if (_initialValue != null && _initialValue != "") {
                        if (event.target.value == _initialValue) {
                            event.target.value = "";
                        }
                        $(event.target).css({"color": "#000000"});
                    }
                });
        },
        reset: function () {
            //得到当前jQuery对象.
            var $obj = $(this);

            //重新设置value和
            $obj.each(function (i, obj) {
                if ($(obj).attr('initialValue') && G.isEmpty($(obj).val())) {
                    $(obj).css({"color": $obj.data("customBackgroundColor")});
                    $(obj).val($(obj).attr('initialValue'));
                } else if ($(obj).val() == $(obj).attr('initialValue')) {
                    $(obj).css({"color": $obj.data("customBackgroundColor")});
                } else {
                    $(obj).css({"color": "#000000"});
                }
            });
          /*  if(!$("#my_date_self_defining").hasClass("clicked")){
                $("#my_date_self_defining").click();
            }else{
                $("#my_date_self_defining").click();
                $("#my_date_self_defining").click();
            }*/
        },
        clear: function () {
            //得到当前jQuery对象.
            var $obj = $(this);

            //重置value.
            $obj.each(function (i, obj) {
                if ($(obj).attr('initialValue') && $.trim($(obj).val()) == $(obj).attr('initialValue')) {
                    $(obj).val('');
                }
            });
        }
    };

    $.fn.placeHolder = function (method) {
        if (!method || method == "init") {
            _placeHolder.init.apply(this, arguments);
        }
        else if (method == "reset") {
            _placeHolder.reset.apply(this, arguments);
        }
        else if (method == "clear") {
            _placeHolder.clear.apply(this, arguments);
        }
        else {
            _placeHolder.init.apply(this, arguments);
        }
    };
})();

function saveSearchBoxDataToWebStorage(_textVal, _methodId) {
    var searchBoxJson = {};
    searchBoxJson["searchMethod"] = _methodId;
    searchBoxJson["searchKeyWord"] = _textVal;
    defaultStorage.setItem(storageKey.SearchBox, JSON.stringify(searchBoxJson));
}
function getCustomerOrSupplierOnlineSuggestionInHead($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var _methodId = $('#_searchMethod').val();

    var dropList = App.Module.droplist;
    dropList.setUUID(G.generateUUID());
    var ajaxData = {
        searchWord: searchWord,
        customerOrSupplier: _methodId,
        shopRange: "notRelated",
        uuid: dropList.getUUID()
    };
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierOnlineSuggestion";
    App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        dropList.show({
            "selector": $domObject,
            "data": result,
            "onSelect": function (event, index, data) {
                $domObject.val(data.label);
                $domObject.css({"color": "#000000"});
                dropList.hide();
                saveSearchBoxDataToWebStorage($domObject.val() == $domObject.attr('initialValue') ? '' : $domObject.val(), _methodId);
                if (_methodId == "supplierOnline") {
                    openOrAssign('apply.do?method=getApplySuppliersPage&supplierName=' + $domObject.val());
                } else if (_methodId == "customerOnline") {
                    openOrAssign('apply.do?method=getApplyCustomersPage&customerName=' + $domObject.val());
                }
            }
        });
    });

}

function getCustomerOrSupplierSuggestionInHead($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var dropList = App.Module.droplist;
    dropList.setUUID(G.generateUUID());
    var ajaxData = {
        searchWord: searchWord,
        searchField: "info",
        titles: "name,contact,mobile",
        uuid: dropList.getUUID()
    };
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
    App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        dropList.show({
            "selector": $domObject,
            "autoSet": false,
            "data": result,
            onGetInputtingData: function () {
                return {
                    details: {"name": $domObject.val()}
                };
            },
            "onSelect": function (event, index, data) {
                $domObject.val(data.details.name);
                $domObject.css({"color": "#000000"});
                dropList.hide();
                var _methodId = $('#_searchMethod').val();
                saveSearchBoxDataToWebStorage($domObject.val() == $domObject.attr('initialValue') ? '' : $domObject.val(), _methodId);
                if(!G.Lang.isEmpty(data.details.id) && data.details.customerOrSupplier=="customer"){
                    //如果是客户
                    openOrAssign('unitlink.do?method=customer&customerId=' +data.details.id);
                }else if(!G.Lang.isEmpty(data.details.id) && data.details.customerOrSupplier=="supplier"){
                    //如果是供应商
                    openOrAssign('unitlink.do?method=supplier&supplierId=' + data.details.id);
                }else{
                    getByKeyWord($domObject.val());
                }
            },
            onKeyboardSelect: function (event, index, data, hook) {
                var _methodId = $('#_searchMethod').val();
                $domObject.val(data.details.name);
                $domObject.css({"color": "#000000"});
                saveSearchBoxDataToWebStorage($domObject.val() == $domObject.attr('initialValue') ? '' : $domObject.val(), _methodId);
            }
        });
    });

}

function generateURLParam(data) {
    var paramNameList = [
        "productName",          "productBrand",         "productSpec",      "productModel",
        "productVehicleBrand",  "productVehicleModel",  "commodityCode"
    ],
        dataNameList = [
            "product_name",         "product_brand",        "product_spec",     "product_model",
            "product_vehicle_brand","product_vehicle_model","commodity_code"
        ],
        param = "";

    var add = function(root ,paramName, dataName) {
        return G.isEmpty(root[dataName]) ? "" : "&" + paramName + "=" + root[dataName];
    };

    for (var i = 0; i < paramNameList.length; i++) {
        param = param.concat(add(data.details,  paramNameList[i],  dataNameList[i]));
    }
    return param;
}

function getProductSuggestionInHead($domObject, ajaxUrl, url) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var _searchMethod = $('#_searchMethod').val();
    if ((_searchMethod == "accessoryOnline" || _searchMethod == "customerInventoryOnline") && G.isEmpty(searchWord)) {
        return;
    }

    var dropList = App.Module.droplist;
    dropList.setUUID(G.generateUUID());
    var ajaxData = {
        searchWord: searchWord,
        searchField: "product_info",
        uuid: dropList.getUUID()
    };
    App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        dropList.show({
            "selector": $domObject,
            "autoSet": false,
            "data": result,
            "onSelect": function (event, index, data) {
                saveSearchBoxDataToWebStorage($domObject.val() == $domObject.attr('initialValue') ? '' : $domObject.val(), _searchMethod);
                var param = generateURLParam(data);
                if (!G.isEmpty(param)) {
                    url += $.trim(param);
                }
                openOrAssign(url);
            }
        });
    });
}


function getVehicleLicenceNoSuggestionInHead(domObject) {
    var $domObject = $(domObject);
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    if (G.isEmpty(searchWord)) return;
    var droplist = App.Module.droplist;
    droplist.setUUID(G.generateUUID());
    var ajaxUrl = "searchInventoryIndex.do?method=getVehicleLicenceNoSuggestion";
    var ajaxData = {
        searchWord: searchWord,
        uuid: droplist.getUUID()
    };
    App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        droplist.show({
            "selector": $domObject,
            "data": result,
            "onSelect": function (event, index, data) {
                $domObject.val(data.label);
                $domObject.css({"color": "#000000"});
                droplist.hide();
                saveSearchBoxDataToWebStorage($domObject.val() == $domObject.attr('initialValue') ? '' : $domObject.val(), $('#_searchMethod').val());
                openOrAssign('txn.do?method=getRepairOrderByVehicleNumber&task=maintain&vehicleNumber=' + data.label);
            }
        });
    });
}

//TODO 根据关键字，查询单位联系人

function getByKeyWord(keyword) {
    if (keyword) {
        var r = App.Net.syncGet({url: "customer.do?method=getbykeyword", data: {keyword: keyword, time: Date.parse(new Date())}, dataType: "json"});
        G.debug("FunctionName=getByKeyWord:" + r);
        if (!r) {
            alert("网络错误");
            return;
        } else if (typeof r == "object") {
            if (!("supplier" in r) && !("customer" in r)) {
                //如果即不是客户又不是供应商
                bcgogo.checksession({
                    "parentWindow": window.parent,
                    'iframe_PopupBox': $("#iframe_tippage")[0],
                    'src': encodeURI("customer.do?method=tippage&uvmValue=" + r.uvmValue + "&newTab=" + isTyping() + "&ts=" + new Date().getTime())
                });
                $("#iframe_tippage")[0].style.display = "block";
            } else if ("customerIdStr" in r) {
                //如果是客户
                openOrAssign('unitlink.do?method=customer&customerId=' + r.customerIdStr);
            } else if ("supplierIdStr" in r) {
                //如果是供应商
                openOrAssign('unitlink.do?method=supplier&supplierId=' + r.supplierIdStr);
            } else {
                //既有客户又有供应商 或者 不是唯一的
                openOrAssign(encodeURI('unitlink.do?method=index&ucmValue=' + $("#_searchInputText").val()));
            }
        }
    } else {
        openOrAssign(encodeURI('unitlink.do?method=index&ucmValue=' + ($("#_searchInputText").val() == $("#_searchInputText").attr('initialValue') ? '' : $("#_searchInputText").val())));
    }
}

/*获取其他金额的总计 */
function getOtherIncomeTotal() {
    var otherIncomeTotal = 0;
    $("table[name='otherIncomeTb'] .otherIncomePrice").each(function () {
      var idPrefix = $(this).attr("id").split(".")[0];

      if ($("#" + idPrefix + "\\.name").val() == "材料管理费" && $("#" + idPrefix + "\\.otherIncomePriceByRate").attr("checked")) {
        $("#" + idPrefix + "\\.otherIncomePriceSpan").text(dataTransition.rounding($("#" + idPrefix + "\\.otherIncomePriceRate").val() * $("#productTotal").text() / 100,2));
        $("#" + idPrefix + "\\.price").val($("#" + idPrefix + "\\.otherIncomePriceSpan").text());


        $("#" + idPrefix + "\\.otherIncomeCalculateWay").val("RATIO");
        if ($("#" + idPrefix + "\\.otherIncomeCostPriceCheckbox").attr("checked")) {
          $("#" + idPrefix + "\\.otherIncomeCostPrice").val($("#" + idPrefix + "\\.price").val());
        }

        if ($("#" + idPrefix + "\\.otherIncomePriceRate").val() != "请输入比率") {
          $("#" + idPrefix + "\\.otherIncomeRate").val($("#" + idPrefix + "\\.otherIncomePriceRate").val());
        }

      } else if ($("#" + idPrefix + "\\.name").val() == "材料管理费" && $("#" + idPrefix + "\\.otherIncomePriceByAmount").attr("checked")) {
        $("#" + idPrefix + "\\.price").val(dataTransition.rounding($("#" + idPrefix + "\\.otherIncomePriceText").val(),2));
        $("#" + idPrefix + "\\.otherIncomeCalculateWay").val("AMOUNT");
      }else  if($("#" + idPrefix + "\\.name").val() != "材料管理费"){
        $("#" + idPrefix + "\\.otherIncomeRate").val("");
        $("#" + idPrefix + "\\.otherIncomeCalculateWay").val("");
      }

      if(!$("#" + idPrefix + "\\.otherIncomeCostPriceCheckbox").attr("checked")){
        $("#" + idPrefix + "\\.otherIncomeCostPrice").val("");
      }else{
        $("#" + idPrefix + "\\.otherIncomeCostPrice").val(dataTransition.rounding($("#" + idPrefix + "\\.otherIncomeCostPrice").val(),2));
      }
      otherIncomeTotal += G.rounding($(this).val(), 2);
    });
    return otherIncomeTotal;
}

/*获取单据金额，包括商品、其他费用*/
function getItemTotal() {
    var itemTotal = 0;
    $(".itemTotal").each(function (i) {
        var txt = $(this);
        if ($.trim(txt.val()))
            itemTotal += parseFloat(txt.val());
    });
    return itemTotal;
}

//商品出入库打通需要显示供应商列表的单据
var pt_ordertypes = ['SALE', 'SALE_RETURN', 'INVENTORY_CHECK', 'INNER_PICKING', 'INNER_RETURN', 'ALLOCATE_RECORD', 'BORROW_ORDER', "RETURN_ORDER", "RETURN"];
function verifyProductThroughOrderVersion(orderType) {
    if (!App.Permission.Version.ProductThroughSelectSupplier) { //验证出入库打通版本
        return false;
    }
    if(getOrderType()=='RETURN'&&getOrderStatus()=="SELLER_DISPATCH"){
        return false;
    }
    if(G.isEmpty(orderType)){
        return false;
    }
    var flag = false;
    for (var i = 0; i < pt_ordertypes.length; i++) {  //验证有下列的单据
        if (orderType == pt_ordertypes[i]) {
            flag = true;
        }
    }
    return flag;
}

function generateProductInfo(product,highLight){
    if(G.isEmpty(product)) return "";
    var productInfo="";
    if(!G.isEmpty(product.commodityCode))
        productInfo+=G.normalize(product.commodityCode)+'&nbsp;';
    if(highLight==false){
        productInfo+=G.normalize(product.name)+'&nbsp;';
    }else{
        productInfo+="<span class='yellow_color'>"+G.normalize(product.name)+"</span>&nbsp;";
    }
    if(!G.isEmpty(product.brand))
        productInfo+=G.normalize(product.brand)+'&nbsp;';
    if(!G.isEmpty(product.spec))
        productInfo+=G.normalize(product.spec)+'&nbsp;';
    if(!G.isEmpty(product.model))
        productInfo+=G.normalize(product.model)+'&nbsp;';
    if(!G.isEmpty(product.vehicleModel))
        productInfo+=G.normalize(product.vehicleModel)+'&nbsp;';
    if(!G.isEmpty(product.vehicleBrand))
        productInfo+=G.normalize(product.vehicleBrand)+'&nbsp;';
     if(!G.isEmpty(product.productVehicleBrand))
        productInfo+=G.normalize(product.productVehicleBrand)+'&nbsp;';
     if(!G.isEmpty(product.productVehicleModel))
        productInfo+=G.normalize(product.productVehicleModel)+'&nbsp;';
    return productInfo;
}

function getAreaInfoByParentNo(parentNo,callBack){
    if(G.isEmpty(parentNo)){
        return;
    }
    APP_BCGOGO.Net.syncGet({
        "url": "shop.do?method=selectarea&parentNo="+parentNo,
        type: "POST",
        cache: false,
        "dataType": "json",
        success: function (areaList) {
            if($.isFunction(callBack)){
                callBack(areaList);
            }
        },
        error:function(){
            nsDialog.jAlert("网络异常！");
        }
    });
}

function getShopComment(callBack){
    APP_BCGOGO.Net.asyncGet({
        "url": "shopData.do?method=getShopComment",
        type: "POST",
        cache: false,
        "dataType": "json",
        success: function (shopInfo) {
            if($.isFunction(callBack)){
                callBack(shopInfo);
            }
        },
        error:function(){
            G.error("shopData.do?method=getShopComment error, from common.js");
        }
    });
}


//获取服务器时间
function getServiceTime(callBack){
    APP_BCGOGO.Net.asyncGet({
        "url": "txn.do?method=getServiceTime",
        type: "POST",
        cache: false,
        "dataType": "json",
        success: function (timeMap) {
            if($.isFunction(callBack)){
                callBack(timeMap);
            }
        },
        error:function(){
            nsDialog.jAlert("网络异常！");
        }
    });
}

function refreshPage(){
    window.location.reload();
}

//主动推荐
function activeRecommendSupplierTip($data_row, orderType) {
    if(!App.Permission.Version.ActiveRecommendSupplier){
        return;
    }
    var productId = $data_row.find("input[id$='.productId']").val(),
        comparePrice,
        amount= $data_row.find("input[id$='.amount']").val();
    if (!productId || "INVENTORY,PURCHASE".indexOf(orderType) == -1) {   //INVENTORY,PURCHASE,REPAIR
        return;
    }
    //非缺料
    if ("REPAIR" == orderType && !isThisItemLack($data_row)) {
        $data_row.find("[action-type=supplier-active-recommend]").remove();
        return;
    }
    var $activeRecommend = $('<a class="jian" action-type="supplier-active-recommend" product-id="' + productId + '" amount="'+amount+'"></a>');
    $data_row.find("[action-type=supplier-active-recommend]").remove();
    if (orderType == "INVENTORY") {
        comparePrice = $data_row.find('[id$=.purchasePrice]').val();
    } else if (orderType == "PURCHASE" ) {
        comparePrice = $data_row.find('[id$=.price]').val();
    }/* else if(orderType == "REPAIR"){
     comparePrice = $data_row.find('[id$=.purchasePrice]').val();
     }*/

    App.Net.asyncAjax({
        type: "POST",
        url: "activeRecommendSupplier.do?method=obtainActiveRecommendSupplierByProductId",
        cache: false,
        data: {productId: productId, comparePrice: comparePrice, orderType: orderType},
        dataType: "json",
        success: function (result) {
            if ("INVENTORY" == orderType && $.isEmptyObject(result)) {
                return;
            }
            var $totalSpan = $data_row.find('[id$=.total_span]');
            if($totalSpan[0] && !$totalSpan.parent().find("a.jian")[0]){
                $totalSpan.after($activeRecommend);
            }
            var div = '<div class="tip" style="margin-left:8px;">';
            div += '    <div class="tipTop"></div>';
            div += '    <div class="tipBody">';
            div += '       <b class="yellow_color tipTittle">温馨小提示</b>';
//            if ("REPAIR" == orderType) {
            var recommend = $("[action-type=supplier-active-recommend]"), productIds = "", productIdsAndAmounts = "";
            for (var i = 0; i < recommend.length; i++) {
                productIds += $(recommend[i]).attr("product-id");
                productIdsAndAmounts += $(recommend[i]).attr("product-id") + "_" + $(recommend[i]).attr("amount");
                if (i + 1 != recommend.length) {
                    productIds += ",";
                    productIdsAndAmounts += ",";
                }
            }
//            } else {
//                productIds = productId;
//                productIdsAndAmounts = productId + "_" + "1.0";
//            }
            var preBu ='preBuyOrder.do?method=createPreBuyOrderByProductIdInfos&productIdInfos=' + productIdsAndAmounts;
            if (result) {
                var leastConsume = result['LEAST_CONSUME'],
                    bcgogoRecommend = result['BCGOGO_RECOMMEND'];
                //最近消费供应商
                if (leastConsume) {
                    var leastConsumeSupplier = leastConsume['supplierDTO'];
                    if (leastConsumeSupplier) {
                        div += '<div class="tipLine">' +
                            '<span class="tipName" style="width:170px; display:inline-block;">上次采购供应商：' +
                            '<span>' + leastConsumeSupplier['name'] + '</span></span>';
                        div += '       <a class="blue_color buy j_recommend_buy" href="RFbuy.do?method=create&supplierId=' + leastConsumeSupplier['idStr'] + '&productIds=' + '" target="_blank" '
                            + ' supplier_id="' + leastConsumeSupplier['idStr'] + '" product_id="' + productId + '">马上采购</a>';
                        div += '   </div>';
                        div += '<div class="hr"></div>';
                    }
                }
                //bcgogo推荐供应商
                if (bcgogoRecommend) {
                    var bcgogoRecommendShop = bcgogoRecommend['shopDTO'],
                        bcgogoRecommendProduct = bcgogoRecommend['productDTO'];
                    if (bcgogoRecommendProduct && bcgogoRecommendShop) {
                        //推荐供应商与最近使用不是同一个供应商
//                        if (!bcgogoRecommendShop) {
                        var commodityCode = (bcgogoRecommendProduct['commodityCode'] ? (bcgogoRecommendProduct['commodityCode'] + ' ') : ""),
                            name = (bcgogoRecommendProduct['name'] ? (bcgogoRecommendProduct['name'] + ' ') : ""),
                            brand = (bcgogoRecommendProduct['brand'] ? bcgogoRecommendProduct['brand'] : ""),
                            model = (bcgogoRecommendProduct['model'] ? ( bcgogoRecommendProduct['model'] + ' ') : ""),
                            spec = (bcgogoRecommendProduct['spec'] ? bcgogoRecommendProduct['spec'] : ""),
                            productVehicleBrand = (bcgogoRecommendProduct['productVehicleBrand'] ? (bcgogoRecommendProduct['productVehicleBrand'] + ' ') : ""),
                            productVehicleModel = (bcgogoRecommendProduct['productVehicleModel'] ? bcgogoRecommendProduct['productVehicleModel'] : "");
                        div += ' <div class="buyList">' +
                            '        <div class="tipLine  yellow_color"><b>推荐供应商：' + bcgogoRecommendShop['name'] + '</b></div>';
                        if (commodityCode || name || brand) {
                            div += '         <div class="tipLine">' + commodityCode + name + brand + '</div>';
                        }
                        if (model || spec) {
                            div += '         <div class="tipLine">' + model + spec + '</div>';
                        }
                        if (productVehicleBrand || productVehicleModel) {
                            div += '         <div class="tipLine">' + productVehicleBrand + productVehicleModel + '</div>';
                        }
                        div += '         <div class="tipLine">单价：<b class="yellow_color">¥' + bcgogoRecommendProduct['inSalesPrice'] + '</b><a style="cursor: pointer;" class="blue_color buy" action-type="purchase-recommend-product" target-id="' + productId + '" product-id="' + bcgogoRecommendProduct['productLocalInfoIdStr'] + '">马上采购</a></div>' +
                            '    </div>';
//                        }
                    }
                } else if ("INVENTORY" != orderType) {
                    //发布求购信息
                    div += '<div class="tipLine"><span>您还可以发布求购信息</span><a class="blue_color buy" action-type="activeRecommendSupplier-PreBuy" href="" target="_blank">马上发布</a></div>';
                }
            } else {
                //如果result为空
                //发布求购信息
                div += '<div class="tipLine">' +
                    '       <span>您还可以发布求购信息</span><a class="blue_color buy" action-type="activeRecommendSupplier-PreBuy" target="_blank">马上发布</a>' +
                    '   </div>';
            }
            div += '  </div>';
            div += '<div class="tipBottom"></div> ';
            div += '</div>';
            $activeRecommend.attr("detail", div.toString());
            $activeRecommend.bubbleTips();
            $("[action-type='activeRecommendSupplier-PreBuy']").attr("href",preBu);


        },
        error: function (e) {
            G.error("activeRecommendSupplier.do?method=obtainActiveRecommendSupplierByProductId error response:" + e);
        }
    });
}

//推荐供应商点击事件
$(function(){
    $(".bcgogo-bubble-tip a.j_recommend_buy").live("click", function(e){
        e.preventDefault();
        e.stopPropagation();
        var supplierId = $(this).attr("supplier_id");
        var productId = $(this).attr("product_id");
        var url = $(this).attr("href");
        var productIds = "";
        $(".bcgogo-bubble-tip a.j_recommend_buy").each(function(){
            if($(this).attr("supplier_id") == supplierId){
                productIds += $(this).attr("product_id") + ",";
            }
        });
        if(productIds.length>2){
            productIds = productIds.substr(0, productIds.length-1);
        }
        url += productIds;
        window.open(url);
    })
});

function isCanEditStandardHours(){
    return App.Permission.SetBusinessConstruction;
}

var f5Flag,backspaceFlag;
function refreshConfirm (){
    nsDialog.jConfirm("是否确认离开当前页面，若是则填写的内容将全部丢失！", "提醒", function (val) {
        f5Flag=val;
        if(val){
            window.location.reload();
        }

    })
}

function backspaceConfirm(){
    nsDialog.jConfirm("是否确认离开当前页面，若是则填写的内容将全部丢失！", "提醒", function (val) {

        backspaceFlag=val;
        if(val){
            window.history.back();
        }
    })
}


$(function(){
    document.onreadystatechange = subSomething;

    function subSomething()
    {
        if(document.readyState == "complete") {
            if(($(".J_leave_page_prompt")).length!=0){
            var initform= $(".J_leave_page_prompt").serialize().toString();
            var act;
            $(window).bind("keydown", function(event) {
                if(event.keyCode==116){//进行页面刷新的时候
                    var afterinitform= $(".J_leave_page_prompt").serialize().toString();
                    act = document.activeElement.tagName;
                    if(initform==afterinitform){
                        if(act!="INPUT"&&act!="TEXTAREA"){
                            refreshConfirm ();
                            if(!f5Flag){ //选择取消则false
                                event.keyCode=0;
                                return false;
                            }

                        }
                    }else{
                        refreshConfirm ();
                        if(!f5Flag){ //选择取消则false
                            event.keyCode=0;
                            return false;
                        }
                    }
                }
                if(event.keyCode==8){//进行输入回格符时
                    var afterinitform= $(".J_leave_page_prompt").serialize().toString();
                    act = document.activeElement.tagName;
                    if(!(initform==afterinitform)){
                        if((act!="INPUT")&&(act!="TEXTAREA")){
                            //弹出对话框
                            backspaceConfirm();
                                if(!backspaceFlag){
                                    event.keyCode=0;
                                    return false;
                                }
                        }
                    }else{
                        if(act=="BUTTON"||act=="DIV"){
                            event.keyCode=0;
                            return false;
                        }

                    }

                }
            });
        }
        }
    }
});
