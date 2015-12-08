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
    <script type="text/javascript" src="js/camera/cameraRecordList.js"></script>
</head>
<body>


<div id="tb" style="padding:2px 5px;">
    车牌号：<input autocomplete="off" id="vehicleNos" style="width: 200px">
    <a id="lastWeek" class="easyui-linkbutton">最近一周</a>
    <a id="lastMonth" class="easyui-linkbutton">最近30天</a>
    开始时间:<input autocomplete="off"  id="startDate" class="easyui-datebox" style="width:110px">
    结束时间:<input autocomplete="off" id="endDate" class="easyui-datebox" style="width:110px">
    <a href="#" id="searchBtn" class="easyui-linkbutton" iconCls="icon-search">查询</a>
    <a href="#" id="camera_set" class="easyui-linkbutton" iconCls="icon-search" onclick="setCamera()">识别仪配置</a>
</div>
<table id="cameraRecordListContent" title="到店车辆记录">
</table>


<%--<div id ="setCamera"  style="display: none">--%>
    <%--<table id="cameraConfigListContent" title="车辆识别仪配置"></table>--%>
    <%--<div class="easyui-panel" style="float: left" id="camera_config">--%>
        <%--<form id="ff" method="post" >--%>
            <%--<table>--%>
                <%--<tr>--%>
                    <%--<td> 同一辆车到访时间间隔（分钟）:</td>--%>
                    <%--<td>--%>
                        <%--<input class="easyui-textbox" data-options="iconCls:'icon-edit',plain:true"--%>
                               <%--id="editInterval_time_warn" name="interval_time_warn"  style="width:200px">--%>
                        <%--<input type="hidden" name="id" id="editId"  autocomplete="off"/>--%>
                        <%--<input type="hidden" name="camera_id" id="editCamera_id"   autocomplete="off"/>--%>
                    <%--</td>--%>
                <%--</tr>--%>
                <%--<tr>--%>
                    <%--<td> 单据类型:</td>--%>
                    <%--<td>--%>
                        <%--<select id="editOrder_type" class="easyui-combobox" name="order_type" style="width:200px;">--%>
                            <%--<option value="NONE">无</option>--%>
                            <%--<option value="WASH">洗车</option>--%>
                        <%--</select>--%>
                    <%--</td>--%>
                <%--</tr>--%>
                <%--<tr>--%>
                    <%--<td> 自动扣会员卡:</td>--%>
                    <%--<td>--%>
                        <%--<select id="editMember_card" class="easyui-combobox" name="member_card"  style="width:200px;">--%>
                            <%--<option value="NO">否</option>--%>
                            <%--<option value="YES">是</option>--%>
                        <%--</select>--%>
                    <%--</td>--%>
                <%--</tr>--%>
                <%--<tr>--%>
                    <%--<td> 施工项目:</td>--%>
                    <%--<td>--%>
                     <%--<input id="editConstruction_project"  name="construction_project"  style="width: 200px;">--%>

                    <%--</td>--%>
                <%--</tr>--%>
                <%--<tr>--%>
                    <%--<td>白名单:</td>--%>
                    <%--<td>--%>
                        <%--<input class="easyui-textbox"--%>
                               <%--id="editWhite_vehicle_nos" name="white_vehicle_nos"  style="width:200px" autocomplete="off" data-options="iconCls:'icon-edit',plain:true">--%>
                    <%--</td>--%>
                <%--</tr>--%>
            <%--</table>--%>
            <%--<div style="text-align:center;padding:5px">--%>
                <%--<a  class="easyui-linkbutton"  id="btn_save" onclick="updateCameraConfig()">确定</a>--%>
            <%--</div>--%>
        <%--</form>--%>
    <%--</div>--%>
<%--</div>--%>

</body>
</html>
