/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-4-18
 * Time: 上午11:27
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(function() {
    $(".J-initialCss").placeHolder();


    var now = new Date();
    var year = now.getFullYear();



    $("#startDate,#endDate")
        .datepicker({
            "numberOfMonths": 1,
            "showButtonPanel": false,
            "changeYear": true,
            "showHour": false,
            "showMinute": false,
            "changeMonth": true,
            "yearRange": "c-100:c+100",
            "yearSuffix": ""
        })
        .blur(function () {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            if (startDate == "" || endDate == "") return;
            if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (startDate > endDate) {
                    $("#endDate").val(startDate);
                    $("#startDate").val(endDate);
                }
            }
        })
        .bind("click", function () {
            $(this).blur();
        })
        .change(function () {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            $(".good_his > .today_list").removeClass("hoverList");
            if (endDate == "" || startDate == "") {
                return;
            }
            if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (startDate > endDate) {
                    $("#endDate").val(startDate);
                    $("#startDate").val(endDate);
                }
            }
        });


    $("#allStoreHouseCheck").live("click",function() {
        $(".J_storehouseCheck").attr("checked",this.checked);
    });
    $(".J_storehouseCheck").live("click",function() {
        var isSelectAll = $(".J_storehouseCheck").length == $(".J_storehouseCheck:checked").length ? true : false;
        $("#allStoreHouseCheck").attr("checked",isSelectAll);
    });

    $("#my_date_lastyear,#my_date_lastmonth,#my_date_thismonth,#my_date_thisyear").bind("click",function(){
        $("#searchBtn").click();
    })

    $('input[name="storehouseIds"]').bind("click",function(){
        $("#searchBtn").click();
    })

    $('input[name="itemType"]').bind("click",function(){
        $("#searchBtn").click();
    })

    $("#clearBtn").bind("click",function(){
        $(".clicked").removeClass("clicked");
        $("#startDate").val("");
        $("#endDate").val("");
        $("input[type='checkbox']").attr("checked",false);
        $(".J_clear_input").val("");
        $(".J-initialCss").placeHolder("reset");
        $("#productCategory").val("--所有商品分类--");
        $("#productCategory").css("color","#ADADAD");


    });

    $("#searchBtn").click(function () {
        $(".J-initialCss").placeHolder("clear");
        var param = $("#productThroughSearchForm").serializeArray();
        var data = {};
        $.each(param, function (index, val) {
            if(!G.Lang.isEmpty(data[val.name])){
                data[val.name] = data[val.name]+","+val.value;
            }else{
                data[val.name] = val.value;
            }
        });
        if(data["productKind"]=="---所有商品分类---"){
            data["productKind"]="";
        }
        $(".J-initialCss").placeHolder("reset");
        var ajaxUrl = "productThrough.do?method=getProductThroughRecord";

        APP_BCGOGO.Net.syncPost({
            url: ajaxUrl,
            data: data,
            dataType: "json",
            success: function (json) {
                initProductThroughRecord(json);
                initPages(json, "productThroughRecord", ajaxUrl, '', "initProductThroughRecord", '', '',data,'');
            }
        });
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
        })
        .bind("change",function(){
            $("#productId").val("");
        });

    function productSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        var productKind=$("#productCategory").val()=="--所有商品分类--"?"":$("#productCategory").val();
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var currentSearchField = $domObject.attr("searchField");
        var ajaxData = {
            productKind:productKind,
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
        ajaxData["productKind"]=productKind;
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
                        $("#productId").val("");
                        dropList.hide();
                        $("#searchBtn").click();
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
                        $("#productId").val("");
                        $("#searchBtn").click();
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
                        $("#productId").val("");
                        dropList.hide();
                        $("#searchBtn").click();
                    },"onKeyboardSelect":function (event, index, data) {
                        $domObject.val(data.label);
                        $("#productId").val("");
                        $("#searchBtn").click();
                    }
                });
            }

        });
    }
    //客户下拉框
    $(".J-customerSuggestion")
        .bind('click', function () {
            customerSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                customerSuggestion($(this));
            }
        })
        .bind("change",function(){
            $("#customerId").val("");
        });

    $("#productCategory").bind('click', function () {
        $(this).val("");
        getProductKindSuggestion($(this));
    })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getProductKindSuggestion($(this));
            }

        })

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
                    $("#searchBtn").click();
                }
            });
        });
    }
    function customerSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var ajaxData = {
            searchWord: searchWord,
            searchField:"info",
            customerOrSupplier: "customer",
            titles:"name,contact,mobile",
            uuid: dropList.getUUID()
        };
        var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            dropList.show({
                "selector":$domObject,
                "data":result,
                "autoSet": false,
                "data": result,
                onGetInputtingData: function() {
                    return {
                        details:{"name":$domObject.val(),"id":$("#customerId").val()}
                    };
                },
                "onSelect":function (event, index, data) {
                    $domObject.val(data.details["name"]);
                    $domObject.css({"color":"#000000"});
                    $("#customerId").val(data.details["id"]);
                    dropList.hide();
                    $("#searchBtn").click();
                },"onKeyboardSelect":function (event, index, data) {
                    $domObject.val(data.details["name"]);
                    $("#customerId").val(data.details["id"]);
                }
            });
        });

    }

    $(".J-supplierSuggestion")
        .bind('click', function () {
            supplierSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                supplierSuggestion($(this));
            }
        })
        .bind("change",function(){
            $("#supplierId").val("");
        });


    $("#dateSort").bind("click",function(){
        clickOnCondition("dateSort","order_created_time");
        $("#searchBtn").click();
    });

    $("#storageSort").bind("click",function(){
        clickOnCondition("storageSort","item_count");
        $("#searchBtn").click();
    });

    $("#shipmentsSort").bind("click",function(){
        clickOnCondition("shipmentsSort","item_count");
        $("#searchBtn").click();
    });

    $("#dateSort").click();


    function supplierSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = APP_BCGOGO.Module.droplist;
        dropList.setUUID(GLOBAL.Util.generateUUID());
        var ajaxData = {
            searchWord: searchWord,
            searchField:"info",
            customerOrSupplier: "supplier",
            titles:"name,contact,mobile",
            uuid: dropList.getUUID()
        };
        var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            dropList.show({
                "selector":$domObject,
                "autoSet": false,
                "data": result,
                onGetInputtingData: function() {
                    return {
                        details:{"name":$domObject.val(),"id":$("#supplierId").val()}
                    };
                },
                "onSelect":function (event, index, data) {
                    $domObject.val(data.details["name"]);
                    $domObject.css({"color":"#000000"});
                    $("#supplierId").val(data.details["id"]);
                    dropList.hide();
                    $("#searchBtn").click();
                },"onKeyboardSelect":function (event, index, data) {
                    $domObject.val(data.details["name"]);
                    $("#supplierId").val(data.details["id"]);
                }
            });
        });

    }

});


