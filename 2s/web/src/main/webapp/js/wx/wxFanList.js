;
$(function () {

    $("#sendWXMsgBtn").click(function () {
        if ($(".J_user_container input").size() == 0) return;
        var userIds = new Array();
        $(".J_user_container input").each(function () {
            if (!G.isEmpty($(this).val())) {
                userIds.push($(this).val());
            }
        });
        $(".J_user_container").submit();
    });

    $("#searchBtn").click(function () {
        queryWXUser();
    });
    //init
    queryWXUser();

});

$(function () {

    $(".J_item_note").live("click", function () {
        var $target = $(this);
        var openId = $(this).attr("openId");
        APP_BCGOGO.Net.asyncGet({
            url: "weChat.do?method=getWXUserDTOByOpenId",
            data: {
                "openId": openId,
                "now": new Date()
            },
            dataType: "json",
            success: function (result) {
                if (G.isEmpty(result)) {
                    nsDialog.jAlert("网络异常");
                    return;
                }
                if (!result.success) {
                    nsDialog.jAlert(result.msg);
                    return;
                }
                var userDTO = result.data;
                _showEditor(userDTO, $target);
            }
        });

        var _showEditor = function (userDTO, $target) {
            $("#headImg").attr("src", userDTO.headimgurl);
            $("#nickName").text(userDTO.nickname);
            $("#city").text(userDTO.city);
            $("#remark").val(userDTO.remark);

            $("#wxUserEditor").dialog({
                resizable: true,
                draggable: true,
                title: "编辑",
                height: 230,
                width: 330,
                modal: true,
                closeOnEscape: false,
                buttons: {
                    "确定": function () {
                        var remark = $("#remark").val();
                        if (G.isEmpty(remark)) {
                            nsDialog.jAlert("请输入备注名。");
                            return;
                        }
                        APP_BCGOGO.Net.asyncGet({
                            url: "weChat.do?method=remarkWXUser",
                            data: {
                                "openId": openId,
                                "remark": remark,
                                "now": new Date()
                            },
                            dataType: "json",
                            success: function (result) {
                                if (G.isEmpty(result)) {
                                    nsDialog.jAlert("网络异常");
                                    return;
                                }
                                if (!result.success) {
                                    nsDialog.jAlert(result.msg);
                                    return;
                                }
                                $target.closest("td").find(".J_item_remark").text(remark);
                                $("#wxUserEditor").dialog("close");
                            }
                        });
                    },
                    "取消": function () {
                        $("#wxUserEditor").dialog("close");
                    }
                },
                close: function () {
                    $("#divMobile").val("");
                }
            });
        }


    });
});

function queryWXUser() {
    var keyWord = $.trim($("#keyword_input").val());
    var url = "weChat.do?method=initFanList";
    var data = {
        keyWord: keyWord,
        startPageNo: 1,
        pageSize: 10
    };
    APP_BCGOGO.Net.asyncAjax({
        url: url,
        type: "POST",
        cache: false,
        data: data,
        dataType: "json",
        success: function (result) {
            initTable_Fan(result);
            initPage(result, "_initTable_Fan", url, null, "initTable_Fan", '', '', data, null);
        },
        error: function () {
            nsDialog.jAlert("网络异常。");
        }
    });
}


