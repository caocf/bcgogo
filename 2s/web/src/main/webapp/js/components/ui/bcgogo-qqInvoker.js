///**
// * qq invoker, God bless you
// * @author zhen.pan
// */
//;
//(function ($) {
//    var id = {
//            "qq": "id-qqInvoker"
//        },
//        c = {
//            "qqIconButton": "qqIconButton",
//            "qqIconInfo": "qqIconInfo"
//        },
//        css = {
//            //121 277
//            "invoker": {
//                "width": 121,
//                "height": 277,
//                "position": "fixed",
//                "top": 164,
//                "left": 8,
//                "color": "#000"
//            },
//            "close": {
//                "width": 39,
//                "height": 22,
//                "position": "absolute",
//                "display": "block",
//                "top": 0,
//                "left": 76,
//                "font-family": "monospace",
//                "text-align": "center",
//                "color": "#fff",
////                    "background-color":"#C5CACF",  //"#FF00B7"
//                "background": "transparent",
//                "line-height": "23px",
//                "cursor": "pointer"
//            },
//            "icon": {
//                "width": 121,
//                "height": 277,
//                "display": "block",
//                "float": "left",
//                "background": "url(js/components/themes/res/qq/qq_img.png)",
//                "font-family": "'Microsoft YaHei','Simsun'"
//            },
//            "qqIconButton": {
//                "width": 86,
//                "height": 22,
//                "margin": "250px 0 0 17px",
//                "font-family": "'Microsoft YaHei','Simsun'",
//                "text-align": "center",
//                "font-size": "12px",
//                "line-height": "22px",
//                "cursor": "pointer"
//            },
//            "qqIconInfo": {
//                "width": "100%",
//                "position": "absolute",
//                "top": 184,
//                "font-family": "'Microsoft YaHei', 'Simsun'",
//                "text-align": "center",
//                "font-size": "12px",
//                "line-height": "20px",
//                "color": "#095996"
//            }
//        },
//        sel = {
//            "qq": "#" + id.qq,
//            "qqIconButton": "." + c.qqIconButton,
//            "qqIconInfo": "." + c.qqIconInfo
//        };
//
//
//    var QQInvoker = (function () {
//        // idea IDE
//        var QQInvoker = function (status) {
//            this._$;
//            this._$qqInvoker = $("<div></div>");
//            this._$close = $("<div></div>");
//            this._$qqIcon = $("<div><div class='qqIconButton'></div><div class='qqIconInfo'></div></div>");
//            this._$clear = $("<div style='clear:both;display:none;'></div>");
//        };
//
//        QQInvoker.method("init", function (p) {
//            var that = this;
//
//            $(document.body).append(this._$qqInvoker);
//
//            this._$qqInvoker
//                .css(css.invoker)
//                .append(this._$qqIcon, this._$close, this._$clear);
//
//            // Ga Le Ge Bi De, fuck modify modify modify requirement in one day! please make a balance and make the decision of requirements!
//            // 大江东去浪淘尽, 孤影垂老碧空矗
//            this._$qqIcon
//                .attr("id", id.qq)
//                .css(css.icon)
//                .find(sel.qqIconButton)
//                .text("QQ交谈")
//                .attr("jshref", "http://b.qq.com/webc.htm?new=0&sid=800060787&eid=2188z8p8p8p8x8p8y8z8y&o=&q=7&from=qqwpa")
//                .css(css.qqIconButton)
//                .bind("click", function (event) {
//                    window.top.open($(this).attr("jshref"));
//                    return false;
//                })
//                .parent().find(sel.qqIconInfo)
//                .css(css.qqIconInfo)
//                .html("客服电话:<br><span style='padding: 0;margin: 0;font-family: monospace'>0512-66733331</span>");
//            // unescape("%u2573")
//
//            this._$close
//                .css(css.close)
//                .bind("click", function () {
//                    that._$qqInvoker.hide();
//                });
//
//            this._$qqInvoker.hide();
//            return this;
//        });
//
//        QQInvoker.method("show", function () {
//            this._$qqInvoker.show();
//            return this;
//        });
//
//        QQInvoker.method("hide", function () {
//            this._$qqInvoker.show();
//            return this;
//        });
//
//        return QQInvoker;
//    }());
//
//
//    // window loaded to load qq Invoker
//    $(window).bind("load", function () {
//        if (window !== window.top) return;
//        if ($(sel.qq)[0]) return;
//
//        var qqInvoker = new QQInvoker();
//
//        // suspend this , TODO requirement changed .....
//        // qqInvoker.init().show();
//    });
//
//}(jQuery));

/**
 * qq Invoker Static, God bless you ~~
 *
 * use method init() to extends functionality
 *
 * @author zhen.pan
 */
