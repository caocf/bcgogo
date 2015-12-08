//主要用于库存查询，及商品首页部分事件和逻辑
var nextPageNo = 1;
var isTheLastPage = false;
/*------------------复选框--------------------*/
var checkedIds = ""; //todo to delete ，用 selectedArr[]  来替代
var checkedAmounts = "";
//记录查询关键字，查询条件。
var input_search_pName_keyWord = $("#productName").val();
var updateSingleLimitFlag = true; //false 表示更新是上下限需要对调位置 ,true表示不需要
var search_Switch = false; //true 表示多条件搜索或者库存告警搜索，false 表示从head中商品名称查找
//var ajaxSearchSumFlag = "1";//1 按了按钮才发出请求，下一页不用发请求   0，表示下一页
var dynamicalID;
var selectedArr = []; //用于存放勾选中商品的的id和数量
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;



function map() {
    var struct = function (key, value) {
        this.key = key;
        this.value = value;
    }

    var put = function (key, value) {
        for (var i = 0; i < this.arr.length; i++) {
            if (this.arr[i].key === key) {
                this.arr[i].value = value;
                return;
            }
        }
        this.arr[this.arr.length] = new struct(key, value);
    }

    var get = function (key) {
        for (var i = 0; i < this.arr.length; i++) {
            if (this.arr[i].key === key) {
                return this.arr[i].value;
            }
        }
        return null;
    }

    var remove = function (key) {
        var v;
        for (var i = 0; i < this.arr.length; i++) {
            v = this.arr.pop();
            if (v.key === key) {
                break;
            }
            this.arr.unshift(v);
        }
    }

    var size = function () {
        return this.arr.length;
    }

    var isEmpty = function () {
        return this.arr.length <= 0;
    }

    var clearMap = function () {
        this.arr = [];
    }
    this.arr = new Array();
    this.get = get;
    this.put = put;
    this.remove = remove;
    this.size = size;
    this.isEmpty = isEmpty;
    this.clearMap = clearMap;
}
var jsonStrMap = new map();

$(function () {
    $(".i_leftBtn").hide();
    $(".i_bottom").show();
    $(".J-more").click(function(event) {
        var tab = $(this).next();
        var pendingStorage = $("#pendingStorageDetail");
        var lackStorage = $("#lackStorageDetail");
        if(event.target == $("#purchaseRemindCount").parent()[0]){
            if (tab.css("display") == "none") {
                tab.show();
                $('.arrow_down', this).css("background", "url('images/arrowsUp.png') no-repeat right");
            }
            lackStorage.hide();
            pendingStorage.show();
        }else if(event.target == $("#repairLackCountSpan").parent()[0]){
            if (tab.css("display") == "none") {
                tab.show();
                $('.arrow_down', this).css("background", "url('images/arrowsUp.png') no-repeat right");
            }
            lackStorage.show();
            pendingStorage.hide();
        }else{
            if (tab.css("display") == "none") {
                lackStorage.show();
                pendingStorage.show();
                tab.show();
                $('.arrow_down', this).css("background", "url('images/arrowsUp.png') no-repeat right");
            } else {
                tab.hide();
                $('.arrow_down', this).css("background", "url('images/arrowsDown.png') no-repeat right");
            }
        }

    });
    if(!APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.ProductClassify){
        $("#_productType").attr("readonly",true);
    }
    if(!APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetUnit){
        $("#_storageUnit").attr("readonly",true);
        $("#_sellUnit").attr("readonly",true);
    }
    $("#clearConditionBtn").bind('click', function () {
        $(".J_clear_input").val("").css("color", "#ADADAD");
        $(".J-initialCss").placeHolder("reset");
    });

    $(":text").attr("autocomplete", "off");
    //update RecommendedPrice
    //TODO ajax设置销售价
    $(".recommendedPrice_input").live("blur",function (e) {
        if ($(e.target).attr("lastValue") == $(e.target).val()) {
            return;
        }
        var price = $(e.target).val();
        if (isNaN(price) || price < -0.0001) {
            $(this).val("");
            nsDialog.jAlert("请输入正确的价格!");
            return false;
        }
        if (!$.trim(price)) price = 0.0;
        price = dataTransition.simpleRounding(price, 2);
        $(e.target).val(price);
        if ($(e.target).next().text() == null || $.trim($(e.target).next().text()) == "" || $(e.target).next().text() == "null") {
            return false;
        }
        var prefix = $(e.target).attr("id").split(".")[0];
        var rowId = prefix.substring(11, prefix.length);
        var ajaxUrl = "txn.do?method=ajaxUpdateRecommendedPrice";
        var ajaxData = {
            id: $("#" + prefix + "\\.productLocalInfoId").val(),
            //product_local_info ID
            price: price
        };
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.setAsyncAndCache(false, false);
        bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
            //             console.log(jsonStr);
            bindProductDetail(rowId);
        });
        $(this).attr("lastValue", $(this).val());
    }).live("focus", function () {
            $(this).attr("lastValue", $(this).val());
        });

    $(".tradePrice_input").live("blur",function (event) {
        if ($(this).attr("lastValue") == $(this).val()) {
            return;
        }
        var tradePrice = $(this).val();
        var idPrefix = $(this).attr("id").split(".")[0];
        var rowId = idPrefix.substring(11, idPrefix.length);
        var productId = $("#" + idPrefix + "\\.productLocalInfoId").val();

        if (!productId) {
            return;
        }

        if (!G.Lang.isNumber(tradePrice) || tradePrice < 0) {
            $(this).val($(this).attr("lastValue"));
            nsDialog.jAlert("请输入正确的价格!");
            return false;
        }
        tradePrice = dataTransition.simpleRounding(tradePrice, 2);
        if (tradePrice == 0) {
            $(this).val($(this).attr("lastValue"));
            nsDialog.jAlert("批发价不能为0,请重新输入!");
            return false;
        }
        var inventoryAveragePrice = $(this).parents("tr").find("input[id$='.inventoryAveragePrice']").val();
        if (tradePrice <= inventoryAveragePrice * 1) {
            var $domObject = $(this);
            nsDialog.jConfirm("友情提示：当前商品批发价小于或者等于均价，是否确认继续修改？", null, function (returnVal) {
                if (returnVal) {
                    $(this).val(tradePrice);
                    var ajaxUrl = "txn.do?method=updateSingleTradePrice";
                    var ajaxData = {
                        productId: productId,
                        //product_local_info ID
                        tradePrice: tradePrice
                    }
                    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
                    bcgogoAjaxQuery.setAsyncAndCache(false, false);
                    bcgogoAjaxQuery.ajaxQuery(function () {
                        //clear last callBack function
                        bindProductDetail(rowId);
                    });
                    $(this).attr("lastValue", $(this).val());
                } else {
                    $domObject.val($domObject.attr("lastValue"));
                }
            });
        } else {
            $(this).val(tradePrice);
            var ajaxUrl = "txn.do?method=updateSingleTradePrice";
            var ajaxData = {
                productId: productId,
                //product_local_info ID
                tradePrice: tradePrice
            }
            bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
            bcgogoAjaxQuery.setAsyncAndCache(false, false);
            bcgogoAjaxQuery.ajaxQuery(function () {
                //clear last callBack function
                bindProductDetail(rowId);
            });
            $(this).attr("lastValue", $(this).val());
        }

    }).live("focus", function () {
            $(this).attr("lastValue", $(this).val());
        });

    $(".J-storageBin").live("blur",function (event) {
        $(this).val($.trim($(this).val()));
        if ($(this).attr("lastValue") == $(this).val()) {
            return;
        }
        var storageBin = $(this).val();
        var idPrefix = $(this).attr("id").split(".")[0];
        var rowId = idPrefix.substring(11, idPrefix.length);
        var productId = $("#" + idPrefix + "\\.productLocalInfoId").val();
        if (!productId) {
            return;
        }
        var ajaxUrl = "txn.do?method=updateSingleStorageBin";
        var ajaxData = {
            productId: productId,
            storageBin: storageBin
        }
        bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
        bcgogoAjaxQuery.setAsyncAndCache(false, false);
        bcgogoAjaxQuery.ajaxQuery(function () {
            //clear last callBack function
            bindProductDetail(rowId);
        });
        $(this).attr("lastValue", $(this).val());

    }).live("focus", function () {
            $(this).attr("lastValue", $(this).val());
        });

    //库存查询搜索按钮
    // 使用ajax查询商品库存   只有明确搜索内容 对应的 field
    $("#searchInventoryBtn").live("click", function () {
        if($("#fromPage").val() != 'stationMessage')  {
            //todo 清数据
            if ($("#searchProductIds")) {
                $("#searchProductIds").val("");
            }
        }
        clearSort();
        getProductWithUnknownField();

    });

    $("[id$='.productInfo'],[id$='.titBottom']").live("click", function (e) {
        if((e.target.tagName == "INPUT" || e.target.tagName == "A" || $(e.target).hasClass("J-productNameInfo") || $(e.target).hasClass("J-inOffSaleIcon") )
            && !$(e.target).hasClass("btnDown") ){
            return;
        }
        var idPrefix = $(this).attr("id").split(".")[0];
        //删除其他productDetail
        $("[id$='.productDetail']").each(function () {
            var itemIdPrefix =  $(this).attr("id").split(".")[0];
            if(itemIdPrefix != idPrefix){
                $(this).remove();
            }

        });
        if ($("#" + idPrefix + "\\.productDetail").size() > 0) {
            $("[id$='.productDetail']").each(function () {
                $(this).remove();
            });
        } else {
            var index = idPrefix.substring(11, idPrefix.length);
            drawProductDetail(index);
            bindProductDetail(index);
        }

    }).live("mouseenter",function(e){

            var idPrefix = $(this).attr("id").split(".")[0];
            $("[id$='.titBottom']").each(function(){
                $(this).find(".div_Btn").hide();
            });
            $("#"+idPrefix + "\\.titBottom").find(".div_Btn").show();
        }).live("mouseleave",function (e){
            var idPrefix = $(this).attr("id").split(".")[0];
            if ( $(e.relatedTarget).parents("#" + idPrefix + "\\.productInfo").length > 0 || $(e.relatedTarget).parents("#" + idPrefix + "\\.titBottom").length > 0
                ) {
                return;
            }
            $("#"+idPrefix + "\\.titBottom").find(".div_Btn").hide();
        });

    //ajax 加载 库存告急信息
    if ($("#pageType").val() == "stockSearch" || $("#pageType").val() == "goodsIndex") {
        var ajaxUrl = "txn.do?method=getLimitCount";
        bcgogoAjaxQuery.setUrlData(ajaxUrl, {});
        bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
            initLimitSpan(jsonStr);
            $("#totalRowsLowerLimit").val(jsonStr[0].currentLowerLimitAmount);
            $("#totalRowsUpperLimit").val(jsonStr[0].currentUpperLimitAmount);
        });
    }

    //绑定库存告警按钮ajax查询
    $("#lowerLimit_click,#upperLimit_click").bind("click", function (e) {
        //预警针对全部库存
        $("#storehouseText").val("");
        $("#storehouseText").attr("title", $("#storehouseText").find("option:selected").text());
        $("#storehouseText").css({
            "color": "#ADADAD"
        });

        if ($(this).attr("id") == "lowerLimit_click" ) {
            limitAction("LOWER_LIMIT");
        } else {
            limitAction("UPPER_LIMIT");
        }

    });

    //复选框操作
    // $(".test,#checkAlls").live("click", function() {
    //     var isChecked = document.getElementsByName("productIds");
    //     for(var i = 0; i < isChecked.length; i++) {
    //         if(isChecked[i].checked == true) {
    //             var arr = {};
    //             arr["id"] = isChecked[i].value;
    //             arr["amount"] = $("#inventoryNum" + $(isChecked[i]).attr("id")).val();
    //             var flag = true;
    //             for(var j = 0, len = selectedArr.length; j < len; j++) {
    //                 if(selectedArr[j]["id"] === arr["id"]) {
    //                     flag = false;
    //                     break;
    //                 }
    //             }
    //             if(flag) {
    //                 selectedArr.push(arr)
    //             }
    //         } else if(isChecked[i].checked == false) {
    //             var arr = {};
    //             arr["id"] = isChecked[i].value;
    //             arr["amount"] = $("#inventoryNum" + $(isChecked[i]).attr("id")).val();
    //             for(var j = 0, len = selectedArr.length; j < len; j++) {
    //                 if(selectedArr[j]["id"] === arr["id"]) {
    //                     selectedArr.splice(j, 1);
    //                     len--;
    //                 }
    //             }
    //         }
    //     }
    //     for(var i = 0; i < isChecked.length; i++) {
    //         if(isChecked[i].checked == false) {
    //             document.getElementById("checkAlls").checked = false;
    //             break;
    //         }
    //         document.getElementById("checkAlls").checked = true;
    //     }
    // });
    $("input[type='checkbox'][name='productIds']").live("change", function (event) {
        var isChecked = document.getElementsByName("productIds");
        for (var i = 0; i < isChecked.length; i++) {
            if (isChecked[i].checked == true) {
                var arr = {};
                arr["id"] = isChecked[i].value;
                arr["amount"] = $("#inventoryNum" + $(isChecked[i]).attr("id")).val();
                var flag = true;
                for (var j = 0, len = selectedArr.length; j < len; j++) {
                    if (selectedArr[j]["id"] === arr["id"]) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    selectedArr.push(arr)
                }
            } else if (isChecked[i].checked == false) {
                var arr = {};
                arr["id"] = isChecked[i].value;
                arr["amount"] = $("#inventoryNum" + $(isChecked[i]).attr("id")).val();
                for (var j = 0, len = selectedArr.length; j < len; j++) {
                    if (selectedArr[j]["id"] === arr["id"]) {
                        selectedArr.splice(j, 1);
                        len--;
                    }
                }
            }
        }
        isCheckAll();
    });

    $(".input_lowerLimit,.input_upperLimit").live("blur",function (event) {
        var idPrefix = $(this).attr("id").split(".")[0];
        var rowId = idPrefix.substring(11, idPrefix.length);
        if ($(this).val() == $(this).attr("lastValue")) {
            updateSingleLimitFlag = true;
            return;
        }
        if (updateSingleLimitFlag) {
            updateSingleLimit(this);
            bindProductDetail(rowId);
        } else {
            updateSingleLimitFlag = true;
        }
    }).live("focus", function () {
            $(this).attr("lastValue", $(this).val());
        });


    $("input[id$='.kind']").live("blur", function (event) {
        if($(this).val()!=$(this).attr("lastvalue")){
            var idPrefix = $(this).attr("id").split(".")[0];
            var rowId = idPrefix.substring(11, idPrefix.length);
            var productId = document.getElementById("productDTOs" + rowId + ".productLocalInfoId").value;
            if (!productId) {
                return;
            }
            var kindName = $.trim($(this).val());
            var ajaxUrl = "stockSearch.do?method=ajaxSaveProductKind";
            var ajaxData = {
                kindName: $.trim($(this).val()),
                productId: productId
            }
            bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
            bcgogoAjaxQuery.setAsyncAndCache(false, false);
            bcgogoAjaxQuery.ajaxQuery(function () {
                //clear last callBack function
                bindProductDetail(rowId);
            });
        }

    }).live("click", function (event) {
            $(this).attr("lastvalue",$(this).val());

        });

    $("#storehouseText").bind("change", function (event) {
        $(this).attr("title", $(this).find("option:selected").text());
        if ($(this).find("option:selected").text() == $(this).attr("initialValue")) {
            $(this).css({
                "color": "#ADADAD"
            });
        } else {
            $(this).css({
                "color": "#000000"
            });
        }
        iniProductWithCertainField();
    });

    $(".J_product_sort").bind("mouseover",function () {
        $(this).find(".alert_develop").show();
    }).bind("mouseout",function () {
            $(this).find(".alert_develop").hide();
        }).bind("click", function () {
            $(".J_product_sort").each(function(){
                $(this).removeClass("hover");
            });
            $(this).addClass("hover");
            var currentSortStatus = $(this).attr("currentSortStatus");
            $(this).find(".J-sort-span").removeClass("arrowDown").removeClass("arrowUp");
            if(currentSortStatus == "Desc"){
                $(this).find(".J-sort-span").addClass("arrowUp");
                $(this).attr("currentSortStatus","Asc");
                $(this).find(".alertBody").html($(this).attr("descContact"));
            } else {
                $(this).find(".J-sort-span").addClass("arrowDown");
                $(this).attr("currentSortStatus", "Desc");
                $(this).find(".alertBody").html($(this).attr("ascContact"));
            }
            $("#sortStatus").val($(this).attr("sortFiled") +  $(this).attr("currentSortStatus"));
            var url = $("#url_stock_search").val();
            if($("#urlChange_stock_search").val()){
                url = $("#urlChange_stock_search").val();
            }
            var data = $("#data_stock_search").val();
            if($("#dataChange_stock_search").val()){
                data = $("#dataChange_stock_search").val();
            }
            data = eval("(" + data + ")");
            data.startPageNo = 1;
            data.sort = $("#sortStatus").val();
            bcgogoAjaxQuery.setUrlData(url, data);
            bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
                stockSearchShowResponse(jsonStr);
                initPage(jsonStr, "_stock_search",url, url, "stockSearchShowResponse", '', '',data,data);
            });
        });
    $("#submitPrint").click(function () {
        $("#editFormStorehouseId").val($("#storehouseText").val());
        $("#editForm").submit();
    });
    $(".J-more-supplierInfo").live("click",function(){
        var url = "stockSearch.do?method=getSupplierInventory";
        var index = $("#numIndex").val();
        var data = {
            productId :$("#productDTOs"+index+"\\.productLocalInfoId").val(),
            startPageNo: 1,
            maxRows: $("#moreSupplierPageSize").val()
        }
        bcgogoAjaxQuery.setUrlData(url, data);
        bcgogoAjaxQuery.ajaxQuery(function (json){
            $("#moreSupplierInfo_dialog .tabRecord").html("");
            if(json.result.success){
                drawMoreSupplier(json);
                initPage(json, "_more_supplier",url, null, "drawMoreSupplier", '', '',data,null);
            }
            $("#moreSupplierInfo_dialog").dialog({
                title: "更多供应商信息",
                width: 750,
                modal: true,
                resizable: false
            });
        })

    });

    $(".J-storeHouseCheck").live("click",function(){
        var storeHouseId =   $(this).parent("td").find("[id$='_storehouse_id']").val();
        $("#_"+storeHouseId+"_actualInventoryNum").val("").placeHolder();
        $(this).parent("td").find(".J-storeHouseCheckContainer").show();
        $(this).hide();
    });
    $(".J-selectSupplierStoreHouseCheck").live("click", function () {
        var url = "stockSearch.do?method=getSupplierInventoryByStorehouseAndProductId";
        var index = $("#numIndex").val();
        var productId = $("#productDTOs" + index + "\\.productLocalInfoId").val();
        var storehouseId = $(this).parent().find("[id$='_storehouse_id']").val();
        if(!productId || !storehouseId){
            return;
        }
        var data = {
            productId:productId,
            storehouseId:storehouseId
        }
        $("#itemDTOs0\\.productId").val(productId);
        $("#inventoryCheckStoreHouseId").val(storehouseId);
        bcgogoAjaxQuery.setUrlData(url, data);
        bcgogoAjaxQuery.ajaxQuery(function (json) {
            $("#selectSupplierInventoryCheckTb tr").remove();
            if (json.result.success) {
                drawSelectSupplierInventoryCheckTable(json);
            }
            $("#selectSupplierInventoryCheck_dialog").dialog({
                title: "盘点商品明细",
                width: 400,
                resizable: false,
                modal: true
            });
        })
    });
    $("#singleInventoryCheck").bind("click", function () {
        var storehouseId = $("#inventoryCheckStoreHouseId").val();
        var storehouseId = $("#inventoryCheckStoreHouseId").val();
        nsDialog.jConfirm("确认盘点结果？",null,function(result){
            if(result){
                APP_BCGOGO.Net.syncPost({
                    url: "inventoryCheck.do?method=validateInventoryCheck",
                    dataType: "json",
                    data: {"storehouseId": storehouseId},
                    success: function (result) {
                        if (result.success) {
                            var data = $("#inventoryCheckForm").serializeArray();
                            APP_BCGOGO.Net.syncPost({
                                url: "inventoryCheck.do?method=saveProductThroughSingleInventoryCheck",
                                dataType: "json",
                                data: data,
                                success: function (json) {
                                    if (json.result.success) {
                                        nsDialog.jAlert("盘点成功",null,function(){
                                            $("#selectSupplierInventoryCheck_dialog").dialog("close");
                                        });
                                        bindProductDetail($("#numIndex").val());
                                        var memcacheLimitDTO = [];
                                        memcacheLimitDTO.push(json.memcacheLimitDTO);
                                        initLimitSpan(memcacheLimitDTO);
                                    } else {
                                        nsDialog.jAlert(result.msg);
                                    }
                                },
                                error: function () {
                                    nsDialog.jAlert("网络异常，请联系客服！");
                                }
                            });
                        } else {
                            nsDialog.jAlert(result.msg);
                        }
                    },
                    error: function () {
                        nsDialog.jAlert("网络异常，请联系客服！");
                    }
                });
            }
        });
    });
    $("#cancelSingleInventoryCheck").bind("click",function(){
        $("#selectSupplierInventoryCheck_dialog").dialog("close");
    });
    $("[name$='.useRelatedAmount']").live("blur",function () {
        if (!$(this).val()) {
            $(this).val(0);
        }
        $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 1));
        setInventoryCheckActualAmount();
    }).live("keyup", function () {
            $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 1));
            setInventoryCheckActualAmount();
        });
    function setInventoryCheckActualAmount(){
        var totalAmount = 0;
        $("[name$='.useRelatedAmount']").each(function(){
            totalAmount +=  $(this).val()*1;
        });
        totalAmount = dataTransition.simpleRounding(totalAmount,1);
        $("#inventoryCheckActualAmount").text(totalAmount);
        $("#itemDTOs0\\.actualInventoryAmount").val(totalAmount);

    }

    $(".J-CancelStoreHouseCheck").live("click",function(){
        var storeHouseId =   $(this).parent().parent().find("[id$='_storehouse_id']").val();
        $("#_"+storeHouseId+"_actualInventoryNum").val("").placeHolder("reset");
        $(this).parent().hide();
        $(this).parent().parent().find(".J-storeHouseCheck").show();
    });

    $(".toInventoryCheckProductRecord").live("click",function(){
        var url="inventoryCheck.do?method=getInventoryCheckByProductId";
        var itemId=$(this).closest("[id$='productDetail']").attr("id").split(".")[0];
        var productId = $("#"+itemId+"\\.productLocalInfoId").val();
        if (G.isEmpty(productId)){
            return;
        }
        $("#inventoryCheckRecord tr:not(:first)").remove();
        $("#inventoryCheck_dialog").dialog({
            resizable: true,
            title: "商品盘点记录",
            width: 850,
            modal: true,
            closeOnEscape: false
        });
        var data = {
            productId: productId,
            startPageNo:1,
            now: new Date()
        };
        APP_BCGOGO.Net.asyncAjax({
            url: url,
            type: "POST",
            cache: false,
            data: data,
            dataType: "json",
            success: function (json) {
                initInventoryCheckRecord(json);
                initPage(json, "_inventory_check_record",url, null, "initInventoryCheckRecord", '', '',data,null);
            }
        });
    });

    $("#export").click(function(){
        $(this).attr("disabled",true);
        $("#exporting").css("display","");
        if($("#inventoryCount").text() * 1 == 0) {
            nsDialog.jAlert("对不起，暂无数据，无法导出！");
            $(this).removeAttr("disabled");
            $("#exporting").css("display","none");
            return;
        }
        var data = stockSearchBeforeExport();
        if (APP_BCGOGO.Permission.Version.StoreHouse) {
            data.ajaxData['storehouseId'] = $("#storehouseText").val();
        }
        data.ajaxData['totalRows'] = $("#inventoryCount").text();
        bcgogoAjaxQuery.setUrlData(data.ajaxUrl, data.ajaxData);
        bcgogoAjaxQuery.ajaxQuery(function (json) {
            $("#export").removeAttr("disabled");
            $("#exporting").css("display","none");
            if(json && json.exportFileDTOList) {
                if(json.exportFileDTOList.length > 1) {
                    showDownLoadUI(json);
                } else {
                    window.open("download.do?method=downloadExportFile&exportFileName=库存导出.xls&exportFileId=" + json.exportFileDTOList[0].idStr);
                }
            }

        });
    });
    $(".deleteTxnRemind").live("click",function(){
        var orderId = $(this).attr("orderId");
        APP_BCGOGO.Net.asyncPost({
            url: "remind.do?method=deleteTxnRemind",
            data: {
                orderId: orderId
            },
            cache: false,
            dataType: "json",
            success: function(result) {
                if(result && result.success) {
                    APP_BCGOGO.Net.asyncPost({
                        url: "remind.do?method=invoicing",
                        cache: false,
                        dataType: "json",
                        success: function(json) {
                            stockSearchInitTr3(json);
                            initPage(json, "dynamical1", "remind.do?method=invoicing", '', "stockSearchInitTr3", '', '', '', '');
                        }
                    });
                }

            }
        });
    });

});

