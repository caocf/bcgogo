
$(function () {

    $("#clearBtn").click(function(){
        $("#w_description").val("");
    });

    $(".J_all_fans").click(function(){
        $(".a-click").removeClass("a-click");
        $(this).addClass("a-click");
        $(".J_wxReceiverGroupType").val("ALL_FANS");
        _$10(".J_selected_fans").tokenfield("setTokens",[{value: "ALL_FANS", label: "全部粉丝"}]);
        $(".J_wx_msg_submit").text("提交审核");
    });

    $(".J_test_fans").click(function(){
        $(".a-click").removeClass("a-click");
        $(this).addClass("a-click");
        $(".J_wxReceiverGroupType").val("TEST_FANS");
        var wxReceivers = _$10(".J_selected_fans").tokenfield('getTokens');
        var isExist = false;
        var validateReceivers = [];
        if (wxReceivers && wxReceivers.length > 0) {
            for (var i = 0; i < wxReceivers.length; i++) {
                if(wxReceivers[i]["value"] === "ALL_FANS"){
                    isExist = true;
                }else{
                    validateReceivers.push(wxReceivers[i]);
                }
            }
        }
        _$10(".J_selected_fans").tokenfield("setTokens",validateReceivers);
//        if (isExist) {
//            _$10(".J_selected_fans").tokenfield('setTokens',[]);
//        }
        $(".J_wx_msg_submit").text("发送");
    });


    $(".J_part_fans").click(function(){
      $(".a-click").removeClass("a-click");
      $(this).addClass("a-click");
      $(".J_wxReceiverGroupType").val("PART_FANS");
      var wxReceivers = _$10(".J_selected_fans").tokenfield('getTokens');
      var isExist = false;
      var validateReceivers = [];
      if (wxReceivers && wxReceivers.length > 0) {
        for (var i = 0; i < wxReceivers.length; i++) {
          if(wxReceivers[i]["value"] === "PART_FANS"){
            isExist = true;
          }else{
            validateReceivers.push(wxReceivers[i]);
          }
        }
      }
      _$10(".J_selected_fans").tokenfield("setTokens",validateReceivers);
  //        if (isExist) {
  //            _$10(".J_selected_fans").tokenfield('setTokens',[]);
  //        }
      $(".J_wx_msg_submit").text("提交审核");
    });


//    _$10(".J_selected_fans").tokenfield({allowEditing: false, allowPasting: false, beautify: false, inputType: 'text \" readOnly=\"readOnly'});
    _$10(".J_selected_fans").tokenfield({allowEditing: false, allowPasting: false,inputType: 'text \" readOnly=\"readOnly'});

    $(".J_wxUser_item").live("click", function () {
        var $item = $(this);
        var nickName = $item.attr("nickName");
        var openid = $item.attr("openId");
        var headimgurl = $item.attr("headimgurl");
        var wxReceivers = _$10(".J_selected_fans").tokenfield('getTokens');
        var isExist = false;
        if (wxReceivers && wxReceivers.length > 0) {

            for (var i = 0; i < wxReceivers.length; i++) {
                if (wxReceivers[i]["value"] === openid) {
                    isExist = true;
                }else if(wxReceivers[i]["value"] === "ALL_FANS"){
                    isExist = true;
                }
            }
        }
        if (!isExist) {
            _$10(".J_selected_fans").tokenfield('createToken', {value: openid, label: nickName});
        }
    });

    $(".J_to_select_template").click(function () {

        $(".J_wx_template_container").dialog({
            resizable: true,
            draggable:true,
            title: "微信模板",
            height: 300,
            width: 690,
            modal: true,
            closeOnEscape: false,
            open:function(){
                var url="weChat.do?method=getWXMsgTemplate";
                var data={
                    startPageNo:1,
                    pageSize:10
                };
                APP_BCGOGO.Net.asyncAjax({
                    url:url,
                    type: "POST",
                    cache: false,
                    data:data,
                    dataType: "json",
                    success: function (result) {
                        drawWXArticleTemplate(result);
                        initPage(result,"_wxArticleTemplate",url, null, "drawWXArticleTemplate", '', '',data,null);
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常。");
                    }
                });

            }
        });

    });

    $(".J_use_wx_template").live("click",function(){
        var parentItem = $(this).parents(".J_wxUser_item");
        var title = parentItem.find(".J_wx_template_title").val();
        var description = parentItem.find(".J_wx_template_description").val();
        var picUrl = parentItem.find(".J_wx_template_picUrl").val();
        var idStr = parentItem.find(".J_wx_template_id").val();
        $(".J_wx_title").val(title);
        $(".J_wx_article_picUrl").val(picUrl);
        $(".J_wx_article_img_show").attr("src",picUrl);
        $(".J_wx_description").val(description);
        $(".J_wx_template_id").val(idStr);
        $(".J_wx_template_container").dialog("close");
       _calculateContentLen();
    });

    $(".J_img_select").click(function(){
        $(".J_wxImgFile").click();
    });

    $(".J_wxImgFile").change(function(e){
        onUploadImgChange($(this).get(0));
    });

    $(".J_wx_msg_submit").click(function(){
       var mask=APP_BCGOGO.Module.waitMask;
        mask.login();
        var wxReceivers = _$10(".J_selected_fans").tokenfield('getTokens');
        var validateReceivers = [];
        if (wxReceivers && wxReceivers.length > 0) {
            for (var i = 0; i < wxReceivers.length; i++) {
                validateReceivers.push(wxReceivers[i]["value"]);
            }
        }
        if(G.isEmpty(validateReceivers)){
             mask.open();
            nsDialog.jAlert("请发送要发送的微信用户。");
            return;
        }
        var data = {};
        data["receiverOpenIds"] = validateReceivers;
        var description=$("#w_description").val();
        if(G.isEmpty(description)){
             mask.open();
            nsDialog.jAlert("请填写要发送内容。");
            return;
        }
        var title=$(".J_wx_title").val();
        if(G.isEmpty(title)){
            mask.open();
            nsDialog.jAlert("请填写内容标题。");
            return;
        }
        $(".J_wx_msg_form").ajaxSubmit({
            data:data,
            success:function(data){
                mask.open();
                if(data && data.success){
                    nsDialog.jAlert(data.msg,null,function(){
                        window.location.href='weChat.do?method=toWxSent';
                    });
                }else{
                    nsDialog.jAlert(data.msg);
                }
            },
            error:function(){
                 mask.open();
            }
        })
    });

    $("#w_description").focus(function(){
        $(this).closest(".mes_textarea").addClass("wxContentFocus");
    })
        .focusout(function(){
            $(this).closest(".mes_textarea").removeClass("wxContentFocus");
        });

    $("#w_description").live("keyup blur",function(){
        _calculateContentLen();
    });

    $(".J_wx_title").focus(function(){
        $(".J_wx_title_container").addClass("focus")
    }).focusout(function(){
            $(".J_wx_title_container").removeClass("focus")
        });
});

