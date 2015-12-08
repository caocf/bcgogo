/**
 * 客户对账页面js
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-9
 * Time: 下午1:22
 * To change this template use File | Settings | File Templates.
 */
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

$(document).ready(function () {

    $(document).click(function (e) {
        var e = e || event;
        var target = e.srcElement || e.target;
        if (!target || !target.id) {
            $("div_brand").hide();
        }
    });
    $("#startDateBill,#endDateBill")
        .datepicker({
            "numberOfMonths": 1,
            "changeYear": true,
            "changeMonth": true,
            "dateFormat": "yy-mm-dd",
            "yearRange": "c-100, c",
            "yearSuffix": "",
            "showButtonPanel": true
        }).blur(function () {
            var startDate = $("#startDateBill").val();
            var endDate = $("#endDateBill").val();
            if (startDate == "" || endDate == "") return;
            if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (startDate > endDate) {
                    $("#endDateBill").val(startDate);
                    $("#startDateBill").val(endDate);
                }
            }
        }).bind("click", function () {
            $(this).blur();
        }).change(function () {
            var startDate = $("#startDateBill").val();
            var endDate = $("#endDateBill").val();
            $(".good_his > .today_list").removeClass("hoverList");
            if (endDate == "" || startDate == "") {
                return;
            }
            if (APP_BCGOGO.Validator.stringIsZhCn(startDate) && APP_BCGOGO.Validator.stringIsZhCn(endDate)) {
                return;
            } else {
                if (startDate > endDate) {
                    $("#endDateBill").val(startDate);
                    $("#startDateBill").val(endDate);
                }
            }
        });
    $("a[name='date_select']").bind("click", function () {
        var text = $(this).text();
        var start = '', end = dateUtil.getToday(false, "end");
        var node = $('a[name=date_select].clicked');
        if (node.length && node[0] == this) {
            node.removeClass('clicked');
        }else{
            node.removeClass('clicked');
            $(this).addClass('clicked');
            if (text == '昨天') {
                start = dateUtil.getYesterday(false, "start");
                end = dateUtil.getYesterday(false, "end");
            } else if (text == '今天') {
                start = dateUtil.getToday(false, "start");
            } else if (text == '最近一周') {
                start = dateUtil.getOneWeekBefore('start')
            } else if (text == '最近一月') {
                start = dateUtil.getOneMonthBefore('start')
            } else if (text == '最近一年') {
                start = dateUtil.getOneYearBefore('start')
            }
        }
        $("#startDateBill").val(start);
        $("#endDateBill").val(end);
        $('#searchStatementAccount').click();
    });
    $("#endDateBill").val(dateUtil.getToday(false, "end"));

    $('#clearAway').click(function(){
        if($('#clearAway').attr('data-page-type') != 'customerOrSupplierInfo'){
            $('#customerOrSupplierId').val('');
        }
        $('a[name=date_select].clicked').removeClass('clicked');
        $("#startDateBill").val('');
        $("#endDateBill").val(dateUtil.getToday(false, "end"));
        $('#customerOrSupplierName').val('');
        $('#mobile').val('');
        $('#receiptNo').val('');
        $('#operator').val('');
        $('#customerOrSupplierIdSArray').val('');
    });

    $("#searchStatementAccount").bind("click", function () {
        if (!$("#endDateBill").val()) {
            nsDialog.jAlert("请输入查询截止日期！", null, function () {
                $('#endDateBill').focus();
            });
            return;
        }
        var param = $("#statementAccountOrderForm").serializeArray();
        var paramJson = {};
        $.each(param, function (index, val) {
            paramJson[val.name] = val.value;
        });
        APP_BCGOGO.Net.asyncAjax({
            url: "statementAccount.do?method=searchStatementAccountOrder",
            data: param,
            cache: false,
            dataType: "json",
            success: function (json) {
                initStatementAccountOrder(json);
                initPages(json, "dynamicalStatementAccountList", "statementAccount.do?method=searchStatementAccountOrder", '', "initStatementAccountOrder", '', '', paramJson, '');
            }
        });
    });

    if($("#searchStatementAccount").length > 0){
      $("#searchStatementAccount").click();
    }

    $("#currentStatementAccount").bind("click", function () {
        var customerOrSupplierId = $("#customerOrSupplierId").val();
        if (customerOrSupplierId != null && customerOrSupplierId != '') {
            redirectCurrentStatement(customerOrSupplierId, $("#orderType").val());
        }
    });

    $("#operator").bind("keyup", function (e) {
        customerBillOperatorSearch(this);
    }).bind("click", function (e) {
        customerBillOperatorSearch(this);
    });

});