//侦听当前click事件
var preRow, currRow, currentRowIndex, currentColor, hasShow;
$(document).click(function (e) {
    var e = e || event;
    var target = e.srcElement || e.target;
    if (target.id == "" || "input_search_pName main_product_id".search(target.id) == -1) {
        $("div_brand").hide();
    }
    if (!target || !target.id) {
        $("div_brand").hide();
    }
    if (APP_BCGOGO.Permission.Txn.InventoryManage.ProductModify) {
        if($(e.target).parents('#table_productNo tbody').length<=0
            && $(e.target).parents(".ui-bcgogo-droplist-container").length <= 0
            && $(e.target).parents(".ui-bcgogo-droplist-buttonBar").length <= 0
            && e.target.tagName != 'INPUT' && $(e.target).parents(".ui-dialog").find("div[id='jAlert']").length <= 0
            && !$(e.target).hasClass("ui-widget-overlay")
            && !($(e.target).parents(".ui-dialog")[0] ||$(e.target).hasClass("ui-dialog"))
            //点击车辆品牌，型号，大小单位下拉 商品编辑页面不消失
            && (!($(e.target).hasClass("ui-autocomplete") || $(e.target).hasClass("button-delete") || $(e.target).hasClass("ui-corner-all") ||  $(e.target).find(".ui-menu-item")[0]))){
            $("[id$='.productDetail']").each(function () {
                $(this).remove();
            });
        }
    }
//    //弹出层
//    if (APP_BCGOGO.Permission.Txn.InventoryManage.ProductModify) {
//        if (e.target.tagName != 'TD' && e.target.tagName != 'TR' && e.target.className.indexOf('-span') < 0 && e.target.className != 'span_purchase_price') {
//
//            if ($(e.target).attr('id') != 'pop_info' && $(e.target).parents('#pop_info').length <= 0 && $(e.target).parents(".ui-bcgogo-droplist-container").length <= 0 && $(e.target).parents(".ui-bcgogo-droplist-buttonBar").length <= 0 && e.target.tagName != 'INPUT' && $(e.target).parents(".ui-dialog").find("div[id='jAlert']").length <= 0) {
//
//                var objStr = '#table_productNo tr:eq(' + currentRowIndex + ') td,#table_productNo tr:eq(' + currentRowIndex + ') a,#table_productDetail tr:eq(' + currentRowIndex + ') td';
//                $(objStr).css({
//                    "color": "#272727",
//                    "background": currentColor,
//                    "border-color": "#BBBBBB"
//                });
//                $(objStr).find("a").css({
//                    "color": "#0094FF"
//                });
//                $(objStr).find(".red_color").css("color", "#cb0000");
//                $('tr:even', '#table_productNo,#table_productDetail,#sata_tab,#tab_three,#table_productNo,#kucun').not('.table_title').css({
//                    'background': '#EAEAEA'
//                });
//                $('#pop_info').slideUp('fast');
//                hasShow = false;
//                preRow = undefined;
//                currRow = undefined;
//            }
//        } else {
//            //点击的为底层表和推拉表
//            if ($(e.target).parents('#table_productNo').length > 0 || $(e.target).parents('#table_productDetail').length > 0) {
//                if ($(e.target).parents('.table_title').length <= 0) {
//
//                    preRow = currRow;
//                    currRow = $(e.target).parents('tr').prevAll().length;
//                    if (preRow == currRow && e.target.tagName != 'INPUT') {
//
//                        var objStr = '#table_productNo tr:eq(' + currentRowIndex + ') td,#table_productNo tr:eq(' + currentRowIndex + ') a,#table_productDetail tr:eq(' + currentRowIndex + ') td';
//                        $(objStr).css({
//                            "color": "#272727",
//                            "background": currentColor,
//                            "border-color": "#BBBBBB"
//                        });
//                        $(objStr).find("a").css({
//                            "color": "#0094FF"
//                        });
//                        $(objStr).find(".red_color").css("color", "#cb0000");
//                        $('tr:even', '#table_productNo,#table_productDetail,#sata_tab,#tab_three,#table_productNo,#kucun').not('.table_title').css({
//                            'background': '#EAEAEA'
//                        });
//
//                        $('#pop_info').slideUp('fast');
//                        hasShow = false;
//                        preRow = undefined;
//                        currRow = undefined;
//                    } else {
//                        var objStr = '#table_productNo tr:eq(' + preRow + ') td,#table_productNo tr:eq(' + preRow + ') a,#table_productDetail tr:eq(' + preRow + ') td';
//                        $(objStr).css({
//                            "color": "#272727",
//                            "background": currentColor,
//                            "border-color": "#BBBBBB"
//                        });
//                        $(objStr).find("a").css({
//                            "color": "#0094FF"
//                        });
//                        $(objStr).find(".red_color").css("color", "#cb0000");
//                        $('tr:even', '#table_productNo,#table_productDetail,#sata_tab,#tab_three,#table_productNo,#kucun').not('.table_title').css({
//                            'background': '#EAEAEA'
//                        });
//                        hasShow = false;
//
//                        //不为详细信息弹出层
//                        if ($(e.target).attr('id') != 'pop_info' && $(e.target).parents('#pop_info').length <= 0) {
//                            currentColor = $(e.target).parents('tr').css('background-color');
//                            currentRowIndex = $(e.target).parents('tr').prevAll().length;
//                            var top = $('#table_productNo tr:eq(' + currentRowIndex + ')').offset().top + $('#table_productNo tr:eq(' + currentRowIndex + ')').outerHeight();
//                            var left = $('#table_productNo tr:eq(' + currentRowIndex + ')').offset().left;
//
//                            var objStr = '#table_productNo tr:eq(' + currentRowIndex + ') td,#table_productNo tr:eq(' + currentRowIndex + ') a,#table_productDetail tr:eq(' + currentRowIndex + ') td';
//
//                            $(objStr).css({
//                                "color": "#ff4800",
//                                "background": "#fceba9",
//                                "border-color": "#ffaa06"
//                            });
//                            $(objStr).find("a").css({
//                                "color": "#ff4800"
//                            });
//                            $(objStr).find(".red_color").css("color", "#0094ff");
//
//                            hasShow = true;
//                            $('#pop_info').slideUp('fast', function () {
//                                //绑定数据
//                                bindProductDetails(currentRowIndex - 1);
//                                $('#pop_info').css({
//                                    'position': 'absolute',
//                                    'z-index': '3',
//                                    'top': top + 'px',
//                                    'left': left + 'px'
//                                }).slideDown('fast');
//                            });
//                        }
//                    }
//                }
//
//            }
//        }
//    }
});

//搜索按钮
function getProductWithUnknownField() {
//    search_condition = "stockSearchWithUnknownField";    todo searchConditoon
    $("#searchMode").val('stockSearchWithUnknownField');    //设置搜索模式，用于导出时判断是模糊查询还是精确搜索还是上下限查询
    var data = stockSearchBeforeAjaxRequest("stockSearchWithUnknownField");
    if (APP_BCGOGO.Permission.Version.StoreHouse) {
        data.ajaxData["storehouseId"] = $("#storehouseText").val();
    }
    bcgogoAjaxQuery.setUrlData(data.ajaxUrl, data.ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
        stockSearchShowResponse(jsonStr);
        initPage(jsonStr, "_stock_search", data.ajaxUrl, data.ajaxUrl, "stockSearchShowResponse", '', '',data.ajaxData,data.ajaxData);
    });
}

//刷新本页库存查询数据
function flushCurrentPageProductInfo() {
    var url = $("#url_stock_search").val();
    if ($("#urlChange_stock_search").val()) {
        url = $("#urlChange_stock_search").val();
    }
    var data = $("#data_stock_search").val();
    if ($("#dataChange_stock_search").val()) {
        data = $("#dataChange_stock_search").val();
    }
    data = eval("(" + data + ")");
    bcgogoAjaxQuery.setUrlData(url, data);
    bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
        stockSearchShowResponse(jsonStr);
        initPage(jsonStr, "_stock_search", url, url, "stockSearchShowResponse", '', '', data, data);
    });
}

//库存查询 ，点击下拉建议
function iniProductWithCertainField() {
    //todo 清数据
    if ($("#searchProductIds")) {
        $("#searchProductIds").val("");
    }
    clearSort();
    getProductWithCertainField();
}

function getProductWithCertainField() {
//    search_condition = "stockSearchWithCertainField";       todo searchCondition
    $("#searchMode").val('stockSearchWithCertainField');    //设置搜索模式，用于导出时判断是模糊查询还是精确搜索还是上下限查询
    var data = stockSearchBeforeAjaxRequest("stockSearchWithCertainField");
    if (APP_BCGOGO.Permission.Version.StoreHouse) {
        data.ajaxData["storehouseId"] = $("#storehouseText").val();
    }
    bcgogoAjaxQuery.setUrlData(data.ajaxUrl, data.ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
        stockSearchShowResponse(jsonStr);
        //分页dynamical formId
        initPage(jsonStr, "_stock_search", data.ajaxUrl,data.ajaxUrl, "stockSearchShowResponse", '', '',data.ajaxData,data.ajaxData);
    });
}

function stockSearchBeforeAjaxRequest(searchCondition) {
    $("#goodsIndexKuCun").hide();
    $("#goodsIndexKuCun2").show();
    $("#table_productNo tr:gt(1)").remove();
    var strUtil = new commonStrUtil();
    var searchWord = strUtil.excludeSpecifiedStr($("#searchWord").attr("initialValue"), $("#searchWord").val());
    var pn = strUtil.excludeSpecifiedStr($("#productName").attr("initialValue"), $("#productName").val());
    var product_commodity_code = strUtil.excludeSpecifiedStr($("#commodityCode").attr("initialValue"), $("#commodityCode").val());
    var pb = strUtil.excludeSpecifiedStr($("#productBrand").attr("initialValue"), $("#productBrand").val());
    var ps = strUtil.excludeSpecifiedStr($("#productSpec").attr("initialValue"), $("#productSpec").val());
    var pm = strUtil.excludeSpecifiedStr($("#productModel").attr("initialValue"), $("#productModel").val());
    var pvb = strUtil.excludeSpecifiedStr($("#productVehicleBrand").attr("initialValue"), $("#productVehicleBrand").val());
    var pvm = strUtil.excludeSpecifiedStr($("#productVehicleModel").attr("initialValue"), $("#productVehicleModel").val());
    var pKind = strUtil.excludeSpecifiedStr($("#product_kind").attr("initialValue"), $("#product_kind").val());
    var supplierKeyWord, supplierId;
    supplierKeyWord = strUtil.excludeSpecifiedStr($("#supplierInfoSearchText").attr("initialValue"), $("#supplierInfoSearchText").val());
    supplierId = $("#supplierInfoSearchText").attr("customerorsupplierid");
    var searchProductIds = "";
    if ($("#searchProductIds")) {
        searchProductIds = $("#searchProductIds").val();
    }
    var ajaxUrl = "";
    var ajaxData = null;
    //精确查询
    if (searchCondition == "stockSearchWithCertainField") {

            ajaxUrl = "stockSearch.do?method=searchProductForStockSearch";
            ajaxData = {
                commodityCode: product_commodity_code,
                productName: pn,
                productBrand: pb,
                productSpec: ps,
                productModel: pm,
                productVehicleBrand: pvb,
                productVehicleModel: pvm,
                productKind: pKind,
                supplierKeyWord: supplierKeyWord,
                supplierId: supplierId,
                productIds: searchProductIds,
                sort: $("#sortStatus").val(),
                maxRows: $("#pageRows").val(),
                startPageNo:1,
                includeBasic: false
            };

    } else if (searchCondition == "stockSearchWithUnknownField") {
        //模糊查询
            ajaxUrl = "goodsindex.do?method=inventory";
            ajaxData = {
                searchWord: searchWord,
                supplierKeyWord: supplierKeyWord,
                supplierId: supplierId,
                commodityCode: product_commodity_code,
                productName: pn,
                productBrand: pb,
                productSpec: ps,
                productModel: pm,
                productVehicleBrand: pvb,
                productVehicleModel: pvm,
                productKind: pKind,
                productIds: searchProductIds,
                sort: $("#sortStatus").val(),
                maxRows: $("#pageRows").val(),
                startPageNo:1,
                includeBasic: false
            };

    }
    return {
        ajaxUrl: ajaxUrl,
        ajaxData: ajaxData
    };
}

function stockSearchShowResponse(jsonStr) {
  //员工商品配置页面
  if ($("#pageType").val() == "allProductConfig") {
    //如果只差未配置的 不需要初始化  代码不可变动
    if ($("#unConfig").val() == "unConfig") {
      return;
    }
    initTrForSearchAchievement(jsonStr);
  } else {
    initTrForSearch(jsonStr);
    getChecked();
  }
}


//绑定数据

var arr = [];

function openIFrameOfInquiryCenterTemp(i) {
    openIFrameOfInquiryCenter(arr[i]);
}

function redirectToInquiryCenter(i) {
    redirectToInquiryCenterTemp(arr[i]);
}

function getProductPageCustomizerConfig() {
    var config = defaultStorage.getItem(storageKey.PageCustomizerProductConfig);
    if (!config) {
        APP_BCGOGO.Net.syncAjax({
            type: "POST",
            url: "pageCustomizerConfig.do?method=getProductPageConfig",
            cache: false,
            dataType: "json",
            success: function (result) {
                if (result && result.success) {
                    defaultStorage.setItem(storageKey.PageCustomizerProductConfig, JSON.stringify(result['data']['contentDto']['configInfoList']));
                } else {
                    G.error("pageCustomizerConfig.do?method=getProductPageConfig response successful, but data is null......");
                }
            },
            error: function () {
                G.error("pageCustomizerConfig.do?method=getProductPageConfig error response!");
            }
        });
    }
    return $.parseJSON(defaultStorage.getItem(storageKey.PageCustomizerProductConfig));
}

//利用JSON初始化库存查询列表页面
function initTrForSearchAchievement(jsonStr) {

  $("#table_productNo tr:gt(1)").remove();
    if (jsonStr == null || jsonStr == "" || G.isEmpty(jsonStr.products)) {
      return;
    }


    if ($("#goodsIndex")[0] && $("#goodsIndex").val() == "goodsIndex") {
        jsonStrMap.put("goodsIndex", jsonStr);
    } else {
        jsonStrMap.put("stockSearch", jsonStr);
    }
    var products = jsonStr.products;

  if ($("#searchType").val() != "shopAchievementConfig") {
    $("#totalRows").val(jsonStr.numFound);
    $("#inventoryCount").text(dataTransition.rounding(G.Lang.normalize(jsonStr.inventoryCount, 0), 1));
    $("#inventoryProductAmount").html(dataTransition.rounding(G.Lang.normalize(jsonStr.inventoryAmount, 0), 1));
    $("#inventorySum").html(dataTransition.rounding(G.Lang.normalize(jsonStr.totalPurchasePrice, 0), 2));
  }


    var permissionGoodsBuy = $("#permissionGoodsBuy").val();
    var permissionGoodsSale = $("#permissionGoodsSale").val();
    if (products && products.length >= 0) {
        for (var i = 0; i < products.length; i++) {
            var productVehicleModel = G.normalize(products[i].productVehicleModel, "");
            var productVehicleBrand = G.normalize(products[i].productVehicleBrand, "");
            var productName = G.normalize(products[i].name, "");
            var productBrand = G.normalize(products[i].brand, "");
            var productSpec = G.normalize(products[i].spec, "");
            var productModel = G.normalize(products[i].model, "");
            var commodityCode = G.normalize(products[i].commodityCode, "");
            var productLocalInfoId = products[i].productLocalInfoIdStr;

            var salesTotalAchievementType = G.normalize(products[i].salesTotalAchievementType, "");
            var salesTotalAchievementAmount = G.normalize(products[i].salesTotalAchievementAmount, 0);

            var salesProfitAchievementType = G.normalize(products[i].salesProfitAchievementType, "");
            var salesProfitAchievementAmount = G.normalize(products[i].salesProfitAchievementAmount, 0);


            var tr = '';
            tr += '';
            tr += '<tr class="titBody_Bg">';
            tr += '<td style="padding-left:10px;">';
            tr += '<input id="productDTOs' + i + '.productLocalInfoId" name="productDTOs[' + i + '].productLocalInfoId" type="hidden"  value="' + productLocalInfoId + '"/>';
            tr += '<div class="line">';
            tr += '<span class="J-productNameInfo link" onclick="javascript:redirectToInquiryCenter(' + i + ')">';
            if(stringUtil.isNotEmpty(commodityCode)){
                tr += commodityCode +"&nbsp;"
            }
            if (APP_BCGOGO.Permission.InquiryCenter) {
                var data = {
                    pageType: "inventory",
                    productName: productName,
                    productBrand: productBrand,
                    productSpec: productSpec,
                    productModel: productModel,
                    productVehicleModel: productVehicleModel,
                    productVehicleBrand: productVehicleBrand,
                    commodityCode:commodityCode
                };
                arr[i] = data;
                tr += '<a class="line blue_color" >' + productName + '</a>&nbsp;';
            } else {
                tr += productName ;
            }
            if (stringUtil.isNotEmpty(productBrand)) {
                tr += "&nbsp;" + productBrand ;
            }
            if (stringUtil.isNotEmpty(productSpec)) {
                tr += "&nbsp;" + productSpec;
            }

            if (stringUtil.isNotEmpty(productModel)) {
                tr += "&nbsp;" + productModel;
            }

            if (stringUtil.isNotEmpty(productVehicleBrand)) {
                tr +=   "&nbsp;" + productVehicleBrand;
            }
            if (stringUtil.isNotEmpty(productVehicleModel)) {
                tr +=   "&nbsp;" + productVehicleModel;
            }
            tr += '</span>';
            tr += '</div>';
            tr += '</td>';
              //库存量/单位 如果仓库作为查询条件了  那么每列的库存量都是显示当前仓库的
            var sellUnit = G.normalize(products[i].sellUnit, "");
            var showInventoryNum = products[i].inventoryNum;
            if (APP_BCGOGO.Permission.Version.StoreHouse) {
                 var searchStorehouseId = $("#storehouseText").val();
                 if (!G.Lang.isEmpty(searchStorehouseId) && products[i].storeHouseInventoryDTOMap && products[i].storeHouseInventoryDTOMap[searchStorehouseId]) {
                     showInventoryNum = G.Lang.normalize(products[i].storeHouseInventoryDTOMap[searchStorehouseId].amount, "0");
                 }
             }
            showInventoryNum = dataTransition.rounding(showInventoryNum, 2);

             //均价/最新价
            var span_purchase_price_displayStyle = "display:block;",
                browser = G.Browser;
            if (browser.isIOS || browser.isAndroid || browser.isWphone) {
                span_purchase_price_displayStyle = "display:inline;";
            }
            var averagePriceStr = dataTransition.rounding(products[i].inventoryAveragePrice, 2);
            var purchasePriceStr =dataTransition.rounding(products[i].purchasePrice, 2);

            var recommendedPrice = products[i].recommendedPrice != undefined ? products[i].recommendedPrice : 0;
            if(recommendedPrice){
                recommendedPrice =  dataTransition.rounding(recommendedPrice, 2)
            }

            tr += '<td><span class="span_average_price" style="' + span_purchase_price_displayStyle + '"><span class="arialFont">&yen;</span>' + averagePriceStr + '</span>'
                + '<input class="purchasePrice_class " id="productDTOs' + i + '.purchasePrice" type="hidden" value="' + dataTransition.rounding(products[i].purchasePrice, 2) + '">'
                + '</td>';
            tr += '<td><span class="span_purchase_price" style="' + span_purchase_price_displayStyle + '"><span class="arialFont">&yen;</span>' + recommendedPrice + '</span>' + '</td>';


            if (salesTotalAchievementType == "RATIO") {
              tr += '<td><select class="salesTotalAchievementTypeSelect" id="productDTOs' + i + '.salesTotalAchievementType" class="txt tic selec_jin"><option value="RATIO">按销售额</option><option value="AMOUNT">按销售量</option></select></td>';
              tr += '<td><input class="txt ti_chen sales_total_ti_chen_input" id="productDTOs' + i + '.salesTotalAchievementAmount" value ="' + salesTotalAchievementAmount + '%' + '"type="text"></td>';

            } else {
              tr += '<td><select class="salesTotalAchievementTypeSelect" id="productDTOs' + i + '.salesTotalAchievementType" class="txt tic selec_jin"><option value="AMOUNT">按销售量</option><option value="RATIO">按销售额</option></select></td>';
              tr += '<td><input class="txt ti_chen sales_total_ti_chen_input" id="productDTOs' + i + '.salesTotalAchievementAmount" value ="' + salesTotalAchievementAmount + '"type="text"></td>';
            }

            if (salesProfitAchievementType == "RATIO") {
              tr += '<td><select class="salesProfitAchievementTypeSelect" id="productDTOs' + i + '.salesProfitAchievementType" class="txt tic selec_jin"><option value="RATIO">按销售额</option><option value="AMOUNT">按销售量</option></select></td>';
              tr += '<td><input class="txt ti_chen sales_profit_ti_chen_input" id="productDTOs' + i + '.salesProfitAchievementAmount" value ="' + salesProfitAchievementAmount + '%' + '"type="text"></td>';

            } else {
              tr += '<td><select class="salesProfitAchievementTypeSelect" id="productDTOs' + i + '.salesProfitAchievementType" class="txt tic selec_jin"><option value="AMOUNT">按销售量</option><option value="RATIO">按销售额</option></select></td>';
              tr += '<td><input class="txt ti_chen sales_profit_ti_chen_input" id="productDTOs' + i + '.salesProfitAchievementAmount" value ="' + salesProfitAchievementAmount + '"type="text"></td>';
            }

          tr += '</tr>';
            $("#table_productNo").append($(tr));
        }
    }
//    //动态设置伸缩条的高度
//    mySlider.setSubLayerHeight();
//
//
//    //动态设置每行的高度
//    mySlider.setRowHeight();

    _productDetailJSON_ = jsonStr.products;
    $('#productDetails').val(jsonStr);

    //全选按钮
    $("#checkAlls").change(function(){
        if($(this).attr('checked')){
            check_all('productIds');
        }else{
            check_none('productIds');
        };
    });

}


