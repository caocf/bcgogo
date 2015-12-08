<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>摄像头记录</title>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/default/easyui.css" />
    <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/icon.css" />
   <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>--%>
    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.easyui.min.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/bcgogo.js"></script>
    <script type="text/javascript" src="js/camera/camera.js"></script>
    <script type="text/javascript" src="js/mask.js"></script>
     <%--<script>--%>
         <%--$(function () {--%>
             <%--alert("111111111111111");--%>
             <%--$("#vehicleNos").combobox({--%>
                 <%--url:'cameraRecord.do?method=getQueryVehicleNo',--%>
                 <%--method:'post',--%>
                 <%--valueField:'vehicle_nos',--%>
                 <%--textField:'vehicle_nos',--%>
                 <%--mode:'remote',--%>
                 <%--queryParam:'vehicle_nos',--%>
                 <%--multiple:false--%>
             <%--});--%>
             <%--alert("2222222");--%>
             <%--});--%>
     <%--</script>--%>


</head>
<body>
<div class="main">
    <!--头部-->
    <%@include file="/WEB-INF/views/header.jsp" %>
    <!--头部结束-->
    <div class="body">
        <!--左侧列表-->
        <%@include file="/WEB-INF/views/left.jsp" %>
        <!--左侧列表结束-->
        <!--右侧内容-->
        <div class="bodyRight">
            <%--<div id="tb" style="padding:2px 5px;">--%>
                <%--车牌号：<input autocomplete="off" id="vehicleNos" style="width: 200px">--%>
                <%--<a id="lastWeek" class="easyui-linkbutton">最近一周</a>--%>
                <%--<a id="lastMonth" class="easyui-linkbutton">最近30天</a>--%>
                <%--开始时间:<input autocomplete="off"  id="startDate" class="easyui-datebox" style="width:110px">--%>
                <%--结束时间:<input autocomplete="off" id="endDate" class="easyui-datebox" style="width:110px">--%>
                <%--<a href="#" id="searchBtn" class="easyui-linkbutton" iconCls="icon-search">查询</a>--%>
            <%--</div>--%>
            <!--内容-->
            <div class="rightMain clear">
                <div class="fileInfo">
                    <table class="easyui-datagrid"  id="cameraRecordList"  title="摄像头记录"
                           data-options="rownumbers:true,
                                    singleSelect:true,
                                    fitColumns:true,
                                    striped:true,
                                     nowrap:false,
                                     url:'camera.do?method=cameraRecordList&&id='+${id},
                                        method:'get',
                                         pagination:true,
                                         pageNumber:1,
                                         pageSize:5,
                                         pageList:[5,10,15,20,30],
                                        toolbar: '#tb'">
                        <thead>
                        <tr>
                            <th data-options="field:'name',width:50">所在店铺</th>
                            <th data-options="field:'vehicle_no',width:50">车牌号</th>
                            <th data-options="field:'arrive_date',width:50">到店时间</th>
                            <th data-options="field:'ref_order_type',width:50">关联记录</th>
                            <%--<th data-options="field:'arrive_date',width:50">到店时间</th>--%>
                        </tr>
                        </thead>

                        <%--{field:'ck',checkbox:false,hidden:true},--%>
                        <%--{field:'id',title:'id',hidden:true},--%>
                        <%--{field:'camera_id',title:'camera_id',hidden:true},--%>
                        <%--{field:'name',title:'所在店铺',width:50},--%>
                        <%--{field:'vehicle_no',title:'车牌号',width:50},--%>
                        <%--{field:'arrive_date',title:'到店时间',width:50},--%>
                        <%--{field:'ref_order_type',title:'关联记录',width:50},--%>
                        <%--{field:'order_id',title:'单据id',width:50}--%>


                    </table>

            </div>
            <!--内容结束-->
            <!--圆角-->
            <div class="bottom_crile clear">
                <div class="crile"></div>
                <div class="bottom_x"></div>
                <div style="clear:both;"></div>
            </div>
            <!--圆角结束-->
        </div>
        <!--右侧内容结束-->
    </div>
</div>
<ul class="suggestionMain" style="list-style: none;color:#000000;">
</ul>
<div id="mask" style="display:block;position: absolute;">
</div>

</body>
</html>