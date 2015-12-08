
$(function (){
    $("#cameraList").datagrid({
        rownumbers:true,
        singleSelect:true,
        fitColumns:true,//适应列宽
        striped:true,//条纹
        nowrap:false,//截取当数据长度超出列宽时将会自动截取
        loadMsg:"数据加载中",//载入时信息
        url:'camera.do?method=cameraList',
        method:'get',
        pagination:true,
        pageNumber:1,
        pageSize:5,
        pageList:[5,10,15,20,30],
        toolbar: '#tb',
        columns:[[
            {field:'ck',checkbox:true},
            {field:'id',title:'id',hidden:true},
            {field:'name',title:'绑定店铺名',width:50},
            {field:'serial_no',title:'摄像头序列号',width:50},
            {field:'last_heart_date',title:'心跳时间',width:50},
            {field:'lan_ip',title:'局域网ip',width:50,hidden:true},
            {field:'lan_port',title:'局域网端口',width:50,hidden:true},
            {field:'username',title:'用户名',width:30,hidden:true},
            {field:'password',title:'密码',width:25,hidden:true},
            {field:'external_address',title:'外网地址',width:65},
            {field:'domain_username',title:'域名网站用户名',width:50,hidden:true},
            {field:'domain_password',title:'域名网站密码',width:50,hidden:true},
            {field:'status',title:'绑定状态',width:40},
//            {field:'white_vehicle_nos',title:'白名单',width:50,hidden:true},
            {field:'install_date',title:'安装日期',width:50,hidden:true},
            {field:'remark',title:'备注',width:50},
            {field:'camera_shop_id',title:'shopId',width:50,hidden:true}
            ]
        ],
      singleSelect: false,
      selectOnCheck: true,
      checkOnSelect: true,
      onLoadSuccess:function(data){
    if(data){
     $.each(data.rows, function(index, item){
       if(item.checked){
         $('#cameraList').datagrid('checkRow', index);
          }
       });
     }
    },
  onClickRow:function(index,data){
//            $("#name").combobox("setText",data["name"]);
            var editId = data["id"];
            var shopSelect = data["name"];
            var editSerial_no = data["serial_no"];
            var editLast_heart_date =  data["last_heart_date"];
            var editLan_ip =  data["lan_ip"];
            var editLan_port =  data["lan_port"];
            var editUsername =  data["username"];
            var editPassword =  data["password"];
            var editExternal_address =  data["external_address"];
            var editDomain_username =  data["domain_username"];
            var editDomain_password =  data["domain_password"];
            var editStatus = data["status"];
//            var editWhite_vehicle_nos = data["white_vehicle_nos"];
            var editInstall_date = data["install_date"];
            var editRemark = data["remark"];
            var editCamera_shop_id = data["camera_shop_id"];

            $("#editStatus").val(editStatus);
            $("#editLast_heart_date").val(editLast_heart_date);
            $("#editId").val(editId);
            $("#shopSelect").textbox("setValue",shopSelect);
            $("#editSerial_no").textbox("setValue",editSerial_no);
            $("#editLan_ip").textbox("setValue",editLan_ip);
            $("#editLan_port").textbox("setValue",editLan_port);
            $("#editUsername").textbox("setValue",editUsername);
            $("#editPassword").textbox("setValue",editPassword);
            $("#editExternal_address").textbox("setValue",editExternal_address);
            $("#editDomain_username").textbox("setValue",editDomain_username);
            $("#editDomain_password").textbox("setValue",editDomain_password);
//            $("#editWhite_vehicle_nos").textbox("setValue",editWhite_vehicle_nos);

//            $("#editInstall_date").textbox("setValue",editInstall_date);
            $("#editInstall_date").datetimebox("setValue",editInstall_date);


            $("#editRemark").textbox("setValue",editRemark);
            $("#editCamera_shop_id").val(editCamera_shop_id);
            $("#shopSelect").combobox("setText",shopSelect);
            $("#editStatus").combobox("setValue",editStatus);
//            $("#birthday").datetimebox("setText", "2014-12-24");
        }
    });

    $("#shopSelect").combobox({
        url:'print.do?method=getShopNameByName',
        method:'post',
        valueField:'id',
        textField:'name',
        mode:'remote',
        queryParam:'name',
        onSelect:function(record){
            $("#editCamera_shop_id").val(record.id);
        }
    });


  $("#shopSelects").combobox({
    url:'print.do?method=getShopNameByName',
    method:'post',
    valueField:'id',
    textField:'name',
    mode:'remote',
    queryParam:'name',
    onSelect:function(record){
      $("#editCamera_shop_id").val(record.id);
    }
  });

  $("#searchBtn").click(function () {
    var $cameraListDom = $('#cameraList');
    var queryParams = $cameraListDom.datagrid('options').queryParams;
    var shopId = $("#shopSelects").combobox("getValue");

    queryParams.shopId = shopId;
    $cameraListDom.datagrid('options').pageNumber = 1;
    var p = $cameraListDom.datagrid('getPager');
    if(p){
      p.pagination({
        pageNumber:1
      });
    }
    //重新加载datagrid的数据
    $("#cameraList").datagrid('reload');
  });

//  $(document).ready(function(){
//      $("#dd").datebox({
//        required:true,
//        onSelect: function(date){
//          $("#dd").val(date);
//        }
//  });
//  });





  $("#saveBtn").click(function(){
//        var $thisDom = $(this);
//        if($thisDom.attr("lock") == "true"){
//            return false;
//        }else{
//            $thisDom.attr("lock","true");
//        }

        var id = $("#editId").val();
        var serial_no = $("#editSerial_no").val();
//        var last_heart_date = $("#editLast_heart_date").val();
        var lan_ip = $("#editLan_ip").val();
        var lan_port = $("#editLan_port").val();
        var username = $("#editUsername").val();
        var password = $("#editPassword").val();
        var external_address = $("#editExternal_address").val();
        var domain_username = $("#editDomain_username").val();
        var domain_password = $("#editDomain_password").val();
//        var status = $("#editStatus").val();
//        var white_vehicle_nos = $("#editWhite_vehicle_nos").val();
        var install_date = $('#editInstall_date').datetimebox("getValue");
        var remark = $("#editRemark").val();
        var camera_shop_id = $("#editCamera_shop_id").val();

        if(install_date!=""&&camera_shop_id==""){
          jQuery.messager.alert('提示:','绑定店铺名不能为空');
          return false;
        }
        if(serial_no==""){
          jQuery.messager.alert('提示:','请输入摄像头序列号');
          return false;
        }

        var validate = true;
        var validateMsg = "";

        if(!validate){
            $.messager.alert('Warning',validateMsg);
            $thisDom.removeAttr("lock");
            return false;
        }
        $.messager.confirm('Confirm','确定提交?',function(r){
            if (r){
                $.ajax({
                    url:'camera.do?method=saveOrUpdateCameraRef',
                    type:'POST',
                    cache:false,
                    data:{id:id,serial_no:serial_no,lan_ip:lan_ip,lan_port:lan_port,username:username,
                      password:password,external_address:external_address,domain_username:domain_username,domain_password:domain_password,
                      install_date:install_date,remark:remark,camera_shop_id:camera_shop_id},
                    dataType:'json',
                    success:function(data){
                        $("#cameraList").datagrid("reload");
                        $.messager.alert('提示：',data.msg);
                        $thisDom.removeAttr("lock");
                    },
                    error:function(data){
                        $("#cameraList").datagrid("reload");
                        $.messager.alert('提示：',data.msg);
                        $thisDom.removeAttr("lock");
                    }
                })
            }else{
                $thisDom.removeAttr("lock");
            }});


    });
});


