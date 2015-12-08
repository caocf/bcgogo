<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理系统——敏感词上传</title>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>

    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp" %>

    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp" %>
    <script type="text/javascript" src="js/uploadSensitiveWord.js"></script>
</head>
<body>
<div class="main">
    <!--头部-->
     <%@include file="/WEB-INF/views/header.jsp" %>
    <!--头部结束-->
    <div class="body">
        <!--左侧列表-->
        <%@include file="/WEB-INF/views/left.jsp" %>
        <!--左侧列表结束-->
        <!--右侧内容-->
        <div class="bodyRight">
            <!--搜索-->
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店铺名" id="txt_shopName"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店主" id="txt_shopOwner"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textAddressbody"><input type="text" value="地址" id="txt_address"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="手机/电话" id="txt_phone"/></div>
                <div class="textRight"></div>
            </div>
            <input type="button" class="rightSearch" value="搜 索"/>
            <!--搜索结束-->
            <!--内容-->
            <div class="rightMain clear">
                <div class="rightTitle">
                    <div class="rightLeft"></div>
                    <div class="rightBody">
                        <div class="titleHover" id="failedSms"><a href="sms.do?method=initFailedSmsPage">短信发送失败管理</a>
                    </div>
                        <div class="titleHover" id="sensitiveWordsUpload"><a
                            href="sms.do?method=initUploadSensitiveWordsPage">敏感词上传</a></div>
                        <div class="titleHover" id="sensitiveWordsManager"><a
                            href="sms.do?method=initSensitiveWordPage">敏感词管理</a></div>
                        <div class="titleHover" id="toTestStopWords"><a href="sms.do?method=toTestStopWords">联逾短信</a></div>
                </div>
                </div>
                <div class="fileInfo">
                    <h3 style="color: red">注意：windows 请先把文本转化成 ANSI</h3>
                    <h3 style="color: red">注意：Linux 请先把文本转化成 Utf-8</h3>
                    <br>

                    <form action="sms.do?method=uploadSensitiveWords" method="post" name="uploadSensitiveWords"
                          enctype="multipart/form-data" id="uploadSensitiveWords">
                        <label>短信敏感词库</label> <input id="sensitiveWords" type="file" name="sensitiveWords"
                                                     class="required"/>
                        <input type="button" value="提交"  id="submitForm" name="submitForm" onclick="thisFormSubmit()"/>
                    </form>
                </div>
                <!--内容结束-->
            </div>
            <!--内容结束-->
            <!--圆角-->
            <div class="bottom_crile clear">
                <div class="crile"></div>
                <div class="bottom_x"></div>
                <div style="clear:both;"></div>
            </div>
            <!--圆角结束-->
        </div>
        <!--右侧内容结束-->
    </div>
</div>
<ul class="suggestionMain" style="list-style: none;color:#000000;">
</ul>
<div id="mask" style="display:block;position: absolute;">
</div>
<iframe name="iframe_PopupBox" id="iframe_PopupBox"
        style="position:absolute;z-index:5; left:400px; top:200px; display:none;" allowtransparency="true"
        width="1000px" height="1500px" frameborder="0" src=""></iframe>
</body>
</html>