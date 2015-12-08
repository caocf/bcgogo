<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>

    <link rel="stylesheet" href="../js/extension/jquery/plugin/jquery-mobile/jquery.mobile-1.3.2.css"/>
    <script src="../js/extension/jquery/jquery-1.9.1.js"></script>
    <script src="../js/extension/jquery/plugin/jquery-mobile/jquery.mobile-1.3.2.js"></script>
    <script type="text/javascript">
        function getLocation(callBack) {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(callBack);
            }
        }
        function callBack(position) {
            var x = document.getElementById("positionDemo");
            x.innerHTML = "Latitude: " + position.coords.latitude +
                    "<br />Longitude: " + position.coords.longitude;
        }
        $(function () {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function (position) {
                    $("#positionDemo").html("Latitude: " + position.coords.latitude + "<br />Longitude: " + position.coords.longitude);

                });
            }
        });
    </script>

</head>
<body >
<div data-role="page">
  <div data-role="header">
    <h1>Page Title</h1>
  </div><!-- /header -->
  <div data-role="content">
    <p>Page content goes here.</p>

      <label>IOS URL</label>      <a href="baidumap://map/marker?location=31.406709,120.604595&coord_type=gcj02&title=邦众汽车修理厂&content=邦众汽车修理厂内容&src=apple|浏览器">点击跳转到百度地图</a><br><br><br>
      <label>IOS URL2</label>    <a href="baidumap://map/geocoder?location=31.406709,120.604595&coord_type=gcj02&src=统购|行车一键通">点击跳转到百度地图</a><br><br><br>
      <label>安卓URL</label>      <a href="bdapp://map/marker?location=31.295727,120.616793&title=目标地址&content=客户目标地址&src=统购|行车一键通">点击跳转到百度地图</a><br><br><br>
      <%--<label>webUrl</label>       <a href="http://api.map.baidu.com/marker?location=31.295727,120.616793&title=我的位置&content=百度奎科大厦&output=html">点击跳转到网页的百度地图</a><br><br><br>--%>
      <label>webUrl</label>       <a href="http://api.map.baidu.com/marker?location=34.726751,104.365403&title=我的位置&content=百度奎科大厦&output=html">点击跳转到网页的百度地图</a><br><br><br>
      <span>短网址的参数：${paramVal}</span><br><br>
      <span>当前手机的平台:${platform}</span><br><br>
      <span id="positionDemo">test：positionDemo</span>
      <a href="tel://18001557667" data-role="button">拨打电话18001557667</a>
      <img src="http://api.map.baidu.com/staticimage?width=400&height=300&center=苏州&zoom=18&scale=1&markers=客户地址|120.7302576,31.267138499999998|&markerStyles=A|m"
           width="288" height="200">
  </div><!-- /content -->
</div><!-- /page -->


<%--<label>IOS URL</label>      <a href="baidumap://map/marker?location=31.406709,120.604595&coord_type=gcj02&title=邦众汽车修理厂&content=邦众汽车修理厂内容&src=apple|浏览器">点击跳转到百度地图</a><br><br><br>--%>
<%--<label>IOS URL2</label>    <a href="baidumap://map/geocoder?location=31.406709,120.604595&coord_type=gcj02&src=统购|行车一键通">点击跳转到百度地图</a><br><br><br>--%>
<%--<label>安卓URL</label>      <a href="bdapp://map/marker?location=31.295727,120.616793&title=目标地址&content=客户目标地址&src=统购|行车一键通">点击跳转到百度地图</a><br><br><br>--%>
<%--&lt;%&ndash;<label>webUrl</label>       <a href="http://api.map.baidu.com/marker?location=31.295727,120.616793&title=我的位置&content=百度奎科大厦&output=html">点击跳转到网页的百度地图</a><br><br><br>&ndash;%&gt;--%>
<%--<label>webUrl</label>       <a href="http://api.map.baidu.com/marker?location=34.726751,104.365403&title=我的位置&content=百度奎科大厦&output=html">点击跳转到网页的百度地图</a><br><br><br>--%>
<%--<span>短网址的参数：${paramVal}</span><br><br>--%>
<%--<span>当前手机的平台:${platform}</span><br><br>--%>
<%--<span id="positionDemo">test：positionDemo</span>--%>
<%--<input type="button" onclick="getLocation()" value="获取当前坐标">--%>

<%--<div data-role="page" id="page1">--%>
	<%--<div data-theme="a" data-role="header">--%>
		<%--<h3>--%>
			<%--Header--%>
		<%--</h3>--%>
		<%--<img src="https://maps.googleapis.com/maps/api/staticmap?center=Madison, WI&amp;zoom=14&amp;size=288x200&amp;markers=Madison, WI&amp;sensor=false"--%>
		<%--width="288" height="200">--%>
		<%--<div class="ui-grid-a">--%>
			<%--<div class="ui-block-a">--%>
			<%--</div>--%>
			<%--<div class="ui-block-b">--%>
			<%--</div>--%>
			<%--<div class="ui-block-a">--%>
			<%--</div>--%>
			<%--<div class="ui-block-b">--%>
			<%--</div>--%>
			<%--<div class="ui-block-a">--%>
			<%--</div>--%>
			<%--<div class="ui-block-b">--%>
			<%--</div>--%>
		<%--</div>--%>
		<%--<div data-role="navbar" data-iconpos="top">--%>
			<%--<ul>--%>
				<%--<li>--%>
					<%--<a href="#page1" data-transition="fade" data-theme="a" data-icon="" class="ui-btn-active ui-state-persist">--%>
						<%--拨打电话--%>
					<%--</a>--%>
				<%--</li>--%>
			<%--</ul>--%>
		<%--</div>--%>
	<%--</div>--%>
	<%--<div data-role="content">--%>
	<%--</div>--%>
<%--</div>--%>
</body>
</html>