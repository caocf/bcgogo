/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-5-23
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
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

var queryType = "day";
var arrayType1 = "timeDesc";
var pageType = "repair";
var totalNum = 0;
$(document).ready(function() {

    jQuery(".runningStat").hide();
    jQuery("#first_cont").addClass("hover_yinye");

    jQuery("#runningStat").click(function() {
        window.location.href = "runningStat.do?method=getRunningStat";
    });

    jQuery("#first_cont").click(function() {
        window.location.href = "businessStat.do?method=getBusinessStat";
    });
    jQuery("#itemStat").click(function() {
        window.location.href = "itemStat.do?method=getItemStat";
    });
    jQuery("#memberStat").click(function() {
        window.location.href = "member.do?method=memberStat";
    });
    $("#couponConsumeStat").click(function() {
        window.location.href = "couponConsume.do?method=couponConsumeStat";
    });

    $("#dayWashStat").bind('click', function() {
        tdClick("day", "wash");
    });

    $("#monthWashStat").bind('click', function() {
        tdClick("month", "wash");
    });

    $("#yearWashStat").bind('click', function() {
        tdClick("year", "wash");
    });

    $("#daySaleStat").bind('click', function() {
        tdClick("day", "sale");
    });

    $("#monthSaleStat").bind('click', function() {
        tdClick("month", "sale");
    });

    $("#yearSaleStat").bind('click', function() {
        tdClick("year", "sale");
    });

    $("#dayRepairStat").bind('click', function() {
        tdClick("day", "repair");
    });

    $("#monthRepairStat").bind('click', function() {
        tdClick("month", "repair");
    });

    $("#yearRepairStat").bind('click', function() {
        tdClick("year", "repair");
    });


    $("#dayMemberStat").bind('click', function() {
        tdClick("day", "member");
    });

    $("#monthMemberStat").bind('click', function() {
        tdClick("month", "member");
    });

    $("#yearMemberStat").bind('click', function() {
        tdClick("year", "member");
    });

    $("#print,#print2,#print3").bind("click",function(){

        var orderType = $(this).attr("orderType");
        var url = "";
        var dataList = "[";
        var jsonObj = null;
        url="businessStat.do?method=getBusinessStatDetailToPrint";

        var total = 0;
        var pagerTotal = 0;
        var costTotal = 0;
        var settleTotal = 0;
        var debtTotal = 0;
        var discountTotal = 0;
        var profitTotal =0;

        if("sale"==orderType)
        {
            var jsonObj = jsonStrMap.get("salesOrder");
            if(jsonObj[jsonObj.length-1].totalRows==0)
            {
                nsDialog.jAlert("无数据，不能打印！");
                return;
            }

            for(var i=0;i<jsonObj.length-9;i++)
            {
                if(null == jsonObj[i].afterMemberDiscountTotal )
                {
                    jsonObj[i].afterMemberDiscountTotal = jsonObj[i].total;
                    jsonObj[i].afterMemberDiscountTotal = dataTransition.rounding(jsonObj[i].afterMemberDiscountTotal,2);
                }
                if("["==dataList)
                {
                    dataList += JSON.stringify(jsonObj[i]);
                }
                else
                {
                    dataList += ","+ JSON.stringify(jsonObj[i]);
                }
            }
            costTotal =jsonObj[jsonObj.length - 9].saleCostTotal ;
            settleTotal = jsonObj[jsonObj.length - 8].saleSettleTotal;
            debtTotal =  jsonObj[jsonObj.length - 7].saleDebtTotal;
            discountTotal = jsonObj[jsonObj.length - 6].saleDiscountTotal;
            profitTotal =  jsonObj[jsonObj.length - 5].saleProfitTotal;
            pagerTotal = jsonObj[jsonObj.length - 2].saleAfterMemberDiscountTotal;
            total = jsonObj[jsonObj.length - 4].total;
        }
        if("repair" == orderType)
        {
            var jsonObj = jsonStrMap.get("repairOrder");
            if(jsonObj[jsonObj.length-1].totalRows==0)
            {
                nsDialog.jAlert("无数据，不能打印！");
                return;
            }

            for(var i=0;i<jsonObj.length-8;i++)
            {
                if(null == jsonObj[i].afterMemberDiscountTotal )
                {
                    jsonObj[i].afterMemberDiscountTotal = jsonObj[i].total;
                }
                if("["==dataList)
                {
                    dataList += JSON.stringify(jsonObj[i]);
                }
                else
                {
                    dataList += ","+ JSON.stringify(jsonObj[i]);
                }
            }

            costTotal =jsonObj[jsonObj.length - 8].repairCostTotal ;
            settleTotal = jsonObj[jsonObj.length - 7].repairSettleTotal;
            debtTotal =  jsonObj[jsonObj.length - 6].repairDebtTotal;
            discountTotal = jsonObj[jsonObj.length - 5].repairDiscountTotal;
            profitTotal =  jsonObj[jsonObj.length - 4].repairProfitTotal;
            pagerTotal = jsonObj[jsonObj.length - 2].pageTotal;
            total = jsonObj[jsonObj.length - 3].total;
        }
        if("wash"==orderType)
        {
            var jsonObj = jsonStrMap.get("washOrder");
            if(jsonObj[jsonObj.length-1].totalRows==0)
            {
                nsDialog.jAlert("无数据，不能打印！");
                return;
            }

            for(var i=0;i<jsonObj.length-8;i++)
            {
                if(null == jsonObj[i].afterMemberDiscountTotal )
                {
                    jsonObj[i].afterMemberDiscountTotal = jsonObj[i].total;
                }
                if("["==dataList)
                {
                    dataList += JSON.stringify(jsonObj[i]);
                }
                else
                {
                    dataList += ","+ JSON.stringify(jsonObj[i]);
                }
            }

            costTotal =jsonObj[jsonObj.length - 8];
            settleTotal = jsonObj[jsonObj.length - 7];
            debtTotal =  jsonObj[jsonObj.length - 6];
            discountTotal = jsonObj[jsonObj.length - 5];
            profitTotal =  jsonObj[jsonObj.length - 4];
            pagerTotal = jsonObj[jsonObj.length - 3];
            total = jsonObj[jsonObj.length - 2];
        }

        dataList +="]";

        var startDateStr = jsonObj[jsonObj.length-1].startDateStr;
        var endDateStr = jsonObj[jsonObj.length-1].endDateStr;

        var data={
            dataList:dataList,
            costTotal:costTotal,
            settleTotal:settleTotal,
            debtTotal:debtTotal,
            discountTotal:discountTotal,
            profitTotal:profitTotal,
            pagerTotal:pagerTotal,
            total:total,
            startDateStr:startDateStr,
            endDateStr:endDateStr,
            orderType:orderType,
            now:new Date()
        }

        $.ajax({
            url:url,
            data:data,
            type: "POST",
            cache:false,
            success:function(data){
                if(!data) return;

                var printWin = window.open("", "", "width=1024,height=768");

                with(printWin.document){
                    open("text/html", "replace");
                    write(data);
                    close();
                }
            }
        });

    });
});

