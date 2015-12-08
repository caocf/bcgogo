/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-8-21
 * Time: 下午7:08
 * 需要 用到商品下拉建议的地方 才需要引入   依赖于common.js
 * 在使用是在对应的jsp中加入id 为 id-searchcomplete的div TODO 以后改成组件自动创建DIV
 *
 */
$(document).ready(function() {

    //去除文本框的自动填充下拉框
    $("input[type='text']").each(function() {
        $(this).attr("autocomplete", "off");
    });
    //产品
    var orderType = getOrderType();
    var width,horizontalCenter;
    if(orderType=="PRE_BUY_ORDER"){
        width=795;
        horizontalCenter=92;
    }
    var searchcomplete = APP_BCGOGO.Module.searchcomplete;
    searchcomplete.init({
        "selector": $("#id-searchcomplete"),
        "detailsListCategoryList": [{
            "name": "commodity_code",
            "title": "商品编码"
        }],
        //暂时只是防止 插件 报错
        "pageSize": 15,
        "pageIndex": 0,
        "isScroll": true,

        // add column content hidden feature
        enableColumnContentHidden:true,
        columnContentHiddenKeycode:17,

        // TODO test 要注意的一点


        "width":width,

        // horizontalCenter 指的是距离水平中点的距离， 可正可负， 此布局方式实现理念参照自 Flash Flex SDK 4.5
        "horizontalCenter":horizontalCenter,



        // 必须
        "autocompleteClick": function(event, index, data) {
            clearAndUpdateData(searchcomplete._relInst, data.details);
            searchcomplete.clear();
            suggestionHidden(searchcomplete);
        },
        "isKeyBoardControlEnabled":true,
        "onGetInputtingData":function() {
            /**
             * 根据 tr 从此行获取并返回， 输入框内的用户数据
             * @param $tr
             * @return data
             * {
             *     // 允許某項是不存在的
             *     "commodity_code":"",
             *     "product_name":"",
             *     "product_brand":"",
             *     "product_model":"",
             *     "product_spec":"",
             *     "product_vehicle_brand":"",
             *     "product_vehicle_model:""
             * }
             */
            var getInputtingUserDataFromTr = function($tr) {
                var inputName = [
                    "commodityCode",
                    "productName",
                    "brand",
                    "model",
                    "spec",
                    "vehicleBrand",
                    "vehicleModel"

                ],
                    dataName = [
                        "commodity_code",
                        "product_name",
                        "product_brand",
                        "product_model",
                        "product_spec",
                        "product_vehicle_brand",
                        "product_vehicle_model"
                    ],
                    data = {};
                for (var i = 0, len = inputName.length; i < len; i++) {
                    var $foo = $tr.find("input[id$='." + inputName[i] + "']");
                    if( $foo[0] ) {
                        data[dataName[i]] = $foo.val();
                    }
                }

                return data;
            };

            var $tr = $(searchcomplete._relInst).parent().parent(),
                inputtingData = getInputtingUserDataFromTr($tr);

            return inputtingData;
        },
        "onFinishedKeyboardControl":function(event, data) {
            var disabledAutoNextFocus = true;
            G.info(data.details);
            clearAndUpdateData(searchcomplete._relInst, data.details, disabledAutoNextFocus);
        },
        // 必须
        "onMore": function(event) {
            //
            searchProductHistorySuggestionOnPage(searchcomplete._relInst);
        },
        "onPrev": function(event) {
            //
            searchProductHistorySuggestionOnPage(searchcomplete._relInst);
        },
        "onDetailsListSelect": function(event, index, data) {
            var $data_row;
            var $table = $(searchcomplete._relInst).closest("table");
            if(orderType == "REPAIR") {
                $data_row = getRepairOrderProductRowByExistsData($table, data);
            } else if(orderType == "INVENTORY"){
                $data_row = null;
            } else {
                $data_row = getRowByExistsData($table, data,"product");
            }
            var $last_row;
            if(orderType == "REPAIR") {
                $last_row = $table.find(".item1:last");
            } else {
                $last_row = $table.find(".item:last");
            }
            if(!G.isEmpty($last_row.find("input[id$='.productName']").val()) && !isSearchRow($last_row)) {
                $last_row = addProductItemRow(orderType, "product", data);
            }
            var backup_data = backupData($(searchcomplete._relInst));
            if($data_row) {
                var $itemAmount = $data_row.find(".itemAmount");
                if(G.isEmpty($itemAmount.val())) {
                    $itemAmount.val(0.0);
                }
                if (G.isEmpty($data_row.find("input[id$='.productId']").val())) {
                    $data_row.find("input[id$='.productId']").val(data.product_id);
                    checkProductData($data_row.find("input[id$='.productId']"));
                }
                if(!verifyProductThroughOrderVersion(getOrderType())){
                    $itemAmount.val(Number($itemAmount.val())+1);
                }
            } else {
                searchcomplete.myClickTimes += 1; //只有当前点击的数据没有找到存在的时候才 +1
                if(searchcomplete.myClickTimes == 1) { //第一次点击 覆盖当前行
                    $data_row = $(searchcomplete._relInst).closest("tr");
                    //
                    setProductItemValue($data_row, data, orderType, "product");
                    if(!G.isEmpty($last_row.find("input[id$='.productName']").val()) && !isSearchRow($last_row)) {
                        $last_row = addProductItemRow(orderType, "product", data);
                    }
                } else {
                    $data_row = $last_row;
                    setProductItemValue($data_row, data, orderType, "product");
                    $last_row = addProductItemRow(orderType, "product", data);
                }

                checkProductData($data_row.find("input[id$='.productId']"));
            }
            //误删
            $(searchcomplete._relInst).closest("tr").find("input[id$='.commodityCode']").attr("blurLock", true);

            $last_row.find("input[type='text']").each(function() {
//                $(this).val(backup_data[$(this).attr("id").split(".")[1]]);
                $(this).addClass("j_search_word");
                if(GLOBAL.Lang.endWith($(this).attr("id"), $(searchcomplete._relInst).attr("id").split(".")[1])) {
                    return false;
                }
            });
            var $node = $last_row.find("input[id$='" + $(searchcomplete._relInst).attr("id").split(".")[1] + "']");
            searchcomplete.moveFollow({
                node: $node[0],
                isMoveOnly: true
            });
            if ($.isFunction(window.setTotal)) {
                setTotal();//每个模块都有自定义的
            }
            activeRecommendSupplierTip($data_row, orderType);
        }
    });
    searchcomplete._target.css("z-index", "10");

    suggestionHidden(searchcomplete);
    /**
     * 必须放在  searchcomplete  init 后面
     * 鼠标点击空白   隐藏 下拉提示
     */
    $(document).bind("click", function(event) {
        var $target=$(event.target);
        var selectorArray = [
            $(".bcgogo-ui-searchcomplete-autocomplete,.bcgogo-ui-searchcomplete-detailsList,.bcgogo-ui-searchcomplete-searchBar", searchcomplete._target),
            "input[id$='.commodityCode']",
            "input[id$='.productName']",
            "input[id$='.brand']",
            "input[id$='.model']",
            "input[id$='.spec']",
            "input[id$='.vehicleBrand']",
            "input[id$='.vehicleModel']",
            "#ui-datepicker-div"
        ];

        // hack 我们伟大的 firefox 的 Dom engine bug
        if(event.target === document.lastChild) {
            //清颜色  清文字
            clearSearchWord($(searchcomplete._relInst).parent().parent(), true);
            suggestionHidden(searchcomplete);
            return;
        }


        if( $(event.target).closest(selectorArray).length == 0
            && G.contains($(event.target).text(), ["<上月", "下月>"]) === false) {
            //清颜色  清文字
            clearSearchWord($(searchcomplete._relInst).parent().parent(), true);
            suggestionHidden(searchcomplete);
        }
    });

    if(verifyProductThroughOrderVersion(getOrderType())){
        $(document).bind("click", function(event) {
            var slideArray=[
                ".item .itemAmount",
                ".item .returnAmount",
                ".supplierInfo"
            ];
            if($(event.target).closest(slideArray).length == 0){
                closeSupplierInventoryList();
            }
        });

        $(document).bind("keyup",function(event){
            //商品出入库打通，当下拉只有一个供应商时
            var reg_amount=/amount$|returnAmount$|actualInventoryAmount$/; //数量输入框
            var $target=$(event.target);
            if(reg_amount.test($target.attr("id"))){
                $(".item").each(function(){
                    var idPrefix=$(this).find("[id$='.productId']").attr("id").split(".")[0];
                    if($("#"+idPrefix+"_supplierInfo").find("[name$='.relatedSupplierId']").size()==1){
                        if(getOrderType()=="INVENTORY_CHECK"){
                            $("#"+idPrefix+"_supplierInfo .useRelatedAmount").val(G.rounding($(this).find("#" + idPrefix + "\\.actualInventoryAmount").val(),2));
                        }if(getOrderType()=="RETURN_ORDER"){
                            $("#"+idPrefix+"_supplierInfo .useRelatedAmount").val(G.rounding($(this).find("#" + idPrefix + "\\.returnAmount").val(),2));
                        }else{
                            $("#"+idPrefix+"_supplierInfo .useRelatedAmount").val(G.rounding($(this).find("#" + idPrefix + "\\.amount").val(),2));
                        }
                        verifySupplierInventoryAction($("#"+idPrefix+"_supplierInfo .useRelatedAmount"));
                    }
                });
            }
        });

        $(".item .itemAmount,.item .returnAmount").live("focus",function(event){
            closeSupplierInventoryList();
            var idPrex =$(this).attr("id").substr(0,9);
            $("#"+idPrex+"_supplierInfo").show();
            if(getOrderType()=="RETURN_ORDER"){
                borrowOrderReturnStyleAdjust();
            }
        });

        $("[id$='_supplierInfo']").live("click",function(event){
            closeSupplierInventoryList();
            var $supplierInfo=$(event.target).closest('.supplierInfo');
            $supplierInfo.show();
            if(getOrderType()=="RETURN_ORDER"){
                borrowOrderReturnStyleAdjust();
            }
        });
        $(".item .itemAmount,.item .returnAmount").live("mouseenter",function(event){
            var idPrefix= $(this).attr("id").split(".")[0];
            if($("#"+idPrefix+"_supplierInfo").find("[name$='relatedSupplierId']").size()>1){
                var name="";
                if(getOrderType()=='SALE'){
                    name="销售数量！";
                } else if(getOrderType()=='SALE_RETURN'){
                    name="退货数量！";
                }else if(getOrderType()=='INNER_PICKING'){
                    name="领料数量！";
                } else if(getOrderType()=='INNER_RETURN'){
                    name="退料数量！";
                }else if(getOrderType()=='BORROW_ORDER'){
                    name="借料数量！";
                } else if(getOrderType()=='RETURN_ORDER'){
                    name="数量！";
                } else if(getOrderType()=='ALLOCATE_RECORD'){
                    name="调拨数量！";
                } else if(getOrderType()=='INVENTORY_CHECK'){
                    name="实际库存";
                }
                var alertMsg= '<div class="alert_develop" style="margin: -1px 0px 0px -4px;"><span class="arrowTop" style="margin-left:20px;"></span>';
                alertMsg+='<div class="alertAll"><div class="alertLeft"></div><div class="alertBody"> 请在下方列表填写商品详细'+name+' </div><div class="alertRight"></div></div></div>';
                $(this).closest("td").append(alertMsg);
            }
        }).live("mouseleave",function(e){
                $(this).closest("td").find(".alert_develop").remove();
            });
    }

    //order  init
    var created_time_title = "",
        item_count_title = "";
    if(orderType == "INVENTORY" || orderType == "RETURN") {
        created_time_title = "入库日期";
        item_count_title = "入库量";
    } else if(orderType == "PURCHASE") {
        created_time_title = "采购日期";
        item_count_title = "采购量";
    } else if(orderType == "SALE" || orderType == "SALE_RETURN") {
        created_time_title = "销售日期";
        item_count_title = "数量";
    }
    var arrayFieldTitle = [{
        "name": "receipt_no",
        "title": "单据编号",
        "charLength": "12"
    }, {
        "name": "created_time",
        "title": created_time_title,
        "charLength": "10"
    }, {
        "name": "commodity_code",
        "title": "商品编码"
    }, {
        "name": "product_name,product_brand",
        "title": "品名/品牌",
        "charLength": "20"
    }, {
        "name": "product_spec,product_model",
        "title": "规格/型号"
    }, {
        "name": "product_vehicle_model,product_vehicle_brand",
        "title": "车型/车辆品牌"
    }, {
        "name": "item_price",
        "title": "单价",
        "charLength": "5"
    }, {
        "name": "item_count",
        "title": item_count_title,
        "charLength": "5"
    }, {
        "name": "unit",
        "title": "单位",
        "charLength": "4"
    }];
    if(orderType == "REPAIR") {
        arrayFieldTitle = [{
            "name": "receipt_no",
            "title": "单据编号",
            "charLength": "12"
        }, {
            "name": "created_time",
            "title": "施工日期",
            "charLength": "10"
        }, {
            "name": "vehicle",
            "title": "车牌号"
        }, {
            "name": "services",
            "title": "施工内容"
        }, {
            "name": "product_name",
            "title": "材料"
        }, {
            "name": "item_price",
            "title": "单价",
            "charLength": "5"
        }, {
            "name": "item_count",
            "title": "数量",
            "charLength": "5"
        }, {
            "name": "unit",
            "title": "单位",
            "charLength": "4"
        }, {
            "name": "order_status",
            "title": "施工状态"
        }];
    }

    var searchcompleteMultiselect = APP_BCGOGO.Module.searchcompleteMultiselect;
    searchcompleteMultiselect.init({
        "selector": $("#id-searchcompleteMultiselect"),
        "detailsListCategoryList": arrayFieldTitle,
        "theme": searchcompleteMultiselect.themes.GIANT,
        "pageSize": 15,
        "pageIndex": 0,
        "isTransparentAuto": true,
        "orderType":orderType,
        // 必须
        "autocompleteClick": function(event, index, data) {

            //把选中的 客户 或者 供应商名字 赋值
            if($(searchcompleteMultiselect._relInst).hasClass("supplierSuggestion")) {
                $("#supplier").attr("blurLock",true);

                if($("#supplier")) $("#supplier").val(data.details.name);
                if($("#supplierShopId")){
                    $("#supplierShopId").val(data.details.customerOrSupplierShopId);
                }
                if($("#supplierId")) {
                    $("#supplierId").val(data.details.id);
                    checkSupplierById(data.details.id);
                }
            } else {
                if($("#customer")) {
                    $("#customer").attr("blurLock",true);
                    $("#customer").val(data.details.name);
                    $("#customerId").val(data.details.id);
                    checkCustomerById(data.details.id);
                }
            }

            //隐藏 div
            searchcompleteMultiselect.clear();
            suggestionHidden(searchcompleteMultiselect);



            // TODO 这一段代码需要妨碍 历史查询中 , 调整样式， 历史查询按钮放在 输入框的后面，使用 div 内嵌 + 浮动
//            $("").bind("click", function(){
//                var foo = App.Module.searchcompleteMultiselect;
//                searchOrderSuggestion(foo, foo._relInst, "");
//            });
            // TODO 下面这段代码在正式的 代码中应该被注掉
//            setTimeout(function() {
//                searchOrderSuggestion(searchcompleteMultiselect, searchcompleteMultiselect._relInst, "");
//            }, 100);
            //
        },
        "onDetailsListSelect": function(event, index, data) {
            var $data_row;
            if(orderType == "REPAIR") {
                if(!(data.item_type && data.item_type == "SERVICE")) {
                    $data_row = getRepairOrderProductRowByExistsData($("#table_productNo_2"), data);
                }
            }else if(orderType == "INVENTORY"){
                $data_row = null;
            } else {
                $data_row = getRowByExistsData($("#table_productNo"), data,"order");
            }
            if($data_row) {
                var $itemAmount = $data_row.find(".itemAmount");
                if(G.isEmpty($itemAmount.val())) {
                    $itemAmount.val(0.0);
                }
                if (G.isEmpty($data_row.find("input[id$='.productId']").val())) {
                    $data_row.find("input[id$='.productId']").val(data.product_id);
                    checkProductData($data_row.find("input[id$='.productId']"));

                }
                $itemAmount.val(Number($itemAmount.val()) + 1);
            } else {
                if(orderType == "REPAIR") {
                    if(data.item_type && data.item_type == "SERVICE") {
                        $data_row = $("#table_task").find("tbody").find("tr:last");
                    } else {
                        $data_row = $("#table_productNo_2").find("tbody").find("tr:last");
                    }
                } else if(orderType == "RETURN") {
                    $data_row = $("#table_productNo").find("tbody").find(".item:last");
                } else {
                    $data_row = $("#table_productNo").find("tbody").find("tr:last");
                }
                if($data_row.find("input[type='text']:first").val() && $data_row.find("input[type='text']:first").val() != "") {
                    $data_row = addProductItemRow(orderType, "order", data);
                }

                setProductItemValue($data_row, data, orderType, "order");
                addProductItemRow(orderType, "order", data);
                if(!(orderType == "REPAIR" && data.item_type && data.item_type == "SERVICE")) {
                    checkProductData($data_row.find("input[id$='.productId']"));
                }
            }
            activeRecommendSupplierTip($data_row, orderType);
            if ($.isFunction(window.setTotal)) {
                setTotal();//每个模块都有自定义的
            }
        },
        // 必须
        "onMore": function(event) {
            switch(searchcompleteMultiselect.state) {
                case searchcompleteMultiselect.STATE.AUTO_SEARCH:
                    searchOrderSuggestion(searchcompleteMultiselect, searchcompleteMultiselect._relInst, "page", true);
                    break;

                case searchcompleteMultiselect.STATE.DETAILS_SEARCH:
                    searchcompleteMultiselect.searchBar.manualSearch({
                        rowStart:searchcompleteMultiselect.getPageIndex(),
                        pageRows:searchcompleteMultiselect.getPageSize(),
                        now:new Date().getTime()
                    });
                    break;
            }
        },
        "onPrev": function(event) {
            switch(searchcompleteMultiselect.state) {
                case searchcompleteMultiselect.STATE.AUTO_SEARCH:
                    //                      y
                    searchOrderSuggestion(searchcompleteMultiselect, searchcompleteMultiselect._relInst, "page", true);
                    break;
                case searchcompleteMultiselect.STATE.DETAILS_SEARCH:
                    searchcompleteMultiselect.searchBar.manualSearch({
                        rowStart:searchcompleteMultiselect.getPageIndex(),
                        pageRows:searchcompleteMultiselect.getPageSize(),
                        now:new Date().getTime()
                    });
                    break;
            }
        },
        // 此方法仅用于 searchBar
        "beforeSearch":function(event) {
            if(searchcompleteMultiselect.state === searchcompleteMultiselect.STATE.AUTO_SEARCH) {
                searchcompleteMultiselect.state = searchcompleteMultiselect.STATE.DETAILS_SEARCH;
                searchcompleteMultiselect.setPageIndex(0);
            }
        },
        //
        "afterSearch":function (data, textStatus) {
            if(textStatus === "success") {
                searchcompleteMultiselect.setUUID(data.uuid);
                searchcompleteMultiselect.draw(data, "detailsList");
            }
        }
    });
    searchcompleteMultiselect._target.css("z-index", "10");

    suggestionHidden(searchcompleteMultiselect);
    /**
     * 必须放在  searchcomplete  init 后面
     * 鼠标点击空白   隐藏 下拉提示
     */
    $(document).bind("click", function(event) {
        var $target = $(event.target);
        if($target.attr("class").search(/ui-button/) != -1 && $target.text().search(/取消/) != -1){
            return;
        }
        var selectorArray = [
            $(".bcgogo-ui-searchcomplete-autocomplete,.bcgogo-ui-searchcomplete-detailsList-order,.bcgogo-ui-searchcomplete-searchBar", searchcompleteMultiselect._target),
            ".supplierSuggestion",
            ".customerSuggestion",
            "#ui-datepicker-div"
        ];

        // hack 我们伟大的 firefox 的 Dom engine bug
        if(event.target === document.lastChild) {
            suggestionHidden(searchcompleteMultiselect);
            return;
        }

        var isOnSearchBarDroplist = App.Module.droplist
            && App.Module.droplist._$relNode
            && App.Module.droplist._$relNode.closest(".bcgogo-ui-searchcomplete-searchBar").length > 0
            && event.target.className.search("bcgogo-droplist") !== -1;
        var isOnSearchBarDatepickerExceptionButtton = G.contains($(event.target).text(), ["<上月", "下月>"]);
        var isOnSearchBar = $(event.target).closest(selectorArray).length > 0;

        if(!(isOnSearchBar || isOnSearchBarDroplist || isOnSearchBarDatepickerExceptionButtton)) {
            suggestionHidden(searchcompleteMultiselect);
        }
    });

    if(!isDisable()) {
        $(".supplierSuggestion,.customerSuggestion").live("click", function(e) {
            var type;
            if($(this).hasClass("supplierSuggestion")) {
                type = "supplier";
            } else {
                type = "customer";
            }

            // add by zhuj
            searchSupplierOrCustomerSuggestion(searchcompleteMultiselect, this, null, type);
        }).live("keyup", function(event) {
                var eventKeyCode = event.which || event.keyCode;
                if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                    if($(this).val() != $(this).attr("lastValueForIe")) { //不能   使用"lastValue"
                        $(this).attr("lastValueForIe", $(this).val());
                        var type;
                        if($(this).hasClass("supplierSuggestion")) {
                            type = "supplier";
                        } else {
                            type = "customer";
                        }
                        // add by zhuj
                        searchSupplierOrCustomerSuggestion(searchcompleteMultiselect, this, eventKeyCode, type);
                    }
                }

            })
            .live('keypress', function (event) {
                var keyName = G.keyNameFromEvent(event);
                if(keyName === "enter" && !G.Lang.isEmpty($(this).val())){
                    searchOrderSuggestion(searchcompleteMultiselect, this, "");
                }
            })
            .live("focus", function() {
                $(this).attr("lastValueForIe", $(this).val());
            });
    }

    $(".operator").live("click focus keyup", function (event) {
        event = event || event.which;
        var keyCode = event.keyCode;
        if (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40) {
            return;
        }
        var obj = this;
        var domID =  $(this).attr("id").split(".");
        var hiddenId = "-";
        for (var i = 0; i < domID.length - 1; i++) {
            hiddenId += domID[i]+".";
        }
        hiddenId += "operatorId-";

        droplistLite.show({
            event: event,
            hiddenId: hiddenId,
            id: "idStr",
            name: "name",
            data: "member.do?method=getSaleMans"
        });
    });
});

