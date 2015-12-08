$(function() {

    carInvoice.initEvent();
    //快速结算
    $(".J_auto_account_btn").live("click", function() {
        var recordId = $(this).attr("recordId");
        if (!recordId) {
            return;
        }
        nsDialog.jConfirm("是否确认结算？","提醒", function (returnVal) {
            if (returnVal) {
                App.Net.asyncPost({
                    url: "repair.do?method=autoAccountRepairOrder",
                    data: {
                        consumingRecordId:recordId
                    },
                    cache: false,
                    dataType: "json",
                    success: function (result) {
                        if (result) {
                            if (!result.success) {
                                nsDialog.jAlert(result.msg);
                            }
                            nsDialog.jAlert("本单现金券已经结算完成");
                        }
                    },
                    error: function () {
                        nsDialog.jAlert("代金券记录异常！");
                    }
                });
            }
        });

    });

});

$(function() {
    var isFirstPage;
    var isLastPage;
    var hasNextPage;
    var hasPrevPage;
    var startPageNo;
    var maxRows;
//alert($("#menu_idvoucher_list_page1").text())
//alert(7)
    var data = {startPageNo:1,maxRows:5};
    App.Net.asyncPost({
        url: "customer.do?method=getCouponList",
        data: data,
        cache: false,
        dataType: "json",
        success: function (response) {
            //alert(response.consumingRecordDTOs[0].id+" "+response.consumingRecordDTOs[1].id+" "+response.consumingRecordDTOs[2].id);
            we(response);
            initPage(response, "voucher_list_page", "customer.do?method=getCouponList", '', "we", '', '', data, '');
            //we(response);
            //alert($(".last").text())
        },
        error: function () {
            nsDialog.jAlert("代金券记录异常！");
        }
    });
});

function we(data) {
    //alert($("#menu_idinvoice_list_page4").text())
    //alert($("#menu_idvoucher_list_page3").text())
//console.log($("#menu_idvoucher_list_page3").text())
    $(".num_1").text(data.pager.totalRows);
    var num = data.pager.pageSize;
    //alert(num)
    $("#voucher_list").empty();
    //for(var i = 0; i < num; i++){
    //
    //    insert();
    //}
    $.each(data.consumingRecordDTOs, function(index, dto) {
        insert();
    });
    $(".voucher_wrap").each(function(index, elem) {
        $(elem).find(".licenceNo").text(data.consumingRecordDTOs[index].vehicleNo);
        $(elem).find(".customerName").text(data.consumingRecordDTOs[index].customerName);
        $(elem).find(".vehicleContact").text(data.consumingRecordDTOs[index].userName);
        $(elem).find(".statusStr").text(data.consumingRecordDTOs[index].orderStatusStr);
        $(elem).find(".startDateStr").text(UnixToDate(data.consumingRecordDTOs[index].time));
        $(elem).find(".voucherMoney").text(data.consumingRecordDTOs[index].coupon + "元");
        $(elem).find(".J_auto_account_btn").attr("recordId", data.consumingRecordDTOs[index].idStr);
        $(elem).find(".toRepairOrder").click(function() {
            window.open('txn.do?method=getBlankRepairOrder&consumingRecordId=' + data.consumingRecordDTOs[index].idStr);
        });
        $(elem).find(".toWashBeautyOrder").click(function() {
            window.open('washBeauty.do?method=getBlankWashBeautyOrder&consumingRecordId=' + data.consumingRecordDTOs[index].idStr);
        });
        $(elem).find(".blankOrderRepeal").click(function(){
            if (confirm("友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！")) {
                window.location.href='couponConsume.do?method=blankOrderRepeal&consumingRecordId=' + data.consumingRecordDTOs[index].idStr;
            }
        });
    })

}