function tdClick(queryTypeStr, type) {
    if (queryType != queryTypeStr) {
        queryType = queryTypeStr;
        initTable(queryType);
    }
    if (type == "wash") {
        $("#carWashTitle")[0].click();
    } else if (type == "repair") {
        $("#serviceTitle")[0].click();
    } else if (type == "sale") {
        $("#goodsSaleTitle")[0].click();
    }
}

function initTable(type) {
    var dayHid = ($("#dayHid").val()) * 1;
    var monthHid = ($("#monthHid").val()) * 1;
    var yearHid = ($("#yearHid").val()) * 1;

    if($("#serviceTitle").css("display")=="block"){
      initRepairTable(dayHid, monthHid, yearHid, type, arrayType1);
    }
    if($("#carWashTitle").css("display")=="block"){
      initWashTable(dayHid, monthHid, yearHid, type, arrayType1);
    }
    initSaleTable(dayHid, monthHid, yearHid, type, arrayType1);
}

function initWashTable(day, month, year, type, arrayType) {
    var date = "" + year + "-" + month + "-" + day;
    queryType = type;
    arrayType1 = arrayType;
    var str = 'businessStat.do?method=getWashOrderDetail';
    $.ajax({
               type:"POST",
               url:"businessStat.do?method=getWashOrderDetail",
               data:{startPageNo:1,maxRows:25,type:type,dateStr:date,arrayType:arrayType},
               cache:false,
               dataType:"json",
               success:function(jsonStr) {
                   initCarWash(jsonStr);
                   initPages(jsonStr, "dynamical3", str, '', "initCarWash", '', '',
                             {startPageNo:'1',maxRows:25,type:type,dateStr:jQuery('#yearHid').val() + '-' + jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val(),arrayType:arrayType},
                             '');
               }
           });
}

