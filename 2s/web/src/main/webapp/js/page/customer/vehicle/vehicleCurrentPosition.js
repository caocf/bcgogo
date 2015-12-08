/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 14-2-7
 * Time: 下午12:05
 * To change this template use File | Settings | File Templates.
 */


$(document).ready(function () {


    $("#gsmObdImei,#gsmObdImeiMoblie")
        .bind('click', function () {
            getVehicleGsmOBdSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getVehicleGsmOBdSuggestion($(this));
            }
        });

    $("#engineNo,#chassisNumber")
        .bind('click', function () {
            getVehicleEngineNoClassNoSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getVehicleGsmOBdSuggestion($(this));
            }
        });

    function getVehicleEngineNoClassNoSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var currentSearchField = $domObject.attr("searchField");

        var droplist = APP_BCGOGO.Module.droplist;
        droplist.setUUID(GLOBAL.Util.generateUUID());
        var ajaxUrl = "searchInventoryIndex.do?method=getVehicleEngineNoClassNoSuggestion";
        var ajaxData = {
            searchWord: searchWord,
            searchField: currentSearchField,
            uuid: droplist.getUUID()
        };
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            droplist.show({
                "selector": $domObject,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.label);
                    $domObject.css({"color": "#000000"});
                    droplist.hide();
                    searchVehicleList();
                }
            });
        });
    }


    $("#licenceNo")
        .bind('click', function () {
            getVehicleGsmOBdSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getVehicleGsmOBdSuggestion($(this));
            }
        });


    function getVehicleLicenceNoSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
//        if (GLOBAL.Lang.isEmpty(searchWord)) return;
        var droplist = APP_BCGOGO.Module.droplist;
        droplist.setUUID(GLOBAL.Util.generateUUID());
        var ajaxUrl = "searchInventoryIndex.do?method=getVehicleLicenceNoSuggestion";
        var ajaxData = {
            searchWord: searchWord,
            uuid: droplist.getUUID()
        };
        APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            droplist.show({
                "selector": $domObject,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.label);
                    $domObject.css({"color": "#000000"});
                    droplist.hide();
                    searchVehicleList();
                }
            });
        });
    }

    $("#customerInfo")
        .bind('click', function () {
            getCustomerSuggestion($(this));
        })
        .bind('keyup', function (event) {
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) == -1) {
                getCustomerSuggestion($(this));
            }
        });

    function getCustomerSuggestion($domObject) {
        var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
        var dropList = App.Module.droplist;
        dropList.setUUID(G.generateUUID());
        var ajaxData = {
            searchWord: searchWord,
            searchField: "info",
            searchFieldStrategies: "searchIncludeMemberNo",
            customerOrSupplier: "customer",
            titles: "name,contact,mobile,memberNo",
            uuid: dropList.getUUID()
        };
        var ajaxUrl = "searchInventoryIndex.do?method=getCustomerSupplierSuggestion";
        App.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
            dropList.show({
                "selector": $domObject,
                "autoSet": false,
                "data": result,
                "onSelect": function (event, index, data) {
                    $domObject.val(data.details.name);
                    $domObject.css({"color": "#000000"});
                    dropList.hide();
                    searchVehicleList();
                }
            });
        });

    }

    $("#clearConditionBtn").bind('click', function () {
        $(".J_clear_input").val("");
        $(".J_initialCss").placeHolder("reset");
    });

    //点击查询按钮
    $("#searchVehicleBtn").bind("click", function () {
        searchVehicleList();
    });


    //初始化
    $(".J_initialCss").placeHolder();


    if (G.isNotEmpty($("#vehicleIdStr").val())) {
        searchVehicleList();
    } else {
//    $("#noDateDiv").html("请输入查询条件进行车辆定位").show();
//    $("#map_container_iframe").hide();
//    var $iframe = $("#map_container_iframe");
//    var str ="&coordinate=" +$("#coordinateLon").val()+"_"+$("#coordinateLat").val();
//
//    $iframe[0].src = "api/proxy/baidu/map/vehiclePosition?data=" + str;
        noDate();

    }

});


