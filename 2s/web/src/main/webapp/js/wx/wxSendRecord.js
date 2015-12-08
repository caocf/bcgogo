;
$(function () {

    $("#backBtn").click(function () {
        window.location.href = "weChat.do?method=toWxSent";
    });

    $("#delBtn").click(function () {
        var msgId = $(this).attr("msgId");
        nsDialog.jConfirm("确认删除?", null, function (flag) {
            var result = false;
            if (flag) {
                APP_BCGOGO.Net.syncGet({
                    url: "weChat.do?method=deleteWXMsg",
                    data: {
                        "id": msgId,
                        "now": new Date()
                    },
                    dataType: "json",
                    success: function (result) {
                        if (G.isEmpty(result)) {
                            nsDialog.jAlert("网络异常");
                            return false;
                        }
                        if (!result.success) {
                            nsDialog.jAlert(result.msg);
                            return false;
                        }
                        window.location.href = "weChat.do?method=toWxSent";
                    }
                });
            }
            return result;
        });
    });

    //消息列表item
    $(".j_msg_item").live("click", function () {
        var msgId = $(this).attr("msgId");
        window.location.href = "weChat.do?method=toFindWxSend&id=" + msgId;
    });
});

function inintTable_SendRecord(jsonStr) {
    if (G.isEmpty(jsonStr) || jsonStr[0].totalRows == 0) {
        var tr = '<tr><td colspan="7">没有发送数据</td></tr>';
        $("#table_AdultJob").append(jQuery(tr));
    }
    jQuery("#table_AdultJob tr:not(:first)").remove();
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length - 1; i++) {
            var idStr = jsonStr[i].idStr == null ? " " : jsonStr[i].idStr;
            var titleT = jsonStr[i].title == null ? " " : jsonStr[i].title;
            var statusName = jsonStr[i].statusName == null ? " " : jsonStr[i].statusName;
            var category = G.normalize(jsonStr[i].category);
            var categoryStr = G.normalize(jsonStr[i].categoryStr);
            var receiver = jsonStr[i].receivers == null ? " " : jsonStr[i].receivers;
            var stime = jsonStr[i].stime == null ? " " : jsonStr[i].stime;
            var receiverCount = G.normalize(jsonStr[i].receiverCount);
            if (category == "MASS") {
                receiverCount = "";
            }                                            n
            var tr = '<tr class="j_msg_item opr-btn" msgId="' + idStr + '">';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td>' + receiver + '</td>';
            tr += '<td>' + titleT + '</td>';
            tr += '<td>' + stime + '</td>';
            tr += '<td>' + categoryStr + '</td>';
            tr += '<td title= \'' + statusName + '\'>' + statusName.substring(0, 25) + '</td>';
            tr += '<td>' + receiverCount + '</td>';
            tr += '</tr >';
            $("#table_AdultJob").append($(tr));
        }
    }

}

function toFindAdult(id) {
    window.location.href = "weChat.do?method=toFindWxSend&id=" + id;
}