function initRepairTable(day, month, year, type, arrayType) {
    var date = "" + year + "-" + month + "-" + day;
    queryType = type;
    arrayType1 = arrayType;
    $.ajax({
               type:"POST",
               url:"businessStat.do?method=getRepairOrderDetail",
               data:{startPageNo:1,maxRows:25,type:type,dateStr:date,arrayType:arrayType},
               cache:false,
               dataType:"json",
               success:function(jsonStr) {
                   initRepairOrder(jsonStr);
                   initPages(jsonStr, "dynamical1", "businessStat.do?method=getRepairOrderDetail", '',
                             "initRepairOrder", '', '',
                             {
                                 startPageNo:'1',
                                 maxRows:25,
                                 type:type,
                                 dateStr:jQuery('#yearHid').val() + '-' + jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val(),
                                 arrayType:arrayType
                             }, '');
               }
           });
}


function initSaleTable(day, month, year, type, arrayType) {
    var date = "" + year + "-" + month + "-" + day;
    queryType = type;
    arrayType1 = arrayType;
    $.ajax({
               type:"POST",
               url:"businessStat.do?method=getSalesOrderDetail",
               data:{startPageNo:1,maxRows:25,type:type,dateStr:date,arrayType:arrayType},
               cache:false,
               dataType:"json",
               success:function(jsonStr) {
                   initSaleOrder(jsonStr);
                   initPages(jsonStr, "dynamical2", "businessStat.do?method=getSalesOrderDetail", '', "initSaleOrder",
                             '', '',
                             {startPageNo:'1',maxRows:25,type:type,dateStr:jQuery('#yearHid').val() + '-' + jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val(),arrayType:arrayType},
                             '');
               }
           });
}

//function initMemberTable(day, month, year, type, arrayType) {
//    var date = "" + year + "-" + month + "-" + day;
//    queryType = type;
//    arrayType1 = arrayType;
//    $.ajax({
//               type:"POST",
//               url:"businessStat.do?method=getMemberOrder",
//               data:{startPageNo:1,maxRows:25,type:type,dateStr:date,arrayType:arrayType},
//               cache:false,
//               dataType:"json",
//               success:function(jsonStr) {
//                   initMemberOrder(jsonStr);
//                   initPages(jsonStr, "dynamical4", "businessStat.do?method=getMemberOrder", '', "initMemberOrder", '',
//                             '',
//                             {startPageNo:'1',maxRows:25,type:type,dateStr:jQuery('#yearHid').val() + '-' + jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val(),arrayType:arrayType},
//                             '');
//               }
//           });
//}


function openWinRepair(orderId) {
    window.open('txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=' + orderId);
}
function openWinSale(orderId) {
    window.open('sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=' + orderId);
}

function openWinWashBeauty(orderId) {
    window.open('washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=' + orderId);
}

function openSalesReturn(orderId) {
    window.open('salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=' + orderId);
}