function insert() {


    $("#voucher_list").append(
        '<div>'+
        '<div class="voucher_wrap">'+
        '<div class="voucherTitle">'+
        //'<strong>施工单号：</strong><span data-mark="receiptNo" class="blue_color">--</span>'+
        '<strong>车牌号：</strong><span data-mark="licenceNo" class="blue_color licenceNo">--</span>&nbsp;'+
        '<strong>客户信息：</strong><span data-mark="customerInfo" class="blue_color"><span data-mark="customerName" class="customerName">--</span> <span data-mark="mobile" class="customerMobile"></span></span>&nbsp;'+
        '<strong>车主信息：</strong><span data-mark="vehicleContact" class="blue_color vehicleContact">--</span> <span data-mark="vehicleMobile" class="blue_color"></span>&nbsp;'+
        '<strong>状态：</strong><span data-mark="statusStr" class="statusStr">--</span>&nbsp;'+
        '<strong>进厂时间：</strong><span data-mark="startDateStr" class="startDateStr">--</span>'+
        '</div>'+
        '<div class="voucherContent">'+
        '<div >&nbsp;</div>'+
        '<table class="voucherTable" cellpadding="0" cellspacing="0">'+
        '<tr class="head">'+
        '<td>商品编号</td>'+
        '<td>品名</td>'+
        '<td>品牌</td>'+
        '<td>规格</td>'+
        '<td>型号</td>'+
        '<td>车辆品牌</td>'+
        '<td>车型</td>'+
        '<td>代金券金额</td>'+
        '<td>操作</td>'+
        '</tr>'+
        '<tr class="operate">'+
        '<td></td>'+
        '<td></td>'+
        '<td></td>'+
        '<td></td>'+
        '<td></td>'+
        '<td></td>'+
        '<td></td>'+
        '<td class="voucherMoney">--元</td>'+
        '<td>'+
        '<span title="施工销售" class="toRepairOrder">施工销售</span><br/>'+
        '<span title="洗车美容" class="toWashBeautyOrder">洗车美容</span><br/>'+
        '<span title="作废" class="blankOrderRepeal">作废</span>'+
        '</td>'+
        '</tr>'+
        '<tr class="balance">'+
        '<td></td>'+
        '<td></td>'+
        '<td></td>'+
        '<td></td>'+
        '<td></td>'+
        '<td></td>'+
        '<td></td>'+
        '<td></td>'+
            '<td><input class="J_auto_account_btn" type="button" value="快速结算" title="快速结算"></td>' +
        '</tr>'+
        '</table>'+
        '<div >&nbsp;</div>'+
        '</div>'+
        '</div>'+
        '</div>'
    );


}

//Unix时间戳转换成具体日期
function UnixToDate(timeStamp) {

    var stamp = " " + timeStamp;
    var time = new Date(parseInt(stamp));
    var year = time.getFullYear();
    var month = time.getMonth() + 1;
    var date = time.getDate();
    var hour = time.getHours();
    var minute = time.getMinutes();

    if (month <= 9) {
        month = "0" + month;
    }
    if (date <= 9) {
        date = "0" + date;
    }
    if (hour <= 9) {
        hour = "0" + hour;
    }
    if (minute <= 9) {
        minute = "0" + minute;
    }

    return year + "-" + month + "-" + date + " " + hour + ":" + minute;
}

var carInvoice = {};

carInvoice.eventType = {
    'LACK': '缺料待修',
    'INCOMING': '来料待修',
    'WAIT_OUT_STORAGE': '待领料',
    'OUT_STORAGE': '领料待修',
    'PENDING': '待交付',
    'DEBT': '还款',
    'FINISH': '完工'
};


