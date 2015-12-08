<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: Sally_Ma
  Date: 14-2-14
  Time: 下午4:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <link type="text/css" rel="stylesheet" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css" />
</head>
<body>
<div class="prompt_box">
    <div class="title">
        <div id="turnOff" class="turn_off"></div>
        发送售后回访信息</div>
    <br />
    <br />
    <div class="content">
        <div class="prompt_textarea">
            尊敬的车主：
            <br />
            您在本店的维修服务已结算，感谢您的光临
            <br />
            <p id="messageContent"></p> </div>
        <div class="fl"><input id="sendSms" name="sendSMS" type="checkbox" value=""/>发送短信</div>
        <div class="fl"><input id="sendApp" name="sendApp" type="checkbox" value="" />发送APP消息</div>
        <div class="fl" style="margin-top:2px;"><a id="helpIcon" href="#"><img src="images/help.png" /></a></div>
        <div id="noRemindDiv">
            <label><input id="noRemind" name="noRemind" type="checkbox"/>不再提示</label>
        </div>
        <div class="clear"></div>
        <div class="wid275">
            <div class="addressList">
                <a id="send" href="#">发 送</a>
                <a id="abandon" href="#">放 弃</a>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</div>
</body>
</html>