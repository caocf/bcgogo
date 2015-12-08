/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-5-7
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */

var nextPageNo = 1,
    isTheLastPage = false,
    pageSize = 5;
var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;
var myHighChart;

$(function() {
    myHighChart = new HighChartFunction({
        title: '营业收入统计',
        url: 'businessStat.do?method=getDataTable',
        render: 'chart_div'
    });

    var dayHid = ($("#dayHid").val()) * 1,
        monthHid = ($("#monthHid").val()) * 1,
        yearHid = ($("#yearHid").val()) * 1;
    reportForm(dayHid, monthHid, yearHid);

    $("#dayStatSum").bind('click', function() {
        var type = "day";
        initTable(type);
    });

    $("#monthStatSum").bind('click', function() {
        var type = "month";
        initTable(type);
    });

    $("#yearStatSum").bind('click', function() {
        var type = "year";
        initTable(type);
    });

    $("#submitForm").click(function() {
        $("#myForm").submit();
    });
    $("#myForm").submit(function() {
        $(this).ajaxSubmit();
        return false;
    });

    $("#printBusinessStat").click(function() {
        bcgogo.checksession({
            "parentWindow": window.parent,
            'iframe_PopupBox': $("#showqueryDate")[0],
            'src': "businessStat.do?method=createQueryDate"
        });
    });

    $("#dayStatSum,#monthStatSum,#yearStatSum,.bus_stock .busTable tr td.clickNum").hover(function(){
        $(this).css({"color":"#fd5300","textDecoration":"underline"});
    },function(){
        $(this).css({"color":"#6699cc","textDecoration":"none"});
    });

    $("#otherExpenditureDiv").css("display","none");
    $("#otherIncomeDiv").css("display","none");


//    for(var i = 0; i < $("#expenditureTable")[0].rows.length; i++) {
//        $("#busTable")[0].rows[i].style.display = "none";
//    }
    $("#otherExpenditureArrow").click(function() {

      if($("#otherExpenditureDiv").css("display") =="none"){
        $("#otherExpenditureDiv").css("display","block");
        $("#otherExpenditureArrow").removeClass("downArrow").addClass("upArrow");

//        $("#otherExpenditureArrow").style.backgroundImage = "url(images/btn_topp.png)";
      }else{
//        $("#otherExpenditureDiv").style.display ="none";
//        $("#otherExpenditureArrow").style.backgroundImage = "url(images/btn_la.png)";
        $("#otherExpenditureDiv").css("display","none");
        $("#otherExpenditureArrow").removeClass("upArrow").addClass("downArrow");
      }
//        for(var i = 0; i < $("#expenditureTable")[0].rows.length; i++) {
//            $("#expenditureTable")[0].rows[i].style.display = $("#expenditureTable")[0].rows[i].style.display == "none" ? '' : "none";
//          $("#otherExpenditureArrow")[0].style.backgroundImage = $("#expenditureTable")[0].rows[i].style.display == "none" ? "url(images/btn_la.png)" : "url(images/btn_topp.png)";
//
//        }

    });

  $("#otherIncomeArrow").click(function() {

    if($("#otherIncomeDiv").css("display") =="none"){
      $("#otherIncomeDiv").css("display","block");
      $("#otherIncomeArrow").removeClass("downArrow").addClass("upArrow");
//      $("#otherIncomeDiv").style.backgroundImage = "url(images/btn_topp.png)";
    }else{
      $("#otherIncomeDiv").css("display","none");
      $("#otherIncomeArrow").removeClass("upArrow").addClass("downArrow");
    }
  });

    $("#timeSort").click(function() {
        $("#timeSort")[0].className = $("#timeSort")[0].className == "Descending" ? "Ascending" : "Descending";
        var arrayType = "timeDesc";
        if($("#timeSort").attr('class') == "Descending") {
            arrayType = "timeDesc";
        } else {
            arrayType = "timeAsc";
        }
        var dayHid = ($("#dayHid").val()) * 1;
        var monthHid = ($("#monthHid").val()) * 1;
        var yearHid = ($("#yearHid").val()) * 1;
        initRepairTable(dayHid, monthHid, yearHid, queryType, arrayType);
    });
    $("#brandSort").click(function() {
        $("#brandSort")[0].className = $("#brandSort")[0].className == "Descending" ? "Ascending" : "Descending";
        var arrayType = "moneyDesc";
        if($("#brandSort").attr('class') == "Descending") {
            arrayType = "moneyDesc";
        } else {
            arrayType = "moneyAsc";
        }
        var dayHid = ($("#dayHid").val()) * 1;
        var monthHid = ($("#monthHid").val()) * 1;
        var yearHid = ($("#yearHid").val()) * 1;
        initRepairTable(dayHid, monthHid, yearHid, queryType, arrayType);
    });
    $("#timeSort2").click(function() {
        $("#timeSort2")[0].className = $("#timeSort2")[0].className == "Descending" ? "Ascending" : "Descending";
        var arrayType = "timeDesc";
        if($("#timeSort2")[0].className == "Descending") {
            arrayType = "timeDesc";
        } else {
            arrayType = "timeAsc";
        }
        var dayHid = ($("#dayHid").val()) * 1;
        var monthHid = ($("#monthHid").val()) * 1;
        var yearHid = ($("#yearHid").val()) * 1;
        initSaleTable(dayHid, monthHid, yearHid, queryType, arrayType);
    });
    $("#brandSort2").click(function() {
        $("#brandSort2")[0].className = $("#brandSort2")[0].className == "Ascending" ? "Descending" : "Ascending";
        var arrayType = "moneyDesc";
        if($("#brandSort2")[0].className == "Descending") {
            arrayType = "moneyDesc";
        } else {
            arrayType = "moneyAsc";
        }
        var dayHid = ($("#dayHid").val()) * 1;
        var monthHid = ($("#monthHid").val()) * 1;
        var yearHid = ($("#yearHid").val()) * 1;
        initSaleTable(dayHid, monthHid, yearHid, queryType, arrayType);

    });
    $("#timeSort3").click(function() {
        $("#timeSort3")[0].className = $("#timeSort3")[0].className == "Descending" ? "Ascending" : "Descending";
        var arrayType = "timeDesc";
        if($("#timeSort3")[0].className == "Descending") {
            arrayType = "timeDesc";
        } else {
            arrayType = "timeAsc";
        }
        var dayHid = ($("#dayHid").val()) * 1;
        var monthHid = ($("#monthHid").val()) * 1;
        var yearHid = ($("#yearHid").val()) * 1;
        initWashTable(dayHid, monthHid, yearHid, queryType, arrayType);
    });
    $("#brandSort3").click(function() {
        $("#brandSort3")[0].className = $("#brandSort3")[0].className == "Descending" ? "Ascending" : "Descending";
        var arrayType = "moneyDesc";
        if($("#brandSort3")[0].className == "Descending") {
            arrayType = "moneyDesc";
        } else {
            arrayType = "moneyAsc";
        }
        var dayHid = ($("#dayHid").val()) * 1;
        var monthHid = ($("#monthHid").val()) * 1;
        var yearHid = ($("#yearHid").val()) * 1;
        initWashTable(dayHid, monthHid, yearHid, queryType, arrayType);
    });


    $("#serviceTitle").click(function() {
        if($("#serviceTitle")[0]) $("#serviceTitle")[0].className = "big_hover_title";
        if($("#goodsSaleTitle")[0]) $("#goodsSaleTitle").attr("class", "big_unhover_title");
        if($("#carWashTitle")[0]) $("#carWashTitle")[0].className = "big_unhover_title";
        if($("#memberTitle")[0]) {
            $("#memberTitle")[0].className = "big_unhover_title";
        }
        if($("#serviceInfo")[0]) $("#serviceInfo")[0].style.display = "";
        if($("#goodsSaleInfo")[0]) $("#goodsSaleInfo")[0].style.display = "none";
        if($("#carWashInfo")[0]) $("#carWashInfo").css({
            "display": "none"
        });
        pageType = "repair";
        tableUtil.limitSpanWidth($(".customer", "#repairTable"), 10);
    });

    $("#goodsSaleTitle").click(function() {
        if($("#serviceTitle")[0]) $("#serviceTitle")[0].className = "big_unhover_title";
        if($("#goodsSaleTitle")[0]) {
            $("#goodsSaleTitle").attr("class", "big_hover_title");
            $("#goodsSaleInfo")[0].style.display = "";
        }
        if($("#carWashTitle")[0]) {
            $("#carWashTitle")[0].className = "big_unhover_title";
            $("#carWashInfo").css({
                "display": "none"
            });
        }
        if($("#memberTitle")[0]) {
            $("#memberTitle")[0].className = "big_unhover_title";
        }
        if($("#serviceInfo")[0]) {
            $("#serviceInfo").css({
                "display": "none"
            });
        }
        pageType = "sale";
        tableUtil.limitSpanWidth($(".customer","#salesOrder"),10);
    });
    $("#carWashTitle").click(function() {
        if($("#serviceTitle")[0]) $("#serviceTitle")[0].className = "big_unhover_title";
        if($("#goodsSaleTitle")[0]) $("#goodsSaleTitle").attr("class", "big_unhover_title");
        if($("#memberTitle")[0]) $("#memberTitle")[0].className = "big_unhover_title";
        if($("#carWashTitle")[0]) $("#carWashTitle")[0].className = "big_hover_title";
        if($("#serviceInfo")[0]) $("#serviceInfo").css({
            "display": "none"
        });
        if($("#goodsSaleInfo")[0]) $("#goodsSaleInfo").css({
            "display": "none"
        });
        if($("#carWashInfo")[0]) $("#carWashInfo")[0].style.display = "";
        pageType = "wash";
    });

    $("#memberTitle").click(function() {
        if($("#serviceTitle")[0]) $("#serviceTitle")[0].className = "big_unhover_title";
        if($("#goodsSaleTitle")[0]) $("#goodsSaleTitle")[0].className = "big_unhover_title";
        if($("#memberTitle")[0]) $("#memberTitle")[0].className = "big_unhover_title";
        if($("#carWashTitle")[0]) $("#carWashTitle")[0].className = "big_unhover_title";
        if($("#memberTitle")[0]) $("#memberTitle")[0].className = "big_hover_title";
        if($("#serviceInfo")[0]) $("#serviceInfo").css({
            "display": "none"
        });
        if($("#goodsSaleInfo")[0]) $("#goodsSaleInfo").css({
            "display": "none"
        });
        if($("#carWashInfo")[0]) $("#carWashInfo").css({
            "display": "none"
        });
        if($("#cardInfo")[0]) $("#cardInfo")[0].style.display = "";
    });

    $("#memberStatSpan").click(function() {
        window.location.href = "member.do?method=memberStat";
    });

    $("#runningStatSum").click(function() {
        window.location.href = "runningStat.do?method=getRunningStat";
    });
    $("#runningStatSum,#memberStatSpan,#printBusinessStat,#print,#print2,#print3").hover(function(){
        $(this).css("color","#FD5300");
    },function(){
        $(this).css("color","#6699cc");
    });
    initRunningStatSum();

    $("#export").click(function(){
        $(this).attr("disabled",true);
        $("#exporting").css("display","");
       var dateStr = jQuery('#yearHid').val() + '-' + jQuery('#monthHid').val() + '-' + jQuery('#dayHid').val();
       var url = "export.do?method=exportBusinessStat";
        if(pageType == 'repair') {
            totalNum = $("#totalRepairNum").val();
        } else if(pageType == 'sale') {
            totalNum = $("#totalSalesNum").val();
        } else if(pageType == 'wash') {
            totalNum = $("#totalWashNum").val();
        }
        if(totalNum == 0) {
            nsDialog.jAlert("对不起，暂无数据，无法导出！");
            $(this).removeAttr("disabled");
            $("#exporting").css("display","none");
            return;
        }
       var data = {
           pageType:pageType,
           arrayType:arrayType1,
           type:queryType,
           dateStr:dateStr,
           totalNum:totalNum
       };
        bcgogoAjaxQuery.setUrlData(url, data);
        bcgogoAjaxQuery.ajaxQuery(function (json) {
            $("#export").removeAttr("disabled");
            $("#exporting").css("display","none");
            if(json && json.exportFileDTOList) {
                if(json.exportFileDTOList.length > 1) {
                    showDownLoadUI(json);
                } else {
                    var toExportFileName = '';
                    if(json.exportScene == 'REPAIR_BUSINESS_STAT') {
                        toExportFileName = '车辆施工营业统计.xls';
                    } else if(json.exportScene == 'SALES_BUSINESS_STAT') {
                        toExportFileName = '商品销售营业统计.xls';
                    }  else if(json.exportScene == 'WASH_BUSINESS_STAT') {
                        toExportFileName = '洗车营业统计.xls';
                    }
                    window.open("download.do?method=downloadExportFile&exportFileName=" + toExportFileName + "&exportFileId=" + json.exportFileDTOList[0].idStr);
                }
            }

        });
    });
});

