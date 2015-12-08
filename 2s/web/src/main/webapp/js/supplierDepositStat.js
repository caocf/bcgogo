$(document).ready(function() {

    $("#statBtn").click(function(e) {
        e.preventDefault();
        e.stopPropagation();
        $("#depositStat").submit();
    });

    // 绑定“统计”事件
    $("#depositStat").submit(function (e) {
        e.preventDefault();
        e.stopPropagation();
        $("#deposit_orders_table tr:not(:first)").remove(); // 清理表格

        var paramJson = prepareQueryCondition();
        var url = "depositOrdersStat.do?method=ajaxQueryDepositOrders";
        APP_BCGOGO.Net.syncPost({
            url:url,
            dataType: "json",
            data:paramJson,
            success: function (data) {
                initDepositOrdersTable(data);
                initStatFields(data);
                var functionName = "initDepositOrdersTable", dynamical = "dynamical1";
                initPage(data, dynamical, url, '', functionName, '', '', paramJson, '');
            },
            error: function () {
                alert("网络异常，请联系客服");
            }
        });
    });

    // 绑定reset事件
    $("#resetSearchCondition").click(function() {

        $("#depositStat").resetForm();
        resetCustomerText();
        resetInOutFlag();
        clearSupplierId();
        if(!$("#my_date_thismonth").hasClass("clicked")){
            $("#my_date_thismonth").click();
        }else{
            $("#my_date_thismonth").click();
            $("#my_date_thismonth").click();
        }

    });

    $("#my_date_thismonth").click();
    // 绑定表头查询事件
    $("#depositOrdersTime,#depositOrdersMoney").bind("click", function(e) {
        $("#deposit_orders_table tr:not(:first)").remove(); // 清理表格
        var sortName;
        var sortFlag = e.target.className === 'descending' ? 'ascending' : 'descending';
        if (e.target.id === 'depositOrdersTime') {
            sortName = 'time';
            $('#depositOrdersTime').attr('class', sortFlag);
        } else if (e.target.id === 'depositOrdersMoney') {
            sortName = 'money';
            $('#depositOrdersMoney').attr('class', sortFlag);
        }
        var param = $("#depositStat").serializeArray(); // 将Form数据序列化为json格式
        var paramJson = {};
        $.each(param, function (index, val) {
            paramJson[val.name] = val.value;
        });
        if (paramJson["supplierName"] == "供应商名") {
            paramJson["supplierName"] = "";
        }
        if (paramJson["supplierMobile"] == "手机号码") {
            paramJson["supplierMobile"] = "";
        }
        if (paramJson["inFlag"] && paramJson["inFlag"] != null && paramJson["outFlag"] && paramJson["outFlag"] != null) {
            paramJson["inOut"] = 0;
        } else if (paramJson["inFlag"] && paramJson["inFlag"] != null) {
            paramJson["inOut"] = paramJson["inFlag"];
        } else if (paramJson["outFlag"] && paramJson["outFlag"] != null) {
            paramJson["inOut"] = paramJson["outFlag"];
        } else {
            paramJson["inOut"] = null;
        }
        /* 默认查询预收款统计信息，按事件倒序排列 */
        paramJson["sortName"] = sortName;
        paramJson["sortFlag"] = sortFlag;
        var url = "depositOrdersStat.do?method=ajaxQueryDepositOrders";
        APP_BCGOGO.Net.syncPost({
            url:url,
            dataType: "json",
            data:paramJson,
            success: function (data) {
                initStatFields(data);
                initDepositOrdersTable(data);
                var functionName = "initDepositOrdersTable", dynamical = "dynamical1";
                initPage(data, dynamical, url, '', functionName, '', '', paramJson, '');
            },
            error: function () {
                alert("网络异常，请联系客服");
            }
        });
        e.stopPropagation();
    });

    // 绑定搜索下拉事件
    $("#supplierName")
        .bind('click', function (event) {
            getSupplierSuggestion($(this), event);
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getSupplierSuggestion(this, event);
            }
        })
        .bind('blur', function(event){
            syncGetSupplierSuggestion(this,event);
        })
        .bind('change',function(event){
            clearSupplierId();
            getSupplierSuggestion(this, event);
        })
        .bind('focus',function(event){
            getSupplierSuggestion(this,event);
        })
        .placeHolder();

    $("#supplierMobile").placeHolder();

    // 绑定datepicker
    $("#startDate,#endDate")
        .datepicker({
            "numberOfMonths":1,
            "showButtonPanel":false,
            "changeYear":true,
            "showHour":false,
            "showMinute":false,
            "changeMonth":true,
            "yearRange":"c-100:c+100",
            "yearSuffix":""
        })
        .blur(function() {
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
        .bind("click", function() {
            $(this).blur();
        })
        .change(function() {
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



    $("#printBtn").bind("click", function() {
        var paramJson = prepareQueryCondition();
        var url = "depositOrdersStat.do?method=printDepositStat";
        APP_BCGOGO.Net.syncPost({
            url: url,
            data: paramJson,
            success: function (data) {
                if (!data) {
                    return;
                }
                var printWin = window.open("", "", "width=1024,height=768");
                with (printWin.document) {
                    open("text/html", "replace");
                    write(data);
                    close();
                }
            },
            error: function () {
                alert("网络异常，请联系客服");
            }
        });
    });

    $("#resetSearchCondition").click(); // 恢复默认查询条件
    $("#depositStat").submit(); // 加载完毕 默认进行查询

});

function prepareQueryCondition() {
    var param = $("#depositStat").serializeArray(); // 将Form数据序列化为json格式
    var paramJson = {};
    $.each(param, function (index, val) {
        paramJson[val.name] = val.value;
    });
    if (paramJson["supplierName"] == "供应商名") {
        paramJson["supplierName"] = "";
    }
    if (paramJson["supplierMobile"] == "手机号码") {
        paramJson["supplierMobile"] = "";
    }
    if (paramJson["inFlag"] && paramJson["inFlag"] != null && paramJson["outFlag"] && paramJson["outFlag"] != null) {
        paramJson["inOut"] = 0;
    } else if (paramJson["inFlag"] && paramJson["inFlag"] != null) {
        paramJson["inOut"] = paramJson["inFlag"];
    } else if (paramJson["outFlag"] && paramJson["outFlag"] != null) {
        paramJson["inOut"] = paramJson["outFlag"];
    } else {
        paramJson["inOut"] = 99; // 后台为long类型，不能传null
    }
    /* 默认查询预收款统计信息，按事件倒序排列 */
    paramJson["sortName"] = "time";
    paramJson["sortFlag"] = "descending";
    return paramJson;
}

function initStatFields(json) {
    if (json.pager.totalRows) {
        $('#totalCount').text(json.pager.totalRows);
    } else {
        $('#totalCount').text(0);
        $('#inTotalAmount').text(0);
        $('#outTotalAmount').text(0);
    }
    var stats = json.data;
    if (stats) {
        $('#inTotalAmount').text(stats[0]);
        $('#outTotalAmount').text(stats[1]);
    }
}

function initDepositOrdersTable(jsonStr) {
    depositOrdersTableContentInit(jsonStr);
    //depositOrdersTableStyleInit();
    var pager = jsonStr.pager;
    $("#maxRows").val(pager.pageSize);
    $("#currentPage").val(pager.currentPage);
}

function depositOrdersTableContentInit(json) {
    var data = json.results;
    $("#deposit_orders_table tr:not(:first)").remove(); // remove掉已经存在的表格数据
    if (data && data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            var tr = "<tr class='titBody_Bg' depositOrderId='" + data[i].id + "'>";
            tr += '<td style="padding-left:10px;">' + data[i].createdTime + '</td>';
            tr += '<td>' + data[i].name + '</td>';
            tr += '<td>' + data[i].mobile + '</td>';
            if (data[i].inOut && data[i].inOut === 1) {
                tr += '<td>' + '收款' + '</td>';
            }
            else {
                tr += '<td>' + '取用' + '</td>';
            }
            tr += '<td>' + dataTransition.rounding(data[i].actuallyPaid, 2) + '元</td>';
            var depositTypes = data[i].depositType.split("|");
            tr += '<td>' + depositTypes[1] + '</td>';
            if (data[i].relatedOrderNo) {
                tr += '<td><a class="blue_color" href="' +
                    genUrlByDepositTypeAndId(depositTypes[0], data[i].relatedOrderIdStr) + '">' + data[i].relatedOrderNo + '</a></td>'; //TODO 这边根据单据的类型生成URL
            } else {
                tr += '<td>' + '-' + '</td>';
            }
            tr += '<td>' + data[i].operator + '</td>';
            tr += '<td>' + '备注' + '</td>';
            tr += '</tr>';
            tr += '<tr class="titBottom_Bg"><td colspan="9"></td></tr>'
            var $tr = $(tr);
            $("#deposit_orders_table").append($tr);
        }
    }
}

function genUrlByDepositTypeAndId(type, id) {
    var urlPrefix;
    if (type) {
        if (type == "SALES" || type == "SALES_REPEAL") {
            urlPrefix = 'sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=';
        }
        if (type == "SALES_BACK" || type == "SALES_BACK_REPEAL") {
            urlPrefix = " salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=";
        }
        if (type == "INVENTORY" || type == "INVENTORY_REPEAL") {
            urlPrefix = "storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=";
        }
        if (type == "INVENTORY_BACK" || type == "INVENTORY_BACK_REPEAL") {
            urlPrefix = "goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=";
        }
        if (type == "COMPARE"){
            urlPrefix = "statementAccount.do?method=showStatementAccountOrderById&statementOrderId=";
        }
    }
    return urlPrefix + id;
}

function depositOrdersTableStyleInit() {
    tableUtil.tableStyle('#deposit_orders_table', '.tab_title,.title');
}

function resetCustomerText() {
    $("#supplierName, #supplierMobile").css("color", "#9a9a9a");
}

function resetInOutFlag() {
    $('#inFlag').attr('checked', 'checked');
    $('#outFlag').attr('checked', 'checked');
}

function clearSupplierId(){
    $('#supplierId').val("");
}

function getSupplierSuggestion(domObject, event) {
    var keycode = event?event.which || event.keyCode : null;
    var $domObject = $(domObject);
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    searchWord = $.trim(searchWord).toUpperCase();
    if (searchWord == "供应商名") {
        searchWord = "";
    }
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        searchWord:searchWord.toUpperCase(),
        uuid:droplist.getUUID()
    };
    ajaxData["customerOrSupplier"] = "supplier";
    ajaxData["titles"] = "name,mobile";
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        if (!G.isEmpty(result.data[0])) {
            G.completer({
                    'domObject': domObject,
                    'keycode': keycode,
                    'title': result.data[0].details.name}
            );
        }
        if(event && event.type != 'blur'){
            droplist.show({
                "selector": $domObject,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.details.name);
                    $("#supplierId").val(data.details.id);
                    var mobile = data.details.mobile?data.details.mobile:"";
                    $("#supplierMobile").val(mobile);
                    $("#supplierMobile").css("color", "#000");
                    $domObject.css({"color": "#000000"});
                    droplist.hide();
                },
                "onKeyboardSelect": function (event, index, data, hook) {
                    if (data && data.details) {
                        $domObject.val(data.details.name);
                        $("#supplierId").val(data.details.id);
                        var mobile = data.details.mobile?data.details.mobile:"";
                                            $("#supplierMobile").val(mobile);
                        $("#supplierMobile").css("color", "#000");
                    }
                }
            });
        }
        if (event && event.type == 'blur' && result && result.data) { // 下拉存在 并且 blur
            for (var i = 0; i < result.data.length; i++) {
                if ($domObject.val() == result.data[i].details.name) { // 名字精确匹配带出信息 否则查询信息为空
                    $("#supplierId").val(result.data[i].details.id);
                    var mobile = result.data[i].details.mobile ?result.data[i].details.mobile:"";
                    $("#supplierMobile").val(mobile).css("color", "#000");
                    $domObject.val(result.data[i].details.name).css({"color": "#000000"});
                    break;
                }
            }
        }
    });
}