function searchVehicleList() {

    $(".J_initialCss").placeHolder("clear");

    if (G.isNotEmpty($('#licenceNo').val()) && !App.Validator.stringIsLicensePlateNumber($('#licenceNo').val().replace(/\s|\-/g, ""))) {
        nsDialog.jAlert("输入的车牌号码不符合规范，请检查！");
        return false;
    }


    var paramForm = $("#searchVehicleListForm").serializeArray();
    var param = {};
    $.each(paramForm, function (index, val) {
        param[val.name] = val.value;
    });
    param["sortStatus"] = "vehicleLastConsumeTimeDesc";


    $(".J_initialCss").placeHolder("reset");
    APP_BCGOGO.Net.syncPost({
        url: "vehicleManage.do?method=queryVehiclePosition",
        dataType: "json",
        data: param,
        success: function (result) {
            if (result.success) {  OBD
                drawVehicleListTable(result.data);
            } else {
                nsDialog.jAlert(result.msg);
////        $("#noDateDiv").html("暂无车辆定位数据").show();
////        $("#map_container_iframe").hide();
//        var $iframe = $("#map_container_iframe");
//        var str ="&coordinate=" +$("#coordinateLon").val()+"_"+$("#coordinateLat").val();
//
//        $iframe[0].src = "api/proxy/baidu/map/vehiclePosition?data=" + str;
                noDate();
            }
        },
        error: function () {
            nsDialog.jAlert("数据异常，请刷新页面！");
        }
    });
}


function noDate() {
    // var $iframe = $("#map_container_iframe");
    // var str = "&city=" + $("#city").val() + "&coordinate=" +$("#coordinateLon").val() + "_" + $("#coordinateLat").val();

    // $iframe[0].src = "api/proxy/baidu/map/vehiclePosition?data=" + str;


    var lon = $("#coordinateLon").val();
    var lat = $("#coordinateLat").val();
    var map = new BMap.Map("allmap");
    var point = new BMap.Point(lon, lat);
    map.centerAndZoom(point, 17);
    // 创建标注
    var marker = new BMap.Marker(point);
    map.addOverlay(marker);

    var mapType1 = new BMap.MapTypeControl({mapTypes: [BMAP_NORMAL_MAP,BMAP_HYBRID_MAP]});
    var mapType2 = new BMap.MapTypeControl({anchor: BMAP_ANCHOR_TOP_LEFT});

    var overView = new BMap.OverviewMapControl();
    var overViewOpen = new BMap.OverviewMapControl({isOpen:true, anchor: BMAP_ANCHOR_BOTTOM_RIGHT});
    map.addControl(mapType1);          //2D图，卫星图
    map.addControl(mapType2);          //左上角，默认地图控件
    map.setCurrentCity("北京");        //由于有3D图，需要设置城市哦
    map.addControl(overView);          //添加默认缩略地图控件
    map.addControl(overViewOpen);      //右下角，打开

}


