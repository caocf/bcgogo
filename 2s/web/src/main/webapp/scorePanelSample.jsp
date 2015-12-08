<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta content="text/html;charset=utf-8" http-equiv="content-type" />

	<title>score panel sample</title>

	<link rel="stylesheet" href="js/components/themes/bcgogo-ratting.css">
	<link rel="stylesheet" href="js/components/themes/bcgogo-scorePanel.css">

	<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="js/base.js"></script>
	<script type="text/javascript" src="js/application.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-ratting.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-scorePanel.js"></script>
	<script type="text/javascript" >



		var scorePanel = new App.Module.ScorePanel();
		$(document).ready(function () {

          $("#test").live("mouseover", function(event){
        showDetail();
    }).live("mouseout", function(){
            $(".bcgogo-scorePanel").hide();
        });


		});

    function showDetail(){
         	scorePanel.show({
				selector:"test",
				config:{
					avgLabelWidth:100,
					subLabelWidth:100,
					width:300,
					height:120
				},
				avgScore:{
					value:4.5,
					htmlLabel:"总分：",
					amount:2301
				},
				subScore:[
					{
						value:4.5,
						htmlLabel:"货品质量："
					},
					{
						value:4.5,
						htmlLabel:"货品性价比："
					},
					{
						value:4.5,
						htmlLabel:"发货速度："
					},
					{
						value:4.5,
						htmlLabel:"服务态度："
					}
				]
			});
    }
	</script>

</head>
<body>
    <span id="test"> 123123</span>
</body>
</html>