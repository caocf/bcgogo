<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<meta content="text/html;charset=utf-8" http-equiv="content-type" />
	<title>sample load swf</title>

	<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="js/extension/swfobject/swfobject.min.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-bcPlayer.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-busSwfLoader.js"></script>

	<script type="text/javascript">
		$(document).ready(function(){
			busSwfLoader.run({url:"dummyData/dummySwfData.json"})
		})
	</script>
</head>
<body>
    <div id="swf1"></div>
</body>
</html>