//利用JSON初始化库存查询列表页面
function initTrForSearch(jsonStr) {
    //清除标志
    $("#fromPage").val('');
    var $goodsIndex = $("#goodsIndex");
    if ($goodsIndex[0] && $goodsIndex.val() == "goodsIndex") {
        jsonStrMap.put("goodsIndex", jsonStr);
    } else {
        jsonStrMap.put("stockSearch", jsonStr);
    }

    if (!jsonStr) {
        $("#submitPrint").hide();
        return;
    }

    if (jsonStr['inventoryCount'] == 0) {
        $("#submitPrint").hide();
    } else {
        $("#submitPrint").show();
    }

    $("#totalRows").val(jsonStr['numFound']);
    var products = jsonStr['products'];
    $("#inventoryCount").text(dataTransition.rounding(G.Lang.normalize(jsonStr['inventoryCount'], 0), 1));
    $("#inventoryProductAmount").html(dataTransition.rounding(G.Lang.normalize(jsonStr['inventoryAmount'], 0), 1));
    $("#inventorySum").html(dataTransition.rounding(G.Lang.normalize(jsonStr['totalPurchasePrice'], 0), 2));
    var permissionGoodsSale = $("#permissionGoodsSale").val(),
        configInfoList = getProductPageCustomizerConfig(), info,
        totalWidth = 981, checkBoxWidth = 20,totalWeight= 0, i,
        operatorWidth=100,
        $tableProductNo = $("#table_productNo");
    //获得 total width
    if (APP_BCGOGO.Permission.Txn.InventoryManage.ProductDelete) {
        totalWidth = totalWidth - checkBoxWidth - operatorWidth;
    }else{
        totalWidth = totalWidth - checkBoxWidth;
    }

    //获得 weight总值
    for (i = 0; i < configInfoList.length; i++) {
        info = configInfoList[i];
        if (!info['checked']) continue;
        if (info['name'] == "storage_bin") {
            if (APP_BCGOGO.Permission.Version.StoreHouse|| !APP_BCGOGO.PersonalizedConfiguration.StorageBinTag)
                continue;
        }
        if (info['name'] == "trade_price") {
            if (!APP_BCGOGO.PersonalizedConfiguration.TradePriceTag||!APP_BCGOGO.PersonalizedConfiguration.TradePriceTag)
                continue;
        }
        if (info['name'] == "storage_bin" && (APP_BCGOGO.Permission.Version.StoreHouse || !APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.StorageBin)) {
            continue;
        }
        totalWeight += info['weight'];
    }

    //拼接 colgroup
    var th= '<colgroup>';
    th += '<col width="' + checkBoxWidth + '">';
    for (i = 0; i < configInfoList.length; i++) {
        info = configInfoList[i];
        if (!info['checked']) continue;
        if (info['name'] == "storage_bin") {
            //没有仓库 或者 自定义关闭
            if (APP_BCGOGO.Permission.Version.StoreHouse || !APP_BCGOGO.PersonalizedConfiguration.StorageBinTag)
                continue;
        }
        if (info['name'] == "trade_price") {
            if (!APP_BCGOGO.PersonalizedConfiguration.TradePriceTag||!APP_BCGOGO.PersonalizedConfiguration.TradePriceTag)
                continue;
        }
        if (info['name'] == "storage_bin" && (APP_BCGOGO.Permission.Version.StoreHouse || !APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.StorageBin)) {
            continue;
        }
        th += '<col width="' + ((info['weight'] / totalWeight) * totalWidth) + '">';
    }
    if (APP_BCGOGO.Permission.Txn.InventoryManage.ProductDelete) {
        th += '<col width="' + operatorWidth + '">';
    }
    th += '</colgroup>';

    //拼接 head
    th += '<tr class="titleBg"><td style="padding-left:10px;width:2%"><input type="checkbox" id="checkAlls"/></td>';
    for (i = 0; i < configInfoList.length; i++) {
        info = configInfoList[i];
        if (!info['checked']) continue;
        switch (info['name']) {
            case "trade_price":
                //个性化配置
                if (APP_BCGOGO.PersonalizedConfiguration.TradePriceTag&&APP_BCGOGO.PersonalizedConfiguration.TradePriceTag){
                    //权限
                    if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetTradePrice) {
                        th += '<td class="trade_price_td"><a class="btnTitle" onclick="showSetTradePrice()">批发价</a></td>'
                    } else {
                        th += '<td class="trade_price_td">批发价</td>';
                    }
                }
                break;
            case "sale_price":
                if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetSalePrice) {
                    th += '<td><a class="btnTitle" onclick="showIt()">零售价</a></td>';
                } else {
                    th += '<td>零售价</td>';
                }
                break;
            case "product_classify":
                if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetSalePrice) {
                    th += '<td><a class="btnTitle" onclick="showSetproductKind()">商品分类</a><input type="hidden" id="oldKindName"/></td>';
                } else {
                    th += '<td>商品分类</td>';
                }
                break;
            case "alarm_settings":
                if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetSalePrice) {
                    th += '<td><a class="btnTitle" onclick="showSetLimit(true);">下/上限</a></td>';
                } else {
                    th += '<td>下/上限</td>';
                }
                break;
            case "storage_bin":
                if (!APP_BCGOGO.Permission.Version.StoreHouse && APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.StorageBin&&APP_BCGOGO.PersonalizedConfiguration.StorageBinTag) {
                    th += '<td>' + info["value"] + '</td>';
                }
                break;
            default:
                th += '<td>' + info["value"] + '</td>';
        }
    }
    if (APP_BCGOGO.Permission.Txn.InventoryManage.ProductDelete) {
        th += '<td style="width:10%">操作</td>';
    }
    th += ' </tr>';

    $tableProductNo.children().remove();
    $tableProductNo.append($(th));

    if (products && products.length >= 0) {
        for (i = 0; i < products.length; i++) {
            var productVehicleModel = G.normalize(products[i].productVehicleModel, "");
            var productVehicleBrand = G.normalize(products[i].productVehicleBrand, "");
            var productName = G.normalize(products[i].name, "");
            var productBrand = G.normalize(products[i].brand, "");
            var productSpec = G.normalize(products[i].spec, "");
            var productModel = G.normalize(products[i].model, "");
            var commodityCode = G.normalize(products[i].commodityCode, "");
            var productLocalInfoId = products[i]['productLocalInfoIdStr'];
            var tr = '';
            tr += '<tr class="titBody_Bg" id="productDTOs' + i + '.productInfo">';
            tr += '<td style="padding-left:10px;">';
            tr += '    <input id="' + i + '" type="checkbox" name="productIds"  class="test" value="' + productLocalInfoId + '" onfocus="this.blur();"/>'
                + '    <input id="productDTOs' + i + '.productLocalInfoId" name="productDTOs[' + i + '].productLocalInfoId" type="hidden"  value="' + productLocalInfoId + '"/>' +
                '      <input id="productDTOs' + i + '.inventoryAveragePrice" type="hidden" value="' + dataTransition.rounding(products[i].inventoryAveragePrice, 2) + '">';
            tr += '</td>';

            //均价/最新价
            var span_purchase_price_displayStyle = "display:none;",
                browser = G.Browser;
            if (browser.isIOS || browser.isAndroid || browser.isWphone) {
                span_purchase_price_displayStyle = "display:inline;";
            }
            var averagePriceStr = dataTransition.rounding(products[i].inventoryAveragePrice, 2);
            var purchasePriceStr =dataTransition.rounding(products[i].purchasePrice, 2);

            var recommendedPrice = products[i].recommendedPrice != undefined ? products[i].recommendedPrice : "";
            if(recommendedPrice){
                recommendedPrice =  dataTransition.rounding(recommendedPrice, 2)
            }
            for (var j = 0; j < configInfoList.length; j++) {
                info = configInfoList[j];
                if (!info['checked']) continue;
                switch (info['name']) {
                    case "product_info":
                        tr += '<td>';
                        tr += '    <div class="line">';
                        tr += '        <span class="J-productNameInfo link" onclick="javascript:redirectToInquiryCenter(' + i + ')">';
                        if(stringUtil.isNotEmpty(commodityCode)){
                            tr += commodityCode +"&nbsp;"
                        }
                        if (APP_BCGOGO.Permission.InquiryCenter) {
                            var data = {
                                pageType: "inventory",
                                productName: productName,
                                productBrand: productBrand,
                                productSpec: productSpec,
                                productModel: productModel,
                                productVehicleModel: productVehicleModel,
                                productVehicleBrand: productVehicleBrand,
                                commodityCode:commodityCode
                            };
                            arr[i] = data;
                            tr += '<a class="line black_color" ><span class="blue_color">' + productName + '</span>&nbsp;';
                        } else {
                            tr += productName ;
                        }
                        if (stringUtil.isNotEmpty(productBrand)) {
                            tr += "&nbsp;" + productBrand ;
                        }
                        if (stringUtil.isNotEmpty(productSpec)) {
                            tr += "&nbsp;" + productSpec;
                        }
                        if (stringUtil.isNotEmpty(productModel)) {
                            tr += "&nbsp;" + productModel;
                        }
                        if (stringUtil.isNotEmpty(productVehicleBrand)) {
                            tr +=   "&nbsp;" + productVehicleBrand;
                        }
                        if (stringUtil.isNotEmpty(productVehicleModel)) {
                            tr +=   "&nbsp;" + productVehicleModel;
                        }
                        tr += "</a>";
                        tr += '</span>';

                        var inOffSaleStatus = null;
                        if(APP_BCGOGO.Permission.AutoAccessoryOnline.OnSaleOperation){
                            if(!G.isEmpty(products[i].salesStatus)){
                                if(products[i].salesStatus == 'NotInSales'){
                                    inOffSaleStatus = "not_in_sale";
                                }else if(products[i].salesStatus == "InSales"){
                                    inOffSaleStatus = "in_sale";
                                }
                            }else{
                                inOffSaleStatus = "not_in_sale";
                            }
                        }

                        if(inOffSaleStatus != null){
                            if(inOffSaleStatus == 'in_sale'){
                                var url = "shopProductDetail.do?method=toShopProductDetail&paramShopId=" + products[i].shopIdStr + "&productLocalId=" + products[i].productLocalInfoIdStr;
                                tr += '<img class="in_off_sale_icon J-inOffSaleIcon" src="images/icon_' + inOffSaleStatus + '.png" onclick="window.open(\'' + url +'\')"></img>';
                            }else{
                                tr += '<img class="in_off_sale_icon J-inOffSaleIcon" src="images/icon_' + inOffSaleStatus + '.png" onclick="toGoodsInSalesEditor(' + products[i].productLocalInfoIdStr + ')"></img>';
                            }
                        }

                        tr += '    </div>';
                        tr += '</td>';
                        break;
                    case "inventory":
                        //库存量/单位 如果仓库作为查询条件了  那么每列的库存量都是显示当前仓库的
                        var sellUnit = G.normalize(products[i].sellUnit, "");
                        var showInventoryNum = products[i]['inventoryNum'];
                        if (APP_BCGOGO.Permission.Version.StoreHouse) {
                            var searchStorehouseId = $("#storehouseText").val();
                            if (!G.Lang.isEmpty(searchStorehouseId) && products[i].storeHouseInventoryDTOMap && products[i].storeHouseInventoryDTOMap[searchStorehouseId]) {
                                showInventoryNum = G.Lang.normalize(products[i].storeHouseInventoryDTOMap[searchStorehouseId].amount, "0");
                            }
                        }
                        showInventoryNum = dataTransition.rounding(showInventoryNum, 2);
                        if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.Inventory) {
                            tr += '<td><span class="J-product-inventory-span">' + showInventoryNum + '</span><span class="J-product_sell_unit">' + sellUnit + '</span><input id="inventoryNum' + i + '" type="hidden" value="' + products[i].inventoryNum + '"></td>';
                        }
                        break;
                    case "trade_price":
                        //批发价
                        var tradePrice = products[i].tradePrice ? dataTransition.simpleRounding(products[i].tradePrice, 2) : "";
                        if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.TradePrice && APP_BCGOGO.PersonalizedConfiguration.TradePriceTag) {
                            if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetTradePrice) {
                                tr += '<td  class="trade_price_td">' + '<input  id="productDTOs' + i + '.tradePrice" name="productDTOs[' + i + '].tradePrice" value="' + tradePrice + '" class="txt_one_money tradePrice_input txt" maxlength="8" type="text"/>' + '' + '</td>'; //TODO <input id="productDTOs' + i + '.inventoryAveragePrice" type="hidden" value="' + dataTransition.rounding(products[i].inventoryAveragePrice, 2) + '">
                            } else {
                                tr += '<td  class="trade_price_td">' + tradePrice + '</td>';
                            }
                        }
                        break;
                    case "sale_price":
                        //销售价
                        if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SalePrice) {
                            if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetSalePrice) {
                                tr += '<td  class="product_setting product_setting_btnSaleja"><input size="7" type="text" id="productDTOs' + i + '.recommendedPrice" name="productDTOs[' + i + '].recommendedPrice" class="txt_one_money recommendedPrice_input txt" maxlength="8" value="' + recommendedPrice + '"><span style="display: none;">' + products[i].productLocalInfoIdStr + '</span></td>';
                            } else {
                                tr += '<td  class="product_setting product_setting_btnSaleja"><input size="7" type="hidden" id="productDTOs' + i + '.recommendedPrice" name="productDTOs[' + i + '].recommendedPrice" class="txt_one_money recommendedPrice_input txt" value="' + recommendedPrice + '">' + recommendedPrice + '<span style="display: none;">' + products[i].productLocalInfoIdStr + '</span></td>';
                            }
                        }
                        break;
                    case "average_price":
                        if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.AveragePrice) {
                            tr += '<td><span class="span_average_price" style="' + span_purchase_price_displayStyle + '"><span class="arialFont">&yen;</span>' + averagePriceStr + '</span>'
                                + '<input class="purchasePrice_class" id="productDTOs' + i + '.purchasePrice" type="hidden" value="' + dataTransition.rounding(products[i].purchasePrice, 2) + '">'
                                + '</td>';
                        }
                        break;
                    case "new_storage_price":
                        //最新价
                        if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.NewStoragePrice) {
                            tr += '<td><span class="span_purchase_price" style="' + span_purchase_price_displayStyle + '"><span class="arialFont">&yen;</span>' + purchasePriceStr + '</span>'
                                + '<input class="purchasePrice_class" id="productDTOs' + i + '.purchasePrice" type="hidden" value="' + dataTransition.rounding(products[i].purchasePrice, 2) + '">'
                                + '</td>';
                        }
                        break;
                    case "storage_bin":
                        //仓位
                        var storageBin = G.normalize(products[i].storageBin, "");
                        if (!APP_BCGOGO.Permission.Version.StoreHouse && APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.StorageBin&&APP_BCGOGO.PersonalizedConfiguration.StorageBinTag) {
                            tr += '<td class="storage_bin_td" >' +
                                '<input type="txt" class="txt J-storageBin" maxlength="10"  id="productDTOs' + i + '.storageBin" name="productDTOs[' + i + '].storageBin" value="' + storageBin + '">' +
                                '</td>';
                        }
                        break;
                    case "product_classify":
                        //商品分类
                        var productKindName = G.normalize(products[i].kindName, "");
                        var productKindId = G.normalize(products[i].kindId, "");
                        if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.ProductClassify) {
                            tr += '<td  class="product_setting product_setting_btnSaleja"><input size="7"  type="text" maxlength="20" class="txt_one_money productKind_input txt" id="productDTOs' + i + '.kind" value="' + productKindName + '" ><span style="display: none;">' + productKindId + '</span></td>';
                        } else {
                            tr += '<td  class="product_setting product_setting_btnSaleja">' + productKindName + '<span style="display: none;">' + productKindId + '</span></td>';
                        }
                        break;
                    case "alarm_settings":
                        //上下限
                        var lowerLimit = dataTransition.simpleRounding(products[i].lowerLimit, 1);
                        var upperLimit = dataTransition.simpleRounding(products[i].upperLimit, 1);
                        if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.AlarmSettings) {
                            tr += '<td class="product_setting product_setting_btnExchange"><input  style= "width:30px" id="productDTOs' + i + '.lowerLimit" name="productDTOs[' + i + '].lowerLimit" class="input_lowerLimit txt_one_unit txt" value="' + lowerLimit + '" >';
                            tr += '<span>/</span>'
                            tr += '<input  style= "width:30px" id="productDTOs' + i + '.upperLimit" name="productDTOs[' + i + '].upperLimit" class="input_upperLimit txt_one_unit txt" value="' + upperLimit + '" ></td>';
                        } else {
                            tr += '<td class="product_setting product_setting_btnExchange">' + lowerLimit;
                            tr += '<span>/</span>'
                            tr += upperLimit + '</td>';
                        }
                        break;
                    default:
                        G.error("initTrForSearch error!");
                }
            }

            if (APP_BCGOGO.Permission.Txn.InventoryManage.ProductDelete) {
                tr += '<td>';
                tr += '<a  href="productThrough.do?method=redirectProductThroughDetail&productId='+productLocalInfoId+'" class="blue_color" target="_blank" >交易明细</a>&nbsp;';
                tr += '<a class="blue_color" onclick="EditGoodsILnfo.deleteProduct('+i+')">删除</a></br>';
                if(APP_BCGOGO.Permission.AutoAccessoryOnline.OnSaleOperation && products[i].salesStatus!="InSales")
                    tr += '<a class="blue_color" onclick="toGoodsInSalesEditor(\''+productLocalInfoId+'\')">我要上架</a>';
                tr += '</td>';
            }
            tr += '</tr>';
            tr += '<tr class="titBottom_Bg" id="productDTOs' + i + '.titBottom"><td colspan="11"><div class="div_Btn" style="display:none;"><a class="btnDown"></a></div></td></tr>';
            $("#table_productNo").append($(tr));
        }
    }
//    //动态设置伸缩条的高度
//    mySlider.setSubLayerHeight();
//
//
//    //动态设置每行的高度
//    mySlider.setRowHeight();

    _productDetailJSON_ = jsonStr.products;
    $('#productDetails').val(jsonStr);

    //全选按钮
    $("#checkAlls").change(function(){
        if($(this).attr('checked')){
            check_all('productIds');
        }else{
            check_none('productIds');
        }
    });

}
var _productDetailJSON_;

//TODO 全选按钮功能，可以选择所有单选框或者取消所有单选框

function check_all(cName) {
    var checkboxs = document.getElementsByName(cName);
    for (var i = 0; i < checkboxs.length; i++) {
        checkboxs[i].checked = true;
    }
    var isChecked = document.getElementsByName("productIds");
    for (var i = 0; i < isChecked.length; i++) {
        if (isChecked[i].checked == true) {
            var arr = {};
            arr["id"] = isChecked[i].value;
            arr["amount"] = $("#inventoryNum" + $(isChecked[i]).attr("id")).val();
            var flag = true;
            for (var j = 0, len = selectedArr.length; j < len; j++) {
                if (selectedArr[j]["id"] === arr["id"]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                selectedArr.push(arr)
            }
        } else if (isChecked[i].checked == false) {
            var arr = {};
            arr["id"] = isChecked[i].value;
            arr["amount"] = $("#inventoryNum" + $(isChecked[i]).attr("id")).val();
            for (var j = 0, len = selectedArr.length; j < len; j++) {
                if (selectedArr[j]["id"] === arr["id"]) {
                    selectedArr.splice(j, 1);
                    len--;
                }
            }
        }
    }
}

function check_none(cName) {
    var checkboxs = document.getElementsByName(cName);
    for (var i = 0; i < checkboxs.length; i++) {
        checkboxs[i].checked = false;
    }
    var isChecked = document.getElementsByName("productIds");
    for (var i = 0; i < isChecked.length; i++) {
        if (isChecked[i].checked == true) {
            var arr = {};
            arr["id"] = isChecked[i].value;
            arr["amount"] = $("#inventoryNum" + $(isChecked[i]).attr("id")).val();
            var flag = true;
            for (var j = 0, len = selectedArr.length; j < len; j++) {
                if (selectedArr[j]["id"] === arr["id"]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                selectedArr.push(arr)
            }
        } else if (isChecked[i].checked == false) {
            var arr = {};
            arr["id"] = isChecked[i].value;
            arr["amount"] = $("#inventoryNum" + $(isChecked[i]).attr("id")).val();
            for (var j = 0, len = selectedArr.length; j < len; j++) {
                if (selectedArr[j]["id"] === arr["id"]) {
                    selectedArr.splice(j, 1);
                    len--;
                }
            }
        }
    }
}

//复选框判断设置

function getChecked() {
    var isChecked = document.getElementsByName("productIds");
    for (var i = 0; i < isChecked.length; i++) {
        var arr = {};
        arr["id"] = isChecked[i].value;
        arr["amount"] = $("#inventoryNum" + $(isChecked[i]).attr("id")).val();
        for (var j = 0, len = selectedArr.length; j < len; j++) {
            if (selectedArr[j]["id"] === arr["id"]) {
                isChecked[i].checked = true;
            }
        }
    }
    isCheckAll();
}

function isCheckAll() {
    var isAllChecked = true;
    var isAllNotCheck = true;
    var checkboxes = document.getElementsByName("productIds");
    for (var i = 0; i < checkboxes.length; i++) {
        if (checkboxes[i].checked == false) {
            isAllChecked = false;
        } else {
            isAllNotCheck = false;
        }

    }
    if (isAllChecked && document.getElementById("checkAlls")) {
        document.getElementById("checkAlls").checked = true;
    }
    if (isAllNotCheck && document.getElementById("checkAlls")) {
        document.getElementById("checkAlls").checked = false;
    }
}


//TODO 用于勾选复选框后的跳转。flag:1-->采购；2-->入库；3-->销售；4-->施工；5-->退货

function buy(flag) {
    var cbStr = "";
    for (var i = 0, len = selectedArr.length; i < len; i++) {
        if (selectedArr[i]["id"]) {
            cbStr += selectedArr[i]["id"] + ",";
        }
    }
    if (flag == 1) { //采购
        if (cbStr) {
            window.location = "RFbuy.do?method=create&productIds=" + cbStr;
        } else {
            window.location = "RFbuy.do?method=create";
        }
    } else if (flag == 2) { //入库
        if (cbStr) {
            window.location = "storage.do?method=getProducts&type=good&goodsindexflag=gi&productIds=" + cbStr;
        } else {
            window.location = "storage.do?method=getProducts&type=txn";
        }

    } else if (flag == 3) { //销售
        var b = false; //是否有库存为0的商品
        for (var i = 0, len = selectedArr.length; i < len; i++) {
            var amount = selectedArr[i]["amount"];
            if (amount == "0" || amount == "0.0" || amount == null || amount == "" || amount < 0.0001) {
                b = true;
                break;
            }
        }
        //todo 在找到更好方法前，暂时嵌套全局变量来控制不同店铺版本不同操作
        if (APP_BCGOGO.Permission.Version.IgnorVerifierInventory) {
            b = false;
        }
        if (b) {
            alert("选中商品库存为0不能销售");
            return;
        } else {
            if (cbStr) {
                window.location = "sale.do?method=getProducts&type=good&productIds=" + cbStr;
            } else {
                window.location = "sale.do?method=getProducts&type=txn";
            }
        }

    } else if (flag == 4) { //施工
        if (cbStr) {
            window.location = "txn.do?method=getProducts&productIds=" + cbStr;
        } else {
            window.location = "txn.do?method=getRepairOrderByVehicleNumber&task=maintain";
        }

    } else if (flag == 5) { //退货
        var supplierId = $("#supplierInfoSearchText").attr("customerorsupplierid");
        if (supplierId == undefined) {
            supplierId = "";
        }
        if (cbStr) {
            var b = false; //是否有库存为0的商品
            for (var i = 0, len = selectedArr.length; i < len; i++) {
                var amount = selectedArr[i]["amount"];
                if (amount == "0" || amount == "0.0" || amount == null || amount == "" || amount < 0.0001) {
                    b = true;
                    break;
                }
            }
            if (b) {
                alert("选中商品库存为0不能退货！");
                return;
            } else {
                window.location = "goodsReturn.do?method=createReturnStorageByProductId&productIds=" + cbStr + "&supplierId=" + supplierId;
            }
        } else {
            alert("请先选择商品，再进行退货处理！");
            return;
        }
    } else if (flag == 6) {
        if (cbStr) {
            window.location = "inventoryCheck.do?method=createInventoryCheckByProductIds&productIds=" + cbStr;
        } else {
            alert("请先选择商品，再进行库存盘点处理！");
            return;
        }
    } else if (flag == 7) {
        if (cbStr) {
            window.location = "salesReturn.do?method=createSalesReturn&productIds=" + cbStr;
        } else {
            alert("请先选择商品，再进行销售退货！");
            return;
        }
    } else {
        return;
    }
}

//TODO 点击库存查询页面的设定销售价按钮，可已在弹出的页面中设置销售价，该方法用于一次性设置所有的销售价

function setRecommendedPrice(percent, value) {
    if (isNaN(percent) || isNaN(value)) {
        return false;
    }
    $(".recommendedPrice_input").each(function (i) {
        var inventoryAveragePrice = $("[id$=inventoryAveragePrice]").eq(i).val();
        if (!inventoryAveragePrice) {
            inventoryAveragePrice = 0;
        }
        $(".recommendedPrice_input").eq(i).val(dataTransition.simpleRounding ((Number(inventoryAveragePrice) * (1 + percent / 100) + value * 1),2));
        if ($(".recommendedPrice_input").eq(i).val() <= 0.0) {
            $(".recommendedPrice_input").eq(i).val('0');
        }
    });
    $("#productDTOListForm").attr("action", "txn.do?method=ajaxUpdateMultipleRecommendedPrice");
    var options = {
        type: "post",
        dataType: "json"
    };
    $("#productDTOListForm").ajaxSubmit(options);
}

//TODO 点击库存查询页面的设定商品分类按钮，可已在弹出的页面中设置商品分类，该方法用于一次性设置所有的商品分类

function setProducKind(value) {
    var idList = "";
    for (var i = 0; i < $(".productKind_input").size(); i++) {
        idList = idList + $("#productDTOs" + i + "\\.productLocalInfoId").val() + ",";
        $("#productDTOs" + i + "\\.kind").val(value);
        $("#productDTOs" + i + "\\.kind").attr("title", value);
    }
    idList.substr(0, idList.length - 1);
    $("#productDTOListForm").attr("action", "txn.do?method=updateMultipleProductKind");
    var options = {
        type: "post",
        dataType: "json",
        data: {
            idList: idList,
            newKindName: value
        }
    };
    $("#productDTOListForm").ajaxSubmit(options);
}

//点击库存查询页面的设定批发价按钮，可已在弹出的页面中批发价按钮，该方法用于一次性设置当前页批发价按钮

function setMultipleTradePrice(percent, value) {
    if (isNaN(percent) || isNaN(value)) {
        return false;
    }
    var tradePriceZeroCount = 0;
    $(".tradePrice_input").each(function () {
        var idPrefix = $(this).attr("id").split(".")[0];
        var inventoryAveragePrice = $("#" + idPrefix + "\\.inventoryAveragePrice").val();
        var newTradePrice = dataTransition.simpleRounding(inventoryAveragePrice * (1 + percent / 100) + value * 1, 2);
        if (newTradePrice == 0) {
            tradePriceZeroCount++;
        }
    });

    $(".tradePrice_input").each(function () {
        var idPrefix = $(this).attr("id").split(".")[0];
        var inventoryAveragePrice = $("#" + idPrefix + "\\.inventoryAveragePrice").val();
        var newTradePrice = dataTransition.simpleRounding(inventoryAveragePrice * (1 + percent / 100) + value * 1, 2);
        if (newTradePrice > 0) {
            $("#" + idPrefix + "\\.tradePrice").val(newTradePrice);
        }
    });
    $("#productDTOListForm").ajaxSubmit({
            url: "txn.do?method=updateMultipleTradePrice",
            dataType: "json",
            type: "POST",
            success: function (json) {
                if (json.success) {
                    if (tradePriceZeroCount > 0) {
                        nsDialog.jAlert("批量设置本页批发价成功!<br>(友情提示：因批发价不能为0,有" + tradePriceZeroCount + "条商品没有被批量修改！)");
                    } else {
                        nsDialog.jAlert("批量设置本页批发价成功!");
                    }
                } else {
                    nsDialog.jAlert("批量设置本页批发价失败!");
                }
            },
            error: function () {
                nsDialog.jAlert("数据异常!");
            }
        }
    );

}

function initInventorySumSpan(jsonStr) {
    if (jsonStr && jsonStr.length > 0) {
        $("#inventoryCount").text(dataTransition.simpleRounding(jsonStr[0].inventoryCount, 0));
        $("#inventorySum").text(dataTransition.simpleRounding(jsonStr[0].inventorySum, 0));
        $("#inventoryProductAmount").text(dataTransition.simpleRounding(jsonStr[0].inventoryProductAmount, 0));
    }
}

//TODO 库存查询页面待入库列表的初始化
function stockSearchInitTr3(jsonStr) {
    $("#purchaseRemind tr:gt(1)").remove();
    var totalRows = jsonStr.pager.totalRows;
    jsonStr = jsonStr.results;
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length; i++) {
            var remindType = G.normalize(jsonStr[i].remindType, "待入库");
            var receiptNo = G.normalize(jsonStr[i].receiptNo, "");
            var supplier = G.normalize(jsonStr[i].supplier, "");
            var productName = G.normalize(jsonStr[i].productName, "");
            var productNameShort = productName;
            if (productName.length > 35) {
                productNameShort = productName.substr(0, 30) + "...";
            }
            var number = G.normalize(jsonStr[i].number, "");
            var totalPrice = G.normalize(jsonStr[i].totalPrice, "");
            var supplierId = G.normalize(jsonStr[i].supplierId, "");
            var purchaseOrderId = G.normalize(jsonStr[i].purchaseOrderIdStr, "");
            var estimateTimeStr = G.normalize(jsonStr[i].estimateTimeStr, "");
            var tr = '';
            tr += '<tr class="titBody_Bg">';
            tr += '<td style="padding-left:10px;">' + (i + 1) + '</td>';
            tr += '<td><a target="_blank" class="blue_color" href="RFbuy.do?method=show&id=' + purchaseOrderId + '"><span>' + receiptNo + '</span></a></td>';
            tr += '<td>' + remindType + '</td>';
            tr += '<td>' + supplier + '</td>';
            tr += '<td title="' + productName + '">' + productNameShort + '</td>';
            tr += '<td>' + number + '</td>';
            tr += '<td>' + totalPrice + '</td>';
            tr += '<td>' + estimateTimeStr + '</td>';
            tr += '<td><a target="_blank" class="blue_color" href="storage.do?method=getProducts&type=txn&supplierId=' + supplierId + '&purchaseOrderId=' + purchaseOrderId + '"><span>' + "生成入库" + '</span></a>&nbsp;<a class="blue_col deleteTxnRemind" href="javascript:;" orderId="' + purchaseOrderId + '" >删除</a></td>';
            tr += '</tr >';
            tr += '<tr class="titBottom_Bg"><td colspan="9"></td></tr>';
            $("#purchaseRemind").append($(tr));
        }
    } else {
        var tr = '';
        tr += '<tr ><td colspan="9">暂无数据</td></tr>';
        $("#purchaseRemind").append($(tr));
    }
    setRemindCount(null,totalRows);
}

