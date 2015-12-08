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

$(document).ready(function () {
    $("#resetSearchCondition").click(function () {
         $(".J_province").val("");
        $(".J_province").change();
        $("#customer_supplierInfoText").css("color","#BBBBBB");
        $("#mobile").css("color","#BBBBBB");
    });

    $("#customer_supplierInfoText").bind('change', function () {
        $("#mobile").val('手机号');
        $("#mobile").css({
            color: "#ADADAD"
        });
    });

    //绑定日期控件
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
        }).blur(function () {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            if (startDate == "" || endDate == "") return;
            if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (transformToDateStr(startDate) > transformToDateStr(endDate)) {
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
            if (App.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (transformToDateStr(startDate) > transformToDateStr(endDate)) {
                    $("#endDate").val(startDate);
                    $("#startDate").val(endDate);
                }
            }
        });
    //绑定事件radio
    $("[name='data_select']").bind("click", function () {
        var id = $(this).attr("id");
        var now = new Date();
        var year = now.getFullYear();
        if (id == "date_this_week") {
            $("#startDate").val(dateUtil.getWeekStartDate());
            $("#endDate").val(dateUtil.getWeekEndDate());
        } else if (id == "date_this_month") {
            $("#startDate").val(dateUtil.getMonthStartDate());
            $("#endDate").val(dateUtil.getMonthEndDate());
        } else if (id == "date_this_year") {
            $("#startDate").val(year + "-01-01");
            $("#endDate").val(year + "-12-31");
        } else if (id == "date_user_defined") {
            $("#startDate").val(year + "-01-01");
            $("#endDate").val(year + "-12-31");
        }
    });
    //fix bug 4495
    $("#date_this_month").click();

    $("#customerNameInput")
        .live('focus keyup', function (e) {
        customerOrSupplierSuggestion(this, "customer");
    })

    $("#supplierNameInput").live('focus keyup', function (e) {
        customerOrSupplierSuggestion(this, "supplier");
    });

    $("#vehicleNumberInput").live('focus', function (e) {
        vehicleNumberSuggestion(this);
    });

    $("#vehicleNumberInput").live('keyup', function (e) {
        var keycode = e.which || e.keyCode;
        vehicleNumberSuggestion(this, keycode);
    });

    /**
     *  隐藏下拉建议
     */
    $(document).bind("click", function (event) {
        if (event.target === document.lastChild) return;

        var selectorArray = [$("#div_customerOrSupplier"), "[id='customerNameInput']", "[id='vehicleNumberInput']", "[id='supplierNameInput']"];
        if ($(event.target).closest(selectorArray).length == 0) {
            $("#div_customerOrSupplier").hide();
        }
    });


    $("#receivableSearchBtn").bind("click", function () {
        var startDateStr = $("#startDate").val();
        var endDateStr = $("#endDate").val();
        if (startDateStr > endDateStr) {
            alert("开始时间应小于结束时间，请重新选择！");
            return;
        }

        $("#receivableSearchForm").ajaxSubmit({
            dataType: "json",
            type: "POST",
            success: function (json) {
                initReceivableTable(json);
                var paramJson = {startPageNo: 1, startDateStr: startDateStr, endDateStr: endDateStr, customerOrSupplierName: $("#customer_supplierInfoText").val(), vehicleNumber: $("#vehicleNumberInput").val()};
                initPages(json, "dynamical1", "arrears.do?method=getReceivableStatData", '', "initReceivableTable", '', '', paramJson, '');
            }
        });


    });

    $("#payableSearchBtn").bind("click", function () {
        var startDateStr = $("#startDate").val();
        var endDateStr = $("#endDate").val();
        if (startDateStr > endDateStr) {
            alert("开始时间应小于结束时间，请重新选择！");
            return;
        }

        $("#payableSearchForm").ajaxSubmit({
            dataType: "json",
            type: "POST",
            success: function (json) {
                initPayableTable(json);
                var paramJson = {startPageNo: 1, startDateStr: startDateStr, endDateStr: endDateStr, customerOrSupplierName: $("#customer_supplierInfoText").val(), vehicleNumber: $("#vehicleNumberInput").val()};
                initPages(json, "dynamical1", "arrears.do?method=getPayableStatData", '', "initPayableTable", '', '', paramJson, '');
            }
        });


    });

    $("#printBtn").bind("click", function () {
        var jsonObj = jsonStrMap.get("payable");
        if (jsonObj[jsonObj.length - 1].totalRows == 0) {
            nsDialog.jAlert("无数据，不能打印");
            return;
        }
        $("#startPageNo").val($("#currentPagedynamical1").val());
        var url = "arrears.do?method=getPayableToPrint";

        var data = $("#payableSearchForm").serialize();
        APP_BCGOGO.Net.syncAjax({
            url: url,
            data: data,
            type: 'POST',
            success: function (data) {
                if (!data) return;

                var printWin = window.open("", "", "width=1024,height=768");

                with (printWin.document) {
                    open("text/html", "replace");
                    write(data);
                    close();
                }
            }
        });
    });


    $("#printBtn2").bind("click", function () {
        var jsonObj = jsonStrMap.get("receivable");
        if (jsonObj[jsonObj.length - 1].totalRows == 0) {
            nsDialog.jAlert("无数据，不能打印");
            return;
        }
        $("#startPageNo").val($("#currentPagedynamical1").val());
        var url = "arrears.do?method=getReceivableToPrint";
        var data = $("#receivableSearchForm").serialize();
        APP_BCGOGO.Net.syncAjax({
            url: url,
            data: data,
            type: "POST",
            cache: false,
            success: function (data) {
                if (!data) return;

                var printWin = window.open("", "", "width=1024,height=768");

                with (printWin.document) {
                    open("text/html", "replace");
                    write(data);
                    close();
                }
            }
        });
    });
    $("#supplierTotalDebt,#customerTotalDebt,#totalArrears").click(function () {
        $("#receiver").val($(this).attr("id"));
        $("#payableSearchBtn").click();
        $("#receivableSearchBtn").click();
    });

    $(".recStatTable tr").live("mouseover mouseout",function(event){
        if(event.type == 'mouseover') {
            if(!$(this).hasClass("tab_title")) {
                var $nextTr = $(this).next();
                if($nextTr.length > 0) {
                    $(this).find('td').css("border-top-color","#FF4800");
                    $nextTr.find('td').css("border-top-color","#FF4800");
                } else {
                    $(this).find('td').css("border-top-color","#FF4800");
                    $(this).find('td').css({"border-bottom-color":"#FF4800"});
                }
            }
        } else {
            if(!$(this).hasClass("tab_title")) {
                var $nextTr = $(this).next();
                if($nextTr.length > 0) {
                    $(this).find('td').css("border-top-color","#BBBBBB");
                    $nextTr.find('td').css("border-top-color","#BBBBBB");
                } else {
                    $(this).find('td').css("border-top-color","#BBBBBB");
                    $(this).find('td').css({"border-bottom-color":"#BBBBBB","border-bottom-width":0});
                }
            }
        }

    });
});

