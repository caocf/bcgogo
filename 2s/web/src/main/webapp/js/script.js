/**
 * @author ??? 失踪中
 * @adjuster 潘震
 * @description 本页面尚需逐步重构，如有问题，请及时联系
 */

//TODO 该文件本为维修单页面所建，现有多处使用
var debug = false;
var idPrefixLastModified = null;
var operation = null;

function repaireOrderAdd() {
    idPrefixLastModified = null;
    addNewRow(0);
    isShowAddButton2();
}


$(document).ready(function () {
    var $id_iframe_PopupBox = $("#iframe_PopupBox");
    if ($id_iframe_PopupBox.length > 0) {
        var $div_close = $("#div_close", $id_iframe_PopupBox[0].contentWindow.document);
        if ($div_close.length > 0) {
            $div_close.click(function () {
                $("#mask").css('display', 'none');
                $id_iframe_PopupBox.css('display', 'none');
                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            });
        }
    }
    var $id_input_clientInfo = $("#input_clientInfo");
    if ($id_input_clientInfo.length > 0) {
        $id_input_clientInfo.click(function () {

            if ($("#customerId").val()) {

                var r = APP_BCGOGO.Net.syncGet({async:false,url:"customer.do?method=checkCustomerStatus",data:{customerId: $("#customerId").val(),now:new Date()},dataType:"json"});

                if (!r.success) {
                    alert("此客户已被删除或合并，不能看信息！");
                    return;
                }
            }

            bcgogo.checksession({
                "parentWindow":window.parent,
                'iframe_PopupBox':$("#iframe_moreUserInfo")[0],
                'src':"txn.do?method=clientInfo&customer=" + $("#customerName").val()
                    + "&mobile="+ $("#mobile").val()
                    + "&customerId=" + $("#customerId").val()
                    + "&contact=" + $("#contact").val()
                    + "&landLine=" + $("#landLine").val()
                    + "&vehicleContact=" + $("#vehicleContact").val()
                    + "&licenceNo=" + $("#licenceNo").val()
                    + "&vehicleMobile=" + $("#vehicleMobile").val()
                    + "&brand=" + $("#brand").val()
                    + "&model=" + $("#model").val()
                    + '&color=' + $('#vehicleColor').val()
                    + '&chassisNumber=' + $('#vehicleChassisNo').val()
                    + '&engineNo=' + $('#vehicleEngineNo').val()

            });
        });
    }

    // TODO 不准使用拼音!!!!
    var $id_a_jiesuan = $("#a_jiesuan");
    if ($id_a_jiesuan.length > 0) {
        $id_a_jiesuan.click(function () {
            bcgogo.checksession({
                "parentWindow":window.parent,
                'iframe_PopupBox':$("#iframe_qiankuan")[0],
                'src':"arrears.do?method=toReceivableSettle&customerId=" + $("#customerId").val()
            });
        });
    }

    var $id_input_makeTime = $("#input_makeTime");
    if ($id_input_makeTime.length > 0) {
        $id_input_makeTime.click(function () {
            bcgogo.checksession({
                "parentWindow":window.parent,
                'iframe_PopupBox':$("#iframe_PopupBoxMakeTime")[0],
                'src':"txn.do?method=makeTime&orderId=" + repairOrderId
            });
        });
    }

    var $id_input_service = $("#input_service");
    if ($id_input_service.length > 0) {
        $id_input_service.click(function () {
            bcgogo.checksession({
                "parentWindow":window.parent,
                'iframe_PopupBox':$id_iframe_PopupBox[0],
                'src':"txn.do?method=service&customerId=" + customerId + "&vehicleId=" + vehicleId
            });
        });
    }

    if ($("#td_name").length > 0) {
        $("#td_name").click(function () {
            if ($("#span_name").length > 0) {
                $("#span_name").css('display', 'none');
            }
            if ($("#input_name").length > 0) {
                $("#input_name")
                    .css('display', 'block')
                    .focus()
                    .bind("blur", function () {
                        $("#span_name")
                            .html($(this).val())
                            .css('display', 'block');
                        $(this).css('display', 'none');
                    })
                    .bind("keyup", function () {
                        $("#div_brand")
                            .css('display', 'block')
                            .css('position', 'absolute')
                            .css('left', G.getX(this) + "px")
                            .css('top', G.getY(this) + this.offsetHeight + 1 + "px")
                            .css('width', $("#input_name").offsetWidth - 2 + "px")
                            .html("");

                        for (var i = 0; i < 5; i++) {
                            var $node_p = $("<p>");
                            $node_p
                                .bind("mouseleave", function (event) {
                                $(this).css("backgroundColor", "#FFFFFF");
                            })
                                .bind("mouseenter", function (event) {
                                    $(this).css("backgroundColor", "#2166ed");
                                })
                                .bind("click", function (event) {
                                    $("#input_name")
                                        .val($(this).html())
                                        .blur();
                                    if ($("#div_brand").length > 0) {
                                        $("#div_brand").css("display", "none");
                                    }
                                })
                                .text("asdfasdfasdf");

                            $("#div_brand").append($node_p);
                        }

                        //弹出复选框的最后一项
                        var $node_div = $("<div>");
                        $node_div
                            .bind("mouseleave", function () {
                            $(this).css('backgroundColor', '#FFFFFF');
                        })
                            .bind("mouseenter", function () {
                                $(this).css('backgroundColor', '#2166ed');
                            })
                            .bind("click", function () {
                                if ($("#div_brand").length > 0) {
                                    $("#div_brand").css('display', 'none');
                                }

                                $("#iframe_PopupBox")
                                    .css('display', 'block')
                                    .attr("src", "searchBand.htm");
                                Mask.Login();
                            })
                            .text("更多");
                        $("#div_brand").append($node_div);
                    });
            }
        });
    }
    if ($("#td_brand").length > 0) {
        $("#td_brand").bind("click", function () {
            searchSuggestion($("#brand")[0], $("#brand").val(), $("#model").val(), $("#year").val(), $("#engine").val(), "click");
        });
    }
    if ($("#td_model").length > 0) {
        $("#td_model").bind("click", function () {
            searchSuggestion($("#model")[0], $("#brand").val(), $("#model").val(), $("#year").val(), $("#engine").val(), "click");
        });
    }
    if ($("#td_year").length > 0) {
        $("#td_year").click(function () {
            $("#span_year").css('display', 'none');
            $("#year")
                .css('display', 'block')
                .focus()
                .bind("blur", function () {
                    $(this).css('display', 'none');
                    $("#span_year")
                        .html(this.value)
                        .css('display', 'block');
                });
            searchSuggestion($("#year")[0], $("#brand").val(), $("#model").val(), $("#year").val(), $("#engine").val(), "click");

        });
    }
    if ($("#td_engine").length > 0) {
        $("#td_engine").bind("click", function () {
            $("#span_engine")
                .css('display', "none")
                .bind("blur", function () {
                    $("#span_engine")
                        .html(this.value)
                        .css('display', "block");
                    $(this).css('display', "none");
                });
            $("#engine")
                .css('display', "block")
                .focus();
            searchSuggestion($("#engine")[0], $("#brand").val(), $("#model").val(), $("#year").val(), $("#engine").val(), "click");
        });
    }

    $(".itemAmount").live("focus", function () {        //todo qiuxinyu    adjuster zhen.pan
        checkProductInventoryData($(this));
    });
});
/**
 *  根据以下字段信息校验产品库存数据
 *  "productName","productBrand","productSpec","productModel",
 *  "vehicleBrand","vehicleModel","vehicleYear","vehicleEngine"
 * @param $domObject
 * 此方法，被我拷贝一份到了common.js 稍有不同，如果维护请同步
 */
