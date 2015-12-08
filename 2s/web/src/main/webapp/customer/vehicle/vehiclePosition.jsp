<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%--<head>--%>
<%--<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />--%>
<%--<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />--%>
<%--<style type="text/css">--%>
<%--body, html,#allmap {width: 100%;height: 100%;overflow: hidden;margin:0;}--%>
<%--</style>--%>
<%--<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=您的密钥"></script>--%>
<%--<title>纯文本的信息窗口</title>--%>
<%--</head>--%>
<%--<body>--%>
<%--<div id="allmap"></div>--%>
<%--</body>--%>
<%--</html>--%>
<%--<script type="text/javascript">--%>

<%--// 百度地图API功能--%>
<%--var map = new BMap.Map("allmap");--%>
<%--var point = new BMap.Point(116.417854,39.921988);--%>
<%--map.centerAndZoom(point, 15);--%>
<%--var opts = {--%>
  <%--width : 200,     // 信息窗口宽度--%>
  <%--height: 60,     // 信息窗口高度--%>
  <%--title : "海底捞王府井店" , // 信息窗口标题--%>
  <%--enableMessage:true,//设置允许信息窗发送短息--%>
  <%--message:"亲耐滴，晚上一起吃个饭吧？戳下面的链接看下地址喔~"--%>
<%--}--%>
<%--var infoWindow = new BMap.InfoWindow("地址：北京市东城区王府井大街88号乐天银泰百货八层", opts);  // 创建信息窗口对象--%>
<%--map.openInfoWindow(infoWindow,point); //开启信息窗口--%>
<%--</script>--%>


<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<style type="text/css">
body, html,#allmap {width: 100%;height: 100%;overflow: hidden;margin:0;}
</style>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=760f39e8b3f09ae5a4d3a0c7b97bc504"></script>
<title>添加复杂内容的信息窗口</title>
</head>
<body>
<div id="allmap"></div>
</body>
</html>
<script type="text/javascript">

// 百度地图API功能
var sContent =
"<h4 style='margin:0 0 5px 0;padding:0.2em 0'>车辆定位</h4>" +
//"<img style='float:right;margin:4px' id='imgDemo' src='http://app.baidu.com/map/images/tiananmen.jpg' width='139' height='104' title='天安门'/>" +
"<p style='margin:0;line-height:1.5;font-size:13px;text-indent:2em'>地址</p>" +
    "<p style='margin:0;line-height:1.5;font-size:13px;text-indent:2em'>地址</p></br>" +
    "<p style='margin:0;line-height:1.5;font-size:13px;text-indent:2em'>地址</p></br>" +

    "<p style='margin:0;line-height:1.5;font-size:13px;text-indent:2em'>地址</p></br>" +

"</div>";
var map = new BMap.Map("allmap");

map.addControl(new BMap.NavigationControl());  //添加默认缩放平移控件
map.addControl(new BMap.NavigationControl({anchor: BMAP_ANCHOR_TOP_RIGHT, type: BMAP_NAVIGATION_CONTROL_SMALL}));  //右上角，仅包含平移和缩放按钮
map.addControl(new BMap.NavigationControl({anchor: BMAP_ANCHOR_BOTTOM_LEFT, type: BMAP_NAVIGATION_CONTROL_PAN}));  //左下角，仅包含平移按钮
map.addControl(new BMap.NavigationControl({anchor: BMAP_ANCHOR_BOTTOM_RIGHT, type: BMAP_NAVIGATION_CONTROL_ZOOM}));  //右下角，仅包含缩放按钮

var point =null;
var marker = null;

var opts = {
  width : 300,     // 信息窗口宽度
  height: 200,     // 信息窗口高度
  title : "车辆定位" , // 信息窗口标题
  enableMessage:false,//设置允许信息窗发送短息
  message:""
}

for(var index=0;index<3;index++){
   point = new BMap.Point(116.404+index, 39.915+index);


   marker = new BMap.Marker(point);

  var infoWindow = new BMap.InfoWindow(sContent,opts);  // 创建信息窗口对象
  if(index ==1){
    map.centerAndZoom(point, 5);
  }
  map.addOverlay(marker);
  marker.addEventListener("click", function(){
     this.openInfoWindow(infoWindow);
     //图片加载完毕重绘infowindow
     document.getElementById('imgDemo').onload = function (){
         infoWindow.redraw();   //防止在网速较慢，图片未加载时，生成的信息框高度比图片的总高度小，导致图片部分被隐藏
     }
  });
}







</script>


<%--<head>--%>
<%--<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />--%>
<%--<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />--%>
<%--<style type="text/css">--%>
<%--body, html,#allmap {width: 100%;height: 100%;overflow: hidden;margin:0;}--%>
<%--</style>--%>
<%--<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=760f39e8b3f09ae5a4d3a0c7b97bc504"></script>--%>
<%--<script type="text/javascript" src="http://developer.baidu.com/map/jsdemo/demo/convertor.js"></script>--%>
<%--<title>GPS转百度</title>--%>
<%--</head>--%>
<%--<body>--%>
<%--<div id="allmap"></div>--%>
<%--</body>--%>
<%--</html>--%>
<%--<script type="text/javascript">--%>

<%--// 百度地图API功能--%>
<%--//GPS坐标--%>
<%--var xx = 116.397428;--%>
<%--var yy = 39.90923;--%>
<%--var gpsPoint = new BMap.Point(xx,yy);--%>

<%--//地图初始化--%>
<%--var bm = new BMap.Map("allmap");--%>
<%--bm.centerAndZoom(gpsPoint, 15);--%>
<%--bm.addControl(new BMap.NavigationControl());--%>

<%--//添加谷歌marker和label--%>
<%--var markergps = new BMap.Marker(gpsPoint);--%>
<%--bm.addOverlay(markergps); //添加GPS标注--%>
<%--var labelgps = new BMap.Label("我是GPS标注哦",{offset:new BMap.Size(20,-10)});--%>
<%--markergps.setLabel(labelgps); //添加GPS标注--%>

<%--//坐标转换完之后的回调函数--%>
<%--translateCallback = function (point){--%>
    <%--var marker = new BMap.Marker(point);--%>
    <%--bm.addOverlay(marker);--%>
    <%--var label = new BMap.Label("我是百度标注哦",{offset:new BMap.Size(20,-10)});--%>
    <%--marker.setLabel(label); //添加百度label--%>
    <%--bm.setCenter(point);--%>
    <%--alert(point.lng + "," + point.lat);--%>
<%--}--%>

<%--setTimeout(function(){--%>
    <%--BMap.Convertor.translate(gpsPoint,0,translateCallback);     //真实经纬度转成百度坐标--%>
<%--}, 2000);--%>
<%--</script>--%>