function findCamera(){
  var editCamera_shop_id = $("#editCamera_shop_id").val();
  $("#cameraList").datagrid({
    rownumbers:true,
    singleSelect:true,
    fitColumns:true,//适应列宽
    striped:true,//条纹
    nowrap:false,//截取当数据长度超出列宽时将会自动截取
    loadMsg:"数据加载中",//载入时信息
    url:'camera.do?method=cameraList&&editCamera_shop_id='+editCamera_shop_id,
    method:'get',
    pagination:true,
    pageNumber:1,
    pageSize:5,
    pageList:[5,10,15,20,30],
    toolbar: '#tb',
//        data:{editCamera_shop_id:editCamera_shop_id},
//        dataType:'json',
    columns:[[
      {field:'ck',checkbox:true},
      {field:'id',title:'id',hidden:true},
      {field:'name',title:'绑定店铺名',width:50},
      {field:'serial_no',title:'摄像头序列号',width:50},
      {field:'last_heart_date',title:'心跳时间',width:50},
      {field:'lan_ip',title:'局域网ip',width:50,hidden:true},
      {field:'lan_port',title:'局域网端口',width:50,hidden:true},
      {field:'username',title:'用户名',width:30,hidden:true},
      {field:'password',title:'密码',width:25,hidden:true},
      {field:'external_address',title:'外网地址',width:65},
      {field:'domain_username',title:'域名网站用户名',width:50,hidden:true},
      {field:'domain_password',title:'域名网站密码',width:50,hidden:true},
      {field:'status',title:'绑定状态',width:40},
//            {field:'white_vehicle_nos',title:'白名单',width:50,hidden:true},
      {field:'install_date',title:'安装日期',width:50,hidden:true},
      {field:'remark',title:'备注',width:50},
      {field:'camera_shop_id',title:'shopId',width:50,hidden:true}
    ]
    ],
    singleSelect: false,
    selectOnCheck: true,
    checkOnSelect: true,
    onLoadSuccess:function(data){
      if(data){
        $.each(data.rows, function(index, item){
          if(item.checked){
            $('#cameraList').datagrid('checkRow', index);
          }
        });
      }
    },
    onClickRow:function(index,data){
//            $("#name").combobox("setText",data["name"]);
      var editId = data["id"];
      var shopSelect = data["name"];
      var editSerial_no = data["serial_no"];
      var editLast_heart_date =  data["last_heart_date"];
      var editLan_ip =  data["lan_ip"];
      var editLan_port =  data["lan_port"];
      var editUsername =  data["username"];
      var editPassword =  data["password"];
      var editExternal_address =  data["external_address"];
      var editDomain_username =  data["domain_username"];
      var editDomain_password =  data["domain_password"];
      var editStatus = data["status"];
//            var editWhite_vehicle_nos = data["white_vehicle_nos"];
      var editInstall_date = data["install_date"];
      var editRemark = data["remark"];
      var editCamera_shop_id = data["camera_shop_id"];

      $("#editStatus").val(editStatus);
      $("#editLast_heart_date").val(editLast_heart_date);
      $("#editId").val(editId);
      $("#shopSelect").textbox("setValue",shopSelect);
      $("#editSerial_no").textbox("setValue",editSerial_no);
      $("#editLan_ip").textbox("setValue",editLan_ip);
      $("#editLan_port").textbox("setValue",editLan_port);
      $("#editUsername").textbox("setValue",editUsername);
      $("#editPassword").textbox("setValue",editPassword);
      $("#editExternal_address").textbox("setValue",editExternal_address);
      $("#editDomain_username").textbox("setValue",editDomain_username);
      $("#editDomain_password").textbox("setValue",editDomain_password);
//            $("#editWhite_vehicle_nos").textbox("setValue",editWhite_vehicle_nos);

//            $("#editInstall_date").textbox("setValue",editInstall_date);
      $("#editInstall_date").datetimebox("setValue",editInstall_date);


      $("#editRemark").textbox("setValue",editRemark);
      $("#editCamera_shop_id").val(editCamera_shop_id);
      $("#shopSelect").combobox("setText",shopSelect);
      $("#editStatus").combobox("setValue",editStatus);
//            $("#birthday").datetimebox("setText", "2014-12-24");
    }
  });
//  window.location.href="camera.do?method=initCameraList";
//  var editContion_shopId = $("#editCamera_shop_id").val();
//  $.ajax({
//    url:'camera.do?method=cameraList',
//    type:'POST',
//    cache:false,
//    data:{editContion_shopId:editContion_shopId},
//    dataType:'json',
//    success:function(data){
//      $("#cameraList").datagrid("reload");
//    }
//  })
}