function initRunningStatSum() {

    var dayHid = ($("#dayHid").val()) * 1,
        monthHid = ($("#monthHid").val()) * 1,
        yearHid = ($("#yearHid").val()) * 1;

    var ajaxUrl = "runningStat.do?method=getRunningStatDate";
    var ajaxData = {
        day: dayHid,
        month: monthHid,
        year: yearHid
    };
    bcgogoAjaxQuery.setUrlData(ajaxUrl, ajaxData);
    bcgogoAjaxQuery.ajaxQuery(function(jsonStr) {
        if(jsonStr.length == 3) {
            try {
                $("#dayRunningSum").text(jsonStr[0].runningSum);
            } catch(e) {

            }
        }
    });
}


// author:zhangjuntao
var time = new Array(),
    timeFlag = true;
time[0] = new Date().getTime();
time[1] = new Date().getTime();

function notOpen() {
    var reg = /^(\d+)$/;
    time[1] = new Date().getTime();
    if(time[1] - time[0] > 3000 || timeFlag) {
        time[0] = time[1];
        timeFlag = false;
        showMessage.fadeMessage("35%", "40%", "slow", 3000, "此功能稍后开放！"); // top left fadeIn fadeOut message
    }

}

function getDate(type) {
    var oldMonth = ($("#monthHid").val()) * 1,
        oldYear = ($("#yearHid").val()) * 1;

    $("#dayHid").val($("#selectDay").val());
    $("#monthHid").val($("#selectMonth").val());
    $("#yearHid").val($("#selectYear").val());
    $("#queryType").val($("#selectDay").val());

    var dayHid = ($("#dayHid").val()) * 1,
        monthHid = ($("#monthHid").val()) * 1,
        yearHid = ($("#yearHid").val()) * 1;

    myHighChart.changeDateTime(oldMonth, oldYear);

    $("#yearStatSum").bind('click', function() {
        var type = "year";
        initTable(type);
    });

    reportForm(dayHid, monthHid, yearHid);

    if($("#serviceTitle").css("display") == "block") {
        initRepairTable(dayHid, monthHid, yearHid, type, arrayType1);
    }
    if($("#carWashTitle").css("display") == "block") {
        initWashTable(dayHid, monthHid, yearHid, type, arrayType1);
    }
    initSaleTable(dayHid, monthHid, yearHid, type, arrayType1);
}