function customerOrSupplierSuggestion(domObject, type) {
    var searchWord = $.trim(domObject.value);
    var searchField = $(domObject).attr("searchfield");
    var data = {searchWord: searchWord, searchField: searchField, customerOrSupplier: type};
    var url = "customer.do?method=getCustomerOrSupplierSuggestion";
    App.Net.syncPost({url: url, dataType: "json", data: data, success: function (jsonStr) {
        ajaxStyleSuggestList(domObject, jsonStr, "CustomerOrSupplier");
    }});

}

function vehicleNumberSuggestion(domObject, keycode) {
    var searchWord = $.trim(domObject.value);
    var data = {plateValue: searchWord};
    var url = "product.do?method=searchlicenseplate";
    App.Net.syncPost({url: url, dataType: "json", data: data, success: function (jsonStr) {
        if (!G.isEmpty(jsonStr[0])) {
            G.completer({
                    'domObject': domObject,
                    'keycode': keycode,
                    'title': jsonStr[0].licenceNo}
            );
        }
        ajaxStyleSuggestList(domObject, jsonStr, "vehicleNumber");
    }});

}


function ajaxStyleSuggestList(domObject, jsonStr, flag) {
    var offset = $(domObject).offset();
    var offsetHeight = $(domObject).height();
    var offsetWidth = $(domObject).width();
    domTitle = domObject.name;
    var point = {
        x: G.getX(domObject),
        y: G.getY(domObject)
    };
    $("#div_customerOrSupplier").css({
        'display': 'block', 'position': 'absolute',
        'left': point.x + 'px',
        'width': offsetWidth,
        'top': point.y + offsetHeight + 3 + 'px',
        'overflow-x': "hidden",
        'overflow-y': "auto"
    });
    $("#cusOrSupContainer").html("");
    var name;
    var customerOrSupplierId = "";
    for (var i = 0; i < jsonStr.length; i++) {
        if (flag == "CustomerOrSupplier") {
            name = jsonStr[i].suggestionEntry[0][1];
//      customerOrSupplierId=jsonStr[i].suggestionEntry[1][1];
        } else if (flag == "vehicleNumber") {
            name = jsonStr[i].carno;
        }
        var a_item = $("<a id='selectItem" + i + "'></a>");
        a_item.html(name.substring(0, 10)).attr('title', name);
//    a_item.attr("customerOrSupplierId",customerOrSupplierId);
        a_item.mouseover(function () {
            $("#cusOrSupContainer > a").removeAttr("class");
            $(this).attr("class", "hover");
            $(this).css("margin", "0");
        });
        a_item.click(function () {
            var valflag = domObject.value != $(this).html() ? true : false;
            new clearItemUtil().clearByFlag(domObject, valflag);
            $(domObject).val($(this).html());
            $(domObject).css("color", "#000000");
//      $("#customerOrSupplierId").val(a_item.attr("customerOrSupplierId"));
            $("#div_customerOrSupplier").css({'display': 'none'});
        });
        $("#cusOrSupContainer").append(a_item);
    }
}


