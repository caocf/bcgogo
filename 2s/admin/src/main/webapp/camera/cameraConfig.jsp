<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>摄像头配置</title>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/default/easyui.css" />
    <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/icon.css" />
    <%--<%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>--%>
    <%--<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>--%>
    <%--<script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>--%>

    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.easyui.min.js"></script>
    <%--<script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/easyloader.js"></script>--%>

    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/bcgogo.js"></script>
    <script type="text/javascript" src="js/camera/cameraConfig_admin.js"></script>
    <script type="text/javascript" src="js/camera/camera.js"></script>
    <script type="text/javascript" src="js/mask.js"></script>

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

            <div class="rightMain clear">
                <div class="fileInfo">
                    <div id="add_dialog"  title="摄像头配置" class="easyui-panel">
                        <form id="ff" method="post" >
                            <table>
                                <tr>
                                    <td> 同一辆车到访时间间隔（分钟）:</td>
                                    <td>
                                        <input class="easyui-textbox" data-options="iconCls:'icon-edit',plain:true"
                                            id="interval_time_warn" name="interval_time_warn"  style="width:200px" value="${cameraConfigDTO.interval_time_warn}">
                                        <input type="hidden" name="camera_id" id="camera_id" value="${cameraConfigDTO.camera_id}"  autocomplete="off"/>
                                        <input type="hidden" name="id" id="id" value="${cameraConfigDTO.id}"  autocomplete="off"/>
                                        <%--<input type="hidden" name="cameraIds" id="cameraIds"/>--%>
                                    </td>
                                </tr>
                                <tr>
                                    <td> 是否自动生成单据:</td>
                                    <td>
                                        <select id="order_type" class="easyui-combobox" name="order_type" value="${cameraConfigDTO.order_type}" style="width:200px;">
                                            <option value="NO">否</option>
                                            <option value="YES">是</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td> 是否自动扣会员卡:</td>
                                    <td>
                                        <select id="member_card" class="easyui-combobox" name="member_card" value="${cameraConfigDTO.member_card}"  style="width:200px;">
                                            <option value="NO">否</option>
                                            <option value="YES">是</option>
                                        </select>
                                    </td>
                                </tr>
                                <%--<tr>--%>
                                    <%--<td> 施工项目:</td>--%>
                                    <%--<td>--%>
                                        <%--<input id="editConstruction_project"  name="construction_project"  style="width: 200px;">--%>

                                    <%--</td>--%>
                                <%--</tr>--%>
                                <tr>
                                    <td>车牌白名单:</td>
                                    <td>
                                        <input class="easyui-textbox" data-options="iconCls:'icon-edit',plain:true"
                                              id="white_vehicle_nos" name="white_vehicle_nos"  style="width:200px" value="${cameraConfigDTO.white_vehicle_nos}">
                                    </td>
                                </tr>
                                <tr>
                                    <td>打印机序列号:</td>
                                    <td>
                                        <input class="easyui-textbox" data-options="iconCls:'icon-edit',plain:true"
                                              id="printer_serial_no" name="printer_serial_no"  style="width:200px" value="${cameraConfigDTO.printer_serial_no}">
                                    </td>
                                    <td>(建议填写由字母和数字组成的16位字符串)</td>
                                </tr>
                            </table>
                        </form>
                        <div style="text-align:center;padding:5px">
                            <a  class="easyui-linkbutton"  id="btn_save" onclick="updateCameraConfig()">确定</a>
                            <a id="btn_back" class="easyui-linkbutton" onclick="backList()">返回</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="bottom_crile clear">
                <div class="crile"></div>
                <div class="bottom_x"></div>
                <div style="clear:both;"></div>
            </div>
        </div>
    </div>
</div>
<ul class="suggestionMain" style="list-style: none;color:#000000;">
</ul>
<div id="mask" style="display:block;position: absolute;">
</div>
</body>
</html>