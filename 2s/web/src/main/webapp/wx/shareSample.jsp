<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 2015-1-6
  Time: 11:44
  To change this template use File | Settings | File Templates.
--%>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%--<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>--%>
<%--<%@ page import="com.bcgogo.config.ConfigController" %>--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">--%>
<%--<html xmlns="http://www.w3.org/1999/xhtml">--%>
<%--<head>--%>
    <%--<title>sample</title>--%>
    <%--<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>--%>
    <%--<meta name="viewport"--%>
          <%--content="width=device-width, initial-scale=1, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>--%>
    <%--<%--%>
        <%--response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1--%>
        <%--response.setHeader("Pragma", "no-cache"); //HTTP 1.0--%>
        <%--response.setDateHeader("Expires", 0); //prevents caching at the proxy server--%>
    <%--%>--%>

    <%--<link rel="stylesheet" type="text/css" href="/web/styles/wechat<%=ConfigController.getBuildVersion()%>.css">--%>
    <%--<link rel="stylesheet" type="text/css"--%>
          <%--href="/web/styles/mobile/wx-appoint<%=ConfigController.getBuildVersion()%>.css">--%>
    <%--<script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>--%>
    <%--<script type="text/javascript" src="/web/js/mobile/mBase<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript" src="/web/js/mobile/weChat<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <%--<script type="text/javascript">--%>
        <%--$(function () {--%>
            <%--var shareData = {--%>
                <%--img_url: "",--%>
                <%--img_width: 200,--%>
                <%--img_height: 200,--%>
                <%--link: '',--%>
                <%--desc: '',--%>
                <%--title: '',--%>
                <%--appid: 0--%>
            <%--};--%>

<%--//            document.addEventListener('WeixinJSBridgeReady', function onBridgeReady() {--%>
<%--//                $("#test").append("menu:share:appmessage start.");--%>
<%--//                // 发送给好友--%>
<%--//                WeixinJSBridge.on('menu:share:appmessage', function (argv) {--%>
<%--//                    $("#test").append("menu:share:appmessage.");--%>
<%--//                    shareFriend();--%>
<%--//                });--%>
<%--//                // 分享到朋友圈--%>
<%--//                WeixinJSBridge.on('menu:share:timeline', function (argv) {--%>
<%--//                    $("#test").append("menu:share:timeline.");--%>
<%--//                    shareTimeline();--%>
<%--//                });--%>
<%--//            }, false);--%>


            <%--function shareTimeline() {--%>
                <%--WeixinJSBridge.invoke('shareTimeline', shareData, function (res) {--%>
                    <%--validateShare(res);--%>
                    <%--//_report('timeline', res.err_msg);--%>
                <%--});--%>
            <%--}--%>

            <%--function shareFriend() {--%>
                <%--WeixinJSBridge.invoke('sendAppMessage', shareData, function (res) {--%>
                    <%--alert("sendAppMessage");--%>
                    <%--validateShare(res);--%>
                    <%--//_report('send_msg', res.err_msg);--%>
                <%--});--%>
            <%--}--%>

            <%--function validateShare(res) {--%>
                <%--$("#test").append("validateShare:" + res.err_msg);--%>
                <%--if (res.err_msg != 'send_app_msg:cancel' && res.err_msg != 'share_timeline:cancel') {--%>
                    <%--//分享完毕回调--%>
                <%--}--%>
            <%--}--%>
        <%--});--%>


        <%--$(function(){--%>
            <%--$("#add").click(function(){--%>
                <%--getCardDetailCallback();--%>
            <%--});--%>

            <%--function getCardDetailCallback(ajaxParams){--%>

                            <%--var qrcode = "q13747338938406";--%>
                            <%--var redirectLink = null;--%>
                            <%--// 防止ozSource 为undefined--%>
                            <%--var ozSource = "";--%>
                <%--var wxid="chuanxiang_meishi";--%>
<%--//                            var apiType=1;--%>
<%--//                            if (apiType == 1) {--%>
<%--//                                WeixinJSBridge.invoke('profile',{--%>
<%--//                                                                'username':wxid,--%>
<%--//                                                                'scene':'57'--%>
<%--//                                                    },function(res){--%>
<%--//                                    alert("wxid is:"+wxid+",and err:"+res.err_msg);--%>
<%--//                                });--%>
<%--//--%>
<%--//                                return false;--%>
<%--//                            }--%>
                            <%--WeixinJSBridge.invoke('addContact',{--%>
                                <%--"webtype" : "0", // 添加联系人的场景，1表示企业联系人。--%>
                                <%--"username" : wxid,　// 需要添加的联系人username--%>
                            <%--},function(res){--%>
                                    <%--alert("wxid is:"+wxid+",and err:"+res.err_msg);--%>
                                <%--if (res.err_msg == 'add_contact:ok') {--%>
                                    <%--_showLoading();--%>
                                    <%--window.setTimeout(function(){--%>
                                        <%--var joinMemberRequest = reqObject(--%>
                                            <%--'/wsh/joinMember?qrcode=' + qrcode + '&ticket=' + ticket,--%>
                                            <%--'POST',--%>
                                            <%--{--%>
                                                <%--clientType: 'webapp',--%>
                                                <%--qrcode:qrcode,--%>
                                                <%--ozSource:ozSource,--%>
                                                <%--wticket:wticket,--%>
                                                <%--ticketSource: ticketSource,--%>
                                                <%--ticket:ticket--%>
                                            <%--}--%>
                                        <%--);--%>
                                        <%--APP.facade.utils.ajax(--%>
                                            <%--joinMemberRequest,--%>
                                            <%--function mycallback(data){--%>
                                                <%--// 加入成功--%>
                                                <%--if (data.code == 0) {--%>
                                                    <%--if(redirectLink) {--%>
                                                        <%--window.location.href=redirectLink;--%>
                                                    <%--}--%>
                                                    <%--_hideLoading();--%>
                                                    <%--_addClass(_q('.card '), 'anim');--%>
                                                    <%--_setTimeout(function(){--%>
                                                        <%--var hObj = {--%>
                                                            <%--act: 3,--%>
                                                            <%--ticket:ticket,--%>
                                                            <%--wticket:wticket,--%>
                                                            <%--ticketSource: ticketSource,--%>
                                                            <%--qrcode:qrcode,--%>
                                                            <%--ozSource:ozSource,--%>
                                                            <%--wechat_webview_type:1--%>
                                                        <%--}--%>
                                                        <%--APP.facade.utils.updateHash(hObj);--%>
                                                    <%--},1500);--%>
                                                <%--}--%>
                                                <%--_hideLoading();--%>
                                            <%--}--%>
                                        <%--);--%>
                                    <%--}, 2012);--%>
                                <%--}--%>
                            <%--});--%>
            			<%--}--%>
        <%--});--%>

        <%--function reqObject (url, method,params, callback, overwrite) {--%>
            <%--this.url = url;--%>
            <%--this.method = method;--%>
            <%--this.params = params;--%>
            <%--this.callback = callback;--%>
            <%--this.set = function (k, v) {--%>
                <%--if (!overwrite || (overwrite && !this.params.hasOwnProperty(k)))--%>
                    <%--this.params[k] = v;--%>
            <%--};--%>
        <%--}--%>

        <%--function _showLoading(){--%>
            <%--var div = document.getElementById('loading');--%>
            <%--if (!div){--%>
                <%--div = document.createElement('div');--%>
            <%--}--%>
            <%--div.style.display = 'none';--%>
            <%--div.id = 'loading';--%>
            <%--div.innerHTML = '<div class="lbk"></div><div class="lcont"><img src="'+loading_asset+'" alt="loading..."/>正在加载...</div>';--%>
            <%--document.querySelector('body').appendChild(div);--%>
        <%--} ;--%>


    <%--</script>--%>

