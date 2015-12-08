<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 14-12-4
  Time: 上午10:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>公众号管理</title>
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/components/ui/bcgogo-wait-mask.js"></script>
    <script type="text/javascript" src="js/bcgogo.js"></script>
    <script type="text/javascript" src="js/mask.js"></script>
    <script type="text/javascript" src="js/wx/wxAccountManager.js"></script>
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
                <%@include file="/weChat/wxNav.jsp"%>
                <div class="fileInfo">
                    <div  id="systemConfig_form"  class="systemConfig_form">
                        <div style="display: none;">


                            <div style="float:left;">公共号<input id="publicNo" type="text" tabindex="6" autocomplete="off" />
                            </div>
                            <div style="float:left;">
                                <input type="button" id="searchBtn" onfocus="this.blur();" value="查询" />
                            </div>
                        </div>
                        <div class="addWXAccount_div">
                            <input type="button" value="新增" onfocus="this.blur();" id="addWXAccount" style="float:right;">
                        </div>
                        <div class="clear"></div>
                        <br/>
                        <div>
                            <table cellpadding="0" cellspacing="0" class="config_table" id="table_account" width="830px">
                                <col width="40">
                                <col width="150">
                                <col width="150">
                                <col width="300">
                                <col width="150">
                                <col width="110">
                                <tr class="dm_table_title">
                                    <td style="border-left:none;">编号</td>
                                    <td>公共号</td>
                                    <td>PUBLIC_NO</td>
                                    <td>关联店铺</td>
                                    <td>备注</td>
                                    <td>操作</td>
                                </tr>
                            </table>
                        </div>
                        <div>
                            <div class="i_pageBtn" style="float:right;margin-top: 10px;">
                                <bcgogo:ajaxPaging
                                        url="weChat.do?method=getWXAccount"
                                        postFn="drawWXAccount"
                                        display="none"
                                        dynamical="_drawWXAccount"/>
                            </div>
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
</body>
<%@ include file="addWXAccount.jsp" %>
</html>