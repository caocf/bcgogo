/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-9-4
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */

var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
var queryType = "day";
var defaultArrayType = "timeDesc";
var myHighChart;

$(document).ready(function() {
    myHighChart = new HighChartFunction({
                                            title:'营业流水余额',
                                            url:'runningStat.do?method=getRunningStatByType',
                                            render:'chart_div'
                                        });

    jQuery(".first_cont").hide();
    jQuery("#runningStat").addClass("hover_yinye");

    jQuery("#runningStat").click(function() {
        window.location.href = "runningStat.do?method=getRunningStat";
    });

    jQuery("#first_cont").click(function() {
        window.location.href = "businessStat.do?method=getBusinessStat";
    });

    jQuery("#itemStat").click(function() {
        window.location.href = "itemStat.do?method=getItemStat";
    });
    $("#memberStat").click(function() {
      window.location.href = "member.do?method=memberStat";
    });
    $("#couponConsumeStat").click(function() {
        window.location.href = "couponConsume.do?method=couponConsumeStat";
    });

   $("#dayIncome").live("click",function () {
        $(".cont_title a").removeClass("hover_title");
        $(".cont_title a").not("#dayIncome").addClass("big_unhover_title");

        $("#dayIncome").removeClass("big_unhover_title");
        $("#dayIncome")[0].className = "hover_title";

        jQuery(".tb_add").css("display", "none");
        $("#dayRunningInfo")[0].style.display = "";

    });
   $("#yearIncome").live("click",function () {
        $(".cont_title a").removeClass("hover_title");
        $(".cont_title a").not("#yearIncome").addClass("big_unhover_title");

        $("#yearIncome").removeClass("big_unhover_title");
        $("#yearIncome")[0].className = "hover_title";

        jQuery(".tb_add").css("display", "none");
        $("#yearRunningInfo")[0].style.display = "";
    });

   $("#monthIncome").live("click",function () {
        $(".cont_title a").removeClass("hover_title");
        $(".cont_title a").not("#monthIncome").addClass("big_unhover_title");

        $("#monthIncome").removeClass("big_unhover_title");
        $("#monthIncome")[0].className = "hover_title";

        jQuery(".tb_add").css("display", "none");
        $("#monthRunningInfo")[0].style.display = "";
    });

   $("#dayExpenditure").live("click",function () {
        $(".cont_title a").removeClass("hover_title");
        $(".cont_title a").not("#dayExpenditure").addClass("big_unhover_title");

        $("#dayExpenditure").removeClass("big_unhover_title");
        $("#dayExpenditure").addClass("hover_title");
        jQuery(".tb_add").css("display", "none");
        $("#dayExpenditureInfo")[0].style.display = "";
    });

    $("#monthExpenditure").live("click",function () {
        $(".cont_title a").removeClass("hover_title");
        $(".cont_title a").not("#monthExpenditure").addClass("big_unhover_title");

        $("#monthExpenditure").removeClass("big_unhover_title");
        $("#monthExpenditure").addClass("hover_title");

        jQuery(".tb_add").css("display", "none");
        $("#monthExpenditureInfo")[0].style.display = "";
    });

      $("#yearExpenditure").live("click",function () {
        $(".cont_title a").removeClass("hover_title");
        $(".cont_title a").not("#yearExpenditure").addClass("big_unhover_title");

        $("#yearExpenditure").removeClass("big_unhover_title");
        $("#yearExpenditure").addClass("hover_title");

        jQuery(".tb_add").css("display", "none");
        $("#yearExpenditureInfo").css("display", "");
    });

    var day = ($("#dayHid").val()) * 1,
        month = ($("#monthHid").val()) * 1,
        year = ($("#yearHid").val()) * 1;

    initRunningStatDate(year, month, day, "normal");
    $("#dayIncome").click();
});