function initTable_Fan(data) {
    $("#table_FanJob tr:not(:first)").remove();
    var wxUserDTOs = data.results.results;
    if (G.isEmpty(wxUserDTOs)) {
        return;
    }
    for (var i = 0; i < wxUserDTOs.length; i++) {
        var wxUserDTO = wxUserDTOs[i];
        var openId = G.normalize(wxUserDTO.openid);
        var idStr = G.normalize(wxUserDTO.idStr);
        var headimgurl = G.normalize(wxUserDTO.headimgurl);
        var nickName = G.normalize(wxUserDTO.nickname);
        var remark = G.normalize(wxUserDTO.remark);
        var wxFanDTOs = G.normalize(wxUserDTO.wxFanDTOs);
        var rLen = wxFanDTOs.length == 0 ? 2 : (wxFanDTOs.length + 1);
        var tr = '<tr>';

        tr += '<td rowspan="' + rLen + '"><input onclick="checkboxListen(this)" type="checkbox" name="userId" value="' + idStr + '"></td>'
        tr += '<td class="t-head-img-url" rowspan="' + rLen + '">';
        tr += '<img width="45" height="45" src="' + headimgurl + '">';
        tr += '</td>';
        tr += '<td rowspan="' + rLen + '">' + nickName + '</td>';
        tr += '<td rowspan="' + rLen + '"><span class="J_item_remark">' + remark + '</span><a class="J_item_note fan-note" openId="' + openId + '">备注</a></td>';
        tr += '</tr>';
        if (G.isEmpty(wxFanDTOs) || wxFanDTOs.length == 0) {
            tr += '<tr><td></td><td></td><td></td><td></td><td></td></tr>';
            $("#table_FanJob").append(tr);
            continue;
        }
        $("#table_FanJob").append(tr);
        for (var j = 0; j < wxFanDTOs.length; j++) {
            var wxFanDTO = wxFanDTOs[j];
            var licenceNo = G.normalize(wxFanDTO.licenceNo);
            var brand = G.normalize(wxFanDTO.brand);
            var model = G.normalize(wxFanDTO.model);
            var name = G.normalize(wxFanDTO.name);
            var mobile = G.normalize(wxFanDTO.mobile);
            var vehicleId = G.normalize(wxFanDTO.vehicleId);
            var customerId = G.normalize(wxFanDTO.customerId);
            var tr = '<tr>';
            if (G.Lang.isEmpty(customerId) || G.Lang.isEmpty(vehicleId)) {
                tr += '<td>' + licenceNo + '</td>';
            } else {
                tr += '<td >' + '<a style="cursor: pointer" onclick="searchVehicle(\'' + customerId + '\',\'' + vehicleId + '\')" >' + licenceNo + '</a></td>';
            }
            tr += '<td>' + brand + '</td>';
            tr += '<td>' + model + '</td>';
            if (G.isEmpty(customerId)) {
                tr += '<td>' + name + '</td>';
            } else {
                tr += '<td >' + '<a style="cursor: pointer" onclick="searchCustomer(\'' + customerId + '\')">' + name + '</a></td>';
            }
            tr += '<td>' + mobile + '</td>';
            tr += '</tr>';
            $("#table_FanJob").append(tr);
        }
    }
    if ($(".J_user_container input").each(function () {
            $("[name='userId'][value='" + $(this).val() + "']").attr("checked", "checked");
        }));
}

function searchVehicle(customerId, vehicleId) {
    if (G.Lang.isNotEmpty(customerId) && G.Lang.isNotEmpty(vehicleId)) {
        window.open("unitlink.do?method=customer&customerId=" + customerId + "&vehicleId=" + vehicleId + "#customerDetailVehicle");
    }
}

//unitlink.do?method=customer&customerId=10000010214829881&fromPage=customerData
function searchCustomer(customerId) {
    if (G.Lang.isNotEmpty(customerId)) {
        window.open("unitlink.do?method=customer&customerId=" + customerId + "&fromPage=customerData");
    }
}


//checkbox全选全不选
function allCheckOrNot() {
    if (document.getElementById("allCheckBox").checked == true) {
        $("input[name='userId']").each(function () {
            $(this).attr("checked", true);
        });
    } else {
        $("input[name='userId']").each(function () {
            $(this).attr("checked", false);
        });
    }
    var s = document.getElementsByName("userId");
    var s2 = 0;
    var i = s.length;
    for (var i = 0; i < s.length; i++) {
        if (s[i].checked) {
            s2++;
        }
    }
    var tr = '';
    tr += '<td id="fanNum" style="display: block">已选中' + " " + s2 + " " + '位粉丝</td>';
    jQuery(tr).replaceAll("#fanNum");
}


//checkbox触发处理
function checkboxListen(target) {
    if (G.isEmpty(target)) return;
    var userId = $(target).val();
    $(".J_user_container").find('[value="' + userId + '"]').remove();
    if (!$(target).attr("checked")) return;
    $(".J_user_container").append("<input name='userIds' value='" + userId + "'>");
}