/**
 * @description 时间处理
 * @author ndong
 * @date create 2012-10-28
 */
 APP_BCGOGO.namespace("GLOBAL.dateUtil");

var dateUtil = {
    dateStringFormatDayHourMin: "yyyy-MM-dd HH:mm",
    dateStringFormatDay: "yyyy-MM-dd",
    millSecondOneHour : 1000 * 60 * 60,
    millSecondOneDay : 1000 * 60 * 60 * 24
};

/**
 * 变量
 */
dateUtil.rangeData={
    now:new Date(),
    nowYear:function(){
        var nowYear=new Date().getYear();
        return nowYear += (nowYear < 2000) ? 1900 : 0;}, //当前年
    nowDay :function(){
        return new Date().getDate()
    }, //当前日
    nowMonth :function(){
        return new Date().getMonth()
    }, //当前月
    nowDayOfWeek :function(){  //今天本周的第几天
        var temp= new Date().getDay();
        if(0==temp){
            return 7;
        }
        return temp ;
    }
};

dateUtil.getTheDateTime = function (theDateTime){
    var theDate= new Date(theDateTime);
    return new Date(theDate.getYear(), theDate.getMonth(),theDate.getDate());
};
//获得某月的天数
dateUtil.getMonthDays = function (myMonth){
    var rData=dateUtil.rangeData;
    var monthStartDate = new Date(rData.nowYear(), myMonth, 1);
    var monthEndDate = new Date(rData.nowYear(), myMonth + 1, 1);
    var days = (monthEndDate - monthStartDate)/dateUtil.millSecondOneDay;
    return days;
};

/**
 * 获得两个日期之间的天数
 */
dateUtil.getDayBetweenTwoDate=function (date1,date2) {
    var days = (date2 - date1)/dateUtil.millSecondOneDay;
    return days;
};

/**
 * 格式化日期
 * @param dateStr
 * @param formate
 */
dateUtil.convertDateStrToDate = function (dateStr, formate) {
    var date="";
    if(G.isEmpty(dateStr)){
        return date;
    }
    switch (formate) {
        case dateUtil.dateStringFormatDayHourMin:
            date = new Date(Date.parse(dateStr.replace(/-/g,   "/")));
            break;
        default:
            date="";
            break;
    }
    return date;
};
/**
 * 格式化日期
 * @param date
 * @param formate
 */
dateUtil.formatDate = function (date, formate) {
    var year = date.getFullYear(),
        month = date.getMonth() + 1,
        day = date.getDate(),
        hour = date.getHours(),
        minute = date.getMinutes(),
        formatDate;
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minute < 10) {
        minute = "0" + minute;
    }
    switch (formate) {
        case dateUtil.dateStringFormatDayHourMin:
            formatDate = (year + "-" + month + "-" + day + " " + hour + ":" + minute);
            break;
        case dateUtil.dateStringFormatDay:
            formatDate = (year + "-" + month + "-" + day);
            break;
        default:
            formatDate = (year + "-" + month + "-" + day);
            break;
    }
    return formatDate;
};

/**
 * 获得本周的开端日期  周一为开端
 */
dateUtil.getWeekStartDate=function (isWithTime,startOrEnd) {
    var rData=dateUtil.rangeData
    var weekStartDate = new Date(rData.nowYear(), rData.nowMonth(), rData.nowDay() - rData.nowDayOfWeek()+1);
    var formateWeekStartDate=dateUtil.formatDate(weekStartDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateWeekStartDate=formateWeekStartDate+" 00:00";
            }if(startOrEnd=="end"){
                formateWeekStartDate=formateWeekStartDate+" 23:59";
            }
        }
    }
    return formateWeekStartDate;
} ;
/**
 * 获得本周的停止日期
 */