function syncGetSupplierSuggestion(domObject, event) {
    var keycode = event?event.which || event.keyCode : null;
    var $domObject = $(domObject);
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    searchWord = $.trim(searchWord).toUpperCase();
    if (searchWord == "供应商名") {
        searchWord = "";
    }
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        searchWord:searchWord.toUpperCase(),
        uuid:droplist.getUUID()
    };
    ajaxData["customerOrSupplier"] = "supplier";
    ajaxData["titles"] = "name,mobile";
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
    APP_BCGOGO.Net.syncAjax({
        url: ajaxUrl,
        dataType: 'json',
        data: ajaxData,
        success: function (result) {
            if (!G.isEmpty(result.data[0])) {
                G.completer({
                        'domObject': domObject,
                        'keycode': keycode,
                        'title': result.data[0].details.name}
                );
            }
            if (event && event.type == 'blur' && result && result.data) { // 下拉存在 并且 blur
                for (var i = 0; i < result.data.length; i++) {
                    if ($domObject.val() == result.data[i].details.name) { // 名字精确匹配带出信息 否则查询信息为空
                        $("#supplierId").val(result.data[i].details.id);
                        var mobile = result.data[i].details.mobile ?result.data[i].details.mobile:"";
                        $("#supplierMobile").val(mobile).css("color", "#000");
                        $domObject.val(result.data[i].details.name).css({"color": "#000000"});
                        break;
                    }
                }
            }else{
                droplist.show({
                    "selector": $domObject,
                    "data": result,
                    "onSelect": function (event, index, data) {
                        $domObject.val(data.details.name);
                        $("#supplierId").val(data.details.id);
                        var mobile = data.details.mobile?data.details.mobile:"";
                        $("#supplierMobile").val(mobile);
                        $("#supplierMobile").css("color", "#000");
                        $domObject.css({"color": "#000000"});
                        droplist.hide();
                    },
                    "onKeyboardSelect": function (event, index, data, hook) {
                        if (data && data.details) {
                            $domObject.val(data.details.name);
                            $("#supplierId").val(data.details.id);
                            var mobile = data.details.mobile?data.details.mobile:"";
                            $("#supplierMobile").val(mobile);
                            $("#supplierMobile").css("color", "#000");
                        }
                    }
                });
            }
        }
    });
}