function checkProductInventoryData($domObject) {
    var idPrefix = $domObject.attr("id");
    idPrefix = idPrefix.substring(0, idPrefix.indexOf("."));
    var productId=$("#" + idPrefix + "\\.productId").val();
    if(verifyProductThroughOrderVersion(getOrderType())){
        if (!G.Lang.isEmpty(productId)&&$("#" + idPrefix + "_supplierInfo [name$='relatedSupplierId']").size()==0){
            getSupplierInventory($("#" + idPrefix + "\\.productId"));
            if(getOrderStatus()=="STOCKING"&&getOrderType()=="SALE"){
                $("[id$='_supplierInfo'] .useRelatedAmount").blur();
            }
            return;
        }

    }
    if (!G.Lang.isEmpty(productId)){
        return;
    }
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
	    if(!$("#" + idPrefix + "\\.productName").val()){
		    return;
	    }
        prepareData[dataMapNameArr[i]] = $("#" + idPrefix + "\\" + subIdNameArr[i]).val();
    }


    var storehouseId = "";
    if($(".j_checkStoreHouse")){
        storehouseId = $(".j_checkStoreHouse").val();
    }
    prepareData["storehouseId"] = storehouseId;
    APP_BCGOGO.Net.syncPost({
        url:"searchInventoryIndex.do?method=ajaxInventorySearchIndex",
        data:prepareData,
        dataType:"json",
        cache: false,
        success:function (data) {
            if (G.isUndefined(data) || G.isNull(data.productId)) {
                if(isCanEditUnit()){
                    $("#" + idPrefix + "\\.unit").val(G.normalize(data.unit));
                }
                $("#" + idPrefix + "\\.inventoryAmount").val("0").css('display', 'none');
                $("#" + idPrefix + "\\.inventoryAmountSpan").html("").css('display', 'none');
                if ($("#" + idPrefix + "\\.inventoryAveragePrice")) {
                    $("#" + idPrefix + "\\.inventoryAveragePrice").val(0);
                }
                if($("#" + idPrefix + "\\.inventoryAmount")[0]){
                    var parentNode = $("#" + idPrefix + "\\.inventoryAmount").parent()[0];
                    var childrenCnt = parentNode.childNodes.length;
                    for (var i = 0; i < childrenCnt; i++) {
                        var childNode = parentNode.childNodes[i];
                        if (childNode.tagName != null && childNode.tagName.toLowerCase() == "span") {
                            if (childNode.id != (idPrefix + ".inventoryAmountSpan")) {
                                childNode.style.display = "inline";
                            }
                        }
                    }
                }

                if ($("#" + idPrefix + "\\.yuliu").length > 0) {
                    $("#" + idPrefix + "\\.yuliu").css('display', "inline");
                }
            } else {
                var itemUsedUnit = G.normalize($("#" + idPrefix + "\\.unit").val());
                var itemUsePurchasePrice = dataTransition.rounding(data.purchasePrice, 2);
                var itemInventoryAmount = dataTransition.rounding(data.amount, 2);
                var recommendedPrice = dataTransition.rounding(data.recommendedPrice, 2);
                var lowerLimit = dataTransition.rounding(data.lowerLimit, 0);
                var upperLimit = dataTransition.rounding(data.upperLimit, 0);
                var tradePrice = dataTransition.simpleRounding(data.tradePrice, 2);
                var inventoryAveragePrice = dataTransition.simpleRounding(data.inventoryAveragePrice, 2);
                if (!G.isEmpty(itemUsedUnit) && itemUsedUnit == data.storageUnit && data.storageUnit != data.sellUnit && data.rate != 0) {     //填写的单位是大单位时
                    inventoryAveragePrice = dataTransition.simpleRounding(data.inventoryAveragePrice * data.rate, 2);
                    itemUsePurchasePrice = dataTransition.rounding(data.purchasePrice * data.rate, 2);
                    itemInventoryAmount = dataTransition.rounding(data.amount / data.rate, 2);
                    recommendedPrice = dataTransition.rounding(data.recommendedPrice * data.rate, 2);
                    tradePrice = dataTransition.simpleRounding(data.tradePrice * data.rate, 2);
                    lowerLimit = dataTransition.rounding(dataTransition.rounding(data.lowerLimit, 0) / data.rate, 0);
                    upperLimit = dataTransition.rounding(dataTransition.rounding(data.upperLimit, 0) / data.rate, 0);
                } else if (!G.isEmpty(itemUsedUnit) && itemUsedUnit != data.storageUnit && itemUsedUnit != data.sellUnit && !G.isEmpty(data.sellUnit) ) { //填写的单位既不是大单位，也不是小单位
                    $("#" + idPrefix + "\\.unit").val(G.normalize(data.sellUnit));
                }else if(G.isEmpty(itemUsedUnit) && isCanEditUnit()){
                    $("#" + idPrefix + "\\.unit").val(G.normalize(data.unit));
                }
                $("#" + idPrefix + "\\.productId").val(data.productIdStr);
                $("#" + idPrefix + "\\.inventoryAmount").val(itemInventoryAmount);
                $("#" + idPrefix + "\\.purchasePrice").val(itemUsePurchasePrice);
                $("#" + idPrefix + "\\.inventoryAmountSpan").html(dataTransition.rounding(itemInventoryAmount, 2));
                $("#" + idPrefix + "\\.inventoryAmount").css('display', "block");
                $("#" + idPrefix + "\\.inventoryAmountSpan").css('display', "inline");
                $("#" + idPrefix + "\\.storageUnit").val(G.normalize(data.storageUnit));
                $("#" + idPrefix + "\\.sellUnit").val(G.normalize(data.sellUnit));
                $("#" + idPrefix + "\\.rate").val(G.normalize(data.rate));
                if ($("#" + idPrefix + "\\.businessCategoryName")) {
                    $("#" + idPrefix + "\\.businessCategoryName").val(G.normalize(data.businessCategoryName));
                    $("#" + idPrefix + "\\.businessCategoryName").attr("hiddenValue",$("#" + idPrefix + "\\.businessCategoryName").val());
                    $("#" + idPrefix + "\\.businessCategoryId").val(G.normalize(data.businessCategoryIdStr));
                }

                if ($("#" + idPrefix + "\\.productKind")) {
                    $("#" + idPrefix + "\\.productKind").val(G.normalize(data.kindName));
                }

                if ($("#" + idPrefix + "\\.lowerLimit").length > 0) {
                    $("#" + idPrefix + "\\.lowerLimit").val(lowerLimit);
                }
                if ($("#" + idPrefix + "\\.upperLimit").length > 0) {
                    $("#" + idPrefix + "\\.upperLimit").val(upperLimit);
                }
                    if( typeof(initUnitTd) === "function"){
                        initUnitTd($("#" + idPrefix + "\\.unit")[0]);
                    }


                if ($("#" + idPrefix + "\\.inventoryAmount")[0] && $("#" + idPrefix + "\\.inventoryAmount")[0].parentNode) {
                    var parentNode = $("#" + idPrefix + "\\.inventoryAmount")[0].parentNode;
                    var childrenCnt = parentNode.childNodes.length;
                    for (var i = 0; i < childrenCnt; i++) {
                        var childNode = parentNode.childNodes[i];
                        if (childNode.tagName != null && childNode.tagName.toLowerCase() == "span") {
                            if (childNode.id != (idPrefix + ".inventoryAmountSpan")) {
                                $(childNode).css('display', "none");
                            }
                        }
                    }
                }
                if ($("#" + idPrefix + "\\.recommendedPrice")) {
                    $("#" + idPrefix + "\\.recommendedPrice").val(recommendedPrice);
                }
                if ($("#" + idPrefix + "\\.storageBin") && data.storageBin) {
                    $("#" + idPrefix + "\\.storageBin").val(data.storageBin);
                }
                if ($("#" + idPrefix + "\\.storageBinSpan") && data.storageBin) {
                    $("#" + idPrefix + "\\.storageBinSpan").html(data.storageBin);
                }
                if ($("#" + idPrefix + "\\.tradePrice") && tradePrice) {
                    $("#" + idPrefix + "\\.tradePrice").val(tradePrice);
                }
                if ($("#" + idPrefix + "\\.inventoryAveragePrice")) {
                    $("#" + idPrefix + "\\.inventoryAveragePrice").val(inventoryAveragePrice);
                }
                var $id_price = $("#" + idPrefix + "\\.price");
                if ($id_price.length > 0 && G.contains(G.trim($id_price.val()), ["", "0", "0.0"]) ) {
                    $id_price.val(recommendedPrice);
                } else if ($id_price.length && Number($id_price.val()) < inventoryAveragePrice) {
                    // TODO 逻辑有错误 ，需求是这样的 需要在多次点击时 只弹出一次
                    if (getOrderType() != "RETURN" && getOrderType() != "SALE_RETURN" && getOrderType() !="BORROW_ORDER") {
                        nsDialog.jAlert("友情提示：该商品的销售价低于成本均价" + dataTransition.rounding(inventoryAveragePrice, 2) + "元，请核查！");
                    }
                }
                $("#" + idPrefix + "\\.commodityCode").val(G.normalize(data.commodityCode));
            }

            if ($("#orderType").val() == "purchaseReturnOrder") {
                $("#" + idPrefix + "\\.inventoryAmount").css('display', "inline");
                $("#" + idPrefix + "\\.inventoryAveragePrice").text(inventoryAveragePrice);

            }else  if (getOrderType() == "ALLOCATE_RECORD") {
                $("#" + idPrefix + "\\.inventoryAmount").css('display', "inline");
                $("#" + idPrefix + "\\.costPrice").val(inventoryAveragePrice);
            }else  if (getOrderType() == "INNER_PICKING") {
                $("#" + idPrefix + "\\.inventoryAmountSpan").css('display', "inline");
                $("#" + idPrefix + "\\.price_span").text(inventoryAveragePrice);
                $("#" + idPrefix + "\\.price").val(inventoryAveragePrice);
                $("#" + idPrefix + "\\.unit").val(G.normalize(data.sellUnit));
                $("#" + idPrefix + "\\.unit_span").text(G.normalize(data.sellUnit));
            }else  if (getOrderType() == "INNER_RETURN") {
                $("#" + idPrefix + "\\.price_span").text(inventoryAveragePrice);
                $("#" + idPrefix + "\\.price").val(inventoryAveragePrice);
            }else if(getOrderType() == "INVENTORY_CHECK"){

                $("#" + idPrefix + "\\.inventoryAmountUnit").val(G.normalize(itemInventoryAmount)+G.normalize(data.sellUnit));

            }



            if ($.isFunction(window.setTotal)) {
                setTotal();
            }
            if(verifyProductThroughOrderVersion(getOrderType())){
                getSupplierInventory($("#" + idPrefix + "\\.productId"));
            }
            activeRecommendSupplierTip($("#" + idPrefix + "\\.inventoryAmount").parent().parent(".item "), getOrderType());
        },
        error:function () {
            alert("网络异常，请重新尝试!");
        }
    });

}

