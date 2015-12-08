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
			var data,
				helpDomain;

			helpDomain = busSwfLoader.getDomain();

			data = {
				template_import: {
					"uuid": G.generateUUID(),
					"id": "swf1",
					"title": "模板导入",
					"name": "模板导入",
					"width": 730,
					"height": 500,
					"swfStr": helpDomain + "/test/help_res/template_import.swf",
					"xiSwfUrlStr": "assets/swf/playerProductInstall.swf",
					"ts": (new Date()).getTime() + "",
					"contentWidth": 730,
					"contentHeight": 500
				},
				custom_import: {
					"uuid": G.generateUUID(),
					"id": "swf1",
					"title": "自定义导入",
					"name": "自定义导入",
					"width": 730,
					"height": 500,
					"swfStr": helpDomain + "/test/help_res/custom_import.swf",
					"xiSwfUrlStr": "assets/swf/playerProductInstall.swf",
					"ts": (new Date()).getTime() + "",
					"contentWidth": 730,
					"contentHeight": 500
				}
			};

			var $guideButtons = $("#guideButtons a");
			$guideButtons.bind("click", function (event) {
				var $this = $(this);
				$guideButtons.removeClass("hover");
				$this.addClass("hover");
				busSwfLoader.render(data[$this.attr("status")]);
				this.scrollIntoView();
			});

			(function loadDefaultSwfInInitialing(){
				var $defaultTabButton = $("#guideButtons a.hover");
				busSwfLoader.render(data[$defaultTabButton.attr("status")]);
			})();
           if("selfDefineExcel"==$("#page_param").val()){
              $("#guideButtons a")[1].click();
           }
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
			<jsp:param name="currPage" value="importDataHelper"/>
		</jsp:include>
         <input type="hidden" id="page_param" value="${page_param}" />
		<div class="right">
			<div class="request">
				<a class="btnDis">数据导入功能说明</a>

				<div class="top"></div>
				<div class="body description">
					<h3>一、数据导入范围</h3>

					<div>数据导入的范围有5种：1、单据信息&nbsp;2、库存商品信息&nbsp;3、客户信息&nbsp;4、供应商信息&nbsp;5、会员信息</div>
					<br/>

					<h3>二、导入数据准备</h3>

					<div>1、导入数据需要事先从原有软件中导出，若不会自行导出，请联系原有软件客服帮助导出；</div>
					<div>2、若选择模板导入，则需先下载标准模板后将导出的数据要拷入相应的数据模板中；</div>
					<div>3、若选择自定义导入，则需在文件上传后进行文件内容字段的匹配操作；</div>
					<div>4、文件中的数据需为文本格式的数据；</div>
					<div>5、文件中联系方式若包含多个，请用“/”进行分隔。</div>
					<div>6、只有新店铺才能导入会员，先导客户信息，其中会员基本信息在客户信息中一起导入，再导会员服务
					</div>
					<br/>

					<h3>三、导入操作说明</h3>

					<div class="titDes" id="guideButtons">
						<a class="hover" status="template_import">模板导入说明</a>
						<a status="custom_import">自定义导入说明</a>
					</div>

					<div id="swf1"></div>
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