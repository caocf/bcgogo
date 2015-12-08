var webSocket,app_user_no,from_user_name;
$(function () {

    $("#sendBtn").click(function() {
        var content = $("#content").val();
        if (!$.trim(content)) {
            return true;
        }
        var shopId = $("#shopId").val();
        var data = {
            fromUserName:shopId,
            appUserNo:app_user_no,
            toUserName:from_user_name,
            content:content,
            type:"MSG_FROM_SHOP_TO_WX_USER"
        };
        var dialog = getRightDialog(content);
        $("#contentBody").append(dialog);
        $("#content").val("");
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
        var start = Number($("#talk_start").val()) + Number($(".dialogue").size());
        var limit = $("#talk_limit").val();
        if (!app_user_no) return;
        $.ajax({
            type: "POST",
            url: "/web/mirror/msg/list",
            data: {
                appUserNo: app_user_no,
                start: start,
                limit: limit,
                type:'talkWith4S'
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
                        dialog = getLeftDialog(content);
                    } else {
                        dialog = getRightDialog(content);
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

//    setInterval("checkStatus()", 5000);

});

function checkStatus() {
    //检测客户端是否上线
    if (!app_user_no) return;
    $.ajax({
        type: "GET",
        url: "/web/mirror/isOnLine/" + from_user_name,
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
    //检测连接是否中断
    if (webSocket.readyState == 2 || webSocket.readyState == 3) {   //已经连接
        connect();
    }
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
                fromUserName:$("#shopId").val(),
                content:"_msg_center_login_from_client"
            }
            webSocket.send(JSON.stringify(data));
        };

        webSocket.onmessage = function (evt) {
            var data = JSON.parse(evt.data);
            var dialog = getRightDialog(data.content);
            $("#contentBody").append(dialog);
        };

        webSocket.onclose = function (evt) {
            console.info("连接关闭。");
        };

        webSocket.onerror = function (evt) {
            console.info("连接出错。")
            connect();
        }

    } catch (e) {
        console.info(e);
    }
}


function getRightDialog(content) {
    return '<div class="dialogue">' +
        '<div class="sj_relative">' +
        '<div class="w76 fl">' +
        '<div class="a1 fr">' + content + '</div>' +
        '</div>' +
        '</div>' +
        '<div class="w23  fr tr">' +
        '<div class="w99"><img width="59" src="./images/wx_mirror_images/tou.jpg"></div>' +
        '</div>' +
        '<div class="clr"></div>' +
        '</div>';
}

function getLeftDialog(content) {
    return '<div class="dialogue">' +
        '<div class="sj_relative">' +
        '<div class="w76 fr">' +
        '<div class="a2 fl">' + content + ' </div> ' +
        '</div>' +
        '</div>' +
        '<div class="w23  fl">' +
        '<div class="w99"><img width="59" src="./images/wx_mirror_images/tou2.jpg"></div> ' +
        '</div>' +
        '<div class="clr"></div>' +
        ' </div>';
}