var trCount1 = 0;
//材料单号默认一行        //flag: repairSale==>维修单中的销售单，处理特例。其余状态如常
function addNewRow(flag) {
    idPrefixLastModified = null;
    if (flag == "repairSale") {
        trCount1 = 0;
    }

    // 建立一个 行单元
    var tr = $("<tr class='bg titBody_Bg item1 table-row-original'></tr>")[0];
    $("#table_productNo_2").append($(tr));

    // 建立一个 列单元格
    var commodityCode_td = $("<td style='borderLeft:none;min-width:100px;'>   </td>")[0];
    $(tr).append($(commodityCode_td));

    var commodityCode_input = $("<input type='text' autocomplete='off' class='table_input checkStringEmpty' style='width:85%;' maxlength='20' id='itemDTOs" + trCount1 + ".commodityCode' name='itemDTOs[" + trCount1 + "].commodityCode'  />")[0];

    commodityCode_td.appendChild(commodityCode_input);

    // 加入 hidden 的 ID 字段
    commodityCode_td.appendChild($("<input type='hidden' class='table_input' id='itemDTOs" + trCount1 + ".id' name='itemDTOs[" + trCount1 + "].id' />")[0]);

    // 销售单加入 hidden 的 templateItemIdStr 字段
    commodityCode_td.appendChild(generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".templateItemIdStr",
        "name":"itemDTOs[" + trCount1 + "].templateItemIdStr",
        "type":"hidden",
        "className":"table_input"
    }));

    var productType_input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".productType",
        "name":"itemDTOs[" + trCount1 + "].productType",
        "type":"hidden",
        "className":"table_input"
    });
    commodityCode_td.appendChild(productType_input);

    var purchasePrice_input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".purchasePrice",
        "name":"itemDTOs[" + trCount1 + "].purchasePrice",
        "type":"hidden",
        "value":"0",
        "className":"table_input"
    });
    commodityCode_td.appendChild(purchasePrice_input);

    var inventoryAveragePrice_input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".inventoryAveragePrice",
        "name":"itemDTOs[" + trCount1 + "].inventoryAveragePrice",
        "type":"hidden"
    });
    commodityCode_td.appendChild(inventoryAveragePrice_input);

    var productId_input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".productId",
        "name":"itemDTOs[" + trCount1 + "].productId",
        "type":"hidden",
        "className":"table_input"
    });
    commodityCode_td.appendChild(productId_input);

    commodityCode_td.appendChild(generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".vehicleBrand",
        "name":"itemDTOs[" + trCount1 + "].vehicleBrand",
        "type":"hidden",
        "className":"table_input"
    }));

    commodityCode_td.appendChild(generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".vehicleModel",
        "name":"itemDTOs[" + trCount1 + "].vehicleModel",
        "type":"hidden",
        "className":"table_input"
    }));

    commodityCode_td.appendChild(generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".vehicleYear",
        "name":"itemDTOs[" + trCount1 + "].vehicleYear",
        "type":"hidden",
        "className":"table_input"
    }));

    commodityCode_td.appendChild(generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".vehicleEngine",
        "name":"itemDTOs[" + trCount1 + "].vehicleEngine",
        "type":"hidden",
        "className":"table_input"
    }));


    var td = generateNode("td", {});
    var inputProductName = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".productName",
        "name":"itemDTOs[" + trCount1 + "].productName",
        "maxlength":"50",
        "type":"text",
        "autocomplete":"off",
        "className":"table_input checkStringEmpty",
        "css":{"width":"85%"},
        "focus":function () {
            this.parentNode.lastChild.value = this.value;
            inputEditButton.value = "";
        }
    });
    td.appendChild(inputProductName);
    tr.appendChild(td);


    function styleselect1Click() {
        searchSuggestion3(inputArray, 1, "", "product_name", "", "", "", "", trCount1, 0);
    }

    function styleselect1Change() {
        modify(this);
    }

    function styleselect1(domObj, e) {
        var pos = getCursorPosition(domObj);
        inputProductName.value = inputProductName.value.replace(/[\ |\\]/g, "");
        var searchValue = G.normalize(G.trim(inputProductName.value), "", ["(无)"]);
        searchSuggestion3(inputArray, 1, searchValue, "product_name", "", "", "", "", trCount1, 0);
        setCursorPosition(domObj, pos);
    }

    td.appendChild(generateNode("input", {
        "id":'product_name;' + trCount1,
        "name":'product_name;' + trCount1,
        "type":"hidden"
    }));


    var td = generateNode("td", {});
    var inputBrand = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".brand",
        "name":"itemDTOs[" + trCount1 + "].brand",
        "type":"text",
        "autocomplete":"off",
        "className":"table_input checkStringEmpty",
        "css":{"width":"100%"},
        "focus":function () {
            this.parentNode.lastChild.value = this.value;
        }
    });
    td.appendChild(inputBrand);
    tr.appendChild(td);


    function styleselect2Click() {
        var input1value = G.normalize(G.trim(inputProductName.value), "", ["(无)"]);
        searchSuggestion3(inputArray, 2, "", "product_brand", input1value, "", "", "", trCount1, 0);
    }

    function styleselect2Change() {
        modify(this);
    }

    function styleselect2(domObj, e) {
        var pos = getCursorPosition(domObj);
        inputBrand.value = inputBrand.value.replace(/[\ |\\]/g, "");
        var input1value = G.normalize(G.trim(inputProductName.value), "", ["(无)"]);
        var searchValue = G.normalize(G.trim(inputBrand.value), "", ["(无)"]);
        searchSuggestion3(inputArray, 2, searchValue, "product_brand", input1value, "", "", "", trCount1, 0);
        setCursorPosition(domObj, pos);
    }


    td.appendChild(generateNode("input", {
        "id":'product_brand;' + trCount1,
        "name":'product_brand;' + trCount1,
        "type":"hidden"
    }));


    var td = generateNode("td", {});
    var inputSpec = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".spec",
        "name":"itemDTOs[" + trCount1 + "].spec",
        "type":"text",
        "autocomplete":"off",
        "className":"table_input checkStringEmpty",
        "css":{"width":"100%"},
        "focus":function () {
            this.parentNode.lastChild.value = this.value;
        }
    });
    td.appendChild(inputSpec);
    tr.appendChild(td);


    function styleselect3Click() {
        var input1value = G.normalize(G.trim(inputProductName.value), "", ["(无)"]);
        var input2value = G.normalize(G.trim(inputBrand.value), "", ["(无)"]);
        searchSuggestion3(inputArray, 3, "", "product_spec", input1value, input2value, "", "", trCount1, 0);
    }

    function styleselect3Change() {
        modify(this);
    }

    function styleselect3(domObj, e) {
        var pos = getCursorPosition(domObj);
        inputSpec.value = inputSpec.value.replace(/[\ |\\]/g, "");
        var input1value = G.normalize(G.trim(inputProductName.value), "", ["(无)"]);
        var input2value = G.normalize(G.trim(inputBrand.value), "", ["(无)"]);
        var searchValue = G.normalize(G.trim(inputSpec.value), "", ["(无)"]);
        searchSuggestion3(inputArray, 3, searchValue, "product_spec", input1value, input2value, "", "", trCount1, 0);
        setCursorPosition(domObj, pos);
    }


    td.appendChild(generateNode("input", {
        "id":'product_spec;' + trCount1,
        "name":'product_spec;' + trCount1,
        "type":"hidden"
    }));

    var td = generateNode("td", {});
    var inputModel = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".model",
        "name":"itemDTOs[" + trCount1 + "].model",
        "type":"text",
        "autocomplete":"off",
        "className":"table_input checkStringEmpty",
        "css":{"width":"100%"},
        "focus":function () {
            this.parentNode.lastChild.value = this.value;
        }
    });
    td.appendChild(inputModel);
    tr.appendChild(td);


    function styleselect4Click() {
        var input1value = G.normalize(G.trim(inputProductName.value), "", ["(无)"]);
        var input2value = G.normalize(G.trim(inputBrand.value), "", ["(无)"]);
        var input3value = G.normalize(G.trim(inputSpec.value), "", ["(无)"]);
        searchSuggestion3(inputArray, 4, "", "product_model", input1value, input2value, input3value, "", trCount1, 0);
    }

    function styleselect4Change() {
        modify(this);
    }

    function styleselect4(domObj, e) {
        var pos = getCursorPosition(domObj);
        inputModel.value = inputModel.value.replace(/[\ |\\]/g, "");
        var input1value = G.normalize(G.trim(inputProductName.value), "", ["(无)"]);
        var input2value = G.normalize(G.trim(inputBrand.value), "", ["(无)"]);
        var input3value = G.normalize(G.trim(inputSpec.value), "", ["(无)"]);
        var searchValue = G.normalize(G.trim(inputModel.value), "", ["(无)"]);
        searchSuggestion3(inputArray, 4, searchValue, "product_model", input1value, input2value, input3value, "", trCount1, 0);
        setCursorPosition(domObj, pos);
    }


    // TODO TODO TODO TODO TODO TODO TODO TODO
    td.appendChild(generateNode("input", {
        "id":'product_model;' + trCount1,
        "name":'product_model;' + trCount1,
        "type":"hidden"
    }));

    var td = generateNode("td", {});
    var inputPrice = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".price",
        "name":"itemDTOs[" + trCount1 + "].price",
        "type":"text",
        "value":"",
        "className":"itemPrice table_input checkNumberEmpty",
        "css":{"width":"100%"}
    });
    inputPrice['data-filter-zero'] = true;
    td.appendChild(inputPrice);
    tr.appendChild(td);

    var td = generateNode("td", {});
    var input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".amount",
        "name":"itemDTOs[" + trCount1 + "].amount",
        "type":"text",
        "className":"itemAmount table_input checkNumberEmpty",
        "css":{"width":"100%"}
    });
    input['data-filter-zero'] = true;
    input.onblur = function() {
        dataTransition.roundingSpanNumber("totalSpan");
        var format = $(this).val();
        format = dataTransition.rounding(format, 2);
        $(this).val() && $(this).val(format);
        //BCSHOP-2029
        setTotal();
    };
    td.appendChild(input);
    tr.appendChild(td);


    var input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".amountHid",
        "name":"itemDTOs[" + trCount1 + "].amountHid",
        "type":"hidden",
        "value":"0",
        "className":"table_input"
    });
    input.onblur = function() {
        dataTransition.roundingSpanNumber("totalSpan");
        var format = $(this).val();
        format = dataTransition.rounding(format, 2);
        $(this).val(format);
        //BCSHOP-2029
        setTotal();
    };
    td.appendChild(input);


    var td = generateNode("td", {});
    var input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".unit",
        "name":"itemDTOs[" + trCount1 + "].unit",
        "type":"text",
        "value":"",
        "className":"itemUnit table_input checkStringEmpty",
        "css":{"width":"100%"}
    });
    td.appendChild(input);
    tr.appendChild(td);

    var input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".sellUnit",
        "name":"itemDTOs[" + trCount1 + "].sellUnit",
        "type":"hidden",
        "value":"",
        "className":"itemSellUnit",
        "css":{"width":"100%"}
    });
    td.appendChild(input);


    var input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".storageUnit",
        "name":"itemDTOs[" + trCount1 + "].storageUnit",
        "type":"hidden",
        "value":"",
        "className":"itemStorageUnit",
        "css":{"width":"100%"}
    });
    td.appendChild(input);


    var input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".rate",
        "name":"itemDTOs[" + trCount1 + "].rate",
        "type":"hidden",
        "value":"",
        "className":"itemRate",
        "css":{"width":"100%"}
    });
    td.appendChild(input);

    var td = generateNode("td", {});
    var input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".total",
        "name":"itemDTOs[" + trCount1 + "].total",
        "type":"hidden",
        "value":"",
        "className":"itemTotal table_input",
        "readOnly":"true",
        "css":{"width":"100%"}
    });
    var span = generateNode("span",{
        "id":"itemDTOs" + trCount1 + ".total_span",
        "name":"itemDTOs[" + trCount1 + "].total",
        "className":"itemTotalSpan"
    });
    td.appendChild(input);
    td.appendChild(span);
    tr.appendChild(td);

    //仓位td
    var td = generateNode("td", {
        "className":"storage_bin_td"
    });
    var span1 = generateNode("span", {
        "id":"itemDTOs" + trCount1 + ".storageBinSpan",
        "name":"itemDTOs[" + trCount1 + "].storageBinSpan",
        "css":{"display":"block"}
    });
    td.appendChild(span1);
    tr.appendChild(td);

    var td = generateNode("td", {});
    var span1 = generateNode("span", {
        "id":"itemDTOs" + trCount1 + ".inventoryAmountSpan",
        "name":"itemDTOs[" + trCount1 + "].inventoryAmountSpan",
        "css":{"display":"block"}
    });
    td.appendChild(span1);
    tr.appendChild(td);


    var inputInventoryAmount = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".inventoryAmount",
        "name":"itemDTOs[" + trCount1 + "].inventoryAmount",
        "type":"hidden",
        "value":"0",
        "className":"itemInventoryAmount",
        "readOnly":"true",
        "css":{"width":"100%"}
    });
    td.appendChild(inputInventoryAmount);

    var input = generateNode("input", {
        "id":"inventoryAmountHid",
        "name":"inventoryAmountHid",
        "type":"hidden",
        "value":"0"
    });
    td.appendChild(input);

    var span = generateNode("span", {
        "innerHTML":"新",
        "css":{"display":"none"},
        "className":"j_new"
    });
    td.appendChild(span);

    //=================

    var td = generateNode("td", {
        "css":{
            "padding-right":"6px",
            "fontSize":"12px"
        }
    });

    var span = generateNode("span", {
        "className":"reserved",
        "innerHTML":""
    });

    var input = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".yuliu",
        "type":"button",
        "className":"lackMaterial",
        "css":{"display":"none", "width":"40px"},
        "focus":function () {
            this.blur();
        }
    });

    var input_reserved = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".reserved",
        "name":"itemDTOs[" + trCount1 + "].reserved",
        "type":"hidden"
    });

    td.appendChild(span);
    td.appendChild(input);
    td.appendChild(input_reserved);
    tr.appendChild(td);
    //=====================


    var td = generateNode("td", {});

    var input_businessCategoryName = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".businessCategoryName",
        "name":"itemDTOs[" + trCount1 + "].businessCategoryName",
        "type":"text",
        "value":"",
        "hiddenValue":"",
        "className":"table_input businessCategoryName"
    });

    var input_businessCategoryId = generateNode("input", {
        "id":"itemDTOs" + trCount1 + ".businessCategoryId",
        "name":"itemDTOs[" + trCount1 + "].businessCategoryId",
        "type":"hidden",
        "value":""
    });

    td.appendChild(input_businessCategoryName);
    td.appendChild(input_businessCategoryId);
    tr.appendChild(td);
    var td = generateNode("td", {
        "css":{"border-right":"none"}
    });


