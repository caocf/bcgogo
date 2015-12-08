<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<title>searchBarSample</title>
	<link rel="stylesheet" href="js/components/themes/bcgogo-searchBar.css">
	<link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css">
	<style type="text/css">
		#ui-datepicker-div, .ui-datepicker {
			font-size: 90%;
		}
	</style>

	<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
	<script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
	<script type="text/javascript" src="js/base.js"></script>
	<script type="text/javascript" src="js/application.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-searchBar.js"></script>
	<script type="text/javascript">
		var searchBar = App.Module.searchBar;
		$(document).ready(function(){
			$("#id-test").append( searchBar.getInstance({"orderType":"INVENTORY"}));
		});
	</script>

</head>
<body>

<div id="id-test" style="text-align: center;"></div>






</body>
</html>