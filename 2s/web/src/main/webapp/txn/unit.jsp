<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<html>--%>
<%--<head>--%>
<script type="text/javascript">
var mouseOverUnit;
var thisUnit;
var jsonStrUnit;
var mouseOverUnitDiv = false;
var orderFlag = true;//true 的时候可以单位切换，false的时候不允许单位切换,单位颜色为灰色

$(function () {
    $(document).click(function (e) {
        var e = e || event;
        var target = e.srvElement || e.target;
        if (typeof(target.id) != "string" || (target.id.split(".")[1] != "unit" && target.id != "div_unit")) {
            $("#div_unit").hide();
        }
    });

    orderFlag = getOrderFlag();
    function getOrderFlag() {
        if ($("#orderType").get(0) == undefined) {
            return true;
        } else {
            var orderTypeVal = $("#orderType").val();
            var orderIdVal = $("#id").val();
            var orderStatus = $("#status").val();
            switch (orderTypeVal) {
                case 'purchaseOrder':
                    if (orderIdVal != '') {
                        return false;
                    } else {
                        return true;
                    }
                    break;
                case 'purchaseInventoryOrder':
                    if (orderIdVal != '') {
                        return false;
                    } else {
                        return true;
                    }
                    break;
                case 'goodsSaleOrder':
                    if (orderIdVal != '') {
                        return false;
                    } else {
                        return true;
                    }
                    break;
                case 'innerPicking':
                    return true;
                    break;

                case 'innerReturn':
                    return true;
                    break;
                case 'repairOrder':
                    if (orderStatus == 'REPAIR_SETTLED' || orderStatus == 'REPAIR_REPEAL') {   //结算和作废
                        return false;
                    } else {
                        return true;
                    }
                    break;
                case 'purchaseReturnOrder':
                    if (orderIdVal != '' &&  orderStatus != "SELLER_PENDING") {
                        return false;
                    } else {
                        return true;
                    }
                    break;
                case 'salesReturnOrder':
                    if (orderStatus == 'PENGIND') {   //结算和作废
                        return false;
                    } else {
                        return true;
                    }
                    break;
                case 'onlinePurchaseReturnOrder':
                    return true;
                case 'borrowOrder':
                    if (!G.isEmpty($("#borrowOrderId").val())) {
                        return false;
                    } else {
                        return true;
                    }
                    break;
                case 'preBuyOrder':
                    return false;
                    break;
                case 'APPOINT_ORDER':
                    return true;
                    break;
            }
        }
    }

    $("#div_unit").live("mouseover", function () {
        mouseOverUnitDiv = true;
    });
    $("#div_unit").live("mouseout", function () {
        mouseOverUnitDiv = false;
    });

    $("#Scroller_Container_unit>a").live("mouseover", function () {
        $(this).attr("class", "unit-a-hover");

    });
    $("#Scroller_Container_unit>a").live("mouseout", function () {
        $(this).removeAttr("class");
    });
    $("#Scroller_Container_unit>a").live("click", function () {
        mouseOverUnit = jQuery.trim($(this).text());
        if ($(this).text().length > 2) {
            $(this).attr("title", $(this).text());
        }
        $(thisUnit).val(mouseOverUnit);
        var unitIdPrefix = $(thisUnit).attr("id");
        unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
        if (mouseOverUnit != "" && mouseOverUnit != $("#" + unitIdPrefix + "\\.storageUnit").val() &&
                !G.Lang.isEmpty($("#" + unitIdPrefix + "\\.storageUnit").val()) && $(thisUnit).get(0) != undefined) {
//                        if (confirm("aclick确定需要为商品:【" + $("#" + unitIdPrefix + "\\.productName").val() +
//                                "】设定销售单位:【" + mouseOverUnit + "】吗？销售单位只能设定一次且不能修改！")) {
            jsonStrUnit = initJsonStr(thisUnit.val(), jsonStrUnit);
//            initUnitA(jsonStrUnit);
            showUnitIframe();
//                        }else{
//                            cancelSetUnit();
//                        }
        } else if (mouseOverUnit == "") {
            cancelSetUnit();
        } else {
            $(thisUnit).val(mouseOverUnit);
            jsonStrUnit = initJsonStr(thisUnit.val(), jsonStrUnit);
//            initUnitA(jsonStrUnit);
        }
        $("#div_unit").hide();
    });

    //单位框click事件触发下拉DIV
    $(".itemUnit").live("click", function () {
        if (isDisable()) {
            return;
        }
        $("#div_unit").show();
    });

    jQuery(".itemUnit").live("focus", function() {
        if (isDisable()) {
            return;
        }
        jQuery("#div_unit").hide();
        thisUnit = jQuery(this);
        if (jsonStrUnit == null) {
            jQuery.ajax({
                type:"POST",
                url:"shop.do?method=getShopUnit",
                async:true,
                data:{},
                cache:false,
                dataType:"json",
                error:function (XMLHttpRequest, error, errorThrown) {
                },
                success:function (jsonStr) {
                    jsonStrUnit = jsonStr;
                    initUnitA(jsonStr);
                }
            });
        }else{
            initUnitA(jsonStrUnit);
        }
        if ($("#div_unit").css("display") == "none") {
            var offset = $(this).offset();
            var offsetMainDiv = $(".i_main").eq(0).offset() || $(".shoppingCart").eq(0).offset();
            var offsetHeight = $(this).height();
            var offsetWidth = $(this).width();
            $("#div_unit").css({
                position:'absolute',
                opacity:'show',
                left:offset.left - offsetMainDiv.left + 'px',
                top:offset.top - offsetMainDiv.top + offsetHeight + 3 + 'px',
                width:offsetWidth + 20 + 'px',
                overflowY:"scroll",
                overflowX:"hidden"
            });
            $("#div_unit").show();
        }
    });
    $(".itemUnit").live("blur", function () {
        if ($("#div_unit").css("display") != 'none' || isDisable()) {
            return;
        }

        var unitIdPrefix = $(thisUnit).attr("id");
        unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
        if( !$("#" + unitIdPrefix + "\\.sellUnit")[0] || !$("#" + unitIdPrefix + "\\.storageUnit")[0]){
            return;
        }
        if (jQuery.trim($(thisUnit).val()) == "") {
            cancelSetUnit();
            return;
        } else if (thisUnit.val() == $("#" + unitIdPrefix + "\\.sellUnit").val()) {
            return;
        } else if ($("#" + unitIdPrefix + "\\.sellUnit").val() == '') {
            jsonStrUnit = initJsonStr(thisUnit.val(), jsonStrUnit);
            initUnitA(jsonStrUnit);
            return;
        } else {
            var unitIdPrefix = $(thisUnit).attr("id");
            unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
            if (thisUnit.val() != $("#" + unitIdPrefix + "\\.sellUnit").val()) {
                jsonStrUnit = initJsonStr(thisUnit.val(), jsonStrUnit);
                initUnitA(jsonStrUnit);
                showUnitIframe();
            }

        }
        //失去焦点增加单位
//                var unitIdPrefix = $(thisUnit).attr("id");
//                unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
//                if ($(thisUnit).val() != $("#" + unitIdPrefix + "\\.storageUnit").val() &&
//                        $("#" + unitIdPrefix + "\\.storageUnit").val() != "") {
//                    if (confirm("blur确定需要为商品:【" + $("#" + unitIdPrefix + "\\.productName").val() +
//                            "】设定销售单位:【" + $(thisUnit).val() + "】吗？销售单位只能设定一次且不能修改！") == true) {
//                        jsonStrUnit = initJsonStr(thisUnit.val(), jsonStrUnit);
//                        initUnitA(jsonStrUnit);
//                        showUnitIframe();
//                    } else {
//                        cancelSetUnit();
//                    }
//                }
    });

    $(".itemUnit").live("keyup", function (e) {
        //过滤特殊符号
        var reg = new RegExp("[`~!@#$^&*()=|\\\\{\\}%_\\+\\-\"':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？·～《》]", "g");
        $(this).val($(this).val().replace(reg, ""));
        if (jQuery.trim($(this).val()) != "") {
            $("#div_unit").hide();
        } else {
            $("#div_unit").hide();
        }
        //判断是否新增单位 回车键新增单位
        var e = e || window.event;
        var eventKeyCode = e.witch || e.keyCode;
        if (eventKeyCode == 13 || eventKeyCode == 108) {
            if (jQuery.trim($(thisUnit).val()) == "") {
                cancelSetUnit();
                return;
            }
            $("#div_unit").hide();
            thisUnit.blur();

//                    var unitIdPrefix = $(thisUnit).attr("id");
//                    unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
//                    if ($(thisUnit).val() != $("#" + unitIdPrefix + "\\.storageUnit").val() &&
//                            $("#" + unitIdPrefix + "\\.storageUnit").val() != "") {
//                        if (confirm("enter确定需要为商品:【" + $("#" + unitIdPrefix + "\\.productName").val() +
//                                "】设定销售单位:【" + $(thisUnit).val() + "】吗？销售单位只能设定一次且不能修改！")) {
//                            showUnitIframe();
//                        } else {
//                            cancelSetUnit();
//                        }
//                    }
        }
    });

    //初始化有单位的TD
    $(".itemUnit").each(function () {
        initUnitTd(this);
    });


    $(".itemUnitSpan").live("click", function () {
        if (!orderFlag) {
            return;
        }
        if ($(this).text() != undefined) {
            var unitIdPrefix = $(this).attr("id");
            unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
            var storageUnitVal = $("#" + unitIdPrefix + "\\.storageUnit").val();
            var sellUnitVal = $("#" + unitIdPrefix + "\\.sellUnit").val();
            var rateVal = $("#" + unitIdPrefix + "\\.rate").val() * 1;
            if ($(this).text() == sellUnitVal) {    //显示销售单位时click
                changeToStorageUnit(this, storageUnitVal, rateVal);
            } else if ($(this).text() == storageUnitVal) { //显示存储单位时click
                changeToSellUnit(this, sellUnitVal, rateVal);
            }
            setTotal();
        }
    });

    //Json数据初始化下拉DIV 中的A标签，
    function initUnitA(jsonStr) {
        if (!jsonStr ||jsonStr.shopUnitStatus == "false") {
            return;
        } else if (jsonStr.shopUnitStatus == "true") {
            $("#Scroller_Container_unit").empty();
            for (var i = 0; i < jsonStr.shopUnitDTOs.length; i++) {
                $("#Scroller_Container_unit").append($(
                        '<a>' + jsonStr.shopUnitDTOs[i].unitName +
                                '<input id="shopUnits' + i + '.unitName" name="shopUnits[' + i + '].unitName"' +
                                'type="hidden" value="' + jsonStr.shopUnitDTOs[i].unitName + '"></a>'));
            }
        }
    }

    //选择一个单位之后修改json数据排序。
    function initJsonStr(str, jsonStrUnit) {
        if (jsonStrUnit.shopUnitStatus == "false") {
            var strTemp = "[";
            $("#Scroller_Container_unit > a").each(function (i) {
                if (jQuery.trim($("#shopUnits" + i + "\\.unitName").val()) != '') {
                    strTemp += "{\"unitName\":\"" + $("#shopUnits" + i + "\\.unitName").val() + "\"},";
                }
            });
            jsonStrUnit.shopUnitDTOs = eval(strTemp + "]");
            jsonStrUnit.shopUnitStatus = "true";
        }
        if (jQuery.trim(str) == '') {
            return jsonStrUnit;
        }
        var newJsonUnit = [];
        newJsonUnit.unshift({unitName:str});   //数组最前面加一条记录newJsonUnit
        for (var i = 0; i < jsonStrUnit.shopUnitDTOs.length; i++) {
            if (jsonStrUnit.shopUnitDTOs[i].unitName != str && jsonStrUnit.shopUnitDTOs[i].unitName != undefined && jsonStrUnit.shopUnitDTOs[i] != '') {
                newJsonUnit.push({unitName:jsonStrUnit.shopUnitDTOs[i].unitName}); //数组后面加一条记录newJsonUnit
            }
        }
        jsonStrUnit.shopUnitDTOs =  newJsonUnit;
        jsonStrUnit.shopUnitStatus = "true";
        return jsonStrUnit;
    }

    //弹出单位换算的iframe
    function showUnitIframe() {
        bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_unit")[0],
            'src':"txn.do?method=gotoSetUnitPage&unitId=" + $(thisUnit).attr("id")});
    }
});

