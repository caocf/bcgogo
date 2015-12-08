$(function () {
    //初始化token组件
    _$10(".J_selected_fans").tokenfield({
        allowEditing: false,
        allowPasting: false,
        inputType: 'text \" readOnly=\"readOnly'
    });

    $("#clearBtn").click(function () {
        $("#w_description").val("");
    });

    $(".J_all_fans").click(function () {
        _$10(".J_selected_fans").tokenfield("setTokens", [{value: "ALL_FANS", label: "全部粉丝"}]);

    });
    //正式发送
    $(".J_send").click(function () {
        $(".J_all_fans").show();
        $(".J_wxReceiverGroupType").val("OFFICIAL");
        $(".a-click").removeClass("a-click");
        $(this).addClass("a-click");
        $(".J_wx_msg_submit").text("提交审核");
    });
    //测试发送
    $(".J_test_send").click(function () {
        $(".J_all_fans").hide();
        $(".a-click").removeClass("a-click");
        $(this).addClass("a-click");
        $(".J_wxReceiverGroupType").val("TEST");
        $(".token").each(function(){
            if($(this).find(".token-label").text()=='全部粉丝'){
                $(this).remove();
            }
        });
        $(".J_wx_msg_submit").text("发送");
    });

    $(".J_wxUser_item").live("click", function () {
        var $item = $(this);
        var openid = $item.attr("openId");
        var wxReceivers = _$10(".J_selected_fans").tokenfield('getTokens');
        var isExist = false;
        if (wxReceivers && wxReceivers.length > 0) {
            for (var i = 0; i < wxReceivers.length; i++) {
                if (wxReceivers[i]["value"] === "ALL_FANS") {
                    _$10(".J_selected_fans").tokenfield('setTokens', []);
                }
            }
            for (var i = 0; i < wxReceivers.length; i++) {
                if (wxReceivers[i]["value"] === openid) {
                    isExist = true;
                    return false;
                }
            }
        }
        var nickName = $item.attr("nickName");
        _$10(".J_selected_fans").tokenfield('createToken', {value: openid, label: nickName});
    });

    $(".J_item_note").live("click", function () {
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
                _showEditor(userDTO);
            }
        });

        var _showEditor = function (userDTO) {
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
                                var $target = $(".J_wxUser_item[openid=" + openId + "]");
                                $target.attr("nickname", remark);
                                $target.find(".wechat_txt").text(remark+"("+userDTO.nickname+")");
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

    $(".J_to_select_template").click(function () {

        $(".J_wx_template_container").dialog({
            resizable: true,
            draggable: true,
            title: "微信模板",
            height: 300,
            width: 690,
            modal: true,
            closeOnEscape: false,
            open: function () {
                var url = "weChat.do?method=getWXMsgTemplate";
                var data = {
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
                        drawWXArticleTemplate(result);
                        initPage(result, "_wxArticleTemplate", url, null, "drawWXArticleTemplate", '', '', data, null);
                    },
                    error: function () {
                        nsDialog.jAlert("网络异常。");
                    }
                });

            }
        });

    });

    $(".J_use_wx_template").live("click", function () {
        var parentItem = $(this).parents(".J_wxUser_item");
        var title = parentItem.find(".J_wx_template_title").val();
        var description = parentItem.find(".J_wx_template_description").val();
        var picUrl = parentItem.find(".J_wx_template_picUrl").val();
        var idStr = parentItem.find(".J_wx_template_id").val();
        $(".J_wx_title").val(title);
        $(".J_wx_article_picUrl").val(picUrl);
        $(".J_wx_article_img_show").attr("src", picUrl);
        $(".J_wx_description").val(description);
        $(".J_wx_template_id").val(idStr);
        $(".J_wx_template_container").dialog("close");
        _calculateContentLen();
    });

    $(".J_img_select").click(function () {
        $(".J_wxImgFile").click();
    });

    $(".J_wxImgFile").change(function (e) {
        onUploadImgChange($(this).get(0));
    });

    $(".J_wx_msg_submit").click(function () {
        var mask = APP_BCGOGO.Module.waitMask;
        mask.login();
        if(G.isEmpty($(".J_wxReceiverGroupType").val())){
            mask.open();
            nsDialog.jAlert("请选择发送方式。");
            return;
        }
        var wxReceivers = _$10(".J_selected_fans").tokenfield('getTokens');
        if (wxReceivers.length == 0) {
            mask.open();
            nsDialog.jAlert("请发送要发送的微信用户。");
            return;
        }
        var validateReceivers = [];
        if (wxReceivers && wxReceivers.length > 0) {
            for (var i = 0; i < wxReceivers.length; i++) {
                if (wxReceivers[i]["value"] != "ALL_FANS") {
                    validateReceivers.push(wxReceivers[i]["value"]);
                }
            }
        }

        if (G.isEmpty($(".J_wx_article_img_show").attr("src"))) {
            mask.open();
            nsDialog.jAlert("消息图片不应为空。");
            return;
        }
        var data = {};
        data["receiverOpenIds"] = validateReceivers;
        var description = $("#w_description").val();
        if (G.isEmpty(description)) {
            mask.open();
            nsDialog.jAlert("请填写要发送内容。");
            return;
        }
        var title = $(".J_wx_title").val();
        if (G.isEmpty(title)) {
            mask.open();
            nsDialog.jAlert("请填写内容标题。");
            return;
        }
        $(".J_wx_msg_form").ajaxSubmit({
            data: data,
            success: function (data) {
                mask.open();
                if (data && data.success) {
                    nsDialog.jAlert(data.msg, null, function () {
                        window.location.href = 'weChat.do?method=toWxSent';
                    });
                } else {
                    nsDialog.jAlert(data.msg);
                }
            },
            error: function () {
                mask.open();
            }
        })
    });

    $("#w_description").focus(function () {
        $(this).closest(".mes_textarea").addClass("wxContentFocus");
    })
        .focusout(function () {
            $(this).closest(".mes_textarea").removeClass("wxContentFocus");
        });

    $("#w_description").live("keyup blur", function () {
        _calculateContentLen();
    });

    $(".J_wx_title").focus(function () {
        $(".J_wx_title_container").addClass("focus")
    }).focusout(function () {
        $(".J_wx_title_container").removeClass("focus")
    });

    $("#searchBtn").click(function () {
        var keyWord = $.trim($("#serachWord").val());
        var url = "weChat.do?method=getShopWXUsers";
        var data = {
            keyWord: keyWord,
            currentPage: 1,
            pageSize: 10
        };
        APP_BCGOGO.Net.asyncAjax({
            url: url,
            type: "POST",
            cache: false,
            data: data,
            dataType: "json",
            success: function (result) {
                if(G.isEmpty(result)) return true;
                drawWXUserList(result);
                initPage(result, "_wxUser", url, null, "drawWXUserList", '', '', data, null);
            },
            error: function () {
                nsDialog.jAlert("网络异常。");
            }
        });
    });

});

