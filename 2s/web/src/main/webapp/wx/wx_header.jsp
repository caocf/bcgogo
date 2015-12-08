<%--&lt;%&ndash;--%>
  <%--Created by IntelliJ IDEA.--%>
  <%--User: ndong--%>
  <%--Date: 14-9-26--%>
  <%--Time: 上午10:07--%>
  <%--To change this template use File | Settings | File Templates.--%>
<%--&ndash;%&gt;--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<script type="text/javascript">--%>

    <%--$(function(){--%>
        <%--if(onBridgeReady == "undefined"||!$.isFunction(onBridgeReady)) {--%>
            <%--return true;--%>
        <%--}--%>
       <%--//注册微信Bridge--%>
        <%--if (typeof WeixinJSBridge == "undefined"){--%>
            <%--if(document.addEventListener){--%>
                <%--document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);--%>
            <%--}else if (document.attachEvent){--%>
                <%--document.attachEvent('WeixinJSBridgeReady', onBridgeReady);--%>
                <%--document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);--%>
            <%--}--%>
        <%--}else{--%>
            <%--onBridgeReady();--%>
        <%--}--%>
    <%--});--%>

    <%--//隐藏微信中网页右上角按钮--%>
    <%--function hideOptionMenu(){--%>
        <%--WeixinJSBridge.log("hideOptionMenu");--%>
<%--//        alert("hideOptionMenu");--%>
        <%--WeixinJSBridge.call('hideOptionMenu');--%>
    <%--}--%>

    <%--//隐藏微信中网页底部导航栏--%>
    <%--function hideToolbar(){--%>
        <%--WeixinJSBridge.call('hideToolbar');--%>
    <%--}--%>

    <%--//网页获取用户网络状态--%>
    <%--function getNetworkType(){--%>
        <%--WeixinJSBridge.invoke('getNetworkType',{},--%>
                <%--function(e){--%>
                    <%--alert(e.err_msg);--%>
                <%--});--%>
    <%--}--%>

    <%--//关闭当前网页窗口--%>
    <%--function closeWindow(){--%>
        <%--WeixinJSBridge.invoke('closeWindow',{},function(res){--%>
            <%--WeixinJSBridge.log(res.err_msg);--%>
        <%--});--%>
    <%--}--%>

<%--</script>--%>