carInvoice.initEvent = function () {
    $('.car_a[data-condition-order]').add('.car_a[data-condition-order-item]').bind('click', function () {
        var orderStatus = $(this).attr('data-condition-order') == null ? 'ALL' : $(this).attr('data-condition-order');
        var repairRemindEventTypes = $(this).attr('data-condition-item') == null ? '' : $(this).attr('data-condition-item');
        var data = {repairRemindEventTypes: repairRemindEventTypes, orderStatus: orderStatus,startPageNo: 1, maxRows: 5};
        App.Net.asyncPost({
            url: "customer.do?method=getCarConstructionInvoiceList",
            data: data,
            cache: false,
            dataType: "json",
            success: function (response) {

                carInvoice.showList(response);
                initPage(response, "invoice_list_page", "customer.do?method=getCarConstructionInvoiceList", '', "showList", '', '', data, '');

            },
            error: function () {
                nsDialog.jAlert("车辆施工记录异常！");
            }
        });


    });

    $('div[data-event=yesterday]').click(function() {
        Number($('span', this).text()) > 0 && window.open('inquiryCenter.do?method=inquiryCenterIndex&date=yesterday&pageType=REPAIR_AND_WASH_BEAUTY');
    });
    $('div[data-event=today]').click(function() {
        Number($('span', this).text()) > 0 && window.open('inquiryCenter.do?method=inquiryCenterIndex&date=today&pageType=REPAIR_AND_WASH_BEAUTY');
    });
    $('div[data-event=add]').click(function() {
        Number($('span', this).text()) > 0 && window.open('customer.do?method=customerdata&todayAdd=true');
    });

    $('.car_status_a').click(function () {
        $('.car_status_a', $(this).parent()).removeClass('hover')
        $(this).addClass('hover');
    });

    $('.car_a').mouseover(
        function () {
            $('span', this).removeClass('blue_color');
        }).mouseout(function () {
            $('span', this).addClass('blue_color');
        });
};


carInvoice.initItemEvent = function (target) {
    var response = target.data('response');

    $('span[data-mark=receiptNo]', target).click(function () {
        window.open('txn.do?method=getRepairOrder&repairOrderId=' + response.idStr);
    });
    $('span[data-mark=customerInfo]', target).click(function() {
        window.open('unitlink.do?method=customer&customerId=' + response.customerIdStr);
    });
    $('span[data-event=warehousing]', target).click(function () {
        window.open('storage.do?method=getProducts&repairOrderId=' + response.idStr);
    });
    $('span[data-event=picking]', target).click(function () {
        window.open('pick.do?method=showRepairPicking&repairPickingId=' + response.repairPickingIdStr);
    });
    $('span[data-event=construction]', target).add('span[data-event=lookOver]', target).click(function () {
        window.open('txn.do?method=getRepairOrderByVehicleNumber&vehicleNumber=' + response.licenceNo);
    });
    $('a[data-mark=expand]', target).click(function () {
        carInvoice.mergerColumn(target);
        $('tr.defaultHide', target).show();
        $(this).hide();
        $('a[data-mark=collapse]', target).show();
    });
    $('a[data-mark=collapse]', target).click(function () {
        $('tr.defaultHide', target).hide();
        $(this).hide();
        $('a[data-mark=expand]', target).show();
        carInvoice.splitColumn(target);
    });
}

carInvoice.handleVal = function (val) {
    return G.Lang.isNotEmpty(val) ? val : '--';
};

carInvoice.TemplateTr = '<tr><td>&#160;</td><td>&#160;</td><td>&#160;</td><td>&#160;</td><td>&#160;</td><td>&#160;</td><td>&#160;</td><td>&#160;</td><td>&#160;</td></tr>';

carInvoice.mergerColumn = function(target, isInit) {
    var anchor = null;
    var list = $('tr', target).not('tr:last');
    isInit && (list = list.not('.defaultHide'));
    list.length > 1 && list.each(function (i, tr) {
        if (anchor && $('td', anchor).length == 9 && $('td', anchor).length == 9 && $('td:eq(7)', tr).text() == $('td:eq(7)', anchor).text()) {
            var rowSpan = $('td:eq(7)', anchor).attr('rowSpan') || 1;
            rowSpan++;
            $('td:eq(8)', anchor).attr('rowSpan', rowSpan);
            $('td:eq(8)', tr).remove();
            $('td:eq(7)', anchor).attr('rowSpan', rowSpan);
            $('td:eq(7)', tr).remove();
        } else {
            anchor = tr;
        }
    });
}

carInvoice.splitColumn = function (target) {
    $('tr.defaultHide', target).each(function (i, item) {
        if ($('td', item).length == 7) {
            var prev = $(item).prev();
            $('td[rowSpan]', prev).removeAttr('rowSpan');
            $('td:last', item).after($('td:eq(8)', prev).clone(true)).after($('td:eq(7)', prev).clone())
        }
    });

}