function checkNumInTwo(num) {
    var reg = /^\d+\.?\d{0,2}$/;
    return reg.exec(num);
}

function reportForm(day, month, year) {
    $.ajax({
        url: "businessStat.do?method=customerResponse",
        type: "POST",
        dataType: "json",
        data: {
            day: day,
            month: month,
            year: year
        },
        success: function(jsonStr) {
            try {
                initRowsValue(jsonStr);
            } catch(e) {
                //alert("初始化数据失败!!");
                initColumns();
            }
        },
        error: function(jsonStr) {}
    });
}

function initRowsValue(jsonStr) {

    //日
    var dayStatSum = (jsonStr[0].statSum).toFixed(2);
    var daySales = (jsonStr[0].sales).toFixed(2);
    var dayWash = (jsonStr[0].wash).toFixed(2);
    var dayService = (jsonStr[0].service).toFixed(2);
    var dayProductCost = (jsonStr[0].productCost).toFixed(2);
    var dayMemberIncome = (jsonStr[0].memberIncome).toFixed(2);
    var dayOtherIncome = (jsonStr[0].otherIncome).toFixed(2);

    var dayOrderOtherIncomeCost = (jsonStr[0].orderOtherIncomeCost).toFixed(2);

//    //日 营业外支出
//    var dayRentHid = (jsonStr[0].rentExpenditure).toFixed(2);
//    var dayLaborHid = (jsonStr[0].salaryExpenditure).toFixed(2);
//    var dayOtherHid = (jsonStr[0].utilitiesExpenditure).toFixed(2);
//    var dayStatOtherId = (jsonStr[0].otherExpenditure).toFixed(2);

    //月
    var monthStatSum = (jsonStr[1].statSum).toFixed(2);
    var monthSales = (jsonStr[1].sales).toFixed(2);
    var monthWash = (jsonStr[1].wash).toFixed(2);
    var monthService = (jsonStr[1].service).toFixed(2);
    var monthProductCost = (jsonStr[1].productCost).toFixed(2);
    var monthMemberIncome = (jsonStr[1].memberIncome).toFixed(2);
    var monthOtherIncome = (jsonStr[1].otherIncome).toFixed(2);
    var monthOrderOtherIncomeCost = (jsonStr[1].orderOtherIncomeCost).toFixed(2);


//    //月 营业外支出
//    var monthRentHid = (jsonStr[1].rentExpenditure).toFixed(2);
//    var monthLaborHid = (jsonStr[1].salaryExpenditure).toFixed(2);
//    var monthOtherHid = (jsonStr[1].utilitiesExpenditure).toFixed(2);
//    var monthStatOtherId = (jsonStr[1].otherExpenditure).toFixed(2);

    //年
    var yearStatSum = (jsonStr[2].statSum).toFixed(2);
    var yearSales = (jsonStr[2].sales).toFixed(2);
    var yearWash = (jsonStr[2].wash).toFixed(2);
    var yearService = (jsonStr[2].service).toFixed(2);
    var yearProductCost = (jsonStr[2].productCost).toFixed(2);
    var yearMemberIncome = (jsonStr[2].memberIncome).toFixed(2);
    var yearOtherIncome = (jsonStr[2].otherIncome).toFixed(2);
    var yearOrderOtherIncomeCost = (jsonStr[2].orderOtherIncomeCost).toFixed(2);


//    //年 营业外支出
//    var yearRentHid = (jsonStr[2].rentExpenditure).toFixed(2);
//    var yearLaborHid = (jsonStr[2].salaryExpenditure).toFixed(2);
//    var yearOtherHid = (jsonStr[2].utilitiesExpenditure).toFixed(2);
//    var yearStatOtherId = (jsonStr[2].otherExpenditure).toFixed(2);


    //日月年营业额
    var jStatSum = $(".statSum").children("div");
    $(jStatSum.eq(1)).text(dayStatSum);
    $(jStatSum.eq(2)).text(monthStatSum);
    $(jStatSum.eq(3)).text(yearStatSum);

    //会员卡
    $("#dayMemberStat").text(dayMemberIncome);
    $("#monthMemberStat").text(monthMemberIncome);
    $("#yearMemberStat").text(yearMemberIncome);

    //洗车
    var jWash = $(".wash").children("td");
    $(jWash.eq(1)).text(dayWash);
    $(jWash.eq(2)).text(monthWash);
    $(jWash.eq(3)).text(yearWash);

    //车辆施工
    var jService = $(".service").children("td");
    $(jService.eq(1)).text(dayService);
    $(jService.eq(2)).text(monthService);
    $(jService.eq(3)).text(yearService);

    //销售
    var jSales = $(".sales").children("td");
    $(jSales.eq(1)).text(daySales);
    $(jSales.eq(2)).text(monthSales);
    $(jSales.eq(3)).text(yearSales);

    //商品成本
    var jProductCost = $(".productCost").children("td");
    $(jProductCost.eq(1)).text(dayProductCost);
    $(jProductCost.eq(2)).text(monthProductCost);
    $(jProductCost.eq(3)).text(yearProductCost);

    //其他成本
    var jOrderOtherIncomeCost = $(".orderOtherCost").children("td");
    $(jOrderOtherIncomeCost.eq(1)).text(dayOrderOtherIncomeCost);
    $(jOrderOtherIncomeCost.eq(2)).text(monthOrderOtherIncomeCost);
    $(jOrderOtherIncomeCost.eq(3)).text(yearOrderOtherIncomeCost);

    //营业外收入
    $("#dayOtherIncome").text(dayOtherIncome);
    $("#monthOtherIncome").text(monthOtherIncome);
    $("#yearOtherIncome").text(yearOtherIncome);

//    //房租
//    var jRent = $(".rent").children("td");
//    $(jRent.eq(1)).text(dayRentHid);
//    $(jRent.eq(2)).text(monthRentHid);
//    $(jRent.eq(3)).text(yearRentHid);
//
//    //工资提成
//    var jLabor = $(".labor").children("td");
//    $(jLabor.eq(1)).text(dayLaborHid);
//    $(jLabor.eq(2)).text(monthLaborHid);
//    $(jLabor.eq(3)).text(yearLaborHid);
//
//    //水电杂项
//    var jOther = $(".other").children("td");
//    $(jOther.eq(1)).text(dayOtherHid);
//    $(jOther.eq(2)).text(monthOtherHid);
//    $(jOther.eq(3)).text(yearOtherHid);
//
//    //其他支出
//    var otherFee = $(".otherFee").children("td");
//    $(otherFee.eq(1)).text(dayStatOtherId);
//    $(otherFee.eq(2)).text(monthStatOtherId);
//    $(otherFee.eq(3)).text(yearStatOtherId);

    //营业外支出
    var jOutDetail = $(".outDetail").children("td");
    $(jOutDetail.eq(1)).text(jsonStr[0].otherExpenditureTotal);
    $(jOutDetail.eq(2)).text(jsonStr[1].otherExpenditureTotal);
    $(jOutDetail.eq(3)).text(jsonStr[2].otherExpenditureTotal);


    //毛利和毛利率
    var statSumValueDay = (parseFloat(dayStatSum) + parseFloat(dayOtherIncome)).toFixed(2);
    var statSumValueMonth = (parseFloat(monthStatSum) + parseFloat(monthOtherIncome)).toFixed(2);
    var statSumValueYear = (parseFloat(yearStatSum) + parseFloat(yearOtherIncome)).toFixed(2);

    var productCostDay = (parseFloat(dayProductCost) + parseFloat(dayOrderOtherIncomeCost)).toFixed(2);
    var productCostMonth = (parseFloat(monthProductCost) + parseFloat(monthOrderOtherIncomeCost)).toFixed(2);
    var productCostYear =(parseFloat(yearProductCost) + parseFloat(yearOrderOtherIncomeCost)).toFixed(2);

    var outDetailDay = ($(jOutDetail.eq(1)).text()) * 1;
    var outDetailMonth = ($(jOutDetail.eq(2)).text()) * 1;
    var outDetailYear = ($(jOutDetail.eq(3)).text()) * 1;

    initRate(statSumValueDay, productCostDay, outDetailDay, 'day');
    initRate(statSumValueMonth, productCostMonth, outDetailMonth, 'month');
    initRate(statSumValueYear, productCostYear, outDetailYear, 'year');

    $("#incomeTable tr").remove();

    var incomeList = jsonStr[0].incomeList;
    var expenditureList = jsonStr[0].expenditureList;

    if (incomeList.length > 0) {
      $("#otherIncomeDiv").css("height", incomeList.length > 5 ? 125 : incomeList.length * 25)
//      $("#otherIncomeArrow").css("display", "");

      var str = "";
      for (var i = 0; i < incomeList.length; i++) {
        var businessCategory = incomeList[i].businessCategory;
        var dayTotal = incomeList[i].dayTotal;
        var monthTotal = incomeList[i].monthTotal;
        var yearTotal = incomeList[i].yearTotal;

        str += '<tr>' +
            '<td style="padding-left:20px;">' + businessCategory + '</td>' +
            '<td class="busNums">' + dayTotal + '</td>' +
            '<td class="busNums addWidth">' + monthTotal + '</td>' +
            '<td class="busNums">' + yearTotal + '</td>' +
            '</tr>';
      }
      $("#incomeTable").append(str);
    } else {
      $("#otherIncomeDiv").css("display", "none");
      $("#otherIncomeDiv").css("height",0);
//      $("#otherIncomeArrow").css("display", "none");
    }

    $("#expenditureTable tr").remove();

    if (expenditureList.length > 0) {
      $("#otherExpenditureDiv").css("height", expenditureList.length > 5 ? 125 : expenditureList.length * 25)

      var str = "";
      for (var i = 0; i < expenditureList.length; i++) {
        var businessCategory = expenditureList[i].businessCategory;
        var dayTotal = expenditureList[i].dayTotal;
        var monthTotal = expenditureList[i].monthTotal;
        var yearTotal = expenditureList[i].yearTotal;

        str += '<tr>' +
            '<td style="padding-left:20px;">' + businessCategory + '</td>' +
            '<td class="busNums">' + dayTotal + '</td>' +
            '<td class="busNums addWidth">' + monthTotal + '</td>' +
            '<td class="busNums">' + yearTotal + '</td>' +
            '</tr>';
      }
      $("#expenditureTable").append(str);
    } else {
      $("#otherExpenditureDiv").css("display", "none");
      $("#otherExpenditureDiv").css("height",0);
    }

}