dateUtil.getWeekEndDate=function (isWithTime,startOrEnd) {
    var rData=dateUtil.rangeData
    var weekEndDate = new Date(rData.nowYear(), rData.nowMonth(), rData.nowDay() + (7 - rData.nowDayOfWeek()));
    var formateWeekEndStartDate=dateUtil.formatDate(weekEndDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateWeekEndStartDate=formateWeekEndStartDate+" 00:00";
            }if(startOrEnd=="end"){
                formateWeekEndStartDate=formateWeekEndStartDate+" 23:59";
            }
        }
    }

    return formateWeekEndStartDate;
};

/**
 * 获取昨天
 */
dateUtil.getYesterday=function (isWithTime,startOrEnd) {
    var date = new Date();
    var ms = date.getTime();
    var yesterday = new Date(ms - dateUtil.millSecondOneDay);
    var formateDateYesterday=dateUtil.formatDate(yesterday);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateDateYesterday=formateDateYesterday+" 00:00";
            }if(startOrEnd=="end"){
                formateDateYesterday=formateDateYesterday+" 23:59";
            }
        }
    }
    return formateDateYesterday;
};

/**
 * 获得本日的时间
 */
dateUtil.getToday = function (isWithTime,startOrEnd){
    var rData=dateUtil.rangeData
    var monthStartDate = new Date(rData.nowYear(), rData.nowMonth(),rData.nowDay());
    var formateDateToday=dateUtil.formatDate(monthStartDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateDateToday=formateDateToday+" 00:00";
            }if(startOrEnd=="end"){
                formateDateToday=formateDateToday+" 23:59";
            }
        }
    }

    return formateDateToday;
};

/**
 * 获得本周的开端日期
 */
dateUtil.getOneWeekBefore = function (isWithTime,startOrEnd){
    var date = new Date();
    var ms = date.getTime();
    var oneWeekBefore = new Date(ms - dateUtil.millSecondOneDay * 6);
    var formateDateOneWeekBefore=dateUtil.formatDate(oneWeekBefore);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateDateOneWeekBefore=formateDateOneWeekBefore+" 00:00";
            }if(startOrEnd=="end"){
                formateDateOneWeekBefore=formateDateOneWeekBefore+" 23:59";
            }
        }
    }
    return formateDateOneWeekBefore;
};

/**
 * 获得上月开始天
 */
dateUtil.getFirstDayOfLastMonth = function (isWithTime,startOrEnd){
    var rData=dateUtil.rangeData;

    var nowMonth = rData.nowMonth();
    var monthStartDate = null;
    if(nowMonth == 0){
        monthStartDate = new Date(rData.nowYear()-1, 11,1);
    }else{
        monthStartDate = new Date(rData.nowYear(), rData.nowMonth()-1,1);
    }
    var formateFirstDayOfLastMonth=dateUtil.formatDate(monthStartDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateFirstDayOfLastMonth=formateFirstDayOfLastMonth+" 00:00";
            }if(startOrEnd=="end"){
                formateFirstDayOfLastMonth=formateFirstDayOfLastMonth+" 23:59";
            }
        }
    }
    return formateFirstDayOfLastMonth;
};

/**
 * 获得上月最后一天
 */
dateUtil.getEndDayOfLastMonth = function (isWithTime,startOrEnd){
    var rData=dateUtil.rangeData;

    var nowMonth = rData.nowMonth();
    var monthStartDate = null;
    if(nowMonth == 0){
        monthStartDate = new Date(rData.nowYear()-1, 11,dateUtil.getMonthDays(11));
    }else{
        monthStartDate = new Date(rData.nowYear(), rData.nowMonth()-1,dateUtil.getMonthDays(rData.nowMonth()-1));
    }
    var formateEndDayOfLastMonth=dateUtil.formatDate(monthStartDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateEndDayOfLastMonth=formateEndDayOfLastMonth+" 00:00";
            }if(startOrEnd=="end"){
                formateEndDayOfLastMonth=formateEndDayOfLastMonth+" 23:59";
            }
        }
    }
    return formateEndDayOfLastMonth;
};