function initRepairOrder(jsonStr) {

    jsonStrMap.put("repairOrder",jsonStr);

    if (jsonStr != null) {
        $("#repairTable tr:not(:first)").remove();

        $("#repairPageTotal").text(jsonStr[jsonStr.length - 2].pageTotal == undefined ? " " : jsonStr[jsonStr.length - 2].pageTotal);
        $("#repairTotal").text(jsonStr[jsonStr.length - 3].total == undefined ? " " : jsonStr[jsonStr.length - 3].total);
        $("#repairCostTotal").text(jsonStr[jsonStr.length - 8].repairCostTotal == undefined ? " " : jsonStr[jsonStr.length - 8].repairCostTotal);
        $("#repairSettleTotal").text(jsonStr[jsonStr.length - 7].repairSettleTotal == undefined ? " " : jsonStr[jsonStr.length - 7].repairSettleTotal);
        $("#repairDebtTotal").text(jsonStr[jsonStr.length - 6].repairDebtTotal == undefined ? " " : jsonStr[jsonStr.length - 6].repairDebtTotal);
        $("#repairDiscountTotal").text(jsonStr[jsonStr.length - 5].repairDiscountTotal == undefined ? " " : jsonStr[jsonStr.length - 5].repairDiscountTotal);
        if(!APP_BCGOGO.Permission.Version.FourSShopVersion){
          $("#repairProfitTotal").text(jsonStr[jsonStr.length - 4].repairProfitTotal == undefined ? " " : jsonStr[jsonStr.length - 4].repairProfitTotal);
        }
        $("#totalRepairNum").val(jsonStr[jsonStr.length - 1].totalRows == undefined ? "0" : jsonStr[jsonStr.length - 1].totalRows);
        for (var i = 0; i < jsonStr.length - 8; i++) {

            var orderTime = jsonStr[i].vestDateStr == undefined ? " " : jsonStr[i].vestDateStr;
            var vehicle = jsonStr[i].vechicle == undefined ? " " : jsonStr[i].vechicle;
            var customer = jsonStr[i].customerName == undefined ? " " : jsonStr[i].customerName;
            var serviceContent = jsonStr[i].serviceContent == undefined ? " " : jsonStr[i].serviceContent;
            var serviceTotal = jsonStr[i].serviceTotal == undefined ? " " : jsonStr[i].serviceTotal;
            var serviceTotalCost = jsonStr[i].serviceTotalCost == undefined ? " " : jsonStr[i].serviceTotalCost;
            var salesContent = jsonStr[i].salesContent == undefined ? " " : jsonStr[i].salesContent;
            var salesTotal = jsonStr[i].salesTotal == undefined ? " " : jsonStr[i].salesTotal;
            var salesTotalCost = jsonStr[i].salesTotalCost == undefined ? " " : jsonStr[i].salesTotalCost;
            var total = jsonStr[i].total == undefined ? " " : jsonStr[i].total;
            var orderTotalCost = jsonStr[i].orderTotalCost == undefined ? " " : jsonStr[i].orderTotalCost;
            var salesContentStr = jsonStr[i].salesContentStr == undefined ? " " : jsonStr[i].salesContentStr;
            var serviceContentStr = jsonStr[i].serviceContentStr == undefined ? " " : jsonStr[i].serviceContentStr;
            var orderProfit = jsonStr[i].orderProfit == undefined ? " " : jsonStr[i].orderProfit;
            var orderProfitPercent = jsonStr[i].orderProfitPercent == undefined ? " " : jsonStr[i].orderProfitPercent;
            var debt = jsonStr[i].debt == undefined ? " " : jsonStr[i].debt;
            var settledAmount = jsonStr[i].settledAmount == undefined ? " " : jsonStr[i].settledAmount;
            var orderDiscount = jsonStr[i].orderDiscount == undefined ? " " : jsonStr[i].orderDiscount;
            var repairOrderId = jsonStr[i].brand == undefined ? " " : jsonStr[i].brand;
            var afterMemberDiscountTotal =jsonStr[i].afterMemberDiscountTotal == undefined ? total: dataTransition.rounding(jsonStr[i].afterMemberDiscountTotal,2);
            var str = "'" + repairOrderId + "'";
            var receiptNo = jsonStr[i].receiptNo == undefined ? " " : jsonStr[i].receiptNo;

            var otherIncomeTotal = jsonStr[i].otherIncomeTotal == undefined ? 0 : dataTransition.rounding(jsonStr[i].otherIncomeTotal,2);
            var otherCost =    jsonStr[i].otherTotalCostPrice == undefined ? 0 : dataTransition.rounding(jsonStr[i].otherTotalCostPrice,2);

            var tr = '<tr class="table-row-original">';

            tr += '<td class="first-padding" title="' + orderTime + '">' + orderTime + '</td>';
            tr += '<td title="' + receiptNo + '">' + '<span class="customer limit-span" style="color:#007CDA;"><a href ="#" onclick="openWinRepair(' + str + ')">' + receiptNo + '</a></span>' + '</td> ';
            tr += '<td title="' + vehicle + '">' + vehicle + '</td>';

            tr += '<td title="' + serviceTotal + '">' + serviceTotal + '</td>';
            tr += '<td title="' + serviceTotalCost + '">' + serviceTotalCost + '</td>';
            tr += '<td title="' + salesTotal + '">' + salesTotal + '</td>';
            tr += '<td title="' + salesTotalCost + '">' + salesTotalCost + '</td>';
            tr += '<td title="' + otherIncomeTotal + '">' + otherIncomeTotal + '/' + otherCost  + '</td>';
            tr += '<td title="' + total + '">' + total + '</td>';
            tr += '<td title="' + orderTotalCost + '">' + orderTotalCost + '</td>';
            tr += '<td title="' + settledAmount + '">' + settledAmount + '</td>';
            tr += '<td title="' + debt + '">' + debt + '</td>';
            if (!APP_BCGOGO.Permission.Version.FourSShopVersion) {
              tr += '<td title="' + orderProfit + '">' + orderProfit + '</td>';
              tr += '<td title="' + orderProfitPercent + '">' + orderProfitPercent + '</td>';
            }
            tr += '<td title="' + orderDiscount + '" class="last-padding">' + orderDiscount + '</td>';
            tr += '</tr>';
            $("#repairTable").append(tr);
        }
        tableUtil.limitSpanWidth($(".customer","#repairTable"),10);
        tableUtil.tableStyle('#repairTable','.tab_title');
    }
}