function initRate(total, productCost, out, type) {
    var rate = calculateRate(total, productCost, out);
    var rateCent = calculateRateCent(total, productCost, out);
    var jRate = $(".rate").children("td");
    var jRateCent = $(".rateCent").children("td");
    if(type == 'day') {
        $(jRate.eq(1)).text(rate);
        $(jRateCent.eq(1)).text(rateCent);
    }

    if(type == 'month') {
        $(jRate.eq(2)).text(rate);
        $(jRateCent.eq(2)).text(rateCent);
    }

    if(type == 'year') {
        $(jRate.eq(3)).text(rate);
        $(jRateCent.eq(3)).text(rateCent);
    }
}


function calculateRate(total, productCost, out) {
    return(total - out - productCost).toFixed(2);
}

function calculateRateCent(total, productCost, out) {
    if(total != 0) return(calculateRate(total, productCost, out) / total * 100).toFixed(1) + "%";
    return 0;

}



$(document).ready(function() {

    $("#submitForm").click(function() {
        $("#myForm").submit();
    });
    $("#myForm").submit(function() {
        $(this).ajaxSubmit();
        return false;
    });
});




function initColumns() {
    var jStatSum = $(".statSum").children("div");
    var jSales = $(".sales").children("td");
    var jWash = $(".wash").children("td");
    var jService = $(".service").children("td");
    var jProductCost = $(".productCost").children("td");
    var jOutDetail = $(".outDetail").children("td");
    var jRent = $(".rent").children("td");
    var jLabor = $(".labor").children("td");
    var jOther = $(".other").children("td");
    var jRate = $(".rate").children("td");
    var jRateCent = $(".rateCent").children("td");

    $(jStatSum.eq(1)).text("0.0");
    $(jStatSum.eq(2)).text("0.0");
    $(jStatSum.eq(3)).text("0.0");

    $(jSales.eq(1)).text("0.0");
    $(jSales.eq(2)).text("0.0");
    $(jSales.eq(3)).text("0.0");


    $(jWash.eq(1)).text("0.0");
    $(jWash.eq(2)).text("0.0");
    $(jWash.eq(3)).text("0.0");

    $(jService.eq(1)).text("0.0");
    $(jService.eq(2)).text("0.0");
    $(jService.eq(3)).text("0.0");

    $(jProductCost.eq(1)).text("0.0");
    $(jProductCost.eq(2)).text("0.0");
    $(jProductCost.eq(3)).text("0.0");

    $(jOutDetail.eq(1)).text("0.0");
    $(jOutDetail.eq(2)).text("0.0");
    $(jOutDetail.eq(3)).text("0.0");

    $("#dayTotalHid").val("0.0");
    $("#monthTotalHid").val("0.0");
    $("#yearTotalHid").val("0.0");


    $(jRent.eq(2).children(":text")).val("0.0");


    $("#dayRentHid").val("0.0");
    $("#monthRentHid").val("0.0");
    $("#yearRentHid").val("0.0");


    $(jLabor.eq(2).children(":text")).val("0.0");


    $("#dayLaborHid").val("0.0");
    $("#monthLaborHid").val("0.0");
    $("#yearLaborHid").val("0.0");


    $(jOther.eq(2).children(":text")).val("0.0");


    $("#dayOtherHid").val("0.0");
    $("#monthOtherHid").val("0.0");
    $("#yearOtherHid").val("0.0");

    $(jRate.eq(1)).text("0.0");
    $(jRateCent.eq(1)).text("0.0");

    $(jRate.eq(2)).text("0.0");
    $(jRateCent.eq(2)).text("0.0");

    $(jRate.eq(3)).text("0.0");
    $(jRateCent.eq(3)).text("0.0");
}