<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 14-10-20
  Time: 上午10:40
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>车辆绑定</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="styles/wechat.css">
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="/web/js/mobile/weChat<%=ConfigController.getBuildVersion()%>.js"></script>
    <script language="javascript" type="text/javascript">

//        var addWxContact = function(wxid, cb){
//            if (typeof WeixinJSBridge == 'undefined') return false;
//            WeixinJSBridge.invoke('addContact', {
//                webtype: '1',
//                username: 'gh_e1f30832359c'
//            }, function(d) {
//                // 返回d.err_msg取值，d还有一个属性是err_desc
//                // add_contact:cancel 用户取消
//                // add_contact:fail　关注失败
//                // add_contact:ok 关注成功
//                // add_contact:added 已经关注
//                WeixinJSBridge.log(d.err_msg);
//                cb && cb(d.err_msg);
//            });
//        };



        //        function onBridgeReady(){
        //            var weChat=new APP_BCGOGO.Module.WeChat();
        //            weChat.hideOptionMenu();
        //        }


        $(function(){

            function onBridgeReady(){
                WeixinJSBridge.invoke('getNetworkType',{},
 		function(e){
 	    	WeixinJSBridge.log(e.err_msg);
             alert("wx:"+e.err_msg)
 	    });
            }

            if (typeof WeixinJSBridge == "undefined"){
                if( document.addEventListener ){
                    document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
                }else if (document.attachEvent){
                    document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                    document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
                }
            }else{
                onBridgeReady();
            }
            onBridgeReady();
//            $("#vBindBtn").click(function(){
//                onBridgeReady();
//            });

        });

    </script>
</head>
<body>
<div id="expressbox">
    <div style="margin-top: 100px"></div>
    <div class="mtop5">
        <input name="query"  value="关闭" class="btn" type="button" onclick="onBridgeReady()">
        <input name="query" id="vBindBtn" value="绑定" class="btn" type="button" onclick="onBridgeReady()">
    </div>
    <div class="mtop5">
        <input type="button" value="关注m" onclick="addWxContact()">
    </div>
</div>
</body>
</html>