function initReceivableTable(json) {
    jsonStrMap.put("receivable", json);
    $("#recStatTable tr:not(:first)").remove();
    jsonStr = json[0].receivables;
    if (null == jsonStr) {
        jsonStr = 0;
    }
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length; i++) {
            var orderIdStr = jsonStr[i].orderIdStr == null ? " " : jsonStr[i].orderIdStr;
            var consumeTime = jsonStr[i].vestDateStr == null ? " " : jsonStr[i].vestDateStr;
            var receiptNo = jsonStr[i].receiptNo == null ? " " : jsonStr[i].receiptNo;
            var customerName = jsonStr[i].customerOrSupplierName == null ? " " : jsonStr[i].customerOrSupplierName;
            var customerOrSupplierIdStr = jsonStr[i].customerOrSupplierIdStr == null ? " " : jsonStr[i].customerOrSupplierIdStr;
            var vehicleNo = jsonStr[i].vehicle == null ? " " : jsonStr[i].vehicle;
            var content = jsonStr[i].orderTypeValue == null ? " " : jsonStr[i].orderTypeValue;
            var totalAmount = jsonStr[i].amount == null ? " " : jsonStr[i].amount;
            var settledAmount = jsonStr[i].settled == null ? " " : jsonStr[i].settled;
            var debt = jsonStr[i].debt == null ? " " : jsonStr[i].debt;
            var temp = totalAmount - settledAmount - debt;

            var orderType = jsonStr[i].orderType == null ? " " : jsonStr[i].orderType;
            var totalCostPrice = jsonStr[i].totalCostPrice == null ? 0.0 : jsonStr[i].totalCostPrice;
            var afterMemberDiscountTotal = jsonStr[i].afterMemberDiscountTotal == null ? totalAmount : jsonStr[i].afterMemberDiscountTotal;

            var deduction = dataTransition.rounding(afterMemberDiscountTotal - settledAmount - debt, 2);
            totalAmount = dataTransition.rounding(afterMemberDiscountTotal, 2);
            settledAmount = dataTransition.rounding(settledAmount, 2);
            debt = dataTransition.rounding(debt, 2);
            var tr = '<tr class="table-row-original">';
            tr += '<td />';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';

            if (orderType == "SUPPLIER_STATEMENT_ACCOUNT" || orderType == "CUSTOMER_STATEMENT_ACCOUNT") {
                tr += '<td>' + '<a class="blue_col" href ="#" onclick="openStatementOrder(\'' + orderIdStr + '\')">' + receiptNo + '</a> ' + '</td> ';
            } else {
                tr += '<td>' + '<a class="blue_col" href ="#" onclick="openTxnOrder(\'' + orderIdStr + '\',\'' + orderType + '\')">' + receiptNo + '</a> ' + '</td> ';

            }

            tr += '<td>' + consumeTime + '</td>';
            tr += '<td><span class="limit-span">' + customerName + '</span></td>';
            if (APP_BCGOGO.Permission.Version.VehicleConstruction) {
                tr += '<td>' + vehicleNo + '</td>';
            }

            tr += '<td>' + content + '</td>';
            tr += '<td title=\'' + afterMemberDiscountTotal + '\'>' + afterMemberDiscountTotal + '</td>';
            tr += '<td title=\'' + totalCostPrice + '\'>' + totalCostPrice + '</td>';
            tr += '<td title=\'' + settledAmount + '\'>' + settledAmount + '</td>';
            tr += '<td title=\'' + deduction + '\'>' + deduction + '</td>';
            tr += '<td class="owedTd" title=\'' + debt + '\'>' + debt + '</td>';

            var orderTypeStr = "CUSTOMER_STATEMENT_ACCOUNT";
            if (orderType == "RETURN" || orderType == "SUPPLIER_STATEMENT_ACCOUNT") {
                orderTypeStr = "SUPPLIER_STATEMENT_ACCOUNT";
            }
            tr += '<td>' + '<a class="blue_col" href ="#" onclick="redirectCurrentStatement(\'' + customerOrSupplierIdStr + '\',\'' + orderTypeStr + '\')">' + "对账" + '</a> ' + '</td> ';

            tr += '<td />';
            tr += '</tr >';
            $("#recStatTable").append($(tr));
            tableUtil.limitSpanWidth($(".customer", "#recStatTable"), 10);
        }
        var tr = '<tr class="table-row-original"><td colspan="13" style="color: #4D4D4D;text-align:left;"><label style="font-weight:bold; margin-left:15px;">本页小计:挂账</label><span id="pageTotal">0.0</span>元</td></tr>';
        $("#recStatTable").append($(tr));
    }
    $("#totalArrears").html(dataTransition.rounding(json[0].totalArrears, 2));
    $("#pageTotal").html(getPageTotal());
    $("#supplierTotalDebt").html(dataTransition.rounding(json[0].supplierTotalDebt, 2));
    $("#customerTotalDebt").html(dataTransition.rounding(json[0].customerTotalDebt, 2));
    $("#totalCostPriceStat").html(dataTransition.rounding(json[0].totalCostPriceStat, 2));
    $("#receiver").val('');
    recOrPayStatStyleAdjust();
}