//缺料待入库列表初始化
function initLackStock(jsonStr) {
    $("#lack_tab tr:gt(1)").remove();
    var totalRows = jsonStr.pager.totalRows;
    jsonStr = jsonStr.results;
    $("#lack_count").html(jsonStr.length);
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length; i++) {
            var receiptNo = G.normalize(jsonStr[i].receiptNo, "");
            var productIds1 = G.normalize(jsonStr[i].productIds1, "");
            var repairOrderId = G.normalize(jsonStr[i].repairOrderIdStr, "");
            var licenceNo = G.normalize(jsonStr[i].licenceNo, "");
            var productName = G.normalize(jsonStr[i].productName, "");
            var name = G.normalize(jsonStr[i].name, "");
            var mobile = G.normalize(jsonStr[i].mobile, "");
            var content = G.normalize(jsonStr[i].content, "");
            var contentShort = content;
            if (content.length > 22) {

                contentShort = content.substring(0, 22);
            }
            var estimateTimeStr = G.normalize(jsonStr[i].estimateTimeStr, "");
            var tr= '';
            tr += '<tr class="titBody_Bg">';
            tr += '<td style="padding-left:10px;">' + (i + 1) + '</td>';
            tr += '<td><a class="blue_color" href="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + repairOrderId + '">' + receiptNo + '</a></td>';
            tr += '<td>缺料待修</td>';
            tr += '<td><a class="blue_color" href="storage.do?method=getProducts&repairOrderId=' + repairOrderId + '&productIds=' + productIds1 + '" target="_blank">' + productName + '</a></td>';
            tr += '<td>' + licenceNo + '</td>';
            tr += '<td>' + name + '</td>';
            tr += '<td>' + mobile + '</td>';
            tr += '<td title="' + content + '">' + contentShort + '</td>';
            tr += '<td >' + estimateTimeStr + '</td>';
            tr += '</tr>';
            tr += '<tr class="titBottom_Bg"><td colspan="9"></td></tr>';
            $("#lack_tab").append($(tr));
        }
    }else{
        var tr = '';
        tr += '<tr ><td colspan="9">暂无数据</td></tr>';
        $("#lack_tab").append($(tr));
    }
    setRemindCount(totalRows,null);
}

//修改商品信息DIV组件
var EditGoodsILnfo = function () {
    var jsonData = {};
    var productId = "";
    var cssLink = '<link id="editGoodsInfo_css_id" rel="stylesheet" type="text/css" href="styles/editGoodsInfo.css"/>';
    var bindStyle = function () {
        if (!$("#editGoodsInfo_css_id")[0]) $("title:last").after(cssLink);
    }
    var removeStyle = function () {
        if ($("#editGoodsInfo_css_id")[0]) $("#editGoodsInfo_css_id").detach();
    }
    var setPosition = function (domId) {
        var editDiv = $("#" + domId);
        editDiv.draggable({
            disabled: true
        });
        var divLeft = (screen.width - editDiv.width()) / 2;
        editDiv.css({
            "position": "absolute",
            "left": divLeft + "px",
            "top": "250px"
        });
    }
    var validator = function (json) {
        if (json.recommendedPrice && isNaN(json.recommendedPrice)) {
            $("#recommendedPrice_id").focus();
            return false;
        }
        if (json.upperLimit && isNaN(json.upperLimit)) {
            $("#upperLimit_id").focus();
            return false;
        }
        if (json.lowerLimit && isNaN(json.lowerLimit)) {
            $("#lowerLimit_id").focus();
            return false;
        }
        return true;
    }
    var bindButton = function () {
        $("#editGoodsInfo_id #div_close,#editGoodsInfo_id #editGoodsInfo_cancel").bind("click", function () {
            EditGoodsILnfo.remove();
        });
        $("#editGoodsInfo_id #editGoodsInfo_done").bind("click", function () {
            if ($("#editGoodsInfo_done").attr("clickFlag")) {
                return;
            }
            $("#editGoodsInfo_done").attr("clickFlag", true);
            var editGoodsInfo_done_click = function () {
                var upperLimitVal = jsonData.upperLimit || 0,
                    lowerLimitVal = jsonData.lowerLimit || 0;
                if (lowerLimitVal > upperLimitVal && upperLimitVal != 0) {
                    var temp = lowerLimitVal;
                    lowerLimitVal = upperLimitVal;
                    upperLimitVal = temp;
                } else if (lowerLimitVal == upperLimitVal && lowerLimitVal != 0) {
                    upperLimitVal++;
                }
                jsonData["upperLimit"] = upperLimitVal;
                jsonData["lowerLimit"] = lowerLimitVal;
                $("#upperLimit_id").val(upperLimitVal);
                $("#lowerLimit_id").val(lowerLimitVal);

                jsonData["actualInventoryAmount"] = $("#actualInventoryAmount").val();
                jsonData["actualInventoryAveragePrice"] = $("#actualInventoryAveragePrice").val();
                if (!validator(jsonData)) return;
                jsonData["productId"] = productId;
                $.get("stockSearch.do?method=ajaxtoupdateproduct&datatime=" + new Date().getTime(), {
                    productLocalInfoId: productId,
                    name: jsonData.productName,
                    brand: jsonData.productBrand,
                    spec: jsonData.productSpec,
                    model: jsonData.productModel,
                    vehicleBrand: jsonData.brand,
                    vehicleModel: jsonData.model,
                    recommendedPrice: jsonData.recommendedPrice,
                    upperLimit: upperLimitVal,
                    lowerLimit: lowerLimitVal,
                    storageBin: jsonData.storageBin,
                    tradePrice: jsonData.tradePrice,
                    storageUnit: jsonData.storageUnit,
                    sellUnit: jsonData.sellUnit,
                    commodityCode: jsonData.commodityCode,
                    kindName: $.trim($("#edit_productKind").val())
                }, function (json) {
                    var lowerLimitCount = $("#lowerLimitCount")[0];
                    var ulc = $("#upperLimitCount")[0];
                    if (llc) $(llc).html(json.currentLowerLimitAmount);
                    if (ulc) $(ulc).html(json.currentUpperLimitAmount);
                    $("#searchInventoryBtn").click();
                    for (i in selectedArr) {
                        if (selectedArr[i]['id'] == jsonData["productId"]) {
                            selectedArr[i]['amount'] = jsonData["actualInventoryAmount"];
                        }
                    }
                }, "json");
                EditGoodsILnfo.remove();
            };
            var i = 0;
            var t = setInterval(function () {
                i++;
                if (i > 50) {
                    clearInterval(t);
                    $("#editGoodsInfo_done").removeAttr("commodityFlag");
                    $("#editGoodsInfo_done").removeAttr("clickFlag");
                }
                if ($("#editGoodsInfo_done").attr("commodityFlag") == "success" || !$("#editGoodsInfo_done").attr("commodityFlag")) {
                    clearInterval(t);
                    $("#editGoodsInfo_done").removeAttr("commodityFlag");
                    editGoodsInfo_done_click();
                    $("#editGoodsInfo_done").removeAttr("clickFlag");
                } else if ($("#editGoodsInfo_done").attr("commodityFlag") == "error") {
                    clearInterval(t);
                    $("#editGoodsInfo_done").removeAttr("commodityFlag");
                    $("#editGoodsInfo_done").removeAttr("clickFlag");
                }
            }, 200);
        });
        $('#actualInventoryAmount').live("blur",function () {
            if (!$(this).val()) {
                $(this).val(0);
            }
            $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 1));
        }).live('keyup', function () {
                $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 1));
            });

        $('#actualInventoryAveragePrice').live("blur",function () {
            if (!$(this).val()) {
                $(this).val(0);
            }
            $(this).val(APP_BCGOGO.StringFilter.priceFilter($(this).val(), 2));
        }).live('keyup', function () {
                $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
            });

        $("#edit_commodity_code_id").bind("keyup",function () {
            var pos = getCursorPosition(this);
            var startLength = $(this).val().length;
            $(this).val(APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter($(this).val()));
            var endLength = $(this).val().length;
            setCursorPosition(this, pos + (endLength - startLength));
        }).bind("blur",function () {
                $(this).val(APP_BCGOGO.StringFilter.commodityCodeFilter($(this).val()));
                EditGoodsILnfo.modifyTextBlur('commodityCode', this);
                var $thisDom = $(this);
                if ($(this).val() && $(this).val() != $(this).attr("lastValue")) {
                    $("#editGoodsInfo_done").attr("commodityFlag", "wait");
                    var ajaxUrl = "stockSearch.do?method=ajaxToGetProductByCommodityCode";
                    var ajaxData = {
                        commodityCode: $("#edit_commodity_code_id").val()
                    };
                    if (APP_BCGOGO.Permission.Version.StoreHouse && $("#storehouseId")) {
                        ajaxData["storehouseId"] = $("#storehouseId").val();
                    }
                    APP_BCGOGO.Net.syncAjax({
                        url:ajaxUrl,
                        dataType: "json",
                        data: ajaxData,
                        success: function (json) {
                            if (jsonStr[0] && jsonStr[0].id && jsonStr[0].id != productId) {
                                var lastCommodityCodeValue = $("#edit_commodity_code_id").val();
                                $("#editGoodsInfo_done").attr("commodityFlag", "error");
                                if (!$("#editGoodsInfo_done").attr("clickFlag")) {
                                    $("#editGoodsInfo_done").removeAttr("commodityFlag");
                                }

                                nsDialog.jAlert("商品编码【" + lastCommodityCodeValue + "】已经使用，请重新输入商品编码");
                                $("#edit_commodity_code_id").val($("#edit_commodity_code_id").attr("lastCommodityCode") ? $("#edit_commodity_code_id").attr("lastCommodityCode") : "");
                                EditGoodsILnfo.modifyTextBlur('commodityCode', $("#edit_commodity_code_id"));
                            } else {
                                $("#editGoodsInfo_done").attr("commodityFlag", "success");
                            }
                        },
                        error: function () {
                            $("#editGoodsInfo_done").attr("commodityFlag", "error");
                            if (!$("#editGoodsInfo_done").attr("clickFlag")) {
                                $("#editGoodsInfo_done").removeAttr("commodityFlag");
                            }
                            nsDialog.jAlert("修改商品编码时网络异常，请稍候再试或者联系客服");
                            $("#edit_commodity_code_id").val($("#edit_commodity_code_id").attr("lastCommodityCode") ? $("#edit_commodity_code_id").attr("lastCommodityCode") : "");
                            EditGoodsILnfo.modifyTextBlur('commodityCode', $("#edit_commodity_code_id"));
                        }
                    });
                }
            }).bind("focus",function () {
                $(this).attr("lastValue", $(this).val());
            }).bind("keyup", function () {
                EditGoodsILnfo.modifyText('commodityCode', this);
            });
    }
    var getData = function () {
        $.get("stockSearch.do?method=ajaxtogetproductbyid&datatime=" + new Date().getTime(), {
            productId: productId
        }, function (json) {
            jsonData = json;
            initComponent(json);
        }, "json");
    }
    var initComponent = function (json) {
        $("#edit_product_name_id").val(json.productName);
        $("#edit_product_brand_id").val(json.productBrand);
        $("#edit_product_spec_id").val(json.productSpec);
        $("#edit_product_model_id").val(json.productModel);
        $("#edit_vehicle_brand_id").val(json.brand);
        $("#edit_vehicle_model_id").val(json.model);
        $("#upperLimit_id").val(json.upperLimit);
        $("#lowerLimit_id").val(json.lowerLimit);
        $("#storage_bin_id").val(json.storageBin);
        $("#recommendedPrice_id").val(json.recommendedPrice);
        $("#tradePrice_id").val(json.tradePrice);
        $("#amount_span").text(json.amount);
        $("#purchase_span").text(json.purchasePrice);
        $("#edit_commodity_code_id").val(json.commodityCode);
        $("#actualInventoryAmount").val(json.amount);
        $("#actualInventoryAveragePrice").val(json.inventoryAveragePrice);
        $("#edit_commodity_code_id").attr("lastCommodityCode", json.commodityCode);
        $("#edit_productKind").val(json.kindName);
        var sellUnit = json.sellUnit;
        var storageUnit = json.storageUnit;
        var rate = json.rate;
        $("#sell_unit_id").val(sellUnit);
        if (storageUnit && sellUnit && rate && storageUnit != sellUnit) {
            $("#sell_unit_id").css("width", "38px");
            $("#storage_unit_num,#storage_unit_id,#equal,#rate_span").show();
            $("#storage_unit_id").val(storageUnit);
            $("#rate_span").text(rate);
        }
    }
    return {
        show: function (checkBoxId) {
            if (!$("#editGoodsInfo_id")[0]) {
                productId = $("#table_productNo #" + checkBoxId + ",#table_productNo #" + checkBoxId).val();
                if (productId == "" || productId == null) {
                    return;
                }
                $.get("common/editGoodsInfo.jsp", function (resp) {
                    Mask.Login();
                    $("body:last").append(resp);
                    bindButton();
                    bindStyle();
                    setTimeout(function () {
                        setPosition("editGoodsInfo_id");
                        $("#editGoodsInfo_id").show();
                    }, 200);
                    getData();
                }, "html");
            }
        },
        remove: function () {
            if ($("#editGoodsInfo_id")[0]) {
                productId = "";
                $("#editGoodsInfo_id").detach();
                removeStyle();
                $("#mask").hide();
            }
        },
        modify: function (field, value) {
            jsonData[field] = value;
        },
        //输入过程中的校验
        modifyText: function (field, dom) {
            if (!dom) return;
            var pos = getCursorPosition(dom);
            var val = $(dom).val();
            var startLength = val.length;
            if (field == "commodityCode") {
                val = APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter(val);
            } else {
                val = APP_BCGOGO.StringFilter.inputtingProductNameFilter(val);
            }
            var endLength = val.length;
            $(dom).val(val);
            setCursorPosition(dom, pos + (endLength - startLength));
            jsonData[field] = val;
        },
        //失去焦点校验
        modifyTextBlur: function (field, dom) {
            if (!dom) return;
            var val = $(dom).val();
            if (field == "commodityCode") {
                val = APP_BCGOGO.StringFilter.commodityCodeFilter(val);
            } else {
                val = APP_BCGOGO.StringFilter.inputtingProductNameFilter(val);
            }
            $(dom).val(val);
            jsonData[field] = val;
        },
        modifyNumber: function (field, dom) {
            if (!dom) return;
            var val = $(dom).val();
            if (field == "recommendedPrice" || field == "tradePrice") {
                val = APP_BCGOGO.StringFilter.inputtingPriceFilter(val, 2);
            } else if (field == "upperLimit" || field == "lowerLimit") {
                val = APP_BCGOGO.StringFilter.inputtingPriceFilter(val, 1);
            }
            $(dom).val(val);
            jsonData[field] = val;
        },
        deleteProduct: function (checkBoxId) {
            var productId = $("#table_productNo #" + checkBoxId + ",#table_productNo #" + checkBoxId).val();
            var submitFlag = false;
            $.get("stockSearch.do?method=ajaxtogetproductbyid&datatime=" + new Date().getTime(), {
                productId: productId
            }, function (json) {
                if (json && json.amount * 1 > 0.001) {
                    var _html = '<div id="jInventoryCheckPrompt" title="信息提示">' +
                        '<p>' +
                        '库存不为0的商品无法删除！<br />请使用“库存盘点”功能将此产品库存盘点为0后再删除。' +
                        '</p>' +
                        '</div>';
                    $(_html).appendTo($("body"));

                    //构建dialog
                    $("#jInventoryCheckPrompt").dialog({
                        resizable: false,
                        modal: true,
                        draggable:true,
                        buttons: {
                            "现在盘点": function() {
                                parent.$("#jInventoryCheckPrompt").dialog("close");
                                window.location = "inventoryCheck.do?method=createInventoryCheckByProductIds&productIds=" + productId;
                            },
                            "稍后再说": function() {
                                parent.$("#jInventoryCheckPrompt").dialog("close");
                            }
                        },
                        open: function(event, ui) {
                            $(event.target).parent().find(".ui-dialog-titlebar-close").hide();
        //                    $(".ui-dialog-titlebar-close").hide();
                        }
                    });
                } else if (json && json.salesStatus == "InSales") {
                    nsDialog.jAlert("正在上架销售的商品无法删除！请下架后再删除。");
                } else if (json && json.amount * 1 < 0.001) {
                    nsDialog.jConfirm("商品删除，请确认！", null, function (getVal) {
                        if (getVal) {
                            if (submitFlag) {
                                return;
                            }
                            submitFlag = true;
                            $(this).dialog("hide");
                            var url = "stockSearch.do?method=ajaxToDeleteProductByProductLocalInfoId";
                            var ajaxData = {
                                productId: productId,
                                dataTime: new Date().getTime()
                            };
                            bcgogoAjaxQuery.setUrlData(url, ajaxData);
                            bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
                                if (jsonStr.result == "error") {
                                    nsDialog.jAlert(jsonStr.resultMsg);
                                } else if (jsonStr.result == "unsettled") {
                                    var html = "";
                                    for (var i = 0, len = jsonStr.orderInfo.length; i < len; i++) {
                                        html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + jsonStr.orderInfo[i].url + "'>" + jsonStr.orderInfo[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
                                        if (i % 2 == 1) {
                                            html += "<br/>";
                                        }
                                    }
                                    $("#deleteProduct_msg").html(html);
                                    $("#deleteProduct_dialog").dialog({
                                        resizable: false,
                                        title: jsonStr.resultMsg,
                                        height: 180,
                                        width: 330,
                                        modal: true,
                                        closeOnEscape: false,
                                        buttons: {
                                            "确定": function () {
                                                $(this).dialog("close");
                                            }
                                        },
                                        close: function () {
                                            $("#deleteProduct_msg").html("");
                                        }
                                    });
                                } else if (jsonStr.result == "success") {
                                    var llc = $("#lowerLimitCount")[0];
                                    var ulc = $("#upperLimitCount")[0];
                                    if (llc) $(llc).html(jsonStr.memcacheLimitDTO.currentLowerLimitAmount);
                                    if (ulc) $(ulc).html(jsonStr.memcacheLimitDTO.currentUpperLimitAmount);
                                    $("#deleteProduct_msg").html("删除成功！");
                                    $("#deleteProduct_dialog").dialog({
                                        resizable: false,
                                        title: jsonStr.resultMsg,
                                        height: 180,
                                        width: 288,
                                        modal: true,
                                        closeOnEscape: false,
                                        buttons: {
                                            "确定": function () {
                                                $(this).dialog("close");
                                            }
                                        },
                                        close: function () {
                                            flushCurrentPageProductInfo();
                                            $("#deleteProduct_msg").html("");

                                        }
                                    });
                                }
                            }, function () {
                                nsDialog.jAlert("网络异常");
                            });
                        } else {
                            if (submitFlag) {
                                return;
                            }
                        }
                    });
                    $('#pop_info').slideUp('fast');
                }
            }, "json");
        }
    }
}();

var ProductCategory = function () {
    var targetDom = null;
    var selectedValue = "";
    var cssLink = '<link id="productCategory_css_id" rel="stylesheet" type="text/css" href="styles/productCategory.css"/>';
    var bindStyle = function () {
        if (!$("#productCategory_css_id")[0]) $("title:last").after(cssLink);
    }
    var removeStyle = function () {
        if ($("#productCategory_css_id")[0]) $("#productCategory_css_id").detach();
    }
    var setPosition = function (domId) {
        var editDiv = $("#" + domId);
        editDiv.draggable({
            disabled: true
        });
        var divLeft = (screen.width - editDiv.width()) / 2;
        editDiv.css({
            "position": "absolute",
            "left": divLeft + "px",
            "top": "250px"
        });
    }
    var setDivName = function (divName) {
        if (divName) $("#productCategory_id #div_drag").html("设定" + divName + "类别");
    }
    var bindButton = function () {
        $("#productCategory_id #div_close,#productCategory_id #productCategory_cancel").bind("click", function () {
            ProductCategory.remove();
        });
        $("#productCategory_id #productCategory_done").bind("click", function () {
            if (targetDom) {
                targetDom.value = selectedValue;
                ProductCategory.remove();
            }
        });
        $("#productCategory_ul").bind("click", function (event) {
            var target = event.target;
            if (target && target.type) {
                if (target.type == "button") {
                    var parentLi = $(target).parents("li");
                    if (parentLi[0]) $(target).parents("li").detach();
                } else if (target.type == "radio") {
                    selectedValue = target.value;
                }
            }
        });
        $("#productCategory_id #productCategory_value").bind("keydown", function (event) {
            $(this).prev().val(this.value);
            if (event.keyCode == "13" && $("#productCategory_id #productCategory_value").val()) {
                var newLi = '<li><input type="radio" value="' + this.value + '" name="radio"><label>' + this.value + '</label><input type="button" class="btnPlus" onfocus="this.blur();"></li>';
                $(this).parents("li").before(newLi);
                $(this).prev().val("");
                $(this).val("");
            }
        });
    }
    //    var addProductCategory = function() {
    //    }
    return {
        show: function (target, divName) {
            if (!$("#productCategory_id")[0]) {
                targetDom = target;
                if (divName) {
                    $.get("common/productCategory.jsp", function (resp) {
                        $("body:last").append(resp);
                        setDivName(divName);
                        bindButton();
                        bindStyle();
                        setTimeout(function () {
                            setPosition("productCategory_id")
                        }, 200);
                    }, "html");
                }
            }
        },
        remove: function () {
            if ($("#productCategory_id")[0]) {
                targetDom = null;
                $("#productCategory_id").detach();
                removeStyle();
            }
        }
    }
}();

$(function () {
    $(".setCommission").bind("click", function () {
        if (this.id == "btnSaleja") { //销售价
            $("#limitOrPriceSwitch").val("price");
            $("#lower_limit_col_id").css("display", "none");
            if ($(".recommond_price_col")[0]) {
                $(".recommond_price_col").eq(0).css("display", "table-column");
            }
            if ($("#tradePriceTag").val() == "false") {
                $(".recommond_price_col").eq(0).css("width", "10%");
            } else {
                $(".recommond_price_col").eq(0).css("width", "5%");
            }
            $("#upper_limit_col_id").css("display", "none");
            if ($(".trade_price_col")[0] && $("#tradePriceTag").val() && $("#tradePriceTag").val() == "true") {
                $(".trade_price_col").eq(0).css("display", "table-column");
            }
            if ($(".trade_price_td") && $("#tradePriceTag").val() && $("#tradePriceTag").val() == "true") {
                $(".trade_price_td").css("display", "table-cell");
            }
        } else { //库存上下限
            $("#limitOrPriceSwitch").val("limit");
            $("#lower_limit_col_id").css("display", "table-column");
            $("#upper_limit_col_id").css("display", "table-column");
            if ($(".recommond_price_col")[0]) {
                $(".recommond_price_col").eq(0).css("display", "none");
            }
            if ($(".trade_price_col")[0]) {
                $(".trade_price_col").eq(0).css("display", "none");
            }
            if ($(".trade_price_td")) {
                $(".trade_price_td").css("display", "none");
            }
            //            $("#gs_id").css("width", "5%");
            //            $("#gs_id").next().show();
        }
        $(".setCommission").attr("class", "setCommission");
        $(this).attr("class", "setCommission setShow");
        $(".product_setting").hide();
        $(".product_setting_" + this.id).show();
    });
});

function limitAction(inventoryAlarm) {
    $("#searchMode").val(inventoryAlarm);    //设置搜索模式，用于导出时判断是模糊查询还是精确搜索还是上下限查询
    var  url = "goodsindex.do?method=inventory";

    var ajaxData = {
        inventoryAlarm: inventoryAlarm,
        sortStatus: $("#sortStatus").val(),
        maxRows: $("#pageRows").val(),
        startPageNo: 1
    };
    bcgogoAjaxQuery.setUrlData(url, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
        stockSearchShowResponse(jsonStr);
        initPage(jsonStr, "_stock_search", "goodsindex.do?method=inventory", "goodsindex.do?method=inventory", "stockSearchShowResponse", '', '',ajaxData,ajaxData);
    });

}

