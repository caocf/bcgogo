<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<meta content="text/html;charset=utf-8" http-equiv="content-type">
	<title>sample bc player</title>

	<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="js/extension/swfobject/swfobject.min.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-bcPlayer.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-busPlayer.js"></script>

	<script type="text/javascript">
		$(document).ready(function(){
			busPlayer.run({url:"dummyData/dummyPlayerData.json"});
		});
	</script>
</head>
<body>

	<div id="testVideo"></div>
</body>
</html>