//  var input = $("<a>")[0];
//  input.className = "opera1";
//  input.id = "serviceDTOs" + newServiceItemCnt + ".deletebutton";
//  input.name = "serviceDTOs[" + newServiceItemCnt + "].deletebutton";
//  input.innerHTML = "删除";
//  input.onfocus = function() {
//      this.blur();
//  };
//


    var input = generateNode("a", {
        "id":"itemDTOs" + trCount1 + ".opera1Btn",
//        "type":"button",
        "innerHTML":"删除",
        "className":"opera1",
        "css":{
            "width":"20px",
            "display":"inline"
        },
        "focus":function () {
            this.blur();
        }
    });
    td.appendChild(input);
    tr.appendChild(td);


    if ($("#licenceNo") != null) {
        var inputEditButton = generateNode("input", {
            "id":"itemDTOs" + trCount1 + ".editbutton",
            "name":"itemDTOs[" + trCount1 + "].editbutton",
            "type":"button",
            "className":"edit1",
            "css":{"margin-left":"6px"},
            "click":function () {
                searchInventoryIndex(this);
            }
        });
        $("#table_productNo_2 tr:last").find("td:eq(1)").append(inputEditButton);

        $("#table_productNo_2 tr:last").find("td:first").append(generateNode("input", {
            "id":"itemDTOs" + trCount1 + ".isNewAdd",
            "name":"itemDTOs[" + trCount1 + "].isNewAdd",
            "type":"hidden"
        }));
    }

    // 总共 8 个
    var inputArray = [
        inputProductName, inputBrand, inputSpec, inputModel,
        inputInventoryAmount, productId_input, productType_input, purchasePrice_input
    ];
    trCount1++;
    $(tr).find("input[type='text']").each(function (i) {
        //去除文本框的自动填充下拉框
        $(this).attr("autocomplete", "off");
    });
    