function initRunningStatDate(year, month, day, type) {

    var ajaxUrl = "runningStat.do?method=getRunningStatDate";
    var ajaxData = {
        day:day,
        month:month,
        year:year
    };
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(json) {
        if (json.length == 3) {
            try {
                initRunningStatValue(json, type);
            }
            catch(e) {

            }
        }
    });
}

function initRunningStatValue(jsonStr, type) {
    if (jsonStr == null || jsonStr.length < 3) {
        return;
    }

    if (type == "incomeDay") {
        $("#dayIncomeTotal").text(jsonStr[0].incomeSum);
        $("#dayIncomeCash").text(jsonStr[0].cashIncome);
        $("#dayIncomeUnionPay").text(jsonStr[0].unionPayIncome);
        $("#dayIncomeCheque").text(jsonStr[0].chequeIncome);
        $("#dayIncomeMemberPay").text(jsonStr[0].memberPayIncome);
        $("#dayIncomeDebt").text(jsonStr[0].debtNewIncome);
        $("#dayCustomerDepositExpenditure").text(jsonStr[0].customerDepositExpenditure); // add by zhuj
        return;
    }

    if (type == "incomeMonth") {
        $("#monthIncomeTotal").text(jsonStr[1].incomeSum);
        $("#monthIncomeCash").text(jsonStr[1].cashIncome);
        $("#monthIncomeUnionPay").text(jsonStr[1].unionPayIncome);
        $("#monthIncomeCheque").text(jsonStr[1].chequeIncome);
        $("#monthIncomeMemberPay").text(jsonStr[1].memberPayIncome);
        $("#monthIncomeDebt").text(jsonStr[1].debtNewIncome);
        $("#monthCustomerDepositExpenditure").text(jsonStr[1].customerDepositExpenditure); // add by zhuj
        return;
    }

    if (type == "expenditureDay") {
        $("#dayExpenditureTotal").text(jsonStr[0].expenditureSum);
        $("#dayExpenditureCash").text(jsonStr[0].cashExpenditure);
        $("#dayExpenditureUnionPay").text(jsonStr[0].unionPayExpenditure);
        $("#dayExpenditureCheque").text(jsonStr[0].chequeExpenditure);
        $("#dayExpenditureDeposit").text(jsonStr[0].depositPayExpenditure);
        $("#dayExpenditureDebt").text(jsonStr[0].debtNewExpenditure);
        return;
    }

    if (type == "expenditureMonth") {
        $("#monthExpenditureTotal").text(jsonStr[1].expenditureSum);
        $("#monthExpenditureCash").text(jsonStr[1].cashExpenditure);
        $("#monthExpenditureUnionPay").text(jsonStr[1].unionPayExpenditure);
        $("#monthExpenditureCheque").text(jsonStr[1].chequeExpenditure);
        $("#monthExpenditureDeposit").text(jsonStr[1].depositPayExpenditure);
        return;
    }



    $("#dayRunningSum").text(jsonStr[0].runningSum);      //日月年收支结余
    $("#monthRunningSum").text(jsonStr[1].runningSum);
    $("#yearRunningSum").text(jsonStr[2].runningSum);

    $("#dayCashIncome").text(jsonStr[0].cashIncome);     //日月年现金收入
    $("#monthCashIncome").text(jsonStr[1].cashIncome);
    $("#yearCashIncome").text(jsonStr[2].cashIncome);

    $("#dayUnionPayIncome").text(jsonStr[0].unionPayIncome);   //日月年银联收入
    $("#monthUnionPayIncome").text(jsonStr[1].unionPayIncome);
    $("#yearUnionPayIncome").text(jsonStr[2].unionPayIncome);

    $("#dayChequeIncome").text(jsonStr[0].chequeIncome);       //日月年支票收入
    $("#monthChequeIncome").text(jsonStr[1].chequeIncome);
    $("#yearChequeIncome").text(jsonStr[2].chequeIncome);

    $("#dayCouponIncome").text(jsonStr[0].couponIncome);       //日月年代金券收入
    $("#monthCouponIncome").text(jsonStr[1].couponIncome);
    $("#yearCouponIncome").text(jsonStr[2].couponIncome);

    $("#dayCashExpenditure").text(jsonStr[0].cashExpenditure);          //日月年现金支出
    $("#monthCashExpenditure").text(jsonStr[1].cashExpenditure);
    $("#yearCashExpenditure").text(jsonStr[2].cashExpenditure);

    $("#dayUnionPayExpenditure").text(jsonStr[0].unionPayExpenditure);   //日月年银联支出
    $("#monthUnionPayExpenditure").text(jsonStr[1].unionPayExpenditure);
    $("#yearUnionPayExpenditure").text(jsonStr[2].unionPayExpenditure);

    $("#dayChequeExpenditure").text(jsonStr[0].chequeExpenditure);      //日月年支票支出
    $("#monthChequeExpenditure").text(jsonStr[1].chequeExpenditure);
    $("#yearChequeExpenditure").text(jsonStr[2].chequeExpenditure);

    //$("#dayCouponExpenditure").text(jsonStr[0].couponExpenditure);      //日月年代金券支出
    //$("#monthCouponExpenditure").text(jsonStr[1].couponExpenditure);
    //$("#yearCouponExpenditure").text(jsonStr[2].couponExpenditure);

    $("#customerTotalPayableSpan")
        .html('客户<span class="arialFont">¥</span>' + jsonStr[0].customerTotalPayable);//客户应付款
    $("#customerTotalReceivableSpan")
        .html('客户<span class="arialFont">¥</span>' + jsonStr[0].customerTotalReceivable);//客户应收款
    $("#supplierTotalPayableSpan")
        .html('供应商<span class="arialFont">¥</span>' + jsonStr[0].supplierTotalPayable); //供应商应付款
    $("#supplierTotalReceivableSpan")
        .html('供应商<span class="arialFont">¥</span>' + jsonStr[0].supplierTotalReceivable);//供应商应收款

    $("#dayIncomeTotal").text(jsonStr[0].incomeSum);      //客户交易明细 日月年收入总额
    $("#monthIncomeTotal").text(jsonStr[1].incomeSum);
    $("#yearIncomeTotal").text(jsonStr[2].incomeSum);

    $("#dayIncomeCash").text(jsonStr[0].cashIncome);     //客户交易明细 日月年现金收入总计
    $("#monthIncomeCash").text(jsonStr[1].cashIncome);
    $("#yearIncomeCash").text(jsonStr[2].cashIncome);

    $("#dayIncomeUnionPay").text(jsonStr[0].unionPayIncome);  //客户交易明细 日月年银联收入总计
    $("#monthIncomeUnionPay").text(jsonStr[1].unionPayIncome);
    $("#yearIncomeUnionPay").text(jsonStr[2].unionPayIncome);

    $("#dayIncomeCheque").text(jsonStr[0].chequeIncome);      //客户交易明细 日月年支票收入总计
    $("#monthIncomeCheque").text(jsonStr[1].chequeIncome);
    $("#yearIncomeCheque").text(jsonStr[2].chequeIncome);

    $("#dayIncomeCoupon").text(jsonStr[0].couponIncome);      //客户交易明细 日月年代金券收入总计
    $("#monthIncomeCoupon").text(jsonStr[1].couponIncome);
    $("#yearIncomeCoupon").text(jsonStr[2].couponIncome);

    $("#dayIncomeMemberPay").text(jsonStr[0].memberPayIncome);  //客户交易明细 日月年会员储值总计
    $("#monthIncomeMemberPay").text(jsonStr[1].memberPayIncome);
    $("#yearIncomeMemberPay").text(jsonStr[2].memberPayIncome);

    $("#dayIncomeDebt").text(jsonStr[0].debtNewIncome);         //客户交易明细 日月年新增欠款总计
    $("#monthIncomeDebt").text(jsonStr[1].debtNewIncome);
    $("#yearIncomeDebt").text(jsonStr[2].debtNewIncome);

    $("#dayExpenditureTotal").text(jsonStr[0].expenditureSum);    //供应商交易明细 日月年支出总额
    $("#monthExpenditureTotal").text(jsonStr[1].expenditureSum);
    $("#yearExpenditureTotal").text(jsonStr[2].expenditureSum);

    $("#dayExpenditureCash").text(jsonStr[0].cashExpenditure);  //供应商交易明细 日月年现金支出总计
    $("#monthExpenditureCash").text(jsonStr[1].cashExpenditure);
    $("#yearExpenditureCash").text(jsonStr[2].cashExpenditure);

    $("#dayExpenditureUnionPay").text(jsonStr[0].unionPayExpenditure); //供应商交易明细 日月年银联支出总计
    $("#monthExpenditureUnionPay").text(jsonStr[1].unionPayExpenditure);
    $("#yearExpenditureUnionPay").text(jsonStr[2].unionPayExpenditure);

    $("#dayExpenditureCheque").text(jsonStr[0].chequeExpenditure);   //供应商交易明细 日月年支票支出总计
    $("#monthExpenditureCheque").text(jsonStr[1].chequeExpenditure);
    $("#yearExpenditureCheque").text(jsonStr[2].chequeExpenditure);

    //$("#dayExpenditureCoupon").text(jsonStr[0].couponExpenditure);   //供应商交易明细 日月年代金券支出总计
    //$("#monthExpenditureCoupon").text(jsonStr[1].couponExpenditure);
    //$("#yearExpenditureCoupon").text(jsonStr[2].couponExpenditure);

    $("#dayExpenditureDeposit").text(jsonStr[0].depositPayExpenditure);//供应商交易明细 日月年用预付款总计
    $("#monthExpenditureDeposit").text(jsonStr[1].depositPayExpenditure);
    $("#yearExpenditureDeposit").text(jsonStr[2].depositPayExpenditure);

    $("#dayExpenditureDebt").text(jsonStr[0].debtNewExpenditure); //供应商交易明细 日月年新增欠款支出总计
    $("#monthExpenditureDebt").text(jsonStr[1].debtNewExpenditure);
    $("#yearExpenditureDebt").text(jsonStr[2].debtNewExpenditure);

    $("#dayCustomerDepositExpenditure").text(jsonStr[0].customerDepositExpenditure);//客户交易明细 日月年用预收款收入总计
    $("#monthCustomerDepositExpenditure").text(jsonStr[1].customerDepositExpenditure);
    $("#yearCustomerDepositExpenditure").text(jsonStr[2].customerDepositExpenditure);

    $("#dayDebtNew").text(jsonStr[0].debtNewIncome);              //日月年本期新增支出总计
    $("#monthDebtNew").text(jsonStr[1].debtNewIncome);
    $("#yearDebtNew").text(jsonStr[2].debtNewIncome);

    $("#dayDebtWithdrawal").text(jsonStr[0].debtWithdrawalIncome);  //日月年本期回笼支出总计
    $("#monthDebtWithdrawal").text(jsonStr[1].debtWithdrawalIncome);
    $("#yearDebtWithdrawal").text(jsonStr[2].debtWithdrawalIncome);

    $("#dayDebtDiscount").text(jsonStr[0].customerDebtDiscount); //日月年欠款优惠支出总计
    $("#monthDebtDiscount").text(jsonStr[1].customerDebtDiscount);
    $("#yearDebtDiscount").text(jsonStr[2].customerDebtDiscount);


}