function initPayableTable(json) {
    jsonStrMap.put("payable", json);
    $("#totalCreditAmount").text(0);
    $("#checkAll").removeAttr("checked");
    $("#payStatTable tr:not(:first)").remove();
    jsonStr = json[0].payables;
    if (null == jsonStr) {
        jsonStr = 0;
    }
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length; i++) {
            var receiptNo = jsonStr[i].receiptNo == null ? " " : jsonStr[i].receiptNo;
            var supplierName = jsonStr[i].customerOrSupplierName == null ? " " : jsonStr[i].customerOrSupplierName;
            var purchaseInventoryIdStr = jsonStr[i].orderIdStr == null ? " " : jsonStr[i].orderIdStr;
            var payTimeStr = jsonStr[i].vestDateStr == null ? "" : jsonStr[i].vestDateStr;
            var orderTypeValue = jsonStr[i].orderTypeValue == null ? "" : jsonStr[i].orderTypeValue;
            var orderType = jsonStr[i].orderType == null ? "" : jsonStr[i].orderType;
            var amount = jsonStr[i].amount == null ? " " : jsonStr[i].amount;
            var paidAmount = jsonStr[i].settled == null ? "" : jsonStr[i].settled;
            var creditAmount = jsonStr[i].debt == null ? "" : jsonStr[i].debt;
            var deduction = jsonStr[i].discount == null ? "" : jsonStr[i].discount;
            paidAmount = dataTransition.rounding(paidAmount, 2);
            amount = dataTransition.rounding(amount, 2);
            creditAmount = dataTransition.rounding(creditAmount, 2);
            deduction = dataTransition.rounding(deduction, 2);
            var totalCostPrice = jsonStr[i].totalCostPrice == null ? "" : jsonStr[i].totalCostPrice;
            var orderId = "'" + purchaseInventoryIdStr + "'";
            var tr = '<tr class="table-row-original">';
            tr += '<td />';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';

            if (orderType == "SUPPLIER_STATEMENT_ACCOUNT" || orderType == "CUSTOMER_STATEMENT_ACCOUNT") {
                tr += '<td>' + '<a class="blue_col" href ="#" onclick="openStatementOrder(' + orderId + ')">' + receiptNo + '</a> ' + '</td> ';
            } else if (orderType == "INVENTORY") {
                tr += '<td>' + '<a class="blue_col" href ="#" onclick="openInventory(' + orderId + ')">' + receiptNo + '</a> ' + '</td> ';

            } else if (orderType == "SALE_RETURN") {
                tr += '<td>' + '<a href ="#" onclick="openSalesReturn(' + orderId + ')">' + receiptNo + '</a> ' + '</td> ';
            }
            tr += '<td>' + payTimeStr + '</td>';
            tr += '<td><span class="limit-span">' + supplierName + '</span></td>';
            tr += '<td >' + orderTypeValue + '</td>';
            tr += '<td title=\'' + amount + '\'>' + amount + '</td>';
            tr += '<td title=\'' + paidAmount + '\'>' + paidAmount + '</td>';
            tr += '<td title=\'' + deduction + '\'>' + deduction + '</td>';
            tr += '<td class="txt_left owedTd" title=\'' + creditAmount + '\'>' + creditAmount + '</td>';
            var orderTypeStr = "SUPPLIER_STATEMENT_ACCOUNT";
            var customerOrSupplierIdStr = jsonStr[i].customerOrSupplierIdStr == null ? " " : jsonStr[i].customerOrSupplierIdStr;
            if (orderType == "SALE_RETURN" || orderType == "CUSTOMER_STATEMENT_ACCOUNT") {
                orderTypeStr = "CUSTOMER_STATEMENT_ACCOUNT";
            }
            tr += '<td>' + '<a class="blue_col" href ="#" onclick="redirectCurrentStatement(\'' + customerOrSupplierIdStr + '\',\'' + orderTypeStr + '\')">' + "对账" + '</a> ' + '</td> ';
            tr += '<td />';
            tr += '</tr >';
            $("#payStatTable").append($(tr));
            tableUtil.limitSpanWidth($(".supplier", "#payStatTable"), 10);
        }
        var tr = '<tr class="table-row-original"><td colspan="12" style="color: #4D4D4D;text-align:left;"><label style="font-weight:bold; margin-left:15px;">本页小计:挂账</label><span id="pageTotal">0.0</span>元</td></tr>';
        $("#payStatTable").append($(tr));
    }
    $("#pageTotal").html(getPageTotal());
    $("#totalArrears").html(dataTransition.rounding(json[0].totalArrears, 2));
    $("#supplierTotalDebt").html(dataTransition.rounding(json[0].supplierTotalDebt, 2));
    $("#customerTotalDebt").html(dataTransition.rounding(json[0].customerTotalDebt, 2));
    $("#totalCostPriceStat").html(dataTransition.rounding(json[0].totalCostPriceStat, 2));
    $("#receiver").val('');
    recOrPayStatStyleAdjust();
}