//    tableUtil.tableStyle('#table_productNo_2','.i_tabelBorder,.table_title');

//  var str = '<tr class="titBottom_Bg"><td colspan="14"></td></tr>';
//   $("#table_productNo_2").append($(str));

    return $(tr);
}

//施工单号

function getNewServiceItemNo(){
    var biggestCnt = 0;
    var exist = false;
    $("#table_task input[name$='service']").each(function(){
        exist = true;
        var id = $(this).attr("id");
        var no = id.substring(11, id.indexOf("."));
        if(biggestCnt < no * 1){
            biggestCnt = no;
        }
    });
    return exist == false ? 0: ++biggestCnt;
}
function taskaddNewRow() {
    // TODO 潘震 已经迁移，后期调整
    var tr = $("#table_task")[0].insertRow(-1);
    tr.className = "bg titBody_Bg item table-row-original";

    var newServiceItemCnt = getNewServiceItemNo();

//    var td = tr.insertCell(-1);
//    td.innerHTML = "　";
//    td.style.borderLeft = "none";

    var td = tr.insertCell(-1);
    td.style.borderLeft = "none";

    var name = "serviceDTOs[" + newServiceItemCnt + "].id";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".id";
    input.type = "hidden";
    input.className = "table_input";
    td.appendChild(input);


    var name = "serviceDTOs[" + newServiceItemCnt + "].serviceId";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".serviceId";
    input.type = "hidden";
    input.className = "table_input";
    td.appendChild(input);

    var name = "serviceDTOs[" + newServiceItemCnt + "].serviceHistoryId";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".serviceHistoryId";
    input.type = "hidden";
    input.className = "table_input";
    td.appendChild(input);


    var name = "serviceDTOs[" + newServiceItemCnt + "].service";
    var input = null;

    input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".service";
    input.type = "text";
    input.className = "table_input checkStringEmpty J-hide-empty-droplist";
    td.appendChild(input);


    var td = tr.insertCell(-1);
    td.style.display = "none";
    var name = "serviceDTOs[" + newServiceItemCnt + "].consumeType";
    var input = null;
    input = document.createElement("select");
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".consumeType";
    input.options.add(new Option("金额", "MONEY"));
    if ("true" == jQuery("#isMemberSwitchOn").val()) {
        input.options.add(new Option("计次划卡", "TIMES"));
    }
    input.onchange = function() {
        consumeTypeChange(this);
    }
    input.style.width = 120 + "px";
    td.appendChild(input);

    // 标准工时
    var td = tr.insertCell(-1);
    var name = "serviceDTOs[" + newServiceItemCnt + "].standardHours";
    var input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".standardHours";
    input.type = "text";
    input.value = '';
    input['data-filter-zero'] = true;
    input.style.width = 70 + "px";
    if (isCanEditStandardHours()) {
        input.className = 'table_input standardHours checkNumberEmpty';
    } else {
        $(input).attr("style","border:none;background-color:transparent;width:70px;text-align: center;");
        input.readOnly = 'readOnly';
    }
    td.appendChild(input);

     // 工时单价
    var td = tr.insertCell(-1);
    var name = "serviceDTOs[" + newServiceItemCnt + "].standardUnitPrice";
    var input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".standardUnitPrice";
    input.type = "text";
    input.value = '';
    input['data-filter-zero'] = true;
    input.style.width = 70 + "px";
    if (isCanEditStandardHours()) {
        input.className = 'table_input standardUnitPrice checkNumberEmpty';
    } else {
        $(input).attr("style","border:none;background-color:transparent;width:70px;text-align: center;");
        input.readOnly = 'readOnly';
    }
    td.appendChild(input);

     // 实际工时
    var td = tr.insertCell(-1);
    var name = "serviceDTOs[" + newServiceItemCnt + "].actualHours";
    var input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".actualHours";
    input.type = "text";
    input.value = '';
    input['data-filter-zero'] = true;
    input.className = 'table_input actualHours checkNumberEmpty';
    td.appendChild(input);
    var td = tr.insertCell(-1);

    var name = "serviceDTOs[" + newServiceItemCnt + "].total";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".total";
    input.type = "text";
    input.value = '';
    input['data-filter-zero'] = true;
    input.className = 'serviceTotal checkNumberEmpty table_input';
    td.appendChild(input);


    var td = tr.insertCell(-1);
    var span = $("<span>")[0];
    span.className = "workersSpan";
    td.appendChild(span);
    var name = "serviceDTOs[" + newServiceItemCnt + "].workers";

    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".workers";
    input.type = "text";
    input.size = "100";
    input.className = "table_input checkStringEmpty";
    span.appendChild(input);

    var name = "serviceDTOs[" + newServiceItemCnt + "].templateServiceIdStr";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".templateServiceIdStr";
    input.type = "hidden";
    input.className = "table_input";
    td.appendChild(input);

    var img = null;
    img = $("<img>")[0];
    img.src = "images/list_close.png";
    img.className = "deleteWorkers";
    img.style.width = 12 + "px";
    img.style.cursor = "pointer";
    img.style.display = "none";
    span.appendChild(img);

    var name = "serviceDTOs[" + newServiceItemCnt + "].workerId";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".workerId";
    input.type = "hidden";
    span.appendChild(input);

    var td = tr.insertCell(-1);
    var name = "serviceDTOs[" + newServiceItemCnt + "].businessCategoryName";
    var input = null;

    input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".businessCategoryName";
    input.type = "text";
    input.value = '';
    input.className = 'table_input businessCategoryName serviceCategory';
    input.hiddenValue = "";

    td.appendChild(input);

    var name = "serviceDTOs[" + newServiceItemCnt + "].businessCategoryId";
    var input = null;

    input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".businessCategoryId";
    input.type = "hidden";
    input.value = '';

    td.appendChild(input);

    var td = tr.insertCell(-1);
    var name = "serviceDTOs[" + newServiceItemCnt + "].memo";
    var input = null;

    input = $("<input>")[0];
    input.name = name;
    input.id = "serviceDTOs" + newServiceItemCnt + ".memo";
    input.type = "text";
    input.value = '';
    input.className = 'checkNumberEmpty table_input';
    td.appendChild(input);

    var td = tr.insertCell(-1);
    td.style.borderRight = "none";



//  '    <td style="border-right:none;">' +
//  '	<a class="opera1" type="button" id="itemDTOs0.deletebutton" name="itemDTOs[0].deletebutton">删除</a>' +
//  '    </td>' +
//  '</tr>';

    var input = $("<a>")[0];
    input.className = "opera1";
    input.id = "serviceDTOs" + newServiceItemCnt + ".deletebutton";
    input.name = "serviceDTOs[" + newServiceItemCnt + "].deletebutton";
    input.innerHTML = "删除";
    input.onfocus = function() {
        this.blur();
    };


    td.appendChild(input);

    $(tr).find("input[type='text']").each(function (i) {
        //去除文本框的自动填充下拉框
        $(this).attr("autocomplete", "off");
    });
//    tableUtil.tableStyle('#table_task','.i_tabelBorder,.table_title');

//    var str = '<tr class="titBottom_Bg"><td colspan="14"></td></tr>';
//    $("#table_task").append($(str));

    return $(tr);
}


