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
				dataType: "json",
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
								"title": "页面设置",
								"name": "页面设置",
								"width": 730,
								"height": 500,
								"swfStr": helpDomain + "/test/help_res/paper_setting.swf",
								"xiSwfUrlStr": "assets/swf/playerProductInstall.swf",
								"ts": (new Date()).getTime() + "",
								"contentWidth": 730,
								"contentHeight": 500
							},
							{
								"uuid": G.generateUUID(),
								"id": "swf2",
								"title": "浏览器设置",
								"name": "浏览器设置",
								"width": 730,
								"height": 500,
								"swfStr": helpDomain + "/test/help_res/browser_setting.swf",
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
		});
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
						<a href="help.do?method=toHelper&title=printHelper">打印驱动安装</a>
						<a href="help.do?method=toHelper&title=handwareConfigHelper">硬件设置</a>
						<a class="hover" href="help.do?method=toHelper&title=softwareConfigHelper">软件设置</a>
					</div>
					<div class="intro">
						<h3>一、打印系统设置</h3>

						<div id="swf1"></div>
						<h3>二、火狐浏览器设置方法</h3>
						<a class="blue_color">（必须在打印机设置好所有格式后再安装，否则需要卸载浏览器重新安装！）</a>

						<div> 1、在浏览器菜单栏点击【文件】-->【页面设置】，弹出页面设置页面；</div>
						<img src="../web/images/helper/soft1.png"/>

						<div>2、格式和选项设置：将格式方向为纵向，比例设置为100%并取消缩放功能；</div>
						<div>3、页边距和页眉/页脚设置：页边距全部设置为0，页眉和页脚都为空白；</div>
						<img src="../web/images/helper/soft2.png" style="float:left; padding-right:30px;"/>
						<img src="../web/images/helper/soft3.png"/>

						<div>4、演示</div>
						<div id="swf2"></div>

						<div class="height"></div>
						<h3>三、IE8浏览器设置方法</h3>

						<div>1、在浏览器菜单栏点击【文件】-->【页面设置】，弹出页面设置页面；</div>
						<img src="../web/images/helper/soft4.png"/>
						<img src="../web/images/helper/soft5.png" style="float:right; margin-top:200px;"/>

						<div>2、页面设置-->选择纸张格式；</div>
						<div>3、页面设置-->不启用缩小字体填充；</div>
						<div>4、页面设置-->将页眉和页脚下项目都选择为空；</div>
						<div style="height:200px;">5、页面设置-->页边距都设置为0；</div>


						<h3>四、打印测试</h3>

						<div>打印测试用于检查打印效果是否正确，如果格式不对请创建对应的纸张的格式或适当改小现有纸张格式设定。</div>
						<div>例： 240*120 可修改为220*120\215*120,如使用的是牵引进纸，纸张在出纸时不方便切纸，可将纸张格式的高度适当调大一些。例如：240*120 调整回240*140等，但需要注意的是下次打印需按二次“进纸\退纸” 按钮，以便纸张回到正确打印位置。</div>
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