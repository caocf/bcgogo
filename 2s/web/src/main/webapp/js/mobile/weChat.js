/**
 * @description 微信浏览器 特定方法
 * @author ndong
 * @date  2014-10-23
 */

//       if (typeof WeixinJSBridge == "undefined"){
//                   if( document.addEventListener ){
//                       document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
//                   }else if (document.attachEvent){
//                       document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
//                       document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
//                   }
//               }

;
(function(){


    var  WeChat=function(){};

    WeChat.prototype={
        //隐藏微信中网页右上角按钮
        hideOptionMenu:function (){
            WeixinJSBridge.log("hideOptionMenu");
            WeixinJSBridge.call('hideOptionMenu');
        },
        //隐藏微信中网页底部导航栏
        hideToolbar:function (){
            WeixinJSBridge.call('hideToolbar');
        },
        //网页获取用户网络状态
        getNetworkType : function (){
            WeixinJSBridge.invoke('getNetworkType',{},
                function(e){
                    alert(e.err_msg);
                })
        },
        //关闭当前网页窗口
        closeWindow:function (){
            WeixinJSBridge.invoke('closeWindow',{},function(res){
                WeixinJSBridge.log(res.err_msg);
            });
        }
    };

    APP_BCGOGO.Module.WeChat=WeChat;


})();

