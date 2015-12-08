/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-2-13
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */

var nextPageNo1 = 1,
    isTheLastPage1 = false,
    pageSize = 10,
    pageNum = 1,
    isSearch = false,
    fillColor = new Array();


$("#pageNo_id1>div").live("click", function () {
    $("#sata_tab tr:not(:first)").remove();
    var selectItem = $(this).html();
    if (selectItem == "上一页") {
        if (nextPageNo1 > 1) {
            nextPageNo1 = nextPageNo1 - 1;
            $("#thisPageNo1").html(nextPageNo1);
            $("#pageNo_id1>div:last").css('display', 'block');
            if (nextPageNo1 == 1) {
                $(this).css('display', 'none');
            }
        }
    } else if (selectItem == "下一页") {
        if (!isTheLastPage1) {
            nextPageNo1 = nextPageNo1 + 1;
            $("#thisPageNo1").html(nextPageNo1);
            $("#pageNo_id1>div:first").css('display', 'block');
        }
    }
    $.ajax({
        type:"POST",
        url:"bizstat.do?method=customerResponse",
        data:{startPageNo:nextPageNo1, maxRows:pageSize, year:$("#queryYear").val(), startMonth:$("#startMonth").val(), endMonth:$("#endMonth").val()},
        cache:false,
        dataType:"json",
        success:function (jsonStr) {
            initTr1(jsonStr);
            if (jsonStr[jsonStr.length - 1].isTheLastPage1 == "true") {
                $("#pageNo_id1>div:last").css('display', 'none');
            }
            //Set the color of the table
            for (var i = 0; i < fillColor.length; i++) {
                $('.statisticsTable .colors' + (i + 1) + ' td').css({'color':fillColor[i]});
            }
        }
    });
});


function initTr1(jsonStr) {
    if (jsonStr != null) {
        $("#histy tr:not(:first)").remove();
        var num_count;
        for (var i = 0; i < jsonStr.length - 2; i++) {
            if (jsonStr[jsonStr.length - 1].isTheLastPage1 == "true") {
                isTheLastPage1 = true;
            } else {
                isTheLastPage1 = false;
            }
            if (jsonStr[jsonStr.length - 2].isTheFirstPage1 == "true") {
                var j = i + 1;
                var trStr = ' class="colors' + j + '"';
                var tdStr = '<div class="color' + j + '"></div>';
            } else {
                var trStr = "";
                var tdStr = "";
            }
            var assistantName = jsonStr[i].assistant == undefined ? " " : jsonStr[i].assistant;
            var carRepair = jsonStr[i].service == undefined ? " " : jsonStr[i].service;
            var sales = jsonStr[i].sales == undefined ? " " : jsonStr[i].sales;
            var washing = jsonStr[i].wash == undefined ? " " : jsonStr[i].wash;
            var total = jsonStr[i].statSum == undefined ? " " : jsonStr[i].statSum;
            var memberIncome = jsonStr[i].memberIncome == undefined ? " " : jsonStr[i].memberIncome;
            var str = '详细';
            var stss = "'" + assistantName + "'," + "'" + memberIncome + "'," + "'" + carRepair + "'," + "'" + sales + "'," + "'" + washing + "'," + "'" + total + "'";
            var tr = '<tr ' + trStr + '>';
            num_count = ($("#thisPageNo1").html() - 1) * 10 + i + 1;
            tr += '<td>' + tdStr + num_count + '</td>';
            tr += '<td align="right">' + assistantName + '</td>';

            if(APP_BCGOGO.Permission.Version.VehicleConstruction){
              tr += '<td align="right">' + washing + '</td>';
              tr += '<td align="right">' + carRepair + '</td>';
            }
            tr += '<td align="right">' + sales + '</td>';
            if(APP_BCGOGO.Permission.Version.MemberStoredValue){
              tr += '<td align="right">' + memberIncome + '</td>';
            }
            tr += '<td align="right">' + total + '</td>';
            tr += '<td align="right">' + '<a class="detail" href = "#" onclick="openWin(' + stss + ')">' + str + '</a> ' + '</td> ';
            tr += '</tr>';
            $("#histy").append(tr);
        }
    }
}
function openWin(assistName, memberIncome, carRepair, sales, washing, total) {
    var year = document.getElementById("queryYear").value;
    var startMonth = document.getElementById("startMonth").value;
    var endMonth = document.getElementById("endMonth").value;
    var assistantName = assistName;

    window.open(encodeURI('bizstat.do?method=getAssistantDetail&assistantName=' + assistantName + '&year=' + year + '&startMonth=' + startMonth + '&endMonth=' + endMonth + "&memberIncome=" + memberIncome + "&carRepair=" + carRepair + "&sales=" + sales + "&washing=" + washing + "&total=" + total));
}