function customerBillOperatorSearch(domObject) {
    var operator = $("#operator").val();
    var ajaxUrl = "statementAccount.do?method=getOperatorByCustomerOrSupplierId";
    var ajaxData = {
        operator: operator,
        customerOrSupplierIdStr: $("#customerOrSupplierId").val(),
        orderType: $("#orderType").val()
    };
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function (json) {
        ajaxOperator(domObject, json);
    });
}


//TODO 将包含客户名的JSON组装成下拉建议
function ajaxOperator(domObject, jsonStr) {
    domTitle = domObject.name;
    selectmoreCustomer = jsonStr.length;
    if (jsonStr.length <= 0)
        $("#div_brand").css({'display': 'none'});
    else {
        var offsetHeight = $(domObject).height();
        suggestionPosition(domObject, 0, offsetHeight - 0);
        $("#Scroller-Container_id").html("");
        for (var i = 0; i < jsonStr.length; i++) {
            var a = $("<a id='selectItem" + i + "'></a>");
            a.html(stringMethod.substring(jsonStr[i].salesMan, 10)).attr('title', jsonStr[i].salesMan);
            a.css({"text-overflow": "ellipsis", "overflow": "hidden", "white-space": "nowrap"});
            a.mouseover(function () {
                $("#Scroller-Container_id> a").removeAttr("class");
                $(this).attr("class", "hover");
            });
            a.click(function () {
                $(domObject).val(($(this).attr("title")));
                $("#div_brand").css({'display': 'none'});
                $("#operator").blur();
            });
            $("#Scroller-Container_id").append(a);
        }
    }
}

function ajaxOpesssrator(domObject, jsonStr) {
    if (jsonStr == null || jsonStr.length <= 0) {
        return;
    }


    var offsetHeight = $(domObject).height();
    suggestionPosition(domObject, 0, offsetHeight + 3);
    $("#Scroller-Container_id").html("");
    $("#div_brand").css({
        'overflow-x': "hidden",
        'overflow-y': "auto",
        'padding-left': 0 + 'px'
    });

    for (var i = 0; i < jsonStr.length; i++) {
        var a = $("<a id='selectItem" + i + "'></a>");
        a.html(stringMethod.substring(jsonStr[i].salesMan, 10)).attr('title', jsonStr[i].salesMan);
        a.mouseover(function () {
            $("#Scroller-Container_id > a").removeAttr("class");
            $(this).attr("class", "hover");
        });
        a.click(function () {
            var valflag = domObject.value != $(this).html() ? true : false;
            new clearItemUtil().clearByFlag(domObject, valflag);
            $(domObject).val($(this).attr("title"));
            $(domObject).blur();
            $("#div_brand").css({
                'display': 'none'
            });
        });
        $("#Scroller-Container_id").append(a).css({
            'overflow-x': "hidden",
            'overflow-y': "auto"
        });
    }
}

function initStatementAccountOrder(data) {
    $('#customerOrSupplierId').val() != '' ? $('#currentStatementAccount').show() : $('#currentStatementAccount').hide();
    $('#customerOrSupplierId').val() != '' ? $('#lastStatementOrder').parent().show() : $('#lastStatementOrder').parent().hide();
    $("#stateAccountOrderTable tr").not('tr:eq(0)').remove();
    if (data == null || data[0] == null || data[0].receivableList == null || data[0].receivableList == 0) {
        $("#pageTotalSpan").text("0");
        $("#lastStatementOrder").text("0");
        $("#noReceivableList").css("display", "block");
        return;
    } else {
        $("#noReceivableList").css("display", "none");
    }
    $("#pageTotalSpan").text(data[1].totalRows);
    $("#lastStatementOrder").text(data[0].lastStateAccount);
    $.each(data[0].receivableList, function (index, order) {
        var vestDateStr = (!order.vestDateStr ? "---" : order.vestDateStr);
        var receiptNo = (!order.receiptNo ? "--" : order.receiptNo);
        var total = (!order.orderTotalStr ? "0" : order.orderTotalStr);
        var settledAmount = (!order.settledAmountStr ? "0" : order.settledAmountStr);
        var discount = (!order.discount ? "0" : order.discount);
        var debt = (order.debt == null ? "0" : order.debt);
        var salesMan = (order.salesMan == null ? "--" : order.salesMan );
        var orderId = "'" + (!order.idStr ? "" : order.idStr) + "'";
        var tr = "<tr>";
        tr += "<td style='padding-left:10px;'>" + (index + 1) + "</td>";
        tr += "<td title='" + vestDateStr + "'>" + vestDateStr + "</td>";
        tr += "<td title='" + order.orderTypeStr + "'>" + order.orderTypeStr + "</td>";
        tr += "<td title='" + receiptNo + "'>" + '<a href ="#" style="color:#0094FF" onclick="openStatementOrder(' + orderId + ')">' + receiptNo + "</a>" + "</td> ";
        tr += "<td title='" + order.customerOrSupplier + "'><a href='javascript:openCustomerOrSupplierDetailed(\""+order.customerOrSupplierIdStr+"\",\"" + order.orderType + "\")' style='color:#0094FF;'>" + order.customerOrSupplier + "</a></td>";
        tr += "<td title='" + total + "'>" + total + "</td>";
        tr += "<td title='" + settledAmount + "'>" + settledAmount + "</td>";
        tr += "<td title='" + discount + "'>" + discount + "</td>";
        tr += "<td class='red_color' title='" + debt + "'>" + debt + "</td>";
        tr += "<td title='" + salesMan + "'>" + salesMan + "</td>";
        tr += "</tr>";
        $("#stateAccountOrderTable").append($(tr));
    });
}