;
(function ($) {
    App.namespace("Module.QQInvokerStatic");

    var defaults = {
        url:"http://b.qq.com/webc.htm?new=0&sid=800060787&eid=2188z8p8p8p8x8p8y8z8y&o=&q=7&from=qqwpa"
    };

    var QQInvokerStatic = (function () {
        var clickHandler = function(event) {
            window.top.open($(event.currentTarget).attr("jshref"));
            return false;
        };

        var QQInvokerStatic = function () {
            this._$ = undefined;
        };

        QQInvokerStatic.method("init", function(sel, param) {
            var _$ = $(sel),
                jshref = undefined;
            this._$ = _$;

            if(!_$[0]) return this;

            if(param) {
                jshref = param.url;
            }

            _$
                .attr("jshref", jshref || defaults.url)
                .css("cursor", "pointer")
                .bind("click", clickHandler);
            return this;
        });

        QQInvokerStatic.method("dispose", function() {
            if(!this._$) return this;

            this._$
                .css("cursor", "auto")
                .removeAttr("jshref")
                .unbind("click", clickHandler);
        });

        return QQInvokerStatic;
    }());

    App.Module.QQInvokerStatic = QQInvokerStatic;
}(jQuery));

/**
 * 基于jquery的多个qq优先选择在线的qq服务的作为首要交谈的组件,如果一个都没有则采用第一个
 * @author SinYu.Qiu
 * QQ的格式可以是数组，只有一个qq号的时候可以是String；
 * 使用方法：1，$("#qqTalk").multiQQInvoker({QQ:"12345"});  //使用系统默认qq在线图标
 * 使用方法：2，$("#qqTalk").multiQQInvoker({QQ:["12345","376022486"]});   //使用系统默认qq在线图标
 * 使用方法：3，$("#qqTalk").multiQQInvoker({QQ:["12345","376022486"],QQIcoStyle:52});  //使用腾讯提供的ui
 * 使用方法：4，$("#qqTalk").multiQQInvoker({    //使用自定义的ui
         QQ: $.fn.multiQQInvoker.getContactQQ(),
         appQQStyle: "test",
         qqStyleClass: {
             test: {
                 1: {
                     css: {},
                     url: "images/qqSmallOnline.gif"
                 },
                 0: {
                     css: {display:"none"},
                     url: "images/icon_qq_offline1.gif"
                 }
             }
         }
     });
 *
 *
 * Version:1.0 2013-08-13
 * Version:1.1 2013-09-27 增加了callBack方法，
 * 使用方法：4 multiQQInvoker({QQ:["12345","376022486"],QQIcoStyle:52，callBack:function(){alert("qq图标渲染后执行的回调函数")}});  //使用腾讯提供的ui
 */