function getDate(typeDate, type) {
    var oldMonth = ($("#monthHid").val()) * 1,
        oldYear = ($("#yearHid").val()) * 1;

    initDate("runningStat");

    $("#dayHid").val($("#selectDay").val());
    $("#monthHid").val($("#selectMonth").val());
    $("#yearHid").val($("#selectYear").val());

    $("#selectDayHid").val($("#selectDay").val());
    $("#selectMonthHid").val($("#selectMonth").val());
    $("#selectYearHid").val($("#selectYear").val());

    var dayHid = ($("#dayHid").val()) * 1,
        monthHid = ($("#monthHid").val()) * 1,
        yearHid = ($("#yearHid").val()) * 1;

    myHighChart.changeDateTime(oldMonth, oldYear);

    initRunningStatDate(yearHid, monthHid, dayHid, "normal");
    initIncomeRunningStatInfoDay(dayHid, monthHid, yearHid, "day", defaultArrayType);
    initExpenditureRunningStatInfoDay(dayHid, monthHid, yearHid, "day", defaultArrayType);

    if (oldMonth != monthHid || oldYear != yearHid) {
        initIncomeRunningStatInfoMonth(dayHid, monthHid, yearHid, "day", defaultArrayType);
        initExpenditureRunningStatInfoMonth(dayHid, monthHid, yearHid, "day", defaultArrayType);
    }
    if (oldYear != yearHid) {
        initIncomeRunningStatInfoYear(dayHid, monthHid, yearHid, "month", defaultArrayType);
        initExpenditureRunningStatInfoYear(dayHid, monthHid, yearHid, "day", defaultArrayType);
    }
}

