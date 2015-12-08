var webSocket;
$(function () {
//    connect();

    $("#sendBtn").click(function() {
        var content = $("#content").val();
        if (!$.trim(content)) {
            return true;
        }
        var dialog = getRightDialog(content);
        $("#contentBody").append(dialog);
        $("#content").val("");

        var openId = $("#openId").val();
        var appUserNo = $("#appUserNo").val();
        var name;
        if ($("#pageType").val() == 'MSG_FROM_WX_USER_TO_MIRROR') {
            name = $("#appUserNo").val();
        } else {
            name = $("#shopId").val();
        }
        var data = {
            fromUserName:openId,
            appUserNo:appUserNo,
            toUserName:name,
            content:content,
            type:$("#pageType").val()
        };
        $.ajax({
            type: "POST",
            url: "/web/mirror/msg/send",
            data: data,
            dataType: "json",
            async:false,
            success: function(result) {

            }
        });

    });

    $("#moreBtn").click(function() {
        if ($(this).attr("lock")) return;
        $(this).attr("lock", "lock");
        $("#loadIcon").show();
        var $me = $(this);
        var start = Number($("#start").val()) + Number($(".dialogue").size());
        var limit = $("#limit").val();
        var appUserNo = $("#appUserNo").val();
        if (!appUserNo) return;
        $.ajax({
            type: "POST",
            url: "/web/mirror/msg/list",
            data: {
                appUserNo: appUserNo,
                start: start,
                limit: limit,
                type:$("#pageType").val()
            },
            dataType: "json",
            async:false,
            success: function(listResult) {
                $me.removeAttr("lock");
                $("#loadIcon").hide();
                var pushMessages = listResult.results;
                if (!pushMessages || pushMessages.length == 0) return;
                for (var i = 0; i < pushMessages.length; i++) {
                    var pushMessage = pushMessages[i];
                    var content = pushMessage.promptContent;
                    var dialog = "";
                    if (pushMessage.type == 'MSG_FROM_WX_USER_TO_MIRROR' || pushMessage.type == 'MSG_FROM_WX_USER_TO_SHOP') {
                        dialog = getRightDialog(content);
                    } else {
                        dialog = getLeftDialog(content);
                    }
                    var $first = $(".dialogue").first();
                    if ($first.size() == 0) {
                        $first = $("#contentBody");
                    }
                    $first.before(dialog);
                }

            },
            error:function(e) {
                $me.removeAttr("lock");
                $("#loadIcon").hide();
            }
        });
    });

    setInterval("checkStatus()", 5000);
//    $(window).bind('beforeunload', function() {
//        alert("good");
//    });

});

function checkStatus() {
    //检测客户端是否上线
    var appUserNo = $("#appUserNo").val();
    if (!appUserNo) return;
    var name;
    if ($("#pageType").val() == 'MSG_FROM_WX_USER_TO_MIRROR') {
        name = $("#appUserNo").val();
    } else {
        name = $("#shopId").val();
    }
    $.ajax({
        type: "GET",
        url: "/web/mirror/isOnLine/" + name,
        dataType: "json",
        data:{
            type:$("#pageType").val()
        },
        async:false,
        success: function(result) {
            if (result) {
                $(".j_content_remind").hide();
            } else {
                $(".j_content_remind").show()
            }

        }
    });
//    //检测连接是否中断
//    if (webSocket.readyState == 2 || webSocket.readyState == 3) {   //已经连接
//        connect();
//    }
}

function connect() {
    try {
        if (webSocket && (webSocket.readyState == 0 || webSocket.readyState == 1)) {   //已经连接
            return;
        }
        var sUrl = $("#wsUrl").val();
        webSocket = new WebSocket(sUrl);
        webSocket.binaryType = "arraybuffer";

        webSocket.onopen = function (evt) {
            console.info("连接已经建立。");
            var openId = $("#openId").val();
            var data = {
                fromUserName:openId,
                content:"_msg_center_login_from_client"
            }
            webSocket.send(JSON.stringify(data));
        };

        webSocket.onmessage = function (evt) {
            var data = JSON.parse(evt.data);
            var dialog = getLeftDialog(data.content);
            $("#contentBody").append(dialog);
        };

        webSocket.onclose = function (evt) {
            console.info("连接关闭。");
        };

        webSocket.onerror = function (evt) {
            console.info("连接出错。");
        }

    } catch (e) {
        console.info(e);
    }
}


function getRightDialog(content) {
    return '<div class="dialogue">' +
        '<div class="sj_relative">' +
        '<div class="lan_absolute"><img src="/web/images/wx_mirror_images/lan_sj.png"></div>' +
        '<div class="w76 fl">' +
        '<div class="a1 fr">' + content + '</div>' +
        '</div> ' +
        '</div>' +
        '<div class="w23  fr tr"> ' +
        '<div class="w99"><img src="' + $("#headimgurl").val() + '"></div> ' +
        '</div>  ' +
        '<div class="clr"></div> ' +
        '</div>';
}

function getLeftDialog(content) {
    return '<div class="dialogue">' +
        '<div class="sj_relative">' +
        '<div class="bai_absolute"><img src="/web/images/wx_mirror_images/bai_sj.png"></div>' +
        '<div class="w76 fr">' +
        '<div class="a2 fr">' + content + '</div></div>' +
        '</div> ' +
        '<div class="w23  fl"> ' +
        '<div class="w99"><img src="/web/images/wx_mirror_images/tou2.jpg"></div> ' +
        '</div>' +
        '<div class="clr"></div> ' +
        '</div>';
}