function removeAllFanToken(){
    $(".token").each(function(){
        if($(this).find(".token-label").text()=="全部粉丝"){
            $(this).find(".close").click();
        }
    });
}

function _calculateContentLen() {
    var limit = 500;
    var len = $("#w_description").val().length;
    $("#contentLength").text(len);
    var left = limit - len;
    $("#leftLength").text(left < 0 ? 0 : left);
}

function onUploadImgChange(file) {
    var pic = $(".J_wx_article_img_show").get(0);
//    var file = document.getElementById("f");
    var ext = file.value.substring(file.value.lastIndexOf(".") + 1).toLowerCase();
    // gif在IE浏览器暂时无法显示
    var lastFileDom = $(".J_wxImgFile_lastFile").get(0);
    if (ext != 'png' && ext != 'jpg' && ext != 'jpeg') {
        nsDialog.jAlert("上传文件必须为图片(jpg,pnd,jpeg)！", null, function () {
//            file.click();
        });
        return;
    }
    $(".J_img_name").text(file.value);
    // IE浏览器
    if (document.all) {
        file.select();
        var reallocalpath = document.selection.createRange().text;
        var ie6 = /msie 6/i.test(navigator.userAgent);
        // IE6浏览器设置img的src为本地路径可以直接显示图片
        if (ie6) pic.src = reallocalpath;
        else {
            // 非IE6版本的IE由于安全问题直接设置img的src无法显示本地图片，但是可以通过滤镜来实现
            pic.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='image',src=\"" + reallocalpath + "\")";
            // 设置img的src为base64编码的透明图片 取消显示浏览器默认图片
            pic.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAP///wAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';
        }
    } else {
        var imgFiles = file.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(imgFiles);
        reader.onload = function (e) {
            pic.src = this.result;
        }
    }
    $(".J_wx_article_picUrl").val("");

}