function initIncomeRunningStatInfoDay(day, month, year, type, arrayType) {
    $("#runningInfoDay tr:not(:first)").remove();

    var date = "" + year + "-" + month + "-" + day;
    queryType = type;
    defaultArrayType = arrayType;
    var str = 'runningStat.do?method=getIncomeDetailByDay';
    APP_BCGOGO.Net.asyncAjax({
               url:"runningStat.do?method=getIncomeDetailByDay",
               data:{startPageNo:1,maxRows:10,type:type,dateStr:date,arrayType:arrayType},
               cache:false,
               dataType:"json",
               success:function(json) {
                   initDayIncomeStat(json);
                   initPages(json, "dynamical1", str, '', "initDayIncomeStat", '', '',
                             {startPageNo:'1',maxRows:10,type:type,dateStr:jQuery('#selectYearHid').val() + '-' + jQuery('#selectMonthHid').val() + '-' + jQuery('#selectDayHid').val(),arrayType:arrayType},
                             '');
               }
           });
}

function initIncomeRunningStatInfoMonth(day, month, year, type, arrayType) {
    $("#runningInfoMonth tr:not(:first)").remove();
    var date = "" + year + "-" + month + "-" + day;
    queryType = type;
    defaultArrayType = arrayType;
    APP_BCGOGO.Net.asyncAjax({
               url:"runningStat.do?method=getIncomeDetailByMonth",
               data:{startPageNo:1,maxRows:12,type:type,dateStr:date,arrayType:arrayType},
               cache:false,
               dataType:"json",
               success:function(json) {
                   initMonthIncomeStat(json);
                   initPages(json, "dynamical2", "runningStat.do?method=getIncomeDetailByMonth", '',
                             "initMonthIncomeStat", '', '',
                             {startPageNo:'1',maxRows:12,type:type,dateStr:jQuery('#selectYearHid').val() + '-' + jQuery('#selectMonthHid').val() + '-' + jQuery('#selectDayHid').val(),arrayType:arrayType},
                             '');
               }
           });
}

