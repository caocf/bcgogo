
$(function(){
  var id = $("#id").val();
    $("#cameraRecordList").datagrid({
        rownumbers:true,
        singleSelect:true,
        fitColumns:true,//适应列宽
        striped:true,//条纹
        nowrap:false,//截取当数据长度超出列宽时将会自动截取
        loadMsg:"数据加载中",//载入时信息
        url:'camera.do?method=cameraRecordList&&id='+id,
        method:'get',
        pagination:true,
        pageNumber:1,
        pageSize:5,
        pageList:[5,10,15,20,30],
        toolbar: '#tb',
        columns:[[
            {field:'ck',checkbox:false},
            {field:'id',title:'id',hidden:true},
            {field:'camera_id',title:'id',hidden:true},
            {field:'name',title:'所在店铺',width:50},
            {field:'serial_no',title:'车牌号',width:50},
            {field:'last_heart_date',title:'到店时间',width:50},
            {field:'lan_ip',title:'关联记录',width:50},
            {field:'lan_port',title:'单据id',width:50},
            {field:'camera_shop_id',title:'shopId',width:50}
            ]
        ]
    });
});