/**
 * 获得一个月前的日期
 */
dateUtil.getOneMonthBefore = function (isWithTime,startOrEnd){
    var rData=dateUtil.rangeData;
    var nowMonth = rData.nowMonth();
    var monthStartDate = null;
    if(nowMonth == 0){
        monthStartDate = new Date(rData.nowYear()-1, 11,rData.nowDay());
    }else{
        monthStartDate = new Date(rData.nowYear(), rData.nowMonth()-1,rData.nowDay());
    }
    var formateDateOneMonthBefore=dateUtil.formatDate(monthStartDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateDateOneMonthBefore=formateDateOneMonthBefore+" 00:00";
            }if(startOrEnd=="end"){
                formateDateOneMonthBefore=formateDateOneMonthBefore+" 23:59";
            }
        }
    }

    return formateDateOneMonthBefore;
};

/**
 * 获得去年的开端日期
 */
dateUtil.getLastYearStart = function (isWithTime,startOrEnd) {
    var rData = dateUtil.rangeData;
    var lastYearStartDate = null;
    lastYearStartDate = new Date(rData.nowYear()-1,0,1);
    var formateLastYearStartDate=dateUtil.formatDate(lastYearStartDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateLastYearStartDate=formateLastYearStartDate+" 00:00";
            }if(startOrEnd=="end"){
                formateLastYearStartDate=formateLastYearStartDate+" 23:59";
            }
        }
    }
    return formateLastYearStartDate;
};

/**
 * 获得去年的结束日期
 */
dateUtil.getLastYearEnd = function (isWithTime,startOrEnd) {
    var rData = dateUtil.rangeData;
    var lastYearEndDate = null;
    lastYearEndDate = new Date(rData.nowYear()-1,11,31);
    var formateLastYearEndDate=dateUtil.formatDate(lastYearEndDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateLastYearEndDate=formateLastYearEndDate+" 00:00";
            }if(startOrEnd=="end"){
                formateLastYearEndDate=formateLastYearEndDate+" 23:59";
            }
        }
    }
    return formateLastYearEndDate;
};

/**
 * 获得本年的开端日期
 */
dateUtil.getYearStart = function (isWithTime,startOrEnd) {
    var rData = dateUtil.rangeData;
    var yearStartDate = null;
    yearStartDate = new Date(rData.nowYear(),0,1);
    var formateYearStartDate=dateUtil.formatDate(yearStartDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateYearStartDate=formateYearStartDate+" 00:00";
            }if(startOrEnd=="end"){
                formateYearStartDate=formateYearStartDate+" 23:59";
            }
        }
    }
    return formateYearStartDate;
};

/**
 * 获得本年的结束日期
 */
dateUtil.getYearEnd = function (isWithTime,startOrEnd) {
    var rData = dateUtil.rangeData;
    var yearEndDate = null;
    yearEndDate = new Date(rData.nowYear(),11,31);
    var formateYearEndDate=dateUtil.formatDate(yearEndDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateYearEndDate=formateYearEndDate+" 00:00";
            }if(startOrEnd=="end"){
                formateYearEndDate=formateYearEndDate+" 23:59";
            }
        }
    }
    return formateYearEndDate;
};

/**
 * 获得最近一年的开始日期
 */
dateUtil.getOneYearBefore = function (isWithTime,startOrEnd) {
    var rData = dateUtil.rangeData;

    var nowMonth = rData.nowMonth();
    var monthStartDate = null;
    monthStartDate = new Date(rData.nowYear() - 1, rData.nowMonth(), rData.nowDay());
    var formateMonthStartDate=dateUtil.formatDate(monthStartDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateMonthStartDate=formateMonthStartDate+" 00:00";
            }if(startOrEnd=="end"){
                formateMonthStartDate=formateMonthStartDate+" 23:59";
            }
        }
    }
    return formateMonthStartDate;
};