function initSaleOrder(jsonStr) {

    jsonStrMap.put("salesOrder",jsonStr);

    if (jsonStr != null) {
        $("#salesOrder tr:not(:first)").remove();

        $("#saleCostTotal").text(jsonStr[jsonStr.length - 9].saleCostTotal == undefined ? " " : jsonStr[jsonStr.length - 9].saleCostTotal);
        $("#saleSettleTotal").text(jsonStr[jsonStr.length - 8].saleSettleTotal == undefined ? " " : jsonStr[jsonStr.length - 8].saleSettleTotal);
        $("#saleDebtTotal").text(jsonStr[jsonStr.length - 7].saleDebtTotal == undefined ? " " : jsonStr[jsonStr.length - 7].saleDebtTotal);
        $("#saleDiscountTotal").text(jsonStr[jsonStr.length - 6].saleDiscountTotal == undefined ? " " : jsonStr[jsonStr.length - 6].saleDiscountTotal);
        $("#saleProfitTotal").text(jsonStr[jsonStr.length - 5].saleProfitTotal == undefined ? " " : jsonStr[jsonStr.length - 5].saleProfitTotal);
        $("#saleTotal").text(jsonStr[jsonStr.length - 4].total == undefined ? " " : jsonStr[jsonStr.length - 4].total);
        $("#salePageTotal").text(jsonStr[jsonStr.length - 3].pageTotal == undefined ? " " : jsonStr[jsonStr.length - 3].pageTotal);
//        $("#salePageTotal").text(jsonStr[jsonStr.length - 2].saleAfterMemberDiscountTotal == undefined ? " " : jsonStr[jsonStr.length - 2].saleAfterMemberDiscountTotal);
        $("#totalSalesNum").val(jsonStr[jsonStr.length - 1].totalRows == undefined ? "0" : jsonStr[jsonStr.length - 1].totalRows);
        for (var i = 0; i < jsonStr.length - 9; i++) {
            var orderTime = jsonStr[i].vestDateStr == undefined ? " " : jsonStr[i].vestDateStr;
            var customer = jsonStr[i].customerOrSupplierName == undefined ? " " : jsonStr[i].customerOrSupplierName;
            var orderContent = jsonStr[i].orderContent == undefined ? " " : jsonStr[i].orderContent;
            var total = jsonStr[i].amount == undefined ? 0 : dataTransition.rounding(jsonStr[i].amount,2);
            var totalCostPrice = jsonStr[i].totalCostPrice == undefined ? " " : dataTransition.rounding(jsonStr[i].totalCostPrice,2);
            var orderProfit = jsonStr[i].grossProfit == undefined ? " " : dataTransition.rounding(jsonStr[i].grossProfit,2);
            var orderProfitPercent = jsonStr[i].grossProfitRate == undefined ? " " : jsonStr[i].grossProfitRate;
            orderProfitPercent +="%";
            var debt = jsonStr[i].debt == undefined ? " " : dataTransition.rounding(jsonStr[i].debt,2);
            var orderContentStr = jsonStr[i].orderContentShort == undefined ? " " : jsonStr[i].orderContentShort;
            var settledAmount = jsonStr[i].settled == undefined ? " " : dataTransition.rounding(jsonStr[i].settled,2);
            var orderDiscount = jsonStr[i].discount == undefined ? " " : dataTransition.rounding(jsonStr[i].discount,2);
            var salesOrderId = jsonStr[i].orderIdStr == undefined ? " " : jsonStr[i].orderIdStr;
            var afterMemberDiscountTotal = jsonStr[i].afterMemberDiscountTotal == undefined ? total : dataTransition.rounding(jsonStr[i].afterMemberDiscountTotal,2);
            var orderTypeValue = jsonStr[i].orderTypeValue == undefined ? " " : jsonStr[i].orderTypeValue;
            var str = "'" + salesOrderId + "'";

            var otherIncomeTotal = jsonStr[i].otherIncomeTotal == undefined ? 0 : dataTransition.rounding(jsonStr[i].otherIncomeTotal,2);
            var productTotal = jsonStr[i].productTotal == undefined ? 0 : dataTransition.rounding(jsonStr[i].productTotal,2);
            var receiptNo = jsonStr[i].receiptNo == undefined ? " " : jsonStr[i].receiptNo;
            var otherCost =    jsonStr[i].otherTotalCostPrice == undefined ? 0 : dataTransition.rounding(jsonStr[i].otherTotalCostPrice,2);
            var productCost = jsonStr[i].productTotalCostPrice == undefined ? 0 : dataTransition.rounding(jsonStr[i].productTotalCostPrice,2);

            var tr = '<tr class="table-row-original">';
            tr += '<td class="first-padding">' + orderTime + '</td>';
            if (orderTypeValue == "销售退货单") {
              tr += '<td title="' + receiptNo + '"><span>' + '<a href ="#" style="color:#005DB7" onclick="openSalesReturn(' + str + ')">' + receiptNo + '</a></span>' + '</td> ';
            } else if (orderTypeValue == "销售单") {
              tr += '<td title="' + receiptNo + '"><span>' + '<a href ="#" style="color:#005DB7" onclick="openWinSale(' + str + ')">' + receiptNo + '</a></span>' + '</td> ';
            }
            tr += '<td title="' + orderTypeValue + '">' + orderTypeValue + '</td>';

            tr += '<td title="' + customer + '">' + customer + '</td>';

            var orderContentStr = orderContentStr.replace('销售内容:', '').replace('退货商品:','');
            if (orderContentStr.length > 2) {
                orderContentStr = orderContentStr.substring(1, orderContentStr.length - 1);
            }
            tr += '<td title="' + orderContent + '">' + orderContentStr + '</td>';
            tr += '<td title="' + productTotal + '">' + productTotal + '</td>';

            tr += '<td title="' + productCost + '">' + productCost + '</td>';
            tr += '<td title="' + otherIncomeTotal + '/' + otherCost + '">' + otherIncomeTotal + '/' + otherCost + '</td>';
            tr += '<td title="' + total + '">' + total + '</td>';

            tr += '<td title="' + settledAmount + '">' + settledAmount + '</td>';
            tr += '<td title="' + debt + '">' + debt + '</td>';
            tr += '<td title="' + totalCostPrice + '">' + totalCostPrice + '</td>';

            tr += '<td title="' + orderProfit + '">' + orderProfit + '</td>';
            tr += '<td title="' + orderProfitPercent + '">' + orderProfitPercent + '</td>';
            tr += '<td title="' + orderDiscount + '" class="last-padding">' + orderDiscount + '</td>';
            tr += '</tr>';
            $("#salesOrder").append(tr);
        }
        tableUtil.limitSpanWidth($(".customer","#salesOrder"),10);
        tableUtil.tableStyle('#salesOrder','.tab_title');
    }
}

