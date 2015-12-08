<%--
  User: ndong
  Date: 13-2-6
  Time: 上午2:38
--%>
<%@ page import="com.bcgogo.common.WebUtil" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>帮助公告</title>
	<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="styles/help<%=ConfigController.getBuildVersion()%>.css"/>
	<%@include file="/WEB-INF/views/header_script.jsp" %>
	<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/extension/swfobject/swfobject.min.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-bcPlayer<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-busSwfLoader<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript">
		$(document).ready(function () {
			var helpDomain,
				swfDataList;
			App.Net.syncGet({
				dataType:"json",
				url: "help.do?method=getHelpResourceConfig",
				success: function (data) {
					if (data.error) {
						G.error("help domain 加载失败！");
					} else {
						helpDomain = data.domain;
						swfDataList = [
							{
								"uuid": G.generateUUID(),
								"id": "swf1",
								"title": "打印设置",
								"name": "打印设置",
								"width": 730,
								"height": 500,
								"swfStr": helpDomain + "/test/help_res/printer_setting.swf",
								"xiSwfUrlStr": "assets/swf/playerProductInstall.swf",
								"ts": (new Date()).getTime() + "",
								"contentWidth": 730,
								"contentHeight": 500
							}
						];

						busSwfLoader.renderAll(swfDataList);
					}
				},
				error: function () {
					G.error("help domain 加载失败！");
				}
			});

//      busSwfLoader.run({url:"help.do?method=getVideo&videoId=10001"})
		})
	</script>
</head>

<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
	<div class="mainTitles">
		<div class="titleWords">帮助中心</div>
	</div>
	<div class="i_mainRight" id="i_mainRight">
		<jsp:include page="helperNav.jsp">
			<jsp:param name="currPage" value="printHelper"/>
		</jsp:include>
		<div class="right">
			<div class="request">
				<a class="btnDis">打印机配置说明</a>

				<div class="top"></div>
				<div class="body description">
					<div class="titDes">
						<a class="hover" href="help.do?method=toHelper&title=printHelper">打印驱动安装</a>
						<a href="help.do?method=toHelper&title=handwareConfigHelper">硬件设置</a>
						<a href="help.do?method=toHelper&title=softwareConfigHelper">软件设置</a>
					</div>
					<div class="intro">
						<h3>一、打印驱动介绍</h3>

						<div>常用的打印机驱动二种：1、设备驱动程序 2、状态监视驱动</div>
						<div>状态监视器的功能：状态监视器使您能检查打印机的状态，如果出现故障，状态监视器会显示故障的类别并提供合适的解决方法。</div>
						<br/>

						<h3>二、驱动文件准备</h3>

						<div>打印机自带的驱动光盘，或到该打印机的官方网站上下载对应型号的打印机驱动，有打印状态监视器功能的打印机也应该下载对应型号状态监视器。</div>
						<br/>

						<h3>三、驱动安装</h3>
                        <div id="swf1"></div>

					</div>
				</div>
				<div class="bottom"></div>
			</div>
		</div>
	</div>
</div>
</div>

</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>


</html>