function add(){
  $("#editId").val("");
  $("#editCamera_shop_id").val("");
  $("#editSerial_no").textbox("setValue","");
//  $("#editLast_heart_date").textbox("setValue","新增无需填写！");
//  $("#editStatus").textbox("setValue","新增无需填写！");
  $("#editLan_ip").textbox("setValue","");
  $("#editLan_port").textbox("setValue","");
  $("#editUsername").textbox("setValue","");
  $("#editPassword").textbox("setValue","");
  $("#editRemark").textbox("setValue","");
  $("#editExternal_address").textbox("setValue","");
  $("#editDomain_username").textbox("setValue","");
  $("#editDomain_password").textbox("setValue","");
  $("#shopSelect").textbox("setValue","");
  $("#editInstall_date").textbox("setValue","");
//  var ui =document.getElementById("last_heart");
//  ui.style.display="none";
//  var ui =document.getElementById("statu");
//  ui.style.display="none";

}

function del(){
  var checkedItems = $('#cameraList').datagrid('getChecked');
  var ids = "";
  var i = 0;
  $.each(checkedItems, function(index, item){
    ids+=item.id+",";
    i++
  });
  if(i==0){
    jQuery.messager.alert('提示:','请勾选至少“一条”记录解绑');
    return false;
  }
  $.messager.confirm('Confirm','确定解绑?',function(r){
    if (r){
      $.ajax({
      url:'camera.do?method=unBandShop',
        type:'POST',
        cache:false,
        data:{ids:ids},
        dataType:'json',
        success:function(data){
          $("#cameraList").datagrid("reload");
          $.messager.alert('success','解绑成功！');

        },
        error:function(data){
          $("#cameraList").datagrid("reload");
          $.messager.alert('error','解绑失败！');

        }
      })
    }else{
    } })
}