var trCount3 = 0;
//其他费用
function otherIncomeAddNewRow() {


    // TODO 潘震 已经迁移，后期调整
    var tr = $("#table_otherIncome")[0].insertRow(-1);
    tr.className = "titBody_Bg item2 table-row-original";

    var td = tr.insertCell(-1);

    var name = "otherIncomeItemDTOList[" + trCount3 + "].name";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.maxLength="50";
    input.id = "otherIncomeItemDTOList" + trCount3 + ".name";
    input.type = "text";
    input.className = "table_input otherIncomeKindName checkStringEmpty";
    td.appendChild(input);

    var td = tr.insertCell(-1);

    var name = "otherIncomeItemDTOList[" + trCount3 + "].id";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "otherIncomeItemDTOList" + trCount3 + ".id";
    input.type = "hidden";
    td.appendChild(input);

    var name = "otherIncomeItemDTOList[" + trCount3 + "].templateIdStr";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "otherIncomeItemDTOList" + trCount3 + ".templateIdStr";
    input.type = "hidden";
    input.className = "table_input";
    td.appendChild(input);


    var name = "otherIncomeItemDTOList[" + trCount3 + "].templateId";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "otherIncomeItemDTOList" + trCount3 + ".templateId";
    input.type = "hidden";
    input.className = "table_input";
    td.appendChild(input);

    var name = "otherIncomeItemDTOList[" + trCount3 + "].price";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "otherIncomeItemDTOList" + trCount3 + ".price";
    input.type = "text";
    input.className = "table_input otherIncomePrice checkStringEmpty";
    input['data-filter-zero'] = true;
    td.appendChild(input);


    var td = tr.insertCell(-1);
    var tdName = "otherIncomeItemDTOList[" + trCount3 + "].otherIncomeCostPrice";
    var id = "otherIncomeItemDTOList" + trCount3 + ".otherIncomeCostPrice";
    td.innerHTML = '<input type="checkbox" id="otherIncomeItemDTOList' + trCount3 + '.otherIncomeCostPriceCheckbox" class="otherIncomeCostPriceCheckbox" name="otherIncomeItemDTOList[' + trCount3 + '].checkbox" style="float:left; margin-right:4px; margin-top:3px;"/>' +
        '<label for="checkbox"style="float:left; margin-right:4px;"/></label>' +
        '计入成本<span style="display:none;" id="otherIncomeItemDTOList' + trCount3 + '.otherIncomeSpan"><input type="text" id='+id  +' name='+tdName +' class="table_input otherIncomeCostPrice checkStringEmpty" style="width:70px;" data-filter-zero="true"/>' +
        '元</span>';

    var td = tr.insertCell(-1);

    var name = "otherIncomeItemDTOList[" + trCount3 + "].memo";
    var input = null;
    input = $("<input>")[0];
    input.name = name;
    input.id = "otherIncomeItemDTOList" + trCount3 + ".memo";
    input.type = "text";
    input.maxLength="100";
    input.className = "table_input memo checkStringEmpty";
    td.appendChild(input);

    var td = tr.insertCell(-1);
    td.style.borderRight = "none";

    var input = $("<a>")[0];
   input.className = "opera1";
   input.id = "otherIncomeItemDTOList" + trCount3 + ".deletebutton";
   input.name = "otherIncomeItemDTOList[" + trCount3 + "].deletebutton";
   input.innerHTML = "删除";
   input.style.marginLeft= "5px";
   input.onfocus = function() {
       this.blur();
   };

   td.appendChild(input);

    trCount3++;
    $(tr).find("input[type='text']").each(function (i) {
        //去除文本框的自动填充下拉框
        $(this).attr("autocomplete", "off");
    });
//    tableUtil.tableStyle('#table_otherIncome','.i_tabelBorder,.table_title');
    return $(tr);
}


