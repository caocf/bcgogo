<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/jquery-easyui/jquery-easyui-1.4.1/themes/bootstrap/easyui.css" />
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/jquery-easyui/jquery-easyui-1.4.1/themes/icon.css" />
    <script type="text/javascript" src="js/extension/jquery/jquery-easyui/jquery-easyui-1.4.1/jquery.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/jquery-easyui/jquery-easyui-1.4.1/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/jquery-easyui/jquery-easyui-1.4.1/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/dateUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/dateUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script>
        $(function () {
            $("#startDate").datebox("setValue", dateUtil.getNDayCloseToday(-30));
            $("#endDate").datebox("setValue", dateUtil.getToday());
            $("#vehicleListTB").datagrid({
                rownumbers: true,
                singleSelect: true,
                fitColumns: true,//适应列宽
                striped: true,//条纹
                nowrap: false,//截取当数据长度超出列宽时将会自动截取
                loadMsg: "数据加载中",//载入时信息
                url: 'internalVehicle.do?method=getInternalVehicleList',
                queryParams:{
//                    vehicleNos: vehicleNo,
                    startDateStr: $("#startDate").datebox("getValue"),
                    endDateStr: $("#endDate").datebox("getValue")
                },
                method: 'get',
                pagination: true,
                pageNumber: 1,
                pageSize: 10,
                pageList: [5, 10, 20, 30],
                toolbar: '#tb',
                columns: [
                    [
                        {field: 'vehicleNo', title: '车牌号', width: 80},
                        {field: 'vehicleInfo', title: '车辆品牌/车型', width: 80},
                        {field: 'distance', title: '行驶里程(KM)', width: 80,sortable:true},
                        {field: 'oilWear', title: '总耗油量(L)', width: 80,sortable:true},
                        {field: 'avgOilWear', title: '平均油耗(L/100KM)', width: 80,sortable:true},
                        {field: 'travelTimeStr', title: '累计行驶时间', width: 80,sortable:true},
                        {field: 'driveCount', title: '行驶次数', width: 50,sortable:true},
                        {field: '_operate', title: '操作', width: 50, align: 'center', formatter: function (val, row, index) {
                            var vehicleNo = row.vehicleNo;
                            var startDate = $("#startDate").datebox("getValue");
                            startDate = G.Lang.normalize(startDate);
                            var endDate = $("#endDate").datebox("getValue");
                            endDate = G.Lang.normalize(endDate);
                            return '<a href="#" onclick="drawDriveLogListWin(\''+vehicleNo+'\',\''+startDate+'\',\''+endDate+'\')">查看详情</a>';
                        }}
                    ]
                ]
            });
            $("#vehicleNos").combobox({
                url:'internalVehicle.do?method=getQueryInternalVehicleNo',
                method:'post',
                valueField:'vehicleNo',
                textField:'vehicleNo',
                mode:'remote',
                multiple:true

            });
            $("#lastWeek").click(function () {
                $("#startDate").datebox("setValue", dateUtil.getNDayCloseToday(-7));
                $("#endDate").datebox("setValue", dateUtil.getToday());
            });
            $("#lastMonth").click(function () {
                $("#startDate").datebox("setValue", dateUtil.getNDayCloseToday(-30));
                $("#endDate").datebox("setValue", dateUtil.getToday());
            });
            $("#searchBtn").click(function () {
                var $vehicleListTBDom = $('#vehicleListTB');
                var queryParams = $vehicleListTBDom.datagrid('options').queryParams;
                var vehicleNos = $("#vehicleNos").combobox("getValues");
                var vehicleNosArr = "";
                if(vehicleNos && vehicleNos.length>0){

                    var i=0;
                    for(i;i<vehicleNos.length;i++){
                        vehicleNosArr += vehicleNos[i];
                        vehicleNosArr += ",";
                    }
                }
                queryParams.vehicleNos = vehicleNosArr;
                queryParams.startDateStr = $("#startDate").datebox("getValue");
                queryParams.endDateStr = $("#endDate").datebox("getValue");
                $vehicleListTBDom.datagrid('options').pageNumber = 1;
                var p = $vehicleListTBDom.datagrid('getPager');
                if(p){
                    p.pagination({
                        pageNumber:1
                    });
                }
                //重新加载datagrid的数据
                $("#vehicleListTB").datagrid('reload');
            });
            $("#driveLogLastWeek").click(function () {
                $("#driveLogStartDate").datebox("setValue", dateUtil.getNDayCloseToday(-7));
                $("#driveLogEndDate").datebox("setValue", dateUtil.getToday());
            });
            $("#driveLogLastMonth").click(function () {
                $("#driveLogStartDate").datebox("setValue", dateUtil.getNDayCloseToday(-30));
                $("#driveLogEndDate").datebox("setValue", dateUtil.getToday());
            });
            $("#driveLogSearchBtn").click(function(){
                var $vehicleListTBDom = $('#vehicleDriveLogListTB');
                var queryParams = $vehicleListTBDom.datagrid('options').queryParams;
                var vehicleNos = $("#driveLogVehicleNos").combobox("getValues");
                var vehicleNosArr = "";
                if(vehicleNos && vehicleNos.length>0){

                    var i=0;
                    for(i;i<vehicleNos.length;i++){
                        vehicleNosArr += vehicleNos[i];
                        vehicleNosArr += ",";
                    }
                }
                queryParams.vehicleNos = vehicleNosArr;
                queryParams.startDateStr = $("#driveLogStartDate").datebox("getValue");
                queryParams.endDateStr = $("#driveLogEndDate").datebox("getValue");
                $vehicleListTBDom.datagrid('options').pageNumber = 1;
                var p = $vehicleListTBDom.datagrid('getPager');
                if(p){
                    p.pagination({
                        pageNumber:1
                    });
                }
                //重新加载datagrid的数据
                $vehicleListTBDom.datagrid('reload');
            });
        });

        function drawDriveLogListWin(vehicleNo,startDate,endDate){
            $("#driveLogListWin").show();
            $("#driveLogListWin").window({
                title:"内部车辆行驶记录",
                width:1000,
                height:400,
                top:12,
//                modal:true,
                closable:true,
                collapsible:false,
                minimizable:false,
                maximizable:false,
                draggable:false,
                resizable:false
            });
            $("#driveLogVehicleNos").combobox({
                url:'internalVehicle.do?method=getQueryInternalVehicleNo',
                method:'post',
                valueField:'vehicleNo',
                textField:'vehicleNo',
                mode:'remote',
                multiple:false

            });
            $("#vehicleDriveLogListTB").datagrid({
                rownumbers: true,
                singleSelect: true,
                fitColumns: true,//适应列宽
                striped: true,//条纹
                nowrap: false,//截取当数据长度超出列宽时将会自动截取
                loadMsg: "数据加载中",//载入时信息
                url: 'internalVehicle.do?method=getInternalVehicleDriveList',
                method: 'get',
                pagination: true,
                pageNumber: 1,
                pageSize: 10,
                pageList: [5, 10, 20, 30],
                toolbar: '#driveLogTb',
                queryParams: {
                    vehicleNos: vehicleNo,
                    startDateStr: startDate,
                    endDateStr: endDate},
                columns: [
                    [
                        {field: 'startPlace', title: '起始位置', width: 80},
                        {field: 'endPlace', title: '终点位置', width: 80},
                        {field: 'startTimeStr', title: '起始时间', width: 100,sortable:false},
                        {field: 'endTimeStr', title: '终点时间', width: 100,sortable:false},
                        {field: 'travelTimeStr', title: '行驶时间', width: 80,sortable:false},
                        {field: 'distance', title: '行驶里程（KM）', width: 60,sortable:false},
                        {field: 'oilCost', title: '耗油量（L）', width: 50,sortable:false},
                        {field: 'oilWear', title: '平均油耗（L/100KM）', width: 50,sortable:false},
                        {field: '_operate', title: '操作', width: 50, align: 'center', formatter: function (val, row, index) {
                            console.log(row);
                            var logId = row.idStr;
                            return '<a href="#" onclick="drawDriveLogPlaceNote(\''+logId+'\')">查看轨迹</a>';
                        }}
                    ]
                ]
            });
            $("#driveLogVehicleNos").combobox("setValue",vehicleNo);
            $("#driveLogStartDate").datebox("setValue",startDate);
            $("#driveLogEndDate").datebox("setValue",endDate);
        }

        function drawDriveLogPlaceNote(driveLogId){
            $("#driveLogPlaceContainer").show();
            $("#driveLogPlaceContainer").window({
                title:"内部车辆行驶轨迹",
                width:740,
                height:580,
                top:14,
//                modal:true,
                closable:true,
                collapsible:false,
                minimizable:false,
                maximizable:false,
                draggable:false,
                resizable:false
            });
            var $iframe = $("#map_container_iframe");
            var str = "&city=" + $("#city").val() + "&coordinate=" +$("#coordinateLon").val() + "_" + $("#coordinateLat").val();
            $iframe[0].src = "api/proxy/baidu/map/vehicleDriveLog?data="+driveLogId + str;
        }
    </script>
