
/*
 * bcplayer
 *
 * 调用方式
 * bcplayer.launch(params)
 *
 * @params params 的格式样例如下
 *     {
 *         "id":"testVideo",
 *         "title":"Wildlife.flv",
 *         "name":"Wildlife.flv",
 *         "width":640,
 *         "height":360,
 *         "mp4":"http://192.168.1.27:8082/BCVideo/res/video/Wildlife/Wildlife.mp4",
 *         "flv":"http://192.168.1.27:8082/BCVideo/res/video/Wildlife.flv",
 *         "swfPlayerStr":"http://192.168.1.27:8081/BCVideo/res/swf/player_flv_maxi.swf",
 *         "xiSwfUrlStr":"http://192.168.1.27:8081/BCVideo/res/swf/playerProductInstall.swf",
 *         "contentWidth":640,
 *         "contentHeight":360
 *     }
 */
;(function () {
    var _launchHtmlVideo = function (playerParams) {
        var s = "" +
            "<video controls width=" + playerParams.width + " height=" + playerParams.height + ">" +
            "    <source src=\"" + playerParams.url + "\" type=\"" + playerParams.type + "\">" +
            "</video>";

        var root = document.getElementById(playerParams.id);
        root.style.display = "block";
        root.width = parseInt(playerParams.width) + "px";
        root.height = parseInt(playerParams.height) + "px";
        root.innerHTML = s;
    };

    var _launchFlvPlayer = function (playerParams) {
        // appendHtml
        var s = "" +
            "<p>" +
            "    To view this page ensure that Adobe Flash Player version" +
            "    11.1.0 or greater is installed." +
            "</p>" +
            "<script type=\"text/javascript\">" +
            "    var pageHost = ((document.location.protocol == \"https:\") ? \"https://\" : \"http://\");" +
            "            document.write(\"<a href='http://www.adobe.com/go/getflashplayer'><img src='\"" +
            "            + pageHost + \"www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' /></a>\" );" +
            "</script>";

        document.getElementById(playerParams.id).innerHTML = s;

        var defaultFlashvars = {
            "autoplay":0,
            "autoload":0,
            "buffer":5,
            "buffermessage":"",
            "playercolor":"464646",
            "loadingcolor":"999898",
            "buttoncolor":"ffffff",
            "buttonovercolor":"dddcdc",
            "slidercolor":"ffffff",
            "sliderovercolor":"dddcdc",
            "showvolume":"1",
            "showfullscreen":"1",
            "playeralpha":"100",
            "title":playerParams.title,
            "margin":"0",
            "buffershowbg":"0"
        };

        for (var k in playerParams.flashvars) {
            defaultFlashvars[k] = playerParams["flashvars"][k];
        }

        var params = {};
        params.quality = "high";
        params.bgcolor = "#ffffff";
        params.allowscriptaccess = "always";
        params.allowfullscreen = true;
        params.wmode = "opaque";
        params.movie = playerParams["swfPlayerStr"];
        params.quality = "high";
        params.menu = true;
        params.autoplay = false;
        params.autoload = false;
        var attributes = {};
        attributes.id = playerParams.id;
        attributes.name = playerParams.name || "KVPlayer";
        attributes.align = "middle";
        swfobject.embedSWF(
            playerParams.swfPlayerStr,
            playerParams.id,
            (playerParams.contentWidth || playerParams["width"]) ,
            (playerParams.contentHeight || playerParams["height"]),
            playerParams.swfVersionStr,
            playerParams.xiSwfUrlStr,
            defaultFlashvars,
            params,
            attributes);
        // JavaScript enabled so display the flashContent div in case it is not replaced with a swf object.
        swfobject.createCSS("#" + playerParams.id, "display:block;text-align:left;");
    };

    var player = {
        launch:function (playerParams) {
            // 根据特征选择不容的播放方式
            // 1. android 或者 safari 上使用 html5 video
            // 2. 其余使用 flash 来播放
            var userAgent = navigator.userAgent.toLocaleLowerCase(),
                isIOS = (userAgent.search("chrome") == -1 && userAgent.search("safari") != -1),
                isAndroid = userAgent.search("android") != -1;

            // 视频参数
            var param = {
                html:{
                    url:playerParams.mp4,
                    width:playerParams.width,
                    height:playerParams.height,
                    type:"video/mp4",
                    id:playerParams.id
                },
                flash:{
                    flashvars:{
                        "flv":playerParams.flv,
                        "width":playerParams.width,
                        "height":playerParams.height
                    },
                    swfPlayerStr:playerParams.swfPlayerStr,
                    id:playerParams.id,
                    // To use express install, set to playerProductInstall.swf, otherwise the empty string.
                    xiSwfUrlStr:playerParams.xiSwfUrlStr,
                    // For version detection, set to min. required Flash Player version, or 0 (or 0.0.0), for no version detection.
                    swfVersionStr:"11.1.0",
                    title:playerParams.title
                }
            }

            if (isIOS) {
                _launchHtmlVideo(param.html);
            } else if (isAndroid) {
                _launchHtmlVideo(param.html);
            } else {
                _launchFlvPlayer(param.flash);
            }
        }
    }


    window.bcplayer = player;
})();