//add by miao.liu
//检查单据是否相同
function checkTheSame(trCount) {
    var trs = $(".item");
    if (!trs) {
        return false;
    }

    var s = '';

    //先获取最后一个
    var cur = '';//当前最后添加的一条记录
    for (var i = trs.length - 1; i >= 0; i--) {
        var inputs = trs[i].getElementsByTagName("input");
        if (!inputs || inputs.length == 0)
            continue;
        var index = inputs[0].name.split(".")[0].substring(inputs[0].name.indexOf('[') + 1, inputs[0].name.indexOf(']'));

        if (i == trs.length - 1) {
//            最后添加的一个
            cur += $("#" + "serviceDTOs" + index + "\\.service").val();
            if ($.trim($("#" + "serviceDTOs" + index + "\\.service").val()) == "") {
                return true;
            }
        } else {
            var older = '';
            older += $("#" + "serviceDTOs" + index + "\\.service").val();
            if (cur == older) {
                return true;
            }
            if ($.trim(older) == "") {
                return true;
            }
        }

    }
    return false;
}

function checkTheSame3(trCount) {
    var trs = $(".item2");

    if (!trs) {
        return false;
    }

    var s = '';

    for (var i = trs.length - 1; i >= 0; i--) {
        var inputs = trs[i].getElementsByTagName("input");
        if (!inputs)
            continue;
        var prefix = inputs[0].id.split(".")[0];
        var name = $("#"+prefix+"\\.name").val();
        var price = $("#"+prefix+"\\.price").val();

        if(!name || !price)
        {
            return true;
        }
    }
    return false;
}

//判断是否显示+按钮
function isShowAddButton() {
    //如果初始化的话就默认加一行
    if ($("#table_task tr").size() <= 2) {
        $("#table_task .opera2").trigger("click");
    }
    $("#table_task tr:not(.titleBg) .opera2").remove();

    var opera1Id = $(".item:last").find("td:last>a[class='opera1']").attr("id");
    if (!opera1Id) return;
    $("#table_task tr:last").find("td:last>a[class='opera1']").after('<a class="opera2" ' +
        ' id="' + opera1Id.split(".")[0] + '.plusbutton">增加</a>');


}

//判断是否显示+按钮
function isShowAddButton2() {
    //如果初始化的话就默认加一行
    if ($("#table_productNo_2 tr").size() <= 2) {
        $("#table_productNo_2 .opera2").trigger("click");
    }

    $("#table_productNo_2 tr:not(.titleBg) .opera2").remove();


  var opera1Id = $("#table_productNo_2 tr:last").find("td:last>a[class='opera1']").attr("id");
  if (!opera1Id) return;

//    $("#table_productNo_2 tr:last")
//        .find("td:last")
//        .append('<input class="opera2" type="button">');


  $("#table_productNo_2 tr:last").find("td:last>a[class='opera1']").after('<a class="opera2" ' +
      ' id="itemDTOs' + (opera1Id.split(".")[0].substring(8)) + '.plusbutton">增加</a>');
}

//判断是否显示+按钮
function isShowAddButton3() {
    //如果初始化的话就默认加一行
    if ($("#table_otherIncome tr").size() <=2) {
        $("#table_otherIncome .opera2").trigger("click");
    }
    $("#table_otherIncome tr:not(.titleBg) .opera2").remove();

  var opera1Id = $("#table_otherIncome tr:last").find("td:last>a[class='opera1']").attr("id");
  if (!opera1Id) return;

  $("#table_otherIncome tr:last").find("td:last>a[class='opera1']").after('<a class="opera2" ' +
      ' id="' + opera1Id.split(".")[0] + '.plusbutton">增加</a>');


//    $("#table_otherIncome tr:last").find("td:last").append('<input class="operaAdd" type="button">');
}