var online = new Array();  //下面方法中要用到这个全局变量
(function ($) {
    // 插件的定义
    //依赖于jquery，base.js,application.js
    $.fn.multiQQInvoker = function (options) {
        // build main options before element iteration
        var qqArray = new Array();
        if (!G.Lang.isEmpty(options.QQ) && typeof options.QQ == "string" && !$.isArray(options.QQ)) {
            var tempQQArray = options.QQ.split(",");
            for (var i = 0, len = tempQQArray.length; i < len; i++) {
                if (!G.Lang.isEmpty(tempQQArray[i])) {
                    qqArray.push(tempQQArray[i]);
                }
            }
        } else if ($.isArray(options.QQ) && !G.Lang.isEmpty(options.QQ)) {
            for (var i = 0, len = options.QQ.length; i < len; i++) {
                if (!G.Lang.isEmpty(options.QQ[i])) {
                    qqArray.push(options.QQ[i]);
                }
            }
        }
        options["QQ"] = qqArray;
        $.fn.multiQQInvoker.defaults = $.extend({}, $.fn.multiQQInvoker.defaults, options);
        var opts = $.fn.multiQQInvoker.defaults;
          //返回的是一个qq在线状态的对象
        var initQQTalk = function (qqArray, $QQDom) {

            var qqStr = "";
            var opts = $.fn.multiQQInvoker.defaults;
            if (G.Lang.isEmpty(qqArray) || !$.isArray(qqArray)) {
                if($QQDom){
                    $($QQDom).html("");
                }
                return;
            }
            var isAllCaches = isQQArrayIsAllCached(qqArray);
//            isAllCaches=true;   //todo for test
            if (isAllCaches) {
                drawQQ(qqArray, $QQDom,opts["callBack"]);
            } else {
                $.each(qqArray, function (index, qq) {
                    qqStr += qq + ":";
                });

                var url = opts.qqOnlineCheckUrl.replace("{qqStr}", qqStr);
                url += "&r=" + Math.random();
                try {
                    var script = document.createElement("script");
                    script.type = "text/javascript";
                    script.src = url;
                    document.getElementsByTagName('head')[0].appendChild(script);
                } catch (e) {
                    console.warn(e);
                }
                script.onload = function () {
                    var storage = $.fn.multiQQInvoker.storage.onlineStatus;
                    $.each(qqArray, function (index, qq) {
                        var status = online[index] || 0;
                        storage[qq] = status;
                    });
                    drawQQ(qqArray, $QQDom,opts["callBack"]);
                }
            }
        };
        // iterate and reformat each matched element
        if (this.length > 0) {
            return this.each(function () {
                initQQTalk(opts["QQ"], this);
            });
        }

    };

    function isQQArrayIsAllCached(qqArray){
        var isAllHaveCached = true;
        if (!qqArray || !$.isArray(qqArray)) {
            return false;
        }
        $.each(qqArray, function (index, qq) {
            if (G.Lang.isEmpty($.fn.multiQQInvoker.storage.onlineStatus)|| G.Lang.isEmpty($.fn.multiQQInvoker.storage.onlineStatus[qq])) {
                isAllHaveCached = false;
                return false;
            }
        });
        return isAllHaveCached;
    }

    function drawQQ(qqArray,$QQDom,callBack) {
        var storage = $.fn.multiQQInvoker.storage.onlineStatus;
        var opts = $.fn.multiQQInvoker.defaults;
        var sortedArray = new Array();
        $.each(qqArray, function (index, qq) {
            if (storage && storage[qq] && storage[qq] == 1) {
                sortedArray.push(qq);
            }
        });
        $.each(qqArray, function (index, qq) {
            if (storage && storage[qq] == 0) {
                sortedArray.push(qq);
            }
        });
        if (sortedArray.length == 0) {
            sortedArray = qqArray;
        }
        var targetQQ = sortedArray[0];
        var qqTalkUrl = opts.qqTalkUrl;
        qqTalkUrl = qqTalkUrl.replace("{qq}", targetQQ);
        var qqImageUrl = opts.qqImageUrl;
        var qqStyle = opts["QQIcoStyle"];

        var bcgogoQQStyle = opts["appQQStyle"];
        qqImageUrl = qqImageUrl.replace("{qq}", targetQQ).replace("{QQIcoStyle}", qqStyle);

        var onlineStatus = $.fn.multiQQInvoker.storage.onlineStatus[targetQQ] || 0;


        var img = $($QQDom).find("img").eq(0);
        if (img.size() == 0) {
            img = $('<img/>');
            $($QQDom).append(img);
        }
        img.attr("title", opts.title);
        if (G.Lang.isEmpty(qqStyle) && !G.Lang.isEmpty(bcgogoQQStyle)) {
            img.attr("src", opts.qqStyleClass[bcgogoQQStyle][onlineStatus]["url"]);
            if (!G.Lang.isEmpty(opts.qqStyleClass[bcgogoQQStyle][onlineStatus]["css"])) {
                img.css(opts.qqStyleClass[bcgogoQQStyle][onlineStatus]["css"]);
            }
        } else {
            img.attr("src", qqImageUrl);
        }

        if ($($QQDom).attr("tagName") == "A") {
            $($QQDom).attr("href", qqTalkUrl)
                .attr("target", "_blank");
        } else {
            $($QQDom).bind("click", function () {
                window.open(qqTalkUrl, "_blank");
            })
        }
        if($.isFunction(callBack)){
            callBack();
        }

    }

    // 插件的defaults
    $.fn.multiQQInvoker.defaults = {
        qqTalkUrl: "http://wpa.qq.com/msgrd?v=3&uin={qq}&site=qq&menu=yes",
        qqImageUrl: "http://wpa.qq.com/pa?p=1:{qq}:{QQIcoStyle}",
//        QQIcoStyle: 52,//调用腾讯提供在线图标方案的时候用
//        qqOnlineCheckUrl: "http://webpresence.qq.com/getonline?Type=1&{qqStr}",   //qqStr 格式为"qq1:qq2:qq3:"
        qqOnlineCheckUrl: "QQRedirect.do?method=getQQStatus&qqStr={qqStr}",   //qqStr 格式为"qq1:qq2:qq3:"
//        elem: '<a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin={qq}&site=qq&menu=yes"><img border="0" src="http://wpa.qq.com/pa?p=2:{qq}:{QQIcoStyle}"/></a>',
        appQQStyle:"bcgogoSmall", //调用自定义在线图标方案用
        qqStyleClass:{
            "bcgogoSmall": {
                1: {
                    url: "images/icon_qq_online1.gif"
                },
                0: {
                    url: "images/icon_qq_offline1.gif"
                }
            }
        },
        title: "点击这里给我发消息"
    };
    //  onlineStatus key 是qq号，value是status  1在线，0不在线
    $.fn.multiQQInvoker.storage = {
        onlineStatus: {}
    };
    $.fn.multiQQInvoker.getContactQQ = function () {
        var qqArray = new Array();
        for (var i = 0; i < 3; i++) {
            var level = $("#contacts\\[" + i + "\\]\\.level").val();
            var qq = $("#contacts\\[" + i + "\\]\\.qq").val();
            if (level == "0" && !G.Lang.isEmpty(qq)) {
                qqArray.push(qq);
            }
        }
        for (var i = 0; i < 3; i++) {
            var level = $("#contacts\\[" + i + "\\]\\.level").val();
            var qq = $("#contacts\\[" + i + "\\]\\.qq").val();
            if (level != "0" && !G.Lang.isEmpty(qq)) {
                qqArray.push(qq);
            }
        }
        return qqArray;
    };
// 闭包结束
})(jQuery);