/**
 * 获得本月的开端日期
 */
dateUtil.getMonthStartDate = function (isWithTime,startOrEnd){
    var rData=dateUtil.rangeData
    var monthStartDate = new Date(rData.nowYear(), rData.nowMonth(), 1);
    var formateMonthStart=dateUtil.formatDate(monthStartDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateMonthStart=formateMonthStart+" 00:00";
            }if(startOrEnd=="end"){
                formateMonthStart=formateMonthStart+" 23:59";
            }
        }
    }
    return formateMonthStart;
};

/**
 * 获得本月的停止日期
 */
dateUtil.getMonthEndDate = function (isWithTime,startOrEnd){
    var rData=dateUtil.rangeData;
    var monthEndDate = new Date(rData.nowYear(), rData.nowMonth(),dateUtil.getMonthDays(rData.nowMonth()));
    var formateMonthEndDate=dateUtil.formatDate(monthEndDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                formateMonthEndDate=formateMonthEndDate+" 00:00";
            }if(startOrEnd=="end"){
                formateMonthEndDate=formateMonthEndDate+" 23:59";
            }
        }
    }
    return formateMonthEndDate;
};



/**
 * 获得三个月的开端日期
 */
dateUtil.getThreeMonthStartDate = function (isWithTime,startOrEnd){
    var rData=dateUtil.rangeData;

    var nowMonth = rData.nowMonth();
    var monthStartDate = null;
    if(nowMonth < 2){
        monthStartDate = new Date(rData.nowYear()-1, 12 - 3 + rData.nowMonth(),rData.nowDay());
    }else{
        monthStartDate = new Date(rData.nowYear(), rData.nowMonth()-3,rData.nowDay());
    }
    var threeMonthBeforeDate=dateUtil.formatDate(monthStartDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                threeMonthBeforeDate=threeMonthBeforeDate+" 00:00";
            }if(startOrEnd=="end"){
                threeMonthBeforeDate=threeMonthBeforeDate+" 23:59";
            }
        }
    }
    return threeMonthBeforeDate;
};

/**
 * 获得六个月的开端日期
 */
dateUtil.getSixMonthStartDate = function (isWithTime,startOrEnd){
    var rData=dateUtil.rangeData;

    var nowMonth = rData.nowMonth();
    var monthStartDate = null;
    if(nowMonth < 5){
        monthStartDate = new Date(rData.nowYear()-1, 12 -6 + rData.nowMonth(),rData.nowDay());
    }else{
        monthStartDate = new Date(rData.nowYear(), rData.nowMonth()-6,rData.nowDay());
    }
    var sixMonthStartDate=dateUtil.formatDate(monthStartDate);
    if(isWithTime!=null&&startOrEnd!=""){
        if(isWithTime){
            if(startOrEnd=="start"){
                sixMonthStartDate=sixMonthStartDate+" 00:00";
            }if(startOrEnd=="end"){
                sixMonthStartDate=sixMonthStartDate+" 23:59";
            }
        }
    }
    return sixMonthStartDate;
};


dateUtil.isBetweenTodayAndTomorrow = function (time){
    var today = new Date();
    today = new Date(today.getFullYear()+"/"+(today.getMonth()+1)+"/"+today.getDate()+" 00:00");
    today = today.getTime();
    var tomorrow = today + 2*24*3600*1000;
    if(today<=time && time<tomorrow){
        return true;
    }else{
        return false;
    }
}

dateUtil.getNDayCloseToday = function (n) {
    var data = dateUtil.rangeData;
    var newDate = new Date(data.nowYear(), data.nowMonth(), data.nowDay() + n);
    return dateUtil.formatDate(newDate,dateUtil.dateStringFormatDay);
}

/**
 *  几天后的日期
 * @param n    天数
 * @param date 日期对象
 */