function initIncomeRunningStatInfoYear(day, month, year, type, arrayType) {
    $("#runningInfoYear tr:not(:first)").remove();
    var date = "" + year + "-" + month + "-" + day;
    queryType = type;
    defaultArrayType = arrayType;
    APP_BCGOGO.Net.asyncAjax({
               url:"runningStat.do?method=getIncomeDetailByYear",
               data:{startPageNo:1,maxRows:12,type:type,dateStr:date,arrayType:arrayType},
               cache:false,
               dataType:"json",
               success:function(json) {
                   initYearIncomeStat(json);
                   initPages(json, "dynamical3", "runningStat.do?method=getIncomeDetailByYear", '',
                             "initYearIncomeStat", '', '',
                             {startPageNo:'1',maxRows:12,type:type,dateStr:jQuery('#yearHid').val() + '-' + jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val(),arrayType:arrayType},
                             '');
               }
           });
}

function initExpenditureRunningStatInfoDay(day, month, year, type, arrayType) {
    $("#expenditureInfoDay tr:not(:first)").remove();
    var date = "" + year + "-" + month + "-" + day;
    queryType = type;
    defaultArrayType = arrayType;
    APP_BCGOGO.Net.asyncAjax({
               url:"runningStat.do?method=getExpenditureDetailByDay",
               data:{startPageNo:1,maxRows:10,type:type,dateStr:date,arrayType:arrayType},
               cache:false,
               dataType:"json",
               success:function(json) {
                   initDayExpenditureStat(json);
                   initPages(json, "dynamical4", "runningStat.do?method=getExpenditureDetailByDay", '',
                             "initDayExpenditureStat", '', '',
                             {startPageNo:'1',maxRows:10,type:type,dateStr:jQuery('#selectYearHid').val() + '-' + jQuery('#selectMonthHid').val() + '-' + jQuery('#selectDayHid').val(),arrayType:arrayType},
                             '');
               }
           });
}

