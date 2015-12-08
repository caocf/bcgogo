<%--
  Created by IntelliJ IDEA.
  User: LiTao
  Date: 15-11-3
  Time: 下午1:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理系统——任务列表</title>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>
    <link rel="stylesheet" type="text/css" href="styles/shopIndividuation.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/bcgogo.js"></script>
    <%--<script type="text/javascript" src="js/wxArticle.js"></script>--%>
    <script type="text/javascript" src="js/mask.js"></script>
    <script type="text/javascript">
        /**
         * 待审核页面初始化
         *
         * @param jsonStr
         */
        function initTable_mirrorTask(jsonStr) {
            jQuery("#table_MirrorTask tr:not(:first)").remove();
            if (jsonStr.length > 0) {
                for (var i = 0; i < jsonStr.length-1; i++) {
                    var createdTimeString=jsonStr[i].createdTimeString==null?" " : jsonStr[i].createdTimeString;
                    var imei = jsonStr[i].imei == null ? " " : jsonStr[i].imei;
                    var tid= jsonStr[i].tid== null ? " " : jsonStr[i].tid;
                    var param = jsonStr[i].param== null ? " " : jsonStr[i].param;
                    var status= jsonStr[i].status== null ? " " : jsonStr[i].status;
                    var filePath=jsonStr[i].filePath== null ? " " : jsonStr[i].filePath;
                    var tr = '<tr>';
//                    tr += '<td style="border-left:none;">' + (i + 1) + '&nbsp;</td>';
                    tr += '<td>' + createdTimeString + '</td>';
                    tr += '<td>' + imei + '</td>';
                    tr += '<td>' + tid + '</td>';
                    tr += '<td>' + param + '</td>';
                    tr += '<td>' + status + '</td>';
                    tr += '<td>' + filePath + '</td>';
//                    tr += '<td><a class="config_modify" href="#" onclick="findImage(\'' +picUrl+'\')">查看图片</a>' +
//                        '&nbsp&nbsp<a class="config_modify" href="#"  onclick="toFindAdult(\'' +idStr+'\')">查看</a>' +
//                        '&nbsp&nbsp<a class="config_modify" href="#"  onclick="toModifyAdult(\'' +idStr+'\')">修改</a></td>';
//      tr += '<td><a class="config_modify" href="#"  onclick="toUploadWeChat(\'' +titleT+ '\',\'' +idStr+'\',\''+ description + '\')">上传图片</a>&nbsp&nbsp<a class="config_modify" href="#"  onclick="modifyWeChat(\'' +titleT+ '\',\'' +idStr+'\',\''+ description + '\')">修改</a>&nbsp&nbsp<a class="config_modify" href="#"  onclick="deleteWeChat(\'' +titleT+ '\',\'' +idStr+'\',\''+ description + '\')">删除</a></td>';
                    tr += '</tr >';
                    jQuery("#table_MirrorTask").append(jQuery(tr));
                }
            }

        }
        /**
         * 查询待审核列表
         */
        function searchMirrorTask(){
            var imei = jQuery("#imei").val();
            jQuery.ajax({
                type:"POST",
                url:"mirrorTask.do?method=mirrorTaskList",
                data:{imei:imei,startPageNo:1},
                cache:false,
                dataType:"json",
                success:function(jsonStr){
                    initTable_mirrorTask(jsonStr);
                    initfenye(jsonStr, "dynamical1", "mirrorTask.do?method=mirrorTaskList", '', "initTable_mirrorTask", '', '',
                        {startPageNo:1,imei:imei}, '');
                }
            });
        }
    </script>
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
            <!--
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
            -->
            <!--搜索结束-->
            <!--内容-->
            <div class="rightMain clear">
                <%@include file="/pages/mirrorTaskNav.jsp"%>
                <div class="fileInfo">
                    <div  id="systemConfig_form"  class="systemConfig_form">
                        <div>
                            <label style="float:left;margin-right: 10px">查询条件: </label>

                            <div style="float:left;">IMEI<input id="imei" type="text" tabindex="6" autocomplete="off"  />
                            </div>
                            <div style="float:left;">
                                <input type="button" id="configSearchBtn" onfocus="this.blur();" value="查询" onclick="searchMirrorTask()"/>
                            </div>
                        </div>
                        <div class="clear"></div>
                        <br/>
                        <div>
                            <table cellpadding="0" cellspacing="0" class="config_table" id="table_MirrorTask" width="830px">
                                <col width="130">
                                <col width="110">
                                <col width="80">
                                <col width="100">
                                <col width="60">
                                <col width="360">
                                <tr class="dm_table_title">
                                    <td style="border-left:none;">提交时间</td>
                                    <td>IMEI</td>
                                    <td>任务类型</td>
                                    <td>参数</td>
                                    <td>状态</td>
                                    <td>文件路径</td>
                                </tr>
                            </table>
                        </div>
                        <div>
                            <div class="simplePageAJAX" style="font-size:12px;">
                                <jsp:include page="/common/pageAJAX.jsp">
                                    <jsp:param name="url" value="mirrorTask.do?method=mirrorTaskList"></jsp:param>
                                    <jsp:param name="data" value="{startPageNo:1}"></jsp:param>
                                    <jsp:param name="jsHandleJson" value="initTable_mirrorTask"></jsp:param>
                                    <jsp:param name="dynamical" value="dynamical1"></jsp:param>
                                </jsp:include>
                            </div>

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