// invocing.jsp
$(document).ready(function () {
    trCount1 = $("#table_productNo_2 tr").size() - 2;
    trCount2 = $("#table_task tr").size() - 2;
    trCount3 = $("#table_otherIncome tr").size() - 2;
    $("#table_task .opera2").live('click', function () {
        if (this.disabled)return;
        if (checkTheSame(trCount2)) {
            alert('施工单有内容重复或为空，请修改或删除');
            return;
        }
        taskaddNewRow();
        isShowAddButton();
    });

    $("#table_task .opera1").live('click', function () {
        if (this.disabled) return;
        var rowIndex = this.parentNode.parentNode.rowIndex;
        $("#table_task")[0].deleteRow(rowIndex);
        setTotal();
        isShowAddButton();
    });

    $("#table_productNo_2 .opera2").live('click', function () {
        if (this.disabled) return;
//        add by miao.liu  材料单重复验证
        //当前行为空行判断
        if (jQuery(this).prev("a[class='opera1']")[0]) {
            idPrefixLastModified = $(this).prev("a[class='opera1']")[0].id.split(".")[0];
            if (G.isEmpty($("#" + idPrefixLastModified + "\\.productName").val())) {
                alert('材料单有重复项目或者空行，请修改或删除。');
                return;
            }
        }
        if (isMaterialSame()) {
            alert('材料单有重复项目或者空行，请修改或删除。');
            return false;
        }

        if ($(this).prev("a[class='opera1']")[0]) {
            idPrefixLastModified = $(this).prev("a[class='opera1']")[0].id.split(".")[0];
            exactSearchInventorySearchIndex();
        }

        addNewRow(0);
        isShowAddButton2();
    });

    $("#table_productNo_2 .opera1").live('click', function () {
        if (this.disabled)
            return;
        var rowIndex = this.parentNode.parentNode.rowIndex;
        var idPrefix = $(this).attr("id").split(".")[0];
        if (debug) alert("[del] cur:" + idPrefix + " last:" + idPrefixLastModified);
        if (idPrefixLastModified == idPrefix) {
            idPrefixLastModified = null;
        }
        $("#table_productNo_2")[0].deleteRow(rowIndex);
        setTotal();
        isShowAddButton2();
    });

    $("#table_otherIncome .opera2").live('click', function () {
        if (this.disabled)return;
        if($("#table_otherIncome tr").size()==2)
        {
            otherIncomeAddNewRow();
            isShowAddButton3();
            return;
        }

        if (checkTheSame3(trCount3)) {
            nsDialog.jAlert('其他费用名称或金额为空，请修改或删除');
            return;
        }
        otherIncomeAddNewRow();
        isShowAddButton3();
    });

    $("#table_otherIncome .opera1").live('click', function () {
        if (this.disabled) return;
        var rowIndex = this.parentNode.parentNode.rowIndex;
        $("#table_otherIncome")[0].deleteRow(rowIndex);
        setTotal();
        isShowAddButton3();
    });

    isShowAddButton();
    isShowAddButton2();
    isShowAddButton3();
})
function addOneRow() {
    addNewRow(0);
    isShowAddButton2();
}
function submitThisForm(flag) {
    if (flag == 1)  //表单标识可以提交时提交
    {
        btnType = "save";
        $(".serviceTotal", parent.document).each(function () {
            $(this).val( G.normalize(G.trim($(this).val()), "", "0.0") );
        });
        $("repairOrderForm", parent.document)[0].submit();
    }
}
function checkEmptyRepairMaterialRow($tr) {
    var propertys = ["productName","brand","spec","model"];
    var itemInfo = "";
    for (var i = 0,len = propertys.length; i < len; i++) {
        itemInfo += $tr.find("input[id$='." + propertys[i] + "']").val();
    }
    return G.isEmpty(itemInfo);
}
//add by miao.liu 材料单是否相同
// 材料单是否相同判断，有相同行返回true，没有返回false
function isMaterialSame() {
    //自动删除最后的空白行
    var $last_tr = $("#table_productNo_2").find("tbody").find("tr:last");
    while ($last_tr.index() >= 3 && checkEmptyRepairMaterialRow($last_tr)) {
        $last_tr.find(".opera1").click();
        $last_tr = $("#table_productNo_2").find("tbody").find("tr:last");
    }
    var isSame = false;
    var cur = "", $lastItem = $(".item1").last();
    cur += $lastItem.find("input[id$='.commodityCode']").eq(0).val() + " , ";
    cur += $lastItem.find("td").eq(1).find("input[id$='.productName']").eq(0).val() + " , ";
    cur += $lastItem.find("td").eq(2).find("input[id$='.brand']").eq(0).val() + " , ";
    cur += $lastItem.find("td").eq(3).find("input[id$='.spec']").eq(0).val() + " , ";
    cur += $lastItem.find("td").eq(4).find("input[id$='.model']").eq(0).val() + " , ";
    cur += $lastItem.find("input[id$='.vehicleBrand']").eq(0).val() + " , ";
    cur += $lastItem.find("input[id$='.vehicleModel']").eq(0).val() + " , ";
    if ($(".item1").size() > 1 && $lastItem.find("td").eq(1).find("input").eq(0).val() == "") {
        return true;
    }
    var item1s = [];
    var productIds = [];
    var productIdIndex = 0;
    $(".item1").each(function (i) {
        var older = "", $this = $(this);
        older += $this.find("input[id$='.commodityCode']").eq(0).val() + " , ";
        older += $this.find("input[id$='.productName']").eq(0).val() + " , ";
        older += $this.find("input[id$='.brand']").eq(0).val() + " , ";
        older += $this.find("input[id$='.spec']").eq(0).val() + " , ";
        older += $this.find("input[id$='.model']").eq(0).val() + " , ";
        older += $this.find("input[id$='.vehicleBrand']").eq(0).val() + " , ";
        older += $this.find("input[id$='.vehicleModel']").eq(0).val() + " , ";
        if ($.inArray(older, item1s) >= 0) {
            isSame = true;
            return true;
        }
        item1s[i] = older;

        var itemProductId = $(".item1").eq(i).find("input[id$='.productId']").eq(0).val();
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

function checkEmptyRepairServiceRow($tr) {
    var propertys = ["service","total","workers"];
    var itemInfo = "";
    for (var i = 0,len = propertys.length; i < len; i++) {
        itemInfo += $tr.find("input[id$='." + propertys[i] + "']").val();
    }
    return G.isEmpty(itemInfo);
}

//施工单是否相同
// 施工单是否相同判断，有相同行返回true，没有返回false
function isRepairSame() {
    //自动删除最后的空白行
    var $last_tr = $("#table_task").find("tbody").find("tr:last");
    while ($last_tr.index() >= 3 && checkEmptyRepairServiceRow($last_tr)) {
        $last_tr.find(".opera1").click();
        $last_tr = $("#table_task").find("tbody").find("tr:last");
    }
    var isSame = false;
    var cur = "";
    cur = cur + $(".item").last().find("td").eq(0).find("input[id$='service']").val();      //施工单的service输入框的值
    if ($(".item").size() > 1 && $(".item").last().find("td").eq(0).find("input[id$='service']").val() == "") {
        return true;
    }
    var items = [];
    $(".item", $('#table_task')).each(function (i) {
        var older = "";
        older = older + $(this).find("td").eq(0).find('input[id$=service]').val();
        if ($.inArray(older, items) >= 0) {
            isSame = true;
            return true;
        }
        items[i] = older;
    });
    return isSame;
}

function isCommodityCodeSame(trClassName) {
    var ItemSet = App.wjl.Collection.Set;
    var propertys = ["commodityCode"]
    var orderItems = $("." + trClassName);
    var newItemSet = new ItemSet();
    var commodityCodeSize = 0;
    orderItems.each(function() {
        var itemInfo = $(this).find("input[id$='." + propertys[0] + "']").val();
        if (itemInfo) {
            commodityCodeSize ++;
            newItemSet.add(itemInfo);
        }
    });

    return commodityCodeSize != newItemSet.size();
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


//==========================
//==========================
/**
 * @description 生成一个 node 节点
 * @param {String} nodeName
 * @param {Object} params
 * @return {*}
 */
function generateNode(nodeName, params) {
    var $node = $("<" + nodeName + ">");
    if (params.hasOwnProperty("name")) {
        $node.attr("name", params["name"]);
    }
    if (params.hasOwnProperty("maxlength")) {
        $node.attr("maxlength", params["maxlength"]);
    }
    if (params.hasOwnProperty("id")) {
        $node.attr("id", params["id"]);
    }
    if (params.hasOwnProperty("type")) {
        $node.attr("type", params["type"]);
    }
    if (params.hasOwnProperty("className")) {
        $node.attr("className", params["className"]);
    }
    if (params.hasOwnProperty("value")) {
        $node.val(params["value"]);
    }
    if (params.hasOwnProperty("css")) {
        $.each(params["css"], function (key, value) {
            $node.css(key, value);
        });
    }
    if (params.hasOwnProperty("autocomplete")) {
        $node.attr("autocomplete", params["autocomplete"]);
    }
    if (params.hasOwnProperty("readOnly")) {
        $node.attr("readOnly", params["readOnly"]);
    }
    if (params.hasOwnProperty("focus")) {
        $node.bind("focus", params["focus"]);
    }
    if (params.hasOwnProperty("click")) {
        $node.bind("click", params["click"]);
    }
    if (params.hasOwnProperty("change")) {
        $node.bind("change", params["change"]);
    }
    if (params.hasOwnProperty("keyup")) {
        $node.bind("keyup", params["keyup"]);
    }
    if (params.hasOwnProperty("innerHTML")) {
        $node.html(params["innerHTML"]);
    }
    if (params.hasOwnProperty("maxlength")) {
        $node.attr("maxlength", params["maxlength"]);
    }
    if (params.hasOwnProperty("hiddenValue")) {
        $node.attr("hiddenValue", params["hiddenValue"]);
    }
    return $node[0];
}

$(document).ready(function(){
    $("#table_task .serviceTotal").live("keyup blur", function (event) {
        if (event.type === "focusout")
            event.target.value = App.StringFilter.inputtedPriceFilter(event.target.value);
        else if (event.type === "keyup")
            event.target.value = App.StringFilter.inputtingPriceFilter(event.target.value);
    });
});