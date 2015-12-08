<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-2-6
  Time: 上午2:38
  To change this template use File | Settings | File Templates.
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
	<script type="text/javascript" src="js/components/ui/bcgogo-bcPlayer.js"></script>
	<script type="text/javascript" src="js/components/ui/bcgogo-busSwfLoader.js"></script>
	<script type="text/javascript">
		// 本页面不需要加载 swf
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
						<a class="hover" href="help.do?method=toHelper&title=handwareConfigHelper">硬件设置</a>
						<a href="help.do?method=toHelper&title=softwareConfigHelper">软件设置</a>
					</div>
					<div class="intro">
						<h3>一、确定打印需要的纸张格式大小</h3>

						<div>一般常见的纸张格式有常见的纸张格式有：、A4（21*29.7）、三联纸（21.0*12）、三联纸（21.0*14）、三联纸（22.0*12）、三联纸（22.0*14）、三联纸（22. 0*9）</div>
						<br/>

						<h3>二、确定打印机纸张的进纸方式</h3>

						<div>进纸方式指的是打印机在进行打印工作时通过何种方式来获得所需要的纸张。一般分为<a class="blue_color">手动送纸</a>和<a class="blue_color">牵引送纸</a>。</div>
						<div><a class="blue_color">手动送纸</a>即在打印时，用户手持纸张直接由打印机的进纸口送入。手动送纸每次只能送入一张，效率非常低。如果连续大量的打印会严重的影响到打印机的效率。</div>
						<div><a class="blue_color">牵引送纸</a>包括两个送纸齿轮协调纸张的进出，可以连续大量的打印，解决了手动进纸的不方便、效率低等。牵引送纸易于控制，能有效解决送纸速度跟不上打印速度。</div>
						<br/>

						<h3>三、纸张安装</h3>

						<div>纸张安装根据进纸方式的不同安装也不同。分为<a class="blue_color">手动推送（手动进纸）</a>和<a class="blue_color">牵引送纸（滚动进纸器）</a>。</div>
						<div>1、<a class="blue_color">手动推送（手动进纸）</a>：有两片挡板</div>
						<img src="../web/images/helper/print1.png"/>

						<div class="i_height"></div>
						<div>2、<a class="blue_color">牵引送纸（滚动进纸器）</a>：有齿轮的</div>
						<div>纸张左右两边会有对应的纸孔，只要对应齿轮放置好，扳下押纸板。然后将纸张整理平整按一下打印机上面的 “进纸/换行”按钮。纸张就会自动进入到打印的位置，等待打印。</div>
						<img src="../web/images/helper/print2.png" style=" float:left;"/>

						<div style="height:209px;">
							一般情况针式打印机都会有纸张打印调节杆，作用：调节杆与滚动进纸（牵引齿轮）连接。当调节杆的位置在单页进纸（图标）时，此时我们在打印几页纸张内容的时候，必须一页纸一页纸的进行手动放纸。当调节杆的位置在连续进纸（图标）时，在打印几页纸张内容的时候，只须要将纸张安置在牵引进纸齿轮上或进纸器，即可连续打印多纸张的内容。
						</div>
						<div class="i_height"></div>
						<div>3、调节 单页纸（A4） 和 连续进纸（三联纸或两联红）</div>
						<img src="../web/images/helper/print3.png"/>
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