/**
 * 页面初始化
 *
 * @param jsonStr*/


  function initFailedSmsJobTable(jsonStr) {

    jQuery("#table_smsFailedJob tr:not(:first)").remove();
    if (jsonStr.length > 0) {
        for (var i = 0; i < jsonStr.length-1; i++) {
            var id=jsonStr[i].id==null?" " : jsonStr[i].id;
            var shop_name = jsonStr[i].name == null ? " " : jsonStr[i].name;
            var receiveMobile= jsonStr[i].receiveMobile== null ? " " : jsonStr[i].receiveMobile;
            var content = jsonStr[i].content== null ? " " : jsonStr[i].content;
            var status= jsonStr[i].status== null ? " " : jsonStr[i].status;
            var reponseReason= jsonStr[i].reponseReason== null ? " " : jsonStr[i].reponseReason;
            var tr = '<tr>';
            tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
            tr += '<td>' + shop_name + '</td>';
            tr += '<td title=  \''+receiveMobile+'\'>' + receiveMobile.substring(0,25) + '</td>';
            tr += '<td title= \''+content+'\'>' + content.substring(0,25) +'</td>';
            tr += '<td title= \''+status+'\'>' + status +'</td>';
            tr += '<td title= \''+reponseReason+'\'>' + reponseReason.substring(0,20) +'</td>';
            tr += '<td><a class="config_modify" href="#"  onclick="modifyFailedSmsJob(\'' +id+ '\',\'' +receiveMobile+'\',\''+ content + '\')">处理</a></td>';
            tr += '</tr >';
            jQuery("#table_smsFailedJob").append(jQuery(tr));
        }
    }

}
/*
*
 * 查询失败短信
*/


function searchFailedSms(){
    var shop_name = jQuery("#shop_name").val();
    var mobile = jQuery("#mobile").val();
    var content = jQuery("#content").val();
    jQuery.ajax({
        type:"POST",
        url:"sms.do?method=failedSmsList",
        data:{shopName:shop_name,mobile:mobile,content:content,startPageNo:1},
        cache:false,
        dataType:"json",
        success:function(jsonStr){
            initFailedSmsJobTable(jsonStr);
             initfenye(jsonStr, "dynamical1", "sms.do?method=failedSmsList", '', "initFailedSmsJobTable", '', '',
                    {startPageNo:1,shopName:shop_name,mobile:mobile,content:content}, '');
        }
    });
}
/**
 * 短信发送失败修改 重新发送弹出框
       *
       * @param type
       * @param content
       * @param shop_id*/


      function modifyFailedSmsJob(id,receiveMobile,content){
          Mask.Login();
          jQuery("#setLocation").css("display", "block");
          jQuery("#id").val(id);
          jQuery("#receiveMobile").val(receiveMobile);
          jQuery("#smsContent").val(content);
      }


/**
     * 弹出框操作 关闭，遮罩关闭操作
     **/


    jQuery(function() {
        jQuery("#div_show").draggable();      //弹出框能够drag
        jQuery("#div_close,#cancleBtn").click(function() {
            jQuery("#mask").css("display", "none");
            jQuery("#setLocation").css("display", "none");
        });

/**
         * 短信发送时验证敏感词
         **/


    jQuery("#confirmBtn").click(function() {
        jQuery('#sendFailedMessage').ajaxSubmit(function (jsonStr) {
            var result=JSON.parse(jsonStr);
            if (result.message == "success") {
                alert("发送成功！");
                jQuery("#mask").css("display", "none");
                jQuery("#setLocation").css("display", "none");
                window.location.reload();
            } else {
                alert('存在敏感词：【' + result.message + '】请修改后重新发送');
            }
        });
    });
});