function drawVehicleListTable(json) {

    if (json == null || json.length == 0) {
//    $("#noDateDiv").html("暂无车辆定位数据").show();
//    $("#map_container_iframe").hide();

        noDate();
        return;
    }
//    $("#noDateDiv").hide();
//    var $iframe = $("#map_container_iframe");
//    $iframe[0].src = "api/proxy/baidu/map/vehiclePosition?data=" + json;
//    //+ JSON.stringify(driveLogDTO);
//    $iframe[0].style.display = "block";

//
//  var map = new BMap.Map("allmap");
////  var url = location.search; //获取url中"?"符后的字串
//  var url  = "?"+json;
//  var strs = null;
//  if (url.indexOf("?") != -1) {
//    var str = url.substr(1);
//    strs = str.split(",,,");
//  }
//  if (strs.length <= 0) {
//    return;
//  }
//
//
//  var array = new Array(strs.length);
//
//  for (var index = 0; index < strs.length; index++) {
//    var gsmPointDTO = strs[index].split("__");
//    var lat = gsmPointDTO[0];
//    var lon = gsmPointDTO[1];
//    var point = new BMap.Point(lon, lat);
//    array[index] = point;
//  }
//
//  map.setViewport(array);
//  var zoom = map.getZoom();
//  map.setZoom(zoom - 1);
//  map.addControl(new BMap.NavigationControl());  //添加默认缩放平移控件
//  map.addControl(new BMap.NavigationControl({anchor: BMAP_ANCHOR_TOP_RIGHT, type: BMAP_NAVIGATION_CONTROL_SMALL}));  //右上角，仅包含平移和缩放按钮
//  map.addControl(new BMap.NavigationControl({anchor: BMAP_ANCHOR_BOTTOM_LEFT, type: BMAP_NAVIGATION_CONTROL_PAN}));  //左下角，仅包含平移按钮
//  map.addControl(new BMap.NavigationControl({anchor: BMAP_ANCHOR_BOTTOM_RIGHT, type: BMAP_NAVIGATION_CONTROL_ZOOM}));  //右下角，仅包含缩放按钮
//  map.enableScrollWheelZoom();
//  map.enableDoubleClickZoom();
//
//  for (var index = 0; index < strs.length; index++) {
//    var gsmPointDTO = strs[index].split("__");
//    var lat = gsmPointDTO[0].substr(0, 6);
//    var lon = gsmPointDTO[1].substr(0, 6);
//
//    var uploadServerTimeStr = gsmPointDTO[2];
//    var address = decodeURI(gsmPointDTO[3]);
//    var vehicleNo = decodeURI(gsmPointDTO[4]);
//
//    // 百度地图API功能
//    var sContent =
//        "<p style='margin:0;line-height:1.0;font-size:13px;text-indent:1em'>车牌号:" + vehicleNo + "</p>" +
//            "<p style='margin:0;line-height:1.0;font-size:13px;text-indent:1em'>经度:" + lon + " 纬度:" + lat + "</p>" +
////        "<p style='margin:0;line-height:1.5;font-size:13px;text-indent:2em'>速度:"+ speed+"</p></br>" +
//        "<p style='margin:0;line-height:1.0;font-size:13px;text-indent:1em'>地址:" + address + "</p>" +
//        "<p style='margin:0;line-height:1.0;font-size:13px;text-indent:1em'>时间:" + uploadServerTimeStr + "</p>";
//    var gpsPoint = null;
//    var marker = null;
//
//    var opts = {
//      width: 80,     // 信息窗口宽度
//      height: 80,     // 信息窗口高度
//      title: "车辆定位", // 信息窗口标题
//      enableMessage: false,//设置允许信息窗发送短息
//      message: ""
//    }
//
//    gpsPoint = new BMap.Point(lon, lat);
//    marker = new BMap.Marker(gpsPoint);
//    map.addOverlay(marker);
//
//    var infoWindow = new BMap.InfoWindow(sContent, opts);  // 创建信息窗口对象
//    marker.openInfoWindow(infoWindow);
//    marker.open = "true";
//
//    marker.addEventListener("click", function () {
//      if (this.open == "true") {
//        this.closeInfoWindow(infoWindow);
//        this.open = "false";
//      } else {
//        this.openInfoWindow(infoWindow);
//        this.open == "true"
//      }
//    });
//  }


//  // 百度地图API功能
//  var map = new BMap.Map("allmap");
//  var point = new BMap.Point(116.404, 39.915);
//  map.centerAndZoom(point, 15);
//// 编写自定义函数,创建标注
//  function addMarker(point){
//    var marker = new BMap.Marker(point);
//    map.addOverlay(marker);
//  }
//// 随机向地图添加25个标注
//  var bounds = map.getBounds();
//  var sw = bounds.getSouthWest();
//  var ne = bounds.getNorthEast();
//  var lngSpan = Math.abs(sw.lng - ne.lng);
//  var latSpan = Math.abs(ne.lat - sw.lat);
//  for (var i = 0; i < 25; i ++) {
//    var point = new BMap.Point(sw.lng + lngSpan * (Math.random() * 0.7), ne.lat - latSpan * (Math.random() * 0.7));
//    addMarker(point);
//  }

//  // 百度地图API功能
////GPS坐标
//  var xx = 116.397428;
//  var yy = 39.90923;
//  var gpsPoint = new BMap.Point(xx,yy);
//
////地图初始化
//  var bm = new BMap.Map("allmap");
//  bm.centerAndZoom(gpsPoint, 15);
//  bm.addControl(new BMap.NavigationControl());
//
////添加谷歌marker和label
//  var markergps = new BMap.Marker(gpsPoint);
//  bm.addOverlay(markergps); //添加GPS标注
//  var labelgps = new BMap.Label("我是GPS标注哦",{offset:new BMap.Size(20,-10)});
//  markergps.setLabel(labelgps); //添加GPS标注
//
////坐标转换完之后的回调函数
//  translateCallback = function (point){
//    var marker = new BMap.Marker(point);
//    map.addOverlay(marker);
//  }
//
//  setTimeout(function(){BMap.Convertor.translate(gpsPoint,0,translateCallback);     //真实经纬度转成百度坐标
//
//  }, 2000);


//  var map = new BMap.Map("allmap");
//
//  var array = new Array(json.length);
//  $.each(json, function (index, gsmPointDTO) {
//    var lat = gsmPointDTO.baiDuLat;
//    var lon = gsmPointDTO.baiDuLon;
//    var point = new BMap.Point(lon, lat);
//    array[index] = point;
//  });
//  map.setViewport(array);
//  var zoom = map.getZoom();
//  map.setZoom(zoom - 1);
//  map.addControl(new BMap.NavigationControl());  //添加默认缩放平移控件
//  map.addControl(new BMap.NavigationControl({anchor: BMAP_ANCHOR_TOP_RIGHT, type: BMAP_NAVIGATION_CONTROL_SMALL}));  //右上角，仅包含平移和缩放按钮
//  map.addControl(new BMap.NavigationControl({anchor: BMAP_ANCHOR_BOTTOM_LEFT, type: BMAP_NAVIGATION_CONTROL_PAN}));  //左下角，仅包含平移按钮
//  map.addControl(new BMap.NavigationControl({anchor: BMAP_ANCHOR_BOTTOM_RIGHT, type: BMAP_NAVIGATION_CONTROL_ZOOM}));  //右下角，仅包含缩放按钮
//  map.enableScrollWheelZoom();
//  map.enableDoubleClickZoom();
//
//  $.each(json, function (index, gsmPointDTO) {
//
//    var lat = gsmPointDTO.baiDuLat;
//    var lon = gsmPointDTO.baiDuLon;
//    var speed = gsmPointDTO.velocity;
//    var uploadServerTimeStr = gsmPointDTO.uploadServerTimeStr;
//    var address = gsmPointDTO.address;
//    var vehicleNo = gsmPointDTO.vehicleNo;
//
//    // 百度地图API功能
//    var sContent =
//        "<p style='margin:0;line-height:1.0;font-size:13px;text-indent:1em'>车牌号:" + vehicleNo + "</p>" +
//    "<p style='margin:0;line-height:1.0;font-size:13px;text-indent:1em'>经度:" + dataTransition.rounding(lon,2)  + " 纬度:" + dataTransition.rounding(lat,2) + "</p>" +
////        "<p style='margin:0;line-height:1.5;font-size:13px;text-indent:2em'>速度:"+ speed+"</p></br>" +
//        "<p style='margin:0;line-height:1.0;font-size:13px;text-indent:1em'>地址:" + address + "</p>" +
//        "<p style='margin:0;line-height:1.0;font-size:13px;text-indent:1em'>时间:" + uploadServerTimeStr + "</p>";
//    var gpsPoint = null;
//    var marker = null;
//
//    var opts = {
//      width: 80,     // 信息窗口宽度
//      height: 80,     // 信息窗口高度
//      title: "车辆定位", // 信息窗口标题
//      enableMessage: false,//设置允许信息窗发送短息
//      message: ""
//    }
//
//    gpsPoint = new BMap.Point(lon, lat);
//    marker = new BMap.Marker(gpsPoint);
//    map.addOverlay(marker);
//
//    var infoWindow = new BMap.InfoWindow(sContent, opts);  // 创建信息窗口对象
//    marker.openInfoWindow(infoWindow);
//
//    marker.addEventListener("click", function () {
//      if ($(this).attr("open") == "true") {
//        this.closeInfoWindow(infoWindow);
//        $(this).attr("open", "false");
//      } else {
//        this.openInfoWindow(infoWindow);
//        $(this).attr("open", "true");
//      }
//    });
//  });


}

function getVehicleGsmOBdSuggestion($domObject) {
    var searchWord = $domObject.val().replace(/[\ |\\]/g, "");
    var currentSearchField = $domObject.attr("searchField");

    var droplist = APP_BCGOGO.Module.droplist;
    droplist.setUUID(GLOBAL.Util.generateUUID());
    var ajaxUrl = "searchInventoryIndex.do?method=getVehicleEngineNoClassNoSuggestion";
    var ajaxData = {
        searchWord: searchWord,
        searchField: currentSearchField,
        uuid: droplist.getUUID()
    };
    APP_BCGOGO.wjl.LazySearcher.lazySearch(ajaxUrl, ajaxData, function (result) {
        droplist.show({
            "selector": $domObject,
            "data": result,
            "onSelect": function (event, index, data) {
                $domObject.val(data.label);
                $domObject.css({"color": "#000000"});
                droplist.hide();
                searchVehicleList();
            }
        });
    });
}


