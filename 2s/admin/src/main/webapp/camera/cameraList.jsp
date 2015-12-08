<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>车牌号识别仪管理</title>
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

            <!--内容-->
            <div class="rightMain clear">
                <div class="fileInfo">
                    <div id="tb" style="height:auto">
                        <a  class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" id="addBtn" onclick="add()">新增</a>
                        <a  class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" id="delBtn" onclick="del()">解绑</a>
                        <a  class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" id="findCameraRecordBtn" onclick="findCameraRecord()">查看摄像头记录</a>
                        <a  class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" id="findCameraConfigBtn" onclick="findCameraConfig()">摄像头配置</a>
                        <span>店铺名:</span><select id="shopSelects" style="width: 150px;" autocomplete="off" data-options="iconCls:'icon-edit',plain:true"></select>
                        <a  class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="btn_find" onclick="findCamera()">搜索</a>
                    </div>

                    <table id="cameraList"  title="车牌号识别仪管理" style="width: 100%;height: auto">
                        <%--<tr>--%>
                             <%--<input class="easyui-textbox" id="shop_name" name="shop_name" /> <a  class="easyui-linkbutton"  id="find" onclick="add()">查询</a>--%>
                        <%--</tr>--%>
                    </table>



                        <div id="add_dialog"  title="新增/修改摄像头参数" class="easyui-panel">
                        <form id="ff" method="post" >
                            <table>
                                <%--<tr>--%>
                                    <%--<td id="last_heart">--%>
                                        <%--心跳时间:<input class="easyui-textbox" id="editLast_heart_date" name="last_heart_date"  autocomplete="off" readonly="true"/>--%>
                                    <%--</td>--%>
                                    <%--<td id="statu">--%>
                                        <%--绑定状态:--%>
                                        <%--<input class="easyui-textbox" id="editStatus" name="status"  autocomplete="off" readonly="true"/>--%>
                                        <%--&lt;%&ndash;<select id="editStatus" class="easyui-combobox" name="status"  style="width:200px;">&ndash;%&gt;--%>
                                            <%--&lt;%&ndash;<option value="binding">已绑定</option>&ndash;%&gt;--%>
                                            <%--&lt;%&ndash;<option value="nobinding">未绑定</option>&ndash;%&gt;--%>
                                        <%--&lt;%&ndash;</select>&ndash;%&gt;--%>
                                    <%--</td>--%>
                                <%--</tr>--%>
                                <tr>
                                    <td>
                                        摄像头序列号:<input class="easyui-textbox" id="editSerial_no" name="serial_no" data-options="iconCls:'icon-edit',plain:true" autocomplete="off" />
                                    </td>
                                    <td>
                                        &nbsp; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 局域网ip:<input class="easyui-textbox" id="editLan_ip" name="lan_ip"  autocomplete="off" data-options="iconCls:'icon-edit',plain:true"/>
                                    </td>
                                    <td>
                                        &nbsp; 局域网端口:<input class="easyui-textbox" id="editLan_port" name="lan_port" autocomplete="off" data-options="iconCls:'icon-edit',plain:true"/>
                                    </td>
                                    <%--<td>--%>

                                    <%--测试日期: <input id="birthday" class="easyui-datetimebox" name="birthday"--%>
                                                 <%--data-options="formatter:myformatter,parser:myparser,showSeconds:true"/>--%>
                                    <%--<input class="easyui-datebox" name="begin_date" id="begin_date"/>--%>
                                        <%--data-options="formatter:myformatter,parser:myparser"--%>
                                    <%--</td>--%>
                                </tr>
                                <tr>
                                    <td style="text-align:center">
                                        &nbsp; &nbsp;  &nbsp; &nbsp;&nbsp; &nbsp;  用户名:<input class="easyui-textbox" id="editUsername" name="username"  autocomplete="off" data-options="iconCls:'icon-edit',plain:true" />
                                    </td>
                                    <td style="text-align:center">
                                        &nbsp; &nbsp;  &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;&nbsp; 密码:<input class="easyui-textbox" id="editPassword" name="password"  autocomplete="off" data-options="iconCls:'icon-edit',plain:true"/>
                                    </td>
                                    <td style="text-align:center">
                                        &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 备注:<input class="easyui-textbox" id="editRemark" name="remark"  autocomplete="off" data-options="iconCls:'icon-edit',plain:true"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="text-align:center">
                                        &nbsp;&nbsp;&nbsp; &nbsp;    外网地址:<input class="easyui-textbox" id="editExternal_address" name="external_address"  autocomplete="off" data-options="iconCls:'icon-edit',plain:true"/>
                                    </td>
                                    <td style="text-align:center">
                                        域名网站用户名:<input class="easyui-textbox" id="editDomain_username" name="domain_username"  autocomplete="off" data-options="iconCls:'icon-edit',plain:true" />
                                    </td>
                                    <td style="text-align:center">
                                        域名网站密码:<input class="easyui-textbox" id="editDomain_password" name="domain_password"  autocomplete="off" data-options="iconCls:'icon-edit',plain:true"/>
                                    </td>
                               </tr>
                                <tr>
                                    <td style="text-align:center">
                                        &nbsp; 绑定店铺名:<select id="shopSelect" style="width: 150px;" autocomplete="off" data-options="iconCls:'icon-edit',plain:true"></select>
                                        <input type="hidden" name="id" id="editId"  autocomplete="off"/>
                                        <input type="hidden" name="camera_shop_id" id="editCamera_shop_id" autocomplete="off"/>
                                        <input type="hidden" name="editLast_heart_date" id="editLast_heart_date"  autocomplete="off"/>
                                        <input type="hidden" name="editStatus" id="editStatus"  autocomplete="off"/>
                                    </td>
                                    <%--<td>--%>
                                        <%--白名单:<input class="easyui-textbox" id="editWhite_vehicle_nos" name="white_vehicle_nos"  autocomplete="off" data-options="iconCls:'icon-edit',plain:true"/>--%>
                                    <%--</td>--%>
                                    <td style="text-align:center">
                                        <%--安装日期:<input class="easyui-textbox" id="editInstall_date" name="install_date"  autocomplete="off" data-options="iconCls:'icon-edit',plain:true" />--%>
                                            &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; 安装日期:<input class="easyui-datetimebox" id="editInstall_date" name="install_date" data-options="iconCls:'icon-edit',plain:true,formatter:myformatter,parser:myparser">
                                    </td>
                                </tr>
                                <%--<tr>--%>
                                    <%--<td colspan=4  align="center">--%>
                                        <%--<div class="easyui-panel" style="float: left">--%>
                                        <%--说明：录入白名单，安装日期相关信息，请绑定到店铺<br>--%>
                                        <%--&lt;%&ndash;1，新店铺添加内部车辆的时候下拉选择店铺<br>&ndash;%&gt;--%>
                                        <%--&lt;%&ndash;2，老店铺修改内部车辆信息从上面列表中选择该店铺，并在后面框里修改车辆信息<br>&ndash;%&gt;--%>
                                        <%--&lt;%&ndash;3，填写该店铺车辆的时候，什么都不填则清空该店铺所有内部车辆<br>&ndash;%&gt;--%>
                                        <%--&lt;%&ndash;4，多个车牌号用逗号分隔<br>&ndash;%&gt;--%>
                                        <%--</div>--%>
                                    <%--</td>--%>
                                <%--</tr>--%>
                            </table>
                        </form>
                        <div style="text-align:center;padding:5px">
                            <a id="saveBtn" class="easyui-linkbutton">确定</a>
                            <%--<a  class="easyui-linkbutton" >取消</a>--%>
                        </div>
                    </div>
                    <div id ="record_camera" style="display: none">
                        <table id="cameraRecordList"  title="摄像头记录" style="width: 100%;height: auto">
                            <div id="driveLogTb" style="padding:2px 5px;">
                                <span>店铺名:</span><select id="shopSelects1" style="width: 150px;" autocomplete="off" data-options="iconCls:'icon-edit',plain:true"></select>
                                <a  class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="btn_find1" onclick="findCamera()">搜索</a>
                            </div>
                        </table>
                    </div>
                </div>
                <!--内容结束-->
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
<%--<div id="setLocation"   style="position: fixed; left: 37%; top: 37%;  z-index: 8; display: none;">--%>
<%--<jsp:include page="sms/editSms.jsp"></jsp:include>--%>
<%--</div>--%>
</body>
</html>