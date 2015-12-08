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
    <title>后台管理系统——任务管理</title>
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
    <!--
    <script type="text/javascript">
        function confirmThis(){
            var titleT = $("#title").val();
            var description = $("#description").val();
            if(titleT==null||titleT==""){
                alert("请输入标题！");
                return false;
            }
            if(description==null||description==""){
                alert("请输入正文！");
                return false;
            }
            document.getElementById('template_form').submit();
        }

        function chancel(){
            window.location.href="weChat.do?method=initWeChatPage";
        }
    </script>
    -->
    <script type="text/javascript">
        function confirmThis(){
            var tid=$("#tid").val();
            var param=$("#param").val();
            var imei=$("#imei").val();
            if(tid=="1"){
                if(param.length!=4){
                    alert("请输入4位参数！");
                    return false;
                }
            }
            if(tid=="2"){
                if(param==null||param==""){
                    alert("请在参数栏输入log文件名！");
                    return false;
                }
            }
            if(imei.length<15||imei.length>17){
              alert("请输入15位IMEI号！");
              return false;
            }
            document.getElementById('template_form').submit();
        }

        function chancel(){
            window.location.href="#";
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
                <div  id="div_show">
                    <div class="i_arrow"></div>
                    <div class="i_upLeft"></div>
                    <div class="i_upCenter">
                        <div class="" id="div_drag">新增后视镜任务</div>

                    </div>
                    <form:form id="template_form" action="mirrorTask.do?method=addtask" method="post">

                    <div class="i_upRight"></div>
                    <div class="i_upBody">
                        <table cellpadding="0" id="configTable" cellspacing="0" class="configTable">
                            <col width="100">
                            <col  width="100"/>
                            <tr>
                                <td class="label">类型&nbsp;</td>
                                <td>
                                    <select id="tid" name="tid" style="height:20px;width: 220px">
                                        <option value="1">标定OBD</option>
                                        <option value="2">log上传</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td>&nbsp;</td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr>
                                <td class="label">参数<img src="images/star.jpg"></td>
                                <td><input type="text" id="param" name="param" style="height:20px;width: 220px"/></td>
                            </tr>
                            <tr>
                                <td>&nbsp;</td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr>
                                <td class="label" id="type">IMEI<img src="images/star.jpg"></td>
                                <td><input type="text" id="imei" name="imei" style="height:20px;width: 222px"/></td>
                            </tr>
                            <tr>
                                <td>&nbsp;</td>
                                <td>&nbsp;</td>
                            </tr>

                        </table>
                        <div class="height"></div>
                        <div class="more_his">
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <input type="button" value="提交" onclick="confirmThis()" class="rightSearch" id="confirmBtn"/>
                            <input type="button" value="取消" onclick="chancel()" class="rightSearch" id="cancleBtn"/>
                        </div>
                    </div>
                </div>
                </form:form>
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