function initCarWash(jsonStr) {
    jsonStrMap.put("washOrder",jsonStr);
    if (jsonStr != null) {

        $("#washCar tr:not(:first)").remove();

        $("#washCostTotal").text(jsonStr[jsonStr.length - 8] == undefined ? "0" : jsonStr[jsonStr.length - 8]);
        $("#washSettleTotal").text(jsonStr[jsonStr.length - 7] == undefined ? "0" : jsonStr[jsonStr.length - 7]);
        $("#washDebtTotal").text(jsonStr[jsonStr.length - 6] == undefined ? "0" : jsonStr[jsonStr.length - 6]);
        $("#washDiscountTotal").text(jsonStr[jsonStr.length - 5] == undefined ? "0" : jsonStr[jsonStr.length - 5]);
        if (!APP_BCGOGO.Permission.Version.FourSShopVersion) {
          $("#washProfitTotal").text(jsonStr[jsonStr.length - 4] == undefined ? "" : jsonStr[jsonStr.length - 4]);
        }
        $("#washTotal").text(jsonStr[jsonStr.length - 2] == undefined ? " " : jsonStr[jsonStr.length - 2]);
        $("#washPageTotal").text(jsonStr[jsonStr.length - 3] == undefined ? " " : jsonStr[jsonStr.length - 3]);
        $("#totalWashNum").val(jsonStr[jsonStr.length - 1].totalRows == undefined ? "0" : jsonStr[jsonStr.length - 1].totalRows);
        // $("#washPageTotal").text(jsonStr[jsonStr.length - 2].pageTotal == undefined ? " " : jsonStr[jsonStr.length - 2].pageTotal);
        // $("#washTotal").text(jsonStr[jsonStr.length - 3].total == undefined ? " " : jsonStr[jsonStr.length - 3].total);
        for (var i = 0; i < jsonStr.length - 8; i++) {

            var orderTime = jsonStr[i].orderTimeCreatedStr == undefined ? " " : jsonStr[i].orderTimeCreatedStr;
            var orderReceiptNo = jsonStr[i].orderReceiptNo == undefined ? "" : jsonStr[i].orderReceiptNo;
            var vehicle = jsonStr[i].vehicle == undefined ? " " : jsonStr[i].vehicle;
            var content = jsonStr[i].itemName == undefined ? "" : jsonStr[i].itemName;
            var cashNum = jsonStr[i].orderTotalAmount == undefined ? " " : jsonStr[i].orderTotalAmount;
            var washBeautyId = jsonStr[i].vehicleYear == undefined ? " " : jsonStr[i].vehicleYear;
            var orderType = jsonStr[i].orderType == undefined ? " " : jsonStr[i].orderType;
            var washBeautyIdStr = "'" + washBeautyId + "'";
            var orderTotalAmount = jsonStr[i].orderTotalAmount == undefined ? cashNum : dataTransition.rounding(jsonStr[i].orderTotalAmount,2);
            var settledAmount =jsonStr[i].settledAmount;
            var arrears =jsonStr[i].arrears;
            var discount =jsonStr[i].discount;
            var memberNo =jsonStr[i].memberCardName == undefined ? "--" : jsonStr[i].memberCardName;
            var tr = '<tr class="table-row-original">';
            tr += '<td class="first-padding">' + orderTime + '</td>';
            if (orderType == "WASH_BEAUTY") {
              tr += '<td title="' + orderReceiptNo + '">' + '<a href ="#" onclick="openWinWashBeauty(' + washBeautyIdStr + ')">' + orderReceiptNo + '</a> ' + '</td> ';
            }
            else {
              tr += '<td>--</td>';
            }
            tr += '<td>' + vehicle + '</td>';

            tr += '<td title="' + content + '">' + (content.length > 40 ? (content.substring(0, 40) + "...") : content) + '</td>';
            tr += '<td title="' + memberNo + '">' + memberNo + '</td>';
            tr += '<td>' + orderTotalAmount + '</td>';
            tr += '<td>' + settledAmount + '</td>';
            tr += '<td>' + arrears + '</td>';
            tr += '<td class="last-padding">' + discount + '</td>';
            tr += '</tr>';
            $("#washCar").append(tr);
        }
        tableUtil.tableStyle('#washCar','.tab_title');
    }
}

