
$(function (){
  $("#cameraConfigListContent").datagrid({
    rownumbers: false,
    singleSelect: true,
    fitColumns: true,//适应列宽
    striped: true,//条纹
    nowrap: false,//截取当数据长度超出列宽时将会自动截取
    loadMsg: "数据加载中",//载入时信息
    url:'cameraRecord.do?method=initCameraConfigList',
    method: 'get',
    pagination: true,
    pageNumber: 1,
    pageSize: 10,
    pageList: [5, 10, 20, 30],
    toolbar: '#tb',
    columns:[[
      {field:'ck',checkbox:false,hidden:true,align: 'center'},
      {field:'id',title:'id',hidden:true,align: 'center'},
      {field:'camera_id',title:'camera_id',hidden:true,align: 'center'},
      {field:'serial_no',title:'识别仪序列号',width:35,align: 'center'},
      {field:'interval_time_warn',title:'同一辆车到访时间间隔（分钟）',width:45,align: 'center',align: 'center'},
      {field:'white_vehicle_nos',title:'白名单',width:60,align: 'center',align: 'center'},
      {field:'member_card',title:'是否自动扣会员卡',width:30,align: 'center',align: 'center',
        formatter:function(value,row,index){
          if(value=="YES"){
            return "是";
          }else{
            return "否";
          }
        }
      },
      {field:'construction_project_text',title:'施工项目',width:50,align: 'center',align: 'center'}
    ]
    ] ,
    onClickRow:function(index,data){
      var editId = data["id"];
      var editCamera_id = data["camera_id"];
      var editSerial_no = data["serial_no"];
      var editInterval_time_warn =  data["interval_time_warn"];
      var editWhite_vehicle_nos =  data["white_vehicle_nos"];
      var editMember_card =  data["member_card"];
      var editOrder_type =  data["order_type"];
      var editConstruction_project_text =  data["construction_project_text"];
      var editConstruction_project_value =  data["construction_project_value"];

      $("#editId").val(editId);
      $("#editCamera_id").val(editCamera_id);
      $("#editInterval_time_warn").textbox("setValue",editInterval_time_warn);
      $("#editWhite_vehicle_nos").textbox("setValue",editWhite_vehicle_nos);
      if(editMember_card=="YES"){
        $("#editMember_card").combobox("setText","是");
      }else{
        $("#editMember_card").combobox("setText","否");
      }
      $("#editMember_card").combobox("setValue",editMember_card);
      if(editOrder_type=="YES"){
        $("#editOrder_type").combobox("setText","是");
      }else{
        $("#editOrder_type").combobox("setText","否");
      }
      $("#editOrder_type").combobox("setValue",editOrder_type);

//      $("#editConstruction_project").combobox("setValue",editConstruction_project_value);
      $("#editConstruction_project_ex").val(editConstruction_project_value);

      $("#editConstruction_project").combobox("setText",editConstruction_project_text);
    }
  });
  $("#editConstruction_project").combobox({
    url:'cameraRecord.do?method=setCategoryPage',
    method:'post',
    valueField:'id',
    textField:'name',
    mode:'remote',
    multiple:true

  });
});



function updateCameraConfig(){
  var id = $("#editId").val();
  var camera_id = $("#editCamera_id").val();
  var order_type = $("#editOrder_type").combobox('getValue');
  var member_card = $("#editMember_card").combobox('getValue');
  var interval_time_warn = $("#editInterval_time_warn").val();
  var white_vehicle_nos = $("#editWhite_vehicle_nos").val();
  var construction_projects_value = $("#editConstruction_project").combobox("getValues");
  var construction_projects_valueArr = "";
  if(construction_projects_value && construction_projects_value.length>0){
    var i=0;
    for(i;i<construction_projects_value.length;i++){
      construction_projects_valueArr += construction_projects_value[i];
      construction_projects_valueArr += ",";
    }
  }else{
    construction_projects_valueArr = $("#editConstruction_project_ex").val();
  }

  var construction_projects_text = $("#editConstruction_project").combobox("getText");

  if(id ==""){
    alert("请选择识别仪再进行配置！");
       return false;
  }

  if(interval_time_warn ==""){
    alert("请输入同一辆车到访间隔时间！");
    return false;
  }

  if(construction_projects_text ==""){
    alert("请选择施工项目！");
    return false;
  }



  $.messager.confirm('Confirm','确定提交?',function(r){
    if (r){
      $.ajax({
        url:'cameraRecord.do?method=updateCameraConfigs',
        type:'POST',
        cache:false,
        data:{id:id,camera_id:camera_id,order_type:order_type,member_card:member_card,interval_time_warn:interval_time_warn,construction_project_value:construction_projects_valueArr,
          construction_project_text:construction_projects_text,white_vehicle_nos:white_vehicle_nos},
        dataType:'json',
        success:function(data){
          $("#cameraConfigListContent").datagrid("reload");
          $.messager.alert('success','保存成功！');
        },
        error:function(data){
          $("#cameraConfigListContent").datagrid("reload");
          $.messager.alert('success','保存成功！');}
      })
    }});
}