//商品出入库打通 supplierInventory下拉收起
function closeSupplierInventoryList(){
    for(var i=0;i<$('[id$="_supplierInfo"]').size();i++){
        var supplierInfo=$('[id$="_supplierInfo"]').get(i);
        var flag_right=false;
        var flag_error=false;
        $(supplierInfo).find(".useRelatedAmount").each(function(){
            if($(this).attr("vflag")=="false"){
                flag_error=true;
            }
            if($(this).attr("vflag")=="true"){
                flag_right=true;
            }
        });
        if(!flag_error&&flag_right){
            $(supplierInfo).hide();
            if(getOrderType()=="RETURN_ORDER"){
                borrowOrderReturnStyleAdjust();
            }
        }
    }
}

function suggestionHidden(searchComplete) {
    searchComplete.myClickTimes = 0;
    searchComplete.hide();
    searchComplete.hide("autocomplete");
    searchComplete.hide("detailsList");
}

//不同页面 增加一行的function 调用不一样


function addProductItemRow(orderType, type, data) {
    if(type == "product") {
        if(data && orderType == "PURCHASE") {
            return purchaseOrderAdd();
        } else if(orderType == "REPAIR") {
            if(data) {
                var $last_tr = $("#table_productNo_2").find("tbody").find("tr:last");
                if($last_tr.find("input[id$='.productName']").val() && $last_tr.find("input[id$='.productName']").val() != "") {
                    var $new_tr = addNewRow(0);
                    isShowAddButton2();
                    return $new_tr;
                }
                return;
            }
        } else if(orderType == "RETURN") {
            return returnStorageOrderAdd();
        } else if(orderType == "INVENTORY_CHECK") {
            return inventoryCheckItemAdd();
        } else if(orderType == "SALE_RETURN") {
            return salesReturnAdd();
        } else if (orderType == "ALLOCATE_RECORD") {
            return addItemRow();
        } else if (orderType == "INNER_PICKING") {
            return innerPickingAdd();
        }else if (orderType == "BORROW_ORDER") {
            return borrowOrderAdd();
        }
        else if (orderType == "INNER_RETURN") {
            return innerReturnAdd();
        }else if (orderType == "INSURANCE_ORDER") {
            return insuranceItemAdd();
        } else if (orderType == "PRE_BUY_ORDER") {
            return preBuyOrderAddItemRow();
        } else if (orderType == "APPOINT_ORDER") {
            return addMaterialItem();
        } else {
            if (data) {
                return inventoryOrderAdd();
            }
        }
    } else if(type == "order") {
        if(data && data.item_type == "MATERIAL" && orderType == "PURCHASE") {
            return purchaseOrderAdd();
        } else if(orderType == "REPAIR") {
            if(data && data.item_type && data.item_type == "SERVICE") {
                var $last_tr = $("#table_task").find("tbody").find("tr:last");
                if($last_tr.find("input[type='text']:first").val() && $last_tr.find("input[type='text']:first").val() != "") {
                    var $new_tr = taskaddNewRow();
                    isShowAddButton();
                    return $new_tr;
                }
                return;
            } else if(data) {
                var $last_tr = $("#table_productNo_2").find("tbody").find("tr:last");
                if($last_tr.find("input[id$='.productName']").val() && $last_tr.find("input[id$='.productName']").val() != "") {
                    var $new_tr = addNewRow(0);
                    isShowAddButton2();
                    return $new_tr;
                }
                return;
            }
        } else if(orderType == "RETURN") {
            return returnStorageOrderAdd();
        } else if(orderType == "SALE_RETURN") {
            return salesReturnAdd();
        }else if (orderType == "ALLOCATE_RECORD") {
            return addItemRow();
        } else if(data && data.item_type == "MATERIAL") {
            return inventoryOrderAdd();
        } else if(orderType == "INVENTORY_CHECK") {
            return inventoryCheckItemAdd();
        } else if (orderType == "INNER_PICKING") {
            return innerPickingAdd();
        } else if (orderType == "INNER_RETURN") {
            return innerReturnAdd();
        }else if (orderType == "INSURANCE_ORDER") {
            return insuranceItemAdd();
        }
    }else if( type === "barcode" ) {
        // TODO 条码扫描仪 大驾光临, 勇士， 继续你的战斗吧

    }

}

