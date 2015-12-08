<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理系统——内部车辆管理</title>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/default/easyui.css" />
    <link rel="stylesheet" type="text/css" href="js/extension/jquery-easyui/jquery-easyui-1.4.1/themes/icon.css" />
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>

    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery-easyui/jquery-easyui-1.4.1/easyloader.js"></script>

    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/bcgogo.js"></script>
    <script type="text/javascript" src="js/fourSVehicle/fourSVehicleManager.js"></script>
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
                    <table id="shopVehicleList"  title="店铺内部车辆管理" style="width: 100%;height: auto">
                    </table>
                    <div id="tb" style="height:auto">
                        <a class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" id="add_btn">新增</a>
                        <a  class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" id="edit_btn">修改</a>
                    </div>

                    <div id="add_dialog"  title="新增/修改店铺车辆" class="easyui-panel">
                        <form id="ff" method="post" >
                            <table style="width: 100%">
                                <tr>
                                    <td>店铺名称:</td>
                                    <td>
                                        <select id="shopSelect" style="width: 200px;" autocomplete="off"></select>
                                        <input type="hidden" name="shopId" id="editShopId" autocomplete="off"/>
                                    </td>
                                    <td>车牌号:</td>
                                    <td>
                                        <input class="easyui-textbox" id="editVehicleNos" name="vehicleNos" autocomplete="off"
                                               data-options="multiline:true" style="height:100px;width: 500px;">
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan=4  align="center">
                                        <div class="easyui-panel" style="float: left">
                                        说明：<br>
                                        1，新店铺添加内部车辆的时候下拉选择店铺<br>
                                        2，老店铺修改内部车辆信息从上面列表中选择该店铺，并在后面框里修改车辆信息<br>
                                        3，填写该店铺车辆的时候，什么都不填则清空该店铺所有内部车辆<br>
                                        4，多个车牌号用逗号分隔<br>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </form>
                        <div style="text-align:center;padding:5px">
                            <a id="saveBtn" class="easyui-linkbutton">确定</a>
                            <a  class="easyui-linkbutton" >取消</a>
                        </div>
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
<%--<div id="setLocation"   style="position: fixed; left: 37%; top: 37%;  z-index: 8; display: none;">
<jsp:include page="sms/editSms.jsp"></jsp:include>
</div>--%>




</body>


