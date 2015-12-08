;
var points = new Array();
var map; //百度地图对象
var car; //汽车图标
var timer; //定时器

$(function () {
    //初始化地图,选取第一个点为起始点
    map = new BMap.Map("container");
    map.centerAndZoom(points[0], 15);
    map.enableScrollWheelZoom();
    //图平移缩放控件
    map.addControl(new BMap.NavigationControl());
    //比例尺控件，默认位于地图左下方，显示地图的比例关系
    map.addControl(new BMap.ScaleControl());
    //通过DrivingRoute获取一条路线的point
    var driving = new BMap.DrivingRoute(map);
    //var driving = new BMap.DrivingRoute(map, {renderOptions:{map: map, autoViewport: true}});
    driving.search(points[0], points[points.length - 1]);
    driving.setSearchCompleteCallback(function () {
        //连接所有点
        //map.addOverlay(new BMap.Polyline(points, {strokeColor: "black", strokeWeight: 5, strokeOpacity: 1}));
        //显示小车子,默认小红点
        car = new BMap.Marker(points[0]);
        map.addOverlay(car);
        map.panTo(points[0]);
    });

    $("#play").click(function () {
        if (timer) {
            window.clearTimeout(timer);
        }
        map.clearOverlays();
        map.clearOverlays();
        car = new BMap.Marker(points[0]);
        map.addOverlay(car);
        map.setViewport([points[0], points[points.length - 1]]);
        play(0);
    });

    $(".j_delete_btn").click(function () {
        if (!confirm("是否确认删除行车轨迹？")) {
            return false;
        }
        var driveLogId = $("#driveLogId").val();
        var startTime = Number($("#startTime").val());
        var endTime = Number($("#endTime").val());
        var openId = $("#openId").val();
        var appUserNo = $("#appUserNo").val();
        if (driveLogId) {
            $.ajax({
                type: "POST",
                url: "/web/mirror/wx/driveLog/delete/" + driveLogId,
                dataType: "json",
                async: false,
                success: function (result) {
//                    if (!result.success) {
//                        alert(result.msg)
//                        return;
//                    }
                    var url = "/web/mirror/2DriveLog/" + openId + "/" + startTime + "/" + endTime+"/"+appUserNo;
                    window.location.href = url;
                }
            });
        }
    });

});

function play(index) {
    var point = points[index];
    if (index > 0) {
        //添加覆盖物
        var overlay = new BMap.Polyline([points[index - 1], point], {
            strokeColor: "red",
            strokeWeight: 1,
            strokeOpacity: 1
        });
        map.addOverlay(overlay);
    }
    car.setPosition(point);
    index++;
    map.panTo(point);
    if (index < points.length) {
        timer = window.setTimeout("play(" + index + ")", 300);
    } else {
        map.panTo(point);
    }
}

function initPoints(placeNotesStr) {
    if (!placeNotesStr) return;
    var placeNotes = placeNotesStr.split("|");
    for (var i = 0; i < placeNotes.length; i++) {
        var placeNote = placeNotes[i];
        var lng = placeNote.split(",")[0];
        var lat = placeNote.split(",")[1];
        if (lat && lng) {
            var point = new BMap.Point(lat, lng);
            points.push(point);
            //BMap.Convertor.translate(point, 0, translateCallback);
        }
    }
}

//function translateCallback(point) {
//    points.push(point);
//
//}

