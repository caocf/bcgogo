/**
 * Created by LiTao on 2015/11/6.
 */
function map() {
    var struct = function(key, value) {
        this.key = key;
        this.value = value;
    }

    var put = function(key, value) {
        for(var i = 0; i < this.arr.length; i++) {
            if(this.arr[i].key === key) {
                this.arr[i].value = value;
                return;
            }
        }
        this.arr[this.arr.length] = new struct(key, value);
    }

    var get = function(key) {
        for(var i = 0; i < this.arr.length; i++) {
            if(this.arr[i].key === key) {
                return this.arr[i].value;
            }
        }
        return null;
    }

    var remove = function(key) {
        var v;
        for(var i = 0; i < this.arr.length; i++) {
            v = this.arr.pop();
            if(v.key === key) {
                break;
            }
            this.arr.unshift(v);
        }
    }

    var size = function() {
        return this.arr.length;
    }

    var isEmpty = function() {
        return this.arr.length <= 0;
    }

    var clearMap = function() {
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

$(document).ready(function() {
    $("#print,#print2,#print3").hover(function(){
        $(this).css("color","#FD5300");
    },function(){
        $(this).css("color","#6699cc");
    });

    jQuery("#income").click(function() {
        $(this).addClass("big_hover_title").siblings().removeClass("big_hover_title");
        $("#incomeRecord").css("display", "");
        $("#expensesRecord").css("display", "none");
    })

    jQuery("#expenses").click(function() {
        $(this).addClass("big_hover_title").siblings().removeClass("big_hover_title");
        $("#incomeRecord").css("display", "none");;
        $("#expensesRecord").css("display", "");
    })

    $("#my_date_thismonth").click();

    $("#radMonth").click(function () {
        checkedChartTypeRadio("month");
    });
    $("#radDay").click(function () {
        checkedChartTypeRadio("day");
    });

    $("#runningStat").click(function() {
        window.location.href = "runningStat.do?method=getRunningStat";
    });

    $("#first_cont").click(function() {
        window.location.href = "businessStat.do?method=getBusinessStat";
    });

    $("#itemStat").click(function() {
        window.location.href = "itemStat.do?method=getItemStat";
    });
    $("#memberStat").click(function() {
        window.location.href = "member.do?method=memberStat";
    });
    $("#couponConsumeStat").click(function() {
        window.location.href = "couponConsume.do?method=couponConsumeStat";
    });

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


    $("#resetSearchCondition").click(function() {
        //reset form
        $("#couponConsumeStatisticsForm").resetForm();
        if(!$("#my_date_thismonth").hasClass("clicked")){
            $("#my_date_thismonth").click();
        }else{
            //$("#my_date_thismonth").click();
            //$("#my_date_thismonth").click();
            $("#statistics").click();
        }


    });

    $("#statistics").click(function(e) {
        $("#incomeInfo tr:not(:first)").remove();
        $("#expensesInfo tr:not(:first)").remove();
        $("#income").text("现金券收入(0)");
        $("#expenses").text("现金券支出(0)");
        e.preventDefault();
        var param = $("#couponConsumeStatisticsForm").serializeArray();
        var paramJson = {};
        $.each(param, function(index, val) {
            paramJson[val.name] = val.value;
        });

        initCouponIncome(paramJson);
        initCouponExpenses(paramJson);
    });


    $("#my_date_self_defining,#my_date_thisyear,#my_date_thismonth,#my_date_thisweek").bind("click",function(){
        $("#statistics").click();

    })

    $(".couponConsumeRecordRepeal").live("click",function(){
        if (confirm("友情提醒：作废后可能会有库存和金额的变化，为保证账面完整，请将原单据回笼，并标记作废！")) {
            //alert('couponConsume.do?method=couponConsumeRepeal&consumingRecordId='+this.title);
            window.location.href='couponConsume.do?method=couponConsumeRepeal&consumingRecordId=' + this.title;
        }
    });
});

//待修改
function initCouponIncome(paramJson) {
    var str = 'couponConsume.do?method=couponConsumeIncome';
    $.ajax({
        type:"POST",
        url:"couponConsume.do?method=couponConsumeIncome",
        data:paramJson,
        cache:false,
        dataType:"json",
        success:function(json) {
            initCouponIncomeByJson(json);
            initPages(json, "dynamical1", str, '', "initCouponIncomeByJson", '', '', paramJson, '');
        }
    });
}
//待修改
function initCouponIncomeByJson(data) {
    //jsonStrMap.put("memberCardOrder",data);
    $("#incomeTotal").text("0");
    $("#incomeInfo tr:not(:first)").remove();
    if (data == null || data[0] == null || data[3] == null) {
        return;
    }
    $("#income").text("现金券收入(" + data[2] + ")");
    //jsonStrMap.put("incomeTotal",data[1]);
    //$("#incomeTotal").text(jsonStrMap.get("incomeTotal"));
    $("#incomeTotal").text(data[1]);
    $.each(data[0], function(index, order) {
        var customerInfo;
        if(order.customerInfo==""||!order.customerInfo){
            if(order.appUserName!=""&&order.appUserName!=null&&order.appUserName!="null"){
                customerInfo=order.appUserName;
                if(order.appVehicleNo!=""&&order.appVehicleNo!=null&&order.appVehicleNo!="null"){
                    customerInfo=customerInfo+"/"+order.appVehicleNo;
                }
            }
            else if(order.appVehicleNo!=""&&order.appVehicleNo!=null&&order.appVehicleNo!="null"){
                customerInfo=order.appVehicleNo;
            }
            else{
                customerInfo="--"
            }
            //customerInfo=(order.appUserName+"/"+order.appVehicleNo);
        }
        else{
            customerInfo=order.customerInfo;
        }
        var id=(order.idStr);
        var product = (!order.product?"--":order.product);
        var orderTypes = (order.orderTypes);
        var orderStatus = (order.orderStatus); //getOrderStatusStr(order.orderStatus);
        //alert(orderStatus);
        var coupon = (order.coupon);
        var consumerTime = (order.consumerTime);
        var orderId = (order.orderIdStr);
        var sumMoney = (order.sumMoney);
        var tr = '<tr class="table-row-original">';
        if(index<=8){
            tr += '<td class="first-padding">' + '0' + (index + 1) + '</td>';
        }else{
            tr += '<td class="first-padding">' + (index + 1) + '</td>';
        }
        tr += '<td title="' + customerInfo + '">' + customerInfo + '</td>';
        tr += '<td title="' + product + '">' + product + '</td>';
        tr += '<td title="' + coupon + '">' + coupon + '</td>';
        tr += '<td title="' + consumerTime + '">' + consumerTime + '</td>';
        if(orderId!=null){
            if(product=="施工销售"||product=="REPAIR"){
                tr += '<td title="' + orderId + '"><a target="_blank" href="txn.do?method=getRepairOrder&repairOrderId='+orderId+'">' + orderId + '</a></td>';
            }
            else if(product=="洗车美容"||product=="WASH_BEAUTY"){
                tr += '<td title="' + orderId + '"><a target="_blank" href="washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId='+orderId+'">' + orderId + '</a></td>';
            }
            else{
                tr += '<td title="' + orderId + '">' + orderId + '</td>';
            }
        }
        else {
            tr += '<td>--</td>'
        }
        tr += '<td title="' + sumMoney + '">' + sumMoney + '</td>';
        if(orderStatus=="REPEAL"){
            tr += '<td><span class="repealed" title="'+id+'" >已作废</span></td>';
        }
        else{
            tr += '<td><a class="couponConsumeRecordRepeal" title="'+id+'" >作废</a></td>';
        }
        tr += '</tr>';
        $("#incomeInfo").append($(tr));
//    tableUtil.limitSpanWidth($(".customer","#infoCard"),10);
        tableUtil.tableStyle('#incomeInfo','.tab_title');
    });
    //alert(data[1]);
}

//待修改
function initCouponExpenses(paramJson) {
        var str = 'couponConsume.do?method=couponConsumeExpenses';
    $.ajax({
        type:"POST",
        url:"couponConsume.do?method=couponConsumeExpenses",
        data:paramJson,
        cache:false,
        dataType:"json",
        success:function(json) {
            initCouponExpensesByJson(json);
            initPages(json, "dynamical2", str, '', "initCouponExpensesByJson", '', '', paramJson, '');
        }
    });
}

//待修改
function initCouponExpensesByJson(data) {
    //jsonStrMap.put("memberCardOrder",data);
    $("#expensesTotal").text("0");
    $("#expensesInfo tr:not(:first)").remove();
    if (data == null || data[0] == null || data[3] == null) {
        return;
    }
    $("#expenses").text("现金券支出(" + data[2] + ")");
    //jsonStrMap.put("expensesTotal",data[1]);  //代金券总额
    //$("#expensesTotal").text(jsonStrMap.get("expensesTotal"));
    $("#expensesTotal").text(data[1]);
    $.each(data[0], function(index, order) {
        var product = (!order.product?"--":order.product);
        var productNum = (!order.productNum?0:order.productNum);
        var coupon = (!order.coupon?0:order.coupon);
        var consumerTime = (!order.consumerTime?"--":order.consumerTime);
        var receiptNo = (!order.receiptNo?"--":order.receiptNo);
        var sumMoney = (!order.sumMoney?0:order.sumMoney);
        var tr = '<tr class="table-row-original">';
        if(index<=8){
            tr += '<td class="first-padding">' + '0' + (index + 1) + '</td>';
        }else{
            tr += '<td class="first-padding">' + (index + 1) + '</td>';
        }
        tr += '<td title="' + product + '">' + product + '</td>';
        tr += '<td title="' + productNum + '">' + productNum + '</td>';
        tr += '<td title="' + coupon + '">' + coupon + '</td>';
        tr += '<td title="' + consumerTime + '">' + consumerTime + '</td>';
        tr += '<td title="' + receiptNo + '">' + receiptNo + '</td>';
        tr += '<td title="' + sumMoney + '">' + sumMoney + '</td>';
        tr += '</tr>';
        $("#expensesInfo").append($(tr));

//    tableUtil.limitSpanWidth($(".customer","#infoCard"),10);
        tableUtil.tableStyle('#expensesInfo','.tab_title');
    });
    //alert(data[1]);
}

function notOpen() {
    var time = new Array(), timeFlag = true;
    time[0] = new Date().getTime();
    time[1] = new Date().getTime();
    var reg = /^(\d+)$/;
    time[1] = new Date().getTime();
    if (time[1] - time[0] > 3000 || timeFlag) {
        time[0] = time[1];
        timeFlag = false;
        showMessage.fadeMessage("35%", "40%", "slow", 3000, "此功能稍后开放！");     // top left fadeIn fadeOut message
    }
}


