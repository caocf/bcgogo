/**
 * 此文件依赖    js/extension/jquery/jquery-1.4.2.js   base.js
 * @修改者 潘震 2012-07-03
 */
;(function() {
    window.bcgogo = {
        /**
         * @description 检查是否存在session
         * @params value
         * {
         *     iframe_PopupBox: 弹出的iframe对象 ,
         *     src: iframe显示的页面的路径
         * }
         */
        checksession: function(value) {
            $(value["iframe_PopupBox"]).attr("src", "");

            // 先检测 session 是否存在
            var sessionCheckResult = APP_BCGOGO.Net.syncGet({
                url: "active.do?method=checksession",
                contentType:"utf-8",
                data: {},
                dataType: "json"
            });
            // 如果检测通过， 弹出弹出窗口 ， 如果没有通过， 进行警告报警
            if(G.Lang.isString(sessionCheckResult) && (sessionCheckResult === "false")) {
                window.location = "login.jsp";
            } else {
                // compatible for IE and Firefox
                var isIframeLoaded = false;
                value["iframe_PopupBox"].onload = value["iframe_PopupBox"].onreadystatechange = function() {
                    if(!isIframeLoaded && G.contains(this.readyState, [undefined, "loaded", "complete"])) {
                        isIframeLoaded = true;
                        setPosition(value);
                        $(value["iframe_PopupBox"]).show("fast", function() {

                            //不用设置 iframe 的宽度， 弹出框将自动设置宽度（值为子页面的宽度）
                            var subWidth = $(value["iframe_PopupBox"].contentWindow.document).width();
                            $(value["iframe_PopupBox"]).attr("width", (subWidth + 5)+"px");

                            setPosition(value);
                        });
                    }
                };
                $(value["iframe_PopupBox"]).attr("src", value["src"]);
                Mask.Login();
//                $("#mask").css({
//                    'height': document.documentElement.scrollHeight + "px"
//                });
            }
        }
    };

    function setPosition(value) {
        var documentW = value["parentWindow"].document.documentElement.clientWidth;
        var documentH = value["parentWindow"].document.documentElement.clientHeight;
        var iframeW = 0,
            iframeH = 0;
        if(value["div_show"]) {
            iframeW = value["div_show"].clientWidth;
            iframeH = value["div_show"].clientHeight;
        } else {
            iframeW = parseFloat($(value["iframe_PopupBox"]).width());
            iframeH = $(value["iframe_PopupBox"]).height();
        }
        var _top = Math.abs((documentH - iframeH) / 2),
            _left = Math.abs((documentW - iframeW) / 2);

        try{
            $(value["iframe_PopupBox"]).css("position", "absolute").css("left", _left + "px").css("top", _top + "px");
        }catch(e){
            ;
        }
        value["iframe_PopupBox"].scrollIntoView(true);

    }

})();