function initProductThroughRecord(data) {

    $("#productThroughTable tr:not('.J-title')").remove();
    $("#countForListDiv").remove();
    var bottomTr="";
    var countStr="";
    if(APP_BCGOGO.Permission.Version.StoreHouse){
        bottomTr="<tr class='titBottom_Bg'><td colspan='12'></td></tr>";
    }else{
        bottomTr="<tr class='titBottom_Bg'><td colspan='11'></td></tr>";
    }
    if(data == null || data[0] == null || data[0].inOutRecords.length==0){
        countStr+='<div id="countForListDiv"><div class="left"><p><strong>共0条&nbsp;&nbsp;</strong><span>总成本：<b class="yellow_color">0</b></span>&nbsp;&nbsp;<span>总收入：' +
            '<b class="yellow_color">0</b></span>&nbsp;&nbsp;<span>总利润：<b class="yellow_color">0</b></span></p></div></div>';
    }else{
        countStr+='<div id="countForListDiv"><div class="left"><p><strong>共'+data[0].numFound+'条&nbsp;&nbsp;</strong><span>总成本：<b class="yellow_color">'+data[0].totalAmounts.ITEM_TOTAL_COST_PRICE.toFixed(2)+'</b></span>&nbsp;&nbsp;<span>总收入：' +
            '<b class="yellow_color">'+data[0].totalAmounts.ITEM_TOTAL.toFixed(2)+'</b></span>&nbsp;&nbsp;<span>总利润：<b class="yellow_color">'+(data[0].totalAmounts.ITEM_TOTAL-data[0].totalAmounts.ITEM_TOTAL_COST_PRICE).toFixed(2)+'</b></span></p></div></div>';
    }

    if (data == null || data[0] == null || data[0].inOutRecords.length==0 ) {
        if(APP_BCGOGO.Permission.Version.StoreHouse){
            $("#productThroughTable").append("<tr class='titBody_Bg'><td style='padding-left:10px;' colspan='9'>没有数据!</td></tr>");
        }else{
            $("#productThroughTable").append("<tr class='titBody_Bg'><td style='padding-left:10px;' colspan='8'>没有数据!</td></tr>");
        }
        $("#productThroughTable").append($(bottomTr));
        $("#countForList").append($(countStr));
        return;
    }



    $.each(data[0].inOutRecords, function (index, record) {
        var dateStr=record.createdTimeStr;
        var commodityCode = record.commodityCode == null ? "" : record.commodityCode+"&nbsp;";
        var productName = record.productName == null ? "" : record.productName+"&nbsp;";
        var productBrand = record.productBrand == null ? "" : record.productBrand+"&nbsp;";
        var productSpec = record.productSpec == null ? "" : record.productSpec+"&nbsp;";
        var productModel = record.productModel == null ? "" : record.productModel;
        var productVehicleBrand = record.productVehicleBrand == null ? "" : record.productVehicleBrand+"&nbsp;";
        var productVehicleModel = record.productVehicleModel == null ? "" : record.productVehicleModel;
        var productInfo = commodityCode+"<span style='font-weight: bold;'>"+productName+productBrand+"</span>"+productSpec+productModel+productVehicleBrand+productVehicleModel;
        var productInfoTitle = commodityCode+productName+productBrand+productSpec+productModel+productVehicleBrand+productVehicleModel;


        var relatedSupplierName= G.normalize(record.relatedSupplierName,"--");
        var relatedCustomerName=G.normalize(record.relatedCustomerName,"--");
        var orderTypeValue = record.orderTypeValue;
        var orderType = record.orderType;
        var storehouseName= record.storehouseName;
        var inAmount ="--",outAmount="--";
        var itemCount = record.itemCount;

        var itemPrice=record.itemPrice;
        var itemTotal=record.itemTotal;//收入
        var itemTotalCostPrice=record.itemTotalCostPrice;//成本
        var itemTotalProfit=itemTotal-itemTotalCostPrice;
        if(itemTotalProfit.toString().length > itemTotalProfit.toString().indexOf(".")+2){
             itemTotalProfit=itemTotalProfit.toFixed(2);
        }
        var unit = G.normalize(record.unit);
        if(record.itemType=="IN"){
            inAmount=itemCount+unit;
        }else{
            outAmount =itemCount+unit;
        }
        var orderReceiptNo = record.orderReceiptNo;
        var orderIdStr = record.orderIdStr;

        var tr = "<tr class='titBody_Bg'> ";
        tr += "<td style='padding-left:10px;'>"+dateStr+"</td>";
        tr += "    <td title='"+productInfoTitle+"'>"+productInfo+"</td>";
        tr += "    <td>"+relatedSupplierName+"</td>";
        tr += "    <td>"+orderTypeValue+"</td>";
        tr += "    <td>"+relatedCustomerName+"</td>";
        if(APP_BCGOGO.Permission.Version.StoreHouse){
            tr += "    <td>"+storehouseName+"</td>";
        }

        tr += "    <td>"+inAmount+"</td>";
        tr += "    <td>"+outAmount+"</td>";

        tr+="<td>"+itemTotalCostPrice+"</td>";
        tr+="<td>"+itemTotal+"</td>";

        tr+="<td>"+itemTotalProfit+"</td>";


        tr += "    <td>";
        if ("SALE" == orderType) {
            tr += '<a class="blue_color" target="_blank" style="cursor: pointer" href="sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=' + orderIdStr + '">' + orderReceiptNo + '</a>';
        }else if ("INVENTORY" == orderType) {
            tr += '<a class="blue_color" target="_blank" style="cursor: pointer" href="storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=' + orderIdStr + '">' + orderReceiptNo + '</a>';
        }else if ("RETURN" == orderType) {
            tr += '<a class="blue_color" target="_blank" style="cursor: pointer" href="goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=' + orderIdStr + '">' + orderReceiptNo + '</a>';
        }else if ("REPAIR" == orderType) {
            tr += '<a class="blue_color" target="_blank" style="cursor: pointer" href="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderIdStr + '">' + orderReceiptNo + '</a>';
        }else if ("SALE_RETURN" == orderType) {
            tr += '<a class="blue_color" target="_blank" style="cursor: pointer" href="salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=' + orderIdStr + '">' + orderReceiptNo + '</a>';
        }else if ("REPAIR_PICKING" == orderType) {
            tr += '<a class="blue_color" target="_blank" style="cursor: pointer" href="pick.do?method=showRepairPicking&repairPickingId=' + orderIdStr + '">' + orderReceiptNo + '</a>';
        }else if ("INNER_PICKING" == orderType) {
            tr += '<a class="blue_color" target="_blank" style="cursor: pointer" href="pick.do?method=showInnerPicking&innerPickingId=' + orderIdStr + '">' + orderReceiptNo + '</a>';
        }else if ("INNER_RETURN" == orderType) {
            tr += '<a class="blue_color" target="_blank" style="cursor: pointer" href="pick.do?method=showInnerReturn&innerReturnId=' + orderIdStr + '">' + orderReceiptNo + '</a>';
        }else if ("BORROW_ORDER" == orderType) {
            tr += '<a class="blue_color" target="_blank" style="cursor: pointer" href="borrow.do?method=toBorrowOrderDetail&borrowOrderId=' + orderIdStr + '">' + orderReceiptNo + '</a>';
        }else if ("ALLOCATE_RECORD" == orderType) {
            tr += '<a class="blue_color" target="_blank" style="cursor: pointer" href="allocateRecord.do?method=showAllocateRecordByAllocateRecordId&allocateRecordId=' + orderIdStr + '">' + orderReceiptNo + '</a>';
        }else if ("INVENTORY_CHECK" == orderType) {
            tr += '<a class="blue_color" target="_blank" style="cursor: pointer" href="inventoryCheck.do?method=getInventoryCheck&inventoryCheckId=' + orderIdStr + '">' + orderReceiptNo + '</a>';
        }else {
            tr += orderReceiptNo;
        }
        tr +="</td>";
        tr += "</tr>";

        $("#productThroughTable").append($(tr));
        $("#productThroughTable").append($(bottomTr));

    });
    $("#countForList").append($(countStr));
}


function clickOnCondition(conditionId,sqlColumn){

    if($("a[name='J_sortStyle']").not(this).hasClass("hover")){
        $("a[name='J_sortStyle']").removeClass("hover");
    }
    $("#"+conditionId).addClass("hover");
    var sortStr = "";
    if ($("#"+conditionId+"Span").hasClass("arrowDown")) {
        $("#"+conditionId+"Span").addClass("arrowUp").removeClass("arrowDown");
        if(conditionId=="storageSort"){
            sortStr ="item_type desc, "+sqlColumn+" asc ";
        }else if(conditionId=="shipmentsSort"){
            sortStr ="item_type asc, "+sqlColumn+" asc ";
        }else{
            sortStr =" "+sqlColumn+" asc ";
        }

    } else {
        $("#"+conditionId+"Span").addClass("arrowDown").removeClass("arrowUp");
        if(conditionId=="storageSort"){
            sortStr ="item_type desc, "+sqlColumn+" desc ";
        }else if(conditionId=="shipmentsSort"){
            sortStr ="item_type asc, "+sqlColumn+" desc ";
        }else{
            sortStr =" "+sqlColumn+" desc ";
        }
    }
    if($("#sort")){
        $("#sort").val(sortStr);
    }
}

