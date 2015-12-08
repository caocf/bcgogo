/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-5-7
 * Time: 下午2:01
 * To change this template use File | Settings | File Templates.
 */
function changeDate(day, month, year, type) {
    day = day * 1;
    month = month * 1;
    year = year * 1;
    if (type == 'addDay') {
        return addDay(day, month, year);
    }
    if (type == 'minusDay') {
        return minusDay(day, month, year);
    }
    if (type == 'addMonth') {
        return addMonth(day, month, year);
    }
    if (type == 'minusMonth') {
        return minusMonth(day, month, year);
    }
    if (type == 'addYear') {
        return addYear(day, month, year);
    }
    if (type == 'minusYear') {
        return minusYear(day, month, year);
    }
}


function checkIsLongYear(year) {
    if (year % 4 == 0 || (year % 100 == 0 && year % 400 == 0)) {
        return true;
    } else {
        return false;
    }
}

function addDay(day, month, year) {
    if (month == 2) {
        if (checkIsLongYear(year)) {
            if (day < 29) {
                day++;
            } else {
                day = 1;
                month++;
            }
        } else {
            if (day < 28) {
                day++;
            } else {
                day = 1;
                month++;
            }
        }
    } else if (month == 4 || month == 6 || month == 9 || month == 11) {
        if (day < 30) {
            day++;
        } else {
            day = 1;
            month++;
        }
    } else if (month == 12) {
        if (day < 31) {
            day++;
        } else {
            day = 1;
            month = 1;
            year++;
        }
    } else {
        if (day < 31) {
            day++;
        } else {
            day = 1;
            month++;
        }
    }
    return day + ";" + month + ";" + year;
}

function minusDay(day, month, year) {
    if (month == 3) {
        if (checkIsLongYear(year)) {
            if (day > 1) {
                day--;
            } else {
                day = 29;
                month--;
            }
        } else {
            if (day > 1) {
                day--;
            } else {
                day = 28;
                month--;
            }
        }
    } else if (month == 1) {
        if (day > 1) {
            day--;
        } else {
            day = 31;
            month = 12;
            year--;
        }
    } else if (month == 5 || month == 7 || month == 10 || month == 12) {
        if (day > 1) {
            day--;
        } else {
            day = 30;
            month--;
        }
    } else {
        if (day > 1) {
            day--;
        } else {
            day = 31;
            month--;
        }
    }
    return day + ";" + month + ";" + year;
}

function addMonth(day, month, year) {
    if (month == 1) {
        if (checkIsLongYear(year)) {
            if (day > 29) {
                month++;
                day = 29;
            } else {
                month++;
            }
        } else {
            if (day > 28) {
                month++;
                day = 28;
            } else {
                month++;
            }
        }
    } else if (month == 3 || month == 5 || month == 8 || month == 10) {
        if (day > 30) {
            month++;
            day = 30;
        } else {
            month++;
        }
    } else if (month == 12) {
        month = 1;
        year++;
    } else {
        month++;
    }

    return day + ";" + month + ";" + year;
}

function minusMonth(day, month, year) {
    if (month == 1) {
        month = 12;
        year--;
    } else if (month == 3) {
        if (checkIsLongYear(year)) {
            if (day > 29) {
                day = 29;
                month--;
            } else {
                month--;
            }
        } else {
            if (day > 28) {
                day = 28;
                month--;
            } else {
                month--;
            }
        }
    } else if (month == 5 || month == 7 || month == 10 || month == 12) {
        if (day > 30) {
            day = 30;
            month--;
        } else {
            month--;
        }
    } else {
        month--;
    }
    return day + ";" + month + ";" + year;
}

function addYear(day, month, year) {
    if (month == 2 && checkIsLongYear(year) && day == 29) {
        day = 28;
        year++;
    } else {
        year++;
    }
    return day + ";" + month + ";" + year;
}

function minusYear(day, month, year) {
    if (month == 2 && checkIsLongYear(year) && day == 29) {
        year--;
        day = 28;
    } else {
        year--;
    }
    return day + ";" + month + ";" + year;
}


function initDate(type, statType) {
  var dayStr = $("#day").text();
  var monthStr = $("#month").text();
  var yearStr = $("#year").text();
  var day = (dayStr.split("日")[0]) * 1;
  var month = (monthStr.split("月")[0]) * 1;
  var year = (yearStr.split("年")[0]) * 1;
  var dateStr = changeDate(day, month, year, type);
  var newDay = dateStr.split(";")[0] + "日";
  var newMonth = dateStr.split(";")[1] + "月";
  var newYear = dateStr.split(";")[2] + "年";
  $("#dayHid").val(dateStr.split(";")[0]);
  $("#monthHid").val(dateStr.split(";")[1]);
  $("#yearHid").val(dateStr.split(";")[2]);
  $("#queryType").val(dateStr.split(";")[0]);
  $("#day").text(newDay);
  $("#month").text(newMonth);
  $("#year").text(newYear);

  if (statType = "runningStat") {
    $("#currentDayIncome").text(newMonth + newDay);
    $("#currentMonthIncome").text(newYear + newMonth);
    $("#currentYearIncome").text(newYear);
    $("#currentDayExpenditure").text(newMonth + newDay);
    $("#currentMonthExpenditure").text(newYear + newMonth);
    $("#currentYearExpenditure").text(newYear);
    $("#selectDayHid").val(dateStr.split(";")[0]);
    $("#selectMonthHid").val(dateStr.split(";")[1]);
    $("#selectYearHid").val(dateStr.split(";")[2]);
  }
}