</head>
<body>
<input type="hidden"  id="coordinateLat" value="${coordinateLat}">
<input type="hidden"  id="coordinateLon" value="${coordinateLon}">
<input type="hidden"  id="city" value="${city}">
<table id="vehicleListTB" title="内部车辆管理"></table>
<div id="tb" style="padding:2px 5px;">
    车牌号：<input autocomplete="off" id="vehicleNos" style="width: 200px">
    <a id="lastWeek" class="easyui-linkbutton">最近一周</a>
    <a id="lastMonth" class="easyui-linkbutton">最近30天</a>
    开始时间:<input autocomplete="off"  id="startDate" class="easyui-datebox" style="width:110px">
    结束时间:<input autocomplete="off" id="endDate" class="easyui-datebox" style="width:110px">
    <a href="#" id="searchBtn" class="easyui-linkbutton" iconCls="icon-search">查询</a>
</div>
<div class="easyui-panel" style="float: left">
    说明：<br>
    1,此功能需要把内部车辆的车牌号发给我们客服，由我们客服录入系统<br>
    2,点击【查看详情】查看详细行车轨迹<br>
</div>
<div id="driveLogListWin" style="display: none">
    <table id="vehicleDriveLogListTB"></table>
    <div id="driveLogTb" style="padding:2px 5px;">
        车牌号：<input autocomplete="off" id="driveLogVehicleNos" style="width: 200px">
        <a id="driveLogLastWeek" class="easyui-linkbutton">最近一周</a>
        <a id="driveLogLastMonth" class="easyui-linkbutton">最近30天</a>
        开始时间:<input autocomplete="off"  id="driveLogStartDate" class="easyui-datebox" style="width:110px">
        结束时间:<input autocomplete="off" id="driveLogEndDate" class="easyui-datebox" style="width:110px">
        <a href="#" id="driveLogSearchBtn" class="easyui-linkbutton" iconCls="icon-search">查询</a>
    </div>
</div>
<div id="driveLogPlaceContainer" style="display: none;height: 700px">
    <iframe src="" id="map_container_iframe" style="width: 100%;height: 530px;;overflow: hidden;margin:0" scrolling="no"
            frameborder="0" allowtransparency="true"></iframe>
</div>
</body>
</html>