function backupData($endDomObject) {
    var data = {};
    $endDomObject.parent().parent().find("input[type='text']").each(function() {
        data[$(this).attr("id").split(".")[1]] = $(this).val();
        if($(this).attr("id") == $endDomObject.attr("id")) {
            return false;
        }
    });
    return data;
}
//设置产品row的 值


function setProductItemValue($tr, data, orderType, type) {
    $tr.find("input[type='text']").each(function() {
        $(this).removeClass("j_search_word");
    });
    if($tr && data) {
        if(data.item_type && data.item_type == "SERVICE") {
            $tr.find("input[id$='.serviceId']").val(data.service_id);
            $tr.find("input[id$='.service']").val(data.services);
            $tr.find("select[id$='.consumeType']").val(data.consume_type);
            $tr.find("select[id$='.consumeType']").change();
            $tr.find("input[id$='.total']").val(data.item_price);
            if(data.service_id) { //获取service最新的营业分类
                APP_BCGOGO.Net.syncPost({
                    url: "category.do?method=getCategoryByServiceId",
                    data: {
                        "serviceId": data.service_id,
                        "now": new Date()
                    },
                    dataType: "json",
                    success: function(jsonObject) {
                        if(null != jsonObject.data) {
                            var id = jsonObject.data.idStr;
                            var name = jsonObject.data.categoryName;
                            id = null == id ? "" : id;
                            name = null == name ? "" : name;
                            $tr.find("input[id$='.businessCategoryId']").val(id);
                            $tr.find("input[id$='.businessCategoryName']").val(name);
                            $tr.find("input[id$='.businessCategoryName']").attr("hiddenValue", name);

                            if(name == "洗车" || name == "美容") {
                                $tr.find("input[id$='.businessCategoryName']").attr("disabled", "disabled");
                            }
                        }
                    }
                });
            }
        } else {
            new clearItemUtil().clearByFlag($tr.find("input[id$='.productName']")[0], true);
            $tr.find("input[id$='.productId']").val(data.product_id);
            $tr.find("input[id$='.productName']").val(data.product_name);
            $tr.find("input[id$='.commodityCode']").val(data.commodity_code);
            $tr.find("input[id$='.brand']").val(data.product_brand);
            $tr.find("input[id$='.model']").val(data.product_model);
            $tr.find("input[id$='.spec']").val(data.product_spec);
            $tr.find("input[id$='.vehicleBrand']").val(data.product_vehicle_brand);
            $tr.find("input[id$='.vehicleModel']").val(data.product_vehicle_model);
            if(verifyProductThroughOrderVersion(getOrderType())){
                $tr.find("input[id$='.amount']").val("");
            }else{
                $tr.find("input[id$='.amount']").val("1.0");
            }
            if(type == "product") {
                if(orderType == "INVENTORY") {
                    $tr.find("input[id$='.purchasePrice']").val(data.purchasePrice);
                } else if(orderType == "PURCHASE") {
                    $tr.find("input[id$='.price']").val(data.purchasePrice);
                    $tr.find("span[id$='.priceSpan']").val(data.purchasePrice);
                } else if(orderType == "REPAIR" || orderType == "INSURANCE_ORDER") {
                    $tr.find("input[id$='.price']").val(data.recommendedPrice);
                } else if(orderType == "SALE") {
                    $tr.find("input[id$='.price']").val(data.recommendedPrice);
                } else if(orderType == "INVENTORY_CHECK") {
                    $tr.find("input[id$='.inventoryAveragePrice']").val("");
                } else if(orderType == "RETURN") {
                    $tr.find("input[id$='.price']").val(data.purchasePrice);
                } else if(orderType == "SALE_RETURN") {
                    if(!G.isEmpty($("#customerId").val())) {
                        APP_BCGOGO.Net.syncPost({
                            url: "txn.do?method=getLatestCustomerPrice",
                            data: {
                                "customerId": $("#customerId").val(),
                                "productId": data.product_id
                            },
                            dataType: "json",
                            success: function(result) {
                               if(result.success && result.data != null) {
                                   data.recommendedPrice = result.data.itemPrice;
                               }
                            }
                        });
                    }
                    $tr.find("input[id$='.price']").val(data.recommendedPrice);
                } else if(orderType == "ALLOCATE_RECORD") {
                    $tr.find("input[id$='.costPrice']").val(data.inventoryAveragePrice);
                }else if(orderType == "INNER_PICKING" || orderType == "INNER_RETURN") {
                    $tr.find("input[id$='.price']").val(data.inventoryAveragePrice);
                }else if(orderType == "BORROW_ORDER" || orderType == "RETURN_ORDER") {
                    $tr.find("input[id$='.price']").val(data.inventoryAveragePrice);
                }



            } else if(type == "order") {
                if(orderType == "INVENTORY") {
                    $tr.find("input[id$='.purchasePrice']").val(data.item_price);
                } else if(orderType == "PURCHASE") {
                    $tr.find("input[id$='.purchasePrice']").val(data.item_price);
                } else if(orderType == "REPAIR") {
                    $tr.find("input[id$='.price']").val(data.item_price);
                } else if(orderType == "SALE") {
                    $tr.find("input[id$='.price']").val(data.item_price);
                } else if(orderType == "INVENTORY_CHECK") {
                    $tr.find("input[id$='.inventoryAveragePrice']").val("");
                } else if(orderType == "RETURN") {
                    $tr.find("input[id$='.price']").val(data.item_price);
                }
            }
        }
    }
}