//本页和Iframe取消修改单位
function cancelSetUnit() {
    var unitIdPrefix = $(thisUnit).attr("id");
    unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
    $(thisUnit).val($("#" + unitIdPrefix + "\\.storageUnit").val());
}

//Iframe提交修改单位
function submitSetUnit(storageUnit, sellUnit, rate) {
    var unitIdPrefix = $(thisUnit).attr("id");
    unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
    jQuery.ajax({
        type:"POST",
        url:"txn.do?method=setSellUnitAndRate",
        async:true,
        data:{
            productId:$("#" + unitIdPrefix + "\\.productId").val(),
            storageUnit:storageUnit,
            sellUnit:sellUnit,
            rate:rate
        },
        cache:false,
        dataType:"json",
        success:function (jsonStr) {
            var orderType = jQuery("#orderType").val();
            if (jsonStr[jsonStr.length - 1].result == "success") {
                $("#" + unitIdPrefix + "\\.storageUnit").val(storageUnit);
                $("#" + unitIdPrefix + "\\.sellUnit").val(sellUnit);
                $("#" + unitIdPrefix + "\\.rate").val(rate);

                if ($(thisUnit).val() == sellUnit) {       //新增销售单位
                    changeToSellUnit(thisUnit, sellUnit, rate);
                } else if ($(thisUnit).val() == storageUnit) {
                    changeToStorageUnit(thisUnit, storageUnit, rate);
                }
                setTotal();
                initUnitTd(thisUnit);
                return;
            }
        }
    });
}

