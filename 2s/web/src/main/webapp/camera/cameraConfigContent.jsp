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
    <script type="text/javascript" src="js/camera/cameraConfig.js"></script>
</head>
<body>
    <table id="cameraConfigListContent" title="车辆识别仪配置"></table>

    <div class="easyui-panel" style="float: left" id="camera_config">
        <form id="ff" method="post" >
            <table>
                <tr>
                    <td> 同一辆车到访时间间隔（分钟）:</td>
                    <td>
                        <input class="easyui-textbox" data-options="iconCls:'icon-edit',plain:true"
                               id="editInterval_time_warn" name="interval_time_warn"  style="width:200px">
                        <input type="hidden" name="id" id="editId"  autocomplete="off"/>
                        <input type="hidden" name="camera_id" id="editCamera_id"   autocomplete="off"/>
                        <input type="hidden" name="construction_project_ex" id="editConstruction_project_ex"   autocomplete="off"/>
                    </td>
                </tr>
                <tr>
                    <td> 是否自动生成单据:</td>
                    <td>
                        <select id="editOrder_type" class="easyui-combobox" name="order_type" style="width:200px;">
                            <option value="NO">否</option>
                            <option value="YES">是</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td> 是否自动扣会员卡:</td>
                    <td>
                        <select id="editMember_card" class="easyui-combobox" name="member_card"  style="width:200px;">
                            <option value="NO">否</option>
                            <option value="YES">是</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td> 施工项目:</td>
                    <td>
                     <input id="editConstruction_project"  name="construction_project"  style="width: 200px;">

                    </td>
                </tr>
                <tr>
                    <td>车牌白名单:</td>
                    <td>
                        <input class="easyui-textbox"
                               id="editWhite_vehicle_nos" name="white_vehicle_nos"  style="width:200px" autocomplete="off" data-options="iconCls:'icon-edit',plain:true">
                    </td>
                </tr>

            </table>
            <div class="easyui-panel" style="float: left">
                <br>
                说明：<br>
                1,修改识别仪配置时，请选中识别仪，再进行设置，设置完成点“保存”，结束操作<br>
                2,当自动生成单据选“是”时，识别仪扫描车牌号后会自动做单据，否则不做单据<br>
                3,当自动扣会员卡选“是”时，系统会自动执行扣费原则：先根据会员卡服务计次扣费，无计次服务则采用金额消费<br>
                4,车牌白名单，填入车牌号并以逗号“，”隔开，这样识别仪将不会扫描这些车牌号<br>
                5,施工项目可多选，自动做单据时则根据所选施工项目进行做单，如需对施工项目进行“添加，修改”等，可以
                   通过 “首页 > 车辆施工 >项目设置”界面进行操作<br>
                <br>
            </div>
            <div style="text-align:center;padding:5px">
                <a  class="easyui-linkbutton"  id="btn_save" onclick="updateCameraConfig()">确定</a>
            </div>
        </form>
    </div>

</body>
</html>
