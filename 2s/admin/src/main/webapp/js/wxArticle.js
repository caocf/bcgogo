/**
 * 微信素材页面初始化
 *
 * @param jsonStr
 */
function inintTable_WXJob(jsonStr) {
    jQuery("#table_WXJob tr:not(:first)").remove();
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length-1; i++) {
            var idStr=jsonStr[i].idStr==null?" " : jsonStr[i].idStr;
            var titleT = jsonStr[i].title == null ? " " : jsonStr[i].title;
            var picUrl= jsonStr[i].picUrl== null ? " " : jsonStr[i].picUrl;
            var description = jsonStr[i].description== null ? " " : jsonStr[i].description;
            var tr = '<tr>';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td>' + titleT + '</td>';
            tr += '<td title=  \''+picUrl+'\'>' + picUrl.substring(0,25) + '</td>';
            tr += '<td title= \''+description+'\'>' + description.substring(0,25) +'</td>';
            tr += '<td><a class="config_modify" href="#" onclick="findImage(\'' +picUrl+'\')">查看图片</a>&nbsp&nbsp<a class="config_modify" href="#"  onclick="toUploadWeChat(\'' +idStr+'\')">上传图片</a>&nbsp&nbsp<a class="config_modify" href="#"  onclick="modifyWeChat(\'' +idStr+'\')">修改</a>&nbsp&nbsp<a class="config_modify" href="#"  onclick="deleteWeChat(\'' +idStr+'\')">删除</a></td>';
            tr += '</tr >';
            jQuery("#table_WXJob").append(jQuery(tr));
        }
    }

}
/**
 * 查询微信素材
 */
function searchWeChat(){
    var titleT = jQuery("#title").val();
    var description = jQuery("#description").val();
    jQuery.ajax({
        type:"POST",
        url:"weChat.do?method=weChatList",
        data:{title:titleT,description:description,startPageNo:1},
        cache:false,
        dataType:"json",
        success:function(jsonStr){
            inintTable_WXJob(jsonStr);
            initfenye(jsonStr, "dynamical1", "weChat.do?method=weChatList", '', "inintTable_WXJob", '', '',
                {startPageNo:1,title:titleT,description:description}, '');
        }
    });
}

//删除
function deleteWeChat(id){
    if (confirm("确认删除?")) {
        window.location.href="weChat.do?method=deleteWeChat&id="+id;
    }
}

//跳转到修改界面
function modifyWeChat(id){
    window.location.href="weChat.do?method=toModifyWeChat&id="+id;
}


//跳转到修改界面
function toModifyAdult(id){
    window.location.href="weChat.do?method=toModifyAudit&id="+id;
}

//跳转到上传图片界面
function toUploadWeChat(id){
    window.location.href="weChat.do?method=toUploadWeChat&id="+id;
}


function findImage(picUrl){
    var str = picUrl;
    window.open(str,'newwindow',
        'height=290px,width=400px,top=300,left=500,toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no,alwaysRaised=yes,titlebar=no');
}
//弹窗（未使用）
//function toUpload(){
//  var str = "weChat.do?method=toPublicTemplateUpload";
//  window.open(str,'newwindow',
//      'height=290px,width=400px,top=300,left=500,toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no,alwaysRaised=yes,titlebar=no');
//}

/**
 * 待审核页面初始化
 *
 * @param jsonStr
 */
function inintTable_AdultJob(jsonStr) {
    jQuery("#table_AdultJob tr:not(:first)").remove();
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length-1; i++) {
            var idStr=jsonStr[i].idStr==null?" " : jsonStr[i].idStr;
            var titleT = jsonStr[i].title == null ? " " : jsonStr[i].title;
            var picUrl= jsonStr[i].picUrl== null ? " " : jsonStr[i].picUrl;
            var description = jsonStr[i].description== null ? " " : jsonStr[i].description;
            var shopName=G.normalize(jsonStr[i].fromShopName);
            var submitReviewTimeStr=G.normalize(jsonStr[i].submitReviewTimeStr);
            var tr = '<tr>';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td>' + submitReviewTimeStr + '</td>';
            tr += '<td>' + shopName + '</td>';
            tr += '<td>' + titleT + '</td>';
            tr += '<td title= \''+description+'\'>' + description.substring(0,25) +'</td>';
            tr += '<td><a class="config_modify" href="#" onclick="findImage(\'' +picUrl+'\')">查看图片</a>' +
                '&nbsp&nbsp<a class="config_modify" href="#"  onclick="toFindAdult(\'' +idStr+'\')">查看</a>' +
                '&nbsp&nbsp<a class="config_modify" href="#"  onclick="toModifyAdult(\'' +idStr+'\')">修改</a></td>';
//      tr += '<td><a class="config_modify" href="#"  onclick="toUploadWeChat(\'' +titleT+ '\',\'' +idStr+'\',\''+ description + '\')">上传图片</a>&nbsp&nbsp<a class="config_modify" href="#"  onclick="modifyWeChat(\'' +titleT+ '\',\'' +idStr+'\',\''+ description + '\')">修改</a>&nbsp&nbsp<a class="config_modify" href="#"  onclick="deleteWeChat(\'' +titleT+ '\',\'' +idStr+'\',\''+ description + '\')">删除</a></td>';
            tr += '</tr >';
            jQuery("#table_AdultJob").append(jQuery(tr));
        }
    }

}
/**
 * 查询待审核列表
 */
function searchAdult(){
    var titleT = jQuery("#title").val();
    var description = jQuery("#description").val();
    jQuery.ajax({
        type:"POST",
        url:"weChat.do?method=initAudit",
        data:{title:titleT,description:description,startPageNo:1},
        cache:false,
        dataType:"json",
        success:function(jsonStr){
            inintTable_AdultJob(jsonStr);
            initfenye(jsonStr, "dynamical1", "weChat.do?method=listAdult", '', "inintTable_AdultJob", '', '',
                {startPageNo:1,title:titleT,description:description}, '');
        }
    });
}

//跳转到查看待审核界面
function toFindAdult(id){
    window.location.href="weChat.do?method=toFindAdult&id="+id;
}

$(".pic").change(function(e){
    onUploadImgChange($(this).get(0));
});

//显示图片
function onUploadImgChange(file) {
    var pic = $(".J_wx_article_img_show").get(0);
//    var file = document.getElementById("f");
    var ext=file.value.substring(file.value.lastIndexOf(".")+1).toLowerCase();
    // gif在IE浏览器暂时无法显示
    var lastFileDom = $(".J_wxImgFile_lastFile").get(0);
    if(ext!='png'&&ext!='jpg'&&ext!='jpeg'){
        nsDialog.jAlert("上传文件必须为图片(jpg,png,jpeg)！",null,function(){
//            file.click();
        });
        return ;
    }
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

//    $(".J_wx_article_picUrl").val("");

}