function _calculateContentLen(){
    var limit=500;
    var len=$("#w_description").val().length;
    $("#contentLength").text(len);
    var left=limit-len;
    $("#leftLength").text(left<0?0:left);
}

function onUploadImgChange(file) {
    var pic = $(".J_wx_article_img_show").get(0);
//    var file = document.getElementById("f");
    var ext=file.value.substring(file.value.lastIndexOf(".")+1).toLowerCase();
    // gif在IE浏览器暂时无法显示
    var lastFileDom = $(".J_wxImgFile_lastFile").get(0);
    if(ext!='png'&&ext!='jpg'&&ext!='jpeg'){
        nsDialog.jAlert("上传文件必须为图片(jpg,pnd,jpeg)！",null,function(){
//            file.click();
        });
        return ;
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
    }else{
        var imgFiles = file.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(imgFiles);
        reader.onload = function(e){
            pic.src=this.result;
        }
    }

    $(".J_wx_article_picUrl").val("");

}

//function html5Reader(file){
//    var file = file.files[0];
//    var reader = new FileReader();
//    reader.readAsDataURL(file);
//    reader.onload = function(e){
//        var pic = document.getElementById("preview");
//        pic.src=this.result;
//    }
//}




function drawWXUserList(wxUserDTOs) {
    if(G.isEmpty(wxUserDTOs)) return;
    $(".j_wxUserContainer li").remove();
    $(".J_total_fans_num").text(wxUserDTOs.length);
    var html = '';
    for (var i = 0; i < wxUserDTOs.length; i++) {
        var wxUserDTO = wxUserDTOs[i];
        var headimgurl = G.Lang.normalize(wxUserDTO.headimgurl) ;
        var nickName = G.Lang.normalize(wxUserDTO.nickname);
        var openid = G.Lang.normalize(wxUserDTO.openid);
        html+='<li class="J_wxUser_item" openId ="'+openid+'" headimgurl = "'+headimgurl+'" nickName= "'+nickName+'">';
        html+='<div class="wechat_personal">';
        html+='<div class="wechat_photo">';
        if(G.Lang.isNotEmpty(headimgurl)){
            html+='<img src="'+headimgurl+'" width="30" height="30" />'
        }
        html+='</div>';
        html+='<div class="wechat_txt">'+nickName+'</div>';
        html+='<div class="clear"></div>';
        html+='</div>';
        html+='</li>';
    }

    $(".j_wxUserContainer").html(html);
}

function drawWXArticleTemplate(data){
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
            html += '<td style="padding-left:10px;">'+(i+1);
            html += '<input type="hidden" class="J_wx_template_title"  value="'+title+'">';
            html += '<input type="hidden" class="J_wx_template_description"  value="'+description+'">';
            html += '<input type="hidden" class="J_wx_template_picUrl"  value="'+picUrl+'">';
            html += '<input type="hidden" class="J_wx_template_id" value="'+idStr+'">';
            html += '</td>';
            html += '<td>'+title+'</td>';
            html += '<td>'+description+'</td>';
            html += '<td><a class="blue_color J_use_wx_template" style="color: #006eca">使用</a></td>';
            html += '</tr>';


        }
    }
    $(".J_wx_template_tb").append(html);
}