dateUtil.getAfterDays = function (n,dateStr) {
    var date=GLOBAL.Util.getExactDate(dateStr);
    if(G.isEmpty(date)){
        return "";
    }
    var newDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() + n);
    return dateUtil.formatDate(newDate,dateUtil.dateStringFormatDay);
}

/**
 *  几天前的日期
 * @param n    天数
 * @param date 日期对象
 */
dateUtil.getBeforeDays = function (n,dateStr) {
    var date=GLOBAL.Util.getExactDate(dateStr);
    if(G.isEmpty(date)){
        return "";
    }
    var newDate = new Date(date.getFullYear(), date.getMonth(),date.getDate() - n);
    return dateUtil.formatDate(newDate,dateUtil.dateStringFormatDay);
}

dateUtil.getCurrentTime = function (format) {
    if(G.isEmpty(format)){
        format=dateUtil.dateStringFormatDayHourMin;
    }
    return dateUtil.formatDate(new Date(),format);
}

dateUtil.getEndOfCurrentTime = function (format) {
    var dayStr=dateUtil.getCurrentTime(dateUtil.dateStringFormatDay);
    dayStr+=' 23:59';
    return dayStr;
}
/**
 *  是否在今天
 * @param day  millSecondDay
 */
dateUtil.inToday = function (day) {
    if(G.isEmpty(day)) return false;
    var rData = dateUtil.rangeData;
    var startOfToday = new Date(rData.nowYear(), rData.nowMonth(), rData.nowDay()).getTime();
    var endOfToday = new Date(rData.nowYear(), rData.nowMonth(), rData.nowDay()+1).getTime();
    return (day>=startOfToday&&day<=endOfToday)?true:false;
}

