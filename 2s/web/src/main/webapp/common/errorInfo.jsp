<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 15-8-24
  Time: 下午9:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>page exception</title>
<style>
*{
	padding:0;
	margin:0;}
ul,li{
	margin:0;
	padding:0;
	list-style:none;}
body {
	font: .85em/1 "微软雅黑", "Microsoft Yahei", 'Arial', 'san-serif';
	color: #666;
	width: 100%;
	line-height:1.5em;
	height: 100%;
	overflow-x: hidden;
	overflow-y: auto;
	-webkit-overflow-scrolling: touch;
	background:#ececec;
}
h1 {
	font-size: 1.5em;
	text-align: center;
}
.error{
	padding:5% 0;
	margin:5%;}
.trajectory {
	padding: 3%;
}
.trajectory li{
	margin-left:1em;}
.error_png{
	width:200px;
	margin:0 auto;}
.error a{
	color:#236bbb;}
.error b{
	color:#333;
	margin-bottom:.5em;}
</style>
</head>

<body>
<div id="wrapper">
    <section class="content">
        <div class="trajectory">
            <div class="error_png"><img src="404.png" width="200" /></div>
            <h1>页面出错啦！</h1>
            <div class="error">
                <b>可能原因：</b>
                <ul>
                    <li>该页面已删除或移动；</li>
                    <li>该页面暂时不可用；</li>
                </ul>
            </div>
            <div class="error">
                <b>您可以尝试：</b>
                <ul>
                    <li><a href="#">${result.prompt}；</a></li>
                    <li><a href="#">返回上一级页面；</a></li>
                    <li><a href="#">反馈本次错误；</a></li>
                </ul>
            </div>
            <div class="error">
                <b>详细信息：</b>
                <ul>
                    <li>${result.errorMsg}</li>
                </ul>
            </div>
        </div>
    </section>
</div>
</body>
</html>
