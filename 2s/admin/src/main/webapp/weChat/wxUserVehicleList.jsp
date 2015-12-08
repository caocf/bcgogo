<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理系统——微信管理</title>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/bcgogo.js"></script>
    <script type="text/javascript" src="js/wx/wxUser.js"></script>
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
            <!--搜索-->
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店铺名" id="txt_shopName"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店主" id="txt_shopOwner"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textAddressbody"><input type="text" value="地址" id="txt_address"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="手机/电话" id="txt_phone"/></div>
                <div class="textRight"></div>
            </div>
            <input type="button" class="rightSearch" value="搜 索"/>
            <!--搜索结束-->
            <!--内容-->
            <div class="rightMain clear">
                <%@include file="/weChat/wxNav.jsp"%>
                <div class="fileInfo">
                    <div  id="systemConfig_form"  class="systemConfig_form">
                        <div>

                            <div style="float:left;">
                                 <input type="hidden" id="openId" value="${openId}">

                            </div>
                        </div>
                        <div class="clear"></div>
                        <br/>
                        <div>
                            <table cellpadding="0" cellspacing="0" class="config_table" id="table_WXUserVehicleJob" width="830px">
                                <col width="40">
                                <col width="100">
                                <col width="100">
                                <col width="100">
                                <col width="100">
                                <col width="200">
                                <tr class="dm_table_title">
                                    <td style="border-left:none;">编号</td>
                                    <td>车牌号</td>
                                    <td>车架号</td>
                                    <td>发动机号</td>
                                    <td>归属地</td>
                                    <td>操作</td>
                                </tr>
                                <c:forEach items="${wXUserVehicleDTOs}" var="vehicle" varStatus="status" >
                                     <tr>
                                         <td>${status.index+1}</td>
                                         <td>${vehicle.vehicleNo}</td>
                                         <td>${vehicle.vin}</td>
                                         <td>${vehicle.engineNo}</td>
                                         <td>${vehicle.provinceAndCity}</td>
                                         <td><a class="config_modify" href="#" onclick="modifyVehicle('${vehicle.idStr}')">修改</a>&nbsp&nbsp<a class="config_modify" href="#"  onclick="deleteVehicle('${vehicle.idStr}')">删除</a></td>
                                     </tr>
                                </c:forEach>
                            </table>
                        </div>
                        <%--<div>--%>
                            <%--<div class="simplePageAJAX" style="font-size:12px;">--%>
                                <%--<jsp:include page="/common/pageAJAX.jsp">--%>
                                    <%--<jsp:param name="url" value="weChat.do?method=wxUserVehicleList"></jsp:param>--%>
                                    <%--<jsp:param name="data" value="{startPageNo:1}"></jsp:param>--%>
                                    <%--<jsp:param name="jsHandleJson" value="inintTable_WXUserVehicleJob"></jsp:param>--%>
                                    <%--<jsp:param name="dynamical" value="dynamical1"></jsp:param>--%>
                                <%--</jsp:include>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                        <br>
                        <br>
                       <div class="text-align:center;margin-left:auto; margin-right:auto;">
                           <input type="button" id="configSearchBtn" onfocus="this.blur();" value="添加车辆" onclick="addUserVehicle()"/>
                           <input type="button" value="返回微信用户列表" onclick="backToWXUser()"  id="cancleBtn"/>
                       </div>
                        <div>
                            <img width="100" class="J_wx_article_img_show">
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
<%--<div id="setLocation"   style="position: fixed; left: 37%; top: 37%;  z-index: 8; display: none;">--%>
    <%--<jsp:include page="sms/editSms.jsp"></jsp:include>--%>
<%--</div>--%>
</body>
</html>