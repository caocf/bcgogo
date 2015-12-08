/**
 * Created by IntelliJ IDEA.
 * User: E. Lee
 * Date: 12-10-9
 * Time: 上午9:10
 * To change this template use File | Settings | File Templates.
 */

function getRandomColor() {
    var valArray = new Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 'A', 'B', 'C', 'D', 'E', 'F'),
        returnVal = '#';
    for (var i = 0; i < 6; i++) {
        returnVal += valArray[Math.floor(Math.random() * 15)];
    }
    return returnVal;
}

function initButtons() {
    if ($("#radMonth").attr('class') == "r_on") {
        $("#radMonth").click();
    } else if ($("#radDay").attr('class') == "r_on") {
        $("#radDay").click();
    } else if ($("#radYear").attr('class') == "r_on") {
        $("#radYear").click();
    } else {
        $("#radDay").click();
    }
}

function HighChartFunction(arguments) {

    //Chart Init Setting
    this.chart = arguments.chart;
    this.title = arguments.title || '营业收入统计';
    this.colors = arguments.colors || ['#058DC7'];
    this.categories = arguments.categories || [];
    this.name = arguments.name || $("#yearHid").val() + '年' + $("#monthHid").val() + '月份';
    this.data = arguments.data || [];
    this.url = arguments.url || "businessStat.do?method=getDataTable";
    this.render = arguments.render || 'chart_div';
    this.width = arguments.width || 550;
    this.height = arguments.height || 270;
    //Return a random color.
    this.randomColor = getRandomColor;

    //Init the buttons.
    this.initButtons = initButtons;

    //Init the some data.
    var selectType;
    this.initChart = function () {
        var obj = this;

        //Bind these buttons.
        $("#radYear").click(function () {
            obj.selectRadioType("year");
        });
        $("#radMonth").click(function () {
            obj.selectRadioType("month");
        });
        $("#radDay").click(function () {
            obj.selectRadioType("day");
        });

        this.initButtons();

        //Fill the color array.
        for (var i = 0; i < 30; i++) {
            this.colors.push(this.randomColor());
        }

        //Init the select box.

        obj.buildDateSelect.initDateSelect("selectYear", "selectMonth", "selectDay", 2010, 2019);

        $("#selectYear").val($("#yearHid").val());
        $("#selectMonth").val($("#monthHid").val());
        $("#selectDay").val($("#dayHid").val());

        obj.buildDateSelect.displaySelectDay($("#selectYear").val(), $("#selectMonth").val(), "selectDay");
    };

    //Choose the date type.
    this.selectRadioType = function (type) {
        selectType = type;
        var _year_ = $("#yearHid").val(),
            _month_ = $("#monthHid").val(),
            _day_ = $("#dayHid").val();
        switch (type) {
            case 'day':
                $("#radYear")[0].className = "rad_off";
                $("#radMonth")[0].className = "rad_off";
                $("#radDay")[0].className = "r_on";
                this.name = $("#yearHid").val() + '年' + $("#monthHid").val() + '月份';
                break;

            case 'month':
                $("#radYear")[0].className = "rad_off";
                $("#radMonth")[0].className = "r_on";
                $("#radDay")[0].className = "rad_off";
                this.name = $("#yearHid").val() + '年度';
                break;

            case 'year':
                $("#radYear")[0].className = "r_on";
                $("#radMonth")[0].className = "rad_off";
                $("#radDay")[0].className = "rad_off";
                this.name = $("#yearHid").val() + '年度';
                break;

            default:
                $("#radYear")[0].className = "rad_off";
                $("#radMonth")[0].className = "rad_off";
                $("#radDay")[0].className = "r_on";
                this.name = $("#yearHid").val() + '年' + $("#monthHid").val() + '月份';
                break;
        }
        this.getDateTable(_day_, _month_, _year_, type)
    };

    //Ajax:get the data according to the date.
    this.getDateTable = function (day, month, year, type) {
        var obj = this;
        $.ajax({
            url:obj.url,
            type:"POST",
            dataType:"json",
            data:{
                type:type,
                day:day,
                month:month,
                year:year
            },
            success:function (jsonStr) {
                try {
                    obj.drawChart(jsonStr, type);
                } catch (e) {
                }
            },
            error:function () {
            }
        });
    };

    //Change the hidden value.
    this.changeDateTime = function (oldMonth, oleYear) {
        var year = $("#yearHid").val(),
            month = $("#monthHid").val();
        if ($("#radDay")[0].className == "r_on" && (oldMonth != month || oleYear != year)) {
            this.initButtons();
        }
        if ($("#radMonth")[0].className == "r_on" && oleYear != year) {
            this.initButtons();
        }
        if ($("#radYear")[0].className == "r_on" && oleYear != year) {
            this.initButtons();
        }
    };

    //Build the select box.
    this.buildDateSelect = {
        initDateSelect:function (year, month, day, startYear, endYear) {
            var thisObj = this;
            if (null == startYear) {
                startYear = "1970";
            }
            if (null == endYear) {
                endYear = new Date().getFullYear();
            }
            //输出年
            for (; startYear <= endYear; startYear++) {
                $("#" + year).append("<option  value=" + startYear + ">" + startYear + "</option>");
            }
            //输出月
            for (var i = 1; i <= 12; i++) {
                $("#" + month).append("<option  value=" + i + ">" + i + "</option>");
            }
            //输出日期
            if (null != day) {
                $(document).ready(function () {
                    $("#" + year).change(function () {
                        thisObj.displaySelectDay($("#" + year).val(), $("#" + month).val(), day);
                        getDate("year");
                    });
                    $("#" + month).change(function () {
                        thisObj.displaySelectDay($("#" + year).val(), $("#" + month).val(), day);
                        getDate("month");
                    });
                    $("#" + day).change(function () {
                        getDate("day");
                    });
                });
                this.displaySelectDay($("#" + year).val(), $("#" + month).val(), day);
            }
        },
        isLeapYear:function (year) {
            return((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0));
        },
        displaySelectDay:function (year, month, selectDayId) {
            if (null != year && null != month && !isNaN(year) && !isNaN(month)) {
                var orgDay = $("#" + selectDayId).val();
                var monthArr = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
                var day = (this.isLeapYear(year) && month == 2) ? monthArr[month - 1] + 1 : monthArr[month - 1];
                $("#" + selectDayId + ">option").remove();
                for (var i = 1; i <= day; i++) {
                    $("#" + selectDayId).append("<option value=" + i + ">" + i + "</option>");
                }
                $("#" + selectDayId).val(orgDay);
            }
        }
    };

    //Set the chart and redraw it.
    this.setChart = function (name, categories, data, color) {
        var obj = this;
        obj.chart.xAxis[0].setCategories(categories, false);
        obj.chart.series[0].remove(false);
        obj.chart.addSeries({
            name:name,
            data:data,
            color:color || 'white'
        }, false);
        obj.chart.redraw();
    };

    //Draw the chart according to the JSON.
    this.drawChart = function (jsonStr, type) {
        var obj = this;
        jsonStr = jsonStr || "";
        if (jsonStr.length <= 0) {
            $("#chart_div").css('display', 'none');
            $("#noData").css('display', 'block');
        } else {
            var _categories = new Array(),
                _data = new Array(jsonStr.length);
            for (var i = 0; i < jsonStr.length; i++) {
                switch (type) {
                    case 'day':
                        _categories.push(jsonStr[i].statDay.toString());
                        break;

                    case 'month':
                        _categories.push(jsonStr[i].statMonth.toString());
                        break;

                    case 'year':
                        _categories.push(jsonStr[i].statYear.toString());
                        break;

                    default:
                        _categories.push(jsonStr[i].statDay.toString());
                        break;
                }
                if (obj.url.indexOf('businessStat') >= 0) {
                    _data[i] = {
                        y:jsonStr[i].statSum,
                        color:obj.colors[i]
                    };
                } else if (obj.url.indexOf('runningStat') >= 0) {
                    _data[i] = {
                        y:jsonStr[i].runningSum,
                        color:obj.colors[i]
                    };
                }
                $("#noData").css('display', 'none');
                $("#chart_div").css('display', 'block');
            }
            obj.setChart(obj.name, _categories, _data, obj.colors);
        }
    };

    //Build a chart.
    this.chart = new Highcharts.Chart({
        chart:{
            renderTo:this.render,
            type:'column',
            backgroundColor:{
                linearGradient:[0, 0, 0, 500],
                stops:[
                    [0, 'rgb(255, 255, 255)'],
                    [1, 'rgb(230, 230, 230)']
                ]
            },
            width:550,
            height:270
        },
        title:{
            text:this.title,
            style:{
                color:'#000',
                fontSize:'16px',
                fontWeight:'bold'
            }
        },
        xAxis:{
            categories:this.categories
        },
        yAxis:{
            title:this.title
        },
        plotOptions:{
            column:{
                cursor:'pointer' //,
                //                                                  dataLabels: {
                //                                                      enabled: true,
                //                                                      color: this.colors[0],
                //                                                      style: {
                //                                                          fontWeight: 'bold'
                //                                                      },
                //                                                      formatter: function() {
                //                                                          return this.y == 0 ? '' : this.y + '元';
                //                                                      }
                //                                                  }
            }
        },
        tooltip:{
            formatter:function () {
                var point = this.point,
                    s;
                var year_ = $('#yearHid').val(),
                    month_ = $('#monthHid').val();
                switch (selectType) {
                    case "day":
                        s = year_ + '年' + month_ + '月' + this.x + '日:<br />' + '¥<strong>' + this.y + '</strong>元';
                        break;

                    case "month":
                        s = year_ + '年' + this.x + '月:<br />' + '¥<strong>' + this.y + '</strong>元';
                        break;

                    case "year":
                        s = this.x + '年:<br />' + '¥<strong>' + this.y + '</strong>元';
                        break;
                }
                return s;
            }
        },
        series:[
            {
                name:this.name,
                data:this.data,
                color:'white'
            }
        ],
        exporting:{
            enabled:false
        },
        legend:{
            borderWidth:0
        }
    });

    //Use the init function.
    this.initChart();
}