function initMemberOrder(jsonStr) {
    if (jsonStr != null) {
        $("#infoCard tr:not(:first)").remove();

        $("#memberCostTotal").text(jsonStr[jsonStr.length - 8] == undefined ? "0" : jsonStr[jsonStr.length - 8]);
        $("#memberSettleTotal").text(jsonStr[jsonStr.length - 7] == undefined ? "0" : jsonStr[jsonStr.length - 7]);
        $("#memberDebtTotal").text(jsonStr[jsonStr.length - 6] == undefined ? "0" : jsonStr[jsonStr.length - 6]);
        $("#memberDiscountTotal").text(jsonStr[jsonStr.length - 5] == undefined ? "0" : jsonStr[jsonStr.length - 5]);
        $("#memberProfitTotal").text(jsonStr[jsonStr.length - 4] == undefined ? "" : jsonStr[jsonStr.length - 4]);
        $("#memberTotal").text(jsonStr[jsonStr.length - 2] == undefined ? " " : jsonStr[jsonStr.length - 2]);
        $("#memberPageTotal").text(jsonStr[jsonStr.length - 3] == undefined ? " " : jsonStr[jsonStr.length - 3]);

        for (var i = 0; i < jsonStr.length - 8; i++) {
            var orderNo = jsonStr[i].orderNo == undefined ? " " : jsonStr[i].orderNo;
            var memberNo = jsonStr[i].memberNo == undefined ? " " : jsonStr[i].memberNo;
            var memberCardName = jsonStr[i].memberCardName == undefined ? " " : jsonStr[i].memberCardName;
            var vestDateStr = jsonStr[i].vestDateStr == undefined ? " " : jsonStr[i].vestDateStr;
            var customerName = jsonStr[i].customerName == undefined ? " " : jsonStr[i].customerName;
            var memberCardType = jsonStr[i].memberCardType == undefined ? " " : jsonStr[i].memberCardType;
            var memberAmount = jsonStr[i].memberAmount == undefined ? " " : jsonStr[i].memberAmount;
            var settledAmount = jsonStr[i].settledAmount == undefined ? " " : jsonStr[i].settledAmount;
            var salesMan = jsonStr[i].salesMan == undefined ? " " : jsonStr[i].salesMan;


            // var str =  "'" + salesOrderId + "'";

            var tr = '<tr class="table-row-original">';
            tr += '<td class="first-padding">' + '0' + (i + 1) + '</td>';
            tr += '<td title="' + orderNo + '">' + orderNo + '</td>';
            //tr += '<td>' + '<a href ="#" onclick="openWinSale(' + str + ')">' + customer + '</a> ' + '</td> ';
            tr += '<td title="' + memberNo + '">' + memberNo + '</td>';
            tr += '<td title="' + memberCardName + '">' + memberCardName + '</td>';
            tr += '<td title="' + vestDateStr + '">' + vestDateStr + '</td>';
            tr += '<td title="' + customerName + '"><span class="customer limit-span">' + customerName + '</span></td>';
            tr += '<td title="' + memberCardType + '">' + memberCardType + '</td>';
            tr += '<td title="' + memberAmount + '">' + memberAmount + '</td>';
            tr += '<td title="' + settledAmount + '">' + settledAmount + '</td>';
            tr += '<td title="' + salesMan + '" class="last-padding">' + salesMan + '</td>';
            tr += '</tr>';
            $("#infoCard").append(tr);
        }
        tableUtil.limitSpanWidth($(".customer","#infoCard"),10);
        tableUtil.tableStyle('#infoCard','.tab_title');
    }
}