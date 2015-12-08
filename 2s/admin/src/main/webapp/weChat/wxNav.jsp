<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 14-10-29
  Time: 上午10:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="rightTitle">
    <div class="rightLeft"></div>
    <div class="rightBody">
        <div class="titleHover" id="sensitiveWordsManager"><a href="weChat.do?method=toAdultList">待审核列表</a></div>
        <div class="titleHover" id="wxAccount"><a href="weChat.do?method=toAccountManager">公共号管理</a></div>
        <div class="titleHover" id="wxShopAccount"><a href="weChat.do?method=toWXShopAccountManager">账户管理</a></div>
        <div class="titleHover" id="wxUser"><a href="weChat.do?method=initWxUser">用户管理</a></div>
        <div class="titleHover" id="failedSms"><a href="weChat.do?method=initWeChatPage">模板查看</a></div>
        <div class="titleHover" id="publicTemplateUpload"><a href="weChat.do?method=toAddWeChat" >模板上传</a></div>
        <div class="titleHover" id="wxOperate"><a href="weChat.do?method=toWXOperator">OPERATOR</a></div>
    </div>
</div>