function clearSearchWord($node, isClearText) {
    var flag = false;
    $node.find("input[type='text']").each(function() {
        if($(this).hasClass("j_search_word")) {
            flag = true;
            return false;
        }
    });
    if(flag) {
        $node.find("input[type='text']").each(function() {
            $(this).removeClass("j_search_word");
            if(isClearText) {
                $(this).val("");
            }
        });
    }
}

function showDropDownSuggestion(searchComplete, data) {
    suggestionHidden(searchComplete);
    searchComplete.show();
    searchComplete.show("autocomplete");
    searchComplete.draw(data, "autocomplete");
}

function showDetailSuggestion(searchComplete, data) {
    suggestionHidden(searchComplete);
    searchComplete.show();
    searchComplete.show("detailsList");
    searchComplete.draw(data, "detailsList");
}

//

function searchSupplierOrCustomerSuggestion(searchcompleteInstance, node, keycode, type) {
    var uuid = G.generateUUID();
    searchcompleteInstance.setUUID(uuid);
    searchcompleteInstance.moveFollow({
        node: node
    });
    var searchWord = node.value.replace(/[\ |\\]/g, "");
    //customer  Or  supplier
    var ajaxData = {
        searchWord: searchWord,
        uuid: searchcompleteInstance.getUUID(),
        customerOrSupplier: type,
        titles: "name,contact,mobile"
    };
    //如果是键盘输入
    if(keycode) {
        var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
        LazySearcher.lazySearch(ajaxUrl, ajaxData, function(json) {
            //默认匹配第一个值  自动补全  高亮
            ///不接受 backspace/上/下/左/右键
            if(json && json.data.length > 0 && $(node)[0] == $(document.activeElement)[0]) {
                var inputtingTimerId = 0;
                clearTimeout(inputtingTimerId);
                if(G.keyNameFromKeyCode(keycode).search(/left|up|right|down|enter|backspace/g) == -1) {
                    inputtingTimerId = setTimeout(function() {
                        APP_BCGOGO.Module.highlightcomplete.complete({
                            "selector": $(node),
                            "value": json.data[0].details["name"] //
                        });
                    }, 300);
                }
            }

            showDropDownSuggestion(searchcompleteInstance, json)
        });
    } else {
        APP_BCGOGO.Net.syncPost({
            url: "searchInventoryIndex.do?method=getCustomerSupplierSuggestion",
            data: ajaxData,
            dataType: "json",
            success: function(json) {
                showDropDownSuggestion(searchcompleteInstance, json)
            }
        });
    }

}
//
//


function getOrderSuggestionAjaxData(searchcompleteInstance, domObject) {
    var orderType = getOrderType();
    var pageOrderType = orderType;//销售退货单用到
    var orderTypes = "";
    var orderStatus="";
    if(orderType == "RETURN"){//退货单显示入库单
        orderTypes = "INVENTORY";
    }else if(orderType == "SALE_RETURN"){//销售退货单显示销售单和施工单
        orderTypes = "SALE,REPAIR";
        orderStatus = "REPAIR_SETTLED,SALE_DONE,SALE_DEBT_DONE";
    }else if(orderType == "PURCHASE"){
        orderTypes = "PURCHASE";
        orderStatus = "PURCHASE_ORDER_DONE";
    }else{
        orderTypes = orderType;
    }
    if (searchcompleteInstance.getPageIndex() < 0) {
      searchcompleteInstance.setPageIndex(0);
    }
    var ajaxData = {
        uuid: searchcompleteInstance.getUUID(),
        orderType: orderTypes,
        orderStatus: orderStatus,
        pageOrderType:pageOrderType,
        rowStart: searchcompleteInstance.getPageIndex() * searchcompleteInstance.getPageSize(),
        pageRows: searchcompleteInstance.getPageSize()
    };

    if(orderType == "REPAIR") {
        if($(domObject).attr("id") == "licenceNo") {
            ajaxData["vehicle"] = $("#licenceNo").val();
        }
        if(GLOBAL.Lang.endWith($(domObject).attr("id"), ".service")) {
            ajaxData["vehicle"] = $("#licenceNo").val();
            ajaxData["service"] = $(domObject).val();
        }
    } else {
        var customerOrSupplierName = domObject.value.replace(/[\ |\\]/g, "");
        ajaxData["customerOrSupplierName"] = customerOrSupplierName;
    }
    return ajaxData;
}

function searchOrderSuggestion(searchcompleteInstance, node, opt, async) {
    if("page" == opt) {
        searchcompleteInstance.moveFollow({
            node: node,
            isMoveOnly: true
        });
    } else {
        searchcompleteInstance.moveFollow({
            node: node,
            isMoveOnly: false
        });
    }

    var ajaxData = getOrderSuggestionAjaxData(searchcompleteInstance,node);
    APP_BCGOGO.Net[(async ? "async" : "sync") +"Post"]({
        url: "searchInventoryIndex.do?method=getOrderItemDetails",
        data: ajaxData,
        dataType: "json",
        success: function(json) {
            var hideSuggestion = false;
            if($(node).attr("class").search(/J-hide-empty-droplist/) != -1 && json.totalCount == 0){
                hideSuggestion = true;
            }
            if(!hideSuggestion){
                showDetailSuggestion(searchcompleteInstance, json);
            }
        }
    });
}

//点击 产品下拉建议 更多