function showIt() {
    bcgogo.checksession({
        "parentWindow": window.parent,
        'iframe_PopupBox': $("#iframe_PopupBox_Limit")[0],
        'src': "txn.do?method=setSale"
    });
}

function showSetTradePrice() {
    bcgogo.checksession({
        "parentWindow": window.parent,
        'iframe_PopupBox': $("#iframe_PopupBox_Limit")[0],
        'src': "txn.do?method=setTradePrice"
    });
}

function showSetproductKind() {
    bcgogo.checksession({
        "parentWindow": window.parent,
        'iframe_PopupBox': $("#iframe_PopupBox_Kind")[0],
        'src': "txn.do?method=setProductKind"
    });
}

$(document).ready(function () {
    $("input[id$='.kind'],#edit_productKind").live("click focus",function (event) {
        askForAssistDroplist(event, null);
    }).live("keyup", function (event) {
            askForAssistDroplist(event, "enter");
        });

    $(".J-initialCss").placeHolder();

    $(".J-productSuggestion")
        .bind('click', function () {
            productSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                productSuggestion($(this));
            }
        })
        .bind('keypress', function (event) {
            var keyName = G.keyNameFromEvent(event);
            if(keyName === "enter"){
                //触发 模糊库存查询
                clearSort();
                getProductWithUnknownField();
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
        $domObject.prevAll(".J-productSuggestion").each(function () {
            var val = $(this).val().replace(/[\ |\\]/g, "");
            if($(this).attr("name")!="searchWord"){
                ajaxData[$(this).attr("name")] = val == $(this).attr("initialValue") ? "" : val;
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
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var val = $(this).val().replace(/[\ |\\]/g, "");
                            details[$(this).attr("searchField")] = val == $(this).attr("initialValue") ? "" : val;
                        });
                        return {
                            details:details
                        };
                    },
                    onSelect: function (event, index, data, hook) {
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var label = data.details[$(this).attr("searchField")];
                            if (G.Lang.isEmpty(label) && $(this).attr("initialValue")) {
                                $(this).val($(this).attr("initialValue"));
                                $(this).css({"color": "#ADADAD"});
                            } else {
                                $(this).val(G.Lang.normalize(label));
                                $(this).css({"color": "#000000"});
                            }
                        });
                        dropList.hide();
                        iniProductWithCertainField();
                    },
                    onKeyboardSelect: function (event, index, data, hook) {
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            var label = data.details[$(this).attr("searchField")];
                            if (G.Lang.isEmpty(label) && $(this).attr("initialValue")) {
                                $(this).val($(this).attr("initialValue"));
                                $(this).css({"color": "#ADADAD"});
                            } else {
                                $(this).val(G.Lang.normalize(label));
                                $(this).css({"color": "#000000"});
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
                        $domObject.nextAll(".J-productSuggestion").each(function () {
                            clearSearchInputValueAndChangeCss(this);
                        });
                        dropList.hide();
                        iniProductWithCertainField();
                    }
                });
            }

        });
    }
    $("#supplierInfoSearchText")
        .bind('click', function () {
            supplierSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                supplierSuggestion($(this));
            }
        })
        .bind('keypress', function (event) {
            var keyName = G.keyNameFromEvent(event);
            if(keyName === "enter"){
                //触发 模糊库存查询
                clearSort();
                getProductWithUnknownField();
            }
        });
    function supplierSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var ajaxData = {
            searchWord: searchWord,
            customerOrSupplier: "supplier",
            titles:"name,contact,mobile",
            uuid: dropList.getUUID()
        };
        var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            dropList.show({
                "selector":$domObject,
                "data":result,
                "onSelect":function (event, index, data) {
                    $domObject.val(data.details["name"]);
                    $domObject.css({"color":"#000000"});
                    dropList.hide();
                },"onKeyboardSelect":function (event, index, data) {
                    $domObject.val(data.details["name"]);
                }
            });
        });

    }

    $("#product_kind").bind("click focus", function(event) {
        var $domObject = $(this);
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        APP_BCGOGO.Net.syncPost({
            url: "stockSearch.do?method=getProductKindsRecentlyUsed",
            dataType: "json",
            data: {"uuid": dropList.getUUID()},
            success: function (result) {
                dropList.show({
                    "selector":$domObject,
                    "data":result,
                    "onSelect":function (event, index, data) {
                        $domObject.val(data.label);
                        $domObject.css({"color":"#000000"});
                        dropList.hide();
                    }
                });
            }
        });
    }).bind("keyup", function(event) {
            var $domObject = $(this);
            var dropList = APP_BCGOGO.Module.droplist;
            dropList.setUUID(GLOBAL.Util.generateUUID());
            APP_BCGOGO.Net.syncPost({
                url: "stockSearch.do?method=getProductKindsWithFuzzyQuery",
                dataType: "json",
                data: {"uuid": dropList.getUUID(),keyword: $.trim($domObject.val())},
                success: function (result) {
                    dropList.show({
                        "selector":$domObject,
                        "data":result,
                        "onSelect":function (event, index, data) {
                            $domObject.val(data.label);
                            $domObject.css({"color":"#000000"});
                            dropList.hide();
                        }
                    });
                }
            });
        });

    $("#_productType").live("click", function(event) {
        askForAssistDroplist(event, null);
    }).live("keyup", function (event) {
            askForAssistDroplist(event, "enter");
        });

    $("#_tradePrice").live("blur", function () {
        var $domObject = $(this);
        var tradePrice = dataTransition.rounding($(this).val(), 2);
        $(this).val(tradePrice);
        if ($(this).val() == $(this).attr("lastValue")) {
            return;
        }
        if (tradePrice <= 0) {
            nsDialog.jAlert("批发价不能小于或者等于0,请重新输入!");
            $(this).val($(this).attr("lastValue"));
            return;
        }
        var inventoryAveragePrice = dataTransition.rounding($("#_inventoryAveragePrice").text(), 2);
        if (tradePrice <= inventoryAveragePrice) {
            nsDialog.jConfirm("友情提示：当前商品批发价小于或者等于均价，是否确认继续修改？", null, function (returnVal) {
                if (returnVal) {
                    $(this).attr("lastValue", $(this).val());
                } else {
                    $domObject.val($domObject.attr("lastValue"));
                }
            });
        }
    });

    $("#_tradePrice").live("focus", function () {
        $(this).attr("lastValue", $(this).val());
    });

    $("#_recommendedPrice,#_tradePrice").live("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
    });

    $("#_upperLimit,#_lowerLimit").live("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingIntFilter($(this).val()));
    });

    $("input[id$=_actualInventoryNum]").live("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
    });

    $("#_actualInventoryAveragePrice").live("keyup", function () {
        $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(), 2));
    });

    function askForAssistDroplist(event, action) {
        var droplist = APP_BCGOGO.Module.droplist;
        var keycode = event.keyCode || event.which;

        if (keycode == 37 || keycode == 38 || keycode == 39 || keycode == 40) {
            return;
        }

        var uuid = GLOBAL.Util.generateUUID();
        droplist.setUUID(uuid);
        //ajax获得最近15次使用的商品分类
        var jsonStr = null;
        if (action == null) {
            jsonStr = APP_BCGOGO.Net.syncGet({
                url: "stockSearch.do?method=getProductKindsRecentlyUsed",
                data: {
                    uuid: uuid
                },
                dataType: "json"
            });
        } else if (action == "enter") {
            jsonStr = APP_BCGOGO.Net.syncGet({
                url: "stockSearch.do?method=getProductKindsWithFuzzyQuery",
                data: {
                    uuid: uuid,
                    keyword: $.trim(event.target.value)
                },
                dataType: "json"
            });
        }
        var inputId = event.target;
        var result = {
            uuid: uuid,
            data: (jsonStr && jsonStr.data) ? jsonStr.data : ""
        };
        if (jsonStr && jsonStr.uuid && uuid == jsonStr.uuid) {
            droplist.show({
                "selector": $(event.currentTarget),
                "isEditable": true,
                "data": result,
                "saveWarning": "保存操作会影响全局数据",
                "deleteWarning": "删除操作会影响全局数据",
                "onSelect": function (event, index, data) {
                    inputId.value = $.trim(data.label);
                    if (data.label != "") {
                        var idPrefix = inputId.id.split(".")[0];
                        var rowId = idPrefix.substring(11, idPrefix.length);
                        //库存列表处发生
                        if (document.getElementById("productDTOs" + rowId + ".productLocalInfoId") != null) {
                            var productId = document.getElementById("productDTOs" + rowId + ".productLocalInfoId").value;
                            if (!productId) {
                                return;
                            }
                            APP_BCGOGO.Net.asyncGet({
                                url: "stockSearch.do?method=ajaxSaveProductKind",
                                data: {
                                    kindName: $.trim(data.label),
                                    productId: productId
                                },
                                dataType: "json"
                            });
                        }
                    }
                    droplist.hide();
                },
                "onEdit": function (event, index, data) {
                    //记下修改前的分类名称
                    $("#oldKindName").val(data.label);
                },
                "onSave": function (event, index, data) {
                    var newKindName = $.trim(data.label);
                    if ($.trim(newKindName) == "") {
                        droplist.hide();
                        nsDialog.jAlert("空字符串不能保存！");
                    } else if (newKindName != $("#oldKindName").val()) {
                        var r = APP_BCGOGO.Net.syncGet({
                            url: "stockSearch.do?method=saveOrUpdateProductKind",
                            data: {
                                oldKindName: $("#oldKindName").val(),
                                newKindName: newKindName
                            },
                            dataType: "json"
                        });
                        if (r == null || r.flag == undefined) {
                            event.target.value = $("#oldKindName").val();
                            droplist.hide();
                            nsDialog.jAlert("保存失败！");
                        } else if (r.flag == "false") {
                            nsDialog.jAlert("分类名“" + newKindName + "”已经存在！");
                        } else if (r.flag == "true") {
                            nsDialog.jAlert("修改成功！");
                        }
                        //保存后清空，避免影响下次保存
                        $("#oldKindName").val("");
                    }
                },
                "onDelete": function (event, index, data) {
                    var r = APP_BCGOGO.Net.syncGet({
                        url: "stockSearch.do?method=deleteProductKind",
                        data: {
                            kindName: data.label
                        },
                        dataType: "json"
                    });
                    if (r == null || r.flag == undefined) {
                        nsDialog.jAlert("删除失败！");
                    } else if (r.flag == "true") {
                        nsDialog.jAlert("删除成功！");
                    }
                }
            });
        }
    }

    $("#resetProductInfo").live("click", function () {
        var index = $("#numIndex").val() * 1;
        bindProductDetail(index);
    });


    $("#_commodityCode").live("keyup",function () {
        var pos = getCursorPosition(this);
        var startLength = $(this).val().length;
        $(this).val(APP_BCGOGO.StringFilter.inputtingCommodityCodeFilter($(this).val()));
        var endLength = $(this).val().length;
        setCursorPosition(this, pos + (endLength - startLength));
    }).live("blur",function () {
            $(this).val(APP_BCGOGO.StringFilter.commodityCodeFilter($(this).val()));
            EditGoodsILnfo.modifyTextBlur('commodityCode', this);
            var $thisDom = $(this);
            if ($(this).val() && $(this).val() != $(this).attr("lastValue")) {
                $("#updateProduct").attr("commodityFlag", "wait");
                $("#updatePadndian").attr("commodityFlag", "wait");
                var ajaxUrl = "stockSearch.do?method=ajaxToGetProductByCommodityCode";
                var ajaxData = {
                    commodityCode: $("#_commodityCode").val()
                };
                if (APP_BCGOGO.Permission.Version.StoreHouse) {
                    if (getOrderType() == "ALLOCATE_RECORD" && $("#outStorehouseId")) {
                        ajaxData["storehouseId"] = $("#outStorehouseId").val();
                    } else if ($("#storehouseId")) {
                        ajaxData["storehouseId"] = $("#storehouseId").val();
                    }
                }
                var index = $("#numIndex").val();
                var productId = $("#productDTOs" + index + "\\.productLocalInfoId").val();

                APP_BCGOGO.Net.syncAjax({
                    url: ajaxUrl,
                    dataType: "json",
                    data: ajaxData,
                    success: function (jsonStr) {
                        if (jsonStr[0] && jsonStr[0].productIdStr && jsonStr[0].productIdStr != productId) {
                            var lastCommodityCodeValue = $("#_commodityCode").val();
                            if (!$("#updateProduct").attr("clickFlag")) {
                                $("#updateProduct").removeAttr("commodityFlag");
                            }
                            if (!$("#updatePandian").attr("clickFlag")) {
                                $("#updatePandian").removeAttr("commodityFlag");
                            }
                            nsDialog.jAlert("商品编码【" + lastCommodityCodeValue + "】已经使用，请重新输入商品编码", null, function () {
                                $("#_commodityCode").val($("#_commodityCode").attr("lastCommodityCode") ? $("#_commodityCode").attr("lastCommodityCode") : "");
                                EditGoodsILnfo.modifyTextBlur('commodityCode', $("#_commodityCode"));
                                $("#_commodityCode").focus();
                            });
                        }
                        if(!jsonStr || jsonStr ==''){
                            $("#updateProduct").removeAttr("commodityFlag");
                        }
                    },
                    error: function () {
                        $("#updateProduct").attr("commodityFlag", "error");
                        $("#updatePandian").attr("commodityFlag", "error");
                        if (!$("#updateProduct").attr("clickFlag")) {
                            $("#updateProduct").removeAttr("commodityFlag");
                        }
                        if (!$("#updatePandian").attr("clickFlag")) {
                            $("#updatePandian").removeAttr("commodityFlag");
                        }
                        nsDialog.jAlert("修改商品编码时网络异常，请稍候再试或者联系客服", null, function () {
                            $("#_commodityCode").val($("#_commodityCode").attr("lastCommodityCode") ? $("#_commodityCode").attr("lastCommodityCode") : "");
                            EditGoodsILnfo.modifyTextBlur('commodityCode', $("#_commodityCode"));
                        });
                    }
                });
            }

        }).live("focus",function () {
            $(this).attr("lastValue", $(this).val());
        }).live("keyup", function () {
            EditGoodsILnfo.modifyText('commodityCode', this);
        });

    $("#updateProduct").live("click", function () {
        if ($("#updateProduct").attr("clickFlag")) {
            return;
        }
        $("#updateProduct").attr("clickFlag", true);
        var update_product_click = function () {

            if (!$("#_name").val() || $.trim($("#_name").val()) == "") {
                nsDialog.jAlert("请填写品名!");
                $("#updateProduct").removeAttr("clickFlag");
                return;
            }

            //单位问题
            var sellUnit = '';
            var storageUnit = '';
            var rate = '';
            if($("#firstUnit")[0]){
                if(!$.trim($("#firstUnit").val())){
                    nsDialog.jAlert("请填写单位!",null,function(){
                        $("#firstUnit").focus();
                        $("#updateProduct").removeAttr("clickFlag");
                    });
                    return;
                } else {
                    sellUnit = $.trim($("#firstUnit").val());
                    storageUnit = $.trim($("#firstUnit").val());
                }
            }

            var upperLimitVal = $("#_upperLimit").val() || 0,
                lowerLimitVal = $("#_lowerLimit").val() || 0;
            if (lowerLimitVal * 1 > upperLimitVal * 1 && upperLimitVal * 1 != 0) {
                var temp = lowerLimitVal;
                lowerLimitVal = upperLimitVal;
                upperLimitVal = temp;
            } else if (lowerLimitVal * 1 == upperLimitVal * 1 && lowerLimitVal * 1 != 0) {
                upperLimitVal++;
            }

            var recommendedPrice = $("#_recommendedPrice").val();

            if (recommendedPrice && isNaN(recommendedPrice)) {
                $("#_recommendedPrice").focus();
                $("#updateProduct").removeAttr("clickFlag");
                return false;
            }
            if (upperLimitVal && isNaN(upperLimitVal)) {
                $("#_upperLimit").focus();
                $("#updateProduct").removeAttr("clickFlag");
                return false;
            }
            if (lowerLimitVal && isNaN(lowerLimitVal)) {
                $("#_lowerLimit").focus();
                $("#updateProduct").removeAttr("clickFlag");
                return false;
            }

            var index = $("#numIndex").val() * 1;

            var productId = $("#productDTOs" + index + "\\.productLocalInfoId").val();
            var productName = $.trim($("#_name").val());
            var productBrand = $("#_brand").val();
            var productSpec = $("#_spec").val();
            var productModel = $("#_model").val();
            var vehicleBrand = $("#_productVehicleBrand").val();
            var vehicleModel = $("#_productVehicleModel").val();
            var recommendedPrice = $("#_recommendedPrice").val();
            var storageBin = $("#_storageBin").val();
            var tradePrice = $("#_tradePrice").val();
            var commodityCode = $("#_commodityCode").val();

            var ajaxData = {
                productLocalInfoId: productId,
                name: productName,
                brand: productBrand,
                spec: productSpec,
                model: productModel,
                vehicleBrand: vehicleBrand,
                vehicleModel: vehicleModel,
                recommendedPrice: recommendedPrice,
                upperLimit: upperLimitVal,
                lowerLimit: lowerLimitVal,
                storageBin: storageBin,
                tradePrice: tradePrice,
                storageUnit: storageUnit,
                sellUnit: sellUnit,
                rate:rate,
                commodityCode: commodityCode,
                kindName: $.trim($("#_productType").val()),
                "imageCenterDTO.productMainImagePath":$("#_imagePath").val()
            };
            if (APP_BCGOGO.Permission.Version.StoreHouse) {
                $("input[id$='_storehouse_storageBin']").each(function (index) {
                    ajaxData["storeHouseInventoryDTOs[" + index + "].productLocalInfoId"] = productId;
                    ajaxData["storeHouseInventoryDTOs[" + index + "].storageBin"] = $(this).val();
                    ajaxData["storeHouseInventoryDTOs[" + index + "].storehouseId"] = $(this).attr("data-storehouse-id");
                });
            }
            ajaxToUpdateProduct(ajaxData);
        };
        var i = 0;
        var t = setInterval(function () {
            i++;
            if (i > 50) {
                clearInterval(t);
                $("#updateProduct").removeAttr("commodityFlag");
                $("#updateProduct").removeAttr("clickFlag");
            }
            if ($("#updateProduct").attr("commodityFlag") == "success" || !$("#updateProduct").attr("commodityFlag")) {
                clearInterval(t);
                $("#updateProduct").removeAttr("commodityFlag");
                update_product_click();

            } else if ($("#updateProduct").attr("commodityFlag") == "error") {
                clearInterval(t);
                $("#updateProduct").removeAttr("commodityFlag");
                $("#updateProduct").removeAttr("clickFlag");
            }
        }, 200);
    });
    $("#_storehouseId").change(function (e) {
        if ($("#_" + $(this).val() + "_storehouse_inventory_amount")) {
            $("#_actualInventoryNum").val($("#_" + $(this).val() + "_storehouse_inventory_amount").text());
        } else {
            $("#_actualInventoryNum").val(0);
        }

    });

    //调价，确定，取消按钮
    $("#priceAdjustBtn").live("click",function (){
        $("#priceAdjustBtn").hide();
        $("#_inventoryAveragePrice").show();
        $("#_actualInventoryAveragePrice").placeHolder();
        $(".J-priceAdjustDiv").show();
    });
    $("#updatePriceAdjust").live("click", function () {
        if($("#_actualInventoryAveragePrice").val() == $("#_actualInventoryAveragePrice").attr("initialValue")
            || !$("#_actualInventoryAveragePrice").val()){
            nsDialog.jAlert("请输入调整价");
            return;
        }
        var idPrefix = $(this).parents("table").parents("tr").attr("id").split(".")[0];
        if ($("#updatePriceAdjust").attr("clickFlag")) {
            return;
        }
        $("#updatePriceAdjust").attr("clickFlag", true);
        var inventoryAveragePrice = $("#_inventoryAveragePrice").html();
        if (inventoryAveragePrice == '--' || !inventoryAveragePrice) {
            inventoryAveragePrice = 0;
        }
        var actualInventoryAveragePrice = $("#_actualInventoryAveragePrice").val();
        if (!actualInventoryAveragePrice) {
            actualInventoryAveragePrice = 0;
        }
        actualInventoryAveragePrice = actualInventoryAveragePrice * 1;
        inventoryAveragePrice = inventoryAveragePrice * 1;
        if (Math.abs(actualInventoryAveragePrice - inventoryAveragePrice) < 0.0001) {
            nsDialog.jAlert("请输入不同的调整价");
            $("#updatePriceAdjust").removeAttr("clickFlag");
            return;
        }
        var productId = $("#" + idPrefix + "\\.productLocalInfoId").val();
        if (!productId) {
            $("#updatePriceAdjust").removeAttr("clickFlag");
            return;
        }
        var paramJson = {
            productId: productId,
            actualInventoryAveragePrice: actualInventoryAveragePrice
        };
        $.get("stockSearch.do?method=ajaxToUpdateAveragePrice&datatime=" + new Date().getTime(), paramJson, function (json) {
            if (json.msg == "success") {
                $("#_inventoryAveragePrice").text(G.Lang.normalize(json.actualTotalInventoryAveragePrice, "--"));
                var trProductInfo = $("#" + idPrefix + "\\.productInfo");
                trProductInfo.find("span[class='span_average_price']").html('<span class="arialFont">&yen;</span>'+$("#_inventoryAveragePrice").text());
                $("#updatePriceAdjust").removeAttr("clickFlag");
                $("#_actualInventoryAveragePrice").placeHolder("reset");
                $("#updatePriceAdjust").parents(".J-priceAdjustDiv").hide();
                $("#priceAdjustBtn").show();
                nsDialog.jAlert("调价成功！");
            } else {
                nsDialog.jAlert("调价失败", null, function () {
                    $("#updatePriceAdjust").removeAttr("clickFlag");
                });
            }
        }, "json");
    });
    $("#cancelPriceAdjust").live("click", function () {
        $("#_actualInventoryAveragePrice").val("");
        $("#_actualInventoryAveragePrice").placeHolder("reset");
        $("#updatePriceAdjust").parents(".J-priceAdjustDiv").hide();
        $("#priceAdjustBtn").show();
    });
    //盘点，确定，取消按钮     J-inventoryCheckDiv
    $("#inventoryCheckBtn").live("click",function (){
        $("#inventoryCheckBtn").hide();
        $("#_actualInventoryNum").val("");
        $("#_actualInventoryNum").placeHolder();
        $(".J-inventoryCheckDiv").show();
    });
    $("#updateInventoryCheck").live("click", function () {
        if ($("#_actualInventoryNum").val() == $("#_actualInventoryNum").attr("initialValue")
            || !$("#_actualInventoryNum").val()) {
            nsDialog.jAlert("请输入盘点数量");
            return;
        }
        var index = $("#numIndex").val() * 1;
        var idPrefix = $(this).parents("table").parents("tr").attr("id").split(".")[0];
        if ($("#updateInventoryCheck").attr("clickFlag")) {
            return;
        }
        $("#updateInventoryCheck").attr("clickFlag", true);
        var inventoryNum = $("#_inventoryNum").html();
        if (inventoryNum == '--' || !inventoryNum) {
            inventoryNum = 0;
        }
        var actualInventoryNum = $("#_actualInventoryNum").val();
        if (!actualInventoryNum) {
            actualInventoryNum = 0;
        }
        inventoryNum = inventoryNum * 1;
        actualInventoryNum = actualInventoryNum * 1;
        if (Math.abs(inventoryNum - actualInventoryNum) < 0.0001) {
            nsDialog.jAlert("请输入不同的盘点数量");
            $("#updateInventoryCheck").removeAttr("clickFlag");
            return;
        }
        var productId = $("#" + idPrefix + "\\.productLocalInfoId").val();
        if (!productId) {
            $("#updateInventoryCheck").removeAttr("clickFlag");
            return;
        }
        var paramJson = {
            productId: productId,
            inventoryAmount: inventoryNum,
            actualInventoryAmount: actualInventoryNum
        };
        $.get("stockSearch.do?method=ajaxtoupdateinventoryAmountAndAveragePrice&datatime=" + new Date().getTime(), paramJson, function (json) {
            if (json.msg == "success") {
                var llc = $("#lowerLimitCount")[0];
                var ulc = $("#upperLimitCount")[0];
                if (llc) $(llc).html(json.memcacheLimitDTO.currentLowerLimitAmount);
                if (ulc) $(ulc).html(json.memcacheLimitDTO.currentUpperLimitAmount);

                var actualTotalInventoryNum = G.Lang.normalize(json.actualTotalInventoryNum, "0") * 1;
                //更新总库存
                $("#_inventoryNum").text(actualTotalInventoryNum);

                var productIdObj = $("#" + idPrefix + "\\.productLocalInfoId");
                productIdObj.closest("tr").find(".J-product-inventory-span").html(actualTotalInventoryNum);

                $("#inventoryNum" + index).val(actualTotalInventoryNum);
                //改盘点的时候复制下面
                for (i in selectedArr) {
                    if (selectedArr[i]['id'] == productId) {
                        selectedArr[i]['amount'] = actualTotalInventoryNum;
                    }
                }
                $("#updateInventoryCheck").removeAttr("clickFlag");
                nsDialog.jAlert("盘点成功！");
                $("#_actualInventoryNum").val("").placeHolder("reset");
                $(".J-inventoryCheckDiv").hide();
                $("#inventoryCheckBtn").show();
            } else {
                nsDialog.jAlert("更新库存盘点失败", null, function () {
                    $("#updateInventoryCheck").removeAttr("clickFlag");
                });
            }

        }, "json");
    });

    //仓库的库存盘点
    $(".J-SureStoreHouseCheck").live("click", function () {
        var $thisDom = $(this);
        if ($thisDom.attr("clickFlag")) {
            return;
        }
        $thisDom.attr("clickFlag", true);
        var index = $("#numIndex").val() * 1;
        var storeHouseId =   $(this).parent().parent().find("[id$='_storehouse_id']").val();
        var inventoryAmount = $("#_"+storeHouseId+"_storehouse_inventory_amount").val();
        if(!inventoryAmount || isNaN(inventoryAmount)){
            inventoryAmount = 0;
        }else{
            inventoryAmount = inventoryAmount * 1;
        }

        if ($("#_"+storeHouseId+"_actualInventoryNum").val() == $("#_"+storeHouseId+"_actualInventoryNum").attr("initialValue")
            || !$("#_"+storeHouseId+"_actualInventoryNum").val()) {
            nsDialog.jAlert("请输入盘点数量");
            $thisDom.removeAttr("clickFlag");
            return;
        }
        var actualInventoryAmount = $("#_"+storeHouseId+"_actualInventoryNum").val()*1;
        if (Math.abs(inventoryAmount - actualInventoryAmount) < 0.0001) {
            nsDialog.jAlert("请输入不同的盘点数量");
            $thisDom.removeAttr("clickFlag");
            return;
        }
        var productId = $("#productDTOs" + index + "\\.productLocalInfoId").val();
        if (!productId) {
            $thisDom.removeAttr("clickFlag");
            return;
        }

        var paramJson = {
            productId: productId,
            inventoryAmount: inventoryAmount,
            actualInventoryAmount: actualInventoryAmount
        };
        if (APP_BCGOGO.Permission.Version.StoreHouse) {
            var flag = true;
            paramJson["storehouseId"] = storeHouseId;
            APP_BCGOGO.Net.syncPost({
                url: "inventoryCheck.do?method=validateInventoryCheck",
                dataType: "json",
                data: {"storehouseId": storeHouseId},
                success: function (result) {
                    if (result.success) {
                        flag = false;
                    } else {
                        nsDialog.jAlert(result.msg);
                    }
                },
                error: function () {
                    nsDialog.jAlert("验证时产生异常，请重试！", function () {
                        $thisDom.removeAttr("clickFlag");
                    });
                }
            });
            if (flag) {
                $thisDom.removeAttr("clickFlag");
                return;
            }
        }

        $.get("stockSearch.do?method=ajaxtoupdateinventoryAmountAndAveragePrice&datatime=" + new Date().getTime(), paramJson, function (json) {
            if (json.msg == "success") {
                var llc = $("#lowerLimitCount")[0];
                var ulc = $("#upperLimitCount")[0];
                if (llc) $(llc).html(json.memcacheLimitDTO.currentLowerLimitAmount);
                if (ulc) $(ulc).html(json.memcacheLimitDTO.currentUpperLimitAmount);

                var actualTotalInventoryNum = G.Lang.normalize(json.actualTotalInventoryNum, "0") * 1;
                //更新总库存
                $("#_inventoryNum").text(actualTotalInventoryNum);

                var productIdObj = $("#productDTOs" + index + "\\.productLocalInfoId");
                var sellUnit =  $("#productDTOs" + index + "\\.productInfo").find(".J-product_sell_unit").text();
                //更新仓库库存  如果仓库作为查询条件了  那么每列的库存量都是显示当前仓库的，并且在盘点时候刷新 也是只刷新当前仓库的
                if (APP_BCGOGO.Permission.Version.StoreHouse) {
                    $("#_" + storeHouseId + "_storehouse_inventory_amount_span").text(actualInventoryAmount + sellUnit);
                    $("#_" + storeHouseId + "_storehouse_inventory_amount").val(actualInventoryAmount);
                    var searchStorehouseId = $("#storehouseText").val();
                    if (G.Lang.isEmpty(searchStorehouseId)) {
                        productIdObj.closest("tr").find(".J-product-inventory-span").html(actualTotalInventoryNum);
                    } else {
                        if (storeHouseId == $("#storehouseText").val()) {
                            productIdObj.closest("tr").find(".J-product-inventory-span").html(actualInventoryAmount);
                        }
                    }
                } else {
                    productIdObj.closest("tr").find(".J-product-inventory-span").html(actualTotalInventoryNum);
                }
                $("#inventoryNum" + index).val(actualTotalInventoryNum);
                //改盘点的时候复制下面
                for (i in selectedArr) {
                    if (selectedArr[i]['id'] == productId) {
                        selectedArr[i]['amount'] = actualTotalInventoryNum;
                    }
                }
                $thisDom.removeAttr("clickFlag");
                nsDialog.jAlert("盘点成功！");
                $("#_"+storeHouseId+"_actualInventoryNum").val("").placeHolder("reset");
                $thisDom.parent().hide();
                $thisDom.parent().parent().find(".J-storeHouseCheck").show();

            } else {
                nsDialog.jAlert("更新库存盘点失败", null, function () {
                    $thisDom.removeAttr("clickFlag");
                });
            }

        }, "json");

    });
    $("#cancelInventoryCheck").live("click", function () {
        $("#_actualInventoryNum").val("");
        $("#_actualInventoryNum").placeHolder("reset");
        $("#updatePriceAdjust").parents(".J-inventoryCheckDiv").hide();
        $("#inventoryCheckBtn").show();
        $(".J-inventoryCheckDiv").hide();
    });
    //增加单位
    $("#addUnit").live("click",function (){
        $("#addUnit").hide();
        $("#secondUnitContainer").show();
    });
    $("#cancelAddUnit").live("click",function(){
        $("#addUnit").show();
        $("#secondUnitContainer,#rateContainer").hide();
        $("#secondUnit,#secondRate,#firstRate").val("");

    });
    $("#secondUnit").live("blur",function () {
        var secondUnit = $.trim($("#secondUnit").val());
        $("#secondUnit").val(secondUnit);
        $("#secondUnitSpan").text(secondUnit);
        $("#secondUnitSpan").attr("title",secondUnit);
        if(secondUnit){
            $("#rateContainer").show();
        }
    }).live("keyup", function () {
            var secondUnit = $.trim($("#secondUnit").val());
            $("#secondUnitSpan").text(secondUnit);
            $("#secondUnitSpan").attr("title",secondUnit);
        });
    $("#sureAddUnit").live("click",function(){
        var sellUnit = '';
        var storageUnit = '';
        var rate = '';
        var index = $("#numIndex").val();
        if($("#sureAddUnit").attr("clickFlag")){
            return;
        }
        $("#sureAddUnit").attr("clickFlag",true);
        if (validateAddSecondUnit()) {
            var firstUnit = $("#firstUnit_Hidden").val();
            var secondUnit = $.trim($("#secondUnit").val());
            var secondRate = $.trim($("#secondRate").val()) * 1;
            var firstRate = $.trim($("#firstRate").val()) * 1;
            if (secondRate > firstRate) {
                sellUnit = secondUnit;
                storageUnit = firstUnit;
                rate = secondRate;
            } else {
                sellUnit = firstUnit;
                storageUnit = secondUnit;
                rate = firstRate;
            }
        } else {
            $("#sureAddUnit").removeAttr("clickFlag");
            return;
        }
        var productId = $("#productDTOs" + index + "\\.productLocalInfoId").val();
        if (!productId || isNaN(productId)) {
            $("#sureAddUnit").removeAttr("clickFlag");
            return;
        }
        APP_BCGOGO.Net.syncAjax({
            url: "txn.do?method=setSellUnitAndRate",
            dataType: "json",
            data: {
                productId:productId ,
                storageUnit: storageUnit,
                sellUnit: sellUnit,
                rate: rate
            },
            success: function (jsonStr) {
                if(jsonStr[jsonStr.length-1].result == "success"){
                    nsDialog.jAlert("您已成功添加一个新单位！")
                    bindProductDetail(index);
                }
            },
            error: function () {
                nsDialog.jAlert("网络异常，请联系客服！")
                bindProductDetail(index);
            }
        });
    });