carInvoice.showOrderItem = function (target, list) {
    if (list && list.length) {
        var node = $('tr', target).last();
        $('span[data-mark=itemCount]', target).text(list.length);
        var waitHandle = 0;
        $.each(list, function (i, item) {
            var tr = $(carInvoice.TemplateTr);
            node.before(tr);
            var event = item.repairRemindEventDTO;
            var remindStatus = event ? carInvoice.eventType[event.eventType] : '--';
            var operate = event ? '<span data-event="construction" class="car_a blue_color">施工</span>' : '<span data-event="lookOver" class="car_a blue_color">查看</span>';
            if (event && event.eventType == 'LACK') {
                waitHandle++
                operate = '<span data-event="warehousing" class="car_a blue_color">入库</span>'
            } else if (event && event.eventType == 'WAIT_OUT_STORAGE') {
                waitHandle++
                operate = '<span data-event="picking" class="car_a blue_color">领料</span>'
            } else if (event) {
                waitHandle++
            } else {
                i != 0 && tr.addClass('defaultHide car_hide');
            }

            $('td:eq(0)', tr).text(item.commodityCode);                                     //商品编号
            $('td:eq(1)', tr).text(item.productName);                                       //品名
            $('td:eq(2)', tr).text(item.brand);                                             //品牌
            $('td:eq(3)', tr).text(item.spec);                                              //规格
            $('td:eq(4)', tr).text(item.model);                                             //型号
            $('td:eq(5)', tr).text(item.vehicleBrand);                                      //车辆品牌
            $('td:eq(6)', tr).text(item.vehicleModel);                                      //车型
            $('td:eq(7)', tr).text(remindStatus);                                           //提醒状态
            $('td:eq(8)', tr).html(operate);                                                //操作
        });
        (list.length == 1 || $('.defaultHide', target).size() == 0) && $('a[data-mark=expand]', target).hide();
        $('span[data-mark=waitHandle]', target).text(waitHandle);
        carInvoice.mergerColumn(target, true);
    } else {
        $('table', target).hide();
        $('div[data-mark=materialDetails]', target).text('无施工材料')
    }
    carInvoice.initItemEvent(target);
}
carInvoice.setHeadVal = function (response) {
    $('#total').text(response.total);
    $('#dispatchTotal').text(response.dispatchTotal);
    $('#dispatchTotal2').text(response.dispatchTotal);
    $('#lackTotal').text(response.lackTotal);
    $('#incomingTotal').text(response.incomingTotal);
    $('#waitOutStorageTotal').text(response.waitOutStorageTotal);
    $('#outStorageTotal').text(response.outStorageTotal);
    $('#normalTotal').text(response.normalTotal);
    $('#pendingTotal').text(response.pendingTotal);
    $('#pendingTotal2').text(response.pendingTotal);

    $('#dispatchFee').text(response.dispatchFee);
    $('#pendingFee').text(response.pendingFee);
}

carInvoice.showList = function (response) {
    var repairOrderList = response.repairOrderDTOList;
    var root = $('#invoice_list').empty();
    repairOrderList && $.each(repairOrderList, function (i, item) {
        var template = $('#template').clone().removeAttr('id').data('response', item);
        root.append(template);
        var mark = ['receiptNo', 'licenceNo', 'customerName', 'mobile', 'vehicleContact', 'vehicleMobile', 'statusStr', 'endDateStr', 'total', 'startDateStr'];
        $.each(mark, function (i, n) {
            $('span[data-mark=' + n + ']', template).text(carInvoice.handleVal(item[n]));
        });
        var service = [];
        item.serviceDTOs && $.each(item.serviceDTOs, function (i, n) {
            service.push(carInvoice.handleVal(n.service));
        });
        $('span[data-mark=service]', template).text(service.length ? service.join() : '--');
        carInvoice.showOrderItem(template, item.itemDTOs);
    });
    carInvoice.setHeadVal(response);
}
window.showList = carInvoice.showList;
window.we = we();