function initExpenditureRunningStatInfoMonth(day, month, year, type, arrayType) {
    $("#expenditureInfoMonth tr:not(:first)").remove();
    var date = "" + year + "-" + month + "-" + day;
    queryType = type;
    defaultArrayType = arrayType;
    APP_BCGOGO.Net.asyncAjax({
               url:"runningStat.do?method=getExpenditureDetailByMonth",
               data:{startPageNo:1,maxRows:12,type:type,dateStr:date,arrayType:arrayType},
               cache:false,
               dataType:"json",
               success:function(json) {
                   initMonthExpenditureStat(json);
                   initPages(json, "dynamical5", "runningStat.do?method=getExpenditureDetailByMonth", '',
                             "initMonthExpenditureStat", '', '',
                             {startPageNo:'1',maxRows:12,type:type,dateStr:jQuery('#selectYearHid').val() + '-' + jQuery('#selectMonthHid').val() + '-' + jQuery('#selectDayHid').val(),arrayType:arrayType},
                             '');
               }
           });
}

function initExpenditureRunningStatInfoYear(day, month, year, type, arrayType) {
    $("#expenditureInfoYear tr:not(:first)").remove();
    var date = "" + year + "-" + month + "-" + day;
    queryType = type;
    defaultArrayType = arrayType;
    APP_BCGOGO.Net.asyncAjax({
               url:"runningStat.do?method=getExpenditureDetailByYear",
               data:{startPageNo:1,maxRows:12,type:type,dateStr:date,arrayType:arrayType},
               cache:false,
               dataType:"json",
               success:function(json) {
                   initYearExpenditureStat(json);
                   initPages(json, "dynamical6", "runningStat.do?method=getExpenditureDetailByYear", '',
                             "initYearExpenditureStat", '', '',
                             {startPageNo:'1',maxRows:12,type:type,dateStr:jQuery('#yearHid').val() + '-' + jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val(),arrayType:arrayType},
                             '');
               }
           });
}
function initDate(statType) {
    var newDay = $("#selectDay").val() + "日";
    var newMonth = $("#selectMonth").val() + "月";
    var newYear = $("#selectYear").val() + "年";

    if (statType = "runningStat") {
        $("#currentDayIncome").text(newMonth + newDay);
        $("#currentMonthIncome").text(newYear + newMonth);
        $("#currentYearIncome").text(newYear);

        $("#currentDayExpenditure").text(newMonth + newDay);
        $("#currentMonthExpenditure").text(newYear + newMonth);
        $("#currentYearExpenditure").text(newYear);
    }
}