//    $("#updatePandian").bind("click", function () {
//        if ($("#updatePandian").attr("clickFlag")) {
//            return;
//        }
//        $("#updatePandian").attr("clickFlag", true);
//        var update_pandian_click = function () {
//            var index = $("#numIndex").val() * 1;
//
//            var inventoryAmount = $("#_inventoryNum").text();//用来比较 是不是库存数量有变动 防止重复更新
//            if (APP_BCGOGO.Permission.Version.StoreHouse) {//取 仓库库存进行比较
//                inventoryAmount = $("#_" + $("#_storehouseId").val() + "_storehouse_inventory_amount").text();
//            }
//            if (inventoryAmount == '--' || !inventoryAmount) {
//                inventoryAmount = 0;
//            }
//
//            var actualInventoryAmount = $("#_actualInventoryNum").val();
//
//            if (!actualInventoryAmount) {
//                actualInventoryAmount = 0;
//            }
//
//            var inventoryAveragePrice = $("#_inventoryAveragePrice").html();
//            if (inventoryAveragePrice == '--' || !inventoryAveragePrice) {
//                inventoryAveragePrice = 0;
//            }
//
//            var actualInventoryAveragePrice = $("#_actualInventoryAveragePrice").val();
//
//            if (!actualInventoryAveragePrice) {
//                actualInventoryAveragePrice = 0;
//            }
//            inventoryAmount = inventoryAmount * 1;
//            actualInventoryAmount = actualInventoryAmount * 1;
//            actualInventoryAveragePrice = actualInventoryAveragePrice * 1;
//            inventoryAveragePrice = inventoryAveragePrice * 1;
//            if (Math.abs(inventoryAmount - actualInventoryAmount) < 0.0001 && Math.abs(actualInventoryAveragePrice - inventoryAveragePrice) < 0.0001) {
//                $("#updatePandian").removeAttr("clickFlag");
//                return;
//            }
//            var productId = $("#productDTOs" + index + "\\.productLocalInfoId").val();
//
//            if (!productId) {
//                $("#updatePandian").removeAttr("clickFlag");
//                return;
//            }
//            var paramJson = {
//                productId: productId,
//                inventoryAmount: inventoryAmount,
//                actualInventoryAmount: actualInventoryAmount,
//                actualInventoryAveragePrice: actualInventoryAveragePrice
//            };
//            if (APP_BCGOGO.Permission.Version.StoreHouse) {
//                var flag = true;
//                paramJson["storehouseId"] = $("#_storehouseId").val();
//                APP_BCGOGO.Net.syncPost({
//                    url: "inventoryCheck.do?method=validateInventoryCheck",
//                    dataType: "json",
//                    data: {"storehouseId": $("#_storehouseId").val()},
//                    success: function (result) {
//                        if (result.success) {
//                            flag = false;
//                        } else {
//                            nsDialog.jAlert(result.msg);
//                        }
//                    },
//                    error: function () {
//                        nsDialog.jAlert("验证时产生异常，请重试！", function () {
//                            $("#updatePandian").removeAttr("clickFlag");
//                        });
//                    }
//                });
//                if (flag) {
//                    $("#updatePandian").removeAttr("clickFlag");
//                    return;
//                }
//            }
//            $.get("stockSearch.do?method=ajaxtoupdateinventoryAmountAndAveragePrice&datatime=" + new Date().getTime(), paramJson, function (json) {
//                if (json.msg == "success") {
//                    var llc = $("#lowerLimitCount")[0];
//                    var ulc = $("#upperLimitCount")[0];
//                    if (llc) $(llc).html(json.memcacheLimitDTO.currentLowerLimitAmount);
//                    if (ulc) $(ulc).html(json.memcacheLimitDTO.currentUpperLimitAmount);
//
//                    var actualTotalInventoryNum = G.Lang.normalize(json.actualTotalInventoryNum, "0") * 1;
//                    //更新总库存
//                    $("#_inventoryNum").text(actualTotalInventoryNum);
//                    $("#_inventoryAveragePrice").text(G.Lang.normalize(json.actualTotalInventoryAveragePrice, "--"));
//
//                    var trPurchasePriceInput = $("#productDTOs" + index + "\\.purchasePrice");
//                    trPurchasePriceInput.prev("span[class='span_purchase_price']").html($("#_inventoryAveragePrice").text() + "/" + ($("#_purchasePrice").text() == null || $("#_purchasePrice").text() == "" ? "--" : $("#_purchasePrice").text()));
//                    var productIdObj = $("#productDTOs" + index + "\\.productLocalInfoId");
//                    //更新仓库库存  如果仓库作为查询条件了  那么每列的库存量都是显示当前仓库的，并且在盘点时候刷新 也是只刷新当前仓库的
//                    if (APP_BCGOGO.Permission.Version.StoreHouse) {
//                        $("#_" + $("#_storehouseId").val() + "_storehouse_inventory_amount").text(actualInventoryAmount);
//                        var searchStorehouseId = $("#storehouseText").val();
//                        if (G.Lang.isEmpty(searchStorehouseId)) {
//                            productIdObj.closest("tr").find(".product-inventory-span").html(actualTotalInventoryNum);
//                        } else {
//                            if ($("#_storehouseId").val() == $("#storehouseText").val()) {
//                                productIdObj.closest("tr").find(".product-inventory-span").html(actualInventoryAmount);
//                            }
//                        }
//                    } else {
//                        productIdObj.closest("tr").find(".product-inventory-span").html(actualTotalInventoryNum);
//                    }
//                    $("#inventoryNum" + index).val(actualTotalInventoryNum);
//                    //改盘点的时候复制下面
//                    for (i in selectedArr) {
//                        if (selectedArr[i]['id'] == productId) {
//                            selectedArr[i]['amount'] = actualTotalInventoryNum;
//                        }
//                    }
//                    $("#updatePandian").removeAttr("clickFlag");
//                    nsDialog.jAlert("盘点成功！");
//                } else {
//                    nsDialog.jAlert("更新库存盘点失败", null, function () {
//                        $("#updatePandian").removeAttr("clickFlag");
//                    });
//                }
//
//            }, "json");
//        };
//        var i = 0;
//        var t = setInterval(function () {
//            i++;
//            if (i > 50) {
//                clearInterval(t);
//                $("#updatePandian").removeAttr("commodityFlag");
//                $("#updatePandian").removeAttr("clickFlag");
//            }
//            if ($("#updatePandian").attr("commodityFlag") == "success" || !$("#updatePandian").attr("commodityFlag")) {
//                clearInterval(t);
//                $("#updatePandian").removeAttr("commodityFlag");
//                update_pandian_click();
//
//            } else if ($("#updatePandian").attr("commodityFlag") == "error") {
//                clearInterval(t);
//                $("#updatePandian").removeAttr("commodityFlag");
//                $("#updatePandian").removeAttr("clickFlag");
//            }
//        }, 200);
//    });


});

function ajaxToUpdateProduct(ajaxData) {
    $.get("stockSearch.do?method=ajaxtoupdateproduct&datatime=" + new Date().getTime(), ajaxData, function (json) {
        if (json.msg == "has the same product") {
            nsDialog.jAlert("已经存在此种商品，请重新输入商品信息", null, function () {
                $("#updateProduct").removeAttr("clickFlag");
            });
            return;
        } else if (json.msg == "updateError") {
            nsDialog.jAlert("更新失败", null, function () {
                $("#updateProduct").removeAttr("clickFlag");
            });
            return;
        } else if (json.msg == "inProgressOrder") {
            var html = "<b>商品正在被进行中的单据使用，无法修改基本信息！</b><br/>";
            for (var i = 0, len = json.data.length; i < len; i++) {
                html += "<a target='_blank' style='cursor: pointer;color: #6699CC;line-height:28px;'  href='" + json.data[i].url + "'>" + json.data[i].receiptNo + "</a>&nbsp;&nbsp;&nbsp;&nbsp;"
                if (i % 2 == 1) {
                    html += "<br/>";
                }
            }
            nsDialog.jAlert(html, "友情提示");
        } else {
            var llc = $("#lowerLimitCount")[0];
            var ulc = $("#upperLimitCount")[0];
            if (llc) $(llc).html(json.memcacheLimitDTO.currentLowerLimitAmount);
            if (ulc) $(ulc).html(json.memcacheLimitDTO.currentUpperLimitAmount);

            //暂时用此方法
            var index = $("#numIndex").val() * 1;
            bindProductDetail(index);

            nsDialog.jAlert("修改成功", null, function () {
                $("#updateProduct").removeAttr("clickFlag");
            });

        }
    }, "json");
}

function openSpecfiyWindown(windowName) {
    var jsonObj = jsonStrMap.get("stockSearch");
    if ($("#goodsIndex")[0] && "goodsIndex" == $("#goodsIndex").val()) {
        jsonObj = jsonStrMap.get("goodsIndex");
    }

    if (null == jsonObj || jsonObj.inventoryCount == 0) {
        nsDialog.jAlert("无数据，不能打印");
        return;
    }

    var jsonStr = JSON.stringify(jsonObj);

    $("#jsonStr").val(jsonStr);

    $("#currentPage").val($("#currentPage_stock_search").val());

    window.open('about:blank', windowName, 'width=1024,height=768,menubar=no,scrollbars=no');

}

function drawProductDetail(index) {
    $("#numIndex").val(index);
    if (APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier) {
        _drawSelectSupplierProductDetail(index);
    } else if (APP_BCGOGO.Permission.Version.StoreHouse) {
        _drawSelectSupplierProductDetail(index);
    } else {
        _drawStandardProductDetail(index);
    }
    if($("#productDTOs"+index+"\\.productInfo").find(".span_purchase_price").css("display") == "none"){
        $("#_minPurchasePrice,#_maxPurchasePrice,#_inventoryAveragePrice").hide();
    }else{
        $("#_minPurchasePrice,#_maxPurchasePrice,#_inventoryAveragePrice").show();
    }
}

function bindProductDetail(index){
    var url = "stockSearch.do?method=ajaxtogetproductbyid";
    var productId = $("#productDTOs" + index + "\\.productLocalInfoId").val();
    if (null == productId || "" == productId) {
        return;
    }
    var data = {
        productId: productId,
        now: new Date()
    }
    APP_BCGOGO.Net.asyncAjax({
        url: url,
        type: "POST",
        cache: false,
        data: data,
        dataType: "json",
        success: function (jsonObj) {
            if (APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier) {
                bindSelectSupplierProductDetail(index,jsonObj);
            } else if (APP_BCGOGO.Permission.Version.StoreHouse) {
                bindSelectSupplierProductDetail(index,jsonObj);
            } else {
                bindStandardProductDetail(index, jsonObj);
            }

            bindProductImageInfo(index, jsonObj);
            bindProductItem(index, jsonObj);
        }
    });


}
function bindProductImageInfo(index,jsonObj){
    var buttonBgUrl = "images/imageUploader.png";
    var buttonOverBgUrl="images/imageUploader.png";

    if(!G.Lang.isEmpty(jsonObj.imageCenterDTO) && !G.Lang.isEmpty(jsonObj.imageCenterDTO.productListSmallImageDetailDTO)){
        var imagePath = jsonObj.imageCenterDTO.productListSmallImageDetailDTO.imagePath;
        $("#_imagePath").val(imagePath);
        var buttonBgUrl = "images/imageUploaderAgain.png";
        var buttonOverBgUrl="images/imageUploaderAgain.png";
    }
    if(!G.Lang.isEmpty($.trim(($("#_addProductMainImageBtn").html())) && !G.Lang.isEmpty($.trim($("#_productMainImageView").html())))){
        return;
    }
    var imageUploader = new App.Module.ImageUploader();
    imageUploader.init({
        "selector":"#_addProductMainImageBtn",
        "flashvars":{
            "debug":"off",
            "maxFileNum":1,
            "width":61,
            "height":24,
            "buttonBgUrl":buttonBgUrl,
            "buttonOverBgUrl":buttonOverBgUrl,
            "currentItemNum": 0,
            "url":APP_BCGOGO.UpYun.UP_YUN_UPLOAD_DOMAIN_URL+"/"+APP_BCGOGO.UpYun.UP_YUN_BUCKET+"/",
            "ext":{
                "policy":$("#policy").val(),
                "signature":$("#signature").val()
            }
        },

        "startUploadCallback":function(message) {
            // 设置 视图组件 uploading 状态
            imageUploaderView.setState("uploading");
        },
        "uploadCompleteCallback":function(message, data) {
            var dataInfoJson = JSON.parse(data.info);
            $("#_imagePath").val(dataInfoJson.url);
        },
        "uploadErrorCallback":function(message, data) {
            var errorData = JSON.parse(data.info);
            errorData["content"] = data.info;
            saveUpLoadImageErrorInfo(errorData);
            nsDialog.jAlert("上传图片失败！");
        },
        "uploadAllCompleteCallback":function() {
            var imagePath = $("#_imagePath").val();
            // 设置 视图组件  idle 状态
            imageUploaderView.setState("idle");
            //更新input
            var outData = [{
                "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + imagePath + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.PRODUCT_LIST_IMAGE_SMALL
            }];
            imageUploaderView.update(outData);
            imageUploader.getFlashObject().deleteFile(0);
            //
            buttonBgUrl = "images/imageUploaderAgain.png";
            buttonOverBgUrl="images/imageUploaderAgain.png";
            imageUploader.getFlashObject().resetAddButton(false, buttonBgUrl, buttonOverBgUrl, 61, 24);
        }
    });
    /**
     * 视图组建
     * */
    var imageUploaderView = new App.Module.ImageUploaderView();
    imageUploaderView.init({
        // 你所需要注入的 dom 节点
        selector:"#_productMainImageView",
        width:60,
        height:62,
        iWidth:60,
        iHeight:60,
        paddingTop:0,
        paddingBottom:0,
        paddingLeft:0,
        paddingRight:0,
        horizontalGap:0,
        verticalGap:0,
        waitingInfo:"加载中...",
        showWaitingImage:false,
        maxFileNum:1,
        // 当删除某张图片时会触发此回调
        onDelete: function (event, data, index) {
            // 从已获得的图片数据池中删除 图片数据
            $("#_imagePath").val("");
            buttonBgUrl = "images/imageUploader.png";
            buttonOverBgUrl="images/imageUploader.png";
            imageUploader.getFlashObject().resetAddButton(false, buttonBgUrl, buttonOverBgUrl, 61, 24);
        }

    });
    if(!G.Lang.isEmpty($("#_imagePath").val())){
        imageUploaderView.setState("idle");
        var outData = [{
            "url": APP_BCGOGO.UpYun.UP_YUN_DOMAIN_URL + $("#_imagePath").val() + APP_BCGOGO.UpYun.UP_YUN_SEPARATOR + APP_BCGOGO.ImageScene.PRODUCT_LIST_IMAGE_SMALL,
        }];
        imageUploaderView.update(outData);
    }
}
function initInventoryCheckRecord(json){
     $("#inventoryCheckRecord tr:not(:first)").remove();
    var productId=json.data;
    if(G.isEmpty(productId)){
        return;
    }
    var trStr="";
    var inventoryChecks=json.results;
    if(G.isEmpty(inventoryChecks)){
        trStr+="<tr><td colspan='6'>无盘点记录！</td></tr>";
    }else{
        for(var i=0;i<inventoryChecks.length;i++){
            inventoryCheck=inventoryChecks[i];
            var editor=G.normalize(inventoryCheck.editor);
            var editDateStr=G.normalize(inventoryCheck.editDateStr);
            var storehouseName=G.normalize(inventoryCheck.storehouseName);
            var receiptNo=G.normalize(inventoryCheck.receiptNo);
            var adjustPriceTotal=G.rounding(inventoryCheck.adjustPriceTotal,2);
            var inventoryCheckId=inventoryCheck.idStr;
            var amountTotalStr=G.normalize(inventoryCheck.amountTotalStr);
            var amountTotal=G.rounding(inventoryCheck.amountTotal,2);
            trStr+="<tr>";
            trStr+='<td>'+editDateStr+'</td>';
            if(APP_BCGOGO.Permission.Version.StoreHouse){
                trStr+='<td>'+storehouseName+'</td>';
            }
            if(amountTotal>0){
                trStr+='<td class="green_color">'+"盘盈"+'</td>'
                trStr+='<td>'+amountTotalStr+'</td>';
                trStr+='<td class="green_color">'+adjustPriceTotal+'</td>';
            }else if(amountTotal<0){
                trStr+='<td class="red_color">'+"盘亏"+'</td>';
                trStr+='<td>'+amountTotalStr+'</td>';
                trStr+='<td class="red_color">'+adjustPriceTotal+'</td>';
            }else{
                trStr+='<td>'+"--"+'</td>'
                trStr+='<td>'+amountTotalStr+'</td>';
                trStr+='<td>'+0+'</td>';
            }

            trStr+='<td>'+editor+'</td>';
            trStr+='<td>'+'<a href ="#" class="blue_color" style="color:#006ECA" onclick="getInventoryCheckRecord(\'' + inventoryCheckId+'\')">' + receiptNo + '</a> '+'</td>';
            trStr+="</tr>";
        }

    }
    $("#inventoryCheckRecord").append(trStr);
    tableUtil.tableStyle('#inventoryCheckRecord','.tabTitle');

}

function getInventoryCheckRecord(inventoryCheckId){
    window.open('inventoryCheck.do?method=getInventoryCheck&inventoryCheckId='+inventoryCheckId);
}


