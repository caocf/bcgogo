/**
 * 微信用户页面初始化
 *
 * @param jsonStr
 */
  function inintTable_WXUserJob(jsonStr) {

    jQuery("#table_WXUserJob tr:not(:first)").remove();
  if (jsonStr.length > 0) {
    for (var i = 0; i < jsonStr.length-1; i++) {
      var idStr=jsonStr[i].idStr==null?" " : jsonStr[i].idStr;
      var nickName = jsonStr[i].nickname == null ? " " : jsonStr[i].nickname;
      var publicNo= jsonStr[i].publicNo== null ? " " : jsonStr[i].publicNo;
      var headimgurl = jsonStr[i].headimgurl== null ? " " : jsonStr[i].headimgurl;
      var openid = jsonStr[i].openid== null ? " " : jsonStr[i].openid;
      var publicName = jsonStr[i].publicName== null ? " " : jsonStr[i].publicName;
      var tr = '<tr>';
      tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
//      tr += '<td>' + 11111 + '</td>';
      tr += '<td title=  \''+nickName+'\'>' + nickName.substring(0,25) + '</td>';
      tr += '<td title= \''+publicNo+'\'>' + publicNo.substring(0,25) +'</td>';
      tr += '<td title= \''+publicName+'\'>' + publicName.substring(0,25) +'</td>';
      tr += '<td><a class="config_modify" href="#" onclick="findVehicleList(\'' +openid+'\')">绑定车辆</a>&nbsp&nbsp<a class="config_modify" href="#"  onclick="findUserImage(\'' +headimgurl+'\')">头像</a></td>';
      tr += '</tr >';
      jQuery("#table_WXUserJob").append(jQuery(tr));
    }
  }

}
/**
 * 查询微信用户
 */
function searchWXUser(){
    var nickName = jQuery("#nickName").val();
    var publicNo = jQuery("#publicNo").val();
    jQuery.ajax({
        type:"POST",
        url:"weChat.do?method=wxUserList",
        data:{nickName:nickName,publicNo:publicNo,startPageNo:1},
        cache:false,
        dataType:"json",
        success:function(jsonStr){
          inintTable_WXUserJob(jsonStr);
             initfenye(jsonStr, "dynamical1", "weChat.do?method=wxUserList", '', "table_WXUserJob", '', '',
                    {startPageNo:1,nickName:nickName,publicNo:publicNo}, '');
        }
    });
}

//删除
//function deleteWeChat(id){
//  if (confirm("确认删除?")) {
//    window.location.href="weChat.do?method=deleteWeChat&id="+id;
//  }
//}

//跳转到用户绑定车辆列表界面
function findVehicleList(openId){
  window.location.href="weChat.do?method=initWxUserVehicleList&openId="+openId;
}


//跳转到修改界面
//function toModifyAdult(id){
//  window.location.href="weChat.do?method=toModifyAudit&id="+id;
//}

//跳转到上传图片界面
//function toUploadWeChat(id){
//  window.location.href="weChat.do?method=toUploadWeChat&id="+id;
//}


function findUserImage(picUrl){
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
 * 绑定车辆页面初始化
 *
 * @param jsonStr
 */
function inintTable_WXUserVehicleJob(jsonStr) {
  jQuery("#table_WXUserVehicleJob tr:not(:first)").remove();
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
      tr += '<td><a class="config_modify" href="#" onclick="findImage(\'' +picUrl+'\')">查看图片</a>' +
          '&nbsp&nbsp<a class="config_modify" href="#"  onclick="toFindAdult(\'' +idStr+'\')">查看</a>' +
          '&nbsp&nbsp<a class="config_modify" href="#"  onclick="toModifyAdult(\'' +idStr+'\')">修改</a></td>';
//      tr += '<td><a class="config_modify" href="#"  onclick="toUploadWeChat(\'' +titleT+ '\',\'' +idStr+'\',\''+ description + '\')">上传图片</a>&nbsp&nbsp<a class="config_modify" href="#"  onclick="modifyWeChat(\'' +titleT+ '\',\'' +idStr+'\',\''+ description + '\')">修改</a>&nbsp&nbsp<a class="config_modify" href="#"  onclick="deleteWeChat(\'' +titleT+ '\',\'' +idStr+'\',\''+ description + '\')">删除</a></td>';
      tr += '</tr >';
      jQuery("#table_WXUserVehicleJob").append(jQuery(tr));
    }
  }

}
/**
 * 查询待审核列表
 */
//function searchAdult(){
//  var titleT = jQuery("#title").val();
//  var description = jQuery("#description").val();
//  jQuery.ajax({
//    type:"POST",
//    url:"weChat.do?method=initAudit",
//    data:{title:titleT,description:description,startPageNo:1},
//    cache:false,
//    dataType:"json",
//    success:function(jsonStr){
//      inintTable_AdultJob(jsonStr);
//      initfenye(jsonStr, "dynamical1", "weChat.do?method=listAdult", '', "inintTable_AdultJob", '', '',
//          {startPageNo:1,title:titleT,description:description}, '');
//    }
//  });
//}

function addUserVehicle(){
  var openId =  $("#openId").val();
  window.location.href="weChat.do?method=toAddUserVehicle&openId="+openId;
}

function modifyVehicle(id){
  window.location.href="weChat.do?method=toModifyVehicle&id="+id;
}

function deleteVehicle(id){
  if (confirm("确认删除?")) {
    window.location.href="weChat.do?method=deleteVehicle&id="+id;
  }
}

function backToWXUser(){
  window.location.href="weChat.do?method=initWxUser"
}