function historySearch() {
    document.getElementById("isSearch").value = 1;
    var searchYear = document.getElementById("txtYear").value;
    var startMonth = document.getElementById("txtFromMonth").value;
    var endMonth = document.getElementById("txtToMonth").value;

    if (!isNaN(searchYear)) {
        if (searchYear < 2010 || searchYear > 2020) {
            alert("您输入的年份有误，请重新输入");
            return;
        }
    }
    else {
        alert("您输入的查询年份有误，请重新输入");
        return;
    }

    if (!isNaN(startMonth)) {
        if (startMonth < 1 || startMonth > 12) {
            alert("您输入的开始月份有误，请重新输入");
            return;
        }
    }
    else {
        alert("您输入的开始月份有误，请重新输入");
        return;
    }

    if (!isNaN(endMonth)) {
        if (endMonth < 1 || endMonth > 12) {
            alert("您输入的结束月份有误，请重新输入");
            return;
        }
    }
    else {
        alert("您输入的结束月份有误，请重新输入");
        return;
    }

    if (parseInt(startMonth, 10) > parseInt(endMonth, 10)) {
        alert("您输入的开始月份大于结束月份，请重新输入");
        return;
    }

    window.location = "bizstat.do?method=agentAchievements&searchYear=" + searchYear + "&startMonth=" + startMonth + "&endMonth=" + endMonth;
}

function statHistorySearch() {
    var startDate = document.getElementById("startDateStr").value;
    var endDate = document.getElementById("endDateStr").value;
}

function drawChart() {
    var startMonth = document.getElementById("startMonth").value;
    var endMonth = document.getElementById("endMonth").value;
    var year = document.getElementById("txtYear").value;
}

function thisMonth() {
    $("#carWash2")[0].className = "title_hover";
    $("#thisMonth")[0].className = "listWords hovers";
    window.location.assign("bizstat.do?method=agentAchievements&month=thisMonth");
}
function lastMonth() {
    $("#carWash2")[0].className = "title_hover";
    $("#lastMonth")[0].className = "listWords hovers";
    window.location.assign("bizstat.do?method=agentAchievements&month=lastMonth");
}

$(function () {
    var chart;
    APP_BCGOGO.namespace('highChart');
    APP_BCGOGO.highChart.setPie = function(data) {

        if (data == null || data.length <= 0) {
            $("#chart_div").css('display', 'none');
            $("#noData").css('display', 'block');
            $("#performanceName").css('display', 'none');
            $("#pageNo_id1").css('display', 'none');
            return;
        } else {
            $("#chart_div").css('display', 'block');
            $("#noData").css('display', 'none');
        }

        // Build the chart
        var jsonStr = new Array();
        for (var i = 0; i < data.length; i++) {
            if (data[i].assistant) {
                jsonStr.push(new Array(
                    data[i].assistant + "(" + data[i].statSum + "元)",
                    data[i].statSum
                ));
            }
        }

        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'chart_div',
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: ''
            },
            tooltip: {
                pointFormat: '{series.name}{point.percentage}%',
                percentageDecimals: 1
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false
                    },
                    showInLegend: true,
                    size:290
                }
            },
            legend: {
                align: 'right',
                verticalAlign: 'top',
                x: -60,
                y: 100,
                layout: 'vertical',
                borderWidth:0
            },
            series: [
                {
                    type: 'pie',
                    name: '约占',
                    data: jsonStr
                }
            ],
            navigation: {
                buttonOptions: {
                    enabled: false
                }
            }
        });
        //Get all the color.
        var colorPath = $('#chart_div .highcharts-point path[fill^="#"]');
        for (var i = 0; i < colorPath.length; i++) {
            fillColor.push($(colorPath[i]).attr('fill'));
        }
        for (var i = 0; i < fillColor.length; i++) {
            $('.statisticsTable .colors' + (i + 1) + ' td').css({'color':fillColor[i]});
        }
    };

    $("#thisMonth a,#lastMonth a").hover(function(){
        $(this).css({"color":"#fd5300","textDecoration":"underline"});
    },function(){
        $(this).css({"color":"#6699cc","textDecoration":"none"});
    });
    $(".detail").live("mouseover",function(){
        $(this).css({"color":"#fd5300","textDecoration":"underline"});
    });
    $(".detail").live("mouseout",function(){
        $(this).css({"color":"#6699cc","textDecoration":"none"});
    });

    $("#pageNo_id1").css('display', 'none');
    $.ajax({
        type:"POST",
        url:"bizstat.do?method=customerResponse",
        data:{startPageNo:nextPageNo1, maxRows:pageSize, year:$("#queryYear").val(), startMonth:$("#startMonth").val(), endMonth:$("#endMonth").val()},
        cache:false,
        dataType:"json",
        success:function (jsonStr) {

            if (jsonStr == null || jsonStr.length <= 0) {
              $("#chart_div").css('display', 'none');
              $("#noData").css('display', 'block');
              $("#performanceName").css('display', 'none');
              $("#pageNo_id1").css('display', 'none');
              return;
            }

            initTr1(jsonStr);
            APP_BCGOGO.highChart.setPie(jsonStr);
            $("#pageNo_id1").css('display', 'block');
            if (isTheLastPage1 == true && nextPageNo1 == 1) {
                $("#pageNo_id1>div:eq(1)").css('display', 'none');
            }
            if (isTheLastPage1 == true) {
                $("#pageNo_id1>div:last").css('display', 'none');

            }
            if (nextPageNo1 == 1) {
                $("#pageNo_id1>div:first").css('display', 'none');
                //Set the color of the table
                for (var i = 0; i < fillColor.length; i++) {
                    $('.statisticsTable .colors' + (i + 1) + ' td').css({'color':fillColor[i]});
                }
            }
        }

    });
});
