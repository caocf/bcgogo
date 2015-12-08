<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<title>Scanning Sample</title>
	<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css" />

	<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript">
		var keydownCallback = function (source, nameStr, interval) {
			GLOBAL.info(source + "\t" + nameStr + "\t" + interval);
		};

		$(document).ready(function(){
			App.Module.scanning.sourceDetect(keydownCallback);
		});
	</script>

</head>
<body>

</body>
</html>