function drawWXUserList(result) {
    var wxUserDTOs = result.data;
    if (G.isEmpty(wxUserDTOs)) return;
    $(".j_wxUserContainer li").remove();
    $(".J_total_fans_num").text(wxUserDTOs.length);
    var html = '';
    for (var i = 0; i < wxUserDTOs.length; i++) {
        var wxUserDTO = wxUserDTOs[i];
        var headimgurl = G.normalize(wxUserDTO.headimgurl);
        var name_title = G.normalize(G.isEmpty(wxUserDTO.remark) ? wxUserDTO.nickname : wxUserDTO.remark);
        var name=name_title;
        if(!G.isEmpty(wxUserDTO.remark)) {
            name_title+="("+wxUserDTO.nickname+")";
        }
        var openid = G.normalize(wxUserDTO.openid);
        html += '<li>';
        html += '<div class="wechat_personal">';
        html += '<span class="J_wxUser_item user-item" openId ="' + openid + '" headimgurl = "' + headimgurl + '" nickName= "' + name + '">';
        html += '<div class="wechat_photo">';
        if (G.isNotEmpty(headimgurl)) {
            html += '<img src="' + headimgurl + '" width="30" height="30" />'
        }
        html += '</div>';
        html += '<div class="wechat_txt">' + name_title + '&nbsp;&nbsp;</div>';
        html += '</span>';
        html += '<span class="J_item_note wechat_note" openId="'+openid+'"><a class="wechat_note_icon"></a></span>';
        html += '<div class="clear"></div>';
        html += '</div>'
        html += '</li>';
    }
    $(".j_wxUserContainer").html(html);
}

function drawWXArticleTemplate(data) {
    $(".J_wx_template_tb tr.J_wxUser_item").remove();
    var wxTemplateDTOs = data.results;
    var html = '';
    if (wxTemplateDTOs) {
        for (var i = 0; i < wxTemplateDTOs.length; i++) {
            var wxTemplateDTO = wxTemplateDTOs[i];
            var title = G.Lang.normalize(wxTemplateDTO.title);
            var description = G.Lang.normalize(wxTemplateDTO.description);
            var picUrl = G.Lang.normalize(wxTemplateDTO.picUrl);
            var idStr = G.Lang.normalize(wxTemplateDTO.idStr);
            html += '<tr class="J_wxUser_item">';
            html += '<td style="padding-left:10px;">' + (i + 1);
            html += '<input type="hidden" class="J_wx_template_title"  value="' + title + '">';
            html += '<input type="hidden" class="J_wx_template_description"  value="' + description + '">';
            html += '<input type="hidden" class="J_wx_template_picUrl"  value="' + picUrl + '">';
            html += '<input type="hidden" class="J_wx_template_id" value="' + idStr + '">';
            html += '</td>';
            html += '<td>' + title + '</td>';
            html += '<td>' + description + '</td>';
            html += '<td><a class="blue_color J_use_wx_template" style="color: #006eca">使用</a></td>';
            html += '</tr>';


        }
    }
    $(".J_wx_template_tb").append(html);
}