$(function(){

    $("a[name='my_date_select']").bind("click", function() {
        var now = new Date();
        var year = now.getFullYear();
        var idStr = $(this).attr("id");
        var confirmstarttime;
        var confirmendtime;
        var isDateTime;
        if($(this).hasClass("select2"))  {
            if($(".my_startdate2").length>0)  {
                confirmstarttime=$(".my_startdate2").attr("id");
            }
            if($(".my_enddate2").length>0)  {
                confirmendtime=$(".my_enddate2").attr("id");
            }

        } else{
            if($(".my_startdate").length>0)  {
                confirmstarttime=$(".my_startdate").attr("id");
            }else{
                confirmstarttime=$(".my_startdatetime").attr("id");
            }
            if($(".my_enddate").length>0)  {
                confirmendtime=$(".my_enddate").attr("id");
            }else{
                confirmendtime=$(".my_enddatetime").attr("id");
            }
        }
        $("a[name='my_date_select']").not(this).removeClass("clicked");
        if($("#"+confirmstarttime).length>0){
            if($("#"+confirmstarttime).attr("class").startWith("my_startdatetime")){
                isDateTime=true;
            }
        }
        if (!$(this).hasClass("clicked")) {
            $(this).addClass("clicked");
            if (idStr == "my_date_yesterday") {
                $("#"+confirmstarttime).val(dateUtil.getYesterday(isDateTime,"start"));
                $("#"+confirmendtime).val(dateUtil.getYesterday(isDateTime,"end"));
            } else if (idStr == "my_date_today") {
                $("#"+confirmstarttime).val(dateUtil.getToday(isDateTime,"start"));
                $("#"+confirmendtime).val(dateUtil.getToday(isDateTime,"end"));
            } else if (idStr == "my_date_oneMonthBefore") {
                $("#"+confirmstarttime).val(dateUtil.getOneMonthBefore(isDateTime,"start"));
                $("#"+confirmendtime).val(dateUtil.getToday(isDateTime,"end"));
            } else if (idStr == "my_date_oneYearBefore") {
                $("#"+confirmstarttime).val(dateUtil.getOneYearBefore(isDateTime,"start"));
                $("#"+confirmendtime).val(dateUtil.getToday(isDateTime,"end"));
            }else if(idStr=="my_date_thisweek"){
                $("#"+confirmstarttime).val(dateUtil.getWeekStartDate(isDateTime,"start"));
                $("#"+confirmendtime).val(dateUtil.getWeekEndDate(isDateTime,"end"));
            }else if(idStr=="my_date_thismonth"){
                $("#"+confirmstarttime).val(dateUtil.getMonthStartDate(isDateTime,"start"));
                $("#"+confirmendtime).val(dateUtil.getToday(isDateTime,"end"));
            }else if(idStr=="my_date_thisyear"){
                $("#"+confirmstarttime).val(dateUtil.getYearStart(isDateTime,"start"));
                $("#"+confirmendtime).val(dateUtil.getToday(isDateTime,"end"));
            }else if(idStr=="my_date_lastmonth"){
                $("#"+confirmstarttime).val(dateUtil.getFirstDayOfLastMonth(isDateTime,"start"));
                $("#"+confirmendtime).val(dateUtil.getEndDayOfLastMonth(isDateTime,"end"));
            }else if(idStr=="my_date_lastyear"){
                $("#"+confirmstarttime).val(dateUtil.getLastYearStart(isDateTime,"start"));
                $("#"+confirmendtime).val(dateUtil.getLastYearEnd(isDateTime,"end"));
            }
            else if(idStr=="my_date_oneWeekBefore"){
                $("#"+confirmstarttime).val(dateUtil.getOneWeekBefore(isDateTime,"start"));
                $("#"+confirmendtime).val(dateUtil.getToday(isDateTime,"end"));
            }
            else if(idStr=="my_date_threeMonthBefore"){
                $("#"+confirmstarttime).val(dateUtil.getThreeMonthStartDate(isDateTime,"start"));
                $("#"+confirmendtime).val(dateUtil.getToday(isDateTime,"end"));
            }else if(idStr == "my_date_self_defining"){
               /* $("#"+confirmstarttime).val("");
                $("#"+confirmendtime).val("");*/
            }else if(idStr == "my_date_self_defining_new"){
                /* $("#"+confirmstarttime).val("");
                 $("#"+confirmendtime).val("");*/
            }
        } else {
            $(this).removeClass("clicked");
            $("#"+confirmstarttime).val("");
            $("#"+confirmendtime).val("");
        }

        var _pageType = $(this).attr("pagetype");
        if (_pageType == "customerdata") {
          $("#customerSearchBtn").click();
        }
        if (_pageType == "supplierdata") {
          $("#supplierSearchBtn").click();
        }
        if(_pageType == "vehicleData"){
            $("#searchVehicleBtn").click();
        }

        if (_pageType == "advertList") {
          $("#searchBtn").click();
        }
    });




});


//动态绑定一对时间选择器，开始时间只能选择结束时间之前，结束时间只能在开始时间之后，清空内容的时候清掉约束条件
dateUtil.bindPairDatePicker = function ($startDate, $endDate, options) {
    var defaultDateOptions = {
        "changeYear": true,
        "changeMonth": true,
        "yearRange": "c-5:c+5",
        "yearSuffix": "",
        "showButtonPanel": true
    };
    var startDateOptions = $.extend({}, defaultDateOptions,
        {"beforeShow": function () {
            var endDate = G.Lang.isEmpty($endDate.val()) ? null : $.datepicker.parseDate("yy-mm-dd", $endDate.val());
            $startDate.datepicker("option", "maxDate", endDate);
        }
        }, options);

    var endDateOptions = $.extend({}, defaultDateOptions,
        {"beforeShow": function () {
            var startDate = G.Lang.isEmpty($startDate.val()) ? null : $.datepicker.parseDate("yy-mm-dd", $startDate.val());
            $endDate.datepicker("option", "minDate", startDate);
        }
        }, options);
    $startDate.datepicker(startDateOptions);
    $endDate.datepicker(endDateOptions);
};