function _drawSelectSupplierProductDetail(index) {
    var tr = '';
    tr += '<tr id="productDTOs' + index + '.productDetail">';
    tr += '<td colspan="11">';
    tr += '<div class="divListInfo">';
    tr += '<div class="clear i_height"></div>';
    tr += '<table cellpadding="0" cellspacing="0" class="tabInfo">';
    tr += '<col width="80">';
    tr += '<col width="110">';
    tr += '<col width="90">';
    tr += '<col width="120">';
    tr += '<tr>';
    tr += '<td>商品编码：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_commodityCode" maxlength="20"/></td>';
    tr += '<td>商品分类：</td>';
    tr += '<td><input type="text" class="txt J-bcgogo-droplist-on" value="" id="_productType" /></td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>品名：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_name" maxlength="50"/></td>';
    tr += '<td>品牌/产地：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_brand" maxlength="50"/></td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>规格：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_spec" maxlength="50"/></td>';
    tr += '<td>型号：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_model" maxlength="50"/></td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>车辆品牌：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_productVehicleBrand"  searchField="brand" cacheField="productVehicleBrandSource" maxlength="20"/></td>';
    tr += '<td>车型：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_productVehicleModel" cacheField="productVehicleModelSource" searchField="model" maxlength="20"/></td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td style="vertical-align: top;">商品主图：</td>';
    tr += '<td colspan="3"> <input type="hidden" id="_imagePath" value=""/>';
    tr += '<div id="_productMainImageView" style="float:left;position: relative;height:60px;width: 60px"></div>';
    tr += '<div style="float:left;width: 250px;padding-left:10px"><div style="float: left;width: 100%"><div style="text-align:left;float: left;width: 80px;">选择本地图片</div><div id="_addProductMainImageBtn"></div></div><div style="color: red;line-height: 18px;text-align: left">提示：所选图片都必须是 jpg、jpeg或png 格式，图片的大小不得超过5M。</div></div>';
    tr += '</td >';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>零售价：</td>';
    if(APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SalePrice){
        tr += '<td><input type="text" class="txt" value="" id="_recommendedPrice" maxlength="8"/></td>';
    }else{
        tr += '<td><input type="text" class="txt" value="" id="_recommendedPrice" maxlength="8" style="display: none"/></td>';
    }
    tr += '<td class="trade_price_td">批发价：</td>';
    if(APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.TradePrice){
        tr += '<td class="trade_price_td"><input type="text" class="txt" value="" id="_tradePrice" maxlength="8"/></td>';
    }else{
        tr += '<td class="trade_price_td"><input type="text" class="txt" value="" id="_tradePrice" maxlength="8" style="display: none"/></td>';
    }
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>最低进价：</td>';
    tr += '<td><span class="num" id="_minPurchasePrice">0</span></td>';
    tr += '<td>最高进价：</td>';
    tr += '<td><span class="num" id="_maxPurchasePrice">0</span></td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>平均进价：</td>';
    tr += '<td colspan="3"><span class="num inventory_average_price" id="_inventoryAveragePrice" >9.1</span>&nbsp;';
    //汽修高级版，有多个仓库可以调价
    if (!APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier && APP_BCGOGO.Permission.Version.StoreHouse) {
        tr += '<a class="blue_color num click_check" id="priceAdjustBtn">调价</a>';
        tr += '<div class="inventory J-priceAdjustDiv" style="display:none">';
        tr += '<input class="txt" id="_actualInventoryAveragePrice" type="text" style="width:85px;" initialValue="请输入调整价" value="请输入调整价">&nbsp;';
        tr += '<a class="btnSure" id="updatePriceAdjust">确 定</a>&nbsp;<a class="blue_color btnCancel" id="cancelPriceAdjust">取消</a>';
        tr += '</div>';
    }

    tr += '</td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>库存总量：</td>';
    tr += '<td><span class="num" id="_inventoryNum">0</span>&nbsp;</td>';
    tr += '<td>下限/上限：</td>';
    tr += '<td>';
    tr += '<input type="text" class="txt" value="" id="_lowerLimit" maxlength="8" style="width:40px;"/>&nbsp;/&nbsp;';
    tr += '<input type="text" class="txt" value="" id="_upperLimit" maxlength="8" style="width:41px;"/>';
    tr += '</td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td style="vertical-align: top;">单位：</td>';
    tr += '<td colspan="3"><div id="_unitContainer" style="float:left"></div>';
    tr += '</td >';
    tr += '</tr>';
    tr += '</table>';
    tr += '<div class="right_tab">';
    tr +=   '<div class="tit_bg">';
    tr +=   '<div class="tit_left" style="width:1px;"></div>';
    tr +=   '<div style="padding-left:10px; width:130px;">仓库名</div>';
    tr +=   '<div style="width:130px;">货位</div>';
    tr +=   '<div style="width:173px;">分库存量</div>';
    tr +=   '<div style="width:100px;"><a class="more toInventoryCheckProductRecord">查看盘点记录>></a></div>';
    tr +=   '<div class="tit_right" style="width:5px;"></div>';
    tr +=   '</div>';
    tr +=  '<div class="divMsg J_storehouseDiv" style="height: 100%;">';
    tr +=   '<table cellpadding="0" cellspacing="0" class="tabMsg" id="storeHouseTb">';
    tr +=   '<col width="1">';
    tr +=   '<col width="140">';
    tr +=   '<col width="130">';
    tr +=   '<col >';
    tr +=   '<col width="2">';
    tr +=   '<tr class="space"><td colspan="5"></td></tr>';
    tr +=   '</table>';
    tr += '</div>';
    tr += '<div class="clear i_height"></div>';
    tr += '<table cellpadding="0" cellspacing="0" class="tabMsg" id="supplierInventoryTb">';
    tr += '<col width="1" />';
    tr += '<col width="120" />';
    tr += '<col width="60" />';
    if (APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier) {
        tr += '<col width="60" />';
        tr += '<col width="60" />';
    }
    tr += '<col width="60" />';
    tr += '<col width="60" />';
    tr += '<col width="125" />';
    tr += '<col width="2" />';
    tr += '<tr class="tit_bg">';
    tr += '<td class="tit_left"></td>';
    tr += '<td style="padding-left:10px;">供应商</td>';
    tr += '<td>入库总量</td>';
    if (APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier) {
        tr += '<td>剩余库存</td>';
        tr += '<td>入库均价</td>';
    }
    tr += '<td>最后进价</td>';
    tr += '<td>最后进量</td>';
    tr += '<td>最后入库时间<a class="more J-more-supplierInfo">更多>></a></td>';
    tr += '<td class="tit_right"></td>';
    tr += '<tr class="space"><td colspan="7"></td></tr>';
    tr += '</table>';
    tr += '</div >';
    tr += '<div class="clear i_height"></div>';
    tr += '<div class="hr"></div>';
    tr += '<div class="clear i_height"></div>';
    tr += '<div class="divBtn"><a class="btnInventory" id="updateProduct">保&nbsp;存</a>&nbsp;<a class="btnInventory" id="resetProductInfo">重&nbsp;置</a></div>';
    tr += '<div class="clear i_height"></div>';
    tr += '<div class="div_Btn"><a class="btnUp"></a></div>';
    tr += '</div>';
    tr += '</td>';
    tr += '</tr>';
    $("#productDTOs" + index + "\\.titBottom").after($(tr));
    $("#productDTOs"+index+"\\.productDetail").find(".btnUp").bind("click",function(){
        $("#productDTOs"+index+"\\.productDetail").remove();
    });
}

function _drawStandardProductDetail(index) {
    var tr = '';
    tr += '<tr id="productDTOs' + index + '.productDetail">';
    tr += '<td colspan="11">';
    tr += '<div class="divListInfo">';
    tr += '<div class="clear i_height"></div>';
    tr += '<table cellpadding="0" cellspacing="0" class="tabInfo">';
    tr += '<col width="80">';
    tr += '<col width="110">';
    tr += '<col width="90">';
    tr += '<col width="120">';
    tr += '<tr>';
    tr += '<td>商品编码：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_commodityCode" maxlength="20"/></td>';
    tr += '<td>商品分类：</td>';
    tr += '<td><input type="text" class="txt J-bcgogo-droplist-on" value="" id="_productType" /></td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>品名：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_name" maxlength="50"/></td>';
    tr += '<td>品牌/产地：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_brand" maxlength="50"/></td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>规格：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_spec" maxlength="50"/></td>';
    tr += '<td>型号：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_model" maxlength="50"/></td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>车辆品牌：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_productVehicleBrand" searchField="brand" cacheField="productVehicleBrandSource" maxlength="20"/></td>';
    tr += '<td>车型：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_productVehicleModel" cacheField="productVehicleModelSource" searchField="model" maxlength="20"/></td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td style="vertical-align: top;">商品主图：</td>';
    tr += '<td colspan="3"> <input type="hidden" id="_imagePath" value=""/>';
    tr += '<div id="_productMainImageView" style="float:left;position: relative;height:60px;width: 60px"></div>';
    tr += '<div style="float:left;width: 250px;padding-left:10px"><div style="float: left;width: 100%"><div style="text-align:left;float: left;width: 80px;">选择本地图片</div><div id="_addProductMainImageBtn"></div></div><div style="color: red;line-height: 18px;text-align: left">提示：所选图片都必须是 jpg、jpeg或png 格式，图片的大小不得超过5M。</div></div>';
    tr += '</td >';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>零售价：</td>';
    if(APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SalePrice){
        tr += '<td><input type="text" class="txt" value="" id="_recommendedPrice" maxlength="8"/></td>';
    }else{
        tr += '<td><input type="text" class="txt" value="" id="_recommendedPrice" maxlength="8" style="display: none"/></td>';
    }
    tr += '<td class="trade_price_td">批发价：</td>';
    if(APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.TradePrice){
        tr += '<td class="trade_price_td"><input type="text" class="txt" value="" id="_tradePrice" maxlength="8"/></td>';
    }else{
        tr += '<td class="trade_price_td"><input type="text" class="txt" value="" id="_tradePrice" maxlength="8" style="display: none"/></td>';
    }
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>最低进价：</td>';
    tr += '<td><span class="num" id="_minPurchasePrice">0</span></td>';
    tr += '<td>最高进价：</td>';
    tr += '<td><span class="num" id="_maxPurchasePrice">0</span></td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>平均进价：</td>';
    tr += '<td colspan="3"><span class="num inventory_average_price" id="_inventoryAveragePrice" >9.1</span>&nbsp;';
    tr += '<a class="blue_color num click_check" id="priceAdjustBtn">调价</a>';
    tr += '<div class="inventory J-priceAdjustDiv" style="display:none">';
    tr += '<input class="txt" id="_actualInventoryAveragePrice" type="text" style="width:85px;" initialValue="请输入调整价" value="请输入调整价">&nbsp;';
    tr += '<a class="btnSure" id="updatePriceAdjust">确 定</a>&nbsp;<a class="blue_color btnCancel" id="cancelPriceAdjust">取消</a>';
    tr += '</div>';
    tr += '</td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>下限/上限：</td>';
    tr += '<td>';
    tr += '<input type="text" class="txt" value="" id="_lowerLimit" maxlength="8" style="width:40px;"/>&nbsp;/&nbsp;';
    tr += '<input type="text" class="txt" value="" id="_upperLimit" maxlength="8" style="width:41px;"/>';
    tr += '</td>';
    tr += '<td>货位：</td>';
    tr += '<td><input type="text" class="txt" value="" id="_storageBin" maxlength="10" /></td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td>库存总量：</td>';
    tr += '<td colspan="3"><span class="num" id="_inventoryNum">0</span>&nbsp;';
    tr += '<a class="blue_color num click_check" id="inventoryCheckBtn">盘点</a>';
    tr += '<div class="inventory J-inventoryCheckDiv" style="display:none;">';
    tr += '<input class="txt" id="_actualInventoryNum"  type="text" style="width:85px;" value="" initialValue="请输入盘点数量">&nbsp;';
    tr += '<a class="btnSure" id="updateInventoryCheck">确 定</a>&nbsp;<a class="blue_color btnCancel" id="cancelInventoryCheck"">取消</a>';
    tr += '</div><a class="blue_color toInventoryCheckProductRecord">查看盘点记录>></a>';
    tr += '</td>';
    tr += '</tr>';
    tr += '<tr>';
    tr += '<td style="vertical-align: top;">单位：</td>';
    tr += '<td colspan="3"><div id="_unitContainer" style="float:left"></div>';
    tr += '</td >';
    tr += '</tr>';
    tr += '</table>';
    tr += '<div class="right_tab">';
    tr += '<table cellpadding="0" cellspacing="0" class="tabMsg" id="supplierInventoryTb">';
    tr += '<col width="1" />';
    tr += '<col width="140" />';
    tr += '<col width="60" />';
    tr += '<col width="60" />';
    tr += '<col width="55" />';
    tr += '<col width="120" />';
    tr += '<col width="2" />';
    tr += '<tr class="tit_bg">';
    tr += '<td class="tit_left"></td>';
    tr += '<td style="padding-left:10px;">供应商</td>';
    tr += '<td>入库总量</td>';
    tr += '<td>最后进价</td>';
    tr += '<td>最后进量</td>';
    tr += '<td>最后入库时间<a class="more J-more-supplierInfo">更多>></a></td>';
    tr += '<td class="tit_right"></td>';
    tr += '<tr class="space"><td colspan="7"></td></tr>';
    tr += '</table>';
    tr += '</div >';
    tr += '<div class="clear i_height"></div>';
    tr += '<div class="hr"></div>';
    tr += '<div class="clear i_height"></div>';
    tr += '<div class="divBtn"><a class="btnInventory" id="updateProduct">保&nbsp;存</a>&nbsp;<a class="btnInventory" id="resetProductInfo">重&nbsp;置</a></div>';
    tr += '<div class="clear i_height"></div>';
    tr += '<div class="div_Btn"><a class="btnUp"></a></div>';
    tr += '</div>';
    tr += '</td>';
    tr += '</tr>';
    $("#productDTOs" + index + "\\.titBottom").after($(tr));
    $("#productDTOs"+index+"\\.productDetail").find(".btnUp").bind("click",function(){
        $("#productDTOs"+index+"\\.productDetail").remove();
    });
}



function bindStandardProductDetail(index, jsonObj) {
    /* 绑定下拉层里的数据 begin */
    $('#_commodityCode').val(G.Lang.normalize(jsonObj.commodityCode));
    if (jsonObj.commodityCode) {
        $("#_commodityCode").attr("lastcommoditycode", jsonObj.commodityCode);
    }
    $('#_name').val(G.Lang.normalize(jsonObj.productName));
    $('#_brand').val(G.Lang.normalize(jsonObj.productBrand));
    $('#_spec').val(G.Lang.normalize(jsonObj.productSpec));
    $('#_model').val(G.Lang.normalize(jsonObj.productModel));


    $('#_productVehicleBrand').val(G.Lang.normalize(jsonObj.brand));
    $('#_productVehicleModel').val(G.Lang.normalize(jsonObj.model));
    $('#_productType').val(G.Lang.normalize(jsonObj.kindName));
    $('#_inventoryNum').text(dataTransition.simpleRounding(jsonObj.amount, 2));
//    $("#_actualInventoryNum").val(dataTransition.simpleRounding(jsonObj.amount, 1));


    $('#_inventoryAveragePrice').text(dataTransition.simpleRounding(jsonObj.inventoryAveragePrice, 2));
//    $("#_actualInventoryAveragePrice").val(dataTransition.simpleRounding(jsonObj.inventoryAveragePrice, 2));

    $('#_purchasePrice').text(dataTransition.simpleRounding(jsonObj.purchasePrice, 2));
    if ($(".recommendedPrice_input")[0]) {
        $('#_recommendedPrice').val(dataTransition.simpleRounding(jsonObj.recommendedPrice, 2));
    }

    $('#_tradePrice').val(dataTransition.simpleRounding(jsonObj.tradePrice, 2));
    $('#_storageBin').val(G.Lang.normalize(jsonObj.storageBin));

    $('#_upperLimit').val(G.Lang.normalize(jsonObj.upperLimit));
    $('#_lowerLimit').val(G.Lang.normalize(jsonObj.lowerLimit));

    $('#_maxPurchasePrice').text(dataTransition.simpleRounding(jsonObj.maxPurchasePrice, 2));
    $('#_minPurchasePrice').text(dataTransition.simpleRounding(jsonObj.minPurchasePrice, 2));
//    var unit = G.Lang.normalize(jsonObj.unit);
    var storageUnit = G.Lang.normalize(jsonObj.storageUnit);
    var sellUnit = G.Lang.normalize(jsonObj.sellUnit);
    var rate = G.Lang.normalize(jsonObj.rate);
//    sellUnit = "个";
//    storageUnit = "个";
    var unitHtml = '';
    if(!sellUnit){
        unitHtml += '<input type="txt" id="firstUnit" maxlength="10" class="txt" style="width:30px;">';
    }else if(sellUnit == storageUnit){
        unitHtml += '<span class="num units" style="float:left" title="' + sellUnit + '">'+sellUnit+'<input type="hidden" id="firstUnit_Hidden" value="' + sellUnit + '"></span>';
        unitHtml += '<a class="blue_color num" id="addUnit">增加单位</a>';
        unitHtml += '<div class="divUnit" id="secondUnitContainer"  style="display:none">';
        unitHtml +=     '<span>增加单位：<input type="text" id="secondUnit" class="txt" maxlength="7" style="width:25px;"/>&nbsp;&nbsp;</span>';
        unitHtml += '</div>';

        unitHtml += '<div class="inventory divUnit" id="rateContainer"  style="margin-left:51px;display:none">';
        unitHtml +=     '<span style="float:left">换算比例：<input type="text" class="txt" style="width:25px;" id="secondRate" />&nbsp;</span>';
        unitHtml +=     '<span id="secondUnitSpan" class="units"  style="width:15px;float:left"></span>';
        unitHtml +=     '<span>=<input id="firstRate" type="text" class="txt" style="width:25px;" /></span>';
        unitHtml +=     '<span class="units" title="'+sellUnit+'" style="width:15px;">&nbsp;'+sellUnit+'&nbsp;</span>&nbsp;';
        unitHtml +=     '<a class="btnSure" id="sureAddUnit">确 定</a>&nbsp;<a class="blue_color btnCancel" id="cancelAddUnit">取消</a>';
        unitHtml += '</div>';

    }else{
        unitHtml += '<span class="num">'+sellUnit+'</span>';
        unitHtml += '<div class="sureUnit" style="float: right; margin-right: 10px; display: block;">';
        unitHtml += '&nbsp;包装单位:'+storageUnit;
        unitHtml += '<span style="color:#787878;">（1'+storageUnit+'&nbsp;='+rate+sellUnit+'）</span>';
        unitHtml += '</div>';
    }
    $("#_unitContainer").html(unitHtml);

//    $("#_sellUnit").val(unit);
//    $("#_storageUnit").val(storageUnit);
//    if (unit != "" && storageUnit != "" && unit != storageUnit) {
//        $("#_sellUnit").css("width", "25%");
//        $("#smallRate").css("display", "inline");
//        $("#_storageUnit").css("display", "inline");
//        $("#rate").text(G.Lang.normalize(jsonObj.rate)).css("display", "inline");
//        $("#equal").css("display", "inline");
//    } else {
//        $("#_sellUnit").css("width", "75%");
//        $("#smallRate").css("display", "none");
//        //            $("_storageUnit").val(storageUnit);
//        //            $("#rate").val(jsonObj.rate).show();
//        $("#_storageUnit").css("display", "none");
//        $("#rate").css("display", "none");
//        $("#equal").css("display", "none");
//    }
//    var storeHouseHtml = '';
//    if (APP_BCGOGO.Permission.Version.StoreHouse) {
//        $.each(jsonObj.storeHouseInventoryDTOMap, function (key, value) {
//            var storeHouseName = value.storeHouseName;
//            var storageBin = G.Lang.normalize(value.storageBin,"");
//            var amount =  G.Lang.normalize(value.amount,0);
//            storeHouseHtml += '<tr>';
//            storeHouseHtml += '<td></td>';
//            storeHouseHtml += '<td style="padding-left:10px;"><div class="line">' + storeHouseName + '</div></td>';
//            storeHouseHtml += '<td><input type="text" class="txt" id="_' + key + '_storehouse_storageBin" value = "'+storageBin+'"></td>';
//            storeHouseHtml += '<td>';
//            storeHouseHtml +=   '<span class="saveNum">'+amount+sellUnit+'</span>';
//            storeHouseHtml +=   '<input type="hidden" id="_' + key + '_storehouse_inventory_amount" value = "'+amount+'">';
//            storeHouseHtml +=   '<a class="blue_color click_check">盘点</a>';
//            storeHouseHtml +=   '<div class="inventory" style="display:none;">';
//            storeHouseHtml +=       '<input class="txt" type="text" style="width:85px;" initialValue="请输入盘点数量">';
//            storeHouseHtml +=       '<a class="btnInventory">确 定</a>&nbsp;<a class="btnInventory btnCancel">取 消</a>';
//            storeHouseHtml +=   '</div>';
//            storeHouseHtml += '</td>';
////            $("#_" + key + "_storehouse_inventory_amount").text(value.amount);
////            $("#_" + key + "_storehouse_storageBin").val(G.Lang.normalize(value.storageBin));
//
//        });
//        $("#storeHouseTb tr:gt(0)").remove();
//        $("#storeHouseTb").append(storeHouseHtml);
//
//        if (jsonObj && jsonObj.storeHouseInventoryDTOMap && !$.isEmptyObject(jsonObj.storeHouseInventoryDTOMap) && jsonObj.storeHouseInventoryDTOMap[$("#_storehouseId").val()]) {
//            $("#_actualInventoryNum").val(jsonObj.storeHouseInventoryDTOMap[$("#_storehouseId").val()].amount);
//        } else {
//            $("#_actualInventoryNum").val(0);
//        }
//    }
    var tr = '';
    //supplierInventoryTb
    if (jsonObj.supplierInventoryDTOs) {
        for(var i = 0; i < jsonObj.supplierInventoryDTOs.length && i < 10; i++) {

            var supplierInventoryDTO = jsonObj.supplierInventoryDTOs[i];
            var supplierName = G.normalize(supplierInventoryDTO.supplierName, "");
            var supplierId = G.normalize(supplierInventoryDTO.supplierIdStr, "");
            if(G.Lang.isEmpty(supplierId)){
                continue;
            }
            var totalInStorageAmount = dataTransition.simpleRounding(supplierInventoryDTO.totalInStorageAmount, 1);
            var unit = G.normalize(supplierInventoryDTO.unit, "");
            var lastStoragePrice = dataTransition.simpleRounding(supplierInventoryDTO.lastStoragePrice, 2);
            if(unit){
                lastStoragePrice += '/'+unit;
            }
            var lastStorageAmount = dataTransition.simpleRounding(supplierInventoryDTO.lastStorageAmount, 1);
            var lastStorageTimeStr = G.normalize(supplierInventoryDTO.lastStorageTimeStr, "");
            tr += '<tr>';
            tr += '<td></td>';
            tr += '<td style="padding-left:10px;">';
            if(supplierId){
                tr += '<a class="blue_color line" href="unitlink.do?method=supplier&supplierId=' + supplierId + '" target="_blank">' + supplierName+'</a>';
            }else{
                tr += supplierName;
            }

            tr += '</td>';
            tr += '<td>' + totalInStorageAmount + unit + '</td>';
            tr += '<td>' + lastStoragePrice + '</td>';
            tr += '<td>' + lastStorageAmount + '</td>';
            tr += '<td>' + lastStorageTimeStr + '</td>';
            tr += '</tr>';
        }
    } else{
        $("#supplierInventoryTb .more").hide();
    }
    $("#supplierInventoryTb tr:gt(1)").remove();
    $("#supplierInventoryTb").append(tr);

    if (!APP_BCGOGO.Permission.Txn.InventoryManage.ProductModify) {
          $("[id$='.productDetail'] input").attr("disabled", "disabled");
          $("#btn").css("display", "none");
          $(".priceAdjust").css("display", "none");
      }


    /* 绑定下拉层里的数据 end */

}
function bindSelectSupplierProductDetail(index, jsonObj) {
    /* 绑定下拉层里的数据 begin */
    $('#_commodityCode').val(G.Lang.normalize(jsonObj.commodityCode));
    if (jsonObj.commodityCode) {
        $("#_commodityCode").attr("lastcommoditycode", jsonObj.commodityCode);
    }
    $('#_name').val(G.Lang.normalize(jsonObj.productName));
    $('#_brand').val(G.Lang.normalize(jsonObj.productBrand));
    $('#_spec').val(G.Lang.normalize(jsonObj.productSpec));
    $('#_model').val(G.Lang.normalize(jsonObj.productModel));


    $('#_productVehicleBrand').val(G.Lang.normalize(jsonObj.brand));
    $('#_productVehicleModel').val(G.Lang.normalize(jsonObj.model));
    $('#_productType').val(G.Lang.normalize(jsonObj.kindName));
    $('#_inventoryNum').text(dataTransition.simpleRounding(jsonObj.amount, 1));
//    $("#_actualInventoryNum").val(dataTransition.simpleRounding(jsonObj.amount, 1));


    $('#_inventoryAveragePrice').text(dataTransition.simpleRounding(jsonObj.inventoryAveragePrice, 2));
//    $("#_actualInventoryAveragePrice").val(dataTransition.simpleRounding(jsonObj.inventoryAveragePrice, 2));

    $('#_purchasePrice').text(dataTransition.simpleRounding(jsonObj.purchasePrice, 2));
    if ($(".recommendedPrice_input")[0]) {
        $('#_recommendedPrice').val(dataTransition.simpleRounding(jsonObj.recommendedPrice, 2));
    }

    $('#_tradePrice').val(dataTransition.simpleRounding(jsonObj.tradePrice, 2));
    $('#_storageBin').val(G.Lang.normalize(jsonObj.storageBin));

    $('#_upperLimit').val(G.Lang.normalize(jsonObj.upperLimit));
    $('#_lowerLimit').val(G.Lang.normalize(jsonObj.lowerLimit));

    $('#_maxPurchasePrice').text(dataTransition.simpleRounding(jsonObj.maxPurchasePrice, 2));
    $('#_minPurchasePrice').text(dataTransition.simpleRounding(jsonObj.minPurchasePrice, 2));
//    var unit = G.Lang.normalize(jsonObj.unit);
    var storageUnit = G.Lang.normalize(jsonObj.storageUnit);
    var sellUnit = G.Lang.normalize(jsonObj.sellUnit);
    var rate = G.Lang.normalize(jsonObj.rate);
//    sellUnit = "个";
//    storageUnit = "个";
    var unitHtml = '';
    if(!sellUnit){
        unitHtml += '<input type="txt" id="firstUnit" maxlength="10" class="txt" style="width:30px;">';
    }else if(sellUnit == storageUnit){
        unitHtml += '<span class="num units" style="float:left" title="' + sellUnit + '">'+sellUnit+'<input type="hidden" id="firstUnit_Hidden" value="' + sellUnit + '"></span>';
        unitHtml += '<a class="blue_color num" id="addUnit">增加单位</a>';
        unitHtml += '<div class="divUnit" id="secondUnitContainer"  style="display:none">';
        unitHtml +=     '<span>增加单位：<input type="text" id="secondUnit" class="txt" maxlength="7" style="width:25px;"/>&nbsp;&nbsp;</span>';
        unitHtml += '</div>';

        unitHtml += '<div class="inventory divUnit" id="rateContainer"  style="margin-left:51px;display:none">';
        unitHtml +=     '<span style="float:left">换算比例：<input type="text" class="txt" style="width:25px;" id="secondRate" />&nbsp;</span>';
        unitHtml +=     '<span id="secondUnitSpan" class="units"  style="width:15px;float:left"></span>';
        unitHtml +=     '<span>=<input id="firstRate" type="text" class="txt" style="width:25px;" /></span>';
        unitHtml +=     '<span class="units" title="'+sellUnit+'" style="width:15px;">&nbsp;'+sellUnit+'&nbsp;</span>&nbsp;';
        unitHtml +=     '<a class="btnSure" id="sureAddUnit">确 定</a>&nbsp;<a class="blue_color btnCancel" id="cancelAddUnit">取消</a>';
        unitHtml += '</div>';
    }else{
        unitHtml += '<span class="num">'+sellUnit+'</span>';
        unitHtml += '<div class="sureUnit" style="float: right; margin-right: 10px; display: block;">';
        unitHtml += '&nbsp;包装单位:'+storageUnit;
        unitHtml += '<span style="color:#787878;">（1'+storageUnit+'&nbsp;='+rate+sellUnit+'）</span>';
        unitHtml += '</div>';
    }
    $("#_unitContainer").html(unitHtml);
    var storeHouseHtml = '';
    if (APP_BCGOGO.Permission.Version.StoreHouse) {
        var isHaveStoreHouse = false;

        $.each(jsonObj.storeHouseInventoryDTOMap, function (key, value) {
            isHaveStoreHouse = true;
            var storeHouseName = value.storeHouseName;
            var storageBin = G.Lang.normalize(value.storageBin, "");
            var amount = G.Lang.normalize(value.amount, 0);
            storeHouseHtml += '<tr>';
            storeHouseHtml += '<td></td>';
            storeHouseHtml += '<td style="padding-left:10px;"><div class="line" title="' + storeHouseName + '">' + storeHouseName + '</div></td>';
            storeHouseHtml += '<td><input type="text" class="txt" id="_' + key + '_storehouse_storageBin" value = "' + storageBin + '" data-storehouse-id="' + key + '"></td>';
            storeHouseHtml += '<td>';
            storeHouseHtml += '<span class="saveNum" id="_' + key + '_storehouse_inventory_amount_span" title="' + amount + sellUnit + '">' + amount + sellUnit + '</span>';
            storeHouseHtml += '<input type="hidden" id="_' + key + '_storehouse_inventory_amount" value = "' + amount + '">';
            storeHouseHtml += '<input type="hidden" id="_' + key + '_storehouse_id" value = "' + key + '">';
            if (APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier) {
                storeHouseHtml += '<a class="blue_color click_check J-selectSupplierStoreHouseCheck">盘点</a>';
            } else {
                storeHouseHtml += '<a class="blue_color click_check J-storeHouseCheck">盘点</a>';
                storeHouseHtml += '<div class="inventory J-storeHouseCheckContainer" style="display:none;">';
                storeHouseHtml += '<input class="txt" type="text" style="width:100px;" initialValue="请输入盘点数量" id="_' + key + '_actualInventoryNum">&nbsp';
                storeHouseHtml += '<a class="btnSure J-SureStoreHouseCheck">确 定</a>&nbsp;&nbsp;<a class="blue_color btnCancel J-CancelStoreHouseCheck">取 消</a>';
                storeHouseHtml += '</div>';
            }

            storeHouseHtml += '</td>';
        });
        if (!isHaveStoreHouse) {
            storeHouseHtml += '<tr>';
            storeHouseHtml += '<td colspan="4">暂无数据！</td>';
            storeHouseHtml += "</tr>"
        }

        $("#storeHouseTb tr:gt(0)").remove();
        $("#storeHouseTb").append(storeHouseHtml);
        if($(".J_storehouseDiv")[0]){
            var $J_storehouseDiv = $(".J_storehouseDiv").eq(0);
            if($J_storehouseDiv.height()>140){
                $J_storehouseDiv.css("height","140px");
            }
        }

        if (jsonObj && jsonObj.storeHouseInventoryDTOMap && !$.isEmptyObject(jsonObj.storeHouseInventoryDTOMap) && jsonObj.storeHouseInventoryDTOMap[$("#_storehouseId").val()]) {
            $("#_actualInventoryNum").val(jsonObj.storeHouseInventoryDTOMap[$("#_storehouseId").val()].amount);
        } else {
            $("#_actualInventoryNum").val(0);
        }
    }
    var maxSupplierInventoryItem = 3;
    if ($(".J_storehouseDiv")[0]) {
        var $J_storehouseDiv = $(".J_storehouseDiv").eq(0);
        var $tabInfo =   $(".tabInfo").eq(0);
        maxSupplierInventoryItem = ($tabInfo.height()- $(".divListInfo .tit_bg").eq(1).height()*2 - $J_storehouseDiv.height())/30;
    }
    if(maxSupplierInventoryItem<3){
        maxSupplierInventoryItem = 3;
    }else if(maxSupplierInventoryItem > 8){
        maxSupplierInventoryItem = 8;
    }
    var tr = '';
    //supplierInventoryTb
    if (jsonObj.supplierInventoryDTOs) {
        for(var i = 0; i < jsonObj.supplierInventoryDTOs.length && i < maxSupplierInventoryItem; i++) {
            var supplierInventoryDTO = jsonObj.supplierInventoryDTOs[i];
            var supplierName = G.normalize(supplierInventoryDTO.supplierName, "");
            var supplierId = G.normalize(supplierInventoryDTO.supplierIdStr, "");
            if (G.Lang.isEmpty(supplierId) && !APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier) {
                continue;
            }
            var totalInStorageAmount = dataTransition.simpleRounding(supplierInventoryDTO.totalInStorageAmount, 1);
            var unit = G.normalize(supplierInventoryDTO.unit, "");
            var lastStoragePrice = dataTransition.simpleRounding(supplierInventoryDTO.lastStoragePrice, 2);
            var remainAmount = dataTransition.simpleRounding(supplierInventoryDTO.remainAmount, 1);
            if(unit){
                lastStoragePrice += '/'+unit;
            }


            var lastStorageAmount = dataTransition.simpleRounding(supplierInventoryDTO.lastStorageAmount, 1);
            var lastStorageTimeStr = G.normalize(supplierInventoryDTO.lastStorageTimeStr, "");
            var averageStoragePrice =  dataTransition.simpleRounding(supplierInventoryDTO.averageStoragePrice, 1);
            tr += '<tr>';
            tr += '<td></td>';
            tr += '<td style="padding-left:10px;">';
            if(supplierId){
                tr += '<a class="blue_color" href="unitlink.do?method=supplier&supplierId=' + supplierId + '" target="_blank" title="'+supplierName+'">' + supplierName+'</a>';
            }else{
                tr += supplierName;
            }

            tr += '</td>';
            tr += '<td>' + totalInStorageAmount + unit + '</td>';
            if (APP_BCGOGO.Permission.Version.ProductThroughSelectSupplier) {
                tr += '<td>' + remainAmount + unit + '</td>';
                tr += '<td>' + averageStoragePrice + '</td>';
            }
            tr += '<td>' + lastStoragePrice + '</td>';
            tr += '<td>' + lastStorageAmount + '</td>';
            tr += '<td>' + lastStorageTimeStr + '</td>';
            tr += '</tr>';
        }
    } else{
        $("#supplierInventoryTb .more").hide();
    }
    $("#supplierInventoryTb tr:gt(1)").remove();
    $("#supplierInventoryTb").append(tr);

    if (!APP_BCGOGO.Permission.Txn.InventoryManage.ProductModify) {
          $("[id$='.productDetail'] input").attr("disabled", "disabled");
          $("#btn").css("display", "none");
          $(".priceAdjust").css("display", "none");
      }


    /* 绑定下拉层里的数据 end */

}

