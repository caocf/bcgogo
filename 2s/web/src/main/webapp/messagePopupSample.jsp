<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
	<title></title>
	<meta content="text/html;charset=utf-8" http-equiv="content-type" />
	<link rel="stylesheet" href="js/components/themes/bcgogo-messagePopup.css" />

	<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-messagePopup<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			var messagePopup = App.Module.messagePopup;
			messagePopup
                .init({ "url": "./dummyData/dataHasMessage.json", "selector": $("#message") })
                .inquire();
		});
	</script>

</head>
<body>

<div id="message" style="display: inline-block;position:absolute;left:200px; color:#FF6417;">消息</div>

</body>
</html>