function searchProductHistorySuggestionOnPage(node) {
    var invoiceCommon = APP_BCGOGO.Module.wjl.invoiceCommon;
    var idPrefix = node.id.split(".")[0];
    var idSuffix = node.id.split(".")[1];
    //TODO 不允许输入反斜杠和空格
    invoiceCommon.excludeSpaceSlash(idPrefix);
    var populateResult = invoiceCommon.populateSearchParams(idPrefix, idSuffix);
    var positionNum = populateResult.positionNum;
    var valueObject = populateResult.valueObject;
    //以下单据只显示选中的仓库中入过库的库存商品
    var showCurrentStorehouseOrders = ["REPAIR", "SALE", "RETURN", "ALLOCATE_RECORD", "INNER_PICKING", "BORROW_ORDER"];
    var showAllStorehouseProducts = true;

    var storehouseId = $(".j_checkStoreHouse").val();
    if($.inArray(getOrderType(), showCurrentStorehouseOrders) != -1){
        showAllStorehouseProducts = false;
    }
    var searchcomplete = APP_BCGOGO.Module.searchcomplete;
    var ajaxData = {
        searchStrategy: "detail",
        orderType: getOrderType(),
        includeBasic: getIncludeBasic(),
        searchWord: searchcomplete.lastSearchWord,
        searchField: positionNum,
        uuid: searchcomplete.getUUID(),
        supplierName: $("#supplier").val(),
        productName: valueObject.productValue,
        productBrand: valueObject.brandValue,
        productSpec: valueObject.specValue,
        productModel: valueObject.modelValue,
        vehicleBrand: valueObject.vehicleBrandValue,
        vehicleModel: valueObject.vehicleModelValue,
        storehouseId: storehouseId,
        showAllStorehouseProducts : showAllStorehouseProducts,
        start: searchcomplete.getPageIndex() * searchcomplete.getPageSize(),
        rows: searchcomplete.getPageSize()
    };
    APP_BCGOGO.Net.syncPost({
        url: "product.do?method=getProductSuggestionAndHistory",
        data: ajaxData,
        dataType: "json",
        success: function(json) {
            searchcomplete.hide();
            searchcomplete.hide("autocomplete");
            searchcomplete.hide("detailsList");
            searchcomplete.show();
            searchcomplete.show("autocomplete");
            searchcomplete.show("detailsList");
            searchcomplete.draw(json.history, "detailsList");
        }
    });
}
//产品



// TODO 此代码需要被复用到 searchcomplete 的键盘事件中, 在此之前先搞定如何在
// TODO     键盘逻辑中调用如下代码
function clearAndUpdateData(node, data, disableAutoNextFocus) {
    var inputSuffix = [
        ".productName",     ".brand",
        ".model",           ".spec",
        ".vehicleBrand",    ".vehicleModel"
    ],
        dataName = [
            "product_name",             "product_brand",
            "product_model",            "product_spec",
            "product_vehicle_brand",    "product_vehicle_model"
        ];

    // 遍歷， 并判斷當前的節點位置
//    for(var i = 0,len=inputSuffix.length;i<len;i++) {
//        if(G.Lang.endWith($(node).attr("id"), inputSuffix[i])) {
//            var valflag = $(node).val() != data[dataName[i]];
//            new clearItemUtil().clearByFlag(node, valflag);
//
//            // TODO 考慮 記錄 當前 node ， 和 index 用於改良現在版本的 清空策略
//            break;
//        }
//    }

    //设置关联input 的值
    var $tr = $(node).parent().parent();
    $tr.find("input[type='text']").each(function() {
        $(this).removeClass("j_search_word");
    });
    var $lastDataInputNode;
    if(data.commodity_code) {
        $tr.find("input[id$='.commodityCode']").val(data.commodity_code);
        $tr.find("input[id$='.commodityCode']").attr("blurLock", true);
        $lastDataInputNode = $tr.find("input[id$='.commodityCode']");
    }

    if(data.product_name) {
        $tr.find("input[id$='.productName']").val(data.product_name);
        $lastDataInputNode = $tr.find("input[id$='.productName']");
    }

    if(data.product_brand) {
        $tr.find("input[id$='.brand']").val(data.product_brand);
        $lastDataInputNode = $tr.find("input[id$='.brand']");
    }

    if(data.product_model) {
        $tr.find("input[id$='.model']").val(data.product_model);
        $lastDataInputNode = $tr.find("input[id$='.model']");
    }

    if(data.product_spec) {
        $tr.find("input[id$='.spec']").val(data.product_spec);
        $lastDataInputNode = $tr.find("input[id$='.spec']");
    }

    if(data.product_vehicle_brand||data.brand) {
        $tr.find("input[id$='.vehicleBrand']").val(data.product_vehicle_brand||data.brand);
        $lastDataInputNode = $tr.find("input[id$='.vehicleBrand']");
    }

    if(data.product_vehicle_model||data.model) {
        $tr.find("input[id$='.vehicleModel']").val(data.product_vehicle_model||data.model);
        $lastDataInputNode = $tr.find("input[id$='.vehicleModel']");
    }

    if(!disableAutoNextFocus)  {
        if($lastDataInputNode) {
            if($lastDataInputNode.attr("id").endWith(".commodityCode")) {
                getProductInfoByCommodityCode($lastDataInputNode);
                autoAddBlankRow($lastDataInputNode);
            }else {
                $lastDataInputNode.blur();
                var $nextText="";
                if($lastDataInputNode.attr("id").endWith(".vehicleBrand")&&
                    G.isEmpty($lastDataInputNode.closest("tr").find('[id$="productName"]').val())) {
                    $nextText = $lastDataInputNode.closest("tr").find('[id$="productName"]');
                }else{
                    $nextText = $lastDataInputNode.parent().next().find("input[type='text']").focus();
                }
                setTimeout(function() {
                    $nextText.click();
                }, 100);
            }

        }
    }

}


function getSupplierInventory($node) {
    if(!verifyProductThroughOrderVersion(getOrderType())){
        return;
    }
    if(!isSelectStorehouseForThroughOrder()){
        return;
    }
    if(getOrderType()=="RETURN"&&G.isEmpty($("#supplierId").val())){
        nsDialog.jAlert("请选择供应商！");
        return;
    }
    var idPrefix=$node.attr("id");
    idPrefix = idPrefix.substring(0, idPrefix.indexOf("."));
    var out_Prefix = Number($node.attr("id").substring(8,idPrefix.length));
    out_Prefix = "itemDTOs["+out_Prefix+"].outStorageRelationDTOs[";
    $("#"+idPrefix+'_supplierInfo').remove();
    $("#"+idPrefix+'_supplierInfo').die();
    var productId = $node.val();
    if(!productId) return;
    var storehouseId=$(".j_checkStoreHouse").val();
    var orderType=getOrderType();
    var orderStatus=getOrderStatus();
    var orderId;
    var outStorageItemId;
    if(orderStatus=="STOCKING"){
        orderId=$("#salesOrderId").val();
        outStorageItemId=$("#"+idPrefix+"\\.itemId").val();
    }
    var data={
        "productIds": productId,
        "storehouseId":storehouseId,
        orderType:orderType,
        orderStatus:orderStatus,
        outStorageItemId:outStorageItemId,
        orderId:orderId,
        productId:productId
    };

    APP_BCGOGO.Net.syncPost({
        url: "txn.do?method=getSupplierInventory",
        data: data,
        dataType: "json",
        cache: false,
        success: function(result) {
            var supplierInventoryDTOs=result[0];
            if(G.isEmpty(supplierInventoryDTOs)){
                return;
            }
            var trStr='<tr id="'+idPrefix+'_supplierInfo" class="supplierInfo"><td colspan="15"><div class="trList">';
            trStr+='<div class="t_title" style="text-align: left;">商品的供应商库存信息</div>';
            for(var i=0;i<supplierInventoryDTOs.length;i++){
                var supplierInventory=supplierInventoryDTOs[i];
                if(G.isEmpty(supplierInventory)){
                    continue;
                }
                var supplierName=G.normalize(supplierInventory.supplierName);
                var supplierId=supplierInventory.supplierIdStr;
                var remainAmount=G.rounding(supplierInventory.remainAmount,2)
                var tempAmount=G.rounding(supplierInventory.tempAmount,2)
                if(G.isEmpty(supplierId)){
                    supplierId="";
                }
                if(G.isEmpty(remainAmount)){
                    remainAmount="";
                }
                if(getOrderType()=="RETURN"&&$("#supplierId").val()!=supplierId){
                    continue
                }
                if(getOrderType()=='RETURN'&&getOrderStatus()=="SELLER_DISPATCH"){
                    continue;
                }
                var relatedSupplierAveragePrice =  dataTransition.rounding(supplierInventory.averageStoragePrice, 2) ;
                trStr+='<div class="divList" style="clear"><input type="hidden" value="'+supplierId+'" name="'+out_Prefix+i+'].relatedSupplierId" />' +
                    '<input type="hidden" value="'+supplierName+'" name="'+out_Prefix+i+'].relatedSupplierName" />' +
                    '<input type="hidden" value="'+relatedSupplierAveragePrice+'" name="'+out_Prefix+i+'].relatedSupplierAveragePrice" />' +
                    '<input type="hidden" value="'+remainAmount+'" name="'+out_Prefix+i+'].relatedSupplierInventory" />' +
                    '<input type="hidden" value="'+supplierInventory.supplierType+'" name="'+out_Prefix+i+'].supplierType" /><div style="width: 140px;padding-left: 5px;" class="supplierName" title="'+supplierName+'">'+tableUtil.limitLen(supplierName,8)+'</div>';
                 if(orderStatus=='PENDING'){
                    trStr+='<div style="width: 90px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">本次出货&nbsp;';
                    trStr+='<input type="text"  remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                }else if(orderStatus=='STOCKING'){
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
                } else if(getOrderType()=='RETURN'){
                    trStr+='<div style="width: 100px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">退货数量&nbsp;';
                    trStr+='<input type="text" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                } else if(getOrderType()=='ALLOCATE_RECORD'){
                    trStr+='<div style="width: 100px">剩余库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">调拨数量&nbsp;';
                    trStr+='<input type="text" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                } else if(getOrderType()=='INVENTORY_CHECK'){
                    trStr+='<div style="width: 100px">账面库存：<label class="remainAmount">'+remainAmount+'</label></div><div style="width: 120px">实际库存&nbsp;';
                    trStr+='<input type="text" value="'+remainAmount+'" remainAmount="'+supplierInventory.remainAmount+'"vFlag="" style="width:50px;" autocomplete="off" class="txt useRelatedAmount" name="'+out_Prefix+i+'].useRelatedAmount"/>';
                }
                trStr+='</div><div class="rightIcon" style="display: none;width:19px;"></div><div class="wrongIcon" style="display: none;width:60px;">库存不足！</div></div>';
            }
            trStr+='</div></td></tr>';
            $node.closest("tr").after(trStr);
            if($(".j_checkStoreHouse").val()){
                initAndbindSupplierInventoryList(idPrefix);
            }
        },
        error:function(){
            alert("网络异常，请重新尝试!");
        }
    });
}

