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
	<script type="text/javascript" src="js/extension/swfobject/swfobject.min.js"></script>
	<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-bcPlayer<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-busSwfLoader<%=ConfigController.getBuildVersion()%>.js"></script>
	<script type="text/javascript">
		$(document).ready(function () {
			var data,
				helpDomain;

			helpDomain = busSwfLoader.getDomain();

			data = {
				"swf1": {
					"uuid": G.generateUUID(),
					"id": "swf1",
					"title": "员工权限1",
					"name": "员工权限1",
					"width": 730,
					"height": 500,
					"swfStr": helpDomain + "/test/help_res/employee_rights_issues_1.swf",
					"xiSwfUrlStr": "assets/swf/playerProductInstall.swf",
					"ts": (new Date()).getTime() + "",
					"contentWidth": 730,
					"contentHeight": 500
				},
				"swf2": {
					"uuid": G.generateUUID(),
					"id": "swf2",
					"title": "员工权限2",
					"name": "员工权限2",
					"width": 730,
					"height": 500,
					"swfStr": helpDomain + "/test/help_res/employee_rights_issues_2.swf",
					"xiSwfUrlStr": "assets/swf/playerProductInstall.swf",
					"ts": (new Date()).getTime() + "",
					"contentWidth": 730,
					"contentHeight": 500
				},
				"swf3": {
					"uuid": G.generateUUID(),
					"id": "swf3",
					"title": "员工权限3",
					"name": "员工权限3",
					"width": 730,
					"height": 500,
					"swfStr": helpDomain + "/test/help_res/employee_rights_issues_3.swf",
					"xiSwfUrlStr": "assets/swf/playerProductInstall.swf",
					"ts": (new Date()).getTime() + "",
					"contentWidth": 730,
					"contentHeight": 500
				},
				"swf4": {
					"uuid": G.generateUUID(),
					"id": "swf4",
					"title": "员工权限4",
					"name": "员工权限4",
					"width": 730,
					"height": 500,
					"swfStr": helpDomain + "/test/help_res/employee_rights_issues_4.swf",
					"xiSwfUrlStr": "assets/swf/playerProductInstall.swf",
					"ts": (new Date()).getTime() + "",
					"contentWidth": 730,
					"contentHeight": 500
				},
				"swf5": {
					"uuid": G.generateUUID(),
					"id": "swf5",
					"title": "员工权限5",
					"name": "员工权限5",
					"width": 730,
					"height": 500,
					"swfStr": helpDomain + "/test/help_res/employee_rights_issues_5.swf",
					"xiSwfUrlStr": "assets/swf/playerProductInstall.swf",
					"ts": (new Date()).getTime() + "",
					"contentWidth": 730,
					"contentHeight": 500
				}
			};

			$("a[id^='btn-step-']").bind("click", function(event){
				var dataItem = data["swf" + this.id.replace("btn-step-", "")];
				busSwfLoader.render(dataItem);
				this.scrollIntoView();
			});

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
			<jsp:param name="currPage" value="staffConfigHelper"/>
		</jsp:include>
		<div class="right">
			<div class="request">
				<a class="btnDis">人员权限配置说明</a>

				<div class="top"></div>
				<div class="body description">
					<h3>一、人员权限配置的常见问题与操作说明</h3>

					<div id="one">1、如果您想新增一个员工并且为其分配一个新权限，<a id="btn-step-1" class="help-btn-step">请点击查看</a></div>
					<div id="swf1"></div>
					<div class="clear i_height"></div>
					<div id="two">2、如果您想新增一个员工并且为其分配一个已经存在的权限，<a id="btn-step-2" class="help-btn-step">请点击查看</a></div>
					<div class="flash" id="swf2">
					</div>
					<div class="clear i_height"></div>
					<div id="three">3、如果您想为已有员工分配一个新的职位权限，<a id="btn-step-3" class="help-btn-step">请点击查看</a></div>
					<div class="flash" id="swf3">
					</div>
					<div class="clear i_height"></div>
					<div id="four">4、如果您想为已有员工分配一个已经存在的职位权限，<a id="btn-step-4" class="help-btn-step">请点击查看</a></div>
					<div class="flash" id="swf4">
					</div>
					<div class="clear i_height"></div>
					<div id="five">5、如果您想新增一个与已有的职位权限相似的职位，<a id="btn-step-5" class="help-btn-step">请点击查看</a></div>
					<div class="flash" id="swf5">
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