function openCustomerOrSupplierDetailed(id, orderType) {
    if ($('#clearAway').attr('data-page-type') == 'customerOrSupplierInfo') {
        $('a[data-page-info=customerOrSupplierInfo]').click();
    } else {
        if (orderType == 'SUPPLIER_STATEMENT_ACCOUNT') {
            window.open('unitlink.do?method=supplier&supplierId=' + id);
        } else if (orderType == 'CUSTOMER_STATEMENT_ACCOUNT') {
            window.open('unitlink.do?method=customer&customerId=' + id);
        }
    }
}

function getCustomerOrSupplierData(val) {
    $('#customerOrSupplierIdSArray').val('');
    if (val == '' || val == null) {
        $('#customerOrSupplierId').val('');
    } else {
        APP_BCGOGO.Net.syncPost({
            url: 'searchInventoryIndex.do?method=getCustomerSupplierSuggestion',
            data: {
                searchWord: val.toUpperCase(),
                titles: "name,mobile",
                uuid: GLOBAL.Util.generateUUID()
            },
            dataType: "json",
            success: function (result) {
                if ((result.data.length > 0)) {
                    $('#customerOrSupplierId').val('');
                    var customerOrSupplierIds = [];
                    $.each(result.data, function () {
                        customerOrSupplierIds.push(this.details.id);
                    });
                    customerOrSupplierIds.length && $('#customerOrSupplierIdSArray').val(customerOrSupplierIds.join());
                } else {
                    $('#customerOrSupplierId').val('');
                }
            }
        });
    }
}

function getCustomerOrSupplierSuggestion(domObject, keycode) {
    var $domObject = $(domObject);
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    searchWord = $.trim(searchWord).toUpperCase();
    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxData = {
        searchWord: searchWord.toUpperCase(),
        titles: "name,contact,mobile",
        uuid: droplist.getUUID()
    };
    var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
    App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        if (!G.isEmpty(result.data[0])) {
            G.completer({
                    'domObject': domObject,
                    'keycode': keycode,
                    'title': result.data[0].details.name}
            );
        }
        result && result.data && $.each(result.data,function(){
            this.label = this.label.replace(/客户：|供应商：/g,'');
        });
        result && result.data && droplist.show({
            "selector": $domObject,
            "data": result,
            "onSelect": function (event, index, data) {
                $domObject.val(data.details.name);
                data.details.customerOrSupplier == 'customer' ? $('#orderType').val('CUSTOMER_STATEMENT_ACCOUNT'):$('#orderType').val('SUPPLIER_STATEMENT_ACCOUNT');
                $('#customerOrSupplierId').val(data.details.id);
                $('#mobile').val(data.details.mobile);
                $('#customerOrSupplierIdSArray').val('');
                droplist.hide();
                $('#searchStatementAccount').click();
            }
        });
    });
}

$(function () {
    $("#customerOrSupplierName").bind('click',function () {
        getCustomerOrSupplierSuggestion($(this));
    }).bind('input',function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getCustomerOrSupplierSuggestion(this, eventKeyCode);
            }
        }).bind('blur', function () {
            getCustomerOrSupplierData($(this).val());
        });
});