/**
 * swf loader
 */
;(function(){
    var T = {
        content:"" +
            "<p>" +
            "    To view this page ensure that Adobe Flash Player version" +
            "    11.1.0 or greater is installed." +
            "</p>" +
            "<script type=\"text/javascript\">" +
            "    var pageHost = ((document.location.protocol == \"https:\") ? \"https://\" : \"http://\");" +
            "            document.write(\"<a href='http://www.adobe.com/go/getflashplayer'><img src='\"" +
            "            + pageHost + \"www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' /></a>\" );" +
            "</script>"
    };

    var swfLoader = {
        /**
         *
         * @param swfParams
         *
         * @param params 的格式样例如下
         *     {
         *         "id":"testVideo",
         *         "title":"Wildlife.flv",
         *         "name":"Wildlife.flv",
         *         "width":640,
         *         "height":360,
         *         "swfStr":"animation.swf",
         *         "xiSwfUrlStr":"http://192.168.1.27:8081/BCVideo/res/swf/playerProductInstall.swf",
         *         "contentWidth":640,
         *         "contentHeight":360
         *     }
         */
        launch:function(swfParams){
            var swfId = swfParams.id + "-swf",
                divTagHeader = "<div id='" + swfId + "' >";
            document.getElementById(swfParams.id).innerHTML = divTagHeader + T.content + "</div>";
            var defaultFlashvars = {};
            var params = {};
            params.quality = "high";
//            params.bgcolor = "#ffffff";
            params.allowscriptaccess = "always";
            params.allowfullscreen = true;
            params.wmode = "opaque";
            params.movie = swfParams["swfStr"];
            params.quality = "high";
//            params.menu = true;
            params.autoplay = false;
            params.autoload = false;
            var attributes = {};
            attributes.id = swfId;
            attributes.name = swfParams.name || "unnamed";
            attributes.align = "middle";
            swfobject.embedSWF(
                swfParams.swfStr,
                swfId,
                (swfParams.contentWidth || playerParams["width"]) ,
                (swfParams.contentHeight || playerParams["height"]),
                "11.1.0",
//                swfParams.swfVersionStr,
                swfParams.xiSwfUrlStr,
                defaultFlashvars,
                params,
                attributes);
            // JavaScript enabled so display the flashContent div in case it is not replaced with a swf object.
            swfobject.createCSS("#" + swfId, "display:block;text-align:left;");
        },
        remove:function(params) {
            if(params && params.hasOwnProperty("id")) {
                var swfId = params.id + "-swf";
                if(document.getElementById(swfId)) {
                    swfobject.removeSWF(swfId);
                }
            }
        }
    };

    window.bcSwfLoader = swfLoader;
})();




