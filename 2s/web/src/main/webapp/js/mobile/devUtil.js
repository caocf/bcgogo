/**
 * @description 分析移动端设备信息
 * @author ndong
 * @date  2014-11-18
 */

;
(function(){

    var  DevUtil=function(){};

    var u = navigator.userAgent,
        app= navigator.appVersion;
    DevUtil.prototype = {
        //苹果、谷歌内核
        webKit:function(){
            return u.indexOf('AppleWebKit') > -1;
        },
        //IE内核
        trident:function(){
            return u.indexOf('Trident') > -1;
        },
        //opera内核
        presto:function(){
            return u.indexOf('Presto') > -1;
        },
        //火狐内核
        gecko:function(){
            return u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1;
        },
        //是否为移动终端
        mobile:function(){
            return !!u.match(/AppleWebKit.*Mobile.*/);
        },
        //ios终端
        ios:function(){
            return !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
        },
        //android终端或uc浏览器
        android:function(){
            return u.indexOf('Android') > -1 || u.indexOf('Linux') > -1;
        },
        //是否为iPhone或者QQHD浏览器
        iPhone:function(){
            return u.indexOf('iPhone') > -1;
        },
        //是否iPad
        iPad:function(){
            return u.indexOf('iPad') > -1;
        },
        //是否web应该程序，没有头部与底部
        webApp:function(){
            return u.indexOf('Safari') > -1;
        },
        language:(navigator.browserLanguage || navigator.language).toLowerCase()
    };
    APP_BCGOGO.Module.DevUtil=DevUtil;
})();
