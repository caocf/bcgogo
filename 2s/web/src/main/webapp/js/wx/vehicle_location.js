;
var map;
$(function () {

    if (eResultJson) {
        var eResult = JSON.parse(eResultJson);
        var bcAlert = APP_BCGOGO.Module.bcAlert;
        bcAlert.login({
            info:eResult.errorMsg,
            level:eResult.level
        });
        return;
    }

    if (ticketSignJson) {
        var ticketSign = JSON.parse(ticketSignJson);
        wx.config({
            debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
            appId: ticketSign.appId, // 必填，公众号的唯一标识
            timestamp: ticketSign.timestamp, // 必填，生成签名的时间戳
            nonceStr: ticketSign.noncestr, // 必填，生成签名的随机串
            signature: ticketSign.signature,// 必填，签名，见附录1
            jsApiList: ['getLocation'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
        });

    }

    try {
        //定位到车辆
        map = new BMap.Map("container");
        var lon = $(".j_vehicle_location").attr("lon");
        var lat = $(".j_vehicle_location").attr("lat");
        var carPoint = new BMap.Point(lon, lat);
        map.centerAndZoom(carPoint, 14);
        map.enableScrollWheelZoom(true);
        map.clearOverlays();
        var carMarker = new BMap.Marker(carPoint);  // 创建标注
        map.addOverlay(carMarker);              // 将标注添加到地图中
        map.panTo(carPoint);

    } catch (e) {
        console.log(e);
    }

});

$(function () {

    $("#findCarBtn").click(function () {
        map.clearOverlays();
        map.addOverlay(carMarker);
        map.panTo(carPoint);
        $("#walkingPath").hide();
        $(this).removeClass("light_blue");
        $("#calDistance").addClass("light_blue");
    });

    $("#calDistance").click(function () {
        var _$me = $(this);
        _$me.removeClass("light_blue");
        $("#findCarBtn").addClass("light_blue");
        wx.getLocation({
            type: 'wgs84',
            success: function (res) {
//                $(".j_error_info").text("success,lat:"+res.latitude+" lon:"+res.longitude)
                var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
                var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
                var mePoint = new BMap.Point(longitude, latitude);
                //坐标转换完之后的回调函数
                var translateCallback = function (point) {
                    var myIcon = new BMap.Icon("http://developer.baidu.com/map/jsdemo/img/fox.gif", new BMap.Size(80, 80));
                    var marker2 = new BMap.Marker(point, {icon: myIcon});  // 创建标注
                    map.addOverlay(marker2);
//                     $(".j_error_info").text("addOverlay success");
                    var walking = new BMap.WalkingRoute(map);
                    walking.search(carPoint, point);
                    //下面添加回调方法，绘制路线
                    walking.setSearchCompleteCallback(function () {
                        var plan = walking.getResults().getPlan(0);
                        //定义折线并添加到地图上
                        var polyLine = new BMap.Polyline(plan.getRoute(0).getPath());
                        map.addOverlay(polyLine);
                        //调整视野
                        map.setViewport([carPoint, point]);
                        $("#distance").text(plan.getDistance(true));
                        $("#duration").text(plan.getDuration(true));
                        $("#walkingPath").show();
//                         $(".j_error_info").text("walking finished");
                    });
                }
                BMap.Convertor.translate(mePoint, 0, translateCallback);
            },
            fail: function (e) {
                console.error(e);
            }
        });
    });

});