//下拉列表输入校验
function verifySupplierInventoryAction($node){
    if(G.isEmpty($node)) return;
    $node.val(App.StringFilter.inputtingPriceFilter($node.val(),2));
    var useRelatedAmount=G.rounding($node.val(),2);
    var remainAmount=G.rounding($node.attr("remainAmount"),2);
    var orderType=getOrderType();
    var noNeedCheckOrders = ["INVENTORY_CHECK", "RETURN_ORDER", "SALE_RETURN", "INNER_RETURN","RETURN"];
    if(G.isEmpty(useRelatedAmount)||useRelatedAmount==0){
        $node.closest(".divList").find(".rightIcon").hide();
        $node.closest(".divList").find(".wrongIcon").hide();
        $node.attr("vFlag","");
    }else if(useRelatedAmount>remainAmount && $.inArray(orderType, noNeedCheckOrders)==-1){
        $node.closest(".divList").find(".rightIcon").hide();
        $node.closest(".divList").find(".wrongIcon").show();
        $node.attr("vFlag",false);
    }else{
        var $dList=$node.closest(".divList");
        $dList.find(".rightIcon").show();
        $dList.find(".wrongIcon").hide();
        $node.attr("vFlag",true);
    }
}


function initAndbindSupplierInventoryList(idPrefix){

    $("#"+idPrefix+"_supplierInfo .supplierName").each(function(){
        $(this).text(tableUtil.limitLen($(this).text(),8));
    });

    $("#"+idPrefix+"_supplierInfo .useRelatedAmount").live("keyup blur",function(){
        verifySupplierInventoryAction($(this));
        var itemTotal=0;
        var $dList=$(this).closest(".divList");
        $dList.closest(".supplierInfo").find(".useRelatedAmount").each(function(){
            if($(this).attr("vFlag")!="false"){
                itemTotal+=G.rounding($(this).val());
            }
            if(getOrderStatus()=="STOCKING"){
                itemTotal+=G.rounding($(this).attr("tempAmount"));
            }
        });
        if(getOrderStatus()=="STOCKING"){
            $("#" + idPrefix + "\\.amount").val(G.rounding(itemTotal));
        }else if(getOrderType()=="INVENTORY_CHECK"){
            $("#" + idPrefix + "\\.actualInventoryAmount").val(G.rounding(itemTotal,2));
        }else if(getOrderType()=="RETURN_ORDER"){
            borrowOrderReturnStyleAdjust();
            $("#" + idPrefix + "\\.returnAmount").val(G.rounding(itemTotal,2));
        }else{
            $("#" + idPrefix + "\\.amount").val(G.rounding(itemTotal,2));
        }
        if ($.isFunction(window.setTotal)) {
            setTotal();//每个模块都有自定义的
        }
    });
    var s_size= $("#"+idPrefix+"_supplierInfo").find("[name$='.relatedSupplierId']").size();
    var inventoryAmount= $("#"+idPrefix+"_supplierInfo").find(".remainAmount").text();
    if(s_size==0){
        $("#"+idPrefix+"_supplierInfo").remove();
        if(getOrderType()=="RETURN"){
            $("#" + idPrefix + "\\.inventoryAmount").val(0);
        }
    }
    if(s_size==1){
        if(getOrderType()=="INVENTORY_CHECK"){
            $("#" + idPrefix + "\\.actualInventoryAmount").removeClass("txtHover");
            $("#" + idPrefix + "\\.actualInventoryAmount").attr("readOnly",false);
        }else if(getOrderType()=="BORROW_ORDER"){
            $("#" + idPrefix + "\\.returnAmount ").removeClass("txtHover");
            $("#" + idPrefix + "\\.returnAmount ").attr("readOnly",false);
        }else if(getOrderType()=="RETURN"){
            $("#" + idPrefix + "\\.inventoryAmount").val(inventoryAmount);
        }else{
            $("#" + idPrefix + "\\.amount").removeClass("txtHover");
            $("#" + idPrefix + "\\.amount").attr("readOnly",false);
        }
    }else{
        if(getOrderType()=="INVENTORY_CHECK"){
            $("#" + idPrefix + "\\.actualInventoryAmount").addClass("txtHover");
            $("#" + idPrefix + "\\.actualInventoryAmount").attr("readOnly",true);
        }else if(getOrderType()=="RETURN_ORDER"){
            $("#" + idPrefix + "\\.returnAmount ").addClass("txtHover");
            $("#" + idPrefix + "\\.returnAmount ").attr("readOnly",true);
        }else{
            $("#" + idPrefix + "\\.amount").addClass("txtHover");
            $("#" + idPrefix + "\\.amount").attr("readOnly",true);
        }
    }

    if(getOrderStatus()=='PENDING'){
        var supplierInventoryTotal=0;
        $("#"+idPrefix+"_supplierInfo .useRelatedAmount").each(function(){
            supplierInventoryTotal+=G.rounding($(this).attr("remainAmount"));
        });
        $("#"+idPrefix+"\\.pAmount").attr("supplierInventoryTotal",supplierInventoryTotal);
        var pAmount= G.rounding($("#"+idPrefix+"\\.pAmount").text());
        if(pAmount>supplierInventoryTotal){
            $("#"+idPrefix+"_supplierInfo .useRelatedAmount").each(function(){
                $(this).val($(this).attr("remainAmount"));
                $(this).attr("readonly",true);
            });
            $("#"+idPrefix+"\\.amount").attr("readonly",true);
            $("#"+idPrefix+"_supplierInfo .useRelatedAmount").blur();
        }
    }

}

