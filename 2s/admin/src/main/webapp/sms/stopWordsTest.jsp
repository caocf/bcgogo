<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理系统——短信发送失败管理</title>
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
    <script type="text/javascript" src="js/mask.js"></script>
    <script type="text/javascript">
        function confirmThis(){
            var content = $("#content").val();
            if(content==null||content==""){
                alert("请输入短信内容！");
                return false;
            }
            document.getElementById('content_form').submit();
        }
        function findBalance(){
            window.location.href="sms.do?method=findBalance";
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
                <div class="rightTitle">
                    <div class="rightLeft"></div>
                    <div class="rightBody">
                        <div class="titleHover" id="failedSms"><a href="sms.do?method=initFailedSmsPage">短信发送失败管理</a></div>
                        <div class="titleHover" id="sensitiveWordsUpload"><a href="sms.do?method=initUploadSensitiveWordsPage">敏感词上传</a></div>
                        <div class="titleHover" id="sensitiveWordsManager"><a href="sms.do?method=initSensitiveWordPage">敏感词管理</a></div>
                        <div class="titleHover" id="toTestStopWords"><a href="sms.do?method=toTestStopWords">联逾短信</a></div>
                    </div>
                </div>
                <div class="fileInfo">
                    <form:form id="content_form" action="sms.do?method=testStopWords"
                               method="post"  >
                    <%--<div  id="systemConfig_form"  class="systemConfig_form">--%>
                        <div>
                            <table cellpadding="0" id="configTable" cellspacing="0">
                                <col width="100">
                                <col  width="100"/>

                                <tr>
                                    <td class="label" id="type">请输入短信内容：<img src="images/star.jpg"></td>
                                    <td><textarea id="content" name="content" style="height: 86px; width: 284px;">${content}</textarea>  </td>
                                </tr>
                                <tr>
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    <input type="button" value="检测" onclick="confirmThis()" class="rightSearch" id="confirmBtn" style="margin-bottom:20px"/>
                                    <input type="button" value="余额" onclick="findBalance()" class="rightSearch" id="confirmBtn" style="margin-bottom:20px;margin-left: 5px;"/>
                                        ${message}
                                </tr>

                            </table>
                        </div>
                    <%--</div>--%>
                    </form:form>
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


</body>
</html>