/**
 * User: Xiao Jian
 * Date: 12-1-9
 */

(function() {
    /*$().ready(function() {
     $("#button_day_previous")[0].onclick = function() {
     changeDate(new Date(window.bizstat.statYear, window.bizstat.statMonth - 1, window.bizstat.statDay - 1));
     };

     $("#button_day_next")[0].onclick = function() {
     changeDate(new Date(window.bizstat.statYear, window.bizstat.statMonth - 1, window.bizstat.statDay + 1));
     };

     $("#button_week_previous")[0].onclick = function() {
     changeDate(new Date(window.bizstat.statYear, window.bizstat.statMonth - 1, window.bizstat.statDay - 7));
     };

     $("#button_week_next")[0].onclick = function() {
     changeDate(new Date(window.bizstat.statYear, window.bizstat.statMonth - 1, window.bizstat.statDay + 7));
     };
     $("#button_month_previous")[0].onclick = function() {
     changeDate(new Date(window.bizstat.statYear, window.bizstat.statMonth - 2, window.bizstat.statDay));
     };
     $("#button_month_next")[0].onclick = function() {
     changeDate(new Date(window.bizstat.statYear, window.bizstat.statMonth, window.bizstat.statDay));
     };
     $("#button_year_previous")[0].onclick = function() {
     changeDate(new Date(window.bizstat.statYear - 1, window.bizstat.statMonth - 1, window.bizstat.statDay));
     };
     $("#button_year_next")[0].onclick = function() {
     changeDate(new Date(window.bizstat.statYear + 1, window.bizstat.statMonth - 1, window.bizstat.statDay));
     };
     });*/

    function changeDate(date) {
        window.location.assign("bizstat.do?method=bizstat&date=" + date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate());
    }

    function init() {
        var date = "" + window.bizstat.statYear + "-" + window.bizstat.statMonth + "-" + window.bizstat.statDay;
        $("#radMonth").click(function() {
            $("#radMonth").attr("class", "r_on");
            $("#radWeek").attr("class", "rad_off");
            $("#radDay").attr("class", "rad_off");
        });

        $("#radWeek").click(function() {
            $("#radMonth").attr("class", "rad_off");
            $("#radWeek").attr("class", "r_on");
            $("#radDay").attr("class", "rad_off");

        });

        $("#radDay").click(function() {
            $("#radMonth").attr("class", "rad_off");
            $("#radWeek").attr("class", "rad_off");
            $("#radDay").attr("class", "r_on");

        });
        $("#radMonth").click();
    }

})();