function recOrPayStatStyleAdjust() {
    tableUtil.tableStyle('.recStatTable', '.tab_title');
}

function getPageTotal() {
    var page_total = 0;
    $(".owedTd").each(function () {
        page_total += parseFloat($(this).text());
    });
    return dataTransition.rounding(page_total, 2);
}

function openTxnOrder(orderId, orderType) {
    if (orderType == "REPAIR") {
        window.open('txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderId);
    } else if (orderType == "SALE") {
        window.open('sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=' + orderId);
    } else if (orderType == "WASH_BEAUTY") {
        window.open('washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=' + orderId);
    } else if (orderType == "RETURN") {
        window.open('goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=' + orderId);
    }
}

function openInventory(idStr) {
    window.open('storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=' + idStr + '&type=txn');
}

//时间名词转换成标准时间
function transformToDateStr(str) {
    if (!APP_BCGOGO.Validator.stringIsZhCn(str)) return str;
    var date = new Date();
    var day = date.getDate();  //getDay 是星期
    var mouth = date.getMonth() + 1;  //+1代表本月
    var year = date.getFullYear();
    var time = null;
    if (str == "今天") {
        time = year + "-" + addZero(mouth) + "-" + addZero(day);
    } else if (str == "昨天") {
        time = year + "-" + addZero(mouth) + "-" + addZero((day - 1));
    } else if (str == "上月第一天") {
        time = year + "-" + addZero((mouth - 1)) + "-01";
    } else if (str == "本月第一天") {
        time = year + "-" + addZero(mouth) + "-01";
    } else if (str == "上月最后一天") {
        var lastMonthLastDay = new Date(new Date(year, mouth, 1).getTime() - 1000 * 60 * 60 * 24);
        time = lastMonthLastDay.getFullYear() + "-" + addZero(lastMonthLastDay.getMonth()) + "-" + addZero(lastMonthLastDay.getDate());
    } else if (str == "今年第一天") {
        time = year + "-01-01";
    }
    return time;
}