function initUnitTd(dom) {
    var unitIdPrefix = $(dom).attr("id");
    unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
    if ($("#" + unitIdPrefix + "\\.sellUnit").val() != '' && $("#" + unitIdPrefix + "\\.storageUnit").val() != ''
            && $("#" + unitIdPrefix + "\\.sellUnit").val() != $("#" + unitIdPrefix + "\\.storageUnit").val()) {
        $(dom).hide();
        if ($("#" + unitIdPrefix + "\\.unitSpan").get(0) == undefined || $("#" + unitIdPrefix + "\\.unitSpan").get(0) == null) {
            if (orderFlag) {
                var unitA = '<span id = "' + unitIdPrefix + '.unitSpan" class = "itemUnitSpan">' +
                        $(dom).val() + '</span>';
            } else {
                var unitA = '<span id = "' + unitIdPrefix + '.unitSpan" class = "itemUnitSpan" style = "color:grey;">' +
                        $(dom).val() + '</span>';
            }

            $(dom).parent().append(unitA);
        } else {
            $("#" + unitIdPrefix + "\\.unitSpan").text($(dom).val());
        }
    }
}

function changeToSellUnit(dom, sellUnitVal, rateVal) {      //点击变化成销售单位     大单位化成小单位，价格变小，数量变大
    $(dom).text(sellUnitVal);
    var unitIdPrefix = $(dom).attr("id");
    unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
    $("#" + unitIdPrefix + "\\.unit").val(sellUnitVal);
    $("#" + unitIdPrefix + "\\.inventoryAmount").val(
            ($("#" + unitIdPrefix + "\\.inventoryAmount").val() * rateVal).toFixed(2));
    if ($("#" + unitIdPrefix + "\\.inventoryAmountSpan").get(0) != undefined) {
        $("#" + unitIdPrefix + "\\.inventoryAmountSpan").text(
                G.rounding($("#" + unitIdPrefix + "\\.inventoryAmountSpan").text() * rateVal)
        );
    }
    if ($("#" + unitIdPrefix + "\\.price").get(0) != undefined) {
        $("#" + unitIdPrefix + "\\.price").val(dataTransition.simpleRounding($("#" + unitIdPrefix + "\\.price").val() * 1 / rateVal,2));
        $("#" + unitIdPrefix + "\\.price").keyup();
    }
    if ($("#" + unitIdPrefix + "\\.price_span").get(0) != undefined){
        $("#" + unitIdPrefix + "\\.price_span").text(dataTransition.simpleRounding($("#" + unitIdPrefix + "\\.price_span").text() / rateVal,2));
    }
    if ($("#" + unitIdPrefix + "\\.inSalesPrice").get(0) != undefined) {        //上架售价
        $("#" + unitIdPrefix + "\\.inSalesPrice").val(G.rounding($("#" + unitIdPrefix + "\\.inSalesPrice").val()*1 /rateVal));
        $("#" + unitIdPrefix + "\\.inSalesPrice").keyup();
    }
    if ($("#" + unitIdPrefix + "\\.inSalesAmount").get(0) != undefined) {
        $("#" + unitIdPrefix + "\\.inSalesAmount").val(
            ($("#" + unitIdPrefix + "\\.inSalesAmount").val() * 1*rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.purchasePrice").get(0) != undefined) {
        $("#" + unitIdPrefix + "\\.purchasePrice").val(
                ($("#" + unitIdPrefix + "\\.purchasePrice").val() * 1 / rateVal).toFixed(2));
        $("#" + unitIdPrefix + "\\.purchasePrice").keyup();
    }
    if ($("#" + unitIdPrefix + "\\.recommendedPrice").get(0) != undefined) {    // 销售价，
        $("#" + unitIdPrefix + "\\.recommendedPrice").val(
                ($("#" + unitIdPrefix + "\\.recommendedPrice").val() * 1 / rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.recommendedPriceSpan").get(0) != undefined) {
        $("#" + unitIdPrefix + "\\.recommendedPriceSpan").text(
                G.rounding($("#" + unitIdPrefix + "\\.recommendedPriceSpan").text() * 1 / rateVal));
    }
    if ($("#" + unitIdPrefix + "\\.tradePrice").get(0) != undefined) {
        $("#" + unitIdPrefix + "\\.tradePrice").val(
                ($("#" + unitIdPrefix + "\\.tradePrice").val() * 1 / rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.tradePriceSpan").get(0) != undefined) {
        $("#" + unitIdPrefix + "\\.tradePriceSpan").text(
                G.rounding($("#" + unitIdPrefix + "\\.tradePriceSpan").text() * 1 / rateVal));
    }

    if ($("#" + unitIdPrefix + "\\.inventoryAmountHid").get(0) != undefined) {    // 施工单 库存总量，
        $("#" + unitIdPrefix + "\\.inventoryAmountHid").val(
                ($("#" + unitIdPrefix + "\\.inventoryAmountHid").val() * rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.reservedSpan").get(0) != undefined) {    // 施工单 预留 span，
        $("#" + unitIdPrefix + "\\.reservedSpan").text(
                ($("#" + unitIdPrefix + "\\.reservedSpan").text() * rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.reserved").get(0) != undefined) {    // 施工单 预留 hidden input，
        $("#" + unitIdPrefix + "\\.reserved").val(
                ($("#" + unitIdPrefix + "\\.reserved").val() * rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.returnAbleAmount").get(0) != undefined) {
        $("#" + unitIdPrefix + "\\.returnAbleAmount").val(($("#" + unitIdPrefix + "\\.returnAbleAmount").val() * 1 * rateVal).toFixed(2));
    }
//    if ($("#" + unitIdPrefix + "\\.amount").get(0) != undefined) {
//        $("#" + unitIdPrefix + "\\.amount").val(($("#" + unitIdPrefix + "\\.amount").val() * 1 * rateVal).toFixed(2));
//    }
    if ($("#" + unitIdPrefix + "\\.lowerLimit").get(0) != undefined) {  //采购单，入库单
        $("#" + unitIdPrefix + "\\.lowerLimit").val(dataTransition.simpleRounding(
                $("#" + unitIdPrefix + "\\.lowerLimit").val() * rateVal, 1
        ));
    }
    if ($("#" + unitIdPrefix + "\\.upperLimit").get(0) != undefined) {  //采购单，入库单
        $("#" + unitIdPrefix + "\\.upperLimit").val(dataTransition.simpleRounding(
                $("#" + unitIdPrefix + "\\.upperLimit").val() * rateVal, 1
        ));
    }
    if(verifyProductThroughOrderVersion(getOrderType())){
        $("#" + unitIdPrefix + "_supplierInfo .useRelatedAmount").each(function(){
            $(this).val(G.rounding($(this).val()*rateVal,2));
            $(this).attr("remainAmount",G.rounding($(this).attr("remainAmount")*rateVal,2));
        });
        $("#" + unitIdPrefix + "_supplierInfo .remainAmount").each(function(){
            $(this).text(G.rounding($(this).text()*rateVal,2));
        });
        $("#" + unitIdPrefix + "_supplierInfo .tempAmount").each(function(){
            $(this).text(G.rounding($(this).text()*rateVal,2));
        });
    }
}
function changeToStorageUnit(dom, storageUnitVal, rateVal) {  //点击变化成库存单位 小单位化成大单位，价格变大，数量变小
    $(dom).text(storageUnitVal);
    var unitIdPrefix = $(dom).attr("id");
    unitIdPrefix = unitIdPrefix.substring(0, unitIdPrefix.indexOf("."));
    $("#" + unitIdPrefix + "\\.unit").val(storageUnitVal);
    $("#" + unitIdPrefix + "\\.inventoryAmount").val(G.rounding($("#" + unitIdPrefix + "\\.inventoryAmount").val() * 1 / rateVal));
    if ($("#" + unitIdPrefix + "\\.inventoryAmountSpan").get(0) != undefined) {      //入库单 库存标签
        $("#" + unitIdPrefix + "\\.inventoryAmountSpan").text(G.rounding($("#" + unitIdPrefix + "\\.inventoryAmountSpan").text() * 1 / rateVal));
    }
    if ($("#" + unitIdPrefix + "\\.price").get(0) != undefined) {        //销售单 销售价
        $("#" + unitIdPrefix + "\\.price").val(
                dataTransition.simpleRounding($("#" + unitIdPrefix + "\\.price").val() * rateVal,2));
        $("#" + unitIdPrefix + "\\.price").keyup();
    }
    if ($("#" + unitIdPrefix + "\\.inSalesPrice").get(0) != undefined) {        //上架售价
        $("#" + unitIdPrefix + "\\.inSalesPrice").val(G.rounding($("#" + unitIdPrefix + "\\.inSalesPrice").val() *1*rateVal));
        $("#" + unitIdPrefix + "\\.inSalesPrice").keyup();
    }
    if ($("#" + unitIdPrefix + "\\.price_span").get(0) != undefined){
        $("#" + unitIdPrefix + "\\.price_span").text(
                dataTransition.simpleRounding($("#" + unitIdPrefix + "\\.price_span").text() * rateVal,2));
    }
    if ($("#" + unitIdPrefix + "\\.purchasePrice").get(0) != undefined) {    // 入库单 入库价
        $("#" + unitIdPrefix + "\\.purchasePrice").val(
                ($("#" + unitIdPrefix + "\\.purchasePrice").val() * rateVal).toFixed(2));
        $("#" + unitIdPrefix + "\\.purchasePrice").keyup();
    }
    if ($("#" + unitIdPrefix + "\\.recommendedPrice").get(0) != undefined) {    // 入库单 设定销售价，
        $("#" + unitIdPrefix + "\\.recommendedPrice").val(
                ($("#" + unitIdPrefix + "\\.recommendedPrice").val() * rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.recommendedPriceSpan").get(0) != undefined) {      //零售价
        $("#" + unitIdPrefix + "\\.recommendedPriceSpan").text(
                ($("#" + unitIdPrefix + "\\.recommendedPriceSpan").text() * 1 * rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.tradePrice").get(0) != undefined) {    // 批发价
        $("#" + unitIdPrefix + "\\.tradePrice").val(
                ($("#" + unitIdPrefix + "\\.tradePrice").val() * rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.tradePriceSpan").get(0) != undefined) {
        $("#" + unitIdPrefix + "\\.tradePriceSpan").text(
                ($("#" + unitIdPrefix + "\\.tradePriceSpan").text() * 1 * rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.inventoryAmountHid").get(0) != undefined) {    // 施工单 库存总量，
        $("#" + unitIdPrefix + "\\.inventoryAmountHid").val(
                ($("#" + unitIdPrefix + "\\.inventoryAmountHid").val() * 1 / rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.inSalesAmount").get(0) != undefined) {
        $("#" + unitIdPrefix + "\\.inSalesAmount").val(
            ($("#" + unitIdPrefix + "\\.inSalesAmount").val() *1/ rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.reservedSpan").get(0) != undefined) {    // 施工单 预留span，
        $("#" + unitIdPrefix + "\\.reservedSpan").text(
                ($("#" + unitIdPrefix + "\\.reservedSpan").text() * 1 / rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.reserved").get(0) != undefined) {    // 施工单 预留 hidden input，
        $("#" + unitIdPrefix + "\\.reserved").val(
                ($("#" + unitIdPrefix + "\\.reserved").val() * 1 / rateVal).toFixed(2));
    }
    if ($("#" + unitIdPrefix + "\\.returnAbleAmount").get(0) != undefined) {
        $("#" + unitIdPrefix + "\\.returnAbleAmount").val(($("#" + unitIdPrefix + "\\.returnAbleAmount").val() * 1 / rateVal).toFixed(2));

    }
//    if ($("#" + unitIdPrefix + "\\.amount").get(0) != undefined) {
//        $("#" + unitIdPrefix + "\\.amount").val(($("#" + unitIdPrefix + "\\.amount").val() * 1 / rateVal).toFixed(2));
//    }
    if ($("#" + unitIdPrefix + "\\.lowerLimit").get(0) != undefined) {  //采购单，入库单
        $("#" + unitIdPrefix + "\\.lowerLimit").val(dataTransition.simpleRounding(
                $("#" + unitIdPrefix + "\\.lowerLimit").val() * 1 / rateVal, 1
        ));
    }
    if ($("#" + unitIdPrefix + "\\.upperLimit").get(0) != undefined) {  //采购单，入库单
        $("#" + unitIdPrefix + "\\.upperLimit").val(dataTransition.simpleRounding(
                $("#" + unitIdPrefix + "\\.upperLimit").val() * 1 / rateVal, 1
        ));
    }
    if(verifyProductThroughOrderVersion(getOrderType())){
        $("#" + unitIdPrefix + "_supplierInfo .useRelatedAmount").each(function(){
            $(this).val(G.rounding($(this).val()/rateVal,2));
            $(this).attr("remainAmount",G.rounding($(this).attr("remainAmount")/rateVal,2));
        });
        $("#" + unitIdPrefix + "_supplierInfo .remainAmount").each(function(){
            $(this).text(G.rounding($(this).text()/rateVal,2));
        });
        $("#" + unitIdPrefix + "_supplierInfo .tempAmount").each(function(){
            $(this).text(G.rounding($(this).text()/rateVal,2));
        });
    }
}

</script>
<%--</head>--%>
<!--商品单位下拉菜单-->
<%--<body>--%>
<div id="div_unit" class="i_scroll_unit">
    <div class="Container" id="Container_unit" style="width: 30px;">
        <div id="Scroller-1unit" style="width:30px;">
            <div class="Scroller-ContainerUnit" id="Scroller_Container_unit">
            </div>
        </div>
    </div>
</div>

<iframe id="iframe_PopupBox_unit" style="position:absolute;z-index:5; left:350px; top:200px; display:none;"
        allowtransparency="true" width="300px" height="200px" frameborder="0" src="" scrolling="no"></iframe>
<%--</body>--%>
<%--</html>--%>