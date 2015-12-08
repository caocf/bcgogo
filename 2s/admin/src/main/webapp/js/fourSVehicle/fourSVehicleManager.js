;
$(function(){
    $("#shopVehicleList").datagrid({
        rownumbers:true,
        singleSelect:true,
        fitColumns:true,//适应列宽
        striped:true,//条纹
        nowrap:false,//截取当数据长度超出列宽时将会自动截取
        loadMsg:"数据加载中",//载入时信息
        url:'fourSVehicle.do?method=shopVehicleList',
        method:'get',
        pagination:true,
        pageNumber:1,
        pageSize:5,
        pageList:[5,10,15,20,30],
        toolbar: '#tb',
        columns:[[
            {field:'ck',checkbox:true},
            {field:'shopId',title:'店铺Id',hidden:true},
            {field:'shopName',title:'店铺名',width:80},
            {field:'vehicleNos',title:'内部车辆车牌号',width:300},
            {field:'vehicleCount',title:'数量',width:50}
            ]
        ],
        onClickRow:function(index,data){
            $("#shopSelect").combobox("setText",data["shopName"]);
            $("#editShopId").val(data["shopId"]);
            $("#editVehicleNos").textbox("setValue",data["vehicleNos"]);
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
            $("#editShopId").val(record.id);
        }
    });

    $("#saveBtn").click(function(){
        var $thisDom = $(this);
        if($thisDom.attr("lock") == "true"){
            return false;
        }else{
            $thisDom.attr("lock","true");
        }
        var shopId = $("#editShopId").val();
        var vehicleNos = $("#editVehicleNos").val();
        var validate = true;
        var validateMsg = "";
        if(G.Lang.isEmpty(shopId)){
            validate = false;
            validateMsg+="请选择店铺";
        }
        if(!validate){
            $.messager.alert('Warning',validateMsg);
            $thisDom.removeAttr("lock");
            return false;
        }
        $.messager.confirm('Confirm','确定提交?',function(r){
            if (r){
                $.ajax({
                    url:'fourSVehicle.do?method=saveOrUpdateShopVehicles',
                    type:'POST',
                    cache:false,
                    data:{shopId:shopId,vehicleNos:vehicleNos},
                    dataType:'json',
                    success:function(data){
                        $("#shopVehicleList").datagrid("reload");
                        $.messager.alert('success','保存成功！');
                        $thisDom.removeAttr("lock");
                    },
                    error:function(data){
                        $("#shopVehicleList").datagrid("reload");
                        $.messager.alert('error','保存失败！');
                        $thisDom.removeAttr("lock");
                    }
                })
            }else{
                $thisDom.removeAttr("lock");
            }});


    });
});