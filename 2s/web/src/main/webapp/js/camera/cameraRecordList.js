
$(function (){
  $("#startDate").datebox("setValue", dateUtil.getNDayCloseToday(-30));
  $("#endDate").datebox("setValue", dateUtil.getToday());
  $("#cameraRecordListContent").datagrid({
    rownumbers: true,
    singleSelect: true,
    fitColumns: true,//适应列宽
    striped: true,//条纹
    nowrap: false,//截取当数据长度超出列宽时将会自动截取
    loadMsg: "数据加载中",//载入时信息
    queryParams:{
//                    vehicleNos: vehicleNo,
      startDateStr: $("#startDate").datebox("getValue"),
      endDateStr: $("#endDate").datebox("getValue")
    },
    url:'cameraRecord.do?method=initCameraRecordList',
    method: 'get',
    pagination: true,
    pageNumber: 1,
    pageSize: 10,
    pageList: [5, 10, 20, 30],
    toolbar: '#tb',
    columns:[[
      {field:'ck',checkbox:false,hidden:true},
      {field:'id',title:'id',hidden:true},
      {field:'camera_id',title:'camera_id',hidden:true},
      {field:'name',title:'所在店铺',width:50,hidden:true,align: 'center'},
      {field:'vehicle_no',title:'车牌号',width:50,align: 'center'},
      {field:'arrive_date',title:'到店时间',sortable:true,width:50,align: 'center'

      },
      {field:'ref_order_type',title:'生成单据',width:50,align: 'center'},
      {field:'order_id',title:'单据id',width:50,align: 'center',hidden:true},
      {field: '_operate', title: '操作', width: 60, align: 'center', formatter: function (val, row, index) {
        var vehicleNo = row.vehicle_no;
        var ref_order_type = row.ref_order_type;
        var order_id = row.order_idStr;
        if(ref_order_type=="未生成"){
          return '<a href="#" onclick="toWashBeauty(\''+vehicleNo+'\')">生成洗车美容单</a>'
              +'&nbsp;&nbsp;<a href="#" onclick="toRepairOrder(\''+vehicleNo+'\')">生成施工单</a>';
        }else{
          return '<a href="#" onclick="toWashBeautyOrder(\''+order_id+'\')">查看单据</a>';
        }
      }}
    ]
    ]
  });

  $("#lastWeek").click(function () {
    $("#startDate").datebox("setValue", dateUtil.getNDayCloseToday(-7));
    $("#endDate").datebox("setValue", dateUtil.getToday());
  });
  $("#lastMonth").click(function () {
    $("#startDate").datebox("setValue", dateUtil.getNDayCloseToday(-30));
    $("#endDate").datebox("setValue", dateUtil.getToday());
  });

  $("#editConstruction_project").combobox({
    url:'cameraRecord.do?method=setCategoryPage',
    method:'post',
    valueField:'name',
    textField:'name',
    mode:'remote',
    multiple:true

  });

  $("#vehicleNos").combobox({
    url:'cameraRecord.do?method=getQueryVehicleNo',
    method:'post',
    valueField:'vehicle_nos',
    textField:'vehicle_nos',
    mode:'remote',
    queryParam:'vehicle_nos',
    multiple:false

  });

  $("#searchBtn").click(function () {
    var $cameraRecordListContentDom = $('#cameraRecordListContent');
    var queryParams = $cameraRecordListContentDom.datagrid('options').queryParams;
    var construction_projects = $("#vehicleNos").combobox("getValue");

    queryParams.vehicle_nos = construction_projects;
    queryParams.startDateStr = $("#startDate").datebox("getValue");
    queryParams.endDateStr = $("#endDate").datebox("getValue");
    $cameraRecordListContentDom.datagrid('options').pageNumber = 1;
    var p = $cameraRecordListContentDom.datagrid('getPager');
    if(p){
      p.pagination({
        pageNumber:1
      });
    }
    //重新加载datagrid的数据
    $("#cameraRecordListContent").datagrid('reload');
  });


});



function toWashBeauty(vehicleNo){
   window.open("washBeauty.do?method=getCustomerInfoByLicenceNo&type=customer&licenceNo="+vehicleNo,
   "newwindow","" );
}

function toRepairOrder(vehicleNo){
  window.open("txn.do?method=getRepairOrderByVehicleNumber&task=maintain&vehicleNumber="+vehicleNo,
      "newwindow","" );
}

function toWashBeautyOrder(washBeautyOrderId){
  window.open("washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId="+washBeautyOrderId,
      "newwindow","" );
}


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
      {field:'interval_time_warn',title:'同一辆车到访时间间隔（分钟）',width:50,align: 'center',align: 'center'},
      {field:'white_vehicle_nos',title:'白名单',width:60,align: 'center',align: 'center'},
      {field:'member_card',title:'自动扣会员卡',width:40,align: 'center',align: 'center'},
      {field:'construction_project',title:'施工项目',width:40,align: 'center',align: 'center'}
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
      var editConstruction_project =  data["construction_project"];

      $("#editId").val(editId);
      $("#editCamera_id").val(editCamera_id);
      $("#editInterval_time_warn").textbox("setValue",editInterval_time_warn);
      $("#editWhite_vehicle_nos").textbox("setValue",editWhite_vehicle_nos);
      $("#editMember_card").combobox("setText",editMember_card);
      $("#editOrder_type").combobox("setText",editOrder_type);
      $("#editConstruction_project").combobox("setText",editConstruction_project);
    }
  });

});

function setCamera(){
  window.open("cameraRecord.do?method=cameraConfigList",
      "newwindow","" );
}


//function updateCameraConfig(){
//  var id = $("#editId").val();
//  var camera_id = $("#editCamera_id").val();
//  var order_type = $("#editOrder_type").combobox('getValue');
//  var member_card = $("#editMember_card").combobox('getValue');
//  var interval_time_warn = $("#editInterval_time_warn").val();
//  var white_vehicle_nos = $("#editWhite_vehicle_nos").val();
//  var construction_projects_value = $("#editConstruction_project").combobox("getValues");
//  var construction_projects_text = $("#editConstruction_project").combobox("getTexts");
//  var construction_projects_valueArr = "";
//  if(construction_projects_value && construction_projects_value.length>0){
//    var i=0;
//    for(i;i<construction_projects_value.length;i++){
//      construction_projects_valueArr += construction_projects_value[i];
//      construction_projects_valueArr += ",";
//    }
//  }
//  var construction_projects_textArr = "";
//  if(construction_projects_text && construction_projects_text.length>0){
//    var i=0;
//    for(i;i<construction_projects_text.length;i++){
//      construction_projects_textArr += construction_projects_text[i];
//      construction_projects_textArr += ",";
//    }
//  }
//
//  $.messager.confirm('Confirm','确定提交?',function(r){
//    if (r){
//      $.ajax({
//        url:'cameraRecord.do?method=updateCameraConfigs',
//        type:'POST',
//        cache:false,
//        data:{id:id,camera_id:camera_id,order_type:order_type,member_card:member_card,interval_time_warn:interval_time_warn,construction_project:construction_projects_valueArr,
//          white_vehicle_nos:white_vehicle_nos},
//        dataType:'json',
//        success:function(data){
//          $("#cameraConfigListContent").datagrid("reload");
//          $.messager.alert('success','保存成功！');
//        },
//        error:function(data){
//          $("#cameraConfigListContent").datagrid("reload");
//          $.messager.alert('error','保存成功！');}
//      })
//    }});
//}

//        formatter:function(value,row,index){
//          var timeFormate = new Date(value);
//          return timeFormate.toLocaleString();
//        }