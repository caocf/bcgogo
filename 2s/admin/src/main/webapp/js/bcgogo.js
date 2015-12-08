/**
 * 此文件依赖    js/extension/jquery/jquery-1.4.2.js   base.js
 * @修改者 潘震 2012-07-03
 */
(function () {
    window.bcgogo = {
        /**
         * @description 检查是否存在session
         * @params value
         * {
         *     iframe_PopupBox: 弹出的iframe对象 ,
         *     src: iframe显示的页面的路径
         * }
         */
        checksession:function (value) {
            var gl = GLOBAL;
            value["iframe_PopupBox"]["src"] = "";

            // 先检测 session 是否存在
            var sessionCheckResult = APP_BCGOGO.Net.syncGet({url:"active.do?method=checksession", data:{},dataType:"json"});
            // 如果检测通过， 弹出弹出窗口 ， 如果没有通过， 进行警告报警
            if (gl.Lang.isString(sessionCheckResult) && (sessionCheckResult === "false")) {
                window.location = "login.jsp";
            } else {
                // compatible for IE and Firefox
                var done = false;
                value["iframe_PopupBox"].onload = value["iframe_PopupBox"].onreadystatechange = function() {
                    if(!done && (!this.readyState  || this.readyState == 'loaded' || this.readyState == 'complete')) {
                        done = true;
                        renderSubPage(value);
                    }
                };
                value["iframe_PopupBox"]["src"] = value["src"];
                Mask.Login();
                value["iframe_PopupBox"].style.display = "block";
            }
        }
    };

    function renderSubPage( value ) {
        value["div_show"] = value["iframe_PopupBox"].contentWindow.document.getElementById("div_show");
        setTimeout( function(){
            setPosition(value);
        }, 200 );
    }

    function setPosition(value) {
        var documentWidth = value["parentWindow"].document.documentElement.clientWidth;
        var documentHeight = value["parentWindow"].document.documentElement.clientHeight;
        if (value["div_show"] != null) {
            var iframeWidth = value["div_show"].clientWidth;
            var iframeHeight = value["div_show"].clientHeight;

            //弹出窗口位置不合理  add by dongnan
//            value["iframe_PopupBox"].style.left = (documentWidth - iframeWidth) / 2 + "px";
//            value["iframe_PopupBox"].style.top = (documentHeight - iframeHeight) / 2 + "px";
        }
    }

})();