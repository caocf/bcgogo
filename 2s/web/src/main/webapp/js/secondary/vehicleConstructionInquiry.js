$(function () {
    $('.dateSelect').click(function () {
        var text = $(this).text();
        var start = '', end = dateUtil.getToday(false, "end");
        var node = $('a.dateSelect.clicked');
        if (node.length && node[0] == this) {
            node.removeClass('clicked');
        } else {
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
        $("#startDateStr").val(start);
        $("#endDateStr").val(end);
        $('#inquiry').click();
    });

    $('input[name=statusStr]').change(function () {
        $('#inquiry').click();
    });

    var droplistLiteCallback = function (event) {
        var _data = "customer.do?method=getCustomerOrSupplierSuggestion&customerOrSupplier=customer&requestType=AJAX&searchIncludeLicenseNoAndMemberNo=true";
        droplistLite.show({
            event: event,
            id: "id",
            keyword: "searchWord",
            data: _data,
            name: function (_obj) {
                return "客户：" + "{name} {contact} {mobile}";
            },
            afterSelected: function (event, index, data, hook) {
                $(hook).val(data.name);
                $('#customerId').val(data.id);
                $('#inquiry').click();
            }
        });
    }

    $('#customerInfo').bind('input',function () {
        $('#customerId').val('');
    }).bind('input', droplistLiteCallback).bind('click', droplistLiteCallback);

    $('#inquiry').click(function () {
        if (!$("#endDateStr").val()) {
            nsDialog.jAlert("请输入查询截止日期！", null, function () {
                $('#endDate').focus();
            });
            return;
        }
        var param = $("#myForm").serializeArray();
        var paramJson = {};
        $.each(param, function (index, val) {
            paramJson[val.name] = val.value;
        });
        APP_BCGOGO.Net.asyncAjax({
            url: "repairOrderSecondary.do?method=queryRepairOrderSecondary",
            data: $('#myForm').serialize(),
            cache: false,
            dataType: "json",
            success: function (json) {
                showList(json);
                initPages(json, "repairOrderSecondaryList", "repairOrderSecondary.do?method=queryRepairOrderSecondary", '', "showList", '', '', paramJson, '');
            }
        });
    });

    $('#reset').click(function () {
        $('#myForm')[0].reset();
        $('.dateSelect.clicked').removeClass('clicked');
        $('#customerId').val('');
        $('#inquiry').click();
        return false;
    });

    window.showList = function (response) {
        var table = $('.body .bottom table')[0];
        while (table.rows.length > 1) {
            table.deleteRow(1);
        }
        if (response.length == 2) {
            $('#noSecondaryList').addClass('hide');
            var data = response[0];
            $('#count').text(data.count);
            $('#total').text(data.total);
            $('#income').text(data.income);
            $('#debt').text(data.debt);
            $('#discount').text(data.discount);
            data.repairOrderSecondaryDTOList && $.each(data.repairOrderSecondaryDTOList, function (i, item) {
                var row = table.insertRow(table.rows.length);
                $(row).addClass('line');
                var n = 0;
                $(row.insertCell(n++)).text(i + 1);
                $(row.insertCell(n++)).append($('<a href="#">' + item.receipt + '</a>').click(function () {
                    window.open('repairOrderSecondary.do?method=showRepairOrderSecondary&repairOrderSecondaryId=' + item.idStr);
                }));
                var startDate = /\d{4}-\d{2}-\d{2}/.exec(item.startDateStr)[0];
                $(row.insertCell(n++)).text(item.startDateStr);
                $(row.insertCell(n++)).text(nullToEmpty(item.customerName) + ' ' + nullToEmpty(item.customerContact) + ' ' + nullToEmpty(item.customerMobile)).css('textAlign', 'left');
                $(row.insertCell(n++)).text(nullToEmpty(item.vehicleLicense));
                $(row.insertCell(n++)).text(nullToEmpty(item.vehicleContact) + ' ' + nullToEmpty(item.vehicleMobile)).css('textAlign', 'left');
                $(row.insertCell(n++)).text(item.total);
                $(row.insertCell(n++)).text(item.settledAmount);
                $(row.insertCell(n++)).text(item.accountDebtAmount);
                $(row.insertCell(n++)).text(item.accountDiscount);
                $(row.insertCell(n++)).text(item.statusStr).css('textAlign', 'left');
                $(row.insertCell(n++)).append($('<a href="#">查看原单</a>').click(function () {
                    window.open('txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + item.repairOrderIdStr);
                }));
            });
        } else {
            $('#noSecondaryList').removeClass('hide');
            $('#count').text('0');
            $('#total').text('0');
            $('#income').text('0');
            $('#debt').text('0');
            $('#discount').text('0');
        }
    }
});

$(function () {
    $('#startDateStr, #endDateStr').datepicker({
        "numberOfMonths": 1,
        "changeYear": true,
        "changeMonth": true,
        "dateFormat": "yy-mm-dd",
        "yearRange": "c-100, c",
        "yearSuffix": "",
        "showButtonPanel": true
    }).change(function () {
            $('#inquiry').click();
        });

    $('.body a[href=#]').attr('href', 'javascript:void(0)');
    $('input[placeholder]').bind('focus',function () {
        var $this = this;
        setTimeout(function () {
            var placeholder = $($this).attr('placeholder');
            placeholder && placeholder != '' && $($this).attr('data-placeholder', placeholder);
            $($this).attr('placeholder', '');
        }, 100);
    }).bind('blur', function () {
            var $this = this;
            setTimeout(function () {
                var placeholder = $($this).attr('data-placeholder');
                placeholder && placeholder != '' && $($this).attr('placeholder', placeholder);
            }, 100);
        });
    $('#inquiry').click();
})

function nullToEmpty(val) {
    return val == null ? '' : val;
}