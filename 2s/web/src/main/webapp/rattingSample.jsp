<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta content="text/html;charset=utf-8" http-equiv="content-type">
	<title>rattingSample</title>
	<link rel="stylesheet" href="js/components/themes/bcgogo-ratting.css">

	<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="js/base.js"></script>
	<script type="text/javascript" src="js/application.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-ratting.js"></script>

	<script type="text/javascript">
		var rattingList = [];
		$(document).ready(function () {

			for (var i = 0; i < 350; i++) {
				var ratting = new App.Module.Ratting();
				/**
				 * {
                 *     selector:"body",
                 *     score:{
                 *         total:10,
                 *         current:5,
                 *     },
                 *     config:{
                 *         isLocked:true,
                 *         isOneOff:true,
                 *         isPrompt:false,
                 *         starType:"small"|"big"
                 *     }
                 *     onRate:function () {}
                 * }
				 */
				ratting.show({
					score:{
						total:10,
						current:0
					},
					config:{
						starType:"big",
						isLocked:false,
						isOneOff:false,
						isPrompt:true,
						tip:{
							contents:[
								{
									scope:[0, 1],
									htmlText:"[0,1]"
								},
								{
									scope:[2, 5],
									htmlText:"[2,5]"
								},
								{
									scope:[6, 8],
									htmlText:"[6,8]"
								},
								{
									scope:[9, 10],
									htmlText:"[9,10]"
								}
							]
						}
					},
					onRate:function (event, data) {
						G.info(data);
					}
				});
				ratting
					.getJqDom()
					.css("display","inline-block")
					.css("margin-right", 20);

				rattingList.push(ratting);
			}

		});
	</script>

</head>
<body>


</body>
</html>