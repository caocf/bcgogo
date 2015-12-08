/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-9-5
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */

    // author:zhangjuntao
var time = new Array(), timeFlag = true;
time[0] = new Date().getTime();
time[1] = new Date().getTime();
function notOpen() {
    var reg = /^(\d+)$/;
    time[1] = new Date().getTime();
    if (time[1] - time[0] > 3000 || timeFlag) {
        time[0] = time[1];
        timeFlag = false;
        showMessage.fadeMessage("35%", "40%", "slow", 3000, "此功能稍后开放！");     // top left fadeIn fadeOut message
    }

}
function initRunningStat() {
    var year = $("#yearHid").val();
    var month = $("#monthHid").val();
    var day = $("#dayHid").val();
    $("#radMonth")[0].onclick = function () {
        $("#radMonth")[0].className = "r_on";
        $("#radYear")[0].className = "rad_off";
        $("#radDay")[0].className = "rad_off";
        $("#monthIncome").click();
    };
    $("#radYear")[0].onclick = function () {
        $("#radMonth")[0].className = "rad_off";
        $("#radYear")[0].className = "r_on";
        $("#radDay")[0].className = "rad_off";
        $("#yearIncome").click();
    };
    $("#radDay")[0].onclick = function () {
        $("#radMonth")[0].className = "rad_off";
        $("#radYear")[0].className = "rad_off";
        $("#radDay")[0].className = "r_on";
        $("#dayIncome").click();
    };

    if ($("#radMonth")[0].className == "r_on") {
        $("#radMonth").click();
    }
    else if ($("#radDay")[0].className == "r_on") {
        $("#radDay").click();
    }
    else if ($("#radYear")[0].className == "r_on") {
        $("#radYear").click();
    }
    else {
        $("#radDay").click();
    }
}