<%--</head>--%>
<%--<body>--%>
<%--this is a test page!--%>
<%--<div id="test">--%>
    <%--<input type="button" value="加关注" id="add" />--%>
<%--</div>--%>
<%--</body>--%>
<%--</html>--%>






<!doctype html>
<html>
<head>
    <title>Multipage example</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css"/>
    <script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>
</head>
<body>
<div class="ui-page" data-role="page" id="page1" data-title="page1" data-next="page2">
    <div data-role="header">
        <h1>Page 1</h1>
    </div>
    <div role="main" class="ui-content">
        <a href="#page2" data-transition="slide" class="ui-btn ui-corner-all ui-btn-inline">Go To Page 2</a>
    </div>
</div>
<div class="ui-page" data-role="page" id="page2" data-title="page2" data-prev="page1">
    <div data-role="header">
        <h1>Page 2</h1>
    </div>
    <div role="main" class="ui-content">
        <a href="#page1" data-rel="back" data-transition="slide" class="ui-btn ui-corner-all ui-btn-inline">
            Go Back To
            Page 1</a>
    </div>
</div>
<script>
    $(document).on("swipeleft", ".ui-page", function () {
        var thePage = $(this),
                next = thePage.jqmData("next");
        if (next) {
            $(":mobile-pagecontainer").pagecontainer("change", "#" + next, {transition: "slide"});
        }
    });
    $(document).on("swiperight", ".ui-page", function () {
        var thePage = $(this),
                prev = thePage.jqmData("prev");
        if (prev) {
            $(":mobile-pagecontainer").pagecontainer("change", "#" + prev, {transition: "slide", reverse: true});
        }
    });
</script>
</body>
</html>