//根据 产品productId 校验并且补全数据
function checkProductData($node) {
    var idPrefix = $node.attr("id");
    idPrefix = idPrefix.substring(0, idPrefix.indexOf("."));
    var productId = $node.val();
    if(!productId) return;
    var storehouseId = "";
    if($(".j_checkStoreHouse")){
        storehouseId = $(".j_checkStoreHouse").val();
    }
    APP_BCGOGO.Net.syncPost({
        url: "searchInventoryIndex.do?method=ajaxInventorySearchIndex",
        data: {
            "productId": productId,
            "storehouseId":storehouseId
        },
        dataType: "json",
        cache: false,
        success: function(data) {
            if (data == null || data.productId == null){
                if ($("#" + idPrefix + "\\.inventoryAveragePrice")) {
                    $("#" + idPrefix + "\\.inventoryAveragePrice").val(0);
                }
                return; //Id 找不到对应的数据时
            }
            var itemUsedUnit = G.normalize($("#" + idPrefix + "\\.unit").val());
            var itemUsePurchasePrice = dataTransition.rounding(data.purchasePrice, 2);
            var itemInventoryAmount = dataTransition.rounding(data.amount, 2);
            var recommendedPrice = dataTransition.rounding(data.recommendedPrice, 2);
            var lowerLimit = dataTransition.rounding(data.lowerLimit, 0);
            var upperLimit = dataTransition.rounding(data.upperLimit, 0);
            var tradePrice = dataTransition.simpleRounding(data.tradePrice, 2);
            var inventoryAveragePrice = dataTransition.simpleRounding(data.inventoryAveragePrice, 2);
            if (!G.isEmpty(itemUsedUnit) && itemUsedUnit == data.storageUnit && data.storageUnit != data.sellUnit && data.rate != 0) { //填写的单位是大单位时
                inventoryAveragePrice = dataTransition.simpleRounding(data.inventoryAveragePrice * data.rate, 2);
                itemUsePurchasePrice = dataTransition.rounding(data.purchasePrice * data.rate, 2);
                itemInventoryAmount = dataTransition.rounding(data.amount / data.rate, 2);
                recommendedPrice = dataTransition.rounding(data.recommendedPrice * data.rate, 2);
                tradePrice = dataTransition.simpleRounding(data.tradePrice * data.rate, 2);
                lowerLimit = dataTransition.rounding(dataTransition.rounding(data.lowerLimit, 0) / data.rate, 0);
                upperLimit = dataTransition.rounding(dataTransition.rounding(data.upperLimit, 0) / data.rate, 0);
            } else if (!G.isEmpty(itemUsedUnit) && itemUsedUnit != data.storageUnit && itemUsedUnit != data.sellUnit && !G.isEmpty(data.sellUnit)) { //填写的单位既不是大单位，也不是小单位
                $("#" + idPrefix + "\\.unit").val(G.normalize(data.sellUnit));
            }else if(G.isEmpty(itemUsedUnit) && isCanEditUnit()){
                $("#" + idPrefix + "\\.unit").val(G.normalize(data.unit));
            }
            $("#" + idPrefix + "\\.productId").val(G.normalize(data.productIdStr));

            $("#" + idPrefix + "\\.inventoryAmount").val(itemInventoryAmount);
            $("#" + idPrefix + "\\.purchasePrice").val(itemUsePurchasePrice);
            $("#" + idPrefix + "\\.inventoryAmountSpan").html(G.rounding(itemInventoryAmount, 2));
            $("#" + idPrefix + "\\.inventoryAmount").css('display', "block");
            $("#" + idPrefix + "\\.inventoryAmountSpan").css('display', "block");
            $("#" + idPrefix + "\\.storageUnit").val(G.normalize(data.storageUnit));
            $("#" + idPrefix + "\\.sellUnit").val(G.normalize(data.sellUnit));
            $("#" + idPrefix + "\\.rate").val(G.normalize(data.rate));
            if ($("#" + idPrefix + "\\.businessCategoryName")) {
                $("#" + idPrefix + "\\.businessCategoryName").val(G.normalize(data.businessCategoryName));
                $("#" + idPrefix + "\\.businessCategoryName").attr("hiddenValue", $("#" + idPrefix + "\\.businessCategoryName").val());
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

            if ($("#" + idPrefix + "\\.inventoryAmount")[0]) {
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
            if ($id_price.length > 0 && ($.trim($id_price.val()) == "" || $id_price.val() == 0 || $id_price.val() == 0.0)) {
                var price = recommendedPrice;
                if(getOrderType() == "PURCHASE"){
                    price = itemUsePurchasePrice;
                }
                $id_price.val(price).data("recommendedPrice", price);
                $id_price.data("recommendedPriceUnit", data.sellUnit);
            } else if ($id_price.length && $id_price.val() * 1 <= inventoryAveragePrice) {
                // TODO 逻辑有错误 ，需求是这样的 需要在多次点击时 只弹出一次
                var excludeOrders = ["RETURN", "SALE_RETURN", "INNER_RETURN", "BORROW_ORDER", "INNER_PICKING"];
                if ($.inArray(getOrderType(), excludeOrders) == -1 && typeof(showMessage)!= "undefined") {
                    if($id_price.val() * 1<inventoryAveragePrice){
                        nsDialog.jAlert("友情提示：该商品的销售价低于成本均价" + dataTransition.rounding(inventoryAveragePrice, 2) + "元，请核查是否输入正确！");
                    }else if($id_price.val() * 1==inventoryAveragePrice){
                        nsDialog.jAlert("友情提示：该商品的销售价等于成本均价" + dataTransition.rounding(inventoryAveragePrice, 2) + "元，请核查是否输入正确！");
                    }

                }
            }

            if (getOrderType() == "RETURN") {
                $("#" + idPrefix + "\\.inventoryAmount").css('display', "inline");
                $id_price.val(itemUsePurchasePrice);
                  $("#" + idPrefix + "\\.inventoryAveragePrice").text(inventoryAveragePrice);
            }
            if (getOrderType() == "ALLOCATE_RECORD") {
                $("#" + idPrefix + "\\.inventoryAmount").css('display', "inline");
                $("#" + idPrefix + "\\.costPrice").val(inventoryAveragePrice);
            }
            if (getOrderType() == "INVENTORY_CHECK") {
                $("#" + idPrefix + "\\.actualInventoryAmount").val(G.rounding(itemInventoryAmount, 2));
                $("#" + idPrefix + "\\.inventoryAmountAdjustment").val(0);

                $("#" + idPrefix + "\\.inventoryAveragePrice").val(inventoryAveragePrice);
                $("#" + idPrefix + "\\.actualInventoryAveragePrice").val(inventoryAveragePrice);
                $("#" + idPrefix + "\\.inventoryAdjustmentPrice").val(0);
                $("#" + idPrefix + "\\.inventoryAveragePriceAdjustment").val(0);
                $("#" + idPrefix + "\\.inventoryAmountUnit").val(itemInventoryAmount + G.normalize(data.sellUnit));
            }
            if (getOrderType() == "SALE_RETURN") {
                $("#" + idPrefix + "\\.originSalesPriceSpan").text(recommendedPrice);
                $("#" + idPrefix + "\\.originSalesPrice").val(recommendedPrice);
//                $("#" + idPrefix + "\\.saleReturnUnitSpan").text(G.normalize(data.sellUnit));

            }
            if (getOrderType() == "INNER_PICKING") {
                $("#" + idPrefix + "\\.inventoryAmountSpan").css('display', "inline");
                $("#" + idPrefix + "\\.price_span").text(inventoryAveragePrice);
                $("#" + idPrefix + "\\.price").val(inventoryAveragePrice);
                $("#" + idPrefix + "\\.unit").val(G.normalize(data.sellUnit));
                $("#" + idPrefix + "\\.unit_span").text(G.normalize(data.sellUnit));
            }
            if (getOrderType() == "BORROW_ORDER") {
                $("#" + idPrefix + "\\.inventoryAmountSpan").css('display', "inline");
                $("#" + idPrefix + "\\.price_span").text(inventoryAveragePrice);
                $("#" + idPrefix + "\\.price").val(inventoryAveragePrice);
            }
            if (getOrderType() == "INNER_RETURN") {
                $("#" + idPrefix + "\\.price_span").text(inventoryAveragePrice);
                $("#" + idPrefix + "\\.price").val(inventoryAveragePrice);
            }
            if (getOrderType() == "INSURANCE_ORDER") {
                $("#" + idPrefix + "\\.price").val(recommendedPrice);
                $("#" + idPrefix + "\\.unit").val(G.normalize(data.sellUnit));
                $("#" + idPrefix + "\\.unitLbl").text(G.normalize(data.sellUnit));
            }
            if(getOrderType() == "PRE_BUY_ORDER") {
                var imageCenterDTO = data.imageCenterDTO;
                if($.isFunction(refreshProductImageCmp)){
                    refreshProductImageCmp(idPrefix,imageCenterDTO);
                }
            }
            if (getOrderType() == "REPAIR"){
                isLack();
            }
            $("#" + idPrefix + "\\.commodityCode").val(G.normalize(data.commodityCode));
            if (typeof(initUnitTd) == "function") {
                if($("#" + idPrefix + "\\.unit").size()!=0){
                    initUnitTd($("#" + idPrefix + "\\.unit"));
                }
            }
            if ($.isFunction(window.setTotal)) {
                setTotal();
            }
            getSupplierInventory($node);
        },
        error: function() {
            alert("网络异常，请重新尝试!");
        }
    });
}

function checkSupplierById(supplierId) {
    APP_BCGOGO.Net.asyncPost({
        url: "RFSupplier.do?method=ajaxSearchSupplierById",
        data: {
            shopId: $("#shopId").val(),
            supplierId: supplierId
        },
        cache: false,
        dataType: "json",
        error: function(XMLHttpRequest, error, errorThrown) {
            $("#supplierId").val('');
            if($("#supplierShopId")) $("#supplierShopId").val('');
        },
        success: function(json) {
            if(!json || !json[0]) return;
            var jsonData = json[0];
            $("#supplierId").val(jsonData.idString);
            $("#supplier").val(jsonData.name);

            if($("#supplierShopId")) $("#supplierShopId").val(jsonData.supplierShopIdString);
            $("#select_province, #select_province_input").val(jsonData.province);
            $("#select_province").change();
            $("#select_city, #select_city_input").val(jsonData.city);
            $("#select_city").change();
            $("#select_township, #select_township_input").val(jsonData.region);
            $("#input_address").val(jsonData.address);
            $("#select_province,#select_city,#select_township,#input_address").css("color","#272727");
            $("#contactId").val(G.Lang.normalize(jsonData.contactIdStr)); // add by zhuj
            $("#contact").val(jsonData.contact);
            $("#mobile").val(jsonData.mobile);
            $("#hiddenMobile").html(jsonData.mobile);
            $("#address").val(jsonData.address);
            $("#bank").val(jsonData.bank);
            $("#account").val(jsonData.account);
            $("#accountName").val(jsonData.accountName);
            $(".warehouseList :checkbox[name=businessScope1]").attr("checked",false);
            if(!jsonData.businessScope) {
                $("#businessScope").val("");
            } else {
                $("#businessScope").val(jsonData.businessScope);
                var scopes = jsonData.businessScope.split(",");
                for(var i= 0;i < scopes.length;i++) {
                    $(".warehouseList :checkbox[name=businessScope1]").each(function(index,checkbox){
                        if($(checkbox).val() == scopes[i]) {
                            $(checkbox).attr("checked",true);
                            return false;
                        }  else {
                            if(index == $(".warehouseList :checkbox[name=businessScope1]").length - 1) {
                                if(i == scopes.length - 1) {
                                    $("#otherCheckbox").val($("#otherInput").val() + scopes[i]);
                                    $("#otherCheckbox").attr("checked",true);
                                    $("#otherInput").val($("#otherInput").val() + scopes[i]);
                                } else {
                                    $("#otherCheckbox").val($("#otherInput").val() + scopes[i] + ',');
                                    $("#otherCheckbox").attr("checked",true);
                                    $("#otherInput").val($("#otherInput").val() + scopes[i] + ',');
                                }
                            }

                        }
                    });
                }
            }
            $("#category").val(jsonData.category);
            $("#abbr").val(jsonData.abbr);
            $("#settlementType").val(jsonData.settlementTypeId);
            if(jsonData.landLine!=""){
                $("#landline").val(jsonData.landLine);
            }else if(jsonData.landLineSecond!==""){
                $("#landline").val(jsonData.landLineSecond);
            }else if(jsonData.landLineThird!==""){
                $("#landline").val(jsonData.landLineThird);
            }
            $("#fax").val(jsonData.fax);
            $("#qq").val(jsonData.qq);
            $("#invoiceCategory").val(jsonData.invoiceCategoryId);
            $("#email").val(jsonData.email);
            if(jsonData.totalDebt){
                $("#receivable").html(G.rounding(jsonData.totalDebt,2));
            }else{
                $("#receivable").html("0");
            }
            if(jsonData.totalPayable){
                $("#payable").html(G.rounding(jsonData.totalPayable,2));
            }else{
                $("#payable").html("0");
            }
            if(getOrderType() == "BORROW_ORDER"){
                $("#customerOrSupplierId").val(jsonData.idString);
                $("#name").val(jsonData.name);
                var contacts = jsonData.contacts;
                if(contacts){
                    for(var i= 0,len = contacts.length;i<len;i++){
                      if(contacts[i] && contacts[i].idStr && contacts[i].idStr ==$("#contactId").val()){
                          $("#email").val(G.Lang.normalize(contacts[i].email));
                          $("#qq").val(G.Lang.normalize(contacts[i].qq));
                      }
                    }
                }
            }
            if(verifyProductThroughOrderVersion(getOrderType())&&!G.isEmpty($("#storehouseId").val())){
                if(getOrderType()=="RETURN"){
                    $(".j_checkStoreHouse").change();
                }
            }
        }
    });
}

function checkCustomerById(customerId, notClearVehicleInfo) {
    var ajaxUrl = "sale.do?method=searchCustomerById";
    var ajaxData = {
        customerId: customerId
    };
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(data) {
        if(data.infos.length > 0) {
            //原始数据清空
            $("#customerId").val("");
            $("#customer").val("");
            $("#contact").val("");
            $("#mobile").val("");
            $("#hiddenMobile").val("");
            $("#returnInfo").val("");
            $("#landline").val("");
            $("#contactId").val(""); //add by zhuj

            $("#shortName").val("");
            $("#email").val("");
            $("#bank").val("");
            $("#account").val("");
            $("#accountName").val("");
            $("#qq").val("");
            $("#mobile").val("");
            $("#fax").val("");
            $("#address").val("");
            $("#settlementType").val("");
            $("#category").val("");
            $("#invoiceCategory").val("");
            if (!notClearVehicleInfo) {
                $('#vehicleId').val('');
                $('#licenceNo').val('');
                $('#vehicleContact').val('');
                $('#vehicleMobile').val('');
            }

            $("#customerId").val(data.infos[0].idStr);
            $("#customer").val(data.infos[0].name);
            if(getOrderType() == "BORROW_ORDER"){
                $("#customerOrSupplierId").val(data.infos[0].idStr);
                $("#name").val(data.infos[0].name);
            }
            $("#contactId").val(G.Lang.normalize(data.infos[0].contactIdStr)); // add by zhuj
            $("#contact").val(data.infos[0].contact);
            $("#mobile").val(data.infos[0].mobile);
            $("#landline").val(data.infos[0].landline);
            $("#hiddenMobile").val(data.infos[0].mobile);

            $("#shortName").val(G.normalize(data.infos[0].shortName));
            $("#email").val(G.normalize(data.infos[0].email));
            $("#bank").val(G.normalize(data.infos[0].bank));
            $("#account").val(G.normalize(data.infos[0].account));
            $("#qq").val(G.normalize(data.infos[0].qq));
            $("#mobile").val(G.normalize(data.infos[0].mobile));
            $("#fax").val(G.normalize(data.infos[0].fax));
            $("#address").val(G.normalize(data.infos[0].address));
            $("#accountName").val(G.normalize(data.infos[0].bankAccountName));
            $("#settlementType").val(G.normalize(data.infos[0].settlementType));
            $("#category").val(G.normalize(data.infos[0].customerKind));
            $("#invoiceCategory").val(G.normalize(data.infos[0].invoiceCategory));
            $("#returnInfo").remove();
            if (typeof clearReadOnly == 'function') {
                clearReadOnly();
                showLicenceNo(null, $("#customerId").val());
                setReadOnly();
            }
            //查询累计欠款
            if($("#customerId").val()) {
                var ajaxUrl = "sale.do?method=getTotalDebts";
                var ajaxData = {
                    shopId: $("#shopId").val(),
                    customerId: $("#customerId").val()
                };
                bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
                bcgogoAjaxQuery.ajaxQuery(function(data) {
                    $("#customerConsume").html(data.totalAmount);

                    if(data.totalDebt){
                        $("#receivable").html(dataTransition.rounding(data.totalDebt,2));
                    }else{
                        $("#receivable").html("0");
                    }
                    if(data.totalReturnDebt){
                        $("#payable").html(dataTransition.rounding(data.totalReturnDebt,2));
                    }else{
                        $("#payable").html("0");
                    }

//                    initDuiZhanInfo();
                });
            }
        }


    }, function(XMLHttpRequest, error, errorThrown) {
        //原始数据清空
        $("#customerId").val("");
        $("#customer").val("");
        $("#contactId").val(""); // add by zhuj
        $("#contact").val("");
        $("#mobile").val("");
        $("#hiddenMobile").html("");
        $("#returnInfo").val("");
        $("#landline").val("");
        $("#shortName").val("");
        $("#email").val("");
        $("#bank").val("");
        $("#account").val("");
        $("#accountName").val("");
        $("#qq").val("");
        $("#fax").val("");
        $("#address").val("");
        $("#settlementType").val("");
        $("#category").val("");
        $("#invoiceCategory").val("");
    });
}

function isSearchRow($row) {
    var flag = false;
    $row.find("input[type='text']").each(function() {
        if($(this).hasClass("j_search_word")) {
            flag = true;
            return false;
        }
    });
    return flag;
}

function getRepairOrderProductRowByExistsData($table, data) {
    var selectedData = data.commodity_code + "_" + data.product_name + "_" + data.product_brand + "_" + data.product_model + "_" + data.product_spec;
    var $tr;
    $table.find(".item1").each(function() {
        if(!GLOBAL.Lang.isEmpty(data.product_id) && data.product_id == $(this).find("input[id$='.productId']").val()) {
            $tr = $(this);
            return false;
        }
    });

    if(!$tr) {
        $table.find(".item1").each(function() {
            if(G.isEmpty($(this).find("input[id$='.productId']").val())) {
                var oldItemData = "";
                oldItemData += $(this).find("input[id$='.commodityCode']").val() + "_";
                oldItemData += $(this).find("input[id$='.productName']").val() + "_";
                oldItemData += $(this).find("input[id$='.brand']").val() + "_";
                oldItemData += $(this).find("input[id$='.model']").val() + "_";
                oldItemData += $(this).find("input[id$='.spec']").val();
                if(selectedData == oldItemData && !isSearchRow($(this))) {
                    $tr = $(this);
                    return false;
                }
            }
        });
    }

    return $tr;
}

function getRowByExistsData($table, data,type) {
    //    var selectedData = data.commodity_code + "_" + data.product_name + "_" + data.product_brand + "_" + data.product_model + "_" + data.product_spec + "_" +
    //        data.product_vehicle_brand + "_" + data.product_vehicle_model;
    var selectedData = data.product_name + "_" + data.product_brand + "_" + data.product_model + "_" + data.product_spec + "_" + data.product_vehicle_brand + "_" + data.product_vehicle_model;
    var $tr;
    //先根据product_id 找 //遍历有product_id
    $table.find(".item").each(function() {
        if(!G.isEmpty(data.product_id) && data.product_id == $(this).find("input[id$='.productId']").val()) {
            $tr = $(this);
            return false;
        }
    });

    //如果没有找到 根据名称等属性找  遍历没有product_id的
    if(!$tr) {
        $table.find(".item").each(function() {
            if(G.isEmpty($(this).find("input[id$='.productId']").val())) {
                if(selectedData == getOldItemData($(this)) && !isSearchRow($(this))) {
                    $tr = $(this);
                    return false;
                }
            }
        });
    }
    return $tr;
}
function getOldItemData($item){
    var oldItemData = "";
    //                oldItemData += $(this).find("input[id$='.commodityCode']").val() + "_";
    oldItemData += $item.find("input[id$='.productName']").val() + "_";
    oldItemData += $item.find("input[id$='.brand']").val() + "_";
    oldItemData += $item.find("input[id$='.model']").val() + "_";
    oldItemData += $item.find("input[id$='.spec']").val() + "_";
    oldItemData += $item.find("input[id$='.vehicleBrand']").val() + "_";
    oldItemData += $item.find("input[id$='.vehicleModel']").val();
    return oldItemData;
}

function updateSupplierInventoryUnit(){

}

function checkHasEffectiveContact(contacts) {
    var isHasEffectiveContact = false;
    if (contacts) {
        for (var i = 0, len = contacts.length; i < len; i++) {
            if (contacts[i] && !GLOBAL.Lang.isEmpty(contacts[i].id)) {
                isHasEffectiveContact = true;
                break;
            }
        }
    }
    return isHasEffectiveContact;
}

function contactDeal(flag){
    if(flag){
        // add by zhuj 设置页面联系人相关信息为readonly
        $("#contact").attr("readonly","true");
        $("#mobile").attr("readonly","true");
        $("#qq").attr("readonly","true");
        $("#email").attr("readonly","true");
        $("#isAdd").val("false");
    }else{
        $("#contact").attr("readonly","");
        $("#mobile").attr("readonly","");
        $("#qq").attr("readonly","");
        $("#email").attr("readonly","");
        //$("#isAdd").val("true");
    }
}