function findCameraRecord(){
//  $("#record_camera").show();
//  $("#record_camera").window({
//    title:"内部车辆行驶记录",
//    width:866,
//    height:428,
//    top:50,
//    left:274,
////                modal:true,
//    closable:true,
//    collapsible:false,
//    minimizable:false,
//    maximizable:false,
//    draggable:false,
//    resizable:false
//  });
  var checkedItems = $('#cameraList').datagrid('getChecked');
  var i = 0;
  var ids = "";
  $.each(checkedItems, function(index, item){
    ids+=item.id;
    i++;
  });
  if(i>1||i==0){
    jQuery.messager.alert('提示:','请勾选“一条”记录查看');
    return false;
  }
//  window.location.href="camera.do?method=toCameraRecordList&id="+ids;
//  var id = $("#editId").val();
//  $("#cameraRecordList").datagrid({
//    rownumbers:true,
//    singleSelect:true,
//    fitColumns:true,//适应列宽
//    striped:true,//条纹
//    nowrap:false,//截取当数据长度超出列宽时将会自动截取
//    loadMsg:"数据加载中",//载入时信息
//    url:'camera.do?method=cameraRecordList&&id='+ids,
//    method:'get',
//    pagination:true,
//    pageNumber:1,
//    pageSize:5,
//    pageList:[5,10,15,20,30],
//    toolbar: '#tb',
//    columns:[[
//      {field:'ck',checkbox:false,hidden:true},
//      {field:'id',title:'id',hidden:true},
//      {field:'camera_id',title:'camera_id',hidden:true},
//      {field:'name',title:'所在店铺',width:50},
//      {field:'vehicle_no',title:'车牌号',width:50},
//      {field:'arrive_date',title:'到店时间',width:50},
//      {field:'ref_order_type',title:'关联记录',width:50},
//      {field:'order_id',title:'单据id',width:50}
//    ]
//    ]
//  });
  window.location.href="camera.do?method=toCameraRecordList&id="+ids;
}

function findCameraConfig(){
  var checkedItems = $('#cameraList').datagrid('getChecked');
  var i = 0;
  var ids = "";
  $.each(checkedItems, function(index, item){
    ids+=item.id;
    i++;
  });
  if(i>1||i==0){
    jQuery.messager.alert('提示:','请勾选“一条”记录查看配置');
    return false;
  }
//  window.location.href="camera.do?method=toCameraRecordList&id="+ids;
  var id = $("#editId").val();
  window.location.href="camera.do?method=toCameraConfig&id="+ids;
}


function myformatter(date) {
  var y = date.getFullYear();
  var m = date.getMonth() + 1;
  var d = date.getDate();
  return y + '-' + (m < 10 ? ('0' + m) : m) + '-'
      + (d < 10 ? ('0' + d) : d);
}

function myparser(s) {
  if (!s)
    return new Date();
  var ss = (s.split('-'));
  var y = parseInt(ss[0], 10);
  var m = parseInt(ss[1], 10);
  var d = parseInt(ss[2], 10);
  if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
    return new Date(y, m - 1, d);
  } else {
    return new Date();
  }
}


function updateCameraConfig(){
  var id = $("#id").val();

  var camera_id = $("#camera_id").val();
  var order_type = $("#order_type").combobox("getValue");
  var member_card =$("#member_card").combobox("getValue");
  var interval_time_warn = $("#interval_time_warn").val();
  var white_vehicle_nos = $("#white_vehicle_nos").val();
  var printer_serial_no = $("#printer_serial_no").val();
//  var construction_projects = $("#editConstruction_project").combobox("getValues");
//  var construction_projectArr = "";
//  if(construction_projects && construction_projects.length>0){
//    var i=0;
//    for(i;i<construction_projects.length;i++){
//      construction_projectArr += construction_projects[i];     construction_project:construction_projectArr,
//      construction_projectArr += ",";
//    }
//  }

  $.messager.confirm('Confirm','确定提交?',function(r){
    if (r){
      $.ajax({
        url:'camera.do?method=updateCameraConfig',
        type:'POST',
        cache:false,
        data:{
            id:id,camera_id:camera_id,order_type:order_type,member_card:member_card,interval_time_warn:interval_time_warn,
          white_vehicle_nos:white_vehicle_nos,
            printer_serial_no:printer_serial_no
        },
        dataType:'json',
        success:function(data){
          $.messager.alert('success','保存成功！');
        },
        error:function(data){
          $.messager.alert('error','保存失败！');}
      })
    }});
}

function backList(){
  window.location.href="camera.do?method=initCameraList";
}