function bindProductItem(index, jsonObj) {
    if (!jsonObj) {
        return;
    }
    var productVehicleModel = G.normalize(jsonObj.model, "");
    var productVehicleBrand = G.normalize(jsonObj.brand, "");
    var productName = G.normalize(jsonObj.productName, "");
    var productBrand = G.normalize(jsonObj.productBrand, "");
    var productSpec = G.normalize(jsonObj.productSpec, "");
    var productModel = G.normalize(jsonObj.productModel, "");
    var commodityCode = G.normalize(jsonObj.commodityCode, "");
    var productLocalInfoId = G.normalize(jsonObj.productIdStr, "");
    var sellUnit = G.normalize(jsonObj.sellUnit, "");

    /* 重新加载改行的数据 begin */
    var productNameInfo = '';
    if (stringUtil.isNotEmpty(commodityCode)) {
        productNameInfo += commodityCode + "&nbsp;"
    }
    if (APP_BCGOGO.Permission.InquiryCenter) {
        var data = {
            pageType: "inventory",
            productName: productName,
            productBrand: productBrand,
            productSpec: productSpec,
            productModel: productModel,
            productVehicleModel: productVehicleModel,
            productVehicleBrand: productVehicleBrand,
            commodityCode:commodityCode
        };
        arr[index] = data;
        productNameInfo += '<a class="line blue_color" >' + productName + '</a>&nbsp;';
    } else {
        productNameInfo += productName;
    }
    if (stringUtil.isNotEmpty(productBrand)) {
        productNameInfo +=  "&nbsp;" + productBrand;
    }
    if (stringUtil.isNotEmpty(productSpec)) {
        productNameInfo += "&nbsp;" + productSpec;
    }
    if (stringUtil.isNotEmpty(productModel)) {
        productNameInfo +=   "&nbsp;" + productModel;
    }

    if (stringUtil.isNotEmpty(productVehicleBrand)) {
        productNameInfo +=  "&nbsp;" + productVehicleBrand;
    }
    if (stringUtil.isNotEmpty(productVehicleModel)) {
        productNameInfo +=  "&nbsp;" + productVehicleModel;
    }
    var $productInfoTr = $("#productDTOs" + index + "\\.productInfo");
    $productInfoTr.find(".J-productNameInfo").html(productNameInfo);


    //库存量
    var showInventoryNum = G.Lang.normalize(jsonObj.amount, "0");
    //如果仓库作为查询条件了  那么每列的库存量都是显示当前仓库的
    if (APP_BCGOGO.Permission.Version.StoreHouse) {
        var searchStorehouseId = $("#storehouseText").val();
        if (!G.Lang.isEmpty(searchStorehouseId)) {
            showInventoryNum = G.Lang.normalize(jsonObj.storeHouseInventoryDTOMap[searchStorehouseId].amount, "0");
        }
    }
    $productInfoTr.find(".J-product-inventory-span").html(showInventoryNum);
    $("#inventoryNum" + index).val(G.Lang.normalize(jsonObj.amount, "0"));
    //单位
    $productInfoTr.find(".J-product_sell_unit").html(sellUnit);


    var averagePriceStr = dataTransition.rounding(jsonObj.inventoryAveragePrice, 2);
    var purchasePriceStr = dataTransition.rounding(jsonObj.purchasePrice, 2);

    //均价/最新价
    if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.AveragePrice && APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.NewStoragePrice) {
        $productInfoTr.find(".span_average_price").html('<span class="arialFont">&yen;</span>' + averagePriceStr);
        $productInfoTr.find(".span_purchase_price").html('<span class="arialFont">&yen;</span>' + purchasePriceStr);
    } else if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.NewStoragePrice) {
        $productInfoTr.find(".span_purchase_price").html('<span class="arialFont">&yen;</span>' + purchasePriceStr);
    } else if (APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.AveragePrice) {
        $productInfoTr.find(".span_average_price").html('<span class="arialFont">&yen;</span>' + averagePriceStr);
    }

    $("#productDTOs" + index + "\\.purchasePrice").val(purchasePriceStr);

    //商品分类
    $("#productDTOs" + index + "\\.kind").val(G.Lang.normalize(jsonObj.kindName));
    //销售价
    $("#productDTOs" + index + "\\.recommendedPrice").val(dataTransition.simpleRounding(jsonObj.recommendedPrice, 2));
    //批发价
    $("#productDTOs" + index + "\\.tradePrice").val(dataTransition.simpleRounding(jsonObj.tradePrice, 2));
    $("#productDTOs" + index + "\\.inventoryAveragePrice").val(averagePriceStr);

    //仓位
    $("#productDTOs" + index + "\\.storageBin").val(G.Lang.normalize(jsonObj.storageBin, ""));
    //上下限
    $("#productDTOs" + index + "\\.lowerLimit").val(G.Lang.normalize(jsonObj.lowerLimit));
    $("#productDTOs" + index + "\\.upperLimit").val(G.Lang.normalize(jsonObj.upperLimit));
//    if (!APP_BCGOGO.Permission.Txn.InventoryManage.ProductModify) {
//        $("#pop_info input").attr("disabled", "disabled");
//        $("#btn").css("display", "none");
//        $(".priceAdjust").css("display", "none");
//    }

    /* 重新加载改行的数据 end */
}

function validateAddSecondUnit() {
    var firstUnit = $("#firstUnit_Hidden").val();
    var secondUnit = $.trim($("#secondUnit").val());
    var secondRate = $.trim($("#secondRate").val());
    var firstRate = $.trim($("#firstRate").val());

//    if (G.isEmpty(secondUnit) && G.isEmpty(secondRate) && G.isEmpty(firstRate)) {
//        return true;
//    }
    if (G.isEmpty(secondUnit)) {
        nsDialog.jAlert("增加单位不能为空！请重新填写");
        return false;
    }else if(secondUnit == firstUnit){
        nsDialog.jAlert("增加单位与原单位不能相同！请重新填写");
        return false;
    }
    if (isNaN(firstRate) || isNaN(secondRate) || G.isEmpty(secondRate) || G.isEmpty(firstRate)
        || !(secondRate * 1 == 1 || firstRate * 1 == 1) || firstRate * 1<1 || secondRate * 1<1 ) {
        nsDialog.jAlert("增加单位换算比例格式不正确！请重新填写<br><br><span>例如：1箱=10个 或者 10个=1箱<span>");
        return false;
    }
    return true;
}

function drawMoreSupplier(json) {
    var tr = '';
    tr += '<colgroup>';
    tr += '<col width="140">';
    tr += '<col width="60">';
    tr += '<col width="50">';
    tr += '<col width="60">';
    tr += '<col width="55">';
    tr += '<col width="120">';
    tr += '</colgroup>';
    tr += '<tr class="tabTitle">';
    tr += '<td style="padding-left:10px;">供应商</td>';
    tr += '<td>入库总量</td>';
    tr += '<td>平均价</td>';
    tr += '<td>最后进价</td>';
    tr += '<td>最后进量</td>';
    tr += '<td>最后入库时间</td>';
    tr += '</tr>'
    if (json.supplierInventoryDTOs && json.supplierInventoryDTOs.length > 0) {
        for (var i = 0; i < json.supplierInventoryDTOs.length; i++) {
            var supplierInventoryDTO = json.supplierInventoryDTOs[i];
            var supplierName = G.normalize(supplierInventoryDTO.supplierName, "");
            var supplierId = G.normalize(supplierInventoryDTO.supplierIdStr, "");
            var unit = G.normalize(supplierInventoryDTO.unit, "");
            var totalInStorageAmount = dataTransition.simpleRounding(supplierInventoryDTO.totalInStorageAmount, 1);
            var lastStoragePrice = dataTransition.simpleRounding(supplierInventoryDTO.lastStoragePrice, 2);
            if (unit) {
                lastStoragePrice += '/' + unit;
                totalInStorageAmount += unit;
            }
            var lastStorageAmount = dataTransition.simpleRounding(supplierInventoryDTO.lastStorageAmount, 1);
            var lastStorageTimeStr = G.normalize(supplierInventoryDTO.lastStorageTimeStr, "");
            var averagePrice = dataTransition.simpleRounding(supplierInventoryDTO.averageStoragePrice, 2);
            tr += '<tr>';
            tr += '<td style="padding-left:10px;">';
            if (supplierId) {
                tr += '<a class="blue_color" style="color: #007EFF;" href="unitlink.do?method=supplier&supplierId=' + supplierId + '" target="_blank">' + supplierName + '</a>';
            } else {
                tr += supplierName;
            }
            tr += '</td>';
            tr += '<td>' + totalInStorageAmount + '</td>';
            tr += '<td>' + averagePrice + '</td>';
            tr += '<td>' + lastStoragePrice + '</td>';
            tr += '<td>' + lastStorageAmount + '</td>';
            tr += '<td>' + lastStorageTimeStr + '</td>';
            tr += '</tr>';
        }
    }
    $("#moreSupplierInfo_dialog table").html(tr);
}

function drawSelectSupplierInventoryCheckTable(json) {
    var tr = '';
    var totalAmount = 0;
    var totalUnit = '';
    if (json.supplierInventoryDTOs && json.supplierInventoryDTOs.length > 0) {
        for (var i = 0; i < json.supplierInventoryDTOs.length; i++) {
            var supplierInventoryDTO = json.supplierInventoryDTOs[i];
            var supplierName = G.normalize(supplierInventoryDTO.supplierName, "");
            var supplierId = G.normalize(supplierInventoryDTO.supplierIdStr, "");
            var unit = G.normalize(supplierInventoryDTO.unit, "");
            var supplierType = G.normalize(supplierInventoryDTO.supplierType, "");
            totalUnit = unit;
            var remainAmount = dataTransition.simpleRounding(supplierInventoryDTO.remainAmount, 1);
            totalAmount += remainAmount*1;
            tr += '<tr>';
            tr += '<td style="padding-left:10px;">'+supplierName+'</td>';
            tr += '<td>' + remainAmount+unit + '</td>';
            tr += '<td>';
            tr += '<input type="hidden" name="itemDTOs[0].outStorageRelationDTOs['+i+'].relatedSupplierId" value="'+supplierId+'">';
            tr += '<input type="hidden" name="itemDTOs[0].outStorageRelationDTOs['+i+'].relatedSupplierName" value="'+supplierName+'">';
            tr += '<input type="hidden" name="itemDTOs[0].outStorageRelationDTOs['+i+'].supplierType" value="'+supplierType+'">';
            tr += '<input type="hidden" name="itemDTOs[0].outStorageRelationDTOs['+i+'].relatedSupplierInventory" value="'+remainAmount+'">';
            tr += '<input type="text"  name="itemDTOs[0].outStorageRelationDTOs['+i+'].useRelatedAmount" class="txt red_color" value="' + remainAmount
                + '" style="width: 60px" maxlength="6"/>&nbsp;' + unit + '</td>';
            tr += '</tr>';
        }
    }
    $("#selectSupplierInventoryCheckTb").append(tr);
    $("#inventoryCheckActualAmount").text(dataTransition.simpleRounding(totalAmount,1));
    $("#inventoryCheckActualUnit").text(unit);
}

function setRemindCount(repairLackCount,purchasePendingCount){
    if(repairLackCount == null){
        repairLackCount = $("#repairLackCountSpan").text()*1;
    }else{
        $(".J-repairLackCount").each(function(){
            $(this).text(repairLackCount);
        })
        repairLackCount = repairLackCount *1;
    }
    if(purchasePendingCount == null){
        purchasePendingCount = $("#purchaseRemindCount").text()*1;
    }else{
        $(".J-purchaseRemindCount").each(function(){
            $(this).text(purchasePendingCount);
        })
        purchasePendingCount = purchasePendingCount* 1;
    }
    $("#totalRemindCount").text(repairLackCount +purchasePendingCount );

}

function clearSort() {
    var searchField = ["searchWord", "productName", "productBrand", "productSpec", "productModel",
        "productVehicleBrand", "productVehicleModel", "product_kind", "commodityCode", "supplierInfoSearchText"];
    var isEmpty = true;
    for (var i = 0; i < searchField.length; i++) {
        var thisVal = $.trim($("#" + searchField[i]).val());
        var thisInitVal = $.trim($("#" + searchField[i]).attr("initialValue"));
        if (!GLOBAL.Lang.isEmpty(thisVal) && thisVal != thisInitVal) {
            isEmpty = false;
        }
    }
    if (!isEmpty) {
        $(".J_product_sort").each(function () {
            $(this).removeClass("hover");
        });
        $("#sortStatus").val("");
    } else {
        if (GLOBAL.Lang.isEmpty($("#sortStatus").val())) {
            var $dom = $("[sortFiled='inventoryAmount']");
            $dom.addClass("hover");
            $dom.find(".J-sort-span").removeClass("arrowUp").addClass("arrowDown");
            $dom.attr("currentSortStatus", "Desc");
            $dom.find(".alertBody").html($dom.attr("ascContact"));
            $("#sortStatus").val($dom.attr("sortFiled") + $dom.attr("currentSortStatus"));
        }
    }

}


function stockSearchBeforeExport() {
    var strUtil = new commonStrUtil();
    var searchWord = strUtil.excludeSpecifiedStr($("#searchWord").attr("initialValue"), $("#searchWord").val());
    var pn = strUtil.excludeSpecifiedStr($("#productName").attr("initialValue"), $("#productName").val());
    var product_commodity_code = strUtil.excludeSpecifiedStr($("#commodityCode").attr("initialValue"), $("#commodityCode").val());
    var pb = strUtil.excludeSpecifiedStr($("#productBrand").attr("initialValue"), $("#productBrand").val());
    var ps = strUtil.excludeSpecifiedStr($("#productSpec").attr("initialValue"), $("#productSpec").val());
    var pm = strUtil.excludeSpecifiedStr($("#productModel").attr("initialValue"), $("#productModel").val());
    var pvb = strUtil.excludeSpecifiedStr($("#productVehicleBrand").attr("initialValue"), $("#productVehicleBrand").val());
    var pvm = strUtil.excludeSpecifiedStr($("#productVehicleModel").attr("initialValue"), $("#productVehicleModel").val());
    var pKind = strUtil.excludeSpecifiedStr($("#product_kind").attr("initialValue"), $("#product_kind").val());
    var supplierKeyWord, supplierId;
    supplierKeyWord = strUtil.excludeSpecifiedStr($("#supplierInfoSearchText").attr("initialValue"), $("#supplierInfoSearchText").val());
    supplierId = $("#supplierInfoSearchText").attr("customerorsupplierid");
    var searchProductIds = "";
    if ($("#searchProductIds")) {
        searchProductIds = $("#searchProductIds").val();
    }
    var ajaxUrl = "export.do?method=exportInventory";
    var ajaxData = null;
    var searchMode = $("#searchMode").val();
    if(searchMode == 'stockSearchWithCertainField') {
        //精确搜索
        ajaxData = {
            commodityCode: product_commodity_code,
            productName: pn,
            productBrand: pb,
            productSpec: ps,
            productModel: pm,
            productVehicleBrand: pvb,
            productVehicleModel: pvm,
            productKind: pKind,
            supplierKeyWord: supplierKeyWord,
            supplierId: supplierId,
            productIds: searchProductIds,
            sort: $("#sortStatus").val(),
            includeBasic: false
        };

    } else if(searchMode == 'LOWER_LIMIT') {
        ajaxData = {
            inventoryAlarm: 'LOWER_LIMIT',
            sortStatus: $("#sortStatus").val()
        };

    } else if(searchMode == 'UPPER_LIMIT') {
        ajaxData = {
            inventoryAlarm: 'UPPER_LIMIT',
            sortStatus: $("#sortStatus").val()
        };
    } else {
        //模糊搜索
        ajaxData = {
            searchWord: searchWord,
            supplierKeyWord: supplierKeyWord,
            supplierId: supplierId,
            commodityCode: product_commodity_code,
            productName: pn,
            productBrand: pb,
            productSpec: ps,
            productModel: pm,
            productVehicleBrand: pvb,
            productVehicleModel: pvm,
            productKind: pKind,
            productIds: searchProductIds,
            sort: $("#sortStatus").val(),
            includeBasic: false
        };

    }
    return {
        ajaxUrl: ajaxUrl,
        ajaxData: ajaxData
    };
}
