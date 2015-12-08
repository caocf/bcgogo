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
				payment_authentication: {
					"uuid": G.generateUUID(),
					"id": "swf1",
					"title": "支付认证说明",
					"name": "支付认证说明",
					"width": 730,
					"height": 500,
					"swfStr": helpDomain + "/test/help_res/payment_authentication.swf",
					"xiSwfUrlStr": "assets/swf/playerProductInstall.swf",
					"ts": (new Date()).getTime() + "",
					"contentWidth": 730,
					"contentHeight": 500
				},
				fast_payment: {
					"uuid": G.generateUUID(),
					"id": "swf1",
					"title": "快捷支付说明",
					"name": "快捷支付说明",
					"width": 730,
					"height": 500,
					"swfStr": helpDomain + "/test/help_res/fast_payment.swf",
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
			<jsp:param name="currPage" value="transferHelper"/>
		</jsp:include>
		<div class="right">
			<div class="request">
				<a class="btnDis">银联转账功能说明</a>

				<div class="top"></div>
				<div class="body description">
					<h3>一、进入银联支付的途径</h3>

					<div>进入银联支付途径有两种：1、短信充值&nbsp;&nbsp;2、付款转账</div>
					<div>在短信充值页面或付款转账页面填写金额及相关信息，确认充值后会弹出安全警告提示（见下图）</div>
					<a class="alert"></a><br/>

					<div>点击【继续】进入银联在线支付页面，银联支付有5种支付方式：认证支付、快捷支付、小额支付、储值卡支付、网银支付；</div>
					<div>建议使用&nbsp;<a class="blue_color">认证支付</a>&nbsp;和&nbsp;<a class="blue_color">快捷支付</a>&nbsp;;</div>
					<br/><br/>

					<h3>二、支付操作说明</h3>

					<div id="guideButtons" class="titDes">
						<a class="hover" status="payment_authentication">认证支付说明</a>
						<a status="fast_payment" >快捷支付说明</a>
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