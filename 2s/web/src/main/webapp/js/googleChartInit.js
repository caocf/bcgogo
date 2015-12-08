/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-5-7
 * Time: 下午3:32
 * To change this template use File | Settings | File Templates.
 */


function getDataTable(day, month, year, type) {
    $.ajax({
        url:"businessStat.do?method=getDataTable",
        type:"POST",
        dataType:"json",
        data:{type:type, day:day, month:month, year:year},
        success:function (jsonStr) {
            try {
                fillDataTable(jsonStr, type);
            } catch (e) {
            }
        },
        error:function (jsonStr) {
        }
    });
}

function fillDataTable(jsonStr, type) {
    var data = new google.visualization.DataTable();
    data.addColumn('string', type);
    data.addColumn('number', '收入');
    if (jsonStr.length <= 0) {
        //从后台没有查到数据
        if (type == "day") {
            drawDays(data);
        } else if (type == "month") {
            drawMonths(data)
        } else if (type == "year") {
            drawYears(data);
        }
        return;
    } else {
        if (type == "day") {
            data.addRows(jsonStr.length);
            for (var i = 0; i < jsonStr.length; i++) {
                var str = parseInt(jsonStr[i].statDay) + '日';
                data.setValue(i, 0, str);
                data.setValue(i, 1, parseInt(jsonStr[i].statSum));
            }
            drawDays(data);
        } else if (type == "month") {
            data.addRows(jsonStr.length);
            for (var i = 0; i < jsonStr.length; i++) {
                var str = parseInt(jsonStr[i].statMonth) + '月';
                data.setValue(i, 0, str);
                data.setValue(i, 1, parseInt(jsonStr[i].statSum));
            }
            drawMonths(data);
        } else if (type == "year") {
            data.addRows(jsonStr.length);
            for (var i = 0; i < jsonStr.length; i++) {
                var str = jsonStr[i].statYear + '年';
                data.setValue(i, 0, str);
                data.setValue(i, 1, parseInt(jsonStr[i].statSum));
            }
            drawYears(data);
        } else {
            type == "day"
            data.addRows(30);
            for (var i = 0; i < 30; i++) {
                var str = i + 1 + '日';
                data.setValue(i, 0, str);
                data.setValue(i, 1, 0);
            }
            drawDays(data);
        }
    }
}

function changeDateTime(oldMonth, oleYear) {
    var year = ($("#yearHid").val()) * 1;
    var month = ($("#monthHid").val()) * 1;
    if ($("#radDay")[0].className == "r_on" && (oldMonth != month || oleYear != year)) {
        init();
    }
    if ($("#radMonth")[0].className == "r_on" && oleYear != year) {
        init();
    }
    if ($("#radYear")[0].className == "r_on" && oleYear != year) {
        init();
    }
}
function init() {
    var year = $("#yearHid").val();
    var month = $("#monthHid").val();
    var day = $("#dayHid").val();
    $("#radMonth")[0].onclick = function () {

        $("#radMonth")[0].className = "r_on";
        $("#radYear")[0].className = "rad_off";
        $("#radDay")[0].className = "rad_off";
        getDataTable(day, month, year, "month");

    }
    $("#radYear")[0].onclick = function () {

        $("#radMonth")[0].className = "rad_off";
        $("#radYear")[0].className = "r_on";
        $("#radDay")[0].className = "rad_off";
        getDataTable(day, month, year, "year");

    }
    $("#radDay")[0].onclick = function () {

        $("#radMonth")[0].className = "rad_off";
        $("#radYear")[0].className = "rad_off";
        $("#radDay")[0].className = "r_on";
        getDataTable(day, month, year, "day");

    }

    if ($("#radMonth")[0].className == "r_on") {
        $("#radMonth")[0].click();
    } else if ($("#radDay")[0].className == "r_on") {
        $("#radDay")[0].click();
    } else if ($("#radYear")[0].className == "r_on") {
        $("#radYear")[0].click();
    } else {
        $("#radDay")[0].click();
    }


}

function drawMonths(response) {
    draw(response, {width:540, height:280,
        title:'营业收入统计', titleTextStyle:{color:'#676767', fontSize:24, fontWeight:'bold', fontStyle:'normal'},
        backgroundColor:{fill:'transparent'},
        series:{0:{color:'#B0254D', visibleInLegend:false}},
        hAxis:{title:$("#yearHid").val() + '年度',
            titleTextStyle:{color:'#753C2A', fontSize:24, fontWeight:'bold', fontStyle:'normal'}, textStyle:{color:'#90AA3C'}},
        vAxis:{gridlineColor:'#CCCCCC', gridlines:{count:5}, baselineColor:'#CCCCCC', textStyle:{color:'#7B4534'},
            viewWindow:{max:500000, min:0}, format:'###,###'}
    });
}

function drawYears(response) {
    draw(response, {width:540, height:280,
        title:'营业收入统计', titleTextStyle:{color:'#676767', fontSize:24, fontWeight:'bold', fontStyle:'normal'},
        backgroundColor:{fill:'transparent'},
        series:{0:{color:'#B0254D', visibleInLegend:false}},
        hAxis:{title:$("#yearHid").val() + '年度',
            titleTextStyle:{color:'#753C2A', fontSize:24, fontWeight:'bold', fontStyle:'normal'}, textStyle:{color:'#90AA3C'}},
        vAxis:{gridlineColor:'#CCCCCC', gridlines:{count:5}, baselineColor:'#CCCCCC', textStyle:{color:'#7B4534'},
            viewWindow:{max:100000, min:0}, format:'###,###'}
    });
}

function drawDays(response) {
    draw(response, {width:540, height:280,
        title:'营业收入统计', titleTextStyle:{color:'#676767', fontSize:24, fontWeight:'bold', fontStyle:'normal'},
        backgroundColor:{fill:'transparent'},
        series:{0:{color:'#B0254D', visibleInLegend:false}},
        hAxis:{title:$("#yearHid").val() + '年 ' + $("#monthHid").val() + '月份',
            titleTextStyle:{color:'#753C2A', fontSize:12, fontWeight:'bold', fontStyle:'normal'}, textStyle:{color:'#90AA3C'}},
        vAxis:{gridlineColor:'#CCCCCC', gridlines:{count:5}, baselineColor:'#CCCCCC', textStyle:{color:'#7B4534'},
            viewWindow:{max:10000, min:0}, format:'###,###'}
    });
}

function draw(response, options) {

    var data = response;
    if (data == null || data.getNumberOfRows() <= 0) {
        $("#chart_div").css('display', 'none');
        $("#noData").css('display', 'block');
    } else {
        $("#chart_div").css('display', 'block');
        $("#noData").css('display', 'none');
        //计算刻度
        var maxValue = 0;
        for (var i = 0, l = data.getNumberOfRows(); i < l; i++) {
            var v = data.getValue(i, 1);
            if (!isNaN(v) && v > maxValue) {
                maxValue = v;
            }
        }

        if (maxValue > 0) {
            var ten = 1;
            do {
                if (maxValue <= 2.5 * ten) {
                    maxValue = 2.5 * ten;
                    break;
                }
                else if (maxValue <= 5 * ten) {
                    maxValue = 5 * ten;
                    break;
                }
                else if (maxValue <= 10 * ten) {
                    maxValue = 10 * ten;
                    break;
                }
                else {
                    ten = ten * 10;
                }
            }
            while (ten < Math.pow(10, 10))//最多支持10的10次方数量级
        }
        if (maxValue > 0) {
            options.vAxis.viewWindow.max = maxValue;
        }

        var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
        chart.draw(data, options);
    }


}